import request from './index'

/**
 * 特殊人群 API
 */
export const specialGroupApi = {
  create(data) {
    return request({
      url: '/specialGroup/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/specialGroup/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/specialGroup/delete/${id}`,
      method: 'delete'
    })
  },

  detail(id) {
    return request({
      url: `/specialGroup/detail/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/specialGroup/list',
      method: 'post',
      data: params
    })
  },

  statistics() {
    return request({
      url: '/specialGroup/statistics',
      method: 'post'
    })
  },

  remind() {
    return request({
      url: '/specialGroup/remind',
      method: 'get'
    })
  },

  export(params) {
    return request({
      url: '/specialGroup/export',
      method: 'post',
      data: params,
      responseType: 'blob'
    })
  },

  addHelpRecord(data) {
    return request({
      url: '/specialGroup/help/add',
      method: 'post',
      data
    })
  },

  deleteHelpRecord(id) {
    return request({
      url: `/specialGroup/help/delete/${id}`,
      method: 'delete'
    })
  },

  listHelpRecords(specialGroupId) {
    return request({
      url: `/specialGroup/help/list/${specialGroupId}`,
      method: 'get'
    })
  }
}