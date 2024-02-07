<template>
  <div v-if="loading"><Spinner :show="loading" size="80"></Spinner></div>
  <div v-else>
    <div class="vendor-header">
      <div class="vendor-title">{{ $t('common.header.vendors') }}</div>
      <div>
        <el-button
          class="fc__add__btn p12"
          v-if="showAddVendor"
          @click="addVendors()"
        >
          {{ $t('common.products.add_vendor') }}
        </el-button>
      </div>
    </div>

    <el-tabs v-model="title" class="rfq-vendor-tabs">
      <el-tab-pane
        :label="$t('common.inventory.awarded_vendors')"
        v-if="canShowAwardedVendors"
        :name="tabNames.AWARDED_VENDORS"
      >
        <div v-if="!details.isAwarded" class="pT40">
          <div class="text-center">
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon icon-130"
            ></inline-svg>
          </div>
          <div class="add-record-msg">
            {{ $t('common.inventory.no_lineitems_awarded') }}
          </div>
        </div>
        <RfqVendorTabs :details="details" :vendorQuotes="awardedVendorQuotes">
          <template #additional-actions="{vendorQuote}">
            <div class="additional-actions">
              <div
                class="view-quote-container"
                @click="redirectToVendorQuote(vendorQuote.id)"
              >
                <span class="view-quote">
                  <fc-icon
                    class="view-quote-icon_rfq"
                    group="dsm"
                    name="open-window"
                    size="16"
                  ></fc-icon>
                  {{ $t('common.inventory.view_quote') }}</span
                >
              </div>
              <div v-if="canCreatePo(vendorQuote)">
                <el-button
                  type="button"
                  class="create-po-btn self-center"
                  @click="createPo(vendorQuote.id)"
                >
                  {{ $t('common.header.create_po') }}
                </el-button>
              </div>
              <div
                v-if="canShowPo(vendorQuote)"
                class="view-quote-container mL10"
                @click="redirectToPO(vendorQuote)"
              >
                <span class="view-quote">
                  <fc-icon
                    class="view-quote-icon_rfq"
                    group="dsm"
                    name="open-window"
                    size="16"
                  ></fc-icon>
                  {{ $t('common.inventory.view_po') }}</span
                >
              </div>
            </div>
          </template>
        </RfqVendorTabs>
      </el-tab-pane>
      <el-tab-pane
        :label="$t('common.inventory.all_vendors')"
        :name="tabNames.ALL_VENDORS"
      >
        <RfqVendorTabs :details="details" :vendorQuotes="vendorQuotes">
          <template #status="{vendorQuote}">
            <div class="vendor-quote-status" v-if="vendorQuote.status">
              {{ vendorQuote.status }}
            </div>
          </template>
          <template #additional-actions="{vendorQuote}">
            <div
              class="view-quote-container"
              @click="redirectToVendorQuote(vendorQuote.id)"
            >
              <span class="view-quote">
                <fc-icon
                  class="view-quote-icon_rfq"
                  group="dsm"
                  name="open-window"
                  size="16"
                ></fc-icon
                >{{ $t('common.inventory.view_quote') }}</span
              >
            </div>
          </template>
        </RfqVendorTabs>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import RfqVendorTabs from './RfqVendorTabs'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import { eventBus } from '@/page/widget/utils/eventBus'
import Spinner from '@/Spinner'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
const TAB_NAMES = {
  AWARDED_VENDORS: 'awardedVendors',
  ALL_VENDORS: 'allVendors',
}
export default {
  name: 'RfqVendor',
  props: ['details'],
  data() {
    return {
      title: 'allVendors',
      vendorQuotes: [],
      awardedVendorQuotes: [],
      loading: false,
      tabNames: TAB_NAMES,
    }
  },
  components: {
    RfqVendorTabs,
    Spinner,
  },
  created() {
    this.loadVendorQuotes()
    this.setDefaultTab()
  },
  computed: {
    canShowAwardedVendors() {
      let { details } = this || {}
      return details.isAwarded || false
    },
    isRfqFinalized() {
      let { details } = this
      let { isRfqFinalized } = details || {}
      return isRfqFinalized
    },
    showAddVendor() {
      let { details, isRfqFinalized } = this
      return !isRfqFinalized && !isEmpty(details.vendor)
    },
  },
  methods: {
    setDefaultTab() {
      let { canShowAwardedVendors } = this || {}
      if (canShowAwardedVendors) {
        this.title = TAB_NAMES.AWARDED_VENDORS
      }
    },
    addVendors() {
      eventBus.$emit('showVendorPopUp')
    },
    canCreatePo(vendorQuote) {
      let { isAwarded } = this.details || {}
      let { purchaseOrder } = vendorQuote || {}
      return isEmpty(purchaseOrder) && isAwarded
    },
    canShowPo(vendorQuote) {
      let { purchaseOrder } = vendorQuote || {}
      return !isEmpty(purchaseOrder)
    },
    async loadVendorQuotes() {
      this.loading = true
      let { details } = this
      let { isRfqFinalized } = details || {}
      if (isRfqFinalized) {
        let reqForQuotId = this.$getProperty(details, 'id')
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
          this.vendorQuotes = list
          this.getAwardedVendorQuotes(cloneDeep(list))
        }
      } else {
        if (!isEmpty(details.vendor)) {
          let vendorIds = (details.vendor || []).map(
            vendorObj => `${vendorObj?.id}`
          )

          let params = {
            filters: JSON.stringify({
              id: {
                operatorId: 9,
                value: vendorIds,
              },
            }),
          }
          let { list, error } = await API.fetchAll('vendors', params)
          if (error) {
            this.$message.error(
              error || this.$t('common._common.error_occurred')
            )
          } else {
            this.vendorQuotes = (list || []).map(vendor => {
              return {
                vendor: vendor,
              }
            })
          }
        }
      }
      this.loading = false
    },
    redirectToPO(vendorQuote) {
      let { purchaseOrder } = vendorQuote || {}
      let { id } = purchaseOrder || {}
      let route
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('purchaseorder', pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: { viewname: 'all', id },
          }).href
        }
      } else {
        route = this.$router.resolve({
          name: 'poSummary',
          params: { viewname: 'all', id },
        }).href
      }
      route && window.open(route, '_blank')
    },
    redirectToVendorQuote(id) {
      let route
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('vendorQuotes', pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: { viewname: 'all', id },
          }).href
        }
      } else {
        route = this.$router.resolve({
          name: 'vendorQuotesSummary',
          params: { viewname: 'all', id },
        }).href
      }
      route && window.open(route, '_blank')
    },
    getAwardedVendorQuotes(vendorQuoteRecords) {
      this.awardedVendorQuotes = (vendorQuoteRecords || []).filter(
        vendorQuote => {
          let vendorQuotesLineItems = (
            vendorQuote.vendorQuotesLineItems || []
          ).filter(vendorQuoteLineItem => {
            return vendorQuoteLineItem.isLineItemAwarded
          })
          vendorQuote.vendorQuotesLineItems = vendorQuotesLineItems
          return !isEmpty(vendorQuote.vendorQuotesLineItems)
        }
      )
    },
    createPo(vendorQuote) {
      let { details } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('purchaseorder', pageTypes.CREATE)
        if (name) {
          this.$router.push({
            name,
            query: {
              convertToPo: true,
              vendorQuote: vendorQuote,
              requestForQuotation: details?.id,
            },
          })
        }
      } else {
        this.$router.push({
          name: 'purchaseorder-create',
          params: {
            viewname: 'all',
          },
          query: {
            convertToPo: true,
            vendorQuote: vendorQuote,
            requestForQuotation: details?.id,
          },
        })
      }
    },
  },
}
</script>
<style scoped lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
.vendor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 16px 0px 16px;
}
.vendor-title {
  font-size: 16px;
  font-weight: 500;
  color: #324056;
}
.vendor-quote-status {
  margin: 2px 0 0 8px;
  padding: 3px 12px;
  border-radius: 10px;
  background-color: rgba(34, 176, 150, 0.1);
  color: #22b096;
  font-size: 12px;
  font-weight: 500;
}
.view-quote {
  display: flex;
  color: #3ab2c2;
  font-size: 14px;
  line-height: 1.43;
  padding-left: 2px;
  cursor: pointer;
}
.view-quote {
  .view-quote-icon_rfq {
    fill: #3ab2c2;
    margin-right: 5px;
  }
}
.view-quote-container {
  padding: 10px 24px;
}
.view-quote-container:hover {
  background-color: rgba(58, 178, 194, 0.05);
}
.view-quote-container,
.additional-actions {
  display: flex;
  align-items: center;
}
.create-po-btn {
  padding: 10px 32.5px;
  margin-left: 12px;
  border-radius: 4px;
  border: solid 1px #3ab2c2;
  background-color: #fff;
  cursor: pointer;
  text-transform: capitalize;
  font-size: 14px;
  text-align: center;
  color: #324056;
}
</style>
<style lang="scss">
.rfq-vendor-tabs .el-tabs__nav-wrap {
  padding-left: 16px;
}
.rfq-vendor-tabs .fc-badge {
  color: #fff;
  background-color: #23b096;
  padding: 3px 12px 4px;
  font-weight: bold;
}
</style>
