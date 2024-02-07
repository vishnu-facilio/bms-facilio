<template>
  <div>
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div
      ref="preview-container"
      class="fc-quotation-con fc-summary-preview-con mT0 overflow-hidden"
      v-else
    >
      <div v-if="$org.logoUrl" class="fc-quotation-logo">
        <img :src="$prependBaseUrl($org.logoUrl)" style="width: 100px;" />
      </div>
      <div class="label-txt-black text-uppercase fwBold pT10">
        {{ $account.org.name }}
      </div>
      <div class="flex-middle justify-content-space width100">
        <div>
          <div class="label-txt-black line-height20">
            <span v-if="$account.org.street">
              {{ `${$account.org.street}${$account.org.city ? `,` : ``}` }}
            </span>
            <span v-if="$account.org.city">
              {{ `${$account.org.city}${$account.org.state ? `,` : ``}` }}
            </span>
          </div>
          <div class="label-txt-black line-height20">
            <span v-if="$account.org.state">
              {{ `${$account.org.state}${$account.org.country ? `,` : ``}` }}
            </span>
            <span v-if="$account.org.country">
              {{
                `${$helpers.getDiplayNameforCountryISOCode(
                  $account.org.country
                )}${$account.org.zip ? `,` : ``}`
              }}
            </span>
          </div>
          <div v-if="$account.org.zip" class="label-txt-black line-height20">
            {{ `${$account.org.zip}` }}
          </div>
        </div>
        <div class="">
          <div class="text-right fc-black3-16 fwBold line-height30 ">
            {{ $t('common.products._rfq') }}
          </div>
          <div class="label-txt-black text-right fwBold">
            {{ `${$t('common.products.rfq_#')} ${details.id}` }}
          </div>
          <div
            class="mT10 text-right label-txt-black text-uppercase  d-flex  justify-content-space items-baseline"
          >
            <div class="fwBold mR10">
              {{ $t('common.products.requested_date') }}
            </div>
            <div>
              {{ details.requestedDate | formatDate(true) }}
            </div>
          </div>
          <div
            class="mT10 text-right label-txt-black  d-flex  justify-content-space items-baseline"
          >
            <div class="fwBold text-uppercase mR10">
              {{ $t('common.inventory.rfq_type') }}
            </div>
            <div>
              {{ getRfqType }}
            </div>
          </div>
        </div>
      </div>

      <div class="fc-quotation-bill-con clearboth">
        <div class="pB10 padding-remove-bottom-pdf">
          <div
            class="rfq-border"
            v-if="
              isAddressFieldNotEmpty($getProperty(details, 'shipToAddress'))
            "
          >
            <div class="p10">
              <span
                class="fc-black-12 text-left fc-bill-to-txt position-relative"
              >
                {{ $t('common.header.shipping_address') }}
              </span>
              <div
                class="fc-black-13 text-left line-height25 pT15"
                v-if="$getProperty(details, 'shipToAddress.street')"
              >
                {{
                  `${$getProperty(details, 'shipToAddress.street', '')}${
                    $getProperty(details, 'shipToAddress.city') ? `,` : ``
                  }`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'shipToAddress.city')"
              >
                {{
                  `${$getProperty(details, 'shipToAddress.city', '')}${
                    $getProperty(details, 'shipToAddress.state') ? `,` : ``
                  }`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'shipToAddress.state')"
              >
                {{
                  `${$getProperty(details, 'shipToAddress.state', '')}${
                    $getProperty(details, 'shipToAddress.country') ? `,` : ``
                  }`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'shipToAddress.country')"
              >
                {{
                  `${$helpers.getDiplayNameforCountryISOCode(
                    $getProperty(details, 'shipToAddress.country', '')
                  )}${$getProperty(details, 'shipToAddress.zip') ? `,` : ``}`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'shipToAddress.zip')"
              >
                {{ `${$getProperty(details, 'shipToAddress.zip', '')}` }}
              </div>
            </div>
          </div>
          <div class="rfq-border">
            <div class="p10 line-height20">
              <span
                class="fc-black-12 text-left fwBold"
                style="padding-right:85px"
              >
                {{ $t('common.header.ship_to') }}</span
              >
              {{ $getProperty(details, 'storeRoom.name', '---') }}
            </div>
          </div>
          <div class="rfq-border">
            <div class="p10 line-height20">
              <span
                class="fc-black-12 text-left text-uppercase fwBold"
                style="padding-right:78px"
              >
                {{ $t('common.products.subject') }}</span
              >
              {{ details.name }}
            </div>
          </div>
          <div class="p10 line-height20">
            <span class="fc-black-12 text-left fwBold pR50">
              {{ $t('common._common.description') }}</span
            >
            {{ $getProperty(details, 'description', '---') }}
          </div>
        </div>
      </div>
      <!-- table -->
      <RfqLineItemsTable
        :details="details"
        :uomEnumMap="uomEnumMap"
        :pdfSource="pdfSource"
      ></RfqLineItemsTable>
      <!-- signature -->
      <div class="pT20 fc-show-only-pdf">
        <div>{{ $t('common.products.yours_truly') }}</div>
        <div class="pT10 pB10 width100px height50"></div>
        <div class="fc-black-13 text-left pT10">
          {{ $t('common.header.authorized_signatory') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import PurchaseOrderMixin from 'src/pages/purchase/po/mixin/poMixin'
import RfqLineItemsTable from 'src/pages/purchase/rfq/components/RfqLineItemsTable'
import { sanitize } from '@facilio/utils/sanitize'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['pdfSource'],
  mixins: [PurchaseOrderMixin],
  components: {
    RfqLineItemsTable,
  },
  created() {
    this.sanitize = sanitize
  },
  computed: {
    getRfqType() {
      let rfqType = this.$getProperty(this.details, 'rfqType')
      if (!isEmpty(rfqType)) {
        if (rfqType == 'CLOSED_BID') {
          return this.$t('common.inventory.closed_bid')
        }
      }
      return this.$t('common.inventory.open_bid')
    },
  },
}
</script>
<style scoped>
.justify-content-right {
  justify-content: right;
}
.rfq-border {
  border-bottom: solid 1px #e4eaed;
}
</style>
