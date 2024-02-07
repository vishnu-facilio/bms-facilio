<template>
  <div
    :class="[
      'fc-pdf-report-print',
      excludeTable && 'pdf-page-break',
      hideWigetLegend && 'hide-wiget-legend',
    ]"
    v-if="!localloading && appName"
  >
    <pdfPage>
      <template #content>
        <FNewReport
          :id="id"
          :showHeadingOnlyForPdf="true"
          :printView="true"
          :hideChartSettings="true"
          :hideColumn="true"
          :showChart="showChart"
          @hideChart="showChart = true"
        ></FNewReport>
      </template>
    </pdfPage>
  </div>
</template>
<script>
import FNewReport from 'pages/report/components/FNewReport.vue'
import pdfPage from 'src/pdf/component/PdfPageComponent'
import OutApp from 'src/pdf/pages/mixin/AccountUtil'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
export default {
  mixins: [OutApp],
  data() {
    return {
      appName: getApp().linkName,
      showChart: null,
      excludeParamsObj: {},
      showOnlyImage: false,
    }
  },
  created() {
    this.loadQData()
    this.showOnlyImage = JSON.parse(this.$route.query.showOnlyImage)
    let { excludeParams = {} } = this.$route.query
    this.excludeParamsObj = !isEmpty(excludeParams)
      ? JSON.parse(excludeParams) ?? {}
      : {}
  },
  components: {
    FNewReport,
    pdfPage,
  },
  computed: {
    hideWigetLegend() {
      return this.excludeParamsObj.widgetLegend
    },
    excludeTable() {
      return this.excludeParamsObj.table || this.showOnlyImage
    },
    id() {
      return this.$getProperty(this.$route, 'params.id', null)
    },
  },
  methods: {},
}
</script>
<style lang="scss">
.fc-pdf-report-print {
  .max-width350px {
    max-width: 350px;
    white-space: normal;
    word-break: break-word;
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
  .fc-logo-report {
    display: block;
    position: absolute;
    left: 0px;
  }
  .report-pdf-header {
    height: 60px;
    justify-content: center;
    position: relative;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #2d394c;
  }
  .f-singlechart .fc-new-chart-type-single {
    display: none;
  }
  .f-tabular-report-new .tabular-report-table th {
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
  .fc-pdf-report-print {
    .widgetleagend-slider {
      display: none;
    }
    .is-active {
      display: inline-flex;
    }
  }
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
  .f-singlechart .fc-new-chart-type-single {
    display: none;
  }
  @page {
    margin: 0px;
  }
}
</style>
