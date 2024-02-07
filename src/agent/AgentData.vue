<template>
  <div class="page-width-cal agent-data-page">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div>
          <div class="fc-agent-black-26">
            {{ $t('agent.agent.agent_data') }}
          </div>
        </div>
        <div>
          <div class="fc-dark-blue5 f13 bold">
            <span class="fc-black-com f12 pR5 bold">
              {{ $t('agent.agent.last_received') }}
            </span>
            <span v-if="$validation.isEmpty(agents)">---</span>
            <span v-else-if="!$validation.isEmpty(agents)">
              {{ agentLastReceivedTime.lastDataReceivedTime | formatDate }}
              <div class="text-right f11">
                {{ agentLastReceivedTime.lastDataReceivedTime | fromNow }}
              </div>
            </span>
          </div>
        </div>
      </div>
    </el-header>
    <div class="agent-list-scroll scrollbar-style">
      <!-- button groups -->
      <div
        v-if="loading"
        class="flex-middle fc-empty-white m10 fc-agent-empty-state"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(agents) && !loading"
        class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column m10 fc-agent-empty-state"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="q-item-label nowo-label">
          {{ $t('agent.empty.no_agent_data') }}
        </div>
      </div>
      <div
        class="fc-agent-table p10 fc-list-table-container"
        v-if="!loading && !$validation.isEmpty(agents)"
      >
        <el-table
          :data="agents"
          :fit="true"
          height="auto"
          style="width: 100%"
          class="fc-list-view fc-table-td-height fc-table-viewchooser pB100"
          @selection-change="selectAgent"
          :default-sort="{ prop: 'time', order: 'descending' }"
        >
          <el-table-column
            type="index"
            width="90"
            fixed="left"
            label="s.no"
          ></el-table-column>
          <el-table-column width="180" label="data time" sortable prop="time">
            <template v-slot="agent">
              <div v-if="agent.row.timestamp">
                <div>{{ agent.row.timestamp | formatDate }}</div>
                <div>{{ agent.row.timestamp | fromNow }}</div>
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column
            width="180"
            label="received time"
            sortable
            prop="time"
          >
            <template v-slot="agent">
              <div v-if="agent.row.arrivalTime">
                <div>{{ agent.row.arrivalTime | formatDate }}</div>
                <div>{{ agent.row.arrivalTime | fromNow }}</div>
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column label="agent" width="180" class="pL0">
            <template v-slot="agent">{{ agent.row.device }}</template>
          </el-table-column>
          <el-table-column label="data">
            <template v-slot="agent">
              <div>
                <div
                  @click="showIotMessage(agent.row)"
                  class="mL10 fc-pdf-blue-txt-13"
                >
                  {{ $t('agent.agent.show_message') }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <el-dialog
      v-if="showMsgJson && showMsgJson.id"
      title="Message"
      :visible.sync="showMsgPopup"
      width="40%"
      :append-to-body="true"
      style="z-index: 9999999999;"
      class="agents-dialog fc-dialog-center-container"
    >
      <div
        class="label-txt-black line-height24 height400 overflow-y-scroll pB50"
      >
        <pre class="fc-json-view">{{ showMsgJson.message }}</pre>
      </div>
    </el-dialog>
    <div class="help-icon-position pointer" @click="helpDialogOpen()">
      <inline-svg
        src="svgs/question"
        class="vertical-middle"
        iconClass="icon icon-xxlll"
      ></inline-svg>
    </div>
    <agent-help
      v-if="showHelpDialog"
      :visibility.sync="showHelpDialog"
    ></agent-help>
  </div>
</template>
<script>
import AgentHelp from 'agent/AgentHelperDocs/HelpData'
export default {
  title() {
    return 'Agent Data'
  },
  components: {
    AgentHelp,
  },
  data() {
    return {
      showHelpDialog: false,
      isNew: true,
      loading: false,
      agents: [],
      showDialog: false,
      buildings: {},
      agent: null,
      listCount: '',
      totalCount: null,
      selectall: false,
      selectedAgent: [],
      total: null,
      selectedAgentShutdownObj: [],
      selectedAgentShutdown: [],
      showMsgPopup: false,
      showMsgJson: null,
      agentLastReceivedTime: [],
      readMore: false,
    }
  },
  mounted() {
    this.loadAgents()
  },
  computed: {},
  watch: {
    selectAll: function(val) {
      let self = this
      if (val) {
        this.selectedAgent = []
        this.agents.filter(function(agent) {
          self.selectedAgent.push(agent.id)
        })
      } else {
        if (this.selectedAgent.length === this.agents.length) {
          this.selectedAgent = []
        }
      }
      this.loadCount()
    },
  },
  methods: {
    activateReadMore() {
      this.readMoreSelected = true
    },
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/agent/data')
        .then(response => {
          this.loading = false
          this.agents =
            response.data.result && response.data.result.data
              ? response.data.result.data
              : []
          this.agentLastReceivedTime = response.data.result
        })
        .catch(() => {
          this.agents = []
          this.loading = false
        })
    },
    selectAgent(selectedSh) {
      this.selectedAgentShutdown = selectedSh.map(value => value.id)
    },
    agentShutdown() {
      this.loading = true
      let shutdown = {
        recordIds: this.selectedAgentShutdown,
      }
      this.$http.post('/v2/agent/shutdown', shutdown).then(response => {
        if (response.data.responseCode === 200) {
          this.$message.success('Shutdown updated successfully.')
          this.loading = false
        } else {
          this.$message.error(response.data.message)
          this.loading = false
        }
      })
    },
    showAddAgent() {
      this.showDialog = true
    },
    showIotMessage(agent) {
      this.showMsgPopup = true
      this.showMsgJson = {
        ...agent,
        message: JSON.stringify(JSON.parse(agent.message), null, 2).trim(),
      }
    },
    helpDialogOpen() {
      this.showHelpDialog = true
    },
  },
}
</script>
<style lang="scss">
.agent-data-page {
  .agent-list-scroll .el-table {
    height: calc(100vh - 150px) !important;
  }
}
</style>
