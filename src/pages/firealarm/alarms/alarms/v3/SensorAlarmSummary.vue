<template>
  <div class="main-div fc-v1-alarm-overview">
    <div v-if="alarmOverviewLoading" class="flex-middle fc-empty-white">
      <spinner :show="alarmOverviewLoading" size="80"></spinner>
    </div>
    <template v-else>
      <div class="fc__layout__align fc__asset__main__header summary-basic">
        <div v-if="currentAlarm">
          <div class="wos-id" :title="$t('common.header.back')">
            <span class="fc-id">#{{ currentAlarm.id }}</span>
          </div>
          <div class="fc-black-22 fw3 flex-middle">
            <div
              class="max-width550px fw4 flex-middle"
              :title="currentAlarm.subject"
              v-tippy="subjectHoverOptions"
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
            @click="acknowledgeAlarms()"
            v-if="
              canAcknowledge &&
                $hasPermission('sensorrollupalarm:ACKNOWLEDGE_ALARM')
            "
            >{{ $t('common.header.acknowledge') }}</el-button
          >
          <div
            class="q-item-label fc-summary-ack unacknowledged-status-btn pR10 pointer"
            v-else-if="isAcknowledged(currentAlarm)"
          >
            {{ $t('common.header.unacknowledged') }}
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
            <el-dropdown-menu slot="dropdown" trigger="click">
              <el-dropdown-item
                command="createWo"
                v-if="
                  isActiveAlarm(currentAlarm) &&
                    $hasPermission('sensorrollupalarm:CREATE_WORKORDER')
                "
              >
                <div v-if="isWoCreated()">
                  {{ $t('common._common.view_workorder') }}
                </div>
                <div v-else-if="hasCreateWoPermission">
                  {{ $t('common.wo_report.create_workorder') }}
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <page
        :module="currentModuleName"
        :id="id"
        :details="pageBuilderDetails"
        :notesModuleName="notesModuleName"
        :primaryFields="primaryFields"
      ></page>
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
    </template>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import BmsAlarmsSummary from 'src/pages/firealarm/alarms/alarms/v3/BmsAlarmSummary.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'SensorAlarmSummary',
  props: ['viewname', 'moduleName'],
  extends: BmsAlarmsSummary,
  computed: {
    hasCreateWoPermission() {
      return this.$hasPermission('alarm:CREATE_WO')
    },
    subjectHoverOptions() {
      return {
        placement: 'top',
        animation: 'shift-away',
        arrow: true,
      }
    },
    pageBuilderDetails() {
      let id = this.$getProperty(this.currentAlarm, 'id')

      return {
        alarm: { ...this.currentAlarm },
        occurrence: this.occurrence,
        id: id,
      }
    },
  },
  methods: {
    async loadRecords(force = true) {
      this.alarmOverviewLoading = true
      let { currentModuleName } = this
      let { sensorrollupalarm, error } = await API.fetchRecord(
        currentModuleName,
        {
          id: this.id,
        },
        { force }
      )
      if (error) this.$message.error(error.message)
      else {
        this.currentAlarm = sensorrollupalarm
        await this.loadSpaceDetails()

        let { alarmoccurrence } = await API.fetchRecord(
          'alarmoccurrence',
          {
            id: this.$getProperty(sensorrollupalarm, 'lastOccurrenceId'),
          },
          { force }
        )
        this.occurrence = alarmoccurrence
      }
      this.alarmOverviewLoading = false
    },
    refreshData() {
      this.loadRecords(true)
    },
    isAcknowledged(currentAlarm) {
      return (
        !this.isActiveAlarm(currentAlarm) &&
        !this.$getProperty(currentAlarm, 'acknowledged')
      )
    },
    isWoCreated() {
      let woId = this.$getProperty(this, 'occurrence.woId')
      return !isEmpty(woId) && woId > 0
    },
    handleDropDown(command) {
      let woId = this.$getProperty(this, 'occurrence.woId')

      if (command === 'createWo') {
        if (woId > 0) {
          this.openWorkorder(woId)
        } else {
          this.createWoDialog(
            this.$getProperty(this, 'currentAlarm.lastOccurrenceId')
          )
        }
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
  align-items: center;
  border-bottom: none;
  position: relative;
  padding: 20px;
}

.unacknowledged-status-btn {
  padding: 10px;
  border-radius: 3px;
  background: rgba(77, 229, 255, 0.2);
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  color: #39b2c2;
}
</style>
