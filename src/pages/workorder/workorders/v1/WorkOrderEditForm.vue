<template>
  <div v-if="visibility">
    <el-dialog
      :fullscreen="true"
      :append-to-body="true"
      style="z-index: 1999"
      :visible="visibility"
      :title="$t('common._common.workorder_edit')"
      custom-class="fc-wo-edit-form-dialog fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-webform-right-dialog new-asset-container"
      :before-close="cancelForm"
    >
      <div class="header pT10 pB10 pL30 pR30 d-flex">
        <div class="title mT10">
          {{ $t('common._common.workorder_edit') }}
        </div>
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
        :isSaving="isSaving"
        :isEdit="true"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :modifySectionPropsHook="modifySectionPropsHook"
        :modifyFieldPropsHook="modifyFieldPropsHook"
        @save="saveRecord"
        @cancel="cancelForm"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import FWebform from '@/FWebform'
import cloneDeep from 'lodash/cloneDeep'
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'

export default {
  name: 'V2Edit',
  extends: FormCreation,
  props: ['visibility', 'recordData'],
  components: { FWebform },
  data() {
    return {
      moduleName: 'workorder',
      fieldMap: {},
      isLoading: true,
      isSaving: false,
    }
  },
  computed: {
    moduleDataId() {
      return this.recordData.id
    },
    hasChangeOwnershipPermission() {
      return (
        this.$hasPermission(`${this.moduleName}:CHANGE_OWNERSHIP`) ||
        this.$hasPermission(`${this.moduleName}:UPDATE_CHANGE_OWNERSHIP`)
      )
    },
  },
  methods: {
    async saveRecord(formModel) {
      this.isSaving = true

      let { id } = this.recordData || {}
      let serializedData = cloneDeep(
        this.serializedData(this.formObj, formModel)
      )
      let { selectedForm } = this
      let formId = (selectedForm || {}).id

      let formData = {
        id: [id],
        workorder: { ...serializedData, id, formId: formId || -1 },
      }

      // if (isWebTabsEnabled()) {
      //   //console.log('editing wo with v3')
      //   // ~ need to handle ~ //
      //   let { error } = await API.updateRecord('workorder', formData)

      //   if (isEmpty(error)) {
      //     this.$message.success(
      //       this.$t('common.products.workorder_updated_successfully')
      //     )
      //     this.$emit('saved')
      //     this.cancelForm()
      //   } else {
      //     this.$message.error(error.message || 'Error Occurred')
      //   }
      // }

      //console.log('editing wo with v2')
      let { error } = await API.post('v2/workorders/update', formData)

      if (isEmpty(error)) {
        this.$message.success(
          this.$t('common.products.workorder_updated_successfully')
        )
        this.$emit('saved')
        this.cancelForm()
      } else {
        this.$message.error(error.message || 'Error Occurred')
      }

      this.isSaving = false
    },
    cancelForm() {
      this.$emit('visibilityUpdate', false)
    },
    modifySectionPropsHook(section) {
      let { fields } = section
      if (!isEmpty(fields) && isArray(fields) && fields.length === 1) {
        let { displayTypeEnum } = fields[0]
        if (displayTypeEnum === 'TASKS')
          return { ...section, hideSection: true }
      }
    },
    modifyFieldPropsHook(field) {
      let { name, lookupModuleName, displayTypeEnum } = field || {}

      if (['subject', 'siteId'].includes(name)) {
        return { ...field, isDisabled: true }
      } else if (name === 'assignment' && !this.hasChangeOwnershipPermission) {
        return { ...field, isDisabled: true }
      } else if (lookupModuleName === 'ticketattachments') {
        return { ...field, hideField: true }
      } else if (displayTypeEnum === 'TASKS') {
        return { ...field, hideField: true }
      }
    },
  },
}
</script>
<style lang="scss">
.fc-wo-edit-form-dialog {
  .header {
    .title {
      font-size: 14px;
      font-weight: 700;
      letter-spacing: 0.3px;
      letter-spacing: 0.9px;
      color: #333;
      text-transform: uppercase;
      background: #fff;
    }
    border-bottom: 1px solid #ebedf4;
  }
  .pT50 {
    padding-top: 20px;
  }
  .f-webform-container .el-form .section-header {
    margin-bottom: 30px;
  }
}
</style>
