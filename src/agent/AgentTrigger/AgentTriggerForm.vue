<template>
  <div>
    <el-dialog
      :title="
        isNew
          ? $t('agent.create.create_agent_trigger')
          : $t('agent.create.edit_agent_trigger')
      "
      :visible="true"
      width="45%"
      custom-class="fc-setup-dialog-form fc-setup-dialog-form fc-setup-rightSide-dialog-scroll"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <el-form ref="triggerForm" :model="trigger" :label-position="'top'">
        <el-form-item label="name">
          <el-input
            v-model="trigger.name"
            class="width100 fc-input-full-border2"
          ></el-input>
        </el-form-item>
        <el-form-item label="agent">
         <SelectAgent
          hideLabel="true"
          @onAgentFilter="updateSelectedAgent"
          :defaultAgent="isNew == false ? triggerData.agent.id : null"
          class="select-agent-width"
         >
         </SelectAgent>
        </el-form-item>

        <el-form-item label="">
          <new-criteria-builder
            ref="criteriaBuilder"
            v-model="trigger.criteria"
            :showSiteField="showSiteField"
            :showFormIDField="showFormIDField"
            :exrule="trigger.criteria"
            @condition="somefnt"
            :module="'asset'"
            :isRendering.sync="criteriaRendered"
            :title="'Specify rules for asset action'"
          ></new-criteria-builder>
        </el-form-item>
        <el-form-item label="">
          <div class="fc-modal-sub-title line-height-normal">
            {{ $t('common._common.execute_on') }}
          </div>
          <div class="fc-sub-title-desc">
            {{ $t('agent.agent.rule_action_executed') }}
          </div>
          <el-select
            v-model="trigger.eventType"
            placeholder="Select agent"
            class="width100 fc-input-full-border2 mT10"
            disabled
          >
            <el-option
              v-for="execute in excuteTrigger"
              :key="execute.value"
              :label="execute.label"
              :value="execute.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <p class="subHeading-pink-txt">{{ $t('common.products.action') }}</p>
          <p class="small-description-txt pT5">
            {{ $t('agent.agent.set_actions_corresponding_rule') }}
          </p>
          <el-row class="mT20">
            <el-col :span="10">
              <p class="details-Heading">
                {{ $t('common._common.execute_script') }}
              </p>
              <p class="small-description-txt2">
                {{ $t('agent.agent.the_system_will_execute') }} <br />
                {{ $t('agent.agent.script_based_criteria') }}
              </p>
            </el-col>
            <el-col :span="8">
              <el-button
                type="button"
                :class="
                  !$validation.isEmpty(trigger.triggerActions)
                    ? 'success-green-btn'
                    : 'small-border-btn'
                "
                @click="showBuilder = true"
                >{{
                  !$validation.isEmpty(trigger.triggerActions)
                    ? 'Configured'
                    : 'Configure'
                }}
                <span
                  v-if="!$validation.isEmpty(trigger.triggerActions)"
                  class="mL10"
                >
                  <i
                    class="el-icon-edit pointer"
                    :title="$t('agent.edit.edit_script')"
                    v-tippy
                  ></i>
                  <!-- <span class="mL10 reset-txt pointer" @click="reset(action)"
                >Reset</span
              > -->
                </span>
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="saveTrigger()"
        >
          {{ $t('panel.dashboard.confirm') }}
        </el-button>
      </div>
    </el-dialog>

    <ScriptDialog
      v-if="showBuilder"
      :script="currentWorkflow"
      name="typeRefObj"
      header="void executeTrigger(Number assetId, Map agent)"
      @onSave="action => (currentWorkflow = action)"
      @onClose="showBuilder = false"
    >
    </ScriptDialog>
  </div>
</template>
<script>
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { API } from '@facilio/api'
import ScriptDialog from 'src/pages/setup/scheduler/ScriptDialog'
import SelectAgent from 'src/agent/components/SelectAgent.vue'
export default {
  props: ['isNew', 'triggerData'],
  data() {
    return {
      criteriaRendered: false,
      saving: false,
      loading: true,
      selectedAction: null,
      showBuilder: false,
      triggerDetailsData: [],
      trigger: {
        name: '',
        agentId: null,
        eventType: 'Timeseries completed',
        actions: [],
        criteria: null,
        workflow: '',
        triggerActions: [],
      },
      excuteTrigger: [
        {
          label: 'Timeseries completed',
          value: -2147483648,
        },
      ],
    }
  },
  components: {
    NewCriteriaBuilder,
    ScriptDialog,
    SelectAgent
  },
  created() {
    if (!this.isNew) {
      this.getAgentTrigger()
    }
  },
  computed: {
    moduleFields() {
      if (this.criteriaRendered) {
        return
      }
      if (
        this.$refs.criteriaBuilder &&
        this.$refs.criteriaBuilder.moduleMetaObject &&
        this.$refs.criteriaBuilder.moduleMetaObject.fields
      ) {
        return this.$refs.criteriaBuilder.moduleMetaObject.fields
      }
      return null
    },
    showSiteField() {
      let workFlowActionHash = this.$constants.WorkFlowAction.module[
        this.module
      ]
      return workFlowActionHash && workFlowActionHash.showSiteField
    },
    showFormIDField() {
      let workFlowActionHash = this.$constants.WorkFlowAction.module[
        this.module
      ]
      return workFlowActionHash && workFlowActionHash.showFormIDField
    },
    currentWorkflow: {
      get() {
        let { triggerActions } = this.trigger || {}
        let scriptActions =
          (triggerActions || []).find(rt => rt.actionType === 3) || {}

        return scriptActions
      },
      set(value) {
        let { triggerActions } = this.trigger || {}
        let scriptActionsIndex = (triggerActions || []).findIndex(
          rt => rt.actionType === 3
        )
        if (scriptActionsIndex > -1) {
          let scriptAction = triggerActions[scriptActionsIndex]
          this.$set(triggerActions, scriptActionsIndex, {
            ...scriptAction,
            ...value,
          })
          this.$set(this.trigger, 'triggerActions', triggerActions)
        } else {
          let scriptAction = {
            name: 'Workflow trigger',
            actionType: 3,
            executionOrder: 1,
            ...value,
          }
          triggerActions.push(scriptAction)
          this.$set(this.trigger, 'triggerActions', triggerActions)
        }
      },
    },
  },
  methods: {
    somefnt(newVal) {
      this.trigger.criteria = newVal
    },
    closeDialog() {
      this.$emit('onClose')
    },
    saveTrigger() {
      this.$refs['triggerForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = 'v3/trigger/addOrUpdate'
        let params = {
          trigger: {
            name: this.trigger.name,
            type: 4,
            eventType: -2147483648,
            status: true,
            agentId: this.trigger.agentId,
            criteria: this.trigger.criteria,
            triggerActions: this.trigger.triggerActions,
          },
        }

        if (!this.isNew) {
          params.trigger = {
            name: this.trigger.name,
            id: this.trigger.id,
            type: 4,
            eventType: -2147483648,
            status: true,
            agentId: this.trigger.agentId,
            criteria: this.trigger.criteria,
            triggerActions: this.trigger.triggerActions,
          }
        }
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success('trigger added')
          this.$emit('onSave')
          this.closeDialog()
        }
        this.saving = false
        this.$forceUpdate()
      })
    },
    showScript() {
      this.showBuilder = true
    },
    async getAgentTrigger() {
      this.loading = true
      let { error, data } = await API.get(`v3/trigger/${this.triggerData.id}`)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.triggerDetailsData = data.triggerContext || []
        if (!this.isNew) {
          this.trigger = {
            ...this.triggerDetailsData,
            name: this.triggerDetailsData.name,
            agentId: this.triggerDetailsData.agentId,
            criteria: this.triggerDetailsData.criteria,
            triggerActions: this.triggerDetailsData.triggerActions,
            status: this.triggerDetailsData.status,
          }
        }
      }
      this.loading = false
    },
    updateSelectedAgent(selectedAgent){
      let{trigger} = this
      trigger.agentId = selectedAgent.id
    }
  },
}
</script>
<style lang="scss">
  .select-agent-width {
  .fc-input-full-border-select2 {
    width: 100% !important;
  }
}
</style>
