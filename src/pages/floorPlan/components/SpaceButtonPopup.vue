<template>
  <div
    class="fplan-space-info-card"
    v-bind:class="{ maxwidth500: $org.id === 410 }"
    v-if="selectedSpaceInfo && selectedSpaceInfo.space"
    :style="{
      top: selectedSpaceInfo.top + 'px',
      left: selectedSpaceInfo.left + 'px',
    }"
  >
    <template v-if="$org.id === 343">
      <div class="position-relative">
        <div @click="close()" class="space-popver-colse-container">
          <i class="el-icon-close pointer"></i>
        </div>
        <el-row>
          <el-col :span="24" class="space-title ele-title">
            {{ selectedSpaceInfo.name }}
          </el-col>
          <template v-if="$org.id === 343">
            <el-col
              :span="24"
              class="ele-name"
              v-for="(assetId, index) in Object.keys(assetDetailsList)"
              :key="index"
            >
              <el-row v-if="assetDetailsList[assetId].name">
                <el-col :span="10" class="pop-value">
                  <div>{{ assetDetailsList[assetId].name }}</div>
                </el-col>
                <el-col :span="4" class="pop-button-value">
                  {{
                    getformattedvalue(
                      assetDetailsList[assetId].value,
                      assetDetailsList[assetId].unit
                    )
                  }}
                </el-col>
                <el-col :span="9" class="open-graphics">
                  <div
                    @click="
                      OpenPopUp(assetId, assetDetailsList[assetId].categoryId)
                    "
                  >
                    Open Graphics
                  </div></el-col
                >
              </el-row>
            </el-col>
          </template>
          <template v-else-if="$org.id === 410">
            <el-table :data="tableData" border style="width: 100%">
              <el-table-column
                :prop="column.prop"
                :label="column.label"
                :width="column.width || '180'"
                v-for="(column, index) in coloumns"
                :key="index"
              >
              </el-table-column>
            </el-table>
          </template>
          <template v-else>
            <el-col
              :span="24"
              class="ele-name"
              v-for="(asset, index) in tooltipData"
              :key="index"
            >
              <el-row v-if="asset.name">
                <el-col :span="10" class="pop-value">
                  <div>{{ asset.name }}</div>
                </el-col>
                <el-col :span="4" class="pop-button-value pull-right pL5">
                  {{ getformattedvalue(asset.value, asset.unit) }}
                </el-col>
              </el-row>
            </el-col>
          </template>
        </el-row>
      </div>
    </template>
    <template v-else-if="$org.id === 410 && tableData.length">
      <div class="position-relative floorplan-dialog">
        <div @click="close()" class="space-popver-colse-container">
          <i class="el-icon-close pointer"></i>
        </div>
        <el-row>
          <el-col :span="24" class="space-title1 ele-title">
            {{ selectedSpaceInfo.name }}
          </el-col>
          <template>
            <el-table
              :data="tableData"
              border
              style="width: 100%"
              header-row-class-name="floor-plan-dialog"
              header-cell-class-name="floor-plan-dialog-header"
            >
              <el-table-column
                :prop="column.prop"
                :label="column.label"
                :width="column.width || '180'"
                v-for="(column, index) in coloumns"
                :key="index"
              >
              </el-table-column>
            </el-table>
          </template>
        </el-row>
      </div>
    </template>
  </div>
</template>

<script>
import CardHelpers from 'pages/card-builder/card-helpers'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'spaceList',
    'visibility',
    'element',
    'floorPlan',
    'area',
    'elements',
  ],
  mixins: [CardHelpers],
  data() {
    return {
      selectedSpaceInfo: null,
    }
  },
  computed: {
    elementlist() {
      if (this.elements && this.elements.length) {
        return this.elements.map(rt => rt.floorplan)
      }
      return []
    },
    selectedArea() {
      if (this.selectedSpaceInfo) {
        let selectedArea = this.area.find(
          rt => rt.spaceId === this.selectedSpaceInfo.id
        )
        if (selectedArea) {
          return selectedArea
        }
      }
      return null
    },
    assetDetailsList() {
      if (this.selectedSpaceInfo) {
        let selectedArea = this.area.find(
          rt => rt.spaceId === this.selectedSpaceInfo.id
        )
        if (selectedArea && selectedArea.assets) {
          return selectedArea.assets[0]
        }
      }
      return {}
    },
    tooltipData() {
      if (this.selectedSpaceInfo) {
        let selectedArea = this.area.find(
          rt => rt.spaceId === this.selectedSpaceInfo.id
        )
        if (selectedArea && selectedArea.spaceTooltip) {
          return selectedArea.spaceTooltip
        }
      }
      return []
    },
    tableData() {
      if (this.selectedArea && this.selectedArea.tableData) {
        return this.selectedArea.tableData
      }
      return []
    },
    coloumns() {
      if (this.selectedArea && this.selectedArea.tableColumnName) {
        return this.selectedArea.tableColumnName
      }
      return []
    },
  },
  mounted() {
    this.initData()
  },
  watch: {
    'element.target': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.initData()
        }
      },
      deep: true,
    },
  },
  methods: {
    getValue(element) {
      if (element && this.area) {
        let selectedArea = this.area.find(rt => rt.spaceId === element.spaceId)
        if (selectedArea.assets && element.assetId) {
          let asset = selectedArea.assets[0][element.assetId]
          if (asset) {
            return `${this.formatDecimal(asset.value)} ${asset.unit}`
          }
        }
      }
      return ''
    },
    getClientHeight() {
      let ele = document.getElementsByClassName('canvas-container')
      if (ele && ele.length) {
        return ele[0].clientHeight
      }
    },
    closePopUp() {
      this.$emit('update:visibility', false)
    },
    initData() {
      if (window.orgId === 410) {
        this.$nextTick(() => {
          if (this.tableData && this.tableData.length === 0) {
            this.closePopUp()
          }
        })
      }
      if (
        this.element &&
        this.element.target &&
        this.element.target.floorplan &&
        this.element.target.floorplan.spaceId
      ) {
        let event = this.element.e
        let { floorplan } = this.element.target
        let { spaceId } = floorplan
        let spaceObj = this.spaceList.find(rt => rt.id === spaceId)
        let EVENTX = event.offsetX
        let EVENTY = event.offsetY
        if (event.offsetX + 500 > window.screen.availWidth) {
          EVENTX = EVENTX - 550
        } else if (event.offsetX < 500) {
          EVENTX = EVENTX + 10
        }
        if (EVENTY > this.getClientHeight() / 2) {
          EVENTY = EVENTY - 150
        }
        let metaObj = {
          top: EVENTY,
          left: EVENTX,
        }
        this.selectedSpaceInfo = { ...spaceObj, ...metaObj }
      } else {
        this.selectedSpaceInfo = null
      }
    },
    close() {
      this.$emit('close')
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    openSpace(space) {
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/site/${this.floorPlan.siteId}/space/${space.id}`,
        })
      }
      this.selectedSpaceInfo = null
    },
    shapesAction(d) {
      let elemnt = null
      if (d && d.id) {
        elemnt = this.elements.find(rt => rt.floorplan.id === d.id)
      }
      let data = {
        target: elemnt,
      }
      this.$emit('shapesAction', data)
    },
    OpenPopUp(assetId, categoryId) {
      this.$emit('openAssetGraphics', assetId, categoryId)
    },
    getformattedvalue(value, unit) {
      return `${this.formatDecimal(value)} ${unit}`
    },
  },
}
</script>

<style>
.floor-plan-dialog-header {
  background: #efefef;
}
.maxwidth500 {
  max-width: 530px !important;
}
.space-popver-colse-container {
  top: 5px;
  position: absolute;
  right: 6px;
  cursor: pointer;
  z-index: 10;
}
.ele-title {
  padding: 10px !important;
  border-bottom: 1px solid #f4f4f4;
  text-align: left;
}
.ele-name {
  padding: 10px;
  cursor: pointer;
  text-align: left;
  font-size: 12px;
  padding: 15px;
}
.ele-name:hover {
  background: #f7f8f9;
}
.pop-value {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 1px;
  color: #324056;
}
.pop-button-value {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
.floor-plan-dialog .cell {
  padding-left: 10px;
  font-size: 10px;
}
.floor-plan-dialog {
  padding-left: 0px !important;
}
.floorplan-dialog .el-table th {
  background: #efefef !important;
}
.floorplan-dialog .el-table__row .cell {
  font-size: 12px;
}
.floorplan-dialog .el-table .cell,
.floorplan-dialog .el-table--border td:first-child .cell,
.floorplan-dialog .el-table--border th:first-child .cell {
  padding-left: 0px;
}
</style>
