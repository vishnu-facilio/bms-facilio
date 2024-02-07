<template>
  <el-dialog
    :visible.sync="visibility"
    width="55%"
    :append-to-body="true"
    :before-close="closeDialog"
    class="fc-dialog-center-body-p0 fc-dialog-center-container fc-dialog-header-hide"
  >
    <div class="fc-dialog-header-custom p20">
      <div class="fc-black2-20 bold line-height24">
        {{ floor && floor.name ? floor.name : '' }}
      </div>
      <div class="fc-black-13 text-left pT10 text-uppercase bold">
        {{ space && space.name ? space.name : '' }}
      </div>
    </div>
    <div class="height500 position-relative">
      <el-row>
        <el-col :span="8">
          <div class="height500 overflow-y-scroll pB50 border-right1">
            <div
              :class="{ SmartListActive: selectedResource === null }"
              class="label-txt-label p20 flex-middle pointer"
              @click="getAvgPoint()"
            >
              <i class="fa fa-circle pR10 f6 active-txt" aria-hidden="true"></i
              >{{
                elementCategory && elementCategory.name
                  ? `${elementCategory.name} (${resourceLength})`
                  : ''
              }}
            </div>
            <div class="fc-grey2 f11 pL20 pR20 pB20 bold">
              {{
                elementCategory && elementCategory._name
                  ? `${elementCategory._name}`
                  : ''
              }}
            </div>
            <div class="">
              <div
                v-for="(asset, index) in elementResourceContext"
                :key="index"
                @click="getControllablepointlist(asset)"
                :class="{ SmartListActive: selectedResource == asset.resource }"
                class="label-txt-label pL20 pR20 pT15 pB15 flex-middle border-bottom15 fc-smart-list-side pointer"
              >
                <i
                  class="fa fa-circle pR10 f6 active-txt"
                  aria-hidden="true"
                ></i>
                {{ asset.resource.name }}
              </div>
            </div>
          </div>
        </el-col>
        <el-col
          :span="16"
          v-if="elementCategory && elementCategory._name === 'LIGHT'"
        >
          <div v-if="point">
            <div
              class="flex-middle justify-content-center flex-direction-column pT20 pB20"
            >
              <div class="fc-black2-18 pT35">
                {{
                  selectedResource && selectedResource.name
                    ? selectedResource.name
                    : elementCategory && elementCategory.name
                    ? `${elementCategory.name} (${resourceLength})`
                    : ''
                }}
              </div>
              <div class="pT35 fc-black-12 letter-spacing1">
                {{ point.name }}
              </div>

              <div class="fc-smart-control-input pT35">
                <el-switch
                  v-model="point.value"
                  :active-text="point.field.trueVal.toUpperCase()"
                  :inactive-text="point.field.falseVal.toUpperCase()"
                  :active-value="1"
                  :inactive-value="0"
                >
                </el-switch>
              </div>

              <div class="pT30 width65">
                <div
                  class=" fc-warning2-12 text-center"
                  v-if="point.value !== point.rawvalue"
                >
                  {{
                    `You switching ${
                      point.value > 0
                        ? point.field.trueVal
                        : point.field.falseVal
                    } ${elementResourceContext.length} lights`
                  }}
                </div>
              </div>
            </div>
            <div class="smart-temp-details">
              <div class="text-right bold fc-grey2 f24" v-if="selectedResource">
                <template v-if="point.rawvalue > 0">
                  {{ `${point.field.trueVal.toUpperCase()}` }}
                </template>
                <template v-else>
                  {{ `${point.field.falseVal.toUpperCase()}` }}
                </template>
                <span class="unit f16 bold">{{ unit }}</span>
              </div>
              <div class="text-right bold fc-grey2 f24" v-else>
                {{
                  `${point.field.trueVal.toUpperCase()}  ${
                    point.actualAvgValue
                  } / ${elementResourceContext.length}`
                }}
                <span class="unit f16 bold">{{ unit }}</span>
              </div>
              <div class="fc-grey2 f11 text-right pT5">
                ROOM LIGHT STATUS
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="16" v-else>
          <div v-if="point">
            <div
              class="flex-middle justify-content-center flex-direction-column pT20 pB20"
            >
              <div class="fc-black2-18 pT35">
                {{
                  selectedResource && selectedResource.name
                    ? selectedResource.name
                    : elementCategory && elementCategory.name
                    ? `${elementCategory.name} (${resourceLength})`
                    : ''
                }}
              </div>
              <div class="pT35 fc-black-12 letter-spacing1">
                {{ point.name }}
              </div>

              <div class="fc-smart-control-input pT35">
                <el-input
                  placeholder="celsius"
                  type="number"
                  v-model="point.value"
                  class=""
                  controls-position="right"
                >
                  <template slot="append">
                    <div class="unit f16 bold">{{ unit }}</div>
                  </template>
                </el-input>
              </div>

              <div class="pT30 width65">
                <div class="flex-middle">
                  <div class="pR20">
                    <el-button
                      @click="decrease"
                      circle
                      class="fc-btn-circle-grey"
                    >
                      <inline-svg
                        iconClass="icon icon-md"
                        src="svgs/minus-icon"
                      ></inline-svg>
                    </el-button>
                  </div>
                  <div
                    class="fc-smart-progress position-relative width100 mL10"
                  >
                    <el-slider
                      :min="point.minValue"
                      :max="point.maxValue"
                      @input="setInputValue(point.sliderValue)"
                      v-model="point.sliderValue"
                      class="fc-temperature-slider"
                    ></el-slider>
                    <!-- <div class="fc-smart-adjust"></div> -->
                  </div>
                  <div class="pL10">
                    <el-button
                      @click="increase"
                      circle
                      class="fc-btn-circle-grey"
                    >
                      <inline-svg
                        iconClass="icon icon-md"
                        src="svgs/plus-icon"
                      ></inline-svg>
                    </el-button>
                  </div>
                </div>
                <div
                  class="pT20 fc-warning2-12 text-center"
                  v-if="point.value - point.rawvalue !== 0"
                >
                  You’ve changed the value by
                  {{ formatValue(point.value - point.rawvalue) }}
                  <span class="unit f12 ">{{ ` ${unit}` }}</span>
                </div>
              </div>
            </div>
            <div class="smart-temp-details">
              <div
                class="text-right bold fc-grey2 f24"
                v-if="
                  point.rawvalueRangeFrom &&
                    point.rawvalueRangeTo &&
                    point.rawvalueRangeFrom !== point.rawvalueRangeTo
                "
              >
                {{
                  `${formatValue(
                    point.rawvalueRangeFrom
                  )} ${unit} ~ ${formatValue(point.rawvalueRangeTo)} ${unit} `
                }}
                <!-- <span class="unit f16 bold">{{ unit }}</span> -->
              </div>
              <div
                class="text-right bold fc-grey2 f24"
                v-else-if="
                  point.rawvalueRangeFrom &&
                    point.rawvalueRangeTo &&
                    point.rawvalueRangeFrom === point.rawvalueRangeTo
                "
              >
                {{ `${formatValue(point.actualAvgValue)} ${unit} ` }}
                <!-- <span class="unit f16 bold">{{ unit }}</span> -->
              </div>
              <div class="text-right bold fc-grey2 f24" v-else>
                {{ formatValue(point.childRDM.value) }}
                <span class="unit f16 bold">{{ unit }}</span>
              </div>
              <div class="fc-grey2 f11 text-right pT5">
                ROOM TEMPERATURE
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">
        {{ $t('setup.users_management.cancel') }}</el-button
      >
      <el-button
        type="primary"
        class="modal-btn-save"
        v-if="selectedResource"
        @click="setValue(point)"
      >
        Set Value
      </el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        v-else
        @click="setSpaceValue(point)"
      >
        Set Value
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { formatDecimal } from '../elements/Common'
export default {
  props: [
    'floorPlan',
    'visibility',
    'element',
    'controlCategoryList',
    'spaceControllableCategoriesMap',
    'spaceList',
    'selectedArea',
  ],
  data() {
    return {
      sliderRerender: false,
      controllableCategories: [],
      elementResourceContext: null,
      controllablepointlist: [],
      point: null,
      selectedResource: null,
    }
  },
  watch: {
    // 'point.sliderValue': {
    //   handler: function(newVal, oldVal) {
    //     this.point.value = this.point.sliderValue
    //   },
    //   deep: true,
    // },
    // 'point.value': {
    //   handler: function(newVal, oldVal) {
    //     this.point.sliderValue = Number(this.point.value)
    //   },
    //   deep: true,
    // },
  },
  computed: {
    unit() {
      let { point } = this
      if (point && point.field && point.field.unit) {
        return point.field.unit
      }
      return ''
    },
    resourceLength() {
      if (this.elementResourceContext && this.elementResourceContext.length) {
        return this.elementResourceContext.length
      }
      return 0
    },
    spaceId() {
      let { target } = this.element
      if (target.floorplan.spaceId) {
        return target.floorplan.spaceId
      }
      return null
    },
    elementCategory() {
      let { target } = this.element
      if (target) {
        let { floorplan } = target
        if (floorplan.markerType) {
          return this.controlCategoryList.find(
            rt => rt._name === floorplan.markerType
          )
        }
      }
      return null
    },
    floorId() {
      if (!isEmpty(this.floorPlan) && this.floorPlan.floorId) {
        return Number(this.floorPlan.floorId)
      }
      return null
    },
    elementCategoryId() {
      if (this.elementCategory && this.elementCategory.categoryId) {
        return this.elementCategory.categoryId
      }
      return null
    },
    elementCategoryPoints() {
      if (this.elementCategory && this.elementCategory.points) {
        return this.elementCategory.points
      }
      return []
    },
    space() {
      if (!isEmpty(this.spaceList)) {
        return this.spaceList.find(rt => rt.id === this.spaceId)
      }
      return null
    },
    floor() {
      if (!isEmpty(this.space)) {
        return this.space.floor
      }
      return null
    },
  },
  mounted() {
    if (this.selectedArea && this.selectedArea.markers) {
      this.controllableCategories = this.selectedArea.markers
    } else {
      this.controllableCategories = this.spaceControllableCategoriesMap[
        this.spaceId
      ]
    }
    this.getResourceData()
    this.getAvgPoint()
  },
  methods: {
    increase() {
      this.point.value += 1
    },
    decrease() {
      this.point.value -= 1
    },
    setInputValue(value) {
      if (this.point.minValue < value) {
        this.point.value = value
      }
    },
    setValue(point) {
      this.setAssetValue(point)
    },
    getAvgPoint() {
      this.selectedResource = null
      let pointList = this.getControllablePointsList()
      this.point = this.getPointFromPointList(pointList)
    },
    getPointFromPointList(pointList) {
      let point = null
      let unitEnum = null
      let avgValue = 0
      let actualAvgValue = 0
      let val = 0
      let valArray = []
      let pointname = ''
      let pointId = null
      let rawvalueRangeFrom =
        pointList[0] && pointList[0].childRDM
          ? this.getChildRDMValue(pointList[0])
          : 0
      let rawvalueRangeTo =
        pointList[0] && pointList[0].childRDM
          ? this.getChildRDMValue(pointList[0])
          : 0
      let minValue =
        pointList[0] && pointList[0].minValue ? pointList[0].minValue : 0
      let maxValue =
        pointList[0] && pointList[0].maxValue ? pointList[0].maxValue : 0
      let datatype = null
      if (pointList) {
        pointList.forEach(rt => {
          datatype = rt.field.dataType
          pointId = rt.pointId
          pointname = rt.name
          unitEnum = rt.field
          if (datatype === 4) {
            if (Number(rt.value) > 0) {
              actualAvgValue++
            }
            valArray.push(Number(rt.value))
          } else {
            avgValue = Number(rt.value) + avgValue
            actualAvgValue = Number(this.getChildRDMValue(rt)) + actualAvgValue
            if (minValue >= rt.minValue) {
              minValue = rt.minValue
            }
            if (maxValue <= rt.maxValue) {
              maxValue = rt.maxValue
            }
            if (rawvalueRangeFrom <= this.getChildRDMValue(rt)) {
              rawvalueRangeFrom = this.getChildRDMValue(rt)
            } else if (rawvalueRangeTo >= this.getChildRDMValue(rt)) {
              rawvalueRangeTo = this.getChildRDMValue(rt)
            }
          }
        })
        if (unitEnum.dataType === 4) {
          avgValue = valArray.length ? Math.min(...valArray) : 0
          point = {
            field: unitEnum,
            pointId: pointId,
            name: pointname,
            maxValue: maxValue,
            minValue: minValue,
            value: avgValue,
            rawvalue: this.formatValue(Number(avgValue)),
            actualAvgValue: this.formatValue(Number(actualAvgValue)),
            rawvalueRangeFrom: rawvalueRangeFrom,
            rawvalueRangeTo: rawvalueRangeTo,
            sliderValue: this.formatValue(Number(avgValue)),
          }
        } else {
          avgValue = avgValue / pointList.length
          actualAvgValue = actualAvgValue / pointList.length
          point = {
            field: unitEnum,
            pointId: pointId,
            name: pointname,
            maxValue: maxValue,
            minValue: minValue,
            value: this.formatValue(Number(avgValue)),
            rawvalue: this.formatValue(Number(avgValue)),
            actualAvgValue: this.formatValue(Number(actualAvgValue)),
            rawvalueRangeFrom: rawvalueRangeFrom,
            rawvalueRangeTo: rawvalueRangeTo,
            sliderValue: this.formatValue(Number(avgValue)),
          }
        }
      }
      return point
    },
    getChildRDMValue(point) {
      return point.childRDM && point.childRDM.value !== null
        ? point.childRDM.value
        : ''
    },
    emitEvents(name) {
      this.$emit(name)
    },
    setAssetValue(point) {
      let { fieldId, resourceId, value } = point
      this.$http
        .get(
          `/v2/controlAction/setReadingValue?resourceId=${resourceId}&value=${value}&fieldId=${fieldId}`
        )
        .then(({ data }) => {
          if (data.responseCode === 0) {
            // this.getspaceControlCategory(this.spaceId)
          }
        })
      this.$message({
        message: `${point.name} updated`,
        type: 'success',
      })
      this.emitEvents('setValue')
      this.closeDialog()
    },
    setSpaceValue() {
      let { value, pointId, name } = this.point
      let { elementCategoryId, spaceId } = this
      this.$http
        .get(
          `/v2/controlAction/setReadingValueForSpace?spaceId=${spaceId}&value=${value}&controllablePoint=${pointId}&controllableCategory=${elementCategoryId}`
        )
        .then(({ data }) => {
          if (data.responseCode === 0) {
            // this.getspaceControlCategory(this.spaceId)
          }
        })
      this.$message({
        message: `${name} updated`,
        type: 'success',
      })
      this.emitEvents('setValue')
      this.closeDialog()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    getResourceData() {
      if (this.elementCategory) {
        this.elementResourceContext = this.markerFilteredResorces()
      }
    },
    markerFilteredResorces() {
      let { elementCategory } = this
      return Object.values(this.controllableCategories)
        .map(rt => {
          if (
            rt.controlTypeEnum &&
            rt.controlTypeEnum._name === elementCategory._name
          ) {
            return rt.controllableResourceContexts
          }
        })
        .flat()
        .filter(rt => rt !== undefined)
    },
    formatValue(value) {
      return formatDecimal(value)
    },
    getControllablePointsList() {
      let self = this
      let point = null
      let pointList = []
      this.elementResourceContext.forEach(asset => {
        if (asset.controllablePointMap) {
          let { controllablePointMap } = asset
          Object.keys(controllablePointMap).forEach(pointId => {
            point = {}
            if (pointId) {
              let pointMetaData = self.elementCategoryPoints.find(
                rt => rt.pointId === Number(pointId)
              )
              let pointdata = controllablePointMap[pointId]
              if (pointMetaData) {
                point = {
                  ...pointdata,
                  ...pointMetaData,
                }
                self.controllablepointlist.push(point)
              }
            }
          })

          if (point) {
            point.value = this.formatValue(Number(point.value))
            point.rawvalue = this.formatValue(Number(point.value))
            point.sliderValue = this.formatValue(Number(point.value))
            pointList.push(point)
          }
          if (point.value === 0) {
            point.minValue = 0
          }
        }
      })
      return pointList
    },
    getControllablepointlist(asset) {
      this.selectedResource = null
      let self = this
      this.selectedResource = asset.resource
      this.controllablepointlist = []
      if (asset.controllablePointMap) {
        let { controllablePointMap } = asset
        Object.keys(controllablePointMap).forEach(pointId => {
          if (pointId) {
            let pointMetaData = self.elementCategoryPoints.find(
              rt => rt.pointId === Number(pointId)
            )
            let pointdata = controllablePointMap[pointId]
            if (pointMetaData) {
              self.controllablepointlist.push({
                ...pointdata,
                ...pointMetaData,
              })
            }
          }
        })
        self.point =
          self.controllablepointlist && self.controllablepointlist.length
            ? self.controllablepointlist[0]
            : null

        if (self.point.value === 0) {
          self.point.minValue = 0
        }
        if (self.point) {
          self.point.value = this.formatValue(Number(self.point.value))
          self.point.rawvalue = this.formatValue(Number(self.point.value))
          self.point.sliderValue = this.formatValue(Number(self.point.value))
        }
      }
    },
  },
}
</script>
<style>
.fc-smart-progress .el-slider__button-wrapper .el-tooltip {
  padding: 11px;
  border-radius: 50%;
  top: 6px;
  position: absolute;
  left: 0;
  box-shadow: 0 2px 4px 0 rgba(147, 158, 164, 0.3);
  border: solid 0.5px #c8d1d6;
  background-color: #ffffff;
}
</style>
