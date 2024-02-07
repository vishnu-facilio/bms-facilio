<template>
  <div>
    <div v-if="field.name === 'document'">
      <div v-if="moduleData.documentFileName">
        <span
          class="insurance-link"
          @click="previewAttachment(moduleData.documentUrl)"
          >{{ moduleData.documentFileName }}</span
        >
      </div>
      <div v-else>---</div>
    </div>
    <el-dialog
      :visible.sync="visibility"
      :append-to-body="true"
      class="f-list-attachment-dialog"
    >
      <preview-file
        :visibility.sync="visibility"
        v-if="visibility"
        :previewFile="getFormattedFile"
        :files="[getFormattedFile]"
      ></preview-file>
    </el-dialog>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
export default {
  props: ['field', 'moduleData'],
  components: {
    PreviewFile,
  },
  data() {
    return {
      visibility: false,
    }
  },
  mounted() {},
  computed: {
    getFormattedFile() {
      return {
        contentType: this.moduleData.documentContentType || '',
        fileName: this.moduleData.documentName,
        previewUrl: this.moduleData.documentUrl,
        downloadUrl: this.moduleData.documentDownloadUrl,
      }
    },
  },
  methods: {
    previewAttachment(fileUrl) {
      if (fileUrl) {
        this.visibility = true
      }
    },
  },
}
</script>
