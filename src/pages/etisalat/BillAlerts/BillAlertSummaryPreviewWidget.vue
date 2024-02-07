<template>
  <div class="emptypagewidget">
    <div ref="preview-container">
      <div class="white-background p20 page-widget-shadow">
        <el-row class="flex-middle flex-align-start">
          <el-col :span="4" v-if="utilityBill">
            <div class="fc-invoice-bill-block">
              <a :href="utilityBill.data.documentUrl" target="_blank">
                <div class="fc-invoice-bill-img-hover">
                  <div class="fc-invoice-img-center">
                    <img
                      v-if="utilityBill"
                      :src="utilityBill.photoUrl"
                      class="fc-invoice-bill-img"
                    />
                    <div
                      class="fc-white-12 text-uppercase bold bill-preview-txt"
                    >
                      <div>
                        <i class="el-icon-view f18"></i>
                      </div>
                      <div>Preview</div>
                    </div>
                  </div>
                </div>
              </a>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-if="getFieldData('picklist', true) === 9"
          >
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_11'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('decimal_11') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('lookup_1') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields border-none">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['picklist_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ Disputed_Utility[getFieldData('picklist_3')] }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL30 mR10"
            v-else-if="getFieldData('picklist', true) === 'Budget exceeded'"
          >
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Current Year Budget</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">---</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Total Spend from Bills</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">---</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 8"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['picklist_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ Disputed_Utility[getFieldData('picklist_3')] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_4'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('decimal_4') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_5'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('decimal_5') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_6'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('decimal_6') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 7"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['picklist_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ Disputed_Utility[getFieldData('picklist_3')] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('decimal') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ 'Expected Consumption Range' }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{
                      `${
                        getFieldData('decimal_7')
                          ? getFieldData('decimal_7') + ' kWh'
                          : '---'
                      } - ${
                        getFieldData('decimal_8')
                          ? getFieldData('decimal_8') + ' kWh'
                          : '---'
                      }`
                    }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('decimal_2') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('decimal_3') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 6"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ 'Associated utilities' }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ associatedUtility || '---' }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['picklist_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ Disputed_Utility[getFieldData('picklist_3')] }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 5"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['picklist_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ Disputed_Utility[getFieldData('picklist_3')] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_5'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('decimal_5') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">{{ 'Average Bill Cost' }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('decimal_4') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['decimal_6'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('decimal_6') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 4"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['picklist_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ Disputed_Utility[getFieldData('picklist_3')] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields border-none">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 3"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Account Number Status</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">Terminated</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_4'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_4') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields border-none">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 2"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    Invalid {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields border-none">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-if="getFieldData('picklist', true) === 1"
          >
            <div class="fc-invoice-fields pT0 ">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields border-none">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col
            :span="24"
            class="mL20 mR10"
            v-else-if="getFieldData('picklist', true) === 10"
          >
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['singleline_2'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('singleline_2') }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">Supplier</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">
                    {{ getFieldData('lookup_1') }}
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="fc-invoice-fields">
              <el-row>
                <el-col :span="6">
                  <div class="field-label bold">Site</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('siteId') }}</div>
                </el-col>
                <el-col :span="6">
                  <div class="field-label bold">
                    {{ fieldDisplayNames['date_3'] }}
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="field-value">{{ getFieldData('date_3') }}</div>
                </el-col>
              </el-row>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="white-background mT20 page-widget-shadow" v-if="tableShow">
        <table
          class="fc-etislat-tariff-table mT10"
          v-if="getFieldData('picklist', true) === 9"
        >
          <thead>
            <th style="width: 17%;">Item</th>
            <th style="width: 17%;">Charges</th>
          </thead>
          <tbody>
            <tr v-for="(value, key) in getFieldData('multiline2')" :key="key">
              <td>{{ key }}</td>
              <td>{{ value ? `${formatCurrency(value)} AED` : '0 AED' }}</td>
            </tr>
            <tr>
              <td class="bold">Total</td>
              <td class="bold">{{ getFieldData('totalcharges') }}</td>
            </tr>
          </tbody>
        </table>
        <table
          class="fc-etislat-tariff-table mT10"
          v-else-if="getFieldData('picklist', true) === 8"
        >
          <thead>
            <th style="width: 17%;">Slab</th>
            <th style="width: 17%;">Consumption</th>
            <th style="width: 17%;">Unit Price</th>
            <th style="width: 17%;">Cost</th>
          </thead>
          <tbody>
            <tr v-for="(row, index) in getFieldData('multiline')" :key="index">
              <template v-if="row.consumption">
                <td>{{ row.name }}</td>
                <td>{{ row.consumption }} kWh</td>
                <td>{{ row.price }} AED</td>
                <td>{{ row.amount }} AED</td>
              </template>
            </tr>
            <tr>
              <td colspan="3" class="white-background bold">Expected Total</td>
              <td class="white-background bold">
                {{ getFieldData('decimal_4') }}
              </td>
            </tr>
            <tr>
              <td colspan="3" class="white-background bold">
                Actual Bill Total
              </td>
              <td class="white-background bold">
                {{ getFieldData('decimal_5') }}
              </td>
            </tr>
          </tbody>
        </table>
        <table
          class="fc-etislat-tariff-table mT10"
          v-else-if="getFieldData('picklist', true) === 7"
        >
          <tbody>
            <tr>
              <td class="width40">Manually Recorded Consumption</td>
              <td class="width40">{{ getFieldData('decimal') }}</td>
            </tr>
            <tr>
              <td class="width40">Consumption in Bill</td>
              <td class="width40">{{ getFieldData('decimal_2') }}</td>
            </tr>
            <tr>
              <td class="width40">Expected Consumption Range (±10%)</td>
              <td class="width40">
                {{
                  `${
                    getFieldData('decimal_7')
                      ? getFieldData('decimal_7') + ' kWh'
                      : '---'
                  } - ${
                    getFieldData('decimal_8')
                      ? getFieldData('decimal_8') + ' kWh'
                      : '---'
                  }`
                }}
              </td>
            </tr>
            <tr>
              <td class="width40">Disputed Consumption</td>
              <td class="width40">{{ getFieldData('decimal_3') }}</td>
            </tr>
          </tbody>
        </table>
        <table
          class="fc-etislat-tariff-table mT10"
          v-else-if="getFieldData('picklist', true) === 6"
        >
          <tbody>
            <tr>
              <td>Disputed Utility</td>
              <td>{{ Disputed_Utility[getFieldData('picklist_3')] }}</td>
            </tr>
            <tr>
              <td>Actual Mapped Utility</td>
              <td>{{ associatedUtility || '---' }}</td>
            </tr>
            <tr>
              <td>Disputed utility cost in Bill</td>
              <td>
                {{ getFieldData('decimal_9') }}
              </td>
            </tr>
            <tr>
              <td>Bill Total</td>
              <td>{{ getFieldData('decimal_10') }}</td>
            </tr>
          </tbody>
        </table>
        <table
          class="fc-etislat-tariff-table mT10"
          v-else-if="getFieldData('picklist', true) === 5"
        >
          <thead>
            <th
              style="width: 17%;"
              v-for="(value, index) in getSortedData(
                getFieldData('multiline2')
              )"
              :key="index"
            >
              {{ value.time ? getDate(value.time, 'MMM YYYY') : '' }}
            </th>
          </thead>
          <tbody>
            <tr>
              <td
                v-for="(value, index) in getSortedData(
                  getFieldData('multiline2')
                )"
                :key="index"
              >
                {{
                  value && value.value
                    ? `${formatCurrency(value.value)} AED`
                    : '0 AED'
                }}
              </td>
            </tr>
            <tr>
              <td
                class="white-background bold"
                :colspan="
                  Object.keys(getFieldData('multiline2')).length > 2
                    ? Object.keys(getFieldData('multiline2')).length - 1
                    : 1
                "
              >
                Average Bill Value
              </td>
              <td class="white-background bold">
                {{ getFieldData('decimal_4') }}
              </td>
            </tr>
            <tr>
              <td
                class="white-background bold"
                :colspan="
                  Object.keys(getFieldData('multiline2')).length > 2
                    ? Object.keys(getFieldData('multiline2')).length - 1
                    : 1
                "
              >
                Expected Bill Value (±20%)
              </td>
              <td class="white-background bold">
                {{
                  `${
                    getFieldData('decimal_7')
                      ? formatCurrency(getFieldData('decimal_7')) + ' AED'
                      : '0 AED'
                  } - ${
                    getFieldData('decimal_8')
                      ? formatCurrency(getFieldData('decimal_8')) + ' AED'
                      : '0 AED'
                  }`
                }}
              </td>
            </tr>
            <tr>
              <td
                class="white-background bold"
                :colspan="
                  Object.keys(getFieldData('multiline2')).length > 2
                    ? Object.keys(getFieldData('multiline2')).length - 1
                    : 1
                "
              >
                Current Bill Value
              </td>
              <td class="white-background bold">
                {{ getFieldData('decimal_5') }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { formatCurrency } from 'charts/helpers/formatter'
export default {
  props: ['details', 'moduleName', 'calculateDimensions', 'resizeWidget'],
  data() {
    return {
      outerVisible: false,
      supplier: null,
      initialWidgetHeight: null,
      utilityBill: null,
      utilityConnections: null,
      associatedUtility: '---',
      Disputed_Utility: {
        1: 'Energy',
        2: 'Water',
        3: 'Gas',
        4: 'Sewage',
      },
      alertType: {
        1: 'Bill missing',
        2: 'Account In bill does not exist',
        3: 'Bill generated for terminated account',
        4: 'Zero consumption',
        5: 'Abnormal consumption',
        6: 'Wrong utility billed',
        7: 'Higher than meter Reading',
        8: 'Difference in tariff',
        9: 'Extra charges present in bill',
        10: 'Duplicate Bill',
      },
    }
  },
  mounted() {
    if (
      this.data &&
      this.data.utilitybill_1 &&
      this.data.utilitybill_1.data &&
      this.data.utilitybill_1.data.supplier &&
      this.data.utilitybill_1.data.supplier.id
    ) {
      this.loadsupplier()
    }
    if (
      this.data &&
      this.data.singleline_2 &&
      this.getFieldData('picklist', true) === 6
    ) {
      this.loadUtilityConnection()
    }

    if (
      this.data &&
      this.data.utilitybill_1 &&
      this.data.utilitybill_1.data &&
      this.data.utilitybill_1.data.bill_document &&
      this.data.utilitybill_1.data.bill_document.id
    ) {
      this.loadBill()
    }

    setTimeout(() => {
      this.autoResize()
    }, 500)
  },
  watch: {
    details() {
      this.$nextTick(this.autoResize)
    },
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
      sites: state => state.sites,
    }),
    fields() {
      return this.moduleMeta.fields || []
    },
    fieldDisplayNames() {
      if (this.moduleMeta && this.moduleMeta.fields) {
        let fieldMap = {}
        this.moduleMeta.fields.forEach(field => {
          this.$set(fieldMap, field.name, field.displayName)
        })
        return fieldMap
      }
      return []
    },
    data() {
      return this.details.data || null
    },
    currentView() {
      let { $route } = this

      if ($route.params.viewName) {
        return $route.params.viewName
      } else {
        return null
      }
    },
    tableShow() {
      let alertType = this.getFieldData('picklist', true)
      let alertShowTables = [9, 8, 7, 6, 5]
      if (alertShowTables.includes(alertType)) {
        return true
      }
      return false
    },
    utilityAccount() {
      if (
        this.data.utilitybill_1 &&
        this.data.utilitybill_1.data &&
        this.data.utilitybill_1.data.utility_account &&
        this.data.utilitybill_1.data.utility_account.id
      ) {
        return this.data.utilitybill_1.data.utility_account.id
      }
      return null
    },
  },
  methods: {
    getSortedData(data) {
      if (data) {
        let timeStamp = Object.keys(data)
        let sortedData = []
        timeStamp.forEach(rt => {
          let d = {
            time: Number(rt),
            value: data[rt],
          }
          sortedData.push(d)
        })
        return sortedData.sort((a, b) => a.time - b.time)
      }
      return data
    },
    getDate(time, formatter) {
      return moment(Number(time))
        .tz(this.$timezone)
        .format(formatter)
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['preview-container']
        // if (container) {
        //   let height = container.scrollHeight + 70
        //   let width = container.scrollWidth
        //   this.resizeWidget({ height, width })
        // }
        if (!container) return

        let height = container.scrollHeight
        let width = container.scrollWidth

        let { h, w } = this.calculateDimensions({ height, width })
        console.log('dimentions', h, w, height, width)
        let params = {}
        params = { h: h }
        this.resizeWidget(params)
      })
    },
    loadsupplier() {
      let id = this.data.utilitybill_1.data.supplier.id
      this.$http.get(`v2/vendors/${id}`).then(response => {
        if (response.data.responseCode === 0) {
          this.supplier = response.data.result.vendor
        }
      })
    },
    loadBill() {
      let id = this.data.utilitybill_1.data.bill_document.id
      this.$http
        .get(`/v2/module/data/${id}?moduleName=custom_billdocuments`)
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleData
          ) {
            this.utilityBill = response.data.result.moduleData
          }
        })
    },
    loadUtilityConnection() {
      let accountNumber = this.data.singleline_2
      this.$http
        .get(
          `/v2/module/data/list?moduleName=custom_utilityconnection&page=1&perPage=500&filters=%7B%22accountdetails%22%3A%7B%22operatorId%22%3A36%2C%22value%22%3A%5B%22${this.utilityAccount}%22%5D%2C%22selectedLabel%22%3A%22${accountNumber}%22%7D%7D&viewName=all&includeParentFilter=true`
        )
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleDatas
          ) {
            this.utilityConnections = response.data.result.moduleDatas
            this.associatedUtility = ''
            let utility = {}
            this.utilityConnections.forEach(rt => {
              if (rt.data && rt.data.picklist) {
                if (rt.data.picklist === 1) {
                  this.$set(utility, 'Energy', 1)
                } else if (rt.data.picklist === 2) {
                  this.$set(utility, 'Water', 1)
                } else if (rt.data.picklist === 3) {
                  this.$set(utility, 'Gas', 1)
                } else if (rt.data.picklist === 4) {
                  this.$set(utility, 'Sewage', 1)
                }
              }
            })
            Object.keys(utility).forEach(rt => {
              if (rt === 'Sewage') {
                this.associatedUtility += rt
              } else {
                this.associatedUtility += rt + ','
              }
            })
          }
        })
    },
    formatCurrency(data) {
      return formatCurrency(data, 2)
    },
    getFieldData(fieldName, custome) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'picklist' && custome) {
          return this.data['picklist']
        } else if (fieldName === 'picklist') {
          let { enumMap } = fieldobj
          return enumMap[this.data['picklist']]
        } else if (fieldName === 'date_1') {
          return this.data.date_1
            ? moment(this.data.date_1)
                .tz(this.$timezone)
                .format('DD MMM YYYY')
            : '---'
        } else if (fieldName === 'date_3') {
          return this.data.date_3
            ? moment(this.data.date_3)
                .tz(this.$timezone)
                .format('MMM YYYY')
            : '---'
        } else if (fieldName === 'date_4') {
          return this.data.date_4
            ? moment(this.data.date_4)
                .tz(this.$timezone)
                .format('MMM YYYY')
            : '---'
        } else if (fieldName === 'number') {
          return this.data.number || '---'
        } else if (fieldName === 'decimal') {
          return this.data.decimal ? `${this.data.decimal} kWh` : '---'
        } else if (fieldName === 'decimal_1') {
          return this.data.decimal_1 || '---  '
        } else if (fieldName === 'decimal_2') {
          return this.data.decimal_2 ? `${this.data.decimal_2} kWh` : '---'
        } else if (fieldName === 'decimal_3') {
          return this.data.decimal_3 ? `${this.data.decimal_3} kWh` : '---'
        } else if (fieldName === 'decimal_4') {
          return this.data.decimal_4
            ? `${this.formatCurrency(this.data.decimal_4)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_5') {
          return this.data.decimal_5
            ? `${this.formatCurrency(this.data.decimal_5)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_6') {
          return this.data.decimal_6
            ? `${this.formatCurrency(this.data.decimal_6)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_7') {
          return this.data.decimal_7 || null
        } else if (fieldName === 'decimal_8') {
          return this.data.decimal_8 || null
        } else if (fieldName === 'decimal_9') {
          return this.data.decimal_9
            ? `${this.formatCurrency(this.data.decimal_9)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_10') {
          return this.data.decimal_10
            ? `${this.formatCurrency(this.data.decimal_10)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_11') {
          return this.data.decimal_11
            ? `${this.formatCurrency(this.data.decimal_11)} AED`
            : '0 AED'
        } else if (fieldName === 'decimal_12') {
          return this.data.decimal_12
            ? `${this.formatCurrency(this.data.decimal_12)} AED`
            : '0 AED'
        } else if (fieldName === 'totalcharges') {
          let multiline = this.data.multiline
            ? JSON.parse(this.data.multiline)
            : {}
          let val = 0
          Object.values(multiline).forEach(rt => {
            if (rt) {
              val = val + Number(rt)
            }
          })
          return `${this.formatCurrency(val)} AED`
        } else if (fieldName === 'decimal_1') {
          return this.data.decimal_1 || '---'
        } else if (fieldName === 'picklist_3') {
          return this.data.picklist_3 || '---'
        } else if (fieldName === 'singleline_2') {
          return this.data.singleline_2 || '---'
        } else if (fieldName === 'siteId') {
          if (this.sites) {
            let site = this.sites.find(rt => rt.id === this.details.siteId)
            return site ? site.name : '---'
          }
          return '---'
        } else if (fieldName === 'number_1') {
          return this.data.number_1 || '---'
        } else if (fieldName === 'multiline') {
          return this.data.multiline ? JSON.parse(this.data.multiline) : []
        } else if (fieldName === 'multiline2') {
          return this.data.multiline ? JSON.parse(this.data.multiline) : {}
        } else if (fieldName === 'date') {
          return moment(this.data.date)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'photoUrl' && this.utilityBill) {
          return this.utilityBill.photoUrl || null
        } else if (fieldName === 'lookup_1') {
          return this.data.lookup_1 ? this.data.lookup_1.name : '---'
        }
      }
      return ''
    },
  },
}
</script>
<style>
.emptypagewidget {
  background: #f7f8f9;
}
</style>
