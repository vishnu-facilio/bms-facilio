<template>
  <div class=" float-container pT20">
    <div
      class="d-flex pB10"
      v-for="(column, index) in questionResponse"
      :key="index"
      :class="[!canShowAnswer(column) && 'display-none']"
    >
      <div class="pT10 multi-col">
        {{ column.name }}
      </div>
      <div class="answer-column">
        {{ getColumnAnswer(column) }}
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'MultiQuestionDisplay',
  props: ['question'],
  created() {
    this.constructModel()
  },
  data() {
    return { questionResponse: [] }
  },
  methods: {
    constructModel() {
      let { question } = this
      let { answer, columns = [] } = question || {}
      let { answer: actualAnswer } = answer || {}
      let { rowAnswer } = actualAnswer || {}
      let { columnAnswer = [] } = rowAnswer[0] || {}
      let multiQuestionResponse = []

      multiQuestionResponse = columns.map(column => {
        let { name, id } = column || {}
        let answerObj = columnAnswer.find(answer => answer.column === id)
        let { answer = '' } = answerObj || {}

        return { name, answer, id }
      })

      this.questionResponse = multiQuestionResponse || []
    },
    canShowAnswer(column) {
      let { id: columnId } = column || {}
      let { question } = this
      let { columns } = question || {}
      let currAnsColumn = columns.find(col => {
        return col.id === columnId
      })
      let { displayLogicMeta } = currAnsColumn || {}

      if (!isEmpty(displayLogicMeta)) {
        let { action } = displayLogicMeta[0] || {}
        return action === 'hide' ? false : true
      }
      return true
    },
    getColumnAnswer(column) {
      let { answer } = column || {}
      if (this.isChooserType(column)) return this.getEnumAnswer(column)
      else return answer
    },
    isChooserType(colAnswer) {
      let { id: columnId } = colAnswer || {}
      let { question } = this
      let { columnVsFieldMap } = question || {}
      let currObj = columnVsFieldMap[columnId]
      let { dataType } = currObj || {}
      return dataType === 8 ? true : false
    },
    getEnumAnswer(colAnswer) {
      let { id: columnId, answer: answerIndex } = colAnswer || {}
      let { question } = this
      let { columnVsFieldMap } = question || {}
      let currObj = columnVsFieldMap[columnId] || {}
      let { values } = currObj || {}
      let actualValue = values.find(val => {
        return val.index === answerIndex
      })
      let { value } = actualValue || {}
      return value
    },
    canDisableColumnValue(column) {
      let { answer } = column || {}
      return answer.length <= 100 ? true : false
    },
    canDisableColumn(column) {
      let { name = null } = column || {}
      return name.length <= 30 ? true : false
    },
  },
}
</script>
<style scoped>
.display-none {
  display: none !important;
}
.answer-column {
  width: 75%;
  max-width: 75%;
  overflow: hidden;
  text-overflow: ellipsis;
  float: left;
  height: fit-content;
  min-height: 40px;
  margin: 0px 0px 15px 15px;
  padding: 10px;
  border: 1px solid rgb(174 170 170 / 51%);
  text-align: justify;
}
.multi-col {
  min-width: 250px;
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
<style lang="scss">
.multiquestion-pop {
  width: 600px;
}
</style>
