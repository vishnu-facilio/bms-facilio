<template>
  <div>
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div
      v-else
      class="fc-quotation-con mT0 overflow-hidden"
      ref="preview-container"
    >
      <div v-if="$org.logoUrl" class="fc-quotation-logo">
        <img :src="$org.logoUrl" style="width: 100px;" />
      </div>
      <div class="label-txt-black text-uppercase fwBold pT10">
        {{ $account.org.name }}
      </div>
      <div class="flex-middle justify-content-space width100">
        <div class="pT5">
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
              {{ `${$account.org.country}${$account.org.zip ? `,` : ``}` }}
            </span>
          </div>
          <div v-if="$account.org.zip" class="label-txt-black line-height20">
            {{ `${$account.org.zip}` }}
          </div>
        </div>
        <div class="">
          <div v-if="$org.id === 17 && $org.domain === 'maximus'">
            <div
              v-if="
                details.financial_type_quote === 2 ||
                  details.financial_type_quote === 3
              "
            >
              <div class="text-right fc-black3-16 fwBold line-height30 f25">
                INVOICE
              </div>
              <div class="label-txt-black text-right">
                INVOICE #{{ details.parentId }}
              </div>
            </div>
            <div v-else>
              <div class="text-right fc-black3-16 fwBold line-height30 f25">
                QUOTE
              </div>
              <div class="label-txt-black text-right">
                QUOTE #{{ details.parentId }}
              </div>
            </div>
          </div>

          <div v-else>
            <div class="text-right fc-black3-16 fwBold line-height30 f25">
              QUOTE
            </div>
            <div class="label-txt-black text-right">
              QUOTE #{{ details.parentId }}
            </div>
          </div>

          <div
            class="fc-tag-round fc-tag-round-vio mT10 fR"
            v-if="
              !$validation.isEmpty(details.revisionNumber) &&
                details.revisionNumber > 0
            "
          >
            REVISION: {{ details.revisionNumber }}
          </div>
        </div>
      </div>
      <!-- bill box -->
      <div class="fc-quotation-bill-con clearboth">
        <!-- bill to -->
        <div class="p10 pB20 padding-remove-bottom-pdf">
          <span class="fc-black-12 text-left fc-bill-to-txt position-relative">
            BILL TO
          </span>
          <div class="d-flex justify-content-space pT10">
            <div class="width70">
              <div
                v-if="
                  !$validation.isEmpty(
                    $getProperty(details, 'contact.name', -1)
                  )
                "
                @click="routeToSummary(details)"
                class="fc-black-15 fwBold line-height25 break-word pR10 pointer"
              >
                {{ `${details.contact.name}` }}
              </div>
              <div
                class="fc-black-13 line-height25 text-left break-word-all"
                v-if="
                  !$validation.isEmpty(
                    $getProperty(details, 'contact.email', -1)
                  )
                "
              >
                {{ `${details.contact.email}` }}
              </div>
              <div class="fc-black-13 text-left line-height25">
                <span v-if="$getProperty(details, 'billToAddress.street')">
                  {{
                    `${$getProperty(details, 'billToAddress.street', '')}${
                      $getProperty(details, 'billToAddress.city') ? `,` : ``
                    }`
                  }}
                </span>
                <span v-if="$getProperty(details, 'billToAddress.city')">
                  {{
                    `${$getProperty(details, 'billToAddress.city', '')}${
                      $getProperty(details, 'billToAddress.state') ? `,` : ``
                    }`
                  }}</span
                >
              </div>
              <div class="fc-black-13 text-left line-height25">
                <span v-if="$getProperty(details, 'billToAddress.state')">
                  {{
                    `${$getProperty(details, 'billToAddress.state', '')}${
                      $getProperty(details, 'billToAddress.country') ? `,` : ``
                    }`
                  }}
                </span>
                <span v-if="$getProperty(details, 'billToAddress.country')">
                  {{
                    `${$getProperty(details, 'billToAddress.country', '')}${
                      $getProperty(details, 'billToAddress.zip') ? `,` : ``
                    }`
                  }}
                </span>
              </div>
              <div class="fc-black-13 text-left line-height25">
                <span v-if="$getProperty(details, 'billToAddress.zip')">
                  {{ `${$getProperty(details, 'billToAddress.zip', '')}` }}
                </span>
              </div>
            </div>
            <div class="width30 pT5">
              <el-row
                v-if="!$validation.isEmpty(details.billDate)"
                class="pB10"
              >
                <el-col :span="12">
                  <div class="fc-black-12 text-left fwBold">
                    BILLING DATE
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="fc-black-12 text-left">
                    {{ details.billDate | formatDate(true) }}
                  </div>
                </el-col>
              </el-row>
              <el-row
                v-if="!$validation.isEmpty(details.expiryDate)"
                class="pB10"
              >
                <el-col :span="12">
                  <div class="fc-black-12 text-left fwBold">
                    EXPIRY DATE
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="fc-black-12 text-left">
                    {{ details.expiryDate | formatDate(true) }}
                  </div>
                </el-col>
              </el-row>
              <el-row
                v-if="
                  !$validation.isEmpty(
                    $getProperty(details, 'workorder.serialNumber', -1)
                  )
                "
                class="pB10"
              >
                <el-col :span="12">
                  <div class="fc-black-12 text-left fwBold">
                    WORKORDER ID
                  </div>
                </el-col>
                <el-col :span="12">
                  <div
                    @click="routeToWOSummary(details.workorder.id)"
                    class="fc-black-12 text-left"
                    :class="[isSummary && 'bluetxt pointer']"
                  >
                    {{ `#${details.workorder.serialNumber}` }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>
      <!-- subject -->
      <div>
        <div class="fc-black-13 text-left pT20">
          <span class="fwBold pR10">SUBJECT:</span>
          {{ details.subject }}
        </div>
        <div class="pT10 line-height20 fc-black-13 text-left">
          {{ details.description }}
        </div>
      </div>
      <!-- table -->
      <LineItemsTable
        :details="details"
        :uomEnumMap="uomEnumMap"
      ></LineItemsTable>
      <!-- terms and condition -->
      <div
        class="pT40 border-bottom17 pB40 fc-page-break-avoid"
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
          <div v-html="termsData.terms.longDesc">
            {{ termsData.terms.longDesc }}
          </div>
        </div>
      </div>
      <!-- signature -->
      <div class="pT40 fc-quotation-signature">
        <div v-if="details.signatureId > 0">
          <img
            :src="$helpers.getImagePreviewUrl(details.signatureId)"
            class="width100px height50"
          />
        </div>
        <div class="fc-black-13 text-left fwBold">
          SIGNATURE
        </div>
        <div class="fc-black-13 text-left pT10" v-if="details.sysCreatedTime">
          <span class="fwBold pR20">DATE</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import QuotationMixin from 'src/pages/quotation/v1/mixins/QuotationMixin'
import LineItemsTable from 'src/pages/quotation/components/LineItemsSummaryTable'
export default {
  mixins: [QuotationMixin],
  components: {
    LineItemsTable,
  },
}
</script>
