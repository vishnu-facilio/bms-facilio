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
                    >SAVE ALL</el-button
                  >
                  <div>
                    <el-popover
                      @show="cleardata"
                      placement="right"
                      v-model="poptab"
                      width="300"
                      trigger="click"
                      :popper-class="'commission-popover'"
                      title="Update"
                    >
                      <div class="height250 pL10 pR10">
                        <div class="">
                          <div class="fc-dark-grey-txt14 mB10">Category</div>
                          <el-select
                            v-model="updateBulk.categoryvalue"
                            filterable
                            @change="updateAsset()"
                            clearable
                            placeholder="Category"
                            class="fc-input-full-border-select2 width100"
                          >
                            <el-option
                              v-for="(category, idx) in assetCategories"
                              :key="idx"
                              :label="category.name"
                              :value="category.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                        <div class="mT20">
                          <div class="fc-dark-grey-txt14 mB10">Asset</div>
                        </div>
                        <el-select
                          v-model="updateBulk.assetvalue"
                          clearable
                          filterable
                          placeholder="Asset"
                          class="fc-input-full-border-select2 width100"
                        >
                          <el-option
                            v-for="asset in assets[
                              this.updateBulk.categoryvalue
                            ]"
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
                              @click="
                                poptab = false
                                cancel()
                              "
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
                        class="fc-btn-pink-medium-fill mT20 mL20"
                        >MAP ASSET</el-button
                      >
                    </el-popover>
                  </div>

                  <div
                    v-if="$org.id === 154 || $org.id === 133"
                    class="fc-dark-blue-txt pointer show-instance-txt flRight "
                    @click="showByDevice = !showByDevice"
                  >
                    {{ showByDevice ? 'Show By Instance' : 'Show By Device' }}
                  </div>
                  <!-- v-if="selectedTab ==='' && canSubscribe" -->
                </el-col>
                <!-- <el-col :span="4" class="flRight">
                   </el-col> -->
              </controller-filter>
              <div class="fR commissioning-search-con">
                <pagination
                  :total="listCount"
                  :perPage="perPage"
                  ref="f-page"
                  class="commission-pagenation-con"
                ></pagination>
                <div class="commissioning-search-block">
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
              </div>

              <el-tabs
                v-model="selectedTab"
                class="pT20"
                @tab-click="
                  listCount = null
                  totalCount = null
                  loadInstances()
                "
              >
                <el-tab-pane
                  v-for="tab in tabs"
                  :key="tab.key"
                  :label="tab.label"
                  :name="tab.key"
                >
                  <v-infinite-scroll
                    v-if="!showByDevice"
                    :loading="fetchingMore"
                    @bottom="loadMore"
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
                            v-if="'mapped' && canSubscribe"
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
                                  :label="category.name"
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
                                @change="fieldupdate(null, instance, null)"
                                class="fc-input-full-border-select2 width160px"
                              >
                                <el-option
                                  v-show="readingMap[instance.resourceId]"
                                  v-for="reading in readingMap[
                                    instance.resourceId
                                  ] || []"
                                  :key="instance.instance + reading.fieldId"
                                  :label="reading.field.displayName"
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
                                    readingMap[instance.resourceId] &&
                                    readingMap[instance.resourceId][
                                      instance.fieldId
                                    ] &&
                                    [4, 8].includes(
                                      readingMap[instance.resourceId][
                                        instance.fieldId
                                      ].field.dataType
                                    )
                                "
                                class="actions inline pointer mL10"
                                title="Set Values"
                                @click="
                                  showInputReadingDialog(
                                    readingMap[instance.resourceId][
                                      instance.fieldId
                                    ]
                                  )
                                "
                                v-tippy
                              >
                                <i class="el-icon-edit-outline"></i>
                              </div>
                            </div>
                          </td>
                          <td class="expand-content-th">
                            <div class="width200px">
                              <el-select
                                v-if="
                                  !instance.fieldId ||
                                    !isMetricAvailable(
                                      instance.resourceId,
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
                                  ),
                                    fieldupdate($event, instance, null)
                                "
                                v-model="
                                  readingMap[instance.resourceId][
                                    instance.fieldId
                                  ].inputUnit
                                "
                                :placeholder="instance.defaultUnit"
                                filterable
                                style="width: 130px;"
                                class="fc-input-full-border-select2"
                              >
                                <el-option
                                  v-for="(unit, index) in metricsUnits
                                    .metricWithUnits[
                                    readingMap[instance.resourceId][
                                      instance.fieldId
                                    ].field.metricEnum
                                  ]"
                                  :key="index"
                                  :value="unit.unitId"
                                  :label="unit.displayName"
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
                        <tr v-if="fetchingMore">
                          <td colspan="100%" class="text-center">
                            <spinner :show="fetchingMore" size="50"></spinner>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </v-infinite-scroll>
                  <template v-else>
                    <table class="setting-list-view-table">
                      <thead>
                        <tr>
                          <th style="width:5%; padding: 15px 20px;">
                            <el-checkbox
                              :value="
                                selectedInstanceIds.length &&
                                  selectedInstanceIds.length ===
                                    instances.length
                              "
                              @change="toggleSelectAll"
                            ></el-checkbox>
                          </th>
                          <th
                            class="setting-table-th setting-th-text"
                            style="width: 58%;padding-left: 20px !important;"
                          >
                            DEVICE
                          </th>
                          <th
                            class="setting-table-th setting-th-text"
                            style="width: 160px;padding-left: 5px !important;"
                          >
                            CATEGORY
                          </th>
                          <th
                            class="setting-table-th setting-th-text"
                            style="width: 160px;padding-left: 5px !important;"
                          >
                            ASSET
                          </th>
                        </tr>
                      </thead>
                    </table>
                    <v-infinite-scroll
                      :loading="fetchingMore"
                      @bottom="loadMore"
                      :offset="20"
                      style="max-height: 80vh; overflow-y: scroll;"
                    >
                      <table class="commissioning-list-table">
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
                        <tbody
                          v-else
                          is="el-collapse"
                          v-model="selectedDevice"
                          accordion
                        >
                          <tr
                            v-for="(info, device, index) in deviceInstanceMap"
                            :key="index"
                            class="expand-bar-container"
                            is="el-collapse-item"
                            :name="device"
                          >
                            <template slot="title">
                              <div
                                style="width: 50%;padding-left: 20px;"
                                class="commissioning-list-h inline"
                              >
                                <el-checkbox
                                  :value="
                                    selectedInstanceIds.indexOf(
                                      info.instances[0].id
                                    ) >= 0
                                  "
                                  @change="
                                    toggleSelection(info.instances[0].id)
                                  "
                                ></el-checkbox>
                                <span style="padding-left: 20px;">{{
                                  info.device
                                }}</span>
                              </div>
                              <div
                                class="expand-content-th inline"
                                style="width: 213px;"
                              >
                                <el-select
                                  v-model="info.categoryId"
                                  @change="onCategoryChanged(null, info)"
                                  filterable
                                  placeholder="Select Category"
                                  style="width: 160px;"
                                  class="fc-input-full-border-select2"
                                >
                                  <el-option
                                    v-for="(category, idx) in assetCategories"
                                    :key="idx"
                                    :label="category.name"
                                    :value="category.id"
                                  ></el-option>
                                </el-select>
                              </div>
                              <div
                                class="expand-content-th inline"
                                style="width: 160px;"
                              >
                                <el-select
                                  v-model="info.resourceId"
                                  placeholder="Select Asset"
                                  @change="onAssetChanged(null, info)"
                                  filterable
                                  style="width: 160px;"
                                  class="fc-input-full-border-select2"
                                >
                                  <el-option
                                    v-for="asset in assets[info.categoryId]"
                                    :key="asset.id"
                                    :label="asset.name"
                                    :value="asset.id"
                                  ></el-option>
                                </el-select>
                              </div>
                            </template>
                            <div
                              class="units-tr-row"
                              v-for="(instance, idx) in info.instances"
                              :key="idx"
                            >
                              <div
                                style="width: 20%;"
                                class="units-td-row inline"
                              ></div>
                              <div class="units-td-row inline">
                                {{ instance.instance }}
                              </div>
                              <div class="units-td-row inline pR20">
                                <el-select
                                  v-model="instance.fieldId"
                                  @change="fieldupdate(null, instance, info)"
                                  filterable
                                  style="width: 160px;"
                                  class="fc-input-full-border-select2 mL20"
                                >
                                  <el-option
                                    v-for="reading in readingMap[
                                      info.resourceId
                                    ] || []"
                                    :key="instance.instance + reading.fieldId"
                                    :label="reading.field.displayName"
                                    :value="reading.fieldId"
                                  ></el-option>
                                </el-select>
                                <div
                                  v-if="info.resourceId > 0"
                                  class="inline pointer actions"
                                  @click="
                                    showAddReading(
                                      info.resourceId,
                                      info.categoryId
                                    )
                                  "
                                >
                                  <i
                                    class="el-icon-plus"
                                    title="Add Reading"
                                    v-tippy
                                  ></i>
                                </div>
                                <div
                                  v-if="
                                    info.resourceId > 0 &&
                                      instance.fieldId &&
                                      [4, 8].includes(
                                        readingMap[info.resourceId][
                                          instance.fieldId
                                        ].field.dataType
                                      )
                                  "
                                  class="actions inline pointer mL10"
                                  title="Set Values"
                                  @click="
                                    showInputReadingDialog(
                                      readingMap[info.resourceId][
                                        instance.fieldId
                                      ]
                                    )
                                  "
                                  v-tippy
                                >
                                  <i class="el-icon-edit-outline"></i>
                                </div>
                              </div>
                              <div class="units-td-row inline">
                                <el-select
                                  v-if="
                                    instance.fieldId &&
                                      isMetricAvailable(
                                        info.resourceId,
                                        instance.fieldId
                                      )
                                  "
                                  @change="
                                    updateUnit(
                                      $event,
                                      info.resourceId,
                                      instance.fieldId
                                    ),
                                      fieldupdate($event, instance, info)
                                  "
                                  :placeholder="instance.defaultUnit"
                                  v-model="
                                    readingMap[info.resourceId][
                                      instance.fieldId
                                    ].inputUnit
                                  "
                                  filterable
                                  style="width: 160px;"
                                  class="fc-input-full-border-select2"
                                >
                                  <el-option
                                    v-for="(unit, index) in metricsUnits
                                      .metricWithUnits[
                                      readingMap[info.resourceId][
                                        instance.fieldId
                                      ].field.metricEnum
                                    ]"
                                    :key="index"
                                    :value="unit.unitId"
                                    :label="unit.displayName"
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
                            </div>
                          </tr>
                        </tbody>
                        <!-- collapse menu content-->
                        <!-- collapse menu content-->
                      </table>
                    </v-infinite-scroll>
                  </template>
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
      width="30%"
      maxHeight="500px"
      @save="subscribe"
      customClass="threshold-value-dialog"
      confirmTitle="Subscribe"
      :stayOnSave="true"
    >
      <div class="pT10 pB10">
        <el-row :gutter="10" class="pB20 threshold-value-item">
          <el-col :span="8">Name</el-col>
          <el-col :span="10">Threshold (%)</el-col>
        </el-row>
        <el-row
          :gutter="10"
          class="pB20 threshold-value-item"
          v-for="instance in selectedInstances.filter(
            inst => ![3, 4, 5].includes(inst.instanceType)
          )"
          :key="instance.id"
        >
          <el-col :span="8">{{ instance.instance }}</el-col>
          <el-col :span="10">
            <el-input
              type="number"
              v-model="instance.threshold"
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
      <div v-if="selectedReading.field.dataType === 4" class="pT10 pB10">
        <el-row :gutter="10" class="pB20 input-value-item">
          <el-col :span="8">{{
            selectedReading.field.trueVal || 'True'
          }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="inputValues[1]"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
        <el-row :gutter="10" class="pB20 input-value-item">
          <el-col :span="8">{{
            selectedReading.field.falseVal || 'False'
          }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="inputValues[0]"
              class="el-input-textbox-full-border"
            ></el-input
          ></el-col>
        </el-row>
      </div>
      <div v-else class="pT10 pB10">
        <el-row
          :gutter="10"
          class="pB20 input-value-item"
          v-for="(label, idx) in selectedReading.field.enumMap"
          :key="idx"
        >
          <el-col :span="8">{{ label }}</el-col>
          <el-col :span="10"
            ><el-input
              v-model="inputValues[idx]"
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
        categoryvalue: null,
        assetvalue: null,
      },
      points: [],
      poptab: false,
      listCount: null,
      totalCount: null,
      selectedController: null,
      assetReadingFields: [],
      quickSearchQueries: null,
      showQuickSearches: false,
      controllers: [],
      selectedInstanceIds: [],
      selectedInstances: [],
      loading: false,
      instances: [],
      deviceInstanceMap: {},
      selectedDevice: null,
      assets: {},
      metricsUnits: {},
      readingMap: {},
      showAddReadingDialog: false,
      selectedTab: 'notmapped',
      showByDevice: false,
      subscribing: false,
      unsubscribing: false,
      selectedAssetId: null,
      selectedCategoryId: null,
      tabs: [
        { key: 'notmapped', label: this.$t('setup.controller.notmapped') },
        { key: 'mapped', label: this.$t('setup.controller.mapped') },
        //  {key: 'subscribed', label: 'Subscribed'}
      ],
      canFetchMore: false,
      fetchingMore: false,
      showInputDialog: false,
      selectedReading: null,
      inputValues: {},
      quickSearchQuery: null,
      showThresholdDialog: false,
      canSubscribe: false,
      defaultUnit: null,
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
      return this.selectedTab === 'mapped' ? 25 : 50
    },
  },

  mounted() {
    this.loadDefaultMetricUnits()
  },
  watch: {
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loading = true
        this.loadInstances()
        //  this.loadInstancesCount()
      }
    },
    selectedTab: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadInstancesCount(true)
      }
    },
  },
  methods: {
    cancel() {
      this.selectedInstanceIds = []
    },
    cleardata() {
      this.updateBulk.categoryvalue = null
      this.updateBulk.assetvalue = null
    },
    fieldupdate(unit, instance, info) {
      instance.unit = unit
      let point = this.points.find(
        point => point.instance === instance.instance
      )
      if (!point) {
        point = {}
        this.points.push(point)
      }
      if (info === null) {
        ;(point.device = instance.device),
          (point.instance = instance.instance),
          (point.categoryId = instance.categoryId),
          (point.resourceId = instance.resourceId),
          (point.fieldId = instance.fieldId),
          (point.unit = instance.unit)

        let metric = this.metricsUnits.orgUnitsList.filter(d => {
          if (
            this.readingMap[instance.resourceId][instance.fieldId].field
              .metricEnum === d.metricEnum._name
          ) {
            return d
          }
        })

        for (let i in this.metricsUnits.metricWithUnits) {
          if (i === metric[0].metricEnum._name) {
            for (let j in this.metricsUnits.metricWithUnits[i]) {
              if (
                this.metricsUnits.metricWithUnits[i][j].unitId ===
                metric[0].unit
              ) {
                instance.defaultUnit = this.metricsUnits.metricWithUnits[i][
                  j
                ].symbol
              }
            }
          }
        }
      } else {
        point.device = info.device
        point.instance = instance.instance
        point.resourceId = info.resourceId
        point.categoryId = info.categoryId
        point.fieldId = instance.fieldId
        point.unit = instance.unit

        let metric = this.metricsUnits.orgUnitsList.filter(d => {
          if (
            this.readingMap[info.resourceId][instance.fieldId].field
              .metricEnum === d.metricEnum._name
          ) {
            return d
          }
        })

        for (let i in this.metricsUnits.metricWithUnits) {
          if (i === metric[0].metricEnum._name) {
            for (let j in this.metricsUnits.metricWithUnits[i]) {
              if (
                this.metricsUnits.metricWithUnits[i][j].unitId ===
                metric[0].unit
              ) {
                info.instances[0].defaultUnit = this.metricsUnits.metricWithUnits[
                  i
                ][j].symbol
              }
            }
          }
        }
      }
    },
    updateAsset() {
      this.updateBulk.assetvalue = null
      if (!this.showByDevice) {
        for (let instance of this.instances) {
          if (
            this.selectedInstanceIds.find(function(selectedinstance) {
              return selectedinstance === instance.id
            })
          ) {
            instance.categoryId = this.updateBulk.categoryvalue
            if (instance.categoryId) {
              this.onCategoryChanged(instance)
            }
            this.$forceUpdate()
          }
        }
      } else {
        for (let instance in this.deviceInstanceMap) {
          if (
            this.selectedInstanceIds.find(s => {
              return s === this.deviceInstanceMap[instance].instances[0].id
            })
          ) {
            this.deviceInstanceMap[
              instance
            ].categoryId = this.updateBulk.categoryvalue
            if (this.deviceInstanceMap[instance].categoryId) {
              this.onCategoryChanged(null, this.deviceInstanceMap[instance])
            }
          }
        }
      }
    },
    updateCategoryAndAsset() {
      if (!this.showByDevice) {
        for (let instance of this.instances) {
          if (
            this.selectedInstanceIds.find(function(selectedinstance) {
              return selectedinstance === instance.id
            })
          ) {
            instance.resourceId = this.updateBulk.assetvalue
            if (instance.resourceId) {
              this.onAssetChanged(instance)
            }
          }
        }
      } else {
        for (let instance in this.deviceInstanceMap) {
          if (
            this.selectedInstanceIds.find(s => {
              return s === this.deviceInstanceMap[instance].instances[0].id
            })
          ) {
            this.deviceInstanceMap[
              instance
            ].resourceId = this.updateBulk.assetvalue
            if (this.deviceInstanceMap[instance].resourceId) {
              this.onAssetChanged(null, this.deviceInstanceMap[instance])
            }
          }
        }
      }
      this.poptab = false
      this.selectedInstanceIds = []
      this.$forceUpdate()
    },
    quickSearches() {
      let string = this.quickSearchQueries
      if (string) {
        this.listCount = null
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
      this.totalCount = null
      this.toggleQuickSearches()
      this.quickSearchQueries = null
      this.listCount = null
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
      this.selectedInstances = this.instances.filter(inst =>
        this.selectedInstanceIds.includes(inst.id)
      )
      let booleanInstances = this.selectedInstances.filter(inst =>
        [3, 4, 5].includes(inst.instanceType)
      )
      if (booleanInstances.length === this.selectedInstances.length) {
        this.subscribe()
      } else {
        this.showThresholdDialog = true
      }
    },
    loadInstancesCount(data) {
      if (isEmpty(this.selectedController)) return

      let url = '/v2/instances/count?controllerId=' + this.selectedController.id
      if (this.selectedTab === 'mapped') {
        url += '&configured=true&fetchMapped=true'
      } else if (this.selectedTab === 'subscribed') {
        url += '&subscribed=true&fetchMapped=true'
      } else {
        url += '&configured=true&fetchMapped=false'
      }
      let queryObj = {
        search: this.quickSearchQueries,
        count: true,
      }
      let params
      params = 'count=' + queryObj.count
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      url = url + '&' + params
      let self = this
      this.$http.get(url).then(response => {
        if (response.data.result.instances) {
          if (
            this.$route.query.page & data ||
            response.data.result.instances[0].count === 0
          ) {
            this.$router.replace({
              query: {},
            })
          }
          self.listCount = response.data.result.instances[0].count
          if (!this.totalCount) {
            this.totalCount = self.listCount
          }
        }
      })
    },
    loadInstances(loadMore) {
      this.instances = []
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
      if (!this.listCount) {
        this.$refs['f-page'].reset()
        this.loadInstancesCount()
      } else if (this.quickSearchQueries) {
        this.$refs['f-page'].reset()
      }
      this.loading = true

      let url = '/v2/instances/' + this.selectedController.id
      if (this.selectedTab === 'mapped') {
        url += '?configured=true&fetchMapped=true'
      } else if (this.selectedTab === 'subscribed') {
        url += '?subscribed=true&fetchMapped=true'
      } else {
        url += '?configured=true&fetchMapped=false'
      }
      let queryObj = {
        page: this.page,
        search: this.quickSearchQueries,
      }
      let params = ''
      if (queryObj.search) {
        params = params + '&search=' + queryObj.search
      }
      url = url + params

      url += '&perPage=' + this.perPage + '&page=' + this.page
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.selectedInstanceIds = []
            if (response.data.result.instances) {
              let instances = response.data.result.instances.map(instance => {
                if (instance.resourceId) {
                  if (instance.categoryId) {
                    // temp fix for optitima
                    this.loadAssets(instance.categoryId)
                  }
                }
                return instance
              })
              this.instances = instances
              this.setReadingMap(response.data.result.readings)
              this.setInstancesByDevices(!loadMore, instances)
              if (this.selectedTab === 'mapped') {
                for (let ins in instances) {
                  if (this.readingMap) {
                    let currentInstance = instances[ins]
                    let readingFieldInstance = this.readingMap[
                      currentInstance.resourceId
                    ]
                      ? this.readingMap[currentInstance.resourceId][
                          currentInstance.fieldId
                        ]
                      : null
                    if (readingFieldInstance) {
                      if (
                        readingFieldInstance.field.metricEnum &&
                        readingFieldInstance.field.metricEnum !== null
                      ) {
                        metric = this.metricsUnits.orgUnitsList.filter(d => {
                          if (
                            this.readingMap[instances[ins].resourceId][
                              instances[ins].fieldId
                            ].field.metricEnum === d.metricEnum._name
                          ) {
                            return d
                          }
                        })

                        for (let i in this.metricsUnits.metricWithUnits) {
                          if (i === metric[0].metricEnum._name) {
                            for (let j in this.metricsUnits.metricWithUnits[
                              i
                            ]) {
                              if (
                                this.metricsUnits.metricWithUnits[i][j]
                                  .unitId === metric[0].unit
                              ) {
                                this.instances[
                                  ins
                                ].defaultUnit = this.metricsUnits.metricWithUnits[
                                  i
                                ][j].symbol
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          this.loading = false
        })
        .catch(_ => {
          this.loading = false
        })
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
            operator: 10,
            threshold: instance.threshold,
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
        .catch(() => {
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
              .catch(() => {
                this.unsubscribing = false
              })
          }
        })
    },
    validateThreshold() {
      let lessThreshold = this.selectedInstances.some(
        inst =>
          ![3, 4, 5].includes(inst.instanceType) &&
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
    setInstancesByDevices(reInit, instances) {
      if (reInit) {
        this.deviceInstanceMap = {}
      }
      instances.forEach(instance => {
        let name = instance.device
        if (!this.deviceInstanceMap[name]) {
          this.$set(this.deviceInstanceMap, name, {
            device: name,
            resourceId: instance.resourceId > 0 ? instance.resourceId : null,
            categoryId: instance.categoryId > 0 ? instance.categoryId : null,
            instances: [],
          })
        } else if (
          instance.resourceId &&
          this.deviceInstanceMap[name].resourceId &&
          instance.resourceId !== this.deviceInstanceMap[name].resourceId
        ) {
          name = instance.device + '_' + instance.resourceId
          if (!this.deviceInstanceMap[name]) {
            this.$set(this.deviceInstanceMap, name, {
              device: instance.device,
              resourceId: instance.resourceId > 0 ? instance.resourceId : null,
              categoryId: instance.categoryId > 0 ? instance.categoryId : null,
              instances: [],
            })
          }
        }
        this.deviceInstanceMap[name].instances.push(instance)
      })
    },
    onCategoryChanged(instance, info) {
      let categoryId
      if (instance) {
        this.$set(instance, 'resourceId', null)
        this.$set(instance, 'fieldId', null)
        categoryId = instance.categoryId
      } else {
        categoryId = info.categoryId
        this.$set(info, 'resourceId', null)
        info.instances.forEach(inst => {
          this.$set(inst, 'fieldId', null)
        })
      }
      this.loadAssets(categoryId)
    },
    onAssetChanged(instance, info) {
      let resourceId
      if (instance) {
        this.$set(instance, 'fieldId', null)
        resourceId = instance.resourceId
      } else {
        resourceId = info.resourceId
        info.instances.forEach(inst => {
          this.$set(inst, 'fieldId', null)
        })
      }
      this.loadReadings(resourceId)
    },
    loadAssets(categoryId) {
      if (this.assets[categoryId]) {
        return this.assets[categoryId]
      }
      this.$set(this.assets, categoryId, [])
      this.$util.loadAsset({ categoryId: categoryId }).then(response => {
        this.$set(this.assets, categoryId, response.assets)
      })
    },
    loadReadings(resourceId, fetchNew) {
      if (!fetchNew && this.readingMap[resourceId]) {
        return
      }
      this.$set(this.readingMap, resourceId, {})
      this.$util.loadLatestReading(resourceId, false, true).then(readings => {
        this.setReadingMap(readings)
      })
    },
    setReadingMap(readings) {
      if (readings) {
        readings.forEach(reading => {
          if (!this.readingMap[reading.resourceId]) {
            this.$set(this.readingMap, reading.resourceId, {})
          }
          if (reading.unit > 0) {
            reading.inputUnit = reading.unit
          }
          this.$set(
            this.readingMap[reading.resourceId],
            reading.fieldId,
            reading
          )
        })
      }
    },
    mapInstance() {
      this.loading = true
      this.$http
        .post('/v2/instances/map', {
          controllerId: this.selectedController.id,
          instances: this.points,
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.loading = false
            this.$message.error(response.data.message)
          } else {
            this.$message.success('Readings Added Successfully')
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

    isMetricAvailable(resourceId, fieldId) {
      if (this.readingMap[resourceId]) {
        let reading = this.readingMap[resourceId][fieldId]
        return reading && reading.field.metricEnum
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

    showInputReadingDialog(reading) {
      this.showInputDialog = true
      this.selectedReading = reading
      this.inputValues = {}
      if (reading.inputValues) {
        this.inputValues = this.$helpers.cloneObject(reading.inputValues)
      } else {
        if (reading.field.dataType === 4) {
          this.$set(this.inputValues, [0], '0')
          this.$set(this.inputValues, [1], '1')
        } else if (reading.field.dataType === 8) {
          for (let idx in reading.field.enumMap) {
            this.$set(this.inputValues, idx, idx + '')
          }
        }
      }
    },

    setInputValue() {
      let values = []
      for (let idx in this.inputValues) {
        if (this.inputValues[idx] || this.inputValues[idx] === 0) {
          values.push({
            idx,
            inputValue: this.inputValues[idx],
          })
        }
      }
      this.$http
        .post('/v2/reading/setinputvalue', {
          readingId: this.selectedReading.id,
          inputValues: values,
        })
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            this.$message.success('Values set successfully')
            this.$set(this.selectedReading, 'inputValues', this.inputValues)
          }
        })
    },
    loadMore() {
      if (this.canFetchMore) {
        this.loadInstances(true)
      }
    },
    onControllerSelected(controller) {
      this.selectedController = controller
      this.listCount = null
      this.totalCount = null
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
</style>
