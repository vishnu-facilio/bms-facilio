// new kpi views
<template>
  <ul class="subheader-tabs pull-left pB10">
    <router-link
      tag="li"
      v-for="item in views"
      :key="item.name"
      :to="item.path"
      active-class="active"
    >
      <a>{{ item.label }}</a>
      <div class="sh-selection-bar"></div>
    </router-link>
  </ul>
</template>
<script>
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  methods: {
    getroutePath(pageType, isLive) {
      if (isWebTabsEnabled()) {
        if (pageType === 'MODULE_KPI') {
          pageType = 'NEW_MODULE_KPI'
        }
        let { name } = findRouteForTab(pageType) || {}
        if (name) {
          return { name, params: { kpiType: isLive ? 'live' : 'scheduled' } }
        }
      } else {
        let pagetypeVsPath = {
          [pageTypes.NEW_READING_KPI_LIST]: `/app/em/readingKpi/list/${
            isLive ? 'live' : 'scheduled'
          }`,
          [pageTypes.MODULE_KPI]: '/app/em/readingKpi/module/list',
        }
        return pagetypeVsPath[pageType]
      }
    },
  },
  data() {
    return {
      views: [
        {
          name: 'moduleKpi',
          label: this.$t('common._common.module_kpi'),
          path: this.getroutePath(pageTypes.MODULE_KPI),
        },
        {
          name: 'liveReadingKpilist',
          label: this.$t('kpi.kpi.live_kpi'),
          path: this.getroutePath(pageTypes.NEW_READING_KPI_LIST, true),
        },
        {
          name: 'scheduledReadingKpilist',
          label: this.$t('kpi.kpi.scheduled_kpi'),
          path: this.getroutePath(pageTypes.NEW_READING_KPI_LIST, false),
        },
      ],
    }
  },
}
</script>
<style lang="scss" scoped>
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
    border-color: var(--fc-theme-color);
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
</style>
