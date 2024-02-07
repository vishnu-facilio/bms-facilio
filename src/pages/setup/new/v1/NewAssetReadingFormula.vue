<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container">
        <div class="setup-modal-title">
          {{ isNew ? 'New' : 'Edit' }} Formula
        </div>
      </div>
      <div
        class="new-body-modal pB100 pR20"
        style="height: calc(100vh - 100px);"
      >
        <div>
          <el-row>
            <el-col :span="18" class="mB30">
              <p class="label-txt2">Field Name</p>
              <el-input
                v-model="formula.name"
                placeholder="Enter the Field name"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="label-txt2 pB10">
                {{ $t('setup.setup.select_category') }}
              </p>
              <div>
                <el-select
                  v-model="formula.assetCategoryId"
                  filterable
                  placeholder="Select Category"
                  class="fc-input-full-border-select2 width100"
                  :disabled="!isNew"
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
            <el-col :span="12">
              <p class="label-txt2 pB10 pL10">Unit</p>
              <el-input
                class="fc-input-full-border-select2 width100"
                placeholder="Enter the unit"
                v-model="formulaFieldUnit"
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
                v-if="isNew"
                @click="chooserVisibility = true"
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                class="el-input__icon el-icon-search"
              ></i>
            </div>
          </div>
          <f-formula-builder
            v-model="formula.workflow"
            module="formulaField"
            class="mT20"
            :assetCategory="{ id: formula.assetCategoryId }"
          ></f-formula-builder>
          <f-safe-limit
            :edit="!isNew"
            v-model="module.fields[0]"
            ref="safelimit"
          />
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="save" class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-dialog>
    <space-asset-multi-chooser
      v-if="chooserVisibility"
      @associate="associateResource"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :showAsset="true"
      :hideBanner="true"
    >
    </space-asset-multi-chooser>
  </div>
</template>
<script>
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FSafeLimit from '@/FSafeLimit'
import { mapState } from 'vuex'
export default {
  props: ['visibility', 'isNew', 'model'],
  data() {
    return {
      module: {
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
      formula: {
        name: '',
        workflow: null,
        includedResources: [],
        assetCategoryId: null,
      },
      formulaFieldUnit: null,
      dummyAssertValue: [1],
      chooserVisibility: false,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {
    this.loadFormulaField()
    if (this.model) {
      if (this.model.fields[0].dataType === 4) {
        if (!this.model.fields[0].trueVal) {
          this.model.fields[0].trueVal = 'True'
        }
        if (!this.model.fields[0].falseVal) {
          this.model.fields[0].falseVal = 'False'
        }
      }
      Object.assign(this.module, this.$helpers.cloneObject(this.model))
      this.formulaFieldUnit = this.model.fields[0].unit
    }
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    resourceData() {
      return {
        assetCategory: this.formula.assetCategoryId,
        isIncludeResource: true,
        selectedResources: this.formula.includedResources.map(resource => ({
          id: resource && typeof resource === 'object' ? resource.id : resource,
        })),
      }
    },
    categoryName() {
      if (this.formula.assetCategoryId) {
        let category = this.assetCategory.find(
          category => category.id === this.formula.assetCategoryId
        )
        if (category) {
          return category.name
        }
      }
      return ''
    },
    resourceLabel() {
      this.dummyAssertValue = [1]
      if (this.formula.assetCategoryId) {
        let message
        let selectedCount = this.formula.includedResources
          ? this.formula.includedResources.length
          : 0
        if (selectedCount) {
          if (selectedCount === 1 && this.formula.includedResources[0].name) {
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
  components: {
    FFormulaBuilder,
    SpaceAssetMultiChooser,
    FSafeLimit,
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
      // let formModel = this.module
      let url
      let param
      this.error = false
      let fieldReadingRules = []

      this.module.fields.forEach(i => {
        fieldReadingRules.push(
          this.$common.getSafeLimitRules(i, this.formula.assetCategoryId)
        )
      })
      let delReadingRulesIds = []
      if (this.module.fields[0]) {
        if (
          this.module.fields[0] &&
          this.module.fields[0].safeLimitPattern === 'none' &&
          this.module.fields[0].safeLimitId &&
          this.module.fields[0].safeLimitId !== -1
        ) {
          delReadingRulesIds.push(this.module.fields[0].safeLimitId)
        }
      }
      let formulaField
      if (this.isNew) {
        let formula = this.$helpers.cloneObject(this.formula)
        formula.includedResources = formula.includedResources.map(
          resource => resource.id
        )
        url = '/reading/addformula'
        formulaField = this.$common.getFormulaFieldFromWorkflow(
          this.formula.workflow,
          formula
        )
      } else {
        url = '/reading/updateformula'
        formulaField = { id: this.formula.id, workflow: this.formula.workflow }
      }
      param = {
        formula: formulaField,
        formulaFieldUnit: this.formulaFieldUnit,
        fieldReadingRules,
        delReadingRulesIds,
      }
      if (!this.isNew) {
        param.moduleId = this.module.fields[0]
          ? this.module.fields[0].moduleId
          : this.module.numberFields[0].moduleId
        if (this.formula.name !== this.model.fields[0].displayName) {
          param.formula.name = this.formula.name
        }
      }

      this.$http.post(url, param).then(response => {
        if (typeof response.data === 'object') {
          this.$message.success(' New Reading added successfully.')
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
    loadFormulaField() {
      this.$http
        .get('/v2/reading/formula/field/' + this.model.fields[0].id)
        .then(response => {
          if (response.data.responseCode === 0) {
            Object.assign(this.formula, response.data.result.formulaField)
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
  },
}
</script>
