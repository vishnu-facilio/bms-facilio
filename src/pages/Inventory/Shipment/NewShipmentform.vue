<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible="visibility"
        :fullscreen="false"
        width="65%"
        :before-close="cancel"
        :append-to-body="true"
        key="1"
        custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog fc-web-form-dialog"
      >
        <div class="fc-pm-main-content-H">
          New Shipment
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div class="new-body-modal">
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Transferred By
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="shipmentData.transferredBy.id"
                  filterable
                  placeholder="Transferred By"
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
                Received By
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="shipmentData.receivedBy.id"
                  clearable
                  filterable
                  placeholder="Received By"
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
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                From Storeroom
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="shipmentData.fromStore.id"
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
            <el-col :span="12">
              <p class="fc-input-label-txt">
                To Storeroom
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="shipmentData.toStore.id"
                  filterable
                  clearable
                  placeholder="To Storeroom"
                  class="width100"
                >
                  <template v-for="storeRoom in storeRooms">
                    <el-option
                      :key="storeRoom.id"
                      v-if="storeRoom.id !== shipmentData.fromStore.id"
                      :label="storeRoom.name"
                      :value="storeRoom.id"
                    ></el-option>
                  </template>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                Shipment Tracking
              </p>
              <el-checkbox
                v-model="shipmentData.shipmentTrackingEnabled"
              ></el-checkbox>
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
                <th
                  class="setting-table-th setting-th-text"
                  style="width: 240px;"
                >
                  UNIT PRICE
                </th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(lineItem, index) in shipmentData.fromStoreLineItems"
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
                        ;(lineItem.quantity = null), (lineItem.assetIds = null)
                      "
                      class="fc-input-full-border-select2 width150px"
                    >
                      <el-option
                        v-for="(item, index) in itemList"
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
                        ;(lineItem.quantity = null), (lineItem.assetIds = null)
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
                  <el-tooltip
                    effect="dark"
                    :open-delay="500"
                    :content="
                      'Available Quantity: ' +
                        inventoryToolTipQuantityFormatter(lineItem)
                    "
                    placement="top"
                  >
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
                  </el-tooltip>
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
                    @save="data => storeAssetData(lineItem, data)"
                  ></rotating-asset-chooser>
                </td>
                <td class="module-builder-td">
                  <el-input
                    placeholder="Cost"
                    type="number"
                    :min="0"
                    v-model="lineItem.unitPrice"
                    class="fc-input-full-border-select2 duration-input"
                  ></el-input>
                </td>
                <td>
                  <div class="visibility-hide-actions export-dropdown-menu">
                    <span
                      @click="addItemEntry2(shipmentData.fromStoreLineItems)"
                      title="Add"
                      v-tippy
                    >
                      <img src="~assets/add-icon.svg" class="mR10 mT10" />
                    </span>
                    <span
                      v-if="shipmentData.fromStoreLineItems.length > 1"
                      @click="
                        removeItemEntry(shipmentData.fromStoreLineItems, index)
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
            @click="saveShipment()"
            :loading="shipmentSaveLoading"
            >{{ shipmentSaveLoading ? 'SAVING...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import RotatingAssetChooser from 'pages/Inventory/component/RotatingAssetChooser'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import {
  getFilteredItemList,
  getFilteredToolList,
  getFilteredStoreRoomList,
} from 'pages/Inventory/InventoryUtil'

export default {
  props: ['visibility', 'shipmentEditData'],
  components: {
    RotatingAssetChooser,
  },
  data() {
    return {
      shipmentData: {
        transferredBy: { id: null },
        receivedBy: { id: null },
        shipmentTrackingEnabled: true,
        fromStore: { id: null },
        toStore: { id: null },
        fromStoreLineItems: [
          {
            inventoryType: 1,
            item: { id: null },
            tool: { id: null },
            quantity: null,
            unitPrice: null,
            visibility: false,
            assetIds: null,
          },
        ],
      },
      shipmentSaveLoading: false,
      listLoading: false,
      itemList: [],
      toolList: [],
      storeRooms: [],
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      account: state => state.account,
    }),
  },
  async mounted() {
    this.shipmentData.transferredBy.id = this.account.user.id
    this.storeRooms = await getFilteredStoreRoomList()
  },
  methods: {
    async saveShipment() {
      let self = this
      this.shipmentSaveLoading = true
      if (!this.isValid(this.shipmentData.transferredBy.id)) {
        this.$message.error('Enter Transferred By')
        self.shipmentSaveLoading = false
        return
      }
      if (!this.isValid(this.shipmentData.fromStore.id)) {
        this.$message.error('Enter From Storeroom')
        self.shipmentSaveLoading = false
        return
      }
      if (!this.isValid(this.shipmentData.toStore.id)) {
        this.$message.error('Enter To Storeroom')
        self.shipmentSaveLoading = false
        return
      }
      for (
        let i = this.shipmentData.fromStoreLineItems.length - 1;
        i >= 0;
        i--
      ) {
        if (this.shipmentData.fromStoreLineItems[i].inventoryType === 1) {
          if (!this.shipmentData.fromStoreLineItems[i].item.id) {
            this.shipmentData.fromStoreLineItems.splice(i, 1)
            continue
          }
        } else if (
          this.shipmentData.fromStoreLineItems[i].inventoryType === 2
        ) {
          if (!this.shipmentData.fromStoreLineItems[i].tool.id) {
            this.shipmentData.fromStoreLineItems.splice(i, 1)
            continue
          }
        }
        if (
          !this.isValid(this.shipmentData.fromStoreLineItems[i].quantity) ||
          !this.isValid(this.shipmentData.fromStoreLineItems[i].unitPrice)
        ) {
          this.shipmentData.fromStoreLineItems.splice(i, 1)
        }
      }
      if (this.shipmentData.fromStoreLineItems.length === 0) {
        this.$message.error('Enter Atleast One Line Item')
        self.shipmentSaveLoading = false
        this.shipmentData.fromStoreLineItems.push({
          inventoryType: 1,
          item: {
            id: null,
          },
          tool: {
            id: null,
          },
          quantity: null,
          unitPrice: null,
          visibility: null,
          assetIds: [],
        })
        return
      } else {
        if (this.validateForOutward() !== false) {
          this.shipmentData['lineItems'] = this.validateForOutward()
        } else {
          this.shipmentSaveLoading = false
          return
        }
      }
      let temp = this.$helpers.cloneObject(this.shipmentData)
      let param = { shipment: temp }
      if (!this.isValid(param.shipment.receivedBy.id)) {
        delete param.shipment.receivedBy
      }
      delete param.shipment.fromStoreLineItems
      let url = ''
      let message
      if (this.shipmentData.shipmentTrackingEnabled) {
        url = '/v2/shipment/addOrUpdate'
        message = 'Shipment Added Successfully'
      } else {
        url = '/v2/shipment/transfer'
        message = 'Shipment Transferred Successfully'
      }
      let { error } = await API.post(url, param)

      if (error) {
        this.$message.error(error.message)
      } else {
        this.$message.success(message)
        this.$emit('saved')
      }
      this.shipmentSaveLoading = false
    },
    validateForOutward() {
      let lineItemsData = []
      for (let value of this.shipmentData.fromStoreLineItems) {
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
              if (parseInt(value.quantity) > itemContext.quantity) {
                this.$message.error('Check With Available Quantity')
                return false
              }
              let tempItem = {
                inventoryType: 1,
                itemType: { id: itemContext.itemType.id },
                quantity: value.quantity,
                unitPrice: value.unitPrice,
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
              if (parseInt(value.quantity) > toolContext.currentQuantity) {
                this.$message.error('Check With Available Quantity')
                return false
              }
              let tempTool = {
                inventoryType: 2,
                toolType: { id: toolContext.toolType.id },
                quantity: value.quantity,
                unitPrice: value.unitPrice,
                assetIds: value.assetIds,
              }
              lineItemsData.push(tempTool)
            }
          }
        }
      }
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
    fromStoreRoomChangeActions() {
      if (this.shipmentData.fromStore.id > 0) {
        this.loadItemforStoreRoom(this.shipmentData.fromStore.id)
        this.loadToolforStoreRoom(this.shipmentData.fromStore.id)
      } else {
        this.items = []
        this.tools = []
      }
      this.shipmentData.fromStoreLineItems = [
        {
          inventoryType: 1,
          item: { id: null },
          tool: { id: null },
          quantity: null,
          unitPrice: null,
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
        unitPrice: null,
        visibility: false,
        assetIds: [],
      }
      field.push(emptyData)
    },
    removeItemEntry(list, index) {
      list.splice(index, 1)
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
        return 0
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
