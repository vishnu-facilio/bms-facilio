<template>
  <div class="related-readings-list height100" ref="related-readings-list">
    <template v-if="isActive">
      <portal :to="portalName" :key="portalName + '-portalwrap'" slim>
        <!-- <f-search
          v-if="isActive"
          v-model="readingsList"
          searchKey="fieldName"
          :key="portalName + '-search'"
          class="mR15 mT5"
        ></f-search> -->
        <template v-if="recordsCount">
          <!-- <span class="seperator">|</span> -->
          <pagination
            v-if="isActive"
            :currentPage.sync="currentPage"
            :total="recordsCount"
            :perPage="perPage"
            class="self-center mT5"
          ></pagination>
        </template>
      </portal>
    </template>
    <div v-if="isLoading" class="hv-center height100">
      <spinner :show="isLoading"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(readingsList)"
      class="height100 hv-center"
    >
      <div>
        <div class="hv-center">
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-130 emptystate-icon-size"
          ></InlineSvg>
        </div>
        <div class="fc-black-dark f18 bold">
          {{ $t('asset.readings.no_readings_available') }}
        </div>
        <!-- <div class="fc-grayish pT10">
          {{ $t('asset.readings.no_readings_available_desc') }}
        </div> -->
      </div>
    </div>
    <div v-else>
      <el-table
        v-if="!$validation.isEmpty(readingsList)"
        :data="readingsList"
        ref="relatedReadingsTable"
        :height="tableHeight"
        :fit="true"
        header-row-class-name="readings-table-header"
      >
        <el-table-column label="Name">
          <template slot-scope="reading">
            <span>{{ $getProperty(reading, 'row.resourceName', '---') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Reading Name">
          <template slot-scope="reading">
            <span>{{
              $getProperty(reading, 'row.field.displayName', '---')
            }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Value">
          <template slot-scope="reading">
            <span>{{ getReadingValue(reading) }}</span>
          </template></el-table-column
        ><el-table-column label="Last Recorded">
          <template slot-scope="reading">
            <span>{{ getTimeFromNow(reading) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Analytics">
          <template slot-scope="reading">
            <span v-if="hasAnalyticsPermission">
              <div style="width:100%" class="text-ellipsis">
                <a class="f13 flex align-center" @click="goToAnalytics(reading)"
                  >{{ $t('asset.readings.go_to_analytics') }}
                  <InlineSvg
                    src="svgs/black-arrow-right"
                    class="pointer flex mL5"
                    iconClass="icon icon-xs"
                  ></InlineSvg>
                </a>
              </div>
            </span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import FSearch from '@/FSearch'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import JumpToHelper from '@/mixins/JumpToHelper'
import { fromNow } from 'src/util/filters'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import isEqual from 'lodash/isEqual'
export default {
  mixins: [JumpToHelper],
  components: { FSearch, Pagination },
  props: ['siteId', 'portalName', 'relDetail', 'isActive'],
  data() {
    return {
      readingsList: [],
      resourceMap: {},
      readingsMap: {},
      isLoading: false,
      currentPage: 1,
      perPage: 10,
      recordsCount: 0,
    }
  },
  computed: {
    tableHeight() {
      let { $refs } = this
      let height = '250px'
      let tableContainer = $refs['related-readings-list']
      if (!isEmpty(tableContainer)) {
        let containerHeight = (tableContainer || {}).clientHeight
        height = `${containerHeight}px`
      }
      return height
    },
    activeTab() {
      return this.$attrs.activeTab
    },
    hasAnalyticsPermission() {
      return (
        this.$helpers.isLicenseEnabled('ENERGY') &&
        this.$hasPermission(
          'energy:CREATE_EDIT_REPORTS,VIEW_REPORTS,CREATE_EDIT_DASHBOARD,EXPORT_REPORTS'
        )
      )
    },
  },
  watch: {
    currentPage(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadReadingsList()
      }
    },
  },
  methods: {
    async fetchCountAndData() {
      this.isLoading = true
      await this.loadReadingsCount()
      await this.loadReadingsList()
      this.isLoading = false
    },
    async loadReadingsCount() {
      let { relDetail, siteId: resourceId, currentPage: page, perPage } = this
      let { reverseRelationLinkName: relMapLinkName } = relDetail || {}
      let params = {
        page,
        perPage,
        relMapLinkName,
        resourceId,
        readingType: 'all',
        fetchCount: true,
      }
      let { data, error } = await API.get(
        'v2/reading/latestdata/getRelatedData',
        params
      )
      if (!error) {
        let { count } = data || {}
        this.recordsCount = !isEmpty(count) ? count : 0
      }
    },
    async loadReadingsList() {
      this.isLoading = true
      let { relDetail, siteId: resourceId, currentPage: page, perPage } = this
      let { reverseRelationLinkName: relMapLinkName } = relDetail || {}
      let params = {
        page,
        perPage,
        relMapLinkName,
        resourceId,
        readingType: 'all',
      }
      let { data, error } = await API.get(
        'v2/reading/latestdata/getRelatedData',
        params
      )
      this.readingsList = []
      this.resourceMap = {}
      if (!error && !isEmpty(data)) {
        let { readingValues = [], resources = {} } = data || {}
        this.readingsList = !isEmpty(readingValues) ? readingValues : []
        this.resourceMap = !isEmpty(resources) ? resources : {}
        this.readingsList.forEach(reading => {
          let fieldName = this.$getProperty(reading, 'field.displayName', '---')
          let { resourceId } = reading
          reading.fieldName = fieldName
          reading.resourceName = this.resourceMap[resourceId].name
        })
      }
      this.isLoading = false
    },
    getReadingValue(readingRow) {
      let reading = this.$getProperty(readingRow, 'row')
      if (!isEmpty(reading)) {
        let { actualValue } = reading
        let unit = this.$getProperty(reading, 'field.unit', '')
        return !isEmpty(actualValue)
          ? `${actualValue} ${!isEmpty(unit) ? unit : ''}`
          : '---'
      }
      return '---'
    },
    getTimeFromNow(readingRow) {
      let reading = this.$getProperty(readingRow, 'row')
      if (!isEmpty(reading)) {
        let { actualValue, ttime } = reading
        return !isEmpty(actualValue) ? fromNow(ttime) : '---'
      }
      return '---'
    },
    goToAnalytics(readingRow) {
      let reading = this.$getProperty(readingRow, 'row')
      let assetId = this.$getProperty(reading, 'resourceId')
      let fieldId = this.$getProperty(reading, 'field.id')
      let dateFilter = {
        operatorId: 63,
        value: reading.ttime ? reading.ttime + '' : null,
      }

      let buildingId = this?.details?.space
        ? this?.details?.space?.buildingId
        : null
      let aggr =
        reading.field &&
        reading.field.unit &&
        ['currency', 'kwh', 'co2', 'kg', 'mwh'].includes(
          reading.field.unit.trim().toLowerCase()
        )
          ? 3
          : 2
      this.jumpReadingToAnalytics(
        assetId,
        fieldId,
        dateFilter,
        null,
        aggr,
        buildingId
      )
    },
  },
  created() {
    this.fetchCountAndData()
  },
}
</script>

<style lang="scss" scoped>
.related-readings-list {
  .hv-center {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>

<style lang="scss">
.related-readings-widget {
  .el-table {
    .readings-table-header {
      height: 52px;
      .el-table__cell {
        background-color: #f3f1fc;
      }
    }
    .el-table__body {
      .el-table__row {
        height: 50px;
        .el-table__cell {
          padding: 10px 20px;
        }
      }
    }
  }
}
</style>
