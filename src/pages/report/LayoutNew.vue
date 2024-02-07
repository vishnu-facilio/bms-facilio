<template>
  <div class="row reports-layout">
    <div class="col-3" id="sideBar">
      <report-sidebar
        ref="reportSidebar"
        :moduleName="$attrs.moduleName"
      ></report-sidebar>
    </div>
    <div class="col-9 reports-summary" id="printable-area">
      <router-view @reportDeleted="handleReportDelete" />
    </div>
  </div>
</template>
<script>
import ReportSidebar from './ReportSidebar'
export default {
  title() {
    return 'Reports'
  },
  components: {
    ReportSidebar,
  },
  methods: {
    handleReportDelete(obj) {
      if (this.$refs['reportSidebar']) {
        this.$refs['reportSidebar'].removeReport(obj)
      }
    },
  },
}
</script>
<style>
@media print {
  #printable-area {
    visibility: visible !important;
    width: 100% !important;
    max-width: 100% !important;
    min-width: 100% !important;
    padding: 0px !important;
  }
  #printable-area .view-column-chooser {
    display: none;
  }
  .fc-underlyingdata .fc-chart-table {
    overflow: hidden !important;
    table-layout: fixed;
  }
  .reports-underlyingdata {
    /* doubt */
    padding: 0px;
  }
  .reports-header {
    box-shadow: none !important;
  }
  #sideBar,
  .fc-layout-aside,
  .layout-header,
  .report-options,
  .title-actions,
  .charttype-options,
  .emptyLegends {
    display: none;
  }
  .height100.pL50.pL60 {
    padding: 0px !important;
  }
  .legend.legendsAll {
    max-width: 800px !important;
    margin: auto;
  } /* doubt */
  .printedTime {
    display: block !important;
  }
  .layout-page-container {
    padding-top: 0px !important;
  }
  #printable-area {
    page-break-after: auto;
  }
  table {
    page-break-after: auto;
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  thead {
    display: table-header-group;
  }
  tfoot {
    display: table-footer-group;
  }
  main.layout-page {
    min-height: 100% !important;
    height: 100% !important;
  }
  .self-center.fchart-section {
    margin-right: 100px !important;
    width: 100% !important;
  }
  svg.fchart {
    zoom: 80%;
  }
  .fc-report-filter,
  .fc-underlin,
  .table-header {
    display: none !important;
  }
  .chart-table-layout {
    padding-bottom: 0px !important;
  }
  .reports-summary .fc-report-section,
  .fc-underlyingdata {
    box-shadow: none !important;
  }
  .reports-summary .fc-report-section {
    box-shadow: none !important;
    border: solid 0px #e6ebf0;
  }
  .reports-summary .reports-chart {
    padding-left: 0px;
    padding-right: 0px;
    padding-top: 0px;
    border: none;
  }
  .reports-summary .fc-underlyingdata {
    border: solid 1px #e6ebf0;
    box-shadow: none;
  }
  .row,
  .filter-field .pdfDateView,
  .filter-field .Reading-Analysis {
    display: block;
  }
  .scrollable {
    overflow: visible;
  }
  .scrollable {
    padding-top: 0px !important;
  }
  .report-title {
    width: 100% !important;
  }
  .report-title .title,
  .report-title .description {
    text-align: center !important;
  }
  .reports-header {
    border-bottom: none !important;
  }
  .chart-option {
    display: inline-flex !important;
  }
  body {
    -webkit-print-color-adjust: exact;
    color-adjust: exact;
  }
  .chart-tooltip {
    opacity: 0 !important;
  }
  .reports-summary .fc-underlyingdata,
  .reports-summary .fc-report-section {
    box-shadow: none;
  }
  .reports-summary .reports-chart,
  .reports-underlyingdata {
    background: #fff;
  }
}
</style>
