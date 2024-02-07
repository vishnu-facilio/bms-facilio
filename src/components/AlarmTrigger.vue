<template>
  <div>
    <el-collapse
      v-model="activeNames"
      :accordion="true"
      class="new-rule-collapse position-relative"
    >
      <el-collapse-item
        v-show="!trigger.isDeleted"
        v-for="(trigger, index) in triggerRule"
        :key="index"
        :name="trigger.name"
        class="pL10 pR20 pT10 pB10 rule-border-blue mT20 position-relative"
      >
        <template class="row" slot="title">
          {{ trigger.name }}
        </template>
        <div class="row">
          <div v-if="index > 0" class="fc-input-label-txt">
            Dependant On
            <el-select
              class="fc-input-full-border-select2 width10"
              filterable
              v-model="trigger.parentRuleName"
            >
              <el-option
                :key="'none'"
                :label="'None'"
                :value="null"
              ></el-option>
              <el-option
                v-if="index > position"
                v-for="(option, position) in triggerRule"
                :key="position"
                :label="option.name"
                :value="option.name"
              ></el-option>
            </el-select>
          </div>
          <div v-if="trigger.parentRuleName" class="fc-input-label-txt pL10">
            On Condition
            <el-select
              class="fc-input-full-border-select2 width10"
              filterable
              v-model="trigger.onSuccess"
            >
              <el-option :key="true" label="Match" :value="true"></el-option>
              <el-option
                :key="false"
                label="Not Match"
                :value="false"
              ></el-option>
            </el-select>
          </div>
        </div>
        <trigger-item
          :trigger.sync="trigger"
          @actions="data => updateTrigger(data, index)"
          :context="context"
          :emitData="emitData"
          :fields="fields"
          :metric="metric"
          :category="category"
          :isActions="isActions"
          :isCondition="isCondition"
          :showUpdate="showUpdate"
          :metricFields="metricFields"
          :isFormule="isFormule"
          :isUpdate="isUpdate"
        ></trigger-item>
        <img
          src="~assets/remove-icon.svg"
          style="height:18px;width:18px;margin-right: 3px;"
          @click="deleteTrigger(index)"
          class="delete-icon pointer"
        />
      </el-collapse-item>
    </el-collapse>
    <el-button class="add-border-btn bR3 mT20 mB10" @click="addTrigger"
      ><i class="el-icon-plus" style="font-weight: bold;margin-right: 4px;"></i
      >ADD {{ this.title }}</el-button
    >
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import TriggerItem from '@/TriggerItem'
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
    'isUpdate',
  ],
  components: { TriggerItem },
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
      triggerRule: [],
      activeNames: ['triggerName0'],
    }
  },
  watch: {
    emitData() {
      if (this.emitData) {
        this.saveTrigger()
      }
    },
  },
  mounted() {
    if (this.context && this.context.length > 0) {
      this.editTriggers()
    } else {
      this.addTrigger()
    }
  },
  methods: {
    addTrigger() {
      this.triggerRule.push({
        index: this.triggerRule.length + 1,
        thresholdType: 1,
        onSuccess: false,
        isChanged: true,
        parentRuleName: null,
        actions: [],
        alarmType: {
          message: '',
          problem: '',
          possibleCauses: '',
          recommendation: '',
        },
        name: this.title + ' ' + (this.triggerRule.length + 1),
      })
    },
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
    updateTrigger(data, index) {
      if (this.triggerRule.length - 1 === index) {
        this.$emit('triggerRule', this.triggerRule)
      } else {
        this.triggerRule[index].actions = data
      }
    },
    appendActions(data, index) {
      this.triggerRule[index].actions.push(data)
    },
    saveTrigger() {
      this.emitaction = true
    },
    deleteTrigger(key) {
      this.triggerRule[key].isDeleted = true
      this.$forceUpdate()
    },
  },
}
</script>
