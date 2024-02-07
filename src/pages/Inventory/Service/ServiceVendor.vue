<template>
  <!-- Service Vendors -->
  <div>
    <div class="fc-vendor-add-btn" v-if="$hasPermission('contract:UPDATE')">
      <el-button
        @click="showVendorDialog = true"
        icon="el-icon-plus"
        class="el-button f12 fc-sites-btn el-button--text sh-button sh-button-add button-add sp-sh-btn-sm"
        type="text"
        :title="$t('common._common.add_vendors')"
        v-tippy
        data-size="small"
      ></el-button>
    </div>
    <el-tabs v-model="activeName1" class="fc-tab-hide">
      <el-tab-pane label="Vendors" name="first">
        <div class="fc-agents-list-table fc-service-vendor-table">
          <el-table
            height="150px"
            :data="serviceVendors"
            empty-text="No Vendors available."
            :default-sort="{
              prop: 'serviceVendors.vendor.name',
              order: 'descending',
            }"
            class="width100 inventory-inner-table mT20 "
          >
            <el-table-column prop="vendor.name" label="NAME"></el-table-column>
            <el-table-column label="Price">
              <template v-slot="scope">
                <div v-if="$hasPermission('inventory:UPDATE')">
                  <div v-if="details.currencyCode" class="d-flex align-center">
                    <span>{{
                      getCurrencyForCurrencyCode(details.currencyCode)
                    }}</span>
                    <el-input
                      placeholder
                      :min="0"
                      v-model="scope.row.lastPrice"
                      type="number"
                      @change="
                        patchUpdateRecord(
                          $t('common.products.price_edited_successfully')
                        )
                      "
                      class="fc-input-full-border2 width50px inventory-input-width text-right pr-summary"
                    >
                    </el-input>
                  </div>
                  <div v-else>
                    <span v-if="$currency === '$'">{{ $currency }}</span>
                    <el-input
                      placeholder
                      :min="0"
                      v-model="scope.row.lastPrice"
                      type="number"
                      @change="
                        patchUpdateRecord(
                          $t('common.products.price_edited_successfully')
                        )
                      "
                      class="fc-input-full-border2 width50px inventory-input-width text-right pr-summary"
                    >
                    </el-input>
                    <span v-if="$currency !== '$'">{{ $currency }}</span>
                  </div>
                </div>
                <currency v-else :value="scope.row.lastPrice"></currency>
              </template>
            </el-table-column>
            <el-table-column>
              <template v-slot="scope">
                <div class="visibility-hide-actions export-dropdown-menu">
                  <i
                    class="el-icon-delete pointer edit-icon-color"
                    v-if="$hasPermission('inventory:UPDATE')"
                    style="padding-left: 18px"
                    data-arrow="true"
                    :title="$t('common.header.delete_vendor')"
                    v-tippy
                    @click="remove(scope.row.id)"
                  ></i>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
    <VendorForm
      v-if="showVendorDialog"
      :serviceVendors="serviceVendors"
      :id="details.id"
      :moduleName="moduleName"
      @onSave="saveVendorList"
      @onClose="showVendorDialog = false"
    >
    </VendorForm>
  </div>
</template>
<script>
import VendorForm from './VendorForm.vue'
import { API } from '@facilio/api'
import { getCurrencyForCurrencyCode } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['details', 'moduleName'],
  components: { VendorForm },
  data() {
    return {
      activeName1: 'first',
      showVendorDialog: false,
      serviceVendors: [],
      getCurrencyForCurrencyCode,
    }
  },
  created() {
    this.serviceVendors = this.details.serviceVendors || []
  },
  methods: {
    async patchUpdateRecord(message) {
      let { id } = this.details || {}
      let param = {
        id,
        data: {
          serviceVendors: this.serviceVendors,
        },
      }

      let { error } = await API.updateRecord(this.moduleName, param)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(message)
      }
    },

    remove(vendorId) {
      if (this.serviceVendors.length === 1) {
        this.$message.error(this.$t('common._common.atleast_vendor_required'))
        return
      }
      let index = this.serviceVendors.findIndex(
        vendor => vendor.id === vendorId
      )

      this.serviceVendors.splice(index, 1)
      this.patchUpdateRecord(
        this.$t('common._common.vendor_removed_successfully')
      )
    },
    saveVendorList(serviceVendor) {
      this.serviceVendors = serviceVendor
    },
  },
}
</script>
<style lang="scss">
.fc-vendor-add-btn {
  position: absolute;
  z-index: 100;
  right: 20px;
  top: 5px;
}
.fc-service-vendor-table {
  .el-table {
    td {
      padding-left: 30px !important;
      padding-right: 30px !important;
    }
    th {
      padding-left: 20px !important;
      padding-right: 20px !important;
    }
  }
}
</style>
