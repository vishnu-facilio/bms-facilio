<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible.sync="visibility"
        :fullscreen="true"
        :before-close="closeDialog"
        :append-to-body="true"
        :key="storeRoom.id"
        custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999;"
      >
        <div class="fc-pm-main-content-H">
          {{ $t('common.products.issue_inventory') }}
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div class="new-body-modal pL20 pR20">
          <!-- <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('common.header.inventory_request') }}
              </p>
              <el-select
                v-model="inventoryIssueData.inventoryRequest.id"
                @change="
                  inventoryRequestChangeActions(
                    inventoryIssueData.inventoryRequest.id
                  )
                "
                filterable
                clearable
                class="fc-input-full-border2 mT5"
              >
                <el-option
                  v-for="inv in inventoryRequestList"
                  :label="inv.name"
                  :value="inv.id"
                  :key="inv.id"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="12" v-if="inventoryIssueData.inventoryRequest.id">
              <p class="fc-input-label-txt">
                {{ $t('common._common.workorder_id') }}
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-input
                  v-model="inventoryIssueData.parentId"
                  :placeholder="$t('common._common.workorder_id')"
                  disabled
                  :min="0"
                  type="number"
                  class="fc-input-full-border-select2 duration-input"
                ></el-input>
              </div>
            </el-col>
          </el-row> -->
          <el-row class="mB20" :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt">
                {{ $t('common.products.issue_type') }}
              </p>
              <el-radio-group
                :disabled="
                  Boolean(
                    inventoryIssueData.inventoryRequest &&
                      inventoryIssueData.inventoryRequest.id
                  )
                "
                @change="issueTypeChangeActions"
                v-model="issueType"
              >
                <el-radio
                  :label="$t('common.products.user')"
                  class="fc-radio-btn"
                  >{{ $t('common.products.user') }}</el-radio
                >
                <el-radio
                  :label="$t('common._common.space/asset')"
                  class="fc-radio-btn"
                  >{{ $t('common._common.space/asset') }}</el-radio
                >
              </el-radio-group>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('common.products.issued_to') }}
              </p>
              <div v-if="issueType === $t('common._common.space/asset')">
                <el-input
                  @change="
                    quickSearchQuery = spaceAssetDisplayName
                    this.spaceAssetVisibility = true
                  "
                  v-model="spaceAssetDisplayName"
                  type="text"
                  :placeholder="$t('common._common.to_search_type')"
                  class="fc-input-full-border-select2"
                >
                  <i
                    @click="spaceAssetVisibility = true"
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                  ></i>
                </el-input>
                <space-asset-chooser
                  @associate="associate"
                  :visibility.sync="spaceAssetVisibility"
                  :query="quickSearchQuery"
                  :appendToBody="false"
                ></space-asset-chooser>
              </div>
              <el-select
                v-else-if="issueType === $t('common.products.user')"
                v-model="inventoryIssueData.resource.id"
                :disabled="
                  Boolean(
                    inventoryIssueData.inventoryRequest &&
                      inventoryIssueData.inventoryRequest.id
                  )
                "
                filterable
                class="fc-input-full-border-select2 mT5"
              >
                <el-option
                  v-for="user in users"
                  :label="user.name"
                  :value="user.id"
                  :key="user.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <div
            class="position-relative"
            v-if="inventoryIssueData.inventoryRequest.id"
          >
            <table class="setting-list-view-table store-table width100">
              <thead class="setup-dialog-thead">
                <tr>
                  <th
                    class="setting-table-th setting-th-text"
                    style="width: 240px;"
                  >
                    {{ $t('common._common.type') }}
                  </th>
                  <th
                    class="setting-table-th setting-th-text"
                    style="width: 240px;"
                  >
                    {{ $t('common.products._name') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('common._common."quantity"') }}
                  </th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(lineItem,
                  index) in inventoryIssueData.invReqLineItems"
                  :key="index"
                  class="visibility-visible-actions"
                >
                  <td class="module-builder-td">
                    <el-select
                      disabled
                      v-model="lineItem.inventoryType"
                      class="fc-input-full-border-select2 width100px"
                      @change="loadTypes(lineItem.inventoryType)"
                    >
                      <el-option
                        :label="$t('common.header.item')"
                        key="1"
                        :value="1"
                      ></el-option>
                      <el-option
                        :label="$t('common.header.tool')"
                        key="2"
                        :value="2"
                      ></el-option>
                    </el-select>
                  </td>
                  <td class="module-builder-td">
                    <div v-if="parseInt(lineItem.inventoryType) === 1">
                      <el-select
                        disabled
                        filterable
                        clearable
                        v-model="lineItem.itemType.id"
                        class="fc-input-full-border-select2 width150px"
                      >
                        <el-option
                          v-for="(itemType, index) in itemTypes"
                          :key="index"
                          :label="itemType.name"
                          :value="itemType.id"
                        >
                        </el-option>
                      </el-select>
                    </div>
                    <div v-if="parseInt(lineItem.inventoryType) === 2">
                      <el-select
                        disabled
                        filterable
                        clearable
                        v-model="lineItem.toolType.id"
                        class="fc-input-full-border-select2 width150px"
                      >
                        <el-option
                          v-for="(toolType, index) in toolTypes"
                          :key="index"
                          :label="toolType.name"
                          :value="toolType.id"
                        >
                        </el-option>
                      </el-select>
                    </div>
                  </td>
                  <td class="module-builder-td">
                    <el-input
                      disabled
                      :placeholder="$t('common._common.quantity')"
                      :min="0"
                      type="number"
                      v-model="lineItem.quantity"
                      class="fc-input-full-border-select2 width100px duration-input"
                    ></el-input>
                  </td>
                  <td></td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="position-relative" v-else>
            <table class="setting-list-view-table store-table width100">
              <thead class="setup-dialog-thead">
                <tr>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('common._common.type') }}
                  </th>
                  <th
                    class="setting-table-th setting-th-text"
                    style="width: 240px;"
                  >
                    {{ $t('common.products.name') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('common._common.quantity') }}
                  </th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="(lineItem,
                  index) in inventoryIssueData.storeIssueLineItems"
                  :key="index"
                  class="visibility-visible-actions"
                >
                  <td class="module-builder-td">
                    <el-select
                      v-model="lineItem.inventoryType"
                      class="fc-input-full-border-select2 width100px"
                      @change="loadTypes(lineItem.inventoryType)"
                    >
                      <el-option
                        :label="$t('common.header.item')"
                        key="1"
                        :value="1"
                      ></el-option>
                      <el-option
                        v-if="issueType !== $t('common._common.space/asset')"
                        :label="$t('common.header.tool')"
                        key="2"
                        :value="2"
                      ></el-option>
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
                        class="fc-input-full-border-select2 width100"
                      >
                        <el-option
                          v-for="(item, index) in lineItemList"
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
                        class="fc-input-full-border-select2 width100"
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
                      :disabled="!inventoryToolTipQuantityFormatter(lineItem)"
                      :content="
                        'Available Quantity: ' +
                          inventoryToolTipQuantityFormatter(lineItem)
                      "
                      placement="top"
                    >
                      <el-input
                        :placeholder="$t('common._common.quantity')"
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
                  <td>
                    <div class="visibility-hide-actions export-dropdown-menu">
                      <span
                        @click="
                          addItemEntry(inventoryIssueData.storeIssueLineItems)
                        "
                        :title="$t('common._common.add')"
                        v-tippy
                      >
                        <img src="~assets/add-icon.svg" class="mR10 mT10" />
                      </span>
                      <span
                        v-if="inventoryIssueData.storeIssueLineItems.length > 1"
                        @click="
                          removeItemEntry(
                            inventoryIssueData.storeIssueLineItems,
                            index
                          )
                        "
                        :title="$t('common._common.remove')"
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
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="issueLineItems()"
            :loading="issueLoading"
            >{{ $t('common.products.issue') }}</el-button
          >
        </div>
        <div v-if="rotatingItemChooserDialog">
          <el-dialog
            :visible="rotatingItemChooserDialog"
            :before-close="closeDialog"
            :fullscreen="false"
            :title="$t('common.products.issue_inventory_request')"
            open="top"
            custom-class="fc-dialog-center-container"
            :append-to-body="true"
          >
            <div class="height400">
              <el-steps
                :active="rotatingItemStepStatus"
                finish-status="success"
              >
                <el-step
                  v-for="(item, i) in rotatingLineItemsList"
                  :title="
                    item.inventoryType === 1
                      ? item.itemType.name
                      : item.toolType.name
                  "
                  :description="
                    (i === rotatingItemStepStatus
                      ? selectedRotatingItem.length
                      : item.assetIds
                      ? item.assetIds.length
                      : '0') +
                      '/' +
                      item.quantity
                  "
                  :key="item.id"
                ></el-step>
              </el-steps>
              <el-table
                ref="issueTable"
                height="200"
                :data="rotatingItemList"
                v-loading="iTloading"
                :empty-text="$t('common.products.no_rotating_assets_available')"
                @selection-change="selectRotatingAsset"
                :default-sort="{ prop: 'costDate', order: 'descending' }"
                class="width100 inventory-inner-table"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="serialNumber"
                  sortable
                  :label="$t('common._common._serial_number')"
                ></el-table-column>
                <el-table-column sortable :label="$t('common.header.price')">
                  <template v-slot="scope">
                    <currency
                      :value="
                        scope.row.unitPrice > 0 ? scope.row.unitPrice : '---'
                      "
                    ></currency>
                  </template>
                </el-table-column>
                <el-table-column
                  sortable
                  prop="purchasedDate"
                  :formatter="getDateTimePurchasedDate"
                  :label="$t('common.products.purchased_time')"
                ></el-table-column>
              </el-table>
              <div class="modal-dialog-footer">
                <el-button class="modal-btn-cancel" @click="closeDialog()">{{
                  $t('common._common.cancel')
                }}</el-button>
                <el-button
                  class="modal-btn-save width100"
                  type="primary"
                  @click="nextStep()"
                  >{{
                    rotatingItemStepStatus === rotatingLineItemsList.length - 1
                      ? 'Issue'
                      : 'Next'
                  }}</el-button
                >
              </div>
            </div>
          </el-dialog>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import RotatingAssetChooser from 'pages/Inventory/component/RotatingAssetChooser'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import { API } from '@facilio/api'
const transactionTypeEnum = {
  MANUAL: 3,
}
const transactionStateEnum = {
  ISSUE: 2,
}
export default {
  data() {
    return {
      inventoryRequestList: null,
      issueLoading: false,
      inventoryIssueData: {
        inventoryRequest: { id: null },
        requestedBy: { id: null },
        resource: { id: null, resourceType: null },
        invReqLineItems: [
          {
            inventoryType: 1,
            itemType: {
              id: null,
            },
            toolType: {
              id: null,
            },
            quantity: null,
          },
        ],
        storeIssueLineItems: [
          {
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
          },
        ],
        parentId: null,
      },
      itemTypes: [],
      toolTypes: [],
      rotatingItemChooserDialog: false,
      rotatingLineItemsList: [],
      rotatingItemStepStatus: 0,
      iTloading: false,
      rotatingItemList: [],
      selectedRotatingItem: [],
      issueType: 'User',
      spaceAssetDisplayName: null,
      spaceAssetVisibility: false,
      quickSearchQuery: '',
    }
  },
  props: ['storeRoom', 'visibility', 'itemList', 'toolList'],
  components: {
    RotatingAssetChooser,
    SpaceAssetChooser,
  },
  mounted() {
    this.loadInventoryRequestList()
    this.loadTypes(1)
    this.loadTypes(2)
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
    lineItemList() {
      let { itemList } = this
      return itemList.filter(item => {
        if (this.issueType === this.$t('common._common.space/asset')) {
          if (item.itemType.isRotating) {
            return false
          }
        }
        if (!item.itemType.consumable) {
          return false
        }
        return true
      })
    },
  },
  methods: {
    loadInventoryRequestList() {
      let self = this
      let param = {
        status: 2,
        storeRoomId: this.storeRoom.id,
      }
      this.$http
        .post('/v2/inventoryrequest/list', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.inventoryRequestList = response.data.result.inventoryrequests
          }
        })
        .catch(() => {})
    },
    issueLineItems() {
      if (this.inventoryIssueData.inventoryRequest.id) {
        this.issueInventoryRequestActions(this.storeRoom)
      } else {
        this.manualIssueActions()
      }
    },
    selectedItemFill(val) {
      if (val) {
        this.inventoryRequestList = this.inventoryRequestList.find(
          i => val === i.id
        )
      }
    },
    cancelForm() {
      this.$emit('update:visibility', false)
    },
    addIRItemEntry(field) {
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
    removeIRItemEntry(list, index) {
      list.splice(index, 1)
    },
    addItemEntry(field) {
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
    removeItemEntry(list, index) {
      list.splice(index, 1)
    },
    inventoryRequestChangeActions(val) {
      if (val) {
        let inventoryRequest = this.inventoryRequestList.find(inv => {
          return inv.id === val
        })
        if (inventoryRequest) {
          if (
            inventoryRequest.requestedFor &&
            inventoryRequest.requestedFor.id
          ) {
            this.inventoryIssueData.resource.id =
              inventoryRequest.requestedFor.id
            this.issueType = this.$t('common.products.user')
          }
          if (inventoryRequest.lineItems) {
            this.inventoryIssueData.invReqLineItems = inventoryRequest.lineItems
          }
          if (inventoryRequest.parentId && inventoryRequest.parentId > 0) {
            this.inventoryIssueData.parentId = inventoryRequest.parentId
          }
        }
      }
    },
    loadTypes(type) {
      if (type === 1) {
        if (this.itemTypes.length === 0) {
          let self = this
          let url = 'v2/itemTypes/view/all'
          this.$http
            .get(url)
            .then(function(response) {
              self.itemTypes =
                response.data.result && response.data.result.itemTypes
                  ? response.data.result.itemTypes
                  : []
            })
            .catch(() => {})
        }
      } else if (type === 2) {
        if (this.toolTypes.length === 0) {
          let self = this
          let url = 'v2/toolTypes/view/all'
          this.$http
            .get(url)
            .then(function(response) {
              self.toolTypes =
                response.data.result && response.data.result.toolTypes
                  ? response.data.result.toolTypes
                  : []
            })
            .catch(() => {})
        }
      }
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
    issueInventoryRequest() {
      let param = {
        inventoryRequest: {
          id: this.inventoryIssueData.inventoryRequest.id,
          lineItems: this.inventoryIssueData.invReqLineItems,
          storeRoom: this.storeRoom,
          requestedBy: { id: this.inventoryIssueData.requestedBy.id },
          requestedFor: { id: this.inventoryIssueData.resource.id },
          status: 6,
        },
      }
      if (this.inventoryIssueData.parentId > 0) {
        param.inventoryRequest['parentId'] = this.inventoryIssueData.parentId
      }
      let self = this
      this.$http
        .post('/v2/inventoryrequest/issueInventoryRequests', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common.products.items_issued_successfully')
            )
            self.$emit('refreshInventory')
            self.closeDialog()
          } else {
            self.$message.error(response.data.message)
            self.closeDialog()
          }
        })
        .catch(() => {})
    },
    issueInventoryRequestActions(storeId) {
      if (storeId) {
        for (let i in this.inventoryIssueData.invReqLineItems) {
          if (
            this.inventoryIssueData.invReqLineItems[i].inventoryType === 1 &&
            this.inventoryIssueData.invReqLineItems[i].itemType.isRotating
          ) {
            this.rotatingLineItemsList.push(
              this.inventoryIssueData.invReqLineItems[i]
            )
          } else if (
            this.inventoryIssueData.invReqLineItems[i].inventoryType === 2 &&
            this.inventoryIssueData.invReqLineItems[i].toolType.isRotating
          ) {
            this.rotatingLineItemsList.push(
              this.inventoryIssueData.invReqLineItems[i]
            )
          }
          if (
            parseInt(i) ===
            this.inventoryIssueData.invReqLineItems.length - 1
          ) {
            if (this.rotatingLineItemsList.length > 0) {
              this.rotatingItemChooserDialog = true
              this.loadRotatingAsset()
              return
            }
            this.issueInventoryRequest()
          }
        }
      }
    },
    nextStep() {
      let count = this.rotatingLineItemsList[this.rotatingItemStepStatus]
        .quantity
      if (this.selectedRotatingItem.length !== count) {
        this.$message.error(
          'Approved Count ' +
            count +
            ' Selected Count ' +
            this.selectedRotatingItem.length
        )
        return
      }
      let assetIds = this.selectedRotatingItem.map(d => d.id)
      this.$refs.issueTable.clearSelection()
      this.rotatingLineItemsList[
        this.rotatingItemStepStatus
      ].assetIds = assetIds
      this.inventoryIssueData.invReqLineItems[
        this.rotatingItemStepStatus
      ].assetIds = assetIds
      this.selectedRotatingItem = []
      if (
        this.rotatingItemStepStatus++ ===
        this.rotatingLineItemsList.length - 1
      ) {
        this.issueInventoryRequest()
        this.rotatingItemStepStatus = 0
        return
      }
      this.loadRotatingAsset()
    },
    selectRotatingAsset(val) {
      this.selectedRotatingItem = val
    },
    getDateTimePurchasedDate(val) {
      let value = val.purchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    loadRotatingAsset() {
      let self = this
      self.iTloading = true
      let queryObj
      if (
        this.rotatingLineItemsList[this.rotatingItemStepStatus]
          .inventoryType === 1
      ) {
        queryObj = {
          inventoryType: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .inventoryType,
          itemTypeId: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .itemType.id,
          storeRoomId: this.storeRoom.id,
        }
      } else {
        queryObj = {
          inventoryType: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .inventoryType,
          toolTypeId: this.rotatingLineItemsList[this.rotatingItemStepStatus]
            .toolType.id,
          storeRoomId: this.storeRoom.id,
        }
      }
      this.$http
        .post('/v2/assets/typeAndStore', queryObj)
        .then(function(response) {
          let individualItemList = []
          if (response.data.responseCode === 0) {
            individualItemList = response.data.result.assets
            self.rotatingItemList = individualItemList
          } else {
            self.$message.error(response.data.message)
            self.closeDialog()
          }
          self.iTloading = false
        })
        .catch(() => {})
    },
    closeDialog() {
      this.rotatingItemChooserDialog = false
      this.rotatingItemStepStatus = 0
      this.rotatingLineItemsList = []
      this.rotatingItemList = []
      this.selectedRotatingItem = []
      this.cancelForm()
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
    async manualIssueActions() {
      // Temp fix Should be changed to single API .. Has Bug
      if (!this.inventoryIssueData.resource.id) {
        this.$message.error(
          this.$t('common.header.please_choose_resource_user')
        )
      } else if (this.inventoryIssueData.resource.id) {
        let itemTransaction = []
        let toolTransaction = []
        this.inventoryIssueData.storeIssueLineItems.forEach(i => {
          if (i.inventoryType === 1) {
            let tempItem = {
              item: {
                id: i.item.id,
              },
              parentId: this.inventoryIssueData.resource.id,
              issuedTo: { ouid: null },
              transactionType: transactionTypeEnum.MANUAL,
              transactionState: transactionStateEnum.ISSUE,
              quantity: i.quantity,
              assetIds: i.assetIds.length === 0 ? null : i.assetIds,
              resource: { resourceType: null },
            }
            if (this.issueType === this.$t('common.products.user')) {
              tempItem.issuedTo.ouid = this.inventoryIssueData.resource.id
              tempItem.resource.resourceType = 4
            } else {
              tempItem.resource.resourceType = this.inventoryIssueData.resource.resourceType
              delete tempItem.issuedTo
            }
            itemTransaction.push(tempItem)
          } else if (i.inventoryType === 2) {
            let tempTool = {
              tool: {
                id: i.tool.id,
              },
              parentId: this.inventoryIssueData.resource.id,
              issuedTo: { ouid: null },
              transactionType: transactionTypeEnum.MANUAL,
              transactionState: transactionStateEnum.ISSUE,
              quantity: i.quantity,
              assetIds: i.assetIds.length === 0 ? null : i.assetIds,
              resource: { resourceType: null },
            }
            if (this.issueType === this.$t('common.products.user')) {
              tempTool.issuedTo.ouid = this.inventoryIssueData.resource.id
              tempTool.resource.resourceType = 4
            } else {
              tempTool.resource.resourceType = this.inventoryIssueData.resource.resourceType
              delete tempTool.issuedTo
            }
            toolTransaction.push(tempTool)
          }
        })
        let url = 'v3/modules/data/bulkCreate'
        if (itemTransaction.length > 0) {
          let params = {
            data: {
              itemTransactions: itemTransaction,
            },
            moduleName: 'itemTransactions',
            params: {
              issue: true,
            },
          }
          let { error } = await API.post(url, params)
          if (error) {
            this.$message.error(
              error.message || this.$t('common.wo_report.unable_to_update')
            )
          } else {
            this.$message.success(
              this.$t('common.header.item_issued_successfully')
            )
            this.$emit('refreshInventory')
            this.closeDialog()
          }
        }
        if (toolTransaction.length > 0) {
          let params = {
            data: {
              toolTransactions: toolTransaction,
            },
            moduleName: 'toolTransactions',
            params: {
              issue: true,
            },
          }
          let { error } = await API.post(url, params)
          if (error) {
            this.$message.error(
              error.message || this.$t('common.wo_report.unable_to_update')
            )
          } else {
            this.$message.success(
              this.$t('common.header.tool_issued_successfully')
            )

            this.$emit('refreshInventory')
            this.closeDialog()
          }
        }
      }
    },
    associate(selectedObj) {
      this.inventoryIssueData.resource.id = selectedObj.id
      this.inventoryIssueData.resource.resourceType = selectedObj.resourceType
      this.spaceAssetDisplayName = selectedObj.name
      this.spaceAssetVisibility = false
    },
    issueTypeChangeActions() {
      this.inventoryIssueData.resource.id = null
      this.spaceAssetDisplayName = ''
      let lineItemObject = [
        {
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
        },
      ]
      this.$set(this.inventoryIssueData, 'storeIssueLineItems', lineItemObject)
    },
  },
}
</script>
