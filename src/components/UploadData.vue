<template>
  <div class="fc-attachments">
    <div
      class="fc-attachment-row mB100"
      v-for="(attachment, index) in attachments"
      :key="index"
    >
      <div class="attachment-label">{{ attachment.fileName }}</div>
      <div class="attachment-sublabel">
        <span>{{ attachment.fileSize | prettyBytes }}</span>
        <span class="attachment-sublabel" v-if="attachment.status === 1"
          >, Uploading...</span
        >
        <span class="attachment-sublabel" v-else-if="attachment.status === 2"
          >, Success</span
        >
        <span class="attachment-sublabel" v-else> {{ attachment.error }}</span>
      </div>
      <div class="remove pull-right">
        <a @click="deleteAttachment()">Remove</a>
      </div>
    </div>
    <form
      enctype="multipart/form-data"
      novalidate
      v-on:submit.prevent="addAttachment"
    >
      <div v-show="show" class="dropbox2">
        <input
          type="file"
          accept=".xls,.xlsx"
          class="input-file"
          @change="filesChange($event.target.files[0])"
        />
        <p>Drag and drop file(s) here</p>
        <el-button
          @change="filesChange($event.target.files[0])"
          type="primary"
          class="btn-upload"
          >BROWSE</el-button
        >
      </div>
      <!-- <div class="import-button-container">
      <el-button class="modal-btn-cancel">Cancel</el-button>
      <el-button @click="addAttachment" v-model="loading" loader type="primary" class="modal-btn-save">NEXT
        <span slot="loading">Uploading</span>
      </el-button>
      </div> -->

      <div
        v-if="buttonToggle"
        class="col-lg-6 col-md-6 modal-dialog-footer"
        style="padding-top: 20px;"
      >
        <el-button
          @click="deleteAttachment"
          class="modal-btn-cancel"
          v-model="loading"
          loader
          type="primary"
        >
          Cancel</el-button
        >
        <el-button
          @click="addAttachment"
          v-model="loading"
          :loading="saving"
          type="primary"
          class="modal-btn-save"
        >
          Next <span slot="loading">Uploading</span></el-button
        >
      </div>
    </form>
  </div>
</template>
<script>
import { prettyBytes } from '@facilio/utils/filters'

export default {
  props: ['module', 'customupload', 'category', 'importMode'],
  data() {
    return {
      saving: false,
      loading: false,
      show: true,
      status: {
        UPLOADING: 1,
        SUCCESS: 2,
        FAILED: 3,
      },
      attachments: this.list ? this.list : [],
      uploadFile: null,
      formFieldName: 'attachment',
      buttonToggle: false,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  methods: {
    filesChange(selectedFile) {
      if (selectedFile) {
        this.show = false
        this.uploadFile = selectedFile
        let fileEntry = {
          attachmentId: -1,
          fileName: selectedFile.name,
          fileSize: selectedFile.size,
          prettyFileSize: prettyBytes(selectedFile.size),
          contentType: selectedFile.type,
          status: null,
          error: null,
        }
        this.attachments.push(fileEntry)
        this.$emit('fileName', selectedFile.name)
        this.buttonToggle = true
      } else {
        this.show = true
      }
    },

    addAttachment() {
      if (this.customupload) {
        this.$emit('onupload', this.uploadFile)
      } else {
        let self = this
        const formData = new FormData()
        if (self.module === 'asset') {
          formData.append('assetCategory', self.category)
        }
        formData.append('moduleName', self.module)
        formData.append('fileUpload', self.uploadFile)
        formData.append('importMode', self.importMode)
        this.saving = true
        self.$http
          .post('/v2/import/upload', formData)
          .then(function(response) {
            if (response.data) {
              self.saving = false
              if (response.data.responseCode !== 0) {
                self.$message.error(response.data.message)
              } else {
                let importProcessContext =
                  response.data.result.importProcessContext
                self.$emit('uploadResponse', importProcessContext)
              }
            }
          })
          .catch(function(error) {
            self.saving = false
            self.$message.error(error)
          })
      }
    },

    reset() {
      this.loading = false
      this.attachments = []
      this.show = true
    },

    deleteAttachment() {
      this.attachments.splice(0, 1)
      this.show = true
      this.buttonToggle = false
    },
  },
}
</script>
<style>
.fc-attachments .dropbox2 {
  outline: 1px dashed #e1e1ed;
  /* outline-offset: -10px; */
  color: #999999;
  padding: 57px 10px;
  position: relative;
  cursor: pointer;
  text-align: center;
}

.fc-attachments .dropbox2 .input-file {
  width: 100%;
  height: 200px;
  opacity: 0;
  position: absolute;
  cursor: pointer;
  left: 0;
  top: 0;
}
.fc-attachments .dropbox2:hover {
  outline: 1px dashed #e1e1ed;
}
.fc-attachments .dropbox2 p {
  font-size: 13px;
  text-align: center;
  letter-spacing: 0.9px;
  color: #999999;
}
.fc-attachments .fc-attachment-row {
  border-bottom: 1px solid #e2e2e2;
  padding: 5px;
}
.fc-attachments {
  font-size: 13px;
}
.fc-attachments .fc-attachment-row .attachment-sublabel,
.fc-attachments .fc-attachment-row .attachment-progress {
  color: rgb(163, 162, 177);
  font-size: 12px;
}
.fc-attachments .fc-attachment-row div {
  padding: 2px;
}
.fc-attachments .fc-attachment-row .remove {
  margin-top: -30px;
}
.fc-attachments .fc-attachment-row:last-child {
  border: none;
}
</style>
