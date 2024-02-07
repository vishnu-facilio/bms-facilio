<template>
  <div>
    <div
      class="row db-test-2 count-grid-container mobile-count-grid-container1"
      v-if="!countSummary.loading"
    >
      <div
        class="col-md-6 col-lg-6"
        style="text-align:center;padding-left: 10px;"
      >
        <div class="q-item-main q-item-section">
          <div @click="redirect('/app/wo/orders/myduetoday')">
            <div
              class="q-item-label"
              style="font-size: 40px;letter-spacing: 0.2px;color: #e4ba2d;text-align: center;"
            >
              {{ countSummary.data.dueToday }}
            </div>
            <div class="count-header">{{ $t('panel.card.due_today') }}</div>
          </div>
        </div>
      </div>
      <div
        class="col-md-6 col-lg-6 mobile-count-grid-container1-col1"
        style="text-align:center;padding-right: 10px;"
      >
        <div class="q-item-main q-item-section">
          <div @click="redirect('/app/wo/orders/myoverdue')">
            <div
              class="q-item-label"
              style="font-size: 40px;letter-spacing: 0.2px;color: #e07575;text-align:center;"
            >
              {{ countSummary.data.overdue }}
            </div>
            <div class="count-header">{{ $t('panel.card.overdue') }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="en-divider" style="margin:0 auto;"></div>
    <div
      class="row db-test-2 count-grid-container mobile-count-grid-container2"
    >
      <div
        class="col-md-6 col-lg-6"
        style="text-align:center;padding-left: 10px;"
      >
        <div class="q-item-main q-item-section">
          <div @click="redirect('/app/wo/orders/myopen')">
            <div
              class="q-item-label"
              style="font-size: 40px;letter-spacing: 0.2px;color: #5dc6d5;text-align: center;"
            >
              {{ countSummary.data.open }}
            </div>
            <div class="count-header">{{ $t('panel.card.open') }}</div>
          </div>
        </div>
      </div>
      <div
        class="col-md-6 col-lg-6"
        style="text-align:center;padding-right: 10px;"
      >
        <div class="q-item-main q-item-section">
          <div @click="redirect('/app/wo/orders/myopen', filterObj)">
            <div
              class="q-item-label"
              style="font-size: 40px;letter-spacing: 0.2px;color: #e08c42;text-align:center;"
            >
              {{ countSummary.data.openHighPriority }}
            </div>
            <div class=" count-header">{{ $t('panel.tyre.high') }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
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
        isdemodata: false,
        data: {
          dueToday: 0,
          overdue: 0,
          openHighPriority: 0,
          open: 0,
        },
      },
    }
  },
  mounted() {
    this.initdata()
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadTicketPriority')
  },
  computed: {
    today() {
      return this.$options.filters.formatDate(new Date(), true, false)
    },
    ...mapState({
      ticketstatus: state => state.ticketStatus.workorder,
    }),
    ...mapGetters(['getTicketPriorityByLabel']),
    reportFilters() {
      if (this.$route.query.filters) {
        return this.$route.query.filters
      }
      return null
    },
    filterObj() {
      let filterObj = {
        includeParentFilter: true,
        search: {
          priority: {
            operatorId: 36,
            value: [this.getTicketPriorityByLabel('High').id + ''],
          },
        },
      }
      return filterObj
    },
  },
  watch: {
    reportFilters: {
      handler(newData, oldData) {
        this.initdata()
      },
      immediate: true,
    },
  },
  methods: {
    redirect(url, query = {}) {
      if (url && !this.$mobile) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}

          if (name) {
            let viewname = url.split('orders/')[1]

            this.$router.push({
              name,
              params: { viewname },
              query,
            })
          }
        } else {
          this.$router.push({ path: url, query })
        }
      }
    },
    initdata() {
      let self = this
      self.countSummary.loading = true
      let params = {
        staticKey: 'mywosummary',
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          if (
            response.data &&
            response.data.cardResult &&
            response.data.cardResult.result
          ) {
            let result = response.data.cardResult.result
            console.log('dyfwduivw', result)
            self.countSummary.data['dueToday'] = result.dueToday
            self.countSummary.data['overdue'] = result.overdue
            self.countSummary.data['openHighPriority'] = result.openHighPriority
            self.countSummary.data['open'] = result.open
          }
          self.countSummary.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.countSummary.loading = false
        })
    },
  },
}
</script>
