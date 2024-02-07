<template>
  <el-dialog
    :visible.sync="showkBar"
    custom-class="f-kbar-container"
    :append-to-body="true"
    :show-close="false"
    :lock-scroll="true"
    ><div class="input-container">
      <el-input
        class="fc-no-border-input"
        placeholder="Search for tabs and modules"
        size="medium"
        v-model="searchText"
        @input="filterTabs"
        ref="kbar-search"
        role="input"
      ></el-input>
    </div>
    <div class="tabs-list-container">
      <div
        v-for="(tab, index) in tabsList"
        :key="tab.id"
        :class="[
          'tab-list-item',
          selectedTabIndex === index && 'selected-tab-item',
          !tab.isTab && 'group-container',
        ]"
        :ref="`kbar-tabs-list-${index}`"
        @click="redirectToSelectedTab(index)"
        :aria-labelledby="tab.name"
        role="li"
      >
        <div class="flex align-center">
          <InlineSvg
            :title="tab.name"
            :src="tab.isTab ? 'tabs' : getIconPath(tab.iconType)"
            iconClass="flex icon new-icon"
          />
          <div class="f14 pL10" v-if="tab.isTab">{{ tab.name }}</div>
          <div class="f14 pL10 bold" v-else>{{ tab.name }}</div>
        </div>
        <div class="flex align-center justify-center">
          <div class="f10 pR3">{{ $t('common._common.select_kbar') }}</div>
          <InlineSvg src="svgs/enterkey" iconClass="icon-xxs flex icon" />
        </div>
      </div>
    </div>
    <div class="bottom-helper-container">
      <div>
        <!-- eslint-disable-next-line vue/no-bare-strings-in-template -->
        <code class="key-icons">↑</code><code class="key-icons mL5">↓</code
        ><span class="pL5">{{ $t('common._common.to_navigate') }}</span>
        <code class="key-icons mL20">{{ $t('common._common.enter') }}</code>
        <span class="pL5">{{ $t('common._common.to_select') }}</span>
      </div>
      <div>
        <code class="key-icons">{{ $t('common._common.esc') }}</code>
        <span class="pL5">{{ $t('common._common.to_close') }}</span>
      </div>
    </div></el-dialog
  >
</template>

<script>
import { getApp, isWebTabsEnabled, findRouteForTab } from '@facilio/router'
import icons from 'newapp/webtab-icons.js'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'KBar',
  mounted() {
    if (isWebTabsEnabled()) {
      this.initEventHandlers()
      this.initTabs()
    }
  },
  data() {
    return {
      showkBar: false,
      selectedTabIndex: 0,
      searchText: '',
      tabsList: [],
      actualTabsList: [],
      canOpenKbar: false,
    }
  },
  watch: {
    selectedTabIndex(val) {
      this.$nextTick(() => {
        let { $refs } = this
        let [elemContainer] = $refs[`kbar-tabs-list-${val}`]
        if (!isEmpty(elemContainer)) {
          elemContainer.scrollIntoView({ behavior: 'smooth' })
        }
      })
    },
    showkBar(val) {
      if (val) {
        this.$nextTick(() => {
          let { $refs } = this
          let elem = $refs[`kbar-search`]
          if (!isEmpty(elem)) {
            elem.$refs.input.focus()
          }
        })
      }
    },
  },
  computed: {
    isSearchTextEmpty() {
      let { searchText } = this
      return isEmpty(searchText)
    },
  },
  methods: {
    initEventHandlers() {
      window.addEventListener('keydown', e => {
        let { userAgentData, platform } = navigator
        let { platform: userPlatform } = userAgentData || {}
        let currentOS = userPlatform || platform
        let isMac = currentOS.toUpperCase().indexOf('MAC') >= 0

        if (
          ((isMac && e.metaKey) || (!isMac && e.shiftKey)) &&
          ['k', 'K'].includes(e.key)
        ) {
          if (e.target.localName === 'body') {
            this.canOpenKbar = true
          }
        }

        if (this.showkBar && e.keyCode === 40) {
          let { selectedTabIndex, tabsList } = this
          if (selectedTabIndex !== tabsList.length - 1)
            this.selectedTabIndex = selectedTabIndex + 1
        }
        if (this.showkBar && e.keyCode === 38) {
          let { selectedTabIndex } = this
          if (selectedTabIndex !== 0)
            this.selectedTabIndex = selectedTabIndex - 1
        }
        if (this.showkBar && e.key === 'Enter') {
          let { selectedTabIndex } = this
          this.redirectToSelectedTab(selectedTabIndex)
        }
      })
      window.addEventListener('keyup', () => {
        if (this.canOpenKbar) {
          this.showkBar = true
          this.canOpenKbar = false
        }
      })
    },
    initTabs() {
      let app = getApp()
      let tabs = []
      let { webTabGroups } = app || {}

      webTabGroups.forEach(group => {
        let { webTabs, name: groupName } = group
        tabs.push(group)
        webTabs.forEach(tab => {
          tabs.push({ ...tab, isTab: true, groupName })
        })
      })
      this.tabsList = tabs || []
      this.actualTabsList = tabs || {}
    },
    getIconPath(type) {
      let iconType = icons[type] || {}
      return iconType.icon || icons[0].icon
    },
    redirectToSelectedTab(selectedTabIndex) {
      let { tabsList } = this
      let currIndex = selectedTabIndex
      let currTab = tabsList.find((_, index) => currIndex === index)
      if (isWebTabsEnabled() && !isEmpty(currTab)) {
        let name
        if (currTab.isTab) {
          let { name: currName } = findRouteForTab(currTab.id)
          name = currName
        } else {
          let { name: currName } = findRouteForTab(currTab.webTabs[0].id)
          name = currName
        }
        name && this.$router.push({ name })
        this.showkBar = false
        this.selectedTabIndex = 0
        this.searchText = ''
        this.initTabs()
      }
    },
    filterTabs(val) {
      if (!isEmpty(val)) {
        val = val.toLowerCase()
        this.selectedTabIndex = 0
        let { actualTabsList } = this
        this.tabsList = actualTabsList.filter(tab => {
          let { name, groupName = '' } = tab
          let lowerCaseName = name.toLowerCase()
          let lowerCaseGrpName = groupName.toLowerCase()
          return (
            name.includes(val) ||
            lowerCaseName.includes(val) ||
            groupName.includes(val) ||
            lowerCaseGrpName.includes(val)
          )
        })
      } else {
        this.initTabs()
      }
    },
  },
}
</script>

<style lang="scss">
.f-kbar-container {
  .el-dialog__header {
    padding: 0px;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .fc-no-border-input .el-input__inner {
    border: none;
  }
  .input-container {
    padding: 15px 25px;
    border-bottom: solid 1px #d8d8d861;
  }
  .tabs-list-container {
    padding: 3px 0px 15px;
    border-bottom: solid 1px #d8d8d861;
    height: 350px;
    overflow: scroll;
  }

  .tab-list-item {
    padding: 12px 25px;
    display: flex;
    justify-content: space-between;
    border-left: solid 3px transparent;
    cursor: pointer;
    margin: 5px 0px;
  }
  .selected-tab-item {
    background-color: #eef5f7;
    border-left: solid 3px #38b3c2;
  }
  .tab-list-item:hover {
    background-color: #eef5f7;
    border-left: solid 3px #eef5f7;
  }
  .group-container {
    border-top: solid 1px #f1f1f1;
  }
  .group-container:first-child {
    border-top: none;
  }
  .key-icons {
    background-color: transparent;
    border: solid 1px #39b2c2;
    border-radius: 4px;
    font-size: 11.05px;
    padding: 4px 7px;
    color: #39b2c2;
    min-width: 24px;
    height: 24px;
    text-align: center;
    align-items: center;
    justify-content: space-around;
    font-weight: 400;
    box-sizing: border-box;
  }
  .bottom-helper-container {
    padding: 15px 25px;
    display: flex;
    justify-content: space-between;
  }
}
</style>
