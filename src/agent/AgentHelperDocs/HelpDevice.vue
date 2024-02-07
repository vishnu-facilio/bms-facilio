<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog25 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">Agent Controller Helper</div>
        </div>
      </div>
      <div class="new-body-modal pT0 pL0 pR0 mT0">
        <el-collapse
          v-model="activeNames"
          accordion
          class="fc-agent-collapse fc-pushing-data-collapse"
        >
          <el-collapse-item title="Add Device" name="1">
            <div class="fc-black-14 text-left line-height25 break-word">
              Add a new device in the agent. This is currently supported for
              Modbus and OPC protocols. The device will be added only if the
              agent is able to connect the device. This is currently supported
              only in Facilio Agent.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Configured" name="2">
            <div class="fc-black-14 text-left line-height25 break-word">
              Controllers whose points have been configured to get their value
              at every interval is listed here.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Unconfigured" name="3">
            <div class="fc-black-14 text-left line-height25 break-word">
              Controllers that don't have any configured point are listed here.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Discover Devices" name="4">
            <div class="fc-black-14 text-left line-height25 break-word">
              Facilio and Niagara agents are capable of discovering BACNet
              devices where the agent is deployed. Discovered devices will be
              added and listed.
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: ['visibility'],
  data() {
    return {
      activeNames: ['1'],
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
