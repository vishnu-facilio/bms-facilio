<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="condition-animated slideInRight fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog condition-manager-form"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <el-form
      :model="namedCriteria"
      :rules="rules"
      :label-position="'top'"
      ref="condition-manager"
      class="fc-form"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('common._common.add_condition')
                : $t('common._common.edit_condition')
            }}
          </div>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <el-form-item :label="$t('common.products.name')" prop="name">
            <el-input
              class="width100 fc-input-full-border2"
              autofocus
              v-model="namedCriteria.name"
              type="text"
              :placeholder="$t('common._common.enter_crieteria_name')"
            />
          </el-form-item>

          <div class="criteria-types-config">
            <p class="fc-input-label-txt bold pB0 text-fc-pink text-uppercase">
              {{ $t('common.products.conditions') }}
            </p>

            <el-dropdown @command="openConditionExpressions">
              <div class="d-flex pointer">
                <inline-svg
                  src="svgs/plus-button"
                  class="fill-blue"
                ></inline-svg>
                <div class="config">{{ $t('common._common.configure') }}</div>
              </div>

              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(label, type) in criteriaType"
                  :key="type"
                  :command="parseInt(type)"
                >
                  {{ label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <div class="mT20">
            <template v-if="conditions.length > 1">
              <el-radio-group
                v-model="operatorType"
                @change="setPattern"
                class="mB15"
              >
                <el-radio class="fc-radio-btn" :label="operatorTypes.AND">
                  {{ $t('common._common.match_all') }}
                </el-radio>
                <el-radio class="fc-radio-btn" :label="operatorTypes.OR">
                  {{ $t('common._common.match_any') }}
                </el-radio>
                <el-radio class="fc-radio-btn" :label="operatorTypes.CUSTOM">
                  {{ $t('common._common.custom_pattern') }}
                </el-radio>
              </el-radio-group>

              <el-form-item
                v-if="operatorType === operatorTypes.CUSTOM"
                prop="pattern"
              >
                <el-input
                  :placeholder="
                    -$t('common._common.please_enter_pattern_for_conditions')
                  "
                  v-model="namedCriteria.pattern"
                  class="width100 fc-input-full-border2"
                >
                </el-input>
              </el-form-item>
            </template>

            <div
              v-for="(condition, conditionKey) in namedCriteria.conditions"
              :key="conditionKey"
              class="criteria-conditions"
            >
              <div class="flex-middle">
                <div class="criteria-alphabet-block alphabet-circle">
                  {{ conditionKey }}
                </div>

                <div class="fc__layout__has__row">
                  <div class="criteria-name">
                    {{ condition.name }}
                  </div>
                  <div class="criteria-type">
                    {{ criteriaType[condition.type] }}
                  </div>
                </div>
              </div>

              <div class="actions-visibility">
                <i
                  class="el-icon-edit edit-icon pR15 pointer"
                  data-arrow="true"
                  :title="$t('common.header.edit_expression')"
                  v-tippy
                  @click="editExpression(condition, conditionKey)"
                ></i>

                <i
                  class="el-icon-delete fc-delete-icon pointer"
                  data-arrow="true"
                  :title="$t('common.header.delete_expression')"
                  v-tippy
                  @click="deleteExpression(conditionKey)"
                ></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitForm()"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>

    <CriteriaBuilder
      v-if="showCriteriaBuilder"
      :module="moduleName"
      :selectedCriteria="activeCriteria"
      :expressionIdx="expressionIdx"
      @save="
        criteria => addConditionExpressions(criteria, criteriaTypes.SIMPLE)
      "
      @close="showCriteriaBuilder = false"
    ></CriteriaBuilder>

    <ScriptDialog
      v-if="showScript"
      :selectedScript="activeScript"
      :moduleName="moduleName"
      :expressionIdx="expressionIdx"
      @onSave="
        scriptData => addConditionExpressions(scriptData, criteriaTypes.SCRIPT)
      "
      @onClose="showScript = false"
    ></ScriptDialog>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import CriteriaBuilder from './CriteriaBuilderDialog'
import ScriptDialog from './ScriptDialog'
import flatten from 'lodash/flatten'

const criteriaTypes = {
  SIMPLE: 1,
  SCRIPT: 2,
}
const operatorTypes = {
  AND: 1,
  OR: 2,
  CUSTOM: 3,
}

export default {
  props: ['selectedConditon', 'isNew', 'moduleName'],
  components: { CriteriaBuilder, ScriptDialog },

  data() {
    return {
      namedCriteria: { name: null, pattern: null, conditions: {} },
      conditions: [],
      saving: false,
      criteriaType: {
        1: 'Simple',
        2: 'Script',
      },
      operatorType: 1,
      showCriteriaBuilder: false,
      showScript: false,
      activeCriteria: null,
      activeScript: null,
      activeConditionKey: null,
      expressionIdx: null,
      criteriaTypes,
      operatorTypes,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        pattern: {
          validator: function(rule, value, callback) {
            let { conditions } = this
            let usedOperator = flatten([...value.matchAll(/and|or/gm)]) //get operators ("and" & "or") alone
            let conditionLength = conditions.length
            let brackets = flatten([...value.matchAll(/\(|\)/gm)]) //get brackets alone
            let validBracketCount = 0

            brackets.forEach(b => {
              if (b === '(') {
                validBracketCount += 1
              } else if (b === ')') {
                validBracketCount -= 1
              }
            })

            if (
              conditionLength !== usedOperator.length + 1 ||
              validBracketCount !== 0
            ) {
              callback(
                new Error(this.$t('common._common.please_enter_valid_pattern'))
              )
            } else callback()
          }.bind(this),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    if (!this.isNew) {
      let { conditions, pattern } = this.selectedConditon
      let usedOperatorTypes = flatten([...pattern.matchAll(/and|or/gm)])

      if (usedOperatorTypes.every(t => t === 'and')) {
        this.operatorType = operatorTypes.AND
      } else if (usedOperatorTypes.every(t => t === 'or')) {
        this.operatorType = operatorTypes.OR
      } else {
        this.operatorType = operatorTypes.CUSTOM
      }

      this.conditions = Object.values(conditions)
      this.namedCriteria = this.selectedConditon
    }
  },

  watch: {
    conditions(newVal) {
      let { namedCriteria } = this
      let conditions = newVal.reduce((namedCriteria, condition, index) => {
        namedCriteria[index + 1] = condition
        return namedCriteria
      }, {})

      this.namedCriteria = { ...namedCriteria, conditions }
    },
  },

  methods: {
    openConditionExpressions(type) {
      let { SIMPLE, SCRIPT } = criteriaTypes

      this.activeCriteria = null
      this.activeScript = null
      this.expressionIdx = this.conditions.length + 1

      if (type === SIMPLE) {
        this.showCriteriaBuilder = true
      } else if (type === SCRIPT) {
        this.showScript = true
      }
    },
    addConditionExpressions(data, criteriaType) {
      let { SIMPLE, SCRIPT } = criteriaTypes
      let condition = {}

      if (criteriaType === SIMPLE) {
        condition = { type: SIMPLE, ...data }
      } else if (criteriaType === SCRIPT) {
        condition = { type: SCRIPT, ...data }
      }

      if (this.activeConditionKey) {
        let index = this.activeConditionKey - 1
        this.conditions.splice(index, 1, condition)
        this.activeConditionKey = null
      } else {
        this.conditions.push(condition)

        let conditionLength = this.conditions.length
        let newPattern = '(1)'

        if (conditionLength !== 1) {
          let { operatorType, namedCriteria } = this
          let { pattern } = namedCriteria || {}
          let { AND, CUSTOM } = operatorTypes
          let operator = [AND, CUSTOM].includes(operatorType) ? 'and' : 'or'
          let newCondition = ` ${operator} ${conditionLength})`
          newPattern = pattern.replace(/\)$/gm, newCondition)
        }

        this.$set(this.namedCriteria, 'pattern', newPattern)
      }
    },
    editExpression(condition, activeKey) {
      let { SIMPLE, SCRIPT } = criteriaTypes
      let { type } = condition

      if (type === SIMPLE) {
        this.activeCriteria = condition
        this.showCriteriaBuilder = true
      } else if (type === SCRIPT) {
        this.activeScript = condition
        this.showScript = true
      }

      this.activeConditionKey = parseInt(activeKey)
    },
    deleteExpression(conditionKey) {
      let index = conditionKey - 1
      this.conditions.splice(index, 1)

      let { operatorType } = this
      let patternConditions = Object.keys(this.conditions).map(
        c => parseInt(c) + 1
      )
      let { AND, CUSTOM } = operatorTypes
      let newPattern = ''

      if ([AND, CUSTOM].includes(operatorType)) {
        newPattern = `(${patternConditions.join(' and ')})`
      } else {
        newPattern = `(${patternConditions.join(' or ')})`
      }

      this.$set(this.namedCriteria, 'pattern', newPattern)
    },
    setPattern() {
      let { operatorType, namedCriteria } = this
      let { pattern } = namedCriteria
      let { AND, CUSTOM } = operatorTypes

      if ([AND, CUSTOM].includes(operatorType)) {
        pattern = pattern.replace(/and|or/gm, 'and')
      } else {
        pattern = pattern.replace(/and|or/gm, 'or')
      }

      this.$set(this.namedCriteria, 'pattern', pattern)
    },
    submitForm() {
      let { conditions, moduleName, namedCriteria } = this

      if (isEmpty(conditions)) {
        this.$message.error(
          this.$t('common.header.atleast_one_condition_should_configured')
        )
        return
      }

      this.$refs['condition-manager'].validate(async valid => {
        if (!valid) return false

        this.saving = true

        let url = 'v2/namedCriteria/addOrUpdate'
        let params = { moduleName, namedCriteria }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$message.success(
            this.$t('common.header.condition_saved_successfully')
          )
          this.$emit('onSave', data.namedCriteria)
          this.closeDialog()
        }
        this.saving = false
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.condition-manager-form {
  .criteria-types-config {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 30px;

    .fill-blue {
      svg {
        path {
          fill: #6171db;
        }
      }
    }
    .config {
      color: #6171db;
      margin-left: 5px;
      font-size: 13px;
      font-weight: 500;
      letter-spacing: 0.46px;
    }
  }
  .criteria-conditions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border: 1px solid #f4f5f7;
    box-shadow: 0 3px 5px #f4f5f7;
    margin: 15px 0px 10px;

    .criteria-name {
      font-weight: 400;
      color: #324056;
      letter-spacing: 0.5px;
      font-size: 14px;
    }
    .criteria-type {
      font-size: 12px;
      letter-spacing: 0.5px;
      color: #8ca0ad;
      margin-top: 5px;
    }
    .actions-visibility {
      visibility: hidden;
    }
  }
  .criteria-conditions:hover {
    box-shadow: none;
    border: 1px solid #38b2c1;

    .actions-visibility {
      visibility: visible;
    }
  }
}
.condition-animated {
  animation-duration: 0.3s;
  animation-fill-mode: both;
}
</style>
