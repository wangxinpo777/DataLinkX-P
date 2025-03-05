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
