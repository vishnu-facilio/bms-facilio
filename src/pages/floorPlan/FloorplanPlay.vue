<template>
  <div class="floor-plan-builder height100">
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
          class="floor-plan-viewer-container col-12 height100"
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
      :area="selectedArea"
    ></floorplanaction>
    <floorplansettings
      @action="handleAction"
      :refresh="refresh"
      :viewMode="viewMode"
      :viewParams="viewParams"
      :hideSettings="hideSettings"
    ></floorplansettings>
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
      :area="selectedArea"
    ></spacepopup>
    <pointerpop
      v-if="pointerpopup"
      @close="closespaceDetailsPopup"
      :visibility.sync="pointerpopup"
      :floorPlan="floorPlan"
      :employeeList="employeeList"
      :fields="employeeFields"
      :areas="areas"
      :canvasMeta="canvas"
      :element="selectedElement"
    ></pointerpop>
    <elementpop
      v-if="elementpopup"
      @close="closespaceDetailsPopup"
      :visibility.sync="elementpopup"
      :floorPlan="floorPlan"
      :employeeList="employeeList"
      :fields="employeeFields"
      :areas="areas"
      :canvasMeta="canvas"
      :element="selectedElement"
    ></elementpop>
    <spacebuttonpopup
      v-if="spaceButtonPopup"
      @close="closespaceDetailsPopup"
      :visibility.sync="spaceButtonPopup"
      :floorPlan="floorPlan"
      :element="selectedElement"
      :spaceList="spaceList"
      :area="selectedArea"
      :elements="mapedElementsList"
      @shapesAction="handleshapesAction"
      @openAssetGraphics="openAssetGraphics"
    ></spacebuttonpopup>
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
    <markeraction ref="marker-actions"></markeraction>
    <floorplan-tooltip
      v-if="tooltipPopup"
      :visibility.sync="tooltipPopup"
      :floorPlan="floorPlan"
      :element="selectedElement"
      :spaceList="spaceList"
      :area="selectedArea"
    ></floorplan-tooltip>
  </div>
</template>

<script>
import { fabric } from 'fabric'

import floorplanaction from 'pages/floorPlan/components/FloorPlanActionDialog'
import floorplansettings from 'pages/floorPlan/components/FloorPlanViewerSettings'
import leagends from 'pages/floorPlan/components/FloorPlanLeagend'
import viwerToolBar from 'pages/floorPlan/components/FloorPlanViwerTools'
import spacepopup from 'pages/floorPlan/components/SpaceDetailsPopup'
import pointerpop from 'pages/floorPlan/components/PointerPopup'
import elementpop from 'pages/floorPlan/components/Elementpop'
import spacebuttonpopup from 'pages/floorPlan/components/SpaceButtonPopup'
import markeraction from 'pages/floorPlan/components/MarkerActions/MarkerActions'
import floorplanTooltip from 'pages/floorPlan/components/floorplantooltip'
import SetReadingPopup from '@/readings/SetReadingValue'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import pointerdetails from 'pages/floorPlan/components/FloorPlanPointerDetailPopup'

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
  props: [
    'height',
    'width',
    'floorId',
    'hideHeader',
    'floorplanId',
    'data',
    'viewMode',
    'viewParams',
    'focus',
    'loadViewMode',
    'hideSettings',
  ],
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
    pointerpop,
    spacebuttonpopup,
    markeraction,
    floorplanTooltip,
    SetReadingPopup,
    FNewAnalyticReport,
    pointerdetails,
    elementpop,
  },
  computed: {
    fileId() {
      if (this.floorPlan) {
        return this.floorPlan.fileId
      }
      return null
    },
    areas() {
      if (this.data && this.data.areas) {
        return this.data.areas
      }
      return []
    },
  },
  data() {
    return {
      isLoading: false,
      pointerDetailsPopup: false,
      readonly: false,
      currentDragElement: null,
      selectedArea: null,
      selectedElement: null,
      refresh: false,
      floorMapVisible: false,
      floorActionVisible: false,
      mappedSpaceId: [],
      layerVisible: false,
      spaceDetailsPopup: false,
      pointerpopup: false,
      elementpopup: false,
      spaceButtonPopup: false,
      tooltipPopup: false,
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
  watch: {
    floorId: {
      handler: function(newVal, oldVal) {
        this.reInit()
      },
      deep: true,
    },
    focus: {
      handler: function(newVal, oldVal) {
        this.spaceFocus()
      },
      deep: true,
    },
  },
  methods: {
    initData() {
      this.isLoading = true
      Promise.all([
        this.fetchFloorDetails(),
        this.fetchFloorSpaces(),
        // this.getFloorControlCategory(),
        this.loadFloorSpaces(),
      ]).then(() => {
        this.fetchFloorPlan().then(() => {
          this.initCanvas()
        }),
          this.fetchListOfFloorPlans()
        this.fetchEmployeeDetails()
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
          that.areaProperties()
          that.fitToCenter()
          that.canvas.renderAll()
        },
        (el, element) => {
          this.canvas.backgroundColor = null
          this.applyValue(element)
          this.applyFilters(element)
          this.applyStyles(element)
          this.setUpViewerProps(element)
          this.setUpViwerObjectEvents(element)
          this.getListOfSpaceId(element)
        }
      )
      this.canvas.renderAll()
      this.$nextTick(() => {
        try {
          this.subscribeToLiveData()
        } catch (err) {
          console.log(err)
        }
        this.setupEditor()

        this.$nextTick(() => {
          this.registerNativeEvents()
          // this.setupViewerEvents()
          // let self = this
          //   setTimeout(function() {
          //     self.areaProperties()
          //   }, 2000)
          // this.panMode()
          // this.setupViewerEvents()
        })
      })
    },
    reInit() {
      let that = this
      //   this.canvas.forEachObject(element => {
      //       this.applyValue(element)
      //     this.applyFilters(element)
      //     this.applyStyles(element)
      //     this.setUpViewerProps(element)
      //     this.setUpViwerObjectEvents(element)
      //     this.getListOfSpaceId(element)
      // })
      if (this.canvas) {
        if (this.canvas.loadFromJSON) {
          this.canvas.loadFromJSON(
            this.floorPlan.canvas,
            () => {
              that.loadVariables()
              that.canvas.backgroundColor = null
              that.areaProperties()
              that.fitToCenter()
              // that.spaceFocus()
              that.canvas.renderAll()
            },
            (el, element) => {
              this.applyValue(element)
              this.applyFilters(element)
              this.applyStyles(element)
              this.setUpViewerProps(element)
              this.setUpViwerObjectEvents(element)
              this.getListOfSpaceId(element)
            }
          )
        }
        this.canvas.renderAll()
        this.$nextTick(() => {
          // try {
          //   this.subscribeToLiveData()
          // } catch (err) {
          //   console.log(err)
          // }
          this.setupEditor()

          this.$nextTick(() => {
            this.registerNativeEvents()
            // this.setupViewerEvents()
            // this.panMode()
            this.canvas.renderAll()
          })
        })
      }
    },
    setupEditor() {
      this.setFitDimensions()
      this.$nextTick(() => {
        this.setupViewerEvents()
        this.fitToResolution()
        this.$nextTick(() => {
          this.canvas.renderAll()
        })
      })
    },
    registerNativeEvents() {
      window.addEventListener('keydown', this.onKeyPress, {
        passive: true,
      })

      window.addEventListener('resize', this.onWindowResize, {
        passive: true,
      })
      if (this.$refs['floorPlanContainer']) {
        this.$refs['floorPlanContainer'].addEventListener('contextmenu', e => {
          if (e.button == 2) e.preventDefault()
        })
      }
    },
    cancel() {},
    save() {
      this.updateFloorPlan().then(({ data }) => {})
    },
    destroyFloorPlan() {
      this.unSubscribeToLiveData()
      this.canvas = null
    },
    closespaceDetailsPopup() {
      this.spaceDetailsPopup = false
      this.tooltipPopup = false
      this.spaceButtonPopup = false
      this.pointerpopup = false
      this.elementpopup = false
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
</style>
