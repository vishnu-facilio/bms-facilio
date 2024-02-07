<template>
  <el-dialog
    :title="question.question"
    :visible.sync="openResponses"
    width="40%"
    :before-close="closeDialog"
    class="inspection-response-dialog"
  >
    <div v-if="loading" class="flex">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else-if="$validation.isEmpty(answers)" class="fc-empty-center">
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="page-name mT10">
        {{ $t('qanda.template.no_answers') }}
      </div>
    </div>
    <div v-else>
      <div class="mB10 d-flex flex-col">
        <div class="font-medium f12 mB10 text-fc-grey">
          {{ $t('qanda.template.response_dialog_desc') }}, {{ timeRange }}
        </div>
        <div class="fc-text-pink2">{{ $t('qanda.template.answers') }}</div>
      </div>
      <template>
        <div
          v-for="(answer, index) in answers"
          class="f14 mT5 pT5 pB5"
          :key="answer.id"
        >
          <div class="break-word">
            {{ index + 1 }}.
            {{ getAnswer(answer) }}
          </div>
          <div class="view-response" @click="redirectToResponse(answer)">
            {{ $t('qanda.template.view_inspection') }}
          </div>
        </div>
      </template>
    </div>
  </el-dialog>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import getProperty from 'dlv'

const { getOrgMoment: moment } = helpers || {}

const QUESTION_TYPE_ANSWER_RENDERER = {
  MULTIPLE_CHOICE_MANY(answer) {
    return answer.other
  },
  MULTIPLE_CHOICE_ONE(answer) {
    return answer.other
  },
  BOOLEAN(answer, question) {
    let { trueLabel, falseLabel } = question || {}
    return answer.answer ? trueLabel : falseLabel
  },
  DATE_TIME(answer, question) {
    if (question.showTime) {
      return moment(answer.answer).format('HH:mm a - DD MMM, YYYY')
    } else {
      return moment(answer.answer).format('DD MMM, YYYY')
    }
  },
  FILE_UPLOAD(answer) {
    let fileName = getProperty(answer, 'answer.fileName')
    return fileName
  },
  MULTI_FILE_UPLOAD(answer) {
    let { answer: filesArray } = answer || {}
    return filesArray.map(file => file.fileName).join(', ')
  },
}

export default {
  props: ['question', 'isOpen', 'closeDialog', 'dateObj'],
  data() {
    return {
      answers: [],
      openResponses: false,
      loading: false,
    }
  },
  computed: {
    timeRange() {
      let { dateObj } = this
      let { value } = dateObj || {}
      if (!isEmpty(value)) {
        let startTime = moment(value[0]).format('DD MMM, YYYY')
        let endTime = moment(value[1]).format('DD MMM, YYYY')
        return `${startTime} - ${endTime}`
      } else {
        return ''
      }
    },
    isMultiChoice() {
      let { question } = this
      let { questionType } = question || {}

      if (
        ['MULTIPLE_CHOICE_ONE', 'MULTIPLE_CHOICE_MANY'].includes(questionType)
      )
        return true
      else return false
    },
  },
  watch: {
    isOpen: {
      handler(newVal) {
        this.openResponses = newVal
        if (newVal) {
          this.loadQuestionResponse()
        }
      },
    },
  },
  methods: {
    async loadQuestionResponse() {
      let { dateObj, isMultiChoice } = this
      let { value } = dateObj
      this.loading = true
      let { id } = this.question
      let params = {
        startTime: value[0],
        endTime: value[1],
      }

      let url = `v3/qanda/questions/fetchanswers/${id}`
      if (isMultiChoice) url = `v3/qanda/questions/fetchotheroptions/${id}`

      let { data, error } = await API.post(url, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.answers = data.answers || data.otherResponses
      }
      this.loading = false
    },
    redirectToResponse(answer) {
      let { responseId, response } = answer || {}
      let id = response || responseId
      let route
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('inspectionResponse', pageTypes.OVERVIEW) || {}

          if (name) {
            route = this.$router.resolve({
              name,
              params: {
                viewname: 'all',
                id,
              },
            })
          }
        } else {
          route = this.$router.resolve({
            name: 'individualInspectionSummary',
            params: { viewname: 'all', id },
          })
        }
        let { href } = route || {}
        window.open(href, '_blank')
      }
    },
    getAnswer(answer) {
      let { question } = this
      let { questionType } = question || {}

      if (!isEmpty(QUESTION_TYPE_ANSWER_RENDERER[questionType])) {
        return QUESTION_TYPE_ANSWER_RENDERER[questionType](answer, question)
      } else {
        return answer.answer
      }
    },
  },
}
</script>

<style lang="scss">
.inspection-response-dialog {
  .el-dialog__body {
    padding: 0px 20px 20px;
  }

  .el-dialog__title {
    font-weight: 500;
    color: #324056;
    font-size: 16px;
    text-transform: none;
  }

  .question {
    font-weight: 500;
    color: #324056;
    font-size: 16px;
  }

  .page-name {
    font-size: 16px;
    font-weight: 500;
    color: #2d394d;
    margin-bottom: 10px;
  }
  .view-response {
    color: #3ab2c1;
    display: flex;
    font-size: 12px;
    cursor: pointer;
    margin-top: 5px;
    margin-left: 15px;
  }
}
</style>
