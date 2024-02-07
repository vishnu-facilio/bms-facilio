<template>
  <div class="layout common-list-layout d-flex">
    <slot name="views-list">
      <ViewsList
        v-if="canShowViewsSidePanel && !hideViewSidebar"
        :canShowViewsSidePanel.sync="canShowViewsSidePanel"
        :groupViews="groupViews"
        :moduleName="moduleName"
        @onChange="goToView"
      ></ViewsList>
    </slot>
    <div class="list-content-container">
      <div
        :class="[
          'view-header-container subheader-section',
          hasQuery === true && 'filter-class',
        ]"
      >
        <slot name="views">
          <ViewHeader
            :moduleName="moduleName"
            :showRearrange="showViewRearrange"
            :showCurrentViewOnly="showSearch"
            :maxVisibleMenu="visibleViewCount"
            :showEditIcon="showViewEdit"
            :pathPrefix="pathPrefix"
            :retainFilters="filtersToRetain"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            @viewsLoaded="$emit('viewsLoaded')"
            @hideViewSidebar="val => (hideViewSidebar = val)"
          >
          </ViewHeader>
        </slot>
        <div class="fR fc-subheader-right">
          <slot name="header"></slot>
        </div>
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
import ViewHeader from 'newapp/components/ViewHeader'
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
      currentModuleName: this.moduleName,
      hideViewSidebar: false,
      isSidePanelOpen:
        JSON.parse(localStorage.getItem('fc-view-sidepanel')) || false,
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
        localStorage.setItem('fc-view-sidepanel', value)
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

        let { currentTab, retainFilters = [] } = this
        let { query } = this.$route

        let hasRetainableQuery = retainFilters.some(filterName =>
          query.hasOwnProperty(filterName)
        )

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
                query: hasRetainableQuery ? query : null,
              })
              .catch(error => console.warn('Could not switch view\n', error))
          }
        } else {
          let path = this.getPathForView(view, group)
          this.$router.push({ path, query }).catch(() => {})
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
.common-list-layout {
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
    height: 0;
    margin-right: 12px;
  }

  .view-header-container {
    flex-shrink: 0;
    border-bottom: 1px solid #f4f4f4;
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
      padding-top: 19px;
      padding-left: 15px;
      padding-bottom: 18px;
      padding-right: 8px;
      box-shadow: none;
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
}
</style>
