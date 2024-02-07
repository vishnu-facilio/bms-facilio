<template>
  <div class="page-width-cal fc-agent-metrics-table">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div class="fc-agent-black-26">
          {{ $t('agent.agent.agent_metrics') }}
        </div>
      </div>
    </el-header>

    <div class="flex-middle justify-content-space">
      <el-select
        v-model="selectedAgentId"
        filterable
        default-first-option
        @change="listRefresh"
        placeholder="Select Agent"
        width="250px"
        class="fc-input-full-border-select2 mL10 mT10"
      >
        <el-option
          v-for="(agent, index) in agents"
          :key="index"
          :label="agent.displayName || agent.name"
          :value="agent.id"
          no-data-text="No controllers available"
          clearable
        ></el-option>
      </el-select>
      <div class="flex-middle">
        <span class="pointer fwBold pR10 f16" @click="listRefresh">
          <i
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Refresh"
            class="el-icon-refresh fwBold f16"
          ></i>
        </span>
        <pagination
          :total="totalCount"
          :perPage="perPage"
          ref="f-page"
        ></pagination>
        <f-search class="mL20" v-model="agentMetrics"></f-search>
      </div>
    </div>
    <div
      v-if="loading"
      class="flex-middle fc-empty-white m10 fc-agent-empty-state"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-if="$validation.isEmpty(agentMetrics) && !loading"
      class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">
        {{ $t('agent.empty.no_metrics') }}
      </div>
    </div>

    <div
      class="fc-agent-table p10"
      v-if="!loading && !$validation.isEmpty(agentMetrics)"
    >
      <el-table
        ref="multipleSelection"
        :data="agentMetrics"
        height="auto"
        :fit="true"
        style="width: 100%;"
        class="fc-list-view fc-table-th-minus pT0 mT10 fc-list-table-container fc-table-td-height fc-table-viewchooser pB100 agentmetricsTableHeight fc-agent-table-notify"
      >
        <el-table-column label="Time" width="200" prop="agentMetric" sortable>
          <template v-slot="agentMetric">
            {{ agentMetric.row.createdTime | formatDate }}
          </template>
        </el-table-column>
        <el-table-column label="publish Type" width="180">
          <template v-slot="agentMetric">
            {{ $constants.AgentPublishType[agentMetric.row.publishType] }}
          </template>
        </el-table-column>
        <el-table-column label="Messages" width="130">
          <template v-slot="agentMetric">
            {{ agentMetric.row.numberOfMessages }}
          </template>
        </el-table-column>
        <el-table-column label="size" width="130">
          <template v-slot="agentMetric">
            {{ bytesToSize(agentMetric.row.size) }}
          </template>
        </el-table-column>
      </el-table>
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
import AgentHelp from 'agent/AgentHelperDocs/HelpMetrics'
import Pagination from 'src/components/list/FPagination'
export default {
  title() {
    return 'Agent Metrics'
  },
  components: {
    FSearch,
    AgentHelp,
    Pagination,
  },
  data() {
    return {
      loading: false,
      agentMetrics: [],
      agentMetric: null,
      showHelpDialog: false,
      agents: [],
      agent: null,
      selectedAgentId: null,
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
      this.loadMetrics()
    },
  },
  methods: {
    loadMetrics() {
      this.loading = true
      let url = `/v2/agent/metrics?agentId=${this.selectedAgentId}&page=${this.page}&perPage=${this.perPage}`
      this.$http
        .get(url)
        .then(response => {
          this.loading = false
          this.agentMetrics = response.data.result.data
            ? response.data.result.data
            : []
        })
        .catch(() => {
          this.loading = false
        })
    },
    listRefresh() {
      this.loadMetrics()
      this.loadCount()
    },
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/agent/list')
        .then(response => {
          this.loading = false
          this.agents = response.data.result.data
            ? response.data.result.data
            : []
          this.selectedAgentId =
            parseInt(this.$route.query.agentId) || this.agents[0].id
          this.loadMetrics()
          this.loadCount()
        })
        .catch(() => {
          this.loading = false
        })
    },
    bytesToSize(bytes) {
      let sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
      if (bytes == 0) return '0 Byte'
      let i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)))
      return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i]
    },
    loadCount() {
      return this.$http
        .get(`/v2/agent/metricsCount?agentId=${this.selectedAgentId}`)
        .then(response => {
          this.totalCount = response.data.result.data || null
        })
        .catch(() => {
          this.loading = false
        })
    },
    helpDialogOpen() {
      this.showHelpDialog = true
    },
  },
}
</script>

<style lang="scss">
.fc-agent-table {
  table {
    width: 100% !important;
  }
}
</style>
