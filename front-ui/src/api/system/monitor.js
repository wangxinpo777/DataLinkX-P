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

export function getSystemVisitStats () {
  return axios({
    url: '/monitor/visit/all',
    method: 'GET'
  })
}
