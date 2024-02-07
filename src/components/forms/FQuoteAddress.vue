<template>
  <div class="fc-form-address">
    <el-row class="d-flex">
      <el-col :span="10">
        <div
          class="fc-black-12 text-left bold height40 line-height40 position-relative fc-text-underline-half mL10 mR10"
        >
          BILLING ADDRESS
        </div>
      </el-col>
      <el-col :span="10">
        <div
          class="fc-black-12 text-left bold height40 line-height40 position-relative fc-text-underline-half mL10 mR10 mL63"
        >
          SHIPPING ADDRESS
        </div>
      </el-col>
      <el-col :span="4" class="flex-middle">
        <el-dropdown @command="action => copyBillToAddress(action)" class="">
          <el-button class="fc-bordrer-btn-grey">
            COPY ADDRESS <i class="el-icon-arrow-down"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :key="1" command="billTo"
              >Billing to Shipping</el-dropdown-item
            >
            <el-dropdown-item :key="2" command="shipTo"
              >Shipping to Billing</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
        <span
          @click="resetAddress()"
          class="fc-link3 f14 fc-text-underline2 pointer mL20"
          >Reset</span
        >
      </el-col>
    </el-row>
    <div class="fc-form-single-el-block mT30">
      <el-row class="d-flex">
        <el-col :span="10">
          <el-form-item
            :label="$t('setup.setup_profile.street')"
            :prop="`billToAddress.street`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="$t('setup.setup_profile.enter_street')"
              v-model="(model.billToAddress || {}).street"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.city_district')"
            :prop="`billToAddress.city`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="$t('setup.setup_profile.entre_city_district')"
              v-model="(model.billToAddress || {}).city"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.state_province_county')"
            :prop="`billToAddress.state`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="
                $t('setup.setup_profile.enter_state_province_county')
              "
              v-model="(model.billToAddress || {}).state"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.zipcode')"
            :prop="`billToAddress.zip`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="$t('setup.setup_profile.enter_zipcode')"
              type="number"
              v-model="(model.billToAddress || {}).zip"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.country')"
            :prop="`billToAddress.country`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-select
              filterable
              clearable
              v-model="(model.billToAddress || {}).country"
              :placeholder="$t('setup.setup_profile.enter_country')"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="country in countryList"
                :key="country.value"
                :label="country.label"
                :value="String(country.value)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="10" class="fc-flex50">
          <el-form-item
            :label="$t('setup.setup_profile.street')"
            :prop="`shipToAddress.street`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="$t('setup.setup_profile.enter_street')"
              v-model="(model.shipToAddress || {}).street"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.city_district')"
            :prop="`shipToAddress.city`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="$t('setup.setup_profile.entre_city_district')"
              v-model="(model.shipToAddress || {}).city"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.state_province_county')"
            :prop="`shipToAddress.state`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="
                $t('setup.setup_profile.enter_state_province_county')
              "
              v-model="(model.shipToAddress || {}).state"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.zipcode')"
            :prop="`shipToAddress.zip`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-input
              :placeholder="$t('setup.setup_profile.enter_zipcode')"
              type="number"
              v-model="(model.shipToAddress || {}).zip"
              class="fc-input-full-border2"
            ></el-input>
          </el-form-item>
          <el-form-item
            :label="$t('setup.setup_profile.country')"
            :prop="`shipToAddress.country`"
            label-width="0px"
            class="section-items form-two-column"
          >
            <el-select
              filterable
              clearable
              v-model="(model.shipToAddress || {}).country"
              :placeholder="$t('setup.setup_profile.enter_country')"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="country in countryList"
                :key="country.value"
                :label="country.label"
                :value="String(country.value)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import countries from 'util/data/countries'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { isWebTabsEnabled } from '@facilio/router'
import VueRouter from 'vue-router'

export default {
  props: ['model'],
  data() {
    return {
      countryList: countries,
      copyAddress: true,
      tenantLoading: false,
      quoteFormsList: [],
      customerTypeData: null,
    }
  },
  computed: {
    getRedirectFormId() {
      let { id = -1 } = this.customerType
      if (id > -1) {
        let customerTypeToFormsMap = {
          1: 'tenantquoteform',
          2: 'clientquoteform',
          3: 'default_quote_web_maintenance',
          4: 'vendorquoteform',
        }
        if (id) {
          let form = this.quoteFormsList.find(rt => {
            if (rt.name === customerTypeToFormsMap[id]) {
              return rt
            } else {
              return null
            }
          })
          if (form?.id) {
            return form.id
          }
        }
      } else {
        if (
          this.quoteFormsList.length &&
          this.quoteFormsList[0] &&
          this.quoteFormsList[0].id
        ) {
          return this.quoteFormsList[0].id
        }
      }
      return null
    },
    tenantId() {
      return this.$getProperty(this.model, 'tenant.id', -1)
    },
    billToAddress() {
      return this.$getProperty(this.getRouterQuerryData, 'billToAddress', -1)
    },
    customerType() {
      return this.$getProperty(this.model, 'customerType', -1)
    },
    getRouterQuerryData() {
      let { $route } = this
      let { query } = $route || {}

      if (!isEmpty(query)) {
        return query
      }
      return null
    },
  },
  watch: {
    tenantId() {
      this.fillAddressFields()
    },
    billToAddress() {
      this.fillQuerryAddress()
    },
    customerType: {
      handler() {
        if (this.quoteFormsList.length && this.quoteFormsList.length > 1) {
          this.handleChangeCustomertype()
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.loadQuoteDependencies()

    if (this.billToAddress) {
      this.fillQuerryAddress()
    }
  },
  methods: {
    setDefaultCustomerType() {
      if (this.customerType?.id) {
        this.customerTypeData = this.customerType.id
      }
    },
    clearFormData() {
      this.$set(this.model, 'billToAddress', {})
    },
    async loadQuoteDependencies() {
      this.setDefaultCustomerType()
      await this.loadQuoteForms()
    },
    async loadQuoteForms() {
      let url = `/v2/forms?moduleName=${'quote'}`

      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { forms = [] } = data || {}
        this.quoteFormsList = forms
      }
    },
    handleChangeCustomertype() {
      if (
        this.customerType?.id &&
        this.customerTypeData !== this.customerType.id
      ) {
        let dialogObj = {
          title: 'Change Quote type',
          htmlMessage:
            'Changing the Quote type may impact certain fields within the form. Do you want to proceed?',
          rbClass: 'confirm-dialog-btn confirm-dialog-btn2',
          rbLabel: 'proceed',
        }
        this.$dialog.confirm(dialogObj).then(value => {
          if (value) {
            this.changeQuoteForm()
          }
        })
      }
    },
    async changeQuoteForm() {
      await this.clearFormData()
      this.redirectToQuotationForm()
    },
    redirectToQuotationForm() {
      if (isWebTabsEnabled()) {
        // let { name } = findRouteForModule('quote', pageTypes.CREATE) || {}
        let query = this.$helpers.cloneObject(this.getRouterQuerryData) || {}
        if (this.getRedirectFormId) {
          query['formId'] = this.getRedirectFormId
        }
        delete query.billToAddress
        delete query.customerType
        if (this.customerType?.id && this.customerType.id === 4) {
          delete query.vendor
        }
        if (this.customerType?.id) {
          this.$set(query, 'customerType', this.customerType.id)
        }
        const currentPath = this.$route.path

        this.$router.push({ path: currentPath, query: query }).catch(err => {
          if (
            VueRouter.isNavigationFailure(
              err,
              VueRouter.NavigationFailureType.duplicated
            )
          ) {
            // Handle the redundant navigation warning
          } else {
            // Handle other errors
            throw err
          }
        })
      }
    },
    copyBillToAddress(action) {
      if (action === 'billTo') {
        this.model.shipToAddress = this.$helpers.cloneObject(
          this.model.billToAddress
        )
      } else if (action === 'shipTo') {
        this.model.billToAddress = this.$helpers.cloneObject(
          this.model.shipToAddress
        )
      }
    },
    resetAddress() {
      this.model.shipToAddress = this.$helpers.cloneObject(
        Constants.ADDRESS_FIELD_DEFAULTS
      )
      this.model.billToAddress = this.$helpers.cloneObject(
        Constants.ADDRESS_FIELD_DEFAULTS
      )
    },
    fillQuerryAddress() {
      let billToAddress = JSON.parse(this.billToAddress)
      if (billToAddress) {
        this.copyAddressValueTo(billToAddress, 'billToAddress')
      }
    },
    async fillAddressFields() {
      if (this.tenantId > 0) {
        let { tenant, error } = await API.fetchRecord('tenant', {
          id: this.tenantId,
        })
        if (!isEmpty(error)) {
          let { message = 'Error Occured while fetching Tenant' } = error
          this.$message.error(message)
        } else {
          if (!isEmpty(tenant.address)) {
            let dialogObj = {
              title: 'Fill Address',
              htmlMessage: 'Copy Tenant address to Billing address.',
              rbClass: 'confirm-dialog-btn',
              rbLabel: 'Copy',
            }
            this.$dialog.confirm(dialogObj).then(value => {
              if (value) {
                this.copyAddressValueTo(tenant.address, 'billToAddress')
              }
            })
          }
        }
      }
    },
    copyAddressValueTo(address, field) {
      address = this.$helpers.cloneObject(address)
      let loactionKeys = [
        'street',
        'city',
        'state',
        'zip',
        'lat',
        'lng',
        'country',
      ]
      loactionKeys.forEach(key => {
        this.$set(this.model[field], key, address[key])
      })
    },
  },
}
</script>
<style lang="scss">
.confirm-dialog-btn2 {
  width: 100%;
}
</style>
