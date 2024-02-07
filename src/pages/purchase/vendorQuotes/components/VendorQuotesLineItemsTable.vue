<template>
  <div ref="vendorQuotesLineItemTable" class="vendor-quotes-lineitems-table">
    <div
      v-if="loading"
      class="section-items f-quote-lineitems flex-middle flex-col"
    >
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else>
      <div>
        <el-table :data="vendorQuotesLineItemsData" max-height="280" border>
          <el-table-column
            :label="$t('common.header.item_description')"
            min-width="192"
            fixed="left"
            class-name="item-name-vq"
          >
            <template slot-scope="scope">
              <div class="flex flex-no-wrap">
                <fc-icon
                  class="mT5 mR5"
                  group="dsm"
                  name="line-item"
                  size="12"
                ></fc-icon>
                <div class="flex flex-direction-column">
                  <span class="fwBold">{{ getItemName(scope.row) }}</span>
                  {{ getItemDescription(scope.row) }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="quantity"
            :label="$t('common._common.quantity')"
            class-name="right-align-elements"
            min-width="128"
          >
          </el-table-column>
          <el-table-column
            :formatter="uom"
            :label="$t('quotation.common.uom')"
            min-width="96"
          >
          </el-table-column>
          <!-- <el-table-column
        prop="unitPrice"
        :formatter="getUnitPrice"
        :label="`${$t('common.header.unit_price')} (${$currency})`"
        class-name="custom-class-price right-align-elements"
        min-width="150"
      >
      </el-table-column> -->
          <el-table-column
            prop="counterPrice"
            :formatter="getCounterPrice"
            :label="
              `${$t('common.inventory.quoted_unit_price')} (${$currency})`
            "
            class-name="custom-class-price right-align-elements"
            min-width="220px"
          >
          </el-table-column>
          <el-table-column
            :formatter="getTaxPercentage"
            :label="`${$t('common.inventory.tax_percentage')} (%)`"
            class-name="right-align-elements"
            min-width="200"
          >
          </el-table-column>
          <el-table-column
            :formatter="getTaxAmount"
            :label="`${$t('common.inventory.tax_amount')} (${$currency})`"
            class-name="right-align-elements"
            min-width="164"
          >
          </el-table-column>
          <el-table-column
            prop="amount"
            fixed="right"
            :formatter="getAmount"
            :label="`${$t('common.inventory.quoted_amount')} (${$currency})`"
            class-name="right-align-elements"
            width="192"
          >
          </el-table-column>
          <el-table-column
            prop="remarks"
            :formatter="getRemarks"
            :label="$t('common.wo_report._remarks')"
            min-width="128"
          >
          </el-table-column>
        </el-table>
      </div>

      <div class="height50 total-amount mR20">
        <div class="pT15 pR50 text-right">
          {{ `${$t('common.inventory.total_quoted_amount')} (${$currency})` }}
        </div>
        <div class="pT15">
          {{ totalQuotedAmount(vendorQuotesLineItemsData) }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import RfqMixin from 'src/pages/purchase/rfq/mixins/RfqMixin'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'

export default {
  props: ['vendorQuotesLineItemsData'],
  mixins: [RfqMixin, LineItemsMixin],
  async created() {
    this.loading = true
    await this.loadTaxes(this.vendorQuotesLineItemsData)
    this.loading = false
  },

  methods: {
    isLineItemAwarded(lineItem) {
      return lineItem?.isLineItemAwarded
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
</style>
