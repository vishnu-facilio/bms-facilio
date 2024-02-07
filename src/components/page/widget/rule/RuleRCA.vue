<template>
  <div v-if="isNewRuleEnabled()">
    <NewRuleRCA :ruleId="ruleId" :moduleName="moduleName"></NewRuleRCA>
  </div>
  <div v-else>
    <div v-if="$validation.isEmpty(alarmRCA) || alarmRCA.length > 0">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="pT20 fc-black-dark f18 bold">No Root Cause Analysis!</div>
        <div class="fc-grayish pT10">
          No root cause associated with this rule.
        </div>
      </div>
    </div>
    <div v-else>
      <portal :to="tab.name + '-title-section'">
        <div
          class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
        >
          <div class="widget-header-name">Root cause analysis</div>
        </div>
      </portal>
      <div
        class="mB10 position-relative "
        v-if="!$validation.isEmpty(alarmRCA)"
      >
        <el-table
          :data="rcaObject"
          :fit="true"
          :height="auto"
          class="root-cause-list-table root-cause-height"
          style="width: 100%;"
        >
          <el-table-column type="index" width="100"> </el-table-column>
          <el-table-column prop="name" label="NAME">
            <template v-slot="rca">
              <div
                class="d-flex main-field-column label-txt-black"
                @click="openRcaAlarm(rca.row)"
              >
                {{ rca.row.alarmRule.alarmTriggerRule.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="DESCRIPTION">
            <template v-slot="rca">
              <div class="label-txt-black">
                {{
                  $getProperty(
                    rca,
                    'row.alarmRule.alarmTriggerRule.description'
                  ) || '---'
                }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="severity" label="SEVERITY">
            <template v-slot="rca">
              <div class="label-txt-black">
                {{ getSeverity(rca.row) }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- alarm graph container new  -->
    </div>
  </div>
</template>

<script>
import NewDateHelper from '@/mixins/NewDateHelper'
import AlarmInsightList from '../performance/AlarmInsights'
import NewRuleRCA from './NewRuleRCA'

import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'widget',
    'groupKey',
    'moduleName',
    'details',
    'fields',
    'primaryFields',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'activeTab',
    'tab',
    'sectionKey',
  ],
  components: {
    AlarmInsightList,
    NewRuleRCA,
  },
  data() {
    return {
      activeRule: null,
      rcaObject: [],
    }
  },

  computed: {
    alarmRCA() {
      let { details } = this
      let { moduleName, alarmRule } = details || {}
      if (moduleName === 'newreadingrules') {
        let { rca } = details || {}
        let { rcaRuleIds } = rca || {}
        if (!isEmpty(rca) && rcaRuleIds.length > 0) {
          return rcaRuleIds
        }
        return null
      } else {
        let { alarmRCARules } = alarmRule || {}
        return alarmRCARules
      }
    },
    ruleId() {
      let { details } = this
      return this.$getProperty(details, 'id')
    },
  },
  methods: {
    isNewRuleEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_READING_RULE')
    },
    getDatePickerObject() {
      return NewDateHelper.getDatePickerObject(31)
    },
    loadRCARules() {
      let promise = []
      this.alarmRCA.forEach(d => {
        promise.push(
          this.$util.fetchRules('alarm', d).then(data => {
            this.rcaObject.push(data)
          })
        )
      })
      Promise.all(promise).finally(() => {
        this.loadRCAReport()
        this.loading = false
      })
    },
    openRcaAlarm(rca) {
      let url = ''
      if (this.moduleName === 'newreadingrules') {
        url =
          '/app/fa/newrules/all/' +
          rca.alarmRule.alarmTriggerRule.id +
          '/summary'
      } else {
        url =
          '/app/fa/rules/all/' + rca.alarmRule.preRequsite.id + '/newsummary'
      }
      this.$router.replace(url)
    },
    getSeverity(rcadetail) {
      let { moduleName, alarmRule } = rcadetail || {}
      let { alarmTriggerRule } = alarmRule || {}
      let severity = ''
      if (moduleName === 'newreadingrules') {
        let { alarmDetails } = alarmTriggerRule || {}
        severity = this.$getProperty(alarmDetails, 'severity', '---')
      } else {
        let { actions } = alarmTriggerRule || {}
        severity = this.$getProperty(
          actions,
          '0.template.originalTemplate.severity',
          '---'
        )
      }
      return severity
    },
    loadRCAReport() {
      if (this.rcaObject && this.rcaObject.length > 0) {
        this.rcaObject.forEach(d => {
          this.$set(d, 'analyticsConfig', null)
          this.$set(
            d,
            'action',
            d.alarmRule.alarmTriggerRule &&
              d.alarmRule.alarmTriggerRule.actions[0] &&
              d.alarmRule.alarmTriggerRule.actions[0].template
              ? d.alarmRule.alarmTriggerRule.actions[0].template
                  .originalTemplate
              : null
          )
        })
      }
    },
    handleChange(index) {
      if (isEmpty(index)) {
        return
      }
      let objrcy = this.rcaObject[index]
      this.$set(objrcy, 'analyticsConfig', {
        readingRuleId: objrcy.alarmRule.alarmTriggerRule.id,
        dateFilter: this.getDatePickerObject(),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        fixedChartHeight: 300,
      })
    },
  },
}
</script>
<style scoped>
.fc-rule-rca-collapse .el-collapse-item__header {
  height: 100px;
  padding-bottom: 14px;
}
.fc-rule-rca-collapse .el-collapse-item__header .el-collapse-item__arrow {
  display: block !important;
  position: absolute;
  right: 0;
  top: 45px;
  width: 26px;
  height: 26px;
  border: solid 1px #e7eeee;
  background-color: #f3f6f6;
  border-radius: 50%;
  text-align: center;
  line-height: 26px;
  font-weight: bold;
  transform: rotate(-90deg);
}
.fc-rule-rca-collapse .el-collapse-item__arrow.is-active {
  transform: rotate(90deg);
}
</style>
<style lang="scss">
.root-cause-list-table {
  .el-table__row td {
    padding-left: 20px;
  }
}
.root-cause-height {
  &.el-table {
    height: auto !important;
  }
}
</style>
