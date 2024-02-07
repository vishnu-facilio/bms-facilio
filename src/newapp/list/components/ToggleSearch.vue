<template>
  <div>
    <el-input
      v-if="showMainFieldSearch"
      v-model="searchData"
      ref="mainFieldSearchInput"
      class="fc-input-full-border2 width150px"
      clearable
      autofocus
      @clear="hideMainFieldSearch"
      @change="onSearchChange"
      :placeholder="$t('common._common.search')"
    ></el-input>
    <span v-else @click="openMainFieldSearch" class="search-icon-val">
      <inline-svg
        src="svgs/search"
        class="vertical-middle cursor-pointer"
        iconClass="icon icon-sm15"
      ></inline-svg>
    </span>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  data() {
    return {
      searchData: '',
      showMainFieldSearch: false,
    }
  },
  methods: {
    hideMainFieldSearch() {
      this.clearSearch()
      this.onSearchChange(this.searchData)
    },
    clearSearch() {
      this.searchData = ''
      this.showMainFieldSearch = false
    },
    openMainFieldSearch() {
      this.showMainFieldSearch = true

      this.$nextTick(() => {
        let mainFieldSearchInput = this.$refs['mainFieldSearchInput']

        if (!isEmpty(mainFieldSearchInput)) {
          mainFieldSearchInput.focus()
        }
      })
    },
    onSearchChange(data) {
      this.$emit('onChange', data)
    },
  },
}
</script>
