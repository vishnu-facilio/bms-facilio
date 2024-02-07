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
        <div class="fc-black-22 fw3 flex-middle">
          <div
            class="max-width550px fw4 flex-middle"
            :title="alarm.subject"
            v-tippy="{
              placement: 'top',
              animation: 'shift-away',
              arrow: true,
            }"
          >
            {{ alarm.subject }}
            <div v-if="alarm.severity" class="flex-middle mT10 pL10">
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
      <div class="display-flex mR10">
        <el-button
          class="fc-btn-green-lg-fill"
          @click="acknowledgeAlarms()"
          v-if="
            !occurrence.acknowledged &&
              isActiveAlarm(alarm) &&
              $hasPermission('alarm:ACKNOWLEDGE_ALARM')
          "
          >Acknowledge</el-button
        >
        <div
          class="q-item-label fc-summary-ack status-btn1 pR10 pointer"
          v-else-if="!isActiveAlarm(alarm) && !occurrence.acknowledged"
        >
          Unacknowledged
        </div>
        <div class="flRight fc-el-button mL10">
          <el-button
            class="fc-btn-green-lg-border"
            v-if="isActiveAlarm(alarm) && $hasPermission('alarm:CLEAR_ALARM')"
            @click="updateAlarmsStatus()"
            >CLEAR</el-button
          >
        </div>
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
            v-tippy="{
              html: '#timer_popover_' + alarm.id,
              distance: 0,
              interactive: true,
              theme: 'light',
              animation: 'scale',
              arrow: true,
            }"
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
              html: '#timer_popover_' + alarm.id,
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
      :module="currentModuleName"
      :notesModuleName="notesModuleName"
      :id="alarm.id"
      :details="{ alarm: alarm, occurrence: occurrence, id: alarm.id }"
      :primaryFields="primaryFields"
    ></page>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <el-dialog
      v-if="showCreateWo"
      width="30%"
      class="dialog-header-remove export-dialog"
      :visible.sync="showCreateWo"
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
    </el-dialog>
    <el-dialog
      v-if="showAssetExport"
      class="dialog-header-remove export-dialog"
      :visible.sync="showAssetExport"
      width="30%"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">Export Alarm summary</div>
        </div>
      </div>
      <div class="export-body-dailog">
        <div>
          <el-checkbox
            checked
            style="padding-bottom: 10px;padding-left: 40px;padding-top: 10px;"
            disabled
            >All Events</el-checkbox
          >
        </div>
        <el-checkbox-group
          v-model="selectedFields"
          class="check-padding-remove"
        >
          <div class="row">
            <el-checkbox
              v-for="field in assetFields"
              :key="field.id"
              :label="field.id"
              class="check-width"
              >{{ field ? field.displayName : '' }}</el-checkbox
            >
          </div>
        </el-checkbox-group>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="showAssetExport = false" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button type="primary" @click="exportEvent()" class="modal-btn-save"
          >EXPORT DATA</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import AlarmMixin from '@/mixins/AlarmMixin'
import Timer from '@/Timer'
import AlarmModel from '@/AlarmModel'
import NewReportHelper from 'pages/report/mixins/NewReportHelper'
import { mapState, mapActions, mapGetters } from 'vuex'

export default {
  components: { Page, Timer, AlarmModel },
  mixins: [AlarmMixin, NewReportHelper],
  mounted() {
    this.$store
      .dispatch('newAlarm/fetchAlarm', { id: this.alarmId })
      .then(d => {
        this.alarmOverviewLoading = false
      })
  },
  props: ['moduleName'],
  data() {
    return {
      notesModuleName: 'basealarmnotes',
      showHistoryDialog: false,
      thresholddialog: false,
      showCreateWo: false,
      alarmOverviewLoading: true,
      isEditLoading: false,
      selectedRules: null,
      selectedRule: null,
      showAssetExport: false,
      assetFields: [],
      exportDownloadUrl: null,
      isLoading: false,
      currentModuleName: 'operationalarm',
      // currentModuleName: this.moduleName ? this.moduleName :'newreadingalarm',
      primaryFields: [],
      selectedFields: [],
    }
  },
  computed: {
    ...mapState({
      severityStatus: state => state.alarmSeverity,
    }),
    ...mapGetters(['getAlarmSeverity']),
    alarm() {
      return this.$store.state.newAlarm.currentAlarm
    },
    // currentModuleName() {
    //   return this.moduleName
    // },
  },
  watch: {
    alarmId: function() {
      this.$store.dispatch('newAlarm/fetchAlarm', { id: this.alarmId })
    },
  },
  methods: {
    ...mapActions({
      acknowledgeAlarm: 'newAlarm/acknowledgeAlarm',
    }),
    updateAlarmsStatus() {
      this.updateAlarmStatus({
        occurrence: this.occurrence,
        alarm: this.alarm,
        clearedTime: Date.now(),
        severity: this.severityStatus.find(
          status => status.severity === 'Clear'
        ),
      })
      this.alarm.clearedBy = this.$account.user
      // this.loadAssetDetails()
    },
    goBack() {
      window.history.go(-1)
    },
    closeWoDialog() {
      this.showCreateWo = false
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
    loadAssetDetails() {
      this.loadAlarmFields()
      // this.loadRCAReport()
    },
    loadAlarmFields() {
      this.$util.loadFields('operationAlarm', false).then(fields => {
        this.alarmFields = fields
      })
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
