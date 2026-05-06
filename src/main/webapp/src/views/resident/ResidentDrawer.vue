<template>
  <el-drawer v-model="drawerVisible" :title="drawerTitle" size="1000px" destroy-on-close>
    <div class="drawer-content">
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="drawer-tabs">
        <el-tab-pane label="基本信息" name="info">
          <!-- 基本信息内容 -->
          <div class="section">
            <div class="section-header">
              <span class="section-title">基本信息</span>
            </div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="姓名">{{ residentData.name }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ residentData.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
              <el-descriptions-item label="身份证号" :span="2">{{ residentData.idCard }}</el-descriptions-item>
              <el-descriptions-item label="出生日期">{{ residentData.birthDate }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ residentData.phone }}</el-descriptions-item>
              <el-descriptions-item label="人员类型" :span="2">{{ getPersonTypeName(residentData.personType) }}</el-descriptions-item>
              <el-descriptions-item label="本村户籍">
                <el-tag :type="residentData.isLocalHousehold === 1 ? 'success' : 'info'" size="small">
                  {{ residentData.isLocalHousehold === 1 ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="户主">
                <el-tag :type="residentData.isHouseholdHead === 1 ? 'success' : 'info'" size="small">
                  {{ residentData.isHouseholdHead === 1 ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="本村常住">
                <el-tag :type="residentData.isLocalResident === 1 ? 'success' : 'info'" size="small">
                  {{ residentData.isLocalResident === 1 ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="户籍状态">
                <el-tag :type="residentData.householdStatus === 'normal' ? 'success' : 'danger'" size="small">
                  {{ residentData.householdStatus === 'normal' ? '正常' : '已销户' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="住址" :span="2">{{ residentData.address || '-' }}</el-descriptions-item>
              <el-descriptions-item label="外地地址" :span="2">{{ residentData.externalAddress || '-' }}</el-descriptions-item>
              <el-descriptions-item label="保障类型" :span="2">
                <el-tag v-for="type in (residentData.securityType || '').split(',')" :key="type" size="small" style="margin-right: 4px" v-show="type">
                  {{ getSecurityTypeName(type) }}
                </el-tag>
                <span v-if="!residentData.securityType">-</span>
              </el-descriptions-item>
              <el-descriptions-item label="备注" :span="2">{{ residentData.remark || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>

        <el-tab-pane label="附件信息" name="attachment">
          <!-- 附件信息内容 -->
          <div class="section">
            <div class="section-header">
              <span class="section-title">附件信息</span>
              <el-button type="primary" size="small" @click="openAttachmentDialog">管理附件</el-button>
            </div>
            <el-tabs v-model="subTab" class="inner-tabs">
              <el-tab-pane v-for="category in categories" :key="category.value" :label="category.label" :name="category.value">
                <div class="attachment-grid" v-if="filterByCategory(category.value).length > 0">
                  <div v-for="item in filterByCategory(category.value)" :key="item.id" class="attachment-card" @click="previewFile(item)">
                    <!-- 图片预览 -->
                    <div v-if="isImage(item)" class="attachment-preview">
                      <img :src="item.filePath" :alt="item.fileName" />
                    </div>
                    <!-- PDF预览 -->
                    <div v-else-if="isPdf(item)" class="attachment-preview pdf">
                      <el-icon :size="36"><Document /></el-icon>
                      <span>PDF</span>
                    </div>
                    <!-- 不支持格式 -->
                    <div v-else class="attachment-preview unsupported">
                      <el-icon :size="36"><Document /></el-icon>
                      <span>{{ getExtension(item.fileName) }}</span>
                    </div>
                    <div class="attachment-name">{{ item.fileName }}</div>
                  </div>
                </div>
                <el-empty v-else description="暂无附件" :image-size="60" />
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-tab-pane>

        <el-tab-pane label="知识图谱" name="graph">
          <!-- 知识图谱内容 -->
          <div class="section">
            <div class="section-header">
              <span class="section-title">数据关系图谱</span>
            </div>
            <div class="graph-container">
              <!-- 中心节点：村民 -->
              <div class="graph-center">
                <div class="node node-center">
                  <el-icon :size="32"><User /></el-icon>
                  <span>{{ residentData.name }}</span>
                </div>
              </div>

              <!-- 关系节点 -->
              <div class="graph-relations">
                <!-- 党员关系 -->
                <div v-if="graphData.partyMember" class="relation-item" @click="goToPartyMember">
                  <div class="node node-party">
                    <el-icon><Edit /></el-icon>
                    <span>党员档案</span>
                  </div>
                  <div class="relation-label">党员状态: {{ getPartyStatusName(graphData.partyMember.status) }}</div>
                </div>

                <!-- 家庭成员关系 -->
                <div v-if="graphData.householdMembers && graphData.householdMembers.length > 0" class="relation-item" @click="goToHousehold">
                  <div class="node node-household">
                    <el-icon><House /></el-icon>
                    <span>家庭成员</span>
                  </div>
                  <div class="relation-label">{{ graphData.householdMembers.length }} 人</div>
                  <div class="relation-list">
                    <el-tag v-for="member in graphData.householdMembers.slice(0, 5)" :key="member.id" size="small" type="info">
                      {{ member.name }}
                    </el-tag>
                    <span v-if="graphData.householdMembers.length > 5" style="font-size: 12px; color: #999;">+{{ graphData.householdMembers.length - 5 }}</span>
                  </div>
                </div>

                <!-- 特殊人群关系 -->
                <div v-if="graphData.specialGroups && graphData.specialGroups.length > 0" class="relation-item" @click="goToSpecialGroup">
                  <div class="node node-special">
                    <el-icon><UserFilled /></el-icon>
                    <span>特殊人群</span>
                  </div>
                  <div class="relation-list">
                    <el-tag v-for="item in graphData.specialGroups" :key="item.id" size="small" type="warning">
                      {{ item.groupType }}
                    </el-tag>
                  </div>
                </div>

                <!-- 公益活动关系 -->
                <div v-if="graphData.activities && graphData.activities.length > 0" class="relation-item" @click="goToActivity">
                  <div class="node node-activity">
                    <el-icon><Calendar /></el-icon>
                    <span>公益活动</span>
                  </div>
                  <div class="relation-label">参与 {{ graphData.activities.length }} 次</div>
                  <div class="relation-list">
                    <el-tag v-for="item in graphData.activities.slice(0, 3)" :key="item.id" size="small" type="success">
                      {{ item.activityName }}
                    </el-tag>
                    <span v-if="graphData.activities.length > 3" style="font-size: 12px; color: #999;">+{{ graphData.activities.length - 3 }}</span>
                  </div>
                </div>

                <!-- 党务活动关系 -->
                <div v-if="graphData.partyActivities && graphData.partyActivities.length > 0" class="relation-item" @click="goToPartyActivity">
                  <div class="node node-party-activity">
                    <el-icon><Flag /></el-icon>
                    <span>党务活动</span>
                  </div>
                  <div class="relation-label">参与 {{ graphData.partyActivities.length }} 次</div>
                  <div class="relation-list">
                    <el-tag v-for="item in graphData.partyActivities.slice(0, 3)" :key="item.id" size="small" type="danger">
                      {{ item.name }}
                    </el-tag>
                    <span v-if="graphData.partyActivities.length > 3" style="font-size: 12px; color: #999;">+{{ graphData.partyActivities.length - 3 }}</span>
                  </div>
                </div>

                <!-- 附件关系 -->
                <div v-if="attachments.length > 0" class="relation-item">
                  <div class="node node-attachment">
                    <el-icon><Folder /></el-icon>
                    <span>附件</span>
                  </div>
                  <div class="relation-label">{{ attachments.length }} 个附件</div>
                </div>
              </div>

              <!-- 无数据提示 -->
              <el-empty v-if="!graphData.partyMember && (!graphData.specialGroups || graphData.specialGroups.length === 0) && (!graphData.householdMembers || graphData.householdMembers.length === 0) && (!graphData.activities || graphData.activities.length === 0) && (!graphData.partyActivities || graphData.partyActivities.length === 0)" description="暂无关联数据" :image-size="60" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 附件管理弹窗 -->
    <ResidentAttachment
      v-model:visible="attachmentDialogVisible"
      :resident-id="residentData.id"
      :resident-name="residentData.name"
      @refresh="loadAttachments"
    />

    <!-- 预览弹窗 -->
    <el-drawer v-model="previewDrawerVisible" title="附件预览" :size="isFullscreen ? '100%' : '800px'" :fullscreen="isFullscreen" append-to-body destroy-on-close>
      <template #header>
        <div class="preview-header">
          <span>{{ previewName }}</span>
          <el-button type="primary" link @click="toggleFullscreen">
            <el-icon><FullScreen /></el-icon>
            {{ isFullscreen ? '退出全屏' : '全屏' }}
          </el-button>
        </div>
      </template>
      <div class="preview-container" :class="{ fullscreen: isFullscreen }">
        <img v-if="previewType === 'image'" :src="previewUrl" :alt="previewName" class="preview-image" />
        <iframe v-else-if="previewType === 'pdf'" :src="previewUrl" class="preview-iframe"></iframe>
        <div v-else class="preview-unsupported">
          <el-icon :size="60"><Document /></el-icon>
          <p>暂不支持此文件格式的预览</p>
          <el-button type="primary" @click="downloadFile">下载文件</el-button>
        </div>
      </div>
    </el-drawer>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { residentApi } from '@/request/resident'
import { partyWorkApi } from '@/request/partyWork'
import { specialGroupApi } from '@/request/specialGroup'
import { householdApi } from '@/request/household'
import { publicActivityApi } from '@/request/publicActivity'
import { Document, FullScreen, User, Edit, UserFilled, House, Calendar, Flag, Folder, Connection } from '@element-plus/icons-vue'
import ResidentAttachment from './ResidentAttachment.vue'

const router = useRouter()

const props = defineProps({
  visible: Boolean,
  residentId: Number
})

const emit = defineEmits(['update:visible'])

const drawerVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const drawerTitle = ref('村民档案详情')
const residentData = ref({})
const attachments = ref([])
const activeTab = ref('info')
const subTab = ref('id_card')
const attachmentDialogVisible = ref(false)

// 知识图谱数据
const graphData = ref({})

// 附件分类
const categories = [
  { label: '身份证照片', value: 'id_card' },
  { label: '个人照片', value: 'personal_photo' },
  { label: '身份证复印件', value: 'copy' },
  { label: '社保卡复印件', value: 'social_security_copy' },
  { label: '户口本复印件', value: 'household_register_copy' },
  { label: '其他', value: 'other' }
]

// 预览相关
const previewDrawerVisible = ref(false)
const previewUrl = ref('')
const previewType = ref('')
const previewName = ref('')
const isFullscreen = ref(false)

const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

const personTypeMap = {
  farmer: '农民',
  worker: '工人',
  teacher: '教师',
  doctor: '医生',
  student: '学生',
  other: '其他'
}

const securityTypeMap = {
  pension: '社会养老',
  worker_pension: '职工养老',
  allowance: '低保',
  five_guarantee: '五保',
  other: '其他',
  none: '无'
}

const getPersonTypeName = (type) => personTypeMap[type] || '-'
const getSecurityTypeName = (type) => securityTypeMap[type] || type

const getExtension = (filename) => {
  if (!filename) return ''
  const ext = filename.split('.').pop()?.toLowerCase()
  return ext || ''
}

const isImage = (item) => {
  if (!item.fileType) return false
  const imageTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp', 'image/bmp']
  return imageTypes.includes(item.fileType.toLowerCase()) || ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'].includes(getExtension(item.fileName))
}

const isPdf = (item) => {
  if (!item.fileType) return false
  return item.fileType.toLowerCase() === 'application/pdf' || getExtension(item.fileName).toLowerCase() === 'pdf'
}

const getPreviewType = (item) => {
  if (isImage(item)) return 'image'
  if (isPdf(item)) return 'pdf'
  return 'unsupported'
}

const filterByCategory = (category) => {
  return attachments.value.filter(item => item.fileCategory === category)
}

const loadData = async () => {
  console.log('loadData called: visible =', props.visible, 'residentId =', props.residentId)
  if (!props.visible || !props.residentId) return

  try {
    const [resident, attachmentList, graph] = await Promise.all([
      residentApi.get(props.residentId),
      residentApi.getAttachments(props.residentId),
      loadGraph()
    ])
    console.log('loadData result: resident =', resident, 'attachmentList =', attachmentList)
    residentData.value = resident || {}
    attachments.value = attachmentList || []
    graphData.value = graph || {}
    drawerTitle.value = `${resident?.name || ''} 的档案详情`
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 加载知识图谱数据
const loadGraph = async () => {
  const graph = {
    partyMember: null,
    specialGroups: [],
    householdInfo: null,
    householdMembers: [],
    activities: [],
    partyActivities: [],
    helpRecords: []
  }
  try {
    // 获取党员信息
    try {
      const partyRes = await partyWorkApi.getMemberByResidentId(props.residentId)
      if (partyRes) {
        graph.partyMember = partyRes
      }
    } catch (e) { console.error('党员信息加载失败:', e) }

    // 获取特殊人群信息
    try {
      const specialRes = await specialGroupApi.getByResidentId(props.residentId)
      if (specialRes) {
        graph.specialGroups = specialRes.list || specialRes || []
      }
    } catch (e) { console.error('特殊人群加载失败:', e) }

    // 获取户籍信息及家庭成员
    try {
      const householdRes = await householdApi.getMemberHousehold(props.residentId)
      if (householdRes) {
        graph.householdInfo = householdRes
      }
      // 获取家庭成员
      if (householdRes?.householdId) {
        const membersRes = await householdApi.getMembers(householdRes.householdId)
        graph.householdMembers = membersRes?.list || membersRes || []
      }
    } catch (e) { console.error('户籍信息加载失败:', e) }

    // 获取公益活动参与记录
    try {
      const activityRes = await publicActivityApi.getSignupsByResidentId(props.residentId)
      if (activityRes) {
        graph.activities = activityRes.list || activityRes || []
      }
    } catch (e) { console.error('公益活动加载失败:', e) }

    // 获取党务活动参与记录
    try {
      const partyActivityRes = await partyWorkApi.getActivitiesByResidentId(props.residentId)
      if (partyActivityRes) {
        graph.partyActivities = partyActivityRes.list || partyActivityRes || []
      }
    } catch (e) { console.error('党务活动加载失败:', e) }
  } catch (error) {
    console.error('加载知识图谱数据失败:', error)
  }
  return graph
}

// 党员状态名称
const getPartyStatusName = (status) => {
  const map = { activist: '积极分子', developing: '发展对象', probationary: '预备党员', formal: '正式党员' }
  return map[status] || status
}

// 导航方法
const goToPartyMember = () => router.push('/party-work')
const goToSpecialGroup = () => router.push('/special-group')
const goToHousehold = () => router.push('/household')
const goToActivity = () => router.push('/public-activity')
const goToPartyActivity = () => router.push('/party-work')

const loadAttachments = async () => {
  console.log('ResidentDrawer loadAttachments: residentId =', props.residentId)
  if (!props.residentId) return
  try {
    const list = await residentApi.getAttachments(props.residentId)
    console.log('ResidentDrawer loadAttachments: list =', list)
    attachments.value = list || []
  } catch (error) {
    console.error('加载附件失败:', error)
  }
}

const openAttachmentDialog = () => {
  attachmentDialogVisible.value = true
}

const previewFile = (item) => {
  previewUrl.value = item.filePath
  previewType.value = getPreviewType(item)
  previewName.value = item.fileName
  previewDrawerVisible.value = true
}

const downloadFile = () => {
  const link = document.createElement('a')
  link.href = previewUrl.value
  link.download = previewName.value
  link.click()
}

watch(() => props.visible, (val) => {
  if (val) {
    // 延迟一点确保residentId已设置
    setTimeout(() => {
      if (props.residentId) {
        loadData()
      }
    }, 100)
  }
})

watch(() => props.residentId, (val) => {
  if (val && props.visible) {
    loadData()
  }
})
</script>

<style scoped>
.drawer-content {
  padding: 0 20px;
}

.section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-header .section-title {
  margin-bottom: 0;
}

.drawer-tabs {
  margin-top: 12px;
}

.attachment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.attachment-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.attachment-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.attachment-preview {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.attachment-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attachment-preview.pdf,
.attachment-preview.unsupported {
  flex-direction: column;
  color: #909399;
  gap: 4px;
}

.attachment-preview.pdf span,
.attachment-preview.unsupported span {
  font-size: 11px;
}

.attachment-name {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.preview-container.fullscreen {
  min-height: calc(100vh - 80px);
  background: #000;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-container.fullscreen .preview-image {
  max-height: calc(100vh - 120px);
}

.preview-iframe {
  width: 100%;
  height: 70vh;
  border: none;
}

.preview-container.fullscreen .preview-iframe {
  height: calc(100vh - 120px);
}

.preview-unsupported {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.preview-unsupported p {
  margin: 16px 0;
}

/* 知识图谱样式 */
.graph-container {
  padding: 20px;
}

.graph-center {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.node {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 8px;
  color: #fff;
}

.node-center {
  background: #409eff;
  font-size: 16px;
  font-weight: 600;
}

.node-party {
  background: #e6a23c;
}

.node-special {
  background: #f56c6c;
}

.node-household {
  background: #67c23a;
}

.node-activity {
  background: #909399;
}

.node-party-activity {
  background: #c71585;
}

.node-attachment {
  background: #409eff;
}

.graph-relations {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}

.relation-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.relation-item:hover {
  transform: scale(1.05);
}

.relation-label {
  font-size: 12px;
  color: #909399;
}

.relation-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.inner-tabs {
  margin-top: 12px;
}
</style>