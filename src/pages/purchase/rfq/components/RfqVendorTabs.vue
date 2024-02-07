<template>
  <div class="height-100 overflow-y-scroll">
    <div v-if="isEmpty(details.vendor)" class="pT40">
      <div class="text-center">
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon icon-130"
        ></inline-svg>
      </div>
      <div class="add-record-msg">
        {{ $t('common.inventory.no_vendors_msg') }}
      </div>
      <div class="center-align-element">
        <el-button class="fc__add__btn" @click="addVendors()">
          {{ $t('common.products.add_vendor') }}
        </el-button>
      </div>
    </div>
    <div v-else class="pB20 vendor-collapse">
      <el-collapse v-model="activeNames" v-if="details.isRfqFinalized">
        <el-collapse-item
          v-for="vendorQuote in vendorQuotesList"
          :key="vendorQuote.id"
          :name="vendorQuote.id"
          class="vendor-card"
        >
          <template slot="title">
            <div class="vendor-quote-container">
              <div class="pL10 pT10 vendor-quote-header">
                <div class="vendor-quote-title">
                  <div class="vendor-name pL8">
                    {{ vendorQuote.vendorName }}
                  </div>
                  <slot name="status" :vendorQuote="vendorQuote"></slot>
                </div>
                <slot
                  name="additional-actions"
                  :vendorQuote="vendorQuote"
                ></slot>
              </div>

              <VendorDetailsCard :vendorQuote="vendorQuote">
              </VendorDetailsCard>
            </div>
          </template>
          <div class="vendor-quotes-lineitems-table">
            <div>
              <el-table :data="getVendorQuotesLineItems(vendorQuote)" border>
                <el-table-column
                  :label="$t('common.header.item_description')"
                  min-width="192"
                  class-name="item-name-vq"
                  fixed="left"
                >
                  <template slot-scope="scope">
                    <div class="flex flex-no-wrap">
                      <fc-icon
                        class="mT5 mR5"
                        group="dsm"
                        name="line-item"
                        size="12"
                      ></fc-icon>
                      <div class="flex flex-direction-column">
                        <span class="fwBold">{{ getItemName(scope.row) }}</span>
                        {{ getItemDescription(scope.row) }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  :formatter="uom"
                  :label="$t('quotation.common.uom')"
                  min-width="76"
                >
                </el-table-column>
                <el-table-column
                  prop="quantity"
                  :label="$t('common._common.quantity')"
                  class-name="right-align-elements"
                  min-width="128"
                >
                </el-table-column>
                <!-- <el-table-column
                prop="unitPrice"
                :formatter="getUnitPrice"
                :label="`${$t('common.header.unit_price')} (${$currency})`"
                class-name="custom-class-price right-align-elements"
                min-width="150"
              >
              </el-table-column> -->
                <el-table-column
                  prop="counterPrice"
                  :formatter="getCounterPrice"
                  :label="
                    `${$t('common.inventory.quoted_unit_price')} (${$currency})`
                  "
                  class-name="custom-class-price right-align-elements"
                  min-width="192"
                >
                </el-table-column>
                <el-table-column
                  :formatter="getTaxPercentage"
                  :label="`${$t('common.inventory.tax_percentage')} (%)`"
                  class-name=" right-align-elements"
                  min-width="192"
                >
                </el-table-column>
                <el-table-column
                  :formatter="getTaxAmount"
                  :label="`${$t('common.inventory.tax_amount')} (${$currency})`"
                  class-name=" right-align-elements"
                  min-width="148"
                >
                </el-table-column>

                <el-table-column
                  prop="remarks"
                  :formatter="getRemarks"
                  :label="$t('common.wo_report._remarks')"
                  min-width="128"
                >
                </el-table-column>
                <el-table-column
                  prop="amount"
                  fixed="right"
                  :formatter="getAmount"
                  :label="
                    `${$t('common.inventory.quoted_amount')} (${$currency})`
                  "
                  class-name="right-align-elements  "
                  min-width="192"
                >
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
      <div
        v-else
        v-for="vendorQuote in vendorQuotesList"
        :key="vendorQuote.id"
        :name="vendorQuote.id"
        class="vendor-quote-container-border vendor-card flex-vendor-container"
      >
        <div class="vendor-container">
          <div class="vendor-quote-title pT10">
            <fc-icon group="webtabs" name="vendor" size="15"></fc-icon>
            <div class="vendor-name">
              {{ vendorQuote.vendorName }}
            </div>
          </div>

          <VendorDetailsCard :vendorQuote="vendorQuote"> </VendorDetailsCard>
        </div>
        <fc-icon
          group="action"
          name="delete"
          size="16"
          class="vendor-delete-icon"
          @click="deleteVendor(vendorQuote)"
        ></fc-icon>
      </div>
    </div>
    <V3LookupFieldWizard
      v-if="canShowWizard"
      :canShowLookupWizard.sync="canShowWizard"
      :selectedLookupField="vendorMultiLookupData"
      @setLookupFieldValue="selectVendors"
    ></V3LookupFieldWizard>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import V3LookupFieldWizard from 'src/newapp/components/V3LookupFieldWizard.vue'
import { eventBus } from '@/page/widget/utils/eventBus'
import RfqMixin from '../mixins/RfqMixin'
import VendorDetailsCard from './VendorDetailsCard'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'

export default {
  name: 'RfqVendorTabs',
  props: ['details', 'vendorQuotes'],
  components: {
    VendorDetailsCard,
    V3LookupFieldWizard,
  },
  mixins: [RfqMixin, LineItemsMixin],
  data() {
    return {
      vendorMultiLookupData: {
        displayName: this.$t('common.header.choose_vendors'),
        name: 'vendors',
        lookupModule: {
          displayName: this.$t('common.header.vendors'),
          name: 'vendors',
        },
        multiple: true,
        forceFetchAlways: true,
        selectedItems: [],
      },
      canShowWizard: false,
      vendorDetails: [],
      activeNames: '',
      isEmpty,
    }
  },
  created() {
    this.loadAllTaxes()
  },
  mounted() {
    eventBus.$on('showVendorPopUp', () => {
      this.addVendors()
    })
  },
  computed: {
    moduleName() {
      return 'requestForQuotation'
    },
    vendorQuotesList() {
      let vendorQuotesArr = (this.vendorQuotes || []).map(vendorQuote => {
        let { vendor, moduleState } = vendorQuote || {}
        let {
          primaryContactName,
          primaryContactEmail,
          primaryContactPhone,
          name,
        } = vendor || {}
        let { displayName } = moduleState || {}
        return {
          ...vendorQuote,
          primaryContactName: primaryContactName || '---',
          emailId: primaryContactEmail || '---',
          contactNumber: primaryContactPhone || '---',
          vendorName: name || '---',
          status: displayName || null,
        }
      })
      return vendorQuotesArr
    },
  },
  methods: {
    loadAllTaxes() {
      let { vendorQuotesList } = this
      let vendorQuoteLineItems = []
      if (!isEmpty(vendorQuotesList)) {
        vendorQuoteLineItems = vendorQuotesList.map(vendorQuote =>
          this.getVendorQuotesLineItems(vendorQuote)
        )
      }
      this.loadTaxes(vendorQuoteLineItems)
    },
    deleteVendor(vendorQuote) {
      let { vendor: vendorToBeDeleted } = vendorQuote || {}
      let filteredVendors = this.vendorQuotes
        .filter(quote => {
          let { vendor } = quote || {}
          let { id } = vendor || {}
          return id !== vendorToBeDeleted?.id
        })
        .map(quote => {
          let { vendor } = quote || {}
          let { id } = vendor || {}
          return { id }
        })
      this.updateVendors(filteredVendors)
    },
    addVendors() {
      this.setSelectedVendors()
      this.canShowWizard = true
    },
    setSelectedVendors() {
      let { details } = this
      let { vendor } = details || {}
      if (!isEmpty(vendor)) {
        let selectedVendors = vendor.map(vendor => ({
          value: vendor?.id,
        }))
        this.$set(this.vendorMultiLookupData, 'selectedItems', selectedVendors)
      }
    },
    selectVendors(selectedVendorValues) {
      let selectedVendors = this.$getProperty(
        selectedVendorValues,
        'field.selectedItems'
      )
      if (isEmpty(selectedVendors)) {
        this.$message.error(
          this.$t('common.inventory.select_vendors_warning_message')
        )
        return
      }
      let vendorsData = selectedVendors.map(vendor => {
        let { value } = vendor || {}
        return {
          id: value,
        }
      })
      this.updateVendors(vendorsData)
    },
    async updateVendors(vendorsData) {
      let { details, moduleName } = this
      let data = {
        id: details.id,
        data: {
          vendor: vendorsData,
        },
      }
      let { error } = await API.updateRecord(moduleName, data)
      if (error) {
        this.$message.error(
          error.message ||
            this.$t('common._common.error_occured_while_selecting_vendors')
        )
      } else {
        let successMsg = this.$t('common._common.selected_vendors_successfully')
        this.$message.success(successMsg)
        this.canShowWizard = false
        eventBus.$emit('refresh-overview')
      }
    },
  },
}
</script>
<style scoped lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
.center-align-element {
  display: flex;
  justify-content: center;
  align-items: center;
}
.vendor-quote-container {
  width: 95%;
  padding-top: 10px;
  margin-left: 18px;
}
.vendor-container {
  width: 85%;
  padding-top: 10px;
  margin-left: 18px;
}
.flex-vendor-container {
  display: flex;
  align-items: center;
}
.vendor-delete-icon {
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  &:hover {
    background-color: #f1f2f4;
  }
}
.vendor-card {
  position: relative;
}
.vendor-card:hover {
  margin-bottom: 4px;
  box-shadow: 0 4px 8px -2px rgba(29, 56, 78, 0.11),
    0 0 1px 0 rgba(29, 56, 78, 0.31);

  border-bottom-color: transparent !important;
}

.vendor-name {
  font-size: 16px;
  font-weight: 500;
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-left: 4px;
}
.vendor-quote-title {
  display: flex;
  align-items: center;
}
.vendor-quote-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.vendor-quote-container-border {
  border-bottom: 1px solid #ebeef5;
}
</style>
<style lang="scss">
.vendor-collapse {
  .el-collapse-item__header {
    height: auto !important;
    line-height: normal;
    font-weight: normal;
  }
  .el-collapse-item__arrow {
    position: absolute;
    left: 0;
    font-weight: 900;
    margin-bottom: 80px;
    margin-left: 8px;
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
}
</style>
