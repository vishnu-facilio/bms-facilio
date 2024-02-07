<template>
  <div class="action-config">
    <div
      v-for="(action, actionElement, index) in actionData"
      :key="getElement(actionElement).name + 'action'"
    >
      <el-form
        :model="action"
        :ref="`action_form_${index}`"
        :rules="rules"
        :label-position="'top'"
      >
        <slot
          name="element-title"
          :element="getElement(actionElement)"
          :index="index"
        >
          <div
            class="element-title fc-input-label-txt"
            :key="getElement(actionElement).name + 'actionName'"
          >
            {{
              getElement(actionElement).displayName ||
                getElement(actionElement).name
            }}
          </div>
        </slot>
        <slot
          name="action-type"
          :actionType="action.actionType"
          :onActionTypeChange="onActionTypeChange"
          :actionTypes="ACTION_CONFIG"
          :index="index"
          :element="getElement(actionElement)"
        >
          <el-row :gutter="10" class="pB10">
            <el-col :span="10">
              <p class="fc-input-label-txt pB5">Action Type</p>
              <el-select
                filterable
                :key="getElement(actionElement).name + 'actionType'"
                class="fc-input-full-border-select2 mB10"
                v-model="action.actionType"
                @change="
                  value => {
                    onActionTypeChange(actionElement, value), save()
                  }
                "
                placeholder="Select Action"
              >
                <el-option
                  v-for="(type, i) in FILTERED_CONFIG
                    ? Object.values(FILTERED_CONFIG)
                    : Object.values(ACTION_CONFIG)"
                  :key="type.actionType + i + ''"
                  :label="type.actionName"
                  :value="type.actionType"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </slot>
        <!-- Config for action types -->
        <div v-if="action.actionType === ACTION_TYPES.CONTROL">
          <el-row :gutter="10">
            <el-col :span="10">
              <el-form-item prop="data" class="mB10">
                <p class="fc-input-label-txt pB5">Control Type</p>
                <el-select
                  class="fc-input-full-border-select2 width100"
                  v-model="action.data.controlType"
                  @change="save"
                  placeholder="Type"
                >
                  <el-option
                    label="Control Point"
                    :value="CONTROL_TYPES.POINT"
                  ></el-option>
                  <el-option
                    label="Control Group"
                    :value="CONTROL_TYPES.GROUP"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col
              :span="14"
              v-if="action.data.controlType === CONTROL_TYPES.POINT"
            >
              <p class="fc-input-label-txt pB5">Asset</p>

              <el-input
                v-model="action.data.label"
                :readonly="true"
                type="text"
                placeholder="Choose Asset"
                class="fc-input-full-border-select2"
              >
                <i
                  @click="showControlPointAssetPicker = true"
                  slot="suffix"
                  style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                  class="el-input__icon el-icon-search"
                ></i>
              </el-input>
              <space-asset-chooser
                @associate="
                  (resource, resourceType) =>
                    associateControlPointAsset(
                      action.data,
                      resource,
                      resourceType
                    )
                "
                :visibility.sync="showControlPointAssetPicker"
                v-if="showControlPointAssetPicker"
                picktype="asset"
                :closeOnClickModel="true"
              ></space-asset-chooser>
            </el-col>
            <el-col :span="24">
              <el-form-item prop="data" class="mB10">
                <p class="fc-input-label-txt pB5">Control Points</p>
                <el-select
                  v-if="action.data.controlType === CONTROL_TYPES.POINT"
                  filterable
                  remote
                  :remote-method="
                    querry => {
                      fetchControlPoints(action, querry)
                    }
                  "
                  class="fc-input-full-border-select2 width100"
                  v-model="action.data.controlPointId"
                  @change="setControlPoint(action.data), save()"
                  placeholder="Select Control Point"
                >
                  <el-option
                    v-for="(cp, index) in controlPoints"
                    :key="index"
                    :label="
                      cp.resourceContext.name +
                        ' (' +
                        cp.field.displayName +
                        ')'
                    "
                    :value="cp.id"
                  ></el-option>
                </el-select>
                <el-select
                  v-if="action.data.controlType === CONTROL_TYPES.GROUP"
                  filterable
                  class="fc-input-full-border-select2 width100"
                  v-model="action.data.controlGroupId"
                  @change="setControlGroup(action.data.controlGroupId), save()"
                  placeholder="Select Control Group"
                >
                  <el-option
                    v-for="(cg, index) in controlGroups"
                    :key="index"
                    :label="cg.name"
                    :value="cg.id"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <template v-if="action.actionType === ACTION_TYPES.LINK">
          <el-row :gutter="10">
            <!-- <el-col :span="4">
              <el-form-item prop="data" class="mB10">
                <p class="fc-input-label-txt pB5">Link Types</p>
                <el-select
                  class="fc-input-full-border-select2 width100"
                  v-model="action.data.linkType"
                  @change="save"
                  placeholder="Type"
                >
                  <el-option
                    label="URL"
                    :value="LINK_TYPES.URL"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col> -->

            <el-col :span="16">
              <p class="fc-input-label-txt pB5">URL</p>
              <el-form-item prop="data" class="mB10">
                <el-input
                  v-model="action.data.url"
                  class="fc-input-full-border2"
                  placeholder="URL"
                ></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <p class="fc-input-label-txt pB5">Target</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  class="fc-input-full-border-select2"
                  v-model="action.data.target"
                  placeholder="Target"
                >
                  <el-option label="Same tab" value="self"></el-option>
                  <el-option label="New tab" value="_blank"></el-option>
                  <el-option label="Popup" value="popup"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        <template v-if="action.actionType === ACTION_TYPES.LISTVIEW">
          <el-row :gutter="10">
            <el-col :span="10">
              <p class="fc-input-label-txt pB5">Target</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  class="fc-input-full-border-select2"
                  v-model="action.data.target"
                  placeholder="Target"
                >
                  <el-option label="Same tab" value="self"></el-option>
                  <el-option label="New tab" value="_blank"></el-option>
                  <el-option label="Popup" value="popup"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <p class="fc-input-label-txt pB5">View</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  filterable
                  class="fc-input-full-border-select2"
                  v-model="action.data.view"
                  placeholder="View"
                >
                  <template v-for="(kpi, index) in Kpiviews">
                    <el-option-group
                      :label="kpi.displayName"
                      :value="kpi.name"
                      :key="index"
                      ><el-option
                        v-for="(item, j) in kpi.views"
                        :key="index + j + ''"
                        :label="item.displayName"
                        :value="item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
        <template v-if="action.actionType === ACTION_TYPES.REPORT">
          <el-row :gutter="10">
            <el-col :span="10">
              <p class="fc-input-label-txt pB5">Target</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  class="fc-input-full-border-select2"
                  v-model="action.data.target"
                  placeholder="Target"
                >
                  <el-option label="Same tab" value="self"></el-option>
                  <el-option label="New tab" value="_blank"></el-option>
                  <el-option label="Popup" value="popup"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <p class="fc-input-label-txt pB5">Report type</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  class="fc-input-full-border-select2"
                  v-model="action.data.type"
                  placeholder="Target"
                  @change="chooseReport(action.data.type)"
                >
                  <el-option label="Module Report" :value="1"></el-option>
                  <el-option label="Reading Report" :value="2"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="10" v-if="action.data.type === 1">
              <p class="fc-input-label-txt pB5">Module</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  class="fc-input-full-border-select2"
                  v-model="action.data.moduleName"
                  :placeholder="$t('setup.formulaBuilder.choose_module')"
                  @change="loadReports(action.data.moduleName)"
                  filterable
                  clearable
                >
                  <el-option
                    v-for="(m, i) in moduleList"
                    :key="i"
                    :label="m.displayName"
                    :value="m.name"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <p class="fc-input-label-txt pB5">Report</p>
              <el-form-item prop="data" class="mB10">
                <el-select
                  class="fc-input-full-border-select2"
                  v-model="action.data.reportId"
                  placeholder="Choose Reports"
                  @change="getReportObj(action.data.reportId)"
                  filterable
                  clearable
                >
                  <el-option-group
                    v-for="(folder, index) in reportFolders"
                    :key="index"
                    :label="folder.name"
                  >
                    <el-option
                      v-for="(report, idx) in folder.reports"
                      :key="idx"
                      :label="report.name"
                      :value="report.id"
                    >
                    </el-option>
                  </el-option-group>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="10">
            <el-col :span="20">
              <el-form-item class="mB10">
                <p class="fc-input-label-txt pB5">Parameters</p>
                <el-input
                  v-model="action.data.parameter"
                  class="width100 fc-input-full-border2"
                  clearable
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="2">
              <el-form-item class="mB10 mT25">
                <p class="fc-input-label-txt pB5"></p>
                <el-popover
                  v-model="showInsertVariablePopover"
                  placement="right"
                  width="250"
                  trigger="click"
                  :popper-class="'f-popover'"
                >
                  <div class="graphics-insert-variable-container">
                    <div class="graphics-insert-variable-title">
                      Insert Variable
                    </div>
                    <div class="graphics-insert-variable-filter">
                      <el-input
                        placeholder="Filter variables"
                        v-model="filterVariablesInput"
                        class="fc-input-full-border2"
                      ></el-input>
                    </div>
                    <div class="graphics-insert-variable-list" v-if="variables">
                      <ul>
                        <li
                          v-for="(v, index) in variables.filter(
                            v =>
                              !filterVariablesInput ||
                              v.name
                                .toLowerCase()
                                .indexOf(filterVariablesInput.toLowerCase()) >=
                                0
                          )"
                          :key="index"
                          @click="
                            action.data.parameter =
                              action.data.parameter + ' ${' + v.name + '}'
                            showInsertVariablePopover = false
                          "
                        >
                          {{ v.displayName }}
                        </li>
                      </ul>
                    </div>
                  </div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content="Insert Variable"
                    placement="top"
                    slot="reference"
                  >
                    <el-button
                      icon="el-icon-s-order"
                      style="padding: 2px 8px; font-size: 24px;"
                      type="text"
                    ></el-button>
                  </el-tooltip>
                </el-popover>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item class="mB10">
                <el-checkbox v-model="action.data.dashboardFilters"
                  >Dashboard Filters</el-checkbox
                >
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-form>
    </div>
  </div>
</template>
<script>
import ActionsMixin from 'pages/card-builder/mixins/ActionsMixin'
import clone from 'lodash/clone'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import Constants from 'util/constant'

export default {
  props: [
    'value',
    'defaultType',
    'elements',
    'canShowActionType',
    'definedActionTypes',
    'ModuleName',
  ],
  mixins: [ActionsMixin],
  components: { SpaceAssetChooser },
  data() {
    return {
      showControlPointAssetPicker: false,
      showInsertVariablePopover: false,
      filterVariablesInput: '',
      rules: {},
    }
  },
  computed: {
    moduleList() {
      return Constants.moduleList
    },
  },
  watch: {
    moduleName: function() {
      this.getlistview()
    },
  },
  mounted() {},
  methods: {
    save() {
      let cardDrilldown = clone(this.actionData)
      this.$emit('input', cardDrilldown)
    },
  },
}
</script>
