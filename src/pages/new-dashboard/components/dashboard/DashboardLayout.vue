<template>
  <div class="pleft">
    <div class="layout new-dashboard-layout-page">
      <div v-if="!loading && headerList && !headerList.length && isPermission">
        <div
          class="col-12 flex-middle justify-content-center flex-direction-column height80vh pT30"
        >
          <inline-svg
            src="svgs/empty-dashboard"
            iconClass="text-center icon-xxxlg"
          ></inline-svg>
          <div class="fc-black-24 pT20">
            {{ $t('panel.dashboard.no_widgets') }}
          </div>
          <div class="fc-grayish f18 pT10">
            {{ $t('panel.dashboard.add_widgets') }}
          </div>

          <el-button
            class="fc-create-btn mT20 letter-spacing0_4"
            style="padding: 15px 30px;"
            @click="addDashboard"
          >
            {{ $t('panel.dashboard.ADD') }}
          </el-button>
        </div>
      </div>
      <template v-else>
        <div
          v-if="!loading && isPermission"
          class="subheader-section  flex-align-start flex-middle"
        >
          <div class=" pointer flex flex-shrink-0 flex-no-wrap">
            <inline-svg
              src="svgs/hamburger-menu"
              class="d-flex pointer hamburger-icon self-center"
              iconClass="icon icon-sm"
              @click.native="showListWorkorder = true"
            ></inline-svg>
            <el-dropdown
              class="fc-dropdown-menu pL10 bold"
              @command="handleCommand"
              placement="bottom-start"
              trigger="click"
            >
              <span class="el-dropdown-link">
                {{
                  headerList.find(rt => rt.id === activeFolderNames)
                    ? headerList.find(rt => rt.id === activeFolderNames).label
                    : ''
                }}
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(header, index) in headerList.filter(
                    rt => rt.childrens.length !== 0
                  )"
                  :key="index"
                  :command="header.id"
                  ><span class="">{{ header.label }}</span></el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
            <div class="fc-separator-lg mL25 mR10 self-center"></div>
          </div>

          <div class="d-flex flex-grow ">
            <div
              v-for="(dashboard, index) in activeDashboardList"
              :key="index"
              @click="openDashboard(dashboard)"
              :class="[
                'dashboard-link',
                dashboard.linkName === dashboardLink && 'active-dashboard',
              ]"
            >
              <div class="dashboard-link-name active-dashboard-link">
                {{ dashboard.label }}
              </div>
              <div class="active"></div>
            </div>

            <OutsideClick
              :visibility="true"
              @onOutsideClick="canShowDashboardLinks = false"
            >
              <div
                v-if="!$validation.isEmpty(moreDashboardList)"
                class="pL15 pR15 pointer"
                @click="canShowDashboardLinks = !canShowDashboardLinks"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  version="1.1"
                  id="Capa_1"
                  x="0px"
                  y="0px"
                  width="18px"
                  height="18px"
                  viewBox="0 0 408 408"
                  style="enable-background:new 0 0 408 408;"
                  xml:space="preserve"
                >
                  <g>
                    <g id="more-horiz">
                      <path
                        d="M51,153c-28.05,0-51,22.95-51,51s22.95,51,51,51s51-22.95,51-51S79.05,153,51,153z M357,153c-28.05,0-51,22.95-51,51    s22.95,51,51,51s51-22.95,51-51S385.05,153,357,153z M204,153c-28.05,0-51,22.95-51,51s22.95,51,51,51s51-22.95,51-51    S232.05,153,204,153z"
                        fill="#b1bdc9"
                      ></path>
                    </g>
                  </g>
                </svg>
              </div>
            </OutsideClick>
            <el-popover
              placement="bottom-start"
              trigger="manual"
              :value="canShowDashboardLinks"
              popper-class="more-dashboard-list"
            >
              <div
                v-for="(dashboard, index) in moreDashboardList"
                :key="index"
                @click="openDashboard(dashboard)"
                class="more-dashboard-name"
              >
                {{ dashboard.label }}
              </div>
            </el-popover>
          </div>

          <!-- right Filter portal + Fullscreen & More button group -->
          <div class="d-flex flex-shrink-0 self-center  mL-auto height-auto">
            <portal-target
              name="dashboardFilter"
              class="dashboard-filter-portal d-flex"
            >
            </portal-target>

            <div class="flex-middle  fc-new-dashboard-full-more mR20 mT2">
              <div
                class="pR10 pL10 pointer"
                v-if="$route.path.endsWith('withfilter')"
                @click="toggleFilters"
                title="Filters"
                data-position="left"
                v-tippy="{ arrow: true, animation: 'perspective' }"
              >
                <img src="~assets/filter-grey.svg" />
              </div>
              <div
                class="pR10 pL10 pointer"
                v-if="$route.path.endsWith('withbuilding')"
                @click="toggleFilters"
                title="Filters"
                data-position="left"
                v-tippy="{ arrow: true, animation: 'perspective' }"
              >
                <img src="~assets/filter-grey.svg" />
              </div>
              <div
                class="pointer user-select-none border-right2"
                v-if="$route.path.endsWith('withtemplate')"
                style="padding-bottom: 36px; padding-left: 10px; padding-right: 33px;opacity: 0.6;"
                @click="toggleTemplate"
                title="Filters"
                data-position="left"
                v-tippy="{ arrow: true, animation: 'perspective' }"
              >
                <span class="rotate90"
                  ><img
                    src="~statics/icons/equalization.svg"
                    style="width: 15px;"
                /></span>
              </div>
              <div
                v-on:click="goInFullscreenBody"
                v-on:keyup.esc="exitfullscreen()"
                class="pointer pR10 pL10 border-right2"
              >
                <i
                  class="fa fa-expand fc-grey6 f14"
                  title="Fullscreen"
                  data-position="top"
                  v-tippy="{ arrow: true, animation: 'perspective' }"
                ></i>
              </div>
              <el-dropdown
                class="dashboard-dropdown-right pR10 pL10"
                @command="dashboardCommand"
                trigger="click"
              >
                <span class="el-dropdown-link">
                  <i class="el-icon-more rotate-90 pointer f14 fc-grey6"></i>
                </span>
                <el-dropdown-menu
                  slot="dropdown"
                  class="dashboard-subheader-dp"
                >
                  <el-dropdown-item
                    command="edit"
                    v-if="
                      hasEditPermission(dashboard) && hasDashboardEditPermission
                    "
                  >
                    <div>
                      {{ $t('common._common.edit') }}
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="editFilters"
                    v-if="hasDashboardEditPermission"
                  >
                    <div>
                      {{ $t('dashboardfilters.configure') }}
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="addDashboardRule"
                    v-if="isDashboardRulesEnabled && hasDashboardEditPermission"
                  >
                    <div>
                      Dashboard Rules
                    </div>
                  </el-dropdown-item>

                  <el-dropdown-item v-if="hasSharePermission" command="share"
                    ><span>{{
                      $t('common._common.share')
                    }}</span></el-dropdown-item
                  >
                  <el-dropdown-item command="download">
                    <div>
                      {{ $t('common._common.download') }}
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="mobile"
                    v-if="hasCreatePermission || hasDashboardEditPermission"
                    ><span
                      class="mobiledashboard"
                      v-bind:class="{ active: isMobileDashboard }"
                      >{{
                        !isMobileDashboard
                          ? $t('home.dashboard.enable_mobile_dashboard')
                          : $t('home.dashboard.disable_mobile_dashboard')
                      }}</span
                    ></el-dropdown-item
                  >
                  <el-dropdown-item
                    v-if="hasCreatePermission"
                    command="openClonePopup"
                  >
                    <span>{{ 'Clone' }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item
                    class="mobiledashboard"
                    command="enableDarkMode"
                    v-bind:class="{ active: changetheme }"
                  >
                    <span>{{
                      !changetheme ? 'Enable Dark Mode' : 'Disable Dark Mode'
                    }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="hasCreatePermission || hasDashboardEditPermission"
                    command="dashboardmanager"
                    divided
                    ><img src="~assets/dashboard-pink.svg" class="mR10" />{{
                      $t('home.dashboard.dashboard_manager')
                    }}</el-dropdown-item
                  >
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- dashboard dolder -->
        <el-drawer
          title="Dashboard"
          custom-class="dashboard-picker"
          :visible.sync="showListWorkorder"
          :modal-append-to-body="false"
          :modal="false"
          :direction="'ltr'"
          :before-close="drawerClose"
          :withHeader="false"
        >
          <div class="view-panel">
            <div class="view-list-container">
              <div class="header-container">
                <div class="label-txt-black fwBold text-uppercase">
                  {{ $t('home.dashboard.Dashboard') }}
                </div>
                <inline-svg
                  src="svgs/arrow"
                  class="rotate-90 pointer d-flex back-icon mL-auto"
                  iconClass="icon icon-xs"
                  @click.native="drawerClose()"
                ></inline-svg>
              </div>
              <div class="search-container mT20 mb25">
                <el-input
                  placeholder="Search"
                  v-model="searchDashboard"
                  type="search"
                  :prefix-icon="'el-icon-search'"
                  class="fc-input-full-border2"
                >
                </el-input>
              </div>
              <div class="views-group dashboard-chooser-container">
                <div
                  v-for="(subheader, index) in newDashboardList.filter(
                    rt => rt.childrens.length !== 0
                  )"
                  :key="index"
                >
                  <template>
                    <div class="folder-name pB10 mT20 text-uppercase">
                      {{ subheader.label }}
                    </div>
                    <template
                      v-for="(dashboard, idx) in subheader.childrens.filter(
                        rt => !rt.dashboardDiabled
                      )"
                    >
                      <div
                        :key="idx"
                        class="view-item pT10 pB15 pointer"
                        v-bind:class="{
                          active: dashboard.path.path === $route.path,
                        }"
                        @click="
                          openDashboard(dashboard),
                            handleCommand(subheader.id, this, true)
                        "
                      >
                        <div class="view-name ellipsis">
                          {{ dashboard.label }}
                          <span
                            class="pull-right"
                            v-if="dashboard.path.path === $route.path"
                            ><i class="el-icon-check"></i
                          ></span>
                        </div>
                      </div>
                    </template>
                  </template>
                </div>
              </div>
            </div>
            <div
              class="view-manager-btn"
              v-if="hasCreatePermission || hasDashboardEditPermission"
              @click="dashboardCommand('dashboardmanager')"
            >
              <img src="~assets/dashboard-pink.svg" class="" />
              <span class="label mL10 text-uppercase">
                {{ $t('home.dashboard.dashboard_manager') }}
              </span>
            </div>
          </div>
        </el-drawer>
        <dashboard-manager
          :module="getCurrentModule()"
          :visibility.sync="showDashboardManager"
        ></dashboard-manager>
        <dashboardViewer
          @mobiledashboard="mobiledashboardstatus"
          @emitDashboard="setDashboard"
          :printMode="printMode"
          ref="dashboardViewer"
        ></dashboardViewer>
        <ShareWith
          ref="viewer-dashboard-sharing-section"
          :appId="appId"
          :dashboard="dashboard"
          :dialogVisible.sync="sharedialogVisible"
          v-if="sharedialogVisible && dashboard"
        />
        <div class="dashboard-share dialog-box" v-if="isClone">
          <el-dialog
            :title="title"
            :visible.sync="isClone"
            width="35%"
            class="fc-dialog-center-container"
            :append-to-body="true"
          >
            <div class="height200">
              <el-form
                ref="cloneDashboardForm"
                :rules="rules"
                :model="dashboardNameObj"
              >
                <el-form-item
                  label="Dashboard Name"
                  prop="name"
                  :required="true"
                >
                  <el-input
                    class="width100 fc-input-full-border2"
                    autofocus
                    v-model="dashboardNameObj.name"
                    type="text"
                    :placeholder="
                      $t('common.placeholders.enter_dashboard_name')
                    "
                  />
                </el-form-item>
              </el-form>
            </div>
            <span class="dialog-footer row">
              <el-button
                @click="isClone = false"
                class="col-6 shareoverbtn shrbtn1"
              >
                {{ $t('common._common.cancel') }}
              </el-button>
              <el-button
                type="primary"
                :loading="saving"
                class="modal-btn-save"
                @click="dashboardCommand('clone')"
              >
                {{ $t('common.header.clone_dashboard') }}
              </el-button>
            </span>
          </el-dialog>
        </div>
      </template>
    </div>
    <pdf-download :url="pdfUrl" :isDownload.sync="isPdfDownload"></pdf-download>
    <div
      v-if="!isPermission"
      style="text-align: center;margin-top: 20%;font-size: 20px"
    >
      <span>You don't have dashboard access</span>
    </div>
  </div>
</template>
<script>
import dashboardLayoutHelper from 'src/pages/new-dashboard/components/dashboard/DashboardlayoutHelper.js'
import dashboardViewer from './DashboardViewer.vue'
import ShareWith from 'pages/dashboard/components/ShareDashboard'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import PdfDownload from 'src/components/PDFDownload'
import OutsideClick from '@/OutsideClick'
import { getApp } from '@facilio/router'
import DashboardPermission from 'src/pages/new-dashboard/utils/DashboardPermissions.js'
export default {
  mixins: [dashboardLayoutHelper, DashboardPermission],
  components: {
    PdfDownload,
    dashboardViewer,
    ShareWith,
    OutsideClick,
  },
  props: {
    printMode: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      changetheme: false,
      searchDashboard: '',
      sharedialogVisible: false,
      publishdialogVisible: false,
      activeDashboardList: [],
      moreDashboardList: [],
      canShowDashboardLinks: false,
      isPdfDownload: false,
      isClone: false,
      rules: {
        name: {
          required: true,
          message: this.$t('common.placeholders.enter_dashboard_name'),
          trigger: 'change',
        },
      },
      dashboardNameObj: { name: null },
      saving: false,
      isPermission: true,
      appId: null,
    }
  },
  created() {
    this.changetheme =
      window.localStorage.getItem('theme') === 'black' ? true : false
    this.appId = getApp().id
  },
  computed: {
    isDashboardRulesEnabled() {
      return this.$helpers.isLicenseEnabled('DASHBOARD_ACTION') ?? false
    },
    title() {
      return 'Clone Dashboard'
    },
    dashboardList() {
      let { headerList } = this
      headerList.forEach(folder => {
        if (folder.childrens && folder.childrens.length) {
          folder.childrens.filter(dashboard => {
            this.$set(dashboard, 'dashboardDiabled', false)
          })
        }
      })
      return headerList
    },
    newDashboardList() {
      let { dashboardList } = this
      if (dashboardList && dashboardList.length) {
        dashboardList.forEach(folder => {
          if (folder.childrens && folder.childrens.length) {
            folder.childrens.filter(dashboard => {
              if (this.compareString(dashboard.label, this.searchDashboard)) {
                this.$set(dashboard, 'dashboardDiabled', false)
              } else {
                this.$set(dashboard, 'dashboardDiabled', true)
              }
            })
          }
        })
        let selectedList = dashboardList.filter(rt => {
          if (rt.childrens) {
            let childrens = rt.childrens.filter(
              dashboard => !dashboard.dashboardDiabled
            )
            if (childrens && childrens.length) {
              return childrens
            }
          }
        })
        return selectedList
      }
      return []
    },
  },
  watch: {
    $route: function(to, from) {
      if (
        to.path === '/app/home/dashboard' &&
        this.activeFolderNames &&
        this.subheaderMenu &&
        this.subheaderMenu.length
      ) {
        this.$router
          .push({
            path: this.subheaderMenu[0].path.path,
          })
          .catch(() => {})
      }
    },
    dashboardLink: {
      handler(value) {
        if (!isEmpty(value)) {
          this.setHeaderList()
        }
      },
      immediate: true,
    },
    subheaderMenu(value) {
      if (!isEmpty(value)) {
        this.setHeaderList()
      }
    },
  },
  mounted() {
    this.loadDashboards()
  },
  methods: {
    setDashboard(dashboard) {
      this.dashboard = dashboard
    },
    updatePermission(permission) {
      this.isPermission = permission
    },
    compareString(string1, string2) {
      if (string2 === '') {
        return true
      }
      if (string1 && string2) {
        if (string1.toLowerCase().indexOf(string2.toLowerCase()) >= 0) {
          return true
        }
      }
      return false
    },
    switchTheme() {
      let storedTheme = window.localStorage.getItem('theme')
      if (storedTheme === '' || storedTheme === 'white') {
        document.body.classList.remove('fc-white-theme')
        document.body.classList.remove('fc-black-theme')
        window.localStorage.setItem('theme', 'black')
        document.body.classList.add('fc-black-theme')
        this.themeClassNew = 'fc-black-theme'
        this.changetheme = true
      } else if (storedTheme === 'black') {
        document.body.classList.remove('fc-white-theme')
        document.body.classList.remove('fc-black-theme')
        window.localStorage.setItem('theme', 'white')
        document.body.classList.add('fc-white-theme')
        this.themeClassNew = 'fc-white-theme'
        this.changetheme = false
      }
      location.reload()
    },
    isActiveDashboard(dashboard) {
      return false
    },
    changeDashboard(dashboard) {
      // this.$emit('onChange', view, group)
      // this.closeViewSidePanel()
    },
    drawerClose() {
      this.showListWorkorder = false
    },
    openDashboard(dashboardObj) {
      let { linkName, path } = dashboardObj || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_VIEWER) || {}

        if (name) {
          this.$router.push({ name, params: { dashboardlink: linkName } })
        }
      } else {
        this.$router.push({ path: path.path })
      }

      this.showListWorkorder = false
      this.canShowDashboardLinks = false
    },
    addDashboard() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_CREATION) || {}

        name && this.$router.push({ name, query: { create: 'new' } })
      } else {
        this.$router.push({
          path: '/app/home/editdashboard/new?create=new',
        })
      }
    },
    setHeaderList() {
      let { dashboardLink, subheaderMenu } = this

      if (!isEmpty(dashboardLink) && !isEmpty(subheaderMenu)) {
        this.activeDashboardList = []
        this.moreDashboardList = []

        subheaderMenu.forEach(dashboard => {
          if (dashboard.linkName === dashboardLink) {
            let index = subheaderMenu.findIndex(
              dashboard => dashboard.linkName === dashboardLink
            )

            if (index > 2) {
              //not first 3 elements
              this.activeDashboardList.push(dashboard)
            }
          }
        })

        let remainingElements = 3 - this.activeDashboardList.length // default 3 elemnts to display
        let remainingActiveDashboard = subheaderMenu.slice(0, remainingElements)

        this.activeDashboardList.unshift(...remainingActiveDashboard)

        this.moreDashboardList = subheaderMenu.filter(dashboard => {
          let isActive = this.activeDashboardList.find(
            activeDashboard => activeDashboard.linkName === dashboard.linkName
          )

          return !isActive
        })
      }
    },
  },
}
</script>
<style>
.comment-icon-size {
  width: 14px;
  height: 14px;
  margin-left: 14px;
  margin-right: 5px;
}

.dashboard-edit {
  position: relative;
  font-size: 16px;
  right: 18px;
  top: 2px;
  cursor: pointer;
  display: none;
}

.subheader-section:hover .dashboard-edit {
  display: inline-block;
}

.share-icon {
  padding-right: 0px;
  color: rgba(0, 0, 0, 0.4);
}

.dashboardmobileicon.active {
  color: #00aece;
}

.dashboardmobileicon {
  margin-right: 40px;
  font-size: 16px;
  cursor: pointer;
  position: relative;
  top: 1px;
  display: none;
}

.subheader-section:hover .dashboardmobileicon {
  display: inline-block !important;
}

.mobiledashboard.active {
  color: #00aece;
}

.dashboard-picker {
  width: 288px !important;
  margin-left: 60px;
  margin-top: 50px;
  height: calc(100vh - 50px) !important;
  box-shadow: 3px 1px 5px rgba(0, 0, 0, 0.06);
}

.dashboard-chooser-container {
  height: calc(100vh - 200px) !important;
}

.new-dashboard-layout-page .hamburger-icon {
  padding: 5px !important;
  border: 1px solid #dae0e8 !important;
  border: 1px solid #dae0e8;
  border-radius: 2px;
  padding: 5px;
  position: relative;
  top: -4px;
}

.new-dashboard-layout-page .fc-separator-lg {
  position: relative;
  top: -4px;
}
</style>
<style lang="scss">
@media print {
  .pleft {
    padding: 0px !important;
  }
}
.new-dashboard-layout-page {
  .db-date-filter {
    right: 100px;
    position: absolute;
    bottom: 10px;
    top: 10px;
  }

  .more-dashboard-list {
    max-height: 240px;
    overflow: scroll;
    margin-top: 20px;
    margin-left: -50px;
    padding: 10px 0px 0px 0px;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  }

  .more-dashboard-name {
    padding: 8px 16px;
    font-size: 13px;
    letter-spacing: 0.7px;
    line-height: 16px;
    color: #25243e;
    cursor: pointer;
  }

  .more-dashboard-name:hover {
    background: hsla(0, 0%, 96.1%, 0.5);
  }

  .dashboard-link-name {
    padding: 0px 15px;
    cursor: pointer;
    border-right: 1px solid #e5e4e4;
  }

  .dashboard-link:last-of-type .dashboard-link-name {
    border-right: none;
  }

  .active-dashboard {
    .active-dashboard-link {
      font-weight: 500;
    }

    .active {
      border: 1px solid transparent;
      width: 25px;
      margin-top: 7px;
      margin-left: 15px;
      position: absolute;
      border-right: 0 solid #e0e0e0;
      border-left: 0 solid #e0e0e0;
      border-color: var(--fc-theme-color);
    }
  }
}
</style>
