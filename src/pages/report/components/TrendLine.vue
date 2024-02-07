<template>
  <el-dialog
    :title="algorithmType ? getTitle() : 'TrendLine'"
    :visible.sync="visibility"
    width="40%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :show-close="false"
    :before-close="closeTrendLine"
    :close-on-click-modal="false"
  >
    <div class="height350 overflow-y-scroll pB80">
      <div class="customize-input-block">
        <el-row>
          <el-col :span="12" class="mR20" v-if="!algorithmType">
            <div class="label-txt-black">Type</div>
            <el-select
              v-model="trendLineObj.type"
              placeholder="Select"
              class="fc-input-full-border2 width100 mT10"
            >
              <el-option label="Linear" value="1"></el-option>
              <el-option label="Polynomial" value="2"></el-option>
            </el-select>
          </el-col>
          <el-col :span="12" v-if="trendLineObj.type == 2">
            <div class="label-txt-black">Degree</div>
            <el-select
              v-model="trendLineObj.degree"
              placeholder="Select"
              class="fc-input-full-border2 width100 mT10"
            >
              <el-option
                v-for="n in 9"
                :key="n"
                :label="n + 1"
                :value="n + 1"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
      <div class="customize-input-block pT20">
        <el-row>
          <el-col :span="12" class="mR20">
            <div class="label-txt-black">Decimals</div>
            <el-input
              v-model.number="trendLineObj.decimal"
              class="fc-input-full-border2 width100 mT10"
            ></el-input>
          </el-col>
          <el-col :span="10" class="pT40 pL20">
            <el-checkbox
              v-model="trendLineObj.showr2"
              label="Show R²"
              title="Show R²"
              v-tippy
            ></el-checkbox>
          </el-col>
        </el-row>
      </div>
      <div class="customize-input-block pT20">
        <el-row>
          <el-col :span="24" class="mR20">
            <div class="label-txt-black">Data Points</div>
            <el-select
              v-model="trendLineObj.selectedPoints"
              placeholder="All"
              multiple
              filterable
              collapse-tags
              class="fc-input-full-border2 width100 mT10 fc-tag"
            >
              <el-option
                v-for="(datapoint, index) in dataPoints.filter(
                  dp => dp.axes !== 'x'
                )"
                :key="index"
                :label="datapoint.label"
                :title="datapoint.label"
                v-tippy
                :value="datapoint.alias"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeTrendLine"
          >CANCEL</el-button
        >
        <el-button type="primary" class="modal-btn-save" @click="saveTrendLine"
          >SAVE</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['visibility', 'report', 'algorithmType'],
  created() {
    if (typeof this.report !== 'undefined' && this.report !== null) {
      this.load()
    }
  },
  data() {
    return {
      trendLineObj: null,
      dataPoints: [],
    }
  },
  methods: {
    load() {
      this.trendLineObj = this.$helpers.cloneObject(
        this.report.options.trendLine
      )
      this.dataPoints = this.$helpers.getDataPoints(
        this.report.options.dataPoints,
        [1, 2],
        false
      )
      let selectedPoints = []
      this.dataPoints.forEach(dp => {
        if (this.trendLineObj.selectedPoints.includes(dp.alias)) {
          selectedPoints.push(dp.alias)
        }
      })
      this.trendLineObj.selectedPoints = selectedPoints
      if (this.algorithmType) {
        this.trendLineObj.type = this.algorithmType
      }
    },
    saveTrendLine() {
      this.$emit('onTrendLineChange', this.trendLineObj)
      this.closeTrendLine()
    },
    closeTrendLine() {
      this.$emit('update:visibility', false)
    },
    getTitle() {
      switch (this.algorithmType) {
        case '1':
          return 'LINEAR ALGORITHM'
        case '2':
          return 'POLYNOMIAL ALGORITHM'
        default:
          return 'TRENDLINE'
      }
    },
  },
}
</script>
