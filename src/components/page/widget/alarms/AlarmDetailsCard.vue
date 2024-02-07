<template>
  <div class="asset-details-widget pT0" ref="alaramDetailCard">
    <div class="container flex" v-if="occurrence">
      <div class="field">
        <el-row class="border-bottom3 pB20 pT15">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.last_Occurred_Time') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left pointer">
                {{ occurrence.createdTime | formatDate() }}
              </div>
            </el-col>
          </el-col>

          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.last_reported_on') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left">
                {{ alarm.lastOccurredTime | formatDate() }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row
          class="pB20 pT15 border-bottom3"
          v-if="moduleName === 'agentAlarm'"
        >
          <el-col :span="24">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common.roles.description') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left pointer pR10">
                {{ alarm.description ? alarm.description : '---' }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row
          class="pB20 pT15 border-bottom3"
          v-else-if="moduleName === 'operationalarm'"
        >
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.thershold_metric') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left line-height20">
                {{
                  alarm.readingField && alarm.readingField
                    ? alarm.readingField.displayName
                    : '---'
                }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row class="pB20 pT15" v-else>
          <el-col v-if="canShowRule()" :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('systemlabels.diagnostics.rule') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div
                @click="
                  openRuleSummaryFromFault(
                    alarm.rule.id,
                    alarm.isNewReadingRule
                  )
                "
                class="fc-black-13 text-left pointer pR10"
              >
                {{ ruleName }}
              </div>
            </el-col>
          </el-col>

          <el-col
            v-if="
              moduleName === 'sensorrollupalarm' &&
                !$validation.isEmpty(alarm.readingField)
            "
            :span="12"
          >
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common.header.reading_name') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left line-height20">
                {{
                  alarm && alarm.readingField
                    ? alarm.readingField.displayName
                    : '---'
                }}
              </div>
            </el-col>
          </el-col>

          <el-col v-else-if="canShowTresholdMetric()" :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.thershold_metric') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left line-height20">
                {{
                  alarm.rule && alarm.rule.readingField
                    ? alarm.rule.readingField.displayName
                    : '---'
                }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row
          class="pB20 pT15 border-top7"
          v-if="!$validation.isEmpty(occurrence.faultType)"
        >
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.rules.fault_type') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div class="fc-black-13 text-left line-height20">
                {{ faultTypes[occurrence.faultType] }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row
          class="pB20 pT15 border-bottom3"
          v-if="alarm.agentAlarmType === 3"
        >
          <el-col :span="24">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('alarm.alarm.points_missing') }}
              </div>
            </el-col>
            <el-col :span="16">
              <div
                @click="redirectToPoints"
                class="fc-black-13 text-left pointer pR10 main-field-column"
              >
                {{ pointsDataMissingCount }}
              </div>
            </el-col>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
  data() {
    return {
      loading: false,
      lookupValue: null,
      spaceDetails: null,
      isAllVisible: false,
      readingField: null,
    }
  },

  mounted() {
    this.autoResize()
    // if (this.alarm.readingFieldId) {
    //   // this.loadFieldDetails()
    // }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ruleName() {
      let name = this.$getProperty(this, 'alarm.rule.name', '---')
      return name
    },
    alarm() {
      return this.details.alarm
    },
    occurrence() {
      return this.details.occurrence
    },
    pointsDataMissingCount() {
      return this.alarm.pointsDataMissingCount
    },
    faultTypes() {
      let enumMap = null
      if (this.metaInfo && this.metaInfo.fields != null) {
        let faultFields = this.metaInfo.fields.filter(
          d => d.name === 'faultType'
        )
        enumMap = faultFields[0].enumMap
      }
      return enumMap
    },
  },
  methods: {
    autoResize() {
      let height = this.$refs['alaramDetailCard'].scrollHeight
      let width = this.$refs['alaramDetailCard'].scrollWidth
      if (this.resizeWidget) {
        this.resizeWidget({ height, width })
      }
    },
    canShowTresholdMetric() {
      let { moduleName, details } = this
      let { alarm } = details || {}
      let { isNewReadingRule } = alarm || {}

      return moduleName !== 'sensorrollupalarm' && !isNewReadingRule
    },
    canShowRule() {
      let { moduleName } = this
      return moduleName !== 'sensorrollupalarm'
    },
    openRuleSummaryFromFault(id, isNewReadingRule) {
      let url = ''
      if (id) {
        if (isNewReadingRule) {
          if (isWebTabsEnabled()) {
            let { name } =
              findRouteForModule('newreadingrules', pageTypes.OVERVIEW) || {}
            name &&
              this.$router.push({
                name,
                params: { id, viewname: 'all' },
              })
          } else {
            this.$router.push({
              name: 'newRulesSummary',
              params: { id, viewname: 'all' },
            })
          }
        } else {
          url = '/app/fa/rules/all/' + id + '/newsummary'
          this.$router.replace({ path: url })
        }
      }
    },
    redirectToPoints() {
      let filters = {
        dataMissing: { operatorId: 15, value: ['true'] },
      }

      let query = {
        search: JSON.stringify(filters),
        includeParentFilter: true,
        agentId: this.$getProperty(this.details, 'alarm.agent.id'),
        agentPointsTab: 'commissioned',
      }

      let path = '/iot/agent/points'
      let name = 'agent-points-default'
      this.$router.push({
        path,
        name,
        query,
      })
    },
  },
}
</script>
<style scoped lang="scss">
.asset-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.field {
  flex: 0 100%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -webkit-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -moz-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}
.fc-link-animation {
  animation: linkanimation 0.85s linear infinite alternate;
  -webkit-animation: linkanimation 0.85s linear infinite alternate;
  -moz-animation: linkanimation 0.85s linear infinite alternate;
}
@keyframes linkanimation {
  from {
    -webkit-transform: translateY(-2px);
    transform: translateY(-2px);
  }
  to {
    -webkit-transform: translateY(2px);
    transform: translateY(2px);
  }
}
.field:last-child:not(:nth-child(even)) {
  border-bottom: none;
}
</style>
