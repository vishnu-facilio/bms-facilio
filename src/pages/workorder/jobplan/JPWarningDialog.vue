<template>
  <el-dialog
    class="fc-dialog-center-container dialog-header-remove"
    :visible.sync="showDialog"
    width="35%"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height180 d-flex">
      <i class="fa fa-exclamation-triangle pR5 pL10" aria-hidden="true"></i>
      <div class="d-flex flex-direction-column mL10 width100">
        <div class="dialog-heading">
          {{ dialogHeader }}
        </div>
        <div v-html="dialogContent" class="dialog-content mT15"></div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">{{
        $t('agent.agent.cancel')
      }}</el-button>
      <el-button
        type="primary"
        @click="saveDialog"
        :loading="stateUpdating"
        class="modal-btn-save"
        >{{ buttonText }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
// Hash for i18n Text Mapping
const dialogHeaderHash = {
  jobplan: {
    Edit: 'jobplan.edit_jp',
    Publish: 'jobplan.publish_jp',
    Unpublish: 'jobplan.unpublish_jp',
    Disable: 'jobplan.disable_jp',
  },
  plannedmaintenance: {
    Edit: 'maintenance.pm._edit_pm',
    Publish: 'maintenance.pm.publish_pm',
    Unpublish: 'maintenance.pm.unpublish_pm',
    Disable: 'maintenance.pm.disable_pm',
  },
}
const dialogContentHash = {
  jobplan: {
    Edit: 'jobplan.edit_jp_content',
    Publish: 'jobplan.publish_jp_content',
    Unpublish: 'jobplan.unpublish_jp_content',
    Disable: 'jobplan.disable_jp_content',
  },
  plannedmaintenance: {
    Edit: 'maintenance.pm.edit_pm_content',
    Publish: 'maintenance.pm.publish_pm_content',
    Unpublish: 'maintenance.pm.unpublish_pm_content',
    Disable: 'maintenance.pm.disable_pm_content',
  },
}
const buttonTextHash = {
  jobplan: {
    Edit: 'jobplan.edit',
    Publish: 'jobplan.publish',
    Unpublish: 'jobplan.unpublish',
    Disable: 'jobplan.disable',
  },
  plannedmaintenance: {
    Edit: 'jobplan.edit',
    Publish: 'jobplan.publish',
    Unpublish: 'jobplan.unpublish',
    Disable: 'jobplan.disable',
  },
}

export default {
  props: [
    'showDialog',
    'dialogType',
    'moduleName',
    'stateUpdating',
    'recordId',
  ],
  computed: {
    dialogHeader() {
      let { dialogType, moduleName } = this
      let dialogHeader = this.$getProperty(
        dialogHeaderHash,
        `${moduleName}.${dialogType}`,
        'jobplan.edit'
      )

      return this.$t(`${dialogHeader}`)
    },
    dialogContent() {
      let { dialogType, moduleName } = this
      let dialogContent = this.$getProperty(
        dialogContentHash,
        `${moduleName}.${dialogType}`,
        'jobplan.edit_content'
      )

      return this.$t(`${dialogContent}`)
    },
    buttonText() {
      let { dialogType, moduleName } = this
      let buttonText = this.$getProperty(
        buttonTextHash,
        `${moduleName}.${dialogType}`,
        'jobplan.edit'
      )

      return this.$t(`${buttonText}`)
    },
  },
  methods: {
    closeDialog() {
      this.$emit('closeDialog')
    },
    saveDialog() {
      let { dialogType } = this
      this.$emit('saveAction', dialogType)
    },
  },
}
</script>
<style scoped>
.fa-exclamation-triangle {
  color: #efa82e;
  font-size: 22px;
  margin-right: 10px;
}
.dialog-heading {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #2d2d51;
}
.dialog-content {
  font-size: 14px;
  line-height: 1.31;
  letter-spacing: 0.5px;
  color: #2d2d51;
  word-break: break-word;
}
.height180 {
  height: 195px !important;
  overflow-y: scroll;
}
</style>
