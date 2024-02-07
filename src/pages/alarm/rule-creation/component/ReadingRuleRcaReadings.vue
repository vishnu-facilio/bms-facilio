<template>
  <div class="d-flex flex-direction-column height-100">
    <div class="top-bar height50">
      <div class="f15 bold pL15">
        {{ $t('rule.create.root_cause') }}
      </div>
      <div class="flex-middle">
        <!-- <AdvancedSearch
          :key="`${rcaScoreModuleName}-search`"
          :moduleName="rcaScoreModuleName"
          :moduleDisplayName="rcaScoreModuleDisplayName"
          :hideQuery="true"
          :onSave="setAppliedfilter"
          :filterList="filters"
        >
        </AdvancedSearch> -->
        <!-- <span v-if="recordCount !== 0" class="separator">|</span> -->
        <Pagination
          :currentPage.sync="page"
          :total="recordCount"
          :perPage="perPage"
          @update:currentPage="loadNextPage"
        ></Pagination>
        <NewDatePicker
          :zone="$timezone"
          class="filter-field date-filter-comp"
          style="margin-left: auto"
          :dateObj.sync="dateObj"
          @date="changeDateFilter"
        ></NewDatePicker>
      </div>
    </div>
    <!-- <div class="top-bar">
      <FTags
        class="f-tag p0"
        :key="`ftags-list-${rcaScoreModuleName}`"
        :hideQuery="true"
        :filterList="filters"
        hideSaveView="true"
        @updateFilters="applyFiltersFromTags"
        @resetFilters="resetFilters"
      ></FTags>
    </div> -->
    <div
      v-if="isLoading"
      class="white-background  width-100 flex-middle justify-content-center flex-direction-column flex-grow"
    >
      <Spinner :show="isLoading" size="80"></Spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(records)"
      class="white-background width-100 flex-middle justify-content-center flex-direction-column flex-grow empty-logo"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('common._common.nodata') }}
      </div>
    </div>
    <el-table
      v-else
      :data="records"
      :header-cell-style="{
        background: '#f3f1fc',
        padding: '15px',
        'font-weight': 500,
      }"
      class="rule-rca-score-readings-table"
    >
      <el-table-column label="FAULT">
        <template v-slot="record">
          <div @click="redirectToAlarmOverview(record)">
            {{ getRcaFaultName(record) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="ASSET">
        <template v-slot="record">
          {{ getAssetCatName(record) }}
        </template>
      </el-table-column>
      <el-table-column label="OCCURRENCES">
        <template v-slot="record">
          {{ getCount(record) }}
        </template>
      </el-table-column>
      <el-table-column label="DURATION">
        <template v-slot="record">
          {{ getDuration(record) }}
        </template>
      </el-table-column>
      <el-table-column label="SCORE">
        <template v-slot="record">
          {{ getRank(record) }}
        </template>
      </el-table-column>
      <el-table-column type="expand">
        <template v-slot="record">
          <div class="pT16">
            <AlarmBar
              :parentId="getRcaFaultId(record)"
              :sourceKey="'alarmId'"
              :dateOperator="dateOperator"
              :dateValue="dateValue"
              class="fc-v1-alarm-cell"
            ></AlarmBar>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import AlarmBar from '@/AlarmBar'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData.js'
import Pagination from 'pageWidgets/utils/WidgetPagination'
// import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import { API } from '@facilio/api'
// import FTags from 'newapp/components/search/FTags'
import Spinner from '@/Spinner'
import NewDateHelper from '@/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  components: {
    Pagination,
    // AdvancedSearch,
    // FTags,
    Spinner,
    AlarmBar,
    NewDatePicker,
  },
  props: ['details', 'widget', 'tab'],
  data() {
    return {
      isLoading: false,
      recordCount: null,
      page: 1,
      perPage: 50,
      records: [],
      filters: {},
      rcaScoreModuleName: 'readingrulerca_score_readings',
      rcaScoreModuleDisplayName: 'Reading Rule RCA Readings',
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      configData: {
        height: 40,
        width: 100,
        dateFilter: NewDateHelper.getDatePickerObject(20),
      },
      dateObj: null,
      dateValue: null,
      dateOperator: 20,
    }
  },
  computed: {
    modelDataClass() {
      return CustomModuleData
    },
    currentAlarmId() {
      let { details: { id } = {} } = this
      return id
    },
  },
  watch: {
    details: {
      handler(newVal) {
        let { alarm } = newVal || {}
        let { lastOccurredTime } = alarm || {}
        if (!isEmpty(lastOccurredTime)) {
          this.init()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    async init() {
      await this.initDateObj()
      await this.loadRecords()
    },
    async initDateObj() {
      let { details: { alarm } = {} } = this
      let { lastOccurredTime } = alarm || {}
      if (!isEmpty(lastOccurredTime)) {
        let endTime = moment(lastOccurredTime)
          .endOf('day')
          .valueOf()
        this.dateValue = [endTime - 6 * 24 * 60 * 60 * 1000, endTime]
        this.dateObj = NewDateHelper.getDatePickerObject(20, [
          endTime - 6 * 24 * 60 * 60 * 1000,
          endTime,
        ])
      }
    },
    async loadRecords() {
      try {
        let {
          page,
          currentAlarmId,
          dateObj,
          dateOperator,
          perPage,
          filters,
        } = this
        if (!isEmpty(currentAlarmId)) {
          let params = {
            page,
            perPage,
            alarmId: currentAlarmId,
            filters: JSON.stringify(filters),
          }
          let { operatorId, value } = dateObj || {}
          if (!isEmpty(dateObj) && !isEmpty(operatorId)) {
            let operatorId = dateOperator
            this.dateValue = value.join()
            if (NewDateHelper.isValueRequired(operatorId)) {
              params.dateOperator = operatorId
              params.dateOperatorValue = this.dateValue
            } else {
              params.dateOperator = operatorId
            }
          }

          this.isLoading = true

          let { data, error } = await API.get(
            `/v3/readingrule/fetchRcaReadings`,
            params
          )
          if (isEmpty(error)) {
            let { result } = data || {}
            let { records, recordCount } = result || {}
            this.records = records
            this.recordCount = recordCount
          } else {
            this.$message.error(error.message)
          }
        }
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
      this.isLoading = false
    },

    applyFiltersFromTags({ filters }) {
      this.filters = filters
      this.resetPage()
      this.loadRecords()
    },
    setAppliedfilter({ filters }) {
      this.filters = filters || {}
      this.loadRecords()
    },
    resetFilters() {
      this.filters = {}
      this.resetPage()
      this.loadRecords()
    },
    resetPage() {
      this.page = 1
    },

    getAssetCatName(record) {
      let { row: { rcaFault: { readingAlarmAssetCategory } = {} } = {} } =
        record || {}
      return this.$getProperty(readingAlarmAssetCategory, 'displayName', '---')
    },
    getRcaFaultId(record) {
      let { row: { rcaFault } = {} } = record || {}
      return this.$getProperty(rcaFault, 'id', '---')
    },
    getRcaFaultName(record) {
      let { row: { rcaFault } = {} } = record || {}
      return this.$getProperty(rcaFault, 'subject', '---')
    },
    getDuration(record) {
      let { row: { duration } = {} } = record || {}
      return !isEmpty(duration)
        ? this.$helpers.getFormattedDuration(duration)
        : '---'
    },
    getCount(record) {
      let { row } = record || {}
      return this.$getProperty(row, 'count', '---')
    },
    getRank(record) {
      let { row } = record || {}
      return this.$getProperty(row, 'score', '---')
    },
    async loadNextPage(page) {
      this.page = page
      await this.loadRecords()
    },
    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter
      // TODO...use alarmbarmixin for booleancard and alarminsight
      if (this.localDateFormat.includes(dateFilter.operatorId)) {
        this.dateOperator = dateFilter.operatorId
        this.dateValue = null
      } else {
        this.dateOperator = 20
        this.dateValue = dateFilter.value.join()
      }
      this.loadRecords()
    },
    redirectToAlarmOverview(record) {
      let { row } = record || {}
      let { rcaFault } = row || {}
      let { id } = rcaFault || {}
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('newreadingalarm', pageTypes.OVERVIEW) || {}
          name &&
            this.$router.push({
              name,
              params: { id, viewname: 'all' },
            })
        } else {
          this.$router.push({
            name: 'newreadingalarm-summary',
            params: { id, viewname: 'all' },
          })
        }
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-bottom: 3px solid #f7f8f9;
}
.alarm-bar-cell .animated-background {
  width: 92.8%;
}
.fc-v1-alarm-cell {
  position: relative;
}
.f-tag {
  border: none !important;
  padding: 15px 15px 0px 15px;
}
.empty-logo {
  height: calc(100vh - 280px) !important;
}
</style>
<style lang="scss">
.rule-rca-score-readings-table {
  .el-table__cell {
    padding: 15px 20px 15px 15px;
    font-size: 13px;
    .cell {
      .el-table__expand-icon {
        width: 11px;
        height: 22px;
        transform: rotate(180deg);
      }
      .el-table__expand-icon--expanded {
        width: 11px;
        height: 22px;

        transform: rotate(90deg);
      }
    }
  }
  .el-table__expanded-cell {
    background-color: #fafafa;
  }
}
</style>
