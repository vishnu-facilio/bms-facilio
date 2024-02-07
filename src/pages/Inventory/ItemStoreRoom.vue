<template>
  <div class="item-summary-table">
    <el-table
      :data="items"
      v-loading="loading"
      @cell-click="openStoreRoomOverview"
      class="width100 inventory-inner-table"
      :empty-text="
        $t('common.products.this_item_is_not_available_in_storeroom')
      "
      :default-sort="{ prop: 'storeRoom.name', order: 'descending' }"
      :sortable="false"
      height="250px"
    >
      <el-table-column
        prop="storeRoom.name"
        sortable
        :label="$t('common.products.storeroom_name')"
        min-width="160"
      >
        <template v-slot="storeRoomAvatar">
          <store-room-avatar
            name="true"
            size="lg"
            :storeRoom="storeRoomAvatar.row.storeRoom"
          ></store-room-avatar>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('common.wo_report._description')"
        min-width="160"
      >
        <template v-slot="desc">
          <div class="textoverflow-ellipsis width240px">
            {{ getDescription(desc) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="quantity"
        sortable
        :label="$t('common.header.current_balance')"
        :formatter="tableQuantityFormatter"
        min-width="100"
        class-name="right-align-items"
      ></el-table-column>
      <el-table-column
        v-if="!itemType.isRotating"
        prop="costTypeEnum"
        :label="$t('common._common.cost_type')"
        min-width="200"
        class-name="cost-type"
      ></el-table-column>
    </el-table>
    <el-dialog
      :visible.sync="purchasedItemDialogMeta.visibility"
      :title="$t('common.products.purchased_items')"
      :fullscreen="false"
      open="top"
      custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
      :append-to-body="true"
    >
      <div>
        <div v-if="itemType.isRotaing">
          <el-table
            v-loading="purLoading"
            :data="purchasedItemList"
            :empty-text="$t('common.products.no_purchased_items_available')"
            :default-sort="{ prop: 'costDate', order: 'descending' }"
            class="inventory-inner-table widht100"
            style="width: 100%;"
          >
            <el-table-column
              prop="serialNumber"
              sortable
              :label="$t('common._common._serial_number')"
            ></el-table-column>
            <el-table-column
              sortable
              prop="unitcost"
              :label="$t('common.header._price')"
              width="110"
            >
              <template v-slot="scope">
                <currency
                  :value="scope.row.unitcost > 0 ? scope.row.unitcost : 0"
                ></currency>
              </template>
            </el-table-column>
            <el-table-column
              prop="used"
              :formatter="isUsedHandle"
              :label="$t('common._common.is_used')"
              width="100"
            ></el-table-column>
            <el-table-column
              sortable
              prop="costDate"
              :formatter="getDateTime"
              :label="$t('common.header.purchased_date_')"
            ></el-table-column>
            <el-table-column width="60">
              <template v-slot="purItemIndTrack">
                <div
                  v-if="!purItemIndTrack.row.isUsed"
                  class="actions visibility-hide-actions"
                  style="margin-top:-3px;margin-right: 15px;text-align:center;"
                >
                  <i
                    class="el-icon-edit pointer edit-icon-color"
                    :title="$t('common.header.edit_purchased_item')"
                    data-arrow="true"
                    v-tippy
                    @click="editPurchasedItem(purItemIndTrack.row)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-if="!itemType.isRotaing">
          <el-table
            :data="purchasedItemList"
            :empty-text="$t('common.products.no_purchased_items_available')"
            :default-sort="{ prop: 'costDate', order: 'descending' }"
            class="width100"
          >
            <el-table-column
              prop="quantity"
              sortable
              :label="$t('common.header.purchased_quantity')"
              width="200"
            ></el-table-column>
            <el-table-column
              prop="currentQuantity"
              sortable
              :label="$t('common.header.current_balance')"
            ></el-table-column>
            <el-table-column
              prop="unitcost"
              sortable
              :label="$t('common.header._unit_price')"
            >
              <template v-slot="scope">
                <currency
                  :value="scope.row.unitcost > 0 ? scope.row.unitcost : 0"
                ></currency>
              </template>
            </el-table-column>
            <el-table-column
              prop="costDate"
              sortable
              :formatter="getDateTime"
              :label="$t('common.header.purchased_date_')"
            ></el-table-column>
          </el-table>
        </div>
      </div>
      <div class="modal-dialog-footer-parts-dialog"></div>
    </el-dialog>
    <div v-if="editPurchasedItemDialog" :key="editPurchaseItemObj.id">
      <el-dialog
        :visible.sync="editPurchasedItemDialog"
        :before-close="closeEditPurchaseItemDialog"
        :title="$t('common.header.edit_purchased_item')"
        :fullscreen="false"
        width="25%"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog purchase-item-edit-dialog"
        :append-to-body="true"
      >
        <div class="">
          <el-form>
            <p class="fc-input-label-txt">
              {{ $t('common._common.serial_number') }}
            </p>
            <el-form-item>
              <el-input
                :placeholder="$t('common._common.serial_number')"
                v-model="editPurchaseItemObj.serialNumber"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
            <p class="fc-input-label-txt">
              {{ $t('common.header.unit_price') }}
            </p>
            <el-form-item>
              <el-input
                :placeholder="$t('common.header.price')"
                type="number"
                v-model="editPurchaseItemObj.unitcost"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-save width100"
            type="primary"
            @click="savePurchasedItem()"
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
import StoreRoomAvatar from '@/avatar/Storeroom'
import { getFilteredItemList } from 'pages/Inventory/InventoryUtil'
import { isEmpty } from '@facilio/utils/validation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  components: {
    StoreRoomAvatar,
  },
  props: {
    itemTypeId: {
      type: Number,
    },
    refreshList: {
      type: Boolean,
    },
    itemType: {},
  },
  data() {
    return {
      loading: true,
      fetchingMore: false,
      purchasedItemDialogMeta: {
        visibility: false,
      },
      purchasedItemList: null,
      purLoading: true,
      editPurchasedItemDialog: false,
      editPurchaseItemObj: null,
      saving: false,
      items: [],
    }
  },
  mounted() {
    this.loadItemStoreRoom()
  },
  watch: {
    refreshList() {
      this.loadItemStoreRoom()
      this.$emit('update:refreshList', false)
    },
  },
  methods: {
    getDescription(desc) {
      return this.$getProperty(desc, 'row.storeRoom.description', '---')
    },
    async loadItemStoreRoom() {
      let filters = {
        itemType: {
          operatorId: 36,
          value: [this.itemTypeId + ''],
        },
      }

      this.loading = true
      if (this.itemType.id) {
        this.items = await getFilteredItemList(filters)
        this.loading = false
        this.fetchingMore = false
      }
    },
    openPurchasedItemsDialog(item) {
      this.purchasedItemDialogMeta = {
        visibility: true,
      }
      this.loadPurchasedItemList(item)
    },
    cancelPurchaseItemsDialog() {
      this.purchasedItemDialogMeta.visibility = false
    },
    loadPurchasedItemList(item) {
      let self = this
      self.purLoading = true
      self.$http
        .get('v2/purchasedItemsList/item/' + item.id)
        .then(function(response) {
          self.loading = false
          if (response.data.responseCode === 0) {
            self.purchasedItemList = response.data.result.purchasedItem
            self.purLoading = false
          }
        })
    },
    getDateTime(val) {
      let value = val.costDate
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value, true)
    },
    openStoreRoomOverview(row, col) {
      if (col.label !== 'STOREROOM NAME') {
        return
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('item', pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id: row.id,
            },
          })
      } else {
        this.$router.push({
          path: '/app/inventory/item/' + 'all/' + row.id + '/summary',
        })
      }
    },
    isUsedHandle(val) {
      if (val.used) {
        return 'Yes'
      } else {
        return 'No'
      }
    },
    editPurchasedItem(purItem) {
      this.editPurchasedItemDialog = true
      this.editPurchaseItemObj = {
        id: purItem.id,
        serialNumber: purItem.serialNumber,
        unitcost: purItem.unitcost,
        item: { id: purItem.item.id },
      }
    },
    closeEditPurchaseItemDialog() {
      this.editPurchasedItemDialog = false
    },
    savePurchasedItem() {
      let self = this
      let param = {
        itemId: this.editPurchaseItemObj.item.id,
        purchasedItems: [
          {
            id: this.editPurchaseItemObj.id,
            serialNumber: this.editPurchaseItemObj.serialNumber,
            unitcost: this.editPurchaseItemObj.unitcost,
          },
        ],
      }
      self.$http
        .post('v2/purchasedItem/update', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common.header.purchased_item_edit_successfully')
            )
            self.editPurchasedItemDialog = false
            self.loadPurchasedItemList(self.editPurchaseItemObj.item)
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(() => {
          self.$message.error(this.$t('common.wo_report.unable_to_edit'))
        })
    },
    tableQuantityFormatter(val, prop) {
      if (isEmpty(val[prop.property])) {
        return 0
      } else {
        return val[prop.property]
      }
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
