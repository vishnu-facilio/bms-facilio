<template>
  <el-dialog
    :visible="showRichTextPreview"
    :append-to-body="true"
    :title="richTextPreviewTitle"
    width="45%"
    class="fc-dialog-center-container rich-text-preview-dialog"
    :before-close="closeDialog"
  >
    <div class="wo-rich-text-area">
      <div class="rich-text-preview-details" v-html="richTextContent"></div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'RichTextPreview',
  props: ['richTextField', 'richTextContent', 'showRichTextPreview'],
  computed: {
    richTextPreviewTitle() {
      let { richTextField = {} } = this
      return this.$getProperty(richTextField, 'displayName', '---')
    },
  },
  methods: {
    closeDialog() {
      this.$emit('closeRichTextPreview')
    },
  },
}
</script>

<style scoped lang="scss">
.wo-rich-text-area {
  height: 300px;
  overflow: scroll;
  .rich-text-preview-details {
    display: flex;
    white-space: pre-line;
    word-break: break-word;
  }
}
</style>
<style lang="scss">
.rich-text-preview-dialog {
  .el-dialog {
    padding-bottom: 30px !important;
    border-radius: 5px !important;
    .el-dialog__header {
      padding: 10px 20px 5px;
      height: 45px;
    }
    .el-dialog__headerbtn {
      top: 13px !important;
    }
    .el-dialog__title {
      text-transform: capitalize;
      color: #324056;
      font-size: 16px;
      font-weight: bold;
      letter-spacing: 0.4px;
    }
    .el-dialog__body {
      padding: 10px 17px 5px !important;
    }
  }
  .rich-text-container {
    p:empty::after {
      content: '\00A0';
    }
    blockquote {
      padding: 0px 0px 0px 1rem;
      border-left: 3px solid rgba(#0d0d0d, 0.1);
      font-size: 1em;
    }
    img {
      max-width: 100%;
      height: auto;
    }
    p {
      margin-block-start: 0px !important;
      margin-block-end: 0px !important;
    }
    .tableWrapper {
      padding: 1rem 0;
      overflow-x: auto;
    }
    ul {
      padding: 0 1rem;
      list-style-type: disc;
    }
    ol {
      padding: 0 1rem;
      list-style: auto;
    }
    table {
      border-collapse: collapse;
      table-layout: fixed;
      width: 100% !important;
      margin: 0;
      overflow: hidden;

      td,
      th {
        min-width: 1em;
        border: 2px solid #ced4da;
        padding: 3px 5px;
        vertical-align: top;
        box-sizing: border-box;
        position: relative;

        > * {
          margin-bottom: 0;
        }
      }

      th {
        font-weight: bold;
        text-align: left;
        background-color: #f1f3f5;
      }

      .richtext-selectedCell:after {
        z-index: 2;
        position: absolute;
        content: '';
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        background: rgba(200, 200, 255, 0.4);
        pointer-events: none;
      }

      .column-resize-handle {
        position: absolute;
        right: -2px;
        top: 0;
        bottom: -2px;
        width: 4px;
        background-color: #adf;
        pointer-events: none;
      }

      p {
        margin: 0;
      }
    }
    h1 {
      font-size: 2.5em;
      margin: 1.75em 0;
    }
    h2 {
      font-size: 1.85em;
      margin: 1.5em 0;
    }
    h3 {
      font-size: 1.5em;
      margin: 1em 0;
    }
    h1,
    h2,
    h3 {
      font-weight: 300;
    }
    p {
      font-size: 1em;
      margin: 0 0 1em;
      padding: 0;
      letter-spacing: 0;
      -webkit-font-smoothing: antialiased;
    }
  }
}
</style>
