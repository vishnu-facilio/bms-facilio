<template>
  <div>
    <el-image
      :src="file.fileUrl"
      @click="openAttachmentsPreview(file)"
      fit="fill"
      class="fc-image-list-preview fc-avatar-square list-image-preview"
    >
      <div
        slot="error"
        @click="openAttachmentsPreview(file)"
        class="image-slot"
      >
        <InlineSvg
          v-if="file.fileUrl"
          src="clip"
          iconClass="icon fill-grey icon-xxlll op5"
        ></InlineSvg>
        <InlineSvg
          v-else
          src="svgs/photo"
          iconClass="icon fill-grey icon-xxlll op5"
        ></InlineSvg>
      </div>
    </el-image>
    <el-dialog
      :visible.sync="visibility"
      :append-to-body="true"
      class="f-list-attachment-dialog"
    >
      <preview-file
        v-if="visibility"
        :visibility.sync="visibility"
        :previewFile="getFormattedFile"
        :files="[getFormattedFile]"
      ></preview-file>
    </el-dialog>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
export default {
  props: ['file'],
  components: {
    PreviewFile,
  },
  data() {
    return {
      visibility: false,
    }
  },
  computed: {
    getFormattedFile() {
      return {
        contentType: this.file.fileContentType || '',
        fileName: this.file.fileName,
        previewUrl: this.file.fileUrl,
        downloadUrl: this.file.fileUrl,
      }
    },
  },
  methods: {
    openAttachmentsPreview(file) {
      if (file.fileUrl) {
        this.visibility = true
      }
    },
  },
}
</script>
