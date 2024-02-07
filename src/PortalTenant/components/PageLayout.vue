<template>
  <div class="portal-layout">
    <div class="portal-layout-header">
      <div class="header-details">
        <div class="tab-header">
          <div class="tab-name">
            {{ currentTab.name }}
          </div>
          <div class="header-section">
            <slot name="header"></slot>
          </div>
        </div>
        <div class="d-flex align-center justify-content-space mT10 height30">
          <div>
            <slot name="views">
              <ViewHeader
                :moduleName="moduleName"
                :canShowViewsSidePanel.sync="canShowViewsSidePanel"
                :groupViews="groupViews"
                :getRoute="getRoute"
                :retainFilters="retainFilters"
                @onChange="goToView"
              ></ViewHeader>
            </slot>
          </div>
          <div class="d-flex align-center">
            <slot name="header-2"></slot>
          </div>
        </div>
      </div>
    </div>
    <div class="portal-layout-container">
      <slot name="views-list">
        <ViewSideBar
          v-if="canShowViewsSidePanel"
          :canShowViewsSidePanel.sync="canShowViewsSidePanel"
          :groupViews="groupViews"
          :moduleName="moduleName"
          @onChange="goToView"
        ></ViewSideBar>
      </slot>
      <div class="module-list-container">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForTab, pageTypes } from '@facilio/router'
import ViewSideBar from './ViewSideBar'
import ViewHeader from 'src/PortalTenant/components/ViewHeaderWithoutGroups.vue'
import debounce from 'lodash/debounce'

const { LIST } = pageTypes

export default {
  props: ['moduleName'],
  components: { ViewSideBar, ViewHeader },
  data() {
    return {
      canShowViewsSidePanel: false,
    }
  },
  computed: {
    ...mapState('view', {
      groupViews: state => {
        return !isEmpty(state.groupViews) ? state.groupViews : []
      },
      metaInfo: state => state.metaInfo,
    }),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    currentView() {
      return this.$route.params.viewname
    },
    retainFilters() {
      return ['search', 'includeParentFilter']
    },
  },
  watch: {
    moduleName: {
      async handler(newVal, oldVal) {
        let { name } = this.metaInfo || {}

        if (![oldVal, name].includes(newVal) && !isEmpty(newVal)) {
          await this.$store.dispatch('view/clearViews')
          this.loadViews()
        }
      },
      immediate: true,
    },
    currentView: {
      handler(value) {
        // If current view is removed from URL, loadViews again and navigate to first group
        if (isEmpty(value)) this.loadViews()
        else this.loadViewDetails()
      },
      immediate: true,
    },
  },
  methods: {
    loadViews: debounce(async function() {
      return this.$store
        .dispatch('view/loadGroupViews', {
          moduleName: this.moduleName,
        })
        .then(this.initViews)
    }, 200),
    loadViewDetails: debounce(async function() {
      let { currentView: viewName, moduleName } = this
      return this.$store
        .dispatch('view/loadViewDetail', { viewName, moduleName })
        .catch(() => {})
    }, 200),
    initViews() {
      if (!this.currentView) {
        let { views } =
          this.groupViews.find(group => !isEmpty(group.views)) || {}

        if (!isEmpty(views)) {
          this.goToView(views[0])
        }
      }
    },
    goToView(view) {
      let route = this.getRoute(view)
      this.$router.replace(route)
    },
    getRoute(view) {
      if (isEmpty(view && view.name)) {
        return
      }

      let { query } = this.$route || {}
      let { id } = this.currentTab
      let { name = null } = findRouteForTab(id, { pageType: LIST }) || {}

      return name
        ? {
            name,
            params: { viewname: view.name },
            query,
          }
        : null
    },
  },
}
</script>
<style lang="scss">
.portal-layout {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: scroll;

  .portal-layout-header {
    background: #ffffff;
    position: relative;
    padding: 13px 25px;
    border-left: 1px solid #f5f6fa;
    flex-shrink: 0;

    .header-details {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .center-title {
        height: 70px;
        justify-content: center;
      }
      .tab-header {
        display: flex;
        justify-content: space-between;

        .tab-name {
          font-size: 24px;
          font-weight: 400;
          letter-spacing: 0.67px;
          color: #000;
          line-height: 40px;
        }
      }
      .header-section {
        display: flex;
        align-items: center;

        & > *:before {
          content: '|';
          margin: 0px 10px;
          color: #d8d8d8;
        }
        & > *:first-child:before,
        & > button:before,
        & > *:has(.custombtn-container):before,
        & > .el-dropdown:has(.create-btn):before {
          content: '';
          margin: 0px;
        }
        & > *:not(:has(.custombtn-container)) + button.create-btn {
          margin-left: 10px;
        }
        & > * {
          display: flex;
          align-items: center;
        }

        .custombtn-container {
          margin-right: 10px;

          &:has(.top-btn) {
            margin-left: 10px;
          }
          .top-btn {
            margin: 0px;
          }
        }
      }
    }
    .router-link-exact-active {
      .portal-active-tab {
        height: 2px;
        width: 25px;
        background: #ff3184;
        position: absolute;
        bottom: 0;
      }
    }
    .grp-view-name {
      font-weight: 500;
      color: #324056;
    }
  }
  .portal-layout-container {
    flex-grow: 1;
    overflow: hidden;
    display: flex;
    margin: 10px;
    justify-content: space-between;

    .module-list-container {
      flex-grow: 1;
      overflow: auto;
      display: flex;
      flex-direction: column;

      .el-table {
        th > .cell {
          padding-left: 0px;
          padding-right: 0px;
        }
        th,
        th.is-leaf,
        td {
          padding: 15px 20px;
        }
      }
      .portal-view-empty-state-container {
        align-items: center;
        background-color: #fff;
        display: flex;
        flex-direction: column;
        justify-content: center;
        flex-grow: 1;

        .module-view-empty-state svg.icon {
          width: 150px;
          height: auto;
        }
        .add-view-btn {
          background-color: #39b2c2;
          line-height: normal;
          padding: 11px 17px;
          border: solid 1px rgba(0, 0, 0, 0);
          margin-bottom: 30px;
        }
      }
    }
    .portal-table-header-actions {
      position: absolute;
      z-index: 1343;
      left: 70px;
      background: #fff;
      width: calc(100% - 70px);
      height: 54px;
      display: flex;
      align-items: center;

      .portal-bulk-action-delete {
        margin-left: 10px;
        letter-spacing: 0.3px;
        text-transform: uppercase;
        font-weight: 500;
        background-color: #fff;
        padding: 5px 10px;
        color: #324056;
        border: 1px solid #dadfe3;

        &:hover {
          background-color: #f3f5f7;
        }
      }
    }
  }

  .list-loading,
  .list-empty-state {
    display: flex;
    flex-grow: 1;
    background-color: #fff;
  }
  .list-empty-state {
    justify-content: center;
    align-items: center;
    flex-direction: column;
  }
  .tags-container {
    margin: 0px;
  }
}
</style>
