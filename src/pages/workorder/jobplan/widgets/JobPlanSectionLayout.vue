<template>
  <div class="jp-section-layout" ref="jobplanSectionWidget">
    <div class="section-banner d-flex">
      <div class="width95 mL10">{{ $t('jobplan.tasks') }}</div>
      <pagination
        :pageNo.sync="sectionPage"
        :total="sectionCount"
        :perPage="sectionsPerWidget"
        class="justify-end d-flex nowrap pT10"
        @onPageChanged="setPage"
      ></pagination>
    </div>
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="$validation.isEmpty(currentSections)">
      <div
        class=" fc-empty-white flex-middle justify-content-center flex-direction-column"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="mT10 fc-black-dark f16 fw6">
          <div class="mT10 label-txt-black f14 mL15">
            {{ $t('jobplan.no_section_available') }}
          </div>
        </div>
      </div>
    </template>
    <template v-else>
      <JobPlanTasks
        v-for="(section, index) in currentSections"
        :key="`section-${sectionPage}-${index}`"
        :sectionData="section"
        :jobPlanId="jobPlanId"
        :sectionLoading="isLoading"
      />
    </template>
  </div>
</template>
<script>
import JobPlanTasks from './JobPlanTasks.vue'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'src/pages/setup/components/SetupPagination.vue'
import { API } from '@facilio/api'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  name: 'JobPlanSectionLayout',
  data() {
    return {
      sectionsPerWidget: 5,
      sectionPage: 1,
      sectionCount: null,
      currentSections: [],
      isLoading: false,
    }
  },
  props: ['resizeWidget', 'widget', 'calculateDimensions'],
  components: { JobPlanTasks, Pagination },
  created() {
    this.getSections()
  },
  computed: {
    jobPlan() {
      let { details = {} } = this.$attrs
      return details
    },
    jobPlanId() {
      let { jobPlan } = this
      let { id } = jobPlan || {}
      return id
    },
    isPrerequisite() {
      let { widget } = this
      let { widgetParams } = widget || {}
      let { isPrerequisite = true } = widgetParams || {}

      return isPrerequisite
    },
  },
  watch: {
    sectionPage: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.getSections()
        }
      },
      immediate: true,
    },
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        let params = {}
        let { widget } = this
        let { layoutParams } = widget || {}
        let { h: defaultWidgetHeight } = layoutParams || {}
        let container = this.$refs['jobplanSectionWidget']

        if (container) {
          let height = container.scrollHeight
          let width = container.scrollWidth
          let { h } = this.calculateDimensions({ height, width })

          if (h <= defaultWidgetHeight) {
            params = { height, width }
          } else {
            params = { h }
          }
          this.resizeWidget(params)
        }
      })
    },
    setPage(page) {
      this.sectionPage = page
    },
    async getSections() {
      this.isLoading = true
      let { sectionsPerWidget, sectionPage, jobPlanId } = this
      let params = {
        page: sectionPage,
        perPage: sectionsPerWidget,
        withCount: true,
      }
      let relatedFieldName = getRelatedFieldName('jobplan', 'jobplansection')
      let relatedConfig = {
        moduleName: 'jobplan',
        id: jobPlanId,
        relatedModuleName: 'jobplansection',
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

        this.$set(this, 'currentSections', list)
        this.$set(this, 'sectionCount', totalCount)
        this.autoResize()
      }
      this.isLoading = false
    },
  },
}
</script>
<style scoped lang="scss">
.jp-section-layout {
  height: 100%;
  background-color: #f7f8f9 !important;
  .widget-header {
    display: none;
  }
  .width95 {
    width: 95% !important;
  }
  .section-banner {
    height: 50px;
    font-size: 18px;
    font-weight: bold;
    padding: 10px 20px 10px 10px;
    border-bottom: 1px solid #ccc;
    background-color: #fff;
  }
}
</style>
