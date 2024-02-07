<template>
  <div class="data-points-container">
    <div
      v-for="(datapoint, index) in dataPoints"
      class="data-point"
      :key="index"
    >
      <div
        @click="openEditDataPoint(index, datapoint)"
        title="Click to edit data point"
        v-tippy
      >
        {{ datapoint.name }}
      </div>
      <i
        @click="removeDataPoint(index)"
        class="el-icon-close data-point-remove"
      ></i>
    </div>
    <el-button
      @click="addDataPoint"
      v-show="dataPoints.length"
      class="add-data-point"
      size="small"
      icon="el-icon-plus"
    ></el-button>
    <el-button
      size="small"
      class="freport-btn"
      v-show="!dataPoints.length && showAddPoint"
      @click="selectDataPoint"
      >Select {{ displayName }}</el-button
    >
    <el-dialog
      width="300px"
      class="add-data-point-dialog"
      :title="dialogTitle"
      :visible.sync="addDataPointFormVisibility"
      custom-class="analystics-dialog"
    >
      <f-add-data-point
        :aggregate="aggregate"
        :reading="reading"
        :type="type"
        ref="addDataPointForm"
        @save="readingColumnAdded"
        @cancel="readingColumnClosed"
      ></f-add-data-point>
    </el-dialog>
  </div>
</template>

<script>
import FAddDataPoint from 'pages/energy/analytics/components/FAddDataPoint'
export default {
  props: ['aggregate', 'type', 'reading', 'showAddPoint'],
  components: {
    FAddDataPoint,
  },
  data() {
    return {
      addDataPointFormVisibility: false,
      dataPoints: [],
      dialogTitle: '',
      editIndex: -1,
    }
  },
  computed: {
    displayName() {
      if (this.type === 'meter') {
        return 'Meter'
      } else if (this.type === 'enpi') {
        return 'EnPI'
      }
      return 'Reading'
    },
  },
  methods: {
    setDataPoints(dataPoints) {
      this.dataPoints = dataPoints
    },
    selectDataPoint() {
      let self = this
      self.editIndex = -1
      self.dialogTitle = 'Select ' + this.displayName
      self.addDataPointFormVisibility = true
      this.$nextTick(function() {
        self.$refs.addDataPointForm.open()
      })
    },
    addDataPoint() {
      let self = this
      self.editIndex = -1
      self.dialogTitle = 'Add ' + this.displayName
      self.addDataPointFormVisibility = true
      this.$nextTick(function() {
        self.$refs.addDataPointForm.open()
      })
    },
    removeDataPoint(index) {
      this.dataPoints.splice(index, 1)
      this.$emit('change', this.dataPoints)
    },
    openEditDataPoint(index, dataPoint) {
      let self = this
      self.addDataPointFormVisibility = true
      self.dialogTitle = 'Edit ' + this.displayName
      self.editIndex = index
      this.$nextTick(function() {
        self.$refs.addDataPointForm.open(dataPoint)
      })
    },
    readingColumnAdded(dataPoint) {
      let readingReport = {
        buildingId: dataPoint.buildingId,
        readingFieldId: dataPoint.readingField.id,
        readingField: dataPoint.readingField,
        parentId: dataPoint.space ? dataPoint.space.id : dataPoint.asset.id,
        parent: dataPoint.space ? dataPoint.space : dataPoint.asset,
        parentType: dataPoint.space ? 'space' : 'asset',
        name: dataPoint.name,
        yAggr: dataPoint.aggregateFunc,
      }

      if (this.editIndex >= 0) {
        this.dataPoints.splice(this.editIndex, 1, readingReport)
      } else {
        this.dataPoints.push(readingReport)
      }
      this.$emit('change', this.dataPoints)
      this.addDataPointFormVisibility = false
    },
    readingColumnClosed() {
      this.addDataPointFormVisibility = false
    },
  },
}
</script>

<style>
.add-data-point,
.add-data-point:hover,
.add-data-point:focus {
  border-radius: 3px;
  background-color: #39b2c2;
  color: #fff;
  border: solid 1px #39b2c2;
  padding: 8px;
}
.add-data-point i {
  font-weight: bolder;
}
.data-points-container .data-point {
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #cff1f6;
  display: inline-flex;
  margin-right: 10px;
  padding: 7px;
  font-size: 13px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #31a4b4;
  max-width: 150px;
  cursor: pointer;
}
.data-points-container .data-point div {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
.add-data-point-dialog .el-dialog__body {
  padding: 0px;
}
.data-point-remove {
  cursor: pointer;
  line-height: 15px;
  position: relative;
  top: 1px;
}
.data-point-remove:hover {
  background: #f0f0f0;
}
/* .v-modal {
  display: none;
}
.data-points-container .el-dialog__wrapper {
    background: #00000099;
} */
</style>
