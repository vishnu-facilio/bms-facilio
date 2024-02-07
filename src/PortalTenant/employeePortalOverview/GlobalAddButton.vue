<template>
  <div>
    <el-dropdown
      trigger="click"
      type="primary"
      @click="handleClick"
      @command="handleMethod"
      class="fc-employee-portal-add-btn relative"
    >
      <!-- <div> -->
      <!-- <div class="emp-portal-header-btn-conatiner-hover"></div> -->

      <el-button type="primary" class="relative">
        <InlineSvg
          src="svgs/employeePortal/ic_add"
          iconClass="icon icon-xs vertical-middle"
        ></InlineSvg>
      </el-button>
      <!-- </div> -->

      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="(action, index) in dropdownAction"
          :key="index"
          :command="action.currentRoute"
          class="d-flex dropdownAction"
          ><div v-if="action.show" class="rotate-div">
            <InlineSvg
              :src="action.icon"
              iconClass="icon icon-xs vertical-middle"
            ></InlineSvg>
          </div>
          <div class="mL10">{{ action.name }}</div></el-dropdown-item
        >
      </el-dropdown-menu>
    </el-dropdown>
    <VisitsAndInvitesForm
      v-if="showFormVisibility"
      :moduleName="localModuleName"
      :formMode="formMode"
      @onClose="closeVisitorTypeSelect"
    ></VisitsAndInvitesForm>
  </div>
</template>
<script>
import ViewMixinHelper from '@/mixins/ViewMixin'
import { mapGetters, mapState } from 'vuex'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import sortBy from 'lodash/sortBy'
import VisitsAndInvitesForm from 'src/pages/visitors/visits/VisitsAndInvitesForm.vue'

export default {
  mixins: [ViewMixinHelper],
  components: {
    VisitsAndInvitesForm,
  },
  data() {
    return {
      localModuleName: null,
      moduleRecordList: [],
      visitsModuleName: null,
      formMode: 'single',
      showFormVisibility: false,
      changeDisplayName: {
        spacebooking: 'Booking',
        serviceRequest: 'Requests',
        invitevisitor: 'Visitors',
      },
      excludeDropdownAction: {
        visitorlog: 'visitorlog',
      },
    }
  },

  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    currentCreationRoute() {
      return this.getCurrentCreationRoute()
    },
    hasPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('CREATE', currentTab)
    },
    buttonDisplayName() {
      let { currentTab } = this
      let { configJSON } = currentTab
      let { actionName } = configJSON || {}

      return actionName
    },
    moduleName() {
      let { modules } = this.currentTab
      if (modules && modules.length) {
        let moduleObject = modules[0]
        return moduleObject?.name ? moduleObject.name : null
      }
      return null
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    currentViewFields() {
      return this.viewFields
    },
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    openModuleId() {
      if (this.$attrs.id) {
        return parseInt(this.$attrs.id)
      }
      return -1
    },
    currentView() {
      return this.$attrs.viewname
    },
    page() {
      return this.$route.query.page || 1
    },
    filters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    title() {
      return this.moduleDisplayName
    },
    isV3Api() {
      return true
    },
    currentTabConfig() {
      let { configJSON } = this.currentTab
      return isEmpty(configJSON) ? {} : configJSON
    },
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column =>
          this.$getProperty(column, 'field.mainField')
        )
      }
      return null
    },
    idField() {
      return 'id'
    },
    canShowEdit() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    canShowDelete() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
    hasActionPermissions() {
      let { canShowEdit, canShowDelete } = this
      return canShowEdit || canShowDelete
    },
    ...mapGetters('webtabs', [
      'getTabGroups',
      'isAppPrefEnabled',
      'tabHasPermission',
    ]),
    ...mapState('webtabs', ['selectedTabGroup']),
    webTabsList() {
      let groups = sortBy(this.getTabGroups(), ['order'])

      let webtabGroupList = groups
        .map(group => {
          return {
            ...group,
            webTabs: sortBy(group.webTabs || [], ['order']),
          }
        })
        .filter(group => !isEmpty(group.webTabs))
      let webTabsList = []
      webtabGroupList.forEach(webTabGroup => {
        webTabsList.push(...webTabGroup.webTabs.map(rt => rt))
      })

      return webTabsList
    },
    dropdownAction() {
      let list = []
      let { webTabsList } = this
      if (!isEmpty(webTabsList)) {
        webTabsList.forEach(tab => {
          if (tab.modules !== null) {
            let { modules } = tab
            if (modules?.length > 0) {
              let moduleName = modules[0].name
              let moduleDisplayName = modules[0].displayName || tab.name
              if (!this.isDuplicateTab(moduleName, list)) {
                if (!this.excludeDropdownAction[moduleName]) {
                  list.push({
                    name: this.changeDisplayName[moduleName]
                      ? this.changeDisplayName[moduleName]
                      : `${moduleDisplayName}`,
                    moduleName: moduleName,
                    currentRoute: this.getCreationRoute(moduleName),
                    icon: 'svgs/employeePortal/ic_add',
                    show: this.tabHasPermission('CREATE', tab),
                  })
                }
              }
            }
          }
        })
      }
      return list
    },

    columnConfig: {
      set(value) {
        this.defaultColumnConfig = value
      },
      get() {
        let { defaultColumnConfig, nameColumn } = this || {}
        let fixedColumns = this.$getProperty(
          this,
          'defaultColumnConfig.fixedColumns',
          []
        )
        if (!isEmpty(nameColumn)) {
          let { name } = nameColumn || {}
          fixedColumns = [...fixedColumns, name]
        }
        return { ...defaultColumnConfig, fixedColumns }
      },
    },
  },
  watch: {
    moduleName: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          this.$store.dispatch('loadTicketStatus', this.moduleName)
          this.$store
            .dispatch('view/loadModuleMeta', this.moduleName)
            .catch(() => {})
        }
      },
      immediate: true,
    },
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    currentView: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.getViewDetail()
          this.loadRecords()
        }
      },
      immediate: true,
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadRecords()
      }
    },
    filters() {
      this.loadRecords()
    },
  },
  methods: {
    isDuplicateTab(moduleName, list) {
      let tabIndex = list.findIndex(rt => rt.moduleName === moduleName)
      if (tabIndex > -1) {
        return true
      }
      return false
    },
    closeVisitorTypeSelect() {
      this.showFormVisibility = false
    },
    handleMethod(command) {
      let findRouterObj = this.dropdownAction.find(
        rt => rt.currentRoute === command
      )
      if (
        findRouterObj?.moduleName &&
        ['invitevisitor', 'visitorlog', 'groupinvite'].includes(
          findRouterObj.moduleName
        )
      ) {
        if (findRouterObj.moduleName == 'groupinvite') {
          this.localModuleName = 'invitevisitor'
          this.showFormVisibility = true
          this.formMode = 'bulk'
        } else {
          this.localModuleName = findRouterObj.moduleName
          this.showFormVisibility = true
          this.formMode = 'single'
        }
      } else {
        this.$router.push(command)
      }
    },
    handleClick() {
      if (this.currentCreationRoute === null) {
        return
      }
      if (
        this.moduleName &&
        ['invitevisitor', 'visitorlog'].includes(this.moduleName)
      ) {
        this.showFormVisibility = true
      } else {
        if (!isEmpty(this.getCurrentCreationRoute())) {
          this.$router.push(this.getCurrentCreationRoute())
        }
      }

      this.$emit('click')
    },
    canShowActions(record) {
      let { moduleName } = this
      return !isRecordLocked(record, moduleName) && !isRequestedState(record)
    },
    getViewDetail() {
      if (this.currentView) {
        this.$store.dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.moduleName,
        })
      }
    },
    refreshList() {
      this.loadRecords()
    },

    async loadRecords(force = false) {
      let { filters, currentView, moduleName, page, includeParentFilter } = this
      if (isEmpty(currentView)) return

      this.loading = true
      let { list, meta: { pagination = {} } = {}, error } = await API.fetchAll(
        moduleName,
        {
          viewName: currentView,
          page,
          perPage: 50,
          withCount: true,
          filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
          includeParentFilter,
        },
        { force }
      )

      if (error) {
        this.$message.error(error.message || 'Could not fetch list')
      } else {
        this.moduleRecordList = list
        this.listCount = pagination.totalCount || null
      }

      this.loading = false
    },
    async deleteRecord(id) {
      let { moduleName, moduleDisplayName } = this

      let value = await this.$dialog.confirm({
        title: `Delete ${moduleDisplayName}`,
        message: `Are you sure you want to delete this ${moduleDisplayName}?`,
        rbDanger: true,
        rbLabel: 'Delete',
      })

      if (!value) {
        return
      }

      let { error } = await API.deleteRecord(moduleName, id)

      if (!error) {
        this.$message.success(this.$t('Deleted Successfully'))
        this.loadRecords()
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }
    },
    openRecordSummary(id) {
      let { hideSummary = false } = this.currentTabConfig
      if (hideSummary) {
        return
      }

      let { moduleName, currentView } = this
      let route = findRouteForModule(moduleName, pageTypes.OVERVIEW)
      if (route) {
        this.$router.push({
          name: route.name,
          params: { viewname: currentView, id },
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    getCurrentCreationRoute() {
      let { moduleName } = this
      let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

      return name ? { name } : null
    },
    getCreationRoute(moduleName) {
      let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

      return name ? { name } : null
    },

    editRecord(id) {
      let { moduleName } = this
      let route = findRouteForModule(moduleName, pageTypes.EDIT)

      this.$router.push({
        name: route.name,
        params: { id },
      })
    },
    getFileName(field, record) {
      let { $getProperty } = this

      return $getProperty(record, `${field.name}FileName`, null)
    },
    openAttachment(field, record) {
      let data = record

      this.selectedFile = {
        contentType: data[`${field.name}ContentType`],
        fileName: data[`${field.name}FileName`],
        downloadUrl: data[`${field.name}DownloadUrl`],
        previewUrl: data[`${field.name}Url`],
      }
      this.showPreview = true
    },
  },
}
</script>
<style scoped>
.el-dropdown-menu {
  margin-top: 0 !important;
}
.emp-portal-header-btn-conatiner-hover {
  background: transparent;
  position: absolute;
  padding: 18px;
  right: -7px;
  top: 14px;
  border-radius: 18px;
}
.fc-employee-portal-add-btn:hover .emp-portal-header-btn-conatiner-hover {
  background: rgba(202, 212, 216, 0.3);
  -webkit-transition: 0.2s all;
  transition: 0.2s all;
}
.fc-employee-portal-add-btn .el-button-group > .el-button:first-child {
  font-size: 13px;
  padding: 6px 14px;
  border-radius: 3px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.fc-employee-portal-add-btn .el-button-group .el-button--primary:last-child {
  font-size: 13px;
  padding: 6px 3px;
  border-radius: 3px;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.fc-employee-portal-add-btn .el-button-group > .el-button:active,
.fc-employee-portal-add-btn .el-button-group > .el-button:hover {
  background: #0d69f1 !important;
  border-color: #0d69f1;
}
.fc-employee-portal-home-header > .el-button-group .el-button:active {
  background: #0d69f1 !important;
  border-color: #0d69f1;
}
</style>
<style lang="scss">
.fc-employee-portal-add-btn .el-button-group .el-button:active {
  background: #0d69f1 !important;
}
.disablebtn {
  .el-button.el-button--primary.el-button--small:first-child {
    cursor: not-allowed;
    opacity: 0.7;
  }
}
.fc-employee-portal-add-btn {
  .el-button {
    font-size: 11px;
    letter-spacing: 0.7px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: normal;
    background-color: #0053cc;
    border: 1px solid #0053cc;
    cursor: pointer;
    width: 22px;
    height: 22px;
    padding: 0px;
    border-radius: 2px;

    color: #fff;
    background-color: #0053cc;
    border: 1px solid #0053cc;
    cursor: pointer;
    &:active {
      background-color: #0d69f1;
      background: #0d69f1 !important;
      border-color: #0d69f1;
    }
    &:hover {
      background-color: #0d69f1;
      border-color: #0d69f1;
    }
    &:focus {
      background-color: #0d69f1;
      background: #0d69f1 !important;
      border-color: #0d69f1;
    }
  }
  .el-dropdown__caret-button {
    background: #5e60eb;
  }
  .el-dropdown__caret-button::before {
    display: none;
  }
  .el-button-group .el-button:active {
    background-color: #0d69f1 !important;
    border-color: #0d69f1;
  }
}
</style>
