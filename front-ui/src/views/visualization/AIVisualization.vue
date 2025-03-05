<template>
  <div class="main">
    <a-card class="chat-container" title="AI 机器人聊天室">
      <div class="chat-box">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
          <a-avatar v-if="message.role === 'user'" class="avatar" src="https://i.pravatar.cc/40?img=1"/>
          <a-avatar v-else class="avatar" src="https://i.pravatar.cc/40?img=2"/>
          <div class="message-content">
            <!-- 渲染Markdown内容 -->
            <div class="bubble mdTextBox" v-html="renderMdText(message.id)"></div>
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

      <a-input-search
        v-model="userInput"
        placeholder="请输入消息..."
        enter-button="发送"
        @search="sendMessage"
        :loading="loading"
      />
    </a-card>
  </div>
</template>

<script>
import { fetchEventSource } from '@microsoft/fetch-event-source'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

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
      return (id) => {
        const message = this.messages.find(msg => msg.id === id)
        return this.markdownRender.render(message.content)
      }
    }
  },
  mounted () {
    this.messages.push(this.message)
  },
  methods: {
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
      console.log(this.userInput)
      if (!this.userInput.trim()) return

      // 添加用户消息
      this.messages.push({ id: crypto.randomUUID(), role: 'user', content: this.userInput })

      // 关闭旧的 SSE 连接
      if (this.eventSource) {
        this.eventSource.close()
      }

      this.loading = true
      const token = localStorage.getItem('Access-Token').replace(/"/g, '')
      const url = `/api/api/deepseek/stream/chat?model=${this.model}&content=${encodeURIComponent(this.userInput)}`
      this.userInput = '' // 清空输入框

      fetchEventSource(url, {
        method: 'GET',
        headers: {
          'ACCESS-TOKEN': token,
          'Accept': 'text/event-stream'
        },
        onopen: () => {
          console.log('SSE 连接已建立')
        },
        onmessage: (event) => {
          console.log('SSE 收到消息:', event.data)
          const data = JSON.parse(event.data)

          // 在 messages 数组中查找相同 id 的消息
          const existingMessage = this.messages.find(msg => msg.id === data.id)

          if (existingMessage) {
            // 追加内容
            existingMessage.content += data.choices[0].delta.content
          } else {
            // 不存在则添加新的消息
            this.messages.push({
              id: data.id,
              role: data.choices[0].delta.role === 'user' ? 'user' : 'assistant',
              content: data.choices[0].delta.content
            })
          }
        },
        onclose: () => {
          console.log('SSE 连接已关闭')
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

<style scoped>
@import '~highlight.js/styles/github.css';

.main {
  padding: 24px 24px 16px 24px;
}

.chat-container {
  width: 100%;
  border-radius: 8px;
}
.message-content {
  padding: 14px 12px 0 12px;
  position: relative;
  background: #f5f5f5 !important;
  color: #333 !important;
  border-radius: 12px;
  max-width: 70%;
}
.copyBtn{
  position: absolute;
  right: 0;
  top: 0;
}

::v-deep .ant-card-body {
  padding: 24px 16px;
}

.chat-box {
  height: 70vh;
  overflow-y: auto;
  margin-bottom: 10px;
  padding: 0 8px;
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.message .avatar {
  width: 30px;
  height: 30px;
  margin-right: 8px;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.bubble {

}
</style>
