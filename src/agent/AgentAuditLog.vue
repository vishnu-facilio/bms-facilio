<template>
  <div class="page-width-cal">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div class="fc-agent-black-26">
          {{ $t('agent.agent.command_logs') }}
        </div>
      </div>
    </el-header>
    <div class="flex-middle justify-content-space">

      <SelectAgent class="fc-input-full-border-select2 mL10 mT10"
      @onAgentFilter = "updateAgentId"
      ></SelectAgent>

      <div class="flex-middle mT15">
        <pagination
          :total="totalCount"
          :perPage="perPage"
          ref="f-page"
          class ="mT5 mR10"
        ></pagination>
        <span
          class="pointer fwBold f16"
          @click="listRefresh"
          v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
          content="Refresh"
        >
          <i class="el-icon-refresh fwBold f16"></i>
        </span>
        <f-search class="mL20" v-model="agentLogs"></f-search>
      </div>
    </div>
    <div
      v-if="loading"
      class="flex-middle fc-empty-white m10 fc-agent-empty-state"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-if="$validation.isEmpty(agentLogs) && !loading"
      class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('agent.empty.no_command_logs') }}
      </div>
    </div>
    <div
      class="fc-list-view pT0 fc-list-table-container fc-table-td-height fc-table-viewchooser pB100"
    >
      <div
        class="fc-agent-table p10 agent-list-scroll scrollbar-style"
        v-if="!loading && !$validation.isEmpty(agentLogs)"
      >
        <el-table
          ref="multipleSelection"
          :data="agentLogs"
          height="auto"
          style="width: 100%"
          :default-sort="{ prop: 'date', order: 'descending' }"
          class="fc-table-th-pLalign-reduce"
          header-cell-class-name="fc-log-header"
        >
          <el-table-column
            label="Command"
            sortable
            prop="command"
            :render-header="renderCommandHeader"
          >
            <template v-slot="agentLog">
              {{
                $constants.Agentcommands[agentLog.row.command]
                  ? $constants.Agentcommands[agentLog.row.command]
                  : '---'
              }}
              <!-- {{controller.row.command}} -->
            </template>
          </el-table-column>
          <el-table-column
            label="sent"
            width="200"
            sortable
            prop="sent"
            :render-header="renderSentHeader"
          >
            <template v-slot="command">
              <div v-if="command.row.sentTime">
                {{ command.row.sentTime | formatDate() }}
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column
            label="ack"
            width="200"
            sortable
            prop="ack"
            :render-header="renderAckHeader"
          >
            <template v-slot="agentLog">
              <div v-if="agentLog.row.ackTime">
                {{ agentLog.row.ackTime | formatDate() }}
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column
            label="Completed"
            width="200"
            sortable
            prop="completed"
            :render-header="renderCompletedHeader"
          >
            <template v-slot="agentLog">
              <div v-if="agentLog.row.completedTime">
                {{ agentLog.row.completedTime | formatDate() }}
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column
            label="status"
            sortable
            prop="status"
            :render-header="renderStatusHeader"
          >
            <template v-slot="agentLog">
              <div
                v-bind:class="{
                  'fc-green-label14':
                    $constants.AgentLogStatus[agentLog.row.status] ===
                    'Success',
                  'fc-red-status':
                    $constants.AgentLogStatus[agentLog.row.status] === 'Failed',
                  'fc-orange-status':
                    $constants.AgentLogStatus[agentLog.row.status] === 'Sent',
                }"
                class="fw4"
              >
                {{
                  $constants.AgentLogStatus[agentLog.row.status]
                    ? $constants.AgentLogStatus[agentLog.row.status]
                    : '---'
                }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="message" width="200" prop="message">
            <template v-slot="agentLog">
              <div
                class="fc-agent-show-txt bold"
                @click="showIotMessage(agentLog.row)"
              >
                Show Message
              </div>
            </template>
          </el-table-column>
        </el-table>
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
        <div class="label-txt-black line-height24">
          <pre class="fc-json-view">{{ showMsgJson.msgData }}</pre>
        </div>
      </el-dialog>
    </div>
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
import FSearch from '@/FSearch'
import AgentHelp from 'agent/AgentHelperDocs/HelpLog'
import Pagination from 'src/components/list/FPagination'
import SelectAgent from 'src/agent/components/SelectAgent.vue'
export default {
  title() {
    return 'Agent Logs'
  },
  components: {
    FSearch,
    AgentHelp,
    Pagination,
    SelectAgent,
  },
  data() {
    return {
      loading: false,
      agentLogs: [],
      agentLog: null,
      agents: [],
      agent: null,
      selectedAgentId: null,
      showMsgPopup: false,
      showMsgJson: null,
      showHelpDialog: false,
      totalCount: null,
      perPage: 50,
    }
  },
  created() {
    this.loadAgents()
  },
  computed: {
    selectedAgentDetails() {
      return this.selectedAgentId
        ? this.agents.find(agent => agent.id === this.selectedAgentId)
        : {}
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    selectedAgentId(val) {
      this.$router.push({
        query: {
          agentId: val,
        },
      })
    },
    page() {
      this.loadLog()
    },
  },
  methods: {
    helpDialogOpen() {
      this.showHelpDialog = true
    },
    loadLog() {
      this.loading = true

      let url = `/v2/agent/getIotMessages?agentId=${this.selectedAgentId}&page=${this.page}&perPage=${this.perPage}`
      this.$http
        .get(url)
        .then(response => {
          this.loading = false
          this.agentLogs = response.data.result.data
            ? response.data.result.data
            : []
        })
        .catch(() => {
          this.agentLogs = []
          this.loading = false
        })
    },
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/agent/list')
        .then(response => {
          this.agents = response.data.result.data
            ? response.data.result.data
            : []
          this.selectedAgentId =
            parseInt(this.$route.query.agentId) || this.agents[0].id
          this.loadLog()
          this.loadCount()
        })
        .catch(() => {
          this.loading = false
        })
    },
    showIotMessage(iotMessage) {
      this.showMsgPopup = true
      this.showMsgJson = {
        ...iotMessage,
        msgData: JSON.stringify(JSON.parse(iotMessage.msgData), null, 2).trim(),
      }
      // this.selectedIndex = JSON.stringify(this.showMsgJson)
    },
    loadCount() {
      return this.$http
        .get(`/v2/agent/iotMessageCount?agentId=${this.selectedAgentId}`)
        .then(response => {
          this.totalCount = response.data.result.data || null
        })
        .catch(() => {
          this.loading = false
        })
    },
    listRefresh() {
      this.loadLog()
      this.loadCount()
    },
    // dynamic tooltip
    renderCommandHeader(h, { column }) {
      return h(
        'span',
        {
          class: 'fc-table-tool-tip',
        },
        [
          column.label + ' ',
          h(
            'el-tooltip',
            {
              class: 'fc-table-tool-tip',
              attrs: {
                content: 'Command sent from the server to the agent.',
                effect: 'dark',
                placement: 'top-start',
              },
            },
            [
              h('i', {
                class: 'far el-icon-info',
              }),
            ]
          ),
        ]
      )
    },
    renderSentHeader(h, { column }) {
      return h(
        'span',
        {
          class: 'fc-table-tool-tip',
        },
        [
          column.label + ' ',
          h(
            'el-tooltip',
            {
              class: 'fc-table-tool-tip',
              attrs: {
                content: 'Time the command was sent from the server',
                effect: 'dark',
                placement: 'top-start',
              },
            },
            [
              h('i', {
                class: 'far el-icon-info',
              }),
            ]
          ),
        ]
      )
    },
    renderAckHeader(h, { column }) {
      return h(
        'span',
        {
          class: 'fc-table-tool-tip',
        },
        [
          column.label + ' ',
          h(
            'el-tooltip',
            {
              class: 'fc-table-tool-tip',
              attrs: {
                content: 'Acknowledgement time received by the agent.',
                effect: 'dark',
                placement: 'top-start',
              },
            },
            [
              h('i', {
                class: 'far el-icon-info',
              }),
            ]
          ),
        ]
      )
    },
    renderCompletedHeader(h, { column }) {
      return h(
        'span',
        {
          class: 'fc-table-tool-tip',
        },
        [
          column.label + ' ',
          h(
            'el-tooltip',
            {
              class: 'fc-table-tool-tip',
              attrs: {
                content:
                  'Time at which the agent completed the command execution.',
                effect: 'dark',
                placement: 'top-start',
              },
            },
            [
              h('i', {
                class: 'far el-icon-info',
              }),
            ]
          ),
        ]
      )
    },
    renderStatusHeader(h, { column }) {
      return h(
        'span',
        {
          class: 'fc-table-tool-tip',
        },
        [
          column.label + ' ',
          h(
            'el-tooltip',
            {
              class: 'fc-table-tool-tip',
              attrs: {
                content: 'Status of the command execution',
                effect: 'dark',
                placement: 'top-start',
              },
            },
            [
              h('i', {
                class: 'far el-icon-info',
              }),
            ]
          ),
        ]
      )
    },
    updateAgentId(selectedAgent){
      this.selectedAgentId = selectedAgent.id
      this.listRefresh()
    }
  },
}
</script>
<style>
.fc-table-tool-tip .el-icon-info {
  visibility: hidden;
}

.fc-table-td-height
  .el-table
  th.is-leaf:hover
  .fc-table-tool-tip
  .el-icon-info {
  visibility: visible;
}
</style>
