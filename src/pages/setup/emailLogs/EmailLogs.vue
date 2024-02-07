<template>
  <div>
    <div class="fc-email-log-page">
      <setup-header>
        <template #heading>
          {{ $t('emailLogs.email_logs') }}
        </template>
        <template #description>
          {{ $t('emailLogs.description') }}
        </template>
        <template #filter>
          <div class="flex-middle">
            <div class="flex-middle">
              <div>
                <div class="fc-black-13 text-left bold pB5">
                  {{ $t('setup.setupLabel.date_filter') }}
                </div>
                <el-select
                  v-model="dateFilterModel"
                  placeholder="Select time period"
                  class="mR20 fc-input-full-border2"
                  @change="reloadFilteredData()"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="(filter, f) in selectFilter"
                    :key="f"
                    :label="filter.label"
                    :value="filter.value"
                  >
                  </el-option>
                </el-select>
              </div>
              <div v-if="dateFilterModel === 'Custom'">
                <div class="fc-black-13 text-left bold pB5">
                  {{ $t('setup.setupLabel.custom') }}
                </div>
                <el-date-picker
                  v-model="datePicker"
                  type="daterange"
                  class="fc-input-full-border2 width250px"
                  placeholder="Select date and time"
                  range-separator="-"
                  start-placeholder="Start date"
                  end-placeholder="End date"
                  @change="reloadFilteredData()"
                  value-format="timestamp"
                >
                </el-date-picker>
              </div>
            </div>
          </div>
        </template>
        <template #searchAndPagination>
          <div class="flex-middle">
            <template v-if="totalRecords">
              <div class="mR15 mT3">
                <pagination
                  :total="totalRecords"
                  :perPage="recordsPerPage"
                  :pageNo="currentPage"
                  @onPageChanged="setPage"
                ></pagination>
              </div>
              <div class="vl"></div>
            </template>
            <div class="mR15 p5 pB10">
              <AdvancedSearch
                :key="`${moduleName}-search`"
                :moduleName="moduleName"
                :moduleDisplayName="moduleDisplayName"
                :hideQuery="true"
                :onSave="applyMailLoggerFilters"
                :filterList="mailLoggerFilterList"
              >
              </AdvancedSearch>
            </div>
            <div class="vl"></div>
            <div class="mR15">
              <div
                class="fc-portal-filter-border"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                :content="$t('common._common.refresh')"
                @click="reloadEmailLogs"
              >
                <i class="el-icon-refresh fwBold f16"></i>
              </div>
            </div>
          </div>
        </template>
      </setup-header>
      <div class="mB15">
        <FTags
          :key="`ftags-list-${moduleName}`"
          :hideQuery="true"
          :hideSaveView="true"
          :filterList="mailLoggerFilterList"
          @updateFilters="applyMailLoggerFilters"
          @resetFilters="resetFilters"
        ></FTags>
      </div>
      <div class="p15 pB0 pT0 fc-setup-table-layout-scroll">
        <setup-loader v-if="isLoading">
          <template #setupLoading>
            <spinner :show="isLoading" size="80"></spinner>
          </template>
        </setup-loader>
        <setup-empty v-else-if="$validation.isEmpty(emailLogsList)">
          <template #emptyImage>
            <inline-svg
              src="svgs/copy2"
              iconClass="icon icon-sm-md"
            ></inline-svg>
          </template>
          <template #emptyHeading>
            {{ $t('emailLogs.no_email_logs') }}
          </template>
          <template #emptyDescription> </template>
        </setup-empty>
        <TableGroups
          v-else
          :tableGroups="days"
          :tableGroupData="groupedEmailLogs"
          :sourceTypeMap="sourceTypeMap"
          :displayNameMap="displayNameMap"
        ></TableGroups>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import Pagination from 'src/components/list/FPagination'
import TableGroups from 'pages/setup/emailLogs/TableGroups'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import moment from 'moment-timezone'
import { mapState } from 'vuex'
import FTags from 'newapp/components/search/FTags'
export default {
  name: 'EmailLogs',
  data() {
    return {
      mailLoggerFilterList: {},
      showSearchInput: false,
      totalRecords: 0,
      currentPage: 1,
      recordsPerPage: 50,
      isLoading: false,
      emailLogsList: [],
      groupedEmailLogs: {},
      dayMap: {},
      days: [],
      sourceTypeMap: {},
      displayNameMap: {},
      dateFilterModel: [],
      dateFilterMap: {
        'This Month': 28,
        Today: 22,
        Yesterday: 25,
        'Last Month': 27,
        'Last Week': 30,
        'This Week': 31,
        'Till Now': 72,
        'Till Yesterday': 26,
      },
      datePicker: [],
      selectFilter: [
        {
          label: 'Today',
          value: 'Today',
        },
        {
          label: 'Till Now',
          value: 'Till Now',
        },
        {
          label: 'Yesterday',
          value: 'Yesterday',
        },
        {
          label: 'Till Yesterday',
          value: 'Till Yesterday',
        },
        {
          label: 'This Week',
          value: 'This Week',
        },
        {
          label: 'Last Week',
          value: 'Last Week',
        },
        {
          label: 'This Month',
          value: 'This Month',
        },
        {
          label: 'Last Month',
          value: 'Last Month',
        },
        {
          label: 'Custom',
          value: 'Custom',
        },
      ],
    }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    moduleName() {
      return 'outgoingMailLogger'
    },
    moduleDisplayName() {
      return 'Outgoing Mail Logger'
    },
  },
  watch: {
    metaInfo: {
      handler(newVal) {
        if (!isEmpty(newVal)) {
          this.setSourceTypeMap()
          this.setDisplayNameMap()
        }
      },
      immediate: true,
    },
  },
  components: {
    SetupHeader,
    Pagination,
    SetupLoader,
    SetupEmpty,
    TableGroups,
    AdvancedSearch,
    FTags,
  },
  created() {
    this.loadMetaAndData()
  },
  methods: {
    async loadMetaAndData() {
      this.isLoading = true
      let { moduleName } = this
      await this.$store.dispatch('view/loadModuleMeta', moduleName)
      await this.fetchEmailLogsList()
      this.isLoading = false
    },

    applyMailLoggerFilters({ filters }) {
      this.mailLoggerFilterList = filters
      this.fetchEmailLogsList()
    },
    resetFilters() {
      this.mailLoggerFilterList = []
      this.fetchEmailLogsList()
    },
    setPage(page) {
      this.currentPage = page
      this.fetchEmailLogsList()
    },
    reloadFilteredData() {
      let { dateFilterModel } = this
      if (dateFilterModel !== 'Custom') {
        this.datePicker = []
      }
      this.totalRecords = 0
      this.currentPage = 1
      this.fetchEmailLogsList()
    },
    reloadEmailLogs() {
      this.totalRecords = 0
      this.currentPage = 1
      this.mailLoggerFilterList = {}
      this.fetchEmailLogsList()
    },
    setSourceTypeMap() {
      let { metaInfo } = this || {}
      let { fields } = metaInfo || {}
      if (!isEmpty(fields)) {
        let sourceTypeField = fields.filter(field => {
          let { name } = field || {}
          return name === 'sourceType'
        })
        let { enumMap } = sourceTypeField[0] || {}
        this.sourceTypeMap = enumMap
      }
    },
    setDisplayNameMap() {
      let { metaInfo } = this || {}
      let { fields } = metaInfo || {}
      if (!isEmpty(fields)) {
        fields.forEach(field => {
          let { name: fieldName, displayName: fieldDisplayName } = field || {}
          this.$set(this.displayNameMap, fieldName, fieldDisplayName)
        })
      }
    },
    deserialize(emailLogsList) {
      this.groupedEmailLogs = {}
      this.dayMap = {}
      this.days = []
      let { dayMap, groupedEmailLogs } = this
      emailLogsList.forEach(emailLog => {
        let { to: toMails } = emailLog
        let toList = toMails?.split(',') || []
        this.$set(
          emailLog,
          'day',
          moment(emailLog.sysCreatedTime)
            .tz(this.$timezone)
            .format('DDMMYYYY')
        )
        this.$set(emailLog, 'toList', toList)
        let { day, sysCreatedTime } = emailLog || {}
        let dayMapHasDay = dayMap.hasOwnProperty(day)
        if (!dayMapHasDay) {
          this.$set(dayMap, day, sysCreatedTime)
        }
        let currentGroupKey = dayMap[day]
        let currentGroup = groupedEmailLogs[currentGroupKey] || []
        currentGroup.push(emailLog)
        this.$set(groupedEmailLogs, currentGroupKey, currentGroup)
      })
      this.days = Object.values(dayMap)
      let { days = [] } = this
      days.sort()
    },
    async fetchEmailLogsList() {
      this.isLoading = true
      let {
        currentPage,
        recordsPerPage,
        mailLoggerFilterList,
        moduleName,
        dateFilterModel,
        dateFilterMap,
      } = this
      let params = {
        page: currentPage,
        perPage: recordsPerPage,
        withCount: true,
        orderType: 'desc',
        orderBy: 'SYS_CREATED_TIME',
      }
      let dateFilter = {}
      if (!isEmpty(dateFilterModel)) {
        if (dateFilterModel === 'Custom') {
          let { datePicker } = this
          if (!isEmpty(datePicker)) {
            dateFilter = {
              sysCreatedTime: {
                operatorId: 20,
                value: this.datePicker.map(a => a.toString()),
              },
            }
          }
        } else {
          let { [dateFilterModel]: operator } = dateFilterMap
          dateFilter = {
            sysCreatedTime: { operatorId: operator },
          }
        }
        params['filters'] = JSON.stringify({
          ...mailLoggerFilterList,
          ...dateFilter,
        })
      }
      if (!isEmpty(mailLoggerFilterList)) {
        params['filters'] = JSON.stringify({
          ...mailLoggerFilterList,
          ...dateFilter,
        })
      }
      let { list, meta = {}, error } = await API.fetchAll(moduleName, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.totalRecords = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
        this.emailLogsList = list
        let { emailLogsList } = this || {}
        this.deserialize(emailLogsList)
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss" scoped>
.fc-email-log-page {
  .vl {
    border-left: 2px solid rgb(208, 211, 208);
    height: 19px;
    width: 25px;
  }
  .pB15 {
    padding-bottom: 15px !important;
  }
  .fc-setup-empty,
  .fc-setup-loader {
    height: calc(100vh - 300px);
  }
}
</style>
