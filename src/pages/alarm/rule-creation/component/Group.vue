<template>
  <div class="grouping-container">
    <div class="flex-center-row-space">
      <div class="desc mT10 mB10 required">
        {{ $t('alarm.rules.group_name') }}
      </div>
      <div class="flex-center-row-space p5">
        <div class="delete-icon mL10 mT10 mB10 mR5" @click="deleteGroup()">
          <fc-icon
            group="default"
            name="trash-can"
            :color="iconsColor"
          ></fc-icon>
        </div>
        <el-switch ref="switch" v-model="defaultRCAGrouping.status">
        </el-switch>
      </div>
    </div>
    <el-input
      v-model="defaultRCAGrouping.name"
      class="fc-input-full-border2 pR75"
      placeholder="Group Name"
      :autofocus="true"
      :disabled="!defaultRCAGrouping.status"
    ></el-input>
    <div
      :class="{
        'fc-id bold pointer f14 pL5': !isV2,
        'fb-v2-text bold pointer pL5': isV2,
      }"
      class="header mT10 pT10"
    >
      {{ $t('alarm.rules.group_criteria') }}
    </div>
    <div class="desc mT20 mB20">
      {{ $t('alarm.rules.group_criteria_description') }}
    </div>
    <div class="desc">{{ $t('alarm.rules.criteria') }}</div>
    <RcaCriteriaBuilder
      class="pB20 pR75"
      v-model="defaultRCAGrouping.criteria"
      :moduleName="moduleName"
      :disabled="!defaultRCAGrouping.status"
    />
    <div
      :class="{
        'fc-id bold pointer f14 pL5': !isV2,
        'fb-v2-text bold pointer pL5': isV2,
      }"
      class="header mT10"
    >
      {{ $t('alarm.rules.group_conditions') }}
    </div>
    <div class="desc mT20">
      {{ $t('alarm.rules.rule_conditions_description') }}
    </div>
    <ConditionBuilder
      class="pB20 pR75"
      v-model="defaultRCAGrouping.conditions"
      :moduleName="moduleName"
      :disabled="disableCondition"
    />
  </div>
</template>
<script>
import { isEmpty } from 'util/validation'
import cloneDeep from 'lodash/cloneDeep'
import clone from 'lodash/clone'
import RcaCriteriaBuilder from './RcaCriteriaBuilder'
import ConditionBuilder from './ConditionBuilder.vue'

export default {
  name: 'Group',
  components: { ConditionBuilder, RcaCriteriaBuilder },
  props: {
    moduleName: {
      type: String,
      required: true,
    },
    group: {
      type: Object,
    },
    isEditForm: {
      type: Boolean,
      required: true,
    },
    isV2: { type: Boolean },
  },
  data() {
    return {
      defaultRCAGrouping: {
        id: null,
        name: '',
        criteria: {},
        conditions: {},
        status: true,
      },
      iconsColor: '#3fb4c3',
    }
  },
  computed: {
    disableCondition() {
      let { defaultRCAGrouping } = this
      let { status, criteria } = defaultRCAGrouping
      return !status || isEmpty(criteria)
    },
  },
  created() {
    this.prefillGroup()
  },
  watch: {
    defaultRCAGrouping: {
      handler() {
        this.serializeData()
      },
      deep: true,
    },
  },
  methods: {
    prefillGroup() {
      let { group } = this
      if (!isEmpty(group)) {
        if (this.isEditForm) {
          let tempGroup = {}
          this.defaultRCAGrouping = cloneDeep(group)
          let { conditions } = this.defaultRCAGrouping
          conditions.forEach((condition, index) => {
            let { criteria: { conditions } = {}, score } = condition || {}
            tempGroup[index + 1] = { ...conditions[1], scoring: score }
          })

          this.defaultRCAGrouping['conditions'] = {
            conditions: tempGroup,
            pattern: this.getDummyPattern(conditions.length),
          }
        } else {
          this.defaultRCAGrouping = cloneDeep(group)
        }
      }
    },
    getDummyPattern(length) {
      let pattern = '(1)'
      for (let i = 2; i <= length; i++) {
        pattern = '(' + pattern + ' and ' + i + ')'
      }
      return pattern
    },
    serializeData() {
      let { defaultRCAGrouping } = this
      let { id, name, criteria, conditions, status } = defaultRCAGrouping || {}
      let conditionsArray = this.createCriteriasFromEachCondition(conditions)
      let newRCAGrouping = {
        id,
        name,
        criteria,
        conditions: conditionsArray,
        status,
      }
      this.$emit('groupChanged', newRCAGrouping)
    },
    createCriteriasFromEachCondition(conditionsObj) {
      let conditionsArray = []
      let { conditions } = conditionsObj || {}
      for (const key in conditions) {
        let condition = clone(conditions[key])
        let { scoring: score } = condition || {}
        delete condition.scoring
        conditionsArray.push({
          criteria: {
            conditions: {
              1: { ...condition, moduleName: 'newreadingalarm' },
            },
          },
          score,
        })
      }

      return conditionsArray
    },
    async deleteGroup() {
      let value = await this.$dialog.confirm({
        title: `${this.$t('rule.create.delete_group')}`,
        message: `${this.$t('rule.create.delete_group_confirm')}`,
        rbDanger: true,
        rbLabel: this.$t('custommodules.list.delete'),
      })
      if (value) {
        let { group } = this
        this.$emit('deleteGroup', group)
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.required {
  color: #e6333d !important;
}
.status-switch {
  margin: 5px 10px 10px 10px;
}
.header {
  font-size: 12px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 1px;
  color: #ee508f;
}

.desc {
  font-size: 14px;
  line-height: normal;
  letter-spacing: 0.5px;
  color: #6b7e91;
}

.delete-icon {
  color: #3ab2c1;
  font-size: 20px;
  float: right;
  cursor: pointer;
}

.grouping-container {
  min-height: 428px;
  margin: 20px 0 0 1px;
  padding: 5px 0px 5px 18px;
  border-radius: 4px;
  border: solid 1px #d0d9e2;
}
</style>
