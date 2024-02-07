<template>
  <div>
    <div>
      <div
        v-if="isPermission"
        v-resize
        @resize="onResize"
        class="dashboardmainlayout dashboardviewer height100vh"
        v-bind:class="{
          mobiledashboard: this.isMobileDashboard,
          'single-dashboard': dashboardLayout && dashboardLayout.length === 1,
          pB110: !isTv,
        }"
      >
        <dashboard-filter-widget-config-dialog
          :widget="widgetForFilterConfig"
          :visibility.sync="showDashboardFilterWidgetConfigDialog"
          v-if="showDashboardFilterWidgetConfigDialog"
          @widgetFilterConfigChange="saveWidgetFilterSettings"
        >
        </dashboard-filter-widget-config-dialog>
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
            @timelineFilterChanged="handleTimelineFilterChange"
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

        <div
          class="full-screen-banner dbHeadingTxt"
          :class="fullScreenClass"
          v-if="fullScrnHeader !== null && $account.org.id === 116"
        >
          <img
            class=" flLeft emirates_logo"
            src="~statics/emirates/emirates_logo.png"
          />
          <span class="baner-header ">EMIRATES FACILITIES MANAGEMENT</span>
          <img class=" flLeft flight" src="~statics/emirates/flight.png" />
          <img
            class=" flLeft facilio-logo"
            src="~statics/emirates/facilio_prod_logo1.png"
          />
        </div>
        <div
          v-if="fullScrnHeader !== null && showDashboardTitle"
          class="dbHeadingTxt"
          :class="fullScreenClass"
        >
          <slot name="dashboardNameList">{{ fullScrnHeader }}</slot>
        </div>
        <!-- Applying filter for non tabbed dashboard-->
        <transition name="fc-scale-vertical">
          <dashboard-filter-container
            class="dashboard-filter-container"
            ref="dbFilterContainer"
            v-if="!(dashboardTabId || loading)"
            v-show="showDashboardFilterBar"
            :dashboardFilterObj="dashboardFilterObj"
            :dashboardId="dashboard.id"
            @dbUserFilters="handleDbUserFilterChange"
            @dbFilterConfigSaved="handleDbFilterConfigSaved"
            :filterEditMode.sync="filterEditMode"
            :hideFilterInsideWidgets.sync="hideFilterInsideWidgets"
          ></dashboard-filter-container>
        </transition>
        <div v-if="loading || !dashboardLayout">
          <div class="sk-cube-grid">
            <div class="sk-cube sk-cube1"></div>
            <div class="sk-cube sk-cube2"></div>
            <div class="sk-cube sk-cube3"></div>
            <div class="sk-cube sk-cube4"></div>
            <div class="sk-cube sk-cube5"></div>
            <div class="sk-cube sk-cube6"></div>
            <div class="sk-cube sk-cube7"></div>
            <div class="sk-cube sk-cube8"></div>
            <div class="sk-cube sk-cube9"></div>
          </div>
        </div>
        <div
          class="dashboard-container"
          style="padding-bottom:0px;"
          :class="{ editmode: editMode }"
          v-else
        >
          <!-- print heading -->
          <template v-if="isPrinting">
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
                  <el-col :span="6" :offset="4">
                    <div class="pm-pdf-heading text-center">
                      {{ dashboard.label }}
                    </div>
                    <div
                      v-if="
                        $helpers.isPortalUser() &&
                          this.$getProperty(this.$route, `query.tab`)
                      "
                      class="pm-pdf-heading text-center"
                    >
                      {{ setTabName() }}
                    </div>
                  </el-col>
                  <el-col :span="6" :offset="4">
                    <div class="pm-pdf-heading text-right">
                      {{ dashboardTimelineFilterRange }}
                    </div>
                  </el-col>
                </el-row>
                <!-- <div class="facil-logo">
            <img src="~assets/facilio-blue-logo.svg" style="width: 80px;" />
          </div> -->
              </div>
            </div>
          </template>
          <!-- print heading end  -->
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
                  Add Widget<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="static"
                    >Pre-built Widget</el-dropdown-item
                  >
                  <el-dropdown-item command="chart"
                    >Chart Widget</el-dropdown-item
                  >
                  <el-dropdown-item command="list"
                    >List Widget</el-dropdown-item
                  >
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
          <dashboard-template-filter
            v-if="
              typeof $route.query.showTemplate !== 'undefined' &&
                ($route.query.showTemplate === true ||
                  $route.query.showTemplate === 'true')
            "
            :dashboardTemplateConfig="dashboardTemplate"
          ></dashboard-template-filter>
          <company-listslider
            v-if="
              showPortfolio &&
                showPortfolio === 'energydata' &&
                $account.org.id !== 210
            "
            :dashboardLink="dashboardLink"
            style="cursor:pointer; padding: 15px 15px 0px 15px;"
            :sites="sites"
          ></company-listslider>
          <alarm-company-listslider
            v-if="showPortfolio && showPortfolio === 'alarm'"
            style="cursor:pointer; padding: 15px 15px 0px 15px;"
          ></alarm-company-listslider>
          <dashboard-filter
            v-if="
              typeof $route.query.showFilter !== 'undefined' &&
                ($route.query.showFilter === true ||
                  $route.query.showFilter === 'true')
            "
          ></dashboard-filter>
          <div
            class="height100"
            :class="
              dashboardTabContexts && dashboardTabContexts.length > 0
                ? 'overflow-y'
                : ''
            "
          >
            <el-row
              v-if="dashboardTabContexts && dashboardTabContexts.length > 0"
            >
              <el-col :span="4" v-if="dashboardTabPlacement === 2">
                <div class="dashboard-tab-sidebar">
                  <div
                    class="dashboard-tab-sidebar-scroll"
                    v-if="
                      dashboardTabContexts && dashboardTabContexts.length > 0
                    "
                  >
                    <div
                      v-for="(folder, index) in dashboardTabContexts"
                      :key="index"
                      class="dashboard-folder-container"
                    >
                      <div
                        @click="changeDashboardTabId(folder.id), expand(folder)"
                        class="dfolder-name"
                        v-bind:class="{ active: dashboardTabId === folder.id }"
                      >
                        <div class="mL5">
                          <span>{{ folder.name }}</span>
                          <div class="dfolder-icon fR" v-if="folder.childTabs">
                            <i
                              class="el-icon-arrow-down"
                              v-if="folder.expand"
                            ></i>
                            <i class="el-icon-arrow-right" v-else></i>
                          </div>
                        </div>
                      </div>
                      <div v-show="folder.expand" class="dfolder-children">
                        <div
                          @click="changeDashboardTabId(childFolder.id)"
                          v-for="(childFolder, cId) in folder.childTabs"
                          :key="cId"
                          v-bind:class="{
                            active: dashboardTabId === childFolder.id,
                          }"
                        >
                          <span class="mL5">{{ childFolder.name }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :span="24" v-if="dashboardTabPlacement === 1">
                <dashboard-top-tab
                  :dashboardTabContexts="dashboardTabContexts"
                  :dashboardTabId="dashboardTabId"
                  @changeDashboardTabId="changeDashboardTabId"
                ></dashboard-top-tab>
              </el-col>
              <el-col
                :span="dashboardTabPlacement === 1 ? 24 : 20"
                class="scrollable pB180"
              >
                <!-- TO:Do change this -->
                <!-- THIS ENTIRE SECTION IS DUPLICATED FOR TABBED DASHBOARD and Non tabbed -->
                <dashboard-filter-container
                  class="position-sticky top-0 dashboard-filter-container"
                  ref="dbFilterContainer"
                  v-if="dashboardTabId && !dashboardTabLoading"
                  v-show="showDashboardFilterBar"
                  :dashboardFilterObj="dashboardFilterObj"
                  :dashboardTabId="dashboardTabId"
                  :filterEditMode.sync="filterEditMode"
                  @dbUserFilters="handleDbUserFilterChange"
                  @dbFilterConfigSaved="handleDbFilterConfigSaved"
                ></dashboard-filter-container>
                <div v-if="dashboardTabLoading || !dashboardLayout">
                  <div class="sk-cube-grid">
                    <div class="sk-cube sk-cube1"></div>
                    <div class="sk-cube sk-cube2"></div>
                    <div class="sk-cube sk-cube3"></div>
                    <div class="sk-cube sk-cube4"></div>
                    <div class="sk-cube sk-cube5"></div>
                    <div class="sk-cube sk-cube6"></div>
                    <div class="sk-cube sk-cube7"></div>
                    <div class="sk-cube sk-cube8"></div>
                    <div class="sk-cube sk-cube9"></div>
                  </div>
                </div>
                <div
                  class="row"
                  v-if="!dashboardTabLoading && !dashboardLayout.length"
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
                      @click="geteditdashboardLink"
                    >
                      ADD NEW WIDGET
                    </el-button>
                  </div>
                </div>
                <template v-if="rerender">
                  <grid-layout
                    :layout="dashboardLayout"
                    :col-num="96"
                    :rowHeight="rowHeight"
                    :is-draggable="editMode"
                    :is-resizable="editMode"
                    :margin="[margin, margin]"
                    v-if="!dashboardTabLoading && dashboardLayout.length"
                  >
                    <grid-item
                      class="dashboard-f-widget"
                      v-for="(item, index) in dashboardLayout"
                      :x="item.x"
                      :y="item.y"
                      :w="item.w"
                      :h="item.h"
                      :i="item.i"
                      :key="index"
                      drag-allow-from=".fc-widget-header, .dragabale-card, .map-drag-area"
                      drag-ignore-from=".fc-widget-body"
                      :is-resizable="editMode && item.widget.type !== 'static'"
                    >
                      <f-mobile-widget
                        :type="item.widget.type"
                        :widget="item.widget"
                        :grid="item"
                        :key="index"
                        :rowHeight="rowHeight"
                        :currentDashboard="currentDashboard"
                        :dashboardObj="dashboard"
                        :dashboard="dashboard ? dashboard.id : null"
                        @deletechart="deleteChart"
                        :mode="editMode"
                        v-if="
                          $route.meta &&
                            $route.meta.dashboardlayout &&
                            $route.meta.dashboardlayout === 'mobile'
                        "
                      ></f-mobile-widget>
                      <CardWrapper
                        :showFilterConfig="filterEditMode"
                        v-else-if="item.widget.type === 'card'"
                        :ref="'widget[' + index + ']'"
                        :isLazyDashboard="isLazyDashboard"
                        :widget="item.widget"
                        :dashboardId="dashboard ? dashboard.id : null"
                        :dbTimelineFilter="timelineFilterMap[item.widget.id]"
                        :dbFilterJson="dbUserFilters[item.widget.id]"
                        :dbCustomScriptFilter="
                          dbCustomScriptFilter[item.widget.id]
                        "
                        @removeCard="deleteCard"
                        @editCard="editCard(item)"
                        @editWidgetFilterConfig="editWidgetFilterConfig"
                      ></CardWrapper>
                      <f-widget
                        v-else
                        :isLazyDashboard="isLazyDashboard"
                        :showFilterConfig="filterEditMode"
                        @editWidgetFilterConfig="editWidgetFilterConfig"
                        @showComments="openReportComments"
                        :type="item.widget.type"
                        :widget="item.widget"
                        :grid="item"
                        :key="index"
                        :rowHeight="rowHeight"
                        :currentDashboard="currentDashboard"
                        :dashboardObj="dashboard"
                        :dbTimelineFilter="timelineFilterMap[item.widget.id]"
                        :dbFilterJson="dbUserFilters[item.widget.id]"
                        :dashboard="dashboard ? dashboard.id : null"
                        @deletechart="deleteChart"
                        :mode="editMode"
                      ></f-widget>
                    </grid-item>
                  </grid-layout>
                </template>
              </el-col>
            </el-row>
            <div v-else>
              <div
                class="row"
                v-if="!dashboardLayout.length && !listWidgets.length"
              >
                <div
                  class="col-12 flex-middle flex-direction-column justify-content-center height80vh pT30"
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
                    @click="geteditdashboardLink"
                  >
                    ADD NEW WIDGET
                  </el-button>
                </div>
              </div>
              <!--tabbed dashboard print ,prints all tabs. to do :filters  not handled in tabbed db print, must handle per tab.its own filters and show filter state for each tab -->
              <template v-if="printTabLayout.length">
                <div v-for="(tab, tindex) in printTabLayout" :key="tindex">
                  <div
                    class="label-txt-black f18 text-left fwBold text-uppercase pT20 pB20 pL30"
                  >
                    {{ tab.tabName }}
                  </div>
                  <template v-if="rerender">
                    <grid-layout
                      :key="tab.id"
                      :layout="tab.layout"
                      :col-num="96"
                      :rowHeight="rowHeight"
                      :is-draggable="editMode"
                      :is-resizable="editMode"
                      :use-css-transforms="true"
                      :margin="[margin, margin]"
                    >
                      <grid-item
                        class="dashboard-f-widget"
                        v-for="(item, index) in tab.layout"
                        :x="item.x"
                        :y="item.y"
                        :w="item.w"
                        :h="item.h"
                        :i="item.i"
                        :key="index"
                        drag-allow-from=".fc-widget-header, .dragabale-card, .map-drag-area"
                        drag-ignore-from=".fc-widget-body"
                        :is-resizable="
                          editMode && item.widget.type !== 'static'
                        "
                      >
                        <CardWrapper
                          :showFilterConfig="filterEditMode"
                          v-if="item.widget.type === 'card'"
                          :ref="'widget[' + index + ']'"
                          :widget="item.widget"
                          :dashboardId="dashboard ? dashboard.id : null"
                          @removeCard="deleteCard"
                          @editCard="editCard(item)"
                          @editWidgetFilterConfig="editWidgetFilterConfig"
                        ></CardWrapper>
                        <f-widget
                          :showFilterConfig="filterEditMode"
                          @editWidgetFilterConfig="editWidgetFilterConfig"
                          @showComments="openReportComments"
                          :type="item.widget.type"
                          :widget="item.widget"
                          :grid="item"
                          :key="index"
                          :rowHeight="rowHeight"
                          :dashboardObj="dashboard"
                          :currentDashboard="currentDashboard"
                          :dashboard="dashboard ? dashboard.id : null"
                          @deletechart="deleteChart"
                          :mode="editMode"
                          v-else-if="item.widget.type !== 'dummy'"
                        ></f-widget>
                      </grid-item>
                    </grid-layout>
                  </template>
                </div>
              </template>
              <grid-layout
                :layout="dashboardLayout"
                :col-num="96"
                :rowHeight="rowHeight"
                :is-draggable="editMode"
                :is-resizable="editMode"
                :use-css-transforms="true"
                :margin="[margin, margin]"
                v-else-if="rerender && dashboardLayout.length"
              >
                <grid-item
                  class="dashboard-f-widget"
                  v-for="(item, index) in dashboardLayout"
                  :x="item.x"
                  :y="item.y"
                  :w="item.w"
                  :h="item.h"
                  :i="item.i"
                  :key="index"
                  drag-allow-from=".fc-widget-header, .dragabale-card, .map-drag-area"
                  drag-ignore-from=".fc-widget-body"
                  :is-resizable="editMode && item.widget.type !== 'static'"
                >
                  <f-mobile-widget
                    :type="item.widget.type"
                    :widget="item.widget"
                    :grid="item"
                    :key="index"
                    :rowHeight="rowHeight"
                    :currentDashboard="currentDashboard"
                    :dashboardObj="dashboard"
                    :dashboard="dashboard ? dashboard.id : null"
                    @deletechart="deleteChart"
                    :mode="editMode"
                    v-if="
                      $route.meta &&
                        $route.meta.dashboardlayout &&
                        $route.meta.dashboardlayout === 'mobile'
                    "
                  ></f-mobile-widget>
                  <CardWrapper
                    :showFilterConfig="filterEditMode"
                    v-else-if="item.widget.type === 'card'"
                    :ref="'widget[' + index + ']'"
                    :widget="item.widget"
                    :dashboardId="dashboard ? dashboard.id : null"
                    :dbTimelineFilter="timelineFilterMap[item.widget.id]"
                    :dbFilterJson="dbUserFilters[item.widget.id]"
                    :dbCustomScriptFilter="dbCustomScriptFilter[item.widget.id]"
                    :isLazyDashboard="isLazyDashboard"
                    @removeCard="deleteCard"
                    @editCard="editCard(item)"
                    @editWidgetFilterConfig="editWidgetFilterConfig"
                  ></CardWrapper>
                  <f-widget
                    :showFilterConfig="filterEditMode"
                    @editWidgetFilterConfig="editWidgetFilterConfig"
                    @showComments="openReportComments"
                    :type="item.widget.type"
                    :widget="item.widget"
                    :grid="item"
                    :key="index"
                    :rowHeight="rowHeight"
                    :dashboardObj="dashboard"
                    :currentDashboard="currentDashboard"
                    :dashboard="dashboard ? dashboard.id : null"
                    @deletechart="deleteChart"
                    :mode="editMode"
                    :dbFilterJson="dbUserFilters[item.widget.id]"
                    :dbTimelineFilter="timelineFilterMap[item.widget.id]"
                    :isLazyDashboard="isLazyDashboard"
                    v-else-if="item.widget.type !== 'dummy'"
                  ></f-widget>
                </grid-item>
              </grid-layout>
            </div>
          </div>
          <template v-for="item in listWidgets">
            <f-list-widget
              class="p10"
              :key="item.widget.id"
              :dbFilterJson="dbUserFilters[item.widget.id]"
              :dbTimelineFilter="timelineFilterMap[item.widget.id]"
              :config="{
                widget: item.widget,
                currentDashboard: currentDashboard,
                dashboardObj: dashboard,
              }"
              :isPrinting="isPrinting"
            >
            </f-list-widget>
          </template>
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
      </div>
    </div>
    <PopupView ref="dashboardPopupView"></PopupView>
  </div>
</template>
<script>
import PopupView from 'src/pages/new-dashboard/components/PopupView.vue'
import Vue from 'vue'
import { API } from '@facilio/api'
import FWidget from './widget/FWidget'
import FListWidget from './widget/FListWidget'
import NewChartWidget from './forms/NewChartWidget'
import NewListWidget from './forms/NewListWidget'
import NewMapWidget from './forms/NewMapWidget'
import NewPrebuiltWidget from './forms/NewPrebuiltWidget'
import NewWebWidget from './forms/NewWebWidget'
import CompanyListslider from '@/CompanyListslider'
import AlarmCompanyListslider from '@/CompanySlider'
import { GridLayout, GridItem } from 'vue-grid-layout'
import DashboardFilter from './DashboardFilter'
import ReportHelper from 'pages/report/mixins/ReportHelper'
import tooltip from '@/graph/mixins/tooltip'
import FMobileWidget from './widget/WidgetLayouts/MobileWidget'
import DashboardTemplateFilter from '@/DashboardTemplateFilter'
import CardWrapper from 'pages/card-builder/components/CardWrapper'
import CardBuilderDashboardMixin from 'pages/card-builder/components/CardBuilderDashboardMixin'
import DashboardFilterContainer from 'pages/dashboard/dashboard-filters/DashboardFilterContainer'
import DashboardFilterMixin from 'pages/dashboard/dashboard-filters/DashboardFilterMixin'
import DashboardTimelineFilter from 'pages/dashboard/dashboard-filters/DashboardTimelineFilter'
import DashboardFilterWidgetConfigDialog from 'pages/dashboard/dashboard-filters/DashboardFilterWidgetConfigDialog'
import { isEmpty } from '@facilio/utils/validation'
import DashboardTopTab from './DashboardTopTab'

import { eventBus } from '@/page/widget/utils/eventBus'
import { mapGetters, mapState } from 'vuex'

import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import { getApp } from '@facilio/router'

const printPageSize = 122
const gridColumn = 96

export default {
  mixins: [ReportHelper, CardBuilderDashboardMixin, DashboardFilterMixin],
  props: {
    isTv: { type: Boolean, default: false },
    sites: {},
    currentDashboard: {},
    buildings: {},
    refresh: { type: Boolean, default: true },
  },
  components: {
    PopupView,
    NewChartWidget,
    NewListWidget,
    NewMapWidget,
    NewPrebuiltWidget,
    NewWebWidget,
    FWidget,
    FListWidget,
    GridLayout,
    GridItem,
    CompanyListslider,
    DashboardFilter,
    AlarmCompanyListslider,
    FMobileWidget,
    DashboardTemplateFilter,
    CardWrapper,
    DashboardFilterContainer,
    DashboardTimelineFilter,
    DashboardFilterWidgetConfigDialog,
    DashboardTopTab,
  },
  data() {
    return {
      rerender: true,
      rowHeight: 5,
      margin: 10,
      showReportComments: false,
      reportCommentMeta: {},
      loading: true,
      dashboard: null,
      isMobileDashboard: false,
      dashboardLayout: null,
      printTabLayout: [],
      removeChartList: [],
      chartDeleted: false,
      shareTo: 2,
      sharedUsers: [],
      sharedRoles: [],
      sharedGroups: [],
      tabName: null,
      fullScrnHeader: null,
      dashboardTabContexts: null,
      dashboardTabLoading: false,
      printed: false,
      printPageSize,
      gridColumn,
      listWidgets: [],
      listWidgetToRender: 0,
      canPrint: false,
      //dbfilter state->from dashboard filter mixin
      showDashboardFilterBar: false,
      showDbFilterIcon: true,
      dashboardTabPlacement: 2,
      isPermission: true,
    }
  },
  mounted() {
    this.registerGlobalComponents()
  },
  created() {
    eventBus.$on('editFilterClicked', this.editFromDropDown)
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings').then(() => {
      this.loadDashboard()
    })
  },
  beforeDestroy() {
    eventBus.$off('editFilterClicked', this.editFromDropDown)
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    // widgets lazy loading .support set flag true to load reports  only when they come into view
    //remove when released
    isLazyDashboard() {
      return !this.isPrinting
    },

    dashboardMeta() {
      if (this.dashboard.clientMetaJsonString) {
        return JSON.parse(this.dashboard.clientMetaJsonString)
      }
      return null
    },
    dashboardTemplate() {
      if (this.dashboardMeta) {
        return this.dashboardMeta.dashboardTemplate
      }
      return null
    },
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
    showPortfolio() {
      if (this.currentDashboard) {
        if (
          this.currentDashboard.linkName === 'portfolio' ||
          this.currentDashboard.linkName === 'maintenanceportfolio' ||
          this.currentDashboard.linkName === 'energyportfolio' ||
          this.currentDashboard.linkName === 'alarmportfolio' ||
          this.currentDashboard.linkName === 'commercial' ||
          this.currentDashboard.linkName === 'residential'
        ) {
          return this.currentDashboard.moduleName
        }
      } else if (
        this.dashboard &&
        this.$helpers.isLicenseEnabled('NEW_LAYOUT')
      ) {
        if (
          this.dashboard.linkName === 'portfolio' ||
          this.dashboard.linkName === 'maintenanceportfolio' ||
          this.dashboard.linkName === 'energyportfolio' ||
          this.dashboard.linkName === 'alarmportfolio' ||
          this.dashboard.linkName === 'commercial' ||
          this.dashboard.linkName === 'residential'
        ) {
          return 'energydata'
        } else if (this.dashboard.linkName === 'alarmportfolio') {
          return 'alarm'
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
        } else if (
          this.$route.path === '/app/em/newdashboard/energyportfolio' ||
          this.$route.path === '/app/em/dashboard/energyportfolio' ||
          this.$route.path === '/app/em/newdashboard/commercial' ||
          this.$route.path === '/app/em/newdashboard/residential'
        ) {
          return 'energydata'
        } else if (
          this.$route.path === '/app/fa/newdashboard/alarmportfolio' ||
          this.$route.path === '/app/fa/dashboard/alarmportfolio'
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
    dashboardTabId() {
      return this.$route.query.tab ? parseInt(this.$route.query.tab) : null
    },
    isPrinting() {
      return (
        this.$route.query.printing === 'true' ||
        this.$route.query.printing === true
      )
    },
    //display when printing on top right
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
  },
  destroyed() {
    tooltip.hideTooltip()
  },
  watch: {
    refresh() {
      this.loadDashboard()
    },
    dashboardLink: function(newVal) {
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
    dashboardTabId: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.loadDashboardTabWidgets()
        this.openGraphicsFolder()
      }
    },
  },
  methods: {
    registerGlobalComponents() {
      if (this.$refs['dashboardPopupView']) {
        Vue.prototype.$popupView = this.$refs['dashboardPopupView']
      }
    },
    calRowHeight() {
      let { rowHeight } = this
      let height = 5
      if (
        document.getElementsByClassName('layout-page')[0] &&
        document.getElementsByClassName('layout-page')[0].offsetWidth
      ) {
        height =
          document.getElementsByClassName('layout-page')[0].offsetWidth / 10000
      } else if (
        document.getElementsByClassName('dashboardmainlayout')[0] &&
        document.getElementsByClassName('dashboardmainlayout')[0].offsetWidth
      ) {
        let width = document.getElementsByClassName('dashboardmainlayout')[0]
          .offsetWidth
        if (width < 1200) {
          height = width / 350
        } else {
          height = width / 276.5
        }
        this.$set(this, 'rowHeight', height)
      }
    },
    calMargin() {
      let { margin } = this
      let mar = 10
      if (
        document.getElementsByClassName('layout-page') &&
        document.getElementsByClassName('layout-page').length
      ) {
        mar =
          document.getElementsByClassName('layout-page')[0].offsetWidth / 100
      } else if (
        document.getElementsByClassName('dashboardmainlayout') &&
        document.getElementsByClassName('dashboardmainlayout').length
      ) {
        let width = document.getElementsByClassName('dashboardmainlayout')[0]
          .offsetWidth
        mar = width / 138
        // this.$set(this, 'margin', mar)
      }
    },
    onResize(e) {
      this.calRowHeight()
      this.calMargin()
      window.dispatchEvent(new Event('resize'))
      // console.log('resize event', e.detail.width, e.detail.height)
    },
    closeAllComments: function() {
      this.showReportComments = false
    },
    geteditdashboardLink(mode) {
      if (mode) {
        if (isWebTabsEnabled()) {
          if (mode === 'new') {
            let { name } = findRouteForTab(pageTypes.DASHBOARD_CREATION) || {}

            if (name) {
              this.$router.push({ name, query: { create: 'new' } })
            }
          } else {
            let { name } = findRouteForTab(pageTypes.DASHBOARD_EDITOR) || {}

            if (name) {
              let params = {
                dashboardlink: this.dashboardLink,
              }

              if (
                this.dashboardLink === 'buildingdashboard' &&
                this.buildingId
              ) {
                params['buildingid'] = this.buildingId
              }
              this.$router.push({ name, params, query: { create: 'edit' } })
            }
          }
        } else {
          let linkname = this.dashboardLink
          if (linkname === 'buildingdashboard' && this.buildingId) {
            linkname += '/' + this.buildingId
          }
          let url = '/app/home/editdashboard/' + linkname
          if (mode === 'new') {
            this.$router.push(url + '?create=new')
          } else {
            this.$router.push(url + '?create=edit')
          }
        }
      }
    },
    openReportComments: function(commentMeta) {
      if (commentMeta.open) {
        this.showReportComments = true
        this.$set(this.reportCommentMeta, 'id', commentMeta.id)
      } else {
        this.showReportComments = false
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
    validateDashboardAccess() {
      this.isPermission = true
      if (isWebTabsEnabled()) {
        return this.tabHasPermission(`VIEW`)
      }
      return this.$hasPermission('dashboard:READ')
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
      if (!isEmpty(this.dashboardLink)) {
        if (!this.isPortalApp() && !this.validateDashboardAccess()) {
          this.$message("You don't have dashboard access")
          this.isPermission = false
          this.$emit('updatePermission', this.isPermission)
          return
        }
        let self = this
        self.loading = true
        this.clearDashboardFilters()
        this.showDashboardFilterBar = false
        self.dashboardLayout = null
        this.printTabLayout = []
        this.calRowHeight()
        this.calMargin()
        // self.rowHeight = (document.getElementsByClassName('layout-page')[0].offsetWidth / 24) - ((document.getElementsByClassName('layout-page')[0].offsetWidth / 24) / 3)

        let url = '/dashboard/' + this.dashboardLink
        if (!this.$helpers.isLicenseEnabled('NEW_LAYOUT')) {
          url += '?moduleName=' + this.getCurrentModule().module
          if (self.siteId) {
            url += '&siteId=' + self.siteId
          } else if (self.buildingId) {
            url += '&buildingId=' + self.buildingId
          }
        } else if (this.dashboardLink === 'buildingdashboard') {
          url += '?moduleName=' + 'energydata'
          if (self.siteId) {
            url += '&siteId=' + self.siteId
          } else if (self.buildingId) {
            url += '&buildingId=' + self.buildingId
          }
        } else {
          if (self.siteId) {
            url += '?siteId=' + self.siteId
          } else if (self.buildingId) {
            url += '?buildingId=' + self.buildingId
          }
        }
        // if (url.indexOf('?') > -1) {
        //   url += '&optimize=true'
        // }
        // else {
        //   url += '?optimize=true'
        // }
        url += self.$http.get(url).then(function(response) {
          self.dashboard = response.data.dashboardJson[0]
          if (self.currentDashboard) {
            self.fullScrnHeader = self.currentDashboard.dashboardName
          } else {
            self.fullScrnHeader =
              self.dashboard.linkName === 'buildingdashboard' ||
              self.dashboard.linkName === 'commercialbuildingdashboard' ||
              self.dashboard.linkName === 'residentialbuildingdashboard'
                ? self.$store.getters.getBuildingsPickList()[self.buildingId]
                : self.dashboard.label
          }
          if (self.dashboard.mobileEnabled) {
            self.$emit('mobiledashboard', self.dashboard.mobileEnabled)
          } else {
            self.$emit('mobiledashboard', false)
          }
          if (self.dashboard.dashboardTabPlacement === -1) {
            self.dashboardTabPlacement = 2
          } else {
            self.dashboardTabPlacement = self.dashboard.dashboardTabPlacement
          }
          if (
            response.data.dashboardJson[0].tabs &&
            response.data.dashboardJson[0].tabs.length > 0
          ) {
            let tabs = response.data.dashboardJson[0].tabs
            // if (!self.isPrinting) { // commenting this bcs dashboard tabs filters not applied while printing
            self.dashboardTabContexts = tabs
            if (self.dashboardTabId) {
              self.loadDashboardTabWidgets()
            } else if (!self.dashboardTabId) {
              self.$router.replace(
                self.$route.path + '?tab=' + self.dashboardTabContexts[0].id
              )
            }
            for (let key in self.dashboardTabContexts) {
              self.$set(self.dashboardTabContexts[key], 'expand', false)
            }
            self.openGraphicsFolder()
            // } else {
            //   self.prepareTabWidgetForPrinting(tabs)
            // }
          } else {
            self.dashboardTabContexts = null
            // self.$router.replace(self.$route.path)
            self.$emit('dashboardobj', self.dashboard)
            self.prepareDashboardLayout()
            self.$nextTick(() => {
              self.$emit('dashboardLoaded', self.dashboard)
            })
          }
          try {
            self.initializeDashboardFilters(self.dashboard.dashboardFilter)
          } catch (e) {
            console.error('error initializing dashboard filters', e)
            self.showDbFilterIcon = false
            //dont break dashboard loading if any issue with filter,just hide icon
          }
          self.loading = false
          self.setTitle(self.fullScrnHeader)
        })
      }
    },
    filterTheDashboard(children) {
      if (this.dashboardLink === 'myworkload') {
        return children.filter(rt => {
          let { widget } = rt
          if (
            widget.type === 'static' &&
            widget.header &&
            widget.header.title === 'My Open Workorders'
          ) {
            //remove empty block, ES lint throws error
          } else {
            return rt
          }
        })
      }
      return children
    },
    prepareDashboardLayout(dashboardChildren) {
      let self = this
      let children = dashboardChildren || this.dashboard.children
      children = this.filterTheDashboard(children)
      let layout = []
      let tx = 0
      if (
        this.$route.meta &&
        this.$route.meta.dashboardlayout &&
        this.$route.meta.dashboardlayout === 'mobile'
      ) {
        this.isMobileDashboard = true
      }
      for (let i = 0; i < children.length; i++) {
        const {
          widget: { type },
        } = children[i]
        if (type == 'section') {
          continue
        }
        if (tx + children[i].widget.layout.width > 96) {
          tx = 0
        }
        let x = children[i].widget.layout.x ? children[i].widget.layout.x : 0
        let y = children[i].widget.layout.y ? children[i].widget.layout.y : 0
        let mx = children[i].widget.mLayout.x ? children[i].widget.mLayout.x : 0
        let my = children[i].widget.mLayout.y ? children[i].widget.mLayout.y : 0
        layout.push({
          i: children[i].widget.id + '',
          x: self.isMobileDashboard ? mx : x,
          y: self.isMobileDashboard ? my : y,
          w: self.isMobileDashboard
            ? children[i].widget.mLayout.width
            : children[i].widget.layout.width,
          h: self.isMobileDashboard
            ? children[i].widget.mLayout.height
            : children[i].widget.layout.height,
          widget: children[i].widget,
          mx: mx,
          my: my,
          wx: x,
          wy: y,
          mw: children[i].widget.mLayout.width,
          mh: children[i].widget.mLayout.height,
          ww: children[i].widget.layout.width,
          wh: children[i].widget.layout.height,
        })
        tx += children[i].widget.layout.width
      }
      //self.dashboardLayout = layout
      let dashboardLayout = this.getPrintLayout(layout)
      if (!dashboardChildren) {
        this.dashboardLayout = dashboardLayout
      }
      return dashboardLayout
    },
    getPrintLayout(dashboardLayout) {
      if (this.isPrinting) {
        dashboardLayout.sort((a, b) => a.y - b.y || a.x - b.x)
        let lastY = 0
        let factor = 0
        let printLayout = []
        let listTotalHeight = 0
        let skipNextWidget = false
        this.listWidgets = []
        let totalMoved = 0

        let { gridColumn, printPageSize } = this

        let clonedLayout = this.$helpers.cloneObject(dashboardLayout)
        for (let i = 0; i < clonedLayout.length; i++) {
          let layout = clonedLayout[i]
          if (skipNextWidget) {
            skipNextWidget = false
            continue
          }
          let { x, y, h, w } = layout
          let initalY = y
          y -= listTotalHeight

          let isListWidget = layout.widget.type === 'view'
          y = y + totalMoved
          let ty = y + h
          factor = Math.ceil(y / printPageSize) * printPageSize
          if (h < printPageSize && ty > factor && y != factor) {
            let dummyHeight = factor - y + factor / printPageSize + 4
            if (!isListWidget) {
              totalMoved += dummyHeight
            }
            printLayout.push({
              i: 'dummy' + initalY,
              x: x,
              y: y,
              w: gridColumn - x,
              h: dummyHeight,
              widget: { type: 'dummy' },
              factor,
            })

            y = factor
          } else if (isListWidget) {
            if (w < 90) {
              let nextLayout = clonedLayout[i + 1]
              if (
                nextLayout &&
                nextLayout.widget.type === 'view' &&
                (nextLayout.y === initalY ||
                  nextLayout.y + nextLayout.h === initalY + h)
              ) {
                listTotalHeight = h > nextLayout.h ? h : nextLayout.h
                skipNextWidget = true
                this.listWidgets.push(nextLayout)
              } else {
                printLayout.push({
                  i: 'dummylist' + y,
                  x: x,
                  y: y,
                  w: gridColumn - x,
                  h: h,
                  widget: { type: 'dummy' },
                  factor,
                })
              }
            } else {
              listTotalHeight += h
            }
          }
          layout.y = y
          layout.factor = factor
          if (isListWidget) {
            this.listWidgets.push(layout)
          } else {
            if (y + h > lastY) {
              lastY = y + h
            }
            printLayout.push(layout)
          }
        }
        return printLayout
      }
      return dashboardLayout
    },
    prepareTabWidgetForPrinting(tabs) {
      let promises = []
      this.dashboardTabLoading = true
      tabs.forEach(tab => {
        let promise = this.$http
          .get('/dashboard/getTabWidgets?dashboardTabId=' + tab.id)
          .then(response => {
            if (response.data.dashboardTabContext) {
              let dashboard = {
                tabId: tab.id,
                sequence: tab.sequence,
                tabName: tab.name,
                lastY: 0,
                children: response.data.dashboardTabContext.clientWidgetJson,
                layout: this.prepareDashboardLayout(
                  response.data.dashboardTabContext.clientWidgetJson
                ),
              }
              /* dashboard.children.forEach(child => {
                let childY = child.widget.layout.y + child.widget.layout.height
                if (childY > dashboard.lastY) {
                  dashboard.lastY = childY
                }
              }) */
              this.printTabLayout.push(dashboard)
            }
          })
        promises.push(promise)
      })
      Promise.all(promises).finally(() => {
        this.printTabLayout.sort((a, b) => a.sequence - b.sequence)
        /* let children = [...dashboards[0].children]
        let lastY = 0
        this.prepareDashboardLayout(children)
        for (let i = 1; i < dashboards.length; i++) {
          lastY += dashboards[i - 1].lastY + 5
          this.prepareDashboardLayout(dashboards[i].children)
          dashboards[i].children.forEach(child => {
            child.widget.layout.y += lastY
          })
          children.push(...dashboards[i].children)
        } */
        // this.$set(this.dashboard, 'children', this.printTabLayout[0].children)
        //this.prepareDashboardLayout()
        this.dashboardTabContexts = null
        this.dashboardTabLoading = false
        this.dashboardLayout = this.printTabLayout[0].layout
      })
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
        console.log('****** index', index)
      }
      console.log(' ********* delete chart called', data, this.removeChartList)
    },
    loadDashboardTabWidgets() {
      if (!this.dashboardTabId) {
        return
      }
      this.dashboardTabLoading = true
      this.clearDashboardFilters()
      this.showDashboardFilterBar = false
      let dashboardTabFilter = null
      this.$http
        .get('/dashboard/getTabWidgets?dashboardTabId=' + this.dashboardTabId)
        .then(response => {
          if (response.data.dashboardTabContext) {
            let dashboard = response.data.dashboardTabContext.clientWidgetJson
            dashboardTabFilter =
              response.data.dashboardTabContext.dashboardFilter
            this.$set(this.dashboard, 'children', dashboard)
            this.prepareDashboardLayout()
            this.$emit('dashboardobj', this.dashboard)
            this.$nextTick(() => {
              this.$emit('dashboardLoaded', self.dashboard)
            })
          } else {
            this.$route.replace(this.$route.path)
          }
          this.initializeDashboardFilters(dashboardTabFilter)

          this.dashboardTabLoading = false
        })
        .catch(error => {
          this.dashboardTabLoading = false
        })
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
    /* Dashboard filter */
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

    handleTimelineFilterChange(value) {
      this.persistDbTimelineFilter(value)
      this.dbTimelineFilter = value
    },
    handleDbUserFilterChange(value) {
      this.persistDbUserFilters(value.filterModel)
      this.dbUserFilters = value.filterJson
      this.setCustomScriptFilters(value.filterModel)
    },
    setTabName() {
      let tabId = this.$getProperty(this.$route, `query.tab`)
      for (let tabs in this.dashboardTabContexts) {
        if (this.dashboardTabContexts[tabs].id == tabId)
          this.tabName = this.dashboardTabContexts[tabs].name
      }
      return this.tabName
    },

    /* Dashboard filter */
  },
}
</script>

<style>
.dashboard-container {
  /* height: 100%; */
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
/* .dashboard-container .fc-list-view-table tbody tr.tablerow td:nth-child(2){
  text-align: center;
} */
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
/* body:-webkit-full-screen .layout-header,
body:-webkit-full-screen .fc-layout-aside,
body:-webkit-full-screen .subheader-section,
body:-webkit-full-screen .el-dialog__wrapper {
  display: none;
}  removed due to widget dialog issue*/
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
.dashboard-tab-sidebar {
  background: white;
  height: 100vh;
  overflow-y: hidden;
  border-right: 1px solid #6666662f;
  position: relative;
  border-top: 1px solid #6666662f;
}
.dfolder-name {
  padding: 14px 10px 14px 20px;
  cursor: pointer;
  font-size: 13px;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}

.dashboard-folder-container {
  position: relative;
}
.dfolder-name.active {
  background: #f0f7f8;
  font-weight: 500;
}

.dfolder-name i {
  margin-right: 6px;
  font-size: 16px;
  color: #66666696;
}

.dfolder-children div {
  padding: 12px 10px 12px 20px;
  display: flex;
  justify-content: space-between;
}

.dfolder-children div:not(.rempty) {
  cursor: pointer;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  letter-spacing: 0.4px;
  text-align: left;
  color: #333333;
}

.dfolder-name.active,
.dfolder-children div.active {
  background: #f0f7f8;
}
.dfolder-name:hover,
.dfolder-children div:not(.rempty):hover {
  background: #f3f6f9;
}
.dashboard-tab-sidebar-scroll {
  height: 100%;
  overflow-y: scroll;
  padding-bottom: 200px;
}
.dashboard-header-pdf {
  display: none;
}
</style>
<style>
@media print {
  .fc-v1-portal-sidebar {
    display: none !important;
  }
  .fc-v1-tenant-overview-header {
    display: none !important;
  }
  .dashboard-tab-sidebar {
    display: none !important;
  }
  .dashboard-top-bar {
    display: none !important;
  }
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
  .vue-grid-item,
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
  .vue-grid-layout {
    position: relative;
    float: none;
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
  /* .dashboard-pdf-grid{
    page-break-after: auto;
  }

  .vue-grid-item{
    display: block !important;
    page-break-inside: avoid;
  } */
}
/* .dashboard-widget-view .single-dashboard .dbHeadingTxt {
  display: none !important;
} */
.dashboard-widget-view .single-dashboard .fc-widget-header {
  display: none !important;
}
.dashboard-widget-view .scrollable.dashboardmainlayout.single-dashboard {
  padding: 0 !important;
}
.dashboard-widget-view .single-dashboard .vue-grid-layout.dashboard-pdf-grid {
  height: 100vh !important;
  overflow: hidden;
  width: 100vw !important;
}
.dashboard-widget-view
  .single-dashboard
  .vue-grid-item.dashboard-f-widget.cssTransforms {
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
