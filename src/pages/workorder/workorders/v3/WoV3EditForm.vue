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
        :moduleDataId="moduleDataId"
        :moduleData="moduleData"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isV3Api="true"
        :modifySectionPropsHook="modifySectionPropsHook"
        @save="saveRecord"
        @cancel="cancelForm"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import Tasks from '@/mixins/tasks/TasksMixin'
import { isEmpty, isArray } from '@facilio/utils/validation'
export default {
  extends: CustomModulesCreation,
  mixins: [FetchViewsMixin, Tasks],
  props: {
    visibility: Boolean,
    wo: Object,
  },
  name: 'WoV3EditForm',
  methods: {
    cancelForm() {
      this.$emit('visibilityUpdate', false)
    },
    afterSaveHook() {
      this.$emit('visibilityUpdate', false)
      this.$root.$emit('reloadWO')
    },
    modifySectionPropsHook(section) {
      // editing tasks is not allowed; hiding the section in form
      let { fields } = section
      if (!isEmpty(fields) && isArray(fields) && fields.length === 1) {
        let { displayTypeEnum } = fields[0]
        if (displayTypeEnum === 'TASKS')
          return { ...section, hideSection: true }
      } else {
        // guarenteed to have 1 assignment field
        let assignmentFldIndex = fields.findIndex(f => f.name === 'assignment')

        if (assignmentFldIndex === -1) return

        let assignmentField = fields[assignmentFldIndex] || {}
        let userObj = this.$getProperty(
          assignmentField,
          'value.assignedTo.id',
          null
        )
        let groupObj = this.$getProperty(
          assignmentField,
          'value.assignmentGroup.id',
          null
        )
        this.$set(
          assignmentField.value,
          'assignedTo',
          !isEmpty(userObj) ? userObj : { id: '' }
        )
        this.$set(
          assignmentField.value,
          'assignmentGroup',
          !isEmpty(groupObj) ? groupObj : { id: '' }
        )
        fields.splice(assignmentFldIndex, 1, assignmentField)
        this.$set(section, 'fields', fields)
      }
    },
  },
}
</script>
