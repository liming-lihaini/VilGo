import request from './index'

/**
 * 活动报名 API
 */
export const activitySignupApi = {
  create(data) {
    return request({
      url: '/activitySignup/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/activitySignup/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/activitySignup/delete/${id}`,
      method: 'delete'
    })
  },

  get(id) {
    return request({
      url: `/activitySignup/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/activitySignup/list',
      method: 'post',
      data: params
    })
  },

  signIn(id) {
    return request({
      url: `/activitySignup/signIn/${id}`,
      method: 'post'
    })
  },

  cancel(id) {
    return request({
      url: `/activitySignup/cancel/${id}`,
      method: 'post'
    })
  }
}