<template>
  <div v-if="visible" class="bulk-action-container">
    <span>{{ selectionText }}</span>
    <span
      v-if="!isSelected && totalCount <= limit"
      class="bulk-select-button"
      @click="toggleSelection"
      >{{
        `${$t('maintenance.pm.select_all')} ${totalCount} ${$t(
          'filters.bulk_action.records'
        )}`
      }}</span
    >
    <span
      v-else-if="isSelected && totalCount <= limit"
      class="bulk-select-button"
      @click="toggleSelection"
      >{{ $t('filters.bulk_action.clear_selection') }}</span
    >
    <span class="bulk-select-error" v-if="totalCount > limit">{{
      warningText
    }}</span>
  </div>
</template>
<script>
import BulkActionBar from 'src/newapp/list/BulkActionBar.vue'
export default {
  extends: BulkActionBar,
  props: [
    'totalCount',
    'perPage',
    'moduleName',
    'visible',
    'isAllSelected',
    'selectedItem',
    'limit',
  ],

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
        selectedCount = this.selectedItem
      }
      return `${allText} ${selectedCount} ${selectText}`
    },
    warningText() {
      let message = ''

      message = `. Bulk update is limited to ${this.limit} records. Please make sure your selection stays within this limit`

      return message
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
      this.$emit('change', this.isSelected)
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
<style>
.bulk-select-error {
  padding: 10px 5px;
  margin: 0px 2px;
  cursor: pointer;
  border-radius: 4px;
}
</style>
