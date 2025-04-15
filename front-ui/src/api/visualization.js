import request from '@/utils/request'

export function saveVisualization (parameter) {
  return request({
    url: '/api/images/save-chart-image',
    method: 'post',
    data: parameter
  })
}

export function getVisualization (parameter) {
  return request({
    url: '/api/images/get-chart-image',
    method: 'get',
    params: parameter
  })
}

export function deleteVisualization (parameter) {
  return request({
    url: '/api/images/delete-chart-image',
    method: 'delete',
    params: parameter
  })
}

export function getImageConfig (parameter) {
  return request({
    url: '/api/images/get-chart-image-config',
    method: 'get',
    params: parameter
  })
}
