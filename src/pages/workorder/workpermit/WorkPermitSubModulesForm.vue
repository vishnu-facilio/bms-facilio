<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="formDisplayName"
      custom-class="fc-dialog-center-container f-webform-right-dialog f-webform-center-dialog width35"
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
import { API } from '@facilio/api'
export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['visibility', 'editId', 'moduleName', 'appendDataMeta'],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    formDisplayName() {
      return this.$getProperty(this.formObj, 'displayName', '')
    },
    moduleDisplayName() {
      return this.$getProperty(this.formObj, 'module.displayName', '')
    },
    moduleDataId() {
      return this.editId
    },
  },
  methods: {
    async saveRecord(formModel) {
      let { moduleDataId, formObj } = this
      let { formId } = formModel
      let moduleData = this.$helpers.cloneObject(
        this.serializedData(formObj, formModel)
      )
      if (!isEmpty(formId)) {
        moduleData.formId = formId
      }
      if (!isEmpty(this.appendDataMeta)) {
        moduleData = { ...moduleData, ...this.appendDataMeta }
      }
      if (isEmpty(moduleDataId)) {
        this.isSaving = true
        let { error } = await API.createRecord(this.moduleName, {
          data: moduleData,
        })
        if (error) {
          let { message = 'Error Occured' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Created Successfully`)
          this.$emit('saved')
          this.closeDialog()
        }
        this.isSaving = false
      } else {
        this.isSaving = true
        let { error } = await API.updateRecord(this.moduleName, {
          id: moduleDataId,
          data: moduleData,
        })
        if (error) {
          let { message = 'Error Occured' } = error
          this.$message.error(message)
        } else {
          this.$message.success(`Updated Successfully`)
          this.$emit('saved')
          this.closeDialog()
        }
        this.isSaving = false
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
