<template>
  <div class=" formbuilder-fullscreen-popup floor-plan-builder height100">
    <viwer-tool-bar
      v-if="!hideHeader"
      :floorPlan="floorPlan"
      :floorPlanId="floorPlanId"
      :flooplans="flooplans"
      :leagend="leagend"
      :mappedSpaceId="mappedSpaceId"
      :controlCategoryList="controlCategoryList"
      @action="handleLeagend"
      @active="handleLeagendActive"
      @type="handleleagendType"
      @OnchangeFloorPlan="floorplanId => loadFloorplan(floorplanId)"
    ></viwer-tool-bar>
    <div class="height100 relative">
      <div class="floor-plan-conatiner height100 row">
        <div
          class="floor-plan-viewer-container col-12 p10 height100"
          ref="floorPlanContainer"
        >
          <canvas id="floorplan-canvas" ref="floorPlan"></canvas>
        </div>
      </div>
    </div>
    <floorplanaction
      v-if="floorActionVisible"
      @setValue="updateActionEvents"
      :visibility.sync="floorActionVisible"
      :floorPlan="floorPlan"
      :element="selectedElement"
      :spaceList="spaceList"
      :controlCategoryList="controlCategoryList"
      :spaceControllableCategoriesMap="spaceControllableCategoriesMap"
    >
    </floorplanaction>
    <floorplansettings @action="handleAction" :refresh="refresh">
    </floorplansettings>
    <leagends
      v-if="leagend && leagend.active"
      :floorPlan="floorPlan"
      :mappedSpaceId="mappedSpaceId"
      :controlCategoryList="controlCategoryList"
      :leagend="leagend"
      class="fp-leagends"
      @action="handleLeagend"
      @active="handleLeagendActive"
      @type="handleleagendType"
    ></leagends>
    <spacepopup
      v-if="spaceDetailsPopup"
      @close="closespaceDetailsPopup"
      :visibility.sync="spaceDetailsPopup"
      :floorPlan="floorPlan"
      :element="selectedElement"
      :spaceList="spaceList"
    ></spacepopup>
    <SetReadingPopup
      v-if="controlPopup.visible"
      :saveAction="onSetControlValue"
      :closeAction="resetControlValue"
      :recordId="controlPopup.controlConfig.assetId"
      :fieldId="controlPopup.controlConfig.fieldId"
      :groupId="controlPopup.controlConfig.groupId"
      :recordName="''"
    ></SetReadingPopup>
    <pointerdetails
      v-if="pointerDetailsPopup"
      :visibility.sync="pointerDetailsPopup"
      @close="closePointerdetailPopup"
      :element="selectedElement"
    >
    </pointerdetails>
    <el-dialog
      title="TREND"
      v-if="trendPopup.visible"
      :visible.sync="trendPopup.visible"
      :append-to-body="true"
      custom-class="f-popup-view"
      top="0%"
    >
      <div
        @click="openSelectedReadingInAnalytics"
        class="content analytics-txt"
        style="cursor: pointer; color: rgb(57, 178, 194); font-size: 13px; text-align: right; font-weight: 500; margin-right: 20px;"
      >
        Go to Analytics
        <img
          style="width:13px; height: 9px;"
          src="~statics/icons/right-arrow.svg"
        />
      </div>
      <f-new-analytic-report
        :config.sync="trendPopup.analyticsConfig"
      ></f-new-analytic-report>
    </el-dialog>
    <div
      class="graphics-actions"
      v-if="clickActions"
      :style="{ top: clickActions.top + 'px', left: clickActions.left + 'px' }"
    >
      <ul class="graphics-actions-menu">
        <li
          class="graphics-actions-menu-item"
          v-for="(action, index) in clickActions.actions"
          :key="index"
          @click="handleClickAction(action)"
        >
          {{
            action.actionType === 'controlAction'
              ? 'Setpoint'
              : action.actionType === 'hyperLink'
              ? action.data.linkType === 'graphics'
                ? 'Open Graphics'
                : 'Goto: ' + action.actionName
              : action.actionName
          }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { fabric } from 'fabric'

import floorplanaction from 'pages/floorPlan/components/FloorPlanActionDialog'
import floorplansettings from 'pages/floorPlan/components/FloorPlanViewerSettings'
import leagends from 'pages/floorPlan/components/FloorPlanLeagend'
import viwerToolBar from 'pages/floorPlan/components/FloorPlanViwerTools'
import spacepopup from 'pages/floorPlan/components/SpaceDetailsPopup'
import pointerdetails from 'pages/floorPlan/components/FloorPlanPointerDetailPopup'
import SetReadingPopup from '@/readings/SetReadingValue'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'

import Notification from 'pages/floorPlan/components/FloorplanBuilderNotification'
import dataMixin from 'pages/floorPlan/mixins/FloorPlanDataMixin'
import canvasSetup from 'pages/floorPlan/mixins/CanvasSetupMixin'
import floorAction from 'pages/floorPlan/mixins/FloorPlanActionMixin'
import drawElementsMixin from 'pages/floorPlan/mixins/DrawElementsMixin'
import FGraphicUtil from 'pages/assets/graphics/FGraphicUtil'
import leagendsMixin from 'pages/floorPlan/mixins/FloorPlanLeagendActions'
import filterMixin from 'pages/floorPlan/mixins/FloorPlanFiltersMixin'
import floorplanvalue from 'pages/floorPlan/mixins/FloorPlanValueMixin'
import FloorPlanUtil from 'pages/floorPlan/Utils/FloorPlanUtil'
import variableMixin from 'pages/floorPlan/mixins/FloorPlanVariableMixin'
import JumpToHelper from '@/mixins/JumpToHelper'

export default {
  props: ['height', 'width', 'floorId', 'hideHeader'],
  mixins: [
    dataMixin,
    canvasSetup,
    floorAction,
    FGraphicUtil,
    Notification,
    drawElementsMixin,
    leagendsMixin,
    filterMixin,
    floorplanvalue,
    variableMixin,
    JumpToHelper,
  ],
  components: {
    floorplanaction,
    floorplansettings,
    leagends,
    viwerToolBar,
    spacepopup,
    SetReadingPopup,
    FNewAnalyticReport,
    pointerdetails,
  },
  computed: {
    fileId() {
      if (this.floorPlan) {
        return this.floorPlan.fileId
      }
      return null
    },
  },
  data() {
    return {
      isLoading: false,
      readonly: false,
      currentDragElement: null,
      selectedElement: null,
      refresh: false,
      floorMapVisible: false,
      floorActionVisible: false,
      mappedSpaceId: [],
      layerVisible: false,
      spaceDetailsPopup: false,
      pointerDetailsPopup: false,
      layer: 'default',
      sublayer: 'control',
      polygonOptions: {
        polygonMode: false,
        pointArray: new Array(),
        lineArray: new Array(),
        activeLine: null,
        activeShape: false,
      },
      graphicUtil: null,
      config: {},
      propertiesvisible: false,
      variables: [],
      liveValues: {},
      usedVariables: [],
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
    this.getAllCategoryEnum()
  },
  destroyed() {
    this.destroyFloorPlan()
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      this.isLoading = true
      Promise.all([
        this.fetchFloorDetails(),
        this.fetchFloorSpaces(),
        this.getFloorControlCategory(),
        this.loadFloorSpaces(),
      ]).then(() => {
        this.fetchFloorPlan().then(() => {
          this.initCanvas()
        }),
          this.fetchListOfFloorPlans()
      })
    },
    loadFloorplan(floorplanId) {
      this.canvas.clear()
      this.canvas.dispose()
      this.fetchFloorPlan(floorplanId).then(() => {
        this.initCanvas()
      })
    },
    initCanvas() {
      this.isLoading = false
      let that = this
      this.canvas = new fabric.Canvas(this.$refs['floorPlan'], {
        selection: false,
        preserveObjectStacking: true,
      })
      this.graphicUtil = new FloorPlanUtil.Floorplan(
        this.canvas,
        this.readonly,
        this.graphics
      )
      this.canvas.loadFromJSON(
        this.floorPlan.canvas,
        () => {
          that.loadVariables()
          that.canvas.backgroundColor = null
          // that.canvasFitToScreen()
          that.fitToCenter()
          that.canvas.renderAll()
        },
        (el, element) => {
          this.canvas.backgroundColor = null
          this.applyValue(element)
          this.applyFilters(element)
          this.setUpViewerProps(element)
          this.setUpViwerObjectEvents(element)
          this.getListOfSpaceId(element)
        }
      )
      this.canvas.renderAll()
      this.$nextTick(() => {
        try {
          this.subscribeToLiveData()
        } catch ({ message }) {
          this.$message.error(message)
        }
        this.setupEditor()

        this.$nextTick(() => {
          this.registerNativeEvents()
          // this.panMode()
          this.setupViewerEvents()
        })
      })
    },
    setupEditor() {
      this.setFitDimensions()
      this.$nextTick(() => {
        this.setupViewerEvents()
        this.fitToResolution()
      })
    },
    registerNativeEvents() {
      window.addEventListener('keydown', this.onKeyPress, {
        passive: true,
      })

      window.addEventListener('resize', this.onWindowResize, {
        passive: true,
      })
      this.$refs['floorPlanContainer'].addEventListener('contextmenu', e => {
        if (e.button == 2) e.preventDefault()
      })
    },
    cancel() {},
    save() {
      this.updateFloorPlan()
    },
    destroyFloorPlan() {
      this.unSubscribeToLiveData()
      this.canvas = null
    },
    closespaceDetailsPopup() {
      this.spaceDetailsPopup = false
    },
  },
}
</script>

<style>
.fb-settings-block button {
  padding: 12px;
}
.fc-v1-site-overview-main-sec .floor-plan-builder .floorplan-builder-toolbar {
  /* position: absolute; */
  z-index: 10;
  width: 100%;
  box-shadow: none;
}

.floor-plan-builder .graphics-actions {
  position: absolute;
  min-width: 200px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}
.floor-plan-builder .graphics-actions .graphics-actions-menu {
  padding: 6px 0;
  margin: 0;
}
.floor-plan-builder .graphics-actions .graphics-actions-menu-item {
  list-style: none;
  padding: 12px;
  font-size: 13px;
  cursor: pointer;
}
.floor-plan-builder .graphics-actions .graphics-actions-menu-section {
  border-top: 1px solid #ededed;
}
.floor-plan-builder .graphics-actions .graphics-actions-menu-item:hover {
  background: #f5f7fa;
}
</style>
