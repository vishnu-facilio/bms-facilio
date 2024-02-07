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
            <div class="fc-breadcrumbBold-active">
              {{ $t('setup.portal.summary') }}
            </div>
          </div>
          <div class="setting-title-block flex-middle justify-content-space">
            <div class="flex-middle pT10">
              <div
                class="fc-portal-icon fc-portal-icon-api-setup-bg flex-center-vH align-center"
              >
                <inline-svg
                  src="svgs/api"
                  iconClass="icon text-center icon-lg fc-fill-white"
                ></inline-svg>
              </div>
              <div class="visibility-visible-actions">
                <div class="fc-grey-txt18 pL10">
                  API Setup
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
      <setupSummarySidebar>
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
      <setupSummaryMainSection>
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
            <dev-users
              v-if="!loading"
              :app="devApp"
              @onEditUser="editUser"
            ></dev-users>
          </template>
          <template v-if="sidebarTabList === 'modules'">
            <api-modules ref="listView" :appId="devApp.id"> </api-modules>
          </template>
        </template>
      </setupSummaryMainSection>
    </div>
  </div>
</template>
<script>
import setupSummaryHeader from 'src/pages/setup/components/SetupSummaryHeader'
import setupSummaryMainSection from 'pages/setup/components/SetupSummaryMainSection'
import setupSummarySidebar from 'src/pages/setup/components/SetupSummarySidebar'
import DevUsers from './DevUsersNew'
import ApiModules from './APIModulesNew'
import { loadApps } from 'util/appUtil'
import { getApp } from '@facilio/router'

export default {
  async created() {
    this.sidebarTabList = 'users'
    this.loading = true
    let { error, data } = await loadApps()
    if (error) {
      this.$message.error(error.message || 'Error Occured')
    }
    this.devApp = data.filter(app => app.linkName === 'dev')[0]
    this.loading = false
  },
  computed: {
    appLinkName() {
      let appName = getApp().linkName
      return appName
    },
  },
  methods: {
    setupHomeRoute() {
      return this.$router.replace({
        path: `/${this.appLinkName}/setup/home`,
      })
    },
    editUser(user) {
      this.currentUser = user
      this.isNew = false
      this.showDialog = true
    },
    showArrow() {
      this.showSidebar = true
      this.showSideBarOpen = false
    },
    showHideMenu(link) {
      if (link === 'users') {
        return (this.sidebarTabList = 'users')
      } else {
        return (this.sidebarTabList = 'modules')
      }
    },
  },
  data() {
    return {
      isNew: false,
      showDialog: false,
      currentUser: null,
      devApp: null,
      loading: true,
      activeName: 'Users',
      showSidebar: true,
      showSideBarOpen: false,
      sidebarTabList: ['users'],
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
                // {
                //   name: 'Modules',
                //   link: 'modules',
                //   icon: 'svgs/tabs',
                //   iconClass: 'icon icon-sm-md op66 mR14 vertical-bottom',
                // },
              ],
            },
          ],
        },
      ],
    }
  },
  components: {
    setupSummaryHeader,
    setupSummaryMainSection,
    setupSummarySidebar,
    DevUsers,
    ApiModules,
  },
}
</script>
