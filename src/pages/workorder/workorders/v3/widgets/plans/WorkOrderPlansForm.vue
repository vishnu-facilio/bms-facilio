<template>
  <f-webform
    v-if="!isLoading"
    :form="formObj"
    :module="moduleName"
    :moduleDisplayName="formDisplayName"
    :moduleData="moduleDataObj"
    formLabelPosition="top"
    :canShowPrimaryBtn="true"
    :canShowSecondaryBtn="true"
    :moduleDataId="moduleDataId"
    @save="saveRecord"
    @cancel="closeDialog"
  ></f-webform>
  <div v-else>
    <spinner :show="isLoading" size="80"></spinner>
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'WorkOrderPlansForm',
  extends: FormCreation,
  props: ['editId', 'moduleName', 'moduleDataObj'],
  components: {
    FWebform,
  },
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
      let { formObj } = this
      if (formObj && formObj.module) {
        return this.$getProperty(formObj, 'module.displayName')
      }
      return ''
    },
    moduleDataId() {
      return this.editId
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    async saveRecord(formModel) {
      let { formObj, moduleName, moduleDataId } = this
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

      response = await API.updateRecord(moduleName, {
        id: moduleDataId,
        data,
      })

      this.isSaving = false
      // Hook to handle notification after crud operation
      this.notificationHandler(response)

      let { error } = response
      if (!error) {
        this.$emit('save')
      }
    },
    closeDialog() {
      this.$emit('cancel')
    },
  },
}
</script>
