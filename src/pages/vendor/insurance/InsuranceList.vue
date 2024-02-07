<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <spinner :show="showLoading" size="80"></spinner>
    </div>
    <div v-else class="flex flex-row">
      <div class="cm-side-bar-container">
        <SummarySidebar
          :list="records"
          :isLoading.sync="isLoading"
          :activeRecordId="selectedRecordId"
          :total="recordCount"
          :currentCount="(records || []).length"
        >
          <template #title>
            <el-row class="cm-sidebar-header">
              <el-col :span="2">
                <span @click="openList()">
                  <inline-svg
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
              class="cm-sidebar-list-item"
              @click="redirectToOverview(record.id)"
            >
              <div
                class="f14 truncate-text bold mT5"
                :title="record[mainFieldName] || '---'"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ record[mainFieldName] || '---' }}
              </div>
            </div>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view></router-view>
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
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
    :key="`${moduleName}-list-layout`"
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
      <template v-if="canShowVisualSwitch">
        <visual-type
          @onSwitchVisualize="val => (canShowListView = val)"
        ></visual-type>
      </template>
      <CustomButton
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        class="custom-button"
        @onSuccess="onCustomButtonSuccess"
        @onError="() => {}"
      ></CustomButton>
      <template v-if="hasPermission('CREATE')">
        <button class="fc-create-btn " @click="redirectToFormCreation()">
          {{ $t('custommodules.list.new') }}
          {{ moduleDisplayName ? moduleDisplayName : '' }}
        </button>
      </template>
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
        <span class="separator" v-if="recordCount > 0">|</span>

        <el-tooltip
          effect="dark"
          :content="$t('common._common.sort')"
          placement="right"
        >
          <Sort
            :key="`${moduleName}-sort`"
            :moduleName="moduleName"
            @onSortChange="updateSort"
          ></Sort>
        </el-tooltip>
        <span v-if="hasPermission('EXPORT')" class="separator">|</span>

        <el-tooltip
          v-if="hasPermission('EXPORT')"
          effect="dark"
          :content="$t('common._common.export')"
          placement="left"
        >
          <f-export-settings
            :module="moduleName"
            :viewDetail="viewDetail"
            :showViewScheduler="false"
            :showMail="false"
            :filters="filters"
          ></f-export-settings>
        </el-tooltip>
      </template>
    </template>
    <template #content>
      <Spinner v-if="showLoading" class="mT40" :show="showLoading"></Spinner>
      <div v-else-if="$validation.isEmpty(records)" class="cm-list-container">
        <div class="fc-list-empty-state-container">
          <img
            class="mT20 self-center"
            src="~statics/noData-light.png"
            width="100"
            height="100"
          />
          <div class="mT10 label-txt-black f14 self-center">
            {{ emptyStateText }}
          </div>
        </div>
      </div>
      <template v-else>
        <div class="cm-list-container">
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
            class="pull-left table-header-actions"
            v-if="!$validation.isEmpty(selectedListItemsIds)"
          >
            <div class="action-btn-slide btn-block">
              <button
                class="btn btn--tertiary pointer"
                @click="deleteRecords(selectedListItemsIds)"
              >
                {{ $t('custommodules.list.delete') }}
              </button>
            </div>
            <CustomButton
              :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
              :modelDataClass="modelDataClass"
              :moduleName="moduleName"
              :position="POSITION.LIST_BAR"
              class="custom-button margin-left-80"
              @onSuccess="onCustomButtonSuccess"
              @onError="() => {}"
              :selectedRecords="selectedListItemsObj"
            ></CustomButton>
          </div>
          <CommonList
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            :redirectToOverview="redirectToOverview"
            @selection-change="selectItems"
            :slotList="slotList"
            canShowCustomButton="true"
            :refreshList="onCustomButtonSuccess"
          >
            <template #[slotList[1].criteria]="{record}">
              <div class="d-flex" @click="redirectToOverview(record.id)">
                <el-tooltip
                  effect="dark"
                  :content="record.companyName || '---'"
                  placement="top"
                  :open-delay="600"
                >
                  <div class="self-center mL5  main-field-column">
                    {{ record.companyName || '---' }}
                  </div>
                </el-tooltip>
              </div>
            </template>
            <template #[slotList[2].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('UPDATE')"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="editModule(record)"
                  v-tippy
                ></i>
                <i
                  v-if="hasPermission('DELETE')"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  @click="deleteRecords([record.id])"
                  v-tippy
                ></i>
              </div>
            </template>
            <template #[slotList[0].name]="{record}">
              <div class="fc-id">
                {{ '#' + record.id }}
              </div>
            </template>
          </CommonList>
        </div>
      </template>
      <column-customization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></column-customization>
    </template>
    <portal to="view-manager-link">
      <router-link tag="div" :to="`viewmanager`" class="view-manager-btn">
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
  </CommonListLayout>
</template>

<script>
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'

export default {
  name: 'insurance',
  extends: CommonModuleList,
  computed: {
    parentPath() {
      return `/app/vendor/insurance/`
    },
    mainFieldName() {
      return 'companyName'
    },
  },
  methods: {
    // route redirection handling
    openList() {
      let { viewname } = this

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'insurancesList',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    redirectToOverview(id) {
      let { moduleName, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'insurancesSummary',
          params: {
            viewname,
            id,
          },
          query: this.$route.query,
        })
      }
    },
    editModule(row) {
      let { moduleName, viewname } = this
      let { id } = row
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        name &&
          this.$router.push({
            name,
            params: {
              id,
            },
          })
      } else {
        this.$router.push({
          name: 'insuranceEdit',
          params: {
            viewname,
            id,
          },
        })
      }
    },
    redirectToFormCreation() {
      let { moduleName } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.CREATE) || {}

        name &&
          this.$router.push({
            name,
          })
      } else {
        this.$router.push({
          name: 'insuranceCreate',
        })
      }
    },
  },
}
</script>
