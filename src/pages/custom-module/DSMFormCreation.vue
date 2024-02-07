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
            :modifyFieldPropsHook="modifyFieldPropsHook"
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
import { LiveForm, FormSwitch, LiveFormLoader } from '@facilio/forms'
import CustomModulesCreation from './CustomModulesCreation.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'NewFormCreation',
  extends: CustomModulesCreation,
  components: { LiveForm, FormSwitch, LiveFormLoader },
  data: () => ({
    formSwitchStyle: 'width: 700px',
  }),
  computed: {
    canShowFormSwitch() {
      let { isCreateEditWidgetConfigured, forms } = this || {}
      return !isEmpty(forms) && !isCreateEditWidgetConfigured
    },
    connectedAppEventChannel() {
      return this.$connectedAppEventChannel
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    switchForm(value) {
      let { forms } = this || {}
      let selectedForm = forms.find(form => form.id == value)

      this.selectedForm = selectedForm
      this.formSwitchStyle = 'width: 700px'
    },
    hasConnectedApp(val) {
      if (val) this.formSwitchStyle = 'width: 1110px'
      else this.formSwitchStyle = 'width: 700px'
    },
  },
}
</script>
<style scoped>
.dsm-main-container {
  height: 100vh;
  display: flex;
  align-items: center;
  flex-direction: column;
  position: relative;
}
.dsm-form-wrapper {
  width: 100%;
}
.form-empty-container {
  width: 700px;
  height: calc(100vh - 130px);
  margin-top: 60px;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  border-radius: 8px;
  box-shadow: 0 0 1px rgba(67, 90, 111, 0.3),
    0 2px 4px -2px rgba(67, 90, 111, 0.47);
}

.dsm-form-container {
  z-index: 20;
  height: calc(100vh - 60px);
  overflow-y: scroll;
  padding: 0px 10px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.loading-container {
  width: 700px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}
.form-switch {
  width: 600px;
}
.form-pattern-container {
  width: 82%;
  height: 100%;
  opacity: 0.2;
  top: 0px;
  z-index: 10;
  position: absolute;
}
.form-background-pattern {
  width: 100%;
  border-radius: 23px;
  overflow: hidden;
  height: 40%;
}
</style>
