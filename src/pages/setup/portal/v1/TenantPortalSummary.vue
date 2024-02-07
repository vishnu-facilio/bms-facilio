<template>
  <div class="height100vh">
    <!-- Tenant Header Section -->
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
              @click="setupUsersRoute"
            >
              {{ $t('common.header.tenant_portal') }}
            </div>
            <div class="fc-setup-breadcrumb-inner pL10 pR10">
              <i class="el-icon-arrow-right f14 fwBold"></i>
            </div>
            <div class="fc-breadcrumbBold-active">
              {{ $t('setup.portal.summary') }}
            </div>
          </div>
          <div class="setting-title-block flex-middle justify-content-space">
            <div class="flex-middle pT15">
              <div
                class="fc-portal-icon fc-portal-icon-tenant-bg flex-center-vH align-center"
              >
                <inline-svg
                  src="svgs/apps/tenant"
                  iconClass="icon icon-xl fill-white"
                ></inline-svg>
              </div>
              <div class="visibility-visible-actions">
                <div class="fc-grey-txt18 pL10 pB5">
                  {{ application.name }}
                  <el-dropdown @command="handleCommand($event)">
                    <span class="el-dropdown-link">
                      <i class="el-icon-more fc-portal-summary-more"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="editApp">
                        {{ $t('setup.portal.edit_app') }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
                <div class="flex-middle">
                  <a
                    :href="`https://${application.appDomain.domain}`"
                    target="_blank"
                    class="fc-app-link-color pL10 f14"
                  >
                    {{ `https://${application.appDomain.domain}` }}
                  </a>
                  <div
                    @click="
                      copyLinkName(`https://${application.appDomain.domain}`)
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
          class="portal-summary flex-middle"
        ></portal-target>
      </template>
    </setupSummaryHeader>

    <div class="fc-main d-flex relative">
      <!-- Tenant sidebar -->
      <setupSummarySidebar v-if="showSidebar">
        <template #setupsidebar>
          <div v-for="(sidebar, list) in sidebarMenuList" :key="list">
            <div v-for="(headingName, inex) in sidebar.menuList" :key="inex">
              <div class="fc-pink f14 bold pB10">
                {{ headingName.name }}
              </div>
              <div v-for="(subMenu, iex) in headingName.submenuList" :key="iex">
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
      <setupSummaryMainSection
        :class="{ widthChangeCon: showSidebar == false }"
      >
        <template #setupMain>
          <div
            @click="showArrow"
            class="fc-open-arrow pointer"
            v-tippy="{ arrow: true, arrowType: 'round' }"
            content="Open Sidebar"
          >
            <i class="el-icon-d-arrow-right f18" v-if="showSideBarOpen"></i>
          </div>
          <template v-if="sidebarTabList === 'users'">
            <!-- user table list -->

            <div
              class="fc-portal-inner-summary-header flex-middle justify-content-space"
            >
              <div class="fc-black2-18 text-left bold">
                {{ $t('setup.users_management.users') }}
              </div>

              <div class="flex-middle">
                <!-- pagination -->
                <pagination
                  ref="pagination"
                  :total="userlistCount"
                  :perPage="perPage"
                  :currentPage.sync="page"
                  class="flex-middle justify-content-end p0 pL10"
                >
                </pagination>
                <!-- filter option -->
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
                    iconClass="icon icon-sm-md fc-fill-path-grey"
                  ></inline-svg>
                  <div
                    :class="{
                      'dot-active-pink': querySearch,
                    }"
                  ></div>
                  <!-- <i class="fc-dot"></i> -->
                </div>
              </div>
            </div>

            <div v-if="showFilter" class="fc-show-filter-con flex-middle">
              <div class="relative">
                <div class="fc-black-13 text-left bold pB5">Search</div>

                <el-input
                  placeholder="Search"
                  v-model="querySearch"
                  @change="searchLoadData"
                  class="fc-input-full-border2 width250px"
                ></el-input>
              </div>
            </div>

            <div class="occupantPortal-tab">
              <SetupLoader class="m10 width98" v-if="loading">
                <template #setupLoading>
                  <spinner :show="loading" size="80"></spinner>
                </template>
              </SetupLoader>
              <setup-empty
                v-else-if="$validation.isEmpty(userlist) && !loading"
                class="m10 width98"
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
              </setup-empty>
              <div class="occupantPortal-tabrow" v-else>
                <div class="col-lg-12 col-md-12">
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
                          {{ user.email }}
                        </td>
                        <td class="width200px">
                          {{ user.tenant.name }}
                        </td>
                        <td>
                          {{ getRoleName(user) }}
                        </td>
                        <td class="pL0 pR0 nowrap">
                          <i
                            class="visibility-hide-actions el-icon-edit fc-setup-list-edit"
                            @click="editTenant(user)"
                          ></i>
                          <i
                            class="visibility-hide-actions el-icon-delete fc-setup-list-delete"
                            @click="showDelete(user)"
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
                      <el-dropdown-item command="existing">{{
                        $t('common.header.existing_user')
                      }}</el-dropdown-item>
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
      @onSave="reloadTenantUser"
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
import TenantUserForm from 'pages/setup/portal/PortalUserForm'
import UserAvatar from '@/avatar/User'
import FDialog from '@/FDialogNew'
import { API } from '@facilio/api'
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import NewApp from 'pages/setup/apps/NewApp'
import pick from 'lodash/pick'
import { mapGetters } from 'vuex'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import setupSummaryMainSection from 'pages/setup/components/SetupSummaryMainSection'
import setupSummarySidebar from 'src/pages/setup/components/SetupSummarySidebar'
import setupSummaryHeader from 'src/pages/setup/components/SetupSummaryHeader'
import portalRoles from 'pages/setup/portal/v1/PortalRoles'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import { getApp } from '@facilio/router'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['application'],
  data() {
    return {
      loading: true,
      activeName: 'Users',
      showDialog: false,
      isNew: true,
      showExistingDialog: false,
      userlist: [],
      showTenantOptionDialog: false,
      selectedDelete: null,
      currentUser: null,
      showFilter: false,
      showAppEdit: false,
      showSidebar: true,
      showSideBarOpen: false,
      app: null,
      searchHide: true,
      searchShow: false,
      querySearch: null,
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
      rolesList: [],
      sidebarTabList: ['users', 'roles', 'tabsLayout'],
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
  components: {
    PortalExistingUser,
    FDialog,
    Spinner,
    TenantUserForm,
    UserAvatar,
    TabsAndLayouts,
    // AppPreferences,
    NewApp,
    Pagination,
    setupSummaryMainSection,
    setupSummarySidebar,
    setupSummaryHeader,
    portalRoles,
    SetupLoader,
    SetupEmpty,
  },
  async created() {
    this.app = { ...this.application }
    await this.$store.dispatch('loadRoles')
    this.loadTenantUser()
    this.loadTenantUserCount()
    this.sidebarTabList = 'users'
  },
  computed: {
    ...mapGetters(['getRoleNameById']),
    appId() {
      return this.app.id
    },
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.loadTenantUser()
        this.loadTenantUserCount()
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
    async loadTenantUser(force = true) {
      this.loading = true
      let filters = {}
      let genericSearch
      if (this.querySearch) {
        genericSearch = this.querySearch
      }
      filters.isTenantPortalAccess = {
        operatorId: 15,
        value: ['true'],
      }
      await API.get(
        `/v2/tenantcontact/list?page=${this.page}&perPage=${this.perPage}`,
        {
          filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          genericSearch,
        },
        { force }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlist = data.tenantcontacts || []
        }
      })
      this.loading = false
    },
    async loadTenantUserCount(force = true) {
      this.loading = true

      let filters = {}
      let genericSearch
      if (this.querySearch) {
        genericSearch = this.querySearch
      }
      filters.isTenantPortalAccess = {
        operatorId: 15,
        value: ['true'],
      }
      await API.get(
        '/v2/tenantcontact/list?fetchCount=true',
        {
          filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          genericSearch,
        },
        { force }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlistCount = data.recordCount || []
        }
      })
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
        }
        this.loadTenantUser()
        this.loadTenantUserCount()
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

      let props = [
        'name',
        'email',
        'phone',
        'language',
        'tenant',
        'rolesMap',
        'securityPolicyMap',
        'scopingsMap',
      ]
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
          this.loadTenantUserCount()
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
    setupHomeRoute() {
      return this.$router.replace({
        path: `/${this.appLinkName}/setup/home`,
      })
    },
    setupUsersRoute() {
      return this.$router.replace({
        path: `/${this.appLinkName}/setup/general/portal`,
      })
    },
    showHideMenu(link) {
      if (link === 'users') {
        return (this.sidebarTabList = 'users')
      } else if (link === 'preferences') {
        return (this.sidebarTabList = 'preferences')
      } else if (link === 'tabsLayout') {
        return (this.sidebarTabList = 'tabsLayout')
      } else if (link === 'roles') {
        return (this.sidebarTabList = 'roles')
      }
      return (this.sidebarTabList = 'users')
    },
    async copyLinkName(copy) {
      await navigator.clipboard.writeText(copy)
      this.$message({
        message: 'Copied - ' + copy,
        type: 'success',
      })
    },
    showHideArrow() {
      this.showSidebar = false
      this.showSideBarOpen = true
    },
    showArrow() {
      this.showSidebar = true
      this.showSideBarOpen = false
    },
    searchShowHide() {
      this.searchHide = false
      this.searchShow = true
    },
    searchLoadData() {
      this.page = 1
      this.perPage = 50
      this.loadTenantUser()
      this.loadTenantUserCount()
    },
    reloadTenantUser() {
      this.loadTenantUser()
      this.loadTenantUserCount()
    },
    handleCommand(command) {
      if (command === 'editApp') {
        this.showAppEdit = true
      }
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
  },
}
</script>
