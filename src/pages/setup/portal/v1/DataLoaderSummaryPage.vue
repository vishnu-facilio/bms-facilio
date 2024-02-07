<template>
  <div class="height100vh">
    <!-- Header -->
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
              {{ application.name }}
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
                class="fc-portal-icon fc-tools-icon-common-bg flex-center-vH align-center"
              >
                <inline-svg
                  src="svgs/dataloader-icon"
                  iconClass="icon icon-xxllg"
                ></inline-svg>
              </div>
              <div class="visibility-visible-actions">
                <div class="fc-grey-txt18 pL10 pB5">
                  {{ application.name }}
                </div>
                <div class="flex-middle pL10 fc-grey4-13">
                  {{ $t('setup.dataloader.dataloader_desc') }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
      <template #summaryRightSide>
        <portal-target
          name="header-buttons"
          class="portal-summary"
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
            <div
              class="fc-portal-inner-summary-header flex-middle justify-content-space height50 fc-application-summary-search"
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

            <div v-if="showFilter" class="fc-show-filter-con relative">
              <div class="flex-middle">
                <div class="relative">
                  <div class="fc-black-13 text-left bold pB5">
                    {{ $t('common._common.search') }}
                  </div>
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
              <setup-loader v-if="loading" class="m10 width98">
                <template #setupLoading>
                  <spinner :show="loading" size="80"></spinner>
                </template>
              </setup-loader>
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
                          {{ $t('setup.setup_profile.phone') }}
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
          <template v-else-if="false">
            <div class="fc-portal-inner-summary-header">
              <div class="fc-black2-18 text-left bold">
                {{ $t('setup.setup.roles') }}
              </div>
            </div>
            <portalRoles
              :app="application"
              :isActiveRoles="sidebarTabList === 'roles'"
              class="portal-summary"
            ></portalRoles>
          </template>
          <template v-else-if="sidebarTabList === 'dataloader'">
            <div class="fc-portal-inner-summary-header">
              <div class="fc-black2-18 text-left bold">
                {{ 'Data Loader Desktop' }}
              </div>
            </div>
            <dataLoaderAppDownload
              class="portal-summary fc-occupant-portal-tabs tabs-layout-table"
            ></dataLoaderAppDownload>
          </template>
        </template>
      </setupSummaryMainSection>
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
      @saved="reloadUser"
    ></AddExistingUser>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import AddExistingUser from 'pages/setup/portal/ExistingUserForm'
import UserAvatar from '@/avatar/User'
import UserForm from 'pages/setup/portal/PortalUserForm'
import pick from 'lodash/pick'
import { mapGetters } from 'vuex'
import setupSummaryMainSection from 'pages/setup/components/SetupSummaryMainSection'
import setupSummarySidebar from 'src/pages/setup/components/SetupSummarySidebar'
import setupSummaryHeader from 'src/pages/setup/components/SetupSummaryHeader'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import portalRoles from 'pages/setup/portal/v1/PortalRoles'
import { getApp } from '@facilio/router'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import dataLoaderAppDownload from 'pages/setup/portal/v1/DataLoaderAppDownload'

export default {
  props: ['application'],
  components: {
    UserAvatar,
    UserForm,
    AddExistingUser,
    setupSummaryMainSection,
    setupSummarySidebar,
    setupSummaryHeader,
    SetupLoader,
    SetupEmpty,
    portalRoles,
    Pagination,
    dataLoaderAppDownload,
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
      activeUserSearchData: '',
      currentUser: null,
      sidebarTabList: ['users', 'roles', 'dataloader'],
      showSidebar: true,
      showSideBarOpen: false,
      showUserSearchInput: false,
      userSearchIcon: true,
      showFilter: false,
      page: 1,
      perPage: 50,
      userlistCount: '',
      rolesListData: null,
      rolesList: [],
      statusData: 'all',
      statusList: [
        {
          label: 'All Users',
          value: 'all',
        },
        {
          label: 'Active Users',
          value: 'active',
        },
        {
          label: 'Pending Users',
          value: 'pending',
        },
      ],
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
              ],
            },
            {
              name: 'Download',
              submenuList: [
                {
                  name: 'Data Loader Desktop',
                  link: 'dataloader',
                  icon: 'svgs/desktop',
                  iconClass: 'icon icon-sm-md op66 mR14 vertical-bottom',
                },
              ],
            },
          ],
        },
      ],
    }
  },
  async created() {
    await this.$store.dispatch('loadRoles')
    this.loadApplicationUser()
    this.loadRolesForApp()
    this.sidebarTabList = 'users'
    this.loadAppicationCount()
  },
  computed: {
    ...mapGetters(['getRoleNameById']),
    appId() {
      return this.application.id
    },
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
  },
  watch: {
    page(newVal, oldVal) {
      if (oldVal != newVal) {
        this.loadApplicationUser()
      }
    },
  },
  methods: {
    openSummary(user) {
      this.$router.push({
        name: 'userSummary',
        params: {
          appName: this.appLinkName,
          id: user.id,
          appId: this.appId,
        },
      })
    },
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
    async loadApplicationUser(force = true) {
      let { appId } = this
      this.loading = true
      let search
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      return await API.get(
        `/v2/application/users/list?page=${this.page}&perPage=${this.perPage}`,
        { appId, search },
        { force }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlist = data.users || []
        }
        this.loading = false
      })
    },
    async loadAppicationCount() {
      let { appId } = this
      this.loading = true
      let search
      if (this.activeUserSearchData) {
        search = this.activeUserSearchData
      }
      await API.get(
        `/v2/application/users/list?appId=${appId}&page=${this.page}&perPage=${this.perPage}&fetchCount=true`,
        { search }
      ).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.userlistCount = data.count || []
        }
      })
      this.loading = false
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
      } else if (link === 'dataloader') {
        return (this.sidebarTabList = 'dataloader')
      } else if (link === 'roles') {
        return (this.sidebarTabList = 'roles')
      }
      return (this.sidebarTabList = 'users')
    },
    showArrow() {
      this.showSidebar = true
      this.showSideBarOpen = false
    },
    showUserSearch() {
      this.showUserSearchInput = true
      this.userSearchIcon = false
    },
    clearUser() {
      this.showUserSearchInput = false
      this.userSearchIcon = true
      this.activeUserSearchData = null
      this.activeUserSearch()
    },
    activeUserSearch() {
      this.page = 1
      this.perPage = 50
      this.loadApplicationUser()
      this.loadAppicationCount()
    },
    reloadUser() {
      this.loadApplicationUser()
      this.loadAppicationCount()
    },
    showExtraFilter() {
      this.showFilter = !this.showFilter
    },
    async loadRolesForApp() {
      let { appId } = this

      this.loading = true
      let { error, data } = await API.get('/setup/roles', { appId })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.rolesList = (data.roles || []).filter(
          role => role.name !== 'Super Administrator'
        )
      }
      this.loading = false
    },
  },
}
</script>
<style lang="scss">
.tabs-layout-table {
  .el-table td.el-table__cell,
  .el-table th.el-table__cell.is-leaf {
    border-bottom: 1px solid #f2f5f6;
  }
}
</style>
