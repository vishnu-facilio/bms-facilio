<template>
  <div :key="customCostList.length">
    <div class="flex cost-title">
      <div class="fc-black3-16 p20 pB15">
        {{ $t('common.workorder_actuals.cost') }}
      </div>
      <div class="add-color">
        <div class="pointer" @click="addAdditionalCost()">
          <i class="el-icon-plus plus-icon"></i>
          {{ $t('common.workorder_actuals.add') }}
        </div>
      </div>
    </div>
    <div v-if="isLoading">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <table ref="actualsCostTable" v-else>
      <tbody>
        <tr>
          <td class="pL20">{{ $t('common.header.labours') }}</td>
          <td class="text-right pT15 pB15 pR5 cost-color">
            {{ `${$currency} ${labourCost}` }}
          </td>
          <td class="width20px"></td>
        </tr>
        <tr>
          <td class="pL20">{{ $t('common.header.items') }}</td>
          <td class="text-right pT15 pB15 pR5 cost-color">
            {{ `${$currency} ${itemCost}` }}
          </td>
          <td class="width20px"></td>
        </tr>
        <tr>
          <td class="pL20">{{ $t('common.header.tools') }}</td>
          <td class="text-right pT15 pB15 pR5 cost-color">
            {{ `${$currency} ${toolCost}` }}
          </td>
          <td class="width20px"></td>
        </tr>
        <tr>
          <td class="pL20">{{ $t('common.products.services') }}</td>
          <td class="text-right pT15 pB15 pR5 cost-color">
            {{ `${$currency} ${serviceCost}` }}
          </td>
          <td class="width20px"></td>
        </tr>
        <tr
          class="addtional-cost"
          v-for="customCost in customCostList"
          :key="customCost.id"
        >
          <td class="pL20 additional-cost-name">
            <div
              v-tippy
              :content="customCost.name"
              class="label-txt-black textoverflow-ellipsis"
            >
              {{ customCost.name || '---' }}
            </div>
          </td>
          <td class="text-right pT15 pB15 pR5 cost-color">
            {{ `${$currency} ${customCost.cost || 0}` }}
          </td>
          <td class="width20px">
            <span
              @click="deleteAdditionalCost(customCost.id)"
              class="additionalCostDelete"
            >
              <i class="el-icon-delete"></i>
            </span>
          </td>
        </tr>
      </tbody>
    </table>
    <el-dialog
      v-if="additionalCostDialogVisibility"
      :visible.sync="additionalCostDialogVisibility"
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
                    {{ `${$currency}` }}
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
    <div class="text-right  grand-total" v-if="!isLoading">
      <div class="cost-color pB10">
        {{ $t('common._common.grand_total') }}
      </div>
      <div class="fw-550">{{ `${$currency} ${grossAmount}` }}</div>
    </div>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['details', 'resizeWidget'],
  data() {
    return {
      additionalCostDialogVisibility: false,
      showDeleteIcon: false,
      itemCost: 0,
      toolCost: 0,
      serviceCost: 0,
      labourCost: 0,
      grossAmount: 0,
      isLoading: false,
      customCostList: {},
      additionalCosts: [{ name: null, cost: null }],
    }
  },
  computed: {
    workorder() {
      let { details } = this || {}
      let { workorder } = details || {}
      return workorder
    },
  },
  created() {
    this.loadWorkOrderCostList()
  },
  mounted() {
    eventBus.$on('reloadWorkorderActualsCost', () => {
      this.loadWorkOrderCostList()
    })
  },
  methods: {
    async deleteAdditionalCost(additionalCostId) {
      this.isLoading = true
      let moduleName = 'workorderCost'
      let { error } = await API.deleteRecord(moduleName, additionalCostId)
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.$message.success(
          this.$t('common.workorder_actuals.add_cost_deleted_success')
        )
        this.isLoading = true
        this.loadWorkOrderCostList()
      }
    },
    async saveRecords() {
      let { additionalCosts, workorder } = this || {}
      let { id } = workorder || {}
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
          }
        })
        let url = 'v3/modules/bulkCreate/workorderCost'
        let params = {
          data: {
            workorderCost: workorderCost,
          },
          moduleName: 'workorderCost',
        }
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.$message.success(
            this.$t('common.workorder_actuals.add_cost_added_success')
          )
          this.loadWorkOrderCostList()
          this.closeDialog()
        }
      } else {
        this.$message.error(
          this.$t('common.workorder_actuals.please_add_additional_cost')
        )
      }
    },
    removeAdditionalCost(index) {
      let { additionalCosts } = this
      additionalCosts.splice(index, 1)
    },
    closeDialog() {
      this.additionalCosts = [
        {
          name: null,
          cost: null,
        },
      ]
      this.additionalCostDialogVisibility = false
    },
    newAdditionalCost() {
      let { additionalCosts } = this
      let additionalCost = { name: null, cost: null }
      additionalCosts.push(deepCloneObject(additionalCost))
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['actualsCostTable']
        if (container) {
          let height = container.scrollHeight + 140
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
    addAdditionalCost() {
      this.additionalCostDialogVisibility = true
    },
    async loadWorkOrderCostList() {
      this.isLoading = true
      let { workorder } = this || {}
      let { id } = workorder || {}
      let params = {
        filters: JSON.stringify({
          parentId: {
            operatorId: 9,
            value: [`${id}`],
          },
        }),
      }
      let { list, error } = await API.fetchAll(`workorderCost`, params)
      if (!isEmpty(error)) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        let totalCostOfCustomCost = 0
        let customCostList = list.filter(
          element => element.costTypeEnum === 'custom'
        )
        this.customCostList = customCostList
        if (!isEmpty(customCostList)) {
          customCostList.forEach(element => {
            totalCostOfCustomCost += element.cost || 0
          })
        }
        let laboursCostObj = list.find(element => element.costName === 'Labour')
        let itemsCostObj = list.find(element => element.costName === 'Items')
        let toolsCostObj = list.find(element => element.costName === 'Tools')
        let servicesCostObj = list.find(
          element => element.costName === 'Service'
        )
        this.labourCost = this.$getProperty(laboursCostObj, 'cost', 0)
        this.itemCost = this.$getProperty(itemsCostObj, 'cost', 0)
        this.toolCost = this.$getProperty(toolsCostObj, 'cost', 0)
        this.serviceCost = this.$getProperty(servicesCostObj, 'cost', 0)
        let { labourCost, itemCost, toolCost, serviceCost } = this || {}
        this.grossAmount =
          labourCost + itemCost + toolCost + serviceCost + totalCostOfCustomCost
        this.isLoading = false
        this.autoResize()
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.footer-btns {
  text-align: right;
  padding-top: 20px;
}

.additional-cost-name {
  max-width: 10px;
}
.addtional-cost {
  &:hover {
    .additionalCostDelete {
      display: block;
    }
  }
}
.additionalCostDelete {
  display: none;
  font-size: 15px;
  color: #e1573f;
  font-weight: 900;
  right: 5px;
  top: 0px;
  cursor: pointer;
}
.cost-title {
  border-bottom: 1px solid #e5eaee;
}
.grand-total {
  margin-top: auto;
  margin-bottom: auto;
  padding-right: 25px;
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
.width210px {
  width: 210px !important;
}
table tr {
  border-bottom: 1px solid #e5eaee;
}
.fw-550 {
  font-weight: 550;
}
.cost-color {
  color: #808998;
  font-weight: 400;
}
.add-color {
  padding-left: 170px;
  padding-top: 23px;
  color: #2b8bff;
  font-weight: 400;
  .plus-icon {
    color: #2b8bff;
  }
}
.mT90 {
  margin-top: 90px !important;
}
</style>
