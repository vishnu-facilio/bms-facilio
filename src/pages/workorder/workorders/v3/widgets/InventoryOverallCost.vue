<template>
  <div class="white-bg-block pm-summary-right-bg wo-summary-right-bg-border">
    <div class="new-in-header p30">
      {{ $t('common.header.overall_cost') }}
    </div>
    <div v-if="workOrderCostList.length">
      <el-row
        class="overallCost visibility-visible-actions"
        v-for="cost in workOrderCostList"
        :key="cost.id"
      >
        <span
          @click="deleteAdditionalCost(cost.id)"
          v-if="cost.costTypeEnum === 'custom' && actionRule"
          class="visibility-hide-actions mL30 export-dropdown-menu additionalCostIcon additionalCostDelete"
        >
          <i class="el-icon-delete"></i>
        </span>
        <el-col :span="18" v-if="cost.costTypeEnum === 'custom'">
          <div
            v-tippy
            :content="cost.name"
            class="label-txt-black textoverflow-ellipsis"
          >
            {{ cost.name }}
          </div>
        </el-col>
        <el-col :span="18" v-else>
          <div class="label-txt-black textoverflow-ellipsis">
            {{ cost.name }}
          </div>
        </el-col>
        <el-col :span="6" v-if="cost.costTypeEnum === 'custom'">
          <div class="label-txt-black text-right">
            <currency
              :value="cost.cost"
              v-tippy
              :content="$helpers.formatedCurrencyCost(cost.cost)"
              :recordCurrency="recordCurrency"
              class="textoverflow-ellipsis"
            ></currency>
          </div>
        </el-col>
        <el-col :span="6" v-else>
          <div class="label-txt-black text-right">
            <currency
              :value="cost.cost"
              class="textoverflow-ellipsis"
              v-tippy
              :content="$helpers.formatedCurrencyCost(cost.cost)"
              :recordCurrency="recordCurrency"
            ></currency>
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else>
      <el-row class="p30">
        <el-col :span="12">
          <div class="label-txt-black">
            {{ $t('common.products.items') }}
          </div>
        </el-col>
        <el-col :span="12">
          <div class="label-txt-black text-right">0</div>
        </el-col>
      </el-row>
      <el-row class="p30 pT10">
        <el-col :span="12">
          <div class="label-txt-black">
            {{ $t('common.header.tools') }}
          </div>
        </el-col>
        <el-col :span="12">
          <div class="label-txt-black text-right">0</div>
        </el-col>
      </el-row>
    </div>

    <el-row
      class="fc-spares-subtotal-additional overallCost relative visibility-visible-actions"
    >
      <el-col :span="17">
        <el-input
          :placeholder="$t('common._common.additional_cost')"
          v-model="additionalCost.name"
          class="fc-input-full-border2 width140px"
          @change="addAdditionalCost()"
        ></el-input>
      </el-col>
      <el-col :span="1"></el-col>
      <el-col :span="6" class="text-right additionalCostEnter pL20">
        <el-input
          v-if="$currency === '$'"
          placeholder="0"
          type="number"
          :prefix="$currency"
          v-model="additionalCost.cost"
          class="fc-input-full-border2 inventory-input-width75px text-right"
          @change="addAdditionalCost()"
        ></el-input>
        <el-input
          v-if="$currency !== '$'"
          placeholder="0"
          type="number"
          :suffix="$currency"
          v-model="additionalCost.cost"
          class="fc-input-full-border2 inventory-input-width75px text-right"
          @change="addAdditionalCost()"
        ></el-input>
      </el-col>
    </el-row>
    <el-row class="border-top1 text-right total-amount">
      <el-col :span="24" class="p30 pT10">
        <div class>
          <currency
            :value="workOrderTotalCost"
            :recordCurrency="recordCurrency"
          ></currency>
        </div>
        <div class="fc-text-pink13 fw-bold text-right pT5">
          {{ $t('common.header._total') }}
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  props: ['details'],
  mixins: [workorderMixin],
  data() {
    return {
      selectedInventory: null,
      hidequrry: true,
      additionalCost: {
        name: null,
        cost: null,
      },
      loading: {
        deleteAdditionalCost: false,
      },
      selectedInventoryList: [],
      inventoryRequestForWOLoading: false,
      iTloading: false,
      workOrderCostList: [],
      inventory: [],
      tempStoreList: [],
      selectedStoreList: [],
      inventoryRequestDialogVisibility: false,
      issueInventoryIRDialogVisibility: false,
      inventoryRequestList: null,
      inventoryListLoading: false,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.init()
  },
  mounted() {
    this.$root.$on('refresh-inventory-summary', () => {
      // console.log('event received to reload from root')
      this.init()
    })
    eventBus.$on('refresh-inventory-summary', () => {
      // console.log('event received to reload')
      this.init()
    })
    eventBus.$on('reloadOverallCost', () => {
      this.init()
    })
  },
  computed: {
    workorder() {
      return this.details.workorder
    },
    workOrderTotalCost() {
      if (this.workOrderCostList && this.workOrderCostList.length) {
        let cost = 0
        for (let i = 0; i < this.workOrderCostList.length; i++) {
          cost += this.workOrderCostList[i].cost
        }
        return cost
      } else {
        return 0
      }
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
    actionRule() {
      if (this.canEdit) {
        return true
      } else {
        return false
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
      this.getWorkOrderCostList(true)
    },
    reload() {
      // this.loadWoItemParts(true)
    },
    addAdditionalCost() {
      let self = this
      if (this.additionalCost.name && this.additionalCost.cost !== null) {
        let param = {
          workorderCost: {
            name: this.additionalCost.name,
            parentId: {
              id: this.workorder.id,
            },
            cost: Number(this.additionalCost.cost),
            costType: 5,
          },
        }
        this.$http.post('v2/workorderCosts/add', param).then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common._common.cost_added_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.additionalCost = { name: null, cost: null }
            self.getWorkOrderCostList()
          } else {
            self.$message.error(response.data.message)
          }
        })
      }
    },
    actionToWorkorderStatusChange() {
      this.fetchWo()
    },
    deleteAdditionalCost(id) {
      if (id) {
        let self = this
        let param = {
          workordercostId: [id],
          parentId: this.workorder.id,
        }
        this.$http.post('v2/workorderCosts/delete', param).then(response => {
          self.actionToWorkorderStatusChange()
          if (response.data.responseCode === 0) {
            self.getWorkOrderCostList()
            self.$message.success('Cost Deleted')
          } else {
            self.$message.error(response.data.message)
          }
        })
      }
    },
    getWorkOrderCostList(loading) {
      let self = this
      this.$http
        .get('v2/workorderCostsList/parent/' + this.workorder.id)
        .then(response => {
          if (!loading) {
            self.itemnsloading = false
          }
          self.workOrderCostList = response.data.result.workorderCost
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
.inv-name {
  height: 40px;
  line-height: 40px;
  padding-left: 15px;
  padding-right: 15px;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #d0d9e2 !important;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #333333;
  text-overflow: ellipsis;
  font-weight: 400;
  padding-right: 30px;
  white-space: nowrap;
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
.search-bar {
  width: 40%;
  justify-content: right;
  align-items: right;
  text-align: right;
  position: absolute;
  right: 28px;
  top: 10px;
  overflow: hidden;
  height: 40px;
}
.search-bar.active {
  width: 10%;
}
.popup-container-new {
  width: 100%;
  padding: 18px 30px 18px;
  border-bottom: 1px solid #edeeef;
  position: -webkit-sticky;
  position: sticky;
  background: #fff;
  display: flex;
  width: 100%;
  align-items: center;
}
.fc-setup-modal-title2 {
  font-size: 14px;
  font-weight: bold;
  letter-spacing: 0.3px;
  letter-spacing: 0.9px;
  color: #333333;
  text-transform: uppercase;
  width: 40%;
}
.store-room-bar {
  position: absolute;
  right: 60px !important;
}
.store-room-bar.active {
  position: absolute;
  left: 0;
}
.store-room-bar .el-input .el-input__inner {
  border: 0px;
}
/* .store-room-bar .el-input__suffix {
  right: -8px;
}
.store-room-bar .el-input__suffix {
  top: 7px;
} */
.worktool-cost-edit-sticky {
  background-color: #ffffff;
  position: sticky;
  z-index: 500;
  right: 0;
  animation: slide-down 0.7s;
  opacity: 1;
}
.worktool-cost-sticky {
  background-color: #ffffff;
  position: sticky;
  right: 35px;
  z-index: 500;
  padding-left: 10px;
  padding-right: 10px;
  animation: slide-down 0.7s;
  opacity: 1;
  box-shadow: -10px 0 34px 0 rgba(24, 19, 59, 0.05);
  -webkit-box-shadow: -10px 0 34px 0 rgba(24, 19, 59, 0.05);
  -moz-box-shadow: -10px 0 34px 0 rgba(24, 19, 59, 0.05);
}
.inventory-table-body-border-none table > tbody tr:last-child {
  border-bottom: none !important;
}
.secondary-quantity-text {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.55px;
  color: #92959a;
  padding-top: 2px;
  align-items: center;
}
.text-align-inventory {
  text-align: center;
}
.inventory-table-body-border-none th:last-child {
  border-right: none !important;
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
