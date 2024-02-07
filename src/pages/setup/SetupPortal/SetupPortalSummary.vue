<template>
  <div>
    <SetupSummaryHeader>
      <template #summaryRightSide>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
          <el-breadcrumb-item>Portal</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
      <template #summaryLeftSide> </template>
      <div class="flex-middle">
        <div>
          Occupant Portal
        </div>
        <div>
          <el-button>
            Add user
          </el-button>
        </div>
      </div>
    </SetupSummaryHeader>
  </div>
</template>
<script>
import SetupSummaryHeader from 'pages/setup/components/SetupSummaryHeader'
export default {
  components: {
    SetupSummaryHeader,
  },
}
</script>
