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

export function deeepseekConversationsHistory (userId) {
  return request({
    url: '/api/deepseek/conversations/history',
    method: 'GET',
    params: {
      userId: userId
    }
  })
}

export function deeepseekDeleteConversation (conversationId) {
  return request({
    url: '/api/deepseek/conversations/delete',
    method: 'DELETE',
    params: {
      conversationId: conversationId
    }
  })
}

export function deeepseekUpdateConversation (conversationId, conversationName) {
  return request({
    url: '/api/deepseek/conversations/update',
    method: 'PUT',
    data: {
      id: conversationId,
      title: conversationName
    }
  })
}
