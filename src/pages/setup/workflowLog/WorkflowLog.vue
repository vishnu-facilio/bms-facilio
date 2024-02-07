<template>
  <div>
    <setup-header>
      <template #heading>
        {{ $t('setup.workflowLog.title') }}
      </template>
      <template #description>
        {{ $t('setup.workflowLog.desc') }}
      </template>
    </setup-header>
    <!-- page list start-->
    <spinner v-if="loading" :show="loading"></spinner>
    <div v-else class="m15 d-flex flex-col-reverse">
      <div>{{ selectedTime }}</div>
      <div>
        <el-table
          :data="workflowLogList"
          empty-text="No Workflow Log Available"
          class="width100 fc-setup-table-th-borderTop fc-setup-table fc-setup-table-p0"
          height="calc(100vh - 200px)"
          :header-cell-style="{ background: '#f3f1fc', height: '60px' }"
        >
          <el-table-column
            prop="recordModuleId"
            label="MODULE ID"
            width="200px"
          >
          </el-table-column>
          <el-table-column prop="recordId" label="RECORD ID" width="400px">
          </el-table-column>
          <el-table-column prop="" label="TIME" width="400px">
            <template v-slot="workflowLog">
              {{ getFormatTime(workflowLog.row.createdTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="logType" label="TYPE" width="400px">
          </el-table-column>
          <el-table-column prop="status" label="STATUS" width="400px">
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import SetupHeader from 'pages/setup/components/SetupHeader'
import moment from 'moment-timezone'

export default {
  // mixins: [FetchViewsMixin],
  data() {
    return {
      loading: true,
      workflowLogList: [],
    }
  },
  components: {
    SetupHeader,
  },

  created() {
    this.fetchWorkflowLog()
    this.loadSystemApps()
    this.loadusers()
  },
  computed: {
    selectedTime() {
      return 'Empty'
    },
  },
  // watch: {
  //   page(newVal, oldVal) {
  //     if (oldVal != newVal) {
  //       this.fetchAuditLog()
  //     }
  //   },
  // },
  methods: {
    async fetchWorkflowLog() {
      this.loading = true
      let params = {
        page: 1,
        perPage: 20,
        withCount: true,
        moduleName: 'crafts',
      }

      let { list, error } = await API.fetchAll(`workflowLog`, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.workflowLogList = list || []
      }
      this.loading = false
    },
    getFormatTime(time) {
      return moment(time)
        .tz(this.$timezone)
        .format('hh:mm A')
    },
  },
}
</script>
<style lang="scss">
.fc-log-search {
  height: 40px;
  line-height: 40px;
  padding-right: 30px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
  font-size: 16px;
  padding-left: 15px !important;
  font-weight: 500;
  transition: 0.4s all;
}
.fc-search-close {
  position: absolute;
  right: 10px;
  top: 10px;
  background: #fff;
  cursor: pointer;
}
.filter-border {
  margin-left: 15px;
  padding-top: 5px;
  cursor: pointer;
  display: flex;
  position: relative;
  align-items: center;
  justify-content: center;
  border-radius: 2px;
  position: relative;
  .dot-active-pink {
    position: absolute;
    top: 2px;
    right: -4px;
  }
}
.filterActive {
  transition: 0.4s all;
  .fc-fill-path-grey path {
    fill: #39b2c2;
  }
}
.fc-audit-log-page {
  .el-table__body-wrapper {
    height: auto !important;
  }
  .el-table__expanded-cell {
    background: #f5f7fa;
  }
  .fc-filter-animation {
    border-bottom: 1px solid #e6e9f2;
  }
  .fc-setup-actions-header {
    padding-bottom: 10px;
  }
  .fc-extra-filter-block {
    border-top: 1px solid rgb(230 233 242 / 60%);
    padding-top: 10px;
    width: 100%;
  }
  .fc-setup-table-header-hide {
    tbody tr {
      &:hover {
        background: #f1f8fa !important;
      }
    }
  }
}
.fc-audit-link {
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
}
</style>
