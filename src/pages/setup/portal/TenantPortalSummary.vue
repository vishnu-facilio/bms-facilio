<template>
  <div class="height100vh">
    <div class="fc-setup-header" style="height: 120px">
      <div class="flex-middle justify-content-space">
        <div class="setting-title-block">
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
            {{ app.name }}
          </div>
        </div>
        <portal-target
          name="header-buttons"
          class="portal-summary flex-middle"
        ></portal-target>
      </div>
      <el-tabs
        v-model="activeName"
        class="fc-setup-tab fc-setup-tab-portal pT13"
      >
        <el-tab-pane :label="$t('common.products.users_')" name="Users">
          <pagination
            ref="pagination"
            :total="userlistCount"
            :perPage="perPage"
            :currentPage.sync="page"
            class="flex-middle justify-content-end mT20"
          >
          </pagination>
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
                          {{ $t('common.header.tenant') }}
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
                          {{
                            $t(
                              'common.products.no_tenant_portal_users_available'
                            )
                          }}
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
                          {{ user.email }}
                        </td>
                        <td class="width200px">
                          {{ user.tenant.name }}
                        </td>
                        <td>
                          {{ getRoleName(user) }}
                        </td>
                        <td class="pL0 pR0">
                          <i
                            class="visibility-hide-actions el-icon-edit"
                            @click="editTenant(user)"
                          ></i>
                          <i
                            class="visibility-hide-actions el-icon-delete pL10"
                            @click="showDelete(user)"
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
                    <el-dropdown-item command="existing">{{
                      $t('common.header.existing_user')
                    }}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <!-- <div class="fc-setup-portal-search">
                  <f-search v-if="!loading" v-model="userlist"></f-search>
                </div> -->
              </div>
            </portal>
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
            class="portal-summary"
            :isActive="activeName === 'custom'"
          ></TabsAndLayouts>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- container -->
    <f-dialog
      v-if="showTenantOptionDialog"
      :visible.sync="showTenantOptionDialog"
      :width="'40%'"
      maxHeight="350px"
      :title="$t('common._common.remove_access')"
      @save="portalAccessRemove(currentUser)"
      @close="showTenantOptionDialog = false"
      confirmTitle="Confirm"
      :stayOnSave="true"
    >
      <div class="height150">
        <div class="label-txt-black text-left pT10">
          {{
            $t(
              'common.header.are_you_sure_you_want_to_remove_tenant_portal_access',
              { name: currentUser.name }
            )
          }}
        </div>
        <div class="label-txt-black text-left pT20">
          <el-checkbox v-model="selectedDelete">
            {{
              $t('common.wo_report.also_remove_this_contact_of', {
                name: currentUser.tenant.name,
              })
            }}
          </el-checkbox>
        </div>
      </div>
    </f-dialog>

    <TenantUserForm
      v-if="showDialog"
      :isNew="isNew"
      formType="tenant"
      :user="currentUser"
      :app="app"
      :save="saveTenant"
      @onClose="showDialog = false"
    ></TenantUserForm>

    <PortalExistingUser
      v-if="showExistingDialog"
      :app="app"
      :fetchParams="fetchParams"
      parentModule="tenant"
      :save="saveTenant"
      @onSave="loadTenantUser"
      @onClose="showExistingDialog = false"
      :fetchParamsCount="fetchParamsCount"
    ></PortalExistingUser>

    <NewApp
      v-if="showAppEdit"
      :selectedApp="app"
      @onSave="updateApp"
      @onClose="showAppEdit = false"
    ></NewApp>
  </div>
</template>

<script>
import PortalExistingUser from 'src/pages/setup/portal/PortalExistingUser'
// import FSearch from '@/FSearch'
import TenantUserForm from './PortalUserForm'
import UserAvatar from '@/avatar/User'
import FDialog from '@/FDialogNew'
import { API } from '@facilio/api'
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import AppPreferences from 'pages/setup/apps/AppPreferences'
import NewApp from 'pages/setup/apps/NewApp'
import pick from 'lodash/pick'
import { mapGetters } from 'vuex'
import Pagination from 'pageWidgets/utils/WidgetPagination'

export default {
  props: ['application'],
  data() {
    return {
      activeName: 'Users',
      showDialog: false,
      isNew: true,
      showExistingDialog: false,
      loading: true,
      userlist: [],
      showTenantOptionDialog: false,
      selectedDelete: null,
      currentUser: null,
      showAppEdit: false,
      app: null,
      fetchParams: {
        params: {
          filters: JSON.stringify({
            isTenantPortalAccess: {
              operatorId: 15,
              value: ['false'],
            },
          }),
        },
        url: 'v2/tenantcontact/list',
        dataKey: 'tenantcontacts',
      },
      fetchParamsCount: {
        params: {
          filters: JSON.stringify({
            isTenantPortalAccess: {
              operatorId: 15,
              value: ['false'],
            },
          }),
          fetchCount: true,
        },
        url: 'v2/tenantcontact/list',
        dataKey: 'tenantcontacts',
      },
      page: 1,
      perPage: 50,
      userlistCount: '',
    }
  },
  components: {
    PortalExistingUser,
    // FSearch,
    FDialog,
    TenantUserForm,
    UserAvatar,
    TabsAndLayouts,
    AppPreferences,
    NewApp,
    Pagination,
  },
  async created() {
    this.app = { ...this.application }
    await this.$store.dispatch('loadRoles')
    this.loadTenantUser()
    this.loadTenantUserCount()
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
        this.loadTenantUser()
      }
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
        this.isNew = false
        this.showExistingDialog = true
      }
    },
    loadTenantUser() {
      let filterParams = {
        isTenantPortalAccess: {
          operatorId: 15,
          value: ['true'],
        },
      }
      let params = {
        filters: JSON.stringify(filterParams),
      }

      this.loading = true
      API.get(
        `/v2/tenantcontact/list?page=${this.page}&perPage=${this.perPage}`,
        params
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlist = data.tenantcontacts || []
        }
        this.loading = false
      })
    },
    loadTenantUserCount() {
      this.loading = true
      let filterParams = {
        isTenantPortalAccess: {
          operatorId: 15,
          value: ['true'],
        },
      }
      let params = {
        filters: JSON.stringify(filterParams),
      }
      API.get('/v2/tenantcontact/list?fetchCount=true', params).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.userlistCount = data.recordCount || []
          }
        }
      )
      this.loading = false
    },
    editTenant(user) {
      this.isNew = false
      this.showDialog = true
      this.currentUser = user
    },
    showDelete(user) {
      this.currentUser = user
      this.showTenantOptionDialog = true
    },
    portalAccessRemove(user) {
      let url = ''
      let params = {}
      let successMsg = ''
      let { selectedDelete } = this
      let { id } = user

      if (!selectedDelete) {
        url = 'v2/tenantcontact/updateTenantPortalAccess'
        params = {
          tenantContacts: [{ id, isTenantPortalAccess: false }],
        }
        successMsg = 'Access Removed successfully'
      } else {
        url = '/v2/tenantcontact/delete'
        params = {
          tenantContactIds: [id],
        }
        successMsg = 'Deleted successfully'
      }

      API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(successMsg)
          this.loadTenantUser()
        }
        this.showTenantOptionDialog = false
      })
    },
    saveTenant(tenant) {
      let { isNew } = this
      let url = ''
      let successMsg = ''
      if (isNew) {
        url = '/v2/tenantcontact/add'
        successMsg = 'New Tenant Added.'
      } else {
        url = '/v2/tenantcontact/update'
        successMsg = 'Tenant Updated.'
      }

      let props = ['name', 'email', 'phone', 'language', 'tenant', 'rolesMap']
      if (!isNew) props.push('id')

      let params = {
        tenantContacts: [
          { ...pick(tenant, props), peopleType: 1, isTenantPortalAccess: true },
        ],
      }

      return API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Ocurred')
          throw new Error()
        } else {
          this.$message(successMsg)
          this.loadTenantUser()
        }
      })
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
    getRoleName(user) {
      let { linkName } = this.app
      let rolesMap = user.rolesMap || {}
      let roleId = rolesMap[linkName]

      return roleId ? this.getRoleNameById(roleId) : '---'
    },
  },
}
</script>
