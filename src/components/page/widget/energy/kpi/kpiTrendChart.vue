<template>
  <div class="d-flex flex-direction-column">
    <div class="width100 border-bottom1px d-flex justify-content-space">
      <div class="f16 bold mT20 mB20 mL30 inline">Historical Trend Chart</div>
      <!-- <div class="f12 font-normal mT20 mR30">
        <a>Go to Analytics</a>
      </div>-->
    </div>
    <div class="pT15 position-relative flex-grow kpi-trend-chart">
      <el-select
        class="fc-input-full-border-select2 pL20 pT5 position-absolute width290px"
        v-if="matchedResources.length > 1"
        v-model="selectedResourceIds"
        multiple
        collapse-tags
        placeholder="Select Resources"
      >
        <el-option
          v-for="item in matchedResources"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>

      <spinner v-if="chartloading" :show="chartloading" size="80"></spinner>

      <f-new-analytic-report
        class="mT0"
        v-if="!loading && !$validation.isEmpty(analyticsConfig.dataPoints)"
        v-show="!chartloading"
        ref="kpiTrendChart"
        :config.sync="analyticsConfig"
        @reportLoaded="chartloading = false"
      ></f-new-analytic-report>
    </div>
  </div>
</template>
<script>
import FNewAnalyticReport from 'pages/energy/analytics/newTools/v1/FNewAnalyticsReportV1'
import { isEmpty } from '@facilio/utils/validation'
import NewDateHelper from '@/mixins/NewDateHelper'

export default {
  components: {
    FNewAnalyticReport,
  },
  mixins: [NewDateHelper],
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'widget',
    'resizeWidget',
    'moduleName',
  ],
  data() {
    return {
      loading: false,
      chartloading: true,
      fields: null,
      selectedResourceIds: [],
      analyticsConfig: {
        chartType: 'line',
        point: {
          show: false,
        },
        axis: {
          y: {
            label: `${(this.details.name || '').toUpperCase()} ${
              this.details.unit ? '(' + this.details.unit + ')' : ''
            }`,
          },
        },
        mode: 1,
        period: 10,
        dataPoints: [],
        dateFilter: NewDateHelper.getDatePickerObject(44, null),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        hideLegends: true,
        fixedChartHeight: 350,
        zoom: {
          enabled: false,
        },
      },
    }
  },
  created() {
    this.setDefaultProps()
    this.prepareChart()
  },
  computed: {
    isSpaceKpi() {
      let { assetCategoryId = null, matchedResourcesIds = [] } = this.details
      let firstMatchedResourceId = matchedResourcesIds[0]

      return (
        isEmpty(assetCategoryId) &&
        !isEmpty(this.$store.getters.getSpace(firstMatchedResourceId))
      )
    },
    matchedResources() {
      let { matchedResources = [] } = this.details
      return matchedResources
    },
  },
  methods: {
    setDefaultProps() {
      this.selectedResourceIds = this.details.matchedResourcesIds.slice(0, 10)
    },
    prepareChart() {
      let { selectedResourceIds, analyticsConfig: config } = this

      if (!isEmpty(selectedResourceIds)) {
        let points = []
        this.selectedResourceIds.forEach(resourceId =>
          points.push({
            parentId: resourceId,
            yAxis: {
              fieldId: this.details.readingFieldId,
              aggr: 3,
            },
          })
        )

        config.dataPoints = points
        this.$set(this, 'analyticsConfig', config)
      }
    },
  },
  watch: {
    selectedResourceIds() {
      this.prepareChart()
    },
  },
}
</script>
<style lang="scss">
.kpi-trend-chart {
  .new-analytics-filter-section {
    text-align: right;
  }
}
</style>
