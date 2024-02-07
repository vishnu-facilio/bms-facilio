<template>
  <div
    class="d-flex flex-direction-column inspection-count-insight"
    ref="inspection-count-insight"
  >
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header display-flex-between-space width100 pR10 pL10 items-center"
      >
        <div class="widget-header-name f12">{{ widgetTitle }}</div>

        <new-date-picker
          :zone="$timezone"
          class="filter-field date-filter-comp"
          :dateObj="dateObj"
          @date="changeDateFilter"
          :isDateFixed="isDateFixed"
        ></new-date-picker>
      </div>
    </portal>

    <div v-if="!isColorEmpty" class="flex p10 justify-center">
      <div v-if="!reportLoading" class="main-container">
        <div class="legend-container"></div>
        <template v-if="!$validation.isEmpty(colorMap)">
          <div
            v-for="(legend, index) in colorMap"
            :key="index"
            class="legend-container"
          >
            <div class="d-flex align-items-center">
              <div
                class="legend-color"
                :style="`background-color:${legend.color}`"
              ></div>
              <div class="legend-label">{{ legend.fieldName }}</div>
            </div>
            <div class="legend-label-count">Count: {{ legend.count }}</div>
            <div class="legend-label-count">
              Percentage: {{ getPercent(legend.count, totalResponses) }} %
            </div>
          </div>
        </template>
      </div>
      <div
        :class="[
          'report-container',
          loading && 'width100',
          $validation.isEmpty(colorMap) && 'pR0',
        ]"
        v-if="!$validation.isEmpty(moduleMeta) && isChartPrepared && !loading"
      >
        <f-new-analytic-modular-report
          :serverConfig.sync="serverConfig"
          :module="moduleObj"
          :defaultChartType="type || 'donut'"
          :hideTabs="true"
          :hideHeader="true"
          :hidecharttypechanger="true"
          :chartType="type"
          :isWidget="true"
          :width="500"
          :height="500"
          :showPeriodSelect="showPeriodSelect"
          @reportLoaded="onReportLoaded"
        ></f-new-analytic-modular-report>
      </div>
    </div>

    <div v-else class="empty-state-report">
      <inline-svg
        src="svgs/emptystate/reportlist"
        iconClass="icon text-center icon-100"
      ></inline-svg>
      <div class="nowo-label f13">
        {{ $t('common.wo_report.empty_data') }}
      </div>
    </div>
  </div>
</template>
<script>
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'InspectionCountInsight',
  extends: CommonWidgetChart,
  props: ['moduleName'],
  mounted() {
    this.$nextTick(() => {
      let container = this.$refs['inspection-count-insight']
      if (container) {
        let width = container.scrollWidth
        if (this.resizeWidget) {
          this.resizeWidget({ height: 480, width })
        }
      }
    })
  },
  data() {
    return {
      colorMap: [],
      isColorEmpty: false,
      reportLoading: true,
    }
  },
  watch: {
    serverConfig: {
      handler() {
        if (!isEmpty(this.customizeChart)) {
          this.$set(
            this.serverConfig,
            'customizeChartOptions',
            this.customizeChart
          )
        }
      },
    },
  },
  computed: {
    customizeChart() {
      let { details } = this
      let { qandAType } = details || {}
      let secondaryText =
        qandAType === 'INDUCTION' ? 'Total Inductions' : 'Total Inspections'
      return {
        donut: {
          centerText: {
            primaryText: '_sum',
            secondaryText,
          },
        },
      }
    },
    totalResponses() {
      let { details } = this || {}
      let { totalResponses } = details || {}
      return totalResponses
    },
    currModuleName() {
      let { moduleName } = this
      if (moduleName.includes('induction')) return 'inductionResponse'
      else if (moduleName.includes('survey')) return 'surveyResponse'
      else return 'inspectionResponse'
    },
    widgetTitle() {
      let { moduleName } = this
      if (moduleName.includes('induction')) {
        return `${this.$t('qanda.template.induction')} ${this.$t(
          'qanda.template.by_status'
        )}`
      } else if (moduleName.includes('survey')) {
        return `${this.$t('qanda.template.survey')} ${this.$t(
          'qanda.template.by_status'
        )}`
      } else {
        return `${this.$t('qanda.template.inspection')} ${this.$t(
          'qanda.template.by_status'
        )}`
      }
    },
  },
  methods: {
    async getMeta() {
      this.loading = true
      let { currModuleName } = this
      return API.get(`/module/meta?moduleName=${currModuleName}`).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            let { meta } = data || {}
            let moduleId = this.$getProperty(meta, 'module.moduleId')
            this.moduleMeta = meta

            this.moduleObj = {
              moduleName: meta.name,
              moduleId: moduleId,
              meta: {
                fieldMeta: {},
              },
            }

            this.loading = false
          }
        }
      )
    },
    onReportLoaded(report) {
      let { options, data } = report || {}
      let { xColorMap } = options || {}
      let { Id } = data || {}
      if (!isEmpty(xColorMap) && !isEmpty(Id)) {
        this.colorMap = Object.keys(xColorMap).map((fieldName, index) => {
          return { fieldName, color: xColorMap[fieldName], count: Id[index] }
        })
      }
      if (isEmpty(data)) this.isColorEmpty = true
      this.reportLoading = false
    },
    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter
      this.isColorEmpty = false
      this.reportLoading = true
      eventBus.$emit('date-change-inspection', dateFilter)
    },
    getPercent(value, total) {
      return ((Number(value) / Number(total)) * 100).toFixed(0)
    },
  },
}
</script>

<style lang="scss">
.inspection-count-insight {
  .legend-container {
    margin: 10px 30px;
  }
  .height375px {
    height: 375px;
  }
  .main-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding-top: 80px;
    width: 220px;
  }
  .report-container {
    width: 360px;
    height: 400px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .legend-color {
    border-radius: 3px;
    height: 15px;
    width: 15px;
    margin-right: 8px;
  }
  .legend-label {
    font-size: 14px;
  }
  .legend-label-count {
    font-size: 14px;
    margin-left: 23px;
    margin-top: 5px;
  }
  .fLegendContainer {
    display: none;
  }
  .empty-state-report {
    margin-top: 30px;
    font-size: 13px;
    padding: 50px;
    line-height: 25px;
    text-align: center;
  }
  .inspection-total-respomse {
    display: flex;
    position: absolute;
    z-index: 99;
    top: 265px;
    right: 158px;
    flex-direction: column;
    align-items: center;
  }
  .count {
    font-size: 42px;
    color: #3ab2c2;
  }
  .label {
    font-size: 18px;
    color: #000000;
  }
}
</style>
