<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="formDisplayName"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
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
        :modifyFieldPropsHook="modifyFieldPropsHook"
        :moduleData="moduleData"
        @save="saveRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
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
  props: ['visibility', 'editId'],
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
      return 'tool'
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    async saveRecord(formModel) {
      let { formObj, moduleName, moduleDataId } = this
      let { formId, currencyCode, exchangeRate } = formModel
      let response = {}
      let data = this.serializedData(formObj, formModel)
      if (!isEmpty(formId)) {
        data.formId = formId
      }
      if (!isEmpty(moduleDataId)) {
        data.id = moduleDataId
      }
      if (!isEmpty(currencyCode)) {
        data.currencyCode = currencyCode
        data.exchangeRate = exchangeRate
      }
      this.isSaving = true
      let params = {
        updateToolAttributes: true,
      }
      response = await API.updateRecord(moduleName, {
        id: moduleDataId,
        data,
        params,
      })
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
    modifyFieldPropsHook(field) {
      let { name } = field || {}

      if (['toolType', 'storeRoom'].includes(name)) {
        return { ...field, isDisabled: true }
      }
    },
  },
}
</script>
