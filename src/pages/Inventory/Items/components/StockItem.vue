<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :before-close="closeDialog"
      :append-to-body="true"
      :title="$t('common.header.stock_item')"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
    >
      <div class="fc-pm-main-content-H pL30">
        {{ $t('common.header.stock_item') }}
      </div>
      <div class="p30 pT20 facilio-inventory-web-form-body">
        <el-row class="pB10">
          <el-col :span="24">
            <p class="stock-form-field pB5">
              {{ $t('common.header.item_type') }}
            </p>
            <FLookupFieldWrapper
              disabled
              v-model="getItemtype"
              :field="itemTypesLookupData"
              class="fc-input-full-border2 width100"
            ></FLookupFieldWrapper>
          </el-col>
        </el-row>
        <el-row class="pB10">
          <el-col :span="24">
            <p class="stock-form-field pB5">
              {{ $t('common.products.storeroom') }}
            </p>
            <FLookupFieldWrapper
              v-if="moduleName === 'itemTypes'"
              v-model="storeRoomId"
              :field="storeRoomLookupData"
              class="fc-input-full-border2 width100 "
            ></FLookupFieldWrapper>
            <FLookupFieldWrapper
              v-else-if="moduleName === 'storeRoom' || moduleName === 'item'"
              disabled
              v-model="getStoreroom"
              :field="storeRoomLookupData"
              class="fc-input-full-border2 width100 "
            ></FLookupFieldWrapper>
          </el-col>
        </el-row>
        <el-row v-if="isRotating">
          <el-col :span="24">
            <p class="cost-form-field pB5">
              {{ $t('common.inventory.issuance_cost') }}
            </p>
            <el-input
              type="number"
              v-model="issuanceCost"
              :min="0"
              class="fc-input-full-border2 width100 "
            >
            </el-input>
          </el-col>
        </el-row>
        <el-row v-if="!isRotating">
          <el-col :span="24">
            <div class="position-relative">
              <div class="stock-form-field mT20  mB10">
                {{ $t('common.products.purchased_items') }}
              </div>
              <table
                class="setting-list-view-table fc-inventory-item-table store-table"
              >
                <thead>
                  <tr>
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
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(purchasedItem, index) in purchasedItems"
                    :key="index"
                    class="visibility-visible-actions"
                  >
                    <td class="module-builder-td">
                      <el-input
                        :placeholder="$t('common._common.quantity')"
                        type="number"
                        :min="0"
                        v-model="purchasedItem.quantity"
                        class="fc-input-full-border-select2 duration-input"
                      ></el-input>
                    </td>
                    <td
                      v-if="checkForMultiCurrency('unitcost', metaFieldTypeMap)"
                      class="item-unit-price-currency"
                    >
                      <FNewCurrencyField
                        :key="purchasedItem.currencyCode"
                        v-model="purchasedItem.unitcost"
                        :isSubform="true"
                        :moduleData="purchasedItem"
                        @setCurrencyCodeInSubform="
                          (code, rate) =>
                            setCurrencyCodeInSubform(code, rate, index)
                        "
                        @calculateExchangeRate="
                          rateObj => calculateExchangeRate(rateObj, index)
                        "
                      ></FNewCurrencyField>
                    </td>
                    <td v-else class="module-builder-td">
                      <el-input
                        :placeholder="$t('common.header.unit_price')"
                        type="number"
                        :min="0"
                        v-model="purchasedItem.unitcost"
                        class="fc-input-full-border-select2 duration-input"
                      />
                    </td>
                    <td>
                      <div class="flex-middle">
                        <i
                          @click="addPurchasedItems()"
                          v-if="purchasedItems.length - 1 === index"
                          class="el-icon-circle-plus-outline fc-row-add-icon f20 bold pL5"
                        ></i>
                        <i
                          @click="removePurchasedItems(index)"
                          v-if="purchasedItems.length > 1"
                          class="el-icon-remove-outline fc-row-delete-icon f20 bold pL10"
                        ></i>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          :loading="saving"
          @click="saveRecord"
          >{{ $t('common._common._save') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { getFieldOptions } from 'util/picklist'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEmpty } from '@facilio/utils/validation'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import {
  getCalculatedCurrencyValue,
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['visibility', 'record', 'moduleName'],
  components: {
    FLookupFieldWrapper,
    FNewCurrencyField,
  },
  data() {
    return {
      saving: false,
      storerooms: {},
      issuanceCost: null,
      data: {},
      purchasedItems: [
        {
          quantity: null,
          unitcost: null,
        },
      ],
      storeRoomId: null,
      itemId: null,
      itemsList: [],
      itemTypesLookupData: {
        lookupModule: { name: 'itemTypes' },
        multiple: false,
      },
      storeRoomLookupData: {
        lookupModule: { name: 'storeRoom' },
        multiple: false,
      },
      metaFields: [],
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  async created() {
    this.loadPickList()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('purchasedItem')
  },
  computed: {
    getItemtype() {
      if (['item', 'storeRoom'].includes(this.moduleName)) {
        return this.$getProperty(this, 'record.itemType.id')
      } else {
        return this.$getProperty(this, 'record.id')
      }
    },
    itemtypeId() {
      return this.moduleName === 'itemTypes'
        ? this.$getProperty(this, 'record.id')
        : this.$getProperty(this, 'record.itemType.id')
    },
    getStoreroom() {
      return this.$getProperty(this, 'record.storeRoom.id')
    },
    storeId() {
      let { storeRoomId, getStoreroom, moduleName } = this

      return moduleName === 'itemTypes' ? storeRoomId : getStoreroom
    },
    isRotating() {
      if (this.moduleName === 'itemTypes') {
        return this.$getProperty(this, 'record.rotating', null)
      } else if (this.moduleName === 'storeRoom') {
        return this.$getProperty(this, 'record.itemType.isRotating', null)
      } else {
        return false
      }
    },
  },
  methods: {
    addPurchasedItems() {
      let { purchasedItems } = this
      purchasedItems.push(deepCloneObject(Constants.PURCHASEDITEM_DEFAULTS))
    },
    removePurchasedItems(index) {
      let { purchasedItems } = this
      purchasedItems.splice(index, 1)
    },
    async loadPickList() {
      this.storerooms = {}
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'storeRoom', skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.storerooms = options
      }
    },
    setCurrencyCodeInSubform(currencyCode, exchangeRate, index) {
      let { purchasedItems } = this
      let purchasedItem = purchasedItems[index]
      purchasedItems.splice(index, 1, {
        ...purchasedItem,
        currencyCode,
        exchangeRate,
      })
    },
    calculateExchangeRate(rateObj, index) {
      let { purchasedItems } = this
      let purchasedItem = purchasedItems[index]
      purchasedItem.unitcost = getCalculatedCurrencyValue(
        rateObj,
        purchasedItem.unitcost
      )
      purchasedItems.splice(index, 1, purchasedItem)
    },
    async saveRecord() {
      this.saving = true
      let recordId = this.$getProperty(this, 'record.id')
      let {
        purchasedItems,
        record,
        storeId,
        itemtypeId,
        isRotating,
        issuanceCost,
      } = this

      let { currencyCode, exchangeRate } = record || {}
      if (!isEmpty(purchasedItems)) {
        let lastPurchasedLineItem = purchasedItems[purchasedItems.length - 1]
        currencyCode = lastPurchasedLineItem.currencyCode
        exchangeRate = lastPurchasedLineItem.exchangeRate
      }

      let promise
      if (
        (purchasedItems[0].quantity === null ||
          purchasedItems[0].unitcost === null) &&
        !isRotating
      ) {
        this.$message.error('Please enter Purchased Items')
      } else {
        if (this.moduleName === 'item') {
          record.purchasedItems = purchasedItems
          record = { ...record, currencyCode, exchangeRate }

          promise = await API.updateRecord('item', {
            id: recordId,
            data: record,
          })
        } else if (['itemTypes', 'storeRoom'].includes(this.moduleName)) {
          if (isEmpty(storeId)) {
            this.$message.error('Please Select the Storeroom')
          } else {
            let status = await this.isAlreadyPresentInStore()
            if (status && isRotating) {
              this.$message.error('Item already present in storeroom')
              this.saving = false
              return
            }
            let { itemId } = this
            let itemRecord = {
              itemType: { id: itemtypeId },
              storeRoom: { id: storeId },
            }
            itemRecord = { ...itemRecord, currencyCode, exchangeRate }
            if (!isRotating) {
              itemRecord['purchasedItems'] = purchasedItems
            }
            if (!isEmpty(issuanceCost) && isRotating) {
              itemRecord['issuanceCost'] = issuanceCost
            }
            if (status) {
              promise = await API.updateRecord('item', {
                id: itemId,
                data: itemRecord,
              })
            } else {
              itemRecord['costType'] = 1
              promise = await API.createRecord('item', {
                data: itemRecord,
              })
            }
          }
        }
        if (!isEmpty(promise)) {
          let { error } = (await promise) || {}
          if (error) {
            this.$message.error(
              error.message || this.$t('common._common.error_stock_items')
            )
          } else {
            this.closeDialog()
            this.$message.success(
              this.$t('common.header.item_added_successfully')
            )
            this.$emit('saved')
          }
        }
      }
      this.saving = false
    },
    async isAlreadyPresentInStore() {
      let params = {
        filters: JSON.stringify({
          itemType: {
            operatorId: 36,
            value: [this.itemtypeId + ''],
          },
          storeRoom: {
            operatorId: 36,
            value: [this.storeId + ''],
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
    closeDialog() {
      this.$emit('update:visibility', false)
      this.purchasedItems = [
        {
          quantity: null,
          unitcost: null,
        },
      ]
      this.storeRoomId = null
    },
  },
}
</script>
<style scoped>
.stock-form-field {
  color: #d54141;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.5px;
  margin: 0;
  padding-bottom: 10px;
}
.cost-form-field {
  color: #385571;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.5px;
  margin: 0;
  padding-bottom: 10px;
}

.item-unit-price-currency {
  padding: 20px 0px 0px;
  width: 32%;
}
</style>
