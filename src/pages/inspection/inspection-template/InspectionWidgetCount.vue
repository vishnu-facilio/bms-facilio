<template>
  <div class="inspection-counts-widget" ref="inspection-count-widget">
    <div class="count-widget-card border-left-widget">
      <div class="count">{{ questionCount }}</div>
      <div class="f13 fc-black-11 fwBold text-uppercase">
        {{
          questionCount === 1
            ? $t('qanda.template.question')
            : $t('qanda.template.questions')
        }}
      </div>
    </div>
    <div class="count-widget-card border-left-widget">
      <div class="count">{{ pagesCount }}</div>
      <div class="f13 fc-black-11 fwBold text-uppercase">
        {{
          pagesCount === 1
            ? $t('qanda.template.page')
            : $t('qanda.template.pages')
        }}
      </div>
    </div>
    <div class="count-widget-card">
      <div class="f13 fc-black-11 fwBold text-uppercase">
        {{
          !$validation.isEmpty(lastTriggeredOn)
            ? $t('qanda.template.last_triggered')
            : $t('qanda.template.not_triggered')
        }}
      </div>
      <div class="count-triggerd-on">{{ lastTriggeredOn }}</div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['details', 'resizeWidget', 'calculateDimensions'],
  data() {
    return {
      questions: [],
    }
  },
  mounted() {
    this.$nextTick(() => {
      let container = this.$refs['inspection-count-widget']

      if (container) {
        let width = container.scrollWidth
        let height = container.scrollHeight
        if (this.resizeWidget) {
          this.resizeWidget({ height: height - 70, width })
        }
      }
    })
  },
  computed: {
    pagesCount() {
      let { details } = this
      let pages = this.$getProperty(details, 'totalPages', 0)
      return pages
    },
    questionCount() {
      let { details } = this
      let questions = this.$getProperty(details, 'totalQuestions', 0)
      return questions
    },
    lastTriggeredOn() {
      let lastTriggered = this.$getProperty(this, 'details.lastTriggeredTime')
      let triggeredTime = this.$options.filters.fromNow(lastTriggered)
      return lastTriggered ? triggeredTime : null
    },
    moduleName() {
      return 'inspectionTemplate'
    },
  },
}
</script>

<style lang="scss" scoped>
.inspection-counts-widget {
  display: flex;
  flex-direction: row;
  .count-widget-card {
    padding: 20px;
    background-color: #fff;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    .count {
      font-size: 38px;
      color: #3ab2c2;
    }
    .count-triggerd-on {
      font-size: 32px;
      color: #3ab2c2;
    }
    .label {
      font-size: 18px;
      color: #000000;
    }
  }
  .border-left-widget {
    border-right: solid 10px #f6f7f8;
  }
}
</style>
