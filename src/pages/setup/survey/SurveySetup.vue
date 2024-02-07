<template>
  <div class="fc-setup-page height100vh">
    <SetupHeader>
      <template #heading>
        {{ $t('survey.surveys') }}
      </template>
      <template #description>
        {{ $t('survey.configure_surveys_to_receive_feedback') }}
      </template>
      <template #actions>
        <div class="flex-middle">
          <!-- will enable this while srvey available for all modules -->
          <!-- <portal-target
            name="automation-modules"
            class="mB30 mL30"
          ></portal-target> -->

          <!-- Just for Initial Cut enabling workorder  -->
          <el-select
            v-model="moduleName"
            filterable
            disabled
            width="250px"
            class="fc-input-full-border-select2"
          >
            <el-option label="Work Orders" value="workorder"></el-option>
          </el-select>

          <div class="action-btn setting-page-btn mL20">
            <el-button
              type="primary"
              class="setup-el-btn"
              @click="handleAddClick"
            >
              <span v-if="currentTabName==='Survey'">{{ $t('survey.configure_survey') }}</span>
              <span v-else>{{ $t('survey.create_survey') }}</span>
            </el-button>
          </div>
        </div>
      </template>
      <template #tabs>
        <el-tabs class="fc-setup-list-tab" v-model="currentTab">
          <el-tab-pane :label="$t('survey.configuration')" name="survey.list">
            <div class="mT10 mB20">
              <SurveyList
                :moduleName="moduleName"
                v-if="currentTabName === 'Survey'"
                :canShowAddSurvey.sync="canShowAddSurvey"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('survey.questionnaire')" name="survey.template">
            <div class="mT10 mB20">
              <SurveyTemplateList
                :moduleName="moduleName"
                v-if="currentTabName === 'Builder'"
                :canShowAddTemplate.sync="canShowAddTemplate"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </template>
    </SetupHeader>
  </div>
</template>

<script>
import SetupHeader from 'pages/setup/components/SetupHeaderTabs'
import SurveyTemplateList from './SurveyTemplateList.vue'
import SurveyList from './SurveyList.vue'
export default {
  name: 'SurveySetup',
  components: { SetupHeader, SurveyTemplateList, SurveyList },
  data() {
    return {
      moduleName: 'workorder',
      canShowAddTemplate: false,
      canShowAddSurvey: false,
    }
  },
  computed: {
    currentTab: {
      get() {
        return this.$route.name
      },
      set(value) {
        this.handleTabChange(value)
      },
    },
    currentTabName() {
      if (this.$route.name === 'survey.list') {
        return 'Survey'
      }
      return 'Builder'
    },
  },
  methods: {
    switchCurrentTab(tab) {
      this.$router.push({ name: tab })
    },
    resetPaginationProps() {
      if (this.currentTab === 'survey.list') {
        this.surveyPage = 1
        this.surveyPerPage = 50
      } else {
        this.templatePage = 1
        this.templatePerPage = 50
      }
    },
    handleTabChange(tab) {
      this.switchCurrentTab(tab)
      this.resetPaginationProps()
      //this.$set(this, 'currentTab', currentTab.name)
    },
    handleAddClick() {
      let { currentTabName } = this
      if (currentTabName === 'Builder') {
        this.canShowAddTemplate = true
      } else {
        this.canShowAddSurvey = true
      }
    },
  },
}
</script>
