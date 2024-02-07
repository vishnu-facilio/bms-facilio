<template>
  <div class="formbuilder-fullscreen-popup floor-plan-builder height100">
    <div>
      <!-- <notification ref="notification" class="notification"></notification> -->
      <div class="setting-header2 position-relative fp-header">
        <div class="setting-title-block">
          <div class="setting-form-title mT10">
            Floor Plan Builder
            <span v-show="isLoading" class="saving-container">Saving ...</span>
          </div>
        </div>
        <div class="action-btn setting-page-btn">
          <el-button
            type="secondary"
            class="fc-btn-green-medium-border shadow-none"
            @click="cancel"
            >Close</el-button
          >
          <el-button
            type="primary"
            class="fc-btn-green-medium-fill shadow-none"
            @click="save"
            >Save</el-button
          >
        </div>
      </div>
      <toolbar
        :leagend="leagend"
        class="fp-tool-bar width100"
        @action="handleAction"
      ></toolbar>
      <div class="floor-plan-conatiner row">
        <!-- <sidebar
          class="col-2"
          :floorPlan="floorPlan"
          @dragging="handleDrag"
        ></sidebar> -->
        <graphics-element
          class="col-2"
          @dragging="handleDrag"
          :floorplan="floorPlan"
        ></graphics-element>
        <div
          class="floor-plan-builder-container col-10 p10"
          ref="floorPlanContainer"
        >
          <canvas id="floorplan-canvas" ref="floorPlan"></canvas>
        </div>
      </div>
    </div>
    <floor-plan-space-mapper
      v-if="floorMapVisible"
      :visibility.sync="floorMapVisible"
      :floorPlan="floorPlan"
      :element="selectedElement"
      @save="bindSpace"
      :mappedSpaceId="getListOfMappedSpaceId()"
    ></floor-plan-space-mapper>
    <!-- <floor-plan-layer
      v-if="layerVisible"
      :visibility.sync="layerVisible"
      :floorPlan="floorPlan"
      @layerInfo="handleLayerInfo"
      :controlCategoryList="controlCategoryList"
    >
    </floor-plan-layer> -->
    <floorplanaction
      v-if="floorActionVisible"
      :visibility.sync="floorActionVisible"
      :floorPlan="floorPlan"
      :element="selectedElement"
      :controlCategoryList="controlCategoryList"
    >
    </floorplanaction>
    <object-settings
      v-if="objectSettingVisible"
      :visibility.sync="objectSettingVisible"
      :object="selectedObject"
      @updateObject="updateObject"
    >
    </object-settings>
    <floor-plan-properties
      v-if="propertiesvisible"
      :variables="variables"
      :visibility.sync="propertiesvisible"
      @updateObject="updateObjectProps"
      :currentObject="selectedObject"
      :spaceList="spaceList"
    >
    </floor-plan-properties>
    <FloorPlanPointerDialog
      v-if="pointervisible"
      :variables="variables"
      :visibility.sync="pointervisible"
      @updateObject="updateObjectProps"
      :currentObject="selectedObject"
      :spaceList="spaceList"
    >
    </FloorPlanPointerDialog>
    <floor-plan-asset-chooser v-if="devloper" :leagend="leagend">
    </floor-plan-asset-chooser>
    <div
      class="fp-contextmenu fp-actions"
      ref="floorplanContextMenu"
      v-if="contextmenu"
      :style="{ top: contextmenu.top + 'px', left: contextmenu.left + 'px' }"
    >
      <ul class="graphics-actions-menu">
        <li
          class="graphics-actions-menu-item"
          :class="{ 'graphics-actions-menu-section': menu.section }"
          @mouseover="menu.showSubMenu = true"
          @mouseout="menu.showSubMenu = false"
          v-for="(menu, index) in contextmenu.menu"
          :key="index"
          @click="handleRightClickAction(menu, contextmenu)"
        >
          {{ menu.label }}
          <template v-if="menu.submenu && menu.submenu.length">
            <i class="el-icon-arrow-right fR"></i>
            <ul
              class="graphics-actions-submenu"
              :style="
                contextmenu.width ? 'left: ' + contextmenu.width + 'px' : ''
              "
              v-if="menu.showSubMenu"
            >
              <li
                v-for="(sm, j) in menu.submenu"
                :key="j"
                @click="handleRightClickAction(menu, contextmenu, sm.value)"
              >
                {{ sm.label }}
              </li>
            </ul>
          </template>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { fabric } from 'fabric'
import { isEmpty } from '@facilio/utils/validation'

import Notification from 'pages/floorPlan/components/FloorplanBuilderNotification'
import graphicsElement from 'pages/floorPlan/components/FloorGraphicsElements'
import toolbar from 'pages/floorPlan/components/FloorPlanToolbar'
import floorPlanSpaceMapper from 'pages/floorPlan/components/FloorPlanSpaceMapper'

// import floorPlanLayer from 'pages/floorPlan/components/FloorPlanLayer'
import floorplanaction from 'pages/floorPlan/components/FloorPlanActionDialog'
import objectSettings from 'pages/floorPlan/components/FloorPlanObjectSettings'
import FloorPlanProperties from 'pages/floorPlan/components/FloorPlanPropertiesDialog'
import FloorPlanPointerDialog from 'pages/floorPlan/components/FloorPlanPointerDialog'
import floorPlanAssetChooser from 'pages/floorPlan/components/FloorPlanAssetChooser'

import dataMixin from 'pages/floorPlan/mixins/FloorPlanDataMixin'
import canvasSetup from 'pages/floorPlan/mixins/CanvasSetupMixin'
import floorAction from 'pages/floorPlan/mixins/FloorPlanActionMixin'
import drawElementsMixin from 'pages/floorPlan/mixins/DrawElementsMixin'
import FGraphicUtil from 'pages/assets/graphics/FGraphicUtil'
import FloorPlanUtil from 'pages/floorPlan/Utils/FloorPlanUtil'
import filterMixin from 'pages/floorPlan/mixins/FloorPlanFiltersMixin'
import floorplanvalue from 'pages/floorPlan/mixins/FloorPlanValueMixin'
import variableMixin from 'pages/floorPlan/mixins/FloorPlanVariableMixin'

export default {
  props: ['floorId', 'floorplanId'],
  mixins: [
    dataMixin,
    canvasSetup,
    floorAction,
    FGraphicUtil,
    Notification,
    drawElementsMixin,
    filterMixin,
    floorplanvalue,
    variableMixin,
  ],
  components: {
    toolbar,
    floorPlanSpaceMapper,
    floorplanaction,
    graphicsElement,
    objectSettings,
    FloorPlanProperties,
    FloorPlanPointerDialog,
    floorPlanAssetChooser,
  },
  data() {
    return {
      width: null,
      height: null,
      isEdit: true,
      contextmenu: null,
      selectedObject: null,
      graphicUtil: null,
      isLoading: false,
      readonly: false,
      currentDragElement: null,
      selectedElement: null,
      floorMapVisible: false,
      floorActionVisible: false,
      objectSettingVisible: false,
      propertiesvisible: false,
      pointervisible: false,
      layerVisible: false,
      layer: 'default',
      sublayer: 'control',
      variables: [],
      liveValues: {},
      usedVariables: [],
      polygonOptions: {
        polygonMode: false,
        pointArray: new Array(),
        lineArray: new Array(),
        activeLine: null,
        activeShape: false,
      },
      config: {},
    }
  },
  computed: {
    fileId() {
      if (this.floorPlan) {
        return this.floorPlan.fileId
      }
      return null
    },
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
        })
      })
    },
    initCanvas() {
      this.isLoading = false

      this.canvas = new fabric.Canvas(this.$refs['floorPlan'], {
        selection: true,
        preserveObjectStacking: true,
      })
      this.graphicUtil = new FloorPlanUtil.Floorplan(
        this.canvas,
        this.readonly,
        this.graphics
      )
      let that = this
      if (this.floorPlanId && this.floorPlan.canvas) {
        this.canvas.loadFromJSON(
          this.floorPlan.canvas,
          () => {
            that.loadVariables()
            that.canvas.renderAll()
          },
          (el, element) => {
            this.applyValue(element)
            // this.applyFilters(element)
            this.setUpEditorProps(element)
            this.setupCommonObjProps(element)
            this.setUpObjectEvents(element)
          }
        )
        this.canvas.renderAll()
      }
      if (this.floorPlan.id) {
        this.$nextTick(() => {
          this.subscribeToLiveData()
          this.setupEditor()

          this.$nextTick(() => this.registerNativeEvents())
        })
      }
    },
    setupEditor() {
      this.setDimensions()
      this.$nextTick(() => {
        this.setBg().then(() => {
          this.$nextTick(() => {
            if (!this.floorPlan.canvas) {
              this.setupBackground()
            }
            this.$nextTick(() => {
              this.setUpEvents()
            })
          })
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
      window.onclick = options => {
        if (
          options &&
          options.srcElement.className.indexOf('fp-contextmenu') === -1
        ) {
          this.contextmenu = null
        }
      }

      window.addEventListener('keyup', this.keyupHandler)

      this.$refs['floorPlanContainer'].addEventListener('contextmenu', e => {
        if (e.button == 2) {
          this.handleMouseRightClickEvent(e)
          e.preventDefault()
        }
      })
    },
    cancel() {
      this.$emit('close')
    },
    save() {
      let layout = {
        width: this.canvas.getWidth(),
        height: this.canvas.getHeight(),
      }
      this.$set(this.leagend, 'layout', layout)
      this.updateFloorPlan().then(() => {
        this.showSuccess('Floor plan saved')
        this.$emit('close')
      })
    },
    destroyFloorPlan() {
      this.unSubscribeToLiveData()

      this.canvas = null
    },
  },
}
</script>

<style lang="scss" scoped></style>
