<template>
  <div>
    <outside-click
      :visibility="showSearchfilter"
      v-if="showSearchfilter"
      @onOutsideClick="close()"
    >
      <div class="inline-flex hideSearch">
        <el-select
          v-model="fieldName"
          slot="prepend"
          filterable
          placeholder="Select"
          class="search-select-comp"
        >
          <el-option
            v-for="(label, value) in config.data"
            :key="value"
            :label="config.data[value].label"
            :value="value"
          ></el-option>
          <el-button slot="append" prefix-icon="el-icon-search"></el-button>
        </el-select>
        <div
          v-if="
            config.data[fieldName] &&
              config.data[fieldName].displayType === 'select' &&
              config.data[fieldName].type !== 'date'
          "
        >
          <el-select
            v-bind:disabled="
              config.data[fieldName].operatorId == 1 ||
                config.data[fieldName].operatorId == 2
            "
            ref="dropdown"
            v-model="config.data[fieldName].value"
            multiple
            collapse-tags
            filterable
            :placeholder="
              config.data[fieldName].operatorId == 1
                ? 'is Empty'
                : config.data[fieldName].operatorId == 2
                ? 'is Not Empty'
                : 'Search..'
            "
            class="search-select-comp2 search-border-left fc-tag"
            popper-class="f-search-popover"
            @change="searchchange()"
          >
            <el-option
              v-for="(label, value) in config.data[fieldName].options"
              :key="value"
              :label="label"
              :value="value"
            ></el-option>
            <el-option
              v-if="
                config.data[fieldName].type === 'date' &&
                  !config.data[fieldName].hideDateRange
              "
              label="Custom"
              value="20"
            ></el-option>
            <div class="search-select-filter-btn">
              <el-button
                class="btn-green-full filter-footer-btn fw-bold"
                @click="apply()"
                >DONE</el-button
              >
            </div>
          </el-select>
        </div>
        <div
          v-if="
            config.data[fieldName].displayType === 'lookup' &&
              !config.data[fieldName].specialType
          "
        >
          <FLookupFieldWrapper
            class="search-input-comp"
            v-model="(formModel[fieldName] || {}).id"
            :field="metaInfo.fields.find(v => v.name === fieldName)"
            :label="(formModel[fieldName] || {}).label"
            @recordSelected="value => setLookUpFilter(value, fieldName)"
            @showingLookupWizard="showingLookupWizard = true"
          ></FLookupFieldWrapper>
        </div>
        <div
          v-if="
            config.data[fieldName] &&
              config.data[fieldName].displayType === 'select' &&
              config.data[fieldName].type === 'date'
          "
        >
          <el-select
            filterable
            v-model="config.data[fieldName].value"
            collapse-tags
            placeholder="Select"
            class="search-select-comp2 search-border-left fc-tag fc-search-bar-select"
            v-if="config.data[fieldName].value !== '20'"
            popper-class="f-search-popover"
            :popper-append-to-body="false"
            @change="filterApply()"
          >
            <el-option
              v-for="(label, value) in config.data[fieldName].options"
              :key="value"
              :label="label"
              :value="value"
            ></el-option>
            <el-option
              v-if="
                config.data[fieldName].type === 'date' &&
                  !config.data[fieldName].hideDateRange
              "
              label="Custom"
              value="20"
            ></el-option>
          </el-select>
        </div>
        <f-date-picker
          ref="fdate"
          class="mT10 position-relative new-search-date-filter search-date-picker"
          v-show="
            config.data[fieldName] &&
              config.data[fieldName].type === 'date' &&
              (config.data[fieldName].single
                ? config.data[fieldName].value === '20'
                : config.data[fieldName].value.includes('20'))
          "
          v-model="config.data[fieldName].dateRange"
          :type="'datetimerange'"
          :value-format="'timestamp'"
          :format="'dd-MM-yyyy HH:mm'"
          @change="onFilterChanged"
        ></f-date-picker>
        <div
          v-if="
            config.data[fieldName] &&
              config.data[fieldName].displayType === 'string'
          "
        >
          <el-input
            v-model="config.data[fieldName].value"
            v-bind:disabled="
              config.data[fieldName].operatorId == 1 ||
                config.data[fieldName].operatorId == 2
            "
            :placeholder="
              config.data[fieldName].operatorId == 1
                ? 'is Empty'
                : config.data[fieldName].operatorId == 2
                ? 'is Not Empty'
                : 'Search..'
            "
            clearable
            class="input-with-select search-input-comp search-icon-set"
            slot="prepend"
            @keyup.native.enter="apply()"
            :autofocus="true"
            type="search"
            :prefix-icon="
              config.data[fieldName].operatorId == 1 ||
              config.data[fieldName].operatorId == 2
                ? ''
                : 'el-icon-search'
            "
          ></el-input>
        </div>
        <div
          v-if="
            config.data[fieldName] &&
              config.data[fieldName].displayType === 'number'
          "
        >
          <el-input
            type="number"
            v-model="config.data[fieldName].value"
            v-bind:disabled="
              config.data[fieldName].operatorId == 1 ||
                config.data[fieldName].operatorId == 2
            "
            :placeholder="
              config.data[fieldName].operatorId == 1
                ? 'is Empty'
                : config.data[fieldName].operatorId == 2
                ? 'is Not Empty'
                : 'Search..'
            "
            clearable
            class="input-with-select search-input-comp search-icon-set"
            slot="prepend"
            @keyup.native.enter="apply()"
            :autofocus="true"
            :prefix-icon="
              config.data[fieldName].operatorId == 1 ||
              config.data[fieldName].operatorId == 2
                ? ''
                : 'el-icon-search'
            "
          ></el-input>
        </div>
        <template
          v-if="
            config.data[fieldName] &&
              ['asset', 'space'].includes(config.data[fieldName].displayType)
          "
        >
          <div class="resource-list">
            <el-select
              filterable
              class="multi resource-list el-input-textbox-full-border search-select-comp2 down-arrow-remove search-border-left fc-tag"
              multiple
              collapse
              :value="
                config.data[fieldName].value &&
                config.data[fieldName].value.length
                  ? [1]
                  : null
              "
              disabled
              style="width: 100%"
            >
              <el-option
                :label="
                  config.data[fieldName].value &&
                  config.data[fieldName].value.length
                    ? config.data[fieldName].value.length +
                      (config.data[fieldName].displayType === 'asset'
                        ? ' Asset'
                        : ' Space') +
                      (config.data[fieldName].value.length > 1 ? 's' : '') +
                      ' selected'
                    : ''
                "
                :value="1"
              ></el-option>
            </el-select>
            <i
              v-if="
                config.data[fieldName].operatorId != 1 &&
                  config.data[fieldName].operatorId != 2
              "
              @click="$set(config.data[fieldName], 'chooserVisibility', true)"
              slot="suffix"
              class="el-input__icon el-icon-search filter-space-search-icon3"
            ></i>
          </div>
        </template>
        <el-button
          class="pointer user-select-none filter-btn"
          @click.stop="selectOperatorDialog()"
          :disabled="
            config.data[fieldName].type === 'date' &&
              config.data[fieldName].value !== '20'
          "
        >
          <img src="~assets/filter.svg" style="width: 18px;" />
        </el-button>

        <!-- condition block -->

        <transition name="fade" v-if="showFilterCondition">
          <outside-click
            :visibility.sync="showFilterCondition"
            class="filter-condition-block"
          >
            <div class="filter-condition-body">
              <div class="filter-condition-radion-btn pT10">
                <div
                  class="filter-radio-display"
                  v-if="
                    fieldName !== 'createdTime' &&
                      fieldName !== 'dueDate' &&
                      fieldName !== 'actualWorkEnd' &&
                      fieldName !== 'warrantyExpiryDate' &&
                      config.data[fieldName] &&
                      fieldName !== 'acknowledgedTime' &&
                      fieldName !== 'clearedTime' &&
                      fieldName !== 'modifiedTime' &&
                      fieldName !== 'isAcknowledged' &&
                      fieldName !== 'connected' &&
                      config.data[fieldName] &&
                      config.data[fieldName].operator !== 'DATE_TIME' &&
                      config.data[fieldName].operator !== 'DATE'
                  "
                >
                  <div v-if="operators && operators[fieldName]">
                    <el-radio
                      v-model="config.data[fieldName].operatorId"
                      v-for="label in moduleMeta.operators[
                        operators[fieldName]
                      ]"
                      :key="label.operatorId"
                      :label="label.operatorId"
                      :value="label.operatorId"
                      v-if="selectedlist(label, operators[fieldName])"
                      class="fc-radio-btn pB10"
                    >
                      {{ label.operator }}
                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            config.data[fieldName].displayType === 'select' &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <el-select
                            v-model="config.data[fieldName].value"
                            multiple
                            collapse-tags
                            filterable
                            placeholder="Select"
                            class="search-select-comp2 fc-tag mT10 search-border-left fc-search-select-width"
                          >
                            <el-option
                              v-for="(label, value) in config.data[fieldName]
                                .options"
                              :key="value"
                              :label="label"
                              :value="value"
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            config.data[fieldName].displayType === 'string' &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <el-input
                            class="width270px fc-input-full-border2 filter-condition-input"
                            v-model="config.data[fieldName].value"
                            type="text"
                            :autofocus="true"
                          ></el-input>
                        </div>
                      </div>

                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            config.data[fieldName].displayType === 'number' &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <el-input
                            class="width270px fc-input-full-border2 filter-condition-input"
                            v-model="config.data[fieldName].value"
                            type="number"
                            :autofocus="true"
                          ></el-input>
                        </div>
                      </div>

                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            ['asset', 'space'].includes(
                              config.data[fieldName].displayType
                            ) &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <div class="resource-list">
                            <el-select
                              class="multi resource-list el-input-textbox-full-border fc-tag mT1 fc-search-select-width mT10"
                              filterable
                              multiple
                              collapse
                              :value="
                                config.data[fieldName].value &&
                                config.data[fieldName].value.length
                                  ? [1]
                                  : null
                              "
                              disabled
                            >
                              <el-option
                                :label="
                                  config.data[fieldName].value &&
                                  config.data[fieldName].value.length
                                    ? config.data[fieldName].value.length +
                                      (config.data[fieldName].displayType ===
                                      'asset'
                                        ? ' Asset'
                                        : ' Space') +
                                      (config.data[fieldName].value.length > 1
                                        ? 's'
                                        : '') +
                                      ' selected'
                                    : ''
                                "
                                :value="1"
                              ></el-option>
                            </el-select>
                            <i
                              @click="
                                $set(
                                  config.data[fieldName],
                                  'chooserVisibility',
                                  true
                                )
                              "
                              slot="suffix"
                              class="el-input__icon el-icon-search filter-spacce-condition-search"
                            ></i>
                          </div>
                          <space-asset-multi-chooser
                            v-if="config.data[fieldName].chooserVisibility"
                            :showAsset="
                              config.data[fieldName].displayType === 'asset'
                            "
                            @associate="
                              associateResource($event, config.data[fieldName])
                            "
                            :initialValues="{
                              isIncludeResource: true,
                              selectedResources: config.data[
                                fieldName
                              ].value.map(id => ({ id: parseInt(id) })),
                            }"
                            :visibility.sync="
                              config.data[fieldName].chooserVisibility
                            "
                            :hideBanner="true"
                            :selectedResource="selectedeResourceObj"
                            class="fc-input-full-border-select2"
                          ></space-asset-multi-chooser>
                        </div>
                      </div>
                    </el-radio>
                  </div>

                  <div v-else-if="config.data[fieldName].operator">
                    <el-radio
                      v-model="config.data[fieldName].operatorId"
                      v-for="label in moduleMeta.operators[
                        config.data[fieldName].operator
                      ]"
                      :key="label.operatorId"
                      :label="label.operatorId"
                      :value="label.operatorId"
                      v-if="
                        selectedlist(label, config.data[fieldName].operator)
                      "
                      class="fc-radio-btn pB10"
                    >
                      {{ label.operator }}
                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            config.data[fieldName].displayType === 'select' &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <el-select
                            v-model="config.data[fieldName].value"
                            multiple
                            collapse-tags
                            filterable
                            placeholder="Select"
                            class="search-select-comp2 fc-tag mT10 search-border-left fc-search-select-width"
                          >
                            <el-option
                              v-for="(label, value) in config.data[fieldName]
                                .options"
                              :key="value"
                              :label="label"
                              :value="value"
                            ></el-option>
                          </el-select>
                        </div>
                      </div>
                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            config.data[fieldName].displayType === 'string' &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <el-input
                            class="width270px fc-input-full-border2 filter-condition-input"
                            v-model="config.data[fieldName].value"
                            style="width:100%"
                            type="text"
                            :autofocus="true"
                          ></el-input>
                        </div>
                      </div>

                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            config.data[fieldName].displayType === 'number' &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <el-input
                            class="width270px fc-input-full-border2 filter-condition-input"
                            v-model="config.data[fieldName].value"
                            type="number"
                            :autofocus="true"
                          ></el-input>
                        </div>
                      </div>

                      <div
                        v-if="
                          label.operatorId == config.data[fieldName].operatorId
                        "
                      >
                        <div
                          v-if="
                            ['asset', 'space'].includes(
                              config.data[fieldName].displayType
                            ) &&
                              label.operatorId !== 1 &&
                              label.operatorId !== 2
                          "
                        >
                          <div class="resource-list">
                            <el-select
                              class="multi resource-list el-input-textbox-full-border fc-tag mT10 fc-search-select-width"
                              filterable
                              multiple
                              collapse
                              :value="
                                config.data[fieldName].value &&
                                config.data[fieldName].value.length
                                  ? [1]
                                  : null
                              "
                              disabled
                              style="width: 100%"
                            >
                              <el-option
                                :label="
                                  config.data[fieldName].value &&
                                  config.data[fieldName].value.length
                                    ? config.data[fieldName].value.length +
                                      (config.data[fieldName].displayType ===
                                      'asset'
                                        ? ' Asset'
                                        : ' Space') +
                                      (config.data[fieldName].value.length > 1
                                        ? 's'
                                        : '') +
                                      ' selected'
                                    : ''
                                "
                                :value="1"
                              ></el-option>
                            </el-select>
                            <i
                              @click="
                                $set(
                                  config.data[fieldName],
                                  'chooserVisibility',
                                  true
                                )
                              "
                              slot="suffix"
                              class="el-input__icon el-icon-search filter-spacce-condition-search"
                            ></i>
                          </div>
                          <space-asset-multi-chooser
                            v-if="config.data[fieldName].chooserVisibility"
                            :showAsset="
                              config.data[fieldName].displayType === 'asset'
                            "
                            @associate="
                              associateResource($event, config.data[fieldName])
                            "
                            :initialValues="{
                              isIncludeResource: true,
                              selectedResources: config.data[
                                fieldName
                              ].value.map(id => ({ id: parseInt(id) })),
                            }"
                            :visibility.sync="
                              config.data[fieldName].chooserVisibility
                            "
                            :hideBanner="true"
                            :selectedResource="selectedeResourceObj"
                            class="fc-input-full-border-select2"
                          ></space-asset-multi-chooser>
                        </div>
                      </div>
                    </el-radio>
                  </div>
                </div>
                <div v-else>
                  <el-radio
                    v-model="config.data[fieldName].value"
                    class="fc-radio-btn pB10"
                    v-for="(label4, value4) in config.data[fieldName].options"
                    :key="value4"
                    :label="value4"
                    >{{ label4 }}</el-radio
                  >
                  <el-radio
                    v-model="config.data[fieldName].value"
                    v-if="
                      config.data[fieldName].type === 'date' &&
                        !config.data[fieldName].hideDateRange
                    "
                    label="20"
                    value="20"
                    >Custom</el-radio
                  >
                  <f-date-picker
                    class="mT10 position-relative"
                    v-if="
                      config.data[fieldName].type === 'date' &&
                        (config.data[fieldName].single
                          ? config.data[fieldName].value === '20'
                          : config.data[fieldName].value.includes('20'))
                    "
                    v-model="config.data[fieldName].dateRange"
                    :type="'datetimerange'"
                    :value-format="'timestamp'"
                    :format="'dd-MM-yyyy HH:mm'"
                    @change="onFilterChanged"
                  ></f-date-picker>
                </div>
              </div>
            </div>
            <div class="search-select-filter-btn">
              <el-button
                class="btn-green-full filter-footer-btn fw-bold"
                @click="apply()"
                >DONE</el-button
              >
            </div>
          </outside-click>
        </transition>
      </div>
    </outside-click>
    <space-asset-multi-chooser
      v-if="config.data[fieldName] && config.data[fieldName].chooserVisibility"
      :showAsset="config.data[fieldName].displayType === 'asset'"
      @associate="associateResource($event, config.data[fieldName])"
      :initialValues="{
        isIncludeResource: true,
        selectedResources: config.data[fieldName].value.map(id => ({
          id: parseInt(id),
        })),
      }"
      :visibility.sync="config.data[fieldName].chooserVisibility"
      :hideBanner="true"
      class="fc-input-full-border-select2"
    ></space-asset-multi-chooser>
    <div></div>
    <el-dialog
      :visible.sync="showSaveDialog"
      :append-to-body="true"
      :fullscreen="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog55 setup-dialog save-dialog"
      :before-close="closeDialog"
      style="z-index: 999999;overflow-y:scroll !important;"
    >
      <div class="wo-save-dialog">
        <p class="setup-modal-title" style="margin:0;">Save as New View</p>
        <el-row>
          <el-col :span="13">
            <el-input
              v-model="newViewName"
              placeholder="Name"
              :autofocus="true"
            ></el-input>
          </el-col>
        </el-row>
        <div v-if="!config.disableColumnCustomization">
          <div class="wo-save-subH">CUSTOMIZE COLUMNS</div>
          <div class="wo-save-greytxt">
            Drag and drop columns that you wish to save in this view
          </div>
          <div class="wo-rearrage-container">
            <div class="wo-rearrage-left wo-rearrange-box mR40">
              <p class="rearrange-box-header">
                <span class="rearrange-box-H">AVAILABLE COLUMNS</span>
                <!-- <span class="rearrange-box-sub">Add All</span> -->
              </p>

              <div class="rearrange-box-body">
                <div v-if="moduleMeta">
                  <draggable
                    v-model="allColumns"
                    class="dragArea"
                    :options="{ group: 'people' }"
                  >
                    <div
                      class="rearrange-el-block"
                      v-for="(element, index) in availableColumns"
                      :key="index"
                      v-if="element.id > '-1'"
                    >
                      <span
                        class="rearrange-plus-icon"
                        v-on:click="addcolumn(element, index, availableColumns)"
                        style="cursor: pointer;"
                        >+</span
                      >
                      <span class="rearrange-txt">{{ element.label }}</span>
                      <span
                        style="text-align:right;float:right;margin-right: 4px;margin-top: 4px;"
                      >
                        <img
                          src="~assets/move.svg"
                          class="icon-right"
                          style="height: 16px;"
                        />
                      </span>
                    </div>
                  </draggable>
                </div>
              </div>
            </div>
            <div class="wo-rearrage-right wo-rearrange-box">
              <p class="rearrange-box-header">
                <span class="rearrange-box-H">SELECTED COLUMNS</span>
                <!-- <span class="rearrange-box-sub">Reset</span> -->
              </p>
              <div class="rearrange-box-body">
                <div v-if="moduleMeta">
                  <div
                    class="rearrange-el-block"
                    style="border: solid 1px #f0f0f0;"
                    v-for="(col, index) in fixedSelectedCol"
                    :key="index"
                  >
                    <span class="rearrange-txt2" style="opacity: 0.5;">
                      {{ col.label }}
                    </span>
                  </div>
                  <draggable
                    v-model="selectedColumns"
                    class="dragArea"
                    :options="{ group: 'people' }"
                  >
                    <div
                      class="rearrange-el-block"
                      v-for="(element, index) in selectedColumns"
                      :key="index"
                      v-if="element.id > '-1'"
                    >
                      <span>
                        <i
                          class="el-icon-close rearrange-plus-icon3"
                          style="font-size: 16px;font-weight: 600;cursor: pointer;"
                          v-on:click="
                            removeColumn(element, index, selectedColumns)
                          "
                        ></i>
                      </span>
                      <span class="rearrange-txt2">{{ element.label }}</span>
                      <span
                        style="text-align:right;float:right;margin-right: 4px;margin-top: 4px;"
                      >
                        <img
                          src="~assets/move.svg"
                          class="icon-right"
                          style="height: 16px;"
                        />
                      </span>
                    </div>
                  </draggable>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="wo-save-subH">FILTERS</div>
        <div
          v-if="Object.keys(filtersApplies).length > '0'"
          class="fc-grey-text-input-label pB10"
        >
          Existing Filters
        </div>
        <div v-if="Object.keys(filtersApplies).length > '0'">
          <new-tag
            :config="config"
            :filters="filtersApplies"
            :showCloseIcon="false"
            class="saveas-newtag"
          ></new-tag>
        </div>
        <div v-if="appliedFilters !== null">
          <div class="fc-grey-text-input-label pB10 pT10">New Filters</div>
          <div>
            <new-tag
              :config="config"
              :filters="appliedFilters"
              :showFilterAdd="showAddFilter"
              :showCloseIcon="true"
              class="saveas-newtag"
            ></new-tag>
          </div>
        </div>
        <!-- </div> -->
        <!-- user -->
        <div class="wo-save-subH">SHARING PERMISSIONS</div>
        <!-- radio user -->
        <el-row align="middle" style="margin:0px;" :gutter="20">
          <el-col :span="24" style="padding-right: 35px;padding-left:0px;">
            <div class="add">
              <el-radio v-model="shareTo" :label="1" class="newradio"
                >Only Me</el-radio
              >
              <el-radio v-model="shareTo" :label="2" class="newradio"
                >Everyone</el-radio
              >
              <el-radio v-model="shareTo" :label="3" class="newradio"
                >Specific</el-radio
              >
            </div>
          </el-col>
        </el-row>
        <!-- radio user -->

        <!-- Team -->
        <el-row v-if="shareTo === 3" class="mT30 el-select-block">
          <el-col :span="24">
            <label class="sharing-label">Team</label>
            <el-select
              filterable
              v-model="sharedGroups"
              multiple
              collapse-tags
              class="width60"
              :placeholder="$t('common.wo_report.choose_users')"
            >
              <el-option
                v-for="group in groups"
                :key="group.id"
                :label="group.name"
                :value="group.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row v-if="shareTo === 3" class="mT30 el-select-block">
          <el-col :span="24">
            <label class="sharing-label">Role</label>
            <el-select
              filterable
              v-model="sharedRoles"
              multiple
              collapse-tags
              class="width60"
              :placeholder="$t('common.wo_report.choose_roles')"
            >
              <el-option
                v-for="role in roles"
                :key="role.id"
                :label="role.name"
                :value="role.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>

        <el-row v-if="shareTo === 3" class="mT30 el-select-block">
          <el-col :span="24">
            <label class="sharing-label">Staff</label>
            <el-select
              filterable
              v-model="sharedUsers"
              multiple
              collapse-tags
              class="width60"
              :placeholder="$t('common.wo_report.choose_teams')"
            >
              <el-option
                v-for="user in users"
                :key="user.id"
                :label="user.name"
                :value="user.id"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer modal-footer-fixed">
        <el-button @click="showSaveDialog = false" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="newView()" class="modal-btn-save"
          >Save</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import { mapActions, mapState, mapGetters } from 'vuex'
import NewTag from '@/NewTag'
import ViewMixinHelper from '@/mixins/ViewMixin'
import draggable from 'vuedraggable'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import OutsideClick from '@/OutsideClick'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { isEmpty } from '@facilio/utils/validation'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { getFieldOptions } from 'util/picklist'

const spaceFields = ['space', 'visitedSpace']

export default {
  mixins: [ViewMixinHelper],
  props: [
    'sortConfig',
    'showSearch',
    'config',
    'saveAs',
    'save',
    'resetFilters',
    'defaultFilter',
    'moduleName',
    'loadViews',
    'includeAllMetaFields',
  ],
  data() {
    return {
      configData: {},
      states: [],
      existingFilter: {},
      excludeOperators: [74, 75, 76, 77, 78, 79, 35, 87, 52, 53],
      filtersApplies: {},
      nonSelectableColumns: [
        'assignmentGroup',
        'asset',
        'week',
        'hour',
        'month',
        'day',
        'localId',
        'id',
        'photoId',
        'space',
        'hideToCustomer',
        'parentAssetId',
        'moduleState',
        'stateFlowId',
        'id',
      ],
      excludeCustomfieldsForSpecificModules: [
        'readingrule',
        'preventivemaintenance',
      ],
      selectedeResourceObj: [],
      showAddFilter: true,
      configs: this.$helpers.cloneObject(this.config),
      showFilterCondition: false,
      showingLookupWizard: false,
      showSearchfilter: true,
      chooserVisibility: false,
      selectOperatotr: false,
      fieldName: this.defaultFilter,
      formModel: {},
      filterApplied: false,
      showSaveDialog: false,
      select: '',
      radio2: '',
      allColumns: [],
      availableColumns: [],
      selectedColumns: [],
      loading: false,
      operators: {
        subject: 'STRING',
        title: 'STRING',
        primaryContactName: 'STRING',
        qrVal: 'STRING',
        id: 'STRING',
        description: 'STRING',
        name: 'STRING',
        primaryContactEmail: 'STRING',
        companyName: 'STRING',
        primaryContactPhone: 'STRING',
        tenant: 'LOOKUP',
        vendor: 'LOOKUP',
        assetCategory: 'LOOKUP',
        kpiCategory: 'LOOKUP',
        assetDepartment: 'LOOKUP',
        siteId: 'LOOKUP',
        assetType: 'LOOKUP',
        category: 'LOOKUP',
        dueDate: 'DATE',
        actualWorkEnd: 'DATE',
        space: 'LOOKUP',
        visitedSpace: 'LOOKUP',
        asset: 'LOOKUP',
        sourceType: 'NUMBER',
        ticketType: 'LOOKUP',
        typeId: 'LOOKUP',
        status: 'LOOKUP',
        moduleState: 'LOOKUP',
        assignedTo: 'LOOKUP',
        registeredBy: 'LOOKUP',
        executedBy: 'LOOKUP',
        frequency: 'NUMBER',
        createdTime: 'LOOKUP',
        tasks: 'LOOKUP',
        priority: 'LOOKUP',
        alarmType: 'NUMBER',
        acknowledgedBy: 'LOOKUP',
        severity: 'LOOKUP',
        previousSeverity: 'LOOKUP',
        readingAlarmCategory: 'LOOKUP',
        readingrule: 'LOOKUP',
        faultType: 'LOOKUP',
      },
      statusFieldNotNeeded: ['readingrule', 'newreadingalarm'],
      newViewName: '',
      selectedResourceName: null,
      shareTo: 2,
      sharedGroups: [],
      sharedUsers: [],
      sharedRoles: [],
      viewSharing: [],
    }
  },
  components: {
    draggable,
    SpaceAssetMultiChooser,
    OutsideClick,
    NewTag,
    FDatePicker,
    FLookupFieldWrapper,
  },
  watch: {
    appliedFilters(val) {
      if (val) {
        if (
          Object.keys(val).length &&
          (!this.filterApplied || !this.config.saveView)
        )
          this.filterApplied = true
      } else {
        if (this.filterApplied) {
          this.reset(null, true)
        }
      }
      this.clearLookUpFilter(val)
    },
    saveAs: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.init()
          this.showSaveDialog = true
        }
      },
      deep: true,
    },
    resetFilters: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.reset(null, false)
          this.resetValues()
        }
      },
      deep: true,
    },
    save: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.init()
          this.confirmationEditViews()
        }
      },
      deep: true,
    },
    fieldName: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.searchchange()
        }
      },
      deep: true,
    },
    viewFilters() {
      // this.reset(null, true)
    },
    shareTo(val, oldVal) {
      if (val === 3) {
        this.sharedUsers.push(this.userDetails.id)
      }
    },
    allFields: {
      handler(val) {
        this.allColumns = this.$helpers.cloneObject(val)
        this.checkColumns()
        this.setOptions()
      },
      deep: true,
    },
    viewColumns: {
      handler() {
        this.availableColumns = []
        this.selectedColumns = []
        this.checkColumns()
      },
    },
    showSearch(val) {
      if (val) {
        this.showSearchfilter = true
      }
    },
    defaultFilter(val) {
      this.fieldName = val
    },
    moduleName: {
      handler(newData, oldData) {
        if (newData !== oldData) {
          this.constructCustomObject()
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.allColumns = this.$helpers.cloneObject(this.allFields)
    this.checkColumns()
    this.constructCustomObject()
    if (this.appliedFilters) {
      this.filterApplied = true
    }
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadTicketCategory'),
      this.$store.dispatch('view/loadModuleMeta', this.moduleName),
      this.$store.dispatch('loadReadingAlarmCategory'),
      this.$store.dispatch('loadTicketType'),
      this.$store.dispatch('loadTicketPriority'),
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadAssetDepartment'),
      this.$store.dispatch('loadAssetType'),
      this.$store.dispatch('loadAlarmSeverity'),
      this.$store.dispatch('loadInventoryCategory'),
      this.$store.dispatch('loadRoles'),
      this.$store.dispatch('loadSites'),
      this.$store.dispatch('loadGroups'),
      this.getStateList(),
    ]).then(() => {
      if (!this.statusFieldNotNeeded.includes(this.moduleName)) {
        this.constructStatusField()
      }
      this.setOptions()
    })
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
      users: state => state.users,
      groups: state => state.groups,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
      readingAlarmCategory: state => state.readingAlarmCategory,
      alarmseverity: state => state.alarmSeverity,
      assetcategory: state => state.assetCategory,
      inventoryCategory: state => state.inventoryCategory,
      siteList: state => state.sites,
      assetdepartment: state => state.assetDepartment,
      assettype: state => state.assetType,
      viewDetail: state => state.view.currentViewDetail,
      userDetails: state => state.account.user,
      roles: state => state.roles,
    }),
    ticketcategoryPicklist() {
      return this.ticketcategory.map(d => d.displayName)
    },
    ...mapGetters(['getTicketTypePickList', 'getCurrentUser']),
    tickettype() {
      return this.getTicketTypePickList()
    },
    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    appliedFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    allFields() {
      let obj = []
      if (this.moduleMeta && this.moduleMeta.fields) {
        obj = this.moduleMeta.fields
          .filter(field => {
            return (
              (this.config.availableColumns ||
                this.config.availableColumns.indexOf(field.name) !== -1 ||
                !field.default) &&
              (!this.config.fixedCols ||
                !this.config.fixedCols.includes(field.name))
            )
          })
          .map((field, idx) => {
            return {
              id: field.id,
              label: field.columnDisplayName || field.displayName,
              key: field.name || '',
            }
          })
      }
      return obj
    },
    fixedSelectedCol() {
      return this.config.fixedCols && this.moduleMeta.fields
        ? this.moduleMeta.fields
            .filter(field => this.config.fixedCols.includes(field.name))
            .map(field => ({
              id: field.id,
              label: field.columnDisplayName || field.displayName,
            }))
        : []
    },
    viewFilters() {
      return this.viewDetail ? this.viewDetail.filters : null
    },
    includeParentFilter() {
      return (
        this.$route.query.includeParentFilter &&
        (this.$route.query.includeParentFilter === 'true' ||
          this.$route.query.includeParentFilter === true)
      )
    },
  },
  methods: {
    setLookUpFilter(filter, fieldName) {
      if (!isEmpty(filter)) {
        this.formModel[fieldName].label = filter.label
        this.apply()
      }
      this.showingLookupWizard = false
    },
    clearLookUpFilter(val) {
      let filters = Object.keys(val || {})
      let formModelKey = Object.keys(this.formModel || {})
      if (!isEmpty(filters) && !isEmpty(formModelKey)) {
        formModelKey.forEach(fval => {
          if (!filters.includes(fval)) {
            this.formModel[fval].id = ''
            this.formModel[fval].label = ''
          } else if (filters.includes(fval)) {
            this.formModel[fval].id =
              !isEmpty(val[fval][0]) && !isEmpty(val[fval][0].value)
                ? parseInt(val[fval][0].value[0])
                : ''
            this.formModel[fval].label =
              !isEmpty(val[fval][0]) && !isEmpty(val[fval][0].selectedLabel)
                ? val[fval][0].selectedLabel
                : ''
          }
        })
      }
    },
    getStateList() {
      return this.$http
        .get(`v2/state/list?parentModuleName=${this.moduleName}`)
        .then(({ data }) => {
          this.states = data.result.status || []
        })
        .catch(({ message = 'Error loading states' }) => {
          this.$message.error(message)
        })
    },
    constructStatusField() {
      if (
        !isEmpty(this.moduleMeta) &&
        !isEmpty(this.moduleMeta.module) &&
        isEmpty(this.config.data.moduleState)
      ) {
        let stateOptions = {}
        if (!isEmpty(this.states)) {
          this.states.forEach(state => {
            stateOptions[state.id] = state.displayName
          })
        }
        if (
          (this.moduleMeta.module.stateFlowEnabled ||
            !this.moduleMeta.module.custom) &&
          !this.config.excludeModuleStateField
        ) {
          let stateObj = {
            label: 'Status',
            displayType: 'select',
            options: stateOptions,
            value: [],
            key: 'moduleState',
          }
          this.$set(this.config.data, 'moduleState', stateObj)
        }
      }
    },
    constructCustomObject() {
      let a = {
        stringObj: {
          label: '',
          displayType: 'string',
          value: [],
        },
        dateObj: {
          label: '',
          displayType: 'select',
          dateRange: [],
          type: 'date',
          customdate: true,
          options: {
            '22': 'Today',
            '26': 'Till Yesterday',
            '31': 'This Week',
            '30': 'Last Week',
            '2_50': 'Last 2 Weeks',
            '32': 'Next Week',
            '2_61': 'Next 2 Days',
            '7_61': 'Next 7 Days',
            '28': 'This Month',
            '27': 'Last Month',
          },
          value: '',
        },
      }
      if (
        this.moduleName &&
        !this.excludeCustomfieldsForSpecificModules.includes(this.moduleName)
      ) {
        this.$store
          .dispatch('view/loadModuleMeta', this.moduleName)
          .then(() => {
            let metaObject = this.moduleMeta
            if (metaObject && metaObject.fields) {
              for (let i = 0; i < metaObject.fields.length; i++) {
                let values
                if (
                  (metaObject.fields[i].default != true &&
                    typeof this.includeAllMetaFields === 'undefined') ||
                  this.includeAllMetaFields
                ) {
                  let key = ''
                  key = metaObject.fields[i].name
                  values = this.$helpers.cloneObject(a.stringObj)
                  if (
                    metaObject.fields[i].dataTypeEnum._name == 'DATE' ||
                    metaObject.fields[i].dataTypeEnum._name == 'DATE_TIME'
                  ) {
                    values = this.$helpers.cloneObject(a.dateObj)
                    values.label = metaObject.fields[i].displayName
                    values.default = metaObject.fields[i].default
                    values.operator = metaObject.fields[i].dataTypeEnum._name
                    values['key'] = metaObject.fields[i].name
                    this.$set(this.config.data, key, values)
                  } else if (
                    metaObject.fields[i].dataTypeEnum._name == 'STRING'
                  ) {
                    values.label = metaObject.fields[i].displayName
                    values.displayType = 'string'
                    values.default = metaObject.fields[i].default
                    values.operator = metaObject.fields[i].dataTypeEnum._name
                    values.operatorId = 5
                    this.$set(this.config.data, key, values)
                  } else if (
                    metaObject.fields[i].dataTypeEnum._name == 'BOOLEAN'
                  ) {
                    values.label = metaObject.fields[i].displayName
                    values.displayType = 'select'
                    values.type = 'Boolean'
                    values.default = metaObject.fields[i].default
                    values.operator = metaObject.fields[i].dataTypeEnum._name
                    values.options = {
                      true: metaObject.fields[i].trueVal || 'Yes',
                      false: metaObject.fields[i].falseVal || 'No',
                    }
                    this.$set(values, 'value', '')
                    values['key'] = metaObject.fields[i].name
                    this.$set(this.config.data, key, values)
                  } else if (
                    metaObject.fields[i].dataTypeEnum._name == 'NUMBER'
                  ) {
                    values.label = metaObject.fields[i].displayName
                    values.displayType = 'number'
                    values.default = metaObject.fields[i].default
                    values.operator = metaObject.fields[i].dataTypeEnum._name
                    values.operatorId = 9
                    this.$set(this.config.data, key, values)
                  } else if (
                    metaObject.fields[i].dataTypeEnum._name == 'LOOKUP'
                  ) {
                    values.label = metaObject.fields[i].displayName
                    values.displayType = 'lookup'
                    if (
                      metaObject.fields[i].specialType &&
                      metaObject.fields[i].specialType === 'users'
                    ) {
                      let userOptions = {}
                      this.users.forEach(user => {
                        userOptions[user.id] = user.name
                      })
                      values.specialType === true
                      values.displayType = 'select'
                      values.options = userOptions
                    }
                    values.default = metaObject.fields[i].default
                    if (
                      !isEmpty(metaObject.fields[i].lookupModule) &&
                      !isEmpty(metaObject.fields[i].lookupModule.name)
                    ) {
                      this.$set(
                        values,
                        'lookupModuleName',
                        metaObject.fields[i].lookupModule.name
                      )
                      this.$set(this.operators, key, 'LOOKUP')
                    }
                    values.isCustomFieldLookup = true

                    values.operator = metaObject.fields[i].dataTypeEnum._name
                    values.operatorId = 36
                    this.$set(this.config.data, key, values)
                    let lookupObj = {
                      id: '',
                      label: '',
                    }
                    this.$set(this.formModel, key, lookupObj)
                  } else if (
                    metaObject.fields[i].dataTypeEnum._name == 'ENUM'
                  ) {
                    values.label = metaObject.fields[i].displayName
                    values.default = metaObject.fields[i].default
                    values.displayType = 'select'
                    values.operatorId = 54
                    values.operator = metaObject.fields[i].dataTypeEnum._name
                    values.type = 'enum'
                    values.value = []
                    values.options = metaObject.fields[i].enumMap
                    values['key'] = metaObject.fields[i].name
                    this.$set(this.config.data, key, values)
                  }
                }
              }
            }
          })
      }
      this.dataConfig()
    },
    filterApply() {
      if (this.config.data[this.fieldName].value !== '20') {
        this.apply()
      } else {
        this.$refs['fdate'].focus()
      }
    },
    outsideClick() {
      this.showFilterCondition = false
    },
    ...mapActions({
      saveNewView: 'view/saveNewView',
      editView: 'view/editView',
    }),
    confirmationEditViews() {
      let self = this
      self.$dialog
        .confirm({
          title: 'Edit View',
          message: 'Are you sure you want to EDIT the view?',
          rbDanger: true,
          rbLabel: 'Edit',
        })
        .then(function(value) {
          self.editViews()
        })
    },
    newView() {
      // if (this.viewDetail.isDefault === false) {
      //   this.init()
      //   this.saveAsNewView()
      // } else {
      this.init()
      this.saveAsNewView()
      // }
    },
    selectedlist(label, operatorType) {
      if (label._name === 'BUILDING_IS' && !(this.fieldName === 'space')) {
        return false
      } else if (label._name === 'LOOKUP') {
        return false
      } else if (this.excludeOperators.includes(label.operatorId)) {
        return false
      } else {
        return true
      }
    },
    selectOperatorDialog() {
      this.showFilterCondition = true
      if (
        this.fieldName === 'sourceType' ||
        this.fieldName === 'alarmType' ||
        (this.config.data[this.fieldName].operator &&
          this.config.data[this.fieldName].operator === 'NUMBER')
      ) {
        this.$set(this.config.data[this.fieldName], 'operatorId', 9)
      }
      if (
        !this.config.data[this.fieldName].operatorId &&
        this.fieldName !== 'sourceType'
      ) {
        this.$set(
          this.config.data[this.fieldName],
          'operatorId',
          spaceFields.includes(this.fieldName)
            ? 38
            : this.fieldName === 'subject' ||
              this.fieldName === 'name' ||
              this.fieldName === 'title' ||
              (this.config.data[this.fieldName].operator &&
                this.config.data[this.fieldName].operator === 'STRING')
            ? 5
            : 36
        )
      } else if (this.config.data[this.fieldName].operatorId) {
        this.$set(
          this.config.data[this.fieldName],
          'operatorId',
          this.appliedFilters[this.fieldName].operatorId
        )
      }
    },
    searchchange() {
      let data = this.config.data
      if (this.fieldName === 'tenant') {
        this.loadPickList('tenant', data.tenant)
      }
      if (this.fieldName === 'client') {
        this.loadPickList('client', data.client)
      }
      if (this.fieldName === 'vendor') {
        this.loadPickList('vendors', data.vendor)
      }
      if (this.fieldName === 'itemType') {
        this.loadPickList('itemTypes', data.itemType)
      }
      if (this.fieldName === 'toolType') {
        this.loadPickList('toolTypes', data.toolType)
      }
      if (this.fieldName === 'storeRoom') {
        this.loadPickList('storeRoom', data.storeRoom)
      }
      if (this.fieldName === 'fromStore') {
        this.loadPickList('storeRoom', data.fromStore)
      }
      if (this.fieldName === 'toStore') {
        this.loadPickList('storeRoom', data.toStore)
      }
      if (this.fieldName === 'visitor') {
        this.loadPickList('visitor', data.visitor)
      }
      if (this.fieldName === 'visitorType') {
        this.loadPickList('visitorType', data.visitorType)
      }
      if (
        this.moduleName === 'serviceRequest' &&
        this.fieldName === 'urgency'
      ) {
        this.loadPickList('servicerequestpriority', data.urgency)
      }
      if (this.fieldName === 'rule') {
        this.loadPickList('readingrule', data.rule)
      }
      if (this.fieldName === 'building') {
        this.loadPickList('building', data.building)
      }
    },
    escapeToHide() {},
    clearFields(fieldName) {
      if (
        fieldName === 'subject' ||
        fieldName === 'name' ||
        fieldName === 'title'
      ) {
        this.config.data[fieldName].value = ''
      } else {
        this.config.data[fieldName].value = []
      }
    },
    dataConfig() {
      this.configData = this.config
    },
    editOldView() {
      this.apply()
      this.editViews()
    },
    operatorSelect() {
      this.selectOperatotr = !this.selectOperatotr
    },
    apply() {
      this.$emit('hideSearch')
      let filters = {}
      let lookupFilter = {}
      for (let fieldName in this.config.data) {
        // if (typeof(this.config.data[fieldName].isCustomFieldLookup) === "undefined" || !this.config.data[fieldName].isCustomFieldLookup) {
        // if (fieldName === 'rule') {
        //   this.config.data.rule.push({
        //     moduleName: 'alarmRaeding',
        //   })
        // }
        let field = this.config.data[fieldName]
        let key = field.key || fieldName
        if (
          (field.operatorId === 1 || field.operatorId === 2 || field.value) &&
          (field.operatorId === 1 ||
            field.operatorId === 2 ||
            !Array.isArray(field.value) ||
            field.value.length)
        ) {
          if (field.customdate) {
            this.setCustomDateFilters(filters, fieldName)
          } else {
            let filterObj = {
              operatorId:
                field.operatorId ||
                (spaceFields.includes(fieldName)
                  ? 38
                  : fieldName === 'subject' ||
                    (this.config.data[this.fieldName].operator &&
                      this.config.data[this.fieldName].operator === 'STRING') ||
                    fieldName === 'name' ||
                    fieldName === 'title'
                  ? 5
                  : fieldName === 'sourceType' ||
                    (this.config.data[fieldName].operator &&
                      this.config.data[fieldName].operator === 'NUMBER') ||
                    fieldName === 'alarmType'
                  ? 9
                  : fieldName === 'isAcknowledged' || fieldName === 'connected'
                  ? 15
                  : 36),
            }
            if (field.operatorId === 1 || field.operatorId === 2) {
              if (
                fieldName === 'subject' ||
                fieldName === 'name' ||
                fieldName === 'title'
              ) {
                field.value = ''
              } else if (
                this.config.data[this.fieldName].operator &&
                this.config.data[this.fieldName].operator === 'STRING'
              ) {
                field.value = ''
              } else {
                field.value = []
              }
            }
            if (fieldName === 'assignedTo') {
              let users = [],
                groups = []
              field.value.forEach(user => {
                if (user.indexOf('_group') !== -1) {
                  groups.push(user.substring(0, user.indexOf('_group')))
                } else {
                  users.push(user)
                }
              })
              if (users.length) {
                filterObj.value = users
              }
              if (groups.length) {
                filterObj.value = groups
                key =
                  key === 'assignedToId'
                    ? 'assignmentGroupId'
                    : 'assignmentGroup'
              }
              filters[key] = filterObj
            } else {
              filterObj.value = !Array.isArray(field.value)
                ? [field.value]
                : field.value.map(val => val + '')
              if (spaceFields.includes(fieldName) || fieldName === 'asset') {
                key = field.key || 'resource'
                if (!filters[key]) {
                  filters[key] = []
                }
                filters[key].push(filterObj)
              } else {
                filters[key] = filterObj
              }
            }
          }
        }
      }
      if (Object.keys(this.formModel).length) {
        for (let key in this.formModel) {
          if (this.formModel[key].id) {
            let value = [
              {
                operatorId:
                  this.config.data[key] && this.config.data[key].operatorId
                    ? this.config.data[key].operatorId
                    : 36,
                value: [
                  this.formModel[key].id
                    ? this.formModel[key].id.toString()
                    : '',
                ],
                selectedLabel: this.formModel[key].label
                  ? this.formModel[key].label
                  : '',
              },
            ]
            this.$set(lookupFilter, key, value)
          }
        }
      }
      if (
        Object.keys(filters).length > '0' ||
        Object.keys(this.formModel).length
      ) {
        let queryParam = {
          search: JSON.stringify(filters),
        }
        if (this.config.includeParentCriteria) {
          queryParam.includeParentFilter = true
        }
        if (this.$route.query.view) {
          queryParam.view = this.$helpers.cloneObject(this.$route.query.view)
        }
        filters = { ...filters, ...lookupFilter }
        if (!isEmpty(filters)) {
          this.$router.replace({
            query: {
              ...this.$route.query,
              search: JSON.stringify(filters),
              includeParentFilter: true,
              page: 1,
            },
          })
        }
      }
      this.filterApplied = true
      this.showFilterCondition = false
      if (!isEmpty(this.$refs['dropdown'])) {
        this.$refs.dropdown.blur()
      }
    },
    applyFilter() {
      let filters = {}
      for (let fieldName in this.configs.data) {
        // if (fieldName === 'rule') {
        //   this.configs.data.rule.push({
        //     moduleName: 'alarmRaeding',
        //   })
        // }
        let field = this.configs.data[fieldName]
        let key = field.key || fieldName
        if (
          (field.operatorId === 1 || field.operatorId === 2 || field.value) &&
          (field.operatorId === 1 ||
            field.operatorId === 2 ||
            !Array.isArray(field.value) ||
            field.value.length)
        ) {
          if (field.customdate) {
            this.setCustomDateFilters(filters, fieldName)
          } else {
            let filterObj = {
              operatorId:
                field.operatorId ||
                (spaceFields.includes(fieldName)
                  ? 38
                  : fieldName === 'subject' ||
                    (this.config.data[this.fieldName].operator &&
                      this.config.data[this.fieldName].operator === 'STRING') ||
                    fieldName === 'name' ||
                    fieldName === 'title'
                  ? 5
                  : fieldName === 'sourceType' ||
                    (this.config.data[fieldName].operator &&
                      this.config.data[fieldName].operator === 'NUMBER')
                  ? 9
                  : fieldName === 'isAcknowledged' || fieldName === 'connected'
                  ? 15
                  : 36),
            }
            if (field.operatorId === 1 || field.operatorId === 2) {
              if (
                fieldName === 'subject' ||
                fieldName === 'name' ||
                fieldName === 'title'
              ) {
                field.value = ''
              } else if (
                this.config.data[this.fieldName].operator &&
                this.config.data[this.fieldName].operator === 'STRING'
              ) {
                field.value = ''
              } else {
                field.value = []
              }
            }
            if (fieldName === 'assignedTo') {
              let users = [],
                groups = []
              field.value.forEach(user => {
                if (user.indexOf('_group') !== -1) {
                  groups.push(user.substring(0, user.indexOf('_group')))
                } else {
                  users.push(user)
                }
              })
              if (users.length) {
                filterObj.value = users
              }
              if (groups.length) {
                filterObj.value = groups
                key =
                  key === 'assignedToId'
                    ? 'assignmentGroupId'
                    : 'assignmentGroup'
              }
              filters[key] = filterObj
            } else {
              filterObj.value = !Array.isArray(field.value)
                ? [field.value]
                : field.value.map(val => val + '')
              if (spaceFields.includes(fieldName) || fieldName === 'asset') {
                key = field.key || 'resource'
                if (!filters[key]) {
                  filters[key] = []
                }
                filters[key].push(filterObj)
              } else {
                filters[key] = filterObj
              }
            }
          }
        }
      }
      if (Object.keys(filters).length > '0') {
        this.filtersApplies = filters
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    reset(event, resetValueOnly) {
      this.filterApplied = false
      for (let field in this.config.data) {
        let data = this.config.data[field]
        data.value = data.single ? '' : []
        delete data.operatorId
      }
      // setting height back to normal for select tags
      let elem = document.getElementById('new-view-filter')
      if (elem) {
        elem.querySelectorAll('.el-input__inner').forEach(node => {
          node.style.removeProperty('height')
        })
      }
      if (!resetValueOnly && this.appliedFilters) {
        let querry = {}
        if (this.$route.query.mapview) {
          querry.mapview = true
        }
        this.$router.push({
          path: this.$route.path,
          query: querry,
        })
      }
    },
    addcolumn(element, index, allColumns) {
      this.selectedColumns.push(element)
      allColumns.splice(index, 1)
    },
    removeColumn(element, index, selectedColumns) {
      this.availableColumns.push(element)
      selectedColumns.splice(index, 1)
    },
    saveAsNewView() {
      let self = this
      if (!this.newViewName) {
        return
      }
      if (
        !this.config.disableColumnCustomization &&
        !this.selectedColumns.length
      ) {
        return
      }
      this.applySharing()
      let fields = null
      if (!this.config.disableColumnCustomization) {
        let columns = [...this.fixedSelectedCol, ...this.selectedColumns]
        fields = columns.map(col => ({
          fieldId: col.id,
          columnDisplayName: col.label,
          fieldName: col.key || '',
        }))
      }
      if (this.loadViews) {
        let a = {
          moduleName: this.moduleName,
          view: {
            displayName: this.newViewName,
            fields,
            includeParentCriteria: true,
            filtersJson: JSON.stringify(this.appliedFilters),
            viewSharing: this.viewSharing,
          },
          parentView: this.currentView,
        }
        self.$http.post('/view/add', a).then(function(response) {
          self.showSaveDialog = false
          if (typeof response.data === 'object') {
            self.resetValues()
            self.$router.push({
              path: self.config.path + response.data.name,
              query: {},
            })
            self.$nextTick(() => {
              self.$router.push({
                path: self.config.path + response.data.name,
                query: {},
              })
            })
            self.loadView()
            self.$message({
              message: 'View created successfully!',
              type: 'success',
            })
          } else {
            self.$message({
              message: 'View creation failed!',
              type: 'error',
            })
          }
        })
      } else {
        this.saveNewView({
          moduleName: this.moduleName,
          view: {
            displayName: this.newViewName,
            fields,
            includeParentCriteria: true,
            filtersJson: JSON.stringify(this.appliedFilters),
            viewSharing: this.viewSharing,
          },
          parentView: this.currentView,
        })
          .then(function(data) {
            self.showSaveDialog = false
            self.resetValues()
            self.$router.push({
              path: self.config.path + data.name,
              query: {},
            })
            self.$nextTick(() => {
              self.$router.push({
                path: self.config.path + data.name,
                query: {},
              })
            })
            self.$message({
              message: 'View created successfully!',
              type: 'success',
            })
          })
          .catch(() => {
            self.$message({
              message: 'View creation failed!',
              type: 'error',
            })
          })
      }
    },
    loadView() {
      this.$emit('loadView')
    },
    applySharing: function() {
      let self = this
      this.viewSharing = []
      if (self.shareTo === 1) {
        this.viewSharing.push({
          type: 1,
          userId: self.getCurrentUser().ouid,
        })
      } else if (self.shareTo === 3) {
        if (self.sharedUsers.length > 0) {
          this.viewSharing.push({
            type: 1,
            userId: self.getCurrentUser().ouid,
          })
          for (let i = 0; i < self.sharedUsers.length; i++) {
            if (self.sharedUsers[i] !== self.getCurrentUser().ouid) {
              this.viewSharing.push({
                type: 1,
                userId: self.sharedUsers[i],
              })
            }
          }
        }
        if (self.sharedRoles.length > 0) {
          for (let i = 0; i < self.sharedRoles.length; i++) {
            this.viewSharing.push({
              type: 2,
              roleId: self.sharedRoles[i],
            })
          }
        }
        if (self.sharedGroups.length > 0) {
          for (let i = 0; i < self.sharedGroups.length; i++) {
            this.viewSharing.push({
              type: 3,
              groupId: self.sharedGroups[i],
            })
          }
        }
      }
    },
    onFilterChanged(value) {
      this.apply()
      if (this.filterApplied) {
        this.filterApplied = false
      }
    },
    isDateOperatorWithValue(operatorId) {
      // TODO get value from date operator
      return [39, 40, 41, 50, 59, 60, 61].includes(operatorId)
    },
    async loadPickList(moduleName, field) {
      if (field.options && Object.keys(field.options).length) {
        return
      }
      this.picklistOptions = {}

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(field, 'options', options)
      }
    },
    setCustomDateFilters(filters, fieldName) {
      let constructDateFilter = val => {
        val = val.split('_')
        let operatorId = val.length === 1 ? parseInt(val[0]) : parseInt(val[1])
        let filter = {
          operatorId: operatorId,
        }
        if (operatorId === 20) {
          let dateRange = this.config.data[fieldName].dateRange
          if (!dateRange || !dateRange.length) {
            return false
          }
          filter.value = this.config.data[fieldName].dateRange.map(
            date => date + ''
          )
        } else if (this.isDateOperatorWithValue(operatorId)) {
          filter.value = [val[0]]
        }
        return filter
      }

      let filterVal = this.config.data[fieldName].value
      let key = this.config.data[fieldName].key || fieldName
      if (Array.isArray(filterVal)) {
        filters[key] = []
        filterVal.forEach(fval => {
          let filter = constructDateFilter(fval)
          if (filter) {
            filters[key].push(filter)
          }
        })
      } else {
        let filter = constructDateFilter(filterVal)
        if (filter) {
          filters[key] = filter
        }
      }
    },
    formatOptions(field, list) {
      let data = this.config.data
      if (data[field] && list) {
        list.forEach(val => {
          this.$set(data[field].options, val.id, val.name)
        })
      }
    },
    associateResource(resource, field) {
      this.$set(field, 'chooserVisibility', false)
      field.value = resource.resourceList
        ? resource.resourceList.map(res => res.id)
        : []
      this.onFilterChanged()
      this.apply()
    },
    setFieldValue(filterVal, field, fieldKey) {
      if (filterVal) {
        if (fieldKey === 'assignmentGroup' || fieldKey === 'assignedTo') {
          filterVal =
            fieldKey === 'assignmentGroup'
              ? filterVal.filter(id => id !== '-100').map(id => id + '_group')
              : filterVal
          if (field.value && !field.single) {
            field.value = [...field.value, ...filterVal]
          } else {
            field.value = filterVal
          }
        } else {
          field.value = filterVal
        }
      }
    },
    setOptions() {
      let data = this.config.data
      let userOptions = {}
      this.users.forEach(user => {
        userOptions[user.id] = user.name
      })
      if (data.assignedTo) {
        data.assignedTo.options = userOptions
        if (!isEmpty(this.groups)) {
          this.groups.forEach(group => {
            data.assignedTo.options[group.id + '_group'] = group.name
          })
        }
      }
      if (data.executedBy) {
        data.executedBy.options = userOptions
        if (!isEmpty(this.groups)) {
          this.groups.forEach(group => {
            data.executedBy.options[group.id + '_group'] = group.name
          })
        }
      }
      if (data.issuedTo) {
        data.issuedTo.options = userOptions
      }
      if (data.issuedBy) {
        data.issuedBy.options = userOptions
      }
      if (data.requestedBy) {
        data.requestedBy.options = userOptions
      }
      if (data.registeredBy) {
        data.registeredBy.options = userOptions
      }
      if (data.requestedFor) {
        data.requestedFor.options = userOptions
      }
      if (data.host) {
        data.host.options = userOptions
      }
      if (data.inviteHost) {
        data.inviteHost.options = userOptions
      }

      // Adding this since Main field is lookup
      if (this.moduleName === 'item' && data.itemType) {
        this.loadPickList('itemTypes', data.itemType)
      }
      if (this.moduleName === 'tool' && data.toolType) {
        this.loadPickList('toolTypes', data.toolType)
      }

      if (data.priority) {
        if (!isEmpty(this.ticketpriority)) {
          this.ticketpriority.forEach(prior => {
            data.priority.options[prior.id] = prior.displayName
          })
        }
      }
      if (data.siteId) {
        this.siteList &&
          this.siteList.forEach(sit => {
            data.siteId.options[sit.id] = sit.name
          })
      }
      if (data.severity) {
        if (!isEmpty(this.alarmseverity)) {
          this.alarmseverity.forEach(severity => {
            data.severity.options[severity.id] = severity.severity
            if (data.previousSeverity) {
              data.previousSeverity.options[severity.id] = severity.severity
            }
          })
        }
      }
      if (data.acknowledgedBy) {
        data.acknowledgedBy.options = userOptions
      }
      if (data.clearedBy) {
        data.clearedBy.options = userOptions
      }
      if (data.severity) {
        if (!isEmpty(this.alarmseverity)) {
          this.alarmseverity.forEach(severity => {
            data.severity.options[severity.id] = severity.severity
          })
        }
      }
      if (data.ticketType) {
        if (!isEmpty(this.tickettype)) {
          data.ticketType.options = this.tickettype
        }
      }
      if (data.frequency) {
        data.frequency.options = Object.assign(
          {},
          {
            0: 'Once',
          },
          this.$constants.FACILIO_FREQUENCY
        )
      }
      if (data.faultType) {
        if (this.moduleMeta && this.moduleMeta.fields != null) {
          let faultFields = this.moduleMeta.fields.filter(
            d => d.name === 'faultType'
          )
          data.faultType.options = faultFields[0].enumMap
        }
      }
      this.formatOptions('category', this.ticketcategory)
      this.formatOptions('readingAlarmCategory', this.readingAlarmCategory)
      this.formatOptions('assetCategory', this.assetcategory)
      this.formatOptions('assetDepartment', this.assetdepartment)
      this.formatOptions('assetType', this.assettype)
      this.formatOptions('inventoryCategory', this.inventoryCategory)
    },
    editViews() {
      let data = {
        moduleName: this.config.moduleName,
        view: {
          name: this.viewDetail.name,
          id: this.viewDetail.id,
        },
      }
      let finalFilteredJsonWithParentFilters = {}
      if (this.filtersApplies) {
        finalFilteredJsonWithParentFilters = {
          ...this.filtersApplies,
          ...this.appliedFilters,
        }
      } else {
        finalFilteredJsonWithParentFilters = this.appliedFilters
      }
      data.view.filtersJson = JSON.stringify(finalFilteredJsonWithParentFilters)

      this.editView(data)
        .then(data => {
          this.showSaveDialog = false
          this.resetValues()
          this.$nextTick(() => {
            this.$router.push({
              path: this.config.path + data.view.name,
              query: {},
            })
          })
          this.$message.success('View Edited successfully!')
        })
        .catch(error => {
          this.$message.error(error.message)
        })
    },
    queryHandler() {
      this.query = null
      this.reset()
    },
    resetValues() {
      this.filterApplied = false
      this.allColumns = this.$helpers.cloneObject(this.allFields)
      this.selectedColumns = []
      this.newViewName = ''
      this.sharedGroups = []
      this.sharedUsers = []
      this.sharedRoles = []
      this.viewSharing = []
      if (!isEmpty(this.formModel)) {
        for (let key in this.formModel) {
          this.formModel[key] = {
            id: '',
            label: '',
          }
        }
      }
    },
    getLookupValue(field, filter, fieldName) {
      // TODO needs to check more operators and support multiple lookups
      // TODO needs to get operatorid from metainfo
      let name = Object.keys(filter.value)[0]
      let lookupFilter = filter.value[name]

      let operatorId = lookupFilter.operatorId
      let valuesToCheck = lookupFilter.value // The values to check whether to insert or not based on operator
      let optionsToSelect = [] // Options that needs to be pre-selected. (The values which will be returned)
      let idsArr = Object.keys(this.config.data[fieldName].options) // All the option values for the field

      if (name === 'typeCode' && fieldName === 'status') {
        valuesToCheck = ['Closed']
        if (lookupFilter.value[0] === '1') {
          operatorId = 4
        }
      }

      idsArr.forEach(id => {
        // TODO check more operator ids
        if (valuesToCheck.includes(this.config.data[fieldName].options[id])) {
          if (operatorId !== 4) {
            optionsToSelect.push(id)
          }
        } else if (operatorId === 4) {
          optionsToSelect.push(id)
        }
      })
      return optionsToSelect
    },
    getValuesFromFilter(field, filter, fieldName) {
      let value
      // TODO temporary...needs to improve
      if (filter.operatorId === 35) {
        value = this.getLookupValue(field, filter, fieldName)
      } else if (filter.operatorId === 15 || field.operatorId === 15) {
        value = filter.value && filter.value[0] === 'true' ? 'true' : 'false'
      } else if (
        field.customdate &&
        (!filter.value || !filter.value.length) &&
        field.options[filter.operatorId]
      ) {
        value = field.single ? filter.operatorId + '' : [filter.operatorId + '']
      } else if (filter.value) {
        if (field.type === 'date' && filter.operatorId === 20) {
          value = '20'
          field.dateRange = filter.value
        } else if (this.isDateOperatorWithValue(filter.operatorId)) {
          value = filter.value[0] + '_' + filter.operatorId
          value = field.single ? value : [value]
        } else if (filter.operatorId === 10 || field.operatorId === 10) {
          let options = this.config.data[fieldName].options
          let idsArr = Object.keys(options)
          value = []
          idsArr.forEach(id => {
            if (!filter.value.includes(id)) {
              value.push(id)
            }
          })
        } else {
          value = field.single ? filter.value[0] : filter.value
        }
      }
      return value
    },
    init() {
      let filters = Object.assign({}, this.viewFilters)
      let self = this
      let fieldKeyMap = {}
      for (let fieldName in this.configs.data) {
        if (
          this.configs.data.hasOwnProperty(fieldName) &&
          this.configs.data[fieldName].key
        ) {
          fieldKeyMap[this.configs.data[fieldName].key] = fieldName
        }
      }

      for (let fieldKey in filters) {
        if (
          filters.hasOwnProperty(fieldKey) ||
          fieldKey === 'assignmentGroup'
        ) {
          let filter = filters[fieldKey]
          let fieldName =
            fieldKey === 'assignmentGroup' ? 'assignedTo' : fieldKey
          let field
          let isResourceField = false
          if (fieldName === 'resource' || fieldName === 'resourceId') {
            field =
              this.configs.data.space || this.configs.data.asset
                ? {
                    parent: 'resource',
                  }
                : null
            isResourceField = true
          } else {
            field =
              this.configs.data[fieldName] ||
              this.configs.data[fieldKeyMap[fieldName]]
          }
          if (field) {
            let filterVal
            if (Array.isArray(filter)) {
              filterVal = []
              filter.forEach(fval => {
                if (isResourceField) {
                  filterVal = []
                  field =
                    fval.operatorId === 38
                      ? this.configs.data.space
                      : this.configs.data.asset
                }

                let val = self.getValuesFromFilter(
                  field,
                  fval,
                  fieldKeyMap[fieldName] || fieldName
                )
                if (Array.isArray(val)) {
                  filterVal = [...filterVal, ...val]
                } else {
                  field.operatorId === 15
                    ? (filterVal = val)
                    : filterVal.push(val)
                }

                if (isResourceField) {
                  self.setFieldValue(filterVal, field, fieldKey)
                }
              })
            } else {
              if (isResourceField) {
                field =
                  filter.operatorId === 38
                    ? this.config.data.space
                    : this.config.data.asset
                field =
                  filter.operatorId === 38
                    ? this.configs.data.space
                    : this.configs.data.asset
              }
              filterVal = self.getValuesFromFilter(
                field,
                filter,
                fieldKeyMap[fieldName] || fieldName
              )
            }

            if (!Array.isArray(filter) || !isResourceField) {
              self.setFieldValue(filterVal, field, fieldKey)
            }
          }
        }
      }
      this.applyFilter()
    },
    checkColumns() {
      this.selectedColumns = []
      for (let j in this.allColumns) {
        let k = false
        for (let i in this.viewColumns) {
          if (this.allColumns[j].label === this.viewColumns[i].displayName) {
            this.selectedColumns.push(this.allColumns[j])
            k = true
          }
        }
        if (!k && !this.nonSelectableColumns.includes(this.allColumns[j].key)) {
          this.availableColumns.push(this.allColumns[j])
        }
      }
    },
    close() {
      if (!this.showingLookupWizard) {
        this.showSearchfilter = false
        this.$emit('hideSearch')
      }
    },
  },
}
</script>
<style>
.search-icon-set {
  height: 80%;
}

.filter-search-clear {
  position: absolute;
  right: 63px;
  color: #8ca1ad;
  font-size: 15px;
  font-weight: 500;
  padding: 2px;
  top: 10px;
  cursor: pointer;
}

.filter-search-clear:hover {
  background: #d0d9e2;
  color: #8ca1ad !important;
  transition: 0.2s all;
  padding: 2px;
  border-radius: 50px;
  -webkit-transition: 0.2s all;
}

.rearrange-box-body .dragArea {
  min-height: 100px;
}

.new-search-date-filter {
  border-radius: 0;
  margin-top: 0;
}

.f-search-popover .el-select-dropdown__list {
  padding-bottom: 70px;
}

.filter-space-search-icon3 {
  position: absolute;
  top: 0;
  right: 47px;
  font-size: 16px;
  color: #333;
}

.down-arrow-remove .el-icon-arrow-up:before,
.el-icon-arrow-up:after {
  display: none;
}

.down-arrow-remove .el-input.is-disabled .el-input__inner {
  cursor: pointer !important;
  opacity: 1 !important;
}

.f-search-popover .el-select-dropdown__item {
  /* fontweight: 400; */
  font-weight: 400;
}

.search-border-left .el-input__inner {
  border-left: 1px solid #d8dce5 !important;
}
.fc-search-bar-select {
  border-left: none !important;
}
</style>
