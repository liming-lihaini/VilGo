import request from './index'

/**
 * 职位 API
 */
export const positionApi = {
  create(data) {
    return request({
      url: '/position/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/position/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/position/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/position/get/${id}`,
      method: 'get'
    })
  },

  list() {
    return request({
      url: '/position/list',
      method: 'get'
    })
  }
}
