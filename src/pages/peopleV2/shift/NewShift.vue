<template>
  <div v-if="visibility">
    <el-dialog
      :fullscreen="true"
      :append-to-body="true"
      style="z-index: 1999"
      :visible="visibility"
      :title="$t('common.header.shift')"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-webform-right-dialog"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ title }}
          </div>
        </div>
      </div>
      <div class="mT20 new-shift">
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
  props: ['id', 'viewName', 'visibility'],
  computed: {
    moduleDataId() {
      return this.id
    },
    moduleName() {
      return 'shift'
    },
    moduleDisplayName() {
      return 'Shift'
    },
    isEdit() {
      return this.id ? true : false
    },
  },
  methods: {
    afterSerializeHook({ data }) {
      if (!data?.isDefault) {
        data.isDefault = false
      }
      if (!data?.isActive) {
        data.isActive = true
      }
      return data
    },
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
    hoursToSeconds(time) {
      const SECONDS_IN_HOUR = 60 * 60
      const SECONDS = 60

      let hours = parseInt(time.slice(0, 2))
      let minutes = parseInt(time.slice(3, 5))
      let timeInSeconds = hours * SECONDS_IN_HOUR + minutes * SECONDS
      return timeInSeconds
    },
    timeToSeconds(formModel) {
      if (!formModel) {
        return
      }
      if (formModel.startTime) {
        formModel.startTime = this.hoursToSeconds(formModel.startTime)
      }
      if (formModel.endTime) {
        formModel.endTime = this.hoursToSeconds(formModel.endTime)
      }
      return formModel
    },
    formatTime(t) {
      return t.toString().padStart(2, '0')
    },
    secondsToHours(seconds) {
      if (!seconds) {
        return '00:00'
      }
      const HOUR_IN_SECONDS = 60 * 60
      const MINUTE_IN_SECONDS = 60
      let minutes = (seconds % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS
      let hours = Math.floor(seconds / HOUR_IN_SECONDS)

      return this.formatTime(hours) + ':' + this.formatTime(minutes)
    },
    toHumanReadableTime() {
      if (!this.moduleData) {
        return
      }
      this.moduleData['startTime'] = this.secondsToHours(
        this.moduleData['startTime']
      )
      this.moduleData['endTime'] = this.secondsToHours(
        this.moduleData['endTime']
      )
    },
  },
}
</script>

<style>
.el-color-dropdown__main-wrapper,
.el-color-dropdown__value {
  display: none;
}
</style>
