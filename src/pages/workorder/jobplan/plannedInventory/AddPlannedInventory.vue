<template>
  <el-dialog
    :visible="true"
    :fullscreen="false"
    :before-close="onClose"
    open="top"
    custom-class="fc-dialog-up Inventoryaddvaluedialog"
    :append-to-body="true"
    width="55%"
  >
    <div>
      <div class="new-header-container pL30">
        <div class="fc-setup-modal-title">
          {{ getTitle }}
        </div>
      </div>
      <div
        class="new-header-container popup-container"
        v-if="inventoryList.length"
      >
        <div class="search-container">
          <el-input
            :autofocus="true"
            v-model="inventorySearchQuerry"
            type="text"
            :placeholder="$t('common._common.search')"
            class="el-input-textbox-full-border"
            @change="loadInventory"
          >
            <span class="sub-divider">|</span>
            <i
              slot="suffix"
              class="el-input__icon el-icon-search pointer search-text"
            ></i>
          </el-input>
        </div>
        <div class="pagination">
          <pagination
            v-if="newItemPartToggle"
            :total="listCount"
            :perPage="20"
            :hidePopover="true"
            @onPageChanged="onPageChange"
          ></pagination>
        </div>
      </div>
      <div class="fc-inv-container-body">
        <table class="width100" v-if="inventoryListLoading">
          <tr>
            <td colspan="100%">
              <div class="iTloading in-no-data">
                <spinner :show="true" size="80"></spinner>
              </div>
            </td>
          </tr>
        </table>

        <table
          v-else
          class="setting-list-view-table width100 invent-table-dialog inventor-store-adde-table"
        >
          <tbody v-if="!inventoryList.length">
            <tr>
              <td colspan="100%">
                <div
                  class="flex-middle justify-content-center flex-direction-column"
                >
                  <inline-svg
                    src="svgs/emptystate/inventory"
                    iconClass="icon text-center icon-100"
                  ></inline-svg>
                  <div class="nowo-label">
                    {{ getEmptyMessage }}
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <template>
              <tr
                class="tablerow asset-hover-td height100px"
                v-for="(inventory, index) in inventoryList"
                :key="inventory.id"
              >
                <td class="width80">
                  <div>{{ $getProperty(inventory, 'name') }}</div>

                  <div
                    class="item-description"
                    :class="getSubjectClassname(inventory, index)"
                  >
                    {{ $getProperty(inventory, 'description') }}
                  </div>
                  <div class="flex flex-direction-row pT6">
                    <a
                      v-if="canShowMore(inventory)"
                      @click="toggleVisibility(index)"
                      class="text-capitalize letter-spacing0_3 f13 pR8 pointer"
                      >{{ showMoreLinkText(index) }}</a
                    >
                  </div>
                </td>
                <td class="width160px text-align-inventory">
                  <template v-if="inventory.addedQuantity === 0">
                    <el-button
                      size="mini"
                      class="f-number-input"
                      @click="inventoryAdd(inventory)"
                      >{{ $t('common._common._add') }}</el-button
                    >
                  </template>
                  <template v-else>
                    <el-input-number
                      size="mini"
                      class="f-number-input"
                      v-model="inventory.addedQuantity"
                      :min="0"
                      :max="inventory.quantity"
                      @change="check(inventory, index)"
                    ></el-input-number>
                  </template>
                </td>
              </tr>
            </template>
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
          @click="addPlannedItems()"
          >{{ $t('common._common._add') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { htmlToText } from '@facilio/utils/filters'
import Pagination from 'src/pages/workorder/workorders/v3/widgets/InventoryPagination.vue'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  name: 'AddPlannedInventory',
  props: ['onClose', 'jobPlanId', 'moduleName', 'workOrderId'],
  components: {
    Pagination,
  },
  data() {
    return {
      selectedIndex: [],
      selectedInventory: null,
      inventoryList: [],
      newItemPartToggle: true,
      inventorySearchQuerry: null,
      listCount: null,
      page: 1,
      searchPage: 1,
      inventoryListLoading: false,
    }
  },
  created() {
    this.loadInventory()
  },
  computed: {
    getTitle() {
      return ['jobPlanItems', 'workOrderPlannedItems'].includes(this.moduleName)
        ? this.$t('common.products.items_list')
        : this.$t('common.products.tools_list')
    },
    getEmptyMessage() {
      return ['jobPlanItems', 'workOrderPlannedItems'].includes(this.moduleName)
        ? this.$t('common.products.no_item_present')
        : this.$t('common.products.no_tools_present')
    },
  },
  methods: {
    check(inventory, index) {
      if (inventory.addedQuantity === 0) {
        if (!isEmpty(this.$getProperty(this.inventoryList, `${index}.checked`)))
          this.inventoryList[index].checked = true
      }
    },
    cancel() {
      this.onClose()
      this.newItemPartToggle = false
    },
    inventoryAdd(inventory) {
      inventory.addedQuantity = 1
      inventory.checked = true
    },
    onPageChange(pageNo) {
      let { inventorySearchQuerry } = this
      this.page = pageNo
      if (!isEmpty(inventorySearchQuerry)) {
        this.searchPage = pageNo
      }
      this.loadInventory()
    },
    async addPlannedItems() {
      let inventoryToBeAdded = this.inventoryList
        .filter(inventory => inventory.checked === true)
        .map(inventory => {
          let paramObj = {
            quantity: inventory.addedQuantity,
          }
          if (['jobPlanItems', 'jobPlanTools'].includes(this.moduleName)) {
            paramObj['jobPlan'] = { id: this.jobPlanId }
          } else {
            paramObj['workOrder'] = { id: this.workOrderId }
          }

          if (
            ['jobPlanItems', 'workOrderPlannedItems'].includes(this.moduleName)
          ) {
            paramObj['itemType'] = { id: inventory.id }
          } else if (
            ['jobPlanTools', 'workOrderPlannedTools'].includes(this.moduleName)
          ) {
            paramObj['toolType'] = { id: inventory.id }
            paramObj['duration'] = 1
          }
          return paramObj
        })
      if (inventoryToBeAdded.length > 0) {
        let { moduleName } = this
        let url = `v3/modules/bulkCreate/${moduleName}`
        let params = {
          data: {
            [this.moduleName]: inventoryToBeAdded,
          },
          moduleName: `${this.moduleName}`,
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
    async loadInventory() {
      this.inventoryListLoading = true
      let { page, inventorySearchQuerry, searchPage } = this
      let params = {
        page: page,
        perPage: 20,
        filters: JSON.stringify({
          isRotating: {
            operatorId: 15,
            value: ['false'],
          },
        }),
      }
      if (inventorySearchQuerry) {
        ;(params.filters = JSON.stringify({
          isRotating: {
            operatorId: 15,
            value: ['false'],
          },
          name: {
            operatorId: 5,
            value: [`${inventorySearchQuerry}`],
          },
        })),
          this.$set(params, 'page', searchPage)
      }
      let isItemTypes = ['jobPlanItems', 'workOrderPlannedItems'].includes(
        this.moduleName
      )
      let inventoryTypeName = isItemTypes ? 'itemTypes' : 'toolTypes'
      let { list, error } = await API.fetchAll(inventoryTypeName, params)
      if (error) {
        this.$message.error(
          error || this.$t('common._common.planned_inventory_list_error_msg')
        )
        return {}
      } else {
        this.listCount = list.length
        for (let inventory of list) {
          inventory.checked = false
          inventory.addedQuantity = 0
        }
        this.inventoryList = list
        this.inventoryListLoading = false
      }
    },
    showMoreLinkText(index) {
      let { selectedIndex } = this
      return selectedIndex.indexOf(index) !== -1 ? 'View Less' : 'View More'
    },
    canShowMore(itemType) {
      let description = this.$getProperty(itemType, 'description')
      if (description) {
        let descriptionLength = description.length
        let htmlToString = htmlToText(description).split(/\r\n|\r|\n/).length
        return htmlToString > 2 || descriptionLength > 130
      } else {
        return false
      }
    },
    getSubjectClassname(itemType, index) {
      if (this.canShowMore(itemType)) {
        if (this.selectedIndex.indexOf(index) !== -1) {
          return 'description-content description-content-viewMore'
        } else {
          return 'description-content description-content-viewLess'
        }
      } else {
        return 'popover-description'
      }
    },
    toggleVisibility(index) {
      let { selectedIndex } = this
      if (selectedIndex.indexOf(index) === -1) {
        selectedIndex.push(index)
      } else {
        let indexOfDesc = selectedIndex.indexOf(index)
        selectedIndex.splice(indexOfDesc, 1)
      }
    },
  },
}
</script>
<style>
.in-no-data {
  height: 100px;
  width: 100%;
  text-align: center;
  justify-content: center;
  display: flex;
  align-items: center;
}
.invent-table-dialog tbody tr.tablerow.active1 td:first-child {
  border-left: 3px solid #39b2c2 !important;
}
.fc-inv-container-body {
  height: 50vh;
  overflow: auto;
}
.invent-table-dialog .fc-setting-table-th setting-th-text {
  padding: 15px 30px;
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
.text-align-inventory {
  text-align: center;
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
.item-description {
  width: 520px;
  margin: 4px 0 0;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: normal;
  color: #959595;
}
.description-content {
  white-space: pre-line;
  overflow: hidden;
  margin-top: 2px;
}

.description-content-viewLess {
  max-height: 32px;
  transition: max-height 0.3s ease-in;
  word-break: break-word;
}
.description-content-viewMore {
  max-height: 500px;
  transition: max-height 0.2s ease-in;
  word-break: break-word;
}
.popover-description {
  display: -webkit-box;
  white-space: pre-line;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  width: 100%;
  word-break: break-word;
  letter-spacing: 0.5px;
  line-height: 1.43;
  margin-top: 2px;
}
a {
  color: #afb5b4;
}
.pagination {
  position: absolute;
  right: 10px;
}
.popup-container {
  width: 100%;
  padding: 10px 30px;
  border-bottom: 1px solid #edeeef;
  position: -webkit-sticky;
  position: sticky;
  background: #fff;
  display: flex;
  width: 100%;
  align-items: center;
}
.search-text {
  line-height: 0px !important;
  font-size: 16px !important;
}
</style>
