<template>
  <div
    v-if="alarm"
    style="height: auto; overflow-y: scroll;"
    class="analmoly-overview-page"
  >
    <div
      class="fc__layout__align fc__asset__main__header pT20 pB20 pL20 pR20"
      style="width: auto; align-items: normal !important; border-bottom: none;"
    >
      <div class="fL">
        <div class="fc-id">#{{ alarm.id }}</div>
        <div>
          <div class="fc-black-22 textoverflow-height-ellipsis max-width700px">
            {{ alarm.subject ? alarm.subject : '---' }}
            <div
              v-if="alarm.severity"
              class="text-uppercase inline vertical-middle mL15"
            >
              <div
                class="uppercase secondary-color summaryseverityTag f11"
                v-bind:style="{
                  'background-color': getAlarmSeverity(alarm.severity.id).color,
                }"
              >
                {{
                  alarm.severity.id
                    ? getAlarmSeverity(alarm.severity.id).displayName
                    : '---'
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="fR mT15">
        <div class="display-flex">
          <el-button
            class="fc-btn-green-lg-fill"
            @click="acknowledgeAlarms()"
            v-if="
              !alarm.acknowledged &&
                isActiveAlarm(alarm) &&
                $hasPermission('alarm:ACKNOWLEDGE_ALARM')
            "
            >Acknowledge</el-button
          >
          <div
            class="q-item-label fc-summary-ack status-btn1 pR10 pointer"
            v-else-if="!isActiveAlarm(alarm)"
          >
            Unacknowledged
          </div>
          <div class="q-item-label mR10 status-btn2" v-else>
            <span class="pB10 fc-summary-ack ellipsis"
              >Acknowledged by
              {{
                alarm.acknowledgedBy
                  ? $store.getters.getUser(alarm.acknowledgedBy.id).name
                  : 'Unknown'
              }}</span
            >
            <div class="text-right">
              &nbsp;
              <span class="fc-summary-timeago text-right">{{
                alarm.acknowledgedTime > 0
                  ? alarm.acknowledgedTime
                  : new Date() | fromNow
              }}</span>
            </div>
          </div>
          <el-button
            class="fc-btn-green-lg-border"
            v-if="isActiveAlarm(alarm) && $hasPermission('alarm:CLEAR_ALARM')"
            @click="updateAlarmsStatus()"
            >CLEAR</el-button
          >
        </div>
      </div>
    </div>
    <page
      v-if="alarmId && !loading"
      :key="alarmId"
      :module="currentModuleName"
      :id="alarmId"
      :details="details"
    ></page>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import { mapActions, mapGetters } from 'vuex'
import NewDateHelper from '@/mixins/NewDateHelper'
import AnomalyMixin from '@/mixins/AnomalyMixin'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [AnomalyMixin, NewDateHelper],
  components: { Page },
  created() {
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
    this.loadAlarmDetails()
  },

  mounted() {},
  data() {
    return {
      currentModuleName: 'mlAnomalyAlarm',
      loading: false,
    }
  },
  computed: {
    ...mapGetters(['getAlarmSeverity']),
    ...mapActions({
      addAlarmApi: 'newAlarm/addAlarm',
      updateAlarmStatus: 'newAlarm/updateAlarmStatus',
      notifyAlarm: 'newAlarm/notifyAlarm',
      acknowledgeAlarm: 'newAlarm/acknowledgeAlarm',
      severityStatus: state => state.alarmSeverity,
      deleteAlarm: 'newAlarm/deleteAlarm',
    }),
    severityStatus() {
      return this.$store.state.alarmSeverity
    },
    alarm() {
      return this.$store.state.newAlarm.currentAlarm
    },
    occurrence() {
      return this.$store.state.newAlarm.currentOccurrence
    },
    alarmId() {
      return parseInt(this.$route.params.id)
    },
    details() {
      return {
        alarm: this.$store.state.newAlarm.currentAlarm,
        occurrence: this.occurrence,
        id: this.alarmId,
      }
    },
  },
  watch: {
    alarmId: function(newVal) {
      this.loadAlarmDetails()
    },
    occurrence: {
      handler(value, oldValue) {
        let { id } = value || {}
        if (isEmpty(id)) return
        let { id: oldId = null } = oldValue || {}
        if (id !== oldId) {
          let paramJson = {}
          paramJson.alarmId = this.alarmId
          paramJson.resourceId =
            this.alarm && this.alarm.resource ? this.alarm.resource.id : null
          paramJson.siteId =
            this.alarm && this.alarm.resource
              ? this.alarm.resource.siteId
              : null
          paramJson.dateRange = this.currMntNLastMntRanges
          this.$store.dispatch('anomalies/getMetricsDetails', paramJson)
        }
      },
      immediate: true,
    },

    // this.$store.dispatch('newAlarm/getOccurrenceFromId', { id: this.alarm })
    // this.$store.dispatch('newAlarm/getEventsFromId', { id: this.occurrence })
  },
  methods: {
    updateAlarmsStatus() {
      let clearSeverity = this.severityStatus.find(
        status => status.severity === 'Clear'
      )
      let context = {
        severity: clearSeverity,
        occurrence: this.occurrence,
        alarm: this.alarm,
        clearedTime: Date.now(),
      }
      console.log('this.severity' + context)
      this.updateAlarmStatus(context)
      this.alarm.clearedBy = this.$account.user
    },
    exportSummary(type) {
      this.exportType = type
      this.showAssetExport = true
      this.$util.loadAssetReadingFields(this.alarm.resource.id).then(fields => {
        this.assetFields = fields
      })
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
    loadAlarmDetails() {
      let self = this
      this.loading = true
      this.$store
        .dispatch('newAlarm/fetchAlarm', {
          id: parseInt(this.alarmId),
        })
        .then(() => {
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
  },
}
</script>
<style scoped lang="scss">
.analmoly-overview-page {
  .fc-anamoly-btn-actions {
    height: 45px;
    padding-left: 10px;
    padding-right: 10px;
    position: relative;
    line-height: 42px;
    padding-top: 0;
    padding-bottom: 0;
  }
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
}
</style>
