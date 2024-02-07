<template>
  <div>
    <el-dialog
      class="fc-dialog-center-container fc-dialog-center-body-p0 fc-html-container fc-html-editor-preview-page"
      :append-to-body="true"
      title="HTML Editor"
      :visible="true"
      width="85%"
      :before-close="closeDialog"
    >
      <div class="fc-genearate-html-codes" @click="generateHtmlCodes">
        Generate html codes
      </div>

      <div class="fc-editor-and-preview">
        <div class="width50">
          <code-mirror
            :codeeditor="true"
            v-model="htmlEditor"
            :options="{ mode: 'text/javascript' }"
          ></code-mirror>
        </div>
        <div class="width50">
          <div class="html-editor-preview" v-html="htmlEditor"></div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('setup.users_management.cancel')
        }}</el-button>
        <el-button type="primary" @click="save()" class="modal-btn-save"
          >Insert</el-button
        >
      </div>
    </el-dialog>
    <CodeGenerator
      v-if="showGenerateCode"
      @onCloseGenerator="showGenerateCode = false"
    >
    </CodeGenerator>
  </div>
</template>
<script>
import CodeMirror from '@/CodeMirror'
import CodeGenerator from 'pages/setup/EmailTemplates/GenerateCodes'
export default {
  props: ['visibility', 'value'],
  data() {
    return {
      htmlEditor: '',
      codeShowAndPreview: 'HTML Editor',
      content: '',
      showGenerateCode: false,
    }
  },
  components: {
    CodeMirror,
    CodeGenerator,
  },
  mounted() {
    this.htmlEditor = this.value
  },
  methods: {
    closeDialog(editor = null) {
      this.$emit('onClose', editor)
    },
    save() {
      if (!this.htmlEditor.includes('<script')) {
        this.closeDialog(this.htmlEditor)
        this.$emit('onSave', this.htmlEditor)
      }
    },
    generateHtmlCodes() {
      this.showGenerateCode = true
    },
  },
}
</script>
<style lang="scss">
.html-editor-preview {
  height: calc(100vh - 200px);
  border-left: 1px solid #eeeeee;
  overflow-y: scroll;
  padding: 20px;
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #324056;
  line-height: 24px;
  word-break: break-word;
  padding-bottom: 100px;
  p {
    font-size: 14px;
    margin-bottom: 0;
  }
}
.fc-html-container {
  .CodeMirror {
    height: calc(100vh - 150px);
  }
  .el-dialog {
    margin-top: 5vh !important;
  }
  .fc-editor-and-preview {
    width: 100%;
    height: calc(100vh - 150px);
    display: flex;
    flex-direction: row;
  }
}
.fc-genearate-html-codes {
  position: absolute;
  top: 15px;
  right: 50px;
  cursor: pointer;
  background: #1763e2;
  color: #fff;
  padding: 5px 10px;
  border-radius: 4px;
}
.fc-html-editor-preview-page {
  table {
    width: 100%;
    th {
      font-weight: 400;
      text-align: left;
      background-color: #fafbff;
      min-width: 1em;
      border: 1px solid #d0d9e2;
      padding: 5px;
      vertical-align: top;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      position: relative;
    }
    td {
      min-width: 1em;
      border: 1px solid #d0d9e2;
      padding: 5px;
      vertical-align: top;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      position: relative;
    }
  }
}
</style>
