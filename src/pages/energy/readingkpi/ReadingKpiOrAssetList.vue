<template>
  <div class="d-flex flex-direction-column height-100">
    <div class="border-bottom1px p10 reading-kpi-list-radio-group">
      <el-radio-group v-model="listData.groupBy" @change="changeGroupBy">
        <el-radio-button label="kpi">{{ $t('kpi.kpi.kpi') }}</el-radio-button>
        <el-radio-button label="asset">{{
          $t('kpi.kpi.resource')
        }}</el-radio-button>
      </el-radio-group>
    </div>
    <div class="p10">
      <el-input
        :placeholder="searchPlaceholder"
        v-model="filter"
        @input="quickSearch"
        class="fc-input-full-border2 width100"
      >
      </el-input>
    </div>
    <div class="p10 border-bottom1px" v-if="listData.groupBy === 'kpi'">
      <el-select
        placeholder="Select Frequency"
        class="fc-input-full-border2 width100"
        v-model="listData.frequency"
        @change="quickSearch"
        clearable
        multiple
        collapse-tags
      >
        <el-option
          v-for="(key, label) in liveKpiFrequencies"
          :key="key"
          :label="label"
          :value="key"
        ></el-option>
      </el-select>
    </div>
    <div
      v-if="isLoading"
      class="d-flex flex-direction-column items-center justify-center flex-grow"
    >
      <Spinner :show="isLoading" size="80"></Spinner>
    </div>
    <div
      v-else-if="noData"
      class="d-flex flex-direction-column items-center justify-center flex-grow"
    >
      <inline-svg
        src="svgs/emptystate/readings-empty"
        iconClass="icon text-center icon-xxxlg"
      ></inline-svg>
      <div class="q-item-label">
        {{ $t('emailLogs.no_records_found') }}
      </div>
    </div>
    <v-infinite-scroll
      v-else
      :offset="20"
      class="overflow-y-scroll mT0 p0 flex-grow"
    >
      <div
        v-for="(record, index) in records"
        :key="index"
        @click="emitClick(record)"
        class="kpi-list-item"
        :class="{ active: listData.currentRecordId === record.id }"
      >
        <div class="kpi-list-name">
          {{ record.name || '---' }}
        </div>
      </div>
    </v-infinite-scroll>
    <Pagination
      :currentPage.sync="page"
      :total="recordCount"
      :perPage="perPage"
      @update:currentPage="loadNextPage"
      class="d-flex justify-content-center pB10 pT10 border-top-grey"
    ></Pagination>
  </div>
</template>
<script>
import VInfiniteScroll from 'v-infinite-scroll'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import debounce from 'lodash/debounce'
import Spinner from '@/Spinner'

const liveKpiFrequencies = {
  '1 Min': 1,
  '2 Mins': 2,
  '3 Mins': 3,
  '4 Mins': 4,
  '5 Mins': 5,
  '10 Mins': 6,
  '15 Mins': 7,
  '20 Mins': 8,
  '30 Mins': 9,
  '1 Hr': 10,
  '2 Hr': 11,
  '3 Hr': 12,
  '4 Hr': 13,
  '8 Hr': 14,
  '12 Hr': 15,
  '1 Day': 16,
}

export default {
  props: ['kpiType'],
  components: { VInfiniteScroll, Pagination, Spinner },
  created() {
    this.init()
  },
  data() {
    return {
      isLoading: false,

      listData: {
        groupBy: 'kpi',
        frequency: null,
        currentRecordId: -1,
        currentRecordName: null,
      },
      noData: false,
      filter: '',
      liveKpiFrequencies: liveKpiFrequencies,
      kpiNames: null,
      assetNames: null,
      page: 1,
      perPage: 50,
      recordCount: null,
    }
  },
  computed: {
    isGroupByKpi() {
      let { listData } = this
      let { groupBy } = listData || {}
      return groupBy === 'kpi'
    },
    quickSearch() {
      return this.isGroupByKpi
        ? debounce(this.loadKpiNameList, 500)
        : debounce(this.loadAssetNameList, 500)
    },
    searchPlaceholder() {
      let groupByName = this.isGroupByKpi ? 'KPIs' : 'Resources'
      return `Search ${groupByName}`
    },
    records() {
      return this.isGroupByKpi ? this.kpiNames : this.assetNames
    },
  },
  watch: {
    listData: {
      handler(value) {
        this.$emit('changeInNameList', value)
      },
      deep: true,
      immediate: true,
    },
    kpiType() {
      this.changeGroupBy()
    },
  },
  methods: {
    init() {
      this.loadKpiNameList()
    },
    emitClick(record) {
      this.setCurrentRecord(record)
      this.$emit('changeRecord', record)
    },
    setCurrentRecord(record) {
      let { id, name } = record || {}
      this.listData.currentRecordId = id
      this.listData.currentRecordName = name
    },
    changeGroupBy() {
      this.$router.push({ query: {} })
      this.listData.frequency = null
      this.filter = null
      this.page = 1
      this.isGroupByKpi ? this.loadKpiNameList() : this.loadAssetNameList()
    },
    async loadKpiNameList() {
      let url = '/v3/readingKpi/fetchKpiNames'
      let { page, perPage, filter, listData, kpiType } = this
      let { frequency } = listData || {}
      let params = {
        page,
        perPage,
        searchText: filter,
        frequencies: frequency,
        kpiType,
      }
      this.isLoading = true
      let { data, error } = await API.get(url, params)
      if (isEmpty(error)) {
        let { kpis, recordCount } = data.result
        this.kpiNames = kpis
        if (!isEmpty(recordCount)) this.recordCount = recordCount
        if (!isEmpty(kpis[0])) {
          this.setCurrentRecord(kpis[0])
          this.noData = false
        } else {
          this.noData = true
          this.setCurrentRecord({ id: 0, name: null })
        }
      }
      this.isLoading = false
    },

    async loadAssetNameList() {
      let url = '/v3/readingKpi/fetchAssetNames'
      // let { changingGroup } = args || {}
      // changingGroup = !isEmpty(changingGroup)
      // let fetchCount = isEmpty(this.recordCount) || changingGroup
      let { page, perPage, filter, kpiType } = this

      let params = {
        page,
        perPage,
        searchText: filter,
        kpiType,

        // fetchCount: fetchCount,
      }
      this.isLoading = true

      let { data, error } = await API.get(url, params)
      if (isEmpty(error)) {
        let { assets, recordCount } = data.result
        this.assetNames = assets
        if (!isEmpty(recordCount)) this.recordCount = recordCount
        if (!isEmpty(assets[0])) {
          this.setCurrentRecord(assets[0])
          this.noData = false
        } else {
          this.noData = true
          this.setCurrentRecord({ id: 0, name: null })
        }
      }
      this.isLoading = false
    },
    async loadNextPage(page) {
      this.page = page
      this.isGroupByKpi
        ? await this.loadKpiNameList()
        : await this.loadAssetNameList()
    },
  },
}
</script>
<style lang="scss" scoped>
.kpi-list-item {
  padding: 15px;
  border-bottom: 1px solid #f4f6f8;
  align-items: center;
  font-size: 15px;
  white-space: nowrap;
  .kpi-list-name {
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 270px;
  }
}
.kpi-list-item:hover,
.kpi-list-item.active {
  background: #eef5f7;
  cursor: pointer;
  font-weight: 500;
}
</style>
<style lang="scss">
.reading-kpi-list-radio-group {
  display: flex;
  justify-content: center;
  .el-radio-group {
    width: 100%;
    display: inline-flex;
    label,
    span {
      width: 100%;
    }
  }
}
</style>
