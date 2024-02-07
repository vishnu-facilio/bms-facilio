<template>
  <div v-if="visibility">
    <el-dialog
      :fullscreen="true"
      :append-to-body="true"
      style="z-index: 1999"
      :visible="visibility"
      :title="$t('common.header.break')"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-webform-right-dialog"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ title }}
          </div>
        </div>
      </div>
      <div class="mT20">
        <div v-if="isLoading" class="loading-container d-flex">
          <Spinner :show="isLoading"></Spinner>
        </div>
        <f-webform
          v-else
          ref="f-webform"
          :form.sync="formObj"
          :module="moduleName"
          :moduleDisplayName="moduleDisplayName"
          :isSaving="isSaving"
          :canShowPrimaryBtn="true"
          :canShowSecondaryBtn="true"
          :isEdit="isEdit"
          :isV3Api="isV3Api"
          :moduleData="moduleData"
          :moduleDataId="moduleDataId"
          :isWidgetsSupported="isWidgetsSupported"
          :subFormRecords="subFormRecords"
          :modifyFieldPropsHook="modifyFieldPropsHook"
          @onBlur="onBlurHook"
          @onWidgetChange="onWidgetChange"
          @save="saveRecord"
          @cancel="redirectToList"
        ></f-webform>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import { isEmpty, isFunction } from '@facilio/utils/validation'

export default {
  extends: CustomModulesCreation,
  name: 'CreateEditBreak',
  props: ['id', 'viewName', 'visibility'],
  computed: {
    moduleDataId() {
      return this.id
    },
    moduleName() {
      return 'break'
    },
    moduleDisplayName() {
      return 'Break'
    },
    isEdit() {
      return this.id ? true : false
    },
  },
  methods: {
    async loadModuleData({ moduleDataId, moduleName }) {
      try {
        let { customProps } = this || {}
        this.moduleData = await this.modelDataClass.fetch({
          moduleName,
          id: moduleDataId,
          ...customProps,
        })
      } catch (errorMsg) {
        this.$message.error(errorMsg)
      }
    },
    async saveRecord(formModel) {
      let { formObj, afterSaveHook, afterSerializeHook } = this
      this.isSaving = true
      let response = await this.moduleData.save(
        formObj,
        formModel,
        afterSerializeHook
      )
      this.isSaving = false
      this.notificationHandler(response)

      // Hook to handle response after crud operation
      if (!isEmpty(afterSaveHook) && isFunction(afterSaveHook)) {
        this.afterSaveHook(response)
      }
      this.$emit('saved')
    },
    afterSaveHook(response) {
      let { error } = response || {}
      if (isEmpty(error)) this.redirectToList()
    },
    async redirectToList() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
