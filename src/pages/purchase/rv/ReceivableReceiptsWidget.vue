<template>
  <div ref="lineItems-container">
    <div
      v-if="$validation.isEmpty(receipts)"
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
        {{ $t('common.products.receipts') }}
      </div>
      <el-table :data="receipts" style="width: 100%" height="210px">
        <el-table-column :label="$t('common.products.line_item')">
          <template v-slot="recData">
            <tool-avatar
              name="true"
              size="lg"
              module="item"
              :recordData="recData.row.lineItem.itemType"
              v-if="
                recData.row.lineItem.inventoryType === inventoryTypes.ITEM_TYPE
              "
            ></tool-avatar>
            <item-avatar
              v-else
              name="true"
              size="lg"
              :module="getModuleAndDataKey(recData.row.lineItem).module"
              :recordData="getModuleAndDataKey(recData.row.lineItem).data"
            ></item-avatar>
          </template>
        </el-table-column>
        <el-table-column
          sortable
          :label="$t('common._common._quantity')"
          prop="quantity"
        ></el-table-column>
        <el-table-column
          sortable
          :label="$t('common.wo_report.time')"
          :formatter="getDateTime"
          prop="receiptTime"
        ></el-table-column>
        <el-table-column
          :label="$t('common.products.status')"
          :formatter="getReceiptStatus"
          prop="statusEnum"
        ></el-table-column>
        <el-table-column
          :label="$t('common.wo_report.remarks')"
          prop="remarks"
        ></el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import ToolAvatar from '@/avatar/ItemTool'
import ItemAvatar from '@/avatar/ItemTool'
import { isEmpty } from '@facilio/utils/validation'
const inventoryTypes = {
  ITEM_TYPE: 1,
  TOOL_TYPE: 2,
  SERVICE: 3,
  DESCRIPTION: 4,
}
export default {
  props: ['details'],
  components: {
    ToolAvatar,
    ItemAvatar,
  },
  data() {
    return {
      receipts: [],
      inventoryTypes,
    }
  },
  created() {
    this.loadReceipts()
  },
  computed: {
    getReceiptStatus(val) {
      let ReceiptStatus = {
        1: 'Received',
        2: 'Returned',
      }
      let { status } = val || {}
      return ReceiptStatus[status]
    },
  },
  methods: {
    getModuleAndDataKey(recordData) {
      let { inventoryType } = recordData || {}

      let { TOOL_TYPE, SERVICE, DESCRIPTION } = inventoryTypes || {}

      let moduleAndDataKeyVsInventoryType = {
        [TOOL_TYPE]: { module: 'tool', data: recordData.toolType },
        [SERVICE]: { module: 'service', data: recordData.service },
        [DESCRIPTION]: { module: 'others', data: recordData.description },
      }

      return moduleAndDataKeyVsInventoryType[inventoryType] || {}
    },
    getDateTime(val) {
      let value = val.receiptTime
      return !isEmpty(value) ? this.$options.filters.formatDate(value) : ''
    },
    async loadReceipts() {
      let param = {
        receivableId: this.details.id,
      }
      let { list, error } = await API.fetchAll('receipts', param)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.receipts = list || []
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.tr-summary-table {
  .el-table th.is-leaf,
  .el-table th.el-table__cell > .cell {
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
