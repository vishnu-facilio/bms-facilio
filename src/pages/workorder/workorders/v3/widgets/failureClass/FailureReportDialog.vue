<template>
  <el-dialog
    title="FAILURE REPORT"
    :visible="true"
    width="40%"
    :append-to-body="true"
    style="z-index: 9999999999;"
    class="dialog-body fc-dialog-center-container dialog-padding"
    :before-close="closeDialog"
  >
    <div class="label-txt-black line-height24 height364 overflow-y-scroll pB50">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm">
        <el-form-item prop="problem" label="Problem">
          <FLookupField
            :model.sync="problem"
            :field="problemField"
            @showLookupWizard="showLookUpWizard('failurecodeproblems', problem)"
          />
        </el-form-item>
        <el-form-item prop="cause" label="Cause">
          <input v-if="!problem" class="fc-input" :disabled="true" />
          <FLookupField
            v-else
            :key="problem"
            :model.sync="cause"
            :field="causeField"
            @showLookupWizard="showLookUpWizard('failurecodecauses', cause)"
          />
        </el-form-item>
        <el-form-item prop="remedy" label="Remedy">
          <input v-if="!cause || !problem" class="fc-input" :disabled="true" />
          <FLookupField
            v-else
            :key="cause"
            :model.sync="remedy"
            :field="remedyField"
            @showLookupWizard="showLookUpWizard('failurecoderemedies', remedy)"
          />
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel f13" @click="closeDialog()">{{
        $t('agent.agent.cancel')
      }}</el-button>
      <el-button
        class="modal-btn-save f13"
        @click="validateAndSave('ruleForm', problem)"
        >{{ $t('common.failure_class.save') }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import FLookupField from '@/forms/FLookupField'
const EQUALS_OPERATOR = 9
export default {
  props: ['selectedProblem', 'selectedCause', 'selectedRemedy', 'failureClass'],
  data() {
    return {
      problem: null,
      cause: null,
      remedy: null,
      ruleForm: {
        problem: '',
        cause: '',
        remedy: '',
      },
      rules: {
        problem: [
          {
            required: true,
            message: this.$t('common.failure_class.select_problem'),
            trigger: 'blur',
          },
        ],
      },
    }
  },
  created() {
    let { selectedProblem, selectedCause, selectedRemedy } = this
    this.problem = selectedProblem
    this.cause = selectedCause
    this.remedy = selectedRemedy
  },
  components: { FLookupField },
  watch: {
    problem: {
      handler(value) {
        if (!this.problem) {
          this.cause = null
          this.remedy = null
        } else {
          let ruleForm = this.$refs['ruleForm']
          if (ruleForm) ruleForm.clearValidate()
        }
      },
    },
    cause() {
      if (!this.cause) {
        this.remedy = null
      }
    },
  },
  computed: {
    problemField() {
      let { failureClass } = this || {}
      let currentField = this.getFailurefields(
        'failurecodeproblems',
        'Failure Code Problems',
        'Choose Problem'
      )
      if (failureClass) {
        let filters = {
          failureClass: {
            operatorId: EQUALS_OPERATOR,
            value: [`${this.failureClass?.id}`],
          },
        }
        currentField['filters'] = filters
      }

      return currentField
    },
    causeField() {
      let { problem } = this || {}
      let currentField = this.getFailurefields(
        'failurecodecauses',
        'Failure Code Causes',
        'Choose Cause'
      )
      if (problem) {
        let filters = {
          failureCodeProblems: {
            operatorId: EQUALS_OPERATOR,
            value: [`${this.problem}`],
          },
        }
        currentField['filters'] = filters
      }

      return currentField
    },
    remedyField() {
      let { cause } = this || {}
      let currentField = this.getFailurefields(
        'failurecoderemedies',
        'Failure Code Remedies',
        'Choose Remedy'
      )
      if (cause) {
        let filters = {
          failureCodeCauses: {
            operatorId: EQUALS_OPERATOR,
            value: [`${this.cause}`],
          },
        }
        currentField['filters'] = filters
      }

      return currentField
    },
  },
  methods: {
    showLookUpWizard(value, selectedItem) {
      this.$emit('showLookUpWizard', value, selectedItem)
    },
    validateAndSave(formName, selectedId) {
      this.$refs[formName].validate(valid => {
        if (selectedId) {
          let { problem, cause, remedy } = this
          this.$emit('savePCRToRelModule', problem, cause, remedy)
          this.$emit('closeDialog')
          this.$refs['ruleForm'].clearValidate()
        } else {
          return false
        }
      })
    },
    closeDialog() {
      this.$emit('closeDialog')
    },
    getFailurefields(lookupModuleName, displayName, placeHolder) {
      let currentField = {
        isDataLoading: false,
        options: [],
        lookupModuleName,
        field: {
          lookupModule: {
            name: lookupModuleName,
            displayName,
          },
        },
        multiple: false,
        forceFetchAlways: true,
        isDisabled: true,
        placeHolderText: placeHolder,
      }
      return currentField
    },
  },
}
</script>
<style scoped>
.fc-input {
  width: 100%;
  height: 40px !important;
  line-height: 40px !important;
  padding-left: 15px !important;
  padding-right: 15px !important;
  border-radius: 3px !important;
  background-color: #ffffff;
  border: solid 1px #d0d9e2 !important;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #324056;
  text-overflow: ellipsis;
  font-weight: 400;
  padding-right: 30px;
  white-space: nowrap;
}
</style>
