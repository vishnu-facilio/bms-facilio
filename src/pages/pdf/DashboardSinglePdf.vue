<template>
  <div
    class="height100 scrollable header-sidebar-hide"
    :class="{
      'header-sidebar-hide': $route.path === 'pdf/summarydownloadreport',
    }"
  >
    <div class="dashboard-pdf-con">
      <div class="dashboard-pdf-header">
        <div class="dashboard-pdf-cus-logo">
          <div v-show="$org.logoUrl">
            <img :src="$org.logoUrl" style="width: 100px;" />
          </div>
        </div>
        <div class="dashboard-pdf-heading">
          <div class="heading-black18 text-center fw-bold fc-black">
            Dashboard 1
          </div>
        </div>
        <div class="dashboard-pdf-fac-logo">
          <img v-if="showLogo" :src="getFacilioLogoURL" />
        </div>
      </div>
      <!-- pdf main section -->
      <div class="dashboard-pdf-main-con">
        <dashboardviewer
          :currentDashboard="{
            linkName: 'myworkload',
            moduleName: 'workorder',
          }"
        ></dashboardviewer>
      </div>
    </div>
  </div>
</template>
<script>
import dashboardviewer from 'src/pages/dashboard/DashboardViewer'
import ReportHelper from 'pages/report/mixins/ReportHelper'
export default {
  mixins: [ReportHelper],
  components: {
    dashboardviewer,
  },
  computed: {
    showLogo() {
      let { brandConfig } = this
      let showLogoInPDF = this.$getProperty(brandConfig, 'showLogoInPDF', true)
      return showLogoInPDF
    },
    brandConfig() {
      return window.brandConfig
    },
    getFacilioLogoURL() {
      let { brandConfig } = this
      let logo = this.$getProperty(brandConfig, 'logo', null)
      return logo
    },
  },
}
</script>

<style scoped lang="scss">
.header-sidebar-hide {
  width: 100%;
  height: 100vh;
  padding-left: 0 !important;
  background: #fff;
  padding-bottom: 50px;
  overflow-y: scroll;
  overflow-x: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999999;
}

.dashboard-pdf-con {
  width: 100%;
  overflow: visible;
  margin-top: 20px;
  margin-left: auto;
  margin-right: auto;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
}

.dashboard-pdf-header {
  width: 100%;
  height: 50px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding-left: 50px;
  padding-right: 50px;
}

.dashboard-pdf-heading {
  width: 60%;
}
.dashboard-pdf-fac-logo {
  width: 20%;
  text-align: right;
}
.dashboard-pdf-cus-logo {
  float: left;
  width: 20%;
}

.dashboard-pdf-main-con {
  margin: 0px 30px 0 30px;
}
.externalLink {
  display: none;
}
@media print {
  @page {
    size: landscape;
    overflow: visible !important;
  }
  .pm-planner-pdf-con {
    width: 100%;
    height: 100%;
    overflow-y: scroll;
    display: block;
    page-break-inside: avoid;
    page-break-after: auto;
  }
  * {
    margin: 0 !important;
    padding: 0 !important;
  }
  .header-sidebar-hide {
    height: 100% !important;
    position: relative !important;
  }
  html,
  body {
    background: #fff;
    display: block;
    width: auto;
    height: auto;
    page-break-after: avoid;
    page-break-before: avoid;
    -webkit-print-color-adjust: exact !important;
  }
  svg {
    -webkit-print-color-adjust: exact !important;
  }
  .scrollable {
    overflow: visible !important;
    margin-top: 0 !important;
    padding-top: 0px !important;
  }
  .normal main {
    display: block;
    width: auto;
    height: auto;
  }
  .scrollable {
    margin-top: 0 !important;
    padding-top: 0px !important;
  }
  table {
    page-break-inside: auto;
    page-break-after: auto;
  }
  tr {
    page-break-inside: avoid !important;
    page-break-after: auto !important;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  tfoot {
    display: table-footer-group;
  }
  .fc-widget-label {
    font-size: 14px !important;
  }
  .fc-widget-sublabel {
    font-size: 12px !important;
  }
  .fc-underline {
    display: none;
  }
  .dashboard-container .date-filter-comp {
    top: 0;
  }
}
</style>
