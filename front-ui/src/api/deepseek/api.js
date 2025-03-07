import request from '@/utils/request'

export function deeepseekChart ({ contents, model }) {
  return request({
    url: '/api/deepseek/chat',
    method: 'POST',
    data: contents, // 发送内容
    params: {
      model: model // 发送 model 参数
    }
  })
}

export function deeepseekMessagesHistory (conversationId) {
  return request({
    url: '/api/deepseek/messages/history',
    method: 'GET',
    params: {
      conversationId: conversationId
    }
  })
}

export function deeepseekConversationsHistory () {
  return request({
    url: '/api/deepseek/conversions/history',
    method: 'GET'
  })
}
