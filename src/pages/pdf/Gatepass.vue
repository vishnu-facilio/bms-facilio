<template>
  <div>
    <div class="pdf-container inventory-pdf" v-if="gatePass">
      <div class="pdf-inner-container">
        <div class="logo-container">
          <div class="fL">
            <div class="pdf-details-block">
              <h5 class="fc-black-30 bold">{{ $account.org.name }}</h5>
            </div>
          </div>
          <div class="fR inventory-purchase-txt">
            <div class="org-logo" v-if="$account.org.logoUrl">
              <img
                :src="$account.org.logoUrl"
                style="width: 100px;margin-left: 30px;height: auto;margin-top: 15px;"
              />
            </div>
          </div>
        </div>
        <div class="clearboth pT10 mL30">
          <el-row class="pB15">
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">GATE PASS NO</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">: {{ gatePass.id }}</div>
            </el-col>
          </el-row>
          <el-row class="pB15">
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">DATE</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                :
                {{
                  gatePass.issuedTime !== -1
                    ? $options.filters.formatDate(gatePass.issuedTime, true)
                    : '---'
                }}
              </div>
            </el-col>
          </el-row>
          <el-row class="pB15">
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">VEHICLE NO</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                : {{ gatePass.vehicleNo ? gatePass.vehicleNo : '---' }}
              </div>
            </el-col>
          </el-row>
          <el-row class="pB15">
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">ISSUED TO</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                : {{ gatePass.issuedTo ? gatePass.issuedTo : '---' }}
              </div>
            </el-col>
          </el-row>
          <el-row
            class="pB15"
            v-if="
              gatePass.gatePassType === 1 &&
                gatePass.toStoreRoom &&
                gatePass.toStoreRoom.name
            "
          >
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">TO STOREROOM</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                :
                {{
                  gatePass.toStoreRoom.name ? gatePass.toStoreRoom.name : '---'
                }}
              </div>
            </el-col>
          </el-row>
          <el-row
            class="pB15"
            v-if="gatePass.gatePassType === 1 && gatePass.parentPoId"
          >
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">PO ID</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                :
                {{
                  gatePass.parentPoId
                    ? '[#' + gatePass.parentPoId.id + ']'
                    : '---'
                }}
              </div>
            </el-col>
          </el-row>
          <el-row
            class="pB15"
            v-if="
              gatePass.gatePassType === 2 &&
                gatePass.fromStoreRoom &&
                gatePass.fromStoreRoom.name
            "
          >
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">
                FROM STOREROOM
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                :
                {{
                  gatePass.fromStoreRoom.name
                    ? gatePass.fromStoreRoom.name
                    : '---'
                }}
              </div>
            </el-col>
          </el-row>
          <el-row class="pB15">
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">TYPE</div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                : {{ gatePass.gatePassType === 1 ? 'Inward' : 'Outward' }}
              </div>
            </el-col>
          </el-row>
          <el-row class="pB15" v-if="gatePass.returnable">
            <el-col :span="5">
              <div class="fc-pdf-blue-txt-13 text-uppercase">
                ITEM RETURNABLE
              </div>
            </el-col>
            <el-col :span="8">
              <div class="fc-pdf-black-13">
                :
                {{
                  gatePass.returnTime !== 0
                    ? 'By ' +
                      $options.filters.formatDate(gatePass.returnTime, true)
                    : 'Yes'
                }}
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="pdf-table-list clearboth">
          <table class="pdf-table gatepass-pdf-table" style="width:100%;">
            <thead class="table-head" style="">
              <tr>
                <th
                  style="width: 13%;padding-left: 30px;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  SR.NO.
                </th>
                <th
                  style="width: 10%;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  ITEM ID
                </th>
                <th
                  style="max-width: 200px;width: 200;text-align: left;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  ITEM NAME
                </th>
                <th
                  style="max-width: 200px;width: 200px;padding-right: 60px;text-align: left;border-top: 1px solid #B0D9DE;border-bottom: 1px solid #B0D9DE;"
                >
                  QTY
                </th>
              </tr>
            </thead>
            <tbody v-for="(lineItem, index) in gatePass.lineItems" :key="index">
              <tr>
                <td style="width: 13%;padding-left: 30px;">
                  {{ index + 1 }}
                </td>
                <td style="width: 10%;">
                  {{ getLineItemId(lineItem) }}
                </td>
                <td
                  style="max-width: 200px;width: 200px;text-align: left;padding-right:0;"
                >
                  {{ getLineItemName(lineItem) }}
                </td>
                <td
                  style="max-width: 200px;width: 200px;padding-right: 60px;text-align: left;"
                >
                  {{ lineItem.quantity }}
                </td>
              </tr>
            </tbody>
            <tfoot class="inventory-footer">
              <tr>
                <td colspan="100%">
                  <div class="mT80">
                    <div class="fL">
                      <div class="pdf-details-block">
                        <div class="fc-pdf-black-13">Issued By</div>
                        <div class="fc-pdf-black-13 fw-bold pT5">
                          {{ getIssuedByName() }}
                        </div>
                      </div>
                    </div>
                    <div class="fR inventory-purchase-txt">
                      <div
                        class="fc-grey-text12-light text-left"
                        style="color:#dbdbdb;"
                      >
                        ____________________________
                      </div>
                      <div class="mT10 text-right">Signature</div>
                    </div>
                  </div>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      loading: true,
      gatePass: null,
    }
  },
  mounted() {
    this.loadGatePass()
  },
  methods: {
    loadGatePass() {
      let self = this
      self.gatePass = null
      if (self.$route.params.id) {
        self.$http
          .get('/v2/gatePass/' + this.$route.params.id)
          .then(function(response) {
            self.loading = false
            if (response.data.responseCode === 0) {
              self.gatePass = response.data.result.gatePass
              setTimeout(function() {
                self.print()
              }, 1000)
            }
          })
      }
    },
    getLineItemName(lineItem) {
      let name
      if (lineItem.inventoryType === 1) {
        name = lineItem.itemType.name
        if (
          lineItem.itemType.isRotating &&
          lineItem.asset &&
          lineItem.asset.serialNumber
        ) {
          name = name + ' ' + '#' + lineItem.asset.serialNumber
        }
      } else if (lineItem.inventoryType === 2) {
        name = lineItem.toolType.name
        if (
          lineItem.toolType.isRotating &&
          lineItem.asset &&
          lineItem.asset.serialNumber
        ) {
          name = name + ' ' + '#' + lineItem.asset.serialNumber
        }
      }
      return name
    },
    getLineItemId(lineItem) {
      if (lineItem.inventoryType === 1) {
        return lineItem.itemType.id
      } else if (lineItem.inventoryType === 2) {
        return lineItem.toolType.id
      }
    },
    getIssuedByName() {
      if (this.gatePass.issuedBy && this.gatePass.issuedBy.id) {
        let tempuser = this.$store.state.users.find(
          u => u.id === this.gatePass.issuedBy.id
        )
        return tempuser.name
      } else {
        return '---'
      }
    },
    print() {
      this.$nextTick(() => {
        window.print()
      })
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
.inventory-pdf .pdf-details-block .pdf-company-name-txt {
  font-size: 18px;
  color: #000000;
  letter-spacing: 0.05px;
}
.pdf-company-name-txt2 {
  font-size: 17px;
  font-weight: 500;
  color: #000000;
  letter-spacing: 0.05px;
  text-transform: capitalize;
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
.gatepass-pdf-table tbody td {
  padding-top: 17px;
  padding-bottom: 17px;
  font-size: 14px;
  color: #333333;
  letter-spacing: 0.01px;
}
.gatepass-pdf-table tbody tr:nth-child(even) {
  border-bottom: 1px solid #b0d9de;
}
.gatepass-pdf-table tbody tr:nth-child(odd) {
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
.pdf-table-list td,
.pdf-table-list th {
  border-left: 1px solid transparent !important;
  border-right: 1px solid transparent !important;
}
.inventory-footer td {
  border-bottom: 1px solid transparent !important;
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
  .inventory-purchase-txt {
    margin-right: 30px;
  }
  .pdf-table-list {
    margin-left: 30px;
    margin-right: 30px;
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
