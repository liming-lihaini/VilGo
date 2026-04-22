import request from './index'

/**
 * 村民档案 API
 */
export const residentApi = {
  /**
   * 新增村民档案
   */
  create(data) {
    return request({
      url: '/resident/create',
      method: 'post',
      data
    })
  },

  /**
   * 更新村民档案
   */
  update(data) {
    return request({
      url: '/resident/update',
      method: 'put',
      data
    })
  },

  /**
   * 删除村民档案
   */
  delete(id) {
    return request({
      url: `/resident/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取村民档案
   */
  get(id) {
    return request({
      url: `/resident/get/${id}`,
      method: 'get'
    })
  },

  /**
   * 查询村民列表
   */
  list(params) {
    return request({
      url: '/resident/list',
      method: 'post',
      data: params
    })
  },

  /**
   * 销户登记
   */
  cancel(id) {
    return request({
      url: `/resident/cancel/${id}`,
      method: 'put'
    })
  },

  /**
   * 人口统计
   */
  statistics() {
    return request({
      url: '/resident/statistics',
      method: 'post'
    })
  },

  /**
   * Excel导出
   */
  export(params) {
    return request({
      url: '/resident/export',
      method: 'post',
      data: params,
      responseType: 'blob'
    })
  }
}