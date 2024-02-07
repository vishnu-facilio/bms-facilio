<template>
  <div class="dsm-main-container">
    <div class="form-pattern-container">
      <inline-svg
        src="svgs/form-background"
        iconClass="width100 height500 z-10"
        class="form-background-pattern"
      ></inline-svg>
    </div>
    <div v-if="showEmptyState" class="form-empty-container z-20">
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
    <div v-else class="dsm-form-container" id="dsm-form-container">
      <div :style="formSwitchStyle">
        <FormSwitch
          :form="selectedForm"
          :formsList="forms"
          :moduleDisplayName="moduleDisplayName"
          :isEdit="isEdit"
          class="form-switch"
          @switchForm="switchForm"
        />
      </div>
      <!-- <FShimmer v-else :width="190" :height="23" class="mT20 mB20" /> -->
      <div v-if="isLoading" class="loading-container">
        <LiveFormLoader />
      </div>
      <template v-else>
        <ConnectedAppViewWidget
          v-if="isCreateEditWidgetConfigured"
          :widgetId="widgetId"
          :recordId="moduleDataId"
          :context="moduleData"
          class="z-20"
        ></ConnectedAppViewWidget>
        <div v-else class="dsm-form-wrapper z-20">
          <LiveForm
            :form="formObj"
            :formsList="forms"
            :isV3Api="isV3Api"
            :showHeader="true"
            :isEdit="isEdit"
            :moduleDataId="moduleDataId"
            :moduleData="moduleData"
            :saving="isSaving"
            :loading="isLoading"
            :connectedAppEventChannel="$connectedAppEventChannel"
            :account="$account"
            :modifyFieldPropsHook="setSelectedCategory"
            @save="saveRecord"
            @cancel="redirectToList"
            @hasConnectedApp="hasConnectedApp"
          />
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import DsmForm from 'src/pages/custom-module/DSMFormCreation.vue'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: DsmForm,
  computed: {},
  methods: {
    setSelectedCategory(field) {
      let { $route } = this
      let { query } = $route
      let { utilityTypeId } = query
      if (field.name === 'utilityType') {
        this.$set(field, 'value', utilityTypeId)
        this.$set(field, 'disabled', true)
      }
      return field
    },
    async saveRecord(data) {
      let { formObj, moduleDataId, moduleDisplayName, moduleName } = this
      let formId = formObj?.id
      let successMsg = ''
      let meterToSave = this.$helpers.cloneObject(data)
      meterToSave = this.serializedData(formObj, meterToSave)
      if (isEmpty(moduleDataId)) {
        successMsg = `${moduleDisplayName} created successfully!`
      } else {
        successMsg = `${moduleDisplayName} updated successfully!`
        meterToSave.id = moduleDataId
      }
      this.isSaving = true
      if (!isEmpty(formId)) {
        meterToSave.formId = formId
      }
      let response = {}
      if (isEmpty(moduleDataId)) {
        response = await API.createRecord(moduleName, {
          data: meterToSave,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data: meterToSave,
        })
      }
      let { error, [moduleName]: record } = response || {}
      this.isSaving = false
      if (error) {
        this.$message.error(
          error?.message || 'Error occured while saving meter'
        )
      } else {
        this.$message.success(successMsg)
        this.redirectToSummary(record?.id)
      }
    },
    /* Overrided hooks */
    async loadFormsList(moduleName) {
      let { $route } = this
      let { query } = $route
      let { categoryModuleName } = query
      let url = `/v2/forms?moduleName=${categoryModuleName}&fetchExtendedModuleForms=${true}`
      this.isLoading = true
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { forms = [] } = data || {}
        this.$set(this, 'forms', forms)
      }
    },
    redirectToSummary(id) {
      if (isWebTabsEnabled() && !isEmpty(id)) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: { id, viewname: 'all' },
          })
      }
    },
    redirectToList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'meter',
        })
      }
    },
  },
}
</script>
