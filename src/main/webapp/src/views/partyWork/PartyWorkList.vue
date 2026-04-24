<template>
  <div class="party-work-list">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="党员档案" name="member">
        <PartyMemberList />
      </el-tab-pane>
      <el-tab-pane label="党务活动" name="activity">
        <PartyActivityList />
      </el-tab-pane>
      <el-tab-pane label="党费收缴" name="dues">
        <PartyDuesList />
      </el-tab-pane>
      <el-tab-pane label="统计" name="statistics">
        <PartyStatistics />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PartyMemberList from './PartyMemberList.vue'
import PartyActivityList from './PartyActivityList.vue'
import PartyDuesList from './PartyDuesList.vue'
import PartyStatistics from './PartyStatistics.vue'

const activeTab = ref('member')
</script>

<style scoped>
.party-work-list {
  padding: 20px;
}
</style>