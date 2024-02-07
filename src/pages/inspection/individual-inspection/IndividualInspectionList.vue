<template>
  <!-- eslint-disable-next-line vue/no-multiple-template-root-->
  <div class="height100 schedule-list-container" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div v-else class="flex flex-row">
      <div
        class="height100vh full-layout-white fc-border-left fc-border-right approval-sidebar-list"
        style="flex: 0 0 320px;max-width:320px"
      >
        <SummarySidebar
          :list="records"
          :isLoading.sync="isLoading"
          :activeRecordId="selectedRecordId"
          :total="recordCount"
          :currentCount="(records || []).length"
          class="inspection-summary-list"
        >
          <template #title>
            <el-row class="p15 pT20 pB20 fc-border-bottom">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
                    @click="openList()"
                    src="svgs/arrow"
                    class="rotate-90 pointer"
                    iconClass="icon icon-sm"
                  ></inline-svg>
                </span>
              </el-col>

              <el-col :span="22">
                <div class="bold">{{ currentViewDetail }}</div>
              </el-col>
            </el-row>
          </template>
          <template v-slot="{ record }">
            <div
              class="sp-li ellipsis f12 pointer asset-item p20"
              @click="openSummary(record.id)"
            >
              <el-row>
                <el-col :span="24">
                  <div class="d-flex">
                    <div class="fc-id pR10">#{{ record.id }}</div>
                  </div>
                  <div class="flex-middle justify-content-space width100 pT10">
                    <div
                      class="f14 truncate-text"
                      :title="$getProperty(record, 'name', '')"
                      v-tippy="{
                        placement: 'top',
                        animation: 'shift-away',
                        arrow: true,
                      }"
                    >
                      {{ $getProperty(record, 'name', '') }}
                    </div>
                    <div class="fc-grey2-text12">
                      <i class="el-icon-time pR5"></i>
                      {{ record.sysCreatedTime | fromNow }}
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </SummarySidebar>
      </div>

      <div style="flex: 1;">
        <router-view
          :viewname="viewname"
          :moduleName="moduleName"
        ></router-view>
      </div>
    </div>
  </div>
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <CommonListLayout
    v-else
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => `All ${moduleDisplayName}s`"
    :pathPrefix="pathPrefix"
    :key="moduleName + '-list-layout'"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout"
  >
    <template #header>
      <AdvancedSearchWrapper
        v-if="!canHideFilter"
        :key="`ftags-list-${moduleName}`"
        :filters="filters"
        :moduleName="moduleName"
        :moduleDisplayName="moduleDisplayName"
      ></AdvancedSearchWrapper>
      <visual-type
        v-if="canShowVisualSwitch"
        @onSwitchVisualize="val => (canShowListView = val)"
      ></visual-type>
      <CustomButton
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        class="custom-button"
        @onSuccess="onCustomButtonSuccess"
        @onError="() => {}"
      ></CustomButton>
    </template>

    <template v-if="canShowCalendarHeader" #sub-header>
      <CalendarDateWrapper v-if="!showListView" />
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12"
        ></pagination>
        <span class="separator" v-if="recordCount && recordCount > 0">|</span>
        <Sort
          :key="`${moduleName}-sort`"
          :moduleName="moduleName"
          @onSortChange="updateSort"
        ></Sort>
      </template>
    </template>
    <template #content>
      <div class="height100 pB10">
        <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
        <div v-else-if="showListView" class="fc-card-popup-list-view">
          <div
            class="fc-list-view fc-list-table-container p10 pT0 height100vh fc-table-td-height fc-table-viewchooser pB100"
          >
            <div
              v-if="$validation.isEmpty(records) && !showLoading"
              class="cm-empty-state-container white-bg flex-middle justify-content-center m0"
            >
              <inline-svg
                src="svgs/emptystate/purchaseOrder"
                iconClass="icon text-center icon-xxxxlg"
              ></inline-svg>
              <div class="nowo-label">No {{ moduleDisplayName }} Available</div>
            </div>
            <div v-if="!showLoading && !$validation.isEmpty(records)">
              <div
                class="column-customization-icon"
                :disabled="!isColumnCustomizable"
                @click="toShowColumnSettings"
              >
                <el-tooltip
                  :disabled="isColumnCustomizable"
                  placement="top"
                  :content="$t('common._common.you_dont_have_permission')"
                >
                  <inline-svg
                    src="column-setting"
                    class="text-center position-absolute icon"
                  />
                </el-tooltip>
              </div>
              <div
                v-if="selectedListItemsIds.length > 0"
                class="pull-left table-header-actions"
              >
                <div class="action-btn-slide btn-block">
                  <button
                    v-if="$hasPermission(`${moduleName}:DELETE`)"
                    class="btn btn--tertiary"
                    @click="deleteRecords(selectedListItemsIds)"
                    :class="{ disabled: isLoading }"
                  >
                    <i class="fa fa-trash-o b-icon" v-if="!isLoading"></i>
                    <i
                      class="fa fa-circle-o-notch b-icon fa-spin"
                      aria-hidden="true"
                      v-if="isLoading"
                    ></i>
                    {{ $t('common._common.delete') }}
                  </button>
                </div>
                <CustomButton
                  :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
                  :modelDataClass="modelDataClass"
                  :moduleName="moduleName"
                  :position="POSITION.LIST_BAR"
                  class="custom-button"
                  @onSuccess="onCustomButtonSuccess"
                  @onError="() => {}"
                  :selectedRecords="selectedListItemsObj"
                >
                </CustomButton>
              </div>

              <CommonList
                :viewDetail="viewDetail"
                :records="records"
                :moduleName="moduleName"
                :redirectToOverview="openSummary"
                :slotList="slotList"
                @selection-change="selectItems"
                canShowCustomButton="true"
                :refreshList="onCustomButtonSuccess"
              >
                <template #[slotList[0].name]="{record}">
                  <div class="fc-id">{{ '#' + record.id }}</div>
                </template>
                <template #[slotList[1].criteria]="{record}">
                  <div
                    @click="openSummary(record.id)"
                    v-tippy
                    small
                    :title="$getProperty(record, 'name', '')"
                    class="flex-middle main-field-column"
                  >
                    <div class="fw5 ellipsis width200px">
                      {{ $getProperty(record, 'name', '') }}
                    </div>
                  </div>
                </template>
                <template #[slotList[2].name]="{record}">
                  <div class="text-center">
                    <i
                      v-if="$hasPermission(`${moduleName}:DELETE`)"
                      class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                      data-arrow="true"
                      :title="$t('common._common.delete')"
                      @click="deleteRecords([record.id])"
                      v-tippy
                    ></i>
                  </div>
                </template>
              </CommonList>
            </div>
          </div>
        </div>
        <CalendarView
          v-else-if="!showListView"
          ref="calendar"
          :moduleName="moduleName"
          :record="records"
          :viewDetail="viewDetail"
          :viewname="viewname"
          :filters="filters"
        ></CalendarView>
      </div>
    </template>
    <portal to="view-manager-link">
      <router-link tag="div" :to="viewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </router-link>
    </portal>
    <column-customization
      :visible.sync="showColumnSettings"
      :moduleName="moduleName"
      :viewName="viewname"
    ></column-customization>
  </CommonListLayout>
</template>

<script>
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'

export default {
  extends: CommonModuleList,
  name: 'InspectionList',
  computed: {
    viewManagerRoute() {
      let { moduleName } = this

      return {
        path: `/app/inspection/${moduleName}/viewmanager`,
      }
    },
    slotList() {
      return [
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'name' }),
        },
        {
          name: 'editDelete',
          isActionColumn: true,
          columnAttrs: {
            width: 130,
            class: 'visibility-visible-actions',
            fixed: 'right',
          },
        },
      ]
    },
    moduleDisplayName() {
      return 'Inspection Response'
    },
    pathPrefix() {
      return '/app/inspection/individual'
    },
  },
  methods: {
    getAssignedToValue(record) {
      let value = record.assignmentGroup ? record.assignmentGroup.name : '---'
      value += ' / ' + (record.assignedTo ? record.assignedTo.name : '---')
      return value
    },
    openSummary(id) {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: this.viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'individualInspectionSummary',

          params: { id, viewname: this.viewname },
          query: this.$route.query,
        })
      }
    },
    openList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: this.viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'individualInspectionList',

          params: { viewname: this.viewname },
          query: this.$route.query,
        })
      }
    },
    async deleteRecords(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t(`qanda.response.delete_inspection`),
        message: this.$t(`qanda.response.delete_inspection_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { error } = await API.deleteRecord(this.moduleName, idList)
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          await this.refreshRecordDetails(true)
          this.$message.success(
            this.$t(`qanda.response.inspection_multiple_success_delete`)
          )
          this.selectedListItemsObj = []
          this.selectedListItemsIds = []
        }
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.create-btn {
  margin-top: -10px;
}

.inspection-summary-list {
  .list-item.active {
    background-color: #f2fafb;
    border: solid 1px #d5ebed;
  }
}
</style>
