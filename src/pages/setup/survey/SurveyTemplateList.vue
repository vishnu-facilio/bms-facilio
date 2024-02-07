<template>
  <div>
    <div class="fc-setup-actions-con" style="height: 60px;">
      <div class="fc-black-15 fwBold">
        {{ $t('survey.all_questionnaire') }}
      </div>
      <div class="flex-middle">
        <pagination
          :total="templates.length"
          :perPage="templatePerPage"
          class="nowrap"
          ref="f-page"
          :pageNo="templatePage"
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

    <setup-empty v-else-if="$validation.isEmpty(templates) && !isLoading">
      <template #emptyImage>
        <inline-svg src="svgs/copy2" iconClass="icon icon-sm-md"></inline-svg>
      </template>
      <template #emptyHeading>
        {{ $t('survey.no_templates_available') }}
      </template>
      <template #emptyDescription> </template>
    </setup-empty>

    <el-table
      v-else
      ref="surveyTemplatesTable"
      :data="templates"
      class="width100 fc-setup-table fc-setup-table-p0 fc-setup-table-th-borderTop"
      height="calc(100vh - 280px)"
    >
      <el-table-column label="Name" prop="name">
        <template v-slot="template">
          <el-tooltip
            effect="dark"
            :content="template.row.name || '---'"
            placement="top-start"
            :open-delay="600"
          >
            <div
              class="truncate-text main-field-column"
              @click="redirectToBuilder(template.row.id)"
            >
              {{ template.row.name || '---' }}
            </div>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="Description" prop="description">
        <template v-slot="template">
          {{ template.row.description || '---' }}
        </template>
      </el-table-column>
      <el-table-column label="Created By" prop="sysCreatedBy" width="220">
        <template v-slot="template">
          <user-avatar
            size="md"
            :user="template.row.sysCreatedBy"
            :id="template.row.sysCreatedBy.id"
          ></user-avatar>
        </template>
      </el-table-column>
      <el-table-column label="Created Time" prop="sysCreatedTime" width="220">
        <template v-slot="template">
          {{ getCreatedTime(template.row) }}
        </template>
      </el-table-column>
      <el-table-column
        prop
        label
        width="130"
        class="visibility-visible-actions"
        fixed="right"
      >
        <template v-slot="template">
          <div class="text-center template-actions">
            <i
              class="el-icon-edit edit-icon visibility-hide-actions pR15"
              data-arrow="true"
              :title="$t('common._common.edit')"
              v-tippy
              @click="editTemplate(template.row)"
            ></i>
            <!-- will be enabled later  -->
            <!-- <i
              class="el-icon-delete fc-delete-icon visibility-hide-actions"
              data-arrow="true"
              :title="$t('common._common.delete')"
              v-tippy
              @click="deleteTemplate([template.row.id])"
            ></i> -->
          </div>
        </template>
      </el-table-column>
    </el-table>

    <NewSurveyTemplate
      :isNew="isNew"
      :showCreateDialog.sync="showCreateDialog"
      :selectedTemplate.sync="selectedTemplate"
      @onClose="handleCloseDialog"
      @onSave="saveRecord"
    />
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import NewSurveyTemplate from './NewSurveyTemplate.vue'
import Pagination from 'src/components/list/FPagination'
import SetupLoader from 'pages/setup/components/SetupLoader'
import SetupEmpty from 'pages/setup/components/SetupEmptyState'
import UserAvatar from '@/avatar/User'

export default {
  data() {
    return {
      currentTab: 'survey.template',
      templates: [],
      isLoading: false,
      showCreateDialog: false,
      selectedTemplate: null,
      isNew: true,
      templatePage: 1,
      templatePerPage: 50,
    }
  },
  props: ['canShowAddTemplate', 'moduleName'],
  components: {
    NewSurveyTemplate,
    Pagination,
    SetupLoader,
    SetupEmpty,
    UserAvatar,
  },
  created() {
    this.loadTemplates()
  },
  watch: {
    canShowAddTemplate(newVal) {
      if (newVal) {
        this.showCreateDialog = newVal
      }
    },
  },
  computed: {
    ...mapGetters(['getUser']),
    targetModuleName() {
      return 'workOrderSurveyTemplate'
    },
  },
  methods: {
    switchTab(tab) {
      let params = { moduleName: this.moduleName }

      this.$router.push({ name: tab.name, params })
    },
    async loadTemplates() {
      let { targetModuleName } = this
      this.isLoading = true
      let url = 'v3/modules/data/list'
      let params = {
        page: 1,
        perPage: 50,
        withCount: true,
        viewName: 'all',
        force: false,
        moduleName: targetModuleName,
      }
      let { data, error } = await API.get(url, params)

      if (error) {
        this.$message.error('Error Occured')
      } else {
        let { workOrderSurveyTemplate } = data || {}
        this.templates = workOrderSurveyTemplate || {}
      }
      this.isLoading = false
    },
    getCreatedTime(template) {
      let { sysCreatedTime } = template || {}

      if (!isEmpty(sysCreatedTime)) {
        return this.$options.filters.formatDate(sysCreatedTime) || '----'
      }
    },
    saveRecord(record) {
      let { isNew } = this
      let { id } = record || {}
      if (isNew) {
        this.createPage(id)
        this.redirectToBuilder(id)
      } else {
        this.loadTemplates()
      }
      this.selectedTemplate = null
    },
    async createPage(templateId) {
      let data = {
        name: 'Page 1',
        description: '',
        parent: templateId,
        position: 1,
      }
      await API.createRecord('qandaPage', {
        data,
      })
    },
    redirectToBuilder(id) {
      this.$router.push({
        name: 'newSurveyQandABuilder',
        params: { id },
        query: {
          pageNo: 1,
        },
      })
    },
    setPage(page) {
      this.templatePage = page
    },
    editTemplate(template) {
      this.showCreateDialog = true
      this.isNew = false
      this.selectedTemplate = template || {}
    },
    //For later purpose
    // async deleteTemplate(idList) {
    //   let { targetModuleName } = this
    //   let value = await this.$dialog.confirm({
    //     title: this.$t('survey.delete_template'),
    //     message: this.$t('survey.are_you_sure_delete_template'),
    //     rbDanger: true,
    //     rbLabel: 'Delete',
    //   })
    //   if (value) {
    //     let { error } = await API.deleteRecord(targetModuleName, idList)
    //     if (error) {
    //       this.$message.error('Error Occured')
    //     } else {
    //       this.loadTemplates()
    //     }
    //   }
    // },
    handleCloseDialog() {
      this.showCreateDialog = false
      this.selectedTemplate = null
      this.$emit('update:canShowAddTemplate', false)
    },
  },
}
</script>

<style lang="scss">
.survey-tab-pane {
  position: sticky;
  top: 82px;
  z-index: 5;
  background-color: #f8f9fa;
}
.survey-page .el-tabs__header {
  margin-left: 30px !important;
  padding-left: 0px !important;
  width: 98%;
  background-color: inherit !important;
}
.survey-template-table {
  overflow: hidden;
  margin-bottom: 60px !important;
}
.qna-btn {
  height: 35px;
}
.template-actions {
  padding-right: -50px;
}
</style>
<style lang="scss">
.survey-template-table {
  .el-table__row {
    height: 60px;
    td {
      padding-left: 20px !important;
    }
  }
}
</style>
