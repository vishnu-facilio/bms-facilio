<template>
  <CommonListLayout
    :moduleName="moduleName"
    :visibleViewCount="5"
    :getPageTitle="() => viewName"
    pathPrefix="/app/wo/planned/"
  >
    <template #header>
      <template v-if="!showSearch">
        <pagination
          :total="listCount"
          :perPage="50"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="listCount > 0">|</span>
      </template>

      <el-tooltip
        effect="dark"
        :content="$t('common._common.search')"
        placement="left"
      >
        <search
          :key="moduleName + '-search'"
          :config="filterConfig"
          :moduleName="moduleName"
          :defaultFilter="defaultFilter"
          class="fc-black-small-txt-12"
        ></search>
      </el-tooltip>

      <template v-if="!showSearch">
        <span class="separator pL10">|</span>
        <div>
          <button
            v-if="hasCreatePMv1Permission"
            class="fc-create-btn"
            style="margin-top: -10px;"
            @click="redirectToFormCreation()"
          >
            <i class="el-icon-plus white-color f12 fw-bold"></i>
          </button>
        </div>
      </template>
    </template>

    <template #content>
      <div>
        <tags :disableSaveFilters="true"></tags>
      </div>
      <div>
        <div
          v-if="showLoading"
          class="flex-middle fc-empty-white mL10 mT10 mR10 mB10 pR10 width100"
        >
          <spinner :show="showLoading" size="80"></spinner>
        </div>
        <div
          v-if="$validation.isEmpty(preventiveList) && !showLoading"
          class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column common-empty-data-con m10"
        >
          <inline-svg
            src="svgs/emptystate/maintenance"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            {{ $t('maintenance.pm_list.no_pm') }}
          </div>
        </div>
        <div
          class="height100"
          v-if="!showLoading && !$validation.isEmpty(preventiveList)"
        >
          <div
            class="fc-list-view p10 pT0 height100 mT10 fc-list-table-container fc-table-td-height fc-table-viewchooser pB100"
            :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
          >
            <div
              v-if="selectedPreventive.length > 0"
              class="pull-left table-header-actions"
            >
              <div class="action-btn-slide btn-block">
                <button
                  class="btn btn--tertiary"
                  @click="activate(selectedPreventive)"
                  :class="{ disabled: actions.activate.loading }"
                  v-if="currentView !== 'active'"
                >
                  <i
                    class="fa fa-check b-icon"
                    v-if="!actions.delete.loading"
                  ></i>
                  <i
                    class="fa fa-circle-o-notch b-icon fa-spin"
                    aria-hidden="true"
                    v-if="actions.activate.loading"
                  ></i>
                  {{ $t('maintenance.pm_list.activate') }}
                </button>
                <button
                  class="btn btn--tertiary"
                  @click="deactivate(selectedPreventive)"
                  :class="{ disabled: actions.deactivate.loading }"
                  v-if="currentView !== 'inactive'"
                >
                  <i
                    class="fa fa-ban b-icon"
                    v-if="!actions.delete.loading"
                  ></i>
                  <i
                    class="fa fa-circle-o-notch b-icon fa-spin"
                    aria-hidden="true"
                    v-if="actions.deactivate.loading"
                  ></i>
                  {{ $t('maintenance.pm_list.deactivate') }}
                </button>
                <button
                  class="btn btn--tertiary"
                  @click="deletePreventive(selectedPreventive, index)"
                  :class="{ disabled: actions.delete.loading }"
                >
                  <i
                    class="fa fa-trash-o b-icon"
                    v-if="!actions.delete.loading"
                  ></i>
                  <i
                    class="fa fa-circle-o-notch b-icon fa-spin"
                    aria-hidden="true"
                    v-if="actions.delete.loading"
                  ></i>
                  {{ $t('common._common.delete') }}
                </button>
              </div>
            </div>
            <el-table
              :data="preventiveList"
              style="width: 100%"
              height="100%"
              :index="indexMethod"
              v-if="currentView !== 'upcoming'"
              @selection-change="selectPmList"
              class="fc-list-eltable"
              :row-class-name="'no-hover'"
            >
              <el-table-column type="selection" width="60"></el-table-column>
              <el-table-column
                fixed
                width="90"
                :label="$t('common._common.id')"
              >
                <template v-slot="preventive">
                  <div class="fc-id">{{ '#' + preventive.row.id }}</div>
                </template>
              </el-table-column>
              <el-table-column
                fixed
                width="300"
                :label="$t('maintenance.pm_list.title')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div
                    class="text-left main-field-column"
                    @click="
                      openSummary(
                        preventive.row.id,
                        preventive.row.pmCreationTypeEnum
                      )
                    "
                  >
                    <div
                      v-tippy
                      small
                      :title="preventive.row.title"
                      class="fw5 q-item-label ellipsis primary-field max-width300px"
                    >
                      {{ preventive.row.title }}
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="170"
                :label="$t('maintenance.wr_list.type')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="q-item-label">
                    {{
                      preventive.row.woTemplate.typeId > 0
                        ? getPMType(preventive.row.woTemplate.typeId)
                        : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="220"
                :label="$t('maintenance.wr_list.assignedto')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div
                    class="q-item-division relative-position"
                    style="min-width: 180px;display: flex;align-items: center;"
                  >
                    <div
                      class="wo-assigned-avatar"
                      style="width:38px;float: left;"
                    >
                      <user-avatar
                        size="md"
                        class="pm-list-avatar"
                        :user="getUser(preventive.row.woTemplate.assignedToId)"
                        :group="
                          getGroup(preventive.row.woTemplate.assignmentGroupId)
                        "
                        :showPopover="true"
                        :showLabel="true"
                      ></user-avatar>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance.wr_list.space_asset')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <span
                    v-if="preventive.row.pmCreationType === 1"
                    class="q-item-label"
                    >{{
                      preventive.row.woTemplate.resourceId !== -1 &&
                      preventive.row.woTemplate.resource
                        ? preventive.row.woTemplate.resource.name
                        : '---'
                    }}</span
                  >
                  <span
                    v-else-if="
                      preventive.row.pmCreationType === 3 &&
                        preventive.row.assignmentType === 8
                    "
                  >
                    {{
                      preventive.row.siteIds &&
                      preventive.row.siteIds.length > 0
                        ? preventive.row.siteIds.length + ' - Site(s)'
                        : '---'
                    }}
                  </span>
                  <span v-else>{{
                    preventive.row.pmCategoryDescription
                      ? preventive.row.pmCategoryDescription
                      : '---'
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance._workorder.status')"
                class="pR0"
                v-if="currentView === 'all' || currentViewDetail.id !== -1"
              >
                <template v-slot="preventive">
                  <div
                    v-if="currentView === 'all' || currentViewDetail.id !== -1"
                    class="text-left"
                  >
                    <div class="q-item-label">
                      {{ preventive.row.status === 0 ? 'InActive' : 'Active' }}
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="280"
                :label="$t('maintenance._workorder.trigger')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="frequency-msg-txt">
                    {{
                      preventive.row.pmTriggerDescription
                        ? preventive.row.pmTriggerDescription
                        : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="280"
                :label="$t('maintenance._workorder.sites')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <el-tooltip
                    v-if="preventive.row.siteObjects"
                    effect="dark"
                    :open-delay="500"
                    placement="left"
                  >
                    <div slot="content">
                      <p
                        v-html="
                          getSitesAsString(preventive.row.siteObjects).replace(
                            /(?:\r\n|\r|\n)/g,
                            '<br />'
                          )
                        "
                      ></p>
                    </div>
                    <div class="frequency-msg-txt">
                      {{ getSitesMessage(preventive.row.siteObjects) }}
                    </div>
                  </el-tooltip>
                  <div v-else>
                    {{ '---' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="280"
                :label="$t('maintenance._workorder.ticket_category')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="frequency-msg-txt">
                    {{
                      preventive.row.ticketCategory != null
                        ? preventive.row.ticketCategory.displayName
                        : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="280"
                :label="$t('maintenance._workorder.vendor_name')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="frequency-msg-txt">
                    {{
                      preventive.row.vendor != null
                        ? preventive.row.vendor.name
                        : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="280"
                :label="$t('maintenance._workorder.next_execution_time')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div
                    v-if="preventive.row.nextExecutionTime > 0"
                    class="frequency-msg-txt"
                  >
                    <div>
                      {{ preventive.row.nextExecutionTime | formatDate(true) }}
                    </div>
                    <div>
                      {{
                        preventive.row.nextExecutionTime
                          | formatDate(false, true)
                      }}
                    </div>
                  </div>
                  <div v-else class="frequency-msg-txt">
                    {{ '---' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="280"
                :label="$t('maintenance._workorder.last_triggered')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div
                    v-if="
                      preventive.row.lastTriggeredTime &&
                        preventive.row.lastTriggeredTime > 0
                    "
                    class="frequency-msg-txt"
                  >
                    <div>
                      {{ preventive.row.lastTriggeredTime | formatDate(true) }}
                    </div>
                    <div>
                      {{
                        preventive.row.lastTriggeredTime
                          | formatDate(false, true)
                      }}
                    </div>
                  </div>
                  <div v-else class="frequency-msg-txt">
                    {{ '---' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column width="100" label class="pR0">
                <template v-slot="preventive">
                  <!-- elipsis button | rendered only when edit or delete access is available -->
                  <el-dropdown
                    @command="handleMoreActions"
                    trigger="click"
                    v-if="
                      $hasPermission('planned:UPDATE') ||
                        $hasPermission('planned:DELETE')
                    "
                  >
                    <el-button
                      type="text"
                      icon="el-icon-more more-icon"
                      data-test-selector="Icon"
                    ></el-button>

                    <el-dropdown-menu slot="dropdown">
                      <!-- conditional rendering based on edit permission -->
                      <span v-if="$hasPermission('planned:UPDATE')">
                        <el-dropdown-item
                          v-if="preventive.row.status"
                          :command="{
                            action: 'executePMs',
                            data: preventive.row,
                          }"
                          data-test-selector="Execute_Now"
                          >{{
                            $t('maintenance.pm_list.execute_now')
                          }}</el-dropdown-item
                        >
                        <el-dropdown-item
                          v-if="!preventive.row.status"
                          :command="{
                            action: 'activate',
                            data: preventive.row,
                          }"
                          data-test-selector="Activate"
                          >{{
                            $t('maintenance.pm_list.activate')
                          }}</el-dropdown-item
                        >
                        <el-dropdown-item
                          v-else-if="
                            !preventive.row.data ||
                              !preventive.row.data.woGenerationStatus
                          "
                          :command="{
                            action: 'deactivate',
                            data: preventive.row,
                          }"
                          data-test-selector="Deactivate"
                          >{{
                            $t('maintenance.pm_list.deactivate')
                          }}</el-dropdown-item
                        >
                        <el-dropdown-item
                          v-if="
                            !preventive.row.data ||
                              !preventive.row.data.woGenerationStatus
                          "
                          :command="{ action: 'edit', data: preventive.row }"
                          data-test-selector="Edit"
                          >{{ $t('common._common.edit') }}</el-dropdown-item
                        >
                      </span>
                      <!-- conditional rendering based on delete permission -->
                      <span v-if="$hasPermission('planned:DELETE')">
                        <el-dropdown-item
                          v-if="
                            !preventive.row.data ||
                              !preventive.row.data.woGenerationStatus
                          "
                          :command="{
                            action: 'delete',
                            data: preventive.row,
                          }"
                          data-test-selector="Delete"
                          >{{ $t('common._common.delete') }}</el-dropdown-item
                        >
                      </span>
                    </el-dropdown-menu>
                  </el-dropdown>
                </template>
              </el-table-column>
            </el-table>
            <el-table v-else>
              <el-table-column
                width="100"
                :label="$t('common._common.id')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="fc-id">{{ '#' + preventive.row.value.id }}</div>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance.pm_list.title')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div
                    class="text-left main-field-column"
                    @click="openSummary(preventive.row.id)"
                  >
                    <div class="q-item-label ellipsis primary-field">
                      {{ preventive.row.value.title }}
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance.wr_list.type')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="q-item-label">
                    {{
                      preventive.row.value.typeId > 0
                        ? getPMType(preventive.row.value.typeId)
                        : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance.pm_list.created_by')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <user-avatar
                    size="sm"
                    :user="getUser(preventive.row.value.createdById)"
                  ></user-avatar>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance._workorder.trigger')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div
                    v-if="
                      preventive.row.value.triggers &&
                        preventive.row.value.triggers.length > 0 &&
                        preventive.row.value.triggers[0].schedule
                    "
                  >
                    <span
                      class="q-item-label"
                      style="text-transform: uppercase;"
                      v-if="
                        preventive.row.value.triggers[0].schedule
                          .frequencyTypeEnum === 'DO_NOT_REPEAT'
                      "
                      >{{ $t('maintenance.pm_list.one_time') }}</span
                    >
                    <span
                      class="q-item-label"
                      style="text-transform: uppercase;"
                      v-if="
                        preventive.row.value.triggers[0].schedule
                          .frequencyTypeEnum === 'DAILY'
                      "
                      >{{ $t('maintenance.pm_list.daily') }}</span
                    >
                    <span
                      class="q-item-label"
                      style="text-transform: uppercase;"
                      v-if="
                        preventive.row.value.triggers[0].schedule
                          .frequencyTypeEnum === 'WEEKLY'
                      "
                      >{{ $t('maintenance.pm_list.weekly') }}</span
                    >
                    <span
                      class="q-item-label"
                      style="text-transform: uppercase;"
                      v-if="
                        preventive.row.value.triggers[0].schedule
                          .frequencyTypeEnum === 'MONTHLY_DAY'
                      "
                      >{{ $t('maintenance.pm_list.monthly') }}</span
                    >
                    <span
                      class="q-item-label"
                      style="text-transform: uppercase;"
                      v-if="
                        preventive.row.value.triggers[0].schedule
                          .frequencyTypeEnum === 'MONTHLY_WEEK'
                      "
                      >{{ $t('maintenance.pm_list.monthly') }}</span
                    >
                    <span
                      class="q-item-label"
                      style="text-transform: uppercase;"
                      v-if="
                        preventive.row.value.triggers[0].schedule
                          .frequencyTypeEnum === 'YEARLY'
                      "
                      >{{ $t('maintenance.pm_list.yearly') }}</span
                    >
                    {{ preventive.row.value.triggers }}
                  </div>
                  <div v-else>
                    <span class="q-item-label">---</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="200"
                :label="$t('maintenance.pm_list.scheduled')"
                class="pR0"
              >
                <template v-slot="preventive">
                  <div class="q-item-label">
                    {{ (preventive.row.key * 1000) | fromNow }}
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <space-asset-multi-chooser
              :visibility.sync="chooserVisibility"
              :siteId.sync="siteId"
              @associate="associateResource"
              :query="resourceQuery"
              :initialValues="resourceData"
              :showAsset="true"
              :disable="true"
              :filter="filter"
            ></space-asset-multi-chooser>
            <new-work-order-new
              :visibility="showCreateNewDialog"
              defaultForm="newPreventiveMaintenance"
            ></new-work-order-new>
            <edit-preventive
              v-if="showEditPreventiveDialog"
              :preventive="editPreventiveData"
            ></edit-preventive>

            <el-dialog
              v-if="showExecutionConfig"
              :visible.sync="showExecutionConfig"
              :append-to-body="true"
              width="30%"
              title="SELECT SPACE/ASSETS"
              class="fc-dialog-center-container resource-dialog"
            >
              <div class="mT20">
                <el-input
                  placeholder="search"
                  v-model="searchQuery"
                  @change="forceUpdate"
                  type="search"
                  class="fc-input-full-border2 text-input-icon-align input-padding width100"
                  prefix-icon="el-icon-search"
                ></el-input>
              </div>
              <table class="fc-pm-form-table clear-both">
                <thead>
                  <tr class="pm-trigger-head-tr">
                    <th class="pT10 pB10">
                      <el-checkbox
                        v-model="selectAllResources"
                        @change="selected"
                        class="pR20"
                      ></el-checkbox
                      >SELECT ALL
                    </th>
                  </tr>
                </thead>
                <tbody class="mT10">
                  <tr
                    v-for="(resource, key) in resources"
                    :key="key"
                    v-show="
                      !searchQuery.trim() ||
                        resource.name
                          .trim()
                          .toLowerCase()
                          .startsWith(searchQuery.trim().toLowerCase())
                    "
                    class="mB10"
                  >
                    <td class="pT15 pB15">
                      <el-checkbox
                        @change="forceUpdate"
                        v-model="resources[key].selected"
                        class="pR20"
                      ></el-checkbox>
                      {{ resource.name }}
                    </td>
                  </tr>
                </tbody>
              </table>
              <div class="modal-dialog-footer">
                <el-button
                  class="modal-btn-cancel"
                  @click="showExecutionConfig = false"
                  >CANCEL</el-button
                >
                <el-button
                  type="primary"
                  class="modal-btn-save"
                  :loading="actions.executePM.multiExecLoading"
                  @click="executPMs(currenPMId, resources)"
                  >EXECUTE</el-button
                >
              </div>
            </el-dialog>
            <!--
            <el-dialog
              :visible.sync="showExecutionConfig"
              :append-to-body="true"
              width="30%"
              title="SELECT SPACE/ASSETS"
              class="fc-dialog-center-container resource-dialog"
              :before-close="beforeConfigClose"
            >
              <div class="mT20">
                <el-input
                  placeholder="search"
                  v-model="searchQuery"
                  @change="forceUpdate"
                  type="search"
                  class="fc-input-full-border2 text-input-icon-align input-padding width100"
                  prefix-icon="el-icon-search"
                ></el-input>
              </div>
              <table class="fc-pm-form-table">
                <thead>
                  <tr class="pm-trigger-head-tr">
                    <th class="pT10 pB10">
                      <el-checkbox
                        v-model="selectAllResources"
                        @change="selected"
                        class="pR20"
                      ></el-checkbox
                      >SELECT ALL
                    </th>
                  </tr>
                </thead>
                <tbody class="mT10">
                  <tr
                    v-for="(resource, key) in resources"
                    :key="key"
                    v-show="
                      !searchQuery.trim() ||
                        resource.name
                          .trim()
                          .toLowerCase()
                          .startsWith(searchQuery.trim().toLowerCase())
                    "
                    class="mB10"
                  >
                    <td class="pT15 pB15">
                      <el-checkbox
                        @change="forceUpdate"
                        v-model="resources[key].selected"
                        class="pR20"
                      ></el-checkbox>
                      {{ resource.name }}
                    </td>
                  </tr>
                </tbody>
              </table>
              <div class="modal-dialog-footer">
                <el-button
                  class="modal-btn-cancel"
                  @click="cancelExecutionConfig"
                  >CANCEL</el-button
                >
                <el-button
                  type="primary"
                  class="modal-btn-save"
                  @click="executeMultiPM"
                  >EXECUTE</el-button
                >
              </div>
            </el-dialog> -->
          </div>
        </div>
      </div>
    </template>
  </CommonListLayout>
</template>
<script>
import UserAvatar from '@/avatar/User'
import EditPreventive from 'pages/workorder/preventive/EditPreventive'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import NewWorkOrderNew from 'pages/workorder/widgets/dialogs/NewWorkOrder'
import infiniteScroll from 'vue-infinite-scroll'
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Pagination from '@/list/FPagination'
import Search from 'newapp/components/Search'
import Tags from 'newapp/components/Tags'
import { mapState, mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '../../../../util/validation'

export default {
  components: {
    UserAvatar,
    EditPreventive,
    SpaceAssetMultiChooser,
    NewWorkOrderNew,
    CommonListLayout,
    Pagination,
    Search,
    Tags,
  },
  directives: { infiniteScroll },
  data() {
    return {
      cancelExecutionConfig: false,
      resources: [],
      selectAllResources: false,
      searchQuery: '',
      showExecutionConfig: false,
      currenPMId: null,
      multiExecLoading: false,
      filter: {},
      searchTextfield: '',
      loading: false,
      selectAll: false,
      newViewName: '',
      idsResource: [],
      actions: {
        activate: {
          loading: false,
        },
        deactivate: {
          loading: false,
        },
        delete: {
          loading: false,
        },
        executePM: {
          loading: false,
          multiExecLoading: false,
        },
      },
      resourceData: {},
      siteId: null,
      selectedPmId: '',
      isMultiResource: this.isAsset,
      selectedPreventive: [],
      preventiveList: [],
      resourceQuery: null,
      chooserVisibility: false,
      upcomingPreventiveList: [],
      fetchingMore: false,
      nextPage: 1,
      hasMorePages: false,
      perPage: 50,
      emptyName: {
        name: 'Unknow',
      },
      selectedPmObj: [],
      listCount: '',
      defaultFilter: 'title',
      filterConfig: {
        moduleName: 'preventivemaintenance',
        path: '/app/wo/planned/',
        data: {
          title: {
            label: 'Title',
            displayType: 'string',
            value: '',
          },
          id: {
            label: this.$t('maintenance._workorder.pm_id'),
            displayType: 'number',
          },
          category: {
            label: this.$t('maintenance.wr_list.category'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'categoryId',
          },
          siteId: {
            label: 'Site',
            options: {},
            value: [],
            displayType: 'lookup',
            key: 'siteId',
          },
          resourceIdSpace: {
            label: this.$t('maintenance._workorder.space'),
            displayType: 'lookup',
            options: {},
            value: [],
            key: 'resourceIdSpace',
          },
          resourceIdAsset: {
            label: this.$t('maintenance._workorder.asset'),
            displayType: 'lookup',
            options: {},
            value: [],
            key: 'resourceIdAsset',
          },
          assignedTo: {
            label: this.$t('maintenance.wr_list.staff/team'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'assignedToId',
          },
          ticketType: {
            label: this.$t('maintenance.wr_list.type'),
            displayType: 'select',
            options: {},
            value: [],
            key: 'typeId',
            views: ['all', 'active', 'inactive'],
          },
          frequency: {
            label: this.$t('maintenance._workorder.frequency'),
            displayType: 'select',
            operatorId: 9,
            options: this.$constants.FACILIO_FREQUENCY,
            value: [],
          },
          priority: {
            label: this.$t('maintenance.wr_list.priority'),
            displayType: 'select',
            options: {},
            key: 'priorityId',
            value: [],
          },
        },
        availableColumns: [
          'title',
          'category',
          'priority',
          'frequency',
          'ticketType',
          'assignedTo',
          'asset',
          'space',
          'resourceIdAsset',
          'resourceIdSpace',
          this.$helpers.isLicenseEnabled('TENANTS') ? 'tenant' : '',
        ],
        fixedCols: ['title'],
        saveView: true,
        includeParentCriteria: true, // Temporary
        disableColumnCustomization: true,
      },
    }
  },
  mounted() {
    this.loadPreventiveList()
    this.loadPMCount()
  },
  created() {
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings')

    if (this.$helpers.isLicenseEnabled('TENANTS')) {
      let tenantObj = {
        label: 'Tenant',
        displayType: 'select',
        options: {},
        value: [],
        key: 'tenantId',
      }
      this.$set(this.filterConfig.data, 'tenant', tenantObj)
    }
  },
  computed: {
    ...mapState({
      currentViewDetail: state => state.view.currentViewDetail,
      assetCategory: state => state.assetCategory,
      showSearch: state => state.search.active,
      currentTab: state => state.webtabs.selectedTab,
    }),

    ...mapGetters([
      'getTicketTypePickList',
      'getSpaceCategoryPickList',
      'getUser',
      'getBuildingsPickList',
      'getGroup',
    ]),
    ...mapGetters('webtabs', ['tabHasPermission']),
    buildingList() {
      return this.getBuildingsPickList()
    },
    tickettype() {
      return this.getTicketTypePickList()
    },
    showLoading() {
      return this.loading || this.$store.state.view.detailLoading
    },
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    scrollDisabled() {
      return this.fetchingMore || !this.hasMorePages || this.loading
    },
    page() {
      return this.$route.query.page || 1
    },
    showCreateNewDialog() {
      if (this.$route.query.create) {
        return true
      }
      return false
    },
    showEditPreventiveDialog() {
      if (this.$route.query.edit) {
        return true
      }
      return false
    },
    currentView() {
      let viewName = this.$attrs.viewname || this.$route.params.viewname

      return viewName || 'all'
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    search() {
      if (this.$route.query.searchText) {
        return this.$route.query.searchText
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
    moduleName() {
      return 'preventivemaintenance'
    },
    viewName() {
      let viewList = {
        all: 'All Preventive Maintenance',
        active: 'Active Preventive Maintenance',
        preventive: 'Preventive Maintenance',
        corrective: 'Corrective Preventive Maintenance',
        rounds: 'Rounds Preventive Maintenance',
        breakdown: 'Breakdown Preventive Maintenance',
        compliance: 'Compliance Preventive Maintenance',
        upcoming: 'Upcoming Preventive Maintenance',
        inactive: 'Inactive Preventive Maintenance',
      }
      let title = this.$getProperty(viewList, this.currentView, null)

      if (title) return title
      return 'Planned Maintenance'
    },
    hasCreatePMv1Permission() {
      if (isWebTabsEnabled()) {
        let { currentTab, tabHasPermission } = this
        return tabHasPermission('CREATE', currentTab)
      } else {
        let { $hasPermission } = this
        return $hasPermission('planned:CREATE')
      }
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.resetPagination()
        this.loadPreventiveList()
        this.loadPMCount()
        this.selectAll = false
        this.selectedPreventive = []
      }
    },
    filters: function() {
      this.resetPagination()
      this.loadPreventiveList()
      this.loadPMCount()
      this.selectAll = false
      this.selectedPreventive = []
    },
    search: function() {
      this.resetPagination()
      this.loadPreventiveList()
      this.selectAll = false
      this.selectedPreventive = []
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadPreventiveList()
        this.selectAll = false
        this.selectedPreventive = []
      }
    },
    selectAll: function(val) {
      let self = this
      if (val) {
        this.selectedPreventive = []
        this.preventiveList.filter(function(preventive) {
          self.selectedPreventive.push(preventive.id)
        })
      } else {
        if (this.selectedPreventive.length === this.preventiveList.length) {
          this.selectedPreventive = []
        }
      }
    },
  },
  methods: {
    selected(val) {
      for (let i = 0; i < this.resources.length; i++) {
        if (
          !this.searchQuery.trim() ||
          this.resources.name
            .trim()
            .toLowerCase()
            .startsWith(this.searchQuery.trim().toLowerCase())
        ) {
          this.resources[i].selected = val
        }
      }
      this.$forceUpdate()
    },
    loadResourcePlanner(id) {
      return this.$http.get(`v2/workorders/pmResourcePlanner?id=${id}`)
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    selectPmList(selectedpm) {
      this.selectedPmObj = selectedpm
      this.selectedPreventive = selectedpm.map(value => value.id)
    },
    resourceLabel({
      assignmentType,
      spaceCategoryId,
      assetCategoryId,
      resources,
      buildingId,
    }) {
      let buildingName = this.buildingList[buildingId]
      if (assignmentType === 1) {
        if (!resources || !resources.length) {
          if (buildingName) {
            return `${buildingName} - All Floors`
          }
          return 'All Floors'
        } else {
          if (buildingName) {
            return `${buildingName} - ${resources.length} Floors`
          }
          return `${resources.length} Floors`
        }
      } else if (assignmentType === 3) {
        if (!resources || !resources.length) {
          if (buildingName) {
            return `${buildingName} - All ${
              this.spacecategory[Number(spaceCategoryId)]
            }`
          }
          return `All ${this.spacecategory[Number(spaceCategoryId)]}`
        } else {
          if (buildingName) {
            return `${buildingName} - ${resources.length} ${
              this.spacecategory[Number(spaceCategoryId)]
            }`
          }
          return `${resources.length} ${
            this.spacecategory[Number(spaceCategoryId)]
          }`
        }
      } else if (assignmentType === 4) {
        if (!resources || !resources.length) {
          if (buildingName) {
            return `${buildingName} - All ${
              this.assetCategory.find(i => i.id === Number(assetCategoryId))
                .displayName
            }`
          }
          return `All ${
            this.assetCategory.find(i => i.id === Number(assetCategoryId))
              .displayName
          }`
        } else {
          if (buildingName) {
            return `${buildingName} - ${resources.length} ${
              this.assetCategory.find(i => i.id === Number(assetCategoryId))
                .displayName
            }`
          }
          return `${resources.length} ${
            this.assetCategory.find(i => i.id === Number(assetCategoryId))
              .displayName
          }`
        }
      }
      return '---'
    },
    resetPagination() {
      this.nextPage = 1
      this.fetchingMore = false
      this.hasMorePages = false
      this.preventiveList = []
      this.upcomingPreventiveList = []
    },
    loadMore() {
      if (!this.scrollDisabled) {
        this.fetchingMore = true
        this.loadPreventiveList()
      }
    },
    openSummary(id, pmCreationType) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { currentView, moduleName } = this
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: currentView,
                pmCreationType: pmCreationType,
                id,
              },
            })
          }
        } else {
          if (pmCreationType && pmCreationType === 'MULTI_SITE') {
            this.$router.push({ path: 'multisummary/' + id })
          } else {
            this.$router.push({ path: 'summary/' + id })
          }
        }
      }
    },
    associateResource(selectedObj) {
      this.selectedResource = selectedObj
      this.selectedResourceList = selectedObj.resourceList
      this.isIncludeResource = selectedObj.isInclude
      let data = {}
      let fieldName
      let url
      url = '/workorder/addBulkPreventiveMaintenance'
      let self = this
      if (this.selectedResourceList) {
        fieldName = this.isIncludeResource
          ? 'includedResources'
          : 'excludedResources'
        this.idsResource = this.selectedResourceList.map(
          resource => resource.id
        )
        data.assetids = this.idsResource
        data.type = fieldName
        data.pmId = this.selectedPmId
        data.categoryId = this.resourceData.assetCategory
        self.$http.post(url, data).then(function() {
          self.nextPage = 1
          self.loadPreventiveList()
        })
      } else {
        this.selectedResource = selectedObj
        this.selectedResourceList = selectedObj.resourceList
        this.isIncludeResource = selectedObj.isInclude
        url = '/workorder/addBulkPreventiveMaintenance'
        if (this.selectedResourceList) {
          fieldName = this.isIncludeResource
            ? 'includedResources'
            : 'excludedResources'
          this.idsResource = this.selectedResourceList.map(
            resource => resource.id
          )
          data.assetids = this.idsResource
          data.type = fieldName
          data.pmId = this.selectedPmId
          data.categoryId = this.resourceData.assetCategory
          self.$http.post(url, data).then(function() {
            self.nextPage = 1
            self.loadPreventiveList()
          })
        } else {
          fieldName = 'all'
          data.type = fieldName
          data.pmId = this.selectedPmId
          data.categoryId = this.resourceData.assetCategory
          self.$http.post(url, data).then(function() {
            self.nextPage = 1
            self.loadPreventiveList()
          })
        }
      }
      this.chooserVisibility = false
      this.filter = {}
      this.resourceQuery = null
    },
    loadPreventiveList() {
      let url = `/planned/${this.currentView}`
      url += `?page=${this.page}&perPage=${this.perPage}`
      if (this.filters) {
        url += `&filters=${encodeURIComponent(JSON.stringify(this.filters))}`
        if (this.includeParentFilter) {
          url += '&includeParentFilter=true'
        }
      }
      if (this.search) {
        url += `&search=${this.search}`
      }
      this.loading = true
      this.$http.get(url).then(response => {
        if (response.data) {
          if (this.page === 1) {
            this.preventiveList = response.data
          } else {
            this.preventiveList = response.data
          }
        }
        this.loading = false
      })
    },
    loadPMCount() {
      let url1 = `/planned/pmCount?viewName=${this.currentView}`
      if (this.filters) {
        url1 += `&filters=${encodeURIComponent(JSON.stringify(this.filters))}`
        if (this.includeParentFilter) {
          url1 += '&includeParentFilter=true'
        }
      }
      if (this.search) {
        url1 += `&search=${this.search}`
      }
      url1 += `&count=true`
      this.$http
        .get(url1)
        .then(({ data }) => {
          if (data) {
            this.listCount = data.woCount
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
    async handleMoreActions(cmd) {
      if (cmd.action === 'deactivate') {
        this.changePMStatus(cmd.data.id, 0)
      } else if (cmd.action === 'activate') {
        this.changePMStatus(cmd.data.id, 1)
      } else if (cmd.action === 'executePMs') {
        let id = cmd.data.id
        this.currenPMId = id
        if (cmd.data.pmCreationType === 3) {
          this.resources = []
          await this.loadResourcePlanner(id).then(response => {
            let resourcePlanners = response.data.resourcePlanners
            resourcePlanners.forEach(r => {
              this.resources.push({
                id: r.resourceId,
                name: r.resourceName,
              })
            })
          })
          this.showExecutionConfig = true
          return
        }
        if (cmd.data.pmCreationType === 2) {
          this.actions.executePM.loading = true
          this.$http
            .get(`/workorder/getMultiplePMResources?pmId=${cmd.data.id}`)
            .then(resp => {
              if (resp.data && resp.data.multiPmResources) {
                this.currenPMId = cmd.data.id
                this.resources = resp.data.multiPmResources || []
                this.selectAllResources = false
                this.actions.executePM.multiExecLoading = false
                this.showExecutionConfig = true
              }
              this.actions.executePM.loading = false
            })
        } else {
          this.executPMs(id)
        }
      } else if (cmd.action === 'delete') {
        this.deletePreventive([cmd.data.id])
      } else if (cmd.action === 'applyTo') {
        this.selectedPmId = cmd.data.id
        if (cmd.data.woTemplate.resource) {
          this.resourceData = {
            assetCategory: cmd.data.woTemplate.resource.category.id,
          }
        }
        if (cmd.data.siteId && cmd.data.siteId > 0) {
          this.filter.siteId = cmd.data.siteId
        }
        this.chooserVisibility = true
      } else if (cmd.action === 'edit') {
        if (isWebTabsEnabled()) {
          let { moduleName } = this
          let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                id: cmd.data.id,
              },
            })
          }
        } else {
          if (cmd.data.pmCreationTypeEnum === 'MULTI_SITE') {
            this.$router.push({
              path: '/app/wo/multiplanned/new',
              query: {
                ...this.$route.query,
                edit: `${cmd.data.id}`,
              },
            })
          } else {
            this.$router.push({
              path: '/app/wo/planned/new',
              query: {
                ...this.$route.query,
                edit: `${cmd.data.id}`,
              },
            })
          }
        }
      }
    },
    getPMType(type) {
      return this.tickettype[type]
    },
    changePMStatus(id, status, message) {
      let self = this
      let url = '/workorder/changePreventiveMaintenanceStatus'
      let formdata = {}
      if (Array.isArray(id)) {
        formdata.id = id
      } else {
        formdata.id = [id]
      }
      formdata.preventivemaintenance = {
        status: status,
      }
      self.actions.delete.loading = true
      self.$http
        .post(url, formdata)
        .then(function() {
          for (let pmid of formdata.id) {
            let idx = self.preventiveList.indexOf(
              self.preventiveList.find(pm => pm.id === pmid)
            )
            if (self.currentView !== 'all') {
              self.preventiveList.splice(idx, 1)
            } else {
              self.preventiveList[idx].status = status
              self.toggleSelection(pmid)
            }
          }
          if (message) {
            self.$message({
              message: message,
              type: 'success',
            })
          }
          self.selectAll = false
        })
        .catch(function(error) {
          let message = self.$getProperty(
            error,
            'data.message',
            'Status Change of Planned maintenance failed!'
          )
          self.$message({
            message: message,
            type: 'error',
          })
          self.selectAll = false
        })

      self.actions.delete.loading = false
    },
    toggleSelection(id) {
      let idx = this.selectedPreventive.indexOf(id)
      if (idx === -1) {
        this.selectedPreventive.push(id)
      } else {
        this.selectedPreventive.splice(idx, 1)
      }
    },
    prettyScheduleObject(scheduleObj) {
      if (!scheduleObj) {
        return '---'
      }

      let prettyStr = ''
      if (scheduleObj.frequencyTypeEnum === 'DAILY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'day'
            : scheduleObj.frequency + ' days')
      } else if (scheduleObj.frequencyTypeEnum === 'WEEKLY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'week'
            : scheduleObj.frequency + ' weeks')
        let days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        prettyStr += ' on '
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          prettyStr += days[i + 1]
        }
      } else if (scheduleObj.frequencyTypeEnum === 'MONTHLY_DAY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'month'
            : scheduleObj.frequency + ' months')
        prettyStr += ' on '
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          prettyStr += scheduleObj.values[i] + this.nth(scheduleObj.values[i])
        }
      } else if (scheduleObj.frequencyTypeEnum === 'MONTHLY_WEEK') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'month'
            : scheduleObj.frequency + ' months')
        prettyStr += ' on ('
        let weeks = ['First', 'Second', 'Third', 'Fourth', 'Last']
        let days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        prettyStr += weeks[scheduleObj.weekFrequency - 1] + ' week'
        prettyStr += ' - '
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          prettyStr += days[i + 1]
        }
        prettyStr += ')'
      } else if (scheduleObj.frequencyTypeEnum === 'YEARLY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'year'
            : scheduleObj.frequency + ' years')
        prettyStr += ' on '
        let months = [
          'Jan',
          'Feb',
          'Mar',
          'Apr',
          'May',
          'Jun',
          'Jul',
          'Aug',
          'Sep',
          'Oct',
          'Nov',
          'Dec',
        ]
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          let j = scheduleObj.values[i]
          prettyStr += months[j - 1]
        }
      }
      return prettyStr
    },
    nth(d) {
      if (d > 3 && d < 21) return 'th'
      switch (d % 10) {
        case 1:
          return 'st'
        case 2:
          return 'nd'
        case 3:
          return 'rd'
        default:
          return 'th'
      }
    },
    activate(id) {
      this.changePMStatus(id, 1)
      this.selectedPreventive = []
    },
    deactivate(id) {
      this.actions.delete.loading = true
      this.changePMStatus(
        id,
        0,
        this.$t('maintenance.pm_list.pm_deactivate_success')
      )
      this.selectedPreventive = []
    },
    deletePreventive(ids, index) {
      let self = this
      self.$dialog
        .confirm({
          title: self.$t('maintenance.pm_list.pm_delete_title'),
          message: self.$t('maintenance.pm_list.pm_delete_body'),
          rbDanger: true,
          rbLabel: self.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/workorder/deletepm', { id: ids })
              .then(response => {
                if (response.data && typeof response.data === 'object') {
                  self.$message({
                    message: self.$t('maintenance.pm_list.pm_delete_success'),
                    type: 'success',
                  })
                  self.nextPage = 1
                  self.loadPreventiveList()
                  if (self.currentView === 'upcoming') {
                    self.upcomingPreventiveList.splice(index, 1)
                  } else {
                    self.preventiveList.splice(index, 1)
                  }
                }
                self.selectAll = false
              })
              .catch(function(error) {
                let message = self.$getProperty(
                  error,
                  'data.message',
                  'Deletion of Planned maintenance failed!'
                )
                self.$message({
                  message: message,
                  type: 'error',
                })
                self.selectAll = false
              })
          }
        })
    },
    executPMs(id, resources) {
      let self = this
      if (!resources) {
        self.actions.executePM.loading = true
      } else {
        self.actions.executePM.multiExecLoading = true
      }
      let data = {}
      data.pmId = id
      data.pmIncludeExcludeResourceContexts = this.resources
        .filter(i => i.selected)
        .map(k => {
          return { resourceId: k.id, pmId: id, isInclude: true }
        })
      self.$http
        .post('/workorder/executePreventiveMaintenance', data)
        .then(function() {
          self.multiExecLoading = false
          self.$message({
            message: self.$t('maintenance.pm_list.pm_success'),
            type: 'success',
          })
          self.actions.executePM.loading = false
          self.actions.executePM.multiExecLoading = false
          self.showExecutionConfig = false
          self.selectedPreventive = []
        })
        .catch(() => {
          self.$message({
            message: self.$t('maintenance.pm_list.pm_failure'),
            type: 'error',
          })
          self.actions.executePM.multiExecLoading = false
        })
    },
    indexMethod(index) {
      return index * 2
    },
    redirectToFormCreation() {
      if (isWebTabsEnabled()) {
        let { moduleName } = this
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        if (this.$helpers.isLicenseEnabled('MULTISITEPM')) {
          this.$router.push({
            path: '/app/wo/multiplanned/new',
          })
        } else {
          this.$router.push({
            path: '/app/wo/planned/new',
          })
        }
      }
    },
    getSitesAsString(siteObjects) {
      let sitesString = ''
      if (!isEmpty(siteObjects)) {
        siteObjects.forEach(site => {
          sitesString += site.name
          if (site.id != siteObjects[siteObjects.length - 1].id) {
            sitesString += '\n'
          }
        })
      } else {
        sitesString = '---'
      }
      return sitesString
    },
    getSitesMessage(siteObjects) {
      if (isEmpty(siteObjects)) {
        return '---'
      }
      const siteCount = siteObjects.length
      if (siteCount.length === 0) {
        return '---'
      }
      if (siteCount === 1) {
        return siteObjects[0].name
      } else if (siteCount === 2) {
        return siteObjects[0].name + ' + 1 site'
      } else {
        return siteObjects[0].name + ' + ' + (siteCount - 1) + ' sites'
      }
    },
  },
}
</script>
<style lang="scss">
.pm-list-avatar .assignment-group-name {
  width: 140px !important;
  max-width: 140px !important;
}
</style>
