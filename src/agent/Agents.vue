<template>
  <div class="width100">
    <div v-if="!id" class="page-width-cal">
      <el-header class="fc-agent-main-header" height="80">
        <div class="flex-middle justify-content-space">
          <div>
            <div class="fc-agent-black-26">{{ $t('agent.agent.agents') }}</div>
          </div>
          <div
            class="fc-red-new bold pL10 fc-text-blink text-center"
            v-if="total"
          >
            {{ total }} {{ $t('agent.agent.agent_offline') }}
          </div>
          <div class="flex-middle">
            <el-button class="fc-agent-add-btn" @click="showAddAgent()">
              <i class="el-icon-plus pR5 fwBold"></i>
              {{ $t('agent.agent.add_agent') }}
            </el-button>
          </div>
        </div>
      </el-header>
      <div class="agent-list-scroll scrollbar-style">
        <!-- button groups -->
        <div class="flex-middle mL15 mT20 justify-content-space">
          <div>
            <el-button
              :disabled="!isConnectionSame"
              v-if="disableAgentButton"
              type="button"
              class="fc-white-btn"
              @click="toggleJob"
              >{{ toggleJobButton }}
            </el-button>
            <el-button
              :disabled="disableDiscoverController"
              v-if="disableControllerButton"
              type="button"
              class="fc-white-btn mR10"
              @click="discoverControllerDialog"
              >{{ 'Discover Controller' }}</el-button
            >
          </div>
          <div class="flex-middle">
            <AdvancedSearch
              key="agent-search"
              :moduleName="moduleName"
              :moduleDisplayName="moduleDisplayName"
            >
            </AdvancedSearch>
            <span class="separator">|</span>
            <pagination :total="totalCount" :perPage="perPage"> </pagination>
            <span class="separator">|</span>
            <span
              class="pointer fwBold pR10 f16"
              @click="listRefresh"
              v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
              content="Refresh"
            >
              <i class="el-icon-refresh fwBold f16"></i>
            </span>
          </div>
        </div>
        <FTags :key="moduleName"></FTags>
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
            {{ $t('agent.agent.no_agents') }}
          </div>
        </div>
        <div class="agent-list-container">
          <div
            class="fc-agent-table p10 fc-agents-list-table fc-list-table-container"
            :class="agentTableHeight()"
            v-if="!loading && !$validation.isEmpty(agents)"
          >
            <el-table
              :data="agents"
              :fit="true"
              height="auto"
              style="width: 100%"
              @selection-change="selectAgent"
              ref="singleTable"
            >
              <el-table-column
                type="selection"
                width="60"
                fixed="left"
                class="pR0"
                v-model="selectall"
                :selectable="agentSelectCheck"
              ></el-table-column>
              <el-table-column
                label="agent"
                width="350"
                fixed="left"
                class="pL0"
                sortable
                prop="agent"
              >
                <template v-slot="agent" prop="agentstatus">
                  <div
                    class="agent-active-section"
                    @click="agentSummary(agent.row)"
                  >
                    <div class="flex-middle">
                      <AgentActivityDot
                        :key="agent.row.name"
                        :status="agent.row.connected"
                      ></AgentActivityDot>
                      <div class="mL10">
                        <div class="label-txt3-14 flex-middle">
                          <div class="main-field-column bold">
                            {{
                              agent.row.displayName
                                ? agent.row.displayName
                                : agent.row.name
                            }}
                          </div>
                          <div class="fc-badge mL10 inline middle nowrap">
                            {{ $constants.AgentTypes[agent.row.agentType] }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <!-- <el-table-column label="status" width="130">
            <template v-slot="agent">
              <div class="label-txt3-12 mT10">
                {{ agent.row.connectionStatus ? 'Connected' : 'Disconnected' }}
              </div>
            </template>
            </el-table-column>-->
              <el-table-column label="Link Name" width="150">
                <template v-slot="agent">
                  <div class="label-txt3-14">{{ agent.row.name }}</div>
                </template>
              </el-table-column>
              <el-table-column label="Controllers" width="150">
                <template v-slot="agent">
                  <div class="label-txt3-14">{{ agent.row.controllers }}</div>
                </template>
              </el-table-column>
              <el-table-column label="Location" width="150">
                <template v-slot="agent">
                  <div class="label-txt3-14">
                    {{
                      agent.row.siteId > 0 ? siteList[agent.row.siteId] : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="DATA RECEIVED" width="200">
                <template v-slot="agent">
                  <div
                    class="label-txt3-14"
                    v-if="agent.row.lastDataReceivedTime"
                  >
                    {{ agent.row.lastDataReceivedTime | fromNow }}
                  </div>
                  <div v-else>
                    ---
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="INTERVAL" width="150">
                <template v-slot="agent">
                  <div class="label-txt3-14">
                    {{
                      agent.row.interval > -1
                        ? agent.row.interval + ' mins'
                        : '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="WRITABLE" width="130">
                <template v-slot="agent">
                  <el-switch
                    v-model="agent.row.writable"
                    @change="toggleWritable(agent.row, agent.row.writable)"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                  <!-- <el-switch v-model="agent.row.writable" @change="toggleWritable(agent.row, agent.row.agent)" class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)" inactive-color="#e5e5e5"></el-switch>-->
                </template>
              </el-table-column>
              <el-table-column width="100" class="visibility-visible-actions">
                <template v-slot="agent">
                  <div class="visibility-hide-actions text-right mR30">
                    <el-dropdown @command="onOptionsSelect($event, agent.row)">
                      <span class="el-dropdown-link">
                        <i class="el-icon-more controller-more"></i>
                      </span>
                      <el-dropdown-menu
                        slot="dropdown"
                        class="controller-dropdown-item"
                      >
                        <el-dropdown-item command="edit">{{
                          $t('agent.agent.edit')
                        }}</el-dropdown-item>
                        <el-dropdown-item command="delete">{{
                          $t('agent.agent.delete')
                        }}</el-dropdown-item>

                        <el-dropdown-item
                          command="configure"
                          >{{ $t('agent.agent.configure') }}</el-dropdown-item
                        >
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
      <new-agent
        v-if="showDialog"
        :isNew="isNew"
        :model="agent"
        :visibility.sync="showDialog"
        @saved="onAgentCreated"
      ></new-agent>
      <rest-agent-configure
        v-if="showConfigureAgent"
        :agent="agent"
        :visibility.sync="showConfigureAgent"
        @save="loadAgents"
      ></rest-agent-configure>
      <router-view @syncCount="setCount"></router-view>
      <div class="help-icon-position pointer" @click="helpDialogOpen()">
        <inline-svg
          src="svgs/question"
          class="vertical-middle"
          iconClass="icon icon-xxlll"
        ></inline-svg>
      </div>
      <agent-help
        v-if="helpDialogVisible"
        :helpDialogVisible.sync="helpDialogVisible"
      ></agent-help>
      <!-- Discover Dialog -->
      <el-dialog
        title="Discover Controller"
        :visible.sync="discoverDialogVisible"
        width="30%"
        :before-close="handleClose"
        class="fc-dialog-center-container"
        :append-to-body="true"
      >
        <div :class="discoverControllersDialogHeight">
          <el-form label-position="top" ref="agentForm">
            <el-form-item class="mB20" label="Agent">
              <div v-if="discoverDialogVisible">
                <SelectAgent
                  @onAgentFilter="updateAgentId"
                  discoverableAgents="true"
                  hideLabel="true"
                  filterActiveAgents="true"
                  class="select-agent-width"
                >
                </SelectAgent>
              </div>
            </el-form-item>
            <el-form-item class="mB20" :required="true" label="Protocol"  v-if="this.selectedAgent != null && ( this.selectedAgent.agentType != 9 && this.selectedAgent.agentType != 6)">
              <el-select
                v-model="selectedType"
                placeholder="Select Type"
                class="fc-input-full-border-select2 width100"
              >
                <div
                  v-if="
                    this.selectedAgent != null &&
                      this.selectedAgent.agentType == 2
                  "
                >
                  <el-option
                    v-for="item in niagaraOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </div>
                <div
                  v-else-if="
                    this.selectedAgent != null &&
                      this.selectedAgent.agentType == 1
                  "
                >
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </div>
                <div
                  v-else-if="
                    this.selectedAgent != null &&
                      this.selectedAgent.agentType == 9
                  "
                >
                  <el-option
                    v-for="item in e2Options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </div>
                <div
                  v-else-if="
                    this.selectedAgent != null &&
                      this.selectedAgent.agentType == 6
                  "
                >
                  <el-option
                    v-for="item in rdmOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </div>
              </el-select>
            </el-form-item>
            <el-form-item
              :required="true"
              label="IP Address"
              v-if="selectedType == 14"
            >
              <el-input
                type="text"
                v-model="controllerIpAddress"
                placeholder="Enter IP Address"
                class="fc-input-full-border2 width100"
              ></el-input>
            </el-form-item>
            <el-form-item
              :required="true"
              label="Port"
              v-if="selectedType == 14"
            >
              <el-input
                type="text"
                v-model="controllerPort"
                placeholder="Enter Port"
                class="fc-input-full-border2 width100"
              ></el-input>
            </el-form-item>
            <div
              class="pB10"
              v-if="selectedType == 1 && this.selectedAgent.agentType == 1"
            >
              Advanced Options
              <el-switch
                v-model="isAdvancedDiscoveryEnabled"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
              <div v-if="isAdvancedDiscoveryEnabled" class="pT10">
                <el-form-item :required="true" label="UDP Port">
                  <el-select
                    v-model="bacnetPort"
                    class="fc-input-full-border-select2  width100"
                  >
                    <el-option
                      v-for="item in bacnetPorts"
                      :key="item"
                      :label="item"
                      :value="item"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item :required="false" label="Timeout (Mins)">
                  <el-select
                    v-model="timeout_sec"
                    class="fc-input-full-border-select2  width100"
                  >
                    <el-option
                      v-for="item in discoverControllersTimeout"
                      :key="item"
                      :label="item"
                      :value="item"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  :required="false"
                  label="Instance Number Range"
                  style="width: 100%;"
                >
                  <el-radio label="all" v-model="IsfullRange">
                    <span class="radio-button-padding">All the devices</span>
                  </el-radio>
                  <br />
                  <el-radio
                    lable="btwrange"
                    v-model="IsfullRange"
                    class="radio-lable"
                  >
                    <div class="d-flex width100">
                      <span class="instance-radio-lable">From</span>
                      <el-input
                        type="number"
                        v-model="instanceNumberFrom"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                      <span class="instance-radio-lable">To</span>
                      <el-input
                        type="number"
                        v-model="instanceNumberTo"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-radio>
                </el-form-item>
              </div>
            </div>
          </el-form>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="handleClose()" class="modal-btn-cancel">{{
            $t('agent.agent.cancel')
          }}</el-button>
          <el-button
            type="primary"
            class="modal-btn-save"
            @click="discoverDevices()"
            :disabled="handleDisable"
            >{{ $t('agent.agent.discover') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <router-view v-else></router-view>
  </div>
</template>
<script>
import NewAgent from 'agent/components/AgentAddForm'
import RestAgentConfigure from 'agent/components/RestAgentConfigure'
import AgentActivityDot from 'agent/components/AgentActivity'
import AgentHelp from 'src/agent/AgentHelperDocs/AgentHelp'
import { API } from '@facilio/api'
import { isEmpty, isNull } from '@facilio/utils/validation'
import { findRouteForTab, tabTypes, getApp } from '@facilio/router'
import Pagination from 'src/components/list/FPagination'
import { getFieldOptions } from 'util/picklist'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import FTags from 'newapp/components/search/FTags'
import SelectAgent from 'src/agent/components/SelectAgent.vue'

const AGENT_TYPE = {
  Facilio: 1,
  Niagara: 2,
  Cloud: 3,
  RDM: 6,
  CloudService: 7,
  E2: 9,
}

export default {
  title() {
    return 'Agent List'
  },
  props: ['id'],
  components: {
    NewAgent,
    AgentActivityDot,
    AgentHelp,
    RestAgentConfigure,
    Pagination,
    AdvancedSearch,
    FTags,
    SelectAgent,
  },
  data() {
    return {
      isAdvancedDiscoveryEnabled: false,
      IsfullRange: 'all',
      isConnectionSame: null,
      isNew: true,
      loading: false,
      agents: [],
      showDialog: false,
      buildings: {},
      agent: null,
      selectedType: null,
      showConfigureAgent: false,
      listCount: '',
      totalCount: null,
      selectall: false,
      helpDialogVisible: false,
      selectedAgent: [],
      total: null,
      direction: 'rtl',
      drawer: false,
      selectedAgents: [],
      selectedAgentId: null,
      discoverDialogVisible: false,
      siteList: {},
      controllerIpAddress: null,
      controllerPort: null,
      instanceNumberFrom: 0,
      instanceNumberTo: 4194302,
      bacnetPort: 47808,
      timeout_sec: 1,
      wholeRange: null,
      bacnetPorts: [
        47808,
        47809,
        47810,
        47811,
        47812,
        47813,
        47814,
        47815,
        47816,
        47817,
      ],
      discoverControllersTimeout: [1, 2, 3, 5, 8, 10, 15],
      options: [
        {
          value: 1,
          label: 'BACnet Ip',
        },
        {
          value: 12,
          label: 'System',
        },
      ],
      e2Options: [
        {
          value: 14,
          label: 'E2',
        },
      ],
      rdmOptions: [
        {
          value: 13,
          label: 'RDM',
        },
      ],
      value: -1,
      niagaraOptions: [
        {
          value: 0,
          label: 'Misc',
        },
        {
          value: 1,
          label: 'BACnet Ip',
        },
        {
          value: 3,
          label: 'Fox',
        },
        {
          value: 5,
          label: 'Modbus_Rtu',
        },
        {
          value: 8,
          label: 'Lon Works',
        },
      ],
      perPage: 50,
      moduleName: 'agent',
      moduleDisplayName: 'Agent',
      disableDiscoverController: false,
    }
  },
  async mounted() {
    await this.loadAgents()
  },
  computed: {
    discoverControllersDialogHeight() {
      return this.selectedAgent!=null && ( this.selectedAgent.agentType == 9 || this.selectedAgent.agentType == 6 )
        ? 'height200'
        : this.isAdvancedDiscoveryEnabled && this.selectedType == 1
        ? 'height635'
        : 'height300'
    },
    handleDisable() {
      if (this.selectedAgent!=null && ( this.selectedAgent.agentType == 9 || this.selectedAgent.agentType == 6)){
        return false
      } else{
        return !(this.selectedAgentId != null && this.selectedType != null)
      }
    },
    selectedAgentShutdown() {
      let { selectedAgents } = this
      return selectedAgents.map(value => value.id)
    },
    toggleJobButton() {
      if (this.isConnectionSame) {
        let { selectedAgents } = this || {}
        let initSelectedAgents = selectedAgents[0]
        if (initSelectedAgents.connected) {
          return this.$t('agent.agent.disableAgent')
        } else {
          return this.$t('agent.agent.enableAgent')
        }
      } else {
        return this.$t('agent.agent.enableAgent')
      }
    },
    disableControllerButton() {
      let { agents } = this || {}
      return agents.some(
        item =>
          item.agentType == AGENT_TYPE['Facilio'] ||
          item.agentType == AGENT_TYPE['Niagara'] ||
          item.agentType == AGENT_TYPE['RDM'] ||
          item.agentType == AGENT_TYPE['E2']
      )
    },
    disableAgentButton() {
      let { agents } = this || {}
      return agents.some(
        item =>
          item.agentType == AGENT_TYPE['Cloud'] ||
          item.agentType == AGENT_TYPE['CloudService']
      )
    },
    page() {
      return this.$route.query.page || 1
    },

    // isDisabled() {
    //   return !this.selectall
    // },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
  },
  watch: {
    // selectAll: function(val) {
    //   let self = this
    //   if (val) {
    //     this.selectedAgent = []
    //     this.agents.filter(function(agent) {
    //       self.selectedAgent.push(agent.id)
    //     })
    //   } else {
    //     if (this.selectedAgent.length === this.agents.length) {
    //       this.selectedAgent = []
    //     }
    //   }
    //   this.loadCount()
    // },
    page() {
      this.loadAgents()
    },
    filters: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.loadAgents()
        }
      },
    },
  },
  methods: {
    updateAgentId(selectedAgent) {
      this.selectedAgent = selectedAgent
      this.selectedAgentId = this.selectedAgent.id
    },
    async loadSites() {
      let siteIds = (this.agents || [])
      .map(agent => agent.siteId)
      .filter(siteId => siteId !== undefined && siteId !== null);
      let defaultIds = [...new Set(siteIds)]
      let perPage = defaultIds.length

      if (perPage === 0) return

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site', skipDeserialize: true },
        defaultIds,
        perPage,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.siteList = options
      }
    },
    loadAgents() {
      let { filters } = this
      this.loading = true
      let url = `/v2/agent/list?page=${this.page}&perPage=${this.perPage}`
      if (filters != null && !isEmpty(filters)) {
        url += `&filters=${encodeURIComponent(JSON.stringify(filters))}`
      }
      return this.$http
        .get(url)
        .then(response => {
          this.loading = false
          this.total = response.data.result.total - response.data.result.active
          this.agents =
            response.data.result && response.data.result.data
              ? response.data.result.data
              : []
          this.agents.sort()
          this.loadSites()
          this.agentCount()
        })
        .catch(() => {
          this.agents = []
          this.loading = false
        })
    },
    listRefresh() {
      this.loadAgents()
    },
    selectAgent(currentSelections) {
      let prevConnection = null
      if (isEmpty(currentSelections)) {
        this.isConnectionSame = false
        this.disableDiscoverController = false
      } else {
        this.isConnectionSame = true
        this.disableDiscoverController = true
      }
      this.selectedAgents = currentSelections
      currentSelections.forEach(item => {
        if (prevConnection == null) {
          prevConnection = item.connected
        } else if (prevConnection != item.connected) {
          this.isConnectionSame = false
        }
      })
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
    agentSelectCheck(row) {
      return (
        row.agentType == AGENT_TYPE['Cloud'] ||
        row.agentType == AGENT_TYPE['CloudService']
      )
    },
    async toggleJob() {
      let { selectedAgentShutdown: agentIds } = this || {}
      let { selectedAgents } = this || {}

      let initSelectedAgents = selectedAgents[0]
      let isActiveUpdateValue
      if (!isEmpty(initSelectedAgents)) {
        isActiveUpdateValue = !initSelectedAgents.connected
      }
      let payload = { agentIds, isActiveUpdateValue }
      let json = { data: payload }
      let url = 'v3/agent/toggleJob'
      let { data, error } = await API.post(url, json)
      if (error) {
        this.$message.error(error.message || 'Error occured')
      } else {
        this.$message.success('Job updated successfully')
      }
    },
    showAddAgent() {
      this.showDialog = true
      this.isNew = true
    },
    setCount(listCount) {
      this.listCount = listCount
    },
    onAgentCreated() {
      this.loadAgents()
    },
    editAgent(agent) {
      this.isNew = false
      this.agent = this.$helpers.cloneObject(agent)
      this.showDialog = true
      this.model = agent
    },
    configureAgent(agent) {
      this.agent = this.$helpers.cloneObject(agent)
      this.showConfigureAgent = true
    },
    onOptionsSelect(command, agent, index) {
      if (command === 'edit') {
        this.editAgent(agent)
      } else if (command === 'delete') {
        this.deleteAgent(agent, index)
      } else if (command === 'configure') {
        this.configureAgent(agent)
      }
    },
    toggleWritable(agent, writable, index) {
      this.$http
        .post('/v2/agent/edit', {
          agentId: agent.id,
          toUpdate: { writable: writable },
        })
        .then(response => {
          if (response.responseCode === 200 || response.responseCode === 201) {
            this.$message('Agent Updated')
          }
        })
        .catch(() => {
          this.$message('Could not update settings')
          this.agents[index].writable = !writable
        })
    },
    loadCount() {
      return this.$http
        .get('/v2/agent/list')
        .then(response => {
          this.totalCount = response.data.result.data || null
        })
        .catch(() => {
          this.loading = false
        })
    },
    agentCount() {
      let { filters } = this
      let url = '/v2/agent/count'
      if (filters != null && !isEmpty(filters)) {
        url += `?filters=${encodeURIComponent(JSON.stringify(filters))}`
      }
      return this.$http
        .get(url)
        .then(response => {
          this.totalCount = response.data.result.data || null
        })
        .catch(() => {
          this.loading = false
        })
    },
    deleteAgent(agent, index) {
      let params = {
        recordIds: [agent.id],
      }
      let url = '/v2/agent/delete'
      this.$dialog
        .confirm({
          title: 'Delete Agent',
          htmlMessage:
            'Are you sure you want to delete ' + agent.displayName + '?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http.post(url, params).then(response => {
              if (response.data.responseCode != 500) {
                this.$message.success('Agent deleted successfully')
                this.loading = false
                this.loadAgents()
              } else {
                this.$message.error(response.data.message)
                this.loading = false
              }
            })
          }
        })
    },
    handleClose() {
      this.discoverDialogVisible = false
      ;(this.selectedAgentId = null),
        (this.selectedAgent = null),
        (this.selectedAgentShutdown = []),
        (this.selectedType = '')
      ;(this.controllerIpAddress = null), (this.controllerPort = null)
      ;(this.IsfullRange = 'all'),
        (this.instanceNumberFrom = 0),
        (this.instanceNumberTo = 4194302),
        (this.timeout_sec = 1),
        (this.bacnetPort = 47808)
      this.isAdvancedDiscoveryEnabled = false
    },
    helpDialogOpen() {
      this.helpDialogVisible = true
    },
    discoverDevices() {
      let discoverControllersData = {
        agentId: this.selectedAgentId,
        controllerType: this.selectedType,
      }
      if (this.selectedType === 14) {
        discoverControllersData.ipAddress = this.controllerIpAddress,
        discoverControllersData.port = this.controllerPort
      }
      if(this.selectedAgent.agentType == 6){
        discoverControllersData.controllerType = 13
      }
      if(this.selectedAgent.agentType == 9){
        discoverControllersData.controllerType = 14
      }
      if (this.selectedType === 1 && this.selectedAgent.agentType == 1) {
        if (this.IsfullRange != 'all') {
          let range = {}
          range.low = parseInt(this.instanceNumberFrom),
          range.high = parseInt(this.instanceNumberTo),
          discoverControllersData.range = range
        }
        discoverControllersData.bacnetPort = this.bacnetPort,
        discoverControllersData.timeout_sec = this.timeout_sec * 60
      }
      this.loading = true
      this.$http
        .post('/v2/controller/discover ', discoverControllersData)
        .then(response => {
          if (response.data.responseCode === 200) {
            this.loading = false
            this.handleClose()
            this.$message.info('Controllers will be discovered in a while.')
          } else {
            this.loading = false
            this.handleClose()
            this.$message.error(response.data.message)
          }
        })
    },
    discoverControllerDialog() {
      this.discoverDialogVisible = true
    },
    agentSummary(agent) {
      let appName = getApp().linkName
      let { path } = findRouteForTab(tabTypes.CUSTOM, {
        config: {
          type: 'agents',
        },
      })
      this.$router.push({
        path: `/${appName}/${path}/all/${agent.id}/overview/`,
      })
    },
    agentTableHeight() {
      let { filters } = this
      return isNull(filters) ? 'fc-list-table-container ' : 'filter-applied'
    },
  },
}
</script>
<style lang="scss">
.agent-list-container {
  .fc-agents-list-table {
    .el-table th > .cell:first-child {
      padding-left: 2px;
      padding-right: 0;
    }
  }

  .fc-list-table-container .el-table {
    height: calc(100vh - 215px) !important;
    padding-bottom: 100px !important;
  }

  .filter-applied .el-table {
    height: calc(100vh - 300px) !important;
    padding-bottom: 100px !important;
  }
}
.select-agent-width {
  .fc-input-full-border-select2 {
    width: 100% !important;
  }
}

.height635 {
  height: 635px;
}

.instance-radio-lable {
  display: flex;
  align-items: center;
  padding: 10px;
}

.radio-button-padding {
  padding: 8px !important;
}

.radio-lable {
  width: 100% !important;
  display: flex;
  align-items: center;
  .el-radio__label {
    width: 100% !important;
  }
}
</style>
