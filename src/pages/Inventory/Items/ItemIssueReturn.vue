<template>
  <div>
    <div v-if="type === 'issue'">
      <div v-if="item.itemType.isRotating">
        <el-dialog
          :visible.sync="showItemIssueReturn"
          :title="$t('common.products.issue_item')"
          :before-close="cancelForm"
          :key="itemType.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div class="height400" v-else>
            <div v-if="item.itemType.isRotating && item.id">
              <el-table
                height="200"
                :data="purchasedUnUsedItemList"
                v-loading="purLoading"
                :empty-text="$t('common.products.no_rotating_assets_available')"
                @selection-change="toggleUnUsedItemSelection"
                :default-sort="{ prop: 'costDate', order: 'descending' }"
                class="width100 inventory-inner-table"
              >
                <el-table-column type="selection" width="60"></el-table-column>
                <el-table-column
                  prop="serialNumber"
                  sortable
                  :label="$t('common._common.serial_number')"
                ></el-table-column>
                <el-table-column sortable :label="$t('common.header._price')">
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
                  :label="$t('common.products.purchased_items')"
                ></el-table-column>
              </el-table>
              <el-row class="mT20">
                <el-col :span="12">
                  <p class="fc-input-label-txt">
                    {{ $t('common.products.issue_to') }}
                  </p>
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
            <el-button class="modal-btn-cancel" @click="cancelForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveIssueForm()"
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
      <div v-if="!item.itemType.isRotating">
        <el-dialog
          :visible.sync="showItemIssueReturn"
          :title="$t('common.products.issue_item')"
          width="40%"
          :before-close="cancelForm"
          :key="itemType.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div
            class="height300"
            v-else-if="!item.itemType.isRotating && item.id"
          >
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
                    :storeRoom="item.storeRoom"
                  ></store-room-avatar>
                </el-col>
              </el-row>
            </div>
            <el-row :gutter="10" class="mT40">
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.products.issue_quantity') }}
                </div>
                <el-input
                  :placeholder="$t('common.products.issue_quantity')"
                  :min="1"
                  :max="item.quantity"
                  type="number"
                  v-model="quantity"
                  class="fc-input-full-border2 mT5"
                >
                  <span slot="suffix" class="width100 item-input-txt">{{
                    $t('common.header.item')
                  }}</span>
                </el-input>
                <div class="green-txt2-13 mT10 fw4">
                  {{ item.quantity }} {{ $t('common.header._current_balance') }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.products.issue_to') }}
                </div>
                <el-select
                  v-model="userid"
                  filterable
                  clearable
                  :placeholder="$t('common.products.select_user')"
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
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveIssueForm()"
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

    <div v-if="type === 'adjustment'">
      <div v-if="!item.itemType.isRotating">
        <el-dialog
          :visible.sync="showItemIssueReturn"
          :title="$t('common._common.adjust_balance')"
          width="40%"
          :before-close="cancelForm"
          :key="itemType.id"
          custom-class="fc-dialog-center-container"
        >
          <div v-if="loading">
            <spinner :show="loading"></spinner>
          </div>
          <div
            class="height300"
            v-else-if="!item.itemType.isRotating && item.id"
          >
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
                    :storeRoom="item.storeRoom"
                  ></store-room-avatar>
                </el-col>
              </el-row>
            </div>
            <el-row :gutter="10" class="mT40">
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.header._new_quantity') }}
                </div>
                <el-input
                  :placeholder="$t('common.header._new_quantity')"
                  type="number"
                  v-model="quantity"
                  class="fc-input-full-border2 mT5"
                >
                  <span slot="suffix" class="width100 item-input-txt">{{
                    $t('common.header.item')
                  }}</span>
                </el-input>
                <div class="green-txt2-13 mT10 fw4" v-if="item.quantity >= 0">
                  {{ item.quantity }}
                  {{ $t('common.header._current_quantity') }}
                </div>
                <div class="green-txt2-13 mT10 fw4" v-if="item.quantity === -1">
                  {{ '___' }}
                  {{ $t('common.header._current_quantity') }}
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-black3-16 text-left fw4">
                  {{ $t('common.header.unit_price') }}
                </div>
                <td
                  v-if="checkForMultiCurrency('unitcost', metaFieldTypeMap)"
                  class="item-unit-price-currency"
                >
                  <FNewCurrencyField
                    :key="item.currencyCode"
                    v-model="price"
                    :isSubform="true"
                    :moduleData="item"
                    :disabled="this.quantity <= item.quantity"
                    @setCurrencyCodeInSubform="setCurrencyCodeInSubform"
                    @calculateExchangeRate="calculateExchangeRate"
                  ></FNewCurrencyField>
                </td>
                <el-input
                  v-else
                  :placeholder="$t('common.header.unit_price')"
                  :disabled="this.quantity <= item.quantity"
                  v-model="price"
                  class="fc-input-full-border2 mT5"
                >
                  <span slot="suffix" class="width100 item-input-txt">{{
                    $t('common.header.price')
                  }}</span>
                </el-input>
              </el-col>
            </el-row>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="cancelForm()">{{
              $t('common._common.cancel')
            }}</el-button>
            <el-button
              class="modal-btn-save"
              type="primary"
              @click="saveAdjustmentForm()"
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
    <div v-if="type === 'return'">
      <el-dialog
        :visible.sync="showItemIssueReturn"
        :fullscreen="false"
        :key="itemType.id"
        :title="$t('common._common.return_item')"
        :before-close="cancelForm"
        width="65%"
        open="top"
        custom-class="fc-dialog-center-container"
        :append-to-body="true"
      >
        <div class="height400">
          <div v-if="!itemType.isRotating">
            <div
              v-if="selectedIssuedItems.length > 0"
              @click="markAsFullyReturned(issuedItems)"
              class="rv-sum-table-click"
            >
              <img src="~assets/mark-as-fully-recieved.svg" class="mR10" />
              {{ $t('common.wo_report.mark_as_fully_returned') }}
            </div>
            <el-table
              :data="issuedItems"
              ref="returnTable"
              @selection-change="selectIssuedItemActions"
              height="300"
              :empty-text="$t('common.products.this_item_not_issued')"
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
                :label="$t('common.products.storeroom_name')"
              ></el-table-column>
              <el-table-column
                prop="remainingQuantity"
                sortable
                width="130"
                :label="$t('common._common.quantity')"
              ></el-table-column>
              <el-table-column
                prop="sysModifiedTime"
                :formatter="getDateTimeIssuedTime"
                sortable
                :label="$t('common.products.issued_time')"
              ></el-table-column>
              <el-table-column
                :label="$t('common.products.issued_to')"
                width="250"
              >
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
                        :title="item.row.resource ? item.row.resource.name : ''"
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
                        :title="item.row.resource ? item.row.resource.name : ''"
                        :to="{ path: getSpaceRouteLink(item.row.resource) }"
                        >#{{ item.row.parentId }}</router-link
                      >
                    </span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                width="180"
                :label="$t('common._common.return_quantity')"
              >
                <template v-slot="issuedItem">
                  <div>
                    <el-input
                      :placeholder="$t('common._common.quantity')"
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
          <div v-if="item.itemType.isRotating">
            <el-table
              :data="issuedItems"
              :empty-text="$t('common.products.this_item_not_issued')"
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
                :label="$t('common.products.storeroom_name')"
                width="190"
              ></el-table-column>
              <el-table-column
                prop="asset.name"
                sortable
                :label="$t('common.products.name')"
                width="140"
              ></el-table-column>
              <el-table-column
                prop="asset.serialNumber"
                sortable
                :label="$t('common._common._serial_number')"
                width="170"
              ></el-table-column>
              <el-table-column
                :label="$t('common.products.issued_to')"
                width="150"
              >
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
                        :title="item.row.resource ? item.row.resource.name : ''"
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
                        :title="item.row.resource ? item.row.resource.name : ''"
                        :to="{ path: getSpaceRouteLink(item.row.resource) }"
                        >#{{ item.row.parentId }}</router-link
                      >
                    </span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                sortable
                prop="sysModifiedTime"
                :formatter="getDateTimeIssuedTime"
                :label="$t('common.products.issued_time')"
              ></el-table-column>
            </el-table>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="returnItem()"
            :loading="saving"
            >{{
              saving
                ? $t('common._common.submitting')
                : $t('common._common.return')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import UserAvatar from '@/avatar/User'
import StoreRoomAvatar from '@/avatar/Storeroom'
import inventoryMixin from 'pages/Inventory/mixin/inventoryHelper'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  getCalculatedCurrencyValue,
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'

const transactionTypeEnum = {
  STOCK: 1,
  MANUAL: 3,
}
const transactionStateEnum = {
  ISSUE: 2,
  RETURN: 3,
  ADJUSTMENT_INCREASE: 7,
  ADJUSTMENT_DECREASE: 8,
}
const resourceTypeEnum = {
  USER: 4,
}
export default {
  props: ['itemType', 'showItemIssueReturn', 'type', 'item'],
  mixins: [inventoryMixin],
  components: {
    UserAvatar,
    StoreRoomAvatar,
    FNewCurrencyField,
  },
  data() {
    return {
      saving: false,
      selectedItem: null,
      loading: false,
      items: null,
      fetchingMore: false,
      quantity: null,
      userid: null,
      workorderid: null,
      purchasedUnUsedItemList: [],
      selectedUnUsedItems: null,
      issueForm: null,
      transactionList: null,
      selectedIndTrackReturnItem: null,
      issuedItems: [],
      purLoading: true,
      editQuantityVisibility: false,
      selectedIssuedItems: [],
      price: null,
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  async mounted() {
    if (this.type === 'issue') {
      this.loadPurchasedUnUsedItemsList()
    } else if (this.type === 'return') {
      this.loadIssuedItem()
    }
    this.metaFieldTypeMap = await getMetaFieldMapForModules('purchasedItem')
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  methods: {
    cancelForm() {
      this.$emit('update:showItemIssueReturn', false)
    },
    savedForm() {
      this.cancelForm()
      this.$emit('refresh')
    },
    async itemTransaction(itemTransactionData, transaction) {
      let param =
        transaction === 'ISSUE' ? { issue: true } : { adjustQuantity: true }
      let { error } = await API.createRecord('itemTransactions', {
        data: itemTransactionData,
        params: param,
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
        this.saving = false
      } else {
        let successMsg =
          transaction === 'ISSUE'
            ? this.$t('common.header.item_issued_successfully')
            : this.$t('common.header.item_adjusted_successfully')

        this.$message.success(successMsg)
        this.savedForm()
      }
    },
    async saveIssueForm() {
      if (!this.userid) {
        this.$message.error(this.$t('common._common.choose_user_issue'))
      } else if (this.userid && !this.itemType.isRotating) {
        if (
          this.quantity === null ||
          this.quantity === undefined ||
          this.quantity === '0'
        ) {
          this.$message.error(this.$t('common._common.enter_quantity'))
        } else {
          let itemTransactionData = {
            item: {
              id: this.item.id,
            },
            parentId: this.userid,
            issuedTo: { ouid: this.userid },
            transactionType: transactionTypeEnum.MANUAL,
            transactionState: transactionStateEnum.ISSUE,
            quantity: this.quantity,
            resource: { resourceType: resourceTypeEnum.USER },
          }
          await this.itemTransaction(itemTransactionData, 'ISSUE')
        }
      } else if (
        this.userid &&
        this.itemType.isRotating &&
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
          resource: { resourceType: resourceTypeEnum.USER },
        }
        await this.itemTransaction(itemTransactionData, 'ISSUE')
      } else if (
        this.workorderid &&
        !this.itemType.isRotating &&
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
              self.$message.success(
                this.$t('common.header.item_issued_successfully')
              )
              self.savedForm()
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(() => {
            this.$message.error(this.$t('common.header.unable_to_update'))
          })
      } else if (this.workorderid && this.itemType.isRotating) {
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
              self.$message.success(
                this.$t('common.header.item_issued_successfully')
              )
              self.savedForm()
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(() => {
            this.$message.error(this.$t('common.header.unable_to_update'))
          })
      }
    },
    async saveAdjustmentForm() {
      let itemQuantity = this.$getProperty(this.item, 'quantity', null)
      let itemId = this.$getProperty(this.item, 'id', null)
      if (!this.itemType.isRotating) {
        if (
          this.quantity === null ||
          (this.quantity === undefined && !this.quantity)
        ) {
          this.$message.error(this.$t('common._common.quantity_error'))
        } else if (this.quantity < itemQuantity) {
          let itemTransactionData = {
            item: {
              id: itemId,
            },
            transactionType: transactionTypeEnum.STOCK,
            transactionState: transactionStateEnum.ADJUSTMENT_DECREASE,
            quantity: itemQuantity - this.quantity,
          }
          await this.itemTransaction(itemTransactionData, 'ADJUST_QUANTITY')
        } else if (this.quantity > itemQuantity) {
          if (isEmpty(this.price)) {
            this.$message.error(this.$t('common._common.unit_price_error'))
          } else {
            let qua = 0

            if (itemQuantity >= 0) {
              qua = itemQuantity
            } else {
              qua = 0
            }
            let { quantity } = this
            quantity = quantity - qua
            let itemTransactionData = {
              item: {
                id: itemId,
              },
              purchasedItem: {
                unitcost: this.price,
                quantity,
              },
              transactionType: transactionTypeEnum.STOCK,
              transactionState: transactionStateEnum.ADJUSTMENT_INCREASE,
              quantity,
            }
            await this.itemTransaction(itemTransactionData, 'ADJUST_QUANTITY')
          }
        } else {
          this.$message.error('Unable to Adjust with the same Quantity')
        }
      }
    },
    async loadPurchasedUnUsedItemsList() {
      if (this.item.itemType.isRotating) {
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
      let self = this
      let filters = {
        item: {
          operatorId: 36,
          value: [this.item.id + ''],
        },
      }
      self.loading = true
      this.$http
        .get(
          '/v2/itemTransactions/view/showItemTransactionListForReturn?showItemsForReturn=true&filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(function(response) {
          self.loading = false
          self.issuedItems = response.data.result.itemTransactions
          for (let i in self.issuedItems) {
            self.$set(self.issuedItems[i], 'updateQuantity', null)
          }
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.fetchingMore = false
          }
        })
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      }
    },
    async returnItem() {
      if (!this.itemType.isRotating) {
        let itemTransaction = []

        for (let i in this.issuedItems) {
          if (
            this.isValid(this.issuedItems[i].updateQuantity) &&
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
              quantity: parseInt(this.issuedItems[i].updateQuantity),
            }
            itemTransaction.push(iTemp)
          }
        }
        let isRotating = this.$getProperty(this.itemType, 'isRotating', null)

        await this.returnItemCall(itemTransaction, isRotating)
      } else if (this.itemType.isRotating) {
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
            assetIds: [this.selectedIndTrackReturnItem[i].asset.id],
          }
          itemTransaction.push(temp)
        }
        let isRotating = this.$getProperty(this.itemType, 'isRotating')

        await this.returnItemCall(itemTransaction, isRotating)
      }
    },
    async returnItemCall(itemTransaction, rotating) {
      if (itemTransaction.length < 1) {
        this.$message.error(this.$t('common._common.return_error'))
      } else {
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
        }
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
    setCurrencyCodeInSubform(currencyCode, exchangeRate) {
      let { purchasedItem } = this.item || {}
      this.$setProperty(this.item, 'purchasedItem', {
        ...(purchasedItem || {}),
        currencyCode,
        exchangeRate,
      })
    },
    calculateExchangeRate(rateObj) {
      let { purchasedItem } = this.item || {}
      purchasedItem.unitcost = getCalculatedCurrencyValue(
        rateObj,
        purchasedItem.unitcost
      )
      this.$setProperty(this.item, `purchasedItem`, {
        ...(purchasedItem || {}),
      })
    },
  },
}
</script>
<style scoped>
.item-unit-price-currency {
  padding: 5px 0px 0px;
  width: 20%;
}
</style>
