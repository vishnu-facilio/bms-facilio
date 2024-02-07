<template>
  <FContainer v-if="hasTransitionBtns" display="flex" gap="containerLarge">
    <FButton
      :disabled="transitions.featured[0].scheduled"
      :loading="firstBtnLoading"
      appearance="secondary"
      @click="startTransition(transitions.featured[0])"
    >
      {{ transitions.featured[0].name }}</FButton
    >
    <FDropdown
      v-if="hasDropDownBtns"
      :disable="transitions.featured[1].scheduled"
      :name="transitions.featured[1].name"
      :split="true"
      :options="dropdownOptions"
      :buttonProps="{ appearance: 'secondary' }"
      :loading="dropdownLoading"
      @click="initiateDropdownTransition(transitions.featured[1])"
      @dropdown="initiateDropdownTransition"
    />
    <FButton
      v-else-if="hasOnly2Btns"
      :disable="transitions.featured[1].scheduled"
      :loading="lastBtnLoading"
      appearance="secondary"
      @click="startTransition(transitions.featured[1])"
    >
      {{ transitions.featured[1].name }}</FButton
    >

    <TransitionForm
      v-if="canShowForm"
      v-bind="transitionFormProps"
    ></TransitionForm>

    <ConfirmationDialog
      v-if="showConfirmationDialog"
      :transition="selectedTransition"
      :confirmations="confirmationDialogs"
      :onConfirm="continueTransition"
      :onCancel="cancelTransition"
    ></ConfirmationDialog>
  </FContainer>
</template>

<script>
import { FContainer, FButton, FDropdown } from '@facilio/design-system'
import TransitionButtons from '@/stateflow/TransitionButtons.vue'

import TransitionForm from '@/stateflow/TransitionForm'
import ConfirmationDialog from '@/stateflow/TransitionConfirmations'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: TransitionButtons,
  name: 'StateflowButtons',
  components: {
    FContainer,
    FButton,
    FDropdown,
    TransitionForm,
    ConfirmationDialog,
  },
  computed: {
    dropdownOptions() {
      let { transitions } = this || {}
      let { dropdown } = transitions || {}
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
    hasTransitionBtns: {
      handler(val) {
        this.$emit('hasTransitionBtns', val)
      },
      immediate: true,
    },
  },
}
</script>
