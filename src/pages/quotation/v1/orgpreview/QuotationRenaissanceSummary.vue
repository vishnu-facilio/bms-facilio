<template>
  <div class="fc-brightfm-po-pdf-con fc-renassiance-quote-summary">
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div
      class="fc-quotation-con mT0 fc-bright-fm-quote overflow-hidden"
      ref="preview-container"
      v-else
    >
      <div class="fc-qfm-page-break">
        <!-- subject -->
        <div v-if="$org.logoUrl" class="fc-quotation-logo">
          <img :src="$org.logoUrl" style="width: 100px;" />
        </div>
        <div class="label-txt-black text-uppercase fwBold pT10">
          RENAISSANCE SERVICES SAOG
        </div>
        <div class="flex-middle justify-content-space width100">
          <div class="pT5">
            <div class="label-txt-black line-height20">
              P O Box: 1676, P.C. 114 - Muttrah,
            </div>
            <div class="label-txt-black line-height20">
              Muscat Governorate, OM,
            </div>
            <div class="label-txt-black line-height20">
              112
            </div>
          </div>
          <div class="">
            <div class="text-right fc-black3-16 fwBold line-height30 f25">
              QUOTATION
            </div>
            <div class="label-txt-black text-right pT10">
              QUOTE #{{ details.parentId }}
            </div>
          </div>
        </div>
        <!-- second row -->
        <!-- bill box -->
        <div class="fc-quotation-bill-con clearboth">
          <!-- bill to -->
          <div class="padding-remove-bottom-pdf">
            <div class="d-flex justify-content-space">
              <div class="width50 border-right8 pT20 pB20">
                <span
                  class="fc-black-12 text-left fc-bill-to-txt position-relative"
                >
                  BILL TO
                </span>
                <div
                  v-if="
                    details.customerType === 1 &&
                      !$validation.isEmpty(
                        $getProperty(details, 'tenant.name', -1)
                      )
                  "
                  @click="routeToSummary(details)"
                  class="fc-black-15 fwBold line-height25 break-word pR10 pointer pT15"
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
                <div
                  class="fc-black-13 line-height25 text-left break-word-all bold fc-text-underline2"
                  style="color: #005ef2;"
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
                  v-if="$getProperty(tenantSummaryData, 'lookup_1.name')"
                >
                  {{ $getProperty(tenantSummaryData, 'lookup_1.name') }}
                </div>
                <div
                  class="fc-black-13 line-height25 text-left break-word-all"
                  v-if="$getProperty(tenantSummaryData, 'tenantunit.name')"
                >
                  {{ $getProperty(tenantSummaryData, 'tenantunit.name') }}
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
                    details.customerType === 2 &&
                      !$validation.isEmpty(
                        $getProperty(details, 'client.primaryContactEmail', -1)
                      )
                  "
                >
                  {{ `${details.client.primaryContactEmail}` }}
                </div>
              </div>
              <div class="width50 pL20 pT20 pB20">
                <el-row
                  v-if="!$validation.isEmpty(details.billDate)"
                  class="pB10"
                >
                  <el-col :span="8">
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      {{
                        $getProperty(fieldsMap, 'billDate.displayName') ||
                          'Billing Date'
                      }}
                    </div>
                  </el-col>
                  <el-col :span="16">
                    <div class="fc-black-13 text-left">
                      {{ details.billDate | formatDate(true) }}
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
                  <el-col :span="8">
                    <div class="fc-black-12 text-left fwBold text-uppercase">
                      WORKORDER ID
                    </div>
                  </el-col>
                  <el-col :span="16">
                    <div
                      @click="routeToWOSummary(details.workorder.id)"
                      class="fc-black-13 text-left"
                      :class="[isSummary && 'bluetxt pointer']"
                    >
                      {{ `#${details.workorder.serialNumber}` }}
                    </div>
                  </el-col>
                </el-row>
                <el-row
                  v-if="
                    !$validation.isEmpty(
                      $getProperty(details, 'workorder.datetime')
                    )
                  "
                  class="pB10 flex-middle"
                >
                  <el-col :span="8">
                    <div
                      class="fc-black-12 text-left fwBold text-uppercase line-height20"
                    >
                      Scheduled inspection date
                    </div>
                  </el-col>
                  <el-col :span="16">
                    <div class="fc-black-13 text-left">
                      {{ details.workorder.datetime | formatDate(true) }}
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </div>
        <!-- third row -->
        <div>
          <div
            class="pT20 line-height20 fc-black-13 text-left"
            v-if="!$validation.isEmpty($getProperty(details, 'description'))"
          >
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
              <td
                colspan="100%"
                v-if="
                  !$validation.isEmpty($getProperty(details, 'multiline_2'))
                "
                class="pT20"
              >
                <div class="line-height20 fc-black-13 text-left">
                  {{ details.multiline_2 }}
                </div>
              </td>
            </tr>

            <tr>
              <td colspan="100%" class="pT20">
                <el-row class="fc-extra-border">
                  <el-col
                    :span="5"
                    class="fc-black-12 text-uppercase fwBold text-left"
                  >
                    Validity period
                  </el-col>
                  <el-col :span="19" class="fc-black-14 text-left">
                    Our Quotation is valid for 15 Days
                  </el-col>
                </el-row>
                <el-row class="fc-extra-border">
                  <el-col
                    :span="5"
                    class="fc-black-12 text-uppercase fwBold text-left"
                  >
                    Payment Terms
                  </el-col>
                  <el-col :span="19" class="fc-black-14 text-left">
                    100% Advance payment
                  </el-col>
                </el-row>
                <el-row class="fc-extra-border">
                  <el-col
                    :span="5"
                    class="fc-black-12 text-uppercase fwBold text-left"
                  >
                    Delivery period
                  </el-col>
                  <el-col :span="19" class="fc-black-14 text-left">
                    After receiving payment confirmation
                  </el-col>
                </el-row>
                <el-row class="fc-extra-border border-bottom17">
                  <el-col
                    :span="5"
                    class="fc-black-12 text-uppercase fwBold text-left"
                  >
                    Completion
                  </el-col>
                  <el-col :span="19" class="fc-black-14 text-left">
                    TBA
                  </el-col>
                </el-row>
              </td>
            </tr>
            <tr>
              <td colspan="100%" v-if="details.signatureId > 0" class="pT20">
                <!-- signature -->
                <div class="fc-page-break-avoid">
                  <div>
                    <img
                      :src="$helpers.getImagePreviewUrl(details.signatureId)"
                      class="width100px height50"
                    />
                  </div>
                  <div
                    class="fc-black-12 text-left fwBold f13 pT10 text-uppercase"
                  >
                    Authorized signatory
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td
                colspan="100"
                v-if="!$validation.isEmpty(details.signatureUploadedTime)"
                class="pT20"
              >
                <el-row class="flex-middle">
                  <el-col
                    :span="2"
                    class="fc-black-12 fwBold text-left f13 text-uppercase"
                  >
                    DATE
                  </el-col>
                  <el-col :span="22">
                    <div class="text-left fc-black-14">
                      {{ details.signatureUploadedTime | formatDate(true) }}
                    </div>
                  </el-col>
                </el-row>
              </td>
            </tr>

            <tr>
              <td colspan="100%" class="pT20">
                <div class="fc-office-block">
                  <span
                    class="fc-black-12 text-left fc-bill-to-txt position-relative text-uppercase"
                  >
                    Office Use
                  </span>
                  <el-row class="pT20">
                    <el-col :span="4">
                      <div class="fc-black-14 text-left fwBold">
                        Email
                      </div>
                    </el-col>
                    <el-col :span="20">
                      <div
                        class="fc-link-blue fc-text-underline2"
                        v-if="
                          details.customerType === 1 &&
                            !$validation.isEmpty(
                              $getProperty(
                                details,
                                'tenant.primaryContactEmail',
                                -1
                              )
                            )
                        "
                      >
                        {{ `${details.tenant.primaryContactEmail}` }}
                      </div>
                    </el-col>
                  </el-row>
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
      <div class="fc-print-footer-center" style="background: #f9f7ff;">
        <div
          class="d-flex flex-col bright-env-border-content fc-blue-txt3-11 align-center pT5 f10"
        >
          <div class="mT5">
            {{
              `PO Box: 1676 | P.C: 114, CBD, Ruwi, Sultanate of Oman, CR No. 1/52285/0/ VATIN OMI 120006606X | Tel: 22845767`
            }}
          </div>
          <div class="mT5 fwBold">
            {{
              `Email ID: sctwn@tiscooman.com | Website: www.renaissanceservices.com`
            }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import QuotationMixin from 'src/pages/quotation/v1/mixins/QuotationMixin'
import LineItemsTable from 'src/pages/quotation/components/LineItemsSummaryTable'
import { API } from '@facilio/api'
export default {
  data() {
    return {
      tenantSummaryData: [],
    }
  },
  mixins: [QuotationMixin],
  components: {
    LineItemsTable,
  },
  created() {
    this.loadTenantSummary()
  },
  methods: {
    async loadTenantSummary() {
      this.isLoading = true
      let { error, data } = await API.get(
        `v3/modules/data/summary?id=${this.details.tenant.id}&moduleName=tenant`
      )
      if (error) {
        this.$messsage.error(
          message || 'Error Occured while fetching quote module meta'
        )
      } else {
        this.tenantSummaryData = data.tenant
      }
    },
  },
}
</script>
<style lang="scss">
.fc-renassiance-quote-summary {
  .fc-quotation-bill-con {
    padding-left: 20px;
    padding-right: 20px;
  }
  .fc-extra-border {
    padding-top: 15px;
    padding-bottom: 15px;
    border-left: 1px solid #e4eaed;
    padding-left: 20px;
    padding-right: 20px;
    border-top: 1px solid #e4eaed;
    border-right: 1px solid #e4eaed;
  }
  .fc-office-block {
    padding: 10px 20px 30px;
    border: 1px solid #acacac;
    border-style: dashed;
  }
}

@media print {
  @page {
    margin-bottom: 0 !important;
  }
  .fc-renassiance-quote-summary {
    .fc-bfm-print-footer {
      height: 70px;
    }
    .fc-print-footer-center {
      width: 100% !important;
      max-width: 100%;
      padding-left: 0;
      padding-right: 0;
      padding-top: 10px;
      padding-bottom: 10px;
      .fc-blue-txt3-11 {
        font-size: 12px !important;
        color: #2f4058;
      }
    }
  }
  .fc-extra-border {
    padding-top: 10px !important;
    padding-bottom: 10px !important;
  }
}
</style>
