<template>
  <div class="planner-summary-container">
    <div class="planner-summary-header">
      {{ $t('maintenance.pm.trigger_summary') }}
    </div>
    <div class="planner-field-container">
      <div
        v-for="(field, index) in fieldList"
        :key="`pm-fields-${index}`"
        class="planner-field"
      >
        <div class="planner-field-label">{{ field.name }}</div>
        <div class="planner-field-value">
          {{ field.getValue({ planner }) }}
        </div>
      </div>
      <div class="planner-field">
        <div class="planner-field-label">{{ resourcePlaceholder }}</div>
        <div class="planner-field-value">
          {{ resourceCount }}
        </div>
      </div>
    </div>
    <template v-if="!$validation.isEmpty(triggerSeasons)">
      <div class="planner-summary-header mT20">
        {{ 'Season Details' }}
      </div>
      <template v-for="(value, index) in triggerSeasons">
        <div :key="index + '-field-season'" class="planner-field-container">
          <div :key="`seasonname-${index}`" class="planner-field">
            <div class="planner-field-label">{{ 'Season Name' }}</div>
            <div class="planner-field-value">
              {{ value.name }}
            </div>
          </div>
          <div :key="`seasonstart-${index}`" class="planner-field">
            <div class="planner-field-label">{{ 'Season Start' }}</div>
            <div class="planner-field-value">
              {{
                ('00' + value.startDate).slice(-2) +
                  ' ' +
                  getMonthName(value.startMonth)
              }}
            </div>
          </div>
          <div :key="`seasonend-${index}`" class="planner-field">
            <div class="planner-field-label">{{ 'Season End' }}</div>
            <div class="planner-field-value">
              {{
                ('00' + value.endDate).slice(-2) +
                  ' ' +
                  getMonthName(value.endMonth)
              }}
            </div>
          </div>
          <div :key="`seasonspace-${index}`" class="planner-field"></div>
        </div>
      </template>
    </template>
  </div>
</template>

<script>
import Constant from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import getProperty from 'dlv'
import helpers from 'src/util/helpers.js'
import Vue from 'vue'
import { FREQUENCY_HASH } from 'pages/workorder/pm/create/utils/pm-utils.js'
import Constants from 'util/constant'

const { getOrgMoment: moment } = helpers
const getSchedulerData = planner => {
  let schedule = getProperty(planner, 'trigger.schedule')
  if (!isEmpty(schedule)) {
    return JSON.parse(schedule)
  } else {
    return {}
  }
}
const SKIP_OPTIONS = [
  {
    label: 'Do not skip',
    value: -1,
  },
  {
    label: 'Second Cycle',
    value: 2,
  },
  {
    label: 'Third Cycle',
    value: 3,
  },
  {
    label: 'Fourth Cycle',
    value: 4,
  },
  {
    label: 'Fifth Cycle',
    value: 5,
  },
  {
    label: 'Sixth Cycle',
    value: 6,
  },
  {
    label: 'Seventh Cycle',
    value: 7,
  },
  {
    label: 'Eighth Cycle',
    value: 8,
  },
  {
    label: 'Ninth Cycle',
    value: 9,
  },
  {
    label: 'Tenth Cycle',
    value: 10,
  },
]

const formatTime = timeStamp => {
  let currentOrgTimeZone = Vue.prototype.$timezone
  timeStamp = moment(timeStamp)

  return timeStamp.tz(currentOrgTimeZone).format('DD MMM YYYY')
}

const FIELDS_HASH = [
  {
    name: 'Frequency',
    getValue: ({ planner }) => {
      let schedule = getSchedulerData(planner)
      let facilioFrequency = Constant.FACILIO_FREQUENCY
      if (!isEmpty(schedule)) {
        let { frequencyType } = schedule || {}

        if (
          ['MONTHLY_DATE', 'MONTHLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 3
        } else if (
          ['QUARTERLY_DATE', 'QUARTERLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 4
        } else if (
          ['HALF_YEARLY_DATE', 'HALF_YEARLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 5
        } else if (
          ['ANNUALLY_DATE', 'ANNUALLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 6
        }

        if (facilioFrequency[frequencyType]) {
          return facilioFrequency[frequencyType]
        } else {
          return frequencyType
        }
      }
      return '---'
    },
  },
  {
    name: 'Execution Start Date',
    getValue: ({ planner }) => {
      let { trigger } = planner || {}
      let { startTime } = trigger || {}

      return startTime ? formatTime(startTime) : '---'
    },
  },
  {
    name: 'Execution End Date',
    getValue: ({ planner }) => {
      let { trigger } = planner || {}
      let { endTime } = trigger || {}

      return endTime ? formatTime(endTime) : '---'
    },
  },
  {
    name: 'Skip',
    getValue: ({ planner }) => {
      let scheduler = getSchedulerData(planner)
      let { skipEvery } = scheduler || {}
      let skipObject = SKIP_OPTIONS.find(option => {
        let { value } = option || {}
        return value === skipEvery
      })
      let { label } = skipObject || {}
      return label || '---'
    },
  },
]

export default {
  name: 'PlannerSummary',
  props: ['planner', 'resourcePlaceholder'],
  data: () => ({
    fieldList: FIELDS_HASH,
    months: Constants.MONTHS,
  }),
  computed: {
    resourceCount() {
      let { planner } = this
      return this.$getProperty(planner, 'resourceCount', 0)
    },
    triggerSeasons() {
      let { planner } = this
      let allSeasons = []
      let schedule = this.$getProperty(planner, 'trigger.schedule')
      if (!isEmpty(schedule)) {
        schedule = JSON.parse(schedule)
        let { seasons } = schedule || {}
        allSeasons = seasons
      }
      return allSeasons
    },
  },
  methods: {
    getMonthName(value) {
      return this.$constants.scheduleMonthValue[value - 1]
    },
  },
}
</script>

<style scoped lang="scss">
.planner-field-container {
  display: grid;
  grid-template-columns: 25% 25% 25% 25%;
}

.planner-field {
  padding: 20px 0px;
}

.planner-field-label {
  color: #324056;
  font-size: 13px;
  font-weight: 550;
  letter-spacing: 0.74px;
  text-transform: capitalize;
}
.planner-field-value {
  font-size: 14px;
  letter-spacing: 0.86px;
  color: #324056;
  margin-top: 8px;
}
</style>
