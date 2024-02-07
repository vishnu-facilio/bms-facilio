<template>
  <div v-if="canShowMainFieldSearch">
    <el-input
      v-if="showMainFieldSearch"
      v-model="searchText"
      ref="mainFieldSearchInput"
      class="fc-input-full-border2 width-auto mL-auto"
      size="mini"
      @blur="hideMainFieldSearch"
      @change="emitSearchText"
      :placeholder="$t('common._common.search')"
    ></el-input>
    <span v-else @click="openSearch">
      <InlineSvg
        src="svgs/search"
        class="d-flex cursor-pointer"
        iconClass="icon icon-sm search-icon"
      ></InlineSvg>
    </span>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['mainFieldObj', 'debounceTime', 'search'],
  data() {
    return {
      showMainFieldSearch: false,
      searchText: null,
    }
  },

  computed: {
    canShowMainFieldSearch() {
      let { dataTypeEnum } = this.mainFieldObj || {}
      return dataTypeEnum === 'STRING'
    },
  },
  watch: {
    search: {
      handler(newVal) {
        if (newVal !== this.searchText) {
          this.searchText = newVal
        }
        this.hideMainFieldSearch()
      },
      immediate: true,
    },
  },
  methods: {
    openSearch() {
      this.showMainFieldSearch = true
      this.$nextTick(() => {
        let searchBox = this.$refs['mainFieldSearchInput']
        !isEmpty(searchBox) && searchBox.focus()
      })
    },
    hideMainFieldSearch() {
      if (isEmpty(this.searchText)) this.showMainFieldSearch = false
    },
    emitSearchText() {
      this.$emit('update:search', this.searchText)
      this.$emit('onSearch', this.searchText)
    },
    // clearSearch() {
    //   this.emitSearchText()
    //   this.hideMainFieldSearch()
    // },
  },
}
</script>
