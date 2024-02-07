<template>
  <div class="setup-user">
    <div class="flex-center-row-space mB30">
      <div>
        <div class="user-heading">{{ title }}</div>
        <div class="user-descrip">{{ description }}</div>
      </div>

      <div class="d-flex">
        <el-select
          v-if="canShowApps"
          v-model="appId"
          placeholder="Select App"
          filterable
          @change="loadusers"
          class="fc-input-full-border2"
        >
          <el-option
            v-for="app in applications"
            :key="app.linkName"
            :label="app.name"
            :value="app.id"
          >
          </el-option>
        </el-select>

        <div class="action-btn setting-page-btn mL30">
          <el-dropdown v-if="canShowApps" @command="addNewForm">
            <el-button type="primary" class="setup-el-btn">
              {{ $t('common.products.add_user')
              }}<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="new">{{
                $t('common.products.new_user')
              }}</el-dropdown-item>
              <el-dropdown-item command="existing">
                {{ $t('common.header.existing_user') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-button
            v-else
            type="primary"
            class="setup-el-btn"
            @click="addUser"
          >
            {{ $t('setup.users_management.add_user') }}
          </el-button>
        </div>
      </div>
    </div>

    <spinner v-if="loading" :show="loading" size="80"></spinner>

    <el-tabs v-else v-model="activeTab">
      <el-tab-pane :label="$t('common.products.users_')" name="user">
        <el-table :data="users" style="width: 100%" height="100%">
          <el-table-column :label="$t('common.roles.name')" width="200">
            <template v-slot="userData">
              <user-avatar
                size="md"
                :user="userData.row"
                class="width200px"
              ></user-avatar>
            </template>
          </el-table-column>
          <el-table-column
            prop="email"
            :label="$t('common.header._email')"
          ></el-table-column>
          <el-table-column
            prop="role.name"
            :label="$t('common.wo_report.role')"
            width="200"
          ></el-table-column>
          <el-table-column
            :label="$t('maintenance._workorder.status')"
            width="150"
          >
            <template v-slot="userData">
              <el-switch
                v-model="userData.row.userStatus"
                :disabled="userData.row.role.name === 'Super Administrator'"
                data-arrow="true"
                :title="$t('setup.users_management.user_status')"
                v-tippy
                @change="changeStatus(userData.row)"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column width="150">
            <template v-slot="userData">
              <div
                v-if="userData.row.role.name !== 'Super Administrator'"
                class="visibility-visible-actions justify-content-even d-flex"
              >
                <i
                  class="fa fa-unlock reset-icon visibility-hide-actions"
                  data-arrow="true"
                  :title="$t('setup.users_management.send_reset_pw')"
                  v-tippy
                  @click="reset(userData.row)"
                ></i>
                <i
                  class="el-icon-edit edit-icon visibility-hide-actions"
                  data-arrow="true"
                  :title="$t('setup.users_management.edit_user')"
                  v-tippy
                  @click="editUser(userData.row)"
                ></i>
                &nbsp;&nbsp;
                <i
                  class="el-icon-delete visibility-hide-actions delete-icon"
                  data-arrow="true"
                  :title="$t('setup.users_management.delete_user')"
                  v-tippy
                  @click="deleteUser(userData.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane :label="$t('common.header.pending_invite')" name="pending">
        <el-table :data="pendingUsers" style="width: 100%" height="100%">
          <el-table-column :label="$t('common.roles.name')" width="200">
            <template v-slot="userData">
              <user-avatar
                size="md"
                :user="userData.row"
                class="width200px"
              ></user-avatar>
            </template>
          </el-table-column>
          <el-table-column
            prop="email"
            :label="$t('common.header.email')"
          ></el-table-column>
          <el-table-column
            prop="role.name"
            :label="$t('common.roles.name')"
            width="200"
          ></el-table-column>
          <el-table-column width="150">
            <template v-slot="userData">
              <el-button
                @click="reinvite(userData.row)"
                type="text"
                :send="sending"
              >
                {{
                  sending === userData.row.id
                    ? $t('setup.users_management.sending')
                    : $t('setup.users_management.reinvite')
                }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column width="150">
            <template v-slot="userData">
              <div class="visibility-visible-actions">
                &nbsp;&nbsp;
                <i
                  class="el-icon-delete visibility-hide-actions delete-icon"
                  data-arrow="true"
                  :title="$t('common.header.delete_user')"
                  v-tippy
                  @click="deleteUser(userData.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <edit-user
      v-if="showDialog"
      :user="editedUser"
      :isNew="isNewUser"
      :appId="appId"
      @close="closeEdit"
    ></edit-user>

    <AddExistingUser
      v-if="showExistingDialog"
      :app="selectedApp"
      @saved="loadusers"
      @close="showExistingDialog = false"
    ></AddExistingUser>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import EditUser from 'pages/setup/users/EditUser'
import AddExistingUser from 'pages/setup/portal/ExistingUserForm'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'

export default {
  name: 'UserList',
  components: { UserAvatar, EditUser, AddExistingUser },
  data() {
    return {
      showDialog: false,
      isNewUser: true,
      loading: true,
      userlist: [],
      editedUser: null,
      applications: [],
      appId: null,
      showExistingDialog: false,
      sending: -1,
      activeTab: 'user',
    }
  },

  title() {
    return this.title
  },

  created() {
    this.$store.dispatch('loadShifts')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')

    this.loadApps().then(() => this.loadusers())
  },
  computed: {
    canShowApps() {
      let { applications } = this
      return applications.length > 1
    },
    title() {
      return this.activeTab === 'user'
        ? this.$t('setup.users_management.users')
        : this.$t('setup.users_management.pending_user')
    },
    description() {
      return this.activeTab === 'user'
        ? this.$t('setup.users_management.list_all_user')
        : this.$t('setup.users_management.list_of_pending_users')
    },
    users() {
      return this.userlist.filter(user => user.inviteAcceptStatus)
    },
    pendingUsers() {
      return this.userlist.filter(user => !user.inviteAcceptStatus)
    },
    selectedApp() {
      return this.applications.find(app => app.id === this.appId)
    },
  },

  watch: {
    activeTab() {
      this.loading = true
      this.$nextTick(() => (this.loading = false))
    },
  },

  methods: {
    loadApps() {
      return API.get('/v2/application/list').then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.applications = data.application.filter(
            app => app.appCategory === 3
          )

          const { linkName: currentApp } = getApp()

          let selectedApp =
            this.applications.find(app => app.linkName === currentApp) ||
            this.applications.find(app => app.isDefault) ||
            this.applications[0]

          this.appId = (selectedApp || {}).id
        }
      })
    },
    loadusers() {
      let { appId } = this
      this.loading = true

      API.get('/v2/application/users/list', { appId }).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.userlist = data.users || []
          }
          this.loading = false
        }
      )
    },
    addUser() {
      this.isNewUser = true
      this.editedUser = {}
      this.showDialog = true
    },
    addNewForm(cmd) {
      if (cmd === 'new') {
        this.editedUser = {}
        this.isNewUser = true
        this.showDialog = true
      } else if (cmd === 'existing') {
        this.showExistingDialog = true
      }
    },
    closeEdit() {
      this.showDialog = false
      this.loadusers()
    },
    deleteUser(user) {
      const formData = new FormData()
      formData.append('user.uid', user.uid)
      formData.append('user.ouid', user.ouid)
      this.$dialog
        .confirm({
          title: this.$t('setup.users_management.delete_user_msg'),
          message: `${this.$t(
            'setup.users_management.delete_user_msg2'
          )} User ${user.name} ?`,
          rbDanger: true,
          rbLabel: this.$t('setup.users_management.delete'),
        })
        .then(value => {
          if (value) {
            API.post('/setup/deleteUser', formData).then(({ error }) => {
              if (error) {
                this.$message.error(
                  this.$t('setup.users_management.unable_to_delete')
                )
              } else {
                this.$message.success(
                  this.$t('setup.users_management.user_delete_success')
                )
                this.userlist.splice(this.userlist.indexOf(user), 1)
                this.$store.dispatch('loadUsers', true)
              }
            })
          }
        })
    },
    editUser(user) {
      this.isNewUser = false
      this.editedUser = user
      this.showDialog = true
    },
    changeStatus(userinfo) {
      const formData = new FormData()
      formData.append('user.uid', userinfo.uid)
      formData.append('user.ouid', userinfo.ouid)
      formData.append('user.userStatus', userinfo.userStatus)
      API.post('/setup/changestatus', formData).then(({ error }) => {
        if (error) {
          this.$message.error(this.$t('setup.users_management.error_occured'))
        } else {
          if (userinfo.userStatus) {
            this.$message.success(
              this.$t('setup.users_management.user_activated')
            )
          } else {
            this.$message.success(
              this.$t('setup.users_management.user_deactivated')
            )
          }
          this.$store.dispatch('loadUsers', true)
        }
      })
    },
    reset(user) {
      this.resetting = true
      API.post('/fresetPassword', { emailaddress: user.email }).then(
        ({ data, error }) => {
          if (error) {
            this.loading = false
            this.$dialog.notify('Password reset was not sent !')
          } else {
            this.resetDone = true
            if (data.invitation.status === 'success') {
              this.$dialog.notify('Password reset link sent successfully!')
              this.$store.dispatch('loadUsers', true)
            } else {
              this.$dialog.notify('Password reset was not sent !')
            }
          }
        }
      )
    },
    reinvite(user) {
      this.sending = user.id
      API.post('setup/resendinvite', { userId: user.id, isPortal: false }).then(
        ({ error }) => {
          if (error) {
            this.sending = -1
            this.$message.error(this.$t('setup.users_management.error_occured'))
          } else {
            this.$message.success(
              this.$t('setup.users_management.invitation_send_success')
            )
            this.sending = -1
            this.$store.dispatch('loadUsers', true)
          }
        }
      )
    },
  },
}
</script>
<style lang="scss">
.setup-user {
  padding: 20px 30px;
  height: calc(100vh - 50px);
  overflow: hidden;

  .user-heading {
    font-size: 18px;
    color: #000;
    letter-spacing: 0.7px;
    font-weight: 400;
    padding-bottom: 5px;
  }
  .user-descrip {
    font-size: 13px;
    color: grey;
    letter-spacing: 0.3px;
  }
  .el-tabs__content {
    overflow: scroll;
    height: calc(100vh - 220px);
  }
  .reset-icon {
    font-size: 15px;
    color: #a1a2a2;
    cursor: pointer;
  }
  .edit-icon {
    color: #319aa8;
    font-size: 16px;
    cursor: pointer;
  }
  .delete-icon {
    color: #de7272;
    font-size: 16px;
    cursor: pointer;
  }
}
</style>
