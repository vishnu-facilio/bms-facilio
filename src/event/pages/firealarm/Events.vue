<template>
  <div class="height100">
    <div class="fc-list-view p10 pT0 height100 mT10" v-if="openEventId === -1">
      <div
        class="row full-layout-white scrollable130-y100 fc-border-1"
        style="margin-top: 10px; display: block"
      >
        <table class="fc-list-view-table">
          <thead>
            <tr>
              <th style="width: 15px"></th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('alarm.alarm.message') }}
              </th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('alarm.alarm.source') }}
              </th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('alarm.alarm.condition') }}
              </th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('maintenance.wr_list.space_asset') }}
              </th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('alarm.alarm.severity') }}
              </th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('maintenance._workorder.created_time') }}
              </th>
              <th class="text-left" style="text-transform: uppercase">
                {{ $t('maintenance._workorder.status') }}
              </th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr class="nowotd" v-if="!events.length">
              <td colspan="100%">
                <div class="row container">
                  <div class="justify-center nowo">
                    <div>
                      <img class="nowo-logo" src="~assets/no_workorder.jpg" />
                      <div class="q-item-label nowo-label">
                        {{ $t('alarm.alarm.no_event_here') }}
                      </div>
                      <div
                        class="q-item-sublabel nowo-sublabel"
                        style="width: 24%"
                      >
                        {{ $t('alarm.alarm.no_event_in_view') }}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
            </tr>

            <tr
              class="tablerow"
              v-for="event in events"
              :key="event.id"
              v-else
              v-bind:class="{ active: openEventId === events.id }"
            >
              <td style="width: 15px"></td>
              <td @click="opensummary(event.id)" class="text-left ellipsis">
                <div class="q-item-division relative-position">
                  <div class="q-item-main q-item-section">
                    <div
                      class="q-item-label ellipsis primary-field bold"
                      style="max-width: 250px"
                    >
                      {{ event.eventMessage }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="text-left secondary-color" style="min-width: 100px">
                <div>
                  <span class="q-item-label ellipsis">{{
                    event.source ? event.source : '---'
                  }}</span>
                </div>
              </td>
              <td class="text-left">
                <div class="q-item-division relative-position">
                  <div v-if="event.condition">
                    <span
                      class="q-item-label ellipsis f13"
                      style="max-width: 200px; display: block"
                      >{{ event.condition }}</span
                    >
                  </div>
                  <div v-else>
                    <span class="q-item-label secondary-color color-d"
                      >---</span
                    >
                  </div>
                </div>
              </td>
              <td class="text-left ellipsis" style="min-width: 100px">
                <div class="q-item-division relative-position">
                  <div v-if="event.resource && event.resource.name">
                    <span class="q-item-label ellipsis f13">{{
                      event.resource.name
                    }}</span>
                  </div>
                  <div v-else>
                    <span class="q-item-label secondary-color color-d"
                      >---</span
                    >
                  </div>
                </div>
              </td>
              <td
                class="text-left secondary-color text-overflow-ellipsis"
                style="min-width: 100px"
              >
                <div class="q-item-label">
                  <i
                    class="fa fa-circle prioritytag"
                    v-if="event.severity"
                    v-bind:style="{
                      color: getAlarmSeverityByName(event.severity).color,
                    }"
                    aria-hidden="true"
                  ></i>
                  <span class="q-item-label uppercase secondary-color f13">{{
                    event.severity
                      ? getAlarmSeverityByName(event.severity).displayName
                      : '---'
                  }}</span>
                </div>
              </td>
              <td class="text-left">
                <div class="q-item-division relative-position">
                  <div v-if="event.createdTime">
                    <div v-if="!event.createdTime">
                      <div class="q-item-sublabel wl-id color-2">---</div>
                    </div>
                    <div v-else>
                      <div class="q-item-sublabel wl-id pointer f13">
                        {{ event.createdTime | fromNow }}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
              <td
                class="text-left secondary-color text-overflow-ellipsis"
                style="min-width: 100px"
              >
                <div>
                  <span class="q-item-label" v-if="event.eventState">
                    {{ getStateVal(event.eventState) }}</span
                  >
                  <span v-else class="color-d"> --- </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="fc-column-view height100" v-else>
      <div class="row fc-column-view-title height100">
        <div class="col-4 fc-column-view-left height100">
          <div
            v-show="false"
            class="row fia-tabel-header fc-border-1"
            style="padding: 18px; padding-left: 14px; background-color: #f1f3f5"
          >
            <div class="col-12 secondary-color f11">
              <div class="pull-left">
                <div class="q-item-label inline row uppercase" style="">
                  <span style="left: 10px; position: relative; top: 3px">{{
                    $t('header.evetns')
                  }}</span>
                </div>
              </div>
              <div class="pull-right row">
                <div style="padding-right: 30px">
                  {{ $t('alarm.recent')
                  }}<i
                    class="fa fa-sort-desc fia-icon-downarrow"
                    aria-hidden="true"
                  ></i>
                </div>
                <div style="padding-right: 20px">|</div>
                <div style="padding-right: 30px">
                  <i class="fa fa-filter" aria-hidden="true"></i
                  ><i
                    class="fa fa-sort-desc fia-icon-downarrow"
                    aria-hidden="true"
                  ></i>
                </div>
                <div><i class="fa fa-search" aria-hidden="true"></i></div>
              </div>
            </div>
          </div>
          <div class="row fia-tabel-nocontent" v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div v-else class="">
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
                <tbody v-if="events.length">
                  <tr
                    class="tablerow"
                    v-for="event in events"
                    :key="event.id"
                    v-bind:class="{ active: openEventId === event.id }"
                  >
                    <td style="width: 5%"></td>
                    <td
                      @click="opensummary(event.id)"
                      class="text-left width90 pL5"
                    >
                      <div class="q-item-main q-item-section">
                        <div class="q-item-label pull-left">
                          {{ event.eventMessage }}
                        </div>
                        <div
                          style="margin-top: 0px"
                          class="q-item-sublabel wl-id pointer pull-right"
                        >
                          {{ event.createdTime | fromNow }}
                        </div>
                      </div>
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

    <new-event-layout v-if="showCreateNewDialog"></new-event-layout>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import NewEventLayout from 'event/pages/firealarm/NewEvent'

import { Ripple } from 'quasar'

export default {
  data() {
    return {
      loading: false,
      summoryview: false,
      selectedEvents: [],
      selectAll: false,
      newViewName: '',
      sortConfig: {
        orderBy: {
          label: this.$t('wr_list.created_on'),
          value: 'createdTime',
        },
        orderType: 'desc',
        list: [
          {
            label: this.$t('wr_list.created_on'),
            value: 'createdTime',
          },
          {
            label: this.$t('wr_list.due_by_time'),
            value: 'dueDate',
          },
          {
            label: this.$t('wr_list.subject'),
            value: 'subject',
          },
        ],
      },
      showQuickSearch: false,
      quickSearchQuery: null,
      states: {
        1: 'Ready',
        2: 'Ignored',
        3: 'Alarm Created',
        4: 'Alarm Updated',
      },
      page: 1,
      perPage: 30,
      allEventsLoaded: false,
    }
  },
  title() {},
  directives: {
    Ripple,
  },
  computed: {
    ...mapGetters(['getAlarmSeverityByName']),
    events() {
      return this.$store.state.event.events
    },
    openEventId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    canShowFilteredFields() {
      let canShow = false
      if (this.filters) {
        let self = this
        Object.keys(this.filters).forEach(function(key) {
          if (self.filters[key].value.length) {
            canShow = true
          }
        })
      }
      return canShow
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'today'
    },
    showCreateNewDialog() {
      if (this.$route.query.create) {
        return true
      }
      return false
    },
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  mounted() {
    this.loadEvents()
  },
  methods: {
    toggleSelection(woId) {
      let idx = this.selectedEvents.indexOf(woId)
      if (idx === -1) {
        this.selectedEvents.push(woId)
      } else {
        this.selectedEvents.splice(idx, 1)
      }
    },
    resetSelectAll() {
      this.selectedEvents = []
      this.selectAll = false
    },
    loadEvents() {
      let self = this
      let queryObj = {
        viewname: this.currentView,
      }
      self.loading = true
      self.$store
        .dispatch('event/fetchEvents', queryObj)
        .then(() => {
          self.loading = false
          self.fetchEvent()
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
      self.setTitle(self.$options.filters.viewName(self.currentView))
    },
    opensummary(id) {
      this.$router.push({
        path:
          '/app/fa/events/' + this.$route.params.viewname + '/summary/' + id,
      })
    },
    openEvent(id) {
      this.summoryview = true
      this.$router.push({
        path:
          '/app/fa/events/' + this.$route.params.viewname + '/summary/' + id,
      })
    },
    clearFiters() {
      this.$router.replace({ path: this.$route.path, query: {} })
    },
    removeFilterField(fl, index) {
      fl.value.splice(index, 1)
      this.$router.replace({
        path: this.$route.path,
        query: { search: JSON.stringify(this.filters) },
      })
    },
    showQuickSearchBox() {
      this.showQuickSearch = true
    },
    hideQuickSearchBox() {
      this.showQuickSearch = false
      if (this.quickSearchQuery && this.quickSearchQuery.trim().length > 0) {
        this.quickSearchQuery = null
        this.loadEvents()
      }
    },
    quickSearch() {
      if (this.quickSearchQuery && this.quickSearchQuery.trim().length > 0) {
        this.loadEvents()
      }
    },
    openCreateDialog() {
      this.$refs.createNewEventModel.open()
    },
    closeCreateDialog() {
      this.$refs.createNewEventModel.close()
    },
    getStateVal(state) {
      return this.states[state]
    },
    openalarm(id) {
      if (id) {
        this.$router.push({ path: '/app/fa/faults/summary/' + id })
      } else {
        return false
      }
    },
    fetchEvent() {
      if (this.openEventId > 0) {
        let event = this.$store.getters['event/getEventById'](this.openEventId)
        if (!event) {
          this.$store.dispatch('event/summaryEvents', { id: this.openEventId })
        }
      }
    },
  },
  watch: {
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadEvents()
      }
    },
    openEventId: function() {
      if (this.openEventId === -1) {
        this.summoryview = false
      } else {
        this.fetchEvent()
      }
    },
    filters() {
      this.loadEvents()
    },
    searchQuery() {
      this.quickSearchQuery = this.searchQuery
      this.loadEvents()
    },
    selectAll(val) {
      if (val) {
        this.selectedEvents = this.events.map(event => event.id)
      } else {
        if (this.selectedEvents.length === this.events.length) {
          this.selectedEvents = []
        }
      }
    },
    selectedEvents: function() {
      if (this.selectedEvents.length !== this.events.length) {
        this.selectAll = false
      }
    },
  },
  components: {
    NewEventLayout,
  },
  filters: {
    viewName: function(name) {
      if (name === 'today') {
        return 'Today Events'
      } else if (name === 'yesterday') {
        return 'Yesterday Events'
      } else if (name === 'thisweek') {
        return 'This Week Events'
      } else if (name === 'lastweek') {
        return 'Last Week Events'
      } else if (name === 'all') {
        return 'All Events'
      }
      return 'Events'
    },
  },
}
</script>
<style>
.saveasview {
  padding: 20px;
}
.saveasviewheader {
  font-size: 22px;
}
.prioritytag {
  font-size: 9px;
  padding-right: 6px;
  position: relative;
  bottom: 1px;
}
.width5 {
  width: 5px;
}
.width90 {
  width: 90%;
}
.tablerow {
  cursor: pointer;
}
.tablerow.active {
  background: #f3f4f7 !important;
  border-left: 3px solid #ff2d81 !important;
  border-right: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-top: 1px solid rgba(232, 232, 232, 0.35) !important;
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
.tablerow.selected {
  background: #e2f1ef;
  border-left: 3px solid #28b2a4;
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
i.fa.fa-sort-desc.wl-icon-downarrow {
  position: relative;
  left: 7px;
  bottom: 2px;
  font-size: 13px;
}
td.filter-td {
  background: #f3f2f8;
  overflow-x: scroll;
}
td.filter-td
  div.q-chip.row.inline.items-center.text-black.uppercase.square.text-white.bg-white {
  color: #25243e !important;
  font-size: 13px;
  border: 1px solid rgba(37, 36, 62, 0.11);
  cursor: pointer;
}
.filter-order-last i.on-right.q-icon.material-icons {
  font-size: 14px;
  color: #f00;
}
.q-chip.row.inline.items-center.text-black.order-last.filter-order-last.uppercase.relative-position.square {
  font-weight: 500;
  font-size: 12px;
}
.filter-td .row .q-chip {
  margin: 0 2px;
}
.filter-td .row .q-chip.filter-value i.on-right.q-icon.material-icons {
  font-size: 14px;
  color: #f00;
  display: none;
  margin-left: 5px;
}
.filter-td .row .q-chip.filter-value:hover i.on-right.q-icon.material-icons {
  display: inline-block;
}
.nowo {
  text-align: center;
  width: 100%;
  position: relative;
}
.nowo-btn i.q-icon.material-icons.on-left {
  font-size: 10px;
  margin-right: 2px;
}
.nowo-btn {
  font-size: 10px;
  font-weight: 500;
  margin-top: 15px;
  background: #fd4b92 !important;
}
.nowo-logo {
  height: 65px;
  width: 65px;
}
.wo-check-box i.q-checkbox-unchecked.cursor-pointer.q-icon.material-icons {
  color: #eee !important;
}
.wo-list-left {
  background: #fff;
  border: 1px solid #e8e8e8;
}
.wl-table-header {
  margin-right: -1px;
}
.wor {
  box-shadow: -5px 0 10px rgba(0, 0, 0, 0.05);
}
.nowotd td {
  border: none !important;
  padding-top: 5%;
}
.model-container {
  padding-left: 25px;
  padding-right: 25px;
  padding-top: 15px;
}
.model-button {
  background-color: #34b3a7;
  color: #feffff;
  border: 1px solid #e7e7e7;
  outline: none;
  font-size: 14px;
  padding-left: 10px;
  padding-right: 10px;
  padding-top: 10px;
  padding-bottom: 10px;
  font-weight: 500;
  width: 100%;
}
.model-button:hover {
  color: #feffff;
  outline: none;
  border-color: #34b3a7;
}
.model-button:before,
.model-button:after {
  outline: none;
}
.model-button-cancel {
  background-color: #f7f8fa;
  color: #dadbdd;
  border: 0;
  outline: none;
  font-size: 10px;
  padding-left: 10px;
  padding-right: 10px;
  padding-top: 10px;
  padding-bottom: 10px;
  width: 100%;
}
.model-button-cancel:hover {
  color: #dadbdd;
  outline: none;
}
.model-button-cancel:before,
.model-button:after {
  outline: none;
}
.f-select {
  font-size: 14px;
}
div.f-select label {
  color: #757575;
}
div.f-select span.selected-tag {
  color: #333 !important;
  font-size: 13px;
}
div.f-select .v-select .open-indicator {
  color: rgba(0, 0, 0, 0.57);
}
.model-footer {
  padding: 25px;
}
.v-select .dropdown-toggle {
  border-bottom: 1px solid rgba(60, 60, 60, 0.14) !important;
}
.closeDilaogicon i.q-item-icon.q-icon.material-icons {
  font-size: 15px;
}
</style>
