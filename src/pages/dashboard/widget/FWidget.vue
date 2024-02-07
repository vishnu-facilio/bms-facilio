<template>
  <div
    v-observe-visibility="
      isLazyDashboard ? handleViewportVisibilityChange : false
    "
    :id="widget.id ? widget.id : -1"
    class="fc-widget"
    style="width: 100%; height: 100%"
    :style="
      widget.type &&
      widget.type === 'static' &&
      (widget.dataOptions.staticKey === 'profilecard' ||
        widget.dataOptions.staticKey === 'weathercard' ||
        widget.dataOptions.staticKey === 'energycard' ||
        widget.dataOptions.staticKey === 'energycostaltayer' ||
        widget.dataOptions.staticKey === 'weathercardaltayer' ||
        widget.dataOptions.staticKey === 'cumulativesavings' ||
        widget.dataOptions.staticKey === 'weathermini' ||
        widget.dataOptions.staticKey === 'profilemini' ||
        widget.dataOptions.staticKey === 'carbonmini' ||
        widget.dataOptions.staticKey === 'energycostmini' ||
        widget.dataOptions.staticKey === 'readingcard' ||
        widget.dataOptions.staticKey === 'readingWithGraphCard' ||
        widget.dataOptions.staticKey === 'readingGaugeCard' ||
        widget.dataOptions.staticKey === 'readingComboCard' ||
        widget.dataOptions.staticKey === 'fcucard' ||
        widget.dataOptions.staticKey.includes('emrilllevel') ||
        widget.dataOptions.staticKey === 'emrillFcu' ||
        widget.dataOptions.staticKey === 'emrillFcuList' ||
        widget.dataOptions.staticKey === 'resourceAlarmBar' ||
        widget.dataOptions.staticKey === 'kpiCard')
        ? 'border:0px'
        : 'border:solid 1px #eae8e8'
    "
  >
    <div class="row fc-widget-header" v-if="viewHeader(widget.header)">
      <div class="col-8" style="overflow: hidden">
        <el-input
          :autofocus="true"
          class="create-dashboard-input-title f18 fc-widget-label ellipsis"
          v-model="widget.header.title"
          placeholder=""
          @blur="blurevent()"
          @change="titlechage()"
          v-if="mode && changeheader"
        ></el-input>
        <div
          class="f18 fc-widget-label ellipsis"
          @dblclick="editheader('edit')"
          v-else
        >
          {{ widget.header.title }}
        </div>
        <div class="fc-widget-sublabel" v-if="widget.header.subtitle">
          {{ setSubTitle || emptyTitle }}
        </div>
        <div class="fc-widget-sublabel" v-else>{{ emptyTitle }}</div>
      </div>
      <div class="col-4" v-if="widget.type != 'static' && !isReadOnly">
        <div
          v-if="
            widgetType === 'f-newchart-widget' ||
              widgetType === 'f-modular-report-widget' ||
              widgetType === 'f-newchart-widget-optimize' ||
              widgetType === 'PivotTableWrapper'
          "
        >
          <el-popover
            :key="widget.id ? widget.id : -1"
            :popper-class="'fc-widget-more-options'"
            placement="bottom"
            width="200"
            trigger="click"
            v-model="newWidgetOptionsPopoverToggle"
          >
            <div
              class="fc-widget-view-options"
              v-if="!mode && !showFilterConfig"
            >
              <template v-if="!isOperationsApp">
                <div
                  v-if="!isPortalApp"
                  class="pointer fc-newChart-options-label"
                  @click="openReport(widget.dataOptions.reportId, widget)"
                >
                  {{ $t('home.dashboard.go_to_report') }}
                </div>
                <div
                  class="pointer fc-newChart-options-label"
                  @click="
                    openInAnalytics(widget.dataOptions.newReportId, widget)
                  "
                  v-if="
                    widgetType === 'f-modular-report-widget' && !isPortalApp
                  "
                >
                  {{ $t('home.dashboard.edit_report') }}
                </div>
                <div
                  class="pointer fc-newChart-options-label"
                  @click="openInPivotBuilder(widget.dataOptions.newReportId)"
                  v-if="widgetType === 'PivotTableWrapper' && !isPortalApp"
                >
                  {{ $t('home.dashboard.edit_report') }}
                </div>
                <div
                  class="pointer fc-newChart-options-label"
                  @click="
                    openInAnalytics(widget.dataOptions.newReportId, widget)
                  "
                  v-if="
                    (widgetType === 'f-newchart-widget' ||
                      widgetType === 'f-newchart-widget-optimize') &&
                      !isPortalApp
                  "
                >
                  {{ $t('home.dashboard.explore_in_analytics') }}
                </div>
                <!-- <div class="pointer fc-newChart-options-label" @click="openReportComments(widget.dataOptions.newReportId)">Show Comments</div> -->
                <div class="en-divider"></div>
              </template>
              <div
                class="pointer fc-newChart-options-label"
                @click="
                  exportAsFile(
                    1,
                    widget.dataOptions.newReportId,
                    widget.dataOptions.reportTemplate,
                    widget
                  )
                "
                v-if="
                  widgetType === 'f-newchart-widget' ||
                    widgetType === 'f-modular-report-widget' ||
                    widgetType === 'f-newchart-widget-optimize' ||
                    widgetType === 'PivotTableWrapper'
                "
              >
                {{ $t('common.wo_report.export_csv') }}
              </div>
              <div
                class="pointer fc-newChart-options-label"
                @click="
                  exportAsFile(
                    2,
                    widget.dataOptions.newReportId,
                    widget.dataOptions.reportTemplate,
                    widget
                  )
                "
                v-if="
                  widgetType === 'f-newchart-widget' ||
                    widgetType === 'f-modular-report-widget' ||
                    widgetType === 'f-newchart-widget-optimize' ||
                    widgetType === 'PivotTableWrapper'
                "
              >
                {{ $t('common.wo_report.export_xcl') }}
              </div>
              <div
                class="pointer fc-newChart-options-label"
                @click="
                  exportAsFile(
                    3,
                    widget.dataOptions.newReportId,
                    widget.dataOptions.reportTemplate,
                    widget
                  )
                "
                v-if="
                  widgetType === 'f-newchart-widget' ||
                    widgetType === 'f-modular-report-widget' ||
                    widgetType === 'f-newchart-widget-optimize'
                "
              >
                {{ $t('common.wo_report.export_pdf') }}
              </div>
              <div
                class="pointer fc-newChart-options-label"
                @click="
                  exportAsFile(
                    4,
                    widget.dataOptions.newReportId,
                    widget.dataOptions.reportTemplate,
                    widget
                  )
                "
                v-if="
                  !isTabular &&
                    (widgetType === 'f-newchart-widget' ||
                      widgetType === 'f-modular-report-widget' ||
                      widgetType === 'f-newchart-widget-optimize')
                "
              >
                {{ $t('common.wo_report.export_image') }}
              </div>
              <template
                v-if="!isOperationsApp && widgetType !== 'PivotTableWrapper'"
              >
                <div class="en-divider"></div>
                <div
                  class="pointer fc-newChart-options-label"
                  @click="emailReportVisibility = true"
                >
                  {{ $t('home.dashboard.email_this_report') }}
                </div>
              </template>
            </div>
            <div
              class="fc-widget-edit-options"
              v-if="mode && !showFilterConfig"
            >
              <div
                class="pointer fc-newChart-options-label"
                v-if="
                  widget &&
                    widget.dataOptions &&
                    widget.dataOptions.newReport &&
                    widget.dataOptions.newReport.type === 4
                "
                @click="showTemplateReportOptions(widget)"
              >
                {{ $t('home.dashboard.report_options') }}
              </div>
              <div
                class="pointer fc-newChart-options-label"
                @click="deleteChart(widget, dashboard)"
              >
                {{ $t('home.dashboard.remove_widget') }}
              </div>

              <div
                class="pointer fc-newChart-options-label "
                @click="editHelpText"
              >
                {{ $t('common._common.help_text') }}
              </div>
            </div>
            <div
              class="fc-widget-filter-config-options"
              v-if="showFilterConfig"
            >
              <div
                class="pointer fc-newChart-options-label"
                @click="editFilterConfig"
              >
                {{ $t('dashboardfilters.configure') }}
              </div>
            </div>
            <i
              slot="reference"
              class="pointer pull-right  fc-widget-moreicon-vertical fa fa-ellipsis-v"
              :class="[
                { tabular: isTabular },
                { 'display-block-important': isInfoIconPopOverOpen },
              ]"
            ></i>
          </el-popover>

          <div
            @click="expandReport"
            v-if="!mode"
            class="pointer pull-right fc-widget-fullScreen"
            :class="[
              { tabular: isTabular },
              { 'display-block-important': isInfoIconPopOverOpen },
            ]"
          >
            <i
              class="fa fa-expand"
              :title="$t('common._common.expand')"
              data-position="top"
              v-tippy="{ arrow: true, animation: 'perspective' }"
            ></i>
          </div>

          <!-- Help Text display popover -->
          <el-popover
            v-if="showWidgetInfoIcon"
            :popper-class="'widget-info-popper'"
            placement="bottom"
            trigger="click"
            class="widget-info-container"
            v-model="isInfoIconPopOverOpen"
            :class="{ 'show-info-icon': isInfoIconPopOverOpen }"
          >
            <div
              class="widget-help-text  space-preline break-normal pT5 pB5 pL10 pR10"
            >
              {{ this.widget.helpText }}
            </div>
            <i
              slot="reference"
              class="pointer el-icon-info pull-right mR10 mT2"
            ></i>
          </el-popover>
          <!-- Helptext ends -->
          <portal-target
            :name="'widget-datepicker' + widget.id"
            v-show="!hideTimelineFilterInsideWidget"
          ></portal-target>
        </div>
        <div v-else-if="widgetType === 'f-graphics-widget'">
          <el-popover
            :popper-class="'fc-widget-more-options'"
            placement="bottom"
            width="200"
            trigger="click"
            v-model="newWidgetOptionsPopoverToggle"
          >
            <div class="fc-widget-edit-options" v-if="mode">
              <div
                class="pointer fc-newChart-options-label"
                v-if="widget && widget.type === 'graphics'"
                @click="openGraphicsOptions(widget)"
              >
                {{ $t('panel.card.graphics_options') }}
              </div>
              <div
                class="pointer fc-newChart-options-label"
                @click="deleteChart(widget, dashboard)"
              >
                {{ $t('home.dashboard.remove_widget') }}
              </div>
            </div>
            <div class="fc-widget-view-options" v-if="!mode">
              <div
                class="pointer fc-newChart-options-label"
                @click="openGraphics(widget.dataOptions.graphicsId, widget)"
              >
                {{ $t('panel.card.graphics') }}
              </div>
              <div
                class="pointer fc-newChart-options-label"
                @click="
                  openGraphicsInAnalytics(widget.dataOptions.graphicsId, widget)
                "
              >
                {{ $t('panel.card.exp_analytics') }}
              </div>
              <div class="en-divider"></div>
              <div
                class="pointer fc-newChart-options-label"
                @click="exportGraphicsAsImage(widget.dataOptions.graphicsId)"
              >
                {{ $t('panel.card.exp_img') }}
              </div>
            </div>
            <i
              slot="reference"
              class="pointer pull-right fc-widget-moreicon-vertical fa fa-ellipsis-v"
            ></i>
          </el-popover>
        </div>
        <!-- <div v-else-if="widgetType === 'PivotTableWrapper'">
          <portal-target
            v-if="widgetType === 'PivotTableWrapper'"
            :name="'widget-datepicker' + widget.id"
          ></portal-target>
          <div
            class="pull-right externalLink"
            v-tippy
            :title="$t('home.dashboard.open_full_view')"
          >
            <img
              src="~statics/report/arrow-report.svg"
              class="chart-icon-report"
              @click="openPivotReport(widget.dataOptions.newReportId)"
            />
          </div>
        </div> -->
        <div v-else-if="enableRedirect(widgetType)">
          <div
            class="pull-right externalLink"
            style=""
            v-tippy
            :title="$t('home.dashboard.open_full_view')"
          >
            <img
              src="~statics/report/arrow-report.svg"
              class="chart-icon-report"
              @click="openReport(widget.dataOptions.reportId, widget)"
            />
          </div>
          <!-- <div class="pull-right chart-delete-icon" style=""  v-tippy title="Delete widget">
        <i class="el-icon-delete f18" @click="deleteChart(widget, dashboard)"></i>
        </div> -->
          <div
            class="pull-right chart-icon-hide"
            style=""
            v-tippy
            title="Hide widget"
          ></div>
        </div>
        <div v-else-if="widgetType == 'f-view-widget' && showFilterConfig">
          <!-- DO NOT BIND V MODEL HERE to this POP-UP TAG DOESNT WORK FOR SOME REASON -->
          <el-popover
            :popper-class="'fc-widget-more-options'"
            placement="bottom"
            width="200"
            trigger="click"
          >
            <div class="fc-widget-filter-config-options">
              <div
                class="pointer fc-newChart-options-label"
                @click="editFilterConfig"
              >
                {{ $t('dashboardfilters.configure') }}
              </div>
            </div>

            <i
              slot="reference"
              class="pointer pull-right fc-widget-moreicon-vertical fa fa-ellipsis-v"
              @click="newWidgetOptionsPopoverToggle = true"
            ></i>
          </el-popover>
        </div>
      </div>
    </div>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
    <!-- <i class="el-icon-delete f18" v-else @click="addwidgetHedaer()"></i> -->
    <div
      class="en-divider"
      :style="
        widget.type &&
        widget.type === 'static' &&
        (widget.dataOptions.staticKey === 'profilecard' ||
          widget.dataOptions.staticKey === 'weathercard' ||
          widget.dataOptions.staticKey === 'energycard' ||
          widget.dataOptions.staticKey === 'energycostaltayer' ||
          widget.dataOptions.staticKey === 'weathercardaltayer' ||
          widget.dataOptions.staticKey === 'cumulativesavings' ||
          widget.dataOptions.staticKey === 'weathermini' ||
          widget.dataOptions.staticKey === 'profilemini' ||
          widget.dataOptions.staticKey === 'carbonmini' ||
          widget.dataOptions.staticKey === 'energycostmini' ||
          widget.dataOptions.staticKey === 'readingcard' ||
          widget.dataOptions.staticKey === 'fahuStatusCard' ||
          widget.dataOptions.staticKey === 'fahuStatusCard1' ||
          widget.dataOptions.staticKey === 'textcard' ||
          widget.dataOptions.staticKey === 'readingWithGraphCard' ||
          widget.dataOptions.staticKey === 'readingGaugeCard' ||
          widget.dataOptions.staticKey === 'readingComboCard' ||
          widget.dataOptions.staticKey === 'fcucard' ||
          widget.dataOptions.staticKey.includes('emrilllevel') ||
          widget.dataOptions.staticKey === 'emrillFcu' ||
          widget.dataOptions.staticKey === 'emrillFcuList' ||
          widget.dataOptions.staticKey === 'alarmbarwidget' ||
          widget.dataOptions.staticKey === 'resourceAlarmBar' ||
          widget.dataOptions.staticKey === 'kpiCard')
          ? 'display:none;'
          : 'display:block;'
      "
    ></div>
    <spinner :show="loading" size="80"></spinner>
    <div
      class="self-center"
      v-bind:class="{
        'fc-widget-body': widget.header.title !== null,
        'fc-widget-dragable-body': widget.header.title === null,
        'fc-new-tabular': isNewTabular,
        'fc-newchart-widget':
          widgetType === 'f-newchart-widget' ||
          widgetType === 'f-modular-report-widget' ||
          widgetType === 'f-newchart-widget-optimize',
      }"
    >
      <div v-if="!loading && failed">
        {{ $t('common._common.load_data_failed') }}
      </div>
      <div
        class="pull-right chart-delete-icon"
        v-if="
          widgetType !== 'f-newchart-widget' &&
            widgetType !== 'f-modular-report-widget' &&
            widgetType !== 'f-newchart-widget-optimize' &&
            widgetType !== 'f-graphics-widget' &&
            widgetType !== 'PivotTableWrapper'
        "
      >
        <el-popover
          :popper-class="'fc-widget-more-options'"
          placement="bottom"
          width="200"
          trigger="click"
          v-model="newWidgetOptionsPopoverToggle"
        >
          <div class="fc-widget-edit-options" v-if="mode">
            <div
              class="pointer fc-newChart-options-label"
              @click="editWidget(widget, dashboard)"
              v-if="editrule"
            >
              {{ $t('home.dashboard.edit_widget') }}
            </div>
            <div
              class="pointer fc-newChart-options-label"
              @click="deleteChart(widget, dashboard)"
            >
              {{ $t('home.dashboard.remove_widget') }}
            </div>
          </div>
          <i
            slot="reference"
            class="pointer pull-right el-icon-more fc-widget-moreicon-vertical fa fa-ellipsis-v"
          ></i>
        </el-popover>
      </div>
      <component
        v-if="false"
        ref="widgetChildComponent"
        :is="widgetType"
        :config="{
          widget: { dataOptions: { chartType: 'boolean' } },
          isAutoRefreshEnabled: isAutoRefreshEnabled,
          dashboardObj: dashboardObj,
          currentDashboard: currentDashboard,
          intervalUpdate: intervalUpdate,
          id: multiChartMapping[widget.id],
          width: widgetSize.width + 300,
          height: 180,
          actualWidth: actualWidgetSize.width,
          actualHeight: actualWidgetSize.height,
          isReportUpdateFromDashboard: true,
          custom: true,
          chartData1: widget,
          layoutConfig: { hideleagend: true, hideDatePicker: true },
        }"
        @onload="reportLoaded"
        @reportUtil="getReportutil"
        @reportLoaded="getNewReportObj"
        :mode1="mode"
        @timelineFilterChange="timelineFilterChange"
        :showTimePeriod="
          chartState ? [1, 4].includes(chartState.common.mode) : false
        "
        class="dualchart"
        v-show="
          !report ||
            (report &&
              report.options.type !== 'matrix' &&
              report.options.type !== 'tabular')
        "
        :getReportUtil="sendUtil"
      >
      </component>
      <component
        ref="widgetComponent"
        :is="widgetType"
        :dbFilterJson="dbFilterJson"
        :dbTimelineFilter="dbTimelineFilter"
        :dashbord="true"
        :isLazyDashboard="isLazyDashboard"
        :isVisibleInViewport="isVisibleInViewport"
        :reportId="$getProperty(widget, 'dataOptions.newReportId')"
        :config="{
          widget: widget,
          isAutoRefreshEnabled: isAutoRefreshEnabled,
          currentDashboard: currentDashboard,
          dashboardObj: dashboardObj,
          intervalUpdate: intervalUpdate,
          id: widget.dataOptions.reportId,
          width: widgetSize.width,
          height: widgetSize.height - 80,
          actualWidth: actualWidgetSize.width,
          actualHeight: actualWidgetSize.height,
          isReportUpdateFromDashboard: true,
          editwidget: editwidget,
          selectedWidgetId: selectedWidgetId,
          rowHeight: rowHeight,
        }"
        @timelineFilterChange="timelineFilterChange"
        @onload="reportLoaded"
        v-bind:class="{ 'fc-secondChart': multiChartMapping[widget.id] }"
        @dateFilterUpdated="dateFilterUpdated"
        @reportUtil="getReportutil"
        @reportLoaded="getNewReportObj"
        :isMobileDashboard="false"
        :mode1="mode"
        :showTimePeriod="
          chartState ? [1, 4].includes(chartState.common.mode) : false
        "
        v-show="
          !report ||
            (report &&
              report.options.type !== 'matrix' &&
              report.options.type !== 'tabular')
        "
        :getReportUtil="sendUtil"
      >
      </component>
      <f-tabular-report
        :reportObject="reportObject"
        v-if="report && report.options.type === 'tabular'"
      ></f-tabular-report>
      <el-dialog
        title="Configure widget"
        width="50%"
        :visible.sync="showTemplateConfigDialog"
        :append-to-body="true"
      >
        <div class="height200">
          <div>
            <el-radio-group v-model="templateConfig.showOrHide">
              <el-radio :label="1">{{ $t('panel.card.show_filter') }}</el-radio>
              <el-radio :label="2">{{ $t('panel.card.hide_filter') }}</el-radio>
            </el-radio-group>
          </div>
          <div v-if="templateConfig.showOrHide === 2" class="mT30">
            <div>
              {{ $t('panel.card.pick_def_asset') }}
            </div>
            <div class="mT10">
              <el-select
                class="fc-input-full-border2"
                filterable
                v-model="templateConfig.choosenResource"
              >
                <el-option
                  v-for="(resource, resourceIdx) in templateConfig.assets"
                  :key="resourceIdx"
                  :label="resource.name"
                  :value="resource.id"
                ></el-option>
              </el-select>
            </div>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closeDialog">{{
            $t('panel.card.cancel__')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="configureWidget"
            >{{ $t('panel.card.configure') }}</el-button
          >
        </div>
      </el-dialog>
      <el-dialog
        v-if="mode"
        title="Graphics Options"
        width="50%"
        :visible.sync="showGraphicsOptionsDialog"
        :append-to-body="true"
      >
        <div class="height250">
          <div>
            <el-checkbox v-model="graphicsOptions.showFilter">{{
              $t('panel.card.show_asset_filter')
            }}</el-checkbox>
          </div>
          <div class="pT10">
            <el-checkbox v-model="graphicsOptions.hideHeader">{{
              $t('panel.card.hide_header')
            }}</el-checkbox>
          </div>
          <div class="mT30">
            <div>
              {{ $t('panel.card.default_asset') }}
            </div>
            <div class="mT10">
              <el-select
                class="fc-input-full-border2"
                filterable
                v-model="graphicsOptions.defaultAsset"
              >
                <el-option
                  v-for="(resource, resourceIdx) in graphicsOptions.assets"
                  :no-data-text="
                    graphicsOptions.assetsLoading ? 'Loading...' : 'No Assets.'
                  "
                  :key="resourceIdx"
                  :label="resource.name"
                  :value="resource.id"
                ></el-option>
              </el-select>
            </div>
          </div>
          <div class="pT15">
            <el-input
              placeholder="eg: 75"
              v-model="graphicsOptions.zoomLevel"
              style="width: 300px"
              class="fc-input-full-border2"
            >
              <template slot="prepend">{{ $t('panel.card.zoom') }}</template>
              <template slot="append">%</template>
            </el-input>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closeGraphicsDialog">{{
            $t('panel.card.cancel__')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="saveGraphicsOptions(widget)"
            >{{ $t('panel.card.save') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <email-report
      v-if="emailReportVisibility"
      :visibility.sync="emailReportVisibility"
      :report="
        widget.dataOptions && widget.dataOptions.newReport
          ? widget.dataOptions.newReport
          : {}
      "
    ></email-report>
  </div>
</template>
<script>
import { getApp } from '@facilio/router'
import FReport from 'pages/report/components/FReport'
import FNewReport from 'pages/report/components/FNewReport'
import FNewReportOptimize from 'pages/report/components/FNewReportOptimize'
import FListWidget from './FListWidget'
import FCountWidget from './FCountWidget'
import FMapWidget from './FMapWidget'
import FStaticWidget from './FStaticWidget'
import FcardsWidget from './Fcards'
import FWebWidget from './FWebWidget'
import FGraphicsWidget from './FGraphicsWidget'
import FTabularReport from 'pages/report//components/FTabularReport'
import moment from 'moment'
import OutsideClick from '@/OutsideClick'
import Comments from '@/relatedlist/Comments2'
import ModuleNewReport from 'src/pages/report/ModuleNewReport'
import util from 'util/util'
import NewReportSummaryHelper from 'src/pages/report/mixins/NewReportSummaryHelper'
import { Message } from 'element-ui'
import PivotTableWrapper from 'src/pages/energy/pivot/PivotTableWrapper'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForTab,
  findRouteForReport,
  pageTypes,
} from '@facilio/router'
import EmailReport from 'pages/report/forms/DashboardEmailReport'
import { API } from '@facilio/api'
import OtherMixin from '@/mixins/OtherMixin'

const types = [
  'chart',
  'newchart',
  'view',
  'map',
  'count',
  'static',
  'web',
  'cards',
  'graphics',
  'connectedapps',
]

export default {
  mixins: [NewReportSummaryHelper, OtherMixin],
  props: {
    type: {
      type: String,
      required: true,
      validator: val => types.includes(val),
    },
    widget: {
      type: Object,
      required: true,
      default: () => ({}),
    },
    currentDashboard: {
      type: Object,
    },
    demoData: {
      type: Object,
    },
    grid: {
      type: Object,
    },
    rowHeight: {
      type: Number,
    },
    dashboard: {
      type: Number,
    },
    mode: {
      type: Boolean,
    },
    editwidget: {
      type: Boolean,
    },
    selectedWidgetId: {
      type: Number,
    },
    dashboardObj: {
      type: Object,
    },
    dbFilterJson: {
      type: Object,
    },
    dbTimelineFilter: {
      type: Object,
    },
    showFilterConfig: {
      type: Boolean,
      default: false,
    },
    isLazyDashboard: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    'f-chart-widget': FReport,
    'f-newchart-widget': FNewReport,
    'f-newchart-widget-optimize': FNewReportOptimize,
    'f-view-widget': FListWidget,
    'f-count-widget': FCountWidget,
    'f-map-widget': FMapWidget,
    'f-static-widget': FStaticWidget,
    'f-cards-widget': FcardsWidget,
    'f-web-widget': FWebWidget,
    'f-graphics-widget': FGraphicsWidget,
    'f-tabular-report': FTabularReport,
    outsideClick: OutsideClick,
    comments: Comments,
    'f-modular-report-widget': ModuleNewReport,
    EmailReport,
    // eslint-disable-next-line vue/no-unused-components
    PivotTableWrapper,
  },
  data() {
    return {
      isVisibleInViewport: false,
      emailReportVisibility: false,
      templateConfig: {
        showOrHide: 1,
        choosenResource: null,
        assets: [],
        hideHeader: false,
      },
      widgetDateObj: null,
      currentTemplate: null,
      exportDownloadUrl: null,
      url: null,
      loading: false,
      newWidgetOptionsPopoverToggle: false,
      failed: false,
      changeheader: false,
      newReportObj: null,
      sendUtil: null,
      data: null,
      report: null,
      reportObject: null,
      selectedPeriod: null,
      refreshInterval: null,
      setSubTitle: '',
      emptyTitle: 'Today',
      ChartType: null,
      pubSubWatcherKey: null,
      graphicsOptions: {
        showFilter: false,
        defaultAsset: null,
        assetsLoading: true,
        assets: [],
        hideHeader: false,
        zoomLevel: null,
      },
      showGraphicsOptionsDialog: false,
      showTemplateConfigDialog: false,
      isInfoIconPopOverOpen: false,
      multiChartMapping: {
        2387: 4498,
        2388: 4498,
        2400: 4498,
        2401: 4498,
        2408: 4498,
        2409: 4498,
        3032: 4498,
        3033: 4498,
        3034: 4498,
        3035: 4498,
        3036: 4498,
        3037: 4498,
        3038: 4498,
        3039: 4498,
        3040: 4498,
        3041: 4498,
        3042: 4498,
        3043: 4498,
        3044: 4498,
        3045: 4498,
        3046: 4498,
        3048: 4498,
        3098: 4498,
        3097: 4498,
        2391: 4499,
        2392: 4499,
        2402: 4499,
        2403: 4499,
        2410: 4499,
        2411: 4499,
        3049: 4499,
        3050: 4499,
        3051: 4499,
        3052: 4499,
        3053: 4499,
        3054: 4499,
        3055: 4499,
        3056: 4499,
        3057: 4499,
        3058: 4499,
        3059: 4499,
        3060: 4499,
        3061: 4499,
        3062: 4499,
        3063: 4499,
        3064: 4499,
        3099: 4499,
        3100: 4499,
        3395: 4499,
        3394: 4499,
        3396: 4499,
        2394: 4500,
        2395: 4500,
        2404: 4500,
        2405: 4500,
        2412: 4500,
        2413: 4500,
        3065: 4500,
        3066: 4500,
        3067: 4500,
        3068: 4500,
        3069: 4500,
        3070: 4500,
        3071: 4500,
        3072: 4500,
        3073: 4500,
        3074: 4500,
        3075: 4500,
        3076: 4500,
        3077: 4500,
        3078: 4500,
        3079: 4500,
        3080: 4500,
        2397: 4501,
        2398: 4501,
        2406: 4501,
        2407: 4501,
        2414: 4501,
        2415: 4501,
        3081: 4501,
        3082: 4501,
        3083: 4501,
        3084: 4501,
        3085: 4501,
        3086: 4501,
        3087: 4501,
        3088: 4501,
        3089: 4501,
        3090: 4501,
        3091: 4501,
        3092: 4501,
        3093: 4501,
        3094: 4501,
        3095: 4501,
        3096: 4501,
        3436: 4501,
        3437: 4501,
        3438: 4501,
        4089: 5745,
        4090: 5745,
        4091: 5745,
        4083: 5745,
        4085: 5745,
        4097: 5759,
        4098: 5759,
        4096: 5759,
        4094: 5759,
        4095: 5759,
        4106: 5773,
        4105: 5773,
        4104: 5773,
        4100: 5773,
        4101: 5773,
        4119: 5790,
        4120: 5790,
        4118: 5790,
        4116: 5790,
        4117: 5790,
        4135: 5806,
        4136: 5806,
        4137: 5806,
        4138: 5806,
        4139: 5806,
        2657: 4337,
        2659: 4337,
        2658: 4337,
        4206: 5830,
        4207: 5830,
        4208: 5830,
        4209: 5834,
        4210: 5834,
        4211: 5834,
        4212: 5838,
        4214: 5838,
        4213: 5838,
        4215: 5841,
        4217: 5841,
        4216: 5841,
        4218: 5845,
        4219: 5845,
        4220: 5845,
        4221: 5849,
        4223: 5849,
        4222: 5849,
        4224: 5853,
        4225: 5853,
        4226: 5853,
        4227: 5857,
        4228: 5857,
        4229: 5857,
        4230: 5862,
        4232: 5862,
        4231: 5862,
        4233: 5866,
        4234: 5866,
        4235: 5866,
        4236: 5870,
        4238: 5870,
        4237: 5870,
        5787: 6546,
        5786: 6546,
        5784: 6546,
        5785: 6546,
        5782: 6546,
        5783: 6546,
        5780: 6546,
        5781: 6546,
        5778: 6546,
        5779: 6546,
        5776: 6546,
        5777: 6546,
        5774: 6546,
        5775: 6546,
        5772: 6546,
        5773: 6546,
        5770: 6546,
        5771: 6546,
        5807: 6622,
        5806: 6622,
        5805: 6622,
        5804: 6622,
        5803: 6622,
        5802: 6622,
        5801: 6622,
        5800: 6622,
        5799: 6622,
        5798: 6622,
        5797: 6622,
        5796: 6622,
        5795: 6622,
        5794: 6622,
        5793: 6622,
        5792: 6622,
        5791: 6622,
        5790: 6622,
        5789: 6622,
        5827: 6623,
        5826: 6623,
        5825: 6623,
        5824: 6623,
        5823: 6623,
        5822: 6623,
        5821: 6623,
        5820: 6623,
        5819: 6623,
        5818: 6623,
        5817: 6623,
        5816: 6623,
        5815: 6623,
        5814: 6623,
        5813: 6623,
        5812: 6623,
        5811: 6623,
        5810: 6623,
        5809: 6623,
        5847: 6624,
        5846: 6624,
        5845: 6624,
        5844: 6624,
        5843: 6624,
        5842: 6624,
        5841: 6624,
        5840: 6624,
        5839: 6624,
        5838: 6624,
        5837: 6624,
        5836: 6624,
        5835: 6624,
        5834: 6624,
        5833: 6624,
        5832: 6624,
        5831: 6624,
        5830: 6624,
        5829: 6624,
      },
    }
  },
  mounted() {
    if (
      this.type === 'graphics' &&
      (this.widget.header.title === '' || this.widget.header.title === null)
    ) {
      this.changeheader = true
    }
    this.initRefreshInterval()
    this.getGraphicsOptions(this.widget)
  },
  destroyed() {
    this.cleanupRefreshInterval()
  },
  computed: {
    isPortalApp() {
      let {
        appCategory: { PORTALS },
      } = this.$constants
      if (getApp() && getApp().appCategory === PORTALS) {
        return true
      }
      return false
    },
    showWidgetInfoIcon() {
      return this.widget?.widgetSettings?.showHelpText && this.widget.helpText
    },
    hideTimelineFilterInsideWidget() {
      if (this.dashboardObj?.dashboardFilter?.hideFilterInsideWidgets) {
        return this.dashboardObj.dashboardFilter.hideFilterInsideWidgets
      }
      return false
    },
    isOperationsApp() {
      let appNameFromUrl = window.location.pathname.slice(1).split('/')[0]
      if (appNameFromUrl === 'operations') {
        return true
      }
      return false
    },
    chartState() {
      if (
        this.widget &&
        this.widget.dataOptions &&
        this.widget.dataOptions.newReport
      ) {
        return this.widget.dataOptions.newReport &&
          this.widget.dataOptions.newReport.chartState
          ? typeof this.widget.dataOptions.newReport.chartState === 'string'
            ? JSON.parse(this.widget.dataOptions.newReport.chartState)
            : this.widget.dataOptions.newReport.chartState
          : null
      } else {
        return null
      }
    },
    isTabular() {
      if (this.widgetType === 'PivotTableWrapper') {
        return true
      }
      if (this.chartState && this.chartState !== null) {
        if (
          this.chartState.settings &&
          this.chartState.settings.chart === false
        ) {
          return true
        }
        return false
      }
      return false
    },
    isReadOnly() {
      if (this.isAltayerNonPrivilagedUser) {
        return true
      } else {
        if (this.currentDashboard && this.currentDashboard.readOnly) {
          return true
        }
      }
      return false
    },
    isAutoRefreshEnabled() {
      return this.$hasPermission('setup:GENERAL') && this.$org.id === 116
    },
    editrule() {
      let data = this.widget.dataOptions
      if (this.$account.org.id === 146) {
        return false
      }
      if (
        data.staticKey === 'readingcard' ||
        data.staticKey === 'readingGaugeCard' ||
        data.staticKey === 'readingWithGraphCard' ||
        data.staticKey === 'textcard' ||
        data.staticKey === 'imagecard' ||
        data.staticKey === 'readingComboCard' ||
        data.staticKey === 'kpiCard'
      ) {
        return true
      } else {
        return false
      }
    },
    widgetSize() {
      let w = null
      let h = null
      if (this.grid) {
        w = this.grid.w
        h = this.grid.h
      } else {
        w = this.widget.layout.width
        h = this.widget.layout.height
      }
      if (this.multiChartMapping[this.widget.id]) {
        return {
          width: w * (this.rowHeight ? this.rowHeight : 100),
          height:
            h * (this.rowHeight ? this.rowHeight * 0.9 : 100) -
            (this.report && this.report.options.showWidgetLegends ? 65 : 0),
        }
      } else {
        if (this.newReportObj) {
          return {
            width: w * (this.rowHeight ? this.rowHeight : 100),
            height:
              h * (this.rowHeight ? this.rowHeight : 100) -
              (this.newReportObj &&
              this.newReportObj.options &&
              this.newReportObj.options.widgetLegend &&
              this.newReportObj.options &&
              this.newReportObj.options.widgetLegend.show
                ? 0
                : -65),
          }
        } else {
          return {
            width: this.$el
              ? this.$el.offsetWidth
              : w * (this.rowHeight ? this.rowHeight : 100),
            height: this.$el
              ? this.$el.offsetHeight -
                (this.report && this.report.options.showWidgetLegends
                  ? 100
                  : 65)
              : h * (this.rowHeight ? this.rowHeight : 100) -
                (this.report && this.report.options.showWidgetLegends ? 0 : 0),
          }
        }
      }
    },
    actualWidgetSize() {
      let w = null
      let h = null
      if (this.grid) {
        w = this.grid.w
        h = this.grid.h
      } else {
        w = this.widget.layout.width
        h = this.widget.layout.height
      }

      return {
        width: w * (this.rowHeight ? this.rowHeight : 100),
        height: h * (this.rowHeight ? this.rowHeight : 100),
      }
    },
    widgetType() {
      if (this.widget.dataOptions.newReportId) {
        if (this.widget.dataOptions.reportType == 2) {
          return 'f-modular-report-widget'
        } else if (this.widget.dataOptions.reportType == 5) {
          return 'PivotTableWrapper'
        } else {
          if (this.$route.query.newchart) {
            return 'f-newchart-widget-optimize'
          } else {
            return 'f-newchart-widget'
          }
        }
      }
      return 'f-' + this.widget.type + '-widget'
    },
    classObject() {
      if (this.widget.layout.width) {
        let className = 'col-' + this.widget.layout.width * 3
        return className
      } else {
        return 'col-1'
      }
    },
    isNewTabular() {
      return (
        this.widget.dataOptions.newReportId &&
        this.newReportObj &&
        this.newReportObj.options &&
        this.newReportObj.options.settings &&
        this.newReportObj.options.settings.chart === false
      )
    },
  },
  methods: {
    getRoute(id, widgetss) {
      let moduleName = null
      let currentModule = this.getCurrentModule()
      if (widgetss?.dataOptions?.newReport?.module?.name) {
        moduleName = widgetss.dataOptions.newReport.module.name
      } else {
        moduleName = currentModule.module
      }
      if (isWebTabsEnabled() && widgetss.dataOptions.newReportId) {
        let routeObj
        if (!moduleName) {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_VIEW
          )
        } else if (moduleName === 'energydata') {
          routeObj = findRouteForReport(
            'analytics_reports',
            pageTypes.REPORT_VIEW,
            { moduleName }
          )
        } else {
          routeObj = findRouteForReport(
            'module_reports',
            pageTypes.REPORT_VIEW,
            { moduleName }
          )
        }
        let { name } = routeObj || {}
        let url =
          this.$router.resolve({ name }).href +
          '/' +
          widgetss.dataOptions.newReportId
        this.url = url
      }
    },
    handleViewportVisibilityChange(isVisible, entry) {
      //one set state from false to true, DONT reset it to false when widget goes out of view again , no need to trigger refresh again. see watcher for isVisibleInViewport inside report comp

      if (isVisible) {
        this.isVisibleInViewport = isVisible
      }
    },
    editFilterConfig() {
      this.newWidgetOptionsPopoverToggle = false
      let { widget } = this
      this.$emit('editWidgetFilterConfig', widget)
    },
    enableRedirect(widgetType) {
      if (widgetType === 'f-graphics-widget') {
        return false
      } else if (widgetType === 'f-view-widget') {
        return false
      } else {
        return true
      }
    },
    timelineFilterChange(dateObj) {
      this.widgetDateObj = dateObj
    },
    closeGraphicsDialog() {
      this.showGraphicsOptionsDialog = false
    },
    saveGraphicsOptions(widget) {
      if (widget) {
        widget.dataOptions.graphicsOptions = JSON.stringify({
          showFilter: this.graphicsOptions.showFilter,
          defaultAsset: this.graphicsOptions.defaultAsset,
          hideHeader: this.graphicsOptions.hideHeader,
          zoomLevel: this.graphicsOptions.zoomLevel,
        })
      }
      this.showGraphicsOptionsDialog = false
    },
    openGraphicsOptions(widget) {
      if (
        widget &&
        widget.dataOptions &&
        (widget.dataOptions.graphicsId || widget.graphicsId)
      ) {
        if (widget.dataOptions.graphicsOptions) {
          let goptions = JSON.parse(widget.dataOptions.graphicsOptions)
          this.graphicsOptions = {
            showFilter: goptions.showFilter ? true : false,
            defaultAsset: goptions.defaultAsset ? goptions.defaultAsset : null,
            assets: [],
            hideHeader: goptions.hideHeader,
            zoomLevel: goptions.zoomLevel,
          }
        } else {
          this.graphicsOptions = {
            showFilter: false,
            defaultAsset: null,
            assets: [],
            hideHeader: false,
            zoomLevel: null,
          }
        }
        this.loadGraphicsAssets(
          widget.dataOptions.graphicsId || widget.graphicsId
        )
        this.showGraphicsOptionsDialog = true
      }
    },
    getGraphicsOptions(widget) {
      if (
        widget &&
        widget.dataOptions &&
        (widget.dataOptions.graphicsId || widget.graphicsId)
      ) {
        if (widget.dataOptions.graphicsOptions) {
          let goptions = JSON.parse(widget.dataOptions.graphicsOptions)
          this.graphicsOptions = {
            showFilter: goptions.showFilter ? true : false,
            defaultAsset: goptions.defaultAsset ? goptions.defaultAsset : null,
            assets: [],
            hideHeader: goptions.hideHeader,
            zoomLevel: goptions.zoomLevel,
          }
        } else {
          this.graphicsOptions = {
            showFilter: false,
            defaultAsset: null,
            assets: [],
            hideHeader: false,
            zoomLevel: null,
          }
        }
      }
    },
    loadGraphicsAssets(graphicsId) {
      this.graphicsOptions.assetsLoading = true
      this.$http
        .get('/v2/graphics/getById?recordId=' + graphicsId)
        .then(response => {
          if (response.data.result.graphics) {
            let graphicsObj = response.data.result.graphics

            let param
            if (graphicsObj.applyTo) {
              let applyTo = JSON.parse(graphicsObj.applyTo)
              if (
                applyTo.applyToType === 2 &&
                applyTo.applyToAssetIds.length > 0
              ) {
                param = {
                  filters: {
                    id: {
                      operatorId: 36,
                      value: applyTo.applyToAssetIds.map(String),
                    },
                  },
                }
              } else if (applyTo.applyToType === 3 && applyTo.criteria) {
                param = { filterCriterias: applyTo.criteria }
              } else {
                param = { categoryId: graphicsObj.assetCategoryId }
              }
            } else {
              param = { categoryId: graphicsObj.assetCategoryId }
            }
            util.loadAsset(param).then(response => {
              this.graphicsOptions.assets = response.assets
              if (!this.graphicsOptions.defaultAsset) {
                this.graphicsOptions.defaultAsset = parseInt(
                  graphicsObj.assetId
                )
              }
              this.graphicsOptions.assetsLoading = false
            })
          }
        })
    },
    closeDialog() {
      this.showTemplateConfigDialog = false
    },
    configureWidget() {
      let template = JSON.parse(JSON.stringify(this.currentTemplate))
      if (this.templateConfig.showOrHide === 1) {
        template.isVisibleInDashBoard = true
      } else {
        template.isVisibleInDashBoard = false
        template.parentId = this.templateConfig.choosenResource
        template.defaultValue = this.templateConfig.choosenResource
      }

      template.chooseValues = []
      this.currentTemplate = template
      this.widget.dataOptions.reportTemplate = template
      this.showTemplateConfigDialog = false
      if (this.multiChartMapping[this.widget.id]) {
        this.$refs.widgetChildComponent.init()
      } else {
        this.$refs.widgetComponent.init()
      }
    },
    showTemplateReportOptions(widget) {
      if (
        widget &&
        widget.dataOptions &&
        widget.dataOptions.newReport &&
        widget.dataOptions.newReport.type === 4
      ) {
        this.currentTemplate = this.widget.dataOptions.newReport.reportTemplate
          ? this.widget.dataOptions.newReport.reportTemplate
          : this.widget.dataOptions.reportTemplate
        if (this.templateConfig.assets.length === 0) {
          let template = JSON.parse(JSON.stringify(this.currentTemplate))
          this.loadReportTemplateValues(template).then(() => {
            this.templateConfig.assets = template.chooseValues
            this.showTemplateConfigDialog = true
          })
        } else {
          this.showTemplateConfigDialog = true
        }
      } else {
        this.showTemplateConfigDialog = false
      }
    },
    openReportComments(id) {
      let temp = {}
      temp['id'] = id
      temp['open'] = true
      this.newWidgetOptionsPopoverToggle = false
      this.$emit('showComments', temp)
    },
    expandReport() {
      let params = {}
      if (this.widgetType == 'PivotTableWrapper') {
        params['type'] = 'pivot'
      } else {
        params['type'] = 'report'
      }
      params['dbFilterJson'] = this.dbFilterJson
      let dateObj = Object.freeze(this.dbTimelineFilter)
      if (this.widgetDateObj) {
        dateObj = {
          startTime: this.widgetDateObj.value[0],
          endTime: this.widgetDateObj.value[1],
          operatorId: this.widgetDateObj.operatorId,
          dateLabel: this.widgetDateObj.label,
          dateValueString: `${this.widgetDateObj.value[0]},${this.widgetDateObj.value[1]}`,
          dateField: this.dbTimelineFilter.dateField,
        }
      }
      params['dbTimelineFilter'] = dateObj

      params['url'] = ''
      params['alt'] = ''
      params['dashboardId'] = ''
      params['reportId'] = this.widget.dataOptions.newReportId
      params['newReport'] = this.widget.dataOptions.newReportId
        ? this.widget.dataOptions.newReport
        : null
      params['target'] = ''
      this.$popupView.openPopup(params)
    },
    openInAnalytics(reportId, widget) {
      if (this.widgetType === 'f-modular-report-widget') {
        let moduleName = widget.dataOptions.newReport.module.name
        let isCustomModule = widget.dataOptions.newReport.module.custom
        if (isWebTabsEnabled()) {
          let criteria = { config: { type: 'module_reports' } }
          let { name } =
            findRouteForReport('module_reports', pageTypes.REPORT_FORM, {
              moduleName,
            }) || {}
          let query = {
            reportId: reportId,
            module: moduleName,
            fromDashboard: true,
          }
          if (name) {
            this.$router.push({
              name,
              query,
            })
          }
          return
        }
        if (this.$helpers.isEtisalat()) {
          this.$router.push({
            path: '/app/em/modulereports/new',
            query: {
              reportId: reportId,
              fromDashboard: true,
              module: moduleName,
            },
          })
          return
        }
        switch (moduleName) {
          case 'workorder':
          case 'workorderLabour':
          case 'workorderCost':
          case 'workorderItem':
          case 'workorderTools':
          case 'workorderService':
          case 'workorderTimeLog':
          case 'workorderHazard':
          case 'plannedmaintenance':
            this.$router.push({
              path: '/app/wo/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'purchaseorder':
          case 'poterms':
          case 'purchaseorderlineitems':
          case 'purchaserequest':
          case 'purchaserequestlineitems':
            this.$router.push({
              path: '/app/purchase/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'inspectionTemplate':
          case 'inspectionResponse':
            this.$router.push({
              path: '/app/inspection/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'budget':
          case 'budgetamount':
            this.$router.push({
              path: '/app/ac/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'item':
          case 'tool':
          case 'itemTransactions':
          case 'tootTransactions':
          case 'itemTypes':
          case 'toolTypes':
          case 'storeRoom':
          case 'shipment':
          case 'transferrequest':
          case 'transferrequestpurchaseditems':
          case 'transferrequestshipmentreceivables':
            this.$router.push({
              path: '/app/inventory/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'alarm':
          case 'newreadingalarm':
          case 'readingalarmoccurrence':
          case 'bmsalarm':
          case 'mlAnomalyAlarm':
          case 'anomalyalarmoccurrence':
          case 'violationalarm':
          case 'violationalarmoccurrence':
          case 'operationalarm':
          case 'operationalarmoccurrence':
          case 'sensoralarm':
          case 'sensoralarmoccurrence':
          case 'sensorrollupalarm':
          case 'sensorrollupalarmoccurrence':
          case 'basealarm':
          case 'alarmoccurrence':
          case 'readingevent':
          case 'bmsevent':
          case 'mlAnomalyEvent':
          case 'violationevent':
          case 'operationevent':
          case 'sensorevent':
          case 'baseevent':
            this.$router.push({
              path: '/app/fa/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: this.$helpers.isLicenseEnabled('NEW_ALARMS')
                  ? moduleName
                  : 'alarm',
              },
            })
            break
          case 'serviceRequest':
            this.$router.push({
              path: '/app/sr/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: this.$helpers.isLicenseEnabled('ServiceRequest')
                  ? moduleName
                  : 'serviceRequest',
              },
            })
            break
          case 'asset':
            this.$router.push({
              path: '/app/at/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: 'asset',
              },
            })
            break
          case 'visitor':
          case 'visitorlog':
          case 'invitevisitor':
          case 'watchlist':
            this.$router.push({
              path: '/app/vi/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'tenant':
          case 'tenantunit':
          case 'tenantcontact':
          case 'quote':
          case 'contact':
          case 'tenantspaces':
          case 'quotelineitems':
          case 'quoteterms':
          case 'people':
          case 'newsandinformationsharing':
          case 'neighbourhoodsharing':
          case 'dealsandofferssharing':
          case 'contactdirectorysharing':
          case 'admindocumentsharing':
          case 'audienceSharing':
            this.$router.push({
              path: '/app/tm/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          case 'contracts':
          case 'purchasecontracts':
          case 'purchasecontractlineitems':
          case 'labourcontracts':
          case 'labourcontractlineitems':
          case 'warrantycontracts':
          case 'warrantycontractlineitems':
          case 'rentalleasecontracts':
          case 'rentalleasecontractlineitems':
            this.$router.push({
              path: '/app/ct/reports/new',
              query: {
                reportId: reportId,
                fromDashboard: true,
                module: moduleName,
              },
            })
            break
          default:
            if (isCustomModule) {
              this.$router.push({
                path: '/app/ca/reports/new',
                query: {
                  reportId: reportId,
                  fromDashboard: true,
                  module: moduleName,
                },
              })
            } else {
              this.$router.push({
                path: '/app/at/reports/new',
                query: {
                  reportId: reportId,
                  fromDashboard: true,
                  module: moduleName,
                },
              })
            }
            break
        }
        return
      }
      this.newWidgetOptionsPopoverToggle = false
      let deafultPath = '/app/em/analytics/'
      let routePath
      let {
        ANALYTIC_PORTFOLIO,
        ANALYTIC_BUILDING,
        HEAT_MAP,
        ANALYTIC_SITE,
        TREE_MAP,
        REGRESSION,
        SCATTER,
      } = pageTypes

      switch (widget.dataOptions.newReport.analyticsType) {
        case 1:
          if (
            this.newReportObj &&
            this.newReportObj.options.common.filters &&
            this.newReportObj.options.common.filters.xCriteriaMode
          ) {
            routePath = { path: 'portfolio', pageType: ANALYTIC_PORTFOLIO }
          } else {
            routePath = { path: 'building', pageType: ANALYTIC_BUILDING }
          }
          break
        case 3:
          routePath = { path: 'heatmap', pageType: HEAT_MAP }
          break
        case 6:
          routePath = { path: 'site', pageType: ANALYTIC_SITE }
          break
        case 7:
          routePath = { path: 'treemap', pageType: TREE_MAP }
          break
        case 8:
          routePath = { path: 'scatter', pageType: SCATTER }
          break
        default:
          routePath = { path: 'building', pageType: ANALYTIC_BUILDING }
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(routePath.pageType) || {}
        let query = { reportId: reportId, fromDashboard: true }
        name &&
          this.$router.push({
            name,
            query,
          })
      } else {
        this.$router.push({
          path: deafultPath + routePath.path,
          query: { reportId: reportId, fromDashboard: true },
        })
      }
    },
    openInPivotBuilder(id) {
      let path = `/app/em/pivotbuilder/new?reportId=${id}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.PIVOT_FORM)
        path = this.$router.resolve({
          name,
          query: { reportId: id },
        }).href
      }
      this.$router.push({ path: path })
    },
    exportAsFile(type, reportId, reportTemplate, widgets) {
      this.newWidgetOptionsPopoverToggle = false
      let params = this.newReportObj.params ? this.newReportObj.params : {}
      this.getRoute(reportId, widgets)
      params['url'] = this?.url ? this.url : ''
      this.$message({
        message: 'Exporting as ' + this.$constants.FILE_FORMAT[type],
        showClose: true,
        duration: 0,
      })
      // this.$message('Exporting as ' + this.$constants.FILE_FORMAT[type])
      if (type === 4) {
        params.exportParams = {
          showHeader: true,
          showPrintDetails: true,
        }
      }
      if (this.widgetType === 'f-modular-report-widget') {
        params['fileFormat'] = type
        params['reportId'] = reportId

        //export WYSIWYG ,pass current date picker range(if enabled) to export api\
        //, Child comp emits sycn newreportObj on  change in FModule or FNewReport. TO DO : remove reportObj state in Fwidget if not needed
        //get child state from child when needed like below
        try {
          let dateFilter = this.$refs['widgetComponent'].dateFilter
          if (dateFilter) {
            params['startTime'] = dateFilter.value[0]
            params['endTime'] = dateFilter.value[1]
          }
          //if dashboard filters applied on widget , get filters from widget comp prop and send param to export
          if (this.dbFilterJson) {
            params['filters'] = JSON.stringify(this.dbFilterJson)
            //summary url gets loaded ., param filters is applied from executereport api in ModuleNewReportComp
          }
        } catch (e) {
          console.error('failed to get report component ref for ', reportId)
        }

        this.$http
          .post('/v2/report/exportModuleReport', params)
          .then(response => {
            Message.closeAll()
            if (response.data && response.data.responseCode === 0) {
              this.exportDownloadUrl = response.data.result.fileUrl
            }
          })
      } else if (this.widgetType === 'PivotTableWrapper') {
        // params['startTime'] = this.newReportObj.dateRange.time[0]
        // params['endTime'] = this.newReportObj.dateRange.time[1]
        params['reportId'] = reportId
        params['fileFormat'] = type
        try {
          if (this.widgetDateObj) {
            params['startTime'] = this.widgetDateObj.value[0]
            params['endTime'] = this.widgetDateObj.value[1]
          } else if (this.dbTimelineFilter) {
            params['startTime'] = this.dbTimelineFilter.startTime
            params['endTime'] = this.dbTimelineFilter.endTime
          }

          //if dashboard filters applied on widget , get filters from widget comp prop and send param to export
          if (this.dbFilterJson) {
            params['filters'] = JSON.stringify(this.dbFilterJson)
            //summary url gets loaded ., param filters is applied from executereport api in ModuleNewReportComp
          }
        } catch (err) {
          console.error(err)
        }

        API.get('/v2/report/exportPivotReport', params).then(
          ({ data, error }) => {
            Message.closeAll()
            if (!error) {
              this.exportDownloadUrl = data.fileUrl
            }
          }
        )
      } else if (
        this.widgetType === 'f-modular-report-widget' ||
        this.widgetType === 'f-newchart-widget-optimize'
      ) {
        try {
          // dashboard filter handel for reading widgets
          //
        } catch (e) {
          console.error('failed to get report component ref for ', reportId)
        }
      } else {
        if (reportTemplate && reportTemplate !== 'null') {
          params['templateString'] = reportTemplate
        }
        params['startTime'] = this.newReportObj.dateRange.time[0]
        params['endTime'] = this.newReportObj.dateRange.time[1]
        params['dateOperator'] = this.newReportObj.dateRange.operatorId
        params['dateOperatorValue'] = this.newReportObj.dateRange.value
        params['chartType'] = this.newReportObj.options.type
        params['mode'] = 1
        params['reportId'] = reportId
        params['fileFormat'] = type

        API.put('/v3/report/reading/export', params).then(response => {
          Message.closeAll()
          if (response.error) {
            this.$message.error('Error while downloading widget PDF')
          } else if (response.data) {
            this.exportDownloadUrl = response.data.fileUrl
          }
        })
      }
    },
    initRefreshInterval() {
      this.subscribeLiveData()
      if (this.isAutoRefreshEnabled) {
        let self = this
        if (
          this.widget.dataOptions.refresh_interval &&
          this.widget.dataOptions.refresh_interval >= 120
        ) {
          // min 2 minutes
          if (this.refreshInterval) {
            clearInterval(this.refreshInterval)
          }

          let intervalTime = this.widget.dataOptions.refresh_interval * 1000 // converting seconds to millis
          this.refreshInterval = setInterval(function() {
            if (self.$refs['widgetComponent']) {
              self.$refs['widgetComponent'].refresh()
            }
          }, intervalTime)
        }
      }
    },
    refresh() {
      if (this.$refs['widgetComponent']) {
        this.$refs['widgetComponent'].refresh()
      }
    },
    getCurrentValueReadings() {
      let readingList = []
      if (!this.widget.dataOptions.staticKey) {
        return readingList
      }
      if (this.widget.dataOptions.staticKey === 'readingcard') {
        let currentDateOperators = [22, 31, 28, 68, 44] // today, this week/month/quarter/year
        if (
          this.widget.dataOptions.paramsJson &&
          this.widget.dataOptions.paramsJson.parentId &&
          this.widget.dataOptions.paramsJson.fieldId &&
          this.widget.dataOptions.paramsJson.aggregateFunc === 6 &&
          currentDateOperators.indexOf(
            this.widget.dataOptions.paramsJson.dateOperator
          ) !== -1
        ) {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldId: this.widget.dataOptions.paramsJson.fieldId + '',
          })
        }
      }
      if (this.widget.dataOptions.staticKey === 'readingGaugeCard') {
        let currentDateOperators = [22, 31, 28, 68, 44] // today, this week/month/quarter/year
        if (
          this.widget.dataOptions.paramsJson &&
          this.widget.dataOptions.paramsJson.parentId &&
          this.widget.dataOptions.paramsJson.fieldId &&
          this.widget.dataOptions.paramsJson.aggregateFunc === 6 &&
          currentDateOperators.indexOf(
            this.widget.dataOptions.paramsJson.dateOperator
          ) !== -1
        ) {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldId: this.widget.dataOptions.paramsJson.fieldId + '',
          })
        }
      } else if (this.widget.dataOptions.staticKey === 'readingWithGraphCard') {
        let currentDateOperators = [22, 31, 28, 68, 44] // today, this week/month/quarter/year
        if (
          this.widget.dataOptions.paramsJson &&
          this.widget.dataOptions.paramsJson.parentId &&
          this.widget.dataOptions.paramsJson.fieldId &&
          currentDateOperators.indexOf(
            this.widget.dataOptions.paramsJson.dateOperator
          ) !== -1
        ) {
          if (this.widget.dataOptions.paramsJson.aggregateFunc === 6) {
            readingList.push({
              parentId: this.widget.dataOptions.paramsJson.parentId + '',
              fieldId: this.widget.dataOptions.paramsJson.fieldId + '',
            })
          } else {
            if (this.widget.dataOptions.metaJson) {
              let metaJsonObj = JSON.parse(this.widget.dataOptions.metaJson)
              if (
                metaJsonObj.aggregateFunction &&
                metaJsonObj.aggregateFunction === 'lastValue'
              ) {
                readingList.push({
                  parentId: this.widget.dataOptions.paramsJson.parentId + '',
                  fieldId: this.widget.dataOptions.paramsJson.fieldId + '',
                })
              }
            }
          }
        }
      } else if (
        this.widget.dataOptions.staticKey.indexOf('fahuStatus') !== -1
      ) {
        if (this.widget.dataOptions.staticKey === 'fahuStatusCard') {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'runstatus',
            moduleName: 'prefilterstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'valvefeedback',
            moduleName: 'prefilterstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'tripstatus',
            moduleName: 'bagfilterstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'automanualstatus',
            moduleName: 'prefilterstatus',
          })
        } else if (this.widget.dataOptions.staticKey === 'fahuStatusCard1') {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'runstatus',
            moduleName: 'runstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'valvecommand',
            moduleName: 'runstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'tripstatus',
            moduleName: 'runstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'automanualstatus',
            moduleName: 'automanualstatus',
          })
        } else if (this.widget.dataOptions.staticKey === 'fahuStatusCard2') {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'runstatus',
            moduleName: 'runstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'valvefeedback',
            moduleName: 'supplyairtemperature',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'tripstatus',
            moduleName: 'runstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'automanualstatus',
            moduleName: 'runstatus',
          })
        } else if (this.widget.dataOptions.staticKey === 'fahuStatusCard3') {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'runstatus',
            moduleName: 'automanualstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'valvecommand',
            moduleName: 'returntemperature',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'tripstatus',
            moduleName: 'returntemperature',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'automanualstatus',
            moduleName: 'automanualstatus',
          })
        } else if (this.widget.dataOptions.staticKey === 'fahuStatusCardNew') {
          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'runstatus',
            moduleName: 'runstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'valvefeedback',
            moduleName: 'valvefeedback',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'tripstatus',
            moduleName: 'tripstatus',
          })

          readingList.push({
            parentId: this.widget.dataOptions.paramsJson.parentId + '',
            fieldName: 'automanualstatus',
            moduleName: 'automanualstatus',
          })
        }
      }
      return readingList
    },
    refreshData() {
      if (self.$refs['widgetComponent']) {
        self.$refs['widgetComponent'].refresh()
      }
    },
    subscribeLiveData() {
      let self = this
      self.pubSubWatcherKey = null
      let readings = this.getCurrentValueReadings()
      if (readings && readings.length) {
        if (this.$wms) {
          this.$http
            .post('/v2/fetchLiveUpdateFields', {
              liveUpdateFields: readings,
            })
            .then(response => {
              let readingFields = response.data.result.liveUpdateFields
              if (readingFields && readingFields.length) {
                for (let reading of readingFields) {
                  let topic =
                    '__livereading__/' +
                    reading.parentId +
                    '/' +
                    reading.fieldId
                  this.$wms.subscribe(topic, this.refreshData)
                }
              }
            })
        } else {
          self.pubSubWatcherKey = this.subscribe(
            'readingChange',
            { readings: readings },
            this.refreshData
          )
        }
      }
    },
    unsubscribeLiveData() {
      let readings = this.getCurrentValueReadings()
      if (readings && readings.length) {
        if (this.$wms) {
          this.$http
            .post('/v2/fetchLiveUpdateFields', {
              liveUpdateFields: readings,
            })
            .then(response => {
              let readingFields = response.data.result.liveUpdateFields
              if (readingFields && readingFields.length) {
                for (let reading of readingFields) {
                  let topic =
                    '__livereading__/' +
                    reading.parentId +
                    '/' +
                    reading.fieldId
                  this.$wms.unsubscribe(topic, this.refreshData)
                }
              }
            })
        } else if (this.pubSubWatcherKey) {
          this.unsubscribe(this.pubSubWatcherKey, 'readingChange', {
            readings: readings,
          })
          this.pubSubWatcherKey = null
        }
      }
    },
    getNewReportObj(report, result) {
      if (report && result) {
        this.$set(this.widget.dataOptions, 'newReport', result.report)
        this.newReportObj = report
      }
    },
    getWidget(widget) {
      if (widget) {
        return widget
      }
    },
    cleanupRefreshInterval() {
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval)
        this.refreshInterval = null
      }
      this.unsubscribeLiveData()
    },
    initData() {
      let self = this
      self.loading = true
      if (
        !this.selectedPeriod &&
        this.widget.header &&
        this.widget.header.periods
      ) {
        this.selectedPeriod = this.widget.header.periods[0].value
        this.widget.header.subtitle = this.selectedPeriod
      }
      let url = this.widget.dataOptions.dataurl
      if (this.selectedPeriod) {
        url =
          url.indexOf('?') > 0
            ? url + '&period=' + this.selectedPeriod
            : url + '?period=' + this.selectedPeriod
      }
      self.$http
        .get(url)
        .then(function(response) {
          self.loading = false
          if (response.data.reportData) {
            self.data = {
              xAxis: {},
              yAxis: {},
              name: self.widget.dataOptions.name,
            }
            let reportData = response.data.reportData.filter(function(item) {
              if (item.label) {
                return true
              } else if (item.value && !item.label) {
                item.label = 'unknown'
                return true
              }
              return false
            })
            self.data = reportData
          } else {
            if (self.widget.dataKey && response.data[self.widget.dataKey]) {
              self.data = response.data[self.widget.dataKey]
            } else if (self.widget.dataKey && response.data.length) {
              self.data = response.data
            } else if (self.demoData) {
              self.data = self.demoData
            } else {
              self.failed = true
            }
          }
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.failed = true
          }
        })
    },
    intervalUpdate(interval) {
      this.widget.dataOptions.refresh_interval = interval
      this.cleanupRefreshInterval()
      this.initRefreshInterval()
    },
    reportLoaded(report, reportObject) {
      let self = this
      this.report = report
      this.reportObject = reportObject
      if (self.report !== null) {
        if (self.report.options.timeObject.time !== null) {
          self.setSubTitle = self.timeForm(
            self.report.options.timeObject.time,
            self.report.options.timeObject.field
          )
        }
      }
    },
    dateFilterUpdated(fulldate) {
      if (this.$refs['widgetChildComponent']) {
        this.$refs['widgetChildComponent'].setDateFilter(fulldate)
      }
    },
    timeForm(time, option) {
      if (option === 'D') {
        return moment(time[0])
          .calendar()
          .split(' at')[0]
      } else if (option === 'W') {
        return (
          moment(time[0])
            .tz(this.$timezone)
            .format('MMM DD, YYYY') +
          ' - ' +
          moment(time[1])
            .tz(this.$timezone)
            .format('MMM DD, YYYY')
        )
      } else if (option === 'M') {
        return (
          moment(time[0])
            .tz(this.$timezone)
            .format('MMM DD, YYYY') +
          ' - ' +
          moment(time[1])
            .tz(this.$timezone)
            .format('MMM DD, YYYY')
        )
      } else if (option === 'Y') {
        return (
          moment(time[0])
            .tz(this.$timezone)
            .format('MMM DD, YYYY') +
          ' - ' +
          moment(time[1])
            .tz(this.$timezone)
            .format('MMM DD, YYYY')
        )
      } else {
        return ''
      }
    },
    getThisperiod(option) {
      if (option === 'D') {
        return 'Today'
      } else if (option === 'W') {
        return 'This Week'
      } else if (option === 'M') {
        return 'This Month'
      } else if (option === 'Y') {
        return 'This Year'
      } else {
        return ''
      }
    },
    selectPeriod(period) {
      this.selectedPeriod = period.value
      this.widget.header.subtitle = period.value
    },
    getPeriod(period) {
      period = period.toLowerCase()
      switch (period) {
        case 'current week': {
          let firstDay = moment().startOf('week')
          let endDay = moment().endOf('week')
          return (
            moment(firstDay).format('DD') +
            ' - ' +
            moment(endDay).format('DD MMM YYYY')
          )
        }
        case 'last week': {
          let lastWeekStart = moment()
            .subtract(1, 'weeks')
            .startOf('week')
          let lastWeekEnd = moment()
            .subtract(1, 'weeks')
            .endOf('week')
          return (
            moment(lastWeekStart).format('DD') +
            ' - ' +
            moment(lastWeekEnd).format('DD MMM YYYY')
          )
        }
        case 'today':
          return moment(new Date()).format('DD MMM YYYY')
      }
      return period
    },
    openReport(id, widget) {
      this.newWidgetOptionsPopoverToggle = false
      if (widget?.dataOptions?.reportType === 5) {
        let route_url = `/app/em/pivot/view/${widget?.dataOptions?.newReportId}`
        if (isWebTabsEnabled()) {
          let { name } = findRouteForTab(pageTypes.PIVOT_VIEW) ?? {}
          if (!isEmpty(name)) {
            route_url = this.$router.resolve({
              name,
              params: { reportId: widget?.dataOptions?.newReportId },
            }).href
            this.$router.push(route_url)
          } else {
            this.$dialog.confirm({
              title: this.$t('common._common.tab_not_configured'),
              message: this.$t('common._common.tab_not_configured_message'),
              rbLabel: this.$t('common._common.ok'),
              lbHide: true,
            })
          }
          return
        }
        this.$router.push(route_url)
        return
      }
      let currentModule = this.getCurrentModule()
      if (widget.type === 'view') {
        let modulePath = ''
        if (widget.dataOptions.moduleName === 'workorder') {
          modulePath = '/app/wo/orders/' + widget.dataOptions.viewName
        } else if (
          widget.dataOptions.moduleName === 'alarm' ||
          widget.dataOptions.moduleName === 'alarmoccurrence' ||
          widget.dataOptions.moduleName === 'readingalarmoccurrence' ||
          widget.dataOptions.moduleName === 'mlalarmoccurrence' ||
          widget.dataOptions.moduleName === 'violationalarmoccurrence' ||
          widget.dataOptions.moduleName === 'baseevent' ||
          widget.dataOptions.moduleName === 'newreadingalarm'
        ) {
          modulePath = '/app/fa/faults/' + widget.dataOptions.viewName
        }
        this.$router.push(modulePath)
      } else {
        let moduleName = null
        let modulePath = null
        if (widget.dataOptions.newReport) {
          moduleName = widget.dataOptions.newReport.module.name
        } else {
          moduleName = currentModule.module
        }
        if (isWebTabsEnabled() && widget.dataOptions.newReportId) {
          let routeObj
          if (!moduleName) {
            routeObj = findRouteForReport(
              'analytics_reports',
              pageTypes.REPORT_VIEW
            )
          } else if (moduleName === 'energydata') {
            routeObj = findRouteForReport(
              'analytics_reports',
              pageTypes.REPORT_VIEW,
              { moduleName }
            )
          } else {
            routeObj = findRouteForReport(
              'module_reports',
              pageTypes.REPORT_VIEW,
              { moduleName }
            )
          }

          let { name } = routeObj || {}
          let params = { reportid: widget.dataOptions.newReportId }

          if (!isEmpty(name)) {
            this.$router.push({ name, params })
          } else {
            this.$dialog.confirm({
              title: this.$t('common._common.tab_not_configured'),
              message: this.$t('common._common.tab_not_configured_message'),
              rbLabel: this.$t('common._common.ok'),
              lbHide: true,
            })
          }
          return
        }
        if (
          [
            'workorder',
            'workorderLabour',
            'workorderCost',
            'workorderItem',
            'workorderTools',
            'workorderService',
            'workorderTimeLog',
            'workorderHazard',
            'plannedmaintenance',
          ].includes(moduleName)
        ) {
          modulePath = '/app/wo'
        } else if (
          [
            'alarm',
            'newreadingalarm',
            'readingalarmoccurrence',
            'bmsalarm',
            'mlAnomalyAlarm',
            'anomalyalarmoccurrence',
            'violationalarm',
            'violationalarmoccurrence',
            'operationalarm',
            'operationalarmoccurrence',
            'sensoralarm',
            'sensoralarmoccurrence',
            'sensorrollupalarm',
            'sensorrollupalarmoccurrence',
            'basealarm',
            'alarmoccurrence',
            'readingevent',
            'bmsevent',
            'mlAnomalyEvent',
            'violationevent',
            'operationevent',
            'sensorevent',
            'baseevent',
          ].includes(moduleName)
        ) {
          modulePath = '/app/fa'
        } else if (moduleName === 'energydata') {
          modulePath = '/app/em'
        } else if (
          ['inspectionTemplate', 'inspectionResponse'].includes(moduleName)
        ) {
          modulePath = '/app/inspection'
        } else if (
          ['asset', 'assetbreakdown', 'vendors'].includes(moduleName)
        ) {
          modulePath = '/app/at'
        } else if (
          [
            'tenant',
            'tenantcontact',
            'tenantunit',
            'quote',
            'contact',
            'tenantspaces',
            'quotelineitems',
            'quoteterms',
            'people',
            'newsandinformationsharing',
            'neighbourhoodsharing',
            'dealsandofferssharing',
            'contactdirectorysharing',
            'admindocumentsharing',
            'audienceSharing',
          ].includes(moduleName)
        ) {
          modulePath = '/app/tm'
        } else if (
          [
            'contracts',
            'purchasecontracts',
            'labourcontracts',
            'warrantycontracts',
            'rentalleasecontracts',
            'purchasecontractlineitems',
            'labourcontractlineitems',
            'warrantycontractlineitems',
            'rentalleasecontractlineitems',
          ].includes(moduleName)
        ) {
          modulePath = '/app/ct'
        } else if (
          ['visitorlog', 'visitor', 'invitevisitor', 'watchlist'].includes(
            moduleName
          )
        ) {
          modulePath = '/app/vi'
        } else if (['serviceRequest'].includes(moduleName)) {
          modulePath = '/app/sr'
        } else if (
          [
            'purchaseorder',
            'purchaseorderlineitems',
            'purchaserequest',
            'poterms',
            'purchaserequestlineitems',
          ].includes(moduleName)
        ) {
          modulePath = '/app/purchase'
        } else if (['budget', 'budgetamount'].includes(moduleName)) {
          modulePath = '/app/ac'
        } else if (
          [
            'item',
            'tool',
            'itemTransactions',
            'tootTransactions',
            'itemTypes',
            'toolTypes',
            'storeRoom',
            'shipment',
            'transferrequest',
            'transferrequestshipmentreceivables',
            'transferrequestpurchaseditems',
          ].includes(moduleName)
        ) {
          modulePath = '/app/inventory'
        } else {
          if (
            widget.dataOptions.newReport !== undefined &&
            widget.dataOptions.newReport.module.custom
          ) {
            modulePath = '/app/ca'
          } else if (currentModule.custom) {
            modulePath = '/app/ca'
          } else {
            modulePath = '/app/at'
          }
        }
        if (this.$helpers.isEtisalat()) {
          let url =
            '/app/em/modulereports/newview/' + widget.dataOptions.newReportId
          this.$router.push(url)
          return
        }
        if (widget.dataOptions.newReportId) {
          let url =
            modulePath + '/reports/newview/' + widget.dataOptions.newReportId
          this.$router.push(url)
        } else if (id) {
          let url = modulePath + '/reports/view/' + id
          if (
            this.$route.params &&
            this.$route.params.dashboardlink === 'buildingdashboard' &&
            this.$route.params.buildingid
          ) {
            let data = { buildingid: parseInt(this.$route.params.buildingid) }
            this.$router.push({
              path: url,
              query: { reportSpaceFilterContext: JSON.stringify(data) },
            })
          } else if (
            this.$route.params &&
            (this.$route.params.dashboardlink === 'chillerplant' ||
              this.$route.params.dashboardlink === 'chillers') &&
            this.$route.params.buildingid
          ) {
            let data = { chillerId: parseInt(this.$route.params.buildingid) }
            this.$router.push({
              path: url,
              query: { reportSpaceFilterContext: JSON.stringify(data) },
            })
          } else {
            this.$router.push(url)
          }
        }
      }
    },
    openPivotReport(id) {
      this.$router.push(`/app/em/pivot/view/${id}`)
    },
    openGraphics(id) {
      this.newWidgetOptionsPopoverToggle = false
      let url = '/app/em/graphics/view/' + id
      this.$router.push({ path: url })
    },
    openGraphicsInAnalytics(id) {
      this.newWidgetOptionsPopoverToggle = false
      if (this.$refs['widgetComponent']) {
        this.$refs['widgetComponent'].openInAnalytics(id)
      }
    },
    exportGraphicsAsImage(id) {
      this.newWidgetOptionsPopoverToggle = false
      if (this.$refs['widgetComponent']) {
        this.$refs['widgetComponent'].exportAsImage(id)
      }
    },
    getCurrentModule() {
      let routeObj = this.$route
      let module = null
      let rootPath = null
      if (routeObj.meta.module) {
        module = routeObj.meta.module
        rootPath = routeObj.path
      } else {
        if (routeObj.matched) {
          for (let matchedRoute of routeObj.matched) {
            if (matchedRoute.meta.module) {
              module = matchedRoute.meta.module
              rootPath = matchedRoute.path
              break
            }
          }
        }
      }
      return {
        module: module,
        rootPath: rootPath,
      }
    },
    editHelpText() {
      this.newWidgetOptionsPopoverToggle = false
      this.$emit('helpTextConfig', this.widget)
    },
    deleteChart(widget, dashboard) {
      this.newWidgetOptionsPopoverToggle = false
      let data = { widgetId: widget.id, dashboardId: dashboard, widget }
      this.$emit('deletechart', data)
    },
    editWidget(widget, dashboard) {
      if (widget.dataOptions) {
        widget.dataOptions.edit = true
      }
      this.$emit('editwidget', widget)
      this.newWidgetOptionsPopoverToggle = false
    },
    hideWidget(widget, dashboard) {
      let data = { widgetId: widget.id, dashboardId: dashboard, widget }
      this.$emit('hidewidget', data)
    },
    editheader() {
      this.changeheader = true
    },
    titlechage() {
      console.log('****** widget title', this.widget.header.title)
    },
    blurevent() {
      if (this.widget.header.title === '') {
        this.changeheader = true
      } else if (this.widget.header.title === null) {
        this.changeheader = true
      } else {
        this.changeheader = false
      }
      this.$emit('widget', this.widget)
    },
    addwidgetHedaer() {
      this.changeheader = true
    },
    viewHeader(header) {
      if (
        this.graphicsOptions &&
        this.graphicsOptions.hideHeader &&
        !this.mode
      ) {
        return false
      } else if ((header && header.title) || this.widget.type == 'chart') {
        return true
      } else if (this.changeheader) {
        return true
      } else {
        return false
      }
    },
    getReportutil(data) {
      this.ChartType = data.ChartType ? data.ChartType : 'line'
    },
    sendReportUtil(data) {
      this.sendUtil = data
    },
  },
}
</script>
<style lang="scss">
/* .dashboard-container .fc-report .fc-report-filter  {
  position: absolute;
  width: 100%;
  z-index: 100;
  display: block;
  width: 100%;
  padding-right: 0px;
  margin-left: -10px;
  top: 0px;
  right: 30px;
  width: 40px;
  padding-left: 0px ;
  padding-right: 0px;
} */
/* .dashboard-f-widget:hover .fc-report .fc-report-filter  {
  display: block !important;
} */
.dashboard-f-widget .fc-widget {
  position: relative;
  /* margin: 10px !important;
  width: calc(100% - 20px) !important;
  height: calc(100% - 20px) !important; */
}
.externalLink,
.chart-delete-icon {
  color: #868686;
  padding-top: 3px;
}
.fc-newChart-options-label {
  padding: 2px;
}
.fc-newChart-options-label:hover {
  background-color: #868686;
}
.fc-widget-body {
  overflow: hidden;
  /* this fix affects the reading cards*/
  /* height: calc(100% - 56px); */
  height: 100%;
}
.fc-widget-body.fc-newchart-widget {
  height: calc(100% - 56px) !important;
}
.fc-widget-dragable-body {
  overflow: hidden;
  height: 100%;
}
.fc-widget.Categories .fc-widget-body {
  overflow: hidden;
}
.dashboard-container .change-chart-select {
  display: none;
}
.fc-widget:hover .change-chart-select {
  display: block !important;
}
/* .dashboard-f-widget:hover .change-chart-select {
  display: block !important;
} */

.fc-widget #tabular-report-hot {
  border: none;
  /* height: 100%; */
}
.chart-icon-report {
  width: 1.4vw;
  height: 1.1vw;
}
/* #1334 .fc-report:second-child {

} */
/* .fc-widget .second-class .boolean-conatiner g.y.y-axis-group.axis {
    display: none;
} */
.fc-secondChart .fc-report-filter.row.header {
  padding: 0px;
}
.fc-secondChart .f-legends {
  position: absolute;
  top: 100px;
  left: 0;
  width: 100%;
}
.dualchart g.x.axis,
.dualchart .month-axis {
  display: none;
}
.fc-widget .dashboard-container .widget-legends {
  padding: 20px;
  padding-left: 40px;
  padding-right: 40px;
  position: absolute;
  bottom: 0;
  width: 100%;
  display: -webkit-box;
  display: -ms-box;
}
.fc-widget .fc-new-chart.bb {
  position: relative;
  top: 10px;
}
.fc-widget .fLegendContainer.fLegendContainer-new {
  top: 25px;
  position: relative;
  z-index: 100;
}
.fc-new-tabular {
  overflow: scroll;
}

.fc-widget .f-multichart {
  padding-top: 35px;
}

.fc-widget-moreicon-vertical {
  display: none;
  // transform: rotate(90deg);
  font-size: 18px;
  color: #6a6990;
  opacity: 1;
  background: #fff;
  border-radius: 3px;
  // need to remove after the proper fix
  padding-right: 5px;
  font-weight: 300 !important;
  //
}
.fc-widget-moreicon-vertical:hover {
  opacity: 1;
}
.fc-widget-edit-options,
.fc-widget-view-options,
.fc-widget-filter-config-options {
  .fc-newChart-options-label {
    padding: 12px;
    color: #25243e;
    font-size: 13px;
  }
}
.fc-widget-edit-options,
.fc-widget-view-options,
.fc-widget-filter-config-options {
  .fc-newChart-options-label:hover {
    background-color: hsla(0, 0%, 96%, 0.5);
  }
}

.fc-widget-fullScreen {
  display: none;
  margin-right: 16px;
  font-size: 15px;
  color: #6a6990;
  opacity: 0.6;
}
.fc-widget-fullScreen:hover {
  opacity: 1;
}

.tabular {
  display: block;
}

.fc-widget-more-options {
  padding: 0px;
}
.mobiledashboard .fc-widget .f-multichart {
  padding-top: 0px !important;
}
.fc-wiget-with-title {
  height: calc(50px - 100%) !important;
}

.fc-widget .drilldown-breadcrumb {
  margin-top: 25px;
}
</style>
