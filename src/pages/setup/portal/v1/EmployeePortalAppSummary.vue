<template>
  <div class="height100vh">
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
              {{ $t('setup.setup.employee_portal') }}
            </div>
            <div class="fc-setup-breadcrumb-inner pL10 pR10">
              <i class="el-icon-arrow-right f14 fwBold"></i>
            </div>
            <div class="fc-breadcrumbBold-active">
              {{ $t('setup.portal.summary') }}
            </div>
          </div>
          <div class="setting-title-block flex-middle justify-content-space">
            <div class="flex-middle pT10">
              <div
                class="fc-portal-icon fc-portal-icon-occpupant-bg flex-center-vH align-center"
              >
                <inline-svg
                  src="svgs/apps/portal"
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
                <div class="flex-middle pT5">
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
            <portal to="header-buttons">
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
            </portal>
            <EmployeeUsers
              ref="userList"
              :app="app"
              :editEmployee="editEmployee"
            />
          </template>
          <template v-else-if="sidebarTabList === 'ssoShow'">
            <sso :showHeader="false" class="fc-occupant-sso-header"></sso>
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
    <EmployeeUserForm
      v-if="showDialog"
      :isNew="isNew"
      formType="employee"
      :user="currentUser"
      :app="app"
      :save="saveEmployee"
      @onClose="showDialog = false"
    ></EmployeeUserForm>
    <PortalExistingUser
      v-if="showExistingDialog"
      :app="app"
      :fetchParams="fetchParams"
      parentModule="employee"
      :save="saveEmployee"
      @onSave="reloadEmployees"
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
import { API } from '@facilio/api'
import pick from 'lodash/pick'
import EmployeeUsers from 'pages/setup/portal/EmployeePortalUsers'
import EmployeeUserForm from 'pages/setup/portal/PortalUserForm'
import sso from 'src/pages/setup/EmployeePortalDomainSSO.vue'
import TabsAndLayouts from 'pages/setup/customization/TabsAndLayouts'
import NewApp from 'pages/setup/apps/NewApp'
import setupSummaryMainSection from 'pages/setup/components/SetupSummaryMainSection'
import setupSummarySidebar from 'src/pages/setup/components/SetupSummarySidebar'
import setupSummaryHeader from 'src/pages/setup/components/SetupSummaryHeader'
import { getApp } from '@facilio/router'
import portalRoles from 'pages/setup/portal/v1/PortalRoles'
import PortalExistingUser from 'src/pages/setup/portal/PortalExistingUser'

export default {
  props: ['application'],
  components: {
    EmployeeUsers,
    EmployeeUserForm,
    sso,
    TabsAndLayouts,
    NewApp,
    setupSummaryMainSection,
    setupSummarySidebar,
    setupSummaryHeader,
    portalRoles,
    PortalExistingUser,
  },
  title() {
    return 'Employee Portal'
  },
  data() {
    return {
      showDialog: false,
      isNew: false,
      loading: false,
      showExistingDialog: false,
      userlist: [],
      currentUser: null,
      showAppEdit: false,
      app: null,
      portalInfo: [],
      showSidebar: true,
      showSideBarOpen: false,
      sidebarTabList: ['users', 'roles', 'ssoShow', 'tabsLayout'],
      fetchParams: {
        params: {
          filters: JSON.stringify({
            employeePortalAccess: {
              operatorId: 15,
              value: ['false'],
            },
            peopleType: {
              operatorId: 54,
              value: ['3'],
            },
          }),
        },
        url: '/v2/people/list',
        dataKey: 'people',
      },
      fetchParamsCount: {
        params: {
          filters: JSON.stringify({
            employeePortalAccess: {
              operatorId: 15,
              value: ['false'],
            },
            peopleType: {
              operatorId: 54,
              value: ['3'],
            },
          }),
          fetchCount: true,
        },
        url: '/v2/people/list',
        dataKey: 'people',
      },
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
                  name: 'SSO',
                  link: 'ssoShow',
                  icon: 'svgs/sso',
                  iconClass:
                    'icon icon-lg op66 vertical-text-bottom fc-sso-icon',
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
  created() {
    this.app = { ...this.application }
    this.sidebarTabList = 'users'
    // this.loadPortInfo()
  },
  computed: {
    appId() {
      return this.app.id
    },
    appLinkName() {
      let appName = getApp().linkName
      return appName
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
    editEmployee(user) {
      this.isNew = false
      this.showDialog = true
      this.currentUser = user
    },
    saveEmployee(employee) {
      let { isNew } = this

      let url = ''
      let successMsg = ''
      if (isNew) {
        url = '/v2/employee/addfromPortal'
        successMsg = this.$t('common.products.new_employee_added')
      } else {
        url = '/v2/employee/updatefromPortal'
        successMsg = this.$t('common.products.employee_updated')
      }

      let props = [
        'name',
        'email',
        'phone',
        'rolesMap',
        'language',
        'securityPolicyMap',
      ]
      if (!isNew) props.push('id')

      let params = {
        employees: [
          {
            ...pick(employee, props),
            peopleType: 3,
            employeePortalAccess: true,
          },
        ],
      }

      return API.post(url, params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Ocurred')
          throw new Error()
        } else {
          this.$message(successMsg)
          this.showDialog = false
          this.showExistingDialog = false
          this.reloadEmployees()
        }
      })
    },
    reloadEmployees() {
      if (this.$refs['userList']) {
        this.$refs['userList'].loadUsersList()
      }
      this.showExistingDialog = false
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
    showHideMenu(link) {
      if (link === 'users') {
        return (this.sidebarTabList = 'users')
      } else if (link === 'ssoShow') {
        return (this.sidebarTabList = 'ssoShow')
      } else if (link === 'roles') {
        return (this.sidebarTabList = 'roles')
      } else if (link === 'preferences') {
        return (this.sidebarTabList = 'preferences')
      } else if (link === 'tabsLayout') {
        return (this.sidebarTabList = 'tabsLayout')
      }
      return (this.sidebarTabList = 'users')
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
    // loadPortInfo() {
    //   API.get('/serviceportal/getportalinfo').then(({ data }) => {
    //     this.portalInfo = data.protalInfo
    //   })
    // },
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
    handleCommand(command) {
      if (command === 'editApp') {
        this.showAppEdit = true
      }
    },
  },
}
</script>
