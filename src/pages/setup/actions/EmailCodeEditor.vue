<template>
  <div>
    <el-dialog
      class="fc-dialog-center-container"
      :append-to-body="true"
      title="HTML Editor"
      :visible.sync="visibility"
      width="50%"
      :before-close="closeDialog"
    >
      <div style="height: 443px;">
        <el-radio-group v-model="codeShowAndPreview">
          <el-radio label="HTML Editor" class="fc-radio-btn"
            >HTML Editor</el-radio
          >
          <el-radio label="Preview" class="fc-radio-btn">Preview</el-radio>
        </el-radio-group>
        <div v-if="codeShowAndPreview === 'HTML Editor'" class="mT20">
          <code-mirror :codeeditor="true" v-model="htmlEditor"></code-mirror>
        </div>
        <div
          v-if="codeShowAndPreview === 'Preview'"
          class="mT20 html-editor-preview"
          v-html="htmlEditor"
        >
          {{ htmlEditor }}
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button type="primary" @click="save()" class="modal-btn-save"
          >Insert</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import CodeMirror from '@/CodeMirror'
export default {
  props: ['visibility', 'value'],
  data() {
    return {
      htmlEditor: '',
      codeShowAndPreview: 'HTML Editor',
      content: '',
      // showScriptDialog: false
    }
  },
  components: {
    CodeMirror,
  },
  mounted() {
    this.htmlEditor = this.value
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    save() {
      if (!this.htmlEditor.includes('<script')) {
        this.$emit('input', this.htmlEditor)
        this.closeDialog()
      }
    },
  },
}
</script>
<style lang="scss">
.html-editor-preview {
  height: 320px;
  border: 1px solid #eeeeee;
  overflow-y: scroll;
  padding-bottom: 20px;
  padding: 20px;
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #324056;
  line-height: 24px;
  word-break: break-word;
}
</style>
