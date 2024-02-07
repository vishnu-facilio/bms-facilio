<template>
  <div class="d-flex flex-col group-table-page">
    <div
      v-for="(id, index) in dataLogListKeys"
      :key="index"
      :class="getContainerClass(index)"
    >
      <div>
        <div class="fc-setup-group-header ">
          <div
            class="fc-black-14 bold text-left flex-middle"
            style="font-size: 16px;"
          >
            {{ getFormatter(dataLogList[id]) }}
          </div>
        </div>
        <div class="fc-group-table">
          <el-table
            :data="dataLogList[id]"
            :cell-style="{ padding: '12px 20px' }"
            style="width: 100%"
            :fit="true"
            height="auto"
            @row-click="handleRowClick"
          >
            <el-table-column :label="$t('agent.logs.start_time')">
              <template v-slot="dataLog">
                <div>{{ getTimeFormatter(dataLog, 'startTime') }}</div>
              </template>
            </el-table-column>
            <el-table-column :label="$t('agent.logs.processed_time')">
              <template v-slot="dataLog">
                <div>{{ getTimeFormatter(dataLog, 'endTime') }}</div>
              </template>
            </el-table-column>
            <el-table-column :label="$t('agent.logs.controller')">
              <template v-slot="dataLog">
                <el-tooltip
                  effect="dark"
                  :content="$getProperty(dataLog, 'row.controller.name', '---')"
                  placement="bottom-start"
                  :open-delay="300"
                  :disabled="
                    $validation.isEmpty(
                      $getProperty(dataLog, 'row.controller.name')
                    )
                  "
                >
                  <div class="controller-name-container">
                    {{ $getProperty(dataLog, 'row.controller.name', '---') }}
                  </div>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column :label="$t('agent.logs.cov')">
              <template v-slot="dataLog">
                <div>
                  {{ isCov(dataLog) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column :label="$t('agent.logs.payload')" prop="payload">
              <template v-slot="dataLog">
                <el-button
                  type="text"
                  class="log-button-hover"
                  @click="openPayloadDialog(dataLog)"
                  >{{ $t('agent.logs.view_payload') }}</el-button
                >
              </template>
            </el-table-column>
            <el-table-column :label="$t('agent.logs.status')">
              <template v-slot="dataLog">
                <div :class="[getStatusClass(dataLog)]">
                  {{ getStatus(dataLog) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="exception">
              <template v-slot="dataLog">
                <div v-if="isException(dataLog)">
                  <el-button
                    type="text"
                    class="log-button-hover"
                    @click="openExceptionDialog(dataLog)"
                    >{{ $t('agent.logs.view_ex') }}</el-button
                  >
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <el-dialog
      v-if="payloadVisible"
      :title="$t('agent.logs.payload')"
      :visible.sync="payloadVisible"
      width="30%"
      :append-to-body="true"
      :before-close="closePayloadDialog"
      class="log-dialog"
      ><pre id="json">{{ payLoad }}</pre>
    </el-dialog>
    <el-dialog
      v-if="exceptionVisible"
      :title="$t('agent.logs.exception')"
      :visible.sync="exceptionVisible"
      width="30%"
      :append-to-body="true"
      :before-close="closeExceptionDialog"
      class="log-exception-dialog"
      >{{ getException(activeDatalog) }}</el-dialog
    >
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import { getTimeInOrgFormat } from 'src/util/helpers'
import { API } from '@facilio/api'

const MESSAGE_STATUS = {
  1: { label: 'Failure', class: 'log-failure-status' },
  2: { label: 'Partial Success', class: 'log-partial-success-status' },
  3: { label: 'Success', class: 'log-success-status' },
}

export default {
  name: 'LogsTable',
  props: ['dataLogList'],
  data() {
    return {
      payloadVisible: false,
      exceptionVisible: false,
      activeDatalog: null,
      payLoad: null,
    }
  },
  computed: {
    dataLogListKeys() {
      let { dataLogList } = this || {}
      let keys = Object.keys(dataLogList) || []
      keys.sort().reverse()
      return keys
    },
  },
  methods: {
    getContainerClass(index) {
      let { dataLogListKeys } = this || {}
      let { length } = dataLogListKeys || []
      let isLastItem = index === length - 1
      return `log-group-table-container ${isLastItem ? '' : 'mB20'}`
    },
    openExceptionDialog(dataLog) {
      let { row } = dataLog || {}
      this.activeDatalog = row
      this.exceptionVisible = true
    },
    closeExceptionDialog(dataLog) {
      this.activeDatalog = null
      this.exceptionVisible = false
    },
    async openPayloadDialog(log) {
      let { payLoad } = this || {}
      if (isEmpty(payLoad)) {
        let { row } = log || {}
        let { id } = row || {}
        let params = {
          includeParentFilter: true,
          moduleName: 'agentDataLogger',
          id: id,
        }

        let url = '/v3/modules/data/summary'
        let { error, data } = await API.get(url, params)
        if (!isEmpty(error)) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { agentDataLogger } = data || {}
          let { payload } = agentDataLogger || {}
          let payLoad = JSON.stringify(JSON.parse(payload), null, 2)
          this.payLoad = payLoad
          this.payloadVisible = true
        }
      } else {
        this.payloadVisible = true
      }
    },
    closePayloadDialog() {
      this.activeDatalog = null
      this.payloadVisible = false
    },
    getFormatter(dataLogItem) {
      let [initItem] = dataLogItem || []
      let { startTime } = initItem || {}

      return moment(startTime)
        .tz(this.$timezone)
        .format('dddd, DD MMM YYYY')
    },
    getTimeFormatter(dataLog, timeLabel) {
      let { row: dataLogRecord } = dataLog || {}
      let { [timeLabel]: currTime } = dataLogRecord || {}

      return getTimeInOrgFormat(currTime)
    },
    getStatus(dataLog) {
      let { row } = dataLog || {}
      let { messageStatus } = row || {}
      if (!isEmpty(MESSAGE_STATUS[messageStatus])) {
        return MESSAGE_STATUS[messageStatus]?.label
      } else {
        return ''
      }
    },
    getStatusClass(dataLog) {
      let { row } = dataLog || {}
      let { messageStatus } = row || {}
      if (!isEmpty(MESSAGE_STATUS[messageStatus])) {
        return MESSAGE_STATUS[messageStatus]?.class
      } else {
        return ''
      }
    },
    isException(dataLog) {
      let { row: dataLogRecord } = dataLog || {}
      let { messageStatus } = dataLogRecord || {}
      return messageStatus == 1 || messageStatus == 2
    },
    getException(dataLog) {
      let { errorStackTrace } = dataLog || {}
      return errorStackTrace
    },
    handleRowClick(dataLogRecord, column) {
      let { property } = column || {}
      if (property !== 'payload' && property !== 'exception') {
        this.$emit('summaryPageReRoute', dataLogRecord)
      }
    },
    isCov(dataLogRecord) {
      let { row } = dataLogRecord || {}
      let { publishType } = row || {}
      return publishType == 3 ? 'True' : 'False'
    },
  },
}
</script>
<style lang="scss" scoped>
.controller-name-container {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 95%;
}

pre {
  white-space: pre-wrap;
}
.log-group-table-container {
  box-shadow: 0 0 8px 0 rgba(0, 0, 0, 0.1);
}
.log-button-hover {
  padding-top: 0px;
  padding-bottom: 0px;
  color: #0088f5;
  font-weight: normal;
  &:hover {
    text-decoration: underline;
  }
}
.group-table-page {
  .fc-setup-group-header {
    top: 0;
    padding: 15px 20px;
    background-color: #f7faff;
  }
}
.log-failure-status {
  color: #d12806;
}
.log-partial-success-status {
  color: #ffb638;
}
.log-success-status {
  color: #25965b;
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
    .el-table__body td {
      font-size: 14px;
    }
    .el-table__expanded-cell {
      background: #f5f7fa;
    }
  }
}
.log-dialog {
  .el-dialog__body {
    padding: 1px 20px;
    border-top: solid 1px #eee;
  }
}
.log-exception-dialog {
  .el-dialog__body {
    padding: 1em 20px;
    border-top: solid 1px #eee;
    word-break: break-word;
  }
}
</style>
