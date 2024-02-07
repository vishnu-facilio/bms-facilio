<template>
  <div>
    <div
      v-if="isPermission"
      v-resize
      class="dashboardmainlayout dashboardviewer height100vh new-dashboard"
      :class="{
        mobiledashboard: this.isMobileDashboard,
        'single-dashboard': dashboardLayout && dashboardLayout.length === 1,
        pB110: !isTv,
      }"
    >
      <portal to="dashboardFilter">
        <dashboard-timeline-filter
          class="mR10 mT2"
          :dbTimelineFilterInitialState="dbTimelineFilter"
          v-if="
            (!(loading || dashboardTabLoading) &&
              dashboardFilterObj &&
              dashboardFilterObj.isTimelineFilterEnabled &&
              !$helpers.isPortalUser()) ||
              ($helpers.isPortalUser() &&
                dashboardFilterObj &&
                dashboardFilterObj.isTimelineFilterEnabled)
          "
          @timelineFilterChanged="timelineFilterChanged"
        >
        </dashboard-timeline-filter>
        <div
          v-if="
            (showDbFilterIcon && !$helpers.isPortalUser()) ||
              ($helpers.isPortalUser() &&
                dashboardFilterObj &&
                dashboardFilterObj.dashboardUserFilters)
          "
          class="dashboard-filter-toggle mR10 mT2"
          :class="[{ 'is-open': showDashboardFilterBar }]"
          @click="showDashboardFilterBar = !showDashboardFilterBar"
        >
          <inline-svg
            v-if="showDashboardFilterBar"
            class="pointer"
            src="svgs/dashboard/filter"
            iconClass="icon icon-md"
            iconStyle="color:#ee518f"
          ></inline-svg>
          <inline-svg
            v-else
            class="pointer"
            src="svgs/dashboard/filter-disabled"
            iconClass="icon icon-md"
          ></inline-svg>
        </div>
      </portal>
      <div v-if="fullScrnHeader" class="dbHeadingTxt" :class="fullScreenClass">
        <slot name="dashboardNameList">{{ fullScrnHeader }}</slot>
      </div>
      <transition name="fc-scale-vertical">
        <dashboard-filter-container
          class="dashboard-filter-container"
          ref="dbFilterContainer"
          v-if="!(dashboardTabId || loading)"
          v-show="showDashboardFilterBar"
          :dashboardFilterObj="dashboardFilterObj"
          :dashboardId="dashboard.id"
          @dbUserFilters="userFilterChanged"
          @dbFilterConfigSaved="handleDbFilterConfigSaved"
          :filterEditMode.sync="filterEditMode"
          :hideFilterInsideWidgets.sync="hideFilterInsideWidgets"
        >
        </dashboard-filter-container>
      </transition>
      <cube-loader v-if="loading || !dashboardLayout"></cube-loader>
      <div
        class="dashboard-container"
        style="padding-bottom:0px;"
        :class="{ editmode: editMode }"
        v-else
      >
        <!-- print heading -->
        <div class="dashboard-header-pdf">
          <div>
            <el-row class="flex-middle">
              <el-col :span="4">
                <div class="customer-logo">
                  <div v-show="$org.logoUrl">
                    <img
                      :src="$org.logoUrl"
                      style="width: 100px; height: 50px; object-fit: contain;"
                    />
                  </div>
                </div>
              </el-col>
              <el-col :span="16">
                <div class="pm-pdf-heading text-center new-dashboard-heading">
                  {{ dashboard.label }}
                </div>
              </el-col>
              <!-- <el-col :span="10" :offset="4" v-if="getScopedSitesToPrintPdf">
                <div class="pm-pdf-heading text-left">
                  <span style="padding-left:9x;"
                    >{{ this.$t('common.products.site') }} : </span
                  >{{ getScopedSitesToPrintPdf }}
                </div>
              </el-col> -->
              <el-col
                :span="8"
                style="display:flex;flex-direction: column;"
              >
                <div v-if="getScopedSitesToPrintPdf" class="pm-pdf-heading text-center new-dashboard-heading">
                  <span>{{ this.$t('common.products.site') }} : </span>{{ getScopedSitesToPrintPdf }}
                </div>
                <div
                  class="pm-pdf-heading text-center new-dashboard-heading"
                >
                  {{ dashboardTimelineFilterRange }}
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <div
          class="height100"
          :class="
            dashboardTabContexts && dashboardTabContexts.length > 0
              ? 'overflow-y'
              : ''
          "
        >
          <el-row>
            <div
              v-if="
                dashboardTabContexts &&
                  dashboardTabContexts.length > 0 &&
                  dashboardTabId
              "
            >
              <el-col :span="24" v-if="dashboardTabPlacement === 1">
                <dashboard-top-tab
                  :dashboardTabContexts="dashboardTabContexts"
                  :dashboardTabId="dashboardTabId"
                  @changeDashboardTabId="changeDashboardTabId"
                ></dashboard-top-tab>
              </el-col>
              <el-col :span="4" v-if="dashboardTabPlacement === 2">
                <vertical-tab-selector
                  :tabsList="dashboardTabContexts"
                  :currentTabId="dashboardTabId"
                  @expand="expand"
                  @changeDashboardTabId="changeDashboardTabId"
                />
              </el-col>
            </div>
            <el-col
              :span="tabsEnabled ? 20 : 24"
              class="scrollable"
              :class="
                dashboardTabPlacement === 2 &&
                dashboardTabContexts &&
                dashboardTabContexts.length > 0
                  ? 'pB105'
                  : dashboardTabPlacement === 1 &&
                    dashboardTabContexts &&
                    dashboardTabContexts.length > 0
                  ? 'pB145'
                  : ''
              "
            >
              <el-col>
                <!-- This needs refactoring, we have used two copies of the same component,
                1) For simple dashboard (Without tabs, uses linkname only).
                2) For dashboards with tabs. -->
              </el-col>
              <div
                class="row"
                v-if="
                  !dashboardTabLoading && !dashboardLayout.length && !loading
                "
              >
                <div
                  class="col-12 flex-middle justify-content-center flex-direction-column height80vh pT30"
                >
                  <inline-svg
                    src="svgs/empty-dashboard"
                    iconClass="text-center icon-xxxlg"
                  ></inline-svg>
                  <div class="fc-black-24 pT20">
                    You haven’t added any widgets yet
                  </div>
                  <div class="fc-grayish f18 pT10">
                    Add widgets to see any further data in this page
                  </div>
                  <el-button
                    class="fc-create-btn mT20 letter-spacing0_4"
                    style="padding: 15px 30px;"
                    @click="gotoEditer"
                  >
                    ADD NEW WIDGET
                  </el-button>
                </div>
              </div>
              <div v-else>
                <transition name="fc-scale-vertical">
                  <dashboard-filter-container
                    class="position-sticky top-0 dashboard-filter-container"
                    ref="dbFilterContainer"
                    v-if="dashboardTabId && !dashboardTabLoading"
                    v-show="showDashboardFilterBar"
                    :filterEditMode.sync="filterEditMode"
                    :dashboardFilterObj="dashboardFilterObj"
                    :dashboardTabId="dashboardTabId"
                    @dbUserFilters="userFilterChanged"
                    @dbFilterConfigSaved="handleDbFilterConfigSaved"
                  ></dashboard-filter-container>
                </transition>
                <div class="scrollable gridstack-container">
                  <GridstackLayout
                    v-if="showGridstackComponent"
                    ref="gridstack"
                    :layout.sync="dashboardLayout"
                    :column="96"
                    :float="false"
                    margin="5px"
                    rowHeight="15px"
                    :static="viewOrEdit == 'edit' ? false : true"
                    :minRows="12"
                    :disableOneColumnMode="true"
                  >
                    <!-- Don't do method binding here inorder to make the code look beautiful, the methods will get called on eachtick when they are bound to the
                template, this component is heavy, method binding will might cause performance issues. Doing method binding on maxW, minW, etc. won't let the CPU to relax.  -->
                    <GridstackItem
                      v-for="item in dashboardLayout"
                      :item-content-style="sectionStyle[item.id]"
                      :class="{ 'gridstack-group': item.type == 'section' }"
                      :itemContentCustomClass="['shadow-widget']"
                      class="dashboard-f-widget"
                      :x="item.x"
                      :y="item.y"
                      :w="item.w"
                      :h="item.h"
                      :id="item.id"
                      :key="item.id"
                      :noResize="
                        item.hasOwnProperty('children') ||
                          (widgetConfigMap.hasOwnProperty(item.id)
                            ? widgetConfigMap[item.id].noResize
                            : false)
                      "
                    >
                      <SectionContainer
                        v-if="item.hasOwnProperty('children')"
                        :section="item"
                        :viewOrEdit="viewOrEdit"
                        @resizeSection="resizeSection"
                      >
                        <GridstackSection :id="item.id" :item="item">
                          <GridstackItem
                            v-for="child in item.children"
                            :key="child.id"
                            :id="child.id"
                            :w="child.w"
                            :h="child.h"
                            :x="child.x"
                            :y="child.y"
                            :itemContentCustomClass="['shadow-widget']"
                            :noResize="
                              widgetConfigMap.hasOwnProperty(child.id)
                                ? widgetConfigMap[child.id].noResize
                                : false
                            "
                          >
                            <WidgetWrapper
                              :item="child"
                              :loadImmediately="loadImmediately"
                              :printMode="printMode"
                              :isLazyDashboard="isLazyDashboard"
                              :widgetConfig="widgetConfigMap[child.id] || {}"
                              :viewOrEdit="viewOrEdit"
                              :hideTimelineFilterInsideWidget="
                                hideTimelineFilterInsideWidget
                              "
                              :updateWidget="updateWidget"
                            ></WidgetWrapper>
                          </GridstackItem>
                        </GridstackSection>
                      </SectionContainer>
                      <template v-else>
                        <!-- While debugging switch off the isLazyDashboard, if isLazyDashboard is set to true then the widgets won't load
                         until they are in the view port, the actions APIs loads the widgets, if isLazyDashboard is enabled then actions API won't
                         load the widget, it will set all the data required to load the widget inside the widget and quit and
                         the widget will only load once it enters the viewpoint using the data provided by the actions API,
                         this might look like a bug, but this a feature.
                    -->
                        <WidgetWrapper
                          :isLazyDashboard="isLazyDashboard"
                          :printMode="printMode"
                          :item="item"
                          :loadImmediately="loadImmediately"
                          :widgetConfig="widgetConfigMap[item.id] || {}"
                          :viewOrEdit="viewOrEdit"
                          :hideTimelineFilterInsideWidget="
                            hideTimelineFilterInsideWidget
                          "
                          :updateWidget="updateWidget"
                        ></WidgetWrapper>
                      </template>
                    </GridstackItem>
                  </GridstackLayout>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <PopupView ref="dashboardPopupView"></PopupView>
  </div>
</template>
<script>
//
import Vue from 'vue'
import VerticalTabSelector from './VerticalTabSelector'
import DashboardActionsMixin from 'src/pages/new-dashboard/actions/DashboardActionsMixin.js'
import WidgetWrapper from 'src/pages/new-dashboard/components/widgets/WidgetWrapper.vue'
import SectionContainer from 'src/pages/new-dashboard/components/groups/SectionContainer.vue'
import cloneDeep from 'lodash/cloneDeep'
import CubeLoader from 'src/pages/new-dashboard/components/CubeLoader.vue'
import { API } from '@facilio/api'
import {
  GridstackItem,
  GridstackLayout,
  GridstackSection,
} from '@facilio/ui/dashboard'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import tooltip from '@/graph/mixins/tooltip'
import DashboardFilterContainer from 'src/pages/new-dashboard/components/widgets/filters/DashboardFilterContainer.vue'
import DashboardFilterMixin from 'src/pages/new-dashboard/components/widgets/filters/DashboardFilterMixin.js'
import DashboardTimelineFilter from 'src/pages/new-dashboard/components/widgets/DashboardTimelineFilter.vue'
import { isEmpty } from '@facilio/utils/validation'
import DashboardTopTab from './DashboardTopTab'
/// import { eventBus } from '@/page/widget/utils/eventBus'
import { mapGetters, mapState } from 'vuex'

import { isWebTabsEnabled } from '@facilio/router'
import { getApp } from '@facilio/router'
import DashboardMixin from './DashboardMixin.js'
import PopupView from 'src/pages/new-dashboard/components/PopupView.vue'
const printPageSize = 122
const gridColumn = 96
export default {
  mixins: [
    DashboardActionsMixin,
    ReportHelper,
    DashboardFilterMixin,
    DashboardMixin,
  ],
  props: {
    printMode: { type: Boolean, default: false },
    isTv: { type: Boolean, default: false },
    sites: {},
    currentDashboard: {},
    buildings: {},
    refresh: { type: Boolean, default: false },
  },
  components: {
    VerticalTabSelector,
    PopupView,
    WidgetWrapper,
    GridstackLayout,
    CubeLoader,
    GridstackItem,
    SectionContainer,
    GridstackSection,
    DashboardFilterContainer,
    DashboardTimelineFilter,
    DashboardTopTab,
  },
  data() {
    return {
      loadImmediately: false,
      timelineFilter: {},
      widgetConfigMap: {},
      rerender: true,
      margin: 10,
      showReportComments: false,
      reportCommentMeta: {},
      loading: true,
      dashboard: null,
      isMobileDashboard: false,
      dashboardLayout: [],
      removeChartList: [],
      chartDeleted: false,
      shareTo: 2,
      sharedUsers: [],
      sharedRoles: [],
      sharedGroups: [],
      fullScrnHeader: '',
      dashboardTabContexts: null,
      dashboardTabLoading: false,
      printed: false,
      printPageSize,
      gridColumn,
      listWidgets: [],
      listWidgetToRender: 0,
      canPrint: false,
      showDashboardFilterBar: false,
      showDbFilterIcon: true,
      dashboardTabPlacement: 2,
      isPermission: true,
    }
  },
  created() {
    this.subscribeEventBus()
  },
  mounted() {
    this.loadDashboard()
    this.registerGlobalComponents()
  },
  computed: {
    dashboardTimelineFilterRange() {
      if (!isEmpty(this.dbTimelineFilter)) {
        return (
          this.$helpers
            .getOrgMoment(this.dbTimelineFilter.startTime)
            .format('DD/MM/YYYY') +
          ' - ' +
          this.$helpers
            .getOrgMoment(this.dbTimelineFilter.endTime)
            .format('DD/MM/YYYY')
        )
      }
      return null
    },
    getScopedSitesToPrintPdf() {
      return this.$route?.query?.scoping_site
    },
    isTimelineFilterEnabled() {
      const { dashboardFilterObj } = this ?? {}
      const { isTimelineFilterEnabled } = dashboardFilterObj ?? {}
      return isTimelineFilterEnabled ?? false
    },
    userFilterList() {
      const { dashboardFilterObj } = this ?? {}
      const { dashboardUserFilters: userFilterList } = dashboardFilterObj ?? {}
      return userFilterList ?? []
    },
    dashboardId() {
      const {
        dashboard: { id: dashboardId },
      } = this ?? {}
      return dashboardId
    },
    viewOrEdit() {
      if (this.filterEditMode) {
        return 'configFilter'
      }
      return 'view'
    },
    hideTimelineFilterInsideWidget() {
      return this.dashboardFilterObj
        ? this.dashboardFilterObj.hideFilterInsideWidgets
        : false
    },
    tabsEnabled() {
      let { dashboardTabContexts, dashboardTabPlacement } = this
      if (
        dashboardTabContexts &&
        dashboardTabContexts.length &&
        dashboardTabPlacement === 2
      ) {
        return true
      }
      return false
    },
    ...mapGetters(['getCurrentUser']),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    // widgets lazy loading .support set flag true to load reports  only when they come into view
    //remove when released
    isLazyDashboard() {
      return true
    },

    dashboardMeta() {
      if (this.dashboard.clientMetaJsonString) {
        return JSON.parse(this.dashboard.clientMetaJsonString)
      }
      return null
    },
    dashboardLink() {
      // this.currentDashboard is used on looprunner.
      if (this?.currentDashboard?.linkName) {
        return this?.currentDashboard?.linkName
      } else {
        return this.$route.params.dashboardlink
      }
    },
    editMode() {
      return false
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
    dashboardTabId() {
      return this.$route?.query?.tab ? parseInt(this.$route?.query?.tab) : null
    },
  },
  destroyed() {
    tooltip.hideTooltip()
  },
  watch: {
    refresh() {
      this.resetActionsPayload()
      this.loadDashboard()
    },
    dashboardLink() {
      this.resetActionsPayload()
      this.loadDashboard()
    },
    dashboardTabId: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.resetActionsPayload()
        this.loadDashboardTabWidgets()
        this.openGraphicsFolder()
      }
    },
  },
  beforeDestroy() {
    this.unsubscribeEventBus()
  },
  methods: {
    registerGlobalComponents() {
      if (this.$refs['dashboardPopupView']) {
        Vue.prototype.$popupView = this.$refs['dashboardPopupView']
      }
    },
    resetActionsPayload() {
      this.recentActionsPayload = cloneDeep({})
      this.recentUserFilterPayload = cloneDeep({})
      this.recentTimelineFilterPayload = cloneDeep({})
    },
    validateDashboardAccess() {
      this.isPermission = true
      if (isWebTabsEnabled()) {
        return this.tabHasPermission(`VIEW`)
      }
      return this.$hasPermission('dashboard:READ')
    },

    openReportComments(commentMeta) {
      if (commentMeta.open) {
        this.showReportComments = true
        this.$set(this.reportCommentMeta, 'id', commentMeta.id)
      } else {
        this.showReportComments = false
      }
    },
    isPortalApp() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
    loadDashboard() {
      const self = this
      if (!isEmpty(this.dashboardLink)) {
        if (!this.isPortalApp() && !this.validateDashboardAccess()) {
          this.$message("You don't have dashboard access")
          this.isPermission = false
          this.$emit('updatePermission', this.isPermission)
        } else {
          self.loading = true
          this.clearDashboardFilters()
          this.showDashboardFilterBar = false
          self.dashboardLayout = []
          let url = 'v3/dashboard/' + this.dashboardLink
          API.get(url, {}, { force: true }).then(function(response) {
            self.dashboard = response.data.dashboardJson[0]
            self.$emit('emitDashboard', cloneDeep(self.dashboard))

            // Mobile dashboard...
            if (self.dashboard.mobileEnabled) {
              self.$emit('mobiledashboard', self.dashboard.mobileEnabled)
            } else {
              self.$emit('mobiledashboard', false)
            }

            // Dashboard tab placement, top or side...
            if (self.dashboard.dashboardTabPlacement === -1) {
              self.dashboardTabPlacement = 2
            } else {
              self.dashboardTabPlacement = self.dashboard.dashboardTabPlacement
            }

            // If there are dashboard tabs then load the tab's widgets or if the tab id is not set,
            // set the tab id on the route, the route is getting watched using a watch, this method will be
            // will be invoked again...
            if (self.dashboard.tabs && self.dashboard.tabs.length > 0) {
              let tabs = self.dashboard.tabs
              self.dashboardTabContexts = tabs
              if (!self.dashboardTabId) {
                self.$router.replace(
                  self.$route.path + '?tab=' + self.dashboardTabContexts[0].id
                )
              } else {
                for (let key in self.dashboardTabContexts) {
                  self.$set(self.dashboardTabContexts[key], 'expand', false)
                }
                self.loadDashboardTabWidgets()
                self.openGraphicsFolder()
              }
            } else {
              self.dashboardTabContexts = null
              const filters = self.dashboard.dashboardFilter
              self.makeFilters(filters)
              self.prepareDashboardLayout(cloneDeep(self.dashboard.children))
            }
            self.loading = false
            self.setTitle(self.dashboard.label)
          })
        }
      }
    },
    setTitle(title = '') {
      this.fullScrnHeader = title
    },
    makeFilters(filter) {
      try {
        this.initializeDashboardFilters(filter)
      } catch (e) {
        console.error('error initializing dashboard filters', e)
        this.showDbFilterIcon = false
        //dont break dashboard loading if any issue with filter,just hide icon
      }
    },
    async loadDashboardTabWidgets() {
      const self = this
      if (this.dashboardTabId) {
        this.dashboardTabLoading = true
        this.clearDashboardFilters()
        this.showDashboardFilterBar = false
        const { data, error } = await API.get(
          'v3/dashboard/tab/' + this.dashboardTabId
        )
        const { dashboardTabContext } = data ?? {}
        if (!isEmpty(dashboardTabContext)) {
          const { clientWidgetJson, dashboardFilter } =
            dashboardTabContext ?? {}
          self.dashboard['children'] = clientWidgetJson
          self.makeFilters(dashboardFilter)
          self.prepareDashboardLayout(cloneDeep(clientWidgetJson))
        } else {
          self.$route.replace(self.$route.path)
        }
        self.dashboardTabLoading = false
      }
    },
    expand(folder) {
      folder.expand = !folder.expand
    },
    changeDashboardTabId(id) {
      if (id && id > 0) {
        this.$router.replace({ query: { ...this.$route.query, tab: id } })
      }
    },
    openGraphicsFolder() {
      let self = this
      for (let key in self.dashboardTabContexts) {
        if (
          self.dashboardTabId === self.dashboardTabContexts[key].id &&
          self.dashboardTabContexts[key].childTabs
        ) {
          self.dashboardTabContexts[key].expand = true
        }
        if (self.dashboardTabContexts[key].childTabs) {
          for (let child of self.dashboardTabContexts[key].childTabs) {
            if (self.dashboardTabId === child.id) {
              self.dashboardTabContexts[key].expand = true
            }
          }
        }
      }
    },
    editFromDropDown() {
      this.showDashboardFilterBar = true
      this.$refs['dbFilterContainer'].enterEditMode()
    },
    async handleDbFilterConfigSaved(payload) {
      this.showDashboardFilterWidgetConfigDialog = false
      this.filterEditMode = false
      let { data, error } = await API.post('v2/dashboardFilter/update', payload)

      if (error) {
        this.$message.error(error)
      }
      this.loadDashboard()
    },
  },
}
</script>

<style lang="scss">
@import './styles/dashboard-styles.scss';
</style>
<style lang="scss">
.dashboard-container {
  flex-grow: 1;
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
.pB145 {
  padding-bottom: 145px;
}
.pB105 {
  padding-bottom: 105px;
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
.dashboard-container .energy_background {
  background-image: linear-gradient(to bottom, #ec637f, #843f78);
}
.dashboard-container .energy-cost {
  background-image: linear-gradient(to left, #7039a9, #4a2973);
  color: #fff;
  height: 100%;
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
}
.dashboard-container .db-container {
  border: none;
}
text.Yaxis-label {
  font-size: 1em !important;
}

.dashboard-container .axis text {
  font-size: 10px;
}
.dashboard-container .fc-widget {
  position: relative;
}

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
  min-width: 250px;
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
  max-width: inherit !important;
  width: inherit;
  white-space: initial;
  word-break: break-word;
  padding-right: 0;
}
.dashboard-container
  .fc-list-view-table
  tbody
  tr.tablerow
  td:first-child
  div
  .severityTag {
  padding-left: 10px;
  padding-right: 10px;
}
.list-widget-row .fc-chart-table-body .severityTag {
  white-space: nowrap !important;
}
.dashboard-container .fc-list-view-table td div {
  word-break: keep-all;
}
.dashboard-container .fc-list-view-table td {
  padding-left: 14px !important;
  word-break: break-word;
}
.dashboard-container .fc-list-view-table tbody tr td:nth-child(2) {
  min-width: 150px;
  white-space: normal;
}

.dashboard-container .fc-list-view-table td {
  word-break: break-all;
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
}
.dashboard-container .legend.legendsAll {
  padding-top: 0;
}
.dashboard-container .emptyLegends {
  padding-top: 55px;
}

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
.fc-black-theme .dashboard-lookup-filter .filter-label-text {
  color: #fff;
}
.fc-black-theme .nowo-label {
  color: #fff;
}
.fc-black-theme .dashboard-container .fc-widget-label {
  color: #fff;
}
.fc-black-theme .dashboard-lookup-filter .rm-arrow .el-input__inner {
  background: #170238 !important;
  color: #fff;
}
.fc-black-theme .dashboard-lookup-filter .el-input__inner {
  background: #170238 !important;
  color: #fff;
}
.fc-black-theme .dashboard-filter-container {
  background: rgba(22, 13, 71, 1);
}
.fc-black-theme svg.icon.icon-sm-md {
  color: white;
}
.fc-black-theme .card-wrapper {
  background-color: #170238;
}
.fc-black-theme .dashboard-filter-portal .dashboard-filter-toggle {
  background-color: #170238;
}
.fc-black-theme .dashboard-filter-portal .dashboard-filter-toggle.is-open {
  border: solid 1px #fff;
}
.fc-black-theme .dfolder-name.active {
  background: #090f2e;
}
.fc-black-theme .dashboard-tab-sidebar {
  background: #170238;
}
.fc-black-theme .dfolder-name {
  color: white;
}
.fc-black-theme .new-create-dashboard-header {
  background: #170238;
}
.fc-black-theme .primary-font {
  color: #fff;
}
.fc-black-theme .fc-create-btn {
  box-shadow: none;
  -webkit-box-shadow: none;
}
.fc-black-theme .dashboard-folder:hover {
  background-color: unset;
}
.fc-black-theme .fc-widget-moreicon-vertical {
  color: #fff;
  background: #090f2e;
}
/* .fc-black-theme .empty-container{
  background: rgba(23,2,56,1);
}
.fc-black-theme .pivot-error-container{
  background: rgba(23,2,56,1);
} */
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
  display: block !important;
}
body:-webkit-full-screen .el-popper {
  z-index: 100000000000;
}
body:-webkit-full-screen .layout-header,
body:-webkit-full-screen .fc-layout-aside,
body:-webkit-full-screen .subheader-section {
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
.dashboard-header-pdf {
  display: none;
}
</style>
<style>
@media print {
  body {
    height: 100%;
    -webkit-print-color-adjust: exact;
  }
  .dashboard-print > .portal-dashboard-layout {
    padding: 10px !important;
    overflow: inherit !important;
  }
  .dashboard-filter-container {
    display: none;
  }
  .dashboard-header-pdf {
    display: block;
  }
  @page {
    margin: 0;
    overflow: visible !important;
  }
  .grid-stack-item-content,
  .fc-widget,
  .dashboard-f-widget,
  .fc-newchart-container {
    position: relative;
    page-break-before: auto;
    page-break-after: auto;
    page-break-inside: avoid;
    display: block;
    float: none;
  }
  .dashboard-container .fc-widget {
    page-break-inside: avoid;
  }

  .fc-new-chart svg,
  .fc-new-chart {
    position: relative;
    display: block;
    page-break-inside: avoid;
  }
  .fc-new-chart svg {
    margin: 0;
    display: inline-block !important;
  }
  .fc-newchart-container {
    page-break-inside: avoid;
  }
}

.dashboard-widget-view .single-dashboard .fc-widget-header {
  display: none !important;
}
.dashboard-widget-view .scrollable.dashboardmainlayout.single-dashboard {
  padding: 0 !important;
}
.dashboard-widget-view .single-dashboard {
  height: 100vh !important;
  overflow: hidden;
  width: 100vw !important;
}
.dashboard-widget-view .single-dashboard {
  height: 100% !important;
  width: 100% !important;
  transform: none !important;
}
.dashboard-widget-view .single-dashboard .fc-widget-header {
  display: none !important;
}
.dashboardviewer {
  display: flex;
  flex-direction: column;
  position: initial;
}
.pB110 {
  padding-bottom: 110px;
}
</style>
<style lang="scss">
.dashboardviewer {
  .fc-black-theme .view-panel .view-manager-btn .label {
    color: white;
  }
  .fc-black-theme .view-panel .view-manager-btn {
    background: #090f2e;
  }
  .fc-black-theme .dashboard-tab-sidebar {
    background: #170238;
  }
  .fc-black-theme rect.tile_empty {
    fill: #7976764a !important;
  }
  .fc-black-theme .heatMap line,
  .fc-black-theme .heatMap .y.axis path {
    stroke: #393b59 !important;
  }
  .fc-black-theme .dashboard-container .fc-widget {
    border: 0 !important;
    box-shadow: none;
  }
  .fc-black-theme .el-table__fixed {
    background: #170238;
  }
  .fc-black-theme .el-table__body-wrapper {
    background: #170238;
  }
}
</style>

<style lang="scss" scoped>
.dbHeadingTxt {
  background-color: rgb(255, 255, 255);
}
// This style is temporary and will be removed after fixing print issues from puppteer.
// This is to differentiate between printouts of new and old dashboard.
.new-dashboard-heading {
  text-decoration: underline;
}
</style>
