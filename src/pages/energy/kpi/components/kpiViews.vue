<template>
  <ul class="subheader-tabs pull-left pB10">
    <router-link
      tag="li"
      v-for="item in views"
      :key="item.name"
      :to="item.path"
      active-class="active"
    >
      <a @click="() => pageTypeChange(item.name)">{{ item.label }}</a>
      <div class="sh-selection-bar"></div>
    </router-link>
  </ul>
</template>
<script>
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
export default {
  methods: {
    pageTypeChange(name) {
      if (name === 'moduleKpi') {
        eventBus.$emit('oldkpi')
      }
    },
    getroutePath(pageType) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageType) || {}
        if (name) {
          return { name }
        }
      } else {
        let pagetypeVsPath = {
          [pageTypes.MODULE_KPI]: '/app/em/kpi/module/list',
          [pageTypes.READING_KPI_LIST]: '/app/em/kpi/reading/list',
          [pageTypes.READING_KPI_TEMPLATE]: '/app/em/kpi/reading/templates',
        }
        return pagetypeVsPath[pageType]
      }
    },
  },
  computed: {
    views() {
      return [
        {
          name: 'moduleKpi',
          label: this.$t('common._common.module_kpi'),
          path: this.getroutePath(pageTypes.MODULE_KPI),
        },
        {
          name: 'kpiViewer',
          label: this.$t('common._common.reading_kpi'),
          path: this.getroutePath(pageTypes.READING_KPI_LIST),
        },
        {
          name: 'kpiList',
          label: this.$t('common._common.reading_kpi_templates'),
          path: this.getroutePath(pageTypes.READING_KPI_TEMPLATE),
        },
      ]
    },
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
