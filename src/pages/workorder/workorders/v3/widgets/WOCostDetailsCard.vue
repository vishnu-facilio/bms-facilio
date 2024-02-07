<template>
  <el-dialog
    :visible="showDetails"
    :title="dialogTitle"
    width="35%"
    class="fc-dialog-center wo-maintenance-cost-info"
    :before-close="closeInfo"
  >
    <div class="cost-info-dialog">
      <div v-if="isLoading" class="mT20 position-relative">
        <spinner :show="true" :size="80"></spinner>
      </div>
      <div v-else>
        <div
          class="wo-planned-cost display-flex-between-space"
          v-for="(key, label) in fixedCostInfo"
          :key="label"
        >
          <div class="cost-name pL20 pB10 pT15">{{ label }}</div>
          <div class="cost-value pR20  pB10 pT15 d-flex mR20">
            <span class="pR16">
              {{ getCostWithCurrency(key) }}
            </span>
          </div>
        </div>
        <div v-if="!actualCost" class="cost-seperator-line"></div>
        <template v-else>
          <div
            v-if="!$validation.isEmpty(customCostInfo)"
            class="cost-seperator-line"
          ></div>
          <div
            class="custom-wo-cost display-flex-between-space"
            v-for="(cost, index) in customCostInfo"
            :key="`cost-${index}`"
          >
            <div class="cost-name pL20 pB10 pT15">
              {{ $getProperty(cost, 'name', '---') }}
            </div>
            <div
              class="cost-value pR20  pB10 pT15 d-flex visibility-visible-actions"
            >
              <span>
                {{ getCostWithCurrency($getProperty(cost, 'cost', 0)) }}
              </span>
              <fc-icon
                group="action"
                name="delete"
                size="16"
                class="mL5 visibility-hide-actions pointer"
                @click="deleteCustomCost(cost, index)"
              ></fc-icon>
            </div>
          </div>
          <div class="cost-seperator-line"></div>
        </template>
        <div class="custom-wo-cost total-cost  display-flex-between-space">
          <div class="cost-name pL20 pB10 pT15 bold f14">
            {{ $t('maintenance._workorder.total_cost') }}
          </div>
          <div class="cost-value pR20  pB10 pT15 bold f14 mR20">
            {{ totalCost }}
          </div>
        </div>
      </div>
      <div
        class="modal-dialog-footer wo-maintenance-cost-info-footer"
        :class="footerClassName"
      >
        <div
          class="pL20 pB20 pT15 add-addnl-cost"
          v-if="actualCost"
          @click="addAdditionalCost()"
        >
          {{ $t('maintenance._workorder.add_addn_cost') }}
        </div>
        <div class="pR20 pB20 pT5">
          <el-button
            type="primary"
            class="wo-maintenance-cost-info-done"
            @click="closeInfo"
          >
            {{ $t('maintenance._workorder.close') }}</el-button
          >
        </div>
      </div>
      <AddAdditionalCost
        :additionalCostDialogVisibility="additionalCostDialogVisibility"
        :details="details"
        @saveAdditionalCost="saveAdditionalCost"
        @closeAdditionalCost="closeAdditionalCost"
      />
    </div>
  </el-dialog>
</template>
<script>
import AddAdditionalCost from './AddAdditionalCost.vue'
import { API } from '@facilio/api'
import { getCurrencyForCurrencyCode } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  name: 'WOCostDetailsCard',
  props: ['details', 'showDetails', 'costType', 'costDetails', 'isLoading'],
  components: { AddAdditionalCost },
  data: () => ({
    additionalCostDialogVisibility: false,
  }),
  computed: {
    dialogTitle() {
      let { costType } = this
      return costType === 'plans'
        ? this.$t('maintenance._workorder.plans_cost')
        : this.$t('maintenance._workorder.actuals_cost')
    },
    actualCost() {
      let { costType } = this
      return costType === 'actuals'
    },
    footerClassName() {
      let { actualCost } = this
      return actualCost ? 'justify-content-space' : 'justify-content-end'
    },
    fixedCostInfo() {
      let { costDetails } = this
      let { Labour, Items, Tools, Services } = costDetails || {}

      return { Labour, Items, Tools, Services }
    },
    customCostInfo() {
      let { costDetails } = this
      let { customCostList } = costDetails || {}

      return customCostList
    },
    totalCost() {
      let { costDetails, $currency, workorderCurrency } = this
      let { grossAmount } = costDetails || {}

      return `${workorderCurrency || $currency} ${grossAmount}`
    },
    workorderCurrency() {
      let { currencyCode } = this.details || {}
      let woCurrency = getCurrencyForCurrencyCode(currencyCode)
      return woCurrency
    },
  },
  methods: {
    getCostWithCurrency(cost) {
      let { $currency, workorderCurrency } = this
      return `${workorderCurrency || $currency} ${cost}`
    },
    closeInfo() {
      this.$emit('closeInfo')
    },
    addAdditionalCost() {
      this.additionalCostDialogVisibility = true
    },
    saveAdditionalCost(costList) {
      this.additionalCosts = costList
      this.$emit('updateAdditionalCost', { costList, skipOldValues: false })
      this.additionalCostDialogVisibility = false
    },
    async deleteCustomCost(cost, index) {
      let { id } = cost || {}
      let moduleName = 'workorderCost'
      let { error } = await API.deleteRecord(moduleName, id)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(
          this.$t('common.workorder_actuals.add_cost_deleted_success')
        )
        let { customCostInfo } = this
        customCostInfo.splice(index, 1)
        this.$emit('updateAdditionalCost', {
          costList: customCostInfo,
          skipOldValues: true,
        })
      }
    },
    closeAdditionalCost() {
      this.additionalCosts = [
        {
          name: null,
          cost: null,
        },
      ]
      this.additionalCostDialogVisibility = false
    },
  },
}
</script>
<style lang="scss">
.wo-maintenance-cost-info {
  .el-dialog {
    padding-bottom: 65px !important;
    border-radius: 8px;
    .el-dialog__header {
      padding: 10px 20px 5px;
      height: 45px;
      border-bottom: 1px solid #f0f0f0;
    }
    .el-dialog__title {
      text-transform: capitalize;
    }
    .el-dialog__headerbtn {
      top: 14px !important;
    }
    .el-dialog__title {
      text-transform: capitalize;
      color: #324056;
      font-size: 16px;
      font-weight: bold;
      letter-spacing: 0.4px;
    }
    .el-dialog__body {
      padding: 0 !important;
    }
  }
}
</style>
<style lang="scss" scoped>
.cost-info-dialog {
  height: 300px;
  overflow: scroll;
}
.total-cost {
  color: #1d384e;
}
.wo-planned-cost,
.custom-wo-cost {
  height: 50px;
  border-bottom: 1px solid #f1f2f4;
}

.cost-seperator-line {
  height: 1px;
  background-color: #d5d9dd;
  margin-top: -2px;
}
.wo-maintenance-cost-info-footer {
  display: flex;
  margin-top: 10px;
  cursor: pointer;
  height: 57px;
  .add-addnl-cost {
    font-size: 14px;
    font-weight: 500;
    color: #0074d1;
    &:hover {
      text-decoration: underline;
    }
  }
  .wo-maintenance-cost-info-done {
    width: 90px;
    height: 42px;
    border-radius: 4px;
    border-color: transparent;
    background-color: #3ab2c2;
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    text-transform: capitalize;
    &:hover {
      background-color: #3ab2c2;
      color: #ffffff;
    }
    &:active {
      color: #fff;
      background-color: #3ab2c2;
      border: transparent;
    }
  }
}
</style>
