<template>
  <div class="mail-attachment-fragment mailer-attachments d-flex">
    <div
      class="fc-mailer-attachment-row d-flex mB10"
      v-for="(attachment, index) in attachmentsDetails"
      @click="openAttachmentsPreview(attachment)"
      :key="`attachment-${index}`"
    >
      <template v-if="!$validation.isEmpty(attachment)">
        <div v-if="!$validation.isEmpty(attachment.className)">
          <div class="icon-document" :class="attachment.className">
            <span class="txt">{{ attachment.displayName }}</span>
          </div>
        </div>
        <div v-else>
          <div class="icon-document gray"><span class="txt">/</span></div>
        </div>
        <div class="attached-files">
          <div
            v-tippy="{
              placement: 'top',
              animation: 'shift-away',
              arrow: true,
              content: attachment.fileFileName,
            }"
            class="mB10 attachment-label"
          >
            {{ attachment.fileFileName }}
          </div>
          <div v-if="attachment.fileSize" class="attached-file-size">
            {{ attachment.fileSize | prettyBytes }}
          </div>
        </div>
        <i
          @click.stop="downloadAttachment(attachment)"
          class="el-icon-download mailer-attachment-icon fc-download-icon download-attachment-icon"
        ></i>
      </template>
    </div>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
    <PreviewFile
      :visibility.sync="filePreviewVisibility"
      v-if="filePreviewVisibility"
      :previewFile="previewFile"
      :files="filePreviewAttachments"
    ></PreviewFile>
  </div>
</template>
<script>
import PreviewFile from '@/PreviewFile'
const ATTACHMENT_DETAILS = [
  { name: 'pdf', displayName: 'PDF', className: 'red' },
  { name: 'docx', displayName: 'DOC', className: 'blue' },
  { name: 'xl', displayName: 'XLS', className: 'green' },
  { name: 'ppt', displayName: 'PPT', className: 'orange' },
  { name: 'img', displayName: 'IMG', className: 'gray' },
]
export default {
  props: ['attachmentsList'],
  components: {
    PreviewFile,
  },
  data: () => ({
    exportDownloadUrl: null,
    filePreviewVisibility: false,
    filePreviewAttachments: [],
    previewFile: null,
  }),
  computed: {
    attachmentsDetails() {
      let { attachmentsList } = this || {}
      return attachmentsList.map(attachment => {
        let extension = this.getExtension(attachment.fileFileName)
        let properties = ATTACHMENT_DETAILS.find(
          details => details.name == extension
        )
        let { displayName, className } = properties || {}
        return { ...attachment, displayName, className }
      })
    },
  },
  methods: {
    openAttachmentsPreview(currentFile) {
      this.previewFile = this.attachmentsFormatter(currentFile)
      this.filePreviewAttachments = this.attachmentsList.map(record =>
        this.attachmentsFormatter(record)
      )
      this.filePreviewVisibility = true
    },
    attachmentsFormatter(record) {
      return {
        contentType: record?.fileContentType,
        downloadUrl: record?.fileDownloadUrl,
        fileName: record?.fileFileName,
        previewUrl: record?.fileUrl,
      }
    },

    downloadAttachment(file) {
      let { fileDownloadUrl: url } = file || {}

      if (this.exportDownloadUrl) {
        this.exportDownloadUrl = null
      }

      this.$nextTick(() => {
        this.exportDownloadUrl = url
      })
    },
    getExtension(fileName) {
      let name = fileName.split('.')
      let format = name[1]
      if (format == 'pdf') {
        return 'pdf'
      }
      if (['jpg', 'jpeg', 'png', 'svg'].includes(format)) {
        return 'img'
      }
      if (['docx', 'doc', 'docm', 'dot', 'dotx'].includes(format)) {
        return 'docx'
      }
      if (
        ['xls', 'xlsx', 'xl', 'xll', 'xlm', 'xlsm', 'xlsx'].includes(format)
      ) {
        return 'xl'
      }
      if (['ppt', 'pptx'].includes(format)) {
        return 'ppt'
      } else {
        return 'none'
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.mailer-attachments {
  &.mailer-border {
    border: 1px solid #e4ebf1;
    border-width: 1px 1px 0px 1px;
    padding-left: 10px;
  }
  margin-left: -20px;
  padding-left: 10px;
  padding-top: 5px;
  display: flex;
  flex-wrap: wrap;
  color: #686b70;
  .fc-mailer-attachment-row {
    margin-right: 10px;
    border-radius: 14px;
    border: solid 1px #ebeff3;
    background-color: #fff;
    .attached-files {
      overflow: hidden;
    }
    .attachment-label {
      padding-top: 7px;
      width: 124px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      letter-spacing: 0.5px;
    }
    .mailer-attachment-icon {
      opacity: 0;
    }
    &:hover {
      box-shadow: 0 0 8px 0 rgba(0, 0, 0, 0.11);
      background-color: #ffffff;
      transition: 0.2s all;
      -webkit-transition: 0.2s all;
      -moz-transition: 0.2s all;
      cursor: pointer;
      .mailer-attachment-icon {
        opacity: 1;
        margin-left: auto;
      }
    }
  }
  .pdf-color {
    margin-top: 6px;
    padding-top: 3px;
    color: rgb(98, 96, 96);
    margin-left: 10px;
    font-weight: 600;
    height: 16px;
    width: 16px;
    margin-right: 7px;
  }
}
.download-attachment-icon {
  padding-right: 10px;
  padding-top: 10px;
  color: #686b70;
}
.icon-document {
  height: 16px;
  width: 16px;
  border-radius: 3px;
  border-start-end-radius: 8px;
  padding-bottom: 18px;
  margin: 8px 5px 0px 10px;
}
.red {
  background-color: orangered;
}
.blue {
  background-color: rgb(0, 110, 255);
}
.green {
  background-color: rgb(3, 105, 3);
}
.orange {
  background-color: rgb(213, 139, 0);
}
.gray {
  background-color: #6c6d6e;
}

.txt {
  color: #fff !important;
  font-size: 6px;
  font-weight: bold;
  text-align: center;
  align-items: center;
  top: 50%;
  left: 50%;
  padding-left: 2px;
  padding-bottom: 10px;
}
</style>
