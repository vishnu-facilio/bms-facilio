<template>
  <div class="height100vh">
    <div class="fc-setup-header" style="height: 120px;">
      <div
        class="setting-title-block flex-middle justify-content-space"
        style="height: 42px;"
      >
        <div class="flex-middle">
          <div
            class="fc-black3-13 f16 pointer fwBold vertical-text-super"
            @click="back()"
            :title="$t('common.header.back')"
            v-tippy="{
              arrow: true,
              arrowType: 'round',
              animation: 'shift-away',
            }"
          >
            <i class="el-icon-back f16 fwBold pR5"></i>
          </div>
          <el-divider direction="vertical" class="mR10 mL10"></el-divider>
          <div class="setting-form-title">{{ app.name }}</div>
        </div>
        <portal-target
          name="header-buttons"
          class="portal-summary flex-middle"
        ></portal-target>
      </div>
      <el-tabs v-model="activeName" class="fc-setup-tab">
        <el-tab-pane :label="$t('common.tabs.summary')" name="Summary">
          <div class="occupantPortal-tab">
            <PortalSettings />
          </div>
        </el-tab-pane>
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
          <OccupantUsers
            ref="userList"
            :app="app"
            :editOccupant="editOccupant"
          />
        </el-tab-pane>
        <el-tab-pane :label="$t('common.header.single_sign_on_sso')" name="SSO">
          <div class="occupantPortal-tab">
            <sso></sso>
          </div>
        </el-tab-pane>
        <el-tab-pane
          :label="$t('common.products.preferences')"
          name="preference"
        >
          <portal to="header-buttons" v-if="activeName === 'preference'" slim>
            <portal-target name="app-preference-update" slim></portal-target>
            <button @click="showAppEdit = true" class="add-app-btn">
              {{ $t('common.products.edit_app') }}
            </button>
          </portal>
          <AppPreferences :app="app" @onSave="updateConfig"></AppPreferences>
        </el-tab-pane>
        <el-tab-pane :label="$t('common._common.customization')" name="custom">
          <TabsAndLayouts
            :appId="appId"
            :isActive="activeName === 'custom'"
            class="portal-summary"
          ></TabsAndLayouts>
        </el-tab-pane>
      </el-tabs>
    </div>

    <OccupantUserForm
      v-if="showDialog"
      :isNew="isNew"
      formType="occupant"
      :user="currentUser"
      :app="app"
      :save="saveOccupant"
      @onClose="showDialog = false"
    ></OccupantUserForm>

    <NewApp
      v-if="showAppEdit"
      :selectedApp="app"
      @onSave="updateApp"
      @onClose="showAppEdit = false"
    ></NewApp>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import pick from 'lodash/pick'
import PortalSettings from 'pages/setup/portal/OccupantPortalSettings'
import OccupantUsers from 'pages/setup/portal/OccupantPortalUsers'
import OccupantUserForm from 'pages/setup/portal/PortalUserForm'
import sso from 'pages/setup/DomainSSO'
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import AppPreferences from 'pages/setup/apps/AppPreferences'
import NewApp from 'pages/setup/apps/NewApp'

export default {
  props: ['application'],
  components: {
    PortalSettings,
    OccupantUsers,
    OccupantUserForm,
    sso,
    TabsAndLayouts,
    AppPreferences,
    NewApp,
  },
  title() {
    let { activeName } = this
    return activeName === 'Users' ? 'Requesters' : 'Occupant Portal'
  },
  data() {
    return {
      activeName: 'Summary',
      showDialog: false,
      isNew: false,
      loading: false,
      userlist: [],
      currentUser: null,
      showAppEdit: false,
      app: null,
    }
  },
  created() {
    this.app = { ...this.application }
  },
  computed: {
    appId() {
      return this.app.id
    },
  },
  methods: {
    back() {
      this.$router.go(-1)
    },
    addUser() {
      this.currentUser = null
      this.isNew = true
      this.showDialog = true
    },
    editOccupant(user) {
      this.isNew = false
      this.showDialog = true
      this.currentUser = user
    },
    saveOccupant(occupant) {
      let { isNew } = this

      let url = ''
      let successMsg = ''
      if (isNew) {
        url = '/v2/people/add'
        successMsg = this.$t('common.products.new_occupant_added')
      } else {
        url = '/v2/people/update'
        successMsg = this.$t('common.products.occupant_updated')
      }

      let props = ['name', 'email', 'phone', 'rolesMap', 'language']
      if (!isNew) props.push('id')

      let params = {
        peopleList: [
          {
            ...pick(occupant, props),
            peopleType: 5,
            isOccupantPortalAccess: true,
          },
        ],
      }

      return API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Ocurred')
          throw new Error()
        } else {
          this.$message(successMsg)
          this.reloadOccupants()
        }
      })
    },
    reloadOccupants() {
      if (this.$refs['userList']) {
        this.$refs['userList'].loadUsersList()
      }
    },
    updateConfig(config) {
      this.app = {
        ...this.app,
        configJSON: config,
        config: JSON.stringify(config),
      }
    },
    updateApp() {
      this.$emit('reload')
    },
  },
}
</script>
