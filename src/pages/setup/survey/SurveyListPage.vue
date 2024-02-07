<template>
  <div class="height100" v-if="isSummaryOpen">
    <div v-if="showLoading" class="flex-middle fc-empty-white">
      <Spinner :show="showLoading" size="80"></Spinner>
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
    :key="`${moduleName}-list-layout`"
    :hideSubHeader="canHideSubHeader"
    :recordCount="recordCount"
    :recordLoading="showLoading"
    class="custom-module-list-layout survey-list-page"
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
    </template>

    <template #sub-header-actions>
      <template v-if="!isEmpty(records) && showListView">
        <pagination
          :total="recordCount"
          :currentPageCount="currentPageCount"
          :perPage="perPage"
          :skipTotalCount="true"
          class="fc-black-small-txt-12 pagination-fix-content"
        ></pagination>
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
        <InlineSvg
          src="svgs/emptystate/readings-empty"
          class="self-center"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size "
        ></InlineSvg>
        <div class="fc-black-dark f18 bold pL50 mT10 line-height10 self-center">
          {{ $t('survey.empty_state') }}
        </div>
      </div>
      <template v-else>
        <div class="cm-list-container">
          <CommonList
            :key="`viewname-${viewname}`"
            :viewDetail="viewDetail"
            :records="records"
            :moduleName="moduleName"
            :redirectToOverview="redirectToOverview"
            :hideListSelect="true"
            :slotList="slotList"
            :refreshList="onCustomButtonSuccess"
          >
            <template #[slotList[0].name]="{record}">
              <div class="d-flex ellipse-style id-style">
                <div>
                  {{ '#' + record[slotList[0].name] }}
                </div>
              </div>
            </template>
            <template #[slotList[1].criteria]="{record}">
              <el-tooltip
                effect="dark"
                :content="record.name || '---'"
                placement="top-start"
                :open-delay="600"
              >
                <div
                  class="self-center width200px pointer label-txt-black ellipsis main-field-column inline-style"
                  @click="sidePanelForIndividualSurvey(record.id)"
                >
                  <span class="list-main-field">{{
                    record.name || '---'
                  }}</span>
                </div>
              </el-tooltip>
            </template>
            <template #[slotList[3].name]="{record}">
              <div class="d-flex">
                <div>{{ calculateScoreDetails(record) }}</div>
              </div>
            </template>
          </CommonList>
        </div>
      </template>
    </template>
    <portal to="view-manager-link">
      <!-- <div @click="getViewManagerRoute" class="view-manager-btn">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div> -->
    </portal>
    <IndividualSurveyResult
      ref="user-response"
      :showResultDialog.sync="showIndividualResponse"
      :details="details"
      :surveyLoading.sync="surveyLoading"
    />
  </CommonListLayout>
</template>

<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import IndividualSurveyResult from 'src/pages/setup/survey/IndividualSurveyResult.vue'
import { isEmpty } from '@facilio/utils/validation'
import { getSurveyDetails } from './SurveyUtil'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: CommonModuleList,
  data() {
    return {
      showIndividualResponse: false,
      recordName: '',
      details: {},
      surveyLoading: false,
    }
  },
  components: { IndividualSurveyResult },
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
          name: 'score',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 110,
            label: 'SCORE',
            fixed: 'right',
          },
        },
      ]
    },
    filters() {
      let { query } = this.$route || {}
      let { search } = query || {}
      let searchdata = !isEmpty(search) ? JSON.parse(search) : null
      let { viewname } = this
      let moduleFilter = null
      if (viewname === 'workorder') {
        moduleFilter = 'workOrderId'
      } else if (viewname === 'serviceRequest') {
        moduleFilter = 'serviceRequestId'
      }
      let filter = null
      if (!isEmpty(moduleFilter)) {
        filter = {
          [moduleFilter]: {
            operatorId: 2,
            value: null,
          },
        }
      }
      let filters = { ...searchdata, ...filter }
      return filters
    },
  },
  methods: {
    sidePanelForIndividualSurvey(surveyId) {
      this.showIndividualResponse = true
      this.loadSurveyDetail(surveyId)
    },
    async loadSurveyDetail(selectedSurveyId) {
      this.surveyLoading = true
      let selectedSurvey = {}
      selectedSurvey.id = selectedSurveyId
      this.details = await getSurveyDetails(selectedSurvey)
      this.recordName = this.$getProperty(this, 'details.name', '---')
      this.surveyLoading = false
    },
    calculateScoreDetails(record) {
      let fullScore = this.$getProperty(record, 'fullScore', '')
      if (!isEmpty(fullScore)) {
        let totalScore = this.$getProperty(record, 'totalScore', '')
        if (!isEmpty(totalScore)) {
          return `${totalScore}/${fullScore}`
        }
      } else {
        return `N/A`
      }
    },
    openList() {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'survey-list',

          query: this.$route.query,
        })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.survey-list-page {
  .title {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.3px;
    color: #25243e;
    .seperator {
      border: 0.5px solid #d8d8d873;
      margin: 0px 15px 0px 15px;
    }
    .sh-selection-survey-bar {
      width: 32px;
      margin-top: 6px;
      border-bottom: 2px solid #ee518f;
    }
  }
  .pagination-fix-content {
    margin-left: -20px;
  }
  .id-style {
    color: #39b2c2;
  }
  .inline-style {
    display: inline !important;
  }
  .cm-list-container {
    border-width: 0px !important;
    border-style: solid;
    padding: 0px 10px 10px;
    height: calc(100vh - 155px) !important;
  }
  .cm-empty-state-container {
    background-color: #fff;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin: 0px 10px 10px;
    height: calc(100vh - 125px);
  }
}
</style>
