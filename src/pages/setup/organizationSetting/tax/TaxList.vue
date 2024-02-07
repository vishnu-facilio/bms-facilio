<template>
  <div class="height500 d-flex flex-direction-column overflow-hidden p15">
    <el-table
      ref="associateTaxTable"
      :data="allTaxList"
      class="setup-tax-table"
      height="450"
      style="width: 100%"
      v-loading="isLoading"
      row-key="index"
      :tree-props="{ children: 'childTaxes' }"
      :header-cell-style="{ background: '#f3f1fc' }"
    >
      <template slot="empty">
        {{ $t('common._common.no_tax_available') }}
      </template>
      <el-table-column
        sortable
        :label="$t('common.products.tax_name')"
        prop="name"
      >
      </el-table-column>
      <el-table-column sortable :label="$t('common.products.rate')" prop="rate">
      </el-table-column>
      <el-table-column sortable :label="$t('common._common.type')" prop="type">
        <template slot-scope="data">
          {{ data.row.typeEnum | pascalCase }}
        </template>
      </el-table-column>
      <el-table-column width="130" class="visibility-visible-actions">
        <template slot-scope="data">
          <div class="text-center" @click="openEditTaxForm(data.row)">
            <inline-svg
              src="svgs/edit"
              class="edit-icon-color visibility-hide-actions"
              iconClass="icon icon-sm mR5 icon-edit"
            ></inline-svg>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <portal to="organization-action-buttons">
      <div class="fc-multi-dropdown-btn">
        <el-dropdown trigger="click" @command="type => openNewTaxForm(type)">
          <el-button type="primary">
            {{ $t('setup.add.add') }}
            <i class="el-icon-arrow-down pL10 font-black"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item key="1" :command="1">{{
              $t('quotation.common.tax')
            }}</el-dropdown-item>
            <el-dropdown-item key="2" :command="2">{{
              $t('quotation.common.tax_group')
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </portal>
    <TaxForm
      v-if="formVisibility"
      :visibility.sync="formVisibility"
      :record="selectedRecord"
      :type="formType"
      :individualTaxes="individualTaxes"
      @saved="loadAllTaxes"
    ></TaxForm>
  </div>
</template>
<script>
import TaxList from 'src/pages/setup/tax/TaxList.vue'

export default {
  extends: TaxList,
}
</script>
<style lang="scss">
.table-header {
  background-color: #f3f1fc;
}

.setup-tax-table {
  .el-table__cell {
    padding-left: 20px;
  }
}
</style>
