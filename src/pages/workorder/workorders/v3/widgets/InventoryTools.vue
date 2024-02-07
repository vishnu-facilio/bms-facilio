<template>
  <div class="p30 white-bg-block mB100">
    <!-- widget header -->
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

    <!-- widget body -->
    <div
      class="inventory-table inventory-tool-table inventory-table-body-border-none overflow-x mT20"
      style="border: 1px solid #eceef1;"
      ref="inventoryToolsWidget"
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
            <th
              class="border-top-none tool-header-sticky"
              style="width: 300px;"
            >
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
                <currency :value="0" :recordCurrency="recordCurrency"></currency>
              </div>
            </td>
            <td class="pT10 pB10">
              <div class="fc-grey3-13 text-right">
                <currency :value="0" :recordCurrency="recordCurrency"></currency>
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
              <td class="pT10 pB10 tool-header-sticky">
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
                        ? `${$t(
                            'common.products.available_tools'
                          )} ${getToolCount(workTool)}`
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
                      formatTime(Number(workTool.issueTime), 'hh:mm a', false)
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
                      @change="changeWorkToolField(workTool, index, false, 1)"
                      :default-time="['12:00:00']"
                    ></el-date-picker>
                  </div>
                </div>
              </td>
              <td class="pT10 pB10 pL20">
                <div class="in-Quantity width110px">
                  <div class="fc-black-13 text-right">
                    {{
                      formatTime(Number(workTool.returnTime), 'hh:mm a', false)
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
                      @change="changeWorkToolField(workTool, index, false, 2)"
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
                  <currency :value="workTool.tool.rate" :recordCurrency="recordCurrency"></currency>
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
              <td class="pT10 pB10 tool-header-sticky">
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
                      formatTime(Number(workTool.issueTime), 'hh:mm a', false)
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
                      formatTime(Number(workTool.returnTime), 'hh:mm a', false)
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
                  <currency :value="workTool.tool.rate" :recordCurrency="recordCurrency"></currency>
                </div>
              </td>
              <td class="pT10 pB10">
                <div class="text-right">
                  <currency :value="workTool.cost" :recordCurrency="recordCurrency"></currency>
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
              <td class="pT10 pB10 tool-header-sticky">
                <div class="pL17" style="width: 300px;">
                  <div
                    v-if="workTool.tool.toolType.isRotating && workTool.asset"
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
                        ? `${$t(
                            'common.products.available_tools'
                          )} ${getToolCount(workTool)}`
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
                      formatTime(Number(workTool.issueTime), 'hh:mm a', false)
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
                      @change="changeWorkToolField(workTool, index, false, 1)"
                    ></el-date-picker>
                  </div>
                </div>
              </td>
              <td class="pT10 pB10 pL20">
                <div class="in-Quantity width110px">
                  <div class="fc-black-13 text-right">
                    {{
                      formatTime(Number(workTool.returnTime), 'hh:mm a', false)
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
                      @change="changeWorkToolField(workTool, index, false, 2)"
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
                  <currency :value="workTool.tool.rate" :recordCurrency="recordCurrency"></currency>
                </div>
              </td>
              <td class="pT10 pB10 worktool-cost-sticky">
                <div class="text-right">
                  <currency :value="workTool.cost" :recordCurrency="recordCurrency"></currency>
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

    <!-- widget footer -->
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
          <currency :value="workToolTotalCost" :recordCurrency="recordCurrency"></currency>
        </div>
      </div>
    </div>

    <!-- add issued tool dialog -->
    <template v-if="issueToolIRDialogVisibility">
      <inventory-issue-use
        :visibility.sync="issueToolIRDialogVisibility"
        :newIRIssueTool="issueToolIRDialogVisibility"
        :workorder="workorder"
        :woToolsList="workorderToolsList"
        @refreshToolList="loadWorkOrderToolParts"
      ></inventory-issue-use>
    </template>

    <!-- add tool dialog -->
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
              @change="loadToolslist"
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
              @change="loadToolslist"
            >
              <i
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important;"
                class="el-input__icon el-icon-search pointer"
              ></i>
            </el-input>
          </div>
          <div class="pagination">
            <pagination
              v-if="newToolPartToggle"
              :total="listCount"
              :perPage="20"
              :hidePopover="true"
              @onPageChanged="onPageChange"
            ></pagination>
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
                <td class="width80">
                  <div>{{ $getProperty(stool, 'toolType.name') }}</div>

                  <div
                    class="item-description"
                    :class="getSubjectClassname(stool, index)"
                  >
                    {{ $getProperty(stool, 'toolType.description') }}
                  </div>
                  <div class="flex flex-direction-row pT6">
                    <a
                      v-if="canShowMore(stool)"
                      @click="toggleVisibility(index)"
                      class="text-capitalize letter-spacing0_3 f13 pR8 pointer"
                      >{{ showMoreLinkText(index) }}</a
                    >
                  </div>
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
                      :max="stool.currentQuantity"
                      @change="handeler1(stool, index)"
                    ></el-input-number>
                  </template>
                  <div class="secondary-quantity-text pT10">
                    {{
                      `${$t('common.products.available')}: ${
                        stool.currentQuantity
                      }`
                    }}
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
  </div>
</template>

<script>
import moment from 'moment-timezone'

import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import InventoryIssueUse from 'pages/workorder/workorders/v1/InventoryIssueUse'
import InlineSvg from '@/InlineSvg'
import { eventBus } from '@/page/widget/utils/eventBus'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getFilteredToolListWithParentModule } from 'pages/Inventory/InventoryUtil'
import Pagination from './InventoryPagination'
import { htmlToText } from '@facilio/utils/filters'

export default {
  props: ['details', 'resizeWidget'],
  mixins: [workorderMixin],
  components: {
    InventoryIssueUse,
    InlineSvg,
    Pagination,
  },
  data() {
    return {
      selectedInventory: null,
      hidequrry: true,
      additionalCost: {
        name: null,
        cost: null,
      },
      loading: {
        deleteAdditionalCost: false,
      },
      toolLoading: false,
      selectedInventoryList: [],
      inventoryRequestForWOLoading: false,
      iTloading: false,
      toolsloading: false,
      selectedTool: null,
      workorderToolsList: [],
      newToolPartToggle: false,
      workOrderCostList: [],
      individualToolList: [],
      inventory: [],
      stockedTools: [],
      tempStoreList: [],
      selectedStoreList: [],
      inventoryRequestDialogVisibility: false,
      issueToolIRDialogVisibility: false,
      issueInventoryIRDialogVisibility: false,
      inventoryRequestList: null,
      allToolListLoading: false,
      inventoryListLoading: false,
      listCount: null,
      page: 1,
      searchPage: 1,
      selectedIndex: [],
      itemSerachQuerry: null,
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
  computed: {
    workorder() {
      return this.details.workorder
    },
    toolsList() {
      let list = []
      list = this.stockedTools.filter(item => {
        return !(item.toolType.approvalNeeded || item.storeRoom.approvalNeeded)
      })
      return list
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
    async init() {
      this.loadWorkOrderToolParts(true)
      await this.getStoreroom()
      this.loadItemsCount()
      this.getWorkOrderCostList(true)
    },
    async cancelToolPartsDialog() {
      this.newToolPartToggle = false
      this.selectedTool = null
      this.page = 1
      this.searchPage = 1
      this.itemSerachQuerry = null
      this.selectedIndex = []
      await this.getStoreroom()
      this.loadInventory()
    },
    reload() {
      this.loadWoItemParts(true)
    },
    showMoreLinkText(index) {
      let { selectedIndex } = this
      return selectedIndex.indexOf(index) !== -1 ? 'View Less' : 'View More'
    },
    canShowMore(inventory) {
      let description = this.$getProperty(inventory, 'toolType.description')
      if (description) {
        let descriptionLength = description.length
        let htmlToStringLength = htmlToText(description).split(/\r\n|\r|\n/)
          .length
        return htmlToStringLength > 2 || descriptionLength > 130
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
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['inventoryToolsWidget']
        if (container) {
          let height = container.scrollHeight + 200
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
    onPageChange(pageNo) {
      let { itemSerachQuerry } = this
      this.page = pageNo
      if (!isEmpty(itemSerachQuerry)) {
        this.searchPage = pageNo
      }
      this.loadToolslist()
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
      }
      let url = `v2/tool/view/all`
      let { data, error } = await API.get(url, params)

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.listCount = this.$getProperty(data, 'count')
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
        this.tempStoreList = storeRooms
        let { id } = storeRooms[0] || {}
        this.selectedStoreList = id
      }
    },
    loadToolslist() {
      this.allToolListLoading = true
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
      }
      API.get('v2/tool/view/all', params).then(({ data }) => {
        this.allToolListLoading = false
        let inventory = []
        inventory = this.$getProperty(data, 'tool', [])
        if (data) {
          for (let item of inventory) {
            item.checked = false
            item.addedQuantity = 0
            item.invidualList = []
          }
          this.stockedTools = inventory
        }
      })
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
    addNewWOTool() {
      this.individualToolList = []
      this.newToolPartToggle = true
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
    actionToWorkorderStatusChange() {
      this.fetchWo()
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
          eventBus.$emit('reloadOverallCost')
        })
    },
    deleteWorkorderTool(workTool, index) {
      if (!workTool.tool.hasOwnProperty('storeRoom')) {
        this.workorderToolsList.pop()
      } else {
        let self = this
        let param = { parentId: this.workorder.id, workorderToolsIds: [] }
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
              this.autoResize()
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch()
      }
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
        parentId: this.workorder.id,
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
              parentId: this.workorder.id,
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
            parentId: this.workorder.id,
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
              parentId: this.workorder.id,
              tool: { id: item.id },
              quantity: item.invidualList.length,
              assetIds: assetIds,
            }
            param.workorderToolsList.push(obj)
          }
        } else {
          if (item.addedQuantity > 0) {
            obj = {
              parentId: this.workorder.id,
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
    async loadStockedTools() {
      this.tools = await getFilteredToolListWithParentModule('workorder')
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
          duration = this.getDurationInHour(duration)
        }
        if (issueTime === 0 || issueTime === null) {
          issueTime = -1
        }
        if (returnTime === 0 || returnTime === null) {
          returnTime = -1
        }
        this.workorderToolsList[index].duration = duration
        temp = {
          parentId: this.workorder.id,
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
    getDurationInHour(durationInSeconds) {
      let durationInHour = 0
      if (!isEmpty(durationInSeconds) && durationInSeconds > 0) {
        durationInHour = durationInSeconds / (60 * 60)
        durationInHour = Number(durationInHour).toFixed(2)
      }
      return durationInHour
    },
    loadWorkOrderToolParts(loading, inventory) {
      if (loading) {
        this.toolLoading = true
      }
      let self = this
      this.$http
        .get('/v2/workorderToolsList/parent/' + this.workorder.id)
        .then(response => {
          let wktool = {}
          this.workorderToolsList = response.data.result.workorderTools
          for (let workTool in response.data.result.workorderTools) {
            let durationInSeconds = this.workorderToolsList[workTool].duration
            let durationInHour = this.getDurationInHour(durationInSeconds)
            this.workorderToolsList[workTool].durationInHour = durationInHour
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
          this.autoResize()
        })
        .catch(error => {
          console.log(error)
          this.$message.error(this.$t('common.wo_report.unable_to_load_tools'))
          this.toolLoading = false
        })
      this.loadStockedTools()
    },
    async loadindividualToolTrackingList(items) {
      this.individualToolList = []
      this.iTloading = true

      let filters = {
        rotatingTool: {
          operatorId: 36,
          value: items.map(rt => rt.id + ''),
        },
        isUsed: { operatorId: 15, value: ['false'] },
      }
      let individualToolList = await this.$util.getFilteredAssetList(filters)

      this.individualToolList = individualToolList.map(item => ({
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
.store-room-bar-item {
  position: absolute;
  left: 10px !important;
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
.tool-header-sticky {
  position: sticky;
  left: 0;
  z-index: 200;
  background: #fff;
}
</style>
