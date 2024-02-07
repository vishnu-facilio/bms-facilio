<template>
  <ul
    class="product-menu uppercase"
    v-if="!$validation.isEmpty(tabsList.length)"
  >
    <router-link
      tag="li"
      v-for="tab in activeTabsList"
      :key="`tab-${tab.id}`"
      :to="tab.route"
      :class="[isTabActive(tab.id) && 'active']"
    >
      <a :id="`navbar-${tab.id}`" @click="removeFocus">{{ tab.label }}</a>
      <div class="sh-selection-bar"></div>
    </router-link>
    <li v-if="canShowMoreMenu()" class="mL10">
      <el-dropdown
        @command="handleCommand"
        placement="bottom-start"
        trigger="click"
      >
        <span class="el-dropdown-link">
          <inline-svg src="svgs/home/topbar-more"></inline-svg>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-input
            v-model="searchText"
            class="modules-header-input"
          ></el-input>
          <el-dropdown-item
            v-for="tab in filteredMoreTabList"
            :key="`tab-${tab.id}`"
            :command="tab"
          >
            <span>{{ tab.label }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </li>
  </ul>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'
import sortBy from 'lodash/sortBy'
import { isWebTabsEnabled, findRouteForTab } from '@facilio/router'

export default {
  data() {
    return {
      maxActiveTabCount: 6,
      searchText: '',
    }
  },
  created() {
    document.addEventListener('keydown', this.keyDownHandler)
  },

  beforeDestroy() {
    document.removeEventListener('keydown', this.keyDownHandler)
  },
  computed: {
    ...mapState('webtabs', ['selectedTabGroup', 'selectedTab']),
    ...mapGetters('webtabs', ['getTabs']),

    tabsList() {
      let { id: groupId, route: routePath } = this.selectedTabGroup || {}
      if (!isWebTabsEnabled() || isEmpty(groupId) || routePath === 'setup')
        return []

      let tabs = this.getTabs(groupId)

      return sortBy(tabs, ['order']).map(({ id, name }) => {
        return {
          id,
          label: name,
          route: findRouteForTab(id),
        }
      })
    },
    activeTabsList() {
      let { tabsList, selectedTab, maxActiveTabCount } = this
      let activeTabs = tabsList.slice(0, maxActiveTabCount)

      if (!isEmpty(selectedTab)) {
        let isCurrentTabInActive = activeTabs.some(t => t.id === selectedTab.id)

        if (!isCurrentTabInActive) {
          let nonSelectedActiveTabs = activeTabs.slice(0, activeTabs.length - 1)
          let selectedTabObj = tabsList.find(t => t.id === selectedTab.id)

          if (selectedTabObj) {
            activeTabs = [...nonSelectedActiveTabs, selectedTabObj]
          } else {
            activeTabs = [...nonSelectedActiveTabs]
          }
        }
      }
      return activeTabs
    },
    filteredMoreTabList() {
      let { tabsList, activeTabsList, searchText } = this
      let activeTabIds = activeTabsList.map(t => t.id)
      let moreTabsList = tabsList.filter(t => !activeTabIds.includes(t.id))

      let filteredMoreTabList = moreTabsList

      if (!isEmpty(searchText)) {
        filteredMoreTabList = moreTabsList.filter(tab => {
          let tabLabel = ''
          if (!isEmpty((tab || {}).label)) {
            tabLabel = tab.label.replace(/\s/g, '').toLowerCase()
          }
          return tabLabel.includes(searchText)
        })
      }

      return filteredMoreTabList
    },
  },
  methods: {
    handleCommand(selectedTab) {
      if (!isEmpty(selectedTab)) {
        this.$router.push(selectedTab.route)
      }
    },
    canShowMoreMenu() {
      let { filteredMoreTabList, searchText } = this
      let returnVal = filteredMoreTabList.length > 0 || !isEmpty(searchText)
      return returnVal
    },
    isTabActive(tabId) {
      let { selectedTab } = this
      return selectedTab && selectedTab.id === tabId
    },
    removeFocus() {
      document.activeElement.blur()
    },
    keyDownHandler(e) {
      if (e.shiftKey && e.key === 'T') {
        let { id: selectedTabId } = this.selectedTab || {}
        if (e.target.localName === 'body') {
          document.getElementById(`navbar-${selectedTabId}`).focus()
        }
      }
    },
  },
}
</script>
<style lang="scss">
.modules-header-input.el-input {
  padding: 0px 20px;
  border-bottom: 1px solid #d8dce5;

  .el-input__inner {
    border-bottom: 0px;
  }
}
</style>
