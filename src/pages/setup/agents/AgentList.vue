<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Agents</div>
        <div class="heading-description">List of all agents</div>
      </div>
      <div class="action-btn setting-page-btn controller-right-actions">
        <f-search class="mL20" v-model="agents"></f-search>
        <!-- <div class="fc-separator-lg mL20 mR20"></div> -->
        <!-- <div @click="downloadCertificate" class="mR20 controller-download pointer"><i class="el-icon-download"></i></div> -->
        <!-- <el-button type="primary" @click="showDialog = true; isNew = true;" class="controller-add-btn">+</el-button> -->
        <NewAgent
          v-if="showDialog"
          :isNew="isNew"
          :model="agent"
          :visibility.sync="showDialog"
          @saved="onAgentCreated"
        ></NewAgent>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12 overflow-x">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text" style="width: 32%">
                  Agent
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 10%;"
                >
                  Controllers
                </th>
                <th class="setting-table-th setting-th-text">Location</th>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 18%;"
                >
                  Data Received
                </th>
                <th class="setting-table-th setting-th-text">Interval</th>
                <th class="setting-table-th setting-th-text">Writable</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="agents.length === 0">
              <tr>
                <td colspan="100%" class="text-center">No Agents available.</td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                @click="goToControllers(agent)"
                class="tablerow"
                v-for="(agent, index) in agents"
                :key="agent.id"
                v-loading="loading"
              >
                <td>
                  <div class="agent-active-section">
                    <AgentActivityDot
                      :key="agent.name"
                      :state="agent.state"
                      :status="agent.connectionStatus"
                    ></AgentActivityDot>
                    <div class="mL10">
                      <div class="label-txt3-14">
                        {{ agent.name }}
                        <div
                          v-if="agent.type"
                          class="fc-badge mL10 inline middle"
                        >
                          {{ agent.type }}
                        </div>
                      </div>
                      <div class="label-txt3-12 mT10">
                        <span class="agent-version">Status:</span>
                        {{
                          agent.connectionStatus ? 'Connected' : 'Disconnected'
                        }}
                      </div>

                      <el-popover
                        v-if="agent.deviceDetails"
                        placement="right"
                        width="200"
                        trigger="hover"
                      >
                        <div>
                          <div
                            v-for="(value, key, index) in JSON.parse(
                              agent.deviceDetails
                            )"
                            :key="index"
                            class="mT5 mb5"
                            style="word-break: break-all;"
                          >
                            {{ key }}: {{ value }}
                          </div>
                        </div>
                        <el-button
                          slot="reference"
                          type="text"
                          class="pT0 pB0 f11 fw-normal all-rule-btn agent-version"
                          >Version: {{ agent.version }}</el-button
                        >
                      </el-popover>
                    </div>
                  </div>
                </td>
                <td class="text-center">
                  <div class="label-txt3-14">{{ agent.controllerCount }}</div>
                </td>
                <td>
                  <div class="label-txt3-14">
                    {{
                      agent.siteId > 0
                        ? ($store.getters.getSite(agent.siteId) || {}).name
                        : '---'
                    }}
                  </div>
                </td>
                <td>
                  <div class="label-txt3-14">
                    {{ agent.lastDataReceivedTime | fromNow }}
                  </div>
                </td>
                <td>
                  <div class="label-txt3-14">
                    {{ agent.interval > -1 ? agent.interval + ' mins' : '---' }}
                  </div>
                </td>
                <td>
                  <el-switch
                    v-model="agent.writable"
                    @change="toggleWritable(agent, index)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>
                <td v-on:click.stop>
                  <el-dropdown @command="onOptionsSelect($event, agent, index)">
                    <span class="el-dropdown-link">
                      <i class="el-icon-more controller-more"></i>
                    </span>
                    <el-dropdown-menu
                      slot="dropdown"
                      class="controller-dropdown-item"
                    >
                      <el-dropdown-item command="edit">Edit</el-dropdown-item>
                      <!-- <el-dropdown-item command="delete">Delete</el-dropdown-item> -->
                      <el-dropdown-item command="logs"
                        >View Logs</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </el-dropdown>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewAgent from 'pages/setup/agents/NewAgent'
import FSearch from '@/FSearch'
import AgentActivityDot from 'pages/setup/agent/AgentActivityDot'
export default {
  title() {
    return 'Agents'
  },
  components: {
    NewAgent,
    FSearch,
    AgentActivityDot,
  },
  data() {
    return {
      isNew: true,
      loading: false,
      agents: [],
      showDialog: false,
      buildings: {},
      agent: null,
    }
  },
  mounted() {
    this.loadAgents()
  },
  methods: {
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/setup/agent/list')
        .then(response => {
          this.loading = false
          this.agents = response.data.result.agentDetails
            ? response.data.result.agentDetails
            : []
        })
        .catch(() => {
          this.agents = []
          this.loading = false
        })
    },
    downloadCertificate() {
      this.$http
        .get('/setup/downloadCertificate')
        .then(response => {
          window.location.href = response.data.url
        })
        .catch(() => {
          this.loading = false
        })
    },
    onAgentCreated() {
      this.loadAgents()
    },
    editAgent(agent) {
      this.isNew = false
      this.agent = this.$helpers.cloneObject(agent)
      this.showDialog = true
    },
    onOptionsSelect(command, agent, index) {
      if (command === 'edit') {
        this.editAgent(agent)
      } else if (command === 'delete') {
        this.deleteAgent(agent, index)
      } else if (command === 'logs') {
        this.goToLogs(agent)
      }
    },
    toggleWritable({ name, writable }, index) {
      let agentContext = { agent: name, writable }
      this.$http
        .post('/v2/setup/agent/edit', { agentContext })
        .then(response => {
          if (response.status === 200) {
            this.$message('Agent Updated')
          }
        })
        .catch(() => {
          this.$message('Could not update settings')
          this.agents[index].writable = !writable
        })
    },
    deleteAgent(agent, index) {
      this.$dialog
        .confirm({
          title: 'Delete Agent',
          htmlMessage: 'Are you sure you want to delete ' + agent.name + '?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/setup/agent/delete', { agentId: agent.id })
              .then(response => {
                if (response.data.responseCode === 0) {
                  this.$message.success('Agent deleted successfully')
                  this.agents.splice(index, 1)
                } else {
                  this.$message.error(response.data.message)
                }
              })
          }
        })
    },
    goToControllers({ id }) {
      this.$router.push({ path: 'controllers', query: { agentId: id } })
    },
    goToLogs({ id }) {
      this.$router.push({ path: 'logs', query: { agentId: id } })
    },
  },
}
</script>
<style scoped>
.agent-version {
  color: #a7a7ad;
}
.controller-right-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.controller-add-btn {
  color: #ffffff;
  border-radius: 0;
  font-size: 21px;
  font-weight: bold;
  padding: 6px 13px;
  background: #39b2c2;
  border: none;
}
.controller-add-btn:hover {
  background: #39b2c2;
}
.controller-download .el-icon-download {
  font-size: 20px;
  color: #1e201d;
  font-weight: 500;
}
.agent-active-section {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
}
.dropdown-controller-item .el-dropdown-menu__item {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #67666a;
  border-bottom: 1px solid rgba(151, 151, 151, 0.1);
}
</style>
