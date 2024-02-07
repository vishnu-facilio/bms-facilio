<template>
  <div class="item-summary-table">
    <el-tabs v-model="subSection">
      <el-tab-pane
        :label="$t('common.header.transactions')"
        name="transactions"
      >
        <el-table
          :data="itemTransactionsList"
          v-loading="tLoading"
          height="250px"
          :empty-text="$t('common.products.no_transaction_yet')"
          :default-sort="{
            prop: 'sysCreatedTime',
            order: 'descending',
          }"
          class="width100 p20 pT10"
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
                  v-if="!tLoading"
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
          <el-table-column min-width="200" class-name="center-align-element">
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
      </el-tab-pane>
      <el-tab-pane
        :label="$t('common.products.purchase_order')"
        name="purchaseOrder"
        class="fc-tab-hide"
      >
        <el-table
          @cell-click="openPOOverview"
          v-loading="poLoading"
          :data="itemPO"
          :empty-text="$t('common.products.no_purchase_orders_available')"
          :default-sort="{
            prop: 'orderedTime',
            order: 'descending',
          }"
          height="250px"
          class="width100 p20 pT10"
        >
          <el-table-column
            sortable
            prop="id"
            :label="$t('common._common.id')"
            min-width="100"
          >
            <template v-slot="purchaseOrder">
              <div class="fc-id">
                {{ '#' + purchaseOrder.row.id }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            sortable
            prop="name"
            :label="$t('common.products._name')"
            min-width="200"
          ></el-table-column>
          <el-table-column
            prop="status"
            :formatter="currentModuleState"
            :label="$t('common.products.status')"
            min-width="200"
          ></el-table-column>
          <el-table-column
            sortable
            prop="totalCost"
            :label="$t('common.header._total_cost')"
            min-width="200"
          >
            <template v-slot="scope">
              <CurrencyPopOver
                v-if="checkForMultiCurrency('totalCost', metaFieldTypeMap)"
                :field="{ name: 'totalCost' }"
                :details="scope.row"
              />
              <currency v-else :value="scope.row.totalCost"></currency>
            </template>
          </el-table-column>
          <el-table-column
            prop="orderedTime"
            sortable
            :label="$t('common._common._ordered_date')"
            min-width="200"
          >
            <template v-slot="scope">
              {{
                scope.row.orderedTime !== -1
                  ? $options.filters.formatDate(scope.row.orderedTime, true)
                  : '---'
              }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import InventoryTransactionDescription from 'pages/Inventory/component/InventoryTransactionDescription'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import {
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  data() {
    return {
      subSection: 'transactions',
      itemTransactionsList: [],
      itemPO: [],
      tLoading: true,
      poLoading: true,
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  components: {
    InventoryTransactionDescription,
    CurrencyPopOver,
  },
  props: ['details'],
  async mounted() {
    this.loadItemTransactions()
    this.loadPurchaseOrder()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('purchaseOrder')
  },
  computed: {
    ...mapGetters(['getUser']),
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
    getUserName(userData) {
      return !isEmpty(userData.sysCreatedBy)
        ? this.getUser(userData.sysCreatedBy.id).name
        : '---'
    },
    getDateTime(val) {
      let value = val.sysCreatedTime
      return isEmpty(value) ? '' : this.$options.filters.formatDate(value)
    },
    openPOOverview(row, col) {
      if (isWebTabsEnabled() && col.label === 'NAME') {
        let { name } =
          findRouteForModule('purchaseorder', pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              id: row.id,
              viewname: 'all',
            },
          })
      } else {
        this.$router.push({
          path: '/app/purchase/po/' + 'all' + '/summary/' + row.id,
        })
      }
    },
    async loadPurchaseOrder() {
      this.poLoading = true
      let params = {
        inventoryType: 1,
        inventoryId: this.$getProperty(this.details, 'itemType.id'),
        storeRoomId: this.$getProperty(this.details, 'storeRoom.id'),
        filterPO: true,
      }
      let { list, error } = await API.fetchAll('purchaseorder', params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.poLoading = false
        this.itemPO = list || []
      }
    },
    async loadItemTransactions() {
      let id = this.$getProperty(this, 'details.id')
      this.tLoading = true
      let params = {
        filters: JSON.stringify({
          item: {
            operatorId: 5,
            value: [id + ''],
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
        this.tLoading = false
      }
    },
    currentModuleState(record) {
      return this.$getProperty(record, 'moduleState.displayName', '---')
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';

.widget-card .el-tabs__nav-wrap {
  padding-left: 20px;
}
</style>
