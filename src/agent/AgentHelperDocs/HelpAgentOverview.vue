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
          <div class="fc-setup-modal-title">
            Agent {{ $t('agent.agent.agents') }}
          </div>
        </div>
      </div>
      <div class="new-body-modal pT0 pL0 pR0 mT0">
        <el-collapse
          v-model="activeNames"
          accordion
          class="fc-agent-collapse fc-pushing-data-collapse"
        >
          <el-collapse-item title="Overview" name="1">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc1') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Agents" name="2">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc2') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Controllers" name="3">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc3') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Points" name="4">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc4') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Commissioning" name="5">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc5') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Alarms" name="6">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc6') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Alarm Notifications" name="7">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc7') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Metrics" name="8">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc8') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Command Log" name="9">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc9') }}
            </div>
          </el-collapse-item>
          <el-collapse-item title="Agent Data" name="10">
            <div class="fc-black-14 text-left line-height25 break-word">
              {{ $t('agent.agent_overview_helper.agent_overview_desc10') }}
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
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
