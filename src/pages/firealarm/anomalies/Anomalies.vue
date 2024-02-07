<template>
  <div class="layout container fc-anamalies-page" style="padding-bottom:100px">
    <div
      v-if="openAlarmId === -1"
      class="pL10 pR10 height100 scrollable mT10"
      style="padding-bottom:0px"
    >
      <div
        class="row full-layout-white fc-border-1 alarm-table scrollbar-style"
        style="padding-bottom:20px;overflow-x:scroll;display: block;"
      >
        <table class="fc-list-view-table fc-alarm-summary-table">
          <thead>
            <th class="text-left" v-if="canShowColumn('severity')"></th>
            <th class="text-left uppercase" style="width:350px;">
              {{ $t('alarm.alarm.message') }}
            </th>
            <th
              class="uppercase"
              v-for="(field, index) in viewColumns"
              v-if="!isFixedColumn(field.name)"
              :key="index"
            >
              <div :class="{ 'text-left': field.name === 'lastOccurredTime' }">
                {{ field.displayName }}
              </div>
            </th>
            <th v-if="canShowColumn('noOfEvents')"></th>
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
          <tbody v-else class="scrollable">
            <tr class="nowotd" v-if="!alarms.length">
              <td colspan="100%">
                <div class="fia-tabel-nocontent text-center">
                  <img class="nowo-logo" src="~assets/no_request.jpg" />
                  <div class="nowo-label">No Anomalies Found</div>
                </div>
              </td>
            </tr>
            <tr
              v-else
              class="tablerow"
              v-for="(alarm, index) in alarms"
              :key="index"
            >
              <td v-if="canShowColumn('severity')">
                <div
                  class="q-item-label self-center f10 secondary-color"
                  v-if="alarm.severity"
                  style="min-width: 90px;margin-left: 13%;"
                >
                  <div
                    class="q-item-label uppercase severityTag"
                    v-bind:style="{
                      'background-color': getAlarmSeverity(alarm.severity.id)
                        .color,
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
                  style="min-width: 250px;"
                >
                  <div
                    class="q-item-sublabel ellipsis"
                    style="margin-top: 5px;font-size:12px;"
                  >
                    <span class="fc-id">#{{ alarm.id }}</span>
                  </div>
                  <div
                    class="q-item-label fw5"
                    style="margin-top: 1px;font-size: 15px;letter-spacing: 0.3px;"
                  >
                    {{ alarm.subject }}
                  </div>
                  <div
                    class="ellipsis"
                    style="font-size: 12px;color:#8a8a8a;margin-top:5px;"
                  >
                    <span style="">{{ alarm.lastOccurredTime | fromNow }}</span>
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
                    style="font-size:13px;letter-spacing: 0.4px;"
                    >{{ alarm.alarmTypeVal ? alarm.alarmTypeVal : '---' }}</span
                  >
                </div>
                <div
                  v-else-if="field.name === 'modifiedTime'"
                  @click="opensummary(alarm.id)"
                  style="min-width:150px"
                >
                  <timer
                    class="alarm-timer p10"
                    :time="alarm.modifiedTime"
                    :title="alarm.modifiedTime | formatDate()"
                    v-tippy="{
                      html: '#timer_popover_' + alarm.id,
                      distance: 0,
                      interactive: true,
                      theme: 'light',
                      animation: 'scale',
                      arrow: true,
                    }"
                  >
                  </timer>
                  <div :id="'timer_popover_' + alarm.id" class="hide">
                    <div
                      style="font-size: 12px;letter-spacing: 0.5px;color: #666666;"
                    >
                      {{ alarm.modifiedTime | formatDate() }}
                    </div>
                  </div>
                </div>
                <div
                  v-else-if="
                    field.name === 'previousSeverity' && alarm.previousSeverity
                  "
                  class="q-item-label self-center f10 secondary-color"
                >
                  <div
                    class="q-item-label uppercase"
                    v-bind:class="
                      alarm.previousSeverity ? { severityTag: true } : ''
                    "
                    v-bind:style="{
                      'background-color': getAlarmSeverity(
                        alarm.previousSeverity.id
                      ).color,
                    }"
                  >
                    {{
                      alarm.previousSeverity
                        ? getAlarmSeverity(alarm.previousSeverity.id)
                            .displayName
                        : '---'
                    }}
                  </div>
                </div>
                <div
                  v-else
                  style="width: 200px;max-width: 200px;white-space: nowrap;text-overflow: ellipsis;overflow: hidden;"
                >
                  <span
                    class="q-item-label"
                    style="font-size:13px;letter-spacing: 0.4px;"
                    >{{ getColumnDisplayValue(field, alarm) }}</span
                  >
                  <div
                    v-if="
                      field.name === 'resource' &&
                        alarm.resource &&
                        alarm.resource.space &&
                        alarm.resource.space.id > 0
                    "
                    style="padding-top: 5px;"
                  >
                    <img
                      src="~statics/space/space-resource.svg"
                      style="height:11px; width:12px; margin-right: 3px;"
                      class="flLeft"
                    />
                    <span
                      class="flLeft q-item-label ellipsis"
                      v-tippy
                      small
                      data-position="bottom"
                      :title="alarm.resource.space.name"
                      style="max-width:85%;font-size: 10px;"
                      >{{ alarm.resource.space.name }}</span
                    >
                  </div>
                </div>
              </td>
              <td v-if="canShowColumn('noOfEvents')">
                <div style="width:55px;" v-if="alarm.noOfEvents > 0">
                  <div class="flLeft">
                    <img
                      src="~statics/icons/event.svg"
                      style="width:15px;"
                      class="flLeft"
                    />
                  </div>
                  <span class="flLeft pL5">{{ alarm.noOfEvents }}</span>
                  <div style="clear:both;"></div>
                </div>
              </td>
              <td v-if="canShowColumn('acknowledgedBy')">
                <div
                  v-if="$hasPermission('alarm:ACKNOWLEDGE_ALARM')"
                  class="pull-left"
                  style="font-size: 13px;color: #383434; white-space: nowrap;"
                >
                  <q-btn
                    color="secondary"
                    class="uppercase fia-alert-btn"
                    small
                    outline
                    @click="
                      acknowledgeAlarm({
                        alarm: alarm,
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
                        <div class=" ackhover-row">
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
                      style="font-size: 14px;letter-spacing: 0.3px;  color: #333333;"
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
                      style="  font-size: 12px;letter-spacing: 0.4px;text-align: left;color: #8a8a8a; padding-top:5px;"
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
                  style="float: right;font-size: 20px;color: #d8d8d8;"
                >
                  <q-popover ref="moreactionspopover">
                    <q-list link class="no-border" v-if="isActiveAlarm(alarm)">
                      <q-item>
                        <q-item-main
                          :label="$t('alarm.alarm.clear')"
                          @click="
                            updateAlarmStatus({
                              alarm: alarm,
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
                            deleteAlarm([alarm.id]),
                              $refs.moreactionspopover[index].close()
                          "
                        />
                      </q-item>
                      <q-item v-if="alarm.woId > 0">
                        <q-item-main
                          :label="$t('alarm.alarm.view_workorder')"
                          @click="
                            viewAlarm(alarm.woId),
                              $refs.moreactionspopover[index].close()
                          "
                        />
                      </q-item>
                      <q-item v-else-if="$hasPermission('alarm:CREATE_WO')">
                        <q-item-main
                          :label="$t('alarm.alarm.create_workorder')"
                          @click="
                            createWoDialog([alarm.id]),
                              $refs.moreactionspopover[index].close()
                          "
                        />
                      </q-item>
                    </q-list>
                    <q-list link class="no-border" v-else>
                      <q-item>
                        <q-item-main
                          :label="$t('common._common.delete')"
                          @click="
                            deleteAlarm([alarm.id]),
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
          class="view-column-chooser fc-anamolies-chooser"
          @click="showColumnSettings = true"
        >
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 36%;right: 25%;"
          />
        </span>
      </div>
    </div>
    <div class="fc-column-view height100 border-border-none" v-else>
      <div class="row fc-column-view-title height100">
        <div
          class="col-4 fc-column-view-left height100"
          style="max-width: 23.5%; flex: 0 0 23.5%;"
        >
          <div class=" row fia-tabel-nocontent" v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div v-else>
            <div
              class="row fc-column-view-records height100"
              style="background: #fff;"
            >
              <div class="fc-anamalies-side-header flex-middle">
                <i
                  @click="back"
                  class="el-icon-back f18 pointer fc-black-color bold pR10"
                ></i>
                <el-dropdown
                  @command="openChild"
                  class="alarm-dp"
                  trigger="click"
                >
                  <span class="el-dropdown-link pointer">
                    {{ currentViewDetail.displayName
                    }}<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown" class="wo-dropdownmenu">
                    <el-dropdown-item
                      v-for="(view, idx) in getviewtype()"
                      :key="idx"
                      :command="view.name"
                      v-if="view.name !== currentViewDetail.name"
                    >
                      {{ view.displayName }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
              <div class="height100vh width100">
                <div class="height100 overflow-y-scroll pB150">
                  <table class="fc-list-view-table">
                    <tbody v-if="alarms.length">
                      <tr
                        @click="opensummary(alarm.id)"
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
                              :title="alarm.subject"
                            >
                              {{ alarm.subject }}
                            </div>
                            <div class="flex-middle pT5">
                              <div class="uppercase fc-id">#{{ alarm.id }}</div>
                              <div class="separator">|</div>
                              <div class="fc-black-13">
                                {{ alarm.resource.name }}
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
                                  color: getAlarmSeverity(alarm.severity.id)
                                    .color,
                                }"
                                aria-hidden="true"
                              ></i>
                            </span>
                            <span
                              class="q-item-label uppercase secondary-color"
                              style="font-size: 10px;letter-spacing: 0.7px;color: #333333;"
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
          </div>
        </div>

        <div
          class="col-8 fc-column-alarm-right new-alarm-summ-bg"
          style="max-width: 76.5%; flex: 0 0 76.5%;"
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
      moduleName="mlAnomalyAlarm"
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
import { getColumnConfig } from 'src/newapp/components/column-customization/columnConfigUtil.js'
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
          value: 'lastOccurredTime',
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
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketCategory')
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
    ...mapGetters(['getTicketCategory', 'getAlarmSeverity']),
    alarms() {
      return this.$store.state.newAlarm.alarms.filter(n => n)
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
      return this.$store.state.newAlarm.sorting
    },
    searchQuery() {
      return this.$store.state.newAlarm.quickSearchQuery
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
    page() {
      return this.$route.query.page || 1
    },
    columnConfig() {
      return getColumnConfig('mlAnomalyAlarm')
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
    getviewtype() {
      let group = this.$store.state.view.groupViews
      if (group) {
        let elem = [],
          types = []
        const len = group.length
        for (let itr = 0; itr < len; itr++) {
          elem.push(group[itr].views)
          types = types.concat(elem[itr])
        }
        return types
      }
    },
    nextPage() {
      console.log('nectPafe')
      if (!this.scrollDisabled) {
        this.subListLoading = true
        this.fetchingMore = true
        this.loadAlarms(true)
      }
    },
    ...mapActions({
      assignAlarmApi: 'newAlarm/assignAlarm',
      updateAlarmStatus: 'newAlarm/updateAlarmStatus',
      notifyAlarm: 'newAlarm/notifyAlarm',
      acknowledgeAlarm: 'newAlarm/acknowledgeAlarm',
      createWoFromAlarm: 'newAlarm/createWoFromAlarm',
      getRelatedWorkorderId: 'newAlarm/getRelatedWorkorderId',
      deleteAlarm: 'newAlarm/deleteAlarm',
    }),
    back() {
      let url = '/app/fa/anomalies/' + this.$route.params.viewname
      this.$router.push({ path: url, query: this.$route.query })
    },
    openChild(viewNames) {
      let self = this
      let queryObj = {
        moduleName: 'mlAnomalyAlarm',
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
        .dispatch('newAlarm/fetchAlarms', queryObj)
        .then(function(response) {
          if (self.$store.state.newAlarm.alarms.length > 0) {
            self.$router.push({
              path:
                '/app/fa/anomalies/' +
                viewNames +
                '/' +
                self.$store.state.newAlarm.alarms[0].id +
                '/summary',
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
        }
      }
      if (data.assignedTo) {
        fields.assignedTo = data.assignedTo
      }
      if (data.assignmentGroup) {
        fields.assignmentGroup = data.assignmentGroup
      }
      self.$store
        .dispatch('newAlarm/createWoFromAlarm', {
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
        moduleName: 'mlAnomalyAlarm',
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
        .dispatch('newAlarm/fetchAlarms', queryObj)
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
      let url =
        'v2/mlAnomalyAlarm/view/' + queryObj.viewname + '?fetchCount=true'
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
    openFromTablesummary(row, col) {
      if (col.label === 'ID' || col.label === 'message') {
        this.$router.push({
          path:
            '/app/fa/anomalies/' +
            this.$route.params.viewname +
            '/' +
            row.id +
            'summary',
          query: this.$route.query,
        })
      }
    },
    opensummary(id) {
      this.$router.push({
        path:
          '/app/fa/anomalies/' +
          this.$route.params.viewname +
          '/' +
          id +
          '/summary',
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
<style lang="scss">
.fc-anamalies-page {
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
  .fc-anamalies-side-header {
    background: #ffffff;
    width: 100%;
    height: 50px;
    border-left: 1px solid #f2f5f6;
    position: sticky;
    top: 0;
    z-index: 200;
    padding: 18px 15px;
  }
  .fc-anamolies-chooser {
    height: 44px !important;
    border-bottom: 1px solid #f2f5f6;
  }
}
</style>
