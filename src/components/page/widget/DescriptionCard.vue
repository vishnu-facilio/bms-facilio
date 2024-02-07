<template>
  <div class="common-description p30">
    <div v-if="showIcon">
      <div v-if="details[photoFieldName] > 0">
        <img
          :src="details.getImage(photoFieldName)"
          class="img-container mR30"
          width="100"
          height="100"
        />
      </div>
      <div v-else>
        <img
          v-if="details.moduleName === 'itemTypes'"
          src="~statics/inventory/item-img.jpg"
          width="100"
          height="100"
          class="mR30"
        />
        <img
          v-if="details.moduleName === 'toolTypes'"
          src="~statics/inventory/tool-img.jpg"
          width="100"
          height="100"
          class="mR30"
        />
      </div>
    </div>

    <div ref="description-container">
      <div class="common-description">
        <div
          v-if="showDescriptionTitle"
          class="text-left fwBold pointer line-height20 mR50 text-center text-uppercase f13"
        >
          {{ $t('common._common.description') }}
        </div>
        <div
          class="text-left space-preline pm-summary-tab-subject"
          v-html="description"
        ></div>
      </div>
      <div v-if="showCategory" class="common-description pT30 pB10">
        <div
          class="text-left fwBold pointer line-height20 mR50 text-center text-uppercase f13"
        >
          {{ $t('common._common.category') }}
        </div>
        <div
          class="text-left space-preline pm-summary-tab-subject mL17"
          v-html="category"
        ></div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
export default {
  props: [
    'details',
    'showDescriptionTitle',
    'showCategory',
    'showIcon',
    'resizeWidget',
  ],
  mounted() {
    this.autoResize()
  },
  computed: {
    photoFieldName() {
      return 'photoId'
    },
    moduleName() {
      return this.$getProperty(this, 'details.moduleName')
    },
    description() {
      let description
      if (this.moduleName === 'item') {
        description = this.$getProperty(
          this,
          'details.itemType.description',
          null
        )
      } else if (this.moduleName === 'tool') {
        description = this.$getProperty(
          this,
          'details.toolType.description',
          null
        )
      } else {
        description = this.$getProperty(this, 'details.description', null)
      }
      return !isEmpty(description) ? sanitize(description) : '---'
    },
    category() {
      let { details } = this
      let { category } = details || {}
      let { displayName, name } = category || {}
      if (!isEmpty(displayName)) {
        return sanitize(displayName)
      }
      if (!isEmpty(name)) {
        return sanitize(name)
      }
      return '---'
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
