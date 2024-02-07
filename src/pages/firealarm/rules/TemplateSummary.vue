<template>
  <div>
    <el-dialog
      :visible.sync="showDialog"
      width="57%"
      height="80%"
      :append-to-body="true"
      style="z-index: 999999"
      :before-close="closeDialog"
      class="template-summary-dialog"
    >
      <div v-if="ruleTemplate" class="position-relative">
        <div class="p20 template-summary-height">
          <div class="fc-black-24">{{ ruleTemplate.name }}</div>
          <div class="fc-grey2-text14 pT10">{{ ruleTemplate.description }}</div>
          <el-row class="pT30 pB30">
            <el-col :span="6">
              <div class="fc-input-label-txt f11 bold">ALARM TYPE</div>
              <div class="label-txt-black">Assets</div>
            </el-col>
            <el-col :span="6">
              <div class="fc-input-label-txt f11 bold">ASSET CATEGORY</div>
              <div class="label-txt-black">{{ ruleTemplate.category }}</div>
            </el-col>
            <el-col :span="6" class="visibility-visible-actions pointer">
              <div class="fc-input-label-txt f11 bold">ASSETS</div>
              <div class="label-txt-black">
                {{ resourceLabel }}
                <i
                  class="el-icon-edit edit-icon-color visibility-hide-actions"
                  title="Edit Asset"
                  data-arrow="true"
                  v-tippy
                  @click="chooserVisibility = true"
                ></i>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="fc-input-label-txt f11 bold">THRESHOLD METRIC</div>
              <div class="label-txt-black">
                {{
                  getFieldDisplayName(ruleTemplate.threshold_metric).displayName
                }}
              </div>
            </el-col>
          </el-row>
          <div class="fc-modal-sub-title">RULE CONDITION</div>
          <div class="label-txt-black">
            <div class="fc-dark-blue-txt2 pT10 pB10">PREREQUISITE</div>
            {{ ruleTemplate.preRequsite.condition_statement }}
            <div class="fc-dark-blue-txt2 pT20 pB10 text-uppercase">
              Alarm Condition
            </div>
            {{ ruleTemplate.alarmCondition.condition_statement }}
            <div class="pT20">
              <div class="fc-input-label-txt flex-middle">
                Severity
                <div
                  class="fc-badge-critical mL20"
                  v-bind:style="{
                    'background-color': getAlarmSeverityByName(
                      ruleTemplate.severity
                    ).color,
                  }"
                >
                  {{
                    getAlarmSeverityByName(ruleTemplate.severity).displayName
                  }}
                </div>
              </div>
            </div>
            <div class="fc-dark-blue-txt2 pT10 pB10">ALARM CLEAR WHEN</div>
            Autoclear (Default)
          </div>
          <div class="fc-modal-sub-title pT20 pB10">ALARM DETAILS</div>
          <div class="fc-input-label-txt pT10 text-uppercase f11 bold">
            ALARM MESSAGE
          </div>
          <div class="label-txt-black">{{ ruleTemplate.name }}</div>
          <div class="fc-input-label-txt pT20 text-uppercase f11 bold">
            PROBLEMS
          </div>
          <div class="label-txt-black">{{ ruleTemplate.problem }}</div>
          <div class="fc-input-label-txt pT20 text-uppercase f11 bold">
            Possible Causes
          </div>
          <div class="label-txt-black">{{ ruleTemplate.possible_causes }}</div>
          <div class="fc-input-label-txt pT20 text-uppercase f11 bold">
            Possible Solution
          </div>
          <div class="label-txt-black">
            {{ ruleTemplate.possible_solution }}
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closeDialog"
            >Cancel</el-button
          >
          <el-button
            type="primary"
            @click="placeHolderVisibility = true"
            class="modal-btn-save"
            >USE TEMPLATE</el-button
          >
        </div>
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="placeHolderVisibility"
      title="Template"
      :append-to-body="true"
      width="30%"
      height="40%"
      class="fc-dialog-center-container"
    >
      <div class="height300 overflow-y-scroll pB50">
        <div
          class
          v-for="(templates, category) in groupedPlaceHolder"
          :key="category"
        >
          <div class="fc-modal-sub-title text-uppercase line-height18">
            {{ category }}
          </div>
          <el-row
            v-show="fields.selection_criteria"
            class="pT10 pB10 flex-middle"
            v-for="(fields, index) in templates"
            :key="index"
          >
            <el-col :span="24">
              <div
                v-if="fields.selection_criteria.displayType === 'Field'"
                class="fc-input-label-txt"
              >
                Field {{ fields.label }}
              </div>
              <div v-else class="fc-input-label-txt">{{ fields.label }}</div>
              <div
                v-if="fields.selection_criteria.displayType === 'ASSETCHOOSER'"
              >
                <el-input
                  v-model="resourceLabel"
                  disabled
                  class="fc-border-select fc-input-full-border-select2 width100"
                >
                  <i
                    @click="chooserVisibility = true"
                    slot="suffix"
                    style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                    class="el-input__icon el-icon-search"
                  ></i>
                </el-input>
              </div>
              <div
                v-else-if="fields.selection_criteria.displayType === 'DURATION'"
              >
                <el-select
                  placeholder="Enter Interval"
                  v-model="fields.default_value"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    :key="1"
                    label="Last 1 Hour"
                    :value="3600"
                  ></el-option>
                  <el-option
                    :key="3"
                    label="Last 3 Hour"
                    :value="10800"
                  ></el-option>
                  <el-option
                    :key="6"
                    label="Last 6 Hour"
                    :value="21600"
                  ></el-option>
                  <el-option
                    :key="12"
                    label="Last 12 Hour"
                    :value="43200"
                  ></el-option>
                </el-select>
              </div>
              <div
                v-else-if="fields.selection_criteria.displayType === 'field'"
              >
                <el-select
                  filterable
                  v-model="fields.default_value"
                  placeholder="Select"
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="field in thresholdFields"
                    :key="field.name + field.id"
                    :label="field.displayName"
                    :value="field.name"
                  ></el-option>
                </el-select>
              </div>
              <div
                v-else-if="fields.selection_criteria.displayType === 'NUMBER'"
              >
                <el-input
                  v-model="fields.default_value"
                  type="number"
                  class="fc-border-select fc-input-full-border-select2 width100"
                ></el-input>
              </div>
              <div
                v-else-if="fields.selection_criteria.displayType === 'Radio'"
              >
                <el-radio
                  v-model="fields.default_value"
                  :label="'true'"
                  class="fc-radio-btn"
                  >{{ fields.selection_criteria.trueVal }}</el-radio
                >
                <el-radio
                  v-model="fields.default_value"
                  :label="'false'"
                  class="fc-radio-btn"
                  >{{ fields.selection_criteria.falseVal }}</el-radio
                >
              </div>
              <el- :span="12" v-else class>
                <div class="fc-black-12 text-left bold">
                  {{ fields.default_value }}
                </div>
              </el->
              <el-col :span="12" class="mT10 pL20"></el-col>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel"
          @click="placeHolderVisibility = false"
          >CANCEL</el-button
        >
        <el-button type="primary" @click="createRules()" class="modal-btn-save"
          >Create Rule</el-button
        >
      </div>
    </el-dialog>
    <space-asset-multi-chooser
      @associate="data => associateResource(data, 'assets')"
      :visibility.sync="chooserVisibility"
      :initialValues="resourceData"
      :query="resourceQuery"
      :showAsset="true"
      :disable="true"
    ></space-asset-multi-chooser>
  </div>
</template>
<script>
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import { mapState, mapGetters } from 'vuex'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'
export default {
  mixins: [TemplateHelper],
  props: ['visibility', 'template'],
  components: {
    SpaceAssetMultiChooser,
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  watch: {
    template: {
      handler() {
        this.groupedPlaceHolder = this.$helpers.cloneObject(
          this.placeHolderGroupFields()
        )
      },
    },
  },
  data() {
    return {
      templates: [],
      chooserVisibility: false,
      selectedResourceList: [],
      resourceQuery: null,
      selectedMetricFields: null,
      groupedPlaceHolder: [],
      thresholdFields: null,
      placeHolderVisibility: false,
    }
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    ...mapGetters(['getAlarmSeverityByName']),
    showDialog() {
      return this.visibility
    },
    ruleTemplate() {
      return this.template
    },
    selectedCategory() {
      if (this.ruleTemplate && this.ruleTemplate.category) {
        let category = this.assetCategory.filter(
          d => d.name === this.ruleTemplate.category
        )
        return category
      }
      return null
    },
    resourceData() {
      return {
        assetCategory: this.selectedCategory
          ? this.selectedCategory[0].id
          : null,
        isIncludeResource: this.isIncludeResource,
        selectedResources: Array.isArray(this.selectedResourceList)
          ? this.selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            }))
          : this.selectedResourceList.id,
      }
    },
    resourceLabel() {
      if (this.ruleTemplate.category) {
        let category = this.selectedCategory
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
            category[0].name +
            (selectedCount > 1 ? 's' : '') +
            ' ' +
            includeMsg
        } else {
          message = 'All ' + category[0].name + 's'
        }
        return message
      } else if (this.selectedResourceList.id > 0) {
        return this.selectedResourceList.name
      }
      return null
    },
  },
  mounted() {
    if (this.ruleTemplate) {
      this.groupedPlaceHolder = this.placeHolderGroupFields()
    }
  },
  methods: {
    createRules() {
      let placeHolder = {}
      let index = 0
      Object.keys(this.groupedPlaceHolder).forEach(key => {
        this.groupedPlaceHolder[key].forEach((d, i) => {
          if (
            d.selection_criteria.displayType === 'ASSETCHOOSER' &&
            this.selectedResourceList.length > 0
          ) {
            let fieldName = this.isIncludeResource
              ? 'includeResource'
              : 'excludeResource'
            d.uniqueId = fieldName
            if (this.selectedResourceList.length > 0) {
              d.default_value = this.selectedResourceList.map(
                resource => resource.id
              )
            } else {
              d.default_value = null
            }
          }
          placeHolder[index] = d
          index++
        })
      })
      let params = {
        id: this.ruleTemplate.id,
        placeHolder: placeHolder,
      }
      console.log('params' + params)
      let url = `/v2/rules/template/createRule`
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0 && response.data.result) {
            this.$message.success('Rule created successfully')
            this.$router.push({
              path: 'active',
            })
          }
        })
        .catch(error => {
          this.$message.success('Something went wrong')
        })
    },
    loadThresholdFields(id) {
      this.thresholdFields = []
      this.$store
        .dispatch('formulabuilder/loadAssetReadings', {
          assetCategoryId: id,
        })
        .then(() => {
          this.thresholdFields = this.$store.getters[
            'formulabuilder/getAssetReadings'
          ](id, true)
        })
    },
    getFieldDisplayName(name) {
      let fieldObj
      this.thresholdFields.filter(d => {
        if (d.name === name) {
          fieldObj = d
        }
      })
      return fieldObj
    },
    checkFields() {
      this.loadThresholdFields(this.selectedCategory[0].id)
      console.log()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    placeHolderGroupFields() {
      this.checkFields()
      Object.keys(this.ruleTemplate.placeHolder).forEach(key => {
        this.templates.push(this.ruleTemplate.placeHolder[key])
      })
      return this.groupByField(this.templates, 'type')
    },
    associateResource(selectedObj, type) {
      if (type === 'assets') {
        if (selectedObj.resourceList && selectedObj.resourceList.length) {
          this.selectedResourceList = selectedObj.resourceList
          this.isIncludeResource = selectedObj.isInclude
        }
      } else {
        this.selectedResourceList = selectedObj
      }
      this.chooserVisibility = false
      this.resourceQuery = null
    },
  },
}
</script>
<style>
.template-summary-dialog .template-summary-height {
  height: 800px;
}

.template-summary-dialog .el-dialog__header {
  display: none;
}

.template-summary-dialog .el-dialog__body {
  padding: 0;
}
</style>
