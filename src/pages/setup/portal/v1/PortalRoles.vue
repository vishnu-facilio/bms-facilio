<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <portal v-if="isActiveRoles" to="header-buttons">
        <div class="action-btn setting-page-btn">
          <el-button type="primary" @click="addRole()" class="setup-el-btn">
            {{ $t('setup.users_management.new_role') }}
          </el-button>
          <template v-if="showWebTabRolePermission">
            <NewWebTabPermission
              v-if="$helpers.isLicenseEnabled('NEW_TAB_PERMISSIONS')"
              :role="role"
              :app="app"
              @onSave="onRoleSaved"
              @onClose="showWebTabRolePermission = false"
            ></NewWebTabPermission>
            <WebTabRolePermission
              v-else
              :role="role"
              :app="app"
              @onSave="onRoleSaved"
              @onClose="showWebTabRolePermission = false"
            ></WebTabRolePermission>
          </template>
          <RoleDetailsForm
            v-if="showRoleDetails"
            :appId="appId"
            :isNew="isNew"
            :role="role"
            :isWebTabRole="true"
            @onSave="onRoleSaved"
            @onClose="showRoleDetails = false"
          ></RoleDetailsForm>
        </div>
      </portal>
    </div>

    <setup-loader v-if="loading" class="m10 width98">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>

    <setup-empty
      v-else-if="$validation.isEmpty(sortedRoles) && !loading"
      class="m10 width98"
    >
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('setup.empty.empty_user') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>

    <div class="occupantPortal-tabrow pL0 pR0" v-else>
      <div class="col-lg-12 col-md-12">
        <table class="setting-list-view-table width100">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text uppercase">
                {{ $t('setup.approvalprocess.name') }}
              </th>
              <th class="setting-table-th setting-th-text uppercase">
                {{ $t('setup.approvalprocess.description') }}
              </th>
              <th class="setting-table-th setting-th-text uppercase">
                {{ $t('setup.users_management.created_time') }}
              </th>
              <th class="setting-table-th setting-th-text"></th>
            </tr>
          </thead>
          <tbody>
            <tr class="tablerow" v-for="role in sortedRoles" :key="role.id">
              <td class="fc-role-name">
                <span
                  class="role-name"
                  @click="!role.isPrevileged ? showEditRole(role) : () => {}"
                >
                  {{ role.name }}
                </span>
              </td>
              <td v-if="role.description">{{ role.description }}</td>
              <td v-else>---</td>
              <td>
                <div v-if="role.createdTime > 0">
                  {{ role.createdTime | fromNow }}
                </div>
                <div v-else>---</div>
              </td>
              <td>
                <div
                  v-if="!role.isPrevileged"
                  class="text-left actions nowrap"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i
                    class="el-icon-edit pointer fc-setup-list-edit"
                    @click="openRoleDetails(role)"
                  ></i>
                  <i
                    class="el-icon-delete pointer fc-setup-list-delete"
                    @click="deleteRole(role)"
                  ></i>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import RoleDetailsForm from 'pages/setup/roles/RoleDetailsForm'
import WebTabRolePermission from 'pages/setup/roles/WebTabRolePermission'
import NewWebTabPermission from 'pages/setup/roles/NewWebTabPermission'
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
import { isEmpty } from '@facilio/utils/validation'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'

export default {
  props: ['app', 'isActiveRoles'],
  name: 'RolesList',
  title() {
    return 'Roles'
  },
  components: {
    RoleDetailsForm,
    WebTabRolePermission,
    NewWebTabPermission,
    SetupLoader,
    SetupEmpty,
  },
  data() {
    return {
      loading: true,
      rolelist: [],
      isNew: false,
      role: {},
      showWebTabRolePermission: false,
      showRoleDetails: false,
    }
  },
  async created() {
    await this.loadRoles()
  },
  computed: {
    appId() {
      let { params } = this.$route
      let { id } = params || {}
      return this.app?.id || id
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
  },
  methods: {
    async loadRoles() {
      let { appId } = this

      this.loading = true
      let { error, data } = await API.get('/setup/roles', { appId })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolelist = data.roles || []
      }
      this.loading = false
    },
    addRole() {
      this.isNew = true
      this.role = {}
      this.showRoleDetails = true
    },
    showEditRole(role) {
      this.role = this.$helpers.cloneObject(role)
      this.isNew = false
      this.showWebTabRolePermission = true
    },
    openRoleDetails(role) {
      this.role = this.$helpers.cloneObject(role)
      this.isNew = false
      this.showRoleDetails = true
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
<style scoped lang="scss">
table.dataTable > tbody > tr > td {
  padding: 10px;
  vertical-align: middle;
  border-spacing: 0;
  border-collapse: collapse;
}

table.dataTable thead > tr > th {
  padding: 5px 10px 2px 10px;
  color: #6f7175;
  vertical-align: top;
  font-weight: 400;
  font-size: 13px;
  border-bottom: 0px;
}

table.dataTable tr.odd {
  background-color: '#fafafa';
}

table.dataTable tr.even {
  background-color: blue;
}

div.dataTables_info {
  padding-top: 8px;
  white-space: nowrap;
  padding-left: 10px;
}

div.dataTables_info,
div.dataTables_paginate {
  padding: 18px;
  white-space: nowrap;
}

div.row-title {
  font-weight: 400;
}

div.row-subtitle {
  font-weight: 400;
  color: #6f7175;
}

.dataTable tbody tr:hover {
  background: #fafafa;
  cursor: pointer;
}

.dataTable tr th .checkbox {
  padding-left: 17px !important;
}

.dataTable tbody tr:last-child td {
  border-bottom: 1px solid #e7e7e7 !important;
}

.dataTable > tbody > tr:first-child > td {
  border-top: 0px;
}

div.row.content-center {
  padding-top: 100px;
  padding-bottom: 144px;
}

table.dataTable.dtr-inline.collapsed > tbody > tr > td:first-child:before,
table.dataTable.dtr-inline.collapsed > tbody > tr > th:first-child:before {
  background-color: #50ca7c;
  font-size: 16px;
  line-height: 16px;
  display: none;
}
.no-screen-msg .row-title {
  font-size: 17px;
  color: #212121;
  padding: 10px 0;
}
.no-screen-msg .row-subtitle {
  font-size: 13px;
  padding: 1px 0px;
}

.dataTable tbody tr.selected {
  background: rgba(14, 153, 227, 0.1);
}
.record-list,
.record-summary {
  padding: 0;
  transition: all 0.3s;
}
.more-actions .dropdown-toggle {
  color: #d8d8d8;
  font-size: 18px;
}

.more-actions .dropdown-toggle:hover {
  color: #000000;
}

.more-actions .dropdown-menu {
  right: 0;
  left: initial;
}
.toggle-switch label {
  position: relative;
  display: block;
  height: 12px;
  width: 30px;
  background: #999;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.08s linear;
}

.toggle-switch label:after {
  position: absolute;
  left: 0;
  top: -2px;
  display: block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fafafa;
  box-shadow: rgba(0, 0, 0, 0.4) 0px 1px 3px 0px;
  content: '';
  transition: 0.08s linear;
}

.toggle-switch label:active:after {
  transform: scale(1.15, 0.85);
}

.toggle-switch .checkbox:checked ~ label {
  background: rgba(80, 202, 124, 0.5);
}

.toggle-switch .checkbox:checked ~ label:after {
  left: 14px;
  background: #50ca7c;
}

.toggle-switch .checkbox:disabled ~ label {
  background: #d5d5d5;
  cursor: not-allowed;
  pointer-events: none;
}

.toggle-switch .checkbox:disabled ~ label:after {
  background: #bcbdbc;
}

.tablerow .role-name {
  font-size: 14px;
  letter-spacing: 0.6px;
}
</style>
