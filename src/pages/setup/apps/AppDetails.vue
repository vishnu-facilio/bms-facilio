<template>
  <spinner v-if="loading" :show="loading" size="80"></spinner>
  <div class="height100vh fc-apps-summary-con" v-else>
    <div class="app-details">
      <setupSummaryHeader>
        <template #summaryLeftSide>
          <div>
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
                @click="redirectToAppList()"
              >
                {{ app.name }}
              </div>
              <div class="fc-setup-breadcrumb-inner pL10 pR10">
                <i class="el-icon-arrow-right f14 fwBold"></i>
              </div>
              <div class="fc-breadcrumbBold-active">
                Summary
              </div>
            </div>
            <div class="setting-title-block flex-middle justify-content-space">
              <div class="flex-middle pT10">
                <div
                  class="fc-portal-icon fc-portal-icon-common-bg flex-center-vH align-center"
                >
                  <inline-svg
                    src="svgs/apps/agent"
                    iconClass="icon icon-xl fill-white"
                  ></inline-svg>
                </div>
                <div class="visibility-visible-actions">
                  <div class="fc-grey-txt18 pL10 pB5">
                    {{
                      $t('common._common.appname_summary', {
                        name: app.name,
                      })
                    }}
                    <el-dropdown @command="handleCommand($event)">
                      <span class="el-dropdown-link">
                        <i class="el-icon-more fc-portal-summary-more"></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="editApp">
                          {{ $t('setup.portal.edit_app') }}
                        </el-dropdown-item>
                        <el-dropdown-item command="delete">
                          {{ $t('common._common.delete') }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>

                  <div class="flex-middle">
                    <a
                      :href="`https://${app.appDomain.domain}/${app.linkName}`"
                      target="_blank"
                      class="fc-app-link-color pL10 f14"
                    >
                      {{ `https://${app.appDomain.domain}/${app.linkName}` }}
                    </a>
                    <div
                      @click="
                        copyLinkName(
                          `https://${app.appDomain.domain}/${app.linkName}`
                        )
                      "
                      class="pointer pL10 visibility-hide-actions"
                    >
                      <inline-svg
                        src="svgs/link-copy"
                        iconClass="icon icon-sm-md vertical-bottom op5"
                      ></inline-svg>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <template #summaryRightSide>
          <portal-target
            name="header-buttons"
            class="app-summary flex-middle"
          ></portal-target>
        </template>
      </setupSummaryHeader>

      <div class="fc-main d-flex">
        <!-- Tenant sidebar -->
        <setupSummarySidebar>
          <template #setupsidebar>
            <div v-for="(sidebar, list) in sidebarMenuList" :key="list">
              <div v-for="(headingName, inex) in sidebar.menuList" :key="inex">
                <div class="fc-pink f14 bold pT10 pB10">
                  {{ headingName.name }}
                </div>
                <div
                  v-for="(subMenu, iex) in headingName.submenuList"
                  :key="iex"
                >
                  <div
                    class="label-txt-black fc-list-label pointer"
                    :class="{ isActive: sidebarTabList == subMenu.link }"
                    @click="showHideMenu(subMenu.link)"
                  >
                    <InlineSvg
                      :src="subMenu.icon"
                      :iconClass="subMenu.iconClass"
                    ></InlineSvg>
                    {{ subMenu.name }}
                  </div>
                </div>
              </div>
            </div>
          </template>
        </setupSummarySidebar>
        <!-- tenant main section -->
        <setupSummaryMainSection>
          <template #setupMain>
            <template v-if="sidebarTabList === 'users'">
              <div
                class="fc-portal-inner-summary-header flex-middle justify-content-space"
              >
                <div class="fc-black2-18 text-left bold">
                  {{ $t('setup.users_management.users') }}
                </div>

                <div class="flex-middle">
                  <pagination
                    ref="pagination"
                    :total="userlistCount"
                    :perPage="perPage"
                    :currentPage.sync="page"
                    class="flex-middle justify-content-end"
                  >
                  </pagination>
                  <div
                    class="fc-portal-filter-border"
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
                        'dot-active-pink': activeUserSearchData,
                      }"
                    ></div>
                  </div>
                </div>
              </div>

              <div v-if="showFilter" class="fc-show-filter-con">
                <div class="flex-middle">
                  <div class="relative">
                    <div class="fc-black-13 text-left bold pB5">Search</div>
                    <el-input
                      placeholder="Search name or email"
                      v-model="activeUserSearchData"
                      @change="activeUserSearch"
                      class="fc-input-full-border2 width280px"
                    ></el-input>
                  </div>
                </div>
              </div>
              <div class="occupantPortal-tab">
                <div class="occupantPortal-tabrow">
                  <setup-loader v-if="loading" class="m10 width98 shadow-none">
                    <template #setupLoading>
                      <spinner :show="loading" size="80"></spinner>
                    </template>
                  </setup-loader>
                  <setup-empty
                    v-else-if="$validation.isEmpty(userlist) && !loading"
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
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            {{ $t('setup.approvalprocess.name') }}
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            {{ $t('setup.setup_profile.email') }}
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            {{ $t('common.header.phone') }}
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          >
                            {{ $t('common.roles.role') }}
                          </th>
                          <th
                            class="setting-table-th setting-th-text uppercase"
                          ></th>
                        </tr>
                      </thead>
                      <tbody>
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
                            {{ user.role ? user.role.name : '---' }}
                          </td>
                          <td class="width200px pL0 pR0 nowrap">
                            <i
                              class="visibility-hide-actions el-icon-edit fc-setup-list-edit"
                              @click="editUser(user)"
                            ></i>
                            <i
                              class="visibility-hide-actions el-icon-delete fc-setup-list-delete "
                              @click="deleteUser(user)"
                            ></i>
                          </td>
                        </tr>
                      </tbody>
                    </table>
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
                  </div>
                </portal>
              </div>
            </template>
            <template v-else-if="sidebarTabList === 'roles'">
              <div class="fc-portal-inner-summary-header">
                <div class="fc-black2-18 text-left bold">
                  {{ $t('setup.setup.roles') }}
                </div>
              </div>
              <portalRoles
                :app="app"
                :isActiveRoles="sidebarTabList === 'roles'"
                class="portal-summary"
              ></portalRoles>
            </template>

            <template v-else-if="sidebarTabList === 'preferences'">
              <div class="fc-portal-inner-summary-header">
                <div class="fc-black2-18 text-left bold">
                  Preferences
                </div>
              </div>
              <AppPreferences
                :app="app"
                :currentAppId="currentAppId"
                @onSave="updateConfig"
                @reloadApp="showReloadApp = true"
              ></AppPreferences>
            </template>
            <template v-else-if="sidebarTabList === 'tabsLayout'">
              <TabsAndLayouts
                :appId="appId"
                :isActive="sidebarTabList === 'tabsLayout'"
                class="portal-summary fc-occupant-portal-tabs"
              ></TabsAndLayouts>
            </template>
          </template>
        </setupSummaryMainSection>
      </div>

      <NewApp
        v-if="showAppEdit"
        :selectedApp="app"
        @onSave="updateApp"
        @onClose="showAppEdit = false"
      ></NewApp>

      <UserForm
        v-if="showDialog"
        :isNew="isNew"
        :user="currentUser"
        :app="app"
        :save="saveUser"
        formType="workCenter"
        @onClose="showDialog = false"
      ></UserForm>
      <AddExistingUser
        v-if="showExistingDialog"
        :app="app"
        @close="showExistingDialog = false"
        @saved="reLoadUser"
      ></AddExistingUser>
    </div>
  </div>
</template>
<script>
import AppPreferences from './AppPreferences'
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import NewApp from './NewApp'
import { loadLayouts } from 'util/webtabUtil'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import setupSummaryMainSection from 'pages/setup/components/SetupSummaryMainSection'
import setupSummarySidebar from 'src/pages/setup/components/SetupSummarySidebar'
import setupSummaryHeader from 'src/pages/setup/components/SetupSummaryHeader'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import UserForm from 'pages/setup/portal/PortalUserForm'
import AddExistingUser from 'pages/setup/portal/ExistingUserForm'
import portalRoles from 'pages/setup/portal/v1/PortalRoles'
import { mapGetters } from 'vuex'
import UserAvatar from '@/avatar/User'
import pick from 'lodash/pick'

export default {
  props: ['application'],
  components: {
    AppPreferences,
    TabsAndLayouts,
    NewApp,
    UserForm,
    setupSummaryMainSection,
    setupSummarySidebar,
    setupSummaryHeader,
    Pagination,
    SetupLoader,
    SetupEmpty,
    AddExistingUser,
    portalRoles,
    UserAvatar,
  },

  data() {
    return {
      app: null,
      loading: false,
      activeTab: 'users',
      activeName: 'Users',
      showExistingDialog: false,
      showAppEdit: false,
      showReloadApp: false,
      sidebarTabList: ['users', 'roles', 'preferences', 'tabsLayout'],
      showFilter: false,
      rolesListData: null,
      statusData: 'all',
      userlist: [],
      activeUserSearchData: '',
      isNew: false,
      userlistCount: null,
      showDialog: false,
      page: 1,
      perPage: 50,
      sidebarMenuList: [
        {
          menuList: [
            {
              name: 'Manage',
              submenuList: [
                {
                  name: 'Users',
                  link: 'users',
                  icon: 'svgs/user-new1',
                  iconClass: 'icon icon-md op66 mR13',
                },
                {
                  name: 'Roles',
                  link: 'roles',
                  icon: 'svgs/roles-new',
                  iconClass:
                    'icon icon-xl op66 mR10 vertical-text-bottom fc-role-icon-align',
                },
                {
                  name: 'Preferences',
                  link: 'preferences',
                  icon: 'svgs/prefrences',
                  iconClass: 'icon icon-md op66 mR10 vertical-text-bottom',
                },
              ],
            },
            {
              name: 'Customization',
              submenuList: [
                {
                  name: 'Tabs And Layouts',
                  link: 'tabsLayout',
                  icon: 'svgs/tabs',
                  iconClass: 'icon icon-sm-md op66 mR14 vertical-bottom',
                },
              ],
            },
          ],
        },
      ],
    }
  },

  beforeRouteLeave(to, from, next) {
    if (this.showReloadApp) {
      let path = this.$router.resolve({ path: to.path }).href
      window.location.href = path
    } else {
      next()
    }
  },

  created() {
    this.loadAppDetails()
    this.loadusers()
    this.loadUsersCount()
    this.sidebarTabList = 'users'
  },

  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.loadusers()
      }
    },
  },

  computed: {
    ...mapGetters(['getRoleNameById']),
    appId() {
      let {
        params: { appId },
      } = this.$route
      return parseInt(appId)
    },
    appUrl() {
      let { appDomain, linkName } = this.app || {}
      let { domain } = appDomain || {}
      return `https://${domain || 'domain'}/${linkName}`
    },
    currentAppId() {
      return (getApp() || {}).id
    },
  },

  methods: {
    async loadAppDetails() {
      this.loading = true
      let { error, data } = await loadLayouts(this.appId)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.app = data
      }
      this.loading = false
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
    redirectToAppList() {
      this.$router.push({ name: 'app-list' })
    },
    updateApp() {
      this.showReloadApp = true
      this.loadAppDetails()
    },
    async loadusers() {
      let { appId } = this
      this.loading = true
      let search
      let inviteAcceptStatus
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      if (this.statusData === 'active') {
        inviteAcceptStatus = true
      } else if (this.statusData === 'pending') {
        inviteAcceptStatus = false
      } else {
        inviteAcceptStatus = null
      }
      await API.get(
        `/v2/application/users/list?page=${this.page}&perPage=${this.perPage}`,
        { appId, search, inviteAcceptStatus }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlist = data.users || []
        }
        this.loading = false
      })
    },
    async loadUsersCount() {
      let { appId } = this
      let search
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      let inviteAcceptStatus
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      if (this.statusData === 'active') {
        inviteAcceptStatus = true
      } else if (this.statusData === 'pending') {
        inviteAcceptStatus = false
      } else {
        inviteAcceptStatus = null
      }
      await API.get(
        `/v2/application/users/list?appId=${appId}&page=${this.page}&perPage=${this.perPage}&fetchCount=true`,
        {
          search,
          inviteAcceptStatus,
        }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlistCount = data.count
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
          this.loadusers()
          this.loadUsersCount()
          this.showDialog = false
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
    setupHomeRoute() {
      return this.$router.replace({
        name: 'setup',
      })
    },
    showHideMenu(link) {
      if (link === 'preferences') {
        return (this.sidebarTabList = 'preferences')
      } else if (link === 'tabsLayout') {
        return (this.sidebarTabList = 'tabsLayout')
      } else if (link === 'users') {
        return (this.sidebarTabList = 'users')
      } else if (link === 'roles') {
        return (this.sidebarTabList = 'roles')
      }
      return (this.sidebarTabList = 'users')
    },
    deleteApp() {
      let { appId } = this
      let { name } = this.app

      this.$dialog
        .confirm({
          title: 'Delete App',
          htmlMessage: `Are you sure you want to delete ${name} app?`,
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(async value => {
          if (value) {
            let { error } = await API.post(
              '/v2/application/deleteApplication',
              { appId }
            )

            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.$message.success(`${name} app deleted successfully`)
              this.redirectToAppList()
            }
          }
        })
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied - ' + copy,
        type: 'success',
      })
    },
    handleCommand(command) {
      if (command === 'editApp') {
        this.showAppEdit = true
      } else if (command === 'delete') {
        this.deleteApp()
      }
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
    activeUserSearch() {
      this.page = 1
      this.perPage = 50
      this.loadusers()
      this.loadUsersCount()
    },
    getRoleName(user) {
      let { linkName } = this.app
      let rolesMap = user.rolesMap || {}
      let roleId = rolesMap[linkName]

      return roleId ? this.getRoleNameById(roleId) : '---'
    },
    editUser(user) {
      this.isNew = false
      this.showDialog = true
      this.currentUser = user
    },
    reLoadUser() {
      this.loadusers()
      this.loadUsersCount()
      this.showExistingDialog = false
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
                  this.loadusers()
                  this.loadUsersCount()
                }
              }
            )
          }
        })
    },
  },
}
</script>
<style lang="scss">
.app-details {
  height: 100%;
  overflow: hidden;

  .app-details-header {
    display: flex;
    justify-content: space-between;

    .header-title {
      font-size: 18px;
      letter-spacing: 0.5px;
      color: #324056;
    }
    .app-summary {
      .add-app-btn,
      .add-group-btn {
        border-radius: 3px;
        border-color: transparent;
        background-color: #ee518f;
        padding: 10px;
        width: 200px;
        color: #fff;
        font-weight: 500;
        text-transform: uppercase;
        cursor: pointer;
      }
      .add-app-btn:hover,
      .add-group-btn:hover {
        font-weight: bold;
      }
    }
    .app-more-options {
      background: #fff;
      border-radius: 3px;
      box-shadow: 0 2px 4px 0 rgb(230 230 230 / 50%);
      border: 1px solid #d9e0e7;
      color: #605e88;
      margin-left: 10px;
      cursor: pointer;
      height: 40px;
    }
  }
  .app-details-container {
    height: calc(100vh - 140px);
    margin-top: 20px;

    .el-tabs__header {
      margin: 0px;
    }

    .app-summary.layout-tabs {
      height: calc(100vh - 170px);

      .layout-icons {
        top: 10px;
      }
      .el-tabs {
        margin-top: 0px;

        .el-tabs__header {
          margin-bottom: 20px;
        }

        .el-tabs__content {
          height: calc(100vh - 270px);
        }
      }
      .layout-container {
        height: 100%;
      }
    }
  }
}
</style>
