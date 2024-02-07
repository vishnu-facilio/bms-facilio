<template>
  <div>
    <div
      class="dashboard-top-bar pointer inline-flex self-center "
      v-if="!loading"
    >
      <div class="inline-flex">
        <div v-for="(tab, index) in mainTabs" :key="index">
          <div
            v-if="tab.childTabs && tab.childTabs.length"
            v-popover="`popover-${tab.id}`"
            class="db-tab-cell"
            @click="mainTabChange(tab)"
            v-bind:class="{ active: getTabActiveClass(tab) }"
          >
            {{ getTabName(tab) }}
            <i class="el-icon-arrow-down pL15"></i>
          </div>
          <div
            v-else
            class="db-tab-cell"
            @click="mainTabChange(tab)"
            v-bind:class="{ active: tab.id === dashboardTabId }"
          >
            {{ tab.name }}
          </div>
          <el-popover
            :ref="`popover-${tab.id}`"
            placement="bottom"
            trigger="hover"
            :visible-arrow="false"
            :after-enter="childTabChange"
            popper-class="inventory-list-popover-1 fc-dashboard-tab-popup"
          >
            <div
              class="db-childtab-cell ellipsis"
              v-bind:class="{ active: tab.id === dashboardTabId }"
              @click="closepopver(tab.id), mainTabChange(tab)"
            >
              {{ tab.name }}
              <div class="pull-right" v-if="tab.id === dashboardTabId">
                <i class="el-icon-check" style="color: green;"></i>
              </div>
            </div>
            <template v-for="(childTab, cId) in tab.childTabs">
              <div
                @click="closepopver(tab.id), mainTabChange(childTab)"
                class="db-childtab-cell ellipsis"
                :key="cId"
                v-bind:class="{ active: childTab.id === dashboardTabId }"
              >
                {{ childTab.name }}
                <div class="pull-right" v-if="childTab.id === dashboardTabId">
                  <i class="el-icon-check" style="color: green;"></i>
                </div>
              </div>
            </template>
          </el-popover>
        </div>
      </div>
      <el-popover
        :ref="`more-popover`"
        placement="bottom"
        trigger="hover"
        :visible-arrow="false"
        popper-class="inventory-list-popover-1 fc-dashboard-more-tab-popup"
      >
        <el-cascader-panel
          v-model="moreTabSelectedDashboard"
          @change="moreTabChange"
          @expand-change="expandChange"
          :options="moreTabsCascadeData"
        ></el-cascader-panel>
      </el-popover>
      <div class="self-center " v-popover="`more-popover`">
        <i class="el-icon-more p15 border-right" v-if="moreTabs.length"></i>
      </div>
      <div
        v-if="edit"
        v-tippy="{
          placement: 'top',
          animation: 'shift-away',
          arrow: true,
        }"
        :title="$t('home.dashboard.tab_manager')"
        class="self-center p15"
        @click="toggleDashboardTabEditor()"
      >
        <i class="el-icon-setting"></i>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['dashboardTabContexts', 'dashboardTabId', 'edit'],
  data() {
    return {
      mainTabs: [],
      moreTabs: [],
      tabLimit: 5,
      loading: false,
      moreTabSelectedDashboard: [],
    }
  },
  computed: {
    moreTabsCascadeData() {
      let { moreTabs } = this
      if (moreTabs && moreTabs.length) {
        let data = []
        moreTabs.forEach(folder => {
          let d = { value: folder.id, label: folder.name, ...folder }
          if (folder.childTabs && folder.childTabs.length) {
            let c = []
            folder.childTabs.forEach(tab => {
              let t = { value: tab.id, label: tab.name, ...tab }
              c.push(t)
            })
            this.$set(d, 'children', c)
          }
          data.push(d)
        })
        return data
      }
      return []
    },
  },
  watch: {
    dashboardTabContexts() {
      this.splitTabList()
    },
  },
  mounted() {
    this.splitTabList()
    this.selectDefaultMoreTab()
  },
  methods: {
    selectDefaultMoreTab() {
      if (this.moreTabsCascadeData.length) {
        this.moreTabsCascadeData.forEach(tab => {
          if (tab.id === this.dashboardTabId) {
            this.moreTabSelectedDashboard.push(tab.id)
          } else {
            if (tab?.children && tab.children.length) {
              tab.children.forEach(children => {
                if (children.id === this.dashboardTabId) {
                  this.moreTabSelectedDashboard = [tab.id, children.id]
                }
              })
            }
          }
        })
      }
    },
    mainTabChange(tab) {
      this.changeDashboardTabId(tab.id)
      this.moreTabSelectedDashboard = []
    },
    expandChange(value) {
      this.changeDashboardTabId(value[0])
    },
    moreTabChange(value) {
      if (value && value.length === 1) {
        this.changeDashboardTabId(value[0])
      } else if (value && value.length === 2) {
        this.changeDashboardTabId(value[1])
      }
    },
    getTabActiveClass(tab) {
      if (tab.id === this.dashboardTabId) {
        return true
      } else if (tab.childTabs && tab.childTabs.length) {
        if (tab.childTabs.findIndex(t => t.id === this.dashboardTabId) > -1) {
          return true
        }
      }
      return false
    },
    switchTabs(tab) {
      let lastTab = []
      lastTab = this.$helpers.cloneObject(
        this.mainTabs[this.mainTabs.length - 1]
      )
      let firstTab = []
      let selectedTabIndex = this.moreTabs.findIndex(rt => rt.id === tab.id)
      firstTab = this.$helpers.cloneObject(tab)
      this.mainTabs.splice(this.mainTabs.length - 1, 1, firstTab)
      this.moreTabs.splice(selectedTabIndex, 1, lastTab)
    },
    closemoreTab() {
      this.$refs[`more-popover`].showPopper = false
    },
    splitTabList() {
      this.loading = true
      let { dashboardTabContexts, tabLimit } = this
      if (dashboardTabContexts && dashboardTabContexts.length > tabLimit) {
        this.mainTabs = []
        this.moreTabs = []
        dashboardTabContexts.forEach((tab, index) => {
          if (index < tabLimit) {
            this.mainTabs.push(tab)
          } else {
            this.moreTabs.push(tab)
          }
        })
      } else {
        this.mainTabs = dashboardTabContexts
      }
      this.loading = false
    },
    getTabName(tab) {
      if (tab && tab.childTabs && tab.childTabs.length) {
        if (this.isTabSwitched(tab)) {
          let childTab = tab.childTabs.find(rt => rt.id === this.dashboardTabId)
          if (childTab) {
            return childTab.name
          }
        }
      }
      return tab.name
    },
    isTabSwitched(tab) {
      if (tab.id === this.dashboardTabId) {
        return false
      } else if (tab.childTabs && tab.childTabs.length) {
        let childTab = tab.childTabs.find(rt => rt.id === this.dashboardTabId)
        if (childTab) {
          return true
        }
      }
      return false
    },
    closepopver(id) {
      if (id) {
        this.$refs[`popover-${id}`][0].showPopper = false
      }
    },
    changeDashboardTabId(tabId) {
      this.$emit('changeDashboardTabId', tabId)
    },
    toggleDashboardTabEditor() {
      this.$emit('toggleDashboardTabEditor')
    },
    childTabChange() {},
  },
}
</script>
<style lang="scss">
.db-childtab-cell {
  padding: 10px 20px;
  cursor: pointer;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
  border-bottom: 1px solid #eff2f5;
  &:hover {
    color: #ff3184;
  }
}

.db-childtab-cell:hover {
  // font-weight: 500;
}

.border-right {
  border-right: 1px solid #eff2f5;
}

.fc-dashboard-tab-popup {
  padding: 0 !important;
}
.db-childtab-cell.active {
  background: #f1f8fa;
}
.fc-dashboard-more-tab-popup {
  .el-cascader-node.in-active-path,
  .el-cascader-node.is-active,
  .el-cascader-node.is-selectable.in-checked-path {
    color: #ff3184;
  }
}
.fc-dashboard-more-tab-popup {
  padding: 0px;
  font-size: 13px;
}
</style>
