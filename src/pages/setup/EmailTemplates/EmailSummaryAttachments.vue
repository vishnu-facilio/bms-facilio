<template>
  <div class="fc-attachments new-attachments fc-email-attachment">
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-if="!loading && $validation.isEmpty(attachments)">
      <form enctype="multipart/form-data" novalidate>
        <div class="dropbox">
          <input
            type="file"
            multiple
            class="input-file"
            @change="filesChange($event.target.files)"
          />
          <p>
            {{ $t('common.attachment_form.drag_and_drop_files') }}
            {{ $t('common.attachment_form.click_to_browse') }}
          </p>
          <p class="pB0 pT16">
            (or)
          </p>
          <el-button class="fc-green-btn-new">
            <i class="el-icon-plus"></i> Attach files
          </el-button>
        </div>
      </form>
      <!-- <el-progress
        :percentage="progressBarData"
        :show-text="false"
        class="image-progressBar"
      ></el-progress> -->
    </div>
    <div v-else>
      <div v-if="attachments.length > 0">
        <div class="row fc-summary-attchment wo-fc-summary-attchment pL0">
          <el-row
            v-for="(attachment, index) in attachments"
            :key="attachment.attachmentId"
            class="attchment-row visibility-visible-actions"
          >
            <el-col :span="22">
              <el-col :span="18">
                <div class="pL10">
                  <a
                    class="self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px"
                    style="border:none;"
                    :title="attachment.fileName"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                    >{{ attachment.fileName }}</a
                  >
                </div>
              </el-col>
            </el-col>
            <el-col :span="2" class="mT5 pointer">
              <span
                v-if="canShowDelete(attachment)"
                @click="deleteAttachment(attachment, index)"
              >
                <inline-svg
                  src="svgs/delete"
                  class="pointer mL10 visibility-hide-actions"
                  iconClass="icon icon-sm fill-grey"
                ></inline-svg>
              </span>
            </el-col>
          </el-row>
          <!-- <el-progress
            :percentage="progressBarData"
            class="image-progressBar"
          ></el-progress> -->
        </div>
        <div class="mT20" v-if="!diasbleAttachment">
          <form enctype="multipart/form-data" novalidate>
            <div class="dropbox-after-add-dropdown dropbox">
              <input
                type="file"
                multiple
                class="input-file"
                @change="filesChange($event.target.files)"
              />
              <el-button class="fc-green-btn-new">
                <i class="el-icon-plus bold"></i> Attach files
              </el-button>
            </div>
          </form>
          <!-- <el-progress
            :percentage="progressBarData"
            class="image-progressBar"
            :show-text="false"
          ></el-progress> -->
        </div>
      </div>
      <div v-else>
        <form
          v-if="!diasbleAttachment"
          enctype="multipart/form-data"
          novalidate
        >
          <div class="dropbox">
            <input
              type="file"
              multiple
              class="input-file"
              @change="filesChange($event.target.files)"
            />
            <p class="fc-black2-18">
              {{ $t('common.attachment_form.drag_and_drop_files') }}<br />
              {{ $t('common.attachment_form.click_to_browse') }}
            </p>
            <p class="pB0">
              (or)
            </p>
            <el-button class="fc-green-btn-new">
              <i class="el-icon-plus"></i> Attach files
            </el-button>
          </div>
        </form>
        <!-- <el-progress
          :percentage="progressBarData"
          class="image-progressBar"
          :show-text="false"
        ></el-progress> -->
      </div>
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
  props: ['module', 'record', 'diasbleAttachment', 'progressBarData'],
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
      selectedAttachedFile: null,
      visibility: false,
      fileListUpload: [],
    }
  },
  components: {
    PreviewFile,
  },
  computed: {
    customColors() {
      return '#835af8'
    },
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
    handleRemove(file, fileList) {
      console.log(file, fileList)
    },
    handlePreview(file) {
      console.log(file)
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`Cancel the transfert of ${file.name} ?`)
    },
    canShowDelete(attachment) {
      let { uploadedBy, status } = attachment || {}
      let { SUCCESS } = this.status
      return (
        (isEmpty(status) || status === SUCCESS) &&
        uploadedBy === this.$account.user.id
      )
    },
    deleteAttachment(attachment, index) {
      let { module } = this
      let ids = []
      ids.push(attachment.id)
      let obj = {
        attachmentId: ids,
        module: module,
      }
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_attachment'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_attachment'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/attachments/delete', obj)
              .then(({ data: { message, responseCode, result = {} } }) => {
                if (responseCode === 0) {
                  this.attachments.splice(index, 1)
                } else {
                  throw new Error(message)
                }
              })
          }
        })
    },
    selectedFile(inList) {
      this.selectedAttachedFile = inList
      this.visibility = true
    },
    loadAttachments() {
      this.loading = true
      return this.$http
        .get(
          '/attachment?module=' + this.module + '&recordId=' + this.record.id
        )
        .then(function(response) {
          if (response.status === 200) {
            this.attachments = response.data.attachments
              ? response.data.attachments
              : []
            this.loading = false
            return 0
          } else {
            this.loading = true
          }
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
        .post(
          '/attachment/add',
          formData
          // {
          //   onUploadProgress: progressEvent => {
          //     this.progressBarData = Math.round(
          //       (progressEvent.loaded / progressEvent.total) * 100
          //     )
          //   },
          //   }
        )
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
<style lang="scss">
.fc-email-attachment {
  font-size: 13px;
  .dropbox {
    display: flex;
    height: 200px;
    outline-offset: -10px;
    color: #696969;
    background: #f9f9ff;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    flex-direction: column;
    outline: none !important;
    p {
      font-size: 14px;
      line-height: 16px;
      color: #324056;
    }
  }
  .dropbox .input-file {
    opacity: 0;
    width: 100%;
    height: 200px;
    position: absolute;
    cursor: pointer;
  }
  .dropbox:hover {
    outline: none;
  }
  .fc-attachment-row {
    border-bottom: 1px solid #f7f7f7;
    padding: 5px;
  }
  .fc-attachment-row .attachment-sublabel,
  .fc-attachment-row .attachment-progress {
    color: #a3a2b1;
    font-size: 12px;
  }
  .fc-attachment-row div {
    padding: 2px;
  }
  .fc-attachment-row .remove {
    margin-top: -30px;
  }
  .fc-attachment-row:last-child {
    border: none;
  }
  .attachment-label a {
    color: #25243e;
  }
  .remove-attachment-link:hover {
    color: #333;
  }
  .comment-file {
    text-align: left;
    font-size: 12px;
    font-weight: normal;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: 0.3px;
    color: #a3a2b1;
  }
  .fc-summary-attchment {
    padding-left: 0px;
    padding-right: 0px;
  }
  .attchment-row {
    width: 100%;
    background: #f4f4f4;
    padding-bottom: 0;
    margin-bottom: 0;
    border-radius: 4px;
    padding: 10px;
    display: flex;
    align-items: center;
    a {
      color: #005ae8;
      font-size: 14px;
      font-weight: 500;
      letter-spacing: 0.5px;
    }
  }
  .new-attachments {
    padding: 0 0 20px 0;
  }
  .wo-fc-summary-attchment {
    margin-top: 10px;
    padding-bottom: 0 !important;
    background: #f4f4f4;
    border-radius: 8px;
    border-bottom: solid 1px #f7f8f9;
  }
  .fc-template-grey12 {
    font-size: 12px;
    font-weight: normal;
    line-height: normal;
    opacity: 0.5;
    letter-spacing: 0.43px;
    color: #2f4058;
  }
  .dropbox-after-add-dropdown {
    background: none;
    display: flex;
    height: 50px;
    outline-offset: -10px;
    color: #696969;
    align-items: flex-start;
    justify-content: center;
    cursor: pointer;
    flex-direction: column;
    padding: 0;
    outline: none !important;
    .input-file {
      width: 107px;
      height: 50px;
    }
  }
}
.image-progressBar {
  margin-top: 10px;
  .el-progress-bar__inner {
    background-color: #3ab2c1;
  }
  .el-progress-bar__outer {
    border-radius: 0;
  }
}
</style>
