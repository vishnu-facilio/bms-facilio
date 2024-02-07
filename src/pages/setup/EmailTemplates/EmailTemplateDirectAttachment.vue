<template>
  <div class="fc-attachments new-attachments fc-email-attachment">
    <div class="text-center" v-if="loading">
      <spinner :show="loading"></spinner>
    </div>
    <div v-if="!loading && $validation.isEmpty(attachments) && !uploadingfile">
      <form enctype="multipart/form-data" novalidate>
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
    </div>
    <el-row
      v-else-if="uploadingfile && attachments.length === 0"
      :key="'file-loading'"
      class="attchment-row visibility-visible-actions"
    >
      <el-col :span="22">
        <div class="pL10">
          <a
            class="self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px"
            style="border:none;"
            :title="uploadingfile.fileName"
            v-tippy="{
              placement: 'top',
              animation: 'shift-away',
              arrow: true,
            }"
            >{{ uploadingfile.fileName }}</a
          >
          <Progress :percentage="uploadingfile.percentage"></Progress>
        </div>
      </el-col>
      <el-col :span="2" class="mT5 pointer">
        <span>
          <inline-svg
            src="svgs/delete"
            class="pointer mL10 visibility-hide-actions"
            iconClass="icon icon-sm fill-grey"
          ></inline-svg>
        </span>
      </el-col>
    </el-row>
    <div v-if="attachments">
      <div v-if="attachments.length > 0">
        <div class="row fc-summary-attchment wo-fc-summary-attchment pL0">
          <el-row
            v-for="(attachment, index) in attachments"
            :key="attachment.attachmentId"
            class="attchment-row visibility-visible-actions"
          >
            <el-col :span="22">
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
          <el-row
            v-if="uploadingfile"
            :key="'file-loading'"
            class="attchment-row visibility-visible-actions"
          >
            <el-col :span="22">
              <div class="pL10">
                <a
                  class="self-center show text-left fc-black-13 textoverflow-ellipsis max-width300px"
                  style="border:none;"
                  :title="uploadingfile.fileName"
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                  }"
                  >{{ uploadingfile.fileName }}</a
                >
                <Progress :percentage="uploadingfile.percentage"></Progress>
              </div>
            </el-col>
            <el-col :span="2" class="mT5 pointer">
              <span>
                <inline-svg
                  src="svgs/delete"
                  class="pointer mL10 visibility-hide-actions"
                  iconClass="icon icon-sm fill-grey"
                ></inline-svg>
              </span>
            </el-col>
          </el-row>
        </div>
        <div class="mT20" v-if="!diasbleAttachment && attachments.length">
          <form enctype="multipart/form-data" novalidate>
            <div class="dropbox-after-add-dropdown dropbox">
              <input
                type="file"
                multiple
                class="input-file"
                @change="filesChange($event.target.files)"
              />
              <el-button class="fc-green-btn-new">
                <i class="el-icon-plus bold"></i> Add Files
              </el-button>
            </div>
          </form>
        </div>
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
import Attachments from 'pages/setup/EmailTemplates/EmailSummaryAttachments'
import { API } from '@facilio/api'
import Progress from 'src/pages/setup/EmailTemplates/components/Progress.vue'
export default {
  props: [
    'templateId',
    'module',
    'record',
    'diasbleAttachment',
    'fileAttachments',
  ],
  data() {
    return {
      uploadingfile: null,
    }
  },
  components: { Progress },
  extends: Attachments,
  watch: {
    attachments(value) {
      this.$emit('update:fileAttachments', value)
    },
  },
  methods: {
    canShowDelete() {
      return true
    },
    loadAttachments() {
      this.attachments = this.fileAttachments || []
      this.loading = false
    },
    filesChange(fileList) {
      if (!fileList.length) return
      const formData = new FormData()
      formData.append('fileContent', fileList[0])
      let { name, size, type } = fileList[0] || {}
      console.log('====>', fileList[0])
      let fileEntry = {
        fileId: -1,
        fileName: name,
        fileSize: size,
        contentType: type,
        error: null,
        previewUrl: null,
        percentage: 0,
      }
      this.uploadingfile = fileEntry

      API.post(`/v2/files/add`, formData).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.uploadingfile.percentage = 100
          setTimeout(() => {
            this.uploadingfile = null
            let attachmentIndexMap = {}
            let fileEntry = {
              attachmentId: -1,
              fileId: data.fileInfo.fileId,
              fileName: fileList[0].name,
              fileSize: fileList[0].size,
              contentType: fileList[0].type,
              status: this.status.UPLOADING,
              error: null,
              previewUrl: null,
              type: 1,
            }
            this.attachments.push(fileEntry)
            attachmentIndexMap[0 + ''] = this.attachments.length - 1

            this.$emit('addAttachment', data.fileInfo)
          }, 500)
        }
      })
    },
    deleteAttachment(attachment, index) {
      if (attachment.id > 0) {
        let { templateId } = this
        let ids = []
        ids.push(attachment.id)
        let formData = {
          moduleName: 'templatefileattachment',
          templateId,
          attachmentIds: ids,
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
              API.post(`/v2/template/attachment/delete`, formData).then(
                ({ error }) => {
                  if (error) {
                    this.$message.error(error.message || 'Error Occured')
                  }
                }
              )
            }
          })
      }
      this.attachments.splice(index, 1)
      this.$emit('removeFileId', attachment.fileId, index)
    },
  },
}
</script>
<style lang="scss">
.email-attachment {
  display: none;
}
</style>
