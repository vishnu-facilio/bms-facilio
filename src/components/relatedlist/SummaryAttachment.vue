<template>
  <div class="fc-attachments p0">
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-else>
      <div
        class="row fc-summary-attchment"
        style="padding-top: 20px;margin-left: 15px;margin-right: 15px; margin-bottom:20px;padding-left:0px;"
      >
        <el-row
          v-for="attachment in attachments"
          :key="attachment.attachmentId"
          style="border-bottom: 1px solid #ededed;width: 100%;padding-top: 10px;padding-bottom: 10px;"
        >
          <el-col :span="22">
            <el-col :span="2" class="mT5">
              <img
                class="svg-icon"
                src="~assets/picture.svg"
                style="width:20px;"
              />
            </el-col>
            <el-col :span="18">
              <div>
                <a
                  class="self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px"
                  style="border:none;"
                  :title="attachment.fileName"
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                  }"
                  v-if="attachment.previewUrl"
                  @click="selectedFile(attachment)"
                  >{{ attachment.fileName }}</a
                >
                <a
                  class="self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px"
                  style="border:none;"
                  :title="attachment.fileName"
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                  }"
                  v-else
                  >{{ attachment.fileName }}</a
                >
                <div
                  class="col-4 comment-file mT2"
                  style="border:none;"
                  v-if="getUser(attachment) > 0"
                >
                  {{ getUser(attachment) }}
                  <span v-if="attachment.createdTime"
                    >|
                    {{
                      attachment.createdTime | formatDate('DD-MM_YYYY')
                    }}</span
                  >
                </div>
                <div class="col-4 comment-file mT2" style="border:none;" v-else>
                  {{ attachment.createdTime | formatDate('DD-MM_YYYY') }}
                </div>
              </div>
            </el-col>
          </el-col>
          <el-col :span="2" class="mT5 pointer">
            <img
              src="~assets/download.svg"
              width="16"
              height="16"
              @click="downloadAttachment(attachment.downloadUrl)"
            />
          </el-col>
        </el-row>
      </div>
      <form
        v-if="!diasbleAttachment"
        enctype="multipart/form-data"
        novalidate
        style="margin-left: 15px;margin-right: 15px;"
      >
        <div class="dropbox">
          <input
            type="file"
            multiple
            class="input-file"
            @change="filesChange($event.target.files)"
          />
          <img
            src="~assets/upload-icon.svg"
            class="upload-img mT10 opacity-05"
            height="20"
            width="20"
          />
          <p>
            {{ $t('common.attachment_form.drag_and_drop_files') }}<br />
            {{ $t('common.attachment_form.click_to_browse') }}
          </p>
        </div>
      </form>
    </div>
    <preview-file
      :visibility.sync="visibility"
      v-if="visibility && selectedAttachedFile"
      :previewFile="selectedAttachedFile"
      :files="attachments"
    ></preview-file>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['module', 'record', 'diasbleAttachment'],
  data() {
    return {
      loading: true,
      status: {
        UPLOADING: 1,
        SUCCESS: 2,
        FAILED: 3,
      },
      attachments: [],
      formFieldName: 'attachment',
      exportDownloadUrl: null,
      selectedAttachedFile: null,
      visibility: false,
    }
  },
  components: {
    PreviewFile,
  },
  mounted() {
    this.loadAttachments()
  },
  watch: {
    record: function(recordObj) {
      this.loadAttachments()
    },
  },
  methods: {
    selectedFile(inList) {
      this.selectedAttachedFile = inList
      this.visibility = true
    },
    loadAttachments() {
      let self = this
      self.loading = true
      return this.$http
        .get(
          '/attachment?module=' + this.module + '&recordId=' + this.record.id
        )
        .then(function(response) {
          self.loading = false
          self.attachments = response.data.attachments
            ? response.data.attachments
            : []
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.attachments = null
          }
        })
    },
    filesChange(files) {
      let fileList = Array.from(files)
      if (!fileList.length) return
      // append the files to FormData
      const formData = new FormData()
      formData.append('module', this.module)
      formData.append('recordId', this.record.id)

      let { UPLOADING, SUCCESS, FAILED } = this.status

      fileList.forEach(file => {
        let { name, size, type } = file || {}
        let fileEntry = {
          fileId: -1,
          fileName: name,
          fileSize: size,
          contentType: type,
          status: UPLOADING,
          error: null,
          previewUrl: null,
          uploadedBy: this.$account.user.id,
        }

        formData.append(this.formFieldName, file, name)
        this.attachments.unshift(fileEntry)
      })

      this.$http
        .post('/attachment/add', formData)
        .then(({ data }) => {
          data.attachments.forEach(attachment => {
            let { fileId, fileName, fileSize } = attachment
            let attachmentIdx = this.attachments.findIndex(
              a => a.fileName === fileName && a.fileSize === fileSize
            )

            if (!isEmpty(attachmentIdx)) {
              let attachmentFile = this.attachments[attachmentIdx]

              if (fileId) {
                attachmentFile = {
                  ...attachmentFile,
                  ...attachment,
                  status: SUCCESS,
                }
              } else {
                attachmentFile = {
                  ...attachmentFile,
                  status: FAILED,
                  error: 'Upload failed.',
                }
              }

              this.$set(this.attachments, attachmentIdx, attachmentFile)
            }
          })
        })
        .catch(() => {
          fileList.forEach(file => {
            let { name, size } = file || {}
            let attachmentIdx = this.attachments.findIndex(
              a => a.fileName === name && a.fileSize === size
            )

            if (!isEmpty(attachmentIdx)) {
              let attachmentFile = this.attachments[attachmentIdx]

              attachmentFile = {
                ...attachmentFile,
                status: FAILED,
                error: 'Upload failed.',
              }
              this.$set(this.attachments, attachmentIdx, attachmentFile)
            }
          })
        })
    },
    getUser(assign) {
      if (assign !== null && assign.id) {
        let assignto = this.$store.getters.getUser(assign.id)
        if (assignto) {
          return assignto.name
        } else {
          return '---'
        }
      } else {
        return '---'
      }
    },
    downloadAttachment(url) {
      this.exportDownloadUrl = url
    },
    icon(file) {
      if (file) {
        if (file === 'image/png') {
          return 'statics/icons/png.svg'
        }
        return 'statics/icons/pdf.svg'
      }
      return 'statics/icons/pdf.svg'
    },
  },
}
</script>
<style>
.fc-attachments .dropbox {
  outline: 1px dashed #d3d3d3;
  outline-offset: -10px;
  color: #696969;
  padding: 10px 10px;
  position: relative;
  cursor: pointer;
}
.fc-attachments .dropbox .input-file {
  opacity: 0;
  width: 100%;
  height: 84px;
  position: absolute;
  cursor: pointer;
}
.fc-attachments .dropbox:hover {
  outline: 1px dashed #808080;
}
.fc-attachments .dropbox p {
  font-size: 0.8em;
  text-align: center;
}
.fc-attachments .fc-attachment-row {
  border-bottom: 1px solid #f7f7f7;
  padding: 5px;
}
.fc-attachments {
  font-size: 13px;
  padding: 10px;
}
.fc-attachments .fc-attachment-row .attachment-sublabel,
.fc-attachments .fc-attachment-row .attachment-progress {
  color: #a3a2b1;
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
.attachment-label a {
  color: #25243e;
}
.remove-attachment-link:hover {
  color: #333;
}
.comment-file {
  font-size: 13px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #919191;
}
.fc-summary-attchment {
  padding-left: 0px;
  padding-right: 0px;
}
</style>
