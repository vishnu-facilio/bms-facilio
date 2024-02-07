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
                    :title="record[mainFieldName] || '---'"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                  >
                    {{ record[mainFieldName] || '---' }}
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
      <template v-if="hasPermission('CREATE')">
        <button class="fc-create-btn " @click="redirectToFormCreation()">
          {{ createBtnText }}
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
            <template #[slotList[0].name]="{record}">
              <div class="d-flex">
                <div class="fc-id">{{ '#' + record.id }}</div>
              </div>
            </template>
            <template #[slotList[1].criteria]="{record}">
              <router-link
                class="d-flex fw5 label-txt-black ellipsis main-field-column"
                :to="redirectToOverview(record.id)"
              >
                <el-tooltip
                  effect="dark"
                  :content="record.name || '---'"
                  placement="top-end"
                  :open-delay="600"
                >
                  <div class="self-center width200px">
                    <span class="list-main-field">{{
                      record.name || '---'
                    }}</span>
                  </div>
                </el-tooltip>
              </router-link>
            </template>
            <template #[slotList[2].criteria]="{record}">
              <div class="table-subheading">
                <i
                  class="el-icon-s-order pointer edit-icon-color"
                  data-arrow="true"
                  :title="$t('common.products.long_description')"
                  v-tippy
                  @click="openLongDesc(record)"
                >
                </i>
              </div>
            </template>
            <template #[slotList[3].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="checkPermission(record, 'UPDATE') && record.canEdit()"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common.header.edit_terms_and_conditons')"
                  @click="editModule(record)"
                  v-tippy
                ></i>
                <i
                  v-if="checkPermission(record, 'DELETE')"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common.header.delete_terms_and_condition')"
                  @click="deleteRecords([record.id])"
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
    <LongDescriptionEditor
      v-if="longDescVisibility"
      :content="selectedTandC.longDesc"
      :disabled="!hasPermission('UPDATE')"
      @onSave="data => updateLongDescriptionData(data)"
      @onClose="longDescVisibility = false"
    >
    </LongDescriptionEditor>
    <TermsAndConditionForm
      :visibility.sync="termsFormVisibility"
      :tAndCData="termsEditObj"
      :editId="editId"
      v-if="termsFormVisibility"
      @saved="refreshRecordDetails"
    ></TermsAndConditionForm>
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
import { API } from '@facilio/api'
import TermsAndConditionForm from './NewTermsAndCondition'
import LongDescriptionEditor from './RichTextAreaEditor'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  extends: CommonModuleList,
  components: {
    TermsAndConditionForm,
    LongDescriptionEditor,
  },
  data() {
    return {
      longDescVisibility: false,
      termsFormVisibility: false,
      editId: null,
      termsEditObj: null,
    }
  },
  computed: {
    parentPath() {
      return `/app/purchase/tandc`
    },
    slotList() {
      return [
        {
          name: 'localId',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 120,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'name' }),
        },
        {
          criteria: JSON.stringify({ name: 'longDesc' }),
          columnAttrs: {
            width: 200,
          },
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
  },
  methods: {
    checkPermission(record, action) {
      let { isPublished } = record
      return this.hasPermission(action) && !isPublished
    },
    editModule(data) {
      this.termsFormVisibility = true
      this.termsEditObj = data
      this.editId = data.id
    },
    redirectToOverview(id) {
      let { moduleName, viewname, $route } = this
      let { query } = $route
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        let route = { name, params: { viewname, id }, query }
        return name && route
      } else {
        return {
          name: 'tandcsummary',
          params: { id, viewname },
          query: query,
        }
      }
    },
    redirectToFormCreation() {
      this.editId = null
      this.termsFormVisibility = true
    },
    openList() {
      let { moduleName, viewname, $route } = this
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
          name: 'tandclist',
          params: { viewname },
          query,
        })
      }
    },
    openLongDesc(val) {
      this.longDescVisibility = true
      this.selectedTandC = val
    },
    async updateLongDescriptionData(data) {
      let { moduleName } = this
      let params = {
        id: this.selectedTandC.id,
        data: { longDesc: data },
      }
      let { error } = await API.updateRecord(moduleName, params)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$message.success(
          this.$t('common.products.long_description_edited_successfully')
        )
        this.longDescVisibility = false
        this.loadRecords()
      }
    },
  },
}
</script>
