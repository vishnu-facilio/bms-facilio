<template>
  <div class="logs-main-container">
    <template v-if="$validation.isEmpty(parentId)">
      <div class="logs-header">{{ $t('agent.logs.logs') }}</div>
      <el-tabs v-model="activeTab" class="logs-tab-body">
        <el-tab-pane :label="$t('agent.logs.data_logs')" name="dataLogs">
          <div class="log-filter-container">
            <div class="flex-middle width850px">
              <div>
                <div class="fc-pink f12 pB5 bold text-left">
                  {{ $t('agent.logs.time') }}
                </div>
                <el-select
                  v-model="dateFilterModel"
                  :placeholder="$t('agent.logs.select_time')"
                  class="mR20 fc-input-full-border2"
                  clearable
                  filterable
                  @change="reloadFilteredData()"
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
                <div class="fc-pink f12 text-left bold pB5">
                  {{ $t('setup.setupLabel.custom') }}
                </div>
                <el-date-picker
                  v-model="datePicker"
                  type="daterange"
                  class="fc-input-full-border2 width250px mR20"
                  :placeholder="$t('agent.logs.select_date')"
                  range-separator="-"
                  :start-placeholder="$t('agent.logs.start_date')"
                  :end-placeholder="$t('agent.logs.end_date')"
                  @change="reloadFilteredData()"
                  value-format="timestamp"
                >
                </el-date-picker>
              </div>
              <SelectAgent @onAgentFilter="updateAgentId"></SelectAgent>
              <div
                v-if="
                  !$validation.isEmpty(selectedAgentId) &&
                    selectedAgentId != null
                "
                class="mL20"
              >
                <div class="fc-pink f12 pB5 bold text-left">
                  {{ $t('agent.logs.controller') }}
                </div>
                <FLookupFieldWrapper
                  v-model="selectedControllerIds"
                  :key="selectedAgentId"
                  :field="{
                    lookupModule: {
                      name: controllerModuleName,
                    },
                    multiple: true,
                  }"
                  :filterConstruction="constructAgentFilter"
                  :hideLookupIcon="false"
                  :disabled="false"
                  class="width250px"
                  @recordSelected="updateControllerIds"
                ></FLookupFieldWrapper>
              </div>
            </div>
            <div class="flex-middle mT20">
              <div class="mR15 mT3">
                <pagination
                  :total="totalRecords"
                  :perPage="perPage"
                  ref="f-page"
                ></pagination>
              </div>
              <div class="vl"></div>
              <div class="mR15 p5 pB10">
                <el-tooltip
                  effect="dark"
                  :content="$t('common._common.search')"
                  placement="top"
                  :open-delay="300"
                >
                  <AdvancedSearch
                    :key="`${moduleName}-search`"
                    :moduleName="moduleName"
                    :moduleDisplayName="moduleDisplayName"
                    :hideQuery="true"
                    :onSave="applyDataLogFilters"
                    :filterList="dataLogFilterList"
                  ></AdvancedSearch>
                </el-tooltip>
              </div>
              <div class="vl"></div>
              <div class="mR15">
                <div
                  v-tippy="{
                    arrow: true,
                    arrowType: 'round',
                    animation: 'fade',
                  }"
                  :content="$t('common._common.refresh')"
                  @click="reloadDataLogs"
                >
                  <i class="el-icon-refresh fwBold f16 pointer"></i>
                </div>
              </div>
            </div>
          </div>
          <div class="mB15">
            <FTags
              :key="`ftags-list-${moduleName}`"
              :hideQuery="true"
              :hideSaveView="true"
              :filterList="dataLogFilterList"
              @updateFilters="applyDataLogFilters"
              @resetFilters="resetFilters"
            ></FTags>
          </div>
          <div v-if="isLoading">
            <spinner :show="isLoading" size="80"></spinner>
          </div>
          <div
            v-else-if="$validation.isEmpty(dataLogList) && !isLoading"
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
          <div v-else class="log-table-container">
            <LogsTable
              @summaryPageReRoute="summaryPageReRoute"
              :dataLogList="dataLogList"
            ></LogsTable>
          </div>
        </el-tab-pane>
      </el-tabs>
    </template>
    <router-view
      v-else-if="!$validation.isEmpty(currentDataLog)"
      :dataLog="currentDataLog"
    ></router-view>
  </div>
</template>

<script>
import SelectAgent from 'src/agent/components/SelectAgent.vue'
import { API } from '@facilio/api'
import SetupLoader from 'pages/setup/components/SetupLoader'
import LogsTable from 'src/agent/logs/LogsTable'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Pagination from 'src/components/list/FPagination'
import FTags from 'newapp/components/search/FTags'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

const SELECT_FILTER = [
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
    label: 'Custom',
    value: 'Custom',
  },
]
export default {
  name: 'DataLogs',
  data() {
    return {
      activeTab: 'dataLogs',
      isLoading: false,
      selectFilter: SELECT_FILTER,
      dataLogList: [],
      currentDataLog: {},
      selectedAgentId: null,
      dateFilterModel: null,
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
      totalRecords: 0,
      perPage: 50,
      moduleName: 'agentDataLogger',
      moduleDisplayName: 'Agent Data Logger',
      dataLogFilterList: {},
      selectedControllerIds: [],
      controllerModuleName: 'controller',
    }
  },
  components: {
    SelectAgent,
    SetupLoader,
    LogsTable,
    AdvancedSearch,
    Pagination,
    FTags,
    FLookupFieldWrapper,
  },
  mounted() {
    this.init()
  },
  watch: {
    page(newVal, oldVal) {
      if (newVal != oldVal) {
        this.loadDataLogsList()
      }
    },
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
  methods: {
    async init() {
      if (!isEmpty(this.parentId)) {
        await this.loadDataLogsList()
        this.setCurrentDataLog()
      }
    },
    setCurrentDataLog() {
      let { parentId, dataLogList } = this || {}
      Object.entries(dataLogList).forEach(([key, value]) => {
        value.forEach(val => {
          if (val.id === parseInt(parentId)) this.currentDataLog = val
        })
      })
    },
    async loadDataLogsList() {
      this.isLoading = true
      let { page, perPage, moduleName } = this || {}
      let filters = this.constructFilters()

      if (!isEmpty(filters)) {
        filters = JSON.stringify(filters)
      }

      let params = {
        includeParentFilter: true,
        page,
        perPage,
        moduleName: moduleName,
        fetchOnlyViewGroupColumn: true,
        viewname: moduleName,
        withCount: true,
        orderType: 'desc',
        orderBy: 'START_TIME',
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
        this.deserialize(data)
      }
      this.isLoading = false
    },
    constructFilters() {
      let {
        selectedAgentId,
        dateFilterModel,
        dateFilterMap,
        dataLogFilterList,
        selectedControllerIds,
      } = this || {}
      let filters = {}

      let agentFilter = {}
      if (!isEmpty(selectedAgentId)) {
        agentFilter = {
          agent: { operatorId: 9, value: [`${selectedAgentId}`] },
        }
        filters = { ...filters, ...agentFilter }
      }
      //controller filter
      let controllerFilter = {}
      if (!isEmpty(selectedControllerIds)) {
        let value = selectedControllerIds.map(e => `${e}`)
        controllerFilter = {
          controller: {
            operatorId: 36,
            value,
          },
        }
        filters = { ...filters, ...controllerFilter }
      }
      //date filter
      let dateFilter = {}
      if (!isEmpty(dateFilterModel)) {
        if (dateFilterModel === 'Custom') {
          let { datePicker } = this
          if (!isEmpty(datePicker)) {
            dateFilter = {
              startTime: {
                operatorId: 20,
                value: this.datePicker.map(a => a.toString()),
              },
            }
          }
        } else {
          let { [dateFilterModel]: operator } = dateFilterMap
          dateFilter = {
            startTime: { operatorId: operator },
          }
        }
        if (!isEmpty(dateFilter)) {
          filters = { ...filters, ...dateFilter }
        }
      }

      //adding advanced search filters
      if (!isEmpty(dataLogFilterList)) {
        filters = { ...filters, ...dataLogFilterList }
      }

      return filters
    },
    deserialize(data) {
      let { agentDataLogger } = data || {}
      let loggerGrpList = {}
      agentDataLogger.forEach(dataLog => {
        let { startTime } = dataLog || {}
        let day = new Date(startTime)
        day = Math.round(day.getTime() / (1000 * 60 * 60 * 24))

        //returns an object if that key has a value else it'll return empty object so have to push the value or add new value for key
        let loggerGroupItem = loggerGrpList[day]
        if (isEmpty(loggerGroupItem)) {
          loggerGrpList[day] = [dataLog]
        } else {
          loggerGrpList[day].push(dataLog)
        }
      })
      this.dataLogList = loggerGrpList
    },
    summaryPageReRoute(dataLogRecord) {
      let { id: parentId } = dataLogRecord || {}
      if (!isEmpty(parentId)) {
        this.$router.push({
          name: 'logsTableSummary',
          params: {
            parentId,
          },
        })
      }
      this.currentDataLog = dataLogRecord
    },
    updateAgentId(selectedAgent) {
      this.selectedAgentId = selectedAgent.id
      this.loadDataLogsList()
    },
    reloadFilteredData() {
      let { dateFilterModel } = this
      if (dateFilterModel !== 'Custom') {
        this.datePicker = []
      }
      this.loadDataLogsList()
    },
    applyDataLogFilters({ filters }) {
      this.dataLogFilterList = filters
      this.loadDataLogsList()
    },
    resetFilters() {
      this.dataLogFilterList = {}
      this.loadDataLogsList()
    },
    reloadDataLogs() {
      this.dateFilterModel = null
      this.totalRecords = 0
      this.dataLogFilterList = {}
      this.loadDataLogsList()
    },
    constructAgentFilter() {
      let { selectedAgentId } = this || {}
      return { agentId: { operatorId: 9, value: [`${selectedAgentId}`] } }
    },
    updateControllerIds(controllers) {
      this.selectedControllerIds = controllers.map(
        controller => controller.value
      )
      this.loadDataLogsList()
    },
  },
}
</script>
<style lang="scss" scoped>
.width850px {
  width: 850px;
}
.logs-main-container {
  background: #fff;
  width: calc(100% - 250px);
  position: relative;
}
.logs-header {
  font-size: 18px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 0.56px;
  line-height: 30px;
  padding: 12px 24px;
}
.logs-tab-body {
  padding: 8px 0px;
}
.log-filter-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0px 24px 16px 24px;
  border-bottom: solid 1px #eee;
}
.log-table-container {
  padding: 0px 24px 16px 24px;
  height: calc(100vh - 270px);
  overflow: scroll;
  margin-top: 16px;
}
.vl {
  border-left: 2px solid rgb(208, 211, 208);
  height: 19px;
  width: 25px;
}
</style>
<style lang="scss">
.logs-main-container {
  .tags-container {
    margin: 10px 24px;
  }
  .el-tabs__header {
    margin: 0 0 16px;
    padding-left: 0px;
  }
  .el-tabs__nav-scroll {
    padding-left: 24px;
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
