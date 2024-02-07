<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Controllers</div>
        <div class="heading-description">List of all controllers</div>
      </div>
      <div class="action-btn setting-page-btn controller-right-actions">
        <f-search class="mL20" v-model="controllers"></f-search>
        <new-addController
          v-if="showDialog"
          :model="controller"
          :visibility.sync="showDialog"
          @saved="onControllerSaved"
        ></new-addController>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT20">
        <div class="col-lg-12 col-md-12 overflow-x">
          <el-row class="mB20">
            <el-col :span="12">
              <div class="category-blue-txt pB10">Agent</div>
              <div>
                <el-select
                  v-model="selectedAgentId"
                  filterable
                  default-first-option
                  @change="totalCount = null"
                  placeholder="Select Agent"
                  width="250px"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="(agent, index) in agents"
                    :key="index"
                    :label="agent.name"
                    :value="agent.id"
                    no-data-text="No controllers available"
                    clearable
                  ></el-option>
                </el-select>
                <el-button
                  v-if="!$validation.isEmpty(selectedAgentId)"
                  @click="showAgentDetails = true"
                  type="text"
                  class="inline mL20"
                  style="font-size: 12px; cursor: pointer; color: #396dc2;"
                  >View Agent Details</el-button
                >
              </div>
            </el-col>
          </el-row>
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 40%; max-width: 340px;"
                >
                  CONTROLLER
                </th>
                <th class="setting-table-th setting-th-text" style="width: 40%">
                  Points
                </th>
                <!-- <th class="setting-table-th setting-th-text" style="width: 15%">Data Received</th> -->
                <!-- <th class="setting-table-th setting-th-text" style="width: 15%">Interval</th> -->
                <th class="setting-table-th setting-th-text" style="width: 20%">
                  Writable
                </th>
                <th style="width: 10%"></th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="controllers.length === 0">
              <tr>
                <td colspan="100%" class="text-center">
                  No Controller available.
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(controller, index) in controllers"
                :key="controller.id"
                v-loading="loading"
              >
                <td style="width:15%; max-width: 340px;">
                  <div class="controller-active-section">
                    <div
                      :title="controller.active ? 'Active' : 'Inactive'"
                      v-tippy
                      :class="[
                        'dot-10',
                        controller.active ? 'color-active' : 'color-inactive',
                      ]"
                    ></div>
                    <div
                      class="label-txt3-14 mL10 break-word-all line-height20"
                    >
                      {{ controller.name }}
                    </div>
                    <div
                      class="fc-badge mL10 inline middle"
                      v-if="controller.controllerType > 0"
                    >
                      {{
                        $constants.ControllerTypes[controller.controllerType]
                      }}
                    </div>
                  </div>
                  <div class="controller-id mT5 mL20">
                    {{ controller.macAddr }}
                  </div>
                </td>
                <td>
                  <div class="fc-list-subject fw-normal">
                    <div class="controller-points mb5">
                      <span class="label-muted">Available:</span>
                      {{ controller.availablePoints }}
                    </div>
                    <div class="controller-points mb5">
                      <span class="label-muted">Configured:</span>
                      {{ controller.configuredPointsCount }}
                    </div>
                    <div class="controller-points mb5">
                      <span class="label-muted">Subscribed:</span>
                      {{ controller.subscribedPointsCount }}
                    </div>
                  </div>
                </td>
                <!-- <td>
                  <div class="label-txt3-14">{{controller.lastDataReceivedTime | fromNow}}</div>
                </td>
                <td>
                  <div
                    class="label-txt3-14"
                  >{{controller.dataInterval > -1 ? (controller.dataInterval + ' mins') : '---'}}</div>
                </td> -->
                <td>
                  <el-switch
                    @change="toggleWritable(controller, index)"
                    v-model="controller.writable"
                    class="Notification-toggle"
                    active-color="rgba(57, 178, 194, 0.8)"
                    inactive-color="#e5e5e5"
                  ></el-switch>
                </td>
                <td>
                  <el-dropdown @command="onOptionsSelect($event, controller)">
                    <span class="el-dropdown-link">
                      <i class="el-icon-more controller-more"></i>
                    </span>
                    <el-dropdown-menu
                      slot="dropdown"
                      class="controller-dropdown-item"
                    >
                      <el-dropdown-item command="edit">Edit</el-dropdown-item>
                      <el-dropdown-item command="config"
                        >Configuration</el-dropdown-item
                      >
                      <el-dropdown-item command="commission"
                        >Commissioning</el-dropdown-item
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

    <!-- Agent Detail Dialog -->
    <el-dialog
      v-if="showAgentDetails"
      title="Agent Details"
      :visible.sync="showAgentDetails"
      width="30%"
      style="z-index: 9999999999;"
      class="agents-dialog"
    >
      <el-row>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Location</div>
          <div class="fc-list-subject fw-normal">
            {{
              selectedAgentDetails.siteId > 0
                ? ($store.getters.getSite(selectedAgentDetails.siteId) || {})
                    .name
                : '---'
            }}
          </div>
        </el-col>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Data Received</div>
          <div class="fc-list-subject fw-normal">
            {{ selectedAgentDetails.lastDataReceivedTime | fromNow }}
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Type</div>
          <div class="fc-list-subject fw-normal">
            {{
              selectedAgentDetails.type
                ? $constants.ControllerTypes[selectedAgentDetails.type]
                : '---'
            }}
          </div>
        </el-col>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Controllers</div>
          <div class="fc-list-subject fw-normal">
            {{ selectedAgentDetails.controllerCount || '---' }}
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Interval</div>
          <div class="fc-list-subject fw-normal">
            {{
              selectedAgentDetails.interval > -1
                ? selectedAgentDetails.interval + ' mins'
                : '---'
            }}
          </div>
        </el-col>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Status</div>
          <div class="fc-list-subject fw-normal">
            {{
              selectedAgentDetails.connectionStatus
                ? 'Connected'
                : 'Disconnected'
            }}
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Version</div>
          <div class="fc-list-subject fw-normal">
            {{ selectedAgentDetails.version || '---' }}
          </div>
        </el-col>
        <el-col :span="12" class="mB20">
          <div class="text-uppercase mB10 fc-blue-label">Writable</div>
          <div class="fc-list-subject fw-normal">
            {{ selectedAgentDetails.writable ? 'Yes' : 'No' }}
          </div>
        </el-col>
      </el-row>
    </el-dialog>
    <!-- Agent Dialog -->
  </div>
</template>
<script>
import NewAddController from 'pages/setup/agents/NewController'
import FSearch from '@/FSearch'
export default {
  title() {
    return 'Controllers'
  },
  components: {
    NewAddController,
    FSearch,
  },
  data() {
    return {
      isNew: true,
      loading: false,
      controllers: [],
      controller: null,
      agents: [],
      agent: null,
      selectedAgentId: null,
      showDialog: false,
      showAgentDetails: false,
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
  },
  methods: {
    loadControllers() {
      this.loading = true

      let url = this.selectedAgentId
        ? `/v2/setup/controller/list?agentId=${this.selectedAgentId}`
        : `/v2/setup/controller/list`

      this.$http
        .get(url)
        .then(response => {
          this.loading = false
          this.controllers = response.data.result.controllerDetails
            ? response.data.result.controllerDetails
            : []
        })
        .catch(() => {
          this.controllers = []
          this.loading = false
        })
    },
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/setup/agent/list')
        .then(response => {
          this.agents = response.data.result.agentDetails
            ? response.data.result.agentDetails
            : []
          this.selectedAgentId =
            parseInt(this.$route.query.agentId) || this.agents[0].id
          this.loadControllers()
        })
        .catch(() => {
          this.loading = false
        })
    },
    onControllerSaved() {
      this.loadControllers()
    },
    editController(controller) {
      this.isNew = false
      this.controller = this.$helpers.cloneObject(controller)
      this.showDialog = true
    },
    onOptionsSelect(command, controller) {
      if (command === 'config') {
        this.$router.push(
          `/app/setup/agents/config/${controller.agentId}/${controller.id}`
        )
      } else if (command === 'commission') {
        this.$router.push(
          `/app/setup/agents/commissioning/${controller.agentId}/${controller.id}`
        )
      } else if (command === 'edit') {
        this.editController(controller)
      }
    },
    toggleWritable({ id, writable }, index) {
      let controllerContext = { id, writable }
      this.$http
        .post('/v2/setup/controller/edit', { controllerContext })
        .then(response => {
          if (response.status === 200) {
            this.$message('Controller Updated')
          }
        })
        .catch(() => {
          this.$message('Could not update settings')
          this.controllers[index].writable = !writable
        })
    },
  },
  watch: {
    selectedAgentId(val) {
      this.$router.push({ query: { agentId: val } })
      this.loadControllers()
    },
  },
}
</script>
<style scoped>
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
.controller-id {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: #a7a7ad;
}
.controller-time {
  font-size: 11px;
  letter-spacing: 0.4px;
  color: #c5c4cb;
  margin-left: 20px;
}
.controller-active-section {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.dropdown-controller-item .el-dropdown-menu__item {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #67666a;
  border-bottom: 1px solid rgba(151, 151, 151, 0.1);
}
.controller-points {
  font-size: 11px;
  letter-spacing: 0.4px;
}
.label-muted {
  color: #a7a7ad;
}
</style>
