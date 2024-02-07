<template>
  <div class="pm-summary-inner-container inventory-page v3-layout-override">
    <div class="fL pm-summary-inner-left-con mT20 fc-res-dell">
      <!-- first card card -->
      <div class="p30 white-bg-block mB20">
        <!-- main section start-->
        <div class="flex-middle justify-content-space">
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

          <div
            v-if="isNotPortal && actionRule && !inventoryRequestForWOLoading"
          >
            <el-button
              :disabled="canDisable"
              :class="[
                'fc-create-btn create-btn mL10',
                { disabled: canDisable },
              ]"
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
              :class="[
                'fc-create-btn create-btn mL10',
                { disabled: canDisable },
              ]"
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
        <div class="inventory-table pT20">
          <table class="width100" v-if="itemnsloading">
            <tr>
              <td colspan="100%">
                <div class="iTloading in-no-data">
                  <spinner :show="true" size="80"></spinner>
                </div>
              </td>
            </tr>
          </table>

          <table class="width100 inventory-tools-table inventory-table" v-else>
            <thead>
              <tr>
                <th style="width: 63%;">{{ $t('common.header._item') }}</th>
                <th class>{{ $t('common._common._quantity') }}</th>
                <th class="p10 text-right">
                  {{ $t('common.header._unit_price') }}
                </th>
                <th class="text-right" style="width: 72px;">
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
                    <currency :value="0"></currency>
                  </div>
                </td>
                <td>
                  <div class="fc-grey3-13 text-right">
                    <currency :value="0"></currency>
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
                          v-if="
                            workItem.item.itemType.isRotating && workItem.asset
                          "
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
                    <div class="in-Quantity row">
                      <div class="item col-6">
                        <el-tooltip
                          class="item text-center quant"
                          effect="dark"
                          :content="
                            actionRule
                              ? $t('common.products.available_items') +
                                getItemCount(workItem)
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
                        :value="workItem.asset.unitPrice"
                      ></currency>
                      <currency
                        v-else
                        class="text-right pR10"
                        :value="workItem.purchasedItem.unitcost"
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
                  <td>
                    <div class="width180px">
                      <div v-if="workItem.id > -1" class="pL17">
                        <div
                          v-if="
                            workItem.item.itemType.isRotating && workItem.asset
                          "
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
                      :value="workItem.asset.unitPrice"
                    ></currency>
                    <currency
                      v-else
                      class="text-right pR10"
                      :value="workItem.purchasedItem.unitcost"
                    ></currency>
                  </td>
                  <td>
                    <div class="text-right">
                      <currency :value="workItem.cost"></currency>
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
                          v-if="
                            workItem.item.itemType.isRotating && workItem.asset
                          "
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
                            ? $t('common.products.available_items') +
                              getItemCount(workItem)
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
                      :value="workItem.asset.unitPrice"
                    ></currency>
                    <currency
                      v-else
                      class="text-right pR10"
                      :value="workItem.purchasedItem.unitcost"
                    ></currency>
                  </td>
                  <td>
                    <div class="text-right">
                      <currency :value="workItem.cost"></currency>
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
              <currency :value="workItemTotalCost"></currency>
            </div>
          </div>
        </div>
      </div>

      <div class="p30 white-bg-block mB20">
        <div class="flex-middle">
          <div class="fc-v1-icon-bg text-center">
            <InlineSvg
              src="svgs/service"
              iconClass="icon icon-lg fill-white"
            ></InlineSvg>
          </div>
          <div class="fc-black3-16 mL10">
            {{ $t('common.products.services') }}
          </div>
        </div>

        <div
          class="inventory-table inventory-tool-table inventory-labour-tool-table pT20"
        >
          <table class="width100" v-if="loading.woService">
            <tr>
              <td colspan="100%">
                <div class="iTloading in-no-data">
                  <spinner :show="true" size="80"></spinner>
                </div>
              </td>
            </tr>
          </table>

          <table class="width100" v-else>
            <thead>
              <tr>
                <th class style="width: 180px;">
                  {{ $t('common.products._service') }}
                </th>
                <th class="text-right width110px">
                  {{ $t('common.wo_report._start_time') }}
                </th>
                <th class="text-right width110px pL20">
                  {{ $t('common.wo_report._end_time') }}
                </th>
                <th class="text-right width130px">
                  {{ $t('common._common.duration_hr') }}
                </th>
                <th class="text-right width130px">
                  {{ $t('common._common._quantity') }}
                </th>
                <th class="text-right">{{ $t('common.header._price') }}</th>
                <th class="text-right">{{ $t('common.tabs._cost') }}</th>
                <th class="width40px"></th>
              </tr>
            </thead>
            <tbody v-if="!workorderServiceList.length">
              <tr>
                <td
                  @click="actionRule ? addNewWOService() : null"
                  :class="{ disabled: !actionRule }"
                  class="inventory-td-selected pL20 pT10 pB10"
                >
                  <div>{{ $t('common.header.add_service') }}</div>
                </td>
                <td class="pT10 pB10">
                  <div class="fc-black-13 text-right">-- -- --</div>
                </td>
                <td class="pT10 pB10 pL20">
                  <div class="fc-black-13 text-right">-- -- --</div>
                </td>
                <td>
                  <div class="text-right">
                    <el-input
                      placeholder
                      type="number"
                      class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                      disabled
                    ></el-input>
                  </div>
                </td>
                <td>
                  <div class="text-right">
                    <el-input
                      placeholder
                      type="number"
                      class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                      disabled
                    ></el-input>
                  </div>
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
                <td></td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                v-for="workService in workorderServiceList"
                :key="workService.id"
                class="borderB1px border-color11 visibility-visible-actions pointer"
              >
                <td>
                  <div class="pL17" style="width: 180px;">
                    <div>{{ workService.service.name }}</div>
                  </div>
                </td>
                <td>
                  <div class="in-Quantity width110px">
                    <div class="fc-black-13 text-right pR17">
                      {{
                        formatTime(
                          Number(workService.startTime),
                          'hh:mm a',
                          false
                        )
                      }}
                    </div>
                    <div class="fc-grey2-text12 text-right">
                      <el-date-picker
                        v-model="workService.startTime"
                        format="dd-MM-yyyy hh:mm a"
                        value-format="timestamp"
                        type="datetime"
                        :disabled="
                          !actionRule ||
                            $getProperty(workService, 'service.paymentType') ===
                              1
                        "
                        placeholder="Start time"
                        class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                        @change="changeWorkServiceField(workService, 1)"
                      ></el-date-picker>
                    </div>
                  </div>
                </td>
                <td class="pL20">
                  <div class="in-Quantity width110px">
                    <div class="fc-black-13 text-right">
                      {{
                        formatTime(
                          Number(workService.endTime),
                          'hh:mm a',
                          false
                        )
                      }}
                    </div>
                    <div class="fc-grey2-text12 text-right">
                      <el-date-picker
                        v-model="workService.endTime"
                        format="dd-MM-yyyy hh:mm a"
                        value-format="timestamp"
                        type="datetime"
                        :disabled="
                          !actionRule ||
                            $getProperty(workService, 'service.paymentType') ===
                              1
                        "
                        placeholder="End time"
                        class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                        @change="changeWorkServiceField(workService, 2)"
                        :picker-options="{
                          disabledDate(time) {
                            return time.getTime() < workService.startTime
                          },
                        }"
                      ></el-date-picker>
                    </div>
                  </div>
                </td>
                <td>
                  <div
                    class="in-Quantity"
                    style="width: 70px; text-align: right; float: right;"
                    :title="workService.duration"
                    v-tippy="{
                      placement: 'top',
                      arrow: true,
                      animation: 'shift-away',
                    }"
                  >
                    <el-input
                      :placeholder="$t('common._common._duration')"
                      v-model="workService.duration"
                      @change="changeWorkServiceField(workService, 3)"
                      :disabled="
                        !actionRule ||
                          $getProperty(workService, 'service.paymentType') === 1
                      "
                      class="pL10 labour-inventory-items-input labour-inventory-input pR0 fc-input-full-border-select2"
                    ></el-input>
                  </div>
                </td>

                <td>
                  <div
                    class="in-Quantity"
                    style="width: 70px; text-align: right; float: right;"
                    :title="workService.quantity"
                    v-tippy="{
                      placement: 'top',
                      arrow: true,
                      animation: 'shift-away',
                    }"
                  >
                    <el-input
                      :placeholder="$t('common._common._duration')"
                      v-model="workService.quantity"
                      @change="changeWorkServiceField(workService, 4)"
                      :disabled="
                        !actionRule ||
                          $getProperty(workService, 'service.paymentType') === 2
                      "
                      class="pL10 labour-inventory-items-input labour-inventory-input pR0 fc-input-full-border-select2"
                    ></el-input>
                  </div>
                </td>
                <td>
                  <div class="text-right">
                    <currency
                      :value="(workService.service || {}).buyingPrice || 0"
                    ></currency>
                  </div>
                </td>
                <td>
                  <div class="text-right">
                    <currency :value="workService.cost"></currency>
                  </div>
                </td>
                <td v-show="actionRule">
                  <i
                    class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                    data-arrow="true"
                    :title="$t('common._common.delete')"
                    v-tippy
                    @click="deleteWoService(workService)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="item-add" v-if="loading.woService"></div>
        <div class="item-add" v-else>
          <div
            class="fL"
            v-show="
              isNotPortal && actionRule && (workorderServiceList || []).length
            "
          >
            <div
              class="green-txt-13 fc-v1-add-txt pointer"
              @click="addNewWOService"
            >
              <img src="~assets/add-icon.svg" />{{
                $t('common.header.add_service')
              }}
            </div>
          </div>
          <div class="fR inline-flex mR44">
            <div class="bold mR50">{{ $t('common.header._total') }}</div>
            <div class="fc-black3-16 text-right bold pL10">
              <currency :value="workServiceCost"></currency>
            </div>
          </div>
        </div>
      </div>

      <div class="p30 white-bg-block mB20" v-if="showLabour">
        <div class="flex-middle">
          <div class="fc-v1-icon-bg text-center">
            <InlineSvg src="svgs/labour" iconClass="icon icon-lg"></InlineSvg>
          </div>
          <div class="fc-black3-16 mL10">{{ $t('common.header.labour') }}</div>
        </div>

        <div
          class="inventory-table inventory-tool-table inventory-labour-tool-table pT20"
        >
          <table class="width100" v-if="labourLoading">
            <tr>
              <td colspan="100%">
                <div class="iTloading in-no-data">
                  <spinner :show="true" size="80"></spinner>
                </div>
              </td>
            </tr>
          </table>

          <table class="width100" v-else>
            <thead>
              <tr>
                <th class style="width: 180px;">
                  {{ $t('common.header._labour') }}
                </th>
                <th class="text-right width110px">
                  {{ $t('common.wo_report._start_time') }}
                </th>
                <th class="text-right width110px pL20">
                  {{ $t('common.wo_report._end_time') }}
                </th>
                <th class="text-right width130px">
                  {{ $t('common._common.duration_hr') }}
                </th>
                <th class="text-right">{{ $t('common.header.rate_hr') }}</th>
                <th class="text-right">{{ $t('common.tabs._cost') }}</th>
                <th class="width40px"></th>
              </tr>
            </thead>
            <tbody v-if="!workorderLabourList.length">
              <tr>
                <td
                  @click="actionRule ? addNewWOLabour() : null"
                  :class="{ disabled: !actionRule }"
                  class="inventory-td-selected pL20 pT10 pB10"
                >
                  <div>{{ $t('common.header._add_labour') }}</div>
                </td>
                <td class="pT10 pB10">
                  <div class="fc-black-13 text-right">-- -- --</div>
                </td>
                <td class="pT10 pB10 pL20">
                  <div class="fc-black-13 text-right">-- -- --</div>
                </td>
                <td>
                  <div class="text-right">
                    <el-input
                      placeholder
                      type="number"
                      class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                      disabled
                    ></el-input>
                  </div>
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
                <td></td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                v-for="(workLabour, index) in workorderLabourList"
                :key="workLabour.id"
                class="borderB1px border-color11 visibility-visible-actions pointer"
              >
                <td>
                  <div class="pL17" style="width: 180px;">
                    <div>{{ workLabour.labour.name }}</div>
                  </div>
                </td>
                <td>
                  <div class="in-Quantity width110px">
                    <div class="fc-black-13 text-right pR17">
                      {{
                        formatTime(
                          Number(workLabour.startTime),
                          'hh:mm a',
                          false
                        )
                      }}
                    </div>
                    <div class="fc-grey2-text12 text-right">
                      <el-date-picker
                        v-model="workLabour.startTime"
                        format="dd-MM-yyyy hh:mm a"
                        value-format="timestamp"
                        type="datetime"
                        :disabled="!actionRule"
                        :placeholder="$t('common.wo_report.start_time')"
                        class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                        @change="changeWorkLabourField(workLabour, index, 1)"
                      ></el-date-picker>
                    </div>
                  </div>
                </td>
                <td class="pL20">
                  <div class="in-Quantity width110px">
                    <div class="fc-black-13 text-right">
                      {{
                        formatTime(Number(workLabour.endTime), 'hh:mm a', false)
                      }}
                    </div>
                    <div class="fc-grey2-text12 text-right">
                      <el-date-picker
                        v-model="workLabour.endTime"
                        format="dd-MM-yyyy hh:mm a"
                        value-format="timestamp"
                        type="datetime"
                        :disabled="!actionRule"
                        :placeholder="$t('common.wo_report.end_time')"
                        class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                        @change="changeWorkLabourField(workLabour, index, 2)"
                        :picker-options="{
                          disabledDate(time) {
                            return time.getTime() < workLabour.startTime
                          },
                        }"
                      ></el-date-picker>
                    </div>
                  </div>
                </td>
                <td>
                  <div
                    class="in-Quantity"
                    style="width: 70px; text-align: right; float: right;"
                    :title="workLabour.duration"
                    v-tippy="{
                      placement: 'top',
                      arrow: true,
                      animation: 'shift-away',
                    }"
                  >
                    <el-input
                      placeholder="duration"
                      v-model="workLabour.duration"
                      @change="changeWorkLabourField(workLabour, index, 3)"
                      :disabled="!actionRule"
                      class="pL10 labour-inventory-items-input labour-inventory-input pR0 fc-input-full-border-select2"
                    ></el-input>
                  </div>
                </td>
                <td>
                  <div class="text-right">
                    <currency :value="workLabour.labour.cost"></currency>
                  </div>
                </td>
                <td>
                  <div class="text-right">
                    <currency :value="workLabour.cost"></currency>
                  </div>
                </td>
                <td v-show="actionRule">
                  <i
                    class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                    data-arrow="true"
                    :title="$t('common._common.delete_labour')"
                    v-tippy
                    @click="deleteWorkorderLabour(workLabour, index)"
                  ></i>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="item-add" v-if="labourLoading"></div>
        <div class="item-add" v-else>
          <div
            class="fL"
            v-show="isNotPortal && actionRule && workorderLabourList.length"
          >
            <div
              class="green-txt-13 fc-v1-add-txt pointer"
              @click="addNewWOLabour"
            >
              <img src="~assets/add-icon.svg" />{{
                $t('common.header._add_labour')
              }}
            </div>
          </div>
          <div class="fR inline-flex mR44">
            <div class="bold mR50">{{ $t('common.header._total') }}</div>
            <div class="fc-black3-16 text-right bold pL10">
              <currency :value="workLabourTotalCost"></currency>
            </div>
          </div>
        </div>
      </div>

      <div class="p30 white-bg-block mB100">
        <!-- main section start-->
        <div class="flex-middle justify-content-space">
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
        </div>
        <div
          class="inventory-table inventory-tool-table inventory-table-body-border-none overflow-x mT20"
          style="border: 1px solid #eceef1;"
        >
          <table class="width100" style="border: none;" v-if="toolLoading">
            <tr>
              <td colspan="100%">
                <div class="iTloading in-no-data">
                  <spinner :show="true" size="80"></spinner>
                </div>
              </td>
            </tr>
          </table>

          <table class="width100" style="border: none;" v-else>
            <thead>
              <tr class="border-top-none">
                <th class="border-top-none" style="width: 300px;">
                  {{ $t('common.header._tool') }}
                </th>
                <th class="pR0 border-top-none">
                  {{ $t('common._common._quantity') }}
                </th>
                <th class="text-right width110px border-top-none">
                  {{ $t('common.wo_report._start_time') }}
                </th>
                <th class="text-right width110px pL20 border-top-none">
                  {{ $t('common.wo_report._end_time') }}
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
            <tbody v-if="!workorderToolsList.length">
              <tr>
                <td
                  @click="actionRule ? addNewWOTool() : null"
                  :class="{ disabled: !actionRule }"
                  class="inventory-td-selected pL20 pT10 pB10"
                >
                  <div>{{ $t('common.products.add_tool') }}</div>
                </td>
                <td class="pT10 pB10">
                  <div>
                    <el-input
                      placeholder
                      type="number"
                      class="fc-input-full-border2 width50px inventory-input-width text-center pL20"
                      disabled
                    ></el-input>
                  </div>
                </td>
                <td class="pT10 pB10">
                  <div class="fc-black-13 text-right">-- -- --</div>
                </td>
                <td class="pT10 pB10 pL20">
                  <div class="fc-black-13 text-right">-- -- --</div>
                </td>
                <td class="pT10 pB10">
                  <div class="text-right">
                    <el-input
                      placeholder
                      type="number"
                      class="fc-input-full-border2 width50px inventory-input-width text-center mR10"
                      disabled
                    ></el-input>
                  </div>
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
              <template v-for="(workTool, index) in workorderToolsList">
                <tr
                  :key="workTool.id"
                  class="borderB1px border-color11 visibility-visible-actions pointer"
                  v-if="workTool.approvedState === 2"
                >
                  <td class="pT10 pB10">
                    <div class="pL17" style="width: 180px;">
                      <div
                        v-if="
                          workTool.tool.toolType.isRotating &&
                            workTool.purchasedTool
                        "
                        class="fc-id"
                        style="padding-bottom: 4px;"
                      >
                        #{{ workTool.purchasedTool.serialNumber }}
                      </div>
                      <div>{{ workTool.tool.toolType.name }}</div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="in-Quantity">
                      <el-tooltip
                        class="item text-center quant"
                        effect="dark"
                        :content="
                          actionRule
                            ? $t('common.products.Available tools') +
                              getToolCount(workTool)
                            : $t('common.header.please_open_the_workorder')
                        "
                        placement="right-end"
                        v-if="!workTool.tool.toolType.isRotating"
                        v-bind:class="{
                          exceeded: getToolQuantityState(workTool),
                        }"
                      >
                        <template>
                          <el-form
                            :model="workTool"
                            ref="numberValidateForm"
                            @submit.prevent.native="checkEnter"
                            class="width50px inventory-input-width"
                            v-bind:class="{
                              exceeded: getToolQuantityState(workTool),
                            }"
                          >
                            <el-form-item label prop="quantity">
                              <el-input
                                placeholder
                                v-model="workTool.quantity"
                                type="number"
                                @change="changeWorkToolField(workTool, index)"
                                class="fc-input-full-border2 width50px inventory-input-width text-right pL20"
                                :disabled="!actionRule"
                              ></el-input>
                            </el-form-item>
                          </el-form>
                        </template>
                      </el-tooltip>
                      <div class="item" v-else>
                        <div class="workItem-quantity">
                          {{ workTool.quantity }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="in-Quantity pL0 width110px">
                      <div class="fc-black-13 text-right">
                        {{
                          formatTime(
                            Number(workTool.issueTime),
                            'hh:mm a',
                            false
                          )
                        }}
                      </div>
                      <div class="fc-grey2-text12 text-right">
                        <el-date-picker
                          v-model="workTool.issueTime"
                          format="dd-MM-yyyy hh:mm a"
                          value-format="timestamp"
                          type="datetime"
                          :disabled="!actionRule"
                          placeholder="Start time"
                          class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                          @change="
                            changeWorkToolField(workTool, index, false, 1)
                          "
                          :default-time="['12:00:00']"
                        ></el-date-picker>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10 pL20">
                    <div class="in-Quantity width110px">
                      <div class="fc-black-13 text-right">
                        {{
                          formatTime(
                            Number(workTool.returnTime),
                            'hh:mm a',
                            false
                          )
                        }}
                      </div>
                      <div class="fc-grey2-text12 text-right">
                        <el-date-picker
                          v-model="workTool.returnTime"
                          format="dd-MM-yyyy hh:mm a"
                          value-format="timestamp"
                          type="datetime"
                          :disabled="!actionRule"
                          :placeholder="$t('common.wo_report.end_time')"
                          class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                          @change="
                            changeWorkToolField(workTool, index, false, 2)
                          "
                          :picker-options="{
                            disabledDate(time) {
                              return time.getTime() < workTool.issueTime
                            },
                          }"
                        ></el-date-picker>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div
                      class="in-Quantity"
                      style="width: 70%; text-align: right; float: right;"
                      :title="workTool.durationInHour"
                      v-tippy="{
                        placement: 'top',
                        arrow: true,
                        animation: 'shift-away',
                      }"
                    >
                      <el-input
                        :placeholder="$t('common._common._duration')"
                        v-model="workTool.durationInHour"
                        @change="changeWorkToolField(workTool, index, true, 3)"
                        :disabled="!actionRule"
                        class="pL10 inventory-items-input fc-input-full-border-select2"
                      ></el-input>
                    </div>
                  </td>
                  <td>
                    <div class="text-right">
                      <currency :value="workTool.tool.rate"></currency>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="text-right">
                      <div class="text-right">
                        <i
                          class="fa fa-lock f18"
                          data-arrow="true"
                          :title="$t('common.products.waiting_for_approval')"
                          v-tippy
                        ></i>
                      </div>
                    </div>
                  </td>
                  <td>
                    <i
                      class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                      data-arrow="true"
                      :title="$t('common.header.delete_tool')"
                      v-tippy
                      @click="deleteWorkorderTool(workTool, index)"
                      v-if="actionRule"
                    ></i>
                  </td>
                </tr>
                <tr
                  :key="workTool.id"
                  class="borderB1px border-color11 visibility-visible-actions pointer disabled"
                  v-else-if="workTool.approvedState === 4"
                >
                  <td class="pT10 pB10">
                    <div class="pL17" style="width: 300px;">
                      <div
                        v-if="
                          workTool.tool.toolType.isRotating &&
                            workTool.purchasedTool
                        "
                        class="fc-id"
                        style="padding-bottom: 4px;"
                      >
                        #{{ workTool.purchasedTool.serialNumber }}
                      </div>
                      <div>{{ workTool.tool.toolType.name }}</div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="in-Quantity">
                      <div class="item">
                        <div class="workItem-quantity">
                          {{ workTool.quantity }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="in-Quantity pL0 width110px">
                      <div class="fc-black-13 text-right">
                        {{
                          formatTime(
                            Number(workTool.issueTime),
                            'hh:mm a',
                            false
                          )
                        }}
                      </div>
                      <div class="fc-grey2-text12 text-right">
                        {{
                          formatTime(
                            Number(workTool.issueTime),
                            'DD-MM-YYYY',
                            false
                          )
                        }}
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10 pL20">
                    <div class="in-Quantity width110px">
                      <div class="fc-black-13 text-right">
                        {{
                          formatTime(
                            Number(workTool.returnTime),
                            'hh:mm a',
                            false
                          )
                        }}
                      </div>
                      <div class="fc-grey2-text12 text-right">
                        {{
                          formatTime(
                            Number(workTool.returnTime),
                            'DD-MM-YYYY',
                            false
                          )
                        }}
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div
                      class="in-Quantity"
                      style="width: 70%; text-align: right; float: right;"
                    >
                      {{ workTool.durationInHour }}
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="text-right">
                      <currency :value="workTool.tool.rate"></currency>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="text-right">
                      <currency :value="workTool.cost"></currency>
                    </div>
                  </td>
                  <td>
                    <i
                      class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                      data-arrow="true"
                      :title="$t('common.header.delete_tool')"
                      v-tippy
                      @click="deleteWorkorderTool(workTool, index)"
                      v-if="actionRule"
                    ></i>
                  </td>
                </tr>
                <tr
                  :key="workTool.id"
                  class="borderB1px border-color11 visibility-visible-actions pointer"
                  v-else
                >
                  <td class="pT10 pB10">
                    <div class="pL17" style="width: 300px;">
                      <div
                        v-if="
                          workTool.tool.toolType.isRotating && workTool.asset
                        "
                        class="fc-id"
                        style="padding-bottom: 4px;"
                      >
                        #{{ workTool.asset.serialNumber }}
                      </div>
                      <div>{{ workTool.tool.toolType.name }}</div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="in-Quantity">
                      <el-tooltip
                        class="item text-center quant"
                        effect="dark"
                        :content="
                          actionRule
                            ? $t('common.products.available_tools') +
                              getToolCount(workTool)
                            : $t('common.header.please_open_the_workorder')
                        "
                        placement="right-end"
                        v-if="!workTool.tool.toolType.isRotating"
                        v-bind:class="{
                          exceeded: getToolQuantityState(workTool),
                        }"
                      >
                        <template>
                          <el-form
                            :model="workTool"
                            ref="numberValidateForm"
                            @submit.prevent.native="checkEnter"
                            class="width50px inventory-input-width"
                            v-bind:class="{
                              exceeded: getToolQuantityState(workTool),
                            }"
                          >
                            <el-form-item label prop="quantity">
                              <el-input
                                placeholder
                                v-model="workTool.quantity"
                                type="number"
                                @change="changeWorkToolField(workTool, index)"
                                class="fc-input-full-border2 width50px inventory-input-width text-right pL20"
                                :disabled="!actionRule"
                              ></el-input>
                            </el-form-item>
                          </el-form>
                        </template>
                      </el-tooltip>
                      <div class="item" v-else>
                        <div class="workItem-quantity">
                          {{ workTool.quantity }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div class="in-Quantity pL0 width110px">
                      <div class="fc-black-13 text-right">
                        {{
                          formatTime(
                            Number(workTool.issueTime),
                            'hh:mm a',
                            false
                          )
                        }}
                      </div>
                      <div class="fc-grey2-text12 text-right">
                        <el-date-picker
                          v-model="workTool.issueTime"
                          format="dd-MM-yyyy hh:mm a"
                          value-format="timestamp"
                          type="datetime"
                          :placeholder="$t('common.wo_report.start_time')"
                          :disabled="!actionRule"
                          class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                          @change="
                            changeWorkToolField(workTool, index, false, 1)
                          "
                        ></el-date-picker>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10 pL20">
                    <div class="in-Quantity width110px">
                      <div class="fc-black-13 text-right">
                        {{
                          formatTime(
                            Number(workTool.returnTime),
                            'hh:mm a',
                            false
                          )
                        }}
                      </div>
                      <div class="fc-grey2-text12 text-right">
                        <el-date-picker
                          v-model="workTool.returnTime"
                          format="dd-MM-yyyy hh:mm a"
                          value-format="timestamp"
                          type="datetime"
                          :disabled="!actionRule"
                          :placeholder="$t('common.wo_report.end_time')"
                          class="fc-input-border-remove inventory-date-picker pL40 fc-grey2-text12"
                          @change="
                            changeWorkToolField(workTool, index, false, 2)
                          "
                          :picker-options="{
                            disabledDate(time) {
                              return time.getTime() < workTool.issueTime
                            },
                          }"
                        ></el-date-picker>
                      </div>
                    </div>
                  </td>
                  <td class="pT10 pB10">
                    <div
                      class="in-Quantity"
                      :title="workTool.durationInHour"
                      v-tippy="{
                        placement: 'top',
                        arrow: true,
                        animation: 'shift-away',
                      }"
                      style="width: 70%; text-align: right; float: right;"
                    >
                      <el-input
                        :placeholder="$t('common._common._duration')"
                        v-model="workTool.durationInHour"
                        @change="changeWorkToolField(workTool, index, true, 3)"
                        :disabled="!actionRule"
                        class="pL10 inventory-items-input fc-input-full-border-select2"
                      ></el-input>
                    </div>
                  </td>
                  <td>
                    <div class="text-right pR20 pL20">
                      <currency :value="workTool.tool.rate"></currency>
                    </div>
                  </td>
                  <td class="pT10 pB10 worktool-cost-sticky">
                    <div class="text-right">
                      <currency :value="workTool.cost"></currency>
                    </div>
                  </td>
                  <td class="pT10 pB10 worktool-cost-edit-sticky">
                    <i
                      class="el-icon-delete pointer inv-delet-icon visibility-hide-actions pR10 pL10"
                      data-arrow="true"
                      :title="$t('common.header.delete_tool')"
                      v-tippy
                      @click="deleteWorkorderTool(workTool, index)"
                      v-if="actionRule"
                    ></i>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
        <div class="item-add" v-if="toolLoading"></div>
        <div class="item-add" v-else>
          <div class="fL" v-if="isNotPortal && actionRule">
            <div class="green-txt-13 fc-v1-add-txt pointer">
              <span
                @click="addNewWOTool"
                v-if="workorderToolsList.length"
                class="pR20"
              >
                <img src="~assets/add-icon.svg" />{{
                  $t('common.products.add_tool')
                }}
              </span>
              <span @click="issueToolIRDialogVisibility = true">
                <img src="~assets/add-icon.svg" />{{
                  $t('common.products.add_issued_tool')
                }}
              </span>
            </div>
          </div>
          <div class="fR inline-flex mR40">
            <div class="bold mR50">{{ $t('common.header._total') }}</div>
            <div class="fc-black3-16 text-right bold pL10">
              <currency :value="workToolTotalCost"></currency>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="fR pm-summary-inner-right-con mT20">
      <div class="fc-scrollbar-wrap mT0">
        <div
          class="white-bg-block pm-summary-right-bg wo-summary-right-bg-border"
        >
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
                <currency :value="workOrderTotalCost"></currency>
              </div>
              <div class="fc-text-pink13 fw-bold text-right pT5">
                {{ $t('common.header._total') }}
              </div>
            </el-col>
          </el-row>
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
                      >{{ $t('common.products_added') }}</el-button
                    >
                  </template>
                  <template v-else-if="item.checked">
                    <el-button
                      size="mini"
                      class="f-number-input f-number-input-no-border"
                      @click="item.checked = false"
                      >{{ $t('common.products_added') }}</el-button
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
          <div class="store-room-bar" v-bind:class="{ active: !hidequrry }">
            <el-select
              v-model="selectedStoreList"
              multiple
              collapse-tags
              style="margin-left: 20px;"
              :placeholder="$t('common.products.select_storeroom')"
              class="fc-tag fc-input-full-border2"
            >
              <el-option
                v-for="(item, index) in tempStoreList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
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
              class="el-input-textbox-full-border"
              @blur="hidequrry = true"
              v-else
            >
              <span class="sub-divider">|</span>
              <i
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important;"
                class="el-input__icon el-icon-search pointer"
              ></i>
            </el-input>
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
                  <td
                    class="width80"
                    v-tippy="{
                      placement: 'top-start',
                      animation: 'shift-away',
                    }"
                    :title="invt.itemType.name + ' - ' + invt.storeRoom.name"
                  >
                    {{ invt.itemType.name }}
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
                      {{ $t('common.products.available') }}: {{ invt.quantity }}
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
    <el-dialog
      :visible.sync="newLabourToggle"
      :fullscreen="false"
      open="top"
      custom-class="fc-dialog-up Inventoryaddvaluedialog"
      :append-to-body="true"
    >
      <div class="new-header-container">
        <div class="fc-setup-modal-title">
          {{ $t('common.header.labour_list') }}
        </div>
      </div>
      <div class="fc-inv-container-body">
        <table class="setting-list-view-table width100 invent-table-dialog">
          <thead>
            <th class="setting-table-th setting-th-text"></th>
            <th class="setting-table-th setting-th-text">
              {{ $t('common.products.name') }}
            </th>
            <th class="setting-table-th setting-th-text">
              {{ $t('common._common._phone_number') }}
            </th>
            <th class="setting-table-th setting-th-text">
              {{ $t('common._common.status') }}
            </th>
          </thead>
          <tbody v-if="!labourList.length">
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
                    {{ $t('common._common.no_labours_present') }}
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow asset-hover-td"
              v-for="labour in labourList"
              :key="labour.id"
            >
              <td style="width:10%;">
                <el-checkbox v-model="labour.checked"></el-checkbox>
              </td>
              <td>{{ labour.name }}</td>
              <td>{{ labour.phone }}</td>
              <td>{{ labour.availability ? 'Active' : 'Inactive' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="modal-dialog-footer-parts-dialog">
        <el-button class="modal-btn-cancel" @click="cancelLabourDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          style="margin-left:0px !important;"
          type="primary"
          @click="addLabourSave()"
          :disabled="iTloading"
          >{{ $t('common._common._add') }}</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="serviceAddDialogVisibility"
      :fullscreen="false"
      open="top"
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
          :show="loading.serviceList"
          size="80"
          v-if="loading.serviceList"
          class="flex-middle justify-content-center flex-direction-column"
        ></spinner>
        <table
          v-else
          class="setting-list-view-table width100 invent-table-dialog"
        >
          <thead>
            <th class="setting-table-th setting-th-text"></th>
            <th class="setting-table-th setting-th-text">
              {{ $t('common.products.name') }}
            </th>
            <th class="setting-table-th setting-th-text">
              {{ `Buying Price (${$currency})` }}
            </th>
          </thead>
          <tbody v-if="!allServiceList.length">
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
              v-for="service in allServiceList"
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
        <el-button class="modal-btn-cancel" @click="closeServiceDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          style="margin-left:0px !important;"
          type="primary"
          @click="addWoService()"
          :loading="loading.woServiceSave"
          >{{ $t('common._common._add') }}</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="newToolPartToggle"
      :fullscreen="false"
      open="top"
      custom-class="fc-dialog-up Inventoryaddvaluedialog"
      :append-to-body="true"
    >
      <div v-if="individualToolTracking">
        <div class="new-header-container popup-container-new">
          <div class="fc-setup-modal-title">
            <i
              class="el-icon-back inv-id-bac-icon pointer"
              @click=";(individualToolList = []), (selectedTool = null)"
            ></i>
            {{ selectedTool.toolType.name }}
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
              class="el-input-textbox-full-border"
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
          <table class="invent-table-dialog setting-list-view-table width100">
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
            <tbody v-else-if="!individualToolListwrapper.length">
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
                      {{ $t('common.products.no_tools_present') }}
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow asset-hover-td"
                v-for="stool in individualToolListwrapper"
                :key="stool.id"
              >
                <td>
                  <div>{{ stool.serialNumber }}</div>
                  <div></div>
                </td>
                <td class="text-right">
                  <template
                    v-if="
                      selectedTool.invidualList.findIndex(
                        rt => rt.id === stool.id
                      ) > -1
                    "
                  >
                    <el-button
                      size="mini"
                      class="f-number-input f-number-input-no-border"
                      @click="
                        selectedTool.invidualList.splice(
                          selectedTool.invidualList.findIndex(
                            rt => rt.id === stool.id
                          ),
                          1
                        )
                      "
                      >{{ $t('common.products.added') }}</el-button
                    >
                  </template>
                  <template v-else-if="stool.checked">
                    <el-button
                      size="mini"
                      class="f-number-input f-number-input-no-border"
                      @click="stool.checked = false"
                      >{{ $t('common.products.added') }}</el-button
                    >
                  </template>
                  <template v-else>
                    <el-button
                      size="mini"
                      class="f-number-input"
                      @click="stool.checked = true"
                      >{{ $t('common._common._add') }}</el-button
                    >
                  </template>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-dialog-footer-parts-dialog">
          <div class="modal-dialog-footer-parts-dialog">
            <el-button
              class="modal-btn-save width100"
              style="margin-left:0px !important;"
              type="primary"
              @click="addindIvidualTrackingTool"
              :disabled="iTloading"
              >{{ $t('common._common._done') }}</el-button
            >
          </div>
        </div>
      </div>
      <div v-else>
        <div class="new-header-container popup-container-new">
          <div class="fc-setup-modal-title">
            {{ $t('common.products.tools_list') }}
          </div>
          <div class="store-room-bar" v-bind:class="{ active: !hidequrry }">
            <el-select
              v-model="selectedToolStoreList"
              multiple
              collapse-tags
              style="margin-left: 20px;"
              :placeholder="$t('common.products.select_storeroom')"
              class="fc-tag fc-input-full-border2"
            >
              <el-option
                v-for="(item, index) in temptoolStoreList"
                :key="index"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
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
              class="el-input-textbox-full-border"
              @blur="hidequrry = true"
              v-else
            >
              <i
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important;"
                class="el-input__icon el-icon-search pointer"
              ></i>
            </el-input>
          </div>
        </div>
        <div class="fc-inv-container-body">
          <table class="width100" v-if="allToolListLoading">
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
            class="setting-list-view-table width100 invent-table-dialog"
          >
            <tbody v-if="!toolsList.length">
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
                      {{ $t('common.products.no_tools_present') }}
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow asset-hover-td height100px"
                v-for="(stool, index) in toolsList"
                :key="stool.id"
              >
                <td
                  class="width80"
                  v-tippy="{ placement: 'top-start', animation: 'shift-away' }"
                  :title="stool.toolType.name + ' - ' + stool.storeRoom.name"
                >
                  {{ stool.toolType.name }}
                </td>
                <td class="text-align-inventory">
                  <template v-if="stool.toolType.isRotating">
                    <el-button
                      size="mini"
                      class="f-number-input f-number-input-no-border"
                      @click="
                        selectTool(stool, index)
                        stool.checked = true
                      "
                      v-if="stool.invidualList && stool.invidualList.length > 0"
                      >{{ stool.invidualList.length }}
                      {{ $t('common.products.qty_added') }}</el-button
                    >
                    <el-button
                      size="mini"
                      class="f-number-input"
                      @click="
                        selectTool(stool, index)
                        stool.checked = true
                        itemSerachQuerry = null
                      "
                      v-else
                      >{{ $t('common._common._add') }}</el-button
                    >
                  </template>
                  <template v-else-if="stool.addedQuantity === 0">
                    <el-button
                      size="mini"
                      class="f-number-input"
                      @click="
                        stool.addedQuantity = 1
                        stool.checked = true
                      "
                      >{{ $t('common._common._add') }}</el-button
                    >
                  </template>
                  <template v-else>
                    <el-input-number
                      size="mini"
                      class="f-number-input"
                      v-model="stool.addedQuantity"
                      :min="0"
                      :max="stool.quantity"
                      @change="handeler1(stool, index)"
                    ></el-input-number>
                  </template>
                  <div class="secondary-quantity-text pT10">
                    {{ $t('common.products.available') }}: {{ stool.quantity }}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-dialog-footer-parts-dialog">
          <el-button
            class="modal-btn-cancel"
            @click="cancelToolPartsDialog()"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            style="margin-left:0px !important;"
            type="primary"
            @click="addNewbulkTool()"
            >{{ $t('common._common._add') }}</el-button
          >
        </div>
      </div>
    </el-dialog>
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
    <div v-if="issueItemIRDialogVisibility">
      <inventory-issue-use
        :visibility.sync="issueItemIRDialogVisibility"
        :newIRIssueItem="issueItemIRDialogVisibility"
        :workorder="workorder"
        :woItemsList="workorderItemsList"
        @refreshItemList="loadWoItemParts"
      ></inventory-issue-use>
    </div>
    <div v-if="issueToolIRDialogVisibility">
      <inventory-issue-use
        :visibility.sync="issueToolIRDialogVisibility"
        :newIRIssueTool="issueToolIRDialogVisibility"
        :workorder="workorder"
        :woToolsList="workorderToolsList"
        @refreshToolList="loadWorkOrderToolParts"
      ></inventory-issue-use>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import Vue from 'vue'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import WOInvReqForm from 'src/pages/workorder/workorders/v3/widgets/ItemsAndLabor/WOInvReqForm.vue'
import InventoryIssueUse from 'pages/workorder/workorders/v1/InventoryIssueUse'
import InlineSvg from '@/InlineSvg'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import {
  getFilteredItemListWithParentModule,
  getFilteredToolListWithParentModule,
} from 'pages/Inventory/InventoryUtil'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  props: ['workorder', 'openWorkorderId', 'showLabour', 'canDisable'],
  mixins: [workorderMixin],
  data() {
    return {
      newItemPartToggle: false,
      selectedInventory: null,
      hidequrry: true,
      additionalCost: {
        name: null,
        cost: null,
      },
      loading: {
        deleteAdditionalCost: false,
        serviceList: false,
        woService: false,
        woServiceSave: false,
      },
      toolLoading: false,
      addissueditem: false,
      selectedInventoryList: [],
      inventoryRequestForWOLoading: false,
      iTloading: false,
      itemnsloading: false,
      toolsloading: false,
      labourLoading: false,
      selectedTool: null,
      workorderItemsList: [],
      workLabourTotalCost: null,
      workorderToolsList: [],
      workorderLabourList: [],
      workorderServiceList: [],
      newToolPartToggle: false,
      workOrderCostList: [],
      individualItemList: [],
      individualToolList: [],
      labourList: [],
      newLabourToggle: false,
      inventory: [],
      stockedTools: [],
      selectedLabour: [],
      tempStoreList: [],
      temptoolStoreList: [],
      itemSerachQuerry: null,
      selectedStoreList: [],
      selectedToolStoreList: [],
      inventoryRequestDialogVisibility: false,
      issueToolIRDialogVisibility: false,
      issueItemIRDialogVisibility: false,
      issueInventoryIRDialogVisibility: false,
      inventoryRequestList: null,
      allToolListLoading: false,
      inventoryListLoading: false,
      serviceAddDialogVisibility: false,
      allServiceList: [],
      items: [],
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.init()
  },
  mounted() {
    eventBus.$on('refresh-inventory-summary', () => {
      this.reload()
    })
  },
  components: {
    WOInvReqForm,
    InventoryIssueUse,
    InlineSvg,
  },
  computed: {
    inventoryList() {
      let list = []
      list = this.inventory.filter(item => {
        return !(item.itemType.approvalNeeded || item.storeRoom.approvalNeeded)
      })
      let self = this
      if (this.selectedStoreList.length) {
        list = list.filter(function(rt) {
          if (
            self.selectedStoreList.findIndex(rl => rl === rt.storeRoom.id) > -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.itemType.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            data.storeRoom.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            JSON.stringify(data.quantity)
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) === 0
          ) {
            return data
          }
        })
      } else {
        return list
      }
    },
    toolsList() {
      let list = []
      list = this.stockedTools.filter(item => {
        return !(item.toolType.approvalNeeded || item.storeRoom.approvalNeeded)
      })
      let self = this
      if (this.selectedToolStoreList.length) {
        list = list.filter(function(rt) {
          if (
            self.selectedToolStoreList.findIndex(rl => rl === rt.storeRoom.id) >
            -1
          ) {
            return rt
          }
        })
      }
      if (this.itemSerachQuerry) {
        let self = this
        return list.filter(data => {
          if (
            data.toolType.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            data.storeRoom.name
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) > -1 ||
            JSON.stringify(data.quantity)
              .toLowerCase()
              .indexOf(self.itemSerachQuerry.toLowerCase()) === 0
          ) {
            return data
          }
        })
      } else {
        return list
      }
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
    individualToolListwrapper() {
      let list = this.individualToolList
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
    workToolTotalCost() {
      if (this.workOrderCostList.length) {
        if (this.workOrderCostList.find(rt => rt.costTypeEnum === 'tools')) {
          return this.workOrderCostList.find(rt => rt.costTypeEnum === 'tools')
            .cost
        } else {
          return 0
        }
      } else {
        return 0
      }
    },
    actionRule() {
      if (this.canEdit) {
        return true
      } else {
        return false
      }
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
    workServiceCost() {
      return (
        (
          (this.workOrderCostList || []).find(
            rt => rt.costTypeEnum === 'service'
          ) || {}
        ).cost || 0
      )
    },
    individualTracking() {
      if (this.inventory && this.inventory.length && this.selectedInventory) {
        return true
      } else {
        return false
      }
    },
    individualToolTracking() {
      if (this.stockedTools && this.stockedTools.length && this.selectedTool) {
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
    init() {
      this.loadWoItemParts(true)
      this.loadWorkOrderToolParts(true)
      this.getWorkOrderCostList(true)
      this.loadWorkOrderLabour(true)
      this.loadWorkOrderService(true)
      this.loadItem(true)
      this.loadInventoryRequest()
    },
    reload() {
      this.loadWoItemParts(true)
      this.loadInventoryRequest()
    },
    loadInventory() {
      let self = this
      this.itemSerachQuerry = null
      this.inventoryListLoading = true
      this.$http
        .get(
          'v2/item/view/all?showForWorkorder=true&includeServingSite=true&siteId=' +
            this.workorder.siteId
        )
        .then(response => {
          this.inventoryListLoading = false
          let inventory = []
          inventory =
            response.data.result && response.data.result.items
              ? response.data.result.items
              : []
          if (response) {
            for (let item of inventory) {
              item.checked = false
              item.addedQuantity = 0
              item.invidualList = []
              if (
                item.storeRoom &&
                self.tempStoreList.findIndex(
                  rt => rt.id === item.storeRoom.id
                ) < 0
              ) {
                self.tempStoreList.push(item.storeRoom)
              }
            }
            let self2 = self
            self.inventory = inventory.filter(function(rt) {
              if (self2.itemshowRule(rt)) {
                return rt
              }
            })
          }
        })
    },
    loadToolslist() {
      let self = this
      this.itemSerachQuerry = null
      this.allToolListLoading = true
      this.$http.get('v2/tool/view/all').then(response => {
        let stockedTools = []
        stockedTools =
          response.data.result && response.data.result.tool
            ? response.data.result.tool
            : []
        this.allToolListLoading = false
        if (response) {
          for (let item of stockedTools) {
            item.checked = false
            item.addedQuantity = 0
            item.invidualList = []
            if (
              item.storeRoom &&
              self.temptoolStoreList.findIndex(
                rt => rt.id === item.storeRoom.id
              ) < 0
            ) {
              self.temptoolStoreList.push(item.storeRoom)
            }
          }
          let self2 = self
          self.stockedTools = stockedTools.filter(function(rt) {
            if (self2.toolshowRule(rt)) {
              return rt
            }
          })
        }
      })
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
    getToolCount(worktool) {
      if (worktool) {
        let diff
        if (worktool.requestedLineItem) {
          diff =
            Number(worktool.requestedLineItem.issuedQuantity) -
            Number(worktool.quantity)
        } else if (worktool.parentTransactionId !== -1) {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.remainingQuantity) -
            Number(worktool.quantity)
        } else {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.tempTool.currentQuantity) -
            Number(worktool.quantity)
        }
        if (Number(worktool.quantity) < 0) {
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
    getToolQuantityState(worktool) {
      if (worktool) {
        let diff
        if (worktool.requestedLineItem) {
          diff =
            Number(worktool.requestedLineItem.issuedQuantity) -
            Number(worktool.quantity)
        } else if (worktool.parentTransactionId !== -1) {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.remainingQuantity) -
            Number(worktool.quantity)
        } else {
          diff =
            Number(worktool.tempQuantity) +
            Number(worktool.tempTool.currentQuantity) -
            Number(worktool.quantity)
        }
        if (Number(worktool.quantity) < 0) {
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
    addNewWOLabour() {
      this.loadLabour(true)
      this.newLabourToggle = true
    },
    addNewWOItem() {
      let self = this
      this.individualItemList = []
      this.inventory = this.inventory.filter(function(rt) {
        if (self.itemshowRule(rt)) {
          rt.checked = false
          return rt
        }
      })
      this.newItemPartToggle = true
    },
    addNewWOTool() {
      let self = this
      this.individualToolList = []
      this.stockedTools = this.stockedTools.filter(function(rt) {
        if (self.toolshowRule(rt)) {
          rt.checked = false
          return rt
        }
      })
      this.newToolPartToggle = true
    },
    ticketstatusvalue(id) {
      if (id !== null) {
        let statusObj = this.$store.getters.getTicketStatus(id, 'workorder')
        if (!statusObj) {
          return ''
        }
        let status = statusObj.status
        if (status) {
          return status
        } else {
          return ''
        }
      }
      return ''
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
    toolshowRule(invt) {
      if (!invt.toolType.isRotating) {
        let tempWorkItem = this.workorderToolsList.filter(i => {
          return i.tool.id === invt.id
        })
        let check = true
        tempWorkItem.forEach(i => {
          if (i.parentTransactionId === -1) {
            check = false
          }
        })
        return check
      } else {
        if (invt.toolType.currentQuantity > 0) {
          return true
        } else {
          return false
        }
      }
    },
    addAdditionalCost() {
      let self = this
      if (this.additionalCost.name && this.additionalCost.cost !== null) {
        let param = {
          workorderCost: {
            name: this.additionalCost.name,
            parentId: {
              id: this.openWorkorderId,
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
    updateAdditionalCost(data) {
      if (data) {
        let self = this
        let param = {
          id: data.id,
          name: data.name,
          parentId: {
            id: this.openWorkorderId,
          },
          costType: 5,
          cost: Number(data.cost),
        }
        this.$http.post('v2/workorderCosts/update', param).then(response => {
          self.actionToWorkorderStatusChange()
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common._common.cost_edited_successfully')
            )
            self.getWorkOrderCostList()
          } else {
            self.$message.error(response.data.message)
          }
        })
      }
    },
    deleteAdditionalCost(id) {
      if (id) {
        let self = this
        let param = {
          workordercostId: [id],
          parentId: this.openWorkorderId,
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
    deleteWorkorderLabour(workLabour) {
      let self = this
      let param = { parentId: this.openWorkorderId, workorderLabourIds: [] }
      param.workorderLabourIds.push(workLabour.id)
      this.$http
        .post('v2/workorderLabour/delete', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success(
              this.$t('common.products.labour_deleted_successfully')
            )
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderLabour()
            self.getWorkOrderCostList()
          } else {
            self.$message.error(response.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
    deleteWorkorderTool(workTool, index) {
      if (!workTool.tool.hasOwnProperty('storeRoom')) {
        this.workorderToolsList.pop()
      } else {
        let self = this
        let param = { parentId: this.openWorkorderId, workorderToolsIds: [] }
        param.workorderToolsIds.push(workTool.id)
        this.$http
          .post('v2/workorderTools/delete', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.actionToWorkorderStatusChange()
              self.loadToolslist()
              self.loadWorkOrderToolParts()
              self.getWorkOrderCostList()
              self.$message.success('Tool Deleted')
              self.workorderToolsList.splice(index, 1)
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch()
      }
    },
    deleteWorkorderItem(workItem, index) {
      let self = this
      if (!workItem.hasOwnProperty('purchasedItem')) {
        this.workorderItemsList.pop()
      } else {
        let param = { parentId: this.openWorkorderId, workorderItemsId: [] }
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
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(error => {
            console.log(error)
          })
      }
    },
    cancelLabourDialog() {
      this.newLabourToggle = false
    },
    cancelToolPartsDialog() {
      this.newToolPartToggle = false
      this.selectedTool = null
      this.selectedTool = null
    },
    getWorkOrderCostList(loading) {
      let self = this
      this.$http
        .get('v2/workorderCostsList/parent/' + this.openWorkorderId)
        .then(response => {
          if (!loading) {
            self.itemnsloading = false
          }
          self.workOrderCostList = response.data.result.workorderCost
        })
    },
    getCurrentTime() {
      return moment()
        .tz(Vue.prototype.$timezone)
        .valueOf()
    },
    addLabourSave() {
      let self = this
      let param = { workorderLabourList: [] }
      if (!this.labourList.length) {
        return
      }
      for (let i = 0; i < this.labourList.length; i++) {
        let labour = this.labourList[i]
        if (labour.checked) {
          let temp = {
            parentId: this.openWorkorderId,
            startTime: -1,
            endTime: -1,
            labour: labour,
          }
          param.workorderLabourList.push(temp)
          this.labourList[i] = false
        }
      }

      this.labourLoading = true
      this.$http
        .post('/v2/workorderLabour/addOrUpdate', param)
        .then(response => {
          self.labourLoading = true
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common._common.added_available_labour_successfully')
            )

            self.actionToWorkorderStatusChange()
            self.loadWorkOrderLabour()
            self.getWorkOrderCostList()
          }
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_add_labour'))
          self.labourLoading = false
        })
      this.newLabourToggle = false
    },
    addToolPartSave() {
      let self = this
      let param = { workorderToolsList: [] }
      let purchasedTools = []
      if (this.individualToolList.length) {
        purchasedTools = this.individualToolList.map(function(rt) {
          if (rt.checked) {
            return rt.id
          }
        })
        purchasedTools = purchasedTools.filter(rt => typeof rt !== 'undefined')
      }
      let temp = {
        parentId: this.openWorkorderId,
        tool: { id: this.selectedTool },
        quantity: 1,
        assetIds: purchasedTools,
      }
      param.workorderToolsList.push(temp)
      this.toolsloading = true
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            self.$message.success(this.$t('common._common.added_successfully'))
            self.selectedTool = null
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderToolParts(false, true)
            self.getWorkOrderCostList()
          }
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_update'))
        })
      this.newToolPartToggle = false
    },
    bulkaddItem() {
      let selectedItems = this.inventory.filter(rt => rt.checked)
      let bulkItem = selectedItems.filter(function(rt) {
        if (rt && rt.itemType && !rt.itemType.isRotating) {
          return rt
        }
      })
      if (bulkItem.length) {
        this.addbulkItem(bulkItem)
        if (!this.individualItemList.length) {
          this.newItemPartToggle = false
        }
      }
      if (this.individualTracking && this.individualItemList.length) {
        this.addbulkItem(false, this.individualItemList)
        this.newItemPartToggle = false
      }
    },
    bulkaddTool() {
      let selectedItems = this.stockedTools.filter(rt => rt.checked)
      let bulkItem = selectedItems.filter(function(rt) {
        if (rt && rt.toolType && !rt.toolType.isRotating) {
          return rt
        }
      })
      if (bulkItem.length) {
        this.addbulkTool(bulkItem)
        if (!this.individualToolList.length) {
          this.newToolPartToggle = false
        }
      }
      if (this.individualToolTracking && this.individualToolList.length) {
        this.addbulkTool(false, this.individualToolList)
        this.newToolPartToggle = false
      }
    },
    addbulkTool(tools, individualToolList) {
      let self = this
      let param = { workorderToolsList: [] }
      let obj = {}
      let assetIds = []
      let data = []
      if (individualToolList && individualToolList.length) {
        for (let i = 0; i < individualToolList.length; i++) {
          data = individualToolList[i]
          if (data.checked) {
            assetIds = [data.id]
            obj = {
              parentId: this.openWorkorderId,
              tool: { id: data.tool.id },
              quantity: 1,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        }
      } else if (tools) {
        data = []
        for (let i = 0; i < tools.length; i++) {
          let data = tools[i]
          obj = {
            parentId: this.openWorkorderId,
            tool: { id: data.id },
            quantity: 1,
            assetIds: assetIds,
          }
          param.workorderToolsList.push(obj)
        }
      }
      this.toolsloading = true
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            self.$message.success(this.$t('common._common.added_successfully'))
            self.selectedTool = null
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderToolParts(false, true)
            self.getWorkOrderCostList()
          }
        })
        .catch(() => {
          this.$message.error(this.$t('common.wo_report.unable_to_update'))
        })
    },
    addbulkItem(items, individualItemList) {
      let param = { workorderItems: [] }
      let obj = {}
      let assetIds = []
      let data = []
      if (individualItemList && individualItemList.length) {
        for (let i = 0; i < individualItemList.length; i++) {
          data = individualItemList[i]
          if (data.checked) {
            assetIds = [data.id]
            obj = {
              parentId: this.openWorkorderId,
              item: { id: data.item.id },
              quantity: 1,
              assetIds: assetIds,
              remainingQuantity: 0,
            }
            param.workorderItems.push(obj)
          }
        }
      } else if (items) {
        data = []
        for (let i = 0; i < items.length; i++) {
          let data = items[i]
          obj = {
            parentId: this.openWorkorderId,
            item: { id: data.id },
            quantity: 1,
            assetIds: assetIds,
            remainingQuantity: 0,
          }
          param.workorderItems.push(obj)
        }
      }

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
        })
        .catch(() => {})
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
              parentId: this.openWorkorderId,
              item: { id: item.id },
              quantity: item.invidualList.length,
              assetIds: assetIds,
            }
            param.workorderItems.push(obj)
          }
        } else {
          if (item.addedQuantity > 0) {
            obj = {
              parentId: this.openWorkorderId,
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
        })
        .catch(error => {
          console.log(error)
        })
    },
    addNewbulkTool() {
      let self = this
      let param = { workorderToolsList: [] }
      let obj = {}
      let assetIds = []
      this.toolsList.forEach(item => {
        obj = {}
        assetIds = []
        if (item.toolType.isRotating) {
          if (item.invidualList.length) {
            assetIds = item.invidualList.map(rl => rl.id)
            obj = {
              parentId: this.openWorkorderId,
              tool: { id: item.id },
              quantity: item.invidualList.length,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        } else {
          if (item.addedQuantity > 0) {
            obj = {
              parentId: this.openWorkorderId,
              tool: { id: item.id },
              quantity: item.addedQuantity,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        }
      })
      this.selectedTool = null
      this.individualItemList = []
      this.individualToolList = []
      this.newToolPartToggle = false
      this.$http
        .post('/v2/workorderTools/addOrUpdate', param)
        .then(response => {
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common.products.tools_added_successfully')
            )
            self.selectedTool = null
            self.actionToWorkorderStatusChange()
            self.loadWorkOrderToolParts(false, true)
            self.getWorkOrderCostList()
          }
        })
        .catch(() => {})
    },
    actionToWorkorderStatusChange() {
      this.fetchWo()
    },
    cancelItemPartsDialog() {
      this.newItemPartToggle = false
      this.selectedInventory = null
      this.selectedTool = null
    },
    async loadItem() {
      this.items = await getFilteredItemListWithParentModule('workorder')
    },
    async loadStockedTools() {
      this.tools = await getFilteredToolListWithParentModule('workorder')
    },
    changeWorkLabourField(workLabour, index, changedfield) {
      let self = this
      let duration = Number(workLabour.duration).toFixed(2)
      let param = { workorderLabourList: [] }
      if (workLabour.startTime === -1) {
        workLabour.startTime = null
      }
      if (workLabour.endTime === -1) {
        workLabour.endTime = null
      }
      let startTime =
        workLabour.startTime !== null
          ? moment(workLabour.startTime)
              .tz(this.$timezone)
              .valueOf()
          : workLabour.startTime
      let endTime =
        workLabour.endTime !== null
          ? moment(workLabour.endTime)
              .tz(this.$timezone)
              .valueOf()
          : workLabour.endTime

      let temp = {
        id: workLabour.id,
        parentId: this.openWorkorderId,
        duration: changedfield === 3 || changedfield === 1 ? duration : -1,
        labour: workLabour.labour,
        startTime: startTime ? startTime : -99,
        endTime: changedfield === 2 ? endTime : -99,
      }
      param.workorderLabourList.push(temp)
      this.$http
        .post('/v2/workorderLabour/addOrUpdate', param)
        .then(response => {
          self.actionToWorkorderStatusChange()
          if (response.data.responseCode !== 0) {
            self.$message.error(response.data.message)
          } else {
            self.$message.success(
              this.$t('common._common.updated_successfully')
            )
            self.loadWorkOrderLabour()
          }
        })
        .catch(() => {})
    },
    changeWorkToolField(workTool, index, durationHour, changedField) {
      if (
        Number(workTool.quantity) <=
        Number(workTool.tempQuantity) + Number(workTool.tempTool.quantity)
      ) {
        let param = { workorderToolsList: [] }
        if (workTool.issueTime === -1) {
          workTool.issueTime = null
        }
        if (workTool.returnTime === -1) {
          workTool.returnTime = null
        }
        let issueTime =
          workTool.issueTime !== null
            ? moment(workTool.issueTime)
                .tz(this.$timezone)
                .valueOf()
            : workTool.issueTime
        let returnTime =
          workTool.returnTime !== null
            ? moment(workTool.returnTime)
                .tz(this.$timezone)
                .valueOf()
            : workTool.returnTime
        let temp = {}
        let duration = Number(workTool.duration)
        if (durationHour) {
          duration = Number(workTool.durationInHour).toFixed(2)
          if (issueTime !== null && issueTime > 0 && duration > 0) {
            returnTime = Number(issueTime) + Number(duration)
          }
        } else {
          duration = Number(duration).toFixed(2)
        }
        if (issueTime === 0 || issueTime === null) {
          issueTime = -1
        }
        if (returnTime === 0 || returnTime === null) {
          returnTime = -1
        }
        this.workorderToolsList[index].duration = duration
        temp = {
          parentId: this.openWorkorderId,
          id: workTool.id,
          tool: { id: workTool.tool.id },
          quantity: parseInt(workTool.quantity),
          duration: changedField == 1 || changedField == 3 ? duration : -1,
          issueTime: issueTime ? issueTime : -99,
          returnTime: changedField === 2 ? returnTime : -99,
          requestedLineItem: workTool.requestedLineItem,
          parentTransactionId: workTool.parentTransactionId,
        }

        param.workorderToolsList.push(temp)
        let self = this
        this.$http
          .post('/v2/workorderTools/addOrUpdate', param)
          .then(response => {
            if (response.data.responseCode === 0) {
              self.$message.success(
                this.$t('common._common.edited_successfully')
              )
              self.actionToWorkorderStatusChange()
              self.loadWorkOrderToolParts()
              self.getWorkOrderCostList()
            } else {
              self.$message.error(response.data.message)
            }
          })
      } else {
        this.$message.error(this.$t('common.dashboard.quantity_should_be_less'))
      }
    },
    changeWorkItemQuantity(workItem) {
      if (
        workItem.quantity <=
        workItem.tempQuantity + workItem.tempItem.quantity
      ) {
        let param = { workorderItems: [] }
        let temp = {
          parentId: this.openWorkorderId,
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
    loadWorkOrderToolParts(loading, inventory) {
      if (loading) {
        this.toolLoading = true
      }
      let self = this
      this.$http
        .get('/v2/workorderToolsList/parent/' + this.openWorkorderId)
        .then(response => {
          let wktool = {}
          this.workorderToolsList = response.data.result.workorderTools
          for (let workTool in response.data.result.workorderTools) {
            this.workorderToolsList[
              workTool
            ].durationInHour = this.workorderToolsList[
              workTool
            ].duration.toFixed(2)
            this.workorderToolsList[workTool].issueTime =
              this.workorderToolsList[workTool].issueTime === -1 ||
              this.workorderToolsList[workTool].issueTime === null
                ? null
                : this.workorderToolsList[workTool].issueTime
            this.workorderToolsList[workTool].returnTime =
              this.workorderToolsList[workTool].returnTime === -1 ||
              this.workorderToolsList[workTool].returnTime === null
                ? null
                : this.workorderToolsList[workTool].returnTime
            wktool = response.data.result.workorderTools[workTool]
            wktool.tempQuantity = wktool.quantity
            wktool.tempTool = wktool.tool
          }
          self.workorderToolsList = self.$helpers.cloneObject(
            response.data.result.workorderTools
          )
          if (loading) {
            self.loadToolslist()
          }
          if (inventory) {
            self.loadToolslist()
          }
          this.toolLoading = false
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_load_tools'))
          this.toolLoading = false
        })
      this.loadStockedTools()
    },
    loadLabour() {
      let self = this
      this.$http
        .get('/v2/labour/labourList')
        .then(response => {
          self.labourList = response.data.result.labours
          for (let i = 0; i < self.labourList.length; i++) {
            let labourItem = self.labourList[i]
            if (self.selectedLabour.includes(labourItem.id)) {
              self.labourList.splice(i, 1)
            }
          }
        })
        .catch(error => {
          console.log(error)
          self.$message.error(this.$t('common.wo_report.unable_to_labor_list'))
        })
    },
    loadWorkOrderLabour(loading) {
      if (loading) {
        this.labourLoading = true
      }
      let self = this
      this.$http
        .get('/v2/workorderLabourList/parent/' + this.openWorkorderId)
        .then(response => {
          this.workLabourTotalCost = 0
          for (let workLabour in response.data.result.workorderLabour) {
            this.workLabourTotalCost =
              this.workLabourTotalCost +
              response.data.result.workorderLabour[workLabour].cost
          }
          self.workorderLabourList = self.$helpers.cloneObject(
            response.data.result.workorderLabour
          )
          self.selectedLabour = []
          for (let index in self.workorderLabourList) {
            let workLabourItem = self.workorderLabourList[index]
            self.selectedLabour.push(workLabourItem.labour.id)
            workLabourItem.startTime =
              workLabourItem.startTime <= 0 ? null : workLabourItem.startTime
            workLabourItem.endTime =
              workLabourItem.endTime <= 0 ? null : workLabourItem.endTime
            workLabourItem.duration = workLabourItem.duration.toFixed(2)
          }
          self.getWorkOrderCostList()
          self.labourLoading = false
        })
        .catch(error => {
          console.log(error)
          self.$message.error(this.$t('common.wo_report.unable_to_load_labor'))
          self.labourLoading = false
        })
    },
    loadWoItemParts(loading, inventory) {
      this.loadWorkOrderItemParts = []
      let self = this
      let self1 = this
      if (loading) {
        this.itemnsloading = true
      }
      this.$http
        .get('/v2/workorderItemsList/parent/' + this.openWorkorderId)
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
        })
        .catch(() => {
          self.$message.error(this.$t('common.wo_report.unable_to_load_parts'))
        })
      this.loadItem()
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
    async loadindividualToolTrackingList(items) {
      this.individualToolList = []
      this.iTloading = true

      let filters = {
        rotatingTool: {
          operatorId: 36,
          value: items.map(rt => rt.id + ''),
        },
        isUsed: { operatorId: 15, value: [false + ''] },
      }
      let individualToolList = await this.$util.getFilteredAssetList(filters)

      this.individualToolList = individualToolList.map(item => ({
        ...item,
        checked: false,
      }))
      this.iTloading = false
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
    async loadWorkOrderService(loading) {
      if (loading) {
        this.loading.woService = true
      }
      let {
        data,
        error,
      } = await API.get(
        `/v2/workorderServiceList/parent/${this.openWorkorderId}`,
        null,
        { force: true }
      )
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t('common._common.error_occured_while_fetching_workorder')
        )
      } else {
        let { workorderServices = [] } = data
        workorderServices.forEach(woService => {
          if (isEmpty(woService.endTime)) {
            woService.endTime = null
          }
          if (isEmpty(woService.startTime)) {
            woService.startTime = null
          }
          if (!isEmpty(woService.duration)) {
            woService.duration = Number(woService.duration).toFixed(2)
          }
        })
        this.workorderServiceList = workorderServices || []
      }
      this.loading.woService = false
    },
    addNewWOService() {
      this.loadAllServices()
      this.serviceAddDialogVisibility = true
    },
    async loadAllServices() {
      let allWoServiceIds = (this.workorderServiceList || []).map(woService =>
        this.$getProperty(woService, 'service.id', -1)
      )
      this.loading.serviceList = true
      let { data, error } = await API.get('/v2/service/all')
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t('common._common.error_occured_while_fetching_services_list')
        )
      } else {
        let { services = [] } = data
        if (!isEmpty(services)) {
          services =
            services.filter(service => !allWoServiceIds.includes(service.id)) ||
            []
          services.forEach(service => {
            this.$setProperty(service, 'checked', false)
          })
        }
        this.allServiceList = services || []
      }
      this.loading.serviceList = false
    },
    closeServiceDialog() {
      this.serviceAddDialogVisibility = false
    },
    async addWoService() {
      let param = { workorderServiceList: [] }
      let { allServiceList = [] } = this
      allServiceList.forEach(service => {
        if (service.checked) {
          let tempObj = {
            parentId: this.openWorkorderId,
            startTime: -1,
            endTime: -1,
            service,
          }
          param.workorderServiceList.push(tempObj)
        }
      })
      if (!isEmpty(param.workorderServiceList)) {
        this.loading.woServiceSave = true
        let { error } = await API.post(
          '/v2/workorderService/addOrUpdate',
          param
        )
        if (error) {
          this.$message.error(
            error ||
              this.$t(
                'common._common.error_occured_while_adding_workorder_services'
              )
          )
        } else {
          this.$message.success(
            this.$t('common._common.added_service_successfully')
          )
          this.closeServiceDialog()
          this.serviceUpdateActions()
        }
        this.loading.woServiceSave = false
      } else {
        this.closeServiceDialog()
      }
    },
    serviceUpdateActions() {
      this.actionToWorkorderStatusChange()
      this.loadWorkOrderService(true)
      this.getWorkOrderCostList()
    },
    async deleteWoService(woService) {
      let param = { parentId: this.openWorkorderId, workorderServiceIds: [] }
      param.workorderServiceIds.push(woService.id)
      this.loading.woService = true
      let { error } = await API.post('v2/workorderService/delete', param)
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t(
              'common._common.error_occured_while_deleting_workorder_service'
            )
        )
      } else {
        this.$message.success(
          this.$t('common.products.service_deleted_successfully')
        )
        this.serviceUpdateActions()
      }
      this.loading.woService = true
    },
    async changeWorkServiceField(workService, changedfield) {
      let duration = Number(workService.duration).toFixed(2)
      let param = { workorderServiceList: [] }
      if (isEmpty(workService.startTime)) {
        workService.startTime = null
      }
      if (isEmpty(workService.endTime)) {
        workService.endTime = null
      }
      let startTime =
        workService.startTime !== null
          ? moment(workService.startTime)
              .tz(this.$timezone)
              .valueOf()
          : workService.startTime
      let endTime =
        workService.endTime !== null
          ? moment(workService.endTime)
              .tz(this.$timezone)
              .valueOf()
          : workService.endTime

      let temp = {
        id: workService.id,
        parentId: this.openWorkorderId,
        duration: changedfield === 3 || changedfield === 1 ? duration : -1,
        service: workService.service,
        startTime: startTime ? startTime : -99,
        endTime: changedfield === 2 ? endTime : -99,
        quantity: workService.quantity,
      }
      param.workorderServiceList.push(temp)
      let { error } = await API.post('/v2/workorderService/addOrUpdate', param)
      if (error) {
        let { message } = error
        this.$message.error(
          message ||
            this.$t(
              'common._common.error_occured_while_updating_workorder_service'
            )
        )
      } else {
        this.$message.success(this.$t('common._common.updated_successfully'))
        this.loadWorkOrderService(true)
        this.getWorkOrderCostList()
      }
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
<style scoped>
.fc-create-btn.disabled:hover {
  box-shadow: 0 2px 4px 0 #ffc9de;
}

.v3-layout-override {
  flex-direction: row !important;
}
</style>
