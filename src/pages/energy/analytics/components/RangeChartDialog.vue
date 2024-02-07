<template>
  <el-dialog
    :visible.sync="visibility"
    title="Range chart"
    width="40%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :show-close="false"
    @keydown.esc="closeRangeDialog"
    :close-on-click-modal="false"
  >
    <div class="height400 pB80">
      <el-button @click="addNewRangeTemplate()" type="primary"
        >New range point</el-button
      >
      <div v-if="showNewRangeTemplate">
        <div>
          <el-row>
            <el-col :span="24">
              Avg:
              <el-select
                clearable
                @clear="clearPoint('avg')"
                @change="setPointMeta($event, 'avg')"
                v-model="rangeTemplate['avg']['clientId']"
              >
                <el-option
                  v-if="dataPointAvg.show === true"
                  v-for="(dataPointAvg, dataPointAvgIdx) in filteredDataPoints"
                  :key="dataPointAvgIdx"
                  :label="dataPointAvg.name"
                  :value="dataPointAvg.clientId"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              Min:
              <el-select
                clearable
                @clear="clearPoint('min')"
                :disabled="typeof rangeTemplate.avg.alias === 'undefined'"
                @change="setPointMeta($event, 'min')"
                v-model="rangeTemplate['min']['clientId']"
              >
                <el-option
                  v-if="dataPointMin.show === true"
                  v-for="(dataPointMin, dataPointMinIdx) in filteredDataPoints"
                  :key="dataPointMinIdx"
                  :label="dataPointMin.name"
                  :value="dataPointMin.clientId"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              Max:
              <el-select
                clearable
                @clear="clearPoint('max')"
                :disabled="typeof rangeTemplate.avg.alias === 'undefined'"
                @change="setPointMeta($event, 'max')"
                v-model="rangeTemplate['max']['clientId']"
              >
                <el-option
                  v-if="dataPointMax.show === true"
                  v-for="(dataPointMax, dataPointMaxIdx) in filteredDataPoints"
                  :key="dataPointMaxIdx"
                  :label="dataPointMax.name"
                  :value="dataPointMax.clientId"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row>
            <el-button type="primary" @click="insertRangePoint()">
              Add
            </el-button>
          </el-row>
        </div>
      </div>

      <div v-if="allRanges && allRanges.length !== 0">
        <div v-for="(allRange, allRangeIdx) in allRanges" :key="allRangeIdx">
          <div>
            <div>
              <i class="el-icon-edit" @click="editRangeConfig(allRange)"></i>
              <i
                class="el-icon-delete"
                @click="deleteRangeConfig(allRangeIdx)"
              ></i>
            </div>
            <div>{{ 'Avg:' + allRange.avg.displayName }}</div>
            <div>{{ 'Min:' + allRange.min.displayName }}</div>
            <div>{{ 'Max:' + allRange.max.displayName }}</div>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeRangeDialog"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="addAllRangePoints"
          >SAVE</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['visibility', 'dataPoints', 'chartState'],
  data() {
    return {
      showNewRangeTemplate: false,
      rangeTemplate: null,
      allRanges: null,
      usedPoints: [],
      lastClientId: 0,
    }
  },
  watch: {
    dataPoints: {
      handler: function(newVal, oldVal) {
        if (newVal) {
          this.filterDataPoints()
        }
      },
      deep: true,
    },
  },
  computed: {
    filteredDataPoints() {
      if (this.dataPoints.length !== 0) {
        return this.filterDataPoints()
      }
      return []
    },
  },
  methods: {
    filterDataPoints() {
      let points = []
      points = [...this.dataPoints.filter(dp => dp.yAxis.dataType !== 4)]
      points = this.reducePoints(points)

      if (this.usedPoints && this.usedPoints.length !== 0) {
        for (let uP of this.usedPoints) {
          let inDp = points.filter(
            dp =>
              dp.metaData.parentIds &&
              dp.metaData.parentIds[0] === uP.parentId &&
              dp.yAxis &&
              dp.yAxis.fieldId === uP.fieldId
          )
          if (inDp.length !== 0) {
            inDp[0]['clientId'] = uP.clientId
          } else {
            this.deleteUsedPoint(uP)
          }
        }
      }

      this.setBooleanFilters(points)
      this.setClientIds(points)
      return points
    },
    setBooleanFilters(points) {
      if (this.usedPoints && this.usedPoints.length !== 0) {
        for (let p of points) {
          let upFilter = this.usedPoints.filter(
            uP =>
              uP.fieldId === p.yAxis.fieldId &&
              uP.parentId === p.metaData.parentIds[0]
          )
          if (upFilter.length === 0) {
            p['show'] = true
          } else {
            p['show'] = false
          }
        }
      } else {
        for (let d of points) {
          d['show'] = true
        }
      }
    },
    reducePoints(points) {
      // Object with parent and filedId key
      let uniqueKeys = {}
      let dpByAlias = {}
      let finalDataPoints = []
      for (let dp of points) {
        dpByAlias[dp.aliases.actual] = JSON.parse(JSON.stringify(dp))
        if (dp.metaData.parentIds && dp.yAxis.fieldId) {
          if (
            !uniqueKeys.hasOwnProperty(
              dp.metaData.parentIds[0] + '.' + dp.yAxis.fieldId
            )
          ) {
            uniqueKeys[dp.metaData.parentIds[0] + '.' + dp.yAxis.fieldId] = []
            uniqueKeys[dp.metaData.parentIds[0] + '.' + dp.yAxis.fieldId].push(
              dp.aliases.actual
            )
          } else {
            uniqueKeys[dp.metaData.parentIds[0] + '.' + dp.yAxis.fieldId].push(
              dp.aliases.actual
            )
          }
        }
      }

      for (let key in uniqueKeys) {
        let dataPoint = dpByAlias[uniqueKeys[key][0]]
        if (uniqueKeys[key].length > 1) {
          // reducing to a single point

          dataPoint.aliases.actual = dataPoint.aliases.actual.split('.')[0]
          finalDataPoints.push(dataPoint)
        } else {
          finalDataPoints.push(dpByAlias[uniqueKeys[key][0]])
        }
      }
      return finalDataPoints
    },
    deleteUsedPoint(point) {
      let uPointIndex = this.usedPoints.indexOf(point)
      this.usedPoints.splice(uPointIndex, 1)
      if (this.allRanges) {
        let index = []
        for (let range of this.allRanges) {
          for (let key in range) {
            if (
              range[key].parentId === point.parentId &&
              range[key].fieldId === point.fieldId
            ) {
              index.push(this.allRanges.indexOf(range))
            }
          }
        }

        for (let i of index) {
          this.allRanges.splice(i, 1)
        }
      }
    },
    setClientIds(dataPoints) {
      for (let p of dataPoints) {
        if (!p.clientId) {
          p['clientId'] = this.lastClientId
        }
        this.lastClientId++
      }
    },
    clearPoint(label) {
      this.rangeTemplate[label] = {}
    },
    insertRangePoint() {
      this.showNewRangeTemplate = false
      if (!this.allRanges) {
        this.allRanges = []
      }

      let uniqueStrings = new Set()
      for (let key in this.rangeTemplate) {
        if (key === 'min' || key === 'avg' || key === 'max') {
          uniqueStrings.add(
            this.rangeTemplate[key].parentId +
              '.' +
              this.rangeTemplate[key].fieldId +
              '.' +
              this.rangeTemplate[key].clientId
          )
        }
      }

      for (let string of uniqueStrings) {
        let split = string.split('.')
        this.usedPoints.push({
          parentId: parseInt(split[0]),
          fieldId: parseInt(split[1]),
          clientId: parseInt(split[2]),
        })
      }
      this.hideDataPoints()
      this.rangeTemplate['index'] =
        this.allRanges.length === 0
          ? this.allRanges.length
          : this.allRanges.length - 1

      this.allRanges.push(this.rangeTemplate)
      this.rangeTemplate = null
    },
    hideDataPoints(uniqueStrings) {
      for (let dP of this.usedPoints) {
        let mDataPoint = this.dataPoints.filter(
          mDp =>
            mDp.yAxis.fieldId === dP.fieldId &&
            mDp.metaData.parentIds[0] === dP.parentId
        )
        if (mDataPoint.length !== 0) {
          mDataPoint['show'] = false
        }
      }
    },
    deleteRangeConfig(rangeConfigIdx) {
      let dataPointKeys = new Set()
      for (let key in this.allRanges[rangeConfigIdx]) {
        if (key === 'min' || key === 'max' || key === 'avg') {
          dataPointKeys.add(
            this.allRanges[rangeConfigIdx][key].parentId +
              '.' +
              this.allRanges[rangeConfigIdx][key].fieldId
          )
        }
      }

      for (let uKey of dataPointKeys.values()) {
        let split = uKey.split('.')

        let usedPoint = this.usedPoints.filter(
          uP =>
            uP.fieldId === parseInt(split[1]) &&
            uP.parentId === parseInt(split[0])
        )
        if (usedPoint.length !== 0) {
          let index = this.usedPoints.indexOf(usedPoint[0])
          this.usedPoints.splice(index, 1)
        }
      }

      if (this.rangeTemplate && this.rangeTemplate.index === rangeConfigIdx) {
        this.rangeTemplate = null
      }
      this.allRanges.splice(rangeConfigIdx, 1)
    },
    editRangeConfig(rangeConfig) {
      this.rangeTemplate = rangeConfig

      this.showNewRangeTemplate = true
    },
    setPointMeta(event, label, dp) {
      console.log(event)
      console.log(label)
      console.log(dp)
      let dataPoint = this.filteredDataPoints.filter(
        point => point.clientId === parseInt(event)
      )
      if (dataPoint.length !== 0) {
        let alias = dataPoint[0].aliases.actual
        if (label === 'avg') {
          if (
            Object.keys(this.rangeTemplate.min).length === 0 &&
            Object.keys(this.rangeTemplate.max).length === 0
          ) {
            this.$set(this.rangeTemplate.min, 'clientId', event)
            this.$set(this.rangeTemplate.max, 'clientId', event)
            this.rangeTemplate.min['fieldId'] = dataPoint[0].yAxis.fieldId
            this.rangeTemplate.max['fieldId'] = dataPoint[0].yAxis.fieldId
            this.rangeTemplate.min['displayName'] = dataPoint[0].name
            this.rangeTemplate.max['displayName'] = dataPoint[0].name
            this.rangeTemplate.min['alias'] = alias + '.min'
            this.rangeTemplate.max['alias'] = alias + '.max'
            this.rangeTemplate.min['aggr'] = 4
            this.rangeTemplate.max['aggr'] = 5
            this.rangeTemplate.min['parentId'] =
              dataPoint[0].metaData.parentIds[0]
            this.rangeTemplate.max['parentId'] =
              dataPoint[0].metaData.parentIds[0]
          }
          this.rangeTemplate.avg['fieldId'] = dataPoint[0].yAxis.fieldId
          this.rangeTemplate.avg['parentId'] =
            dataPoint[0].metaData.parentIds[0]
          this.rangeTemplate.avg['aggr'] = 2
          this.rangeTemplate.avg['alias'] = alias + '.avg'
        } else if (label === 'min') {
          this.$set(this.rangeTemplate.min, 'clientId', event)
          this.$set(
            this.rangeTemplate.min,
            'fieldId',
            dataPoint[0].yAxis.fieldId
          )
          let centrePointAlias = this.rangeTemplate.avg.alias.split('.')
          this.rangeTemplate.min['alias'] = centrePointAlias[0] + '.min'
          this.rangeTemplate.min['fieldId'] = dataPoint[0].yAxis.fieldId
          this.rangeTemplate.min['aggr'] = 4
        } else {
          this.$set(this.rangeTemplate.max, 'clientId', event)
          this.$set(
            this.rangeTemplate.max,
            'fieldId',
            dataPoint[0].yAxis.fieldId
          )
          let centrePointAlias = this.rangeTemplate.avg.alias.split('.')
          this.rangeTemplate.max['alias'] = centrePointAlias[0] + '.max'
          this.rangeTemplate.max['fieldId'] = dataPoint[0].yAxis.fieldId
          this.rangeTemplate.max['aggr'] = 5
        }
        this.rangeTemplate[label].displayName = dataPoint[0].name
        this.rangeTemplate[label].parentId = dataPoint[0].metaData.parentIds[0]
      }
    },
    addNewRangeTemplate() {
      let rangeTemplate = {
        min: {},
        max: {},
        avg: {},
      }
      this.rangeTemplate = rangeTemplate
      this.showNewRangeTemplate = true
    },
    closeRangeDialog() {
      this.showNewRangeTemplate = false
      this.rangeTemplate = null
      this.$emit('update:visibility', false)
    },
    addAllRangePoints() {
      console.log('Adding all range points')
      let allDataPoints = [...this.filteredDataPoints]
      for (let usedPoint of this.usedPoints) {
        let point = allDataPoints.filter(
          f =>
            f.metaData.parentIds[0] + '' + f.yAxis.fieldId ===
            usedPoint.parentId + '' + usedPoint.fieldId
        )
        if (point.length !== 0) {
          allDataPoints.splice(allDataPoints.indexOf(point[0]), 1)
        }
      }

      this.$emit('rangePoints', this.allRanges, allDataPoints)
      this.closeRangeDialog()
    },
  },
}
</script>

<style></style>
