<template>
  <div class="vm-formula-builder">
    <!-- RelatedGroups -->
    <div v-if="addRelatedGroups" class="pL0 mT0">
      <div class="vm-reading-sub-title sub-heading mB10">
        {{ $t('rule.create.related_groups') }}
      </div>
      <div class="cost-impact">
        <div
          v-for="(relGroup, index) in relatedGroups"
          :key="`relGroups-${index}`"
        >
          <div class="rel-group-variable-row">
            <el-input
              class="fc-input-full-border2 width70px rel-group-variable-container"
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
                relGroup.relationType !== 1 &&
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
              v-else-if="relGroup.relationType !== 1"
              class="criteria_button_related  mL10"
            >
              <el-button @click="openCriteriaBuilder(relGroup, false)">{{
                $t('rule.create.add_criteria')
              }}</el-button>
            </div>
            <div
              class="rel-group-delete-icon p10"
              @click="showConfirmDelete(index)"
            >
              <fc-icon
                class="vm-action"
                group="default"
                name="trash-can"
                :size="18"
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
          group="navigation"
          name="addition"
          size="16"
          color="#0074d1"
        ></fc-icon>
        <div class="fc-id  pointer f14 pL5" @click="addRelGroup">
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
    <div v-if="addRelatedGroups" class="mT7"></div>

    <!-- Variables -->
    <div class="pL0 mT0">
      <div
        class="display-flex-between-space fc__layout_media_center pB5 width97"
      >
        <div class="vm-reading-sub-title sub-heading mB10">
          {{ $t('common._common.variables') }}
        </div>
        <div
          v-if="!addRelatedGroups && moduleName !== 'faultImpact'"
          class="d-flex align-center pR15"
          @click="enableRelatedGroups"
        >
          <fc-icon
            group="navigation"
            name="addition"
            size="16"
            color="#0074d1"
          ></fc-icon>
          <div class="fc-id  pointer f14 pL5">
            {{ $t('rule.create.add_related_group') }}
          </div>
        </div>
      </div>

      <div class="cost-impact">
        <div v-for="(variable, index) in variables" :key="`variables-${index}`">
          <div class="variable-row d-flex">
            <el-input
              class="fc-input-full-border2 width110px variable-container"
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
              @change="handleChange(variable)"
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
              <el-option
                v-for="(key, label) in aggregateOptions"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>

            <FieldLoader
              v-if="variable.readingsInitLoading"
              :isLoading="variable.readingsInitLoading"
              class="mL15 width30"
            ></FieldLoader>
            <el-select
              v-else-if="!isScheduledKpi"
              class="fc-input-full-border2 mL15 width30"
              v-model="variable.dataInterval"
              :disabled="disableTime(variable)"
              @change="constructNamespace"
              placeholder="Choose Time"
            >
              <el-option
                v-for="(key, label) in getInterval"
                :key="key"
                :label="label"
                :value="key"
              ></el-option>
            </el-select>
            <div
              :class="getDeleteBtnClassName(index)"
              class="delete-icon p10"
              @click="deleteVariable(index)"
            >
              <fc-icon
                class="vm-action"
                group="default"
                name="trash-can"
                :size="18"
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
          group="navigation"
          name="addition"
          size="16"
          color="#0074d1"
        ></fc-icon>
        <div class="fc-id  pointer f14 pL5" @click="addVariable">
          {{ $t('rule.create.add_new_variable') }}
        </div>
      </div>

      <div class="pB30">
        <div class="display-flex-between-space width97 mT30 pB10">
          <div class="vm-reading-sub-title sub-headingrequired-label">
            {{ $t('rule.create.expression') }}
          </div>
          <div
            v-if="canShowToggle"
            class="toggle-text"
            @click="toggleEditor(true)"
          >
            {{ switchHelper }}
          </div>
        </div>

        <el-input
          v-if="expressionType === 'expression'"
          v-model="expressionViewString"
          type="text"
          class="fc-input-full-border2 width97"
          :placeholder="`${this.$t('rule.create.enter_expression')}`"
          :autofocus="true"
          @input="handleExpression()"
        ></el-input>
        <div
          v-else
          class="height250 overflow-y-scroll width97 pB50 formula-code"
        >
          <CodeMirror :codeeditor="true" v-model="codeViewString"></CodeMirror>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FormulaBuilder from 'src/pages/alarm/rule-creation/component/FormulaBuilder'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import isEqual from 'lodash/isEqual'

const getFreqInterval = freq => {
  const durations = {
    6: '10 Mins',
    7: '15 Mins',
    8: '20 Mins',
    9: '30 Mins',
    10: '1 Hr',
    11: '2 Hr',
    12: '3 Hr',
    13: '4 Hr',
    14: '8 Hr',
    15: '12 Hr',
    16: '1 Day',
  }
  if (durations[freq]) {
    return durations[freq]
  } else {
    return null
  }
}

export default {
  name: 'VMFormulaBuilder',
  extends: FormulaBuilder,
  props: ['startDuration'],
  data() {
    return {
      codeBoilerPlate: 'Number test(){\n    \nreturn  ;  \n}',
    }
  },
  computed: {
    getInterval() {
      let frequency = this.$getProperty(this, 'startDuration')
      let startfreq = getFreqInterval(frequency)
      let map = {}
      if (!isEmpty(startfreq)) {
        let starttime = this.timeOptions[startfreq]
        Object.keys(this.timeOptions)
          .filter(optionKey => this.timeOptions[optionKey] >= starttime)
          .forEach(optionKey => {
            this.$set(map, optionKey, this.timeOptions[optionKey])
          })
      }
      return map
    },
  },
  watch: {
    ruleCondition: {
      async handler(newVal, oldVal) {
        if (!isEmpty(newVal) && !isEqual(oldVal, newVal)) {
          this.loadRelatedGroupsList()
        }
      },
      deep: true,
    },
  },
  methods: {
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
      await this.loadUtilityTypes()
    },
    async loadUtilityTypes() {
      let params = {
        page: 1,
        perPage: 50,
        withCount: true,
      }
      let { data, error } = await API.get(
        'v2/module/data/list?moduleName=utilitytype',
        params
      )
      if (isEmpty(error)) {
        let { assetCategoryList } = this
        let utilitytypes = this.$getProperty(data, 'moduleDatas', [])

        assetCategoryList = [...assetCategoryList, ...utilitytypes]
        this.assetCategoryList = assetCategoryList
        this.assetCatListLoaded = true
      }
    },
    async loadRelatedGroupsList(categoryId) {
      let { relationField, ruleCondition } = this
      let { lookupModuleName: moduleName, assetCategoryId } =
        ruleCondition || {}
      categoryId = assetCategoryId
      if (
        ['assetCategory', 'spaceCategory'].includes(moduleName) &&
        isEmpty(categoryId)
      ) {
        return
      }
      if (!isEmpty(categoryId)) {
        moduleName = this.getCategoryName(categoryId)
      }
      this.$set(relationField, 'lookupModuleName', moduleName)
      if (!isEmpty(moduleName)) {
        let url = '/v2/relation/list'
        let params = { moduleName }
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
        let { category, type } = this.getCategoryId(toModuleId)

        if (!isEmpty(category)) {
          let url =
            type === 'utility'
              ? 'v2/readings/utilitytype'
              : 'v2/readings/assetcategory'
          let params = { id: category, excludeEmptyFields: true }
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
    getCategoryId(moduleId) {
      let { assetCategoryList } = this || {}
      if (moduleId > 0 && assetCategoryList) {
        let type = ''
        let category = (assetCategoryList || []).find(category => {
          let { assetModuleID, meterModuleID } = category || {}
          let relModuleId = null

          if (!isEmpty(meterModuleID)) {
            relModuleId = meterModuleID
            type = 'utility'
          } else {
            relModuleId = assetModuleID
            type = 'assetCategory'
          }
          return relModuleId === moduleId
        })
        return { category: this.$getProperty(category, 'id', null), type }
      }
    },
    deleteVariable(index) {
      let { variables } = this
      let variable = variables[index]
      let { id } = variable
      delete this.readingsMap[id]
      this.variables = this.variables.filter((obj, i) => i !== index)
      if (isEmpty(this.variables)) {
        this.initVariables()
      }
      this.constructNamespace()
      this.handleExpression()
    },
    handleChange(variable) {
      this.handleReading(variable)
      if (variable.dataInterval == 900000) {
        let frequency = this.$getProperty(this, 'startDuration')
        let startfreq = getFreqInterval(frequency)
        let starttime = this.timeOptions[startfreq]
        variable.dataInterval = starttime
      }
    },
  },
}
</script>
<style lang="scss">
.vm-formula-builder {
  .rel-group-variable-container .el-input__inner,
  .variable-container .el-input__inner {
    background-color: #0074d1 !important;
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    width: 55px;
  }
  .fc-id,
  .toggle-text {
    color: #0074d1;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
  .vm-reading-sub-title {
    font-size: 14px;
    font-weight: 500;
    color: #324056;
    text-transform: capitalize;
    margin-bottom: 0px !important;
  }

  .cost-impact {
    .variable-row,
    .rel-group-variable-row {
      display: flex;
      width: 100%;

      &:hover {
        background-color: #f2f5fa;
      }
    }
  }

  .vm-reading-sub-title {
    font-size: 14px;
    font-weight: 500;
    color: #324056;
    text-transform: capitalize;
  }
}
</style>
