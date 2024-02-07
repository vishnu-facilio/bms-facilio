<template>
  <div class="fc-po-pr-sumary-con">
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div
      class="fc-quotation-con mT0 overflow-hidden fc-summary-preview-con"
      ref="preview-container"
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
          <div class="text-right fc-black3-16 fwBold line-height30 f25">
            PURCHASE REQUEST
          </div>
          <div class="label-txt-black text-right">
            PR #{{ details.localId }}
          </div>
          <div class="mT10 text-right label-txt-black">
            PR DATE: {{ details.requestedTime | formatDate(true) }}
          </div>
          <div
            v-if="!$validation.isEmpty(details.workorder)"
            class="mT10 text-right label-txt-black"
          >
            WORKORDER ID:
            <span
              class="fc-black-12 text-left"
              :class="[isSummary && 'bluetxt pointer']"
              @click="routeToWOSummary(details.workorder.id)"
              >{{ `#${details.workorder.serialNumber}` }}</span
            >
          </div>
        </div>
      </div>
      <!-- bill box -->
      <div class="fc-quotation-bill-con clearboth">
        <!-- bill to -->
        <div class="p10 pB20 padding-remove-bottom-pdf">
          <div class="d-flex justify-content-space pT10">
            <div v-if="canShowBillToShowAddress" class="width50">
              <span
                class="fc-black-12 text-left fc-bill-to-txt position-relative"
              >
                TO
              </span>
              <div
                class="fc-black-15 pT15 fwBold line-height25 break-word pR10 pointer"
                v-if="
                  !$validation.isEmpty($getProperty(details, 'vendor.name', -1))
                "
              >
                {{ details.vendor.name }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'billToAddress.street')"
              >
                {{
                  `${$getProperty(details, 'billToAddress.street', '')}${
                    $getProperty(details, 'billToAddress.city') ? `,` : ``
                  }`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'billToAddress.city')"
              >
                {{
                  `${$getProperty(details, 'billToAddress.city', '')}${
                    $getProperty(details, 'billToAddress.state') ? `,` : ``
                  }`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'billToAddress.state')"
              >
                {{
                  `${$getProperty(details, 'billToAddress.state', '')}${
                    $getProperty(details, 'billToAddress.country') ? `,` : ``
                  }`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'billToAddress.country')"
              >
                {{
                  `${$helpers.getDiplayNameforCountryISOCode(
                    $getProperty(details, 'billToAddress.country')
                  )}${$getProperty(details, 'billToAddress.zip') ? `,` : ``}`
                }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
                v-if="$getProperty(details, 'billToAddress.zip')"
              >
                {{ `${$getProperty(details, 'billToAddress.zip', '')}` }}
              </div>
            </div>
            <div
              v-if="canShowShipToShowAddress"
              :class="[canShowBillToShowAddress && 'pL20 border-left5']"
              class="width50"
            >
              <span
                class="fc-black-12 text-left fc-bill-to-txt position-relative"
              >
                SHIP TO
              </span>
              <div
                v-if="$getProperty(details, 'storeRoom.name')"
                class="fc-black-15 fwBold pT15 line-height25 break-word pR10 pointer"
              >
                {{ details.storeRoom.name }}
              </div>
              <div
                class="fc-black-13 text-left line-height25"
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
                    $getProperty(details, 'shipToAddress.country')
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
        </div>
      </div>
      <!-- subject -->
      <div>
        <div class="fc-black-13 text-left pT20">
          <span class="fwBold pR10">Subject</span>
          {{ details.name }}
        </div>
        <div
          class="pT10 line-height20 fc-black-13 text-left"
          v-if="$getProperty(details, 'description')"
        >
          {{ details.description }}
        </div>
      </div>
      <!-- table -->
      <LineItemsTable
        :details="details"
        :uomEnumMap="uomEnumMap"
      ></LineItemsTable>
      <!-- notes -->
      <div
        class="pT20 widthreduce-pdf"
        style="width: 51.4%;"
        v-if="!$validation.isEmpty(details.notes)"
      >
        <div class="fc-black-13 fwBold text-left">
          NOTES
        </div>
        <div class="fc-black-13 pT10 text-left line-height20 pR20">
          {{ details.notes }}
        </div>
      </div>
      <!-- terms and condition -->
      <div
        class="pT20 border-bottom17 pB20 fc-page-break-avoid"
        v-if="!$validation.isEmpty(details.termsAssociated)"
      >
        <div class="fc-black-13 fwBold text-left">
          TERMS AND CONDITIONS
        </div>
        <div
          class="fc-black-13 text-left pT20 line-height20 break-word"
          v-for="termsData in details.termsAssociated"
          :key="termsData.id"
        >
          <div v-html="sanitize(termsData.terms.longDesc)"></div>
        </div>
      </div>
      <!-- signature -->
      <div class="pT20 fc-show-only-pdf">
        <div>Yours truly,</div>
        <div class="pT10 pB10 width100px height50"></div>
        <div class="fc-black-13 text-left pT10">
          Authorized Signatory
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import PurchaseRequestMixin from 'src/pages/purchase/pr/mixin/prMixin'
import LineItemsTable from 'src/pages/purchase/pr/PrLineItemsTable'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  mixins: [PurchaseRequestMixin],
  components: {
    LineItemsTable,
  },
  created() {
    this.sanitize = sanitize
  },
}
</script>
