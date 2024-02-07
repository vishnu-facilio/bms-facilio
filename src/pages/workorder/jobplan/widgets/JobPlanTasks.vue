<template>
  <div class="task-view">
    <div v-if="sectionLoading" class="flex-middle fc-empty-white">
      <spinner :show="sectionLoading" size="80"></spinner>
    </div>
    <el-row v-else class="section-area">
      <el-col class="d-flex mT10 mL10 section-name" :span="21">
        <div class="section-sequence">
          {{ `JP-${sectionData.sequenceNumber}` }}
        </div>
        <div class="mL15">{{ sectionName }}</div>
      </el-col>
      <el-col class="mT15 pR10" :span="3">
        <div class="view-details" @click="setActiveSectionOrTask()">
          {{ $t('jobplan.view_detail') }}
        </div>
      </el-col>
    </el-row>
    <div class="task-area" v-if="sectionData">
      <div class="task-details">
        <span class="text-capitalize pL20 self-center">{{
          $t('jobplan._task_details')
        }}</span>
      </div>
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div v-else class="task-view-area">
        <el-row
          v-for="(task, index) in selectedSectionTasks"
          :key="index"
          class="task-row d-flex"
        >
          <el-col class="task-info  d-flex pT15" :span="20">
            <div class="task-id pL20">
              {{ `#${task.sequence}` }}
            </div>
            <div class="d-flex  mL15 flex-direction-column   task-context">
              <div class="task-subject">
                {{ getTaskSubject(task) }}
              </div>
              <div
                v-if="task.description"
                :title="task.description"
                v-tippy
                class="task-desc mT5 mB10"
              >
                {{ task.description }}
              </div>
            </div>
          </el-col>
          <el-col class="pT15 pL30" :span="4">
            <div
              class="view-details mL10"
              @click="setActiveSectionOrTask(index)"
            >
              {{ $t('jobplan.view_detail') }}
            </div>
          </el-col>
        </el-row>
      </div>
      <pagination
        :pageNo.sync="taskPage"
        :total="taskTotalCount"
        :perPage="taskPerPage"
        class="mT80 pT15 pB10 d-flex justify-center task-pagination"
        @onPageChanged="setPage"
      ></pagination>
    </div>
    <JobPlanSettingsDialog
      v-if="showSettingDialog"
      :showSettingDialog.sync="showSettingDialog"
      :isSection="isSection"
      :selectedSectionInfo="selectedSectionInfo"
    />
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/pages/setup/components/SetupPagination.vue'
import JobPlanSettingsDialog from './JobPlanSettingsDialog.vue'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  name: 'JobPlanTasks',
  data() {
    return {
      selectedSectionTasks: [],
      taskPage: 1,
      taskPerPage: 10,
      taskTotalCount: null,
      isSection: false,
      selectedSectionInfo: null,
      showSettingDialog: false,
      isLoading: false,
    }
  },
  props: ['sectionData', 'jobPlanId', 'sectionLoading'],
  components: { Pagination, JobPlanSettingsDialog, Spinner },
  watch: {
    taskPage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.getSectionTasks()
        }
      },
      immediate: true,
    },
    sectionData: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.getSectionTasks()
        }
      },
      immediate: true,
    },
    showSettingDialog: {
      async handler(newVal) {
        if (!newVal) {
          this.isSection = false
          this.selectedSectionInfo = null
        }
      },
      immediate: true,
    },
  },
  created() {
    this.getSectionTasks()
  },
  computed: {
    sectionName() {
      let { sectionData } = this
      let { name } = sectionData || {}
      if (!isEmpty(name)) {
        name = name.replace('JP-', '')
        return name
      }
      return ''
    },
    jobPlanSectionId() {
      let { sectionData } = this
      let { id } = sectionData || {}
      return id
    },
  },
  methods: {
    getTaskSubject(task) {
      let { subject } = task
      if (!isEmpty(subject)) subject = subject.replace('JP-', '')

      return subject
    },
    setActiveSectionOrTask(taskIndex) {
      let { sectionData, selectedSectionTasks } = this

      this.isSection = isEmpty(taskIndex)
      if (!isEmpty(selectedSectionTasks) && !this.isSection) {
        this.selectedSectionInfo = selectedSectionTasks[taskIndex]
      } else {
        this.selectedSectionInfo = sectionData
      }
      this.showSettingDialog = true
    },
    setPage(page) {
      this.taskPage = page
    },
    async getSectionTasks() {
      this.isLoading = true
      let { taskPerPage, taskPage, jobPlanSectionId } = this
      let params = {
        page: taskPage,
        perPage: taskPerPage,
        jobplansection: jobPlanSectionId,
        withCount: true,
      }
      let relatedFieldName = getRelatedFieldName(
        'jobplansection',
        'jobplantask'
      )
      let relatedConfig = {
        moduleName: 'jobplansection',
        id: jobPlanSectionId,
        relatedModuleName: 'jobplantask',
        relatedFieldName,
      }
      let { error, list, meta } = await API.fetchAllRelatedList(
        relatedConfig,
        params
      )

      if (error) {
        let { message } = error || {}
        this.$message.error(message || 'Error Occured')
      } else {
        let { pagination } = meta || {}
        let { totalCount = null } = pagination || {}

        this.$set(this, 'selectedSectionTasks', list)
        this.$set(this, 'taskTotalCount', totalCount)
      }
      this.isLoading = false
    },
  },
}
</script>
<style scoped lang="scss">
.task-view {
  max-height: 750px;
  background-color: #fff;
  margin-bottom: 20px;
  .section-area {
    height: 40px;
    display: flex;
    justify-content: space-between;
    padding: 5px 20px 10px 10px;
    font-weight: 500;
    font-size: 16px;
    color: #324056;
  }

  .section-name {
    font-weight: 500;
    font-size: 16px;
  }
  .task-area {
    max-height: 700px;
    display: flex;
    flex-direction: column;
  }
  .task-id {
    color: #38b2c2;
  }
  .pR65 {
    padding-right: 65px;
  }
  .view-details {
    color: #0a7aff;
    letter-spacing: 0.5px;
    font-size: 12px;
    cursor: pointer;
    font-weight: 500;
    &:hover {
      text-decoration: underline;
    }
  }
  .task-details {
    margin-top: 10px;
    height: 40px;
    background-color: #f3f1fc;
    display: flex;
    font-weight: 500;
  }
  .task-row {
    cursor: pointer;
    min-height: 55px;
    border-bottom: 0.7px solid #d8d8d8;
    justify-content: space-between;
  }
  .task-info {
    height: inherit;
    .task-context {
      max-width: 100%;
      word-break: break-word;
      .task-desc {
        color: #848484;
        font-weight: 300;
        max-width: 95%;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }
  .task-scope {
    height: inherit;
    right: 0px;
  }
  .task-view-area {
    max-height: 650px;
    overflow-y: scroll;
  }

  .task-pagination {
    border-top: 0.7px solid #d8d8d8;
  }
}
</style>
