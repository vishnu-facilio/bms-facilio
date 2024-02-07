<template>
  <div class="height100 width100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('setup.smart_controls.smart_controls') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.smart_controls.customize_smart_controls') }}
        </div>
      </div>
      <div>
        <el-button
          type="primary"
          class="el-button setup-el-btn el-button--primary scale-up-left"
          @click="openAddDeviceDialog()"
          >{{ $t('common.header.add_smart_control_kiosk') }}</el-button
        >
      </div>
    </div>
    <div class="container-scroll overflow-y">
      <div class="row setting-Rlayout overflow-y">
        <!-- list -->
        <div class="width100">
          <div class="visitor-hor-card scale-up-left" v-if="loading">
            <spinner :show="loading" size="80"></spinner>
          </div>

          <div
            class="visitor-hor-card scale-up-left flex-middle flex-direction-column justify-content-center"
            v-else-if="devices.length === 0"
          >
            <InlineSvg
              src="svgs/emptystate/approval"
              iconClass="icon icon-xxxlg vertical-middle mR10"
            >
            </InlineSvg>
            <div class="text-center nowo-label">
              {{ $t('setup.setup_empty_state.smartcontrolempty') }}
            </div>
          </div>

          <div v-else class="mT20 smart-controls-table">
            <el-table :data="devices" style="width: 100%" height="auto">
              <el-table-column label="Device Name" width="230">
                <template v-slot="device">
                  <div
                    class="agent-active-section flex-middle"
                    style="align-items: flex-start;"
                  >
                    <AgentActivityDot
                      :key="device.row.name"
                      :state="device.row.isDeviceConnected"
                      :status="device.row.isDeviceConnected"
                      class="mT5"
                    >
                    </AgentActivityDot>
                    <div class="mL10">
                      <div class="label-txt3-14">
                        {{ device.row.name }}
                      </div>
                      <div class="label-txt3-13 flex-middle">
                        <span class="fc-grey2 f13 pR5"
                          >{{ $t('common.header.status_:') }}
                        </span>

                        {{
                          device.row.isDeviceConnected
                            ? 'Connected'
                            : 'Disconnected'
                        }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column :label="$t('common._common.space_floor')">
                <template v-slot="device">
                  <div class="label-txt3-14">
                    <img
                      src="~statics/space/space-resource.svg"
                      :title="$t('common.wo_report.device_location')"
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
              <el-table-column :label="$t('common.header.tenant')" width="200">
                <template v-slot="device">
                  {{ $getProperty(device.row, 'tenant.name', '---') }}
                </template>
              </el-table-column>
              <el-table-column class="visibility-visible-actions">
                <template v-slot="device">
                  <template v-if="device.row.isDeviceConnected">
                    <el-button
                      type="primary"
                      class="fc-btn-green-medium-fill shadow-none visibility-hide-actions"
                      @click="onOptionsSelect('disconnect', device.row, index)"
                      >{{ $t('common.header.disconnect') }}</el-button
                    >
                  </template>
                  <template v-else>
                    <el-button
                      type="primary"
                      class="fc-btn-green-medium-fill shadow-none visibility-hide-actions"
                      @click="onOptionsSelect('connect', device.row, index)"
                      >{{ $t('common.header.connect') }}</el-button
                    >
                  </template>
                </template>
              </el-table-column>
              <el-table-column>
                <template v-slot="device">
                  <div v-on:click.stop>
                    <el-dropdown
                      @command="onOptionsSelect($event, device.row, index)"
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

          <smart-controls-form
            v-if="showForm"
            :isEdit="isEdit"
            :kioskContext="device"
            :drawerVisibility.sync="showForm"
            @save="handleFormSubmit"
          ></smart-controls-form>

          <connect-device
            @connected="loadDevices"
            :title="showConnectDialogTitle"
            :visibility.sync="showConnectPopover"
            :device="device"
          >
            {{ $t('common.header.access') }}
            <b>{{ $t('common._common.facilio_smart_control') }} </b>
            {{ $t('common.dialog.app_on_your_tablet_phone_to_passcode') }}
          </connect-device>
        </div>
        <!-- list -->
      </div>
    </div>
  </div>
</template>

<script>
import connectDevice from 'src/pages/setup/ConnectDevice'
import AgentActivityDot from 'pages/setup/agent/AgentActivityDot'
import smartControlsForm from 'pages/setup/SmartControlsForm'
export default {
  title() {
    return 'Smart Control Settings'
  },
  components: {
    AgentActivityDot,
    smartControlsForm,
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
      showPrinterDialog: false,
      printerList: [],
      selectedPrinter: null,
      // showDeviceConnectPopover:false,
      //   addCode: {
      //   code: null
      // }
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

    //TO DO , remove facilio web form and use plain el-drawer
    handleFormSubmit(e) {
      console.log('form submitted', e)
      // deviceObj.id = this.device.id
      if (this.isEdit) {
        let kiosk = e.formModel
        kiosk.id = this.device.id
        this.$http
          .post('v2/smartControlKiosk/update', {
            smartControlKiosk: kiosk,
          })
          .then(() => {
            this.showForm = false
            this.loadDevices()
          })
      } else {
        this.$http
          .post('v2/smartControlKiosk/add', {
            smartControlKiosk: e.formModel,
          })
          .then(() => {
            this.showForm = false
            this.loadDevices()
          })
      }
    },

    loadDevices() {
      this.loading = true
      this.$http
        .get('/v2/smartControlKiosk/list')
        .then(response => {
          this.loading = false
          this.devices = response.data.result.smartControlKiosks
            ? response.data.result.smartControlKiosks
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
        this.isEdit = true
        this.showForm = true
      } else if (command === 'delete') {
        this.$dialog
          .confirm({
            title: this.$t('common.wo_report.delete_device_title'),
            message: this.$t('common._common.delete_device_message'),
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
