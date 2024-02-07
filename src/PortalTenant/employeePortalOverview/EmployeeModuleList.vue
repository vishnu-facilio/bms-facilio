<template>
  <EmployeePageLayout :moduleName="moduleName">
    <template slot="title">
      {{ moduleDisplayName }}
    </template>
    <template slot="header">
      <template v-if="listCount">
        <pagination :total="listCount" :perPage="50"></pagination>
        <span class="separator">|</span>
      </template>
      <el-tooltip
        effect="dark"
        :content="$t('common._common.sort')"
        placement="bottom"
      >
        <Sort
          :moduleName="moduleName"
          :key="moduleName + '-sort'"
          @sortChange="loadRecords"
        >
        </Sort>
      </el-tooltip>
      <span class="separator">|</span>
      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="bottom"
      >
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        >
        </AdvancedSearch>
      </el-tooltip>
      <!-- <span class="separator">|</span> -->
      <!-- <CreateButton :to="getCreationRoute()">
        {{ moduleDisplayName ? moduleDisplayName : '' }}
      </CreateButton> -->
    </template>
    <Tags
      :key="`ftags-list-${moduleName}`"
      :hideSaveView="true"
      :moduleName="moduleName"
    ></Tags>
    <div class="list-container" v-if="openModuleId === -1">
      <div v-if="showLoading" class="list-loading">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(moduleRecordList) && !showLoading"
        class="list-empty-state"
      >
        <inline-svg
          src="svgs/emptystate/workorder"
          iconClass="icon text-center icon-xxxxlg height-auto"
        ></inline-svg>
        <div class="line-height20 nowo-label">
          {{ $t('setup.setup.no') }}
          {{ moduleDisplayName ? moduleDisplayName.toLowerCase() : moduleName }}
          {{ $t('setup.setup.available') }}
        </div>
      </div>

      <template v-if="!showLoading && !$validation.isEmpty(moduleRecordList)">
        <div
          class="view-column-chooser"
          @click="showColumnSettings = true"
          v-show="false"
        >
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          />
        </div>
        <el-table
          :data="moduleRecordList"
          ref="tableList"
          class="width100"
          height="100%"
          :fit="true"
        >
          <el-table-column fixed prop label="ID" min-width="120">
            <template v-slot="data">
              <div class="fc-id" @click="openRecordSummary(data.row.id)">
                {{ '#' + data.row[idField] }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            fixed
            prop
            :label="nameColumn.displayName || 'NAME'"
            min-width="200"
          >
            <template v-slot="data">
              <div
                v-tippy
                @click="openRecordSummary(data.row.id)"
                small
                :title="
                  nameColumn
                    ? getColumnDisplayValue(nameColumn, data.row) || ''
                    : data.row.name
                "
                class="flex-middle"
              >
                <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                  {{
                    nameColumn
                      ? getColumnDisplayValue(nameColumn, data.row) || ''
                      : data.row.name
                  }}
                </div>
              </div>
            </template>
          </el-table-column>
          <template v-for="(field, index) in viewColumns">
            <el-table-column
              :align="checkDecimalType(field) ? 'right' : 'left'"
              :key="index"
              :prop="field.name"
              :label="field.displayName"
              min-width="200"
              v-if="!isFixedColumn(field.name) || field.parentField"
            >
              <template v-slot="data">
                <div v-if="$getProperty(field, 'field.displayType') === 'FILE'">
                  <div
                    v-if="!$validation.isEmpty(getFileName(field, data.row))"
                    @click="openAttachment(field, data.row)"
                    class="d-flex file-column"
                  >
                    <a class="truncate-text">
                      {{ getFileName(field, data.row) }}
                    </a>
                  </div>
                  <div v-else>---</div>
                </div>
                <div
                  v-else-if="!isFixedColumn(field.name) || field.parentField"
                >
                  <div
                    class="table-subheading"
                    :class="{
                      'text-right': checkDecimalType(field),
                    }"
                  >
                    {{ getColumnDisplayValue(field, data.row) || '---' }}
                  </div>
                </div>
              </template>
            </el-table-column>
          </template>
          <el-table-column
            v-if="hasActionPermissions"
            width="130"
            class="visibility-visible-actions"
            fixed="right"
          >
            <template v-slot="data">
              <div v-if="canShowActions(data.row)" class="text-center">
                <i
                  v-if="canShowEdit"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  v-tippy
                  @click="editRecord(data.row)"
                ></i>
                <i
                  v-if="canShowDelete"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  v-tippy
                  @click="deleteRecord(data.row.id)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </div>

    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      :columnConfig="columnConfig"
      :viewName="currentView"
    ></column-customization>

    <preview-file
      v-if="showPreview && selectedFile"
      :visibility.sync="showPreview"
      :previewFile="selectedFile"
      :files="[selectedFile]"
    ></preview-file>
  </EmployeePageLayout>
</template>
<script>
import EmployeePageLayout from 'src/PortalTenant/employeePortalOverview/EmployeePageLayout'
import Pagination from 'src/components/list/FPagination'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { mapGetters, mapState } from 'vuex'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import PreviewFile from '@/PreviewFile'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import { isDecimalField } from '@facilio/utils/field'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Tags from 'newapp/components/search/FTags'
import Sort from '../components/Sort'

export default {
  mixins: [ViewMixinHelper],
  components: {
    ColumnCustomization,
    EmployeePageLayout,
    Pagination,
    AdvancedSearch,
    Tags,
    PreviewFile,
    Sort,
  },
  data() {
    return {
      moduleRecordList: [],
      listCount: null,
      loading: true,
      fetchingMore: false,
      showColumnSettings: false,
      defaultColumnConfig: {
        fixedColumns: ['id', 'name'],
        availableColumns: [],
        showLookupColumns: false,
      },
      tableLoading: false,
      selectedFile: {},
      showPreview: false,
    }
  },
  computed: {
    ...mapState({
      viewLoading: state => state.view.isLoading,
      viewFields: state => {
        return state.view.currentViewDetail
          ? state.view.currentViewDetail.fields
          : []
      },
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    moduleName() {
      return this.$attrs.moduleName
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName ? this.metaInfo.displayName : ''
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
      return {}
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
    checkDecimalType(fieldObj) {
      let { field } = fieldObj || {}
      return !isEmpty(field) ? isDecimalField(field) : false
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
    getCreationRoute() {
      let { moduleName } = this
      let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

      return name ? { name } : null
    },
    editRecord(record) {
      let id = record.id
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
