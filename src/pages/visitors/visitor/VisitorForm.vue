<template>
  <el-dialog
    :visible.sync="visibility"
    :before-close="closeDialog"
    :append-to-body="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
  >
    <div class="fc-pm-main-content-H">{{ formDisplayName }}</div>
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      v-else
      :form.sync="formObj"
      :module="moduleName"
      :moduleDisplayName="formDisplayName"
      :isSaving="isSaving"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
      :isV3Api="isV3Api"
      :moduleDataId="moduleDataId"
      :isEdit="!$validation.isEmpty(moduleDataId)"
      @save="saveRecord"
      @cancel="closeDialog"
    ></f-webform>
  </el-dialog>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['visibility', 'editId', 'moduleName'],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    formDisplayName() {
      return (this.formObj || {}).displayName
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    moduleDataId() {
      return this.editId
    },
    moduleKey() {
      return 'visitors'
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    async saveRecord(formModel) {
      let { formObj, moduleName, moduleDataId, isRevised } = this
      let { formId } = formModel
      let response = {}
      let data = this.serializedData(formObj, formModel)
      if (!isEmpty(formId)) {
        data.formId = formId
      }
      if (!isEmpty(moduleDataId)) {
        data.id = moduleDataId
      }
      this.isSaving = true
      if (isEmpty(moduleDataId) || isRevised) {
        let requestObj = { data }

        if (isRevised) {
          requestObj.data = { ...data, isPublished: true }
          requestObj.params = { revise: true }
        }
        response = await API.createRecord(moduleName, requestObj)
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        })
      }
      this.isSaving = false
      // Hook to handle notification after crud operation
      this.notificationHandler(response)

      let { error } = response
      if (!error) {
        this.$emit('saved')
        this.closeDialog()
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
