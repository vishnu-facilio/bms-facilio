<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            Configure Scripts
          </div>
        </div>
      </div>
      <div class="new-body-modal" v-if="agent">
        <template v-for="(action, idx) in actions">
          <el-row
            class="mT20"
            :key="idx"
            :gutter="10"
            v-if="action.showIf(agent)"
          >
            <el-col :span="10">
              <p class="details-Heading">{{ action.title }}</p>
              <p class="small-description-txt2">{{ action.description }}</p>
            </el-col>
            <el-col :span="8" class="mT20">
              <el-button
                type="button"
                v-bind:class="
                  action.workflow ? 'success-green-btn' : 'small-border-btn'
                "
                @click="!action.workflow ? configure(action) : null"
                >{{ action.workflow ? 'Configured' : 'Configure' }}</el-button
              >
              <span v-if="action.workflow" class="mL10">
                <i
                  class="el-icon-edit pointer"
                  @click="configure(action)"
                  title="Edit Script"
                  v-tippy
                ></i>
                <!-- <span class="mL10 reset-txt pointer" @click="reset(action)"
                >Reset</span
              > -->
              </span>
            </el-col>
          </el-row>
        </template>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="save"
          :loading="saving"
          class="modal-btn-save"
          >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
        >
      </div>
    </el-dialog>

    <f-dialog
      v-if="showBuilder"
      :visible.sync="showBuilder"
      width="60%"
      maxHeight="350px"
      :title="`Configure ${selectedAction.title}`"
      @save="changeWorkflow"
      confirmTitle="Confirm"
      class="script-dialog"
    >
      <div class="dialog-switch">
        <el-switch v-model="showDiff" active-text="Diff"></el-switch>
      </div>
      <!-- fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0 -->
      <div class="height350 overflow-y-scroll pB50">
        <script-editor 
         :scriptClass="'left-con p0  border-11 height100'" 
         v-model="currentWorkflow"
         :diff="showDiff &&{ type: 'side_by_side' }"
        >
        </script-editor>
      </div>
    </f-dialog>  
  </div>
</template>
<script>
import CodeMirror from '@/CodeMirror'
import FDialog from '@/FDialogNew'
import { API } from '@facilio/api'
import { ScriptEditor } from '@facilio/ui/setup'
const payloadHeader =
  'List getPayload(Number fromTime, Number toTime, Map agent) {\n'
const transformHeader = 'List getPayload(Map payload, Map agent) {\n'
const commandHeader =
  'Map getPayload(Map payload, Map agent, String command) {\n'

export default {
  props: ['visibility', 'agent'],
  components: {
    CodeMirror,
    FDialog,
    ScriptEditor
  },
  data() {
    return {
      showBuilder: false,
      currentWorkflow: '',
      selectedAction: null,
      currentAgent: null,
      actions: [
        {
          title: 'Data Payload Script',
          description:
            'Configure the payload script to fetch data from cloud agent',
          workflow: '',
          header: payloadHeader,
          comments:
            '// `fromTime` and `toTime` params will be passed to the script\n//return the payload as list\n\n',
          key: 'workflow',
          showIf(agent) {
            // if (this.agent) {
            //   console.log('-----ttt----', this.agent.agentType)
            // }
            return [3, 7].includes(agent?.agentType)
          },
        },
        {
          title: 'Transform Payload Script',
          description: 'Transform the payload message recevied from agent',
          workflow: '',
          header: transformHeader,
          comments:
            '// `payload` and `agent` params will be passed to the script\n//return the payload as list\n\n',
          key: 'transformWorkflow',
          showIf() {
            return true
          },
        },
        {
          title: 'Command Payload Script',
          description: 'The script to transform command payload',
          workflow: '',
          header: commandHeader,
          comments:
            '// `payload`, `agent`, `command` params will be passed to the script\n//return the payload as map\n\n',
          key: 'commandWorkflow',
          showIf(agent) {
            return ![1, 2, 6, 9].includes(agent?.agentType)
          },
        },
      ],
      saving: false,
      showDiff: false,
    }
  },
  async mounted() {
    let url = '/v3/agent/' + this.agent.id
    let { data, error } = await API.get(url)
    this.currentAgent = data.agent
    this.init()
  },
  methods: {
    init() {
      this.actions.forEach(action => {
        let workflow = this.$getProperty(
          this,
          `currentAgent.${action.key}.workflowV2String`
        )
        if (workflow) {
          action.workflow = workflow.replace(action.header, '').slice(0, -1)
        }
      })
    },
    configure(action) {
      this.currentWorkflow = ''
      if (action.workflow) {
        this.currentWorkflow = this.$helpers.cloneObject(action.workflow)
      } else {
        this.currentWorkflow = action.comments
      }
      this.selectedAction = action
      this.showBuilder = true
    },
    changeWorkflow() {
      this.selectedAction.workflow = this.currentWorkflow
    },
    save() {
      let editAgent = {
        agentId: this.agent.id,
        toUpdate: {},
      }
      this.actions.forEach(action => {
        if (action.workflow) {
          editAgent.toUpdate[action.key] = {
            isV2Script: true,
            workflowV2String: action.header + action.workflow + '\n}',
          }
        }
      })
      this.saving = true
      this.$http.post('/v2/agent/edit', editAgent).then(response => {
        if (response.data.responseCode !== 500) {
          this.$message('Script configured successfully')
          this.$emit('save')
          this.closeDialog()
        } else {
          this.$message.error(response.data.result.exception)
        }
        this.saving = false
      })
    },
    reset() {
      this.action.workflow = null
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style lang="scss">
.script-dialog {
  .f-dialog-content {
    padding: 0px !important;
  }
}
.dialog-switch {
  position: absolute;
  top: 20px;
  right: 60px;
  font-weight: bolder;
}
</style>
