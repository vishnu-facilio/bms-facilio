<template>
  <div class="portal-dashboard-layout">
    <el-header class="fc-v1-tenant-overview-header overflow-x">
      <div class="portal">
        <div
          class="d-flex"
          style="height:100%"
          v-if="!$validation.isEmpty(activeDashboardList)"
        >
          <div
            @click="
              routeToDashboard(
                dashboard.linkName,
                null,
                false,
                index,
                dashboard
              )
            "
            v-for="(dashboard, index) in activeDashboardList"
            :key="index"
            class="fc-white-14 pR20 pL20 pointer"
          >
            {{ dashboard.dashboardName }}
            <div
              v-if="currentLinkName === dashboard.linkName"
              class="portal-dashboard-active"
            ></div>
          </div>
          <div class="d-flex flex-grow ">
            <div
              v-if="!$validation.isEmpty(moreDashboardList)"
              class="pL20 pR20 pointer"
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

            <el-popover
              placement="bottom-start"
              trigger="manual"
              :value="canShowDashboardLinks"
              popper-class="more-dashboard-list"
            >
              <div
                v-for="(dashboard, index) in moreDashboardList"
                :key="index"
                @click="
                  routeToDashboard(
                    dashboard.linkName,
                    null,
                    true,
                    index,
                    dashboard
                  )
                "
                class="more-dashboard-name"
              >
                {{ dashboard.dashboardName }}
              </div>
            </el-popover>
          </div>
          <div class="fR fc-subheader-right marginL-auto mR10 self-center">
            <portal-target
              name="dashboardFilter"
              class="dashboard-filter-portal d-flex"
            />
          </div>
          <div
            class="flex-middle  fc-new-dashboard-full-more mR20 mT2"
            style="margin-top: -10px;"
          >
            <div>
              <el-dropdown
                class="dashboard-dropdown-right pR10 pL10"
                trigger="click"
              >
                <span class="el-dropdown-link">
                  <i class="el-icon-more rotate-90 pointer f14 fc-grey6"></i>
                </span>
                <el-dropdown-menu
                  slot="dropdown"
                  class="dashboard-subheader-dp"
                >
                  <el-dropdown-item>
                    <div @click="isPdfDownload = true">
                      {{ $t('common._common.download') }}
                    </div>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
      </div>
    </el-header>

    <div v-if="loading" class="position-relative height100vh">
      <loader></loader>
    </div>

    <component :is="selectDashboard" v-else-if="currentLinkName" />

    <div v-else class="mT100 text-center d-flex flex-col">
      <inline-svg
        src="svgs/empty-dashboard"
        iconClass="text-center icon-xxxlg"
      ></inline-svg>
      <div class="fc-grayish pT20">No dashboard configured</div>
    </div>
    <pdf-download :url="pdfUrl" :isDownload.sync="isPdfDownload"></pdf-download>
  </div>
</template>
<script>
import { isArray, isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Loader from '@/Loader'
import dashboardLayoutHelper from 'src/pages/new-dashboard/components/dashboard/DashboardlayoutHelper.js'
import PdfDownload from 'src/components/PDFDownload'
import { getApp } from '@facilio/router'

export default {
  name: 'DashboardLayout',
  mixins: [dashboardLayoutHelper],
  props: ['dashboardlink'],
  components: {
    Loader,
    OldDashboardViewer: () => import('pages/dashboard/DashboardViewer'),
    NewDashboardViewer: () =>
      import(
        'src/pages/new-dashboard/components/dashboard/DashboardViewer.vue'
      ),
    PdfDownload,
  },
  data() {
    return {
      moreDashboardList: [],
      activeDashboardList: [],
      canShowDashboardLinks: false,
      loading: true,
      currentDashboard: null,
      dashboardList: [],
      isPdfDownload: false,
    }
  },
  async created() {
    await this.loadDashboardFolder()
    this.setHeaderList()
  },
  computed: {
    selectDashboard() {
      const isNewDashboardEnabled = this.$helpers.isLicenseEnabled(
        'NEW_DASHBOARD_FLOW'
      )
      return isNewDashboardEnabled ? 'NewDashboardViewer' : 'OldDashboardViewer'
    },
    currentLinkName() {
      return this.$getProperty(this.$route, `params.dashboardlink`, null)
    },
  },
  watch: {
    dashboardlink: {
      handler(value, oldValue) {
        if (value !== oldValue) this.loadDashboardFolder()
      },
    },
  },
  methods: {
    setHeaderList() {
      let { dashboardList } = this

      if (dashboardList.length > 3) {
        this.activeDashboardList = dashboardList.slice(0, 3)
        this.moreDashboardList = dashboardList.slice(3)
      } else {
        this.activeDashboardList = dashboardList
      }
      this.currentDashboard = this.activeDashboardList[0]
    },
    async loadDashboardFolder() {
      this.loading = true
      // let { data, error } = await API.get('/dashboardWithFolder')
      let params = { appId: getApp().id, onlyPublished: true }
      let { data, error } = await API.get('v3/dashboard/list', params)

      if (!error) {
        let { dashboardFolders = [] } = data || {}

        this.dashboardList = dashboardFolders.reduce((dashboards, folder) => {
          if (isArray(folder.dashboards) && !isEmpty(folder.dashboards)) {
            dashboards = [...dashboards, ...folder.dashboards]
          }
          return dashboards
        }, [])

        if (isEmpty(this.dashboardlink)) {
          let linkName = null
          let tabs = this.$getProperty(this.$route, `query.tab`)
          let tab = null
          let link = this.$getProperty(this.$route, `params.dashboardlink`)
          if (link == null) {
            linkName = this.$getProperty(this.dashboardList, `0.linkName`)
          } else {
            linkName = link
            if (tabs) {
              tab = tabs
            }
          }
          this.routeToDashboard(linkName, tab)
        }
      }

      this.loading = false
    },
    routeToDashboard(dashboardlink, tab, link, index, dashboard) {
      this.currentDashboard = dashboard ? dashboard : ''
      const printQuery = () => {
        if (this.$route?.query?.printing == 'true') {
          return {
            printing: 'true',
          }
        } else {
          return {}
        }
      }
      if (dashboardlink) {
        this.$router.push({
          params: {
            dashboardlink,
          },
          query: {
            ...printQuery(),
          },
        })
      }
      if (tab) {
        this.$router.push({
          query: {
            tab,
            ...printQuery(),
          },
        })
      }
      if (link) {
        let dashboardLink = this.moreDashboardList[index]
        this.moreDashboardList = [
          ...this.moreDashboardList.slice(0, index),
          ...this.moreDashboardList.slice(index + 1),
          this.activeDashboardList[2],
        ]
        this.activeDashboardList = [
          ...this.activeDashboardList.slice(0, 2),
          dashboardLink,
        ]
      }
      this.canShowDashboardLinks = false
    },
  },
}
</script>
<style lang="scss" scoped>
.fc-v1-tenant-overview-header {
  width: 100%;
  height: 60px;
  background-color: #3c3681;
  position: relative;
  box-sizing: border-box;
  padding: 24px 30px 22px 30px;
}
.fc-white-14 {
  line-height: normal;
  letter-spacing: 0.39px;
}
.portal-dashboard-active {
  width: 33px;
  height: 1px;
  border: solid 2px #32cbcb;
  position: absolute;
  bottom: 0;
}
.portal {
  height: 100%;
}
</style>
<style lang="scss">
.portal-dashboard-layout {
  .dashboard-filter-portal .dashboard-timeline-filter .el-button:hover {
    background: #ef508f50 !important;
  }
  .dashboard-filter-portal .dashboard-timeline-filter .cal-left-btn {
    color: white;
  }
  .dashboard-filter-portal .dashboard-timeline-filter .cal-right-btn {
    color: white;
  }
  .dashboard-filter-portal .dashboard-timeline-filter .button-row .el-button {
    color: white !important;
  }
  .dashboardviewer {
    padding-bottom: 70px;
  }
  .more-dashboard-list {
    max-height: 240px;
    overflow: scroll;
    margin-top: 20px;
    margin-left: -50px;
    padding: 10px 0px 0px 0px;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  }
  .el-popover {
    position: fixed !important;
  }
  .more-dashboard-name {
    padding: 8px 16px;
    font-size: 13px;
    letter-spacing: 0.7px;
    color: #25243e;
    cursor: pointer;
  }
  .more-dashboard-name:hover {
    background: hsla(0, 0%, 96.1%, 0.5);
  }
}
</style>
