<template>
  <el-dialog
    :visible.sync="visibility"
    :before-close="closeDialog"
    :append-to-body="true"
    :title="formDisplayName"
    :fullscreen="true"
    custom-class="terms-form-dialog fc-dialog-form setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
  >
    <div v-if="!isLoading" class="fc-pm-main-content-H">
      {{ formDisplayName }}
    </div>
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
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['tAndCData', 'visibility', 'editId', 'isRevised'],
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
    moduleName() {
      return 'termsandconditions'
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
<style lang="scss">
.terms-form-dialog.el-dialog.fc-dialog-form {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  overflow-y: hidden;
  border-radius: 0px;
  .el-button + .el-button,
  .el-checkbox.is-bordered + .el-checkbox.is-bordered {
    margin-left: 0;
  }
}
</style>
