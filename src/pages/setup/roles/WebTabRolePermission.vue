<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-animated slideInRight fc-dialog-form fc-dialog-right setup-dialog80 setup-dialog webtab-role"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="header-layout">
      <error-banner
        :error.sync="error"
        :errorMessage.sync="errorText"
      ></error-banner>
      <div class="header-content">
        <div>
          <div class="role-name">{{ role.name }}</div>
          <div class="role-descrip">{{ role.description }}</div>
        </div>
        <div>
          <el-button @click="closeDialog" class="fc-v1-btn-cancel">
            Cancel
          </el-button>
          <el-button @click="saveRole" :loading="saving" class="fc-v1-btn-save"
            >{{ saving ? 'Saving' : 'Save' }}
          </el-button>
        </div>
      </div>
      <div class="header-content mT10">
        <div>
          <inline-svg
            src="product-icons/app"
            class="vertical-middle mR5"
            iconClass="icon icon-md"
          />
          {{ appName }}
        </div>
        <div>
          <inline-svg
            src="svgs/default-view"
            class="mL5"
            :iconClass="getViewIconClass(layoutTypes.DEFAULT_VIEW)"
            @click.native="selectView(layoutTypes.DEFAULT_VIEW)"
          ></inline-svg>
          <inline-svg
            src="svgs/web-view"
            class="mL5"
            :iconClass="getViewIconClass(layoutTypes.WEB_VIEW)"
            @click.native="selectView(layoutTypes.WEB_VIEW)"
          ></inline-svg>
          <inline-svg
            src="svgs/mobile-view"
            class="mL5"
            :iconClass="getViewIconClass(layoutTypes.MOBILE_VIEW)"
            @click.native="selectView(layoutTypes.MOBILE_VIEW)"
          ></inline-svg>
        </div>
      </div>
    </div>
    <div class="tab-permission-layout">
      <div class="tab-permission-sidebar">
        <template v-if="selectedLayout === layoutTypes.DEFAULT_VIEW">
          <div class="search-box">
            <i
              class="el-icon-search pT7 pR10"
              style="color: rgba(0, 0, 0, 0.5);"
            ></i>

            <el-input
              v-model="filterValue"
              placeholder="Search tabs"
              :autofocus="true"
              clearable
            ></el-input>
          </div>
          <div class="sidebar-container pB100">
            <template v-if="tabListLoading">
              <div
                v-for="index in [1, 2, 3, 4, 5]"
                :key="index"
                style="padding: 10px 30px;"
              >
                <span class="tab-lines loading-shimmer"></span>
              </div>
            </template>
            <template v-else>
              <div
                v-for="tab in filteredTabs"
                :key="tab.id"
                class="tab-list-name"
                :id="`${trimTabName(tab.name)}-link`"
                @click="scrollTo(trimTabName(tab.name))"
              >
                {{ tab.name }}
              </div>
            </template>
          </div>
        </template>
        <template v-else>
          <div class="sidebar-container">
            <template v-if="groupListLoading">
              <div
                v-for="index in [1, 2, 3, 4, 5]"
                :key="index"
                style="padding: 10px 30px;"
              >
                <span class="grp-lines loading-shimmer"></span>
              </div>
            </template>
            <template v-else>
              <div
                v-for="group in groupPermissions"
                :key="group.id"
                @click="activeGroupId = group.id"
                :class="['group-list', activeGroupId === group.id && 'active']"
              >
                <inline-svg
                  :src="getGroupIcon(group.iconType)"
                  :iconClass="
                    `icon group-icon-color ${getGroupIconClass(group.iconType)}`
                  "
                  class="mR20"
                ></inline-svg>
                <div>
                  <div class="group-name">{{ group.name }}</div>
                  <div class="group-tabs textoverflow-ellipsis">
                    {{ getTabNames(group.tabs) }}
                  </div>
                </div>
                <el-switch
                  v-model="group.groupPermission"
                  @change="toggleGroupPermissions(group)"
                  class="mL-auto"
                ></el-switch>
              </div>
            </template>
          </div>
        </template>
      </div>
      <div class="tab-permission-content">
        <div v-if="showEmptyState" class="empty-state-class">
          <inline-svg
            src="svgs/emptystate/history"
            iconClass="icon icon-xxxxlg"
          ></inline-svg>

          <template v-if="selectedLayout !== layoutTypes.DEFAULT_VIEW">
            <div v-if="$validation.isEmpty(groupPermissions)">
              No Groups available for
              {{ appName }}
            </div>
            <div
              v-else-if="$validation.isEmpty(tabListForGroup) && activeGroupId"
            >
              No Tabs associated for this group
            </div>
          </template>

          <div v-else>
            No Tabs available for
            {{ appName }}
          </div>
        </div>
        <div
          v-else-if="tabListLoading || groupListLoading"
          class="loading-state-class"
        >
          <spinner :show="true" size="80"></spinner>
        </div>
        <template v-else>
          <div
            v-if="selectedLayout !== layoutTypes.DEFAULT_VIEW"
            class="group-header-wrapper"
          >
            <div class="group-tabs-header">
              <div
                v-for="tab in tabListForGroup"
                :key="tab.id"
                :id="`${trimTabName(tab.name)}-link`"
                @click="scrollTo(trimTabName(tab.name))"
                class="selected-group-tab-name"
              >
                {{ tab.name }}
                <div class="border-highlight"></div>
              </div>
            </div>
          </div>
          <div class="tab-content-layout">
            <div
              v-for="tab in tabListForGroup"
              :key="tab.id"
              :id="`${trimTabName(tab.name)}-section`"
              class="pT20 pB20"
            >
              <div class="flex-middle">
                <div class="tab-name">{{ tab.name }}</div>
                <div class="mL-auto">
                  {{ tab.tabPermission ? 'Enabled' : 'Enable' }}
                </div>
                <el-switch
                  v-model="tab.tabPermission"
                  active-color="#ef4f8f"
                  @change="toggleTabPermissions(tab)"
                  class="mL10 mR20"
                ></el-switch>
              </div>
              <div class="tab-permissions">
                <template
                  v-for="(permissionVal,
                  permissionName,
                  index) in tab.permission"
                >
                  <div
                    class="tab-permission-list"
                    :key="`${permissionName}-${index}`"
                  >
                    <div class="tab-permission-name">
                      {{ permissionName }}
                    </div>
                    <el-switch
                      v-model="permissionVal.enabled"
                      @change="togglePermission(tab, permissionName)"
                      class="width50px mL20"
                    ></el-switch>
                    <div class="mL20">
                      <el-radio-group
                        v-if="
                          permissionVal.enabled && permissionVal.permissions
                        "
                        v-model="permissionVal.value"
                        class="fc-radio-btn"
                      >
                        <el-radio
                          v-for="(perm, permName) in permissionVal.permissions"
                          :key="perm.value"
                          :label="perm.value"
                        >
                          {{ permName }}
                        </el-radio>
                      </el-radio-group>
                    </div>
                  </div>
                  <template
                    v-if="permissionVal.enabled && permissionVal.subPermission"
                  >
                    <div
                      v-for="(subPermVal,
                      subPermName,
                      index) in permissionVal.subPermission"
                      :key="`${subPermName}-${index}`"
                      class="tab-permission-list"
                    >
                      <div class="tab-subpermission-name">
                        {{ subPermName }}
                      </div>
                      <el-switch
                        v-model="subPermVal.enabled"
                        class="mL20 tab-subpermission-padding"
                      ></el-switch>
                    </div>
                  </template>
                </template>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ErrorBanner from '@/ErrorBanner'
import ScrollHandlingMixin from './ScrollHandlingMixin'
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
  props: ['role', 'app'],
  components: { ErrorBanner },
  mixins: [ScrollHandlingMixin],

  data() {
    return {
      tabPermissions: [],
      groupPermissions: [],
      errorText: 'Please enter role name',
      error: false,
      saving: false,
      selectedLayout: 1,
      filterValue: null,
      iconClass: 'icon icon-xxxl p5 pointer',
      groupListLoading: false,
      tabListLoading: false,
      appLayouts: null,
      activeGroupId: null,
      layoutTypes,
      getGroupIcon,
      getGroupIconClass,
    }
  },

  computed: {
    appId() {
      return this.app.id
    },
    appName() {
      return this.app.name || 'this app'
    },
    filteredTabs() {
      let { filterValue, tabPermissions } = this

      if (!isEmpty(filterValue)) {
        return tabPermissions.filter(tab => {
          let tabName = tab.name.toLowerCase()
          return tabName.includes(filterValue.toLowerCase())
        })
      } else {
        return tabPermissions
      }
    },
    tabListForGroup() {
      let {
        activeGroupId,
        tabPermissions,
        selectedLayout,
        layoutTypes,
        groupPermissions,
      } = this

      if (
        selectedLayout !== layoutTypes.DEFAULT_VIEW &&
        !isEmpty(activeGroupId)
      ) {
        let { tabs } = groupPermissions.find(
          group => group.id === activeGroupId
        )
        return tabPermissions.filter(tab => !isEmpty(tabs[tab.id]))
      }
      return tabPermissions
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
    showEmptyState() {
      let {
        groupPermissions,
        tabListForGroup,
        selectedLayout,
        layoutTypes,
        tabListLoading,
        groupListLoading,
      } = this
      let defaultLayout = selectedLayout === layoutTypes.DEFAULT_VIEW

      if (defaultLayout && !tabListLoading) {
        return isEmpty(tabListForGroup)
      } else if (!defaultLayout && !groupListLoading) {
        return isEmpty(groupPermissions) || isEmpty(tabListForGroup)
      } else {
        return false
      }
    },
  },

  created() {
    this.init()
  },

  watch: {
    selectedLayout(value) {
      if (value !== this.layoutTypes.DEFAULT_VIEW) {
        this.constructGroupPermission()
        this.activeGroupId = this.$getProperty(
          this.groupPermissions,
          '0.id',
          null
        )
      } else {
        this.activeGroupId = null
      }
    },
    tabListForGroup(value) {
      this.sidebarElements = []
      this.sectionElements = []
      this.setElementsId()
      if (!isEmpty(value)) {
        this.$nextTick(() => {
          let tabName = this.trimTabName(value[0].name)
          this.scrollTo(tabName)
        })
      }
    },
    enabledLayouts(value) {
      let {
        layoutTypes: { DEFAULT_VIEW, WEB_VIEW, MOBILE_VIEW },
        selectedLayout,
      } = this

      if (selectedLayout === WEB_VIEW && !value[selectedLayout]) {
        if (!value[MOBILE_VIEW]) {
          this.selectedLayout = DEFAULT_VIEW
        } else {
          this.selectedLayout = MOBILE_VIEW
        }
      }
    },
  },

  methods: {
    async loadTabs() {
      let { error, data } = await loadAppTabs(this.appId)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.constructTabPermission(data)
      }
      this.tabListLoading = false
    },
    async loadAppLayouts() {
      let { error, data } = await loadLayouts(this.appId)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { layouts } = data || {}

        this.appLayouts = layouts
        this.constructGroupPermission()
        this.activeGroupId = this.$getProperty(
          this.groupPermissions,
          '0.id',
          null
        )
        this.$nextTick(this.registerScrollHandler)
      }
      this.groupListLoading = false
    },
    async init() {
      this.tabListLoading = true
      this.groupListLoading = true
      await this.loadTabs()
      await this.loadAppLayouts()
    },
    constructTabPermission(tabsList) {
      let { newPermissions } = this.role || {}
      let tabVsPerm = {}

      if (!isEmpty(newPermissions)) {
        tabVsPerm = newPermissions.reduce((tabPerm, perm) => {
          let { tabId, permission } = perm
          tabPerm[tabId] = permission
          return tabPerm
        }, {})
      }

      this.tabPermissions = tabsList.reduce((tabWithPermission, tab) => {
        let { permission, id, name } = tab
        let tabPermission = {
          id,
          name,
          tabPermission: !isEmpty(tabVsPerm[id]),
          permission: this.setTabPermissions(permission, tabVsPerm[id]),
        }

        tabWithPermission.push(tabPermission)
        return tabWithPermission
      }, [])
    },
    setTabPermissions(permission, tabPermissionCount = 0) {
      if (!isEmpty(permission)) {
        return permission.reduce((permissionObj, permName) => {
          let { actionName, displayName, value, permissions } = permName
          let tabPermValue = tabPermissionCount & value
          let enabled = tabPermValue !== 0 && tabPermValue === value
          let subPermName = ''

          if (actionName) {
            subPermName = actionName.toLowerCase().split('_')[0]
          }

          let parentPermName = Object.keys(permissionObj).find(
            perm => subPermName === perm.toLowerCase()
          )

          if (parentPermName) {
            permissionObj[parentPermName].subPermission = {
              ...permissionObj[parentPermName].subPermission,
              [displayName]: {
                actionName,
                value,
                enabled,
              },
            }
          } else {
            permissionObj[displayName] = {
              actionName,
              value,
              enabled,
            }

            if (!isEmpty(permissions)) {
              let permissionEnabled = {}
              let rolePermission = permissions.reduce((rolePerm, perm) => {
                let { actionName, displayName, value } = perm
                let tabPermValue = tabPermissionCount & value
                let enabled = tabPermValue !== 0 && tabPermValue === value

                if (enabled) {
                  permissionEnabled = { enabled, value }
                }
                rolePerm[displayName] = {
                  actionName,
                  value,
                }
                return rolePerm
              }, {})
              permissionObj[displayName] = {
                ...permissionObj[displayName],
                ...permissionEnabled,
                permissions: rolePermission,
              }
            }
          }

          return permissionObj
        }, {})
      }
    },
    constructGroupPermission() {
      let { webTabGroupList: groups } =
        this.appLayouts.find(
          view => view.layoutDeviceType === this.selectedLayout
        ) || {}

      if (!isEmpty(groups)) {
        let groupPermissions = groups.reduce((groupWithPermission, group) => {
          let { id, name, iconType, webTabs = [], order } = group
          let tabs = webTabs.reduce((groupTab, tab) => {
            groupTab[tab.id] = tab.name
            return groupTab
          }, {})
          let groupPermission = {
            id,
            name,
            iconType,
            tabs,
            order,
            groupPermission: this.checkGroupPermission(tabs),
          }

          groupWithPermission.push(groupPermission)
          return groupWithPermission
        }, [])
        this.groupPermissions = sortBy(groupPermissions, ['order'])
      } else {
        this.groupPermissions = []
      }
    },
    checkGroupPermission(tabs) {
      return this.tabPermissions.reduce((hasPerm, tab) => {
        let { id, tabPermission } = tab

        if (!isEmpty(tabs[id])) {
          hasPerm = hasPerm || tabPermission
        }
        return hasPerm
      }, false)
    },
    toggleGroupPermissions(group) {
      let { groupPermission, tabs } = group

      this.tabPermissions.forEach(tab => {
        if (!isEmpty(tabs[tab.id])) {
          tab.tabPermission = groupPermission
          this.toggleSubTabPermission(tab, groupPermission)
        }
      })
    },
    toggleTabPermissions(tab) {
      let { tabPermission, id } = tab

      this.toggleSubTabPermission(tab, tabPermission)
      this.toggleGroup(id)
    },
    togglePermission(tab, permissionName) {
      let { permission, id } = tab
      let isTabActive = Object.values(permission).reduce(
        (isPermEnabled, tabpermission) => {
          let { enabled } = tabpermission
          isPermEnabled = isPermEnabled || enabled
          return isPermEnabled
        },
        false
      )
      let { enabled, permissions, subPermission } = permission[permissionName]

      if (!isEmpty(permissions)) {
        if (enabled) {
          tab.permission[permissionName].value = permissions['All'].value //First role
        } else {
          tab.permission[permissionName].value = 0
        }
        if (!isEmpty(subPermission)) {
          Object.keys(subPermission).forEach(perm => {
            tab.permission[permissionName].subPermission[perm].enabled = enabled
          })
        }
      }
      tab.tabPermission = isTabActive
      this.toggleGroup(id)
    },
    toggleGroup(tabId) {
      let currentGroup = this.groupPermissions.find(
        group => !isEmpty(group.tabs[tabId])
      )

      if (!isEmpty(currentGroup)) {
        currentGroup.groupPermission = this.tabListForGroup.reduce(
          (isPermEnabled, tab) => {
            isPermEnabled = isPermEnabled || tab.tabPermission
            return isPermEnabled
          },
          false
        )
      }
    },
    toggleSubTabPermission(tab, enabledVal) {
      let { permission } = tab

      if (!isEmpty(permission)) {
        Object.values(permission).forEach(tabPerm => {
          let { permissions, subPermission } = tabPerm

          tabPerm.enabled = enabledVal
          if (!isEmpty(permissions)) {
            if (enabledVal) {
              tabPerm.value = permissions['All'].value //First role
            } else {
              tabPerm.value = 0
            }
            if (!isEmpty(subPermission)) {
              Object.keys(subPermission).forEach(perm => {
                tabPerm.subPermission[perm].enabled = enabledVal
              })
            }
          }
        })
      }
    },
    getTabNames(tabs) {
      return Object.values(tabs).reduce((tabNames, tab, index, tabs) => {
        let tabsLength = tabs.length

        tabNames += tab
        if (tabsLength !== index + 1) {
          tabNames += ', '
        }

        return tabNames
      }, '')
    },
    serialize() {
      let permissions = this.tabPermissions
        .map(tab => {
          let { id, tabPermission, permission } = tab

          if (tabPermission && permission) {
            let totalPermission = Object.values(permission).reduce(
              (permVal, perm) => {
                let { enabled, value, subPermission } = perm

                if (enabled) {
                  permVal += value
                  if (!isEmpty(subPermission)) {
                    permVal += Object.values(subPermission).reduce(
                      (subPermNum, subPerm) => {
                        let { enabled, value } = subPerm
                        if (enabled) subPermNum += value
                        return subPermNum
                      },
                      0
                    )
                  }
                }
                return permVal
              },
              0
            )

            let tabObj = { tabId: id, permission: totalPermission }
            return tabObj
          } else {
            return null
          }
        })
        .filter(tab => !isEmpty(tab))

      return permissions
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
    saveRole() {
      if (isEmpty(this.role.name)) {
        this.error = true
        return
      } else {
        this.error = false

        let url = '/setup/updateWebTabRole'
        let { name, description, roleId } = this.role
        let { appId } = this
        let role = { name, description, roleId }
        let newPermissions = this.serialize()

        let params = {
          role,
          roleApp: [{ applicationId: appId }],
          newPermissions,
        }

        this.saving = true
        API.post(url, params)
          .then(({ error }) => {
            if (error) {
              this.$message.error(error.message || 'Error occured while saving')
            } else {
              this.$message.success('Saved Successfully')
              this.$emit('onSave')
              this.closeDialog()
            }
          })
          .finally(() => {
            this.saving = false
          })
      }
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.webtab-role {
  .el-dialog__body {
    height: 100%;
  }
  .header-layout {
    padding: 15px 30px;
    border: solid 1px #e9ebef;
  }
  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .role-name {
    font-size: 20px;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .role-descrip {
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #808080;
    margin-top: 10px;
  }
  .fc-input-full-border-select2.role-name-input {
    .el-input__inner {
      font-size: 20px;
      letter-spacing: 0.5px;
      color: #324056;
      border: none !important;
    }
  }
  .fc-input-full-border-select2.role-descrip-input {
    .el-input__inner {
      border: none !important;
      font-size: 13px;
      letter-spacing: 0.5px;
      color: #808080;
    }
  }
  .tab-permission-layout {
    display: flex;
    height: 100%;
    padding-bottom: 130px;

    .tab-permission-sidebar {
      width: 30%;
      height: 100%;

      .sidebar-container {
        overflow: scroll;
        height: 100%;
      }
      .tab-lines {
        height: 15px;
        width: 100%;
        border-radius: 10px;
      }
      .grp-lines {
        height: 40px;
        width: 100%;
        border-radius: 10px;
      }
      .search-box {
        display: flex;
        padding: 5px 5px 5px 20px;
        border-bottom: 1px solid #e5e4e4;

        .el-input__inner {
          border: none;
        }
        .el-input__suffix {
          top: -5px;
        }
      }
      .tab-list-name {
        font-size: 14px;
        letter-spacing: 0.5px;
        color: #555555;
        padding: 11px 30px;
        border-left: 3px solid transparent;
        cursor: pointer;
      }
      .tab-list-name:hover {
        color: #555;
        background-color: #f3f4f7;
        opacity: 0.7;
      }
      .group-list {
        padding: 15px 20px;
        display: flex;
        align-items: center;
        border-left: 3px solid transparent;
        cursor: pointer;
      }
      .group-list:hover {
        background-color: #f3f4f7;
      }
      .group-icon-color {
        fill: #a9aacb;
      }
      .group-name {
        font-size: 11px;
        font-weight: bold;
        letter-spacing: 1px;
        color: #324056;
        margin-bottom: 8px;
        text-transform: uppercase;
      }
      .group-tabs {
        font-size: 11px;
        letter-spacing: 0.5px;
        color: #8ca1ad;
        margin: 0px 5px 5px 0px;
        max-width: 180px;
      }
      .group-tabs:last-of-type {
        .separation {
          display: none;
        }
      }
      .tab-list-name.active,
      .group-list.active {
        background-color: #f3f4f7;
        border-left: 3px solid #ef518f;
      }
      .tab-list-name.active:hover,
      .group-list.active:hover {
        border-left: 3px solid transparent;
      }
    }

    .tab-permission-content {
      width: 70%;
      height: 100%;
      background-color: #f8f9fa;

      .empty-state-class {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        height: 100%;
      }

      .loading-state-class {
        height: 100%;
        display: flex;
      }

      .group-header-wrapper {
        background-color: #fff;
        overflow-x: auto;

        .group-tabs-header {
          display: flex;
          width: max-content;
          overflow: scroll;
          padding-left: 30px;
          align-items: center;
          height: 100%;

          .selected-group-tab-name {
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 1px;
            color: #324056;
            margin-right: 50px;
            cursor: pointer;
            height: 100%;
            display: flex;
            flex-direction: column;
            padding-top: 20px;
            text-transform: uppercase;

            .border-highlight {
              border-bottom: 2px solid transparent;
              width: 30px;
              margin-top: 20px;
            }

            &.active {
              font-weight: bold;

              .border-highlight {
                border-bottom: 2px solid #ee518f;
              }
            }
          }
        }
      }
      .tab-content-layout {
        padding: 10px 30px 60px 30px;
        overflow: scroll;
        height: calc(100vh - 200px);

        .tab-name {
          font-size: 12px;
          font-weight: bold;
          letter-spacing: 1px;
          color: #ee518f;
          text-transform: capitalize;
        }
        .tab-permissions {
          margin-top: 20px;
          background-color: #fff;
          border: 1px solid #ececec;

          .tab-permission-list {
            margin: 0px 15px;
            display: flex;
            align-items: center;
            padding: 20px;
            border-bottom: 1px solid #f3f5f6;

            .tab-permission-name {
              font-size: 14px;
              font-weight: 500;
              letter-spacing: 0.5px;
              color: #324056;
              width: 200px;
              height: 22px;
            }
            .tab-subpermission-name {
              font-size: 14px;
              letter-spacing: 0.5px;
              color: #324056;
              padding-left: 10px;
              width: 200px;
            }
            .tab-subpermission-padding {
              padding-right: 250px;
            }
          }
        }
      }
    }
  }
}
</style>
