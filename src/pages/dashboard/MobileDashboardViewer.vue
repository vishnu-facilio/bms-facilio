<template>
  <div
    class="height100 scrollable dashboardmainlayout"
    v-bind:class="{ mobiledashboard: this.isMobileDashboard }"
  >
    <div v-if="loading || !dashboardLayout"></div>
    <div
      id="mobiledashboardcontainer"
      class="dashboard-container mobile-dashboard-container"
      :class="{ editmode: editMode }"
      v-else
      v-on:scroll="handleScroll"
    >
      <div class="dashboard-edit-toolbar" v-if="editMode">
        <div class="pull-left" style="width: auto;">
          <el-input
            class="dashboard-name-input"
            v-model="dashboard.label"
            placeholder="Edit Dashboard"
          ></el-input>
        </div>
        <div class="pull-right" style="margin-right: 70px;">
          <el-button
            type="primary"
            size="small"
            style="margin-right:10px;"
            @click="loadSharing"
            >Sharing</el-button
          >
          <el-dropdown
            @command="handleAddWidget"
            size="small"
            style="margin-right: 8px;"
          >
            <el-button type="primary" size="small">
              Add Widget
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="static"
                >Pre-built Widget</el-dropdown-item
              >
              <el-dropdown-item command="chart">Chart Widget</el-dropdown-item>
              <el-dropdown-item command="list">List Widget</el-dropdown-item>
              <el-dropdown-item command="map">Map Widget</el-dropdown-item>
              <el-dropdown-item command="web">Web Widget</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="small" type="primary" @click="saveEdit"
            >SAVE</el-button
          >
          <el-button
            size="small"
            @click="cancelEdit"
            style="color:#39b2c2;border-color:#39b2c2; letter-spacing: 0.7px; margin-left: 8px;"
            >CANCEL</el-button
          >
        </div>
      </div>
      <company-listslider
        v-if="showPortfolio && showPortfolio === 'energydata'"
        :dashboardLink="dashboardLink"
        style="cursor:pointer; padding: 15px 15px 0px 15px;"
        :sites="sites"
      ></company-listslider>
      <alarm-company-listslider
        v-if="showPortfolio && showPortfolio === 'alarm'"
        style="cursor:pointer; padding: 15px 15px 0px 15px;"
      ></alarm-company-listslider>
      <dashboard-filter v-if="$route.query.filters"></dashboard-filter>
      <div class="row" v-if="!dashboardLayout.length">
        <div class="col-12 text-center pT30">No widgets available.</div>
      </div>
      <el-row :gutter="10">
        <el-col
          v-for="(item, index) in dashboardLayout"
          :xs="item.w"
          :sm="item.w"
          :key="index"
          v-bind:class="{ disablewidget: disablewidget }"
        >
          <f-mobile-widget
            :type="item.widget.type"
            :widget="item.widget"
            :grid="item"
            :key="index"
            :isLazyDashboard="isLazyDashboard"
            :rowHeight="rowHeight"
            :currentDashboard="currentDashboard"
            :dashboard="dashboard ? dashboard.id : null"
            @deletechart="deleteChart"
            :mode="editMode"
            :style="{ 'min-height': item.h * 2 * rowHeight + 'px' }"
            v-if="item.key === 'chart'"
            @reportLoaded="reportLoaded"
            :moduleName="currentModuleName"
            :dbFilterJson="dbUserFilters[item.widget.id]"
            :dbTimelineFilter="timelineFilterMap[item.widget.id]"
          ></f-mobile-widget>
          <CardWrapper
            v-else-if="
              item.widget.type === 'card' &&
                item.widget.dataOptions &&
                item.widget.dataOptions.cardLayout &&
                item.widget.dataOptions.cardLayout === 'web_layout_1'
            "
            :ref="'widget[' + index + ']'"
            :widget="item.widget"
            :dashboardId="dashboard ? dashboard.id : null"
            :isLazyDashboard="isLazyDashboard"
            :dbFilterJson="dbUserFilters[item.widget.id]"
            :dbTimelineFilter="timelineFilterMap[item.widget.id]"
            style="height: 400px"
          ></CardWrapper>
          <CardWrapper
            v-else-if="item.widget.type === 'card'"
            :ref="'widget[' + index + ']'"
            :widget="item.widget"
            :dashboardId="dashboard ? dashboard.id : null"
            :isLazyDashboard="isLazyDashboard"
            :dbFilterJson="dbUserFilters[item.widget.id]"
            :dbTimelineFilter="timelineFilterMap[item.widget.id]"
          ></CardWrapper>
          <f-mobile-widget
            :type="item.widget.type"
            :widget="item.widget"
            :grid="item"
            :key="index"
            :rowHeight="rowHeight"
            :currentDashboard="currentDashboard"
            :dashboard="dashboard ? dashboard.id : null"
            @deletechart="deleteChart"
            :mode="editMode"
            :style="{ height: item.h * 2 * rowHeight + 'px' }"
            :moduleName="currentModuleName"
            :dbFilterJson="dbUserFilters[item.widget.id]"
            :dbTimelineFilter="timelineFilterMap[item.widget.id]"
            :isLazyDashboard="isLazyDashboard"
            v-else
          ></f-mobile-widget>
        </el-col>
      </el-row>
    </div>
    <div v-if="editMode">
      <new-chart-widget
        :dashboardId="dashboard ? dashboard.id : null"
        ref="newChartWidget"
      ></new-chart-widget>
      <new-list-widget
        :dashboardId="dashboard ? dashboard.id : null"
        ref="newListWidget"
      ></new-list-widget>
      <new-map-widget
        :dashboardId="dashboard ? dashboard.id : null"
        ref="newMapWidget"
      ></new-map-widget>
      <new-prebuilt-widget
        :dashboardId="dashboard ? dashboard.id : null"
        ref="newStaticWidget"
      ></new-prebuilt-widget>
      <new-web-widget
        :dashboardId="dashboard ? dashboard.id : null"
        ref="newWebWidget"
      ></new-web-widget>
    </div>
    <!-- DB Filters -->
    <div
      class="dashboard-filter-toggle-mobile"
      @click="showDashboardFilterPopover = true"
      v-if="showDashboardFilterIcon"
    >
      <inline-svg
        class="pointer"
        src="svgs/dashboard/filter"
        iconClass="icon icon-md"
        iconStyle="color:#ee518f"
      ></inline-svg>
    </div>
    <dashboard-filter-container-mobile
      ref="dbFilterContainer"
      v-if="!loading"
      :visibility.sync="showDashboardFilterPopover"
      :dashboardFilterObj="dashboardFilterObj"
      :dashboardId="dashboard.id"
      @dbUserFilters="handleDbUserFilterChange"
    ></dashboard-filter-container-mobile>
    <!--******-->
  </div>
</template>

<script>
import NewChartWidget from './forms/NewChartWidget'
import NewListWidget from './forms/NewListWidget'
import NewMapWidget from './forms/NewMapWidget'
import NewPrebuiltWidget from './forms/NewPrebuiltWidget'
import NewWebWidget from './forms/NewWebWidget'
import CompanyListslider from '@/CompanyListslider'
import AlarmCompanyListslider from '@/CompanySlider'
import infiniteScroll from 'vue-infinite-scroll'
import DashboardFilter from './DashboardFilter'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import tooltip from '@/graph/mixins/tooltip'
import cardtooltip from '@/graph/mixins/cardtooltip'
import FMobileWidget from './widget/WidgetLayouts/MobileWidget'
import CardWrapper from 'pages/card-builder/components/CardWrapper'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'

import DashboardFilterContainerMobile from 'pages/dashboard/dashboard-filters/mobile/DashboardFilterContainerMobile'
import DashboardFilterMixin from 'pages/dashboard/dashboard-filters/DashboardFilterMixin'

export default {
  mixins: [ReportHelper, cardtooltip, DashboardFilterMixin],
  props: ['sites', 'currentDashboard', 'buildings'],
  components: {
    NewChartWidget,
    NewListWidget,
    NewMapWidget,
    NewPrebuiltWidget,
    NewWebWidget,
    CompanyListslider,
    DashboardFilter,
    AlarmCompanyListslider,
    FMobileWidget,
    CardWrapper,
    DashboardFilterContainerMobile,
  },
  data() {
    return {
      loading: true,
      dashboard: null,
      isMobileDashboard: false,
      disablewidget: false,
      rowHeight: 100,
      dashboardLayout: null,
      removeChartList: [],
      chartDeleted: false,
      shareTo: 2,
      sharedUsers: [],
      sharedRoles: [],
      sharedGroups: [],
      showDashboardFilterIcon: false,
      showDashboardFilterPopover: false,
      isLazyDashboard: true,
    }
  },
  directives: {
    infiniteScroll,
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    dashboardLink() {
      if (this.currentDashboard) {
        return this.currentDashboard.linkName
      } else {
        if (
          this.$route.params.dashboardlink === 'residentialbuildingdashboard' ||
          this.$route.params.dashboardlink === 'commercialbuildingdashboard'
        ) {
          return 'buildingdashboard'
        } else {
          return this.$route.params.dashboardlink
        }
      }
    },
    mobileModuleName() {
      if (this.$route.query && this.$route.query.mobileModuleName) {
        return JSON.parse(this.$route.query.mobileModuleName)
      }
      return null
    },
    showDashboardTitle() {
      if (this.currentDashboard) {
        if (
          !this.currentDashboard.screenSetting ||
          typeof this.currentDashboard.screenSetting['titleVisibility'] ===
            'undefined' ||
          this.currentDashboard.screenSetting['titleVisibility']
        ) {
          return true
        }
        return false
      }
      return true
    },
    currentModuleName() {
      return this.mobileModuleName
        ? this.mobileModuleName
        : this.getCurrentModule().module
    },
    showPortfolio() {
      if (this.currentDashboard) {
        if (
          this.currentDashboard.linkName === 'portfolio' ||
          this.currentDashboard.linkName === 'commercial' ||
          this.currentDashboard.linkName === 'residential'
        ) {
          return this.currentDashboard.moduleName
        }
      } else {
        if (
          this.$route.path === '/app/em/newdashboard/portfolio' ||
          this.$route.path === '/app/em/dashboard/portfolio' ||
          this.$route.path === '/app/em/newdashboard/commercial' ||
          this.$route.path === '/app/em/newdashboard/residential'
        ) {
          return 'energydata'
        } else if (
          this.$route.path === '/app/fa/newdashboard/portfolio' ||
          this.$route.path === '/app/fa/dashboard/portfolio'
        ) {
          return 'alarm'
        }
      }
      return null
    },
    buildingId() {
      if (this.currentDashboard) {
        return this.currentDashboard.buildingId
      } else if (this.dashboardLink === 'buildingdashboard') {
        return this.$route.params.buildingid
      }
      return null
    },
    siteId() {
      if (this.currentDashboard) {
        return this.currentDashboard.siteId
      } else if (this.dashboardLink === 'sitedashboard') {
        return this.$route.params.buildingid
      }
      return null
    },
    chillerPlant() {
      if (this.currentDashboard) {
        return this.currentDashboard.siteId
      } else if (this.dashboardLink === 'chillerplant') {
        return this.$route.params.buildingid
      }
      return null
    },
    chillerId() {
      if (this.currentDashboard) {
        return this.currentDashboard.siteId
      } else if (this.dashboardLink === 'chillers') {
        return this.$route.params.buildingid
      }
      return null
    },
    editMode() {
      return this.$route.query.mode === 'edit'
    },
    sharingDialogVisible() {
      if (this.$route.query.mode === 'share') {
        return true
      } else {
        return false
      }
    },
    fullScreenClass() {
      if (this.currentDashboard) {
        return 'remoteScreenFlSc'
      }
      return 'headingFlSc'
    },
  },
  created() {
    this.$store.dispatch('loadRoles')
    this.registerMobileListeners()
  },
  mounted() {
    this.loadDashboard()
  },
  destroyed() {
    this.unRegisterMobileListeners()
    tooltip.hideTooltip()
    this.hideTooltip()
    this.hideTooltip('kpi-gauge-chart')
    this.hideSparklineTooltip()
  },
  watch: {
    dashboardLink: function(newVal) {
      this.loadDashboard()
    },
    currentModuleName: function(newVal) {
      this.loadDashboard()
    },
    buildingId: function(newVal) {
      this.loadDashboard()
    },
    siteId: function(newVal) {
      this.loadDashboard()
    },
    chillerId: function(newVal) {
      this.loadDashboard()
    },
    chillerPlant: function(newVal) {
      this.loadDashboard()
    },
  },
  // title () {
  //   let path = this.$route.path
  //   return 'Workorder Dashboard'
  // },
  methods: {
    openDashboardFilterPopover() {
      this.showDashboardFilterPopover = true
    },
    closeDashboardFilterPopover() {
      this.showDashboardFilterPopover = false
    },
    handleDbUserFilterChange(value) {
      this.dbUserFilters = value.filterJson
    },

    registerMobileListeners() {
      {
        window.openDashboardFilterPopover = this.openDashboardFilterPopover
        window.closeDashboardFilterPopover = this.closeDashboardFilterPopover
      }
    },
    unRegisterMobileListeners() {
      window.openDashboardFilterPopover = null
      window.closeDashboardFilterPopover = null
    },
    reportLoaded(report, result) {
      if (
        report &&
        report.options &&
        report.options.settings &&
        report.options.settings.chartMode &&
        report.options.settings.chartMode === 'multi'
      ) {
        this.disablewidget = true
      }
    },
    loadSharing: function() {
      let self = this
      if (!self.dashboard) {
        return
      }
      self.$http
        .get('/dashboardsharing/' + self.dashboard.id)
        .then(function(response) {
          if (response.data.dashboardSharing) {
            if (response.data.dashboardSharing.length === 0) {
              self.shareTo = 2
            } else {
              self.sharedUsers = []
              self.sharedRoles = []
              self.sharedGroups = []
              for (let i = 0; i < response.data.dashboardSharing.length; i++) {
                let dashboardSharing = response.data.dashboardSharing[i]
                if (dashboardSharing.sharingType === 1) {
                  self.sharedUsers.push(dashboardSharing.orgUserId)
                } else if (dashboardSharing.sharingType === 2) {
                  self.sharedRoles.push(dashboardSharing.roleId)
                } else if (dashboardSharing.sharingType === 3) {
                  self.sharedGroups.push(dashboardSharing.groupId)
                }
              }
              if (
                response.data.dashboardSharing.length === 1 &&
                self.sharedUsers.length === 1 &&
                self.sharedUsers[0] === self.getCurrentUser().ouid
              ) {
                self.shareTo = 1
              } else {
                self.shareTo = 3
              }
            }
          }
          self.sharingDialogVisible = true
        })
    },
    applySharing: function() {
      let self = this
      let dashboardSharing = []
      if (self.shareTo === 1) {
        dashboardSharing.push({
          dashboardId: self.dashboard.id,
          sharingType: 1,
          orgUserId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          dashboardSharing.push({
            dashboardId: self.dashboard.id,
            sharingType: 1,
            orgUserId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              dashboardSharing.push({
                dashboardId: self.dashboard.id,
                sharingType: 1,
                orgUserId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            dashboardSharing.push({
              dashboardId: self.dashboard.id,
              sharingType: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      }
      self.$http
        .post('/dashboardsharing/apply', {
          dashboardId: self.dashboard.id,
          dashboardSharing: dashboardSharing,
        })
        .then(function(response) {
          self.$message({
            message: 'Sharing applied successfully!',
            type: 'success',
          })
          self.sharingDialogVisible = false
        })
    },
    loadDashboard() {
      let self = this
      self.loading = true
      this.clearDashboardFilters()
      this.showDashboardFilterIcon = false
      self.dashboardLayout = null
      self.rowHeight =
        document.getElementsByClassName('dashboardmainlayout')[0].offsetWidth /
          24 -
        document.getElementsByClassName('dashboardmainlayout')[0].offsetWidth /
          24 /
          3
      let tabId = self.$route.query.tab
      if(!isEmpty(tabId) && !isNaN(tabId))
      {
        self.$http
          .get('/dashboard/getTabWidgets?dashboardTabId=' + Number(tabId))
          .then(response => {
            if (response.data.dashboardTabContext) {
              let dashboard = {
                tabId: tabId,
                sequence: response.data.dashboardTabContext.sequence,
                tabName: response.data.dashboardTabContext.name,
                lastY: 0,
                children: response.data.dashboardTabContext.clientWidgetJson,
              }
              self.dashboard= cloneDeep(dashboard)
              self.prepareDashboardLayout(response.data.dashboardTabContext.clientWidgetJson)
              self.initializeDashboardFilters(response.data.dashboardTabContext.dashboardFilter)
              self.loading = false
            }
          })
      }
      else
      {
        let url = '/dashboard/' + this.dashboardLink
        if (self.siteId) {
          url += '&siteId=' + self.siteId
        } else if (self.buildingId) {
          url += '&buildingId=' + self.buildingId
        }
        self.$http.get(url).then(function(response) {
          self.dashboard = response.data.dashboardJson[0]
          self.prepareDashboardLayout()
          self.initializeDashboardFilters(self.dashboard.dashboardFilter)
          self.loading = false
        })
      }
    },
    filterTheDashboard(childrens) {
      if (this.dashboardLink === 'myworkload') {
        return childrens.filter(rt => {
          let { widget } = rt
          if (
            widget.type === 'static' &&
            widget.header &&
            widget.header.title === 'My Open Workorders'
            // eslint-disable-next-line no-empty
          ) {
          } else {
            return rt
          }
        })
      }
      return childrens
    },
    prepareDashboardLayout() {
      let self = this
      let layout = []
      let tx = 0
      if (
        this.$route.meta &&
        this.$route.meta.dashboardlayout &&
        this.$route.meta.dashboardlayout === 'mobile'
      ) {
        this.isMobileDashboard = true
      }
      let childrens = this.filterTheDashboard(self.dashboard.children)
      for (let i = 0; i < childrens.length; i++) {
        const widget = childrens[i]
        const {
          widget: { type },
        } = widget
        if (type == 'section') {
          // For now `groups` are not supported in mobile dashboard.
          continue
        }
        let key =
          childrens[i].widget.dataOptions &&
          childrens[i].widget.dataOptions.staticKey
            ? childrens[i].widget.dataOptions.staticKey
            : childrens[i].widget.type
            ? childrens[i].widget.type
            : ''
        if (tx + childrens[i].widget.layout.width > 24) {
          tx = 0
        }
        let x = childrens[i].widget.layout.x ? childrens[i].widget.layout.x : 0
        let y = childrens[i].widget.layout.y ? childrens[i].widget.layout.y : 0
        if (
          childrens[i].widget.type &&
          childrens[i].widget.type === 'chart' &&
          childrens[i].widget.dataOptions &&
          childrens[i].widget.dataOptions.reportId
          // eslint-disable-next-line no-empty
        ) {
        } else if (
          childrens[i].widget.type &&
          childrens[i].widget.type === 'view'
          // eslint-disable-next-line no-empty
        ) {
        } else {
          layout.push({
            i:
              (childrens[i].widget.layout.position
                ? childrens[i].widget.layout.position
                : 0) + '',
            x: x,
            y: y,
            w:
              childrens[i].widget.layout.width === 0
                ? 0
                : self.getMobileLayout(
                    key,
                    childrens[i].widget.dataOptions,
                    childrens[i].widget
                  ).width,
            h:
              childrens[i].widget.layout.width === 0
                ? 0
                : self.getMobileLayout(
                    key,
                    childrens[i].widget.dataOptions,
                    childrens[i].widget
                  ).height,
            widget: childrens[i].widget,
            key: key,
            index: self.getMobileLayout(
              key,
              childrens[i].widget.dataOptions,
              childrens[i].widget
            ).index,
          })
        }
        tx += childrens[i].widget.layout.width
      }
      let layout2 = []
      layout2 = layout.sort((a, b) =>
        parseInt(a.y) > parseInt(b.y)
          ? 1
          : parseInt(b.y) > parseInt(a.y)
          ? -1
          : 0
      )
      self.dashboardLayout = self.sorting(layout2)
      // To calculate the x of the mobiledashboard
      self.calculateX()
    },
    sorting(layout) {
      let layout3 = []
      layout3 = layout.sort((a, b) =>
        a.y === b.y ? (a.index > b.index ? 1 : b.index > a.index ? -1 : 0) : 0
      )
      return layout3
    },
    calculateX() {
      let self = this
      let layout = null
      let tempX = 0
      if (self.dashboardLayout.length) {
        for (let i = 0; i < self.dashboardLayout.length; i++) {
          layout = self.dashboardLayout[i]
          if (layout.w === 12) {
            if (tempX === 0) {
              self.dashboardLayout[i].x = tempX
              tempX = 12
            } else {
              self.dashboardLayout[i].x = tempX
              tempX = 0
            }
          } else {
            tempX = 0
          }
        }
      }
    },
    getMobileLayout(key, data, widget) {
      if (data && data.metaJson && key === 'readingComboCard') {
        let meta = JSON.parse(data.metaJson)
        if (meta && meta.cardType && meta.cardType === 'AHU') {
          return {
            height: 10,
            width: 12,
            index: 23,
          }
        } else if (
          meta &&
          meta.cardType &&
          meta.cardType.indexOf('HVAC') > -1
        ) {
          return {
            height: 10,
            width: 24,
            index: 23,
          }
        } else {
          return {
            height: 8,
            width: 12,
            index: 23,
          }
        }
      }
      if (key === 'kpiCard' && data.paramsJson && data.paramsJson.key) {
        key = data.paramsJson.key
      }
      let mobileLayoutMapper = {
        readingcard: {
          height: 6,
          width: 12,
          index: 10,
        },
        fahuStatusCard: {
          height: 6,
          width: 12,
          index: 11,
        },
        fahuStatusCard1: {
          height: 6,
          width: 12,
          index: 12,
        },
        fahuStatusCard2: {
          height: 6,
          width: 12,
          index: 13,
        },
        fahuStatusCard3: {
          height: 6,
          width: 12,
          index: 14,
        },
        fahuStatusCardNew: {
          height: 6,
          width: 12,
          index: 14,
        },
        readingWithGraphCard: {
          height: 8,
          width: 12,
          index: 15,
        },
        readingGaugeCard: {
          height: 8,
          width: 12,
          index: 16,
        },
        readingComboCard: {
          height: 12,
          width: 24,
          index: 22,
        },
        emrilllevel1: {
          height: 8,
          width: 12,
          index: 23,
        },
        emrilllevel2: {
          height: 8,
          width: 12,
          index: 24,
        },
        emrilllevel3: {
          height: 8,
          width: 12,
          index: 25,
        },
        emrillFcu: {
          height: 8,
          width: 12,
          index: 27,
        },
        imagecard: {
          height: 12,
          width: 24,
          index: 18,
        },
        weathermini: {
          height: 6,
          width: 24,
          index: 9,
        },
        energycostmini: {
          height: 6,
          width: 24,
          index: 6,
        },
        profilemini: {
          height: 6,
          width: 24,
          index: 5,
        },
        carbonmini: {
          height: 6,
          width: 24,
          index: 8,
        },
        web: {
          height: 12,
          width: 24,
          index: 18,
        },
        view: {
          height: 20,
          width: 24,
          index: 20,
        },
        chart: {
          height: 16,
          width: 24,
          index: 19,
        },
        profilecard: {
          height: 14,
          width: 24,
          index: 1,
        },
        energycard: {
          height: 14,
          width: 24,
          index: 2,
        },
        energycost: {
          height: 14,
          width: 24,
          index: 3,
        },
        weathercard: {
          height: 14,
          width: 24,
          index: 4,
        },
        weathercardaltayer: {
          height: 14,
          width: 24,
          index: 4,
        },
        energycostaltayer: {
          height: 14,
          width: 24,
          index: 4,
        },
        workordersummary: {
          height: 18,
          width: 24,
          index: 26,
        },
        resourceAlarmBar: {
          height: 0,
          width: 0,
          index: 28,
        },
        kpiCard: {
          height: 6,
          width: 24,
          index: 29,
        },
        graphics: {
          height: 30,
          width: 24,
          index: 1,
        },
        controlCommandmini: {
          height: 14,
          width: 24,
          index: 30,
        },
        targetmeter: {
          height: 14,
          width: 24,
          index: 1,
        },
        kpitargetCard: {
          height: 14,
          width: 24,
          index: 1,
        },
        smartcard: {
          height: 6,
          width: 24,
          index: 1,
        },
      }
      return mobileLayoutMapper[key]
        ? mobileLayoutMapper[key]
        : this.cardBuilderLayoutFilter(key, data, widget)
    },
    cardBuilderLayoutFilter(key, data, widget) {
      if (key === 'card' && data.hasOwnProperty('cardLayout')) {
        if (data.cardLayout.indexOf('readingcard_') > -1) {
          if (['readingcard_layout_1', 'readingcard_layout_1'].includes(key)) {
            return {
              height: 6,
              width: 24,
              index: 10,
            }
          }
        } else if (data.cardLayout === 'gauge_layout_7') {
          return {
            height: 6,
            width: 24,
            index: 10,
          }
        } else if (data.cardLayout === 'web_layout_1') {
          return {
            height: 20,
            width: 24,
            index: 10,
          }
        } else if (data.cardLayout.indexOf('gauge_') > -1) {
          return {
            height: 6,
            width: 24,
            index: 10,
          }
        }
      } else if (key === 'textcard') {
        if (key && widget) {
          if (widget.layout.height < 12) {
            return {
              height: 4,
              width: 24,
              index: 17,
            }
          }
        }
        return {
          height: 16,
          width: 24,
          index: 17,
        }
      }
      return {
        height: 20,
        width: 24,
        index: 21,
      }
    },
    saveEdit() {
      let self = this

      let dashboardWidgets = []
      for (let i = 0; i < self.dashboardLayout.length; i++) {
        let gridItem = self.dashboardLayout[i]
        dashboardWidgets.push({
          id: gridItem.widget.id,
          type: gridItem.widget.type,
          layoutWidth: gridItem.w,
          layoutHeight: gridItem.h,
          order: i + 1,
          xPosition: gridItem.x,
          yPosition: gridItem.y,
        })
      }

      let dashboardObj = {
        dashboardMeta: {
          id: self.dashboard.id,
          dashboardName: self.dashboard.label,
          linkName: self.dashboardLink,
          dashboardWidgets: dashboardWidgets,
        },
      }
      for (let i = 0; i < self.removeChartList.length; i++) {
        self.chartDeleted = false
        self.$http
          .post('/dashboard/deleteWidgetFromDashboard', self.removeChartList[i])
          .then(function(response) {
            self.chartDeleted = true
          })
      }
      if (self.chartDeleted) {
        self.$message({
          message: 'Dashboard Deleted successfully!',
          type: 'success',
        })
      }
      self.$http
        .post('/dashboard/update', dashboardObj)
        .then(function(response) {
          self.$message({
            message: 'Dashboard updated successfully!',
            type: 'success',
          })
          self.$router.push(self.$route.path)
        })
    },
    cancelEdit() {
      location.reload()
      this.$router.push(this.$route.path)
    },
    handleAddWidget(widgetType) {
      if (widgetType === 'chart') {
        this.$refs.newChartWidget.open()
      } else if (widgetType === 'list') {
        this.$refs.newListWidget.open()
      } else if (widgetType === 'map') {
        this.$refs.newMapWidget.open()
      } else if (widgetType === 'static') {
        this.$refs.newStaticWidget.open()
      } else if (widgetType === 'web') {
        this.$refs.newWebWidget.open()
      }
    },
    deleteChart(data) {
      this.removeChartList.push(data)
      let obj = this.dashboardLayout.find(r => r.widget.id === data.widgetId)
      if (obj) {
        let index = this.dashboardLayout.indexOf(obj)
        this.dashboardLayout.splice(index, 1)
      }
    },
    handleScroll() {
      tooltip.hideTooltip()
      this.hideTooltip()
      this.hideTooltip('kpi-gauge-chart')
      this.hideSparklineTooltip()
    },
    hideSparklineTooltip() {
      let tooltip = document.getElementsByClassName('sparkline-tooltip')
      let tooltipLine = document.getElementsByClassName(
        'tooltip-line-indicator'
      )
      if (tooltip.length) {
        for (let i = 0; i < tooltip.length; i++) {
          tooltip[i].style.display = 'none'
        }
      }
      if (tooltipLine.length) {
        for (let i = 0; i < tooltipLine.length; i++) {
          tooltipLine[i].style.display = 'none'
        }
      }
    },
  },
}
</script>

<style lang="scss">
.dashboard-container {
  height: 100%;
  overflow-y: scroll;
  width: 100%;
  padding-bottom: 70px;
}

.dashboard-container .fc-report-filter .filter-left {
  display: none;
}

.dashboard-container .fc-report-filter {
  top: -75px;
}

.vue-grid-item {
  padding: 0px !important;
}

.sk-cube-grid {
  width: 40px;
  height: 40px;
  margin: 200px auto;
}

.sk-cube-grid .sk-cube {
  width: 33%;
  height: 33%;
  float: left;
  -webkit-animation: sk-cubeGridScaleDelay 1.3s infinite ease-in-out;
  animation: sk-cubeGridScaleDelay 1.3s infinite ease-in-out;
}

.sk-cube-grid .sk-cube1 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

.sk-cube-grid .sk-cube2 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.3s;
  animation-delay: 0.3s;
}

.sk-cube-grid .sk-cube3 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.4s;
  animation-delay: 0.4s;
}

.sk-cube-grid .sk-cube4 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.1s;
  animation-delay: 0.1s;
}

.sk-cube-grid .sk-cube5 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

.sk-cube-grid .sk-cube6 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.3s;
  animation-delay: 0.3s;
}

.sk-cube-grid .sk-cube7 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0s;
  animation-delay: 0s;
}

.sk-cube-grid .sk-cube8 {
  background-color: #fd4b92;
  -webkit-animation-delay: 0.1s;
  animation-delay: 0.1s;
}

.sk-cube-grid .sk-cube9 {
  background-color: #2f2e49;
  -webkit-animation-delay: 0.2s;
  animation-delay: 0.2s;
}

@-webkit-keyframes sk-cubeGridScaleDelay {
  0%,
  70%,
  100% {
    -webkit-transform: scale3D(1, 1, 1);
    transform: scale3D(1, 1, 1);
  }

  35% {
    -webkit-transform: scale3D(0, 0, 1);
    transform: scale3D(0, 0, 1);
  }
}

@keyframes sk-cubeGridScaleDelay {
  0%,
  70%,
  100% {
    -webkit-transform: scale3D(1, 1, 1);
    transform: scale3D(1, 1, 1);
  }

  35% {
    -webkit-transform: scale3D(0, 0, 1);
    transform: scale3D(0, 0, 1);
  }
}

.dashboard-edit-toolbar {
  position: fixed;
  width: 100%;
  height: 56px;
  background: #fff;
  top: 50px;
  z-index: 1000;
  padding: 12px 15px;
}

.dashboard-name-input {
  width: 300px;
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000;
}

.dashboard-container.editmode .fc-widget-header,
.dashboard-container.editmode .dragabale-card {
  cursor: move;
}

.dashboard-container .fchart-section {
  padding-left: 0px !important;
  padding-right: 0px !important;
  text-align: center;
}

.dashboard-container .baselineoption span {
  display: none;
}

.dashboard-container .fc-report-filter .header-content,
.dashboard-container .fc-report-building,
.dashboard-container .fc-underline,
.dashboard-container .fc-report-building {
  display: none;
}

.dashboard-container .fc-report-filter.row.header {
  align-items: right;
  text-align: right;
}

.dashboard-container .fc-report-filter .fc-report-building {
  display: none !important;
}

.dashboard-container .fc-report-pop-btn-row,
.dashboard-container .compare-row {
  display: none !important;
}

.dashboard-container .chart-option .c-option-1,
.dashboard-container .chart-option .c-option-2,
.dashboard-container .chart-option .c-diff {
  padding-top: 10px;
  padding-bottom: 10px;
}

.dashboard-container .chart-option {
  border: none;
  margin: 0px;
  padding-left: 2%;
  padding-right: 15px;
  white-space: nowrap;
  position: absolute;
  bottom: 0;
}

/* dashboard new css */
.dashboard-container .energy_background {
  background-image: linear-gradient(to bottom, #ec637f, #843f78);
}

.dashboard-container .energy-cost {
  background-image: linear-gradient(to left, #7039a9, #4a2973);
  color: #fff;
  height: 100%;
}

.fc-black-theme .dashboard-container .fc-widget {
  border: 0 !important;
  box-shadow: none;
}

.fc-black-theme .dashboard-container .fc-widget-label {
  color: #fff;
}

.fc-black-theme rect.tile_empty {
  fill: #7976764a !important;
}

.fc-black-theme .heatMap line,
.fc-black-theme .heatMap .y.axis path {
  stroke: #393b59 !important;
}

.dashboard-container .fc-widget-label {
  font-size: 1.1vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #2f2e49;
  padding-bottom: 0px;
  /* white-space: pre-wrap; */
}

.dashboard-container .primaryfill-color {
  fill: #fff !important;
}

.dashboard-container .carbon-weather {
  background-image: linear-gradient(to left, #2f2e49, #2d436e);
}

.dashboard-container .fc-widget {
  border: solid 1px #eae8e8;
  box-shadow: 0 7px 6px 0 rgba(233, 233, 233, 0.5);
  background: #fff;
}

.dashboard-container .db-container {
  border: none;
}

/* dashboard resonsive changes */
text.Yaxis-label {
  font-size: 1em !important;
}

.dashboard-container .axis text {
  font-size: 10px;
}

.dashboard-container .fc-widget {
  position: relative;
}

/* new issue fixes monday */
.dashboard-container .fc-widget-header {
  padding: 15px;
  padding-top: 18px;
  padding-bottom: 18px;
}

.dashboard-container .fc-widget-sublabel {
  padding-top: 2px;
  font-size: 12px;
}

text.Yaxis-label.timeseries {
  font-size: 10px !important;
}

.dashboard-container .fc-list-view-table td {
  white-space: nowrap;
  padding-left: 14px;
}

.dashboard-container .fc-list-view-table tbody tr.tablerow td:first-child {
  border-left: 3px solid transparent !important;
  font-size: 13px;
  font-weight: 400;
  font-style: normal;
  font-stretch: normal;
  line-height: 1.57;
  letter-spacing: 0.3px;
  text-align: left;
  color: #333333;
  white-space: nowrap;
  max-width: 230px;
}

.fc-black-theme
  .dashboard-container
  .fc-list-view-table
  tbody
  tr.tablerow
  td:first-child {
  color: #fff !important;
}

.dashboard-container .fc-list-view-table tbody tr.tablerow td:first-child div {
  max-width: 230px;
  width: 230px;
  white-space: initial;
}

.dashboard-container .fc-list-view-table tbody tr.tablerow td:nth-child(2) {
  text-align: center;
}

.dashboard-container thead tr th:nth-child(2) {
  text-align: center;
}

.dashboard-container .fc-widget-header .fc-widget-sublabel {
  display: none;
}

.dashboard-container .date-filter-comp button {
  right: 0;
  top: -30px;
  font-size: 0.9vw;
  padding: 8px;
  border: none;
}

.dashboard-container .chartSlt {
  position: absolute;
  /* top: 28px; */
}

.dashboard-container .legend.legendsAll {
  padding-top: 0;
}

.dashboard-container .emptyLegends {
  padding-top: 55px;
}

/*  chart change icon postion css*/
.dashboard-container .fc-widget:hover .externalLink {
  display: block !important;
  cursor: pointer;
  height: auto !important;
}

.dashboard-container.editmode .fc-widget:hover .chart-delete-icon {
  display: block !important;
  cursor: pointer;
  height: auto !important;
}

.dashboard-container .externalLink {
  display: none;
  position: absolute;
  right: 15px;
  top: 15px;
}

/* edit mode css*/
.dashboard-container.editmode .externalLink {
  display: none;
  position: absolute;
  right: 65px !important;
  top: 15px;
}

.dashboard-container .chart-delete-icon {
  display: none;
  position: absolute;
  right: 8px;
  top: 13px;
  z-index: 2;
}

.dbHeadingTxt {
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  outline: 0;
  transition: color 0.25s;
  font-size: 25px;
  padding: 20px 10px 5px 20px;
  letter-spacing: 0.5px;
}

.fc-white-theme .dbHeadingTxt {
  color: #25243e;
}

.fc-black-theme .dbHeadingTxt {
  color: #ffffff;
}

.headingFlSc {
  display: none;
}

.remoteScreenFlSc {
  display: block;
}

body:-webkit-full-screen .headingFlSc {
  display: block;
}

body:-webkit-full-screen .el-popper {
  z-index: 100000000000;
}

body:-webkit-full-screen .layout-header,
body:-webkit-full-screen .fc-layout-aside,
body:-webkit-full-screen .subheader-section,
body:-webkit-full-screen .el-dialog__wrapper {
  display: none;
}

body:-webkit-full-screen .normal .layout-page {
  height: 100vh !important;
}

body:-webkit-full-screen .dashboardmainlayout {
  margin: 0;
}

body:-webkit-full-screen .layout-page-container,
body:-webkit-full-screen .layout-page .height100 {
  padding: 0px !important;
}

.full-screen-banner {
  height: 80px;
  background: #372668;
  width: 100%;
  position: relative;
}

.baner-header {
  font-size: 0.6em;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.9px;
  color: #ffffff;
  position: absolute;
  left: 150px;
  top: 30px;
}

.emirates_logo {
  height: 85%;
  position: absolute;
  left: 20px;
  top: 5px;
}

.flight {
  position: absolute;
  left: 39%;
  top: 10px;
}

.facilio-logo {
  position: absolute;
  right: 40px;
  top: 25px;
}

.card-shimmer section {
  height: 280px !important;
}

.fcu-sections {
  padding-top: 20px !important;
}

.row.db-test-2.count-grid-container.mobile-count-grid-container2
  .col-md-6.col-lg-6 {
  width: 25%;
  margin: auto;
}

.row.db-test-2.count-grid-container.mobile-count-grid-container1
  .col-md-6.col-lg-6 {
  width: 25%;
  margin: auto;
}

.mobile-dashboard-container .graphics-canvas {
  transform: rotate(90deg) !important;
  left: -135px !important;
  top: 90px !important;
}
/* DB filter style */
.dashboard-filter-toggle-mobile {
  position: fixed;
  top: 5px;
  right: 5px;
  padding: 8px 8px 4px 8px;
  border-radius: 2px;
  box-shadow: 0 2px 7px 0 rgba(217, 224, 231, 0.47);
  border: solid 1px #dae0e8;
  border: solid 1px #ee518f;
  background-color: #ffffff;
}

/*  */
</style>
