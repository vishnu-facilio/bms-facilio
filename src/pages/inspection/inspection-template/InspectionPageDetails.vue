<template>
  <div class="inspection-page-details">
    <portal :to="widget.key + '-title-section'">
      <div class="widget-header display-flex-between-space width100 p10 ">
        <div class="widget-header-name f12">Questions</div>
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
    <div v-else>
      <div v-if="loading" class="flex-middle">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else ref="inspection-questions">
        <div class="header-container">
          <div class="bold fc-black-13 text-left letter-spacing1">
            {{ page.name }}
          </div>
          <div v-if="page.description" class="mT10 f12 text-fc-grey">
            {{ page.description }}
          </div>
        </div>
        <div class="pT10 pR25 pL25">
          <div
            v-for="(question, index) in questionList"
            :key="index"
            class="questions-container"
          >
            <div>
              <div class="question">
                {{ question.questionNo }} {{ question.question }}
              </div>
            </div>
            <div
              v-if="question.answerRequired"
              :class="[
                'leading-none f12',
                (question || {}).mandatory
                  ? 'mandatory-field'
                  : 'optional-field',
              ]"
            >
              {{ (question || {}).mandatory ? 'Mandatory' : 'Optional' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { getUnRelatedModuleSummary } from 'src/util/relatedFieldUtil'

export default {
  props: ['details', 'resizeWidget', 'calculateDimensions', 'widget'],
  data() {
    return {
      pageNumber: 1,
      questions: [],
      page: null,
      loading: false,
    }
  },
  computed: {
    moduleName() {
      return 'qandaPage'
    },
    totalPages() {
      let { details } = this
      let { pages } = details || {}
      return (pages || []).length
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
    canLoadPages() {
      let { $route } = this
      let { path = '' } = $route || {}

      return !(path || '').includes('builder')
    },
  },
  watch: {
    pageNumber: {
      handler() {
        let { canLoadPages } = this
        if (canLoadPages) this.loadPagesSummary()
      },
    },
    details: {
      handler() {
        let { canLoadPages } = this
        if (canLoadPages) this.loadPagesSummary()
      },
      immediate: true,
    },
  },
  methods: {
    async loadPagesSummary() {
      let { details, pageNumber, moduleName } = this
      let { pages } = details || {}

      if (!isEmpty(pages)) {
        this.loading = true
        let { id } = pages[pageNumber - 1]
        let {
          [moduleName]: record,
          error,
        } = await getUnRelatedModuleSummary(
          'inspectiontemplate',
          'qandaPage',
          id,
          { force: true }
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
        let container = this.$refs['inspection-questions']

        if (container) {
          let height = container.scrollHeight
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height: height + 60, width: width + 100 })
          }
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.inspection-page-details {
  .page-pagination {
    position: absolute;
    right: 40px;
    align-items: center;
    display: flex;
    top: 20px;
  }

  .header-container {
    border-bottom: 1px solid #e2e8ee;
    display: flex;
    flex-direction: column;
    padding: 19px 27.4px 14px 19px;
    background-color: #f8fbfe;
  }
  .page-name {
    font-size: 16px;
    font-weight: 500;
    color: #2d394d;
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
    padding: 15px 0px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .question {
      font-weight: 500;
    }
  }
  .mandatory-field {
    color: #ee508f;
  }
  .optional-field {
    color: #8ca1ad;
  }
  .questions-empty-text {
    color: #324056;
    font-size: 14px;
    font-weight: bold;
  }
}
</style>
