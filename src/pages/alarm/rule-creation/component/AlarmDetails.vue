<template>
  <div>
    <div id="alarmdetails-header" class="section-header">
      {{ $t('alarm.rules.rule_alarm_details') }}
    </div>
    <el-form
      ref="alarmDetailsForm"
      :model="action"
      :rules="validationRules"
      label-width="150px"
      label-position="left"
      class="p50 pT10 pB30 alarm-details-container"
    >
      <div class="section-container flex-container">
        <div class="form-one-column fc-label-required">
          <el-form-item
            :label="`${this.$t('alarm.rules.alarm_message')}`"
            prop="name"
          >
            <el-input
              v-model="action.message"
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 6 }"
              resize="none"
              class="fc-input-full-border-textarea"
              :placeholder="`${this.$t('alarm.rules.alarm_message')}`"
              :autofocus="true"
            ></el-input>
          </el-form-item>
        </div>
        <div class="form-one-column">
          <el-form-item
            :label="`${this.$t('alarm.rules.fault_type')}`"
            prop="faultType"
          >
            <el-select
              v-model="action.faultType"
              class="fc-input-full-border2 width100"
              :placeholder="`${this.$t('alarm.rules.alarm_message')}`"
            >
              <el-option
                v-for="(label, key) in faultTypes"
                :key="key"
                :label="label"
                :value="parseInt(key)"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="form-one-column fc-label-required">
          <el-form-item
            :label="`${this.$t('alarm.alarm.severity')}`"
            prop="severity"
          >
            <el-select
              v-model="action.severity"
              class="fc-input-full-border2 width100"
              :placeholder="`${this.$t('alarm.alarm.severity')}`"
            >
              <el-option
                v-for="label in severityList"
                :key="label.id"
                :label="label.displayName"
                :value="label.severity"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="form-one-column">
          <el-form-item
            :label="`${this.$t('alarm.alarm.problem')}`"
            prop="description"
          >
            <el-input
              v-model="action.problem"
              class="fc-input-full-border2"
              :placeholder="`${this.$t('alarm.rules.problem_placeholder')}`"
              :autofocus="true"
            ></el-input>
          </el-form-item>
        </div>

        <div class="cause-recommendation-conatiner">
          <div
            :class="{
              'fc-id bold pointer f14 pL5': !isV2,
              'fb-v2-text bold pointer pL5': isV2,
            }"
            class="header"
          >
            {{ $t('alarm.alarm.possible_causes') }}
          </div>
          <div class="desc">{{ $t('alarm.rules.possible_desc') }}</div>
          <div
            v-for="(causes, index) in action.possibleCauses"
            :key="index"
            class="cause-recommendation-list"
          >
            <div class="cause-recommendation-index">{{ index + 1 }}</div>
            <el-input
              :ref="`possible-cause-${index}`"
              v-model="action.possibleCauses[index]"
              class="fc-input-border-remove"
              placeholder="Enter a possible cause"
            />
            <i
              class="el-icon-delete pointer trash-icon mR10"
              @click="deleteItem(index, 'possibleCauses')"
            ></i>
          </div>
          <div class="flex flex-direction-row pointer mT20 items-center">
            <fc-icon
              group="action"
              name="circle-plus"
              :color="iconsColor"
              size="18"
            ></fc-icon>
            <div
              ref="possibleCauses"
              :class="{
                'fc-id bold pointer f14 pL5': !isV2,
                'fb-v2-text bold pointer pL5': isV2,
              }"
              @click="addPossibleCause"
            >
              {{ $t('alarm.rules.add_causes') }}
            </div>
          </div>
        </div>

        <div class="cause-recommendation-conatiner">
          <div
            :class="{
              'fc-id bold pointer f14 pL5': !isV2,
              'fb-v2-text bold pointer pL5': isV2,
            }"
            class="header"
          >
            {{ $t('alarm.alarm.recommendation') }}
          </div>
          <div class="desc">{{ $t('alarm.rules.recommendation_desc') }}</div>
          <div
            v-for="(causes, index) in action.recommendations"
            :key="index"
            class="cause-recommendation-list"
          >
            <div class="cause-recommendation-index">{{ index + 1 }}</div>
            <el-input
              :ref="`recommendation-${index}`"
              v-model="action.recommendations[index]"
              class="fc-input-border-remove"
              placeholder="Enter recommendations"
            />
            <i
              class="el-icon-delete pointer trash-icon mR10"
              @click="deleteItem(index, 'recommendations')"
            ></i>
          </div>
          <div class="flex flex-direction-row pointer mT20 items-center">
            <fc-icon
              group="action"
              name="circle-plus"
              :color="iconsColor"
              size="18"
            ></fc-icon>
            <div
              :class="{
                'fc-id bold pointer f14 pL5': !isV2,
                'fb-v2-text bold pointer pL5': isV2,
              }"
              @click="addRecommendation"
            >
              {{ $t('alarm.rules.add_recommendation') }}
            </div>
          </div>
        </div>
      </div>
    </el-form>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from 'util/validation'

export default {
  props: ['ruleDetails', 'isV2'],
  data() {
    return {
      action: {
        message: '',
        severity: null,
        faultType: null,
        problem: '',
        iconsColor: '#3fb4c3',
        possibleCauses: [],
        recommendations: [],
      },
      validationRules: {
        message: [
          {
            required: true,
            message: 'Please enter rule name',
          },
        ],
        severity: [
          {
            required: true,
            message: 'Please select severity',
          },
        ],
      },
    }
  },
  computed: {
    ...mapState({
      alarmSeverity: state => state.alarmSeverity,
      metaInfo: state => state.view.metaInfo,
    }),
    severityList() {
      let { alarmSeverity } = this
      let severityList = []
      if (!isEmpty(alarmSeverity)) {
        severityList = alarmSeverity.filter(m => m.severity !== 'Clear')
      }
      return severityList
    },
    faultTypes() {
      let faultTypeArr = []
      let { metaInfo } = this
      let { fields } = metaInfo || {}
      if (!isEmpty(fields)) {
        let faultField = this.metaInfo.fields.find(
          field => field.displayName === 'faultType'
        )
        let { enumMap } = faultField || {}
        faultTypeArr = enumMap || []
      }
      return faultTypeArr
    },
  },
  mounted() {
    if (this.isV2) {
      this.iconsColor = '#0059d6'
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')
    let { ruleDetails } = this
    let { alarmDetails } = ruleDetails
    if (!isEmpty(alarmDetails)) {
      this.action = alarmDetails
      let { action } = this
      let { faultType } = action || {}

      if (faultType <= 0) {
        this.$set(action, 'faultType', null)
      }
    }
  },
  watch: {
    action: {
      handler(newVal) {
        this.$emit('onDetailsChange', { alarmDetails: newVal })
      },
      deep: true,
    },
  },
  methods: {
    addPossibleCause() {
      this.action.possibleCauses.push('')
      let possibleCauses = this.$getProperty(this, 'action.possibleCauses')
      let index = possibleCauses.length - 1
      this.$nextTick(() => {
        let input = this.$refs[`possible-cause-${index}`][0]
        if (!isEmpty(input)) input.focus()
      })
    },
    addRecommendation() {
      this.action.recommendations.push('')
      let recommendations = this.$getProperty(this, 'action.recommendations')
      let index = recommendations.length - 1
      this.$nextTick(() => {
        let input = this.$refs[`recommendation-${index}`][0]
        if (!isEmpty(input)) input.focus()
      })
    },
    deleteItem(index, key) {
      this.action[key].splice(index, 1)
    },
  },
}
</script>
<style lang="scss" scoped>
.alarm-details-container {
  .cause-recommendation-conatiner {
    margin-top: 30px;
    width: 100%;
    .header {
      font-size: 12px;
      font-weight: 500;
      line-height: normal;
      letter-spacing: 1px;
      color: #ee508f;
    }
    .desc {
      margin: 10px 0px 20px;
      font-size: 14px;
      line-height: normal;
      letter-spacing: 0.5px;
      color: #6b7e91;
    }
    .cause-recommendation-list {
      display: flex;
      flex-direction: row;
      align-items: center;
      flex-wrap: nowrap;
      padding: 20px 5px;
      border-top: solid 0.5px #f3f5f6;
      &:hover {
        background-color: #ebfafd;
      }
    }
    .configured-green {
      color: #5bc293;
    }
    .cause-recommendation-index {
      color: #91b3b6;
      margin-right: 10px;
    }
    .trash-icon {
      color: #de7272;
      display: none;
    }
    .cause-recommendation-list:hover .trash-icon {
      display: block;
    }
  }
}
</style>
