<template>
  <div class="height100vh">
    <div class="fc-setup-header" style="height: 120px;">
      <div class="flex-middle justify-content-space" style="height: 42px;">
        <div class="setting-title-block ">
          <div class="setting-form-title flex-middle">
            <div
              class="fc-black3-13 f16 pointer fwBold vertical-text-super"
              @click="back()"
              :title="$t('common.header.back')"
              v-tippy="{
                arrow: true,
                arrowType: 'round',
                animation: 'shift-away',
                placement: 'top',
              }"
            >
              <i class="el-icon-back f16 fwBold pR5"></i>
            </div>
            <el-divider direction="vertical" class="mR10 mL10"></el-divider>
            {{
              $t('common._common.appname_summary', { name: application.name })
            }}
          </div>
        </div>
        <portal-target
          name="header-buttons"
          class="portal-summary"
        ></portal-target>
      </div>
      <el-tabs
        v-model="activeName"
        class="fc-setup-tab fc-setup-tab-portal pT13"
      >
        <el-tab-pane :label="$t('common.products.users_')" name="Users">
          <div class="occupantPortal-tab">
            <div class="container-scroll">
              <div class="row setting-Rlayout pL0 pR0 mT20">
                <div class="col-lg-12 col-md-12 mT60">
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
                    <tbody v-if="loading">
                      <tr>
                        <td colspan="100%" class="text-center">
                          <spinner :show="loading" size="80"></spinner>
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else-if="userlist.length === 0">
                      <tr>
                        <td colspan="100%" class="text-center">
                          {{ $t('common._common.no_users_available') }}
                        </td>
                      </tr>
                    </tbody>
                    <tbody v-else>
                      <tr
                        class="tablerow visibility-visible-actions"
                        v-for="(user, index) in userlist"
                        :key="index"
                      >
                        <td>
                          <user-avatar
                            size="md"
                            :user="user"
                            class="width200px"
                          ></user-avatar>
                        </td>
                        <td>
                          {{ user.email ? user.email : '---' }}
                        </td>
                        <td>
                          {{ user.phone ? user.phone : '---' }}
                        </td>
                        <td>
                          {{ getRoleName(user) }}
                        </td>
                        <td class="width200px pL0 pR0 nowrap">
                          <i
                            class="visibility-hide-actions el-icon-edit fc-setup-list-edit"
                            @click="editUser(user)"
                          ></i>
                          <i
                            class="visibility-hide-actions el-icon-delete fc-setup-list-delete"
                            @click="deleteUser(user)"
                          ></i>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
            <portal v-if="activeName === 'Users'" to="header-buttons">
              <div class="position-relative">
                <el-dropdown @command="addNewForm">
                  <el-button type="primary" class="setup-el-btn">
                    {{ $t('common.products.add_user') }}
                    <i class="el-icon-arrow-down el-icon--right"></i>
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
                <div class="fc-setup-portal-search">
                  <f-search v-if="!loading" v-model="userlist"></f-search>
                </div>
              </div>
            </portal>
          </div>
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

    <UserForm
      v-if="showDialog"
      :isNew="isNew"
      formType="workCenter"
      :user="currentUser"
      :app="application"
      :save="saveUser"
      @onClose="showDialog = false"
    ></UserForm>

    <AddExistingUser
      v-if="showExistingDialog"
      :app="application"
      @close="showExistingDialog = false"
      @saved="loadApplicationUser"
    ></AddExistingUser>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import AddExistingUser from 'pages/setup/portal/ExistingUserForm'
import UserAvatar from '@/avatar/User'
import FSearch from '@/FSearch'
import UserForm from './PortalUserForm'
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import pick from 'lodash/pick'
import { mapGetters } from 'vuex'

export default {
  props: ['application'],
  components: {
    UserAvatar,
    FSearch,
    UserForm,
    AddExistingUser,
    TabsAndLayouts,
  },
  title() {
    return 'Application User List'
  },
  data() {
    return {
      activeName: 'Users',
      showDialog: false,
      loading: true,
      showExistingDialog: false,
      userlist: [],
      isNew: false,
      currentUser: null,
    }
  },
  async created() {
    await this.$store.dispatch('loadRoles')
    this.loadApplicationUser()
  },
  computed: {
    ...mapGetters(['getRoleNameById']),
    appId() {
      return this.application.id
    },
  },
  methods: {
    back() {
      this.$router.go(-1)
    },
    addNewForm(cmd) {
      if (cmd === 'new') {
        this.currentUser = null
        this.isNew = true
        this.showDialog = true
      } else if (cmd === 'existing') {
        this.showExistingDialog = true
      }
    },
    loadApplicationUser() {
      let { appId } = this

      this.loading = true
      return API.get('/v2/application/users/list', { appId }).then(
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
    editUser(user) {
      this.isNew = false
      this.showDialog = true
      this.currentUser = user
    },
    deleteUser(user) {
      let { appId } = this
      let { name, ouid } = user

      let params = {
        appId,
        user: { ouid },
      }
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_user'),
          htmlMessage: this.$t('common._common.do_you_want_delete_user_name', {
            name,
          }),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.post('/v2/application/users/delete', params).then(
              ({ error }) => {
                if (error) {
                  this.$message.error(error.message || 'Error Occured')
                } else {
                  this.loadApplicationUser()
                  this.$store.dispatch('loadUsers', true)
                }
              }
            )
          }
        })
    },
    saveUser(user) {
      let { isNew, appId } = this

      let url = ''
      let successMsg = ''
      if (isNew) {
        url = '/v2/application/users/add'
        successMsg = this.$t('common.products.new_user_added')
      } else {
        url = '/setup/updateuser'
        successMsg = this.$t('common.products.user_updated')
      }

      let props = [
        'name',
        'email',
        'phone',
        'roleId',
        'applicationId',
        'language',
      ]
      if (!isNew) props.push('id')

      let params = { appId, user: pick(user, props) }

      return API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Ocurred')
          throw new Error()
        } else {
          this.$message(successMsg)
          this.loadApplicationUser()
          this.$store.dispatch('loadUsers', true)
        }
      })
    },
    getRoleName(user) {
      let { roleId } = user || {}
      return roleId ? this.getRoleNameById(roleId) : '---'
    },
  },
}
</script>
