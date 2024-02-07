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
        @save="saveRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
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
      return 'amenity'
    },
  },
  methods: {
    afterSaveHook({ error }) {
      if (!error) this.afterSave()
    },
    afterSave() {
      this.$emit('saved')
      this.closeDialog()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
