<template>
  <div v-if="visibility">
    <el-dialog
      :visible.sync="visibility"
      :title="$t('common.header.associate_terms')"
      :fullscreen="false"
      open="top"
      width="65%"
      :before-close="closeDialog"
      custom-class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <div class="height350">
        <el-table
          :data="termsAndConditionTableList"
          ref="table1"
          @selection-change="selectTandC"
          height="320"
          :empty-text="$t('common.products.no_tc_available_associate')"
          v-loading="loading"
          :default-sort="{ prop: 'name', order: 'descending' }"
          class="inventory-inner-table width100 termAsscociationTable "
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
        </el-table>
      </div>

      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog()">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="associateTerms()"
          :loading="saving"
          >{{ $t('common._common.add') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isArray } from '@facilio/utils/validation'
export default {
  props: ['visibility', 'record', 'associateUrl'],
  data() {
    return {
      loading: false,
      saving: false,
      selectedTandC: [],
      termsAndConditionTableList: [],
    }
  },
  created() {
    this.loadAllTermsAndConditions()
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    selectTandC(list) {
      this.selectedTandC = list
    },
    async loadAllTermsAndConditions() {
      this.loading = true
      let params = {
        filters: JSON.stringify({
          isPublished: { operatorId: 15, value: ['true'] },
          isRevised: { operatorId: 15, value: ['false'] },
        }),
      }
      let { list, error } = await API.fetchAll('termsandconditions', params)
      if (error) {
        this.$message.error(
          error ||
            this.$t(
              'common._common.error_while_fetching_terms_and_conditions_list'
            )
        )
      } else {
        let termsAssociated = this.$getProperty(
          this,
          'record.termsAssociated',
          []
        )
        let associatedTermsId
        if (isArray(termsAssociated)) {
          associatedTermsId = termsAssociated.map(term =>
            this.$getProperty(term, 'terms.id', -1)
          )
        }
        this.termsAndConditionTableList = (list || []).filter(record => {
          return !associatedTermsId.includes(record.id)
        })
      }
      this.loading = false
    },
    async associateTerms() {
      let tempTandCList = []
      for (let tandC of this.selectedTandC) {
        tempTandCList.push({
          terms: { id: tandC.id },
        })
      }
      let param = {
        termsAssociated: tempTandCList,
        recordId: this.record.id,
      }
      this.saving = true
      let { error } = await API.post(this.associateUrl, param)
      if (error) {
        this.$message.error(
          error || this.$t('common.wo_report.unable_to_associate_terms')
        )
      } else {
        this.$emit('refreshSummary')
        this.closeDialog()
      }
      this.saving = false
    },
  },
}
</script>
<style lang="scss">
.termAsscociationTable {
  th {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
}
</style>
