<template>
  <div class="layout">
    <div
      v-if="loading"
      class="flex-middle flex-direction-row justify-between"
      style="height: 70px;"
    >
      <div class="">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="fc-animated-background width100px height20"></div>
          </el-col>
          <el-col :span="6">
            <div class="fc-animated-background width100px height20"></div>
          </el-col>
          <el-col :span="6">
            <div class="fc-animated-background width100px height20"></div>
          </el-col>
          <el-col :span="6">
            <div class="fc-animated-background width100px height20"></div>
          </el-col>
        </el-row>
      </div>
      <div class="">
        <el-col :span="6">
          <div class="fc-animated-background width100px height35"></div>
        </el-col>
        <el-col :span="6">
          <div class="fc-animated-background width100px height35"></div>
        </el-col>
      </div>
    </div>
    <subheader
      v-else
      :menu="subheaderMenu"
      :maxVisibleMenu="5"
      parent="/app/em/analytics"
      :positionsheader="'pull-left'"
      class="width100 new-analytics-subheader"
    >
    </subheader>
    <div>
      <div class="newanalytics">
        <router-view class="col-12"></router-view>
      </div>
    </div>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      subheaderMenu: [],
      loading: true,
      buildings: [],
    }
  },
  created() {
    Promise.all([this.getBuildingList()]).then(() => this.init())
  },
  methods: {
    async getBuildingList() {
      let { data, error } = await API.get('/v3/picklist/building')
      if (!error) {
        this.buildings = data.pickList
      }
    },
    init() {
      this.loading = true
      let menu = []
      if (this.$org.id === 168) {
        menu = [
          {
            label: this.$t('common.header.portfolio'),
            path: { path: '/app/em/analytics/newportfolio' },
          },
          {
            label: this.$t('space.sites._site_type'),
            path: { path: '/app/em/analytics/site' },
          },
        ]
      } else {
        if (this.buildings && Object.keys(this.buildings).length > 1) {
          if (isWebTabsEnabled()) {
            menu = [
              {
                label: this.$t('common.header.portfolio'),
                path: { path: this.getroutePath(pageTypes.ANALYTIC_PORTFOLIO) },
              },
            ]
          } else {
            menu = [
              {
                label: this.$t('common.header.portfolio'),
                path: { path: '/app/em/analytics/portfolio' },
              },
            ]
          }
        }
      }
      if (isWebTabsEnabled()) {
        menu.push(
          {
            label: this.$t('alarm.alarm.building'),
            path: { path: this.getroutePath(pageTypes.ANALYTIC_BUILDING) },
          },
          {
            label: this.$t('home.reports.heatmap'),
            path: { path: this.getroutePath(pageTypes.HEAT_MAP) },
          },
          {
            label: 'Treemap',
            path: { path: this.getroutePath(pageTypes.TREE_MAP) },
          },
          {
            label: 'Regression',
            path: { path: this.getroutePath(pageTypes.REGRESSION) },
          }
          // {
          //   label: 'WorkPlace TreeMap',
          //   path: { path: this.getroutePath(pageTypes.WORKPLACE_TREEMAP) },
          // }
        )
      } else {
        menu.push(
          {
            label: this.$t('alarm.alarm.building'),
            path: { path: '/app/em/analytics/building' },
          },
          {
            label: this.$t('home.reports.heatmap'),
            path: { path: '/app/em/analytics/heatmap' },
          },
          {
            label: 'Treemap',
            path: { path: '/app/em/analytics/treemap' },
          },
          // {
          //   label: 'WorkPlace TreeMap',
          //   path: { path: '/app/em/analytics/workplacetreemap' },
          // },
          {
            label: 'Regression',
            path: { path: '/app/em/analytics/regression' },
          },
          {
            label: 'Scatter',
            path: { path: '/app/em/analytics/scatter' },
          }
          // uncomment when pivot ready to release
          // {
          //   label: 'Pivot',
          //   path: { path: '/app/em/analytics/pivot' },
          // }
        )
      }
      this.subheaderMenu = menu
      if (this.subheaderMenu !== []) {
        this.loading = false
      }
    },
    getroutePath(pageType) {
      let { name } = findRouteForTab(pageType) || {}
      let path

      if (name) {
        path = this.$router.resolve({ name }).href
      }

      return path
    },
  },
  components: {
    Subheader,
  },
}
</script>
<style>
.new-analytics-subheader {
  border: solid 1px #e0e0e0;
  border-left: 1px solid #e6e6e6;
  border-top: none;
  box-shadow: 0 1px 4px 0 #f2f2f2 !important;
  border-bottom: none;
  height: 70px !important;
  justify-content: center;
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid #f0f4f4;
}
.new-analytics-subheader .subheader-tabs li:first-child {
  padding-left: 0;
}
@media print {
  .layout-header,
  .subheader-section,
  .fc-layout-aside,
  .newanalytics-sidebar,
  .newanalytics-page-header,
  .chart-icon {
    display: none !important;
  }

  .height100.pL50.pL60,
  .layout-page-container.transition-generic {
    padding: 0 !important;
  }

  .normal main.layout-page {
    height: 100% !important;
    min-height: 100% !important;
    display: block;
  }

  .analytics-section-new {
    height: 100% !important;
    display: block;
  }

  .analytic-summary.new-analytic-summary {
    width: 100%;
    max-width: 100% !important;
    min-width: 100%;
  }

  body {
    -webkit-print-color-adjust: exact;
    color-adjust: exact;
  }
  .chart-tooltip {
    opacity: 0 !important;
  }
}
</style>
