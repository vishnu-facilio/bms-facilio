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
              <div class="text-fc-grey">#{{ record['id'] }}</div>
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
        <button class="fc-create-btn " @click="addShift()">
          {{ $t('custommodules.list.new') }}
          {{ moduleDisplayName ? moduleDisplayName : '' }}
        </button>
      </template>
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="(records || []).length"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="pL15 fc-black-small-txt-12 "
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
            :disabled="!isColumnCustomizable"
            @click="toShowColumnSettings"
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
            <CustomButton
              :key="`${moduleName}_${viewname}_${POSITION.LIST_BAR}`"
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
            <template #[slotList[0].criteria]="{ record }">
              <div class="d-flex" @click="redirectToOverview(record.id)">
                <el-tooltip
                  effect="dark"
                  :content="record.name || '---'"
                  placement="top"
                  :open-delay="600"
                >
                  <div class="self-center mL5 main-field-column">
                    {{ record.name || '---' }}
                  </div>
                </el-tooltip>
              </div>
            </template>
            <template #[slotList[1].name]="{ record }">
              <div class="d-flex text-center">
                <i
                  v-if="hasUpdatePermission"
                  class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                  data-arrow="true"
                  :title="$t('common._common.edit')"
                  @click="editShift(record)"
                  v-tippy
                ></i>
                <i
                  v-if="deleteableShift(record)"
                  class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
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
              {{ getReadableTime(record.startTime) }}
            </template>
            <template #[slotList[4].criteria]="{ record }">
              {{ getReadableTime(record.endTime) }}
            </template>
          </CommonList>
        </div>
        <column-customization
          :visible.sync="showColumnSettings"
          :moduleName="moduleName"
          :viewName="viewname"
          @refreshRelatedList="showColumnSettings = false"
        ></column-customization>
      </template>
    </template>
    <portal to="view-manager-link">
      <router-link
        tag="div"
        :to="`/app/pl/shift/viewmanager`"
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
    <NewShift
      v-if="visibility"
      :visibility.sync="visibility"
      :id="editModuleID"
      :viewName="viewname"
      @saved="loadRecords"
    ></NewShift>
  </CommonListLayout>
</template>
<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import NewShift from 'src/pages/peopleV2/shift/NewShift.vue'
import { getFormatedTime } from '@/mixins/TimeFormatMixin.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { findRouterForModuleInApp } from 'newapp/viewmanager/routeUtil'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
import { mapState, mapGetters } from 'vuex'
export default {
  name: 'ShiftList',
  extends: CommonModuleList,
  mixins: [PeopleMixin],
  components: {
    NewShift,
  },
  created() {
    this.$root.$on('reload-shift-list', () => {
      this.loadRecords()
    })
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasUpdatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    hasDeletePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('DELETE', currentTab)
    },
    parentPath() {
      return `/app/pl/shift/`
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
          criteria: JSON.stringify({ name: 'startTime' }),
        },
        {
          criteria: JSON.stringify({ name: 'endTime' }),
        },
      ]
    },
  },
  data() {
    return {
      perPage: 50,
      showColumnSettings: false,
      editModuleID: null,
      visibility: false,
    }
  },
  methods: {
    deleteableShift(shift) {
      if (shift.defaultShift) {
        return false
      }
      return this.hasDeletePermission
    },
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
          name: 'shift-v2-list',
          params: { viewname },
          query: this.$route.query,
        })
      }
    },
    redirectToOverview(shiftID) {
      let { moduleName, viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname,
              id: shiftID,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'shift-v2-summary',
          params: {
            viewname,
            id: shiftID,
          },
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
    formatTime(t) {
      return t.toString().padStart(2, '0')
    },
    toHumanReadableTime(Seconds) {
      if (!Seconds) {
        Seconds = 0
      }
      const HOUR_IN_SECONDS = 60 * 60
      const MINUTE_IN_SECONDS = 60
      let minutes = (Seconds % HOUR_IN_SECONDS) / MINUTE_IN_SECONDS
      let hours = Math.floor(Seconds / HOUR_IN_SECONDS)

      return this.formatTime(hours) + ':' + this.formatTime(minutes)
    },
    addShift() {
      this.editModuleID = null
      this.visibility = true
    },
    editShift(row) {
      let { id } = row
      this.editModuleID = id
      this.visibility = true
    },
    getReadableTime(value) {
      return getFormatedTime(value)
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
