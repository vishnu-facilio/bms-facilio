<template>
  <div
    :class="[
      'fc-pdf-report-print',
      (excludeTable || hideTableImage) && 'pdf-page-break',
      hideWigetLegend && 'hide-wiget-legend',
    ]"
    v-if="!localloading && appName"
  >
    <pdfPage>
      <template #content>
        <f-new-analytic-report-optimize
          ref="newAnalyticReport"
          :config.sync="analyticsConfig"
          :printView="true"
          :showHeadingOnlyForPdf="true"
          :baseLines="baseLineList"
          @reportLoaded="reportLoaded"
          @removeFilters="removeFilters"
          :showTimePeriod="analyticsConfig.mode === 1 ? true : false"
          :showFilterBar="true"
          :showChartMode="true"
          :noChartState="noChartState"
          v-if="isOptimize === false"
        ></f-new-analytic-report-optimize>
      </template>
    </pdfPage>
  </div>
</template>

<script>
import NewBuildingAnalysis from 'src/pages/energy/analytics/newTools/NewBuildingAnalysis'
import { getApp } from '@facilio/router'
import pdfPage from 'src/pdf/component/PdfPageComponent'
import OutApp from 'src/pdf/pages/mixin/AccountUtil'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [OutApp],
  extends: NewBuildingAnalysis,
  components: {
    pdfPage,
  },
  data() {
    return {
      alarmPdf: true,
      appName: getApp().linkName,
      excludeParamsObj: {},
    }
  },
  async created() {
    await this.loadQData()
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadAssetCategory')
    let { excludeParams = {} } = this.$route.query
    this.excludeParamsObj = !isEmpty(excludeParams)
      ? JSON.parse(excludeParams) ?? {}
      : {}

    if (this.isSiteAnalysis) {
      this.analyticsConfig.analyticsType = 6
    }
    if (this.$route.query.filters) {
      let filters = this.$route.query.filters
      let filtersJSON = JSON.parse(filters)
      this.analyticsConfig = deepmerge.objectAssignDeep(
        this.analyticsConfig,
        filtersJSON
      )
    }
    if (this.reportId || this.alarmId || this.cardId) {
      if (this.$mobile) {
        this.loadReport(this.mobileConfig)
      } else {
        this.loadReport()
      }
    }
    let self = this
    self.$http.get('/baseline/all').then(function(response) {
      if (response.status === 200) {
        self.baseLineList = response.data ? response.data : []

        self.baseLineCasecaderOptions = []
        self.baseLineCasecaderOptions.push({
          label: 'None',
          value: -1,
        })

        for (let b of self.baseLineList) {
          let children = null
          if (
            b.rangeTypeEnum === 'PREVIOUS_MONTH' ||
            b.rangeTypeEnum === 'ANY_MONTH'
          ) {
            children = [
              {
                label: 'Same date',
                value: 4,
              },
              {
                label: 'Same week',
                value: 1,
              },
            ]
          } else if (
            b.rangeTypeEnum === 'PREVIOUS_YEAR' ||
            b.rangeTypeEnum === 'ANY_YEAR'
          ) {
            children = [
              {
                label: 'Same date',
                value: 3,
              },
              {
                label: 'Same week',
                value: 1,
              },
            ]
          }

          if (b.rangeTypeEnum !== 'PREVIOUS') {
            self.baseLineCasecaderOptions.push({
              label: b.name,
              value: b.id,
              children: children,
            })
          }
        }
      }
    })
    self.loadZones()
  },
  computed: {
    excludeTable() {
      return this.excludeParamsObj.table
    },
    hideTableImage() {
      return this.analyticsConfig.hidetabular
    },
    hideWigetLegend() {
      return this.excludeParamsObj.widgetLegend
    },
  },
  mounted() {
    this.analyticsConfig.hidedatepicker = true
    this.analyticsConfig.hidechartoptions = true
    this.analyticsConfig.hidecharttypechanger = true
    this.analyticsConfig.hideColumn = true
    const { showOnlyImage } = this.$route.query
    if (!isEmpty(showOnlyImage) && JSON.parse(showOnlyImage)) {
      this.analyticsConfig.hidetabular = true
    }
  },
}
</script>
<style lang="scss">
.fc-pdf-report-print {
  .max-width350px {
    max-width: 350px;
    white-space: normal;
    word-break: break-word;
  }
  .mT20 {
    margin-top: 0px !important;
  }
  .fc-black-small-txt-12 {
    display: none;
  }
  .fc-grey-svg2 {
    display: none;
  }
  .button-row {
    display: none;
  }
  margin-left: auto;
  margin-right: auto;
  max-width: 1200px;
  .report-tab {
    .el-tabs__header {
      display: none;
    }
  }

  .report-pdf-header {
    height: 60px;
    justify-content: center;
    position: relative;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #2d394c;
  }
  .fc-logo-report {
    display: block;
    position: absolute;
    left: 0px;
  }
  .f-singlechart .fc-new-chart-type-single {
    display: none;
  }
  .f-tabular-page .tabular-report-table th {
    height: auto !important;
    color: #000 !important;
    font-weight: 600 !important;
    font-size: 0.65rem !important;
    background: #f6fbff !important;
    border: 1px solid #333 !important;
    text-align: center !important;
    padding: 0px 0px !important;
  }
  .tabular-report-table .tabular-data-td {
    text-align: center;
    min-width: 49px;
    max-width: 49px;
    color: #333 !important;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1px;
    padding: 13px 10px !important;
  }
  .tabular-report-table {
    border-collapse: collapse;
    border: 1px solid #000;
    width: 100%;
  }
  .tabular-report-table .tabular-data-td {
    color: #333 !important;
    border: 1px solid #333 !important;
  }
  .tablerow:nth-child(odd) {
    background-color: #fff;
  }
  .tablerow:nth-child(even) {
    background-color: #f0f9fb;
  }
  .report-user-filters {
    display: none;
  }
  .fc-report-cus-logo {
    display: block !important;
    position: absolute;
    left: 0px;
  }
  .fc-report-author-sec {
    display: block;
  }
  .fc-report-author-txt {
    font-size: 9px;
    font-weight: normal;
    line-height: 16px;
    letter-spacing: normal;
    color: #000000;
  }
}
@media print {
  .hide-wiget-legend {
    .widget-legends {
      display: none !important;
    }
  }
  .pdf-page-break {
    .fc-new-chart {
      page-break-after: avoid !important;
    }
  }
  .fc-pdf-report-print {
    .fc-new-chart {
      page-break-after: avoid !important;
    }
    .f-tabular-page {
      page-break-before: always;
    }
    .tablular-container {
      page-break-before: avoid;
      page-break-after: avoid;
    }
    table {
      border: none !important;
    }
    * {
      background: none;
    }
    .fc-alarms-chart {
      display: block !important;
    }
    .widgetleagend-slider {
      display: none;
    }
    .is-active {
      display: inline-flex;
    }
  }
  @page {
    margin: 0px;
  }
}
</style>
