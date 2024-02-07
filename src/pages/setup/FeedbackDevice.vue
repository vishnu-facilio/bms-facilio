<template>
  <div class="">
    <div class="visitor-hor-card scale-up-left" v-if="loading">
      <spinner :show="loading" size="80"></spinner>
    </div>

    <div
      class="text-center fc-empty-white flex-middle justify-center flex-col"
      v-else-if="devices.length === 0"
    >
      <InlineSvg
        src="svgs/emptystate/approval"
        iconClass="icon icon-xxxlg vertical-middle mR10"
      >
      </InlineSvg>
      <div class="nowo-label">
        {{ $t('setup.setup_empty_state.feedbackEmpty') }}
      </div>
    </div>

    <div v-else class="mT20 feedback-device-table">
      <el-table :data="devices" style="width: 100%" height="auto">
        <el-table-column
          :label="$t('common.wo_report.device_name')"
          width="230"
        >
          <template v-slot="device">
            <div
              class="agent-active-section flex-middle"
              style="align-items: flex-start;"
            >
              <AgentActivityDot
                :key="device.row.name"
                :state="device.row.isDeviceConnected"
                :status="device.row.isDeviceConnected"
              >
              </AgentActivityDot>
              <div class="mL10">
                <div class="label-txt3-14">
                  {{ device.row.name }}
                </div>
                <div class="label-txt3-13 flex-middle">
                  <span class="fc-grey2 f13 pR5"
                    >{{ $t('setup.users_management.status') }}:
                  </span>

                  {{
                    device.row.isDeviceConnected ? 'Connected' : 'Disconnected'
                  }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('common._common.feedback_types')"
          width="170"
        >
          <template v-slot="device">
            {{ device.row.feedbackType.name }}
          </template>
        </el-table-column>
        <!-- <el-table-column label="Space" width="170">
          <template v-slot="device">
            {{
              device.row.associatedResource
                ? device.row.associatedResource.name
                : '---'
            }}
          </template>
        </el-table-column> -->
        <el-table-column
          :label="$t('common.header.associated_types')"
          width="160"
        >
          <template v-slot="device">
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
                device.row.associatedResource
                  ? device.row.associatedResource.name
                  : '---'
              }}
            </div>
          </template>
        </el-table-column>
        <el-table-column class="visibility-visible-actions" width="250">
          <template v-slot="device">
            <div class="flex-middle">
              <template v-if="device.row.isDeviceConnected">
                <el-button
                  type="primary"
                  class="fc-btn-green-medium-fill shadow-none visibility-hide-actions f11"
                  @click="onOptionsSelect('disconnect', device.row)"
                  >{{ $t('setup.devices.disconnect') }}</el-button
                >
              </template>
              <template v-else>
                <el-button
                  type="primary"
                  class="fc-btn-green-medium-fill shadow-none visibility-hide-actions f11"
                  @click="onOptionsSelect('connect', device.row)"
                  >{{ $t('common.header.connect') }}</el-button
                >
              </template>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="100" fixed="right">
          <template v-slot="device">
            <div v-on:click.stop>
              <el-dropdown
                @command="onOptionsSelect($event, device.row)"
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
                  <el-dropdown-item command="edit">{{
                    $t('common._common.edit')
                  }}</el-dropdown-item>

                  <el-dropdown-item command="delete">
                    {{ $t('common._common.delete') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <feedback-kiosk-form
      v-if="showForm"
      :isEdit="isEdit"
      :kioskContext="device"
      :drawerVisibility.sync="showForm"
      @save="handleFormSubmit"
    ></feedback-kiosk-form>

    <connect-device
      @connected="loadDevices"
      :title="showConnectDialogTitle"
      :visibility.sync="showConnectPopover"
      :device="device"
    >
      Access <b>Facilio Feedback</b> app on your tablet/phone to obtain the 6
      digit passcode
    </connect-device>
  </div>
</template>

<script>
import connectDevice from 'src/pages/setup/ConnectDevice'
import AgentActivityDot from 'pages/setup/agent/AgentActivityDot'
import feedbackKioskForm from 'pages/setup/FeedbackKioskForm'
export default {
  title() {
    return this.$t('common._common.devices')
  },
  components: {
    AgentActivityDot,
    feedbackKioskForm,
    connectDevice,
  },
  data() {
    return {
      isEdit: false,
      loading: true,
      devices: [],
      showForm: false,
      buildings: {},
      device: null,
      metaInfo: null,
      showConnectPopover: false,
      showConnectDialogTitle: null,
    }
  },
  created() {
    this.$util.loadModuleMeta('devices').then(meta => {
      console.log('device module meta', meta)
      this.metaInfo = meta
      this.loadDevices()
    })
  },

  mounted() {},
  methods: {
    openAddDeviceDialog() {
      this.isEdit = false
      this.showForm = true
    },

    handleFormSubmit(e) {
      console.log('form submitted', e)

      if (this.isEdit) {
        let kiosk = e.formModel
        kiosk.id = this.device.id

        this.$http
          .post('v2/feedbackKiosk/update', { feedbackKiosk: kiosk })
          .then(() => {
            this.showForm = false
            this.loadDevices()
          })
      } else {
        this.$http
          .post('v2/feedbackKiosk/add', { feedbackKiosk: e.formModel })
          .then(() => {
            this.showForm = false
            this.loadDevices()
          })
      }
    },

    loadDevices() {
      this.loading = true

      this.$http
        .get('/v2/feedbackKiosk/list')
        .then(response => {
          this.loading = false
          this.devices = response.data.result.feedbackKiosks
            ? response.data.result.feedbackKiosks
            : []
        })
        .catch(() => {
          this.devices = []
          this.loading = false
        })
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
            title: this.$t('common.wo_report.disconnect_device_title'),
            message: this.$t('common.wo_report.disconnect_device_message'),
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
    deleteDevice(deviceId) {
      this.$http
        .post('v2/devices/delete', {
          device: {
            id: deviceId,
          },
        })
        .then(() => {
          this.loadDevices()
        })
        .catch()
      {
        console.log('error deleting device')
      }
    },
  },
}
</script>
