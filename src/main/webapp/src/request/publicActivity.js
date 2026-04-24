import request from './index'

/**
 * 公益活动 API
 */
export const publicActivityApi = {
  create(data) {
    return request({
      url: '/publicActivity/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/publicActivity/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/publicActivity/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/publicActivity/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/publicActivity/list',
      method: 'post',
      data: params
    })
  },

  statistics(params) {
    return request({
      url: '/publicActivity/statistics',
      method: 'post',
      data: params
    })
  }
}