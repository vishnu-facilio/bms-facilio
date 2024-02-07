<template>
  <div class="d-flex flex-wrap">
    <el-form-item
      :label="$t('setup.setup_profile.street')"
      :prop="`${(field || {}).name}.street`"
      label-width="0px"
      :required="field.required"
      class="section-items form-two-column"
    >
      <el-input
        :placeholder="$t('setup.setup_profile.enter_street')"
        v-model="(model || {}).street"
        class="fc-input-full-border2"
      ></el-input>
    </el-form-item>
    <el-form-item
      :label="$t('setup.setup_profile.city_district')"
      :prop="`${(field || {}).name}.city`"
      label-width="0px"
      :required="field.required"
      class="section-items form-two-column"
    >
      <el-input
        :placeholder="$t('setup.setup_profile.entre_city_district')"
        v-model="(model || {}).city"
        class="fc-input-full-border2"
      ></el-input>
    </el-form-item>
    <el-form-item
      :label="$t('setup.setup_profile.state_province_county')"
      :prop="`${(field || {}).name}.state`"
      label-width="0px"
      :required="field.required"
      class="section-items form-two-column"
    >
      <el-input
        :placeholder="$t('setup.setup_profile.enter_state_province_county')"
        v-model="(model || {}).state"
        class="fc-input-full-border2"
      ></el-input>
    </el-form-item>
    <el-form-item
      :label="zipcodeText"
      :prop="`${(field || {}).name}.zip`"
      label-width="0px"
      :required="field.required"
      class="section-items form-two-column"
    >
      <el-input
        :placeholder="`${$t('maintenance._workorder.enter')} ${zipcodeText}`"
        v-model="(model || {}).zip"
        class="fc-input-full-border2"
      ></el-input>
    </el-form-item>
    <el-form-item
      :label="$t('setup.setup_profile.country')"
      :prop="`${(field || {}).name}.country`"
      label-width="0px"
      :required="field.required"
      class="section-items form-two-column"
    >
      <el-select
        filterable
        clearable
        v-model="(model || {}).country"
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
  </div>
</template>
<script>
import countries from 'util/data/countries'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { getStoreRoomById } from 'pages/Inventory/InventoryUtil'

export default {
  props: ['model', 'field', 'storeRoomId', 'vendorId'],
  data() {
    return {
      countryList: countries,
    }
  },
  watch: {
    storeRoomId(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.onStoreRoomChange()
      }
    },
    vendorId(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.onVendorChange()
      }
    },
  },
  computed: {
    zipcodeText() {
      let { user, timezone } = this.$account || {}
      if (user.language === 'en' && timezone === 'Europe/London') {
        return 'Postcode'
      } else {
        return this.$t('setup.setup_profile.zipcode')
      }
    },
  },
  methods: {
    // Special handling for prefilling store address in billing address
    async onStoreRoomChange() {
      let { storeRoomId, field } = this
      let { name } = field
      if (!isEmpty(storeRoomId) && name === 'shipToAddress') {
        let { location } = await getStoreRoomById(storeRoomId)

        if (!isEmpty(location)) {
          let addressProp = ['city', 'street', 'state', 'country', 'zip']
          addressProp.forEach(prop => {
            this.$set(this.model, `${prop}`, location[prop])
          })
        }
      }
    },
    async onVendorChange() {
      let { vendorId, field } = this
      let { name } = field
      if (!isEmpty(vendorId) && name === 'billToAddress') {
        let { vendors, error } = await API.fetchRecord('vendors', {
          id: vendorId,
        })
        if (!isEmpty(error)) {
          let { message = 'Error Occured while fetching Vendor' } = error
          this.$message.error(message)
        } else {
          let { address } = vendors
          if (!isEmpty(address)) {
            let addressProp = ['city', 'street', 'state', 'country', 'zip']
            addressProp.forEach(prop => {
              this.$set(this.model, `${prop}`, address[prop])
            })
          }
        }
      }
    },
  },
}
</script>
<style lang="scss"></style>
