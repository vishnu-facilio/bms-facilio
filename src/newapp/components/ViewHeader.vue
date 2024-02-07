<template>
  <ul
    class="subheader-tabs pull-left"
    :class="{
      'has-more-views':
        !$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly,
    }"
  >
    <div class="flex-middle">
      <div class="fL fc-subheader-left d-flex">
        <inline-svg
          v-if="!canShowViewsSidePanel && currentView"
          src="svgs/hamburger-menu"
          class="d-flex pointer hamburger-icon self-center"
          iconClass="icon icon-sm"
          @click.native="openViewsSidePanel"
        ></inline-svg>
        <div class="pR15 pointer self-center">
          <el-dropdown
            v-if="groupViews && groupViews.length > 1"
            class="fc-dropdown-menu pL10"
            @command="openGroup"
            trigger="click"
          >
            <span class="el-dropdown-link">
              {{
                currentGroup
                  ? currentGroup.displayName || currentGroup.name
                  : ''
              }}
              <i
                v-if="!showCurrentViewOnly"
                class="el-icon-arrow-down el-icon--right"
              ></i>
            </span>
            <el-dropdown-menu
              slot="dropdown"
              v-if="groupViews && !showCurrentViewOnly"
            >
              <el-dropdown-item
                v-for="(group, key) in groupViews"
                :key="key"
                :command="group.name"
              >
                <template v-if="!$validation.isEmpty(group.views)">
                  {{ group.displayName || group.name }}
                </template>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div
          v-if="groupViews && groupViews.length > 1"
          class="fc-separator-lg mL10 mR10 self-center"
        ></div>
      </div>

      <li
        v-for="(item, index) in filteredViews.list"
        :key="item.name + index"
        class="self-center"
        :class="[currentView === item.name && 'active']"
        @click="goToView(item, currentGroup)"
      >
        <a>{{ item.label }}</a>
        <div v-if="!item.disable" class="sh-selection-bar"></div>
      </li>

      <template
        v-if="!$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly"
      >
        <el-dropdown
          trigger="click"
          class="mL20 pointer more-views-dropdown"
          @command="goToView"
        >
          <span class="el-dropdown-link more-icon-container">
            <i class="el-icon-more"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item, index) in filteredViews.more"
              :key="item.name + index"
              :command="item"
            >
              {{ item.label }}
            </el-dropdown-item>
            <el-dropdown-item
              v-if="showRearrange"
              class="rearrange-option"
              :command="{ name: 'rearrange-views' }"
              >{{ $t('setup.users_management.rearrange') }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </div>

    <ViewCustomization
      :visible.sync="showReorderPanel"
      :reload="true"
      :menu="currentViews"
      :moduleName="moduleName || ''"
      @onchange="loadViews()"
    ></ViewCustomization>
    <NewView :pathPrefix="pathPrefix"></NewView>
  </ul>
</template>
<script>
import ViewCustomization from '@/ViewCustomization'
import NewView from './NewView'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled, findRouteForTab, pageTypes } from '@facilio/router'

export default {
  props: [
    'moduleName',
    'pathPrefix',
    'showCurrentViewOnly',
    'maxVisibleMenu',
    'showRearrange',
    'retainFilters',
    'canShowViewsSidePanel',
  ],
  components: { ViewCustomization, NewView },
  created() {
    this.init()
  },
  data() {
    return {
      showReorderPanel: false,
    }
  },
  computed: {
    ...mapState({
      groupViews: state => {
        return !isEmpty(state.view.groupViews) ? state.view.groupViews : []
      },
    }),
    ...mapState('webtabs', {
      currentTabGroup: state => state.selectedTabGroup,
      currentTab: state => state.selectedTab,
    }),
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      } else {
        return null
      }
    },
    currentViews() {
      let { groupViews, currentView } = this
      let selectedViews = []

      groupViews.forEach(group => {
        let { views } = group
        let selectedView = (views || []).find(v => v.name === currentView)
        if (selectedView) {
          selectedViews = views
        }
      })

      return selectedViews.map(view => ({
        label: view.displayName,
        id: view.id,
        name: view.name,
        isCustom: !view.isDefault,
        primary: view.primary,
      }))
    },
    currentGroup() {
      let { groupViews } = this

      let selectedGroup = (groupViews || []).find(group => {
        let { views } = group
        let selectedView = (views || []).find(v => v.name === this.currentView)

        return !isEmpty(selectedView)
      })

      return selectedGroup
    },
    maxMenuLength() {
      return this.maxVisibleMenu || 3
    },
    filteredViews() {
      let list = []
      let more = null
      let {
        showCurrentViewOnly,
        currentViews,
        currentView,
        maxMenuLength: maxLength,
      } = this

      let currentViewPosition = currentViews.findIndex(
        v => v.name === currentView
      )

      if (showCurrentViewOnly) {
        list = [currentViews[currentViewPosition]]
      } else if (currentViews.length - 1 < maxLength) {
        list = currentViews
      } else if (currentViewPosition < maxLength) {
        list = currentViews.slice(0, maxLength)
        more = currentViews.slice(maxLength)
      } else {
        list = [
          ...currentViews.slice(0, maxLength - 1),
          currentViews[currentViewPosition],
        ]
        more = currentViews
          .slice(maxLength - 1)
          .filter(v => v.name !== currentView)
      }

      return {
        list,
        more,
      }
    },
  },
  watch: {
    moduleName(newVal, oldVal) {
      newVal !== oldVal && this.init()
    },
    currentView: {
      handler(newVal, oldVal) {
        if (!isEmpty(newVal, oldVal)) {
          this.loadViewDetails()
        } else if (newVal !== oldVal && isEmpty(newVal)) {
          this.initializeViews()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async init() {
      await this.$store.dispatch('view/clearViews')
      await this.loadViews()
    },

    async loadViews() {
      let { moduleName } = this
      await this.$store.dispatch('view/loadGroupViews', { moduleName })
      this.initializeViews()
    },

    loadViewDetails() {
      let { currentView, moduleName, currentGroup } = this
      let viewObj =
        this.$getProperty(currentGroup, 'views', []).find(
          v => v.name === currentView
        ) || {}
      let viewModuleName = this.$getProperty(viewObj, 'moduleName', null)

      if (isEmpty(currentView)) return
      if (isEmpty(moduleName) && isEmpty(viewModuleName)) return

      let param = {
        viewName: currentView,
        moduleName: isEmpty(viewModuleName) ? moduleName : viewModuleName,
      }
      this.$store.dispatch('view/loadViewDetail', param).catch(() => {})
    },

    initializeViews() {
      let { groupViews, currentView } = this
      let viewsList = (groupViews || []).reduce(
        (res, { views }) => [...res, ...(views || [])],
        []
      )
      let isCurrentViewNotAvailable = isEmpty(
        (viewsList || []).find(view => view.name === currentView)
      )
      if (isCurrentViewNotAvailable) {
        let firstGroup = groupViews && groupViews[0]
        let firstGroupWithViews = groupViews.find(
          group => !isEmpty(group.views)
        )
        let { name } = firstGroupWithViews || {}
        if (firstGroup && name) {
          this.$emit('hideViewSidebar', false)
          this.openGroup(name)
        } else {
          this.$emit('hideViewSidebar', true)
        }
      }
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
        let retainedQuery = retainFilters.reduce((filter, key) => {
          filter[key] = query[key]

          return filter
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
                query: retainedQuery,
              })
              .catch(error => console.warn('Could not switch view\n', error))
          }
        } else {
          let path = this.getPathForView(view, group)
          this.$router.push({ path, query: retainedQuery }).catch(() => {})
        }
      }
    },

    openGroup(command) {
      let view
      let group = (this.groupViews || []).find(g => g.name === command)

      if (!isEmpty(group)) {
        if (!isEmpty(group.views)) {
          view = group.views && group.views[0]
          this.goToView(view, group)
        }
      }
    },

    openViewsSidePanel() {
      this.$emit('update:canShowViewsSidePanel', true)
    },

    getPathForView(view) {
      let { pathPrefix } = this
      let prefix =
        pathPrefix[pathPrefix.length - 1] === '/'
          ? pathPrefix.slice(0, -1)
          : pathPrefix

      return `${prefix}/${view.name}`
    },
  },
}
</script>
<style lang="scss" scoped>
.el-icon-more {
  color: black;
  font-size: 16px;
  margin-top: 1px;
}

.more-icon-container {
  padding: 5px;
  padding-left: 6px;
  padding-right: 6px;
  border-radius: 20%;

  &:hover {
    background: rgb(202 212 216 / 30%);
  }
}

.subheader-tabs {
  list-style-type: none;
  margin-top: -3px;
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

  .wo-three-line {
    position: relative;
    top: 2px;
  }
  .fc-subheader-left {
    display: flex;
    flex-direction: row;
    position: relative;
  }
}
</style>
<style lang="scss">
.subheader-tabs {
  .fc-dropdown-menu {
    font-weight: 500;
    color: #2d2d52;
  }
  .hamburger-icon {
    border: 1px solid #dae0e8;
    border-radius: 2px;
    padding: 5px;
    .icon {
      fill: #615e88;
    }
  }
}

.el-dropdown-menu__item.rearrange-option {
  color: #fff;
  background: #39b2c2;
  text-align: center;
  font-weight: 600;
  margin-bottom: -10px;
  margin-top: 5px;

  &:hover {
    background: #33a6b5;
    color: #fff;
  }
}
</style>
