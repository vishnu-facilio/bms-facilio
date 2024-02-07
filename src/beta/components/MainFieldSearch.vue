<template>
  <FContainer v-if="canShowMainFieldSearch">
    <FInput
      v-if="showMainFieldSearch"
      v-model="searchText"
      ref="mainFieldSearchInput"
      :placeholder="$t('common._common.search')"
      appearance="default"
      size="small"
      @blur="hideMainFieldSearch"
      @change="emitSearchText"
    >
      <template #prefix>
        <FIcon
          group="action"
          name="search"
          size="18"
          color="#7B91B0"
          :pressable="false"
        ></FIcon>
      </template>
    </FInput>
    <FIcon
      v-else
      group="action"
      name="search"
      size="18"
      color="#7B91B0"
      @click="openSearch"
    ></FIcon>
  </FContainer>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { FContainer, FInput, FIcon } from '@facilio/design-system'

export default {
  props: ['mainFieldObj', 'search'],
  components: { FContainer, FInput, FIcon },
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
        !isEmpty(searchBox) && searchBox.selectInput()
      })
    },
    hideMainFieldSearch() {
      if (isEmpty(this.searchText)) this.showMainFieldSearch = false
    },
    emitSearchText() {
      this.$emit('update:search', this.searchText)
      this.$emit('onSearch', this.searchText)
    },
  },
}
</script>
