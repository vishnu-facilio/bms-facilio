<template>
  <div class="fc-po-pr-sumary-con fc-brightfm-po-pdf-con fc-aster-po-preview">
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div
      ref="preview-container"
      class="fc-quotation-con fc-summary-preview-con mT0 overflow-hidden"
      v-else
    >
      <div class="pdf-padding-container">
        <div class="flex-middle justify-content-space width100">
          <div v-if="$org.logoUrl" class="fc-quotation-logo">
            <img :src="$prependBaseUrl($org.logoUrl)" style="width: 130px;" />
          </div>
          <div>
            <div class="label-txt-black fwBold text-right">
              Eurohealth Systems
            </div>
            <div class="label-txt-black pT5">
              Tel: +971 4 4281483, Fax: +971 4 4281482
            </div>
            <div class="label-txt-black pT5 text-right">
              P.O Box 8703, Dubai, UAE
            </div>
            <div class="pT5 text-right">
              <a
                href="https://eurohealthsystems.com/"
                target="_blank"
                class="fc-link-blue"
                >www.eurohealthsystems.com</a
              >
            </div>
            <div class="label-txt-black pT5 text-right">
              VAT Reg No : 100010192100003
            </div>
          </div>
        </div>

        <!-- table -->
        <div class="page-break pT20">
          <table class="width100 border-none">
            <tbody>
              <tr>
                <td colspan="100%">
                  <div class="d-flex flex-direction-column items-center">
                    <div class="fc-black3-16 fwBold">
                      Purchase Order
                    </div>
                    <div class="pT5 label-txt-black">
                      <span class="fwBold">PO Number</span> #{{
                        details.localId
                      }}
                    </div>
                    <div class="pT5 label-txt-black fc-print-pB10">
                      <span class="fwBold">Date</span>
                      {{ details.orderedTime | formatDate(true) }}
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <!-- bill box -->
                  <div class="fc-quotation-bill-con clearboth mT20">
                    <!-- bill to -->
                    <div class="padding-remove-bottom-pdf">
                      <div class="d-flex justify-content-space">
                        <div
                          v-if="canShowBillToShowAddress"
                          class="width50 pL20 pT20 pB10"
                        >
                          <span
                            class="fc-black-12 text-left fc-bill-to-txt position-relative"
                          >
                            VENDOR
                          </span>
                          <div
                            class="fc-black-15 pT15 fwBold line-height25 break-word pR10 pointer"
                            v-if="
                              !$validation.isEmpty(
                                $getProperty(details, 'vendor.name', -1)
                              )
                            "
                          >
                            {{ details.vendor.name }}
                          </div>
                          <div
                            class="fc-black-13 text-left line-height25"
                            v-if="
                              $getProperty(details.vendor, 'address.street')
                            "
                          >
                            {{
                              `${$getProperty(
                                details.vendor,
                                'address.street',
                                ''
                              )}${
                                $getProperty(details.vendor, 'address.city')
                                  ? `,`
                                  : ``
                              }`
                            }}
                          </div>
                          <div
                            class="fc-black-13 text-left line-height25"
                            v-if="$getProperty(details.vendor, 'address.city')"
                          >
                            {{
                              `${$getProperty(
                                details.vendor,
                                'address.city',
                                ''
                              )}${
                                $getProperty(details.vendor, 'address.state')
                                  ? `,`
                                  : ``
                              }`
                            }}
                          </div>
                          <div
                            class="fc-black-13 text-left line-height25"
                            v-if="$getProperty(details.vendor, 'address.state')"
                          >
                            {{
                              `${$getProperty(
                                details.vendor,
                                'address.state',
                                ''
                              )}${
                                $getProperty(details.vendor, 'address.country')
                                  ? `,`
                                  : ``
                              }`
                            }}
                          </div>
                          <div
                            class="fc-black-13 text-left line-height25"
                            v-if="
                              $getProperty(details.vendor, 'address.country')
                            "
                          >
                            {{
                              `${$helpers.getDiplayNameforCountryISOCode(
                                $getProperty(
                                  details.vendor,
                                  'address.country',
                                  ''
                                )
                              )}${
                                $getProperty(details.vendor, 'address.zip')
                                  ? `,`
                                  : ``
                              }`
                            }}
                          </div>
                          <div
                            class="fc-black-13 text-left line-height25"
                            v-if="$getProperty(details.vendor, 'address.zip')"
                          >
                            {{
                              `${$getProperty(
                                details.vendor,
                                'address.zip',
                                ''
                              )}`
                            }}
                          </div>
                        </div>
                        <div
                          v-if="canShowShipToShowAddress"
                          :class="[
                            canShowBillToShowAddress && 'pL20 border-left5',
                          ]"
                          class="width50 pL20 pT20 pB10"
                        >
                          <span
                            class="fc-black-12 text-left fc-bill-to-txt position-relative"
                          >
                            Deliver to
                          </span>
                          <div
                            v-if="$getProperty(details, 'storeRoom.name')"
                            class="fc-black-15 fwBold pT15 line-height25 break-word pR10 pointer"
                          >
                            {{ details.storeRoom.name }}
                          </div>
                          <div
                            class="line-height20 fc-black-13 text-left space-preline"
                            v-if="$getProperty(details, 'multiline_1')"
                          >
                            {{ details.multiline_1 }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <!-- subject -->
                  <div class="fc-quotation-bill-con border-top-none pT15 pB15">
                    <div class="pL20">
                      <el-row>
                        <el-col :span="4">
                          <div class="fc-black-13 text-left">
                            <span class="fwBold pR10">Project</span>
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div
                            class="line-height20 fc-black-13 text-left"
                            v-if="$getProperty(details, 'singleline_7')"
                          >
                            {{
                              details.singleline_7
                                ? details.singleline_7
                                : '---'
                            }}
                          </div>
                          <div v-else>
                            ---
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <!-- subject -->
                  <div class="fc-quotation-bill-con border-top-none pT15 pB15">
                    <div class="pL20">
                      <el-row>
                        <el-col :span="4">
                          <div class="fc-black-13 text-left">
                            <span class="fwBold pR10">Buyer</span>
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div
                            class="line-height20 fc-black-13 text-left"
                            v-if="$getProperty(details, 'singleline_5')"
                          >
                            {{ details.singleline_5 }}
                          </div>
                          <div v-else>
                            ---
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <!-- subject -->
                  <div class="fc-quotation-bill-con border-top-none pT15 pB15">
                    <div class="pL20">
                      <el-row>
                        <el-col :span="4">
                          <div class="fc-black-13 text-left">
                            <span class="fwBold pR10">Delivery Date</span>
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div
                            class="line-height20 fc-black-13 text-left"
                            v-if="$getProperty(details, 'requiredTime')"
                          >
                            {{ details.requiredTime | formatDate(true) }}
                          </div>
                          <div v-else>
                            ---
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <!-- subject -->
                  <div class="fc-quotation-bill-con border-top-none pT15 pB15">
                    <div class="pL20">
                      <el-row>
                        <el-col :span="4">
                          <div class="fc-black-13 text-left">
                            <span class="fwBold pR10">Payment Terms</span>
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div
                            class="line-height20 fc-black-13 text-left"
                            v-if="$getProperty(details, 'singleline_4')"
                          >
                            {{ details.singleline_4 }}
                          </div>
                          <div v-else>
                            ---
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <LineItemsTable
                    :details="details"
                    :uomEnumMap="uomEnumMap"
                  ></LineItemsTable>
                </td>
              </tr>
              <tr>
                <td colspan="100%" class="p0">
                  <!-- subject -->
                  <div class="fc-quotation-bill-con border-top-none pT15 pB15">
                    <div class="pL20">
                      <el-row class="flex-middle">
                        <el-col :span="4">
                          <div class="fc-black-13 text-left">
                            <span class="fwBold pR10">Remarks</span>
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div
                            class="line-height20 fc-black-13 text-left"
                            v-if="$getProperty(details, 'multiline_2')"
                          >
                            {{ details.multiline_2 }}
                          </div>
                          <div v-else class="fc-grey-text12-light f13">
                            No Remarks
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%">
                  <div class="pT20">
                    <div class="fc-black-13 pT10 text-left line-height20 pR20">
                      This order is subject to the Eurohealth PO – Standard
                      Terms & Conditions which is attached to this PO and which
                      shall be deemed to be accepted by and shall be binding
                      upon Supplier on confirmation of acceptance of Order or
                      delivery of the relevant Goods or provision of the
                      Services.
                    </div>
                  </div>
                  <div class="page-break-always"></div>
                </td>
              </tr>

              <tr>
                <td colspan="100%">
                  <!-- terms and condition -->
                  <div
                    class="pT20 border-bottom17 pB20 fc-page-break-avoid"
                    v-if="!$validation.isEmpty(details.termsAssociated)"
                  >
                    <div>
                      <div class="fc-black-13 fwBold text-left">
                        TERMS AND CONDITIONS
                      </div>
                      <div
                        class="fc-black-13 text-left pT10 line-height20 break-word"
                        v-for="termsData in details.termsAssociated"
                        :key="termsData.id"
                      >
                        <div v-html="sanitize(termsData.terms.longDesc)"></div>
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
          <div
            class="d-flex flex-col bright-env-border-content fc-blue-txt3-11 align-center pT5 f10"
          >
            This is a system generated Purchase Order which doesn’t require
            signature.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import PurchaseOrderMixin from 'src/pages/purchase/po/mixin/poMixin'
import LineItemsTable from 'src/pages/purchase/pr/PrLineItemsTable'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  mixins: [PurchaseOrderMixin],
  components: {
    LineItemsTable,
  },
  created() {
    this.sanitize = sanitize
  },
}
</script>
<style lang="scss">
.fc-aster-po-preview {
  .fc-quotation-bill-con {
    margin-top: 0;
  }
  .fc-qfm-pt20 {
    padding-top: 0 !important;
  }
}
@media print {
  .fc-aster-po-preview {
    .fc-bfm-print-footer {
      height: 30px !important;
    }
    .fc-quotation-bill-con {
      margin-top: 0 !important;
    }
    .fc-print-pB10 {
      padding-bottom: 10px;
    }
  }
  .page-break-always {
    page-break-before: always;
    page-break-after: always;
  }
}
</style>
