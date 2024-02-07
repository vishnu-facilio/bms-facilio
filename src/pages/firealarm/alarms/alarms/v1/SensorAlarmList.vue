<template>
  <div class="height100 alarm-list-page">
    <CommonListLayout
      v-if="openAlarmId === -1"
      :moduleName="currentModuleName"
      :showViewRearrange="true"
      :showViewEdit="true"
      :visibleViewCount="3"
      :getPageTitle="() => currentModuleDisplayName"
      :pathPrefix="`${parentPath}/sensoralarms/`"
    >
      <template #header>
        <template v-if="!showSearch">
          <pagination
            :total="listCount"
            :perPage="50"
            class="pL15 fc-black-small-txt-12"
          ></pagination>
          <span class="separator" v-if="listCount > 0">|</span>

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
          <span class="separator">|</span>
        </template>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.search')"
          placement="left"
        >
          <AdvancedSearch
            :key="`${currentModuleName}-search`"
            :moduleName="currentModuleName"
            :moduleDisplayName="currentModuleDisplayName"
          >
          </AdvancedSearch>
        </el-tooltip>
      </template>

      <!-- List Content -->
      <div>
        <FTags :key="`ftags-list-${currentModuleName}`"></FTags>
      </div>

      <div class="scrollable">
        <div
          class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
        >
          <div v-if="showLoading" class="flex-middle fc-empty-white">
            <spinner :show="showLoading" size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(sensorAlarms) && !showLoading"
            class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
          >
            <inline-svg
              src="svgs/emptystate/alarmEmpty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="nowo-label">
              {{ $t('alarm.alarm.no_alarms_found') }}
            </div>
          </div>
          <div v-if="!showLoading && !$validation.isEmpty(sensorAlarms)">
            <div class="view-column-chooser" @click="showColumnSettings = true">
              <InlineSvg
                src="column-setting"
                iconClass="icon icon-md mT15"
              ></InlineSvg>
            </div>
            <el-table :data="sensorAlarms" style="width: 100%" height="100%">
              <el-table-column
                fixed
                align="left"
                prop="readingFieldId"
                label="Sensor"
                min-width="250"
              >
                <template v-slot="alarm">
                  <div
                    @click="opensummary(alarm.row.id)"
                    small
                    class="flex-middle"
                  >
                    <div>
                      <div
                        class="fw5 ellipsis textoverflow-ellipsis width300px"
                        :title="
                          !$validation.isEmpty(alarm.row.readingField)
                            ? alarm.row.readingField.displayName
                            : '---'
                        "
                        v-tippy="{
                          placement: 'top',
                          arrow: true,
                        }"
                      >
                        {{
                          !$validation.isEmpty(alarm.row.readingField)
                            ? alarm.row.readingField.displayName
                            : '---'
                        }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column
                :fixed="field.name === 'readingFieldId'"
                :align="
                  field.field.dataTypeEnum === 'DECIMAL' ? 'right' : 'left'
                "
                :prop="field.name"
                v-for="(field, index) in viewColumns"
                v-if="!isFixedColumn(field.name) || field.parentField"
                :key="index"
                :label="field.displayName"
                min-width="200"
              >
                <template v-slot="alarm">
                  <div v-if="!isFixedColumn(field.name) || field.parentField">
                    <div v-if="field.name === 'alarmType'">
                      <span
                        class="q-item-label"
                        style="font-size: 13px; letter-spacing: 0.4px"
                        >{{
                          alarm.row.alarmTypeVal
                            ? alarm.row.alarmTypeVal
                            : '---'
                        }}</span
                      >
                    </div>

                    <div v-else-if="field.name === 'readingAlarmCategory'">
                      <span
                        class="q-item-label"
                        style="font-size: 13px; letter-spacing: 0.4px"
                      >
                        {{
                          alarm.row.readingAlarmCategory &&
                          alarm.row.readingAlarmCategory.id > -1
                            ? getReadingAlarmCategory(
                                alarm.row.readingAlarmCategory.id
                              ).name
                            : '---'
                        }}
                      </span>
                    </div>

                    <div
                      v-else-if="field.name === 'lastOccurredTime'"
                      style="min-width: 150px"
                    >
                      <timer
                        class="alarm-timer"
                        :time="alarm.row.lastOccurredTime"
                        :title="alarm.row.lastOccurredTime | formatDate()"
                        v-tippy="{
                          html: '#timer_popover_' + alarm.row.id,
                          distance: 0,
                          interactive: true,
                          theme: 'light',
                          animation: 'scale',
                          arrow: true,
                        }"
                      ></timer>
                      <div :id="'timer_popover_' + alarm.row.id" class="hide">
                        <div
                          style="
                            font-size: 6px;
                            letter-spacing: 0.4px;
                            color: #666666;
                          "
                        >
                          {{ alarm.row.lastOccurredTime | formatDate() }}
                        </div>
                      </div>
                    </div>

                    <div
                      v-else-if="field.name === 'lastCreatedTime'"
                      style="min-width: 150px"
                    >
                      <div :id="'timer_popover_' + alarm.row.id" class="">
                        <div
                          style="
                            font-size: 12px;
                            letter-spacing: 0.5px;
                            color: #666666;
                          "
                          v-if="alarm.row.lastCreatedTime > -1"
                        >
                          {{ alarm.row.lastCreatedTime | formatDate() }}
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
                      v-else-if="field.name === 'acknowledgedBy'"
                      style="min-width: 150px"
                    >
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
                              alarm: alarm.row,
                              occurrence: {
                                id:
                                  alarm.row && alarm.row.lastOccurrenceId
                                    ? alarm.row.lastOccurrenceId
                                    : null,
                              },
                              acknowledged: true,
                              acknowledgedBy: $account.user,
                            })
                          "
                          v-if="
                            !alarm.row.acknowledged && isActiveAlarm(alarm.row)
                          "
                          >{{ $t('alarm.alarm.acknowledge') }}</q-btn
                        >
                        <span
                          class="q-item-label"
                          v-else-if="!isActiveAlarm(alarm.row)"
                        ></span>
                        <span class="q-item-label f11" v-else>
                          <div
                            :id="'contentpopup2_' + alarm.row.id"
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
                                      :user="
                                        getUserName(alarm.row.acknowledgedBy.id)
                                      "
                                    ></user-avatar>
                                  </span>
                                </div>
                                <div class="hover-row">
                                  {{
                                    alarm.row.acknowledgedTime > 0
                                      ? alarm.row.acknowledgedTime
                                      : new Date() | fromNow
                                  }}
                                </div>
                              </div>
                            </div>
                          </div>
                          <span
                            class="f11"
                            style="letter-spacing: 0.3px; color: #333333"
                            v-tippy="{
                              html: 'contentpopup2_' + alarm.row.id,
                              interactive: true,
                              reactive: true,
                              distance: 15,
                              theme: 'light',
                              animation: 'scale',
                            }"
                            >{{ $t('alarm.alarm.acknowledged') }}</span
                          >
                          <div style="text-align: left; color: #8a8a8a">
                            {{
                              alarm.row.acknowledgedTime > 0
                                ? alarm.row.acknowledgedTime
                                : new Date() | fromNow
                            }}
                          </div>
                        </span>
                      </div>
                    </div>

                    <div
                      v-else-if="field.name === 'noOfOccurrences'"
                      @click="opensummary(alarm.row.id)"
                      style="min-width: 150px"
                    >
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
                          alarm.row.noOfOccurrences &&
                          alarm.row.noOfOccurrences > -1
                            ? alarm.row.noOfOccurrences
                            : '0'
                        }}
                      </span>
                      <div style="clear: both"></div>
                    </div>

                    <div
                      class="table-subheading"
                      v-else
                      :class="{
                        'text-right': field.field.dataTypeEnum === 'DECIMAL',
                      }"
                    >
                      {{ getColumnDisplayValue(field, alarm.row) || '---' }}
                    </div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column
                prop
                label
                width="130"
                class="visibility-visible-actions"
                fixed="right"
              >
                <template v-slot="alarm">
                  <q-icon
                    slot="right"
                    name="more_vert"
                    style="float: right; font-size: 20px; color: #d8d8d8"
                  >
                    <q-popover ref="moreactionspopover">
                      <q-list link class="no-border">
                        <q-item v-if="alarm.row.lastWoId > 0">
                          <q-item-main
                            :label="$t('alarm.alarm.view_workorder')"
                            @click="
                              viewAlarm(alarm.row.lastWoId),
                                $refs.moreactionspopover[index].close()
                            "
                          />
                        </q-item>
                        <q-item
                          v-else-if="
                            $hasPermission('alarm:CREATE_WO') &&
                              alarm.row &&
                              alarm.row.lastOccurrenceId &&
                              isActiveAlarm(alarm.row)
                          "
                        >
                          <q-item-main
                            :label="$t('alarm.alarm.create_workorder')"
                            @click="
                              createWoDialog(alarm.row.lastOccurrenceId),
                                $refs.moreactionspopover[index].close()
                            "
                          />
                        </q-item>
                        <q-item>
                          <q-item-main
                            :label="$t('common._common.delete')"
                            @click="
                              deleteAlarmConf([alarm.row.id]),
                                $refs.moreactionspopover[index].close()
                            "
                          />
                        </q-item>
                      </q-list>
                    </q-popover>
                  </q-icon>
                </template>
              </el-table-column>
            </el-table>
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
          :viewName="currentView"
          excludeMainField="true"
        ></column-customization>

        <portal to="view-manager-link">
          <router-link
            tag="div"
            :to="`/app/fa/sensoralarms/sensorrollupalarm/viewmanager`"
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
              <tbody v-if="sensorAlarms.length">
                <template v-for="(alarm, index) in sensorAlarms">
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
import CommonListLayout from 'newapp/list/DeprecatedCommonLayout'
import Pagination from '@/list/FPagination'
import FExportSettings from '@/FExportSettings'
import Sort from 'newapp/components/Sort'
import { mapState, mapGetters, mapActions } from 'vuex'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import { QList, QItem, QItemMain, QIcon, QPopover, QModal, QBtn } from 'quasar'
import AlarmModel from '@/AlarmModel'
import Timer from '@/Timer'
import { mapStateWithLogging } from 'store/utils/log-map-state'

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
    Timer,
    QBtn,
    ColumnCustomization,
    Pagination,
    CommonListLayout,
    FTags,
    FExportSettings,
    Sort,
    AdvancedSearch,
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
    }
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', this.currentModuleName)
    this.fetchMetaFields()
    let group = this.$store.state.view.groupViews
    if (isEmpty(group)) {
      let param = {
        moduleName: 'sensorrollupalarm',
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
    sensorAlarms() {
      return this.$store.state.newAlarm.alarms.filter(n => n)
    },
    currentModuleName() {
      return 'sensorrollupalarm'
    },
    currentModuleDisplayName() {
      return 'Sensor alarm'
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
              this.loadSensorAlarm()
              this.loadSensorAlarmCount()
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
        this.loadSensorAlarm()
        this.loadSensorAlarmCount()
      }
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadSensorAlarm()
      }
    },
    searchQuery() {
      this.loadSensorAlarm()
      this.loadSensorAlarmCount()
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
        this.loadSensorAlarm(true)
      }
    },
    openChild(viewNames) {
      let url = '/app/fa/sensoralarms/' + viewNames + '/newsummary/' + null
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
              '/app/fa/sensoralarms/' +
              viewNames +
              '/newsummary/' +
              this.$store.state.newAlarm.alarms[0].id,
            query: this.$route.query,
          })
        } else {
          let newPath = this.$route.path.substring(
            0,
            this.$route.path.indexOf('/sensoralarms/')
          )
          newPath += '/sensoralarms/' + viewNames
          this.$router.push({
            path: newPath,
            query: this.$route.query,
          })
        }
      })
    },
    back() {
      let url = '/app/fa/sensoralarms/' + this.$route.params.viewname
      this.$router.push({ path: url, query: this.$route.query })
    },
    loadMore() {
      this.fetchingMore = true
      this.loadSensorAlarm(true)
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
      this.sensorAlarms.clearedBy = this.$account.user
      this.loadSensorAlarm()
      this.loadSensorAlarmCount()
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
            this.$refs['createWOModel'].close()
          } else {
            this.$message.success(
              this.$t('common.wo_report.workorder_created_success')
            )
            this.$refs['createWOModel'].close()
          }
        })
    },
    isActiveAlarm(alarm) {
      if (
        alarm.severity &&
        alarm.severity.id &&
        this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear'
      ) {
        return true
      }
      return false
    },
    opensummary(id) {
      this.$router.push({
        path:
          '/app/fa/sensoralarms/' +
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
            this.sortConfigLists = []
            for (let i = 0; i < moduleMetaObject.fields.length; i++) {
              this.sortConfigLists.push(moduleMetaObject.fields[i].name)
            }
          }
        })
    },

    refreshAssetList() {
      this.loadSensorAlarm()
    },

    loadSensorAlarm(loadMoreData) {
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
    loadSensorAlarmCount() {
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
          title: this.$t('common.header.delete_alarm'),
          message: this.$t('common._common.are_you_want_delete_this_alarm'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.$store.dispatch('newAlarm/deleteAlarmNew', id).then(() => {
              this.$dialog.notify('Alarm deleted successfully')
            })
          }
          this.loadSensorAlarmCount()
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
        .then(() => this.loadSensorAlarm())
    },
  },
}
</script>

<style lang="scss">
.alarm-list-page {
  .fc-timer.alarm-timer .t-label {
    font-size: 10px !important;
    font-weight: 500;
    letter-spacing: 0.1px;
    padding-top: 0;
  }
  .fc-timer.alarm-timer .t-sublabel {
    font-size: 7px !important;
    letter-spacing: 0.6px;
    color: #a5a5a5;
    padding-top: 0px;
  }
  .alarm-timer {
    padding: 0 !important;
    height: 40px;
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
</style>
