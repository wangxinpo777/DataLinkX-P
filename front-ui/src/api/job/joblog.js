import { axios } from '@/utils/request'

export function pageQuery (params) {
  return axios({
    url: '/api/job/log/page',
    method: 'GET',
    params: params
  })
}

export function jobCount (params) {
  return axios({
    url: '/api/job/log/count',
    method: 'GET',
    params: params
  })
}
