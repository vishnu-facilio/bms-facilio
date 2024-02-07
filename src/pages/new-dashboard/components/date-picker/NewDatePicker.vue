<template>
  <div>
    <div v-if="!$mobile">
      <el-popover
        ref="popover4"
        placement="right"
        popper-class="new-calender-popup"
        :width="825"
        v-model="datePicker"
        trigger="click"
      >
        <div class="row">
          <div class="row datepicker-header">
            <div class="pointer"></div>
            <div
              class="p10 range-selecter marginL"
              v-if="enableTabs('C')"
              @click="changeTab('C')"
              v-bind:class="{ active: currentTab === 'C' }"
            >
              {{ $t('common.date_picker.quick_ranges') }}
            </div>
            <div
              class="p10 range-selecter"
              v-if="enableTabs('D')"
              @click="changeTab('D')"
              v-bind:class="{ active: currentTab === 'D' }"
            >
              {{ $t('common.date_picker.day') }}
            </div>
            <div
              class="p10 range-selecter"
              v-if="enableTabs('W')"
              @click="changeTab('W')"
              v-bind:class="{ active: currentTab === 'W' }"
            >
              {{ $t('common.date_picker.week') }}
            </div>
            <div
              class="p10 range-selecter"
              v-if="enableTabs('M')"
              @click="changeTab('M')"
              v-bind:class="{ active: currentTab === 'M' }"
            >
              {{ $t('common.date_picker.month') }}
            </div>
            <div
              class="p10 range-selecter"
              v-if="enableTabs('Y')"
              @click="changeTab('Y')"
              v-bind:class="{ active: currentTab === 'Y' }"
            >
              {{ $t('common.date_picker.year') }}
            </div>
            <div
              class="p10 range-selecter"
              v-if="enableTabs('Q')"
              @click="changeTab('Q')"
              v-bind:class="{ active: currentTab === 'Q' }"
            >
              {{ $t('common.date_picker.quarter') }}
            </div>
            <div
              class="p10 range-selecter"
              v-if="enableTabs('R')"
              @click="changeTab('R')"
              v-bind:class="{ active: currentTab === 'R' }"
            >
              {{ $t('common.date_picker.custom') }}
            </div>
            <div></div>
          </div>
          <div class="days-section">
            <div v-if="currentTab === 'C'" class="row">
              <div
                class="custom-section"
                v-if="dateOperator !== 'Custom'"
                v-for="(dateOperator, index) in Object.keys(dateOperators)"
                :key="index"
              >
                <span class="custom-section-title">{{ dateOperator }}</span>
                <div class="quick-range-container">
                  <div
                    class="cal-custom-day"
                    v-for="(section, index1) in dateOperators[dateOperator]"
                    :key="index1"
                    @click="custom(section)"
                    v-bind:class="{
                      active:
                        customCaseSection !== null
                          ? customCaseSection.operatorId ===
                              section.operatorId &&
                            customCaseSection.offset === section.offset
                          : false,
                    }"
                  >
                    {{ section.label }}
                  </div>
                </div>
              </div>
            </div>

            <div v-if="currentTab === 'Q'" class="row month-row">
              <div class="year-section">
                <div
                  class="p5 year"
                  v-for="y in years"
                  :key="y"
                  v-bind:class="{ active: selectedQuarter.year === y }"
                  @click="setYear(y)"
                >
                  {{ y }}
                </div>
              </div>
              <div class="col-3 month-container">
                <div
                  class="quarter-row pointer"
                  v-bind:class="{
                    active: selectedQuarter.quarter === quarter.id,
                  }"
                  v-for="quarter in quarters"
                  :key="quarter.id"
                  @click="setQuarter(quarter.id, null)"
                >
                  {{ quarter.label }}
                </div>
              </div>
              <div class="calender-custom col-4" v-if="currentTab === 'Q'">
                <div
                  class="cal-custom-day"
                  v-if="enableTabs('C')"
                  @click="quickChoose('thisquarter')"
                  v-bind:class="{
                    active:
                      time[0] === thisQuarter[0] && time[1] === thisQuarter[1],
                  }"
                >
                  {{ $t('common.date_picker.this_quarter') }}
                </div>
                <div
                  class="cal-custom-day"
                  v-if="enableTabs('C')"
                  @click="quickChoose('lastquarter')"
                  v-bind:class="{
                    active:
                      time[0] === lastQuarter[0] && time[1] === lastQuarter[1],
                  }"
                >
                  {{ $t('common.date_picker.last_quarter') }}
                </div>
              </div>
            </div>

            <div
              class="row calenderheader-row"
              v-if="currentTab === 'D' || currentTab === 'W'"
            >
              <div class="p5" @click="previous()">
                <i class="el-icon-arrow-left"></i>
              </div>
              <div class="p5">{{ months[selectedDate['month']].label }}</div>
              <div class="p5">{{ selectedDate['year'] }}</div>
              <div class="p5" @click="next()">
                <i class="el-icon-arrow-right"></i>
              </div>
            </div>
            <div
              class="row week-row"
              v-if="currentTab === 'D' || currentTab === 'W'"
            >
              <div
                class="p5 day-block"
                v-for="(w, index) in daysHeader"
                :key="index"
              >
                {{ w }}
              </div>
            </div>
            <div class="day-container">
              <div class="row day-row pull-left" v-if="currentTab === 'D'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in days"
                  :key="index"
                  @click="setDate(d.day, selectedDate['month'])"
                  v-bind:class="{
                    active: selectedDate['day'] === d.day,
                    custom_disable: d.day < 0,
                  }"
                >
                  {{ d.day < 0 ? -d.day : d.day }}
                </div>
              </div>
              <div class="calender-custom pull-right" v-if="currentTab === 'D'">
                <div class="pull-left calender-div"></div>
                <div class="pull-right">
                  <div
                    class="cal-custom-day"
                    v-if="enableTabs('C')"
                    @click="quickChoose('today')"
                    v-bind:class="{
                      active: time[0] === today[0] && time[1] === today[1],
                    }"
                  >
                    {{ $t('common.date_picker.today') }}
                  </div>
                  <div
                    class="cal-custom-day"
                    v-if="enableTabs('C')"
                    @click="quickChoose('yesterday')"
                    v-bind:class="{
                      active:
                        time[0] === yesterday[0] && time[1] === yesterday[1],
                    }"
                  >
                    {{ $t('common.date_picker.yesterday') }}
                  </div>
                </div>
              </div>
            </div>
            <div class="day-container">
              <div class="row day-row pull-left" v-if="currentTab === 'W'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in days"
                  :key="index"
                  @click="
                    setWeek(d.day, selectedDate['month'], selectedDate['year'])
                  "
                  v-bind:class="{
                    active:
                      computeWeek(d.year, d.month, Math.abs(d.day)) ===
                      selectedWeek,
                    custom_disable: d.day < 0,
                  }"
                >
                  {{ d.day < 0 ? -d.day : d.day }}
                </div>
              </div>
              <div class="calender-custom pull-right" v-if="currentTab === 'W'">
                <div class="pull-left calender-div"></div>
                <div class="pull-right">
                  <div
                    class="cal-custom-day"
                    v-if="enableTabs('C')"
                    @click="quickChoose('thisweek')"
                    v-bind:class="{
                      active:
                        time[0] === thisWeek[0] && time[1] === thisWeek[1],
                    }"
                  >
                    {{ $t('common.date_picker.this_week') }}
                  </div>
                  <div
                    class="cal-custom-day"
                    v-if="enableTabs('C')"
                    @click="quickChoose('lastweek')"
                    v-bind:class="{
                      active:
                        time[0] === lastWeek[0] && time[1] === lastWeek[1],
                    }"
                  >
                    {{ $t('common.date_picker.last_week') }}
                  </div>
                </div>
              </div>
            </div>

            <div
              v-if="currentTab === 'M' || currentTab === 'Y'"
              class="row month-row"
            >
              <div class="year-section">
                <div
                  class="p5 year"
                  v-for="y in years"
                  :key="y"
                  v-bind:class="{ active: selectedYear === y }"
                  @click="setYear(y)"
                >
                  {{ y }}
                </div>
              </div>
              <div class="col-5 month-container" v-if="currentTab === 'M'">
                <div class="row">
                  <div
                    class="col-6 p5 month"
                    v-for="m in months.slice(0, 6)"
                    :key="m.id"
                    @click="setMonth(m.id, m.label, selectedYear)"
                    v-bind:class="{ active: selectedMonth === m.id }"
                  >
                    {{ m.label }}
                  </div>
                  <div
                    class="col-6 p5 month"
                    v-for="m in months.slice(6, 12)"
                    :key="m.id"
                    @click="setMonth(m.id, m.label, selectedYear)"
                    v-bind:class="{ active: selectedMonth === m.id }"
                  >
                    {{ m.label }}
                  </div>
                </div>
              </div>
              <div class="calender-custom col-4" v-if="currentTab === 'M'">
                <div
                  class="cal-custom-day"
                  v-if="enableTabs('C')"
                  @click="quickChoose('thismonth')"
                  v-bind:class="{
                    active:
                      time[0] === thisMonth[0] && time[1] === thisMonth[1],
                  }"
                >
                  {{ $t('common.date_picker.this_month') }}
                </div>
                <div
                  class="cal-custom-day"
                  v-if="enableTabs('C')"
                  @click="quickChoose('lastmonth')"
                  v-bind:class="{
                    active:
                      time[0] === lastMonth[0] && time[1] === lastMonth[1],
                  }"
                >
                  {{ $t('common.date_picker.last_month') }}
                </div>
              </div>
            </div>
          </div>
          <div class="calender-range-section" v-if="currentTab === 'R'">
            <div class="from-table">
              <div class="row calenderheader-row" v-if="currentTab === 'R'">
                <div class="p5" @click="rangeNavPre('from')">
                  <i class="el-icon-arrow-left"></i>
                </div>
                <div class="p5">{{ months[rangeObj['fromMonth']].label }}</div>
                <div class="p5">{{ rangeObj['fromYear'] }}</div>
                <div class="p5" @click="rangeNavNext('from')">
                  <i class="el-icon-arrow-right"></i>
                </div>
              </div>
              <div class="row week-row" v-if="currentTab === 'R'">
                <div class="p5 day-block" v-for="w in daysHeader" :key="w">
                  {{ w }}
                </div>
              </div>
              <div class="row day-row" v-if="currentTab === 'R'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in rangeObj.fromDays"
                  :key="index"
                  @click="setRange('from', d.day)"
                  v-bind:class="{
                    active: rangeObj.fromDay === d.day,
                    custom_disable: d.day < 0,
                  }"
                >
                  {{ d.day < 0 ? -d.day : d.day }}
                </div>
              </div>
              <!-- <el-time-picker
 class="timePicker"
   v-model="rangeObj['fromTime']">
  </el-time-picker> -->
            </div>
            <div class="to-table">
              <div class="row calenderheader-row" v-if="currentTab === 'R'">
                <div class="p5" @click="rangeNavPre('to')">
                  <i class="el-icon-arrow-left"></i>
                </div>
                <div class="p5">{{ months[rangeObj['toMonth']].label }}</div>
                <div class="p5">{{ rangeObj['toYear'] }}</div>
                <div class="p5" @click="rangeNavNext('to')">
                  <i class="el-icon-arrow-right"></i>
                </div>
              </div>
              <div class="row week-row" v-if="currentTab === 'R'">
                <div class="p5 day-block" v-for="w in daysHeader" :key="w">
                  {{ w }}
                </div>
              </div>
              <div class="row day-row" v-if="currentTab === 'R'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in rangeObj.toDays"
                  :key="index"
                  @click="setRange('to', d.day)"
                  v-bind:class="{
                    active: rangeObj.toDay === d.day,
                    disabled:
                      computeMilliSeconds(
                        rangeObj.toYear,
                        rangeObj.toMonth,
                        d.day,
                        'start'
                      ) <
                        computeMilliSeconds(
                          rangeObj.fromYear,
                          rangeObj.fromMonth,
                          rangeObj.fromDay,
                          'start'
                        ) || d.day < 0,
                  }"
                >
                  {{ d.day < 0 ? -d.day : d.day }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="row datepic-btn-row">
          <el-button @click="returnDate()" class="pull-right cal-ok-btn">{{
            $t('common._common.ok')
          }}</el-button>
        </div>
      </el-popover>
      <div class="button-row" :class="[{ 'picker-disabled': disabled }]">
        <div v-if="!isDateFixed" class="cal-left-btn" @click="navPrevious()">
          <i class="el-icon-arrow-left date-arrow"></i>
        </div>
        <el-button
          v-if="!isDateFixed"
          :disabled="hidePopover || isDateFixed || disabled"
          v-popover:popover4
          >{{ result.label }}</el-button
        >
        <el-button v-else>{{ result.label }}</el-button>
        <div
          v-if="!isDateFixed && !isCurrentDateRange"
          class="cal-right-btn"
          @click="navNext()"
        >
          <i class="el-icon-arrow-right date-arrow"></i>
        </div>
      </div>
    </div>
    <div class="mobile-new-date-filter" v-else>{{ result.label }}</div>
  </div>
</template>

<script>
import NewDateHelper from './NewDateHelper'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import dateoperators from '@/mixins/NewDateHelper'
import Vue from 'vue'

export default {
  data() {
    return {
      currentTab: 'C',
      days: [],
      months: [
        {
          id: 0,
          label: this.$t('common.date_picker.jan'),
        },
        {
          id: 1,
          label: this.$t('common.date_picker.feb'),
        },
        {
          id: 2,
          label: this.$t('common.date_picker.mar'),
        },
        {
          id: 3,
          label: this.$t('common.date_picker.apr'),
        },
        {
          id: 4,
          label: this.$t('common.date_picker.may'),
        },
        {
          id: 5,
          label: this.$t('common.date_picker.jun'),
        },
        {
          id: 6,
          label: this.$t('common.date_picker.jul'),
        },
        {
          id: 7,
          label: this.$t('common.date_picker.aug'),
        },
        {
          id: 8,
          label: this.$t('common.date_picker.sep'),
        },
        {
          id: 9,
          label: this.$t('common.date_picker.oct'),
        },
        {
          id: 10,
          label: this.$t('common.date_picker.nov'),
        },
        {
          id: 11,
          label: this.$t('common.date_picker.dec'),
        },
      ],
      datePicker: false,
      isRange: false,
      daysHeader: ['Su', 'M', 'Tu', 'W', 'Th', 'F', 'Sa'],
      years: [],
      timeZone: null,
      firstTime: true,
      selectedDate: {},
      selectedWeek: null,
      selectedMonth: null,
      selectedMonthLabel: null,
      selectedYear: null,
      numberOfDays: 0,
      timeinMills: [],
      customCaseSection: {},
      dateOperators: dateoperators.dateOperators(),
      time: [], // comprises start and end time
      quarters: [
        {
          label: 'Q1',
          id: 1,
          startMonth: 0,
          endMonth: 2,
        },
        {
          label: 'Q2',
          id: 2,
          startMonth: 3,
          endMonth: 5,
        },
        {
          label: 'Q3',
          id: 3,
          startMonth: 6,
          endMonth: 8,
        },
        {
          label: 'Q4',
          id: 4,
          startMonth: 9,
          endMonth: 11,
        },
      ],
      selectedQuarter: {},
      rangeObj: {},
      result: null,
    }
  },
  props: {
    dateObj: {},
    zone: {},
    hidePopover: {},
    isDateFixed: {},
    tabs: {},
    disableFutureDate: {},
    disabled: {},
  },
  created() {
    if (this.activeTab) {
      this.currentTab = this.activeTab
    }
    this.setTimeZone()
    if (this.dateObj !== null && typeof this.dateObj !== 'undefined') {
      if (this.wellFormed()) {
        this.loadFromObject()
      } else {
        this.loadDefaults()
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .startOf('day')
            .valueOf()
        )
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .endOf('day')
            .valueOf()
        )
        this.loadRangeObject()
      }
    } else {
      this.loadDefaults()
      this.time.push(
        moment
          .tz(
            [
              this.selectedDate['year'],
              this.selectedDate['month'],
              this.selectedDate['day'],
            ],
            this.timeZone
          )
          .startOf('day')
          .valueOf()
      )
      this.time.push(
        moment
          .tz(
            [
              this.selectedDate['year'],
              this.selectedDate['month'],
              this.selectedDate['day'],
            ],
            this.timeZone
          )
          .endOf('day')
          .valueOf()
      )
      this.loadRangeObject()
    }
    let startYear = null
    if (typeof this.tabs !== 'undefined') {
      if (this.tabs.loadAdditional) {
        let additionalInfo = this.tabs.loadAdditional.year
        startYear = dateoperators.computeMoment(additionalInfo, this.timeZone)
      } else {
        startYear = moment.tz(this.timeZone).year()
      }
    } else {
      startYear = moment.tz(this.timeZone).year()
    }
    for (let i = startYear; i > 1969; i--) {
      this.years.push(i)
    }
  },
  computed: {
    activeTab: function() {
      if (typeof this.tabs !== 'undefined') {
        if (this.tabs.enableByOperationOnId) {
          let currentTab = null
          switch (this.dateObj.operationOn) {
            case 'day': {
              currentTab = 'D'
              break
            }
            case 'week': {
              currentTab = 'W'
              break
            }
            case 'month': {
              currentTab = 'M'
              break
            }
            case 'year': {
              currentTab = 'Y'
              break
            }
            case 'quarter': {
              currentTab = 'Q'
              break
            }
            default: {
              currentTab = 'C'
              break
            }
          }
          return currentTab
        }
        return null
      }
      return null
    },
    isCurrentDateRange: function() {
      let currentmillis = moment.tz(this.timeZone).valueOf()
      let {
        result: { value, operatorId },
      } = this
      if (isEmpty(value)) {
        value = NewDateHelper.getDatePickerObject(operatorId).value
      }
      if (currentmillis < value[1] && this.disableFutureDate === true) {
        return true
      }
      return false
    },
    today: function() {
      return [
        moment
          .tz(this.timeZone)
          .startOf('day')
          .valueOf(),
        moment
          .tz(this.timeZone)
          .endOf('day')
          .valueOf(),
      ]
    },
    todayUptoNow: function() {
      let startOfYear = moment
        .tz(this.timeZone)
        .startOf('day')
        .valueOf()
      let endOfYear = moment.tz(this.timeZone).valueOf()
      return [startOfYear, endOfYear]
    },
    yesterday: function() {
      return [
        moment
          .tz(this.timeZone)
          .subtract(1, 'day')
          .startOf('day')
          .valueOf(),
        moment
          .tz(this.timeZone)
          .subtract(1, 'day')
          .endOf('day')
          .valueOf(),
      ]
    },
    lastWeek: function() {
      let startOfWeek = moment
        .tz(this.timeZone)
        .subtract(1, 'week')
        .startOf('week')
        .valueOf()
      let endOfWeek = moment
        .tz(this.timeZone)
        .subtract(1, 'week')
        .endOf('week')
        .valueOf()
      return [startOfWeek, endOfWeek]
    },
    thisWeek: function() {
      let startOfWeek = moment
        .tz(this.timeZone)
        .startOf('week')
        .valueOf()
      let endOfWeek = moment
        .tz(this.timeZone)
        .endOf('week')
        .valueOf()
      return [startOfWeek, endOfWeek]
    },
    thisMonth: function() {
      let startOfMonth = moment
        .tz(this.timeZone)
        .startOf('month')
        .valueOf()
      let endOfMonth = moment
        .tz(this.timeZone)
        .endOf('month')
        .valueOf()
      return [startOfMonth, endOfMonth]
    },
    thisMonthTillYesterday: function() {
      let startOfMonth = moment
        .tz(this.timeZone)
        .startOf('month')
        .valueOf()
      if (
        moment
          .tz(this.timeZone)
          .startOf('day')
          .valueOf() ===
        moment
          .tz(this.timeZone)
          .startOf('month')
          .valueOf()
      ) {
        startOfMonth = moment
          .tz(this.timeZone)
          .subtract(1, 'month')
          .startOf('month')
          .valueOf()
      }
      let endOfMonth = moment
        .tz(this.timeZone)
        .subtract(1, 'day')
        .endOf('day')
        .valueOf()
      return [startOfMonth, endOfMonth]
    },
    lastMonth: function() {
      let startOfMonth = moment
        .tz(this.timeZone)
        .subtract(1, 'month')
        .startOf('month')
        .valueOf()
      let endOfMonth = moment
        .tz(this.timeZone)
        .subtract(1, 'month')
        .endOf('month')
        .valueOf()
      return [startOfMonth, endOfMonth]
    },
    thisYear: function() {
      let startOfYear = moment
        .tz(this.timeZone)
        .startOf('year')
        .valueOf()
      let endOfYear = moment
        .tz(this.timeZone)
        .endOf('year')
        .valueOf()
      return [startOfYear, endOfYear]
    },
    lastYear: function() {
      let startOfYear = moment
        .tz(this.timeZone)
        .subtract(1, 'year')
        .startOf('year')
        .valueOf()
      let endOfYear = moment
        .tz(this.timeZone)
        .subtract(1, 'year')
        .endOf('year')
        .valueOf()
      return [startOfYear, endOfYear]
    },
    thisYearuptoNow: function() {
      let startOfYear = moment
        .tz(this.timeZone)
        .startOf('year')
        .valueOf()
      let endOfYear = moment.tz(this.timeZone).valueOf()
      return [startOfYear, endOfYear]
    },
    thisWeekuptoNow: function() {
      let startOfYear = moment
        .tz(this.timeZone)
        .startOf('week')
        .valueOf()
      let endOfYear = moment.tz(this.timeZone).valueOf()
      return [startOfYear, endOfYear]
    },
    thisMonthuptoNow: function() {
      let startOfYear = moment
        .tz(this.timeZone)
        .startOf('month')
        .valueOf()
      let endOfYear = moment.tz(this.timeZone).valueOf()
      return [startOfYear, endOfYear]
    },
    thisQuarter: function() {
      let startOfQuarter = moment
        .tz(this.timeZone)
        .startOf('quarter')
        .valueOf()
      let endOfQuarter = moment
        .tz(this.timeZone)
        .endOf('quarter')
        .valueOf()
      return [startOfQuarter, endOfQuarter]
    },
    lastQuarter: function() {
      let startOfQuarter = moment
        .tz(this.timeZone)
        .subtract(1, 'quarter')
        .startOf('quarter')
        .valueOf()
      let endOfQuarter = moment
        .tz(this.timeZone)
        .subtract(1, 'quarter')
        .endOf('quarter')
        .valueOf()
      return [startOfQuarter, endOfQuarter]
    },
  },
  methods: {
    enableTabs(tabName) {
      if (typeof this.tabs !== 'undefined') {
        let enabledTabs = this.tabs.enabledTabs
        if (enabledTabs.includes(tabName)) {
          return true
        }
        return false
      }
      return true
    },
    openDatePicker() {
      this.datePicker = !this.datePicker
    },
    navNext() {
      dateoperators.updateStartOfWeekInMoment(Vue.prototype.$org)
      let startTime = null
      let endTime = null
      if (this.result.offset === -1) {
        startTime = moment
          .tz(this.result.value[0], this.timeZone)
          .add(Math.abs(this.result.offset + 2), this.result.operationOn)
          .startOf(this.result.operationOn)
          .valueOf()
        endTime = moment
          .tz(startTime, this.timeZone)
          .endOf(this.result.operationOn)
          .valueOf()
      } else if (this.result.offset === 0) {
        let timeStamp = this.result.value[0]
        if (this.result.operatorId === 72 || this.result.id === 25) {
          timeStamp = this.result.value[1]
        }
        startTime = moment
          .tz(timeStamp, this.timeZone)
          .add(Math.abs(this.result.offset + 1), this.result.operationOn)
          .startOf(this.result.operationOn)
          .valueOf()
        endTime = moment
          .tz(startTime, this.timeZone)
          .add(Math.abs(this.result.offset), this.result.operationOn)
          .endOf(this.result.operationOn)
          .valueOf()
      } else {
        startTime = moment
          .tz(this.result.value[0], this.timeZone)
          .add(Math.abs(this.result.offset + 1), this.result.operationOn)
          .startOf(this.result.operationOn)
          .valueOf()
        endTime = moment
          .tz(startTime, this.timeZone)
          .add(Math.abs(this.result.offset + 1), this.result.operationOn)
          .endOf(this.result.operationOn)
          .valueOf()
      }
      this.result.value = []
      this.result.value = [startTime, endTime]
      this.dateObj = this.$helpers.cloneObject(this.result)
      if (this.result.operationOn === 'day' && this.result.operatorId !== 49) {
        this.loadFromObject()
      } else if (
        this.result.operationOn === 'week' &&
        this.result.operatorId !== 50
      ) {
        this.loadFromObject()
      } else if (
        this.result.operationOn === 'month' &&
        this.result.operatorId !== 51
      ) {
        this.loadFromObject()
      } else if (this.result.operationOn === 'year') {
        this.loadFromObject()
      } else if (this.result.operationOn === 'quarter') {
        this.loadFromObject()
      } else {
        this.currentTab = 'R'
        this.result.operatorId = 20
        this.loadRangeObject()
        this.returnDate()
      }
    },
    navPrevious() {
      dateoperators.updateStartOfWeekInMoment(Vue.prototype.$org)
      let startTime = null
      let endTime = null
      if (this.result.offset === -1) {
        startTime = moment
          .tz(this.result.value[0], this.timeZone)
          .subtract(Math.abs(this.result.offset + 2), this.result.operationOn)
          .startOf(this.result.operationOn)
          .valueOf()
        endTime = moment
          .tz(startTime, this.timeZone)
          .endOf(this.result.operationOn)
          .valueOf()
      } else if (this.result.offset === 0) {
        let timeStamp = this.result.value[0]
        if (this.result.operatorId === 72 || this.result.id === 25) {
          timeStamp = this.result.value[1]
        }
        startTime = moment
          .tz(timeStamp, this.timeZone)
          .subtract(Math.abs(this.result.offset + 1), this.result.operationOn)
          .startOf(this.result.operationOn)
          .valueOf()
        endTime = moment
          .tz(startTime, this.timeZone)
          .add(Math.abs(this.result.offset), this.result.operationOn)
          .endOf(this.result.operationOn)
          .valueOf()
      } else {
        startTime = moment
          .tz(this.result.value[0], this.timeZone)
          .subtract(Math.abs(this.result.offset + 1), this.result.operationOn)
          .startOf(this.result.operationOn)
          .valueOf()
        endTime = moment
          .tz(startTime, this.timeZone)
          .add(Math.abs(this.result.offset + 1), this.result.operationOn)
          .endOf(this.result.operationOn)
          .valueOf()
      }
      this.result.value = []
      this.result.value = [startTime, endTime]
      this.dateObj = this.$helpers.cloneObject(this.result)
      if (this.result.operationOn === 'day' && this.result.operatorId !== 49) {
        this.loadFromObject()
      } else if (
        this.result.operationOn === 'week' &&
        this.result.operatorId !== 50
      ) {
        this.loadFromObject()
      } else if (
        this.result.operationOn === 'month' &&
        this.result.operatorId !== 51
      ) {
        this.loadFromObject()
      } else if (this.result.operationOn === 'year') {
        this.loadFromObject()
      } else if (this.result.operationOn === 'quarter') {
        this.loadFromObject()
      } else {
        this.result.operatorId = 20
        this.currentTab = 'R'
        this.loadRangeObject()
        this.returnDate()
      }
    },
    setTimeZone() {
      if (this.zone !== null && typeof this.zone !== 'undefined') {
        this.timeZone = this.zone
      } else {
        this.timeZone = moment.tz.guess()
      }
    },
    computeMilliSeconds(year, month, day, decider) {
      if (decider === 'start') {
        return moment
          .tz([year, month, day], this.timeZone)
          .startOf('day')
          .valueOf()
      } else {
        return moment
          .tz([year, month, day], this.timeZone)
          .endOf('day')
          .valueOf()
      }
    },
    computeWeek(year, month, date) {
      return moment.tz([year, month, date], this.timeZone).week()
    },
    quickChoose(period) {
      if (period === 'today') {
        this.$set(this.selectedDate, 'day', moment.tz(this.timeZone).date())
        this.$set(this.selectedDate, 'month', moment.tz(this.timeZone).month())
        this.$set(this.selectedDate, 'year', moment.tz(this.timeZone).year())
        this.time = []
        this.time = this.today
        this.loadDays()
      } else if (period === 'yesterday') {
        this.$set(
          this.selectedDate,
          'day',
          moment
            .tz(this.timeZone)
            .subtract(1, 'day')
            .date()
        )
        this.$set(
          this.selectedDate,
          'month',
          moment
            .tz(this.timeZone)
            .subtract(1, 'day')
            .month()
        )
        this.$set(
          this.selectedDate,
          'year',
          moment
            .tz(this.timeZone)
            .subtract(1, 'day')
            .year()
        )
        this.time = []
        this.time = this.yesterday
        this.loadDays()
      } else if (period === 'thisweek') {
        this.setWeek(
          moment.tz(this.thisWeek[0], this.timeZone).date(),
          moment.tz(this.thisWeek[0], this.timeZone).month(),
          moment.tz(this.thisWeek[0], this.timeZone).year()
        )
      } else if (period === 'lastweek') {
        this.setWeek(
          moment.tz(this.lastWeek[0], this.timeZone).date(),
          moment.tz(this.lastWeek[0], this.timeZone).month(),
          moment.tz(this.lastWeek[0], this.timeZone).year()
        )
      } else if (period === 'thismonth') {
        this.setMonth(
          moment.tz(this.thisMonth[0], this.timeZone).month(),
          this.months[moment.tz(this.thisMonth[0], this.timeZone).month()]
            .label,
          moment.tz(this.thisMonth[0], this.timeZone).year()
        )
      } else if (period === 'lastmonth') {
        this.setMonth(
          moment.tz(this.lastMonth[0], this.timeZone).month(),
          this.months[moment.tz(this.lastMonth[0], this.timeZone).month()]
            .label,
          moment.tz(this.lastMonth[0], this.timeZone).year()
        )
      } else if (period === 'thisquarter') {
        this.setQuarter(
          moment.tz(this.timeZone).quarter(),
          moment
            .tz(
              moment
                .tz(this.timeZone)
                .startOf('quarter')
                .valueOf(),
              this.timeZone
            )
            .year()
        )
      } else if (period === 'lastquarter') {
        this.setQuarter(
          moment
            .tz(this.timeZone)
            .subtract(1, 'quarter')
            .quarter(),
          moment
            .tz(
              moment
                .tz(this.timeZone)
                .subtract(1, 'quarter')
                .startOf('quarter')
                .valueOf(),
              this.timeZone
            )
            .year()
        )
      }
      const { operatorId } = dateoperators.getOperationFromLabel(period)
      this.customCaseSection = dateoperators.getDatePickerObject(operatorId)
    },
    loadRangeObject() {
      if (
        this.dateObj !== null &&
        typeof this.dateObj !== 'undefined' &&
        this.dateObj.value &&
        this.dateObj.value.length
      ) {
        this.$set(
          this.rangeObj,
          'fromDay',
          moment.tz(this.dateObj.value[0], this.timeZone).date()
        )
        this.$set(
          this.rangeObj,
          'fromMonth',
          moment.tz(this.dateObj.value[0], this.timeZone).month()
        )
        this.$set(
          this.rangeObj,
          'fromYear',
          moment.tz(this.dateObj.value[0], this.timeZone).year()
        )
        this.$set(
          this.rangeObj,
          'toDay',
          moment.tz(this.dateObj.value[1], this.timeZone).date()
        )
        this.$set(
          this.rangeObj,
          'toMonth',
          moment.tz(this.dateObj.value[1], this.timeZone).month()
        )
        this.$set(
          this.rangeObj,
          'toYear',
          moment.tz(this.dateObj.value[1], this.timeZone).year()
        )
        this.$set(this.rangeObj, 'from', this.dateObj.value[0])
        this.$set(this.rangeObj, 'to', this.dateObj.value[1])
        this.$set(this.rangeObj, 'fromDays', [])
        this.$set(this.rangeObj, 'toDays', [])
        this.loadRangeDays('from')
        this.loadRangeDays('to')
      } else {
        this.$set(this.rangeObj, 'fromDay', this.selectedDate['day'])
        this.$set(this.rangeObj, 'fromMonth', this.selectedDate['month'])
        this.$set(this.rangeObj, 'fromYear', this.selectedDate['year'])
        this.$set(this.rangeObj, 'toDay', this.selectedDate['day'])
        this.$set(this.rangeObj, 'toMonth', this.selectedDate['month'])
        this.$set(this.rangeObj, 'toYear', this.selectedDate['year'])
        this.$set(this.rangeObj, 'from', this.time[0])
        this.$set(this.rangeObj, 'to', this.time[1])
        this.$set(this.rangeObj, 'fromDays', [])
        this.$set(this.rangeObj, 'toDays', [])
        // this.$set(this.rangeObj, 'fromTime', new Date(this.rangeObj.fromYear, this.rangeObj.fromMonth, this.rangeObj.fromDay, 0, 0))
        // this.$set(this.rangeObj, 'toTime', new Date(this.rangeObj.toYear, this.rangeObj.toMonth, this.rangeObj.toDay, 23, 59))
        this.loadRangeDays('from')
        this.loadRangeDays('to')
      }
    },
    loadRangeDays(decider) {
      this.rangeObj[decider + 'Days'] = []
      let numberOfDaysInAMonth = moment([
        this.rangeObj[decider + 'Year'],
        this.rangeObj[decider + 'Month'],
      ]).daysInMonth()
      let dayOfTheWeek = moment
        .tz(
          [
            this.rangeObj[decider + 'Year'],
            this.rangeObj[decider + 'Month'],
            this.rangeObj[decider + 'Day'],
          ],
          this.timeZone
        )
        .startOf('month')
        .format('dd')
      let offset = this.daysHeader.indexOf(dayOfTheWeek)
      if (offset === -1) {
        offset = this.daysHeader.indexOf(dayOfTheWeek.slice(0, 1))
      }
      let numberOfDaysInLastMonth = moment
        .tz(
          [
            this.rangeObj[decider + 'Month'] - 1 === -1
              ? this.rangeObj[decider + 'Year'] - 1
              : this.rangeObj[decider + 'Year'],
            this.rangeObj[decider + 'Month'] - 1 === -1
              ? 11
              : this.rangeObj[decider + 'Month'] - 1,
          ],
          this.timeZone
        )
        .daysInMonth()
      let daysInLastMonth = []
      for (let j = 1; j <= numberOfDaysInLastMonth; j++) {
        daysInLastMonth.push(j * -1)
      }
      let daysFromPreviousMonth = daysInLastMonth.slice(offset * -1)
      for (let j = 0; j < offset; j++) {
        let temp = {}
        temp['day'] = daysFromPreviousMonth[j]
        temp['month'] =
          this.rangeObj[decider + 'Month'] - 1 === -1
            ? 11
            : this.rangeObj[decider + 'Month'] - 1
        temp['year'] =
          this.rangeObj[decider + 'Month'] - 1 === -1
            ? this.rangeObj[decider + 'Year'] - 1
            : this.rangeObj[decider + 'Year']
        this.rangeObj[decider + 'Days'].push(temp)
      }
      for (let i = 1; i < numberOfDaysInAMonth + 1; i++) {
        let temp = {}
        temp['day'] = i
        temp['month'] = this.rangeObj[decider + 'Month']
        temp['year'] = this.rangeObj[decider + 'Year']
        this.rangeObj[decider + 'Days'].push(temp)
      }
      let existing = this.rangeObj[decider + 'Days']
      dayOfTheWeek = moment
        .tz(
          [
            this.rangeObj[decider + 'Year'],
            this.rangeObj[decider + 'Month'],
            existing[existing.length - 1].day,
          ],
          this.timeZone
        )
        .format('dd')
      offset = this.daysHeader.indexOf(dayOfTheWeek)
      if (offset === -1) {
        offset = this.daysHeader.indexOf(dayOfTheWeek.slice(0, 1))
      }
      let remaining = this.daysHeader.length - offset
      for (let i = 1; i < remaining; i++) {
        let temp = {}
        temp['day'] = i * -1
        temp['month'] = this.rangeObj[decider + 'Month'] + 1
        temp['year'] = this.rangeObj[decider + 'Year']
        this.rangeObj[decider + 'Days'].push(temp)
      }
    },
    rangeNavPre(decider) {
      let timeUpdate = moment
        .tz(this.rangeObj[decider], this.timeZone)
        .subtract(1, 'month')
        .startOf('month')
        .valueOf()
      this.rangeObj[decider] = timeUpdate
      this.$set(
        this.rangeObj,
        decider + 'Day',
        moment.tz(this.rangeObj[decider], this.timeZone).date()
      )
      this.$set(
        this.rangeObj,
        decider + 'Month',
        moment.tz(this.rangeObj[decider], this.timeZone).month()
      )
      this.$set(
        this.rangeObj,
        decider + 'Year',
        moment.tz(this.rangeObj[decider], this.timeZone).year()
      )
      this.loadRangeDays(decider)
    },
    rangeNavNext(decider) {
      let timeUpdate = moment
        .tz(this.rangeObj[decider], this.timeZone)
        .add(1, 'month')
        .startOf('month')
        .valueOf()
      this.rangeObj[decider] = timeUpdate
      this.$set(
        this.rangeObj,
        decider + 'Day',
        moment.tz(this.rangeObj[decider], this.timeZone).date()
      )
      this.$set(
        this.rangeObj,
        decider + 'Month',
        moment.tz(this.rangeObj[decider], this.timeZone).month()
      )
      this.$set(
        this.rangeObj,
        decider + 'Year',
        moment.tz(this.rangeObj[decider], this.timeZone).year()
      )
      this.loadRangeDays(decider)
    },
    wellFormed() {
      if (
        typeof this.dateObj.operatorId !== 'undefined' &&
        this.dateObj.operatorId !== null &&
        typeof this.dateObj.operationOnId !== 'undefined' &&
        this.dateObj.operationOnId !== null
      ) {
        return true
      } else {
        return false
      }
    },
    loadDefaults() {
      this.$set(this.selectedDate, 'day', moment.tz(this.timeZone).date())
      this.$set(this.selectedDate, 'month', moment.tz(this.timeZone).month())
      this.$set(this.selectedDate, 'year', moment.tz(this.timeZone).year())
      this.selectedMonth = moment.tz(this.timeZone).month()
      this.selectedYear = moment.tz(this.timeZone).year()
      this.selectedWeek = moment.tz(this.timeZone).week()
      this.$set(this.selectedQuarter, 'year', this.selectedYear)
      this.$set(
        this.selectedQuarter,
        'quarter',
        moment.tz(this.timeZone).quarter()
      )
      this.result = dateoperators.returnOperator(22, null)
      if (this.result.value && this.today) {
        this.$set(this.result, 'value', this.today)
      }
      this.loadDays()
    },
    loadCustomCaseFromDate() {
      if (!('value' in this.dateObj)) {
        let operationDefaults = dateoperators.returnOperator(
          this.dateObj.operatorId
        )
        for (let key in Object.keys(operationDefaults)) {
          this.$set(
            this.customCaseSection,
            Object.keys(operationDefaults)[key],
            operationDefaults[Object.keys(operationDefaults)[key]]
          )
        }
        // this.customCaseSection.value = dateoperators.returnOperator(this.dateObj.operatorId, this.timeZone)
      } else {
        for (let key in Object.keys(this.dateObj)) {
          let property = Object.keys(this.dateObj)[key]
          this.$set(this.customCaseSection, property, this.dateObj[property])
        }
      }
    },
    loadFromObject() {
      if (dateoperators.returnOperator(this.dateObj.operatorId) !== null) {
        this.customCaseSection = {}
        this.loadCustomCaseFromDate()
        if (this.activeTab) {
          this.currentTab = this.activeTab
        } else {
          this.currentTab = dateoperators.getTabFromOperation(
            this.dateObj.operationOnId,
            this.dateObj.offset
          )
        }
        if (this.customCaseSection.value === null) {
          this.customCaseSection = dateoperators.calculateCustomCaseValue(
            this.customCaseSection,
            this.timeZone,
            this.$org
          )
          this.loadFromMilliseconds(this.customCaseSection.value[0])
          this.dateObj = this.customCaseSection
          this.loadRangeObject()
        } else {
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.loadRangeObject()
        }
        this.time = this.customCaseSection.value
      } else {
        if (this.dateObj.operationOnId === 1) {
          this.currentTab =
            this.activeTab && this.tabs.enableOperationOnId
              ? this.activeTab
              : 'D'
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.setDate(
            moment.tz(this.dateObj.value[0], this.timeZone).date(),
            moment.tz(this.dateObj.value[0], this.timeZone).month()
          )
        } else if (this.dateObj.operationOnId === 2) {
          this.currentTab =
            this.activeTab && this.tabs.enableOperationOnId
              ? this.activeTab
              : 'W'
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.setWeek(
            moment.tz(this.dateObj.value[0], this.timeZone).date(),
            moment.tz(this.dateObj.value[0], this.timeZone).month(),
            moment.tz(this.dateObj.value[0], this.timeZone).year()
          )
        } else if (this.dateObj.operationOnId === 3) {
          this.currentTab =
            this.activeTab && this.tabs.enableOperationOnId
              ? this.activeTab
              : 'M'
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.setMonth(
            moment.tz(this.dateObj.value[0], this.timeZone).month(),
            this.months[moment.tz(this.dateObj.value[0], this.timeZone).month()]
              .label,
            moment.tz(this.dateObj.value[0], this.timeZone).year()
          )
        } else if (this.dateObj.operationOnId === 4) {
          this.currentTab =
            this.activeTab && this.tabs.enableOperationOnId
              ? this.activeTab
              : 'Y'
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.setYear(moment(this.dateObj.value[0]).year())
        } else if (this.dateObj.operationOnId === 5) {
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.currentTab =
            this.activeTab && this.tabs.enableOperationOnId
              ? this.activeTab
              : 'Q'
          this.setQuarter(
            moment.tz(this.dateObj.value[0], this.timeZone).quarter(),
            moment.tz(this.dateObj.value[0], this.timeZone).year()
          )
        } else if (this.dateObj.operationOnId === 6) {
          this.loadFromMilliseconds(this.dateObj.value[0])
          this.currentTab = 'R'
          this.loadRangeObject()
        }
      }
      this.result = this.dateObj
      this.loadDays()
      this.returnDate()
    },
    loadFromMilliseconds(startTime) {
      this.$set(
        this.selectedDate,
        'day',
        moment.tz(startTime, this.timeZone).date()
      )
      this.$set(
        this.selectedDate,
        'month',
        moment.tz(startTime, this.timeZone).month()
      )
      this.$set(
        this.selectedDate,
        'year',
        moment.tz(startTime, this.timeZone).year()
      )
      this.selectedWeek = moment.tz(startTime, this.timeZone).week()
      this.selectedMonth = this.selectedDate['month']
      this.selectedYear = this.selectedDate['year']
      this.$set(
        this.selectedQuarter,
        'year',
        moment.tz(startTime, this.timeZone).year()
      )
      this.$set(
        this.selectedQuarter,
        'quarter',
        moment.tz(startTime, this.timeZone).quarter()
      )
    },
    loadDays() {
      this.days = []
      let dayOfTheWeek = moment
        .tz(
          [this.selectedDate['year'], this.selectedDate['month']],
          this.timeZone
        )
        .startOf('month')
        .format('dd')
      let offset = this.daysHeader.indexOf(dayOfTheWeek)
      if (offset === -1) {
        offset = this.daysHeader.indexOf(dayOfTheWeek.slice(0, 1))
      }
      this.numberOfDays = moment
        .tz(
          [this.selectedDate['year'], this.selectedDate['month']],
          this.timeZone
        )
        .daysInMonth()
      let numberOfDaysInLastMonth = moment
        .tz(
          [
            this.selectedDate['month'] - 1 === -1
              ? this.selectedDate['year'] - 1
              : this.selectedDate['year'],
            this.selectedDate['month'] - 1 === -1
              ? 11
              : this.selectedDate['month'] - 1,
          ],
          this.timeZone
        )
        .daysInMonth()
      let daysInLastMonth = []
      for (let j = 1; j <= numberOfDaysInLastMonth; j++) {
        daysInLastMonth.push(j * -1)
      }
      let daysFromPreviousMonth = daysInLastMonth.slice(offset * -1)
      for (let j = 0; j < offset; j++) {
        let temp = {}
        temp['day'] = daysFromPreviousMonth[j]
        temp['month'] =
          this.selectedDate['month'] - 1 === -1
            ? 11
            : this.selectedDate['month'] - 1
        temp['year'] =
          this.selectedDate['month'] - 1 === -1
            ? this.selectedDate['year'] - 1
            : this.selectedDate['year']
        this.days.push(temp)
      }

      for (let i = 1; i < this.numberOfDays + 1; i++) {
        let temp = {}
        temp['day'] = i
        temp['month'] = this.selectedDate['month']
        temp['year'] = this.selectedDate['year']
        this.days.push(temp)
      }
      dayOfTheWeek = moment
        .tz(
          [
            this.selectedDate['year'],
            this.selectedDate['month'],
            this.days[this.days.length - 1].day,
          ],
          this.timeZone
        )
        .format('dd')
      offset = this.daysHeader.indexOf(dayOfTheWeek)
      if (offset === -1) {
        offset = this.daysHeader.indexOf(dayOfTheWeek.slice(0, 1))
      }
      let remaining = this.daysHeader.length - offset
      for (let i = 1; i < remaining; i++) {
        let temp = {}
        temp['day'] = i * -1
        temp['month'] = this.selectedDate['month'] + 1
        temp['year'] = this.selectedDate['year']
        this.days.push(temp)
      }
    },
    changeTab(choosenTab) {
      this.currentTab = choosenTab
      this.setDefaultTimeInMills()
    },
    setYear(y) {
      if (this.currentTab === 'Q') {
        this.$set(this.selectedQuarter, 'year', y)
        this.time = []
        this.time.push(
          moment
            .tz([this.selectedQuarter.year], this.timeZone)
            .quarter(this.selectedQuarter.quarter)
            .startOf('quarter')
            .valueOf()
        )
        this.time.push(
          moment
            .tz([this.selectedQuarter.year], this.timeZone)
            .quarter(this.selectedQuarter.quarter)
            .endOf('quarter')
            .valueOf()
        )
      } else if (this.currentTab === 'M') {
        this.selectedYear = y
        this.$set(this.selectedDate, 'month', 0)
        this.$set(this.selectedDate, 'day', null)
        this.$set(this.selectedDate, 'year', y)
        this.selectedWeek = null
        this.selectedMonth = 0
        this.time = []
        this.time.push(
          moment
            .tz([y, 0], this.timeZone)
            .startOf('month')
            .valueOf()
        )
        this.time.push(
          moment
            .tz([y, 0], this.timeZone)
            .endOf('month')
            .valueOf()
        )
        this.loadDays()
      } else {
        this.selectedYear = y
        this.$set(this.selectedDate, 'day', null)
        this.$set(this.selectedDate, 'month', 0)
        this.$set(this.selectedDate, 'year', y)
        this.selectedWeek = null
        this.selectedMonth = 0
        this.time = []
        this.time.push(
          moment
            .tz([y], this.timeZone)
            .startOf('year')
            .valueOf()
        )
        this.time.push(
          moment
            .tz([y], this.timeZone)
            .endOf('year')
            .valueOf()
        )
        this.loadDays()
      }
    },
    setMonth(monthId, label, year) {
      this.selectedMonthLabel = label
      this.selectedMonth = monthId
      this.selectedYear = year
      this.$set(this.selectedDate, 'day', null)
      this.$set(this.selectedDate, 'month', monthId)
      this.selectedWeek = null
      this.time = []
      this.time.push(
        moment
          .tz([year, monthId], this.timeZone)
          .startOf('month')
          .valueOf()
      ) // Months are indexed from 0 in moment
      this.time.push(
        moment
          .tz([year, monthId], this.timeZone)
          .endOf('month')
          .valueOf()
      )
      this.loadDays()
    },
    setWeek(d, month, year) {
      this.selectedWeek = moment.tz([year, month, d], this.timeZone).week()
      this.time = []
      this.selectedDate['month'] = month
      this.selectedDate['year'] = year
      this.selectedMonth = month
      this.selectedYear = year
      this.time.push(
        moment
          .tz([year, month, d], this.timeZone)
          .startOf('week')
          .valueOf()
      )
      this.time.push(
        moment
          .tz([year, month, d], this.timeZone)
          .endOf('week')
          .valueOf()
      )
      this.loadDays()
    },
    custom(section) {
      this.customCaseSection = section
      this.customCaseSection = dateoperators.calculateCustomCaseValue(
        this.customCaseSection,
        this.timeZone,
        this.$org
      )
      this.time = this.customCaseSection.value
      this.loadFromMilliseconds(this.customCaseSection.value[0])
      if (
        this.customCaseSection.label.toLowerCase() === 'today' ||
        this.customCaseSection.label.toLowerCase() === 'yesterday' ||
        this.customCaseSection.label.toLowerCase() === 'thisweek' ||
        this.customCaseSection.label.toLowerCase() === 'lastweek' ||
        this.customCaseSection.label.toLowerCase() === 'thismonth' ||
        this.customCaseSection.label.toLowerCase() === 'lastmonth'
      ) {
        this.quickChoose(this.customCaseSection.label.toLowerCase())
      }
    },
    setQuarter(quarterId, year) {
      if (year !== null && typeof year !== 'undefined') {
        this.$set(this.selectedQuarter, 'year', year)
      }
      this.$set(this.selectedQuarter, 'quarter', quarterId)
      this.time = []
      this.time.push(
        moment
          .tz([this.selectedQuarter.year], this.timeZone)
          .quarter(this.selectedQuarter.quarter)
          .startOf('quarter')
          .valueOf()
      )
      this.time.push(
        moment
          .tz([this.selectedQuarter.year], this.timeZone)
          .quarter(this.selectedQuarter.quarter)
          .endOf('quarter')
          .valueOf()
      )
    },
    setDate(d, month) {
      this.$set(this.selectedDate, 'day', d)
      this.$set(this.selectedDate, 'month', month)
      this.time = []
      this.time.push(
        moment
          .tz(
            [this.selectedDate['year'], this.selectedDate['month'], d],
            this.timeZone
          )
          .startOf('day')
          .valueOf()
      )
      this.time.push(
        moment
          .tz(
            [this.selectedDate['year'], this.selectedDate['month'], d],
            this.timeZone
          )
          .endOf('day')
          .valueOf()
      )
    },
    previous() {
      let startOfMonth = moment
        .tz(
          [this.selectedDate['year'], this.selectedDate['month']],
          this.timeZone
        )
        .subtract(1, 'month')
        .startOf('month')
        .valueOf()
      this.$set(
        this.selectedDate,
        'day',
        moment.tz(startOfMonth, this.timeZone).date()
      )
      this.$set(
        this.selectedDate,
        'month',
        moment.tz(startOfMonth, this.timeZone).month()
      )
      this.$set(
        this.selectedDate,
        'year',
        moment.tz(startOfMonth, this.timeZone).year()
      )
      this.selectedMonth = this.selectedDate['month']
      this.selectedYear = this.selectedDate['year']
      this.time = []
      this.time.push(
        moment
          .tz(
            [this.selectedDate['year'], this.selectedDate['month']],
            this.timeZone
          )
          .startOf('month')
          .valueOf()
      )
      if (this.currentTab === 'D') {
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .endOf('day')
            .valueOf()
        )
      } else {
        this.time.push(
          moment
            .tz(
              [this.selectedDate['year'], this.selectedDate['month']],
              this.timeZone
            )
            .endOf('month')
            .valueOf()
        )
      }
      this.loadDays()
    },
    next() {
      let startOfMonth = moment
        .tz(
          [this.selectedDate['year'], this.selectedDate['month']],
          this.timeZone
        )
        .add(1, 'month')
        .startOf('month')
        .valueOf()
      this.$set(
        this.selectedDate,
        'day',
        moment.tz(startOfMonth, this.timeZone).date()
      )
      this.$set(
        this.selectedDate,
        'month',
        moment.tz(startOfMonth, this.timeZone).month()
      )
      this.$set(
        this.selectedDate,
        'year',
        moment.tz(startOfMonth, this.timeZone).year()
      )
      this.selectedMonth = this.selectedDate['month']
      this.selectedYear = this.selectedDate['year']
      this.time = []
      this.time.push(
        moment
          .tz(
            [this.selectedDate['year'], this.selectedDate['month']],
            this.timeZone
          )
          .startOf('month')
          .valueOf()
      )
      if (this.currentTab === 'D') {
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .endOf('day')
            .valueOf()
        )
      } else {
        this.time.push(
          moment
            .tz(
              [this.selectedDate['year'], this.selectedDate['month']],
              this.timeZone
            )
            .endOf('month')
            .valueOf()
        )
      }
      this.loadDays()
    },
    setRange(decider, day) {
      if (decider === 'from') {
        this.$set(
          this.rangeObj,
          'from',
          moment
            .tz(
              [this.rangeObj['fromYear'], this.rangeObj['fromMonth'], day],
              this.timeZone
            )
            .startOf('day')
            .valueOf()
        )
        this.$set(
          this.rangeObj,
          'fromDay',
          moment.tz(this.rangeObj['from'], this.timeZone).date()
        )
        this.$set(
          this.rangeObj,
          'fromMonth',
          moment.tz(this.rangeObj['from'], this.timeZone).month()
        )
        this.$set(
          this.rangeObj,
          'fromYear',
          moment.tz(this.rangeObj['from'], this.timeZone).year()
        )
        this.isRange = true
      } else {
        this.$set(
          this.rangeObj,
          'to',
          moment
            .tz(
              [this.rangeObj['toYear'], this.rangeObj['toMonth'], day],
              this.timeZone
            )
            .endOf('day')
            .valueOf()
        )
        this.$set(
          this.rangeObj,
          'toDay',
          moment.tz(this.rangeObj['to'], this.timeZone).date()
        )
        this.$set(
          this.rangeObj,
          'toMonth',
          moment.tz(this.rangeObj['to'], this.timeZone).month()
        )
        this.$set(
          this.rangeObj,
          'toYear',
          moment.tz(this.rangeObj['to'], this.timeZone).year()
        )
        this.isRange = true
      }
    },
    setDefaultTimeInMills() {
      if (this.currentTab === 'D') {
        this.time = []
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .startOf('day')
            .valueOf()
        )
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .endOf('day')
            .valueOf()
        )
      } else if (this.currentTab === 'W') {
        this.time = []
        this.selectedWeek = moment
          .tz(
            [
              this.selectedDate['year'],
              this.selectedDate['month'],
              this.selectedDate['day'],
            ],
            this.timeZone
          )
          .week()
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .startOf('week')
            .valueOf()
        )
        this.time.push(
          moment
            .tz(
              [
                this.selectedDate['year'],
                this.selectedDate['month'],
                this.selectedDate['day'],
              ],
              this.timeZone
            )
            .endOf('week')
            .valueOf()
        )
      } else if (this.currentTab === 'M') {
        this.time = []
        this.time.push(
          moment
            .tz([this.selectedYear, this.selectedMonth], this.timeZone)
            .startOf('month')
            .valueOf()
        )
        this.time.push(
          moment
            .tz([this.selectedYear, this.selectedMonth], this.timeZone)
            .endOf('month')
            .valueOf()
        )
      } else if (this.currentTab === 'Y') {
        this.time = []
        this.time.push(
          moment
            .tz([this.selectedYear], this.timeZone)
            .startOf('year')
            .valueOf()
        )
        this.time.push(
          moment
            .tz([this.selectedYear], this.timeZone)
            .endOf('year')
            .valueOf()
        )
      } else if (this.currentTab === 'Q') {
        this.time = []
        this.time.push(
          moment
            .tz([this.selectedQuarter.year], this.timeZone)
            .quarter(this.selectedQuarter.quarter)
            .startOf('quarter')
            .valueOf()
        )
        this.time.push(
          moment
            .tz([this.selectedQuarter.year], this.timeZone)
            .quarter(this.selectedQuarter.quarter)
            .endOf('quarter')
            .valueOf()
        )
      }
    },
    returnDate() {
      let orgCreatedTime = this.$getProperty(
        this,
        '$org.createdTime',
        1483209000000
      )
      if (this.currentTab === 'C') {
        this.result = this.customCaseSection
        if (!this.firstTime) {
          this.$emit('date', this.customCaseSection)
        } else {
          this.firstTime = false
        }
      } else if (this.currentTab === 'D') {
        let temp = {}
        temp['value'] = this.time
        temp['offset'] = 0
        temp['operationOnId'] = 1
        temp['operationOn'] = 'day'
        if (this.time[0] === this.today[0] && this.time[1] === this.today[1]) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.$options.filters.formatDate(this.time[0], true)
              : 'Today'
          temp['operatorId'] = 22
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          this.time[0] === this.yesterday[0] &&
          this.time[1] === this.yesterday[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.$options.filters.formatDate(this.time[0], true)
              : 'Yesterday'
          temp['operatorId'] = 25
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
          temp['offset'] = -1
        } else if (
          (this.time[0] === this.todayUptoNow[0] &&
            this.time[1] === this.todayUptoNow[0]) ||
          (this.time[0] === this.todayUptoNow[0] &&
            moment.tz(this.time[1], this.timeZone).day() ===
              moment.tz(this.todayUptoNow[1], this.timeZone).day())
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.$options.filters.formatDate(this.time[0], true)
              : 'Today upto Now'
          temp['operatorId'] = 43
          temp['offset'] = 0
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else {
          temp['label'] = this.$options.filters.formatDate(this.time[0], true)
          temp['operatorId'] = 62
        }
        this.result = temp
        if (this.currentTab === 'C') {
          Object.assign(this.customCaseSection, this.result)
        }
        if (!this.firstTime) {
          this.$emit('date', temp)
        } else {
          this.firstTime = false
        }
      } else if (this.currentTab === 'W') {
        let temp = {}
        temp['value'] = this.time
        temp['operationOnId'] = 2
        temp['operationOn'] = 'week'
        temp['offset'] = 0
        if (
          this.time[0] === this.thisWeek[0] &&
          this.time[1] === this.thisWeek[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.$options.filters.formatDate(this.time[0], true) +
                ' - ' +
                this.$options.filters.formatDate(this.time[1], true)
              : 'This Week'
          temp['operatorId'] = 31
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
          // this.currentTab = 'C'
        } else if (
          this.time[0] === this.lastWeek[0] &&
          this.time[1] === this.lastWeek[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.$options.filters.formatDate(this.time[0], true) +
                ' - ' +
                this.$options.filters.formatDate(this.time[1], true)
              : 'Last Week'
          temp['operatorId'] = 30
          temp['offset'] = -1
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
          // this.currentTab = 'C'
        } else if (
          (this.time[0] === this.thisWeekuptoNow[0] &&
            this.time[1] === this.thisWeekuptoNow[0]) ||
          (this.time[0] === this.thisWeekuptoNow[0] &&
            moment.tz(this.time[1], this.timeZone).day() ===
              moment.tz(this.thisWeekuptoNow[1], this.timeZone).day())
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.$options.filters.formatDate(this.time[0], true) +
                ' - ' +
                this.$options.filters.formatDate(this.time[1], true)
              : 'This Week Until Now'
          temp['operatorId'] = 47
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else {
          temp['label'] =
            this.$options.filters.formatDate(this.time[0], true) +
            ' - ' +
            this.$options.filters.formatDate(this.time[1], true)
          temp['operatorId'] = 63
        }
        this.result = temp
        if (this.currentTab === 'C') {
          this.customCaseSection = this.result
        }
        if (!this.firstTime) {
          this.$emit('date', temp)
        } else {
          this.firstTime = false
        }
      } else if (this.currentTab === 'M') {
        let temp = {}
        temp['value'] = this.time
        temp['operationOnId'] = 3
        temp['offset'] = 0
        temp['operationOn'] = 'month'
        if (
          this.time[0] === this.thisMonth[0] &&
          this.time[1] === this.thisMonth[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment(this.time[0])
                  .tz(this.timeZone)
                  .format('MMM') +
                ' ' +
                moment(this.time[0])
                  .tz(this.timeZone)
                  .format('YYYY')
              : 'This Month'
          temp['operatorId'] = 28
          // this.currentTab = 'C'
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          this.time[0] === this.lastMonth[0] &&
          this.time[1] === this.lastMonth[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment(this.time[0])
                  .tz(this.timeZone)
                  .format('MMM') +
                ' ' +
                moment(this.time[0])
                  .tz(this.timeZone)
                  .format('YYYY')
              : 'Last Month'
          temp['operatorId'] = 27
          temp['offset'] = -1
          // this.currentTab = 'C'
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          this.time[0] === this.thisMonthTillYesterday[0] &&
          this.time[1] === this.thisMonthTillYesterday[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment(this.time[0])
                  .tz(this.timeZone)
                  .format('MMM') +
                ' ' +
                moment(this.time[0])
                  .tz(this.timeZone)
                  .format('YYYY')
              : 'This Month Till Yesterday'
          temp['operatorId'] = 66
          // this.currentTab = 'C'
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          (this.time[0] === this.thisMonthuptoNow[0] &&
            this.time[1] === this.thisMonthuptoNow[1]) ||
          (this.time[0] === this.thisMonthuptoNow[0] &&
            moment.tz(this.time[1], this.timeZone).day() ===
              moment.tz(this.thisMonthuptoNow[1], this.timeZone).day())
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment(this.time[0])
                  .tz(this.timeZone)
                  .format('MMM') +
                ' ' +
                moment(this.time[0])
                  .tz(this.timeZone)
                  .format('YYYY')
              : 'This Month Until Now'
          temp['operatorId'] = 48
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else {
          temp['label'] =
            moment(this.time[0])
              .tz(this.timeZone)
              .format('MMM') +
            ' ' +
            moment(this.time[0])
              .tz(this.timeZone)
              .format('YYYY')
          temp['operatorId'] = 64
        }
        this.result = temp
        if (this.currentTab === 'C') {
          this.customCaseSection = this.result
        }
        if (!this.firstTime) {
          this.$emit('date', temp)
        } else {
          this.firstTime = false
        }
      } else if (this.currentTab === 'Y') {
        let temp = {}
        temp['offset'] = 0
        if (
          this.time[0] === this.thisYear[0] &&
          this.time[1] === this.thisYear[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment.tz(this.time[0], this.timeZone).year()
              : 'This year'
          temp['operatorId'] = 44
          // this.currentTab = 'C'
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          this.time[0] === this.lastYear[0] &&
          this.time[1] === this.lastYear[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment.tz(this.time[0], this.timeZone).year()
              : 'Last Year'
          temp['operatorId'] = 45
          temp['offset'] = -1
          // this.currentTab = 'C'
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          (this.time[0] === this.thisYearuptoNow[0] &&
            this.time[1] === this.thisYearuptoNow[1]) ||
          (this.time[0] === this.thisYearuptoNow[0] &&
            moment.tz(this.time[1], this.timeZone).day() ===
              moment.tz(this.thisYearuptoNow[1], this.timeZone).day())
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment.tz(this.time[0], this.timeZone).year()
              : 'This Year upto Now'
          temp['operatorId'] = 46
          temp['offset'] = 0
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else if (
          this.time[0] === orgCreatedTime &&
          (this.time[1] === this.thisYearuptoNow[1] ||
            moment.tz(this.time[1], this.timeZone).day() ===
              moment.tz(this.thisYearuptoNow[1], this.timeZone).day())
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? moment.tz(this.time[0], this.timeZone).year()
              : 'Till Now'
          temp['operatorId'] = 72
          if (
            typeof this.tabs !== 'undefined' &&
            this.activeTab &&
            this.tabs.enableByOperationOnId === true
          ) {
            this.currentTab = this.activeTab
          } else {
            this.currentTab = 'C'
          }
        } else {
          temp['label'] = moment.tz(this.time[0], this.timeZone).year()
          temp['operatorId'] = 65
        }
        temp['value'] = this.time
        temp['operationOnId'] = 4
        temp['operationOn'] = 'year'
        this.result = temp
        if (this.currentTab === 'C') {
          this.customCaseSection = this.result
        }
        if (!this.firstTime) {
          this.$emit('date', temp)
        } else {
          this.firstTime = false
        }
      } else if (this.currentTab === 'R') {
        let temp = {}
        temp['value'] = [this.rangeObj.from, this.rangeObj.to]
        // let fromMoment = moment.tz([this.rangeObj.fromYear, this.rangeObj.fromMonth, this.rangeObj.fromDay, this.rangeObj.fromTime.getHours(), this.rangeObj.fromTime.getMinutes()], this.timeZone)
        // let toMoment = moment.tz([this.rangeObj.toYear, this.rangeObj.toMonth, this.rangeObj.toDay, this.rangeObj.toTime.getHours(), this.rangeObj.toTime.getMinutes()], this.timeZone)
        // temp['offset'] = -(toMoment.diff(fromMoment, temp.operationOn)) - 1
        if (!this.isRange) {
          if (this.firstTime && this.result.operatorId !== 20) {
            temp['operationOn'] = this.result.operationOn
            temp['operationOnId'] = this.result.operationOnId
            // let fromMoment = moment.tz([this.rangeObj.fromYear, this.rangeObj.fromMonth, this.rangeObj.fromDay, this.rangeObj.fromTime.getHours(), this.rangeObj.fromTime.getMinutes()], this.timeZone)
            // let toMoment = moment.tz([this.rangeObj.toYear, this.rangeObj.toMonth, this.rangeObj.toDay, this.rangeObj.toTime.getHours(), this.rangeObj.toTime.getMinutes()], this.timeZone)
            let fromMoment = moment.tz(
              [
                this.rangeObj.fromYear,
                this.rangeObj.fromMonth,
                this.rangeObj.fromDay,
              ],
              this.timeZone
            )
            let toMoment = moment.tz(
              [
                this.rangeObj.toYear,
                this.rangeObj.toMonth,
                this.rangeObj.toDay,
              ],
              this.timeZone
            )
            temp['offset'] = -(toMoment.diff(fromMoment, temp.operationOn) + 1)
            temp['label'] =
              'Last ' +
              Math.abs(temp['offset']) +
              ' ' +
              temp['operationOn'] +
              (Math.abs(temp['offset']) > 1 ? 's' : '')
            temp['operatorId'] = this.result.operatorId
          } else if (this.result.operatorId === 20) {
            temp['operationOn'] = 'day'
            temp['operationOnId'] = 1
            let fromMoment = moment.tz(
              [
                this.rangeObj.fromYear,
                this.rangeObj.fromMonth,
                this.rangeObj.fromDay,
              ],
              this.timeZone
            )
            let toMoment = moment.tz(
              [
                this.rangeObj.toYear,
                this.rangeObj.toMonth,
                this.rangeObj.toDay,
              ],
              this.timeZone
            )
            if (this.firstTime) {
              temp['offset'] = -toMoment.diff(fromMoment, temp.operationOn) - 1
            } else {
              temp['offset'] = -toMoment.diff(fromMoment, temp.operationOn) - 1
            }
            // let fromMoment = moment.tz([this.rangeObj.fromYear, this.rangeObj.fromMonth, this.rangeObj.fromDay, this.rangeObj.fromTime.getHours(), this.rangeObj.fromTime.getMinutes()], this.timeZone)
            // let toMoment = moment.tz([this.rangeObj.toYear, this.rangeObj.toMonth, this.rangeObj.toDay, this.rangeObj.toTime.getHours(), this.rangeObj.toTime.getMinutes()], this.timeZone)
            // temp['offset'] = -(toMoment.diff(fromMoment, temp.operationOn)) - 1
            // let fromHour = this.rangeObj.fromTime.getHours() < 10 ? '0' + this.rangeObj.fromTime.getHours() : this.rangeObj.fromTime.getHours()
            // let fromMinute = this.rangeObj.fromTime.getMinutes() < 10 ? '0' + this.rangeObj.fromTime.getMinutes() : this.rangeObj.fromTime.getMinutes()
            // let toHour = this.rangeObj.toTime.getHours() < 10 ? '0' + this.rangeObj.toTime.getHours() : this.rangeObj.toTime.getHours()
            // let toMinute = this.rangeObj.toTime.getMinutes() < 10 ? '0' + this.rangeObj.toTime.getMinutes() : this.rangeObj.toTime.getMinutes()
            // temp['label'] = this.rangeObj.fromDay + ' ' + this.months[this.rangeObj.fromMonth].label + ' ' + this.rangeObj.fromYear + ' ' + fromHour + ':' + fromMinute + ' ' +
            //               ' to ' + this.rangeObj.toDay + ' ' + this.months[this.rangeObj.toMonth].label + ' ' + this.rangeObj.toYear + ' ' + toHour + ':' + toMinute

            temp['label'] =
              this.$options.filters.formatDate(temp.value[0], true) +
              ' to ' +
              this.$options.filters.formatDate(temp.value[1], true)
            //   this.rangeObj.fromDay +
            //   ' ' +
            //   this.months[this.rangeObj.fromMonth].label +
            //   ' ' +
            //   this.rangeObj.fromYear +
            //   ' ' +
            //   ' to ' +
            //   this.rangeObj.toDay +
            //   ' ' +
            //   this.months[this.rangeObj.toMonth].label +
            //   ' ' +
            //   this.rangeObj.toYear +
            //   ' '
            temp['operatorId'] = 20
          } else {
            temp['operationOn'] = this.result.operationOn
            temp['operationOnId'] = this.result.operationOnId
            let fromMoment = moment.tz(
              [
                this.rangeObj.fromYear,
                this.rangeObj.fromMonth,
                this.rangeObj.fromDay,
              ],
              this.timeZone
            )
            let toMoment = moment.tz(
              [
                this.rangeObj.toYear,
                this.rangeObj.toMonth,
                this.rangeObj.toDay,
              ],
              this.timeZone
            )
            temp['offset'] = -toMoment.diff(fromMoment, temp.operationOn) - 1
            temp['label'] =
              this.$options.filters.formatDate(temp.value[0], true) +
              ' to ' +
              this.$options.filters.formatDate(temp.value[1], true)
            temp['operatorId'] = this.result.operatorId
          }
        } else {
          temp['operatorId'] = 20
          temp['operationOn'] = 'day'
          temp['operationOnId'] = 1
          let fromMoment = moment.tz(
            [
              this.rangeObj.fromYear,
              this.rangeObj.fromMonth,
              this.rangeObj.fromDay,
            ],
            this.timeZone
          )
          let toMoment = moment.tz(
            [this.rangeObj.toYear, this.rangeObj.toMonth, this.rangeObj.toDay],
            this.timeZone
          )
          if (this.firstTime) {
            temp['offset'] = -toMoment.diff(fromMoment, temp.operationOn) - 1
          } else {
            temp['offset'] = -toMoment.diff(fromMoment, temp.operationOn) - 1
          }
          temp['label'] =
            this.$options.filters.formatDate(temp.value[0], true) +
            ' to ' +
            this.$options.filters.formatDate(temp.value[1], true)
        }
        this.result = temp
        if (!this.firstTime) {
          this.$emit('date', temp)
        } else {
          this.firstTime = false
        }
      } else if (this.currentTab === 'Q') {
        let temp = {}
        temp['value'] = this.time
        if (
          this.time[0] === this.thisQuarter[0] &&
          this.time[1] === this.thisQuarter[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.quarters[this.selectedQuarter.quarter - 1].label +
                ' of ' +
                this.selectedQuarter.year
              : 'This quarter'
          temp['operatorId'] = 68
        } else if (
          this.time[0] === this.lastQuarter[0] &&
          this.time[1] === this.lastQuarter[1]
        ) {
          temp['label'] =
            typeof this.tabs !== 'undefined' && this.tabs.disableDefaultLabels
              ? this.quarters[this.selectedQuarter.quarter - 1].label +
                ' of ' +
                this.selectedQuarter.year
              : 'Last quarter'
          temp['operatorId'] = 69
        } else {
          temp['label'] =
            this.quarters[this.selectedQuarter.quarter - 1].label +
            ' of ' +
            this.selectedQuarter.year
          temp['operatorId'] = 67
        }
        temp['offset'] = 0
        temp['operationOnId'] = 5
        temp['operationOn'] = 'quarter'
        if (
          typeof this.tabs !== 'undefined' &&
          this.activeTab &&
          this.tabs.enableByOperationOnId === true
        ) {
          this.currentTab = this.activeTab
        } else {
          this.currentTab = 'C'
        }
        this.result = temp
        if (this.currentTab === 'C') {
          this.customCaseSection = this.result
        }
        if (!this.firstTime) {
          this.$emit('date', temp)
        } else {
          this.firstTime = false
        }
      }
      this.datePicker = false
    },
  },
}
</script>

<style lang="scss">
.quarter-row {
  padding: 5px;
  font-size: 14px;
  border-left: 2px solid transparent;
}

.day-block {
  white-space: nowrap;
  width: 30px;
  height: 30px;
  cursor: pointer;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.2px;
  text-align: center;
  font-size: 12px;
}

.day-row,
.month-row,
.year-row,
.range-row {
  width: 220px;
  height: 220px;
  text-align: center;
  align-items: center;
}

.days-section .month-row {
  margin-left: 7%;
}

.month-row {
  font-size: 12px;
  font-weight: 500;
  width: 500px;
}

.week-row {
  width: 220px;
  padding-top: 0px;
  text-align: center;
  align-items: center;
}

.timePicker .el-input__inner {
  padding-left: 80px;
  padding-right: 0px;
  padding-bottom: 10px;
  padding-top: 20px;
  border: none !important;
}

.calenderheader-row {
  justify-content: center;
  width: 220px;
  padding-top: 15px;
  text-align: center;
  align-items: center;
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  padding-bottom: 10px;
  text-align: center;
  color: #2e9fac;
  text-transform: uppercase;
}

.year.active,
.month.active {
  border-color: var(--fc-theme-color) !important;
}

.quarter-row.pointer.active {
  border-color: var(--fc-theme-color) !important;
}

.day.active {
  background: #39b3c1 !important;
}

.year.hover,
.month.hover,
.day.hover {
  background: #39b3c1;
  opacity: 0.5;
}

.calender-range-section {
  display: inline-flex;
  margin-left: 2%;
}

.range-selecter {
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 500;
  letter-spacing: 0.5px;
  cursor: pointer;
  padding-left: 15px;
  padding-right: 15px;
  color: #888888;
}

.range-selecter.active {
  background: var(--fc-theme-color);
  border-radius: 3px;
  color: #fff;
}
.calender-div {
  height: 299px;
  margin-top: -79px;
  border-left: 1px solid #e3e3e3;
  margin-left: 15px;
  margin-right: 15px;
}

.cal-custom-day {
  padding: 8px;
  text-transform: uppercase;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
  cursor: pointer;
  text-align: left;
  border-left: 2px solid transparent;
}

.new-calender-popup .custom-section-title {
  font-size: 10px;
  margin-left: 12px;
}

.button-row {
  display: inline-flex;
}

.date-arrow {
  font-size: 1.5em;
  padding-top: 5px;
  margin-top: -3px;
}

.button-row.picker-disabled {
  opacity: 0.5;
  color: #324056 !important;
  .el-button {
    color: #324056 !important;
  }
}

.button-row .el-button {
  font-size: 13px;
  padding: 10px 8px !important;
  border: none !important;
  /*color: #ef508f !important;*/
  color: var(--fc-theme-color) !important;

  background: transparent !important;
}
.cal-right-btn {
  padding: 5px;
  padding-left: 0px;
}
.cal-left-btn {
  padding: 5px;
  padding-right: 0px;
}

.button-row {
  &:not(.picker-disabled) {
    .cal-right-btn,
    .cal-left-btn {
      cursor: pointer;
    }
    .el-button:hover {
      background: #ef508f50 !important;
    }
  }
}

.cal-custom-day.active {
  color: #000;
  /*border-left: 2px solid #f93181;*/
  border-left: 2px solid var(--fc-theme-color);
}

.day-block.day:hover {
  background: #e3f8fa;
}

.year-section {
  width: 68px;
  border-right: 1px solid #e3e3e3;
  border-style: solid;
  border-width: 0px 1px 0px 0px;
  border-color: #e3e3e3;
  height: 100%;
  overflow: scroll;
  border-radius: 0px;
  margin-right: 14px;
  padding-top: 20px;
}

.year-section .year,
.month-row .month {
  width: 54px;
  height: 30px;
  cursor: pointer;
  border-left: 2px solid transparent;
}

.button-row .p5,
.day-row .p5 {
  padding: 5px;
}

.year-section .year:hover,
.month-row .month:hover {
  background: #e3f8fa;
}

.button-row {
  font-size: 13px;
  padding-right: 0px;
  padding-top: 0;
  padding-bottom: 0px;
  border: none;
  color: #ef508f;
}

.from-table {
  padding-right: 10px;
  margin-left: 5px;
  margin-right: 10px;
  border-right: 1px solid #e3e3e3;
}

.to-table {
  padding-left: 10px;
}

/* .calender-range-section .calenderheader-row {
 padding-top: 15px !important;
} */
.calender-range-section .week-row {
  width: 220px;
}

.days-section {
  margin: 0px 0 0 5%;
}

.custom_disable {
  color: #d3d3d3;
  pointer-events: none;
}

/* .fc-black-theme .calender-popup {
 background: rgba(59, 59, 59, 0.96);
 border: gray;
}
.calender-popup {
 background: rgba(59, 59, 59, 0.96) !important;
 border: gray !important;
 color: #fff;
} */
.new-calender-popup [class^='el-icon-'] {
  cursor: pointer;
}

.p10.range-selecter:first-child {
  border-top-left-radius: 15px !important;
  border-bottom-left-radius: 15px !important;
}

.range-selecter:last-child {
  border-top-right-radius: 15px;
  border-bottom-right-radius: 15px;
}

// these styles are being overridden from datepicker(old comp). must remove asap,using !important as text fix
.button-row {
  .cal-right-btn,
  .cal-left-btn {
    cursor: not-allowed;
  }
}

.week-row .day-block {
  font-size: 13px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.2px;
  text-align: center;
  color: #000000;
}

.Reading-Analysis {
  margin-top: 10px;
  font-size: 26px;
  color: #333;
  display: none;
}

.pdfDateView {
  font-size: 16px;
  margin-top: 20px;
  margin-bottom: 20px;
  color: #333;
  display: none;
}

.new-calender-popup {
  padding: 0px;
}

.datepicker-header {
  width: 100%;
  border-bottom: 1px solid #e3e3e3;
  padding-top: 10px;
  padding-bottom: 10px;
  padding-left: 4%;
}

.datepic-btn-row {
  justify-content: center;
  padding-top: 10px;
  padding-bottom: 10px;
  border-top: 1px solid #e3e3e3;
}

.month-container {
  height: 100%;
  padding-top: 20px;
  border-right: 1px solid #e3e3e3;
  padding-right: 14px;
  margin-right: 15px;
}

.new-calender-popup .custom-section {
  padding: 15px 10px;
}

.new-calender-popup .custom-section .cal-custom-day {
  margin-top: 10px;
  padding-left: 10px;
}

/* .arrow-hide {
 display: none;
} */
.DateQuarterSelecter {
  border: none !important;
  margin: auto;
  margin-top: 0px;
}

.DateQuarterSelecter .el-input .el-input__inner,
.DateQuarterSelecter .el-textarea .el-textarea__inner {
  border-bottom: none !important;
}

.DateQuarterSelecter .el-input__icon {
  display: block !important;
}

.DateQuarterSelecter .el-input.el-input--suffix {
  width: 100px;
}

.DateQuarterSelecter .el-input__suffix .el-select__caret {
  padding-bottom: 10px;
}

.DateQuarterSelecter .el-input__suffix .el-select__caret,
.DateQuarterSelecter input.el-input__inner {
  /*color: #ef508f !important;*/
  color: var(--fc-theme-color);
  font-weight: 500 !important;
}
.calender-popup .cal-ok-btn,
.new-calender-popup .cal-ok-btn {
  border: 1px solid #39b2c2 !important;
  background: #39b2c2 !important;
  padding: 10px;
  padding-left: 20px;
  font-size: 12px;
  padding-right: 20px;
  color: #fff;
}
.calender-popup .cal-ok-btn:hover,
.new-calender-popup .cal-ok-btn:hover {
  border: 1px solid #39b2c2 !important;
  background: #fff !important;
  padding: 10px;
  padding-left: 20px;
  font-size: 12px;
  padding-right: 20px;
  color: #39b2c2;
}
</style>
