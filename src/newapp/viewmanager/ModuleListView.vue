<template>
  <div class="overflow-y-scroll pB50" style="height: calc(100vh - 200px)">
    <div v-if="isLoading">
      <div
        v-for="index in 3"
        :key="index"
        class="fc__white__bg__asset d-flex width100 height50 mB10"
      >
        <div class="fL width85 self-center">
          <div class="fc-animated-background p10 width140px"></div>
        </div>
        <div class="fR self-center">
          <div class="fc-animated-background p10 width140px"></div>
        </div>
      </div>
    </div>
    <div
      v-else-if="$validation.isEmpty(foldersList)"
      class="vm-empty-state-container"
    >
      <inline-svg
        src="svgs/no-configuration"
        class="d-flex module-view-empty-state"
        iconClass="icon"
      ></inline-svg>
      <div class="fc-black-dark f18 bold">
        {{ $t('viewsmanager.list.no_view_config') }}
      </div>
    </div>
    <draggable
      v-else
      v-model="foldersList"
      group="viewGroups"
      @change="onGroupChange()"
    >
      <div class="mB10" v-for="(group, index) in foldersList" :key="index">
        <el-collapse v-model="expandedFolders" class="folder-collapse">
          <el-collapse-item
            :name="group.name"
            class="manager-views-item visibility-visible-actions"
          >
            <template slot="title">
              <div class="d-flex width100">
                <inline-svg
                  src="svgs/drag-and-drop"
                  class="d-flex self-center mR30 cursor-drag"
                  iconClass="icon fill-lite-grey pointer"
                ></inline-svg>
                <div class="d-flex self-center">
                  <inline-svg
                    src="svgs/folder"
                    class="d-flex mR10 self-center fill-grey"
                    iconClass="icon pointer"
                  ></inline-svg>
                  <div class="text-uppercase">
                    {{ group.displayName || group.name }}
                  </div>
                </div>
                <div @click.stop class="view-group-actions mL-auto">
                  <el-tooltip
                    :open-delay="700"
                    effect="dark"
                    :content="$t('common._common.edit')"
                    placement="top"
                  >
                    <fc-icon
                      @click="openFolderCreationDialog(group)"
                      class="pointer"
                      group="edit"
                      name="edit-line"
                      size="18"
                      color="#324056"
                    ></fc-icon>
                  </el-tooltip>
                  <div class="view-group-actions-dropdown">
                    <el-tooltip
                      :open-delay="700"
                      effect="dark"
                      :content="$t('common._common.more')"
                      placement="top"
                    >
                      <el-dropdown
                        v-if="$validation.isEmpty(group.views)"
                        trigger="click"
                        @command="
                          action => handleGroupCommand(action, group, index)
                        "
                      >
                        <span class="el-dropdown-link pointer">
                          <fc-icon
                            group="action"
                            name="options-vertical"
                            size="18"
                            color="#324056"
                          ></fc-icon>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item command="delete">
                            {{ $t('common._common.delete') }}
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </el-tooltip>
                  </div>
                </div>
              </div>
            </template>
            <draggable
              v-model="group.views"
              v-bind="viewsDragOptions"
              @change="onViewsChange(group, ...arguments)"
            >
              <div
                v-for="(view, viewIndex) in group.views"
                class="views-item"
                :key="viewIndex"
              >
                <inline-svg
                  src="svgs/drag-and-drop"
                  class="d-flex self-center mR30 cursor-drag"
                  iconClass="icon fill-lite-grey"
                ></inline-svg>
                <inline-svg
                  src="svgs/views-list"
                  class="d-flex self-center mR20 cursor-drag"
                  iconClass="icon icon-sm views-list"
                ></inline-svg>
                <div
                  class="f12 bold letter-spacing1 text-uppercase self-center"
                >
                  {{ view.displayName }}
                </div>
                <div class="d-flex self-center align-center mL-auto mR33">
                  <el-tooltip
                    effect="dark"
                    :content="$t('common._common.status')"
                    placement="top"
                    :open-delay="700"
                  >
                    <el-switch
                      v-model="view.status"
                      active-color="#3ab2c2"
                      :width="36"
                      @change="changeViewStatus(view)"
                    >
                    </el-switch>
                  </el-tooltip>
                  <div class="view-actions">
                    <el-tooltip
                      v-if="view.isEditable"
                      :open-delay="700"
                      effect="dark"
                      :content="$t('common._common.edit')"
                      placement="top"
                    >
                      <fc-icon
                        @click="openViewCreation(view.name)"
                        class="pointer"
                        group="edit"
                        name="edit-line"
                        size="18"
                        color="#324056"
                      ></fc-icon>
                    </el-tooltip>
                    <div class="view-actions-dropdown">
                      <el-tooltip
                        v-if="!isDefaultView(view)"
                        :open-delay="700"
                        effect="dark"
                        :content="$t('common._common.more')"
                        placement="top"
                      >
                        <el-dropdown
                          trigger="click"
                          @command="
                            action =>
                              handleCommand(action, view, index, viewIndex)
                          "
                        >
                          <span class="el-dropdown-link pointer">
                            <fc-icon
                              group="action"
                              name="options-vertical"
                              size="18"
                              color="#324056"
                            ></fc-icon>
                          </span>
                          <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="clone">
                              {{ $t('viewsmanager.view_clone.clone') }}
                            </el-dropdown-item>
                            <el-dropdown-item
                              command="delete"
                              v-if="view.isEditable"
                            >
                              {{ $t('common._common.delete') }}
                            </el-dropdown-item>
                          </el-dropdown-menu>
                        </el-dropdown>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
              </div>
            </draggable>
          </el-collapse-item>
        </el-collapse>
      </div>
    </draggable>
    <SelectAppViewClone
      v-if="showCloneAppDialog"
      :apps="apps"
      :currentAppId="appId"
      @save="cloneViewForApp"
      @close="showCloneAppDialog = false"
    >
    </SelectAppViewClone>
    <FolderCreation
      v-if="showFolderCreationDialog"
      :moduleName="moduleName"
      :groupDetail="selectedGroup"
      :appId="appId"
      @onClose="showFolderCreationDialog = false"
      @onSave="loadGroupViewsList"
    ></FolderCreation>
    <portal to="view-manager-actions">
      <div class="btn-container">
        <el-select
          @change="loadGroupViewsList"
          v-model="appId"
          placeholder="Select App"
          filterable
          class="fc-input-full-border2 width200px pR15"
        >
          <el-option
            v-for="app in apps"
            :key="app.linkName"
            :label="app.name"
            :value="app.id"
          >
          </el-option>
        </el-select>
        <el-button
          type="primary"
          class="manager-secondary-btn ml-add-btn-label"
          @click="openViewCreation()"
        >
          <span class="btn-label">{{ $t('viewsmanager.list.add_view') }}</span>
        </el-button>
      </div>
    </portal>
  </div>
</template>
<script>
import draggable from 'vuedraggable'
import isEqual from 'lodash/isEqual'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { mapGetters, mapActions } from 'vuex'
import FolderCreation from 'src/newapp/viewmanager/FolderCreation.vue'
import SelectAppViewClone from './SelectAppViewClone'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { findRouterForModuleInApp } from './routeUtil'

export default {
  props: ['moduleName'],
  components: { draggable, FolderCreation, SelectAppViewClone },

  data() {
    return {
      cloningViewName: null,
      showCloneAppDialog: false,
      viewCloneAppId: null,
      isLoading: false,
      appId: null,
      apps: [],
      selectedGroup: null,
      showFolderCreationDialog: false,
      foldersList: [],
      expandedFolders: [],
      viewsDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'viewItem',
        sort: true,
      },
    }
  },

  created() {
    this.availableApps()
  },

  computed: {
    ...mapGetters('view', ['getViewsFolderList']),

    groupViewsList() {
      return cloneDeep(this.getViewsFolderList())
    },
    currentSelectedApp() {
      let { query } = this.$route || {}
      let { appId } = query || {}
      return !isEmpty(appId) ? parseInt(appId) : (getApp() || {}).id
    },
  },

  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.loadGroupViewsList()
        }
      },
      immediate: true,
    },
    groupViewsList: {
      handler(newVal) {
        if (newVal) {
          this.foldersList = newVal
          this.expandedFolders = [
            ...this.expandedFolders,
            ...newVal.filter(grp => isEmpty(grp.views)).map(grp => grp.name),
          ]
        }
      },
      immediate: true,
    },
    appId(newVal) {
      if (this.currentSelectedApp !== newVal) {
        this.expandedFolders = []
        this.$router.push({ query: { appId: newVal } })
      }
    },
  },

  methods: {
    ...mapActions({
      customizeFolders: 'view/customizeFolders',
      customizeViewsList: 'view/customizeViews',
      deleteFolder: 'view/deleteFolder',
      viewDelete: 'view/deleteView',
    }),
    async cloneViewForApp(cloneAppId) {
      this.isLoading = true
      let { cloningViewName } = this
      this.viewCloneAppId = cloneAppId
      await this.openViewCreation(cloningViewName)
      this.isLoading = false
    },
    handleCommand(action, view, index, viewIndex) {
      if (action === 'clone') {
        this.cloningViewName = view.name
        this.showCloneAppDialog = true
      } else if (action === 'delete') {
        let { id } = view
        this.deleteView(id, index, viewIndex)
      }
    },
    isDefaultView(view) {
      let { name } = view || {}
      let checkViewIsDefault = !isEmpty(name)
        ? name.includes('maintenance_')
        : false // temporary handling - to be removed
      return view.default || checkViewIsDefault
    },
    changeViewStatus({ id, status }) {
      let params = {
        id,
        status,
      }
      let url = 'v2/views/editStatus'
      let { error } = API.post(url, params)
      if (error) {
        this.$message.error(
          this.$t('viewsmanager.view_status.error_while_editing_view_status')
        )
      } else {
        this.$message.success(
          status
            ? this.$t('viewsmanager.view_status.view_enabled_successfully')
            : this.$t('viewsmanager.view_status.view_disabled_successfully')
        )
      }
    },
    async availableApps() {
      let { moduleName, currentSelectedApp } = this
      let { data, error } = await API.get('v2/application/fetchList', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.apps = data.application || []
        this.appId = currentSelectedApp

        if (isEmpty(this.appId)) {
          let defaultApp =
            this.apps.find(app => app.linkName === 'newapp') || this.apps[0]
          this.appId = defaultApp.id
        }
      }
    },
    async loadGroupViewsList() {
      let { moduleName, appId, currentSelectedApp } = this
      let data = {
        moduleName,
        appId: appId || currentSelectedApp,
        restrictPermissions: true,
        fromBuilder: true,
      }

      this.isLoading = true
      await this.$store.dispatch('view/loadGroupViews', data)
      this.isLoading = false
    },
    openFolderCreationDialog(group = null) {
      this.selectedGroup = group
      this.showFolderCreationDialog = true
    },
    openViewCreation(viewname = null) {
      let { moduleName, appId, viewCloneAppId } = this
      let findRouteMethod = isWebTabsEnabled()
        ? findRouteForModule
        : findRouterForModuleInApp
      let { name } = findRouteMethod(moduleName, pageTypes.VIEW_CREATION) || {}

      if (!name) return

      if (viewname && viewCloneAppId) {
        this.$router.push({
          name,
          params: { moduleName },
          query: { appId, viewCloneAppId, viewname },
        })
      } else if (viewname) {
        this.$router.push({
          name,
          params: { moduleName, viewname },
          query: { appId },
        })
      } else {
        this.$router.push({
          name,
          params: { moduleName },
          query: { appId },
        })
      }
    },
    onGroupChange(groupViewsList) {
      if (isEmpty(groupViewsList)) {
        groupViewsList = cloneDeep(this.foldersList)
      }
      groupViewsList.forEach((group, index) => {
        group.sequenceNumber = ++index
      })
      this.customizeGroups(groupViewsList)
    },
    async customizeGroups(groupViewsList) {
      let { moduleName, appId } = this
      let params = {
        viewGroupsList: groupViewsList,
        moduleName,
        appId,
      }
      let { error } = await this.customizeFolders(params)

      if (error) {
        this.$message.error(
          error.message || this.$t(`viewsmanager.customize.error`)
        )
      } else {
        this.$message.success(this.$t(`viewsmanager.customize.updated`))
      }
    },
    handleGroupCommand(action, group, index) {
      if (action === 'delete') {
        this.deleteGroup(group, index)
      }
    },
    async deleteGroup(group, index) {
      let value = await this.$dialog.confirm({
        title: this.$t(`viewsmanager.views.delete_group`),
        message: this.$t(`viewsmanager.views.delete_group_confirmation`),
        rbDanger: true,
        rbLabel: this.$t(`viewsmanager.views.delete`),
      })
      if (!value) return

      let { id } = group || {}
      let params = { viewGroupData: { viewGroup: { id } }, index }
      let { data, error } = await this.deleteFolder(params)

      if (error) {
        this.$message.error(
          data.message || this.$t(`viewsmanager.views.delete_group_error`)
        )
      } else {
        this.$message.success(this.$t(`viewsmanager.views.delete_group_sucess`))
      }
    },
    onViewsChange(group, props) {
      let { added, moved } = props
      if (!isEmpty(added)) {
        // to handle the swap across folders
        let { foldersList } = this
        let clonedFoldersList = cloneDeep(foldersList)
        let currentGroupIndex = clonedFoldersList.findIndex(
          folder => folder.id === group.id
        )
        let serializedGroupViews = group.views.map((view, index) => {
          view.sequenceNumber = index + 1
          return view
        })

        clonedFoldersList[currentGroupIndex].views = serializedGroupViews
        this.onGroupChange(clonedFoldersList)
      } else if (!isEmpty(moved)) {
        let views = group.views.map((view, index) => {
          let { name, id, type } = view || {}
          return { id, sequenceNumber: index + 1, name, type }
        })
        this.customizeViews(views)
      }
    },
    async customizeViews(views) {
      let { moduleName, appId } = this
      let { type } = views[0] || {}
      let params = {
        views: views,
        moduleName,
        groupStatus: true,
        appId,
        groupType: type,
        viewType: type,
        fromBuilder: true,
      }
      let { error } = await this.customizeViewsList(params)

      if (error) {
        this.$message.error(
          error.message || this.$t(`viewsmanager.customize.error`)
        )
      } else {
        this.$message.success(this.$t(`viewsmanager.customize.updated`))
      }
    },
    async deleteView(id, index, viewIndex) {
      let value = await this.$dialog.confirm({
        title: this.$t(`viewsmanager.views.delete_view`),
        message: this.$t(`viewsmanager.views.delete_view_confirmation`),
        rbDanger: true,
        rbLabel: this.$t(`viewsmanager.views.delete`),
      })

      if (!value) return

      let params = { viewData: { id }, index, viewIndex }
      let { error } = await this.viewDelete(params)

      if (error) {
        this.$message.error(
          error.message || this.$t(`viewsmanager.views.delete_view_error`)
        )
      } else {
        this.$message.success(this.$t(`viewsmanager.views.delete_view_sucess`))
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.ml-add-btn-label {
  background-color: #39b2c2 !important;
}
</style>
<style lang="scss">
.vm-empty-state-container {
  align-items: center;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
  overflow: auto;
  height: 100%;

  .module-view-empty-state svg.icon {
    width: 150px;
    height: 150px;
  }
}
.manager-views-item {
  .mR33 {
    margin-right: 33px;
  }
  .view-group-actions {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    visibility: hidden;
    line-height: 0px;
    margin-right: 20px;
    .el-tooltip {
      height: 18px;
      margin-right: 20px;
    }
    .view-group-actions-dropdown {
      width: 20px;
      height: 18px;
      .el-tooltip {
        height: 18px;
        margin-right: 20px;
      }
    }
  }
  .el-collapse-item__header:hover {
    .view-group-actions {
      visibility: visible;
    }
  }
  .view-actions {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    visibility: hidden;
    margin-right: 20px;
    .el-tooltip {
      height: 18px;
      margin-right: 20px;
    }
    .view-actions-dropdown {
      width: 20px;
      height: 18px;
      .el-tooltip {
        height: 18px;
        margin-right: 20px;
      }
    }
  }

  .views-item:hover {
    .view-actions {
      visibility: visible;
    }
  }
  .views-item {
    .el-switch {
      margin-right: 20px;
      .el-switch__core {
        width: 36px;
        height: 16px;
      }
      .el-switch__core::after {
        width: 12px;
        height: 12px;
      }
    }
    .el-switch.is-checked .el-switch__core::after {
      left: 100%;
      margin-left: -13px;
      width: 12px;
      height: 12px;
    }
  }
  .el-dropdown {
    height: 18px;
  }
}
</style>
