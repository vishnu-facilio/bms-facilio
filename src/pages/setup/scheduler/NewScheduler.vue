<template>
  <el-dialog
    :visible="true"
    width="50%"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog scheduler-form"
  >
    <el-form
      :rules="rules"
      :model="scheduleData"
      :label-position="'top'"
      ref="scheduleDetails"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('common.products.new_schedule_details')
                : $t('common.header.edit_schedule_details')
            }}
          </div>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <el-form-item :label="$t('common.products._name')" prop="name">
            <el-input
              v-model="scheduleData.name"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <schedule-trigger :trigger="frequencyEdit"></schedule-trigger>
          <el-form-item prop="actions">
            <el-row class="mB10 mT25">
              <el-col :span="16" class="pL0">
                <div
                  class="fc-input-label-txt bold pB0 text-fc-pink text-uppercase actions-text"
                >
                  {{ $t('common.products.actions') }}
                </div>
                <div class="fc-sub-title-desc pT5">
                  {{
                    $t('common.wo_report.configure_actions_schedule_execute')
                  }}
                </div>
                <div v-if="actionsIsEmpty" class="error-message">
                  {{ $t('common.wo_report.configure_actions_empty') }}
                </div>
              </el-col>
              <el-col :span="8">
                <el-dropdown
                  class="fR"
                  @command="openActions"
                  hide-on-click
                  trigger="click"
                >
                  <div style="color: #23b096" class="f13 mT10 pointer">
                    <img
                      src="~assets/add-icon.svg"
                      style="height:18px;width:18px;margin-right: 4px;"
                      class="vertical-middle"
                    />
                    {{ $t('common.header.add_actions') }}
                  </div>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      v-for="(label, type) in actionTypeDisplayName"
                      :key="type"
                      :command="parseInt(type)"
                      required
                    >
                      {{ label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-col>
            </el-row>
          </el-form-item>

          <el-row
            v-for="(action, index) in scheduleData.actions"
            :key="index"
            class="action-row"
          >
            <el-col :span="12">
              <p class="details-Heading">
                {{ actionTypeDisplayName[action.actionType] }}
              </p>
            </el-col>

            <el-col :span="12" class="line-height24 d-flex">
              <span class="configured-green mL-auto">{{
                $t('common._common.configured')
              }}</span>
              <span class="mL20 action-edit-section">
                <i
                  class="el-icon-edit pointer"
                  @click="editAction(action, index)"
                  :title="$t('common._common.edit')"
                  v-tippy
                ></i>
                <span
                  class="mL20 reset-txt pointer"
                  @click="deleteAction(index)"
                >
                  {{ $t('common._common.reset') }}
                </span>
              </span>
            </el-col>
          </el-row>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>

        <el-button
          type="primary"
          @click="submitForm()"
          :loading="saving"
          class="modal-btn-save"
        >
          {{
            saving
              ? $t('maintenance._workorder.saving')
              : $t('maintenance._workorder.save')
          }}
        </el-button>
      </div>
    </el-form>

    <TriggerDialog
      v-if="showTrigger"
      :selectedTrigger="activeTrigger"
      @onSave="action => saveAction(action, actionTypes.TRIGGER)"
      @onClose="closeAction"
    ></TriggerDialog>

    <ScriptDialog
      v-if="showScript"
      :actionObj="activeScript"
      @onSave="action => saveAction(action, actionTypes.SCRIPT)"
      @onClose="closeAction"
    ></ScriptDialog>
  </el-dialog>
</template>
<script>
import ScriptDialog from 'src/newapp/setupActions/components/ScriptEditor.vue'
import TriggerDialog from './TriggerDialog'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import ScheduleTrigger from 'src/components/TriggerTypeSchedule.vue'
import { FREQUENCY_HASH } from 'src/pages/workorder/pm/create/utils/pm-utils.js'
const actionTypes = {
  TRIGGER: 34,
  SCRIPT: 21,
}

export default {
  props: ['selectedSchedule'],
  components: {
    ScriptDialog,
    TriggerDialog,
    ScheduleTrigger,
  },
  data() {
    return {
      saving: false,
      actionsIsEmpty: null,
      actionTypeDisplayName: {
        34: 'Invoke Trigger',
        21: 'Execute Script',
      },
      scheduleData: {
        name: '',
        startHour: ['00:00'],
        startDate: null,
        actions: [],
      },
      showTrigger: false,
      frequencyEdit: null,
      activeTrigger: null,
      showScript: false,
      activeScript: null,
      activeActionIdx: null,
      actionTypes,
      schedule: null,
      rules: {
        name: [
          { required: true, message: 'Name is Mandatory ', trigger: 'blur' },
        ],
        actions: {
          validator: function(rule, value, callback) {
            if (isEmpty(value)) {
              callback(new Error('Actions is Empty'))
            } else callback()
          }.bind(this),
        },
      },
    }
  },

  created() {
    this.initTrigger()
    if (!this.isNew) {
      const selectedSchedule = cloneDeep(this.selectedSchedule)
      let { actions: actionsList, schedule } = selectedSchedule || {}
      delete schedule.rolledBackDatesAsList
      delete schedule.timeObjects
      this.frequencyEdit = this.deserializeTrigger(selectedSchedule)
      let actions = (actionsList || []).map(action => {
        let {
          actionType,
          template: { originalTemplate },
        } = action
        let templateJson = {}

        if (actionType === actionTypes.SCRIPT) {
          this.activeScript = action
          let {
            workflowContext: { workflowV2String, id },
          } = originalTemplate || {}
          let resultWorkflowContext = { workflowV2String, isV2Script: true }
          if (!isEmpty(id)) {
            resultWorkflowContext = { ...resultWorkflowContext, id }
          }
          templateJson = { resultWorkflowContext }
        } else if (actionType === actionTypes.TRIGGER) {
          templateJson = { ...originalTemplate }
        }

        return { actionType, templateJson }
      })

      this.scheduleData = {
        ...selectedSchedule,
        actions,
      }
    }
  },

  computed: {
    isNew() {
      return isEmpty(this.selectedSchedule)
    },
    frequencyType() {
      let frequencyType = this.$getProperty(
        this,
        'frequencyEdit.schedule.frequencyType',
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
  methods: {
    openActions(type) {
      let { TRIGGER, SCRIPT } = actionTypes

      this.activeTrigger = null
      this.activeScript = null

      if (type === TRIGGER) {
        this.showTrigger = true
      } else if (type === SCRIPT) {
        this.showScript = true
      }
    },
    serializeTrigger() {
      let { frequencyEdit, frequencyType: frequencyEnum } = this || {}
      let { schedule, startDate, basedOn } = frequencyEdit || {}
      let { frequencyType, endDate } = schedule || {}

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
        ...schedule,
        endTime: endDate,
        startDate,
        frequencyType,
        schedule: JSON.stringify(schedule),
      }
      return trigger
    },
    deserializeTrigger(trigger) {
      let { scheduleJson: schedule } = trigger
      if (!isEmpty(schedule)) {
        schedule = JSON.parse(schedule)
        let { frequencyType, skipEvery, frequency } = schedule || {}
        let basedOn = ''

        if (
          [
            'MONTHLY_DATE',
            'QUARTERLY_DATE',
            'HALF_YEARLY_DATE',
            'ANNUALLY_DATE',
            'DAILY',
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

        schedule = {
          ...schedule,
          frequencyType,
          skipEvery: isEmpty(skipEvery) ? -1 : skipEvery,
          frequency: isEmpty(frequency) ? 1 : frequency,
        }

        return {
          ...trigger,
          schedule,
          basedOn,
          frequency: frequencyType,
          type: 1,
        }
      }
      delete trigger.rolledBackDatesAsList
      delete trigger.timeObjects
      return trigger
    },
    initTrigger() {
      let type = 1
      let startDate = new Date()
      startDate.setHours(0, 0, 0, 0)
      let startTime = startDate.getTime()
      let name = `Trigger`
      this.frequencyEdit = {
        id: null,
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
        stopAfter: 'never',
        endTime: new Date(),
      }
    },
    editAction(action, index) {
      let { actionType, templateJson } = action
      let { TRIGGER, SCRIPT } = actionTypes

      if (actionType === TRIGGER) {
        this.activeTrigger = templateJson
        this.showTrigger = true
      } else if (actionType === SCRIPT) {
        this.activeScript = action
        this.showScript = true
      }
      this.activeActionIdx = index
    },
    saveAction(action, type) {
      this.$refs.scheduleDetails.clearValidate('actions')
      let activeAction =
        type === actionTypes.SCRIPT
          ? { ...action }
          : { actionType: type, templateJson: { ...action } }
      let { activeActionIdx } = this

      if (!isEmpty(activeActionIdx)) {
        this.scheduleData.actions.splice(activeActionIdx, 1, activeAction)
      } else {
        this.scheduleData.actions.push(activeAction)
      }
      this.activeActionIdx = null
    },
    deleteAction(index) {
      this.scheduleData.actions.splice(index, 1)
    },
    closeAction() {
      this.showTrigger = false
      this.showScript = false
      this.activeActionIdx = null
    },

    async validate() {
      let isNameNotEmpty = await new Promise(resolve => {
        this.$refs['scheduleDetails'].validate(valid => {
          resolve(valid)
        })
      })
      return isNameNotEmpty
    },
    async submitForm() {
      let isValid = await this.validate()

      if (!isValid) return

      let { name, id, actions } = this.scheduleData
      let { basedOn, startTime } = this.frequencyEdit
      let payloadSchedule = this.serializeTrigger()
      let startDate = new Date()

      startDate.setHours(0, 0, 0, 0)

      let params = {
        scheduledWorkflow: {
          startDate,
          startTime,
          basedOn,
          schedule: payloadSchedule,
          name,
          actions,
        },
      }
      let url = '/v2/workflow/addOrUpdateScheduledWorkflow'

      if (!isEmpty(id)) {
        params.scheduledWorkflow.id = id
      }
      this.saving = true

      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success(
          this.$t('common._common.scheduler_saved_successfully')
        )
        this.$emit('onSave')
        this.closeDialog()
      }
      this.saving = false
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.scheduler-form {
  .fc-modal-sub-title {
    color: #324056;
  }
  .details-Heading {
    font-weight: normal;
    color: #324056;
  }
  .action-row {
    padding: 20px 10px;
  }
  .action-row:hover {
    background-color: #f1f8fa;
  }
  .action-edit-section {
    padding-left: 20px;
    border-left: 1px solid #d9e0e1;
  }
  .configured-green {
    color: #5bc293;
  }
  .reset-txt {
    font-size: 12px;
    letter-spacing: 0.5px;
    color: #6171db;
  }
  .custom-frequency .fc-input-label-txt {
    margin-top: 22px;
  }
  .error-message {
    color: rgb(245, 104, 104);
    font-size: 12px;
    line-height: 1;
  }
  .actions-text::after {
    content: '*';
    color: red;
    margin-left: 3px;
    font-size: 15px;
  }
}
</style>
