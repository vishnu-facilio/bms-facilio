<template>
  <CommonListLayout
    moduleName="formulaField"
    :getPageTitle="() => 'Reading KPI Viewer'"
    :class="['reading-kpi', isInPageBuilder && 'summary-page']"
  >
    <template #views>
      <KpiViews v-if="!isInPageBuilder"></KpiViews>
      <div v-else></div>
    </template>

    <template #header v-if="!isInPageBuilder">
      <template v-if="!showSearch && totalCount > 0">
        <pagination
          :total="totalCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>

        <span class="separator">|</span>
      </template>

      <search
        v-if="!isFilterOptionsLoading"
        :config="filterConfig"
        :moduleName="filterConfig.moduleName"
        defaultFilter="name"
        :includeAllMetaFields="false"
      ></search>

      <template v-if="!showSearch">
        <span class="separator">|</span>

        <div class="pL10">
          <button
            class="fc-create-btn outline-none"
            style="margin-top: -10px;"
            @click="showCreationForm = true"
          >
            {{ $t('common.products.new_kpi') }}
          </button>
        </div>
      </template>
    </template>

    <div class="p10 pT0 mT10 kpi-viewer">
      <div v-if="!isInPageBuilder">
        <tags :disableSaveFilters="true"></tags>
      </div>
      <div
        class="width100 fc-rp-top-bar white-bg inline-flex justify-content-space"
      >
        <span v-if="!isInPageBuilder" class="mT10 mR10 bold">{{
          $t('common._common.frequency')
        }}</span>
        <el-select
          v-if="!isInPageBuilder"
          v-model="currentView"
          @change="handleViewTabClick"
          class="fc-input-full-border2"
        >
          <el-option
            v-for="(view, index) in views"
            :key="index"
            :label="$helpers.capitalize(view)"
            :value="view"
            class="text-capitalize reading-kpi"
          ></el-option>
        </el-select>

        <div
          v-if="!isInPageBuilder && lastUpdatedTime"
          class="llast-updated f12 p15 mL20 letter-spacing0_5 pT12"
        >
          {{ $t('common.header.last_updated') }}
          <span class="bold">{{ lastUpdatedTime }}</span>
        </div>

        <div class="marginL-auto d-flex">
          <div class="width: 220px; text-align: right;">
            <new-date-picker
              v-if="showPicker"
              :dateObj="dateObj"
              :zone="$timezone"
              @date="onDateChange"
              class="facilio-resource-date-picker"
            ></new-date-picker>
          </div>
        </div>
      </div>

      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>

      <div
        v-else-if="$validation.isEmpty(list)"
        class="width100 text-center empty-container pB30 pT30"
      >
        <inline-svg
          src="svgs/emptystate/kpi"
          class="vertical-middle"
          iconClass="icon icon-256"
        ></inline-svg>
        <div class="f18 bold" style="margin-bottom: 7px;">
          {{ $t('common.products.no_kpis_found') }}
        </div>
        <div class="f12 grey-text2">
          {{ $t('common.header.you_dont_seem_to_have_kpis_created_view') }}
        </div>
      </div>

      <div
        v-else
        :class="['table-container', !isInPageBuilder && 'full-height']"
      >
        <table cellpadding="0">
          <thead ref="tableHeader" class="letter-spacing08">
            <tr>
              <th style="width: 26%" class="text-uppercase header">
                <div class="header-cell">
                  <template v-if="isInPageBuilder">{{ kpiType }}</template>
                  <template v-else>
                    {{ isGroupedByKpi ? 'KPI' : kpiType }}
                  </template>
                  <a
                    v-if="!isInPageBuilder"
                    @click="changeGrouping(isGroupedByKpi)"
                    class="f11 fR font-normal line-height18 text-capitalize"
                  >
                    {{ isGroupedByKpi ? `Group By Resource` : 'Group By KPI' }}
                  </a>
                </div>
              </th>
              <th style="width: 8%" class="text-uppercase text-center header">
                <div class="header-cell">
                  {{ $t('common._common.safe_limit') }}
                </div>
              </th>
              <th style="width: 10%" class="text-uppercase text-center header">
                <div class="header-cell">
                  {{ $t('common.header.current_value') }}
                </div>
              </th>
              <th
                style="width: 8%"
                class="text-uppercase text-center header secondary"
              >
                <div class="header-cell">{{ $t('common._common.sum') }}</div>
              </th>
              <th
                style="width: 8%"
                class="text-uppercase text-center header secondary"
              >
                <div class="header-cell">{{ $t('common.header.avg') }}</div>
              </th>
              <th
                style="width: 12%"
                class="text-uppercase text-center header secondary"
              >
                <div class="header-cell">
                  {{ $t('common.products.min_max') }}
                </div>
              </th>
              <th style="width: 22%" class="text-uppercase header secondary">
                <div class="header-cell">
                  {{ $t('common.header.kpi_trend') }}
                </div>
              </th>
              <th
                style="width: 6%"
                class="text-uppercase text-right header secondary"
              >
                <div class="header-cell">
                  {{ $t('common.tabs.violations') }}
                </div>
              </th>
            </tr>
          </thead>
          <tbody ref="tableBody">
            <template v-for="item in list">
              <tr
                v-if="!isInPageBuilder"
                :key="item.id"
                class="separator-row pointer"
              >
                <th colspan="8">
                  <div
                    class="cell p10 pT5 pB5 f12"
                    @click="toggleDetails(item)"
                  >
                    <i v-if="item.canShow" class="el-icon-arrow-up"></i>
                    <i v-else class="el-icon-arrow-down"></i>
                    {{ item.name }}
                    <span
                      v-if="
                        isGroupedByKpi &&
                          $getProperty(item, 'data.0.unit', null)
                      "
                      class="pL5 fR fw-normal bold"
                      >{{ $t('common.header.all_values_in') }}
                      {{ $getProperty(item, 'data.0.unit', null) }}</span
                    >
                  </div>
                </th>
              </tr>
              <template v-if="item.canShow">
                <tr
                  class="data-row"
                  v-for="row in item.data"
                  :key="
                    `${item.id}-${isGroupedByKpi ? row.resourceId : row.id}`
                  "
                >
                  <td>
                    <div :class="['cell', !isInPageBuilder && 'pL40']">
                      {{ isGroupedByKpi ? row.resourceName : row.name }}
                    </div>
                  </td>
                  <td class="text-right">
                    <div class="cell">
                      {{ getSafeLimit(row.target, row.minTarget) }}
                    </div>
                  </td>
                  <td class="text-right">
                    <div class="cell">
                      {{ Number.parseFloat(row.value).toFixed(2) || '--' }}
                      <span v-if="!isGroupedByKpi && row.unit">
                        {{ row.unit }}
                      </span>
                    </div>
                  </td>

                  <td v-if="shimmerLoading" class="text-right">
                    <div class="loading-shimmer lines"></div>
                  </td>
                  <td v-else class="text-right">
                    <div class="cell">
                      {{ row.sum || '--' }}
                      <span v-if="!isGroupedByKpi && row.unit">
                        {{ row.unit }}
                      </span>
                    </div>
                  </td>

                  <td v-if="shimmerLoading" class="text-right">
                    <div class="loading-shimmer lines"></div>
                  </td>
                  <td v-else class="text-right">
                    <div class="cell">
                      {{ row.avg || '--' }}
                      <span v-if="!isGroupedByKpi && row.unit">
                        {{ row.unit }}
                      </span>
                    </div>
                  </td>

                  <td v-if="shimmerLoading" class="text-center">
                    <div class="loading-shimmer lines"></div>
                  </td>
                  <td v-else class="text-center">
                    <div class="cell p5" v-if="row.min || row.max">
                      {{ row.min || '--' }}
                      <span v-if="!isGroupedByKpi && row.unit">
                        {{ row.unit }}
                      </span>
                      -
                      {{ row.max || '--' }}
                      <span v-if="!isGroupedByKpi && row.unit">
                        {{ row.unit }}
                      </span>
                    </div>
                    <div v-else>---</div>
                  </td>

                  <td v-if="shimmerLoading">
                    <div class="sparkline-trend loading-shimmer lines"></div>
                  </td>
                  <td v-else class="p0">
                    <div class="sparkline-trend" ref="sparkline-trend">
                      <sparkline
                        v-if="row.sparkline && row.sparkline.dataObj"
                        :height="sparkLineStyleObj.height"
                        :width="sparkLineStyleObj.width"
                        :tooltipProps="row.sparkline.label"
                      >
                        <sparklineCurve
                          :data="row.sparkline.dataObj"
                          :limit="row.sparkline.dataObj.length"
                          :styles="sparkLineStyleObj.style"
                          :textStyles="row.sparkline.label"
                          refLineType="custom"
                          :refLineStyles="
                            row.target
                              ? sparkLineStyleObj.targetLineStyle
                              : null
                          "
                          :refLineProps="
                            row.target ? { value: row.target } : null
                          "
                        />
                      </sparkline>
                    </div>
                  </td>

                  <td v-if="shimmerLoading" class="text-right">
                    <div class="loading-shimmer lines"></div>
                  </td>
                  <td v-else class="text-right">
                    <div class="cell">{{ row.violations || '--' }}</div>
                  </td>
                </tr>
              </template>
            </template>
          </tbody>
        </table>
      </div>
    </div>
    <new-kpi
      v-if="showCreationForm"
      :isNew="true"
      @onSave="loadData()"
      @onClose="showCreationForm = false"
    ></new-kpi>
  </CommonListLayout>
</template>
<script>
import ViewMixinHelper from '@/mixins/ViewMixin'
import NewDatePicker from '@/NewDatePicker'
import NewDateHelper from '@/mixins/NewDateHelper'
import { isEmpty } from '@facilio/utils/validation'
import sparkline from 'newcharts/sparklines/Sparkline'
import Pagination from '@/list/FPagination'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import { viewStateMeta } from './helpers/kpiConstants'
import formatter from 'charts/helpers/formatter'
import moment from 'moment-timezone'
import Vue from 'vue'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Search from 'newapp/components/Search'
import Tags from 'newapp/components/Tags'
import NewKpi from 'pages/energy/kpi/components/AddKpi'
import { mapState } from 'vuex'
import KpiViews from './components/kpiViews'
import { getFieldOptions } from 'util/picklist'

export default {
  props: ['isInPageBuilder', 'details'],
  mixins: [NewDateHelper, AnalyticsMixin, ViewMixinHelper],
  components: {
    NewDatePicker,
    sparkline,
    Pagination,
    CommonListLayout,
    Search,
    Tags,
    NewKpi,
    KpiViews,
  },
  data() {
    return {
      loading: false,
      shimmerLoading: false,
      perPage: 50,
      totalCount: null,
      dateObj: null,
      list: [],
      viewState: {},
      views: ['HOURLY', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'],
      currentView: null,
      showPicker: true,
      groupBy: 'kpi',
      lastUpdatedTime: null,
      showCreationForm: false,
      isFilterOptionsLoading: true,
      filterConfig: {
        moduleName: 'formulaField',
        path: `/app/em/kpi/`,
        includeParentCriteria: true,
        excludeModuleStateField: true,
        data: {
          name: {
            label: this.$t('common._common.name'),
            displayType: 'string',
            value: '',
          },
          kpiCategory: {
            label: this.$t('common._common.category'),
            displayType: 'select',
            options: {},
            value: null,
          },
          siteId: {
            label: this.$t('common.products.site'),
            options: {},
            value: null,
            displayType: 'select',
            key: 'siteId',
          },
          space: {
            label: this.$t('common.space_asset_chooser.space'),
            displayType: 'space',
            options: {},
            value: [],
          },
          assetCategoryId: {
            label: this.$t('common._common.asset_category'),
            displayType: 'select',
            options: {},
            value: null,
          },
        },
        availableColumns: [
          'name',
          'kpiCategory',
          'siteId',
          'space',
          'assetCategoryId',
        ],
        fixedCols: ['name'],
        saveView: false,
      },
    }
  },
  created() {
    this.intiViewStateObj()
    this.loadFilterOptions()
  },
  computed: {
    ...mapState({
      showSearch: state => state.search.active,
    }),
    defaultView() {
      return this.views[1]
    },
    currentViewState() {
      let { currentView } = this
      let viewState = this.viewState[currentView]

      return isEmpty(viewState)
        ? this.viewState[this.defaultView]
        : { ...viewState }
    },
    page() {
      return this.$route.query.page || 1
    },
    selectedKpiId() {
      return this.isInPageBuilder && this.details.id
    },
    isSpaceKpi() {
      let { matchedResources = [] } = this.details
      let firstMatchedResource = matchedResources[0] || {}

      return firstMatchedResource.resourceTypeEnum === 'SPACE'
    },
    kpiType() {
      return this.isInPageBuilder
        ? this.isSpaceKpi
          ? 'space'
          : 'asset'
        : 'all'
    },
    isGroupedByKpi() {
      return this.groupBy === 'kpi'
    },
    filters() {
      let { $route } = this
      let {
        query: { search },
      } = $route || {}

      if (!isEmpty(search)) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    sparkLineStyleObj() {
      let width = 400
      let height = 55
      let { $refs, loading } = this

      if (!loading && !isEmpty($refs['sparkline-trend'])) {
        width = $refs['sparkline-trend'][0].scrollWidth
      }

      return {
        width,
        height,
        style: {
          stroke: '#886cff',
          fill: '#886cff',
        },
        targetLineStyle: {
          stroke: '#d14',
          strokeOpacity: 1,
          strokeDasharray: '2, 2',
        },
      }
    },
    watchedProps() {
      return this.page, this.kpiType, this.viewState, this.groupBy, Date.now()
    },
  },
  watch: {
    watchedProps() {
      this.loadData()
    },
    page() {
      this.loadData()
    },
    filters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.loadData()
        }
      },
    },
  },
  methods: {
    intiViewStateObj() {
      this.views.forEach(view => {
        this.$set(this.viewState, view, viewStateMeta[view])
      })
      this.currentView = this.defaultView

      let { frequency = null, groupBy = null } = this.$route.query

      if (frequency && this.views.includes(frequency)) {
        this.currentView = frequency
      }

      if (groupBy && ['kpi', 'resource'].includes(groupBy)) {
        this.groupBy = groupBy
      }

      this.setPicker()
    },

    syncTabInQuery() {
      this.$router.replace({
        query: {
          ...this.$route.query,
          frequency: this.currentView,
        },
      })
    },

    syncGroupingInQuery() {
      this.$router.replace({
        query: {
          ...this.$route.query,
          groupBy: this.groupBy,
        },
      })
    },

    switchView(view) {
      this.currentView = view

      this.syncTabInQuery()
      this.loadData()
    },

    handleViewTabClick(view) {
      this.switchView(view)
      this.setPicker()
    },

    setPicker() {
      this.showPicker = false
      this.dateObj = NewDateHelper.getDatePickerObject(
        this.currentViewState.operatorID,
        '' + this.currentViewState.start
      )
      this.$nextTick(() => (this.showPicker = true))
    },

    changeGrouping(isGroupedByKpi) {
      if (isGroupedByKpi) this.groupBy = 'resource'
      else this.groupBy = 'kpi'

      this.syncGroupingInQuery()
    },

    async loadFilterOptions() {
      this.isFilterOptionsLoading = true

      let { error, options } = await getFieldOptions({
        field: {
          lookupModuleName: 'kpiCategory',
          skipDeserialize: true,
        },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this.filterConfig.data, 'kpiCategory', {
          ...this.filterConfig.data.kpiCategory,
          options,
        })
        this.isFilterOptionsLoading = false
      }
    },

    getUrl() {
      let {
        kpiType,
        currentViewState = {},
        page,
        perPage,
        groupBy,
        filters,
        selectedKpiId,
      } = this

      let filterObj = { ...filters }
      let { resource } = filterObj
      let url = `/v2/kpi/viewer/${kpiType}?`

      let parsedFilters
      if (!isEmpty(selectedKpiId)) {
        filterObj = filterObj || {}
        filterObj.id = { operatorId: 9, value: [`${selectedKpiId}`] }
      }
      if (!isEmpty(filterObj)) {
        if (filterObj.resource) delete filterObj.resource
        parsedFilters = encodeURIComponent(JSON.stringify(filterObj))
      }

      if (!isEmpty(parsedFilters)) url = `${url}filters=${parsedFilters}&`

      let { frequencyId = null } = currentViewState

      if (this.isInPageBuilder) {
        frequencyId = this.details.frequency
      }

      if (isEmpty(frequencyId)) {
        return
      }

      let params = {
        frequency: frequencyId,
        page: page,
        perPage: perPage,
        groupBy: groupBy,
      }

      if (!isEmpty(resource)) params.resource = resource[0].value

      return Object.keys(params).reduce((res, key) => {
        res += key + '=' + params[key] + '&'
        return res
      }, url)
    },

    loadData() {
      let url = this.getUrl()
      if (isEmpty(url)) return

      this.loading = true

      this.$http
        .get(url)
        .then(({ data }) => {
          this.loading = false

          if (data.responseCode === 0) {
            let { lastUpdatedTime, kpis = [] } = data.result

            let items = kpis.map(item => {
              this.$set(item, 'canShow', true)
              return item
            })

            this.list = items
            this.lastUpdatedTime = moment(lastUpdatedTime)
              .tz(Vue.prototype.$timezone)
              .format('ddd DD MMM YYYY')
            this.$nextTick(() => {
              this.loadViolationsCount()
              this.loadChartData()
            })
          } else {
            this.list = []
            this.lastUpdatedTime = null
          }
        })
        .catch(() => {
          this.list = []
          this.lastUpdatedTime = null
          this.loading = false
        })

      this.$http.get(`${url}&fetchCount=true`).then(({ data }) => {
        if (data.responseCode === 0) {
          this.totalCount = data.result.count
        }
      })
    },

    loadViolationsCount() {
      let { list, dateObj } = this
      let idMap = list.reduce((acc, item) => {
        if (item.data) {
          item.data.forEach(({ id, resourceId }) => {
            acc.push(`${id}_${resourceId}`)
          })
        }
        return acc
      }, [])

      return this.$http
        .post(`/v2/kpi/viewer/violations`, {
          kpiResourceIds: idMap,
          dateRange: {
            startTime: dateObj.value[0],
            endTime: dateObj.value[1],
          },
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            let {
              result: { violations },
            } = data

            list.forEach(item => {
              item.data &&
                item.data.forEach(resource => {
                  let { id, resourceId } = resource
                  let key = `${id}_${resourceId}`

                  this.$set(resource, 'violations', violations[key])
                })
            })
          }
        })
        .catch(() => {
          this.$message.error(
            this.$t('common._common.could_not_fetch_violations')
          )
        })
    },

    loadChartData() {
      if (isEmpty(this.dateObj)) return

      let points = []
      this.list.forEach(({ data = [] }) => {
        data.forEach(resource => {
          points.push({
            parentId: [resource.resourceId],
            yAxis: {
              fieldId: resource.readingFieldId,
            },
          })
        })
      })
      this.setAlias(points)

      let params = {
        startTime: this.dateObj.value[0],
        endTime: this.dateObj.value[1],
        fields: JSON.stringify(points),
        analyticsType: 2,
        newFormat: true,
      }
      return this.$http
        .post('/v2/report/readingReport', params)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            let { result } = data
            let {
              reportData,
              reportData: { aggr },
              report,
            } = result
            let dataParams = this.constructChartData(reportData, report)

            this.list.forEach(({ data = [] }) =>
              data.forEach(resource => {
                let point = points.find(
                  point =>
                    point.parentId.includes(resource.resourceId) &&
                    point.yAxis.fieldId === resource.readingFieldId
                )
                let { aliases: { actual: alias } = {} } = point || {}

                if (alias) {
                  // Set Sparkline Data
                  let { sparkline = null } =
                    dataParams.find(p => p.alias === alias) || {}
                  this.$set(resource, 'sparkline', sparkline)

                  // Set other props from reportData
                  let props = ['avg', 'min', 'max', 'sum']
                  props.forEach(prop => {
                    let value = aggr[`${alias}.${prop}`]
                    value = !isEmpty(value)
                      ? Number.parseFloat(value).toFixed(2)
                      : null

                    !isEmpty(value) && this.$set(resource, prop, value)
                  })
                } else {
                  this.$set(resource, 'sparkline', null)
                }
              })
            )
          } else {
            //
          }
        })
        .catch(error => {})
    },

    constructChartData(reportData, report) {
      let { data } = reportData
      let { dataPoints } = report
      let dataParams = []

      if (!isEmpty(data) && !isEmpty(dataPoints)) {
        dataPoints.forEach(dataPoint => {
          let {
            aliases: { actual },
          } = dataPoint
          let sparkline = {}
          sparkline.dataObj = data.map(sparkData => Number(sparkData[actual]))
          sparkline.labelArray = data.map(sparkData => Number(sparkData['X']))

          let currentView = this.currentView
          sparkline.label = {
            formatter(val) {
              let date
              if (currentView === 'DAILY' || currentView === 'WEEKLY')
                date = formatter.formatCardTime(
                  sparkline.labelArray[val.index],
                  12,
                  22
                )
              else if (currentView === 'HOURLY')
                date = formatter.formatCardTime(
                  sparkline.labelArray[val.index],
                  20,
                  22
                )
              else if (currentView === 'MONTHLY' || currentView === 'YEARLY')
                date = moment(sparkline.labelArray[val.index])
                  .tz(Vue.prototype.$timezone)
                  .format('MMM YYYY')

              let templateValue = `<div style="padding:3px;">
                            <div>
                              <label>${date}</label>
                            </div>
                          </div>
                          <div>
                            <label style="color:#fff;font-weight:bold;">${val.value}</label>&nbsp;`
              return templateValue
            },
          }
          dataParams.push({ alias: actual, sparkline })
        })
      }
      return dataParams
    },

    toggleDetails(item) {
      item.canShow = !item.canShow
    },

    onDateChange(dateFilter) {
      let promises = []

      this.shimmerLoading = true
      this.$set(this, 'dateObj', dateFilter)

      promises.push(this.loadChartData())
      promises.push(this.loadViolationsCount())
      promises.push(this.$helpers.delay(3000))

      Promise.all(promises).then(() => {
        this.shimmerLoading = false
      })
    },

    getSafeLimit(target, mintarget) {
      if (!isEmpty(target) && !isEmpty(mintarget))
        return `${mintarget} - ${target}`
      else if (!isEmpty(target)) return `< ${target}`
      else if (!isEmpty(mintarget)) return `> ${mintarget}`
      else return '--'
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-rp-top-bar {
  padding: 10px 20px 10px;
}
.empty-container {
  background-color: #fff;
  height: calc(100vh - 190px);
}
.table-container {
  padding: 0 20px 20px;
  scroll-behavior: smooth;
  overflow: scroll;
  width: 100%;
  background: #fff;

  &.full-height {
    height: calc(100vh - 190px) !important;
  }

  table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0;
    table-layout: auto;
    border: solid 1px #dbe4ef;
    border-top: none;

    th,
    td {
      font-weight: 500;
      padding: 5px 0;
      border-bottom: 1px solid #eff2f5;

      &:not(:last-of-type) {
        border-right: 1px solid #e4ebf3;
      }
    }
    th {
      font-size: 11px;
      position: sticky;
    }
    td {
      font-size: 12px;
      //Required , only when td height is set , div inside cell respects 100% height
      height: 0 !important;
    }
  }

  .header {
    background: #ffffff;
    position: sticky;
    z-index: 5;
    top: 0;
    left: 0;
    border-top: 1px solid #dbe4ef;

    &.secondary {
      background-color: #f7f8f9;
    }
  }

  .header-cell {
    padding-top: 10px;
    padding-bottom: 10px;
    padding-left: 20px;
    padding-right: 10px;
  }

  .data-row {
    background-color: #fff;
    .cell {
      padding: 15px 20px 15px 20px;
      font-size: 12px;
      font-weight: normal;
    }
  }

  .separator-row {
    background-color: #f7f8f9;
    .cell {
      overflow: visible;
      white-space: nowrap;
      min-height: 42px;
      line-height: 30px;

      .el-icon-arrow-down,
      .el-icon-arrow-up {
        font-weight: 600;
        margin-left: 5px;
        margin-right: 5px;
        border-radius: 50%;
        color: #576161;
        background: #e4ecec;
        padding: 5px;
        text-align: center;
        cursor: pointer;
      }
    }
  }
}
.lines {
  display: flex;
  height: 20px;
  margin: 0px 20px 0px 20px;
  border-radius: 5px;
}
</style>
<style lang="scss">
.reading-kpi.summary-page {
  .view-header-container.subheader-section {
    padding: 0px;
    height: 0px;
  }
}
</style>
