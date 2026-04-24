import request from './index'

/**
 * 党务管理 API
 */
export const partyWorkApi = {
  // ==================== 党员档案相关 ====================

  /**
   * 新增党员档案
   */
  createMember(data) {
    return request({
      url: '/partyWork/member/create',
      method: 'post',
      data
    })
  },

  /**
   * 更新党员档案
   */
  updateMember(data) {
    return request({
      url: '/partyWork/member/update',
      method: 'put',
      data
    })
  },

  /**
   * 删除党员档案
   */
  deleteMember(id) {
    return request({
      url: `/partyWork/member/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取党员档案
   */
  getMember(id) {
    return request({
      url: `/partyWork/member/get/${id}`,
      method: 'get'
    })
  },

  /**
   * 查询党员列表
   */
  listMembers(params) {
    return request({
      url: '/partyWork/member/list',
      method: 'post',
      data: params
    })
  },

  /**
   * 更新党员状态
   */
  updateMemberStatus(id, status) {
    return request({
      url: '/partyWork/member/updateStatus',
      method: 'put',
      params: { id, status }
    })
  },

  // ==================== 党务活动相关 ====================

  /**
   * 新增党务活动
   */
  createActivity(data) {
    return request({
      url: '/partyWork/activity/create',
      method: 'post',
      data
    })
  },

  /**
   * 更新党务活动
   */
  updateActivity(data) {
    return request({
      url: '/partyWork/activity/update',
      method: 'put',
      data
    })
  },

  /**
   * 删除党务活动
   */
  deleteActivity(id) {
    return request({
      url: `/partyWork/activity/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取党务活动
   */
  getActivity(id) {
    return request({
      url: `/partyWork/activity/get/${id}`,
      method: 'get'
    })
  },

  /**
   * 查询党务活动列表
   */
  listActivities(params) {
    return request({
      url: '/partyWork/activity/list',
      method: 'post',
      data: params
    })
  },

  // ==================== 党费收缴相关 ====================

  /**
   * 新增党费记录
   */
  createDues(data) {
    return request({
      url: '/partyWork/dues/create',
      method: 'post',
      data
    })
  },

  /**
   * 更新党费记录
   */
  updateDues(data) {
    return request({
      url: '/partyWork/dues/update',
      method: 'put',
      data
    })
  },

  /**
   * 删除党费记录
   */
  deleteDues(id) {
    return request({
      url: `/partyWork/dues/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取党费记录
   */
  getDues(id) {
    return request({
      url: `/partyWork/dues/get/${id}`,
      method: 'get'
    })
  },

  /**
   * 查询党费列表
   */
  listDues(params) {
    return request({
      url: '/partyWork/dues/list',
      method: 'post',
      data: params
    })
  },

  /**
   * 标记党费为已缴
   */
  markDuesAsPaid(id) {
    return request({
      url: `/partyWork/dues/markPaid/${id}`,
      method: 'put'
    })
  },

  // ==================== 统计相关 ====================

  /**
   * 党员统计
   */
  partyMemberStatistics() {
    return request({
      url: '/partyWork/statistics/member',
      method: 'get'
    })
  },

  /**
   * 党费收缴统计
   */
  partyDuesStatistics() {
    return request({
      url: '/partyWork/statistics/dues',
      method: 'get'
    })
  }
}