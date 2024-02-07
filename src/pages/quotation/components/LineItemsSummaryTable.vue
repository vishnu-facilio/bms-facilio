<template>
  <div class="fc-qfm-pt20-10" v-if="!$validation.isEmpty(details.lineItems)">
    <table class="fc-quotation-summary-table fc-quotation-table">
      <thead>
        <tr>
          <th style="width: 50px;" class="text-center">
            {{ $t('quotation.common.s_no') }}
          </th>
          <th class="text-center" style="width: 43%;">
            {{ $t('quotation.common.item_and_description') }}
          </th>
          <th class="text-center" style="width: 130px;">
            {{ $t('quotation.common.unit_price') }}
            {{ `(${lineItemCurrency})` }}
          </th>
          <th class="width100px text-center" style="width: 60px;">
            {{ $t('quotation.common.qty') }}
          </th>
          <th
            class="width100px text-center"
            style="width: 60px;"
            v-if="showMarkupValue && !details.isGlobalMarkup"
          >
            {{ `Markup` }}
            <!-- <el-tooltip
              effect="dark"
              class="m4"
              :content="markupContent"
              placement="top-start"
            >
              <i class="el-icon-warning-outline warning-icon" />
            </el-tooltip> -->
          </th>
          <th
            v-if="$helpers.taxMode() === 1"
            style="width: 116px;"
            class="text-center"
          >
            {{ $t('quotation.common.tax') }} {{ `(${lineItemCurrency})` }}
          </th>
          <th style="width: 60px;" class="ttext-center">
            {{ $t('quotation.common.uom') }}
          </th>
          <th style="width: 142px;" class="text-center">
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
            <div class="" v-if="tableDetail.type === 1">
              <div class="bold">
                {{ $getProperty(tableDetail, 'itemType.name') }}
              </div>
            </div>
            <div class="" v-if="tableDetail.type === 2">
              <div class="bold">
                {{ $getProperty(tableDetail, 'toolType.name') }}
              </div>
            </div>
            <div class="" v-if="tableDetail.type === 3">
              <div class="bold">
                {{ $getProperty(tableDetail, 'service.name') }}
              </div>
            </div>
            <div class="" v-if="tableDetail.type === 4">
              <div class="bold">
                {{ $getProperty(tableDetail, 'labour.name') }}
              </div>
            </div>
            <!-- Using span to avoid pre line white spaces caused by prettier while using div -->
            <span
              class="label-txt-black block space-preline"
              :class="[tableDetail.type !== 5 && 'pT5']"
              >{{ tableDetail.description }}</span
            >
          </td>
          <td>
            <div>
              <div v-if="$org.id === 559">
                {{
                  $d3.format(',.3f')(
                    getFormatedUnitPrice(tableDetail.unitPrice, tableDetail)
                  )
                }}
              </div>
              <div class="text-right" v-else>
                {{
                  $d3.format(',.2f')(
                    getFormatedUnitPrice(tableDetail.unitPrice, tableDetail)
                  )
                }}
              </div>

              <div
                v-if="[2, 4].includes(Number(tableDetail.type))"
                class="fc-grey7-12 mT5"
              >
                {{ `per hr` }}
              </div>
            </div>
          </td>

          <td class="text-center">
            {{ tableDetail.quantity }}
          </td>
          <td
            class="text-center"
            v-if="showMarkupValue && !details.isGlobalMarkup"
          >
            {{ tableDetail.markup }}
          </td>
          <td v-if="$helpers.taxMode() === 1" class="text-right">
            <div v-if="$org.id === 559">
              {{
                tableDetail.taxAmount
                  ? $d3.format(',.3f')(tableDetail.taxAmount)
                  : '---'
              }}
            </div>
            <div v-else>
              {{
                tableDetail.taxAmount
                  ? $d3.format(',.2f')(tableDetail.taxAmount)
                  : '---'
              }}
            </div>
          </td>
          <td class="text-center">
            {{ uomEnumMap[tableDetail.unitOfMeasure] || '---' }}
          </td>
          <td class="text-right">
            <div v-if="$org.id === 559">
              {{
                $d3.format(',.3f')(
                  formatedTotalCost(tableDetail.cost, tableDetail)
                )
              }}
            </div>
            <div v-else>
              {{
                $d3.format(',.2f')(
                  formatedTotalCost(tableDetail.cost, tableDetail)
                )
              }}
            </div>
          </td>
        </tr>
        <tr
          v-if="
            showMarkupValue && details.isGlobalMarkup && details.totalMarkup
          "
        >
          <td colspan="5">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ `Markup` }}
              {{ `(${details.markup})%` }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div>
              {{ $d3.format(',.2f')(details.totalMarkup) }}
            </div>
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
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(details.subTotal) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(details.subTotal) }}
            </div>
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
                  ? `DISCOUNT (${details.discountPercentage} %) (${lineItemCurrency})`
                  : `DISCOUNT (${lineItemCurrency})`
              }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(-discountAmount) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(-discountAmount) }}
            </div>
          </td>
        </tr>
        <tr
          v-if="
            $helpers.discountMode() === 1 &&
              (!$validation.isEmpty(details.discountPercentage) ||
                !$validation.isEmpty(discountAmount)) &&
              !$validation.isEmpty(details.taxSplitUp)
          "
        >
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              {{ `ADJUSTED TOTAL (${lineItemCurrency})` }}
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(getAdjustedTotal) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(getAdjustedTotal) }}
            </div>
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
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')($getProperty(taxSplitUp, 'taxAmount')) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')($getProperty(taxSplitUp, 'taxAmount')) }}
            </div>
          </td>
        </tr>
        <tr
          v-if="!$validation.isEmpty(details.totalTaxAmount) && canShowTotalTax"
        >
          <td colspan="5">
            <div class="text-right fc-violet fwBold text-uppercase f12">
              TOTAL TAX
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(details.totalTaxAmount) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(details.totalTaxAmount) }}
            </div>
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
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(-discountAmount) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(-discountAmount) }}
            </div>
          </td>
        </tr>
        <tr v-if="!$validation.isEmpty(details.shippingCharges)">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              Shipping Charges
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(details.shippingCharges) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(details.shippingCharges) }}
            </div>
          </td>
        </tr>
        <tr v-if="!$validation.isEmpty(details.miscellaneousCharges)">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              Misc. Charges
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(details.miscellaneousCharges) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(details.miscellaneousCharges) }}
            </div>
          </td>
        </tr>
        <tr v-if="!$validation.isEmpty(details.adjustmentsCost)">
          <td colspan="5" class="">
            <div class="text-right fc-violet f12 text-uppercase fwBold">
              Adjustment Cost
            </div>
          </td>
          <td colspan="3" class="text-right fwBold fc-black-14">
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(details.adjustmentsCost) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(details.adjustmentsCost) }}
            </div>
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
            <div v-if="$org.id === 559">
              {{ $d3.format(',.3f')(details.totalCost) }}
            </div>
            <div v-else>
              {{ $d3.format(',.2f')(details.totalCost) }}
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import round from 'lodash/round'
import { getCurrencyInDecimalValue } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'
export default {
  props: ['details', 'uomEnumMap'],
  created() {
    this.$store.dispatch('getActiveCurrencyList')
  },
  computed: {
    markupContent() {
      return 'The global markup value is used.'
    },
    ...mapState({
      currencyList: state => state.activeCurrencies,
    }),
    withmarkup() {
      return this.$route.query.withmarkup || false
    },
    isGlobalMarkup() {
      return this.details?.isGlobalMarkup
    },
    showMarkupValue() {
      if (this.withmarkup && this.withmarkup === 'false') {
        return false
      }
      return this.details.showMarkupValue || false
    },
    canShowSubTotal() {
      let { details, discountAmount } = this
      return (
        !isEmpty(discountAmount) ||
        !isEmpty(details.taxSplitUp) ||
        !isEmpty(details.shippingCharges) ||
        !isEmpty(details.miscellaneousCharges) ||
        !isEmpty(details.adjustmentsCost) ||
        details.showMarkupValue
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
    getAdjustedTotal() {
      let { details, discountAmount } = this
      return details.subTotal - discountAmount
    },
    lineItemCurrency() {
      let { currencyCode } = this.details || {}
      let currency = (this.currencyList || []).find(
        cur => cur.currencyCode === currencyCode
      )
      return currency?.displaySymbol || this.$currency
    },
  },
  methods: {
    formatedTotalCost(totlaCost) {
      if (
        this.withmarkup &&
        this.withmarkup === 'true' &&
        this.isGlobalMarkup &&
        this.details?.markup
      ) {
        return totlaCost
      } else if (
        this.withmarkup &&
        this.withmarkup === 'false' &&
        this.isGlobalMarkup &&
        this.details?.markup
      ) {
        return (
          totlaCost + this.getPercetageOfValue(totlaCost, this.details.markup)
        )
      } else {
        return totlaCost
      }
    },
    getFormatedUnitPrice(unitPrice, lineItem) {
      let { markup, quantity = 1 } = lineItem
      if (
        this.withmarkup &&
        this.withmarkup === 'true' &&
        this.isGlobalMarkup &&
        this.details?.markup
      ) {
        return unitPrice
      } else if (
        this.withmarkup &&
        this.withmarkup === 'false' &&
        this.isGlobalMarkup &&
        this.details?.markup
      ) {
        console.log(
          '---->',
          unitPrice,
          this.getPercetageOfValue(unitPrice, this.details.markup),
          unitPrice + this.getPercetageOfValue(unitPrice, this.details.markup)
        )
        return (
          unitPrice + this.getPercetageOfValue(unitPrice, this.details.markup)
        )
      } else if (
        this.withmarkup &&
        this.withmarkup === 'true' &&
        !this.isGlobalMarkup
      ) {
        return unitPrice
      } else if (
        this.withmarkup &&
        this.withmarkup === 'false' &&
        !this.isGlobalMarkup
      ) {
        return this.getFormatedLineItemUnitPrice(unitPrice, markup, quantity)
      } else {
        return unitPrice
      }
    },
    getFormatedLineItemUnitPrice(unitPrice, markup, quantity = 1) {
      if (markup) {
        return unitPrice + this.getPercetageOfValue(unitPrice, markup)
      }
      return unitPrice
    },
    getPercetageOfValue(value, percentage) {
      if (value && percentage !== null) {
        let perOfV = (percentage / 100) * value
        return round(perOfV, 3)
      } else {
        return 0
      }
    },
  },
}
</script>
