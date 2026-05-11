import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'
import Login from '@/views/Login.vue'
import { useUserStore } from '@/store/user'

// 路由缓存key
const ROUTE_CACHE_KEY = 'village_last_route'

// 获取缓存的路由
const getCachedRoute = () => {
  try {
    return sessionStorage.getItem(ROUTE_CACHE_KEY)
  } catch {
    return null
  }
}

// 保存路由到缓存
const setCachedRoute = (path) => {
  try {
    sessionStorage.setItem(ROUTE_CACHE_KEY, path)
  } catch {
    // 忽略存储错误
  }
}

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

// 路由守卫 - 每次路由导航后保存当前路由
router.afterEach((to) => {
  if (to.meta.requiresAuth && to.path !== '/login') {
    setCachedRoute(to.fullPath)
  }
})

// 路由守卫 - 认证检查
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn()) {
    next()
  } else {
    next()
  }
})

// 应用初始化时恢复缓存的路由
const initRouter = async () => {
  const cachedPath = getCachedRoute()
  const userStore = useUserStore()

  if (userStore.isLoggedIn() && cachedPath && cachedPath !== '/') {
    // 验证缓存的路由是否有效
    const route = router.getRoutes().find(r => r.path === cachedPath || r.fullPath === cachedPath)
    if (route) {
      router.replace(cachedPath)
      return
    }
  }
  // 默认跳转到村民档案
  router.replace('/resident')
}

// 暴露初始化方法
export { initRouter }