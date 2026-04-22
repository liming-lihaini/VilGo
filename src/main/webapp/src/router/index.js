import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/resident'
  },
  {
    path: '/resident',
    name: 'Resident',
    component: () => import('../views/resident/ResidentList.vue')
  },
  {
    path: '/household',
    name: 'Household',
    component: () => import('../views/household/HouseholdList.vue')
  },
  {
    path: '/special-group',
    name: 'SpecialGroup',
    component: () => import('../views/specialGroup/SpecialGroupList.vue')
  },
  {
    path: '/two-committee',
    name: 'TwoCommittee',
    component: () => import('../views/twoCommittee/TwoCommitteeList.vue')
  },
  {
    path: '/public-activity',
    name: 'PublicActivity',
    component: () => import('../views/publicActivity/PublicActivityList.vue')
  },
  {
    path: '/party-work',
    name: 'PartyWork',
    component: () => import('../views/partyWork/PartyWorkList.vue')
  },
  {
    path: '/notice',
    name: 'Notice',
    component: () => import('../views/notice/NoticeList.vue')
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('../views/news/NewsList.vue')
  },
  {
    path: '/screen',
    name: 'Screen',
    component: () => import('../views/screen/ScreenDashboard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router