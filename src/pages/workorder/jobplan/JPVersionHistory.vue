<template>
  <el-dialog
    :visible="true"
    :title="$t('jobplan.version_history')"
    width="55%"
    :append-to-body="true"
    :before-close="closeDialog"
    class="fc-dialog-center-container jp-version-dialog"
  >
    <div class="version-history">
      <div v-if="isLoading" class="text-center width100 pT50 mT50">
        <spinner :show="isLoading" size="60"></spinner>
      </div>
      <el-table
        v-else
        ref="version-history-table"
        :data="recordVersions"
        height="100%"
        style="width: 100%"
        :header-cell-style="headerStyle"
        :empty-text="$t('jobplan.no_version_available')"
        :row-class-name="activeRowClass"
      >
        <el-table-column :label="$t('jobplan.version')" prop="jobPlanVersion">
        </el-table-column>
        <el-table-column
          :label="$t('jobplan.publish_state')"
          prop="jpStatus"
          width="200"
        >
          <template v-slot="template">
            {{ getPublishStatus(template.row) }}
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('jobplan.created_by')"
          prop="sysCreatedBy"
          width="150"
        >
          <template v-slot="template">
            <el-tooltip
              effect="dark"
              :content="getCreatedBy(template.row)"
              :open-delay="600"
              placement="top"
              :disabled="canDisableTooltip(template.row)"
            >
              <div class="truncate-text">
                {{ getCreatedBy(template.row) }}
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('jobplan.created_time')"
          prop="sysCreatedTime"
          width="200"
        >
          <template v-slot="template">
            {{ getCreatedTime(template.row) }}
          </template>
        </el-table-column>
        <el-table-column label="" prop="" class="visibility-visible-actions">
          <template v-slot="template">
            <div
              class="visibility-hide-actions version-view d-flex"
              @click="redirectToJobplan(template.row)"
              v-if="canShowView(template.row)"
            >
              <fc-icon
                group="navigation"
                name="open-window"
                color="#0074d1"
                size="15"
                class="mR5 mT4"
              ></fc-icon>
              {{ $t('jobplan.view') }}
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { PUBLISHED_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { formatDate } from 'src/util/filters.js'
import router from 'src/router'
import Spinner from '@/Spinner'

export default {
  name: 'JPVersionHistory',
  props: ['jobPlanId'],
  data: () => ({
    recordVersions: [],
    isLoading: false,
  }),
  components: { Spinner },
  created() {
    this.loadHistory()
  },
  computed: {
    headerStyle() {
      let style = { 'background-color': '#f7faff' }
      return style
    },
    moduleName() {
      return 'jobplan'
    },
  },
  methods: {
    async loadHistory() {
      this.isLoading = true
      let { jobPlanId } = this
      let { error, data } = await API.get('v3/jobPlan/versionHistory', {
        jobPlanId,
      })

      if (!error) {
        this.recordVersions = data.result || []
      } else {
        this.$message.error(error.message || 'Error Occured')
      }
      this.isLoading = false
    },
    getPublishStatus(record) {
      let { jpStatus } = record || {}
      return this.$getProperty(PUBLISHED_STATUS, `${jpStatus}`, '---')
    },
    getCreatedTime(record) {
      let { sysCreatedTime } = record || {}
      return !isEmpty(sysCreatedTime) ? formatDate(sysCreatedTime) : '---'
    },
    getCreatedBy(record) {
      let { sysCreatedBy } = record || {}
      return this.$getProperty(sysCreatedBy, `name`, '---')
    },
    redirectToJobplan(record) {
      let { group, jobPlanVersion: version } = record || {}
      let groupId = this.$getProperty(group, 'id', null)
      let { moduleName } = this
      let route = {}

      version = `v${version}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        route = {
          name,
          params: {
            viewname: 'all',
            id: groupId,
          },
          query: { version },
        }
      } else {
        route = {
          name: 'jobPlanSummary',
          params: { moduleName, viewname: 'all', id: groupId },
          query: { version },
        }
      }
      let { href } = router.resolve(route) || {}

      if (!isEmpty(href)) {
        window.open(href, '_blank', 'noopener,noreferrer')
      }
    },
    closeDialog() {
      this.$emit('closeDialog')
    },
    activeRowClass({ row }) {
      let { jobPlanId } = this
      let { id } = row || {}

      return jobPlanId === id ? 'current-record-version' : ''
    },
    canDisableTooltip(record) {
      let createdBy = this.getCreatedBy(record)
      return isEmpty(createdBy) || createdBy === '---'
    },
    canShowView(record) {
      let { jobPlanId } = this
      let currentRecordId = this.$getProperty(record, 'id', null)

      return jobPlanId !== currentRecordId
    },
  },
}
</script>
<style lang="scss">
.jp-version-dialog {
  padding-bottom: 65px !important;
  .el-dialog__header {
    padding: 15px 20px 5px;
    height: 55px;
  }
  .el-dialog__headerbtn {
    top: 15px !important;
  }
  .el-dialog__title {
    text-transform: capitalize;
    color: #324056;
    font-size: 16px !important;
    font-weight: bold;
    letter-spacing: 0.5px;
  }
  .el-dialog__body {
    padding: 0 !important;
  }
  .el-table {
    td {
      padding-left: 20px !important;
    }
    th > .cell {
      text-transform: capitalize;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.5px;
      color: #324056;
    }
    tr {
      &:hover > td.el-table__cell {
        background-color: #f0f8ff;
      }
    }
  }
  .current-record-version {
    background-color: #f0f8ff;
  }
  .version-view {
    color: #0074d1;
    &:hover {
      text-decoration: underline;
    }
  }
  .version-history {
    height: 300px;
    overflow: scroll;
  }
}
</style>
