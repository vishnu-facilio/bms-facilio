<template>
  <div>
    <div>
      <el-dialog
        :before-close="cancelItemsDialog"
        :visible.sync="visibility"
        :fullscreen="false"
        width="60%"
        open="top"
        custom-class="fc-dialog-up Inventoryaddvaluedialog"
        :append-to-body="true"
      >
        <div>
          <div class="new-header-container popup-container-new">
            <div class="fc-setup-modal-title">
              {{ $t('common.products.issued_inventory_list') }}
            </div>
            <div class="store-room-bar" v-bind:class="{ active: !hideQuery }">
              <el-select
                v-model="selectedStoreList"
                multiple
                collapse-tags
                :placeholder="$t('common.products.select_storeroom')"
                class="fc-tag mL20 fc-input-full-border-select2"
              >
                <el-option
                  v-for="(item, index) in tempStoreList"
                  :key="index"
                  :label="item.name"
                  :value="item.id"
                >
                </el-option>
              </el-select>
            </div>
            <div class="search-bar" v-bind:class="{ active: hideQuery }">
              <i
                slot="suffix"
                class="el-input__icon el-icon-search inventory-table-search-bar"
                @click="hideQuery = false"
                v-if="hideQuery"
              ></i>
              <el-input
                :autofocus="true"
                v-model="itemSearchQuery"
                type="text"
                :placeholder="$t('common._common.to_search_type')"
                class="el-input-textbox-full-border"
                @blur="hideQuery = true"
                v-else
              >
                <span class="sub-divider">|</span>
                <i
                  slot="suffix"
                  class="el-input__icon el-icon-search pointer inventory-table-search-bar"
                ></i>
              </el-input>
            </div>
          </div>
          <div class="height400">
            <div v-if="newIRIssueItem">
              <el-table
                :data="itemTransactionsTableList"
                ref="table1"
                v-loading="itemLoading"
                @selection-change="selectItemActions"
                height="350"
                :empty-text="$t('common.products.no_issued_items')"
                :default-sort="{
                  prop: 'item.itemType.name',
                  order: 'descending',
                }"
                class="inventory-inner-table width100"
              >
                <el-table-column width="60" type="selection"></el-table-column>
                <el-table-column
                  prop="item.itemType.name"
                  sortable
                  width="170"
                  :label="$t('common.header._item')"
                ></el-table-column>
                <el-table-column
                  prop="item.storeRoom.name"
                  sortable
                  width="170"
                  :label="$t('common.products.storeroom')"
                ></el-table-column>
                <el-table-column
                  prop="remainingQuantity"
                  sortable
                  width="200"
                  :label="$t('common.wo_report.remaining_qty')"
                ></el-table-column>
                <el-table-column
                  prop="asset.serialNumber"
                  sortable
                  width="130"
                  label="S NO"
                ></el-table-column>
                <el-table-column
                  :label="$t('common.products.issue_qty')"
                  width="180"
                >
                  <template v-slot="issuedItem">
                    <div>
                      <el-input
                        :placeholder="$t('common._common.quantity')"
                        :min="1"
                        ref="quantityElInput"
                        :disabled="issuedItem.row.item.itemType.isRotating"
                        type="number"
                        v-model="issuedItem.row.updateQuantity"
                        class="fc-input-full-border-select2"
                      ></el-input>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-if="newIRIssueTool">
              <el-table
                :data="toolTransactionsTableList"
                ref="table2"
                v-loading="toolLoading"
                @selection-change="selectToolActions"
                height="350"
                :empty-text="$t('common.products.no_issued_tools')"
                :default-sort="{
                  prop: 'tool.toolType.name',
                  order: 'descending',
                }"
                class="inventory-inner-table width100"
              >
                <el-table-column width="60" type="selection"></el-table-column>
                <el-table-column
                  prop="tool.toolType.name"
                  sortable
                  width="170"
                  :label="$t('common.header._tool')"
                ></el-table-column>
                <el-table-column
                  prop="tool.storeRoom.name"
                  sortable
                  width="170"
                  :label="$t('common.products.storeroom')"
                ></el-table-column>
                <el-table-column
                  prop="remainingQuantity"
                  sortable
                  width="200"
                  :label="$t('common.wo_report.remaining_qty')"
                ></el-table-column>
                <el-table-column
                  prop="asset.serialNumber"
                  sortable
                  width="130"
                  :label="$t('common.header.s_no')"
                ></el-table-column>
                <el-table-column label="ISSUE QTY">
                  <template v-slot="issuedTool">
                    <div>
                      <el-input
                        :placeholder="$t('common._common.quantity')"
                        :min="1"
                        ref="quantityElInput"
                        :disabled="issuedTool.row.tool.toolType.isRotating"
                        type="number"
                        v-model="issuedTool.row.updateQuantity"
                        class="fc-input-full-border-select2"
                      ></el-input>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <!-- <table class="setting-list-view-table width100 invent-table-dialog inventor-store-adde-table">
            <thead>
                <th class="setting-table-th setting-th-text">ID</th>
                <th class="setting-table-th setting-th-text">Name</th>
                <th class="setting-table-th setting-th-text">Storeroom</th>
                <th class="setting-table-th setting-th-text">S NO</th>
                <th class="text-right setting-table-th setting-th-text">Quantity</th>
                <th class="setting-table-th setting-th-text"></th>
            </thead>
            <tbody v-if="itemLoading">
                <tr>
                  <td colspan=100%>
                    <div class="iTloading">
                      <spinner :show="true" size="80"></spinner>
                    </div>
                  </td>
                </tr>
              </tbody>
            <tbody v-else-if="!issuedItemsTableList.length">
                <tr>
                  <td colspan=100%>
                    <div class="iTloading in-no-data text-uppercase fc-setting-table-th setting-th-text">
                      No Issued Inventory present
                    </div>
                  </td>
                </tr>
              </tbody>
            <tbody v-else>
              <template>
                <tr class="tablerow asset-hover-td" v-for="(invt, index) in issuedItemsTableList" :key="invt.id">
                    <td class="width25px">
                      <router-link class="fc-id" :to="{path:'/app/wo/inventoryrequests/all/summary/' + invt.inventoryRequestId}">
                      {{'#'+invt.inventoryRequestId}}</router-link></td>
                    <td class="width160px">{{invt.inventoryType === 1 ? invt.itemType.name : invt.toolType.name}}</td>
                    <td class="width160px">{{invt.store.name}}</td>
                    <td class="width160px">{{invt.asset ? invt.assetContext.serialNumber : '---'}}</td>
                    <td class="text-right width160px">{{invt.asset ? '1' : invt.quantity}}</td>
                    <td class="width160px">
                      <template v-if="(invt.inventoryType === 1 && invt.itemType.isRotating) || (invt.inventoryType === 2 && invt.toolType.isRotating)">
                          <el-button size="mini" class="f-number-input f-number-input-no-border" @click="invt.checked = false" v-if="invt.checked === true">Added</el-button>
                          <el-button size="mini" class="f-number-input" @click="invt.checked = true;itemSearchQuery = null" v-else>ADD</el-button>
                      </template>
                      <template v-else-if="invt.addedQuantity === 0">
                          <el-button size="mini" class="f-number-input" @click="invt.addedQuantity = 1">ADD</el-button>
                      </template>
                      <template v-else>
                          <el-input-number size="mini" class="f-number-input" v-model="invt.addedQuantity" :min="0" :max="invt.quantity"></el-input-number>
                      </template>
                    </td>
                </tr>
                </template>
            </tbody>
        </table> -->
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelItemsDialog()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              v-if="newIRIssueItem"
              @click="addSelectedItemTransactions()"
              :loading="saveLoading"
              >{{ $t('common._common._add') }}</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              v-if="newIRIssueTool"
              @click="addSelectedToolTransactions()"
              :loading="saveLoading"
              >{{ $t('common._common._add') }}</el-button
            >
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
export default {
  props: [
    'visibility',
    'workorder',
    'woItemsList',
    'woToolsList',
    'newIRIssueItem',
    'newIRIssueTool',
  ],
  data() {
    return {
      rotatingItemList: [],
      itemSearchQuery: null,
      hideQuery: true,
      issuedItemsList: [],
      selectedItem: null,
      itemLoading: false,
      iTloading: false,
      selectedStoreList: [],
      tempStoreList: [],
      saveLoading: false,
      selectedLineItems: [],
      itemTransactionsList: [],
      selectedItemsTransactions: [],
      toolTransactionsList: [],
      selectedToolsTransactions: [],
      toolLoading: false,
    }
  },
  mounted() {
    if (this.newIRIssueItem) {
      this.loadItemTransactions()
    } else if (this.newIRIssueTool) {
      this.loadToolTransactions()
    }
  },
  computed: {
    rotatingItemTableList() {
      let list = this.rotatingItemList
      if (this.itemSearchQuery) {
        let self = this
        return list.filter(data => {
          if (
            data.serialNumber
              .toLowerCase()
              .indexOf(self.itemSearchQuery.toLowerCase()) > -1
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    issuedItemsTableList() {
      let list = []
      list = this.issuedItemsList
      let self = this
      if (this.selectedStoreList.length) {
        list = list.filter(function(rt) {
          if (
            self.selectedStoreList.findIndex(rl => rl === rt.storeRoom.id) > -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSearchQuery) {
        let self = this
        return list.filter(data => {
          if (
            (data.inventoryType === 1 &&
              data.itemType.name
                .toLowerCase()
                .indexOf(self.itemSearchQuery.toLowerCase()) > -1) ||
            (data.inventoryType === 2 &&
              data.toolType.name
                .toLowerCase()
                .indexOf(self.itemSearchQuery.toLowerCase()) > -1) ||
            data.storeRoom.name
              .toLowerCase()
              .indexOf(self.itemSearchQuery.toLowerCase()) > -1 ||
            JSON.stringify(data.tempQuantity)
              .toLowerCase()
              .indexOf(self.itemSearchQuery.toLowerCase()) === 0 ||
            (data.asset &&
              data.asset.serialNumber
                .toLowerCase()
                .indexOf(self.itemSearchQuery.toLowerCase()) > -1)
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    itemTransactionsTableList() {
      let list = []
      list = this.itemTransactionsList
      if (this.selectedStoreList.length) {
        list = list.filter(rt => {
          if (
            this.selectedStoreList.findIndex(
              rl => rl === rt.item.storeRoom.id
            ) > -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSearchQuery) {
        return list.filter(data => {
          if (
            data.item.itemType.name
              .toLowerCase()
              .indexOf(this.itemSearchQuery.toLowerCase()) > -1 ||
            data.item.storeRoom.name
              .toLowerCase()
              .indexOf(this.itemSearchQuery.toLowerCase()) > -1 ||
            JSON.stringify(data.updateQuantity)
              .toLowerCase()
              .indexOf(this.itemSearchQuery.toLowerCase()) === 0 ||
            (data.asset &&
              data.asset.serialNumber
                .toLowerCase()
                .indexOf(this.itemSearchQuery.toLowerCase()) > -1)
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    toolTransactionsTableList() {
      let list = []
      list = this.toolTransactionsList
      if (this.selectedStoreList.length) {
        list = list.filter(rt => {
          if (
            this.selectedStoreList.findIndex(
              rl => rl === rt.tool.storeRoom.id
            ) > -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSearchQuery) {
        return list.filter(data => {
          if (
            data.tool.toolType.name
              .toLowerCase()
              .indexOf(this.itemSearchQuery.toLowerCase()) > -1 ||
            data.tool.storeRoom.name
              .toLowerCase()
              .indexOf(this.itemSearchQuery.toLowerCase()) > -1 ||
            JSON.stringify(data.updateQuantity)
              .toLowerCase()
              .indexOf(this.itemSearchQuery.toLowerCase()) === 0 ||
            (data.asset &&
              data.asset.serialNumber
                .toLowerCase()
                .indexOf(this.itemSearchQuery.toLowerCase()) > -1)
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
  },
  methods: {
    loadIssuedItemsList() {
      let self = this
      let param = {
        status: 6,
        parentId: this.workorder.id,
      }
      this.itemLoading = true
      let workOrderInventoryList = [...this.woItemsList, ...this.woToolsList]
      let reqLineItemList = workOrderInventoryList.filter(i => {
        if (i.requestedLineItem && i.requestedLineItem.id) {
          return true
        }
      })
      let reqLineItemId = new Set(
        reqLineItemList.map(i => i.requestedLineItem.id)
      )
      this.$http
        .post('/v2/inventoryrequest/lineItemListByParentId', param)
        .then(r => {
          let param2 = {
            status: 6,
            requesterId: self.$store.state.account.user.id,
          }
          let woLineItemsList
          if (r.data.responseCode === 0) {
            woLineItemsList = r.data.result.inventoryrequestlineitems
          }
          self.$http
            .post('/v2/inventoryrequest/lineItemListByRequesterId', param2)
            .then(response => {
              if (response.data.responseCode === 0) {
                let reqItemsList =
                  response.data.result.inventoryrequestlineitems
                let ids = new Set(woLineItemsList.map(d => d.id))
                self.issuedItemsList = [
                  ...woLineItemsList,
                  ...reqItemsList.filter(d => !ids.has(d.id)),
                ]
                self.issuedItemsList = self.issuedItemsList.filter(i => {
                  return !reqLineItemId.has(i.id)
                })
                self.issuedItemsList = self.issuedItemsList.filter(i => {
                  if (i.inventoryType === 1) {
                    if (i.issuedQuantity - i.usedQuantity > 0) {
                      return true
                    } else {
                      return false
                    }
                  } else {
                    return true
                  }
                })

                for (let item of self.issuedItemsList) {
                  self.$set(item, 'checked', false)
                  self.$set(
                    item,
                    'tempQuantity',
                    item.asset
                      ? 1
                      : item.inventoryType === 1
                      ? item.issuedQuantity - item.usedQuantity
                      : item.issuedQuantity
                  )
                  if (
                    item.storeRoom &&
                    self.tempStoreList.findIndex(
                      rt => rt.id === item.storeRoom.id
                    ) < 0
                  ) {
                    self.tempStoreList.push(item.storeRoom)
                  }
                }
              }
              self.itemLoading = false
            })
        })
        .catch(error => {
          console.log(error)
        })
    },
    selectItemActions(val) {
      this.selectedItemsTransactions = val
    },
    loadItemTransactions() {
      let self = this
      self.itemLoading = true
      let workOrderItemList = this.woItemsList.filter(i => {
        if (i.parentTransactionId !== -1) {
          return true
        }
      })
      let parentTransactionIdList = new Set(
        workOrderItemList.map(i => i.parentTransactionId)
      )
      this.$http
        .get(
          '/v2/itemTransactions/view/showItemTransactionListForIssue?showItemsForIssue=true'
        )
        .then(function(response) {
          self.itemLoading = false
          self.itemTransactionsList =
            response.data.result.itemTransactions || []
          self.itemTransactionsList = self.itemTransactionsList.filter(i => {
            return !parentTransactionIdList.has(i.id)
          })
          for (let item of self.itemTransactionsList) {
            self.$set(item, 'updateQuantity', item.remainingQuantity)
            if (
              item.item.storeRoom &&
              self.tempStoreList.findIndex(
                rt => rt.id === item.item.storeRoom.id
              ) < 0
            ) {
              self.tempStoreList.push(item.item.storeRoom)
            }
          }
        })
        .catch(function(error) {
          if (error) {
            self.itemLoading = false
          }
        })
    },
    addSelectedItemTransactions() {
      let param = { workorderItems: [] }
      let obj = {}
      if (this.selectedItemsTransactions.length === 0) {
        this.$message.error(this.$t('common.header.kindly_select_items_to_add'))
        return
      }
      this.selectedItemsTransactions.forEach((item, index) => {
        obj = {}
        if (item.itemType.isRotating) {
          let assetIds = []
          assetIds.push(item.asset.id)
          obj = {
            parentId: this.workorder.id,
            item: { id: item.item.id },
            quantity: 1,
            assetIds: assetIds,
            parentTransactionId: item.id,
            remainingQuantity: item.remainingQuantity,
            requestedLineItem: item.requestedLineItem
              ? item.requestedLineItem
              : null,
          }
          param.workorderItems.push(obj)
        } else {
          obj = {
            parentId: this.workorder.id,
            item: { id: item.item.id },
            quantity: item.updateQuantity,
            assetIds: null,
            parentTransactionId: item.id,
            remainingQuantity: item.remainingQuantity,
            requestedLineItem: item.requestedLineItem
              ? item.requestedLineItem
              : null,
          }
          param.workorderItems.push(obj)
        }
      })
      this.saveLoading = true
      let self = this
      this.$http
        .post('/v2/workorderItems/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
            self.saveLoading = false
          } else {
            self.$message.success(
              this.$t('common.products.items_added_successfully')
            )
            self.$emit('update:visibility', false)
            self.$emit('refreshItemList')
            this.$emit('refresh-item-list')
            self.saveLoading = false
          }
        })
        .catch(error => {
          console.log(error)
          self.saveLoading = false
        })
    },
    selectToolActions(val) {
      this.selectedToolsTransactions = val
    },
    loadToolTransactions() {
      let self = this
      self.toolLoading = true
      let workOrderToolList = this.woToolsList.filter(i => {
        if (i.parentTransactionId !== -1) {
          return true
        }
      })
      let parentTransactionIdList = new Set(
        workOrderToolList.map(i => i.parentTransactionId)
      )
      this.$http
        .get(
          '/v2/toolTransactions/view/showToolTransactionListForIssue?showToolsForIssue=true'
        )
        .then(function(response) {
          self.toolLoading = false
          self.toolTransactionsList =
            response.data.result.toolTransactions || []
          self.toolTransactionsList = self.toolTransactionsList.filter(i => {
            return !parentTransactionIdList.has(i.id)
          })
          for (let tool of self.toolTransactionsList) {
            self.$set(tool, 'updateQuantity', tool.remainingQuantity)
            if (
              tool.tool.storeRoom &&
              self.tempStoreList.findIndex(
                rt => rt.id === tool.tool.storeRoom.id
              ) < 0
            ) {
              self.tempStoreList.push(tool.tool.storeRoom)
            }
          }
        })
        .catch(function(error) {
          if (error) {
            self.toolLoading = false
          }
        })
    },
    addSelectedToolTransactions() {
      let self = this
      let param = { workorderToolsList: [] }
      let obj = {}
      if (this.selectedToolsTransactions.length === 0) {
        this.$message.error(this.$t('common.header.kindly_select_tools_to_add'))
        return
      }
      this.selectedToolsTransactions.forEach((item, index) => {
        obj = {}
        if (item.toolType.isRotating) {
          let assetIds = []
          assetIds.push(item.asset.id)

          obj = {
            parentId: this.workorder.id,
            tool: { id: item.tool.id },
            quantity: 1,
            duration: 3600000,
            assetIds: assetIds,
            parentTransactionId: item.id,
            requestedLineItem: item.requestedLineItem
              ? item.requestedLineItem
              : null,
          }
          param.workorderToolsList.push(obj)
        } else {
          obj = {
            parentId: this.workorder.id,
            tool: { id: item.tool.id },
            quantity: item.updateQuantity,
            duration: 3600000,
            assetIds: null,
            parentTransactionId: item.id,
            requestedLineItem: item.requestedLineItem
              ? item.requestedLineItem
              : null,
          }
          param.workorderToolsList.push(obj)
        }
      })
      self.saveLoading = true
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
            self.saveLoading = false
          } else {
            self.$message.success(
              this.$t('common.products.tools_added_successfully')
            )
            self.$emit('update:visibility', false)
            self.$emit('refreshToolList')
            this.$emit('refresh-tool-list')
            self.saveLoading = false
          }
        })
        .catch(error => {
          console.log(error)
          self.saveLoading = false
        })
    },
    cancelItemsDialog() {
      this.$emit('update:visibility', false)
      this.selectedItem = null
    },
    toggleSelection(selected) {
      this.selectedLineItems = selected
    },
    addSelectedItem() {
      // Temp fix Should be changed to single API .. Has Bug
      let param = { inventoryType: 1, lineItems: [] }
      let param2 = { inventoryType: 2, lineItems: [] }
      let obj = {}
      let assetIds = []
      this.selectedLineItems.forEach((item, index) => {
        obj = {}
        assetIds = []
        if (item.inventoryType === 1) {
          if (item.itemType.isRotating) {
            item.assetIds = [item.asset]
            assetIds = [item.asset]
            item.parentId = this.workorder.id
            obj = {
              parentId: this.workorder.id,
              id: item.id,
              itemType: { id: item.itemType.id },
              storeRoom: item.storeRoom,
              inventoryRequestId: item.inventoryRequestId,
              quantity: 1,
              asset: item.asset,
              inventoryType: 1,
            }
            param.lineItems.push(obj)
          } else {
            if (item.tempQuantity > 0) {
              item.assetIds = [item.asset]
              assetIds = [item.asset]
              item.parentId = this.workorder.id
              obj = {
                parentId: this.workorder.id,
                id: item.id,
                itemType: { id: item.itemType.id },
                storeRoom: item.storeRoom,
                inventoryRequestId: item.inventoryRequestId,
                quantity: item.tempQuantity,
                inventoryType: 1,
              }
              param.lineItems.push(obj)
            }
          }
        } else if (item.inventoryType === 2) {
          if (item.toolType.isRotating) {
            assetIds = [item.asset]
            obj = {
              parentId: this.workorder.id,
              id: item.id,
              toolType: { id: item.toolType.id },
              storeRoom: item.storeRoom,
              inventoryRequestId: item.inventoryRequestId,
              quantity: 1,
              asset: item.asset,
              inventoryType: 2,
            }
            param2.lineItems.push(obj)
          } else {
            if (item.tempQuantity > 0) {
              obj = {
                parentId: this.workorder.id,
                id: item.id,
                toolType: { id: item.toolType.id },
                storeRoom: item.storeRoom,
                inventoryRequestId: item.inventoryRequestId,
                quantity: item.tempQuantity,
                inventoryType: 2,
              }
              param2.lineItems.push(obj)
            }
          }
        }
      })
      let self = this
      this.saveLoading = true
      let successCount = 0
      let callCount = 0
      if (param.lineItems.length) {
        callCount++
        this.$http
          .post('/v2/inventoryrequest/useLineItems', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.$message.success(
                this.$t('common.products.items_added_successfully')
              )
              self.$emit('refreshItemList')
              successCount++
              if (callCount === successCount) {
                self.$emit('update:visibility', false)
              }
            } else {
              self.$message.error(response.data.message)
            }
            self.saveLoading = false
          })
          .catch(error => {
            console.log(error)
          })
      }
      if (param2.lineItems.length) {
        callCount++
        this.$http
          .post('/v2/inventoryrequest/useLineItems', param2)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.$message.success(
                this.$t('common.products.tools_added_successfully')
              )
              self.$emit('refreshToolList')
              successCount++
              if (callCount === successCount) {
                self.$emit('update:visibility', false)
              }
            } else {
              self.$message.error(response.data.message)
            }
            self.saveLoading = false
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
  },
}
</script>
<style lang="scss">
.Inventoryaddvaluedialog {
  .inventory-table thead > tr {
    height: 55px;
    border-top: 1px solid #eceef1;
    border-bottom: 1px solid #eceef1;
  }
  .inventory-table th {
    white-space: nowrap;
  }
  .inventory-table.pB20.pT20.tbody.tr:hover .el-input__inner {
    border-color: #d0d9e2 !important;
  }
  .inventory-table table > tbody tr:last-child {
    border-bottom: 1px solid #eceef1 !important;
  }
  .fc-inv-container-body {
    height: 50vh;
    overflow: auto;
    /* padding: 0 20px 20px; */
  }
  .invent-table-dialog .fc-setting-table-th setting-th-text {
    padding: 15px 30px;
  }
  .exceeded .el-input__inner,
  .exceeded .el-input__inner:focus {
    border-color: #f56c6c !important;
  }
  .search-bar {
    width: 40%;
    justify-content: right;
    align-items: right;
    text-align: right;
    position: absolute;
    right: 28px;
    top: 10px;
    overflow: hidden;
    height: 40px;
  }
  .search-bar.active {
    width: 10%;
  }
  .popup-container-new {
    width: 100%;
    padding: 18px 30px 18px;
    border-bottom: 1px solid #edeeef;
    position: -webkit-sticky;
    position: sticky;
    background: #fff;
    display: -webkit-inline-box;
    width: 100%;
    align-items: center;
  }
  .fc-setup-modal-title2 {
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 0.3px;
    letter-spacing: 0.9px;
    color: #333333;
    text-transform: uppercase;
    width: 40%;
  }
  .store-room-bar {
    position: absolute;
    right: 60px !important;
  }
  .store-room-bar.active {
    position: absolute;
    left: 0;
    background: #fff;
  }
  .store-room-bar .el-input .el-input__inner {
    border: 0px;
  }
  // .store-room-bar .el-input__suffix {
  //   right: -8px;
  // }
  // .store-room-bar .el-input__suffix {
  //   top: 7px;
  // }
  .in-no-data {
    height: 100px;
    width: 100%;
    text-align: center;
    justify-content: center;
    display: flex;
    align-items: center;
  }
  .invent-table-dialog tbody tr.tablerow.active1 td:first-child {
    border-left: 3px solid #39b2c2 !important;
  }
  .invent-table-dialog .fc-setting-table-th setting-th-text {
    padding: 15px 30px;
  }
  .popup-container-new {
    width: 100%;
    padding: 18px 30px 18px;
    border-bottom: 1px solid #edeeef;
    position: -webkit-sticky;
    position: sticky;
    background: #fff;
    display: flex;
    width: 100%;
    align-items: center;
  }
  .inv-id-bac-icon {
    position: relative;
    right: 1px;
    margin-right: 10px;
    font-weight: bold;
    color: #324056;
  }
}
</style>
