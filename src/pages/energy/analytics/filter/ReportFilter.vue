<template>
  <el-dialog
    :visible.sync="visibility"
    width="65%"
    class="fc-dialog-center-container fc-dialog-center-body-p0 fc-dialog-right-hide"
    :append-to-body="true"
    :show-close="false"
    :before-close="close"
    :close-on-click-modal="false"
  >
    <el-tabs v-model="activeTab" class="fc-analytics-filter-tab">
      <el-tab-pane v-if="isTFEnable" label="Time Filter" name="timeFilter">
        <div class="p30">
          <time-filter :config.sync="tConfig"></time-filter>
        </div>
      </el-tab-pane>
      <!-- <el-tab-pane v-if="isDFEnable" label="Data Filter" name="dataFilter">
        <div class="p30">
          <data-filter :config.sync="dConfig"></data-filter>
        </div>
      </el-tab-pane> -->
      <!-- Remove this comment while implementing new data filter-->
      <el-tab-pane label="Data Filter" name="newDataFilter">
        <div class="fDF_class">
          <FDataFilter
            ref="f-data-filter"
            :excludeOperators="excludeOperators"
            :rulesArr="dConfig"
            :existingOptions="existingOptions"
            :canShowMatchConditions="false"
            :loadAssetsInitial="false"
            :withReadings="true"
          ></FDataFilter>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="close">CANCEL</el-button>
      <el-button type="primary" class="modal-btn-save" @click="save"
        >APPLY</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import TimeFilter from 'pages/energy/analytics/filter/TimeFilter'
// import DataFilter from 'pages/energy/analytics/filter/DataFilter'
import FDataFilter from '@/FDataFilter'
import {
  isEmpty,
  areValuesEmpty,
  isNullOrUndefined,
} from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'

export default {
  name: 'ReportFilter',
  components: {
    TimeFilter,
    // DataFilter,
    FDataFilter,
  },
  props: {
    visibility: Boolean,
    existingOptions: {
      type: Array,
      default: () => [],
    },
    config: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      alias: '',
      activeTab: 'timeFilter',
      tConfig: {
        type: '0',
        time: {
          //   0: [],
        }, //Time ranges
        days: [], // 7 Days
        months: [], // 12 Months
      },
      dConfig: [],
      excludeOperators: [74, 75, 76, 77, 78, 79, 35, 87, 81, 82],
      rulesArr: [],
    }
  },
  computed: {
    isTFEnable() {
      return this.config.hasOwnProperty('timeFilter')
    },
    isDFEnable() {
      return this.config.hasOwnProperty('dataFilter')
    },
  },
  mounted() {
    this.load()
  },
  methods: {
    load() {
      if (this.isTFEnable) {
        if (!areValuesEmpty(this.config.timeFilter.conditions)) {
          this.tConfig = this.getTimeFilterRenderObj(
            this.$helpers.cloneObject(this.config.timeFilter)
          )
        }
      }
      if (this.isDFEnable) {
        if (!areValuesEmpty(this.config.dataFilter.conditions)) {
          this.dConfig = this.getDataFilterRenderObj(
            this.$helpers.cloneObject(this.config.dataFilter)
          )
        }
      }
    },
    getAlias() {
      let alias = this.alias
      if (isEmpty(alias)) {
        alias = 'A'
      } else {
        alias = String.fromCharCode(alias.charCodeAt() + 1)
      }
      this.alias = alias
      return alias
    },
    getTimeFilter() {
      let conditions = {}
      let tConfig = this.tConfig
      this.alias = ''
      if (!areValuesEmpty(tConfig.days)) {
        let alias = this.getAlias()
        conditions[alias] = {
          field: 'ttime',
          operatorId: 85,
          value: tConfig.days,
        }
      }
      if (tConfig.time) {
        for (let interval in tConfig.time) {
          if (!isNullOrUndefined(tConfig.time[interval])) {
            let alias = this.getAlias()
            conditions[alias] = {
              field: 'ttime',
              operatorId: 86,
              value: tConfig.time[interval],
            }
          }
        }
      }
      return { conditions: conditions }
    },
    getDataFilter() {
      this.dConfig.conditions = this.$refs['f-data-filter'].saveRules()
      let conditions = {}
      this.alias = ''
      for (let condition of this.dConfig.conditions) {
        if (condition.operatorId !== null) {
          let fieldObj = condition.field.options[0]
            ? condition.field.options[0]
            : { label: null }
          let alias = this.getAlias()
          let con = {}
          con.parentId = condition.resourceId
          con.fieldId = condition.fieldId
          con.operatorId = condition.operatorId
          con.value = condition.value
          con.currentAsset = fieldObj.label == 'Current Asset'
          conditions[alias] = con
        }
      }
      return { conditions: conditions }
    },
    getFilter() {
      return {
        timeFilter: this.getTimeFilter(),
        dataFilter: this.getDataFilter(),
      }
    },
    getDataFilterRenderObj(dConfig) {
      let renderObj = this.dConfig
      for (let key in dConfig.conditions) {
        let condition = dConfig.conditions[key]
        let rule = {}
        rule.resourceId = condition.parentId
        rule.fieldId = condition.fieldId
        rule.operatorId = condition.operatorId
        rule.value = condition.value
        renderObj.push(rule)
      }
      return renderObj
    },
    getTimeFilterRenderObj(tConfig) {
      let renderObj = this.tConfig
      for (let key in tConfig.conditions) {
        let condition = tConfig.conditions[key]
        let operator = this.getOperatorObj(condition.operatorId)
        if (renderObj[operator]) {
          if (operator === 'time') {
            let intervals = renderObj[operator]
            if (Object.keys(intervals).length > 0) {
              let lastKey = Object.keys(intervals)
                .sort()
                .reverse()[0]
              intervals[lastKey + 1] = condition.value
            } else {
              renderObj[operator] = {}
              renderObj[operator][0] = condition.value
            }
          } else {
            renderObj[operator] = condition.value
          }
        }
      }
      if (!renderObj.days) {
        renderObj.days = []
      }
      renderObj.type = this.getTimeFilterType(renderObj)
      return renderObj
    },
    getTimeFilterType(renderObj) {
      let dayLen = renderObj.days.length
      let type = '0'
      if (dayLen > 0 && isEqual(renderObj.days, [6, 7])) {
        type = '1'
      } else if (dayLen > 0 && isEqual(renderObj.days, [1, 2, 3, 4, 5])) {
        type = '2'
      } else {
        type = '3'
      }
      return type
    },
    getOperatorObj(id) {
      switch (id) {
        case 85:
          return 'days'
        case 86:
          return 'time'
      }
    },
    save() {
      this.$emit('reportFilter', this.getFilter())
      this.close()
    },
    close() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style lang="scss">
.fc-analytics-filter-tab {
  .el-tabs__nav {
    padding: 10px 30px;
  }
  .el-tabs__active-bar {
    left: 30px;
  }
}
.fDF_class {
  padding: 0px 30px 30px 30px;
}
</style>
