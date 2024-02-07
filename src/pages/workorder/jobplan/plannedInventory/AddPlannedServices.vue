<template>
  <el-dialog
    :visible="true"
    :fullscreen="false"
    open="top"
    :before-close="onClose"
    custom-class="fc-dialog-up Inventoryaddvaluedialog"
    :append-to-body="true"
  >
    <div class="new-header-container">
      <div class="fc-setup-modal-title">
        {{ $t('common.products.services_list') }}
      </div>
    </div>
    <div class="fc-inv-container-body">
      <spinner
        :show="inventoryListLoading"
        size="80"
        v-if="inventoryListLoading"
        class="flex-middle justify-content-center flex-direction-column"
      ></spinner>
      <table
        v-else
        class="setting-list-view-table width100 invent-table-dialog"
      >
        <thead v-if="serviceList.length">
          <th class="setting-table-th setting-th-text"></th>
          <th class="setting-table-th setting-th-text">
            {{ $t('common.products.name') }}
          </th>
          <th class="setting-table-th setting-th-text">
            {{ `Buying Price (${$currency})` }}
          </th>
        </thead>
        <tbody v-if="!serviceList.length">
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
                  {{ $t('common.products.no_services_present') }}
                </div>
              </div>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr
            class="tablerow asset-hover-td"
            v-for="service in serviceList"
            :key="service.id"
          >
            <td style="width:10%;">
              <el-checkbox v-model="service.checked"></el-checkbox>
            </td>
            <td>{{ service.name }}</td>
            <td>{{ service.buyingPrice || '---' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="modal-dialog-footer-parts-dialog">
      <el-button class="modal-btn-cancel" @click="cancel()">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        class="modal-btn-save"
        style="margin-left:0px !important;"
        type="primary"
        @click="addService()"
        >{{ $t('common._common._add') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  name: 'AddPlannedServices',
  props: ['onClose', 'jobPlanId', 'moduleName', 'workOrderId'],
  data() {
    return {
      inventoryListLoading: false,
      serviceList: [],
    }
  },
  created() {
    this.loadServices()
  },
  methods: {
    async loadServices() {
      this.inventoryListLoading = true
      let { list, error } = await API.fetchAll('service')
      if (error) {
        this.$message.error(
          error || this.$t('common._common.planned_services_list_error_msg')
        )
        return {}
      } else {
        this.listCount = list.length
        for (let inventory of list) {
          inventory.checked = false
        }
        this.serviceList = list
        this.inventoryListLoading = false
      }
    },
    cancel() {
      this.onClose()
    },
    async addService() {
      let { jobPlanId, workOrderId } = this
      let selectedServices = []
      if (this.moduleName === 'jobPlanServices') {
        selectedServices = this.serviceList.filter(
          service => service.checked === true
        )
        selectedServices = selectedServices.map(service => {
          let { id, duration } = service || {}
          return {
            jobPlan: { id: jobPlanId },
            service: { id: id },
            quantity: 1,
            duration: duration,
          }
        })
      } else if (this.moduleName === 'workOrderPlannedServices') {
        selectedServices = this.serviceList.filter(
          service => service.checked === true
        )
        selectedServices = selectedServices.map(service => {
          let { id, duration } = service || {}
          return {
            workOrder: { id: workOrderId },
            service: { id: id },
            quantity: 1,
            duration: duration,
          }
        })
      }
      if (selectedServices.length > 0) {
        let { moduleName } = this
        let url = `v3/modules/bulkCreate/${moduleName}`
        let params = {
          data: {
            [this.moduleName]: selectedServices,
          },
          moduleName: this.moduleName,
        }
        let { error } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_adding_inventory')
          )
        } else {
          this.$message.success(this.$t('common._common.added_successfully'))
          this.cancel()
          this.$emit('save')
          eventBus.$emit('reloadOverallCost')
        }
      } else {
        this.$message.error(this.$t('common._common.select_inventory_error'))
      }
    },
  },
}
</script>
