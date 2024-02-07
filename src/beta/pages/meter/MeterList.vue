<script>
import { API } from '@facilio/api'
import { isEmpty, isArray } from '@facilio/utils/validation'
import ModuleList from 'src/beta/list/ModuleList.vue'
import UtilityTypeListDialog from 'src/beta/pages/meter/UtilityTypeListDialog'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: ModuleList,
  name: 'MeterList',
  components: {
    UtilityTypeListDialog,
  },
  data() {
    return {
      selectedCategory: null,
      canShowMeterCreation: false,
      canShowUtilityTypeDialog: false,
      showDialog: false,
      meterId: null,
      meterList: [],
    }
  },
  methods: {
    redirectToFormCreation() {
      if (this.moduleName === 'meter') {
        this.canShowUtilityTypeDialog = true
      } else {
        this.selectedCategory = {}
        this.openMeterCreation()
      }
    },
    async openMeterCreation(meter) {
      if (!isEmpty(meter)) {
        let { id, utilityType } = meter
        let { id: utilityTypeId } = utilityType
        if (!isEmpty(utilityTypeId)) {
          let selectedCategory = await this.getUtilityTypeById(utilityTypeId)
          if (!isEmpty(selectedCategory)) {
            this.$set(this, 'selectedCategory', selectedCategory)
          }
        }
        this.$set(this, 'meterId', id)
      } else {
        this.$set(this, 'meterId', null)
      }
      this.$set(this, 'canShowUtilityTypeDialog', false)
      this.$nextTick(() => {
        this.$set(this, 'canShowMeterCreation', true)
      })
    },
    async getUtilityTypeById(id) {
      let { utilitytype, error } = await API.fetchRecord('utilitytype', { id })
      if (!error) {
        return utilitytype || {}
      } else {
        this.$message.error(error?.message || 'Error Occured')
      }
      return {}
    },
    isPhysicalType(meter) {
      let { isVirtual } = meter || {}
      return !isVirtual
    },
    async editModule(record) {
      let { id } = record || {}
      let meter = this.meterList.find(meter => meter.id === id) || {}
      let { utilityType } = meter || {}
      let { id: categoryId } = utilityType
      if (!isEmpty(categoryId)) {
        let selectedCategory = await this.getUtilityTypeById(categoryId)
        if (!isEmpty(selectedCategory)) {
          this.$set(this, 'selectedCategory', selectedCategory)
          let categoryModuleName = selectedCategory.moduleName
          let utilityTypeId = (selectedCategory || {}).id
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('meter', pageTypes.EDIT) || {}
            name &&
              this.$router.push({
                name,
                params: {
                  id,
                },
                query: { categoryModuleName, utilityTypeId },
              })
          } else {
            this.$router.push({
              name: 'meter',
              params: {
                id,
              },
              query: { categoryModuleName, utilityTypeId },
            })
          }
        }
      }
    },
    async loadRecords(force = false) {
      let {
        moduleName,
        viewname,
        filters,
        page,
        perPage,
        sortObj,
        searchText,
      } = this

      await API.cancel({ uniqueKey: `${moduleName}_LIST` })
      await API.cancel({ uniqueKey: `${moduleName}_CUSTOM_BUTTON` })

      try {
        this.isLoading = true
        this.currentPageCount = null

        let params = {
          moduleName,
          viewname,
          filters,
          page,
          perPage,
          force,
          search: searchText,
          ...sortObj,
        }

        let { list, error } = await API.fetchAll('meter', params)
        if (!error) {
          this.meterList = list || []
          this.records = list || []
          if (isArray(this.records)) {
            this.currentPageCount = this.records.length
            this.isLoading = false
          }
        }
      } catch (error) {
        this.showErrorToastMessage(
          error,
          this.$t('custommodules.list.list_error')
        )
        this.isLoading = false
      }
    },
  },
  render() {
    let { selectedListItemsIds, canShowUtilityTypeDialog } = this || {}
    let { length: selectedLength } = selectedListItemsIds || []
    return (
      <FContainer height="100%" position="relative">
        {this.isSummaryOpen ? (
          <FSidebar
            sidebarWidth={280}
            toggleText="Side panel"
            title={this.moduleDisplayName}
            defaultOpen={false}
            {...{
              scopedSlots: {
                sidebar: () => {
                  return this.sidebar()
                },
                content: () => {
                  return <router-view></router-view>
                },
              },
            }}
          ></FSidebar>
        ) : (
          <CommonLayout
            moduleName={this.moduleName}
            getPageTitle={() => this.moduleDisplayName}
            {...{
              scopedSlots: {
                header: () => {
                  return this.header()
                },
                ['sub-header']: () => {
                  return this.subHeader()
                },
                default: () => {
                  return this.body()
                },
              },
            }}
          ></CommonLayout>
        )}{' '}
        <ColumnCustomization
          visible={this.showColumnSettings}
          moduleName={this.moduleName}
          viewName={this.viewname}
          {...{
            on: { 'update:visible': val => (this.showColumnSettings = val) },
          }}
        />
        <FContainer
          position="absolute"
          bottom="40px"
          display="flex"
          justifyContent="center"
          width="100%"
        >
          {!isEmpty(selectedListItemsIds) && (
            <FBulkToolbar
              recordCount={selectedLength}
              vOn:close={this.unselectAll}
            >
              <FButton
                appearance="secondary"
                size="small"
                vOn:click={() => this.deleteRecords(this.selectedListItemsIds)}
              >
                {this.$t('common._common.delete')}
              </FButton>
            </FBulkToolbar>
          )}
        </FContainer>
        {canShowUtilityTypeDialog && (
          <UtilityTypeListDialog
            canShowUtilityTypeDialog={this.canShowUtilityTypeDialog}
            selectedCategory={this.selectedCategory}
            VOn:openMeterCreation={this.openMeterCreation}
          ></UtilityTypeListDialog>
        )}
      </FContainer>
    )
  },
}
</script>
