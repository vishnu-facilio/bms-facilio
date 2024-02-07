<template>
  <div class="p30 white-bg-block">
    <!-- service logo -->
    <div class="inline-flex">
      <div class="fc-v1-icon-bg">
        <InlineSvg
          src="svgs/service"
          iconClass="icon icon-lg fill-white"
        ></InlineSvg>
      </div>
      <div class="fc-black3-16 mL10 mT12">
        {{ $t('common.products.services') }}
      </div>
    </div>

    <!-- widget body -->
    <div class="inventory-table pT20" ref="plannedServicesWidget">
      <!-- loading spinner -->
      <table class="width100" v-if="plannedServicesloading">
        <tr>
          <td colspan="100%" class="iTloading in-no-data">
            <spinner :show="true" size="80"></spinner>
          </td>
        </tr>
      </table>

      <!-- service table -->
      <table class="width100 inventory-table" v-else>
        <thead>
          <tr class="border-top-none">
            <th class="border-top-none tool-header-sticky" style="width: 30%">
              {{ $t('common.products._service') }}
            </th>
            <th style="width: 25%;">{{ $t('common._common.description') }}</th>
            <th class="pR0 border-top-none">
              {{ $t('common._common._quantity') }}
            </th>
            <th class="text-right width130px border-top-none">
              {{ $t('common._common.duration_hr') }}
            </th>
            <th class="text-right border-top-none width130px pR20 pL20">
              {{ $t('common.header._price') }}
            </th>
            <th class="text-right worktool-cost-sticky border-top-none">
              {{ $t('common.tabs._cost') }}
            </th>
            <th
              class="width40px worktool-cost-edit-sticky border-top-none"
            ></th>
          </tr>
        </thead>
        <tbody v-if="!plannedInventoryServices.length">
          <tr>
            <td
              @click="addServices()"
              class="inventory-td-selected pL20 pT10 pB10"
            >
              {{ $t('common.header.add_service') }}
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
            <td class="pT10 pB10 fc-grey3-13 text-right">
              <currency :value="0"></currency>
            </td>
            <td class="pT10 pB10 fc-grey3-13 text-right">
              <currency :value="0"></currency>
            </td>
            <td class="pT10 pB10"></td>
          </tr>
        </tbody>
        <tbody v-else>
          <template
            v-for="(plannedInventoryService, index) in plannedInventoryServices"
          >
            <tr
              :key="plannedInventoryService.id"
              class="border-top4 border-bottom9 visibility-visible-actions pointer"
            >
              <td class="width180px">
                <div v-if="plannedInventoryService.id > -1" class="pL17">
                  <div>
                    {{ $getProperty(plannedInventoryService, 'service.name') }}
                  </div>
                </div>
                <el-input v-else type="text" class="fc-input-full-border2">
                  <i
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                    @click="addServices()"
                  ></i>
                </el-input>
              </td>
              <td class="pL17">
                {{
                  $getProperty(
                    plannedInventoryService,
                    'service.description',
                    '---'
                  )
                }}
              </td>
              <td>
                <el-input
                  v-model="plannedInventoryService.quantity"
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  @change="updateQuantity(plannedInventoryService)"
                ></el-input>
              </td>
              <td>
                <el-input
                  v-if="!fixedDuration(plannedInventoryService)"
                  v-model="plannedInventoryService.duration"
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  @change="updateDuration(plannedInventoryService)"
                ></el-input>
                <el-input
                  v-else
                  v-model="plannedInventoryService.duration"
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  disabled
                ></el-input>
              </td>
              <td>
                <currency
                  v-if="getPrice(plannedInventoryService) !== '---'"
                  class="text-right pR10"
                  :value="getPrice(plannedInventoryService)"
                ></currency>
                <div v-else class="text-center">{{ '---' }}</div>
              </td>
              <td class="text-right">
                <currency
                  v-if="totalCost(plannedInventoryService) !== '---'"
                  :value="totalCost(plannedInventoryService)"
                ></currency>
                <div v-else class="text-center">{{ '---' }}</div>
              </td>
              <td class="text-right">
                <i
                  class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_service')"
                  v-tippy
                  @click="deletePlannedService(plannedInventoryService, index)"
                ></i>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <!-- widget footer -->
    <div class="item-add" v-if="!plannedServicesloading">
      <div class="green-txt-13 fc-v1-add-txt pointer fL">
        <span
          @click="addServices()"
          v-if="plannedInventoryServices.length"
          class="mR20"
        >
          <img src="~assets/add-icon.svg" />
          {{ $t('common.header.add_service') }}
        </span>
      </div>
      <div class="fR inline-flex mR30">
        <div class="bold mR60">{{ $t('common.header._total') }}</div>
        <div class="fc-black3-16 text-right bold pR7">
          <currency :value="servicesTotalCost"></currency>
        </div>
      </div>
    </div>

    <AddPlannedServices
      v-if="showAddServicesDialog"
      :moduleName="'jobPlanServices'"
      :jobPlanId="details.id"
      :onClose="() => ((showAddServicesDialog = false), getPlannedServices())"
    >
    </AddPlannedServices>
  </div>
</template>
<script>
import InlineSvg from '@/InlineSvg'
import { API } from '@facilio/api'
import AddPlannedServices from './AddPlannedServices.vue'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  name: 'PlannedInventoryServices',
  components: {
    InlineSvg,
    AddPlannedServices,
  },
  props: ['details', 'resizeWidget'],
  data() {
    return {
      plannedServicesloading: false,
      plannedInventoryServices: [],
      showAddServicesDialog: false,
    }
  },
  created() {
    this.getPlannedServices()
  },
  mounted() {
    this.autoResize()
  },
  computed: {
    servicesTotalCost() {
      let { plannedInventoryServices } = this
      let totalCost = 0
      for (let service of plannedInventoryServices) {
        let { cost } = service || {}
        if (!isEmpty(cost)) {
          totalCost += cost
        }
      }
      eventBus.$emit('servicesTotalCost', totalCost.toFixed(2))
      return totalCost
    },
  },
  methods: {
    fixedDuration(plannedInventoryService) {
      let paymentTypeEnum = this.$getProperty(
        plannedInventoryService,
        'service.paymentTypeEnum'
      )
      return paymentTypeEnum === 'FIXED'
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['plannedServicesWidget']
        if (container) {
          let height = container.scrollHeight + 180
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
    addServices() {
      this.showAddServicesDialog = true
    },
    getPrice(plannedInventoryService) {
      let price = this.$getProperty(
        plannedInventoryService,
        'service.buyingPrice'
      )
      return !isEmpty(price) ? price : '---'
    },
    async getPlannedServices() {
      this.plannedServicesloading = true
      let jobPlanid = this.$getProperty(this.details, 'id')
      let params = {
        filters: JSON.stringify({
          jobPlan: {
            operatorId: 36,
            value: [`${jobPlanid}`],
          },
        }),
      }
      let { list, error } = await API.fetchAll('jobPlanServices', params)
      if (error) {
        this.$message.error(
          error || this.$t('common._common.planned_services_list_error_msg')
        )
      } else {
        this.plannedInventoryServices = list
        this.plannedServicesloading = false
        this.autoResize()
      }
    },
    addService() {
      this.showAddServicesDialog = true
    },
    totalCost(plannedInventoryService) {
      let quantity = this.$getProperty(plannedInventoryService, 'quantity')
      let price = this.$getProperty(
        plannedInventoryService,
        'service.buyingPrice'
      )
      let duration = this.$getProperty(plannedInventoryService, 'duration')
      let cost = 0
      if (this.fixedDuration(plannedInventoryService)) {
        cost = !isEmpty(price) ? quantity * price : '---'
      } else {
        cost =
          !isEmpty(price) && !isEmpty(duration)
            ? quantity * price * duration
            : '---'
      }

      if (cost !== '---') {
        plannedInventoryService.cost = cost
      }

      return cost
    },
    async updateQuantity(plannedInventoryService) {
      let { quantity, id } = plannedInventoryService || {}
      let data = { quantity: quantity }
      await this.updatePlannedService(id, data)
    },
    async updateDuration(plannedInventoryService) {
      let { duration, id } = plannedInventoryService || {}
      let data = { duration: duration }
      await this.updatePlannedService(id, data)
    },
    async updatePlannedService(id, data) {
      let { error } = await API.updateRecord('jobPlanServices', {
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

        this.getPlannedServices()
        eventBus.$emit('reloadOverallCost')
      }
    },
    async deletePlannedService(plannedInventoryService, index) {
      let { id } = plannedInventoryService
      let { error } = await API.deleteRecord('jobPlanServices', [id])
      if (error) {
        this.$message.error(
          error || this.$t('common._common.error_occurred_delete')
        )
      } else {
        this.plannedInventoryServices.splice(index, 1)
        this.$message.success(this.$t('common._common.service_del'))
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
