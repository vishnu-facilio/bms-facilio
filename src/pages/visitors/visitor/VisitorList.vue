<template>
  <CommonListLayout
    :moduleName="moduleName"
    :showViewRearrange="true"
    :showViewEdit="true"
    :visibleViewCount="3"
    :getPageTitle="() => moduleDisplayName"
    :pathPrefix="parentPath"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    :key="`${moduleName}-list-layout`"
    class="custom-module-list-layout"
  >
    <template #header>
      <template v-if="!isEmpty(viewname)">
        <AdvancedSearchWrapper
          v-if="!canHideFilter"
          :filters="filters"
          :moduleName="moduleName"
          :moduleDisplayName="moduleDisplayName"
        ></AdvancedSearchWrapper>
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
        <button class="fc-create-btn" @click="openNewForm()">
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
          class=" fc-black-small-txt-12 "
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
        <el-button type="primary" class="add-view-btn" @click="openNewForm">
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
            <div
              class="action-btn-slide btn-block"
              v-if="hasPermission('DELETE')"
            >
              <button
                class="btn btn--tertiary pointer"
                @click="deleteRecords(selectedListItemsIds)"
              >
                {{ $t('custommodules.list.delete') }}
              </button>
            </div>
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
                <div class="mR10">
                  <VisitorAvatar
                    module="visitor"
                    :name="false"
                    size="lg"
                    :recordData="record"
                  ></VisitorAvatar>
                </div>
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
            <template #[slotList[2].name]="{record}">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('EDIT,UPDATE') && record.canEdit()"
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
          </CommonList>
        </div>
      </template>
      <ColumnCustomization
        :visible.sync="showColumnSettings"
        :moduleName="moduleName"
        :viewName="viewname"
      ></ColumnCustomization>
      <VisitorForm
        v-if="formVisibility"
        :visibility.sync="formVisibility"
        :editId="editId"
        :moduleName="moduleName"
        @saved="refreshList()"
      ></VisitorForm>
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
import VisitorForm from './VisitorForm'
import VisitorAvatar from '@/avatar/VisitorAvatar'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['viewname'],
  extends: CommonModuleList,
  components: { VisitorForm, VisitorAvatar },
  computed: {
    parentPath() {
      return '/app/vi/visitor'
    },
  },
  data() {
    return {
      formVisibility: false,
      editId: null,
    }
  },
  methods: {
    refreshList() {
      this.refreshRecordDetails(true)
    },
    openNewForm() {
      this.editId = null
      this.formVisibility = true
    },
    editModule(data) {
      this.editId = data.id
      this.formVisibility = true
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
          name: 'visitorsummary',
          params: { moduleName, viewname, id },
          query,
        }
      }
    },
  },
}
</script>
