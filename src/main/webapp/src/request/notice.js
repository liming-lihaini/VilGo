import request from './index'

/**
 * 公示 API
 */
export const noticeApi = {
  create(data) {
    return request({
      url: '/notice/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/notice/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/notice/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/notice/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/notice/list',
      method: 'post',
      data: params
    })
  },

  submitFeedback(data) {
    return request({
      url: '/notice/feedback',
      method: 'post',
      data
    })
  },

  replyFeedback(data) {
    return request({
      url: '/notice/feedback/reply',
      method: 'put',
      data
    })
  },

  getFeedbackList(noticeId) {
    return request({
      url: `/notice/feedback/list/${noticeId}`,
      method: 'get'
    })
  }
}
