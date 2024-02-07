<template>
  <div>
    <div class="pB12 pT12 pL25 pR25 border-bottom1px flex-middle">
      <div class="log-breadcrumb-inner pointer" @click="agentHomeRoute">
        {{ $t('agent.logs.home') }}
      </div>
      <div class="log-breadcrumb-inner pL10 pR10">
        <i class="el-icon-arrow-right f14 fwBold"></i>
      </div>
      <div class="log-breadcrumb-inner pointer" @click="agentDataLogsRoute">
        {{ $t('agent.logs.data_logs') }}
      </div>
      <div class="log-breadcrumb-inner pL10 pR10">
        <i class="el-icon-arrow-right f14 fwBold"></i>
      </div>
      <div class="log-breadcrumbBold-active">#{{ parentId }}</div>
    </div>
    <div class="pL25 pT15 pR25 pB15 border-bottom1px bold f16">
      {{ $t('agent.logs.summary') }}
    </div>
    <div class="log-detail-container">
      <div class="bold f16">{{ $t('agent.logs.details') }}</div>
      <div class="flex-center-row-space pT27">
        <div class="width25">
          <div class="f14 bold">{{ $t('agent.logs.time_stamp') }}</div>
          <div class="details-sub-text">{{ timeStamp }}</div>
        </div>

        <div class="width25">
          <div class="f14 bold">{{ $t('agent.logs.payload') }}</div>
          <div
            class="log-details-button pointer"
            @click="openPayloadDialog(dataLog)"
          >
            {{ $t('agent.logs.view_payload') }}
          </div>
        </div>

        <div class="width25">
          <div class="f14 bold">{{ $t('agent.logs.agent_name') }}</div>
          <el-tooltip
            effect="dark"
            :content="this.agentName"
            placement="bottom-start"
            :open-delay="300"
            :disabled="$validation.isEmpty(this.agentName)"
          >
            <div class="details-sub-text">{{ agentName }}</div>
          </el-tooltip>
        </div>

        <div class="width25">
          <div class="f14 bold">{{ $t('agent.logs.cont_name') }}</div>
          <el-tooltip
            effect="dark"
            :content="this.controllerName"
            placement="bottom-start"
            :open-delay="300"
            :disabled="$validation.isEmpty(this.controllerName)"
          >
            <div class="details-sub-text">{{ controllerName }}</div>
          </el-tooltip>
        </div>
      </div>
    </div>
    <div v-if="isLoading">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(payloadData) && !isLoading"
      class="data_log-empty-state"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('agent.empty.no_dataLog') }}
      </div>
    </div>
    <div v-else class="logs-table-shadow group-table-page">
      <div class="log-table-header fc-setup-group-header flex-center-row-space">
        {{ $t('agent.logs.readings') }}

        <pagination
          :total="totalRecords"
          :perPage="perPage"
          ref="f-page"
        ></pagination>
      </div>
      <div class="fc-group-table">
        <el-table
          :data="payloadData"
          :cell-style="{ padding: '12px 20px' }"
          style="width: 100%"
          :fit="true"
          height="auto"
        >
          <el-table-column class="flex-middle" :label="$t('agent.logs.point')">
            <template v-slot="dataLogRecord">
              <el-tooltip
                effect="dark"
                :content="$getProperty(dataLogRecord.row, 'point')"
                placement="top-start"
                :open-delay="300"
                :disabled="
                  $validation.isEmpty($getProperty(dataLogRecord.row, 'point'))
                "
              >
                <div class="truncate-text">
                  {{ $getProperty(dataLogRecord.row, 'point', '---') }}
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column class="flex-middle" :label="$t('agent.logs.asset')">
            <template v-slot="dataLogRecord">
              <el-tooltip
                effect="dark"
                :content="$getProperty(dataLogRecord.row, 'asset.name')"
                placement="top-start"
                :open-delay="600"
                :disabled="
                  $validation.isEmpty(
                    $getProperty(dataLogRecord.row, 'asset.name')
                  )
                "
              >
                <div class="truncate-text text-left">
                  {{ $getProperty(dataLogRecord.row, 'asset.name', '---') }}
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column
            class="flex-middle"
            :label="$t('agent.logs.reading')"
          >
            <template v-slot="dataLogRecord">
              <el-tooltip
                effect="dark"
                :content="getReading(dataLogRecord)"
                placement="top-start"
                :open-delay="600"
                :disabled="getReading(dataLogRecord) == '---'"
                ><div
                  class="truncate-text"
                  :ref="`reading-column-${dataLogRecord.row.id}`"
                >
                  {{ getReading(dataLogRecord) }}
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column :label="$t('agent.logs.value')">
            <template v-slot="dataLogRecord">
              <div>
                {{ $getProperty(dataLogRecord.row, 'value', '---') }}
              </div></template
            >
          </el-table-column>
          <el-table-column :label="$t('agent.logs.status')">
            <template v-slot="dataLogRecord">
              <div :class="[getStatusClass(dataLogRecord)]">
                {{ getStatus(dataLogRecord) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="exception" v-if="showExceptionColumn">
            <template v-slot="dataLogRecord">
              <div v-if="isException(dataLogRecord)">
                <el-button
                  type="text"
                  class="log-button-hover"
                  @click="openExceptionDialog(dataLogRecord)"
                  >{{ $t('agent.logs.view_ex') }}</el-button
                >
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <el-dialog
      v-if="payloadVisible"
      :title="$t('agent.logs.payload')"
      :visible.sync="payloadVisible"
      width="30%"
      :append-to-body="true"
      :before-close="() => (payloadVisible = false)"
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
import { API } from '@facilio/api'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/components/list/FPagination'

const MESSAGE_STATUS = {
  1: { label: 'Failure', class: 'log-failure-status' },
  2: { label: 'Success', class: 'log-success-status' },
}

export default {
  name: 'LogsTableSummary',
  props: ['dataLog'],
  components: {
    Pagination,
  },
  mounted() {
    this.loadPayloadData()
    this.deserialize()
  },
  computed: {
    parentId() {
      let { $route: route } = this
      let { params } = route
      let { parentId } = params
      if (!isEmpty(parentId)) {
        return parentId
      }
      return -1
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (newVal != oldVal) {
        this.loadPayloadData()
      }
    },
  },
  data() {
    return {
      isLoading: false,
      payloadData: [],
      payLoad: '',
      agentName: '',
      controllerName: '',
      timeStamp: '',
      payloadVisible: false,
      totalRecords: 0,
      perPage: 50,
      moduleName: 'agentDataProcessingLogger',
      activeDatalog: null,
      exceptionVisible: false,
      readings: {},
      showExceptionColumn: true,
      agentId: null,
      id: null,
    }
  },
  methods: {
    async loadPayloadData() {
      this.isLoading = true
      let { page, perPage, moduleName, parentId } = this || {}
      let filters = {}
      let parentIdFilter = {
        parentId: { operatorId: 9, value: [`${parentId}`] },
      }
      filters = JSON.stringify({ ...filters, ...parentIdFilter })
      let params = {
        page,
        perPage,
        withCount: true,
        orderType: 'asc',
        orderBy: 'POINT',
        moduleName: moduleName,
        filters,
      }
      let url = '/v3/modules/data/list'
      let { error, data, meta } = await API.get(url, params)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.totalRecords = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
        let { agentDataProcessingLogger: payloadData, readings } = data || {}
        this.readings = readings
        this.payloadData = payloadData
        this.isLoading = false
      }
    },
    deserialize() {
      let { dataLog } = this || {}
      let { startTime, agent, controller, messageStatus, id } = dataLog || {}
      let timeStamp = moment(startTime)
        .tz(this.$timezone)
        .format('DD MMM YYYY hh:mmA')
      this.timeStamp = timeStamp
      this.id = id

      let { displayName: agentName } = agent || {}
      this.agentName = agentName

      let { id: agentId } = agent || {}
      this.agentId = agentId

      let { name: controllerName } = controller || {}
      this.controllerName = controllerName

      if (messageStatus == 1 || messageStatus == 2) {
        this.showExceptionColumn = true
      } else {
        this.showExceptionColumn = false
      }
    },

    getStatus(dataLogRecord) {
      let { row } = dataLogRecord || {}
      let { status } = row || {}
      if (!isEmpty(MESSAGE_STATUS[status])) {
        return MESSAGE_STATUS[status]?.label
      } else {
        return ''
      }
    },
    getStatusClass(dataLogRecord) {
      let { row } = dataLogRecord || {}
      let { status } = row || {}
      if (!isEmpty(MESSAGE_STATUS[status])) {
        return MESSAGE_STATUS[status]?.class
      } else {
        return ''
      }
    },
    agentHomeRoute() {
      this.$router.replace({ name: 'overview' })
    },
    agentDataLogsRoute() {
      this.$router.replace({
        name: 'logs',
        query: {
          agentId: this.agentId,
        },
      })
    },
    isException(dataLog) {
      let { row: dataLogRecord } = dataLog || {}
      let { status } = dataLogRecord || {}
      return status == 1
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
    getException(dataLog) {
      let { errorStackTrace } = dataLog || {}
      return errorStackTrace
    },
    getReading(dataLogRecord) {
      let { row } = dataLogRecord || {}
      let { readings } = this || {}
      let { readingId } = row
      let reading = readings[readingId] || {}
      return reading.name || '---'
    },
    async openPayloadDialog() {
      let { payLoad } = this || {}
      if (isEmpty(payLoad)) {
        let { id } = this || {}
        let params = {
          includeParentFilter: true,
          moduleName: 'agentDataLogger',
          id: id,
        }

        let url = '/v3/modules/data/summary'
        let { error, data, meta } = await API.get(url, params)
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
  },
}
</script>
<style lang="scss" scoped>
.log-detail-container {
  margin: 15px 25px;
  box-shadow: 0 0 8px 0 rgba(0, 0, 0, 0.1);
  padding: 18px 20px;
}
.log-table-header {
  padding: 15px 20px;
  background-color: #f7faff;
  font-weight: 500;
  font-size: 16px;
}
.details-sub-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 80%;
  font-size: 14px;
  color: #737376;
  padding-top: 8px;
}
.group-table-page {
  .fc-setup-group-header {
    top: 0;
    padding: 15px 20px;
  }
  .fc-group-table {
    .truncate-text {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 80%;
      padding-top: 4px;
      padding-bottom: 3px;
      span {
        padding: 0px;
      }
    }
  }
}

.log-failure-status {
  color: #d12806;
}
.log-success-status {
  color: #25965b;
}
.logs-table-shadow {
  margin: 15px 25px;
  box-shadow: 0 0 8px 0 rgba(0, 0, 0, 0.1);
  max-height: calc(100vh - 330px);
  overflow: scroll;
  min-height: fit-content;
}
.log-breadcrumb-inner {
  color: #8ca1ad;
  font-size: 13px;
  font-weight: 500;
  &:hover {
    color: #46a2bf;
    text-decoration: underline;
  }
}
.log-breadcrumbBold-active {
  font-size: 13px;
  color: #324056;
  font-weight: 500;
}
.log-button-hover {
  padding-top: 0px;
  padding-bottom: 0px;
  font-size: 14px;
  color: #0088f5;
  font-weight: normal;
  &:hover {
    text-decoration: underline;
  }
}
.log-details-button {
  padding-top: 8px;
  font-size: 14px;
  color: #0088f5;
  font-weight: normal;
  &:hover {
    text-decoration: underline;
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
    .el-table__body td {
      font-size: 14px;
    }
    .el-table__body-wrapper {
      height: auto !important;
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
.data_log-empty-state {
  height: calc(100vh - 300px) !important;
  background: #ffffff;
  width: calc(100% - 21px);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  margin: 10px !important;
}
</style>
