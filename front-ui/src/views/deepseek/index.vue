<template>
  <div class="main">
    <a-card class="chat-container" title="DeepSeek" :headStyle="{minHeight: '50px',maxHeight: '50px'}">
      <div class="menu-box" v-if="conversations.length > 0">
        <a-menu mode="vertical" class="menu" :selected-keys="[conversationId]">
          <template v-for="item in conversations">
            <a-menu-item
              :key="item.id"
              :class="{ 'edit': editConversationId === item.id && editConversationVisible }"
              @click="flushMessages(item.id)"
              style="position: relative;"
              @mouseenter="handleMouseEnter(item.id)"
              @mouseleave="handleMouseLeave()">
              <a-input
                ref="editInput"
                v-if="editConversationId === item.id && editConversationVisible"
                v-model="item.title"
                @blur="updateConversation(item.id,item.title)"
                style="width:100%;"
              />
              <a-tooltip
                v-else
                :title="item.title"
                :mouseEnterDelay="0.5"
                placement="right">
                {{ item.title }}
              </a-tooltip>
              <a-dropdown
                :trigger="['click']"
                @visibleChange="visible => toggleDropdown = visible"
                style="position: absolute; right: 0; top: 15px;"
                v-if="hoveredItem === item.id && !editConversationVisible">
                <a-icon type="dash" />
                <a-menu slot="overlay">
                  <a-menu-item>
                    <div @click="deleteConversation(item.id)">
                      <a-icon
                        type="delete"
                        style="margin-right: 8px;"
                      />
                      <span>删除</span>
                    </div>
                  </a-menu-item>
                  <a-menu-item>
                    <div @click="editConversation(item.id)">
                      <a-icon
                        type="edit"
                        style="margin-right: 8px;"
                      />
                      <span>重命名</span>
                    </div>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </a-menu-item>
          </template>
        </a-menu>
        <div class="add-conversation">
          <a-checkable-tag :checked="true" class="add-conversation-text" @change="messages = [];conversationId = '';userInput = ''">
            新增会话
          </a-checkable-tag>
        </div>
      </div>
      <div class="chat-box" ref="messageContainer">
        <div class="message">
          <a-icon class="avatar" style="color: rgba(59,130,246)" :component="deepSeek" />
          <div class="message-content">
            <div class="bubble mdTextBox">
              <p>你好，我是 DeepSeek，有什么可以帮助你的吗？</p>
            </div>
          </div>
        </div>
        <div v-for="message in messages" :key="message.id" :class="['message', message.role]">
          <template v-if="message.role === 'user'">
            <div class="message-content">
              <!-- 渲染Markdown内容 -->
              <div class="bubble1" v-html="renderUserMarkdownRender(message)"></div>
            </div>
            <img class="userAvatar" :src="avatar"/>
          </template>
          <template v-else>
            <a-icon class="avatar" style="color: rgba(59,130,246)" :component="deepSeek" />
            <div class="message-content" @mouseenter="copyBtnVisible = true" @mouseleave="copyBtnVisible = false">
              <div
                class="bubble2"
                v-if="message.reasoningContent"
                v-html="renderMdTextReasoning(message)"
              >
              </div>
              <!-- 渲染Markdown内容 -->
              <div class="bubble1" v-html="renderMdText(message)"></div>
              <!-- 复制按钮 -->
              <a-button
                :class="['copyBtn', { 'copyBtnVisible': copyBtnVisible }]"
                type="link"
                size="small"
                style="transition: all 0.3s; opacity: 0;"
                @click="copyText(message.id)"
                icon="copy"
              />
            </div>
          </template>
        </div>
      </div>
      <div class="operation">
        <a-icon class="clearIcon" v-if="userInput" type="close" @click="userInput=''"/>
        <a-textarea
          v-model="userInput"
          class="input-text"
          placeholder="询问任何问题"
          @pressEnter="e => sendMessage(e)"
        >
        </a-textarea>
        <div class="buttons" style="position: relative;">
          <!--excel或者csv-->
          <a-upload
            :showUploadList="false"
            :accept="'.csv,.xls,.xlsx'"
            :beforeUpload="beforeUpload"
          >
            <a-button
              shape="circle"
              setsize="small"
              icon="file-excel" />
          </a-upload>
          <a-select
            v-model="model"
            placeholder="选择模型"
            :dropdownStyle="{ bottom: '100%', top: 'auto' }"
            style="margin-left: 8px"
          >
            <a-select-option value="deepseek-chat">DeepSeek V3</a-select-option>
            <a-select-option value="deepseek-reasoner">DeepSeek R1</a-select-option>
          </a-select>
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
    </a-card>
  </div>
</template>

<script>
import MarkdownIt from 'markdown-it'
import markdownItPlainText from 'markdown-it-plain-text'
import markdownItCodeCopy from 'markdown-it-code-copy'
import hljs from 'highlight.js'
import {
  deeepseekConversationsHistory,
  deeepseekDeleteConversation,
  deeepseekMessagesHistory,
  deeepseekStreamChart,
  deeepseekUpdateConversation
} from '@/api/deepseek/api'
import storage from 'store'
import { AVATAR } from '@/store/mutation-types'
import { deepSeek } from '@/core/icons'
import * as XLSX from 'xlsx'
import { fetchEventSource } from '@microsoft/fetch-event-source'

const pyodideWorker = new Worker('/js/pyodideWorker.js')
export default {
  name: 'AIVisualization',
  data () {
    return {
      count: 0,
      loadedPython: false,
      copyBtnVisible: false,
      runningPython: false,
      markdownRender: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        highlight: (str, lang) => {
          if (lang && hljs.getLanguage(lang)) {
            try {
              return `<div class="code-wrapper"><pre class="hljs"><code>${hljs.highlight(lang, str, true).value}</code></pre>
<button class="run-btn" onclick="runCode(this, '${lang}')">` + this.openButton(lang) + `</button></div>`
            } catch (err) {
              console.error('代码高亮失败:', err) // 可选：打印错误
            }
          }
          return `<div class="code-wrapper"><pre class="hljs"><code>` + this.markdownRender.utils.escapeHtml(str) + `</code></pre></div>`
        }
      }).use(markdownItCodeCopy, {
        iconStyle: 'font-size: 18px; opacity: 0.6;',
        iconClass: 'mdi mdi-content-copy', // 复制图标（默认 Material Design Icons）
        buttonStyle: 'position: absolute; top: 10px; right: 10px; cursor: pointer; background: none; border: none;',
        buttonClass: 'copy-btn',
        element: '<span>📋</span>', // 可自定义按钮
        onSuccess: (e) => this.$message.success('复制成功！'),
        onError: (e) => this.$message.error('复制失败！')
      }),
      userMarkdownRender: new MarkdownIt({
        html: true,
        linkify: true,
        typographer: true,
        breaks: true
      }).use(markdownItPlainText),
      userInput: '',
      hoveredItem: null,
      conversations: [],
      conversationId: '',
      onConversationId: '',
      editConversationId: '',
      tempTitle: '',
      messages: [],
      message: {
        id: 0,
        role: 'assistant',
        content: '你好，我是 DeepSeek，有什么可以帮助你的吗？'
      },
      loading: false,
      eventSource: null,
      model: 'deepseek-chat',
      toggleDropdown: false,
      editConversationVisible: false
    }
  },
  computed: {
    avatar () {
      return storage.get(AVATAR)
    },
    deepSeek () {
      return deepSeek
    },
    renderMdText () {
      return (message) => {
        return this.markdownRender.render(message.content)
      }
    },
    openButton () {
      return (lang) => {
        if (lang === 'html') {
          return '查看结果'
        } else if (lang === 'python') {
          if (this.loadedPython) {
            return '运行Python'
          } else {
            return 'Python环境加载中...'
          }
        } else {
          return '查看数据'
        }
      }
    }
  },
  mounted () {
    window.runCode = this.runCode
    this.initDeepSeek()
    pyodideWorker.onmessage = (event) => {
      const { result, error } = event.data
      if (result === 'True') {
        this.loadedPython = true
        console.log('Pyodide 加载成功')
      } else {
        result && console.log('Pyodide Result:', result)
        error && console.error('Pyodide Error:', error)
        document.querySelector('.result-container-' + this.count).innerHTML = 'Python运行中...\n' + '运行结果：' + (error ? `<pre>${error}</pre>` : `<pre>${result}</pre>`)
        document.querySelector('.run-btn-' + this.count).innerHTML = '运行Python'
        this.runningPython = false
      }
    }
    pyodideWorker.postMessage({ pythonCode: 'Test' })
  },
  methods: {
    beforeUpload (file) {
      // 判断文件类型
      const fileType = file.type
      // 限制文件大小
      if (file.size > 1024 * 1024 * 5) {
        this.$message.error('文件大小不能超过5M')
        return false
      }
      if (fileType === 'application/vnd.ms-excel' || fileType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
        this.analyzeExcelFile(file)
      } else if (fileType === 'text/csv') {
        this.analyzeCsvFile(file)
      } else {
        this.$message.error('请上传Excel或者CSV文件')
      }
      return false
    },
    // 处理上传是Excel文件
    analyzeExcelFile (file) {
      const fileReader = new FileReader()
      fileReader.readAsBinaryString(file)
      fileReader.onload = (ev) => {
        try {
          const workbook = XLSX.read(ev.target.result, {
            type: 'binary'
          })
          const workSheets = workbook.Sheets[workbook.SheetNames[0]]
          this.userInput += XLSX.utils.sheet_to_csv(workSheets)
        } catch (e) {
          console.log(e)
        }
      }
    },
    // 处理是csv格式
    analyzeCsvFile (file) {
      const fileReader = new FileReader()
      fileReader.readAsText(file)
      fileReader.onload = (ev) => {
        try {
          // 处理解析之后的csv格式
          this.userInput += ev.target.result
        } catch (e) {
          console.log(e)
        }
      }
    },
    deleteConversation (conversationId) {
      this.$confirm({
        title: '删除会话',
        content: '确定要删除该会话吗？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          this.toggleDropdown = false
          deeepseekDeleteConversation(conversationId).then((res) => {
            this.initDeepSeek()
            if (res.status === '0') {
              this.$message.success('删除成功')
            } else {
              this.$message.error('删除失败')
            }
          }).catch(() => {
            this.$message.error('删除失败')
          })
        },
        onCancel: () => {
          this.toggleDropdown = false
        }
      })
    },
    updateConversation (id, title) {
      if (!title) {
        this.$message.error('会话名称不能为空')
        this.conversations.find(item => item.id === id).title = this.tempTitle
        this.editConversationVisible = false
        this.editConversationId = ''
        return
      }
      if (title.length > 10) {
        this.$message.error('会话名称不能超过10个字符')
        this.conversations.find(item => item.id === id).title = this.tempTitle
        this.editConversationVisible = false
        this.editConversationId = ''
        return
      }
      if (title === this.tempTitle) {
        this.editConversationVisible = false
        this.editConversationId = ''
        return
      }
      deeepseekUpdateConversation(id, title).then((res) => {
        if (res.status === '0') {
          this.$message.success('修改成功')
        } else {
          this.$message.error('修改失败')
        }
        this.editConversationVisible = false
        this.editConversationId = ''
        this.initDeepSeek()
      }).catch(() => {
        this.$message.error('修改失败')
        this.editConversationVisible = false
        this.editConversationId = ''
      })
    },
    editConversation (id) {
      this.editConversationId = id
      this.editConversationVisible = true
      this.toggleDropdown = false
      this.tempTitle = this.conversations.find(item => item.id === id).title
      this.$nextTick(() => {
        this.$refs.editInput[0].focus()
      })
    },
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
      if (conversationId !== this.conversationId && !this.toggleDropdown && !this.editConversationVisible) {
        this.conversationId = conversationId
        this.deeepseekMessagesHistory()
      }
    },
    initDeepSeek () {
      deeepseekConversationsHistory(JSON.parse(localStorage.getItem('User')).userId).then(res => {
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
    sendMessage (e) {
      if (!this.userInput.trim() || this.loading) return

      if (e.shiftKey) {
        return
      }
      e.preventDefault()
      // 关闭旧的 SSE 连接
      if (this.eventSource) {
        this.eventSource = null
      }

      this.loading = true
      const token = localStorage.getItem('Access-Token').replace(/"/g, '')
      this.messages.push({
        id: this.messages.length + 1,
        role: 'user',
        content: this.userInput
      })
      const content = this.userInput
      this.userInput = '' // 清空输入框
      this.scrollToBottom()
      deeepseekStreamChart(content, JSON.parse(localStorage.getItem('User')).userId, this.conversationId, this.model).then(res => {
        this.conversationId = res.result
        this.onConversationId = this.conversationId
        this.eventSource = fetchEventSource(`api/api/deepseek/get/stream/chat?userId=${JSON.parse(localStorage.getItem('User')).userId}&conversationId=${this.conversationId}`, {
          openWhenHidden: true,
          headers: {
            'ACCESS-TOKEN': token,
            'Accept': 'text/event-stream',
            'Content-Type': 'application/json' // 必须指定 JSON 类型
          },
          onopen: () => {
            deeepseekConversationsHistory(JSON.parse(localStorage.getItem('User')).userId).then(res => {
              this.conversations = res.result
            })
            console.log('SSE 连接已建立')
          },
          onmessage: (event) => {
            if (this.conversationId === '' || this.conversationId !== this.onConversationId) {
              return
            }
            const data = JSON.parse(event.data)

            // 在 messages 数组中查找相同 id 的消息
            const existingMessage = this.messages.find(msg => msg.id === data.id)

            if (existingMessage) {
              // 追加内容
              if (data.choices[0].delta.content) {
                existingMessage.content += data.choices[0].delta.content
              }
              if (data.choices[0].delta.reasoning_content) {
                existingMessage.reasoningContent += data.choices[0].delta.reasoning_content
              }
            } else {
              // 不存在则添加新的消息
              this.messages.push({
                id: data.id,
                role: data.choices[0].delta.role === 'user' ? 'user' : 'assistant',
                content: data.choices[0].delta.content ? data.choices[0].delta.content : '',
                reasoningContent: data.choices[0].delta.reasoning_content ? data.choices[0].delta.reasoning_content : ''
              })
            }
          },
          onclose: () => {
            console.log('SSE 连接已关闭')
            this.scrollToBottom()
            this.loading = false
          },
          onerror: (err) => {
            this.loading = false
            console.error('SSE 发生错误:', err)
            throw new Error('SSE 连接发生错误')
          }
        })
      }).catch(() => {
        this.$message.error('发送失败')
        this.loading = false
      })
    },
    handleMouseLeave () {
      if (!this.toggleDropdown) {
        this.hoveredItem = null
      }
    },
    handleMouseEnter (id) {
      if (!this.toggleDropdown) {
        this.hoveredItem = id
      }
    },
    renderMdTextReasoning (message) {
      return this.markdownRender.render(message.reasoningContent)
    },
    renderUserMarkdownRender (message) {
      return this.userMarkdownRender.render(message.content)
    },
    runCode (button, lang) {
      if (lang === 'html') {
        const html = button.previousElementSibling.textContent.trim()
        const blob = new Blob([html], { type: 'text/html' })
        const url = URL.createObjectURL(blob)
        window.open(url, '_blank')
      } else if (lang === 'python') {
        if (!this.loadedPython) {
          this.$message.error('Python环境加载中，请稍后再试')
          return
        }
        if (this.runningPython) {
          this.$message.error('Python脚本正在运行中，请稍后再试')
          return
        }
        // 获取按钮类名中的数字
        const className = button.className
        const match = className.match(/run-btn-(\d+)/)
        if (match) {
          this.count = match[1]
        } else {
          this.count++
        }
        button.classList.add('run-btn-' + this.count)
        button.innerHTML = 'Python运行中...'
        this.runningPython = true
        const code = button.previousElementSibling.textContent
        // 获取按钮的父级元素
        const parentElement = button.parentNode
        // 获取父级元素的父级元素
        const grandParentElement = parentElement.parentNode
        // 检查是否已经存在结果 div，避免重复创建
        let resultDiv = grandParentElement.querySelector('.result-container')
        if (!resultDiv) {
          // 创建一个新的 div 元素
          resultDiv = document.createElement('div')
          resultDiv.classList.add('result-container', 'result-container-' + this.count)
          resultDiv.innerHTML = '正在运行中...\n'
          // 将结果 div 插入到父级同级
          grandParentElement.appendChild(resultDiv)
        }
        pyodideWorker.postMessage({ pythonCode: code })
      } else {
        const data = button.previousElementSibling.textContent
        const blob = new Blob([data], { type: 'text/plain;charset=utf-8' })
        const url = URL.createObjectURL(blob)
        window.open(url, '_blank')
      }
    }
  }
}
</script>

<style scoped lang="less">
@import '~highlight.js/styles/vs.css';

.main {
  padding: 24px 24px 16px 24px;
  height: 100%;

  .edit {
    padding: 0 8px;

    .ant-input {
      padding: 0 8px;
    }
  }
}

.chat-container {
  width: 100%;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.message-content {
  padding: 14px 12px;
  position: relative;
  word-wrap:break-word;
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
  right: 3px;
  top: 3px;
}
.copyBtnVisible {
  opacity: 1 !important;
}

::v-deep .ant-card-body {
  padding: 24px 16px;
  display: flex;
  flex: 1;
  min-height: calc(100vh - 65px - 40px - 50px);
  max-height: calc(100vh - 65px - 40px - 50px);
}

.chat-box {
  border: 1px solid #f0f0f0;
  border-radius: 8px 0 0 8px;
  overflow-y: auto;
  padding: 16px;
  flex: 1;
}

.operation {
  border: 1px solid #f0f0f0;
  border-left: none;
  border-radius: 0 8px 8px 0;
  padding: 10px;
  display: flex;
  width: 20vw;
  flex-direction: column;
  position: relative;
  .clearIcon {
    position: absolute;
    right: 5px;
    top: 5px;
    cursor: pointer;
    z-index: 1;
  }
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  width: 100%;
  .avatar {
    width: 30px;
    height: 30px;
    margin-right: 8px;
    font-size: 30px;
  }
  .userAvatar {
    width: 30px;
    height: 30px;
    margin-left: 8px;
  }

}

::v-deep .copy-btn {
  top: 5px !important;
  right: 5px !important;
  &:hover {
    opacity: 0.8 ;
  }
}

::v-deep .code-wrapper {
  position: relative;
  margin-bottom: 15px;
}

::v-deep .result-container {
  background-color: white;
  padding: 10px;
}

::v-deep .run-btn {
  position: absolute;
  max-width: 200px;
  bottom: -5px;
  right: 0;
  padding: 5px 10px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  &:hover {
    background: #0056b3;
  }
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
  overflow-y: auto;
  border: none;
}

::v-deep .input-text {
  border: none;
  resize: none;
  flex: 1;
  word-break: break-all;
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
::v-deep table {
  width: 100%;
}

::v-deep th, td {
  border: 1px solid #ddd; /* 添加边框 */
  padding: 10px !important;
}

::v-deep th {
  background-color: #f2f2f2; /* 表头背景颜色 */
}

/* 条纹效果 - 奇数行和偶数行交替背景色 */
::v-deep tr:nth-child(odd) {
  background-color: #f9f9f9; /* 奇数行背景颜色 */
}

::v-deep tr:nth-child(even) {
  background-color: #f2f2f2; /* 偶数行背景颜色 */
}

::v-deep tr:hover {
  background-color: #e9e9e9; /* 鼠标悬停时背景颜色 */
}
</style>
