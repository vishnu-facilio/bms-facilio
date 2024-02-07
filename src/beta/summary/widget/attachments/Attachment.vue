<template>
  <FContainer padding="containerXLarge">
    <FContainer
      v-if="loading"
      height="100%"
      display="flex"
      alignItems="center"
      justifyContent="center"
    >
      <FSpinner :size="20" />
    </FContainer>
    <FAttachment
      v-else
      v-model="attachments"
      :uploadFile="uploadFiles"
      :removeFile="removeFile"
      :multiple="true"
      height="250px"
    />
  </FContainer>
</template>

<script>
import { API } from '@facilio/api'
import { FContainer, FAttachment, FSpinner } from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import BaseAttachment from 'src/components/base/Attachments.vue'
import cloneDeep from 'lodash/cloneDeep'

export default {
  extends: BaseAttachment,
  name: 'Attachment',
  components: { FContainer, FAttachment, FSpinner },
  data: () => ({
    attachments: [],
    loading: false,
    formFieldName: 'attachment',
    defaultFiles: [],
  }),
  mounted() {
    this.loadAttachments()
  },
  methods: {
    async loadAttachments() {
      this.loading = true
      let { module, record, parentModule: parentModuleName } = this || {}
      let { id: recordId } = record || {}

      let url = `/attachment`

      let param = {
        module,
        recordId,
      }
      if (!isEmpty(parentModuleName)) {
        param = { ...param, parentModuleName }
      }

      let { data, error } = await API.get(url, param)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error occured')
      } else {
        let { attachments } = data || {}
        let modifiedAttachments = attachments.map(attachment => {
          let { fileName: name, fileSize: size, fileId: uid } = attachment || {}
          return {
            ...attachment,
            name,
            size,
            uid,
          }
        })
        this.attachments = modifiedAttachments
        this.defaultFiles = modifiedAttachments
      }
      this.loading = false
    },
    async uploadFiles(val) {
      let { defaultFiles } = this || {}
      let fileList = cloneDeep(val)
      let oldValue = defaultFiles
      let oldIds = oldValue.map(file => file.uid)
      // save only new attachments apart from attachments available on load
      fileList = fileList.filter(currVal => !oldIds.includes(currVal.uid))
      if (!isEmpty(fileList)) {
        const formData = new FormData()
        formData.append('module', this.module)
        formData.append('recordId', this.record.id)
        formData.append('parentModuleName', this.parentModule)

        fileList.forEach(file => {
          let { name, originFileObj } = file || {}

          formData.append(this.formFieldName, originFileObj, name)
        })

        let { error } = await API.post('/attachment/add', formData)
        if (!isEmpty(error)) {
          this.$message.error(error.message || 'Error occured')
        }
      }

      return val
    },
    async removeFile(val) {
      let { module, record, parentModule: parentModuleName } = this || {}
      let obj = {
        attachmentId: [val.id],
        module: module,
        parentModuleName,
        recordId: record?.id,
      }

      let promptValue = await this.$dialog.confirm({
        title: 'Delete Attachment',
        message: 'Are you sure you want to delete this attachment',
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (promptValue) {
        let { error } = await API.post('/v2/attachments/delete', obj)
        if (!isEmpty(error)) {
          this.$message.error(error.message || 'Error occured')
        } else {
          return val
        }
      }
    },
  },
}
</script>
