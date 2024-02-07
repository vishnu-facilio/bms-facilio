<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    :close-on-click-modal="false"
    :show-close="false"
    :before-close="cancelForm"
    custom-class="line-item-popup-form-dialog"
    class="line-item-popup-form"
  >
    <template #title>
      <div
        v-if="hasPreviousDialog"
        class="line-item-popup-form-header"
        @click="openPreviousDialog"
      >
        <fc-icon
          group="dsm"
          name="chevron-left"
          size="16"
          color="#3ab2c2"
          class="mR8"
        ></fc-icon>
        {{ $t('common.header.previous') }}
      </div>
      <div class="line-item-popup-form-header-title">{{ customTitle }}</div>
    </template>
    <div v-if="forms.length > 1" class="form-list-dropdown d-flex mT12">
      <el-select
        v-model="selectedForm"
        value-key="name"
        class="fc-input-full-border-select2 mL-auto"
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
    <FWebform
      v-else
      ref="line-item-f-web-form"
      :form.sync="formObj"
      :module="moduleName"
      :isSaving="isSaving"
      :isEdit="isEdit"
      :isV3Api="true"
      :isWidgetsSupported="false"
      :moduleData="moduleData"
      :moduleDataId="moduleDataId"
      :subFormRecords="subFormRecords"
      formLabelPosition="1"
      :modifyFieldPropsHook="config.modifyFieldPropsHook"
      @save="saveRecord"
      @onBlur="config.onBlurHook"
      @onWidgetChange="config.onWidgetChange"
    ></FWebform>
    <template #footer>
      <el-button @click="cancelForm">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        type="primary"
        :loading="isSaving"
        @click="onClickSaveRecord"
        >{{ $t('common._common._save') }}</el-button
      >
    </template>
  </el-dialog>
</template>
<script>
import CustomModulesCreation from 'src/pages/custom-module/CustomModulesCreation'
import { isEmpty, isFunction } from '@facilio/utils/validation'

export default {
  extends: CustomModulesCreation,
  props: [
    'dataId',
    'config',
    'additionalParams',
    'hideNotification',
    'hasPreviousDialog',
    'recordConversionDetails',
  ],
  computed: {
    moduleDataId() {
      return this.dataId
    },
    isEdit() {
      return !isEmpty(this.dataId)
    },
    moduleName() {
      return this.$attrs.moduleName
    },
    moduleDisplayName() {
      return this.$attrs.moduleDisplayName
    },
    customTitle() {
      let { formTitle } = this.config
      let { defaultForm, addForm, editForm } = formTitle || {}
      let customTitle

      if (this.isEdit && editForm) customTitle = editForm
      else if (!this.isEdit && addForm) customTitle = addForm
      else if (defaultForm) customTitle = defaultForm
      else customTitle = this.title

      return customTitle
    },
    loadDataManually() {
      let { selectedLookupModuleId, currentBtnDetails } =
        this.recordConversionDetails || {}
      let { getRecordDetails } = currentBtnDetails || {}

      return (
        !isEmpty(selectedLookupModuleId) &&
        !isEmpty(getRecordDetails) &&
        isFunction(getRecordDetails)
      )
    },
  },
  methods: {
    openPreviousDialog() {
      this.$emit('openPrevious')
    },
    cancelForm() {
      this.$emit('onClose')
    },
    async loadModuleData({ moduleDataId, moduleName }) {
      try {
        let { customProps, loadDataManually, recordConversionDetails } = this

        if (!loadDataManually) {
          this.moduleData = await this.modelDataClass.fetch({
            moduleName,
            id: moduleDataId,
            ...customProps,
          })
        } else {
          let { selectedLookupModuleId: id, currentBtnDetails } =
            recordConversionDetails || {}
          let { getRecordDetails, lookupModuleName } = currentBtnDetails || {}
          let payload = { id, lookupModuleName, moduleName, ...customProps }

          this.moduleData = await getRecordDetails(payload)
        }
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.summary.record_summary_error')
        )
      }
    },
    afterSerializeHook({ data }) {
      let { additionalParams, moduleData } = this
      let additionalPayLoad = isFunction(additionalParams)
        ? additionalParams(data, moduleData)
        : additionalParams

      return { ...data, ...(additionalPayLoad || {}) }
    },
    afterSaveHook(response) {
      let { error } = response || {}

      this.$emit('onSave', response)
      if (!error) {
        this.cancelForm()
      }
    },
    onClickSaveRecord() {
      this.$refs['line-item-f-web-form']?.saveRecord()
    },
    notificationHandler(response) {
      if (!isEmpty(this.hideNotification)) return
      let { moduleDataId, moduleDisplayName } = this
      let { error } = response

      if (error) {
        let { message = 'Error occured' } = error
        this.$message.error(message)
      } else {
        let successMsg = moduleDataId
          ? `${moduleDisplayName} updated successfully`
          : `${moduleDisplayName} created successfully`
        this.$message.success(successMsg)
      }
    },
  },
}
</script>
<style lang="scss">
.line-item-popup-form {
  display: flex;

  .line-item-popup-form-dialog {
    margin: auto;
    height: max-content;
    max-height: calc(100% - 64px);
    max-width: 60%;
    width: 680px;
    display: flex;
    flex-direction: column;

    .el-dialog__header {
      flex-shrink: 0;
      padding: 16px 24px;
      border-bottom: 1px solid #e3e6e8;

      .line-item-popup-form-header {
        font-size: 14px;
        font-weight: 500;
        letter-spacing: 0.5px;
        color: #3ab2c2;
        display: flex;
        align-items: center;
        cursor: pointer;
        margin-bottom: 12px;
      }
      .line-item-popup-form-header-title {
        font-size: 16px;
        font-weight: 500;
        letter-spacing: 0.3px;
        color: #324056;
        padding-left: 24px;
      }
    }
    .el-dialog__body {
      padding: 0px 48px;
      overflow: scroll;

      & > .loading-container {
        display: flex;
        margin: auto;
        min-height: 200px;
      }

      .height-100 {
        height: auto !important;
      }

      .f-webform-container {
        height: auto;

        .el-form .section-container {
          padding: 0px;
          border: 0px;
          padding-right: 150px;

          .section-items {
            padding: 0px;
          }
        }
      }
    }
    .el-dialog__footer {
      flex-shrink: 0;
      border-top: 1px solid #e3e6e8;
      padding: 16px;
    }
  }
  //Overriding existing css class style to new color

  .el-button {
    border-color: #3ab2c2;
    padding: 12px 16px;
    min-width: 96px;
  }
  .el-button--primary,
  .el-button--primary.is-disabled,
  .el-button--primary.is-disabled:active {
    background-color: #3ab2c2;
    border-color: #3ab2c2;
  }
  .el-button:hover,
  .el-button:focus,
  .el-button--primary:hover,
  .el-button--primary:focus,
  .el-button--primary.is-disabled:hover,
  .el-button--primary.is-disabled:focus {
    color: #fff;
    background-color: #3cbfd0;
    border-color: #3cbfd0;
  }

  .fc-radio-btn .el-radio__inner:hover {
    border-color: #3ab2c2;
  }
  .fc-radio-btn .el-radio__input.is-checked .el-radio__inner {
    border-color: #3ab2c2;
    background: #3ab2c2;
  }
}
</style>
