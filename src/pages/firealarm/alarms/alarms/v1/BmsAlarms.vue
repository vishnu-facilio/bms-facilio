<template>
  <div class="height100 alarm-list-page">
    <CommonListLayout
      v-if="openAlarmId === -1"
      :moduleName="currentModuleName"
      :showViewRearrange="true"
      :showViewEdit="true"
      :visibleViewCount="3"
      :getPageTitle="() => currentModuleDisplayName"
      :hideSubHeader="isEmpty(bmsAlarms)"
      :pathPrefix="`${parentPath}/bmsalarms/`"
      :recordCount="listCount"
      :recordLoading="showLoading"
    >
      <template #header>
        <AdvancedSearchWrapper
          :key="`${moduleName}-search`"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        ></AdvancedSearchWrapper>
      </template>

      <template #sub-header-actions>
        <template v-if="!isEmpty(listCount)">
          <pagination
            :total="listCount"
            :perPage="50"
            :skipTotalCount="true"
            class="pL15 fc-black-small-txt-12"
          ></pagination>
          <span class="separator">|</span>

          <el-tooltip
            effect="dark"
            :content="$t('common._common.sort')"
            placement="right"
          >
            <sort
              :key="currentModuleName + '-sort'"
              :config="sortConfig"
              :sortList="sortConfigLists"
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
              :module="currentModuleName"
              :viewDetail="viewDetail"
              :showViewScheduler="true"
              :showMail="false"
              :filters="filters"
            ></f-export-settings>
          </el-tooltip>
        </template>
      </template>

      <!-- List Content -->
      <div class="scrollable">
        <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>

        <div
          class="pL10 pR10 height100 alarm-list-page scrollable "
          style="padding-bottom: 70px"
        >
          <div
            class="row full-layout-white fc-border-1 scrollbar-style fc-bms-alarm-table"
            style="padding-bottom: 20px; overflow-x: scroll; display: block"
          >
            <table
              class="fc-list-view-table fc-list-view-table2 fc-alarm-summary-table"
            >
              <thead>
                <th class="text-left" v-if="canShowColumn('severity')"></th>
                <th class="text-left uppercase" style="width: 350px">
                  {{ $t('alarm.alarm.message') }}
                </th>
                <th
                  class="uppercase"
                  v-for="(field, index) in viewColumns"
                  v-if="!isFixedColumn(field.name)"
                  :key="index"
                >
                  <div
                    :class="{ 'text-center': field.name === 'noOfOccurrences' }"
                    v-if="field.name === 'noOfOccurrences'"
                    class="p10"
                  ></div>
                  <div
                    :class="{ 'text-center': field.name === 'noOfOccurrences' }"
                    v-else
                  >
                    {{ field.displayName }}
                  </div>
                </th>
                <th v-if="canShowColumn('noOfOccurrences')"></th>
                <th
                  v-if="canShowColumn('acknowledgedBy')"
                  class="text-left uppercase"
                ></th>
                <th></th>
              </thead>
              <tbody v-if="loading">
                <tr>
                  <td colspan="100%" class="text-center">
                    <spinner :show="loading" size="80"></spinner>
                  </td>
                </tr>
              </tbody>
              <tbody v-else>
                <tr class="nowotd" v-if="!bmsAlarms.length">
                  <td colspan="100%" style="border-bottom: none !important;">
                    <div
                      class="flex-middle justify-content-center flex-direction-column"
                    >
                      <inline-svg
                        src="svgs/emptystate/alarmEmpty"
                        iconClass="icon text-center icon-xxxxlg"
                      ></inline-svg>
                      <div class="nowo-label">
                        {{ $t('alarm.alarm.no_alarms_found') }}
                      </div>
                    </div>
                  </td>
                </tr>
                <tr
                  v-else
                  class="tablerow"
                  v-for="(alarm, index) in bmsAlarms"
                  :key="index"
                >
                  <td v-if="canShowColumn('severity')">
                    <div
                      class="q-item-label self-center f10 secondary-color"
                      v-if="alarm.severity"
                      style="min-width: 90px; margin-left: 13%"
                    >
                      <div
                        class="q-item-label uppercase severityTag"
                        v-bind:style="{
                          'background-color': getAlarmSeverity(
                            alarm.severity.id
                          ).color,
                        }"
                      >
                        {{
                          alarm.severity.id
                            ? getAlarmSeverity(alarm.severity.id).displayName
                            : '---'
                        }}
                      </div>
                    </div>
                  </td>
                  <td @click="opensummary(alarm.id)">
                    <div
                      class="q-item-main q-item-section"
                      style="min-width: 230px"
                    >
                      <div
                        class="q-item-sublabel ellipsis"
                        style="margin-top: 5px; font-size: 12px"
                      >
                        <span class="fc-id">#{{ alarm.id }}</span>
                      </div>
                      <div
                        class="q-item-label fw5"
                        style="
                          margin-top: 1px;
                          font-size: 15px;
                          letter-spacing: 0.3px;
                        "
                      >
                        {{ alarm.subject }}
                      </div>
                      <div
                        class="ellipsis"
                        style="font-size: 12px; color: #8a8a8a; margin-top: 5px"
                      >
                        <span style>{{
                          alarm.lastOccurredTime | fromNow
                        }}</span>
                      </div>
                    </div>
                  </td>
                  <td
                    v-if="!isFixedColumn(field.name)"
                    v-for="(field, index) in viewColumns"
                    :key="index"
                  >
                    <div v-if="field.name === 'alarmType'">
                      <span
                        class="q-item-label"
                        style="font-size: 13px; letter-spacing: 0.4px"
                        >{{
                          alarm.alarmTypeVal ? alarm.alarmTypeVal : '---'
                        }}</span
                      >
                    </div>
                    <div v-else-if="field.name === 'noOfOccurrences'">
                      <div class="flLeft">
                        <img
                          src="~statics/icons/event.svg"
                          style="width: 15px"
                          class="flLeft"
                        />
                      </div>
                      <span
                        class="q-item-label pL5"
                        style="font-size: 13px; letter-spacing: 0.4px"
                      >
                        {{
                          alarm.noOfOccurrences && alarm.noOfOccurrences > -1
                            ? alarm.noOfOccurrences
                            : '0'
                        }}
                      </span>
                      <div style="clear: both"></div>
                    </div>
                    <div v-else-if="field.name === 'readingAlarmCategory'">
                      <span
                        class="q-item-label"
                        style="font-size: 13px; letter-spacing: 0.4px"
                      >
                        {{
                          alarm.readingAlarmCategory &&
                          alarm.readingAlarmCategory.id > -1
                            ? getReadingAlarmCategory(
                                alarm.readingAlarmCategory.id
                              ).name
                            : '---'
                        }}
                      </span>
                    </div>

                    <div
                      v-else-if="field.name === 'lastOccurredTime'"
                      style="min-width: 150px"
                    >
                      <div :id="'timer_popover_' + alarm.id" class="">
                        <div
                          style="
                            font-size: 12px;
                            letter-spacing: 0.5px;
                            color: #666666;
                          "
                          v-if="alarm.lastOccurredTime > -1"
                        >
                          {{ alarm.lastOccurredTime | formatDate() }}
                        </div>
                        <div
                          style="
                            font-size: 12px;
                            letter-spacing: 0.5px;
                            color: #666666;
                          "
                          v-else
                        >
                          ---
                        </div>
                      </div>
                    </div>

                    <div
                      v-else-if="field.name === 'lastCreatedTime'"
                      @click="opensummary(alarm.id)"
                      style="min-width: 150px"
                    >
                      <div :id="'timer_popover_' + alarm.id" class="">
                        <div
                          style="
                            font-size: 12px;
                            letter-spacing: 0.5px;
                            color: #666666;
                          "
                          v-if="alarm.lastCreatedTime > -1"
                        >
                          {{ alarm.lastCreatedTime | formatDate() }}
                        </div>
                        <div
                          style="
                            font-size: 12px;
                            letter-spacing: 0.5px;
                            color: #666666;
                          "
                          v-else
                        >
                          ---
                        </div>
                      </div>
                    </div>
                    <div
                      v-else
                      style="
                        width: 200px;
                        max-width: 200px;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                        overflow: hidden;
                      "
                    >
                      <span
                        class="q-item-label"
                        style="font-size: 13px; letter-spacing: 0.4px"
                        >{{ getColumnDisplayValue(field, alarm) }}</span
                      >
                      <!-- <div
                        v-if="
                          field.name === 'resource' &&
                          alarm.resource &&
                          alarm.resource.space &&
                          alarm.resource.space.id > 0
                        "
                        style="padding-top: 5px"
                      >
                        <img
                          src="~statics/space/space-resource.svg"
                          style="height: 11px; width: 12px; margin-right: 3px"
                          class="flLeft"
                        />
                        <span
                          class="flLeft q-item-label ellipsis"
                          v-tippy
                          small
                          data-position="bottom"
                          :title="alarm.resource.space.name"
                          style="max-width: 85%; font-size: 10px"
                          >{{ alarm.resource.space.name }}</span
                        >
                      </div> -->
                    </div>
                  </td>
                  <td v-if="canShowColumn('noOfOccurrences')">
                    <div style="width: 55px" v-if="alarm.noOfOccurrences > 0">
                      <div class="flLeft">
                        <img
                          src="~statics/icons/event.svg"
                          style="width: 15px"
                          class="flLeft"
                        />
                      </div>
                      <span class="flLeft pL5">{{
                        alarm.noOfOccurrences
                      }}</span>
                      <div style="clear: both"></div>
                    </div>
                  </td>
                  <td v-if="canShowColumn('acknowledgedBy')">
                    <div
                      v-if="$hasPermission('alarm:ACKNOWLEDGE_ALARM')"
                      class="pull-left"
                      style="
                        font-size: 13px;
                        color: #383434;
                        white-space: nowrap;
                      "
                    >
                      <q-btn
                        color="secondary"
                        class="uppercase fia-alert-btn"
                        small
                        outline
                        @click="
                          acknowledgeAlarm({
                            alarm: alarm,
                            occurrence: {
                              id:
                                alarm && alarm.lastOccurrenceId
                                  ? alarm.lastOccurrenceId
                                  : null,
                            },
                            acknowledged: true,
                            acknowledgedBy: $account.user,
                          })
                        "
                        v-if="!alarm.acknowledged && isActiveAlarm(alarm)"
                        >{{ $t('alarm.alarm.acknowledge') }}</q-btn
                      >
                      <span
                        class="q-item-label"
                        v-else-if="!isActiveAlarm(alarm)"
                      ></span>
                      <span class="q-item-label f11" v-else>
                        <div
                          :id="'contentpopup2_' + alarm.id"
                          class="hide ackhover"
                        >
                          <div>
                            <div class="ackhover-row">
                              <div class="hover-row">
                                {{ $t('alarm.alarm.acknowledge_by') }}
                              </div>
                              <div class="hover-row">
                                <span>
                                  <user-avatar
                                    size="sm"
                                    :user="getUserName(alarm.acknowledgedBy.id)"
                                  ></user-avatar>
                                </span>
                              </div>
                              <div class="hover-row">
                                {{
                                  alarm.acknowledgedTime > 0
                                    ? alarm.acknowledgedTime
                                    : new Date() | fromNow
                                }}
                              </div>
                            </div>
                          </div>
                        </div>
                        <span
                          class="f11 pB10"
                          style="
                            font-size: 14px;
                            letter-spacing: 0.3px;
                            color: #333333;
                          "
                          v-tippy="{
                            html: 'contentpopup2_' + alarm.id,
                            interactive: true,
                            reactive: true,
                            distance: 15,
                            theme: 'light',
                            animation: 'scale',
                          }"
                          >{{ $t('alarm.alarm.acknowledged') }}</span
                        >
                        <div
                          style="
                            font-size: 12px;
                            letter-spacing: 0.4px;
                            text-align: left;
                            color: #8a8a8a;
                            padding-top: 5px;
                          "
                        >
                          {{
                            alarm.acknowledgedTime > 0
                              ? alarm.acknowledgedTime
                              : new Date() | fromNow
                          }}
                        </div>
                      </span>
                    </div>
                  </td>
                  <td class="self-center secondary-color">
                    <q-icon
                      slot="right"
                      name="more_vert"
                      style="float: right; font-size: 20px; color: #d8d8d8"
                    >
                      <q-popover ref="moreactionspopover">
                        <q-list link class="no-border">
                          <!--
                        Hiding clear option for sensor alarms
                        <q-item v-if="isActiveAlarm(alarm)">
                        <q-item-main
                          :label="$t('alarm.alarm.clear')"
                          @click="updateAlarmsStatus(alarm)"
                        />
                      </q-item> -->
                          <q-item v-if="alarm.lastWoId > 0">
                            <q-item-main
                              :label="$t('alarm.alarm.view_workorder')"
                              @click="
                                viewAlarm(alarm.lastWoId),
                                  $refs.moreactionspopover[index].close()
                              "
                            />
                          </q-item>
                          <q-item
                            v-else-if="
                              $hasPermission('alarm:CREATE_WO') &&
                                alarm &&
                                alarm.lastOccurrenceId &&
                                isActiveAlarm(alarm)
                            "
                          >
                            <q-item-main
                              :label="$t('alarm.alarm.create_workorder')"
                              @click="
                                createWoDialog(alarm.lastOccurrenceId),
                                  $refs.moreactionspopover[index].close()
                              "
                            />
                          </q-item>
                          <q-item>
                            <q-item-main
                              :label="$t('common._common.delete')"
                              @click="
                                deleteAlarmConf([alarm.id]),
                                  $refs.moreactionspopover[index].close()
                              "
                            />
                          </q-item>
                        </q-list>
                      </q-popover>
                    </q-icon>
                  </td>
                </tr>
                <tr v-if="fetchingMore">
                  <td colspan="100%" class="text-center">
                    <spinner :show="fetchingMore" size="50"></spinner>
                  </td>
                </tr>
              </tbody>
            </table>
            <span
              class="view-column-chooser fc-alarm-list-view"
              @click="showColumnSettings = true"
            >
              <img
                src="~assets/column-setting.svg"
                style="
                  text-align: center;
                  position: absolute;
                  top: 36%;
                  right: 25%;
                "
              />
            </span>
          </div>
        </div>

        <q-modal
          ref="createWOModel"
          noBackdropDismiss
          content-classes="fc-model"
          :content-css="{
            padding: '0px',
            background: '#f7f8fa',
            Width: '10vw',
            Height: '30vh',
          }"
        >
          <alarm-model
            ref="confirmWoModel"
            @submit="createWO"
            @closed="closeWoDialog"
          ></alarm-model>
        </q-modal>

        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="currentModuleName"
          :columnConfig="columnConfig"
          :viewName="currentView"
        ></column-customization>

        <portal to="view-manager-link">
          <router-link
            tag="div"
            :to="`/app/fa/bmsalarms/bmsalarm/viewmanager`"
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
      </div>
    </CommonListLayout>
    <div class="fc-column-view height100 border-border-none" v-else>
      <div class="row fc-column-view-title height100">
        <div
          class="col-4 fc-column-view-left height100"
          style="max-width: 26.3333%; flex: 0 0 26.3333%"
        >
          <div class="row container col-header pL20">
            <div class="col-12">
              <div class="pull-left">
                <i class="el-icon-back fw6 pointer pR10" @click="back"></i>
                <el-dropdown
                  @command="openChild"
                  class="alarm-dp"
                  trigger="click"
                  placement="top-end"
                >
                  <span class="el-dropdown-link pointer">
                    {{ currentViewDetail.displayName }}
                    <i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown" class="alarm-dropdownmenu">
                    <div
                      v-for="(views, idx) in $store.state.view.groupViews"
                      :key="idx"
                      v-if="views.name !== 'systemviews'"
                    >
                      <div class="alarm-view-name-dp pT10">
                        {{ views.displayName }}
                      </div>
                      <el-dropdown-item
                        v-for="(view, idx) in views.views"
                        :key="idx"
                        :command="view.name"
                        v-if="view.name !== currentViewDetail.name"
                        >{{ view.displayName }}</el-dropdown-item
                      >
                    </div>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </div>
          <v-infinite-scroll
            :loading="loading"
            @bottom="nextPage"
            :offset="20"
            style="height: 100vh; padding-bottom: 150px; overflow-y: scroll"
          >
            <table
              class="fc-list-view-table"
              style="
                height: calc(100vh - 100px);
                overflow-y: scroll;
                display: block;
                padding-bottom: 60px;
              "
            >
              <tbody v-if="bmsAlarms.length">
                <template v-for="(alarm, index) in bmsAlarms">
                  <tr
                    @click="opensummary(alarm.id)"
                    class="tablerow"
                    :key="index"
                    v-bind:class="{ active: openAlarmId === alarm.id }"
                  >
                    <td class="text-left width100 pL5">
                      <div class="q-item-main q-item-section pL10">
                        <div
                          class="fw5 workRequest-heading f14 textoverflow-height-ellipsis"
                          v-tippy
                          :title="alarm.Subject"
                        >
                          {{ alarm.subject }}
                        </div>
                        <div class="flex-middle pT5">
                          <div class="uppercase fc-id">#{{ alarm.id }}</div>
                          <div class="separator">|</div>
                          <div class="fc-grey2-text12">
                            <img
                              src="~statics/space/space-resource.svg"
                              style="
                                height: 11px;
                                width: 12px;
                                margin-right: 3px;
                              "
                              class="flLeft"
                            />
                            <span v-if="alarm.hasOwnProperty('agent')">{{
                              alarm.agent ? alarm.agent.name : '--'
                            }}</span>
                            <span v-else>{{
                              alarm.resource ? alarm.resource.name : '--'
                            }}</span>
                          </div>
                        </div>
                      </div>
                    </td>
                    <td class="text-left clearboth" v-if="alarm.severity">
                      <span class="ellipsis pT5">
                        <span class="q-item-label">
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:style="{
                              color: getAlarmSeverity(alarm.severity.id).color,
                            }"
                            aria-hidden="true"
                          ></i>
                        </span>
                        <span
                          class="q-item-label uppercase secondary-color"
                          style="
                            font-size: 10px;
                            letter-spacing: 0.7px;
                            color: #333333;
                          "
                        >
                          {{ getAlarmSeverity(alarm.severity.id).displayName }}
                        </span>
                      </span>
                    </td>
                  </tr>
                </template>
              </tbody>
            </table>
          </v-infinite-scroll>
        </div>

        <div
          class="col-8 fc-column-alarm-right new-alarm-summ-bg"
          style="max-width: 73.6667%; flex: 0 0 73.6667%"
        >
          <div slot="right">
            <router-view name="summary"></router-view>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import ColumnCustomization from '@/ColumnCustomization'
import VInfiniteScroll from 'v-infinite-scroll'
import UserAvatar from '@/avatar/User'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { isEmpty } from '@facilio/utils/validation'
import CommonListLayout from 'newapp/list/CommonLayout'
import Pagination from 'src/newapp/components/ListPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import { mapState, mapGetters, mapActions } from 'vuex'
import { mapStateWithLogging } from 'store/utils/log-map-state'
import { QList, QItem, QItemMain, QIcon, QPopover, QModal, QBtn } from 'quasar'
import AlarmModel from '@/AlarmModel'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper'

export default {
  mixins: [ViewMixinHelper],
  components: {
    UserAvatar,
    QList,
    QItem,
    QItemMain,
    QIcon,
    QPopover,
    QModal,
    QBtn,
    ColumnCustomization,
    Pagination,
    CommonListLayout,
    FExportSettings,
    Sort,
    AdvancedSearchWrapper,
    AlarmModel,
    VInfiniteScroll,
  },
  data() {
    return {
      subListLoading: false,
      parentPath: '/app/fa',
      fetchingMore: false,
      createWoIds: [],
      loading: true,
      listCount: null,
      deleteLoading: false,
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['subject'],
        nonSelectableColumns: [
          'type',
          'noOfNotes',
          'lastWoId',
          'lastOccurrenceId',
          'lastOccurredTime',
          'lastClearedTime',
          'key',
          'description',
        ],
        fixedSelectableColumns: [
          'severity',
          'noOfOccurrences',
          'acknowledgedBy',
        ],
        availableColumns: [],
        showLookupColumns: false,
      },
      perPage: 50,
      tableLoading: false,
      sortConfig: {
        orderBy: {
          label: this.$t('alarm.alarm.date_modified'),
          value: 'lastOccurredTime',
        },
        orderType: 'desc',
      },
      sortConfigLists: [],
      isEmpty,
    }
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', this.currentModuleName)
    this.fetchMetaFields()
    let group = this.$store.state.view.groupViews
    if (isEmpty(group)) {
      let param = {
        moduleName: 'bmsalarm',
      }
      this.$store.dispatch('view/loadGroupViews', param)
    }
    if (!isEmpty(this.$route.params.id)) {
      this.loadViewDetails()
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      views: state => state.view.views,
      severityStatus: state => state.alarmSeverity,
      currentViewDetail: state => state.view.currentViewDetail,
      showSearch: state => state.search.active,
      viewDetail: state => state.view.currentViewDetail,
      viewLoading: state => state.view.isLoading,
      searchQuery: state => state.newAlarm.quickSearchQuery,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters([
      'getTicketCategory',
      'getReadingAlarmCategory',
      'getAlarmSeverity',
    ]),
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    canLoadMore() {
      return this.$store.state.newAlarm.canLoadMore
    },
    bmsAlarms() {
      return this.$store.state.newAlarm.alarms.filter(n => n)
    },
    currentModuleName() {
      return 'bmsalarm'
    },
    currentModuleDisplayName() {
      return 'BMS alarm'
    },

    openAlarmId() {
      if (this.$route.params.id) {
        this.$emit('showTag', false)
        return parseInt(this.$route.params.id)
      }
      this.$emit('showTag', true)
      return -1
    },

    viewDetailFields() {
      return this.viewDetail && this.viewDetail['fields']
    },
    view() {
      if (this.$route.query.view) {
        let data = JSON.parse(this.$route.query.view)
        if (data.viewName) {
          return data.viewName
        } else {
          return false
        }
      } else {
        return false
      }
    },
    showLoading() {
      return this.loading || this.viewLoading || this.tableLoading
    },
    currentView() {
      return this.$attrs.viewname || this.$route.params.viewname || null
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    currentView: {
      handler: function(newVal, oldVal) {
        if (oldVal !== newVal && !isEmpty(newVal)) {
          this.$store
            .dispatch('view/loadModuleMeta', this.currentModuleName)
            .then(() => {
              this.loadBmsAlarm()
              this.loadBmsAlarmCount()
              this.fetchMetaFields()
            })
        }
      },
      immediate: true,
    },
    viewDetailFields() {
      this.tableLoading = true
      this.$nextTick(() => {
        this.$refs.tableList ? this.$refs.tableList.doLayout() : null
        this.tableLoading = false
      })
    },
    filters: function(oldVal, newVal) {
      if (oldVal !== newVal) {
        this.loadBmsAlarm()
        this.loadBmsAlarmCount()
      }
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadBmsAlarm()
      }
    },
    searchQuery() {
      this.loadBmsAlarm()
      this.loadBmsAlarmCount()
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
  },
  methods: {
    ...mapActions({
      assignAlarmApi: 'newAlarm/assignAlarm',
      updateAlarmStatus: 'newAlarm/updateAlarmStatus',
      notifyAlarm: 'newAlarm/notifyAlarm',
      acknowledgeAlarm: 'newAlarm/acknowledgeAlarm',
      createWoFromAlarm: 'newAlarm/createWoFromAlarm',
      getRelatedWorkorderId: 'newAlarm/getRelatedWorkorderId',
      deleteAlarm: 'newAlarm/deleteAlarm',
      deleteAlarmNew: 'newAlarm/deleteAlarmNew',
    }),
    viewAlarm(ticketId) {
      this.$router.push({ path: '/app/wo/orders/summary/' + ticketId })
    },
    loadViewDetails() {
      this.$store
        .dispatch('view/loadViewDetail', {
          viewName: this.currentView,
          moduleName: this.currentModuleName,
        })
        .catch(() => {})
    },
    nextPage() {
      if (!this.scrollDisabled) {
        this.subListLoading = true
        this.fetchingMore = true
        this.loadBmsAlarm(true)
      }
    },
    openChild(viewNames) {
      let url = '/app/fa/bmsalarms/' + viewNames + '/newsummary/' + null
      this.$router.replace({ path: url })
      let { currentModuleName } = this

      let queryObj = {
        viewname: viewNames,
        page: this.page,
        filters: this.filters,
        orderBy: this.sortConfig.orderBy.value,
        orderType: this.sortConfig.orderType,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        isNew: true,
        moduleName: currentModuleName,
      }
      this.$store.dispatch('newAlarm/fetchAlarms', queryObj).then(response => {
        if (this.$store.state.newAlarm.alarms.length > 0) {
          this.$router.push({
            path:
              '/app/fa/bmsalarms/' +
              viewNames +
              '/newsummary/' +
              this.$store.state.newAlarm.alarms[0].id,
            query: this.$route.query,
          })
        } else {
          let newPath = this.$route.path.substring(
            0,
            this.$route.path.indexOf('/bmsalarms/')
          )
          newPath += '/bmsalarms/' + viewNames
          this.$router.push({
            path: newPath,
            query: this.$route.query,
          })
        }
      })
    },
    back() {
      let url = '/app/fa/bmsalarms/' + this.$route.params.viewname
      this.$router.push({ path: url, query: this.$route.query })
    },
    loadMore() {
      this.fetchingMore = true
      this.loadBmsAlarm(true)
    },
    getUserName(id) {
      if (id !== -1) {
        if (id) {
          return this.$store.getters.getUser(id).name
        } else {
          return 'Unkown'
        }
      } else {
        return 'Unkown'
      }
    },
    updateAlarmsStatus(alarm) {
      this.updateAlarmStatus({
        occurrence: { id: alarm.lastOccurrenceId },
        alarm: alarm,
        clearedTime: Date.now(),
        severity: this.severityStatus.find(
          status => status.severity === 'Clear'
        ),
      })
      this.bmsAlarms.clearedBy = this.$account.user
      this.loadBmsAlarm()
      this.loadBmsAlarmCount()
    },
    createWoDialog(idList) {
      this.createWoIds = idList
      this.$refs['createWOModel'].open()
      this.$refs.confirmWoModel.reset()
    },
    closeWoDialog() {
      this.$refs['createWOModel'].close()
    },
    createWO(data) {
      let fields = {}
      if (data.category) {
        fields.category = {
          id: data.category,
          name: this.getTicketCategory(data.category).name,
        }
      }
      if (data.priority) {
        fields.priority = {
          id: data.priority,
        }
      }
      if (data.assignedTo) {
        fields.assignedTo = data.assignedTo
      }
      if (data.assignmentGroup) {
        fields.assignmentGroup = data.assignmentGroup
      }
      if (data.siteId > 0) {
        fields.siteId = data.siteId
      }
      this.$store
        .dispatch('newAlarm/createWoFromAlarm', {
          id: this.createWoIds,
          fields: fields,
        })
        .then(d => {
          if (d.data.responseCode === 1) {
            this.$message({
              message: d.data.message,
              type: 'error',
            })
          } else {
            this.$dialog.notify('Workorder created successfully!')
            this.$refs['createWOModel'].close()
          }
        })
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
    opensummary(id) {
      this.$router.push({
        path:
          '/app/fa/bmsalarms/' +
          this.$route.params.viewname +
          '/newsummary/' +
          id,
        query: this.$route.query,
      })
    },
    fetchMetaFields() {
      let { currentModuleName } = this
      this.$http
        .get('/module/metafields?moduleName=' + currentModuleName)
        .then(response => {
          let moduleMetaObject = response.data.meta
          if (!isEmpty(moduleMetaObject) && !isEmpty(moduleMetaObject.fields)) {
            for (let i = 0; i < moduleMetaObject.fields.length; i++) {
              this.sortConfigLists.push(moduleMetaObject.fields[i].name)
            }
          }
        })
    },

    refreshAssetList() {
      this.loadBmsAlarm()
    },

    loadBmsAlarm(loadMoreData) {
      this.loading = true
      let { currentModuleName } = this
      let queryObj = {
        viewname: this.currentView,
        page: loadMoreData
          ? this.$store.state.newAlarm.currentPage + 1
          : this.page,
        filters: this.filters,
        orderBy: this.sortConfig.orderBy.value,
        orderType: this.sortConfig.orderType,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        subList: loadMoreData,
        isNew: true,
        moduleName: currentModuleName,
      }
      this.$store.dispatch('newAlarm/fetchAlarms', queryObj).finally(() => {
        this.loading = false
        this.fetchingMore = false
      })
    },
    loadBmsAlarmCount() {
      let { currentModuleName } = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        moduleName: currentModuleName,
        count: true,
      }
      let url = 'v2/newAlarms/view/' + queryObj.viewname + '?fetchCount=true'
      let params
      params = 'isCount=' + queryObj.count
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
      params = params + '&alarmModule=' + queryObj.moduleName
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      if (queryObj.criteriaIds) {
        params = params + '&criteriaIds=' + queryObj.criteriaIds
      }
      if (queryObj.includeParentFilter) {
        params = params + '&includeParentFilter=' + queryObj.includeParentFilter
      }
      url = url + '&' + params
      this.$http
        .get(url)
        .then(response => {
          this.listCount = response.data.result.count
        })
        .catch(function(error) {
          console.log(error)
        })
    },

    deleteAlarmConf(id) {
      this.$dialog
        .confirm({
          title: 'Delete Alarm',
          message: 'Are you sure you want to delete this alarm ?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$store.dispatch('newAlarm/deleteAlarmNew', id).then(() => {
              this.$dialog.notify('Alarm deleted successfully')
            })
          }
          this.loadBmsAlarmCount()
        })
    },

    updateSort(sorting) {
      let { name = '' } = this.$getProperty(this.metaInfo, 'module', {})

      this.$store
        .dispatch('view/savesorting', {
          viewName: this.currentView,
          orderBy: sorting.orderBy,
          orderType: sorting.orderType,
          categoryModule: true,
          moduleName: name,
        })
        .then(() => this.loadBmsAlarm())
    },
  },
}
</script>

<style lang="scss">
.alarm-list-page {
  .fc-timer.alarm-timer .t-label {
    font-size: 16px !important;
    font-weight: 500;
    letter-spacing: 0.6px;
    padding-top: 0;
  }
  .fc-timer.alarm-timer .t-sublabel {
    font-size: 9px !important;
    letter-spacing: 1.1px;
    color: #a5a5a5;
  }
  .alarm-timer {
    padding: 0 !important;
  }
  .fc-list-view-table2 tbody tr.tablerow td:first-child {
    width: 10px !important;
  }
  .fc-alarm-list-view {
    height: 43px !important;
  }
}
.page-sort-popover {
  right: 40px !important;
}
.qr-dialog .el-radio + .el-radio {
  margin-left: 0px;
}
@media print {
  .fc-list-table-container .el-table {
    display: none;
  }
}
.as-view-icon {
  width: 15px;
  margin-right: 5px;
}
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
}
</style>
