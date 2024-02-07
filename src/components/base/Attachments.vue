<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['module', 'record', 'parentModule'],
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
    }
  },
  mounted() {
    this.loadAttachments()
  },
  watch: {
    record() {
      this.loadAttachments()
    },
    attachments() {
      let { attachments = [] } = this
      this.record.noOfAttachments = (attachments || []).length
    },
  },
  methods: {
    loadAttachments() {
      this.loading = true

      let url = `/attachment?module=${this.module}&recordId=${this.record.id}`
      if (!isEmpty(this.parentModule)) {
        url += `&parentModuleName=${this.parentModule}`
      }
      return this.$http
        .get(url)
        .then(response => {
          this.loading = false
          this.attachments = response.data.attachments
            ? response.data.attachments
            : []
        })
        .catch(error => {
          if (error) {
            this.loading = false
            this.attachments = null
          }
        })
    },
    filesChange(file) {
      let fileList = Array.from(file)
      if (!fileList.length) return

      // Append the files to FormData
      const formData = new FormData()
      formData.append('module', this.module)
      formData.append('recordId', this.record.id)
      formData.append('parentModuleName', this.parentModule)

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
    canShowDelete(attachment) {
      let { uploadedBy, status } = attachment || {}
      let { SUCCESS } = this.status
      return (
        (isEmpty(status) || status === SUCCESS) &&
        uploadedBy === this.$account.user.id
      )
    },
    deleteAttachment(attachment, index) {
      let ids = []
      let { module, parentModule, record } = this
      ids.push(attachment.id)
      let obj = {
        attachmentId: ids,
        module: module,
        parentModuleName: parentModule,
        recordId: record?.id,
      }
      this.$dialog
        .confirm({
          title: 'Delete Attachment',
          message: 'Are you sure you want to delete this attachment',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/attachments/delete', obj)
              .then(({ data: { message, responseCode, result = {} } }) => {
                if (responseCode === 0) {
                  this.attachments.splice(index, 1)
                  this.loadAttachments()
                } else {
                  throw new Error(message)
                }
              })
          }
        })
    },
  },
}
</script>
