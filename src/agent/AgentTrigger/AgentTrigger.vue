<template>
  <div class="page-width-cal agent-data-page">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div>
          <div class="fc-agent-black-26">
            {{ $t('agent.agent.agent_trigger') }}
          </div>
        </div>
        <div>
          <el-button class="fc-agent-add-btn" @click="addTrigger">
            <i class="el-icon-plus pR5 fwBold"></i>
            {{ $t('agent.agent.add_trigger') }}
          </el-button>
        </div>
      </div>
    </el-header>
    <div
      class="agent-list-scroll scrollbar-style"
      style="height: calc(100vh - 150px) !important;"
    >
      <div
        v-if="loading"
        class="flex-middle fc-empty-white m10 fc-agent-empty-state"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(triggerData) && !loading"
        class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column m10 fc-agent-empty-state"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="q-item-label nowo-label">
          {{ $t('agent.empty.no_trigger') }}
        </div>
      </div>
      <div class="fc-agent-table p10 fc-list-table-container" v-else>
        <el-table
          :fit="true"
          height="auto"
          style="width: 100%"
          class="fc-list-view fc-table-td-height fc-table-viewchooser pB100"
          :data="triggerData"
        >
          <el-table-column
            width="180"
            :label="$t('common.products.name')"
            prop="name"
          >
            <template v-slot="trigger">
              {{ trigger.row.name }}
            </template>
          </el-table-column>
          <el-table-column
            width="180"
            :label="$t('agent.agent.agent')"
            prop="agent"
          >
            <template v-slot="trigger">
              <div v-if="trigger.row.agent && trigger.row.agent.displayName">
                {{ trigger.row.agent && trigger.row.agent.displayName }}
              </div>
              <div v-else>
                ---
              </div>
            </template>
          </el-table-column>
          <el-table-column>
            <template v-slot="trigger">
              <el-switch
                @change="toggleWritable(trigger.row, trigger.row.status)"
                v-model="trigger.row.status"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#df5d5d"
              >
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column class="visibility-visible-actions">
            <template v-slot="trigger">
              <i
                class="el-icon-delete visibility-hide-actions"
                @click="deleteTrigger(trigger.row)"
              ></i>
              <i
                class="el-icon-edit visibility-hide-actions pL20"
                @click="editTrigger(trigger.row)"
              ></i>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <addTrigger
      v-if="showTrigger"
      @onClose="showTrigger = false"
      :isNew="isNew"
      :triggerData="selectedTrigger"
      @onSave="getAgentTrigger"
    >
    </addTrigger>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import addTrigger from 'agent/AgentTrigger/AgentTriggerForm'
export default {
  data() {
    return {
      triggerData: [],
      showTrigger: false,
      isNew: false,
      loading: true,
      agentData: [],
      selectedTrigger: null,
    }
  },
  title() {
    return 'Agent Trigger'
  },
  created() {
    this.getAgentTrigger()
    this.getAgentList()
  },
  components: {
    addTrigger,
  },
  computed: {},
  methods: {
    async getAgentTrigger() {
      this.loading = true
      let { error, data } = await API.get(`v3/trigger/agent`)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.triggerData = data.triggerList
      }
      this.loading = false
    },
    addTrigger() {
      this.showTrigger = true
      this.isNew = true
      this.selectedTrigger = null
    },
    async getAgentList() {
      this.loading = true
      let { error, data } = await API.get('/v2/agent/list')
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.agentData = data
      }
      this.loading = false
    },
    async toggleWritable(trigger) {
      let { id, status: isActive } = trigger
      let url = 'v3/trigger/changeStatus'
      let params = {
        id: id,
        status: trigger.status,
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(
          isActive ? 'Trigger marked as active' : 'Trigger marked as inactive'
        )
      }
    },
    editTrigger(trigger) {
      this.isNew = false
      this.showTrigger = true
      this.selectedTrigger = trigger
    },
    deleteTrigger(trigger) {
      this.$dialog
        .confirm({
          title: this.$t('agent.delete.trigger_delete'),
          message: this.$t(
            'agent.delete.are_you_sure_you_want_to_delete_this_trigger'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(async value => {
          if (!value) return

          let { error } = await API.post('v3/trigger/delete', {
            id: trigger.id,
          })

          if (error) {
            this.$message.error(
              error.message || this.$t('agent.delete.trigger_deletion_failed')
            )
          } else {
            this.$message.success(
              this.$t('agent.delete.trigger_deleted_successfully')
            )
            this.getAgentTrigger()
          }
        })
    },
  },
}
</script>
