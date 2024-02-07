<template>
  <el-dialog
    :visible.sync="visibility"
    :before-close="closeDialog"
    :append-to-body="true"
    width="90%"
    :title="$t('common.products.add_quote')"
    custom-class="add-vendorquote-dialog setup-dialog90 setup-dialog fc-web-form-dialog"
  >
    <div
      v-if="loading"
      class="section-items f-quote-lineitems flex-middle flex-col"
    >
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else>
      <div class="fc-pm-main-content-H pL20">
        {{ $t('common.products.add_quote') }}
      </div>
      <div class="vendor-quotes-lineitems-table">
        <el-table :data="lineItems" max-height="280" border>
          <el-table-column
            :label="$t('common.header.item_description')"
            width="192"
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
            width="128"
          >
          </el-table-column>
          <el-table-column
            :formatter="uom"
            :label="$t('quotation.common.uom')"
            width="96"
          >
          </el-table-column>
          <el-table-column
            prop="counterPrice"
            :label="
              `${$t('common.inventory.quoted_unit_price')} (${$currency})`
            "
            width="208"
          >
            <template v-slot="lineItem">
              <el-input
                :min="0"
                type="number"
                v-model="lineItem.row.counterPrice"
                class="fc-input-full-border-select2 duration-input"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column
            prop="tax"
            :label="`${$t('common.products.tax')} (%)`"
            min-width="126"
          >
            <template v-slot="lineItem">
              <el-select
                filterable
                clearable
                v-model="lineItem.row.tax.id"
                class="width100 fc-input-full-border2"
              >
                <el-option-group
                  key="1"
                  :label="$t('common.products.tax_group')"
                >
                  <el-option
                    v-for="(tax, index) in groupedTaxes"
                    :key="index"
                    :label="taxLabel(tax)"
                    :value="tax.id"
                  ></el-option>
                </el-option-group>
                <el-option-group key="2" :label="individualTaxesLable">
                  <el-option
                    v-for="(tax, index) in individualTaxes"
                    :key="index"
                    :label="taxLabel(tax)"
                    :value="tax.id"
                  ></el-option>
                </el-option-group>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column
            prop="taxAmount"
            :formatter="calculateTaxAmount"
            :label="`${$t('common.inventory.tax_amount')} (${$currency})`"
            class-name="right-align-elements"
            width="164"
          >
          </el-table-column>
          <el-table-column
            prop="amount"
            :formatter="getAmount"
            :label="`${$t('common.inventory.quoted_amount')} (${$currency})`"
            class-name="right-align-elements"
            min-width="200"
          >
          </el-table-column>
          <el-table-column
            prop="remarks"
            :label="$t('common.wo_report._remarks')"
            min-width="128"
          >
            <template v-slot="lineItem">
              <el-input
                type="text"
                v-model="lineItem.row.remarks"
                class="fc-input-full-border-select2 duration-input"
              ></el-input>
            </template>
          </el-table-column>
        </el-table>
        <div class="height50 total-amount total-amount-right-offset">
          <div class="pT15 text-right">
            {{ `${$t('common.inventory.total_quoted_amount')} (${$currency})` }}
            {{ totalQuotedAmount(lineItems) }}
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          :loading="isButtonLoading"
          @click="saveRecord"
          >{{ $t('common._common._save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import RfqMixin from 'src/pages/purchase/rfq/mixins/RfqMixin.js'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'

export default {
  props: ['record', 'visibility', 'moduleName'],
  created() {
    this.init()
  },
  data() {
    return {
      isButtonLoading: false,
      lineItems: [],
      loading: false,
    }
  },
  mixins: [RfqMixin, LineItemsMixin],
  computed: {
    individualTaxesLable() {
      let { groupedTaxes } = this
      return groupedTaxes.length > 0 ? 'Individual Taxes' : ''
    },
  },
  methods: {
    async init() {
      this.loading = true
      await this.setLineItems()
      await this.addDefaultValuesForLineItems()
      await this.loadTaxes(this.lineItems)
      this.loading = false
    },
    setLineItems() {
      let { record: { vendorQuotesLineItems } = [] } = this || []
      this.$set(this, 'lineItems', deepCloneObject(vendorQuotesLineItems))
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    taxLabel(tax) {
      return tax.isActive
        ? `${tax.name} (${tax.rate} %)`
        : `${tax.name}* (${tax.rate} %)`
    },
    totalQuotedAmount(vendorQuotesLineItems) {
      let totalAmount = this.$d3.sum(
        vendorQuotesLineItems.map(vendorQuoteLineItem => {
          let { quantity, counterPrice, taxAmount } = vendorQuoteLineItem || {}
          let amount = quantity * counterPrice
          if (!isEmpty(taxAmount)) {
            amount += taxAmount
          }
          return !isEmpty(amount) ? amount : '---'
        })
      )
      return totalAmount ? this.$d3.format(',.2f')(totalAmount) : '---'
    },
    async addDefaultValuesForLineItems() {
      let { lineItems } = this || {}
      if (!isEmpty(lineItems)) {
        lineItems.forEach(lineItem => {
          let tax = this.$getProperty(lineItem, 'tax')
          if (isEmpty(tax)) {
            this.$set(lineItem, 'tax', { id: null })
          }
        })
        this.$set(this, 'lineItems', lineItems)
      }
    },
    async saveRecord() {
      this.isButtonLoading = true
      let param = {
        id: this.$getProperty(this.record, 'id'),
        data: {
          relations: {
            vendorQuotesLineItems: [
              { data: this.$getProperty(this, 'lineItems') },
            ],
          },
        },
      }
      let { error } = await API.updateRecord(this.moduleName, param)
      if (error) {
        this.$message.error(
          error.message ||
            this.$t('common._common.error_while_adding_quote_details')
        )
      } else {
        let successMsg = this.$t('common.inventory.quote_added_success_msg')
        this.$message.success(successMsg)
        this.closeDialog()
        this.$emit('saved')
      }
      this.isButtonLoading = false
    },
  },
}
</script>
<style lang="scss">
.rfq-award-vendors-table {
  .el-table th.is-leaf,
  .el-table th.el-table__cell > .cell {
    padding-left: 0;
    padding-right: 0;
  }
  .el-table th.el-table__cell {
    color: #2f4058;
  }
  .el-table--enable-row-transition .el-table__body td.el-table__cell {
    padding-right: 20px;
  }
}
.add-vendorquote-dialog {
  height: 60%;
  right: 0;
  left: 0;
  position: absolute;
  overflow-y: hidden;
  border-radius: 8px;
  .el-dialog__body {
    padding: 0px 0px 20px 0px;
  }
  .total-amount-right-offset {
    margin-right: 162px;
  }
}
</style>
