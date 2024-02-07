<template>
  <div class="layout container">
    <div v-if="openAlarmId === -1" class="fia-layout scrollable mT15">
      <div
        class="fc-border-1"
        v-infinite-scroll="loadMore"
        infinite-scroll-disabled="scrollDisabled"
        infinite-scroll-distance="100"
        infinite-scroll-immediate-check="false"
      >
        <div class="row fia-tabel-header f11">
          <div class="col-1 fia-th uppercase"></div>
          <div
            class="col-4 fia-th uppercase"
            style="max-width: 22%; margin-left: 4%"
          >
            MESSAGE
          </div>
          <div class="col-1 fia-th uppercase" style="margin-left: 2%">
            Category
          </div>
          <div class="col-2 fia-th uppercase" style="margin-left: 4%">
            Condition
          </div>
          <div
            class="col-2 fia-th uppercase text-center"
            style="margin-left: -1%"
          >
            time since report
          </div>
          <div
            class="col-1 fia-th uppercase text-center"
            style="margin-left: -1%"
          ></div>
          <div class="col-1 fia-th uppercase"></div>
        </div>
        <div class="fia-tabel-nocontent" v-if="loading">
          <spinner :show="loading"></spinner>
        </div>
        <div class="fia-tabel-nocontent" v-else-if="!alarms.length">
          <inline-svg
            src="svgs/emptystate/alarmEmpty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            {{ $t('alarm.alarm.no_alarms_found') }}
          </div>
        </div>
        <div class="fia-tabel-content" v-else>
          <div
            class="fia-postion"
            v-for="(alarm, index) in alarms"
            :key="index"
          >
            <div class="fia-normal-row">
              <div
                class="row fia-table-row fia-alarm-entry"
                :class="{ unread: alarm.unseen }"
              >
                <div
                  class="col-1 fia-td self-center text-left f10 secondary-color column1"
                >
                  <div class="q-item-label" v-if="alarm.severity">
                    <div
                      class="q-item-label uppercase severityTag"
                      v-bind:style="{
                        'background-color': getAlarmSeverity(alarm.severity.id)
                          .color,
                      }"
                    >
                      {{ getAlarmSeverity(alarm.severity.id).displayName }}
                    </div>
                  </div>
                </div>
                <div
                  class="col-4 fia-td self-center"
                  @click="opensummary(alarm.id)"
                  style="max-width: 22%; margin-left: 4%"
                >
                  <div class="q-item-main q-item-section">
                    <div
                      class="q-item-sublabel ellipsis"
                      style="margin-top: 5px; font-size: 12px"
                    >
                      <span
                        class="uppercase"
                        style="color: rgb(24, 178, 164); font-weight: 400"
                        >#{{ alarm.serialNumber }}</span
                      >
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
                      <span style="">{{ alarm.modifiedTime | fromNow }}</span>
                    </div>
                  </div>
                </div>
                <div
                  class="col-1 fia-td self-center f12"
                  style="cursor: pointer; margin-left: 2%"
                >
                  <span
                    class="q-item-label"
                    style="font-size: 13px; letter-spacing: 0.4px"
                    >{{ alarm.alarmTypeVal ? alarm.alarmTypeVal : '---' }}</span
                  >
                </div>
                <div
                  class="col-2 fia-td self-center f12"
                  style="cursor: pointer; margin-left: 4%"
                >
                  <span
                    class="q-item-label"
                    style="font-size: 13px; letter-spacing: 0.4px"
                    >{{ alarm.node ? alarm.node : '---' }}</span
                  >
                </div>
                <div
                  class="col-2 fia-td self-center secondary-color"
                  style="font-size: 12px; padding: 0px 25px; margin-left: -1%"
                  @click="opensummary(alarm.id)"
                >
                  <timer
                    class="alarm-timer p10"
                    :time="alarm.modifiedTime"
                  ></timer>
                </div>
                <div
                  class="col-1 fia-td self-center secondary-color"
                  style="margin-left: 2%"
                >
                  <div
                    class="pull-left"
                    style="
                      font-size: 13px;
                      color: #383434;
                      white-space: nowrap;
                      padding-top: 15px;
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
                          isAcknowledged: true,
                          acknowledgedBy: $account.user,
                        })
                      "
                      v-if="!alarm.isAcknowledged && isActiveAlarm(alarm)"
                      >Acknowledge</q-btn
                    >
                    <span
                      class="q-item-label"
                      v-else-if="!isActiveAlarm(alarm)"
                    ></span>
                    <span class="q-item-label f11" v-else>
                      <div id="contentpopup2" class="hide ackhover">
                        <div>
                          <div class="ackhover-row">
                            <div class="hover-row">Acknowledged By</div>
                            <div class="hover-row">
                              <span>
                                <user-avatar
                                  size="sm"
                                  :user="getUserName(alarm.acknowledgedBy.id)"
                                ></user-avatar>
                              </span>
                            </div>
                            <div class="hover-row">
                              {{ alarm.modifiedTime | fromNow }}
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
                          html: '#contentpopup2',
                          interactive: true,
                          reactive: true,
                          distance: 15,
                          theme: 'light',
                          animation: 'scale',
                        }"
                        >Acknowledged</span
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
                        {{ alarm.modifiedTime | fromNow }}
                      </div>
                    </span>
                  </div>
                </div>
                <div class="col-1 fia-td self-center secondary-color">
                  <q-icon
                    v-if="isActiveAlarm(alarm)"
                    slot="right"
                    name="more_vert"
                    style="float: right; font-size: 20px; color: #d8d8d8"
                  >
                    <q-popover ref="moreactionspopover">
                      <q-list link class="no-border">
                        <q-item>
                          <q-item-main
                            label="Clear"
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
                            label="Delete"
                            @click="
                              deleteAlarm([alarm.id]),
                                $refs.moreactionspopover[index].close()
                            "
                          />
                        </q-item>
                        <q-item v-if="alarm.isWoCreated">
                          <q-item-main
                            label="View Workorder"
                            @click="
                              viewAlarm(alarm.id),
                                $refs.moreactionspopover[index].close()
                            "
                          />
                        </q-item>
                        <q-item v-else-if="$hasPermission('alarm:CREATE_WO')">
                          <q-item-main
                            label="Create Workorder"
                            @click="
                              createWoDialog([alarm.id]),
                                $refs.moreactionspopover[index].close()
                            "
                          />
                        </q-item>
                      </q-list>
                    </q-popover>
                  </q-icon>
                </div>
              </div>
            </div>
          </div>
          <div class="fia-tabel-nocontent" v-if="fetchingMore">
            <spinner :show="fetchingMore" size="50"></spinner>
          </div>
        </div>
      </div>
    </div>
    <div class="fc-column-view height100" v-else>
      <div class="row fc-column-view-title height100">
        <div class="col-4 fc-column-view-left height100">
          <div class="row container col-header">
            <div class="col-12">
              <div class="pull-left">
                <div class="fc-list-view-name inline">
                  {{ $route.params.viewname | viewName }}
                </div>
              </div>
              <div class="pull-right fc-list-view-filter">
                <div class="pointer">
                  Recent<i
                    class="fa fa-sort-desc fia-icon-downarrow"
                    aria-hidden="true"
                  ></i>
                </div>
                <div class="op5">|</div>
                <div class="pointer">
                  <i class="fa fa-search" aria-hidden="true"></i>
                </div>
              </div>
            </div>
          </div>
          <div class="row fia-tabel-nocontent" v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div v-else>
            <div class="row fc-column-view-records height100">
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
                    @click="opensummary(alarm.id)"
                    class="tablerow"
                    v-for="(alarm, index) in alarms"
                    :key="index"
                    v-bind:class="{ active: openAlarmId === alarm.id }"
                  >
                    <td style="width: 5%"></td>
                    <td class="text-left width90 pL5">
                      <div class="q-item-main q-item-section">
                        <div
                          class="q-item-sublabel ellipsis mtb5 space-secondary-color f12"
                          style="margin-bottom: 2px"
                        >
                          <span
                            class="uppercase mtb5"
                            style="
                              font-size: 12px;
                              letter-spacing: 0.4px;
                              color: #18b2a4;
                            "
                            >#{{ alarm.serialNumber }}</span
                          >
                        </div>
                        <div
                          class="q-item-label fw5"
                          v-tippy
                          :title="alarm.Subject"
                          style="
                            font-size: 15px;
                            font-weight: 500;
                            letter-spacing: 0.3px;
                            color: #333333;
                          "
                        >
                          {{ alarm.subject }}
                        </div>
                        <div
                          class="q-item-label fw5"
                          style="
                            font-size: 12px;
                            letter-spacing: 0.4px;
                            color: #8a8a8a;
                            margin-top: 5px;
                            font-weight: normal;
                          "
                        >
                          {{ alarm.modifiedTime | fromNow }}
                        </div>
                      </div>
                    </td>
                    <td class="text-left" v-if="alarm.severity">
                      <span class="ellipsis">
                        <span class="q-item-label">
                          <i
                            class="fa fa-circle prioritytag"
                            v-bind:class="{
                              critical:
                                getAlarmSeverity(alarm.severity.id).severity ===
                                'Critical',
                              major:
                                getAlarmSeverity(alarm.severity.id).severity ===
                                'Major',
                              minor:
                                getAlarmSeverity(alarm.severity.id).severity ===
                                'Minor',
                              warning:
                                getAlarmSeverity(alarm.severity.id).severity ===
                                'Warning',
                              clear:
                                getAlarmSeverity(alarm.severity.id).severity ===
                                'Clear',
                              info:
                                getAlarmSeverity(alarm.severity.id).severity ===
                                'Info',
                            }"
                            aria-hidden="true"
                          ></i>
                        </span>
                        <span
                          class="q-item-label uppercase secondary-color"
                          style="
                            font-size: 12px;
                            letter-spacing: 0.7px;
                            color: #333333;
                            padding-left: 5px;
                          "
                          >{{
                            getAlarmSeverity(alarm.severity.id).severity
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

        <div class="col-8 fc-column-view-right">
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
  </div>
</template>

<script>
import UserAvatar from '@/avatar/User'
import Timer from '@/Timer'
import NewAlarm from 'pages/firealarm/alarms/NewAlarm'
import { mapActions, mapState, mapGetters } from 'vuex'
import AlarmModel from '@/AlarmModel'
import infiniteScroll from 'vue-infinite-scroll'
import { QList, QItem, QItemMain, QIcon, QPopover, QModal, QBtn } from 'quasar'
import { mapStateWithLogging } from 'store/utils/log-map-state'
export default {
  data() {
    return {
      createWoIds: [],
      loading: true,
      fetchingMore: false,
      sortConfig: {
        orderBy: {
          label: 'Date modified',
          value: 'modifiedTime',
        },
        orderType: 'desc',
        list: [
          {
            label: 'Created on',
            value: 'createdTime',
          },
          {
            label: 'Due by time',
            value: 'dueDate',
          },
          {
            label: 'Subject',
            value: 'subject',
          },
        ],
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
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
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
        console.log('openAlarmId ::::: ', this.$route.params.id)
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
    canLoadMore() {
      return this.$store.state.alarm.canLoadMore
    },
    scrollDisabled() {
      return this.loading || !this.canLoadMore || this.fetchingMore
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadAlarms()
      }
    },
    filters: function(newVal) {
      this.loadAlarms()
    },
    sorting: function(newVal) {
      this.sortConfig.orderBy = this.sorting.orderBy
      this.sortConfig.orderType = this.sorting.orderType
      this.loadAlarms()
    },
    searchQuery: function(newVal) {
      this.quickSearchQuery = this.searchQuery
      this.loadAlarms()
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
          name: this.getTicketCategory(data.category).displayName,
        }
      }
      if (data.priority) {
        fields.priority = {
          id: this.getTicketPriority(data.priority).id,
          name: this.getTicketPriority(data.priority).displayName,
        }
      }
      if (data.assignedTo) {
        fields.assignedTo = this.getUserName(data.assignedTo)
      }
      self.$store
        .dispatch('alarm/createWoFromAlarm', {
          id: this.createWoIds,
          fields: fields,
        })
        .then(function() {
          self.$dialog.notify('Workorder created successfully!')
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
    loadAlarms(loadMore) {
      let self = this
      this.page = loadMore ? this.page : 1
      let queryObj = {
        viewname: this.currentView,
        page: this.page,
        filters: this.filters,
        orderBy: this.sortConfig.orderBy.value,
        orderType: this.sortConfig.orderType,
        search: this.quickSearchQuery,
      }
      loadMore ? (self.fetchingMore = true) : (self.loading = true)
      self.$store
        .dispatch('alarm/fetchAlarms', queryObj)
        .then(function(response) {
          self.loading = false
          self.fetchingMore = false
          self.page++
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
      self.setTitle(self.$options.filters.viewName(self.currentView))
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
      this.$router.push({ path: '/app/fa/faults/summary/' + id })
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
      self.$router.replace({ path: '/app/wo/orders/summary/' + ticketId })
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
</style>
