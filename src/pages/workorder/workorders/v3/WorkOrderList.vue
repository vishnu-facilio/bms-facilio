<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    pathPrefix="/app/wo/orders"
    :key="moduleName + '-list-layout'"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
        v-if="!canHideFilter"
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <visual-type
        v-if="canShowVisualSwitch"
        @onSwitchVisualize="val => (canShowListView = val)"
      ></visual-type>

      <CustomButton
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        :transformFn="transformFormData"
        :updateUrl="updateUrlString"
        @onSuccess="onCustomButtonSuccess"
        @onError="() => {}"
      />
      <button
        v-if="hasCreatePermission"
        class="fc-create-btn"
        @click="openWOCreate()"
      >
        {{ $t('common.products.new_workorder') }}
      </button>
    </template>

    <template v-if="canShowCalendarHeader" #sub-header>
      <CalendarDateWrapper v-if="!showListView" />
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>

        <span class="separator pL0" v-if="recordCount">| </span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          :open-delay="500"
          placement="top"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span class="separator pL10"> |</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.export')"
          :open-delay="500"
          placement="top"
        >
          <FExportSettings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="true"
            :showMail="true"
            :filters="filters"
          ></FExportSettings>
        </el-tooltip>
      </template>
    </template>
    <template
      #sub-header-selectedall
      v-if="
        showBulkActionBar() && this.$helpers.isLicenseEnabled('BULK_UPDATE')
      "
    >
      <div class="bulk-update-bar">
        <NewBulkActionBar
          ref="bulk-action-bar"
          visible="true"
          :totalCount="recordCount"
          :perPage="perPage"
          :isAllSelected.sync="isAllSelected"
          :moduleName="moduleName"
          @change="validateTotalCount"
          :selectedItem="selectedListItemsObj.length"
          :limit="bulkActionAcount"
        />
      </div>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <div v-else class="height100 pB10">
        <div v-if="showListView" class="fc-card-popup-list-view">
          <div
            class="
            fc-list-view
            fc-list-table-container
            fc-table-td-height fc-table-viewchooser
            pB100
          "
          >
            <div
              v-if="$validation.isEmpty(records) && !showLoading"
              class="
              fc-list-empty-state-container m10 mT0
            "
            >
              <inline-svg
                src="svgs/emptystate/workorder"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="nowo-label">
                {{ $t('common.products.no_workorders_available') }}
              </div>
            </div>

            <div
              v-if="!showLoading && !$validation.isEmpty(records)"
              class="m10 mT0"
            >
              <div
                class="column-customization-icon"
                :disabled="!isColumnCustomizable"
                @click="toShowColumnSettings"
              >
                <el-tooltip
                  :disabled="isColumnCustomizable"
                  placement="top"
                  :content="$t('common._common.you_dont_have_permission')"
                >
                  <inline-svg
                    src="column-setting"
                    class="text-center position-absolute icon"
                  />
                </el-tooltip>
              </div>

              <div
                v-if="!$validation.isEmpty(selectedListItemsObj)"
                class="pull-left table-header-actions"
              >
                <!-- assign op. -->
                <span v-if="hasChangeOwnershipPermission && showBulkAssign">
                  <el-popover
                    placement="right"
                    width="auto"
                    trigger="click"
                    popper-class="fcPopover"
                  >
                    <div class="max-height400 overflo-auto">
                      <ul v-for="u in users" :key="u.email" class="text item">
                        <li
                          class="el-dropdown-menu__item"
                          @click="assignWOToUser(selectedListItemsIds, u)"
                          :class="{ disabled: isLoading }"
                          slot="reference"
                        >
                          {{ u.name + ' | ' + u.email }}
                        </li>
                      </ul>
                      <ul v-for="g in groups" :key="g.id" class="text item">
                        <li
                          class="el-dropdown-menu__item"
                          @click="assignWOToGroup(selectedListItemsIds, g)"
                          :class="{ disabled: isLoading }"
                          slot="reference"
                        >
                          {{ g.name }}
                        </li>
                      </ul>
                    </div>
                    <button
                      class="btn btn--tertiary wo-table-btn-spacing"
                      :class="{ disabled: isLoading }"
                      :disabled="isDisable"
                      slot="reference"
                    >
                      <inline-svg
                        src="svgs/user2"
                        iconClass="icon vertical-middle icon-sm fill-grey2"
                      ></inline-svg>
                      {{ $t('common._common.assign') }}
                    </button>
                  </el-popover>
                </span>

                <!-- close op. wrapped with permission -->
                <button
                  class="btn btn--tertiary wo-table-btn-spacing"
                  @click="closeWO(selectedListItemsIds)"
                  v-if="hasWorkOrderClosePermission && showBulkClose"
                  :class="{ disabled: isLoading }"
                  :disabled="isDisable"
                >
                  <inline-svg
                    src="done"
                    iconClass="icon vertical-middle icon-sm fill-grey2"
                  ></inline-svg>
                  {{ $t('common._common.close') }}
                </button>

                <!-- bulk update op. wrapped with permission -->
                <button
                  class="btn btn--tertiary wo-table-btn-spacing"
                  @click="OpenBulkUpdate()"
                  :class="{ disabled: isLoading }"
                  v-if="hasUpdatePermission"
                >
                  <inline-svg
                    src="svgs/edit"
                    iconClass="icon vertical-middle icon-sm fill-grey2"
                  ></inline-svg>
                  {{ $t('common._common.bulk_update') }}
                </button>

                <!-- delete op. / wrapped with DELETE permission -->
                <span v-if="hasDeletePermission">
                  <button
                    class="btn btn--tertiary wo-table-btn-spacing"
                    @click="deleteRecords(selectedListItemsIds)"
                    :class="{ disabled: isLoading }"
                    :disabled="isDisable"
                  >
                    <inline-svg
                      src="svgs/delete"
                      iconClass="icon vertical-middle icon-sm fill-grey2"
                    ></inline-svg>
                    {{ $t('common._common.delete') }}
                  </button>
                </span>

                <!-- print op. / not wrapped with permission -->
                <button
                  class="btn btn--tertiary wo-table-btn-spacing"
                  @click="openPrintOptions = true"
                  :disabled="isDisable"
                >
                  <inline-svg
                    src="svgs/print2"
                    iconClass="icon vertical-middle icon-sm fill-grey2"
                  ></inline-svg>
                  {{ $t('common._common.print') }}
                </button>

                <CustomButton
                  v-if="!isDisable"
                  :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
                  :modelDataClass="modelDataClass"
                  :moduleName="moduleName"
                  :position="POSITION.LIST_BAR"
                  class="custom-button mL10 inline-block"
                  @onSuccess="onCustomButtonSuccess"
                  @onError="() => {}"
                  :selectedRecords="selectedListItemsObj"
                ></CustomButton>

                <!-- print dialog -->
                <workorder-options
                  v-if="openPrintOptions"
                  :workorders="selectedListItemsIds"
                  :visibility.sync="openPrintOptions"
                  :viewName="viewname"
                ></workorder-options>

                <!-- bulk update dialog -->
                <template v-if="enableNewBulkUpdate()">
                  <new-bulk-update-viewer
                    v-if="bulkAction"
                    module="workorder"
                    :ref="'bulk-update-dialog'"
                    :fieldlist="filteredBulkActionFieldList"
                    :recordCount="recordCount"
                    @submit="newbulkActionHandler"
                    @closed="handleBulkActiondialogClose"
                    :content-css="{
                      padding: '0px',
                      background: '#f7f8fa',
                      Width: '10vw',
                      Height: '30vh',
                    }"
                  ></new-bulk-update-viewer>
                </template>
                <template v-else>
                  <bulk-update-viewer
                    v-if="bulkAction"
                    module="workorder"
                    :fieldlist="filteredBulkActionFieldList"
                    @submit="bulkActionHandler"
                    @closed="closeBulkActionDialog"
                    :content-css="{
                      padding: '0px',
                      background: '#f7f8fa',
                      Width: '10vw',
                      Height: '30vh',
                    }"
                  ></bulk-update-viewer>
                </template>
              </div>

              <CommonList
                :moduleName="moduleName"
                :viewDetail="filteredViewDetail"
                :records="records"
                :slotList="slotList"
                :redirectToOverview="redirectToOverview"
                @selection-change="selectItems"
                :refreshList="onCustomButtonSuccess"
              >
                <!-- id column -->
                <template #[slotList[0].name]="{ record }">
                  <div class="fc-id">
                    {{ '#' + $getProperty(record, 'localId', '---') }}
                  </div>
                </template>

                <!-- subject column -->
                <template #[slotList[1].criteria]="{ record }">
                  <div
                    v-tippy
                    small
                    :title="$getProperty(record, 'subject', '---')"
                    class="flex-middle"
                  >
                    <router-link
                      :to="openWorkorderOverview(record.id)"
                      class="fw5 label-txt-black ellipsis subject-underline"
                    >
                      {{ $getProperty(record, 'subject', '---') }}
                    </router-link>
                  </div>
                </template>

                <!-- comments column -->
                <template #[slotList[2].criteria]="{ record }">
                  <div @click="showComments(record)" class="d-flex">
                    <inline-svg
                      src="comment"
                      iconClass="icon icon-xs text-center"
                      class="vertical-middle flex-middle"
                    ></inline-svg>
                    <div class="mL5">
                      {{ $getProperty(record, 'noOfNotes', '0') }}
                    </div>
                  </div>
                </template>

                <!-- tasks column -->
                <template #[slotList[3].criteria]="{ record }">
                  <div class="d-flex">
                    <inline-svg
                      src="done"
                      v-if="$getProperty(record, 'noOfTasks', 0)"
                      iconClass="icon vertical-middle icon-xs fill-grey2"
                      class="mR5"
                    ></inline-svg>
                    <div>{{ formatTaskCompletionStatus(record) }}</div>
                  </div>
                </template>

                <!-- attachments column -->
                <template #[slotList[4].criteria]="{ record }">
                  <div>
                    <span @click="showAttachments(record)">
                      <inline-svg
                        v-if="$getProperty(record, 'noOfAttachments', 0)"
                        src="svgs/attachment-icon"
                        iconClass="icon vertical-middle icon-sm fill-grey2"
                      ></inline-svg>
                      {{ $getProperty(record, 'noOfAttachments', '---') }}
                    </span>
                  </div>
                </template>

                <!-- edit column -->
                <template #[slotList[5].name]="{ record }">
                  <div class="text-center">
                    <i
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      v-if="canDeleteRecord(record)"
                      :title="$t('common.header.delete_workorder')"
                      v-tippy
                      @click="deleteRecords([record.id])"
                    ></i>
                  </div>
                </template>

                <!-- type column handling -->
                <template #[slotList[6].criteria]="{ record }">
                  <el-popover
                    placement="bottom"
                    width="200"
                    popper-class="fcPopover"
                    v-if="canEditRecord(record)"
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
                      {{ getTicketTypeName(record) }}
                      <i
                        v-if="canEditRecord(record)"
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
                    {{ getTicketTypeName(record) }}
                  </div>
                </template>

                <!-- priority column handling -->
                <template #[slotList[7].criteria]="{ record }">
                  <el-popover
                    v-if="canEditRecord(record)"
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
                        v-if="$getProperty(record, 'priority', '---')"
                        v-bind:style="{
                          color: $getProperty(record, 'priority.colour', '---'),
                        }"
                        aria-hidden="true"
                      ></i>
                      {{ getWorkorderPriority(record) }}
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
                      v-if="$getProperty(record, 'priority', '---')"
                      v-bind:style="{
                        color: $getProperty(record, 'priority.colour', '---'),
                      }"
                      aria-hidden="true"
                    ></i>
                    {{ getWorkorderPriority(record) }}
                  </div>
                </template>

                <!-- category column handling -->
                <template #[slotList[8].criteria]="{ record }">
                  <el-popover
                    v-if="canEditRecord(record)"
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
                <template #[slotList[9].criteria]="{ record }">
                  <div @click="canShowSpaceAssetChooser(record)">
                    <div v-if="isResourceAvailable(record)" class="d-flex">
                      <div class="pR5">
                        <img
                          v-if="getResourceType(record)"
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
                        :title="$getProperty(record, 'resource.name', '---')"
                      >
                        {{ $getProperty(record, 'resource.name', '---') }}
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
                <template #[slotList[10].criteria]="{ record }">
                  <div class="wo-assigned-avatar fL">
                    <user-avatar
                      size="md"
                      :user="$getProperty(record, 'assignedTo', {})"
                      :group="$getProperty(record, 'assignmentGroup', {})"
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
                <template #[slotList[11].name]="{ record }">
                  <CustomButton
                    :moduleName="moduleName"
                    :record="record"
                    :position="POSITION.LIST_ITEM"
                    :transformFn="transformFormData"
                    :updateUrl="updateUrlString"
                    class="custom-button"
                    @onSuccess="onCustomButtonSuccess"
                    @onError="() => {}"
                  ></CustomButton>
                </template>
              </CommonList>
            </div>
          </div>
        </div>
        <CalendarView
          v-else-if="!showListView"
          ref="calendar"
          :moduleName="moduleName"
          :record="records"
          :viewDetail="viewDetail"
          :viewname="viewname"
          :filters="filters"
        ></CalendarView>
      </div>

      <!-- comment dialog -->
      <outside-click
        :visibility="showCommentsWindow"
        class="comment-dialog"
        :style="commentDailogWidth"
        style=" top: 50px; position: fixed;"
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
              :setNoOfNotesLength="setNoOfNotesLength"
            ></comments>
          </div>
        </div>
      </outside-click>

      <!-- attachment dialog -->
      <outside-click :visibility="showAttachmentsWindow" class="comment-dialog">
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
            >
            </attachments>
          </div>
        </div>
      </outside-click>

      <column-customization
        :visible.sync="showColumnSettings"
        moduleName="workorder"
        :viewName="viewname"
      ></column-customization>

      <space-asset-chooser
        @associate="associateSpaceAsset"
        v-if="showSpaceAssetDialog"
        :visibility.sync="showSpaceAssetDialog"
        :filter="filter()"
      ></space-asset-chooser>

      <preview-file
        :visibility.sync="showPreview"
        v-if="showPreview && selectedFile"
        :previewFile="selectedFile"
        :files="[selectedFile]"
      ></preview-file>
      <portal to="view-manager-link">
        <router-link
          tag="div"
          :to="`/app/wo/${moduleName}/viewmanager`"
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
    </template>
  </CommonListLayout>
</template>
<script>
import ViewMixinHelper from '@/mixins/ViewMixin'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import { isEmpty } from 'util/validation'
import { API } from '@facilio/api'
import { mapState, mapGetters } from 'vuex'
import NewBulkUpdateViewer from '@/NewBulkUpdateViewer'
import PreviewFile from '@/PreviewFile'
import workorderOptions from 'pages/workorder/workorders/v1/WorkOrderSummaryPdfOptions'
import Comments from '@/relatedlist/Comments2'
import Attachments from '@/relatedlist/SummaryAttachment'
import OutsideClick from '@/OutsideClick'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import UserAvatar from '@/avatar/User'
import FAssignment from '@/FAssignment'
import BulkUpdateViewer from '@/BulkUpdateViewer'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import moment from 'moment-timezone'
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
import OtherMixin from '@/mixins/OtherMixin'
import NewBulkActionBar from 'src/newapp/list/NewBulkActionBar'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'

export default {
  mixins: [ViewMixinHelper, workorderMixin, OtherMixin],
  extends: CommonModuleList,
  name: 'WorkorderList',
  components: {
    PreviewFile,
    NewBulkUpdateViewer,
    workorderOptions,
    Comments,
    OutsideClick,
    Attachments,
    SpaceAssetChooser,
    UserAvatar,
    FAssignment,
    NewBulkActionBar,
    BulkUpdateViewer,
  },
  created() {
    this.$store.dispatch('loadTicketStatus', this.moduleName)
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketCategory')
  },
  data() {
    return {
      bulkAction: false,
      bulkActionAcount: 0,
      bulkActionFieldList: [
        'requester',
        'assignedTo',
        'category',
        'dueDate',
        'priority',
        'type',
        'resource',
        'vendor',
      ],
      openPrintOptions: false,
      actions: {
        assignLoading: false,
        closeLoading: false,
        deleteLoading: false,
      },
      selectedFile: null,
      showPreview: false,
      showCommentsWindow: false,
      showAttachmentsWindow: false,
      selectedWorkorder: {},
      showSpaceAssetDialog: false,
      perPage: 20,
      isAllSelected: false,
      isDisable: false,
      isBulkUpdateVisible: false,
      updateBulkRecord: true,
    }
  },
  mounted() {
    this.updateLimit()
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
      users: state => state.users,
      groups: state => state.groups,
      ticketType: state => state.ticketType,
      ticketcategory: state => state.ticketCategory,
      ticketPriority: state => state.ticketPriority,
    }),
    showBulkBar() {
      let { totalCount, pePage } = this || {}
      return recordCount
    },
    moduleDisplayName() {
      return 'Workorder'
    },
    isWoStateTransitionLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('WO_STATE_TRANSITION_V3')
    },
    updateUrlString() {
      if (!this.isWoStateTransitionLicenseEnabled) {
        return 'v2/workorders/update'
      }
      return null
    },
    showBulkClose() {
      // hiding bulk close for Aster
      const ASTER = 583
      if (this.$org.id === ASTER) {
        return false
      }
      return true
    },
    showBulkAssign() {
      // hiding bulk assign for Aster
      const ASTER = 583
      if (this.$org.id === ASTER) {
        return false
      }
      return true
    },
    filteredViewColumns() {
      let { viewColumns } = this
      let { fixedColumns, fixedSelectableColumns } = getColumnConfig(
        this.moduleName
      )
      let finalFixedColumns = fixedColumns.concat(fixedSelectableColumns)
      if (!isEmpty(viewColumns)) {
        return viewColumns.filter(column => {
          return !finalFixedColumns.includes(column.name)
        })
      }
      return []
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 150,
            fixed: 'left',
            label: 'ID',
          },
        },
        {
          criteria: JSON.stringify({ name: 'subject' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfNotes' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfTasks' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfAttachments' }),
        },
        {
          name: 'editColumn',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
            align: 'right',
          },
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
        {
          name: 'customButton',
          isActionColumn: true,
          columnAttrs: {
            width: 200,
            class: 'visibility-visible-actions',
          },
        },
      ]
    },
    /* filteredViewDetail filters and keeps the comments, tasks, attachement
      columns at the end and removes the column-header of it.
    */
    filteredViewDetail() {
      let { viewDetail } = this
      let { fields, defaultModuleFields } = viewDetail || {}
      if (!isEmpty(fields)) {
        let notes = fields.find(column => column.name === 'noOfNotes')
        let tasks = fields.find(column => column.name === 'noOfTasks')
        let attachments = fields.find(
          column => column.name === 'noOfAttachments'
        )
        let columnAttrs = {
          'min-width': 80,
        }

        fields = fields.filter(column => {
          return !['noOfNotes', 'noOfTasks', 'noOfAttachments'].includes(
            column.name
          )
        })

        if (!isEmpty(tasks)) {
          tasks = {
            ...tasks,
            columnDisplayName: '',
            field: { ...tasks.field, displayName: '' },
            columnAttrs: columnAttrs,
          }
          fields.push(tasks)
        }

        if (!isEmpty(notes)) {
          notes = {
            ...notes,
            columnDisplayName: '',
            field: { ...notes.field, displayName: '' },
            columnAttrs: columnAttrs,
          }
          fields.push(notes)
        }

        if (!isEmpty(attachments)) {
          attachments = {
            ...attachments,
            columnDisplayName: '',
            field: { ...attachments.field, displayName: '' },
            columnAttrs: columnAttrs,
          }
          fields.push(attachments)
        }

        let customizeDisplayNames = {}

        let { noOfTasks, noOfNotes } = defaultModuleFields || {}

        if (!isEmpty(noOfTasks)) {
          customizeDisplayNames = {
            ...customizeDisplayNames,
            noOfTasks: {
              ...noOfTasks,
              columnDisplayName: '',
            },
          }
        }

        if (!isEmpty(noOfNotes)) {
          customizeDisplayNames = {
            ...customizeDisplayNames,
            noOfNotes: {
              ...noOfNotes,
              columnDisplayName: '',
            },
          }
        }

        viewDetail = {
          ...viewDetail,
          fields: fields,
          defaultModuleFields: {
            ...defaultModuleFields,
            ...customizeDisplayNames,
          },
        }
      }
      return viewDetail
    },

    hasChangeOwnershipPermission() {
      if(isWebTabsEnabled()){
          return this.$hasPermission(`${this.moduleName}:UPDATE_CHANGE_OWNERSHIP`)
        }
      return (
        this.$hasPermission(`${this.moduleName}:CHANGE_OWNERSHIP`)
      )
    },

    hasDeletePermission() {
      return this.$hasPermission(`${this.moduleName}:DELETE`)
    },

    hasCreatePermission() {
      return this.$hasPermission(`${this.moduleName}:CREATE`)
    },

    hasUpdatePermission() {
      return this.$hasPermission(`${this.moduleName}:UPDATE`)
    },

    hasWorkOrderClosePermission() {
      if(isWebTabsEnabled()){
        return this.$hasPermission(`${this.moduleName}:UPDATE_CLOSE_WORKORDER`)
      }
      return this.$hasPermission(`${this.moduleName}:CLOSE_WORK_ORDER`)
    },

    filteredBulkActionFieldList() {
      let fieldList = this.bulkActionFieldList
      fieldList = fieldList.filter(item => {
        // show Team/Staff only if the user has CHANGE_OWNERSHIP permission.
        if (item === 'assignedTo') {
          if (this.hasChangeOwnershipPermission) {
            return item
          }
        } else {
          return item
        }
      })
      return fieldList
    },
    commentDailogWidth() {
      return this.$helpers.isLicenseEnabled('NEW_COMMENTS')
        ? 'width: 41%'
        : null
    },
  },
  methods: {
    async updateLimit() {
      let actionName = 'workorderbulkupdate'
      let params = {
        actionName,
      }
      let { data, error } = await API.get(`v3/bulkaction/limit/fetch`, params)
      if (data) {
        this.bulkActionAcount = data.limit
      }
    },
    showBulkActionBar() {
      if (!isEmpty(this.selectedListItemsObj)) {
        this.isAllSelected = false
        this.isDisable = false
        this.isBulkUpdateVisible = false
        return true
      } else {
        this.isAllSelected = true
        return false
      }
    },
    setNoOfNotesLength(length) {
      this.$set(this.selectedWorkorder, 'noOfNotes', length)
    },
    formatTaskCompletionStatus(wo) {
      if (!wo || !wo.noOfTasks) {
        return ''
      }
      if (!wo.noOfClosedTasks) {
        return 0 + '/' + wo.noOfTasks
      }
      return wo.noOfClosedTasks + '/' + wo.noOfTasks
    },
    openAttachment(field, record) {
      this.selectedFile = {
        contentType: record[`${field.name}ContentType`],
        fileName: record[`${field.name}FileName`],
        downloadUrl: record[`${field.name}DownloadUrl`],
        previewUrl: record[`${field.name}Url`],
      }
      this.showPreview = true
    },
    openWOCreate() {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}
        name && this.$router.push({ name })
      } else {
        this.$router.push({ path: '/app/wo/create' })
      }
    },
    getWorkorderPriority(record) {
      let { priority } = record || {}
      if (!priority || !priority.priority || !this.ticketPriority) {
        return '---'
      }
      const prio = this.ticketPriority.filter(tp => tp.id === priority.id)
      if (prio.length !== 1) {
        return '---'
      }
      return prio[0].displayName
    },
    showComments(workorder) {
      this.selectedWorkorder = workorder
      this.showCommentsWindow = true
    },
    showAttachments(workorder) {
      this.selectedWorkorder = workorder
      this.showAttachmentsWindow = true
    },
    deleteRecords(idList) {
      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.delete_wo'),
          message: this.$t('maintenance._workorder.delete_wo_body'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.actions.deleteLoading = true
            this.workOrderDeleteV3(idList)
          }
        })
    },
    closeWO(idList) {
      this.checkAndShowWorkDuration(idList, 'Closed', idList =>
        this.doCloseWO(idList)
      )
      if (!this.showWorkDurationDialog) {
        this.doCloseWO(idList)
        this.selectedListItemsIds = []
      }
    },
    async doCloseWO(idList, actualDuration) {
      let paramObj = {
        ids: idList,
      }

      if (actualDuration) {
        let { duration, workTimings } = actualDuration || {}

        if (duration !== -1) {
          paramObj.actualWorkDuration = duration
        }
        paramObj.actualTimings = workTimings
      }
      this.actions.closeLoading = true
      let url = 'v3/workorders/close'
      let { error } = await API.post(url, paramObj)
      if (error) {
        this.$message.error(error)
        this.resetSelectAll()
        this.loadRecords()
      } else {
        this.$message.success('Work orders closed successfully')
        this.resetSelectAll()
        this.loadRecords()
      }
    },
    checkAndShowWorkDuration(idList, status, cbk) {
      let showDuration = idList.some(id => {
        let workorder = this.records.find(wo => wo.id === id)
        let { isWorkDurationChangeAllowed, woStatus } = workorder || {}
        let { id: woStatusId } = woStatus || {}

        let getTicketStatusObj =
          (woStatusId && this.getTicketStatus(woStatusId, 'workorder')) || {}
        let closedOrResolvedState =
          status === 'Closed' && getTicketStatusObj.status !== 'Resolved'

        return (
          isWorkDurationChangeAllowed &&
          (status === 'Resolved' || closedOrResolvedState)
        )
      })
      if (showDuration) {
        if (this.$refs.statuspopover) {
          this.$refs.statuspopover.forEach(popover => popover.close())
        }
        this.workorderSelected = idList.length === 1 ? idList[0] : -1
        this.workDurationCallBack = cbk
        this.showWorkDurationDialog = true
      }
    },
    assignWOToGroup(idList, assignmentGroup) {
      let workorder = {}
      if (assignmentGroup) {
        workorder = { assignmentGroup }
      }
      let assignment = {
        ids: idList,
        workOrder: workorder,
      }
      this.assignWO(assignment)
    },
    assignWOToUser(idList, assignedTo) {
      let workorder = {}
      if (assignedTo) {
        workorder = { assignedTo }
      }
      let payload = {
        ids: idList,
        workOrder: workorder,
      }
      this.assignWO(payload)
    },
    assignWO(payload) {
      this.actions.assignLoading = true
      API.post('/v3/workorders/assign', payload)
        .then(({ error }) => {
          if (error) {
            this.$message.error(
              this.$t(
                error.message || 'maintenance._workorder.wo_update_failed'
              )
            )
          } else {
            this.loadRecords(true)
            this.onAssignWo(payload)
            this.$dialog.notify(
              this.$t('maintenance._workorder.wo_assigned_success')
            )
            this.resetSelectAll()
          }
        })
        .finally(() => {
          this.actions.assignLoading = false
          this.resetSelectAll()
        })
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
    async newbulkActionHandler(actions) {
      //await this.getRecordCount()
      if (this.isDisable) {
        this.executeBulkAction(actions)
      } else {
        this.bulkActionHandler(actions)
      }
    },
    async getRecordCount() {
      let { moduleName, viewname, filters } = this
      let recordCount = null

      try {
        let params = { moduleName, viewname, filters }
        recordCount = await this.modelDataClass.fetchRecordsCount(params)
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.unable_to_fetch_count')
        )
      }
      return recordCount
    },
    async executeBulkAction(actions) {
      // each action represents a field update
      let fields = {}

      actions.forEach(action => {
        let { field, valueArray, value, parseLabel, fieldObj } = action || {}
        if (field === 'category') {
          let { displayName } = this.getTicketCategory(parseInt(valueArray))

          fields[field] = {
            id: valueArray,
            name: displayName,
          }
        } else if (field === 'priority') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else if (field === 'requester' || field === 'vendor') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else if (field === 'type') {
          fields[field] = {
            id: valueArray,
            name: this.getTicketType(parseInt(valueArray)).name,
          }
        } else if (field === 'assignedTo') {
          if (!isEmpty(value)) {
            fields[field] = { id: parseInt(value) }
          }
          if (!isEmpty(valueArray)) {
            fields.assignmentGroup = { id: parseInt(valueArray) }
          }
        } else if (field === 'resource') {
          fields[field] = { id: parseInt(parseLabel.id) }
        } else if (field === 'dueDate') {
          fields[field] = Date.parse(value)
        } else {
          if (!isEmpty(fieldObj)) {
            let fieldObject = fieldObj[0]

            // lookup handling
            if (this.isLookup(fieldObject) && !isEmpty(action.value.id)) {
              action.value.id = parseInt(action.value.id)
              fields[field] = action.value
            }

            // picklist handling
            else if (this.isPicklist(fieldObject) && !isEmpty(action.value)) {
              fields[field] = +action.value
            }

            //date and dateTime Handling
            else if (
              (this.isDate(fieldObject) || this.isDateTime(fieldObject)) &&
              !isEmpty(action.value)
            ) {
              fields[field] = moment(action.value).valueOf()
            }

            //multiLookUp Handling
            else if (
              this.isMultiLookUp(fieldObject) &&
              !isEmpty(action.value)
            ) {
              let actionObject = []
              valueArray.forEach(index => {
                let valueArrayItems = {}
                valueArrayItems['id'] = index
                actionObject.push(valueArrayItems)
              })
              fields[field] = actionObject
            } else {
              // other
              fields[field] = action.value
            }
          } else {
            fields[field] = action.value
          }
        }
      })

      let data = []
      let datas = []
      datas.push(fields)
      for (let woID of this.selectedListItemsIds) {
        data.push({ id: woID, ...fields })
      }
      if (!this.updateBulkRecord) {
        let { error } = await this.bulkUpdateWithV3(datas)
        this.showLoading = false
        if (this.$refs['bulk-update-dialog']) {
          if (error) {
            let message = error?.message ? error.message : ''
            this.$refs['bulk-update-dialog'].updateAPICallback({
              message: message,
              error: true,
            })
          } else {
            this.$refs['bulk-update-dialog'].updateAPICallback({
              message: 'Records updated successfully.',
            })
          }
        }
        //this.resetSelectAll()
        //  this.closeBulkActionDialog()
        //  this.loadRecords()
      }
    },
    bulkActionHandler(actions) {
      // each action represents a field update
      let fields = {}

      actions.forEach(action => {
        let { field, valueArray, value, parseLabel, fieldObj } = action || {}
        if (field === 'category') {
          let { displayName } = this.getTicketCategory(parseInt(valueArray))

          fields[field] = {
            id: valueArray,
            name: displayName,
          }
        } else if (field === 'priority') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else if (field === 'requester') {
          fields[field] = {
            id: parseInt(valueArray),
          }
        } else if (field === 'type') {
          fields[field] = {
            id: valueArray,
            name: this.getTicketType(parseInt(valueArray)).name,
          }
        } else if (field === 'assignedTo') {
          if (!isEmpty(value)) {
            fields[field] = { id: parseInt(value) }
          }
          if (!isEmpty(valueArray)) {
            fields.assignmentGroup = { id: parseInt(valueArray) }
          }
        } else if (field === 'resource') {
          fields[field] = { id: parseInt(parseLabel.id) }
        } else if (field === 'dueDate') {
          fields[field] = Date.parse(value)
        } else {
          if (!isEmpty(fieldObj)) {
            let fieldObject = fieldObj[0]

            // lookup handling
            if (this.isLookup(fieldObject) && !isEmpty(action.value.id)) {
              action.value.id = parseInt(action.value.id)
              fields[field] = action.value
            }

            // picklist handling
            else if (this.isPicklist(fieldObject) && !isEmpty(action.value)) {
              fields[field] = +action.value
            }

            //date and dateTime Handling
            else if (
              (this.isDate(fieldObject) || this.isDateTime(fieldObject)) &&
              !isEmpty(action.value)
            ) {
              fields[field] = moment(action.value).valueOf()
            }

            //multiLookUp Handling
            else if (
              this.isMultiLookUp(fieldObject) &&
              !isEmpty(action.value)
            ) {
              let actionObject = []
              valueArray.forEach(index => {
                let valueArrayItems = {}
                valueArrayItems['id'] = index
                actionObject.push(valueArrayItems)
              })
              fields[field] = actionObject
            } else {
              // other
              fields[field] = action.value
            }
          } else {
            fields[field] = action.value
          }
        }
      })

      let data = []
      for (let woID of this.selectedListItemsIds) {
        data.push({ id: woID, ...fields })
      }

      this.bulkUpdate(data)
      this.closeBulkActionDialog()
      this.resetSelectAll()
    },
    bulkUpdate(data) {
      let { moduleName } = this
      const body = {
        workorder: data,
      }
      return API.post(`v3/modules/bulkPatch/${moduleName}`, {
        data: body,
        moduleName,
      })
        .then(({ error }) => {
          if (error) {
            this.$message.error(this.$t(error.message))
          } else {
            this.$message.success(
              this.$t('maintenance._workorder.wo_update_success')
            )
          }
        })
        .then(() => {
          this.resetSelectAll()
          this.closeBulkActionDialog()
          this.loadRecords()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
    },
    isLookup(field) {
      const LOOKUP_DATATYPE = 7
      return field?.dataType && field.dataType === LOOKUP_DATATYPE
    },

    isPicklist(field) {
      const PICKLIST_DATATYPE = 8
      return field?.dataType && field.dataType === PICKLIST_DATATYPE
    },
    isDate(field) {
      const DATE_DATATYPE = 5
      return field?.dataType && field.dataType === DATE_DATATYPE
    },
    isDateTime(field) {
      const DATETIME_DATATYPE = 6
      return field?.dataType && field.dataType === DATETIME_DATATYPE
    },
    isMultiLookUp(field) {
      const MULTI_LOOKUP_DATATYPE = 13
      return field?.dataType && field.dataType === MULTI_LOOKUP_DATATYPE
    },
    openWorkorderOverview(id) {
      let { viewname, moduleName } = this
      if (id === undefined || id <= 0) {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        if (name) {
          return {
            name,
            params: {
              viewname: viewname,
              id,
            },
          }
        }
      } else {
        return {
          name: 'wosummarynew',
          params: {
            viewname,
            id,
          },
        }
      }
    },
    openList() {
      if (isWebTabsEnabled()) {
        let { viewname } = this
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'workorder',
          params: { viewname: this.viewname },
          query: this.$route.query,
        })
      }
    },
    getTicketStatusDisplayName(record) {
      return (
        this.getTicketStatus(
          this.$getProperty(record, 'moduleState.id', -1),
          this.moduleName
        ) || {}
      ).displayName
    },
    // quick edit methods
    canShowSpaceAssetChooser(workorder) {
      if (this.canEditRecord(workorder)) {
        this.showSpaceAssetDialog = true
        this.workOrder = workorder
        this.workorderSelected = workorder.id
      }
    },
    associateSpaceAsset(resource) {
      this.showSpaceAssetDialog = false
      this.updateRecordResource(this.workOrder, resource)
      this.workOrder = -1
    },
    isRequestedState(workorder) {
      let { approvalStatus } = workorder || {}

      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    isRecordLocked(workorder) {
      let moduleState = this.$getProperty(workorder, 'moduleState.id', null)
      return moduleState && this.isStatusLocked(moduleState.id, 'workorder')
    },
    canEditRecord(workorder) {
      let isLocked = this.isRecordLocked(workorder)
      let isRequestedState = this.isRequestedState(workorder)

      return !isLocked && !isRequestedState
    },
    canShowTeamStaffChooser(workorder) {
      return (
        !isEmpty(workorder) &&
        this.hasChangeOwnershipPermission &&
        this.canEditRecord(workorder)
      )
    },
    // misc methods
    closeBulkActionDialog() {
      this.bulkAction = false
    },
    handleBulkActiondialogClose() {
      this.bulkAction = false
      this.resetSelectAll()
      this.loadRecords()
    },
    filter() {
      let filter = {}
      let { siteId } = this.workOrder || {}
      if (!isEmpty(siteId)) {
        filter.site = Number(siteId)
      }
      return filter
    },
    redirectToOverview(id) {
      let { moduleName, viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        }

        return name && route
      } else {
        return {
          name: 'wo-summary',
          params: { moduleName, viewname, id },
          query: this.$route.query,
        }
      }
    },
    getLookUpDisplayName(fieldName, data) {
      let { primaryValue, name, displayName, subject } = data[fieldName] || {}
      let value = displayName || name || subject
      return !isEmpty(primaryValue) ? primaryValue : value
    },
    resetSelectAll() {
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },

    //  V3 Updates
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
          this.resetSelectAll()
          this.closeBulkActionDialog()
          this.loadRecords()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
    },
    async bulkUpdateWithV3(fields) {
      this.isSaving = true

      let { moduleName, viewname, filters } = this
      this.currentPageCount = null
      let params = {
        moduleName,
        viewname,
        filters,
        count: this.recordCount,
      }
      let viewName = viewname

      const body = {
        workorder: fields,
      }

      this.showLoading = true
      return await API.post(`v3/bulkupdate/workorder/update  `, {
        data: body,
        moduleName,
        viewName,
        filters: JSON.stringify(filters),
        count: this.recordCount,
      }).catch(() => {
        this.$message.error(this.$t('maintenance._workorder.wo_update_failed'))
      })
    },
    async workOrderDeleteV3(idList) {
      let { moduleName } = this
      let { error } = await API.deleteRecord(moduleName, idList)

      if (error) {
        this.$message.error(this.$t(error.message))
      } else {
        this.$message.success(
          `${this.$t('maintenance._workorder.wo_delete_success')}`
        )
        this.refreshRecordDetails(true)
        this.resetSelectAll()
      }
    },
    canDeleteRecord(record) {
      let { hasDeletePermission } = this
      let { moduleState } = record || {}
      let { typeCode } = moduleState || {}
      let canDelete = !isEmpty(moduleState)
        ? typeCode === 1 && hasDeletePermission
        : hasDeletePermission

      return canDelete
    },
    getTicketTypeName(record) {
      let typeId = this.$getProperty(record, 'type.id', -1)
      let ticketType = this.getTicketType(typeId) || {}

      return this.$getProperty(ticketType, 'name', '---')
    },
    isResourceAvailable(record) {
      let resourceId = this.$getProperty(record, 'resource.id', -1)
      return resourceId > -1
    },
    getResourceType(record) {
      let resourceType = this.$getProperty(record, 'resource.resourceType', -1)
      return resourceType === 1
    },
    async OpenBulkUpdate() {
      if (this.isDisable) {
        if (this.recordCount > this.bulkActionAcount) {
          let dialogObj = {
            title: `Bulk update limit exceeds`,
            htmlMessage: `Bulk update can be performed upto ${this.bulkActionAcount} records . Please ensure your selection does not exceed this limit.`,
            rbDanger: true,
            rbLabel: 'Confirm',
          }
          this.$dialog.confirm(dialogObj).then(value => {
            if (value) {
              this.isAllSelected = true
            }
          })
        } else {
          this.bulkAction = true
          let moduleName = 'workorder'
          let params = {
            moduleName,
          }
          let { data, error } = API.get(
            `v3/bulkupdate/workorder/fetchFields`,
            params
          )
        }
      } else {
        this.bulkAction = true
        let moduleName = 'workorder'
        let params = {
          moduleName,
        }
        let { data, error } = API.get(
          `v3/bulkupdate/workorder/fetchFields`,
          params
        )
      }
    },
    clearSelection() {
      this.bulkAction = false
      this.isAllSelected = false
    },
    validateTotalCount(selectedItem) {
      if (!selectedItem) {
        this.updateBulkRecord = false
        this.isDisable = true
        if (this.recordCount > this.bulkActionAcount) {
          this.isBulkUpdateVisible = true
        }
      } else {
        this.isDisable = false
        this.isBulkUpdateVisible = false
        this.updateBulkRecord = true
      }
    },
    enableNewBulkUpdate() {
      return this.$helpers.isLicenseEnabled('BULK_UPDATE')
    },
  },
}
</script>
<style scoped lang="scss">
.bulk-update-bar {
  .bulk-action-container {
    padding: 5px;
    background-color: #f3f4f5;
    border-bottom: 1px solid #ebeef5;
  }
}

.create-btn {
  margin-top: -10px;
}

.wo-table-btn-spacing {
  margin-left: 4px;
}

.subject-underline:hover {
  color: #46a2bf;
  text-decoration: underline;
}

.custom-button-container {
  min-height: 25px;
}
.fc-card-wo-list-view {
  .fc-list-table-container {
    .el-table {
      height: calc(100vh - 175px) !important;
    }
  }
}
</style>
