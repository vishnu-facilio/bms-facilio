<template>
  <div class="fc-setup-page">
    <SetupHeader>
      <template #heading>
        {{ $t('setup.setup.impact') }}
      </template>
      <template #description>
        {{ $t('rule.create.list_of_all_impact') }}
      </template>
      <template #actions>
        <div class="flex-middle">
          <div class="action-btn setting-page-btn">
            <el-button
              type="primary"
              @click="addNewImpact"
              class="setup-el-btn"
            >
              {{ $t('rule.create.add_impact') }}
            </el-button>
          </div>
        </div>
      </template>
      <template #searchAndPagination class="p10">
        <div class="flex-middle">
          <pagination
            :total="impactCount"
            :current-page="impactPage"
            @pagechanged="setPage"
            class="mR15"
          ></pagination>
        </div>
      </template>
    </SetupHeader>
    <setup-loader v-if="loading">
      <template #setupLoading>
        <spinner :show="loading" size="80"></spinner>
      </template>
    </setup-loader>
    <setup-empty v-else-if="$validation.isEmpty(impactTemplates) && !loading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('rule.create.no_impact_available') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>
    <div v-else class="mL20 mT20 mR30">
      <el-table
        :data="impactTemplates"
        class="width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
        height="calc(100vh - 280px)"
      >
        <el-table-column prop="name" :label="$t('common.products._name')">
        </el-table-column>
        <el-table-column
          :label="$t('common._common.description')"
          prop="description"
        >
          <template v-slot="impactTemplate">
            {{ impactTemplate.row.description || '---' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="assetCategory"
          :label="$t('asset.assets.asset_category')"
        >
          <template v-slot="impactTemplate">
            {{ getAssetCategory(impactTemplate.row) }}
          </template>
        </el-table-column>
        <el-table-column
          prop
          label
          width="180"
          class="visibility-visible-actions"
          fixed="right"
        >
          <template v-slot="impactTemplate">
            <div class="text-center template-actions">
              <i
                class="el-icon-edit edit-icon visibility-hide-actions pR15"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editImpact(impactTemplate.row)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon visibility-hide-actions"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deleteImpact(impactTemplate.row)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <NewImpactTemplate
      v-if="showDialog"
      :isNew="isNew"
      :visibility.sync="showDialog"
      :selectedImpact="selectedImpact"
      @impactSaved="loadImpact"
    ></NewImpactTemplate>
  </div>
</template>
<script>
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import Pagination from 'pages/setup/AuditLog/AuditLogPagination'
import NewImpactTemplate from '@/NewImpactTemplate'
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    NewImpactTemplate,
    SetupHeader,
    SetupLoader,
    SetupEmpty,
    Pagination,
  },
  data() {
    return {
      isNew: false,
      showDialog: false,
      loading: false,
      impactTemplates: [],
      selectedImpact: null,
      impactCount: null,
      impactPage: 1,
      impactPerPage: 30,
    }
  },
  created() {
    this.loadImpact()
    this.$store.dispatch('view/loadModuleMeta', 'readingrule')
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategoryList: state => state.assetCategory,
      metaInfo: state => state.view.metaInfo,
    }),
    moduleName() {
      return 'faultImpact'
    },
  },
  watch: {
    impactPage() {
      this.loadImpact()
    },
  },
  methods: {
    addNewImpact() {
      this.selectedImpact = null
      this.isNew = true
      this.showDialog = true
    },
    async loadImpact() {
      this.loading = true
      let url = 'v3/modules/data/list'
      let { moduleName, impactPage, impactPerPage } = this
      let params = {
        page: impactPage,
        perPage: impactPerPage,
        withCount: true,
        viewName: 'all',
        force: false,
        moduleName: moduleName,
      }
      let { error, data, meta = {} } = await API.get(url, params)

      if (error) {
        this.$message.error('Error Occured')
      } else {
        let { faultImpact } = data || {}
        this.impactCount = this.$getProperty(
          meta,
          'pagination.totalCount',
          null
        )
        this.impactTemplates = faultImpact
      }
      this.loading = false
    },
    getAssetCategory(impact) {
      let { assetCategoryList } = this
      let { assetCategory } = impact || {}
      let { id: assetCategoryId } = assetCategory || {}

      if (assetCategoryId > 0 && !isEmpty(assetCategoryList)) {
        let category = assetCategoryList.find(
          category => category.id === assetCategoryId
        )
        if (!isEmpty(category)) {
          let { displayName } = category || {}
          return displayName || '---'
        }
      }
      return '---'
    },
    getFaultType(impact) {
      let { type } = impact || {}
      let faultTypeArr = []
      let { metaInfo } = this
      let { fields } = metaInfo || {}

      if (!isEmpty(fields)) {
        let faultField = fields.find(field => field.displayName === 'faultType')
        let { enumMap } = faultField || {}
        faultTypeArr = enumMap || []
      }
      let faultType = this.$getProperty(faultTypeArr, `${type}`, '')
      return faultType
    },
    editImpact(impact) {
      this.selectedImpact = impact
      this.isNew = false
      this.showDialog = true
    },
    async deleteImpact(impact) {
      let { moduleName } = this
      let value = await this.$dialog.confirm({
        title: this.$t('common.wo_report.delete_rule_title'),
        message: this.$t('rule.create.are_you_sure_to_delete_impact'),
        rbDanger: true,
        rbLabel: 'Delete',
      })

      if (value) {
        let { id } = impact || {}
        let url = 'v3/modules/data/delete'
        let params = {
          moduleName: moduleName,
          data: {
            faultImpact: [id],
          },
        }
        let { error, data } = await API.post(url, params)
        if (error) {
          this.$message.error('Error Occured')
        } else {
          if (!isEmpty(data)) {
            this.$message.success('rule.create.impact_deleted_successfully')
            this.loadImpact()
          }
        }
      }
    },
    setPage(page) {
      this.impactPage = page
    },
  },
}
</script>
<style scoped>
.impact-setup-table thead th {
  position: sticky;
  top: 0;
}
.impact-row {
  height: 60px;
}
</style>
