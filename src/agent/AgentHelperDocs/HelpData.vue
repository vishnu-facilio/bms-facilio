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
          <div class="fc-setup-modal-title">Agent Data Helper</div>
        </div>
      </div>
      <div class="new-body-modal pT0 pL0 pR0 mT0">
        <el-collapse
          v-model="activeNames"
          accordion
          class="fc-agent-collapse fc-pushing-data-collapse"
        >
          <el-collapse-item title="Time" name="1">
            <div class="fc-black-14 text-left line-height25 break-word">
              Time at which the message was sent by the agent.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Agent" name="2">
            <div class="fc-black-14 text-left line-height25 break-word">
              Name of the agent which sent the message.
            </div>
          </el-collapse-item>
          <el-collapse-item title="Data" name="3">
            <div class="fc-black-14 text-left line-height25 break-word">
              Message which was sent by the Agent
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
