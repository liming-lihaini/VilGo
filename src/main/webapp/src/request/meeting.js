import request from './index'

/**
 * 会议 API
 */
export const meetingApi = {
  create(data) {
    return request({
      url: '/meeting/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/meeting/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/meeting/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/meeting/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/meeting/list',
      method: 'post',
      data: params
    })
  },

  convertToTask(data) {
    return request({
      url: '/meeting/convert',
      method: 'post',
      data
    })
  }
}
