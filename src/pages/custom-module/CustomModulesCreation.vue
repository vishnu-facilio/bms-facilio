<template>
  <div
    :class="[
      !isCreateEditWidgetConfigured
        ? 'form-data-creation'
        : 'form-widget-container',
      !isEmpty(widgetsArr) ? 'form-creation-widgets' : '',
      customClassForContainer,
    ]"
  >
    <div v-if="showEmptyState" class="form-empty-container">
      <inline-svg
        src="svgs/no-configuration"
        class="d-flex module-view-empty-state"
        iconClass="icon"
      ></inline-svg>
      <div class="mB10 mT10 no-template-text f14 self-center">
        {{ $t('forms.live_form.no_forms') }}
      </div>
      <div class="contact-admin-text f14 self-center">
        {{ $t('forms.live_form.contact_admin') }}
      </div>
    </div>
    <div
      v-else
      :class="[!$validation.isEmpty(widgetsArr) ? 'form-left-container' : '']"
      class="height-100"
    >
      <div v-if="!isCreateEditWidgetConfigured" class="header mT20 mB10 d-flex">
        <div class="title mT10">{{ title }}</div>
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
      <ConnectedAppViewWidget
        v-else-if="isCreateEditWidgetConfigured"
        :widgetId="widgetId"
        :recordId="moduleDataId"
        :context="moduleData"
      ></ConnectedAppViewWidget>
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
    <portal-target class="widgets-container" name="side-bar-widgets">
    </portal-target>
  </div>
</template>
<script>
import { isEmpty, isFunction } from '@facilio/utils/validation'
import FormCreation from '@/base/FormCreation'
import ConnectedAppViewWidget from 'pages/connectedapps/ConnectedAppViewWidget'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { CustomModuleData } from './CustomModuleData'

export default {
  extends: FormCreation,
  props: ['dataObj', 'selectedFormDetails'],
  components: { ConnectedAppViewWidget },
  mixins: [FetchViewsMixin],
  data() {
    return {
      widgetId: null,
      isEmpty,
    }
  },
  computed: {
    showEmptyState() {
      let { forms } = this || {}
      return (
        !this.isLoading && isEmpty(forms) && !this.isCreateEditWidgetConfigured
      )
    },
    moduleName() {
      let {
        $route: {
          params: { moduleName },
        },
      } = this

      if (!moduleName) moduleName = this.$attrs.moduleName // TODO: make this a prop and use directly

      return isEmpty(moduleName) ? null : moduleName
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    isWidgetsSupported() {
      return true
    },
    isCreateEditWidgetConfigured() {
      let { widgetId } = this
      return !isEmpty(widgetId)
    },
    isV3Api() {
      return true
    },
    modelDataClass() {
      return CustomModuleData
    },
    customProps() {
      return {}
    },
    loadDataManually() {
      return false
    },
  },
  methods: {
    async init() {
      let { moduleDataId, moduleName, loadDataManually } = this
      this.isLoading = true
      if (!isEmpty(moduleDataId) || loadDataManually) {
        await this.loadModuleData({
          moduleName,
          moduleDataId,
        })
      } else {
        this.moduleData = new this.modelDataClass({
          moduleName,
          ...(this.dataObj || {}),
        })
      }

      if (!isEmpty(this.selectedFormDetails)) {
        this.selectedForm = await this.loadFormData(
          null,
          this.selectedFormDetails || null
        )
        return
      }

      // To load to widgets and replace the creation form for the module
      let widgetId = await this.getCreateEditRecordWidgetId()

      if (!isEmpty(widgetId)) {
        this.widgetId = widgetId
        this.isLoading = false
      } else {
        await this.loadFormsList(moduleName)
        this.setInitialForm()
      }
      this.isLoading = false
    },
    async getCreateEditRecordWidgetId() {
      let { isEdit } = this
      let operatorValue = isEdit ? 7 : 6
      let filters = {
        entityType: {
          operatorId: 9,
          value: [`${operatorValue}`],
        },
      }

      this.isLoading = true
      try {
        let { connectedAppWidgets } = await this.modelDataClass.loadWidgets(
          filters
        )
        let [selectedWidget] = connectedAppWidgets || []
        let { id: widgetId } = selectedWidget || {}

        return widgetId
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.form.widget_error')
        )
      }
    },
    async loadFormsList(moduleName) {
      try {
        this.forms = await this.modelDataClass.loadFormsList(moduleName)
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.form.form_template_error')
        )
      }
    },
    async loadModuleData({ moduleDataId, moduleName }) {
      try {
        let { customProps } = this || {}
        this.moduleData = await this.modelDataClass.fetch({
          moduleName,
          id: moduleDataId,
          ...customProps,
        })
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.summary.record_summary_error')
        )
      }
    },
    async loadFormData(formId, selectedFormDetails) {
      let {
        selectedForm,
        moduleName,
        moduleDataId,
        moduleData,
        loadDataManually,
        dataObj,
      } = this
      let formObj = {}

      try {
        let form = await this.modelDataClass.loadFormData({
          formId,
          moduleName,
          selectedForm: selectedFormDetails || selectedForm,
        })

        if (!isEmpty(form)) {
          formObj = {
            ...form,
            secondaryBtnLabel: this.$t('common._common.cancel'),
            primaryBtnLabel: this.$t('common._common._save'),
          }

          if (
            (!isEmpty(moduleDataId) && !isEmpty(moduleData?.id)) ||
            loadDataManually ||
            !isEmpty(dataObj)
          ) {
            formObj = this.moduleData.deserialize(formObj)
          }
          this.formObj = formObj
        }
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.form.form_details_error')
        )
      }

      return formObj
    },
    async saveRecord(formModel) {
      let { formObj, afterSaveHook, afterSerializeHook } = this

      this.isSaving = true
      //same params as serialize method
      let response = await this.moduleData.save(
        formObj,
        formModel,
        afterSerializeHook
      )
      this.isSaving = false

      // Hook to handle notification after crud operation
      this.notificationHandler(response)

      // Hook to handle response after crud operation
      if (!isEmpty(afterSaveHook) && isFunction(afterSaveHook)) {
        this.afterSaveHook(response)
      }
    },
    afterSaveHook(response) {
      let { moduleName } = this
      let { [moduleName]: data } = response
      let { id } = data
      this.redirectToSummary(id)
    },
    async redirectToSummary(id) {
      this.isSaving = true

      let { moduleName } = this
      let viewname = await this.fetchView(moduleName)

      this.isSaving = false

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({ name, params: { viewname, id } })
        }
      } else {
        this.$router
          .push({
            name: 'custommodules-summary',
            params: {
              id,
              viewname,
            },
          })
          .catch(() => {})
      }
    },
    redirectToList() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}

        if (name) {
          this.$router.push({ name })
        }
      } else {
        this.$router
          .push({ name: 'custommodules-list', params: { moduleName } })
          .catch(() => {})
      }
    },
    showErrorToastMessage(error, customMsg) {
      let message = error?.message || customMsg
      this.$message.error(message)
    },
  },
}
</script>
<style lang="scss">
.form-widget-container {
  height: 100%;
}
.no-template-text {
  color: #6fa5f2;
  font-weight: bold;
}
.contact-admin-text {
  color: #6fa5f2;
}
</style>
