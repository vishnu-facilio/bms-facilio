<template>
  <div>
    <el-dialog
      title="Tips"
      :visible="true"
      width="70%"
      :before-close="closeDialog"
      custom-class="fc-dialog-right setup-dialog fc-utility-dialog slideInRight fc-animated"
      :modalAppendToBody="false"
    >
      <div v-if="loading" class="mT50 fc-webform-loading">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else class="position-relative">
        <div class="fc-dialog-sticky-header">
          <div class="flex-center-row-space pT30 pB30 pL20 pR20">
            <div class>
              <div class="fc-black2-18">
                {{ details && details.name ? details.name : '' }}
              </div>
              <div class="flex-middle pT8">
                <div class="fc-black-13 text-left">
                  {{
                    details && details.data ? details.data.supplier.name : ''
                  }}
                </div>
                <el-divider direction="vertical"></el-divider>
                <div class="fc-black-13 text-left">
                  {{ getFieldData('singleline_2') }}
                </div>
                <el-divider direction="vertical"></el-divider>
                <div class="fc-red4-13 fw4">
                  {{
                    details && details.data
                      ? $getProperty(details, 'moduleState.displayName')
                      : ''
                  }}
                </div>
              </div>
            </div>
            <div class="flex-middle">
              <!-- <el-button class="fc-btn-green-lg-fill">Re-Parse Bill</el-button> -->
              <i
                class="el-icon-close fc-black2-20 mL20 f30 pointer"
                @click="closeDialog"
              ></i>
            </div>
          </div>
        </div>
        <div class="fc-billsummary-body-scroll">
          <!-- first widget -->
          <div class>
            <el-row class="flex-middle white-bg-block">
              <el-col
                :span="4"
                v-if="
                  details &&
                    details.data &&
                    details.data.bill_document &&
                    details.data.bill_document.data &&
                    details.data.bill_document.photoUrl
                "
              >
                <div
                  class="fc-invoice-bill-block fc-utility-bill-preview"
                  style="overflow: inherit;"
                >
                  <div
                    class="fc-invoice-bill-img-hover"
                    style="display: block;"
                  >
                    <div class="fc-invoice-img-center">
                      <a
                        :href="details.data.bill_document.data.documentUrl"
                        target="_blank"
                      >
                        <img
                          :src="details.data.bill_document.photoUrl"
                          class="fc-invoice-bill-img"
                        />
                        <div
                          class="fc-white-12 text-uppercase bold fc-preview-text"
                        >
                          <div>
                            <i class="el-icon-view f18"></i>
                          </div>
                          <div class="fw6">Preview</div>
                        </div>
                      </a>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :span="20" class="pL30">
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['date_5'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('date_5') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['date_1'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('date_1') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['date_2'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('date_2') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">Last Receipt</div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('date_4') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-invoice-fields border-none">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['date_3'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('date_3') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['date'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('date') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </el-col>
            </el-row>

            <!-- second row -->
            <el-row class="flex-middle white-bg-block mT20">
              <el-col :span="24" class="pL20 pR20">
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">Site</div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{
                          details && details.siteId
                            ? getSiteName(details.siteId)
                            : '---'
                        }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        Bill Template
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{
                          details &&
                          details.data &&
                          details.data.bill_template &&
                          details.data.bill_template.name
                            ? details.data.bill_template.name
                            : '---'
                        }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col
                      :span="6"
                      v-if="
                        details && details.data && details.data.singleline_3
                      "
                    >
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['singleline_3'] }}
                      </div>
                    </el-col>
                    <el-col
                      :span="6"
                      v-if="
                        details && details.data && details.data.singleline_3
                      "
                    >
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('singleline_3') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        Bill Document
                      </div>
                    </el-col>
                    <el-col
                      :span="6"
                      v-if="
                        details && details.data && details.data.bill_document
                      "
                    >
                      <div class="fc-black-13 text-left">
                        <a
                          v-if="
                            details.data.bill_document.data &&
                              details.data.bill_document.data.documentUrl
                          "
                          :href="details.data.bill_document.data.documentUrl"
                          target="_blank"
                          class="fc-black-13"
                        >
                          {{
                            details.data &&
                            details.data.bill_document &&
                            details.data.bill_document.name
                              ? details.data.bill_document.name
                              : '---'
                          }}
                        </a>
                        <div v-else>
                          ---
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-invoice-fields border-none">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['singleline_1'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('singleline_1') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['singleline_2'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('singleline_2') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </el-col>
            </el-row>
            <!-- cost -->
            <div class="fc-blue-label pT20 pB20">Cost</div>
            <el-row class="flex-middle white-bg-block">
              <!-- new one -->
              <el-col :span="24" class="pL20 pR20">
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_1'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_1') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- second row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['number'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('number') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_6'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_6') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- third row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['number_3'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('number_3') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_4'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_4') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- fourth row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_2'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_2') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_5'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_5') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- fifth row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_3'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_3') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_12'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_12') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- sixth row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_13'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_13') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_7'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_7') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- seventh row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_9'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_9') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_8'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_8') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- eight row -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_10'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_10') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_11'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_11') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- nineth -->
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_14'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_14') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['decimal_15'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('decimal_15') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- tenth row -->
              </el-col>
            </el-row>
            <!-- payments -->
            <div class="fc-blue-label pT20 pB20">Payment Memo</div>
            <el-row class="flex-middle white-bg-block">
              <el-col :span="24" class="pL20 pR20">
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ fieldDisplayNames['number_2'] }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('number_2') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ 'Arrear Payment Memo' }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('arrearinvoice', 'id') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-invoice-fields">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ 'Payment Memo' }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('invoice', 'id') }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        {{ 'Arrear Payment Memo Status' }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('arrearinvoice', 'status') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-invoice-fields border-none">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left bold">
                        Payment Memo Status
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-black-13 text-left">
                        {{ getFieldData('invoice', 'status') }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </el-col>
            </el-row>
            <!-- electricity -->
            <div v-for="(lineItem, index) in lineItems" :key="index">
              <div class="fc-blue-label pT20 pB20">{{ lineItem.name }}</div>
              <div class="white-bg-block">
                <div
                  class="fc-black-14 pT10 pL20 pR20 text-left border-bottom3 bold pB10"
                >
                  Meter Details
                </div>
                <el-row class="flex-middle pL20 pR20">
                  <el-col :span="24" class>
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Meter Number
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'singleline') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Previous Reading
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_1') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Tariff - Account Number
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'number_2') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Current Reading
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_2') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="fc-invoice-fields border-none">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Previous Reading Date
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'date') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Multiplication Factor
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_13') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <!-- cost -->
              <div class="white-bg-block mT20">
                <div
                  class="fc-black-14 pT10 pL20 pR20 pB10 text-left border-bottom3 bold"
                >
                  Cost
                </div>
                <el-row class="flex-middle pL20 pR20">
                  <el-col :span="24" class>
                    <!-- first row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">Slab 1</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_14') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">Slab 2</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_15') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- second row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">Slab 3</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_3') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">Slab 4</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_16') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- third row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Adjustments
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_6') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Meter Charges
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_22') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- fourth row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Discounts
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_10') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Fuel Surcharge
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_12') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- fifth row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Transformer Charges
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_17') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Service Charges
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_9') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- sixth row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Vat on other charges
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_23') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">VAT</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_11') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- seventh row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Current Amount
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_5') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">Arrears</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_4') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- eight row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">Receipts</div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_7') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Total Amount
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- nineth row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Total Amount Due
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_24') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Sys Non-taxable Amount
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_19') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- tenth row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Sys Taxable Amount
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_18') }}
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Sys Vat on Taxable Amount
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_20') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- elventh row -->
                    <div class="fc-invoice-fields">
                      <el-row>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left bold">
                            Sys Total
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="fc-black-13 text-left">
                            {{ getLineFieldData(lineItem, 'decimal_21') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import { formatCurrency } from 'charts/helpers/formatter'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['summary', 'viewname'],
  data() {
    return {
      loading: false,
      fieldObject: null,
      detail: null,
      lineItems: null,
    }
  },
  mounted() {
    this.getSummaryData()
  },
  computed: {
    fields() {
      if (this.fieldObject) {
        return this.fieldObject.fields
      }
      return []
    },
    fieldDisplayNames() {
      if (this.fields) {
        let fieldMap = {}
        this.fields.forEach(field => {
          this.$set(fieldMap, field.name, field.displayName)
        })
        return fieldMap
      }
      return {}
    },
    data() {
      return this.details.data || {}
    },
    details() {
      if (this.detail) {
        return this.detail
      }
      return this.summary
    },
  },
  methods: {
    getLineFieldData(item, fieldName) {
      if (fieldName && item && item.data) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        let lineItem = item.data
        if (fieldName === 'singleline') {
          return lineItem.singleline ? lineItem.singleline : '---'
        } else if (fieldName === 'decimal_1') {
          return lineItem.decimal_1 ? `${lineItem.decimal_1} kWh` : '---'
        } else if (fieldName === 'decimal_2') {
          return lineItem.decimal_2 ? `${lineItem.decimal_2} kWh` : '---'
        } else if (fieldName === 'number_2') {
          return lineItem.number_2 ? `${lineItem.number_2}` : '---'
        } else if (fieldName === 'date') {
          return lineItem.date
            ? moment(lineItem.date)
                .tz(this.$timezone)
                .format('DD MMM YYYY')
            : '---'
        } else if (fieldName === 'date') {
          return lineItem.date
            ? moment(lineItem.date)
                .tz(this.$timezone)
                .format('DD MMM YYYY')
            : '---'
        } else if (fieldName === 'decimal_13') {
          return lineItem.decimal_13
            ? `${this.formatCurrency(lineItem.decimal_13)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_14') {
          return lineItem.decimal_14
            ? `${this.formatCurrency(lineItem.decimal_14)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_15') {
          return lineItem.decimal_15
            ? `${this.formatCurrency(lineItem.decimal_15)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_16') {
          return lineItem.decimal_16
            ? `${this.formatCurrency(lineItem.decimal_16)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_3') {
          return lineItem.decimal_3
            ? `${this.formatCurrency(lineItem.decimal_3)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_18') {
          return lineItem.decimal_18
            ? `${this.formatCurrency(lineItem.decimal_18)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_19') {
          return lineItem.decimal_19
            ? `${this.formatCurrency(lineItem.decimal_19)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_20') {
          return lineItem.decimal_20
            ? `${this.formatCurrency(lineItem.decimal_20)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_6') {
          return lineItem.decimal_6
            ? `${this.formatCurrency(lineItem.decimal_6)} AED`
            : '0 AED'
        } else if (fieldName === 'number_3') {
          return lineItem.number_3
            ? `${this.formatCurrency(lineItem.number_3)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_5') {
          return lineItem.decimal_5
            ? `${this.formatCurrency(lineItem.decimal_5)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_4') {
          return lineItem.decimal_4
            ? `${this.formatCurrency(lineItem.decimal_4)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal') {
          return lineItem.decimal
            ? `${this.formatCurrency(lineItem.decimal)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_7') {
          return lineItem.decimal_7
            ? `${this.formatCurrency(lineItem.decimal_7)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_9') {
          return lineItem.decimal_9
            ? `${this.formatCurrency(lineItem.decimal_9)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_8') {
          return lineItem.decimal_8
            ? `${this.formatCurrency(lineItem.decimal_8)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_10') {
          return lineItem.decimal_10
            ? `${this.formatCurrency(lineItem.decimal_10)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_11') {
          return lineItem.decimal_11
            ? `${this.formatCurrency(lineItem.decimal_11)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_22') {
          return lineItem.decimal_22
            ? `${this.formatCurrency(lineItem.decimal_22)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_12') {
          return lineItem.decimal_12
            ? `${this.formatCurrency(lineItem.decimal_12)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_17') {
          return lineItem.decimal_17
            ? `${this.formatCurrency(lineItem.decimal_17)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_9') {
          return lineItem.decimal_9
            ? `${this.formatCurrency(lineItem.decimal_9)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_8') {
          return lineItem.decimal_8
            ? `${this.formatCurrency(lineItem.decimal_8)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_23') {
          return lineItem.decimal_23
            ? `${this.formatCurrency(lineItem.decimal_23)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_23') {
          return lineItem.decimal_23
            ? `${this.formatCurrency(lineItem.decimal_23)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_24') {
          return lineItem.decimal_24
            ? `${this.formatCurrency(lineItem.decimal_24)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_21') {
          return lineItem.decimal_21
            ? `${this.formatCurrency(lineItem.decimal_21)} AED`
            : '0 AED'
        }
      }
      return ''
    },
    formatCurrency(data) {
      return formatCurrency(data, 2)
    },
    loadutilityLineItem() {
      let filters = `{"utilitybill":{"operatorId":36,"value":["${this.details.id}"],"selectedLabel":"${this.details.name}"}}`
      this.$http
        .get(
          `v2/module/data/list?moduleName=custom_utilitybilllineitems&page=1&perPage=50&filters=${encodeURIComponent(
            filters
          )}&viewName=all&includeParentFilter=true`
        )
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleDatas
          ) {
            this.lineItems = response.data.result.moduleDatas
          }
        })
    },
    getSummaryData() {
      this.loading = true
      let { id } = this.summary
      this.$http
        .get(`/v2/module/data/${id}?moduleName=custom_utilitybills`)
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleData
          ) {
            this.detail = response.data.result.moduleData
          }
          this.getFields()
        })
    },
    getFields() {
      this.$http
        .get('/module/meta?moduleName=custom_utilitybills')
        .then(response => {
          if (response.data.meta) {
            this.fieldObject = response.data.meta
          }
          this.loadutilityLineItem()
          this.loading = false
        })
    },
    closeDialog() {
      if (isWebTabsEnabled()) {
        let { viewname, $route } = this
        let { name } = findRouteForModule('custom_utilitybills', pageTypes.LIST)

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: $route.query || {},
          })
        }
      }
      this.$emit('onClose')
    },
    getSiteName(siteId) {
      let site = (this.sites || []).find(({ id }) => id === siteId)
      return site ? site.name : '---'
    },
    getFieldData(fieldName, field2) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'date_5') {
          return moment(this.data.date_5)
            .tz(this.$timezone)
            .format('MMM YYYY')
        } else if (fieldName === 'date_1') {
          return moment(this.data.date_1)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'date_2') {
          return moment(this.data.date_2)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'date_3') {
          return moment(this.data.date_3)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'date_4') {
          return moment(this.data.date_4)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'date') {
          return moment(this.data.date)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'decimal_5') {
          return this.data.decimal_5
            ? `${this.formatCurrency(this.data.decimal_5)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_2') {
          return this.data.decimal_2
            ? `${this.formatCurrency(this.data.decimal_2)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_1') {
          return this.data.decimal_1
            ? `${this.formatCurrency(this.data.decimal_1)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_8') {
          return this.data.decimal_8
            ? `${this.formatCurrency(this.data.decimal_8)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_10') {
          return this.data.decimal_10
            ? `${this.formatCurrency(this.data.decimal_10)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_1') {
          return this.data.decimal_1
            ? `${this.formatCurrency(this.data.decimal_1)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_4') {
          return this.data.decimal_4
            ? `${this.formatCurrency(this.data.decimal_4)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_3') {
          return this.data.decimal_3
            ? `${this.formatCurrency(this.data.decimal_3)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_14') {
          return this.data.decimal_14
            ? `${this.formatCurrency(this.data.decimal_14)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_11') {
          return this.data.decimal_11
            ? `${this.formatCurrency(this.data.decimal_11)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_9') {
          return this.data.decimal_9
            ? `${this.formatCurrency(this.data.decimal_9)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_6') {
          return this.data.decimal_6
            ? `${this.formatCurrency(this.data.decimal_6)} AED`
            : '0 AED'
        } else if (fieldName === 'number_3') {
          return this.data.number_3
            ? `${this.formatCurrency(this.data.number_3)} AED`
            : '0 AED'
        } else if (fieldName === 'picklist_1') {
          let { enumMap } = fieldobj
          return this.data.picklist_1 ? enumMap[this.data['picklist_1']] : '---'
        } else if (fieldName === 'decimal_7') {
          return this.data.decimal_7
            ? `${this.formatCurrency(this.data.decimal_7)} AED`
            : '0 AED'
        } else if (fieldName === 'singleline_2') {
          return this.data.singleline_2 ? this.data.singleline_2 : '---'
        } else if (fieldName === 'singleline_1') {
          return this.data.singleline_1 ? this.data.singleline_1 : '---'
        } else if (fieldName === 'singleline_3') {
          return this.data.singleline_3 ? this.data.singleline_3 : '---'
        } else if (fieldName === 'decimal_12') {
          return this.data.decimal_12 ? this.data.decimal_12 : '---'
        } else if (fieldName === 'decimal_13') {
          return this.data.decimal_13
            ? `${this.formatCurrency(this.data.decimal_13)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_15') {
          return this.data.decimal_15 ? this.data.decimal_15 : '---'
        } else if (fieldName === 'decimal') {
          return this.data.decimal
            ? `${this.formatCurrency(this.data.decimal)} AED`
            : '0 AED'
        } else if (fieldName === 'singleline') {
          return this.data.singleline ? this.data.singleline : '---'
        } else if (fieldName === 'number_2') {
          return this.data.number_2 ? this.data.number_2 : '---'
        } else if (fieldName === 'number') {
          return this.data.number
            ? `${this.formatCurrency(this.data.number)} AED`
            : '0 AED'
        } else if (fieldName === 'picklist') {
          let { enumMap } = fieldobj
          return this.data.picklist ? enumMap[this.data['picklist']] : '---'
        } else if (fieldName === 'arrearinvoice' && field2 === 'id') {
          return this.data.arrearinvoice && this.data.arrearinvoice.id
            ? this.data.arrearinvoice.id
            : '---'
        } else if (fieldName === 'arrearinvoice' && field2 === 'status') {
          return this.data.arrearinvoice && this.data.arrearinvoice.moduleState
            ? this.$getProperty(
                this.data.arrearinvoice,
                'moduleState.displayName'
              )
            : '---'
        } else if (fieldName === 'invoice' && field2 === 'id') {
          return this.data.invoice && this.data.invoice.id
            ? this.data.invoice.id
            : '---'
        } else if (fieldName === 'invoice' && field2 === 'status') {
          return this.data.invoice && this.data.invoice.moduleState
            ? this.$getProperty(this.data.invoice, 'moduleState.displayName')
            : '---'
        }
        return ''
      }
    },
  },
}
</script>
