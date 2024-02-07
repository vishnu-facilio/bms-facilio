<template>
  <div class="calender-height">
    <div>
      <div class="row datepicker-header">
        <div class="pointer"></div>
        <div
          class="p10 range-selecter"
          @click="chooseOption('D')"
          v-bind:class="{ active: option === 'D' }"
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
        <!-- <div class="p10 range-selecter" @click="chooseOption('C')" v-bind:class="{active: option === 'C'}">Custom</div> -->
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
          <div class="p5 day-block" v-for="(w, index) in weeks" :key="index">
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
              v-bind:class="{ active: selectedDate.day === parseInt(d.day) }"
            >
              {{ d.day }}
            </div>
          </div>
          <div class="calender-custom" v-if="option === 'D'">
            <div class="pull-left calender-div"></div>
            <div class="pull-left custom-block">
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
                  active: time[0] === yeaterDay[1] || time[0] === yeaterDay[0],
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
              v-bind:class="{ active: selectedDate.week === parseInt(d.week) }"
            >
              {{ d.day }}
            </div>
          </div>
          <div class="calender-custom" v-if="option === 'W'">
            <div class="pull-left calender-div"></div>
            <div class="pull-left custom-block">
              <div
                class="cal-custom-day"
                @click="customeDay('thisweek')"
                v-bind:class="{
                  active: time[0] === thisWeek[1] || time[0] === thisWeek[0],
                }"
              >
                This week
              </div>
              <div
                class="cal-custom-day"
                @click="customeDay('lastweek')"
                v-bind:class="{
                  active: time[0] === lastWeek[1] || time[0] === lastWeek[0],
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
          <div
            class="calender-custom col-4 custom-mothn-align"
            v-if="option === 'M'"
          >
            <div
              class="cal-custom-day"
              @click="customeDay('thismonth')"
              v-bind:class="{
                active: time[0] === thismonth[1] || time[0] === thismonth[0],
              }"
            >
              {{ $t('common.date_picker.this_month') }}
            </div>
            <div
              class="cal-custom-day"
              @click="customeDay('lastmonth')"
              v-bind:class="{
                active: time[0] === lastmonth[1] || time[0] === lastmonth[0],
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
                active: time[0] === last7days[0] || time[1] === last7days[0],
              }"
            >
              Last 7 days
            </div>
            <div
              class="cal-custom-day"
              @click="customeDay('last10days')"
              v-bind:class="{
                active: time[0] === last10days[0] || time[1] === last10days[0],
              }"
            >
              Last 10 days
            </div>
            <div
              class="cal-custom-day"
              @click="customeDay('last15days')"
              v-bind:class="{
                active: time[0] === last15days[0] || time[1] === last15days[0],
              }"
            >
              Last 15 days
            </div>
            <div
              class="cal-custom-day"
              @click="customeDay('last30days')"
              v-bind:class="{
                active: time[0] === last30days[0] || time[1] === last30days[0],
              }"
            >
              Last 30 days
            </div>
          </div>
        </div>
      </div>
      <div class="calender-range-section" v-if="option === 'R'">
        <div class="from-table">
          <div class="row calenderheader-range-row" v-if="option === 'R'">
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
            <div class="p5 day-block-range" v-for="w in weeks" :key="w">
              {{ w }}
            </div>
          </div>
          <div class="row day-row" v-if="option === 'R'">
            <div
              class="p5 day-block-range day"
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
          <div class="row calenderheader-range-row" v-if="option === 'R'">
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
            <div class="p5 day-block-range" v-for="w in weeks" :key="w">
              {{ w }}
            </div>
          </div>
          <div class="row day-row" v-if="option === 'R'">
            <div
              class="p5 day-block-range day"
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
  </div>
</template>
<script>
import moment from 'moment-timezone'
export default {
  data() {
    return {
      NumberOfDay: moment().daysInMonth(),
      days: [],
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
      weeks: ['Su', 'M', 'Tu', 'W', 'Th', 'F', 'Sa'],
      year: parseInt(moment().format('YYYY')),
      month: moment().format('MMM'),
      timeStamp: moment().valueOf(),
      time: [],
      option: 'D',
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
      selectedDate: {
        day: moment().format('D'),
        week: moment().week(),
      },
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
          label: 'This Week',
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
          label: 'This Month',
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
          label: 'Last Month',
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
          index: 3,
          label: 'This Year',
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
          label: 'Last 7 Days',
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
          label: 'Last 10 Days',
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
          label: 'Last 15 Days',
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
          label: 'Last 30 Days',
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
      ],
      obj: {
        operatorId: 49,
        value: '30',
        enable: false,
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
  watch: {
    time: function(val) {
      this.emitDate()
    },
  },
  methods: {
    initData() {
      let self = this
      this.NumberOfDay = moment().daysInMonth()
      self.year = moment().format('YYYY')
      self.month = moment(self.timeStamp).format('MMM')
      self.constractDays()
      // self.modeset()
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
    setMonth(label, value) {
      let self = this
      this.monthInNumber = value
      this.month = label
      self.getTime(parseInt(value))
    },
    setYear(y) {
      let self = this
      if (y) {
        this.year = y
      }
      self.getTime(self.months.find(row => row.label === self.month).value)
    },
    setdateObj(id, value) {
      this.dateObj.operatorId = id
      this.dateObj.value = value
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
      this.emitDate()
    },
    rangeInt() {
      this.mergeTimeStamp()
      this.constractRangeDay()
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
    mergeTimeStamp() {
      let self = this
      self.time[0] = self.range[0].time
      self.time[1] = self.range[1].time
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
    chooseOption(opt) {
      this.option = opt
      if (opt === 'R') {
        this.rangeInt()
        this.emitDate()
      }
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
    customeDay(option) {
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
    emitDate() {
      let self = this
      self.datePicker = false
      let fulldate = {}
      if (self.option === 'D') {
        self.time[1] = moment(self.time[0])
          .endOf('day')
          .valueOf()
        fulldate.time = self.time
        fulldate.time[0] = this.$helpers.getTimeInOrg(moment(fulldate.time[0]))
        fulldate.time[1] = this.$helpers.getTimeInOrg(moment(fulldate.time[1]))
      } else {
        self.temptime = self.time
        fulldate.time = self.time
        fulldate.time[0] = this.$helpers.getTimeInOrg(moment(fulldate.time[0]))
        fulldate.time[1] = this.$helpers.getTimeInOrg(moment(fulldate.time[1]))
      }
      fulldate.filterName = self.option
      if (this.dataObj) {
        fulldate.operatorId = this.dataObj.operatorId
      } else {
        fulldate.operatorId = this.operators.find(
          sb => sb.option === self.option
        ).value
      }
      if (this.obj.enable) {
        fulldate.value = this.obj.value
      }
      this.$emit('onSelect', fulldate)
    },
  },
}
</script>
<style>
.day-block {
  white-space: nowrap;
  width: 30px;
  height: 30px;
  cursor: pointer;
  text-align: center;
  margin-right: 30px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.2px;
  color: #333333;
  line-height: 20px;
  border-radius: 3px;
}
.day-row,
.month-row,
.year-row,
.range-row {
  width: 430px;
  height: 220px;
  text-align: center;
  align-items: center;
  margin-right: 30px;
}
.days-section .month-row {
  margin-left: 0;
}
.month-row {
  font-size: 12px;
  font-weight: 500;
  width: 500px;
}
.week-row {
  width: 430px;
  padding-top: 0px;
  text-align: center;
  align-items: center;
}
.calenderheader-row {
  justify-content: center;
  width: 400px;
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
  border-color: #f93182 !important;
  background: #e3f8fa;
}
.day.active {
  background: #39b3c1 !important;
  color: #fff;
  font-weight: 600;
  line-height: 20px;
  border-radius: 3px;
}
.year.hover,
.month.hover,
.day.hover {
  background: #39b3c1;
  opacity: 0.5;
}
.calender-range-section {
  display: inline-flex;
  margin-left: 0;
}
.range-selecter {
  font-size: 12px;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.6px;
  cursor: pointer;
  padding-left: 15px;
  padding-right: 15px;
  color: #385571;
}
.range-selecter.active {
  background: #f93182;
  border-radius: 3px;
  color: #fff;
  font-weight: bold;
}
.calender-div {
  height: 350px;
  margin-top: -101px;
  border-left: 1px solid #f1f3f6;
  margin-left: 15px;
  margin-right: 15px;
}
.cal-custom-day {
  padding: 3px 12px;
  text-transform: uppercase;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  cursor: pointer;
  text-align: left;
  border-left: 2px solid transparent;
  line-height: 24px;
  margin-bottom: 10px;
}
.button-row {
  display: inline-flex;
}
.date-arrow {
  font-size: 1.5em;
  padding-top: 5px;
}
.button-row .el-button {
  font-size: 13px;
  padding: 8px !important;
  border: none !important;
  color: #ef508f !important;
  background: transparent !important;
}
.button-row .el-button:hover {
  background: #ef508f50 !important;
}
.cal-custom-day.active {
  color: #000;
  border-left: 3px solid #f93181;
  background: #e3f8fa;
}
.day-block.day:hover {
  background: #e3f8fa;
}
.year-section {
  width: 68px;
  border-right: 1px solid #f1f3f6;
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
  height: 26px;
  cursor: pointer;
  border-left: 2px solid transparent;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.2px;
  text-align: center;
  color: #333333;
  line-height: 16px;
  margin-bottom: 15px;
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
.days-section {
  margin: 0;
}
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
  line-height: normal;
  letter-spacing: 0.2px;
  text-align: center;
  color: #000000;
  margin-right: 30px;
  line-height: 20px;
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
  border-bottom: 1px solid #f1f3f6;
  padding-top: 10px;
  padding-bottom: 10px;
}
.datepic-btn-row {
  justify-content: center;
  padding-top: 10px;
  padding-bottom: 10px;
  border-top: 1px solid #f1f3f6;
}
.month-container {
  height: 100%;
  padding-top: 20px;
  border-right: 1px solid #f1f3f6;
  margin-right: 20px;
}
.custom-section {
  height: 250px;
  padding-top: 20px;
  margin-left: 25px;
}
.custom-section .cal-custom-day {
  margin-top: 10px;
  padding-left: 10px;
  line-height: 24px;
}
.month-row {
  height: 100%;
}
.calender-range-section .calenderheader-range-row {
  width: 260px;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  justify-content: center;
  padding-top: 15px;
  text-align: center;
  -webkit-box-align: center;
  -ms-flex-align: center;
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
.calender-range-section .week-row {
}
.calender-range-section .week-row .day-block-range {
  white-space: nowrap;
  width: 30px;
  height: 30px;
  cursor: pointer;
  text-align: center;
  margin-right: 10px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.2px;
  color: #333333;
  line-height: 20px;
  border-radius: 3px;
}
.calender-range-section .day-row {
  width: 300px;
}
.calender-range-section .from-table {
  width: 300px;
}
.day-block-range {
  white-space: nowrap;
  width: 30px;
  height: 30px;
  cursor: pointer;
  text-align: center;
  margin-right: 10px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.2px;
  color: #333333;
  line-height: 20px;
  border-radius: 3px;
}
.custom-block {
  position: relative;
  margin-top: -60px;
}
.custom-mothn-align {
  margin-top: -160px;
}
</style>
