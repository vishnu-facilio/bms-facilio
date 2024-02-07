<template>
  <!-- dict:  wr: wrapper -->
  <div>
    <spinner v-if="isLoading" :show="isLoading" size="80"></spinner>
    <div v-else>
      <div class="calendar-view-wr">
        <table>
          <tr class="header">
            <th>
              <div v-if="showSearchBar" class="top-left-cell">
                <FLookupField
                  :model.sync="employeesToFilter"
                  :field="employeeField"
                  :hideLookupIcon="true"
                  @update:model="updateEmployeeFilter"
                  style="width: 100%"
                />
              </div>
              <div v-else class="top-left-cell">
                <div>{{ $t('common.shift_planner.people') }}</div>
                <div>
                  <fc-icon
                    group="default"
                    name="search"
                    class="search-icon"
                    @click="toggleSearchBar"
                  ></fc-icon>
                  <fc-icon
                    group="default"
                    name="sort"
                    class="sort-icon"
                    @click="toggleSortOrderAndFetchEmployees()"
                  ></fc-icon>
                </div>
              </div>
            </th>
            <th class="day-card" v-for="day in range" :key="day">
              <div class="day-card-day">{{ getDay(day) }}</div>
              <div class="day-card-date">{{ getDate(day) }}</div>
            </th>
          </tr>
          <tr v-for="employee in employees" :key="employee.id">
            <td style="overflow: clip">
              {{ employee.name }}
            </td>
            <td
              v-for="(shift, ix) in shifts[employee.id]"
              :class="
                `shift-card ${allowShiftUpdate(shift) && 'shift-card-display'}`
              "
              :style="shiftCardStyle(shift)"
              :key="employee.id + ix + ''"
              :colspan="shift.span"
            >
              <div class="shift-card-wr">
                <div v-if="!shift.isWeeklyOff" class="shift-time">
                  <fc-icon
                    group="dsm"
                    name="clock"
                    size="8"
                    color="#324056"
                  ></fc-icon>
                  {{ readableTime(shift.startTime) }} -
                  {{ readableTime(shift.endTime) }}
                </div>
                <div class="shift-name">
                  {{ shift.isWeeklyOff ? 'Weekly Off' : shift.name }}
                </div>
              </div>
              <div
                v-if="allowShiftUpdate(shift)"
                class="shift-card-hover-wr"
                @click="editEmployeeShift(employee, shift)"
              >
                <div class="shift-card-hover-edit">
                  <span class="icon-bg">
                    <fc-icon
                      group="default"
                      name="edit"
                      size="20"
                      color="#324056"
                    ></fc-icon>
                  </span>
                </div>
                <div style="filter: blur(1px)">
                  <div class="shift-time">
                    <fc-icon
                      group="dsm"
                      name="clock"
                      size="8"
                      color="#324056"
                    ></fc-icon>
                    {{ readableTime(shift.startTime) }} -
                    {{ readableTime(shift.endTime) }}
                  </div>
                  <div class="shift-name">{{ shift.name }}</div>
                </div>
              </div>
            </td>
          </tr>
        </table>
      </div>
      <div class="pagination-view-footer d-flex">
        <div class="pagination-wr d-flex justify-content-center">
          <Pagination
            :currentPage.sync="pagination.page"
            :total="pagination.count"
            :perPage="pagination.perPage"
            @update:currentPage="fetchCurrentPageEmployees"
          ></Pagination>
        </div>
        <div></div>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import FLookupField from '@/forms/FLookupField'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
import { mapGetters, mapState } from 'vuex'
export default {
  name: 'CalendarView',
  components: {
    FLookupField,
    Pagination,
  },
  props: {
    timelineRange: {
      required: true,
      datatype: Object,
    },
  },
  mixins: [PeopleMixin],
  created() {
    this.fetchCurrentPageEmployees()
    this.$root.$on('reload-employee-shift', () => {
      this.fetchCurrentPageEmployees()
    })
    this.$root.$on('export-shift-planner-cal-csv', () => {
      this.exportShiftPlanner('csv')
    })
    this.$root.$on('export-shift-planner-cal-xlsx', () => {
      this.exportShiftPlanner('xlsx')
    })
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasUpdatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
  },
  watch: {
    timelineRange: {
      handler() {
        this.fetchCurrentPageEmployees()
      },
      deep: true,
    },
  },
  data() {
    return {
      isLoading: true,
      employees: null,
      showSearchBar: false,
      employeesToFilter: {
        peopleType: {
          operatorId: 54,
          value: ['2', '3'],
        },
      },
      range: null,
      shifts: null,
      sortType: 'asc',
      pagination: {
        page: 1,
        perPage: 10,
        count: 0,
      },
      employeeField: {
        isDataLoading: false,
        lookupModuleName: 'people',
        field: {
          lookupModule: {
            name: 'people',
            displayName: 'People',
          },
        },
        filters: {
          peopleType: {
            operatorId: 54,
            value: ['2', '3'],
          },
        },
        multiple: true,
      },
    }
  },
  methods: {
    allowShiftUpdate(slot) {
      const { epoch, span } = slot
      if (epoch && span) {
        const endDay = this.computeSlotEndDay(epoch, span)
        const today = this.getTodayDate()

        if (endDay < today) {
          return false
        }
        if (epoch <= today && endDay >= today) {
          return this.hasUpdatePermission
        }
      }
      return this.hasUpdatePermission
    },
    updateEmployeeFilter(employees) {
      this.employeesToFilter = employees
      this.fetchCurrentPageEmployees()
    },
    toggleSearchBar() {
      this.showSearchBar = !this.showSearchBar
    },
    toggleSortOrderAndFetchEmployees() {
      this.toggleSortOrder()
      this.fetchCurrentPageEmployees()
    },
    toggleSortOrder() {
      if (this.sortType === 'asc') {
        this.sortType = 'desc'
      } else {
        this.sortType = 'asc'
      }
    },
    shiftCardStyle(shift) {
      if (shift.isWeeklyOff) {
        return {
          'background-image':
            'repeating-linear-gradient( -45deg, transparent 0 20px, ' +
            shift.colorCode +
            ' 20px 25px )',
        }
      }
      return { 'background-color': shift.colorCode }
    },
    toggleShiftCardHover() {
      this.shiftCardHover = !this.shiftCardHover
    },
    getDate(d) {
      return `${new Date(d).getDate()}`.padStart(2, '0')
    },
    getDay(d) {
      const weekday = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
      return weekday[new Date(d).getDay()]
    },
    editEmployeeShift(employee, shift) {
      const { span, epoch } = shift
      const startDate = epoch
      const endDate = this.computeSlotEndDay(epoch, span)

      const shiftObj = { id: shift.shiftId }
      const empObj = { id: employee.id }
      let record = { employee: empObj, shift: shiftObj, startDate, endDate }

      this.$root.$emit('edit-day-record', record)
    },
    fetchCurrentPageEmployees() {
      this.fetchSchedule(
        this.pagination.page,
        this.pagination.perPage,
        this.sortType
      )
    },
    async exportShiftPlanner(format) {
      const { from, to } = this.timelineRange
      const { page, perPage } = this.pagination
      const sortType = this.sortType

      if (!(from || to || format || perPage || page || sortType)) {
        console.warn('precondition failure in exporting shift')
        return
      }
      let route = `/v3/shiftplanner/export?rangeFrom=${from}&rangeTo=${to}&format=${format}&perPage=${perPage}&page=${page}&orderBy=name&orderType=${sortType}`
      if (!isEmpty(this.employeesToFilter)) {
        route += `&filters=${encodeURIComponent(
          JSON.stringify(this.employeesToFilter)
        )}`
      }
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        window.open(data.url, '_blank')
      }
    },

    async fetchSchedule(page, perPage, sortType) {
      if (!(page || perPage || sortType)) {
        return
      }
      this.isLoading = true
      const { from, to } = this.timelineRange
      let route = `/v3/shiftplanner/calendar?withCount=true&rangeFrom=${from}&rangeTo=${to}&perPage=${perPage}&page=${page}&orderBy=name&orderType=${sortType}`
      if (!isEmpty(this.employeesToFilter)) {
        route += `&filters=${encodeURIComponent(
          JSON.stringify(this.employeesToFilter)
        )}`
      } else {
        this.showSearchBar = false
      }
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.employees = data.people
        this.shifts = data.shifts
        this.range = data.range
        this.pagination.count = data.count
      }
      this.isLoading = false
    },
  },
}
</script>

<style scoped>
.calendar-view-wr {
  overflow: scroll;
  max-height: calc(100vh - 175px);
  margin: 10px 10px 0 10px;
}

table {
  width: 100%;
  table-layout: fixed;
  background-color: white;
  border-collapse: separate;
  border-spacing: 0;
}

.day-card {
  text-align: center;
  color: #324056;
  padding: 5px;
}

.day-card > .day-card-day {
  font-size: 1.1em;
}

.day-card > .day-card-date {
  font-size: 1em;
}

.header {
  height: 10px;
}

.top-left-cell {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sort-icon {
  cursor: pointer;
}

.close-icon {
  cursor: pointer;
  margin: 2px;
}
.search-icon {
  cursor: pointer;
  border-right: 1px solid #e0e0e0;
  padding-right: 7px;
  margin-right: 7px;
}

/* date row */
table tr th {
  border-right: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
  border-top: 1px solid #e0e0e0;
  position: sticky;
  top: 0;
  z-index: 1;
  background: white;
  width: 165px;
}

/* stickifying first column */
table tr td:first-child,
table tr th:first-child {
  border-left: 1px solid #e0e0e0;
  border-right: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
  position: sticky;
  left: 0;
  z-index: 2;
  background: white;
  width: 230px;
  color: #324056;
}

/* left employee column */
table tr td:first-child {
  padding: 10px;
  font-weight: bold;
}

/* top left corner box */
table tr th:first-child {
  padding: 10px;
  z-index: 3;
}

tr {
  height: 75px;
}

.shift-card {
  padding: 5px;
  margin: 5px;
  min-width: 125px;
  border-right: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
  background-clip: content-box;
}

.shift-time {
  font-size: 0.8em;
  margin-left: 10px;
}

.shift-name {
  font-weight: bold;
  margin-top: 3px;
  margin-left: 10px;
}

.shift-card-hover-wr {
  display: none;
  position: relative;
}
.shift-card-hover-edit {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  width: 100%;
  height: 100%;
}
.shift-card-wr {
  display: block;
}

.shift-card-display:hover .shift-card-wr {
  display: none;
}

.shift-card-display:hover .shift-card-hover-wr {
  display: block;
}

.icon-bg {
  background: white;
  border-radius: 50%;
  padding: 5px;
}

.pagination-view-footer {
  background-color: white;
  margin-left: 10px;
  margin-right: 10px;
  border: 1px solid #e0e0e0;
}

.pagination-view-footer > .pagination-wr {
  min-width: 229px;
  border-right: 1px solid #e0e0e0;
  padding: 10px;
}
</style>
<style lang="scss">
.pagination-view-footer {
  .fc-black-small-txt-12 {
    color: #0f81fe;
  }
}
</style>
