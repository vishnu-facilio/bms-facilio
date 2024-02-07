<template>
  <div class="item-summary-table">
    <div>
      <el-select
        filterable
        clearable
        :placeholder="$t('common.header.filter_by_storeroom')"
        v-model="selectedItemid"
        @change="changeItem(selectedItemid)"
        class="fc-input-full-border-select2 width200px mT20"
      >
        <el-option
          v-for="(item, index) in items"
          :key="index"
          :label="item.storeRoom.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-table
        :data="itemTransactionsList"
        v-loading="loading"
        :empty-text="$t('common.products.no_transaction_yet')"
        :default-sort="{ prop: 'sysCreatedTime', order: 'descending' }"
        class="width100 inventory-inner-table"
        height="200px"
      >
        <el-table-column
          prop="sysCreatedTime"
          sortable
          :formatter="getDateTime"
          :label="$t('common.products.transaction_time')"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="itemTransactionsList"
          :formatter="getStoreRoomName"
          sortable
          :label="$t('common.products.storeroom_name')"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="transactionStateEnum"
          :label="$t('common._common._type')"
          min-width="100"
        ></el-table-column>
        <el-table-column
          prop="quantity"
          :formatter="getQuantity"
          sortable
          :label="$t('common._common._quantity')"
          min-width="100"
          class-name="right-align-items"
        >
          <template v-slot="itemTransaction">
            <div>
              <div
                v-if="!loading"
                :title="
                  itemTransaction.row.asset
                    ? itemTransaction.row.asset.serialNumber
                    : ''
                "
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                  interactive: true,
                }"
              >
                {{ itemTransaction.row.quantity }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column min-width="150" class-name="center-align-element">
          <template v-slot="itemTransactionVal">
            <el-popover placement="top" width="350" trigger="hover">
              <div class="pL20 pR20">
                <div class="transaction-info">
                  {{ $t('common._common.initiated_by') }}
                </div>
                <div class="fc-black-14 text-left pB15 pT5">
                  {{ getUserName(itemTransactionVal.row) }}
                </div>
                <div class="transaction-info">
                  {{ $t('common.wo_report._description') }}
                </div>
                <div class="ffc-black-14 text-left pT5">
                  <InventoryTransactionDescription
                    :transaction="itemTransactionVal.row"
                  >
                  </InventoryTransactionDescription>
                </div>
              </div>
              <InlineSvg
                src="svgs/info-2"
                iconClass="icon icon-sm mR10"
                slot="reference"
              ></InlineSvg>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import inventoryMixin from 'pages/Inventory/mixin/inventoryHelper'
import InventoryTransactionDescription from 'pages/Inventory/component/InventoryTransactionDescription'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import { getFilteredItemList } from 'pages/Inventory/InventoryUtil'
export default {
  props: {
    refreshList: {
      type: Boolean,
    },
    itemType: {},
  },
  components: {
    InventoryTransactionDescription,
  },
  mixins: [inventoryMixin],
  data() {
    return {
      itemTransactionsList: [],
      selectedItem: null,
      selectedItemid: null,
      loading: false,
      items: null,
    }
  },
  computed: {
    ...mapGetters(['getUser']),
  },
  watch: {
    refreshList() {
      this.loadItem()
      this.$emit('update:refreshList', false)
    },
  },
  mounted() {
    this.loadItem()
  },
  methods: {
    getStoreRoomName(val) {
      let storeRoomName = this.$getProperty(val, 'storeRoom.name', '---')
      return storeRoomName
    },
    getQuantity(val) {
      let quantity = this.$getProperty(val, 'quantity', '---')
      return quantity
    },
    async loadItemTransactions() {
      this.loading = true
      if (this.selectedItemid) {
        let params = {
          filters: JSON.stringify({
            item: {
              operatorId: 5,
              value: [this.selectedItemid + ''],
            },
          }),
        }
        let { list, error } = await API.fetchAll('itemTransactions', params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.itemTransactionsList = list || {}
          this.loading = false
        }
      } else {
        let params = {
          filters: JSON.stringify({
            itemType: {
              operatorId: 5,
              value: [this.itemType.id + ''],
            },
          }),
        }
        let { list, error } = await API.fetchAll('itemTransactions', params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.itemTransactionsList = list || {}
          this.loading = false
        }
      }
    },
    getDateTime(val) {
      let value = val.sysCreatedTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value)
    },
    async loadItem() {
      let { itemType } = this
      let { id } = itemType || {}
      this.loading = true
      if (id) {
        let filters = {
          itemType: {
            operatorId: 36,
            value: [id + ''],
          },
        }
        let items = await getFilteredItemList(filters)
        if (!isEmpty(items)) {
          this.items = items
          this.loadItemTransactions()
        }
      }
      this.loading = false
    },
    changeItem(itemid) {
      if (itemid) {
        this.selectedItem = this.items.find(it => it.id === itemid)
      }
      this.loadItemTransactions()
    },
    getUserName(userData) {
      return !isEmpty(userData.sysCreatedBy)
        ? this.getUser(userData.sysCreatedBy.id).name
        : '---'
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
