<template>
  <el-dialog
    :title="$t('commissioning.sheet.filter')"
    :visible.sync="canShowDialog"
    class="handson-column-filter fc-dialog-center-container"
    :append-to-body="true"
    width="30%"
  >
    <div class="height150">
      <div class="mT10">{{ $t('commissioning.sheet.filter_by_value') }}</div>
      <div>
        <el-input class="fc-input-full-border2 mT5" v-model="searchText">
        </el-input>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog"
        >Cancel</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="setFilter"
        >Confirm</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: {
    canShowFilterDialog: {
      type: Boolean,
    },
    currentFilterColumn: {
      type: Number,
    },
    filteredCondition: {
      type: Array,
    },
  },
  data() {
    return {
      searchText: '',
    }
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canShowFilterDialog
      },
      set(value) {
        this.$emit('update:canShowFilterDialog', value)
      },
    },
    activeColumnIndex: {
      get() {
        return this.currentFilterColumn
      },
      set(value) {
        this.$emit('update:currentFilterColumn', value)
      },
    },
    selectedColumn() {
      let { filteredCondition, activeColumnIndex } = this
      let selectedColumn = {}
      if (!isEmpty(filteredCondition)) {
        selectedColumn = filteredCondition.find(
          condition => condition.columnIndex === activeColumnIndex
        )
      }
      return selectedColumn
    },
    selectedColumnIndex() {
      let { filteredCondition, activeColumnIndex } = this
      let selectedColumnIndex = null
      if (!isEmpty(filteredCondition)) {
        selectedColumnIndex = filteredCondition.findIndex(
          condition => condition.columnIndex === activeColumnIndex
        )
      }
      return selectedColumnIndex
    },
  },
  created() {
    let { selectedColumn } = this
    if (!isEmpty(selectedColumn)) {
      let { searchText } = selectedColumn
      this.$set(this, 'searchText', searchText)
    }
  },
  methods: {
    setFilter() {
      let {
        searchText,
        filteredCondition,
        activeColumnIndex,
        selectedColumn,
        selectedColumnIndex,
      } = this
      let conditionArr = filteredCondition
      if (!isEmpty(selectedColumn)) {
        if (!isEmpty(searchText)) {
          selectedColumn.searchText = searchText
        } else {
          conditionArr.splice(selectedColumnIndex, 1)
        }
      } else if (!isEmpty(searchText)) {
        conditionArr.push({
          columnIndex: activeColumnIndex,
          searchText,
        })
      }
      this.$emit('update:filteredCondition', conditionArr)
      this.closeDialog()
    },
    closeDialog() {
      this.canShowDialog = false
      this.activeColumnIndex = null
    },
  },
}
</script>
