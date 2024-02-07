<template>
  <div
    class="fc-qfm-pt20"
    v-if="!$validation.isEmpty(details.requestForQuotationLineItems)"
  >
    <table class="fc-quotation-summary-table fc-quotation-table">
      <thead>
        <tr>
          <th style="width: 40px;" class="text-left">
            {{ $t('quotation.common.s_no') }}
          </th>
          <th class="text-left" style="width: 240px;">
            {{ $t('quotation.common.item_and_description') }}
          </th>
          <!-- <th class="text-left" style="width: 100px;">
            {{ $t('quotation.common.unit_price') }} {{ `(${$currency})` }}
          </th> -->
          <th class="width100px text-left" style="width: 80px;">
            {{ $t('quotation.common.qty') }}
          </th>
          <th style="width: 50px;" class="text-left">
            {{ $t('quotation.common.uom') }}
          </th>
          <th style="width: 50px;" class="text-left">
            {{ $t('common.products.awarded_price') }} {{ `(${$currency})` }}
          </th>
          <th style="width: 50px;" class="text-left">
            {{ $t('common.inventory.tax_amount') }} {{ `(${$currency})` }}
          </th>
          <th style="width: 132px;" class="text-left">
            {{ $t('common.products.awarded_to') }}
          </th>
          <th style="width: 132px;" class="text-left">
            {{ $t('quotation.common.amount') }} {{ `(${$currency})` }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(tableDetail,
          lineItemIndex) in details.requestForQuotationLineItems"
          :key="`lineItem_${lineItemIndex}`"
        >
          <td class="text-center">
            {{ lineItemIndex + 1 }}
          </td>
          <td>
            <div class="" v-if="tableDetail.inventoryType === 1">
              <div class="bold">
                {{ $getProperty(tableDetail, 'itemType.name') }}
              </div>
            </div>
            <div class="" v-if="tableDetail.inventoryType === 2">
              <div class="bold">
                {{ $getProperty(tableDetail, 'toolType.name') }}
              </div>
            </div>
            <div class="" v-if="tableDetail.inventoryType === 3">
              <div class="bold">
                {{ $getProperty(tableDetail, 'service.name') }}
              </div>
            </div>
            <!-- Using span to avoid pre line white spaces caused by prettier while using div -->
            <span
              class="label-txt-black block space-preline"
              :class="[tableDetail.inventoryType !== 4 && 'pT5']"
              >{{ tableDetail.description }}</span
            >
          </td>
          <!-- <td>
            <div>
              <div class="text-center">
                {{ unitPrice(tableDetail.unitPrice) }}
              </div>
            </div>
          </td> -->

          <td class="text-center">
            {{ tableDetail.quantity }}
          </td>
          <td class="text-left">
            {{ uomEnumMap[tableDetail.unitOfMeasure] || '---' }}
          </td>
          <td
            :class="lineItemTableClass(tableDetail.awardedPrice, 'text-right')"
          >
            {{ awardedPrice(tableDetail) }}
          </td>
          <td :class="lineItemTableClass(tableDetail.taxAmount, 'text-right')">
            {{ taxAmount(tableDetail) }}
          </td>
          <td :class="lineItemTableClass(tableDetail.awardedTo, 'text-center')">
            {{ awardedTo(tableDetail) }}
          </td>
          <td :class="lineItemTableClass(tableDetail.totalCost, 'text-right')">
            {{ amount(tableDetail) }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details', 'uomEnumMap', 'pdfSource'],
  methods: {
    // unitPrice(unitPrice) {
    //   return !isEmpty(unitPrice) ? this.$d3.format(',.2f')(unitPrice) : '---'
    // },
    awardedPrice(tableDetail) {
      let { awardedPrice } = tableDetail || {}

      return !isEmpty(awardedPrice) && this.pdfSource !== 'vendorQuotes'
        ? this.$d3.format(',.2f')(awardedPrice)
        : '---'
    },
    taxAmount(tableDetail) {
      let { taxAmount } = tableDetail || {}
      return !isEmpty(taxAmount) && this.pdfSource !== 'vendorQuotes'
        ? this.$d3.format(',.2f')(taxAmount)
        : '---'
    },
    awardedTo(tableDetail) {
      let awardedTo = this.$getProperty(tableDetail, 'awardedTo.name')

      return !isEmpty(awardedTo) && this.pdfSource !== 'vendorQuotes'
        ? awardedTo
        : '---'
    },
    amount(tableDetail) {
      let { totalCost } = tableDetail || {}

      return !isEmpty(totalCost) && this.pdfSource !== 'vendorQuotes'
        ? this.$d3.format(',.2f')(totalCost)
        : '---'
    },
    lineItemTableClass(value, align) {
      return !isEmpty(value) ? align : 'text-center empty-text-rfq'
    },
  },
}
</script>
<style scoped>
.empty-text-rfq {
  color: rgb(192 186 186);
}
</style>
