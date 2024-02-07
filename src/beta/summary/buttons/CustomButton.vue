<template>
  <FContainer v-if="!$validation.isEmpty(customButtons)" display="flex">
    <FButton
      v-if="customButtons.length == 1"
      :disabled="actionLoading"
      :loading="loading"
      @click="startButtonAction(button)"
    >
      {{ button.name }}</FButton
    >
    <FDropdown
      v-else
      :name="$getProperty(buttons, `featured.0.name`)"
      :split="true"
      :options="dropdownOptions"
      :disabled="actionLoading"
      :loading="loading"
      @dropdown="startButtonAction"
      @click="startButtonAction(buttons.featured[0])"
    />
    <template v-if="showRecordActionForm">
      <CreateUpdateRecord
        :selectedButton="selectedButton"
        :record="record"
        :moduleName="moduleName"
        :updateUrl="updateUrl"
        :transformFn="transformFn"
        :actionType="currentActionType"
        :isPortalApp="isPortalApp"
        @response="responseHandler"
        @closed="
          () => {
            this.showRecordActionForm = false
          }
        "
      />
    </template>
    <template v-if="showBulkActionForm">
      <BulkUpdateRecord
        :selectedButton="selectedButton"
        :selectedRecords="selectedRecords"
        :moduleName="moduleName"
        @response="responseHandler"
        @closed="
          () => {
            this.showBulkActionForm = false
          }
        "
      />
    </template>
    <template v-if="showConnectedAppWidget">
      <ConnectedAppWidget
        :record="record"
        :widgetId="widgetId"
        :showConnectedAppWidget.sync="showConnectedAppWidget"
        @loadData="loadData"
      />
    </template>
  </FContainer>
</template>

<script>
import { FButton, FDropdown, FContainer } from '@facilio/design-system'
import CustomButton from '@/custombutton/CustomButton.vue'
import { isEmpty } from '@facilio/utils/validation'
import CreateUpdateRecord from '@/custombutton/actions/CreateUpdateRecord'
import BulkUpdateRecord from '@/custombutton/actions/BulkUpdateRecord'
import ConnectedAppWidget from '@/custombutton/actions/ConnectedAppWidget'
export default {
  extends: CustomButton,
  name: 'CustomButtons',
  components: {
    FButton,
    FDropdown,
    FContainer,
    CreateUpdateRecord,
    BulkUpdateRecord,
    ConnectedAppWidget,
  },
  computed: {
    dropdownOptions() {
      let { buttons } = this || {}
      let { dropdown } = buttons || {}

      if (!isEmpty(dropdown)) {
        dropdown = dropdown.map(currDropdown => ({
          ...currDropdown,
          label: currDropdown.name,
          value: currDropdown.id,
        }))
      }
      return dropdown
    },
  },
  watch: {
    customButtons: {
      handler(val) {
        this.$emit('hasCustomButtons', !isEmpty(val))
      },
      immediate: true,
    },
  },
}
</script>
