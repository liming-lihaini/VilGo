import request from './index'

/**
 * 两委班子 API
 */
export const twoCommitteeApi = {
  create(data) {
    return request({
      url: '/twoCommittee/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/twoCommittee/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/twoCommittee/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/twoCommittee/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/twoCommittee/list',
      method: 'post',
      data: params
    })
  },

  statistics() {
    return request({
      url: '/twoCommittee/statistics',
      method: 'post'
    })
  }
}
