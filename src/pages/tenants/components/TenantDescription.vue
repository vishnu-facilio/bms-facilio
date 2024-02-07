<template>
  <div class="common-description p20">
    <div ref="description-container">
      <div
        class="line-height20 mR50 text-left f13 bold text-uppercase fc-black-13"
      >
        {{ $t('common._common.description') }}
      </div>
      <div class="common-description">
        <div
          class="text-left space-preline pm-summary-tab-subject mT10"
          v-html="description"
        ></div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  props: ['details', 'resizeWidget'],
  mounted() {
    this.autoResize()
  },
  computed: {
    description() {
      let { details } = this
      let { description } = details || {}
      return !isEmpty(description) ? sanitize(description) : '---'
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          let container = this.$refs['description-container']
          if (container) {
            let height = container.scrollHeight + 60
            let width = container.scrollWidth
            if (this.resizeWidget) {
              this.resizeWidget({ height: height, width })
            }
          }
        })
      })
    },
  },
}
</script>
<style scoped>
.common-description {
  justify-content: left;
  display: flex;
  flex-direction: row !important;
}
img {
  border-radius: 50%;
}
</style>
