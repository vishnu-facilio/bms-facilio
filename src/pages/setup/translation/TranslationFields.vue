<template>
  <div :class="{ 'bg-small': hasFilter, 'bg-large': !hasFilter }">
    <Spinner v-if="loadingFields" :show="loadingFields" size="80"></Spinner>
    <div v-else-if="showEmpty" class="empty-container">
      <InlineSvg
        src="svgs/emptystate/readings-empty"
        iconClass="icon text-center icon-130 emptystate-icon-size"
      ></InlineSvg>
      <div class="empty-state-text">
        {{ $t('setup.translation.no_fields') }}
      </div>
    </div>
    <div v-else class="field_container">
      <div class="mT10 pB20" v-if="checkActiveColumnIsReport">
        <el-select
          v-model="selectedReportFolder"
          filterable
          default-first-option
          class="fc-input-full-border-select2 pR20"
          @change="loadReportList()"
          :placeholder="$t('common._common.select_report_folder')"
        >
          <el-option
            v-for="item in reportFolderData"
            :key="item.id"
            :value="item.name"
          >
          </el-option>
        </el-select>
        <el-select
          class="fc-input-full-border-select2 pL10"
          filterable
          v-model="selectedReport"
          default-first-option
          @change="reportLoadDetail()"
          :placeholder="$t('common._common.select_report')"
        >
          <el-option
            v-for="item in selectedReportList"
            :key="item.id"
            :value="item.name"
          >
          </el-option>
        </el-select>
      </div>
      <div v-for="section in sections" :key="section.key">
        <div
          v-if="!isEmpty(section.label)"
          class="bold text-uppercase mB10 fc-black-13 text-left letter-spacing1"
        >
          {{ section.label }}
        </div>
        <div
          v-for="field in section.fields"
          :key="field.prefix + field.key + field.suffix"
        >
          <div class="input-field">
            <i
              :class="[
                field.isExpanded
                  ? 'el-icon-remove-outline'
                  : 'el-icon-circle-plus-outline',
                !field.fields && 'hide-icon',
                'cursor-pointer',
              ]"
              @click="toggleExpansion(field)"
            ></i>
            <p>{{ field.label }}</p>
            <el-input
              class="el-input-textbox-full-border"
              v-model="field.value"
            ></el-input>
          </div>
          <transition name="fade">
            <div v-show="field.isExpanded">
              <div
                class="input-field"
                v-for="option in field.fields"
                :key="option.prefix + option.key + option.suffix"
              >
                <i class="el-icon-remove-outline hide-icon"></i>
                <p>{{ option.label }}</p>
                <el-input
                  class="el-input-textbox-full-border"
                  v-model="option.value"
                ></el-input>
              </div>
            </div>
          </transition>
        </div>
      </div>

      <div v-if="!isEmpty(sections)" class="d-flex save">
        <AsyncButton class="fc-wo-border-btn" :clickAction="saveFields">
          {{ $t('common._common._save') }}
        </AsyncButton>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import AsyncButton from '@/AsyncButton'
import Spinner from '@/Spinner'

export default {
  props: [
    'tabId',
    'columnId',
    'appId',
    'filter',
    'hasFilter',
    'activeColumnId',
  ],
  components: {
    AsyncButton,
    Spinner,
  },
  data() {
    return {
      loadingFields: false,
      sections: null,
      showReportDropdown: false,
      reportFolderData: null,
      selectedReportFolder: null,
      selectedReport: null,
      reportQuery: {},
      selectedReportList: [],
    }
  },
  computed: {
    langCode() {
      let { params } = this.$route
      let { language } = params || {}
      return language
    },

    checkActiveColumnIsReport() {
      let { activeColumnId } = this
      return activeColumnId === 'REPORT'
    },

    showEmpty() {
      const {
        filter,
        hasFilter,
        sections,
        checkActiveColumnIsReport,
        reportFolderData,
      } = this
      if (checkActiveColumnIsReport) {
        return isEmpty(reportFolderData)
      } else {
        return (!hasFilter || !filter.loading) && isEmpty(sections)
      }
    },
  },
  async created() {
    let { checkActiveColumnIsReport } = this
    if (checkActiveColumnIsReport) {
      this.loadReport()
    } else {
      this.loadFields()
    }
  },
  watch: {
    filter: function() {
      this.loadFields()
    },
  },
  methods: {
    async loadReport() {
      let { appId } = this
      this.loadingFields = true
      let { data, error } = await API.get('v3/report/folders', {
        appId,
      })
      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.report_folder_error')
        )
      } else {
        let { reportFolders } = data || {}
        this.reportFolderData = reportFolders
        if (!isEmpty(data)) {
          this.loadingFields = false
          this.showReportDropdown = true
          this.selectedReportFolder = this.$getProperty(
            this.reportFolderData,
            '0.name',
            null
          )
          this.loadReportList()
        }
      }
    },
    reportLoadDetail() {
      let { selectedReportList, selectedReport } = this
      let currentReportObject = selectedReportList.find(
        ele => ele.name === selectedReport
      )
      let { id: reportId } = currentReportObject || {}
      this.reportQuery = { reportId }
      this.loadFields()
    },
    loadReportList() {
      let { selectedReportFolder, reportFolderData } = this
      let selectedReportObject = reportFolderData.find(
        ele => ele.name === selectedReportFolder
      )
      this.selectedReportList = this.$getProperty(
        selectedReportObject,
        'reports',
        []
      )
      this.selectedReport = this.$getProperty(
        this.selectedReportList,
        '0.name',
        null
      )
      this.reportLoadDetail()
    },
    toggleExpansion(field) {
      this.$set(field, 'isExpanded', !field.isExpanded)
    },
    isEmpty,
    async saveFields() {
      let filteredFields = []
      this.sections.forEach(section => {
        const fields = section.fields.filter(field => {
          return !isEmpty(field.value)
        })
        section.fields.forEach(field => {
          const { fields: options } = field
          const changedOptions = options
            ? options.filter(option => {
                return !isEmpty(option.value)
              })
            : []
          filteredFields = [...filteredFields, ...changedOptions]
        })
        filteredFields = [...filteredFields, ...fields]
      })
      const { error } = await API.post('v2/translation/update', {
        langCode: this.langCode,
        translations: filteredFields,
      })
      if (error) {
        this.$message.error(error.message || 'Error occurred')
      } else {
        this.$message.success('Changes saved')
      }
    },
    async loadFields() {
      const { hasFilter, filter, tabId, columnId, langCode, reportQuery } = this
      const { query, loading } = filter || {}
      this.sections = null
      if (hasFilter && (loading || !query)) return
      this.loadingFields = true
      const { error, data } = await API.post('/v2/translation/getFields', {
        tabId: tabId,
        translationType: columnId,
        langCode: langCode,
        filter: !isEmpty(reportQuery) ? reportQuery : query,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
        this.sections = {}
      } else {
        const { sections } = data ?? {}
        let list = []
        sections &&
          sections.forEach((section, index) => {
            let { fields, label } = section
            fields = fields
              ? fields.map(field => {
                  return {
                    ...field,
                    value: field.value ?? '',
                    ...(!isEmpty(field.fields) && { isExpanded: false }),
                  }
                })
              : []
            if (!isEmpty(fields)) {
              list = [
                ...list,
                {
                  label,
                  fields,
                  key: index,
                },
              ]
            }
          })
        this.sections = list
        this.reportQuery = {}
      }
      this.loadingFields = false
    },
  },
}
</script>

<style scoped lang="scss">
.empty-container {
  height: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-content: center;
  align-items: center;

  .empty-state-text {
    font-size: 14px;
    letter-spacing: 0.5px;
    text-align: center;
    color: #324056;
    padding: 15px;
  }
}

.bg-small {
  height: calc(100vh - 270px);
  overflow-y: scroll;
  position: relative;
}

.bg-large {
  height: calc(100vh - 235px);
  overflow-y: scroll;
  position: relative;
}
.field_container {
  max-width: 600px;
  margin-left: 20px;
}

.save {
  padding: 10px 0px 5px 23px;
  z-index: 999;
  position: sticky;
  bottom: 0px;
  background-color: white;
}

.input-field {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-block: 20px;
}

p {
  margin: 0;
  font-size: 14px;
  width: 200px;
}

.el-input {
  margin-left: 20px;
}

i {
  color: #39b2c2;
  padding-right: 5px;
  font-size: 18px;
}

.hide-icon {
  visibility: hidden;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
