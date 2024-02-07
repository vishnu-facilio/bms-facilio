<template>
  <div>
    <!-- RelatedGroups -->
    <div v-if="addRelatedGroups" class="pL50 mT25">
      <div class="fc-modal-sub-title sub-heading mB10">
        {{ $t('rule.create.related_groups') }}
      </div>
      <div class="cost-impact">
        <div
          v-for="(relGroup, index) in relatedGroups"
          :key="`relGroups-${index}`"
        >
          <div class="rel-group-variable-row">
            <el-input
              class="fc-input-full-border2 width70px rel-group-variable-container mL10"
              type="text"
              :readonly="true"
              v-model="relGroup.relGroupName"
            ></el-input>

            <span class="separator variable-separator">|</span>

            <template>
              <el-select
                class="fc-input-full-border width40 rel-group-linkname-container"
                v-model="relGroup.relMapContext.mappingLinkName"
                :placeholder="$t('rule.create.select_relation')"
                @change="handleRelGroupUpdate(relGroup)"
              >
                <template v-slot:prefix>
                  <div
                    v-if="relGroupHasMappingLinkName(relGroup)"
                    class="display-flex"
                  >
                    <fc-icon
                      group="action"
                      :name="relGroup.iconName"
                      class="pR10"
                    ></fc-icon>
                  </div>
                </template>
                <el-option
                  v-for="(relMap, index) in relatedGroupsOptions"
                  :key="index"
                  :label="relMap.label"
                  :value="relMap.reverseRelationLinkName"
                >
                  <div class="display-flex">
                    <fc-icon
                      group="action"
                      :name="relMap.iconName"
                      class="pR10"
                    ></fc-icon>
                    {{ relMap.label }}
                  </div>
                </el-option>
              </el-select>
            </template>
            <el-select
              class="fc-input-full-border2 mL10 width15"
              filterable
              v-if="
                isOneToManyRelation(relGroup) &&
                  relGroupHasMappingLinkName(relGroup)
              "
              v-model="relGroup.relAggregationType"
              placeholder="Choose Aggregation"
              @change="constructNamespace"
            >
              <el-option
                v-for="(key, label) in relAggregateOptions"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>
            <div
              v-if="relGroupHasCriteria(relGroup)"
              class="criteria_button_related"
            >
              <el-button
                class="mL10"
                @click="openCriteriaBuilder(relGroup, true)"
                >{{ $t('rule.create.edit_criteria') }}</el-button
              >
              <el-button class="mL10" @click="resetCriteria(relGroup)">{{
                $t('common._common.reset')
              }}</el-button>
            </div>
            <div
              v-else-if="isOneToManyRelation(relGroup)"
              class="criteria_button_related  mL10"
            >
              <el-button @click="openCriteriaBuilder(relGroup, false)">{{
                $t('rule.create.add_criteria')
              }}</el-button>
            </div>
            <div
              class="rel-group-delete-icon"
              @click="showConfirmDelete(index)"
            >
              <fc-icon
                group="default"
                name="trash-can-solid"
                :color="iconsColor"
              ></fc-icon>
            </div>
          </div>
        </div>
      </div>
      <div
        class="d-flex mT10 mB10 align-center pB10"
        :class="getRelGroupClassName"
      >
        <fc-icon
          group="action"
          name="circle-plus"
          :color="iconsColor"
          size="18"
        ></fc-icon>
        <div
          ref="fbText"
          :class="{
            'fc-id bold pointer f14 pL5': !isV2,
            'fb-v2-text bold pointer pL5': isV2,
          }"
          @click="addRelGroup"
        >
          {{ $t('rule.create.add_new_related_group') }}
        </div>
      </div>
    </div>
    <FDialog
      :visible.sync="showCriteriaBuilder"
      :width="'50%'"
      :title="`${addOrEditCriteria} Criteria`"
      @save="saveCriteria"
      @close="closeCriteria"
    >
      <CriteriaBuilder
        class="pB20 pR75"
        v-model="criteria"
        :moduleName="currentToModuleName"
        :key="groupKey"
      />
    </FDialog>
    <div v-if="addRelatedGroups" class="row-seperator"></div>

    <!-- Variables -->
    <div class="pL50 mT25">
      <div
        class="display-flex-between-space fc__layout_media_center width93 pB5"
      >
        <div class="fc-modal-sub-title sub-heading mB10">
          {{ $t('common._common.variables') }}
        </div>
        <div
          v-if="!addRelatedGroups && moduleName !== 'faultImpact'"
          class="d-flex align-center pR15"
          @click="enableRelatedGroups"
        >
          <fc-icon
            group="action"
            name="circle-plus"
            :color="iconsColor"
            size="18"
          ></fc-icon>
          <div
            ref="fbText1"
            :class="{
              'fc-id bold pointer f14 pL5': !isV2,
              'fb-v2-text bold pointer pL5': isV2,
            }"
          >
            {{ $t('rule.create.add_related_group') }}
          </div>
        </div>
      </div>

      <div class="cost-impact">
        <div v-for="(variable, index) in variables" :key="`variables-${index}`">
          <div class="variable-row">
            <el-input
              class="fc-input-full-border2 width110px variable-container mL10"
              type="text"
              v-model="variable.varName"
            ></el-input>
            <span class="separator variable-separator">|</span>
            <FieldLoader
              v-if="variable.readingsInitLoading"
              :isLoading="variable.readingsInitLoading"
              class="width25"
            ></FieldLoader>
            <el-select
              v-else
              class="fc-input-full-border2 width25"
              v-model="variable.type"
              :placeholder="$t('setup.formulaBuilder.choose_module')"
              @change="loadReadings(null, null, variable)"
            >
              <el-option
                v-for="(key, label) in moduleOptions"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>

            <FieldLoader
              v-if="variable.readingsInitLoading"
              :isLoading="variable.readingsInitLoading"
              class="mL10 width25"
            ></FieldLoader>
            <FLookupField
              v-else-if="variable.type !== 'RELATED_READING'"
              :key="`${variable.type}-field`"
              :ref="`${variable.type}-picker`"
              :model="variable.resourceId"
              class="mL10 width25"
              :field="getLookupField(variable)"
              @showLookupWizard="showLookupWizard(...arguments, variable)"
              @recordSelected="loadReadings(...arguments, variable)"
              @optionsLoadedOnce="addCustomOption"
            ></FLookupField>
            <template v-else>
              <el-select
                class="fc-input-full-border2 mL10 width25"
                v-model="variable.relGroupId"
                :placeholder="$t('rule.create.select_relation')"
                @change="loadReadings(null, relationField, variable)"
                clearable
              >
                <el-option
                  v-for="(relMap, index) in relationReadingsForOptions"
                  :key="index"
                  :label="relMap.relGroupName"
                  :value="relMap.id"
                ></el-option>
              </el-select>
            </template>

            <FieldLoader
              v-if="variable.readingsInitLoading"
              :isLoading="variable.readingsInitLoading"
              class="mL10 width30"
            ></FieldLoader>
            <el-select
              v-else
              class="fc-input-full-border2 mL10 width30"
              filterable
              v-model="variable.fieldId"
              :loading="variable.readingsLoading"
              :disabled="canDisableSelect(variable)"
              @change="handleReading(variable)"
              :placeholder="getPlaceHolderForReading(variable)"
            >
              <template v-if="variable.type === 'ASSET_READING'">
                <el-option
                  v-for="field in getAssetReadings(variable)"
                  :key="`${variable.id}-${field.name}`"
                  :label="field.displayName"
                  :value="field.id"
                >
                </el-option>
              </template>
              <template v-else-if="variable.type === 'SPACE_READING'">
                <el-option
                  v-for="field in getSpaceReadings(variable)"
                  :key="`${variable.id}-${field.name}`"
                  :label="field.displayName"
                  :value="field.fieldId"
                >
                </el-option>
              </template>
              <template v-else-if="variable.type === 'RELATED_READING'">
                <el-option
                  v-for="field in getRelationReadings(variable)"
                  :key="`${variable.id}-${field.name}`"
                  :label="field.displayName"
                  :value="field.id"
                >
                </el-option>
              </template>
              <template v-else-if="variable.type === 'ASSET'">
                <el-option
                  v-for="field in assetFields"
                  :key="`${variable.id}-${field.name}`"
                  :label="field.displayName"
                  :value="field.id"
                >
                </el-option>
              </template>
              <template v-else-if="variable.type === 'SPACE'">
                <el-option
                  v-for="field in spaceFields"
                  :key="`${variable.id}-${field.name}`"
                  :label="field.displayName"
                  :value="field.id"
                >
                </el-option>
              </template>
            </el-select>

            <FieldLoader
              v-if="variable.readingsInitLoading"
              :isLoading="variable.readingsInitLoading"
              class="mL15 width25"
            ></FieldLoader>
            <el-select
              v-else
              class="fc-input-full-border2  mL15 width25"
              filterable
              v-model="variable.aggregationType"
              :disabled="disableAggregation(variable)"
              placeholder="Choose Aggregation"
              @change="handleAggrgation(variable)"
            >
              <div v-if="!isDynamicKpi">
                <el-option
                  v-for="(key, label) in aggregateOptions"
                  :key="key"
                  :label="label"
                  :value="key"
                ></el-option>
              </div>
              <div v-else>
                <el-option
                  v-for="(key, label) in relAggregateOptions"
                  :key="key"
                  :label="label"
                  :value="key"
                ></el-option>
              </div>
            </el-select>

            <FieldLoader
              v-if="variable.readingsInitLoading"
              :isLoading="variable.readingsInitLoading"
              class="mL15 width30"
            ></FieldLoader>
            <el-select
              v-else-if="!isScheduledKpi && !isDynamicKpi"
              class="fc-input-full-border2 mL15 width30"
              v-model="variable.dataInterval"
              :disabled="disableTime(variable)"
              @change="constructNamespace"
              placeholder="Choose Time"
            >
              <el-option
                v-for="(key, label) in timeOptions"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>
            <div
              :class="getDeleteBtnClassName(index)"
              class="delete-icon"
              @click="deleteVariable(index)"
            >
              <fc-icon
                group="default"
                name="trash-can-solid"
                :color="iconsColor"
              ></fc-icon>
            </div>
          </div>
        </div>
        <template v-if="canShowLookupWizard">
          <LookupWizard
            v-if="isNewLookupWizardEnabled"
            :canShowLookupWizard.sync="canShowLookupWizard"
            :field="selectedLookupField"
            @setLookupFieldValue="setFieldValue"
          ></LookupWizard>
          <FLookupFieldWizard
            v-else
            :canShowLookupWizard.sync="canShowLookupWizard"
            :selectedLookupField="selectedLookupField"
            @setLookupFieldValue="setFieldValue"
          ></FLookupFieldWizard>
        </template>
      </div>
      <div class="d-flex mT10 mB10 align-center" :class="getVariableClassName">
        <fc-icon
          group="action"
          name="circle-plus"
          :color="iconsColor"
          size="18"
        ></fc-icon>
        <div
          ref="fbText2"
          :class="{
            'fc-id bold pointer f14 pL5': !isV2,
            'fb-v2-text bold pointer pL5': isV2,
          }"
          @click="addVariable"
        >
          {{ $t('rule.create.add_new_variable') }}
        </div>
      </div>

      <div class="pB30">
        <div class="display-flex-between-space width93 mT30 pB10">
          <div class="fc-modal-sub-title sub-headingrequired-label">
            {{ $t('rule.create.expression') }}
          </div>
          <div
            v-if="canShowToggle"
            :class="{ 'toggle-text-v2': isV2, 'toggle-text': !isV2 }"
            @click="toggleEditor(true)"
          >
            {{ switchHelper }}
          </div>
        </div>

        <el-input
          v-if="expressionType === 'expression'"
          v-model="expressionViewString"
          type="text"
          class="fc-input-full-border2 width93"
          :placeholder="`${this.$t('rule.create.enter_expression')}`"
          :autofocus="true"
          @input="handleExpression()"
        ></el-input>
        <div
          v-else
          class="height250 overflow-y-scroll width93 pB50 formula-code"
        >
          <CodeMirror :codeeditor="true" v-model="codeViewString"></CodeMirror>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FieldLoader from '@/forms/FieldLoader'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import CodeMirror from '@/CodeMirror'
import FLookupField from '@/forms/FLookupField'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import cloneDeep from 'lodash/cloneDeep'
import { v4 as uuid } from 'uuid'
import { LookupWizard } from '@facilio/ui/forms'
import { CriteriaBuilder } from '@facilio/criteria'
import FDialog from '@/FDialogNew'

const relGroupModel = {
  relGroupName: '',
  relMapId: -1,
  relMapContext: {
    mappingLinkName: null,
  },
  relAggregationType: '',
  criteria: null,
  relationType: 1,
  iconName: 'one-to-one',
}
const variableModel = {
  varName: 'A',
  type: 'ASSET_READING',
  moduleId: '',
  resourceId: '',
  fieldId: '',
  aggregationType: '',
  dataInterval: null,
  readingsLoading: false,
  readingsInitLoading: false, // to show field loader on init
  relGroupId: null,
  varDataType: null,
}

const aggregateOptions = {
  Start: 'FIRST',
  End: 'LAST',
  Average: 'AVG',
  Minimum: 'MIN',
  Maximum: 'MAX',
  Sum: 'SUM',
  Count: 'COUNT',
  'Distinct Count': 'DISTINCT_COUNT',
  Latest: 'LATEST',
}

const relAggregateOptions = {
  Average: 'AVG',
  Minimum: 'MIN',
  Maximum: 'MAX',
  Sum: 'SUM',
}

const relGroupVarHash = {
  assetReadings: [],
  spaceReadings: [],
  relationReadings: [],
  readingName: [],
}

const readingsHash = {
  assetReadings: [],
  spaceReadings: [],
  relationReadings: [],
  readingName: [],
}
const timeOptions = {
  '1 Min': 60000,
  '2 Mins': 120000,
  '3 Mins': 180000,
  '4 Mins': 240000,
  '5 Mins': 300000,
  '10 Mins': 600000,
  '15 Mins': 900000,
  '20 Mins': 1200000,
  '30 Mins': 1800000,
  '1 Hr': 3600000,
  '2 Hr': 7200000,
  '3 Hr': 10800000,
  '4 Hr': 14400000,
  '8 Hr': 28800000,
  '12 Hr': 43200000,
  '1 Day': 86400000,
}

const scriptDataTypes = ['Number', 'String', 'Map', 'Boolean', 'List']

export default {
  name: 'FormulaBuilder',
  components: {
    CodeMirror,
    FLookupField,
    FLookupFieldWizard,
    LookupWizard,
    CriteriaBuilder,
    FDialog,
    FieldLoader,
  },
  props: [
    'ruleCondition',
    'isEditForm',
    'preferredExpressionType',
    'preferredCodeBoilerPlate',
    'removeToggle',
    'moduleName',
    'isV2',
  ],
  data() {
    return {
      initiated: false,
      assetCatListLoaded: false,
      addRelatedGroups: false,
      currentRelGroupId: null,
      showCriteriaBuilder: false,
      groupKey: null,
      currentToModuleName: null,
      relatedGroups: [],
      addOrEditCriteria: 'Add',
      currentRelGroupLabelIndex: 1,
      criteria: null,
      iconsColor: '#3fb4c3',
      variables: [],
      relatedGroupsOptions: [],
      assetFields: [],
      spaceFields: [],
      currentWizardVariable: null,
      selectedLookupField: null,
      canShowLookupWizard: false,
      UIMode: {
        CODE: 5,
        EXPR: 4,
      },
      canShowToggle: true,
      assetField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        placeHolderText: `${this.$t('rule.create.select_asset')}`,
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
      },
      spaceField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'basespace',
        placeHolderText: `${this.$t('rule.create.select_space')}`,
        field: {
          lookupModule: {
            name: 'basespace',
            displayName: 'Space',
          },
        },
      },
      relationField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: null,
        placeHolderText: this.$t('rule.create.select_relation'),
        field: {
          lookupModule: {
            name: null,
            displayName: 'Relation',
          },
        },
      },
      expression: '',
      includedAssetIds: [],
      expressionViewString: '',
      codeViewString: '',
      spaces: [],
      expressionType: 'expression',
      variableCharCode: 65,
      variableCycle: 0,
      aggregateOptions: aggregateOptions,
      relAggregateOptions: relAggregateOptions,
      scriptDataTypes: scriptDataTypes,
      timeOptions: timeOptions,
      readingsMap: {},
      relGroupReadingsMap: {},
      codeBoilerPlate: 'Boolean test(){\n    \nreturn  ;  \n}',
      canShowCodeView: false,
      assetCategoryList: null,
    }
  },
  mounted() {
    if (this.isV2) {
      this.iconsColor = '#0059d6'
    }
  },
  async created() {
    this.init()
  },
  watch: {
    expression: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.constructNamespace()
        }
      },
      immediate: true,
    },
    codeViewString: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.$set(this, 'expression', newVal)
        }
      },
      immediate: true,
    },
    removeToggle: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && newVal) {
          this.$set(this, 'canShowToggle', false)
        }
      },
      immediate: true,
    },
    preferredExpressionType: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          this.$set(this, 'expressionType', newVal)
        }
      },
      immediate: true,
    },
    preferredCodeBoilerPlate: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && !isEmpty(newVal)) {
          this.$set(this, 'codeBoilerPlate', newVal)
        }
      },
      immediate: true,
    },
    assetCategoryId: {
      async handler(newVal) {
        let { assetCatListLoaded } = this
        if (!isEmpty(newVal) && assetCatListLoaded) {
          this.loadAssetReadingForCurrentAsset()
          this.loadRelatedGroupsList(newVal)
        }
      },
    },
    ruleInterval: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          let { ruleCondition, variables } = this
          let { ruleInterval } = ruleCondition
          variables.forEach(variable => {
            if (isEmpty(variable.fieldId)) {
              variable.dataInterval = ruleInterval
            }
          })
          this.constructNamespace()
        }
      },
    },
    variableCharCode: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal && newVal === 90) {
          //To Make Variable Name as A1 after as Z
          this.variableCharCode = 64
          this.variableCycle += 1
        }
      },
      immediate: true,
    },
  },
  computed: {
    assetCategoryId() {
      let { ruleCondition: { assetCategoryId } = {} } = this
      return assetCategoryId
    },
    ruleInterval() {
      let { ruleCondition: { ruleInterval } = {} } = this
      return ruleInterval
    },
    moduleOptions() {
      let moduleOptions = {
        'Asset Reading': 'ASSET_READING',
        'Space Reading': 'SPACE_READING',
      }
      if (this.moduleName !== 'faultImpact' && this.addRelatedGroups)
        moduleOptions = {
          ...moduleOptions,
          'Related Reading': 'RELATED_READING',
        }
      if (!this.isDynamicKpi) {
        moduleOptions = {
          ...moduleOptions,
          'Asset ': 'ASSET',
          'Space ': 'SPACE',
        }
      }
      return moduleOptions
    },
    switchHelper() {
      let { expressionType } = this
      if (expressionType === 'expression') {
        return this.$t('rule.create.switch_code')
      }
      return this.$t('rule.create.switch_expression')
    },
    getVariableClassName() {
      let { variables } = this
      if (!isEmpty(variables)) {
        let variable = variables[variables.length - 1]
        let { fieldId } = variable || {}
        let disableAddOption = isEmpty(fieldId)
        if (disableAddOption) {
          return 'disable-add-variable'
        }
      }
      return ''
    },
    getRelGroupClassName() {
      let { relatedGroups } = this
      if (!isEmpty(relatedGroups)) {
        let relGroup = relatedGroups[relatedGroups.length - 1]
        let { relMapContext: { mappingLinkName } = {} } = relGroup ?? {}
        let disableAddOption = isEmpty(mappingLinkName)
        if (disableAddOption) {
          return 'disable-add-variable'
        }
      }
      return ''
    },
    relationReadingsForOptions() {
      let { relatedGroups } = this
      return relatedGroups.filter(
        relGroup => !isEmpty(relGroup.relMapContext.mappingLinkName)
      )
    },
    isScheduledKpi() {
      let { ruleCondition, moduleName } = this
      let { kpiType } = ruleCondition || {}
      return moduleName === 'readingkpi' && kpiType === 2
    },
    isDynamicKpi() {
      let { ruleCondition, moduleName } = this
      let { kpiType } = ruleCondition || {}
      return moduleName === 'readingkpi' && kpiType === 3
    },
    isNewLookupWizardEnabled() {
      return this.$helpers.isLicenseEnabled('NEW_LOOKUP_WIZARD')
    },
  },
  methods: {
    async init() {
      let { ruleCondition, assetCategoryId, isEditForm } = this
      if (isEditForm && !isEmpty(ruleCondition)) {
        await this.prefillRuleCondition(ruleCondition)
      } else {
        this.initVariables()
        await this.loadAssetCategories()
        await this.loadRelatedGroupsList(assetCategoryId)
        await this.loadAssetFields()
        await this.loadSpaceFields()
        this.addRelGroup()
        this.handleExpression()
      }
      this.initiated = true
    },
    async loadAssetCategories() {
      let params = {
        page: 1,
        perPage: 5000,
        withCount: true,
      }
      let { data, error } = await API.get(
        'v2/module/data/list?moduleName=assetcategory',
        params
      )
      if (isEmpty(error)) {
        let { moduleDatas } = data || []
        this.assetCategoryList = moduleDatas
        this.assetCatListLoaded = true
      }
    },
    setExpressions(code, expression) {
      this.$set(this, 'codeViewString', code)
      this.$set(this, 'expression', code)
      this.$set(this, 'expressionViewString', expression)
    },
    setVariables(variable, id) {
      let { variables } = this
      let index = variables.findIndex(variable => variable.id === id)
      this.$set(this.variables, `${index}`, variable)
    },
    initVariables() {
      let variableModelObj = cloneDeep(variableModel)
      let id = uuid().substring(0, 4)
      variableModelObj.id = id
      let readingsHashObj = cloneDeep(readingsHash)
      this.readingsMap[id] = readingsHashObj
      this.variables.push(variableModelObj)
    },
    addRelGroup() {
      let { currentRelGroupLabelIndex } = this
      this.currentRelGroupLabelIndex++
      let relGroupModelObj = cloneDeep(relGroupModel)
      let id = uuid().substring(0, 4)
      relGroupModelObj.id = id
      relGroupModelObj.relGroupName = `RG${currentRelGroupLabelIndex}`
      let relGroupHashObj = cloneDeep(relGroupVarHash)
      this.relGroupReadingsMap[id] = relGroupHashObj
      this.relatedGroups.push(relGroupModelObj)
    },
    handleRelGroupUpdate(relGroup) {
      relGroup.relAggregationType = 'AVG'
      let { relMapContext: { mappingLinkName } = {} } = relGroup || {}
      let currentRelGroup = this.relatedGroupsOptions.find(
        relGroup => relGroup.reverseRelationLinkName === mappingLinkName
      )
      let { relationType } = currentRelGroup || {}
      relGroup.relationType = relationType
      relGroup.iconName = this.getRelTypeIconName(relationType)
      this.clearAssociatedVariable(relGroup)
    },
    getPlaceHolderForReading(variable) {
      if (this.isVariableTypeAssetOrSpace(variable)) return 'Choose Field'
      return 'Choose Reading'
    },
    getLookupField(variable) {
      let { type } = variable
      let { assetField, spaceField, relationField } = this

      switch (type) {
        case 'ASSET_READING':
        case 'ASSET':
          return assetField
        case 'SPACE_READING':
        case 'SPACE':
          return spaceField
        case 'RELATED_READING':
          this.$set(
            relationField,
            'lookupModuleName',
            this.getLookupModuleName()
          )
          return relationField
      }
    },
    getLookupModuleName() {
      let { ruleCondition: { lookupModuleName } = {}, assetCategoryId } = this
      if (isEmpty(lookupModuleName)) {
        return this.getCategoryName(assetCategoryId)
      }
      return lookupModuleName
    },
    showConfirmDelete(index) {
      let dialogObj = {
        title: 'CONFIRM DELETE',
        htmlMessage: `${this.$t('rule.create.confirm_rel_group_deletion')}`,
        rbDanger: true,
        rbLabel: 'Confirm',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteRelGroup(index)
        }
      })
    },
    deleteRelGroup(index) {
      let { relatedGroups } = this
      let relGroup = relatedGroups[index]
      let { id } = relGroup
      delete this.relGroupReadingsMap[id]
      this.relatedGroups = this.relatedGroups.filter((obj, i) => i !== index)
      if (this.relatedGroups.length === 0) {
        this.addRelatedGroups = false
      }
      this.deleteAssociatedVariable(relGroup)
      this.handleExpression()
    },
    async clearAssociatedVariable(relGroup) {
      let { variables } = this
      let relatedVars = variables.filter(
        variable =>
          variable.type === 'RELATED_READING' &&
          variable.relGroupId === relGroup.id
      )
      return relatedVars.forEach(variable => {
        variable = {
          ...variable,
          fieldId: null,
          aggregationType: null,
          ruleInterval: null,
        }
        this.loadReadings(null, null, variable)
      })
    },
    async deleteAssociatedVariable(relGroup) {
      let { variables } = this
      let relatedVars = variables.filter(
        variable =>
          variable.type === 'RELATED_READING' &&
          variable.relGroupId === relGroup.id
      )
      return relatedVars.map(variable =>
        this.deleteVariableWithoutIndex(variable)
      )
    },
    async enableRelatedGroups() {
      this.addRelatedGroups = true
    },
    relGroupHasCriteria(relGroup) {
      let { criteria, relationType } = relGroup ?? {}
      return !isEmpty(criteria) && relationType !== 1 && relationType !== 2 // not one to one or many to one
    },
    relGroupHasMappingLinkName(relGroup) {
      let { relMapContext: { mappingLinkName } = {} } = relGroup ?? {}
      return !isEmpty(mappingLinkName)
    },
    openCriteriaBuilder(relGroup, isEdit) {
      let { id, criteria } = relGroup
      this.currentRelGroupId = id
      this.groupKey = id
      if (isEdit && !isEmpty(criteria)) {
        this.addOrEditCriteria = 'Edit'
        this.criteria = criteria
      }
      this.showCriteriaBuilder = true
      this.currentToModuleName = this.getRelatedModuleName(relGroup)
    },
    saveCriteria() {
      let { criteria } = this
      let relGroup = this.relatedGroups.find(
        relGroup => this.currentRelGroupId === relGroup.id
      )
      this.$set(relGroup, 'criteria', criteria)
      this.constructNamespace()
      this.criteria = null
    },
    closeCriteria() {
      this.showCriteriaBuilder = false
      this.groupKey = null
      this.criteria = null
    },
    resetCriteria(relGroup) {
      this.groupKey = null
      this.$set(relGroup, 'criteria', null)
      this.constructNamespace()
    },
    addVariable() {
      let variableModelObj = cloneDeep(variableModel)
      this.variableCharCode += 1
      let { variableCharCode, variableCycle } = this
      let variableSuffix = variableCycle ? variableCycle : ''
      variableModelObj.varName = `${String.fromCharCode(
        variableCharCode
      )}${variableSuffix}`
      let id = uuid().substring(0, 4)
      variableModelObj.id = id
      let readingsHashObj = cloneDeep(readingsHash)
      this.readingsMap[id] = readingsHashObj
      this.variables.push(variableModelObj)
      this.handleExpression()
    },
    deleteVariableWithoutIndex(variable) {
      let { variables } = this
      let index = variables.findIndex(v => v.id === variable.id)
      this.deleteVariable(index)
    },
    deleteVariable(index) {
      let { variables } = this
      let variable = variables[index]
      let { id } = variable
      delete this.readingsMap[id]
      this.variables = this.variables.filter((obj, i) => i !== index)
      this.constructNamespace()
      this.handleExpression()
    },
    handleAggrgation(variable) {
      let { aggregationType, id } = variable || {}
      if (!isEmpty(aggregationType) && aggregationType === 'LATEST') {
        variable.dataInterval = null
      }
      this.setVariables(variable, id)
      this.constructNamespace()
    },
    disableTime(variable) {
      let { aggregationType } = variable
      if (this.isVariableTypeAssetOrSpace(variable)) {
        return true
      }
      if (isEmpty(aggregationType)) {
        return true
      }
      if (!isEmpty(aggregationType) && aggregationType === 'LATEST') {
        return true
      }
      return false
    },
    async loadAssetReadingForCurrentAsset() {
      let { variables } = this
      variables.forEach(variable => {
        let { type } = variable || {}
        if (type === 'ASSET_READING') {
          this.loadAssetReadings(variable)
        }
        if (type === 'RELATED_READING') {
          this.loadRelationReadings(variable)
        }
      })
    },
    loadReadings(selectedValue, field, variable) {
      let { type } = variable || {}
      let { value } = selectedValue || {}
      if (type !== 'RELATION_READING') {
        this.$set(variable, 'resourceId', value)
        this.$set(variable, 'relMapId', -1)
      }
      switch (type) {
        case 'ASSET_READING':
          this.loadAssetReadings(variable)
          break
        case 'SPACE_READING':
          this.loadSpaceReadings(variable)
          break
        case 'RELATED_READING':
          this.loadRelationReadings(variable)
          break
        case 'ASSET':
        case 'SPACE':
          variable.readingsLoading = false
      }
      this.resetReading(variable)
    },
    async loadSpaceReadings(variable) {
      let { readingsMap, initiated } = this
      let { resourceId, id } = variable || {}
      if (!initiated) variable.readingsInitLoading = true
      this.$set(variable, 'readingsLoading', true)

      if (!isEmpty(resourceId)) {
        let url = 'reading/getspacespecificreadings'
        let params = { parentId: resourceId, excludeEmptyFields: true }
        let { data, error } = await API.get(url, params)
        if (!isEmpty(error)) {
          this.$message.error(error.message)
        } else {
          if (Object.prototype.hasOwnProperty.call(readingsMap, id)) {
            let spaceReadings = []
            if (Array.isArray(data) && data.length > 0) {
              data.forEach(reading => {
                if (reading.fields) {
                  reading.fields.forEach(field => {
                    field.module = { name: reading.name }
                    spaceReadings.push(field)
                  })
                }
              })
            }

            this.$set(this.readingsMap[id], 'spaceReadings', spaceReadings)
            this.$set(variable, 'readingsLoading', false)
            if (!initiated) variable.readingsInitLoading = false
          }
        }
      }
      this.setVariables(variable, id)
    },
    async loadAssetReadings(variable) {
      let { readingsMap, initiated } = this
      let { resourceId, id } = variable || {}
      let assetDetail = {}
      let assetCategoryId = null
      if (!initiated) variable.readingsInitLoading = true
      this.$set(variable, 'readingsLoading', true)
      if (!isEmpty(resourceId) && resourceId > 0) {
        let url = 'v3/modules/data/summary'
        let params = { id: resourceId, moduleName: 'asset' }
        let { error, data } = await API.get(url, params)
        if (error) {
          this.$message.error('Error Occured')
        } else {
          if (!isEmpty(data)) {
            let { asset } = data || {}
            assetDetail = asset
          }
        }
        assetCategoryId = this.$getProperty(assetDetail, 'category.id', -1)
      }
      if (resourceId === -1) {
        let { ruleCondition } = this
        assetCategoryId = this.$getProperty(
          ruleCondition,
          'assetCategoryId',
          -1
        )
      }
      if (!isEmpty(assetCategoryId)) {
        let url = 'v2/readings/assetcategory'
        let params = { id: assetCategoryId, excludeEmptyFields: true }
        let { data: readingsData, error: readingsError } = await API.get(
          url,
          params
        )
        if (readingsError) {
          this.$message.error('Error Occured')
        } else {
          let { readings } = readingsData
          let serializedReadings = Object.values(readings).filter(
            reading => reading.name !== 'info'
          )
          if (Object.prototype.hasOwnProperty.call(readingsMap, id)) {
            await this.$set(
              this.readingsMap[id],
              'assetReadings',
              serializedReadings
            )
            if (!initiated) variable.readingsInitLoading = false
            this.$set(variable, 'readingsLoading', false)
          }
        }
      }
      this.setVariables(variable, id)
    },
    async loadRelatedGroupsList(assetCategoryId) {
      let { relationField } = this
      let categoryName = this.getCategoryName(assetCategoryId)
      this.$set(relationField, 'lookupModuleName', categoryName)
      if (!isEmpty(assetCategoryId)) {
        let url = '/v2/relation/list'
        let params = { moduleName: this.relationField.lookupModuleName }
        let { error, data } = await API.get(url, params)
        if (error) {
          this.$message.error('Error Occured')
        } else {
          let { relationList } = data || {}
          let relatedGroupsOptions = relationList.filter(relation =>
            this.isNotManyToManyRelation(relation)
          )
          relatedGroupsOptions = relatedGroupsOptions.map(relationMap => {
            let { name, reverseRelationLinkName, relationType } =
              relationMap || {}
            let label = `${name} - ${reverseRelationLinkName}`
            let iconName = this.getRelTypeIconName(relationType)

            return { ...relationMap, label, iconName }
          })
          this.relatedGroupsOptions = relatedGroupsOptions
        }
      }
    },
    isOneToManyRelation(relGroup) {
      let { iconName } = relGroup || {}
      return iconName === 'one-to-many'
    },
    isNotManyToManyRelation(relMap) {
      let { relationTypeEnum } = relMap || {}
      return relationTypeEnum !== 'MANY_TO_MANY'
    },
    getRelTypeIconName(relationType) {
      switch (relationType) {
        case 1:
          return 'one-to-one'

        case 2:
          return 'one-to-many'

        case 3:
          return 'many-to-one'

        case 4:
          return 'many-to-many'
      }
    },
    async loadRelationReadings(variable) {
      let { id } = variable || {}
      let { relatedGroups, initiated } = this
      this.$set(
        this.readingsMap[id],
        'relationReadings',
        relatedGroups.filter(
          relGroup => !isEmpty(relGroup.relMapContext.mappingLinkName)
        )
      )
      this.$set(variable, 'resourceId', -1)
      if (!initiated) variable.readingsInitLoading = true

      this.$set(variable, 'readingsLoading', true)
      let { relGroupId } = variable || {}
      if (!isEmpty(relGroupId)) {
        let chosenRelGroup = relatedGroups.find(
          relGroup => relGroup.id === relGroupId
        )
        let toModuleId = this.getRelationModuleId(variable, chosenRelGroup)
        let assetCategory = this.getCategoryId(toModuleId)
        if (!isEmpty(assetCategory)) {
          let url = 'v2/readings/assetcategory'
          let params = { id: assetCategory, excludeEmptyFields: true }
          let { data: readingsData, error: readingsError } = await API.get(
            url,
            params
          )
          if (readingsError) {
            this.$message.error('Error Occured')
          } else {
            let { readings } = readingsData || {}
            if (Object.prototype.hasOwnProperty.call(this.readingsMap, id)) {
              this.$set(this.readingsMap[id], 'readingName', readings)
              this.$set(variable, 'readingsLoading', false)
              if (!initiated) variable.readingsInitLoading = false
            }
          }
        }
      }
      this.setVariables(variable, id)
    },
    async loadAssetFields() {
      let fields = await this.loadModuleFields('asset')
      this.$set(this, 'assetFields', fields)
    },
    async loadSpaceFields() {
      let fields = await this.loadModuleFields('basespace')
      this.$set(this, 'spaceFields', fields)
    },
    async loadModuleFields(moduleName) {
      let url = `/module/fields?moduleName=${moduleName}`
      // let params = { id: resourceId }
      let { error, data } = await API.get(url)
      if (error) {
        this.$message.error('Error Occured')
      } else {
        if (!isEmpty(data)) {
          let { fields } = data || {}
          return fields
        }
      }
    },
    canDisableSelect(variable) {
      let { type } = variable
      let { relMapContext } = variable || {}
      let { mappingLinkName } = relMapContext || {}
      switch (type) {
        case 'ASSET_READING':
          if (variable.resourceId === -1) {
            return false
          }
          break
        case 'RELATED_READING':
          if (mappingLinkName !== null) {
            return false
          }
          break
        case 'ASSET':
        case 'SPACE':
          if (variable.resourceId >= -1) {
            return false
          }
      }
      return isEmpty(variable.resourceId)
    },
    showLookupWizard(field, canShow, variable) {
      let { id } = variable || {}
      this.selectedLookupField = field
      this.canShowLookupWizard = canShow
      this.currentWizardVariable = variable
      this.setVariables(variable, id)
    },
    getArgumentForFunction() {
      let { variables } = this
      let argument = ''
      variables.forEach((variable, index) => {
        let { varName, varDataType } = variable
        if (index !== variables.length - 1) {
          argument += `${
            isEmpty(varDataType) ? 'Number' : varDataType
          } ${varName},`
        } else {
          argument += `${
            isEmpty(varDataType) ? 'Number' : varDataType
          } ${varName}`
        }
      })
      return argument
    },
    getReturnStatement() {
      let { expressionViewString } = this
      if (!isEmpty(expressionViewString)) {
        return expressionViewString
      }
      return ''
    },
    handleExpression() {
      let { codeBoilerPlate } = this
      let argument = this.getArgumentForFunction()
      let returnStatement = this.getReturnStatement()
      let codeBoilerPlateString = codeBoilerPlate
      codeBoilerPlateString = codeBoilerPlateString.replace(
        '()',
        `(${argument})`
      )
      codeBoilerPlateString = codeBoilerPlateString.replace(
        'return  ;',
        `return ${returnStatement};`
      )
      this.setExpressions(codeBoilerPlateString, returnStatement)
    },
    toggleEditor(canToggle) {
      let { expressionType } = this
      if (canToggle) {
        if (expressionType === 'expression') {
          this.expressionType = 'code_view'
          this.handleExpression()
        } else {
          this.$confirm(
            'Switching to expression view would lose your changes',
            {
              type: 'warning',
            }
          ).then(() => {
            this.expressionType = 'expression'
            this.codeViewString = ''
          })
        }
      }
    },
    preFillAggregationAndTime(variable) {
      let { ruleCondition, isDynamicKpi } = this
      let { ruleInterval } = ruleCondition || {}
      let { id, fieldId } = variable || {}
      if (this.isVariableTypeAssetOrSpace(variable)) {
        //   variable.aggregationType = null
        variable.aggregationType = 'FIRST'
        variable.ruleInterval = null
      } else {
        if (!isEmpty(fieldId)) {
          variable.aggregationType = isDynamicKpi ? 'AVG' : 'FIRST'
        }
        variable.dataInterval = 900000
        if (!isEmpty(ruleInterval)) {
          variable.dataInterval = ruleInterval
        }
      }
      this.setVariables(variable, id)
      this.constructNamespace()
    },
    resetVariable(variable) {
      variable.resourceId = ''
      this.resetReading(variable)
    },
    resetReading(variable) {
      variable.fieldId = ''
      variable.aggregationType = ''
      variable.dataInterval = null
    },
    isVariableTypeAssetOrSpace(variable) {
      let { type } = variable || {}
      return ['ASSET', 'SPACE'].includes(type)
    },
    setFieldValue(props) {
      let { currentWizardVariable: variable } = this
      let { field } = props
      let { options = [], selectedItems = [] } = field
      let { type } = variable
      let selectedItemIds = []
      if (!isEmpty(selectedItems)) {
        selectedItemIds = selectedItems.find(item => item.value).value || {}
        if (!isEmpty(options)) {
          let ids = options.map(item => item.value)
          let newOptions = selectedItems.filter(
            item => !ids.includes(item.value)
          )
          options.unshift(...newOptions)
        } else {
          options = [...selectedItems]
        }
      }
      switch (type) {
        case 'ASSET_READING':
          this.$set(variable, `resourceId`, selectedItemIds)
          this.loadAssetReadings(variable)
          break
        case 'SPACE_READING':
          this.$set(variable, `resourceId`, selectedItemIds)
          this.loadSpaceReadings(variable)
          break
        case 'RELATED_READING':
          this.loadRelationReadings(variable)
          break
        case 'ASSET':
        case 'SPACE':
          this.$set(variable, `resourceId`, selectedItemIds)
          // this.loadAssetFields(variable)
          break
      }
      this.$set(this.selectedLookupField, 'options', options)
    },
    handleReading(variable) {
      let { readingsMap, assetFields, spaceFields } = this
      let { fieldId, type, id } = variable
      let readingField = {}
      switch (type) {
        case 'ASSET_READING':
          readingField = readingsMap[id].assetReadings.find(
            readingField => readingField.id === fieldId
          )
          break
        case 'SPACE_READING':
          readingField = readingsMap[id].spaceReadings.find(
            field => field.id === fieldId
          )
          break
        case 'RELATED_READING':
          readingField = readingsMap[id].relationReadings.find(
            field => field.id === fieldId
          )
          break
        case 'ASSET':
          readingField = assetFields.find(field => field.id === fieldId)
          this.setVarDataType(variable, readingField)
          break
        case 'SPACE':
          readingField = spaceFields.find(field => field.id === fieldId)
          this.setVarDataType(variable, readingField)
          break
      }
      if (!isEmpty(readingField)) {
        let moduleId = this.$getProperty(readingField, 'moduleId', -1)
        variable.moduleId = moduleId
      }
      this.setVariables(variable, id)
      this.preFillAggregationAndTime(variable)
      this.handleExpression()
      this.constructNamespace()
    },
    setVarDataType(variable, readingField) {
      let { scriptDataTypes } = this
      let { dataTypeEnum: { typeAsString } = {} } = readingField || {}

      this.$set(
        variable,
        'varDataType',
        scriptDataTypes.includes(typeAsString) ? typeAsString : 'Number'
      )
    },
    addCustomOption() {
      let newOption = [{ label: 'CurrentAsset', value: -1 }]
      let { assetField } = this
      let { options = [] } = assetField || {}
      let initialOption = options[0]
      let { value } = initialOption || {}
      if (value !== -1) {
        options.unshift(...newOption)
        this.$set(this.assetField, 'options', options)
      }
    },
    async prefillRuleCondition(ruleCondition) {
      let { assetCategory: { id: assetCategoryId } = {} } = ruleCondition || {}

      await this.loadAssetCategories()
      await this.loadRelatedGroupsList(assetCategoryId)
      await this.loadAssetFields()
      await this.loadSpaceFields()
      let { ns } = ruleCondition
      let { fields, workflowContext } = ns || {}
      let { workflowV2String, workflowUIMode } = workflowContext || {}
      await Promise.all(
        (fields || []).map(async variable => {
          let id = uuid().substring(0, 4)
          let { relatedInfo: { relMapId } = {}, nsFieldType, varDataType } =
            variable || {}
          variable.id = id
          variable.type = nsFieldType
          variable.varDataType = varDataType
          let readingsHashObj = cloneDeep(readingsHash)
          this.readingsMap[id] = readingsHashObj
          if (
            nsFieldType === 'ASSET_READING' ||
            nsFieldType === 'SPACE_READING' ||
            this.isVariableTypeAssetOrSpace(variable)
          ) {
            variable.selectedLookupField = null
            variable.canShowLookupWizard = false
            variable.readingsLoading = false
            variable.relMapContext = Object.assign(
              {},
              variableModel.relMapContext
            )
            let { resourceId, dataInterval } = variable || {}
            if (isEmpty(resourceId)) {
              variable.resourceId = -1
            }
            if (dataInterval === -1) {
              variable.dataInterval = null
            }
            this.variables.push(variable)
            if (nsFieldType === 'ASSET_READING') {
              await this.loadAssetReadings(variable)
            } else if (nsFieldType === 'SPACE_READING') {
              await this.loadSpaceReadings(variable)
            }
          } else if (nsFieldType === 'RELATED_READING' && relMapId > 0) {
            variable.selectedLookupField = null
            variable.canShowLookupWizard = false
            variable.readingsLoading = false
            let { dataInterval } = variable || {}
            if (dataInterval === -1) {
              variable.dataInterval = null
            }

            let relId = uuid().substring(0, 4)
            this.relGroupReadingsMap[relId] = cloneDeep(relGroupVarHash)
            let { relatedInfo } = variable || {}
            this.relatedGroups.push(this.createRelGroup(relId, relatedInfo))
            this.currentRelGroupLabelIndex++
            this.addRelatedGroups = true
            variable.relGroupId = relId
            await this.loadRelationReadings(variable)
            this.variables.push(variable)
          }
        })
      )
      let { relatedGroups } = this
      if (isEmpty(relatedGroups)) {
        this.addRelGroup()
      }
      let expressionValue = workflowV2String.split('return')[1]
      this.expression = workflowV2String
      this.codeViewString = workflowV2String
      this.expressionViewString = expressionValue.split(';')[0]
      this.canShowCodeView = this.checkUIMode(workflowUIMode)
      let { canShowCodeView } = this
      if (canShowCodeView) {
        this.expressionType = 'code_view'
      }
      this.variableCharCode += this.variables ? this.variables.length - 1 : 0
    },
    createRelGroup(relId, relatedInfo) {
      let { currentRelGroupLabelIndex } = this
      let { relAggregationType, criteria, relMapContext } = relatedInfo || {}
      let { relationType } = relMapContext || {}
      let relGroupModelObj = cloneDeep(relGroupModel)
      relGroupModelObj.relGroupName = `RG${currentRelGroupLabelIndex}`
      relGroupModelObj.id = relId
      relGroupModelObj.relAggregationType = relAggregationType
      relGroupModelObj.criteria = criteria
      relGroupModelObj.relMapContext = relMapContext
      relGroupModelObj.relationType = relationType
      relGroupModelObj.iconName = this.getRelTypeIconNameOnEdit(relationType)

      return relGroupModelObj
    },
    getRelTypeIconNameOnEdit(relationType) {
      switch (relationType) {
        case 1:
          return 'one-to-one'

        case 3: // stored reverse
          return 'one-to-many'

        case 2:
          return 'many-to-one'

        case 4:
          return 'many-to-many'
      }
    },
    checkUIMode(workflowUIMode) {
      let { UIMode } = this
      let { CODE } = UIMode
      return (
        workflowUIMode === CODE ||
        workflowUIMode === -1 ||
        isEmpty(workflowUIMode)
      )
    },
    async constructNamespace() {
      let { ruleCondition, isEditForm } = this
      let {
        expression,
        variables,
        expressionType,
        codeViewString,
        includedAssetIds,
        UIMode,
      } = this
      let { CODE, EXPR } = UIMode
      let scriptData = expression
      if (expressionType === 'code_view') {
        scriptData = codeViewString
      }
      let workflowContext = {
        workflowV2String: scriptData,
        workflowUIMode: expressionType === 'expression' ? EXPR : CODE,
      }

      if (isEditForm) {
        let { ns: namespace } = ruleCondition || {}
        let { workflowContext: workflowCtxt } = namespace || {}
        let { id } = workflowCtxt || {}
        workflowContext = { ...workflowContext, id: id }
      }

      let { assets } = ruleCondition || {}
      if (!isEmpty(assets)) {
        this.$set(this, 'includedAssetIds', assets)
      }

      let { ruleInterval } = ruleCondition || {}
      let fields = cloneDeep(variables)
      fields.forEach(field => {
        this.serializeField(field)
      })

      let ns = {
        execInterval: ruleInterval,
        fields,
        expressionType,
        includedAssetIds,
        workflowContext,
      }

      this.$emit('setCondition', { ns })
    },
    serializeField(field) {
      let excludeFields = [
        'id',
        'selectedLookupField',
        'type',
        'readingsLoading',
        'readingsInitLoading',
        'canShowLookupWizard',
        'relGroupId',
        'varDataType',
      ]
      let { aggregationType, relGroupId, type } = field
      field.nsFieldType = type

      if (!isEmpty(aggregationType) && aggregationType === 'LATEST') {
        field.dataInterval = -1
      }
      if (!isEmpty(relGroupId)) {
        let relGroup = this.relatedGroups.find(
          relGroup => field.relGroupId === relGroup.id
        )
        let { relAggregationType, criteria, relMapContext } = relGroup || {}
        field.relatedInfo = { relMapContext, relAggregationType, criteria }
      }
      excludeFields.forEach(excludeField => delete (field || {})[excludeField])
    },

    getDeleteBtnClassName(index) {
      if (this.moduleName && this.moduleName === 'faultImpact') {
        return ''
      }
      return index === 0 ? 'hide-v' : ''
    },
    getCategoryName(assetCategoryId) {
      let { assetCategoryList } = this || {}
      if (assetCategoryId > 0 && assetCategoryList) {
        let category = assetCategoryList.find(
          category => category.id === assetCategoryId
        )
        if (!isEmpty(category)) {
          let { moduleName } = category || {}
          return moduleName
        }
      }
    },
    getCategoryId(moduleId) {
      let { assetCategoryList } = this || {}
      if (moduleId > 0 && assetCategoryList) {
        let category = assetCategoryList.find(
          category => category.assetModuleID === moduleId
        )
        if (!isEmpty(category)) {
          let { id } = category || {}
          return id
        }
      }
    },
    getRelationModuleId(variable, relGroup) {
      let { relatedGroupsOptions } = this
      let { relMapContext } = relGroup || {}
      let { mappingLinkName } = relMapContext || {}
      if (mappingLinkName !== null) {
        let moduleId = relatedGroupsOptions.find(
          relOption => relOption.reverseRelationLinkName === mappingLinkName
        )
        if (!isEmpty(moduleId)) {
          let { toModuleId } = moduleId || {}
          this.$set(variable, 'moduleId', toModuleId)
          return toModuleId
        }
      }
    },
    getRelatedModuleName(relGroup) {
      let { relatedGroupsOptions } = this
      let { relMapContext } = relGroup || {}
      let { mappingLinkName } = relMapContext || {}
      if (mappingLinkName !== null) {
        let { toModuleName } = relatedGroupsOptions.find(
          relOption => relOption.reverseRelationLinkName === mappingLinkName
        )
        return toModuleName
      }
    },
    getRelationReadings(variable) {
      let { readingsMap } = this || {}
      let { id } = variable || {}
      return this.$getProperty(readingsMap[id], 'readingName')
    },
    getAssetReadings(variable) {
      let { readingsMap } = this || {}
      let { id } = variable || {}
      return this.$getProperty(readingsMap[id], 'assetReadings')
    },
    getSpaceReadings(variable) {
      let { readingsMap } = this || {}
      let { id } = variable || {}
      return this.$getProperty(readingsMap[id], 'spaceReadings')
    },
    disableAggregation(variable) {
      let { fieldId, type } = variable || {}
      return isEmpty(fieldId) || ['ASSET', 'SPACE'].includes(type)
    },
  },
}
</script>
<style scoped lang="scss">
.criteria_button_related {
  .el-button {
    border: solid 1px #38b2c2;
    border-radius: 4px;
    padding: 7px 8px;
  }
}
.impact-container {
  margin-top: -25px;
}
.sub-heading {
  color: #385571;
}
.sub-text {
  font-size: 13px;
}
.cost-impact {
  display: flex;
  flex-direction: column;
  .variable-row {
    display: flex;
    width: 95%;
    height: 80px;
    align-items: center;
    &:hover {
      background-color: #f6fcfc;
    }
  }
  .rel-group-variable-row {
    display: flex;
    width: 95%;
    height: 80px;
    align-items: center;
    &:hover {
      background-color: #f6fcfc;
      .rel-group-delete-icon {
        transition: 0.3s;
        opacity: 1;
        cursor: pointer;
      }
    }
  }
  .equals {
    height: 15px;
    width: 15px;
    font-weight: 100;
  }
}
.row-seperator {
  border: 0.1px solid #e5e5e5;
  margin: 0px 50px;
}
.rel-group-delete-icon {
  opacity: 0;
  display: block;
  padding-left: 10px;
  padding-right: 10px;
  color: #3ab2c1;
  font-size: 20px;
  float: right;
  cursor: pointer;
  margin-left: auto;
}
.delete-icon {
  display: block;
  padding-left: 10px;
  padding-right: 10px;
  color: #3ab2c1;
  font-size: 20px;
  float: right;
  cursor: pointer;
}
.circle-font {
  font-size: 10px;
}
.expression-circle {
  border: solid 1px #64c3d0;
  border-radius: 100%;
  font-size: 14px;
  font-weight: bold;
  color: #64c3d0;
  letter-spacing: 0.5px;
  line-height: 14px;
  text-align: center;
}
.formula-code {
  border: solid 1px #d0d9e2;
}
.ruleAdd {
  height: 16px;
  width: 16px;
  color: #64c3d0 !important;
}
.toggle-text {
  color: #319aa8;
  font-weight: bolder;
  &:hover {
    cursor: pointer;
  }
}
.toggle-text-v2 {
  color: #0059d6;
  font-weight: 500;
  &:hover {
    cursor: pointer;
  }
}
.variable-separator {
  font-size: 30px;
  color: #d8d8d873;
}
.disable-add-variable {
  pointer-events: none;
  opacity: 0.4;
}
.required-label {
  color: #e6323c;
}
</style>
<style lang="scss">
.fb-v2-text {
  color: #0059d6 !important;
  font-size: 13px;
  letter-spacing: 0.4px;
  font-weight: 400;
}
.variable-container {
  .el-input__inner {
    background-color: #fff0f6 !important;
    font-weight: 700;
    text-align: center;
    width: 70px;
  }
}
.add-var-icon {
  height: 16px;
  width: 16px;
  font-weight: 500;
}
.rel-group-variable-container {
  .el-input__inner {
    background-color: #fff0f6 !important;
    font-weight: 700;
    text-align: center;
    padding: 0px !important;
    width: 70px;
  }
}
.rel-group-linkname-container {
  .el-input__inner {
    font-weight: 400;
    font-size: 14px;
    padding-left: 40px;
  }
  .el-input {
    .el-input__prefix {
      padding: 10px;
    }
  }
}
</style>
