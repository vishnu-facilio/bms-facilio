<template>
  <FContainer class="related-list-widget">
    <portal :to="`action-${widget.id}-${widget.name}`">
      <FContainer class="related-list-widget-header-action">
        <MainFieldSearch
          v-if="!isEmpty(recordList) || !isEmpty(searchText)"
          :mainFieldObj="mainFieldObj"
          :search.sync="searchText"
        ></MainFieldSearch>
        <template v-if="!isEmpty(recordList)">
          <FDivider height="24px" style="margin: 0px 12px" />
          <FIcon
            group="action"
            name="add-column"
            size="18"
            :pressable="true"
            color="#283648"
            @click="showColumnSettings = true"
          ></FIcon>
        </template>
      </FContainer>
    </portal>
    <FContainer v-if="showLoading">
      <FSpinner :size="30" />
    </FContainer>
    <FContainer
      v-else-if="isEmpty(recordList)"
      class="related-list-empty-state"
    >
      <InlineSvg src="svgs/list-empty" iconClass="icon icon-130"></InlineSvg>
      <FText fontWeight="bold">{{ emptyStateText }}</FText>
    </FContainer>
    <CommonList
      v-else-if="!isEmpty(recordList)"
      :viewDetail="viewDetail"
      :records="recordList"
      :slotList="slotList"
      :hideBorder="true"
      :orgDetails="orgDetails"
      selectType=""
      :mainFieldAction="mainFieldAction"
    >
      <template #[slotList[0].name]="{ record }">
        <FText color="textCaption">{{ '#' + record.id }}</FText>
      </template>
    </CommonList>
    <ColumnCustomization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      :viewName="viewname"
    ></ColumnCustomization>
    <portal :to="`footer-${widget.id}-${widget.name}`">
      <Pagination
        v-if="!isEmpty(recordCount) || !isEmpty(recordList)"
        :key="`pagination-${moduleName}`"
        :totalCount="recordCount"
        :currentPageNo.sync="page"
        :currentPageCount="(recordList || []).length"
        :perPage="perPage"
      />
    </portal>
  </FContainer>
</template>
<script>
import { CommonList } from '@facilio/ui/new-app'
import {
  FText,
  FContainer,
  FSpinner,
  FIcon,
  FDivider,
} from '@facilio/design-system'
import Pagination from 'src/beta/list/Pagination'
import ColumnCustomization from '@/ColumnCustomization'
import { API } from '@facilio/api'
import { RelatedListData } from 'pageWidgets/common/line-items/RelatedListData'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { findRouteForModule, pageTypes } from '@facilio/router'
import MainFieldSearch from '../../components/MainFieldSearch.vue'

export default {
  props: ['widget', 'details'],
  components: {
    CommonList,
    Pagination,
    ColumnCustomization,
    MainFieldSearch,
    FText,
    FSpinner,
    FContainer,
    FIcon,
    FDivider,
  },
  data() {
    return {
      showColumnSettings: false,
      recordList: [],
      recordCount: null,
      page: 1,
      isLoading: false,
      viewDetail: {},
      viewLoading: false,
      searchText: null,
      isEmpty,
    }
  },
  created() {
    this.init()
  },
  computed: {
    modelDataClass() {
      return RelatedListData
    },
    relatedListObj() {
      return this.$getProperty(this.widget, 'relatedList') || {}
    },
    relatedFieldName() {
      return this.$getProperty(this.relatedListObj, 'field.name')
    },
    moduleName() {
      return this.$getProperty(this.relatedListObj, 'module.name') || ''
    },
    perPage() {
      return 5
    },
    moduleDisplayName() {
      return this.$getProperty(this.relatedListObj, 'module.displayName') || ''
    },
    viewname() {
      return 'hidden-all'
    },
    title() {
      let { moduleDisplayName, relatedListObj } = this
      let { relatedListDisplayName } = relatedListObj?.field || {}

      return relatedListDisplayName || moduleDisplayName
    },
    mainFieldObj() {
      let { fields } = this.viewDetail || {}
      let { field: mainField } =
        (fields || []).find(viewFld => viewFld?.field?.mainField) || {}
      return mainField || {}
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 80,
            displayName: 'ID',
            fixed: true,
          },
        },
      ]
    },
    emptyStateText() {
      let { moduleDisplayName } = this
      return this.$t('setup.relationship.no_module_available', {
        moduleName: moduleDisplayName,
      })
    },
    showLoading() {
      return this.isLoading || this.viewLoading
    },
    recordId() {
      let { id } = this.details
      return id
    },
    orgDetails() {
      return {
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
      }
    },
    routeName() {
      let { moduleName } = this
      let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
      return name || null
    },
    mainFieldAction() {
      return this.routeName ? this.redirectToOverview : null
    },
  },
  watch: {
    page() {
      this.loadRecords(true)
    },
    searchText() {
      this.loadRecords(true)
    },
  },
  methods: {
    init() {
      this.loadViewDetails()
      this.loadRecords()
    },
    async loadRecords(force = false) {
      let {
        moduleName,
        viewname,
        perPage,
        recordId,
        relatedFieldName,
        $attrs,
        page,
        searchText,
      } = this
      let { moduleName: currentModuleName } = $attrs
      let params = {
        moduleName,
        viewname,
        perPage,
        recordId,
        relatedFieldName,
        currentModuleName,
        page,
        search: searchText,
        force,
      }

      this.isLoading = true
      try {
        let recordList = await this.modelDataClass.fetchAll(params)
        if (isArray(recordList)) this.recordList = recordList || []

        this.recordCount = this.modelDataClass.recordListCount
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Unable to fetch moduleList')
      }
      this.isLoading = false
    },
    async loadViewDetails() {
      this.viewLoading = true

      try {
        let { moduleName, viewname } = this
        let { error, data } = await API.get(`v2/views/${viewname}`, {
          moduleName,
        })

        if (error) throw error
        else this.viewDetail = data?.viewDetail || {}
      } catch (errorMsg) {
        this.$message.error(errorMsg || 'Cannot fetch view details')
      }
      this.viewLoading = false
    },
    redirectToOverview({ record, value }) {
      let { routeName } = this
      let { id } = record || {}

      if (routeName) {
        let route = this.$router.resolve({
          name: routeName,
          params: { viewname: 'all', id },
        }).href

        return { url: route, target: '_blank', text: value }
      }
    },
  },
}
</script>
<style lang="scss">
.related-list-widget {
  display: flex;
  align-items: center;
  justify-content: center;

  .related-list-empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
}
.related-list-widget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 52px;
}
.related-list-widget-header-action {
  display: flex;
  align-items: center;
}
</style>
