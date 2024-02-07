<template>
  <div ref="lineItems-container">
    <div
      v-if="$validation.isEmpty(details.shipmentReceivables)"
      class="fc-align-center-column height100"
    >
      <div>
        <inline-svg
          src="svgs/emptystate/data-empty"
          class="vertical-middle'"
          iconClass="icon icon-60"
        ></inline-svg>
      </div>
      <div class="fc-black3-16 self-center bold">
        {{ $t('common.products.no_receipts_available') }}
      </div>
    </div>
    <div v-else class="tr-summary-table width100 pT20 pL30 pB20">
      <div class="widget-title mL0 mB10">
        {{ $t('common.products.receivables') }}
      </div>
      <el-table
        :data="details.shipmentReceivables"
        style="width: 100%"
        height="210px"
      >
        <el-table-column
          prop="inventoryTypeEnum"
          :label="$t('common._common.inventory_type')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="details.shipmentReceivables"
          :formatter="inventoryName"
          :label="$t('common.roles.name')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="quantityReceived"
          :label="$t('common._common.quantity_received')"
          min-width="200"
        >
        </el-table-column>

        <el-table-column
          prop="receiptDate"
          :formatter="formatDate"
          :label="$t('common._common.receipt_date')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="remarks"
          :label="$t('common.wo_report.remarks')"
          min-width="200"
        >
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details'],
  methods: {
    inventoryName(val) {
      if (!isEmpty(val.toolType)) {
        return this.$getProperty(val.toolType, 'name')
      } else {
        return this.$getProperty(val.itemType, 'name')
      }
    },
    formatDate(val, prop) {
      let value = val[prop.property]
      return !isEmpty(value)
        ? this.$options.filters.formatDate(value, true)
        : ''
    },
  },
}
</script>
<style lang="scss">
.tr-summary-table {
  .el-table th.is-leaf,
  .el-table th.el-table__cell > .cell {
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
