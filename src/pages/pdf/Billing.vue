<template>
  <div>
    <div class="pdf-container">
      <div class="pdf-inner-container">
        <div class="logo-container">
          <div class="fL">
            <div class="org-logo" v-show="tenant.logoUrl">
              <img
                :src="tenant.logoUrl"
                style="width: 120px;margin-left: 30px;height: auto;"
              />
            </div>
            <div class="pdf-details-block">
              <h5 class="pdf-company-name-txt">{{ tenant.name }}</h5>
              <div
                v-if="tenant.space && tenant.space.name"
                class="company-address-txt"
                style="color: #666666;"
              >
                {{ tenant.space.name }}
              </div>
              <!-- <div class="company-address-txt">SP Infocity, Chennai</div> -->
              <div class="year-txt">
                {{ startTime | formatDate(true) }} -
                {{ endTime | formatDate(true) }}
              </div>
            </div>
          </div>
          <div class="fR">
            <div class="org-logo" v-show="$org.logoUrl">
              <img
                :src="$org.logoUrl"
                class="flRight"
                style="width: 150px;height: auto;"
              />
            </div>
          </div>
        </div>
        <div class="pdf-table-list">
          <table class="pdf-table">
            <thead class="table-head" style="">
              <tr>
                <th
                  style="max-width: 500px;width: 500px;padding-left: 30px;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  ITEM
                </th>
                <th
                  style="max-width: 200px;width: 200px;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  NO. OF UNITS
                </th>
                <th
                  style="max-width: 200px;width: 200;text-align: left;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  COST PER UNIT <b>({{ this.$currency }})</b>
                </th>
                <th
                  style="max-width: 200px;width: 200px;padding-right: 60px;text-align: right;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  COST <b>({{ this.$currency }})</b>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in items" :key="index">
                <td style="max-width: 500px;width: 500px;padding-left: 30px;">
                  {{
                    item.utilityId
                      ? $constants.TenantUtility.getUtility(item.utilityId).name
                      : 'Common Area'
                  }}
                  - {{ item.name }}
                </td>
                <td style="max-width: 200px;width: 200px;">
                  {{
                    item.consumption === undefined ? '---' : item.consumption
                  }}
                </td>
                <td
                  style="max-width: 180px;width: 180px;text-align: right;padding-right:64px;"
                  class="text-center"
                >
                  {{ getCostPerUnit(item.costPerUnit) }}
                </td>
                <td
                  style="max-width: 200px;width: 200px;padding-right: 60px;text-align: right;"
                >
                  {{ item.cost | round }}
                </td>
              </tr>
              <tr></tr>
              <tr>
                <td
                  style="max-width: 600px;width: 600px;border-bottom: 1px solid transparent;"
                ></td>
                <td
                  style="max-width: 200px;width: 200px;border-bottom: 1px solid transparent;"
                ></td>
                <td
                  style="max-width: 200px;width: 200px;padding-right: 64px;text-align: right;font-size: 13px;"
                  class="total-txt-blue text-center border-bottom-blue"
                >
                  Sub Total
                </td>
                <td
                  style="max-width: 200px;width: 200px;padding-right: 60px;text-align: right;"
                  class="border-bottom-blue"
                >
                  {{ subTotal | round }}
                </td>
              </tr>
              <tr>
                <td
                  style="max-width: 600px;width: 600px;border-bottom: 1px solid transparent;"
                ></td>
                <td
                  style="max-width: 200px;width: 200px;border-bottom: 1px solid transparent;"
                ></td>
                <td
                  style="max-width: 200px;width: 200px;padding-right: 64px;text-align: right;font-size: 13px;"
                  class="total-txt-blue border-bottom-blue text-center"
                >
                  Tax {{ taxPercentage ? '(' + taxPercentage + '%)' : '' }}
                </td>
                <td
                  style="max-width: 200px;width: 200px;text-align: right;padding-right: 60px;"
                  class="border-bottom-blue"
                >
                  {{ tax | round }}
                </td>
              </tr>
              <tr>
                <td
                  style="max-width: 600px;width: 600px;border-bottom: 1px solid transparent;"
                ></td>
                <td
                  style="max-width: 200px;width: 200px;border-bottom: 1px solid transparent;"
                ></td>
                <td
                  style="max-width: 200px;width: 200px;font-weight: 700;font-size: 12px;text-align: right;padding-right: 64px;font-size: 15px;"
                  class="total-txt-blue text-center border-bottom-blue2"
                >
                  TOTAL
                </td>
                <td
                  style="max-width: 200px;width: 200px;padding-right: 60px;text-align: right;font-weight: 700;word-break: break-all;"
                  class="border-bottom-blue2"
                >
                  {{ (subTotal + tax) | round }}
                </td>
              </tr>
            </tbody>
            <!-- <tfoot>
                <tr>
                  <td colspan="100%" class="text-center">
                     <div class="powered-block">
                  <div class="powered-txt">POWERED BY</div>
                  <img src="~assets/facilio-logo-black.svg" style="height: 20px;">
                </div>
                  </td>
                 footer
                </tr>
              </tfoot> -->
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { round } from '@facilio/utils/filters'

export default {
  data() {
    return {
      items: [],
      tenant: null,
      subTotal: 0,
      taxPercentage: 0,
    }
  },
  computed: {
    startTime() {
      return parseInt(this.$route.query.startTime)
    },
    endTime() {
      return parseInt(this.$route.query.endTime)
    },
    tenantId() {
      return this.$route.query.tenantId
    },
    rateCardId() {
      return this.$route.query.rateCardId
    },
    tax() {
      let tax = 0
      if (this.taxPercentage) {
        tax = (this.subTotal * this.taxPercentage) / 100
      }
      return tax
    },
  },
  mounted() {
    this.generateBill()
  },
  methods: {
    generateBill() {
      this.$http
        .post('/tenant/generatebill', {
          startTime: this.startTime,
          endTime: this.endTime,
          tenantId: this.tenantId,
          rateCardId: this.rateCardId,
        })
        .then(response => {
          let billDetails = response.data.billDetails
          if (billDetails != null) {
            this.items = billDetails.items
            this.tenant = billDetails.tenant
            this.taxPercentage = billDetails.tax
            this.subTotal = billDetails.total
          }
        })
    },
    getCostPerUnit(costPerUnit) {
      return !isEmpty(costPerUnit) ? round(costPerUnit) : '---'
    },
  },
}
</script>

<style>
.fc-white-theme .layout-page-container {
  background: #fff;
}
.fc-layout-aside,
.layout-header {
  display: none;
}
.pdf-container {
  width: 100%;
  height: 100vh;
  overflow-x: hidden;
  overflow-y: scroll;
}
.pL60 {
  padding-left: 0 !important;
}
.pdf-inner-container {
  width: 100%;
  margin: 50px auto;
  max-width: 980px;
  margin-top: 50px;
  overflow: visible;
}
table {
  page-break-after: auto;
}
tr {
  page-break-inside: avoid;
  page-break-after: auto;
}
td {
  page-break-inside: avoid;
  page-break-after: auto;
}
thead {
  display: table-header-group;
}
tfoot {
  display: table-footer-group;
}
body {
  overflow: scroll;
}
.height100 {
  height: auto !important;
}
.normal main {
  overflow: visible !important;
}
.pdf-details-block {
  /* margin-top: 40px; */
  margin-left: 30px;
}
.pdf-details-block .pdf-company-name-txt {
  font-size: 22px;
  color: #000000;
  letter-spacing: 0.05px;
  /* margin-bottom: 20px; */
}
.company-address-txt {
  font-size: 11px;
  color: #666666;
  letter-spacing: 0.05px;
}
.year-txt {
  margin-top: 40px;
  font-size: 14px;
  color: #20909d;
  letter-spacing: 0.05px;
}
.pdf-table-list {
  clear: both;
  width: 100%;
  padding-top: 60px;
}
.table-head {
  display: table-header-group;
  vertical-align: middle;
  border-color: inherit;
}
.table-head th {
  display: table-cell;
  vertical-align: inherit;
  padding-top: 17px;
  padding-bottom: 17px;
  font-size: 12px;
  color: #20909d;
  letter-spacing: 0.7px;
  font-weight: normal;
}
.table-head tr {
  display: table-row;
  vertical-align: inherit;
  border-top: 1px solid #b0d9de;
  border-bottom: 1px solid #b0d9de;
}
.pdf-table td {
  display: table-cell;
  vertical-align: inherit;
}
.pdf-table {
  display: table;
  empty-cells: hide;
}
.pdf-table tbody {
  display: table-row-group;
  vertical-align: middle;
  border-color: inherit;
}
.pdf-table tbody td {
  padding-top: 17px;
  padding-bottom: 17px;
  font-size: 14px;
  color: #333333;
  letter-spacing: 0.01px;
  border-bottom: 1px solid #ebebeb;
}
.total-txt-blue {
  color: #3ea1ae !important;
  letter-spacing: 0.05px !important;
}
.border-bottom-blue {
  border-bottom: 1px solid #b0d9de !important;
}
.border-bottom-blue2 {
  border-top: 2px solid #b0d9de !important;
  border-bottom: 2px solid #b0d9de !important;
}
td:empty {
  visibility: hidden;
}
@media only print {
  .pdf-container,
  .pdf-table-list {
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
    page-break-after: always;
  }
}
.powered-block {
  text-align: center;
  /* position: absolute; */
  left: 0;
  right: 0;
  bottom: 0;
  margin-top: 60px;
}
.powered-txt {
  font-size: 10px;
  letter-spacing: 2px;
  color: #adb0b6;
  padding-bottom: 2px;
}
</style>
