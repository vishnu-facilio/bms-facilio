<template>
  <div class="fc-qfm-pt20" v-if="!$validation.isEmpty(details.lineItems)">
    <table class="fc-quotation-summary-table fc-quotation-table">
      <thead>
        <tr>
          <th style="width: 50px;" class="text-left">
            {{ $t('quotation.common.s_no') }}
          </th>
          <th class="text-left" style="width: 43%;">
            {{ $t('quotation.common.item_and_description') }}
          </th>
          <th class="text-left" style="width: 130px;">
            {{ $t('quotation.common.unit_price') }}
            {{ `(${lineItemCurrency})` }}
          </th>
          <th class="width100px text-left" style="width: 60px;">
            {{ $t('quotation.common.qty') }}
          </th>
          <th
            v-if="$helpers.taxMode() === 1"
            style="width: 116px;"
            class="text-left"
          >
            {{ $t('quotation.common.tax') }} {{ `(${lineItemCurrency})` }}
          </th>
          <th style="width: 60px;" class="text-left">
            {{ $t('quotation.common.uom') }}
          </th>
          <th style="width: 142px;" class="text-left">
            {{ $t('quotation.common.amount') }} {{ `(${lineItemCurrency})` }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(tableDetail, lineItemIndex) in details.lineItems"
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
          <td>
            <div>
              <div class="text-right">
                {{ $d3.format(',.2f')(tableDetail.unitPrice) }}
              </div>
            </div>
          </td>

          <td class="text-center">
            {{ tableDetail.quantity }}
          </td>
          <td v-if="$helpers.taxMode() === 1" class="text-right">
            {{
              tableDetail.taxAmount
                ? $d3.format(',.2f')(tableDetail.taxAmount)
                : '---'
            }}
          </td>
          <td class="text-left nowrap">
            {{ uomEnumMap[tableDetail.unitOfMeasure] || '---' }}
          </td>
          <td class="text-right">
            {{ $d3.format(',.2f')(tableDetail.cost) }}
          </td>
        </tr>
        <tr v-if="canShowSubTotal">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ $t('quotation.common.sub_total') }}
              {{ `(${lineItemCurrency})` }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(details.subTotal) }}
          </td>
        </tr>
        <tr
          v-if="
            $helpers.discountMode() === 1 &&
              (!$validation.isEmpty(details.discountPercentage) ||
                !$validation.isEmpty(discountAmount))
          "
        >
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{
                details.discountPercentage
                  ? `DISCOUNT ${details.discountPercentage} %`
                  : `DISCOUNT ${discountAmount} ${lineItemCurrency}`
              }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(-discountAmount) }}
          </td>
        </tr>
        <tr
          v-for="(taxSplitUp, taxIndex) in details.taxSplitUp"
          :key="`tax_${taxIndex}`"
        >
          <td colspan="5">
            <div class="text-right fc-violet fwBold text-uppercase f12">
              {{
                `${$getProperty(taxSplitUp, 'tax.name')} (${$getProperty(
                  taxSplitUp,
                  'tax.rate'
                )} %)`
              }}
              {{ `(${lineItemCurrency})` }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')($getProperty(taxSplitUp, 'taxAmount')) }}
          </td>
        </tr>
        <tr
          v-if="!$validation.isEmpty(details.totalTaxAmount) && canShowTotalTax"
        >
          <td colspan="5">
            <div class="text-right fc-violet fwBold text-uppercase f12">
              {{ $t('common.header.total_tax') }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(details.totalTaxAmount) }}
          </td>
        </tr>

        <tr
          v-if="
            $helpers.discountMode() === 2 &&
              (!$validation.isEmpty(details.discountPercentage) ||
                !$validation.isEmpty(discountAmount))
          "
        >
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{
                details.discountPercentage
                  ? `DISCOUNT ${details.discountPercentage} %`
                  : `DISCOUNT ${discountAmount} ${lineItemCurrency}`
              }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(-discountAmount) }}
          </td>
        </tr>
        <tr v-if="!$validation.isEmpty(details.shippingCharges)">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ $t('common.header.shipping_charges') }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(details.shippingCharges) }}
          </td>
        </tr>
        <tr v-if="!$validation.isEmpty(details.miscellaneousCharges)">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ $t('common.header.misc_charges') }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(details.miscellaneousCharges) }}
          </td>
        </tr>
        <tr v-if="!$validation.isEmpty(details.adjustmentsCost)">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ $t('common.header.adjustment_cost') }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(details.adjustmentsCost) }}
          </td>
        </tr>
        <tr>
          <td colspan="5">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ $t('quotation.common.grand_total') }}
              {{ `(${lineItemCurrency})` }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            {{ $d3.format(',.2f')(details.totalCost) }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { getCurrencyInDecimalValue } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['details', 'uomEnumMap'],
  created() {
    this.$store.dispatch('getActiveCurrencyList')
  },
  computed: {
    ...mapState({
      currencyList: state => state.activeCurrencies,
    }),
    canShowSubTotal() {
      let { details, discountAmount } = this
      return (
        !isEmpty(discountAmount) ||
        !isEmpty(details.taxSplitUp) ||
        !isEmpty(details.shippingCharges) ||
        !isEmpty(details.miscellaneousCharges) ||
        !isEmpty(details.adjustmentsCost)
      )
    },
    discountAmount() {
      let { discountAmount } = this.details || {}
      let currencyDetail = { decimalPlaces: 2 }
      return getCurrencyInDecimalValue(discountAmount, currencyDetail)
    },
    canShowTotalTax() {
      return (
        (this.$getProperty(this, 'details.taxSplitUp', []) || []).length > 1
      )
    },

    lineItemCurrency() {
      let { currencyCode } = this.details || {}
      let currency = (this.currencyList || []).find(
        cur => cur.currencyCode === currencyCode
      )
      return currency?.displaySymbol || this.$currency
    },
  },
}
</script>
