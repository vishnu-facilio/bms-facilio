<template>
  <el-dialog
    :visible="additionalCostDialogVisibility"
    width="40%"
    :fullscreen="false"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <template slot="title">
      <div class="label-txt-black fw-bold text-uppercase fL">
        {{ $t('common.workorder_actuals.additional_cost') }}
      </div>
    </template>
    <div>
      <div class="headers">
        <div class="cost-name">
          {{ $t('common.workorder_actuals.cost_name') }}
          <span class="mandatory">
            *
          </span>
        </div>
        <div class="pL142">
          {{ $t('common.workorder_actuals.cost') }}
          <span class="mandatory">
            *
          </span>
        </div>
      </div>
      <div class="overflow-y-scroll max-height-275 pL15">
        <div
          v-for="(additionalCost, index) in additionalCosts"
          :key="index"
          class="flex"
        >
          <div class="cost-common-wrap">
            <div class="cost-name-wrap">
              <div>
                <el-input
                  :placeholder="$t('common.workorder_actuals.enter_name')"
                  v-model="additionalCost.name"
                  class="fc-input-full-border2 width200px"
                ></el-input>
              </div>
            </div>
            <div class="cost-name-wrap">
              <div style="display:flex">
                <div class="currency">
                  {{ `${recordCurrency || $currency}` }}
                </div>
                <div>
                  <el-input
                    type="number"
                    :placeholder="$t('common.workorder_actuals.enter_price')"
                    v-model="additionalCost.cost"
                    class="fc-input-full-border2 width200px"
                  ></el-input>
                </div>
              </div>
            </div>
          </div>
          <div class="remove-icon">
            <i
              @click="removeAdditionalCost(index)"
              v-if="additionalCosts.length > 1"
              class="el-icon-remove-outline fc-row-delete-icon f15 bold"
            ></i>
          </div>
        </div>
      </div>
      <div class="pT20 pL15">
        <el-button
          type="primary"
          class="line-item-list-footer-add-btn"
          @click="newAdditionalCost()"
        >
          <i class="el-icon-plus plus-icon"></i>
          {{ $t('common.workorder_actuals.add') }}
        </el-button>
      </div>
    </div>
    <div class="footer-btns">
      <el-button type="primary" class="save-btn" @click="saveRecords()">
        {{ $t('common.workorder_actuals.save') }}</el-button
      >
      <el-button type="primary" class="cancel-btn" @click="closeDialog()">
        {{ $t('common.workorder_actuals.cancel') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import cloneDeep from 'lodash/cloneDeep'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getCurrencyForCurrencyCode } from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  name: 'AdditionalCost',
  props: ['additionalCostDialogVisibility', 'details'],
  data: () => ({
    additionalCosts: [{ name: null, cost: null }],
  }),
  computed: {
    recordCurrency() {
      let { currencyCode } = this.details || {}
      let recordCurrency = getCurrencyForCurrencyCode(currencyCode)
      return recordCurrency
    },
  },
  methods: {
    newAdditionalCost() {
      let { additionalCosts } = this
      let additionalCost = { name: null, cost: null }
      additionalCosts.push(cloneDeep(additionalCost))
      this.additionalCosts = additionalCosts
    },
    removeAdditionalCost(index) {
      let { additionalCosts } = this
      additionalCosts.splice(index, 1)
    },
    async saveRecords() {
      let { additionalCosts, details } = this || {}
      let { id, currencyCode, exchangeRate } = details || {}
      additionalCosts = additionalCosts.filter(element => {
        let { name, cost } = element || {}
        return !isEmpty(name) && !isEmpty(cost)
      })
      if (!isEmpty(additionalCosts)) {
        let workorderCost = additionalCosts.map(element => {
          let { name, cost } = element || {}
          return {
            name: name,
            cost: cost,
            parentId: { id },
            costType: 5,
            currencyCode,
            exchangeRate,
          }
        })
        let url = 'v3/modules/bulkCreate/workorderCost'
        let params = {
          data: {
            workorderCost: workorderCost,
          },
          moduleName: 'workorderCost',
        }
        let { error, data } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common.workorder_actuals.add_cost_added_success')
          )
          this.resetAdditionalCosts()
          let { workorderCost } = data || {}
          this.$emit('saveAdditionalCost', workorderCost)
        }
      } else {
        this.$message.error(
          this.$t('common.workorder_actuals.please_add_additional_cost')
        )
      }
    },
    closeDialog() {
      this.$emit('closeAdditionalCost')
    },
    resetAdditionalCosts() {
      let resetAdditionalCosts = [{ name: null, cost: null }]
      this.$set(this, 'additionalCosts', resetAdditionalCosts)
    },
  },
}
</script>
<style lang="scss" scoped>
.footer-btns {
  text-align: right;
  padding-top: 20px;
}
.mandatory {
  color: #d60000;
}
.remove-icon {
  padding-left: 20px;
  padding-top: 26px;
}
.currency {
  width: 40px;
  height: 40px;
  background-color: #fff;
  border: solid 1px #e4eaed;
  background-color: #f7faff;
  border-radius: 3px !important;
  text-align: center;
  padding-top: 10px;
}
.width200px {
  width: 200px;
}
.pL142 {
  padding-left: 142px;
}
.headers {
  display: flex;
  padding-bottom: 5px;
}
.max-height-275 {
  max-height: 275px;
}
.line-item-list-footer-add-btn {
  border-radius: 4px;
  border: solid 1px #38b2c2;
  background-color: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #324056;
  .plus-icon {
    margin-right: 4px;
    font-weight: bold;
  }
}
.save-btn {
  border-radius: 4px;
  background-color: #3ab2c2;
  border: solid 1px #38b2c2;
}
.cancel-btn {
  border-radius: 4px;
  border: solid 1px #38b2c2;
  background-color: #fff;
  color: #324056;
}
.cost-common-wrap {
  width: 464px;
  display: flex;
  justify-content: space-between;
}
.cost-name {
  padding-left: 18px;
}
.cost-name-wrap {
  padding-top: 15px;
  display: flex;
  flex-direction: column;
}
</style>
