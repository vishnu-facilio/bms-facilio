<template>
  <div>
    <div class="width100">
      <div class="fc-table-td-height">
        <el-table
          :data="itemTransactions"
          v-loading="loading"
          :empty-text="$t('common.products.no_transaction_yet')"
          :default-sort="{ prop: 'sysCreatedTime', order: 'descending' }"
          :fit="true"
          height="380"
        >
          <template slot="empty">
            <div class="mT40">
              <InlineSvg
                src="svgs/emptystate/readings-empty"
                iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
              ></InlineSvg>
              <div class="pT20 fc-black-dark f18 bold">
                {{ $t('asset.assets.no_transactions_available') }}
              </div>
            </div>
          </template>
          <el-table-column
            prop="sysCreatedTime"
            sortable
            :formatter="getDateTime"
            :label="$t('common.products.transaction_time')"
            width="200"
          ></el-table-column>
          <el-table-column
            prop="itemTransactions"
            :formatter="getStoreRoomName"
            sortable
            :label="$t('common.products.storeroom_name')"
            width="200"
          ></el-table-column>
          <el-table-column
            prop="transactionStateEnum"
            :label="$t('common._common._type')"
          ></el-table-column>
          <el-table-column
            prop="quantity"
            sortable
            :label="$t('common._common._quantity')"
          >
            <template v-slot="itemTransaction">
              <div>
                <div
                  v-if="!loading"
                  :title="
                    $getProperty(itemTransaction, 'row.asset.serialNumber', '')
                  "
                  v-tippy="{
                    placement: 'top',
                    animation: 'shift-away',
                    arrow: true,
                    interactive: true,
                    show: (itemTransaction.row.asset || {}).serialNumber,
                  }"
                >
                  {{ itemTransaction.row.quantity }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.wo_report._description')">
            <template v-slot="itemTransactionVal">
              <div>
                <InventoryTransactionDescription
                  :transaction="itemTransactionVal.row"
                ></InventoryTransactionDescription>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <portal :to="widget.key + '-title-section'">
        <div
          class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
        >
          <div class="widget-header-name">
            {{ $t('asset.assets.item_transactions') }}
          </div>
        </div>
      </portal>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import InventoryTransactionDescription from 'pages/Inventory/component/InventoryTransactionDescription'
export default {
  props: ['details', 'widget'],
  components: {
    InventoryTransactionDescription,
  },
  data() {
    return {
      loading: true,
      itemTransactions: [],
    }
  },
  created() {
    this.init()
  },
  methods: {
    getStoreRoomName(val) {
      let storeRoomName = this.$getProperty(val, 'storeRoom.name', '---')
      return storeRoomName
    },
    async init() {
      let params = {
        filters: JSON.stringify({
          asset: {
            operatorId: 36,
            value: [String((this.details || {}).id)],
          },
        }),
      }
      let { list, error } = await API.fetchAll('itemTransactions', params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.itemTransactions = list || {}
        this.loading = false
      }
    },
    getDateTime(val, prop) {
      let value = val[prop.property]
      return value && value > -1 ? this.$options.filters.formatDate(value) : ''
    },
  },
}
</script>
