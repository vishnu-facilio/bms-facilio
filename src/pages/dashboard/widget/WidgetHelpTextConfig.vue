<template>
  <div class="db-widget-filter-manager">
    <el-dialog
      :visible="visibility"
      class="db-widget-filter-manager-dialog"
      :show-close="false"
      :append-to-body="true"
      title="Customize Help Text"
      width="35%"
    >
      <div class="body">
        <el-checkbox
          class="mB20"
          style="border: none;"
          v-model="checkBoxModel"
          label="Show helptext cue in widgets"
        ></el-checkbox>

        <div class="mB15 help-text-title">
          Help text
        </div>

        <el-input
          type="textarea"
          class="fc-input-full-border-textarea width100 help-text-txt-area "
          resize="none"
          :placeholder="$t('common._common.enter_desc')"
          v-model="helpTextModel"
        ></el-input>
      </div>
      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          Cancel</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveFilterConfig"
          >Done</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { deepCloneObject } from 'util/utility-methods'

export default {
  components: {},
  props: ['visibility', 'widget'],
  created() {
    let widget = deepCloneObject(this.widget)
    let {
      widgetSettings: { showHelpText: checkBoxModel },
      helpText,
    } = widget

    this.checkBoxModel = checkBoxModel || false
    this.helpTextModel = helpText
  },
  data() {
    return {
      checkBoxModel: null,
      helpTextModel: null,
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },

    saveFilterConfig() {
      let widget = deepCloneObject(this.widget)
      widget.widgetSettings.showHelpText = this.checkBoxModel
      widget.helpText = this.helpTextModel

      this.$emit('helpTextChanged', widget)
    },
  },
}
</script>

<style lang="scss">
.db-widget-filter-manager-dialog {
  .el-select {
    width: 250px !important;
  }
  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 180px;
    padding: 20px 30px;
  }

  .help-text-title {
    font-size: 14px;
    color: #324056;
    letter-spacing: 0.5px;
  }

  .modal-btn-save {
    margin-left: 0px !important;
  }
  .help-text-txt-area {
    .el-textarea__inner {
      min-height: 60px !important;
      word-break: normal;
    }
  }
}
</style>
