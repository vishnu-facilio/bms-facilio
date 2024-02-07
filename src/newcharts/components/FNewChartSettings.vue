<template>
  <el-popover v-model="popoverVisibility" :width="250" popper-class="pT0 pB0">
    <outside-click
      class="pT20 pB20"
      @onOutsideClick="popoverVisibility = false"
      :visibility="popoverVisibility"
    >
      <div class="pL10 pB20" v-if="showChartMode !== false">
        <div class="label-txt-black pb5">Mode</div>
        <div class="">
          <el-radio-group
            :disabled="disableChartMode"
            v-model="settings.chartMode"
            @change="onChartModeChange"
            class="fc-radio-btn chart-setting-group pT10"
          >
            <el-radio label="single">Single</el-radio>
            <el-radio label="multi">Multiple</el-radio>
          </el-radio-group>
        </div>
      </div>

      <div class="pL10 pB20" v-if="showAlarm !== false">
        <div class="label-txt-black pb5">Alarm</div>
        <div>
          <el-radio-group
            v-model="settings.alarm"
            @change="onSettingsChange('alarm')"
            class="chart-setting-group fc-radio-btn pT10"
          >
            <el-radio :label="true">Show</el-radio>
            <el-radio :label="false">{{
              $t('fields.properties.hideFieldTxt')
            }}</el-radio>
          </el-radio-group>
        </div>
      </div>

      <div class="pL10 pB20" v-if="showTimePeriod !== false">
        <div class="label-txt-black pb5">Time period</div>
        <div>
          <el-radio-group
            v-model="settings.timeperiod"
            class="chart-setting-group fc-radio-btn pT10"
          >
            <el-radio :label="true">Show</el-radio>
            <el-radio :label="false">{{
              $t('fields.properties.hideFieldTxt')
            }}</el-radio>
          </el-radio-group>
        </div>
      </div>

      <div class="pL10 pB20" v-if="showFilterBar">
        <div class="label-txt-black pb5">Filter</div>
        <div>
          <el-radio-group
            v-model="settings.filterBar"
            @change="onSettingsChange('filter')"
            class="chart-setting-group fc-radio-btn pT10"
          >
            <el-radio :label="true">Show</el-radio>
            <el-radio :label="false">{{
              $t('fields.properties.hideFieldTxt')
            }}</el-radio>
          </el-radio-group>
        </div>
      </div>
    </outside-click>

    <div
      slot="reference"
      title="Report Settings"
      class="mR30 pT5 chart-icon pointer flRight portfolio-equiv-icon pB8"
      data-arrow="true"
      v-tippy
    >
      <span @click.stop="popoverVisibility = true">
        <InlineSvg
          src="svgs/equalization"
          iconClass="icon icon-sm-md"
          class="rotate90 fc-grey-svg2"
          style="top: 2px;"
        ></InlineSvg>
      </span>
    </div>
  </el-popover>
</template>
<script>
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import InlineSvg from '@/InlineSvg'
import OutsideClick from '@/OutsideClick'
export default {
  props: [
    'dataPoints',
    'settings',
    'showChartMode',
    'showAlarm',
    'showChart',
    'showTable',
    'showSafelimit',
    'showTimePeriod',
    'disableChartMode',
    'showFilterBar',
  ],
  data() {
    return {
      popoverVisibility: false,
    }
  },
  mixins: [NewDataFormatHelper],
  components: {
    InlineSvg,
    OutsideClick,
  },
  methods: {
    onChartModeChange() {
      if (this.settings.chartMode === 'multi') {
        let unitCount = this.countUnits(this.dataPoints)
        if (Object.keys(unitCount).length > 1) {
          this.settings.autoGroup = true
          this.autoGroupDataPoints(unitCount)
        } else {
          this.settings.autoGroup = false
        }
      } else {
        this.settings.autoGroup = false
        this.autoGroupDataPoints()
      }
    },

    onToggleChart() {
      if (!this.settings.chart) {
        this.settings.table = true
      }
    },

    onToggleTable() {
      if (!this.settings.table) {
        this.settings.chart = true
      }
    },
    autoGroupDataPoints(unitCount) {
      if (this.settings.autoGroup) {
        this.groupDataPoints(unitCount, this.dataPoints)
      } else {
        // remove groups enabled by autogrouping
        for (
          let groupIndex = this.dataPoints.length - 1;
          groupIndex >= 0;
          groupIndex--
        ) {
          if (this.dataPoints[groupIndex].type === 'group') {
            if (
              this.dataPoints[groupIndex].unit &&
              this.dataPoints[groupIndex].dataType
            ) {
              continue
            } else {
              let group = this.dataPoints.splice(groupIndex, 1)
              let children = group[0].children
              for (
                let childIndex = children.length - 1;
                childIndex >= 0;
                childIndex--
              ) {
                let removedChild = children.splice(childIndex, 1)
                this.dataPoints.push(removedChild[0])
              }
            }
          } else {
            continue
          }
        }
      }
    },
    groupDataPoints(unitCount, dataPoints) {
      this.dataPoints = this.groupPoints(unitCount, dataPoints)
    },
    onSettingsChange(option) {
      this.$emit('onSettingsChange', option)
    },
  },
}
</script>
<style>
.table-only-settings {
  position: absolute;
  right: 11px;
  top: 54px;
}
.chart-setting-group .el-radio-button__inner {
  padding: 6px 9px;
  margin-top: 5px;
}
.portfolio-equiv-icon {
  position: absolute;
  right: 0;
  top: 19px;
}
</style>
