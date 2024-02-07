<template>
  <div v-if="loading">
    <loader></loader>
  </div>
  <div v-else-if="currentDashboard">
    <!-- TODO Replace this with subheader once multiple dashboards are required -->
    <div class="view-header-container subheader-section d-flex">
      <ul class="subheader-tabs pull-left pB10">
        <li class="active">
          <a>{{ currentDashboard.displayName }}</a>
          <div class="sh-selection-bar"></div>
        </li>
      </ul>
      <div class="fR fc-subheader-right marginL-auto mR10 self-center">
        <portal-target
          name="dashboardFilter"
          class="dashboard-filter-portal d-flex"
        />
        <div
          class="flex-middle  fc-new-dashboard-full-more mR20 mT2"
          style="display:none"
        >
          <div>
            <el-dropdown
              class="dashboard-dropdown-right pR10 pL10"
              trigger="click"
            >
              <span class="el-dropdown-link">
                <i class="el-icon-more rotate-90 pointer f14 fc-grey6"></i>
              </span>
              <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
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

    <component
      :is="selectDashboard"
      :currentDashboard="currentDashboard"
      class="operations-dashboard"
    ></component>
    <pdf-download :url="pdfUrl" :isDownload.sync="isPdfDownload"></pdf-download>
  </div>
  <div v-else class="mT100 text-center d-flex flex-col">
    <inline-svg
      src="svgs/empty-dashboard"
      iconClass="text-center icon-xxxlg"
    ></inline-svg>
    <div class="fc-grayish pT20">No dashboard configured</div>
  </div>
</template>
<script>
import Loader from '@/Loader'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import dashboardLayoutHelper from 'src/pages/new-dashboard/components/dashboard/DashboardlayoutHelper.js'
import PdfDownload from 'src/components/PDFDownload'
export default {
  mixins: [dashboardLayoutHelper],
  components: {
    Loader,
    OldDashboardViewer: () => import('pages/dashboard/DashboardViewer'),
    NewDashboardViewer: () =>
      import(
        'src/pages/new-dashboard/components/dashboard/DashboardViewer.vue'
      ),
    PdfDownload,
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
    this.$store.dispatch('loadBuildings')
  },
  data() {
    return {
      dashboardList: [],
      currentDashboard: null,
      loading: true,
      isPdfDownload: false,
    }
  },
  computed: {
    selectDashboard() {
      const isNewDashboardEnabled = this.$helpers.isLicenseEnabled(
        'NEW_DASHBOARD_FLOW'
      )
      return isNewDashboardEnabled ? 'NewDashboardViewer' : 'OldDashboardViewer'
    },
    ...mapState('webtabs', ['selectedTab']),
    currentDashboardLinkName() {
      let { selectedTab, $getProperty } = this
      return $getProperty(selectedTab, 'configJSON.dashboardLink') || null
    },
  },
  watch: {
    currentDashboardLinkName: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          this.loadDashboard()
        } else if (isEmpty(newVal)) {
          this.loading = false
          this.$nextTick(() => (this.currentDashboard = null))
        }
      },
      immediate: true,
    },
  },
  methods: {
    async loadDashboard() {
      let { currentDashboardLinkName } = this

      this.loading = true

      let { data, error } = await API.get(
        `/dashboard/${currentDashboardLinkName}`
      )

      if (!error) {
        let { dashboardJson } = data
        await this.setDashboard(dashboardJson[0])
      } else {
        this.currentDashboard = null
      }
      this.loading = false
    },
    setDashboard(dashboard) {
      if (isEmpty(dashboard) || isEmpty(dashboard.linkName)) {
        this.currentDashboard = null
        return Promise.resolve()
      }

      this.currentDashboard = {
        displayName: dashboard.label,
        dashboardName: dashboard.label,
        path: this.$router.currentRoute.path + dashboard.linkName,
        moduleName: dashboard.moduleName,
        baseSpaceId: dashboard.baseSpaceId,
        dashboard: dashboard,
        linkName: dashboard.linkName,
        screenSetting: { titleVisibility: false },
      }

      let { params = {} } = this.$route

      if (isEmpty(params.dashboardlink)) {
        return this.$router
          .push({
            params: {
              dashboardlink: dashboard.linkName,
            },
          })
          .catch(() => {})
      } else {
        return Promise.resolve()
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.operations-dashboard {
  width: 100%;
}
.view-header-container.subheader-section {
  height: 56px;
  position: relative;
  font-size: 14px;
  z-index: 100;
  box-sizing: border-box;
  padding-top: 19px;
  padding-left: 15px;
  padding-bottom: 18px;
  padding-right: 8px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.06);

  .fc-subheader-right {
    display: flex;
    flex-direction: row;
  }
  .subheader-tabs {
    list-style-type: none;
    margin: 0;
    padding: 0;
    overflow: hidden;
    background-color: transparent;

    li {
      float: left;
      padding: 0px 15px;
    }
    li.active a {
      font-weight: 500;
    }
    li.active .sh-selection-bar {
      border-right: 0px solid #e0e0e0;
      border-left: 0px solid #e0e0e0;
      border-color: #ff3184;
    }
    li .sh-selection-bar {
      border: 1px solid transparent;
      width: 25px;
      margin-top: 7px;
      position: absolute;
    }
    &:not(.has-more-views) li:last-of-type {
      border-right: 0px;
    }
  }
}
</style>
