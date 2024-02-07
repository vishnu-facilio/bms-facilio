<template>
  <div class="main-div fc-v1-alarm-overview">
    <div v-if="alarmOverviewLoading" class="flex-middle fc-empty-white">
      <spinner :show="alarmOverviewLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div
        class="
        fc__layout__align fc__asset__main__header
        pT20
        pB20
        pL20
        pR20
        position-relative
        summary-basic
      "
      >
        <div v-if="currentAlarm">
          <div class="wos-id" :title="$t('common.header.back')">
            <span class="fc-id">#{{ currentAlarm.id }}</span>
          </div>
          <div class="fc-black-22 fw3 flex-middle">
            <div
              class="max-width550px fw4 flex-middle"
              :title="currentAlarm.subject"
              v-tippy="{
                placement: 'top',
                animation: 'shift-away',
                arrow: true,
              }"
            >
              <div class="fc-black-22 textoverflow-ellipsis">
                {{ currentAlarm.subject }}
              </div>
              <div v-if="currentAlarm.severity" class="flex-middle mT10 pL10">
                <div
                  class="uppercase secondary-color summaryseverityTag f11"
                  v-bind:style="{
                    'background-color': getAlarmColor(currentAlarm),
                  }"
                >
                  {{ getAlarmDisplayName(currentAlarm) }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="display-flex mR10">
          <div class="display-flex mR5">
            <CustomButton
              class="pR10"
              :record="currentAlarm"
              :moduleName="moduleName"
              :position="POSITION.SUMMARY"
              @refresh="refreshData()"
              @onError="() => {}"
            />
          </div>
          <el-button
            class="fc-btn-green-lg-fill"
            @click="acknowledgeAlarms"
            v-if="
              canAcknowledge && $hasPermission('bmsalarm:ACKNOWLEDGE_ALARM')
            "
            >{{ $t('common.header.acknowledge') }}</el-button
          >
          <div
            class="q-item-label fc-summary-ack status-btn1 pR10 pointer"
            v-else-if="
              !isActiveAlarm(currentAlarm) &&
                !$getProperty(currentAlarm, 'acknowledged')
            "
          >
            {{ $t('common.header.unacknowledged') }}
          </div>
          <div
            v-if="currentModuleName !== 'sensorrollupalarm'"
            class="flRight fc-el-button mL10"
          >
            <el-button
              class="fc-btn-green-lg-border"
              v-if="
                isActiveAlarm(currentAlarm) &&
                  $hasPermission('bmsalarm:CLEAR_ALARM')
              "
              @click="updateAlarmStatus(currentAlarm)"
              :disabled="buttonStatus"
              >{{ $t('common.header._clear') }}</el-button
            >
          </div>
          <div
            class="flex-middle z-10 alarm-timer"
            v-if="!alarmOverviewLoading"
          >
            <div class="fc-black-12">{{ $t('common._common.duration') }}:</div>
            <timer
              v-if="isAlarmClear"
              class="fc-red-new bold f14 p10 alarm-timer-duration"
              :time="occurrence.createdTime"
              :twoDigits="true"
              :title="occurrence.createdTime | formatDate()"
            ></timer>
            <timer
              v-else
              class="fc-red-new bold f14 p10 alarm-cleared-duration"
              :time="duration"
              :staticTime="true"
              :twoDigits="true"
              :title="durationDate"
            ></timer>
          </div>
          <el-dropdown
            class="mL10 fc-btn-ico-lg pointer"
            @command="handleDropDown($event)"
            trigger="click"
          >
            <span class="el-dropdown-link">
              <fc-icon
                group="action"
                name="options-vertical"
                color="rgb(0 0 0)"
              ></fc-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu slot="dropdown" trigger="click">
                <el-dropdown-item
                  command="createWo"
                  v-if="
                    isActiveAlarm(currentAlarm) &&
                      $hasPermission('bmsalarm:CREATE_WORKORDER')
                  "
                >
                  <div v-if="isWoCreated()">
                    {{ $t('common._common.view_workorder') }}
                  </div>
                  <div v-else>
                    {{ $t('common.wo_report.create_workorder') }}
                  </div>
                </el-dropdown-item>
                <div v-if="$hasPermission('bmsalarm:EXPORT')">
                  <el-dropdown-item command="exportCSV">
                    <div v-if="isActiveAlarm(currentAlarm)">
                      {{ $t('alarm.alarm.export_csv') }}
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item command="exportExcel">
                    <div v-if="isActiveAlarm(currentAlarm)">
                      {{ $t('alarm.alarm.export_excel') }}
                    </div>
                  </el-dropdown-item>
                </div>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <page
        :module="currentModuleName"
        :id="id"
        :details="detailsForPageBuilder"
        :notesModuleName="notesModuleName"
        :primaryFields="primaryFields"
      ></page>
      <iframe
        v-if="exportDownloadUrl"
        :src="exportDownloadUrl"
        style="display: none"
      ></iframe>
      <el-dialog
        :visible.sync="dialogVisible"
        width="32%"
        custom-class="dialog"
      >
        <AlarmModel
          ref="confirmWoModel"
          @submit="createWO"
          @closed="closeWoDialog"
        ></AlarmModel>
      </el-dialog>
      <el-dialog
        v-if="showAssetExport"
        :visible.sync="showAssetExport"
        width="30%"
      >
        <BMSAlarmsExportDialog
          :assetFields="assetFields"
          @submit="exportEvent"
          @closed="closeExportDialog"
        >
        </BMSAlarmsExportDialog>
      </el-dialog>
    </template>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Page from '@/page/PageBuilder'
import NewAlarmMixin from '@/mixins/NewAlarmMixin'
import Timer from '@/Timer'
import { isEmpty } from '@facilio/utils/validation'
import AlarmModel from '@/AlarmModel'
import BMSAlarmsExportDialog from 'src/pages/firealarm/alarms/alarms/v1/BMSAlarmsExportDialog.vue'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'

export default {
  props: ['viewname', 'moduleName'],
  name: 'BmsAlarmsSummary',
  mixins: [NewAlarmMixin],
  components: { Page, Timer, AlarmModel, BMSAlarmsExportDialog, CustomButton },
  created() {
    this.init()
  },
  data() {
    return {
      assetFields: [],
      selectedFields: [],
      exportType: null,
      currentModuleName: this.moduleName,
      currentAlarm: null,
      primaryFields: [
        'description',
        'source',
        'condition',
        'lastOccurredTime',
        'lastOccurredTimeString',
      ],
      alarmOverviewLoading: false,
      buttonStatus: false,
      showAssetExport: false,
      exportDownloadUrl: null,
      notesModuleName: 'basealarmnotes',
      POSITION: POSITION_TYPE,
    }
  },
  computed: {
    id() {
      let paramId = this.$attrs.id || this.$route.params.id
      return !isEmpty(paramId) ? parseInt(this.$route.params.id) : ''
    },
    alarm() {
      return this.currentAlarm
    },
    clearedTime() {
      return this.$getProperty(this, 'occurrence.clearedTime')
    },
    createdTime() {
      return this.$getProperty(this, 'occurrence.createdTime')
    },
    isAlarmClear() {
      return this.occurrence && this.createdTime && isEmpty(this.clearedTime)
    },
    duration() {
      return this.clearedTime - this.createdTime
    },
    durationDate() {
      return this.$options.filters.formatDate(this.duration)
    },
    detailsForPageBuilder() {
      return {
        alarm: this.currentAlarm,
        occurrence: this.occurrence,
        id: this.$getProperty(this.currentAlarm, 'id'),
      }
    },
    canAcknowledge() {
      let { currentAlarm } = this
      return (
        !this.$getProperty(currentAlarm, 'acknowledged') &&
        this.isActiveAlarm(currentAlarm) &&
        this.$hasPermission('alarm:ACKNOWLEDGE_ALARM')
      )
    },
  },
  watch: {
    id: {
      handler() {
        this.loadRecords()
      },
      // immediate: true,
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
      this.alarmOverviewLoading = true
      let { currentModuleName } = this
      let { bmsalarm, error } = await API.fetchRecord(
        currentModuleName,
        {
          id: this.id,
        },
        { force }
      )
      if (error) this.$message.error(error.message)
      else {
        this.currentAlarm = bmsalarm
        await this.loadSpaceDetails()

        let { alarmoccurrence } = await API.fetchRecord(
          'alarmoccurrence',
          {
            id: this.$getProperty(bmsalarm, 'lastOccurrenceId'),
          },
          { force }
        )
        this.occurrence = alarmoccurrence
      }
      this.alarmOverviewLoading = false
    },
    async loadSpaceDetails() {
      let { currentAlarm } = this
      let { resource } = currentAlarm || {}
      if (!isEmpty(resource)) {
        let { spaceId, buildingId, siteId } = resource || {}
        let locationId =
          spaceId > 0 ? spaceId : buildingId > 0 ? buildingId : siteId

        if (locationId > 0) {
          let url = `v2/basespaces/${spaceId}`
          let { data, error } = await API.get(url)
          if (isEmpty(error)) {
            this.currentAlarm['spaceDetails'] = this.$getProperty(
              data,
              'basespace'
            )
          } else {
            this.$message.error(error.message)
          }
        }
      }
    },
    refreshData() {
      this.loadRecords(true)
    },
    goBack() {
      window.history.go(-1)
    },

    closeExportDialog() {
      this.showAssetExport = false
      this.selectedFields = []
    },
    exportSummary(type) {
      this.exportType = type
      this.showAssetExport = true
      this.$util
        .loadAssetReadingFields(this.$getProperty(this, 'alarm.resource.id'))
        .then(fields => {
          this.assetFields = fields
        })
    },
    isWoCreated() {
      let woId = this.$getProperty(this, 'alarm.lastWoId')
      return !isEmpty(woId) && woId > 0
    },
    handleDropDown(command) {
      let woId = this.$getProperty(this, 'alarm.lastWoId')
      if (command === 'createWo') {
        woId > 0
          ? this.openWorkorder(woId)
          : this.createWoDialog(
              this.$getProperty(this, 'currentAlarm.lastOccurrenceId')
            )
      } else if (command === 'exportCSV') {
        this.exportSummary(1)
      } else {
        this.exportSummary(2)
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
.main-div {
  height: auto;
  overflow-y: scroll;
}
.summary-basic {
  width: auto;
  align-items: center !important;
  border-bottom: none;
}
</style>
