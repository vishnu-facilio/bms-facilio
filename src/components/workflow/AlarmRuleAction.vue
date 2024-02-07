<template>
  <div>
    <div v-if="templateAction && templateAction.templateJson">
      <div v-if="!showUpdate">
        <div class="fc-text-pink mT10">
          ALARM DETAILS
        </div>
        <el-form-item :label="'Severity'" v-if="isSeverity">
          <el-select
            class="fc-input-full-border-select2 width100"
            v-model="templateAction.templateJson.fieldMatcher.severity"
            collapse-tags
          >
            <el-option
              v-for="label in alarmseverity"
              :key="label.id"
              :label="label.displayName"
              :value="label.severity"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :rules="[{ required: true, message: 'Alarm Message is required' }]"
          v-if="isSeverity"
        >
          <div class="fc-red-txt14 mT10">Alarm Message</div>
          <el-input
            v-model="templateAction.templateJson.fieldMatcher.message"
            autofocus
            placeholder="Alarm Message"
            class="fc-input-full-border2"
          ></el-input>
        </el-form-item>
        <el-form-item :label="'Problem'">
          <el-input
            type="textarea"
            v-model="templateAction.templateJson.fieldMatcher.problem"
            autofocus
            placeholder="Enter Problem"
            :autosize="{ minRows: 3, maxRows: 4 }"
            resize="none"
            class="fc-input-full-border-textarea mT10"
          ></el-input>
        </el-form-item>
        <el-form-item :label="'Possible Causes'">
          <el-input
            type="textarea"
            v-model="templateAction.templateJson.fieldMatcher.possibleCauses"
            autofocus
            :autosize="{ minRows: 3, maxRows: 4 }"
            resize="none"
            placeholder="Enter Possible Causes"
            class="fc-input-full-border-textarea mT10"
          ></el-input>
        </el-form-item>
        <el-form-item :label="'Recommendation'">
          <el-input
            type="textarea"
            v-model="templateAction.templateJson.fieldMatcher.recommendation"
            :autosize="{ minRows: 3, maxRows: 4 }"
            resize="none"
            autofocus
            placeholder="Enter Recommendation"
            class="fc-input-full-border-textarea mT10"
          ></el-input>
        </el-form-item>
      </div>
      <!-- Update Action -->
      <f-field-update
        v-if="showUpdate"
        :isServerity="true"
        :fields="customFields"
        @updateActions="appendActions($event, index)"
        v-model="actions"
        @actions="data => addUpdateAction(data)"
        :emitData="updateField"
        :model.sync="actions"
        :editField="model"
        :category="selectedCategory"
      ></f-field-update>
    </div>
  </div>
</template>
<script>
import FFieldUpdate from '@/workflow/FFieldUpdate'
import { mapState } from 'vuex'
import WorkFlowMixin from '@/mixins/WorkFlowMixin'

export default {
  mixins: [WorkFlowMixin],
  components: { FFieldUpdate },
  data() {
    return {
      isChanged: false,
      templateAction: {
        actionType: 6,
        templateJson: {
          fieldMatcher: {
            problem: null,
            message: null,
            possibleCauses: null,
            severity: null,
            recommendation: null,
          },
        },
      },
      actions: [],
      updateField: false,
    }
  },
  props: [
    'model',
    'metric',
    'category',
    'showUpdate',
    'emitData',
    'isSeverity',
    'fields',
    'isUpdate',
  ],
  computed: {
    ...mapState({
      alarmseverity: state => state.alarmSeverity,
    }),
    selectedCategory() {
      return this.category
    },
    customFields() {
      return this.fields
    },
  },
  watch: {
    emitData() {
      if (this.emitData) {
        this.saveAction()
      }
    },
    model: {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.parseTemplate(newVal)
        }
      },
    },
    'templateAction.templateJson.fieldMatcher': {
      handler() {
        if (this.isUpdate) {
          this.isChanged = true
        }
      },
      deep: true,
    },
  },
  mounted() {
    if (this.model && this.model.length > 0) {
      this.parseTemplate(this.model)
    }
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    parseTemplate() {
      if (this.model) {
        this.model.forEach(d => {
          if (d.actionType === 6) {
            if (d.template && d.template.originalTemplate) {
              this.templateAction.templateJson.fieldMatcher.severity =
                d.template.originalTemplate.severity
              this.templateAction.templateJson.fieldMatcher.message =
                d.template.originalTemplate.message
              this.templateAction.templateJson.fieldMatcher.problem =
                d.template.originalTemplate.problem
              this.templateAction.templateJson.fieldMatcher.possibleCauses =
                d.template.originalTemplate.possibleCauses
              this.templateAction.templateJson.fieldMatcher.recommendation =
                d.template.originalTemplate.recommendation
            } else if (d.templateJson !== null) {
              d.templateJson.fieldMatcher.forEach(d => {
                this.templateAction.templateJson.fieldMatcher[d.field] = d.value
              })
            }
          }
          this.$forceUpdate()
        })
      }
    },
    appendActions(data) {
      this.actions.push(data)
    },
    addUpdateAction(data) {
      this.$emit('update:model', data)
      this.$emit('actions', data)
    },
    saveAction() {
      if (this.showUpdate) {
        this.updateField = true
      } else {
        if (!this.isSeverity) {
          delete this.templateAction.templateJson.fieldMatcher.severity
        }
        let actions = this.parseAction(this.templateAction)
        actions.isChanged = this.isChanged
        this.$emit('update:model', [actions])
        this.$emit('actions', [actions])
      }
    },
  },
}
</script>
