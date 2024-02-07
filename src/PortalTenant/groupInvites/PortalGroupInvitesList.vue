<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('tenant.vendor.invites') }}
    </template>
    <template #header>
      <template>
        <pagination
          :total="listCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="listCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <Sort
            :moduleName="moduleName"
            :key="moduleName + '-sort'"
            @sortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span class="separator">|</span>
      </template>

      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="left"
      >
        <AdvancedSearch
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          moduleDisplayName="Group Invites"
        >
        </AdvancedSearch>
      </el-tooltip>

      <template>
        <span class="separator pL10">|</span>
        <div class="pL10">
          <button
            class="fc-create-btn create-btn"
            style="margin-top: -10px;"
            @click="openInviteForm"
          >
            {{ $t('common.header.group_invite') }}
          </button>
        </div>
      </template>
    </template>

    <div>
      <FTags :key="`ftags-list-${moduleName}`"></FTags>
    </div>
    <div
      class="height100 fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
    >
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
            {{ $t('home.visitor.visitor_invites_no_data') }}
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
            <el-table-column fixed prop :label="$t('common._common.id')">
              <template v-slot="data">
                <div class="fc-id">{{ '#' + data.row.id }}</div>
              </template>
            </el-table-column>
            <el-table-column fixed prop label="Name">
              <template v-slot="data">
                <div
                  v-tippy
                  small
                  :title="$getProperty(data, 'row.name', '---')"
                  class="flex-middle"
                  @click="openRecordSummary(data.row.id)"
                >
                  <div class="fw5 ellipsis textoverflow-ellipsis">
                    {{ $getProperty(data, 'row.name', '---') }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <template v-for="(field, index) in filteredViewColumns">
              <el-table-column
                :align="checkDecimalType(field) ? 'right' : 'left'"
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                min-width="200"
                v-if="!isFixedColumn(field.name) || field.parentField"
              >
                <template v-slot="data">
                  <div v-if="!isFixedColumn(field.name) || field.parentField">
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
              width="180px"
              class="visibility-visible-actions"
              fixed="right"
            >
              <template v-slot="data">
                <div class="text-center">
                  <i
                    v-if="canShowEdit"
                    class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('home.insurance.edit_insurance')"
                    v-tippy
                    @click="editRecord(data.row)"
                  ></i>
                  <i
                    v-if="canShowDelete"
                    class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                    data-arrow="true"
                    :title="$t('home.insurance.delete_insurance')"
                    v-tippy
                    @click="deleteRecord(data.row.id)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </div>
    </div>

    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="{ name: 'vi-viewmanager', params: { moduleName } }"
        class="view-manager-btn"
      >
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </router-link>
    </portal>

    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      :columnConfig="columnConfig"
      :viewName="currentView"
    ></column-customization>

    <VisitsAndInvitesForm
      v-if="showFormVisibility"
      :moduleName="inviteModule"
      :formMode="formMode"
      @onClose="showFormVisibility = false"
    ></VisitsAndInvitesForm>

    <router-view />
  </PageLayout>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import VisitsAndInvitesForm from 'pages/visitors/visits/VisitsAndInvitesForm'
import Pagination from '@/list/FPagination'
import Sort from '../components/Sort'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ColumnCustomization from '@/ColumnCustomization'
import PageLayout from 'src/PortalTenant/components/PageLayout'
import { API } from '@facilio/api'
import { isDecimalField } from '@facilio/utils/field'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['moduleName', 'viewname'],
  mixins: [ViewMixinHelper],
  components: {
    VisitsAndInvitesForm,
    Pagination,
    Sort,
    AdvancedSearch,
    FTags,
    PageLayout,
    ColumnCustomization,
  },
  data() {
    return {
      showFormVisibility: false,
      loading: false,
      tableLoading: false,
      listCount: null,
      moduleRecordList: null,
      stateflows: {},
      fields: {},
      formMode: 'bulk',
      inviteModule: 'invitevisitor',
      sortConfig: { orderType: 'asc' },
      sortConfigList: [],
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['name'],
        availableColumns: [],
        showLookupColumns: false,
      },
    }
  },
  computed: {
    ...mapGetters('webtabs', ['tabHasPermission']),
    ...mapState('view', {
      views: state => state.groupViews,
      viewDetail: state => state.currentViewDetail,
      viewLoading: state => state.isLoading,
    }),
    openModuleId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'name')
      }
      return {}
    },
    filteredViewColumns() {
      let { viewColumns, columnConfig } = this
      let { fixedColumns, fixedSelectableColumns } = columnConfig
      let finalFixedColumns = fixedColumns.concat(fixedSelectableColumns)
      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !finalFixedColumns.includes(column.name)
        })
      }
      return []
    },
    filters() {
      let {
        $route: { query },
      } = this
      let { search } = query || {}
      return search ? JSON.parse(search) : null
    },
    currentView() {
      return this.viewname
    },
    page() {
      let {
        $route: { query },
      } = this
      let { page } = query || {}
      return page || 1
    },
    showLoading() {
      return this.viewLoading || this.loading || this.tableLoading
    },
    isV3Api() {
      return true
    },
    viewDetailFields() {
      return this.viewDetail.fields
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
  },
  watch: {
    currentView: {
      handler(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.loadRecordList()
          this.fetchMetaFields()
        }
      },
      immediate: true,
    },
    filters() {
      this.loadRecordList()
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadRecordList()
      }
    },
    viewDetail() {
      let { sortFields = [] } = this.viewDetail || {}

      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})
        this.sortConfig = {
          orderType: sortFields[0].isAscending ? 'asc' : 'desc',
          orderBy: name ? name : '',
        }
      }
    },
    viewDetailFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
  },
  methods: {
    checkDecimalType(fieldObj) {
      let { field } = fieldObj || {}
      return !isEmpty(field) ? isDecimalField(field) : false
    },
    async fetchMetaFields() {
      let { error, data } = await API.get(
        `/module/metafields?moduleName=${this.moduleName}`
      )

      if (!error) {
        let { fields } = data.meta || {}
        if (!isEmpty(fields)) {
          this.sortConfigList = fields.map(fld => fld.name)
        }
        this.sortConfigList.push('localId')
      }
    },
    async loadRecordList() {
      this.loading = true

      let { moduleName, page, currentView, filters } = this
      let params = {
        withCount: true,
        viewName: currentView,
        includeParentFilter: this.includeParentFilter,
        page,
        perPage: 50,
        filters: !isEmpty(filters) ? JSON.stringify(filters) : null,
      }
      let {
        list,
        meta: { pagination = {}, stateflows },
        error,
      } = await API.fetchAll(moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.moduleRecordList = list || []
        this.listCount = this.$getProperty(pagination, 'totalCount', null)
        this.stateflows = stateflows || {}
      }
      this.loading = false
    },
    updateSort(sorting) {
      let { moduleName, currentView } = this
      let sortObj = {
        viewName: currentView,
        orderBy: sorting.orderBy,
        orderType: sorting.orderType,
        moduleName,
      }
      this.$store
        .dispatch('view/savesorting', sortObj)
        .then(() => this.loadRecordList())
    },
    goToOverview({ id }) {
      let { moduleName, currentView, $route } = this
      let params = { viewname: currentView, id }
      let { query } = $route || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.replace({ name, params, query })
        }
      } else {
        this.$router.replace({
          name: 'invites-overview',
          params,
          query,
        })
      }
    },
    openInviteForm() {
      this.showFormVisibility = true
    },
    editRecord(data) {
      let { id, formId, visitorTypeId } = data
      let query = {
        formId: formId,
        formMode: 'bulk',
        visitorTypeId: visitorTypeId,
      }

      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (name) {
          this.$router.push({
            name,
            params: { id },
            query,
          })
        }
      } else {
        this.$router.push({
          name: 'group-invites-edit',
          params: { id },
          query,
        })
      }
    },
    deleteRecords(id) {
      this.$dialog
        .confirm({
          title: this.$t('home.visitor.delete_visitor_invite'),
          message: this.$t('home.visitor.delete_visitor_invite_confirmation'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.deleteRecord(this.moduleName, id).then(({ error }) => {
              if (error) {
                this.$message.error(error.messsage || 'Error Occured')
              } else {
                this.$message.success(
                  this.$t('home.visitor.visitor_invite_delete_success')
                )
                this.loadRecordList()
              }
            })
          }
        })
    },
    openRecordSummary(id) {
      let { moduleName, currentView, $route } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: { viewname: currentView, id },
            query: $route.query,
          })
        }
      } else {
        this.$router.push({
          path: `/app/vi/groupinvite/${currentView}/${id}/summary`,
          query: $route.query,
        })
      }
    },
  },
}
</script>
