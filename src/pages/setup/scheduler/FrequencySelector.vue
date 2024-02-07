<template>
  <el-form :rules="rules" :model="schedule" ref="frequencyForm">
    <el-row class="mT20">
      <div class="fc-input-label-txt">
        {{ $t('setup.scheduler.frequency') }}
      </div>
      <el-col :span="24">
        <el-select
          @change="resetSchedule"
          v-model="schedule.frequencyType"
          class="fc-input-full-border2 width100"
        >
          <el-option
            v-for="(item, key) in frequencyTypeMap"
            :key="key"
            :label="item"
            :value="parseInt(key)"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row class="mT20">
      <el-col :span="24">
        <el-form-item
          :label="$t('setup.scheduler.execution_start')"
          prop="startTime"
        >
          <el-date-picker
            v-model="schedule.startTime"
            :picker-options="dateOptions"
            class="form-item fc-input-full-border-select2 date-picker-trigger"
            style="width:100%"
            float-label="Start Date"
            :type="dateType"
            suffix-icon="el-icon-date"
            value-format="timestamp"
            @change="resetEndDate"
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row class="mT20">
      <el-col :span="24">
        <el-form-item
          :label="$t('setup.scheduler.execution_end')"
          prop="endDate"
        >
          <el-date-picker
            v-model="schedule.endDate"
            :picker-options="endDateOptions"
            class="form-item fc-input-full-border-select2 date-picker-trigger"
            style="width:100%"
            float-label="End Date"
            :type="dateType"
            suffix-icon="el-icon-date"
            value-format="timestamp"
            :disabled="!schedule.startTime"
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row class="mT20" v-if="showMonths">
      <div class="fc-input-label-txt">
        {{ $t('setup.scheduler.execution_end') }}
        {{ frequencyTypeMap[schedule.frequencyType] }}
      </div>
      <el-col>
        <el-select
          v-model="schedule.monthValue"
          class="fc-input-full-border2 width100"
        >
          <el-option
            v-for="(month, key) in monthsMap"
            :value="parseInt(key)"
            :label="month"
            :key="parseInt(key)"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row class="mT20" v-if="showDays">
      <el-form-item :label="$t('setup.scheduler.execution_days')" prop="values">
        <el-col :span="24">
          <el-select
            class="fc-input-full-border2 width100"
            v-model="schedule.values"
            multiple
            collapse-tags
          >
            <el-option
              v-for="(day, key) in weekDaysMap"
              :key="day"
              :label="day"
              :value="parseInt(key)"
            ></el-option>
          </el-select>
        </el-col>
      </el-form-item>
    </el-row>
    <el-row class="mT20" v-if="showBasedOn">
      <div class="fc-input-label-txt">
        {{ $t('setup.scheduler.based_on') }}
      </div>
      <el-col :span="24" class="mB20">
        <el-radio-group v-model="schedule.basedOn" @input="onBasedOnChange">
          <el-radio
            v-for="(f, key) in basedOnMap"
            :label="f"
            :value="f"
            :key="key"
            class="fc-radio-btn"
          ></el-radio>
        </el-radio-group>
      </el-col>
      <el-row class="mT20" v-if="isAnnually">
        <el-col :span="24">
          <el-form-item :label="$t('setup.scheduler.months')" prop="months">
            <el-select
              v-model="schedule.months"
              multiple
              collapse-tags
              class="fc-input-full-border2 width100 fc-tag"
            >
              <el-option
                v-for="(option, key) in fullMonthsMap"
                :label="option"
                :value="parseInt(key)"
                :key="parseInt(key)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mT20" v-if="schedule.basedOn === 'Date'">
        <el-col>
          <el-form-item
            :label="$t('setup.scheduler.execution_date')"
            prop="values"
          >
            <el-select
              class="fc-input-full-border2 width100"
              v-model="schedule.values"
              :multiple="!isAnnually"
              collapse-tags
            >
              <el-option
                v-for="(option, key) in getNumberRange(31)"
                :key="parseInt(key)"
                :value="parseInt(key)"
                :label="option"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row class="mT20" v-if="schedule.basedOn === 'Week'">
        <el-form-item :label="$t('setup.scheduler.execute_on')" prop="values">
          <el-col :span="5">
            <el-select v-model="schedule.weekFrequency">
              <el-option
                v-for="(option, key) in weekListMap"
                :key="key"
                :label="option"
                :value="parseInt(key)"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="19">
            <el-select
              class="fc-input-full-border2 width100"
              v-model="schedule.values"
              multiple
              collapse-tags
            >
              <el-option
                v-for="(day, key) in weekDaysMap"
                :key="day"
                :label="day"
                :value="parseInt(key)"
              ></el-option>
            </el-select>
          </el-col>
        </el-form-item>
      </el-row>
    </el-row>

    <el-row class="mT20">
      <el-col :span="12">
        <el-form-item
          :label="$t('setup.scheduler.execution_time')"
          prop="times"
        >
          <div v-if="isDaily">
            <el-select
              class="fc-input-full-border-select2 width100 fc-tag"
              multiple
              v-model="schedule.times"
              :multiple-limit="timeOptionLimit"
              collapse-tags
            >
              <el-option
                v-for="time in timeOptionMapforDaily"
                :label="time"
                :value="time"
                :key="time"
              ></el-option>
            </el-select>
          </div>
          <div v-else>
            <el-select
              class="fc-input-full-border-select2 width100 fc-tag"
              multiple
              v-model="schedule.times"
              :multiple-limit="timeOptionLimit"
              collapse-tags
            >
              <el-option
                v-for="time in timeOptionMap"
                :label="time"
                :value="time"
                :key="time"
              ></el-option>
            </el-select>
          </div>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row v-if="showRunEvery" class="mT20">
      <div class="fc-input-label-txt">
        {{ $t('setup.scheduler.run_every') }}
      </div>
      <el-col>
        <el-select
          class="fc-input-full-border2 width100"
          v-model="schedule.frequency"
        >
          <el-option
            v-for="(option, key) in runEveryOptions"
            :label="option"
            :value="parseInt(key)"
            :key="parseInt(key)"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row class="mT20">
      <div class="fc-input-label-txt">{{ $t('setup.scheduler.skip') }}</div>
      <el-col>
        <el-select
          class="fc-input-full-border2 width100"
          v-model="schedule.skipEvery"
        >
          <el-option
            v-for="item in skipEveryMap"
            :label="item.label"
            :value="parseInt(item.value)"
            :key="parseInt(item.value)"
          >
          </el-option>
        </el-select>
      </el-col>
    </el-row>
  </el-form>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import {
  skipEveryMap,
  weekDaysMap,
  weekListMap,
  fullMonthsMap,
  frequencyTypeMap,
  monthOfQuarterMap,
  monthOfHalfMap,
  basedOnMap,
  frequencyTypes,
} from './schedulerFrequencyUtil'

const initialSchedule = {
  times: ['00:00'],
  startTime: null,
  endDate: null,
  skipEvery: -1,
  frequency: 1,
  values: [],
  months: [],
  frequencyType: 1,
  weekFrequency: -1,
  monthValue: -1,
  basedOn: 'Date',
}

export default {
  props: ['value', 'isNew'],
  data() {
    return {
      timeVal: 0,
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          let fiveYear = new Date()
          let startData = time.getTime()

          today.setHours(0, 0, 0, 0)
          fiveYear.setFullYear(today.getFullYear() + 5)
          return startData < today.getTime() || startData > fiveYear.getTime()
        },
      },
      timeOptionMap: [],
      timeOptionMapforDaily: [],
      schedule: cloneDeep(initialSchedule),
      skipEveryMap,
      weekDaysMap,
      weekListMap,
      fullMonthsMap,
      frequencyTypeMap,
      monthOfQuarterMap,
      monthOfHalfMap,
      basedOnMap,
      frequencyTypes,
      rules: {
        name: [
          { required: true, message: 'Name is Mandatory ', trigger: 'blur' },
        ],
        startTime: [
          {
            type: 'date',
            required: true,
            message: 'Execution Start Date is Mandatory ',
            trigger: 'blur',
          },
        ],
        months: [
          {
            required: true,
            message: 'Months cannot be empty',
            trigger: 'change',
          },
        ],
        values: [
          {
            required: true,
            message: 'Execution Date is Mandatory ',
            trigger: 'change',
          },
        ],
        times: [
          {
            required: true,
            message: 'Execution Time is Mandatory ',
            trigger: 'change',
          },
        ],
      },
    }
  },

  computed: {
    endDateOptions() {
      let { schedule } = this || {}
      let { startTime } = schedule || {}
      return {
        disabledDate(time) {
          return time.getTime() < startTime
        },
      }
    },
    showMonths() {
      let { QUARTERLY, HALF_YEARLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return [QUARTERLY, HALF_YEARLY].includes(frequencyType)
    },
    isDaily() {
      let { DAILY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return DAILY === frequencyType
    },
    showDays() {
      let { WEEKLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return frequencyType >= WEEKLY ? true : false
    },
    dateType() {
      let { MONTHLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return frequencyType < MONTHLY ? 'date' : 'month'
    },
    showBasedOn() {
      let { MONTHLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return frequencyType >= MONTHLY ? true : false
    },
    showRunEvery() {
      let { DO_NOT_REPEAT, QUARTERLY, HALF_YEARLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return ![DO_NOT_REPEAT, QUARTERLY, HALF_YEARLY].includes(frequencyType)
    },
    timeOptionLimit() {
      let { DO_NOT_REPEAT } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return frequencyType === DO_NOT_REPEAT ? 1 : 0
    },
    runEveryOptions() {
      let { DAILY, WEEKLY, MONTHLY, ANNUALLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      let displayNames = {
        [DAILY]: 'Day',
        [WEEKLY]: 'Week',
        [MONTHLY]: 'Month',
        [ANNUALLY]: 'Year',
      }
      let freqType = displayNames[frequencyType]
      let freqList = { 1: freqType }
      let freqRange = frequencyType === ANNUALLY ? 30 : 60
      for (let i = 2; i <= freqRange; i++) {
        freqList[i] = `${i} ${freqType}s`
      }
      return freqList
    },
    monthsMap() {
      let { QUARTERLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return frequencyType === QUARTERLY
        ? this.monthOfQuarterMap
        : this.monthOfHalfMap
    },
    isAnnually() {
      let { ANNUALLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      return ANNUALLY === frequencyType
    },
  },
  watch: {
    schedule: {
      handler(newValue) {
        this.$emit('input', newValue)
      },
      deep: true,
    },
  },
  mounted() {
    if (!this.isNew) {
      this.schedule = cloneDeep(this.value)
    }
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timeOptionMap.push(time + '00')
      this.timeOptionMap.push(time + '30')
    }
    for (let i = 0; i <= 23; i++) {
      let time = (i < 10 ? '0' + i : i) + ':'
      this.timeOptionMapforDaily.push(time + '00')
      this.timeOptionMapforDaily.push(time + '15')
      this.timeOptionMapforDaily.push(time + '30')
      this.timeOptionMapforDaily.push(time + '45')
    }
  },
  methods: {
    resetEndDate() {
      let { schedule } = this || {}
      this.schedule = { ...schedule, endDate: null }
    },
    resetSchedule() {
      let { DAILY, QUARTERLY, HALF_YEARLY, ANNUALLY } = this.frequencyTypes
      let { frequencyType } = this.schedule
      let { months } = initialSchedule
      let monthValue = [QUARTERLY, HALF_YEARLY].includes(frequencyType) ? 1 : 1
      let values = frequencyType === DAILY ? [] : [1]
      let basedOn = 'Date'
      if (frequencyType === ANNUALLY) {
        months = [1]
        values = 1
      }
      this.schedule = {
        ...initialSchedule,
        frequencyType,
        monthValue,
        values,
        basedOn,
        months,
      }
    },
    getNumberRange(no) {
      let list = {}
      let startDate = this.$helpers.getOrgMoment('2022-01-01')
      for (let i = 1; i <= no; i++) {
        list[i] = startDate.format('DDDo')
        startDate.add(1, 'd')
      }
      return list
    },
    onBasedOnChange() {
      this.schedule.values = [1]
      if (this.schedule.basedOn === 'Week') this.schedule.weekFrequency = 1
      if (this.schedule.basedOn === 'Date' && this.isAnnually)
        this.schedule.values = 1
    },
    async formValidate() {
      let valid = await new Promise(resolve => {
        this.$refs['frequencyForm'].validate(valid => {
          resolve(valid)
        })
      })
      return valid
    },
  },
}
</script>
