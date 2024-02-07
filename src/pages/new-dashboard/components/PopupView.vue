<template>
  <SetReadingPopup
    v-if="link && link.type === 'control'"
    :saveAction="onSetControlValue"
    :closeAction="resetControlValue"
    :recordId="link.resourceId"
    :fieldId="link.fieldId"
    :groupId="link.groupId"
    :recordName="''"
  ></SetReadingPopup>
  <el-dialog
    v-else-if="link"
    custom-class="f-popup-view"
    top="0%"
    :visible.sync="showDialog"
  >
    <template>
      <span slot="title" style="font-size: 18px; font-weight: 500;">
        {{ popupTitle ? popupTitle : 'Loading...' }}
      </span>
      <iframe
        v-if="link.type === 'url'"
        style="border: 0px;"
        :src="link.url"
        class="f-popup-iframe"
        width="100%"
        height="100%"
      ></iframe>
      <template
        v-if="
          link.type === 'report' &&
            link.newReport &&
            [1, 4].includes(link.newReport.type)
        "
      >
        <div
          class="content analytics-txt"
          style="cursor: pointer; color: rgb(57, 178, 194); font-size: 13px; text-align: right; font-weight: 500; margin-right: 20px;"
          @click="openInAnalytics"
        >
          Go to Analytics
          <img
            style="width:13px; height: 9px;"
            src="~statics/icons/right-arrow.svg"
          />
        </div>
        <f-new-report
          :id="parseInt(link.reportId)"
          @reportLoaded="reportLoaded"
          :qs="link.qs"
          :hideChartSettings="true"
          :dbFilterJson="link.dbFilterJson"
          :dbTimelineFilter="link.dbTimelineFilter"
        ></f-new-report>
      </template>
      <template v-if="link.type === 'pivot'">
        <div class="h100">
          <pivot-table-wrapper
            :reportId="link.reportId"
            :dbFilterJson="link.dbFilterJson"
            :dbTimelineFilter="link.dbTimelineFilter"
          />
        </div>
      </template>
      <module-new-report
        :module="link.newReport.module.name"
        v-if="
          link.type === 'report' && link.newReport && link.newReport.type === 2
        "
        :id="parseInt(link.reportId)"
        @reportLoaded="reportLoaded"
        :qs="link.qs"
        :dbFilterJson="link.dbFilterJson"
        :dbTimelineFilter="link.dbTimelineFilter"
        :loadImmediately="true"
      ></module-new-report>
      <dashboard-viewer
        v-if="link.type === 'dashboard'"
        :currentDashboard="dashboard"
      ></dashboard-viewer>
      <f-graphics-builder
        v-if="link.type === 'graphics' && link.graphicsContext"
        :graphicsContext="link.graphicsContext"
        readonly="true"
      ></f-graphics-builder>
      <f-graphics-builder
        v-else-if="link.type === 'graphics'"
        :defaultAsset="link.assetId"
        :graphicsContext="{ id: link.graphicsId }"
        readonly="true"
      ></f-graphics-builder>
      <template v-if="link.type === 'trend'">
        <div
          class="content analytics-txt"
          style="cursor: pointer; color: rgb(57, 178, 194); font-size: 13px; text-align: right; font-weight: 500; margin-right: 20px;"
          @click="openSelectedReadingInAnalytics"
        >
          Go to Analytics
          <img
            style="width:13px; height: 9px;"
            src="~statics/icons/right-arrow.svg"
          />
        </div>
        <f-new-analytic-report
          :config.sync="link.analyticsConfig"
        ></f-new-analytic-report>
      </template>
    </template>
  </el-dialog>
</template>

<script>
import FNewReport from 'pages/report/components/FNewReport'
import DashboardViewer from 'pages/dashboard/DashboardViewer'
import ModuleNewReport from 'src/pages/report/ModuleNewReport'
import SetReadingPopup from '@/readings/SetReadingValue'
import JumpToHelper from '@/mixins/JumpToHelper'
import PivotTableWrapper from 'src/pages/energy/pivot/PivotTableWrapper'

import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  name: 'PopupView',
  components: {
    FNewReport,
    DashboardViewer,
    ModuleNewReport,
    SetReadingPopup,
    FNewAnalyticReport: () =>
      import('pages/energy/analytics/components/FNewAnalyticReport'),
    FGraphicsBuilder: () => import('pages/assets/graphics/FGraphicsBuilder'),
    PivotTableWrapper,
  },
  mixins: [JumpToHelper],
  data() {
    return {
      showDialog: false,
      link: null,
      dashboard: null,
      report: null,
    }
  },
  watch: {
    showDialog: function() {
      if (!this.showDialog) {
        this.closePopup()
      }
    },
  },
  computed: {
    popupTitle() {
      if (this.link) {
        if (this.link.type === 'url') {
          return this.link.alt && this.link.alt.trim()
            ? this.link.alt
            : this.link.url
        } else if (this.link.type === 'dashboard') {
          return this.dashboard ? this.dashboard.dashboardName : ''
        } else if (this.link.type === 'report') {
          return this.report ? this.report.report.name : ''
        } else if (this.link.type === 'pivot') {
          return this.link.newReport.name
        } else if (this.link.title) {
          return this.link.title
        }
      }
      return 'Popup View'
    },
  },
  methods: {
    openPopup(link) {
      this.showDialog = true
      this.link = link
      if (this.link.type === 'dashboard') {
        this.loadDashboard(this.link.dashboardId)
      }
    },
    loadDashboard(linkName) {
      let dashboardLink = linkName.split('/')[0]
      let siteId = null
      let buildingId = null
      if (linkName.split('/').length > 1) {
        if (dashboardLink === 'buildingdashboard') {
          buildingId = linkName.split('/')[1]
        } else if (dashboardLink === 'sitedashboard') {
          siteId = linkName.split('/')[1]
        }
      }

      let url = '/dashboard/' + dashboardLink + '?moduleName=energydata'
      if (siteId) {
        url += '&siteId=' + siteId
      } else if (buildingId) {
        url += '&buildingId=' + buildingId
      }
      let self = this
      self.$http.get(url).then(function(response) {
        self.dashboard = {
          id: response.data.dashboardJson[0].id,
          dashboardName: response.data.dashboardJson[0].label,
          linkName: dashboardLink,
          moduleName: 'energydata',
          buildingId: buildingId,
          siteId: siteId,
          readOnly: false,
          screenSetting: null,
        }
      })
    },
    onSetControlValue(readingObj) {
      if (this.link && this.link.onSetControlValue) {
        this.link.onSetControlValue(readingObj)
      }
      this.closePopup()
    },
    resetControlValue(readingObj) {
      if (this.link && this.link.resetControlValue) {
        this.link.resetControlValue(readingObj)
      }
      this.closePopup()
    },
    openSelectedReadingInAnalytics() {
      if (
        this.link &&
        this.link.analyticsConfig.dataPoints &&
        this.link.analyticsConfig.dataPoints.length
      ) {
        this.jumpReadingsToAnalytics(
          this.link.analyticsConfig.dataPoints,
          null,
          null,
          null
        )
        this.closePopup()
      }
    },
    openInAnalytics() {
      if (this.report) {
        let { report, filters } = this.report || {}
        let { id, analyticsType } = report || {}
        let { xCriteriaMode } = filters || {}
        let path

        if (isWebTabsEnabled()) {
          let {
            ANALYTIC_BUILDING,
            ANALYTIC_PORTFOLIO,
            ANALYTIC_SITE,
            HEAT_MAP,
            TREE_MAP,
            REGRESSION,
          } = pageTypes
          let pathMap = {
            1: xCriteriaMode ? ANALYTIC_PORTFOLIO : ANALYTIC_BUILDING,
            3: HEAT_MAP,
            4: REGRESSION,
            6: ANALYTIC_SITE,
            7: TREE_MAP,
          }

          let pageType = pathMap[analyticsType] || ANALYTIC_BUILDING
          let { name } = findRouteForTab(pageType) || {}

          name && this.$router.push({ name, query: { reportId: id } })
        } else {
          switch (analyticsType) {
            case 1:
              if (xCriteriaMode) {
                path = 'portfolio'
              } else {
                path = 'building'
              }
              break
            case 3:
              path = 'heatmap'
              break
            case 4:
              path = 'regression'
              break
            case 6:
              path = 'site'
              break
            case 7:
              path = 'treemap'
              break
            default:
              path = 'building'
          }

          path = '/app/em/analytics/' + path + '?reportId=' + id
          this.$router.push({ path: path })
        }
      }
      this.closePopup()
    },
    closePopup() {
      this.showDialog = false
      this.link = null
      this.dashboard = null
      this.report = null
    },
    reportLoaded(reportObj, resultObj) {
      this.report = resultObj
    },
  },
}
</script>

<style>
.f-popup-view .el-dialog__header {
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 15px;
}
.f-popup-view {
  width: 95vw;
  height: 95vh;
  margin: 20px auto !important;
  overflow: hidden;
  box-shadow: 0 8px 10px 1px rgba(0, 0, 0, 0.14),
    0 3px 14px 2px rgba(0, 0, 0, 0.12), 0 5px 5px -3px rgba(0, 0, 0, 0.2) !important;
}
.f-popup-view .el-dialog__body {
  height: 100%;
  overflow: scroll;
  padding: 0px;
  margin-left: 30px;
  margin-right: 30px;
  margin-top: 20px;
}
.f-popup-view .date-filter-comp-new-report {
  margin-right: 30px;
  position: absolute;
  top: 1px;
  z-index: 1;
  left: 43%;
}
.f-popup-view .fLegendContainer-right {
  margin-top: 41px;
}
.f-popup-view .tabular-report-table {
  margin-top: 30px;
}
</style>
