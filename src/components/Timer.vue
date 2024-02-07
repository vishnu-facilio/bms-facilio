<template>
  <div class="fc-timer">
    <div class="inline_flex">
      <!-- days -->
      <div class="t-days" v-if="days > 0">
        <div class="t-label" :style="bgColor" v-if="days < 9 && twoDigits">
          0{{ days }}
        </div>
        <div class="t-label" :style="bgColor" v-else>{{ days }}</div>
        <div class="t-sublabel uppercase" v-if="days < 2">
          {{ $t('maintenance._workorder.day') }}
        </div>
        <div class="t-sublabel uppercase" v-else>
          {{ $t('maintenance._workorder.days') }}
        </div>
      </div>

      <div class="t-separate" v-if="days > 0">:</div>

      <!-- hours -->
      <div class="t-hours">
        <div class="t-label" :style="bgColor" v-if="hours < 9 && twoDigits">
          0{{ hours }}
        </div>
        <div class="t-label" :style="bgColor" v-else>{{ hours }}</div>
        <div class="t-sublabel uppercase" v-if="hours < 2">
          {{ $t('maintenance._workorder.hour') }}
        </div>
        <div class="t-sublabel uppercase" v-else>
          {{ $t('maintenance._workorder.hours') }}
        </div>
      </div>

      <div class="t-separate">:</div>

      <!-- mins -->
      <div class="t-mins">
        <div class="t-label" :style="bgColor" v-if="mins < 9 && twoDigits">
          0{{ mins }}
        </div>
        <div class="t-label" :style="bgColor" v-else>{{ mins }}</div>
        <div class="t-sublabel uppercase" v-if="mins < 2">
          {{ $t('maintenance._workorder.min') }}
        </div>
        <div class="t-sublabel uppercase" v-else>
          {{ $t('maintenance._workorder.mins') }}
        </div>
      </div>

      <div class="t-separate" v-if="days <= 0">:</div>

      <!-- secs -->
      <div class="t-secs" v-if="days <= 0">
        <div class="t-label" :style="bgColor" v-if="secs < 9 && twoDigits">
          0{{ secs }}
        </div>
        <div class="t-label" :style="bgColor" v-else>{{ secs }}</div>
        <div class="t-sublabel uppercase" v-if="secs < 2">
          {{ $t('maintenance._workorder.sec') }}
        </div>
        <div class="t-sublabel uppercase" v-else>
          {{ $t('maintenance._workorder.secs') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// import moment from 'moment-timezone'
export default {
  props: ['time', 'color', 'twoDigits', 'staticTime', 'static', 'duration'],
  data() {
    return {
      days: '',
      hours: '',
      mins: '',
      secs: '',
      bgColor: 'color: ' + this.color + ';',
      intervalObj: null,
    }
  },
  watch: {
    time: function() {
      this.initCounter()
    },
    staticTime: {
      handler: function(newValue, oldValue) {
        if (newValue !== oldValue) {
          if (this.staticTime) {
            this.initCounter()
          }
        }
      },
    },
  },
  mounted() {
    this.initCounter()
  },
  methods: {
    initCounter() {
      let dateObj = new Date(this.time)
      let date
      let currentTime = Date.now()
      if (this.staticTime) {
        this.days = ''
        this.hours = ''
        this.mins = ''
        this.secs = ''
        if (this.intervalObj) {
          clearInterval(this.intervalObj)
          this.intervalObj = null
        }
        date = dateObj / 1000
        if (date < 0 && date > -2) {
          date = 0
        }
        let d = Math.floor(date / (24 * 60 * 60))
        let h = Math.floor((date % (24 * 60 * 60)) / 3600)
        let m = Math.floor((date % 3600) / 60)
        let s = Math.floor((date % 3600) % 60)

        this.days = d >= 0 ? d : 0
        this.hours = h >= 0 ? h : 0
        this.mins = m >= 0 ? m : 0
        this.secs = s >= 0 ? s : 0
      } else if (this.static) {
        this.days = ''
        this.hours = ''
        this.mins = ''
        this.secs = ''
        date = this.duration
        if (date < 0 && date > -2) {
          date = 0
        }
        let d = Math.floor(date / (24 * 60 * 60))
        let h = Math.floor((date % (24 * 60 * 60)) / 3600)
        let m = Math.floor((date % 3600) / 60)
        let s = Math.floor((date % 3600) % 60)

        this.days = d >= 0 ? d : 0
        this.hours = h >= 0 ? h : 0
        this.mins = m >= 0 ? m : 0
        this.secs = s >= 0 ? s : 0
      } else {
        date = (currentTime - dateObj) / 1000
        if (date < 0 && date > -2) {
          date = 0
        }
        let d = Math.floor(date / (24 * 60 * 60))
        let h = Math.floor((date % (24 * 60 * 60)) / 3600)
        let m = Math.floor((date % 3600) / 60)
        let s = Math.floor((date % 3600) % 60)

        this.days = d >= 0 ? d : 0
        this.hours = h >= 0 ? h : 0
        this.mins = m >= 0 ? m : 0
        this.secs = s >= 0 ? s : 0
        this.startCounter()
      }
    },
    startCounter() {
      if (this.intervalObj) {
        clearInterval(this.intervalObj)
        this.intervalObj = null
      }

      let self = this
      this.intervalObj = setInterval(function() {
        self.secs++
        if (self.secs >= 60) {
          self.secs = 0
          self.mins++
        }

        if (self.mins >= 60) {
          self.mins = 0
          self.hours++
        }

        if (self.hours >= 24) {
          self.hours = 0
          self.days++
        }
      }, 1000)
    },
  },
}
</script>
<style>
.fc-timer div.t-label {
  font-size: 20px;
  background-color: transparent !important;
  text-align: center;
  color: #2f2e49;
  text-align: center;
}
.fc-timer div.t-sublabel {
  color: rgb(98, 98, 105);
  text-align: center;
  padding-top: 3px;
}
.fc-timer div.t-mins,
.fc-timer div.t-secs,
.fc-timer div.t-hours,
.fc-timer div.t-days {
  flex: auto;
}
.fc-timer .t-separate {
  color: #939499;
  text-align: center;
  margin-left: 10px;
  margin-right: 10px;
  margin-top: 10px;
}
.t-days {
  flex: auto;
}
</style>
