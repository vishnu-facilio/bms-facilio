<template>
  <div class="fc-setup-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.setup.rolesPermission') }}
      </template>
      <template #description>
        {{ $t('setup.users_management.list_of_roles') }}
      </template>
      <template #actions>
        <el-select
          v-if="canShowApps"
          v-model="appId"
          placeholder="Select App"
          filterable
          @change="loadRoles"
          class="mL-auto mR20 fc-input-full-border2"
        >
          <el-option
            v-for="app in applications"
            :key="app.linkName"
            :label="app.name"
            :value="app.id"
          >
          </el-option>
        </el-select>
        <div class="action-btn setting-page-btn">
          <el-button type="primary" @click="addRole()" class="setup-el-btn">
            {{ $t('setup.users_management.new_role') }}
          </el-button>
          <div v-if="showWebTabRolePermission">
            <NewWebTabPermission
              v-if="$helpers.isLicenseEnabled('NEW_TAB_PERMISSIONS')"
              :role="role"
              :app="selectedApp"
              @onSave="onRoleSaved"
              @onClose="showWebTabRolePermission = false"
            ></NewWebTabPermission>
            <WebTabRolePermission
              v-else
              :role="role"
              :app="selectedApp"
              @onSave="onRoleSaved"
              @onClose="showWebTabRolePermission = false"
            ></WebTabRolePermission>
          </div>
          <RolePermission
            v-if="showOldRolePermission"
            :isNew="isNew"
            :role="role"
            :appId="appId"
            @saved="onRoleSaved"
            @onClose="showOldRolePermission = false"
          ></RolePermission>
          <RoleDetailsForm
            v-if="showRoleDetails"
            :appId="appId"
            :isNew="isNew"
            :role="role"
            :isWebTabRole="isWebTabRole"
            @onSave="onRoleSaved"
            @onClose="showRoleDetails = false"
          ></RoleDetailsForm>
        </div>
      </template>
      <template #searchAndPagination class="p10">
        <div class="flex-middle">
          <pagination
            :total="roleCount"
            :perPage="perPage"
            class="nowrap pT5"
            ref="f-page"
            :pageNo="rolePage"
            @onPageChanged="setPage"
          ></pagination>
        </div>
      </template>
    </SetupHeader>
    <div class="mB15">
      <SetupLoader v-if="loading">
        <template #setupLoading>
          <spinner :show="loading" size="80"></spinner>
        </template>
      </SetupLoader>
      <SetupEmpty v-else-if="$validation.isEmpty(rolelist) && !loading">
        <template #emptyImage>
          <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
        </template>
        <template #emptyHeading>
          {{ $t('setup.users_management.no_roles_available') }}
        </template>
      </SetupEmpty>
      <div v-else class="mL10 mT10 mR10">
        <el-table
          :data="sortedRoles"
          class="width100 fc-setup-table fc-setup-table-th-borderTop"
          height="calc(100vh - 210px)"
          :fit="true"
          :header-cell-style="{ background: '#f3f1fc' }"
        >
          <el-table-column
            :label="$t('setup.approvalprocess.name')"
            prop="name"
            :width="300"
          >
            <template v-slot="role">
              <div
                @click="
                  !role.row.isPrevileged ? showEditRole(role.row) : () => {}
                "
              >
                {{ role.row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="description"
            :label="$t('setup.approvalprocess.description')"
            :width="330"
          >
            <template v-slot="role">
              {{ role.row.description || '---' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="createdTime"
            :label="$t('setup.users_management.created_time')"
            :width="330"
          >
            <template v-slot="role">
              {{ role.row.createdTime | fromNow }}
            </template>
          </el-table-column>
          <el-table-column
            prop
            label
            width="180"
            class="visibility-visible-actions"
            fixed="right"
          >
            <template v-slot="role">
              <div
                v-if="!role.row.isPrevileged"
                class="text-center template-actions"
              >
                <i
                  class="el-icon-edit edit-icon visibility-hide-actions pR15"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  v-tippy
                  @click="openRoleDetails(role.row)"
                ></i>
                <i
                  class="el-icon-delete fc-delete-icon visibility-hide-actions"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  v-tippy
                  @click="deleteRole(role.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script>
import SetupHeader from 'pages/setup/components/SetupHeader'
import RolePermission from './RolePermission'
import RoleDetailsForm from './RoleDetailsForm'
import WebTabRolePermission from './WebTabRolePermission'
import NewWebTabPermission from 'pages/setup/roles/NewWebTabPermission'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import { getFilteredApps, loadApps } from 'util/appUtil'
import { isEmpty } from '@facilio/utils/validation'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import Pagination from 'pages/setup/components/SetupPagination'
export default {
  name: 'RolesList',
  title() {
    return 'Roles'
  },
  components: {
    RolePermission,
    RoleDetailsForm,
    WebTabRolePermission,
    NewWebTabPermission,
    SetupHeader,
    SetupLoader,
    SetupEmpty,
    Pagination,
  },
  data() {
    return {
      applications: [],
      appId: null,
      showOldRolePermission: false,
      loading: true,
      rolelist: [],
      isNew: false,
      role: {},
      showWebTabRolePermission: false,
      showRoleDetails: false,
      perPage: 50,
      roleCount: null,
      rolePage: 1,
    }
  },
  async created() {
    await this.loadAvailableApps()
    await this.loadRoles()
  },
  computed: {
    roleQuery() {
      let { query } = this.$route || {}
      return query.newRoles === 'true'
    },
    isWebTabRole() {
      let { linkName } = this.applications.find(a => a.id === this.appId)
      return linkName !== 'newapp' || this.roleQuery
    },
    canShowApps() {
      let { applications } = this
      return applications.length > 1
    },

    sortedRoles() {
      let roles = cloneDeep(this.rolelist)

      roles.sort((role1, role2) => {
        let adminRoles = ['Super Administrator', 'Administrator']

        if (adminRoles.includes(role1.name)) {
          return -1
        } else if (adminRoles.includes(role2.name)) {
          return 1
        } else {
          return role1.name.localeCompare(role2.name)
        }
      })
      return roles
    },
    selectedApp() {
      return this.applications.find(a => a.id === this.appId)
    },
  },
  watcher: {
    rolePage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadRoles()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadAvailableApps() {
      let { error, data } = await loadApps()
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.applications = getFilteredApps(data)
        this.applications = this.applications.filter(
          app => app.linkName !== 'dev'
        )

        const { linkName: currentApp } = getApp()
        const selectedApp =
          this.applications.find(app => app.linkName === currentApp) ||
          this.applications[0]
        this.appId = (selectedApp || {}).id
      }
    },
    async loadRoles() {
      let { appId } = this
      let { linkName } = getApp()
      let params = {}

      if (appId) {
        params = { appId }
      } else {
        params = { linkName }
      }

      this.loading = true
      let { error, data } = await API.get('/setup/roles', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolelist = data.roles || []
        this.roleCount = this.rolelist.length || 0
      }
      this.loading = false
    },
    addRole() {
      this.isNew = true
      this.role = {}
      this.showRoleDetails = true
    },
    setPage(page) {
      this.rolePage = page
    },
    showEditRole(role) {
      this.role = this.$helpers.cloneObject(role)
      this.isNew = false

      if (this.isWebTabRole) {
        this.showWebTabRolePermission = true
      } else {
        this.showOldRolePermission = true
      }
    },
    openRoleDetails(role) {
      this.role = this.$helpers.cloneObject(role)
      this.isNew = false

      if (this.isWebTabRole) this.showRoleDetails = true
      else this.showOldRolePermission = true
    },
    onRoleSaved() {
      this.loadRoles()
      this.$store.dispatch('loadRoles', true)
    },
    deleteRole(role) {
      let formData = {
        roleId: role.roleId,
      }
      this.$dialog
        .confirm({
          title: this.$t('setup.users_management.delete_role_confirm'),
          message:
            role.name +
            ' ' +
            this.$t('setup.users_management.permanently_delete'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.post('/setup/deleterole', formData).then(({ error }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                let index = this.rolelist.findIndex(
                  r => r.roleId === role.roleId
                )

                if (!isEmpty(index)) {
                  this.rolelist.splice(index, 1)
                }
                this.$store.dispatch('loadRoles', true)
                this.$message.success(
                  this.$t('setup.users_management.deleted_success')
                )
              }
            })
          }
        })
    },
  },
}
</script>
