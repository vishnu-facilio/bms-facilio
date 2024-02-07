<template>
  <div class="inspection-page-details">
    <portal :to="widget.key + '-title-section'">
      <div class="widget-header display-flex-between-space width100 p10 ">
        <div class="widget-header-name f12">{{ widgetTitle }}</div>
        <div class="d-flex fc-black-small-txt-12 widget-header-name">
          <div>{{ pageNumber }} of {{ totalPages }} Pages</div>
          <span
            class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
            @click="prev()"
            v-bind:class="{ disable: pageNumber === 1 }"
          ></span>
          <span
            class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
            @click="next()"
            v-bind:class="{ disable: pageNumber === totalPages }"
          ></span>
        </div>
      </div>
    </portal>
    <div
      class="fc-empty-center height100"
      v-if="!loading && $validation.isEmpty(questions)"
    >
      <inline-svg
        src="svgs/inspection-empty"
        iconClass="icon text-center icon-80"
      ></inline-svg>
      <div class="questions-empty-text">
        {{ $t('qanda.response.no_questions') }}
      </div>
    </div>
    <div v-else ref="inspection-answers">
      <div v-if="loading" class="flex-center-vH mT10">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <template v-else-if="!$validation.isEmpty(questions)">
        <div class="header-container">
          <div class="bold fc-black-13 text-left letter-spacing1">
            {{ page.name }}
          </div>
          <div v-if="page.description" class="mT10 f13 text-fc-grey">
            {{ page.description }}
          </div>
        </div>

        <div
          v-for="(question, index) in questionList"
          :key="index"
          class="questions-container p20"
        >
          <div class="display-flex-between-space align-center">
            <div>
              <div class="question">
                {{ question.questionNo }} {{ question.question }}
              </div>
              <div
                class="d-flex flex-row primary-font"
                v-if="question.answerRequired"
              >
                <div class="f13 mL25 mT8">
                  {{ $t('qanda.template.answered') }}:
                  <span>{{ question.answered ? question.answered : 0 }}</span>
                </div>
                <div class="f13 mT8">
                  , {{ $t('qanda.template.skipped') }}:
                  <span>{{
                    question.answered && totalResponses
                      ? Math.abs(totalResponses - question.answered)
                      : 0
                  }}</span>
                </div>
              </div>
            </div>
            <div
              v-if="question.showResponses"
              class="show-responses-container"
              @click="openResponseDialog(question)"
            >
              Show Answer
            </div>
          </div>
          <div v-if="isMultiChoice(question)">
            <MCQTable :question="question" />
            <div
              v-if="question.summary"
              class="show-responses-container mT10"
              @click="openResponseDialog(question)"
            >
              Show Other Responses
            </div>
          </div>
          <div v-else-if="isRatingField(question)">
            <RatingTable :question="question" />
          </div>
        </div>
      </template>
      <InspectionResponse
        :isOpen="isDialogOpen"
        :question="selectedQuestion"
        :closeDialog="closeDialog"
        :dateObj="dateObj"
      />
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import InspectionResponse from './InspectionResponse'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import clone from 'lodash/clone'
import MCQTable from './MCQTable'
import RatingTable from './RatingTable'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  props: ['details', 'resizeWidget', 'widget', 'sectionKey', 'moduleName'],
  data() {
    return {
      pageNumber: 1,
      questions: [],
      page: null,
      loading: false,
      isDialogOpen: false,
      selectedQuestion: false,
      dateObj: clone(NewDateHelper.getDatePickerObject(44, null)),
    }
  },
  components: {
    InspectionResponse,
    MCQTable,
    RatingTable,
  },
  created() {
    eventBus.$on('date-change-inspection', this.setDateObject)
  },
  computed: {
    pageModuleName() {
      return 'qandaPage'
    },
    totalPages() {
      let { details } = this
      let { pages } = details || {}
      return (pages || []).length
    },
    totalResponses() {
      let { details } = this || {}
      let { totalResponses } = details || {}
      return totalResponses
    },
    questionList() {
      let { questions } = this
      let count = 0
      return questions.map(question => {
        if (question.answerRequired) {
          count += 1
          return { ...question, questionNo: `Q${count}.` }
        } else {
          return question
        }
      })
    },
    widgetTitle() {
      let { moduleName } = this
      if (moduleName.includes('induction')) {
        return `${this.$t('qanda.template.induction')} ${this.$t(
          'qanda.template.summary'
        )}`
      } else if (moduleName.includes('survey')) {
        return `${this.$t('qanda.template.survey')} ${this.$t(
          'qanda.template.summary'
        )}`
      } else {
        return `${this.$t('qanda.template.inspection')} ${this.$t(
          'qanda.template.summary'
        )}`
      }
    },
  },
  watch: {
    pageNumber: {
      handler() {
        this.loadPagesSummary()
      },
    },
    details: {
      handler() {
        this.loadPagesSummary()
      },
      immediate: true,
    },
  },
  methods: {
    setDateObject(dateFilter) {
      this.dateObj = dateFilter
      this.loadPagesSummary()
    },
    async loadPagesSummary() {
      let { details, pageNumber, pageModuleName, dateObj } = this
      let { value } = dateObj
      let { pages } = details || {}

      if (!isEmpty(pages)) {
        this.loading = true
        let { id } = pages[pageNumber - 1]
        let { [pageModuleName]: record, error } = await API.fetchRecord(
          pageModuleName,
          {
            id,
            fetchSummary: true,
            startTime: value[0],
            endTime: value[1],
          }
        )
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.page = record
          this.questions = this.$getProperty(record, 'questions')
        }
        this.loading = false
        this.autoResize()
      }
    },
    prev() {
      let { pageNumber } = this
      if (pageNumber !== 1) {
        this.$set(this, 'pageNumber', pageNumber - 1)
      }
    },
    next() {
      let { pageNumber, totalPages } = this
      if (pageNumber !== totalPages) {
        this.$set(this, 'pageNumber', pageNumber + 1)
      }
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['inspection-answers']

        if (container) {
          let height = container.scrollHeight
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height: height + 70, width })
          }
        }
      })
    },
    openResponseDialog(question) {
      this.selectedQuestion = question
      this.isDialogOpen = true
    },
    closeDialog() {
      this.isDialogOpen = false
    },
    isMultiChoice(question) {
      let { questionType } = question || {}

      if (
        ['MULTIPLE_CHOICE_ONE', 'MULTIPLE_CHOICE_MANY'].includes(questionType)
      )
        return true
      else return false
    },
    isRatingField(question) {
      let { questionType } = question || {}

      if (['STAR_RATING', 'SMILEY_RATING'].includes(questionType)) return true
      else return false
    },
  },
}
</script>

<style lang="scss" scoped>
.inspection-page-details {
  .header-container {
    border-bottom: 1px solid #e2e8ee;
    display: flex;
    flex-direction: column;
    padding: 19px 27.4px 14px 19px;
    background-color: #f8fbfe;
  }
  .el-icon-arrow-right.disable {
    color: #50506c;
    font-weight: bold;
    opacity: 0.4;
  }
  .el-icon-arrow-left.disable {
    color: #50506c;
    font-weight: bold;
    opacity: 0.4;
  }
  .questions-container {
    padding: 20px 20px 10px;
    .question {
      font-weight: 500;
      color: #324056;
      font-size: 14px;
    }
    .show-responses-container {
      color: #3ab2c1;
      display: flex;
      justify-content: center;
      font-size: 12px;
      cursor: pointer;
    }
  }
  .page-name {
    font-size: 16px;
    font-weight: 500;
    color: #2d394d;
  }
  .questions-empty-text {
    color: #324056;
    font-size: 14px;
    font-weight: bold;
  }
}
</style>
<style lang="scss">
.inspection-page-details {
  .inspection-widget-header {
    color: #8ca1ad;
    text-transform: uppercase;
    font-weight: bold;
    line-height: 18px;
  }
}
</style>
