<template>
  <div>
    <div v-if="type === 'issue'">
      <div>
        <el-dialog
          :visible.sync="showItemIssueReturn"
          title="ISSUE ITEM"
          :before-close="cancelForm"
          :key="storeRoom.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div>
            <div class="fc-black3-16 text-left fw4">
              Item
            </div>
            <el-select
              v-model="selectedItem"
              @change="selectedItemFill(selectedItem)"
              filterable
              class="fc-input-full-border-select2 mT5"
            >
              <template v-for="item in itemList">
                <el-option
                  :label="item.itemType.name"
                  v-if="
                    !(
                      item.itemType.approvalNeeded ||
                      item.storeRoom.approvalNeeded
                    ) && item.itemType.consumable
                  "
                  :value="item.id"
                  :key="item.id"
                ></el-option>
              </template>
            </el-select>
          </div>
          <div class="height400 mT20" v-if="!loading">
            <div v-if="item && item.itemType.isRotating && item.id">
              <el-table
                height="200"
                :data="purchasedUnUsedItemList"
                v-loading="purLoading"
                empty-text="No Rotating assets available."
                @selection-change="toggleUnUsedItemSelection"
                :default-sort="{ prop: 'costDate', order: 'descending' }"
                class="width100 inventory-inner-table"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="serialNumber"
                  sortable
                  label="SERIAL NUMBER"
                ></el-table-column>
                <el-table-column sortable label="PRICE">
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
                  label="PURCHASED TIME"
                ></el-table-column>
              </el-table>
              <el-row class="mT20">
                <el-col :span="12">
                  <p class="fc-black3-16 text-left fw4">
                    Issue To
                  </p>
                  <el-select
                    v-model="userid"
                    filterable
                    :placeholder="'Select User'"
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      v-for="user in users"
                      :key="user.id"
                      :label="user.name"
                      :value="parseInt(user.id)"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
            <div v-else-if="item && !item.itemType.isRotating && item.id">
              <div>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="fc-id">#{{ item.itemType.id }}</div>
                    <div class="fc-black3-16 text-left">
                      {{ item.itemType.name }}
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <store-room-avatar
                      name="true"
                      size="lg"
                      :storeRoom="storeRoom"
                    ></store-room-avatar>
                  </el-col>
                </el-row>
              </div>
              <el-row :gutter="10" class="mT40">
                <el-col :span="12">
                  <div class="fc-black3-16 text-left fw4">
                    Issue Quantity
                  </div>
                  <el-input
                    placeholder="Issue Quantity"
                    :min="1"
                    :max="item.quantity"
                    type="number"
                    v-model="quantity"
                    class="fc-input-full-border2 mT5"
                  >
                    <span slot="suffix" class="width100 item-input-txt"
                      >Item</span
                    >
                  </el-input>
                  <div class="green-txt2-13 mT10 fw4">
                    {{ item.quantity }} Current Balance
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="fc-black3-16 text-left fw4">
                    Issue To
                  </div>
                  <el-select
                    v-model="userid"
                    filterable
                    clearable
                    :placeholder="'Select User'"
                    class="fc-input-full-border-select2 width100 mT5"
                  >
                    <el-option
                      v-for="user in users"
                      :key="user.id"
                      :label="user.name"
                      :value="parseInt(user.id)"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()"
              >CANCEL</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveIssueForm()"
              :loading="saving"
              >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
    <div v-if="type === 'return'">
      <div>
        <el-dialog
          :visible.sync="showItemIssueReturn"
          :key="storeRoom.id"
          title="RETURN ITEM"
          :before-close="cancelForm"
          width="65%"
          custom-class="fc-dialog-center-container"
        >
          <div>
            <div class="fc-black3-16 text-left fw4">
              Item
            </div>
            <el-select
              v-model="selectedItem"
              @change="selectedItemFill(selectedItem)"
              filterable
              class="fc-input-full-border-select2 mT5"
            >
              <template v-for="item in itemList">
                <el-option
                  :label="item.itemType.name"
                  v-if="item.itemType.consumable"
                  :value="item.id"
                  :key="item.id"
                ></el-option>
              </template>
            </el-select>
          </div>
          <div class="height400 mT20">
            <div v-if="issueLoading">
              <spinner :show="issueLoading"></spinner>
            </div>
            <div
              v-if="
                !issueLoading && item && !item.itemType.isRotating && item.id
              "
            >
              <div
                v-if="selectedIssuedItems.length > 0"
                @click="markAsFullyReturned(issuedItems)"
                class="rv-sum-table-click"
              >
                <img src="~assets/mark-as-fully-recieved.svg" class="mR10" />
                Mark as Fully Returned
              </div>
              <el-table
                :data="issuedItems"
                v-loading="issueLoading"
                ref="returnTable"
                @selection-change="selectIssuedItemActions"
                height="300"
                empty-text="This item is not issued to anyone."
                :default-sort="{
                  prop: 'item.storeRoom.name',
                  order: 'descending',
                }"
                class="inventory-inner-table width100"
              >
                <el-table-column width="60" type="selection"></el-table-column>
                <el-table-column
                  prop="item.storeRoom.name"
                  sortable
                  width="190"
                  label="STOREROOM NAME"
                ></el-table-column>
                <el-table-column
                  prop="remainingQuantity"
                  sortable
                  width="130"
                  label="QUANTITY"
                ></el-table-column>
                <el-table-column
                  prop="sysModifiedTime"
                  :formatter="getDateTimeIssuedTime"
                  sortable
                  label="ISSUED TIME"
                ></el-table-column>
                <el-table-column label="ISSUED TO" width="140">
                  <template v-slot="item">
                    <div v-if="item.row.transactionType === 2">
                      WO-<router-link
                        class="fc-id"
                        :to="{
                          path: '/app/wo/orders/summary/' + item.row.parentId,
                        }"
                      >
                        #{{ item.row.parentId }}
                      </router-link>
                    </div>
                    <div v-if="item.row.transactionType === 3">
                      <user-avatar
                        v-if="item.row.resource.resourceType === 4"
                        size="md"
                        :user="$store.getters.getUser(item.row.parentId)"
                      ></user-avatar>
                      <span v-else-if="item.row.resource.resourceType === 2"
                        >Asset-<router-link
                          class="fc-id"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          :title="
                            item.row.resource ? item.row.resource.name : ''
                          "
                          :to="{
                            path:
                              '/app/at/assets/all/' +
                              item.row.parentId +
                              '/overview/',
                          }"
                          >#{{ item.row.parentId }}</router-link
                        >
                      </span>
                      <span v-else-if="item.row.resource.resourceType === 1"
                        >Space-
                        <router-link
                          class="fc-id"
                          v-tippy="{
                            placement: 'top',
                            arrow: true,
                            animation: 'shift-away',
                          }"
                          :title="
                            item.row.resource ? item.row.resource.name : ''
                          "
                          :to="{ path: getSpaceRouteLink(item.row.resource) }"
                          >#{{ item.row.parentId }}</router-link
                        >
                      </span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column width="180" label="RETURN QUANTITY">
                  <template v-slot="issuedItem">
                    <div>
                      <el-input
                        placeholder="Quantity"
                        :min="1"
                        ref="quantityElInput"
                        :max="issuedItem.row.remainingQuantity"
                        type="number"
                        v-model="issuedItem.row.updateQuantity"
                        class="fc-input-full-border-select2"
                      ></el-input>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div
              v-else-if="
                !issueLoading && item && item.itemType.isRotating && item.id
              "
            >
              <el-table
                :data="issuedItems"
                v-loading="issueLoading"
                empty-text="This item is not issued to anyone."
                height="310"
                @selection-change="toggleReturnItemSelection"
                :default-sort="{
                  prop: 'item.storeRoom.name',
                  order: 'descending',
                }"
                class="width100 inventory-inner-table"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="item.storeRoom.name"
                  sortable
                  label="STOREROOM NAME"
                  width="190"
                ></el-table-column>
                <el-table-column
                  prop="asset.name"
                  sortable
                  label="NAME"
                  width="140"
                ></el-table-column>
                <el-table-column
                  prop="asset.serialNumber"
                  sortable
                  label="SERIAL NUMBER"
                  width="170"
                ></el-table-column>
                <el-table-column label="ISSUED TO" width="150">
                  <template v-slot="itemscope">
                    <div v-if="itemscope.row.transactionType === 2">
                      WO-<router-link
                        class="fc-id"
                        :to="{
                          path:
                            '/app/wo/orders/summary/' + itemscope.row.parentId,
                        }"
                      >
                        #{{ itemscope.row.parentId }}
                      </router-link>
                    </div>
                    <div v-if="itemscope.row.transactionType === 3">
                      <user-avatar
                        size="md"
                        :user="$store.getters.getUser(itemscope.row.parentId)"
                      ></user-avatar>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  sortable
                  prop="sysModifiedTime"
                  :formatter="getDateTimeIssuedTime"
                  label="ISSUED TIME"
                ></el-table-column>
              </el-table>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()"
              >CANCEL</el-button
            >
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="returnItem()"
              :loading="saving"
              >{{ saving ? 'Submitting...' : 'Return' }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import UserAvatar from '@/avatar/User'
import StoreRoomAvatar from '@/avatar/Storeroom'
import inventoryMixin from 'pages/Inventory/mixin/inventoryHelper'
import { getFilteredItemList } from 'pages/Inventory/InventoryUtil'
import { API } from '@facilio/api'
const transactionTypeEnum = {
  STOCK: 1,
  MANUAL: 3,
}
const transactionStateEnum = {
  ISSUE: 2,
  RETURN: 3,
}
export default {
  components: {
    UserAvatar,
    StoreRoomAvatar,
  },
  props: ['storeRoom', 'showItemIssueReturn', 'type'],
  mixins: [inventoryMixin],
  data() {
    return {
      saving: false,
      selectedItem: null,
      loading: false,
      fetchingMore: false,
      quantity: null,
      userid: null,
      workorderid: null,
      purchasedUnUsedItemList: null,
      selectedUnUsedItems: null,
      issueForm: null,
      transactionList: null,
      selectedIndTrackReturnItem: null,
      issuedItems: null,
      purLoading: true,
      editQuantityVisibility: false,
      selectedIssuedItems: [],
      item: null,
      itemList: [],
      issueLoading: false,
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  mounted() {
    this.loadItemsforStoreroom()
  },
  methods: {
    async loadItemsforStoreroom() {
      let { $getProperty } = this
      let filters = {
        storeRoom: {
          operatorId: 36,
          value: [this.storeRoom.id + ''],
        },
      }
      this.itemList = await getFilteredItemList(filters)
      this.item = this.itemList.find(
        item =>
          !$getProperty(item, 'itemType.approvalNeeded') &&
          $getProperty(item, 'itemType.consumable')
      )
      this.selectedItem = this.item?.id
      if (this.type === 'issue') {
        this.loadPurchasedUnUsedItemsList()
      } else if (this.type === 'return') {
        this.loadIssuedItem()
      }
    },
    cancelForm() {
      this.$emit('update:showItemIssueReturn', false)
    },
    savedForm() {
      this.cancelForm()
      this.$emit('refresh')
    },
    async itemIssue(itemTransactionData) {
      let { error } = await API.createRecord('itemTransactions', {
        data: itemTransactionData,
        params: {
          issue: true,
        },
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$message.success(this.$t('common.header.item_issued_successfully'))
        this.savedForm()
      }
    },
    async saveIssueForm() {
      if (!this.userid) {
        this.$message.error(this.$t('common._common.choose_user_issue'))
      } else if (this.userid && !this.item.itemType.isRotating) {
        if (
          this.quantity === null ||
          this.quantity === undefined ||
          this.quantity === '0'
        ) {
          this.$message.error(this.$t('common._common.enter_quantity'))
        }
        let itemTransactionData = {
          item: {
            id: this.item.id,
          },
          parentId: this.userid,
          issuedTo: { ouid: this.userid },
          transactionType: transactionTypeEnum.MANUAL,
          transactionState: transactionStateEnum.ISSUE,
          quantity: this.quantity,
        }
        await this.itemIssue(itemTransactionData)
      } else if (
        this.userid &&
        this.item.itemType.isRotating &&
        this.selectedUnUsedItems
      ) {
        let itemTransactionData = {
          item: {
            id: this.item.id,
          },
          parentId: this.userid,
          issuedTo: { ouid: this.userid },
          transactionType: transactionTypeEnum.MANUAL,
          transactionState: transactionStateEnum.ISSUE,
          assetIds: this.selectedUnUsedItems,
        }
        await this.itemIssue(itemTransactionData)
      } else if (
        this.workorderid &&
        !this.item.itemType.isRotating &&
        this.quantity
      ) {
        let param = {
          workorderItems: [
            {
              parentId: this.workorderid,
              item: { id: this.item.id },
              quantity: this.quantity,
            },
          ],
        }
        let self = this
        this.$http
          .post('/v2/workorderItems/addOrUpdate', param)
          .then(response => {
            self.loadWoItemParts()
            if (response.data.responseCode !== 0) {
              self.$message.success('Item Issued Successfully')
              self.savedForm()
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(() => {
            this.$message.error('Unable to update')
          })
      } else if (this.workorderid && this.item.itemType.isRotating) {
        let param = {
          workorderItems: [
            { parentId: this.workorderid, item: { id: this.item.id } },
          ],
          purchasedItems: this.selectedUnUsedItems,
        }
        let self = this
        this.$http
          .post('/v2/workorderItems/addOrUpdate', param)
          .then(response => {
            self.loadWoItemParts()
            if (response.data.responseCode !== 0) {
              self.$message.success('Item Issued Successfully')
              self.savedForm()
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(() => {
            this.$message.error('Unable to update')
          })
      }
    },
    async loadPurchasedUnUsedItemsList() {
      if (this.item && this.item.itemType.isRotating) {
        let filters = {
          rotatingItem: {
            operatorId: 36,
            value: [this.item.id + ''],
          },
          isUsed: { operatorId: 15, value: [false + ''] },
        }

        this.purLoading = true
        this.purchasedUnUsedItemList = await this.$util.getFilteredAssetList(
          filters
        )
        this.purLoading = false
      }
    },
    toggleUnUsedItemSelection(selected) {
      this.selectedUnUsedItems = selected.map(value => value.id)
    },
    toggleReturnItemSelection(selected) {
      this.selectedIndTrackReturnItem = selected
    },
    getDateTimePurchasedDate(val) {
      let value = val.purchasedDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    getDateTimeIssuedTime(val) {
      let value = val.sysModifiedTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    loadIssuedItem() {
      if (!this.item) {
        return
      }
      let self = this
      let filters = {
        item: {
          operatorId: 36,
          value: [this.item.id + ''],
        },
      }
      self.issueLoading = true
      this.$http
        .get(
          '/v2/itemTransactions/view/showItemTransactionListForReturn?showItemsForReturn=true&filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(function(response) {
          self.issueLoading = false
          self.issuedItems = response.data.result.itemTransactions
          for (let i in self.issuedItems) {
            self.$set(self.issuedItems[i], 'updateQuantity', null)
          }
        })
        .catch(function(error) {
          if (error) {
            self.issueLoading = false
            self.fetchingMore = false
          }
        })
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      }
    },
    async returnItemCall(itemTransaction, rotating) {
      let url = 'v3/modules/data/bulkCreate'
      let params = {
        data: {
          itemTransactions: itemTransaction,
        },
        moduleName: 'itemTransactions',
        params: {
          return: true,
        },
      }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$message.success(
          this.$t('common.header.item_returned_successfully')
        )
        this.loadIssuedItem()
        if (rotating) {
          this.selectedIndTrackReturnItem = null
        }
        this.savedForm()
        this.$emit('refreshInventory')
      }
    },
    async returnItem() {
      if (!this.item.itemType.isRotating) {
        let itemTransaction = []

        for (let i in this.issuedItems) {
          if (
            this.issuedItems[i].updateQuantity &&
            this.issuedItems[i].updateQuantity >
              this.issuedItems[i].remainingQuantity
          ) {
            this.$message.error(
              this.$t('common._common.return_quantity_greater_issued')
            )
            return
          }
          if (
            this.isValid(this.issuedItems[i].updateQuantity) &&
            !isNaN(this.issuedItems[i].updateQuantity)
          ) {
            let iTemp = {
              item: {
                id: this.issuedItems[i].item.id,
              },
              parentTransactionId: this.issuedItems[i].id,
              parentId: this.issuedItems[i].parentId,
              transactionType: this.issuedItems[i].transactionType,
              transactionState: transactionStateEnum.RETURN,
              requestedLineItem: this.issuedItems[i].requestedLineItem,
              quantity: parseInt(this.issuedItems[i].updateQuantity),
            }
            itemTransaction.push(iTemp)
          }
        }
        await this.returnItemCall(
          itemTransaction,
          this.$getProperty(this.item, 'itemType.isRotating', null)
        )
      } else if (this.item.itemType.isRotating) {
        let itemTransaction = []
        for (let i in this.selectedIndTrackReturnItem) {
          let temp = {
            item: {
              id: this.selectedIndTrackReturnItem[i].item.id,
            },
            parentId: this.selectedIndTrackReturnItem[i].parentId,
            parentTransactionId: this.selectedIndTrackReturnItem[i].id,
            transactionType: this.selectedIndTrackReturnItem[i].transactionType,
            transactionState: transactionStateEnum.RETURN,
            requestedLineItem: this.issuedItems[i].requestedLineItem,
            assetIds: [this.selectedIndTrackReturnItem[i].asset.id],
          }
          itemTransaction.push(temp)
        }
        await this.returnItemCall(
          itemTransaction,
          this.$getProperty(this.item, 'itemType.isRotating')
        )
      }
    },
    selectIssuedItemActions(val) {
      this.selectedIssuedItems = val
    },
    markAsFullyReturned(val) {
      val.forEach(element => {
        this.selectedIssuedItems.forEach(obj => {
          if (element.id === obj.id) {
            element.updateQuantity = element.remainingQuantity
          }
        })
      })
      this.$refs.returnTable.clearSelection()
    },
    selectedItemFill(val) {
      if (val) {
        this.item = this.itemList.find(it => val === it.id)
        if (this.type === 'issue') {
          this.loadPurchasedUnUsedItemsList()
        } else if (this.type === 'return') {
          this.loadIssuedItem()
        }
      }
    },
  },
}
</script>
