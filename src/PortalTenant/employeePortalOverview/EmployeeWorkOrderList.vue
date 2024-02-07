<template>
  <EmployeePageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('portal.tenant.work_request') }}
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
          @sortChange="loadWorkRequests"
        >
        </Sort>
      </el-tooltip>

      <span
        v-if="$hasPermission(`${moduleName}:EXPORT`)"
        class="separator pL10"
      >
        |</span
      >

      <el-tooltip
        v-if="$hasPermission(`${moduleName}:EXPORT`)"
        effect="dark"
        :content="$t('common._common.export')"
        :open-delay="500"
        placement="top"
      >
        <FExportSettings
          :module="moduleName"
          :viewDetail="viewDetail"
          :showViewScheduler="false"
          :showMail="false"
          :filters="filters"
        ></FExportSettings>
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
        {{ $t('common._common._new') }}
        {{ moduleDisplayName ? moduleDisplayName : '' }}
      </CreateButton> -->
    </template>
    <Tags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></Tags>

    <div class="list-container">
      <div v-if="showLoading" class="list-loading">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(workorderList) && !showLoading"
        class="list-empty-state"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="mT10 fc-black-dark f16 fw6">
          {{ $t('tenant.work.no_work_req') }}
        </div>
        <div class="fc-grayish f14 line-height30">
          {{ $t('tenant.work.no_work_req_view') }}
        </div>
      </div>
      <template v-if="!showLoading && !$validation.isEmpty(workorderList)">
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
          :data="workorderList"
          ref="tableList"
          class="width100"
          height="100%"
          :fit="true"
        >
          <el-table-column
            fixed
            prop
            :label="$t('common._common.id')"
            min-width="90"
          >
            <template v-slot="data">
              <div class="fc-id">{{ '#' + data.row.serialNumber }}</div>
            </template>
          </el-table-column>
          <el-table-column
            fixed
            prop="subject"
            :label="$t('common._common.subject')"
            min-width="200"
          >
            <template v-slot="data">
              <div @click="redirectToSummery(data.row.id)">
                {{ data.row.subject }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            v-for="(field, index) in showInViewColumns"
            :fixed="field.name === 'name'"
            :align="checkDecimalType(field) ? 'right' : 'left'"
            :key="index"
            :prop="field.name"
            :label="field.displayName"
            min-width="200"
          >
            <template v-slot="data">
              <div v-if="field.name === 'urgency'">
                {{
                  !$validation.isEmpty(data.row.urgency)
                    ? WOUrgency[data.row.urgency]
                    : '---'
                }}
              </div>
              <div v-else-if="field.name === 'siteId'">
                {{
                  data.row.siteId && siteList[data.row.siteId]
                    ? siteList[data.row.siteId]
                    : '---'
                }}
              </div>
              <div
                v-else-if="
                  $getProperty(field, 'field.displayType') === 'SIGNATURE'
                "
              >
                <SignatureField
                  :field="(field || {}).field"
                  :record="data.row"
                />
              </div>
              <div v-else-if="field.name === 'noOfNotes'">
                <div @click="showComments(data.row)" class="d-flex">
                  <inline-svg
                    v-if="data.row.noOfNotes && data.row.noOfNotes > 0"
                    src="comment"
                    iconClass="icon icon-xs"
                    class="mT5 mR5"
                  />
                  <div>
                    {{ data.row.noOfNotes ? data.row.noOfNotes : '' }}
                  </div>
                </div>
              </div>
              <div v-else-if="field.name === 'noOfAttachments'">
                <div>
                  <span @click="showAttachments(data.row)">
                    <inline-svg
                      v-if="
                        data.row.noOfAttachments && data.row.noOfAttachments > 0
                      "
                      src="svgs/attachment-icon"
                      iconClass="icon vertical-middle icon-sm fill-grey2"
                    ></inline-svg>
                    {{
                      data.row.noOfAttachments ? data.row.noOfAttachments : ''
                    }}
                  </span>
                </div>
              </div>
              <div
                class="table-subheading"
                v-else
                :class="{
                  'text-right': checkDecimalType(field),
                }"
              >
                {{ getColumnDisplayValue(field, data.row) || '---' }}
              </div>
            </template>
          </el-table-column>

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
                  :title="$t('common.header.edit_my_request')"
                  v-tippy
                  @click="editRecord(data.row.id)"
                ></i>
                <i
                  v-if="canShowDelete"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_my_request')"
                  v-tippy
                  @click="deleteRecord(data.row.id)"
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- comment dialog -->
        <outside-click
          :visibility="showCommentsWindow"
          class="comment-dialog"
          style="top: 50px; position: fixed"
        >
          <div>
            <div class="comment-dialog-header">
              <h3
                class="comment-dialog-heading"
                style="text-transform: uppercase"
              >
                {{ $t('maintenance.wr_list.comments') }}
                <span>
                  {{
                    selectedWorkorder.noOfNotes > 0
                      ? '(' + selectedWorkorder.noOfNotes + ')'
                      : ''
                  }}
                </span>
              </h3>
              <div class="comment-close">
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="Close"
                  placement="bottom"
                >
                  <i
                    class="el-icon-close"
                    aria-hidden="true"
                    v-on:click="showCommentsWindow = false"
                  ></i>
                </el-tooltip>
              </div>
            </div>
            <div class="comment-dialog-body">
              <comments
                module="ticketnotes"
                parentModule="workorder"
                :record="selectedWorkorder"
                :isPortalsPage="true"
              ></comments>
            </div>
          </div>
        </outside-click>

        <!-- attachment dialog -->
        <outside-click
          :visibility="showAttachmentsWindow"
          class="comment-dialog"
          style="top: 50px; position: fixed"
        >
          <div>
            <div class="comment-dialog-header">
              <h3
                class="comment-dialog-heading"
                style="text-transform: uppercase"
              >
                {{ $t('maintenance.wr_list.attachments') }}
                <span>
                  {{
                    selectedWorkorder.noOfAttachments > 0
                      ? '(' + selectedWorkorder.noOfAttachments + ')'
                      : ''
                  }}
                </span>
              </h3>
              <div class="comment-close">
                <i
                  class="el-icon-close"
                  aria-hidden="true"
                  v-on:click="showAttachmentsWindow = false"
                ></i>
              </div>
            </div>
            <div class="comment-dialog-body">
              <attachments
                module="ticketattachments"
                :record="selectedWorkorder"
                :diasbleAttachment="true"
              ></attachments>
            </div>
          </div>
        </outside-click>
      </template>
    </div>
  </EmployeePageLayout>
</template>
<script>
import EmployeePageLayout from 'src/PortalTenant/employeePortalOverview/EmployeePageLayout'
import ViewMixinHelper from '@/mixins/ViewMixin'
import Pagination from 'src/components/list/FPagination'
import { API } from '@facilio/api'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { mapGetters, mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import SignatureField from '@/list/SignatureColumn'
import { isRecordLocked, isRequestedState } from 'PortalTenant/util'
import { isDecimalField } from '@facilio/utils/field'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Tags from 'newapp/components/search/FTags'
import Sort from '../components/Sort'
import Comments from '@/relatedlist/Comments2'
import Attachments from '@/relatedlist/SummaryAttachment'
import OutsideClick from '@/OutsideClick'
import FExportSettings from '@/FExportSettings'

export default {
  mixins: [ViewMixinHelper],
  components: {
    EmployeePageLayout,
    Pagination,
    SignatureField,
    AdvancedSearch,
    Tags,
    Sort,
    Comments,
    OutsideClick,
    Attachments,
    FExportSettings,
  },
  title() {
    return 'My Requests'
  },
  async created() {
    await this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('view/loadModuleMeta', this.moduleName).catch(() => {})
    this.getViewDetail()
    this.loadWorkRequests()
  },
  data() {
    return {
      loading: true,
      showColumnSettings: false,
      workorderList: [],
      listCount: null,
      columnConfig: {
        fixedColumns: ['id', 'subject'],
        availableColumns: [],
        showLookupColumns: false,
        lookupToShow: ['vendor'],
      },
      WOUrgency: {
        1: 'Not Urgent',
        2: 'Urgent',
        3: 'Emergency',
      },
      tableLoading: false,
      showCommentsWindow: false,
      showAttachmentsWindow: false,
      selectedWorkorder: {},
    }
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
      viewLoading: state => state.view.isLoading,
      viewDetail: state => state.view.currentViewDetail,
      metaInfo: state => state.view.metaInfo,
      viewFields: state => {
        return state.view.currentViewDetail
          ? state.view.currentViewDetail.fields
          : []
      },
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    moduleName() {
      return 'workorder'
    },
    moduleDisplayName() {
      return (this.metaInfo || {}).displayName
    },
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    currentView() {
      return this.$route.params.viewname
    },
    currentViewFields() {
      return this.viewFields
    },
    showInViewColumns() {
      let { viewColumns } = this

      if (this.currentView === 'open') {
        viewColumns = viewColumns.filter(fld => fld.name !== 'actualWorkEnd')
      }

      return viewColumns.filter(
        col => !this.isFixedColumn(col.name) || col.parentField
      )
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
    siteList() {
      let { sites } = this
      return (sites || []).reduce((data, site) => {
        data[site.id] = site.name
        return data
      }, {})
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
    currentView(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.getViewDetail()
        this.loadWorkRequests()
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
      if (oldVal !== newVal && !this.loading) {
        this.loadWorkRequests()
      }
    },
    filters() {
      this.loadWorkRequests()
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
      if (this.currentView) {
        this.$store.dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.moduleName,
        })
      }
    },
    mapStatus(workorder) {
      let status = this.$store.getters.getTicketStatus(
        workorder.moduleState.id,
        'workorder'
      )
      return status ? status.displayName : ''
    },
    async loadWorkRequests(force = false) {
      let { filters, currentView, moduleName, page, includeParentFilter } = this

      if (isEmpty(currentView)) return

      this.loading = true
      let {
        list,
        meta: { pagination = {} },
        error,
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

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.could_not_fetch_workpermit')
        )
      } else {
        this.workorderList = list
        this.listCount = this.$getProperty(pagination, 'totalCount') || null
      }

      this.loading = false
    },
    redirectToSummery(id) {
      let route = findRouteForModule('workorder', pageTypes.OVERVIEW)
      if (route) {
        this.$router.push({ name: route.name, params: { viewname: 'all', id } })
      } else {
        console.warn(this.$t('common._common.could_not_resolve_route'))
      }
    },
    getCreationRoute() {
      let { moduleName } = this
      let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

      return name ? { name } : null
    },
    editRecord(id) {
      let route = findRouteForModule('workorder', pageTypes.EDIT)
      this.$router.push({
        name: route.name,
        params: { id },
      })
    },
    async deleteRecord(id) {
      let value = await this.$dialog.confirm({
        title: this.$t('maintenance._workorder.delete_wo'),
        message: this.$t('maintenance._workorder.delete_wo_body'),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })

      if (value) {
        let { error } = await API.deleteRecord('workorder', id)

        if (!error) {
          this.$message.success(
            this.$t('maintenance._workorder.wo_delete_success')
          )
          this.loadWorkRequests()
        } else {
          this.$message.error(error.message)
        }
      }
    },
    showComments(workorder) {
      this.selectedWorkorder = workorder
      this.showCommentsWindow = true
    },
    showAttachments(workorder) {
      this.selectedWorkorder = workorder
      this.showAttachmentsWindow = true
    },
  },
}
</script>
<style lang="scss">
.fc-portal-tenant-wolist-page {
  height: calc(100vh - 50px) !important;
  overflow-y: scroll;
  margin-bottom: 50px;
  padding-bottom: 50px;
  .fc-avatar {
    -webkit-box-align: center;
    -webkit-align-items: center;
    -ms-flex-align: center;
    align-items: center;
    border-radius: 50%;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    display: -webkit-inline-box;
    display: -webkit-inline-flex;
    display: -ms-inline-flexbox;
    display: inline-flex;
    -webkit-box-pack: center;
    -webkit-justify-content: center;
    -ms-flex-pack: center;
    justify-content: center;
    position: relative;
    vertical-align: top;
    font-weight: 500;
  }

  .fc-avatar-sm {
    font-size: 11px;
    height: 24px;
    line-height: 24px;
    width: 24px;
    text-align: center;
    color: #fff;
    margin-left: -1px;
  }

  .req-list-overflow {
    max-width: 300px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .cmt-txt {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.4px;
    text-align: left;
    color: #7383bf;
    position: relative;
    top: -3px;
  }

  .comment-dialog .el-dialog {
    position: absolute;
    border-radius: 2px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
    box-sizing: border-box;
    width: 50%;
    right: 0;
    margin-top: 0 !important;
    margin: 0 auto 0;
    height: 100%;
    border-radius: none;
  }

  .comment-dialog .el-dialog__header {
    display: none;
  }

  .comment-dialog-header {
    height: 0;
    padding: 28px 20px;
    border-bottom: 1px solid #f6f3f3;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .comment-dialog-heading {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.2px;
    text-align: left;
    color: #2f2e49;
  }

  .comment-close {
    float: right;
  }

  .comment-dialog .el-dialog__body {
    padding: 0;
  }

  .comment-dialog-body .comment-area {
    width: 100%;
    position: absolute !important;
    border-top: none !important;
    bottom: 30px !important;
  }

  .comment-dialog-body .fc-comments .fc-comment-row2 {
    border-bottom: none !important;
  }

  .comment-dialog-body .fc-comments {
    margin-left: 0 !important;
    padding-left: 0 !important;
  }

  .comment-counnt-txt {
    position: relative;
    top: -3px;
    letter-spacing: 0.2px;
    text-align: left;
    font-size: 13px;
    color: #666666;
  }

  .inline {
    display: inline-block;
    position: relative;
  }

  .fc-avatar-element {
    text-align: left;
    display: flex;
    align-items: center;
  }

  .comments-avatar .q-item-label {
    font-size: 13px;
    font-weight: 500;
    color: #333333;
    letter-spacing: 0.2px;
  }

  .fc-white-theme .secondary-color2 {
    color: #ddd;
  }

  .comment-time {
    padding-top: 8px;
    float: right;
    color: rgb(216, 216, 216);
  }

  .f12 {
    font-size: 12px;
  }

  .comment-time .fa-circle {
    color: rgb(216, 216, 216);
    font-size: 8px;
    padding-right: 10px;
  }

  .comments-avatar .q-item-label {
    font-size: 13px;
    font-weight: 500;
    color: #333333;
    letter-spacing: 0.2px;
  }

  .fc-avatar-element .q-item-label {
    margin-left: 7px;
  }

  .pL15 {
    padding-left: 15px;
  }

  .empty-container {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -ms-flex-wrap: nowrap;
    flex-wrap: nowrap;
  }

  .no-request {
    text-align: center;
    width: 100%;
    position: relative;
  }

  .no-request-txt {
    font-weight: 500;
    font-size: 18px;
  }

  .no-request-txt2 {
    color: #757575;
    font-size: 12px;
  }

  .fc-avatar-xs {
    font-size: 10px;
    height: 21px;
    line-height: 24px;
    width: 21px;
    color: #fff;
    border-radius: 100%;
    text-align: center;
  }

  .el-select .el-input__inner {
    cursor: pointer !important;
  }

  .flRight {
    float: right;
  }

  .fc-comments .comment-btn {
    padding: 8px 22px;
    outline: none;
    cursor: pointer;
    border-radius: 3px;
    border: solid 1px transparent;
    font-size: 10px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.7px;
    text-align: center;
    color: #fff;
    font-size: 12px;
    font-weight: bold;
    background: #39b2c2;
    margin-top: 10px;
  }

  /*new portal 2.0 css*/
  table.sp-list-table {
    width: 100%;
    padding: 5%;
    border-collapse: collapse;
    border-radius: 3px;
    border: 1px solid #ecf0f6;
  }

  .portal-req-td {
    padding: 8px 20px;
  }

  tr.sp-list-tr {
    align-items: center;
    background: #fff;
    width: 100%;
    border-bottom: 1px solid #f4f5f7;
    height: 90px;
  }

  .request-layout {
    overflow: auto;
    padding: 0;
    padding-left: 10px;
    padding-right: 10px;
    margin: 70px;
    margin-top: 0;
    padding-top: 0px;
    overflow-y: scroll;
    height: calc(100vh - 100px);
    padding-bottom: 50px;
  }

  .spl-id {
    font-size: 13px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.4px;
    color: #324056;
    text-align: left;
    padding-left: 10px;
  }

  .spl-description {
    font-size: 13px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: 30px;
    letter-spacing: 0.3px;
    color: #717f88;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 650px;
  }

  .sp-list-tr .fc-avatar-inline {
    display: block;
  }

  .spl-avatar {
    display: -webkit-inline-box;
    display: -moz-inline-box;
    display: -os-inline-box;
  }

  .spl-cmd {
    width: 40px;
    padding-right: 40px !important;
  }

  .spl-img {
    width: 30px;
    height: 30px;
    background-color: #f3f7fd;
    border-radius: 35px;
    justify-content: right;
    border: 1px solid #ecf4ff;
    text-align: center;
    cursor: pointer;
  }

  .spl-img-tag {
    position: relative;
    top: 6px;
  }

  .loading-tabel {
    width: 100%;
  }

  .loading-container {
    width: 100%;
    height: calc(100vh - 100px);
    align-items: center;
    justify-content: center;
    background: #fff;
    position: relative;
  }

  .loading-div {
    align-items: center;
    justify-content: center;
    position: absolute;
    left: 40%;
    top: 30%;
  }

  .portal-comment-dialog .el-dialog {
    position: absolute;
    border-radius: 2px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
    box-sizing: border-box;
    width: 50%;
    right: 0;
    margin-top: 0 !important;
    margin: 0 auto 0;
    height: 100%;
    border-radius: none;
  }

  .portal-comment-dialog .el-dialog__header {
    display: none;
  }

  .portal-comment-dialog .el-dialog__body {
    padding: 0;
  }

  .spr-li .el-icon-arrow-up:before {
    position: relative;
    bottom: 6px;
  }

  .req-list .spr-ul-right {
    width: 150px;
  }

  .request-header {
    font-size: 11px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 1px;
    color: #324056;
    text-transform: uppercase;
  }

  tr.sp-list-tr2.header {
    height: 50px;
    background: #fff;
    border-bottom: 1px solid #ecf0f6;
  }

  .request-layout .fc-comments .comment-box {
    border: 1px solid #ecf0f6 !important;
    box-shadow: none;
    color: #324056 !important;
  }

  .comment-count {
    font-size: 8px;
    background: #ec2d53;
    border-radius: 100%;
    padding: 0px 3px;
    height: 1;
    color: #fff;
    text-align: center;
    position: absolute;
    top: 4px;
    right: 3px;
    font-weight: 600;
  }

  .display {
    margin-top: 57px;
  }

  .wotag {
    border-radius: 3px;
    background-color: #eef3ff;
    padding: 2px 10px 2px 10px;
    font-size: 8px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    text-align: center;
    color: gray;
    margin-left: 5px;
  }

  .wotag.NOTURGENT {
    border-radius: 3px;
    background-color: #eef3ff;
    font-size: 8px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.3px;
    text-align: center;
    color: #6e96f4;
    padding: 4px 10px 4px 10px;
    margin-left: 5px;
  }

  .wotag.URGENT {
    border-radius: 3px;
    background-color: #fceeed;
    padding: 4px 10px 4px 10px;
    margin-left: 5px;
    font-size: 8px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.3px;
    text-align: center;
    color: #e65244;
    margin-left: 5px;
  }

  .wotag.EMERGENCY {
    border-radius: 3px;
    background-color: #e65244;
    padding: 4px 10px 4px 10px;
    font-size: 8px;
    font-weight: bold;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    text-align: center;
    color: #ffffff;
    margin-left: 5px;
  }
}
</style>
