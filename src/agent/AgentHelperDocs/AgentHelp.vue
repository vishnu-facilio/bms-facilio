<template>
  <div>
    <el-dialog
      title="Agent Helper"
      :visible.sync="helpDialogVisible"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right width30 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class>
        <div class="new-header-container pL20">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ $t('agent.agent_helper.agent_helper') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal pT0 pL0 pR0 mT0">
          <el-collapse
            v-model="activeNames"
            accordion
            class="fc-agent-collapse fc-pushing-data-collapse"
          >
            <el-collapse-item title="Add Agent" name="1">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc1') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Reboot" name="2">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc2') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Shutdown" name="3">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc3') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Upgrade" name="4">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc4') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Agent" name="5">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc5') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Controllers" name="6">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc6') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Location" name="7">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc7') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Data Received" name="8">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc8') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Interval" name="9">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc9') }}
              </div>
            </el-collapse-item>
            <el-collapse-item title="Writable" name="10">
              <div class="fc-black-14 text-left line-height25 break-word">
                {{ $t('agent.agent_helper.agent_desc10') }}
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: ['helpDialogVisible'],
  data() {
    return {
      activeNames: ['1'],
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:helpDialogVisible', false)
    },
  },
}
</script>

<style lang="stylus" scoped></style>
