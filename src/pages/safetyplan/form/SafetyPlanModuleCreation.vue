<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="formDisplayName"
      custom-class="fc-dialog-center-container f-webform-right-dialog f-webform-center-dialog"
    >
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
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['visibility', 'editId', 'moduleName'],
  data() {
    return {
      isSaving: false,
      requestUrl: {
        safetyPlan: 'safetyplan',
        hazard: 'hazard',
        precaution: 'precaution',
      },
      moduleKey: {
        safetyPlan: 'safetyPlan',
        hazard: 'hazard',
        precaution: 'precautions',
      },
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
  },
  methods: {
    saveRecord(formModel) {
      let {
        moduleDataId,
        formObj,
        moduleName,
        requestUrl,
        moduleDisplayName,
        moduleKey,
      } = this
      let { formId } = formModel
      let url = ''
      let successMsg
      let serializedData = this.serializedData(formObj, formModel)
      if (isEmpty(moduleDataId)) {
        url = `/v2/${requestUrl[moduleName]}/add`
        successMsg = `${moduleDisplayName} created successfully!`
      } else {
        url = `/v2/${requestUrl[moduleName]}/update`
        successMsg = `${moduleDisplayName} updated successfully!`
        serializedData.id = moduleDataId
      }
      if (!isEmpty(formId)) {
        serializedData.formId = formId
      }
      if (moduleName === 'precaution') {
        serializedData = [serializedData]
      }
      let param = {
        [moduleKey[moduleName]]: serializedData,
      }
      this.isSaving = true
      let promise = this.$http
        .post(url, param)
        .then(({ data: { message, responseCode } }) => {
          if (responseCode === 0) {
            this.$emit('saved')
            this.closeDialog()
            this.$message.success(successMsg)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => (this.isSaving = false))
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
