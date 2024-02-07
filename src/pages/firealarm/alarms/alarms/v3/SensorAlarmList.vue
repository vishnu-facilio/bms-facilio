<template>
  <div class="height100" v-if="isSummaryOpen">
    <div class="flex flex-row">
      <div class="cm-side-bar-container">
        <SummarySidebar
          :list="records"
          :isLoading.sync="isLoading"
          :activeRecordId="selectedRecordId"
          :total="recordCount"
          :currentCount="(records || []).length"
        >
          <template #title>
            <el-row class="cm-sidebar-header">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  ></inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ currentViewDetail }}</div>
              </el-col>
            </el-row>
          </template>

          <template v-slot="{ record }">
            <router-link
              tag="div"
              class="label-txt-black link-hover-decoration"
              :to="redirectToOverview(record.id)"
            >
              <div class="inline-flex justify-content-space pT20 pB10">
                <div
                  class="summary-item-heading"
                  :title="record.subject"
                  v-tippy="tippyOptions"
                >
                  {{ getReadingFieldName(record) }}
                </div>
                <div class="flex-middle pR10">
                  <span>
                    <i
                      class="fa fa-circle prioritytag"
                      :style="{
                        color: getAlarmColor(record),
                      }"
                      aria-hidden="true"
                    ></i>
                  </span>
                  <span class=" uppercase secondary-color severityTagSummary">
                    {{ getAlarmDisplayName(record) }}
                  </span>
                </div>
              </div>
              <div class="pL20 pB20">
                <div class="fc-grey2-text12 max-width70">
                  <fc-icon
                    group="default"
                    name="assets"
                    color="#8ca1ae"
                    size="17"
                    class="pR10"
                  ></fc-icon>
                  <span v-if="record.hasOwnProperty('agent')">{{
                    $getProperty(record, 'agent.name', '---')
                  }}</span>
                  <span
                    v-else
                    :title="getResourceName(record)"
                    v-tippy="tippyOptions"
                    class="textoverflow-ellipsis inline-block width160px"
                    >{{ getResourceName(record) }}</span
                  >
                </div>
              </div>
            </router-link>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view :viewname="viewname"></router-view>
      </div>
    </div>
  </div>

  <!-- eslint-disable-next-line vue/valid-template-root -->
  <!-- eslint-disable-next-line vue/no-multiple-template-root -->
  <CommonListLayout
    v-else
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
    :key="`${moduleName}-list-layout`"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
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
        class="custom-button"
        @onSuccess="refreshRecordDetails"
        @onError="() => {}"
      ></CustomButton>
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
        <span class="separator" v-if="recordCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          :open-delay="500"
          placement="top"
          :tabindex="-1"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span v-if="hasPermission('EXPORT')" class="separator">|</span>

        <el-tooltip
          v-if="hasPermission('EXPORT')"
          effect="dark"
          :content="$t('common._common.export')"
          :open-delay="500"
          placement="top"
          :tabindex="-1"
        >
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <template v-else-if="showListView">
        <div v-if="isRecordsEmpty" class="cm-empty-state-container">
          <img
            class="mT20 self-center"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
          <div class="mT10 label-txt-black f14 self-center">
            {{ emptyStateText }}
          </div>
        </div>
        <template v-if="isRecordsNotEmpty">
          <div class="cm-list-container">
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
              class="pull-left table-header-actions"
              v-if="selectedListItemsIds.length > 0"
            >
              <div class="action-btn-slide btn-block">
                <button
                  v-if="$hasPermission('sensorrollupalarm:DELETE')"
                  class="btn btn--tertiary pointer"
                  @click="deleteRecords(selectedListItemsIds)"
                  :class="{ disabled: isLoading }"
                >
                  <i
                    class="fa fa-circle-o-notch b-icon fa-spin"
                    aria-hidden="true"
                    v-if="isLoading"
                  ></i>
                  <i class="fa fa-trash-o b-icon" v-else></i>
                  {{ $t('common._common.delete') }}
                </button>
              </div>
              <CustomButton
                :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
                :modelDataClass="modelDataClass"
                :selectedRecords="selectedListItemsObj"
                :moduleName="moduleName"
                :position="POSITION.LIST_BAR"
                class="custom-button"
                @onSuccess="refreshRecordDetails"
                @onError="() => {}"
              ></CustomButton>
            </div>
            <CommonList
              :moduleName="moduleName"
              :viewDetail="viewDetail"
              :records="records"
              :slotList="slotList"
              :redirectToOverview="redirectToOverview"
              @selection-change="selectAlarm"
              :refreshList="onCustomButtonSuccess"
            >
              <template #[slotList[0].name]="{ record }">
                <router-link
                  class="d-flex fw5 label-txt-black ellipsis main-field-column"
                  :to="redirectToOverview(record.id)"
                >
                  <div class="min-width150">
                    <span class="q-item-label pL5 f13 letter-spacing0_4">
                      {{ getSensorDisplayName(record) }}
                    </span>
                  </div>
                </router-link>
              </template>
              <template #[slotList[1].criteria]="{ record }">
                <div class="d-flex">
                  <div
                    class="self-center mL5 textoverflow-ellipsis"
                    :title="getSensorDisplayName(record)"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{ getSensorDisplayName(record) }}
                  </div>
                </div>
              </template>
              <template #[slotList[2].criteria]="{record}">
                <div>
                  <i
                    class="fa fa-circle prioritytag"
                    v-if="record.severity.id"
                    v-bind:style="{
                      color: getAlarmColor(record),
                    }"
                    aria-hidden="true"
                  ></i>
                  {{ getAlarmDisplayName(record) }}
                </div>
              </template>
              <template #[slotList[3].criteria]="{record}">
                <div class="min-width150">
                  <timer
                    class="alarm-timer"
                    :id="'timer_popover_' + record.id"
                    :time="record.lastOccurredTime"
                    :title="record.lastOccurredTime | formatDate()"
                    v-tippy="timerHoverOptions"
                  ></timer>
                  <div :id="'timer_popover_' + record.id" class="hide">
                    <div class="alarm-timer-text">
                      {{ record.lastOccurredTime | formatDate() }}
                    </div>
                  </div>
                </div>
              </template>
              <template #[slotList[4].criteria]="{ record }">
                <div class="min-width150">
                  <inline-svg src="event" class="occurrence-icon" />
                  <span class="q-item-label pL5 f13 letter-spacing0_4">
                    {{ getNoOfOccurrences(record) }}
                  </span>
                </div>
              </template>
              <template #[slotList[5].criteria]="{ record }">
                <div class="min-width150">
                  <div
                    v-if="$hasPermission('sensorrollupalarm:ACKNOWLEDGE_ALARM')"
                    class="pull-left acknowledgeColumn"
                  >
                    <el-button
                      size="mini"
                      class="uppercase ack-btn"
                      plain
                      @click="acknowledgeAlarm(record)"
                      v-if="!record.acknowledged && isActiveAlarm(record)"
                      >{{ $t('alarm.alarm.acknowledge') }}</el-button
                    >
                    <span
                      class="q-item-label"
                      v-else-if="!isActiveAlarm(record)"
                    ></span>
                    <span class="q-item-label f11" v-else>
                      <div class="self-center mL5">
                        <user-avatar
                          size="sm"
                          :user="record.acknowledgedBy"
                        ></user-avatar>
                      </div>
                      <div class="self-center mL5">
                        {{ getAcknowledgedTime(record) }}
                      </div>
                    </span>
                  </div>
                </div>
              </template>
              <template #[slotList[6].name]="{ record }">
                <CustomButton
                  :key="`${moduleName}_${record.id}_${POSITION.LIST_ITEM}`"
                  :moduleName="moduleName"
                  :position="POSITION.LIST_ITEM"
                  :record="record"
                  :ref="`custom-btn-${record.id}`"
                  @onSuccess="onCustomButtonSuccess"
                  @freezeRecord="val => (isTableFreeze = val)"
                  @moreActionButtons="
                    command => handleDropDown(command, record)
                  "
                  trigger="click"
                >
                  <el-dropdown-item
                    command="createWo"
                    v-if="
                      isActiveAlarm(record) &&
                        $hasPermission('sensorrollupalarm:CREATE_WORKORDER')
                    "
                  >
                    <div v-if="isWoCreated(record)">
                      {{ $t('common._common.view_workorder') }}
                    </div>
                    <div v-else>
                      {{ $t('common.wo_report.create_workorder') }}
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item
                    command="delete"
                    v-if="$hasPermission('sensorrollupalarm:DELETE')"
                    >{{ $t('common._common.delete') }}
                  </el-dropdown-item>
                </CustomButton>
              </template>
            </CommonList>

            <el-dialog
              :visible.sync="dialogVisible"
              width="32%"
              custom-class="dialog"
            >
              <alarm-model
                ref="confirmWoModel"
                @submit="createWO"
                @closed="closeWoDialog"
              ></alarm-model>
            </el-dialog>
          </div>
        </template>
      </template>
      <CalendarView
        v-else-if="!showListView"
        ref="calendar"
        :moduleName="moduleName"
        :record="records"
        :viewDetail="viewDetail"
        :viewname="viewname"
        :filters="filters"
      ></CalendarView>

      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></column-customization>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="{
          path: `/app/fa/sensoralarms/sensorrollupalarm/viewmanager`,
        }"
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
  </CommonListLayout>
</template>

<script>
import { API } from '@facilio/api'
import UserAvatar from '@/avatar/User'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import AlarmModel from '@/AlarmModel'
import NewAlarmMixin from '@/mixins/NewAlarmMixin'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import Timer from '@/Timer'

export default {
  extends: CommonModuleList,
  name: 'SensorAlarmList',
  props: ['viewname'],
  mixins: [NewAlarmMixin],
  components: {
    UserAvatar,
    AlarmModel,
    Timer,
  },
  data() {
    return {
      dialogVisible: false,
      createWoIds: [],
      isTableFreeze: false,
      tippyOptions: {
        placement: 'top',
        animation: 'shift-away',
        arrow: true,
      },
    }
  },
  created() {
    this.init()
  },

  computed: {
    slotList() {
      return [
        {
          name: 'readingField',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 275,
            fixed: 'left',
            label: 'SENSOR',
          },
        },
        {
          criteria: JSON.stringify({ name: 'readingFieldId' }),
        },
        {
          criteria: JSON.stringify({ name: 'severity' }),
        },
        {
          criteria: JSON.stringify({ name: 'lastOccurredTime' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfOccurrences' }),
        },
        {
          criteria: JSON.stringify({ name: 'acknowledgedBy' }),
        },
        {
          name: 'clearDeleteCreateWO',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
            align: 'right',
          },
        },
      ]
    },
    timerHoverOptions() {
      return {
        distance: 0,
        interactive: true,
        theme: 'light',
        animation: 'scale',
        arrow: true,
      }
    },
    isRecordsEmpty() {
      return isEmpty(this.records) && !this.showLoading
    },
    isRecordsNotEmpty() {
      return !isEmpty(this.records) && !this.showLoading
    },
    parentPath() {
      let { modulePath } = this
      return `/app/${modulePath}/`
    },
    modulePath() {
      return 'fa/sensoralarms'
    },
    emptyStateText() {
      return 'No Data Available'
    },
  },

  methods: {
    getUserInfo(record) {
      let { acknowledgedBy } = record || {}
      let { id } = acknowledgedBy || {}

      return this.getUserName(id)
    },
    async init() {
      this.$store.dispatch('view/loadModuleMeta', this.moduleName)
      this.$store.dispatch('loadTicketPriority')
      this.$store.dispatch('loadTicketCategory')
      this.$store.dispatch('loadTicketStatus', this.moduleName || '')
    },
    getReadingFieldName(record) {
      return this.$getProperty(record, 'subject', '---')
    },
    getResourceName(record) {
      let { resource } = record || {}
      return this.$getProperty(resource, 'name', '---')
    },
    isWoCreated(record) {
      let { lastWoId } = record || {}
      return !isEmpty(lastWoId)
    },
    async openList() {
      let { viewname, $route } = this
      let { query } = $route || {}
      await this.loadRecords()

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        this.$router.push({
          name,
          params: {
            viewname,
          },
          query,
        })
      } else {
        this.$router.push({
          name: 'sensorrollupalarm-list',
          params: { viewname },
          query,
        })
      }
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
          query: { ...this.$route.query, tab: 'summary' },
        }

        return route
      } else {
        return {
          name: 'sensorrollupalarm-summary',
          params: { moduleName, viewname, id },
          query: { ...this.$route.query, tab: 'summary' },
        }
      }
    },
    handleDropDown(command, record) {
      if (command === 'clear') {
        this.updateAlarmStatus(record)
      } else if (command === 'delete') {
        this.deleteRecords([record.id])
      } else {
        let woId = this.$getProperty(record, 'lastWoId')
        if (woId > 0) {
          this.openWorkorder(woId)
        } else {
          this.createWoDialog(record.lastOccurrenceId)
        }
      }
    },
    async acknowledgeAlarm(record) {
      let { lastOccurrenceId, id } = record || {}
      let lastOccId = record && lastOccurrenceId ? lastOccurrenceId : null
      let { $account } = this
      let { user } = $account || {}
      let dataObj = {
        alarm: record,
        occurrence: {
          id: lastOccId,
        },
        acknowledged: true,
        acknowledgedBy: user,
        acknowledgedTime: Date.now(),
        id,
      }
      let params = {
        data: dataObj,
        moduleName: this.moduleName,
        id,
      }
      let { error } = await API.post('v3/modules/data/patch', params)

      if (isEmpty(error)) {
        await this.loadRecords(true)
      }
    },
    selectAlarm(selectAlarmcheck) {
      this.selectedListItemsObj = selectAlarmcheck
      this.selectedListItemsIds = selectAlarmcheck.map(value => value.id)
    },
    unSelectRecords() {
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },
  },
}
</script>

<style lang="scss" scoped>
.occurrence-icon {
  width: 15px;
}
.alarm-timer-text {
  font-size: 6px;
  letter-spacing: 0.4px;
  color: #666666;
}
.cm-side-bar-container {
  flex: 0 0 335px;
  max-width: 335px;
  background: white;
  position: relative;
  height: 100vh;
  border-right: 1px solid #ececec;
  border-left: 1px solid #ececec;
}
.cm-sidebar-header {
  padding: 20px 15px;
  border-bottom: 1px solid #f2f2f2;
}
.summary-item-heading {
  width: 250px;
  font-weight: 500;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding-left: 20px;
  font-size: 14px;
  cursor: pointer;
}
.summary-main-div {
  display: flex;
  justify-content: space-between;
}
.location-icon {
  height: 11px;
  width: 12px;
  margin-right: 3px;
}
.cm-empty-state-container {
  background-color: #fff;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 0px 10px 10px;
}
.cm-list-container {
  border-width: 0px;
  border-style: solid;
  padding: 0px 10px 10px;
}
.column-customization-icon {
  position: absolute;
  right: 11px;
  display: block;
  width: 45px;
  height: 50px;
  cursor: pointer;
  text-align: center;
  background-color: #ffffff;
  border-left: 1px solid #f2f5f6;
  z-index: 20;
  .icon {
    top: 35%;
    right: 29%;
  }
  margin-top: 2px;
}
.severityTagSummary {
  font-size: 10px;
  letter-spacing: 0.7px;
  color: #333333;
}
.acknowledgeColumn {
  font-size: 13px;
  color: #383434;
  white-space: nowrap;
}
.acknowledgedTime {
  text-align: left;
  color: #8a8a8a;
}
.ack-btn {
  border-color: #2dc7ff;
  color: #08bdff;
  font-size: 11px;
}
</style>

<style lang="scss">
.alarm-timer {
  padding: 0;
  height: 40px;
}
.fc-timer.alarm-timer .t-label {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.1px;
  padding-top: 0;
}
.fc-timer.alarm-timer .t-sublabel {
  font-size: 7px;
  letter-spacing: 0.6px;
  color: #a5a5a5;
  padding-top: 0px;
}
.dialog {
  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 0px;
  }
}
.cm-list-container {
  .el-table td {
    padding: 10px 20px;
  }
  .el-table th.is-leaf {
    padding: 15px 20px;
  }
  .el-table th > .cell {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #333;
    white-space: nowrap;
    padding-left: 0;
    padding-right: 0;
  }
  .hover-actions {
    visibility: hidden;
  }
  .el-table__body tr.hover-row > td .hover-actions {
    visibility: visible;
  }
  .overflow-handle {
    word-break: normal;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }
}
</style>
