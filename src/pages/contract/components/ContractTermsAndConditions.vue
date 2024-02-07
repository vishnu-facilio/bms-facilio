<template>
  <div>
    <el-table
      :data="summaryData.termsAssociated"
      v-loading="loading.terms"
      :empty-text="$t('common.products.no_terms_associated')"
      :default-sort="{ prop: 'terms.name', order: 'descending' }"
      class="width100 inventory-inner-table"
    >
      <el-table-column
        prop="terms.name"
        sortable
        :label="$t('common.products._name')"
      ></el-table-column>
      <el-table-column
        prop="terms.shortDesc"
        :label="$t('common.products.short_description')"
      ></el-table-column>
      <el-table-column
        prop="terms.termType"
        :label="$t('common._common._type')"
      ></el-table-column>
      <el-table-column width="80" v-if="summaryData.status === 1">
        <template v-slot="scope">
          <div class="visibility-hide-actions export-dropdown-menu">
            <i
              class="el-icon-delete pointer edit-icon-color pL18"
              v-if="$hasPermission('inventory:UPDATE')"
              data-arrow="true"
              :title="$t('common.header.delete_terms')"
              v-tippy
              @click="deleteAssociatedTerms(scope.row.id, type)"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="addTandCVisibility">
      <el-dialog
        :visible.sync="addTandCVisibility"
        :title="$t('common.header.associate_terms')"
        :fullscreen="false"
        open="top"
        width="65%"
        :before-close="cancelForm"
        custom-class="assetaddvaluedialog fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
        :append-to-body="true"
      >
        <div class="height350">
          <el-table
            :data="termsAndConditionTableList"
            ref="table1"
            @selection-change="selectTandC"
            height="320"
            :empty-text="$t('common.products.no_tc_available_associate')"
            v-loading="loading.allTerms"
            :default-sort="{ prop: 'name', order: 'descending' }"
            class="inventory-inner-table width100"
          >
            <el-table-column width="60" type="selection"></el-table-column>
            <el-table-column
              prop="name"
              sortable
              :label="$t('common.products._name')"
            ></el-table-column>
            <el-table-column
              prop="shortDesc"
              :label="$t('common.products.short_description')"
            ></el-table-column>
            <el-table-column
              prop="termType"
              :label="$t('common._common.type')"
            ></el-table-column>
          </el-table>
        </div>

        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            v-if="addTandCVisibility"
            @click="addTerms()"
            :loading="loading.associateTerms"
            >{{ $t('common._common._add') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import contractMixin from 'pages/contract/mixin/contractHelper'
export default {
  props: ['summaryData', 'type', 'addTandCVisibility'],
  mixins: [contractMixin],
  data() {
    return {
      termsAndConditionList: [],
      loading: {
        terms: false,
        associateTerms: false,
        allTerms: false,
      },
      saving: false,
      selectedTandCObj: null,
    }
  },
  computed: {
    termsAndConditionTableList() {
      return this.termsAndConditionList
    },
  },
  watch: {
    addTandCVisibility(newVal) {
      if (newVal) {
        this.loadTermsAndConditionList()
      }
    },
  },
  methods: {
    addTerms() {
      this.associateTermsAndCondition(
        this.summaryData,
        this.selectedTandCObj,
        this.type
      )
    },
    cancelForm() {
      this.selectedTandCObj = null
      this.$emit('update:addTandCVisibility', false)
    },
    selectTandC(val) {
      this.selectedTandCObj = val
    },
  },
}
</script>
