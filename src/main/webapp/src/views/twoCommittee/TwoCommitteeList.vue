<template>
  <div class="two-committee">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 成员管理 -->
      <el-tab-pane label="成员管理" name="member">
        <div class="filter-bar">
          <el-form :inline="true" :model="memberQuery" class="query-form">
            <el-form-item label="姓名" style="width: 150px">
              <el-input v-model="memberQuery.name" placeholder="姓名" clearable @keyup.enter="loadMembers" />
            </el-form-item>
            <el-form-item label="职务" style="width: 150px">
              <el-select v-model="memberQuery.position" placeholder="请选择" clearable @change="loadMembers">
                <el-option label="书记" value="书记" />
                <el-option label="主任" value="主任" />
                <el-option label="副主任" value="副主任" />
                <el-option label="委员" value="委员" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadMembers">查询</el-button>
              <el-button @click="resetMemberQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="toolbar">
          <el-button type="primary" @click="handleAddMember">新增成员</el-button>
          <el-button type="warning" @click="handleMemberStatistics">统计</el-button>
        </div>

        <el-table v-loading="memberLoading" :data="memberList" stripe border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="name" label="姓名" width="100" align="center" />
          <el-table-column prop="gender" label="性别" width="60" align="center">
            <template #default="{ row }">{{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}</template>
          </el-table-column>
          <el-table-column prop="positionNames" label="职务" min-width="150" align="center" />
          <el-table-column prop="phone" label="联系电话" width="120" />
          <el-table-column prop="dividedWork" label="分管工作" min-width="150" />
          <el-table-column prop="joinDate" label="入职时间" width="120" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditMember(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteMember(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="memberQuery.pageNum"
            v-model:page-size="memberQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="memberTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadMembers"
            @current-change="loadMembers"
          />
        </div>
      </el-tab-pane>

      <!-- 任务管理 -->
      <el-tab-pane label="任务管理" name="task">
        <div class="filter-bar">
          <el-form :inline="true" :model="taskQuery" class="query-form">
            <el-form-item label="接收人" style="width: 150px">
              <el-select v-model="taskQuery.assigneeId" placeholder="请选择" clearable @change="loadTasks">
                <el-option v-for="m in memberList" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="任务状态" style="width: 150px">
              <el-select v-model="taskQuery.status" placeholder="请选择" clearable @change="loadTasks">
                <el-option label="待处理" value="pending" />
                <el-option label="进行中" value="ongoing" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadTasks">查询</el-button>
              <el-button @click="resetTaskQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="toolbar">
          <el-button type="primary" @click="handleAddTask">分配任务</el-button>
        </div>

        <el-table v-loading="taskLoading" :data="taskList" stripe border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="content" label="任务内容" min-width="200" />
          <el-table-column prop="assigner" label="分配人" width="100" align="center" />
          <el-table-column label="接收人" width="100" align="center">
            <template #default="{ row }">{{ getMemberName(row.assigneeId) }}</template>
          </el-table-column>
          <el-table-column prop="deadline" label="完成期限" width="120" />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)" size="small">{{ getStatusName(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="result" label="完成成效" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditTask(row)">编辑</el-button>
              <el-button link type="success" size="small" @click="handleUpdateTaskStatus(row)">状态</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteTask(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="taskQuery.pageNum"
            v-model:page-size="taskQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="taskTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadTasks"
            @current-change="loadTasks"
          />
        </div>
      </el-tab-pane>

      <!-- 会议管理 -->
      <el-tab-pane label="会议管理" name="meeting">
        <div class="filter-bar">
          <el-form :inline="true" :model="meetingQuery" class="query-form">
            <el-form-item label="会议时间" style="width: 200px">
              <el-date-picker v-model="meetingQuery.meetingTime" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" clearable @change="loadMeetings" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadMeetings">查询</el-button>
              <el-button @click="resetMeetingQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="toolbar">
          <el-button type="primary" @click="handleAddMeeting">新增会议</el-button>
        </div>

        <el-table v-loading="meetingLoading" :data="meetingList" stripe border>
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="meetingTime" label="会议时间" width="160" />
          <el-table-column prop="location" label="会议地点" width="150" />
          <el-table-column prop="attendees" label="参会人员" min-width="150" show-overflow-tooltip />
          <el-table-column prop="content" label="会议内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="resolution" label="决议事项" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditMeeting(row)">编辑</el-button>
              <el-button link type="warning" size="small" @click="handleConvertToTask(row)">转任务</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteMeeting(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="meetingQuery.pageNum"
            v-model:page-size="meetingQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="meetingTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadMeetings"
            @current-change="loadMeetings"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 成员表单弹窗 -->
    <el-dialog v-model="memberDialogVisible" :title="memberDialogTitle" width="550px" destroy-on-close>
      <el-form ref="memberFormRef" :model="memberForm" :rules="memberRules" label-width="100px">
        <el-form-item label="村民选择" prop="residentId">
          <div style="display: flex; gap: 8px; width: 100%">
            <el-input v-model="memberForm.name" placeholder="请选择村民" readonly style="flex: 1" />
            <el-button type="primary" @click="handleSelectResident">选择村民</el-button>
          </div>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="memberForm.gender" placeholder="请选择" :disabled="!!memberForm.residentId">
            <el-option label="男" value="male" />
            <el-option label="女" value="female" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="memberForm.positionIds" multiple placeholder="请选择职务" style="width: 100%">
            <el-option v-for="p in positionList" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="memberForm.phone" placeholder="请输入联系电话" :disabled="!!memberForm.residentId" />
        </el-form-item>
        <el-form-item label="分管工作">
          <el-input v-model="memberForm.dividedWork" placeholder="请输入分管工作" />
        </el-form-item>
        <el-form-item label="入职时间">
          <el-date-picker v-model="memberForm.joinDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMember">确定</el-button>
      </template>
    </el-dialog>

    <!-- 选择村民弹窗 -->
    <el-dialog v-model="residentDialogVisible" title="选择村民" width="700px" destroy-on-close>
      <div class="filter-bar" style="margin-bottom: 16px">
        <el-form :inline="true" :model="residentQuery">
          <el-form-item label="身份证号">
            <el-input v-model="residentQuery.idCard" placeholder="身份证号" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="residentQuery.name" placeholder="姓名" clearable style="width: 120px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadResidents">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="residentList" stripe border size="small" max-height="300" @row-click="handleResidentSelect" highlight-current-row>
        <el-table-column type="index" label="序号" width="50" align="center"/>
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="idCard" label="身份证号" width="170" />
        <el-table-column prop="gender" label="性别" width="60">
          <template #default="{ row }">{{ row.gender === 'male' ? '男' : row.gender === 'female' ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="address" label="住址" min-width="150" show-overflow-tooltip />
      </el-table>
      <div class="pagination" style="margin-top: 12px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="residentQuery.pageNum"
          v-model:page-size="residentQuery.pageSize"
          :total="residentTotal"
          layout="total, prev, pager, next"
          small
          @current-change="loadResidents"
        />
      </div>
    </el-dialog>

    <!-- 任务表单弹窗 -->
    <el-dialog v-model="taskDialogVisible" :title="taskDialogTitle" width="500px" destroy-on-close>
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="100px">
        <el-form-item label="任务内容" prop="content">
          <el-input v-model="taskForm.content" type="textarea" :rows="3" placeholder="请输入任务内容" />
        </el-form-item>
        <el-form-item label="接收人" prop="assigneeId">
          <el-select v-model="taskForm.assigneeId" placeholder="请选择" style="width: 100%">
            <el-option v-for="m in memberList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成期限">
          <el-date-picker v-model="taskForm.deadline" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="taskForm.status" placeholder="请选择" style="width: 100%">
            <el-option label="待处理" value="pending" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成成效">
          <el-input v-model="taskForm.result" type="textarea" :rows="2" placeholder="请输入完成成效" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitTask">确定</el-button>
      </template>
    </el-dialog>

    <!-- 任务状态更新弹窗 -->
    <el-dialog v-model="statusDialogVisible" title="更新任务状态" width="400px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusTag(taskForm.status)">{{ getStatusName(taskForm.status) }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="newStatus">
          <el-select v-model="newStatus" placeholder="请选择" style="width: 100%">
            <el-option v-for="s in availableStatuses" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成成效">
          <el-input v-model="taskForm.result" type="textarea" :rows="2" placeholder="请输入完成成效" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStatus">确定</el-button>
      </template>
    </el-dialog>

    <!-- 会议表单弹窗 -->
    <el-dialog v-model="meetingDialogVisible" :title="meetingDialogTitle" width="600px" destroy-on-close>
      <el-form ref="meetingFormRef" :model="meetingForm" :rules="meetingRules" label-width="100px">
        <el-form-item label="会议时间" prop="meetingTime">
          <el-date-picker v-model="meetingForm.meetingTime" type="datetime" placeholder="选择日期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="会议地点">
          <el-input v-model="meetingForm.location" placeholder="请输入会议地点" />
        </el-form-item>
        <el-form-item label="参会人员">
          <el-input v-model="meetingForm.attendees" placeholder="请输入参会人员，多人用逗号分隔" />
        </el-form-item>
        <el-form-item label="会议内容">
          <el-input v-model="meetingForm.content" type="textarea" :rows="3" placeholder="请输入会议内容" />
        </el-form-item>
        <el-form-item label="决议事项">
          <el-input v-model="meetingForm.resolution" type="textarea" :rows="2" placeholder="请输入决议事项" />
        </el-form-item>
        <el-form-item label="落实情况">
          <el-input v-model="meetingForm.implementation" type="textarea" :rows="2" placeholder="请输入落实情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="meetingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMeeting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 决议转任务弹窗 -->
    <el-dialog v-model="convertDialogVisible" title="决议转任务" width="500px" destroy-on-close>
      <el-form ref="convertFormRef" :model="convertForm" :rules="convertRules" label-width="100px">
        <el-form-item label="决议事项">
          <el-input v-model="meetingForm.resolution" type="textarea" :rows="2" disabled />
        </el-form-item>
        <el-form-item label="任务内容" prop="content">
          <el-input v-model="convertForm.content" type="textarea" :rows="2" placeholder="请输入任务内容" />
        </el-form-item>
        <el-form-item label="接收人" prop="assigneeId">
          <el-select v-model="convertForm.assigneeId" placeholder="请选择" style="width: 100%">
            <el-option v-for="m in memberList" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成期限">
          <el-date-picker v-model="convertForm.deadline" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="convertDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitConvert">确定</el-button>
      </template>
    </el-dialog>

    <!-- 统计弹窗 -->
    <el-dialog v-model="statsDialogVisible" title="班子统计" width="400px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="班子总人数">{{ memberStats.total || 0 }}</el-descriptions-item>
        <el-descriptions-item label="书记人数">{{ memberStats.secretary || 0 }}</el-descriptions-item>
        <el-descriptions-item label="主任人数">{{ memberStats.director || 0 }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { twoCommitteeApi } from '@/request/twoCommittee'
import { taskApi } from '@/request/task'
import { meetingApi } from '@/request/meeting'
import { positionApi } from '@/request/position'
import { residentApi } from '@/request/resident'

const activeTab = ref('member')

// 成员管理
const memberLoading = ref(false)
const memberList = ref([])
const memberTotal = ref(0)
const memberQuery = reactive({ name: '', position: '', pageNum: 1, pageSize: 10 })

const memberDialogVisible = ref(false)
const memberDialogTitle = ref('新增成员')
const memberFormRef = ref()
const memberForm = reactive({ id: null, residentId: null, name: '', gender: '', positionIds: [], phone: '', dividedWork: '', joinDate: '' })
const memberRules = { name: [{ required: true, message: '请选择或输入姓名', trigger: 'change' }] }

const positionList = ref([])
const statsDialogVisible = ref(false)
const memberStats = ref({})

// 村民选择
const residentDialogVisible = ref(false)
const residentList = ref([])
const residentTotal = ref(0)
const residentQuery = reactive({ idCard: '', name: '', pageNum: 1, pageSize: 10 })

// 任务管理
const taskLoading = ref(false)
const taskList = ref([])
const taskTotal = ref(0)
const taskQuery = reactive({ assigneeId: null, status: '', pageNum: 1, pageSize: 10 })

const taskDialogVisible = ref(false)
const taskDialogTitle = ref('分配任务')
const taskFormRef = ref()
const taskForm = reactive({ id: null, content: '', assigneeId: null, deadline: '', status: 'pending', result: '' })
const taskRules = { content: [{ required: true, message: '请输入任务内容', trigger: 'blur' }], assigneeId: [{ required: true, message: '请选择接收人', trigger: 'change' }] }

const statusDialogVisible = ref(false)
const newStatus = ref('')
const availableStatuses = ref([])

// 会议管理
const meetingLoading = ref(false)
const meetingList = ref([])
const meetingTotal = ref(0)
const meetingQuery = reactive({ meetingTime: '', pageNum: 1, pageSize: 10 })

const meetingDialogVisible = ref(false)
const meetingDialogTitle = ref('新增会议')
const meetingFormRef = ref()
const meetingForm = reactive({ id: null, meetingTime: '', location: '', attendees: '', content: '', resolution: '', implementation: '' })
const meetingRules = { meetingTime: [{ required: true, message: '请选择会议时间', trigger: 'change' }] }

const convertDialogVisible = ref(false)
const convertFormRef = ref()
const convertForm = reactive({ content: '', assigneeId: null, deadline: '' })
const convertRules = { content: [{ required: true, message: '请输入任务内容', trigger: 'blur' }], assigneeId: [{ required: true, message: '请选择接收人', trigger: 'change' }] }

onMounted(() => { loadMembers(); loadPositions() })

// 职位管理方法
const loadPositions = async () => {
  try {
    const res = await positionApi.list()
    positionList.value = res || []
  } catch (e) { ElMessage.error(e.message || '加载职位失败') }
}

// 村民选择方法
const handleSelectResident = () => {
  residentQuery.idCard = ''
  residentQuery.name = ''
  residentQuery.pageNum = 1
  residentDialogVisible.value = true
  loadResidents()
}

const loadResidents = async () => {
  try {
    const res = await residentApi.list(residentQuery)
    residentList.value = res.list || []
    residentTotal.value = res.total || 0
  } catch (e) { ElMessage.error(e.message || '加载村民列表失败') }
}

const handleResidentSelect = (row) => {
  memberForm.residentId = row.id
  memberForm.name = row.name
  memberForm.gender = row.gender
  memberForm.phone = row.phone
  residentDialogVisible.value = false
}

// 成员管理方法
const loadMembers = async () => {
  memberLoading.value = true
  try {
    const res = await twoCommitteeApi.list(memberQuery)
    memberList.value = res.list || []
    memberTotal.value = res.total || 0
  } catch (e) { ElMessage.error(e.message || '查询失败') }
  finally { memberLoading.value = false }
}

const resetMemberQuery = () => { memberQuery.name = ''; memberQuery.position = ''; memberQuery.pageNum = 1; loadMembers() }

const handleAddMember = () => {
  memberDialogTitle.value = '新增成员'
  Object.assign(memberForm, { id: null, residentId: null, name: '', gender: '', positionIds: [], phone: '', dividedWork: '', joinDate: '' })
  memberDialogVisible.value = true
}

const handleEditMember = (row) => {
  memberDialogTitle.value = '编辑成员'
  const positionIds = row.positionIds ? row.positionIds.split(',').map(Number).filter(Boolean) : []
  Object.assign(memberForm, { ...row, positionIds })
  memberDialogVisible.value = true
}

const handleSubmitMember = async () => {
  try {
    await memberFormRef.value.validate()
    const positionNames = memberForm.positionIds.map(id => {
      const p = positionList.value.find(x => x.id === id)
      return p ? p.name : ''
    }).filter(Boolean)
    const submitData = { ...memberForm, positionNames }
    if (memberForm.id) { await twoCommitteeApi.update(submitData); ElMessage.success('更新成功') }
    else { await twoCommitteeApi.create(submitData); ElMessage.success('创建成功') }
    memberDialogVisible.value = false
    loadMembers()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '操作失败') }
}

const handleDeleteMember = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该成员吗？', '提示', { type: 'warning' })
    await twoCommitteeApi.delete(row.id)
    ElMessage.success('删除成功')
    loadMembers()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '删除失败') }
}

const handleMemberStatistics = async () => {
  try {
    const res = await twoCommitteeApi.statistics()
    memberStats.value = res || {}
    statsDialogVisible.value = true
  } catch (e) { ElMessage.error(e.message || '统计失败') }
}

const getMemberName = (id) => {
  const m = memberList.value.find(x => x.id === id)
  return m ? m.name : '-'
}

// 任务管理方法
const loadTasks = async () => {
  taskLoading.value = true
  try {
    const res = await taskApi.list(taskQuery)
    taskList.value = res.list || []
    taskTotal.value = res.total || 0
  } catch (e) { ElMessage.error(e.message || '查询失败') }
  finally { taskLoading.value = false }
}

const resetTaskQuery = () => { taskQuery.assigneeId = null; taskQuery.status = ''; taskQuery.pageNum = 1; loadTasks() }

const handleAddTask = () => {
  taskDialogTitle.value = '分配任务'
  Object.assign(taskForm, { id: null, content: '', assigneeId: null, deadline: '', status: 'pending', result: '' })
  taskDialogVisible.value = true
}

const handleEditTask = (row) => {
  taskDialogTitle.value = '编辑任务'
  Object.assign(taskForm, row)
  taskDialogVisible.value = true
}

const handleSubmitTask = async () => {
  try {
    await taskFormRef.value.validate()
    if (taskForm.id) { await taskApi.update(taskForm); ElMessage.success('更新成功') }
    else { await taskApi.create(taskForm); ElMessage.success('创建成功') }
    taskDialogVisible.value = false
    loadTasks()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '操作失败') }
}

const handleUpdateTaskStatus = (row) => {
  Object.assign(taskForm, row)
  newStatus.value = ''
  availableStatuses.value = getAvailableStatuses(row.status)
  statusDialogVisible.value = true
}

const getAvailableStatuses = (current) => {
  const all = [{ value: 'pending', label: '待处理' }, { value: 'ongoing', label: '进行中' }, { value: 'completed', label: '已完成' }]
  if ('pending' === current) return [all[1]]
  if ('ongoing' === current) return [all[2]]
  return []
}

const handleSubmitStatus = async () => {
  try {
    if (!newStatus.value) { ElMessage.warning('请选择新状态'); return }
    await taskApi.update({ id: taskForm.id, status: newStatus.value, result: taskForm.result })
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    loadTasks()
  } catch (e) { ElMessage.error(e.message || '操作失败') }
}

const handleDeleteTask = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该任务吗？', '提示', { type: 'warning' })
    await taskApi.delete(row.id)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '删除失败') }
}

// 会议管理方法
const loadMeetings = async () => {
  meetingLoading.value = true
  try {
    const res = await meetingApi.list(meetingQuery)
    meetingList.value = res.list || []
    meetingTotal.value = res.total || 0
  } catch (e) { ElMessage.error(e.message || '查询失败') }
  finally { meetingLoading.value = false }
}

const resetMeetingQuery = () => { meetingQuery.meetingTime = ''; meetingQuery.pageNum = 1; loadMeetings() }

const handleAddMeeting = () => {
  meetingDialogTitle.value = '新增会议'
  Object.assign(meetingForm, { id: null, meetingTime: '', location: '', attendees: '', content: '', resolution: '', implementation: '' })
  meetingDialogVisible.value = true
}

const handleEditMeeting = (row) => {
  meetingDialogTitle.value = '编辑会议'
  Object.assign(meetingForm, row)
  meetingDialogVisible.value = true
}

const handleSubmitMeeting = async () => {
  try {
    await meetingFormRef.value.validate()
    if (meetingForm.id) { await meetingApi.update(meetingForm); ElMessage.success('更新成功') }
    else { await meetingApi.create(meetingForm); ElMessage.success('创建成功') }
    meetingDialogVisible.value = false
    loadMeetings()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '操作失败') }
}

const handleDeleteMeeting = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该会议记录吗？', '提示', { type: 'warning' })
    await meetingApi.delete(row.id)
    ElMessage.success('删除成功')
    loadMeetings()
  } catch (e) { if (e !== 'cancel') ElMessage.error(e.message || '删除失败') }
}

const handleConvertToTask = (row) => {
  Object.assign(meetingForm, row)
  Object.assign(convertForm, { content: '', assigneeId: null, deadline: '' })
  convertDialogVisible.value = true
}

const handleSubmitConvert = async () => {
  try {
    await convertFormRef.value.validate()
    await meetingApi.convertToTask({ ...convertForm, assigner: '系统' })
    ElMessage.success('任务创建成功')
    convertDialogVisible.value = false
    activeTab.value = 'task'
    loadTasks()
  } catch (e) { ElMessage.error(e.message || '操作失败') }
}

// 状态转换
const getStatusName = (status) => { const map = { pending: '待处理', ongoing: '进行中', completed: '已完成' }; return map[status] || status }
const getStatusTag = (status) => { const map = { pending: 'info', ongoing: 'warning', completed: 'success' }; return map[status] || '' }
</script>

<style scoped>
.two-committee { padding: 20px; }
.filter-bar { background: #fff; padding: 20px; border-radius: 4px; margin-bottom: 16px; }
.query-form { display: flex; flex-wrap: wrap; gap: 8px; }
.toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
