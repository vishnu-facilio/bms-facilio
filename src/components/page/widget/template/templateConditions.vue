<template>
  <div class="p20 fc-rule-template-contion">
    <div
      v-if="selectedRules && selectedRules.alarmRule.alarmTriggerRule.workflow"
    >
      <div class="fc-blue-label">Alarm Condition</div>
      <div>
        <div class="label-txt-black pT10">
          {{
            selectedRules.alarmRule.alarmTriggerRule.workflow.resultEvaluator
          }}
        </div>
        <f-formula-builder
          v-model="selectedRules.alarmRule.alarmTriggerRule.workflow"
          module="thresholdrule"
          :title="''"
          :renderInLeft="true"
          :restrictInit="true"
          :showOnlyVarible="true"
          :assetCategory="{ id: this.selectedCategory[0].id }"
        ></f-formula-builder>
      </div>
    </div>
    <div v-if="selectedRules && selectedRules.alarmRule.preRequsite.workflow">
      <div class="fc-blue-label pT20">PreRequsite Condition</div>
      <div>
        <div class="label-txt-black pT10">
          {{ selectedRules.alarmRule.preRequsite.workflow.resultEvaluator }}
        </div>
        <f-formula-builder
          v-model="selectedRules.alarmRule.preRequsite.workflow"
          module="thresholdrule"
          :title="''"
          :renderInLeft="true"
          :restrictInit="true"
          :showOnlyVarible="true"
          :assetCategory="{ id: this.selectedCategory[0].id }"
        ></f-formula-builder>
      </div>
    </div>
    <div class="fc-blue-label pT20">Clear Condition</div>
    <div class="fc-black-13 text-left pT5">Autoclear (Default)</div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import TemplateHelper from 'pages/firealarm/rules/templateHepler'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'

export default {
  props: ['details'],
  data() {
    return {
      id: null,
      templates: [],
      groupedPlaceHolder: [],
      selectedResourceList: [],
      selectedRules: null,
    }
  },
  mixins: [TemplateHelper],
  components: {
    FFormulaBuilder,
  },
  computed: {
    selectedCategory() {
      if (this.details && this.details.category) {
        let category = this.assetCategory.filter(
          d => d.name === this.details.category
        )
        return category
      }
      return null
    },
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  mounted() {
    if (this.details) {
      this.groupedPlaceHolder = this.placeHolderGroupFields()
      this.getRuleContext()
    }
    this.$store.dispatch('formulabuilder/loadAssetReadings', {
      assetCategoryId: this.selectedCategory[0].id,
    })
  },
  created() {},
  methods: {
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
        id: this.details.id,
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

    placeHolderGroupFields() {
      Object.keys(this.details.placeHolder).forEach(key => {
        this.templates.push(this.details.placeHolder[key])
      })
      return this.groupByField(this.templates, 'type')
    },
  },
}
</script>

<style lang="scss">
.fc-rule-template-contion {
  .ecm-option-container {
    padding: 10px 50px 10px 30px;
    flex-direction: row;
    .ecm-reporting-label {
      color: #4273e9;
    }
  }
  .fbVariableLegendContainer {
    padding-top: 20px;
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
    width: 646px;
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
  .fbVariableLegendContainer td.fbVariableLegendInfo {
    padding-left: 10px;
  }
}
</style>
