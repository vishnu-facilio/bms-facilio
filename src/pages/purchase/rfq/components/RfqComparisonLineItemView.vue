<template>
  <div
    class="disable-actions  vendor-quotes-lineitems-table"
    :class="{ active: canDisableClickActions }"
  >
    <div class="empty-space-between"></div>
    <div class="rfq-line-item-view-wrap">
      <div class="rfq-line-item-view-header flex width72">
        <div class="vendor-name width39">
          <fc-icon
            group="dsm"
            name="line-item"
            size="12"
            class="lineitem-icon-rfq"
          ></fc-icon>
          <div>
            <div>
              {{ getItemName(rfqLineItem) }}
            </div>
            <div class="item-description">
              {{ getItemDescription(rfqLineItem) }}
            </div>
          </div>
        </div>
        <div class="rfqlineitem-header pR30 width10">
          <div class="fwBold pB4">
            {{ $t('common._common.uom') }}
          </div>
          {{ getUOM(rfqLineItem) }}
        </div>
        <div class="rfqlineitem-header pR8 width17">
          <div class="pB4 fwBold">
            {{ $t('common._common.required_quantity') }}
          </div>
          {{ getQuantity(rfqLineItem) }}
        </div>
        <!-- <div class="rfqlineitem-header custom-class-price pR10 width17">
          <div class="pB4">
            {{ $t('common._common.unit_price') }}
          </div>
          <div class="fwBold">
            {{ getUnitPrice(rfqLineItem) }}
          </div>
        </div> -->
      </div>
      <div v-if="canShowAwardedInLineItem" class="width15 awarded-Items">
        <div class="awarded-Items-content">
          <div class="mR5">{{ $t('common.products.awarded_to') }}</div>
          <el-tooltip
            effect="dark"
            :content="showAwardedInLineItem(rfqLineItem)"
            placement="top"
          >
            <div class="fwBold">
              {{ showAwardedInLineItem(rfqLineItem) }}
            </div>
          </el-tooltip>
        </div>
      </div>
    </div>
    <div class="vendor-quotes-lineitems-table">
      <el-table :data="getVendorQuotesLineItems(rfqLineItem)" border>
        <el-table-column
          :label="$t('common.header.award')"
          width="100"
          class="mL5"
          class-name="award-column"
        >
          <template v-slot="scope">
            <el-radio
              v-model="radioStatusArr[index]"
              :label="scope.row.id"
              class="fc-radio-btn"
              @change="setCheckedLineItem(rfqLineItem, scope.row.id)"
            >
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column
          :label="`${$t('common.header.vendor_name')}`"
          width="256"
          class-name="item-name-rfq-lineitem_view"
        >
          <template slot-scope="scope">
            <div class="flex flex-no-wrap">
              <fc-icon
                class="fc-icon-vendor"
                size="12"
                group="webtabs"
                name="vendor"
              ></fc-icon>
              <div class="flex flex-direction-column">
                <span class="fwBold mL10">{{ getVendorName(scope.row) }}</span>
                <span class="mL8">
                  {{ getVendorLocation(scope.row) }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :formatter="getTaxPercentage"
          :label="`${$t('common.inventory.tax_percentage')} (%)`"
          class-name=" right-align-elements"
          min-width="164"
        >
        </el-table-column>
        <el-table-column
          :formatter="getTaxAmount"
          :label="`${$t('common.inventory.tax_amount')} (${$currency})`"
          class-name=" right-align-elements"
          min-width="148"
        >
        </el-table-column>
        <el-table-column
          prop="counterPrice"
          :formatter="getCounterPrice"
          :label="`${$t('common.inventory.quoted_unit_price')} (${$currency})`"
          class-name="custom-class-price right-align-elements"
          width="200"
        >
        </el-table-column>
        <el-table-column
          prop="amount"
          :formatter="getAmount"
          :label="`${$t('common.inventory.quoted_amount')} (${$currency})`"
          class-name="right-align-elements"
          width="164"
        >
        </el-table-column>
        <el-table-column
          prop="remarks"
          :formatter="getRemarks"
          :label="$t('common.wo_report._remarks')"
          min-width="150"
        >
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import PurchaseOrderMixin from 'src/pages/purchase/po/mixin/poMixin'
import RfqMixin from '../mixins/RfqMixin'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'

export default {
  props: [
    'isAwarded',
    'rfqLineItem',
    'vendorQuotesLineItems',
    'index',
    'lineItemsChecked',
    'filteredRfqLineItems',
    'taxDetails',
  ],
  mixins: [PurchaseOrderMixin, RfqMixin, LineItemsMixin],
  components: {},
  data() {
    return {
      radioStatusArr: [],
    }
  },
  watch: {
    filteredRfqLineItems(newVal, oldVal) {
      if (oldVal != newVal) {
        this.setRadioStatusArr()
      }
    },
  },
  created() {
    this.setTaxes()
    this.setRadioStatusArr()
  },
  computed: {
    canDisableClickActions() {
      let { isAwarded } = this || {}
      return isAwarded
    },
    canShowAwardedInLineItem() {
      let { radioStatusArr } = this || {}
      let showAwardedInLineItem = radioStatusArr.some(
        element => !isEmpty(element)
      )
      return showAwardedInLineItem
    },
  },
  methods: {
    setTaxes() {
      let { taxDetails } = this || {}
      this.allTaxes = taxDetails
    },

    setRadioStatusArr() {
      let radioStatusArr = []
      let { index, filteredRfqLineItems, rfqLineItem } = this || {}
      let requestForQuotationLineItems = filteredRfqLineItems
      let rfqLineItemsLength = this.getLength(requestForQuotationLineItems)
      for (let i = 0; i < rfqLineItemsLength; i++) {
        if (i == index) {
          let vendorQuoteLineItemForRfqLineItem = this.getVendorQuotesLineItems(
            rfqLineItem
          )
          let checkedLineItem = vendorQuoteLineItemForRfqLineItem.find(
            element => {
              return element?.checked
            }
          )
          if (!isEmpty(checkedLineItem)) {
            radioStatusArr.push(checkedLineItem.id)
          } else {
            radioStatusArr.push(null)
          }
        } else {
          radioStatusArr.push(null)
        }
      }
      this.radioStatusArr = radioStatusArr
    },
    setCheckedLineItem(val, id) {
      let rfqLineItemId = id
      let vendorQuoteLineItems = this.getVendorQuotesLineItems(val)
      vendorQuoteLineItems.forEach(element => {
        let { id } = element || {}
        if (rfqLineItemId == id) {
          element.checked = true
        } else {
          element.checked = false
        }
      })
      let { vendorQuotesLineItems } = this || {}
      let checkedLineItems = vendorQuotesLineItems.filter(
        vendorQuoteLineItem => vendorQuoteLineItem.checked
      )
      this.$emit('setLineItemsChecked', checkedLineItems)
      this.$emit('setlineItemsCheckedCount', this.getLength(checkedLineItems))
    },
    showAwardedInLineItem(val) {
      let { lineItemsChecked } = this || {}
      let rfqId = val.id || {}
      let lineItemForRfq = {}
      if (!isEmpty(lineItemsChecked)) {
        lineItemsChecked.forEach(lineItemChecked => {
          let { requestForQuotationLineItem } = lineItemChecked || {}
          if (requestForQuotationLineItem?.id === rfqId)
            lineItemForRfq = lineItemChecked
        })
      }
      let vendorName = this.$getProperty(lineItemForRfq, 'vendor.name', '---')
      return !isEmpty(lineItemForRfq) ? vendorName : 'NONE'
    },
    getVendorLocation(val) {
      let address = this.$getProperty(val, 'vendor.address')
      let { empty } = address || {}
      return !empty ? address.name : ''
    },
    getUOM(val) {
      let { unitOfMeasure } = val || {}
      return this.uomEnumMap[unitOfMeasure]
    },
    getQuantity(val) {
      let { quantity } = val || {}
      return quantity || '---'
    },
    getVendorName(val) {
      let vendorName = this.$getProperty(val, 'vendor.name', '---')
      return vendorName
    },
    getVendorQuotesLineItems(val) {
      let rfqId = this.$getProperty(val, 'id')
      let { vendorQuotesLineItems } = this || {}
      let vendorQuotesLineItemsForRfq = vendorQuotesLineItems.filter(
        vendorQuoteLineItem => {
          let { requestForQuotationLineItem } = vendorQuoteLineItem || {}
          let id = requestForQuotationLineItem.id
          return rfqId === id
        }
      )
      return vendorQuotesLineItemsForRfq
    },
    getLength(val) {
      let lengthProp = Object.keys(val)
      let { length } = lengthProp
      return length
    },
  },
}
</script>
<style lang="scss" scoped>
.item-description {
  font-size: 13px;
  font-weight: normal;
}
.rfqlineitem-header {
  display: flex;
  flex-direction: column;
  letter-spacing: 0.5px;
  text-align: right;
  margin-left: 0.5%;
}
.empty-space-between {
  height: 15px;
  background-color: #f5f5f5;
}
.awarded-Items {
  background-color: #3ab2c2;
  color: #ffffff;
  margin-left: auto;
  display: flex;
  align-items: center;
  padding: 10px 15px;
}
.awarded-Items-content {
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
}
.vendor-name {
  font-size: 16px;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 20px;
  color: black;
  display: inline-flex;
}
.disable-actions.active {
  pointer-events: none;
}
</style>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
.item-name-rfq-lineitem_view {
  .fc-icon-vendor {
    margin-top: 6px;
  }
}
.vendor-name {
  .lineitem-icon-rfq {
    margin-top: 4px;
    margin-right: 6px;
  }
}
.rfq-line-item-view-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  align-items: stretch;
}
.rfq-line-item-view-header {
  margin-left: 10px;
  align-items: center;
}
</style>
