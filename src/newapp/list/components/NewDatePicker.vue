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
      <div
        class="calendar-button-row"
        :class="[{ 'picker-disabled': disabled }]"
      >
        <div class="">
          <el-button
            class="calendar-btn calendar-label"
            v-if="!isDateFixed"
            :disabled="hidePopover || isDateFixed || disabled"
            v-popover:popover4
            >{{ result.label }}</el-button
          >
          <el-button v-else class="calendar-btn calendar-label">{{
            result.label
          }}</el-button>
        </div>
        <div
          :class="[
            'd-flex align-center date-operators-container',
            iconDisabled && 'disabled-events-cal',
          ]"
        >
          <div
            v-if="!isDateFixed"
            class="pT3 cal-left-btn nav-btn f20"
            @click="navPrevious()"
          >
            <i class="el-icon-arrow-left date-arrow"></i>
          </div>
          <el-button @click="getToday" class="calendar-btn tdy-btn">{{
            $t('common._common.today')
          }}</el-button>
          <!-- <portal-target name="today-btn-calendar"></portal-target> -->
          <div
            v-if="!isDateFixed && !isCurrentDateRange"
            class="pT3 cal-right-btn nav-btn f20"
            @click="navNext()"
          >
            <i class="el-icon-arrow-right date-arrow"></i>
          </div>
        </div>
      </div>
    </div>
    <div class="mobile-new-date-filter" v-else>{{ result.label }}</div>
  </div>
</template>

<script>
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import moment from 'moment-timezone'
import DatePicker from '@/NewDatePicker'

export default {
  extends: DatePicker,
  props: ['iconDisabled'],
  data() {
    return {}
  },
  methods: {
    getToday() {
      let startOfMonth = moment
        .tz(this.timeZone)
        .startOf('month')
        .valueOf()

      this.dateObj = this.$helpers.cloneObject(
        NewDateHelper.getDatePickerObject(64, `${startOfMonth}`) //for now its only month view 64 defines operatorId for month
      )

      this.loadFromObject()
      this.$emit('today-clicked', true)
    },
  },
}
</script>
<style lang="scss">
.date-operators-container {
  gap: 8px;
}
.disabled-events-cal {
  pointer-events: none;
  opacity: 0.5;
}
.calendar-button-row {
  height: 32px;
  display: flex;
  align-items: center;
  color: #324056;
  padding: 4px;
  justify-content: space-between;

  .nav-btn {
    padding: 0px;
    color: #007adb;
  }

  .date-arrow {
    padding: 0px;
    font-size: 20px;
  }

  .calendar-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 24px;
    padding: 2px 8px;
    font-size: 14px;
    border-radius: 4px;
  }
  .calendar-label {
    border: none;

    &:hover {
      background-color: #f0f8ff;
    }
  }
  .tdy-btn {
    border-radius: 2px;
    font-size: 12px;
    border: 1px solid #a8d9ff;
    color: #0074d1;
    background-color: #fff;
  }
}
</style>
