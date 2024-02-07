<template>
  <div v-if="question.answerRequired" class="mL27 scrollView">
    <div v-if="!$validation.isEmpty(question.answer)">
      <!-- File Fields -->
      <FileFieldPreview
        v-if="isFileTypeField(question)"
        :attachments="answer"
      />
      <!-- Multi select fields -->
      <template v-else-if="checkFieldType('MULTIPLE_CHOICE_MANY')">
        <div
          class="answer d-flex items-center"
          v-for="(choice, index) in answer"
          :key="`${choice.label}-${index}`"
        >
          <div class="green-dot"></div>
          <div>{{ choice.label }}</div>
        </div>
      </template>
      <!-- Rating -->
      <template v-else-if="checkFieldType('SMILEY_RATING')">
        <EmojiIconRenderer
          class="emoji-icon-layout mL_7"
          :rating="getEmojiAnswer()"
          :isActive="true"
        />
      </template>
      <div class="flex mL_7 mT3" v-else-if="checkFieldType('STAR_RATING')">
        <inline-svg
          v-for="star in answer"
          :key="star"
          src="svgs/star-yellow"
          class="vertical-middle fill-blue5"
          iconClass="icon icon-xxxl"
        />
        <inline-svg
          v-for="unselectedStar in unselectedRatingStars"
          :key="unselectedStar"
          src="svgs/star-line-yellow"
          class="vertical-middle fill-blue5"
          iconClass="icon icon-xxxl"
        />
      </div>
      <MatrixQuestionDisplay
        v-else-if="checkFieldType('MATRIX')"
        :answer="answer"
        :question="question"
      />
      <MultiQuestionDisplay
        v-else-if="checkFieldType('MULTI_QUESTION')"
        :question="question"
      >
      </MultiQuestionDisplay>
      <!-- Others -->
      <div v-else class="answer">
        {{ answer }}
      </div>
    </div>
    <div v-else class="text-fc-grey mT5">
      {{ $t('qanda.response.no_answer') }}
    </div>
  </div>
  <div v-else-if="!question.answerRequired">
    <div
      v-html="sanitizeHtml(question.richText)"
      class="inspection-heading-field"
    ></div>
  </div>
</template>

<script>
import { sanitize } from '@facilio/utils/sanitize'
import { isEmpty } from '@facilio/utils/validation'
import { EmojiIconRenderer } from '@facilio/survey'
import FileFieldPreview from './FileFieldPreview'
import MatrixQuestionDisplay from './MatrixQuestionDisplay'
import MultiQuestionDisplay from './MultiQuestionDisplay'
import { formatDate } from 'src/util/filters.js'

const QUESTION_TYPE_ANSWER_RENDERER = {
  MULTIPLE_CHOICE_MANY(answer, question) {
    let { options } = question || {}
    let { selected, other } = answer || {}
    selected = options
      .filter(option => selected.includes(option.id))
      .map(option => {
        if (option.other) {
          return { ...option, label: `${option.label} - ${other}` }
        } else {
          return option
        }
      })
    return selected
  },
  MULTIPLE_CHOICE_ONE(answer, question) {
    let { options } = question || {}
    let { selected, other } = answer || {}
    selected = options.find(option => selected === option.id)
    if (!isEmpty(selected) && !isEmpty(selected.other)) {
      selected.label = `${selected.label} - ${other}`
    }
    return selected.label
  },
  BOOLEAN(answer, question) {
    let { trueLabel, falseLabel } = question || {}
    return answer ? trueLabel : falseLabel
  },
  DATE_TIME(answer, question) {
    if (question.showTime) {
      return formatDate(answer)
    } else {
      return formatDate(answer, true)
    }
  },
}

export default {
  components: {
    FileFieldPreview,
    EmojiIconRenderer,
    MatrixQuestionDisplay,
    MultiQuestionDisplay,
  },
  props: ['question'],
  computed: {
    answer() {
      let { question } = this
      let { questionType } = question
      let answer = this.$getProperty(this, 'question.answer.answer', null)
      if (!isEmpty(QUESTION_TYPE_ANSWER_RENDERER[questionType]))
        return QUESTION_TYPE_ANSWER_RENDERER[question.questionType](
          answer,
          question
        )
      else return answer
    },
    unselectedRatingStars() {
      let { question, answer } = this
      let { questionType } = question || {}
      if (questionType === 'STAR_RATING') {
        let { ratingScale } = question || {}
        return Math.abs(ratingScale - answer) || 0
      } else {
        return 0
      }
    },
  },
  methods: {
    isFileTypeField(question) {
      let { questionType } = question || {}
      return ['MULTI_FILE_UPLOAD', 'FILE_UPLOAD'].includes(questionType)
    },
    checkFieldType(type) {
      let { question } = this
      let { questionType } = question || {}
      return questionType === type
    },
    sanitizeHtml(richText) {
      return sanitize(richText)
    },
    getEmojiAnswer() {
      let { question, answer } = this || {}
      let { ratingScale } = question
      let halfScal = ratingScale / 2
      let startEmoji = Math.round(5 - halfScal)

      return Math.abs(answer + startEmoji - 1)
    },
  },
}
</script>

<style scoped>
.emoji-icon-layout {
  height: 3rem;
  margin-left: -3px;
}
.mL_7 {
  margin-left: -5px;
}
.qandacontainer {
  border: none !important;
}
.scrollView {
  overflow: scroll;
}
</style>
