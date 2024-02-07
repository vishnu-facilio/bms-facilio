<template>
  <CommonListLayout
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
      <template v-if="hasPermission('CREATE')">
        <button class="fc-create-btn " @click="addBreak()">
          {{ $t('custommodules.list.new') }}
          {{ moduleDisplayName ? moduleDisplayName : '' }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :perPage="50"
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
            @click="showColumnSettings = true"
          >
            <img
              src="~assets/column-setting.svg"
              class="text-center position-absolute icon"
            />
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
          </div>
          <CommonList
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            @selection-change="selectItems"
            :slotList="slotList"
            :canShowCustomButton="false"
            :refreshList="onCustomButtonSuccess"
          >
            <template #[slotList[0].criteria]="{ record }">
              <div class="d-flex">
                <div class="self-center mL5">
                  {{ record.name || '---' }}
                </div>
              </div>
            </template>
            <template #[slotList[1].name]="{ record }">
              <div class="d-flex text-center">
                <i
                  v-if="hasPermission('EDIT')"
                  class="
                    el-icon-edit
                    pointer
                    edit-icon-color
                    visibility-hide-actions
                    mL10
                  "
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="editBreak(record)"
                  v-tippy
                ></i>
                <i
                  v-if="hasPermission('DELETE')"
                  class="
                    el-icon-delete
                    pointer
                    edit-icon-color
                    visibility-hide-actions
                    mL10
                  "
                  data-arrow="true"
                  :title="$t('common._common.delete')"
                  @click="deleteRecords([record.id])"
                  v-tippy
                ></i>
              </div>
            </template>
            <template #[slotList[2].name]="{ record }">
              <div class="fc-id">
                {{ '#' + record.id }}
              </div>
            </template>
            <template #[slotList[3].criteria]="{ record }">
              {{ secondsToHoursAndMinutes(record.breakTime) }}
            </template>
          </CommonList>
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="viewname"
        ></column-customization>
      </template>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="`/app/pl/break/viewmanager`"
        class="view-manager-btn"
      >
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
    <CreateEditBreak
      v-if="visibility"
      :visibility.sync="visibility"
      :id="editModuleID"
      :viewName="viewname"
      @saved="loadRecords"
    ></CreateEditBreak>
  </CommonListLayout>
</template>
<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import CreateEditBreak from 'src/pages/peopleV2/break/CreateEditBreak.vue'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
export default {
  name: 'BreakList',
  extends: CommonModuleList,
  mixins: [PeopleMixin],
  components: {
    CreateEditBreak,
  },
  computed: {
    moduleDisplayName() {
      return 'Break'
    },
    parentPath() {
      return `/app/pl/break/`
    },
    slotList() {
      return [
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
        {
          name: 'id',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 90,
            label: 'ID',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'breakTime' }),
        },
      ]
    },
  },
  data() {
    return {
      editModuleID: null,
      visibility: false,
    }
  },
  methods: {
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
          name: 'break-v2-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    getViewManagerRoute() {
      let { moduleName, $route } = this
      let { query } = $route

      let route = findRouterForModuleInApp(moduleName, pageTypes.VIEW_MANAGER)
      if (route)
        this.$router.push({
          ...route,
          params: { moduleName },
          query: { ...query },
        })
    },
    addBreak() {
      this.editModuleID = null
      this.visibility = true
    },
    editBreak(row) {
      let { id } = row
      this.editModuleID = id
      this.visibility = true
    },
  },
}
</script>

<style lang="scss">
.cm-side-bar-container {
  flex: 0 0 320px;
  max-width: 320px;
  background: white;
  position: relative;
  height: 100vh;
  border-right: 1px solid #ececec;
  border-left: 1px solid #ececec;
}
.cm-sidebar-header {
  padding: 20px 15px;
  border-bottom: 1px solid #f2f2f2;
}
.cm-sidebar-list-item {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  padding: 20px;
  font-size: 12px;
  cursor: pointer;
}
.custom-module-list-layout {
  .create-btn {
    margin-top: -10px;
  }
  .cm-view-empty-state-container {
    align-items: center;
    background-color: #fff;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 10px;
    flex-grow: 1;
    overflow: auto;

    .module-view-empty-state svg.icon {
      width: 150px;
      height: 120px;
    }
    .add-view-btn {
      background-color: #39b2c2;
      line-height: normal;
      padding: 11px 17px;
      border: solid 1px rgba(0, 0, 0, 0);
      margin-bottom: 30px;
    }
  }
  .cm-empty-state-container {
    background-color: #fff;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 0px 10px 10px;
  }
  .cm-list-container {
    border-width: 0px !important;
    border-style: solid;
    height: calc(100vh - 155px);
  }
  .img-container {
    width: 37px;
    height: 37px;
    border: 1px solid #f9f9f9;
    border-radius: 50%;
  }
  .column-customization-icon {
    position: absolute;
    right: 11px;
    display: block;
    width: 45px;
    height: 50px;
    cursor: pointer;
    text-align: center;
    background-color: #ffffff;
    border-left: 1px solid #f2f5f6;
    z-index: 20;
    .icon {
      top: 35%;
      right: 29%;
    }
    margin-top: 2px;
  }
}
.cm-list-container {
  .el-table td {
    padding: 10px 20px;
  }
  .el-table th.is-leaf {
    padding: 15px 20px;
  }
  .el-table th > .cell {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    color: #333;
    white-space: nowrap;
    padding-left: 0;
    padding-right: 0;
  }
  .hover-actions {
    visibility: hidden;
  }
  .el-table__body tr.hover-row > td .hover-actions {
    visibility: visible;
  }
}
</style>
