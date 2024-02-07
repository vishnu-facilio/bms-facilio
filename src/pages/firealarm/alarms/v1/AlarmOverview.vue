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
        <div class="wos-id" :title="$t('common.header.back')">
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
            <div
              class="fc-black-22"
              v-bind:class="fontsize(alarm.subject + '')"
            >
              {{ alarm.subject }}
            </div>
            <div v-if="alarm.severity" class="flex-middle mT10 pL10">
              <div
                class="uppercase secondary-color summaryseverityTag f11"
                v-bind:style="{
                  'background-color': getAlarmSeverityWithId(alarm.severity.id)
                    .color,
                }"
              >
                {{
                  alarm.severity.id
                    ? getAlarmSeverityWithId(alarm.severity.id).displayName
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
            !alarm.acknowledged &&
              isActiveAlarm(alarm) &&
              $hasPermission('alarm:ACKNOWLEDGE_ALARM')
          "
          >{{ $t('common.header.acknowledge') }}</el-button
        >
        <div
          class="q-item-label fc-summary-ack status-btn1 pR10 pointer"
          v-else-if="!isActiveAlarm(alarm) && !alarm.acknowledged"
        >
          {{ $t('common.header.unacknowledged') }}
        </div>
        <div
          v-if="currentModuleName !== 'sensorrollupalarm'"
          class="flRight fc-el-button mL10"
        >
          <el-button
            class="fc-btn-green-lg-border"
            v-if="isActiveAlarm(alarm) && $hasPermission('alarm:CLEAR_ALARM')"
            @click="updateFaultStatus(alarm)"
            :disabled="buttonStatus"
            >{{ $t('common.header._clear') }}</el-button
          >
        </div>
        <div class="flex-middle z-10 alarm-timer" v-if="!alarmOverviewLoading">
          <div class="fc-black-12">{{ $t('common._common.duration') }}:</div>
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
          ></timer>
        </div>
        <el-dropdown
          class="mL10 fc-btn-ico-lg pointer"
          style="padding-top: 5px; padding-bottom: 5px;"
          trigger="click"
        >
          <span class="el-dropdown-link">
            <img src="~assets/menu.svg" width="16" height="16" />
          </span>
          <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
            <div
              class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
              v-if="occurrence.woId > 0"
              @click="openWorkorder(occurrence.woId)"
            >
              {{ $t('common._common.view_workorder') }}
            </div>
            <div
              class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
              v-else-if="$hasPermission('alarm:CREATE_WO')"
              @click="openAlarmWoCreation(alarm.lastOccurrenceId)"
            >
              {{ $t('common.wo_report.create_workorder') }}
            </div>
          </el-dropdown-menu>
        </el-dropdown>
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
    <AlarmWoCreation
      v-if="canShowAlarmWoCreation"
      :canShowDialog.sync="canShowAlarmWoCreation"
      :currentAlarmId.sync="currentAlarmId"
      @onSuccess="onAlarmWoCreation"
    ></AlarmWoCreation>
    <el-dialog
      v-if="showAssetExport"
      class="dialog-header-remove export-dialog"
      :visible.sync="showAssetExport"
      width="30%"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('common.wo_report.export_alarm_summary') }}
          </div>
        </div>
      </div>
      <div class="export-body-dailog">
        <div>
          <el-checkbox
            checked
            style="padding-bottom: 10px;padding-left: 40px;padding-top: 10px;"
            disabled
            >{{ $t('common.events.all_events') }}</el-checkbox
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
        <el-button @click="showAssetExport = false" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="exportEvent()"
          class="modal-btn-save"
          >{{ $t('common.wo_report.export_data') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Page from '@/page/PageBuilder'
import AlarmMixin from '@/mixins/AlarmMixin'
import Timer from '@/Timer'
import NewReportHelper from 'pages/report/mixins/NewReportHelper'
import { mapState, mapActions, mapGetters } from 'vuex'
import { eventBus } from '@/page/widget/utils/eventBus'
import AlarmWoCreation from 'pages/firealarm/alarms/AlarmWoCreation'
import FaultListMixin from 'pages/firealarm/alarms/alarms/v1/FaultListMixin.vue'

export default {
  components: { Page, Timer, AlarmWoCreation },
  mixins: [AlarmMixin, NewReportHelper, FaultListMixin],
  created() {
    this.init()
  },

  mounted() {
    this.$store
      .dispatch('newAlarm/fetchAlarm', { id: this.alarmId })
      .then(() => {
        this.alarmOverviewLoading = false
      })
  },
  props: ['moduleName'],
  data() {
    return {
      notesModuleName: 'basealarmnotes',
      showHistoryDialog: false,
      thresholddialog: false,
      alarmOverviewLoading: true,
      isEditLoading: false,
      selectedRules: null,
      selectedRule: null,
      showAssetExport: false,
      assetFields: [],
      exportDownloadUrl: null,
      isLoading: false,
      primaryFields: [],
      selectedFields: [],
      canShowAlarmWoCreation: false,
      currentAlarmId: null,
      buttonStatus: false,
    }
  },
  computed: {
    ...mapState({
      severityStatus: state => state.alarmSeverity,
    }),
    alarm() {
      return this.$store.state.newAlarm.currentAlarm
    },
    currentModuleName() {
      return this.moduleName
    },
  },
  methods: {
    ...mapActions({
      acknowledgeAlarm: 'newAlarm/acknowledgeAlarm',
    }),
    async init() {
      await Promise.all([
        this.loadAlarmSeverity(),
        this.loadReadingAlarmCategory(),
      ])
    },
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
    },
    exportEvent(type) {
      type = 1
      let array = {
        alarmId: this.occurrence.id,
        fieldId: this.selectedFields,
        type: this.exportType,
        parentId: this.alarm.resource.id,
      }
      let self = this
      let url = 'event/eventExport'
      self.$message({
        showClose: true,
        message: this.$t('common._common.downloading'),
        type: 'success',
      })
      this.$http.post(url, array).then(function(response) {
        self.exportDownloadUrl = response.data.fileUrl
        self.showAssetExport = false
      })
    },
    goBack() {
      window.history.go(-1)
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverityWithId(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
    acknowledgeAlarms() {
      let self = this
      self.$store
        .dispatch('newAlarm/acknowledgeAlarm', {
          alarm: this.alarm,
          occurrence: this.occurrence,
          acknowledged: true,
          acknowledgedBy: this.$account.user,
        })
        .then(function() {
          self.$dialog.notify(self.$t('common.dialog.alarm_was_acknowledge'))
          eventBus.$emit('reload')
          self.occurrence.acknowledged = true
        })
      this.loadAssetDetails()
    },
    loadAssetDetails() {
      this.loadAlarmFields()
      this.loadRCAReport()
    },
    loadAlarmFields() {
      this.$util.loadFields('newreadingalarm', false).then(fields => {
        this.alarmFields = fields
      })
    },
    loadRCAReport() {
      if (
        this.additionInfo &&
        this.additionInfo.rcaJSONArray &&
        this.additionInfo.rcaJSONArray.length > 0
      ) {
        this.additionInfo.rcaJSONArray.forEach(d => {
          this.$set(d, 'analyticsConfig', null)
          this.$nextTick(() => {
            this.$set(d, 'analyticsConfig', {
              alarmId: this.occurrence.id,
              readingRuleId: d.rcaRule.id,
              dateFilter: this.getDatePickerObject(),
              hidechartoptions: true,
              hidetabular: true,
              hidecharttypechanger: true,
              fixedChartHeight: 300,
              isFromAlarmSummary: true,
              applyReportDate: this.alarm.sourceType === 12,
            })
          })
        })
      }
    },
    getDatePickerObject() {
      let lastOccurredTime
      lastOccurredTime = this.alarm.lastOccurredTime
      if (this.alarm.lastOccurredTime > 0) {
        lastOccurredTime = this.alarm.lastOccurredTime
      }
      return newDateHelper.getDatePickerObject(
        this.alarm.sourceType === 12 ? 20 : 62,
        '' + lastOccurredTime
      )
    },
    fontsize(value) {
      if (!value) {
        return 'f22'
      }
      value = value + ''
      if (value.length < 100) {
        return 'f20'
      } else if (value.length < 130) {
        return 'f18'
      } else if (value.length < 150) {
        return 'f16'
      } else if (value.length < 170) {
        return 'f14'
      } else if (value.length < 180) {
        return 'f12'
      } else {
        return 'f10'
      }
    },
    openAlarmWoCreation(alarmId) {
      this.canShowAlarmWoCreation = true
      this.currentAlarmId = alarmId
      this.loadRecord()
    },
    onAlarmWoCreation(props) {
      let { woId } = props
      this.$store.commit('newAlarm/UPDATE_ALARM_WO', {
        woId,
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
