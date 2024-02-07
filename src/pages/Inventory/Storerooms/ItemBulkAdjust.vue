<template>
  <div>
    <div v-if="loading" class="mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div class="facilio-inventory-web-form-body" v-else-if="itemBulkAdjustForm">
      <el-dialog
        :visible.sync="itemBulkAdjustForm"
        :title="$t('common.products.item_bulk_adjust')"
        :before-close="cancelForm"
        width="75%"
        :key="storeRoom.id"
        custom-class="fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
      >
        <table class="setting-list-view-table store-table">
          <thead>
            <tr>
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common.header.item_type') }}
              </th>
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common.header._current_quantity') }}
              </th>
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common.header._new_quantity') }}
              </th>
              <th
                :class="[
                  !checkForMultiCurrency('unitcost', metaFieldTypeMap) &&
                    'setting-table-th',
                  'setting-th-text uppercase',
                ]"
                style="width: 240px;"
              >
                {{ $t('common.header.unit_price') }}
              </th>

              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(addItem, index) in formData"
              :key="index"
              class="visibility-visible-actions"
            >
              <td class="module-builder-td">
                <el-select
                  filterable
                  clearable
                  v-model="addItem.itemType.id"
                  class="fc-input-full-border-select2 width200px"
                  @change="
                    addItemFormData(formData, addItem.itemType.id, index)
                  "
                >
                  <el-option
                    v-for="(option, i) in items"
                    v-if="!isSelected(option.itemType.id, index)"
                    :key="i"
                    :label="option.itemType.name"
                    :value="option.itemType.id"
                  ></el-option>
                </el-select>
              </td>
              <td>
                {{ addItem.quantity }}
              </td>
              <td>
                <el-input
                  :placeholder="$t('common.header._new_quantity')"
                  type="number"
                  v-model="addItem.purchasedItems[0].quantity"
                  class="fc-input-full-border-select2 width130px"
                ></el-input>
              </td>

              <td
                v-if="checkForMultiCurrency('unitcost', metaFieldTypeMap)"
                class="item-unit-price-currency"
              >
                <FNewCurrencyField
                  :key="addItem.purchasedItems[0].currencyCode"
                  v-model="addItem.purchasedItems[0].unitcost"
                  :isSubform="true"
                  :disabled="
                    addItem.purchasedItems[0].quantity <= addItem.quantity
                  "
                  :moduleData="addItem.purchasedItems[0]"
                  @setCurrencyCodeInSubform="
                    (code, rate) => setCurrencyCodeInSubform(code, rate, index)
                  "
                  @calculateExchangeRate="
                    rateObj => calculateExchangeRate(rateObj, index)
                  "
                ></FNewCurrencyField>
              </td>
              <td v-else>
                <el-input
                  :placeholder="$t('common.header.unit_price')"
                  type="number"
                  :disabled="
                    addItem.purchasedItems[0].quantity <= addItem.quantity
                  "
                  v-model="addItem.purchasedItems[0].unitcost"
                  class="fc-input-full-border-select2 width110px"
                ></el-input>
              </td>
              <td>
                <div class="visibility-hide-actions export-dropdown-menu">
                  <span
                    @click="addItemFormDataEntry(formData, index)"
                    :title="$t('asset.readings.add')"
                    v-tippy
                  >
                    <img src="~assets/add-icon.svg" class="mR10" />
                  </span>
                  <span
                    v-if="formData.length > 1"
                    @click="removeItemFormDataEntry(formData, index)"
                    :title="$t('agent.agent.remove')"
                    v-tippy
                  >
                    <img src="~assets/remove-icon.svg" />
                  </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveItemBulkAdjustForm()"
            :loading="saving"
            >{{
              saving
                ? $t('common._common.submitting')
                : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { loadNonIndTrackedInventory } from 'pages/Inventory/InventoryUtil'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import {
  getCalculatedCurrencyValue,
  getCurrencyForCurrencyCode,
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['itemList', 'storeRoom', 'itemBulkAdjustForm'],
  components: { FNewCurrencyField },
  data() {
    return {
      itemTypesList: null,
      formData: [],
      saving: false,
      itemTypes: null,
      loading: true,
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  async mounted() {
    this.loadNonRotatingItemTypes()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('purchasedItem')
  },
  computed: {
    items() {
      return this.itemList.filter(
        i => i.itemType.isRotating === false || !i.itemType.isRotating
      )
    },
    filterdItemTypeList() {
      return this.itemTypesList.filter((item, index) =>
        !this.isSelected(item.id, index) ? true : false
      )
    },
  },
  methods: {
    async loadNonRotatingItemTypes() {
      this.loading = true
      let itemTypes = await loadNonIndTrackedInventory('itemTypes')
      if (isEmpty(itemTypes)) {
        this.loading = false
      } else {
        this.itemTypesList = itemTypes
        this.fillFormData()
      }
    },
    isSelected(id, index) {
      for (let i in this.formData) {
        if (this.formData[i].itemType.id === id) {
          if (parseInt(i) === index) {
            return false
          }
          return true
        }
      }
      return false
    },
    cancelForm() {
      this.$emit('update:itemBulkAdjustForm', false)
    },
    async saveItemBulkAdjustForm() {
      this.saving = true
      let itemTransaction = []
      let filteredFormData = this.formData.filter(data => {
        let quantity = this.$getProperty(data, 'purchasedItems.0.quantity')
        return !isEmpty(quantity)
      })
      if (filteredFormData.length > 0) {
        for (let adjitem in filteredFormData) {
          let obj = {}
          let item = {}
          let transactionType = 1
          let transactionState = null
          let quantity = this.$getProperty(
            filteredFormData[adjitem],
            'purchasedItems.0.quantity'
          )

          item['id'] = this.$getProperty(filteredFormData[adjitem], 'id')
          let newQuantity = this.$getProperty(
            filteredFormData[adjitem],
            'quantity'
          )
          if (quantity === null) {
            this.$message.error('Enter the New Quantity')
            this.saving = false
            break
          }
          if (quantity > newQuantity) {
            let purchased_item = {}
            purchased_item['unitcost'] = this.$getProperty(
              filteredFormData[adjitem],
              'purchasedItems.0.unitcost'
            )
            purchased_item['currencyCode'] = this.$getProperty(
              filteredFormData[adjitem],
              'purchasedItems.0.currencyCode'
            )
            purchased_item['exchangeRate'] = this.$getProperty(
              filteredFormData[adjitem],
              'purchasedItems.0.exchangeRate'
            )

            purchased_item['quantity'] = quantity - newQuantity
            transactionState = 7
            obj['item'] = item
            obj['purchasedItem'] = purchased_item
            obj['transactionType'] = transactionType
            obj['transactionState'] = transactionState
            obj['quantity'] = quantity - newQuantity
            itemTransaction.push(obj)
          } else if (quantity < newQuantity) {
            transactionState = 8
            obj['item'] = item
            obj['transactionType'] = transactionType
            obj['transactionState'] = transactionState
            obj['quantity'] = newQuantity - quantity
            itemTransaction.push(obj)
          } else {
            this.$message.error('Unable to Adjust with same Quantity')
            this.saving = false
            break
          }
        }
        if (this.saving) {
          let url = 'v3/modules/data/bulkCreate'
          let params = {
            data: {
              itemTransactions: itemTransaction,
            },
            moduleName: 'itemTransactions',
            params: {
              adjustQuantity: true,
            },
          }
          let { error } = await API.post(url, params)
          if (error) {
            this.$message.error(
              error.message || this.$t('common.wo_report.unable_to_update')
            )
            this.saving = false
          } else {
            this.$message.success(
              this.$t('common.header.item_adjusted_successfully')
            )
            this.saving = false
            this.cancelForm()
            this.$emit('refresh')
          }
        }
      } else {
        this.$message.error('Enter the New Quantity')
        this.saving = false
      }
    },
    removeItemFormDataEntry(list, index) {
      list.splice(index, 1)
    },
    addItemFormDataEntry(field) {
      field.push({
        itemType: { id: null },
        quantity: null,
        id: null,
        purchasedItems: [{ quantity: null, unitcost: null }],
      })
    },
    addItemFormData(field, id, index) {
      let item = this.items.findIndex(element => element.itemType.id === id)
      let qua = 0
      if (this.items[item].quantity >= 0) {
        qua = this.items[item].quantity
      } else {
        qua = 0
      }
      let obj = {
        itemType: { id: id },
        quantity: qua,
        id: this.items[item].id,
        purchasedItems: [{ quantity: null, unitcost: null }],
      }
      field.splice(index, 1, obj)
    },
    fillFormData() {
      if (this.items.length === 0) {
        let t = {
          itemType: { id: null },
          quantity: null,
          id: null,
          purchasedItems: [{ quantity: null, unitcost: null }],
        }
        this.formData.push(t)
      }
      for (let i in this.items) {
        if (i === '10') {
          break
        } else {
          let qua = 0
          if (this.items[i].quantity >= 0) {
            qua = this.items[i].quantity
          } else {
            qua = 0
          }
          let temp = {
            itemType: { id: this.items[i].itemType.id },
            quantity: qua,
            id: this.items[i].id,
            purchasedItems: [{ quantity: null, unitcost: null }],
          }
          this.formData.push(temp)
        }
      }
      this.loading = false
    },
    updateItem(addItem) {
      alert(addItem.quantity)
    },
    setCurrencyCodeInSubform(currencyCode, exchangeRate, index) {
      let { purchasedItems } = this.formData[index] || {}
      let purchasedItem = purchasedItems[0]
      this.formData[index].purchasedItems = [
        {
          ...purchasedItem,
          currencyCode,
          exchangeRate,
        },
      ]
    },
    getrecordCurrency(currencyCode) {
      return getCurrencyForCurrencyCode(currencyCode)
    },
    calculateExchangeRate(rateObj, index) {
      let { purchasedItems } = this.formData[index] || {}
      let purchasedItem = purchasedItems[0]
      purchasedItem.unitcost = getCalculatedCurrencyValue(
        rateObj,
        purchasedItem.unitcost
      )
      this.$setProperty(this.formData, `${index}.purchasedItems.0`, {
        ...purchasedItem,
      })
    },
  },
}
</script>
<style scoped>
.item-unit-price-currency {
  padding: 20px 0px 0px;
  width: 32%;
}
</style>
