<template>
  <div class="layout cm-common-list-layout d-flex">
    <slot name="views-list">
      <ViewsList
        v-if="canShowViewsSidePanel"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
        :groupViews="groupViews"
        :moduleName="moduleName"
        :hideArrowIcon="true"
        @onChange="goToView"
      ></ViewsList>
    </slot>
    <div class="list-content-container">
      <div
        :class="[
          'view-header-container subheader-section d-flex justify-content-space',
          hasQuery === true && 'filter-class',
        ]"
      >
        <slot name="views">
          <ViewHeader
            :moduleName="moduleName"
            :showRearrange="showViewRearrange"
            :showEditIcon="showViewEdit"
            :pathPrefix="pathPrefix"
            :retainFilters="filtersToRetain"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            @viewsLoaded="$emit('viewsLoaded')"
            @hideViewSidebar="val => (hideViewSidebar = val)"
          >
          </ViewHeader>
        </slot>
        <div class="header-right">
          <slot name="header"></slot>
        </div>
      </div>
      <slot name="sub-header">
        <div
          v-if="!hideSubHeader"
          class="view-sub-header-container  d-flex  justify-content-space align-center"
        >
          <slot name="sub-header-content">
            <div class="cm-sub-header-txt">
              <span
                v-if="isEmpty(recordCount)"
                class="count-shimmer-line loading-shimmer"
              ></span>
              <span v-else class="bold pR5">{{ recordCount }}</span>
              <span v-if="recordCount > 1">{{
                $t('common._common.tot_records')
              }}</span>
              <span v-if="recordCount === 1">{{
                $t('common._common.tot_record')
              }}</span>
            </div>
          </slot>
          <div class="d-flex align-center">
            <slot name="sub-header-actions"></slot>
          </div>
        </div>
      </slot>
      <div class="select-all-container">
        <slot name="sub-header-selectedall"> </slot>
      </div>
      <div class="height100 common-content-container">
        <slot name="content"></slot>
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty, isNull } from '@facilio/utils/validation'
import ViewHeader from 'newapp/components/ViewHeaderWithoutGroups'
import ViewsList from 'pages/views/ViewsSidePanel'
import {
  isWebTabsEnabled,
  findRouteForTab,
  pageTypes,
  findRouteForModule,
} from '@facilio/router'

export default {
  props: [
    'moduleName',
    'getPageTitle',
    'showViewRearrange',
    'visibleViewCount',
    'showViewEdit',
    'pathPrefix',
    'retainFilters',
    'hideSubHeader',
    'recordCount',
    'recordLoading',
  ],
  components: {
    ViewHeader,
    ViewsList,
  },
  created() {
    this.$store.dispatch('view/clearViews').catch(() => {})
    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
    }
  },
  beforeDestroy() {
    if (isWebTabsEnabled()) {
      document.removeEventListener('keydown', this.keyDownHandler)
    }
  },
  data() {
    return {
      isEmpty,
      currentModuleName: this.moduleName,
      hideViewSidebar: false,
      isSidePanelOpen: false,
    }
  },
  title() {
    let title = this.getPageTitle()
    return !isEmpty(title) ? title : ''
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
      viewDetail: state => state.view.currentViewDetail,
      showSearch: state => state.search.active,
      currentTab: state => state.webtabs.selectedTab,
      groupViews: state => {
        return !isEmpty(state.view.groupViews) ? state.view.groupViews : []
      },
    }),

    currentView() {
      let { $route } = this

      if ($route.params.viewname) {
        return $route.params.viewname
      } else {
        return null
      }
    },
    filtersToRetain() {
      let { retainFilters } = this
      if (isNull(retainFilters)) {
        return []
      } else if (!isEmpty(retainFilters)) {
        return retainFilters
      } else {
        return ['search', 'includeParentFilter']
      }
    },
    hasQuery() {
      let { query } = this.$route

      if (query.filters === '') {
        return true
      } else if (query.filters) {
        return true
      } else {
        return false
      }
    },
    canShowViewsSidePanel: {
      get() {
        let { isSidePanelOpen, $slots } = this
        let isViewHeaderOverriden = !isEmpty($slots.views)

        return isSidePanelOpen && !isViewHeaderOverriden
      },
      set(value) {
        this.isSidePanelOpen = value
      },
    },
  },
  watch: {
    moduleName(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.setPageTitle()
      }
    },
    $route(newVal, oldVal) {
      if (newVal.params.viewname !== oldVal.params.viewname) {
        this.setPageTitle()
      }
    },
    metaInfo(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.setPageTitle()
      }
    },
    currentView: {
      async handler(newVal, oldVal) {
        if (newVal && oldVal !== newVal) {
          await this.loadViewDetails()
          this.getMetaInfo()
          this.setPageTitle()
        }
      },
      immediate: true,
    },
  },
  methods: {
    reloadList() {
      let routeParams = this.$getProperty(this, '$route.params')
      this.$router
        .push({ params: { ...routeParams, viewname: '' } })
        .catch(() => {})
      this.$router
        .push({
          params: { ...routeParams, viewname: routeParams.viewname },
        })
        .catch(() => {})
    },
    getMetaInfo() {
      this.$store
        .dispatch('view/loadModuleMeta', this.moduleName)
        .catch(() => {})
    },
    goToView(view, group = null) {
      if (!isEmpty(view)) {
        let { name: viewname } = view

        if (viewname === 'rearrange-views') {
          this.showReorderPanel = true
          return
        }

        let { currentTab, filtersToRetain = [] } = this
        let { query } = this.$route

        let filteredQuery = Object.keys(query || {}).reduce((obj, key) => {
          if (filtersToRetain.includes(key)) {
            obj[key] = query[key]
          }
          return obj
        }, {})

        if (isWebTabsEnabled()) {
          let route =
            findRouteForTab(currentTab.id, {
              pageType: pageTypes.LIST,
            }) || {}

          if (route) {
            this.$router
              .push({
                ...route,
                params: { viewname },
                query: filteredQuery,
              })
              .catch(error => console.warn('Could not switch view\n', error))
          }
        } else {
          let path = this.getPathForView(view, group)
          this.$router.push({ path, query: filteredQuery }).catch(() => {})
        }
      }
    },
    getPathForView(view) {
      let { pathPrefix } = this
      let prefix =
        pathPrefix[pathPrefix.length - 1] === '/'
          ? pathPrefix.slice(0, -1)
          : pathPrefix

      return `${prefix}/${view.name}`
    },
    loadViewDetails() {
      this.$store
        .dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.moduleName,
        })
        .catch(() => {})
    },
    setPageTitle() {
      let { getPageTitle, setTitle, currentView } = this
      let title = getPageTitle(currentView)

      if (!isEmpty(title)) setTitle(title)
    },
    keyDownHandler(e) {
      if (e.shiftKey && e.key === 'N') {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(this.moduleName, pageTypes.CREATE) || {}
          if (e.target.localName === 'body') {
            name && this.$router.push({ name }).catch(() => {})
          }
        }
      }
    },
  },
}
</script>

<style lang="scss">
.cm-common-list-layout {
  .list-content-container {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
  }

  .common-content-container {
    display: flex;
    flex-direction: column;
    .list-empty-state {
      background: #fff;
      flex-grow: 1;
    }
  }

  .fc-subheader-left {
    display: flex;
    flex-direction: row;
    position: relative;
  }

  .fc-subheader-right {
    display: flex;
    flex-direction: row;
    margin-right: 12px;
  }
  .header-right {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .view-header-container {
    flex-shrink: 0;
    border-bottom: 1px solid #f4f4f4;
    margin-bottom: 8px;
    &.filter-class {
      box-shadow: none !important;
      border-bottom: 1px solid #ededed;
    }
    &.subheader-section {
      height: 56px;
      position: relative;
      font-size: 14px;
      z-index: 100;
      box-sizing: border-box;
      padding: 0px 10px;
      box-shadow: none;
      align-items: center;
    }
    .shadow-12 {
      box-shadow: -4px 4px 8px rgba(250, 75, 146, 0.2);
    }
    .sh-button-add i.q-icon.material-icons:hover {
      font-weight: 900;
    }
    .subheader-section .disable:disabled {
      opacity: 0.7;
    }
    .sh-subheader-count .el-badge__content {
      line-height: 15px !important;
    }

    .subheader-tab {
      background: #fff;
      height: 2px;
    }
    .subheader-tab.active {
      background: #ef508f;
      margin-top: 8px;
      width: 20px;
    }
  }
  .view-sub-header-container {
    background: #fff;
    margin: 0px 10px;
    padding: 0px 16px;
    border-radius: 4px 4px 0px 0px;
    border-bottom: 1px solid #f1f2f4;
    min-height: 40px;

    .cm-sub-header-txt {
      margin: auto 0px;
    }

    .count-shimmer-line {
      height: 12px;
      width: 50px;
      margin-right: 4px;
    }
  }
  .select-all-container {
    display: inline-flex;
    margin-left: 12px;
    height: 50px;
    flex-direction: column;
  }
}
</style>
