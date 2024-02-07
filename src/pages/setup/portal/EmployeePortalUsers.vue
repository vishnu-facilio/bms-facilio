<template>
  <div>
    <template>
      <div class="fc-occupant-user-header">
        <div class="fc-black2-18 text-left bold">
          {{ $t('setup.users_management.users') }}
        </div>

        <div class="fc-user-search-header">
          <pagination
            ref="pagination"
            class="fc-occupant-pagiantion"
            :total="employeeUsersCount"
            :perPage="perPage"
            :currentPage.sync="page"
          >
          </pagination>
          <!-- filter option -->
          <div
            class="fc-portal-filter-border mT10"
            @click="showExtraFilter"
            :class="{ filterActive: showFilter }"
            v-tippy="{
              arrow: true,
              arrowType: 'round',
              animation: 'fade',
            }"
            content="Advanced filters"
          >
            <inline-svg
              class="pointer"
              src="svgs/dashboard/filter"
              iconClass="icon icon-md fc-fill-path-grey"
            ></inline-svg>
            <div
              :class="{
                'dot-active-pink': activeUserSearchData || activeTab,
              }"
            ></div>
          </div>
        </div>
      </div>

      <div class="fc-show-filter-con" v-if="showFilter">
        <div class="flex-middle">
          <div class="relative mR20">
            <div class="fc-black-13 text-left bold pB5">
              {{ $t('setup.setup.search') }}
            </div>
            <el-input
              placeholder="Search"
              v-model="activeUserSearchData"
              @change="searchUsers"
              class="fc-input-full-border2 width250px"
            ></el-input>
          </div>

          <div>
            <div class="fc-black-13 text-left bold pB5">
              {{ $t('setup.setup.type') }}
            </div>
            <el-select
              placeholder="Select"
              @change="changeActiveTab"
              v-model="activeTab"
              class="fc-input-full-border-select2"
              clearable
              filterable
            >
              <el-option
                v-for="(tab, inex) in tabList"
                :key="inex"
                :label="tab.label"
                :value="tab.value"
              >
              </el-option>
            </el-select>
          </div>
        </div>
      </div>

      <div class="occupantPortal-tabrow pL0 pR0">
        <setup-loader v-if="loading" class="m10 width98 shadow-none">
          <template #setupLoading>
            <spinner :show="loading" size="80"></spinner>
          </template>
        </setup-loader>

        <setup-empty
          v-else-if="$validation.isEmpty(employeeUsers) && !loading"
          class="m10 width98 shadow-none"
        >
          <template #emptyImage>
            <inline-svg
              src="svgs/copy2"
              iconClass="icon icon-sm-md"
            ></inline-svg>
          </template>
          <template #emptyHeading>
            {{ $t('setup.empty.empty_user') }}
          </template>
          <template #emptyDescription> </template>
        </setup-empty>

        <div class="col-lg-12 col-md-12" v-else>
          <table class="setting-list-view-table" width="100%">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.approvalprocess.name') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.setup_profile.email') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('setup.setup_profile.phone') }}
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  {{ $t('common.roles.role') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody>
              <tr
                class="tablerow visibility-visible-actions"
                v-for="(user, index) in employeeUsers"
                :key="index"
              >
                <td>
                  <user-avatar size="md" :user="user"></user-avatar>
                </td>
                <td>{{ user.email }}</td>
                <td>
                  {{ user.phone ? user.phone : '---' }}
                </td>
                <td>
                  {{ getRoleName(user) }}
                </td>
                <td class="nowrap">
                  <i
                    class="visibility-hide-actions el-icon-delete fc-setup-list-delete"
                    @click="removeEmployee(user)"
                  ></i>
                  <i
                    class="visibility-hide-actions el-icon-edit fc-setup-list-edit"
                    @click="editEmployeeUser(user)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
    <EmployeeUserForm
      v-if="showDialog"
      :isNew="isNew"
      formType="employee"
      :user="currentUser"
      :app="app"
      :save="saveEmployee"
      @onClose="showDialog = false"
    ></EmployeeUserForm>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import { isEmpty } from '@facilio/utils/validation'
import EmployeeUserForm from 'pages/setup/portal/PortalUserForm'
import pick from 'lodash/pick'
export default {
  props: ['app'],
  components: {
    UserAvatar,
    Pagination,
    SetupLoader,
    SetupEmpty,
    EmployeeUserForm,
  },
  data() {
    return {
      activeTab: '',
      loading: true,
      employeeUsers: [],
      showDialog: false,
      employees: [],
      employeeUsersCount: '',
      activeUserSearchData: null,
      employeesCount: '',
      currentUser: null,
      showEmployeeDialog: false,
      page: 1,
      perPage: 50,
      showFilter: false,
      fetchParams: {
        params: {
          filters: JSON.stringify({
            employeePortalAccess: {
              operatorId: 15,
              value: ['true'],
            },
          }),
        },
      },
      tabList: [
        {
          label: 'All',
          value: 'all',
        },
        {
          label: 'Employees',
          value: 'employees',
        },
      ],
    }
  },
  async created() {
    await this.$store.dispatch('loadRoles')
    await this.loadEmployeesCount()
    await this.loadEmployees()
  },
  computed: {
    ...mapGetters(['getRoleNameById']),

    appId() {
      return this.app.id
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.loadEmployees()
      }
    },
  },
  methods: {
    async loadUsersList() {
      await this.loadEmployeesCount()
      await this.loadEmployees()
    },
    async loadEmployeesCount() {
      this.loading = true
      this.loading = true
      let { appId } = this
      let genericSearch
      let filters = {}
      if (this.activeTab === 'all') {
        filters = {
          employeePortalAccess: {
            value: ['true'],
            operatorId: 15,
          },
          peopleType: {
            operatorId: 54,
            value: ['3'],
          },
        }
      } else if (this.activeTab === 'employees') {
        filters = {
          peopleType: {
            operatorId: 54,
            value: ['3'],
          },
          employeePortalAccess: {
            value: ['true'],
            operatorId: 15,
          },
        }
      } else {
        filters = {
          employeePortalAccess: {
            value: ['true'],
            operatorId: 15,
          },
          peopleType: {
            operatorId: 54,
            value: ['3'],
          },
        }
      }

      if (this.activeUserSearchData) {
        genericSearch = this.activeUserSearchData
      }

      let { error, data } = await API.get(
        `/v2/people/list?fetchCount=true&page=${this.page}&perPage=50`,
        {
          filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          appId,
          genericSearch,
        }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.employeeUsersCount = data.recordCount || []
      }
      this.loading = false
    },
    async loadEmployees() {
      this.loading = true
      this.loading = true
      let { appId } = this
      let genericSearch
      let filters = {}
      if (this.activeTab === 'all') {
        filters = {
          employeePortalAccess: {
            value: ['true'],
            operatorId: 15,
          },
          peopleType: {
            operatorId: 54,
            value: ['3'],
          },
        }
      } else if (this.activeTab === 'employees') {
        filters = {
          peopleType: {
            operatorId: 54,
            value: ['3'],
          },
          employeePortalAccess: {
            value: ['true'],
            operatorId: 15,
          },
        }
      } else {
        filters = {
          employeePortalAccess: {
            value: ['true'],
            operatorId: 15,
          },
          peopleType: {
            operatorId: 54,
            value: ['3'],
          },
        }
      }

      if (this.activeUserSearchData) {
        genericSearch = this.activeUserSearchData
      }

      let { error, data } = await API.get(
        `/v2/people/list?page=${this.page}&perPage=50`,
        {
          filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          appId,
          genericSearch,
        }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.employeeUsers = data.people || []
      }
      this.loading = false
    },
    changeActiveTab() {
      this.loadEmployees()
      this.loadEmployeesCount()
    },

    getRoleName(user) {
      let { linkName } = this.app
      let rolesMap = user.rolesMap || {}
      let roleId = rolesMap[linkName]

      return roleId ? this.getRoleNameById(roleId) : '---'
    },

    async removeEmployee(user) {
      user.employeePortalAccess = false
      let {
        id,

        employeePortalAccess,
        rolesMap,
      } = user || {}
      let params = {
        employees: [{ id, employeePortalAccess, rolesMap }],
      }
      let { error } = await API.post('/v2/employee/update', params)
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
        throw new Error()
      } else {
        // this.$message.success(this.$t('people.employee.update_success'))
        this.$message.success(this.$t('common._common.delete_success'))

        this.loadEmployees()
        this.loadEmployeesCount()
      }
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
    clearUser() {
      this.activeUserSearchData = null
    },
    searchUsers() {
      this.page = 1
      this.perPage = 50
      this.loadEmployees()
      this.loadEmployeesCount()
    },
    editEmployeeUser(user) {
      this.isNew = false
      this.showDialog = true
      this.currentUser = user
    },
    saveEmployee(employee) {
      let { isNew } = this

      let url = ''
      let successMsg = ''
      if (isNew) {
        url = '/v2/employee/addfromPortal'
        successMsg = this.$t('common.products.new_employee_added')
      } else {
        url = '/v2/employee/updatefromPortal'
        successMsg = this.$t('common.products.employee_updated')
      }

      let props = [
        'name',
        'email',
        'phone',
        'rolesMap',
        'securityPolicyMap',
        'language',
      ]
      if (!isNew) props.push('id')

      let params = {
        employees: [
          {
            ...pick(employee, props),
            peopleType: 3,
            employeePortalAccess: true,
          },
        ],
      }
      return API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Ocurred')
          throw new Error()
        } else {
          this.$message(successMsg)
          this.loadEmployees()
          this.loadEmployeesCount()
        }
      })
    },
  },
}
</script>
<style lang="scss">
.user-tabs {
  position: relative;
  top: 7px;
  .el-radio-button {
    .el-radio-button__inner {
      background: none;
      border: none;
      box-shadow: none;
      padding: 0 20px;
      height: 36px;
      line-height: 38px;
      display: inline-block;
      position: relative;
      font-weight: 500;
      font-size: 11px;
      color: #324056;
      text-transform: uppercase;
      letter-spacing: 0.6px;
    }
    &.is-active .el-radio-button__inner {
      font-weight: bold;
      color: #385571;
      letter-spacing: 0.6px;
    }
    &.is-active::after {
      content: '';
      position: absolute;
      width: 30%;
      bottom: 0;
      left: 20px;
      border: 1px solid #ee518f;
    }
  }
}
.portal-user-search {
  .fc-input-full-border-h35 {
    margin-top: 0;
    .el-input__inner {
      width: 250px;
      height: 40px !important;
      background: #fff;
      padding-left: 10px;
    }
    .el-input__prefix {
      display: none;
    }
    .el-input__suffix {
      top: -5px;
      right: 15px;
    }
    .el-icon-close {
      color: #324056;
      font-size: 18px;
    }
  }
  .f-quick-search-input {
    margin-top: -5px;
  }
}
.fc-occupant-user-header {
  height: 50px;
  padding-left: 20px;
  padding-right: 10px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.fc-user-search-header {
  position: relative;
  top: -4px;
  display: flex;
  align-items: center;
}
.fc-occupant-search-user {
  .fc-portal-close-icon {
    top: 10px;
    left: 224px;
  }
}
</style>
