<template>
  <div class="summary-tabs">
    <setup-loader v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </setup-loader>

    <setup-empty v-else-if="$validation.isEmpty(tableData)">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('emailLogs.no_records_found') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>
    <div v-else>
      <div class="summary-table">
        <el-table
          :data="tableData"
          style="width: 100%"
          :cell-style="{ padding: '12px 30px' }"
          class="width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
          height="calc(100vh - 280px)"
        >
          <el-table-column
            type="index"
            :label="$t('emailLogs.email_dialog.s_no')"
            width="35"
            class-name="pL15"
          >
          </el-table-column>
          <el-table-column
            prop="time"
            :label="$getProperty(displayNameMap, 'sysModifiedTime')"
            width="75"
            class-name="pL0"
          >
            <template v-slot="tableSlotName">
              <div>
                {{
                  getFormatter(
                    $getProperty(tableSlotName.row, 'sysModifiedTime')
                  ) || '---'
                }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="email"
            :label="$getProperty(displayNameMap, 'recipient')"
            width="130"
            class-name="pL0"
          >
            <template v-slot="tableSlotName">
              <div>
                {{ $getProperty(tableSlotName.row, 'recipient') || '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            :label="$getProperty(displayNameMap, 'name')"
            class-name="pL0"
            width="75"
          >
            <template v-slot="tableSlotName">
              <div>
                {{ $getProperty(tableSlotName.row, 'name') || '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-if="!isBouncedListTable"
            prop="status"
            :label="$getProperty(displayNameMap, 'status')"
            width="75"
            class-name="pL0"
          >
            <template v-slot="tableSlotName">
              <div v-if="tableSlotName.row.status === 4">
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="$getProperty(tableSlotName, 'row.bounceReason')"
                  placement="top"
                >
                  <span class="m0 p5 pL0">
                    {{ getStatus(tableSlotName.row) }}
                  </span>
                </el-tooltip>
              </div>
              <div v-else class="p5 pL0">
                {{ getStatus(tableSlotName.row) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-if="isBouncedListTable"
            prop="bounceType"
            :label="$getProperty(displayNameMap, 'bounceType')"
            width="75"
            class-name="pL0"
          >
            <template v-slot="tableSlotName">
              <div>
                {{ getBounceType(tableSlotName.row) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-if="isBouncedListTable"
            prop="bounceReason"
            :label="$getProperty(displayNameMap, 'bounceReason')"
            width="75"
            class-name="pL0"
          >
            <template v-slot="tableSlotName">
              <div>
                {{ $getProperty(tableSlotName.row, 'bounceReason') || '---' }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import { getTimeInOrgFormat } from 'src/util/helpers'
import { mapState } from 'vuex'

const statusMap = {
  1: 'emailLogs.email_dialog.in_progress',
  2: 'emailLogs.email_dialog.sent',
  3: 'emailLogs.email_dialog.delivered',
  4: 'emailLogs.email_dialog.bounced',
}
const bounceTypeMap = {
  1: 'emailLogs.email_dialog.hard_bounce',
  2: 'emailLogs.email_dialog.soft_bounce',
  3: 'emailLogs.email_dialog.unknown_bounce',
}
export default {
  data() {
    return {
      isLoading: false,
      tableData: [],
      isBouncedListTable: false,
      totalRecords: 0,
      displayNameMap: {},
    }
  },
  props: ['loggerId', 'status', 'recordsPerPage', 'currentPage', 'searchText'],
  components: { SetupEmpty, SetupLoader },
  async created() {
    this.isLoading = true
    let { moduleName } = this
    await this.$store.dispatch('view/loadModuleMeta', moduleName)
    this.fetchTableData()
  },
  computed: {
    moduleName() {
      return 'outgoingRecipientLogger'
    },
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
  },
  watch: {
    metaInfo: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.setDisplayNameMap()
        }
      },
      immediate: true,
    },
    searchText(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.fetchTableData()
      }
    },
    currentPage(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.fetchTableData()
      }
    },
  },
  methods: {
    setDisplayNameMap() {
      let { metaInfo } = this
      let { fields } = metaInfo || {}
      if (!isEmpty(fields)) {
        fields.forEach(field => {
          let { name: fieldName, displayName: fieldDisplayName } = field || {}
          this.$set(this.displayNameMap, fieldName, fieldDisplayName)
        })
      }
    },
    getFormatter(timeStamp) {
      return `${moment(timeStamp)
        .tz(this.$timezone)
        .format('DD MMM YYYY')}, ${getTimeInOrgFormat(timeStamp)}`
    },
    getStatus(row) {
      let { status } = row || {}
      let statusKey = statusMap[status] || null
      return statusKey ? this.$t(statusKey) : '---'
    },
    getBounceType(row) {
      let { bounceType } = row || {}
      let bounceTypeKey = bounceTypeMap[bounceType] || null
      return bounceTypeKey ? this.$t(bounceTypeKey) : '---'
    },
    async fetchTableData() {
      this.isLoading = true
      let {
        status,
        loggerId,
        currentPage,
        recordsPerPage,
        searchText,
        moduleName,
      } = this
      if (status === 'bounced') {
        this.isBouncedListTable = true
      }
      let params = {
        loggerId: loggerId,
        status: status,
        withCount: true,
        page: currentPage,
        perPage: recordsPerPage,
      }
      if (!isEmpty(searchText)) {
        params['filters'] = JSON.stringify({
          recipient: { operatorId: 5, value: [searchText] },
        })
      }
      let { list, meta = {}, error } = await API.fetchAll(moduleName, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.tableData = list
        this.totalRecords = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
        this.$emit('paginationFetch', this.totalRecords)
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss">
.summary-tabs {
  .fc-setup-empty,
  .fc-setup-loader {
    height: calc(100vh - 280px);
  }
}
</style>