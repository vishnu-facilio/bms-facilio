<template>
  <div>
    <div v-if="$account.org.id === 108">
      <template v-if="reportId === 2362 || reportId === 2409">
        <el-select
          v-model="value2"
          placeholder="Select Year"
          @change="setYearData"
          class="DateQuarterSelecter datepic-btn-row"
        >
          <el-option label="This Year" value="thisyear"></el-option>
          <el-option label="Last Year" value="lastyear"></el-option>
        </el-select>
      </template>
      <template v-else>
        <el-select
          v-model="value"
          placeholder="Select Quarter"
          @change="getquarterData"
          class="DateQuarterSelecter datepic-btn-row"
        >
          <el-option
            v-for="(quarter, index) in quarters"
            :key="index"
            :label="quarter.label"
            :value="quarter.label"
          >
          </el-option>
        </el-select>
      </template>
    </div>
    <div v-else>
      <el-popover
        ref="popover4"
        placement="right"
        popper-class="calender-popup"
        :width="500"
        v-model="datePicker"
        trigger="click"
      >
        <div class="row">
          <div class="row datepicker-header">
            <div class="pointer"></div>
            <div
              class="p10 range-selecter marginL"
              @click="chooseOption('D')"
              v-bind:class="{ active: option === 'D', marginL: option === 'R' }"
            >
              day
            </div>
            <div
              class="p10 range-selecter"
              @click="chooseOption('W')"
              v-bind:class="{ active: option === 'W' }"
            >
              Week
            </div>
            <div
              class="p10 range-selecter"
              @click="chooseOption('M')"
              v-bind:class="{ active: option === 'M' }"
            >
              month
            </div>
            <div
              class="p10 range-selecter"
              @click="chooseOption('Y'), setYear()"
              v-bind:class="{ active: option === 'Y' }"
            >
              year
            </div>
            <div
              class="p10 range-selecter"
              @click="chooseOption('R')"
              v-bind:class="{ active: option === 'R' }"
            >
              range
            </div>
            <div
              class="p10 range-selecter"
              @click="chooseOption('C')"
              v-bind:class="{ active: option === 'C' }"
            >
              Custom
            </div>
            <div></div>
          </div>
          <div class="days-section">
            <div
              class="row calenderheader-row"
              v-if="option === 'D' || option === 'W'"
            >
              <div class="p5" @click="pre()">
                <i class="el-icon-arrow-left"></i>
              </div>
              <div class="p5">{{ month }}</div>
              <div class="p5">{{ year }}</div>
              <div class="p5" @click="next()">
                <i class="el-icon-arrow-right"></i>
              </div>
            </div>
            <div class="row week-row" v-if="option === 'D' || option === 'W'">
              <div
                class="p5 day-block"
                v-for="(w, index) in weeks"
                :key="index"
              >
                {{ w }}
              </div>
            </div>
            <div class="day-container">
              <div class="row day-row pull-left" v-if="option === 'D'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in days"
                  :key="index"
                  @click="selectDate(d)"
                  v-bind:class="{
                    active: selectedDate.day === parseInt(d.day),
                  }"
                >
                  {{ d.day }}
                </div>
              </div>
              <div class="calender-custom pull-right" v-if="option === 'D'">
                <div class="pull-left calender-div"></div>
                <div class="pull-right">
                  <div
                    class="cal-custom-day"
                    @click="customeDay('today')"
                    v-bind:class="{
                      active: time[0] === today[1] || time[0] === today[0],
                    }"
                  >
                    today
                  </div>
                  <div
                    class="cal-custom-day"
                    @click="customeDay('yeaterday')"
                    v-bind:class="{
                      active:
                        time[0] === yeaterDay[1] || time[0] === yeaterDay[0],
                    }"
                  >
                    yesterday
                  </div>
                </div>
              </div>
            </div>
            <div class="day-container">
              <div class="row day-row pull-left" v-if="option === 'W'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in days"
                  :key="index"
                  @click="weekSelect(d)"
                  v-bind:class="{
                    active: selectedDate.week === parseInt(d.week),
                  }"
                >
                  {{ d.day }}
                </div>
              </div>
              <div class="calender-custom pull-right" v-if="option === 'W'">
                <div class="pull-left calender-div"></div>
                <div class="pull-right">
                  <div
                    class="cal-custom-day"
                    @click="customeDay('thisweek')"
                    v-bind:class="{
                      active:
                        time[0] === thisWeek[1] || time[0] === thisWeek[0],
                    }"
                  >
                    This week
                  </div>
                  <div
                    class="cal-custom-day"
                    @click="customeDay('lastweek')"
                    v-bind:class="{
                      active:
                        time[0] === lastWeek[1] || time[0] === lastWeek[0],
                    }"
                  >
                    Last week
                  </div>
                </div>
              </div>
            </div>
            <div v-if="option === 'M' || option === 'Y'" class="row month-row">
              <div class="year-section">
                <div
                  class="p5 year"
                  v-for="y in yearList"
                  :key="y"
                  v-bind:class="{ active: year === y }"
                  @click="setYear(y)"
                >
                  {{ y }}
                </div>
              </div>
              <div class="col-5 month-container" v-if="option === 'M'">
                <div class="row">
                  <div
                    class="col-6 p5 month"
                    v-for="m in monthFirstHalf"
                    :key="m.value"
                    @click="setMonth(m.label, m.value)"
                    v-bind:class="{ active: month === m.label }"
                  >
                    {{ m.label }}
                  </div>
                  <div
                    class="col-6 p5 month"
                    v-for="m in monthSecondHalf"
                    :key="m.value"
                    @click="setMonth(m.label, m.value)"
                    v-bind:class="{ active: month === m.label }"
                  >
                    {{ m.label }}
                  </div>
                </div>
              </div>
              <div class="calender-custom col-4" v-if="option === 'M'">
                <div
                  class="cal-custom-day"
                  @click="customeDay('thismonth')"
                  v-bind:class="{
                    active:
                      time[0] === thismonth[1] || time[0] === thismonth[0],
                  }"
                >
                  This month
                </div>
                <div
                  class="cal-custom-day"
                  @click="customeDay('lastmonth')"
                  v-bind:class="{
                    active:
                      time[0] === lastmonth[1] || time[0] === lastmonth[0],
                  }"
                >
                  Last month
                </div>
              </div>
            </div>
            <div v-if="option === 'C'" class="row">
              <div class="custom-section">
                <div
                  class="cal-custom-day"
                  @click="customeDay('last7days')"
                  v-bind:class="{
                    active:
                      time[0] === last7days[0] || time[1] === last7days[0],
                  }"
                >
                  Last 7 days
                </div>
                <div
                  class="cal-custom-day"
                  @click="customeDay('last10days')"
                  v-bind:class="{
                    active:
                      time[0] === last10days[0] || time[1] === last10days[0],
                  }"
                >
                  Last 10 days
                </div>
                <div
                  class="cal-custom-day"
                  @click="customeDay('last15days')"
                  v-bind:class="{
                    active:
                      time[0] === last15days[0] || time[1] === last15days[0],
                  }"
                >
                  Last 15 days
                </div>
                <div
                  class="cal-custom-day"
                  @click="customeDay('last30days')"
                  v-bind:class="{
                    active:
                      time[0] === last30days[0] || time[1] === last30days[0],
                  }"
                >
                  Last 30 days
                </div>
              </div>
            </div>
          </div>
          <div class="calender-range-section" v-if="option === 'R'">
            <div class="from-table">
              <div class="row calenderheader-row" v-if="option === 'R'">
                <div class="p5" @click="fromRangeNav(range[0].time, 'from')">
                  <i class="el-icon-arrow-left"></i>
                </div>
                <div class="p5">{{ fromMonth }}</div>
                <div class="p5">{{ fromYear }}</div>
                <div class="p5" @click="toRangeNav(range[0].time, 'from')">
                  <i class="el-icon-arrow-right"></i>
                </div>
              </div>
              <div class="row week-row" v-if="option === 'R'">
                <div class="p5 day-block" v-for="w in weeks" :key="w">
                  {{ w }}
                </div>
              </div>
              <div class="row day-row" v-if="option === 'R'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in range[0].days"
                  :key="index"
                  @click="rangeset(d.mills, 'from')"
                  v-bind:class="{ active: range[0].time === parseInt(d.mills) }"
                >
                  {{ d.day }}
                </div>
              </div>
            </div>
            <div class="to-table">
              <div class="row calenderheader-row" v-if="option === 'R'">
                <div class="p5" @click="fromRangeNav(range[1].time, 'to')">
                  <i class="el-icon-arrow-left"></i>
                </div>
                <div class="p5">{{ toMonth }}</div>
                <div class="p5">{{ toYear }}</div>
                <div class="p5" @click="toRangeNav(range[1].time, 'to')">
                  <i class="el-icon-arrow-right"></i>
                </div>
              </div>
              <div class="row week-row" v-if="option === 'R'">
                <div class="p5 day-block" v-for="w in weeks" :key="w">
                  {{ w }}
                </div>
              </div>
              <div class="row day-row" v-if="option === 'R'">
                <div
                  class="p5 day-block day"
                  v-for="(d, index) in range[1].days"
                  :key="index"
                  @click="rangeset(d.mills, 'to')"
                  v-bind:class="{
                    active: range[1].time === parseInt(d.mills),
                    disabled: range[1].time < range[0].time,
                  }"
                >
                  {{ d.day }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="row datepic-btn-row">
          <el-button @click="data()" class="pull-right cal-ok-btn"
            >OK</el-button
          >
        </div>
      </el-popover>
      <div class="Reading-Analysis">Reading Analysis</div>
      <div ref="pdfDate" class="pdfDateView">Period : {{ pdfDate }}</div>
      <div class="button-row">
        <div
          class="p5 cal-left-btn"
          @click="prebtn()"
          v-bind:class="{ 'arrow-hide': option === 'C' }"
        >
          <i class="el-icon-arrow-left date-arrow"></i>
        </div>
        <el-button v-popover:popover4>{{ fulldate() }}</el-button>
        <div
          class="p5 cal-right-btn"
          @click="nxtbtn()"
          v-bind:class="{ 'arrow-hide': option === 'C' }"
        >
          <i class="el-icon-arrow-right date-arrow"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment-timezone'
export default {
  props: ['mills', 'filter', 'dateObj', 'reportId'],
  data() {
    return {
      NumberOfDay: moment().daysInMonth(),
      days: [],
      quarters: [
        {
          label: 'Q1 2018',
          value: [
            moment()
              .tz(this.$timezone)
              .quarter(1)
              .startOf('quarter')
              .valueOf(),
            moment()
              .tz(this.$timezone)
              .quarter(1)
              .endOf('quarter')
              .valueOf(),
          ],
        },
        {
          label: 'Q2 2018',
          value: [
            moment()
              .tz(this.$timezone)
              .quarter(2)
              .startOf('quarter')
              .valueOf(),
            moment()
              .tz(this.$timezone)
              .quarter(2)
              .endOf('quarter')
              .valueOf(),
          ],
        },
        {
          label: 'Q3 2018',
          value: [
            moment()
              .tz(this.$timezone)
              .quarter(3)
              .startOf('quarter')
              .valueOf(),
            moment()
              .tz(this.$timezone)
              .quarter(3)
              .endOf('quarter')
              .valueOf(),
          ],
        },
        {
          label: 'Q4 2018',
          value: [
            moment()
              .tz(this.$timezone)
              .quarter(4)
              .startOf('quarter')
              .valueOf(),
            moment()
              .tz(this.$timezone)
              .quarter(4)
              .endOf('quarter')
              .valueOf(),
          ],
        },
      ],
      value: 'Q2 2018',
      value2: 'This Year',
      operators: [
        {
          index: 0,
          label: 'Today',
          option: 'D',
          value: 22,
          uptoNowValue: 43,
          timestamp: [
            moment()
              .startOf('day')
              .valueOf(),
            moment()
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 1,
          label: 'This week',
          option: 'W',
          value: 31,
          uptoNowValue: 47,
          timestamp: [
            moment()
              .startOf('week')
              .valueOf(),
            moment()
              .endOf('week')
              .valueOf(),
          ],
        },
        {
          index: 2,
          label: 'This month',
          option: 'M',
          value: 28,
          uptoNowValue: 48,
          timestamp: [
            moment()
              .startOf('month')
              .valueOf(),
            moment()
              .endOf('month')
              .valueOf(),
          ],
        },
        {
          index: 3,
          label: 'This year',
          option: 'Y',
          value: 44,
          uptoNowValue: 46,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 4,
          label: 'Range',
          option: 'R',
          value: 100,
          uptoNowValue: 100,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 5,
          label: 'Custom',
          option: 'C',
          value: 49,
          days: '7',
          uptoNowValue: 49,
          timestamp: [
            moment()
              .startOf('year')
              .valueOf(),
            moment()
              .endOf('year')
              .valueOf(),
          ],
        },
        {
          index: 6,
          label: 'Last 7 days',
          option: 'C',
          value: 49,
          uptoNowValue: 49,
          days: '7',
          timestamp: [
            moment()
              .subtract(6, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(0, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 7,
          label: 'Last 10 days',
          option: 'C',
          value: 49,
          uptoNowValue: 49,
          days: '10',
          timestamp: [
            moment()
              .subtract(9, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(0, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 8,
          label: 'Last 15 days',
          option: 'C',
          value: 49,
          uptoNowValue: 49,
          days: '15',
          timestamp: [
            moment()
              .subtract(14, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(0, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 9,
          label: 'Last 30 days',
          option: 'C',
          value: 49,
          days: '30',
          uptoNowValue: 49,
          timestamp: [
            moment()
              .subtract(29, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(0, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 10,
          label: 'Yesterday',
          option: 'D',
          value: 25,
          days: '30',
          uptoNowValue: 25,
          timestamp: [
            moment()
              .subtract(1, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(1, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
        {
          index: 11,
          label: 'Last month',
          trimLabel: 'M',
          value: 27,
          uptoNowValue: 48,
          timestamp: [
            moment()
              .subtract(1, 'month')
              .startOf('month')
              .valueOf(),
            moment()
              .subtract(1, 'month')
              .endOf('month')
              .valueOf(),
          ],
        },
        {
          index: 12,
          label: 'Last Week',
          trimLabel: 'W',
          value: 30,
          uptoNowValue: 47,
          timestamp: [
            moment()
              .subtract(1, 'week')
              .startOf('eek')
              .valueOf(),
            moment()
              .subtract(1, 'week')
              .endOf('week')
              .valueOf(),
          ],
        },
        {
          index: 13,
          label: 'yesterday',
          option: 'D',
          value: 25,
          uptoNowValue: 43,
          timestamp: [
            moment()
              .subtract(1, 'day')
              .startOf('day')
              .valueOf(),
            moment()
              .subtract(1, 'day')
              .endOf('day')
              .valueOf(),
          ],
        },
      ],
      today: [
        moment()
          .startOf('day')
          .valueOf(),
        moment()
          .startOf('day')
          .valueOf(),
      ],
      yeaterDay: [
        moment()
          .subtract(1, 'day')
          .startOf('day')
          .valueOf(),
        moment()
          .subtract(1, 'day')
          .startOf('day')
          .valueOf(),
      ],
      thisWeek: [
        moment()
          .startOf('week')
          .valueOf(),
        moment()
          .startOf('week')
          .valueOf(),
      ],
      lastWeek: [
        moment()
          .subtract(1, 'week')
          .startOf('week')
          .valueOf(),
        moment()
          .subtract(1, 'week')
          .startOf('week')
          .valueOf(),
      ],
      thismonth: [
        moment()
          .startOf('month')
          .valueOf(),
        moment()
          .startOf('month')
          .valueOf(),
      ],
      thisyear: [
        moment()
          .startOf('year')
          .valueOf(),
        moment()
          .endOf('year')
          .valueOf(),
      ],
      lastyear: [
        moment()
          .subtract(1, 'year')
          .startOf('year')
          .valueOf(),
        moment()
          .subtract(1, 'year')
          .endOf('year')
          .valueOf(),
      ],
      lastmonth: [
        moment()
          .subtract(1, 'month')
          .startOf('month')
          .valueOf(),
        moment()
          .subtract(1, 'month')
          .startOf('month')
          .valueOf(),
      ],
      last7days: [
        moment()
          .subtract(6, 'day')
          .startOf('day')
          .valueOf(),
        moment()
          .subtract(0, 'day')
          .endOf('day')
          .valueOf(),
      ],
      last10days: [
        moment()
          .subtract(9, 'day')
          .startOf('day')
          .valueOf(),
        moment()
          .subtract(0, 'day')
          .endOf('day')
          .valueOf(),
      ],
      last15days: [
        moment()
          .subtract(14, 'day')
          .startOf('day')
          .valueOf(),
        moment()
          .subtract(0, 'day')
          .endOf('day')
          .valueOf(),
      ],
      last30days: [
        moment()
          .subtract(29, 'day')
          .startOf('day')
          .valueOf(),
        moment()
          .subtract(0, 'day')
          .endOf('day')
          .valueOf(),
      ],
      datePicker: false,
      weeks: ['Su', 'M', 'Tu', 'W', 'Th', 'F', 'Sa'],
      year: parseInt(moment().format('YYYY')),
      month: moment().format('MMM'),
      timeStamp: moment().valueOf(),
      time: [],
      between: false,
      option: 'D',
      obj: {
        operatorId: 49,
        value: '30',
        enable: false,
      },
      monthFirstHalf: [
        { label: 'Jan', value: 0 },
        { label: 'Feb', value: 1 },
        { label: 'Mar', value: 2 },
        { label: 'Apr', value: 3 },
        { label: 'May', value: 4 },
        { label: 'Jun', value: 5 },
      ],
      monthSecondHalf: [
        { label: 'Jul', value: 6 },
        { label: 'Aug', value: 7 },
        { label: 'Sep', value: 8 },
        { label: 'Oct', value: 9 },
        { label: 'Nov', value: 10 },
        { label: 'Dec', value: 11 },
      ],
      months: [
        { label: 'Jan', value: 0 },
        { label: 'Feb', value: 1 },
        { label: 'Mar', value: 2 },
        { label: 'Apr', value: 3 },
        { label: 'May', value: 4 },
        { label: 'Jun', value: 5 },
        { label: 'Jul', value: 6 },
        { label: 'Aug', value: 7 },
        { label: 'Sep', value: 8 },
        { label: 'Oct', value: 9 },
        { label: 'Nov', value: 10 },
        { label: 'Dec', value: 11 },
      ],
      yearList: [2018, 2017, 2016, 2015, 2014, 2013],
      temptime: [],
      selectedDate: {
        day: moment().format('D'),
        week: moment().week(),
      },
      hoverRow: {
        day: moment().format('D'),
        week: moment().week(),
      },
      range: [
        {
          time: moment()
            .startOf('day')
            .valueOf(),
          day: moment().format('D'),
          days: [],
          numberOfDays: 0,
          year: parseInt(moment().format('YYYY')),
        },
        {
          time: moment()
            .add(1, 'month')
            .endOf('day')
            .valueOf(),
          day: moment().format('D'),
          days: [],
          numberOfDays: 0,
          year: parseInt(moment().format('YYYY')),
        },
      ],
    }
  },
  computed: {
    fromMonth: function() {
      return moment(this.range[0].time).format('MMM')
    },
    fromYear: function() {
      return moment(this.range[0].time).format('YYYY')
    },
    toMonth: function() {
      return moment(this.range[1].time).format('MMM')
    },
    toYear: function() {
      return moment(this.range[1].time).format('YYYY')
    },
    pdfDate() {
      if (this.mills && this.mills.length === 2) {
        let startDate = moment(this.mills[0])
          .tz(this.$timezone)
          .format('DD MMM YYYY')
        let endDate = moment(this.mills[1])
          .tz(this.$timezone)
          .format('DD MMM YYYY')
        return startDate + (startDate !== endDate ? ' - ' + endDate : '')
      } else return ''
    },
  },
  mounted() {
    this.time = [
      moment(this.timeStamp)
        .startOf('month')
        .valueOf(),
      moment(this.timeStamp)
        .endOf('month')
        .valueOf(),
    ]
    this.initData()
  },
  methods: {
    initData() {
      let self = this
      this.NumberOfDay = moment().daysInMonth()
      self.year = moment().format('YYYY')
      self.month = moment(self.timeStamp).format('MMM')
      self.constractDays()
      self.modeset()
    },
    modeset() {
      if (this.mills) {
        if (Array.isArray(this.mills)) {
          this.time[0] = this.mills[0]
          this.time[1] = this.mills[1]
          this.range[0].time = moment(this.mills[0])
            .startOf('day')
            .valueOf()
          this.range[1].time = moment(this.mills[1])
            .endOf('day')
            .valueOf()
          this.option = this.filter
            ? this.filter
            : this.operators.find(rt => rt.value === this.dateObj.operatorId)
                .option
          this.rangeInt()
        } else {
          this.time[0] = this.mills
          this.range[0].time = this.mills[0]
          this.option = 'D'
        }
        this.timeStamp = this.mills ? this.mills : moment().valueOf()
        this.selectedDate.day = this.mills
          ? parseInt(
              moment(this.mills)
                .endOf('day')
                .format('D')
            )
          : parseInt(
              moment()
                .endOf('day')
                .format('D')
            )
      }
    },
    fromRangeNav(timestamp, option) {
      if (option === 'from') {
        this.range[0].time = moment(timestamp)
          .add(-1, 'month')
          .startOf('day')
          .valueOf()
      } else if (option === 'to') {
        this.range[1].time = moment(timestamp)
          .add(-1, 'month')
          .startOf('day')
          .valueOf()
      }
      this.rangeInt()
    },
    toRangeNav(timestamp, option) {
      if (option === 'from') {
        this.range[0].time = moment(timestamp)
          .add(1, 'month')
          .endOf('day')
          .valueOf()
      } else if (option === 'to') {
        this.range[1].time = moment(timestamp)
          .add(1, 'month')
          .endOf('day')
          .valueOf()
      }
      this.rangeInt()
    },
    rangeset(m, option) {
      if (m !== null) {
        if (option === 'from') {
          this.range[0].time = parseInt(
            moment(m)
              .startOf('day')
              .valueOf()
          )
        } else if (option === 'to') {
          this.range[1].time = parseInt(
            moment(m)
              .endOf('day')
              .valueOf()
          )
        }
      }
      this.mergeTimeStamp()
    },
    getTime(month, day, mills) {
      let startTime = moment()
        .startOf('day')
        .valueOf()
      let endTime = moment()
        .endOf('day')
        .valueOf()
      let self = this
      if (self.option === 'D') {
        startTime = moment([self.year, month, self.selectedDate.day])
          .startOf('day')
          .valueOf()
        endTime = moment([self.year, month, self.selectedDate.day])
          .endOf('day')
          .valueOf()
      } else if (self.option === 'W') {
        startTime = moment([self.year, month, self.selectedDate.day])
          .startOf('week')
          .valueOf()
        endTime = moment([self.year, month, self.selectedDate.day])
          .endOf('week')
          .valueOf()
      } else if (self.option === 'M') {
        startTime = moment([self.year, month])
          .startOf('month')
          .valueOf()
        endTime = moment([self.year, month])
          .endOf('month')
          .valueOf()
      } else if (self.option === 'Y') {
        startTime = moment([self.year, 0])
          .startOf('year')
          .valueOf()
        endTime = moment([self.year, 0])
          .endOf('year')
          .valueOf()
      } else {
        startTime = moment([self.year, month])
          .startOf('day')
          .valueOf()
        endTime = moment([self.year, month])
          .endOf('day')
          .valueOf()
      }
      self.time = [startTime, endTime]
      if (self.option === 'W') {
        self.selectedDate.day = parseInt(moment(mills).format('D'))
        self.month = moment(mills).format('MMM')
        self.constractDays(mills)
      } else {
        self.selectedDate.day = parseInt(moment(startTime).format('D'))
        self.month = moment(startTime).format('MMM')
        self.constractDays()
      }
    },
    pre() {
      let self = this
      let monthInNumber = moment(this.time[0]).month()
      if (monthInNumber < 1) {
        monthInNumber = 12
        self.year = parseInt(self.year) - 1
      }
      self.getTime(monthInNumber - 1)
      let startTime = moment([self.year, monthInNumber - 1])
        .startOf('x')
        .valueOf()
      let endTime = moment([self.year, monthInNumber - 1])
        .endOf('month')
        .valueOf()
      this.time = [startTime, endTime]
      this.timeStamp = startTime
      self.month = moment(self.timeStamp).format('MMM')
      self.selectedDate.day = parseInt(
        moment(startTime)
          .endOf('day')
          .format('D')
      )
      self.constractDays()
    },
    next() {
      let self = this
      let monthInNumber = moment(this.time[1]).month() + 1
      if (monthInNumber > 11) {
        monthInNumber = 0
        self.year = parseInt(self.year) + 1
      }
      self.getTime(monthInNumber)
      let startTime = moment([self.year, monthInNumber])
        .startOf('month')
        .valueOf()
      let endTime = moment([self.year, monthInNumber])
        .endOf('month')
        .valueOf()
      this.time = [startTime, endTime]
      this.timeStamp = startTime
      self.month = moment(self.timeStamp).format('MMM')
      self.selectedDate.day = parseInt(
        moment(startTime)
          .endOf('day')
          .format('D')
      )
      self.constractDays()
    },
    constractDays(mills) {
      let self = this
      self.days = []
      let dateArray = []
      if (self.option === 'W') {
        if (mills) {
          dateArray = moment(mills).toArray()
          self.NumberOfDay = moment(mills).daysInMonth()
        } else {
          dateArray = moment(self.time[0]).toArray()
          self.NumberOfDay = moment(self.time[0]).daysInMonth()
        }
      } else {
        dateArray = moment(self.time[0]).toArray()
        self.NumberOfDay = moment(self.time[0]).daysInMonth()
      }
      for (
        let j = 0;
        j < moment([dateArray[0], dateArray[1], 1]).weekday();
        j++
      ) {
        let time = {}
        time.day = ''
        time.week = null
        time.mills = null
        self.days.push(time)
      }
      for (let i = 1; i <= self.NumberOfDay; i++) {
        let time = {}
        time.day = i
        time.mills = moment([dateArray[0], dateArray[1], i]).valueOf()
        time.week = moment([dateArray[0], dateArray[1], i]).week()
        self.days.push(time)
      }
    },
    constractRangeDay() {
      let self = this
      self.range[0].days = []
      self.range[1].days = []
      self.range[0].numberOfDays = moment(self.range[0].time).daysInMonth()
      self.range[1].numberOfDays = moment(self.range[1].time).daysInMonth()
      self.range[0].days = self.contDays(
        self.range[0].time,
        self.range[0].numberOfDays
      )
      self.range[1].days = self.contDays(
        self.range[1].time,
        self.range[1].numberOfDays,
        'to'
      )
    },
    contDays(timezone, days, option) {
      let date = []
      let time = {}
      let dateArray = moment(timezone).toArray()
      for (
        let j = 0;
        j < moment([dateArray[0], dateArray[1], 1]).weekday();
        j++
      ) {
        time.day = ''
        time.week = null
        time.mills = null
        date.push(time)
      }
      for (let i = 1; i <= days; i++) {
        let time = {}
        time.day = i
        time.week = moment([dateArray[0], dateArray[1], i]).week()
        if (option === 'to') {
          time.mills = moment([dateArray[0], dateArray[1], i])
            .endOf('day')
            .valueOf()
        } else {
          time.mills = moment([dateArray[0], dateArray[1], i]).valueOf()
        }
        date.push(time)
      }
      return date
    },
    chooseOption(opt) {
      this.option = opt
      if (opt === 'R') {
        this.rangeInt()
      }
    },
    setYear(y) {
      let self = this
      if (y) {
        this.year = y
      }
      self.getTime(self.months.find(row => row.label === self.month).value)
    },
    setMonth(label, value) {
      let self = this
      this.monthInNumber = value
      this.month = label
      self.getTime(parseInt(value))
    },
    selectDate(d) {
      let self = this
      if (d.day !== '' && d.week !== null) {
        this.selectedDate.day = d.day
        this.selectedDate.week = d.week
        self.getTime(
          self.months.find(row => row.label === self.month).value,
          d.day,
          d.mills
        )
      }
    },
    weekSelect(d) {
      let self = this
      if (d.day !== '' && d.week !== null) {
        this.selectedDate.day = d.day
        this.selectedDate.week = d.week
        self.getTime(
          self.months.find(row => row.label === self.month).value,
          d.day,
          d.mills
        )
      }
    },
    customeDay(option) {
      this.between = false
      let self = this
      if (option === 'today') {
        self.time[0] = moment()
          .startOf('day')
          .valueOf()
        self.time[1] = moment()
          .endOf('day')
          .valueOf()
        self.constractDays()
      } else if (option === 'yeaterday') {
        this.obj.actualOperatorId = 25
        self.time[0] = moment()
          .subtract(1, 'day')
          .startOf('day')
          .valueOf()
        self.time[1] = moment()
          .subtract(1, 'day')
          .endOf('day')
          .valueOf()
        self.constractDays()
      } else if (option === 'thisweek') {
        let self = this
        self.time[0] = moment()
          .startOf('week')
          .valueOf()
        self.time[1] = moment()
          .endOf('week')
          .valueOf()
        let d = {
          day: parseInt(
            moment()
              .startOf('day')
              .format('D')
          ),
          week: parseInt(moment().week()),
          mills: moment()
            .startOf('week')
            .valueOf(),
        }
        self.month = moment().format('MMM')
        self.weekSelect(d)
      } else if (option === 'lastweek') {
        this.obj.actualOperatorId = 30
        self.time[0] = moment()
          .subtract(1, 'week')
          .startOf('week')
          .valueOf()
        self.time[1] = moment()
          .subtract(1, 'week')
          .endOf('week')
          .valueOf()
        let d = {
          day: parseInt(moment(self.time[0]).format('D')),
          week: parseInt(moment(self.time[0]).week()),
          mills: self.time[0],
        }
        self.month = moment(d.mills).format('MMM')
        self.weekSelect(d)
      } else if (option === 'thismonth') {
        this.setMonth(moment().format('MMM'), moment().month())
      } else if (option === 'lastmonth') {
        this.obj.actualOperatorId = 27
        this.setMonth(
          moment()
            .subtract(1, 'month')
            .format('MMM'),
          moment()
            .subtract(1, 'month')
            .month()
        )
      } else if (option === 'last7days') {
        this.obj.enable = true
        this.obj.operatorId = 49
        this.obj.value = '7'
        if (this.dateObj) {
          this.setdateObj(this.obj.operatorId, this.obj.value)
        }
        self.time = self.last7days
      } else if (option === 'last10days') {
        this.obj.enable = true
        this.obj.operatorId = 49
        this.obj.value = '10'
        if (this.dateObj) {
          this.setdateObj(this.obj.operatorId, this.obj.value)
        }
        self.time = self.last10days
      } else if (option === 'last15days') {
        this.obj.enable = true
        this.obj.operatorId = 49
        this.obj.value = '15'
        if (this.dateObj) {
          this.setdateObj(this.obj.operatorId, this.obj.value)
        }
        self.time = self.last15days
      } else if (option === 'last30days') {
        this.obj.enable = true
        this.obj.operatorId = 49
        this.obj.value = '30'
        if (this.dateObj) {
          this.setdateObj(this.obj.operatorId, this.obj.value)
        }
        self.time = self.last30days
      }
    },
    setdateObj(id, value) {
      this.dateObj.operatorId = id
      this.dateObj.value = value
    },
    mouseOver(d) {
      let self = this
      if (d.day !== '' && d.week !== null) {
        self.hoverRow.day = d.day
        self.hoverRow.week = moment(d.mills).week()
      }
    },
    data(fetchTime) {
      let self = this
      self.datePicker = false
      let fulldate = { time: [] }
      if (self.option === 'D') {
        self.time[1] = moment(self.time[0])
          .endOf('day')
          .valueOf()
        fulldate.time = self.time
        // fulldate.time[0] = this.$helpers.getTimeInOrg(moment(fulldate.time[0]))
        // fulldate.time[1] = this.$helpers.getTimeInOrg(moment(fulldate.time[1]))
      }
      // else if (self.option === 'W') {
      //   self.time[0] = moment(self.time[0]).tz(self.$timezone)
      //   self.time[1] = moment(self.time[1]).tz(self.$timezone)
      //   fulldate.time[0] = this.$helpers.getTimeInOrg((self.time[0]))
      //   fulldate.time[1] = this.$helpers.getTimeInOrg(moment(self.time[1]))
      // }
      else {
        self.temptime = self.time
        fulldate.time = self.time
        // fulldate.time[0] = this.$helpers.getTimeInOrg(moment(fulldate.time[0]))
        // fulldate.time[1] = this.$helpers.getTimeInOrg(moment(fulldate.time[1]))
      }
      // let time = moment(fulldate.time[0]).toArray()
      fulldate.filterName = self.option
      if (this.dataObj) {
        fulldate.operatorId = this.dataObj.operatorId
      } else if (this.obj.actualOperatorId) {
        fulldate.operatorId = this.obj.actualOperatorId
      } else {
        fulldate.operatorId = this.operators.find(
          sb => sb.option === self.option
        ).value
      }
      if (this.obj.enable) {
        fulldate.value = this.obj.value
      }
      fulldate.time[0] = this.$helpers.getTimeInOrg(fulldate.time[0])
      fulldate.time[1] = this.$helpers.getTimeInOrg(fulldate.time[1])
      fulldate.mounted = 2
      if (fetchTime) {
        return fulldate
      }
      this.$emit('data', fulldate)
    },
    fulldate() {
      let self = this
      let array1 = moment().toArray()
      if (this.option !== 'D') {
        if (
          this.time[0] ===
            moment([array1[0], array1[1], array1[2]])
              .startOf('week')
              .valueOf() &&
          this.time[1] ===
            moment([array1[0], array1[1], array1[2]])
              .endOf('week')
              .valueOf()
        ) {
          return 'This week'
        } else if (
          this.time[0] ===
            moment()
              .subtract(1, 'week')
              .startOf('day')
              .valueOf() &&
          this.time[1] ===
            moment()
              .subtract(1, 'week')
              .endOf('day')
              .valueOf()
        ) {
          return 'Last week'
        } else if (
          this.time[0] ===
            moment()
              .subtract(0, 'month')
              .startOf('month')
              .valueOf() &&
          this.time[1] ===
            moment()
              .subtract(0, 'month')
              .endOf('month')
              .valueOf()
        ) {
          return 'This month'
        } else if (
          this.time[0] === this.lastmonth[0] &&
          this.time[1] === this.lastmonth[1]
        ) {
          return 'Last month'
        } else if (
          this.time === this.thisWeek ||
          this.time ===
            [
              this.$helpers.getTimeInOrg(this.time[0]),
              this.$helpers.getTimeInOrg(this.time[1]),
            ]
        ) {
          return 'This week'
        } else if (this.option === 'C') {
          if (!this.between) {
            let name = ''
            let val
            let id
            if (this.dateObj) {
              if (this.obj.enable) {
                id = this.obj.operatorId
                val = this.obj.value
              } else {
                id = this.dateObj.operatorId
                if (this.dateObj.value) {
                  val = this.dateObj.value
                }
              }
              if (id === 44 && this.option === 'C') {
                id = 49
              }
              if (id === 42) {
                name = 'Last ' + val + ' hours'
              } else if (id === 49) {
                name = 'Last ' + val + ' Days'
              } else if (id === 50) {
                name = 'Last ' + val + ' Weeks'
              } else if (id === 51) {
                name = 'Last ' + val + ' Months'
              } else {
                name =
                  moment(this.time[0]).format('DD MMM YYYY') +
                  ' - ' +
                  moment(this.time[1])
                    .tz(this.$timezone)
                    .format('DD MMM YYYY')
              }
            } else if (this.obj.value) {
              id = this.obj.operatorId
              val = this.obj.value
              if (id === 42) {
                name = 'Last ' + val + ' hours'
              } else if (id === 49) {
                name = 'Last ' + val + ' Days'
              } else if (id === 50) {
                name = 'Last ' + val + ' Weeks'
              } else if (id === 51) {
                name = 'Last ' + val + ' Months'
              } else {
                name =
                  moment(this.time[0]).format('DD MMM YYYY') +
                  ' - ' +
                  moment(this.time[1])
                    .tz(this.$timezone)
                    .format('DD MMM YYYY')
              }
            } else {
              name =
                moment(this.time[0]).format('DD MMM YYYY') +
                ' - ' +
                moment(this.time[1])
                  .tz(this.$timezone)
                  .format('DD MMM YYYY')
            }
            return name
          } else {
            return (
              moment(this.time[0]).format('DD MMM YYYY') +
              ' - ' +
              moment(this.time[1])
                .tz(this.$timezone)
                .format('DD MMM YYYY')
            )
          }
        } else {
          if (this.option === 'M') {
            return moment(this.time[0]).format('MMM YYYY')
          } else if (this.option === 'Y') {
            let year = ''
            if (
              (self.time[0] === self.thisyear[0] &&
                self.time[1] === self.thisyear[1]) ||
              self.time === self.thisyear ||
              (self.time[0] === this.$helpers.getTimeInOrg(self.thisyear[0]) &&
                self.time[1] ===
                  this.$helpers.getTimeInOrg(self.thisyear[1])) ||
              moment(this.time[0]).format('YYYY') ===
                moment(self.thisyear).format('YYYY')
            ) {
              year = 'This Year'
            } else if (
              (self.time[0] === self.lastyear[0] &&
                self.time[1] === self.lastyear[1]) ||
              self.time === self.lastyear ||
              (self.time[0] === this.$helpers.getTimeInOrg(self.lastyear[0]) &&
                self.time[1] ===
                  this.$helpers.getTimeInOrg(self.lastyear[1])) ||
              moment(this.time[0]).format('YYYY') ===
                moment(self.lastyear).format('YYYY')
            ) {
              year = 'Last year'
            } else {
              year = moment(this.time[0]).format('YYYY')
            }
            return year
          } else {
            return (
              moment(this.time[0]).format('DD MMM YYYY') +
              ' - ' +
              moment(this.time[1])
                .tz(this.$timezone)
                .format('DD MMM YYYY')
            )
          }
        }
      } else {
        if (this.option === 'D') {
          if (
            this.time[0] ===
              moment()
                .subtract(0, 'day')
                .startOf('day')
                .valueOf() ||
            this.time[0] ===
              moment()
                .tz(this.$timezone)
                .startOf('day')
                .valueOf()
          ) {
            this.selectrange()
            return 'Today'
          } else if (
            this.time[0] ===
              moment()
                .subtract(1, 'day')
                .startOf('day')
                .valueOf() ||
            this.time[0] ===
              moment()
                .tz(this.$timezone)
                .subtract(1, 'day')
                .startOf('day')
                .valueOf()
          ) {
            this.selectrange()
            return 'Yesterday'
          } else {
            this.selectrange()
            return moment(this.time[0]).format('DD MMM YYYY')
          }
        } else {
          this.selectrange()
          return moment(this.time[0]).format('DD MMM YYYY')
        }
      }
    },
    selectrange() {
      let self = this
      self.month = moment(this.time[0]).format('MMM')
      self.selectedDate.day = parseInt(
        moment(this.time[0])
          .endOf('day')
          .format('D')
      )
      self.year = parseInt(moment(this.time[0]).format('YYYY'))
    },
    rangeInt() {
      this.mergeTimeStamp()
      this.constractRangeDay()
    },
    mergeTimeStamp() {
      let self = this
      self.time[0] = self.range[0].time
      self.time[1] = self.range[1].time
    },
    prebtn() {
      let self = this
      this.between = true
      if (this.option === 'D') {
        self.time[0] = moment(self.time[0])
          .subtract(1, 'day')
          .startOf('day')
          .valueOf()
        self.time[1] = moment(self.time[1])
          .subtract(1, 'day')
          .endOf('day')
          .valueOf()
        self.selectedDate.day = parseInt(
          moment(self.time[0])
            .endOf('day')
            .format('D')
        )
        self.month = moment(self.time[0]).format('MMM')
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'W') {
        self.time[0] = moment(self.time[0])
          .subtract(1, 'week')
          .startOf('week')
          .valueOf()
        self.time[1] = moment(self.time[0])
          .subtract(0, 'week')
          .endOf('week')
          .valueOf()
        self.selectedDate.week = parseInt(moment(self.time[0]).week())
        self.month = moment(self.time[0]).format('MMM')
        self.year = parseInt(moment(self.time[0]).format('YYYY'))
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'M') {
        self.time[0] = moment(self.time[0])
          .subtract(1, 'month')
          .startOf('month')
          .valueOf()
        self.time[1] = moment(self.time[0])
          .subtract(0, 'month')
          .endOf('month')
          .valueOf()
        self.month = moment(self.time[0]).format('MMM')
        self.year = parseInt(moment(self.time[0]).format('YYYY'))
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'Y') {
        self.time[0] = moment(self.time[0])
          .subtract(1, 'year')
          .startOf('year')
          .valueOf()
        self.time[1] = moment(self.time[0])
          .subtract(0, 'year')
          .endOf('year')
          .valueOf()
        self.year = parseInt(moment(self.time[0]).format('YYYY'))
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'R') {
        let start = moment(self.time[0])
          .startOf('day')
          .valueOf()
        let end = moment(self.time[1])
          .subtract(1, 'day')
          .endOf('day')
          .valueOf()
        let diff = end - start
        let days = moment.duration(diff, 'milliseconds').days()
        let time1 = {
          time: moment(start),
          day: parseInt(moment(start).format('D')),
        }
        let time2 = {
          time: moment(end),
          day: parseInt(moment(end).format('D')),
        }
        // let diffMonth = parseInt(time2.time.diff(time1.time, 'months'))
        let diffDays = parseInt(time2.time.diff(time1.time, 'days'))
        if (
          parseInt(moment(self.range[0].time).format('M')) !==
          parseInt(moment(self.range[1].time).format('M'))
        ) {
          let newtime1 = {
            time: moment(self.range[0].time)
              .subtract(diffDays, 'day')
              .startOf('day')
              .valueOf(),
          }
          let newtime2 = {
            time: moment(self.range[1].time)
              .subtract(diffDays, 'day')
              .startOf('day')
              .valueOf(),
          }
          let startTime = {
            month: parseInt(moment(newtime1.time).format('M')),
            year: parseInt(moment(newtime1.time).format('YYYY')),
            day: time1.day,
          }
          let endTime = {
            month: parseInt(moment(newtime2.time).format('M')),
            year: parseInt(moment(newtime2.time).format('YYYY')),
            day: time2.day,
          }
          if (
            parseInt(
              moment([startTime.year, startTime.month, startTime.day])
                .endOf('month')
                .format('D')
            ) < startTime.day
          ) {
            startTime.day = parseInt(
              moment([startTime.year, startTime.month, startTime.day])
                .endOf('month')
                .format('D')
            )
          }
          if (
            parseInt(
              moment([endTime.year, endTime.month, endTime.day])
                .endOf('month')
                .format('D')
            ) < endTime.day
          ) {
            endTime.day = parseInt(
              moment([endTime.year, endTime.month, endTime.day])
                .endOf('month')
                .format('D')
            )
          }
          let rangeMonth = endTime.month - startTime.month
          if (rangeMonth > 0) {
            let time1 = {}
            let time2 = {}
            time1.time = moment(self.range[0].time)
              .subtract(rangeMonth - 1 === 0 ? 1 : rangeMonth - 1, 'month')
              .valueOf()
            time2.time = moment(self.range[1].time)
              .subtract(rangeMonth - 1 === 0 ? 1 : rangeMonth - 1, 'month')
              .valueOf()
            self.range[0].time = time1.time
            self.range[1].time = time2.time
          } else {
            // self.range[1].time = self.range[0].time
            self.range[0].time = moment([
              startTime.year,
              startTime.month === 0 ? 0 : startTime.month - 1,
              startTime.day,
            ])
              .startOf('day')
              .valueOf()
            self.range[1].time = moment([
              endTime.year,
              endTime.month === 0 ? 0 : endTime.month - 1,
              endTime.day,
            ])
              .endOf('day')
              .valueOf()
          }
        } else {
          self.range[0].time = moment(self.range[0].time)
            .subtract(days, 'day')
            .startOf('day')
            .valueOf()
          self.range[1].time = moment(self.range[1].time)
            .subtract(days, 'day')
            .endOf('day')
            .valueOf()
        }
        self.rangeInt()
      } else if (this.option === 'C') {
        this.obj.enable = true
        if (this.dateObj) {
          this.obj.value = this.dateObj.value
        }
        self.time = this.getNimes(this.obj.operatorId, this.obj.value, 'pre')
      } else {
        self.time[0] = moment(self.time[0])
          .subtract(1, 'day')
          .startOf('day')
          .valueOf()
        self.time[1] = moment(self.time[1])
          .subtract(1, 'day')
          .endOf('day')
          .valueOf()
        self.selectedDate.day = parseInt(
          moment(self.time[0])
            .endOf('day')
            .format('D')
        )
        self.month = moment(self.time[0]).format('MMM')
        self.timeStamp = self.time[0]
        self.constractDays()
      }
      this.data()
    },
    nxtbtn() {
      let self = this
      this.between = true
      if (this.option === 'D') {
        self.time[0] = moment(self.time[0])
          .add(1, 'day')
          .startOf('day')
          .valueOf()
        self.time[1] = moment(self.time[1])
          .add(1, 'day')
          .endOf('day')
          .valueOf()
        self.selectedDate.day = parseInt(
          moment(self.time[0])
            .endOf('day')
            .format('D')
        )
        self.month = moment(self.time[0]).format('MMM')
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'W') {
        self.time[0] = moment(self.time[0])
          .add(1, 'week')
          .startOf('week')
          .valueOf()
        self.time[1] = moment(self.time[0])
          .add(0, 'week')
          .endOf('week')
          .valueOf()
        self.selectedDate.week = parseInt(moment(self.time[0]).week())
        self.month = moment(self.time[0]).format('MMM')
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'M') {
        self.time[0] = moment(self.time[0])
          .add(1, 'month')
          .startOf('month')
          .valueOf()
        self.time[1] = moment(self.time[0])
          .add(0, 'month')
          .endOf('month')
          .valueOf()
        self.month = moment(self.time[0]).format('MMM')
        self.year = parseInt(moment(self.time[0]).format('YYYY'))
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'Y') {
        self.time[0] = moment(self.time[0])
          .add(1, 'year')
          .startOf('year')
          .valueOf()
        self.time[1] = moment(self.time[0])
          .add(0, 'year')
          .endOf('year')
          .valueOf()
        self.year = parseInt(moment(self.time[0]).format('YYYY'))
        self.timeStamp = self.time[0]
        self.constractDays()
      } else if (this.option === 'R') {
        // let start = moment(self.time[0]).startOf('day').valueOf()
        // let end = moment(self.time[1]).endOf('day').valueOf()
        // let diff = end - start
        // let days = moment.duration(diff, 'milliseconds').days()
        // self.range[0].time = moment(self.range[0].time).add(days, 'day').startOf('day').valueOf()
        // self.range[1].time = moment(self.range[1].time).add(days, 'day').endOf('day').valueOf()
        // self.rangeInt()

        let start = moment(self.time[0])
          .startOf('day')
          .valueOf()
        let end = moment(self.time[1])
          .subtract(1, 'day')
          .endOf('day')
          .valueOf()
        let diff = end - start
        let days = moment.duration(diff, 'milliseconds').days()
        let time1 = {
          time: moment(start),
          day: parseInt(moment(start).format('D')),
        }
        let time2 = {
          time: moment(end),
          day: parseInt(moment(end).format('D')),
        }
        // let diffMonth = parseInt(time2.time.diff(time1.time, 'months'))
        let diffDays = parseInt(time2.time.diff(time1.time, 'days'))
        if (
          parseInt(moment(self.range[0].time).format('M')) !==
          parseInt(moment(self.range[1].time).format('M'))
        ) {
          let newtime1 = {
            time: moment(self.range[0].time)
              .subtract(diffDays, 'day')
              .startOf('day')
              .valueOf(),
          }
          let newtime2 = {
            time: moment(self.range[1].time)
              .subtract(diffDays, 'day')
              .startOf('day')
              .valueOf(),
          }
          let startTime = {
            month: parseInt(moment(newtime1.time).format('M')),
            year: parseInt(moment(newtime1.time).format('YYYY')),
            day: time1.day,
          }
          let endTime = {
            month: parseInt(moment(newtime2.time).format('M')),
            year: parseInt(moment(newtime2.time).format('YYYY')),
            day: time2.day,
          }
          if (
            parseInt(
              moment([startTime.year, startTime.month, startTime.day])
                .endOf('month')
                .format('D')
            ) < startTime.day
          ) {
            startTime.day = parseInt(
              moment([startTime.year, startTime.month, startTime.day])
                .endOf('month')
                .format('D')
            )
          }
          if (
            parseInt(
              moment([endTime.year, endTime.month, endTime.day])
                .endOf('month')
                .format('D')
            ) < endTime.day
          ) {
            endTime.day = parseInt(
              moment([endTime.year, endTime.month, endTime.day])
                .endOf('month')
                .format('D')
            )
          }
          let rangeMonth = endTime.month - startTime.month
          if (rangeMonth > 0) {
            let time1 = {}
            let time2 = {}
            time1.time = moment(self.range[0].time)
              .add(rangeMonth - 1 === 0 ? 1 : rangeMonth - 1, 'month')
              .valueOf()
            time2.time = moment(self.range[1].time)
              .add(rangeMonth - 1 === 0 ? 1 : rangeMonth - 1, 'month')
              .valueOf()
            self.range[0].time = time1.time
            self.range[1].time = time2.time
          } else {
            // self.range[1].time = self.range[0].time
            self.range[0].time = moment([
              startTime.year,
              startTime.month === 0 ? 0 : startTime.month - 1,
              startTime.day,
            ])
              .startOf('day')
              .valueOf()
            self.range[1].time = moment([
              endTime.year,
              endTime.month === 0 ? 0 : endTime.month - 1,
              endTime.day,
            ])
              .endOf('day')
              .valueOf()
          }
        } else {
          self.range[0].time = moment(self.range[0].time)
            .add(days, 'day')
            .startOf('day')
            .valueOf()
          self.range[1].time = moment(self.range[1].time)
            .add(days, 'day')
            .endOf('day')
            .valueOf()
        }
        self.rangeInt()
      } else if (this.option === 'C') {
        this.obj.enable = true
        if (this.dateObj) {
          this.obj.value = this.dateObj.value
        }
        self.time = this.getNimes(this.obj.operatorId, this.obj.value, 'next')
      } else {
        self.time[0] = moment(self.time[0])
          .add(1, 'day')
          .startOf('day')
          .valueOf()
        self.time[1] = moment(self.time[1])
          .add(1, 'day')
          .endOf('day')
          .valueOf()
        self.selectedDate.day = parseInt(
          moment(self.time[0])
            .endOf('day')
            .format('D')
        )
        self.month = moment(self.time[0]).format('MMM')
        self.timeStamp = self.time[0]
        self.constractDays()
      }
      this.data()
    },
    getNimes(id, value, state) {
      let val, time
      let val2 = 0
      if (value) {
        val = parseInt(value)
      }
      let timechoose = 0
      if (state) {
        if (state === 'pre') {
          val = val - 1
        } else if (state === 'next') {
          val2 = -(val - 1)
          val = 0
          timechoose = 1
        }
      }
      if (id === 42) {
        time = [
          moment(this.time[timechoose])
            .subtract(val, 'hour')
            .startOf('hour')
            .valueOf(),
          moment(this.time[timechoose])
            .subtract(val2, 'hour')
            .endOf('hour')
            .valueOf(),
        ]
      } else if (id === 49) {
        time = [
          moment(this.time[timechoose])
            .subtract(val, 'day')
            .startOf('day')
            .valueOf(),
          moment(this.time[timechoose])
            .subtract(val2, 'day')
            .endOf('day')
            .valueOf(),
        ]
      } else if (id === 50) {
        time = [
          moment(this.time[timechoose])
            .subtract(val, 'week')
            .startOf('week')
            .valueOf(),
          moment(this.time[timechoose])
            .subtract(val2, 'week')
            .endOf('week')
            .valueOf(),
        ]
      } else if (id === 51) {
        time = [
          moment(this.time[timechoose])
            .subtract(val, 'month')
            .startOf('month')
            .valueOf(),
          moment(this.time[timechoose])
            .subtract(val2, 'month')
            .endOf('month')
            .valueOf(),
        ]
      } else {
        time = [
          moment(this.time[timechoose])
            .subtract(val, 'hour')
            .startOf('hour')
            .valueOf(),
          moment(this.time[timechoose])
            .subtract(val2, 'hour')
            .endOf('hour')
            .valueOf(),
        ]
      }
      return time
    },
    getquarterData() {
      let fulldate = {}
      fulldate.time = this.quarters.find(rt => rt.label === this.value).value
      fulldate.filterName = 'R'
      fulldate.operatorId = 49
      fulldate.mounted = 2
      this.$emit('data', fulldate)
    },
    setYearData() {
      let fulldate = {
        time: this.value2 === 'thisyear' ? this.thisyear : this.lastyear,
        filterName: 'R',
        operatorId: 49,
        mounted: 2,
      }
      this.$emit('data', fulldate)
    },
  },
}
</script>
<style lang="scss">
.day-block {
  white-space: nowrap;
  width: 30px;
  height: 30px;
  cursor: pointer;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: 20px;
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
  /*border-color: #f93182 !important;*/
  border-color: var(--fc-theme-color) !important;
}
.day.active {
  background: #39b3c1 !important;
  color: #fff;
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
  /*background: #f93182;*/
  background: var(--fc-theme-color);
  border-radius: 3px;
  color: #fff;
}
/* .marginL {
 margin-left: 30px;
} */
.calender-div {
  height: 299px;
  margin-top: -79px;
  border-left: 1px solid #e3e3e3;
  margin-left: 15px;
  margin-right: 15px;
}
.cal-custom-day {
  padding: 10px;
  text-transform: uppercase;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
  cursor: pointer;
  text-align: left;
  border-left: 2px solid transparent;
}
.button-row {
  display: inline-flex;
}
.date-arrow {
  font-size: 1.5em;
  padding-top: 5px;
}
.button-row {
  &:not(.picker-disabled) .el-button {
    font-size: 13px;
    padding: 8px !important;
    border: none !important;
    /*color: #ef508f !important;*/
    color: var(--fc-theme-color);
    background: transparent !important;
  }
}
.button-row {
  &:not(.picker-disabled) {
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
  padding-right: 8px;
  padding-top: 0;
  padding-bottom: 8px;
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
/* .fc-black-theme .calender-popup {
 background: rgba(59, 59, 59, 0.96);
 border: gray;
}
.calender-popup {
 background: rgba(59, 59, 59, 0.96) !important;
 border: gray !important;
 color: #fff;
} */
.calender-popup [class^='el-icon-'] {
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
.cal-right-btn,
.cal-left-btn {
  cursor: pointer;
}
.week-row .day-block {
  font-size: 13px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: 24px;
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
.calender-popup {
  padding: 0px;
}
.datepicker-header {
  width: 100%;
  border-bottom: 1px solid #e3e3e3;
  padding-top: 10px;
  padding-bottom: 10px;
  padding-left: 8%;
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
  margin-right: 20px;
}
.calender-popup .custom-section {
  height: 250px;
  padding-top: 20px;
  margin-left: 25px;
}
.calender-popup .custom-section .cal-custom-day {
  margin-top: 10px;
  padding-left: 10px;
}
/* .arrow-hide {
 display: none;
} */
.DateQuarterSelecter {
  border: none !important;
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
  color: var(--fc-theme-color) !important;
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
