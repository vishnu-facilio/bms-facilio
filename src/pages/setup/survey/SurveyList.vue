<template>
  <div>
    <div class="fc-setup-actions-con" style="height: 60px;">
      <div class="fc-black-15 fwBold">
        {{ $t('survey.all_configuration') }}
      </div>
      <div class="flex-middle">
        <pagination
          :total="surveys.length || 0"
          :perPage="surveyPerPage"
          class="nowrap"
          ref="f-page"
          :pageNo="surveyPage"
          @onPageChanged="setPage"
        >
        </pagination>
      </div>
    </div>

    <setup-loader v-if="isLoading">
      <template #setupLoading>
        <spinner :show="isLoading" size="80"></spinner>
      </template>
    </setup-loader>

    <setup-empty v-else-if="$validation.isEmpty(surveys) && !isLoading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('survey.no_surveys_available') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>

    <el-table
      v-else
      ref="surveyTable"
      :data="surveys"
      :cell-style="{ padding: '12px 20px' }"
      class="width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
      height="calc(100vh - 280px)"
    >
      <el-table-column :label="$t('survey.survey_name')" prop="name">
        <template v-slot="survey">
          <el-tooltip
            effect="dark"
            :content="survey.row.name || '---'"
            placement="top-start"
            :open-delay="600"
          >
            <div
              class="truncate-text main-field-column"
              @click="editSurvey(survey.row)"
            >
              {{ survey.row.name || '---' }}
            </div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="Description" prop="description">
        <template v-slot="survey">
          {{ survey.row.description || '---' }}
        </template>
      </el-table-column>
      <el-table-column label="Created Time" prop="sysCreatedTime" width="220">
        <template v-slot="survey">
          {{ getCreatedTime(survey.row) }}
        </template>
      </el-table-column>
      <el-table-column label="Status" prop="status">
        <template v-slot="survey">
          <el-switch
            v-model="survey.row.status"
            @change="handleStatusChange(survey.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        prop
        label
        width="180"
        class="visibility-visible-actions"
        fixed="right"
      >
        <template v-slot="survey">
          <div class="text-center template-actions">
            <i
              class="el-icon-delete fc-delete-icon visibility-hide-actions"
              data-arrow="true"
              :title="$t('common._common.delete')"
              v-tippy
              @click="deleteSurvey([survey.row.id])"
            ></i>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import Pagination from 'src/components/list/FPagination'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'

export default {
  data() {
    return {
      surveyPage: 1,
      surveyPerPage: 50,
      isLoading: false,
      surveys: [],
    }
  },
  components: {
    Pagination,
    SetupLoader,
    SetupEmpty,
  },
  props: ['canShowAddSurvey', 'moduleName'],
  computed: {
    ...mapGetters(['getUser']),
  },
  created() {
    this.loadSurveys()
  },
  watch: {
    canShowAddSurvey(newVal) {
      if (newVal) {
        this.redirectToSurveyCreation()
      }
    },
  },
  methods: {
    async loadSurveys(force = false) {
      this.isLoading = true
      let { moduleName } = this
      let params = {
        ruleType: 46,
        moduleName: moduleName,
        page: 1,
        perPage: 20,
      }
      let { error, data } = await API.get('v2/modules/rules/list', params, {
        force,
      })

      if (error) {
        this.$message.error('Error Occured')
      } else {
        if (!isEmpty(data)) {
          let { workflowRuleList = [] } = data || {}
          this.surveys = workflowRuleList
        }
      }
      this.isLoading = false
    },
    getCreatedTime(survey) {
      let { createdTime } = survey || {}

      if (!isEmpty(createdTime)) {
        return this.$options.filters.formatDate(createdTime) || '----'
      }
    },
    editSurvey(survey) {
      let { id } = survey || {}

      this.$router.push({
        name: 'editSurvey',
        params: { id },
        query: this.$route.query,
      })
    },
    async deleteSurvey(idList) {
      let value = await this.$dialog.confirm({
        title: this.$t('survey.delete_survey'),
        message: this.$t('survey.are_you_sure_delete_survey'),
        rbDanger: true,
        rbLabel: 'Delete',
      })
      if (value) {
        let { error } = await API.post('v2/setup/survey/delete', {
          ruleIds: idList,
        })
        if (error) {
          this.$message.error(this.$t('common._common.error_occured'))
        } else {
          this.loadSurveys(true)
          this.$message.success(this.$t('survey.survey_deleted_successfully'))
        }
      }
    },
    setPage(page) {
      this.surveyPage = page
    },
    async handleStatusChange(survey) {
      let { status, id } = survey || {}
      let url = status ? 'setup/turnonrule' : 'setup/turnoffrule'
      let params = { workflowId: id }
      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(this.$t('common._common.error_occured'))
      } else {
        this.$message.success(
          this.$t('survey.survey_status_changed_successfully')
        )
        this.loadSurveys()
      }
    },
    redirectToSurveyCreation() {
      this.$router.push({
        name: 'newSurvey',
        params: { moduleName: this.moduleName },
      })
    },
  },
}
</script>
