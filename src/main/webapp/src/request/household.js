import request from './index'

/**
 * 户籍档案 API
 */
export const householdApi = {
  /**
   * 新增家庭户
   */
  create(data) {
    return request({
      url: '/household/create',
      method: 'post',
      data
    })
  },

  /**
   * 更新家庭户
   */
  update(data) {
    return request({
      url: '/household/update',
      method: 'put',
      data
    })
  },

  /**
   * 删除家庭户
   */
  delete(id) {
    return request({
      url: `/household/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取家庭户详情
   */
  detail(id) {
    return request({
      url: `/household/detail/${id}`,
      method: 'get'
    })
  },

  /**
   * 查询家庭户列表
   */
  list(params) {
    return request({
      url: '/household/list',
      method: 'post',
      data: params
    })
  },

  /**
   * 从村民档案同步户主
   */
  syncFromResident(residentId) {
    return request({
      url: `/household/sync/${residentId}`,
      method: 'post'
    })
  },

  /**
   * 同步所有户主
   */
  syncAll() {
    return request({
      url: '/household/sync-all',
      method: 'post'
    })
  },

  // ========== 家庭成员管理 ==========

  /**
   * 添加家庭成员
   */
  addMember(data) {
    return request({
      url: '/household/member/add',
      method: 'post',
      data
    })
  },

  /**
   * 移除家庭成员
   */
  removeMember(id) {
    return request({
      url: `/household/member/remove/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取家庭成员列表
   */
  getMembers(householdId) {
    return request({
      url: `/household/members/${householdId}`,
      method: 'get'
    })
  },

  // ========== 年度收入管理 ==========

  /**
   * 保存年度收入
   */
  saveIncome(data) {
    return request({
      url: '/household/income/save',
      method: 'post',
      data
    })
  },

  /**
   * 删除年度收入
   */
  deleteIncome(id) {
    return request({
      url: `/household/income/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 查询年度收入列表
   */
  getIncomes(householdId) {
    return request({
      url: `/household/incomes/${householdId}`,
      method: 'get'
    })
  },

  // ========== 户籍变动管理 ==========

  /**
   * 登记户籍变动
   */
  createChange(data) {
    return request({
      url: '/household/change/create',
      method: 'post',
      data
    })
  },

  /**
   * 删除户籍变动记录
   */
  deleteChange(id) {
    return request({
      url: `/household/change/delete/${id}`,
      method: 'delete'
    })
  },

  /**
   * 查询户籍变动列表
   */
  getChanges(householdId) {
    return request({
      url: `/household/changes/${householdId}`,
      method: 'get'
    })
  }
}