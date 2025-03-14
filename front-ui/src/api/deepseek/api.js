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

export function deeepseekStreamChart (content,
                                     userId,
                                     conversationId, model) {
  return request({
    url: '/api/deepseek/stream/chat',
    method: 'POST',
    data: {
      content: content,
      userId: userId,
      conversationId: conversationId
    },
    params: {
      model: model
    }
  })
}

export function getDeepseekStream (conversationId, userId) {
  return request({
    url: '/api/deepseek/get/stream/chat',
    method: 'GET',
    params: {
      conversationId: conversationId,
      userId: userId
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
