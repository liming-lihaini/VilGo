import request from './index'

/**
 * 任务 API
 */
export const taskApi = {
  create(data) {
    return request({
      url: '/task/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/task/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/task/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/task/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/task/list',
      method: 'post',
      data: params
    })
  }
}
