<template>
  <el-dialog
    :fullscreen="true"
    :append-to-body="true"
    style="z-index: 1999"
    :visible="true"
    custom-class="fc-wo-edit-form-dialog fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-webform-right-dialog new-asset-container"
    :before-close="cancelForm"
  >
    <div class="header pT10 pB10 pL30 pR30 d-flex">
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
    <f-webform
      v-else
      :form.sync="formObj"
      :module="moduleName"
      :isSaving="isSaving"
      :isEdit="isEdit"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
      :modifyFieldPropsHook="modifyFieldPropsHook"
      :moduleDisplayName="moduleDisplayName"
      :isV3Api="isV3Api"
      :isWidgetsSupported="isWidgetsSupported"
      :moduleData="moduleData"
      :moduleDataId="moduleDataId"
      :subFormRecords="subFormRecords"
      @onWidgetChange="onWidgetChange"
      @save="saveRecord"
      @cancel="cancelForm"
    ></f-webform>
  </el-dialog>
</template>

<script>
import InventoryRequestForm from 'src/pages/Inventory/InventoryRequest/InventoryRequestForm.vue'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { mapState } from 'vuex'

export default {
  props: ['dataModuleId', 'woDetails'],
  extends: InventoryRequestForm,

  computed: {
    ...mapState({
      account: state => state.account,
    }),

    moduleDataId() {
      let { dataModuleId } = this
      return !isEmpty(dataModuleId) ? Number(dataModuleId) : null
    },
  },
  methods: {
    afterSerializeHook({ data }) {
      let { inventoryrequestlineitems: lineItems } = data || {}
      let { id } = this.woDetails

      if (!isEmpty(lineItems) && isArray(lineItems)) {
        lineItems = lineItems
          .filter(
            item =>
              !isEmpty(item.quantity) &&
              ((item.inventoryType === 1 && item.itemType?.id) ||
                (item.inventoryType === 2 && item.toolType?.id))
          )
          .map(item => {
            let { inventoryType } = item || {}
            if (inventoryType === 1) {
              return { ...item, toolType: null }
            } else {
              return { ...item, itemType: null }
            }
          })
        this.$setProperty(
          data,
          'inventoryrequestlineitems',
          !isEmpty(lineItems) ? lineItems : null
        )
      }
      data.parentId = id
      return data
    },

    afterSaveHook({ error }) {
      if (!error) {
        this.$emit('onSave')
        this.cancelForm()
      }
    },
    cancelForm() {
      this.$emit('onClose')
    },
    modifyFieldPropsHook(field) {
      let { moduleDataId, woDetails, account } = this
      let { localId, id: woId } = woDetails || {}
      let { user } = account || {}
      let { id: userId } = user || {}
      let fieldValueObj = {
        name: {
          ...field,
          value: `${this.$t(
            'common.inventory.required_inventory_items_for_wo'
          )} ${localId}`,
        },
        requestedBy: { ...field, value: userId, isDisabled: true },
        requestedFor: { ...field, value: userId },
        workorder: { ...field, value: woId, isDisabled: true },
      }

      if (isEmpty(moduleDataId)) {
        let { name } = field || {}
        return fieldValueObj[name] || field
      } else {
        let fieldValueObj = {
          requestedBy: { ...field, isDisabled: true },
          workorder: { ...field, isDisabled: true },
        }
        let { name } = field || {}
        return fieldValueObj[name] || field
      }
    },
  },
}
</script>
