<template>
  <div style="height: auto; overflow-y: scroll;" class="fc-v1-alarm-overview">
    <div v-if="alarmOverviewLoading" class="flex-middle fc-empty-white">
      <spinner :show="alarmOverviewLoading" size="80"></spinner>
    </div>
    <div
      v-else-if="alarm && occurrence"
      class="fc__layout__align fc__asset__main__header pT20 pB20 pL20 pR20 position-relative"
      style="width: auto; align-items: center !important; border-bottom: none;"
    >
      <div v-if="alarm">
        <div class="wos-id" title="Back">
          <span class="fc-id">#{{ alarm.id }}</span>
        </div>
        <div class="fc-black-18 fw3 flex-middle max-width550px">
          <div class="max-width550px fw4 flex-middle">
            {{ alarm.subject }}
          </div>
          <div v-if="alarm.severity" class="flex-middle mT10 pL10">
            <div
              class="uppercase secondary-color summaryseverityTag f11"
              v-bind:style="{
                'background-color': getAlarmColor(alarm),
              }"
            >
              {{ getAlarmDisplayName(alarm) }}
            </div>
          </div>
        </div>
      </div>
      <div class="display-flex mR10 agent-alarm-summary-btn">
        <div class="flex-middle z-10 alarm-timer" v-if="!alarmOverviewLoading">
          <div class="fc-black-12">Duration:</div>
          <timer
            v-if="
              occurrence &&
                occurrence.createdTime &&
                occurrence.clearedTime === -1
            "
            class="fc-red-new bold f14 p10 alarm-timer-duration"
            :time="occurrence.createdTime"
            :twoDigits="true"
            :title="occurrence.createdTime | formatDate()"
          ></timer>
          <timer
            v-else
            class="fc-red-new bold f14 p10 alarm-cleared-duration"
            :time="occurrence.clearedTime - occurrence.createdTime"
            :staticTime="true"
            :twoDigits="true"
            :title="
              (occurrence.clearedTime - occurrence.createdTime) | formatDate()
            "
            v-tippy="{
              distance: 0,
              interactive: true,
              theme: 'light',
              animation: 'scale',
              arrow: true,
            }"
          ></timer>
        </div>
      </div>
    </div>
    <page
      v-if="alarm"
      :key="alarm.id"
      :module="'agentAlarm'"
      :notesModuleName="notesModuleName"
      :id="alarm.id"
      :details="{ alarm: alarm, occurrence: occurrence, id: alarm.id }"
      :primaryFields="primaryFields"
    ></page>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Page from '@/page/PageBuilder'
import Timer from '@/Timer'
import NewAlarmMixin from '@/mixins/NewAlarmMixin'
import { isEmpty } from '@facilio/utils/validation'
import { mapActions, mapState, mapGetters } from 'vuex'

export default {
  components: { Page, Timer },
  mixins: [NewAlarmMixin],
  created() {
    this.init()
  },
  props: ['viewname', 'moduleName'],
  data() {
    return {
      notesModuleName: 'basealarmnotes',
      currentModuleName: this.moduleName,
      alarmOverviewLoading: false,
      primaryFields: [],
      currentAlarm: null,
    }
  },
  computed: {
    id() {
      let paramId = this.$getProperty(this.$route, 'params.id')
      return !isEmpty(paramId) ? parseInt(paramId) : ''
    },
    alarm() {
      return this.currentAlarm
    },
  },
  watch: {
    id: {
      handler() {
        this.loadRecords()
      },
    },
    occurrence: function() {
      let currentAlarmId = this.$getProperty(this, 'currentAlarm.id')
      this.getOccurrenceFromId(currentAlarmId)
      this.getEventsFromId(currentAlarmId)
    },
  },
  methods: {
    async init() {
      await this.loadRecords()
      let currentAlarmId = this.$getProperty(this, 'currentAlarm.id')
      await Promise.all([
        this.getOccurrenceFromId(currentAlarmId),
        this.getEventsFromId(currentAlarmId),
      ])
    },
    async loadRecords(force = true) {
      try {
        let { currentModuleName } = this
        let { agentAlarm } = await API.fetchRecord(
          currentModuleName,
          {
            id: this.id,
          },
          { force }
        )
        this.currentAlarm = agentAlarm
        let { alarmoccurrence } = await API.fetchRecord(
          'alarmoccurrence',
          {
            id: this.$getProperty(agentAlarm, 'lastOccurrenceId'),
          },
          { force }
        )
        this.occurrence = alarmoccurrence
      } catch (errorMsg) {
        this.$message.error(errorMsg.message)
      }
    },
  },
}
</script>
<style scoped>
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.asset-details .asset-id {
  font-size: 12px;
  color: #39b2c2;
}
.asset-details .asset-name {
  font-size: 22px;
  color: #324056;
}
.asset-details .asset-space {
  font-size: 13px;
  color: #8ca1ad;
}
.alarm-timer {
  position: absolute;
  right: 20px;
  top: 70px;
}
</style>
