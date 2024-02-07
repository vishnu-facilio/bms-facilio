<template>
  <div class="d-flex flex-direction-column">
    <div class="width100 border-bottom1px d-flex justify-content-space">
      <div class="f16 bold mT20 mB20 mL30 inline">Lastest KPI Values</div>
    </div>
    <div class="pT25 position-relative flex-grow kpi-bubble-chart">
      <el-select
        class="fc-input-full-border-select2 position-absolute pL20 pT5 width290px"
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
        class="mT15"
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
import AnalayticsFilter from 'pages/energy/analytics/components/AnalayticsFilter'

export default {
  components: {
    FNewAnalyticReport,
  },
  mixins: [NewDateHelper, AnalayticsFilter],
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'activeTab',
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
        chartType: 'bubble',
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
        mode: 8,
        period: 10,
        dataPoints: [],
        dateFilter: NewDateHelper.getDatePickerObject(44, null),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        hideLegends: true,
        fixedChartHeight: 385,
        zoom: {
          enabled: false,
        },
      },
      // Maps the frequencies in constant -> FACILIO_FREQUENCY to periods in
      // FNewAnalyticReportV1
      frequencyToPeriodMap: {
        1: {
          frequency: 'Daily',
          period: 12,
          dateOperator: 22,
        },
        2: {
          frequency: 'Weekly',
          period: 11,
          dateOperator: 31,
        },
        3: {
          frequency: 'Monthly',
          period: 10,
          dateOperator: 44,
        },
        4: {
          frequency: 'Quarterly',
          period: 10,
          dateOperator: 44,
        },
        5: {
          frequency: 'Half Yearly',
          period: 10,
          dateOperator: 44,
        },
        6: {
          frequency: 'Annually',
          period: 10,
          dateOperator: 44,
        },
        8: {
          frequency: 'Hourly',
          period: 20,
          dateOperator: 28,
        },
      },
    }
  },
  created() {
    this.prepareChart()
    this.setDefaultProps()
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
      let { matchedResources } = this.details
      return matchedResources
    },
  },
  methods: {
    setDefaultProps() {
      this.selectedResourceIds = this.details.matchedResourcesIds.slice(0, 10)

      let { dateOperator, period } = this.frequencyToPeriodMap[
        this.details.frequency
      ]
      if (dateOperator) {
        this.analyticsConfig.dateFilter = NewDateHelper.getDatePickerObject(
          dateOperator,
          null
        )
      }
      if (period) {
        this.analyticsConfig.period = period
      }
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
.kpi-bubble-chart {
  .new-analytics-filter-section {
    text-align: right;
  }
}
</style>
