<template>
  <shimmer-loading :config="{ type: 'form' }" v-if="loading"> </shimmer-loading>
  <div class="dragabale-card energy-cost h100" v-else>
    <div class="engery-contanier-first1 db-container1 h100">
      <div
        class="f14 fw6 h100 uppercase f16 mobile-energy-card-H"
        style="padding:8% 20% 10px;letter-spacing: 1.5px;font-size: 1vw;"
      >
        <center>{{ $t('panel.card.cst') }}</center>
      </div>
      <div class="gauge-container">
        <div class="gauge-a"></div>
        <div class="gauge-b"></div>
        <div class="gauge-bg"></div>
        <div
          class="gauge-c"
          :style="'transform:rotate(' + data.currentMonthPercent + 'deg);'"
        >
          <div class="c-radius"></div>
        </div>
        <div class="gauge-cg"></div>
        <div
          class="gauge-d"
          :style="'transform:rotate(' + data.lastMonthPercent + 'deg);'"
        >
          <div class="d-radius"></div>
        </div>
        <div class="gauge-dg"></div>
        <div class="gauge-datag"></div>
        <div class="gauge-data">
          <div class="inner-unit mobile-inner-unit">
            <span class="value mobile-energy-value">{{
              data.currentMonth
            }}</span
            ><span class="unit mobile-energy-unit">&nbsp;{{ $currency }}</span>
          </div>
          <div class="thisMonth uppercase mobile-energy-thismonth">
            {{ $t('panel.card.this_month') }}
          </div>
          <div class="date mobile-energy-date">{{ data.thismonth }}</div>
        </div>
      </div>
      <center class="energy-cost-divider">
        <div
          class="divider"
          style="width: 30%;opacity: 0.1;border-bottom: solid 1px #ffffff;height: 1px;"
        ></div>
      </center>
      <center style="" class="cost-lastmonth">
        <div style="" class="cost-lastmonth-span">
          <span
            v-if="buildingDetails.data.previousVal.cost"
            class="lastmonth-value mobile-lastmonth-value"
            >{{ data.lastMonth
            }}<span class="prev-val mobile-energy-prev-val"
              >&nbsp;{{ $currency }}</span
            ></span
          >
          <span v-else class="lastmonth-value">0</span>
        </div>
        <div class="secondarytext-color lastmonth mobile-energy-lastmonth">
          {{ $t('panel.card.last_month') }}
        </div>
        <div class="date mobile-energy-date">{{ data.lastMonthThisDate }}</div>
      </center>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget', 'currentDashboard'],
  data() {
    return {
      buildingDetails: {
        loading: false,
        data: [],
      },
      data: [],
      loading: true,
      guagestyle: {
        width: '200',
        height: '100',
      },
      configGuage: {
        color: ['#b671bc', '#f17791'],
        height: 150,
        data: [
          {
            text: '$3456 M',
            subText: 'November 2017',
            fillTicks: 13,
            majorTicks: 30,
            unit: this.$currency,
          },
        ],
      },
    }
  },
  components: {
    shimmerLoading,
  },
  computed: {
    buildingId() {
      if (this.currentDashboard) {
        if (this.currentDashboard.linkName === 'dsoem1dailyconsumption') {
          return 616227
        }
        return this.currentDashboard.buildingId
      }
      if (this.$route.params.dashboardlink === 'dsoem1dailyconsumption') {
        return 616227
      }
      if (this.$route.params.buildingid) {
        return parseInt(this.$route.params.buildingid)
      }
    },
  },
  mounted() {
    this.loadBuildingDetails()
  },
  methods: {
    loadBuildingDetails() {
      let self = this
      self.buildingDetails.loading = true
      self.loading = true
      const formData = new FormData()
      formData.append('buildingId', self.buildingId)
      self.$http
        .post('/report/energy/building/getBuildingDetails', formData)
        .then(function(response) {
          self.demoData(
            response.data.reportData.currentVal,
            response.data.reportData.previousVal
          )
          self.loadcardData()
          let reportData = response.data.reportData
          let daycount =
            moment()
              .endOf('month')
              .format('DD') - 1
          let todaydate = moment(new Date()).format('DD')
          let guagedata = [
            {
              text: Math.round(reportData.currentVal.cost)
                ? Math.round(reportData.currentVal.cost)
                : 0 + reportData.currentVal.currency,
              subText: self.$helpers.weekly('month'),
              fillTicks: todaydate,
              majorTicks: daycount,
              costUnit: '$',
              valueUnit: '',
            },
          ]
          self.configGuage.data = guagedata
          guagedata.costUnit = response.data.reportData.currentVal.currency
          guagedata.valueUnit = response.data.reportData.currentVal.costUnits
          self.buildingDetails.data = reportData
          self.buildingDetails.arrow = self.$helpers.arrowHandleClass(
            reportData.variance
          )
          self.purpose = reportData.purpose
          self.loading = false
        })
        .catch(function(error) {
          console.log(error)
          // self.energyConsumption.loading = false
          self.loading = false
        })
      self.buildingDetails.loading = false
    },
    loadcardData() {
      let self = this
      self.loading = true
      let params = {
        widgetId: self.widget.id,
        reportSpaceFilterContext: { buildingId: self.buildingId },
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.data = self.prepareData(response.data.cardResult)
          self.loading = false
        })
        .catch(function(error) {
          console.log(error)
        })
      self.loading = false
    },
    demoData(current, last) {
      let d = {}
      if (current.costUnits === ' K') {
        d.currentMonth = parseInt(current.cost) * 1000
      } else {
        d.currentMonth = parseInt(current.cost)
      }
      if (last.costUnits === ' K') {
        d.lastMonth = parseInt(last.cost) * 1000
      } else {
        d.lastMonth = parseInt(last.cost)
      }
      this.data = this.prepareData(d)
    },
    prepareData(data) {
      let d = {}
      if (data.currentMonth > 1000) {
        d.currentMonth = (data.currentMonth / 1000).toFixed(1) + 'k'
      } else {
        d.currentMonth = data.currentMonth.toFixed(1)
      }
      if (data.lastMonth > 1000) {
        d.lastMonth = (data.lastMonth / 1000).toFixed(1) + 'k'
      } else {
        d.lastMonth = data.lastMonth.toFixed(1)
      }
      if (data.currentMonth > data.lastMonth) {
        d.maxrange = data.currentMonth
      } else {
        d.maxrange = data.lastMonth
      }
      let lastMonthmills = data.lastMonthDate
        ? data.lastMonthDate
        : moment()
            .tz(this.$timezone)
            .add(-1, 'month')
            .valueOf()
      if (data.lastMonthThisDate) {
        if (data.lastMonthThisDate > 1000) {
          d.lastMonthThisDate =
            (data.lastMonthThisDate / 1000).toFixed(1) +
            ' k as on ' +
            moment(lastMonthmills)
              .tz(this.$timezone)
              .format('Do MMM')
        } else {
          d.lastMonthThisDate =
            data.lastMonthThisDate +
            ' as on ' +
            moment(lastMonthmills)
              .tz(this.$timezone)
              .format('Do MMM')
        }
      } else {
        d.lastMonthThisDate = ''
      }
      d.currentMonthPercent = this.degToPer(d.maxrange, data.currentMonth)
      d.lastMonthPercent = this.degToPer(d.maxrange, data.lastMonthThisDate)
      d.thismonth =
        'as on ' +
        moment()
          .tz(this.$timezone)
          .format('Do MMM')
      let currentDay = moment()
        .tz(this.$timezone)
        .format('D')
      let LastMonthDay = moment()
        .tz(this.$timezone)
        .subtract(1, 'month')
        .endOf('month')
        .format('D')
      console.log('********* last month', currentDay, LastMonthDay)
      if (parseInt(currentDay) > parseInt(LastMonthDay)) {
        d.lastMonthThisDate = ''
        d.lastMonthPercent = this.degToPer(d.maxrange, data.lastMonth)
      }
      return d
    },
    degToPer(range, value) {
      return (178 / range) * value
    },
  },
}
</script>
<style>
/* .dragabale-card svg {
  zoom: 73%;
} */
.prev-val {
  font-size: 11px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: left;
  color: #ffffff;
}
.cost-lastmonth {
  padding-bottom: 35px;
  position: absolute;
  width: 100%;
  top: 73%;
}
.cost-lastmonth-span {
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.4px;
}
.energy-cost-divider {
  align-self: center;
  padding-top: 10px;
  position: absolute;
  width: 100%;
  top: 65%;
}
.h100 {
  height: 100%;
}
.gauage-graph {
  position: absolute;
  width: 100%;
  top: 20%;
}

/* gauge chart update */
.gauge-container {
  width: 280px;
  height: 140px;
  position: absolute;
  top: 40%;
  left: 50%;
  overflow: hidden;
  text-align: center;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
}
.gauge-bg {
  z-index: 4;
  position: absolute;
  background-color: #4a1b80;
  width: 80%;
  height: 80%;
  top: 20%;
  left: 10%;
  right: 10%;
  border-radius: 250px 250px 0px 0px;
}
.gauge-a {
  z-index: 1;
  position: absolute;
  background-color: #4a1b80;
  width: 95%;
  height: 95%;
  top: 8%;
  left: 3%;
  right: 10%;
  border-radius: 250px 250px 0px 0px;
}

.gauge-b {
  z-index: 7;
  position: absolute;
  background-color: #4a1b80;
  width: 72%;
  height: 72%;
  left: 14.4%;
  top: 28%;
  border-radius: 250px 250px 0px 0px;
}

.gauge-cg {
  z-index: 2;
  position: absolute;
  /* background-color:rgb(76, 42, 118); */
  background-image: linear-gradient(to left, #7039a9, #7039a9);
  width: 86%;
  height: 86%;
  top: 100%;
  left: 7%;
  margin-left: auto;
  margin-right: auto;
  border-radius: 0px 0px 200px 200px;
  -webkit-transform-origin: center top;
  transform-origin: center top;
  transition: all 1.3s ease-in-out;
  transform: rotate(180deg);
  opacity: 0.4;
}

.gauge-c {
  z-index: 3;
  position: absolute;
  /* background-color: #f866a0; */
  background-image: linear-gradient(to left, #f866a0, #f73b6a);
  width: 86%;
  height: 86%;
  top: 100%;
  left: 7%;
  margin-left: auto;
  margin-right: auto;
  border-radius: 0px 0px 200px 200px;
  -webkit-transform-origin: center top;
  transform-origin: center top;
  transition: all 1.3s ease-in-out;
}
.gauge-d {
  z-index: 6;
  position: absolute;
  /* background-color: #59adac; */
  background-image: linear-gradient(to left, #59adac, #1a98ac);
  width: 75%;
  height: 75%;
  top: 100%;
  left: 13%;
  margin-left: auto;
  margin-right: auto;
  border-radius: 0px 0px 200px 200px;
  -webkit-transform-origin: center top;
  transform-origin: center top;
  transition: all 1.3s ease-in-out;
}
.gauge-datag {
  z-index: 8;
  position: absolute;
  /* background-color: #2d3f67; */
  background-image: linear-gradient(to left, #7039a9, #4a2973);
  width: 62%;
  height: 62%;
  left: 19%;
  top: 38%;
  border-radius: 250px 250px 0px 0px;
}
.gauge-dg {
  z-index: 5;
  position: absolute;
  /* background-color: rgb(76, 42, 118); */
  background-image: linear-gradient(to left, #7039a9, #7039a9);
  width: 75%;
  height: 75%;
  top: 100%;
  left: 13%;
  margin-left: auto;
  margin-right: auto;
  border-radius: 0px 0px 200px 200px;
  -webkit-transform-origin: center top;
  transform-origin: center top;
  transition: all 1.3s ease-in-out;
  transform: rotate(180deg);
  opacity: 0.4;
}
/* .gauge-container:hover .gauge-c  {  transform:rotate(130deg);
}
 .gauge-container:hover .gauge-d {  transform:rotate(80deg);} */
.gauge-container:hover .gauge-data {
  color: rgba(255, 255, 255, 1);
}

.gauge-data {
  z-index: 10;
  color: rgba(255, 255, 255, 0.2);
  font-size: 1em;
  line-height: 25px;
  position: absolute;
  width: 80%;
  height: 80%;
  top: 25%;
  left: 10%;
  margin-left: auto;
  margin-right: auto;
  transition: all 1s ease-out;
}
.inner-unit .value {
  font-size: 1.7vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: left;
  color: #ffffff;
  border: none;
}
.inner-unit .unit {
  font-size: 0.8vw;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: left;
  color: #ffffff;
}
.gauge-data .thisMonth {
  font-size: 0.8vw;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: center;
  color: #f866a0;
}
.gauge-data .date {
  opacity: 0.7;
}
.gauge-data .date,
.cost-lastmonth .date {
  font-size: 0.8vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: center;
  color: #ffffff;
  opacity: 0.7;
}
.gauge-data {
  top: 47%;
  position: absolute;
}
.cost-lastmonth .lastmonth-value {
  font-size: 1.7vw;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: left;
  color: #ffffff;
}
.cost-lastmonth .lastmonth {
  font-size: 0.8vw;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.9px;
  text-align: center;
  color: #61dbe4;
}
.cost-lastmonth .prev-val {
  font-size: 0.7vw;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0px;
  text-align: left;
  color: #ffffff;
}
.d-radius {
  position: relative;
  background: #2198ac;
  top: -2px;
  z-index: 12;
  width: 4px;
  height: 4px;
  border-top-right-radius: 5px;
  border-top-left-radius: 5px;
}
.c-radius {
  position: relative;
  background: #f73c6a;
  top: -3px;
  z-index: 12;
  width: 9px;
  height: 9px;
  border-top-right-radius: 11px;
  border-top-left-radius: 11px;
}
</style>
