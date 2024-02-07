<template>
  <div class="layout-tabs">
    <AppBanner v-if="showReloadApp">
      {{ $t('common.wo_report.app_has_changes_to') }}
      <span @click="reload" class="fwBold pointer">
        {{ $t('common.products.reload') }}</span
      >
      .
    </AppBanner>
    <slot></slot>
    <portal v-if="isActive" to="header-buttons">
      <el-dropdown v-if="activeTab === 'layout'" @command="addTabGroup">
        <el-button type="primary" class="setup-el-btn pL20 pR20 height40 f12">
          {{ $t('common._common.add_tab_group') }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown" class="width200px">
          <el-dropdown-item command="group">{{
            $t('common._common.add_tab_group')
          }}</el-dropdown-item>
          <el-dropdown-item command="tab">
            {{ $t('common._common.add_tab') }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <button v-else @click="editTab()" class="setup-el-btn pL20 pR20">
        {{ $t('common._common.add_tab') }}
      </button>
    </portal>
    <div v-if="activeTab === 'layout'" class="layout-icons">
      <inline-svg
        src="svgs/web-view"
        class="mL5 active-border-radius web-view-icon"
        :iconClass="getViewIconClass(layoutTypes.WEB_VIEW)"
        @click.native="selectView(layoutTypes.WEB_VIEW)"
      ></inline-svg>
      <inline-svg
        src="svgs/mobile-view"
        class="mL5 active-border-radius mobile-view-icon"
        :iconClass="getViewIconClass(layoutTypes.MOBILE_VIEW)"
        @click.native="selectView(layoutTypes.MOBILE_VIEW)"
      ></inline-svg>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane
        :label="$t('common.dashboard.tabs')"
        name="tab"
        class="height-100 fc-tabLayout-scroll"
      >
        <div v-if="loading || tabListLoading" class="height80vh flex-center-vH">
          <spinner :show="loading || tabListLoading" size="80"></spinner>
        </div>

        <div v-else-if="$validation.isEmpty(webtabs)" class="empty-tab-list">
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-130 emptystate-icon-size"
          ></InlineSvg>

          <div class="empty-state-text">
            {{ $t('common._common.no_tabs_available') }}
          </div>

          <button @click="showAddTab = true" class="add-tab-btn">
            {{ $t('common.wo_report.create_tab') }}
          </button>
        </div>
        <el-table
          v-else
          :data="webtabs"
          :cell-style="{ padding: '12px 30px' }"
          style="width: 100%"
          height="100%"
        >
          <el-table-column
            prop="name"
            :label="$t('common._common.tab_name')"
          ></el-table-column>
          <el-table-column :label="$t('maintenance.wr_list.type')">
            <template v-slot="tab">
              {{ tabTypesDisplayName[tab.row.type] || '---' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="route"
            :label="$t('common.wo_report.route')"
          ></el-table-column>
          <el-table-column class="visibility-visible-actions">
            <template v-slot="tab">
              <div class="text-center">
                <i
                  v-if="canShowEditTab(tab.row.type)"
                  class="el-icon-edit edit-icon visibility-hide-actions"
                  :title="$t('common.products.edit_tab')"
                  data-arrow="true"
                  v-tippy
                  @click="editTab(tab.row)"
                ></i>
                <i
                  class="el-icon-delete webtab-delete-icon visibility-hide-actions"
                  data-arrow="true"
                  :title="$t('common.wo_report.delete_tab_title')"
                  v-tippy
                  @click="deleteTab(tab.row)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane
        :label="$t('common._common.layouts')"
        name="layout"
        class="height-100"
      >
        <div
          v-if="loading || groupListLoading"
          class="height80vh flex-center-vH"
        >
          <spinner :show="loading || groupListLoading" size="80"></spinner>
        </div>
        <el-row v-else class="layout-container">
          <el-col :span="8" class="height-100 d-flex flex-col">
            <div v-if="!showSearch" class="group-list-header">
              <div class="group-header">
                {{ $t('common._common.tab_group') }}
              </div>
              <div class="pointer" @click="toggleSearch">
                <i class="fa fa-search" aria-hidden="true"></i>
              </div>
            </div>
            <div v-else class="group-list-search">
              <div class="d-flex">
                <i
                  class="el-icon-search pT5 pR10 f16 fc-black-15 fwBold"
                  style="color: #25243e;"
                ></i>
                <el-input
                  v-model="grpFilterVal"
                  :placeholder="$t('common._common.search_group')"
                  :autofocus="true"
                ></el-input>
              </div>
              <i
                class="el-icon-close pointer f16 fc-black-15 fwBold"
                @click="toggleSearch"
              ></i>
            </div>

            <div class="group-list">
              <draggable
                v-model="filteredGroup"
                v-bind="fieldSectionDragOptions"
                group="groups"
                @end="updateGroupList"
                draggable=".is-draggable"
                handle=".task-handle"
              >
                <div
                  v-for="group in filteredGroup"
                  :key="group.id"
                  @click="activeGroupId = group.id"
                  :class="[
                    'group-list-item visibility-visible-actions',
                    activeGroupId === group.id && 'active',
                    grpFilterVal ? 'is-default' : 'is-draggable',
                  ]"
                >
                  <div class="task-handle mR20 p5">
                    <img src="~assets/drag-grey.svg" />
                  </div>
                  <inline-svg
                    :src="getGroupIcon(group.iconType)"
                    :iconClass="
                      `icon group-icon-color ${getGroupIconClass(
                        group.iconType
                      )}`
                    "
                  ></inline-svg>
                  <div class="group-name">{{ group.name }}</div>
                  <div class="d-flex mL-auto">
                    <i
                      class="el-icon-edit edit-icon visibility-hide-actions"
                      :title="$t('common.header.edit_group')"
                      data-arrow="true"
                      v-tippy
                      @click="editGroup(group)"
                    ></i>
                    <i
                      class="el-icon-delete webtab-delete-icon visibility-hide-actions"
                      data-arrow="true"
                      :title="$t('common.wo_report.delete_group')"
                      v-tippy
                      @click="deleteGroup(group)"
                    ></i>
                  </div>
                </div>
              </draggable>
            </div>
          </el-col>

          <el-col :span="16" class="selected-group-tabs pT0">
            <div
              class="flex-center-row-space mB20 border-bottom23 pL20 pR20 flex-middle"
              style="height: 67px;"
            >
              <div class="group-header">
                {{ $t('common._common.selected_tabs') }}
              </div>
              <div
                v-if="!$validation.isEmpty(tabListForGroup)"
                @click="showTabPicker = true"
                class="d-flex pointer fc-add-associate"
              >
                <inline-svg
                  src="svgs/plus-button"
                  class="fill-greeny-blue"
                ></inline-svg>
                <div class="add-tab">
                  {{ $t('common._common.associate_tabs') }}
                </div>
              </div>
            </div>

            <div
              v-if="$validation.isEmpty(tabListForGroup)"
              class="empty-tab-list"
            >
              <InlineSvg
                src="svgs/emptystate/readings-empty"
                iconClass="icon text-center icon-130 emptystate-icon-size"
              ></InlineSvg>

              <template v-if="$validation.isEmpty(webTabGroups)">
                <div class="empty-state-text">
                  {{ $t('common._common.no_group_available') }}
                </div>
                <button @click="showAddGroup = true" class="add-tab-btn">
                  {{ $t('common._common.create_group') }}
                </button>
              </template>

              <template v-else>
                <div class="empty-state-text">
                  {{
                    $t('common._common.no_tabs_associated_click_here_associate')
                  }}
                </div>
                <button @click="showTabPicker = true" class="add-tab-btn">
                  {{ $t('common._common.associate_tabs') }}
                </button>
              </template>
            </div>

            <draggable
              v-else
              v-model="tabListForGroup"
              v-bind="fieldSectionDragOptions"
              group="tabs"
              @end="updateTabList"
              draggable=".is-draggable"
              handle=".task-handle"
              class="overflow-scroll pL20 pR20"
              style="height: calc(100vh - 333px);"
            >
              <div
                v-for="tab in tabListForGroup"
                :key="tab.id"
                class="mB15 is-draggable"
              >
                <div class="selected-tab visibility-visible-actions">
                  <div class="task-handle mR20 p5">
                    <img src="~assets/drag-grey.svg" />
                  </div>
                  <div class="mT-auto mB-auto">
                    <div class="tab-name">{{ tab.name }}</div>
                    <div class="tab-type">
                      {{ tabTypesDisplayName[tab.type] }}
                    </div>
                  </div>
                  <div class="mL-auto">
                    <i
                      v-if="canShowEditTab(tab.type)"
                      class="el-icon-edit edit-icon visibility-hide-actions"
                      :title="$t('common.products.edit_tab')"
                      data-arrow="true"
                      v-tippy
                      @click="editTab(tab)"
                    ></i>
                    <i
                      class="el-icon-delete trash-icon visibility-hide-actions"
                      data-arrow="true"
                      :title="$t('common._common.remove_tab')"
                      v-tippy
                      @click="disassociateTab(tab)"
                    ></i>
                  </div>
                </div>
              </div>
            </draggable>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      :visible="showTabDeletionWarning"
      width="30%"
      :title="$t('common.header.warning')"
      class="fieldchange-Dialog pB15 fc-dialog-center-container"
      custom-class="dialog-header-padding"
      :append-to-body="true"
      :before-close="
        () => {
          showTabDeletionWarning = false
        }
      "
    >
      <div class="overflow-y-scroll pB40 pT10" style="word-break: break-word;">
        {{ $t('common._common.tab_associated_group') }}
        <br />
        <br />
        {{ $t('common.header.remove_tabs_before_delete') }}
      </div>
    </el-dialog>

    <tab-picker
      v-if="showTabPicker"
      :webtabs="webtabs"
      :webTabGroups="webTabGroups"
      :activeGroupId="activeGroupId"
      :tabTypes="tabTypesDisplayName"
      @updateGroupTabs="updateGroupTabs"
      @createTab="openTabCreation"
      @reload="toggleBanner"
      @close="showTabPicker = false"
    ></tab-picker>

    <NewWebtabGroup
      v-if="showAddGroup"
      :activeGroup="activeGroup"
      :isNew="$validation.isEmpty(activeGroup)"
      :linkName="appName"
      :disabledIcons="selectedIcons"
      :layoutId="layoutId"
      :appLayoutType="appLayoutType"
      :currentAppDomain="currentAppDomain"
      @onSave="updateGroup"
      @reload="toggleBanner"
      @onClose="showAddGroup = false"
    ></NewWebtabGroup>

    <NewWebTab
      v-if="showAddTab"
      :activeWebtab="activeWebTab"
      :isNew="$validation.isEmpty(activeWebTab)"
      :tabTypes="tabTypes"
      :tabTypesDisplayName="tabTypesForEdit"
      :applicationId="appId"
      @onSave="updateTab"
      @reload="toggleBanner"
      @onClose="showAddTab = false"
    ></NewWebTab>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import draggable from 'vuedraggable'
import TabPicker from './TabPicker'
import NewWebtabGroup from './NewWebtabGroup'
import NewWebTab from './NewWebTab'
import AppBanner from 'pages/AppBanner.vue'
import { API } from '@facilio/api'
import sortBy from 'lodash/sortBy'
import {
  loadAppTabs,
  loadLayouts,
  getGroupIcon,
  getGroupIconClass,
} from 'util/webtabUtil'

const layoutTypes = {
  DEFAULT_VIEW: 0,
  WEB_VIEW: 1,
  MOBILE_VIEW: 2,
}

export default {
  props: ['appId', 'currentAppId', 'loading', 'isActive'],
  components: { draggable, TabPicker, NewWebtabGroup, NewWebTab, AppBanner },
  data() {
    return {
      activeTab: 'layout',
      webtabs: null,
      webTabGroups: [],
      selectedLayout: 1,
      iconClass: 'icon icon-40 p10 pointer',
      showSearch: false,
      grpFilterVal: null,
      showTabPicker: false,
      showAddGroup: false,
      showAddTab: false,
      reOpenAssociateTab: false,
      activeGroup: null,
      activeWebTab: null,
      showReloadApp: false,
      showTabDeletionWarning: false,
      appName: null,
      groupListLoading: false,
      tabListLoading: false,
      appLayouts: null,
      appDomain: null,
      activeGroupId: null,
      tabTypes: {
        MODULE: 1,
        APPROVAL: 2,
        REPORT: 4,
        ANALYTICS: 5,
        KPI: 6,
        DASHBOARD: 7,
        CUSTOM: 8,
        APPS: 9,
        PORTAL_OVERVIEW: 12,
        TIMELINE: 11,
        INDOOR_FLOORPLAN: 14,
        HOMEPAGE: 15,
        SERVICE_CATALOG: 16,
        SURVEY: 38,
        PIVOT: 78,
        SHIFT_PLANNER: 83,
        MY_ATTENDANCE: 84,
        ATTENDANCE: 85,
        NEW_KPI: 87,
        RULES: 88,
        NEW_DASHBOARD: 94,
        DISPATCHER_CONSOLE: 100,
      },
      tabTypesDisplayName: {
        1: 'Module',
        2: 'Approval',
        4: 'Report',
        5: 'Analytics',
        6: 'KPI',
        7: 'Dashboard',
        8: 'Custom',
        9: 'Connected App',
        12: 'WebView',
        11: 'Timeline',
        14: 'Indoor Floorplan',
        15: 'Home Page',
        16: 'Service Catalog',
        38: 'Survey',
        78: 'Pivot',
        // 10: 'Setting',
        83: 'Shift Planner',
        84: 'My Attendance',
        85: 'Attendance',
        87: 'New KPI',
        88: 'Rules',
        94: 'New Dashboard',
        100: 'Dispatcher Console',
      },
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      layoutTypes,
      getGroupIcon,
      getGroupIconClass,
    }
  },

  mounted() {
    document.addEventListener('keydown', this.keyDownHandler)
  },
  beforeDestroy() {
    document.removeEventListener('keydown', this.keyDownHandler)
  },

  computed: {
    tabListForGroup: {
      get() {
        let { webTabGroups, activeGroupId } = this

        if (activeGroupId) {
          let { tabs } =
            webTabGroups.find(group => group.id === activeGroupId) || {}
          return sortBy(tabs, ['order'])
        } else {
          return []
        }
      },
      set(value) {
        let tabs = value.map((tab, order) => {
          let { id, name, type } = tab
          return { id, name, order: order + 1, type }
        })

        this.webTabGroups.forEach(group => {
          if (group.id === this.activeGroupId) {
            this.$set(group, 'tabs', tabs)
          }
        })
      },
    },
    selectedIcons() {
      return this.webTabGroups.map(group => group.iconType)
    },
    tabTypesForEdit() {
      let { canShowEditTab, tabTypesDisplayName } = this
      let tabTypes = { ...tabTypesDisplayName }

      !canShowEditTab(8) && delete tabTypes[8]
      return tabTypes
    },
    filteredGroup: {
      get() {
        let { grpFilterVal, webTabGroups } = this

        if (!isEmpty(grpFilterVal)) {
          let filterObj = webTabGroups.filter(grp => {
            let name = grp.name.toLowerCase()

            return name.includes(grpFilterVal)
          })
          return filterObj
        } else {
          return webTabGroups
        }
      },
      set(value) {
        this.webTabGroups = value
      },
    },
    tabGroupHash() {
      let { webTabGroups } = this

      if (webTabGroups) {
        return webTabGroups.reduce((tabGroupHash, group) => {
          let { id: grpId, tabs } = group
          let tabIds = tabs.map(tab => tab.id)

          tabIds.forEach(tab => {
            tabGroupHash[tab] = grpId
          })

          return tabGroupHash
        }, {})
      } else {
        return {}
      }
    },
    enabledLayouts() {
      let {
        appLayouts,
        layoutTypes: { DEFAULT_VIEW, WEB_VIEW, MOBILE_VIEW },
      } = this
      let enabledLayouts = {
        [DEFAULT_VIEW]: true,
        [WEB_VIEW]: false,
        [MOBILE_VIEW]: false,
      }

      if (!isEmpty(appLayouts)) {
        appLayouts.forEach(layout => {
          enabledLayouts[layout.layoutDeviceType] = true
        })
      }

      return enabledLayouts
    },
    currentAppLayout() {
      let { appLayouts, selectedLayout } = this

      return (
        (appLayouts || []).find(
          view => view.layoutDeviceType === selectedLayout
        ) || {}
      )
    },
    appLayoutType() {
      let { currentAppLayout } = this
      let { appLayoutType } = currentAppLayout
      return appLayoutType || 2
    },
    layoutId() {
      let { currentAppLayout } = this
      return currentAppLayout.id || null
    },
    currentAppDomain() {
      let { domain } = this.appDomain
      return domain
    },
  },

  watch: {
    selectedLayout() {
      this.constructWebtabGroups()
      this.activeGroupId = this.$getProperty(this.webTabGroups, '0.id', null)
    },
    activeTab(newVal) {
      if (!isEmpty(this.webTabGroups)) {
        if (newVal === 'layout') {
          this.activeGroupId = this.webTabGroups[0].id
        } else {
          this.tabListLoading = true
          this.$nextTick(() => (this.tabListLoading = false))
          this.activeGroupId = null
        }
      }
    },
    appId: {
      handler(value) {
        if (!isEmpty(value)) {
          this.init()
        }
      },
      immediate: true,
    },
    showAddTab(value) {
      if (!value && this.reOpenAssociateTab) {
        this.showTabPicker = true
        this.reOpenAssociateTab = false
      }
    },
  },

  methods: {
    async loadTabs() {
      let { error, data } = await loadAppTabs(this.appId)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.webtabs = data
      }
      this.tabListLoading = false
    },
    async loadAppLayouts() {
      let { error, data } = await loadLayouts(this.appId)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { linkName, layouts, appDomain } = data || {}

        this.appLayouts = layouts
        this.appName = linkName
        this.appDomain = appDomain
        this.selectedLayout = 1
        this.constructWebtabGroups()
      }
      this.groupListLoading = false
    },
    async init() {
      this.tabListLoading = true
      this.groupListLoading = true
      await this.loadTabs()
      await this.loadAppLayouts()
    },
    constructWebtabGroups() {
      let { webTabGroupList: groups } = this.currentAppLayout

      if (!isEmpty(groups)) {
        let webTabGroups = groups.reduce((groupWithPermission, group) => {
          let { id, name, iconType, route, webTabs, order } = group
          let tabs = (webTabs || []).map(tab => {
            let { id, name, order, type } = tab
            return { id, name, order, type }
          })
          let groupPermission = {
            id,
            name,
            iconType,
            route,
            tabs,
            order,
          }

          groupWithPermission.push(groupPermission)
          return groupWithPermission
        }, [])

        this.webTabGroups = sortBy(webTabGroups, ['order'])
        this.activeGroupId = this.webTabGroups[0].id
      } else {
        this.webTabGroups = []
      }
    },
    canShowEditTab(type) {
      let { tabTypes, $route, tabTypesDisplayName } = this
      let { query } = $route || {}
      let isCustomTabEnabled = type === tabTypes.CUSTOM && query.showCustom

      return (
        (process.env.NODE_ENV === 'development' ||
          ![tabTypes.CUSTOM].includes(type) ||
          isCustomTabEnabled) &&
        !isEmpty(tabTypesDisplayName[type])
      )
    },
    addTabGroup(command) {
      if (command === 'tab') {
        this.editTab()
      } else if (command === 'group') {
        this.editGroup()
      }
    },
    editTab(tab = null) {
      let webtab = tab ? this.webtabs.find(t => t.id === tab.id) : null

      this.activeWebTab = webtab
      this.showAddTab = true
    },
    openTabCreation() {
      this.reOpenAssociateTab = true
      this.activeWebTab = null
      this.showAddTab = true
    },
    deleteTab({ id }) {
      if (this.tabGroupHash[id]) {
        this.showTabDeletionWarning = true
      } else {
        let dialogObj = {
          title: this.$t('common.wo_report.delete_tab_title'),
          htmlMessage: this.$t(
            'common.wo_report.are_you_sure_want_to_delete_this_tab'
          ),
          rbDanger: true,
          rbLabel: this.$t('common.login_expiry.rbLabel'),
        }

        this.$dialog.confirm(dialogObj).then(async value => {
          if (value) {
            API.post('/v2/tab/delete', { id }).then(({ error }) => {
              if (error) {
                this.$message.error(error.message || 'Error Occured')
              } else {
                let index = this.webtabs.findIndex(tab => tab.id === id)

                if (!isEmpty(index)) {
                  this.webtabs.splice(index, 1)
                }
                this.$message.success(
                  this.$t('common._common.tab_deleted_success')
                )
                this.toggleBanner()
              }
            })
          }
        })
      }
    },
    async removeTab(id) {
      let params = {
        tabList: [{ id }],
        tabGroupId: this.activeGroupId,
      }

      await API.post('v2/tab/disassociate', params).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(
            this.$t('common._common.tab_disassociated_from_this_group')
          )
          this.webTabGroups.forEach(group => {
            let { id: groupId, tabs } = group

            if (this.activeGroupId === groupId) {
              group.tabs = tabs.filter(tab => tab.id !== id)
            }
          })
          this.toggleBanner()
        }
      })
    },
    disassociateTab({ id }) {
      let dialogObj = {
        title: this.$t('common._common.disassciate_tab'),
        htmlMessage: this.$t(
          'common._common.are_you_sure_want_to_remove_tab_from_this_group'
        ),
        rbDanger: true,
        rbLabel: this.$t('common.login_expiry.rbLabel'),
      }

      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.removeTab(id)
        }
      })
    },
    editGroup(group = null) {
      this.activeGroup = group
      this.showAddGroup = true
    },
    deleteGroup({ id }) {
      let dialogObj = {
        title: this.$t('common.wo_report.delete_group'),
        htmlMessage: this.$t(
          'common._common.are_you_sure_want_to_delete_group_from_this_app'
        ),
        rbDanger: true,
        rbLabel: this.$t('common.login_expiry.rbLabel'),
      }

      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          API.post('/v2/tabGroup/delete', { id }).then(({ error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              let index = this.webTabGroups.findIndex(group => group.id === id)

              if (!isEmpty(index)) {
                this.webTabGroups.splice(index, 1)
              }
              this.$message.success(
                this.$t('common._common.group_deleted_sucess')
              )
              this.toggleBanner()
            }
          })
        }
      })
    },
    updateTabList() {
      let { activeGroupId, webTabGroups } = this
      let { tabs } = webTabGroups.find(group => group.id === activeGroupId)
      let tabsGroupsList = tabs.map(tab => {
        let { id, order } = tab
        return { webTabId: id, order, webTabGroupId: activeGroupId }
      })

      API.post('/v2/tab/reorder', { tabsGroupsList }).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(this.$t('common._common.tabs_reordered'))
          this.toggleBanner()
        }
      })
    },
    updateGroupList() {
      let groupList = this.filteredGroup.map((group, order) => {
        let { id } = group
        return { id, order: order + 1 }
      })

      API.post('/v2/tabGroup/reorder', { groupList }).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(this.$t('common._common.groups_reordered'))
          this.toggleBanner()
        }
      })
    },
    updateTab(webtab) {
      let index = this.webtabs.findIndex(tab => webtab.id === tab.id)

      if (isEmpty(index)) {
        this.webtabs.push(webtab)
      } else {
        let grpId = this.tabGroupHash[webtab.id]

        if (!isEmpty(grpId)) {
          let grpIdx = this.webTabGroups.findIndex(group => group.id === grpId)

          if (!isEmpty(grpIdx)) {
            let webtabGroup = this.webTabGroups[grpIdx]
            let { tabs } = webtabGroup || {}
            let idx = (tabs || []).findIndex(t => t.id === webtab.id)

            if (!isEmpty(idx)) {
              let { id, name, order, type } = webtab

              tabs.splice(idx, 1, { id, name, order, type })
              this.webTabGroups.splice(grpIdx, 1, { ...webtabGroup, tabs })
            }
          }
        }
        this.webtabs.splice(index, 1, webtab)
      }
    },
    updateGroup(webGroup) {
      let index = this.webTabGroups.findIndex(group => group.id === webGroup.id)

      if (isEmpty(index)) {
        this.webTabGroups.push(webGroup)
      } else {
        let webtabGroup = this.webTabGroups[index]
        let { name, route, iconType } = webGroup || {}

        this.webTabGroups.splice(index, 1, {
          ...webtabGroup,
          name,
          route,
          iconType,
        })
      }
    },
    updateGroupTabs(groupTabs) {
      let { activeGroupId, webTabGroups } = this
      let index = webTabGroups.findIndex(group => group.id === activeGroupId)

      if (!isEmpty(index)) {
        let webtabGroup = webTabGroups[index]

        this.webTabGroups.splice(index, 1, {
          ...webtabGroup,
          tabs: groupTabs,
        })
      }
    },
    getViewIconClass(layoutType) {
      let iconClass = this.iconClass

      if (!this.enabledLayouts[layoutType]) iconClass += ' fill-disabled-grey'
      if (layoutType === this.selectedLayout) iconClass += ' fill-pink'
      return iconClass
    },
    selectView(layoutType) {
      if (!this.groupListLoading && this.enabledLayouts[layoutType]) {
        this.selectedLayout = layoutType
      }
    },
    keyDownHandler(e) {
      if (e.key === 'Escape' && this.showSearch) {
        this.toggleSearch()
      }
    },
    toggleSearch() {
      this.grpFilterVal = null
      this.showSearch = !this.showSearch
    },
    toggleBanner() {
      this.$nextTick(() => {
        let { currentAppId, appId } = this

        if (appId === currentAppId) {
          this.$emit('reloadApp')
          this.showReloadApp = true
        }
      })
    },
    reload() {
      let { path } = this.$route
      window.location.href = path
    },
  },
}
</script>
<style lang="scss">
.layout-tabs {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

  .header-container {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;

    .header-title {
      font-size: 18px;
      letter-spacing: 0.5px;
      color: #324056;
      padding-bottom: 5px;
    }
    .header-content {
      font-size: 13px;
      letter-spacing: 0.5px;
      color: #808080;
    }
    .add-group-btn {
      border-radius: 3px;
      border-color: transparent;
      background-color: #ee518f;
      padding: 10px;
      width: 200px;
      color: #fff;
      font-weight: 500;
      text-transform: uppercase;
      cursor: pointer;
    }
    .add-group-btn:hover {
      font-weight: bold;
    }
  }
  .layout-icons {
    top: 70px;
    right: 20px;
    position: absolute;
    z-index: 1;
  }
  .layout-container {
    height: calc(100vh - 210px);
    border: 1px solid #ececec;
  }
  .el-tabs {
    margin-top: 20px;
    .el-tabs__header {
      margin-bottom: 10px;

      .el-tabs__item,
      .el-tabs__item.is-active {
        font-size: 11px;
      }
    }
    .el-tabs__content {
      background-color: #fff;
      overflow: scroll;
      height: calc(100vh - 210px);
    }
  }

  .webtab-delete-icon {
    cursor: pointer;
    color: #324056;
    padding: 5px 5px;
    border-radius: 4px;
    margin-left: 10px;
    border: 1px solid transparent;
    font-size: 16px;
    &:hover {
      color: #de7272;
      background: #fff;
      border: 1px solid #d0d9e2;
      transition: 0.3s;
    }
  }

  .edit-icon {
    cursor: pointer;
    color: #324056;
    padding: 5px 5px;
    border-radius: 4px;
    border: 1px solid transparent;
    font-size: 16px;
    &:hover {
      color: #319aa8;
      background: #fff;
      border: 1px solid #d0d9e2;
      transition: 0.3s;
    }
  }

  .group-header {
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 1px;
    color: #324056;
    text-transform: uppercase;
  }
  .group-list-header {
    display: flex;
    justify-content: space-between;
    padding: 25px 30px;
    border-bottom: 1px solid #ececec;
  }
  .group-list-search {
    display: flex;
    justify-content: space-between;
    padding: 19px 30px;
    border-bottom: 1px solid #ececec;
    align-items: center;

    .el-input .el-input__inner {
      border-bottom: 0px solid #d8dce5;
    }
  }
  .group-list {
    overflow-y: scroll;
    padding-bottom: 50px;
  }
  .group-list-item {
    display: flex;
    padding: 20px 30px;
    align-items: center;
    border-bottom: 1px solid #f3f5f6;

    .group-icon-color {
      fill: #a9abc9;
    }
    .group-name {
      font-size: 14px;
      letter-spacing: 0.5px;
      color: #324056;
      padding-left: 10px;
    }
  }
  .group-list-item.active {
    background-color: #f7f8f9;
  }
  .group-list-item:hover {
    background-color: #f1f8fa;
  }
  .selected-group-tabs {
    height: 100%;
    padding: 25px 0;
    border-left: 1px solid #e6e6e6;
    display: flex;
    flex-direction: column;

    .selected-tab {
      padding: 15px 20px;
      border: 1px solid #f4f5f7;
      box-shadow: 0px 3px 5px #f4f5f7;
      display: flex;
      font-size: 13px;
      align-items: center;

      .tab-name {
        font-size: 14px;
        letter-spacing: 0.5px;
        color: #324056;
      }
      .tab-type {
        font-size: 12px;
        letter-spacing: 0.5px;
        color: #8ca0ad;
        margin-top: 5px;
      }
      .trash-icon {
        cursor: pointer;
        color: #324056;
        padding: 5px 5px;
        border-radius: 4px;
        margin-left: 10px;
        border: 1px solid transparent;
        font-size: 16px;
        &:hover {
          color: #de7272;
          background: #fff;
          border: 1px solid #d0d9e2;
          transition: 0.3s;
        }
      }

      .edit-icon {
        cursor: pointer;
        color: #324056;
        padding: 5px 5px;
        border-radius: 4px;
        border: 1px solid transparent;
        font-size: 16px;
        &:hover {
          color: #319aa8;
          background: #fff;
          border: 1px solid #d0d9e2;
          transition: 0.3s;
        }
      }
    }
    .selected-tab:hover {
      box-shadow: none;
      border: 1px solid #38b2c1;
    }
    .fill-greeny-blue {
      svg {
        path {
          fill: #39b2c2;
        }
      }
    }
    .add-tab {
      color: #39b2c2;
      margin-left: 5px;
      font-size: 13px;
      font-weight: 500;
      letter-spacing: 0.46px;
    }
  }
  .empty-tab-list {
    height: 50vh;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    margin: auto;

    .empty-state-text {
      width: 69%;
      font-size: 14px;
      letter-spacing: 0.5px;
      text-align: center;
      color: #324056;
      line-height: 20px;
    }
    .add-tab-btn {
      border-radius: 2.8px;
      border: solid 1px #3ab2c2;
      background-color: #ffffff;
      font-size: 12px;
      font-weight: 500;
      letter-spacing: 1px;
      color: #3ab2c2;
      margin-top: 20px;
      padding: 5px 25px;
      text-transform: uppercase;
      cursor: pointer;
    }
  }
  .is-draggable .task-handle {
    cursor: move;
  }
  .height43 {
    height: 43px;
  }
}
.active-border-radius {
  svg.icon.fill-pink {
    border-radius: 4px;
  }
  &:hover {
    .icon-40 {
      cursor: pointer;
      border-radius: 4px;
      background-color: #fff1f7;
    }
  }
}
.fc-add-associate {
  padding: 6px 10px;
  align-items: center;
  .fill-greeny-blue {
    position: relative;
    top: 2px;
  }
  &:hover {
    cursor: pointer;
    background: #39b2c2;
    border-radius: 4px;
    transition: 0.4s all;
    .add-tab {
      color: #fff;
    }
    .fill-greeny-blue svg path {
      fill: #fff;
    }
  }
}

.fc-tabLayout-scroll {
  .el-table__row {
    &:hover {
      background: #f1f8fa !important;
    }
  }
  .el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell {
    background-color: inherit;
  }
}
</style>
