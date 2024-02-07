<script>
import {
  FButton,
  FText,
  FIcon,
  FContainer,
  FSpinner,
  FSidebar,
  FInput,
  FTooltip,
  FBulkToolbar,
  fDialog,
} from '@facilio/design-system'
import { CommonList } from '@facilio/ui/new-app'
import CommonLayout from './CommonLayout'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import Pagination from './Pagination.vue'
import AdvancedSearchWrapper from 'newapp/components/search/AdvancedSearchWrapper.vue'
import ColumnCustomization from './views/ColumnCustomization'
import { findRouteForModule, pageTypes } from '@facilio/router'
import isEqual from 'lodash/isEqual'
import debounce from 'lodash/debounce'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: CommonModuleList,
  name: 'ModuleList',
  components: {
    CommonLayout,
    FButton,
    CommonList,
    FText,
    FIcon,
    FContainer,
    Pagination,
    AdvancedSearchWrapper,
    ColumnCustomization,
    FSpinner,
    FSidebar,
    FInput,
    FTooltip,
    FBulkToolbar,
  },
  data() {
    return {
      searchText: '',
    }
  },
  computed: {
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
        {
          criteria: JSON.stringify({ name: 'name' }),
        },
        {
          name: 'action',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
    recordsList() {
      return this.records
    },
    modifiedViewDetails() {
      return this.viewDetail
    },
  },
  watch: {
    searchText(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.loadDebouncedRecords()
      }
    },
  },
  methods: {
    unselectAll() {
      let { $refs } = this || {}
      let element = $refs['common-list']
      if (!isEmpty(element)) {
        element.unselectAll()
        this.selectedListItemsObj = []
        this.selectedListItemsIds = []
      }
    },
    loadDebouncedRecords: debounce(function() {
      this.loadRecords()
    }, 500),
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this
      let { query } = $route

      let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
      let route = { name, params: { viewname, id }, query }

      this.$router.push(route)
    },
    async deleteRecords(idList) {
      let { moduleDisplayName, moduleName } = this

      let value = await fDialog.danger({
        title: `${this.$t('custommodules.list.delete')} ${moduleDisplayName}`,
        description: `${this.$t(
          'custommodules.list.delete_confirmation'
        )} ${moduleDisplayName}?`,
        saveText: this.$t('custommodules.list.delete'),
      })

      if (value) {
        this.isLoading = true
        try {
          await this.modelDataClass.delete(moduleName, idList)
          this.$message.success(
            `${moduleDisplayName} ${this.$t(
              'custommodules.list.delete_success'
            )}`
          )
          this.selectedListItemsIds = []
          this.selectedListItemsObj = []
          await this.refreshRecordDetails(true)
        } catch (error) {
          this.showErrorToastMessage(
            error,
            this.$t('custommodules.list.delete_error')
          )
        }
        this.isLoading = false
      }
    },
    // UI Methods
    header() {
      let { canHideFilter, metaInfo, createBtnText } = this || {}
      return (
        <div class="flex items-center">
          {!canHideFilter && (
            <AdvancedSearchWrapper
              filters={this.filters}
              moduleName={this.moduleName}
              moduleDisplayName={this.moduleDisplayName}
            ></AdvancedSearchWrapper>
          )}
          {!canHideFilter && <span class="separator">|</span>}
          {!isEmpty(metaInfo) && (
            <FButton vOn:click={() => this.redirectToFormCreation()}>
              <FText
                fontsize="body3"
                fontWeight="medium"
                overflow="hidden"
                textOverflow="ellipsis"
              >
                {createBtnText}
              </FText>
            </FButton>
          )}
        </div>
      )
    },
    subHeader() {
      let {
        currentPageCount,
        recordCount,
        showLoading,
        records,
        perPage,
        showListView,
      } = this || {}
      return (
        <FContainer
          padding="containerXLarge"
          display="flex"
          justifyContent="space-between"
          alignItems="center"
        >
          <div>
            {!showLoading && !isEmpty(records) && (
              <FText appearance="headingMed14">{`${currentPageCount} of ${recordCount} Records`}</FText>
            )}
          </div>

          {!isEmpty(records) && showListView && (
            <div class="flex items-center">
              {!isEmpty(recordCount) && (
                <Pagination
                  totalCount={recordCount}
                  currentPageCount={this.currentPageCount}
                  perPage={perPage}
                />
              )}
              <span class="separator">|</span>
              <div vOn:click={() => this.toShowColumnSettings()}>
                <FIcon
                  group="action"
                  name="add-column"
                  size="18"
                  pressable={true}
                  color="#283648"
                ></FIcon>
              </div>
            </div>
          )}
        </FContainer>
      )
    },
    body() {
      let { showLoading, viewname, records } = this || {}
      return (
        <div>
          {showLoading ? (
            <div class="cm-dsm-container flex items-center justify-center">
              <FSpinner size={30} />
            </div>
          ) : isEmpty(viewname) ? (
            this.viewEmptyState()
          ) : isEmpty(records) ? (
            this.recordEmptyState()
          ) : (
            this.moduleTable()
          )}
        </div>
      )
    },
    viewEmptyState() {
      return (
        <div class="cm-dsm-container">
          <inline-svg
            src="svgs/no-configuration"
            class="d-flex module-view-empty-state"
            iconClass="icon"
          ></inline-svg>
          <FText>{this.$t('viewsmanager.list.no_view_config')}</FText>
          <div class="mB20"></div>
          <FButton vOn:click={this.openViewCreation}>
            {this.$t('viewsmanager.list.add_view')}
          </FButton>
        </div>
      )
    },
    recordEmptyState() {
      let { emptyStateText } = this || {}
      return (
        <div class="cm-dsm-container">
          <img
            class="mT20 self-center"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
          <FText fontWeight="bold">{emptyStateText}</FText>
        </div>
      )
    },
    moduleTable() {
      let { currentPageCount, slotsContent } = this || {}
      return (
        <CommonList
          ref="common-list"
          viewDetail={this.modifiedViewDetails}
          records={this.recordsList}
          slotList={this.slotList}
          hideBorder={true}
          showSelectBar={false}
          style={{
            height: `${currentPageCount > 10 ? 'calc(100vh - 210px)' : ''}`,
            overflow: 'scroll',
          }}
          orgDetails={this.$account.org}
          vOn:recordSelected={this.selectItems}
          {...{
            scopedSlots: slotsContent(),
          }}
        ></CommonList>
      )
    },
    slotsContent() {
      let { slotList, mainFieldName } = this || {}
      return {
        [slotList[0].name]: ({ record } = {}) => {
          return (
            <div>
              <FText color="textMain">{'#' + record.id}</FText>
            </div>
          )
        },
        [slotList[1].criteria]: ({ record } = {}) => {
          return (
            <FTooltip placement="bottomLeft" mouseEnterDelay={1}>
              <FText
                slot="title"
                fontsize="body3"
                fontWeight="medium"
                color="backgroundCanvas"
              >
                {record[mainFieldName]}
              </FText>
              <FButton
                appearance="link"
                vOn:click={() => this.redirectToOverview(record.id)}
              >
                <FText
                  fontsize="body3"
                  fontWeight="medium"
                  overflow="hidden"
                  textOverflow="ellipsis"
                >
                  {record[mainFieldName]}
                </FText>
              </FButton>
            </FTooltip>
          )
        },
        [slotList[2].name]: ({ record } = {}) => {
          return (
            <FContainer display="flex" gap="containerMedium">
              <FIcon
                group="edit"
                name="edit-line"
                size="16"
                vOn:click={() => this.editModule(record)}
              />
              <FIcon
                group="default"
                name="trash-can"
                size="16"
                vOn:click={() => this.deleteRecords([record.id])}
              />
            </FContainer>
          )
        },
      }
    },
    sidebar() {
      let {
        recordCount,
        perPage,
        currentPageCount,
        records,
        selectedRecordId,
      } = this || {}
      return (
        <div>
          <FContainer
            padding="containerXLarge containerXxLarge"
            borderBottom="solid 0.5px"
            borderColor="borderNeutralBaseSubtle"
          >
            <FInput
              placeholder="Search"
              v-model={this.searchText}
              {...{
                scopedSlots: {
                  prefix: () => {
                    return (
                      <fc-icon group="action" name="search" size="16"></fc-icon>
                    )
                  },
                },
              }}
            ></FInput>
          </FContainer>

          <FContainer
            height="calc(100vh - 220px)"
            overflow="scroll"
            marginTop="containerXLarge"
          >
            {this.showLoading ? (
              <div class="h-full flex items-center justify-center">
                <FSpinner size={30} />
              </div>
            ) : (
              records.map(record => {
                return (
                  <FContainer
                    key={record.id}
                    hover-backgroundColor="backgroundNeutralHovered"
                    backgroundColor={
                      selectedRecordId === record.id
                        ? 'backgroundNeutralHovered'
                        : ''
                    }
                    padding="containerXLarge containerXxLarge"
                    margin="containerSmall containerLarge"
                    borderRadius="medium"
                    cursor="pointer"
                    vOn:click={() => this.redirectToOverview(record.id)}
                  >
                    <FText
                      color={
                        selectedRecordId === record.id
                          ? 'backgroundPrimaryDefault'
                          : 'textMain'
                      }
                    >
                      {record[this.mainFieldName] || '---'}
                    </FText>
                  </FContainer>
                )
              })
            )}
          </FContainer>

          <FContainer
            borderTop="solid 0.5px"
            borderColor="borderNeutralBaseSubtle"
            padding="containerXLarge"
            width="100%"
          >
            {!isEmpty(recordCount) && (
              <Pagination
                totalCount={recordCount}
                currentPageCount={currentPageCount}
                perPage={perPage}
              />
            )}
          </FContainer>
        </div>
      )
    },
  },
  render() {
    let { selectedListItemsIds } = this || {}
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
      </FContainer>
    )
  },
}
</script>

<style>
.cm-dsm-container {
  height: calc(100vh - 170px);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
</style>
