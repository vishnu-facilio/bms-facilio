<template>
  <div class="p30 white-bg-block mB20" ref="inventoryLaborWidget">
    <div class="flex-middle">
      <div class="fc-v1-icon-bg text-center">
        <InlineSvg src="svgs/labour" iconClass="icon icon-lg"></InlineSvg>
      </div>
      <div class="fc-black3-16 mL10">{{ $t('common.header.labour') }}</div>
    </div>

    <div
      class="inventory-table inventory-tool-table inventory-labour-tool-table pT20"
    >
      <table class="width100" v-if="labourLoading">
        <tr>
          <td colspan="100%">
            <div class="iTloading in-no-data">
              <spinner :show="true" size="80"></spinner>
            </div>
          </td>
        </tr>
      </table>

      <table class="width100" v-else>
        <thead>
          <tr>
            <th class style="width: 180px;">
              {{ $t('common.header._labour') }}
            </th>
            <th class="text-right width110px">
              {{ $t('common.wo_report._start_time') }}
            </th>
            <th class="text-right width110px pL20">
              {{ $t('common.wo_report._end_time') }}
            </th>
            <th class="text-right width130px">
              {{ $t('common._common.duration_hr') }}
            </th>
            <th class="text-right">{{ $t('common.header.rate_hr') }}</th>
            <th class="text-right">{{ $t('common.tabs._cost') }}</th>
            <th class="width40px"></th>
          </tr>
        </thead>
        <tbody v-if="!workorderLabourList.length">
          <tr>
            <td
              @click="actionRule ? addNewWOLabour() : null"
              :class="{ disabled: !actionRule }"
              class="inventory-td-selected pL20 pT10 pB10"
            >
              <div>{{ $t('common.header._add_labour') }}</div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-black-13 text-right">-- -- --</div>
            </td>
            <td class="pT10 pB10 pL20">
              <div class="fc-black-13 text-right">-- -- --</div>
            </td>
            <td>
              <div class="text-right">
                <el-input
                  placeholder
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                  disabled
                ></el-input>
              </div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency
                  :value="0"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency
                  :value="0"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td></td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr
            v-for="(workLabour, index) in workorderLabourList"
            :key="workLabour.id"
            class="borderB1px border-color11 visibility-visible-actions pointer"
          >
            <td>
              <div class="pL17" style="width: 180px;">
                <div>{{ workLabour.labour.name }}</div>
              </div>
            </td>
            <td>
              <div class="in-Quantity width110px">
                <div class="fc-black-13 text-right pR17">
                  {{
                    formatTime(Number(workLabour.startTime), 'hh:mm a', false)
                  }}
                </div>
                <div class="fc-grey2-text12 text-right">
                  <el-date-picker
                    v-model="workLabour.startTime"
                    format="dd-MM-yyyy hh:mm a"
                    value-format="timestamp"
                    type="datetime"
                    :disabled="!actionRule"
                    :placeholder="$t('common.wo_report.start_time')"
                    class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                    @change="changeWorkLabourField(workLabour, index, 1)"
                  ></el-date-picker>
                </div>
              </div>
            </td>
            <td class="pL20">
              <div class="in-Quantity width110px">
                <div class="fc-black-13 text-right">
                  {{ formatTime(Number(workLabour.endTime), 'hh:mm a', false) }}
                </div>
                <div class="fc-grey2-text12 text-right">
                  <el-date-picker
                    v-model="workLabour.endTime"
                    format="dd-MM-yyyy hh:mm a"
                    value-format="timestamp"
                    type="datetime"
                    :disabled="!actionRule"
                    :placeholder="$t('common.wo_report.end_time')"
                    class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                    @change="changeWorkLabourField(workLabour, index, 2)"
                    :picker-options="{
                      disabledDate(time) {
                        return time.getTime() < workLabour.startTime
                      },
                    }"
                  ></el-date-picker>
                </div>
              </div>
            </td>
            <td>
              <div
                class="in-Quantity"
                style="width: 70px; text-align: right; float: right;"
                :title="workLabour.duration"
                v-tippy="{
                  placement: 'top',
                  arrow: true,
                  animation: 'shift-away',
                }"
              >
                <el-input
                  placeholder="duration"
                  v-model="workLabour.duration"
                  @change="changeWorkLabourField(workLabour, index, 3)"
                  :disabled="!actionRule"
                  class="pL10 labour-inventory-items-input labour-inventory-input pR0 fc-input-full-border-select2"
                ></el-input>
              </div>
            </td>
            <td>
              <div class="text-right">
                <currency
                  :value="workLabour.labour.cost"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td>
              <div class="text-right">
                <currency
                  :value="workLabour.cost"
                  :recordCurrency="recordCurrency"
                ></currency>
              </div>
            </td>
            <td v-show="actionRule">
              <i
                class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                data-arrow="true"
                :title="$t('common._common.delete_labour')"
                v-tippy
                @click="deleteWorkorderLabour(workLabour, index)"
              ></i>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="item-add" v-if="labourLoading"></div>
    <div class="item-add" v-else>
      <div
        class="fL"
        v-show="isNotPortal && actionRule && workorderLabourList.length"
      >
        <div class="green-txt-13 fc-v1-add-txt pointer" @click="addNewWOLabour">
          <img src="~assets/add-icon.svg" />{{
            $t('common.header._add_labour')
          }}
        </div>
      </div>
      <div class="fR inline-flex mR44">
        <div class="bold mR50">{{ $t('common.header._total') }}</div>
        <div class="fc-black3-16 text-right bold pL10">
          <currency
            :value="workLabourTotalCost"
            :recordCurrency="recordCurrency"
          ></currency>
        </div>
      </div>

      <el-dialog
        :visible.sync="newLabourToggle"
        :fullscreen="false"
        open="top"
        custom-class="fc-dialog-up Inventoryaddvaluedialog"
        :append-to-body="true"
      >
        <div class="new-header-container">
          <div class="fc-setup-modal-title">
            {{ $t('common.header.labour_list') }}
          </div>
        </div>
        <div class="fc-inv-container-body">
          <table class="setting-list-view-table width100 invent-table-dialog">
            <thead>
              <th class="setting-table-th setting-th-text"></th>
              <th class="setting-table-th setting-th-text">
                {{ $t('common.products.name') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('common._common._phone_number') }}
              </th>
              <th class="setting-table-th setting-th-text">
                {{ $t('common._common.status') }}
              </th>
            </thead>
            <tbody v-if="!labourList.length">
              <tr>
                <td colspan="100%">
                  <div
                    class="flex-middle justify-content-center flex-direction-column"
                  >
                    <inline-svg
                      src="svgs/emptystate/inventory"
                      iconClass="icon text-center icon-100"
                    ></inline-svg>
                    <div class="nowo-label f14 bold">
                      {{ $t('common._common.no_labours_present') }}
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow asset-hover-td"
                v-for="labour in labourList"
                :key="labour.id"
              >
                <td style="width:10%;">
                  <el-checkbox v-model="labour.checked"></el-checkbox>
                </td>
                <td>{{ labour.name }}</td>
                <td>{{ labour.phone }}</td>
                <td>{{ labour.availability ? 'Active' : 'Inactive' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-dialog-footer-parts-dialog">
          <el-button class="modal-btn-cancel" @click="cancelLabourDialog()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            style="margin-left:0px !important;"
            type="primary"
            @click="addLabourSave()"
            :disabled="iTloading"
            >{{ $t('common._common._add') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import moment from 'moment-timezone'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import InlineSvg from '@/InlineSvg'

export default {
  props: ['details', 'resizeWidget'],
  mixins: [workorderMixin],
  components: {
    InlineSvg,
  },
  data() {
    return {
      iTloading: false,
      labourLoading: false,
      workLabourTotalCost: null,
      workorderLabourList: [],
      labourList: [],
      newLabourToggle: false,
      selectedLabour: [],
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.init()
  },
  computed: {
    workorder() {
      return this.details.workorder
    },
    actionRule() {
      return this.canEdit
    },
    canEdit() {
      if (this.isStateFlowEnabled) {
        return !this.$store.getters.isStatusLocked(
          this.workorder.moduleState.id,
          'workorder'
        )
      } else {
        return this.workorder && this.workorder.approvalState !== 2
      }
    },
    isStateFlowEnabled() {
      return Boolean(
        this.workorder.moduleState && this.workorder.moduleState.id
      )
    },
  },
  methods: {
    init() {
      this.loadWorkOrderLabour(true)
    },
    autoResize() {
      this.$nextTick(() => {
        console.log('resizing!')
        let container = this.$refs['inventoryLaborWidget']
        if (container) {
          let height = container.scrollHeight + 10
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
    addNewWOLabour() {
      this.loadLabour(true)
      this.newLabourToggle = true
    },
    deleteWorkorderLabour(workLabour) {
      let param = { parentId: this.workorder.id, workorderLabourIds: [] }
      param.workorderLabourIds.push(workLabour.id)
      this.$http
        .post('v2/workorderLabour/delete', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$message.success(
              this.$t('common.products.labour_deleted_successfully')
            )
          } else {
            this.$message.error(response.data.message)
          }
          this.$root.$emit('refresh-inventory-summary')
        })
        .then(() => {
          this.loadWorkOrderLabour()
        })
        .catch(error => {
          console.error(error)
        })
    },
    cancelLabourDialog() {
      this.newLabourToggle = false
    },
    addLabourSave() {
      let param = { workorderLabourList: [] }
      if (!this.labourList.length) {
        return
      }
      for (let i = 0; i < this.labourList.length; i++) {
        let labour = this.labourList[i]
        if (labour.checked) {
          let temp = {
            parentId: this.workorder.id,
            startTime: -1,
            endTime: -1,
            labour: labour,
          }
          param.workorderLabourList.push(temp)
          this.labourList[i] = false
        }
      }

      this.labourLoading = true
      this.$http
        .post('/v2/workorderLabour/addOrUpdate', param)
        .then(response => {
          this.labourLoading = true
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            this.$message.success(
              this.$t('common._common.added_available_labour_successfully')
            )
          }
        })
        .then(() => {
          this.loadWorkOrderLabour()
        })
        .catch(error => {
          console.error(error)
          this.$message.error(this.$t('common.wo_report.unable_to_add_labour'))
        })
        .finally(() => {
          this.labourLoading = false
          this.newLabourToggle = false
          this.$root.$emit('refresh-inventory-summary')
        })
    },

    changeWorkLabourField(workLabour, index, changedfield) {
      let self = this
      let duration = Number(workLabour.duration).toFixed(2)
      let param = { workorderLabourList: [] }
      if (workLabour.startTime === -1) {
        workLabour.startTime = null
      }
      if (workLabour.endTime === -1) {
        workLabour.endTime = null
      }
      let startTime =
        workLabour.startTime !== null
          ? moment(workLabour.startTime)
              .tz(this.$timezone)
              .valueOf()
          : workLabour.startTime
      let endTime =
        workLabour.endTime !== null
          ? moment(workLabour.endTime)
              .tz(this.$timezone)
              .valueOf()
          : workLabour.endTime

      let temp = {
        id: workLabour.id,
        parentId: this.workorder.id,
        duration: changedfield === 3 || changedfield === 1 ? duration : -1,
        labour: workLabour.labour,
        startTime: startTime ? startTime : -99,
        endTime: changedfield === 2 ? endTime : -99,
      }
      param.workorderLabourList.push(temp)
      this.$http
        .post('/v2/workorderLabour/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common._common.updated_successfully')
            )
          }
        })
        .then(() => {
          self.loadWorkOrderLabour()
        })
    },
    loadLabour() {
      let self = this
      this.$http
        .get('/v2/labour/labourList')
        .then(response => {
          self.labourList = response.data.result.labours
          for (let i = 0; i < self.labourList.length; i++) {
            let labourItem = self.labourList[i]
            if (self.selectedLabour.includes(labourItem.id)) {
              self.labourList.splice(i, 1)
            }
          }
        })
        .catch(error => {
          console.log(error)
          self.$message.error(this.$t('common.wo_report.unable_to_labor_list'))
        })
    },
    loadWorkOrderLabour(loading) {
      if (loading) {
        this.labourLoading = true
      }
      this.$http
        .get('/v2/workorderLabourList/parent/' + this.workorder.id)
        .then(response => {
          this.workLabourTotalCost = 0
          for (let workLabour in response.data.result.workorderLabour) {
            this.workLabourTotalCost =
              this.workLabourTotalCost +
              response.data.result.workorderLabour[workLabour].cost
          }
          this.workorderLabourList = this.$helpers.cloneObject(
            response.data.result.workorderLabour
          )
          this.selectedLabour = []
          for (let index in this.workorderLabourList) {
            let workLabourItem = this.workorderLabourList[index]
            this.selectedLabour.push(workLabourItem.labour.id)
            workLabourItem.startTime =
              workLabourItem.startTime <= 0 ? null : workLabourItem.startTime
            workLabourItem.endTime =
              workLabourItem.endTime <= 0 ? null : workLabourItem.endTime
            workLabourItem.duration = workLabourItem.duration.toFixed(2)
          }

          this.labourLoading = false
        })
        .catch(error => {
          console.error(error)
          this.$message.error(this.$t('common.wo_report.unable_to_load_labor'))
        })
        .finally(() => {
          this.labourLoading = false
          this.autoResize()
        })
    },
  },
}
</script>
<style>
.item-add {
  padding-top: 20px;
  padding-bottom: 20px;
}
.inventory-tool-table .in-Quantity .el-input--prefix input.el-input__inner {
  padding-left: 0 !important;
  padding-right: 0;
}
.inventory-tool-table .in-Quantity .el-input--prefix .el-input__prefix {
  display: none;
}
.inventory-table thead > tr {
  height: 55px;
  border-top: 1px solid #eceef1;
  border-bottom: 1px solid #eceef1;
}
.inventory-table th {
  white-space: nowrap;
}
.inventory-table.pB20.pT20.tbody.tr:hover .el-input__inner {
  border-color: #d0d9e2 !important;
}

.inventory-table tbody td .p5 {
  padding: 6px;
  padding-left: 15px;
}
.in-Quantity .item {
  margin: 0px;
}
.inv-item:hover {
  background: #fafbfc;
}
.inv-id-bac-icon {
  position: relative;
  right: 1px;
  margin-right: 10px;
  font-weight: bold;
  color: #324056;
}
.in-no-data {
  height: 100px;
  width: 100%;
  text-align: center;
  justify-content: center;
  display: flex;
  align-items: center;
}
.new-in-header {
  font-size: 16px;
  letter-spacing: 0.7px;
  color: #324056;
  padding-bottom: 20px;
  padding-top: 30px;
}
.search-bar .el-icon-search {
  font-size: 14px;
  color: #50506c;
  font-weight: bold;
}
.inv-search-grey .el-icon-search {
  margin-right: 0;
  color: #50506c;
  font-weight: normal;
  color: #d0d9e2;
}
.total-amount {
  font-size: 24px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: right;
  color: #324056;
}
.invent-table-dialog tbody tr.tablerow.active1 td:first-child {
  border-left: 3px solid #39b2c2 !important;
}
.quant .el-input__inner {
  background: transparent !important;
  padding-right: 0;
  width: 80px;
  text-align: center;
  padding: 0;
}
.inv-icon {
  left: 11px;
  position: relative;
  top: 9px;
}
.additionalCostAdd {
  font-size: 15px;
  color: #00b395;
  font-weight: 900;
  position: absolute;
  right: 5px;
  top: 10px;
  cursor: pointer;
}
.additionalCostDelete {
  font-size: 15px;
  color: #e1573f;
  font-weight: 900;
  position: absolute;
  right: 5px;
  top: 0px;
  cursor: pointer;
}
.additionalCostEnter input.el-input__inner {
  text-align: right;
  padding-right: 7px;
}
.overallCost {
  padding: 20px 30px;
  padding-top: 0;
}
.inv-delet-icon {
  color: #e1573f;
  font-size: 14px;
}
.workItem-quantity {
  width: 80px;
  text-align: center;
  height: 40px;
  align-items: center;
  padding-top: 10px;
  cursor: no-drop;
}
.in-Quantity .el-form-item {
  margin: 0px;
}
.inventory-table table > tbody tr:last-child {
  border-bottom: 1px solid #eceef1 !important;
}
.fc-inv-container-body {
  height: 50vh;
  overflow: auto;
}
.invent-table-dialog .fc-setting-table-th setting-th-text {
  padding: 15px 30px;
}
.exceeded .el-input__inner,
.exceeded .el-input__inner:focus {
  border-color: #f56c6c !important;
}

@keyframes slide-down {
  0% {
    opacity: 1;
    transform: translateY(-100%);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
<style scoped>
.fc-create-btn.disabled:hover {
  box-shadow: 0 2px 4px 0 #ffc9de;
}

.v3-layout-override {
  flex-direction: row !important;
}
</style>
