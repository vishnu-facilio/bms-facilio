<template>
  <div class="d-flex flex-col-reverse group-table-page">
    <div v-for="(currentGroup, index) in ScriptLogsList" :key="index">
      <div v-if="!$validation.isEmpty(groupedScriptLogs[currentGroup])">
        <div>
          <div class="fc-setup-group-header header-lavender">
            <div class="fc-black-14 bold text-left flex-middle">
              {{ getFormattedDate(currentGroup) }}
            </div>
          </div>
          <div class="mB20 fc-group-table">
            <el-table
              :data="groupedScriptLogs[currentGroup]"
              :cell-style="{ padding: '12px 20px' }"
              style="width: 100%"
              height="auto"
              class="fc-setup-table"
              :fit="true"
              ref="scriptLogTable"
            >
              <el-table-column
                :label="$t('setup.scriptlogs.workflow')"
                prop="workflowRuleName"
                width="90"
              >
                <template v-slot="currentLog">
                  <div
                    class="truncate-text"
                    v-tippy
                    :title="
                      $getProperty(currentLog.row, 'workflowRuleName', 'N/A')
                    "
                  >
                    {{
                      $getProperty(currentLog.row, 'workflowRuleName', 'N/A')
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('setup.customButton.module_name')"
                prop="recordModuleName"
                width="90"
              >
                <template v-slot="currentLog">
                  <div class="capitalized">
                    {{
                      $getProperty(currentLog.row, 'recordModuleName', 'N/A')
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('setup.scriptlogs.record_id')"
                prop="recordId"
                width="90"
              >
                <template v-slot="currentLog">
                  <div>
                    {{ $getProperty(currentLog.row, 'recordId', 'N/A') }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('setup.scriptlogs.created_time')"
                prop="createdTime"
                width="45"
                class-name="pR0 pL10"
              >
                <template v-slot="currentLog">
                  <div>
                    {{ getFormatTime(currentLog.row) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('setup.scriptlogs.log_type')"
                prop="logType"
                width="90"
                class-name="pR0"
              >
                <template v-slot="currentLog">
                  <div>
                    {{ getLogType(currentLog.row) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('setup.scriptlogs.status')"
                prop="status"
                width="75"
                class-name="pR0"
              >
                <template v-slot="currentLog">
                  <el-tag
                    :type="getTagType(currentLog.row)"
                    style="width: 70px;text-align: center"
                  >
                    {{ getStatusType(currentLog.row) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column width="90">
                <template v-slot="currentLog">
                  <div
                    v-if="checklog(currentLog.row)"
                    @click="showDialog(currentLog.row)"
                    class="tag-style"
                    type="text"
                  >
                    {{ getTagname(currentLog.row) }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <ScriptDialog
              v-if="centerDialogVisible"
              :centerDialogVisible="centerDialogVisible"
              :rowData="currentRowData"
              @handleClose="handleClose"
            ></ScriptDialog>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import ScriptDialog from './ScriptLogDialog'
const logTypes = {
  1: 'Formula',
  2: 'Scheduler',
  3: 'Module Rule',
  4: 'StateFlow Rule',
  5: 'Workflow Rule',
  6: 'Q and A Rule',
  7: 'Custom Button Rule',
  8: 'Approval State FlowRule',
  9: 'Approval State Transition Rule',
  10: 'Approval Rule',
}

const statusTypes = {
  1: 'Success',
  2: 'Failed',
  3: 'Syntax Error',
}

const tagType = {
  1: 'success',
  2: 'danger',
  3: 'warning',
}
export default {
  name: 'ScriptLogs',
  props: ['ScriptLogsList', 'groupedScriptLogs'],
  components: { ScriptDialog },
  data() {
    return {
      centerDialogVisible: false,
      currentRowData: {},
    }
  },
  methods: {
    getFormattedDate(timeStamp) {
      return moment(timeStamp)
        .tz(this.$timezone)
        .format('dddd, DD MMM YYYY')
    },
    getFormatTime(log) {
      let { createdTime } = log || {}
      return !isEmpty(createdTime)
        ? this.$options.filters.formatDate(createdTime, false, true)
        : 'N/A'
    },
    getLogType(log) {
      let { logType } = log || {}
      return !isEmpty(logType) ? logTypes[logType] : 'N/A'
    },
    getStatusType(log) {
      let { status } = log || {}
      return !isEmpty(status) ? statusTypes[status] : 'N/A'
    },
    getTagType(log) {
      let { status } = log || {}
      return !isEmpty(status) ? tagType[status] : 'N/A'
    },
    showDialog(row) {
      this.centerDialogVisible = true
      this.currentRowData = row
    },
    checklog(row) {
      let { logAvailable, exceptionAvailable } = row || {}
      return logAvailable || exceptionAvailable
    },
    getTagname(row) {
      let { exceptionAvailable } = row || {}
      if (exceptionAvailable) {
        return `${this.$t('common.wo_report.view')} ${this.$t(
          'setup.scriptlogs.exception'
        )}`
      } else {
        return `${this.$t('common.wo_report.view')} ${this.$t(
          'setup.scriptlogs.log_value'
        )}`
      }
    },
    handleClose() {
      this.centerDialogVisible = false
      this.currentRowData = {}
    },
  },
}
</script>
<style lang="scss" scoped>
.group-table-page {
  .fc-setup-group-header {
    top: 0;
  }
  .tag-style {
    color: #46a2bf;
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
<style lang="scss">
.group-table-page {
  .header-row {
    overflow-y: scroll;
  }
  .el-table {
    overflow: visible;
    .el-table__header-wrapper {
      position: sticky;
      top: 51px;
      z-index: 50;
      .el-table__cell {
        padding: 15px 20px;
      }
    }
  }
  .el-table__fixed {
    overflow: visible;
    width: 100%;
    z-index: 100;
    .el-table__fixed-header-wrapper {
      position: sticky;
      top: 51px;
      z-index: 150;
      .el-table__cell {
        padding: 15px 20px;
      }
    }
    .el-table__fixed-header-wrapper,
    .el-table__fixed-body-wrapper {
      overflow-x: hidden;
      overflow-y: hidden;
      width: 100%;
    }
  }
  .fc-group-table {
    .pL15 {
      padding-left: 15px !important;
    }
    .el-table__body-wrapper {
      height: auto !important;
    }
    .el-table__expanded-cell {
      background: #f5f7fa;
    }
  }
}
</style>
