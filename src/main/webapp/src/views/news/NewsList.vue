<template>
  <div class="news-list">
    <!-- 轮播图区域 -->
    <div class="carousel-section">
      <el-carousel :interval="5000" type="card" height="280px" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in carouselList" :key="index">
          <div class="carousel-item" @click="handleView(item)">
            <img :src="item.coverImage || defaultCover" class="carousel-image" />
            <div class="carousel-overlay">
              <span class="carousel-title">{{ item.title }}</span>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="carouselList.length === 0">
          <div class="carousel-empty">
            <el-icon :size="48"><Document /></el-icon>
            <p>暂无轮播新闻</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 按分类分区的新闻列表 -->
    <div class="category-sections">
      <div
        v-for="category in categories"
        :key="category.code"
        class="category-section"
      >
        <div class="section-header">
          <div class="section-title">
            <span class="title-text">{{ category.name }}</span>
            <span class="news-count">共 {{ getCategoryCount(category.code) }} 条</span>
          </div>
          <el-button type="primary" size="small" @click="handleAddNews(category.code)">
            <el-icon><Plus /></el-icon>添加
          </el-button>
        </div>

        <div class="news-list-container">
          <template v-if="getNewsByCategory(category.code).length > 0">
            <div
              v-for="news in getNewsByCategory(category.code)"
              :key="news.id"
              class="news-item"
              @click="handleView(news)"
            >
              <div class="news-thumb">
                <img :src="news.coverImage || defaultCover" />
              </div>
              <div class="news-info">
                <h3 class="news-title">{{ news.title }}</h3>
                <div v-if="news.keywords" class="news-tags">
                  <el-tag
                    v-for="keyword in getKeywords(news.keywords)"
                    :key="keyword"
                    size="small"
                    type="info"
                  >
                    {{ keyword }}
                  </el-tag>
                </div>
                <div class="news-meta">
                  <span><el-icon><Clock /></el-icon> {{ news.publishTime }}</span>
                  <span><el-icon><User /></el-icon> {{ news.creator }}</span>
                </div>
              </div>
              <div class="news-actions" @click.stop>
                <el-button link type="primary" size="small" @click="handleEdit(news)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDelete(news)">删除</el-button>
              </div>
            </div>
          </template>
          <el-empty
            v-else
            description="暂无新闻，点击添加按钮发布"
            :image-size="60"
          />
        </div>
      </div>
    </div>

    <!-- 新闻表单对话框 -->
    <NewsForm
      v-model:visible="formVisible"
      :news="currentNews"
      :default-category="defaultCategory"
      @success="handleFormSuccess"
    />

    <!-- 新闻详情对话框 -->
    <el-dialog v-model="detailVisible" title="新闻详情" width="900px">
      <div v-if="currentNews" class="news-detail">
        <div class="detail-header">
          <h1 class="detail-title">{{ currentNews.title }}</h1>
          <div class="detail-meta">
            <el-tag :type="getCategoryTag(currentNews.category)">{{ getCategoryName(currentNews.category) }}</el-tag>
            <span class="meta-item"><el-icon><User /></el-icon> {{ currentNews.creator }}</span>
            <span class="meta-item"><el-icon><Clock /></el-icon> {{ currentNews.publishTime }}</span>
          </div>
          <div v-if="currentNews.keywords" class="detail-keywords">
            <el-tag v-for="keyword in getKeywords(currentNews.keywords)" :key="keyword" size="small" type="info">
              {{ keyword }}
            </el-tag>
          </div>
        </div>
        <div v-if="currentNews.coverImage" class="detail-cover">
          <img :src="currentNews.coverImage" />
        </div>
        <div class="detail-content" v-html="currentNews.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, User, Clock, Plus } from '@element-plus/icons-vue'
import NewsForm from './NewsForm.vue'
import { newsApi } from '@/request/news'

const newsLoading = ref(false)
const newsList = ref([])
const carouselList = ref([])
const formVisible = ref(false)
const detailVisible = ref(false)
const currentNews = ref(null)
const defaultCategory = ref('')

const defaultCover = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAwIiBoZWlnaHQ9IjQ1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjRmNGY2Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGR5PSIuM2VtIiBmaWxsPSIjOTAzOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmb250LXNpemU9IjIwIiBmb250LWZhbWlseT0ic2Fucy1zZXJpZiI+5paw6YCa55CG5LiLPC90ZXh0Pjwvc3ZnPg=='

const categories = [
  { code: 'policy_interpretation', name: '政策解读' },
  { code: 'activity_report', name: '活动报道' },
  { code: 'advanced_deeds', name: '先进事迹' },
  { code: 'notice_announcement', name: '通知公告' }
]

const newsQuery = reactive({
  pageNum: 1,
  pageSize: 100,
  title: '',
  category: '',
  status: ''
})

onMounted(() => {
  loadNews()
})

async function loadNews() {
  try {
    newsLoading.value = true
    const response = await newsApi.list(newsQuery)
    newsList.value = response.list || []
    carouselList.value = newsList.value
      .filter(item => item.coverImage)
      .slice(0, 5)
  } catch (error) {
    console.error('加载新闻列表失败:', error)
    ElMessage.error('加载新闻列表失败')
  } finally {
    newsLoading.value = false
  }
}

function getCategoryCount(categoryCode) {
  return newsList.value.filter(n => n.category === categoryCode).length
}

function getNewsByCategory(categoryCode) {
  return newsList.value.filter(n => n.category === categoryCode).slice(0, 10)
}

function handleAddNews(category) {
  defaultCategory.value = category
  currentNews.value = null
  formVisible.value = true
}

function handleEdit(row) {
  defaultCategory.value = ''
  currentNews.value = { ...row }
  formVisible.value = true
}

function handleView(row) {
  currentNews.value = { ...row }
  detailVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确认删除新闻"${row.title}"吗？`,
      '删除确认',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    await newsApi.delete(row.id)
    ElMessage.success('删除成功')
    loadNews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

function handleFormSuccess() {
  loadNews()
}

function getCategoryTag(category) {
  const tagMap = { policy_interpretation: '', activity_report: 'success', advanced_deeds: 'warning', notice_announcement: 'danger' }
  return tagMap[category] || 'info'
}

function getCategoryName(category) {
  const categoryMap = { policy_interpretation: '政策解读', activity_report: '活动报道', advanced_deeds: '先进事迹', notice_announcement: '通知公告' }
  return categoryMap[category] || category
}

function getKeywords(keywords) {
  if (!keywords) return []
  return keywords.split(',').map(k => k.trim()).filter(k => k)
}
</script>

<style scoped>
.news-list { padding: 20px; background: #f5f7fa; min-height: 100vh; }

.carousel-section { margin-bottom: 24px; }
.carousel-item { position: relative; cursor: pointer; border-radius: 8px; overflow: hidden; height: 100%; }
.carousel-image { width: 100%; height: 100%; object-fit: cover; }
.carousel-overlay { position: absolute; bottom: 0; left: 0; right: 0; background: linear-gradient(transparent, rgba(0,0,0,0.7)); padding: 20px; color: white; }
.carousel-title { font-size: 18px; font-weight: bold; }
.carousel-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #909399; }

/* 分类分区 */
.category-section { background: #fff; border-radius: 8px; margin-bottom: 20px; overflow: hidden; }
.section-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #ebeef5; }
.section-title { display: flex; align-items: center; gap: 12px; }
.title-text { font-size: 18px; font-weight: bold; color: #303133; }
.news-count { font-size: 14px; color: #909399; }

/* 新闻列表 */
.news-list-container { padding: 16px 20px; }

.news-item { display: flex; align-items: center; padding: 16px; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.3s; }
.news-item:last-child { border-bottom: none; }
.news-item:hover { background: #f8f9fa; }

.news-thumb { width: 160px; height: 100px; flex-shrink: 0; border-radius: 6px; overflow: hidden; margin-right: 16px; }
.news-thumb img { width: 100%; height: 100%; object-fit: cover; }

.news-info { flex: 1; min-width: 0; }
.news-title { font-size: 16px; font-weight: 600; color: #303133; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.news-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 8px; }
.news-meta { display: flex; gap: 16px; font-size: 13px; color: #909399; }
.news-meta span { display: flex; align-items: center; gap: 4px; }

.news-actions { display: flex; gap: 8px; flex-shrink: 0; margin-left: 16px; opacity: 0; transition: opacity 0.3s; }
.news-item:hover .news-actions { opacity: 1; }

/* 详情页 */
.news-detail { padding: 0; }
.detail-header { margin-bottom: 20px; }
.detail-title { font-size: 24px; font-weight: bold; margin-bottom: 12px; line-height: 1.4; }
.detail-meta { display: flex; align-items: center; gap: 16px; color: #909399; margin-bottom: 12px; }
.meta-item { display: flex; align-items: center; gap: 4px; }
.detail-keywords { display: flex; gap: 8px; flex-wrap: wrap; }
.detail-cover { margin-bottom: 20px; border-radius: 8px; overflow: hidden; }
.detail-cover img { width: 100%; max-height: 400px; object-fit: cover; }
.detail-content { line-height: 1.8; font-size: 14px; }
.detail-content :deep(img) { max-width: 100%; height: auto; }
.detail-content :deep(table) { border-collapse: collapse; width: 100%; margin: 10px 0; }
.detail-content :deep(table td), .detail-content :deep(table th) { border: 1px solid #dcdfe6; padding: 8px; text-align: left; }
</style>