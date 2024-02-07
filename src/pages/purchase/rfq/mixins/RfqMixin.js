import { isEmpty } from '@facilio/utils/validation'
import PurchaseOrderMixin from 'src/pages/purchase/po/mixin/poMixin'
export default {
  mixins: [PurchaseOrderMixin],
  methods: {
    getVendorQuotesLineItems(val) {
      return this.$getProperty(val, 'vendorQuotesLineItems')
    },
    uom(val) {
      let unitOfMeasure = this.$getProperty(
        val,
        'requestForQuotationLineItem.unitOfMeasure'
      )
      return this.uomEnumMap[unitOfMeasure]
    },
    getItemName(val) {
      let { inventoryTypeEnum } = val || {}
      const inventoryTypeVsKeys = {
        ITEM: 'itemType.name',
        TOOL: 'toolType.name',
        SERVICE: 'service.name',
        DEFAULT: 'description',
      }
      let nameKey =
        inventoryTypeVsKeys[inventoryTypeEnum] || inventoryTypeVsKeys['DEFAULT']
      return this.$getProperty(val, nameKey, '---')
    },
    getItemDescription(val) {
      let { inventoryTypeEnum } = val || {}
      if (inventoryTypeEnum === 'OTHERS') {
        return ' '
      }
      return this.$getProperty(val, 'description', ' ')
    },

    getRemarks(val) {
      return this.$getProperty(val, 'remarks', '---')
    },
    getAmount(val) {
      let { quantity, counterPrice, taxAmount } = val || {}
      let amount = quantity * counterPrice
      if (!isEmpty(taxAmount)) {
        amount += taxAmount
      }
      val.amount = amount
      return !isEmpty(amount) && !isEmpty(counterPrice)
        ? this.$d3.format(',.2f')(amount)
        : '---'
    },

    getTotalAmount(vendorQuote) {
      let { vendorQuotesLineItems } = vendorQuote || {}
      let totalAmount = this.$d3.sum(
        vendorQuotesLineItems.map(vendorQuoteLineItem => {
          return vendorQuoteLineItem.amount
        })
      )
      return !isEmpty(totalAmount)
        ? this.$d3.format(',.2f')(totalAmount)
        : '---'
    },
    getCounterPrice(val) {
      let counterPrice = this.$getProperty(val, 'counterPrice')
      return !isEmpty(counterPrice)
        ? this.$d3.format(',.2f')(counterPrice)
        : '---'
    },
    getUnitPrice(val) {
      let unitPrice = this.$getProperty(val, 'unitPrice')
      return !isEmpty(unitPrice) ? this.$d3.format(',.2f')(unitPrice) : '---'
    },

    totalQuotedAmount(vendorQuotesLineItems) {
      let totalAmount = this.$d3.sum(
        vendorQuotesLineItems.map(vendorQuoteLineItem => {
          let { quantity, counterPrice, taxAmount } = vendorQuoteLineItem || {}
          let amount = quantity * counterPrice
          if (!isEmpty(taxAmount)) {
            amount += taxAmount
          }
          return amount
        })
      )
      return totalAmount ? this.$d3.format(',.2f')(totalAmount) : '---'
    },
  },
}
