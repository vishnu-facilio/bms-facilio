<template>
  <div>
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="loading" :size="80"></spinner>
    </div>
    <div v-else class="qanda-response-widget" ref="qanda-section">
      <div class="header">
        <div class="d-flex justify-between width100 pL5 pR5 pB20 align-center">
          <div v-if="$org.logoUrl" class="fc-quotation-logo">
            <img :src="$org.logoUrl" style="width: 100px;" />
          </div>
          <div class="fw6 text-uppercase f16">
            {{ headerText }}
          </div>
        </div>

        <div class="d-flex flex-col width100 pL5 pR5 pB20">
          <div class="page-name">{{ responseName }}</div>
          <div
            v-if="(templateRecord || {}).description"
            class="page-description "
          >
            {{ (templateRecord || {}).description }}
          </div>
        </div>
        <div class="field-details">
          <div class="d-flex flex-col justify-center">
            <div class="field-container" v-if="!$validation.isEmpty(siteName)">
              <span class="field-label"> {{ $t('qanda.response.site') }}</span>
              <span class="field-value">{{ siteName }}</span>
            </div>
            <div
              class="field-container"
              v-if="
                !$validation.isEmpty($getProperty(details, 'resource.name'))
              "
            >
              <span class="field-label">{{ $t('qanda.response.space') }}</span>
              <span class="field-value">{{
                $getProperty(details, 'resource.primaryValue', null) ||
                  $getProperty(details, 'resource.name', '')
              }}</span>
            </div>
            <div
              class="field-container"
              v-if="!$validation.isEmpty(startedTime)"
            >
              <span class="field-label">{{
                $t('qanda.response.started_at')
              }}</span>
              <span class="field-value">{{ startedTime }}</span>
            </div>
            <div class="field-container">
              <span class="field-label">{{
                $t('qanda.response.completed_at')
              }}</span>
              <span class="field-value">{{ completedTime }}</span>
            </div>
          </div>
          <div class="d-flex flex-col">
            <div
              class="field-container pT5"
              v-if="!$validation.isEmpty(details.id)"
            >
              <span class="field-label">{{ $t('qanda.response.id') }}</span>
              <span class="fc-id">#{{ details.id }}</span>
            </div>
            <div
              class="field-container"
              v-if="
                !$validation.isEmpty(
                  $getProperty(details, 'parent.qandAType')
                ) && isInspectionModule
              "
            >
              <span class="field-label">{{
                $t('qanda.response.category')
              }}</span>
              <span class="field-value">{{
                $getProperty(details, 'category.displayName')
              }}</span>
            </div>
            <div class="field-container">
              <span class="field-label">{{
                isInspectionModule
                  ? $t('qanda.response.conducted_by')
                  : $t('qanda.response.attended_by')
              }}</span>
              <span class="field-value">{{ attenderName }}</span>
            </div>
            <div
              v-if="!$validation.isEmpty(responseScore)"
              class="field-container pB0"
            >
              <span class="field-label">{{ $t('qanda.response.score') }}</span>
              <span class="score-text f14">{{ responseScore }}</span>
            </div>
          </div>
        </div>
      </div>
      <table class="width100 border-tlbr-none">
        <tbody>
          <tr>
            <td colspan="100%">
              <div class="qanda-section" v-if="!$validation.isEmpty(pages)">
                <div
                  v-for="(page, pageIndex) in pages"
                  :key="page.id"
                  :class="[
                    'page-section',
                    !isLastItem(pageIndex, page.pages) && 'border-bottom',
                  ]"
                >
                  <div class="page-details-header">
                    <div>
                      <div class="page-name">{{ page.name }}</div>
                      <div class="page-description">{{ page.description }}</div>
                    </div>
                    <div
                      v-if="!$validation.isEmpty(getPageScore(page))"
                      class="score-text"
                    >
                      {{ getPageScore(page) }}
                    </div>
                  </div>
                  <template v-if="!$validation.isEmpty(page.questions)">
                    <table class="border-tlbr-none print-inner-table width100">
                      <tbody>
                        <tr
                          v-for="(question, questionIndex) in page.questions"
                          :key="question.id"
                          :class="[
                            'fc-tr-border ',
                            !canShowQuestion(question) && 'display-none',
                          ]"
                        >
                          <td colspan="100%">
                            <div
                              :class="[
                                'qandacontainer',
                                !isLastItem(questionIndex, page.questions) &&
                                  'border-bottom',
                              ]"
                            >
                              <div class="fc__layout__align">
                                <div class="question">
                                  {{ question.questionNo }}
                                  {{ question.question }}
                                </div>
                                <div
                                  v-if="
                                    !$validation.isEmpty(
                                      getQuestionScore(question)
                                    )
                                  "
                                  class="score-text"
                                >
                                  {{ getQuestionScore(question) }}
                                </div>
                              </div>
                              <div
                                v-if="
                                  !$validation.isEmpty(question.description)
                                "
                                class="question-description pT5"
                              >
                                {{ question.description }}
                              </div>
                              <div>
                                <div
                                  class="answer-heading-text mL27"
                                  v-if="question.answerRequired"
                                >
                                  {{ $t('qanda.response.answer') }}
                                </div>
                                <!-- Answer -->
                                <FieldDisplayValue :question="question" />
                                <!-- Comments -->
                                <template
                                  v-if="(question.answer || {}).comments"
                                >
                                  <div
                                    class="bold f14 pT15 text-fc-grey remarks-field mL27"
                                  >
                                    {{ (question || {}).commentsLabel }}
                                  </div>
                                  <div
                                    class="answer mL27 text-justify text-indent-30"
                                  >
                                    {{ (question.answer || {}).comments }}
                                  </div>
                                </template>
                                <!-- Attachments -->
                                <template
                                  v-if="
                                    !$validation.isEmpty(
                                      (question.answer || {}).attachmentList
                                    )
                                  "
                                >
                                  <div
                                    class="bold f14 pT15 text-fc-grey remarks-field mL27"
                                  >
                                    {{ (question || {}).attachmentLabel }}
                                  </div>
                                  <FileFieldPreview
                                    class="mL27"
                                    :attachments="
                                      (question.answer || {}).attachmentList
                                    "
                                  />
                                </template>
                              </div>
                            </div>
                          </td>
                        </tr>
                      </tbody>

                      <tbody></tbody>
                    </table>
                  </template>
                  <div v-else class="question-empty-state">
                    <inline-svg
                      src="svgs/inspection-empty"
                      iconClass="icon text-center icon-80"
                    ></inline-svg>
                    <div class="questions-empty-text">
                      {{ $t('qanda.response.no_questions') }}
                    </div>
                  </div>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import FieldDisplayValue from '../common/FieldDisplayValue'
import helpers from 'src/util/helpers.js'
import FileFieldPreview from '../common/FileFieldPreview'
import { getUnRelatedModuleSummary } from 'src/util/relatedFieldUtil'
const { getOrgMoment: moment } = helpers

export default {
  props: ['details', 'resizeWidget'],
  data() {
    return {
      loading: false,
      pages: [],
    }
  },
  components: { FieldDisplayValue, FileFieldPreview },
  created() {
    this.loadTemplate()
  },
  computed: {
    attenderName() {
      let { details } = this || {}
      let assignedToName = this.$getProperty(details, 'assignedTo.name')
      if (!isEmpty(assignedToName)) {
        return assignedToName || '---'
      } else {
        return this.$getProperty(details, 'people.name', '---')
      }
    },
    startedTime() {
      let { details } = this
      let { actualWorkStart = null } = details || {}
      return isEmpty(actualWorkStart)
        ? actualWorkStart
        : moment(actualWorkStart).format('DD MMM, YYYY HH:mm')
    },
    completedTime() {
      let { details } = this
      let { actualWorkEnd } = details
      if (!isEmpty(actualWorkEnd))
        return moment(actualWorkEnd).format('DD MMM, YYYY HH:mm')
      else return 'N/A'
    },
    responseScore() {
      let { details } = this
      let { fullScore, totalScore, scorePercent } = details || {}
      if (!isEmpty(totalScore) && !isEmpty(scorePercent)) {
        return `${totalScore}/${fullScore}, ${scorePercent}%`
      } else {
        return null
      }
    },
    moduleName() {
      return this.$getProperty(this, '$attrs.moduleName')
    },
    templateModuleName() {
      let { moduleName } = this

      if (moduleName.includes('induction')) return 'inductionTemplate'
      else return 'inspectionTemplate'
    },
    headerText() {
      let { moduleName } = this
      if (moduleName.includes('induction')) return 'Induction'
      else return 'Inspection'
    },
    siteName() {
      let { details } = this
      let { siteId } = details || {}
      let site = this.$store.getters.getSite(siteId)

      return (site || {}).name || ''
    },
    isInspectionModule() {
      let { moduleName } = this
      return moduleName.includes('inspection')
    },

    templateRecord() {
      let { details } = this
      let { template } = details
      return template
    },
    responseName() {
      let { details } = this
      let { name } = details || {}
      return name
    },
  },
  methods: {
    async loadTemplate(responseModuleName = null) {
      this.loading = true
      let { details } = this
      let { template } = details || {}
      let { pages } = template || {}
      if (!isEmpty(pages)) {
        let response = await this.loadQAndA(pages, responseModuleName)
        let finalQandA = []
        response.forEach(qandaResponse => {
          let { error, ['qandaPage']: data } = qandaResponse
          let { questions } = data
          let count = 0
          if (!isEmpty(questions)) {
            questions = questions.map(question => {
              if (question.answerRequired) {
                count += 1
                return { ...question, questionNo: `Q${count}.` }
              } else {
                return question
              }
            })
          } else {
            questions = []
          }
          if (error) {
            this.$message.error(error.messge || 'Error Occured')
          } else {
            finalQandA.push({ ...data, questions })
          }
        })
        this.pages = finalQandA
      }

      this.loading = false
      this.autoResize()
    },
    async loadQAndA(pages, responseModuleName) {
      let { details, moduleName } = this
      let { id: responseId } = details || {}
      let promise = []
      if (!isEmpty(responseId)) {
        pages.forEach(page => {
          let params = { response: responseId }
          if (isEmpty(responseModuleName)) {
            params = { ...params, responseModuleName: moduleName }
          } else {
            params = { ...params, responseModuleName }
          }
          let { id } = page || {}
          let qandaResponse = getUnRelatedModuleSummary(
            moduleName,
            'qandaPage',
            id,
            params
          )
          promise.push(qandaResponse)
        })
      }
      return Promise.all(promise)
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['qanda-section']

        if (container) {
          let height = container.scrollHeight
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height: height + 60, width })
          }
        }
      })
    },
    isLastItem(index, array) {
      return Array.isArray(array) && index === array.length - 1
    },
    getPageScore(page) {
      let { fullScore, totalScore } = page || {}

      if (!isEmpty(totalScore)) {
        return `${totalScore}/${fullScore}`
      } else {
        return ''
      }
    },
    getQuestionScore(question) {
      let { fullScore, score } = question || {}

      let validateScore = !isEmpty(score) && !isEmpty(fullScore)
      if (validateScore) {
        return `${score}/${fullScore}`
      } else {
        return ''
      }
    },
    canShowQuestion(question) {
      let { responseStatus } = this.details
      let { displayLogicMeta, answer = null } = question || {}
      if (!isEmpty(displayLogicMeta)) {
        let { action } = displayLogicMeta[0] || {}
        if (action === 'hide') {
          if (!isEmpty(answer) && responseStatus == 4) {
            return true
          }
          return false
        }
        return true
      }
      return true
    },
  },
}
</script>

<style lang="scss">
.qanda-response-widget {
  height: 100%;
  margin: 0px 20px;
  padding: 20px;
  overflow: auto;
  .inspection-heading-field {
    color: #324056;
    img {
      width: 100%;
      min-height: 100px;
    }
    p {
      font-size: 14px;
      margin: 5px 0px;
      color: #324056;
    }
  }
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0px 20px;
    flex-direction: column;
    page-break-after: avoid;
    page-break-inside: avoid;
  }
  .field-details {
    width: 100%;
    border: solid 1px #e4eaed;
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    padding: 20px;
    .field-container {
      display: flex;
      font-size: 14px;
      align-items: center;
      padding-bottom: 10px;
    }
    .field-label {
      text-transform: uppercase;
      width: 140px;
      letter-spacing: 1px;
      color: #64686e;
      font-size: 11px;
      font-weight: 600;
    }
    .field-value {
      font-size: 14px;
      letter-spacing: 0.5px;
      color: #324056;
    }
  }
  .page-details-header {
    background-color: #f8fbfe;
    color: #324056;
    padding: 15px;
    border: solid 1px #dee7ef;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  .page-section {
    margin-bottom: 20px;
  }
  .question-empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    flex-direction: column;
    padding: 15px;
    border-left: solid 1px #e4eaed;
    border-right: solid 1px #e4eaed;
  }

  .green-dot {
    background-color: #38b3c2;
    width: 7px;
    height: 7px;
    border-radius: 5px;
    margin-right: 10px;
  }
  .answer-heading-text {
    line-height: 1.25;
    font-weight: 500;
    font-size: 14px;
    letter-spacing: normal;
    text-align: left;
    color: #ff3184;
    margin-top: 10px;
  }
  .page-name {
    font-size: 14px;
    font-weight: bold;
    line-height: normal;
    letter-spacing: 1.17px;
    color: #324056;
  }
  .page-description {
    margin-top: 10px;
    font-size: 13px;
    line-height: 1.54;
    letter-spacing: 0.46px;
    color: #707588;
  }
  .answer {
    font-size: 13px;
    margin-top: 5px;
  }
  .question {
    font-size: 16px;
    margin-bottom: 2px;
    font-weight: 500;
    color: #324056;
    letter-spacing: 0.7px;
    font-size: 14px;
  }
  .question-description {
    font-size: 13px;
    color: #385571b4;
    margin-left: 27px;
  }
  .mL27 {
    margin-left: 27px;
  }
  .qandacontainer {
    margin: 0px 10px;
    padding: 15px 3px 15px 10px;
  }
  .border-bottom {
    border-bottom: none;
  }
  .inline {
    display: flex;
    justify-content: flex-start;
  }
  .questions-empty-text {
    color: #324056;
    font-size: 14px;
    font-weight: bold;
  }
  .text-indent-30 {
    text-indent: 30px;
  }
}
.fc-tr-border {
  border: solid 1px #dee7ef;
  border-bottom: 1px solid #dee7ef;
}
.score-text {
  font-size: 13px;
  color: #16b676;
  letter-spacing: 0.46px;
}
.print-inner-table {
  .display-none {
    display: none !important;
  }
}
</style>
