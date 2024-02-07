<script>
import { isEmpty, isNumber, isArray } from '@facilio/utils/validation'
import {
  validateForDropDownField,
  validateForPositiveNumber,
  validateForNumber,
} from 'util/form-validator'
import { API } from '@facilio/api'

const MODULE_LINEITEM_MAP = {
  purchaserequest: 'purchaserequestlineitems',
  purchaseorder: 'purchaseorderlineitems',
  quote: 'quotelineitems',
}
import round from 'lodash/round'

export default {
  data() {
    return {
      quotationsetting: null,
      quantityValidationRules: {
        quantity: [
          {
            required: true,
            message: 'Required',
            trigger: 'blur',
          },
          {
            validator: validateForPositiveNumber,
            trigger: 'blur',
          },
        ],
      },
      nameAndDescriptionValidationRules: {
        itemType: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
        toolType: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
        service: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
      },
      descriptionValidationRules: {
        description: [
          {
            required: true,
            message: 'Please enter description',
            trigger: 'blur',
          },
        ],
        itemType: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
        toolType: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
        service: [
          {
            required: true,
            message: 'Please input',
            trigger: 'change',
            validator: validateForDropDownField,
          },
        ],
      },
      additionalCostsRules: {
        miscellaneousCharges: [
          {
            validator: validateForPositiveNumber,
            trigger: 'blur',
          },
        ],
        shippingCharges: [
          {
            validator: validateForPositiveNumber,
            trigger: 'blur',
          },
        ],
        discountPercentage: [
          {
            validator: validateForPositiveNumber,
            trigger: 'blur',
          },
        ],
        discountAmount: [
          {
            validator: validateForPositiveNumber,
            trigger: 'blur',
          },
        ],
        adjustmentsCost: [
          {
            validator: validateForNumber,
            trigger: 'blur',
          },
        ],
      },
      individualTaxes: [],
      groupedTaxes: [],
      allTaxes: [],
      discountVisibility: false,
      itemTypes: [],
      nonVendorItems: [],
      toolTypes: [],
      vendorItems: [],
      service: [],
      labours: [],
      uomFieldOptions: [],
      loading: true,
      currencyInfoObj: {},
      isDefaultCurrency: false,
    }
  },
  computed: {
    showMarkupValue() {
      // this showmarkup used to create the quote
      if (
        this.customerType !== null &&
        this.customerType === 4 &&
        this.quotationsetting
      ) {
        return this.quotationsetting.vendorquote
      } else if (
        this.customerType !== null &&
        (this.customerType === 1 || this.customerType === 2)
      ) {
        return this.quotationsetting?.enduserquote
      }
      return this.quotationsetting?.showMarkupValue || false
    },
    customerType() {
      return this.model?.customerType?.id ? this.model.customerType.id : null
    },
    isGlobalMarkup() {
      if (this.quotationsetting?.markupdisplayMode) {
        if (this.quotationsetting.markupdisplayMode === 2) {
          return false
        }
      }
      return true
    },
    unitPriceValidationRules() {
      return {
        unitPrice: [
          {
            required: !this.module === 'requestForQuotation',
            message: 'Required',
            trigger: 'blur',
          },
          {
            validator: validateForPositiveNumber,
            trigger: 'blur',
          },
        ],
      }
    },
    totalAmount() {
      return (
        Number(this.subTotal) +
        Number(this.totalTax) +
        Number(this.model.shippingCharges || 0) +
        Number(this.model.adjustmentsCost || 0) +
        Number(this.model.miscellaneousCharges || 0) +
        (Number(this.discount) || 0)
      )
    },
    totalTax() {
      let totalTax = 0
      let markup = this.model.markup

      if (this.$helpers.taxMode() === 1) {
        this.lineItems.forEach(item => {
          let relativeCost = Number(this.getRelativeLineItemCost(item))
          if (this.showMarkupValue && this.isGlobalMarkup) {
            let markupValue = this.getGlobalMarkupvalue(item, markup)
            if (markupValue !== null && isNumber(markupValue)) {
              relativeCost = markupValue + relativeCost
            }
          }
          let taxId = this.$getProperty(item, 'tax.id', -1)
          let tax = this.getTaxObject(taxId)
          let taxRate = this.$getProperty(tax, 'rate', 0)
          totalTax =
            totalTax + Number(((relativeCost * taxRate) / 100).toFixed(2))
        })
      } else if (this.$helpers.taxMode() === 2) {
        let { subTotal = 0 } = this
        let total = Number(subTotal)
        if (this.$helpers.discountMode() === 1) {
          // Discount itself will be negative
          total += Number(this.discount)
        }
        let taxId = this.$getProperty(this.model, 'tax.id', -1)
        let tax = this.getTaxObject(taxId)
        let taxRate = this.$getProperty(tax, 'rate', 0)
        totalTax = (total * taxRate) / 100
      }
      return Number(totalTax)
    },
    discount() {
      let { subTotal } = this
      let discount = 0
      let total = Number(subTotal)
      if (this.$helpers.discountMode() === 2) {
        // Beware of recursion
        total += Number(this.totalTax)
      }
      if (!isEmpty(this.model.discountAmount)) {
        discount = Number(this.model.discountAmount)
      } else if (!isEmpty(this.model.discountPercentage)) {
        discount = (total * Number(this.model.discountPercentage)) / 100
      }
      return Number(-discount) || 0
    },
    subTotal() {
      let subTotal = 0
      this.lineItems.forEach(item => {
        let lineItemCost = this.getLineItemTotalCost(item)
        subTotal += Number(lineItemCost) || 0
      })
      return Number(subTotal) + Number(this.getTotalMarkupAmount)
    },
    getAdjustedTotal() {
      let { subTotal, discount } = this
      return Number(subTotal + discount) || 0
    },
    getTotalMarkupAmount() {
      let totlaMarkup = 0
      let markup = this.model.markup
      this.lineItems.forEach(lineItem => {
        totlaMarkup += this.getGlobalMarkupvalue(lineItem, markup)
      })
      return totlaMarkup
    },
    taxSplitUp() {
      let taxSplitUpMap = {}
      let markup = this.model.markup

      if (this.$helpers.taxMode() === 1) {
        this.lineItems.forEach(lineItem => {
          let taxId = this.$getProperty(lineItem, 'tax.id', -1)
          if (taxId > 0) {
            let taxObj = this.getTaxObject(taxId)
            let lineItemCost = Number(this.getRelativeLineItemCost(lineItem))
            if (this.showMarkupValue && this.isGlobalMarkup) {
              let markupValue = this.getGlobalMarkupvalue(lineItem, markup)
              if (markupValue !== null && isNumber(markupValue)) {
                lineItemCost = markupValue + lineItemCost
              }
            }
            let taxRate
            if (taxObj && taxObj.type === 1) {
              taxRate = Number(taxObj.rate)
              let taxAmount = (lineItemCost * taxRate) / 100
              taxAmount = Number(taxAmount)
              if (taxAmount && taxAmount !== 0) {
                if (taxSplitUpMap[taxObj.id]) {
                  taxSplitUpMap[taxObj.id].taxAmount += Number(taxAmount)
                } else {
                  taxSplitUpMap[taxObj.id] = {
                    tax: taxObj,
                    taxAmount: Number(taxAmount),
                  }
                }
              }
            } else if (taxObj && taxObj.type === 2) {
              taxObj.childTaxes.forEach(child => {
                taxRate = Number(child.rate)
                let taxAmount = (lineItemCost * taxRate) / 100
                taxAmount = Number(taxAmount)
                if (taxAmount !== 0) {
                  if (taxSplitUpMap[child.id]) {
                    taxSplitUpMap[child.id].taxAmount += Number(taxAmount)
                  } else {
                    taxSplitUpMap[child.id] = {
                      tax: child,
                      taxAmount: Number(taxAmount),
                    }
                  }
                }
              })
            }
          }
        })
      } else if (this.$helpers.taxMode() === 2) {
        let { subTotal = 0 } = this
        let taxId = this.$getProperty(this.model, 'tax.id')
        if (!isEmpty(taxId)) {
          let taxObj = this.getTaxObject(taxId)
          if ((taxObj || {}).type === 1) {
            taxSplitUpMap[taxId] = {
              tax: taxObj,
              taxAmount: this.totalTax,
            }
          } else if ((taxObj || {}).type === 2) {
            let taxRate = 0
            let total = subTotal
            if (this.$helpers.discountMode() === 1) {
              total += this.discount
            }
            taxObj.childTaxes.forEach(child => {
              taxRate = Number(child.rate)
              let taxAmount = (total * taxRate) / 100
              taxAmount = Number(taxAmount)
              if (taxAmount !== 0) {
                if (taxSplitUpMap[child.id]) {
                  taxSplitUpMap[child.id].taxAmount += Number(taxAmount)
                } else {
                  taxSplitUpMap[child.id] = {
                    tax: child,
                    taxAmount: Number(taxAmount),
                  }
                }
              }
            })
          }
        }
      }
      return taxSplitUpMap
    },
    showTotalTax() {
      return Object.keys(this.taxSplitUp || {}).length > 1
    },
    lineItemCurrency() {
      return this.currencyDetails?.symbol || this.$currency
    },
    isMulticurrencyEnabled() {
      let { multiCurrencyEnabled } =
        this.$getProperty(this.account, 'data.currencyInfo') || {}
      return multiCurrencyEnabled
    },
    canShowCurrencyDropDown() {
      let { isMulticurrencyEnabled, isDefaultCurrency } = this
      return !isDefaultCurrency && isMulticurrencyEnabled
    },
    currencyDetails: {
      get() {
        let { currencyCode, exchangeRate, symbol, initialCurrencyCode } =
          this.currencyData || this.model || {}

        return { currencyCode, exchangeRate, symbol, initialCurrencyCode }
      },
      set(value) {
        let { currencyCode, exchangeRate } = value || {}
        this.currencyData = { ...(this.currencyData || {}), ...value }
        let lineItems = this.lineItems.map(item => {
          return { ...item, currencyCode, exchangeRate }
        })
        this.$emit('update:model', {
          ...this.model,
          currencyCode,
          exchangeRate,
          lineItems,
        })
      },
    },
  },
  created() {
    this.fetchMetaFields()
  },
  methods: {
    async fetchQuoteSetting() {
      if (this.module === 'quote') {
        const route = 'v1/setup/quotesetting/fetch'
        let { data, error } = await API.get(route)
        if (error) {
          this.$message.error(error.message || 'Error occurred')
          return false
        } else {
          if (data?.data?.quotationsetting) {
            this.quotationsetting = data.data.quotationsetting
          }
          return true
        }
      }
    },
    getDefaultMarkupValue(markup) {
      let markupValue = 0
      let {
        markupDefaultValue = 0,
        globalMarkupValue = 0,
        markupdisplayMode,
      } = this.quotationsetting

      if (markupdisplayMode && markupdisplayMode === 1) {
        return globalMarkupValue
      }

      if (markupDefaultValue) {
        markupValue = markupDefaultValue
      }
      if (markup) {
        markupValue = markup
      }
      return markupValue
    },
    getPercetageOfValue(value, percentage, quantity = 1) {
      let {
        markupDefaultValue = 0,
        globalMarkupValue = 0,
        markupdisplayMode,
      } = this.quotationsetting

      let per = 0
      if (markupdisplayMode === 1) {
        per = globalMarkupValue
      } else if (markupDefaultValue) {
        per = markupDefaultValue
      }

      if (percentage) {
        per = percentage
      }
      if (value && per !== null) {
        let perOfV = (per / 100) * value * quantity
        return round(perOfV, 3)
      } else {
        return 0
      }
    },
    getMarkupvalue(lineItem) {
      if (!this.showMarkupValue) {
        return 0
      }
      let { markup, unitPrice } = lineItem
      return this.getPercetageOfValue(unitPrice, markup)
    },
    getGlobalMarkupvalue(lineItem, markup) {
      if (!this.showMarkupValue) {
        return 0
      }
      let { unitPrice, quantity } = lineItem
      if (this.isGlobalMarkup) {
        return this.getPercetageOfValue(unitPrice, markup, quantity)
      } else {
        return 0
      }
    },
    validateForm() {
      let isValid = true
      let { model } = this || {}
      let { lineItems } = model || {}
      let { requestForQuotationLineItems } = model || {}
      let { module } = this || {}
      let requestedLineItems = {}
      if (module === 'requestForQuotation') {
        requestedLineItems = requestForQuotationLineItems
      } else {
        requestedLineItems = lineItems
      }
      requestedLineItems.forEach((lineItem, index) => {
        let descriptionFormRef = this.$refs[`lineItem-description-${index}`]
        let unitPriceFormRef = this.$refs[`lineItem-unitPrice-${index}`]
        let quantityFormRef = this.$refs[`lineItem-quantity-${index}`]
        if (!isEmpty(descriptionFormRef)) {
          descriptionFormRef[0].validate(valid => {
            if (!valid) {
              isValid = false
            }
          })
        }
        if (!isEmpty(unitPriceFormRef)) {
          unitPriceFormRef[0].validate(valid => {
            if (!valid) {
              isValid = false
            }
          })
        }
        if (!isEmpty(quantityFormRef)) {
          quantityFormRef[0].validate(valid => {
            if (!valid) {
              isValid = false
            }
          })
        }
      })
      let additionalCostForm = this.$refs[`lineItem-additionalCosts`]
      if (!isEmpty(additionalCostForm)) {
        additionalCostForm.validate(valid => {
          if (!valid) {
            isValid = false
          }
        })
      }
      return isValid
    },
    removeLineItems(index) {
      let { lineItems } = this
      lineItems.splice(index, 1)
    },
    getLineItemsList(type) {
      let listModel = (this.lineItemTypesMap[`${type}`] || {}).list
      return listModel ? this[`${listModel}`] : false
    },
    typeChangeAction(val) {
      val.unitPrice = null
    },
    async loadTaxes(lineItems = []) {
      let { list, error } = await API.fetchAll(`tax`, {
        viewName: 'all',
      })
      if (error) {
        let { message = 'Error Occured while fetching Tax list' } = error
        this.$message.error(message)
      } else {
        list = list || []
        let lineItemsTaxIdsList = lineItems.map(lineItem => {
          return this.$getProperty(lineItem, 'tax.id')
        })
        this.allTaxes = list
        this.individualTaxes = list.filter(
          t =>
            t.type === 1 && (lineItemsTaxIdsList.includes(t.id) || t.isActive)
        )
        this.groupedTaxes = list.filter(
          t =>
            t.type === 2 && (lineItemsTaxIdsList.includes(t.id) || t.isActive)
        )
        if (
          !this.isEdit &&
          this.$helpers.taxMode() === 2 &&
          !isEmpty(list) &&
          isArray(list) &&
          list.length === 1
        ) {
          this.$setProperty(
            this.model,
            'tax.id',
            this.$getProperty(list, [0, 'id'])
          )
        }
      }
    },
    getTaxAmount(lineItem) {
      return this.$getProperty(lineItem, 'taxAmount', '---')
    },
    calculateTaxAmount(lineItem) {
      let taxAmount = 0
      let { quantity, counterPrice } = lineItem || {}
      let taxRate = this.getTaxRate(lineItem)
      if (!isEmpty(quantity) && !isEmpty(counterPrice) && !isEmpty(taxRate)) {
        let amount = quantity * counterPrice
        taxAmount = Number(((amount * taxRate) / 100).toFixed(2))
      }
      this.$set(lineItem, 'taxAmount', taxAmount)
      return taxAmount
    },
    getTaxPercentage(lineItem) {
      let percentage = this.getTaxRate(lineItem)
      return !isEmpty(percentage) ? `${percentage} %` : '---'
    },
    getTaxRate(lineItem) {
      let taxId = Number(this.$getProperty(lineItem, 'tax.id', -1))
      let taxObj
      if (taxId > 0) {
        taxObj = this.getTaxObject(taxId)
      }
      let { rate } = taxObj || {}
      return rate
    },
    getTaxObject(id) {
      let { allTaxes } = this
      return (allTaxes || []).find(tax => tax.id === id)
    },
    getLineItemTotalCost(lineItem) {
      let totalCost = 0
      if (this.showMarkupValue && !this.isGlobalMarkup) {
        let { quantity, unitPrice } = lineItem
        totalCost =
          (Number(unitPrice) + Number(this.getMarkupvalue(lineItem))) *
          Number(quantity)
        return isNumber(totalCost) ? totalCost : 0
      } else {
        totalCost = Number(lineItem.unitPrice) * Number(lineItem.quantity)
        return isNumber(totalCost) ? totalCost : 0
      }
    },
    addCost(cost) {
      if (cost === 'discount') {
        this.discountVisibility = true
      } else {
        this.model[`${cost}`] = 0
      }
    },
    removeCost(cost) {
      this.model[`${cost}`] = null
    },
    getRelativeLineItemCost(lineItem) {
      let lineItemCost = this.getLineItemTotalCost(lineItem)
      if (this.$helpers.discountMode() === 1) {
        let { subTotal, discount } = this
        let subTotalAfterDiscount = Number(subTotal) + Number(discount)
        return subTotalAfterDiscount * (lineItemCost / subTotal)
      } else if (this.$helpers.discountMode() === 2) {
        return lineItemCost
      }
      return 0
    },
    getExchangeRate(prevCurrencyCode, lineItemObj) {
      let { currencyList, currencyDetails, currencyObj } = this
      if (!isEmpty(currencyList)) {
        let { currencyCode } = currencyDetails || {}

        let result = (currencyList || []).find(
          currency => currencyCode === currency.currencyCode
        )

        prevCurrencyCode = prevCurrencyCode || currencyObj?.oldCurrencyCode
        let previousCurrency = (currencyList || []).find(
          currency => prevCurrencyCode === currency.currencyCode
        )

        let { exchangeRate } = result || {}
        let { currencyCode: baseCode } =
          this.$getProperty(this.account, 'data.currencyInfo') || {}
        let isBaseCurrencyCode = currencyCode === baseCode
        let isPrevBaseCurrencyCode = prevCurrencyCode === baseCode

        let oldExchangeRate = previousCurrency?.exchangeRate

        let { currencyCode: initCode, exchangeRate: initRate } =
          this.initialCurrency || {}
        if (currencyCode === initCode) {
          exchangeRate = initRate || exchangeRate
        }
        this.currencyInfoObj = {
          exchangeRate,
          oldExchangeRate,
          isBaseCurrencyCode,
          isPrevBaseCurrencyCode,
          currencyCode,
          lineItemObj,
        }
      }

      this.$emit('calculateExchangeRate', this.currencyInfoObj || {})
    },
    setCurrencyData() {
      let { currencyCode, exchangeRate, initialCurrencyCode } =
        this.currencyObj || this.model || {}
      this.currencyDetails = { currencyCode, exchangeRate, initialCurrencyCode }
    },
    setCurrencyCodeOnChange(currencyCode, exchangeRate, symbol) {
      if (!isEmpty(currencyCode))
        this.currencyDetails = { currencyCode, exchangeRate, symbol }
      this.$emit('setCurrencyCode', currencyCode, exchangeRate)
    },
    async fetchMetaFields() {
      let { module } = this
      if (isEmpty(module)) {
        return
      }

      let moduleName = MODULE_LINEITEM_MAP[module]
      let { data } = await API.get('/module/metafields', {
        moduleName,
      })
      let lineItemFields = this.$getProperty(data, 'meta.fields', [])
      let multiCurrencyField = (lineItemFields || []).find(
        fld => fld.displayTypeEnum === 'MULTI_CURRENCY'
      )
      this.hasMultiCurrencyField = !isEmpty(multiCurrencyField)
    },
  },
}
</script>
