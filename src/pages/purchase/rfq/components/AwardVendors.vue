<template>
  <el-dialog
    :visible="true"
    :before-close="onClose"
    :title="$t('common.header.vendor_comparision')"
    width="90%"
    custom-class="vendorComparisonDialog fc-web-form-dialog"
    :append-to-body="true"
  >
    <div class="fixed-header-vendor">
      <div v-if="lineItemsCheckedCount" class="height30 fwBold awarded-Items">
        <div class="text-center pT5">
          {{ awardedInTotal }}
        </div>
      </div>
      <div class="search-vendors flex justify-content-space">
        <div v-if="vendorViewVisibility">
          <el-input
            v-model="searchVendor"
            class="fc-input-full-border2 mT10"
            type="text"
            :placeholder="$t('common._common.search_vendor')"
            @change="filterVendorQuotes"
          >
          </el-input>
        </div>
        <div v-if="lineItemViewVisibility">
          <el-input
            v-model="searchLineItem"
            class="fc-input-full-border2 mT10"
            type="text"
            :placeholder="$t('common._common.search_lineitem')"
            @change="filterRfqLineItem"
          ></el-input>
        </div>
        <div class="flex">
          <fc-icon
            class="fc-icon-award-vendors"
            group="default"
            size="12"
            name="eye-open"
          ></fc-icon>
          <div class="mT20 mL5 mR5">
            {{ $t('common._common.view_as') }}
          </div>
          <div class="dialog-button-wrap">
            <div class="button-wrap">
              <el-button
                :class="{
                  active: !vendorViewVisibility,
                }"
                size="mini"
                @click="openVendorViewDialog"
              >
                <div class="flex">
                  <fc-icon size="12" group="webtabs" name="vendor"></fc-icon>
                  <div class="mL5">
                    {{ $t('common._common.vendor') }}
                  </div>
                </div>
              </el-button>
              <el-button
                size="mini"
                :class="{
                  active: !lineItemViewVisibility,
                }"
                @click="openLineItemViewDialog"
              >
                <fc-icon
                  class="lineitem-icon-rfq"
                  group="dsm"
                  name="line-item"
                  size="10"
                ></fc-icon>
                {{ $t('common._common.line_item') }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <template v-if="vendorViewVisibility">
      <div v-if="$validation.isEmpty(filteredVendorQuotes)">
        <div
          class="flex-middle justify-content-center wo-flex-col flex-direction-column"
          style="margin-top:10%"
        >
          <inline-svg
            :src="`svgs/emptystate/readings-empty`"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="pT10 fc-black-dark f18 bold">
            {{ $t('common._common.no_matching_data') }}
          </div>
        </div>
      </div>
      <div
        v-else
        v-for="vendorQuote in filteredVendorQuotes"
        :key="vendorQuote.id"
      >
        <RfqComparisonVendorView
          :isAwarded="isAwarded"
          :vendorQuote="vendorQuote"
          :vendorQuotes="vendorQuotes"
          :lineItemTotalCount="lineItemTotalCount"
          :lineItemsCheckedCount="lineItemsCheckedCount"
          :lineItemsChecked="lineItemsChecked"
          :taxDetails="allTaxes"
          @setLineItemsChecked="setLineItemsChecked"
          @setlineItemsCheckedCount="setlineItemsCheckedCount"
        >
        </RfqComparisonVendorView>
      </div>
    </template>
    <template v-if="lineItemViewVisibility">
      <div v-if="$validation.isEmpty(filteredRfqLineItems)">
        <div
          class="flex-middle justify-content-center wo-flex-col flex-direction-column"
          style="margin-top:10%"
        >
          <inline-svg
            :src="`svgs/emptystate/readings-empty`"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="pT10 fc-black-dark f18 bold">
            {{ $t('common._common.no_matching_data') }}
          </div>
        </div>
      </div>
      <div
        v-else
        v-for="(rfqLineItem, index) in filteredRfqLineItems"
        :key="rfqLineItem.id"
      >
        <RfqComparisonLineItemView
          :isAwarded="isAwarded"
          :rfqLineItem="rfqLineItem"
          :filteredRfqLineItems="filteredRfqLineItems"
          :index="index"
          :taxDetails="allTaxes"
          :vendorQuotesLineItems="vendorQuotesLineItems"
          :lineItemsChecked="lineItemsChecked"
          @setLineItemsChecked="setLineItemsChecked"
          @setlineItemsCheckedCount="setlineItemsCheckedCount"
        >
        </RfqComparisonLineItemView>
      </div>
    </template>
    <div v-if="!isAwarded" class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        class="modal-btn-save"
        :loading="isButtonLoading"
        :class="{
          active: awardButtonClass,
        }"
        type="primary"
        @click="saveRecord"
        >{{ $t('common.header.award') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import RfqComparisonVendorView from './RfqComparisonVendorView'
import RfqComparisonLineItemView from './RfqComparisonLineItemView'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'
import RfqMixin from '../mixins/RfqMixin'
export default {
  props: ['onClose', 'moduleName', 'record'],
  mixins: [RfqMixin, LineItemsMixin],
  components: {
    RfqComparisonVendorView,
    RfqComparisonLineItemView,
  },
  data() {
    return {
      requestForQuotationLineItems: null,
      vendorViewVisibility: true,
      vendorQuotes: null,
      lineItemTotalCount: '',
      lineItemsChecked: null,
      lineItemsCheckedCount: '',
      lineItemViewVisibility: false,
      vendorQuotesLineItems: null,
      searchVendor: '',
      searchLineItem: '',
      filteredVendorQuotes: null,
      filteredRfqLineItems: null,
      isButtonLoading: false,
    }
  },
  async created() {
    await this.loadVendorQuotes()
    this.loadTaxes(this.vendorQuotes)
  },
  computed: {
    isAwarded() {
      let { record } = this || {}
      let { isAwarded } = record || {}
      return isAwarded
    },
    awardButtonClass() {
      let { record, lineItemTotalCount, lineItemsCheckedCount } = this || {}
      let { isAwarded } = record || {}
      let isAllChecked = lineItemTotalCount === lineItemsCheckedCount
      return !isAllChecked || isAwarded
    },
    awardedInTotal() {
      let { lineItemsCheckedCount, lineItemTotalCount, lineItemsChecked } =
        this || {}
      let totalAmountOfLineItemsChecked = this.$d3.sum(
        lineItemsChecked.map(lineItem => {
          return lineItem.amount || 0
        })
      )
      return `Awarded ${lineItemsCheckedCount} of ${lineItemTotalCount} items in total for ${this.$currency} ${totalAmountOfLineItemsChecked}`
    },
    checkAwardedToOneVendor() {
      let { lineItemsChecked, lineItemTotalCount, lineItemsCheckedCount } =
        this || {}
      if (!isEmpty(lineItemsChecked)) {
        let isAllChecked = lineItemTotalCount === lineItemsCheckedCount
        let pickOneVendor = lineItemsChecked[0].vendor || {}
        let pickOneVendorName = pickOneVendor.name || {}
        let isAwardedToOneVendor = lineItemsChecked.every(element => {
          let { vendor } = element || {}
          let { name } = vendor || {}
          return name === pickOneVendorName
        })
        if (isAwardedToOneVendor && isAllChecked) {
          return true
        }
      }
      return false
    },
  },
  methods: {
    filterRfqLineItem() {
      let { searchLineItem, requestForQuotationLineItems } = this || {}
      let searchKey = searchLineItem.toLowerCase() || {}
      let filteredRfqLineItems = requestForQuotationLineItems.filter(
        rfqLineItem => {
          let itemName = this.getItemName(rfqLineItem).toLowerCase()
          return itemName.includes(searchKey)
        }
      )
      this.filteredRfqLineItems = filteredRfqLineItems
      if (isEmpty(searchKey)) {
        this.filteredRfqLineItems = requestForQuotationLineItems
      }
    },
    filterVendorQuotes() {
      let { searchVendor, vendorQuotes } = this || {}
      let searchKey = searchVendor.toLowerCase() || {}
      let filteredVendorQuotes = vendorQuotes.filter(vendorQuote => {
        let vendorName = this.$getProperty(
          vendorQuote,
          'vendor.name'
        ).toLowerCase()
        return vendorName.includes(searchKey)
      })
      this.filteredVendorQuotes = filteredVendorQuotes
      if (isEmpty(searchKey)) {
        this.filteredVendorQuotes = vendorQuotes
      }
    },
    setlineItemsCheckedCount(count) {
      this.lineItemsCheckedCount = count
    },
    setLineItemsChecked(val) {
      this.lineItemsChecked = val
    },
    openVendorViewDialog() {
      this.vendorViewVisibility = true
      this.lineItemViewVisibility = false
    },
    openLineItemViewDialog() {
      this.lineItemViewVisibility = true
      this.vendorViewVisibility = false
    },
    async loadVendorQuotes() {
      let vendorQuotesLineItemsList = []
      let reqForQuotId = this.$getProperty(this, 'record.id')
      let params = {
        filters: JSON.stringify({
          requestForQuotation: {
            operatorId: 36,
            value: [`${reqForQuotId}`],
          },
        }),
        getWithLineItems: true,
      }
      let { list, error } = await API.fetchAll('vendorQuotes', params)
      if (error) {
        this.$message.error(
          error || this.$t('common._common.error_loading_vendor_quotes')
        )
      } else {
        list = list.filter(vendorQuote => {
          let { isFinalized, negotiation } = vendorQuote || {}
          return isFinalized && !negotiation
        })
        list.forEach(vendorQuote => {
          let { vendorQuotesLineItems, vendor } = vendorQuote || {}
          vendorQuotesLineItems.forEach(vendorQuotesLineItem => {
            vendorQuotesLineItem.checked = false
            vendorQuotesLineItem.vendor = vendor
            vendorQuotesLineItem.amount = 0
            vendorQuotesLineItemsList.push(vendorQuotesLineItem)
          })
          this.lineItemTotalCount = this.getLength(vendorQuotesLineItems)
        })
        this.vendorQuotesLineItems = vendorQuotesLineItemsList
        this.vendorQuotes = list || []
        this.filteredVendorQuotes = list || []
      }
      let rfqLineItems = this.getRfqLineItems()
      this.requestForQuotationLineItems = rfqLineItems
      this.auotSetCheckedLineItemsIfAwarded()
    },
    getRfqLineItems() {
      let { requestForQuotationLineItems } = this.record || {}
      this.filteredRfqLineItems = requestForQuotationLineItems
      return requestForQuotationLineItems
    },
    auotSetCheckedLineItemsIfAwarded() {
      let { isAwarded } = this.record || {}
      let lineItemsChecked = []
      if (isAwarded) {
        let { requestForQuotationLineItems, vendorQuotesLineItems } = this || {}
        requestForQuotationLineItems.forEach(element => {
          let { awardedTo, id } = element || {}
          let rfqVendorName = awardedTo.name || {}
          let checkedLineItems = vendorQuotesLineItems
            .filter(element => {
              let { requestForQuotationLineItem, vendor } = element || {}
              let rfqId = requestForQuotationLineItem.id || {}
              let vendQuotVendorName = vendor.name || {}
              return rfqId === id && vendQuotVendorName === rfqVendorName
            })
            .map(element => {
              element.checked = true
              return element
            })
          if (!isEmpty(checkedLineItems))
            lineItemsChecked.push(checkedLineItems)
        })
        this.lineItemsChecked = lineItemsChecked
        this.lineItemsCheckedCount = this.getLength(lineItemsChecked)
      }
    },
    getLength(val) {
      let lengthProp = Object.keys(val)
      let { length } = lengthProp
      return length
    },
    closeDialog() {
      this.onClose()
    },
    async saveRecord() {
      this.isButtonLoading = true
      let { lineItemsChecked, checkAwardedToOneVendor } = this || {}
      let message = checkAwardedToOneVendor
        ? this.$t('common._common.award_all_to_one_vendor_confirmation')
        : this.$t('common._common.award_vendor_comparison_confirmation')
      if (!isEmpty(lineItemsChecked)) {
        let value = await this.$dialog.confirm({
          title: this.$t('common.inventory.confirm_vendor_selection'),
          message: message,
          lbLabel: this.$t('common._common.cancel'),
          rbDanger: true,
          rbLabel: this.$t('common._common.confirm'),
        })
        if (value) {
          let rfqLineItems = lineItemsChecked.map(lineItem => {
            let { requestForQuotationLineItem } = lineItem || {}
            let {
              counterPrice: awardedPrice,
              vendor: awardedTo,
              quantity,
              tax,
            } = lineItem || {}
            return {
              ...requestForQuotationLineItem,
              awardedPrice,
              awardedTo,
              quantity,
              tax,
            }
          })
          let data = {
            id: this.record.id,
            data: {
              requestForQuotationLineItems: rfqLineItems,
            },
            params: {
              awardQuotes: true,
            },
          }
          let { error } = await API.updateRecord(this.moduleName, data)
          if (error) {
            this.$message.error(
              error.message || this.$t('common._common.error_awarding_vendors')
            )
          } else {
            let successMsg = this.$t('common._common.awarded_vendors_success')
            this.$message.success(successMsg)
            this.onClose()
            this.$emit('saved')
          }
        }
      }
      this.isButtonLoading = false
    },
  },
}
</script>
<style lang="scss" scoped>
.dialog-button-wrap {
  height: fit-content;
  background-color: #f5f5f5;
  margin-top: 10px;
  margin-bottom: 10px;
  border-radius: 3px;
}
.button-wrap {
  margin: 5px 5px 5px 5px;
}
.modal-btn-save.active {
  background-color: #d6d6d6;
  pointer-events: none;
}
.awarded-Items {
  background-color: #3ab2c2;
  color: #ffffff;
}
.fixed-header-vendor {
  background: white;
  position: sticky;
  top: 0;
  z-index: 100;
}
.search-vendors {
  box-shadow: 0px 2px #f5f5f5;
  border-top: 2px solid #f7f7f7;
  height: 60px;
  padding-left: 10px;
  padding-right: 10px;
}
</style>
<style lang="scss">
.vendorComparisonDialog {
  margin-top: 50px !important;
  height: 88%;
  margin: 0 auto 0px;

  .el-dialog__body {
    max-height: fit-content;
    height: inherit;
    overflow-y: scroll;
    padding: 0px 0px 20px 0px;
  }

  .el-table th.is-leaf {
    background-color: #f0f8ff;
    padding-left: 5px;
  }
  .el-table--enable-row-transition .el-table__body td.el-table__cell {
    padding-left: 10px;
  }
  .el-table .disabled-row {
    background: #f5f5f5;
    opacity: 0.6;
    pointer-events: none;
  }
}
.search-vendors {
  .fc-icon-award-vendors {
    margin-top: 23px;
  }
}
.button-wrap {
  .el-button:focus,
  .el-button:hover {
    color: #324056;
    border-color: #c6e2ff;
    background-color: #fefefe;
    border: solid 1px #e0e0e0;
  }
  .el-button.active {
    background: #f5f5f5;
    border: none;
  }
  .lineitem-icon-rfq {
    margin-right: 2px;
    margin-top: 0px;
  }
}
</style>
