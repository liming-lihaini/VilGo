import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import Login from '@/views/Login.vue'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/resident',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/resident',
        name: 'Resident',
        meta: { title: '村民档案' },
        component: () => import('../views/resident/ResidentList.vue')
      },
      {
        path: '/resident/statistics',
        name: 'ResidentStatistics',
        meta: { title: '村民统计' },
        component: () => import('../views/resident/ResidentStatistics.vue')
      },
      {
        path: '/household',
        name: 'Household',
        meta: { title: '户籍管理' },
        component: () => import('../views/household/HouseholdList.vue')
      },
      {
        path: '/household/member',
        name: 'HouseholdMember',
        meta: { title: '户籍成员' },
        component: () => import('../views/household/HouseholdList.vue')
      },
      {
        path: '/household/change',
        name: 'HouseholdChange',
        meta: { title: '户籍变动' },
        component: () => import('../views/household/HouseholdList.vue')
      },
      {
        path: '/special-group',
        name: 'SpecialGroup',
        meta: { title: '特殊人群' },
        component: () => import('../views/specialGroup/SpecialGroupList.vue')
      },
      {
        path: '/two-committee',
        name: 'TwoCommittee',
        meta: { title: '两委班子' },
        component: () => import('../views/twoCommittee/TwoCommitteeList.vue')
      },
      {
        path: '/two-committee/position',
        name: 'TwoCommitteePosition',
        meta: { title: '职位管理' },
        component: () => import('../views/twoCommittee/PositionList.vue')
      },
      {
        path: '/public-activity',
        name: 'PublicActivity',
        meta: { title: '公益活动' },
        component: () => import('../views/publicActivity/PublicActivityList.vue')
      },
      {
        path: '/party-work',
        name: 'PartyWork',
        meta: { title: '党务管理' },
        component: () => import('../views/partyWork/PartyWorkList.vue')
      },
      {
        path: '/party-work/member',
        name: 'PartyMember',
        meta: { title: '党员档案' },
        component: () => import('../views/partyWork/PartyMemberList.vue')
      },
      {
        path: '/party-work/activity',
        name: 'PartyActivity',
        meta: { title: '党务活动' },
        component: () => import('../views/partyWork/PartyActivityList.vue')
      },
      {
        path: '/party-work/dues',
        name: 'PartyDues',
        meta: { title: '党费管理' },
        component: () => import('../views/partyWork/PartyDuesList.vue')
      },
      {
        path: '/party-work/statistics',
        name: 'PartyStatistics',
        meta: { title: '党务统计' },
        component: () => import('../views/partyWork/PartyStatistics.vue')
      },
      {
        path: '/notice',
        name: 'Notice',
        meta: { title: '公示栏' },
        component: () => import('../views/notice/NoticeList.vue')
      },
      {
        path: '/news',
        name: 'News',
        meta: { title: '新闻动态' },
        component: () => import('../views/news/NewsList.vue')
      },
      {
        path: '/screen',
        name: 'Screen',
        meta: { title: '数字大屏' },
        component: () => import('../views/screen/ScreenDashboard.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn()) {
    next('/')
  } else {
    next()
  }
})

export default router