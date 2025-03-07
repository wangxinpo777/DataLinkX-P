<template>
  <div class="main">
    <a-card class="chat-container" title="AI 机器人聊天室">
      <div class="menu-box" v-if="conversations.length > 0">
        <a-menu mode="vertical" class="menu" :selected-keys="[conversationId]">
          <template v-for="item in conversations">
            <a-menu-item
              :key="item.id"
              @click="flushMessages(item.id)"
              style="position: relative;"
              @mouseenter="hoveredItem = item.id"
              @mouseleave="hoveredItem = null">
              <a-tooltip
                :title="item.title"
                :mouseEnterDelay="0.5"
                placement="right">
                {{ item.title }}
              </a-tooltip>
              <a-icon v-if="hoveredItem === item.id" type="delete" style="position: absolute; right: 0; top: 12px;"/>
            </a-menu-item>
          </template>
        </a-menu>
        <div class="add-conversation">
          <a-checkable-tag :checked="true" class="add-conversation-text" @change="messages = [];conversationId = ''">
            新增会话
          </a-checkable-tag>
        </div>
      </div>
      <div class="chat">
        <div class="chat-box" ref="messageContainer">
          <div class="message">
            <a-avatar class="avatar" src="https://i.pravatar.cc/40?img=2"/>
            <div class="message-content">
              <div class="bubble mdTextBox">
                <p>你好，我是 AI 机器人，有什么可以帮助你的吗？</p>
              </div>
            </div>
          </div>
          <div v-for="message in messages" :key="message.id" :class="['message', message.role]">
            <a-avatar v-if="message.role === 'user'" class="avatar" src="https://i.pravatar.cc/40?img=1"/>
            <a-avatar v-else class="avatar" src="https://i.pravatar.cc/40?img=2"/>
            <div class="message-content">
              <div
                class="bubble2"
                v-if="message.role === 'assistant' && message.reasoningContent"
                v-html="renderMdTextReasoning(message)"
              >
              </div>
              <!-- 渲染Markdown内容 -->
              <div class="bubble1" v-html="renderMdText(message)"></div>
              <!-- 复制按钮 -->
              <a-button
                class="copyBtn"
                v-if="message.role === 'assistant'"
                type="link"
                size="small"
                @click="copyText(message.id)"
                icon="copy"
              />
            </div>
          </div>
        </div>
        <div class="operation">
          <a-select
            v-model="model"
            style="position: absolute; right: 10px;z-index: 1; top: 5px;"
            placeholder="选择模型"
            :dropdownStyle="{ bottom: '100%', top: 'auto' }"
          >
            <a-select-option value="deepseek-chat">DeepSeek V3</a-select-option>
            <a-select-option value="deepseek-reasoner">DeepSeek R1</a-select-option>
          </a-select>
          <a-textarea
            v-model="userInput"
            class="input-text"
            placeholder="询问任何问题"
            @pressEnter.prevent="sendMessage"
          >
          </a-textarea>
          <div class="buttons" style="position: relative;">
            <a-button
              shape="circle"
              setsize="small"
              icon="plus"/>
            <a-button
              shape="circle"
              setsize="small"
              style="position: absolute; right: 0; bottom: 0;"
              @click="sendMessage"
              :loading="loading"
              icon="arrow-up"
            ></a-button>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script>
import { fetchEventSource } from '@microsoft/fetch-event-source'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import {
  deeepseekConversationsHistory,
  deeepseekMessagesHistory
} from '@/api/deepseek/api'

export default {
  name: 'AIVisualization',
  data () {
    return {
      markdownRender: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight: (str, lang) => {
          if (lang && hljs.getLanguage(lang)) {
            try {
              return hljs.highlight(lang, str).value // 旧语法
            } catch (err) {
              console.error('代码高亮失败:', err) // 可选：打印错误
            }
          }

          // 兜底方案：自动检测语言
          try {
            return hljs.highlightAuto(str).value
          } catch (err) {
            return hljs.highlight('plaintext', str).value // 纯文本兜底
          }
        }
      }),
      userInput: '',
      hoveredItem: null,
      conversations: [],
      conversationId: '',
      messages: [],
      message: {
        id: 0,
        role: 'assistant',
        content: '你好，我是 AI 机器人，有什么可以帮助你的吗？'
      },
      loading: false,
      eventSource: null,
      model: 'deepseek-chat'
    }
  },
  computed: {
    renderMdText () {
      return (message) => {
        return this.markdownRender.render(message.content)
      }
    },
    renderMdTextReasoning () {
      return (message) => {
        return this.markdownRender.render(message.reasoningContent)
      }
    }
  },
  mounted () {
    this.initDeepSeek()
  },
  methods: {
    scrollToBottom () {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer

        const start = container.scrollTop
        const end = container.scrollHeight
        const duration = 1000 // 滚动持续时间（毫秒）
        const startTime = performance.now()

        const animateScroll = (currentTime) => {
          const elapsedTime = currentTime - startTime
          const progress = Math.min(elapsedTime / duration, 1)
          container.scrollTop = start + (end - start) * progress

          if (progress < 1) {
            requestAnimationFrame(animateScroll)
          }
        }

        requestAnimationFrame(animateScroll)
      })
    },
    flushMessages (conversationId) {
      if (conversationId !== this.conversationId) {
        this.conversationId = conversationId
        this.deeepseekMessagesHistory()
      }
    },
    initDeepSeek () {
      deeepseekConversationsHistory().then(res => {
        this.conversations = res.result
        this.conversationId = res.result.length > 0 ? res.result[0].id : ''
        deeepseekMessagesHistory(this.conversationId).then(res => {
          this.messages = res.result
          this.scrollToBottom()
        })
      })
    },
    deeepseekMessagesHistory () {
      deeepseekMessagesHistory(this.conversationId).then(res => {
        this.messages = res.result
        this.scrollToBottom()
      })
    },
    // 复制文本内容
    copyText (messageId) {
      const message = this.messages.find(msg => msg.id === messageId)
      const textArea = document.createElement('textarea')
      textArea.value = message.content
      document.body.appendChild(textArea)
      textArea.select()
      document.execCommand('copy')
      document.body.removeChild(textArea)
      this.$message.success('消息已复制到剪贴板')
    },
    sendMessage () {
      if (!this.userInput.trim() || this.loading) return

      // 关闭旧的 SSE 连接
      if (this.eventSource) {
        this.eventSource.close()
      }

      this.loading = true
      const token = localStorage.getItem('Access-Token').replace(/"/g, '')
      const url = `/api/api/deepseek/stream/chat?model=${this.model}&content=${encodeURIComponent(this.userInput)}&conversationId=${this.conversationId}`
      this.messages.push({
        id: this.messages.length + 1,
        role: 'user',
        content: this.userInput
      })
      this.userInput = '' // 清空输入框
      this.scrollToBottom()

      fetchEventSource(url, {
        method: 'GET',
        openWhenHidden: true,
        headers: {
          'ACCESS-TOKEN': token,
          'Accept': 'text/event-stream'
        },
        onopen: () => {
          deeepseekConversationsHistory().then(res => {
            this.conversations = res.result
            if (this.conversationId === '') {
              this.conversationId = res.result.length > 0 ? res.result[0].id : ''
            }
          })
          console.log('SSE 连接已建立')
        },
        onmessage: (event) => {
          const data = JSON.parse(event.data)

          // 在 messages 数组中查找相同 id 的消息
          const existingMessage = this.messages.find(msg => msg.id === data.id)

          if (existingMessage) {
            // 追加内容
            existingMessage.content += data.choices[0].delta.content ? data.choices[0].delta.content : data.choices[0].delta.reasoning_content
          } else {
            // 不存在则添加新的消息
            this.messages.push({
              id: data.id,
              role: data.choices[0].delta.role === 'user' ? 'user' : 'assistant',
              content: data.choices[0].delta.content ? data.choices[0].delta.content : data.choices[0].delta.reasoning_content
            })
          }
        },
        onclose: () => {
          console.log('SSE 连接已关闭')
          this.scrollToBottom()
          this.loading = false
        },
        onerror: (err) => {
          console.error('SSE 发生错误:', err)
          this.loading = false
        }
      }).then(() => {
        console.log('SSE 连接已关闭')
        this.loading = false
      })
    }
  }
}
</script>

<style scoped lang="less">
@import '~highlight.js/styles/github.css';

.main {
  padding: 24px 24px 16px 24px;
  height: 100%;
}

.chat-container {
  width: 100%;
  border-radius: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.message-content {
  padding: 14px 12px;
  position: relative;
  background: #f5f5f5 !important;
  color: #333 !important;
  border-radius: 12px;
  max-width: 70%;

  ::v-deep img {
    max-width: 100%;
  }
}

.copyBtn {
  position: absolute;
  right: 0;
  top: 0;
}

::v-deep .ant-card-body {
  padding: 24px 16px;
  display: flex;
  flex: 1;
}

.chat-box {
  overflow-y: auto;
  padding: 0 8px;
  height: 65vh;
  min-height: 40vh;
}

.operation {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  width: 100%;
}

.message .avatar {
  width: 30px;
  height: 30px;
  margin-right: 8px;
}

::v-deep ul, ol {
  list-style: initial !important;
}

::v-deep ul {
  list-style-type: disc; /* 实心圆点 */
}

::v-deep ol {
  list-style-type: decimal; /* 数字序号 */
}

::v-deep p {
  margin-bottom: 0;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.menu-box {
  display: flex;
  flex-direction: column;
  width: 150px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 10px;
  margin-right: 16px;
}

.menu {
  border: none;
}

.chat {
  width: 70%;
  display: flex;
  flex-direction: column;
}

::v-deep .input-text {
  border: none;
  flex: 1;
  border-radius: 8px;
  margin-bottom: 10px;

  &:focus {
    border: none;
    box-shadow: none;
  }
}

::v-deep .ant-select-selection--single {
  border-radius: 8px;
}

.add-conversation {
  margin-top: auto;
}

.add-conversation-text {
  height: 30px;
  width: 100%;
  text-align: center;
  font-size: 14px;
  line-height: 30px;
  border-radius: 6px;
  cursor: pointer;
}

.bubble1 {
  background-color: #f5f5f5;
}

.bubble2 {
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
  background-color: white;
}
</style>
