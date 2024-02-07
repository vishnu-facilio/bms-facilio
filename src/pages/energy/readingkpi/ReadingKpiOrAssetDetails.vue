<template>
  <div class="d-flex flex-direction-column height-100">
    <div v-if="currentRecordId !== 0" class="top-bar p10 height50">
      <div class="asset-name pL20">
        {{ currentRecordName }}
      </div>
      <div class="pagination-search">
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        >
        </AdvancedSearch>
        <span v-if="recordCount !== 0" class="separator">|</span>
        <Pagination
          :currentPage.sync="page"
          :total="recordCount"
          :perPage="perPage"
          @update:currentPage="loadNextPage"
        ></Pagination>
      </div>
    </div>
    <div v-if="!$validation.isEmpty(query)" class="top-bar p0">
      <FTags class="f-tag" :key="`ftags-list-${moduleName}`"></FTags>
    </div>
    <div
      v-if="isLoading"
      class="white-background  width-100 flex-middle justify-content-center flex-direction-column flex-grow"
    >
      <Spinner :show="isLoading" size="80"></Spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(records)"
      class="white-background  width-100 flex-middle justify-content-center flex-direction-column flex-grow"
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
        'padding-left': '30px',
        'font-weight': 500,
      }"
      height="100%"
      class="reading-kpi-list-table"
    >
      <el-table-column label="NAME">
        <template v-slot="record">
          {{ getRecordData(record, 'name') }}
        </template>
      </el-table-column>
      <el-table-column label="CURRENT VALUE">
        <template v-slot="record">
          {{ getValue(record) }}
        </template>
      </el-table-column>
      <el-table-column label="LAST RECORDED">
        <template v-slot="record">
          {{ getRecordData(record, 'ttime') | fromNow }}
        </template>
      </el-table-column>
      <el-table-column label="ANALYTICS">
        <template v-slot="record">
          <div style="width:100%" class="text-ellipsis">
            <a class="f13 flex align-center" @click="goToAnalytics(record.row)">
              {{ $t('asset.readings.go_to_analytics') }}
              <InlineSvg
                src="svgs/black-arrow-right"
                class="pointer flex mL5"
                iconClass="icon icon-xs"
              ></InlineSvg>
            </a>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import JumpToHelper from '@/mixins/JumpToHelper'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FTags from 'newapp/components/search/FTags'
import Spinner from '@/Spinner'

export default {
  components: { Pagination, AdvancedSearch, FTags, Spinner },
  mixins: [JumpToHelper],
  props: ['groupBy', 'currentRecordId', 'currentRecordName', 'kpiType'],
  data() {
    return {
      isLoading: false,
      recordCount: null,
      page: 1,
      perPage: 50,
      records: [],
    }
  },
  computed: {
    isGroupByKpi() {
      let { groupBy } = this
      return groupBy === 'kpi'
    },
    moduleName() {
      return this.isGroupByKpi ? 'asset' : 'readingkpi'
    },
    moduleDisplayName() {
      return this.isGroupByKpi ? 'Resources' : 'ReadingKpi'
    },
    query() {
      let { query } = this.$route || {}
      return query
    },
  },
  watch: {
    currentRecordId(value) {
      if (value === 0) {
        this.recordCount = 0
        this.records = []
      } else if (value !== -1) this.loadData(this.query)
    },
    query: {
      handler(value) {
        let { currentRecordId } = this
        if (currentRecordId > 0) this.loadData(value)
      },
    },
    groupBy() {
      this.page = 1
    },
  },
  methods: {
    async loadData(filters) {
      let url = '/v3/readingKpi/fetchReadings'
      let { groupBy, currentRecordId, page, perPage, kpiType } = this
      let { search } = filters || {}
      let params = {
        groupBy,
        recordId: currentRecordId,
        page,
        perPage,
        filters: search,
        kpiType,
      }
      this.isLoading = true
      let { data, error } = await API.get(url, params)
      if (isEmpty(error)) {
        let { result } = data || {}
        let { records, recordCount } = result || {}
        this.records = records
        if (!isEmpty(recordCount)) this.recordCount = recordCount
      }
      this.isLoading = false
    },
    getRecordData(record, property) {
      return this.$getProperty(record, `row.${property}`, '---')
    },
    getValue(record) {
      let { row: { value, unitLabel } = {} } = record || {}
      return isEmpty(unitLabel)
        ? `${parseFloat(value).toFixed(2)}`
        : `${parseFloat(value).toFixed(2)} ${unitLabel}`
    },
    goToAnalytics(kpi) {
      let { fieldId, unitLabel: unit, resourceId, ttime } = kpi || {}
      let dateFilter = {
        operatorId: 63,
        value: !isEmpty(ttime) ? ttime + '' : null,
      }
      let aggr

      if (!isEmpty(unit)) {
        if (
          ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
            unit.trim().toLowerCase()
          )
        )
          aggr = 3
      } else {
        aggr = 2
      }
      this.jumpReadingToAnalytics(
        resourceId,
        fieldId,
        dateFilter,
        null,
        aggr,
        null
      )
    },
    async loadNextPage(page) {
      this.page = page
      await this.loadData()
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
.f-tag {
  border: none;
  padding: 0px;
}
.asset-name {
  font-size: 15px;
  font-weight: 500;
}
.pagination-search {
  display: flex;
  align-items: center;
}
</style>
<style lang="scss">
.reading-kpi-list-table {
  height: calc(100vh - 120px) !important;
  .el-table__cell {
    padding: 15px 20px 15px 30px;
    font-size: 13px;
  }
}
</style>
