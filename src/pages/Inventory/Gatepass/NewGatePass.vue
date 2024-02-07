<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible="visibility"
        :fullscreen="true"
        :before-close="cancel"
        title="Generate Gate Pass"
        custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999"
      >
        <div class="fc-pm-main-content-H">
          Generate Gate Pass
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div class="new-body-modal">
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Issue Date
              </p>
              <el-date-picker
                v-model="generateGatePassData.issuedTime"
                value-format="timestamp"
                type="date"
                placeholder="Select date"
                class="width100"
              ></el-date-picker>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Gate Pass Type
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="generateGatePassData.gatePassType"
                  filterable
                  placeholder="Gate Pass Type"
                  class="width100"
                >
                  <el-option label="Inward" :value="1" :key="1"></el-option>
                  <el-option label="Outward" :value="2" :key="2"></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Issued To
              </p>
              <el-input
                placeholder="Issued To"
                v-model="generateGatePassData.issuedTo"
                class="width100"
              ></el-input>
              <!-- <span class="position-absolute right60">Name</span> -->
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Issued To Phone Number
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-input
                  placeholder="Issued To Phone Number"
                  type="number"
                  v-model="generateGatePassData.issuedToPhoneNumber"
                  class="width100"
                ></el-input>
                <!-- <span class="position-absolute right60">Phone Number</span> -->
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Issued By
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="generateGatePassData.issuedBy.ouid"
                  filterable
                  placeholder="Issued By"
                  class="width100"
                >
                  <el-option
                    v-for="user in users"
                    :key="user.id"
                    :label="user.name"
                    :value="user.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Vehicle Number
              </p>
              <el-input
                placeholder="Vehicle Number"
                v-model="generateGatePassData.vehicleNo"
                class="width100"
              ></el-input>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12" v-if="generateGatePassData.gatePassType === 2">
              <p class="fc-input-label-txt">
                From Storeroom
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="generateGatePassData.fromStoreRoom.id"
                  @change="fromStoreRoomChangeActions()"
                  filterable
                  clearable
                  placeholder="From Storeroom"
                  class="width100"
                >
                  <el-option
                    v-for="storeRoom in storeRooms"
                    :key="storeRoom.id"
                    :label="storeRoom.name"
                    :value="storeRoom.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="12" v-if="generateGatePassData.gatePassType === 1">
              <p class="fc-input-label-txt">
                To Storeroom
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="generateGatePassData.toStoreRoom.id"
                  @change="toStoreRoomChangeActions()"
                  filterable
                  clearable
                  placeholder="To Storeroom"
                  class="width100"
                >
                  <el-option
                    v-for="storeRoom in storeRooms"
                    :key="storeRoom.id"
                    :label="storeRoom.name"
                    :value="storeRoom.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="12" v-if="generateGatePassData.gatePassType === 1">
              <p class="fc-input-label-txt">
                Purchase Order
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="generateGatePassData.parentPoId.id"
                  filterable
                  clearable
                  @change="purchaseOrderChangeActions()"
                  placeholder="Purchase Order"
                  class="width100"
                >
                  <el-option
                    v-for="po in purchaseOrders"
                    :key="po.id"
                    :label="po.name"
                    :value="String(po.id)"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Is Returnable
              </p>
              <el-checkbox
                v-model="generateGatePassData.isReturnable"
              ></el-checkbox>
            </el-col>
            <el-col :span="12" v-if="generateGatePassData.isReturnable">
              <p class="fc-input-label-txt">
                Return Due Date
              </p>
              <el-date-picker
                v-model="generateGatePassData.returnTime"
                type="date"
                value-format="timestamp"
                placeholder="Select date"
              ></el-date-picker>
            </el-col>
          </el-row>
          <div class="gate-pass-lineitem-heading">LINE ITEMS</div>
          <table class="setting-list-view-table store-table">
            <thead class="setup-dialog-thead">
              <tr>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 240px;"
                >
                  TYPE
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 240px;"
                >
                  NAME
                </th>
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 240px;"
                >
                  QUANTITY
                </th>
                <th></th>
              </tr>
            </thead>
            <tbody v-if="poLoading">
              <tr>
                <td colspan="100%">
                  <div class="in-no-data">
                    <spinner :show="true" size="80"></spinner>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="generateGatePassData.gatePassType === 1">
              <tr
                v-for="(field,
                lineindex) in generateGatePassData.toStoreLineItems"
                :key="lineindex"
                class="visibility-visible-actions"
              >
                <td class="module-builder-td">
                  <el-select
                    v-model="
                      generateGatePassData.toStoreLineItems[lineindex]
                        .inventoryType
                    "
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option label="Item" key="1" :value="1"></el-option>
                    <el-option label="Tool" key="2" :value="2"></el-option>
                  </el-select>
                </td>
                <td class="module-builder-td">
                  <el-select
                    filterable
                    clearable
                    v-model="
                      generateGatePassData.toStoreLineItems[lineindex].itemType
                        .id
                    "
                    class="fc-input-full-border-select2 width150px"
                    v-if="
                      parseInt(
                        generateGatePassData.toStoreLineItems[lineindex]
                          .inventoryType
                      ) === 1
                    "
                  >
                    <el-option
                      filterable
                      v-for="(item, index) in itemTypes"
                      :key="index"
                      :label="item.name"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                  <el-select
                    filterable
                    clearable
                    v-model="
                      generateGatePassData.toStoreLineItems[lineindex].toolType
                        .id
                    "
                    class="fc-input-full-border-select2 width150px"
                    v-if="
                      parseInt(
                        generateGatePassData.toStoreLineItems[lineindex]
                          .inventoryType
                      ) === 2
                    "
                  >
                    <el-option
                      v-for="(tool, index) in toolTypes"
                      :key="index"
                      :label="tool.name"
                      :value="tool.id"
                    ></el-option>
                  </el-select>
                </td>
                <td class="module-builder-td">
                  <el-input
                    placeholder="Quantity"
                    type="number"
                    :min="0"
                    v-model="
                      generateGatePassData.toStoreLineItems[lineindex].quantity
                    "
                    class="fc-input-full-border-select2 width110px"
                  ></el-input>
                </td>
                <td>
                  <div class="visibility-hide-actions export-dropdown-menu">
                    <span
                      @click="
                        addItemEntry(generateGatePassData.toStoreLineItems)
                      "
                      title="Add"
                      v-tippy
                    >
                      <img src="~assets/add-icon.svg" class="mR10 mT10" />
                    </span>
                    <span
                      v-if="generateGatePassData.toStoreLineItems.length > 1"
                      @click="
                        removeItemEntry(
                          generateGatePassData.toStoreLineItems,
                          lineindex
                        )
                      "
                      title="Remove"
                      v-tippy
                    >
                      <img src="~assets/remove-icon.svg" class="mT10" />
                    </span>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="generateGatePassData.gatePassType === 2">
              <tr
                v-for="(lineItem,
                index) in generateGatePassData.fromStoreLineItems"
                :key="index"
                class="visibility-visible-actions"
              >
                <td class="module-builder-td">
                  <el-select
                    v-model="lineItem.inventoryType"
                    class="fc-input-full-border-select2 width100px"
                  >
                    <el-option label="Item" key="1" :value="1"></el-option>
                    <el-option label="Tool" key="2" :value="2"></el-option>
                  </el-select>
                </td>
                <td class="module-builder-td">
                  <div v-if="parseInt(lineItem.inventoryType) === 1">
                    <el-select
                      filterable
                      clearable
                      v-model="lineItem.item.id"
                      @change="
                        ;(lineItem.quantity = null), (lineItem.assetIds = [])
                      "
                      class="fc-input-full-border-select2 width150px"
                    >
                      <el-option
                        v-for="(item, index) in consumableItemList"
                        :key="index"
                        :label="item.itemType.name"
                        :value="item.id"
                      >
                      </el-option>
                    </el-select>
                  </div>
                  <div v-if="parseInt(lineItem.inventoryType) === 2">
                    <el-select
                      filterable
                      clearable
                      v-model="lineItem.tool.id"
                      @change="
                        ;(lineItem.quantity = null), (lineItem.assetIds = [])
                      "
                      class="fc-input-full-border-select2 width150px"
                    >
                      <el-option
                        v-for="(tool, index) in toolList"
                        :key="index"
                        :label="tool.toolType.name"
                        :value="tool.id"
                      >
                      </el-option>
                    </el-select>
                  </div>
                </td>
                <td>
                  <el-input
                    placeholder="Quantity"
                    :min="0"
                    type="number"
                    :readonly="checkForIsRotatingCase(lineItem)"
                    v-model="lineItem.quantity"
                    class="fc-input-full-border-select2 width100px duration-input"
                  >
                    <i
                      @click="lineItem.visibility = true"
                      v-if="checkForIsRotatingCase(lineItem)"
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                    ></i>
                  </el-input>
                  <rotating-asset-chooser
                    v-if="
                      checkForIsRotatingCase(lineItem) && lineItem.visibility
                    "
                    :visibility.sync="lineItem.visibility"
                    :item="
                      lineItem.inventoryType === 1 ? lineItem.item.id : null
                    "
                    :tool="
                      lineItem.inventoryType === 2 ? lineItem.tool.id : null
                    "
                    :assetIds="lineItem.assetIds"
                    :isGatePass="true"
                    @save="data => storeAssetData(lineItem, data)"
                  ></rotating-asset-chooser>
                </td>
                <td>
                  <div class="visibility-hide-actions export-dropdown-menu">
                    <span
                      @click="
                        addItemEntry2(generateGatePassData.fromStoreLineItems)
                      "
                      title="Add"
                      v-tippy
                    >
                      <img src="~assets/add-icon.svg" class="mR10 mT10" />
                    </span>
                    <span
                      v-if="generateGatePassData.fromStoreLineItems.length > 1"
                      @click="
                        removeItemEntry(
                          generateGatePassData.fromStoreLineItems,
                          index
                        )
                      "
                      title="Remove"
                      v-tippy
                    >
                      <img src="~assets/remove-icon.svg" class="mT10" />
                    </span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancel()"
            >CANCEL</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="generateGatePass()"
            :loading="gatePassSaveLoading"
            >{{ gatePassSaveLoading ? 'SAVING...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import RotatingAssetChooser from 'pages/Inventory/component/RotatingAssetChooser'
import {
  getFilteredItemList,
  getFilteredToolList,
  getFilteredStoreRoomList,
} from 'pages/Inventory/InventoryUtil'

export default {
  props: ['visibility', 'gatePassData'],
  components: {
    RotatingAssetChooser,
  },
  data() {
    return {
      generateGatePassData: {
        issuedTime: null,
        issuedTo: null,
        issuedToPhoneNumber: null,
        issuedBy: { ouid: null },
        isReturnable: false,
        returnTime: null,
        vehicleNo: null,
        gatePassType: 2,
        fromStoreRoom: { id: null },
        toStoreRoom: { id: null },
        parentPoId: { id: null },
        toStoreLineItems: [
          {
            inventoryType: 1,
            itemType: { id: null },
            toolType: { id: null },
            quantity: null,
          },
        ],
        fromStoreLineItems: [
          {
            inventoryType: 1,
            item: { id: null },
            tool: { id: null },
            quantity: null,
            visibility: false,
            assetIds: null,
          },
        ],
      },
      itemTypes: [],
      toolTypes: [],
      gatePassSaveLoading: false,
      poLoading: false,
      itemList: [],
      toolList: [],
      storeRooms: [],
      purchaseOrders: [],
    }
  },
  computed: {
    users() {
      return this.$store.state.users
    },
    consumableItemList() {
      return this.itemList.filter(item =>
        this.$getProperty(item, 'itemType.isConsumable')
      )
    },
  },
  async mounted() {
    this.generateGatePassData.issuedBy.ouid = this.$store.state.account.user.id
    let filters = {
      isGatePassRequired: { operatorId: 15, value: ['true'] },
    }
    // TODO add filters for purchase order list and Storeroom List
    this.storeRooms = await getFilteredStoreRoomList(filters)
    this.generateGatePassData.issuedTime = Date.now()
    this.loadLineTypes(1)
    this.loadLineTypes(2)
  },
  methods: {
    generateGatePass() {
      let self = this
      this.gatePassSaveLoading = true
      if (!this.isValid(this.generateGatePassData.issuedBy.ouid)) {
        delete this.generateGatePassData.issuedBy
      }
      if (!this.isValid(this.generateGatePassData.issuedTo)) {
        this.$message.error('Enter Issued To')
        self.gatePassSaveLoading = false
        return
      }
      if (isNaN(this.generateGatePassData.issuedToPhoneNumber)) {
        this.$message.error('Enter Issued to Phone Number')
        self.gatePassSaveLoading = false
        return
      }
      if (this.generateGatePassData.gatePassType === 1) {
        delete this.generateGatePassData.fromStoreRoom
        for (
          let i = this.generateGatePassData.toStoreLineItems.length - 1;
          i >= 0;
          i--
        ) {
          if (
            this.generateGatePassData.toStoreLineItems[i].inventoryType === 1
          ) {
            delete this.generateGatePassData.toStoreLineItems[i].toolType
            if (!this.generateGatePassData.toStoreLineItems[i].itemType.id) {
              this.generateGatePassData.toStoreLineItems.splice(i, 1)
              continue
            }
          } else if (
            this.generateGatePassData.toStoreLineItems[i].inventoryType === 2
          ) {
            delete this.generateGatePassData.toStoreLineItems[i].itemType
            if (!this.generateGatePassData.toStoreLineItems[i].toolType.id) {
              this.generateGatePassData.toStoreLineItems.splice(i, 1)
              continue
            }
          }
          if (
            this.generateGatePassData.toStoreLineItems[i].quantity === 0 ||
            this.generateGatePassData.toStoreLineItems[i].quantity === '' ||
            this.generateGatePassData.toStoreLineItems[i].quantity === null
          ) {
            this.generateGatePassData.toStoreLineItems.splice(i, 1)
          }
        }
        if (this.generateGatePassData.toStoreLineItems.length === 0) {
          this.$message.error('Enter Atleast One Line Item')
          self.gatePassSaveLoading = false
          this.generateGatePassData.toStoreLineItems.push({
            inventoryType: 1,
            itemType: {
              id: null,
            },
            toolType: {
              id: null,
            },
            quantity: null,
          })
          return
        } else {
          this.generateGatePassData[
            'lineItems'
          ] = this.generateGatePassData.toStoreLineItems
        }
      }
      if (this.generateGatePassData.gatePassType === 2) {
        delete this.generateGatePassData.parentPoId
        delete this.generateGatePassData.toStoreRoom
        for (
          let i = this.generateGatePassData.fromStoreLineItems.length - 1;
          i >= 0;
          i--
        ) {
          if (
            this.generateGatePassData.fromStoreLineItems[i].inventoryType === 1
          ) {
            delete this.generateGatePassData.fromStoreLineItems[i].tool
            if (!this.generateGatePassData.fromStoreLineItems[i].item.id) {
              this.generateGatePassData.fromStoreLineItems.splice(i, 1)
              continue
            }
          } else if (
            this.generateGatePassData.fromStoreLineItems[i].inventoryType === 2
          ) {
            delete this.generateGatePassData.fromStoreLineItems[i].item
            if (!this.generateGatePassData.fromStoreLineItems[i].tool.id) {
              this.generateGatePassData.fromStoreLineItems.splice(i, 1)
              continue
            }
          }
          if (
            this.generateGatePassData.fromStoreLineItems[i].quantity === 0 ||
            this.generateGatePassData.fromStoreLineItems[i].quantity === '' ||
            this.generateGatePassData.fromStoreLineItems[i].quantity === null
          ) {
            this.generateGatePassData.fromStoreLineItems.splice(i, 1)
          }
        }
        if (this.generateGatePassData.fromStoreLineItems.length === 0) {
          this.$message.error('Enter Atleast One Line Item')
          self.gatePassSaveLoading = false
          this.generateGatePassData.fromStoreLineItems.push({
            inventoryType: 1,
            item: {
              id: null,
            },
            tool: {
              id: null,
            },
            quantity: null,
            visibility: null,
            assetIds: [],
          })
          return
        } else {
          this.generateGatePassData['lineItems'] = this.validateForOutward()
        }
      }
      if (!this.generateGatePassData.isReturnable) {
        delete this.generateGatePassData.returnTime
      }
      let temp = this.$helpers.cloneObject(this.generateGatePassData)
      let param = { gatePass: temp }
      delete param.gatePass.fromStoreLineItems
      delete param.gatePass.toStoreLineItems
      this.$http
        .post('/v2/gatePass/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.gatePassSaveLoading = false
            self.$message.success('Gate Pass Requested Successfully')
            self.$emit('saved')
          } else {
            self.$message.error(response.data.message)
            self.gatePassSaveLoading = false
          }
        })
        .catch(error => {
          self.gatePassSaveLoading = false
          self.$message.error(error)
        })
    },
    validateForOutward() {
      let lineItemsData = []
      this.generateGatePassData.fromStoreLineItems.forEach(value => {
        if (value.inventoryType === 1) {
          if (value.item.id) {
            let itemContext = this.itemList.find(i => {
              return i.id === value.item.id
            })
            if (
              itemContext &&
              itemContext.itemType &&
              itemContext.itemType.id
            ) {
              let tempItem = {
                inventoryType: 1,
                itemType: { id: itemContext.itemType.id },
                quantity: value.quantity,
                assetIds: value.assetIds,
              }
              lineItemsData.push(tempItem)
            }
          }
        } else if (value.inventoryType === 2) {
          if (value.tool.id) {
            let toolContext = this.toolList.find(i => {
              return i.id === value.tool.id
            })
            if (
              toolContext &&
              toolContext.toolType &&
              toolContext.toolType.id
            ) {
              let tempTool = {
                inventoryType: 2,
                toolType: { id: toolContext.toolType.id },
                quantity: value.quantity,
                assetIds: value.assetIds,
              }
              lineItemsData.push(tempTool)
            }
          }
        }
      })
      return lineItemsData
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      } else {
        return false
      }
    },
    cancel() {
      this.$emit('update:visibility', false)
    },
    purchaseOrderChangeActions() {
      if (this.generateGatePassData.parentPoId.id > 0) {
        let id = parseInt(this.generateGatePassData.parentPoId.id)
        let poC = this.purchaseOrders.find(po => {
          return po.id === id
        })
        let self = this
        if (poC) {
          self.generateGatePassData.toStoreLineItems = poC.lineItems
        }
      } else {
        this.generateGatePassData.toStoreLineItems = [
          {
            inventoryType: 1,
            itemType: { id: null },
            toolType: { id: null },
            quantity: null,
          },
        ]
      }
    },
    toStoreRoomChangeActions() {
      if (this.generateGatePassData.toStoreRoom.id > 0) {
        let param2 = {
          viewname: 'all',
          filters: {
            storeRoom: {
              operatorId: 36,
              value: [this.generateGatePassData.toStoreRoom.id + ''],
            },
          },
        }
        let self = this
        self.generateGatePassData.parentPoId.id = null
        self.generateGatePassData.toStoreLineItems = [
          {
            inventoryType: 1,
            itemType: { id: null },
            toolType: { id: null },
            quantity: null,
          },
        ]
        self.generateGatePassData.parentPoId.id = null
        this.$store
          .dispatch('purchaseorder/fetchPurchaseOrders', param2)
          .then(() => {
            self.purchaseOrders = self.$store.state.purchaseorder.purchaseOrders
          })
      } else {
        this.purchaseOrders = []
        this.generateGatePassData.parentPoId.id = null
        this.generateGatePassData.toStoreLineItems = [
          {
            inventoryType: 1,
            itemType: { id: null },
            toolType: { id: null },
            quantity: null,
          },
        ]
      }
    },
    fromStoreRoomChangeActions() {
      if (this.generateGatePassData.fromStoreRoom.id > 0) {
        this.loadItemforStoreRoom(this.generateGatePassData.fromStoreRoom.id)
        this.loadToolforStoreRoom(this.generateGatePassData.fromStoreRoom.id)
      } else {
        this.items = []
        this.tools = []
      }
      this.generateGatePassData.fromStoreLineItems = [
        {
          inventoryType: 1,
          item: { id: null },
          tool: { id: null },
          quantity: null,
          visibility: false,
          assetIds: [],
        },
      ]
    },
    addItemEntry2(field) {
      let emptyData = {
        inventoryType: 1,
        item: {
          id: null,
        },
        tool: {
          id: null,
        },
        quantity: null,
        visibility: false,
        assetIds: [],
      }
      field.push(emptyData)
    },
    addItemEntry(field) {
      let emptyData = {
        inventoryType: 1,
        itemType: {
          id: null,
        },
        toolType: {
          id: null,
        },
        quantity: null,
      }
      field.push(emptyData)
    },
    removeItemEntry(list, index) {
      list.splice(index, 1)
    },
    loadLineTypes(type) {
      if (type === 1) {
        if (this.itemTypes.length === 0) {
          let self = this
          let url = 'v2/itemTypes/view/all'
          this.$http.get(url).then(function(response) {
            self.itemTypes =
              response.data.result && response.data.result.itemTypes
                ? response.data.result.itemTypes
                : []
          })
        }
      } else if (type === 2) {
        if (this.toolTypes.length === 0) {
          let self = this
          let url = 'v2/toolTypes/view/all'
          this.$http.get(url).then(function(response) {
            self.toolTypes =
              response.data.result && response.data.result.toolTypes
                ? response.data.result.toolTypes
                : []
          })
        }
      }
    },
    async loadItemforStoreRoom(val) {
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [val + ''],
        },
      }
      this.itemList = await getFilteredItemList(filters)
    },
    async loadToolforStoreRoom(val) {
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [val + ''],
        },
      }
      this.toolList = await getFilteredToolList(filters)
    },
    inventoryToolTipQuantityFormatter(val) {
      if (val.inventoryType === 1 && val.item.id) {
        let itemId = parseInt(val.item.id)
        let item = this.itemList.find(i => i.id === itemId)
        return item.quantity
      } else if (val.inventoryType === 2 && val.tool.id) {
        let toolId = parseInt(val.tool.id)
        let tool = this.toolList.find(i => i.id === toolId)
        return tool.currentQuantity
      } else {
        return false
      }
    },
    checkForIsRotatingCase(val) {
      if (val.inventoryType === 1 && val.item.id) {
        let selectedItem = this.itemList.find(item => {
          return item.id === val.item.id
        })
        return selectedItem.itemType.isRotating
      } else if (val.inventoryType === 2 && val.tool.id) {
        let selectedTool = this.toolList.find(tool => {
          return tool.id === val.tool.id
        })
        return selectedTool.toolType.isRotating
      }
      return false
    },
    storeAssetData(lineItem, ids) {
      lineItem.assetIds = ids
      lineItem.quantity = ids.length
    },
  },
}
</script>
