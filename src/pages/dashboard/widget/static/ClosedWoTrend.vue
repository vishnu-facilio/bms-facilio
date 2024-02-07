<template>
  <div>
    <div
      class="row db-test-2 mobile-db-test-2-container"
      v-if="!countSummary.loading"
    >
      <div
        class="col-md-6 col-lg-6 db-test-2-l mobile-db-test-2-l-close-fL"
        style="text-align:center;padding: 10px;"
      >
        <div class="q-item-main q-item-section" @click="navigateTo('duetoday')">
          <div
            class="q-item-label"
            style="font-size: 42px;letter-spacing: 0.2px;color: #e4ba2d;text-align: center;"
          >
            {{ countSummary.data.dueToday }}
          </div>
          <div class="count-header">{{ $t('panel.card.due_today') }}</div>
        </div>
      </div>
      <div
        class="col-md-6 col-lg-6 db-test-2-r mobile-db-test-2-l-close-fR"
        style="text-align:center;padding: 10px;"
      >
        <div class="q-item-main q-item-section" @click="navigateTo('overdue')">
          <div
            class="q-item-label"
            style="font-size: 42px;letter-spacing: 0.2px;color: #e07575;text-align: center;"
          >
            {{ countSummary.data.overdueOpen }}
          </div>
          <div class="count-header">{{ $t('panel.card.overdue') }}</div>
        </div>
      </div>
    </div>
    <div class="en-divider"></div>
    <div class="row p10 fc-widget-header close-fc-widget-header">
      <div class="col-6 pull-left">
        <!-- <div class="f18 fc-widget-label">
                <i class="fa fa-info-circle demo-info-icon" aria-hidden="true" v-if="countSummary.isdemodata">
                  <q-tooltip>Sufficient data not available for this report, so we are showing demo data for you.</q-tooltip>
                </i>
              </div> -->
        <div class="fc-widget-sublabel">{{ countSummary.weekly }}</div>
      </div>
      <div class="col-6 text-right">
        <div class="pull-right">
          <div class="row fc-widget-option">
            <div
              class="filterButton"
              @click="countSummary.period = 'Last Week'"
              v-bind:class="{ active: countSummary.period === 'Last Week' }"
            >
              {{ $t('panel.card.last') }}
            </div>
            <div
              class="filterButton"
              @click="countSummary.period = 'Current Week'"
              v-bind:class="{ active: countSummary.period === 'Current Week' }"
            >
              {{ $t('panel.card.current') }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="sp-div-row"></div>
    <div class="sp-div-row"></div>
    <div class="sp-div-row"></div>
    <div
      class="db-prog-container close-db-prog-container"
      v-if="!countSummary.closed.loading"
    >
      <div class="row db-prog-row close-db-prog-row">
        <div class="col-4 db-prog-l close-db-prog-l">
          {{ $t('panel.card.ontime') }}
        </div>
        <div class="col-5 db-prog-c">
          <div
            class="q-progress db-progress-1 ontime-prog close-ontime-prog"
            style="margin-left:5px"
          >
            <div
              class="q-progress-track db-progress-track"
              style="width: 100%;"
            >
              &nbsp;
            </div>
            <div
              class="q-progress-model"
              style="border-radius:10px"
              v-bind:style="{
                width:
                  percent(
                    countSummary.data.workorders,
                    countSummary.data.Ontime
                  ) + '%',
              }"
            >
              &nbsp;
            </div>
          </div>
        </div>
        <div
          class="col-3 db-prog-l clickable close-db-prog-l"
          style="padding-right:10px"
        >
          {{ countSummary.data.Ontime }}
        </div>
      </div>
      <div class="row db-prog-row close-db-prog-row">
        <div class="col-4 db-prog-l close-db-prog-l">
          {{ $t('panel.card.overdue') }}
        </div>
        <div class="col-5 db-prog-c">
          <div
            class="q-progress db-progress-1 overdue-prog close-ontime-prog"
            style="margin-left:5px"
          >
            <div
              class="q-progress-track db-progress-track"
              style="width: 100%;"
            >
              &nbsp;
            </div>
            <div
              class="q-progress-model"
              style="border-radius:10px"
              v-bind:style="{
                width:
                  percent(
                    countSummary.data.workorders,
                    countSummary.data.Overdue
                  ) + '%',
              }"
            >
              &nbsp;
            </div>
          </div>
        </div>
        <div
          class="col-3 db-prog-l clickable close-db-prog-l"
          style="padding-right:10px"
        >
          {{ countSummary.data.Overdue }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  data() {
    return {
      countSummary: {
        loading: true,
        closed: {
          loading: true,
        },
        isdemodata: false,
        period: 'Current Week',
        weekly: '',
        data: {
          workorders: 0,
          Ontime: 0,
          Overdue: 0,
          dueToday: 0,
          overdueOpen: 0,
        },
      },
    }
  },
  mounted() {
    this.initdata()
    this.loadCountSummary()
  },
  computed: {
    today() {
      return this.$options.filters.formatDate(new Date(), true, false)
    },
  },
  watch: {
    'countSummary.period': function(newVal) {
      this.initdata()
    },
  },
  methods: {
    initdata() {
      let self = this
      self.countSummary.closed.loading = true
      self.countSummary.weekly = self.weekly(self.countSummary.period)
      self.$http
        .get(
          '/report/workorder/preventiveMaintenance?type=closed&period=' +
            self.countSummary.period
        )
        .then(function(response) {
          let reportData = response.data.reportData
          let Ontime = reportData.find(rt => rt.Ontime)
            ? reportData.find(rt => rt.Ontime).Ontime
            : 0
          let Overdue = reportData.find(rt => rt.Overdue)
            ? reportData.find(rt => rt.Overdue).Overdue
            : 0
          let countData = { Ontime: Ontime || 0, Overdue: Overdue || 0 }
          self.countSummary.data.Ontime = countData.Ontime
          self.countSummary.data.Overdue = countData.Overdue
          self.countSummary.isdemodata = false
          self.countSummary.closed.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.countSummary.closed.loading = false
        })
    },
    loadCountSummary() {
      let self = this

      self.countSummary.loading = true
      self.countSummary.weekly = self.weekly(self.countSummary.period)
      self.$http
        .get('/report/workorder/preventiveMaintenance?type=countSummary')
        .then(function(response) {
          let reportData = response.data.reportData
          let dueToday = reportData.find(row => row.label === 'dueToday')
          let overdue = reportData.find(row => row.label === 'Overdue')
          let countData = {
            dueToday: dueToday ? dueToday.value : 0,
            overdueOpen: overdue ? overdue.value : 0,
          }
          self.countSummary.data.dueToday = countData.dueToday
          self.countSummary.data.overdueOpen = countData.overdueOpen
          self.countSummary.isdemodata = false
          self.countSummary.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.countSummary.loading = false
        })
    },
    weekly(period) {
      switch (period) {
        case 'Current Week': {
          let firstDay = moment().startOf('week')
          let endDay = moment().endOf('week')
          return (
            moment(firstDay).format('DD') +
            ' - ' +
            this.$options.filters.formatDate(endDay, true, false)
          )
        }
        case 'Last Week': {
          let lastWeekStart = moment()
            .subtract(1, 'weeks')
            .startOf('week')
          let lastWeekEnd = moment()
            .subtract(1, 'weeks')
            .endOf('week')
          return (
            moment(lastWeekStart).format('DD') +
            ' - ' +
            this.$options.filters.formatDate(lastWeekEnd, true, false)
          )
        }
        case 'Today':
          return this.$options.filters.formatDate(new Date(), true, false)
      }
    },
    getDemoData(thisObject, period) {
      if (period === 'Current Week') {
        return thisObject.thisWeek
      } else if (period === 'Last Week') {
        return thisObject.lastWeek
      }
    },
    percent(total, value) {
      let percentage = (value / total) * 100
      if (isNaN(percentage)) {
        return 0
      } else {
        return percentage
      }
    },
    navigateTo(count) {
      if (!this.$mobile) {
        if (this.countSummary.isdemodata) {
          alert("This is a demo data, so you can't drill down the results.")
          return
        }
        let filterJson = {
          sourceType: this.preventiveCondition,
        }
        let filterPath = {
          path: '/app/wo/orders/' + count,
          query: {
            search: JSON.stringify(filterJson),
          },
        }
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}

          if (name) {
            this.$router.push({
              name,
              params: { viewname: count },
              query: {
                search: JSON.stringify(filterJson),
              },
            })
          }
        } else {
          this.$router.push(filterPath)
        }
      }
    },
  },
}
</script>
