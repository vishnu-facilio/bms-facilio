<template>
  <div class="p30 d-flex flex-direction-column">
    <div class="flex-middle justify-content-center">
      <template v-if="!isSpaceKpi">
        <div
          v-if="$validation.isEmpty(associatedCount)"
          class="empty-grey-13 pT50 text-center"
        >
          <div class="empty-grey-desc2">
            No
            {{ associatedAssetCategory ? associatedAssetCategory : 'Assets' }}
            associated
          </div>
        </div>
        <div class="f12 bold fc-black-13 text-uppercase text-center">
          {{ associatedAssetCategory ? associatedAssetCategory : 'Asset' }}s
          Associated
        </div>
      </template>
      <template v-else>
        <div
          v-if="$validation.isEmpty(associatedCount)"
          class="empty-grey-13 pT50 text-center"
        >
          <div class="empty-grey-desc2">No spaces associated</div>
        </div>
        <div class="f12 bold fc-black-13 text-uppercase text-center">
          Spaces Associated
        </div>
      </template>
    </div>

    <template v-if="!$validation.isEmpty(associatedCount)">
      <div class="f45 fc-black-com mT20 text-center">{{ associatedCount }}</div>
      <div class="f11 fc-blue-label text-capitalize text-center">This KPI</div>
    </template>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState, mapGetters } from 'vuex'

export default {
  components: {},
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'activeTab',
    'widget',
    'resizeWidget',
    'moduleName',
  ],
  data() {
    return {
      workorder: null,
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {},
  methods: {},
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    ...mapGetters(['getAssetCategory']),

    isSpaceKpi() {
      let { matchedResources = [] } = this.details
      let firstMatchedResource = matchedResources[0] || {}

      return firstMatchedResource.resourceTypeEnum === 'SPACE'
    },

    associatedCount() {
      let { matchedResourcesIds } = this.details
      return !isEmpty(matchedResourcesIds) ? matchedResourcesIds.length : null
    },

    associatedAssetCategory() {
      let { assetCategoryId } = this.details
      return !isEmpty(assetCategoryId) && !isEmpty(this.assetCategory)
        ? this.getAssetCategory(assetCategoryId).displayName
        : null
    },
  },
}
</script>
