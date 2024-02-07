<template>
  <div>
    <date
      class="filter-field date-filter-comp uts-datefilter"
      @data="setDateFilter"
      v-if="true"
      :dateObj="filter"
      :filter="filter.filterName"
      :mills="filter.time"
    ></date>
    <div class="sample-ppm-section uts">
      <div class="ppm-conatiner">
        <div class="ppm-row">
          <div class="ppm-area">
            {{ $t('panel.card.scorecard') }}
          </div>
          <div class="ppm-fullscore">
            {{ $t('panel.card.weight') }}
          </div>
          <div class="non-compliance">
            {{ $t('panel.card.achieved') }}
          </div>
          <div class="total-points-earned">
            {{ $t('panel.card.final') }} (C=AxB)
            <!-- (Weighted Score) -->
          </div>
        </div>
      </div>
      <spinner :show="loading" size="80" v-if="loading"></spinner>
      <div v-else>
        <div
          class="ppm-row pointer tableData"
          :class="index !== tableData.length - 1 ? 'border-hide' : ''"
          v-for="(row, index) in tableData"
          :key="index"
        >
          <div class="ppm-area">
            {{ row.Criteria }}
          </div>
          <div class="ppm-fullscore">
            {{ row.A }}
          </div>
          <div class="non-compliance">
            {{ row.B }}
          </div>
          <div class="repeat-finding ppm-list-txt">
            {{ row.C }}
          </div>
        </div>
        <div class="ppm-row pointer border-hide">
          <div class="ppm-area ppm-list-txt">{{ $t('panel.card.max') }}</div>
          <div class="total-points-earned float-right">
            {{ data['Maximum Score'] }}
          </div>
        </div>
        <div class="ppm-row pointer">
          <div class="ppm-area ppm-list-txt">
            {{ $t('panel.card.total') }}
          </div>
          <div class="total-points-earned float-right">
            {{ data['Total Weightage Score Achieved'] }}
          </div>
        </div>
        <div class="ppm-row pointer">
          <div class="ppm-area ppm-list-txt ppm-pink-txt uppercase">
            {{ $t('panel.card.performance') }}
          </div>
          <div class="total-points-earned float-right ppm-pink-txt">
            {{ data['Performance Score'] || 0 }}%
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
      loading: false,
      tableData: [],
      filter: {
        time: [
          moment()
            .tz(this.$timezone)
            .startOf('month')
            .valueOf(),
          moment()
            .tz(this.$timezone)
            .endOf('month')
            .valueOf(),
        ],
        filterName: 'M',
        operatorId: 28,
        mounted: 2,
      },
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
      return null
    },
  },
  methods: {
    init() {
      let self = this
      self.loading = true
      let params = {
        dateFilter: this.filter.time,
        buildingId: this.buildingId,
      }
      this.$http.post('/dashboard/getUTCData', params).then(response => {
        if (response.data && response.data.resultJSON) {
          this.data = response.data.resultJSON
          this.tableData =
            response.data.resultJSON && response.data.resultJSON.tableData
              ? response.data.resultJSON.tableData
              : []
          self.loading = false
        }
      })
    },
    getquarterData() {
      this.init()
    },
    setDateFilter(date) {
      console.log('**** sample ppm date filter', date)
      this.filter.time = date.time
      this.init()
    },
    openWorkorder(id) {
      if (!this.mobile) {
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
  width: 16%;
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
  width: 20%;
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
.sample-ppm-section.uts {
  margin-top: 10px;
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
.empty-row {
  margin-top: 50px;
  border-bottom: 1px solid #eae8e8;
}
.uts-datefilter {
  top: 0px !important;
  right: 15px !important;
}
</style>
