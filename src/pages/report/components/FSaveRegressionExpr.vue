<template>
  <el-popover
    width="250"
    trigger="click"
    placement=""
    :popper-class="'f-popover save-regression-popup'"
    v-model="visibility"
  >
    <div v-if="computedRegressionList.length === 0">
      No Expressions Available
    </div>
    <div v-else class="fc-popover-content">
      <slot name="head">
        <div
          class="title uppercase f12 bold"
          style="letter-spacing: 1.1px; color:#000000"
        >
          Save Expression
        </div>
      </slot>
      <el-select
        class="width100 fc-full-border-select-multiple2 pT10"
        filterable
        clearable
        v-model="choosenDataPoints"
        placeholder="Choose group"
      >
        <el-option
          v-for="(rPoint, rPointIdx) in computedRegressionList"
          :key="rPointIdx"
          :label="rPoint.label"
          :value="rPoint.key"
        ></el-option>
      </el-select>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()"
        >Close</el-button
      >
      <el-button
        type="primary"
        :disabled="computedRegressionList.length === 0"
        class="modal-btn-save"
        @click="saveAsEnpi()"
        >Save</el-button
      >
    </div>
  </el-popover>
</template>

<script>
import AnalyticsModelHelper from 'src/pages/report/mixins/FNewAnalyticsModelHelper'
export default {
  props: ['reportObject', 'visibility'],
  computed: {
    computedRegressionList() {
      let finalDataPoints = []
      if (typeof this.reportObject !== 'undefined' && this.reportObject) {
        let dataPoints = JSON.parse(
          JSON.stringify(this.reportObject.options.dataPoints)
        )
        for (let dp of dataPoints) {
          if (
            dp.type === 'group' &&
            dp.pointType &&
            dp.pointType === AnalyticsModelHelper.contextNames().REGRESSION
          ) {
            let regressionPoint = dp.children.filter(
              dataPoint => dataPoint.alias === dp.key
            )
            if (regressionPoint.length) {
              dp.label = regressionPoint[0].label
            }
            finalDataPoints.push(dp)
          }
        }
      }
      return finalDataPoints
    },
  },
  data() {
    return {
      choosenDataPoints: null,
    }
  },
  methods: {
    closeDialog() {
      this.choosenDataPoints = null
      this.$emit('update:visibility', false)
    },
    saveAsEnpi() {
      console.log('#####To save#####')
      console.log(this.choosenDataPoints)
      let enpiConfig = AnalyticsModelHelper.prepareNewEnpiObject(
        this.reportObject,
        [this.choosenDataPoints]
      )
      console.log(enpiConfig)
      this.$emit('newEnpiConfig', enpiConfig)
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style lang="scss">
.save-regression-popup {
  right: 31px;
}
</style>
