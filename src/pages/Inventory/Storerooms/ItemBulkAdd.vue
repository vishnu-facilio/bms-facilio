<template>
  <div>
    <div v-if="loading" class="mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div class="facilio-inventory-web-form-body" v-else-if="itemBulkAddForm">
      <el-dialog
        :visible.sync="itemBulkAddForm"
        title="ITEM BULK ADD"
        :before-close="cancelForm"
        width="70%"
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
                {{ $t('common._common.quantity') }}
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
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common.tabs.cost') }}
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
                  class="fc-input-full-border-select2 width250px"
                >
                  <el-option
                    v-for="(option, i) in itemTypesList"
                    v-if="!isSelected(option.id, index)"
                    :key="i"
                    :label="option.name"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </td>
              <td>
                <el-input
                  placeholder="Quantity"
                  type="number"
                  v-model="addItem.purchasedItems[0].quantity"
                  class="fc-input-full-border-select2 width110px"
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
                  placeholder="Unit Price"
                  type="number"
                  v-model="addItem.purchasedItems[0].unitcost"
                  class="fc-input-full-border-select2 width110px"
                ></el-input>
              </td>
              <td>
                <currency
                  :value="
                    addItem.purchasedItems[0].quantity > 0 &&
                    addItem.purchasedItems[0].unitcost > 0
                      ? addItem.purchasedItems[0].quantity *
                        addItem.purchasedItems[0].unitcost
                      : '0'
                  "
                  :recordCurrency="
                    getrecordCurrency(addItem.purchasedItems[0].currencyCode)
                  "
                ></currency>
              </td>
              <td>
                <div class="visibility-hide-actions export-dropdown-menu">
                  <span
                    @click="addItemFormDataEntry(formData, index)"
                    title="Add"
                    v-tippy
                  >
                    <img src="~assets/add-icon.svg" class="mR10" />
                  </span>
                  <span
                    v-if="formData.length > 1"
                    @click="removeItemFormDataEntry(formData, index)"
                    title="Remove"
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
            $t('common._common._close')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveItemBulkAddForm()"
            :loading="saving"
            >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { loadNonIndTrackedInventory } from 'pages/Inventory/InventoryUtil'
import { isEmpty } from '@facilio/utils/validation'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import {
  getCalculatedCurrencyValue,
  getCurrencyForCurrencyCode,
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['itemList', 'storeRoom', 'itemBulkAddForm'],
  components: { FNewCurrencyField },
  data() {
    return {
      itemTypesList: null,
      formData: [],
      saving: false,
      loading: true,
      createItems: [],
      updateItems: [],
      itemId: null,
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
      this.$emit('update:itemBulkAddForm', false)
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
    async saveItemBulkAddForm() {
      let self = this
      this.saving = true

      for (let j = this.formData.length - 1; j >= 0; j--) {
        if (
          this.formData[j].purchasedItems[0].hasOwnProperty('quantity') &&
          this.formData[j].purchasedItems[0].quantity === null &&
          this.formData[j].purchasedItems[0].hasOwnProperty('unitcost') &&
          this.formData[j].purchasedItems[0].unitcost === null
        ) {
          this.formData.splice(j, 1)
          continue
        }
        let storeroomId = this.$getProperty(this, 'storeRoom.id')
        let itemTypeId = this.$getProperty(this.formData, `${j}.itemType.id`)

        let itemTypeObj = this.$getProperty(this.formData, `${j}.itemType`)
        if (itemTypeObj && itemTypeId) {
          this.formData[j]['storeRoom'] = { id: storeroomId }

          let status = await this.isAlreadyPresentInStore(
            itemTypeId,
            storeroomId
          )
          if (status) {
            this.formData[j]['id'] = this.itemId
            this.updateItems.push(this.formData[j])
          } else {
            this.formData[j]['costType'] = 1
            this.createItems.push(this.formData[j])
          }
        } else {
          this.formData.splice(j, 1)
          continue
        }
      }
      let promise
      if (this.createItems.length > 0) {
        let url = 'v3/modules/data/bulkCreate'
        let params = {
          data: {
            item: this.createItems,
          },
          moduleName: 'item',
        }
        promise = await API.post(url, params)
      }
      if (this.updateItems.length > 0) {
        let url = 'v3/modules/data/bulkpatch'
        let params = {
          data: {
            item: this.updateItems,
          },
          moduleName: 'item',
        }
        promise = await API.post(url, params)
      }
      let { error } = (await promise) || {}
      if (error) {
        this.$message.error(
          error.message || 'Error Occurred while stocking items'
        )
        self.saving = false
      } else {
        this.$message.success(`Added successfully`)
        self.saving = false
        self.cancelForm()
        this.$emit('refresh')
      }
    },
    async isAlreadyPresentInStore(itemTypeId, storeId) {
      let params = {
        filters: JSON.stringify({
          itemType: {
            operatorId: 36,
            value: [itemTypeId + ''],
          },
          storeRoom: {
            operatorId: 36,
            value: [storeId + ''],
          },
        }),
      }
      let { list, error } = await API.fetchAll('item', params)
      if (error) {
        this.$message.error(
          error || 'Error ocurred while checking the availability'
        )
        return false
      } else {
        if (list.length > 0) {
          this.itemId = list[0].id
        }
        return list.length > 0
      }
    },
    removeItemFormDataEntry(list, index) {
      list.splice(index, 1)
    },
    addItemFormDataEntry(field) {
      field.push({
        itemType: { id: null },
        purchasedItems: [{ quantity: null, unitcost: null }],
      })
    },
    fillFormData() {
      if (this.items.length === 0) {
        let t = {
          itemType: { id: null },
          purchasedItems: [{ quantity: null, unitcost: null }],
        }
        this.formData.push(t)
      }
      for (let i in this.items) {
        if (i === '10') {
          break
        } else {
          let temp = {
            itemType: { id: this.items[i].itemType.id },
            purchasedItems: [{ quantity: null, unitcost: null }],
          }
          this.formData.push(temp)
        }
      }
      this.loading = false
    },
  },
}
</script>

<style scoped>
.item-unit-price-currency {
  padding: 20px 0px 0px;
  width: 20%;
}
</style>
