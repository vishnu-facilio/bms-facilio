<template>
  <div class="mT8">
    <div
      class="flex flex-nowrap "
      v-for="(row, rowIndex) in matrixQuestion.rowAnswer"
      :key="rowIndex"
    >
      <div
        v-for="(column, colIndex) in row.columnAnswer"
        :key="colIndex"
        :class="[
          rowIndex == 0 ? 'table-content-row' : 'table-header-row',
          'flex flex-nowrap flex-col justify-center items-center matrix-cell',
          getCellClass(rowIndex, colIndex),
          `${rowIndex}${colIndex}`,
        ]"
      >
        <template v-if="rowIndex === 0 || colIndex === 0">
          <p class="m-0  matrix-row-display-label" v-if="rowIndex != 0">
            {{ row.name }}
          </p>
          <p class="m-0  matrix-col-display-label" v-else-if="colIndex != 0">
            {{ column.name }}
          </p>
        </template>
        <template v-else>
          <div
            v-if="$validation.isEmpty(column.answer)"
            class="text-fc-grey  mT5 matrix-row-display-label"
            v-tippy
            :title="$t('qanda.response.no_answer')"
          >
            {{ $t('qanda.response.no_answer') }}
          </div>
          <div v-else class="answer break-word " v-tippy :title="column.answer">
            {{ column.answer }}
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'MatrixQuestionDisplay',
  props: ['answer', 'question'],
  computed: {
    matrixQuestion() {
      let { question } = this || {}
      let { rows, columns } = question || []
      let answer = []
      rows.forEach(row => {
        let rowObj = { row: row.id, columnAnswer: [{}], name: row.name }
        columns.forEach(column => {
          let prevColObj = this.getPrevColObj({ row: row.id, col: column.id })
          let { answer } = prevColObj || {}
          rowObj.columnAnswer.push({
            name: column.name,
            column: column.id,
            answer,
            field: column.field,
          })
        })
        answer.push(rowObj)
      })
      return {
        rowAnswer: [{ columnAnswer: [{}, ...columns] }, ...answer],
      }
    },
    matrixLength() {
      let { question } = this || {}
      let { rows } = question || {}
      let { columns } = question || {}
      let { length: columnLength } = columns || {}
      let { length: rowLength } = rows || {}
      return [rowLength + 1, columnLength + 1]
    },
  },
  methods: {
    getPrevColObj(props) {
      let { answer } = this || {}
      let { row, col } = props || {}
      let { rowAnswer } = answer || {}
      let currRowObj = (rowAnswer || []).find(rowObj => rowObj.row === row)
      let currColObj
      if (!isEmpty(currRowObj)) {
        let { columnAnswer } = currRowObj || {}
        currColObj = (columnAnswer || []).find(colObj => colObj.column === col)
      }
      return currColObj
    },
    getCellClass(row, col) {
      let { matrixLength } = this || {}
      let [rowLength, columnLength] = matrixLength || {}
      if (rowLength - 1 === row && columnLength - 1 === col) {
        return 'last-row-cell last-column-cell'
      } else if (rowLength - 1 === row) {
        return 'last-row-cell'
      } else if (columnLength - 1 === col) {
        return 'last-column-cell'
      } else {
        return ''
      }
    },
  },
}
</script>

<style scoped lang="scss">
$border-style: solid 1px #eceef4;
.matrix-cell {
  border-left: $border-style;
  border-top: $border-style;
}
.last-row-cell {
  border-bottom: $border-style;
}
.last-column-cell {
  border-right: $border-style;
}
.matrix-row-display-label,
.matrix-col-display-label {
  font-size: 14px;
}
.table-header-row {
  min-width: 200px;
  height: 100px;
  padding: 0.75rem;
}
.table-content-row {
  min-width: 200px;
  height: 50px;
  padding: 0.75rem;
}
.truncate-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.break-word {
  word-break: break-word;
  cursor: pointer;
}
</style>
