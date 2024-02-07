<template>
  <div class="">
    <div class="visitor-hor-card scale-up-left" v-if="loading">
      <spinner :show="loading" size="80"></spinner>
    </div>

    <div
      class="visitor-hor-card scale-up-left d-flex align-center justify-content-center flex-direction-column"
      v-else-if="devices.length === 0"
    >
      <InlineSvg
        src="svgs/emptystate/approval"
        iconClass="icon icon-xxxlg vertical-middle mR10"
      >
      </InlineSvg>
      <div class="text-center">
        {{ $t('setup.setup_empty_state.deviceemptystate') }}
      </div>
    </div>

    <div v-else class="mT20">
      <div
        class="visitor-hor-card scale-up-left"
        v-for="(device, index) in devices"
        :key="device.id"
        v-loading="loading"
      >
        <el-row class="flex-middle">
          <el-col :span="5">
            <div
              class="agent-active-section flex-middle"
              style="align-items: flex-start;"
            >
              <AgentActivityDot
                :key="device.name"
                :state="device.isDeviceConnected"
                :status="device.isDeviceConnected"
              ></AgentActivityDot>
              <div class="mL10">
                <div class="label-txt3-14">
                  {{ device.name }}
                </div>
                <div class="label-txt3-12 mT10 flex-middle">
                  <span class="agent-version">Status: </span>

                  {{ device.isDeviceConnected ? 'Connected' : 'Disconnected' }}
                </div>
              </div>
            </div>
          </el-col>

          <el-col :span="4">
            <div class="label-txt3-14">
              <img
                src="~statics/space/space-resource.svg"
                title="Device Location"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
                style="height:11px; width:12px; margin-right: 3px;"
              />
              {{
                device.associatedResource
                  ? device.associatedResource.name
                  : '---'
              }}
            </div>
          </el-col>
          <el-col :span="3">
            <div class="label-txt3-14 flex-middle">
              <InlineSvg
                src="svgs/telephone-icon"
                class="pointer"
                iconClass="icon icon-sm flex-middle"
                title="Default Country Code"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              ></InlineSvg>
              <span class="mL5">
                {{ device.countryCode ? device.countryCode : '---' }}</span
              >
            </div>
          </el-col>
          <el-col :span="4">
            <div class="label-txt3-14">
              {{ $getProperty(device, 'printer.name', '--') }}
            </div>
          </el-col>
          <el-col :span="7">
            <div class="visitor-device-actions flex-middle">
              <template v-if="device.isDeviceConnected">
                <el-button
                  type="primary"
                  class="fc-btn-green-medium-fill shadow-none"
                  @click="onOptionsSelect('disconnect', device, index)"
                  >Disconnect</el-button
                >
                <el-button
                  type="secondary"
                  class="fc-btn-green-medium-border shadow-none"
                  @click="onOptionsSelect('reconnect', device, index)"
                  >Reconnect</el-button
                >
              </template>
              <template v-else>
                <el-button
                  type="primary"
                  class="fc-btn-green-medium-fill shadow-none"
                  @click="onOptionsSelect('connect', device, index)"
                  >Connect</el-button
                >
              </template>
            </div>
          </el-col>
          <el-col :span="1" class="text-right">
            <div v-on:click.stop>
              <el-dropdown
                @command="onOptionsSelect($event, device, index)"
                slot="reference"
                trigger="click"
              >
                <span class="el-dropdown-link">
                  <i class="el-icon-more controller-more"></i>
                </span>
                <el-dropdown-menu
                  slot="dropdown"
                  class="controller-dropdown-item"
                >
                  <!-- <el-dropdown-item command="refresh">
                    Refresh Kiosk
                  </el-dropdown-item> -->
                  <el-dropdown-item command="edit">Edit</el-dropdown-item>

                  <el-dropdown-item command="delete">
                    Delete
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <!-- anchor popover to this element if connect dropdown item is clickedf -->
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <visitor-kiosk-form
      v-if="showForm"
      :kioskContext="device"
      :isEdit="isEdit"
      :drawerVisibility.sync="showForm"
      @save="handleFormSubmit"
    ></visitor-kiosk-form>

    <connect-device
      @connected="loadVisitorKioskList"
      :title="showConnectDialogTitle"
      :visibility.sync="showConnectPopover"
      :device="device"
    >
      Access <b>Facilio Visitor</b> app on your tablet/phone to obtain the 6
      digit passcode
    </connect-device>
  </div>
</template>

<script>
//import NewAgent from 'pages/setup/agents/NewAgent'
import FDialog from '@/FDialogNew'
import connectDevice from 'src/pages/setup/ConnectDevice'
import { getDisplayValue } from 'src/util/field-utils'
import AgentActivityDot from 'pages/setup/agent/AgentActivityDot'
import VisitorKioskForm from 'pages/setup/VisitorKioskForm'
export default {
  title() {
    return 'Devices'
  },
  components: {
    AgentActivityDot,
    connectDevice,
    VisitorKioskForm,
  },
  data() {
    return {
      isNew: true,
      loading: true,
      devices: [],
      showForm: false,
      device: null,
      isEdit: false,
      showConnectPopover: false,
      showConnectDialogTitle: null,
    }
  },
  created() {
    this.loadVisitorKioskList()
  },

  mounted() {},
  methods: {
    openAddDeviceDialog() {
      this.device = null
      this.isEdit = false
      this.showForm = true
    },
    getDeviceTypeDisplayVal(enumVal) {
      return getDisplayValue(
        this.metaInfo.fields.find(e => e.name == 'deviceType'),
        enumVal
      )
    },
    //TO DO , remove facilio web form and use plain el-drawer
    handleFormSubmit(context) {
      this.$http
        .post(context.id ? 'v2/visitorKiosk/update' : 'v2/visitorKiosk/add', {
          visitorKiosk: context,
        })
        .then(() => {
          this.showForm = false
          this.loadVisitorKioskList()
        })
    },

    loadVisitorKioskList() {
      this.loading = true
      this.$http
        .get('/v2/visitorKiosk/list')
        .then(response => {
          this.loading = false
          this.devices = response.data.result.visitorKiosks
            ? response.data.result.visitorKiosks
            : []
        })
        .catch(() => {
          this.devices = []
          this.loading = false
        })
    },

    onOptionsSelect(command, device, index) {
      if (command === 'edit') {
        this.device = device
        this.showForm = true
        this.isEdit = true
      } else if (command === 'delete') {
        this.$dialog
          .confirm({
            title: 'Delete device',
            message: 'Are you sure you want to delete this device?',
            rbDanger: true,
            rbLabel: this.$t('common._common.delete'),
          })
          .then(value => {
            if (value) {
              this.deleteDevice(device.id)
            }
          })
      } else if (command === 'connect') {
        this.showConnectPopover = true
        this.showConnectDialogTitle = 'Connect Device'
        this.device = device
      } else if (command === 'reconnect') {
        this.showConnectPopover = true
        this.showConnectDialogTitle = 'Reconnect Device'
        this.device = device
      } else if (command == 'disconnect') {
        this.$dialog
          .confirm({
            title: 'Disconnect device',
            message: 'Are you sure you want to disconnect this device?',
            rbDanger: true,
            rbLabel: this.$t('setup.devices.disconnect'),
          })
          .then(value => {
            if (value) {
              this.disconnectDevice(device.id)
            }
          })
      }
    },

    disconnectDevice(deviceId) {
      this.$http
        .post('v2/devices/disconnect', {
          device: {
            id: deviceId,
          },
        })
        .then(() => {
          this.loadVisitorKioskList()
        })
        .catch(() => {
          this.$message.error('error disconnecting device')
        })
    },
    deleteDevice(deviceId) {
      this.$http
        .post('v2/devices/delete', {
          device: {
            id: deviceId,
          },
        })
        .then(() => {
          this.loadVisitorKioskList()
        })
        .catch(() => {
          {
            this.$message.error('error deleting device')
          }
        })
    },
  },
}
</script>
