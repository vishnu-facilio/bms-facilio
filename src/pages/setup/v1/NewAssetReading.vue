<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog45 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form ref="newAssetReadingForm" :model="module" :label-position="'top'">
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div v-if="isNew" class="setup-modal-title">
              New {{ resourceType }} Reading
            </div>
            <div v-else class="setup-modal-title">
              Edit {{ resourceType }} Reading
            </div>
          </div>
        </div>
      </div>
      <div class="new-body-modal mT20 pB20">
        <div v-if="resourceType === 'Asset'">
          <div class="label-txt-black pB10">
            {{ $t('setup.setup.select_category') }}
          </div>
          <div>
            <el-select
              :disabled="module.disableUneditable"
              v-model="module.categoryId"
              filterable
              clearable
              :placeholder="$t('setup.setup.select_category')"
              class="width250px fc-input-full-border-select2"
            >
              <el-option
                v-for="category in assetCategory"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              ></el-option>
            </el-select>
          </div>
        </div>
        <div v-if="resourceType === 'Space'">
          <div class="label-txt-black pB10">
            {{ $t('setup.setup.select_category') }}
          </div>
          <div>
            <el-select
              v-model="module.categoryId"
              filterable
              clearable
              :placeholder="$t('setup.setup.select_category')"
              class="width250px fc-input-full-border-select2"
            >
              <el-option
                v-for="(category,
                index) in $store.getters.getSpaceCategoryPickList()"
                :key="index"
                :label="category"
                :value="index"
              ></el-option>
            </el-select>
          </div>
        </div>
        <div v-if="resourceType === 'site'">
          <div class="label-txt-black pB10">
            {{ $t('setup.setup.select_site') }}
          </div>
          <div class="">
            <el-select
              v-model="module.categoryId"
              filterable
              clearable
              :placeholder="$t('setup.setup.select_site')"
              class="width250px fc-input-full-border-select2"
            >
              <el-option
                v-for="(option, index) in picklistOptions"
                :key="index"
                :label="option"
                :value="index"
              ></el-option>
            </el-select>
          </div>
        </div>
        <div class="mT20" v-if="resourceType === 'Asset'">
          <el-checkbox
            :disabled="module.disableUneditable"
            v-model="isFormulaField"
            >Is Formula Field</el-checkbox
          >
        </div>
        <div class="mT20" v-if="isFormulaField">
          <el-form-item label="Field Name" style="width: 38%;">
            <el-input
              v-model="formula.name"
              class="fc-input-full-border-select2"
            ></el-input>
          </el-form-item>
          <div class="label-txt-black pB10">Asset(s)</div>
          <div class="add f-element resource-list">
            <div>
              <el-select
                class="multi fc-input-full-border-select2"
                multiple
                v-model="dummyAssertValue"
                disabled
                style="width: 80%;"
              >
                <el-option :label="resourceLabel" :value="1"></el-option>
              </el-select>
              <i
                @click="!isAssertDisabled ? (chooserVisibility = true) : null"
                :disabled="isAssertDisabled"
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                class="el-input__icon el-icon-search"
              ></i>
            </div>
          </div>
        </div>
        <div v-if="resourceType === 'Asset'" class="">
          <f-formula-builder
            v-if="isFormulaField"
            v-model="formula.workflow"
            module="formulaField"
            :assetCategory="{ id: module.categoryId }"
          ></f-formula-builder>
          <!-- <f-module-builder v-else v-model="module"></f-module-builder> -->
          <new-f-module-builder
            v-if="visibility"
            v-model="module"
          ></new-f-module-builder>
        </div>
        <div v-else class="">
          <new-f-module-builder
            v-if="visibility"
            v-model="module"
          ></new-f-module-builder>
          <!-- <f-module-builder v-model="module"></f-module-builder> -->
        </div>
      </div>
      <space-asset-multi-chooser
        v-if="chooserVisibility"
        @associate="associateResource"
        :visibility.sync="chooserVisibility"
        :initialValues="resourceData"
        :showAsset="true"
      ></space-asset-multi-chooser>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="save" class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import NewFModuleBuilder from 'pages/setup/new/v1/NewFmoduleBuilder'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import { mapState } from 'vuex'
import { getFieldOptions } from 'util/picklist'

export default {
  props: ['data', 'resourceType', 'visibility', 'isNew', 'model'],
  components: {
    FFormulaBuilder,
    SpaceAssetMultiChooser,
    NewFModuleBuilder,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
  },
  data() {
    return {
      formTitle: 'New Asset Reading',
      categoryList: [],
      dataTypeEnum: ['Text', 'Number', 'Decimal', 'Boolean'],
      module: {
        disableUneditable: false,
        categoryId: null,
        includeValidations: false,
        fields: [
          {
            safeLimitId: -1,
            inputPatternId: -1,
            raiseSafeLimitAlarm: false,
            raiseInputPatternAlarm: false,
            displayName: '',
            dataType: 3,
            dataTypeTemp: 3,
            minSafeLimit: null,
            maxSafeLimit: null,
            safeLimitSeverity: 'Minor',
            inputPatternSeverity: 'Minor',
            counterField: false,
          },
        ],
      },
      picklistOptions: {},
      isFormulaField: false,
      formula: {
        name: '',
        workflow: null,
        includedResources: [],
      },
      dummyAssertValue: [1],
      chooserVisibility: false,
    }
  },
  mounted() {
    this.init()
    if (!this.isNew && this.model) {
      Object.assign(this.module, this.model)
    }
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),

    resourceData() {
      return {
        assetCategory: this.module.categoryId,
        isIncludeResource: true,
        selectedResources: this.formula.includedResources.map(resource => ({
          id: resource && typeof resource === 'object' ? resource.id : resource,
        })),
      }
    },
    categoryName() {
      if (this.module.categoryId) {
        let category = this.assetCategory.find(
          category => category.id === this.module.categoryId
        )
        if (category) {
          return category.name
        }
      }
      return ''
    },
    isAssertDisabled() {
      return this.resourceType === 'Asset' && this.module.categoryId < 0
    },
    resourceLabel() {
      this.dummyAssertValue = [1]
      if (this.model.categoryId) {
        let message
        let selectedCount = this.formula.includedResources.length
        if (selectedCount) {
          if (selectedCount === 1) {
            return this.includedResources[0].name
          }
          message =
            selectedCount +
            ' ' +
            this.categoryName +
            (selectedCount > 1 ? 's' : '') +
            ' included'
        } else {
          message = 'All ' + this.categoryName + 's selected'
        }
        return message
      } else {
        this.dummyAssertValue = []
      }
    },
  },
  methods: {
    associateResource(selectedObj) {
      if (selectedObj.resourceList && selectedObj.resourceList.length) {
        this.formula.includedResources = selectedObj.resourceList
      }
      this.chooserVisibility = false
    },
    init: function() {
      if (this.resourceType === 'Asset') {
        this.module.includeValidations = true
      }
      if (this.resourceType === 'site') {
        this.loadPickList('site')
      }
    },
    async loadPickList(moduleName) {
      this.picklistOptions = {}
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.picklistOptions = options
      }
    },
    cancel() {
      this.$emit('canceled')
    },
    save() {
      if (this.isNew) {
        this.saveNew()
      } else {
        let url = '/reading/updatesetupreading'
        if (this.module.fields) {
          this.setReadingRules(this.module.fields[0], this.module.categoryId)
          let delReadingRulesIds = []
          if (
            this.module.fields[0].inputPattern === 'none' &&
            this.module.fields[0].inputPatternId &&
            this.module.fields[0].inputPatternId !== -1
          ) {
            delReadingRulesIds.push(this.module.fields[0].inputPatternId)
          }
          this.$http
            .post(url, {
              field: this.module.fields[0],
              moduleId: this.module.fields[0].moduleId,
              delReadingRulesIds,
            })
            .then(response => {
              if (typeof response.data === 'object') {
                this.$message.success('Reading Updated successfully.')
                this.reset()
                this.$emit('saved')
                this.closeDialog()
              } else {
                this.$message.error('Reading updation failed.')
              }
            })
        }
      }
    },
    setReadingRules(f, categoryId) {
      f.readingRules = []
      if (
        f.inputPattern === 'incremental' ||
        f.inputPattern === 'decremental'
      ) {
        f.readingRules.push({
          id: f.inputPatternId,
          name:
            f.inputPattern === 'incremental' ? 'Incremental' : 'Decremental',
          ruleType: 9,
          event: {
            activityType: 1,
          },
          assetCategoryId: categoryId,
          thresholdType: 5,
          workflow: {
            expressions: [
              {
                name: 'a',
                constant: '${inputValue}', // eslint-disable-line no-template-curly-in-string
              },
              {
                name: 'b',
                constant: '${previousValue}', // eslint-disable-line no-template-curly-in-string
              },
            ],
            parameters: [
              {
                name: 'inputValue',
                typeString: f.dataType === 2 ? 'Number' : 'Decimal',
              },
              {
                name: 'previousValue',
                typeString: f.dataType === 2 ? 'Number' : 'Decimal',
              },
            ],
            resultEvaluator:
              f.inputPattern === 'incremental'
                ? '(b!=-1)&&(a<b)'
                : '(b!=-1)&&(a>b)',
          },
          actions: !f.raiseInputPatternAlarm
            ? null
            : [
                {
                  actionType: 6,
                  templateJson: {
                    fieldMatcher: [
                      {
                        field: 'message',
                        value: `${f.displayName} should be incremental`,
                      },
                      {
                        field: 'severity',
                        value: f.inputPatternSeverity,
                      },
                    ],
                  },
                },
              ],
        })
      }
      if (f.minSafeLimit !== null || f.maxSafeLimit !== null) {
        f.readingRules.push({
          id: f.safeLimitId,
          name: 'Safe Limit',
          ruleType: 9,
          event: {
            activityType: 1,
          },
          assetCategoryId: categoryId,
          thresholdType: 5,
          workflow: {
            expressions: [
              {
                name: 'a',
                constant: '${inputValue}', // eslint-disable-line no-template-curly-in-string
              },
              {
                name: 'b',
                constant: f.minSafeLimit === null ? -1 : f.minSafeLimit,
              },
              {
                name: 'c',
                constant: f.maxSafeLimit === null ? -1 : f.maxSafeLimit,
              },
            ],
            parameters: [
              {
                name: 'inputValue',
                typeString: f.dataType === 2 ? 'Number' : 'Decimal',
              },
            ],
            resultEvaluator: '(b!=-1&&a<b)||(c!=-1&&a>c)',
          },
          actions: !f.raiseSafeLimitAlarm
            ? null
            : [
                {
                  actionType: 6,
                  templateJson: {
                    fieldMatcher: [
                      {
                        field: 'message',
                        value: `${f.displayName} should be within safe limit`,
                      },
                      {
                        field: 'severity',
                        value: f.safeLimitSeverity,
                      },
                    ],
                  },
                },
              ],
        })
      }
    },
    saveNew() {
      let formModel = this.module
      let url
      let param
      if (this.isFormulaField) {
        let formula = this.$helpers.cloneObject(this.formula)
        formula.assetCategoryId = this.module.categoryId
        formula.includedResources = formula.includedResources.map(
          resource => resource.id
        )
        let formulaField = this.$common.getFormulaFieldFromWorkflow(
          this.formula.workflow,
          formula
        )
        url = '/reading/addformula'
        param = { formula: formulaField }
      } else {
        url = '/reading/addsetupreading'
        if (this.module.includeValidations && this.module.fields) {
          this.module.fields.forEach(f =>
            this.setReadingRules(f, formModel.categoryId)
          )
        }
        param = {
          resourceType: this.resourceType,
          fieldJsons: this.module.fields,
          parentCategoryId: formModel.categoryId,
          readingName: this.module.fields[0].displayName,
        }
      }
      this.$http.post(url, param).then(response => {
        if (typeof response.data === 'object') {
          this.$message.success(' New Reading added successfully.')
          this.reset()
          this.$emit('saved')
          this.closeDialog()
        } else {
          this.$message.error(' New Reading addition failed.')
        }
      })
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    reset() {
      this.$refs.newAssetReadingForm.categoryId = ''
      this.$refs.newAssetReadingForm.fields = []
    },
  },
}
</script>
<style>
.new-header-container {
  margin-top: 0 !important;
}
</style>
