<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner :error="error"></error-banner>
    <el-form :model="eventRule" :label-position="'top'" ref="ruleForm">
      <div>
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isNew ? 'New Event Filter' : 'Edit Event Filter' }}
            </div>
          </div>
        </div>

        <div class="new-body-modal">
          <el-row :gutter="20">
            <el-col :span="24">
              <p class="fc-input-label-txt">Name</p>
              <el-form-item prop="name">
                <el-input
                  v-model="eventRule.name"
                  placeholder="New Event Name"
                  class="fc-input-full-border-select2 width100"
                  autocomplete="off"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <p class="fc-input-label-txt">Description</p>
            <el-input
              v-model="eventRule.description"
              type="textarea"
              resize="none"
              :autosize="{ minRows: 4, maxRows: 4 }"
              :placeholder="$t('common._common.enter_desc')"
              class="fc-input-full-border-select2 width100"
            ></el-input>
          </el-row>

          <div class="setup-input-block mT20">
            <p class="fc-input-label-txt">Condition Type</p>
            <el-radio-group v-model="conditionType">
              <el-radio key="1" :label="1" class="fc-radio-btn"
                >Simple Condition</el-radio
              >
              <el-radio :key="5" :label="2" class="fc-radio-btn"
                >Complex Condition</el-radio
              >
            </el-radio-group>
          </div>

          <div v-if="conditionType === 1" class="form-input mT40">
            <new-criteria-builder
              v-model="eventRule.criteria"
              :exrule="eventRule.criteria"
              @condition="somefnt"
              :module="
                this.$helpers.isLicenseEnabled('NEW_ALARMS')
                  ? 'bmsevent'
                  : 'event'
              "
              :title="'Specify rules for event filtering'"
            ></new-criteria-builder>
          </div>
          <f-formula-builder
            v-else
            style="padding-top: 20px"
            module="thresholdrule"
            v-model="eventRule.workflow"
          ></f-formula-builder>

          <div class="setup-input-block">
            <p class="fc-modal-sub-title">Success Action</p>
            <p class="fc-sub-title-desc">Specify rules for the reading</p>
            <el-radio-group v-model="eventRule.successAction" class="mT20">
              <el-radio :label="1" class="fc-radio-btn">Ignore Event</el-radio>
              <el-radio :label="2" class="fc-radio-btn"
                >Transform Event</el-radio
              >
            </el-radio-group>

            <div v-if="eventRule.successAction == 2" class="mT20">
              <el-row align="middle" style="margin: 0px; padding-top: 20px">
                <el-col
                  :span="20"
                  style="padding-right: 35px; padding-left: 0px"
                >
                  <div class="fc-input-label-txt">Problem</div>
                  <div class="add">
                    <el-input
                      v-model="problem"
                      class="fc-input-full-border-select2 mT10"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row align="middle" style="margin: 0px; padding-top: 20px">
                <el-col :span="20">
                  <div class="fc-input-label-txt pT20">Possible Causes</div>
                  <div class="add textarea-height-set">
                    <el-input
                      v-model="possibleCauses"
                      placeholder=""
                      class="fc-input-full-border-select2"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                      resize="none"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row align="middle" style="margin: 0px; padding-top: 20px">
                <el-col :span="20">
                  <div class="fc-input-label-txt">Recommendations</div>
                  <div class="add textarea-height-set">
                    <el-input
                      v-model="recommendation"
                      placeholder=""
                      class="fc-input-full-border-select2"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                      resize="none"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <field-matcher
                v-model="eventRule.fieldMatcher"
                :fields="fieldOptions"
              ></field-matcher>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button @click="addEventRule('ruleForm')" class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import FieldMatcher from '@/FieldMatcher2'
import ErrorBanner from '@/ErrorBanner'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
export default {
  props: ['selectedEventRule', 'isNew', 'visibility'],
  data() {
    return {
      error: false,
      conditionType: 1,
      eventRule: {
        name: null,
        description: null,
        executionOrder: 1,
        criteria: null,
        workflow: null,
        successAction: 1,
        transformJson: null,
        fieldMatcher: [],
      },
      problem: null,
      possibleCauses: null,
      recommendation: null,
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter event name',
            trigger: 'blur',
          },
        ],
        description: [
          {
            required: true,
            message: 'Please enter description',
            trigger: 'blur',
          },
        ],
      },
      fieldOptions: [
        'source',
        'condition',
        'eventMessage',
        'severity',
        'alarmType',
      ],
    }
  },
  components: {
    FieldMatcher,
    ErrorBanner,
    NewCriteriaBuilder,
    FFormulaBuilder,
  },
  created() {
    this.initEventRule()
  },
  methods: {
    somefnt(newVal) {
      this.eventRule.criteria = newVal
    },
    initEventRule: function() {
      if (!this.isNew) {
        let self = this
        self.loading = true
        self.$http
          .get('/event/geteventrule?id=' + this.selectedEventRule.id)
          .then(function(response) {
            self.eventRule = self.$helpers.copy(self.eventRule, response.data)
            self.eventRule.id = response.data.id
            if (response.data.workflow) {
              self.conditionType = 2
            }
            self.setFieldMatcherFromTemplate(response.data.transformTemplate)
            self.loading = false
          })
      } else {
        this.eventRule = {
          name: null,
          description: null,
          executionOrder: 1,
          criteria: null,
          workflow: null,
          successAction: 1,
          transformJson: null,
          fieldMatcher: [],
        }
        this.problem = null
        this.possibleCauses = null
        this.recommendation = null
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    convertFieldMatcher(fieldMatcher) {
      let transformJson = {}
      for (let i = 0; i < fieldMatcher.length; i++) {
        let matcher = fieldMatcher[i]
        transformJson[matcher.field] = matcher.value
      }
      return transformJson
    },
    setFieldMatcherFromTemplate(template) {
      if (template && template.originalTemplate) {
        this.eventRule.fieldMatcher = []
        Object.keys(template.originalTemplate).forEach(key => {
          if (['problem', 'possibleCauses', 'recommendation'].includes(key)) {
            this[key] = template.originalTemplate[key]
          } else {
            this.eventRule.fieldMatcher.push({
              field: key,
              value: template.originalTemplate[key],
            })
          }
        })
      }
    },
    addEventRule(ruleForm) {
      this.$refs[ruleForm].validate(valid => {
        if (!valid) {
          this.error = true
          return false
        }
      })
      let data = this.$helpers.cloneObject(this.eventRule)
      if (data.successAction === 2) {
        data.fieldMatcher.push(
          { field: 'problem', value: this.problem },
          { field: 'possibleCauses', value: this.possibleCauses },
          { field: 'recommendation', value: this.recommendation }
        )
        if (data.fieldMatcher && data.fieldMatcher.length) {
          data.transformJson = this.convertFieldMatcher(data.fieldMatcher)
        }
      } else {
        delete data.fieldMatcher
      }

      if (this.conditionType === 1) {
        delete data.workflow
      } else {
        delete data.criteria
      }

      if (!this.isNew) {
        this.updateEventRule(data)
      } else {
        let self = this
        this.$http
          .post('/event/addEventRule', {
            eventRule: data,
          })
          .then(function(response) {
            self.$message.success('Rule added sucessfully')
            self.$emit('saved', true)
            self.closeDialog()
          })
          .catch(function(error) {
            console.log(error)
          })
      }
    },
    updateEventRule(data) {
      let self = this
      if (data.criteria) {
        for (let key in data.criteria.conditions) {
          let condition = data.criteria.conditions[key]
          delete condition.operator
          data.criteria.conditions[key] = condition
        }
        delete data.criteria.regEx
      }

      this.$http
        .post('/event/updateEventRule', {
          eventRule: data,
        })
        .then(function(response) {
          self.$message.success('Rule updated sucessfully')
          self.$emit('saved', true)
          self.closeDialog()
        })
        .catch(function(error) {
          console.log(error)
        })
    },
  },
}
</script>
<style>
.fc-create-record {
  width: 50% !important;
}
</style>
