<script>
import Attachments from '@/relatedlist/SummaryAttachment2'
import { API } from '@facilio/api'

export default {
  props: [
    'templateId',
    'module',
    'record',
    'diasbleAttachment',
    'attachmentsList',
  ],
  extends: Attachments,
  watch: {
    attachments(value) {
      this.$emit('update:attachmentsList', value)
    },
  },
  methods: {
    canShowDelete() {
      return true
    },
    loadAttachments() {
      this.attachments = this.attachmentsList
      this.loading = false
    },
    filesChange(fileList) {
      if (!fileList.length) return

      const formData = new FormData()
      formData.append('fileContent', fileList[0])

      API.post(`/v2/files/add`, formData).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
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
          }
          this.attachments.push(fileEntry)
          attachmentIndexMap[0 + ''] = this.attachments.length - 1

          this.$emit('addAttachment', data.fileInfo)
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
