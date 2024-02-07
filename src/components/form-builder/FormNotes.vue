<template>
  <el-dialog
    :title="$t('asset.assets.notes')"
    :visible.sync="canShow"
    :append-to-body="true"
    width="40%"
    class="fc-dialog-center-container notes-editor"
  >
    <RichTextArea
      v-model="notesFieldValue"
      :isRichTextMode.sync="isRichTextMode"
      :field="field"
      :disabled="field.isDisabled"
      :rows="8"
      :maxRows="6"
      :hideImgTool="true"
      :hideOrderedList="true"
      :hideUnorderedList="true"
      :canShowModeToggle="true"
    />
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()"
        >CANCEL</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="saveNotes"
        >SAVE</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import RichTextArea from '@/forms/RichTextArea'
import isEqual from 'lodash/isEqual'

export default {
  props: {
    field: {
      type: Object,
      required: true,
    },
    canShowRichText: {
      type: Boolean,
      required: true,
    },
  },
  components: {
    RichTextArea,
  },
  computed: {
    canShow: {
      get() {
        return this.canShowRichText
      },
      set(value) {
        this.$emit('update:canShowRichText', value)
      },
    },
  },
  watch: {
    'field.value': {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.notesFieldValue = newVal
        }
      },
      immediate: true,
    },
  },
  data() {
    return {
      notesFieldValue: '',
      isRichTextMode: true,
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      let { field } = this
      let { config } = field || {}
      let { isRichTextMode = true } = config || {}
      this.isRichTextMode = isRichTextMode
    },
    saveNotes() {
      let { notesFieldValue, isRichTextMode, field } = this
      field.value = notesFieldValue
      field.config = {
        ...field.config,
        isRichTextMode,
      }
      this.closeDialog()
    },
    closeDialog() {
      this.$set(this, 'canShow', false)
    },
  },
}
</script>
<style lang="scss">
.fc-dialog-center-container {
  &.notes-editor {
    .el-dialog__body {
      height: 400px;
    }
  }
}
</style>
