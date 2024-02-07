<template>
  <div class="mT20 pB20">
    <div class="fc-text-pink2 mT20">SAFE LIMIT</div>
    <p class="grey-text2 pT5">
      {{ $t('setup.users_management.specify_rules_for_reading') }}
    </p>
    <el-row class="mT20">
      <el-col :span="6" class="mT10">
        <el-radio
          @change="clearData(field.safeLimitPattern)"
          v-model="field.safeLimitPattern"
          label="none"
          class="fc-radio-btn fc-radio-btn2"
        >
          {{ $t('maintenance.wr_list.none') }}
        </el-radio>
      </el-col>
    </el-row>
    <el-row class="mT10">
      <el-col :span="7" class="mT10">
        <el-radio
          @change="clearData(field.safeLimitPattern)"
          v-model="field.safeLimitPattern"
          label="greater"
          class="fc-radio-btn fc-radio-btn2"
          >Greater Than</el-radio
        >
      </el-col>
      <el-col v-if="field.safeLimitPattern === 'greater'" :span="6">
        <el-input
          v-model="field.greaterThan"
          class="fc-input-full-border2"
        ></el-input>
      </el-col>
    </el-row>
    <el-row class="mT10">
      <el-col :span="7" class="mT10">
        <el-radio
          v-model="field.safeLimitPattern"
          @change="clearData(field.safeLimitPattern)"
          label="lesser"
          class="fc-radio-btn fc-radio-btn2"
          >Lesser Than</el-radio
        >
      </el-col>
      <el-col v-if="field.safeLimitPattern === 'lesser'" :span="6">
        <el-input
          v-model="field.lesserThan"
          class="fc-input-full-border2"
        ></el-input>
      </el-col>
    </el-row>
    <el-row class="mT10">
      <el-col :span="6" class="mT10">
        <el-radio
          v-model="field.safeLimitPattern"
          @change="clearData(field.safeLimitPattern)"
          label="between"
          class="fc-radio-btn fc-radio-btn2"
          >Between</el-radio
        >
      </el-col>
      <div v-if="field.safeLimitPattern === 'between'">
        <el-col :span="6">
          <el-input
            v-model="field.betweenFrom"
            class="fc-input-full-border2"
          ></el-input>
        </el-col>
        <el-col :span="2" style="margin-top: 18px;text-align: center;">
          <span class="label-txt-black">to</span>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="field.betweenTo"
            class="fc-input-full-border2"
          ></el-input>
        </el-col>
      </div>
    </el-row>
    <div class="mT30">
      <el-row>
        <el-col :span="7" class="el-checkbox-container mT10">
          <el-checkbox v-model="field.raiseSafeLimitAlarm"
            >Raise Alarm</el-checkbox
          >
        </el-col>
        <el-col :span="7" class="mT10">
          <div class="label-txt2">Alarm Severity</div>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="field.safeLimitSeverity"
            placeholder="Select"
            class="fc-input-full-border-select2"
          >
            <el-option
              v-for="item in alarmSeverity"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  props: ['value', 'edit'],
  data() {
    return {
      field: {
        safeLimitId: -1,
        raiseSafeLimitAlarm: false,
        displayName: '',
        dataType: 1,
        lesserThan: null,
        greaterThan: null,
        betweenTo: null,
        betweenFrom: null,
        safeLimitPattern: 'none',
        safeLimitSeverity: 'Minor',
      },
      alarmSeverity: ['Critical', 'Major', 'Minor'],
    }
  },
  mounted() {
    this.$helpers.extend(this.field, this.value)
    if (this.edit) {
      this.editReadingRules(this.value)
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  watch: {
    field: {
      handler(val) {
        this.$emit('input', this.field)
      },
      deep: true,
    },
  },
  methods: {
    editReadingRules(fields) {
      this.field.raiseSafeLimitAlarm = false
      this.field.safeLimitSeverity = 'Minor'
      this.field.lesserThan = null
      this.field.greaterThan = null
      this.field.betweenTo = null
      this.field.betweenFrom = null
      this.field.safeLimitPattern = 'none'
      if (fields.readingRules) {
        fields.readingRules.forEach(rule => {
          if (rule.workflow.resultEvaluator === '(b!=-1&&a<b)||(c!=-1&&a>c)') {
            let min = rule.workflow.expressions.find(e => e.name === 'b')
              .constant
            let max = rule.workflow.expressions.find(e => e.name === 'c')
              .constant
            if (min === '-1' && max === '-1') {
              this.field.safeLimitPattern = 'none'
            } else if (min === '-1') {
              this.field.safeLimitPattern = 'lesser'
              this.field.lesserThan = max
            } else if (max === '-1') {
              this.field.safeLimitPattern = 'greater'
              this.field.greaterThan = min
            } else {
              this.field.safeLimitPattern = 'between'
              this.field.betweenTo = max
              this.field.betweenFrom = min
            }
            this.field.safeLimitId = rule.id || -1
            if (rule.actions) {
              this.field.safeLimitSeverity =
                rule.actions[0].template.originalTemplate.severity
              this.field.raiseSafeLimitAlarm = true
            }
          }
        })
      }
    },
    clearData(safeLimitPattern) {
      if (safeLimitPattern === 'none') {
        this.field.lesserThan = null
        this.field.greaterThan = null
        this.field.betweenFrom = null
        this.field.betweenTo = null
      } else if (safeLimitPattern === 'lesser') {
        this.field.greaterThan = null
        this.field.betweenFrom = null
        this.field.betweenTo = null
      } else if (safeLimitPattern === 'greater') {
        this.field.lesserThan = null
        this.field.betweenFrom = null
        this.field.betweenTo = null
      } else if (safeLimitPattern === 'between') {
        this.field.lesserThan = null
        this.field.greaterThan = null
      }
    },
  },
}
</script>
