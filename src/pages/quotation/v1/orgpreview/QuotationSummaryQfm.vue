<template>
  <div>
    <div v-if="isLoading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <template v-else>
      <div ref="preview-container" class="fc-quotation-con mT0 overflow-hidden">
        <div class="fc-qfm-page-break">
          <div class="flex-middle justify-content-space">
            <div class="">
              <img
                src="~assets/customer-logo/quality-group.jpeg"
                width="90"
                height="100"
              />
            </div>
            <div v-if="$getProperty(details, 'from') === 1">
              <img
                src="~assets/customer-logo/quality-llc.png"
                width="300"
                height="70"
                class="object-scale-down"
              />
            </div>
            <div v-if="$getProperty(details, 'from') === 2">
              <img
                src="~assets/customer-logo/emirtage.png"
                width="200"
                height="60"
                class="object-scale-down"
              />
            </div>
          </div>

          <table class="width100 border-none">
            <tbody>
              <tr>
                <td colspan="100%">
                  <div class="flex-middle justify-content-space fc-qfm-mT0">
                    <div class="fc-black3-16 f25 fwBold text-uppercase">
                      {{ $t('quotation.common.qfm_quotation') }}
                    </div>
                    <div>
                      <div>
                        <div
                          class="fc-black-14 bold text-uppercase text-right"
                          v-if="!$validation.isEmpty(details.billDate)"
                        >
                          <span class="bold f14 pL5">{{
                            details.billDate | formatDate(true)
                          }}</span>
                        </div>
                        <div class="d-flex flex-direction-row justify-end">
                          <div
                            class="fc-black-14 bold text-right"
                            v-if="!$validation.isEmpty(details.quoteid)"
                          >
                            {{ details.quoteid }}
                          </div>
                          <!-- <div
                  v-if="!$validation.isEmpty(details.revisionNumber)"
                  class="fc-black-14 bold line-height25 text-right "
                >
                  {{ `${details.revisionNumber}` }}
                </div> -->
                        </div>
                        <div
                          class="fc-black-14 bold text-right"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'picklist_1', -1)
                            )
                          "
                        >
                          {{
                            $getProperty(
                              fieldsMap,
                              `picklist_1.enumMap.${details.picklist_1}`,
                              '---'
                            )
                          }}
                        </div>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%">
                  <div class="fc-border3 fc-qfm-subject width100 ">
                    <el-row>
                      <el-col :span="12" class="p10 print-reduced-padding">
                        <el-row
                          class="pB15"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'contact.name', -1)
                            )
                          "
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.to') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{ `${details.contact.name}` }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'client.name', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.company') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{ $getProperty(details, 'client.name', '---') }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'building.name', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.building') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{
                                $getProperty(details, `building.name`, '---')
                              }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          class="pB15"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'unit.name', -1)
                            )
                          "
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.unit_no') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{ $getProperty(details, `unit.name`, '---') }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          class="pB15"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'unit.picklist', -1)
                            )
                          "
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.unit_capacity') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{
                                $getProperty(
                                  tenantUnitsFieldMap,
                                  `picklist.enumMap.${details.unit.picklist}`,
                                  '---'
                                )
                              }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'category', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.category') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{
                                $getProperty(
                                  fieldsMap,
                                  `category.enumMap.${details.category}`,
                                  '---'
                                )
                              }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          class="pB15"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'contact.phone', -1)
                            )
                          "
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.telephone') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{ `${details.contact.phone}` }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          class="pB15"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'contact.email', -1)
                            )
                          "
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.email_id') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left break-word-all">
                              {{ `${details.contact.email}` }}
                            </div>
                          </el-col>
                        </el-row>
                      </el-col>
                      <el-col :span="12" class="p10 print-reduced-padding">
                        <el-row class="pB15">
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.from') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{ $t('quotation.common.quality_group') }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'from', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.division') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{
                                $getProperty(
                                  fieldsMap,
                                  `from.enumMap.${details.from}`,
                                  '---'
                                )
                              }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          class="pB15"
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(
                                details,
                                'workorder.serialNumber',
                                -1
                              )
                            )
                          "
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.job_order_no') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
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
                            !$validation.isEmpty(
                              $getProperty(details, 'from', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.telephone') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div
                              v-if="$getProperty(details, 'from') === 1"
                              class="fc-black-12 text-left"
                            >
                              04 396 5955
                            </div>
                            <div
                              v-if="$getProperty(details, 'from') === 2"
                              class="fc-black-12 text-left"
                            >
                              04 326 9429
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'from', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.email_id') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div
                              v-if="$getProperty(details, 'from') === 1"
                              class="fc-black-12 text-left"
                            >
                              info@thequalitygroup.ae
                            </div>
                            <div
                              v-if="$getProperty(details, 'from') === 2"
                              class="fc-black-12 text-left"
                            >
                              hi@emirtage.com
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'from', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.url') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div
                              v-if="$getProperty(details, 'from') === 1"
                              class="fc-black-12 text-left"
                            >
                              <a
                                href="http://www.thequalitygroup.ae/"
                                target="_blank"
                                class="fc-black-12 no-underline pointer"
                              >
                                www.thequalitygroup.ae
                              </a>
                            </div>
                            <div
                              v-if="$getProperty(details, 'from') === 2"
                              class="fc-black-12 text-left"
                            >
                              <a
                                href="https://www.emirtage.com/"
                                target="_blank"
                                class="fc-black-12 no-underline pointer"
                              >
                                www.emirtage.com
                              </a>
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'class', null)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.class') }}
                            </div>
                          </el-col>
                          <el-col :span="17">
                            <div class="fc-black-12 text-left">
                              {{
                                $getProperty(
                                  fieldsMap,
                                  `class.enumMap.${details.class}`,
                                  '---'
                                )
                              }}
                            </div>
                          </el-col>
                        </el-row>
                        <el-row
                          v-if="
                            !$validation.isEmpty(
                              $getProperty(details, 'criticality', -1)
                            )
                          "
                          class="pB15"
                        >
                          <el-col :span="7">
                            <div
                              class="fc-black-11 text-left fwBold text-uppercase letter-spacing1"
                            >
                              {{ $t('quotation.common.criticality') }}
                            </div>
                          </el-col>
                          <el-col :span="17" class="flex-middle">
                            <div
                              class="dot-7 fc-priority-normal mR5"
                              v-if="$getProperty(details, 'criticality') === 2"
                            ></div>
                            <div
                              class="dot-7 fc-priority-low mR5"
                              v-if="$getProperty(details, 'criticality') === 1"
                            ></div>
                            <div
                              class="dot-7 fc-priority-high mR5"
                              v-if="$getProperty(details, 'criticality') === 3"
                            ></div>
                            <div class="fc-black-12 text-left">
                              {{
                                $getProperty(
                                  fieldsMap,
                                  `criticality.enumMap.${details.criticality}`,
                                  '---'
                                )
                              }}
                            </div>
                          </el-col>
                        </el-row>
                      </el-col>
                    </el-row>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%">
                  <div class="flex-middle fc-qfm-subject mT20">
                    <div class="fc-black-13 fwBold text-uppercase text-left">
                      {{ $t('quotation.common.subject') }} :
                    </div>
                    <div class="fc-black-14 pL5">
                      {{ details.subject }}
                    </div>
                  </div>
                </td>
              </tr>
              <tr colspan="100%">
                <div v-if="!$validation.isEmpty(details.description)">
                  <div
                    class="fc-qfm-desc fc-black-14 text-left line-height20 break-word"
                  >
                    {{ details.description }}
                  </div>
                </div>
              </tr>
              <tr>
                <td colspan="100%">
                  <LineItemsTable
                    :details="details"
                    :uomEnumMap="uomEnumMap"
                  ></LineItemsTable>
                </td>
              </tr>
              <tr>
                <td colspan="100%">
                  <div
                    class="pT20 width100 fc-qfm-pb10 fc-print-page-break-inside-avoid"
                  >
                    <div class="fwBold fc-black-14 text-left">
                      {{ $t('quotation.common.notes') }}
                    </div>
                    <div class="fc-print-pT15">
                      <div class="fc-black-14 text-left pB5">
                        {{ $t('quotation.common.notes_txt') }}
                      </div>
                      <div class="fc-black-14 text-left">
                        {{ $t('quotation.common.notes_subtxt') }}
                      </div>
                      <el-row
                        v-if="
                          !$validation.isEmpty(
                            $getProperty(details, 'validity', -1)
                          )
                        "
                        class="fc-print-pT15 flex-middle"
                      >
                        <el-col :span="4">
                          <div
                            class="fc-black-12 text-left fwBold text-uppercase"
                          >
                            {{ $t('quotation.common.validity') }}
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div class="fc-black-13 text-left">
                            {{
                              $getProperty(
                                fieldsMap,
                                `validity.enumMap.${details.validity}`,
                                '---'
                              )
                            }}
                          </div>
                        </el-col>
                      </el-row>

                      <el-row
                        v-if="
                          !$validation.isEmpty(
                            $getProperty(details, 'work_completion', -1)
                          )
                        "
                        class="fc-print-pT15 flex-middle"
                      >
                        <el-col :span="4">
                          <div
                            class="fc-black-12 text-left fwBold text-uppercase"
                          >
                            {{ $t('quotation.common.completion') }}
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div class="fc-black-13 text-left">
                            {{
                              $getProperty(
                                fieldsMap,
                                `work_completion.enumMap.${details.work_completion}`,
                                '---'
                              )
                            }}
                          </div>
                        </el-col>
                      </el-row>
                      <el-row
                        v-if="
                          !$validation.isEmpty(
                            $getProperty(details, 'payment', -1)
                          )
                        "
                        class="fc-print-pT15 flex-middle"
                      >
                        <el-col :span="4">
                          <div
                            class="fc-black-12 text-left fwBold text-uppercase"
                          >
                            {{ $t('quotation.common.payment') }}
                          </div>
                        </el-col>
                        <el-col :span="20">
                          <div class="fc-black-13 text-left">
                            {{
                              $getProperty(
                                fieldsMap,
                                `payment.enumMap.${details.payment}`,
                                '---'
                              )
                            }}
                          </div>
                        </el-col>
                      </el-row>
                      <div
                        class="fc-black-14 text-left fc-print-pT20 pT10"
                        v-if="details.notes"
                      >
                        {{ details.notes }}
                      </div>

                      <div class="fc-black-14 text-left fc-print-pT20 pT10">
                        {{ $t('quotation.common.sign_desc') }}
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <td colspan="100%">
                  <div
                    class="d-flex fc-qfm-pt20 justify-content-space border-top8 flex-direction-row fc-print-page-break-inside-avoid"
                  >
                    <div>
                      <div class="fc-black-14 text-left">
                        {{ $t('quotation.common.yours_truly') }},
                      </div>
                      <div class="fc-signature-border fc-qfm-mB20">
                        <img
                          class="qfm-sign-width"
                          :src="details.fmsignatureUrl"
                          v-if="details.fmsignatureUrl"
                        />
                      </div>
                      <div
                        class="fc-black-13 text-left fwBold line-height30 text-uppercase"
                        v-if="
                          $getProperty(details, 'fmsignatureUploadedBy.name')
                        "
                      >
                        {{
                          $getProperty(details, 'fmsignatureUploadedBy.name')
                        }}
                      </div>
                      <div
                        class="fc-black-13 text-left fwBold text-uppercase"
                        v-if="$getProperty(details, 'from') === 1"
                      >
                        {{ $t('quotation.common.qmaintenance_llc') }}
                      </div>
                      <div
                        class="fc-black-13 text-left fwBold text-uppercase"
                        v-if="$getProperty(details, 'from') === 2"
                      >
                        {{ $t('quotation.common.emirtage_llc') }}
                      </div>
                      <div
                        v-if="
                          $getProperty(
                            details,
                            'fmsignatureUploadedBy.role.name'
                          )
                        "
                        class="fc-qfm-pt10 fc-black-13 text-left text-uppercase"
                      >
                        {{
                          $getProperty(
                            details,
                            'fmsignatureUploadedBy.role.name'
                          )
                        }}
                      </div>
                      <div
                        class="fc-qfm-pt10 fc-black-13 text-left fwBold"
                        v-if="details.fmsignatureUrl"
                      >
                        {{ details.fmsignatureUploadedTime | formatDate(true) }}
                      </div>
                    </div>
                    <div>
                      <div class="fc-black-14 text-left">
                        {{ $t('quotation.common.sign_confirm') }}
                      </div>
                      <div class="">
                        <div
                          class="fc-signature-border fc-qfm-mB20"
                          v-if="details.signatureUrl"
                        >
                          <img
                            class="qfm-sign-width"
                            :src="details.signatureUrl"
                          />
                        </div>
                        <div
                          class="fc-black-13 text-left fwBold line-height30 text-uppercase"
                          v-if="
                            $getProperty(details, 'signatureUploadedBy.name')
                          "
                        >
                          {{
                            $getProperty(details, 'signatureUploadedBy.name')
                          }}
                        </div>
                        <div
                          v-if="
                            $getProperty(
                              details,
                              'signatureUploadedBy.role.name'
                            )
                          "
                          class="fc-qfm-pt10 fc-black-13 text-left text-uppercase"
                        >
                          {{
                            $getProperty(
                              details,
                              'signatureUploadedBy.role.name'
                            )
                          }}
                        </div>
                        <div
                          v-if="
                            !$validation.isEmpty(details.signatureUploadedTime)
                          "
                          class="fc-qfm-pt10 fc-black-13 text-left fwBold"
                        >
                          {{ details.signatureUploadedTime | formatDate(true) }}
                        </div>
                      </div>
                      <div
                        v-if="
                          $getProperty(details, 'contact.email', '') ===
                            'sllenas@thegateholding.com' &&
                            details.samuel_signature_quoteUrl
                        "
                      >
                        <div class="fc-signature-border fc-qfm-mB20">
                          <img
                            class="qfm-sign-width"
                            :src="details.samuel_signature_quoteUrl"
                          />
                        </div>
                        <div
                          class="fc-black-13 text-left fwBold line-height30 text-uppercase"
                          v-if="$getProperty(details, 'contact.name')"
                        >
                          {{ $getProperty(details, 'contact.name') }}
                        </div>
                        <div
                          class="fc-black-13 text-left fwBold text-uppercase"
                          v-if="$getProperty(details, 'from') === 1"
                        >
                          {{ $t('quotation.common.qmaintenance_llc') }}
                        </div>
                        <div
                          class="fc-black-13 text-left fwBold text-uppercase"
                          v-if="$getProperty(details, 'from') === 2"
                        >
                          {{ $t('quotation.common.emirtage_llc') }}
                        </div>
                        <div
                          class="fc-qfm-pt10 fc-black-13 text-left fwBold"
                          v-if="details.samuel_signature_quoteUploadedTime"
                        >
                          {{
                            details.samuel_signature_quoteUploadedTime
                              | formatDate(true)
                          }}
                        </div>
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
            <tfoot class="fc-page-footer">
              <tr>
                <td style="border: none !important;">
                  <div class="page-footer-space" style="height: 40px;"></div>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
      <!-- footer -->
      <div class="fc-qfm-print-footer">
        <div>
          <div class="d-flex flex-direction-row quote-id-footer">
            <div
              class="fc-black-9"
              v-if="!$validation.isEmpty(details.quoteid)"
            >
              {{ details.quoteid }}
            </div>
          </div>
        </div>
        <div class="fwBold fc-qfm-footer-txt quote-id-footer">
          TEL: 04 396 5955, FAX: 04 396 8115, P.O. BOX: 26187, DUBAI - UAE
        </div>
        <div></div>
      </div>
    </template>
  </div>
</template>
<script>
import QuotationMixin from 'src/pages/quotation/v1/mixins/QuotationMixin'
import LineItemsTable from 'src/pages/quotation/components/LineItemsSummaryTable'
export default {
  components: {
    LineItemsTable,
  },
  mixins: [QuotationMixin],
}
</script>
<style lang="scss">
.fc-qfm-print-footer {
  display: none;
}

.fc-print-pT15 {
  padding-top: 10px;
}

.fc-quotation-con {
  .qfm-sign-width {
    width: 250px;
    height: 50px;
    object-fit: scale-down;
  }
}

.fc-print-pT20 {
  padding-top: 20px;
}

@media print {
  @page {
    counter-increment: page;
    counter-reset: page 1;
    @top-right {
      content: 'Page ' counter(page) ' of ' counter(pages);
    }
  }
  .fc-print-pT15,
  .fc-print-pT20 {
    padding-top: 10px !important;
  }
}
</style>
