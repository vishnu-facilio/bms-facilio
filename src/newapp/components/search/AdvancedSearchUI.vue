<template>
  <div class="advance-search-ui">
    <fc-icon
      group="default"
      name="filter-outline"
      size="14"
      class="d-flex cursor-pointer"
      @click="showAdvWizard = true"
    ></fc-icon>
    <div v-if="!isEmpty(filterList)" class="dot-active-pink"></div>

    <el-dialog
      :visible.sync="showAdvWizard"
      :append-to-body="true"
      :show-close="true"
      :title="`Filter ${moduleDisplayName} by`"
      custom-class="advance-search-dialog"
      class="advance-search-ui-dialog"
    >
      <template v-if="showAdvWizard">
        <AdvancedSearch
          :key="`filter-${moduleName}`"
          ref="advancedSearch"
          :moduleName="moduleName"
          :hideQuery="hideQuery"
          :filterList="filterList"
          :showAdvWizard="showAdvWizard"
          @applyFilters="applyFilters"
          @closeDialog="showAdvWizard = false"
        />
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { AdvancedSearch } from '@facilio/criteria'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['filterList', 'moduleName', 'moduleDisplayName', 'hideQuery'],
  components: { AdvancedSearch },
  data: () => ({
    showAdvWizard: false,
    isEmpty,
  }),
  methods: {
    applyFilters(filters) {
      this.showAdvWizard = false
      this.$emit('applyFilters', filters)
    },
  },
}
</script>
<style lang="scss">
.advance-search-ui {
  position: relative;

  .dot-active-pink {
    position: absolute;
    top: -6px;
    right: -10px;
    background-color: #3ab2c2;
  }
}
.advance-search-ui-dialog .advance-search-dialog {
  width: 30%;
  overflow: hidden;
  height: 100vh;
  display: flex;
  flex-direction: column;
  margin: 0px;
  margin-left: auto;
  margin-top: 0px !important;

  .el-dialog__body {
    overflow: scroll;
    padding: 0px;
    height: 100%;
  }
}
</style>
