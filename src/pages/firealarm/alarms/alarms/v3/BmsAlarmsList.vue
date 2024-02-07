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
          class="purchaseorder-summary-list"
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
              class=" label-txt-black link-hover-decoration"
              :to="redirectToOverview(record.id)"
            >
              <div class="inline-flex justify-content-space pT20 pB10">
                <div
                  class="summary-item-heading"
                  :title="record[mainFieldName] || '---'"
                  v-tippy="tippyOptions"
                >
                  {{ record[mainFieldName] || '---' }}
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
        v-if="!canHideFilter"
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
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <template v-else-if="showListView">
        <div
          v-if="$validation.isEmpty(records) && !showLoading"
          class="cm-empty-state-container"
        >
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
        <template v-if="!showLoading && !$validation.isEmpty(records)">
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
                  v-if="$hasPermission('bmsalarm:DELETE')"
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
              :viewDetail="viewDetail"
              :records="records"
              :moduleName="moduleName"
              :redirectToOverview="redirectToOverview"
              :slotList="slotList"
              :refreshList="onCustomButtonSuccess"
              :isTableFreeze="isTableFreeze"
              @selection-change="selectAlarm"
            >
              <template #[slotList[0].name]="{ record }">
                <div
                  class="self-center f10 secondary-color"
                  style="min-width: 90px; margin-left: 13%"
                >
                  <div
                    class="q-item-label uppercase severityTag"
                    :style="{
                      'background-color': getAlarmColor(record),
                    }"
                  >
                    {{ getAlarmDisplayName(record) }}
                  </div>
                </div>
              </template>
              <template #[slotList[1].criteria]="{ record }">
                <div
                  class="q-item-sublabel ellipsis"
                  style="margin-top: 5px; font-size: 12px"
                >
                  <span class="fc-id">#{{ record.id }}</span>
                </div>
                <div>
                  <router-link
                    class="d-flex fw5 label-txt-black ellipsis main-field-column"
                    :to="redirectToOverview(record.id)"
                  >
                    <el-tooltip
                      effect="dark"
                      :content="record.subject || '---'"
                      placement="top-end"
                      :open-delay="600"
                    >
                      <div class="self-center width200px">
                        <span class="list-main-field">{{
                          record.subject || '---'
                        }}</span>
                      </div>
                    </el-tooltip>
                  </router-link>
                </div>
                <div
                  class="ellipsis"
                  style="font-size: 12px; color: #8a8a8a; margin-top: 5px"
                >
                  <span style>{{ record.lastOccurredTime | fromNow }}</span>
                </div>
              </template>
              <template #[slotList[2].criteria]="{ record }">
                <router-link
                  class="d-flex fw5 label-txt-black ellipsis main-field-column"
                  :to="redirectToOverview(record.id)"
                >
                  <div style="min-width: 150px">
                    <div class="flLeft">
                      <inline-svg
                        src="event"
                        style="width: 15px"
                        class="flLeft"
                      />
                    </div>
                    <span
                      class="q-item-label pL5"
                      style="font-size: 13px; letter-spacing: 0.4px"
                    >
                      {{ getNoOfOccurrences(record) }}
                    </span>
                  </div>
                </router-link>
              </template>
              <template #[slotList[3].criteria]="{ record }">
                <div style="min-width: 150px">
                  <div
                    v-if="$hasPermission('bmsalarm:ACKNOWLEDGE_ALARM')"
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
              <template #[slotList[4].name]="{ record }">
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
                  class="custom-button visibility-hide-actions"
                >
                  <el-dropdown-item
                    command="createWo"
                    v-if="
                      isActiveAlarm(record) &&
                        $hasPermission('bmsalarm:CREATE_WORKORDER')
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
                    v-if="$hasPermission('bmsalarm:DELETE')"
                    >{{ $t('common._common.delete') }}
                  </el-dropdown-item>
                </CustomButton>
              </template>
            </CommonList>
          </div>
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
          path: `/app/fa/bmsalarms/bmsalarm/viewmanager`,
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
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import UserAvatar from '@/avatar/User'
import { API } from '@facilio/api'
import AlarmModel from '@/AlarmModel'
import NewAlarmMixin from '@/mixins/NewAlarmMixin'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'

export default {
  extends: CommonModuleList,
  name: 'BmsAlarmsList',
  mixins: [NewAlarmMixin],
  components: {
    UserAvatar,
    AlarmModel,
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
  computed: {
    parentPath() {
      let { modulePath } = this
      return `/app/${modulePath}/bmsalarms/`
    },
    modulePath() {
      return 'fa'
    },
    mainFieldName() {
      return 'subject'
    },
    slotList() {
      return [
        {
          name: 'severity',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 150,
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'subject' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfOccurrences' }),
        },
        {
          criteria: JSON.stringify({ name: 'acknowledgedBy' }),
        },
        {
          name: 'DeleteCreateWO',
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
    emptyStateText() {
      return 'No Data Available'
    },
  },
  methods: {
    async init() {
      this.$store.dispatch('loadTicketStatus', this.moduleName || '')
      this.$store.dispatch('loadTicketPriority')
      this.$store.dispatch('loadTicketCategory')
    },
    isWoCreated(record) {
      let { lastWoId } = record || {}
      return !isEmpty(lastWoId)
    },
    getResourceName(record) {
      let { resource } = record || {}
      return this.$getProperty(resource, 'name', '---')
    },
    selectAlarm(selectAlarmcheck) {
      this.selectedListItemsObj = selectAlarmcheck
      this.selectedListItemsIds = selectAlarmcheck.map(value => value.id)
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

        return name && route
      } else {
        return {
          name: 'bmsalarm-summary',
          params: { moduleName, viewname, id },
          query: { ...this.$route.query, tab: 'summary' },
        }
      }
    },
    handleDropDown(command, record) {
      if (command === 'delete') {
        this.deleteRecords([record.id])
      } else {
        let woId = this.$getProperty(record, 'lastWoId')
        woId > 0
          ? this.openWorkorder(woId)
          : this.createWoDialog(record.lastOccurrenceId)
      }
    },
    async acknowledgeAlarm(record) {
      let { lastOccurrenceId } = record

      let dataObj = {
        alarm: record,
        occurrence: {
          id: lastOccurrenceId || null,
        },
        acknowledged: true,
        acknowledgedBy: this.$account.user,
        acknowledgedTime: Date.now(),
        id: record.id,
      }
      let params = {
        data: dataObj,
        moduleName: this.moduleName,
        id: dataObj.id,
      }
      let { error } = await API.post('v3/modules/data/patch', params)

      if (isEmpty(error)) {
        await this.loadRecords(true)
      }
    },
    async openList() {
      let { moduleName, viewname, $route } = this
      let { query } = $route || {}
      await this.loadRecords(true)
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query,
          })
      } else {
        this.$router.push({
          name: 'bmsalarm-list',
          params: { moduleName, viewname },
          query,
        })
      }
    },
    unSelectRecords() {
      this.selectedListItemsIds = []
      this.selectedListItemsObj = []
    },
  },
}
</script>

<style lang="scss" scoped>
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
.cm-sidebar-list-item {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 20px;
  font-size: 12px;
  cursor: pointer;
}
.cm-empty-state-container {
  background-color: #fff;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 10px;
  margin-top: 0px;
}
.cm-list-container {
  border-width: 0px !important;
  border-style: solid;
  padding: 0px 10px 10px;
  height: calc(100vh - 155px) !important;
}
.img-container {
  width: 37px;
  height: 37px;
  border: 1px solid #f9f9f9;
  border-radius: 50%;
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
</style>

<style lang="scss">
.link-hover-decoration {
  &:hover {
    cursor: pointer;
    color: #46a2bf;
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
}
</style>

<style scoped>
.severityTag {
  border-radius: 38px;
  font-size: 10px !important;
  font-weight: bold !important;
  letter-spacing: 0.8px;
  text-align: center;
  color: #ffffff !important;
  padding: 4px 10px;
  width: 73px;
  margin-bottom: 6px;
  line-height: 15px;
  white-space: nowrap;
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
.location-icon {
  height: 11px;
  width: 12px;
  margin-right: 3px;
}
</style>
