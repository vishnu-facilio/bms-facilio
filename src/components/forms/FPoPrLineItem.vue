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

    <table class="fc-quote-line-item-table mT20">
      <thead>
        <tr>
          <th :class="nameClass">
            {{ $t('common._common.name_and_description') }}
          </th>
          <th
            v-if="module !== 'requestForQuotation'"
            :class="
              this.$helpers.taxMode() === 1
                ? 'fcQuoteWidth166'
                : 'fcQuoteWidth16'
            "
            class="text-center"
          >
            {{ `${$t('common.header._unit_price')} (${lineItemCurrency})` }}
          </th>
          <th class="text-center" :class="quantityClass">
            {{ $t('common.products.qty') }}
          </th>
          <th class="text-center" :class="uomClass">
            {{ $t('common.products.uom') }}
          </th>
          <th
            class="text-center fcQuoteWidth100"
            v-if="displayTaxField"
            :class="
              $helpers.taxMode() === 1 ? 'fcQuoteWidth100' : 'fcQuoteWidth16'
            "
          >
            {{ $t('common.products.tax') }}
          </th>
          <th
            v-if="module !== 'requestForQuotation'"
            class="text-center fcAmountWidth"
          >
            {{ `${$t('common.header.amounts')} (${lineItemCurrency})` }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(lineItem, index) in lineItems" :key="index">
          <td style="padding: 0;">
            <el-form
              :ref="`lineItem-description-${index}`"
              :rules="nameAndDescriptionValidationRules"
              :model="lineItem"
            >
              <el-row>
                <el-col :span="4">
                  <el-select
                    v-model="lineItem.inventoryType"
                    class="fc-select-suffix-hide fc-select-icon fc-quote-item-select"
                    @change="lineItemChangeAction(lineItem, index)"
                  >
                    <template slot="prefix">
                      <InlineSvg
                        :key="lineItem.inventoryType"
                        :src="getLineItemTypeObj(lineItem.inventoryType).icon"
                        :iconClass="
                          getLineItemTypeObj(lineItem.inventoryType).iconClass
                        "
                      ></InlineSvg>
                      <i class="el-icon-caret-bottom"></i>
                    </template>
                    <el-option
                      v-for="item in lineItemTypes"
                      :key="item.inventoryType"
                      :label="''"
                      :value="item.inventoryType"
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
                <el-col
                  :span="20"
                  v-if="getLineItemsList(lineItem.inventoryType)"
                >
                  <el-form-item
                    :prop="lineItemTypesMap[lineItem.inventoryType].model"
                    :required="true"
                    class="hide-error"
                  >
                    <FLookupFieldWrapper
                      v-if="showLookupField"
                      v-model="
                        lineItem[
                          `${lineItemTypesMap[lineItem.inventoryType].model}`
                        ].id
                      "
                      @recordSelected="lineItemChangeAction(lineItem, index)"
                      :field="currentFieldObject(lineItem)"
                      class="width100 fc-input-full-border-select2 lookup-lineItem fc-quoation-form-select"
                    ></FLookupFieldWrapper>
                  </el-form-item>
                </el-col>
                <el-col
                  :span="24"
                  v-if="getLineItemsList(lineItem.inventoryType)"
                >
                  <div
                    class="fc-black-13 text-left pL10 line-height20 mR10 mT10"
                  >
                    <el-form-item prop="description" class="hide-error">
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
                    class="fc-black-13 text-left pL10 line-height20 mR10 mL10"
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
                        class="fc-input-full-border2 fc-quotation-first-td"
                        :autosize="{ minRows: 2, maxRows: 100 }"
                      >
                      </el-input>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            </el-form>
          </td>
          <td v-if="module !== 'requestForQuotation'">
            <el-form
              v-if="module !== 'requestForQuotation'"
              :ref="`lineItem-unitPrice-${index}`"
              :rules="unitPriceValidationRules"
              :model="lineItem"
              class="table-row"
            >
              <div class="fc-black-13 text-right">
                <el-form-item prop="unitPrice" class="hide-error">
                  <el-input
                    :placeholder="$t('common.header.unit_price')"
                    type="number"
                    v-model.number="lineItem.unitPrice"
                    class="fc-input-full-border2 width100"
                  >
                  </el-input>
                </el-form-item>
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
            <div>
              <el-select
                filterable
                v-model="lineItem.unitOfMeasure"
                class="width100 fc-input-full-border2"
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
          <td v-if="displayTaxField">
            <el-select
              filterable
              clearable
              v-model="lineItem.tax.id"
              class="width100 fc-input-full-border2"
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
                  :value="tax.id"
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
                  :value="tax.id"
                ></el-option>
              </el-option-group>
            </el-select>
            <div v-if="getTaxRate(lineItem)" class="fc-grey7-12 pR10 pT5">
              {{ `${getTaxRate(lineItem)} %` }}
            </div>
          </td>
          <td
            class="text-center rfq-line-items-action"
            v-if="module === 'requestForQuotation'"
          >
            <!-- actions -->
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
            <!-- actions -->
          </td>
          <td class="text-right" v-if="module !== 'requestForQuotation'">
            <div class="fc-black-13 text-right pT18 fc-quotation-total">
              {{ $d3.format(',.2f')(getLineItemTotalCost(lineItem)) }}
            </div>
            <!-- actions -->
            <div class="fc-form-tax-actions">
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

    <!-- additional information section start-->
    <div class="d-flex flex-row">
      <div class="flex-1">
        <div class="flex-middle mT20">
          <div v-if="showDiscount" class="mL20">
            <div @click="addCost('discount')" class="fc-id bold pointer f14">
              <i class="el-icon-plus"></i>
              {{ $t('common.products.add_discount') }}
            </div>
          </div>
        </div>
      </div>
      <div class="flexWidthQuote" v-if="module !== 'requestForQuotation'">
        <el-form
          ref="lineItem-additionalCosts"
          :rules="additionalCostsRules"
          :model="model"
        >
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
              <div class="fc-black-12 text-right bold break-word-all">
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
              <div class="label-txt-black text-right fwBold break-word-all">
                {{ $d3.format(',.2f')(totalAmount) }}
              </div>
            </el-col>
          </el-col>
        </el-form>
      </div>
    </div>
    <!-- additional information section end -->
  </div>
</template>
<script>
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import { constructEnumFieldOptions } from '@facilio/utils/utility-methods'
import DiscountSegment from 'src/pages/quotation/components/QuoteFormDiscountSegment'
import TaxSegment from 'src/pages/quotation/components/FormTaxSegment'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'

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
      showLookupField: true,
      record: null,
      fields: {
        itemTypes: {
          lookupModule: { name: 'itemTypes', displayName: 'Item Type' },
          multiple: false,
        },
        toolTypes: {
          lookupModule: { name: 'toolTypes', displayName: 'Tool Type' },
          multiple: false,
        },
        service: {
          lookupModule: { name: 'service', displayName: 'Service' },
          multiple: false,
        },
      },

      lineItemTypes: [
        {
          value: 'items',
          label: this.$t('common.products.items'),
          icon: 'svgs/items',
          iconClass: 'icon icon-sm-md vertical-sub mR10 stroke-grey',
          inventoryType: 1,
        },
        {
          value: 'tools',
          label: this.$t('common.header.tools'),
          icon: 'svgs/setting',
          iconClass: 'icon icon-md vertical-sub mR10 fill-grey',
          inventoryType: 2,
        },
        {
          value: 'service',
          label: this.$t('common.products._service'),
          icon: 'svgs/service',
          iconClass: 'icon icon-sm-md vertical-sub mR10 fill-grey',
          inventoryType: 3,
        },
        {
          value: 'others',
          label: this.$t('common._common.others'),
          icon: 'svgs/pencil',
          iconClass: 'icon icon-sm vertical-sub mR10',
          inventoryType: 4,
        },
      ],
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
          list: 'service',
          model: 'service',
        },
        4: {
          model: 'others',
        },
      },
      currencyData: {},
      hasMultiCurrencyField: false,
    }
  },
  async created() {
    this.init()
  },
  computed: {
    ...mapState({
      account: state => state.account,
      currencyList: state => state.activeCurrencies,
    }),
    nameClass() {
      return this.module === 'requestForQuotation'
        ? 'name-and-description-rfq'
        : 'name-and-description'
    },
    quantityClass() {
      if (this.module === 'requestForQuotation') {
        return 'rfq-quantity-uom-width'
      }
      return this.$helpers.taxMode() === 1 ? 'fcQuoteWidth13' : 'fcQuoteWidth16'
    },
    uomClass() {
      if (this.module === 'requestForQuotation') {
        return 'rfq-quantity-uom-width'
      }
      return this.$helpers.taxMode() === 1 ? 'fcQuoteWidth10' : 'fcQuoteWidth16'
    },
    showDiscount() {
      return !this.discountVisibility && this.module !== 'requestForQuotation'
    },
    displayTaxField() {
      return (
        this.$helpers.taxMode() === 1 &&
        !this.hideTaxField &&
        this.module !== 'requestForQuotation'
      )
    },
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
    currentFieldObject(lineItem) {
      let { inventoryType } = lineItem || {}
      if (inventoryType !== null) {
        return this.fields[`${this.lineItemTypesMap[inventoryType].list}`]
      }
    },
    init() {
      let { lineItems } = this || {}
      this.loading = true
      Promise.all([
        this.loadTaxes(lineItems),
        this.loadLineItemsModuleMeta(),
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
    async loadRecord(recordId, moduleName) {
      let { [moduleName]: record, error } = await API.fetchRecord(moduleName, {
        id: recordId,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.record = record || {}
      }
    },
    getLineItemTypeObj(type) {
      return this.lineItemTypes.find(item => item.inventoryType === type) || {}
    },
    addLineItems() {
      let { lineItems } = this
      let newLineItem = deepCloneObject(Constants.LINE_ITEM_DEFAULTS)
      let { currencyCode, exchangeRate } = this.currencyDetails || {}
      lineItems.push({
        ...newLineItem,
        currencyCode,
        exchangeRate,
        currencyCalculated: false,
      })
    },
    loadLineItemsModuleMeta() {
      const onMetaSave = ({ fields = [] }) => {
        let uomField = fields.filter(field => field.name === 'unitOfMeasure')
        if (!isEmpty(uomField)) {
          let values = this.$getProperty(uomField, [0, 'values'], [])
          this.uomFieldOptions = constructEnumFieldOptions(values || [])
        }
      }
      if (this.module === 'requestForQuotation') {
        return this.$store
          .dispatch('view/loadModuleMeta', 'requestForQuotationLineItems')
          .then(onMetaSave)
      }
      return this.$store
        .dispatch(
          'view/loadModuleMeta',
          this.module === 'purchaserequest'
            ? 'purchaserequestlineitems'
            : 'purchaseorderlineitems'
        )
        .then(onMetaSave)
    },
    updateLineItem(lineItems) {
      this.$emit('update:lineItems', lineItems)
      this.$emit('update:model', { ...this.model, lineItems })
    },
    async lineItemChangeAction(val, index) {
      this.showLookupField = false
      let { inventoryType } = val
      let { lineItemTypesMap, lineItems } = this
      if (lineItemTypesMap[inventoryType].model !== 'others') {
        let { model, list } = lineItemTypesMap[inventoryType]
        let typeId = val[model].id
        if (!isEmpty(typeId)) {
          await this.loadRecord(typeId, list)
          let typeData = this.record
          let unitPrice = this.$getProperty(
            typeData,
            'lastPurchasedPrice',
            null
          )
          let description = this.$getProperty(typeData, 'description', null)
          unitPrice =
            !isEmpty(unitPrice) && this.module !== 'requestForQuotation'
              ? unitPrice
              : null

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
      }
      this.$nextTick(() => {
        this.showLookupField = true
      })
    },
  },
}
</script>
<style lang="scss">
.lookup-lineItem .el-input__inner {
  height: 48px !important;
}
</style>
<style lang="scss" scoped>
.flexWidthQuote {
  flex: 0 0 52% !important;
}
.fcAmountWidth {
  width: 15.2% !important;
}
.rfq-line-items-action {
  border-bottom: transparent !important;
  border-right: transparent !important;
}
.name-and-description-rfq {
  width: 38%;
}
.name-and-description {
  width: 33.3333%;
}
.rfq-quantity-uom-width {
  width: 23% !important;
}

.lineItem-currency-header {
  font-size: 0.875rem;
  font-weight: 500;
  line-height: 1;
  letter-spacing: 0.3px;
  color: #324056;
}
</style>
