<template>
  <div>
    <spinner
      v-if="showSnippetViewLoader"
      :show="showSnippetViewLoader"
      size="100"
    ></spinner>
    <div v-else class="list-view-container">
      <div v-if="showEmployeeColumn" class="employee-list-container">
        <div
          v-if="showSearchBar"
          class="p5"
          style="margin-top: 3px; min-height: 58px"
        >
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
      <div class="slot-list-container">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import FLookupField from '@/forms/FLookupField'
import { mapGetters } from 'vuex'
export default {
  name: 'EmployeeSnippetView',
  components: {
    Pagination,
    FLookupField,
  },
  props: {
    myAttendance: {
      datatype: Boolean,
      default: false,
    },
  },
  created() {
    this.loadEmployee()
  },
  watch: {
    myAttendance: {
      handler() {
        this.loadEmployee()
      },
    },
  },
  data() {
    return {
      pagination: {
        current: 1,
        perPage: 10,
      },
      showSnippetViewLoader: false,
      showSearchBar: false,
      employeesToFilter: {
        peopleType: {
          operatorId: 54,
          value: ['2', '3'],
        },
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
      employee: {
        current: null,
        list: [],
        sortType: 'asc',
        count: 0,
      },
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    showEmployeeColumn() {
      return !this.myAttendance
    },
  },
  methods: {
    loadEmployee() {
      if (this.myAttendance) {
        this.fetchCurrentUser()
      } else {
        this.fetchCurrentPageEmployee()
      }
    },
    isEmployeeSelected(p) {
      const selectedEmployeeID = this.employee?.current?.id
      return selectedEmployeeID && p.id === selectedEmployeeID
    },
    toggleSearchBar() {
      this.showSearchBar = !this.showSearchBar
    },
    toggleSortOrderAndFetchEmployees() {
      this.toggleSortOrder()
      this.fetchCurrentPageEmployee()
    },
    toggleSortOrder() {
      if (this.employee.sortType === 'asc') {
        this.employee.sortType = 'desc'
      } else {
        this.employee.sortType = 'asc'
      }
    },
    async fetchCurrentPageEmployee() {
      await this.fetchEmployees(
        this.pagination.current,
        this.pagination.perPage,
        this.employee.sortType
      )
      if (this.employee.list.length > 0) {
        this.setCurrentEmployee(this.employee.list[0])
      }
    },
    async fetchCurrentUser() {
      const { peopleId } = this.getCurrentUser()
      await this.fetchEmployee(peopleId)
      if (this.employee.list.length > 0) {
        this.setCurrentEmployee(this.employee.list[0])
      }
    },
    async fetchEmployee(employeeID) {
      this.showSnippetViewLoader = true
      let route = `/v3/modules/people/${employeeID}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.employee.list = [data.people]
      }
      this.showSnippetViewLoader = false
    },
    async fetchEmployees(currentPage, perPage, sortType) {
      if (!(currentPage && perPage && sortType)) {
        return
      }
      this.showSnippetViewLoader = true
      let route = `/v3/modules/data/list?moduleName=people&withCount=true&orderBy=name&orderType=${sortType}&page=${currentPage}&perPage=${perPage}`
      if (!isEmpty(this.employeesToFilter)) {
        // const filter = {
        //   id: {
        //     operatorId: 36,
        //     value: this.employeesToFilter.map(e => String(e)),
        //   },
        // }
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
      }
      this.showSnippetViewLoader = false
    },
    handleCurrentPageChange() {
      this.fetchCurrentPageEmployee()
    },
    setCurrentEmployee(employee) {
      this.employee.current = employee
      this.$emit('setEmployee', employee)
    },
    updateEmployeeFilter(employees) {
      this.employeesToFilter = employees
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
  min-height: 61px;
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

.employee-list-foot {
  width: 235px;
  flex-basis: 10%;
  display: flex;
  justify-content: center;
  align-items: center;
  border-top: solid 1px #e0e0e0;
}

.slot-list-container {
  width: 100%;
  background-color: white;
}

.employee-list-item {
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
</style>
