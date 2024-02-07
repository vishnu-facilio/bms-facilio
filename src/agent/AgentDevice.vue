<template>
  <div class="agent-table-scroll-tab">
    <new-add-Controller
      v-if="showDialog"
      :model="device"
      isNew="isNew"
      :visibility.sync="showDialog"
      @saved="onControllerSaved"
    ></new-add-Controller>
    <portal to="device-form" slim>
      <el-button class="fc-agent-add-btn" @click="showController()">
        <i class="el-icon-plus pR5 fwBold"></i>Add Controller
      </el-button>
    </portal>
    <div class="">
      <portal to="device-actions" slim>
        <div
          class="flex-middle justify-content-space width100 pL10 pT20"
          v-if="activeName === 'unconfigured'"
        >
          <div class>
            <el-select
              v-model="selectedAgentId"
              filterable
              default-first-option
              @change="loadDevice"
              placeholder="Select Agent"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="(agent, index) in agents"
                :key="index"
                :label="agent.name"
                :value="agent.id"
                no-data-text="No Agent available"
                clearable
              ></el-option>
            </el-select>
            <el-select
              v-model="selectedType"
              filterable
              placeholder="Select Type"
              class="fc-input-full-border-select2 mL20"
              @change="onTypeChange"
            >
              <template v-for="(label, value) in $constants.ControllerTypes">
                <el-option
                  :key="value"
                  :label="label"
                  :value="parseInt(value)"
                ></el-option>
              </template>
            </el-select>
            <el-button
              class="fc-white-btn mL20"
              v-if="this.selectedType === 1 || this.selectedType === 12"
              @click="discoverDevices()"
              >Discover Devices</el-button
            >
          </div>
          <div class="flex-middle">
            <pagination :total="listCount" :perPage="perPage"></pagination>
            <f-search class="mR20" v-model="devices"></f-search>
            <div
              class="pointer fwBold pR10 f16 clearboth"
              @click="listRefresh"
              v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
              content="Refresh"
            >
              <i class="el-icon-refresh fwBold f16"></i>
            </div>
          </div>
        </div>
      </portal>
    </div>
    <!-- unconfigured started -->
    <div
      v-if="loading"
      class="flex-middle fc-empty-white m10 fc-agent-empty-state height-calc200"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-if="$validation.isEmpty(realDevices) && !loading"
      class="fc-empty-white flex-middle height-calc200 justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="q-item-label nowo-label">No Unconfigured available</div>
    </div>
    <div
      class="fc-agent-table agent-list-scroll scrollbar-style fc-list-table-container"
      v-if="!loading && !$validation.isEmpty(realDevices)"
    >
      <el-table
        ref="multipleSelection"
        :data="realDevices"
        style="width: 100%"
        height="auto"
        class="fc-list-view pT0 height100vh fc-table-td-height fc-table-viewchooser pB100 fc-table-th-minus"
      >
        <el-table-column label="NAME">
          <template v-slot="device">
            <div class>{{ device.row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column label="Site">
          <template v-slot="device">
            {{
              device.row.siteId > 0
                ? ($store.getters.getSite(device.row.siteId) || {}).name
                : '---'
            }}
          </template>
        </el-table-column>
        <el-table-column label="Created Time" sortable prop="device">
          <template v-slot="device">
            {{ device.row.createdTime | formatDate(true) }}
            {{ device.row.createdTime | formatDate(true) }}
          </template>
        </el-table-column>
        <el-table-column label="Controller Properties">
          <template v-slot="device">
            <div
              @click="showIotMessage(device.row)"
              class="fc-agent-show-txt bold"
            >
              Show Properties
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog
        v-if="showMsgJson && showMsgJson.id"
        title="Controller Props"
        :visible.sync="dialogVisible"
        :append-to-body="true"
        width="30%"
        class="fc-dialog-center-container"
      >
        <div class="fc-json-view pB20">{{ showMsgJson.propsStr }}</div>
        <!-- table render -->
        <!-- <el-table :data="devices" style="width: 100%">
          <el-table-column prop="id" label="id" width="180"> </el-table-column>
        </el-table>-->
      </el-dialog>
    </div>
  </div>
</template>
<script>
import NewAddController from 'agent/components/ControllerAddForm'
import FSearch from '@/FSearch'
import Pagination from 'src/components/list/FPagination'
export default {
  title() {
    return 'Agent Device'
  },
  data() {
    return {
      loading: false,
      devices: [],
      agents: [],
      device: null,
      agent: null,
      showHelpDialog: false,
      controllers: [],
      selectedAgentId: null,
      dialogVisible: false,
      selectedDevice: null,
      selectedControllerId: null,
      showDialog: false,
      selectedType: 0,
      selectedDevices: null,
      showMsgJson: null,
      selectedDeviceDiscover: [],
      selectall: false,
      clicked: [],
      realDevices: [],
      listCount: null,
      perPage: 50,
      activeName: 'unconfigured',
    }
  },
  created() {
    this.loadAgents()
  },
  components: {
    NewAddController,
    FSearch,
    Pagination,
  },
  computed: {
    // availableTypes() {
    //   return this.realDevices.map(device => device.controllerType)
    // },
    // filteredControllers() {
    //   return this.realDevices.filter(
    //     device => device.controllerType === this.selectedType
    //   )
    // },
    selectedController() {
      if (this.selectedControllerId) {
        return this.devices.find(
          device => device.id === this.selectedControllerId
        )
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    selectedAgentId(id) {
      this.$router.push({
        query: {
          agentId: id,
        },
      })
    },
    selectAll: function(val) {
      let self = this
      if (val) {
        this.selectedDevices = []
        this.agents.filter(function(device) {
          self.selectedDevices.push(device)
        })
      } else {
        if (this.selectedDevices.length === this.devices.length) {
          this.selectedDevices = []
        }
      }
    },
    page() {
      this.loadDevice()
      this.loadCount(true)
    },
  },
  methods: {
    showIotMessage(device) {
      this.dialogVisible = true
      this.showMsgJson = device
      // this.selectedIndex = JSON.stringify(this.showMsgJson)
      this.showMsgJson = {
        ...device,
        propsStr: JSON.stringify(JSON.parse(device.propsStr), null, 2).trim(),
      }
    },
    discoverDevices() {
      this.loading = true
      this.$http
        .post('/v2/controller/discover ', {
          agentId: this.selectedAgentId,
          controllerType: this.selectedType,
        })
        .then(response => {
          if (response.data.responseCode === 200) {
            this.loading = false
            this.$message.success('Discover updated successfully.')
          } else {
            this.loading = false
            this.$message.error(response.data.message)
          }
        })
    },
    helpDialogOpen() {
      this.showHelpDialog = true
    },
    loadDevice() {
      this.loading = true
      let url = this.selectedAgentId
        ? `/v2/device/list?agentId=${this.selectedAgentId}&controllerType=${this.selectedType}`
        : `/v2/device/list`

      this.$http
        .get(url)
        .then(response => {
          this.loading = false
          this.realDevices = response.data.result.data
            ? response.data.result.data
            : []
          // this.selectedType = this.realDevices[0].controllerType
          // this.devices = response.data.result.data
          //   ? response.data.result.data.filter(
          //       c => c.controllerType == this.selectedType
          //     )
          //   : []
        })
        .catch(() => {
          this.devices = []
          this.loading = false
        })
    },
    loadCount() {
      this.listCount = 0
      let url = `/v2/device/count?agentId=${this.selectedAgentId}&controllerType=${this.selectedType}`
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 200) {
          this.listCount = response.data.result.data
        }
      })
    },
    loadAgents() {
      this.setLoading(true)
      this.$http
        .get('/v2/agent/list')
        .then(response => {
          this.agents = response.data.result.data
            ? response.data.result.data
            : []
          if (this.agents.length) {
            this.selectedAgentId =
              parseInt(this.$route.params.agentId) || this.agents[0].id
            this.loadDevice()
            this.loadCount()
          } else {
            // this.devices = []
            this.setLoading(false)
          }
        })
        .catch(() => {
          this.agents = []
          this.setLoading(false)
        })
    },
    listRefresh() {
      this.loadDevice()
    },
    onTypeChange() {
      this.loadCount()
      this.loadDevice()
    },
    showController() {
      this.showDialog = true
    },
    setLoading(isLoading) {
      this.$emit('update:loading', isLoading)
    },
  },
}
</script>
