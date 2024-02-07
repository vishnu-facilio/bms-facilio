<template>
  <PageLayout :moduleName="moduleName">
    <template slot="title">
      {{ $t('portal.tenant.work_request') }}
    </template>
    <template slot="header">
      <AdvancedSearchWrapper
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :hideSaveView="true"
      ></AdvancedSearchWrapper>
      <CustomButton
        v-if="isBulkActionLicenseEnabled"
        :key="`${moduleName}_${viewname}_${POSITION.LIST_TOP}`"
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        @onSuccess="onCustomButtonSuccess"
      ></CustomButton>
      <CreateButton @click="redirectToFormCreation">
        {{ $t('common._common._new') }}
        {{ moduleDisplayName ? moduleDisplayName : '' }}
      </CreateButton>
    </template>
    <template slot="header-2">
      <pagination
        :total="recordCount"
        :perPage="perPage"
        :currentPageCount="currentPageCount"
      ></pagination>
      <span class="separator pL10" v-if="recordCount > 0">|</span>
      <el-tooltip
        effect="dark"
        :content="$t('common._common.sort')"
        placement="bottom"
      >
        <Sort
          :moduleName="moduleName"
          :key="moduleName + '-sort'"
          @onSortChange="updateSort"
        >
        </Sort>
      </el-tooltip>
      <span class="separator pL10">|</span>
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
    </template>

    <div v-if="showLoading" class="list-loading">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div
      v-if="$validation.isEmpty(records) && !showLoading"
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
    <template v-if="!showLoading && !$validation.isEmpty(records)">
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
      <div class="portal-common-list">
        <div
          class="portal-table-header-actions"
          v-if="
            !$validation.isEmpty(selectedListItemsIds) &&
              isBulkActionLicenseEnabled
          "
        >
          <button
            v-if="canShowDelete"
            class="portal-bulk-action-delete"
            @click="deleteRecords(selectedListItemsIds)"
          >
            {{ $t('custommodules.list.delete') }}
          </button>
          <CustomButton
            :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
            :moduleName="moduleName"
            :position="POSITION.LIST_BAR"
            :selectedRecords="selectedListItemsObj"
            class="custom-button"
            @onSuccess="onCustomButtonSuccess"
          ></CustomButton>
        </div>
        <CommonList
          :viewDetail="viewDetail"
          :records="records"
          :columnConfig="columnConfig"
          :slotList="slotList"
          :redirectToOverview="redirectToOverview"
          :moduleName="moduleName"
          :refreshList="onCustomButtonSuccess"
          @selection-change="selectItems"
          :canShowCustomButton="isBulkActionLicenseEnabled"
          :hideListSelect="!isBulkActionLicenseEnabled"
        >
          <template #[slotList[0].name]="{record}">
            <div class="d-flex">
              <div class="fc-id">{{ '#' + record[slotList[0].name] }}</div>
            </div>
          </template>
          <template #[slotList[1].criteria]="{record}">
            <router-link
              class="d-flex fw5 label-txt-black ellipsis main-field-column"
              :to="redirectToOverview(record.id)"
            >
              <el-tooltip
                effect="dark"
                :content="$getProperty(record, mainFieldName, '---') || '---'"
                placement="top-end"
                :open-delay="600"
              >
                <div class="self-center width200px">
                  <span class="list-main-field">
                    {{ $getProperty(record, mainFieldName, '---') || '---' }}
                  </span>
                </div>
              </el-tooltip>
            </router-link>
          </template>
          <template #[slotList[2].criteria]="{record}">
            <span
              @click="
                record.noOfAttachments && record.noOfAttachments > 0
                  ? showAttachments(record)
                  : ''
              "
            >
              <inline-svg
                src="svgs/attachment-icon"
                iconClass="icon vertical-middle icon-sm fill-grey2"
              ></inline-svg>
              {{ record.noOfAttachments ? record.noOfAttachments : '0' }}
            </span>
          </template>
          <template #[slotList[3].criteria]="{record}">
            <div class="text-align-center">
              <div @click="showComments(record)" class="d-flex">
                <inline-svg
                  src="comment"
                  iconClass="icon icon-xs"
                  class="mT5 mR5"
                />
                <div>
                  {{
                    record.noOfNotes && record.noOfNotes > 0
                      ? record.noOfNotes
                      : '0'
                  }}
                </div>
              </div>
            </div>
          </template>
          <template #[slotList[4].criteria]="{record}">
            {{
              !$validation.isEmpty(record.urgency)
                ? WOUrgency[record.urgency]
                : '---'
            }}
          </template>
          <template v-if="hasActionPermissions" #[slotList[5].name]="{record}">
            <div class="d-flex text-center">
              <i
                v-if="canShowEdit && record.canEdit"
                class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                :data-arrow="true"
                :title="$t('common._common.edit')"
                @click="editModule(record)"
                v-tippy
              ></i>
              <i
                v-if="canShowDelete"
                class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                data-arrow="true"
                :title="$t('common._common.delete')"
                @click="deleteRecords([record.id])"
                v-tippy
              ></i>
            </div>
          </template>

          <!-- Task column handling -->
          <template #[slotList[6].criteria]="{ record }">
            <div class="d-flex">
              <inline-svg
                src="done"
                v-if="record.noOfTasks"
                iconClass="icon vertical-middle icon-xs fill-grey2"
                class="mR5"
              ></inline-svg>
              <div>{{ formatTaskCompletionStatus(record) }}</div>
            </div>
          </template>

          <!-- type column handling -->
          <template #[slotList[7].criteria]="{ record }">
            <el-popover
              placement="bottom"
              width="200"
              popper-class="fcPopover"
              v-if="canShowEdit"
            >
              <div class="max-height400 overflo-auto">
                <ul>
                  <li
                    v-for="(value, key) in ticketType"
                    :key="key"
                    class="el-dropdown-menu__item"
                    @click="updateRecordType(record, value)"
                  >
                    {{ value.name }}
                  </li>
                </ul>
              </div>

              <span slot="reference">
                {{
                  record.type && record.type.id
                    ? getTicketType(record.type.id).name
                    : '---'
                }}

                <i
                  v-if="canShowEdit"
                  aria-hidden="true"
                  class="
                        fa fa-sort-desc
                        wl-icon-downarrow
                        picklist-downarrow
                      "
                ></i>
              </span>
            </el-popover>
            <div v-else>
              {{
                record.type && record.type.id
                  ? getTicketType(record.type.id).name
                  : '---'
              }}
            </div>
          </template>
          <!-- priority column handling -->
          <template #[slotList[8].criteria]="{ record }">
            <el-popover
              v-if="canShowEdit"
              placement="bottom"
              width="200"
              trigger="click"
              popper-class="fcPopover"
            >
              <div class="max-height400 overflo-auto">
                <ul>
                  <li
                    @click="updateRecordPriority(record, value)"
                    v-for="(value, key) in ticketPriority"
                    :key="key"
                    class="el-dropdown-menu__item"
                  >
                    {{ value.displayName }}
                  </li>
                </ul>
              </div>

              <div slot="reference">
                <i
                  class="fa fa-circle prioritytag"
                  v-if="record.priority"
                  v-bind:style="{
                    color: record.priority.colour,
                  }"
                  aria-hidden="true"
                ></i>
                {{ getWorkorderPriority(record.priority) }}
                <i
                  aria-hidden="true"
                  class="
                        fa fa-sort-desc
                        wl-icon-downarrow
                        picklist-downarrow
                      "
                ></i>
              </div>
            </el-popover>
            <div v-else>
              <i
                class="fa fa-circle prioritytag"
                v-if="record.priority"
                v-bind:style="{
                  color: record.priority.colour,
                }"
                aria-hidden="true"
              ></i>
              {{ getWorkorderPriority(record.priority) }}
            </div>
          </template>

          <!-- category column handling -->
          <template #[slotList[9].criteria]="{ record }">
            <el-popover
              v-if="canShowEdit"
              placement="bottom"
              width="200"
              trigger="click"
              popper-class="fcPopover"
            >
              <div class="max-height400 overflo-auto">
                <ul>
                  <li
                    v-for="(value, key) in ticketcategory"
                    :key="key"
                    class="el-dropdown-menu__item"
                    @click="updateRecordCategory(record, value)"
                  >
                    {{ value.displayName }}
                  </li>
                </ul>
              </div>
              <div slot="reference">
                {{ getLookUpDisplayName('category', record) || '---' }}
                <i
                  aria-hidden="true"
                  class="
                        fa fa-sort-desc
                        wl-icon-downarrow
                        picklist-downarrow
                      "
                ></i>
              </div>
            </el-popover>
            <div v-else>
              {{ getLookUpDisplayName('category', record) || '---' }}
            </div>
          </template>
          <!-- space/asset column handling -->
          <template #[slotList[10].criteria]="{ record }">
            <div @click="canShowSpaceAssetChooser(record)">
              <div
                v-if="record.resource && record.resource.id !== -1"
                class="d-flex"
              >
                <div class="pR5">
                  <img
                    v-if="record.resource.resourceType === 1"
                    src="~statics/space/space-resource.svg"
                    style="height: 12px; width: 14px"
                  />
                  <img
                    v-else
                    src="~statics/space/asset-resource.svg"
                    style="height: 11px; width: 14px"
                  />
                </div>
                <div
                  class="q-item-label ellipsis max-width140px"
                  v-tippy
                  small
                  data-position="bottom"
                  :title="record.resource.name"
                >
                  {{ record.resource.name }}
                </div>
              </div>
              <div v-else>
                <span class="q-item-label secondary-color color-d">
                  ---{{ $t('maintenance.wr_list.space_asset') }}---
                </span>
              </div>
            </div>
          </template>

          <!-- team/staff column handling -->
          <template #[slotList[11].criteria]="{ record }">
            <div class="wo-assigned-avatar fL">
              <user-avatar
                size="md"
                :user="record.assignedTo"
                :group="record.assignmentGroup"
                :showPopover="true"
                :showLabel="true"
                :moduleName="'workorder'"
              ></user-avatar>
            </div>
            <f-assignment
              @assignactivity="onAssignWo"
              v-if="canShowTeamStaffChooser(record)"
              viewtype="view"
              :record="record"
              :siteId="$getProperty(record, 'siteId', null)"
            ></f-assignment>
          </template>

          <!-- custom button column handling -->
        </CommonList>
      </div>
      <outside-click
        :visibility="showCommentsWindow"
        class="comment-dialog"
        style="top: 50px; position: fixed;"
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
              :setNoOfNotesLength="setNoOfNotesLength"
              :record="selectedWorkorder"
              :isPortalsPage="true"
            ></comments>
          </div>
        </div>
      </outside-click>
      <WorkOrderAttachmentDialog
        v-if="showAttachmentsWindow"
        :selectedWorkorder="selectedWorkorder"
        @onClose="showAttachmentsWindow = false"
      ></WorkOrderAttachmentDialog>
    </template>
  </PageLayout>
</template>
<script>
import FExportSettings from '@/FExportSettings'
import WorkOrderAttachmentDialog from 'PortalTenant/workorder/WorkOrderAttachmentDialog'
import ModuleList from 'PortalTenant/custom-module/ModuleList'
import Comments from '@/relatedlist/Comments2'
import OutsideClick from '@/OutsideClick'
import { API } from '@facilio/api'
import { mapGetters, mapState } from 'vuex'
import { isEmpty } from 'util/validation'
import UserAvatar from '@/avatar/User'
import FAssignment from '@/FAssignment'

export default {
  extends: ModuleList,
  components: {
    WorkOrderAttachmentDialog,
    FExportSettings,
    Comments,
    OutsideClick,
    UserAvatar,
    FAssignment,
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
  },
  data() {
    return {
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['id', 'subject'],
        availableColumns: ['noOfNotes', 'noOfTasks', 'noOfAttachments'],
        showLookupColumns: false,
        lookupToShow: ['vendor'],
      },
      WOUrgency: {
        1: 'Not Urgent',
        2: 'Urgent',
        3: 'Emergency',
      },
      showCommentsWindow: false,
      showAttachmentsWindow: false,
      selectedWorkorder: {},
      showSpaceAssetDialog: false,
    }
  },
  computed: {
    ...mapGetters([
      'getTicketStatus',
      'isStatusLocked',
      'getTicketType',
      'getApprovalStatus',
      'getTicketPriority',
      'getTicketCategory',
    ]),
    ...mapState({
      viewDetail: state => state.view.currentViewDetail,
      ticketType: state => state.ticketType,
      ticketcategory: state => state.ticketCategory,
      ticketPriority: state => state.ticketPriority,
    }),
    currentViewDetail() {
      let { viewDetail, filters } = this
      if (filters) {
        return 'Filtered View'
      } else {
        return (viewDetail || {}).displayName || ''
      }
    },
    slotList() {
      return [
        {
          name: 'localId',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'subject' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfAttachments' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfNotes' }),
        },
        {
          criteria: JSON.stringify({ name: 'urgency' }),
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
        {
          criteria: JSON.stringify({ name: 'noOfTasks' }),
        },
        {
          criteria: JSON.stringify({ name: 'type' }),
        },
        {
          criteria: JSON.stringify({ name: 'priority' }),
        },
        {
          criteria: JSON.stringify({ name: 'category' }),
        },
        {
          criteria: JSON.stringify({ name: 'resource' }),
        },
        {
          criteria: JSON.stringify({ name: 'assignedTo' }),
        },
      ]
    },
  },
  watch: {
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
  },
  methods: {
    canShowTeamStaffChooser(workorder) {
      return (
        !isEmpty(workorder) &&
        this.hasChangeOwnershipPermission &&
        this.canShowEdit
      )
    },
    showComments(workorder) {
      this.selectedWorkorder = workorder
      this.showCommentsWindow = true
    },
    showAttachments(workorder) {
      this.selectedWorkorder = workorder
      this.showAttachmentsWindow = true
    },
    setNoOfNotesLength(length) {
      this.$set(this.selectedWorkorder, 'noOfNotes', length)
    },
    onAssignWo(assignToObj) {
      if (!assignToObj) {
        return
      }

      let woIds = this.$getProperty(assignToObj, 'id', [])

      woIds.forEach(woId => {
        let workOrder = this.records.find(wo => wo.id === woId)

        if (!isEmpty(workOrder)) {
          let { assignedTo, assignmentGroup, status } = assignToObj
          let { status: wOStatus } = workOrder.status || {}
          let index = this.records.findIndex(wo => wo.id === woId)

          if (workOrder.id === woId) {
            if (!isEmpty(assignedTo)) {
              let { id } = assignedTo
              workOrder.assignedTo = id !== -1 ? assignedTo : null
            }

            if (!isEmpty(assignmentGroup)) {
              let { id } = assignmentGroup
              workOrder.assignmentGroup = id !== -1 ? assignmentGroup : null
            }

            if (wOStatus === 'Submitted' && status) {
              workOrder.status = status
            }
          }
          this.records.splice(index, 1, workOrder)
        }
      })
    },
    hasChangeOwnershipPermission() {
      if(isWebTabsEnabled()){
          return this.$hasPermission(`${this.moduleName}:UPDATE_CHANGE_OWNERSHIP`)
        }
      return (
        this.$hasPermission(`${this.moduleName}:CHANGE_OWNERSHIP`)
      )
    },
    canShowSpaceAssetChooser(workorder) {
      if (this.canShowEdit) {
        this.showSpaceAssetDialog = true
        this.workOrder = workorder
        this.workorderSelected = workorder.id
      }
    },
    getLookUpDisplayName(fieldName, data) {
      let { primaryValue, name, displayName, subject } = data[fieldName] || {}
      let value = displayName || name || subject
      return !isEmpty(primaryValue) ? primaryValue : value
    },
    getWorkorderPriority(priority) {
      if (!priority || !priority.priority || !this.ticketPriority) {
        return '---'
      }
      const prio = this.ticketPriority.filter(tp => tp.id === priority.id)
      if (prio.length !== 1) {
        return '---'
      }
      return prio[0].displayName
    },
    formatTaskCompletionStatus(wo) {
      if (!wo || !wo.noOfTasks) {
        return '---'
      }
      if (!wo.noOfClosedTasks) {
        return 0 + '/' + wo.noOfTasks
      }
      return wo.noOfClosedTasks + '/' + wo.noOfTasks
    },
    updateRecordType(wo, value) {
      this.updateRecordWithV3(wo, { type: value })
    },
    updateRecordCategory(wo, value) {
      this.updateRecordWithV3(wo, { category: value })
    },
    updateRecordPriority(wo, value) {
      this.updateRecordWithV3(wo, { priority: value })
    },
    updateRecordResource(wo, value) {
      this.updateRecordWithV3(wo, { resource: value })
    },
    updateRecordWithV3(wo, data) {
      API.updateRecord(this.moduleName, {
        id: +wo.id,
        data: data,
      })
        .then(() => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_update_success')
          )
        })
        .then(() => {
          this.loadRecords()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
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
