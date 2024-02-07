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
      <div>
        <el-form
          :model="newadddevice"
          :label-position="'top'"
          ref="deviceform"
          label-width="120px"
          class="fc-form"
        >
          <div class="new-header-container">
            <div class="new-header-text">
              <div class="fc-setup-modal-title">
                {{ isNew ? 'New' : 'Edit' }} Controller
              </div>
            </div>
          </div>
          <div class="new-body-modal">
            <div class="setup-input-block">
              <p class="fc-input-label-txt fc-input-space-vertical">
                Select Agent
              </p>
              <el-form-item>
                <el-select
                  prop="agents"
                  v-model="newadddevice.selectedAgentId"
                  filterable
                  default-first-option
                  placeholder="Select Agent"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="(agent, index) in this.agentFilter"
                    :key="index"
                    :label="agent.displayName"
                    :value="agent.id"
                    no-data-text="No Agent available"
                    clearable
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="setup-input-block">
              <p class="fc-input-label-txt fc-input-space-vertical">Type</p>
              <el-form-item prop="controllerType">
                <el-select
                  v-model="newadddevice.controllerType"
                  class="width100"
                  @change="controllerTypeSelect()"
                >
                  <el-option
                    v-for="(label, value) in $constants.ControllerTypesDevice"
                    :key="value"
                    :label="label"
                    :value="parseInt(value)"
                  ></el-option>
                </el-select>
              </el-form-item>
            </div>
            <div
              class="setup-input-block"
              v-if="
                newadddevice.controllerType != null &&
                  newadddevice.controllerType != 1
              "
            >
              <p class="fc-input-label-txt fc-input-space-vertical">
                Controller Name
              </p>
              <el-form-item prop="name">
                <el-input
                  v-model="newadddevice.name"
                  placeholder="Enter Controller Name"
                  class="width100"
                ></el-input>
              </el-form-item>
            </div>
            <div
              class="modbus"
              v-if="
                this.$constants.ControllerTypesDevice[
                  newadddevice.controllerType
                ] === 'Modbus Rtu'
              "
            >
              <div class="fc-modal-sub-title pT20">modbus Rtu</div>

              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="setup-input-block">
                    <p class="fc-input-label-txt pT10">Slave id</p>
                    <el-form-item prop="slaveId">
                      <el-input
                        :type="number"
                        v-model="newadddevice.slaveId"
                        placeholder="Enter Slave id"
                        class="fc-input-txt fc-desc-input"
                      ></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="setup-input-block pT10">
                    <div
                      class="flex-middle justify-content-space position-relative"
                    >
                      <div>
                        <p class="fc-input-label-txt">Network Id</p>
                      </div>
                    </div>
                    <el-form-item prop="networkId">
                      <el-select
                        v-model="idx"
                        placeholder="Select"
                        class="width100"
                      >
                        <el-option
                          v-for="(netwrok, index) in rtuNetworks"
                          :key="index"
                          :label="netwrok.name"
                          :value="parseInt(index)"
                        >
                        </el-option>
                        <el-option>
                          <div
                            class="fwBold pointer line-height20 pT8 text-center text-uppercase f11"
                            @click="createNewNetwork()"
                          >
                            Create new network
                          </div>
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
              <div v-if="showCreateNetwork">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="setup-input-block">
                      <p class="fc-input-label-txt pT10">Network Name</p>
                      <el-form-item>
                        <el-input
                          v-model="newaddnetwork.name"
                          placeholder="Enter Network Name"
                          class="fc-input-txt fc-desc-input"
                        ></el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="setup-input-block">
                      <p class="fc-input-label-txt pT10">Port</p>
                      <el-form-item>
                        <el-input
                          v-model="newaddnetwork.comPort"
                          placeholder="Select"
                          class="fc-input-txt fc-desc-input"
                        >
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="setup-input-block">
                      <p class="fc-input-label-txt pT10">Baud Rate</p>
                      <el-form-item>
                        <el-input
                          v-model="newaddnetwork.baudRate"
                          placeholder="Enter Baud Rate"
                          class="fc-input-txt fc-desc-input"
                        ></el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="setup-input-block">
                      <p class="fc-input-label-txt pT10">Data Bits</p>
                      <el-form-item>
                        <el-input
                          v-model="newaddnetwork.dataBits"
                          placeholder="Select"
                          class="fc-input-txt fc-desc-input"
                        >
                        </el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="setup-input-block">
                      <p class="fc-input-label-txt pT10">Stop Bits</p>
                      <el-form-item>
                        <el-input
                          v-model="newaddnetwork.stopBits"
                          placeholder="Enter stop bits Name"
                          class="fc-input-txt fc-desc-input"
                        ></el-input>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="setup-input-block">
                      <p class="fc-input-label-txt pT10">Parity</p>
                      <el-form-item>
                        <el-select
                          v-model="newaddnetwork.parity"
                          placeholder="Select"
                          class="fc-input-txt fc-desc-input"
                        >
                          <el-option key="0" label="0" value="0"></el-option>
                          <el-option key="1" label="1" value="1"></el-option>
                        </el-select>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <!-- BACnet IP  -->
            <div
              class="opc-xml-sec"
              v-if="
                this.$constants.ControllerTypesDevice[
                  newadddevice.controllerType
                ] === 'BACnet Ip'
              "
            >
              <div class="setup-input-block">
                <p class="fc-input-label-txt pT10 width100">IP Address</p>
                <el-form-item>
                  <el-input
                    v-model="newadddevice.ipAddress"
                    placeholder="Enter the IP Address"
                    class="fc-input-txt fc-desc-input"
                  >
                  </el-input>
                </el-form-item>
              </div>
              <el-row :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt pT10">Instance Number</p>
                  <el-form-item prop="instanceNumber">
                    <el-input
                      type="number"
                      v-model="newadddevice.instanceNumber"
                      placeholder="Enter the Instance Number"
                      class="fc-input-txt fc-desc-input"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <p class="fc-input-label-txt pT10">Network Number</p>
                  <el-form-item prop="networkNumber">
                    <el-input
                      type="text"
                      v-model="newadddevice.networkNumber"
                      placeholder="Enter the Network Number"
                      class="fc-input-txt fc-desc-input"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <!-- opc ua section -->

            <div
              class="opu-ua-sec"
              v-if="
                this.$constants.ControllerTypesDevice[
                  newadddevice.controllerType
                ] === 'Opc UA'
              "
            >
              <div class="setup-input-block">
                <p class="fc-input-label-txt pT10 width100">URL</p>
                <el-form-item prop="url">
                  <el-input
                    v-model="newadddevice.url"
                    placeholder="Enter the URL"
                    class="fc-input-txt fc-desc-input"
                  ></el-input>
                </el-form-item>
              </div>
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="setup-input-block">
                    <p class="fc-input-label-txt pT10">Security Policy</p>
                    <el-form-item prop="policy">
                      <el-select
                        v-model="newadddevice.policy"
                        placeholder="Select"
                        class="width100"
                      >
                        <el-option label="None" :value="0"></el-option>
                        <el-option label="Basic128Rsa15" :value="1"></el-option>
                        <el-option label="Basic256" :value="2"></el-option>
                        <el-option
                          label="Basic256Sha256"
                          :value="3"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="setup-input-block">
                    <p class="fc-input-label-txt pT10">Security mode</p>
                    <el-form-item prop="security">
                      <el-select
                        v-model="newadddevice.security"
                        placeholder="Select"
                        class="width100"
                      >
                        <el-option label="None" :value="0"></el-option>
                        <el-option label="Sign" :value="1"></el-option>
                        <el-option
                          label="Sign & Encrypt"
                          :value="2"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- opc XML -->

            <div
              class="opc-xml-sec"
              v-if="
                ['Opc XML DA', 'RDM'].includes(
                  this.$constants.ControllerTypesDevice[
                    newadddevice.controllerType
                  ]
                )
              "
            >
              <div class="setup-input-block">
                <p class="fc-input-label-txt pT10">URL</p>
                <el-form-item prop="controllerUrl">
                  <el-input
                    type="text"
                    v-model="newadddevice.controllerUrl"
                    placeholder="Enter the URL"
                    class="fc-input-txt fc-desc-input"
                  ></el-input>
                </el-form-item>
              </div>
              <el-row :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt pT10">User Name</p>
                  <el-form-item prop="userName">
                    <el-input
                      type="text"
                      v-model="newadddevice.userName"
                      placeholder="Enter the Username"
                      class="fc-input-txt fc-desc-input"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <p class="fc-input-label-txt pT10">Password</p>
                  <el-form-item prop="password">
                    <el-input
                      type="text"
                      v-model="newadddevice.password"
                      placeholder="Enter the password"
                      class="fc-input-txt fc-desc-input"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>

            <!-- modubus ip -->

            <div
              class="opc-xml-sec"
              v-if="
                this.$constants.ControllerTypesDevice[
                  newadddevice.controllerType
                ] === 'Modbus Ip'
              "
            >
              <el-row :gutter="20">
                <el-col :span="12">
                  <p class="fc-input-label-txt pT10">Slave Id</p>
                  <el-form-item prop="modbusSlave">
                    <el-input
                      type="number"
                      v-model="newadddevice.modbusSlave"
                      placeholder="Enter the slave id"
                      class="fc-input-txt fc-desc-input"
                    ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <p class="fc-input-label-txt pT10">Ip Address</p>
                  <el-form-item prop="modbusIp">
                    <el-input
                      type="text"
                      v-model="newadddevice.modbusIp"
                      placeholder="Enter the Ip Address"
                      class="fc-input-txt fc-desc-input"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="setup-input-block">
                    <p class="fc-input-label-txt pT10">Port</p>
                    <el-form-item>
                      <el-input
                        v-model="newadddevice.portNumber"
                        placeholder="Enter Port"
                        class="fc-input-txt fc-desc-input"
                      >
                      </el-input>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button @click="closeDialog()" class="modal-btn-cancel"
              >CANCEL</el-button
            >
            <el-button
              type="primary"
              @click="submitForm('deviceform')"
              :loading="saving"
              class="modal-btn-save"
            >
              {{ saving ? 'Saving...' : 'SAVE' }}</el-button
            >
          </div>
        </el-form>
      </div>
    </el-dialog>
    <new-network
      v-if="networkFormVisible"
      :visibility.sync="networkFormVisible"
    ></new-network>
  </div>
</template>

<script>
import NewNetwork from 'agent/components/AgentNetworkCreateForm'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['visibility', 'isNew', 'model'],
  components: {
    NewNetwork,
  },
  data() {
    return {
      saving: false,
      agents: [],
      agent: null,
      newadddevice: {
        name: '',
        controllerType: null,
        slaveId: '',
        networkId: null,
        url: '',
        security: null,
        policy: null,
        certificate: '',
        controllerUrl: '',
        userName: '',
        password: '',
        modbusSlave: '',
        modbusIp: '',
        selectedAgentId: null,
        ipAddress: '',
        networkNumber: '',
        instanceNumber: '',
        portNumber: null,
      },
      newaddnetwork: {
        name: '',
        comPort: null,
        baudRate: '',
        dataBits: '',
        stopBits: '',
        parity: '',
      },
      networkFormVisible: false,
      rtuNetworks: [],
      showCreateNetwork: false,
      idx: null,
    }
  },
  mounted() {
    this.loadAgents()
    // if (!this.isNew) {
    //   let {
    //     name,
    //     macAddr,
    //     controllerType,
    //     dataInterval,
    //     writable,
    //     instanceNumber,
    //     displayName,
    //   } = this.model
    //   Object.assign(this.newaddController, {
    //     name,
    //     macAddr,
    //     controllerType,
    //     dataInterval,
    //     writable,
    //     instanceNumber,
    //     displayName,
    //   })
    //   let nullableFields = ['dataInterval', 'controllerType']
    //   nullableFields.forEach(fieldName => {
    //     if (this.newAgent[fieldName] === -1) {
    //       this.newAgent[fieldName] = null
    //     }
    //   })
    // }
  },
  computed: {
    agentFilter() {
      return this.agents.filter(
        agent => agent.agentType != 0 && agent.agentType != 2
      )
    },
  },
  methods: {
    submitForm(deviceform) {
      this.$refs[deviceform].validate(valid => {
        if (valid) {
          if (!this.validation()) {
            return
          }
          this.saving = true
          let payload = null
          let url
          // TODO change these implementations to single api
          if (this.newadddevice.controllerType === 1) {
            url = '/v2/bacnet/addController'
            let bacnetControllerObj = {
              ipAddress: this.newadddevice.ipAddress,
              networkNumber: this.newadddevice.networkNumber,
              instanceNumber: this.newadddevice.instanceNumber,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
            }
            payload = {
              bacnetController: bacnetControllerObj,
            }
          } else if (this.newadddevice.controllerType === 4) {
            url = '/v2/modbus/addController'
            payload = {
              name: this.newadddevice.name,
              slaveId: this.newadddevice.modbusSlave,
              ip: this.newadddevice.modbusIp,
              port: this.newadddevice.portNumber,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
            }
          } else if (this.newadddevice.controllerType === 5) {
            let addnewNetwork
            if (this.newaddnetwork.name != '') {
              addnewNetwork = {
                name: this.newaddnetwork.name,
                comPort: this.newaddnetwork.comPort,
                baudRate: parseInt(this.newaddnetwork.baudRate),
                dataBits: parseInt(this.newaddnetwork.dataBits),
                stopBits: parseInt(this.newaddnetwork.stopBits),
                parity: parseInt(this.newaddnetwork.parity),
              }
            } else {
              addnewNetwork = {
                name: this.rtuNetworks[this.idx].name,
                comPort: this.rtuNetworks[this.idx].comPort,
                baudRate: this.rtuNetworks[this.idx].baudRate,
                dataBits: this.rtuNetworks[this.idx].dataBits,
                stopBits: this.rtuNetworks[this.idx].stopBits,
                parity: this.rtuNetworks[this.idx].parity,
              }
            }
            url = '/v2/modbus/addController'
            payload = {
              name: this.newadddevice.name,
              slaveId: parseInt(this.newadddevice.slaveId),
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
              rtuNetwork: addnewNetwork,
            }
          } else if (this.newadddevice.controllerType === 7) {
            url = '/v2/opcUa/addController'
            payload = {
              name: this.newadddevice.name,
              url: this.newadddevice.url,
              certPath: this.newadddevice.certificate,
              securityMode: this.newadddevice.security,
              securityPolicy: this.newadddevice.policy,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
            }
          } else if (this.newadddevice.controllerType === 6) {
            url = '/v2/opcda/addController'
            payload = {
              name: this.newadddevice.name,
              url: this.newadddevice.controllerUrl,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
              userName: this.newadddevice.userName,
              password: this.newadddevice.password,
            }
          } else if (this.newadddevice.controllerType === 0) {
            url = '/v2/modbus/addMiscController'
            payload = {
              name: this.newadddevice.name,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
            }
          } else if (this.newadddevice.controllerType === 13) {
            url = '/v2/rdm/addController'
            payload = {
              name: this.newadddevice.name,
              url: this.newadddevice.controllerUrl,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
              userName: this.newadddevice.userName,
              password: this.newadddevice.password,
            }
          } else if (this.newadddevice.controllerType === 14) {
            url = '/v2/e2/addController'
            payload = {
              name: this.newadddevice.name,
              agentId: this.newadddevice.selectedAgentId,
              controllerType: this.newadddevice.controllerType,
              ipAddress: this.newadddevice.ipAddress,
              portNumber: this.newadddevice.portNumber,
            }
          }

          this.$http.post(url, payload).then(response => {
            if (response.status === 200) {
              this.$message('Device will be added in a while.')
              this.saving = false
              this.resetForm()
              this.$emit('saved')
              this.$emit('update:visibility', false)
            }
          })
        }
      })
    },
    loadAgents() {
      this.loading = true
      this.$http
        .get('/v2/agent/getFilter')
        .then(response => {
          this.loading = false
          this.agents =
            response.data.result && response.data.result.data
              ? response.data.result.data
              : []
          this.agents.sort()
        })
        .catch(() => {
          this.agents = []
          this.loading = false
        })
    },
    getRtuNetworks() {
      let url = `/v2/modbus/rtuNetworks?agentId=${this.newadddevice.selectedAgentId}`
      this.$http(url).then(response => {
        if (response.data.responseCode === 200) {
          this.rtuNetworks = response.data.result.data
        }
      })
    },
    resetForm() {
      this.$refs['deviceform'].resetFields()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    cancel() {
      this.$emit('canceled')
    },
    createNewNetwork() {
      this.showCreateNetwork = true
    },
    controllerTypeSelect() {
      this.$forceUpdate()
      if (this.newadddevice.controllerType === 5) {
        this.getRtuNetworks()
      }
    },
    validation() {
      if (isEmpty(this.newadddevice.selectedAgentId)) {
        this.$message.error('Please Select Agent.')
        return false
      } else if (
        this.newadddevice.controllerType != 1 &&
        isEmpty(this.newadddevice.name)
      ) {
        this.$message.error('Please Enter name.')
        return false
      } else if (isEmpty(this.newadddevice.controllerType)) {
        this.$message.error('Please Select Type.')
        return false
      }
      return true
    },
  },
}
</script>
<style lang="scss">
.align-right-abs {
  position: absolute;
  right: 0;
  top: 2px;
}
</style>
