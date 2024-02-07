<template>
  <div class="disable-actions" :class="{ active: canDisableClickActions }">
    <div class="empty-space-between"></div>
    <div
      class="disabled-row"
      :class="{
        active: canDisableMarkAll(vendorQuote),
      }"
    >
      <div class="flex justify-content-space">
        <div class="vendor-name">
          <div class="mR10">
            <el-checkbox
              v-model="checked"
              @change="markAll(vendorQuote, checked)"
              :disabled="canDisableMarkAll(vendorQuote)"
            >
            </el-checkbox>
          </div>
          <div class="flex">
            <fc-icon
              class="fc-icon-vendor"
              size="12"
              group="webtabs"
              name="vendor"
            ></fc-icon>
            {{ getVendorName(vendorQuote) }}
          </div>
        </div>
        <div v-if="lineItemsCheckedCount" class="width15 awarded-Items">
          <div class="mT15 mL15">
            {{ $t('common._common.awarded') }}
            <div class="fwBold">
              {{ showAwardedInVendor(vendorQuote) }}
            </div>
          </div>
        </div>
      </div>
      <div class="vendor-quotes-lineitems-table">
        <el-table
          :data="getVendorQuotesLineItems(vendorQuote)"
          :row-class-name="getDisabledRowClassName"
          border
        >
          <el-table-column
            :label="$t('common.header.award')"
            width="100"
            class="mL5"
            class-name="award-column"
          >
            <template v-slot="vendorQuoteLineItem">
              <el-checkbox
                v-model="vendorQuoteLineItem.row.checked"
                @change="setMarkedLineItems(vendorQuoteLineItem.row.checked)"
                :disabled="checkIsDisabled(vendorQuoteLineItem.row)"
              ></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.header.item_description')"
            min-width="192"
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
            :formatter="uom"
            :label="$t('quotation.common.uom')"
            min-width="76"
          >
          </el-table-column>
          <el-table-column
            prop="quantity"
            :label="$t('common._common.quantity')"
            class-name="right-align-elements"
            min-width="128"
          >
          </el-table-column>
          <!-- <el-table-column
          prop="unitPrice"
          :formatter="getUnitPrice"
          :label="`${$t('common.header.unit_price')} (${$currency})`"
          class-name="custom-class-price right-align-elements"
          width="150"
        >
        </el-table-column> -->
          <el-table-column
            prop="counterPrice"
            :formatter="getCounterPrice"
            :label="
              `${$t('common.inventory.quoted_unit_price')} (${$currency})`
            "
            class-name="custom-class-price right-align-elements"
            min-width="192"
          >
          </el-table-column>
          <el-table-column
            :formatter="getTaxPercentage"
            :label="`${$t('common.inventory.tax_percentage')} (%)`"
            min-width="164"
          >
          </el-table-column>
          <el-table-column
            :formatter="getTaxAmount"
            :label="`${$t('common.inventory.tax_amount')} (${$currency})`"
            min-width="148"
          >
          </el-table-column>
          <el-table-column
            prop="amount"
            :formatter="getAmount"
            :label="`${$t('common.inventory.quoted_amount')} (${$currency})`"
            class-name="right-align-elements "
            min-width="164"
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
      <div class="height50 total-amount ">
        <div class="pT15 pR50 text-right">
          {{ `${$t('common.inventory.total_quoted_amount')} (${$currency})` }}
        </div>
        <div class="pT15">
          {{ getTotalAmount(vendorQuote) }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import RfqMixin from '../mixins/RfqMixin'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'

export default {
  props: [
    'isAwarded',
    'vendorQuote',
    'lineItemsCheckedCount',
    'vendorQuotes',
    'lineItemTotalCount',
    'lineItemsChecked',
    'taxDetails',
  ],
  mixins: [RfqMixin, LineItemsMixin],
  data() {
    return {
      checked: false,
    }
  },
  created() {
    this.setTaxes()
    this.setMarkedLineItems()
  },
  watch: {
    lineItemsCheckedCount(newVal, oldVal) {
      if (oldVal != newVal) {
        this.setMarkAll()
      }
    },
    taxDetails(val) {
      this.$setProperty(this, 'allTaxes', val)
    },
  },
  computed: {
    canDisableClickActions() {
      let { isAwarded } = this || {}
      return isAwarded
    },
  },
  methods: {
    setTaxes() {
      let { taxDetails } = this || {}
      this.allTaxes = taxDetails
    },

    getVendorName(val) {
      return this.$getProperty(val, 'vendor.name', '---')
    },
    getVendorQuotesLineItems(val) {
      return this.$getProperty(val, 'vendorQuotesLineItems')
    },

    showAwardedInVendor(vendorQuote) {
      let count,
        checkedLineItems = {}
      let { vendorQuotesLineItems } = vendorQuote || {}
      checkedLineItems = vendorQuotesLineItems.filter(
        vendorQuoteLineItem => vendorQuoteLineItem?.checked
      )
      count = this.getLength(checkedLineItems)
      let { lineItemTotalCount } = this || {}
      return `${count} of ${lineItemTotalCount} Items`
    },
    canDisableMarkAll(vendorQuote) {
      let { vendorQuotesLineItems } = vendorQuote || {}
      let isNotCheckedAndDisabled = vendorQuotesLineItems.every(
        vendorQuoteLineItem => {
          return (
            !vendorQuoteLineItem?.checked &&
            this.checkIsDisabled(vendorQuoteLineItem)
          )
        }
      )
      let { lineItemsChecked } = this || {}
      return isNotCheckedAndDisabled && !isEmpty(lineItemsChecked)
    },
    checkIsDisabled(val) {
      let rfqIds = {}
      let { lineItemsChecked } = this || {}
      if (!isEmpty(lineItemsChecked)) {
        rfqIds = lineItemsChecked.map(checkedLineItem => {
          let { requestForQuotationLineItem } = checkedLineItem || {}
          let { id } = requestForQuotationLineItem || {}
          return id
        })
      }
      let rfqId = this.$getProperty(val, 'requestForQuotationLineItem.id')
      return (
        !isEmpty(val) &&
        val.checked == false &&
        !isEmpty(rfqIds) &&
        rfqIds.includes(rfqId)
      )
    },
    getDisabledRowClassName({ row }) {
      if (this.checkIsDisabled(row)) {
        return 'disabled-row'
      }
      return ''
    },
    setMarkedLineItems() {
      let { vendorQuotes } = this || {}
      let checkedLineItems = []
      vendorQuotes.forEach(vendorQuote => {
        let { vendorQuotesLineItems, vendor } = vendorQuote || {}
        vendorQuotesLineItems.forEach(vendorQuotesLineItem => {
          if (vendorQuotesLineItem.checked) {
            vendorQuotesLineItem.vendor = vendor
            checkedLineItems.push(vendorQuotesLineItem)
          }
        })
      })
      this.$emit('setLineItemsChecked', checkedLineItems)
      this.$emit('setlineItemsCheckedCount', this.getLength(checkedLineItems))
      this.setMarkAll()
    },
    setMarkAll() {
      let { vendorQuote } = this || {}
      let vendorQuotesLineItems = this.getVendorQuotesLineItems(vendorQuote)
      let isAnyLineItmeChecked = vendorQuotesLineItems.some(element => {
        return element?.checked
      })
      if (isAnyLineItmeChecked) {
        let lineItemsChecked = vendorQuotesLineItems.filter(element => {
          return element?.checked
        })
        let lineItemsCheckedCount = this.getLength(lineItemsChecked)
        let lineItemsDisabled = vendorQuotesLineItems.filter(element =>
          this.checkIsDisabled(element)
        )
        let lineItemsDisabledCount = this.getLength(lineItemsDisabled)
        let total = lineItemsCheckedCount + lineItemsDisabledCount
        if (total === this.lineItemTotalCount) {
          this.checked = true
        } else {
          this.checked = false
        }
      } else {
        this.checked = false
      }
    },
    getLength(val) {
      let lengthProp = Object.keys(val)
      let { length } = lengthProp
      return length
    },
    markAll(vendorQuote, check) {
      let { vendorQuotesLineItems } = vendorQuote || {}
      vendorQuotesLineItems.forEach(vendorQuotesLineItem => {
        if (!this.checkIsDisabled(vendorQuotesLineItem))
          vendorQuotesLineItem.checked = check
      })
      this.setMarkedLineItems(check)
    },
  },
}
</script>
<style lang="scss" scoped>
.empty-space-between {
  height: 15px;
  background-color: #f5f5f5;
}
.total-amount {
  color: #333;
  font-weight: 700;
  margin-right: 147px;
  display: flex;
  justify-content: right;
}
.awarded-Items {
  background-color: #3ab2c2;
  color: #ffffff;
}
.vendor-name {
  font-size: 20px;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 20px;
  color: black;
  display: inline-flex;
  padding-left: 10px;
}
.disabled-row.active {
  background: #f5f5f5;
  opacity: 0.6;
  pointer-events: none;
}
.disable-actions.active {
  pointer-events: none;
}
.el-table__body td.el-table__cell.item-name-rfq-vendor_view {
  padding-left: 24px;
}
</style>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
.vendor-name {
  .fc-icon-vendor {
    margin-top: 8px;
    margin-right: 8px;
  }
}
.item-name-rfq-vendor_view {
  .lineitem-icon-rfq {
    margin-top: 6px;
  }
}
</style>
