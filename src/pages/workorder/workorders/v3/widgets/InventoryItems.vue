<template>
  <div class="p30 white-bg-block  overflow-y-scroll" ref="inventoryItemsWidget">
    <!-- widget header -->
    <div class="flex-middle justify-content-space">
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

      <!-- inventory request button -->
      <div v-if="isNotPortal && actionRule && !inventoryRequestForWOLoading">
        <el-button
          :disabled="canDisable"
          :class="['fc-create-btn create-btn mL10', { disabled: canDisable }]"
          v-if="inventoryRequestList && inventoryRequestList.length > 0"
          @click="inventoryRequestDialogVisibility = true"
          :title="$t('common._common.view_inventory_request')"
          v-tippy
          data-size="small"
        >
          <InlineSvg
            src="svgs/view"
            iconClass="icon icon-md vertical-middle fc-white-color"
          ></InlineSvg>
        </el-button>
        <el-button
          :disabled="canDisable"
          :class="['fc-create-btn create-btn mL10', { disabled: canDisable }]"
          v-else
          @click="inventoryRequestDialogVisibility = true"
          :title="$t('common.products.new_inventory_request')"
          v-tippy
          data-size="small"
        >
          <i class="el-icon-plus white-color f12 fw-bold"></i>
        </el-button>
      </div>
    </div>

    <!-- widget body -->
    <div class="inventory-table pT20">
      <!-- loading spinner -->
      <table class="width100" v-if="itemnsloading">
        <tr>
          <td colspan="100%">
            <div class="iTloading in-no-data">
              <spinner :show="true" size="80"></spinner>
            </div>
          </td>
        </tr>
      </table>

      <!-- items table -->
      <table class="width100 inventory-tools-table inventory-table" v-else>
        <thead>
          <tr>
            <th style="width: 55%;">{{ $t('common.header._item') }}</th>
            <th class>{{ $t('common._common._quantity') }}</th>
            <th class="p10 text-right">
              {{ $t('common.header._unit_price') }}
            </th>
            <th class="text-right" style="width: 80px;">
              {{ $t('common.tabs._cost') }}
            </th>
            <th class="width40px"></th>
          </tr>
        </thead>
        <tbody v-if="!workorderItemsList.length">
          <tr>
            <td
              @click="actionRule ? addNewWOItem() : null"
              :class="{ disabled: !actionRule }"
              class="inventory-td-selected pL20"
              style="width: 63%;"
            >
              <div>{{ $t('common.header.add_item') }}</div>
            </td>
            <td>
              <div>
                <el-input
                  placeholder
                  type="number"
                  class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                  disabled
                ></el-input>
              </div>
            </td>
            <td>
              <div class="fc-grey3-13 text-right">
                <currency :value="0" :recordCurrency="recordCurrency"></currency>
              </div>
            </td>
            <td>
              <div class="fc-grey3-13 text-right">
                <currency :value="0" :recordCurrency="recordCurrency"></currency>
              </div>
            </td>
            <td></td>
          </tr>
        </tbody>
        <tbody v-else>
          <template v-for="(workItem, index) in workorderItemsList">
            <tr
              :key="workItem.id"
              class="border-top4 border-bottom9 visibility-visible-actions pointer disabled"
              v-if="workItem.approvedState === 2"
            >
              <td>
                <div class="width180px">
                  <div v-if="workItem.id > -1" class="pL17">
                    <div
                      v-if="workItem.item.itemType.isRotating && workItem.asset"
                      class="fc-id"
                      style="padding-bottom: 4px;"
                    >
                      #{{ workItem.asset.serialNumber }}
                    </div>
                    <div>
                      {{ workItem.item.itemType.name }}
                    </div>
                  </div>
                  <el-input
                    v-else
                    v-model="workItem.item.itemType.name"
                    type="text"
                    :placeholder="$t('common.header.item_name')"
                    class="fc-input-full-border2"
                  >
                    <i
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                      @click="addNewWOItem()"
                    ></i>
                  </el-input>
                </div>
              </td>
              <td>
                <div class="in-Quantity row">
                  <div class="item col-6">
                    <el-tooltip
                      class="item text-center quant"
                      effect="dark"
                      :content="
                        actionRule
                          ? `${$t(
                              'common.products.available_items'
                            )} ${getItemCount(workItem)}`
                          : $t('common.header.please_open_the_workorder')
                      "
                      placement="top"
                      v-if="!workItem.item.itemType.isRotating"
                      v-bind:class="{
                        exceeded: getItemQuantityState(workItem),
                      }"
                    >
                      <template>
                        <el-form
                          :model="workItem"
                          ref="numberValidateForm"
                          @submit.prevent.native="checkEnter"
                          class="width50px inventory-input-width"
                          v-bind:class="{
                            exceeded: getItemQuantityState(workItem),
                          }"
                        >
                          <el-form-item label prop="quantity">
                            <el-input
                              placeholder
                              v-model="workItem.quantity"
                              type="number"
                              @change="
                                changeWorkItemQuantity(
                                  workItem,
                                  workItem.item.quantity
                                )
                              "
                              class="fc-input-full-border2 width50px inventory-input-width75px text-right pL20"
                              :disabled="!actionRule"
                            ></el-input>
                          </el-form-item>
                        </el-form>
                      </template>
                    </el-tooltip>
                    <div class="item" v-else>
                      <div class="workItem-quantity">
                        {{ workItem.quantity }}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
              <td>
                <div>
                  <currency
                    v-if="workItem.item.itemType.isRotating"
                    class="text-right pR10"
                    :value="unitPrice(workItem)"
                    :recordCurrency="recordCurrency"
                  ></currency>
                  <currency
                    v-else
                    class="text-right pR10"
                    :value="workItem.purchasedItem.unitcost"
                    :recordCurrency="recordCurrency"
                  ></currency>
                </div>
              </td>
              <td>
                <div class="text-right">
                  <i
                    class="fa fa-lock f18"
                    data-arrow="true"
                    :title="$t('common.products.waiting_for_approval')"
                    v-tippy
                  ></i>
                </div>
              </td>
              <td class="text-right">
                <i
                  class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_item')"
                  v-tippy
                  @click="deleteWorkorderItem(workItem, index)"
                  v-if="actionRule"
                ></i>
              </td>
            </tr>
            <tr
              :key="workItem.id"
              class="border-top4 border-bottom9 visibility-visible-actions pointer"
              v-else-if="workItem.approvedState === 4"
            >
              <td class="break-word-all">
                <div class="width180px">
                  <div v-if="workItem.id > -1" class="pL17">
                    <div
                      v-if="workItem.item.itemType.isRotating && workItem.asset"
                      class="fc-id"
                      style="padding-bottom: 4px;"
                    >
                      #{{ workItem.asset.serialNumber }}
                    </div>
                    <div>
                      {{ workItem.item.itemType.name }}
                    </div>
                  </div>
                  <el-input
                    v-else
                    v-model="workItem.item.itemType.name"
                    type="text"
                    :placeholder="$t('common.header.item_name')"
                    class="fc-input-full-border2"
                  >
                    <i
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                      @click="addNewWOItem()"
                    ></i>
                  </el-input>
                </div>
              </td>
              <td>
                <div class="in-Quantity">
                  <div class="item col-6">
                    <div class="item">
                      <div class="workItem-quantity">
                        {{ workItem.quantity }}
                      </div>
                    </div>
                  </div>
                </div>
              </td>
              <td>
                <currency
                  v-if="workItem.item.itemType.isRotating"
                  class="text-right pR10"
                  :value="unitPrice(workItem)"
                  :recordCurrency="recordCurrency"
                ></currency>
                <currency
                  v-else
                  class="text-right pR10"
                  :value="workItem.purchasedItem.unitcost"
                  :recordCurrency="recordCurrency"
                ></currency>
              </td>
              <td>
                <div class="text-right">
                  <currency
                    :value="workItem.cost"
                    :recordCurrency="recordCurrency"
                  ></currency>
                </div>
              </td>
              <td class="text-right">
                <i
                  class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_item')"
                  v-tippy
                  @click="deleteWorkorderItem(workItem, index)"
                  v-if="actionRule"
                ></i>
              </td>
            </tr>
            <tr
              :key="workItem.id"
              class="border-top4 border-bottom9 visibility-visible-actions pointer"
              v-else
            >
              <td>
                <div class="width180px inv-table-subject">
                  <div v-if="workItem.id > -1" class="pL17">
                    <div
                      v-if="workItem.item.itemType.isRotating && workItem.asset"
                      class="fc-id"
                      style="padding-bottom: 4px;"
                    >
                      #{{ workItem.asset.serialNumber }}
                    </div>
                    <div>{{ workItem.item.itemType.name }}</div>
                  </div>
                  <el-input
                    v-else
                    v-model="workItem.item.itemType.name"
                    type="text"
                    :placeholder="$t('common.header.item_name')"
                    class="fc-input-full-border2"
                  >
                    <i
                      slot="suffix"
                      style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                      class="el-input__icon el-icon-search"
                      @click="addNewWOItem()"
                    ></i>
                  </el-input>
                </div>
              </td>
              <td>
                <div class="in-Quantity">
                  <el-tooltip
                    class="item text-center quant"
                    effect="dark"
                    :content="
                      actionRule
                        ? `${$t(
                            'common.products.available_items'
                          )} ${getItemCount(workItem)}`
                        : $t('common.header.please_open_the_workorder')
                    "
                    placement="top"
                    v-if="!workItem.item.itemType.isRotating"
                    v-bind:class="{
                      exceeded: getItemQuantityState(workItem),
                    }"
                  >
                    <template>
                      <el-form
                        :model="workItem"
                        ref="numberValidateForm"
                        @submit.prevent.native="checkEnter"
                        class="width50px inventory-input-width"
                        v-bind:class="{
                          exceeded: getItemQuantityState(workItem),
                        }"
                      >
                        <el-form-item label prop="quantity">
                          <el-input
                            placeholder
                            v-model="workItem.quantity"
                            type="number"
                            @change="
                              changeWorkItemQuantity(
                                workItem,
                                workItem.item.quantity
                              )
                            "
                            class="fc-input-full-border2 width50px inventory-input-width75px text-right pL20"
                            :disabled="!actionRule"
                          ></el-input>
                        </el-form-item>
                      </el-form>
                    </template>
                  </el-tooltip>
                  <div class="item" v-else>
                    <div class="workItem-quantity">
                      {{ workItem.quantity }}
                    </div>
                  </div>
                </div>
              </td>
              <td>
                <currency
                  v-if="workItem.item.itemType.isRotating"
                  class="text-right pR10"
                  :value="unitPrice(workItem)"
                  :recordCurrency="recordCurrency"
                ></currency>
                <currency
                  v-else
                  class="text-right pR10"
                  :value="workItem.purchasedItem.unitcost"
                  :recordCurrency="recordCurrency"
                ></currency>
              </td>
              <td>
                <div class="text-right">
                  <currency
                    :value="workItem.cost"
                    :recordCurrency="recordCurrency"
                  ></currency>
                </div>
              </td>
              <td class="text-right">
                <i
                  class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_item')"
                  v-tippy
                  @click="deleteWorkorderItem(workItem, index)"
                  v-if="actionRule"
                ></i>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <!-- widget footer -->
    <div class="item-add" v-if="itemnsloading"></div>
    <div class="item-add" v-else>
      <div class="fL" v-if="actionRule && isNotPortal">
        <div class="green-txt-13 fc-v1-add-txt pointer">
          <span
            @click="addNewWOItem()"
            v-if="workorderItemsList.length"
            class="mR20"
          >
            <img src="~assets/add-icon.svg" />
            {{ $t('common.header.add_item') }}
          </span>
          <span @click="issueItemIRDialogVisibility = true">
            <img src="~assets/add-icon.svg" />{{
              $t('common.products.add_issued_item')
            }}
          </span>
        </div>
      </div>
      <div class="fR inline-flex mR30">
        <div class="bold mR60">{{ $t('common.header._total') }}</div>
        <div class="fc-black3-16 text-right bold pR7">
          <currency
            :value="workItemTotalCost"
            :recordCurrency="recordCurrency"
          ></currency>
        </div>
      </div>
    </div>

    <el-dialog
      :visible.sync="newItemPartToggle"
      :fullscreen="false"
      open="top"
      custom-class="fc-dialog-up Inventoryaddvaluedialog"
      :append-to-body="true"
      width="55%"
    >
      <div v-if="individualTracking">
        <div class="new-header-container popup-container-new">
          <div class="fc-setup-modal-title">
            <i
              class="el-icon-back inv-id-bac-icon pointer"
              @click=";(individualItemList = []), (selectedInventory = null)"
            ></i>
            {{ selectedInventory.itemType.name }}
          </div>
          <div class="search-bar" v-bind:class="{ active: hidequrry }">
            <i
              slot="suffix"
              style="line-height:0px !important; font-size:16px !important;"
              class="el-input__icon el-icon-search"
              @click="hidequrry = false"
              v-if="hidequrry"
            ></i>
            <el-input
              :autofocus="true"
              v-model="itemSerachQuerry"
              type="text"
              :placeholder="$t('common._common.to_search_type')"
              class="el-input-textbox-full-border inv-search-grey"
              @blur="hidequrry = true"
              v-else
            >
              <i
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important;"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
          </div>
        </div>
        <div class="fc-inv-container-body">
          <table class="setting-list-view-table width100 invent-table-dialog">
            <thead>
              <th class="setting-table-th setting-th-text">
                {{ $t('common.header.s_no') }}
              </th>
              <th class="setting-table-th setting-th-text"></th>
            </thead>
            <tbody v-if="iTloading">
              <tr>
                <td colspan="100%">
                  <div class="iTloading">
                    <spinner :show="true" size="80"></spinner>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="!individualItemListwrapper.length">
              <tr>
                <td colspan="100%">
                  <div
                    class="flex-middle justify-content-center flex-direction-column"
                  >
                    <inline-svg
                      src="svgs/emptystate/inventory"
                      iconClass="icon text-center icon-100"
                    ></inline-svg>
                    <div class="f14 nowo-label">
                      {{ $t('common.products.no_item_present') }}
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow asset-hover-td"
                v-for="(item, idx) in individualItemListwrapper"
                :key="idx"
              >
                <td>
                  <div>{{ item.serialNumber }}</div>
                  <div></div>
                </td>
                <td class="text-right">
                  <template
                    v-if="
                      selectedInventory.invidualList.findIndex(
                        rt => rt.id === item.id
                      ) > -1
                    "
                  >
                    <el-button
                      size="mini"
                      class="f-number-input f-number-input-no-border"
                      @click="
                        selectedInventory.invidualList.splice(
                          selectedInventory.invidualList.findIndex(
                            rt => rt.id === item.id
                          ),
                          1
                        )
                      "
                      >{{ $t('common.products.added') }}</el-button
                    >
                  </template>
                  <template v-else-if="item.checked">
                    <el-button
                      size="mini"
                      class="f-number-input f-number-input-no-border"
                      @click="item.checked = false"
                      >{{ $t('common.products.added') }}</el-button
                    >
                  </template>
                  <template v-else>
                    <el-button
                      size="mini"
                      class="f-number-input"
                      @click="item.checked = true"
                      >{{ $t('common._common._add') }}</el-button
                    >
                  </template>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-dialog-footer-parts-dialog">
          <el-button
            class="modal-btn-save width100"
            style="margin-left:0px !important;"
            type="primary"
            @click="addindIvidualTrackingItem"
            :disabled="iTloading"
            >{{ $t('common._common._done') }}</el-button
          >
        </div>
      </div>
      <div v-else>
        <div class="new-header-container popup-container-new">
          <div class="fc-setup-modal-title">
            {{ $t('common.products.items_list') }}
          </div>
        </div>
        <div class="new-header-container popup-container">
          <div
            class="store-room-bar-item"
            v-bind:class="{ active: !hidequrry }"
          >
            <el-select
              v-model="selectedStoreList"
              collapse-tags
              style="margin-left: 20px;"
              :placeholder="$t('common.products.select_storeroom')"
              class="fc-tag fc-input-full-border2"
              @change="loadInventory"
            >
              <el-option
                v-for="(item, index) in tempStoreList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </div>
          <div class="search-container">
            <el-input
              :autofocus="true"
              v-model="itemSerachQuerry"
              type="text"
              :placeholder="$t('common._common.search')"
              class="el-input-textbox-full-border"
              @change="loadInventory"
            >
              <span class="sub-divider">|</span>
              <i
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important;"
                class="el-input__icon el-icon-search pointer"
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
          <table
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
                      {{ $t('common.products.no_item_present') }}
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <template>
                <tr
                  class="tablerow asset-hover-td height100px"
                  v-for="(invt, index) in inventoryList"
                  :key="invt.id"
                >
                  <td class="width80">
                    <div>{{ $getProperty(invt, 'itemType.name') }}</div>

                    <div
                      class="item-description"
                      :class="getSubjectClassname(invt, index)"
                    >
                      {{ $getProperty(invt, 'itemType.description') }}
                    </div>
                    <div class="flex flex-direction-row pT6">
                      <a
                        v-if="canShowMore(invt)"
                        @click="toggleVisibility(index)"
                        class="text-capitalize letter-spacing0_3 f13 pR8 pointer"
                        >{{ showMoreLinkText(index) }}</a
                      >
                    </div>
                  </td>
                  <td class="width160px text-align-inventory">
                    <template v-if="invt.itemType.isRotating">
                      <el-button
                        size="mini"
                        class="f-number-input f-number-input-no-border"
                        @click="
                          selectItem(invt, index)
                          invt.checked = true
                        "
                        v-if="invt.invidualList && invt.invidualList.length > 0"
                        >{{ invt.invidualList.length }}
                        {{ $t('common.products.qty_added') }}</el-button
                      >
                      <el-button
                        size="mini"
                        class="f-number-input"
                        @click="
                          selectItem(invt, index)
                          invt.checked = true
                          itemSerachQuerry = null
                        "
                        v-else
                        >{{ $t('common._common._add') }}</el-button
                      >
                    </template>
                    <template v-else-if="invt.addedQuantity === 0">
                      <el-button
                        size="mini"
                        class="f-number-input"
                        @click="
                          invt.addedQuantity = 1
                          invt.checked = true
                        "
                        >{{ $t('common._common._add') }}</el-button
                      >
                    </template>
                    <template v-else>
                      <el-input-number
                        size="mini"
                        class="f-number-input"
                        v-model="invt.addedQuantity"
                        :min="0"
                        :max="invt.quantity"
                        @change="handeler1(invt, index)"
                      ></el-input-number>
                    </template>
                    <div class="secondary-quantity-text pT10">
                      {{ $t('common.products.available') }}:
                      {{ getAvailableQuantity(invt) }}
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
        <div class="modal-dialog-footer-parts-dialog">
          <el-button
            class="modal-btn-cancel"
            @click="cancelItemPartsDialog()"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            style="margin-left:0px !important;"
            type="primary"
            @click="addNewbulkItem()"
            >{{ $t('common._common._add') }}</el-button
          >
        </div>
      </div>
    </el-dialog>

    <template v-if="issueItemIRDialogVisibility">
      <inventory-issue-use
        :visibility.sync="issueItemIRDialogVisibility"
        :newIRIssueItem="issueItemIRDialogVisibility"
        :workorder="workorder"
        :woItemsList="workorderItemsList"
        @refreshItemList="loadWoItemParts"
      ></inventory-issue-use>
    </template>

    <WOInvReqForm
      v-if="inventoryRequestDialogVisibility"
      :dataModuleId="
        inventoryRequestList && inventoryRequestList.length > 0
          ? inventoryRequestList[0].id
          : null
      "
      :woDetails="{
        id: workorder.id,
        name: workorder.subject,
        localId: workorder.localId,
      }"
      @onSave="loadInventoryRequest()"
      @onClose="inventoryRequestDialogVisibility = false"
    ></WOInvReqForm>
  </div>
</template>
<script>
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import WOInvReqForm from 'src/pages/workorder/workorders/v3/widgets/ItemsAndLabor/WOInvReqForm.vue'
import InlineSvg from '@/InlineSvg'
import InventoryIssueUse from 'pages/workorder/workorders/v1/InventoryIssueUse'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import { getFilteredItemListWithParentModule } from 'pages/Inventory/InventoryUtil'
import Pagination from './InventoryPagination'
import { isEmpty } from '@facilio/utils/validation'
import { htmlToText } from '@facilio/utils/filters'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  name: 'InventoryItems',
  props: ['moduleName', 'details', 'canDisable', 'resizeWidget'],
  mixins: [workorderMixin],
  components: {
    InlineSvg,
    InventoryIssueUse,
    WOInvReqForm,
    Pagination,
  },
  data() {
    return {
      selectedIndex: [],
      newItemPartToggle: false,
      selectedInventory: null,
      hidequrry: true,
      additionalCost: {
        name: null,
        cost: null,
      },
      loading: {
        serviceList: false,
      },
      inventoryRequestForWOLoading: false,
      iTloading: false,
      itemnsloading: false,
      workorderItemsList: [],
      workOrderCostList: [],
      individualItemList: [],
      inventory: [],
      tempStoreList: [],
      itemSerachQuerry: null,
      selectedStoreList: [],
      inventoryRequestDialogVisibility: false,
      issueItemIRDialogVisibility: false,
      inventoryRequestList: null,
      inventoryListLoading: false,
      items: [],
      listCount: null,
      page: 1,
      searchPage: 1,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.init()
  },
  mounted() {
    this.autoResize()
    eventBus.$on('refresh-inventory-summary', () => {
      this.reload()
    })
  },
  watch: {
    newItemPartToggle: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.loadInventory()
        }
      },
      immediate: true,
    },
  },

  computed: {
    workorder() {
      return this.details.workorder
    },
    inventoryList() {
      let list = []
      list = this.inventory.filter(item => {
        //return !(item.itemType.approvalNeeded || item.storeRoom.approvalNeeded)
        return !this.$getProperty(item, 'storeRoom.approvalNeeded')
      })
      return list
    },
    individualItemListwrapper() {
      let list = this.individualItemList
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.serialNumber
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    actionRule() {
      return this.canEdit
    },
    workItemTotalCost() {
      if (this.workOrderCostList.length) {
        if (this.workOrderCostList.find(rt => rt.costTypeEnum === 'items')) {
          return this.workOrderCostList.find(rt => rt.costTypeEnum === 'items')
            .cost
        } else {
          return 0
        }
      } else {
        return 0
      }
    },
    individualTracking() {
      if (this.inventory && this.inventory.length && this.selectedInventory) {
        return true
      } else {
        return false
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
    isStateFlowEnabled() {
      return Boolean(
        this.workorder.moduleState && this.workorder.moduleState.id
      )
    },
  },
  methods: {
    getAvailableQuantity(invt) {
      let { quantity } = invt || {}
      if (isEmpty(quantity) || quantity < 0) {
        return '--'
      }
      return quantity
    },
    unitPrice(workItem) {
      let unitPrice = this.$getProperty(workItem, 'asset.unitPrice')
      return isEmpty(unitPrice) ? 0.0 : unitPrice
    },
    showMoreLinkText(index) {
      let { selectedIndex } = this
      return selectedIndex.indexOf(index) !== -1 ? 'View Less' : 'View More'
    },
    canShowMore(inventory) {
      let description = this.$getProperty(inventory, 'itemType.description')
      if (description) {
        let descriptionLength = description.length
        let htmlToString = htmlToText(description).split(/\r\n|\r|\n/).length
        return htmlToString > 2 || descriptionLength > 130
      } else {
        return false
      }
    },
    getSubjectClassname(inventory, index) {
      if (this.canShowMore(inventory)) {
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
    async getStoreroom() {
      let siteId = this.$getProperty(this.workorder, 'siteId')
      let params = {
        siteId: siteId,
      }

      let { error, data } = await API.get('v2/storeRoom/view/all', params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { storeRooms } = data || {}

        this.tempStoreList = storeRooms.filter(store => {
          return !this.$getProperty(store, 'approvalNeeded')
        })
        let { id } = this.tempStoreList[0] || {}
        this.selectedStoreList = id
      }
    },
    onPageChange(pageNo) {
      let { itemSerachQuerry } = this
      this.page = pageNo
      if (!isEmpty(itemSerachQuerry)) {
        this.searchPage = pageNo
      }
      this.loadInventory()
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['inventoryItemsWidget']
        if (container) {
          let height = container.scrollHeight
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
    async init() {
      this.loadWoItemParts(true)
      this.getWorkOrderCostList(true)
      this.loadItem(true)
      this.loadInventoryRequest()
      await this.getStoreroom()
      this.loadItemsCount()
    },
    reload() {
      this.loadWoItemParts(true)
      this.loadInventoryRequest()
    },
    async loadItemsCount() {
      this.page = 1
      let siteId = this.$getProperty(this.workorder, 'siteId')
      let { selectedStoreList, itemSerachQuerry } = this
      let params = {
        showForWorkorder: true,
        includeServingSite: true,
        siteId: siteId,
        count: true,
      }
      if (!isEmpty(itemSerachQuerry)) {
        this.$set(params, 'searchQuery', itemSerachQuerry)
        this.searchPage = 1
      }
      if (!isEmpty(selectedStoreList)) {
        let filters = {
          storeRoom: {
            value: [`${selectedStoreList}`],
            operatorId: 36,
          },
        }
        this.$set(params, 'filters', JSON.stringify(filters))
        let url = `v2/item/view/all`
        let { data, error } = await API.get(url, params)

        if (error) {
          let { message } = error
          this.$message.error(message)
        } else {
          this.listCount = this.$getProperty(data, 'count')
        }
      }
    },
    loadInventory() {
      this.inventoryListLoading = true
      let siteId = this.$getProperty(this.workorder, 'siteId')
      let { page, itemSerachQuerry, selectedStoreList, searchPage } = this
      let params = {
        page: page,
        perPage: 20,
        showForWorkorder: true,
        includeServingSite: true,
        siteId: siteId,
      }
      if (itemSerachQuerry) {
        this.$set(params, 'searchQuery', itemSerachQuerry)
        this.$set(params, 'page', searchPage)
        this.loadItemsCount()
      }
      if (!isEmpty(selectedStoreList)) {
        let filters = {
          storeRoom: {
            value: [`${selectedStoreList}`],
            operatorId: 36,
          },
        }
        this.$set(params, 'filters', JSON.stringify(filters))
        this.loadItemsCount()
        API.get('v2/item/view/all', params).then(({ data }) => {
          this.inventoryListLoading = false
          let inventory = []
          inventory = data && data.items ? data.items : []
          if (data) {
            inventory = inventory.map(item => {
              return {
                ...item,
                checked: false,
                addedQuantity: 0,
                invidualList: [],
              }
            })
            this.inventory = inventory
          }
        })
      }
    },
    getItemCount(workItem) {
      if (workItem) {
        let diff
        if (workItem.requestedLineItem) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.requestedLineItem.issuedQuantity) -
            Number(workItem.requestedLineItem.usedQuantity) -
            Number(workItem.quantity)
        } else if (workItem.parentTransactionId !== -1) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.remainingQuantity) -
            Number(workItem.quantity)
        } else {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.tempItem.quantity) -
            Number(workItem.quantity)
        }
        if (Number(workItem.quantity) < 0) {
          return 'Quantity must be nonzero'
        } else if (diff < 0) {
          return 'Exceeded'
        } else {
          return diff
        }
      } else {
        return ''
      }
    },
    getItemQuantityState(workItem) {
      if (workItem) {
        let diff
        if (workItem.requestedLineItem) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.requestedLineItem.issuedQuantity) -
            Number(workItem.requestedLineItem.usedQuantity) -
            Number(workItem.quantity)
        } else if (workItem.parentTransactionId !== -1) {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.remainingQuantity) -
            Number(workItem.quantity)
        } else {
          diff =
            Number(workItem.tempQuantity) +
            Number(workItem.tempItem.quantity) -
            Number(workItem.quantity)
        }
        if (Number(workItem.quantity) < 0) {
          return true
        } else if (diff < 0) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
    addNewWOItem() {
      this.individualItemList = []
      this.newItemPartToggle = true
    },
    itemshowRule(invt) {
      if (!invt.itemType.isRotating) {
        let tempWorkItem = this.workorderItemsList.filter(i => {
          return i.item.id === invt.id
        })
        let check = true
        tempWorkItem.forEach(i => {
          if (i.parentTransactionId === -1) {
            check = false
          }
        })
        return check
      } else {
        if (invt.quantity > 0 && invt.itemType.currentQuantity > 0) {
          return true
        } else {
          return false
        }
      }
    },
    deleteWorkorderItem(workItem, index) {
      let self = this
      if (!workItem.hasOwnProperty('purchasedItem')) {
        this.workorderItemsList.pop()
      } else {
        let param = { parentId: this.workorder.id, workorderItemsId: [] }
        param.workorderItemsId.push(workItem.id)
        this.$http
          .post('v2/workorderItems/delete', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.actionToWorkorderStatusChange()
              self.loadInventory()
              self.$message.success('Item Deleted')
              self.getWorkOrderCostList()
              self.workorderItemsList.splice(index, 1)
              this.autoResize()
            } else {
              self.$message.error(response.data.message)
            }
            this.$root.$emit('refresh-inventory-summary')
          })
          .catch(error => {
            console.error(error)
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
    addNewbulkItem() {
      let param = { workorderItems: [] }
      let obj = {}
      let assetIds = []
      this.inventoryList.forEach(item => {
        obj = {}
        assetIds = []
        if (item.itemType.isRotating) {
          if (item.invidualList.length) {
            assetIds = item.invidualList.map(rl => rl.id)
            obj = {
              parentId: this.workorder.id,
              item: { id: item.id },
              quantity: item.invidualList.length,
              assetIds: assetIds,
            }
            param.workorderItems.push(obj)
          }
        } else {
          if (item.addedQuantity > 0) {
            obj = {
              parentId: this.workorder.id,
              item: { id: item.id },
              quantity: item.addedQuantity,
              assetIds: assetIds,
            }
            param.workorderItems.push(obj)
          }
        }
      })
      this.selectedInventory = null
      this.individualItemList = []
      this.individualToolList = []
      this.newItemPartToggle = false
      let self = this
      this.$http
        .post('/v2/workorderItems/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common.header.item_added_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.loadWoItemParts(false, true)
          }
          this.$root.$emit('refresh-inventory-summary')
        })
        .catch(error => {
          console.error(error)
        })
    },
    actionToWorkorderStatusChange() {
      this.fetchWo()
    },
    async cancelItemPartsDialog() {
      this.newItemPartToggle = false
      this.selectedInventory = null
      this.selectedTool = null
      this.page = 1
      this.searchPage = 1
      this.itemSerachQuerry = null
      this.selectedIndex = []
      await this.getStoreroom()
      this.loadInventory()
    },
    async loadItem() {
      this.items = await getFilteredItemListWithParentModule('workorder')
    },
    changeWorkItemQuantity(workItem) {
      if (
        workItem.quantity <=
        workItem.tempQuantity + workItem.tempItem.quantity
      ) {
        let param = { workorderItems: [] }
        let temp = {
          parentId: this.workorder.id,
          id: workItem.id,
          item: { id: workItem.item.id },
          quantity: workItem.quantity,
          requestedLineItem: workItem.requestedLineItem,
          parentTransactionId: workItem.parentTransactionId,
          remainingQuantity: workItem.remainingQuantity + workItem.tempQuantity,
        }
        param.workorderItems.push(temp)
        let self = this
        this.$http
          .post('/v2/workorderItems/addOrUpdate', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.loadWoItemParts()
              self.actionToWorkorderStatusChange()
              self.$message.success(
                this.$t('common._common.updated_successfully')
              )
            } else {
              self.$message.error(response.data.message)
            }
          })
      } else {
        this.$message.error(this.$t('common.dashboard.quantity_should_be_less'))
      }
    },
    loadWoItemParts(loading, inventory) {
      this.loadWorkOrderItemParts = []
      let self = this
      let self1 = this
      if (loading) {
        this.itemnsloading = true
      }
      this.$http
        .get('/v2/workorderItemsList/parent/' + this.workorder.id)
        .then(response => {
          let workItem = {}
          for (let item in response.data.result.workorderItem) {
            workItem = response.data.result.workorderItem[item]
            workItem.tempQuantity = workItem.quantity
            workItem.tempItem = workItem.item
          }
          if (loading || inventory) {
            self.loadInventory()
          }

          self1.itemnsloading = false
          self.workorderItemsList = self.$helpers.cloneObject(
            response.data.result.workorderItem
          )
          self1.getWorkOrderCostList()
          this.autoResize()
        })
        .catch(() => {
          self.$message.error(this.$t('common.wo_report.unable_to_load_parts'))
        })
      this.loadItem()
    },
    async loadInventoryRequest(force = false) {
      this.inventoryRequestForWOLoading = true
      let { workorder } = this
      let { id } = workorder || {}
      let relatedFieldName = getRelatedFieldName(
        'workorder',
        'inventoryrequest'
      )
      let relatedConfig = {
        moduleName: 'workorder',
        id,
        relatedModuleName: 'inventoryrequest',
        relatedFieldName,
      }
      let { error, list } = await API.fetchAllRelatedList(
        relatedConfig,
        {},
        {
          force,
        }
      )

      if (!error) {
        if (!isEmpty(list)) {
          this.inventoryRequestList = list
        }
      } else {
        this.$error.message(error.message || 'Error Occured')
      }

      this.inventoryRequestForWOLoading = false
    },

    async loadindividualTrackingList(items) {
      this.individualItemList = []
      this.iTloading = true

      let filters = {
        rotatingItem: {
          operatorId: 36,
          value: items.map(rt => rt.id + ''),
        },
        isUsed: { operatorId: 15, value: ['false'] },
      }
      let individualItemList = await this.$util.getFilteredAssetList(filters)

      this.individualItemList = individualItemList.map(item => ({
        ...item,
        checked: false,
      }))
      this.iTloading = false
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
.store-room-bar-item {
  position: absolute;
  left: 10px !important;
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
<style scoped>
.fc-create-btn.disabled:hover {
  box-shadow: 0 2px 4px 0 #ffc9de;
}

.v3-layout-override {
  flex-direction: row !important;
}
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
.search-container {
  position: absolute;
  left: 220px !important;
}
.popup-container {
  width: 100%;
  padding: 30px;
  border-bottom: 1px solid #edeeef;
  position: -webkit-sticky;
  position: sticky;
  background: #fff;
  display: flex;
  width: 100%;
  align-items: center;
}
</style>
