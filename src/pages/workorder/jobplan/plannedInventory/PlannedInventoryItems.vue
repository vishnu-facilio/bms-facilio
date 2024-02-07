<template>
  <div class="p30 white-bg-block">
    <!-- items logo -->
    <div class="inline-flex">
      <div class="fc-v1-icon-bg">
        <InlineSvg
          src="svgs/items"
          iconClass="icon icon-lg stroke-white"
        ></InlineSvg>
      </div>
      <div class="fc-black3-16 mL10 mT12">
        {{ $t('common.products.items') }}
      </div>
    </div>

    <!-- widget body -->
    <div class="inventory-table pT20 " ref="plannedItemsWidget">
      <!-- loading spinner -->
      <table class="width100" v-if="plannedItemsloading">
        <tr>
          <td colspan="100%" class="iTloading in-no-data">
            <spinner :show="true" size="80"></spinner>
          </td>
        </tr>
      </table>

      <!-- items table -->
      <table class="width100 inventory-table" v-else>
        <thead>
          <tr>
            <th class="header-width20">{{ $t('common.header._item') }}</th>
            <th class="header-width20">
              {{ $t('common._common.description') }}
            </th>
            <th class="header-width20">
              {{ $t('common.products._storeroom') }}
            </th>
            <th class="header-width15">{{ $t('common._common._quantity') }}</th>
            <th class="header-width10">
              {{ $t('common.header._unit_price') }}
            </th>
            <th class="text-right header-width10">
              {{ $t('common.tabs._cost') }}
            </th>
            <th class="width40px"></th>
          </tr>
        </thead>
        <tbody v-if="!plannedInventoryItems.length">
          <tr>
            <td @click="addItems()" class="pL20">
              {{ $t('common.header.add_item') }}
            </td>
            <td class="pL20">
              {{ '---' }}
            </td>
            <td class="pL20">
              <FLookupFieldWrapper
                :field="storeRoomLookupData"
                disabled
              ></FLookupFieldWrapper>
            </td>
            <td>
              <el-input
                placeholder
                type="number"
                class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                disabled
              ></el-input>
            </td>
            <td class="fc-grey3-13 text-right">
              <currency :value="0"></currency>
            </td>
            <td class="fc-grey3-13 text-right">
              <currency :value="0"></currency>
            </td>
            <td></td>
          </tr>
        </tbody>
        <tbody v-else>
          <template
            v-for="(plannedInventoryItem, index) in plannedInventoryItems"
          >
            <tr
              :key="plannedInventoryItem.id"
              class="border-top4 border-bottom9 visibility-visible-actions pointer"
            >
              <td class="width180px">
                <div v-if="plannedInventoryItem.id > -1" class="pL17">
                  {{ $getProperty(plannedInventoryItem, 'itemType.name') }}
                </div>
                <el-input
                  v-else
                  type="text"
                  :placeholder="$t('common.header.item_name')"
                  class="fc-input-full-border2"
                >
                  <i
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                    @click="addItems()"
                  ></i>
                </el-input>
              </td>
              <td class="pL17">
                {{
                  $getProperty(
                    plannedInventoryItem,
                    'itemType.description',
                    '---'
                  )
                }}
              </td>
              <td class="pL20">
                <FLookupFieldWrapper
                  v-model="plannedInventoryItem.storeRoom.id"
                  :field="storeRoomLookupData"
                  @recordSelected="updateStoreRoom(plannedInventoryItem)"
                ></FLookupFieldWrapper>
              </td>
              <td>
                <el-input
                  v-model="plannedInventoryItem.quantity"
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  @change="updateQuantity(plannedInventoryItem)"
                ></el-input>
              </td>
              <td>
                <currency
                  v-if="getUnitPrice(plannedInventoryItem) !== '---'"
                  class="text-right"
                  :value="getUnitPrice(plannedInventoryItem)"
                ></currency>
                <div v-else class="text-right">{{ '---' }}</div>
              </td>
              <td>
                <currency
                  v-if="totalCost(plannedInventoryItem) !== '---'"
                  class="text-right"
                  :value="totalCost(plannedInventoryItem)"
                ></currency>
                <div v-else class="text-right">{{ '---' }}</div>
              </td>
              <td class="text-right">
                <i
                  class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_item')"
                  v-tippy
                  @click="deletePlannedItem(plannedInventoryItem, index)"
                ></i>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <!-- widget footer -->
    <div class="item-add" v-if="!plannedItemsloading">
      <div class="green-txt-13 fc-v1-add-txt pointer fL">
        <span
          @click="addItems()"
          v-if="plannedInventoryItems.length"
          class="mR20"
        >
          <img src="~assets/add-icon.svg" />
          {{ $t('common.header.add_item') }}
        </span>
      </div>

      <div class="fR inline-flex mR30">
        <div class="bold mR60">{{ $t('common.header._total') }}</div>
        <div class="fc-black3-16 text-right bold pR7">
          <currency :value="itemsTotalCost"></currency>
        </div>
      </div>
    </div>

    <AddPlannedInventory
      v-if="showAddItemsDialog"
      :jobPlanId="details.id"
      :moduleName="'jobPlanItems'"
      :onClose="() => ((showAddItemsDialog = false), getPlannedItems())"
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
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
export default {
  name: 'PlannedInventoryItems',
  components: {
    InlineSvg,
    AddPlannedInventory,
    FLookupFieldWrapper,
  },
  props: ['details', 'resizeWidget'],
  data() {
    return {
      plannedItemsloading: false,
      plannedInventoryItems: [],
      showAddItemsDialog: false,
      selectedStoreroom: null,
      storeRoomLookupData: {
        lookupModule: { name: 'storeRoom' },
        multiple: false,
      },
    }
  },
  created() {
    this.getPlannedItems()
  },
  mounted() {
    this.autoResize()
  },
  computed: {
    itemsTotalCost() {
      let { plannedInventoryItems } = this
      let totalCost = 0
      for (let item of plannedInventoryItems) {
        let { cost } = item || {}
        if (!isEmpty(cost)) {
          totalCost += cost
        }
      }
      eventBus.$emit('itemsTotalCost', totalCost.toFixed(2))
      return totalCost
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['plannedItemsWidget']
        if (container) {
          let height = container.scrollHeight + 180
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
    getUnitPrice(plannedInventoryItem) {
      let unitPrice = this.$getProperty(
        plannedInventoryItem,
        'itemType.lastPurchasedPrice'
      )
      return !isEmpty(unitPrice) ? unitPrice : '---'
    },
    async getPlannedItems() {
      this.plannedItemsloading = true
      let jobPlanid = this.$getProperty(this.details, 'id')
      let params = {
        filters: JSON.stringify({
          jobPlan: {
            operatorId: 36,
            value: [`${jobPlanid}`],
          },
        }),
      }
      let { list, error } = await API.fetchAll('jobPlanItems', params)
      if (error) {
        this.$message.error(
          error || this.$t('common._common.planned_items_list_error_msg')
        )
      } else {
        for (let plannedInventoryItem of list) {
          let storeRoomId = this.$getProperty(
            plannedInventoryItem,
            'storeRoom.id'
          )
          if (isEmpty(storeRoomId)) {
            let id = { id: null }
            plannedInventoryItem.storeRoom = id
          }
        }
        this.plannedInventoryItems = list
        this.plannedItemsloading = false
        this.autoResize()
      }
    },
    addItems() {
      this.showAddItemsDialog = true
    },
    totalCost(plannedInventoryItem) {
      let quantity = this.$getProperty(plannedInventoryItem, 'quantity')
      let unitPrice = this.$getProperty(
        plannedInventoryItem,
        'itemType.lastPurchasedPrice'
      )

      let cost = !isEmpty(unitPrice) ? quantity * unitPrice : '---'
      if (cost !== '---') {
        plannedInventoryItem.cost = cost
      }

      return cost
    },
    async updateQuantity(plannedInventoryItem) {
      let { quantity, id } = plannedInventoryItem || {}
      let { error } = await API.updateRecord('jobPlanItems', {
        id: id,
        data: { quantity: quantity },
      })
      if (error) {
        this.$message.error(
          error || this.$t('common._common.unable_to_update_quantity')
        )
      } else {
        this.$message.success(
          this.$t('common._common.quantity_updated_successfully')
        )
        this.getPlannedItems()
        eventBus.$emit('reloadOverallCost')
      }
    },
    async updateStoreRoom(plannedInventoryItem) {
      let { id, storeRoom } = plannedInventoryItem || {}
      let { error } = await API.updateRecord('jobPlanItems', {
        id: id,
        data: {
          storeRoom: storeRoom,
        },
      })
      if (error) {
        this.$message.error(
          error || this.$t('common._common.unable_to_update_storeroom')
        )
      } else {
        this.$message.success(
          this.$t('common._common.storeroom_updated_successfully')
        )
        this.getPlannedItems()
        eventBus.$emit('reloadOverallCost')
      }
    },
    async deletePlannedItem(plannedInventoryItem, index) {
      let { id } = plannedInventoryItem
      let { error } = await API.deleteRecord('jobPlanItems', [id])
      if (error) {
        this.$message.error(
          error || this.$t('common._common.error_occurred_delete')
        )
      } else {
        this.plannedInventoryItems.splice(index, 1)
        this.$message.success(this.$t('common._common.item_del'))
        eventBus.$emit('reloadOverallCost')
        this.autoResize()
        this.selectedStoreroom = null
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
.header-width20 {
  width: 20%;
}
.header-width15 {
  width: 15%;
}
.header-width10 {
  width: 10%;
}
</style>
