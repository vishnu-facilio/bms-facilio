<template>
  <el-dialog
    :title="$t('common._common.default_value')"
    :visible.sync="showDefaultValueDialog"
    width="50%"
    class="fc-dialog-center-container taskdialog"
    :before-close="closeDialogBox"
    :append-to-body="true"
    :persistent="true"
  >
    <div class="default-value-dialog">
      <div v-if="isLoading" class="mT20">
        <spinner :show="isLoading" size="40"></spinner>
      </div>
      <div v-else>
        <template v-if="isFileField">
          <div class="mT30 width90">
            <div class="attachment-label mB20">
              {{ $t('common._common.file_default_value') }}
            </div>

            <FFileUpload
              :field.sync="fieldObj"
              :isFileType="isFileField"
              :isDisabled="false"
              :imgContentUrl.sync="fieldObj.imgUrl"
              :fileObj.sync="fieldObj.fileObj"
              :showWebCamPhoto="true"
              :isV3Api="true"
              :preview="true"
              :isSaveBtnDisabled="isSaveBtnDisabled"
              :addImgFile="
                (fileOrId, fieldObj) => addImgFile({ fileOrId, fieldObj })
              "
              @removeImgFile="fieldObj => removeImgFile({ fieldObj })"
            ></FFileUpload>
          </div>
        </template>
        <template class="mT30" v-if="isAttachmentField">
          <div class="attachment-label mB20">
            {{ $t('common._common.attachment_default_value') }}
          </div>
          <el-form
            :model="mockFormModel"
            label-position="top"
            class="width90 mL30"
          >
            <FormAttachments
              :formModel="mockFormModel"
              :attachments="attachments"
              :field="fieldObj"
              labelPosition="top"
              :isV3Api="true"
              :clearError="() => {}"
              :preview="true"
              @updateAttachment="updateAttachment"
              @updateFormModel="updateFormModel"
              :disableSaveBtn="value => (isSaveBtnDisabled = value)"
            />
          </el-form>
        </template>
        <template v-if="showRichTextArea" class="mT30">
          <div class="attachment-label mB20">
            {{ $t('common._common.richtext_default_value') }}
          </div>
          <RichTextArea v-model="richTextValue" :field="fieldObj" />
        </template>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialogBox" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="saveDefaultValue"
          :loading="isSaving"
          >{{ $t('alarm.alarm.save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import FFileUpload from '@/FFileUpload'
import FormAttachments from '@/forms/FFormAttachments'
import { getBaseURL } from 'util/baseUrl'
import { API } from '@facilio/api'
import RichTextArea from '@/forms/RichTextArea'

export default {
  name: 'DefaultValueSelector',
  props: ['showDefaultValueDialog', 'selectedField'],
  components: { FFileUpload, FormAttachments, RichTextArea, Spinner },
  data() {
    return {
      fieldObj: {},
      isSaveBtnDisabled: false,
      attachments: [],
      mockFormModel: {},
      richTextValue: '',
      fileDefaults: [],
      isLoading: false,
      isSaving: false,
      allowRichContentSave: true,
    }
  },
  created() {
    this.initDefaultValueSetup()
  },
  computed: {
    defaultValueEmpty() {
      let { selectedField } = this
      let { value } = selectedField || {}
      return isEmpty(value)
    },
    showRichTextArea() {
      let { selectedField } = this
      let { configJSON, displayTypeEnum } = selectedField || {}
      let { richText } = configJSON || {}

      return (
        (richText && displayTypeEnum === 'TEXTAREA') ||
        displayTypeEnum === 'RICH_TEXT'
      )
    },
    isFileField() {
      let { fieldObj } = this
      let { displayTypeEnum } = fieldObj || {}

      return displayTypeEnum === 'FILE'
    },
    isAttachmentField() {
      let { fieldObj } = this
      let { displayTypeEnum } = fieldObj || {}

      return displayTypeEnum === 'ATTACHMENT'
    },
  },
  methods: {
    async initDefaultValueSetup() {
      this.isLoading = true
      let { selectedField, showRichTextArea } = this
      if (!isEmpty(selectedField)) {
        let { displayTypeEnum, lookupModuleName } = selectedField || {}

        if (displayTypeEnum === 'ATTACHMENT' && !isEmpty(lookupModuleName)) {
          let mockFormModelObj = {}
          mockFormModelObj[`${lookupModuleName}`] = []
          this.mockFormModel = mockFormModelObj
        } else if (showRichTextArea) {
          let { value } = selectedField || {}
          if (!isEmpty(value)) {
            if (typeof value === 'string') {
              value = JSON.parse(value)
            }
            let { fileId } = value || {}
            let richTextContent = ''
            if (!isEmpty(fileId)) {
              let richTextUrl = `${getBaseURL()}/v2/files/preview/${fileId}`
              let { error, data } = await API.get(richTextUrl)

              if (error) this.$message.error('Error Occured')
              else richTextContent = data
            }
            this.richTextValue = richTextContent
          }
        }
        this.fieldObj = selectedField
      }
      this.isLoading = false
    },
    updateAttachment(attachments) {
      this.$set(this, 'attachments', attachments)
    },
    updateFormModel(formModel) {
      this.$set(this, 'mockFormModel', formModel)
    },
    constructAttachment() {
      let { attachments, mockFormModel, fieldObj } = this
      if (!isEmpty(mockFormModel)) {
        let { lookupModuleName } = fieldObj || {}
        let defaultAttachments = []

        if (!isEmpty(lookupModuleName)) {
          let { [lookupModuleName]: attachmentIdArr = [] } = mockFormModel || {}
          if (!isEmpty(attachmentIdArr)) {
            attachmentIdArr.forEach((attachment, index) => {
              let currentAttachmentInfo = attachments[index]
              let { name: fileName } = currentAttachmentInfo || {}
              let { fileId, createdTime } = attachment || {}

              defaultAttachments.push({ fileName, fileId, createdTime })
            })
          }
          if (isEmpty(defaultAttachments)) defaultAttachments = null
          this.setDefaultValue(defaultAttachments)
        }
      }
    },
    addImgFile(props) {
      let { fileOrId: data } = props || {}
      let { fieldObj } = this
      let { fileObj } = fieldObj || {}
      let defaultValueArr = []
      let defaultValue = {}

      if (!isEmpty(fileObj)) {
        let { name } = fileObj || {}
        defaultValue = { fileName: name, fileId: data }
        defaultValueArr.push(defaultValue)
      }
      this.fileDefaults = defaultValueArr
    },
    removeImgFile(props) {
      let { fieldObj } = props || {}
      if (!isEmpty(fieldObj)) {
        this.fileDefaults = null
      }
    },
    async uploadRichText() {
      let { richTextValue } = this
      let fileId = null
      if (!isEmpty(richTextValue)) {
        if (richTextValue.length > 32000) {
          this.allowRichContentSave = false
        } else {
          const file = new File([richTextValue], 'richText.txt', {
            type: 'text/plain',
          })
          let { ids, error } = await API.uploadFiles([file])
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            fileId = isEmpty(ids) ? null : ids[0]
          }
          let value = { fileId }

          this.setDefaultValue(value)
        }
      }
    },
    saveFileDefaults() {
      let { fileDefaults } = this
      if (isEmpty(fileDefaults)) {
        this.fieldObj.fileObj = null
      }
      this.setDefaultValue(fileDefaults)
    },
    setDefaultValue(value) {
      this.$set(this.fieldObj, 'value', value)
    },
    async saveDefaultValue() {
      this.isSaving = true
      let { fieldObj, showRichTextArea } = this
      let { displayTypeEnum } = fieldObj || {}

      if (showRichTextArea) await this.uploadRichText()
      else if (displayTypeEnum === 'ATTACHMENT') this.constructAttachment()
      else if (displayTypeEnum === 'FILE') this.saveFileDefaults()
      let { allowRichContentSave } = this
      if (allowRichContentSave) {
        this.$emit('saveDefaultValue', this.fieldObj)
      } else {
        this.$message.error(this.$t('common._common.max_length_reached'))
      }
      this.isSaving = false
    },
    async closeDialogBox() {
      this.$emit('closeDefaultVaueDialog')
    },
  },
}
</script>
<style lang="scss">
.default-value-dialog {
  min-height: 300px;
  max-height: 400px;
  overflow: scroll;

  .f-img-container {
    .attached-files {
      margin-top: 10px;
    }
  }
  .fc-attachment-row {
    border: 0.5px solid #d8d8d8;
    padding-top: 15px;
    margin-bottom: 20px;
    margin-left: 5px;
    height: 70px;
    cursor: default !important;
    .attached-files {
      width: 500px !important;
    }
    .attachment-delete {
      color: #de7272;
      font-size: 16px;
      margin-top: 10px;
    }
  }
}
</style>
