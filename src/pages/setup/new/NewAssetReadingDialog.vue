<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog60 setup-dialog dialog-res-screen65"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage="errorMessage"
    ></error-banner>
    <el-form ref="newAssetReadingForm" :model="module" :label-position="'top'">
      <div class="new-header-container border-none">
        <div class="setup-modal-title">New Asset Reading</div>
      </div>
      <div class="setup-formula-block2 mT20" v-if="!hideFormula">
        <el-row>
          <el-col :span="10">
            <span class="setup-formula-txt">Is this a formula field ?</span>
          </el-col>
          <el-col :span="2">
            <div class="yesno-txt">{{ isFormulaField ? 'Yes' : 'No' }}</div>
          </el-col>
          <el-col :span="3">
            <el-switch
              v-model="isFormulaField"
              class="setup-formula-switch"
              active-color="#39b2c2"
              inactive-color="#b3c3d3"
            ></el-switch>
          </el-col>
        </el-row>
      </div>
      <div
        class="new-body-modal pB20 pR20"
        style="height: calc(100vh - 220px);"
      >
        <div v-if="isFormulaField">
          <el-row>
            <el-col :span="10" class="mB30">
              <p class="label-txt2">Field Name</p>
              <el-input
                v-model="formula.name"
                style="height: 20px;"
                class="asset-field-input"
              ></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="10">
              <p class="label-txt2 pb10">Select Category</p>
              <div>
                <el-select
                  v-model="module.categoryId"
                  filterable
                  placeholder="Select Category"
                  style="width:80%"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="category in assetCategory"
                    :key="category.id"
                    :label="category.displayName"
                    :value="category.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
            <el-col :span="10">
              <p class="label-txt2 pB10 pL10">Unit</p>
              <el-input
                v-model="formulaFieldUnit"
                style="height: 20px; width:100%"
              ></el-input>
            </el-col>
          </el-row>
          <p class="label-txt2 mT20">Asset(s)</p>
          <div class="add f-element resource-list pB20">
            <div>
              <el-select
                class="multi"
                multiple
                v-model="dummyAssertValue"
                disabled
                style="width: 80%;"
              >
                <el-option
                  :label="resourceLabel"
                  :value="1"
                  style="height: 20px;"
                ></el-option>
              </el-select>
              <i
                @click="chooserVisibility = true"
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                class="el-input__icon el-icon-search"
              ></i>
            </div>
          </div>
        </div>
        <div>
          <f-formula-builder
            v-if="isFormulaField"
            v-model="formula.workflow"
            module="formulaField"
            :assetCategory="{ id: module.categoryId }"
          ></f-formula-builder>
          <div v-else>
            <p class="label-txt2">Select Category</p>
            <div>
              <el-select
                v-model="module.categoryId"
                filterable
                :disabled="categoryId > 0"
                placeholder="Select Category"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="category in assetCategory"
                  :key="category.id"
                  :label="category.displayName"
                  :value="category.id"
                ></el-option>
              </el-select>
            </div>
            <div class="fc-text-pink2 mT40">ASSET READINGS</div>
            <p class="grey-text2">Asset fields and types</p>
            <new-f-module-builder
              v-if="visibility"
              v-model="module"
            ></new-f-module-builder>
          </div>
          <f-safe-limit
            v-model="module.fields[0]"
            v-if="isFormulaField"
            ref="safelimit"
            class="pL40"
          />
        </div>
      </div>
      <space-asset-multi-chooser
        v-if="chooserVisibility"
        @associate="associateResource"
        :visibility.sync="chooserVisibility"
        :initialValues="resourceData"
        :showAsset="true"
        :hideBanner="true"
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
import NewFModuleBuilder from '@/NewFModuleBuilder'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import ErrorBanner from '@/ErrorBanner'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FSafeLimit from '@/FSafeLimit'
import { mapState } from 'vuex'

export default {
  props: ['visibility', 'model', 'hideFormula', 'categoryId'],
  components: {
    NewFModuleBuilder,
    FFormulaBuilder,
    ErrorBanner,
    SpaceAssetMultiChooser,
    FSafeLimit,
  },
  data() {
    return {
      module: {
        categoryId: null,
        fields: [
          {
            safeLimitPattern: 'none',
            raiseSafeLimitAlarm: false,
            displayName: '',
            dataType: 1,
            safeLimitSeverity: 'Minor',
            lesserThan: null,
            greaterThan: null,
            betweenTo: null,
            betweenFrom: null,
            inputPatternSeverity: 'Minor',
          },
        ],
      },
      error: false,
      errorMessage: null,
      isFormulaField: false,
      formula: {
        name: '',
        workflow: null,
        includedResources: [],
      },
      formulaFieldUnit: null,
      dummyAssertValue: [1],
      chooserVisibility: false,
      alarmSeverity: ['Critical', 'Major', 'Minor'],
    }
  },
  mounted() {
    if (this.model) {
      Object.assign(this.module, this.model)
    }
    if (this.categoryId > 0) {
      this.module.categoryId = this.categoryId
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
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
    resourceLabel() {
      this.dummyAssertValue = [1]
      if (this.module.categoryId) {
        let message
        let selectedCount = this.formula.includedResources.length
        if (selectedCount) {
          if (selectedCount === 1) {
            return this.formula.includedResources[0].name
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
    cancel() {
      this.$emit('canceled')
    },
    save() {
      this.saveNew()
    },
    saveNew() {
      let formModel = this.module
      let url
      let param
      this.error = false
      let fieldReadingRules = []
      if (this.isFormulaField) {
        this.module.fields.forEach(i => {
          fieldReadingRules.push(
            this.$common.getSafeLimitRules(i, this.module.categoryId)
          )
        })
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
        param = {
          formula: formulaField,
          formulaFieldUnit: this.formulaFieldUnit,
          fieldReadingRules,
        }
      } else {
        this.module.fields.forEach(i => {
          fieldReadingRules.push(
            this.$common.getSafeLimitRules(i, this.module.categoryId)
          )
        })
        if (!formModel.categoryId) {
          this.error = true
          this.errorMessage = 'Please select category for the fields'
          return
        } else if (!this.module.fields[0].displayName) {
          this.error = true
          this.errorMessage = 'Please enter name for the fields'
          return
        } else {
          this.module.fields.forEach(d => {
            if (!d.displayName) {
              this.error = true
              this.errorMessage = 'Please enter name for the fields'
              return
            }
            if (d.dataType === 8) {
              if (!d.values || d.values.length < 2) {
                this.error = true
                this.errorMessage =
                  'Please enter atleast two option for pick list field'
              }
            }
          })
          if (this.error) {
            return
          }
        }
        url = '/reading/addsetupreading'
        let moduleName = this.module.fields[0].displayName
        param = {
          resourceType: 'Asset',
          fieldJsons: this.module.fields,
          parentCategoryId: formModel.categoryId,
          readingName: moduleName,
          fieldReadingRules,
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
      this.reset()
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
.asset-field-input .el-input .el-input__inner,
.el-textarea .el-textarea__inner {
  height: 18px;
}
</style>
