<template>
  <div id="fbFormulaBuilder">
    <div v-if="title || title !== ''" class="fbTitle">
      {{ title ? title : $t('setup.formulaBuilder.formula_builder') }}
    </div>
    <div
      :class="renderInLeft ? 'text-left' : 'text-right'"
      class="f-element"
      v-if="!hideModeChange && !showOnlyVarible && !isV2Script"
    >
      <el-radio-group v-model="uiMode" @change="handleModeChange">
        <el-radio class="fc-radio-btn" :label="WorkflowUIMode.COMPLEX">
          {{ $t('setup.formulaBuilder.change_expression') }}
        </el-radio>
        <el-radio
          v-if="!hideCodeView"
          class="fc-radio-btn"
          :label="WorkflowUIMode.XML"
        >
          {{ $t('setup.formulaBuilder.code_view') }}
        </el-radio>
      </el-radio-group>
    </div>
    <div v-if="uiMode !== WorkflowUIMode.XML">
      <div v-if="!showOnlyVarible" class="fbContainer">
        <div class="fbExpressions">
          <template style="float:left;" v-for="(token, index) in result">
            <template v-if="token.type === 'variable'">
              <!-- <el-popover
                popper-class="fbVariablePopper"
                v-model="variables[token.value].popover"
                placement="bottom"
                trigger="click" :key="index"> -->
              <f-popover
                placement="right"
                popper-class="fbVariablePopper"
                v-model="variables[token.value].popover"
                :title="$t('setup.formulaBuilder.set_variable')"
                trigger="click"
                :key="'variable' + index"
                @save="setVariable(variables[token.value])"
                width="290"
              >
                <template slot="content">
                  <div v-if="module !== 'virtual'">
                    <el-row align="middle" style="margin:0px;padding-top:20px;">
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            @change="
                              loadRelatedModule(variables[token.value]),
                                (variables[token.value].aggregation = null)
                            "
                            v-model="variables[token.value].module"
                            style="width:100%"
                            class="fc-input-full-border-select2"
                            :placeholder="
                              $t('setup.formulaBuilder.choose_module')
                            "
                          >
                            <el-option
                              key="asset"
                              label="Asset"
                              value="asset"
                            ></el-option>
                            <el-option
                              key="space"
                              label="Space"
                              value="space"
                            ></el-option>
                            <el-option
                              key="assetreading"
                              label="Asset Reading"
                              value="assetreading"
                            ></el-option>
                            <el-option
                              key="spacereading"
                              label="Space Reading"
                              value="spacereading"
                            ></el-option>
                            <el-option
                              v-if="module === 'workorder'"
                              key="workorder"
                              label="Work Order"
                              value="workorder"
                            ></el-option>
                            <el-option
                              key="function"
                              label="Function"
                              value="function"
                            ></el-option>
                            <el-option
                              key="constant"
                              label="Constant"
                              value="constant"
                            ></el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <!-- this.assetCategory.id -->
                    <el-row
                      v-if="
                        variables[token.value].module === 'assetreading' ||
                          variables[token.value].module === 'asset'
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            @change="
                              loadRelatedModule(variables[token.value]),
                                (variables[token.value].readingFieldId = null)
                            "
                            filterable
                            v-model="variables[token.value].assetId"
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Asset"
                          >
                            <el-option
                              v-for="asset in assetList"
                              :key="'asset' + asset.id"
                              :label="asset.name"
                              :value="asset.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        variables[token.value].module === 'spacereading' ||
                          variables[token.value].module === 'space'
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            @change="loadRelatedModule(variables[token.value])"
                            filterable
                            v-model="variables[token.value].spaceId"
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Space"
                          >
                            <el-option
                              v-for="space in spaceList"
                              :key="'space' + space.id"
                              :label="space.name"
                              :value="space.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="variables[token.value].module === 'workorder'"
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            @change="loadRelatedModule(variables[token.value])"
                            filterable
                            v-model="variables[token.value].workorderFieldName"
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Field"
                          >
                            <el-option
                              v-for="field in workorderFields"
                              :key="'wofields' + field.id"
                              :label="field.displayName"
                              :value="field.name"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="variables[token.value].module === 'space'"
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].spaceFieldId"
                            filterable
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Field"
                          >
                            <el-option
                              v-for="field in spaceFields"
                              :key="'spacefields' + field.id"
                              :label="field.displayName"
                              :value="field.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        variables[token.value].module === 'spacereading' &&
                          variables[token.value].spaceId
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].spaceReadingFieldId"
                            filterable
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Reading"
                            @change="
                              variables[token.value].aggrCondOperator = null
                            "
                          >
                            <el-option
                              v-for="field in getSpaceReadings(
                                variables[token.value].spaceId
                              )"
                              :key="field.id"
                              :label="field.displayName"
                              :value="field.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="variables[token.value].module === 'asset'"
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].assetFieldId"
                            filterable
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Field"
                          >
                            <el-option
                              v-for="field in assetFields"
                              :key="'assetfields' + field.id"
                              :label="field.displayName"
                              :value="field.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        variables[token.value].module === 'assetreading' &&
                          variables[token.value].assetId
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].readingFieldId"
                            filterable
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Reading"
                            @change="
                              variables[token.value].aggrCondOperator = null
                            "
                          >
                            <el-option
                              v-for="field in getAssetSpecificReadings(
                                variables[token.value].assetId
                              )"
                              :key="'assetreadings' + field.id"
                              :label="field.displayName"
                              :value="field.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="variables[token.value].module === 'constant'"
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-popover
                            v-if="module === 'thresholdrule'"
                            v-model="variables[token.value].showPrevPopover"
                            :width="
                              $refs['previnput' + token.value] &&
                              $refs['previnput' + token.value][0] &&
                              $refs['previnput' + token.value][0].$el
                                ? $refs['previnput' + token.value][0].$el
                                    .clientWidth
                                : ''
                            "
                            trigger="click"
                            popper-class="prev-popper"
                          >
                            <div
                              class="prev-value"
                              style="cursor:pointer;padding: 10px;"
                              @click="setPrevValue(token.value, index)"
                            >
                              {{
                                (metric ? metric.displayName + "'s " : '') +
                                  'Previous Value'
                              }}
                            </div>
                            <div slot="reference">
                              <el-input
                                :ref="'previnput' + token.value"
                                @keydown.native="
                                  variables[token.value].showPrevPopover = false
                                "
                                v-model="variables[token.value].constant"
                                class="fc-input-full-border-select2 width100"
                                type="text"
                                placeholder="Enter Constant Value"
                              >
                                <i
                                  slot="suffix"
                                  :class="[
                                    'prev-icon el-select__caret el-input__icon el-icon-arrow-down',
                                    {
                                      active:
                                        variables[token.value].showPrevPopover,
                                    },
                                  ]"
                                  style="line-height: 16px;"
                                ></i>
                              </el-input>
                            </div>
                          </el-popover>
                          <el-input
                            class="fc-input-full-border2"
                            v-else
                            v-model="variables[token.value].constant"
                            style="width:100%"
                            type="text"
                            placeholder="Enter Constant Value"
                          ></el-input>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        variables[token.value].module === 'assetreading' ||
                          variables[token.value].module === 'spacereading'
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].aggregation"
                            @change="variables[token.value].dateRange = null"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Aggregation"
                            style="width:100%;"
                          >
                            <el-option
                              v-for="(label, value) in aggregateOptions"
                              :key="'aggregation' + value"
                              :label="label"
                              :value="value"
                            ></el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        variables[token.value].aggregation &&
                          !['lastValue'].includes(
                            variables[token.value].aggregation
                          ) &&
                          variables[token.value].spaceReadingFieldId !==
                            'currentEnpi'
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-checkbox
                            v-model="
                              variables[token.value].hasAggregateCondValue
                            "
                            style="width:100%"
                            class="form-item "
                            >Apply Aggregation Condition</el-checkbox
                          >
                        </div>
                      </el-col>
                    </el-row>
                    <template
                      v-if="variables[token.value].hasAggregateCondValue"
                    >
                      <el-row style="margin:0px;padding-top:20px;">
                        <el-col :span="24">
                          <div class="add">
                            <el-select
                              v-model="variables[token.value].aggrCondOperator"
                              @change="
                                variables[token.value].aggrCondValue = null
                              "
                              style="width:100%"
                              class="form-item fc-input-full-border-select2 width280px"
                              placeholder="Choose Operator"
                            >
                              <el-option
                                v-for="operator in getReadingFieldOperators(
                                  variables[token.value]
                                )"
                                :key="operator.operator"
                                :label="operator.operator | firstUpperCase"
                                :value="parseInt(operator.operatorId)"
                              ></el-option>
                            </el-select>
                          </div>
                        </el-col>
                      </el-row>
                      <el-row
                        style="margin:0px;padding-top:20px;"
                        v-if="
                          !valueNotNeed.includes(
                            variables[token.value].aggrCondOperator
                          )
                        "
                      >
                        <el-col :span="24">
                          <div class="add">
                            <el-select
                              v-model="variables[token.value].aggrCondValue"
                              v-if="
                                variables[token.value].readingDataType ===
                                  'BOOLEAN'
                              "
                              style="width:100%"
                              class="form-item fc-input-full-border-select2"
                            >
                              <el-option label="True" value="true"></el-option>
                              <el-option
                                label="False"
                                value="false"
                              ></el-option>
                            </el-select>
                            <el-input
                              v-else
                              v-model="variables[token.value].aggrCondValue"
                              style="width:100%"
                              class="fc-input-full-border-select2 width280px"
                              type="text"
                              placeholder="Enter Value"
                            ></el-input>
                          </div>
                        </el-col>
                      </el-row>
                    </template>
                    <el-row
                      v-if="
                        module === 'enpi' &&
                          (variables[token.value].module === 'assetreading' ||
                            variables[token.value].module === 'spacereading') &&
                          variables[token.value].aggregation !== 'lastValue'
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-checkbox
                            v-model="variables[token.value].hasBaseline"
                            style="width:100%"
                            class="form-item "
                            >Apply Baseline Condition</el-checkbox
                          >
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        module === 'enpi' &&
                          variables[token.value].hasBaseline &&
                          (variables[token.value].module === 'assetreading' ||
                            variables[token.value].module === 'spacereading')
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].baselineId"
                            style="width:100%"
                            class="form-item fc-input-full-border-select2"
                            placeholder="Choose Baseline"
                          >
                            <el-option
                              v-for="baseline in baselines"
                              :key="'base' + baseline.id"
                              :label="baseline.name"
                              :value="baseline.id"
                            ></el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <el-row
                      v-if="
                        (module === 'thresholdrule' || module === 'impact') &&
                          variables[token.value].aggregation &&
                          variables[token.value].aggregation !== 'lastValue'
                      "
                      align="middle"
                      style="margin:0px;padding-top:20px;"
                    >
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].dateRange"
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Date Range"
                          >
                            <el-option
                              v-for="(label, value) in dateRangeOptions"
                              :key="'dateRangeOptions' + value"
                              :label="label"
                              :value="parseInt(value)"
                            ></el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>

                    <template
                      v-if="variables[token.value].module === 'function'"
                    >
                      <el-row
                        align="middle"
                        style="margin:0px;padding-top:20px;"
                      >
                        <el-col :span="24">
                          <div class="add">
                            <el-select
                              v-model="variables[token.value].nameSpace"
                              filterable
                              style="width:100%"
                              class="form-item fc-input-full-border-select2 width280px"
                              placeholder="Choose Namespace"
                            >
                              <!-- <el-option  v-for="namespace in config.namespaces"  :key="namespace.value" :label="namespace.name"  :value="namespace.name">  </el-option> -->
                              <el-option
                                v-for="(namespace, key) in namespaces"
                                :key="key"
                                :label="namespace.label"
                                :value="key"
                              ></el-option>
                            </el-select>
                          </div>
                        </el-col>
                      </el-row>
                      <el-row
                        v-if="variables[token.value].nameSpace"
                        align="middle"
                        style="margin:0px;padding-top:20px;"
                      >
                        <el-col :span="24">
                          <div class="add">
                            <el-select
                              v-model="variables[token.value].functionName"
                              filterable
                              style="width:100%"
                              class="form-item fc-input-full-border-select2 width280px"
                              placeholder="Choose Function"
                            >
                              <el-option
                                v-for="(func, value) in namespaces[
                                  variables[token.value].nameSpace
                                ].functions"
                                :key="value"
                                :label="func.label"
                                :value="value"
                              ></el-option>
                            </el-select>
                          </div>
                        </el-col>
                      </el-row>
                    </template>
                  </div>
                  <div v-if="module === 'virtual'" style="width:250px;">
                    <el-row align="middle" style="margin:0px;padding-top:20px;">
                      <el-col :span="24">
                        <div class="add">
                          <el-select
                            v-model="variables[token.value].assetId"
                            filterable
                            style="width:100%"
                            class="form-item fc-input-full-border-select2 width280px"
                            placeholder="Choose Meter"
                          >
                            <el-option
                              v-for="meter in meterlist"
                              :key="meter.id"
                              :label="meter.name"
                              :value="meter.id"
                            >
                            </el-option>
                          </el-select>
                        </div>
                      </el-col>
                    </el-row>
                    <!-- <div style="text-align: right;margin: 25px 0px 0px;">
                    <el-button type="primary" size="mini" @click="setVariable(variables[token.value])">Ok</el-button>
                  </div> -->
                  </div>
                </template>
                <el-button
                  slot="reference"
                  :class="[
                    'fbVariable',
                    { focused: variables[token.value].popover },
                  ]"
                >
                  {{ variables[token.value].name }}
                </el-button>
              </f-popover>
              <!-- </el-popover> -->
            </template>
            <template v-if="token.type === 'operator'">
              <el-popover
                popper-class="fbOperatorPopper"
                v-if="index !== result.length - 1"
                placement="top"
                trigger="hover"
                :key="'operator' + index"
              >
                <div class="operator-list p15">
                  <div class="arithmetic">
                    <div @click="selectOperator('+', index)">
                      <img src="~statics/formula/plus.svg" />
                    </div>
                    <div @click="selectOperator('-', index)">
                      <img src="~statics/formula/minus.svg" class="small" />
                    </div>
                    <template v-if="module !== 'virtual'">
                      <div @click="selectOperator('*', index)">
                        <img src="~statics/formula/multiply.svg" />
                      </div>
                      <div @click="selectOperator('/', index)">
                        <img src="~statics/formula/divide.svg" />
                      </div>
                    </template>
                  </div>
                  <div
                    v-if="module === 'thresholdrule' || module === 'impact'"
                    class="relational"
                  >
                    <div @click="selectOperator('==', index)">
                      <img src="~statics/formula/equalto.svg" class="small" />
                    </div>
                    <div @click="selectOperator('!=', index)">
                      <img
                        src="~statics/formula/notequalto.svg"
                        class="small"
                      />
                    </div>
                    <div @click="selectOperator('<', index)">
                      <img src="~statics/formula/less_than.svg" class="small" />
                    </div>
                    <div @click="selectOperator('<=', index)">
                      <img src="~statics/formula/less_than_or_equal_to.svg" />
                    </div>
                    <div @click="selectOperator('>', index)">
                      <img
                        src="~statics/formula/greater_than.svg"
                        class="small"
                      />
                    </div>
                    <div @click="selectOperator('>=', index)">
                      <img
                        src="~statics/formula/greater_than_or_equal_to.svg"
                      />
                    </div>
                  </div>
                  <!-- <div v-if="module === 'thresholdrule' && index + 1 !== result.length && result[index + 1].type==='brace' && result[index + 1].value==='('" class="logical"> -->
                  <div v-if="module === 'thresholdrule'" class="logical">
                    <div @click="selectOperator('&&', index)">
                      <img src="~statics/formula/and.svg" />
                    </div>
                    <div @click="selectOperator('||', index)">
                      <img src="~statics/formula/or.svg" />
                    </div>
                  </div>
                </div>
                <div slot="reference" class="fbOperator">
                  <template v-if="token.value === '+'"
                    ><img src="~statics/formula/plus-white.svg"
                  /></template>
                  <template v-else-if="token.value === '-'"
                    ><img
                      src="~statics/formula/minus-white.svg"
                      style="padding-top:3px"
                  /></template>
                  <template v-else-if="token.value === '*'"
                    ><img src="~statics/formula/multiply-white.svg"
                  /></template>
                  <template v-else-if="token.value === '/'"
                    ><img
                      src="~statics/formula/divide-white.svg"
                      style="padding-top:2px"
                  /></template>
                  <template v-else-if="token.value === '=='"
                    ><img src="~statics/formula/equalto-white.svg"
                  /></template>
                  <template v-else-if="token.value === '!='"
                    ><img src="~statics/formula/notequalto-white.svg"
                  /></template>
                  <template v-else-if="token.value === '<'"
                    ><img
                      src="~statics/formula/less_than-white.svg"
                      style="width:9px"
                  /></template>
                  <template v-else-if="token.value === '<='"
                    ><img src="~statics/formula/le-white.svg" style="width:15px"
                  /></template>
                  <template v-else-if="token.value === '>'"
                    ><img
                      src="~statics/formula/greater_than-white.svg"
                      style="width:9px"
                  /></template>
                  <template v-else-if="token.value === '>='"
                    ><img src="~statics/formula/ge-white.svg" style="width:15px"
                  /></template>
                  <template v-else-if="token.value === '&&'"
                    ><img
                      src="~statics/formula/and-white.svg"
                      style="width:15px"
                  /></template>
                  <template v-else-if="token.value === '||'"
                    ><img src="~statics/formula/or-white.svg"
                  /></template>
                  <div v-else>{{ token.value }}</div>
                </div>
              </el-popover>
            </template>
            <template v-if="token.type === 'brace'">
              <img
                v-if="token.value === '('"
                style="height: 50px"
                src="~statics/icons/open_bracket.svg"
                :key="'brace' + index"
              />
              <div
                v-else
                style="display: flex;align-items: center;"
                :key="'brace' + index"
              >
                <div @click="addExpression(index)" class="fbNewBtn inner">
                  +
                </div>
                <span
                  style="letter-spacing: 2px;font-size: 13px;color: #84ccd5;"
                  >---</span
                >
                <img
                  style="height: 50px;"
                  src="~statics/icons/close_bracket.svg"
                />
              </div>
            </template>
            <span
              v-if="index !== result.length - 1"
              class="flLeft"
              style="letter-spacing: 2px;font-size: 13px;color: #84ccd5;"
              :key="index"
              >---</span
            >
          </template>
          <span style="letter-spacing: 2px;font-size: 13px;color: #84ccd5;"
            >---</span
          >
          <div @click="addExpression()" class="fbNewBtn">+</div>
        </div>

        <!-- Occurence -->
        <div v-if="module === 'thresholdrule'" class="fbOccurence">
          <el-row
            align="middle"
            style="margin:0px;padding-top:20px;"
            :gutter="20"
          >
            <el-col :span="12" style="padding-right: 35px;padding-left:0px;">
              <div class="fc-input-label-txt">Occurrences</div>
              <div class="add">
                <el-select
                  v-model="occurenceType"
                  style="width:100%"
                  class="form-item fc-input-full-border-select2 width280px"
                  placeholder="Choose Occurence Type"
                  @change="evaluateExpression"
                >
                  <el-option label="None" :value="0"></el-option>
                  <el-option
                    label="Occurrence Consecutively"
                    :value="1"
                  ></el-option>
                  <el-option
                    label="Occurrence Over Period"
                    :value="2"
                  ></el-option>
                  <el-option label="Over Period" :value="3"></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row
            v-if="[1, 2].includes(occurenceType)"
            align="middle"
            style="margin:0px;padding-top:20px;"
            :gutter="20"
          >
            <el-col :span="12" style="padding-right: 35px;padding-left:0px;">
              <div class="fc-input-label-txt">Count</div>
              <div class="add">
                <el-input
                  v-model="occurences"
                  type="number"
                  placeholder="Enter Occurrences"
                  class="fc-input-select fc-input-full-border-select2 width100"
                  @change="evaluateExpression"
                ></el-input>
              </div>
            </el-col>
          </el-row>
          <el-row
            v-if="[2, 3].includes(occurenceType)"
            align="middle"
            style="margin:0px;"
            :gutter="20"
          >
            <el-col
              :span="12"
              style="padding-right: 35px;padding-left:0px;padding-top: 20px;"
            >
              <div class="fc-input-label-txt">Period</div>
              <div class="add">
                <el-select
                  v-model="overPeriod"
                  style="width:100%"
                  class="form-item fc-input-full-border-select2 width280px"
                  placeholder="Choose Period"
                  @change="evaluateExpression"
                >
                  <el-option label="Last 20 Minutes" :value="1200"></el-option>
                  <el-option label="Last 1 Hour" :value="3600"></el-option>
                  <el-option label="Last 3 Hour" :value="10800"></el-option>
                  <el-option label="Last 6 Hour" :value="21600"></el-option>
                  <el-option label="Last 12 Hour" :value="7200"></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <el-row
        v-if="uiMode === WorkflowUIMode.COMPLEX && !showOnlyVarible"
        align="middle"
        style="margin:0px;padding-top:20px;"
      >
        <el-col :span="24">
          <div class="add">
            <div class="new-label-text">Expression</div>
            <el-input
              v-model="fbResult"
              @change="evaluateExpression"
              style="width:100%"
              type="text"
              placeholder="Enter Expression"
              class="fc-input-full-border2 pT10"
            ></el-input>
          </div>
        </el-col>
      </el-row>

      <div class="fbVariableLegendContainer">
        <table border="0" cellspacing="0" cellpadding="0">
          <tr
            v-for="(token, index) in result"
            :key="'legend' + index"
            class="fbVariableLegend"
          >
            <template v-if="token.type === 'variable'">
              <td class="fbVariableLegend">
                {{ variables[token.value].name }}
              </td>
              <td class="fbVariableLegendInfo">
                <div style="float:left;">
                  <span v-if="getVariableName(variables[token.value]) !== ''">{{
                    getVariableName(variables[token.value])
                  }}</span>
                  <span style="color: #aaa;" v-else>Variable not set</span>
                </div>
                <div
                  v-if="!showOnlyVarible"
                  @click="removeExpression(index)"
                  style="float: right;"
                  class="fbVariableLegendDelete"
                >
                  <i class="fa fa-trash"></i>
                </div>
                <div style="clear:both;"></div>
              </td>
            </template>
          </tr>
        </table>
      </div>
    </div>
    <div class="pT10" v-else>
      <div class="height150 overflow-y-scroll pB50" v-if="isV2Script">
        <code-mirror
          :codeeditor="true"
          v-model="workflowV2String"
        ></code-mirror>
      </div>
      <textarea
        v-else
        class="formula-xml"
        rows="10"
        @change="evaluateExpression"
        v-model="workflowString"
        placeholder="Write in XML"
      ></textarea>
    </div>
  </div>
</template>
<script>
import FPopover from '@/FPopover'
import { mapState, mapActions, mapGetters } from 'vuex'
import CodeMirror from '@/CodeMirror'
const formatOptions = { indent: 4, newline: '\n' }
const format = require('prettify-xml')
const expRegex = /([a-zA-Z])|([1-9]\d*)|([\\(\\)])|([-+*\\/])|(<=?|>=?|==|!=)|(&&|\|{1,2})/g

let WorkflowUIMode = {
  GUI: 1,
  XML: 2,
  COMPLEX: 3,
}
export default {
  props: [
    'module',
    'value',
    'title',
    'metric',
    'assetCategory',
    'hideCodeView',
    'hideModeChange',
    'restrictInit',
    'setResultAsExpr',
    'renderInLeft',
    'showOnlyVarible',
    'isMv',
  ],
  components: { FPopover, CodeMirror },
  data() {
    return {
      variables: {},
      result: [],
      lastVariable: 65,
      meterlist: [],
      model: {
        name: '',
        value: '',
        buildingId: null,
        purposeSpaceId: null,
        assetId: '',
        popover: false,
        showPrevPopover: false,
        module: '',
        readingFieldId: '',
        spaceReadingFieldId: '',
        aggregation: null,
        dateRange: null,
        assetFieldId: '',
        constant: '',
        spaceId: '',
        spaceFieldId: '',
        hasBaseline: null,
        hasAggregateCondValue: false,
        baselineId: null,
        aggrCondOperator: null,
        aggrCondValue: null,
      },
      aggregateOptions: {
        sum: 'Sum',
        count: 'Count',
        avg: 'Average',
        min: 'Minimum',
        max: 'Maximum',
        lastValue: 'Current Value',
      },
      dateRangeOptions: {
        20: 'Last 20 Minutes', // Temp
        30: 'Last 30 Minutes', // Temp
        40: 'Last 40 Minutes', // Temp
        1: 'Last 1 Hour',
        3: 'Last 3 Hour',
        6: 'Last 6 Hour',
        12: 'Last 12 Hour',
        22: 'Today',
        23: 'Tomorrow',
        25: 'Yesterday',
        28: 'This Month',
        44: 'This Year',
      },
      namespaces: {
        date: {
          label: 'Date',
          functions: {
            getCurrentHour: {
              label: 'Get Current Hour',
              params: 'startTime',
              paramType: 'Number',
            },
            getCurrentDate: {
              label: 'Get Current Date',
              params: 'startTime',
              paramType: 'Number',
            },
            getCurrentDay: {
              label: 'Get Current Day',
              params: 'startTime',
              paramType: 'Number',
            },
            getCurrentMonth: {
              label: 'Get Current Month',
              params: 'startTime',
              paramType: 'Number',
            },
            getCurrentMonthDays: {
              label: 'Get Current Month Days',
              params: 'startTime',
              paramType: 'Number',
            },
            getLastMonthDays: {
              label: 'Get Last Month Days',
              params: 'startTime',
              paramType: 'Number',
            },
            now: {
              label: 'Now',
              params: 'startTime',
              paramType: 'Number',
            },
          },
        },
      },
      valueNotNeed: [1, 2],
      evaluated: false,
      uiMode: WorkflowUIMode.GUI,
      WorkflowUIMode: WorkflowUIMode,
      fbResult: '',
      workflowString: '',
      workflowV2String: '',
      isV2Script: false,
      occurenceType: 0,
      occurences: null,
      overPeriod: 1200,
      assetMetaField: {
        name: 'asset',
        lookupModule: { name: 'asset' },
      },
      currentAsset: {
        value: 'resourceId',
        label: 'Current Asset',
      },
    }
  },
  created() {
    if (!this.restrictInit) {
      this.$store.dispatch('formulabuilder/initData')
    }
  },
  mounted() {
    let unwatch = this.$watch('value', function() {
      if (!this.evaluated) {
        this.initExpressions()
        unwatch()
      }
    })

    this.initExpressions()
    if (this.module === 'virtual') {
      this.loadMeters()
    }
    this.loadBaselines()
  },
  watch: {
    workflowV2String() {
      this.evaluateExpression()
    },
  },
  computed: {
    ...mapState({
      assetList: state => state.formulabuilder.assetList,
      assetReadingFields: state => state.formulabuilder.assetReadingFields,
      spaceReadingFields: state => state.formulabuilder.spaceReadingFields,
      assetFields: state => state.formulabuilder.assetFields,
      spaceList: state => state.formulabuilder.spaceList,
      workorderFields: state => state.formulabuilder.workorderFields,
      spaceFields: state => state.formulabuilder.spaceFields,
      baselines: state => state.formulabuilder.baselines,
    }),
    ...mapGetters({
      getSpaceReadings: 'formulabuilder/getSpaceReadings',
      getAssetReadings: 'formulabuilder/getAssetReadings',
      getAsset: 'formulabuilder/getAsset',
      getSpace: 'formulabuilder/getSpace',
    }),
    isMvChange() {
      return this.isMv && this.$org.id === 321
    },
  },
  methods: {
    setDefaultExpression() {
      let a = this.$helpers.cloneObject(this.model)
      a.name = 'A'
      this.$set(this.variables, 'A', a)

      let b = this.$helpers.cloneObject(this.model)
      b.name = 'B'
      this.$set(this.variables, 'B', b)

      this.lastVariable++

      this.result = [
        {
          type: 'brace',
          value: '(',
        },
        {
          type: 'variable',
          value: 'A',
        },
        {
          type: 'operator',
          value: '+',
        },
        {
          type: 'variable',
          value: 'B',
        },
        {
          type: 'brace',
          value: ')',
        },
      ]
    },
    initExpressions: function() {
      this.result = []
      this.uiMode = this.value ? this.value.workflowUIMode : WorkflowUIMode.GUI
      if (
        !this.value ||
        (typeof this.value === 'object' &&
          this.value.constructor === Object &&
          !Object.keys(this.value).length)
      ) {
        this.setDefaultExpression()
      } else if (this.uiMode === WorkflowUIMode.XML) {
        this.workflowString = this.value.workflowString
        if (this.value.isV2Script) {
          this.isV2Script = true
          this.workflowV2String = this.value.workflowV2String
        }
        this.setDefaultExpression()
      } else {
        let resultEvaluator
        let isVirtual = this.module === 'virtual'
        if (!isVirtual && this.value && this.value.expressions) {
          resultEvaluator = this.value.resultEvaluator
          if (!resultEvaluator && this.setResultAsExpr) {
            let expression = this.value.expressions.find(
              expr => expr.name === 'resultExpr' || expr.expr
            )
            if (expression) {
              resultEvaluator = expression.expr
            }
          }
        } else {
          resultEvaluator = this.value
        }

        if (resultEvaluator) {
          let tempExp = {}
          if (!isVirtual) {
            this.value.expressions.forEach(expression => {
              tempExp[expression.name] = expression
            })
          }
          // this.setFbResult()
          if (this.value && this.value.occurences) {
            let occurences = this.value.occurences
            this.occurences =
              occurences.occurences !== -1 ? occurences.occurences : null
            this.overPeriod =
              occurences.overPeriod !== -1 ? occurences.overPeriod : 1200
            if (this.occurences) {
              if (occurences.overPeriod !== -1) {
                this.occurenceType = 2
              } else if (occurences.consecutive) {
                this.occurenceType = 1
              }
            } else if (occurences.overPeriod !== -1) {
              this.occurenceType = 3
            }
          }

          if (this.uiMode === WorkflowUIMode.COMPLEX) {
            this.value.expressions.forEach(expression => {
              this.initVariable(expression.name, expression)
            })
            this.fbResult = resultEvaluator
            return
          }
          let match = expRegex.exec(resultEvaluator)

          while (match != null) {
            if (match[1] || match[2]) {
              let name
              let exp
              let assetId
              if (this.module === 'virtual') {
                name = String.fromCharCode(this.lastVariable++)
                assetId = parseInt(match[2])
              } else {
                name = match[0]
                exp = tempExp[match[1]]
              }
              this.initVariable(name.toUpperCase(), exp, assetId)
            } else if (match[3]) {
              this.result.push({ type: 'brace', value: match[3] })
            } else if (match[4] || match[5] || match[6]) {
              this.result.push({ type: 'operator', value: match[0] })
            }
            match = expRegex.exec(resultEvaluator)
          }
        }
      }
    },
    initVariable(name, exp, assetId) {
      let variable = this.$helpers.cloneObject(this.model)
      if (this.module === 'virtual') {
        variable.assetId = assetId
      } else {
        this.lastVariable =
          name.charCodeAt(0) > this.lastVariable
            ? name.charCodeAt(0)
            : this.lastVariable
        this.setEnpi(exp, variable)
      }
      variable.name = name
      this.$set(this.variables, name, variable)
      this.result.push({ type: 'variable', value: name })
    },
    setEnpi(exp, expression) {
      if (!exp) {
        return
      }
      let moduleName = exp.moduleName

      if (exp.customFunctionResultEvaluator) {
        moduleName = 'function'
      } else if (!moduleName) {
        moduleName = exp.constant.startsWith('${workorder.')
          ? 'workorder'
          : 'constant'
      } else if (moduleName === 'basespace') {
        moduleName = 'space'
      } else if (moduleName !== 'asset') {
        let id
        if (
          exp.criteria &&
          exp.criteria.conditions &&
          exp.criteria.conditions['1']
        ) {
          let val = exp.criteria.conditions['1'].value
          id = isNaN(val)
            ? val.includes('resourceId')
              ? 'resourceId'
              : val
            : parseInt(val)
        }
        // temp
        if (this.isMvChange) {
          this.$http
            .get(`v2/resource/getResourcesDetails?resourceId[0]=${id}`)
            .then(response => {
              let resource = response.data.result.resource[0]
              expression.module =
                resource.resourceType === 2 ? 'assetreading' : 'spacereading'
              this.setEnpiExpression(exp, expression)
            })
        } else {
          this.loadAssets().then(() => {
            expression.module = this.assetList.find(asset => asset.id === id)
              ? 'assetreading'
              : 'spacereading'
            this.setEnpiExpression(exp, expression)
          })
        }
        return
      }
      expression.module = moduleName
      this.setEnpiExpression(exp, expression)
    },
    setEnpiExpression(exp, expression) {
      let parentId
      if (
        exp.criteria &&
        exp.criteria.conditions &&
        exp.criteria.conditions['1']
      ) {
        let val = exp.criteria.conditions['1'].value
        parentId = isNaN(val)
          ? val.includes('resourceId')
            ? 'resourceId'
            : val
          : parseInt(val)
      }
      if (
        expression.module === 'assetreading' ||
        expression.module === 'asset'
      ) {
        expression.assetId = parentId
      } else if (
        expression.module === 'spacereading' ||
        expression.module === 'space'
      ) {
        expression.spaceId = parentId
      } else if (expression.module === 'constant') {
        expression.constant = exp.constant
      } else if (expression.module === 'function') {
        expression.functionName = exp.defaultFunctionContext.functionName
        expression.nameSpace = exp.defaultFunctionContext.nameSpace
      } else if (expression.module === 'workorder') {
        expression.workorderFieldName = exp.constant.match(
          /\${workorder\.([a-z1-9]*)}/
        )[1]
      }

      if (
        this.module === 'enpi' &&
        (expression.module === 'assetreading' ||
          expression.module === 'spacereading')
      ) {
        if (exp.conditionSeqVsBaselineId && exp.conditionSeqVsBaselineId[2]) {
          expression.hasBaseline = true
          expression.baselineId = exp.conditionSeqVsBaselineId[2]
        }
      }
      if (
        (this.module === 'thresholdrule' || this.module === 'impact') &&
        (expression.module === 'assetreading' ||
          expression.module === 'spacereading') &&
        exp.criteria.conditions['2']
      ) {
        let operatorId = exp.criteria.conditions['2'].operatorId
        let range
        if ([22, 23, 25, 28, 44].includes(operatorId)) {
          range = operatorId
        } else if (!isNaN(exp.criteria.conditions['2'].value.split(',')[0])) {
          range = parseInt(exp.criteria.conditions['2'].value.split(',')[0])
        }
        expression.dateRange = range
      }

      if (
        exp.criteria &&
        exp.criteria.conditions &&
        exp.criteria.conditions['3']
      ) {
        expression.hasAggregateCondValue = true
        expression.aggrCondOperator = exp.criteria.conditions['3'].operatorId
        expression.aggrCondValue = exp.criteria.conditions['3'].value
      }

      this.loadRelatedModule(expression, true).then(() => {
        if (expression.module === 'asset') {
          expression.assetFieldId = this.assetFields.find(
            field => field.name === exp.fieldName
          ).id
        } else if (expression.module === 'space') {
          expression.spaceFieldId = this.spaceFields.find(
            field => field.name === exp.fieldName
          ).id
        } else if (expression.module === 'assetreading') {
          expression.aggregation = exp.aggregateString
          expression.readingFieldId = this.getAssetSpecificReadings(
            parentId
          ).find(
            readingField =>
              readingField.module.name === exp.moduleName &&
              readingField.name === exp.fieldName
          ).id
        } else if (expression.module === 'spacereading') {
          expression.aggregation = exp.aggregateString
          expression.spaceReadingFieldId = this.getSpaceReadings(parentId).find(
            readingField =>
              readingField.module.name === exp.moduleName &&
              readingField.name === exp.fieldName
          ).id
        }
      })
    },
    evaluateExpression: function(fetchObj) {
      this.evaluated = true
      if (this.uiMode === WorkflowUIMode.XML && fetchObj !== true) {
        let workflow = {
          workflowString: this.workflowString,
          workflowUIMode: this.uiMode,
        }
        if (this.isV2Script) {
          workflow.workflowV2String = this.workflowV2String
          workflow.isV2Script = true
        }
        this.$emit('input', workflow)
        return
      }

      if (this.module === 'virtual') {
        let expressionObj = ''
        for (let i = 0; i < this.result.length; i++) {
          let token = this.result[i]
          if (token.type === 'variable') {
            if (this.variables[token.value].assetId) {
              expressionObj += this.variables[token.value].assetId
            } else if (this.result[i + 1].type === 'operator') {
              i++
            }
          } else {
            if (
              token.type === 'operator' &&
              this.result[i + 1].type === 'variable' &&
              !this.variables[this.result[i + 1].value].assetId
            ) {
              i++
              continue
            }
            expressionObj += token.value
          }
        }
        this.$emit('input', expressionObj)
      } else {
        let obj = {
          workflowContext: {
            expressions: [],
            parameters: [],
            resultEvaluator: '',
            workflowUIMode: this.uiMode,
          },
        }
        for (let i = 0; i < this.result.length; i++) {
          let token = this.result[i]
          if (token.type === 'variable') {
            let exp = this.variables[token.value]
            if (exp.module === 'constant') {
              obj.workflowContext.expressions.push({
                name: exp.name,
                constant: exp.constant,
              })
            } else if (exp.module === 'asset') {
              let isCurrentResource = exp.assetId === 'resourceId'
              if (isCurrentResource) {
                this.addParam(obj, 'resourceId', 'Number')
              }
              obj.workflowContext.expressions.push({
                name: exp.name,
                fieldName: this.assetFields.find(
                  field => field.id === exp.assetFieldId
                ).name,
                moduleName: 'asset',
                aggregateString: '[0]',
                criteria: {
                  pattern: '1',
                  conditions: {
                    1: {
                      fieldName: 'id',
                      operatorId: 9,
                      sequence: '1',
                      value: isCurrentResource
                        ? '${' + exp.assetId + '}'
                        : exp.assetId,
                    },
                  },
                },
              })
            } else if (exp.module === 'space') {
              obj.workflowContext.expressions.push({
                name: exp.name,
                fieldName: this.spaceFields.find(
                  field => field.id === exp.spaceFieldId
                ).name,
                moduleName: 'basespace',
                aggregateString: '[0]',
                criteria: {
                  pattern: '1',
                  conditions: {
                    1: {
                      fieldName: 'id',
                      operatorId: 9,
                      sequence: '1',
                      value: exp.spaceId,
                    },
                  },
                },
              })
            } else if (exp.module === 'workorder') {
              let constantName = 'workorder.' + exp.workorderFieldName
              obj.workflowContext.expressions.push({
                name: exp.name,
                constant: '${' + constantName + '}',
              })
              this.addParam(obj, constantName)
            } else if (
              exp.module === 'assetreading' ||
              exp.module === 'spacereading'
            ) {
              let parentId
              let reading
              if (exp.module === 'assetreading') {
                parentId = exp.assetId
                reading = this.getAssetSpecificReadings(parentId).find(
                  readingField => readingField.id === exp.readingFieldId
                )
              } else {
                parentId = exp.spaceId
                reading = this.getSpaceReadings(parentId).find(
                  readingField => readingField.id === exp.spaceReadingFieldId
                )
              }
              let isCurrentResource = parentId === 'resourceId'
              if (isCurrentResource) {
                this.addParam(obj, 'resourceId', 'Number')
              }
              if (exp.spaceReadingFieldId === 'currentEnpi') {
                this.addParam(obj, 'currentModule')
                this.addParam(obj, 'currentField')
              }
              let expression = {
                name: exp.name,
                aggregateString: exp.aggregation,
                fieldName: reading.name,
                moduleName: reading.module.name,
                criteria: {
                  pattern: '(1 and 2)',
                  conditions: {
                    1: {
                      fieldName: 'parentId',
                      operatorId: 9,
                      sequence: '1',
                      value: isCurrentResource
                        ? '${' + parentId + '}'
                        : parentId,
                    },
                    2: {
                      fieldName: 'ttime',
                      operatorId: 20,
                      sequence: '2',
                      value: '$' + '{startTime}, ' + '$' + '{endTime}',
                    },
                  },
                },
                conditionSeqVsBaselineId: {},
              }
              // if (this.module === 'impact') {
              //   expression.criteria.pattern = '(1)'
              //   delete expression.criteria.conditions['2']
              // }
              if (exp.aggregation === 'lastValue') {
                expression.orderByFieldName = 'ttime'
                expression.sortBy = 'desc'
                expression.limit = 1
                if (
                  this.module === 'thresholdrule' ||
                  this.module === 'impact'
                ) {
                  expression.criteria.pattern = '(1)'
                  delete expression.criteria.conditions['2']
                }
              } else if (
                (this.module === 'thresholdrule' || this.module === 'impact') &&
                exp.dateRange
              ) {
                let operatorId
                if ([20, 30, 40].includes(exp.dateRange)) {
                  operatorId = 56
                } else if ([1, 3, 6, 12].includes(exp.dateRange)) {
                  operatorId = 42
                } else {
                  operatorId = exp.dateRange
                }
                expression.criteria.conditions['2'].operatorId = operatorId
                if (operatorId !== exp.dateRange) {
                  expression.criteria.conditions['2'].value =
                    exp.dateRange + ',${ttime}' // eslint-disable-line no-template-curly-in-string
                  this.addParam(obj, 'ttime', 'Number')
                } else {
                  delete expression.criteria.conditions['2'].value
                }
              }
              if (
                exp.hasAggregateCondValue &&
                exp.aggrCondOperator &&
                exp.aggrCondValue
              ) {
                expression.criteria.conditions['3'] = {
                  fieldName: expression.fieldName,
                  operatorId: exp.aggrCondOperator,
                  sequence: '3',
                  value: exp.aggrCondValue,
                }
                expression.criteria.pattern = '(1 and 2 and 3)'
              }
              if (exp.baselineId) {
                expression.conditionSeqVsBaselineId[2] = exp.baselineId
              }
              obj.workflowContext.expressions.push(expression)

              if (this.module !== 'thresholdrule' && this.module !== 'impact') {
                this.addParam(obj, 'startTime', 'Number')
                this.addParam(obj, 'endTime', 'Number')
              }
            }

            // Temp...needs to handle all fucnctions
            else if (exp.module === 'function') {
              let func = this.namespaces[exp.nameSpace].functions[
                exp.functionName
              ]
              obj.workflowContext.expressions.push({
                name: exp.name,
                defaultFunctionContext: {
                  nameSpace: exp.nameSpace,
                  functionName: exp.functionName,
                  params: func.params,
                },
              })
              if (func.params) {
                let placeholder = this.$helpers.getPlaceholders(func.params)
                this.addParam(
                  obj,
                  placeholder.length ? placeholder[0] : func.params,
                  func.paramType
                )
              }
            }

            if (exp.module) {
              obj.workflowContext.resultEvaluator += token.value
            }
          } else if (
            token.type !== 'operator' ||
            this.result[i + 1].type !== 'variable' ||
            this.variables[this.result[i + 1].value].module
          ) {
            obj.workflowContext.resultEvaluator += token.value
          }
        }
        if (this.uiMode === WorkflowUIMode.COMPLEX) {
          obj.workflowContext.resultEvaluator = this.fbResult
        }
        if (this.setResultAsExpr) {
          obj.workflowContext.expressions.push({
            name: 'resultExpr',
            expr: obj.workflowContext.resultEvaluator,
          })
          delete obj.workflowContext.resultEvaluator
        }
        if (this.module === 'thresholdrule' || this.module === 'impact') {
          if (this.metric) {
            let typeString
            if (typeof this.metric.dataTypeEnum === 'object') {
              typeString = this.metric.dataTypeEnum.typeAsString
            } else if (this.metric.dataTypeEnum === 'STRING') {
              let type = {
                STRING: 'String',
                NUMBER: 'Number',
                DECIMAL: 'Decimal',
                BOOLEAN: 'Boolean',
              }
              typeString = type[this.metric.dataTypeEnum]
            }
            this.addParam(obj, 'previousValue', typeString)
          }

          obj.workflowContext.occurences = {
            occurences:
              [1, 2].includes(this.occurenceType) && this.occurences > 0
                ? this.occurences
                : -99,
            overPeriod:
              [2, 3].includes(this.occurenceType) && this.overPeriod > 0
                ? this.overPeriod
                : -99,
            consecutive: this.occurenceType === 1,
          }
        }

        this.$emit('input', obj.workflowContext)
        return obj.workflowContext
      }
    },
    addParam(obj, name, type) {
      if (!obj.workflowContext.parameters) {
        obj.workflowContext.parameters = []
      }
      let params = obj.workflowContext.parameters
      if (!params.find(param => param.name === name)) {
        params.push({ name: name, typeString: type || 'String' })
      }
    },
    addExpression: function(index) {
      if (this.lastVariable === 90) {
        // Temp check
        return
      }
      if (this.lastVariable === 68) {
        this.lastVariable++
      }
      let name = String.fromCharCode(++this.lastVariable)
      let variable = this.$helpers.cloneObject(this.model)
      variable.name = name
      this.$set(this.variables, name, variable)
      if (index) {
        let tokens = [
          {
            type: 'operator',
            value: '+',
          },
          {
            type: 'variable',
            value: name,
          },
        ]
        this.result.splice(index, 0, ...tokens)
      } else {
        if (this.uiMode === WorkflowUIMode.COMPLEX) {
          this.result.push({
            type: 'variable',
            value: name,
          })
        } else {
          if (this.result.length !== 0) {
            this.result.push({
              type: 'operator',
              value: '+',
            })
          }
          this.result.push(
            {
              type: 'brace',
              value: '(',
            },
            {
              type: 'variable',
              value: name,
            },
            {
              type: 'brace',
              value: ')',
            }
          )
        }
      }
      // this.setFbResult()
      this.evaluateExpression()
    },
    loadRelatedModule(expression, fetchSpaceDetails) {
      if (expression.module === 'assetreading') {
        return this.loadAssets().then(() =>
          this.loadAssetReadings(expression.assetId)
        )
      } else if (expression.module === 'asset') {
        return this.loadAssets().then(() => this.loadAssetFields())
      } else if (expression.module === 'workorder') {
        return this.loadWorkorderFields()
      } else if (expression.module === 'spacereading') {
        // temp
        if (this.isMvChange) {
          this.loadSpaces()
          return this.loadSpaceDetails(expression.spaceId).then(() =>
            this.loadSpaceReadings(expression.spaceId)
          )
        } else {
          return this.loadSpaces().then(() =>
            this.loadSpaceReadings(expression.spaceId)
          )
        }
      } else if (expression.module === 'space') {
        return this.loadSpaces().then(() => this.loadSpaceFields())
      }
      return Promise.resolve()
    },
    getAssetSpecificReadings(assetId) {
      if (assetId && assetId > 0) {
        return this.getAssetReadings(assetId)
      } else {
        return this.getAssetReadings(
          this.assetCategory ? this.assetCategory.id : -1,
          true
        )
      }
    },
    ...mapActions({
      loadSpaceDetails: 'formulabuilder/loadSpaceDetails',
      loadSpaces: 'formulabuilder/loadSpaces',
      loadSpaceFields: 'formulabuilder/loadSpaceFields',
      loadAssetFields: 'formulabuilder/loadAssetFields',
      loadWorkorderFields: 'formulabuilder/loadWorkorderFields',
      loadSpaceReadings: 'formulabuilder/loadSpaceReadings',
      loadBaselines: 'formulabuilder/loadBaselines',
    }),
    loadAssets() {
      return this.$store.dispatch('formulabuilder/loadAssets', {
        assetCategory: this.assetCategory,
        orgId: this.$account.org.id,
      })
    },
    loadAssetReadings(assetId) {
      return this.$store.dispatch('formulabuilder/loadAssetReadings', {
        assetCategoryId:
          assetId && assetId > -1
            ? this.getAsset(assetId).category.id
            : this.assetCategory
            ? this.assetCategory.id
            : -1,
      })
    },
    loadMeters: function() {
      let that = this
      that.$http.get('/energymeter/all').then(function(response) {
        that.loading = false
        if (response.status === 200) {
          that.meterlist = response.data ? response.data : []
        }
      })
    },
    getVariableName: function(expression) {
      if (this.module === 'virtual' && expression.assetId) {
        return this.getMeterName(expression.assetId)
      } else {
        let variable = ''
        if (expression.module === 'asset') {
          if (expression.assetId) {
            variable = variable + this.getAssetName(expression.assetId)
          }
          if (expression.assetFieldId) {
            variable =
              variable + ' - ' + this.getAssetFieldName(expression.assetFieldId)
          }
          if (expression.aggregation) {
            variable =
              variable + ' - ' + this.aggregateOptions[expression.aggregation]
          }
        } else if (expression.module === 'space') {
          if (expression.spaceId) {
            variable = variable + this.getSpaceName(expression.spaceId)
          }
          if (expression.spaceFieldId) {
            variable =
              variable + ' - ' + this.getSpaceFieldName(expression.spaceFieldId)
          }
          if (expression.aggregation) {
            variable =
              variable + ' - ' + this.aggregateOptions[expression.aggregation]
          }
        } else if (expression.module === 'workorder') {
          if (expression.workorderFieldName) {
            variable =
              variable +
              this.getWorkorderFieldName(expression.workorderFieldName)
          }
        } else if (expression.module === 'assetreading') {
          if (expression.assetId) {
            variable = variable + this.getAssetName(expression.assetId)
          }
          if (expression.readingFieldId) {
            variable =
              variable +
              ' - ' +
              this.getReadingFieldName(
                expression.readingFieldId,
                expression.assetId,
                'asset'
              )
          }
          if (expression.aggregation) {
            variable =
              variable + ' - ' + this.aggregateOptions[expression.aggregation]
          }
          if (expression.dateRange) {
            variable += ' - ' + this.dateRangeOptions[expression.dateRange]
          }
          if (expression.baselineId) {
            variable += ' - ' + this.getBaselineName(expression.baselineId)
          }
        } else if (expression.module === 'spacereading') {
          if (expression.spaceId) {
            variable = variable + this.getSpaceName(expression.spaceId)
          }
          if (expression.spaceReadingFieldId) {
            variable =
              variable +
              ' - ' +
              this.getReadingFieldName(
                expression.spaceReadingFieldId,
                expression.spaceId,
                'space'
              )
          }
          if (expression.aggregation) {
            variable =
              variable + ' - ' + this.aggregateOptions[expression.aggregation]
          }
          if (expression.dateRange) {
            variable += ' - ' + this.dateRangeOptions[expression.dateRange]
          }
          if (expression.baselineId) {
            variable += ' - ' + this.getBaselineName(expression.baselineId)
          }
        } else if (expression.module === 'constant') {
          variable = variable + expression.constant
        } else if (expression.module === 'function' && expression.nameSpace) {
          let namespace = this.namespaces[expression.nameSpace]
          variable += namespace.label
          if (expression.functionName) {
            variable +=
              ' - ' + namespace.functions[expression.functionName].label
          }
        }
        return variable
      }
    },
    getBaselineName: function(id) {
      return this.baselines.find(baseline => baseline.id === id).name
    },
    getReadingFieldName: function(id, objId, type) {
      if (type === 'asset') {
        let field = this.getAssetSpecificReadings(objId).find(
          readingField => readingField.id === id
        )
        if (field) {
          return field.displayName
        }
      } else if (type === 'space') {
        let field = this.getSpaceReadings(objId).find(
          readingField => readingField.id === id
        )
        if (field) {
          return field.displayName
        }
      }
    },
    getSpaceFieldName: function(id) {
      return this.spaceFields.find(spaceField => spaceField.id === id)
        .displayName
    },
    getWorkorderFieldName: function(name) {
      return this.workorderFields.find(
        workorderField => workorderField.name === name
      ).displayName
    },
    getAssetFieldName: function(id) {
      return this.assetFields.find(assetField => assetField.id === id)
        .displayName
    },
    getSpaceName: function(id) {
      return this.spaceList.length
        ? this.spaceList.find(space => space.id === id).name
        : ''
    },
    getAssetName: function(id) {
      return this.assetList.length
        ? this.assetList.find(asset => asset.id === id).name
        : ''
    },
    getMeterName: function(id) {
      return this.meterlist.length
        ? this.meterlist.find(meter => meter.id === id).name
        : ''
    },
    getReadingFieldOperators(variable) {
      let fields
      let readingFieldId
      if (variable.module === 'assetreading') {
        fields = this.getAssetSpecificReadings(variable.assetId)
        readingFieldId = variable.readingFieldId
      } else {
        fields = this.getSpaceReadings(variable.spaceId)
        readingFieldId = variable.spaceReadingFieldId
      }
      let readingField = fields.find(
        readingField => readingField.id === readingFieldId
      )
      if (readingField) {
        variable.readingDataType = readingField.dataTypeEnum._name
        return readingField.dataTypeEnum.operators
      }
    },
    removeExpression: function(index) {
      delete this.variables[this.result[index].value]
      if (this.uiMode !== WorkflowUIMode.COMPLEX) {
        let deleteCount = 1
        if (index !== 0 && this.result[index - 1].type === 'operator') {
          --index
          deleteCount = 2
        } else if (index !== 0 && this.result[index - 1].type === 'brace') {
          if (
            this.result[index + 1] &&
            this.result[index + 1].type === 'brace'
          ) {
            /* if (index === 1) {
            --index; deleteCount = 3
          }
          else {
            index = index - 2; deleteCount = 4
          } */
            index = index === 1 ? index - 1 : index - 2
            deleteCount = 4
          } else {
            deleteCount = 2
          }
        }
        this.result.splice(index, deleteCount)
      } else {
        this.result.splice(index, 1)
      }

      // this.setFbResult()
      this.evaluateExpression()
    },
    selectOperator: function(operator, index) {
      this.result[index].value = operator
      this.evaluateExpression()
    },
    setVariable: function(expression) {
      expression.popover = false
      this.evaluateExpression()
    },
    setPrevValue(val) {
      this.variables[val].constant = '$' + '{previousValue}'
      this.variables[val].showPrevPopover = false
    },
    setFbResult() {
      if (this.uiMode !== WorkflowUIMode.COMPLEX) {
        return
      }
      this.fbResult = ''
      for (let i = 0; i < this.result.length; i++) {
        let token = this.result[i]
        if (token.type === 'variable') {
          this.fbResult += token.value
        } else {
          this.fbResult += token.value
        }
      }
    },
    parseResult(resultExp) {
      this.result = []
      let match = expRegex.exec(resultExp)
      let virtualVariable = 97
      while (match != null) {
        if (match[1] || match[2]) {
          let name =
            this.module === 'virtual'
              ? String.fromCharCode(virtualVariable++)
              : match[0]
          this.result.push({ type: 'variable', value: name })
        } else if (match[3]) {
          this.result.push({ type: 'brace', value: match[3] })
        } else if (match[4] || match[5] || match[6]) {
          this.result.push({ type: 'operator', value: match[0] })
        }
        match = expRegex.exec(resultExp)
      }
      this.evaluateExpression()
    },
    handleModeChange() {
      if (this.uiMode === WorkflowUIMode.COMPLEX) {
        this.result = this.result.filter(token => token.type === 'variable')
      } else if (this.uiMode === WorkflowUIMode.XML && this.value) {
        let workflow = this.evaluateExpression(true)
        this.$http
          .post('/v2/workflow/xml', { workflow: workflow })
          .then(response => {
            if (response.data.responseCode === 0) {
              this.workflowString = format(
                response.data.result.workflowString,
                formatOptions
              )
              this.evaluateExpression()
            }
          })
      }
    },
  },
}
</script>
<style>
.fbVariablePopper {
  min-height: 200px;
  width: 280px !important;
  padding: 0px !important;
  border-radius: 0px;
}

.operator-list > *:not(:first-child) {
  margin-top: 15px;
}

.operator-list * div {
  display: inline-block;
  cursor: pointer;
  margin-left: 10px;
  text-align: center;
  width: 30px;
  height: 30px;
  font-size: 15px;
  padding: 5px;
  border: 1px solid #f1f1f1;
  color: rgb(221, 221, 221) !important;
  transition: all 0.3s;
}

.operator-list * div:first-child {
  margin-left: 0px;
}

.operator-list * img {
  color: #ddd;
  opacity: 0.2;
  height: 13px;
  width: 20px;
}

.operator-list * img.small {
  width: 15px;
}

.operator-list * div:hover {
  background-color: #f0f9fa !important;
}

.fbVariableLegendContainer {
  padding-top: 30px;
  width: 100%;
}
.fbVariableLegendContainer table {
  width: 100%;
  border-collapse: collapse;
}
.fbVariableLegendContainer td {
  border: 1px solid #cae8ec;
}
.fbVariableLegendContainer td.fbVariableLegend {
  background: #f6fcfc;
  padding: 10px;
  width: 40px;
  text-align: center;
  color: #39b2c2;
  font-size: 18px;
  font-weight: 500;
}
.fbVariableLegendContainer td.fbVariableLegendInfo {
  padding-left: 10px;
}
.fbVariableLegendContainer tr td .fbVariableLegendDelete {
  display: none;
}
.fbVariableLegendContainer tr:hover td .fbVariableLegendDelete {
  display: block;
  padding-right: 10px;
  color: #e15e5e;
  font-size: 15px;
  cursor: pointer;
}
.fbOperator {
  border-radius: 3px;
  background-color: #39b2c2;
  color: #fff;
  width: 19px;
  height: 19px;
  text-align: center;
  line-height: 18px;
  font-weight: 500;
  font-size: 18px;
  cursor: pointer;
}

.fbOperator img {
  height: 13px;
  width: 10px;
  padding-top: 1px;
}

.fbNewBtn,
.fbNewBtn.inner:hover {
  border-radius: 50%;
  color: #fff;
  font-size: 15px;
  font-weight: 500;
  width: 15px;
  height: 15px;
  background-color: #ef4f8f;
  text-align: center;
  line-height: 15px;
  cursor: pointer;
}

.fbNewBtn.inner {
  background-color: #f095ba;
  font-size: 11px;
  width: 13px;
  height: 13px;
  line-height: 12px;
}

.fbVariable {
  border-radius: 3px;
  background-color: #f0f9fa;
  border: solid 1px #84ccd5;
  width: 52px !important;
  height: 50px !important;
  font-size: 18px;
  color: #39b2c2 !important;
  font-weight: 500;
  float: left;
}
.fbVariable.focused {
  width: 60px !important;
  height: 60px !important;
  font-size: 18px;
  font-size: 22px;
}
.fbContainer {
  background-color: #f6fcfc;
  margin-top: 20px;
  padding: 25px;
}

.fbContainer .fbExpressions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.fbExpressions > * {
  margin-bottom: 10px;
}

.fbOccurence .el-input__inner {
  background-color: transparent;
}

.fbTitle {
  color: #ef4f8f;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 1.6px;
  font-weight: 500;
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
.formula-xml {
  width: 100%;
}
.formula-xml:focus {
  outline: 0 !important;
  border-color: #39b2c2 !important;
}
</style>
