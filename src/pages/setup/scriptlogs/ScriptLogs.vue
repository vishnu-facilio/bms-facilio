<template>
  <div>
    <div class="fc-table-expand-row fc-script-log-page">
      <setup-header>
        <template #heading>
          {{ $t('setup.scriptlogs.scriptlogs') }}
        </template>
        <template #description>
          {{ $t('setup.scriptlogs.scriptlog_desc') }}
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
                  default-first-option
                  placeholder="Select time period"
                  class="mR20 fc-input-full-border2"
                  @change="reloadFilteredData()"
                  clearable
                  filterable
                >
                  <el-option
                    v-for="(filter, index) in selectFilter"
                    :key="index"
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
                <div class="script-log-date-input">
                  <f-date-picker
                    v-model="dateTimePicker"
                    type="datetimerange"
                    class="fc-input-full-border-select1"
                    :start-placeholder="$t('kpi.historical.start_date')"
                    :end-placeholder="$t('kpi.historical.end_date')"
                    @change="pickerChange"
                  >
                  </f-date-picker>
                </div>
              </div>
            </div>
          </div>
        </template>
        <template #searchAndPagination>
          <div class="flex-middle">
            <template v-if="!$validation.isEmpty(logCount)">
              <div class="mR15 mT3">
                <pagination
                  :total="logCount"
                  :perPage="logPerPage"
                  class="nowrap pT5"
                  ref="f-page"
                  :pageNo="logPage"
                  @onPageChanged="setPage"
                ></pagination>
              </div>
              <div class="seperator"></div>
            </template>
            <div class="mR15 p5 pB10">
              <AdvancedSearch
                :key="`${moduleName}-search`"
                :moduleName="moduleName"
                :moduleDisplayName="moduleDisplayName"
                :hideQuery="true"
                :onSave="applyScriptLoggerFilters"
                :filterList="scriptlogFilterList"
              >
              </AdvancedSearch>
            </div>
            <div class="seperator"></div>
            <div class="mR15">
              <div
                class="fc-portal-filter-border"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                :content="$t('common._common.refresh')"
                @click="reloadScriptLogs"
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
          :filterList="scriptlogFilterList"
          @updateFilters="applyScriptLoggerFilters"
          @resetFilters="resetFilters"
        ></FTags>
      </div>
      <div class="p15 pB0 pT0 fc-setup-table-layout-scroll">
        <setup-loader v-if="isLoading">
          <template #setupLoading>
            <spinner :show="isLoading" size="80"></spinner>
          </template>
        </setup-loader>
        <setup-empty v-else-if="$validation.isEmpty(scriptLogsList)">
          <template #emptyImage>
            <inline-svg
              src="svgs/copy2"
              iconClass="icon icon-sm-md"
            ></inline-svg>
          </template>
          <template #emptyHeading>
            {{ $t('setup.scriptlogs.no_script_logs') }}
          </template>
          <template #emptyDescription> </template>
        </setup-empty>
        <ScriptLogsList
          v-else
          :ScriptLogsList="days"
          :groupedScriptLogs="groupedScriptLogs"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import Pagination from 'pages/setup/components/SetupPagination'
import ScriptLogsList from './ScriptLogList'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import moment from 'moment-timezone'
import FTags from 'newapp/components/search/FTags'
import FDatePicker from 'pages/assets/overview/FDatePicker'
export default {
  name: 'ScriptLogs',
  data() {
    return {
      scriptlogFilterList: [],
      logCount: null,
      logPage: 1,
      logPerPage: 50,
      isLoading: false,
      scriptLogsList: [],
      groupedScriptLogs: {},
      dayMap: {},
      days: [],
      dateFilterModel: 'Today',
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
      dateTimePicker: [],
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
    moduleName() {
      return 'workflowLog'
    },
    moduleDisplayName() {
      return 'Script Log'
    },
  },
  components: {
    SetupHeader,
    Pagination,
    SetupLoader,
    SetupEmpty,
    ScriptLogsList,
    AdvancedSearch,
    FDatePicker,
    FTags,
  },
  title() {
    return this.$t('setup.scriptlogs.scriptlogs')
  },
  created() {
    this.fetchScriptLogsList()
  },
  methods: {
    applyScriptLoggerFilters({ filters }) {
      this.scriptlogFilterList = filters
      this.fetchScriptLogsList()
    },
    resetFilters() {
      this.scriptlogFilterList = []
      this.fetchScriptLogsList()
    },
    setPage(page) {
      this.logPage = page
      this.fetchScriptLogsList()
    },
    reloadFilteredData() {
      let { dateFilterModel } = this
      if (dateFilterModel !== 'Custom') {
        this.dateTimePicker = []
      }

      this.logPage = 1
      this.fetchScriptLogsList()
    },
    pickerChange(value) {
      if (!isEmpty(value) && isArray(value)) {
        this.datePicker = value
        this.dateTimePicker = value.map(val => this.getTimeInSystemZone(val))
        this.reloadFilteredData()
      } else {
        this.dateTimePicker = []
      }
    },
    getTimeInSystemZone(value) {
      let { $timezone } = this
      return moment.tz(value, $timezone).format('YYYY-MM-DD HH:mm')
    },
    reloadScriptLogs() {
      this.logPage = 1
      this.scriptlogFilterList = {}
      this.fetchScriptLogsList()
    },
    deserialize(scriptLogsList) {
      this.groupedScriptLogs = {}
      this.dayMap = {}
      this.days = []
      let { dayMap, groupedScriptLogs, $timezone } = this
      scriptLogsList.forEach(scriptLog => {
        this.$set(
          scriptLog,
          'day',
          moment(scriptLog.createdTime)
            .tz($timezone)
            .format('DDMMYYYY')
        )
        let { day, createdTime } = scriptLog || {}
        let dayMapHasDay = dayMap.hasOwnProperty(day)
        if (!dayMapHasDay) {
          this.$set(dayMap, day, createdTime)
        }
        let currentGroupKey = dayMap[day]
        let currentGroup = groupedScriptLogs[currentGroupKey] || []
        currentGroup.push(scriptLog)
        this.$set(groupedScriptLogs, currentGroupKey, currentGroup)
      })
      this.days = Object.values(dayMap)
      let { days = [] } = this
      days.sort()
    },
    async fetchScriptLogsList() {
      this.isLoading = true
      let {
        logPage,
        logPerPage,
        scriptlogFilterList,
        moduleName,
        dateFilterModel,
        dateFilterMap,
      } = this
      let params = {
        page: logPage,
        perPage: logPerPage,
        withCount: true,
        orderType: 'desc',
        orderBy: 'CREATED_TIME',
      }
      let dateFilter = {}
      if (!isEmpty(dateFilterModel)) {
        if (dateFilterModel === 'Custom') {
          let { datePicker } = this
          if (!isEmpty(datePicker)) {
            dateFilter = {
              createdTime: {
                operatorId: 20,
                value: datePicker.map(date => date.toString()),
              },
            }
          }
        } else {
          let { [dateFilterModel]: operator } = dateFilterMap
          dateFilter = {
            createdTime: { operatorId: operator },
          }
        }
        params['filters'] = JSON.stringify({
          ...scriptlogFilterList,
          ...dateFilter,
        })
      }
      if (!isEmpty(scriptlogFilterList)) {
        params['filters'] = JSON.stringify({
          ...scriptlogFilterList,
          ...dateFilter,
        })
      }
      let { list, meta = {}, error } = await API.fetchAll(moduleName, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.logCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.scriptLogsList = list
        let { scriptLogsList } = this || {}
        this.deserialize(scriptLogsList)
      }
      this.isLoading = false
    },
  },
}
</script>

<style lang="scss" scoped>
.fc-script-log-page {
  .seperator {
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
<style lang="scss">
.script-log-date-input {
  .el-input__inner {
    width: 90% !important;
  }
}
</style>
