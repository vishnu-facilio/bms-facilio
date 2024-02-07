<template>
  <div>
    <div class="" v-if="!technicianSummary.loading">
      <div class="q-list">
        <div
          class="q-item q-item-division relative-position  db-row-2"
          v-for="(technician, index) in technicianSummary.data.stats"
          :key="index"
          v-show="index < 6"
        >
          <div class="q-item-side q-item-side-left q-item-section ">
            <user-avatar
              size="md"
              :user="{
                name: technician.technician_name
                  ? technician.technician_name
                  : technician.technician_email,
                id: technician.label,
              }"
              :name="false"
            ></user-avatar>
          </div>
          <div class="q-item-main">
            <div
              class="q-item-label"
              style="text-overflow: ellipsis; overflow: hidden; width: 90%;"
            >
              {{
                technician.technician_name
                  ? technician.technician_name
                  : technician.technician_email
              }}
            </div>
          </div>
          <div
            class="q-item-side q-item-side-left q-item-section clickable"
            @click="filterByTechnician(technician)"
          >
            <div class="db-avatar-sm1">{{ technician.value }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment'
import { mapState } from 'vuex'
import UserAvatar from '@/avatar/User'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  data() {
    return {
      technicianSummary: {
        loading: true,
        isdemodata: false,
        period: 'Today',
        data: {
          stats: [],
        },
      },
    }
  },
  mounted() {
    this.initdata()
  },
  components: {
    UserAvatar,
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketStatus', 'workorder')
  },
  computed: {
    today() {
      return this.$options.filters.formatDate(new Date(), true, false)
    },
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
      self.technicianSummary.loading = true
      self.$http
        .get('/report/workorder/summary?type=technician')
        .then(function(response) {
          let reportData = response.data.reportData
          if (!reportData || !reportData.length) {
            self.technicianSummary.isdemodata = true
            self.technicianSummary.data = self.demoTechnicianSummary.data
          } else {
            reportData.sort(function(a, b) {
              return a.value - b.value
            })
            reportData = reportData.filter(row => {
              if (row.label) {
                let thisTechnician = self.users.find(
                  user => user.id === row.label
                )
                if (thisTechnician) {
                  row.technician_name = thisTechnician.name
                  row.technician_email = thisTechnician.email
                  return true
                } else {
                  return false
                }
              } else {
                return false
              }
            })
            console.log(reportData)
            self.technicianSummary.data.stats = reportData.reverse()
            self.technicianSummary.isdemodata = false
          }
          self.technicianSummary.loading = false
        })
        .catch(function(error) {
          console.log(error)
          self.technicianSummary.loading = false
        })
    },
    filterByTechnician(technician) {
      if (this.technicianSummary.isdemodata) {
        alert("This is a demo data, so you can't drill down the results.")
        return
      }
      let filterJson = {
        assignedTo: {
          module: 'workorder',
          operator: 'is',
          value: [
            technician.label +
              '_' +
              (technician.technician_name
                ? technician.technician_name
                : technician.technician_email),
          ],
        },
      }
      if (!technician.label) {
        filterJson.assignedTo.operator = 'is empty'
        filterJson.assignedTo.value = []
      }

      let filterPath = {
        path: '/app/wo/orders/open',
        query: {
          search: JSON.stringify(filterJson),
          includeParentFilter: 'true',
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
