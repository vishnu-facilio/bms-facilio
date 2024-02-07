<template>
  <div :class="['inspection-details-widget', title && 'pT0']">
    <template v-if="title">
      <div class="widget-topbar">
        <div class="widget-title mL0 pm-title">{{ title }}</div>
      </div>
    </template>

    <!-- Shimmer loading -->
    <div v-if="loading" :class="['container', widget.title && 'pL0']">
      <el-row
        v-for="index in [1, 2, 3]"
        :key="`rowIndex-${index}`"
        class="width100"
      >
        <el-col
          :span="6"
          class="field"
          v-for="index in [1, 2, 3, 4]"
          :key="`col-${index}`"
        >
          <div class="d-flex flex-direction-column">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
            <span
              :class="['lines loading-shimmer', widget.title && 'mT10']"
            ></span>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- Empty state -->
    <div v-else-if="emptyState" class="mT20 empty-state-color">
      <div class="d-flex flex-direction-column">
        <inline-svg
          src="svgs/emptystate/notes"
          class="vertical-middle self-center"
          iconClass="icon icon-xxxlg mR5"
        ></inline-svg>
        <div class="f14 pL10 self-center">
          {{ $t('qanda.template.no_fields') }}
        </div>
      </div>
    </div>

    <div
      v-else
      :class="['container', widget.title && 'pL0']"
      ref="content-container"
    >
      <template v-if="isSecondaryDetails && !$validation.isEmpty(fieldsList)">
        <el-row class="width100" v-if="!$validation.isEmpty(details.subject)">
          <el-col class="field" :span="24">
            <div class="d-flex flex-direction-column">
              <span class="field-label mB10">
                {{ $t('maintenance.wr_list.subject') }}
              </span>
              <div class="field-value pL0">
                {{ details.subject }}
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row
          class="width100"
          v-if="!$validation.isEmpty(details.description)"
        >
          <el-col class="field" :span="24">
            <div class="d-flex flex-direction-column">
              <span class="field-label mB10">
                {{ $t('maintenance._workorder.description') }}
              </span>
              <div class="field-value pL0">
                {{ details.description }}
              </div>
            </div>
          </el-col>
        </el-row>
      </template>
      <div class="width100 pmv2-wo">
        <div
          v-for="(field, index) in fieldsList"
          :key="`top-down-field-${index}`"
          class="field"
          :class="['field', isFullColumnSpan(field) && 'field-full-width']"
        >
          <div class="d-flex flex-direction-column">
            <span class="field-label mB10">
              {{ getFormattedDisplayName(field) }}
            </span>
            <div class="field-value pL0">
              <div
                v-if="isRotatingItemOrTool(field)"
                @click="redirectToOverview(field)"
              >
                <item-tool-avatar
                  name="true"
                  size="lg"
                  module="item"
                  :recordData="getRecordData(field)"
                ></item-tool-avatar>
              </div>
              <div v-else-if="isLookupSimple(field)">
                <GlimpseLookupWrapper
                  :field="field"
                  :record="details"
                  :siteList="siteList"
                  :recordModuleName="moduleName"
                ></GlimpseLookupWrapper>
              </div>

              <div v-else-if="systemFields.includes(field.name)">
                {{ getSysFieldValue(field) }}
              </div>
              <div v-else-if="isFileField(field)">
                <el-row>
                  <el-col
                    :span="12"
                    :key="index + 1 + 'asset'"
                    class="attachment-label pointer"
                  >
                    <iframe
                      v-if="exportDownloadUrl[field.name]"
                      :src="exportDownloadUrl[field.name]"
                      style="display: none;"
                    ></iframe>

                    <a @click="downloadAttachment(field)">
                      {{ getFileName(field) }}
                    </a>
                  </el-col>
                </el-row>
              </div>

              <div v-else-if="isSignatureField(field)">
                <SignatureField
                  :field="(field || {}).field"
                  :record="details"
                />
              </div>

              <template v-else-if="isGeoLocationField(field)">
                <div
                  v-if="!$validation.isEmpty(field.displayValue)"
                  class="bluetxt pointer"
                  @click="$helpers.openInMapForLatLngStr(field.displayValue)"
                >
                  {{ `@${field.displayValue}` }}
                </div>
                <span v-else>---</span>
              </template>
              <template v-else-if="field.displayTypeEnum === 'URL_FIELD'">
                <a
                  v-if="
                    !$validation.isEmpty(field.displayValue) &&
                      field.displayValue !== '---'
                  "
                  rel="nofollow"
                  class="url-field-display"
                  :href="$getProperty(field, 'displayValue.href', '')"
                  :target="$getProperty(field, 'displayValue.target', '')"
                  referrerpolicy="no-referrer"
                  >{{ getUrlDisplayName(field) }}</a
                >
                <span v-else>---</span>
              </template>
              <template v-else-if="field.displayTypeEnum === 'RICH_TEXT'">
                <div
                  class="rich-text-preview-details"
                  v-if="!$validation.isEmpty(field.displayValue)"
                  v-html="sanitize(field.displayValue)"
                ></div>
                <span v-else>---</span>
              </template>
              <span v-else>{{ field.displayValue }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import FieldDetails from '@/page/widget/common/field-details/VerticalFieldDetails'
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'
import { SCOPE_PLACEHOLDER } from '../create/utils/pm-utils'

const SCOPE_FIELDS = [
  {
    name: 'sites',
    displayName: 'Sites',
    field: {
      displayType: 'MULTI_LOOKUP_SIMPLE',
      dataType: 13,
      dataTypeEnum: 'MULTI_LOOKUP',
      name: 'sites',
    },
  },
  {
    displayName: 'Asset Category',
    name: 'assetCategory',
    field: {
      displayType: 'LOOKUP_SIMPLE',
      name: 'assetCategory',
    },
  },
  {
    displayName: 'Space Category',
    name: 'spaceCategory',
    field: {
      displayType: 'LOOKUP_SIMPLE',
      name: 'spaceCategory',
    },
  },
  {
    displayName: 'Category',
    name: 'assignmentTypeEnum',
    field: {
      name: 'assignmentTypeEnum',
    },
  },
]

const SCOPE_FIELDS_LIST = [
  'sites',
  'assignmentTypeEnum',
  'assetCategory',
  'spaceCategory',
  'sysCreatedTime',
]

export default {
  extends: FieldDetails,
  props: ['widget', 'moduleName'],
  name: 'PMFieldDetails',
  created() {
    this.sanitize = sanitize
  },
  computed: {
    title() {
      let { isSecondaryDetails } = this || {}
      if (isSecondaryDetails) return 'Workorder'
      else return 'Scope'
    },
    isSecondaryDetails() {
      let { widget } = this || {}
      let { widgetParams } = widget || {}
      let { card } = widgetParams || {}
      return card === 'plannedMaintenanceSecondaryDetails'
    },
  },
  methods: {
    deserialize(data) {
      let { sites } = this
      let promises = []
      let skipFields = [...this.filterfields, ...this.primaryFields]

      // Fetch field values
      let fieldsArray = this.$getProperty(data, 'fields', [])

      fieldsArray = [...SCOPE_FIELDS, ...fieldsArray]

      let fields = []
      fieldsArray.forEach(field => {
        let dataObj =
          !this.isV3Api && field.field && field.field.default !== true
            ? this.details.data
            : this.details

        if (!skipFields.includes(this.$getProperty(field, 'field.name'))) {
          let promise = this.getFormattedValue(field, dataObj, sites).then(
            value => {
              this.$set(field, 'displayValue', value)
            }
          )
          promises.push(promise)
          fields.push(field)
        }
      })

      let { isSecondaryDetails, details } = this
      if (!isSecondaryDetails) {
        let actualFields = SCOPE_FIELDS_LIST.map(field => {
          let modifiedField = fields.find(currField => currField.name === field)
          let name = this.$getProperty(modifiedField, 'field.name')
          if (name === 'assignmentTypeEnum') {
            let { assignmentTypeEnum } = details || {}
            let value = SCOPE_PLACEHOLDER[assignmentTypeEnum] || {}
            modifiedField = { ...modifiedField, displayValue: value }
          }
          return modifiedField
        })
        let { assignmentTypeEnum } = details || {}
        if (assignmentTypeEnum === 'ASSETCATEGORY') {
          actualFields = actualFields.filter(
            field => field.name !== 'spaceCategory'
          )
        } else if (assignmentTypeEnum === 'SPACECATEGORY') {
          actualFields = actualFields.filter(
            field => field.name !== 'assetCategory'
          )
        } else {
          actualFields = actualFields.filter(
            field => !['spaceCategory', 'assetCategory'].includes(field.name)
          )
        }
        this.fieldsList = actualFields.filter(field => !isEmpty(field))
      } else {
        this.fieldsList = fields.filter(
          field => !SCOPE_FIELDS_LIST.includes(field.name)
        )
      }

      this.fieldsList = this.fieldsList.filter(
        field => !['subject', 'description', 'category'].includes(field.name)
      )

      let fieldCount =
        this.fieldsList.length -
        this.fieldsList.filter(field => field.displayTypeEnum === 'TEXTAREA')
          .length
      if (this.details.geoLocationEnabled) fieldCount += 5

      if (fieldCount % 2 !== 0) this.additionalField = true

      Promise.all(promises).then(() => {
        this.loading = false
        this.autoResize()
      })
    },
  },
}
</script>

<style lang="scss">
.inspection-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.pm-title {
  font-size: 12px;
  text-transform: uppercase;
}
.pmv2-wo {
  display: flex;
  flex-wrap: wrap;
  .field {
    flex: 0 25%;
    padding: 20px 0;
    border-bottom: 1px solid #edf4fa;
    transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  }
}
</style>
