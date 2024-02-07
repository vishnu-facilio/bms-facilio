<template>
  <div class="layout container">
    <div
      v-if="openAlarmId === -1"
      class="pL15 pR15 height100 scrollable mT15"
      style="padding-bottom:0px"
    >
      <div
        class="row full-layout-white fc-border-1 alarm-table scrollbar-style"
        style="padding-bottom:100px;overflow-x:scroll;display: block;"
      >
        <table class="fc-list-view-table">
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
              <div :class="{ 'text-center': field.name === 'modifiedTime' }">
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
                <div class="fia-tabel-nocontent">
                  {{ $t('alarm.alarm.no_alarms_found') }}
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
                    <span
                      class="uppercase ellipsis"
                      style="color:rgb(24, 178, 164);font-weight: 400;"
                      >#{{ alarm.serialNumber }}</span
                    >
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
                    <span style="">{{ alarm.modifiedTime | fromNow }}</span>
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
                  ></timer>
                  <div :id="'timer_popover_' + alarm.id" class="hide">
                    <div
                      style="font-size: 12px;letter-spacing: 0.5px;color: #666666;"
                    >
                      {{ alarm.modifiedTime | formatDate() }}
                    </div>
                  </div>
                </div>
                <div
                  v-else-if="field.name === 'previousSeverity'"
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
                      field.field.name === 'resource' &&
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
                        isAcknowledged: true,
                        acknowledgedBy: $account.user,
                      })
                    "
                    v-if="!alarm.isAcknowledged && isActiveAlarm(alarm)"
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
                              severity: 'Clear',
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
                      <q-item
                        ><q-item-main
                          :label="$t('common._common.delete')"
                          @click="
                            deleteAlarm([alarm.id]),
                              $refs.moreactionspopover[index].close()
                          "
                      /></q-item>
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
        <span class="view-column-chooser" @click="showColumnSettings = true">
          <img
            src="~assets/column-setting.svg"
            style="text-align: center; position: absolute; top: 35%;right: 29%;"
          />
        </span>
      </div>
    </div>
    <div class="fc-column-view height100 border-border-none" v-else>
      <div class="row fc-column-view-title height100">
        <div
          class="col-4 fc-column-view-left height100"
          style="max-width: 26.3333%; flex: 0 0 26.3333%;"
        >
          <div class="row container col-header pL25">
            <div class="col-12">
              <div class="pull-left">
                <el-dropdown
                  @command="$router.push({ path: '/app/fa/faults/' + $event })"
                >
                  <span class="el-dropdown-link pointer">
                    {{ currentViewDetail.displayName
                    }}<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown" class="wo-dropdownmenu">
                    <el-dropdown-item
                      :command="view.name"
                      v-for="(view, idx) in views"
                      :key="idx"
                      v-if="view.name !== currentViewDetail.name"
                    >
                      {{ view.displayName }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </div>
          </div>
          <div class=" row fia-tabel-nocontent" v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div v-else>
            <div class="row fc-column-view-records height100">
              <table
                class="fc-list-view-table"
                style="height: calc( 100vh - 100px); overflow-y: scroll; display: block; padding-bottom: 60px;"
              >
                <tbody v-if="alarms.length">
                  <tr
                    @click="opensummary(alarm.id)"
                    class="tablerow"
                    v-for="(alarm, index) in alarms"
                    :key="index"
                    v-bind:class="{ active: openAlarmId === alarm.id }"
                  >
                    <td class="text-left width90 pL5">
                      <div class="q-item-main q-item-section pL20">
                        <div class="mtb5 space-secondary-color f12">
                          <span class="uppercase fc-id"
                            >#{{ alarm.serialNumber }}</span
                          >
                        </div>
                        <div
                          class="fw5 workRequest-heading f14"
                          v-tippy
                          :title="alarm.Subject"
                        >
                          {{ alarm.subject }}
                        </div>
                        <div class="fc-grey2-text12 pT10">
                          {{ alarm.modifiedTime | fromNow }}
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

        <div
          class="col-8 fc-column-alarm-right fc-alarm-right-bg"
          style="max-width: 73.6667%; flex: 0 0 73.6667%;"
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
      return this.$store.state.alarm.alarms
    },
    openAlarmId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
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
          self.$dialog.notify('Workorder created successfully!')
          self.$refs['createWOModel'].close()
        })
      console.log('fields', fields)
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
          '/app/fa/faults/' + this.$route.params.viewname + '/summary/' + id,
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
.column1 {
  padding-left: 35px !important;
}
.severityTag {
  border-radius: 38px;
  font-size: 10px !important;
  font-weight: bold !important;
  letter-spacing: 0.8px;
  text-align: center;
  color: #ffffff !important;
  padding: 4px 10px;
  width: 72px;
  margin-bottom: 6px;
}
.fia-header-chip {
  height: 14px;
  min-height: 13px;
  width: 35px;
  font-size: 12px;
  position: relative;
  left: 21px;
  top: -9px;
  background: #2f2e49 !important;
  border-radius: 7px;
}

.fia-layout {
  padding: 1rem;
  padding-top: 0px;
  padding-left: 2rem;
  padding-right: 2rem;
  padding-bottom: 5rem;
}

i.fa.fa-sort-desc.fia-icon-downarrow {
  position: relative;
  left: 7px;
  bottom: 2px;
  font-size: 13px;
}

.row.container.fia-header {
  padding: 10px;
  padding-bottom: 15px;
  padding-top: 10px;
}

.fia-alert-header {
  padding: 10px;
  padding-bottom: 15px;
}

.row.fia-tabel-header {
  background-color: #f1f3f5;
  font-size: 11px;
  letter-spacing: 1px;
  color: #717b85;
}

.fia-table-active-row {
  border-bottom: 2px solid #f8f9fd;
  background-color: white;
  padding-left: 1rem;
  padding-right: 1rem;
}

.fia-th,
.fia-td {
  padding: 10px;
  padding-top: 15px;
  padding-bottom: 15px;
}
.fia-td-f {
  padding: 6px !important;
  padding-left: 25px !important;
  padding-right: 2px !important ;
}
.fia-td-padding div.q-item.q-item-division.relative-position {
  padding: 0px !important  ;
  font-size: 13px;
  color: #252525;
  font-weight: 400;
}
.fia-td-padding div.q-item-section {
  margin: 0px !important ;
}
.fia-table-row {
  background-color: white;
  border-bottom: 2px solid #f8f9fd;
  border-left: 2px solid transparent;
}
.fia-table-row.unread {
  border-left: 2px solid red;
}
.fia-alert-row {
  background-color: white;
  border-radius: 6px;
}
.fia-alert-row-y {
  border: 1px solid rgba(248, 104, 49, 0.47);
  box-shadow: 0px 2px 9px 1px rgba(255, 112, 0, 0.23),
    1px 2px 14px 11px rgba(244, 157, 54, 0.06),
    0 4px 20px 3px rgba(0, 0, 0, 0.12);
}
.fia-alert-row:hover .fia-td-f i.fa.fa-sort-desc.wl-icon-downarrow {
  color: black;
}
.fia-alert-row:hover {
  cursor: pointer;
}
.fia-logo-f-normal img {
  text-align: center;
  margin: 0 auto;
  padding-left: 15px;
}
.fia-logo-f img {
  padding-left: 15px;
}
.row.fia-table-row:hover {
  background-color: #fafbfc !important;
  cursor: pointer;
}
.fia-td-f i.fa.fa-sort-desc.wl-icon-downarrow {
  color: transparent;
}
.fia-active-row .row.fia-table-row:hover {
  background-color: white;
  cursor: pointer;
}

.fia-active-row {
  margin-left: -15px;
  margin-right: -15px;
  z-index: 100;
  position: relative;
}

.fia-postion {
  position: relative;
}
.fia-postion-f {
  position: relative;
  margin-bottom: 20px;
}

button.q-btn.row.inline.flex-center.q-focusable.q-hoverable.relative-position.uppercase.fia-alert-btn.q-btn-rectangle.q-btn-small.bg-primary.text-white {
  background-color: #f86968 !important;
  border: 1px solid #da4959;
  color: white !important;
}

.row.fia-table-alert-row {
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
  max-height: 70px;
  position: relative;
}

.fia-alert-gif {
  background: #f13113;
  padding: 23px;
  margin-left: -7px;
  margin-right: 7px;
  margin-top: -15px;
  padding-left: 10px;
  padding-right: 10px;
  margin-bottom: -15px;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
  max-width: 7.3% !important;
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
}

.fia-shadow {
  box-shadow: 8px 11px 20px 1px rgba(255, 17, 0, 0.11),
    7px 4px 14px 4px rgba(244, 67, 54, 0.06), 0 4px 20px 3px rgba(0, 0, 0, 0.12);
}

.fia-tabel-content span.q-item-label,
.fia-alert span.q-item-label {
  font-size: 13px;
  color: #252525;
  font-weight: 400;
}

.fia-tabel-content .picklist-downarrow,
.fia-alert .picklist-downarrow {
  font-size: 13px;
  padding-left: 3px;
  color: transparent;
}

.fia-postion:hover .picklist-downarrow,
.fia-alert:hover .picklist-downarrow {
  color: #000;
}

.fia-tabel-content div.q-item-main.q-item-section,
.fia-alert div.q-item-main.q-item-section {
  margin-left: 0px;
}

.fia-tabel-content div.q-item.q-item-division.relative-position {
  padding: 0px;
}

.fia-tabel-nocontent {
  text-align: center;
  padding: 30px;
  font-size: 14px;
  background: white;
}

.fia-active-row,
.fia-normal-row {
  -moz-transition: 0.2s ease-in-out;
  -o-transition: 0.2s ease-in-out;
  -webkit-transition: 0.2s ease-in-out;
  transition: 0.2s ease-in-out;
}

.fia-active-row .fia-table-alert-row {
  border: none;
  background: white;
  border-radius: 0px;
  -webkit-border-radius: 0px;
  -moz-border-radius: 0px;
  -o-border-radius: 0px;
  padding-bottom: 15px;
}
.timer-col {
  margin: 0 auto;
  padding: 20px;
}
.fia-ack {
  float: left;
}
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
.ackhover {
  background: #fff;
  opacity: 1;
}
.ackhover-row {
  letter-spacing: 0.7px;
  font-size: 12px;
  text-align: left;
}
.ackhover-row .hover-row {
  padding: 5px;
}

.alarm-table .view-column-chooser {
  right: 18px;
  width: 40px;
  height: 40px;
}

.alarm-table .fc-list-view-table td {
  padding: 0.8rem;
}
</style>
