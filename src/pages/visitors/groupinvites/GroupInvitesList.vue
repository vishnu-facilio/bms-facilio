<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :hideSubHeader="$validation.isEmpty(invitesList)"
    :recordCount="listCount"
    :recordLoading="showLoading"
    :visibleViewCount="3"
    :getPageTitle="() => 'Invite'"
    pathPrefix="/app/vi/groupinvite/"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <template>
        <span class="separator pL10">|</span>
        <div class="pL10">
          <button
            class="fc-create-btn create-btn"
            style="margin-top: -10px;"
            @click="openInviteForm"
          >
            {{ $t('common.header.new_group_invite') }}
          </button>
        </div>
      </template>
    </template>

    <template #sub-header-actions>
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
          <sort
            :key="moduleName + '-sort'"
            :config="sortConfig"
            :sortList="sortConfigList"
            @onchange="updateSort"
          ></sort>
        </el-tooltip>
        <span class="separator">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.export')"
          placement="left"
        >
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
        <span class="separator">|</span>
      </template>

    </template>
  <template #content>
    <div
    class="fc-list-view p10 pT0  fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
    >
      <div v-if="showLoading" class="flex-middle fc-empty-white">
        <spinner :show="showLoading" size="80"></spinner>
      </div>

      <div
        v-else-if="$validation.isEmpty(invitesList)"
        class="fc-list-empty-state-container"      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="mT10 fc-black-dark f16 fw6">
          {{ $t('home.visitor.visitor_invites_no_data') }}
        </div>
      </div>

      <div v-else-if="!$validation.isEmpty(invitesList)">
        <div class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          />
        </div>

        <el-table
          :data="invitesList"
          ref="tableList"
          class="fc-list-eltable width100"
          height="auto"
          :fit="true"
        >
          <el-table-column fixed prop label="ID" min-width="90" class="pR0 pL0">
            <template v-slot="data" class="pL0">
              <div class="fc-id">{{ '#' + data.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column
            v-if="!$validation.isEmpty(nameColumn)"
            :label="nameColumn.displayName"
            :prop="nameColumn.name"
            fixed
            min-width="250"
          >
            <template v-slot="item">
              <div
                @click="openRecordSummary(item.row.id)"
                class="table-subheading"
              >
                <div class="self-center mL10 main-field-column">
                  {{ getColumnDisplayValue(nameColumn, item.row) || '---' }}
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
            :label="field.displayName"
            min-width="200"
          >
            <template v-slot="data">
              <div>
                <div
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
            width="180"
            class="visibility-visible-actions"
            fixed="right"
          >
            <template v-slot="data">
              <div class="text-center">
                <i
                  v-if="$hasPermission(`${moduleName}:UPDATE`)"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  v-tippy
                  @click="editRecord(data.row)"
                ></i>
                <i
                  v-if="$hasPermission(`${moduleName}:DELETE`)"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  v-tippy
                  @click="deleteRecords([data.row.id])"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
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
      @onClose="handleCloseInviteForm"
    ></VisitsAndInvitesForm>

    <router-view />
    </template>
  </CommonListLayout>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import VisitsAndInvitesForm from 'pages/visitors/visits/VisitsAndInvitesForm'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import VisitorAvatar from '@/avatar/VisitorAvatar'
import TransitionButtons from '@/stateflow/TransitionButtonsForList'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ColumnCustomization from '@/ColumnCustomization'
import UserAvatar from '@/avatar/User'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'
import { API } from '@facilio/api'
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
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    AdvancedSearch,
    FTags,
    VisitorAvatar,
    TransitionButtons,
    UserAvatar,
    ColumnCustomization,
    AdvancedSearchWrapper,
  },
  data() {
    return {
      showFormVisibility: false,
      loading: false,
      tableLoading: false,
      listCount: null,
      invitesList: null,
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
    ...mapState('view', {
      views: state => state.groupViews,
      viewDetail: state => state.currentViewDetail,
      viewLoading: state => state.isLoading,
    }),
    nameColumn() {
      let { viewColumns } = this
      if (!isEmpty(viewColumns)) {
        return viewColumns.find(column => column.name === 'name')
      }
      return {}
    },
    moduleDisplayName() {
      return this.metaInfo?.displayName || 'groupinvite'
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
    handleCloseInviteForm() {
      this.loadRecordList()
      this.showFormVisibility = false

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
        force: true,
      }
      let {
        list,
        meta: { pagination = {}, stateflows },
        error,
      } = await API.fetchAll(moduleName, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.invitesList = list || []
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
          title: this.$t('home.visitor.delete_group_invite'),
          message: this.$t('home.visitor.delete_group_invite_confirmation'),
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
                  this.$t('home.visitor.group_invite_delete_success')
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
