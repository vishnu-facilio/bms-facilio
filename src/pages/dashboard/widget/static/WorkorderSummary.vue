<template>
  <spinner
    v-if="countSummary.loading"
    style="mT15"
    :show="countSummary.loading"
    size="80"
  ></spinner>
  <div v-else>
    <div
      class="row db-test-2 count-grid-container mobile-count-grid-container1"
    >
      <div
        class="col-md-6 col-lg-6"
        style="text-align:center;padding-left: 10px;"
      >
        <div class="q-item-main q-item-section">
          <div @click="openfilterurl('/app/wo/orders/duetoday')">
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
        class="col-md-6 col-lg-6"
        style="text-align:center;padding-right: 10px;"
      >
        <div class="q-item-main q-item-section">
          <div @click="openfilterurl('/app/wo/orders/overdue')">
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
          <div @click="openfilterurl('/app/wo/orders/open')">
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
          <div @click="openfilterurl('/app/wo/orders/unassigned')">
            <div
              class="q-item-label"
              style="font-size: 40px;letter-spacing: 0.2px;color: #e08c42;text-align:center;"
            >
              {{ countSummary.data.unassigned }}
            </div>
            <div class=" count-header">{{ $t('panel.card.unassigned') }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['widget'],
  data() {
    return {
      countSummary: {
        loading: true,
        isdemodata: false,
        data: {
          dueToday: 0,
          overdue: 0,
          unassigned: 0,
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
  },
  computed: {
    today() {
      return this.$options.filters.formatDate(new Date(), true, false)
    },
    ...mapState({
      ticketstatus: state => state.ticketStatus.workorder,
    }),
    reportFilters() {
      if (this.$route.query.filters) {
        return this.$route.query.filters
      }
      return null
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
    initdata() {
      let self = this
      self.countSummary.loading = true
      let params = {
        staticKey: 'workorderSummary',
      }
      if (this.$route.query.filters) {
        params.filters = this.$route.query.filters
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
            self.countSummary.data['dueToday'] = result.dueToday
            self.countSummary.data['overdue'] = result.overdue
            self.countSummary.data['unassigned'] = result.unassigned
            self.countSummary.data['open'] = result.open
          }
          self.countSummary.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.countSummary.loading = false
        })
    },
    openfilterurl(url) {
      if (!this.$mobile) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}

          if (name) {
            let viewname = url.split('orders/')[1]
            let query = {}
            let { filters } = this.$route.query || {}

            if (filters) {
              query = {
                search: filters,
                includeParentFilter: 'true',
              }
            }
            this.$router.push({
              name,
              params: { viewname },
              query,
            })
          }
        } else {
          if (this.$route.query && this.$route.query.filters) {
            this.$router.push({
              path: url,
              query: {
                search: this.$route.query.filters,
                includeParentFilter: 'true',
              },
            })
          } else {
            this.$router.push({ path: url })
          }
        }
      }
    },
  },
}
</script>
