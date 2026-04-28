import request from './index'

/**
 * 新闻动态 API
 */
export const newsApi = {
  create(data) {
    return request({
      url: '/news/create',
      method: 'post',
      data
    })
  },

  update(data) {
    return request({
      url: '/news/update',
      method: 'put',
      data
    })
  },

  delete(id) {
    return request({
      url: `/news/delete/${id}`,
      method: 'delete'
    })
  },

  archive(id) {
    return request({
      url: `/news/archive/${id}`,
      method: 'put'
    })
  },

  get(id) {
    return request({
      url: `/news/get/${id}`,
      method: 'get'
    })
  },

  list(params) {
    return request({
      url: '/news/list',
      method: 'post',
      data: params
    })
  },

  getCategoryList() {
    return request({
      url: '/news/category/list',
      method: 'get'
    })
  }
}
