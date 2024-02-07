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
              <div
                class="f14 truncate-text"
                :title="$getProperty(record, 'itemType.name', '---')"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ $getProperty(record, 'itemType.name', '---') }}
              </div>
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
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <template v-if="!isEmpty(viewname)">
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
            <template #[slotList[0].name]="{record}">
              <div class="d-flex">
                <div class="fc-id">{{ '#' + record[slotList[0].name] }}</div>
              </div>
            </template>
            <template #[slotList[1].criteria]="{record}">
              <router-link
                class="d-flex fw5 label-txt-black ellipsis main-field-column"
                :to="redirectToOverview(record.id)"
              >
                <div v-if="record[photoFieldName] > 0">
                  <img
                    :src="record.getImage(photoFieldName)"
                    class="img-container"
                  />
                </div>
                <div v-else>
                  <item-avatar
                    :name="false"
                    size="lg"
                    module="item"
                    :recordData="record.itemType"
                  ></item-avatar>
                </div>
                <el-tooltip
                  effect="dark"
                  :content="$getProperty(record, 'itemType.name', '---')"
                  placement="top"
                  :open-delay="600"
                >
                  <div class="self-center mL5 truncate-text">
                    <span class="list-main-field">{{
                      $getProperty(record, 'itemType.name', '---')
                    }}</span>
                  </div>
                </el-tooltip>
              </router-link>
            </template>
            <template #[slotList[2].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('UPDATE,UPDATE_OWN') && record.canEdit()"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="editModule(record)"
                  v-tippy
                ></i>
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
    <update-item-form
      :visibility.sync="newFormVisibility"
      :itemData="itemEditObj"
      :editId="editId"
      v-if="newFormVisibility"
      @saved="loadRecords(true)"
    ></update-item-form>
  </CommonListLayout>
</template>
<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import ItemAvatar from '@/avatar/ItemTool'
import UpdateItemForm from './UpdateItemForm'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  name: 'ItemList',
  extends: CommonModuleList,
  components: {
    ItemAvatar,
    UpdateItemForm,
  },
  data() {
    return {
      newFormVisibility: false,
      itemEditObj: false,
      editId: null,
    }
  },
  computed: {
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
          criteria: JSON.stringify({ name: 'itemType' }),
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
    parentPath() {
      return `/app/inventory/item`
    },
  },
  methods: {
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = { name, params: { viewname, id }, query }

        return name && route
      } else {
        return {
          name: 'itemSummary',
          params: { moduleName, viewname, id },
          query,
        }
      }
    },
    hasPermission(action) {
      return this.$hasPermission(`inventory:${action}`)
    },
    openList() {
      let { viewname, $route } = this
      let { query } = $route

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name, params: { viewname }, query })
      } else {
        this.$router.push({
          name: 'item',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    editModule(data) {
      this.newFormVisibility = true
      this.itemEditObj = data
      this.editId = data.id
    },
  },
}
</script>
