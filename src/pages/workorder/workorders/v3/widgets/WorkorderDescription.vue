<template>
  <div>
    <div
      v-if="workorder.subject.length > 45 || workorder.description"
      class="fc-visibility-gicon-visible"
      :ref="getRefName"
    >
      <div class="p10 fc__wo__sum__h__desc">
        <div>
          <div v-if="workorder.subject.length > 45">
            <div class="heading-black18">
              {{ workorder.subject ? workorder.subject : '---' }}
            </div>
          </div>
          <div
            class="pm-summary-tab-subject pT10 space-preline"
            v-if="workorder.description"
            v-html="getDescription(workorder)"
          ></div>

          <div v-if="workorder.subject.length > 45">
            <InstantTranslate
              v-if="woDescriptiontranslate"
              :content="workorder.subject"
              :extraContent="workorder.description"
              @onData="handleDataLoad"
            ></InstantTranslate>
          </div>
          <div v-else>
            <InstantTranslate
              v-if="woDescriptiontranslate"
              :content="workorder.description"
              @onData="handleDataLoad"
            ></InstantTranslate>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
import InstantTranslate from 'components/InstantTranslate'
import { eventBus } from '@/page/widget/utils/eventBus'
import _random from 'lodash/random'
export default {
  props: ['moduleName', 'details', 'resizeWidget'],
  name: 'WorkorderDescription',
  mounted() {
    this.$nextTick(() => {
      this.autoResize()
    })
  },
  data() {
    return {
      woDescriptiontranslate: false,
    }
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Workorder Description'
    },
    workorder() {
      return this.details.workorder
    },
    getRefName() {
      const refName = _random(1, 3)
      return `dis-${refName}`
    },
  },
  created() {
    eventBus.$on('woDescriptiontranslate', woDescriptiontranslate => {
      this.woDescriptiontranslate = woDescriptiontranslate
      this.$nextTick(() => {
        this.autoResize()
      })
    })
  },
  destroyed() {
    eventBus.$off('woDescriptiontranslate', () => {
      this.woDescriptiontranslate = false
      this.$nextTick(() => {
        this.autoResize()
      })
    })
  },
  components: {
    InstantTranslate,
  },
  methods: {
    handleDataLoad() {
      this.$nextTick(() => {
        this.autoResize()
      })
    },
    getDescription(record) {
      let { description } = record
      return !isEmpty(description) ? sanitize(description) : '---'
    },
    autoResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          let container = this.$refs[this.getRefName]
          if (container) {
            let width = container.scrollWidth
            let height = container.scrollHeight + 50
            if (this.resizeWidget) {
              this.resizeWidget({ height: height, width: width })
            }
          }
        })
      })
    },
    handleTranslate() {
      this.woDescriptiontranslate = !this.woDescriptiontranslate
      eventBus.$emit('woDescriptiontranslate', this.woDescriptiontranslate)
    },
  },
}
</script>
