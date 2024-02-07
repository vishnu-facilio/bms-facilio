<template>
  <EmployeePageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('tenant.vendor.visits') }}
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
      <span class="separator">|</span>

      <CreateButton @click="showFormVisibility = true">
        {{ $t('setup.setup.new') }}
        {{ moduleDisplayName ? moduleDisplayName : '' }}
      </CreateButton>

      <VisitsAndInvitesForm
        v-if="showFormVisibility"
        :moduleName="moduleName"
        :requestedBy="{ id: $portaluser.ouid }"
        @onSave="loadRecords"
        @onClose="showFormVisibility = false"
      ></VisitsAndInvitesForm>
    </template>
    <Tags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></Tags>
    <div class="list-container">
      <div v-if="showLoading" class="list-loading">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(moduleRecordList) && !showLoading"
        class="list-empty-state"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="mT10 fc-black-dark f16 fw6">
          {{ $t('home.visitor.visitor_log_no_data') }}
        </div>
        <div class="fc-grayish f14 line-height30">
          {{ $t('tenant.vendor.no_visits') }}
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
        <div
          v-if="(selectedRecords || []).length > 0"
          class="pull-left table-header-actions"
        >
          <div class="action-btn-slide btn-block">
            <button
              class="btn btn--tertiary"
              @click="addToWatchList('vip')"
              :class="{ disabled: watchListLoading }"
            >
              {{ $t('home.visitor.mark_as_vip') }}
            </button>
          </div>
          <div class="action-btn-slide btn-block">
            <button
              class="btn btn--tertiary"
              @click="addToWatchList('block')"
              :class="{ disabled: watchListLoading }"
            >
              {{ $t('home.visitor.mark_as_blocked') }}
            </button>
          </div>
          <div class="action-btn-slide btn-block">
            <button
              class="btn btn--tertiary"
              @click="printBadge(selectedRecords)"
            >
              <i class="el-icon-printer"></i>
              {{ $t('common._common.print_badge') }}
            </button>
          </div>
        </div>
        <el-table
          :data="moduleRecordList"
          ref="tableList"
          class="width100"
          height="100%"
          :fit="true"
          @selection-change="selectRecords"
        >
          <el-table-column
            type="selection"
            width="60"
            v-if="false"
          ></el-table-column>
          <el-table-column fixed prop label="ID" min-width="90">
            <template v-slot="data">
              <div class="fc-id">{{ '#' + data.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column fixed prop label="NAME" min-width="200">
            <template v-slot="data">
              <div
                small
                class="flex-middle"
                @click="openVisitorSummary(data.row.id)"
              >
                <visitor-avatar
                  :module="moduleName"
                  :name="false"
                  size="lg"
                  v-if="data.row"
                  :recordData="data.row"
                ></visitor-avatar>
                <div
                  class="fw5 ellipsis textoverflow-ellipsis"
                  :title="data.row ? data.row.visitorName : '---'"
                  v-tippy
                >
                  {{ data.row ? data.row.visitorName : '---' }}
                </div>
              </div>
            </template>
          </el-table-column>
          <template v-for="(field, index) in viewColumns">
            <el-table-column
              v-if="!isFixedColumn(field.name) || field.parentField"
              class="visibility-visible-actions"
              :align="checkDecimalType(field) ? 'right' : 'left'"
              :key="index"
              :prop="field.name"
              :label="field.displayName"
              min-width="200"
            >
              <template v-slot="data">
                <div
                  class="text-align-center"
                  v-if="field.name === 'attachmentPreview'"
                >
                  <f-list-attachment-preview
                    module="newvisitorlogattachments"
                    :record="data.row"
                  ></f-list-attachment-preview>
                </div>
                <div v-else-if="field.name === 'host' && data.row.host">
                  <user-avatar size="md" :user="data.row.host"></user-avatar>
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
            v-if="canShowDelete"
            width="120"
            class="visibility-visible-actions"
            fixed="right"
          >
            <template v-slot="data">
              <div
                v-if="canShowActions(data.row)"
                class="text-center flex-middle"
              >
                <i
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('home.visitor.delete_visitor_log')"
                  v-tippy
                  @click="deleteRecord(data.row)"
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

    <router-view></router-view>
  </EmployeePageLayout>
</template>
<script>
import EmployeePageLayout from 'src/PortalTenant/employeePortalOverview/EmployeePageLayout'
import Pagination from 'src/components/list/FPagination'
import Spinner from '@/Spinner'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import FListAttachmentPreview from '@/relatedlist/ListAttachmentPreview'
import UserAvatar from '@/avatar/User'
import VisitorAvatar from '@/avatar/VisitorAvatar'
import VisitsAndInvitesForm from 'pages/visitors/visits/VisitsAndInvitesForm'
import CreateButton from 'PortalTenant/components/CreateButton'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { mapGetters, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import { isDecimalField } from '@facilio/utils/field'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Tags from 'newapp/components/search/FTags'
import Sort from '../components/Sort'

export default {
  props: ['moduleName', 'viewname'],
  title() {
    return 'Visitor Logs'
  },
  mixins: [ViewMixinHelper],
  components: {
    Spinner,
    ColumnCustomization,
    FListAttachmentPreview,
    UserAvatar,
    VisitorAvatar,
    EmployeePageLayout,
    Pagination,
    AdvancedSearch,
    Tags,
    VisitsAndInvitesForm,
    CreateButton,
    Sort,
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.$store.dispatch('view/loadModuleMeta', this.moduleName).catch(() => {})
    this.loadRecords()
    this.getViewDetail()
  },
  data() {
    return {
      loading: true,
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['visitor', 'localId', 'visitorName'],
        availableColumns: [],
        showLookupColumns: true,
        lookupToShow: ['visitor'],
      },
      activeName: 'details',
      moduleRecordList: [],
      listCount: null,
      selectedRecords: {},
      watchListLoading: false,
      tableLoading: false,
      showFormVisibility: false,
    }
  },
  computed: {
    ...mapState({
      viewLoading: state => state.view.isLoading,
      viewFields: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    moduleDisplayName() {
      return this.metaInfo?.displayName
    },
    currentViewFields() {
      let { fields } = this.viewFields || {}
      return fields || []
    },
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    currentView() {
      return this.$route.params.viewname
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
    isV3Api() {
      return true
    },
    canShowDelete() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
  },
  watch: {
    currentView(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.getViewDetail()
        this.loadRecords()
      }
    },
    currentViewFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    page(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadRecords()
      }
    },
    filters() {
      this.loadRecords()
    },
  },
  methods: {
    checkDecimalType(fieldObj) {
      let { field } = fieldObj || {}
      return !isEmpty(field) ? isDecimalField(field) : false
    },
    canShowActions(record) {
      let { moduleName } = this
      return !isRecordLocked(record, moduleName) && !isRequestedState(record)
    },
    getViewDetail() {
      let { moduleName, currentView } = this

      if (this.currentView) {
        this.$store.dispatch('view/loadViewDetail', {
          viewName: currentView,
          moduleName,
        })
      }
    },
    async loadRecords(force = false) {
      let { filters, currentView, moduleName, page, includeParentFilter } = this
      if (isEmpty(currentView)) return

      this.loading = true
      let {
        error,
        list,
        meta: { pagination = {} },
      } = await API.fetchAll(
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

      if (!error) {
        this.moduleRecordList = list || []
        this.listCount = pagination.totalCount || null
      }
      this.loading = false
    },
    openVisitorSummary(id) {
      let { moduleName, viewname } = this
      let route = findRouteForModule(moduleName, pageTypes.OVERVIEW)

      if (route) {
        this.$router.push({ name: route.name, params: { viewname, id } })
      }
    },
    selectRecords(records) {
      this.selectedRecords = records
    },
    addToWatchList(type) {
      let { moduleName, selectedRecords } = this
      let records = selectedRecords
        .map(r => r.visitor)
        .filter(v => !isEmpty(v))
        .map(v => {
          let { visitorName, visitorEmail, visitorPhone, id } = v || {}
          let currentType = {}

          if (type === 'vip') {
            currentType = { isVip: true }
          } else if (type === 'block') {
            currentType = { isBlocked: true }
          }

          return { id, visitorName, visitorEmail, visitorPhone, ...currentType }
        })

      this.watchListLoading = true
      API.put('v3/modules/data/patch', { data: records, moduleName }).then(
        ({ error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success('Records Added to Watchlist Successfully')
            this.$refs.tableList.clearSelection()
          }
          this.watchListLoading = true
        }
      )
    },
    printBadge(selectedRecords) {
      let recordIds = selectedRecords.map(rec => rec.id + '')
      window.open(
        window.location.protocol +
          '//' +
          window.location.host +
          '/app/pdf/visitorbadge?visitId=' +
          recordIds
      )
    },
    async deleteRecord({ id }) {
      let value = await this.$dialog.confirm({
        title: this.$t('home.visitor.delete_visitor_log'),
        message: this.$t('home.visitor.delete_visitor_log_confirmation'),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { error } = await API.deleteRecord(this.moduleName, id)

        if (error) {
          this.$message.error(error.messsage || 'Error Occured')
        } else {
          this.$message.success(
            this.$t('home.visitor.visitor_log_delete_success')
          )
          this.loadRecords()
        }
      }
    },
  },
}
</script>
