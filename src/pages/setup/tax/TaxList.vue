<template>
  <div class="height100 d-flex flex-direction-column overflow-hidden">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('quotation.common.tax_rates') }}
        </div>
        <div class="heading-description">
          {{ $t('quotation.common.list_tax_rates') }}
        </div>
      </div>
      <div class="fc-multi-dropdown-btn">
        <el-dropdown trigger="click" @command="type => openNewTaxForm(type)">
          <el-button type="primary">
            {{ $t('common._common._new')
            }}<i class="el-icon-arrow-down pL10 font-black"></i>
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
    </div>
    <div class="m30 mT10 height100">
      <el-table
        ref="associateTaxTable"
        :data="allTaxList"
        class="setup-tax-table"
        height="calc(100vh - 170px)"
        v-loading="isLoading"
        row-key="index"
        :tree-props="{ children: 'childTaxes' }"
      >
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
        <el-table-column
          sortable
          :label="$t('common.products.rate')"
          prop="rate"
        >
        </el-table-column>
        <el-table-column
          sortable
          :label="$t('common._common.type')"
          prop="type"
        >
          <template slot-scope="data">
            {{ data.row.typeEnum | pascalCase }}
          </template>
        </el-table-column>
        <el-table-column width="130" class="visibility-visible-actions">
          <template slot-scope="data">
            <div class="text-center">
              <span @click="openEditTaxForm(data.row)">
                <inline-svg
                  src="svgs/edit"
                  class="edit-icon-color visibility-hide-actions"
                  iconClass="icon icon-sm mR5 icon-edit"
                ></inline-svg>
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
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
import { API } from '@facilio/api'
import TaxForm from './TaxForm'
import { isEmpty, isArray } from '@facilio/utils/validation'

export default {
  components: {
    TaxForm,
  },
  data() {
    return {
      formVisibility: false,
      selectedRecord: null,
      formType: '',
      allTaxList: [],
      individualTaxes: [],
      isLoading: false,
    }
  },
  created() {
    this.loadAllTaxes()
  },
  methods: {
    openNewTaxForm(formType) {
      this.selectedRecord = null
      this.formType = formType
      this.formVisibility = true
    },
    openEditTaxForm(record) {
      this.selectedRecord = record
      this.formType = record.type
      this.formVisibility = true
    },
    async loadAllTaxes() {
      this.isLoading = true

      let { list = [], error } = await API.fetchAll(`tax`, {
        withCount: false,
        viewName: 'active',
      })
      if (error) {
        let {
          message = this.$t(
            'common._common.error_occured_while_fetching_tax_list'
          ),
        } = error
        this.$message.error(message)
      } else {
        let index = 1
        if (!isEmpty(list) && isArray(list)) {
          list.forEach(item => {
            this.$setProperty(item, 'index', index++)
            if (!isEmpty(item.childTaxes) && isArray(item.childTaxes)) {
              item.childTaxes.forEach(child => {
                this.$setProperty(child, 'index', index++)
              })
            }
          })
          this.allTaxList = list
          this.individualTaxes = list.filter(t => t.type === 1)
        }
      }

      this.isLoading = false
    },
    async disableTax(record) {
      let recordId = record.id
      let isActive = record.isActive

      let { error } = await API.updateRecord('tax', {
        id: recordId,
        data: { isActive },
      })

      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.loadAllTaxes()
        this.$message.success(
          this.$t('common.products.tax_updated_successfully')
        )
      }
    },
  },
}
</script>
