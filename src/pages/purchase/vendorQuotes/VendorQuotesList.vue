<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <Spinner :show="showLoading" size="80"></Spinner>
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
            <router-link
              tag="div"
              class="cm-sidebar-list-item label-txt-black main-field-column"
              :to="redirectToOverview(record.id)"
            >
              <el-row>
                <el-col :span="24">
                  <div
                    class="f14 truncate-text"
                    :title="
                      $getProperty(record, 'requestForQuotation.name', '---')
                    "
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{
                      $getProperty(record, 'requestForQuotation.name', '---')
                    }}
                  </div>
                  <div class="flex-middle justify-content-space width100 pT10">
                    <div class="fc-id pR10">#{{ record.id }}</div>
                    <div class="fc-grey2-text12">
                      <i class="el-icon-time pR5"></i>
                      {{ record.sysCreatedTime | fromNow }}
                    </div>
                  </div>
                </el-col>
              </el-row>
            </router-link>
          </template>
        </SummarySidebar>
      </div>
      <div class="flex-1">
        <router-view :viewname="viewname"></router-view>
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
      <template v-if="!isEmpty(viewname)">
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
      </template>
      <CustomButton
        :key="`${moduleName}_${viewname}_${POSITION.LIST_TOP}`"
        :moduleName="moduleName"
        :position="POSITION.LIST_TOP"
        :modelDataClass="modelDataClass"
        class="custom-button"
        @onSuccess="onCustomButtonSuccess"
        @onError="() => {}"
      ></CustomButton>
      <!-- <template v-if="hasPermission('CREATE')">
        <div class="pL10">
          <button
            class="fc-create-btn create-btn"
            @click="redirectToFormCreation()"
          >
            {{ createBtnText }}
          </button>
        </div>
      </template> -->
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
          :open-delay="500"
          placement="top"
          :tabindex="-1"
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
          :open-delay="500"
          placement="top"
          :tabindex="-1"
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
      <div v-else-if="isEmpty(viewname)" class="cm-view-empty-state-container">
        <inline-svg
          src="svgs/no-configuration"
          class="d-flex module-view-empty-state"
          iconClass="icon"
        ></inline-svg>
        <div class="mB20 label-txt-black f14 self-center">
          {{ $t('viewsmanager.list.no_view_config') }}
        </div>
        <el-button
          type="primary"
          class="add-view-btn"
          @click="openViewCreation"
        >
          <span class="btn-label">{{ $t('viewsmanager.list.add_view') }}</span>
        </el-button>
      </div>
      <div v-else-if="isEmpty(records)" class="cm-empty-state-container">
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
            v-if="!isEmpty(selectedListItemsIds)"
          >
            <CustomButton
              :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
              :moduleName="moduleName"
              :position="POSITION.LIST_BAR"
              :modelDataClass="modelDataClass"
              class="custom-button"
              @onSuccess="onCustomButtonSuccess"
              @onError="() => {}"
              :selectedRecords="selectedListItemsObj"
            ></CustomButton>
          </div>
          <CommonList
            :key="`viewname-${viewname}`"
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            :redirectToOverview="redirectToOverview"
            @selection-change="selectItems"
            :slotList="slotList"
            canShowCustomButton="true"
            :refreshList="onCustomButtonSuccess"
            :modelDataClass="modelDataClass"
          >
            <template #[slotList[0].criteria]="{record}">
              <router-link
                class="d-flex fw5 label-txt-black ellipsis main-field-column"
                :to="redirectToOverview(record.id)"
              >
                <el-tooltip
                  effect="dark"
                  :content="
                    $getProperty(record, 'requestForQuotation.name', '---')
                  "
                  placement="top"
                  :open-delay="600"
                >
                  <div class="self-center mL5 truncate-text">
                    {{
                      $getProperty(record, 'requestForQuotation.name', '---')
                    }}
                  </div>
                </el-tooltip>
              </router-link>
            </template>
            <template #[slotList[1].name]="{record}">
              <div class="fc-id">
                {{ '#' + record.id }}
              </div>
            </template>
          </CommonList>
        </div>
      </template>
      <ColumnCustomization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></ColumnCustomization>
    </template>
    <portal to="view-manager-link">
      <div @click="getViewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div>
    </portal>
  </CommonListLayout>
</template>

<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CommonModuleList,
  computed: {
    parentPath() {
      return `/app/purchase/vendorQuotes`
    },
    slotList() {
      return [
        {
          criteria: JSON.stringify({ name: 'requestForQuotation' }),
        },
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 90,
            label: 'ID',
            fixed: 'left',
          },
        },
      ]
    },
  },
  methods: {
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this
      let { query } = $route
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = {
          name,
          params: {
            viewname,
            id,
          },
          query,
        }
        return name && route
      } else {
        return {
          name: 'vendorQuotesSummary',
          params: { moduleName, viewname, id },
          query: this.$route.query,
        }
      }
    },
    openList() {
      let { viewname, moduleName, $route } = this
      let { query } = $route
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        let route = {
          name,
          params: {
            viewname,
          },
          query,
        }
        name && this.$router.push(route)
      } else {
        this.$router.push({
          name: 'vendorQuotes',
          params: { viewname },
          query,
        })
      }
    },
  },
}
</script>
