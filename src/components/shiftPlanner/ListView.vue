<template>
  <spinner
    v-if="shift.isLoading || employee.isLoading"
    :show="shift.isLoading || employee.isLoading"
    size="100"
  ></spinner>
  <div v-else class="list-view-container">
    <div class="employee-list-container">
      <div v-if="showSearchBar" class="p5" style="margin-top: 3px">
        <FLookupField
          :model.sync="employeesToFilter"
          :field="employeeField"
          :hideLookupIcon="true"
          @update:model="updateEmployeeFilter"
        />
      </div>
      <div v-else class="employee-list-head">
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
            @click="toggleSortOrderAndFetchEmployees"
          ></fc-icon>
        </div>
      </div>
      <div class="employee-list-body">
        <div
          v-for="p in employee.list"
          :key="p.id"
          class="employee-list-item"
          :class="{ active: isEmployeeSelected(p) }"
          @click="setCurrentEmployee(p)"
        >
          {{ p.name }}
        </div>
      </div>
      <div class="employee-list-foot">
        <Pagination
          :currentPage.sync="pagination.current"
          :total="employee.count"
          :perPage="pagination.perPage"
          @update:currentPage="handleCurrentPageChange"
        ></Pagination>
      </div>
    </div>
    <div class="shift-list-container">
      <div class="shift-list-head">
        <div>{{ currentEmployeeName }}</div>
      </div>
      <div class="shift-list-body">
        <el-table
          id="shift-list-table"
          :data="shift.list"
          max-height="560"
          style="width: 100%"
          :cell-style="{ padding: '10px' }"
          :header-cell-style="{ padding: '10px' }"
        >
          <el-table-column label="Date">
            <template v-slot="data">
              {{ formatDateWithoutTime(data.row.epoch) }}
            </template>
          </el-table-column>
          <el-table-column prop="name" label="Shift"> </el-table-column>
          <el-table-column label="Start Time">
            <template v-slot="data">
              {{ readableTime(data.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column label="End Time">
            <template v-slot="data">
              {{ readableTime(data.row.endTime) }}
            </template>
          </el-table-column>

          <el-table-column v-if="hasUpdatePermission" width="100">
            <template v-slot="data">
              <div class="visibility-visible-actions">
                <el-button
                  v-if="allowShiftUpdate(data.row.epoch)"
                  type="text"
                  size="small"
                  @click="editDayRecord(data.row)"
                >
                  <i
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions"
                    data-arrow="true"
                    :title="$t('common._common.edit')"
                    v-tippy
                  ></i>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
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
  name: 'ListView',
  components: {
    Pagination,
    FLookupField,
  },
  mixins: [PeopleMixin],
  props: {
    timelineRange: {
      required: true,
      datatype: Object,
    },
  },
  created() {
    this.fetchCurrentPageEmployee()
    this.$root.$on('reload-employee-shift', () => {
      this.fetchCurrentEmployeeShift()
    })

    this.$root.$on('export-shift-planner-list-csv', () => {
      this.exportShiftPlanner('csv')
    })

    this.$root.$on('export-shift-planner-list-xlsx', () => {
      this.exportShiftPlanner('xlsx')
    })
  },
  watch: {
    timelineRange: {
      handler() {
        this.fetchCurrentEmployeeShift()
      },
      deep: true,
    },
  },
  data() {
    return {
      pagination: {
        current: 1,
        perPage: 10,
      },
      showSearchBar: false,
      employeesToFilter: {
        peopleType: {
          operatorId: 54,
          value: ['2', '3'],
        },
      },
      employee: {
        current: null,
        list: [],
        sortType: 'asc',
        count: 0,
        isLoading: true,
      },
      shift: {
        list: [],
        isLoading: true,
      },
      employeeField: {
        isDataLoading: false,
        lookupModuleName: 'people',
        field: {
          lookupModule: {
            name: 'people',
            displayName: 'people',
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
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasUpdatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    currentEmployeeName() {
      const { current } = this.employee
      return current ? current.name : '---'
    },
  },
  methods: {
    allowShiftUpdate(epoch) {
      const today = this.getTodayDate()
      if (epoch <= today) {
        return false
      }
      return this.hasUpdatePermission
    },
    updateEmployeeFilter(employees) {
      this.employeesToFilter = employees
      this.fetchCurrentPageEmployee()
    },
    toggleSortOrderAndFetchEmployees() {
      this.toggleSortOrder()
      this.fetchCurrentPageEmployee()
    },
    toggleSearchBar() {
      this.showSearchBar = !this.showSearchBar
    },
    toggleSortOrder() {
      if (this.employee.sortType === 'asc') {
        this.employee.sortType = 'desc'
      } else {
        this.employee.sortType = 'asc'
      }
    },
    editDayRecord(row) {
      const employee = this.employee.current
      const startDate = new Date(row.epoch)
      const endDate = new Date(row.epoch)
      let payload = { employee, startDate, endDate }
      this.$root.$emit('edit-day-record', payload)
    },
    isEmployeeSelected(p) {
      const selectedEmployeeID = this.employee?.current?.id
      return selectedEmployeeID && p.id === selectedEmployeeID
    },
    async fetchEmployees(currentPage, perPage, sortType) {
      if (!(currentPage || perPage || sortType)) {
        return
      }
      this.employee.isLoading = true
      let route = `/v3/modules/data/list?moduleName=people&withCount=true&orderBy=name&orderType=${sortType}&page=${currentPage}&perPage=${perPage}`
      if (!isEmpty(this.employeesToFilter)) {
        route += `&filters=${encodeURIComponent(
          JSON.stringify(this.employeesToFilter)
        )}`
      } else {
        this.showSearchBar = false
      }
      let { error, data, meta } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.employee.list = data.people
        this.employee.count = meta?.pagination?.totalCount || 0
        if (this.employee.list.length > 0) {
          this.employee.current = this.employee.list[0]
          this.fetchCurrentEmployeeShift()
        }
      }
      this.employee.isLoading = false
    },
    fetchCurrentPageEmployee() {
      this.fetchEmployees(
        this.pagination.current,
        this.pagination.perPage,
        this.employee.sortType
      )
    },
    async fetchShift(rangeFrom, rangeTo, employeeID) {
      if (!(rangeFrom || rangeTo || employeeID)) {
        console.warn('precondition failure in fetching shift')
        return
      }
      this.shift.isLoading = true
      let route = `/v3/shiftplanner/view?rangeFrom=${rangeFrom}&rangeTo=${rangeTo}&peopleId=${employeeID}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.shift.list = data.shifts
      }
      this.shift.isLoading = false
    },
    async exportShiftPlanner(format) {
      const { from, to } = this.timelineRange

      const { current } = this.employee
      const employeeID = current?.id

      const sortType = this.employee.sortType

      if (!(from || to || employeeID || format)) {
        console.warn('precondition failure in exporting shift')
        return
      }

      const route = `/v3/shiftplanner/export?rangeFrom=${from}&rangeTo=${to}&employeeID=${employeeID}&format=${format}&orderBy=name&orderType=${sortType}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        window.open(data.url, '_blank')
      }
    },
    fetchCurrentEmployeeShift() {
      const employeeID = this.employee?.current?.id
      this.fetchShift(
        this.timelineRange.from,
        this.timelineRange.to,
        employeeID
      )
    },
    setCurrentEmployee(employee) {
      this.employee.current = employee
      this.fetchCurrentEmployeeShift()
    },
    handleCurrentPageChange() {
      this.fetchCurrentPageEmployee()
    },
  },
}
</script>
<style scoped>
.sort-icon {
  cursor: pointer;
}
.close-icon {
  cursor: pointer;
  margin: 10px;
}
.search-icon {
  cursor: pointer;
  border-right: 1px solid #e0e0e0;
  padding-right: 7px;
  margin-right: 7px;
}
.list-view-container {
  display: flex;
  padding: 5px;
  box-sizing: border-box;
  padding: 10px;
  height: 85vh;
}
.employee-list-container {
  display: flex;
  flex-direction: column;
  border: solid 1px #e0e0e0;
  background-color: white;
}
.employee-list-head {
  width: 230px;
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  padding: 15px;
  align-content: center;
  align-items: center;
  margin-left: 5px;
}
.employee-list-body {
  width: 235px;
  overflow: scroll;
  min-height: 520px;
}
.employee-list-item {
  overflow: clip;
  padding: 15px 15px 35px 15px;
  border-top: solid 1px #e0e0e0;
  font-weight: bold;
}
.employee-list-item:hover {
  cursor: pointer;
}
.active {
  border-left: 5px solid #f53085;
  padding-left: 10px;
  background-color: #f6f8fc;
}
.employee-list-foot {
  width: 230px;
  flex-basis: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: solid 1px #e0e0e0;
}
.shift-list-container {
  width: 100%;
  background-color: white;
}
.shift-list-head {
  border-bottom: solid 1px #e0e0e0;
  padding: 15px 15px 15px 15px;
  font-weight: bold;
  height: 55px;
}
</style>

<style lang="scss">
.employee-list-foot {
  .fc-black-small-txt-12 {
    color: #0f81fe;
  }
}
</style>
