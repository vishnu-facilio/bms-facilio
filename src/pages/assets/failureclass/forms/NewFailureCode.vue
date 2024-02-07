<template>
  <div>
    <el-dialog
      v-if="showCreateNewDialog"
      :visible.sync="showCreateNewDialog"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="moduleDisplayName"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
    >
      <div class="f-form-header pT10 pB10 pL40 pR40 d-flex">
        <div class="f-form-title mT10">{{ this.moduleDisplayName }}</div>
        <el-select
          v-if="forms.length > 1"
          v-model="selectedForm"
          value-key="name"
          class="fc-input-full-border-select2 mL-auto width25"
        >
          <el-option
            v-for="(form, index) in forms"
            :key="index"
            :value="form"
            :label="form.displayName"
          ></el-option>
        </el-select>
      </div>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        formLabelPosition="top"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isEdit="!$validation.isEmpty(dataId)"
        @save="saveRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: FormCreation,
  props: ['canShowFormCreation', 'moduleName', 'moduleDisplayName', 'dataId'],
  computed: {
    moduleDataId() {
      return this.dataId
    },
    showCreateNewDialog() {
      return this.canShowFormCreation
    },
  },
  methods: {
    closeDialog(code) {
      this.$emit('update:canShowFormCreation', false)
      if (code?.id) this.$emit('closeDialog', code)
      else if (this.dataId) this.$emit('closeDialog')
    },

    async saveRecord(data) {
      let { formObj, moduleDisplayName, moduleName } = this
      let formId = formObj?.id
      let successMsg = ''
      let failurecodeToSave = this.$helpers.cloneObject(data)

      if (!isEmpty(this.dataId)) {
        successMsg = `${moduleDisplayName} updated successfully!`
        failurecodeToSave.id = this.dataId
      }
      this.isSaving = true
      if (!isEmpty(formId)) {
        failurecodeToSave.formId = formId
      }
      let response = {}
      if (isEmpty(this.dataId)) {
        response = await API.createRecord(moduleName, {
          data: failurecodeToSave,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: this.dataId,
          data: failurecodeToSave,
        })
      }
      let { error } = response || {}
      this.isSaving = false
      if (error) {
        this.$message.error(
          error?.message || 'Error occured while saving Failure Code'
        )
      } else {
        successMsg && this.$message.success(successMsg)
        isEmpty(this.dataId)
          ? this.closeDialog(response.failurecode)
          : this.closeDialog()
      }
    },
  },
}
</script>
