<template>
  <div class="">
    <div class="visitor-hor-card scale-up-left" v-if="loading">
      <spinner :show="loading" size="80"></spinner>
    </div>

    <div
      class="visitor-hor-card scale-up-left d-flex align-center justify-content-center flex-direction-column"
      v-else-if="customkiosk.length === 0"
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
        class="visitor-hor-card scale-up-left visibility-visible-actions"
        v-for="(device, index) in customkiosk"
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
          <el-col :span="7" width="100" class="visibility-hide-actions">
            <template>
              <i
                class="el-icon-delete delete-icon-danger"
                style="float:right"
                @click="onOptionsSelect('delete', device)"
              ></i>
              <i
                class="el-icon-edit pR20"
                style="float:right"
                @click="onOptionsSelect('edit', device)"
              ></i>
            </template>
          </el-col>
        </el-row>
      </div>
    </div>

    <customKioskForm
      v-if="showForm"
      :isEdit="isEdit"
      :kioskContext="device"
      :drawerVisibility.sync="showForm"
      @save="handleFormSubmit"
    ></customKioskForm>

    <connect-device
      @connected="loadDevices"
      :title="showConnectDialogTitle"
      :visibility.sync="showConnectPopover"
      :device="device"
    >
      Access <b>Facilio Device</b> app on your tablet/phone to obtain the 6
      digit passcode
    </connect-device>
  </div>
</template>

<script>
import connectDevice from 'src/pages/setup/ConnectDevice'
import AgentActivityDot from 'pages/setup/agent/AgentActivityDot'
import customKioskForm from 'pages/setup/CustomKioskForm'
import { API } from '@facilio/api'
export default {
  title() {
    return this.$t('common._common.devices')
  },
  components: {
    AgentActivityDot,
    customKioskForm,
    connectDevice,
  },
  data() {
    return {
      isEdit: false,
      loading: true,
      customkiosk: [],
      showForm: false,
      buildings: {},
      device: null,
      metaInfo: null,
      showConnectPopover: false,
      showConnectDialogTitle: null,
      customkioskbutton: [],
    }
  },
  created() {
    this.$util.loadModuleMeta('devices').then(meta => {
      console.log('device module meta', meta)
      this.metaInfo = meta
    })
  },
  async mounted() {
    this.loading = true
    await this.loadDevices()
    this.loading = false
  },
  methods: {
    openAddDeviceDialog() {
      this.isEdit = false
      this.showForm = true
    },

    handleFormSubmit(e) {
      console.log('form submitted', e)
      let params = { moduleName: 'customkiosk', data: e.formModel }
      if (this.isEdit) {
        params = { ...params, id: this.device.id }
        this.$http.post('/v3/modules/data/update', params).then(() => {
          this.showForm = false
          this.loadDevices()
        })
      } else {
        this.$http.post('/v3/modules/data/create', params).then(() => {
          this.showForm = false
          this.loadDevices()
        })
      }
    },

    async loadDevices() {
      let queryParam = {
        moduleName: 'customkiosk',
      }

      let { data } = await API.get('/v3/modules/data/list', queryParam)
      this.customkiosk = data?.customkiosk ? data.customkiosk : {}
    },

    onOptionsSelect(command, device) {
      if (command === 'edit') {
        this.device = device
        this.isEdit = true
        this.showForm = true
      } else if (command === 'delete') {
        this.$dialog
          .confirm({
            title: this.$t('common.wo_report.delete_device_title'),
            message: this.$t('common.wo_report.delete_device_message'),
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
            title: this.$t('common._common.disconnect_device'),
            message: this.$t('common._common.disconnect_this_device'),
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
          this.loadDevices()
        })
        .catch()
      {
        console.log('error disconnecting device')
      }
    },
    async deleteDevice(deviceId) {
      let moduleName = 'customkiosk'
      await API.delete('v3/modules/data/delete', {
        data: { customkiosk: [deviceId] },
        moduleName,
      }).then(() => this.loadDevices())
    },
  },
}
</script>
