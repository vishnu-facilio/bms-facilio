<template>
  <el-dialog
    :visible="true"
    :fullscreen="false"
    :before-close="close"
    :title="$t('common._common.add_vendors')"
    width="40%"
    open="top"
    custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
    :append-to-body="true"
  >
    <table class="setting-list-view-table store-table">
      <thead class="setup-dialog-thead">
        <tr>
          <th class="setting-table-th setting-th-text" style="width: 240px;">
            {{ this.$t('common.products.vendor') }}
          </th>
          <th class="setting-table-th setting-th-text" style="width: 240px;">
            {{ this.$t('common.header._price') }}
          </th>
          <th></th>
        </tr>
      </thead>
      <div v-if="isLoading">
        <spinner :show="isLoading"></spinner>
      </div>

      <tbody v-else>
        <tr
          v-for="(vendorObj, index) in serviceVendorList"
          :key="index"
          class="visibility-visible-actions"
        >
          <td class="module-builder-td">
            <el-select
              filterable
              clearable
              v-model="vendorObj.vendorId"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(vendor, index) in vendors"
                :key="index"
                :label="vendor.name"
                :value="vendor.id"
              >
              </el-option>
            </el-select>
          </td>
          <td
            v-if="checkForMultiCurrency('lastPrice', metaFieldTypeMap)"
            class="item-unit-price-currency"
          >
            <FNewCurrencyField
              :key="vendorObj.currencyCode"
              v-model="vendorObj.lastPrice"
              :isSubform="true"
              :moduleData="vendorObj"
              @setCurrencyCodeInSubform="
                (code, rate) => setCurrencyCodeInSubform(code, rate, index)
              "
              @calculateExchangeRate="
                rateObj => calculateExchangeRate(rateObj, index)
              "
            ></FNewCurrencyField>
          </td>
          <td class="module-builder-td">
            <el-input
              placeholder="Price"
              type="number"
              v-model="vendorObj.lastPrice"
              class="fc-input-full-border-select2 duration-input"
            ></el-input>
          </td>
          <td>
            <div class="visibility-hide-actions export-dropdown-menu">
              <span
                @click="addVendorEntry()"
                :title="$t('common._common.add')"
                v-tippy
              >
                <img src="~assets/add-icon.svg" class="mR10 mT10" />
              </span>
              <span
                v-if="serviceVendorList.length > 1"
                @click="removeVendorEntry(index)"
                :title="$t('common._common.remove')"
                v-tippy
              >
                <img src="~assets/remove-icon.svg" class="mT10" />
              </span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="close">{{
        this.$t('common.roles.cancel')
      }}</el-button>
      <el-button class="modal-btn-save" type="primary" @click="save()">{{
        this.$t('common._common.add')
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import {
  getCalculatedCurrencyValue,
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['serviceVendors', 'moduleName', 'id'],
  components: { FNewCurrencyField },
  data() {
    return {
      vendors: [],
      serviceVendorList: [],
      isLoading: false,
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  async created() {
    await this.loadVendors()

    this.serviceVendorList = this.serviceVendors.map(v => {
      let { vendor, lastPrice, id } = v
      return { vendorId: vendor.id, lastPrice, id }
    })
    if (isEmpty(this.serviceVendorList)) {
      this.serviceVendorList = [{ vendorId: null, lastPrice: null }]
    }

    this.metaFieldTypeMap = await getMetaFieldMapForModules('serviceVendors')
  },
  methods: {
    async loadVendors() {
      this.isLoading = true
      let { data, error } = await API.get('/v2/vendors/view/all')

      if (!error) {
        this.vendors = data.vendors || []
      }
      this.isLoading = false
    },

    addVendorEntry() {
      this.serviceVendorList.push({ vendorId: null, lastPrice: null })
    },

    removeVendorEntry(index) {
      this.serviceVendorList.splice(index, 1)
    },
    async patchUpdateRecord(serviceVendors) {
      let { id, moduleName } = this
      let param = {
        id,
        data: {
          serviceVendors,
        },
      }
      let { [moduleName]: data, error } = await API.updateRecord(
        moduleName,
        param
      )

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          this.$t('common._common.vendor_updated_succesfully')
        )
        this.$emit('onSave', data.serviceVendors)
        this.close()
      }
    },

    save() {
      let serviceVendor = this.serviceVendorList
        .filter(v => v.vendorId && v.lastPrice)
        .map(v => {
          let { vendorId, lastPrice, id, currencyCode, exchangeRate } = v
          let vendorObj = {
            lastPrice: parseInt(lastPrice),
            vendor: { id: vendorId },
            currencyCode,
            exchangeRate,
          }
          if (id) {
            vendorObj = { ...vendorObj, id }
          }
          return vendorObj
        })

      if (isEmpty(serviceVendor)) {
        this.$message.error(this.$t('common._common.atleast_vendor_required'))
        return
      }
      this.patchUpdateRecord(serviceVendor)
    },
    close() {
      this.$emit('onClose')
    },
    setCurrencyCodeInSubform(currencyCode, exchangeRate, index) {
      let { serviceVendorList } = this
      let serviceVendor = serviceVendorList[index]
      serviceVendorList.splice(index, 1, {
        ...serviceVendor,
        currencyCode,
        exchangeRate,
      })
    },
    calculateExchangeRate(rateObj, index) {
      let { serviceVendorList } = this
      let serviceVendor = serviceVendorList[index]
      serviceVendor.lastPrice = getCalculatedCurrencyValue(
        rateObj,
        serviceVendor.lastPrice
      )
      serviceVendorList.splice(index, 1, serviceVendor)
    },
  },
}
</script>
<style scoped>
.item-unit-price-currency {
  padding: 20px 0px 0px;
  width: 32%;
}
</style>
