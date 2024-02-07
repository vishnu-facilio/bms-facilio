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
          <div class="fc-setup-modal-title">Agent points helper</div>
        </div>
      </div>
      <div class="new-body-modal pT0 pL0 pR0 mT0">
        <el-collapse
          v-model="activeNames"
          accordion
          class="fc-agent-collapse fc-pushing-data-collapse"
        >
          <el-collapse-item title="Discover" name="1">
            <div class="fc-black-14 text-left line-height25 break-word">
              Discover action is used to discover the points in the selected
              controller(s). This action currently supports only BACNet IP, OPC
              XML/DA and OPC UA protocols in Facilio Agent.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Configure" name="2">
            <div class="fc-black-14 text-left line-height25 break-word">
              This action is used to configure the points in which we are
              interested. These values of these points are collected at the
              configured interval of the agent and published to the cloud.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Unconfigure" name="3">
            <div class="fc-black-14 text-left line-height25 break-word">
              This action is used to unconfigure the points for which we no
              longer need the values.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Add Points" name="4">
            <div class="fc-black-14 text-left line-height25 break-word">
              This action is used to add points to the agent for the protocols
              for which the discovery is not possible. Currently we add points
              for Modbus protocols in Facilio Agent.
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
