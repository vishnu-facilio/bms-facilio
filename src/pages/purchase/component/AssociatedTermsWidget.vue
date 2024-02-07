<template>
  <div ref="lineItems-container">
    <div
      v-if="$validation.isEmpty(termsAssociated)"
      class="fc-align-center-column height100"
    >
      <inline-svg
        src="svgs/emptystate/purchaseOrder"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="nowo-label">
        {{ $t('common.products.no_terms_associated') }}
      </div>
    </div>
    <div v-else class="tr-summary-table width100 pT20 pL30 pB20">
      <div class="widget-title mL0 mB10">
        {{ moduleDetailsMap[moduleName].title }}
      </div>
      <el-table :data="termsAssociated" class="width100" height="210px">
        <el-table-column
          prop="terms.name"
          :label="$t('common.roles.name')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="terms.termType"
          :label="$t('common.products.term_type')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="terms.shortDesc"
          :label="$t('common.products.short_description')"
          min-width="200"
        >
        </el-table-column>
        <el-table-column
          prop="terms"
          :label="$t('common.products.long_description')"
          min-width="200"
        >
          <template v-slot="termsData">
            <TermsSpecialFieldsList
              :terms="$getProperty(termsData, 'row.terms')"
            >
            </TermsSpecialFieldsList>
          </template>
        </el-table-column>
        <el-table-column
          v-if="canShowDelete"
          prop
          width="100"
          class="visibility-visible-actions"
        >
          <template v-slot="item">
            <div class="text-center">
              <span v-if="canShowDelete" @click="deleteRecord(item.row)">
                <inline-svg
                  src="svgs/delete"
                  class="pointer edit-icon-color visibility-hide-actions mL10"
                  iconClass="icon icon-sm icon-remove"
                ></inline-svg>
              </span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import TermsSpecialFieldsList from '@/list/TermsSpecialFieldsList'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  props: ['details', 'widget'],
  components: {
    TermsSpecialFieldsList,
  },
  data() {
    return {
      moduleDetailsMap: {
        quoteterms: {
          url: `v2/quotation/disassociateTerms`,
          title: 'Quote Associated Terms',
        },
        poterms: {
          url: `v2/purchaseorder/disassociateTerms`,
          title: 'PO Associated Terms',
        },
        prterms: {
          url: `v2/purchaserequest/disassociateTerms`,
          title: 'PR Associated Terms',
        },
      },
    }
  },
  computed: {
    termsAssociated() {
      return this.$getProperty(this, 'details.termsAssociated')
    },
    moduleName() {
      return this.$getProperty(this, 'widget.widgetParams.moduleName')
    },
    canShowDelete() {
      let { moduleName } = this
      let tAndCEabledModulesHash = {
        quoteterms: 'quote',
        prterms: 'purchaserequest',
        poterms: 'purchaseorder',
      }

      return this.$hasPermission(
        `${tAndCEabledModulesHash[moduleName]}: UPDATE`
      )
    },
  },
  methods: {
    async deleteRecord(item) {
      let { moduleName } = this
      if (this.moduleDetailsMap[moduleName]) {
        let url = (this.moduleDetailsMap[moduleName] || {}).url
        let successMsg = this.$t('common.products.terms_disassociated')
        let param = {
          recordIds: [`${item.id}`],
        }

        API.post(url, param).then(({ error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success(successMsg)
            eventBus.$emit('refesh-parent')
          }
        })
      }
    },
  },
}
</script>
<style lang="scss">
.tr-summary-table {
  .el-table th.is-leaf,
  .el-table th.el-table__cell > .cell {
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
