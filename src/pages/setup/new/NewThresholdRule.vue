<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <el-form :model="model" :label-position="'top'" ref="ruleForm">
      <div id="newthresholdrule">
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isnew
                  ? isDependant
                    ? 'New Threshold Dependant Rule'
                    : 'New Alarm Rule'
                  : 'Edit Alarm Rule'
              }}
            </div>
          </div>
        </div>

        <div class="new-body-modal">
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt">Enter Rule Name</p>
              <el-form-item prop="name">
                <el-input
                  v-model="model.readingRule.name"
                  required
                  autofocus="true"
                  type="text"
                  placeholder="Enter the name"
                  class="fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt ">Description</p>
              <el-form-item class="textarea-height-set">
                <el-input
                  v-model="model.readingRule.description"
                  required
                  type="textarea"
                  :placeholder="$t('common._common.enter_desc')"
                  :autosize="{ minRows: 3, maxRows: 4 }"
                  resize="none"
                  class="fc-input-full-border-select2 width100"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt ">Type</p>
              <el-form-item>
                <el-select
                  class="width100 fc-input-full-border-select2"
                  v-model="resourceType"
                  @change="resetIfSelected"
                  :disabled="!this.isnew"
                >
                  <el-option label="Asset" value="asset"></el-option>
                  <el-option label="Space" value="space"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="resource-row" :gutter="20">
            <el-col v-if="isAsset" :span="12">
              <div class="fc-input-label-txt">Asset Category</div>
              <el-form-item prop="assetcategory" style="margin-top: 10px;">
                <el-select
                  class="width100 fc-input-full-border-select2"
                  v-model="model.readingRule.assetCategoryId"
                  @change="loadThresholdFields()"
                  :disabled="!isnew || isDependant"
                  filterable
                  default-first-option
                  placeholder="Choose Category"
                >
                  <el-option
                    v-for="(category, index) in assetCategoryList"
                    :key="index"
                    :label="category.name"
                    :value="category.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <div class="fc-input-label-txt">
                {{ isAsset ? 'Asset(s)' : 'Space' }}
              </div>
              <div class="add f-element resource-list">
                <div v-if="isMultiResource">
                  <el-form-item prop="assets">
                    <el-select
                      class="multi mT10 width100 fc-input-full-border-select2"
                      multiple
                      v-model="dummyValue"
                      disabled
                    >
                      <el-option :label="resourceLabel" :value="1"></el-option>
                    </el-select>
                  </el-form-item>
                  <i
                    @click="
                      !isResourceDisabled ? (chooserVisibility = true) : null
                    "
                    :disabled="isResourceDisabled"
                    slot="suffix"
                    class="el-input__icon el-icon-search threshold-icon-search"
                  ></i>
                </div>
                <el-input
                  v-else
                  @change="
                    resourceQuery = selectedResource.name
                    chooserVisibility = true
                  "
                  :disabled="isResourceDisabled"
                  v-model="selectedResource.name"
                  class="mT10 width100"
                  type="text"
                  :placeholder="$t('common._common.to_search_type')"
                >
                  <i
                    @click="
                      !isResourceDisabled ? (chooserVisibility = true) : null
                    "
                    :disabled="isResourceDisabled"
                    slot="suffix"
                    class="el-input__icon el-icon-search threshold-icon-search"
                  ></i>
                </el-input>
              </div>
            </el-col>
          </el-row>

          <el-row class="mT20" :gutter="20">
            <el-col :span="12">
              <div class="fc-input-label-txt">Threshold Metric</div>
              <div class="add">
                <el-form-item prop="threshold">
                  <el-select
                    v-model="selectedThresholdFieldName"
                    :disabled="!isnew || isDependant"
                    filterable
                    default-first-option
                    class="form-item width100 fc-input-full-border-select2"
                    placeholder="Choose Field"
                  >
                    <el-option
                      v-for="field in thresholdFields"
                      :key="field.name"
                      :label="field.displayName"
                      :value="field.name"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </div>
            </el-col>
          </el-row>

          <el-row
            align="middle"
            :gutter="20"
            v-if="isDependant || (!isnew && rule.parentRuleId !== -1)"
          >
            <el-col :span="12">
              <div class="fc-input-label-txt">Parent Rule Name</div>
              <div class="add">
                <el-input
                  :disabled="true"
                  v-model="parentRuleName"
                  type="text"
                  class="fc-input-txt fc-input-full-border2 width100"
                ></el-input>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="fc-input-label-txt">Execute On</div>
              <div class="add">
                <el-select
                  v-model="model.readingRule.onSuccess"
                  class="form-item width100 fc-input-full-border-select2"
                >
                  <el-option
                    :key="true"
                    label="Condition Match"
                    :value="true"
                  ></el-option>
                  <el-option
                    :key="false"
                    label="Condition Not Match"
                    :value="false"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>

          <el-row align="middle" class="mT20" :gutter="20">
            <el-col :span="12">
              <div class="fc-input-label-txt">Condition Type</div>
              <div class="add">
                <el-select
                  v-model="model.readingRule.thresholdType"
                  class="form-item width100 fc-input-full-border-select2"
                  placeholder="Choose Asset"
                >
                  <el-option
                    :key="1"
                    label="Simple Condition"
                    :value="1"
                  ></el-option>
                  <el-option
                    :key="2"
                    label="Aggregation"
                    :value="2"
                  ></el-option>
                  <el-option :key="3" label="Baseline" :value="3"></el-option>
                  <el-option :key="4" label="Flapping" :value="4"></el-option>
                  <el-option :key="6" label="Function" :value="6"></el-option>
                  <el-option
                    :key="5"
                    label="Complex Condition"
                    :value="5"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>

          <el-container>
            <el-main class="p0">
              <div
                v-if="
                  model.readingRule.thresholdType === 2 ||
                    model.readingRule.thresholdType === 3
                "
              >
                <div class="fc-sub-title-container">
                  <div class="fc-modal-sub-title">CONDITIONS</div>
                </div>

                <el-row
                  v-if="model.readingRule.thresholdType === 3"
                  align="middle"
                  :gutter="20"
                >
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Baseline</div>
                    <div class="add">
                      <el-select
                        v-model="model.readingRule.baselineId"
                        class="form-item width100 fc-input-full-border-select2"
                        placeholder="Choose Baseline"
                      >
                        <el-option
                          v-for="baseline in baselines"
                          :key="baseline.id"
                          :label="baseline.name"
                          :value="baseline.id"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                </el-row>
                <el-row align="middle" :gutter="20">
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Aggregation</div>
                    <div class="add">
                      <el-select
                        v-model="model.readingRule.aggregation"
                        class="form-item width100 fc-input-full-border-select2"
                        placeholder="Choose Aggregation"
                      >
                        <el-option
                          key="sum"
                          label="Sum"
                          value="sum"
                        ></el-option>
                        <el-option
                          key="avg"
                          label="Average"
                          value="avg"
                        ></el-option>
                        <el-option
                          key="min"
                          label="Minimum"
                          value="min"
                        ></el-option>
                        <el-option
                          key="max"
                          label="Maximum"
                          value="max"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <duration
                      v-model="model.readingRule.dateRange"
                      label="Date Range"
                      labelClass="fc-input-label-txt"
                      format="h"
                      size="l"
                      :minHour="true"
                    >
                      <el-col :span="3" class="pT5" slot="prefix"
                        ><span class="textcolor">Last</span></el-col
                      >
                    </duration>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Operator</div>
                    <div class="add">
                      <el-select
                        v-model="model.readingRule.operatorId"
                        class="form-item width100 fc-input-full-border-select2"
                        placeholder="Choose Operator"
                      >
                        <el-option
                          :key="9"
                          label="Equals"
                          :value="9"
                        ></el-option>
                        <el-option
                          :key="10"
                          label="Not Equals"
                          :value="10"
                        ></el-option>
                        <el-option
                          :key="13"
                          label="Greater Than"
                          :value="13"
                        ></el-option>
                        <el-option
                          :key="14"
                          label="Greater Than Equals"
                          :value="14"
                        ></el-option>
                        <el-option
                          :key="11"
                          label="Less Than"
                          :value="11"
                        ></el-option>
                        <el-option
                          :key="12"
                          label="Less Than Equals"
                          :value="12"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                  <el-col
                    v-if="model.readingRule.thresholdType === 3"
                    :span="12"
                  >
                    <div class="fc-input-label-txt">Percentage</div>
                    <div class="add">
                      <el-input
                        v-model="model.readingRule.percentage"
                        type="number"
                        placeholder="0%"
                        class="width100 fc-input-full-border2"
                      ></el-input>
                    </div>
                  </el-col>
                  <el-col
                    v-if="model.readingRule.thresholdType === 2"
                    :span="12"
                  >
                    <div class="fc-input-label-txt">Value</div>
                    <div class="add">
                      <el-input
                        v-model="model.readingRule.percentage"
                        placeholder="Enter Value"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div v-else-if="model.readingRule.thresholdType === 4">
                <div class="fc-sub-title-container">
                  <div class="fc-modal-sub-title">CONDITIONS</div>
                </div>
                <el-row align="middle" :gutter="20" v-if="!isBooleanMetric">
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Lower Trigger</div>
                    <div class="add">
                      <el-input
                        v-model="model.readingRule.minFlapValue"
                        placeholder="Lower Trigger Value"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Upper Trigger</div>
                    <div class="add">
                      <el-input
                        v-model="model.readingRule.maxFlapValue"
                        placeholder="Upper Trigger Value"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-col>
                </el-row>
                <el-row align="middle" class="mT20" :gutter="20">
                  <el-col :span="12">
                    <div class="fc-input-label-txt">
                      Flapping Frequency (n times)
                    </div>
                    <div class="add">
                      <el-input
                        v-model="model.readingRule.flapFrequency"
                        placeholder="Enter Flapping Frequency"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-input-label-txt">
                      Flapping Interval (period in minutes)
                    </div>
                    <div class="add">
                      <el-input
                        v-model="model.readingRule.flapInterval"
                        placeholder="Enter Flapping Interval"
                        class="fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div v-else-if="model.readingRule.thresholdType === 1">
                <div class="fc-sub-title-container">
                  <div class="fc-modal-sub-title">CONDITIONS</div>
                </div>

                <el-row align="middle" :gutter="20">
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Operator</div>
                    <div class="add">
                      <el-select
                        v-model="model.readingRule.operatorId"
                        class="form-item fc-input-full-border-select2 width100"
                        placeholder="Choose Operator"
                      >
                        <el-option
                          :key="9"
                          label="Equals"
                          :value="9"
                        ></el-option>
                        <el-option
                          :key="10"
                          label="Not Equals"
                          :value="10"
                        ></el-option>
                        <el-option
                          :key="13"
                          label="Greater Than"
                          :value="13"
                        ></el-option>
                        <el-option
                          :key="14"
                          label="Greater Than Equals"
                          :value="14"
                        ></el-option>
                        <el-option
                          :key="11"
                          label="Less Than"
                          :value="11"
                        ></el-option>
                        <el-option
                          :key="12"
                          label="Less Than Equals"
                          :value="12"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Value</div>
                    <div class="add">
                      <el-popover
                        v-if="isNumberMetric"
                        v-model="showPrevPopover"
                        :width="
                          $refs.previnput && $refs.previnput.$el
                            ? $refs.previnput.$el.clientWidth
                            : ''
                        "
                        trigger="click"
                        popper-class="prev-popper"
                      >
                        <div class="prev-value pointer" @click="setPrevValue">
                          {{
                            (this.selectedMetric
                              ? this.selectedMetric.displayName + "'s "
                              : '') + 'Previous Value'
                          }}
                        </div>
                        <div slot="reference">
                          <el-input
                            ref="previnput"
                            @keydown.native="showPrevPopover = false"
                            v-model="model.readingRule.percentage"
                            placeholder="Enter Value"
                            class="width100 fc-input-full-border-select2"
                          >
                            <i
                              slot="suffix"
                              :class="[
                                'prev-icon el-select__caret el-input__icon el-icon-arrow-down',
                                { active: showPrevPopover },
                              ]"
                              style="line-height: 16px;"
                            ></i>
                          </el-input>
                        </div>
                      </el-popover>
                      <el-input
                        v-else
                        v-model="model.readingRule.percentage"
                        placeholder="Enter Value"
                        class="width100"
                      >
                      </el-input>
                    </div>
                  </el-col>
                </el-row>
                <el-row align="middle" :gutter="20" class="mT20">
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Occurrences</div>
                    <div class="add">
                      <el-input
                        v-model="occurrences"
                        placeholder="Enter Occurrences"
                        class="width100 fc-input-full-border2 width100"
                      ></el-input>
                    </div>
                  </el-col>
                </el-row>
                <el-row v-if="occurrences > 0" align="middle" :gutter="20">
                  <el-col :span="12">
                    <div class="textcolor">
                      <el-radio
                        v-model="consecutiveoroverperiod"
                        :label="1"
                        class="fc-radio-btn"
                        >Over Period</el-radio
                      >
                      <el-radio
                        v-model="consecutiveoroverperiod"
                        :label="2"
                        class="fc-radio-btn"
                        >Consecutive</el-radio
                      >
                    </div>
                  </el-col>
                </el-row>
                <el-row
                  v-if="occurrences > 0 && consecutiveoroverperiod === 1"
                  align="middle"
                  :gutter="20"
                  class="mT20"
                >
                  <el-col :span="12">
                    <div class="fc-input-label-txt">Period</div>
                    <div class="add">
                      <el-select
                        v-model="overperiod"
                        style=""
                        class="form-item fc-input-full-border-select2 width100"
                        placeholder="Choose Period"
                      >
                        <el-option
                          :key="1"
                          label="Last 1 Hour"
                          :value="1"
                        ></el-option>
                        <el-option
                          :key="3"
                          label="Last 3 Hour"
                          :value="3"
                        ></el-option>
                        <el-option
                          :key="6"
                          label="Last 6 Hour"
                          :value="6"
                        ></el-option>
                        <el-option
                          :key="12"
                          label="Last 12 Hour"
                          :value="12"
                        ></el-option>
                      </el-select>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div v-else-if="model.readingRule.thresholdType === 5">
                <div class="fc-sub-title-container">
                  <div class="fc-modal-sub-title">CONDITIONS</div>
                </div>
                <!-- <el-row align="middle" style="margin:0px;padding-top:20px;" :gutter="20">
            <el-col :span="12" style="padding-right: 35px;padding-left: 0px;">
              <div class="textcolor">Date Range</div>
              <div class="add">
                <el-select v-model="model.readingRule.dateRange" style="width:100%" class="form-item " placeholder="Choose Date Range">
                  <el-option :key="1" label="Last 1 Hour" :value="1"></el-option>
                  <el-option :key="3" label="Last 3 Hour" :value="3"></el-option>
                  <el-option :key="6" label="Last 6 Hour" :value="6"></el-option>
                  <el-option :key="12" label="Last 12 Hour" :value="12"></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row> -->
                <f-formula-builder
                  class="mT20"
                  title=""
                  module="thresholdrule"
                  v-model="model.readingRule.workflow"
                  :metric="selectedMetric"
                  :assetCategory="{ id: model.readingRule.assetCategoryId }"
                ></f-formula-builder>
              </div>
              <div v-else-if="model.readingRule.thresholdType === 6">
                <div class="fc-sub-title-container">
                  <div class="fc-modal-sub-title">CONDITIONS</div>
                </div>
                <el-row align="middle" class="mT20">
                  <function
                    v-model="model.readingRule.workflow"
                    :metric="selectedMetric"
                    :resource="selectedResource"
                  ></function>
                </el-row>
              </div>
              <div class="fc-sub-title-container">
                <div class="fc-modal-sub-title">ALARM DETAILS</div>
              </div>
              <el-row align="middle" class="mT20" :gutter="20">
                <el-col :span="12">
                  <div class="fc-input-label-txt">Message</div>
                  <div class="add">
                    <el-form-item prop="message">
                      <el-input
                        v-model="newalarm.message"
                        class="width100 fc-input-full-border-select2"
                        type="text"
                        placeholder="Enter Alarm Message"
                      ></el-input>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="fc-input-label-txt">Severity</div>
                  <div class="add">
                    <el-select
                      v-model="newalarm.severity"
                      class="form-item width100 fc-input-full-border-select2"
                      placeholder="Enter Severity"
                    >
                      <el-option
                        key="Critical"
                        label="Critical"
                        value="Critical"
                      ></el-option>
                      <el-option
                        key="Major"
                        label="Major"
                        value="Major"
                      ></el-option>
                      <el-option
                        key="Minor"
                        label="Minor"
                        value="Minor"
                      ></el-option>
                      <el-option
                        key="Clear"
                        label="Clear"
                        value="Clear"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>
              <el-row align="middle" :gutter="20">
                <el-col :span="24">
                  <div class="fc-input-label-txt">Problem</div>
                  <div class="add">
                    <el-input
                      v-model="newalarm.problem"
                      class="width100 mT10 fc-input-full-border-select2"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row align="middle" class="mT20" :gutter="20">
                <el-col :span="24">
                  <div class="fc-input-label-txt">Possible Causes</div>
                  <div class="add textarea-height-set mT10">
                    <el-input
                      v-model="newalarm.possibleCauses"
                      placeholder=""
                      class="width100 fc-input-full-border-select2 width100"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                      resize="none"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row align="middle" class="mT20" :gutter="20">
                <el-col :span="24">
                  <div class="fc-input-label-txt">Recommendations</div>
                  <div class="add textarea-height-set mT10">
                    <el-input
                      v-model="newalarm.recommendation"
                      placeholder=""
                      class="width100 fc-input-full-border-select2"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                      resize="none"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>

              <div class="fc-sub-title-container">
                <div class="fc-modal-sub-title">ADDITIONAL INFORMATION</div>
              </div>

              <div class="fc-rule-actions">
                <el-card
                  class="fc-rule-action box-card"
                  v-for="(action, index) in model.actions"
                  :key="index"
                >
                  <el-row :gutter="10" align="middle">
                    <el-col :md="24" :lg="24">
                      <field-matcher
                        ref="field-matcher"
                        v-model="action.templateJson.fieldMatcher"
                      ></field-matcher>
                    </el-col>
                  </el-row>
                </el-card>
              </div>
            </el-main>
          </el-container>
        </div>
        <space-asset-chooser
          v-if="!isMultiResource"
          @associate="associateResource"
          :visibility.sync="chooserVisibility"
          :initialValues="{}"
          :query="resourceQuery"
          :showAsset="false"
          picktype="space"
        ></space-asset-chooser>
        <space-asset-multi-chooser
          v-else
          @associate="associateResource"
          :visibility.sync="chooserVisibility"
          :initialValues="resourceData"
          :query="resourceQuery"
          :showAsset="true"
          :disable="true"
        ></space-asset-multi-chooser>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="save('ruleForm')"
          :loading="saving"
          >{{ saving ? 'Saving...' : 'SAVE' }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import FieldMatcher from '@/FieldMatcher2'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import ErrorBanner from '@/ErrorBanner'
import Duration from '@/FDuration'
import Function from '@/workflow/FFormulaFunction'
export default {
  props: [
    'visible',
    'isnew',
    'visibility',
    'rule',
    'isDependant',
    'parentRuleName',
  ],
  data() {
    return {
      consecutiveoroverperiod: 1,
      occurrences: null,
      overperiod: null,
      show: false,
      moduleMeta: null,
      callmail: false,
      callsms: false,
      module: 'alarm',
      saving: false,
      showPrevPopover: false,
      error: false,
      model: {
        readingRule: {
          module: '',
          name: '',
          description: '',
          executionOrder: 0,
          resourceId: -1,
          readingFieldId: -1,
          assetCategoryId: null,
          event: {
            moduleId: null,
            activityType: 1,
          },
          criteria: null,
          // ruleType: 2,
          baselineId: null,
          aggregation: null,
          dateRange: 1,
          operatorId: null,
          percentage: null,
          thresholdType: 1,
          onSuccess: false,
          parentRuleId: null,
        },
        actions: [
          {
            actionType: 6,
            templateJson: {
              fieldMatcher: [],
            },
          },
        ],
      },
      selectedResource: {
        name: '',
      },
      resourceType: 'asset',
      selectedResourceList: [],
      isIncludeResource: false,
      dummyValue: [1],
      assetCategory: '',
      assetList: [],
      readings: [],
      newalarm: {
        message: '',
        severity: 'Minor',
        problem: null,
        possibleCauses: null,
        recommendation: null,
      },
      duration: -1,
      baselines: [],
      expressionString: '',
      selectedThresholdFieldName: '',
      thresholdFields: [],
      actionChanged: false,
      chooserVisibility: false,
      resourceQuery: null,
      errorText: '',
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  components: {
    FieldMatcher,
    FFormulaBuilder,
    SpaceAssetChooser,
    SpaceAssetMultiChooser,
    ErrorBanner,
    Duration,
    Function,
  },
  computed: {
    isAsset() {
      return this.resourceType === 'asset'
    },
    isMultiResource() {
      return (
        this.isAsset &&
        (!this.rule ||
          !Object.keys(this.rule).length ||
          this.rule.assetCategoryId > 0)
      )
    },
    showDialog() {
      return this.visible
    },
    selectedMetric() {
      return this.selectedThresholdFieldName && this.thresholdFields.length
        ? this.thresholdFields.find(
            field => field.name === this.selectedThresholdFieldName
          )
        : ''
    },
    isBooleanMetric() {
      return (
        this.selectedMetric &&
        (this.selectedMetric.dataTypeEnum === 'BOOLEAN' ||
          this.selectedMetric.dataTypeEnum._name === 'BOOLEAN')
      )
    },
    isNumberMetric() {
      return (
        this.selectedMetric &&
        (['NUMBER', 'DECIMAL'].includes(this.selectedMetric.dataTypeEnum) ||
          ['NUMBER', 'DECIMAL'].includes(
            this.selectedMetric.dataTypeEnum._name
          ))
      )
    },
    assetCategoryList() {
      return this.$store.state.assetCategory
    },
    resourceData() {
      return {
        assetCategory: this.model.readingRule.assetCategoryId,
        isIncludeResource: this.isIncludeResource,
        selectedResources: this.selectedResourceList.map(resource => ({
          id: resource && typeof resource === 'object' ? resource.id : resource,
        })),
      }
    },
    isResourceDisabled() {
      return (
        !this.isnew ||
        this.isDependant ||
        (this.isAsset &&
          (!this.model.readingRule.assetCategoryId ||
            this.model.readingRule.assetCategoryId < 0))
      )
    },
    categoryName() {
      if (
        this.model.readingRule.assetCategoryId > 0 &&
        this.assetCategoryList
      ) {
        let category = this.assetCategoryList.find(
          category => category.id === this.model.readingRule.assetCategoryId
        )
        if (category) {
          return category.name
        }
      }
      return ''
    },
    resourceLabel() {
      this.dummyValue = [1]
      if (this.model.readingRule.assetCategoryId > 0) {
        let message
        let selectedCount = this.selectedResourceList.length
        if (selectedCount) {
          let includeMsg = this.isIncludeResource ? 'included' : 'excluded'
          if (selectedCount === 1) {
            return this.selectedResourceList[0].name
          }
          message =
            selectedCount +
            ' ' +
            this.categoryName +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + this.categoryName + 's selected'
        }
        return message
      } else if (this.selectedResource.id > 0) {
        return this.selectedResource.name
      } else {
        this.dummyValue = []
      }
    },
  },
  watch: {
    selectedResource(newVal, oldVal) {
      if (newVal.id !== oldVal.id) {
        if (oldVal.id) {
          this.selectedThresholdFieldName = ''
          this.model.readingRule.readingFieldId = null
        }
        this.loadThresholdFields()
      } else if (!newVal || !newVal.id) {
        this.thresholdFields = []
        this.selectedThresholdFieldName = ''
        this.model.readingRule.readingFieldId = null
      }
    },
    selectedThresholdFieldName: function() {
      let field = this.thresholdFields.find(
        field => field.name === this.selectedThresholdFieldName
      )
      if (field) {
        this.model.readingRule.event.moduleId = field.moduleId
        this.model.readingRule.readingFieldId = field.id
        this.model.readingRule.percentage =
          this.model.readingRule.percentage === '$' + '{previousValue}' &&
          !this.isNumberMetric
            ? ''
            : this.model.readingRule.percentage
      }
    },
  },
  mounted() {
    this.loadBaselines()
    this.loadRuleData()
  },
  methods: {
    resetIfSelected() {
      this.resourceLabel = null
      console.log('resetIfSelected')
    },
    setExpressionString(expression) {
      this.model.readingRule.workflow = expression
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    loadRuleData() {
      if (!this.isnew || this.isDependant) {
        if (this.rule.assetCategoryId > 0) {
          this.resourceType = 'asset'
          this.model.readingRule.assetCategoryId = this.rule.assetCategoryId
          if (
            this.rule.includedResources &&
            this.rule.includedResources.length
          ) {
            this.isIncludeResource = true
            this.selectedResourceList = this.rule.includedResources.map(id => ({
              id: id,
            }))
          } else if (this.rule.excludedResources) {
            this.selectedResourceList = this.rule.excludedResources.map(id => ({
              id: id,
            }))
          }
          this.loadAssets()
        } else {
          this.selectedResource = this.rule.matchedResources[
            Object.keys(this.rule.matchedResources)[0]
          ]
          if (this.selectedResource.resourceType === 2) {
            this.resourceType = 'asset'
            this.loadAssets()
          } else {
            this.resourceType = 'space'
          }
        }

        if (!this.isDependant) {
          this.model.readingRule.thresholdType = this.rule.thresholdType
          this.model.readingRule.name = this.rule.name
          this.model.readingRule.description = this.rule.description
        }
        this.model.readingRule.resourceId = this.rule.resourceId
        this.model.readingRule.readingFieldId = this.rule.readingFieldId
        this.selectedThresholdFieldName = this.rule.readingField.name
        this.model.readingRule.event.moduleId = this.rule.readingField.moduleId

        if (this.isDependant) {
          this.model.readingRule.onSuccess = false
          this.model.readingRule.parentRuleId = this.rule.id
        } else {
          if (
            this.model.readingRule.thresholdType === 3 ||
            this.model.readingRule.thresholdType === 2
          ) {
            if (this.model.readingRule.thresholdType === 3) {
              this.model.readingRule.baselineId = this.rule.baselineId
            }
            this.model.readingRule.aggregation = this.rule.aggregation
            this.model.readingRule.dateRange = this.rule.dateRange
            this.model.readingRule.operatorId = this.rule.operatorId
            this.model.readingRule.percentage = this.rule.percentage
          } else if (this.model.readingRule.thresholdType === 4) {
            this.model.readingRule.minFlapValue =
              this.rule.minFlapValue > -1 ? this.rule.minFlapValue : null
            this.model.readingRule.maxFlapValue =
              this.rule.maxFlapValue > -1 ? this.rule.maxFlapValue : null
            this.model.readingRule.flapInterval =
              this.rule.flapInterval / (60 * 1000)
            this.model.readingRule.flapFrequency =
              this.rule.flapFrequency > -1 ? this.rule.flapFrequency : null
          } else if (this.model.readingRule.thresholdType === 1) {
            this.model.readingRule.operatorId = this.rule.operatorId

            if (this.rule.workflow) {
              this.occurrences = this.rule.percentage
              this.model.readingRule.percentage = this.rule.workflow.expressions[0].aggregateCondition[0].value
              if (this.rule.workflow.expressions[0].criteria.conditions['2']) {
                this.consecutiveoroverperiod = 1
                this.overperiod = parseInt(
                  this.rule.workflow.expressions[0].criteria.conditions['2']
                    .value
                )
              } else {
                this.consecutiveoroverperiod = 2
              }
            } else {
              this.model.readingRule.percentage = this.rule.percentage
            }
          } else if (
            this.model.readingRule.thresholdType === 5 ||
            this.model.readingRule.thresholdType === 6
          ) {
            this.model.readingRule.dateRange =
              this.rule.dateRange && this.rule.dateRange !== -1
                ? this.rule.dateRange
                : ''
            this.rule.workflow.occurences = {
              occurences: this.rule.occurences,
              overPeriod: this.rule.overPeriod,
              consecutive: this.rule.consecutive,
            }
            this.model.readingRule.workflow = this.rule.workflow
          }

          if (this.rule.actions) {
            let content = this.rule.actions[0].template.originalTemplate
            this.$helpers.copy(this.newalarm, content)
            let keys = Object.keys(this.newalarm)
            Object.keys(content)
              .filter(key => !keys.includes(key) && key !== 'alarmType')
              .forEach(key => {
                this.model.actions[0].templateJson.fieldMatcher.push({
                  field: key,
                  value: content[key],
                })
              })
          }

          let unwatch = this.$watch(
            'newalarm',
            function() {
              this.actionChanged = true
              unwatch()
            },
            { deep: true }
          )
        }
      } else {
        this.model.readingRule.thresholdType = 1
      }
    },
    loadThresholdFields() {
      if (this.isMultiResource) {
        if (this.model.readingRule.assetCategoryId > 0) {
          this.thresholdFields = []
          this.$util
            .loadAssetReadingFields(
              this.selectedResourceList.length === 1
                ? this.selectedResourceList[0].id
                : -1,
              this.model.readingRule.assetCategoryId
            )
            .then(fields => {
              this.thresholdFields = fields
            })
        }
      } else if (this.selectedResource && this.selectedResource.id > 0) {
        this.thresholdFields = []
        this.$util.loadReadingFields(this.selectedResource).then(fields => {
          this.thresholdFields = fields
        })
      }
    },
    loadBaselines() {
      let self = this
      self.$http.get('/baseline/all').then(function(response) {
        if (response.status === 200) {
          self.baselines = response.data
        }
      })
    },
    thresholdfield(field) {
      this.model.readingRule.readingFieldId = field.id
      this.model.readingRule.event.moduleId = field.moduleId
    },
    getAsset(id) {
      return this.assetList.find(at => at.id === id)
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (!rule.name) {
        this.errorText = 'Please enter Name'
        this.error = true
      } else if (
        (!rule.assetCategoryId || rule.assetCategoryId < 0) &&
        (!rule.readingFieldId || rule.readingFieldId < 0) &&
        this.resourceType === 'asset'
      ) {
        this.errorText = 'Please select Asset Category'
        this.error = true
      } else if (!rule.readingFieldId || rule.readingFieldId < 0) {
        this.errorText = 'Please select Threshold Metric'
        this.error = true
      } else if (!this.newalarm.message) {
        this.errorText = 'Please enter alarm message'
        this.error = true
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    save(ruleForm) {
      let self = this
      // self.saving = true
      this.validation(this.model.readingRule)
      if (this.error) {
        return
      }
      let data = this.$helpers.cloneObject(this.model)

      if (!self.isDependant) {
        delete data.readingRule.parentRuleId
        delete data.readingRule.onSuccess
      }
      /* this.$refs[ruleForm].validate((valid) => {
        if (valid) {
          console.log('submited')
        }
        else {
          this.error = true
          console.log('error submit!!')
          return false
        }
      }) */
      if (this.selectedResourceList) {
        let fieldName = this.isIncludeResource
          ? 'includedResources'
          : 'excludedResources'
        data.readingRule[fieldName] = this.selectedResourceList.map(
          resource => resource.id
        )
      }
      if (!data.readingRule.assetCategoryId) {
        delete data.readingRule.assetCategoryId
      }

      if (data.readingRule.module === '') {
        data.readingRule.module = this.module
        data.readingRule.resourceId = self.selectedResource.id
      }
      if (data.readingRule.thresholdType === 1) {
        delete data.readingRule.baselineId
        delete data.readingRule.dateRange
        delete data.readingRule.flapInterval
        delete data.readingRule.flapFrequency
        delete data.readingRule.minFlapValue
        delete data.readingRule.maxFlapValue

        let isExpressionValue =
          data.readingRule.percentage === '$' + '{previousValue}'

        if (!self.occurrences) {
          delete data.readingRule.workflow
          delete data.readingRule.aggregation
          data.readingRule.criteria = {
            pattern: '(1)',
            conditions: {
              1: {
                fieldName: self.selectedThresholdFieldName,
                columnName: self.thresholdFields.find(
                  field => field.name === self.selectedThresholdFieldName
                ).columnName,
                operatorId: data.readingRule.operatorId,
                sequence: '1',
                value: isExpressionValue
                  ? 'previousValue'
                  : data.readingRule.percentage,
                isExpressionValue: isExpressionValue,
              },
            },
          }
        } else {
          delete data.readingRule.criteria
          data.readingRule.workflow = {
            expressions: [],
            resultEvaluator: '',
            parameters: [{ name: 'resourceId', typeString: 'Number' }],
          }
          data.readingRule.workflow.expressions.push({
            name: 'a',
            aggregateString: 'count',
            fieldName: self.selectedThresholdFieldName,
            moduleName: this.thresholdFields.find(
              field => field.name === self.selectedThresholdFieldName
            ).module.name,
            aggregateCondition: [],
            criteria: {
              pattern: '(1)',
              conditions: {
                1: {
                  fieldName: 'parentId',
                  operatorId: 9,
                  sequence: '1',
                  value: this.isMultiResource
                    ? '${resourceId' + '}'
                    : this.selectedResource.id,
                },
              },
            },
          })
          data.readingRule.workflow.expressions[0].aggregateCondition.push({
            fieldName: self.selectedThresholdFieldName,
            operatorId: data.readingRule.operatorId,
            sequence: '1',
            value: data.readingRule.percentage,
          })
          if (self.consecutiveoroverperiod === 2) {
            data.readingRule.workflow.expressions[0].aggregateString = 'count'
            data.readingRule.workflow.expressions[0].orderByFieldName = 'ttime'
            data.readingRule.workflow.expressions[0].sortBy = 'desc'
            data.readingRule.workflow.expressions[0].limit = self.occurrences
          } else {
            data.readingRule.workflow.expressions[0].criteria.pattern =
              '(1 and 2)'
            data.readingRule.workflow.expressions[0].criteria.conditions[
              '2'
            ] = {
              fieldName: 'ttime',
              operatorId: 42,
              sequence: '2',
              value: self.overperiod + ',${ttime}', // eslint-disable-line no-template-curly-in-string
            }
            this.addParam('ttime', 'Number', data)
          }
          data.readingRule.percentage = self.occurrences
          data.readingRule.workflow.resultEvaluator = 'a==' + self.occurrences
        }
      } else if (data.readingRule.thresholdType === 2) {
        delete data.readingRule.criteria
        delete data.readingRule.baselineId
        delete data.readingRule.flapInterval
        delete data.readingRule.flapFrequency
        delete data.readingRule.minFlapValue
        delete data.readingRule.maxFlapValue
        if (this.isnew || this.checkWorkFlowChange()) {
          data.readingRule.workflow = {
            expressions: [],
            resultEvaluator: '',
            parameters: [{ name: 'resourceId', typeString: 'Number' }],
          }
          data.readingRule.workflow.expressions.push({
            name: 'a',
            aggregateString: data.readingRule.aggregation,
            fieldName: this.thresholdFields.find(
              field => field.name === self.selectedThresholdFieldName
            ).name,
            moduleName: this.thresholdFields.find(
              field => field.name === self.selectedThresholdFieldName
            ).module.name,
            criteria: {
              pattern: '(1 and 2)',
              conditions: {
                1: {
                  fieldName: 'parentId',
                  operatorId: 9,
                  sequence: '1',
                  value: this.isMultiResource
                    ? '${resourceId' + '}'
                    : this.selectedResource.id,
                },
                2: {
                  fieldName: 'ttime',
                  operatorId: 42,
                  sequence: '2',
                  value: data.readingRule.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
                },
              },
            },
            aggregateCondition: [],
          })
          self.addParam('ttime', 'Number', data)
          if (data.readingRule.aggregation === 'ccount') {
            data.readingRule.workflow.expressions[0].aggregateString = 'count'
            data.readingRule.workflow.expressions[0].orderByFieldName = 'ttime'
            data.readingRule.workflow.expressions[0].sortBy = 'desc'
            data.readingRule.workflow.expressions[0].limit =
              data.readingRule.percentage
          }
          data.readingRule.workflow.resultEvaluator =
            'a' +
            this.getOperatorSymbol(data.readingRule.operatorId) +
            data.readingRule.percentage
        }
      } else if (data.readingRule.thresholdType === 3) {
        delete data.readingRule.criteria
        delete data.readingRule.flapInterval
        delete data.readingRule.flapFrequency
        delete data.readingRule.minFlapValue
        delete data.readingRule.maxFlapValue
        if (
          this.isnew ||
          data.readingRule.baselineId !== this.rule.baselineId ||
          this.checkWorkFlowChange()
        ) {
          data.readingRule.workflow = {
            expressions: [],
            resultEvaluator: '',
            parameters: [{ name: 'resourceId', typeString: 'Number' }],
          }
          let baselineExp = {
            name: 'a',
            aggregateString: data.readingRule.aggregation,
            fieldName: this.thresholdFields.find(
              field => field.name === self.selectedThresholdFieldName
            ).name,
            moduleName: this.thresholdFields.find(
              field => field.name === self.selectedThresholdFieldName
            ).module.name,
            criteria: {
              pattern: '(1 and 2)',
              conditions: {
                1: {
                  fieldName: 'parentId',
                  operatorId: 9,
                  sequence: '1',
                  value: this.isMultiResource
                    ? '${resourceId' + '}'
                    : this.selectedResource.id,
                },
                2: {
                  fieldName: 'ttime',
                  operatorId: 42,
                  sequence: '2',
                  value: data.readingRule.dateRange + ',${ttime}', // eslint-disable-line no-template-curly-in-string
                },
              },
            },
            conditionSeqVsBaselineId: { 2: data.readingRule.baselineId },
          }
          self.addParam('ttime', 'Number', data)
          data.readingRule.workflow.expressions.push(baselineExp)

          let baselineExpB = self.$helpers.cloneObject(baselineExp)
          baselineExpB.name = 'b'
          baselineExpB.conditionSeqVsBaselineId = null
          data.readingRule.workflow.expressions.push(baselineExpB)

          data.readingRule.workflow.resultEvaluator =
            '((b-a)/a)*100' +
            this.getOperatorSymbol(data.readingRule.operatorId) +
            data.readingRule.percentage
        }
      } else if (data.readingRule.thresholdType === 4) {
        delete data.readingRule.criteria
        delete data.readingRule.workflow
        delete data.readingRule.baselineId
        delete data.readingRule.aggregation
        delete data.readingRule.dateRange
        delete data.readingRule.operatorId
        delete data.readingRule.percentage
        data.readingRule.flapInterval =
          data.readingRule.flapInterval * (60 * 1000)
      } else if (
        data.readingRule.thresholdType === 5 ||
        data.readingRule.thresholdType === 6
      ) {
        delete data.readingRule.criteria
        delete data.readingRule.flapInterval
        delete data.readingRule.flapFrequency
        delete data.readingRule.minFlapValue
        delete data.readingRule.maxFlapValue
        delete data.readingRule.baselineId
        delete data.readingRule.aggregation
        delete data.readingRule.dateRange
        delete data.readingRule.operatorId
        delete data.readingRule.percentage

        if (data.readingRule.workflow.occurences) {
          Object.assign(data.readingRule, data.readingRule.workflow.occurences)
        }
      }

      if (!this.isnew) {
        data.readingRule = this.$helpers.compareObject(
          data.readingRule,
          this.rule
        )
        data.readingRule.id = this.rule.id

        if (
          this.actionChanged ||
          data.actions[0].templateJson.fieldMatcher.length
        ) {
          this.setActions(data)
        } else {
          delete data.actions
        }
      } else {
        this.setActions(data)
      }
      this.$util.addOrUpdateRule('alarm', data, !this.isnew).then(rule => {
        this.$emit('saved', true)
        this.closeDialog()
      })
    },
    addParam(name, type, data) {
      if (!data.readingRule.workflow.parameters) {
        data.readingRule.workflow.parameters = []
      }
      let params = data.readingRule.workflow.parameters
      if (!params.find(param => param.name === name)) {
        params.push({ name: name, typeString: type || 'String' })
      }
    },
    setActions(data) {
      data.actions[0].templateJson.fieldMatcher = data.actions[0].templateJson.fieldMatcher.filter(
        field => field.field !== ''
      )
      for (let key in this.newalarm) {
        if (this.newalarm[key] !== null) {
          data.actions[0].templateJson.fieldMatcher.push({
            field: key,
            value: this.newalarm[key],
          })
        }
      }
    },
    checkWorkFlowChange() {
      return (
        this.model.readingRule.aggregation !== this.rule.aggregation ||
        this.selectedResource.id !== this.rule.resourceId ||
        this.model.readingRule.dateRange !== this.rule.dateRange ||
        this.model.readingRule.operatorId !== this.rule.operatorId ||
        this.model.readingRule.percentage !== this.rule.percentage ||
        this.model.readingRule.readingFieldId !== this.rule.readingFieldId
      )
    },
    getOperatorSymbol: function(operatorId) {
      if (operatorId === 9) {
        return '='
      } else if (operatorId === 10) {
        return '!='
      } else if (operatorId === 13) {
        return '>'
      } else if (operatorId === 14) {
        return '>='
      } else if (operatorId === 11) {
        return '<'
      } else if (operatorId === 12) {
        return '<='
      }
      return ''
    },
    cancel() {
      this.$emit('canceled', true)
    },
    loadAssets() {
      let self = this
      let url = '/asset/all'
      this.$http.get(url).then(function(response) {
        if (response.data) {
          // self.assetList = response.data.assets
          if (!self.isnew || self.isDependant) {
            if (!self.isMultiResource) {
              self.selectedResource = response.data.assets.find(
                asset => asset.id === self.selectedResource.id
              )
            } else {
              self.selectedResourceList = response.data.assets.filter(asset =>
                self.selectedResourceList.some(
                  resource => resource.id === asset.id
                )
              )
            }
            self.loadThresholdFields()
          }
        }
      })
    },
    associateResource(selectedObj) {
      if (this.isMultiResource) {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.selectedResourceList = selectedObj.resourceList
          this.isIncludeResource = selectedObj.isInclude
          this.loadThresholdFields()
        }
      } else {
        this.selectedResource = selectedObj
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
    showCreateNewDialog() {
      if (this.$route.query.create) {
        return true
      }
      return false
    },
    backto() {
      this.$router.push({ path: 'thresholdrules' })
    },
    setPrevValue() {
      this.model.readingRule.percentage = '$' + '{previousValue}'
      this.showPrevPopover = false
    },
  },
}
</script>
<style>
#newthresholdrule .ruletitle {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.8px;
  color: #ef4f8f;
}
#newthresholdrule .header.el-input .el-input__inner,
.header.el-textarea .el-textarea__inner {
  resize: none;
}
#newthresholdrule .primarybutton.el-button {
  width: 125px;
  height: 40px;
  border-radius: 4px;
  background-color: #39b2c2;
  border-radius: 4px;
  float: right;
}
.boxheader {
  background-color: #ffffff;
  padding-left: 34px;
  height: 70px !important ;
}
.discription-textbox {
  font-size: 12px;
  text-align: left;
  color: #6b7e91;
  letter-spacing: 0.4px;
}
.indent {
  padding-top: 35px;
  padding-left: 35px;
  padding-bottom: 150px;
}
.line {
  background-color: #ffffff;
  box-shadow: 0 2px 7px 0 rgba(191, 191, 191, 0.5);
}
.font {
  font-size: 14px;
  font-weight: 500;
  text-align: left;
  color: #39b2c2;
  padding-top: 15px;
  padding-bottom: 15px;
}
.thinline {
  border: solid 1px #dff4f6;
}
.el-button.is-round {
  border-radius: 20px;
  padding: 7px 10px;
}
.el-button--primary {
  color: #fff;
  background-color: #39b2c2;
  border-color: #39b2c2;
  letter-spacing: 0.7px;
}
.el-radio-button__orig-radio:checked + .el-radio-button__inner {
  color: #70678f;
  background-color: #f3f3f9;
  border-color: #e2e8ee;
  box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5);
}
.el-button.is-round {
  padding: 6px 5px;
}
.Alarm-title {
  margin-top: -13px;
  width: 30%;
  font-size: 18px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #000000;
}
.edittext {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.Alarm-Created {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.Action-text {
  font-size: 13px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #757575;
}
.mailbody {
  font-size: 13px;
  letter-spacing: 0.6px;
  text-align: left;
  color: #757575;
  overflow: hidden;
  text-overflow: ellipsis;
}
.inline {
  /* display:inline; */
  /* padding-left:8%; */
  overflow: hidden;
  text-overflow: ellipsis;
}
.boldtitle {
  font-size: 11px;
  color: #000000;
  font-weight: 500;
  letter-spacing: 0.9px;
  padding-top: 45px;
}
.childinline {
  display: inline;
  padding-left: 56px;
}
.dropdownbutton {
  border-color: #39b2c2;
  font-size: 11px;
  color: #39b2c2;
  font-weight: bold;
  padding: 5px;
  padding-bottom: 15px;
}
.team-down-icon {
  width: 10px;
  height: 10px;
  color: #e1e4eb;
  bottom: 9px;
  right: 41%;
}
.el-main {
  padding: 0px 20px;
  max-width: 100%;
}

.prev-popper {
  padding: 0px;
}
.prev-icon.active {
  transform: rotate(-180deg);
}
.prev-value:hover {
  background-color: #f5f7fa;
}
#newthresholdrule .el-input.is-disabled .el-input__inner {
  background-color: transparent !important;
}

.resource-row .el-input__inner {
  min-height: 40px;
}

.resource-list .multi .el-select .el-tag__close.el-icon-close,
.resource-list .multi .el-input__suffix {
  display: none;
}
.threshold-icon-search {
  line-height: 0px !important;
  font-size: 16px !important;
  cursor: pointer;
  position: absolute;
  top: 5px;
  right: 15px;
}
</style>
