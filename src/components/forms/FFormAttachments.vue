<template>
  <el-form-item
    :prop="field.lookupModuleName"
    :required="field.required"
    class="section-items attachments-container"
  >
    <div slot="label" :class="[labelPosition === 'top' && 'attachment-label']">
      {{ field.displayName }}

      <div
        v-if="attachments.length > 0 && labelPosition === 'top'"
        class="pT5 f12 fc-link3 cursor-pointer self-center"
        onclick="document.getElementById('file-attachment').click()"
      >
        <inline-svg src="clip" iconClass="icon icon-xs"></inline-svg>
        {{ $t('common._common.attach_files') }}
      </div>
    </div>
    <div class="fc-attachments mT10">
      <template v-if="attachments.length > 0">
        <div
          class="fc-attachment-row d-flex"
          v-for="(attachment, index) in attachments"
          :key="`attachment-${index}`"
        >
          <inline-svg
            src="svgs/doc-outline"
            class="mT5 mR15 d-flex"
            iconClass="icon icon-xxl"
          ></inline-svg>
          <div class="attached-files">
            <div class="mB10 attachment-label">
              {{ attachment.name }}
            </div>
            <div v-if="!preview" class="attached-file-size">
              {{ attachment.bytes }}
            </div>
            <div v-if="attachment.status === 1">
              , {{ $t('common._common.uploading') }}
            </div>
            <div v-else-if="attachment.status === 2">
              , {{ $t('common._common.success') }}
            </div>
            <div v-else>{{ attachment.error }}</div>
          </div>
          <i
            class="el-icon-delete pointer attachment-delete"
            @click="deleteAttachment(field.name, index)"
          ></i>
          <div></div>
        </div>
      </template>
      <div
        class="pointer"
        v-show="attachments.length === 0"
        @change="addAttachment(field, ...arguments)"
      >
        <spinner v-if="isLoading" :show="isLoading" size="40"></spinner>
        <form
          v-else
          enctype="multipart/form-data"
          :data-test-selector="`${field.name}`"
        >
          <div class="dropbox text-center">
            <img src="~assets/upload-icon.svg" class="mT10 upload-icon" />
            <input
              class="input-file"
              type="file"
              id="file-attachment"
              :accept="acceptedFileTypes"
              multiple
            />
            <p>
              {{ $t('common.attachment_form.drag_and_drop_file') }}
              <br />
              {{ $t('common.attachment_form.click_to_browse') }}
            </p>
          </div>
        </form>
      </div>
    </div>

    <div
      v-if="attachments.length > 0 && labelPosition !== 'top'"
      class="pT10 pB10 f12 fc-link3 cursor-pointer"
      onclick="document.getElementById('file-attachment').click()"
    >
      <inline-svg src="clip" iconClass="icon icon-xs"></inline-svg>
      {{ $t('common._common.attach_files') }}
    </div>
  </el-form-item>
</template>
<script>
import { API } from '@facilio/api'
import { prettyBytes } from '@facilio/utils/filters'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers'
const { getOrgMoment: moment } = helpers
import Spinner from '@/Spinner'

export default {
  props: [
    'field',
    'formModel',
    'attachments',
    'labelPosition',
    'isV3Api',
    'clearError',
    'disableSaveBtn',
    'preview',
  ],
  components: { Spinner },
  data() {
    return {
      isLoading: false,
    }
  },
  created() {
    let { field } = this
    let { value } = field || {}
    if (!isEmpty(value)) {
      this.setAttachments(field)
    }
  },
  computed: {
    acceptedFileTypes() {
      let { field, $getProperty } = this

      // fileTypes inside field should be an array. refer
      // https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/file#Unique_file_type_specifiers
      let acceptedFileTypes = $getProperty(field, 'config.fileTypes', '')

      return !isEmpty(acceptedFileTypes) ? acceptedFileTypes : ''
    },
  },
  methods: {
    async addAttachment({ lookupModuleName }, event) {
      let {
        target: { files = [] },
      } = event
      let { attachments: availableAttachments } = this
      let attachments = availableAttachments
      for (let index = 0; index < files.length; index++) {
        let { name, size, type } = files[index]
        let bytes = prettyBytes(size)
        attachments.push({
          name,
          size,
          bytes,
          type,
          status: null,
          error: null,
        })
      }

      this.disableSaveBtn(true)
      await this.uploadFiles(files)
      this.disableSaveBtn(false)
      this.$emit('updateAttachment', attachments)
      this.clearError(lookupModuleName)
    },
    async uploadFiles(files) {
      this.isLoading = true
      let { formModel } = this
      let { error, ids } = await API.uploadFiles(files)

      if (error) {
        let { message } = error
        this.$message.error(message || 'Error occured')
      } else if (ids) {
        let { lookupModuleName } = this.field

        let uploadedFiles = []
        if (!isEmpty(ids))
          ids.forEach(id => {
            uploadedFiles.push({
              fileId: id,
              createdTime: Date.now(),
            })
          })

        let fileIds = this.formModel[lookupModuleName] || []

        if (!isEmpty(fileIds)) {
          fileIds = fileIds.concat(uploadedFiles)
          formModel[lookupModuleName] = fileIds
        } else {
          formModel[lookupModuleName] = uploadedFiles
        }
        this.$emit('updateFormModel', formModel, this.field)
        this.isLoading = false
      }
    },
    setAttachments(field) {
      let { formModel } = this
      let { value, lookupModuleName } = field || {}
      if (!isEmpty(value)) {
        if (typeof value === 'string') {
          value = JSON.parse(value)
        }
        let attachmentsInfo = value.map(file => {
          let { fileId, createdTime } = file || {}
          //need to remove after server chnage
          if (isEmpty(createdTime)) {
            createdTime = moment().valueOf()
          }
          return { fileId, createdTime }
        })
        let attachments = value.map(file => {
          let { fileName: name } = file || {}
          return { name }
        })
        formModel[lookupModuleName] = attachmentsInfo

        this.$emit('updateAttachment', attachments)
        this.$emit('updateFormModel', formModel, this.field)
      }
    },
    deleteAttachment(name, index) {
      let { attachments, formModel } = this
      attachments.splice(index, 1)

      let { lookupModuleName } = this.field
      let [removedAttachment] = formModel[lookupModuleName].splice(index, 1)

      if (this.isV3Api) {
        // Temp handling for removing values from submodules
        if (this.$getProperty(removedAttachment, 'id')) {
          let deletedSubModuleKey = `${lookupModuleName}_delete`
          if (isEmpty(formModel[deletedSubModuleKey])) {
            formModel[deletedSubModuleKey] = []
          }
          formModel[deletedSubModuleKey].push(removedAttachment.id)
        }
        this.$emit('updateFormModel', formModel, this.field)
      }

      if (isEmpty(this.attachments)) {
        ;(document.getElementById('file-attachment') || {}).value = ''
      }
    },
  },
}
</script>
