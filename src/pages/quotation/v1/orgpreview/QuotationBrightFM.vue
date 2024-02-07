<template>
  <div class="fc-brightfm-po-pdf-con">
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div
      v-else
      class="fc-quotation-con mT0 fc-bright-fm-quote overflow-hidden"
      ref="preview-container"
    >
      <div class="fc-qfm-page-break">
        <!-- subject -->
        <div v-if="$org.logoUrl" class="fc-quotation-logo">
          <img :src="$org.logoUrl" style="width: 50px;" />
        </div>
        <div class="label-txt-black text-uppercase fwBold pT10">
          BRIGHT ENVIRONMENT LLC
        </div>
        <div class="flex-middle justify-content-space width100">
          <div class="pT5">
            <div class="label-txt-black line-height20">
              info@brightenv.ae
            </div>
            <div class="label-txt-black line-height20">
              Business Bay, Dubai
            </div>
            <div class="label-txt-black line-height20">
              VATIN: 100202847800003
            </div>
          </div>
          <div class="">
            <div class="text-right fc-black3-16 fwBold line-height30 f25">
              QUOTE
            </div>
            <div class="label-txt-black text-right">
              QUOTE #{{ details.parentId }}
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
        <!-- second row -->
        <!-- bill box -->
        <div class="fc-quotation-bill-con clearboth">
          <!-- bill to -->
          <div class="p10 pB20 padding-remove-bottom-pdf">
            <span
              class="fc-black-12 text-left fc-bill-to-txt position-relative"
            >
              BILL TO
            </span>
            <div class="d-flex justify-content-space pT10">
              <div class="width70">
                <div
                  v-if="
                    details.customerType === 1 &&
                      !$validation.isEmpty(
                        $getProperty(details, 'tenant.name', -1)
                      )
                  "
                  @click="routeToSummary(details)"
                  class="fc-black-15 fwBold line-height25 break-word pR10 pointer"
                >
                  {{ `${details.tenant.name}` }}
                </div>
                <div
                  v-if="
                    details.customerType === 2 &&
                      !$validation.isEmpty(
                        $getProperty(details, 'client.name', -1)
                      )
                  "
                  @click="routeToSummary(details)"
                  class="fc-black-15 fwBold line-height25 break-word pR10 pointer"
                >
                  {{ `${details.client.name}` }}
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
                        $helpers.getDiplayNameforCountryISOCode(
                          $getProperty(details, 'billToAddress.country')
                        )
                          ? `,`
                          : ``
                      }`
                    }}
                  </span>
                  <span v-if="$getProperty(details, 'billToAddress.country')">
                    {{
                      `${$helpers.getDiplayNameforCountryISOCode(
                        $getProperty(details, 'billToAddress.country')
                      )}${
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
                <div
                  class="fc-black-13 line-height25 text-left break-word-all"
                  v-if="
                    details.customerType === 1 &&
                      !$validation.isEmpty(
                        $getProperty(details, 'tenant.primaryContactEmail', -1)
                      )
                  "
                >
                  {{ `${details.tenant.primaryContactEmail}` }}
                </div>
                <div
                  class="fc-black-13 line-height25 text-left break-word-all"
                  v-if="
                    details.customerType === 2 &&
                      !$validation.isEmpty(
                        $getProperty(details, 'client.primaryContactEmail', -1)
                      )
                  "
                >
                  {{ `${details.client.primaryContactEmail}` }}
                </div>
              </div>
              <div class="width30 pT5">
                <el-row
                  v-if="!$validation.isEmpty(details.billDate)"
                  class="pB10"
                >
                  <el-col :span="12">
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      {{
                        $getProperty(fieldsMap, 'billDate.displayName') ||
                          'Billing Date'
                      }}
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
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      {{
                        $getProperty(fieldsMap, 'expiryDate.displayName') ||
                          'Expiry Date'
                      }}
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
                <el-row
                  v-if="
                    !$validation.isEmpty($getProperty(details, 'siteId', -1))
                  "
                  class="pB10"
                >
                  <el-col :span="12">
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      {{ $t('common.products.site') }}
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-black-12 text-left">
                      {{
                        ($store.getters.getSite(details.siteId) || {}).name ||
                          '---'
                      }}
                    </div>
                  </el-col>
                </el-row>
                <el-row
                  v-if="
                    !$validation.isEmpty(
                      $getProperty(details, 'space.name', -1)
                    )
                  "
                  class="pB10"
                >
                  <el-col :span="12">
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      {{ $t('common._common.space/asset') }}
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-black-12 text-left">
                      {{ details.space.name }}
                    </div>
                  </el-col>
                </el-row>
                <el-row
                  v-if="
                    !$validation.isEmpty(
                      $getProperty(details, 'building.name', -1)
                    )
                  "
                  class="pB10"
                >
                  <el-col :span="12">
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      {{ $t('maintenance.pm_list.building') }}
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-black-12 text-left">
                      {{ details.building.name }}
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </div>
        <!-- third row -->
        <div>
          <div class="fc-black-13 text-left pT20">
            <span class="fwBold pR10">SUBJECT:</span>
            {{ details.subject }}
          </div>
          <div class="pT10 line-height20 fc-black-13 text-left">
            {{ details.description }}
          </div>
        </div>
        <table class="width100 border-none" cellpadding="0" cellspacing="0">
          <tbody>
            <tr>
              <td colspan="100%">
                <!-- table -->
                <LineItemsTable
                  :details="details"
                  :uomEnumMap="uomEnumMap"
                ></LineItemsTable>
              </td>
            </tr>
            <tr>
              <td colspan="100%">
                <div
                  class="pT20 widthreduce-pdf fc-page-break-avoid"
                  v-if="!$validation.isEmpty(details.notes)"
                >
                  <div class="fc-black-13 fwBold text-left space-preline">
                    NOTES
                  </div>
                  <div
                    class="fc-black-13 pT10 text-left space-preline line-height20 pR20"
                  >
                    {{ details.notes }}
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="100%">
                <!-- terms and condition -->
                <div
                  class="pT20 border-bottom17 pB20 terms-condition-block fc-page-break-avoid"
                  v-if="!$validation.isEmpty(details.termsAssociated)"
                >
                  <div class="fc-black-13 fwBold text-left">
                    TERMS AND CONDITIONS
                  </div>
                  <div
                    class="fc-black-13 text-left pT10 line-height20 break-word"
                    v-for="termsData in details.termsAssociated"
                    :key="termsData.id"
                  >
                    <div v-html="termsData.terms.longDesc">
                      {{ termsData.terms.longDesc }}
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="100%">
                <!-- signature -->
                <div
                  class="d-flex flex-row justify-content-space fc-page-break-avoid bright-fm-sign"
                >
                  <div class="pT40 fc-quotation-signature">
                    <div v-if="details.signature_1Id > 0">
                      <img
                        :src="
                          $helpers.getImagePreviewUrl(details.signature_1Id)
                        "
                        class="width100px height50"
                      />
                    </div>
                    <div v-else class="width100px height50"></div>
                    <div class="fc-black-13 text-left fwBold">
                      Authorized Signatory
                    </div>
                  </div>
                  <div class="pT40 fc-quotation-signature">
                    <div v-if="details.signatureId > 0">
                      <img
                        :src="$helpers.getImagePreviewUrl(details.signatureId)"
                        class="width100px height50"
                      />
                    </div>
                    <div v-else class="width100px height50"></div>
                    <div class="fc-black-13 text-left fwBold">
                      Prepared By
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>

          <tfoot class="fc-page-footer">
            <tr>
              <td style="border: none !important;">
                <div class="page-footer-space"></div>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
    <div class="fc-bfm-print-footer">
      <div class="fc-print-footer-center">
        <div class="bright-env-border position-relative"></div>
        <div
          class="d-flex flex-col bright-env-border-content fc-blue-txt3-11 align-center pT5 f10"
        >
          <div class="mT5">
            {{
              `Head office: Tel:+971-4-5585007, Fax: +971-4-5584997, P.O. Box: 76299, 2401, Al Manara Tower, Business Bay, Dubai, U.A.E`
            }}
          </div>
          <div class="mT5">
            {{
              `Branches: No.6, Maysaloon Area, Sharjah Main City, Sharjah, U.A.E || No. 6, 4th Floor, Shaika Mariam Building, Corniche, Abu Dhabi, U.A.E`
            }}
          </div>
          <div class="mT5">
            Web: www.brightenv.ae || Email: info@brightenv.ae
          </div>
          <div class="mT5">ISO9001 : 2015 & 14001 : 2015</div>
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
