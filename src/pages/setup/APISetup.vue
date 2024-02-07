<template>
  <div class="height100vh">
    <div class="fc-setup-header" style="height: 120px;">
      <div
        class="setting-title-block flex-middle justify-content-space"
        style="height: 42px;"
      >
        <div class="flex-middle">
          <div class="setting-form-title">API Setup</div>
        </div>
        <portal-target
          name="header-buttons"
          class="portal-summary flex-middle"
        ></portal-target>
      </div>
      <el-tabs v-model="activeName" class="fc-setup-tab">
        <el-tab-pane :label="$t('panel.dashboard.users')" name="Users">
          <portal v-if="activeName === 'Users'" to="header-buttons">
            <div class="position-relative">
              <div style="height: 42px;">
                <el-button
                  type="primary"
                  class="setup-el-btn"
                  @click="addUser()"
                >
                  {{ $t('common.products.new_user') }}
                </el-button>
              </div>
            </div>
          </portal>
          <dev-users
            v-if="!loading"
            :app="devApp"
            @onEditUser="editUser"
          ></dev-users>
        </el-tab-pane>
        <el-tab-pane :label="$t('common._common.apimodules')" name="apimodules">
          <portal v-if="activeName === 'apimodules'" to="header-buttons">
            <div class="position-relative">
              <div style="height: 42px;">
                <el-button
                  type="primary"
                  class="setup-el-btn"
                  @click="addModule()"
                >
                  {{ $t('common._common.add_module') }}
                </el-button>
              </div>
            </div>
          </portal>
          <api-modules
            v-if="activeName === 'apimodules'"
            ref="listView"
            :appId="devApp.id"
          >
          </api-modules>
        </el-tab-pane>
      </el-tabs>
    </div>
    <portal-user-form
      v-if="showDialog"
      :isNew="isNew"
      formType="dev"
      :user="currentUser"
      :app="devApp"
      :save="saveDev"
      @onClose="showDialog = false"
    ></portal-user-form>
    <new-api-module
      v-if="showNewApiModule"
      :appId="devApp.id"
      @onClose="onCloseAddMod"
    />
  </div>
</template>
<script>
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import { loadApps } from 'util/appUtil'
import PortalUserForm from './portal/PortalUserForm'
import DevUsers from './DevUsers'
import ApiModules from './APIModules'
import { API } from '@facilio/api'
import NewApiModule from './NewAPIModule'

export default {
  data() {
    return {
      activeName: 'Users',
      devApp: null,
      isNew: false,
      showDialog: false,
      currentUser: null,
      loading: false,
      showList: true,
      showNewApiModule: false,
    }
  },
  components: {
    PortalUserForm,
    DevUsers,
    TabsAndLayouts,
    ApiModules,
    NewApiModule,
  },
  async created() {
    this.loading = true
    let { error, data } = await loadApps()
    if (error) {
      this.$message.error(error.message || 'Error Occured')
    }
    this.devApp = data.filter(app => app.linkName === 'dev')[0]
    this.loading = false
  },
  methods: {
    addModule() {
      this.showNewApiModule = true
    },
    saveDev(user) {
      let url
      if (this.isNew) {
        url = '/setup/adduser'
      } else {
        url = '/setup/updateuser'
      }

      let params = {
        emailVerificationNeeded: false,
        appId: this.devApp.id,
        user,
      }
      this.loading = true
      API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let message = this.isNew
            ? this.$t('common._common.user_added_successfully')
            : this.$t('setup.users_management.user_details_success')

          this.$message.success(message)
          this.showDialog = false
        }
        this.loading = false
      })
    },
    editUser(user) {
      this.currentUser = user
      this.isNew = false
      this.showDialog = true
    },
    addUser() {
      this.currentUser = null
      this.isNew = true
      this.showDialog = true
    },
    onCloseAddMod() {
      this.showNewApiModule = false
      this.$refs.listView.loadSelectedModules()
    },
  },
}
</script>
