<template>
  <div class="height100">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openAlarmId === -1"
      :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
    >
      <!-- table start -->
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <div class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%; right: 29%"
          />
        </div>
      </div>

      <el-table
        :data="alarms"
        style="width: 100%"
        height="100%"
        :index="indexMethod"
      >
        <template slot="empty">
          <img src="~statics/noData-light.png" width="100" height="100" />
          <div class="mT10 label-txt-black f14">No Alarms Available</div>
        </template>
        <el-table-column fixed width="130" label=" " class="pR0">
          <template v-slot="alarm">
            <div v-if="canShowColumn('severity')">
              <div
                class="q-item-label self-center f10 secondary-color"
                v-if="alarm.row.severity"
                style="min-width: 90px"
              >
                <div
                  class="q-item-label uppercase severityTag"
                  v-bind:style="{
                    'background-color': getAlarmSeverity(alarm.row.severity.id)
                      .color,
                  }"
                >
                  {{
                    alarm.row.severity
                      ? getAlarmSeverity(alarm.row.severity.id).displayName
                      : '---'
                  }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="100" label="ID" fixed>
          <template v-slot="alarm" class="pL0">
            <div class="fc-id">{{ '#' + alarm.row.serialNumber }}</div>
          </template>
        </el-table-column>
        <el-table-column
          width="300"
          prop="name"
          label="message"
          fixed
          class="pR0"
        >
          <template v-slot="alarm" class="pL0">
            <div class="fw5">
              {{ alarm.row.subject }}
            </div>
            <div class="ellipsis" style="font-size: 12px; color: #8a8a8a">
              {{ alarm.row.modifiedTime | fromNow }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(field, index) in viewColumns"
          :key="index"
          :prop="field.name"
          :label="field.displayName"
          min-width="220"
          v-if="!isFixedColumn(field.name)"
        >
          <template v-slot="alarm">
            <div v-if="field.name === 'alarmType'">
              <span
                class="q-item-label"
                style="font-size: 13px; letter-spacing: 0.4px"
                >{{
                  alarm.row.alarmTypeVal ? alarm.row.alarmTypeVal : '---'
                }}</span
              >
            </div>
            <div
              v-else-if="field.name === 'modifiedTime'"
              @click="opensummary(alarm.row.id)"
              style="min-width: 150px"
            >
              <timer
                class="alarm-timer"
                :time="alarm.row.modifiedTime"
                :title="alarm.row.modifiedTime | formatDate()"
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
                  style="font-size: 12px; letter-spacing: 0.5px; color: #666666"
                >
                  {{ alarm.row.modifiedTime | formatDate() }}
                </div>
              </div>
            </div>
            <div
              v-else-if="
                field.name === 'previousSeverity' && alarm.row.previousSeverity
              "
              class="q-item-label self-center f10 secondary-color"
            >
              <div
                class="q-item-label uppercase"
                v-bind:class="
                  alarm.row.previousSeverity ? { severityTag: true } : ''
                "
                v-bind:style="{
                  'background-color': getAlarmSeverity(
                    alarm.row.previousSeverity.id
                  ).color,
                }"
              >
                {{
                  alarm.row.previousSeverity
                    ? getAlarmSeverity(alarm.row.previousSeverity.id)
                        .displayName
                    : '---'
                }}
              </div>
            </div>
            <div v-else>
              <span
                class="q-item-label"
                style="font-size: 13px; letter-spacing: 0.4px"
                >{{ getColumnDisplayValue(field, alarm.row) }}</span
              >
              <div
                v-if="
                  field.field.name === 'resource' &&
                    alarm.row.resource &&
                    alarm.row.resource.space &&
                    alarm.row.resource.space.id > 0
                "
                style="padding-top: 5px"
                class="flex-middle"
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
                  :title="alarm.row.resource.space.name"
                  style="max-width: 85%; font-size: 10px"
                  >{{ alarm.row.resource.space.name }}</span
                >
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="150">
          <template v-slot="alarm">
            <div v-if="canShowColumn('noOfEvents')">
              <div v-if="alarm.row.noOfEvents > 0" class="flex-middle">
                <div class="flLeft">
                  <img
                    src="~statics/icons/event.svg"
                    style="width: 15px"
                    class="flLeft"
                  />
                </div>
                <span class="flLeft pL5">{{ alarm.row.noOfEvents }}</span>
                <div style="clear: both"></div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="160">
          <template v-slot="alarm">
            <div v-if="canShowColumn('acknowledgedBy')">
              <div
                v-if="$hasPermission('alarm:ACKNOWLEDGE_ALARM')"
                class="pull-left"
                style="font-size: 13px; color: #383434; white-space: nowrap"
              >
                <q-btn
                  color="secondary"
                  class="uppercase fia-alert-btn"
                  small
                  outline
                  @click="
                    acknowledgeAlarm({
                      alarm: alarm,
                      isAcknowledged: true,
                      acknowledgedBy: $account.user,
                    })
                  "
                  v-if="!alarm.row.isAcknowledged && isActiveAlarm(alarm.row)"
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
                              :user="getUserName(alarm.row.acknowledgedBy.id)"
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
                    style="
                      font-size: 14px;
                      letter-spacing: 0.3px;
                      color: #333333;
                    "
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
                      alarm.row.acknowledgedTime > 0
                        ? alarm.row.acknowledgedTime
                        : new Date() | fromNow
                    }}
                  </div>
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="150">
          <template v-slot="alarm">
            <q-icon
              slot="right"
              name="more_vert"
              style="float: right; font-size: 20px; color: #d8d8d8"
            >
              <q-popover ref="moreactionspopover">
                <q-list link class="no-border" v-if="isActiveAlarm(alarm.row)">
                  <q-item>
                    <q-item-main
                      :label="$t('alarm.alarm.clear')"
                      @click="
                        updateAlarmStatus({
                          alarm: alarm.row,
                          severity: severityStatus.find(
                            status => status.severity === 'Clear'
                          ),
                        }),
                          $refs.moreactionspopover[index].close()
                      "
                    />
                  </q-item>
                  <q-item>
                    <q-item-main
                      :label="$t('common._common.delete')"
                      @click="
                        deleteAlarm([alarm.row.id]),
                          $refs.moreactionspopover[index].close()
                      "
                    />
                  </q-item>
                  <q-item v-if="alarm.woId > 0">
                    <q-item-main
                      :label="$t('alarm.alarm.view_workorder')"
                      @click="
                        viewAlarm(alarm.row.woId),
                          $refs.moreactionspopover[index].close()
                      "
                    />
                  </q-item>
                  <q-item v-else-if="$hasPermission('alarm:CREATE_WO')">
                    <q-item-main
                      :label="$t('alarm.alarm.create_workorder')"
                      @click="
                        createWoDialog([alarm.row.id]),
                          $refs.moreactionspopover[index].close()
                      "
                    />
                  </q-item>
                </q-list>
                <q-list link class="no-border" v-else>
                  <q-item
                    ><q-item-main
                      :label="$t('common._common.delete')"
                      @click="
                        deleteAlarm([alarm.row.id]),
                          $refs.moreactionspopover[index].close()
                      "
                  /></q-item>
                </q-list>
              </q-popover>
            </q-icon>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="fc-column-view height100 border-border-none" v-else>
      <div class="row fc-column-view-title height100">
        <div
          class="col-4 fc-column-view-left height100"
          style="max-width: 26.3333%; flex: 0 0 26.3333%"
        >
          <div class="row container col-header pL20">
            <div class="col-12">
              <div class="pull-left">
                <el-dropdown @command="openChild" class="alarm-dp">
                  <span class="el-dropdown-link pointer">
                    {{ currentViewDetail.displayName
                    }}<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown" class="wo-dropdownmenu">
                    <el-dropdown-item
                      v-for="(view, idx) in views"
                      :key="idx"
                      :command="view.name"
                      v-if="view.name !== currentViewDetail.name"
                    >
                      {{ view.displayName }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </div>
          <div class="row fia-tabel-nocontent" v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div v-else>
            <div
              class="row fc-column-view-records height100"
              style="background: #fff"
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
                <tbody v-if="alarms.length">
                  <tr
                    @click="opensummary(alarm.row.id)"
                    class="tablerow"
                    v-for="(alarm, index) in alarms"
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
                          <div class="uppercase fc-id">
                            #{{ alarm.serialNumber }}
                          </div>
                          <div class="separator">|</div>
                          <div class="fc-grey2-text12">
                            {{ alarm.modifiedTime | fromNow }}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td class="text-left" v-if="alarm.severity">
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
                          >{{
                            getAlarmSeverity(alarm.severity.id).displayName
                          }}</span
                        >
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
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
    <q-modal
      ref="createNewAlarmModel"
      position="right"
      content-classes="fc-create-record"
      @close="closeDialog"
    >
      <q-btn flat @click="$refs.createNewAlarmModel.close()">
        <q-icon name="close" />
      </q-btn>
      <new-alarm @closed="closeDialog"></new-alarm>
    </q-modal>
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
      moduleName="alarm"
      :columnConfig="columnConfig"
      :viewName="currentView"
    ></column-customization>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
import Timer from '@/Timer'
import NewAlarm from 'pages/firealarm/alarms/NewAlarm'
import { mapActions, mapState, mapGetters } from 'vuex'
import AlarmModel from '@/AlarmModel'
import infiniteScroll from 'vue-infinite-scroll'
import ColumnCustomization from '@/ColumnCustomization'
import ViewMixinHelper from '@/mixins/ViewMixin'
import { QList, QItem, QItemMain, QIcon, QPopover, QModal, QBtn } from 'quasar'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  mixins: [ViewMixinHelper],
  data() {
    return {
      createWoIds: [],
      loading: true,
      fetchingMore: false,
      sortConfig: {
        orderBy: {
          label: this.$t('alarm.alarm.date_modified'),
          value: 'modifiedTime',
        },
        orderType: 'desc',
        list: [
          {
            label: this.$t('maintenance.wr_list.created_on'),
            value: 'createdTime',
          },
          {
            label: this.$t('maintenance.wr_list.due_by_time'),
            value: 'dueDate',
          },
          {
            label: this.$t('maintenance.wr_list.subject'),
            value: 'subject',
          },
        ],
      },
      showColumnSettings: false,
      columnConfig: {
        fixedColumns: ['subject'],
        fixedSelectableColumns: ['severity', 'acknowledgedBy', 'noOfEvents'],
        availableColumns: [
          'alarmClass',
          'alarmType',
          'previousSeverity',
          'condition',
          'modifiedTime',
          'createdTime',
          'clearedTime',
          'source',
          'resource',
        ],
        showLookupColumns: false,
        lookupToShow: ['asset', 'space'],
      },
    }
  },
  components: {
    QList,
    QItem,
    QItemMain,
    QIcon,
    QPopover,
    QModal,
    NewAlarm,
    Timer,
    UserAvatar,
    QBtn,
    AlarmModel,
    ColumnCustomization,
  },
  directives: {
    infiniteScroll,
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('loadGroups')
    this.loadAlarms()
  },
  mounted() {
    this.loadAlarmCount()
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      views: state => state.view.views,
      severityStatus: state => state.alarmSeverity,
      currentViewDetail: state => state.view.currentViewDetail,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters([
      'getTicketPriority',
      'getTicketCategory',
      'getAlarmSeverity',
    ]),
    alarms() {
      return this.$store.state.alarm.alarms.filter(n => n)
    },
    openAlarmId() {
      if (this.$route.params.id) {
        this.$emit('showTag', false)
        return parseInt(this.$route.params.id)
      }
      this.$emit('showTag', true)
      return -1
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    sorting() {
      return this.$store.state.alarm.sorting
    },
    searchQuery() {
      return this.$store.state.alarm.quickSearchQuery
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadAlarms()
        this.loadAlarmCount()
      }
    },
    filters: function(newVal) {
      this.loadAlarms()
      this.loadAlarmCount()
    },
    sorting: function(newVal) {
      this.sortConfig.orderBy = this.sorting.orderBy
      this.sortConfig.orderType = this.sorting.orderType
      this.loadAlarms()
      this.loadAlarmCount()
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadAlarms()
      }
    },
    searchQuery: function(newVal) {
      this.quickSearchQuery = this.searchQuery
      this.loadAlarms()
      this.loadAlarmCount()
    },
  },
  filters: {
    viewName: function(name) {
      if (name === 'active') {
        return 'Active Alarms'
      } else if (name === 'unacknowledged') {
        return 'Unacknowledged Alarms'
      } else if (name === 'critical') {
        return 'Critical Alarms'
      } else if (name === 'major') {
        return 'Major Alarms'
      } else if (name === 'minor') {
        return 'Minor Alarms'
      } else if (name === 'fire') {
        return 'Fire Alarms'
      } else if (name === 'energy') {
        return 'Energy Alarms'
      } else if (name === 'hvac') {
        return 'HVAC Alarms'
      } else if (name === 'cleared') {
        return 'Cleared Alarms'
      } else if (name === 'anomalies') {
        return 'Anomalies'
      } else if (name === 'fire') {
        return 'Fire Alarms'
      }
      return 'Alarms'
    },
  },
  methods: {
    ...mapActions({
      assignAlarmApi: 'alarm/assignAlarm',
      updateAlarmStatus: 'alarm/updateAlarmStatus',
      notifyAlarm: 'alarm/notifyAlarm',
      acknowledgeAlarm: 'alarm/acknowledgeAlarm',
      createWoFromAlarm: 'alarm/createWoFromAlarm',
      getRelatedWorkorderId: 'alarm/getRelatedWorkorderId',
      deleteAlarm: 'alarm/deleteAlarm',
    }),
    openChild(viewNames) {
      let self = this
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
      }
      this.$store
        .dispatch('alarm/fetchAlarms', queryObj)
        .then(function(response) {
          if (self.$store.state.alarm.alarms.length > 0) {
            self.$router.push({
              path:
                '/app/fa/faults/' +
                viewNames +
                '/newsummary/' +
                self.$store.state.alarm.alarms[0].id,
              query: self.$route.query,
            })
          } else {
            let newPath = self.$route.path.substring(
              0,
              self.$route.path.indexOf('/alarms/')
            )
            newPath += '/alarms/' + viewNames
            self.$router.push({
              path: newPath,
              query: self.$route.query,
            })
          }
        })
    },
    createWO(data) {
      let self = this

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
          name: this.getTicketPriority(data.priority),
        }
      }
      if (data.assignedTo) {
        fields.assignedTo = data.assignedTo
      }
      if (data.assignmentGroup) {
        fields.assignmentGroup = data.assignmentGroup
      }
      self.$store
        .dispatch('alarm/createWoFromAlarm', {
          id: self.createWoIds,
          fields: fields,
        })
        .then(function() {
          self.$dialog.notify(self.$t('alarm.alarm.wo_created_success'))
          self.$refs['createWOModel'].close()
        })
    },
    createWoDialog(idList) {
      this.createWoIds = idList
      this.$refs['createWOModel'].open()
      this.$refs.confirmWoModel.reset()
    },
    closeWoDialog() {
      this.$refs['createWOModel'].close()
    },
    loadAlarms() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.filters,
        orderBy: this.sortConfig.orderBy.value,
        orderType: this.sortConfig.orderType,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        isNew: true,
      }
      self.$store
        .dispatch('alarm/fetchAlarms', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
      self.setTitle(self.$options.filters.viewName(self.currentView))
    },
    loadAlarmCount() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
        filters: this.filters,
        search: this.quickSearchQuery,
        criteriaIds: this.$route.query.criteriaIds,
        includeParentFilter: this.includeParentFilter,
        count: true,
      }
      let url = '/v2/alarms/alarmCount?viewName=' + queryObj.viewname
      let params
      params = 'isCount=' + queryObj.count
      if (queryObj.filters) {
        params =
          params +
          '&filters=' +
          encodeURIComponent(JSON.stringify(queryObj.filters))
      }
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
      self.$http
        .get(url)
        .then(function(response) {
          console.log('+++++++++++++++' + response.data.result.count)
          console.log(response)
          self.listcount = response.data.count
          self.$emit('syncCount', response.data.result.count)
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
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
    opensummary(id) {
      this.$router.push({
        path:
          '/app/fa/faults/' + this.$route.params.viewname + '/newsummary/' + id,
        query: this.$route.query,
      })
    },
    assignAlarm: function(alarm, userId, userName) {
      let data = {
        id: [alarm.id],
        alarm: {
          assignedTo: {
            id: userId,
            name: userName,
          },
        },
      }
      this.assignAlarmApi({ data: data, alarm: alarm })
    },
    indexMethod(index) {
      return index * 2
    },
    viewAlarm(ticketId) {
      let self = this
      self.$router.push({ path: '/app/wo/orders/summary/' + ticketId })
    },
    closeDialog() {},
    loadMore() {
      this.fetchingMore = true
      this.loadAlarms(true)
    },
  },
}
</script>
<style>
.fc-timer.alarm-timer .t-label {
  font-size: 16px !important;
  font-weight: 500;
  letter-spacing: 0.6px;
  padding-top: 10px;
}
.fc-timer.alarm-timer .t-sublabel {
  font-size: 9px !important;
  letter-spacing: 1.1px;
  text-align: center;
  color: #a5a5a5;
}
</style>
