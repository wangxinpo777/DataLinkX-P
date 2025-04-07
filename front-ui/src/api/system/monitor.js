import { axios } from '@/utils/request'

export function getSystemMonitor () {
  return axios({
    url: '/monitor/server/info',
    method: 'GET'
  })
}

export function getSystemMonitorAnalysis (params) {
  return axios({
    url: '/monitor/server/info/analysis',
    method: 'GET',
    params: params
  })
}
