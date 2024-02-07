<template>
  <div>
    <f-condition-type
      v-if="!isFormule"
      :modelData="trigger"
      :isSeverity="true"
      :metricFields="metricFields"
      :assetCategoryId="category.id"
    ></f-condition-type>
    <f-formula-builder
      class="pB20 rule-alarm-trigger"
      v-else
      style="padding-top: 20px;"
      title=""
      module="impact"
      v-model="trigger.workflow"
      :metric="selectedMetric"
      :assetCategory="{ id: category.id }"
    ></f-formula-builder>
    <alarm-rule-action
      v-if="isActions"
      @actions="data => update(data)"
      :showUpdate="showUpdate"
      :fields="fields"
      :model.sync="trigger.actions"
      :category="selectedCategory"
      :isSeverity="false"
      :isUpdate="isUpdate"
      :emitData="emitaction"
    ></alarm-rule-action>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import FConditionType from '@/FConditionType'
import AlarmRuleAction from '@/workflow/AlarmRuleAction'
export default {
  props: [
    'context',
    'emitData',
    'fields',
    'metric',
    'category',
    'title',
    'isActions',
    'isCondition',
    'showUpdate',
    'metricFields',
    'isFormule',
    'trigger',
    'isUpdate',
  ],
  components: { FConditionType, AlarmRuleAction, FFormulaBuilder },
  computed: {
    ...mapState({
      alarmseverity: state => state.alarmSeverity,
    }),
    ...mapGetters(['getAssetCategory']),
    selectedMetric() {
      return this.metric
    },
    selectedCategory() {
      return this.getAssetCategory(this.category)
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
  },
  data() {
    return {
      emitaction: false,
      isHistory: true,
      triggerRule: [],
      activeNames: ['triggerName0'],
    }
  },
  watch: {
    trigger: {
      handler(newVal, oldVal) {
        if (this.isHistory) {
          this.trigger.isChanged = true
        }
      },
      deep: true,
    },
    model: {
      handler(newVal, oldVal) {
        if (this.isHistory) {
          this.trigger.isChanged = true
        }
      },
      deep: true,
    },
    context() {
      this.editTriggers()
    },
    emitData() {
      if (this.emitData) {
        this.isHistory = false
        this.saveTrigger()
      }
    },
  },
  methods: {
    changedObject(trigger) {},
    editTriggers() {
      this.triggerRule = []
      if (this.context) {
        this.context.forEach(d => {
          d.alarmType = {
            message: '',
            problem: '',
            possibleCauses: '',
            recommendation: '',
          }
          if (
            d.actions &&
            d.actions[0] &&
            d.actions[0].template &&
            d.actions[0].template.originalTemplate
          ) {
            d.alarmType.problem = d.actions[0].template.originalTemplate.problem
            d.alarmType.possibleCauses =
              d.actions[0].template.originalTemplate.possibleCauses
            d.alarmType.recommendation =
              d.actions[0].template.originalTemplate.recommendation
          }
          this.triggerRule.push(d)
        })
        this.$forceUpdate()
      }
    },
    update(data) {
      this.$emit('actions', data)
    },
    appendActions(data, index) {
      this.triggerRule[index].actions.push(data)
    },
    saveTrigger() {
      this.emitaction = true
    },
    deleteTrigger(key) {
      this.$delete(this.triggerRule, key)
    },
  },
}
</script>
