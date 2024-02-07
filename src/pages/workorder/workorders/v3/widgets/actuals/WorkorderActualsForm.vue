<template>
  <PopupLineItemForm
    :key="`popup-form-${moduleName}`"
    :config="formConfig"
    :moduleName="moduleName"
    :moduleDisplayName="moduleDisplayName"
    :dataId="recordId"
    :dataObj="record"
    :additionalParams="getAdditionalParams"
    @onSave="refreshList"
    @onClose="closeForm"
  ></PopupLineItemForm>
</template>
<script>
import PopupLineItemForm from 'src/components/page/widget/common/line-items/PopupLineItemForm.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'record',
    'recordId',
    'moduleName',
    'moduleDisplayName',
    'additionalParams',
    'config',
    'recordIndex',
  ],
  components: {
    PopupLineItemForm,
  },
  computed: {
    getAdditionalParams() {
      let { additionalParams, record } = this || {}
      let { inventoryReservation, workorder } = record || {}
      if (!isEmpty(inventoryReservation)) {
        return {
          ...additionalParams,
          inventoryReservation: inventoryReservation,
          workorder: workorder,
        }
      }
      return { ...additionalParams, workorder: workorder }
    },
    isEdit() {
      return !isEmpty(this.recordId)
    },
    formConfig() {
      let { currentRecordId, config, modifyFieldPropsHook } = this
      let {
        formType,
        formTitle,
        onBlurHook = () => {},
        onWidgetChange = () => {},
      } = config || {}

      formType = isEmpty(currentRecordId) ? 'POP_UP_FORM' : formType
      return {
        formType,
        formTitle,
        modifyFieldPropsHook,
        onBlurHook,
        onWidgetChange,
      }
    },
  },
  methods: {
    refreshList() {
      this.$emit('onSave')
    },
    closeForm() {
      this.$emit('onClose')
    },
    modifyFieldPropsHook(field) {
      let { isEdit, recordIndex } = this || {}
      let { name } = field || {}
      if (['storeRoom', 'workorder'].includes(name)) {
        return { ...field, hideField: true }
      }
      if (isEdit || !isEmpty(recordIndex)) {
        if (['item', 'tool', 'service'].includes(name)) {
          return { ...field, isDisabled: true }
        }
      }
    },
  },
}
</script>
