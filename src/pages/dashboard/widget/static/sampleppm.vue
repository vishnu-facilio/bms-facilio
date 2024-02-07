<template>
  <div>
    <date
      class="filter-field date-filter-comp"
      @data="setDateFilter"
      v-if="false"
    ></date>
    <el-select
      v-model="value"
      placeholder="Select Quarter"
      @change="getquarterData"
      class="quarterSelecter"
    >
      <el-option
        v-for="(quarter, index) in quarters"
        :key="index"
        :label="quarter.label"
        :value="quarter.label"
      >
      </el-option>
    </el-select>
    <div class="sample-ppm-section">
      <div class="ppm-conatiner">
        <div class="ppm-row">
          <div class="ppm-area">
            {{ $t('panel.card.area') }}
          </div>
          <div class="ppm-fullscore">
            {{ $t('panel.card.fullscore') }}
          </div>
          <div class="non-compliance">
            {{ $t('panel.card.non_compliance') }}
          </div>
          <div class="repeat-finding">
            {{ $t('panel.card.repeat_finding') }}
          </div>
          <div class="total-points-earned">
            {{ $t('panel.card.total_points') }}
          </div>
        </div>
      </div>
      <spinner :show="loading" size="80" v-if="loading"></spinner>
      <div v-else>
        <div
          class="ppm-row pointer"
          v-if="data && data.length"
          v-for="(row, index) in data"
          :key="index"
          :class="row.area === 'Total' ? 'border-two' : 'border-hide'"
          @click="openWorkorder(row.workOrderId)"
        >
          <div class="ppm-area ppm-list-txt">
            {{ row.area }}
          </div>
          <div class="ppm-fullscore ppm-list-txt">
            {{ row.fullScore }}
          </div>
          <div class="non-compliance ppm-list-txt">
            {{ row.nonCompliance }}
          </div>
          <div class="repeat-finding ppm-list-txt">
            {{ row.repeatFinding }}
          </div>
          <div class="total-points-earned ppm-list-txt">
            {{ row.totalPointsEarned }}
          </div>
        </div>
        <div class="ppm-row border-hide">
          <div class="ppm-area ppm-list-txt ppm-pink-txt">
            {{ $t('panel.card.divisional_rating') }}
          </div>
          <div class="ppm-fullscore"></div>
          <div class="non-compliance "></div>
          <div class="repeat-finding "></div>
          <div class="total-points-earned ppm-pink-txt float-right">
            {{ overallRating }}%
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import date from '@/DatePicker'
import moment from 'moment-timezone'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  data() {
    return {
      data: [],
      overallRating: 0,
      loading: false,
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
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    buildingId() {
      if (this.$route.params.buildingid) {
        return parseInt(this.$route.params.buildingid)
      }
    },
  },
  methods: {
    init() {
      let self = this
      self.loading = true
      let params = {
        dateFilter: this.quarters.find(rt => rt.label === this.value).value,
        buildingId: this.buildingId,
      }
      this.$http.post('/dashboard/getAswaqData', params).then(response => {
        if (response.data && response.data.resultJSON) {
          this.data = response.data.resultJSON.tableData
          this.overallRating = response.data.resultJSON.overallRating
          self.loading = false
        }
      })
    },
    getquarterData() {
      this.init()
    },
    setDateFilter(date) {
      console.log('**** sample ppm date filter', date)
    },
    openWorkorder(id) {
      if (!this.$mobile) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: { viewname: 'all', id },
            })
          }
        } else {
          this.$router.push({ path: '/app/wo/orders/summary/' + id })
        }
      }
    },
  },
  components: {
    date,
  },
}
</script>

<style>
.ppm-row {
  width: 100%;
  border-bottom: 1px solid #eae8e8;
  padding-bottom: 23px;
  height: 0;
  padding-top: 23px;
  line-height: 1px;
  position: relative;
}
.ppm-area {
  width: 44%;
  float: left;
  text-align: left;
  font-weight: 500;
  padding-left: 20px;
  color: #7e7e7e;
  letter-spacing: 0.4px;
  font-size: 14px;
}
.ppm-fullscore {
  width: 14%;
  float: left;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #7e7e7e;
  text-align: left;
  font-weight: 500;
}
.non-compliance,
.repeat-finding,
.total-points-earned {
  width: 14%;
  float: left;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #7e7e7e;
  text-align: left;
  font-weight: 500;
}
.total-points-earned {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
}
.ppm-list-txt {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
}
.border-hide {
  border-bottom: transparent;
  padding-bottom: 23px;
  height: 0;
  padding-top: 23px;
  line-height: 1px;
}
.ppm-table-bg {
  background-color: #f9f9f9;
  border-bottom: transparent;
  padding-bottom: 23px;
  height: 0;
  padding-top: 23px;
  line-height: 1px;
}
.border-two {
  border-bottom: 1px solid #eae8e8;
  border-top: 1px solid #eae8e8;
  padding-bottom: 23px;
  height: 0;
  padding-top: 23px;
  line-height: 1px;
}
.ppm-pink-txt {
  font-size: 15px;
  font-weight: bold;
  letter-spacing: 0.5px;
  color: #ef508f;
}
.float-right {
  float: right;
}
.sample-ppm-section {
  margin-top: 4vh;
}
.quarterSelecter {
  position: absolute;
  top: 10px;
  right: 30px;
}
.quarterSelecter .el-input .el-input__inner,
.quarterSelecter .el-textarea .el-textarea__inner {
  border: none;
}
.el-select.quarterSelecter input.el-input__inner {
  font-size: 14px;
  font-weight: 500;
  color: #ef508f;
  width: 90px;
}
</style>
