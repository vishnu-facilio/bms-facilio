<template>
  <el-dialog
    title="CHART OPTIONS"
    :visible.sync="visibility"
    width="40%"
    class="fc-dialog-center-container fc-dialog-center-body-p0"
    :append-to-body="true"
    :show-close="false"
    :before-close="close"
    :close-on-click-modal="false"
  >
    <div class="height380 overflow-y-scroll pB80">
      <div class="customize-input-block pR20 pL20 pT20">
        <el-row>
          <el-col :span="16">
            <div class="fc-grey7-12 f14 text-left line-height25">Shape</div>
            <el-select
              v-model="report.options.scatter.shape"
              placeholder="Select"
              class="fc-input-full-border2 width100"
              @change="OnShapeChange()"
            >
              <el-option label="Circle" value="circle"></el-option>
              <el-option label="Rectangle" value="rectangle"></el-option>
              <el-option label="Triangle" value="triangle"></el-option>
              <el-option label="Diamond" value="diamond"></el-option>
              <el-option
                label="Varies based on asset"
                value="predefined"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>

      <el-row class="pR20 pL20 pT20" :gutter="20">
        <el-col :span="12">
          <div class="customize-input-block">
            <div class="fc-grey7-12 f14 text-left line-height25">
              Color
            </div>
            <el-row>
              <el-col :span="20">
                <el-select
                  v-model="report.options.scatter.color.mode"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                >
                  <el-option label="None" :value="null"></el-option>
                  <el-option label="Monochrome" value="monochrome"></el-option>
                  <el-option
                    v-if="metricPoints.length < 2"
                    label="Color palette"
                    value="palette"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </el-col>
        <el-col :span="12" v-if="report.options.scatter.color.mode">
          <div class="customize-input-block">
            <div class="fc-grey7-12 f14 text-left line-height25">
              Color based on
            </div>
            <el-row>
              <el-col :span="20">
                <el-select
                  v-model="colorBasedOn"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                  filterable
                  :loading="loading"
                  remote
                  :remote-method="remoteMethod"
                  @change="constructDataPoint('color')"
                >
                  <el-option
                    v-for="(reading, idx) in readings"
                    :key="idx"
                    :label="reading.displayName"
                    :value="reading.fieldId"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
      <el-row class="pR20 pL20 pT20">
        <el-col
          v-if="report.options.scatter.color.mode === 'palette'"
          :span="24"
        >
          <f-color-palettes
            :colorIndex="report.options.scatter.color.pallete"
            @colorSelected="onColorChange"
          ></f-color-palettes>
        </el-col>
      </el-row>
      <el-row class="pR20 pL20 pT20" :gutter="20" v-if="sizeEnabled">
        <el-col :span="12">
          <div class="customize-input-block">
            <div class="fc-grey7-12 f14 text-left line-height25">
              Size
            </div>

            <el-row>
              <el-col :span="20">
                <el-select
                  v-model="report.options.scatter.size.mode"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                >
                  <el-option label="None" :value="null"></el-option>
                  <el-option
                    label="Varies based on Readings"
                    value="reading"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </el-col>
        <el-col :span="12" v-if="report.options.scatter.size.mode">
          <div class="customize-input-block">
            <div class="fc-grey7-12 f14 text-left line-height25">
              Size based on
            </div>
            <el-row>
              <el-col :span="20">
                <el-select
                  v-model="sizeBasedOn"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                  filterable
                  :loading="loading"
                  remote
                  :remote-method="remoteMethod"
                  @change="constructDataPoint('size')"
                >
                  <el-option
                    v-for="(reading, idx) in readings"
                    :key="idx"
                    :label="reading.displayName"
                    :value="reading.fieldId"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="close">CANCEL</el-button>
      <el-button type="primary" class="modal-btn-save" @click="save"
        >DONE</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import FColorPalettes from 'newcharts/components/FColorPalettes.vue'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  components: { FColorPalettes },
  props: ['visibility', 'report', 'config'],
  mixins: [AnalyticsMixin],
  data() {
    return {
      activeScatterChartItem: null,
      colorBasedOn: null,
      sizeBasedOn: null,
      readings: [],
      loading: false,
      sizeEnabled: true,
    }
  },
  computed: {
    dataPointObject() {
      return this.report
        ? this.$helpers.getDataPoints(
            this.report.options.dataPoints,
            [1, 2, 4],
            true
          )
        : []
    },
    allPoints() {
      let points = []
      if (this.dataPointObject) {
        this.dataPointObject.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      return points.filter(
        dp => dp.axes !== 'x' && !dp.isBaseLine && dp.pointCustomization
      )
    },
    metricPoints() {
      let points = []
      if (this.dataPointObject) {
        this.dataPointObject.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      return points.filter(
        dp =>
          dp.axes !== 'x' &&
          !dp.isBaseLine &&
          dp.pointCustomization &&
          dp.visible
      )
    },
    xDataPoints() {
      let points = []
      if (this.dataPointObject) {
        this.dataPointObject.forEach(dp => {
          if (dp.type === 'group') {
            points.push(...dp.children)
          } else {
            points.push(dp)
          }
        })
      }
      return points.filter(dp => dp.axes === 'x')
    },
  },
  mounted() {
    if (this.report) {
      if (this.report.options.scatter.color.reading) {
        this.colorBasedOn = this.report.options.scatter.color.reading
      }
      if (this.report.options.scatter.size.reading) {
        this.sizeBasedOn = this.report.options.scatter.size.reading
      }
    }
    this.loading = true
    let url = 'asset/getAssetsWithReadings'
    let params = {
      fetchOnlyReadings: true,
      page: 1,
      perPage: 50,
    }
    if (this.config && this.config.selectedAssetCategory)
      params.categoryIds = [this.config.selectedAssetCategory]
    API.post(url, params).then(({ data, error }) => {
      if (!error) {
        this.readings = Object.values(data.fields || {})
        this.loading = false
      }
    })
  },
  methods: {
    save() {
      this.close()
    },
    close() {
      this.$emit('update:visibility', false)
    },
    prefix(point) {
      if (point.duplicateDataPoint) {
        if (
          this.report.params.xAggr !== 0 ||
          (this.report &&
            ![1, 4, 'reading'].includes(this.report.options.common.mode))
        ) {
          switch (point.aggr) {
            case 2:
              return 'Avg of '
            case 3:
              return 'Sum of '
            case 4:
              return 'Min of '
            case 5:
              return 'Max of '
            default:
              return ''
          }
        }
      }
      return ''
    },
    onColorChange(idx, colorArr) {
      if (this.report && this.report.options.scatter.color) {
        this.report.options.scatter.color.pallete = idx
        this.report.options.scatter.color.colors = colorArr
      }
    },
    OnShapeChange() {
      if (this.report && this.report.options.scatter.shape) {
        let val = this.report.options.scatter.shape
        if (val !== 'circle') {
          this.sizeEnabled = false
          this.report.options.scatter.size.mode = null
        }
        if (val !== 'predefined') {
          this.allPoints.forEach(dp => (dp.pointCustomization.shape = val))
        } else {
          let shapeArr = ['circle', 'rectangle', 'triangle', 'diamond']
          this.allPoints.forEach(
            (dp, idx) => (dp.pointCustomization.shape = shapeArr[idx % 4])
          )
        }
      }
    },
    remoteMethod(query) {
      if (query != '') {
        this.loading = true
        let url = 'asset/getAssetsWithReadings'
        let params = {
          fetchOnlyReadings: true,
          page: 1,
          perPage: 50,
        }
        if (this.config && this.config.selectedAssetCategory)
          params.categoryIds = [this.config.selectedAssetCategory]
        if (!isEmpty(query)) params.search = query
        API.post(url, params).then(({ data, error }) => {
          if (!error) {
            this.readings = Object.values(data.fields || {})
            this.loading = false
          }
        })
      }
    },
    constructDataPoint(mode) {
      let { colorBasedOn, sizeBasedOn, allPoints, xDataPoints } = this
      let changePoint = false
      let changedDataPoints = this.config.dataPoints
        .slice()
        .filter(dp => dp.type !== 6)
      for (let dp of allPoints) {
        if (mode === 'color') {
          this.report.options.scatter.color.reading = colorBasedOn
          if (parseInt(dp.fieldId) === parseInt(colorBasedOn)) {
            dp.pointCustomization.color.basedOn = `${dp.key}`
          } else {
            let match = allPoints.find(
              dataPoint =>
                parseInt(dp.parentId) === parseInt(dataPoint.parentId) &&
                parseInt(dp.fieldId) === parseInt(colorBasedOn)
            )
            if (match) {
              dp.pointCustomization.color.basedOn = `${match.key}`
            } else {
              let match = xDataPoints.find(
                dataPoint =>
                  parseInt(dp.parentId) === parseInt(dataPoint.parentId) &&
                  parseInt(dataPoint.fieldId) === parseInt(colorBasedOn)
              )
              if (match) {
                dp.pointCustomization.color.basedOn = `${match.key}`
              } else {
                let aliases = {
                  actual: `${dp.key}_colorscatter`,
                }
                let point = {
                  parentId: dp.parentId,
                  prediction: false,
                  aliases: aliases,
                  yAxis: {
                    fieldId: colorBasedOn,
                    aggr: 3,
                  },
                  type: 6,
                }
                dp.pointCustomization.color.basedOn = `${dp.key}_colorscatter`
                changedDataPoints = changedDataPoints.filter(
                  point => point.aliases.actual !== `${dp.key}_colorscatter`
                )
                changedDataPoints.push(point)
                changePoint = true
              }
            }
          }
        } else if (mode === 'size') {
          this.report.options.scatter.size.reading = sizeBasedOn
          if (parseInt(dp.fieldId) === parseInt(sizeBasedOn)) {
            dp.pointCustomization.size.basedOn = `${dp.key}`
          } else {
            let match = allPoints.find(
              dataPoint =>
                parseInt(dp.parentId) === parseInt(dataPoint.parentId) &&
                parseInt(dp.fieldId) === parseInt(sizeBasedOn)
            )
            if (match) {
              dp.pointCustomization.size.basedOn = `${match.key}`
            } else {
              let match = xDataPoints.find(
                dataPoint =>
                  parseInt(dp.parentId) === parseInt(dataPoint.parentId) &&
                  parseInt(dataPoint.fieldId) === parseInt(sizeBasedOn)
              )
              if (match) {
                dp.pointCustomization.size.basedOn = `${match.key}`
              } else {
                let aliases = {
                  actual: `${dp.key}_sizescatter`,
                }
                let point = {
                  parentId: dp.parentId,
                  prediction: false,
                  aliases: aliases,
                  yAxis: {
                    fieldId: sizeBasedOn,
                    aggr: 3,
                  },
                  type: 6,
                }
                dp.pointCustomization.size.basedOn = `${dp.key}_sizescatter`
                changedDataPoints = changedDataPoints.filter(
                  point => point.aliases.actual !== `${dp.key}_sizescatter`
                )
                changedDataPoints.push(point)
                changePoint = true
              }
            }
          }
        }
      }
      if (changePoint) {
        this.updateDataPoints(changedDataPoints)
      }
    },
    updateDataPoints(changedDataPoints) {
      if (this.config.dataPoints.length) {
        let configIndexes = []
        let alreadyAddedIndexes = []
        let pointsToRemove = []

        this.enableReadingSelection =
          changedDataPoints.length > 1 ? true : false

        let isValid = this.allPoints
          .filter(dp => dp.pointType !== 2 && !dp.duplicateDataPoint)
          .every(dp => {
            let idx = changedDataPoints.findIndex(
              cdp =>
                parseInt(cdp.parentId) === parseInt(dp.parentId) &&
                parseInt(cdp.yAxis.fieldId) === parseInt(dp.fieldId)
            )
            if (idx === -1) {
              pointsToRemove.push(dp.alias)
            }
            if (idx !== -1) {
              let configIdx = this.getConfigDataPointFromOptionDP(
                this.config,
                dp,
                true
              )
              configIndexes.push(configIdx)
              alreadyAddedIndexes.push(idx)
            }
            return true
          })
        if (isValid) {
          if (pointsToRemove.length) {
            this.removeOptionPoints(this.report.options, pointsToRemove)
          }
          this.config.dataPoints = [
            ...this.config.dataPoints.filter(
              (dp, idx) =>
                dp.type === 2 ||
                dp.duplicateDataPoint ||
                configIndexes.includes(idx)
            ),
            ...changedDataPoints.filter(
              (cdp, idx) => !alreadyAddedIndexes.includes(idx)
            ),
          ]
        }
      } else {
        this.config.dataPoints = changedDataPoints
      }
    },
  },
}
</script>
