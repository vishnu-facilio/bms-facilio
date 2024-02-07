<template>
  <div>
    <FilterDropdown
      v-if="!loadingWoAsset"
      v-model="activeWoAsset"
      :options="options"
    >
    </FilterDropdown>
    <div v-else class="loading-shimmer width120px height40 bR5"></div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import FilterDropdown from './specialFilterDropDown'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['moduleName', 'setFilter'],
  data() {
    return {
      woAssetData: [],
      activeWoAsset: null,
      loadingWoAsset: true,
      woAssetList: [],
      showEmptyData: false,
    }
  },
  components: {
    FilterDropdown,
  },
  created() {
    this.loadWoAssetMapping()
  },
  watch: {
    activeWoAsset: function() {
      this.setFilter({
        loading: false,
        query: {
          moduleName: this.activeWoAsset.toString(),
        },
      })
    },
  },
  computed: {
    options() {
      return this.woAssetList.map(list => {
        return {
          id: list.value,
          label: list.label,
          value: list.value,
        }
      })
    },
  },
  methods: {
    async loadWoAssetMapping() {
      this.loadingWoAsset = true
      const { error, data } = await API.get(
        `v2/translationsetup/${this.moduleName}`
      )
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        const { fields } = data || {}
        if (!isEmpty(fields)) {
          this.woAssetList = fields
          this.activeWoAsset = this.woAssetList[0].value
          this.showEmptyData = false
        } else {
          this.setFilter({
            loading: false,
            query: null,
          })
        }
      }
      this.loadingWoAsset = false
    },
  },
}
</script>
