<template>
  <div
    v-if="loading"
    class="section-items f-quote-lineitems flex-middle flex-col"
  >
    <spinner :show="true" :size="80"></spinner>
  </div>
  <div v-else class="section-items f-quote-lineitems flex-col display-table">
    <div
      class="mL-auto d-flex items-baseline"
      v-if="canShowCurrencyDropDown && module !== 'requestForQuotation'"
    >
      <span class="mR10 lineItem-currency-header">{{
        $t('common.products.currency')
      }}</span>
      <FNewCurrencyField
        :moduleData="currencyDetails"
        :isLineItem="true"
        :initialCurrency="initialCurrency"
        @setCurrencyCode="setCurrencyCodeOnChange"
        @setIsDefaultCurrency="val => (isDefaultCurrency = val)"
      >
      </FNewCurrencyField>
    </div>
    <!-- table layout -->
    <table class="fc-quote-line-item-table mT20">
      <thead>
        <tr>
          <th style="width: 33.33333%;">
            {{ $t('common._common.name_and_description') }}
          </th>
          <th
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth166' : 'fcQuoteWidth16'
            "
            class="text-center"
          >
            {{ `${$t('common.header._unit_price')} (${lineItemCurrency})` }}
          </th>
          <th
            class="text-center"
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth13' : 'fcQuoteWidth16'
            "
          >
            {{ $t('common.products.qty') }}
          </th>
          <th
            class="text-center"
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth10' : 'fcQuoteWidth16'
            "
          >
            {{ $t('common.products.uom') }}
          </th>
          <th
            class="text-center fcQuoteWidth100"
            v-if="
              $helpers.taxMode() === 1 &&
                !hideTaxField &&
                showMarkupValue &&
                !isGlobalMarkup
            "
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth100' : 'fcQuoteWidth16'
            "
          >
            {{ `MARKUP` }}
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
            class="text-center fcQuoteWidth100"
            v-if="$helpers.taxMode() === 1 && !hideTaxField"
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth100' : 'fcQuoteWidth16'
            "
          >
            {{ $t('common.products.tax') }}
          </th>
          <th
            class="text-center"
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth28' : 'fcQuoteWidth16'
            "
          >
            {{ `${$t('common.header.total_amount')} (${lineItemCurrency})` }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(lineItem, index) in lineItems" :key="index">
          <td style="padding: 0;">
            <el-form
              :ref="`lineItem-description-${index}`"
              :rules="descriptionValidationRules"
              :model="lineItem"
              class="table-row"
            >
              <el-row>
                <el-col :span="4">
                  <el-select
                    v-model="lineItem.type"
                    class="fc-select-suffix-hide fc-select-icon fc-quote-item-select"
                    @change="lineItemChangeAction(lineItem, index)"
                  >
                    <template slot="prefix">
                      <InlineSvg
                        :src="getLineItemTypeObj(lineItem.type).icon"
                        :iconClass="getLineItemTypeObj(lineItem.type).iconClass"
                      ></InlineSvg>
                      <i class="el-icon-caret-bottom"></i>
                    </template>
                    <el-option
                      v-for="item in lineItemTypes"
                      :key="item.type"
                      :label="''"
                      :value="item.type"
                    >
                      <div class="flex-middle">
                        <InlineSvg
                          :src="item.icon"
                          :iconClass="item.iconClass"
                        ></InlineSvg>
                        <div class="pR40">
                          {{ item.label }}
                        </div>
                      </div>
                    </el-option>
                  </el-select>
                </el-col>
                <el-col :span="20" v-if="getLineItemsList(lineItem.type)">
                  <el-form-item
                    :prop="lineItemTypesMap[lineItem.type].model"
                    :required="true"
                    class="hide-error"
                  >
                    <FLookupFieldWrapper
                      v-if="showLookupField"
                      v-model="
                        lineItem[`${lineItemTypesMap[lineItem.type].model}`].id
                      "
                      @recordSelected="lineItemChangeAction(lineItem, index)"
                      :field="currentFieldObject(lineItem)"
                      class="width100 fc-input-full-border-select2 lookup-lineItem fc-quoation-form-select"
                    ></FLookupFieldWrapper>
                  </el-form-item>
                </el-col>
                <el-col :span="24" v-if="getLineItemsList(lineItem.type)">
                  <div
                    class="fc-black-13 text-left pL10 line-height20 mR10 mT10"
                  >
                    <el-form-item
                      prop="description"
                      :required="true"
                      class="hide-error"
                    >
                      <el-input
                        :placeholder="$t('common.wo_report.report_description')"
                        type="textarea"
                        v-model="lineItem.description"
                        :resize="'none'"
                        class="fc-input-full-border2 width100 fc-quotation-first-td"
                        :autosize="{ minRows: 2, maxRows: 100 }"
                      >
                      </el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="20" v-else>
                  <div
                    class="fc-black-13 text-left pL10 line-height20 mR10 width100 mL10 fc-desc-width"
                  >
                    <el-form-item
                      prop="description"
                      :required="true"
                      class="hide-error"
                    >
                      <el-input
                        type="textarea"
                        :placeholder="$t('common.wo_report.report_description')"
                        v-model="lineItem.description"
                        :resize="'none'"
                        class="fc-input-full-border2 width100 fc-quotation-first-td"
                        :autosize="{ minRows: 2, maxRows: 100 }"
                      >
                      </el-input>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            </el-form>
          </td>
          <td>
            <el-form
              :ref="`lineItem-unitPrice-${index}`"
              :rules="unitPriceValidationRules"
              :model="lineItem"
              class="table-row"
            >
              <div class="fc-black-13 text-right">
                <el-form-item
                  prop="unitPrice"
                  :required="true"
                  class="hide-error"
                >
                  <el-input
                    :placeholder="$t('common.header.unit_price')"
                    type="number"
                    v-model.number="lineItem.unitPrice"
                    class="fc-input-full-border2 width100"
                  >
                  </el-input>
                </el-form-item>
              </div>
              <div
                v-if="[2, 4].includes(Number(lineItem.type))"
                class="fc-grey7-12 pR10 pT5"
              >
                {{ `per hr` }}
              </div>
            </el-form>
          </td>
          <td>
            <el-form
              :ref="`lineItem-quantity-${index}`"
              :rules="quantityValidationRules"
              :model="lineItem"
              class="table-row"
            >
              <div class="fc-black-13">
                <el-form-item
                  :required="true"
                  prop="quantity"
                  class="hide-error"
                >
                  <el-input
                    :placeholder="$t('common._common.quantity')"
                    type="number"
                    v-model="lineItem.quantity"
                    class="fc-input-full-border2 width100"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </el-form>
          </td>
          <td>
            <div class="fc-black-13">
              <el-select
                filterable
                v-model="lineItem.unitOfMeasure"
                class="width100 fc-input-full-border2 fc-form-select-grey-txt"
              >
                <el-option
                  v-for="(item, index) in uomFieldOptions"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </div>
          </td>
          <td v-if="showMarkupValue && !isGlobalMarkup">
            <el-form
              :ref="`lineItem-markup-${index}`"
              :model="lineItem"
              class="table-row"
            >
              <div class="fc-black-13 text-right">
                <el-form-item
                  prop="unitPrice"
                  :required="false"
                  class="hide-error"
                >
                  <el-input
                    :placeholder="'%'"
                    type="number"
                    v-model.number="lineItem.markup"
                    class="fc-input-full-border2 width100"
                  >
                  </el-input>
                </el-form-item>
              </div>
              <div
                v-if="[2, 4].includes(Number(lineItem.type))"
                class="fc-grey7-12 pR10 pT5"
              >
                {{ `per hr` }}
              </div>
            </el-form>
          </td>
          <td v-if="$helpers.taxMode() === 1 && !hideTaxField">
            <el-select
              filterable
              clearable
              v-model="lineItem.tax"
              @clear="clearTaxLineItem(index)"
              class="width100 fc-input-full-border2 fc-form-select-grey-txt"
            >
              <el-option-group key="1" label="Tax Groups">
                <el-option
                  v-for="(tax, index) in groupedTaxes"
                  :key="index"
                  :label="
                    tax.isActive
                      ? `${tax.name} (${tax.rate} %)`
                      : `${tax.name}* (${tax.rate} %)`
                  "
                  :value="tax"
                ></el-option>
              </el-option-group>
              <el-option-group
                key="2"
                :label="groupedTaxes.length > 0 ? 'Individual Taxes' : ''"
              >
                <el-option
                  v-for="(tax, index) in individualTaxes"
                  :key="index"
                  :label="
                    tax.isActive
                      ? `${tax.name} (${tax.rate} %)`
                      : `${tax.name}* (${tax.rate} %)`
                  "
                  :value="tax"
                ></el-option>
              </el-option-group>
            </el-select>
            <div v-if="getTaxRate(lineItem)" class="fc-grey7-12 pR10 pT5">
              {{ `${getTaxRate(lineItem)} %` }}
            </div>
          </td>
          <td class="text-right">
            <div class="fc-black-13 text-right pT18 fc-quotation-total">
              {{ $d3.format(',.2f')(getLineItemTotalCost(lineItem)) }}
            </div>
            <!-- actions -->
            <div
              class="fc-form-tax-actions"
              :class="{ 'quo-actions': lineItem.type === 5 }"
            >
              <div class="flex-middle">
                <i
                  @click="addLineItems()"
                  v-if="lineItems.length - 1 === index"
                  class="el-icon-circle-plus-outline fc-row-add-icon f20 bold pL5"
                ></i>
                <i
                  @click="removeLineItems(index)"
                  v-if="lineItems.length > 1"
                  class="el-icon-remove-outline fc-row-delete-icon f20 bold pL10"
                ></i>
              </div>
            </div>
            <!-- actions -->
          </td>
        </tr>
      </tbody>
    </table>

    <div class="d-flex flex-row">
      <div class="flex-1">
        <div class="flex-middle mT20">
          <div>
            <el-dropdown
              v-if="
                model.shippingCharges == null ||
                  model.miscellaneousCharges == null ||
                  model.adjustmentsCost === null
              "
              @command="action => addCost(action)"
              class=""
            >
              <el-button class="fc-bordrer-btn-grey">
                <i class="el-icon-plus"></i>
                {{ $t('common._common.additional_cost') }}
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :key="1"
                  v-if="model.shippingCharges == null"
                  command="shippingCharges"
                  >{{
                    $t('common.products.add_shipping_charges')
                  }}</el-dropdown-item
                >
                <el-dropdown-item
                  :key="2"
                  v-if="model.miscellaneousCharges == null"
                  command="miscellaneousCharges"
                  >{{
                    $t('common.products.add_misc_charges')
                  }}</el-dropdown-item
                >
                <el-dropdown-item
                  v-if="model.adjustmentsCost === null"
                  :key="3"
                  command="adjustmentsCost"
                  >{{
                    $t('common.products.add_adjustment_costs')
                  }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div v-if="!discountVisibility" class="mL20">
            <div @click="addCost('discount')" class="fc-id bold pointer f14">
              <i class="el-icon-plus"></i>
              {{ $t('common.products.add_discount') }}
            </div>
          </div>
        </div>
      </div>
      <div :class="$helpers.taxMode() === 1 ? 'flexWidthQuote' : 'flexWidth0'">
        <el-form
          ref="lineItem-additionalCosts"
          :rules="additionalCostsRules"
          :model="model"
        >
          <!-- total markup section -->
          <el-col
            v-if="isGlobalMarkup && lineItems.length && showMarkupValue"
            :span="24"
            class="position-relative"
          >
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
            >
              <div class="fc-black-12 text-right fw6">
                {{ `Markup (${getDefaultMarkupValue(model.markup)})%` }}
                <el-tooltip
                  effect="dark"
                  class="m4"
                  :content="markupContent"
                  placement="top-start"
                >
                  <i class="el-icon-warning-outline warning-icon" />
                </el-tooltip>
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td"
            >
              <div class="fc-black-12 text-right bold">
                {{ $d3.format(',.2f')(getTotalMarkupAmount) }}
              </div>
            </el-col>
          </el-col>
          <!-- 000000 -->
          <el-col :span="24" class="position-relative">
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
            >
              <div class="fc-black-12 text-right fw6">
                {{ $t('common._common.sub_total') }}
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td"
            >
              <div class="fc-black-12 text-right bold">
                {{ $d3.format(',.2f')(subTotal) }}
              </div>
            </el-col>
          </el-col>
          <DiscountSegment
            v-if="$helpers.discountMode() === 1"
            :discountVisibility.sync="discountVisibility"
            :model="model"
            :discount="discount"
            :currency="lineItemCurrency"
          />
          <el-col
            v-if="$helpers.discountMode() === 1 && discount"
            :span="24"
            class="position-relative"
          >
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
            >
              <div class="fc-black-12 text-right fw6">
                {{ $t('common.header.adjusted_total') }}
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td"
            >
              <div class="fc-black-12 text-right bold">
                {{ $d3.format(',.2f')(getAdjustedTotal) }}
              </div>
            </el-col>
          </el-col>
          <TaxSegment
            :model="model"
            :individualTaxes="individualTaxes"
            :groupedTaxes="groupedTaxes"
            :showTotalTax="showTotalTax"
            :totalTax="totalTax"
            :taxSplitUp="taxSplitUp"
          />

          <DiscountSegment
            v-if="$helpers.discountMode() === 2"
            :discountVisibility.sync="discountVisibility"
            :model="model"
            :discount="discount"
            :currency="lineItemCurrency"
          />
          <el-col
            v-if="model.shippingCharges != null"
            :span="24"
            class="position-relative"
          >
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
            >
              <div class="fc-black-12 text-right text-uppercase fw6">
                {{ $t('common.header.shipping_charges') }}
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td pT10"
            >
              <div class="fc-black-12 text-right">
                <el-form-item prop="shippingCharges" class="hide-error">
                  <el-input
                    :placeholder="$t('common.header.shipping_charges')"
                    type="number"
                    v-model="model.shippingCharges"
                    class="fc-input-full-border2 width100 fc-input-text-right"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </el-col>
            <div
              @click="removeCost('shippingCharges')"
              class="fc-form-action-total-remove"
            >
              <i class="el-icon-remove-outline fc-row-delete-icon f20 bold"></i>
            </div>
          </el-col>

          <el-col
            v-if="model.miscellaneousCharges != null"
            :span="24"
            class="position-relative"
          >
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
            >
              <div class="fc-black-12 text-right fw6 text-uppercase">
                {{ $t('common.header.misc_charges') }}
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td pT10"
            >
              <div class="fc-black-12 text-right">
                <el-form-item prop="miscellaneousCharges" class="hide-error">
                  <el-input
                    :placeholder="$t('common.header.misc_charges')"
                    type="number"
                    v-model="model.miscellaneousCharges"
                    class="fc-input-full-border2 width100 fc-input-text-right"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </el-col>
            <div
              @click="removeCost('miscellaneousCharges')"
              class="fc-form-action-total-remove"
            >
              <i class="el-icon-remove-outline fc-row-delete-icon f20 bold"></i>
            </div>
          </el-col>

          <el-col
            v-if="model.adjustmentsCost != null"
            :span="24"
            class="position-relative"
          >
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
            >
              <div class="fc-black-12 text-right fw6 text-uppercase">
                {{ $t('common.header.adjustment_cost') }}
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td pT10"
            >
              <div class="fc-black-12 text-right">
                <el-form-item prop="adjustmentsCost" class="hide-error">
                  <el-input
                    :placeholder="$t('common.header.adjustment_cost')"
                    type="number"
                    v-model="model.adjustmentsCost"
                    class="fc-input-full-border2 fc-input-text-right width100"
                  >
                  </el-input>
                </el-form-item>
              </div>
            </el-col>
            <div
              @click="removeCost('adjustmentsCost')"
              class="fc-form-action-total-remove"
            >
              <i class="el-icon-remove-outline fc-row-delete-icon f20 bold"></i>
            </div>
          </el-col>

          <el-col :span="24">
            <el-col
              :span="16"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border fc-form-total-td-bg"
            >
              <div class="fc-black-12 text-right fwBold">
                {{
                  `${$t('common.header.total_amount')} (${lineItemCurrency})`
                }}
              </div>
            </el-col>
            <el-col
              :span="8"
              class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-total-td-bg"
            >
              <div class="label-txt-black text-right fwBold">
                {{ $d3.format(',.2f')(totalAmount) }}
              </div>
            </el-col>
          </el-col>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'
import { API } from '@facilio/api'
import { constructEnumFieldOptions } from '@facilio/utils/utility-methods'
import DiscountSegment from 'src/pages/quotation/components/QuoteFormDiscountSegment'
import TaxSegment from 'src/pages/quotation/components/FormTaxSegment'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import { mapState } from 'vuex'

export default {
  props: [
    'model',
    'field',
    'lineItems',
    'isEdit',
    'module',
    'currencyObj',
    'initialCurrency',
    'hasMultiCurrencyFieldInModel',
  ],
  mixins: [LineItemsMixin],
  components: {
    DiscountSegment,
    TaxSegment,
    FLookupFieldWrapper,
    FNewCurrencyField,
  },
  data() {
    return {
      markupContent: 'The global markup value is used.',
      showLookupField: true,
      currencyData: {},
      fields: {
        itemTypes: {
          lookupModule: { name: 'itemTypes', displayName: 'Item Type' },
          multiple: false,
        },
        toolTypes: {
          lookupModule: { name: 'toolTypes', displayName: 'Tool Type' },
          multiple: false,
        },
        services: {
          lookupModule: { name: 'service', displayName: 'Service' },
          multiple: false,
        },
        labours: {
          lookupModule: { name: 'labour', displayName: 'Labour' },
          multiple: false,
        },
      },
      lineItemTypesMap: {
        1: {
          list: 'itemTypes',
          model: 'itemType',
        },
        2: {
          list: 'toolTypes',
          model: 'toolType',
        },
        3: {
          list: 'services',
          model: 'service',
        },
        4: {
          list: 'labours',
          model: 'labour',
        },
        5: {
          model: 'others',
        },
      },
      lineItemTypes: [
        {
          value: 'items',
          label: this.$t('common.products.items'),
          icon: 'svgs/items',
          iconClass: 'icon icon-sm-md vertical-sub mR10 stroke-grey',
          type: 1,
        },
        {
          value: 'service',
          label: this.$t('common.header.service'),
          icon: 'svgs/service',
          iconClass: 'icon icon-sm-md vertical-sub mR10 fill-grey',
          type: 3,
        },
        {
          value: 'others',
          label: this.$t('common._common.others'),
          icon: 'svgs/pencil',
          iconClass: 'icon icon-sm vertical-sub mR10',
          type: 5,
        },
      ],
    }
  },
  created() {
    this.init()
  },
  computed: {
    ...mapState({
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    hideTaxField() {
      return this.$getProperty(this, 'field.config.hideTaxField')
    },
  },
  watch: {
    'currencyData.currencyCode': {
      handler(newVal, oldVal) {
        if (newVal != oldVal) {
          let lineItems = (this.lineItems || []).map(item => {
            return { ...item, currencyCalculated: false }
          })
          this.updateLineItem(lineItems)
          this.getExchangeRate(oldVal)
        }
      },
      deep: true,
    },
  },
  methods: {
    init() {
      let { lineItems } = this || {}
      this.loading = true
      Promise.all([
        this.loadItemTypes(),
        this.loadTaxes(lineItems),
        this.loadServices(),
        this.loadLineItemsModuleMeta(),
        this.fetchQuoteSetting(),
      ]).then(() => {
        this.setCurrencyData()
        this.loading = false
      })

      if (
        (this.model || {}).discountAmount ||
        (this.model || {}).discountPercentage
      ) {
        if ((this.model || {}).discountPercentage) {
          this.model.discountAmount = null
        }
        this.discountVisibility = true
      }
    },
    getLineItemTypeObj(type) {
      return this.lineItemTypes.find(item => item.type === type) || {}
    },
    addLineItems() {
      let { lineItems } = this
      let newLineItem = deepCloneObject(Constants.QUOTE_LINE_ITEM_DEFAULTS)
      let { currencyCode, exchangeRate } = this.currencyDetails || {}
      lineItems.push({
        ...newLineItem,
        currencyCode,
        exchangeRate,
        currencyCalculated: false,
      })
    },
    updateLineItem(lineItems) {
      this.$emit('update:lineItems', lineItems)
      this.$emit('update:model', { ...this.model, lineItems })
    },
    async loadItemTypes() {
      let { list, error } = await API.fetchAll('itemTypes')
      if (error) {
        let {
          message = this.$t(
            'common._common.error_occured_while_fetching_item_ypes_list'
          ),
        } = error
        this.$message.error(message)
      } else {
        this.itemTypes = list || []
      }
    },
    async loadServices() {
      let { data, error } = await API.get('/v2/service/all')
      if (error) {
        let {
          message = this.$t(
            'common._common.error_occured_while_fetching_services_list'
          ),
        } = error
        this.$message.error(message)
      } else {
        this.services = data.services || []
      }
    },
    lineItemChangeAction(val, index) {
      this.showLookupField = false
      let { type } = val
      let { lineItemTypesMap, lineItems } = this
      if (lineItemTypesMap[type].model !== 'others') {
        let { model, list } = lineItemTypesMap[type]
        let typeId = val[model].id
        let typeData = this[list].filter(type => type.id === typeId)
        let unitPrice = this.$getProperty(typeData, [0, 'sellingPrice'], null)
        let description = this.$getProperty(typeData, [0, 'description'], null)
        unitPrice = !isEmpty(unitPrice) ? unitPrice : null
        let { currencyCode } = typeData || {}

        val = { ...val, unitPrice, description, currencyCalculated: false }
        lineItems = lineItems.map((currVal, currIndex) => {
          if (currIndex === index) return val
          else {
            return currVal
          }
        })
        this.updateLineItem(lineItems)
        let itemObj = {
          type: model,
          typeId,
        }
        this.getExchangeRate(currencyCode, itemObj)
      }

      this.$nextTick(() => {
        this.showLookupField = true
      })
    },
    loadLineItemsModuleMeta() {
      return this.$store
        .dispatch('view/loadModuleMeta', 'quotelineitems')
        .then(({ fields = [] }) => {
          let uomField = fields.filter(field => field.name === 'unitOfMeasure')
          if (!isEmpty(uomField)) {
            let values = this.$getProperty(uomField, [0, 'values'], [])
            this.uomFieldOptions = constructEnumFieldOptions(values || [])
          }
        })
    },
    currentFieldObject(lineItem) {
      let { type } = lineItem || {}
      if (type !== null) {
        return this.fields[`${this.lineItemTypesMap[type].list}`]
      }
    },
    clearTaxLineItem(index) {
      this.lineItems[index].tax = null
    },
  },
}
</script>
<style lang="scss">
.lookup-lineItem .el-input__inner {
  height: 48px !important;
}
</style>
