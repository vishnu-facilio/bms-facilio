<template>
  <div class="height100 pB50">
    <div class="alarm-template-container height100">
      <div class="alarm-template-header">
        <div class="fL">
          <div class="summary-back" @click="goBack()">
            <i class="el-icon-back mR10"></i>
            {{ $t('common._common.back_list') }}
          </div>
          <div class="heading-black22 mT10">
            <span class="heading-black22">{{ ruleTemplate.name }}</span>
          </div>
        </div>
        <div style="margin-left: auto;" class="mL10 fR">
          <el-button type="button" class="fc-btn-edit">
            <i class="el-icon-edit" @click="editThresholdRule()"></i>
          </el-button>
        </div>
      </div>
      <div class="rules-template-body">
        <div class="width100">
          <div class="fL rules-body-sum-left">
            <div class="height100 overflow-y-scroll pB100">
              <div class="rules-sum-header">
                <div class="fc-black-14 fwBold text-left text-uppercase">
                  Template Summary
                </div>
              </div>
              <div class="p30">
                <div class="fc-text-pink11 text-uppercase">Description</div>
                <div class="fc-black-13 pT10 text-left">
                  {{ ruleTemplate.description }}
                </div>
                <div class="pT20">
                  <el-row>
                    <el-col :span="6">
                      <div class="fc-blue-label text-uppercase text-left">
                        Alarm Type
                      </div>
                      <div class="fc-black-13 text-left pT5">Assets</div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-blue-label text-uppercase text-left">
                        ASSET CATEGORY
                      </div>
                      <div class="fc-black-13 text-left pT5">
                        {{ ruleTemplate.category }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-blue-label text-uppercase text-left">
                        ASSETS
                      </div>
                      <div class="fc-black-13 text-left pT5">
                        {{ resourceLabel }}
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="fc-blue-label text-uppercase text-left">
                        THRESHOLD METRIC
                      </div>
                      <div class="fc-black-13 text-left pT5">
                        {{
                          getFieldDisplayName(ruleTemplate.threshold_metric)
                            .displayName
                        }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="pT30">
                  <div class="fc-text-pink11 text-uppercase">
                    RULE CONDITION
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label">PREREQUISITE</div>
                    <div class="fc-black-13 text-left pT5">
                      {{
                        ruleTemplate.preRequsite
                          ? ruleTemplate.preRequsite.condition_statement
                          : null
                      }}
                    </div>
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label">Alarm Condition</div>
                    <div class="fc-black-13 text-left pT5">
                      {{ ruleTemplate.alarmCondition.condition_statement }}
                    </div>
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label flex-middle">
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
                          getAlarmSeverityByName(ruleTemplate.severity)
                            .displayName
                        }}
                      </div>
                    </div>
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label">ALARM CLEAR WHEN</div>
                    <div class="fc-black-13 text-left pT5">
                      Autoclear (Default)
                    </div>
                  </div>
                </div>
                <div class="pT30">
                  <div class="fc-text-pink11 text-uppercase">ALARM DETAILS</div>
                  <div class="pT20">
                    <div class="fc-blue-label">ALARM MESSAGE</div>
                    <div class="fc-black-13 text-left pT5">
                      {{ ruleTemplate.name }}
                    </div>
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label">PROBLEM</div>
                    <div
                      class="fc-black-13 text-left pT5"
                      v-html="ruleTemplate.problem"
                    ></div>
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label bold">Possible Causes</div>
                    <div
                      class="fc-black-13 text-left pT5"
                      v-html="ruleTemplate.possible_causes"
                    ></div>
                  </div>
                  <div class="pT20">
                    <div class="fc-blue-label bold">Possible Solution</div>
                    <div
                      class="fc-black-13 text-left pT5"
                      v-html="ruleTemplate.possible_solution"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="fR rules-body-sum-right">
            <place-holder
              :ruleTemplate="ruleTemplate"
              :ruleId="ruleTemplate.ruleId"
              :selectedCategory="selectedCategory"
              :templateId="ruleTemplate.id"
              class="rules-body-sum-right-bg"
            ></place-holder>
          </div>
        </div>
      </div>
    </div>
    <new-rule
      v-if="thresholddialog"
      :rules="selectedRules"
      :ruleId="selectedRule"
      :visibility.sync="thresholddialog"
    ></new-rule>
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
import NewRule from 'pages/firealarm/rules/NewRule'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import PlaceHolder from '@/templates/PlaceHolder'
import { mapState, mapGetters } from 'vuex'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'
export default {
  components: {
    SpaceAssetMultiChooser,
    NewRule,
    PlaceHolder,
  },
  mixins: [TemplateHelper],
  computed: {
    ...mapGetters(['getAlarmSeverityByName']),
    ruleTemplate() {
      return this.$store.state.ruleTemplate.selectedTemplate
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
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    resourceData() {
      return {
        assetCategory: this.selectedCategory[0].id,
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
  data() {
    return {
      templates: [],
      selectedRules: null,
      thresholddialog: false,
      chooserVisibility: false,
      selectedResourceList: [],
      resourceQuery: null,
      selectedMetricFields: null,
      groupedPlaceHolder: [],
      thresholdFields: null,
      placeHolderVisibility: false,
    }
  },
  mounted() {
    if (this.ruleTemplate) {
      this.groupedPlaceHolder = this.placeHolderGroupFields()
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    editThresholdRule() {
      this.getRuleContext()
    },
    goBack() {
      window.history.go(-1)
    },
    getRuleContext() {
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
      let url = `/v2/rules/template/getRuleContext`
      this.$http
        .post(url, params)
        .then(response => {
          if (response.data.responseCode === 0 && response.data.result) {
            this.selectedRules = response.data.result
            this.selectedRule = -1
            this.thresholddialog = true
          }
        })
        .catch(() => {})
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
