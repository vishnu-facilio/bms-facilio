<template>
  <div class="p30 white-bg-block">
    <!-- tools logo -->
    <div class="inline-flex">
      <div class="fc-v1-icon-bg">
        <InlineSvg
          src="svgs/settings3"
          iconClass="icon icon-md stroke-white"
        ></InlineSvg>
      </div>
      <div class="fc-black3-16 mL10 mT12">
        {{ $t('common.header.tools') }}
      </div>
    </div>

    <!-- widget body -->
    <div class="inventory-table pT20" ref="plannedToolsWidget">
      <!-- loading spinner -->
      <table class="width100" v-if="plannedToolsloading">
        <tr>
          <td colspan="100%" class="iTloading in-no-data">
            <spinner :show="true" size="80"></spinner>
          </td>
        </tr>
      </table>

      <!-- tools table -->
      <table class="width100 inventory-table" v-else>
        <thead>
          <tr class="border-top-none">
            <th class="border-top-none tool-header-sticky" style="width: 30%;">
              {{ $t('common.header._tool') }}
            </th>
            <th style="width: 25%;">{{ $t('common._common.description') }}</th>
            <th class="pR0 border-top-none">
              {{ $t('common._common._quantity') }}
            </th>
            <th class="text-right width130px border-top-none">
              {{ $t('common._common.duration_hr') }}
            </th>
            <th class="text-right border-top-none width130px pR20 pL20">
              {{ $t('common.header.rate_hr') }}
            </th>
            <th class="text-right worktool-cost-sticky border-top-none">
              {{ $t('common.tabs._cost') }}
            </th>
            <th
              class="width40px worktool-cost-edit-sticky border-top-none"
            ></th>
          </tr>
        </thead>
        <tbody v-if="!plannedInventoryTools.length">
          <tr>
            <td
              @click="addTools()"
              class="inventory-td-selected pL20 pT10 pB10"
            >
              <div>{{ $t('common.products.add_tool') }}</div>
            </td>
            <td class="pL20">
              {{ '---' }}
            </td>
            <td class="pT10 pB10">
              <el-input
                placeholder
                type="number"
                class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                disabled
              ></el-input>
            </td>
            <td class="pT10 pB10 text-right">
              <el-input
                placeholder
                type="number"
                class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                disabled
              ></el-input>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency :value="0"></currency>
              </div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency :value="0"></currency>
              </div>
            </td>
            <td class="pT10 pB10"></td>
          </tr>
        </tbody>
        <tbody v-else>
          <template
            v-for="(plannedInventoryTool, index) in plannedInventoryTools"
          >
            <tr
              :key="plannedInventoryTool.id"
              class="border-top4 border-bottom9 visibility-visible-actions pointer"
            >
              <td class="width180px">
                <div v-if="plannedInventoryTool.id > -1" class="pL17">
                  {{ $getProperty(plannedInventoryTool, 'toolType.name') }}
                </div>
                <el-input v-else type="text" class="fc-input-full-border2">
                  <i
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                    @click="addTools()"
                  ></i>
                </el-input>
              </td>
              <td class="pL17">
                {{
                  $getProperty(
                    plannedInventoryTool,
                    'toolType.description',
                    '---'
                  )
                }}
              </td>
              <td>
                <el-input
                  v-model="plannedInventoryTool.quantity"
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  @change="updateQuantity(plannedInventoryTool)"
                ></el-input>
              </td>
              <td>
                <el-input
                  v-model="plannedInventoryTool.duration"
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  @change="updateDuration(plannedInventoryTool)"
                ></el-input>
              </td>
              <td>
                <currency
                  v-if="getRate(plannedInventoryTool) !== '---'"
                  class="text-right pR10"
                  :value="getRate(plannedInventoryTool)"
                ></currency>
                <div v-else class="text-center">{{ '---' }}</div>
              </td>
              <td class="text-right">
                <currency
                  v-if="totalCost(plannedInventoryTool) !== '---'"
                  :value="totalCost(plannedInventoryTool)"
                ></currency>
                <div v-else class="text-center">{{ '---' }}</div>
              </td>
              <td class="text-right">
                <i
                  class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_tool')"
                  v-tippy
                  @click="deletePlannedTool(plannedInventoryTool, index)"
                ></i>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <!-- widget footer -->
    <div class="item-add" v-if="!plannedToolsloading">
      <div class="green-txt-13 fc-v1-add-txt pointer fL">
        <span
          @click="addTools()"
          v-if="plannedInventoryTools.length"
          class="mR20"
        >
          <img src="~assets/add-icon.svg" />
          {{ $t('common.products.add_tool') }}
        </span>
      </div>

      <div class="fR inline-flex mR30">
        <div class="bold mR60">{{ $t('common.header._total') }}</div>
        <div class="fc-black3-16 text-right bold pR7">
          <currency :value="toolsTotalCost"></currency>
        </div>
      </div>
    </div>

    <AddPlannedInventory
      v-if="showAddToolsDialog"
      :jobPlanId="details.id"
      :moduleName="'jobPlanTools'"
      :onClose="() => ((showAddToolsDialog = false), getPlannedTools())"
    >
    </AddPlannedInventory>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
import { API } from '@facilio/api'
import AddPlannedInventory from './AddPlannedInventory.vue'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  name: 'PlannedInventoryTools',
  components: {
    InlineSvg,
    AddPlannedInventory,
  },
  props: ['details', 'resizeWidget'],
  data() {
    return {
      plannedToolsloading: false,
      plannedInventoryTools: [],
      showAddToolsDialog: false,
    }
  },
  created() {
    this.getPlannedTools()
  },
  mounted() {
    this.autoResize()
  },
  computed: {
    toolsTotalCost() {
      let { plannedInventoryTools } = this
      let totalCost = 0
      for (let tool of plannedInventoryTools) {
        let { cost } = tool || {}
        if (!isEmpty(cost)) {
          totalCost += cost
        }
      }
      eventBus.$emit('toolsTotalCost', totalCost.toFixed(2))
      return totalCost
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['plannedToolsWidget']
        if (container) {
          let height = container.scrollHeight + 180
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
    getRate(plannedInventoryTool) {
      let unitPrice = this.$getProperty(
        plannedInventoryTool,
        'toolType.sellingPrice'
      )
      return !isEmpty(unitPrice) ? unitPrice : '---'
    },
    async getPlannedTools() {
      this.plannedToolsloading = true
      let jobPlanid = this.$getProperty(this.details, 'id')
      let params = {
        filters: JSON.stringify({
          jobPlan: {
            operatorId: 36,
            value: [`${jobPlanid}`],
          },
        }),
      }
      let { list, error } = await API.fetchAll('jobPlanTools', params)
      if (error) {
        this.$message.error(
          error || this.$t('common._common.planned_tools_list_error_msg')
        )
      } else {
        this.plannedInventoryTools = list
        this.plannedToolsloading = false
        this.autoResize()
      }
    },
    addTools() {
      this.showAddToolsDialog = true
    },
    totalCost(plannedInventoryTool) {
      let quantity = this.$getProperty(plannedInventoryTool, 'quantity')
      let unitPrice = this.$getProperty(
        plannedInventoryTool,
        'toolType.sellingPrice'
      )
      let duration = this.$getProperty(plannedInventoryTool, 'duration')
      let cost =
        !isEmpty(unitPrice) && !isEmpty(duration)
          ? quantity * unitPrice * duration
          : '---'
      if (cost !== '---') {
        plannedInventoryTool.cost = cost
      }

      return cost
    },
    async updateQuantity(plannedInventoryTool) {
      let { quantity, id } = plannedInventoryTool || {}
      let data = { quantity: quantity }
      await this.updatePlannedTool(id, data)
    },
    async updateDuration(plannedInventoryTool) {
      let { duration, id } = plannedInventoryTool || {}
      let data = { duration: duration }
      await this.updatePlannedTool(id, data)
    },
    async updatePlannedTool(id, data) {
      let { error } = await API.updateRecord('jobPlanTools', {
        id: id,
        data: data,
      })
      if (error) {
        let { quantity } = data || {}
        let errorMsg = !isEmpty(quantity)
          ? this.$t('common._common.unable_to_update_quantity')
          : this.$t('common._common.unable_to_update_duration')
        this.$message.error(error || errorMsg)
      } else {
        let { quantity } = data || {}
        let successMsg = !isEmpty(quantity)
          ? this.$t('common._common.quantity_updated_successfully')
          : this.$t('common._common.duration_updated_successfully')
        this.$message.success(successMsg)

        this.getPlannedTools()
        eventBus.$emit('reloadOverallCost')
      }
    },
    async deletePlannedTool(plannedInventoryTool, index) {
      let { id } = plannedInventoryTool
      let { error } = await API.deleteRecord('jobPlanTools', [id])
      if (error) {
        this.$message.error(
          error || this.$t('common._common.error_occurred_delete')
        )
      } else {
        this.plannedInventoryTools.splice(index, 1)
        this.$message.success(this.$t('common._common.tool_del'))
        eventBus.$emit('reloadOverallCost')
        this.autoResize()
      }
    },
  },
}
</script>
<style scoped>
.item-add {
  padding-top: 20px;
  padding-bottom: 20px;
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
.inventory-table td {
  padding-top: 10px;
  padding-bottom: 10px;
}
.inventory-table table > tbody tr:last-child {
  border-bottom: 1px solid #eceef1 !important;
}
.inventory-table-body-border-none table > tbody tr:last-child {
  border-bottom: none !important;
}
.inventory-table-body-border-none th:last-child {
  border-right: none !important;
}
.workItem-quantity {
  width: 80px;
  text-align: center;
  height: 40px;
  align-items: center;
  padding-top: 10px;
  cursor: no-drop;
}
.inv-delet-icon {
  color: #e1573f;
  font-size: 14px;
}
.tool-header-sticky {
  position: sticky;
  left: 0;
  z-index: 200;
  background: #fff;
}
</style>
