<template>
  <div>
    <div class="alarm-summary-header">
      <div class="fL">
        <div class="fc-id">#{{ rule.alarmRule.preRequsite.id }}</div>
        <div class="fc-black3-24 max-width600px textoverflow-height-ellipsis">
          {{ rule.alarmRule.preRequsite.name }}
        </div>
        <div class="fc-grey2-text14 ellipsis max-width500px mT10">
          {{ rule.alarmRule.preRequsite.description }}
        </div>
      </div>
      <div class="fR">
        <div class="triangle-close">
          <i
            class="close-summary-icon material-icons pull-right pointer"
            @click="closeSummary"
            data-theme="light"
            data-arrow="true"
            v-tippy
            >close</i
          >
        </div>
        <div class="display-flex">
          <div class="label-txt-black mR10">Enabled</div>
          <el-switch
            v-model="rule.alarmRule.preRequsite.status"
            @change="changeRuleStatus(rule)"
            class="mR10"
            active-color="rgba(57, 178, 194, 0.8)"
            inactive-color="#e5e5e5"
          ></el-switch>
          <div class="fc-subheader-divide-small mR10"></div>
          <div class="label-txt-blue mR10">Edit Rule</div>
        </div>
      </div>
    </div>
    <!-- alarm summary body -->
    <div class="alarm-summary-body clearboth">
      <div class="alarm-summary-body-left">
        <div class="alaram-summary-body-scroll">
          <div class="alarm-summary-inner">
            <!-- counts -->
            <div class="mB20">
              <el-row>
                <el-col :span="8">
                  <div class="fc-white-bg-container text-center">
                    <div class="text-center blue-bg mR-auto mL-auto">
                      <img src="~assets/box-outline.svg" />
                    </div>
                    <div class="f36 fc-black-2">
                      {{
                        rule.alarmRulewoSummary ? rule.alarmRulewoSummary : '--'
                      }}
                    </div>
                    <div class="fc-black-small-txt mT10 bold">
                      ASSETS ASSOCIATED
                    </div>
                  </div>
                </el-col>
                <el-col
                  :span="15"
                  class="fc-white-bg-container text-center mL20"
                  style="width: 63.748%;"
                >
                  <el-col :span="12">
                    <div class="red-bg mR-auto mL-auto">
                      <img src="~assets/alarm2.svg" width="30" height="30" />
                    </div>
                    <div class="f36 fc-black-2">
                      {{
                        rule.alarmRuleActiveAlarm
                          ? rule.alarmRuleActiveAlarm
                          : '--'
                      }}
                    </div>
                    <div class="fc-black-small-txt mT10 bold">
                      ACTIVE ALARMS
                    </div>
                  </el-col>
                  <el-col :span="12" class="card-border-right text-center">
                    <div class="red-bg mR-auto mL-auto">
                      <img src="~assets/calendar3.svg" width="30" height="30" />
                    </div>
                    <div class="f36 fc-black-2">
                      {{
                        rule.alarmRuleThisWeek ? rule.alarmRuleThisWeek : '--'
                      }}
                    </div>
                    <div class="fc-black-small-txt mT10 bold">
                      ALARMS THIS WEEK
                    </div>
                  </el-col>
                </el-col>
              </el-row>
            </div>
            <!-- first -->
            <!-- expression -->
            <div class="fc-white-bg-container mT20">
              <div class="fc-blue3 f11 text-uppercase letter-spacing07">
                CONDITION
              </div>
              <div
                class="fc-dark-blue-txt2 text-uppercase pT20 letter-spacing07"
              >
                PREREQUISITE
              </div>
              <div class="mT10 label-txt-black">{{ rule.preRequsite }}</div>
              <div class="fc-divider-width100 mT20 mB20"></div>
              <div class="fc-dark-blue-txt2 text-uppercase">
                Alarm triggered when
              </div>
              <el-row class="mT20">
                <el-col :span="4">
                  <div class="fc-badge-critical">CRITICAL</div>
                </el-col>
                <el-col :span="19">
                  <div class="label-txt-black">{{ rule.alarmCondition }}</div>
                </el-col>
              </el-row>
              <div class="fc-divider-width100 mT20 mB20"></div>
              <div class="fc-dark-blue-txt2 text-uppercase">
                ALARM CLEARED WHEN
              </div>
              <div class="mT10 label-txt-black">Autoclear (Default)</div>
            </div>
            <div class="fc-white-bg-container mT20">
              <el-row class="pT10" :gutter="20">
                <el-col :span="11">
                  <div class="fc-blue3 f11 text-uppercase letter-spacing07">
                    Possible Causes
                  </div>
                  <div class="mT10 label-txt-black">
                    {{
                      rule.alarmRule.alarmTriggerRule.actions[0].template
                        .originalTemplate.possibleCauses
                    }}
                  </div>
                </el-col>
                <el-col :span="1">
                  <div class="separator-lg"></div>
                </el-col>
                <el-col :span="11">
                  <div class="fc-blue3 f11 text-uppercasee letter-spacing07">
                    POSSIBLE SOLUTIONS
                  </div>
                  <div class="mT10 label-txt-black">
                    {{
                      rule.alarmRule.alarmTriggerRule.actions[0].template
                        .originalTemplate.problem
                    }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <!-- table -->
            <div class="fc-white-bg-container mT20">
              <div class="fc-black3-16">Top 5 Critical Asset</div>
              <div class="container-scroll">
                <div class="row setting-Rlayout p0">
                  <table class="setting-list-view-table">
                    <thead>
                      <tr>
                        <th class="setting-table-th setting-th-text">ID</th>
                        <th class="setting-table-th setting-th-text">NAME</th>
                        <th class="setting-table-th setting-th-text">
                          LOCATION
                        </th>
                        <th class="setting-table-th setting-th-text">
                          DURATION
                        </th>
                      </tr>
                    </thead>
                    <tbody v-if="loading">
                      <tr>
                        <td colspan="100%" class="text-center">
                          <spinner :show="loading" size="80"></spinner>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
            <!-- table -->
            <div class="fc-white-bg-container mT20">
              <el-row>
                <el-col :span="4" class="text-center width25">
                  <div>
                    <img src="~assets/monitor.svg" width="15" height="15" />
                  </div>
                  <div v-if="rule.alarmRulewoSummary" class="f36 fc-black-2">
                    {{ rule.alarmRulewoSummary.woCreatedThisWeek }}
                  </div>
                  <div class="fc-black-small-txt bold">OPEN</div>
                  <div class="pT10 fc-grey2-11">WORKORDERS</div>
                </el-col>
                <el-col :span="4" class="card-border-right text-center width25">
                  <div>
                    <img src="~assets/monitor.svg" width="15" height="15" />
                  </div>
                  <div v-if="rule.alarmRulewoSummary" class="f36 fc-black-2">
                    {{ rule.alarmRulewoSummary.woCreatedThisMonth }}
                  </div>
                  <div class="fc-black-small-txt bold">WORKORDERS</div>
                  <div class="pT10 fc-grey2-11">THIS MONTH</div>
                </el-col>
                <el-col :span="4" class="card-border-right text-center width25">
                  <div class>
                    <img src="~assets/clock-black.svg" width="15" height="15" />
                  </div>
                  <div v-if="rule.alarmRulewoSummary" class="f36 fc-black-2">
                    {{ rule.alarmRulewoSummary.avgResponseTimeInMins }}
                  </div>
                  <div class="fc-black-small-txt bold">AVG TIME TAKEN</div>
                  <div class="pT10 fc-grey2-11">TO ACKNOWLEDGE</div>
                </el-col>
                <el-col :span="4" class="text-center card-border-right width25">
                  <div class>
                    <img src="~assets/clock-black.svg" width="15" height="15" />
                  </div>
                  <div v-if="rule.alarmRulewoSummary" class="f36 fc-black-2">
                    {{ rule.alarmRulewoSummary.avgResolutionTimeInMins }}
                  </div>
                  <div class="fc-black-small-txt mT10 bold">AVG TIME TAKEN</div>
                  <div class="pT10 fc-grey2-11">TO COMPLETE</div>
                </el-col>
              </el-row>
            </div>
            <!-- tool -->
          </div>
        </div>
      </div>
      <div class="alarm-summary-body-right clearboth">
        <div class="alaram-summary-body-scroll mL30 mR20">
          <div class="mT20">
            <div class="fc-blue3 f11 text-uppercase letter-spacing07">
              ASSET CATEGORY
            </div>
            <div class="mT10 label-txt-black">
              {{
                rule.alarmRule.preRequsite.assetCategoryId > 0
                  ? getAssetCategory(rule.alarmRule.preRequsite.assetCategoryId)
                      .name
                  : '---'
              }}
            </div>
          </div>
          <div class="mT20">
            <div class="fc-blue3 f11 text-uppercase letter-spacing07">
              THRESHOLD METRIC
            </div>
            <div class="mT10 label-txt-black">
              {{ metricField ? metricField.displayName : '--' }}
            </div>
          </div>
          <div class="mT20">
            <div class="fc-blue3 f11 text-uppercase letter-spacing07">
              MESSAGE
            </div>
            <div class="mT10 label-txt-black">
              {{
                rule.alarmRule.alarmTriggerRule.actions[0].template
                  .originalTemplate.message
              }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import JumpToHelper from '@/mixins/JumpToHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import newDateHelper from '@/mixins/NewDateHelper'
import NewDataFormatHelper from 'pages/report/mixins/NewDataFormatHelper'
import { mapGetters } from 'vuex'
export default {
  mixins: [JumpToHelper, NewDataFormatHelper, AnalyticsMixin, newDateHelper],
  data() {
    return {
      thresholdFields: null,
      metricField: null,
      rule: {},
      rules: {},
      alarmRule: [],
    }
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
    rulesId() {
      return parseInt(this.$route.params.id)
    },
  },
  created() {
    this.$util.fetchRule('alarm', this.$route.params.id)
    this.$store.dispatch('loadAssetCategory')
  },
  watch: {
    rulesId: function(newVal) {
      this.loadRuleDetails()
    },
  },
  mounted() {
    this.loadRuleDetails()
  },
  methods: {
    loadRuleDetails() {
      let self = this
      self.$http
        .get('/v2/alarm/rules/fetchRule?ruleId=' + self.$route.params.id)
        .then(function(response) {
          self.rule = response.data.result
          self.loadThresholdFields(
            response.data.result.alarmRule.preRequsite.assetCategoryId,
            response.data.result.alarmRule.preRequsite.readingFieldId
          )
          console.log('******************', response.data.result)
        })
      self.$util
        .fetchRule('alarm', self.$route.params.id)
        .then(function(response) {
          self.rules = response.data.result
          console.log('******************', response.data.result)
        })
    },
    loadThresholdFields(id, readingId) {
      this.thresholdFields = []
      this.$store
        .dispatch('formulabuilder/loadAssetReadings', {
          assetCategoryId: id,
        })
        .then(() => {
          this.thresholdFields = this.$store.getters[
            'formulabuilder/getAssetReadings'
          ](id, true)
          this.getFieldDisplayName(readingId)
        })
    },
    getFieldDisplayName(id) {
      let fieldObj
      this.thresholdFields.filter(d => {
        if (d.id === id) {
          fieldObj = d
        }
      })
      this.metricField = fieldObj
      return fieldObj
    },
    closeSummary() {
      let newpath = this.$route.path.substring(
        0,
        this.$route.path.indexOf('/summary/')
      )
      this.$router.replace({ path: newpath })
    },
    changeRuleStatus(rule) {
      this.$util
        .changeRuleStatus('alarm', rule.id, rule.status)
        .then(function(response) {})
        .catch(function(error) {
          console.log(error)
        })
    },
  },
}
</script>
