<template>
  <div v-if="visible" class="bulk-action-container">
    <span>{{ selectionText }}</span>
    <span
      v-if="!isSelected"
      class="bulk-select-button"
      @click="toggleSelection"
      >{{
        `${$t('maintenance.pm.select_all')} ${totalCount} ${$t(
          'filters.bulk_action.records'
        )}`
      }}</span
    >
    <span v-else class="bulk-select-button" @click="toggleSelection">{{
      $t('filters.bulk_action.clear_selection')
    }}</span>
  </div>
</template>

<script>
import { API } from '@facilio/api'
export default {
  name: 'BulkActionBar',
  props: ['totalCount', 'perPage', 'moduleName', 'visible', 'isAllSelected'],
  computed: {
    isSelected: {
      get() {
        return this.isAllSelected
      },
      set(value) {
        this.$emit('update:isAllSelected', value)
      },
    },
    selectedCount() {
      let { isSelected, perPage, totalCount } = this || {}
      if (isSelected) {
        return totalCount
      } else {
        return perPage
      }
    },
    selectionText() {
      let allText = this.$t('filters.bulk_action.all')
      let { selectedCount, isSelected } = this || {}
      let selectText = ''
      if (isSelected) {
        selectText = this.$t('filters.bulk_action.per_page_all_selected')
      } else {
        selectText = this.$t('filters.bulk_action.per_page_selected')
      }
      return `${allText} ${selectedCount} ${selectText}`
    },
  },
  watch: {
    visible(value) {
      if (!value) {
        this.isSelected = false
      }
    },
    isSelected: {
      handler(value) {
        this.$emit('update:isAllSelected', value)
      },
      immediate: true,
    },
  },
  methods: {
    toggleSelection() {
      this.isSelected = !this.isSelected
    },
    async updateRecord(params, filters) {
      let { moduleName } = this || {}
      let url = `v3/bulkActionPatch/${moduleName}?filters=${filters}`
      let data = {
        moduleName,
        data: {
          [moduleName]: params,
        },
      }
      let response = await API.post(url, data)
      return response
    },
    async deleteRecord(filters) {
      let { moduleName } = this || {}
      let url = `v3/bulkActionDelete/${moduleName}?filters=${filters}`
      let response = await API.post(url, {})
      return response
    },
    async createRecord(params, filters) {
      let url = `v3/bulkAction/create?filters=${filters}`
      let response = await API.post(url, params)
      return response
    },
  },
}
</script>
<style lang="scss" scoped>
.bulk-action-container {
  background-color: #fff;
  width: 100%;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.bulk-select-button {
  color: #39b2c2;
  padding: 10px 5px;
  margin: 0px 2px;
  cursor: pointer;
  border-radius: 4px;
  &:hover {
    text-decoration: underline;
  }
}
</style>
