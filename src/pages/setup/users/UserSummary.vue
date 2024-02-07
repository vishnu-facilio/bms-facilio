<template>
  <div class="fc-setup-summary">
    <div v-if="loading" class="p20 fc-setup-page-loader-height">
      <SetupLoader>
        <template #setupLoading>
          <spinner :show="loading" size="80"></spinner>
        </template>
      </SetupLoader>
    </div>
    <!-- Summary header with tabs -->
    <div v-else>
      <el-header class="fc-setup-summary-header" height="150">
        <div class="flex-middle fc-setup-breadcrumb">
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupHomeRoute"
          >
            {{ $t('common.products.home') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div
            class="fc-setup-breadcrumb-inner pointer"
            @click="setupUsersRoute"
          >
            {{ $t('setup.users_management.users') }}
          </div>
          <div class="fc-setup-breadcrumb-inner pL10 pR10">
            <i class="el-icon-arrow-right f14 fwBold"></i>
          </div>
          <div class="fc-breadcrumbBold-active">#{{ user.id }}</div>
        </div>
        <div class="display-flex-between-space pT15">
          <div class="flex-middle">
            <div class="fc-avatar-summary mR10">
              <avatar size="lg" :user="{ name: user.name }"></avatar>
            </div>
            <div>
              <div class="d-flex">
                <div>
                  <div class="flex-middle">
                    <div class="fc-grey-txt18">
                      {{ user.name }}
                    </div>
                    <el-button
                      class="fc-active-btn mL15"
                      v-if="user.userStatus === true"
                      >Active</el-button
                    >
                    <el-button class="fc-deactive-btn mL20" v-else
                      >Deactivated</el-button
                    >
                  </div>
                  <div class="pT5 fc-black2-14">
                    {{ user.role ? user.role && user.role.name : '---' }}
                  </div>
                </div>
              </div>
              <!-- <div class="fc-grey-txt12">
              Minnesota
            </div> -->
              <!-- <div class="pT10 flex-middle">
              <div class="fc-black-12 text-left">
                <span class="fc-blue-txt12 pR10">Last active</span>
                2:30 PM
              </div>
              <div class="fc-black-12 text-left pL20">
                <span class="fc-blue-txt12 pR10">Current</span>
                2:30 PM
              </div>
            </div> -->
            </div>
          </div>
          <div
            class="flex-middle"
            v-if="!(user.role && user.role.name === 'Super Administrator')"
          >
            <el-button
              class="mR20 fc-activate-btn"
              v-if="user.userStatus === false"
              @click="changeStatus(user)"
              >{{ $t('common._common.activate') }}</el-button
            >
            <el-button
              class="mR20 fc-deactivate-btn"
              v-else
              @click="changeStatus(user)"
              >{{ $t('common._common.deactivate') }}</el-button
            >
            <div>
              <el-dropdown
                @command="onOptionsSelect($event, user)"
                class="pointer"
              >
                <span class="el-dropdown-link">
                  <i class="el-icon-more fc-black2-18 text-left rotate-90 "></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="edit">{{
                    $t('setup.users_management.edit_user')
                  }}</el-dropdown-item>
                  <el-dropdown-item command="delete">{{
                    $t('setup.users_management.delete_user')
                  }}</el-dropdown-item>
                  <el-dropdown-item command="resend">
                    {{ $t('setup.users_management.send_reset_pw') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
      </el-header>
      <!-- Tabs -->
      <el-tabs v-model="summaryActiveTabs" @tab-click="tabSwitch">
        <el-tab-pane label="User Information" name="first">
          <div class="fc-user-summary-scroll">
            <div class="fc-grey3-text14 bold">
              {{ $t('setup.users_management.basic_information') }}
            </div>
            <el-card
              shadow="never"
              class="fc-setup-summary-card heightInitial mB20 mT15"
            >
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.approvalprocess.name') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.name }}
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.setup.username') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.userName ? user.userName : '---' }}
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.from_address.email') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.email ? user.email : '---' }}
                  </div>
                </el-col>
              </el-row>
              <el-row class="pT15" :gutter="20">
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.users_management.role') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.role ? user.role && user.role.name : '---' }}
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.setup_profile.mobile') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.mobile ? user.mobile : '---' }}
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.setup_profile.phone') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.phone ? user.phone : '---' }}
                  </div>
                </el-col>
              </el-row>
              <el-row class="pT15" :gutter="20">
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.setup_profile.created_at') }}
                  </div>
                  <div
                    class="fc-black-14 text-left bold"
                    v-if="
                      !$validation.isEmpty(
                        $getProperty(user, 'role.createdTime', -1)
                      )
                    "
                  >
                    {{ user.role && user.role.createdTime | formatDate(true) }}
                  </div>
                  <div class="fc-black-14 text-left bold" v-else>
                    ---
                  </div>
                </el-col>
              </el-row>
            </el-card>

            <div class="fc-grey3-text14 bold">
              {{ $t('setup.users_management.address_information') }}
            </div>

            <el-card
              shadow="never"
              class="fc-setup-summary-card heightInitial mB20 mT15"
            >
              <div
                class="fc-black-14 text-left bold"
                v-if="
                  user.street ||
                    user.city ||
                    user.zip ||
                    user.state ||
                    user.country
                "
              >
                <span>{{ user.street ? user.street : '' }} ,</span>
                <span v-if="user.city">
                  {{ user.city ? user.city : '' }} ,
                </span>
                <span v-if="user.state">
                  {{ user.state ? user.state : '' }} ,
                </span>
                <span v-if="user.zip"> {{ user.zip ? user.zip : '' }}, </span>
                <span v-if="user.country">
                  {{ user.country ? user.country : '' }}
                </span>
              </div>
              <div v-else class="label-txt-black bold">
                {{ $t('setup.users_management.no_info_available') }}
              </div>
            </el-card>
            <div class="fc-grey3-text14 bold">
              {{ $t('setup.users_management.locale_information') }}
            </div>

            <el-card
              shadow="never"
              class="fc-setup-summary-card heightInitial mT15"
            >
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="fc-grey2-text12 bold line-height20">
                    {{ $t('setup.setup_profile.timezone') }}
                  </div>
                  <div class="fc-black-14 text-left bold">
                    {{ user.timezone ? user.timezone : '---' }}
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Accessible spaces" name="second">
          <el-card shadow="never" class="fc-setup-summary-card-p0">
            <accessible-spaces></accessible-spaces>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="Delegates" name="third">
          <deleagte></deleagte>
        </el-tab-pane>
        <el-tab-pane label="Apps" name="fourth">
          <apps-list></apps-list>
        </el-tab-pane>
      </el-tabs>
    </div>
    <edit-user
      v-if="showDialog"
      :user="editedUser"
      :isNew="isNewUser"
      :appId="applicationId"
      :applications="selectedApp"
      @close="closeEdit"
    ></edit-user>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import Avatar from '@/Avatar'
import Deleagte from 'pages/setup/users/delegate/DelegateList'
import AppsList from 'pages/setup/users/AppsList'
import EditUser from 'pages/setup/users/EditUser'
import SetupLoader from 'pages/setup/components/SetupLoader'
import AccessibleSpaces from 'pages/setup/users/AccessibleSpaces/AccessibleSpacesList'
import { loadApps } from 'util/appUtil'

export default {
  data() {
    return {
      summaryActiveTabs: 'first',
      loading: false,
      user: [],
      showDialog: false,
      editedUser: null,
      isNewUser: true,
      applications: [],
    }
  },
  components: {
    Avatar,
    Deleagte,
    EditUser,
    SetupLoader,
    AccessibleSpaces,
    AppsList,
  },
  created() {
    this.loadUserSummary()
  },
  computed: {
    summaryId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    applicationId() {
      if (this.$route.params.appId) {
        return parseInt(this.$route.params.appId)
      }
      return -1
    },
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
    selectedApp() {
      return this.applications.find(app => app.id === this.appId)
    },
  },
  methods: {
    tabSwitch() {
      console.log('Tab switch')
    },
    async loadUserSummary() {
      this.loading = true
      let params = {
        appId: this.applicationId,
        ouId: this.summaryId,
      }
      let { error, data } = await API.post(
        'v2/application/users/summary',
        params
      )
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.user = data.user || []
      }
      this.loading = false
    },
    editUser(user) {
      this.isNewUser = false
      this.editedUser = user
      this.showDialog = true
    },
    closeEdit() {
      this.showDialog = false
      this.loadUserSummary()
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
                this.$store.dispatch('loadUserSummary', true)
                this.loadUserSummary()
              }
            })
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
              this.$store.dispatch('loadUserSummary', true)
            } else {
              this.$dialog.notify('Password reset was not sent !')
            }
          }
        }
      )
    },
    onOptionsSelect(command, user) {
      if (command === 'edit') {
        this.editUser(user)
      } else if (command === 'delete') {
        this.deleteUser(user)
      } else if (command === 'resend') {
        this.reset(user)
      }
    },
    changeStatus(userinfo) {
      const formData = new FormData()
      formData.append('user.uid', userinfo.uid)
      formData.append('user.ouid', userinfo.ouid)
      formData.append('user.userStatus', !userinfo.userStatus)
      API.post('/setup/changestatus', formData).then(({ error }) => {
        if (error) {
          this.$message.error(this.$t('setup.users_management.error_occured'))
        } else {
          if (!userinfo.userStatus) {
            this.$message.success(
              this.$t('setup.users_management.user_activated')
            )
          } else {
            this.$message.success(
              this.$t('setup.users_management.user_deactivated')
            )
          }
          this.$store.dispatch('loadUsers', true)
          this.loadUserSummary()
        }
      })
    },
    setupHomeRoute() {
      this.$router.replace({ name: 'setup' })
    },
    setupUsersRoute() {
      this.$router.replace({ name: 'users' })
    },
    loadAppList() {
      this.appLoading = true

      return loadApps()
        .then(({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.applications = data.filter(app => app.appCategory === 3)
            const { linkName: currentApp } = getApp()
            let selectedApp =
              this.applications.find(app => app.linkName === currentApp) ||
              this.applications.find(app => app.isDefault) ||
              this.applications[0]

            this.appId = (selectedApp || {}).id
          }
        })
        .finally(() => (this.appLoading = false))
    },
  },
}
</script>
<style lang="scss">
.fc-breadcrumbBold {
  .el-breadcrumb__inner {
    font-weight: bold;
  }
}
</style>
