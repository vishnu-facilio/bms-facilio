<template>
  <div>
    <!-- Portal Content -->
    <portal to="approval-list-top" slim>
      <div class="d-flex">
        <span class="separator mR10">|</span>
        <div class="mode-switcher">
          <div
            v-for="(mode, modeName) in modes"
            :key="modeName"
            :title="mode.label"
            v-tippy="{ placement: 'bottom', arrow: true }"
            class="mR15 cursor-pointer"
            :class="{ 'is-active': mode.isActive }"
            @click="selectedMode = modeName"
          >
            <inline-svg :src="mode.icon" icon-class="icon icon-md"></inline-svg>
          </div>
        </div>
      </div>
    </portal>

    <!-- Empty State -->
    <div
      v-if="$validation.isEmpty(list)"
      class="full-layout-white height100 m10"
    >
      <div class="row container nowor">
        <div class="justify-center nowo fc-h100vh pT50">
          <div>
            <inline-svg
              src="svgs/emptystate/workorder"
              class="mB10"
              iconClass="icon text-center icon-xxxxlg height-auto"
            ></inline-svg>
            <div class="nowo-label">
              No Work Requests Found
            </div>
            <div class="nowo-sublabel" style="width: 24%;">
              There are no work orders that require approval.
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Grid View -->
    <div
      v-else-if="selectedMode === 'GRID'"
      class="fc-list-view height100vh p10 pB100"
    >
      <div class="scrollable130-y100 fc-full-border">
        <div
          v-for="workorder in list"
          :key="workorder.id"
          class="workorder-row d-flex"
        >
          <div @click="openSummary(workorder)" class="pL20 d-flex">
            <div class="text-primary mR20">
              <user-avatar
                size="md"
                :user="workorder.requestedBy"
                :showPopover="true"
                :name="false"
              ></user-avatar>
            </div>
            <div class="d-flex flex-col">
              <div class="d-flex">
                <div
                  class="ellipsis fc-list-subject max-width300px"
                  :title="workorder.subject"
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                  }"
                >
                  {{ workorder.subject }}
                </div>
                <div
                  class="fc-tag-urgent mL10"
                  v-if="workorder.urgency > 0"
                  :style="{
                    background: urgencyColorCode[workorder.urgency],
                  }"
                >
                  {{ $constants.WO_URGENCY[workorder.urgency] }}
                </div>
              </div>

              <div class="ellipsis fc-list-desc max-width500px">
                {{ workorder.description }}
              </div>

              <div
                class="width500px inline-flex flex-row align-center pT10 f13"
                style="color: #454448;"
              >
                <div class="ellipsis fc-id">#{{ workorder.serialNumber }}</div>
                <template v-if="workorder.sourceType">
                  <span class="separator">|</span>
                  <div style="color: #2ea2b2; padding-right: 5px;">
                    <i
                      v-if="workorder.sourceType === 2"
                      class="fa fa-envelope-o"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 10"
                      class="f14 fa fa-globe"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 1"
                      class="f14 el-icon-service approval-el-icon-service"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 4"
                      class="f14 el-icon-bell approval-el-icon-bell"
                    ></i>
                    <i
                      v-else-if="workorder.sourceType === 5"
                      class="f14 el-icon-date approval-el-icon-date"
                    ></i>
                  </div>
                  <div
                    v-if="workorder.requester"
                    class="pT0"
                    style="padding-right: 8px;color: #8ca1ad;"
                  >
                    {{
                      !$validation.isEmpty(workorder.requester.id)
                        ? workorder.requester.name
                        : '---'
                    }}
                  </div>
                </template>
                <div
                  v-if="
                    workorder.resource &&
                      !$validation.isEmpty(workorder.resource.id)
                  "
                  class="fc-grey2-text12"
                >
                  <span class="separator">|</span>
                  <img
                    v-if="workorder.resource.resourceType === 1"
                    src="~statics/space/space-resource.svg"
                    style="height:12px; width:14px;"
                    class="mR5"
                  />
                  <img
                    v-else
                    src="~statics/space/asset-resource.svg"
                    style="height:12px; width:14px;"
                    class="mR5"
                  />
                  {{ workorder.resource.name }}
                </div>
              </div>
            </div>
          </div>
          <div class="d-flex mL-auto width200px mR100">
            <div
              v-if="!$validation.isEmpty(getApprovalStates(workorder))"
              class="approval-list-action-container "
              @click.stop="() => {}"
            >
              <ApprovalButtons
                size="small"
                moduleName="workorder"
                :record="workorder"
                :availableTransitions="getApprovalStates(workorder)"
                :transformFn="transformFormData"
                :updateUrl="updateUrl"
                @onSuccess="onTransitionSuccess"
                @onFailure="onTransitionError"
                class="approval-list-actions"
              ></ApprovalButtons>
            </div>
            <div class="pT0">
              <span class="mT10 show">
                <i
                  v-if="workorder.priority"
                  class="fa fa-circle prioritytag mR5"
                  v-bind:style="{
                    color: getTicketPriority(workorder.priority.id).colour,
                  }"
                  aria-hidden="true"
                ></i>
                {{
                  workorder.priority && workorder.priority.id > 0
                    ? getTicketPriority(workorder.priority.id).displayName
                    : '---'
                }}
              </span>
              <div class="creted-time mT10">
                <i
                  class="fa fa-clock-o fc-due-date-clock"
                  style="padding-right: 5px;font-size: 15px"
                ></i>
                <span style="font-size: 13px">
                  Created {{ workorder.createdTime | fromNow }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- List View -->
    <div
      v-else
      class="fc-list-view fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser p10 pB100"
    >
      <div
        class="view-column-chooser"
        style="top: 10px;"
        @click="showColumnSettings = true"
      >
        <img
          src="~assets/column-setting.svg"
          style="text-align: center; position: absolute; top: 35%;right: 29%;"
        />
      </div>

      <el-table
        :data="list"
        ref="approvalList"
        class="width100"
        height="auto"
        :fit="true"
        @row-click="goToSummary"
      >
        <el-table-column fixed prop label="ID" min-width="90">
          <template v-slot="data">
            <div class="fc-id">{{ '#' + data.row.serialNumber }}</div>
          </template>
        </el-table-column>

        <el-table-column fixed prop label="SUBJECT" width="300">
          <template slot-scope="data">
            <div
              v-tippy
              small
              :title="$getProperty(data, 'row.subject')"
              class="flex-middle"
            >
              <div class="fw5 ellipsis textoverflow-ellipsis width200px">
                {{ $getProperty(data, 'row.subject') }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          v-for="(field, index) in viewColumns.filter(
            field => !isFixedColumn(field.name) || field.parentField
          )"
          :key="index"
          :prop="field.name"
          :label="field.displayName"
          :align="
            $getProperty(field, 'field.dataTypeEnum') === 'DECIMAL'
              ? 'right'
              : 'left'
          "
          min-width="200"
        >
          <template v-slot="data">
            <div v-if="field.name === 'siteId'">
              <span>
                {{ data.row.siteId ? getSiteName(data.row.siteId) : '---' }}
              </span>
            </div>

            <template v-else-if="field.name === 'urgency'">
              <div
                v-if="getUrgency(data.row.urgency)"
                :style="{
                  color: urgencyColorCode[data.row.urgency],
                }"
              >
                {{ getUrgency(data.row.urgency) }}
              </div>
              <div v-else>---</div>
            </template>

            <div
              v-else-if="field.name === 'resource'"
              class="relative-position"
              style="min-width: 170px;"
            >
              <div v-if="data.row.resource && data.row.resource.id !== -1">
                <div class="flLeft mR7">
                  <img
                    v-if="data.row.resource.resourceType === 1"
                    src="~statics/space/space-resource.svg"
                    style="height:12px; width:14px;"
                  />
                  <img
                    v-else
                    src="~statics/space/asset-resource.svg"
                    style="height:11px; width:14px;"
                  />
                </div>
                <span
                  class="flLeft ellipsis max-width140px"
                  v-tippy
                  small
                  data-position="bottom"
                  :title="data.row.resource.name"
                >
                  {{ data.row.resource.name }}
                </span>
              </div>
              <div v-else>
                <span class="secondary-color color-d">
                  --- {{ $t('maintenance.wr_list.space_asset') }} ---
                </span>
              </div>
            </div>

            <div v-else>
              <div
                class="table-subheading"
                :class="{
                  'text-right':
                    $getProperty(field, 'field.dataTypeEnum') === 'DECIMAL',
                }"
              >
                {{ getColumnDisplayValue(field, data.row) || '---' }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          fixed="right"
          prop
          label
          column-key="actions"
          width="250"
        >
          <template v-slot="data">
            <div class="approval-list-action-container" @click.stop="() => {}">
              <ApprovalButtons
                size="small"
                moduleName="workorder"
                :record="data.row"
                :availableTransitions="getApprovalStates(data.row)"
                :transformFn="transformFormData"
                :updateUrl="updateUrl"
                @onSuccess="onTransitionSuccess"
                @onFailure="onTransitionError"
                class="approval-list-actions"
              ></ApprovalButtons>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <column-customization
        :visible.sync="showColumnSettings"
        moduleName="workorder"
        :columnConfig="columnConfig"
        :viewName="currentView"
      ></column-customization>
    </div>
  </div>
</template>
<script>
import ApprovalButtons from '@/approval/ApprovalButtons'
import ViewMixinHelper from '@/mixins/ViewMixin'
import transformMixin from 'pages/workorder/workorders/v1/mixins/workorderTransform'
import ColumnCustomization from '@/ColumnCustomization'
import { mapState, mapGetters } from 'vuex'
import UserAvatar from '@/avatar/User'
import { API } from '@facilio/api'

export default {
  props: [
    'list',
    'currentView',
    'refreshAction',
    'setSortConfig',
    'setTransitionConfig',
    'getApprovalStates',
    'openSummary',
  ],
  mixins: [ViewMixinHelper, transformMixin],
  components: { ApprovalButtons, ColumnCustomization, UserAvatar },
  created() {
    this.init()
  },
  data() {
    return {
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['serialNumber', 'subject'],
        availableColumns: [],
      },
      urgencyColorCode: {
        1: '#7fa5ff',
        2: '#f56837',
        3: '#e65244',
      },
      selectedMode: localStorage.getItem('fc-approval-view') || 'LIST',
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      sites: state => state.sites,
    }),
    ...mapGetters(['getTicketPriority']),
    modes() {
      let { selectedMode } = this
      return {
        GRID: {
          icon: 'svgs/grid-view',
          label: 'Grid View',
          isActive: selectedMode === 'GRID',
        },
        LIST: {
          icon: 'svgs/list-view',
          label: 'List View',
          isActive: selectedMode === 'LIST',
        },
      }
    },
    updateUrl() {
      return `/v2/workorders/update`
    },
    tableRef() {
      return this.$refs['approvalList']
    },
  },
  watch: {
    list: 'init',
    selectedMode(value) {
      localStorage.setItem('fc-approval-view', value)
    },
  },
  methods: {
    init() {
      this.updateSortConfig()
      this.setTransitionConfig(this.updateUrl, this.transformFormData)
    },
    async updateSortConfig() {
      let excludedSortFields = [
        'assignedTo',
        'assignedBy',
        'assignmentGroup',
        'createdBy',
        'requestedBy',
        'requester',
      ]

      let sortConfig = {
        orderBy: {
          label: this.$t('maintenance.wr_list.datecreated'),
          value: 'createdTime',
        },
        orderType: 'desc',
      }

      let sortConfigList = []

      let { data, error } = await API.get('/module/metafields', {
        moduleName: 'workorder',
      })

      if (!error) {
        let { meta = {} } = data || {}
        sortConfigList = (meta.fields || {}).map(({ name }) => name)
      }

      this.setSortConfig(sortConfig, sortConfigList, excludedSortFields)
    },
    onTransitionSuccess() {
      this.refreshAction()
    },
    onTransitionError() {
      this.$message.error('Could not update Work Order')
    },
    goToSummary(row, col) {
      if (['selection', 'actions'].includes(col.columnKey)) {
        return
      }
      this.openSummary(row)
    },
    getSiteName(siteId) {
      let site = (this.sites || []).find(({ id }) => id === siteId)
      return site ? site.name : '---'
    },
    getUrgency(value) {
      let urgency = this.$constants.WO_URGENCY[value]
      return urgency
    },
  },
}
</script>
<style lang="scss" scoped>
.mode-switcher {
  display: flex;
  .is-active {
    color: #206bba;
  }
}
// Temp CSS for grid view.. should remove once QFM gets used to new view
.workorder-row {
  cursor: pointer;
  background: #fff;
  padding: 5px;
  border-left: 3px solid transparent;
  border-right: 1px solid transparent;
  border-top: 1px solid transparent;
  border-bottom: 1px solid #f2f5f6;
  align-items: center;
  min-height: 90px;
  position: relative;

  .prioritytag {
    position: static;
  }
  .approval-list-action-container {
    position: absolute;
    right: 0;
    margin-right: 100px;
  }
  &:hover {
    background: #fafbfc;
    border-left: 3px solid #39b2c2;

    .approval-list-actions {
      padding: 15px;
      display: flex;
      background: #fafbfc;
    }
  }
}
</style>
