<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :hideSubHeader="isEmpty(visitsList)"
    :getPageTitle="() => 'Visit'"
    :recordCount="listCount"
    :recordLoading="showLoading"
    pathPrefix="/app/vi/visits/"
  >
    <template #header>
      <AdvancedSearchWrapper
        :key="`${moduleName}-search`"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>

      <template>
        <button
          v-if="$hasPermission(`${moduleName}:CREATE`)"
          class="fc-create-btn"
          @click="showFormVisibility = true"
        >
          {{ $t('common.products.check_in') }}
        </button>
      </template>
    </template>
    <template #sub-header-actions>
      <template v-if="!isEmpty(visitsList)">
        <pagination
          :total="listCount"
          :perPage="50"
          :skipTotalCount="true"
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
        <template v-if="!isAltayerNonPrivilagedUser">
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
        </template>
      </template>
    </template>
    <div class="fc-card-popup-list-view">
      <div
        class=" fc-list-view p10 pT0  fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      >
        <spinner v-if="showLoading" class="mT40" :show="showLoading"></spinner>

        <div
          v-else-if="$validation.isEmpty(visitsList)"
          class="fc-list-empty-state-container"
        >
          <inline-svg
            src="svgs/list-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="mT10 fc-black-dark f16 fw6">
            {{ $t('home.visitor.visitor_log_no_data') }}
          </div>
        </div>

        <div v-else-if="!$validation.isEmpty(visitsList)">
          <div class="view-column-chooser" @click="showColumnSettings = true">
            <img
              src="~assets/column-setting.svg"
              style="text-align: center; position: absolute; top: 35%;right: 29%;"
            />
          </div>

          <div
            v-if="!$validation.isEmpty(selectedRecords)"
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
              <button class="btn btn--tertiary" @click="printBadge()">
                <i class="el-icon-printer"></i>
                {{ $t('common._common.print_badge') }}
              </button>
            </div>
          </div>

          <el-table
            :data="visitsList"
            ref="tableList"
            class="width100"
            height="auto"
            :fit="true"
            @selection-change="selectRecords"
          >
            <el-table-column type="selection" width="60"></el-table-column>
            <el-table-column
              fixed
              :label="$t('common._common.id')"
              min-width="90"
            >
              <template v-slot="data">
                <div class="fc-id">{{ '#' + data.row.id }}</div>
              </template>
            </el-table-column>
            <el-table-column
              fixed
              :label="$t('common.products._name')"
              width="300"
            >
              <template v-slot="data">
                <div
                  v-tippy
                  small
                  @click="goToOverview(data.row)"
                  :title="$getProperty(data, 'row.visitorName', '---')"
                  class="flex-middle"
                >
                  <VisitorAvatar
                    module="visitorlog"
                    size="lg"
                    :recordData="data.row"
                  ></VisitorAvatar>
                  <div class="fw5 ellipsis width200px mL10">
                    {{ $getProperty(data, 'row.visitorName', '---') }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <template v-for="(field, index) in viewColumns">
              <el-table-column
                class="visibility-visible-actions"
                :align="
                  field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                "
                :key="index"
                :prop="field.name"
                :label="field.displayName"
                min-width="200"
                v-if="!isFixedColumn(field.name) || field.parentField"
              >
                <template v-slot="data">
                  <div
                    class="text-align-center"
                    v-if="field.name === 'attachmentPreview'"
                  >
                    <FListAttachmentPreview
                      module="newvisitorlogattachments"
                      :record="data.row"
                    ></FListAttachmentPreview>
                  </div>
                  <div
                    v-else-if="field.name === 'actualVisitDuration'"
                    class="text-align-center"
                  >
                    {{ getActualVisitDuration(field, data.row) }}
                  </div>
                  <div
                    v-else
                    :class="[
                      'table-subheading',
                      {
                        'text-right': field.field.dataTypeEnum === 'DECIMAL',
                      },
                    ]"
                  >
                    {{ getColumnDisplayValue(field, data.row) || '---' }}
                  </div>
                </template>
              </el-table-column>
            </template>
            <el-table-column
              :width="calculateColumnWidth()"
              class="visibility-visible-actions"
            >
              <template v-slot="data">
                <div class="text-center visibility-hide-actions">
                  <TransitionButtons
                    :record="data.row"
                    :moduleName="moduleName"
                    :stateFlowList="stateflows"
                    @transitionSuccess="loadRecordList"
                  ></TransitionButtons>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              width="120"
              class="visibility-visible-actions"
              fixed="right"
            >
              <template v-slot="data">
                <div class="text-center flex-middle">
                  <div @click="goToOverview(data.row)">
                    <InlineSvg
                      src="svgs/binoculars"
                      iconClass="icon text-center icon-sm-md mR10 fill-grey2 visibility-hide-actions vertical-text-bottom"
                    ></InlineSvg>
                  </div>
                  <i
                    v-if="$hasPermission(`${moduleName}:DELETE`)"
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
        </div>
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
      :viewName="currentView"
    ></column-customization>

    <VisitsAndInvitesForm
      v-if="showFormVisibility"
      :moduleName="moduleName"
      @onSave="loadRecordList"
      @onClose="showFormVisibility = false"
    ></VisitsAndInvitesForm>

    <router-view />
  </CommonListLayout>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import VisitsAndInvitesForm from './VisitsAndInvitesForm'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import VisitorAvatar from '@/avatar/VisitorAvatar'
import TransitionButtons from '@/stateflow/TransitionButtonsForList'
import FListAttachmentPreview from '@/relatedlist/ListAttachmentPreview'
import ViewMixinHelper from '@/mixins/ViewMixin'
import ColumnCustomization from '@/ColumnCustomization'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import OtherMixin from '@/mixins/OtherMixin'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  props: ['moduleName', 'viewname'],
  mixins: [ViewMixinHelper, OtherMixin],
  components: {
    VisitsAndInvitesForm,
    CommonListLayout,
    Pagination,
    FExportSettings,
    Sort,
    VisitorAvatar,
    TransitionButtons,
    FListAttachmentPreview,
    ColumnCustomization,
    AdvancedSearchWrapper,
  },
  data() {
    return {
      showFormVisibility: false,
      loading: false,
      tableLoading: false,
      listCount: null,
      visitsList: null,
      selectedRecords: [],
      stateflows: {},
      watchListLoading: false,
      sortConfig: { orderType: 'asc' },
      sortConfigList: [],
      showColumnSettings: false,
      availableStates: [],
      isEmpty,
    }
  },
  computed: {
    ...mapState('view', {
      views: state => state.groupViews,
      viewDetail: state => state.currentViewDetail,
      viewLoading: state => state.isLoading,
    }),
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
        this.visitsList = list || []
        this.listCount = this.$getProperty(pagination, 'totalCount', null)
        this.stateflows = stateflows || {}
        this.columnWidth()
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
          name: 'visits-overview',
          params,
          query,
        })
      }
    },
    addToWatchList(type) {
      let { moduleName } = this
      let visitorlog = this.selectedRecords.map(visit => {
        let { id, purposeOfVisit } = visit
        let currentType = {}

        if (type === 'vip') {
          currentType = { isVip: true }
        } else if (type === 'block') {
          currentType = { isBlocked: true }
        }

        return { id, purposeOfVisit, ...currentType }
      })

      this.watchListLoading = true
      API.put('v3/modules/data/bulkpatch', {
        data: { visitorlog },
        moduleName,
      }).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(
            this.$t('common.products.records_added__watchlist_successfully')
          )
          this.$refs.tableList.clearSelection()
          this.loadRecordList()
        }
        this.watchListLoading = true
      })
    },
    printBadge() {
      let recordIds = this.selectedRecords.map(rec => rec.id + '')

      window.open(
        window.location.protocol +
          '//' +
          window.location.host +
          '/app/pdf/visitorbadge?visitId=' +
          recordIds
      )
    },
    selectRecords(records) {
      this.selectedRecords = records
    },
    deleteRecord({ id }) {
      this.$dialog
        .confirm({
          title: this.$t('home.visitor.delete_visitor_log'),
          message: this.$t('home.visitor.delete_visitor_log_confirmation'),
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
                  this.$t('home.visitor.visitor_log_delete_success')
                )
                this.loadRecordList()
              }
            })
          }
        })
    },
    getActualVisitDuration(field, data) {
      let seconds = this.$getProperty(data, 'actualVisitDuration', null)
      let unit = this.$getProperty(field, 'field.unit')
      if (isEmpty(seconds)) {
        return '---'
      }
      if (!isEmpty(unit)) {
        return `${seconds} ${unit}`
      } else {
        if (seconds < 60) {
          return `${seconds} sec`
        } else {
          seconds = Number(seconds)
          let d = Math.floor(seconds / (3600 * 24))
          let h = Math.floor((seconds % (3600 * 24)) / 3600)
          let m = Math.floor((seconds % 3600) / 60)
          let dDisplay = d > 0 ? d + (d == 1 ? ' day, ' : ' days, ') : ''
          let hDisplay = h > 0 ? h + (h == 1 ? ' hr, ' : ' hrs, ') : ''
          let mDisplay = m > 0 ? m + (m == 1 ? ' min ' : ' mins ') : ''
          return `${dDisplay}${hDisplay}${mDisplay} `
        }
      }
    },
    columnWidth() {
      let valuesArray = Object.values(this.stateflows)
      valuesArray[0]?.forEach(field => {
        let { typeEnum } = field || {}

        if (typeEnum === 'NORMAL') {
          this.availableStates.push(field)
        }
      })
    },
    calculateColumnWidth() {
      let pixels =
        this.availableStates && this.availableStates.length > 2
          ? '420'
          : this.availableStates.length == 2
          ? '350'
          : this.availableStates.length == 0
          ? '10'
          : '210'
      return pixels
    },
  },
}
</script>
<style scoped></style>
