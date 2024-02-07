<template>
  <div class="mv-baseline-container d-flex flex-direction-column">
    <div class="header f12 bold mT30 mL30 mR30 d-flex">
      <div>
        <div class="text-uppercase">{{ expressionTitle }}</div>
        <div class="fc-heading-border-width43 mT15"></div>
      </div>
      <div
        v-if="isAdjustments && expressionArr.length > 0"
        class="mL-auto pointer"
        @click="addExpression"
      >
        <inline-svg
          src="svgs/add-circled"
          class="vertical-middle"
          iconClass="icon icon-sm icon-add mR5"
        ></inline-svg>
        <span
          v-if="!$validation.isEmpty(expressionBtnLabel)"
          class="btn-text f12 bold"
          >{{ expressionBtnLabel }}</span
        >
      </div>
    </div>
    <div class="mv-baseline-content" :class="[isAdjustments ? 'mB20' : 'mb5']">
      <div
        class="d-flex flex-direction-column text-center"
        v-if="$validation.isEmpty(expressionArr)"
      >
        <inline-svg
          src="svgs/emptystate/readings-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="mT10 empty-text f15 bold">
          No adjustments available here!
        </div>
        <div class="inline-block mT20">
          <el-button
            class="mv-adjustment-btn mv-adjustment-btn"
            @click="addExpression"
          >
            <span class="btn-label mL5 f12 bold">Add Adjustments</span>
          </el-button>
        </div>
      </div>
      <div
        v-else
        class="position-relative"
        v-for="(expression, index) in expressionArr"
        :ref="`expression-section-${index}`"
        :key="index"
      >
        <MVExpressionSection
          :isAdjustments="isAdjustments"
          :expression="expression"
          :index="index"
          :sectionTitle="sectionTitle"
          :sharedData="sharedData"
        ></MVExpressionSection>
        <div v-if="isAdjustments" @click="removeExpression(index)">
          <inline-svg
            src="svgs/minus-circled"
            class="vertical-middle icon-container"
            iconClass="icon icon-sm icon-remove mR5 white-bg"
          ></inline-svg>
        </div>
      </div>
    </div>
    <div class="d-flex mT-auto">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="pushData(false)"
        >Previous</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="pushData(true)"
        >Proceed to next</el-button
      >
    </div>
  </div>
</template>

<script>
import MVExpressionSection from 'pages/energy/mv/MVExpressionSection'
import { deepCloneObject } from 'util/utility-methods'
import { isEmpty, isArray } from '@facilio/utils/validation'

export default {
  components: {
    MVExpressionSection,
  },
  props: {
    expressionTitle: {
      type: String,
    },
    expressionBtnLabel: {
      type: String,
    },
    sectionTitle: {
      type: String,
    },
    isAdjustments: {
      type: Boolean,
    },
    sharedData: {
      type: Object,
    },
  },
  created() {
    let { isAdjustments, expressionArr, sharedData } = this
    let { adjustmentsResObj, baselinesResObj, projectId } = sharedData
    if (isAdjustments && !isEmpty(expressionArr)) {
      expressionArr[0].period = 1
    }
    // Handling for edit cases
    if (!isEmpty(projectId)) {
      let expArr = []
      if (isAdjustments) {
        adjustmentsResObj.forEach(adjustment => {
          let obj = this.deserializeObj(adjustment)
          expArr.push(obj)
        })
        this.$set(this, 'expressionArr', expArr)
      } else {
        let expArr = []
        baselinesResObj.forEach(baseline => {
          let obj = this.deserializeObj(baseline)
          expArr.push(obj)
        })
        this.$set(this, 'expressionArr', expArr)
      }
    }
  },
  data() {
    return {
      expressionArr: this.isAdjustments
        ? []
        : [
            {
              name: this.isAdjustments
                ? ''
                : `${this.$t('mv.summary.baseline_equation')}`,
              formulaField: {
                workflow: null,
              },
              isConstant: this.isAdjustments,
            },
          ],
    }
  },
  methods: {
    deserializeObj(obj) {
      let { isAdjustments } = this
      let finalObj = {
        formulaField: {},
      }
      if (isAdjustments) {
        finalObj.constant = obj.constant
        finalObj.isConstant = true
      }
      if (!isEmpty(obj.formulaField)) {
        finalObj.formulaField.workflow = obj.formulaField.workflow
        finalObj.formulaField.id = obj.formulaField.id
      }
      finalObj.name = obj.name
      finalObj.baseLinePeriod = [obj.startTime, obj.endTime]
      finalObj.id = obj.id
      return finalObj
    },
    pushData(canProceed = true) {
      let { expressionArr } = this
      let clonedExpressionArr = expressionArr.map(expression => {
        return deepCloneObject(expression)
      })
      let isValid = !this.validateExpressions()
      if (isValid) {
        clonedExpressionArr.forEach(arr => {
          if (arr.constant) {
            arr.constant = parseInt(arr.constant)
          }
          let { baseLinePeriod, isConstant } = arr
          if (!isEmpty(baseLinePeriod)) {
            arr.startTime = baseLinePeriod[0]
            arr.endTime = baseLinePeriod[1]
            delete arr.baseLinePeriod
          }
          if (isConstant) {
            delete arr.formulaField
            delete arr.isConstant
          } else {
            delete arr.isConstant
            delete arr.constant
          }
        })
        let data = {
          expressionArr: clonedExpressionArr,
          proceed: canProceed,
        }
        this.$emit('appendData', data)
      } else {
        this.$message.error('Please enter the mandatory fields')
      }
    },
    addExpression() {
      let { expressionArr, isAdjustments } = this
      let expressionObj = {
        formulaField: {
          workflow: null,
        },
        isConstant: isAdjustments,
      }
      if (isAdjustments) {
        expressionObj.period = 1
      }
      expressionArr.push(expressionObj)
      // Have to open this once layout issue is fixed
      // this.$nextTick(() => {
      //   let expressionSectionName = `expression-section-${expressionArrLength}`
      //   let newlyAddedExpression = this.$refs[expressionSectionName][0]
      //   newlyAddedExpression.scrollIntoView()
      // })
    },
    validateExpressions() {
      let { expressionArr } = this
      let isNotValid = expressionArr.some(
        ({
          baseLinePeriod,
          isConstant,
          formulaField: { workflow },
          constant,
          name,
        }) => {
          return (
            (!isConstant &&
              (isEmpty(workflow) ||
                (workflow.workflowUIMode === 2 &&
                  isEmpty(workflow.workflowString)) ||
                (workflow.workflowUIMode === 3 &&
                  isEmpty(workflow.resultEvaluator)))) ||
            (isConstant && isEmpty(constant)) ||
            !isArray(baseLinePeriod) ||
            isEmpty(baseLinePeriod) ||
            isEmpty(name)
          )
        }
      )
      return isNotValid
    },
    removeExpression(index) {
      let { expressionArr } = this
      expressionArr.splice(index, 1)
    },
  },
}
</script>

<style lang="scss">
.mv-baseline-container {
  overflow-y: scroll;
  scroll-behavior: smooth;
  height: calc(100vh - 150px);
  .header {
    letter-spacing: 1.6px;
    color: #324056;
    .btn-text {
      letter-spacing: 0.5px;
      color: #23b096;
    }
  }
  .empty-text {
    line-height: 1.44;
    color: #2c2c53;
  }
  .empty-text-desc {
    letter-spacing: 0.4px;
    color: #50526c;
  }
  .mv-adjustment-btn {
    border-radius: 3px;
    border: solid 1px #00b5c5;
    background-color: #ffffff;
    .btn-label {
      letter-spacing: 1px;
      color: #39b2c2;
    }
  }
  .mv-baseline-content {
    margin-top: 20px;
    .icon-container {
      position: absolute;
      top: calc(50%);
      right: 28px;
    }
  }
}
</style>
