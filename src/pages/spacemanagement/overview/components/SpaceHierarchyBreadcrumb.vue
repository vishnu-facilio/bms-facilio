<template>
  <div class="d-flex flex-direction-row space-breadcrumb align-center">
    <div
      v-for="(item, index) in hierarchyList"
      :key="index"
      class="d-flex flex-direction-row"
      @click="redirect(item.route)"
    >
      <div
        :class="[
          index === hierarchyList.length - 1
            ? 'breadcrumb-last-item'
            : 'breadcrumb-text',
        ]"
      >
        {{ item.displayName }}
      </div>
      <div v-if="index != hierarchyList.length - 1" class="pL10 pR10">/</div>
    </div>
  </div>
</template>
<script>
import spaceCardMixin from 'src/components/mixins/SpaceCardMixin'
import { getFieldOptions } from 'util/picklist'
export default {
  mixins: [spaceCardMixin],
  props: ['record'],
  data() {
    return {
      hierarchyList: [],
      sites: [],
    }
  },
  created() {
    this.getSiteList().then(() => {
      this.hierarchyList = this.initHierarchy(this.record)
    })
  },
  methods: {
    async getSiteList() {
      let { record } = this
      let defaultIds = []
      defaultIds.push(record.siteId)

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site' },
        defaultIds,
      })
      if (!error) {
        this.sites = options
      }
    },
  },
}
</script>
