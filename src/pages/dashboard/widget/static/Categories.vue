<template>
  <div>
    <div
      class="q-list categories-chart mobile-categories-chart"
      v-if="!categorySummary.loading"
    >
      <div class="row db-test-2" style="margin:5px;">
        <div
          class="col-md-6 col-lg-6 db-test-2-l mobile-categories-fL"
          style="text-align:center;padding-left: 10px;"
        >
          <div class="q-item-main q-item-section">
            <div
              class="q-item-label mobile-categories-label"
              style="font-size: 25px;text-align: center;"
            >
              {{ categorySummary.data.categories }}
            </div>
            <div class="secondary-color mobile-categories-txt f13">
              {{ $t('panel.card.categories') }}
            </div>
          </div>
        </div>
        <div
          class="col-md-6 col-lg-6 db-test-2-r mobile-categories-fR"
          style="text-align:center;padding-right: 10px;"
        >
          <div class="q-item-main q-item-section">
            <div
              class="q-item-label mobile-categories-label"
              style="font-size: 25px;"
            >
              {{ categorySummary.data.workorders }}
            </div>
            <div class="secondary-color mobile-categories-txt f13">
              {{ $t('panel.card.work_orders') }}
            </div>
          </div>
        </div>
      </div>
      <div class="db-prog-container mobile-db-prog-container">
        <div
          class="row db-prog-row"
          v-for="(category, index) in categorySummary.data.stats"
          :key="index"
          v-show="index < 8"
        >
          <div class="col-4 db-prog-l mobile-db-prog-l">
            {{ category.label }}
          </div>
          <div class="col-6 db-prog-c">
            <div
              class="q-progress db-progress-1 mobile-q-progress"
              style="margin-left:5px"
            >
              <div
                class="q-progress-track db-progress-track"
                style="width: 100%;"
              >
                &nbsp;
              </div>
              <div
                class="q-progress-model db-progress-1"
                v-bind:style="{
                  width:
                    percent(categorySummary.data.workorders, category.value) +
                    '%',
                }"
              >
                &nbsp;
              </div>
            </div>
          </div>
          <div
            class="col-2 db-prog-r clickable mobile-db-prog-r"
            @click="filterByCategory(category)"
          >
            {{ category.value }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import { mapState, mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  data() {
    return {
      categorySummary: {
        loading: true,
        isdemodata: false,
        data: {
          workorders: 0,
          categories: 0,
          stats: [],
        },
      },
    }
  },
  mounted() {
    this.initdata()
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
  },
  computed: {
    today() {
      return this.$options.filters.formatDate(new Date(), true, false)
    },
    ...mapGetters(['getTicketCategoryPickList']),
    ...mapState({
      users: state => state.users,
      ticketcategory: state => state.ticketCategory,
      ticketstatus: state => state.ticketStatus.workorder,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
  },
  methods: {
    initdata() {
      let self = this
      self.categorySummary.loading = true
      self.$http
        .get('/report/workorder/summary?type=category')
        .then(function(response) {
          console.log('******response', response)
          let reportData = response.data.reportData

          if (!reportData || !reportData.length) {
            self.categorySummary.isdemodata = true
            self.categorySummary.data = self.demoCategorySummary.data
          } else {
            let totalWorkOrders = 0
            let totalCategories = 0
            reportData = reportData.filter(row => {
              if (row.label) {
                row.id = row.label
                row.label = self.getTicketCategoryPickList()[row.label]
              } else {
                row.label = 'Unknown'
              }
              totalWorkOrders = totalWorkOrders + row.value
              return true
            })

            if (totalWorkOrders <= 0) {
              self.categorySummary.isdemodata = true
              self.categorySummary.data = self.demoCategorySummary.data
            } else {
              console.log(self.getTicketCategoryPickList())
              Object.keys(self.getTicketCategoryPickList()).forEach(function(
                key
              ) {
                let cate = self.getTicketCategoryPickList()[key]
                let thisCategory = reportData.find(row => row.label === cate)
                if (!thisCategory) {
                  thisCategory = {}
                  thisCategory.id = key
                  thisCategory.label = cate
                  thisCategory.value = 0
                  reportData.push(thisCategory)
                }
                totalCategories++
              })

              reportData.sort(function(a, b) {
                return a.value - b.value
              })
              self.categorySummary.data.workorders = totalWorkOrders
              self.categorySummary.data.categories = totalCategories
              self.categorySummary.data.stats = reportData.reverse()
              self.categorySummary.isdemodata = false
            }
          }

          self.categorySummary.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.categorySummary.loading = false
        })
    },
    percent(total, value) {
      let percentage = (value / total) * 100
      if (isNaN(percentage)) {
        return 0
      } else {
        return percentage
      }
    },
    filterByCategory(category) {
      if (this.categorySummary.isdemodata) {
        alert("This is a demo data, so you can't drill down the results.")
        return
      }
      let filterJson = {
        category: {
          module: 'workorder',
          operator: 'is',
          value: [category.id + '_' + category.label],
        },
      }
      if (!category.id) {
        filterJson.category.operator = 'is empty'
        filterJson.category.value = []
      }

      let filterPath = {
        path: '/app/wo/orders/open',
        query: {
          search: JSON.stringify(filterJson),
        },
      }
      if (!this.$mobile) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('workorder', pageTypes.LIST) || {}

          if (name) {
            this.$router.push({
              name,
              params: { viewname: 'open' },
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
<style>
.categories-chart .db-prog-row {
  margin-top: 15px;
  margin-bottom: 15px;
}
</style>
