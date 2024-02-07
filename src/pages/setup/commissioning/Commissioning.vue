<template>
  <div>
    <div class="full-layout-white height100 user-layout width100">
      <!-- setupheader start -->
      <div class="setting-header">
        <div class="setting-title-block">
          <div class="setting-form-title">Commissioning</div>
          <div class="heading-description">
            List of all the devices that are to be mapped
          </div>
        </div>
      </div>
      <!-- setupBody start -->
      <div class="container-scroll">
        <div class="row setting-Rlayout">
          <div class="col-lg-12 col-md-12">
            <div class="commissioning-tab-container mT20">
              <controller-filter
                :loading.sync="loading"
                @onControllerSelected="onControllerSelected"
              >
                <el-col :span="8" class="flRight commission-tool-right">
                  <el-button
                    v-if="selectedTab === 'mapped' && canSubscribe"
                    :disabled="!selectedInstanceIds.length"
                    type="primary"
                    class="fc-btn-green-medium-fill mT20"
                    @click="onSubscribeClicked"
                    :loading="subscribing"
                    >{{ subscribing ? 'Subscribing...' : 'Subscribe' }}
                  </el-button>
                  <el-button
                    v-else-if="selectedTab === 'subscribed'"
                    :disabled="!selectedInstanceIds.length"
                    type="primary"
                    class="fc-btn-green-medium-fill mT20"
                    @click="unsubscribe"
                    :loading="unsubscribing"
                    >{{ unsubscribing ? 'Unsubscribing...' : 'Unsubscribe' }}
                  </el-button>
                  <el-button
                    v-if="
                      selectedTab === 'mapped' || selectedTab === 'notmapped'
                    "
                    @click="mapInstance()"
                    type="primary"
                    class="fc-btn-green-medium-fill mT20"
                    >SAVE</el-button
                  >
                </el-col>
                <el-col
                  :span="24"
                  class="flRight commission-tool-right"
                  v-if="selectedTab === 'mapped' || selectedTab === 'notmapped'"
                >
                  <div>
                    <el-popover
                      @show="onPopoverShow"
                      placement="right"
                      v-model="poptab"
                      width="300"
                      trigger="click"
                      :popper-class="'commission-popover'"
                      title="Map Asset"
                    >
                      <div class="height250 pL10 pR10">
                        <div class="">
                          <div class="fc-dark-grey-txt14 mB10">Category</div>
                          <el-select
                            v-model="updateBulk.categoryId"
                            @change="onBulkCategoryChange"
                            filterable
                            clearable
                            placeholder="Category"
                            class="fc-input-full-border-select2 width100"
                          >
                            <el-option
                              v-for="(category, idx) in assetCategories"
                              :key="idx"
                              :label="category.displayName"
                              :value="category.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                        <div class="mT20">
                          <div class="fc-dark-grey-txt14 mB10">Asset</div>
                        </div>
                        <el-select
                          v-model="updateBulk.assetId"
                          clearable
                          filterable
                          placeholder="Asset"
                          class="fc-input-full-border-select2 width100"
                        >
                          <el-option
                            v-for="asset in assets[this.updateBulk.categoryId]"
                            :key="asset.id"
                            :label="asset.name"
                            :value="asset.id"
                          >
                          </el-option>
                        </el-select>
                      </div>
                      <div class="modal-dialog-footer">
                        <slot name="footer">
                          <div class="modal-dialog-footer">
                            <el-button
                              class="modal-btn-cancel"
                              type="primary"
                              @click="cancel"
                              >Cancel</el-button
                            >
                            <el-button
                              class="modal-btn-save"
                              type="primary"
                              @click="updateCategoryAndAsset"
                              >{{ 'SAVE' }}
                            </el-button>
                          </div>
                        </slot>
                      </div>
                      <el-button
                        v-if="selectedInstanceIds.length"
                        @click="poptab = true"
                        slot="reference"
                        size="mini"
                        class="fc-btn-pink-medium-fill mL20"
                        >MAP ASSET</el-button
                      >
                    </el-popover>
                  </div>

                  <div>
                    <el-popover
                      @show="onPopoverShow"
                      placement="right"
                      v-model="showBulkReadings"
                      width="300"
                      trigger="click"
                      :popper-class="'commission-popover'"
                      title="Map Readings"
                    >
                      <div class="height250 pL10 pR10">
                        <div class="">
                          <div class="fc-dark-grey-txt14 mB10">Category</div>
                          <el-select
                            v-model="updateBulk.categoryId"
                            @change="onBulkCategoryChange"
                            filterable
                            clearable
                            placeholder="Category"
                            class="fc-input-full-border-select2 width100"
                          >
                            <el-option
                              v-for="(category, idx) in assetCategories"
                              :key="idx"
                              :label="category.displayName"
                              :value="category.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                        <div class="mT20">
                          <div class="fc-dark-grey-txt14 mB10">Readings</div>
                        </div>
                        <el-select
                          v-model="updateBulk.fieldId"
                          filterable
                          placeholder="Select Reading"
                          class="fc-input-full-border-select2 width100"
                        >
                          <el-option
                            v-show="readingMap[updateBulk.categoryId]"
                            v-for="reading in readingMap[
                              updateBulk.categoryId
                            ] || []"
                            :key="reading.fieldId"
                            :label="reading.displayName"
                            :value="reading.fieldId"
                          ></el-option>
                        </el-select>
                      </div>
                      <div class="modal-dialog-footer">
                        <slot name="footer">
                          <div class="modal-dialog-footer">
                            <el-button
                              class="modal-btn-cancel"
                              type="primary"
                              @click="cancel"
                              >Cancel</el-button
                            >
                            <el-button
                              class="modal-btn-save"
                              type="primary"
                              @click="updateCategoryAndReading"
                              >{{ 'SAVE' }}
                            </el-button>
                          </div>
                        </slot>
                      </div>
                      <el-button
                        v-if="selectedInstanceIds.length"
                        @click="showBulkReadings = true"
                        slot="reference"
                        size="mini"
                        class="fc-btn-pink-medium-fill mL10"
                        >MAP READINGS</el-button
                      >
                    </el-popover>
                  </div>
                  <!-- v-if="selectedTab ==='' && canSubscribe" -->
                </el-col>
                <!-- <el-col :span="4" class="flRight">
                   </el-col> -->
              </controller-filter>
              <div
                class="fR commissioning-search-pagination"
                :style="{
                  top:
                    selectedInstanceIds.length &&
                    (selectedTab === 'mapped' || selectedTab === 'notmapped')
                      ? '192px'
                      : '150px',
                }"
              >
                <div class="inline mR20">
                  <div
                    class="row"
                    style="margin-right: 20px"
                    v-if="showQuickSearches"
                  >
                    <div class="fc-list-search">
                      <div
                        class="fc-list-search-wrapper fc-list-search-wrapper-reading relative"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          width="18"
                          height="18"
                          viewBox="0 0 32 32"
                          class="search-icon3"
                        >
                          <title>search</title>
                          <path
                            d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                          ></path>
                        </svg>
                        <input
                          ref="quickSearchQueries"
                          autofocus
                          type="text"
                          v-model="quickSearchQueries"
                          @keyup.enter="quickSearches()"
                          placeholder="Search"
                          class="quick-search-input6"
                        />
                        <svg
                          @click="closeSearches"
                          xmlns="http://www.w3.org/2000/svg"
                          width="18"
                          height="18"
                          viewBox="0 0 32 32"
                          class="close-icon6"
                          aria-hidden="true"
                        >
                          <title>close</title>
                          <path
                            d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                          ></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                  <div
                    class="pointer text-right search-show-hide"
                    @click="toggleQuickSearches"
                    v-show="!showQuickSearches"
                  >
                    <span class="">
                      <i
                        class="fa fa-search"
                        aria-hidden="true"
                        style="font-size: 14px;"
                      ></i>
                    </span>
                  </div>
                </div>
                <pagination
                  :total="listCount"
                  :perPage="perPage"
                  ref="f-page"
                  class="inline"
                ></pagination>
              </div>

              <el-tabs
                v-model="selectedTab"
                class="pT20"
                @tab-click="onTabChange"
              >
                <el-tab-pane
                  v-for="tab in tabs"
                  :key="tab.key"
                  :label="tab.label"
                  :name="tab.key"
                >
                  <v-infinite-scroll
                    :offset="20"
                    style="max-height: 80vh; overflow-y: scroll;"
                  >
                    <table class="setting-list-view-table instance-mapping">
                      <thead v-if="selectedTab === 'subscribed'">
                        <tr>
                          <th style="width:5%; padding: 15px 30px;">
                            <el-checkbox
                              :value="
                                selectedInstanceIds.length &&
                                  selectedInstanceIds.length ===
                                    instances.filter(
                                      instance => instance.subscribeStatus !== 2
                                    ).length
                              "
                              @change="toggleSelectAll"
                            ></el-checkbox>
                          </th>
                          <th
                            class="setting-table-th setting-th-text ellipsis"
                            style="width: 160px;"
                          >
                            INSTANCE NAME
                          </th>
                          <th class="setting-table-th setting-th-text">
                            INSTANCE NUMBER
                          </th>
                          <th class="setting-table-th setting-th-text">
                            INSTANCE TYPE
                          </th>
                          <th class="setting-table-th setting-th-text">
                            ADDED TIME
                          </th>
                          <th
                            v-if="canSubscribe"
                            class="setting-table-th setting-th-text"
                          >
                            SUBSCRIBE STATUS
                          </th>
                        </tr>
                      </thead>
                      <thead v-else>
                        <tr>
                          <th
                            style="width:5%; padding: 15px 30px;"
                            v-if="
                              selectedTab === ('mapped' && canSubscribe) ||
                                'notmapped'
                            "
                          >
                            <el-checkbox
                              :value="
                                selectedInstanceIds.length &&
                                  selectedInstanceIds.length ===
                                    instances.filter(
                                      instance => instance.subscribeStatus !== 2
                                    ).length
                              "
                              @change="toggleSelectAll"
                            ></el-checkbox>
                          </th>
                          <th
                            class="setting-table-th setting-th-text ellipsis"
                            style="width: 160px;"
                          >
                            INSTANCE
                          </th>
                          <th class="setting-table-th setting-th-text">
                            CATEGORY
                          </th>
                          <th class="setting-table-th setting-th-text">
                            ASSET
                          </th>
                          <th class="setting-table-th setting-th-text">
                            READING
                          </th>
                          <th class="setting-table-th setting-th-text">
                            INPUT UNIT
                          </th>
                          <th
                            class="setting-table-th setting-th-text"
                            v-if="selectedTab === 'mapped' && canSubscribe"
                          >
                            SUBSCRIBE STATUS
                          </th>
                        </tr>
                      </thead>
                      <tbody v-if="loading">
                        <tr>
                          <td colspan="100%" class="text-center">
                            <spinner :show="loading" size="80"></spinner>
                          </td>
                        </tr>
                      </tbody>
                      <tbody v-else-if="!instances.length">
                        <tr>
                          <td colspan="100%" class="text-center pT20 pB20">
                            No instances available
                          </td>
                        </tr>
                      </tbody>
                      <tbody v-else-if="selectedTab === 'subscribed'">
                        <tr
                          class="tablerow"
                          v-for="instance in instances"
                          :key="instance.id"
                          v-loading="loading"
                          v-show="
                            !quickSearchQueries ||
                              instance.instance
                                .toLowerCase()
                                .includes(quickSearchQueries.toLowerCase())
                          "
                        >
                          <td class="text-center">
                            <el-checkbox
                              :disabled="instance.subscribeStatus === 2"
                              :value="
                                selectedInstanceIds.indexOf(instance.id) >= 0
                              "
                              @change="toggleSelection(instance.id)"
                            ></el-checkbox>
                          </td>
                          <td>{{ instance.instance }}</td>
                          <td>{{ instance.objectInstanceNumber }}</td>
                          <td>{{ instance.instanceTypeVal | pascalCase }}</td>
                          <td>{{ instance.createdTime | formatDate }}</td>
                          <td v-if="canSubscribe">
                            {{
                              $constants.SUBSCRIBE_STATUS[
                                instance.subscribeStatus
                              ]
                            }}
                          </td>
                        </tr>
                      </tbody>
                      <tbody v-else>
                        <tr
                          class="tablerow"
                          v-for="(instance, idx) in instances"
                          :key="idx"
                        >
                          <td
                            v-if="
                              selectedTab === ('mapped' && canSubscribe) ||
                                'notmapped'
                            "
                            class="text-center"
                          >
                            <el-checkbox
                              :disabled="
                                selectedTab !== 'notmapped' &&
                                  instance.subscribeStatus === 2
                              "
                              :value="
                                selectedInstanceIds.indexOf(instance.id) >= 0
                              "
                              @change="toggleSelection(instance.id)"
                            ></el-checkbox>
                          </td>
                          <td class="expand-content-th">
                            <div class="min-width180px">
                              <div class="label-txt3-14">
                                {{ instance.instance }}
                              </div>
                              <div class="label-txt3-12">
                                {{ instance.device }}
                              </div>
                            </div>
                          </td>
                          <td class="expand-content-th">
                            <div class="width200px">
                              <el-select
                                v-model="instance.categoryId"
                                @change="onCategoryChanged(instance)"
                                filterable
                                placeholder="Select Category"
                                class="fc-input-full-border-select2 width160px"
                              >
                                <el-option
                                  v-for="(category, idx) in assetCategories"
                                  :key="idx"
                                  :label="category.displayName"
                                  :value="category.id"
                                ></el-option>
                              </el-select>
                            </div>
                          </td>
                          <td class="expand-content-th">
                            <div class="width200px">
                              <el-select
                                v-model="instance.resourceId"
                                placeholder="Select Asset"
                                @change="onAssetChanged(instance)"
                                filterable
                                class="fc-input-full-border-select2 width160px"
                              >
                                <el-option
                                  v-for="asset in assets[instance.categoryId]"
                                  :key="asset.id"
                                  :label="asset.name"
                                  :value="asset.id"
                                ></el-option>
                              </el-select>
                            </div>
                          </td>
                          <td class="expand-content-th">
                            <div class="width200px flex-middle">
                              <el-select
                                v-model="instance.fieldId"
                                filterable
                                placeholder="Select Reading"
                                @change="fieldupdate(instance)"
                                class="fc-input-full-border-select2 width160px"
                              >
                                <el-option
                                  v-show="readingMap[instance.categoryId]"
                                  v-for="reading in readingMap[
                                    instance.categoryId
                                  ] || []"
                                  :key="instance.instance + reading.fieldId"
                                  :label="reading.displayName"
                                  :value="reading.fieldId"
                                ></el-option>
                              </el-select>
                              <div
                                v-show="instance.resourceId > 0"
                                class="actions inline pointer mL10"
                                title="Add Reading"
                                @click="
                                  showAddReading(
                                    instance.resourceId,
                                    instance.categoryId
                                  )
                                "
                                v-tippy
                              >
                                <i class="el-icon-plus"></i>
                              </div>
                              <div
                                v-if="
                                  instance.resourceId > 0 &&
                                    instance.fieldId &&
                                    instance.rdmId &&
                                    readingMap[instance.categoryId] &&
                                    readingMap[instance.categoryId][
                                      instance.fieldId
                                    ] &&
                                    [4, 8].includes(
                                      readingMap[instance.categoryId][
                                        instance.fieldId
                                      ].dataType
                                    )
                                "
                                class="actions inline pointer mL10"
                                title="Set Values"
                                @click="showInputReadingDialog(instance)"
                                v-tippy
                              >
                                <i class="el-icon-edit-outline"></i>
                              </div>
                            </div>
                          </td>
                          <td class="expand-content-th">
                            <div class="width200px">
                              <el-select
                                style="width: 130px;"
                                class="fc-input-full-border-select2"
                                v-if="
                                  !instance.fieldId ||
                                    !isMetricAvailable(
                                      instance.categoryId,
                                      instance.fieldId
                                    )
                                "
                                v-model="instance.temp"
                              ></el-select>
                              <el-select
                                v-else
                                @change="
                                  updateUnit(
                                    $event,
                                    instance.resourceId,
                                    instance.fieldId
                                  )
                                "
                                v-model="instance.unit"
                                :placeholder="instance.defaultUnit"
                                filterable
                                style="width: 130px;"
                                class="fc-input-full-border-select2"
                              >
                                <el-option
                                  v-for="(unit, index) in metricsUnits
                                    .metricWithUnits[
                                    readingMap[instance.categoryId][
                                      instance.fieldId
                                    ].metricEnum._name
                                  ]"
                                  :key="index"
                                  :value="unit.unitId"
                                  :label="unit.symbol"
                                  ><span
                                    v-html="
                                      unit.displayName +
                                        (unit.symbol
                                          ? ' (' + unit.symbol + ')'
                                          : '')
                                    "
                                  ></span
                                ></el-option>
                              </el-select>
                            </div>
                          </td>
                          <td class="expand-content-th">
                            <div
                              class="width200px"
                              v-if="selectedTab !== 'notmapped'"
                            >
                              {{
                                $constants.SUBSCRIBE_STATUS[
                                  instance.subscribeStatus
                                ] || 'Not Subscribed'
                              }}
                            </div>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </v-infinite-scroll>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </div>
      </div>
    </div>
    <new-asset-reading-dialog
      v-if="showAddReadingDialog"
      :visibility.sync="showAddReadingDialog"
      @saved="loadReadings(selectedAssetId, true)"
      hideFormula="true"
      :categoryId="selectedCategoryId"
    ></new-asset-reading-dialog>
    <f-dialog
      v-if="showThresholdDialog"
      :visible.sync="showThresholdDialog"
      title="Set Threshold Values"
      width="50%"
      maxHeight="500px"
      @save="subscribe"
      customClass="threshold-value-dialog"
      confirmTitle="Subscribe"
      :stayOnSave="true"
    >
      <div class="pT10 pB10">
        <el-row :gutter="10" class="pB20 threshold-value-item">
          <el-col :span="6">Name</el-col>
          <el-col :span="6">Operator</el-col>
          <el-col :span="6">Threshold</el-col>
          <el-col :span="6">Step Count</el-col>
        </el-row>
        <el-row
          :gutter="10"
          class="pB20 threshold-value-item"
          v-for="instance in selectedInstances.filter(
            inst => ![3, 4, 5].includes(inst.instanceType)
          )"
          :key="instance.id"
        >
          <el-col :span="6">{{ instance.instance }}</el-col>
          <el-col :span="6">
            <el-select
              v-model="instance.operatorValue"
              style="width: 130px;"
              class="fc-input-full-border-select2"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="6" v-if="instance.operatorValue === 10">
            <el-input
              type="number"
              v-model="instance.threshold"
              class="el-input-textbox-full-border"
              :min="5"
            >
              <template slot="append">(%)</template>
            </el-input>
          </el-col>
          <el-col :span="6" v-else>
            <el-input
              type="number"
              v-model="instance.threshold"
              class="el-input-textbox-full-border"
              :min="5"
            >
            </el-input>
          </el-col>
          <el-col :span="6">
            <el-input
              type="number"
              v-model="instance.stepCountVal"
              class="el-input-textbox-full-border"
              :min="5"
            ></el-input>
          </el-col>
        </el-row>
      </div>
    </f-dialog>
    <f-dialog
      v-if="showInputDialog"
      :visible.sync="showInputDialog"
      title="Set Input Values"
      width="30%"
      maxHeight="500px"
      @save="setInputValue"
      customClass="input-value-dialog"
    >
      <div v-if="selectedReading.dataType === 4" class="pT10 pB10">
        <el-row :gutter="10" class="pB20 input-value-item">
          <el-col :span="8">{{ selectedReading.trueVal || 'True' }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="selectedInputValues[1]"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
        <el-row :gutter="10" class="pB20 input-value-item">
          <el-col :span="8">{{ selectedReading.falseVal || 'False' }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="selectedInputValues[0]"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
      </div>
      <div v-else class="pT10 pB10">
        <el-row
          :gutter="10"
          class="pB20 input-value-item"
          v-for="(label, idx) in selectedReading.enumMap"
          :key="idx"
        >
          <el-col :span="8">{{ label }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="selectedInputValues[idx]"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
      </div>
    </f-dialog>
  </div>
</template>
<script>
import NewAssetReadingDialog from 'pages/setup/new/NewAssetReadingDialog'
import VInfiniteScroll from 'v-infinite-scroll'
import Pagination from '@/list/FPagination'
import FDialog from '@/FDialogNew'
import FormMixin from '@/mixins/FormMixin'
import ControllerFilter from 'pages/setup/agents/ControllerFilter'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [FormMixin],
  title() {
    return 'Commissioning'
  },
  data() {
    return {
      updateBulk: {
        categoryId: null,
        assetId: null,
        fieldId: null,
        assetLoading: false,
        readingLoading: false,
      },
      points: [],
      poptab: false,
      showBulkReadings: false,
      listCount: null,
      selectedController: null,
      assetReadingFields: [],
      quickSearchQueries: null,
      showQuickSearches: false,
      selectedInstanceIds: [],
      loading: false,
      instances: [],
      assets: {},
      metricsUnits: {},
      readingMap: {},
      showAddReadingDialog: false,
      selectedTab: 'notmapped',
      subscribing: false,
      unsubscribing: false,
      selectedAssetId: null,
      selectedCategoryId: null,
      tabs: [
        { key: 'notmapped', label: this.$t('setup.controller.notmapped') },
        { key: 'mapped', label: this.$t('setup.controller.mapped') },
        //  {key: 'subscribed', label: 'Subscribed'}
      ],
      showInputDialog: false,
      selectedReading: null,
      selectedInputValues: {},
      selectedRdm: null,
      quickSearchQuery: null,
      showThresholdDialog: false,
      canSubscribe: false,
      options: [
        {
          value: 5,
          label: 'Greater than',
        },
        {
          value: 6,
          label: 'Less than',
        },
        {
          value: 7,
          label: 'Greater than or equals',
        },
        {
          value: 8,
          label: 'Less than or equals',
        },
        {
          value: 9,
          label: 'Equals',
        },
        {
          value: 10,
          label: 'Precentage',
        },
      ],
      value: -1,
    }
  },
  components: {
    NewAssetReadingDialog,
    VInfiniteScroll,
    Pagination,
    FDialog,
    ControllerFilter,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    assetCategories() {
      return this.$store.state.assetCategory
    },
    page() {
      return this.$route.query.page || 1
    },
    perPage() {
      return this.selectedTab === 'notmapped' ? 100 : 25
    },
    selectedInstances() {
      if (this.instances && this.selectedInstanceIds) {
        return this.instances.filter(inst =>
          this.selectedInstanceIds.includes(inst.id)
        )
      }
      return []
    },
  },

  mounted() {
    this.loadDefaultMetricUnits()
  },
  watch: {
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadInstances(true)
      }
    },
  },
  methods: {
    cancel() {
      this.showBulkReadings = false
      this.poptab = false
    },
    onPopoverShow() {
      let categoryId = this.selectedInstances[0].categoryId
      let isAllCategorySame =
        this.selectedInstances.reduce((a, b) => (a === b ? a : b))
          .categoryId === categoryId
      this.updateBulk.categoryId = isAllCategorySame ? categoryId : null
      this.updateBulk.assetId = null
      this.updateBulk.fieldId = null
    },
    fieldupdate(instance) {
      this.$set(instance, 'inputValues', null)
      this.$set(instance, 'rdmId', null)
      this.addPoint(instance)
      let field = this.readingMap[instance.categoryId][instance.fieldId]
      if (field && field.metricEnum) {
        this.setDefaultUnit(field.metricEnum._name, instance)
      }
    },
    updateCategoryAndAsset() {
      if (this.updateBulk.assetId) {
        for (let instance of this.selectedInstances) {
          this.$set(instance, 'resourceId', this.updateBulk.assetId)
          if (
            instance.categoryId &&
            instance.categoryId !== this.updateBulk.categoryId
          ) {
            this.$set(instance, 'fieldId', null)
          }
          this.$set(instance, 'categoryId', this.updateBulk.categoryId)
          this.addPoint(instance)
        }
      }
      this.poptab = false
      this.selectedInstanceIds = []
    },
    updateCategoryAndReading() {
      if (this.updateBulk.fieldId) {
        for (let instance of this.selectedInstances) {
          this.$set(instance, 'fieldId', this.updateBulk.fieldId)
          if (
            instance.categoryId &&
            instance.categoryId !== this.updateBulk.categoryId
          ) {
            this.$set(instance, 'resourceId', null)
          }
          this.$set(instance, 'categoryId', this.updateBulk.categoryId)
          this.fieldupdate(instance)
        }
      }
      this.showBulkReadings = false
      this.selectedInstanceIds = []
    },
    quickSearches() {
      let string = this.quickSearchQueries
      if (string) {
        this.loadInstances()
      }
    },
    toggleQuickSearches() {
      this.showQuickSearches = !this.showQuickSearches
      if (this.showQuickSearches) {
        this.$nextTick(() => {
          this.$refs.quickSearchQueries.focus()
        })
      }
    },
    closeSearches() {
      this.toggleQuickSearches()
      this.quickSearchQueries = null
      this.loadInstances()
      this.quickSearches()
    },
    toggleSelectAll(val) {
      this.selectedInstanceIds = val
        ? this.instances
            .filter(instance => instance.subscribeStatus !== 2)
            .map(instance => instance.id)
        : []
    },
    toggleSelection(instance) {
      let idx = this.selectedInstanceIds.indexOf(instance)
      if (idx === -1) {
        this.selectedInstanceIds.push(instance)
      } else {
        this.selectedInstanceIds.splice(idx, 1)
      }
    },
    onSubscribeClicked() {
      let booleanInstances = this.selectedInstances.filter(inst =>
        [3, 4, 5].includes(inst.instanceType)
      )
      if (booleanInstances.length === this.selectedInstances.length) {
        this.subscribe()
      } else {
        this.showThresholdDialog = true
      }
    },
    initSubscribe() {
      if (
        this.$common.isBacnetController(this.selectedController) ||
        this.selectedController.controllerType === 3
      ) {
        let isSub = this.tabs.filter(d => d.key === 'subscribed')
        if (!isSub.length > 0) {
          this.tabs.push({ key: 'subscribed', label: 'Subscribed' })
        }
        this.canSubscribe = this.$helpers.isLicenseEnabled('CONTROL_ACTIONS')
      } else {
        this.tabs.splice(2, 1)
        this.canSubscribe = false
      }
    },
    loadInstancesCount() {
      if (isEmpty(this.selectedController)) return

      let url = '/v2/instances/count'
      this.loadData(url, true).then(response => {
        if (response.data.result.instances) {
          this.listCount = response.data.result.instances[0].count
        }
      })
    },
    loadData(url, count) {
      if (this.selectedTab === 'mapped') {
        url += '?configured=true&fetchMapped=true'
      } else if (this.selectedTab === 'subscribed') {
        url += '?subscribed=true&fetchMapped=true'
      } else {
        url += '?configured=true&fetchMapped=false'
      }
      if (this.quickSearchQueries) {
        url += '&search=' + this.quickSearchQueries
      }
      if (!count) {
        url += '&perPage=' + this.perPage + '&page=' + this.page
      } else {
        url += '&controllerId=' + this.selectedController.id
      }
      return this.$http.get(url)
    },
    loadInstances(fetchMore) {
      this.instances = []
      if (!fetchMore || (this.quickSearchQueries && this.page === 1)) {
        this.listCount = 0
        this.$refs['f-page'].reset()
        this.loadInstancesCount()
      }
      this.loading = true
      this.loadData('/v2/instances/' + this.selectedController.id)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.selectedInstanceIds = []
            if (response.data.result.instances) {
              this.instances = response.data.result.instances || []
              this.instances.forEach(instance => {
                let reading
                if (response.data.result.readings) {
                  reading = response.data.result.readings.find(
                    reading =>
                      instance.resourceId === reading.resourceId &&
                      instance.fieldId === reading.fieldId
                  )
                }
                if (instance.categoryId) {
                  this.loadAssets(instance.categoryId)
                  this.loadReadings(instance.categoryId)
                  if (this.selectedTab === 'mapped' && reading.field) {
                    this.setDefaultUnit(reading.field.metricEnum, instance)
                  }
                }
                this.setRDMValues(reading, instance)
              })
            }
          }
          this.loading = false
        })
        .catch(_ => {
          this.loading = false
        })
    },
    setRDMValues(reading, instance) {
      if (!reading) {
        return
      }
      if (reading.unit > 0) {
        this.$set(instance, 'unit', reading.unit)
      }
      if (reading.inputValues) {
        this.$set(instance, 'inputValues', reading.inputValues)
      }
      this.$set(instance, 'rdmId', reading.id)
    },
    setDefaultUnit(fieldMetric, instance) {
      if (!fieldMetric) {
        return
      }
      let orgUnit = this.metricsUnits.orgUnitsList.find(
        d => fieldMetric === d.metricEnum._name
      )
      if (orgUnit && orgUnit.unitEnum) {
        instance.defaultUnit = orgUnit.unitEnum.symbol
      }
    },
    subscribe() {
      if (!this.validateThreshold()) {
        return
      }
      this.showThresholdDialog = false
      this.subscribing = true
      let instances = this.selectedInstances.map(instance => {
        let inst = {
          id: instance.id,
        }
        if (instance.threshold) {
          inst.thresholdJson = JSON.stringify({
            operator: instance.operatorValue,
            threshold: instance.threshold,
            stepCount: instance.stepCountVal,
          })
        }
        return inst
      })
      this.$http
        .post('/v2/instances/subscribe', {
          instances: instances,
          controllerId: this.selectedController.id,
        })
        .then(response => {
          this.subscribing = false
          if (response.data.responseCode === 0) {
            this.$message.success('Points will be subscribed in a while')
            this.toggleSelectAll(false)
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(_ => {
          this.subscribing = false
        })
    },
    unsubscribe() {
      this.$dialog
        .confirm({
          title: 'Unsubscribe Instance',
          htmlMessage:
            'Are you sure you want to unsubscribe selected instance(s)?',
          rbDanger: true,
          rbLabel: 'Proceed',
        })
        .then(value => {
          if (value) {
            this.unsubscribing = true
            this.$http
              .post('/v2/instances/unsubscribe', {
                ids: this.selectedInstanceIds,
                controllerId: this.selectedController.id,
              })
              .then(response => {
                this.unsubscribing = false
                if (response.data.responseCode === 0) {
                  this.$message.success(
                    'Points will be unsubscribed in a while'
                  )
                  this.instances = this.instances.filter(
                    instance => !this.selectedInstanceIds.includes(instance.id)
                  )
                  this.toggleSelectAll(false)
                } else {
                  this.$message.error(response.data.message)
                }
              })
              .catch(_ => {
                this.unsubscribing = false
              })
          }
        })
    },
    validateThreshold() {
      let lessThreshold = this.selectedInstances.some(
        inst =>
          ![3, 4, 5].includes(inst.instanceType) &&
          inst.operatorValue === 10 &&
          (!inst.threshold || inst.threshold < 5)
      )
      if (lessThreshold) {
        this.$message.error(
          'The difference should be greater than or equal to 5 percentage'
        )
        return false
      }
      return true
    },
    onBulkCategoryChange(categoryId) {
      this.loadAssets(categoryId)
      this.loadReadings(categoryId)
    },
    onCategoryChanged(instance) {
      this.$set(instance, 'resourceId', null)
      this.$set(instance, 'fieldId', null)
      this.$set(instance, 'unit', null)
      this.$set(instance, 'inputValues', null)
      this.$set(instance, 'rdmId', null)
      this.loadAssets(instance.categoryId)
      this.loadReadings(instance.categoryId)
      this.addPoint(instance)
    },
    onAssetChanged(instance) {
      this.addPoint(instance)
    },
    addPoint(instance) {
      if (!this.points.find(point => point.id === instance.id)) {
        this.points.push(instance)
      }
    },
    loadAssets(categoryId) {
      if (this.assets[categoryId]) {
        return this.assets[categoryId]
      }
      this.$set(this.assets, categoryId, [])
      return this.$util
        .loadAsset({ categoryId: categoryId, selectFields: ['name'] })
        .then(response => {
          this.$set(
            this.assets,
            categoryId,
            (response.assets || []).map(asset => ({
              name: asset.name,
              id: asset.id,
            }))
          )
        })
    },
    getAsset(assetId) {
      let asset
      for (let categoryId in this.assets) {
        if (!asset) {
          asset = this.assets[categoryId].find(casset => casset.id === assetId)
        }
      }
      return asset
    },
    loadReadings(categoryId, fetchNew) {
      if (!fetchNew && this.readingMap[categoryId]) {
        return Promise.resolve()
      }
      this.$set(this.readingMap, categoryId, {})
      return this.$util
        .loadAssetReadingFields(null, categoryId, false)
        .then(readings => {
          this.setReadingMap(readings, categoryId)
        })
    },
    setReadingMap(readings, categoryId) {
      if (readings) {
        readings.forEach(reading => {
          if (!this.readingMap[categoryId]) {
            this.$set(this.readingMap, categoryId, {})
          }
          this.$set(this.readingMap[categoryId], reading.fieldId, reading)
        })
      }
    },
    mapInstance() {
      this.loading = true
      let points = this.points
      //if (this.selectedTab !== 'notmapped') {
      points = this.points.filter(point => point.resourceId && point.fieldId)
      //}
      this.$http
        .post('/v2/instances/map', {
          controllerId: this.selectedController.id,
          instances: points,
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.loading = false
            this.$message.error(response.data.message)
          } else {
            this.$message.success('Readings mapped Successfully')
            this.points = []
            this.loadInstances()
          }
        })
    },
    loadDefaultMetricUnits() {
      this.$http.get('/units/getDefaultMetricUnits').then(response => {
        this.metricsUnits = response.data || {}
      })
    },

    isMetricAvailable(categoryId, fieldId) {
      if (this.readingMap[categoryId]) {
        let reading = this.readingMap[categoryId][fieldId]
        return reading && reading.metricEnum
      }
    },

    updateUnit(unit, resourceId, fieldId) {
      this.$http
        .post('/v2/reading/update', {
          readingDataMeta: {
            resourceId: resourceId,
            fieldId: fieldId,
            unit: unit,
          },
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          }
        })
    },

    showAddReading(resourceId, categoryId) {
      this.selectedAssetId = resourceId
      this.selectedCategoryId = categoryId
      this.showAddReadingDialog = true
    },

    showInputReadingDialog(instance) {
      let reading = this.readingMap[instance.categoryId][instance.fieldId]
      this.showInputDialog = true
      this.selectedReading = reading
      this.selectedInputValues = {}
      this.selectedRdm = instance.rdmId
      if (instance.inputValues) {
        this.selectedInputValues = this.$helpers.cloneObject(
          instance.inputValues
        )
      } else {
        if (reading.dataType === 4) {
          this.$set(this.selectedInputValues, [0], '0')
          this.$set(this.selectedInputValues, [1], '1')
        } else if (reading.dataType === 8) {
          for (let idx in reading.enumMap) {
            this.$set(this.selectedInputValues, idx, idx + '')
          }
        }
      }
    },

    setInputValue() {
      let values = []
      for (let idx in this.selectedInputValues) {
        if (
          this.selectedInputValues[idx] ||
          this.selectedInputValues[idx] === 0
        ) {
          values.push({
            idx,
            inputValue: this.selectedInputValues[idx],
          })
        }
      }
      this.$http
        .post('/v2/reading/setinputvalue', {
          readingId: this.selectedRdm,
          inputValues: values,
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            this.$message.success('Values set successfully')
            let instance = this.getInstanceFromReading(this.selectedReading)
            this.$set(instance, 'inputValues', this.inputValues)
          }
        })
    },
    // Getting instance based on rdm value
    getInstanceFromReading(reading) {
      return this.instances.find(
        inst =>
          inst.resourceId === reading.resourceId &&
          inst.fieldId === reading.fieldId
      )
    },
    onControllerSelected(controller) {
      this.selectedController = controller
      this.initSubscribe()
      this.loadInstances()
      this.points = []
    },
    onTabChange() {
      this.points = []
      this.loadInstances()
    },
  },
}
</script>
<style>
.commissioning-tab-container .el-tabs__item {
  padding: 0 22px;
  height: 40px;
  box-sizing: border-box;
  line-height: 40px;
  display: inline-block;
  list-style: none;
  position: relative;
  font-size: 12px;
  letter-spacing: 0.6px;
  font-weight: 500;
  color: #333333;
  text-transform: uppercase;
}

.commissioning-tab-container .el-tabs__item.is-active {
  font-weight: 600;
  letter-spacing: 0.6px;
  /* letter-spacing: 0.6px;
  font-size: 12px; */
}

.commissioning-tab-container .el-tabs__active-bar {
  background: #ef4f8f;
  width: 51px !important;
}

.commissioning-tab-container .el-tabs__nav-wrap::after {
  height: 1px;
  background-color: #f0f0f0;
}
.commissioing-collapse-block .el-input .el-input__inner,
.el-textarea .el-textarea__inner,
.units-td-row .el-input .el-input__inner,
.el-textarea .el-textarea__inner {
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #e2e8ee;
}
.commissioing-collapse-block .el-collapse-item__header {
  background-color: #f0f4f4;
}
.expand-txt {
  font-size: 13px;
  letter-spacing: 0.7px;
  color: #2f7eca;
  text-align: right;
}
.expand-bar-container .el-collapse-item__header {
  height: 66px !important;
  line-height: 66px !important;
}

.expand-bar-container .el-collapse-item__header.is-active {
  background-color: #f8fafa !important;
}

.expand-bar-container .el-collapse-item__wrap {
  border: 0;
}

.expand-bar-container .el-collapse-item__header .el-collapse-item__arrow {
  display: none;
  margin-top: 8px;
}

.uncommissioned-tab-section .el-checkbox__inner {
  width: 16px;
  height: 16px;
  border: 1px solid#b4c8c8;
}
.expand-content-th .el-input .el-input__inner,
.el-textarea .el-textarea__inner,
.units-td-row .el-input .el-input__inner,
.el-textarea .el-textarea__inner {
  height: 35px !important;
  border: 1px solid #e2e8ee;
  padding-left: 10px;
  border-radius: 3px;
  padding-left: 10px;
  font-size: 14px;
  letter-spacing: 0.5px;
  /* color: #999999; */
  background: #fff;
}
.expand-content-th .el-select .el-input .el-select__caret {
  color: #8da2b7;
  font-size: 12px;
}
.commissioning-list-h {
  font-size: 14px;
  line-height: 3.57;
  letter-spacing: 1.2px;
  color: #333333;
  font-weight: normal;
  padding-top: 8px;
  padding-bottom: 8px;
}
.commissioning-list-h .el-icon-arrow-down {
  color: #8da2b7;
  padding-left: 10px;
}
.units-tr-row {
  border-bottom: 1px solid #f0f0f0;
}
.units-td-row {
  font-size: 14px;
  letter-spacing: 0.4px;
  color: #333333;
  padding-top: 9px;
  padding-bottom: 9px;
}
.expand-bar-container .el-collapse-item__header .category-blue-txt {
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #415e7b;
}
/* commissioning list */
.search-result-txt {
  font-size: 16px;
  letter-spacing: 0.7px;
  color: #2197a7;
}
.commissioning-tab-container
  .el-collapse-item.expand-bar-container:nth-child(odd)
  .el-collapse-item__header {
  background-color: #fbfbfb;
}
.commissioning-tab-container
  .el-collapse-item.expand-bar-container:nth-child(even)
  .el-collapse-item__header {
  background-color: #ffffff;
}
.commissioning-tab-container .el-input.is-disabled .el-input__inner {
  color: #333333;
}
.commissioning-list-table .el-collapse {
  border-top: none;
  border-bottom: none;
}

.setting-list-view-table.instance-mapping td,
.setting-list-view-table.instance-mapping th {
  padding-left: 15px;
  padding-right: 15px;
}

.commissioning-tab-container .el-icon-plus,
.commissioning-tab-container .el-icon-edit-outline {
  font-size: 16px;
}
.show-instance-txt {
  position: relative;
  top: 50px;
}
.commissioning-list-table .units-tr-row:hover .actions {
  visibility: visible;
}

.commissioning-list-table .units-tr-row .actions {
  visibility: hidden;
}

.input-value-dialog .input-value-item {
  display: flex;
  align-items: center;
}
.commission-tool-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 15px;
}
.commission-tool-right .fc-black-small-txt-12 {
  padding-top: 10px;
}
.commission-popover .el-popover__title {
  padding: 10px;
}
.commissioning-search-pagination {
  position: absolute;
  right: 0px;
  z-index: 123;
}

.commissioning-search-pagination .search-show-hide .fa-search {
  position: relative;
  top: 0px;
  right: 0px;
}
</style>
