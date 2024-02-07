<template>
  <div class="dashboard-lookup-filter-mobile">
    <div class="filter-label">
      <p class="filter-label-text mB0  textoverflow-ellipsis width80">
        {{ userFilterObj.label }}
      </p>
    </div>

    <el-select
      popper-class="db-filter-mobile-options"
      class="fc-input-full-border width100"
      placeholder="loading ..."
      v-if="initialOptionsLoading && !isEditMode"
      :value="{}"
    >
      <!-- el select ,ID is shown in the selext box until options are loaded. to avoid ID flickering ,use temp empty select box with placeholder-->
    </el-select>
    <el-select
      popper-class="db-filter-mobile-options"
      placeholder="Type to search"
      :loading="initialOptionsLoading || remoteOptionsLoading"
      loading-text="loading ... "
      class="fc-input-full-border width100"
      :multiple="userFilterObj.componentType == 2"
      collapse-tags
      :filterable="isLazyLoad"
      v-model="elSelectModel"
      v-if="!initialOptionsLoading && !isEditMode"
      @change="valueChanged"
      :remote="isLazyLoad"
      :remote-method="handleRemoteSearch"
    >
      <el-option
        v-for="option in elSelectOptions"
        :label="option.label"
        :value="option.value"
        :key="option.value"
      >
      </el-option>
    </el-select>
  </div>
</template>

<script>
import DashboardLookupFilter from '../DashboardLookupFilter'

export default {
  extends: DashboardLookupFilter,
}
</script>

<style lang="scss">
@import '../style/_variables';
//set font size to pop over , popver appends to body  ,so have to set seperately
.db-filter-mobile-options {
  font-family: 'ProximaNova' !important;
}
.dashboard-lookup-filter-mobile {
  .el-input__inner {
    font-family: 'ProximaNova' !important;
    height: 45px;
    font-size: 14px !important;
    font-weight: 500;
  }

  .filter-label {
    display: flex;
    margin-bottom: 5px;

    margin-left: 3px;

    .el-input__inner {
      color: #191a45;
      letter-spacing: 0.3px;
      padding-left: 15px !important;
      padding-right: 15px !important;
    }
  }
  .filter-label-text {
    font-size: 14px;
    font-weight: 700;
    letter-spacing: 0.3px;
    color: #191a45;
  }
}
</style>
