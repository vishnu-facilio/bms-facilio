<template>
  <f-dialog
    v-if="dialogVisibility"
    :append-to-body="true"
    :visible.sync="dialogVisibility"
    width="58%"
    maxHeight="300px"
    title="Set Criteria"
    @save="saveRule"
    @close="dialogVisibility = false"
    :stayOnSave="true"
  >
    <div class="height330 overflow-y-scroll pB50">
      <CriteriaBuilder
        v-if="dialogVisibility"
        v-model="criteria"
        :moduleName="lookupModuleName"
        :customValues="customValues"
        :showValueType="true"
      >
      </CriteriaBuilder>
    </div>
  </f-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import FDialog from '@/FDialogNew'
import Constants from 'util/constant'
import { CriteriaBuilder } from '@facilio/criteria'

export default {
  components: {
    FDialog,
    CriteriaBuilder,
  },
  data() {
    return {
      criteria: null,
      customValues: [
        {
          allowCreate: true,
          options: [],
          label: `${this.$t('forms.builder.field')}`,
          value: 'fields',
          matcher: value => {
            let regexArray = String(value).match(/\$\{(.*?)\}/)
            if (!isEmpty(regexArray)) return true
            return false
          },
        },
      ],
    }
  },
  props: [
    'formId',
    'moduleName',
    'visibility',
    'lookupFieldsList',
    'fieldObject',
    'existingRule',
    'isSubForm',
    'subFormId',
    'options',
  ],
  created() {
    this.constructCriteriaFromExistingRule()
    this.customValues[0].options = (this.lookupFieldsList || [])
      .map(option => {
        let { displayName, name, field } = option || {}

        if (displayName) {
          let value = `\${${this.moduleName}.${name}}`

          if (field?.dataTypeEnum === 'LOOKUP')
            value = `\${${this.moduleName}.${name}.id}`

          return { label: displayName, value }
        }
        return null
      })
      .filter(data => !isEmpty(data))
  },
  computed: {
    dialogVisibility: {
      get() {
        return this.visibility
      },
      set(val) {
        this.$emit('update:visibility', val)
      },
    },
    lookupModuleName() {
      let { lookupModuleName } = this.fieldObject || {}
      return lookupModuleName
    },
  },
  methods: {
    constructCriteriaFromExistingRule() {
      let { existingRule } = this
      if (!isEmpty(existingRule)) {
        let { actions } = existingRule || {}
        let formRuleActionField = actions[0].formRuleActionFieldsContext[0]
        if (!isEmpty(formRuleActionField)) {
          this.criteria = formRuleActionField.criteria
        }
      }
    },
    getLookupModuleName() {
      let { fieldObject } = this
      let { field: fieldObj } = fieldObject
      if (!isEmpty(fieldObj)) {
        let { lookupModule } = fieldObj
        let { config } = fieldObject || {}
        let {
          filterValue,
          isFiltersEnabled,
          lookupModuleName: configLookupModuleName,
        } = config || {}
        if (isFiltersEnabled) {
          let moduleName =
            configLookupModuleName || Constants.LOOKUP_FILTERS_MAP[filterValue]
          return moduleName
        } else if (!isEmpty(lookupModule)) {
          return lookupModule.name
        }
      }
      return ''
    },
    updateCriteria(value) {
      this.$set(this, 'criteria', value)
    },
    saveRule() {
      let { criteria, moduleName, isSubForm, subFormId } = this
      let triggerFields = []
      if (!isEmpty(criteria)) {
        for (let key in criteria.conditions) {
          let ruleForField
          let criteriaForField
          if (
            !isEmpty(criteria?.conditions[key]?.value) &&
            criteria.conditions[key].value.includes('${' + moduleName + '.')
          ) {
            ruleForField = criteria.conditions[key].value
            ruleForField = ruleForField.replace('${' + moduleName + '.', '')
            ruleForField = ruleForField.replace('data.', '')
            ruleForField = ruleForField.replace('}', '')
            ruleForField = ruleForField.replace('.id', '')
          }
          if (!isEmpty(ruleForField)) {
            criteriaForField = this.lookupFieldsList.find(
              field => field.name === ruleForField
            )
          }
          if (!isEmpty(criteriaForField)) {
            let triggerFieldObj = {
              fieldId: criteriaForField.id ? criteriaForField.id : '',
            }
            if (!isEmpty(this.existingRule)) {
              triggerFieldObj.ruleId = this.existingRule.id
            }
            triggerFields.push(triggerFieldObj)
          }
        }

        let { formId } = this

        let formRuleContext = {
          name: 'Set Filter Rule',
          formId: parseInt(formId),
          type: 2,
          triggerType: 2,
          ruleType: 1,
          actions: [
            {
              actionType: 6,
              formRuleActionFieldsContext: [
                {
                  criteria: criteria,
                  formFieldId: this.fieldObject.id,
                },
              ],
            },
          ],
        }
        if (!isEmpty(triggerFields)) {
          formRuleContext.triggerFields = triggerFields
        } else {
          formRuleContext.triggerType = 1
        }
        if (isSubForm) {
          formRuleContext = { ...formRuleContext, subFormId, triggerType: 4 }
        }
        let url = !isEmpty(this.existingRule)
          ? `v2/form/rule/update`
          : `v2/form/rule/add`
        if (!isEmpty(this.existingRule)) {
          formRuleContext.id = this.existingRule.id
          formRuleContext.actions[0].formRuleId = this.existingRule.id
          formRuleContext.actions[0].id = this.existingRule.actions[0].id
        }
        this.$http
          .post(url, {
            formRuleContext,
          })
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.$message.success('Form Rule saved')
              this.$emit('update:visibility', false)
              this.dialogVisibility = false
              this.$emit('saved')
            }
          })
          .catch(() => {
            this.$message.error('Could not save form rule')
          })
      }
    },
  },
}
</script>
