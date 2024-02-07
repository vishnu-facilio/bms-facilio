<template>
  <el-dialog
    :visible.sync="visibility"
    :before-close="closeDialog"
    :append-to-body="true"
    :title="$t('common.header.stock_tool')"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
  >
    <div class="fc-pm-main-content-H pL30">
      {{ $t('common.header.stock_tool') }}
    </div>
    <div class="p30 pT20 facilio-inventory-web-form-body">
      <el-row class="pB10">
        <el-col :span="24">
          <p class="pB5 stock-form-field">
            {{ $t('common.header.tool_type') }}
          </p>
          <FLookupFieldWrapper
            disabled
            v-model="getTooltype"
            :field="toolTypesLookupData"
            class="fc-input-full-border2 width100"
          ></FLookupFieldWrapper>
        </el-col>
      </el-row>
      <el-row class="pB10">
        <el-col :span="24">
          <p class="pB5 stock-form-field">
            {{ $t('common.products.storeroom') }}
          </p>
          <FLookupFieldWrapper
            v-if="moduleName === 'toolTypes'"
            v-model="storeRoomId"
            :field="storeRoomLookupData"
            class="fc-input-full-border2 width100 "
          ></FLookupFieldWrapper>
          <FLookupFieldWrapper
            v-else-if="moduleName === 'storeRoom' || moduleName === 'tool'"
            disabled
            v-model="getStoreroom"
            :field="storeRoomLookupData"
            class="fc-input-full-border2 width100 "
          ></FLookupFieldWrapper>
        </el-col>
      </el-row>
      <el-row v-if="!isRotating" class="pB10">
        <el-col :span="24">
          <div class="position-relative">
            <div class="stock-form-field mT20  mB10">
              {{ $t('common.products.purchased_tools') }}
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
                    class="setting-table-th setting-th-text uppercase"
                    style="width: 240px;"
                  >
                    {{ $t('common.inventory.purchased_price') }}
                  </th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(purchasedTool, index) in purchasedTools"
                  :key="index"
                  class="visibility-visible-actions"
                >
                  <td class="module-builder-td">
                    <el-input
                      :placeholder="$t('common._common.quantity')"
                      type="number"
                      :min="0"
                      v-model="purchasedTool.quantity"
                      class="fc-input-full-border-select2 duration-input"
                    ></el-input>
                  </td>
                  <td class="module-builder-td">
                    <el-input
                      :placeholder="$t('common.inventory.purchased_price')"
                      type="number"
                      :min="0"
                      v-model="purchasedTool.unitPrice"
                      class="fc-input-full-border-select2 duration-input"
                    ></el-input>
                  </td>
                  <td>
                    <div class="flex-middle">
                      <i
                        @click="addPurchasedTools()"
                        v-if="purchasedTools.length - 1 === index"
                        class="el-icon-circle-plus-outline fc-row-add-icon f20 bold pL5"
                      ></i>
                      <i
                        @click="removePurchasedTools(index)"
                        v-if="purchasedTools.length > 1"
                        class="el-icon-remove-outline fc-row-delete-icon f20 bold pL10"
                      ></i>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-col>
        <!-- <el-col :span="24">
          <p class="pB5 stock-form-field">
            {{ $t('common.header.unit_price') }}
          </p>
          <el-input
            type="number"
            v-model="toolQuantity"
            :min="0"
            class="fc-input-full-border2 width100"
          >
          </el-input>
        </el-col> -->
      </el-row>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        :loading="isButtonLoading"
        class="modal-btn-save"
        @click="saveRecord"
        >{{ $t('common._common._save') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { getFieldOptions } from 'util/picklist'
import { API } from '@facilio/api'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'
export default {
  props: ['visibility', 'record', 'moduleName'],
  components: {
    FLookupFieldWrapper,
  },
  data() {
    return {
      storerooms: {},
      data: {},
      storeRoomId: null,
      toolId: null,
      purchasedTools: [
        {
          quantity: null,
          unitPrice: null,
        },
      ],
      toolTypesLookupData: {
        lookupModule: { name: 'toolTypes' },
        multiple: false,
      },
      storeRoomLookupData: {
        lookupModule: { name: 'storeRoom' },
        multiple: false,
      },
      toolQuantity: null,
      toolRecord: {},
      isButtonLoading: false,
    }
  },
  created() {
    this.loadPickList()
  },
  computed: {
    getTooltype() {
      if (['tool', 'storeRoom'].includes(this.moduleName)) {
        return this.$getProperty(this, 'record.toolType.id')
      } else {
        return this.$getProperty(this, 'record.id')
      }
    },
    tooltypeId() {
      return this.moduleName === 'toolTypes'
        ? this.$getProperty(this, 'record.id')
        : this.$getProperty(this, 'record.toolType.id')
    },
    getStoreroom() {
      return this.$getProperty(this, 'record.storeRoom.id')
    },
    storeId() {
      let { storeRoomId, getStoreroom, moduleName } = this

      return moduleName === 'toolTypes' ? storeRoomId : getStoreroom
    },
    isRotating() {
      if (this.moduleName === 'toolTypes') {
        return this.$getProperty(this, 'record.rotating', null)
      } else if (this.moduleName === 'storeRoom') {
        return this.$getProperty(this, 'record.toolType.isRotating', null)
      } else {
        return false
      }
    },
  },
  methods: {
    addPurchasedTools() {
      let { purchasedTools } = this
      purchasedTools.push(deepCloneObject(Constants.PURCHASEDTOOL_DEFAULTS))
    },
    removePurchasedTools(index) {
      let { purchasedTools } = this
      purchasedTools.splice(index, 1)
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
    async saveRecord() {
      this.isButtonLoading = true
      let recordId = this.$getProperty(this, 'record.id')
      let { purchasedTools, storeId, tooltypeId, isRotating } = this
      let promise
      if (
        (purchasedTools[0].quantity === null ||
          purchasedTools[0].unitPrice === null) &&
        !isRotating
      ) {
        this.$message.error('Please enter Purchased Tools')
      } else {
        this.toolRecord = {
          toolType: {
            id: tooltypeId,
          },
          storeRoom: { id: storeId },
        }
        if (!isRotating) {
          // this.toolRecord['quantity'] = toolQuantity
          this.toolRecord['purchasedTools'] = purchasedTools
        }
        if (this.moduleName === 'tool') {
          promise = await API.updateRecord('tool', {
            id: recordId,
            data: this.toolRecord,
          })
        } else if (['toolTypes', 'storeRoom'].includes(this.moduleName)) {
          if (isEmpty(storeId)) {
            this.$message.error('Please Select the Storeroom')
          } else {
            let status = await this.isAlreadyPresentInStore()
            let { toolId } = this
            if (status) {
              promise = await API.updateRecord('tool', {
                id: toolId,
                data: this.toolRecord,
              })
            } else {
              this.toolRecord['costType'] = 1
              promise = await API.createRecord('tool', {
                data: this.toolRecord,
              })
            }
          }
        }
        if (!isEmpty(promise)) {
          let { error } = (await promise) || {}
          if (error) {
            this.$message.error(
              error.message || this.$t('common._common.error_stock_tools')
            )
          } else {
            this.closeDialog()
            this.$message.success(
              this.$t('common.header.tool_added_successfully')
            )
            this.$emit('saved')
          }
        }
      }
      this.isButtonLoading = false
    },
    async isAlreadyPresentInStore() {
      let params = {
        filters: JSON.stringify({
          toolType: {
            operatorId: 36,
            value: [this.tooltypeId + ''],
          },
          storeRoom: {
            operatorId: 36,
            value: [this.storeId + ''],
          },
        }),
      }
      let { list, error } = await API.fetchAll('tool', params)
      if (error) {
        this.$message.error(
          error || 'Error ocurred while checking the availability'
        )
        return false
      } else {
        if (list.length > 0) {
          this.toolId = list[0].id
        }
        return list.length > 0
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.purchasedTools = [
        {
          quantity: null,
          unitPrice: null,
        },
      ]
      this.storeRoomId = null
      this.toolQuantity = null
      this.toolRecord = {}
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
</style>
