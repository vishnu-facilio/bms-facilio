<template>
  <div class="d-flex flex-row width100 flex-wrap">
    <div
      :class="[(index + 1) % 4 !== 0 && 'mR20', 'mB20 building-card-body']"
      v-for="(card, index) in modulesList"
      :key="index"
    >
      <div @click="openSummary(card)" class="d-flex flex-col">
        <div
          :class="[
            !card.avatarUrl && 'card-bg',
            'height125 d-flex align-center justify-center',
          ]"
        >
          <img
            :src="$helpers.getImagePreviewUrl(card.photoId)"
            v-if="card.photoId > 0"
            class="image width100"
            height="125"
          />
          <InlineSvg
            v-else
            src="svgs/spacemanagement/space"
            iconClass="icon space-card"
          ></InlineSvg>
        </div>
        <div class="p20">
          <div class="card-text">{{ card.name }}</div>
          <div class="pT5 card-text2 d-flex flex-row">
            <div>
              {{ `${card.area > 0 ? card.area : '---'} sq.ft` }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details', 'modulesList', 'moduleName'],
  mounted() {
    eventBus.$emit('autoResizeContainer', this.moduleName)
  },
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    openSummary(record) {
      let { siteid } = this.$route.params
      let { id } = record
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({ path: `${parentPath}/site/${siteid}/space/${id}` })
      }
    },
  },
}
</script>
