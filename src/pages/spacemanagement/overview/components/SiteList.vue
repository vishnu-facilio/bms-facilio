<template>
  <div class="d-flex flex-col" style="overflow-y: scroll;">
    <div class="site-list-common-layout">
      <CommonListLayout
        :moduleName="currentModule"
        :showViewRearrange="false"
        :showViewEdit="true"
        :visibleViewCount="1"
        :getPageTitle="() => `All ${moduleDisplayName}`"
        :pathPrefix="
          this.currentModule == 'site'
            ? '/app/home/portfolio/sites/'
            : '/app/home/portfolio/buildings/'
        "
        :key="currentModule + '-list-layout'"
        class="portfolio-home-layout"
        @resizeList="resizeList"
      >
        <template #header style="z-index:-1">
          <template>
            <div class="flex-middle mT10">
              <pagination
                :currentPage.sync="page"
                :total="listCount"
                :perPage="50"
                class="fc-black-small-txt-12"
              ></pagination>
            </div>
            <span class="separator">|</span>
          </template>

          <el-tooltip
            effect="dark"
            :content="$t('common._common.search')"
            placement="left"
          >
            <AdvancedSearch
              :key="`${currentModule}-search`"
              :moduleName="currentModule"
              :moduleDisplayName="moduleDisplayName"
            >
            </AdvancedSearch>
          </el-tooltip>
        </template>
        <template #content>
          <div style="z-index:1;left:0;">
            <div>
              <FTags :key="`ftags-list-${currentModule}`"></FTags>
            </div>
            <div>
              <div v-if="loading">
                <spinner :show="true" size="80"></spinner>
              </div>
              <div
                v-else-if="$validation.isEmpty(moduleRecordList)"
                class="height300"
              >
                <div
                  class="flex-middle justify-content-center wo-flex-col height100 flex-direction-column"
                >
                  <inline-svg
                    :src="`svgs/emptystate/readings-empty`"
                    iconClass="icon text-center icon-xxxlg"
                  ></inline-svg>
                  <div class="pT20 fc-black-dark f18 bold">
                    No {{ moduleDisplayName }} available.
                  </div>
                </div>
              </div>
              <div
                v-else
                class="fc-list-view fc-list-table-container site-list-table-container"
                :class="resizeSiteClass"
              >
                <el-table
                  :data="moduleRecordList"
                  height="100%"
                  style="width: 100%"
                  ref="fc-table-list fc-site-table"
                >
                  <el-table-column
                    fixed
                    prop
                    :label="mainFieldName"
                    min-width="300"
                  >
                    <template v-slot="data">
                      <div
                        v-tippy
                        small
                        @click="openRecordSummary(data.row)"
                        :title="$getProperty(data, 'row.name', '---')"
                        class="flex-middle flex-direction-row"
                      >
                        <div v-if="data.row.avatarUrl">
                          <img
                            :src="data.row.avatarUrl"
                            class="site-list-img-container"
                          />
                        </div>
                        <div v-else>
                          <InlineSvg
                            :src="`svgs/spacemanagement/${currentModule}`"
                            class="width100"
                            iconClass="icon icon-xxxl"
                          ></InlineSvg>
                        </div>
                        <div class="d-flex flex-col mL20">
                          <div class="fc-id line-height-normal">
                            {{ '#' + data.row.id }}
                          </div>
                          <div
                            class="fw5 ellipsis textoverflow-ellipsis width100 site-description"
                          >
                            {{ $getProperty(data, 'row.name', '---') }}
                          </div>
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column
                    :align="
                      field.field && field.field.dataTypeEnum === 'DECIMAL'
                        ? 'right'
                        : 'left'
                    "
                    v-for="(field, index) in filteredViewColumns"
                    :key="index"
                    :prop="field.name"
                    :label="getColumnHeaderLabel(field)"
                    min-width="200"
                  >
                    <template v-slot="data">
                      <div>
                        <div
                          v-if="field.name === 'site'"
                          class="table-subheading"
                        >
                          {{
                            $getProperty(siteList, `${data.row.siteId}`, '---')
                          }}
                        </div>
                        <div
                          v-else
                          class="table-subheading"
                          :class="{
                            'text-right':
                              (field.field || {}).dataTypeEnum === 'DECIMAL',
                          }"
                        >
                          {{ getColumnDisplayValue(field, data.row) || '---' }}
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop
                    label
                    width="100"
                    class="visibility-visible-actions fixed-column-bg"
                    fixed="right"
                    align="right"
                  >
                    <template slot="header">
                      <div @click="showColumnSettings = true">
                        <InlineSvg
                          src="svgs/column-cus"
                          iconClass="icon icon-md vertical-middle pointer"
                          class="fc-white-bg pR20 pL10"
                        >
                        </InlineSvg></div
                    ></template>
                    <template v-slot="data">
                      <div class="text-center">
                        <i
                          class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                          data-arrow="true"
                          :title="editTitle"
                          v-tippy
                          v-if="$hasPermission('space:UPDATE')"
                          @click="openEditForm(data.row)"
                        ></i>
                        <i
                          class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                          data-arrow="true"
                          :title="deleteTitle"
                          v-tippy
                          v-if="$hasPermission('space:DELETE')"
                          @click="invokeDeleteDialog(data.row)"
                        ></i>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
            <column-customization
              :visible.sync="showColumnSettings"
              :moduleName="currentModule"
              :viewName="currentView"
            ></column-customization>
          </div>
        </template>
      </CommonListLayout>
    </div>
    <SpaceManagentForms
      @saved="moduleName => refreshList(moduleName)"
    ></SpaceManagentForms>
    <DeleteDialog
      v-if="showDialog"
      :moduleName="moduleName"
      :errorMap="errorMap"
      :id="deletingRecordId"
      :type="errorType"
      @refresh="refreshList()"
      @onClose="closeDialog()"
    >
    </DeleteDialog>
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import SpaceManagentForms from 'pages/spacemanagement/overview/components/SpaceManagementForms'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { API } from '@facilio/api'
import CommonListLayout from 'src/pages/spacemanagement/components/CommonLayout.vue'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import { sanitize } from '@facilio/utils/sanitize'
import DeleteDialog from 'src/pages/spacemanagement/overview/components/DeleteDialog.vue'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import { getFieldOptions } from 'util/picklist'

export default {
  mixins: [ViewMixinHelper],
  components: {
    Pagination,
    ColumnCustomization,
    SpaceManagentForms,
    CommonListLayout,
    AdvancedSearch,
    FTags,
    DeleteDialog,
  },
  props: ['moduleName', 'viewname'],
  data() {
    return {
      isLoading: false,
      listLoading: false,
      siteList: {},
      iconPath: `svgs/spacemanagement/site`,
      siteViews: [],
      buildingViews: [],
      currentModule: 'site',
      currentSiteView: 'all',
      currentBuildingView: null,
      perPage: 50,
      listCount: 0,
      moduleRecordList: [],
      showColumnSettings: false,
      searchText: null,
      page: 1,
      showMainFieldSearch: false,
      tableLoading: false,
      showDialog: false,
      deletingRecordId: null,
      errorMap: null,
      errorType: null,
      resizeSiteClass: '',
    }
  },
  created() {
    this.$set(this, 'currentModule', this.moduleName)
    this.changeCurrentView(this.viewname, this.moduleName)
    this.init()
    this.sanitize = sanitize
  },
  computed: {
    isV3Api() {
      return true
    },
    loading() {
      let { tableLoading, isLoading, listLoading } = this
      return isLoading || tableLoading || listLoading
    },
    viewDetail() {
      return this.$store.state.view.currentViewDetail
    },
    viewDetailFields() {
      return (this.viewDetail || {}).fields
    },
    mainField() {
      let { viewColumns } = this
      let mainField = null
      if (!isEmpty(viewColumns)) {
        mainField = (viewColumns || []).find(field => {
          let { field: fieldObj } = field
          return (fieldObj || {}).mainField
        })
      } else {
        mainField = {
          name: 'name',
          displayName: 'Name',
          field: {
            name: 'name',
            dataTypeEnum: 'STRING',
          },
        }
      }
      return mainField
    },
    mainFieldName() {
      let { mainField = {} } = this
      return mainField.displayName || 'Name'
    },
    filters() {
      return this.$route.query.search
        ? JSON.parse(this.$route.query.search)
        : null
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      } else if (this.currentModule === 'site') {
        return this.currentSiteView
      } else if (this.currentModule === 'building') {
        return this.currentBuildingView
      }
      return 'all'
    },
    appliedFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
    deleteTitle() {
      let { currentModule } = this
      return this.$t(`space.sites.delete_${currentModule}`)
    },
    editTitle() {
      let { currentModule } = this
      return this.$t(`space.sites.edit_${currentModule}`)
    },
    moduleDisplayName() {
      let { currentModule } = this
      return currentModule === 'site'
        ? this.$t('space.sites.sites')
        : this.$t('space.sites.buildings')
    },
    filteredViewColumns() {
      let { viewColumns } = this
      let { fixedColumns, fixedSelectableColumns } = getColumnConfig(
        this.currentModule
      )
      let finalFixedColumns = fixedColumns.concat(fixedSelectableColumns)
      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !finalFixedColumns.includes(column.name)
        })
      }
      return []
    },
  },
  watch: {
    filters(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadList()
      }
    },
    searchText(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.$set(this, 'isSearchDataLoading', true)
        this.debounceMainFieldSearch()
      }
    },
    page(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        let { isSearchDataLoading } = this
        if (!isSearchDataLoading) {
          this.loadList(false)
        }
      }
    },
    viewDetailFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.doLayout()
        this.tableLoading = false
      })
    },
    moduleName(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.$set(this, 'currentModule', newVal)
      }
    },
    viewname(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.changeCurrentView(newVal, this.moduleName)
        this.init()
      }
    },
  },

  methods: {
    init() {
      let promises = []
      this.isLoading = true
      promises.push(this.loadAllView())
      promises.push(this.loadList())
      promises.push(this.loadViewDetails())
      promises.push(this.loadModuleMeta())
      Promise.all(promises).then(() => {
        this.isLoading = false
      })
      this.debounceMainFieldSearch = this.$helpers.debounce(() => {
        this.loadList()
      }, 2000)
    },
    closeDialog() {
      this.showDialog = false
    },
    changeCurrentView(viewName, moduleName) {
      if (moduleName == 'site') {
        this.currentSiteView = viewName
      } else if (moduleName == 'building') {
        this.currentBuildingView = viewName
      }
    },
    loadModuleMeta() {
      return this.$store.dispatch('view/loadModuleMeta', this.currentModule)
    },
    loadViewDetails() {
      let { currentView, currentModule } = this
      let param = {
        viewName: currentView,
        moduleName: currentModule,
      }
      return this.$store.dispatch('view/loadViewDetail', param)
    },
    loadAllView() {
      let promises = []
      promises.push(
        this.$http.get(`/v2/views/viewList?moduleName=site&groupStatus=false`)
      )
      promises.push(
        this.$http.get(
          `/v2/views/viewList?moduleName=building&groupStatus=false`
        )
      )
      return Promise.all(promises)
        .then(([siteViews, buildingViews]) => {
          if (!isEmpty(siteViews)) {
            let {
              data: { message, responseCode, result = {} },
            } = siteViews
            if (responseCode === 0) {
              let { views } = result
              this.siteViews = views
            } else {
              throw new Error(message)
            }
          }
          if (!isEmpty(buildingViews)) {
            let {
              data: { message, responseCode, result = {} },
            } = buildingViews
            if (responseCode === 0) {
              let { views } = result
              this.buildingViews = views
            } else {
              throw new Error(message)
            }
          }
        })
        .catch(({ message = 'Error Occurred while loading views' }) => {
          this.$message.error(message)
        })
        .finally(() => {
          this.isSearchDataLoading = false
        })
    },
    async loadList(fetchCount = true) {
      let params = {
        withCount: true,
        viewName: this.currentView,
        includeParentFilter: this.includeParentFilter,
        criteriaIds: this.$route.query.criteriaIds,
      }
      if (!isEmpty(this.page)) {
        params['page'] = this.page
        params['perPage'] = this.perPage
      }
      if (!isEmpty(this.filters)) {
        params['filters'] = JSON.stringify(this.filters)
      }
      if (!isEmpty(this.searchText)) {
        params['search'] = this.searchText
      }
      this.listLoading = true

      let { list, meta, error } = await API.fetchAll(
        this.currentModule,
        params,
        {
          force: fetchCount,
        }
      )
      if (error && Object.keys(error).length > 0) {
        let { message = 'Error Occured while fetching Site list' } = error
        this.$message.error(message)
      } else {
        this.listCount = this.$getProperty(meta, 'pagination.totalCount', null)
        this.moduleRecordList = list || []
        let mapData = this.moduleRecordList.filter(record => {
          return (
            !isEmpty(record.location) &&
            !isEmpty(record.location.lat) &&
            !isEmpty(record.location.lng)
          )
        })
        this.$emit('mapData', mapData)
        await this.getSites()
      }
      this.listLoading = false
      this.isSearchDataLoading = false
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    openRecordSummary(record) {
      let { id, siteId } = record
      let { currentModule } = this
      let parentPath = this.findRoute()
      let viewname = this.viewname
      if (parentPath) {
        if (currentModule === 'site') {
          this.$router.push(
            `${parentPath}/sites/${viewname}/site/${id}/overview`
          )
        } else if (currentModule === 'building') {
          this.$router.push(
            `${parentPath}/buildings/${viewname}/site/${siteId}/building/${id}`
          )
        }
      }
    },
    async invokeDeleteDialog(moduleData) {
      let { id } = moduleData
      let messageString = `space.sites.delete_${this.currentModule}_msg`

      let { error } = await API.fetchRecord(
        this.moduleName,
        {
          id,
          fetchChildCount: true,
        },
        { force: true }
      )

      if (!error) {
        let value = await this.$dialog.confirm({
          title: this.$t(`space.sites.delete_${this.currentModule}`),
          message: this.$t(messageString),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })

        if (value) {
          let { error } = await API.deleteRecord(this.moduleName, [id])
          if (!error) {
            this.$message.success(this.$t('space.sites.delete_success'))
            this.refreshList()
          } else {
            this.$message.error(error)
          }
        }
      } else {
        this.deletingRecordId = id
        let map = JSON.parse(error.message)
        this.errorMap = map
        this.errorType = error.code
        this.showDialog = true
      }
    },
    refreshList() {
      let promises = []
      this.isLoading = true
      promises.push(this.loadList())
      if (this.currentModule === 'site') {
        promises.push(this.$store.dispatch('getCurrentAccount'))
        promises.push(this.$store.dispatch('loadSite', true))
      }
      Promise.all(promises).then(() => {
        this.isLoading = false
      })
      this.$emit('refreshCount')
    },
    openEditForm(record) {
      if (this.currentModule === 'site') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: false,
          data: record,
          module: 'site',
          visibility: true,
        })
      } else if (this.currentModule === 'building') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: false,
          buildingObj: record,
          module: 'building',
          visibility: true,
        })
      }
    },
    doLayout() {
      let { $refs } = this
      let container = $refs['fc-table-list']
      if (!isEmpty(container)) {
        container.doLayout()
      }
    },
    openMainFieldSearch() {
      this.$set(this, 'showMainFieldSearch', true)
      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']
        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },
    hideMainFieldSearch() {
      let { searchText } = this
      if (isEmpty(searchText)) {
        this.$set(this, 'showMainFieldSearch', false)
      }
    },
    async getSites() {
      let { moduleRecordList } = this
      let siteIds = (moduleRecordList || []).map(record => record.siteId)
      let defaultIds = [...new Set(siteIds)]
      let perPage = defaultIds.length

      if (perPage === 0) return

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site', skipDeserialize: true },
        defaultIds,
        perPage,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.siteList = options
      }
    },
    resizeList(canResize) {
      this.resizeSiteClass = canResize ? 'resize-portfolio-list' : ''
    },
  },
}
</script>
<style lang="scss">
.site-list-table-container {
  table {
    width: 100% !important;
  }
}
</style>
<style lang="scss" scoped>
.portfolio-home-layout {
  .view-panel {
    width: 290px !important;
  }
  .resize-portfolio-list {
    margin-left: 290px !important;
  }
}
</style>
