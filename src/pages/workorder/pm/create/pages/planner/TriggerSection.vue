<template>
  <div>
    <div class="mT5 planner-trigger-section">
      <div class="planner-tab-header">
        <div class="section-sub-heading">
          {{ $t('maintenance._workorder.trigger') }}
        </div>
        <div class="flex flex-no-wrap mT10 justify-between">
          <div class="section-description width60">
            {{ getFormattedText($t('maintenance.pm.trigger_desc')) }}
          </div>
          <el-button
            v-if="!isEdit"
            @click="openTrigger"
            class="add-trigger-btn"
            >{{ $t('maintenance.pm.add_trigger') }}</el-button
          >
        </div>
        <div v-if="isEdit" class="trigger-edit-section">
          <div v-if="isLoading" class="width100 flex-center-vH">
            <spinner :show="isLoading" size="30"></spinner>
          </div>
          <template v-else>
            <div class="d-flex items-center">
              <div class="pm-green-dot mR8"></div>
              {{ frequencyType }}
            </div>

            <div class="d-flex text-center items-center">
              <div class="mL10 pointer" @click="openTrigger">
                <fc-icon group="default" name="edit-solid" size="16"></fc-icon>
              </div>
              <div>
                <div
                  v-if="!isDeleting"
                  class="mL10 pointer"
                  @click="resetTrigger"
                >
                  <fc-icon
                    group="default"
                    name="trash-can-solid"
                    size="16"
                    class="mR2"
                  ></fc-icon>
                </div>
                <spinner
                  v-else
                  size="20"
                  :show="isDeleting"
                  class="mL10"
                ></spinner>
              </div>
            </div>
          </template>
        </div>
      </div>
      <el-dialog
        v-if="openTriggerDialog"
        :visible.sync="openTriggerDialog"
        :append-to-body="true"
        class="fc-dialog-center-container new-add-triger-dialog fc-dialog-header-hide"
      >
        <div v-if="isLoading">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <div class="positon-relative trigger-dialog-body">
            <trigger
              :trigger="triggerEdit"
              :customTriggerTypes="[1]"
              :pmProps="pmProps"
            ></trigger>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancel">{{
              $t('maintenance._workorder.cancel')
            }}</el-button>
            <el-button
              type="primary"
              class="modal-btn-save"
              :loading="isSaving"
              @click="save"
              >{{ $t('maintenance._workorder.save') }}</el-button
            >
          </div>
        </template>
      </el-dialog>
    </div>
    <div class="mT15" v-if="false" @click="openTaskDialog">
      <div class="add-link-planner">+ {{ $t('maintenance.pm.add_task') }}</div>
    </div>
  </div>
</template>

<script>
import Trigger from './Trigger'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getPlaceholderText, FREQUENCY_HASH } from '../../utils/pm-utils.js'
import SeasonTriggerMixin from '@/mixins/SeasonTriggerMixin'

export default {
  props: [
    'pmProps',
    'planner',
    'reloadPlanner',
    'trigger',
    'showTaskButton',
    'pmRecord',
  ],
  mixins: [SeasonTriggerMixin],
  components: { Trigger },
  data: () => ({
    triggerEdit: null,
    openTriggerDialog: false,
    isSaving: false,
    moduleName: 'pmTriggerV2',
    isLoading: false,
    isDeleting: false,
  }),
  computed: {
    isEdit() {
      let { planner } = this || {}
      let { trigger } = planner || {}
      let { id } = trigger || {}
      return id
    },
    frequencyType() {
      let frequencyType = this.$getProperty(
        this,
        'triggerEdit.schedule.frequencyType',
        ''
      )
      let facilioFrequency = this.$getProperty(
        this,
        '$constants.FACILIO_FREQUENCY'
      )
      if (!isEmpty(frequencyType)) {
        let frequencyValue = facilioFrequency[frequencyType]
        return !isEmpty(frequencyValue) ? frequencyValue : frequencyType
      }
      return '---'
    },
  },
  watch: {
    trigger: {
      handler() {
        let { isEdit } = this || {}
        if (isEdit) {
          this.loadTrigger()
        } else {
          this.initTrigger()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getFormattedText(text, isUpperCase) {
      let { pmRecord } = this || {}
      return getPlaceholderText({ pmRecord, text, isUpperCase })
    },
    openTaskDialog() {
      this.$emit('openTaskDialog')
    },
    openTrigger() {
      let { trigger, isEdit } = this
      if (isEdit) {
        this.triggerEdit = this.deserializeTrigger(trigger)
      }
      this.openTriggerDialog = true
    },
    async resetTrigger() {
      this.isDeleting = true
      let { planner } = this || {}
      let trigger = null
      let { id } = planner || {}
      let params = { id, data: { trigger } }
      let { error } = await API.updateRecord('pmPlanner', params)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('maintenance.pm.planner_update'))
        this.openTriggerDialog = false
        this.reloadPlanner()
      }
      this.isDeleting = false
    },
    initTrigger() {
      let type = 1
      let startDate = new Date()
      startDate.setHours(0, 0, 0, 0)
      let startTime = startDate.getTime()
      let name = `Trigger`
      this.triggerEdit = {
        name: name,
        type: type,
        startDate: startDate,
        startTime: startTime,
        basedOn: 'Date',
        schedule: {
          times: ['00:00'],
          frequency: 1,
          skipEvery: -1,
          values: [],
          frequencyType: 1,
          weekFrequency: -1,
          yearlyDayValue: null,
          monthValue: -1,
          yearlyDayOfWeekValues: [],
        },
        customTrigger: {
          customModuleId: null,
          fieldId: null,
          days: null,
          hours: null,
        },
        stopAfter: 'never',
        endTime: new Date(),
        startReading: null,
        readingFieldId: null,
        stopAfterReading: 'never',
        endReading: null,
        readingInterval: null,
        assignedTo: null,
        reminders: [],
        criteria: null,
        sharingContext: {
          shareTo: 2,
          sharedRoles: [],
          sharedUsers: [],
          sharedGroups: [],
        },
      }
    },
    cancel() {
      let { isEdit, trigger } = this || {}
      if (!isEdit) {
        this.initTrigger()
      } else if (!isEmpty(trigger)) {
        this.triggerEdit = this.deserializeTrigger(trigger)
      }
      this.openTriggerDialog = false
    },
    async loadTrigger() {
      this.isLoading = true
      let { trigger, moduleName } = this || {}
      let { id } = trigger || {}
      let { [moduleName]: record } = await API.fetchRecord(moduleName, { id })
      this.triggerEdit = this.deserializeTrigger(record)
      this.isLoading = false
    },
    deserializeTrigger(trigger) {
      let { schedule } = trigger
      if (!isEmpty(schedule)) {
        schedule = JSON.parse(schedule)
        let { frequencyType } = schedule || {}
        let basedOn = ''

        if (
          [
            'MONTHLY_DATE',
            'QUARTERLY_DATE',
            'HALF_YEARLY_DATE',
            'ANNUALLY_DATE',
          ].includes(FREQUENCY_HASH[frequencyType])
        ) {
          basedOn = 'Date'
        } else {
          basedOn = 'Week'
        }

        if (
          ['MONTHLY_DATE', 'MONTHLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 3
        } else if (
          ['QUARTERLY_DATE', 'QUARTERLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 4
        } else if (
          ['HALF_YEARLY_DATE', 'HALF_YEARLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 5
        } else if (
          ['ANNUALLY_DATE', 'ANNUALLY_WEEK'].includes(
            FREQUENCY_HASH[frequencyType]
          )
        ) {
          frequencyType = 6
        }
        schedule = { ...schedule, frequencyType }
        return { ...trigger, schedule, basedOn, frequency: frequencyType }
      }
      return trigger
    },
    serializeTrigger() {
      let { triggerEdit, pmProps, frequencyType: frequencyEnum } = this || {}
      let { schedule, startDate, basedOn } = triggerEdit || {}
      let { frequencyType, endDate } = schedule || {}
      let { id: pmId } = pmProps || {}

      if (frequencyEnum === 'Monthly') {
        if (basedOn === 'Date') {
          frequencyType = 3
        } else {
          frequencyType = 4
        }
      } else if (frequencyEnum === 'Quarterly') {
        if (basedOn === 'Date') {
          frequencyType = 7
        } else {
          frequencyType = 8
        }
      } else if (frequencyEnum === 'Half Yearly') {
        if (basedOn === 'Date') {
          frequencyType = 9
        } else {
          frequencyType = 10
        }
      } else if (frequencyEnum === 'Annually') {
        if (basedOn === 'Date') {
          frequencyType = 5
        } else {
          frequencyType = 6
        }
      }

      schedule = { ...schedule, frequencyType }

      let trigger = {
        ...triggerEdit,
        frequency: frequencyType,
        endTime: endDate,
        startDate,
        frequencyType,
        schedule: JSON.stringify(schedule),
        pmId,
      }
      return trigger
    },
    async save() {
      if (!await this.canSaveTrigger()) {
        return
      }
      this.isSaving = true
      let { planner } = this || {}
      let trigger = this.serializeTrigger(trigger)
      let { id } = planner || {}
      let params = { id, data: { trigger } }
      let { error } = await API.updateRecord('pmPlanner', params)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(this.$t('maintenance.pm.planner_update'))
        this.openTriggerDialog = false
        this.reloadPlanner()
      }
      this.isSaving = false
    },
  },
}
</script>

<style lang="scss">
.new-add-triger-dialog .el-dialog__body {
  padding: 0;
}
.new-add-triger-dialog .el-dialog {
  width: 70% !important;
}
.trigger-dialog-body {
  height: 100%;
  max-height: 600px;
  overflow: hidden;
}
.planner-trigger-section {
  .add-trigger-btn {
    &:hover {
      border: solid 1px #39b2c2;
      background: #fff;
      color: #39b2c2;
    }
  }
}
.add-link-planner {
  font-size: 14px;
  color: #343f9c;
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
}
.trigger-edit-section {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 20px 15px;
  background: #fafeff;
  border: solid 1px #daedf0;
  margin-top: 10px;
}
.planner-tab-header {
  .add-trigger-btn {
    margin-top: -25px;
    height: 40px;
    width: 120px;
  }
}
</style>
