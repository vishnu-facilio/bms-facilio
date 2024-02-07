<template>
  <div class="item-summary-table">
    <el-tabs v-model="toolsTab">
      <el-tab-pane
        :label="$t('common.header.transactions')"
        name="transactionsTool"
      >
        <el-table
          v-loading="tLoading"
          :data="toolTransactionsList"
          :empty-text="$t('common.products.no_transaction_yet')"
          :default-sort="{
            prop: 'sysCreatedTime',
            order: 'descending',
          }"
          height="250px"
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
            prop="toolTransactionsList"
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
          ></el-table-column>
          <el-table-column min-width="200" class-name="center-align-element">
            <template v-slot="toolTransactionVal">
              <el-popover placement="top" width="350" trigger="hover">
                <div class="pL20 pR20">
                  <div class="transaction-info">
                    {{ $t('common._common.initiated_by') }}
                  </div>
                  <div class="fc-black-14 text-left pB15 pT5">
                    {{ getUserName(toolTransactionVal.row) }}
                  </div>
                  <div class="transaction-info">
                    {{ $t('common.wo_report._description') }}
                  </div>
                  <div class="ffc-black-14 text-left pT5">
                    <InventoryTransactionDescription
                      :transaction="toolTransactionVal.row"
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
          :data="toolPO"
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
                :field="{ name: 'totalCost' }"
                :details="scope.row"
              />
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
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import InventoryTransactionDescription from 'pages/Inventory/component/InventoryTransactionDescription'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  data() {
    return {
      toolsTab: 'transactionsTool',
      tLoading: true,
      toolTransactionsList: [],
      poLoading: true,
      toolPO: [],
    }
  },
  watch: {
    toolsTab: {
      handler() {
        if (this.toolsTab === 'purchaseOrder') {
          this.loadPurchaseOrder()
        }
      },
      immediate: true,
    },
  },
  components: {
    InventoryTransactionDescription,
    CurrencyPopOver,
  },
  props: ['details'],
  mounted() {
    this.loadToolTransactions()
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
    async loadToolTransactions() {
      this.tLoading = true
      let toolId = this.$getProperty(this.details, 'id')
      let params = {
        filters: JSON.stringify({
          tool: {
            operatorId: 36,
            value: [toolId + ''],
          },
        }),
      }
      let { list, error } = await API.fetchAll('toolTransactions', params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.toolTransactionsList = list || {}
        this.tLoading = false
      }
    },
    openPOOverview(row, col) {
      if (col.label !== 'NAME') {
        return
      }
      if (isWebTabsEnabled()) {
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
        inventoryType: 2,
        inventoryId: this.$getProperty(this.details, 'toolType.id'),
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
        this.toolPO = list || []
      }
    },
    getDateTime(val) {
      let value = val.sysCreatedTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value)
    },
    getUserName(userData) {
      return !isEmpty(userData.sysCreatedBy)
        ? this.getUser(userData.sysCreatedBy.id).name
        : '---'
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
