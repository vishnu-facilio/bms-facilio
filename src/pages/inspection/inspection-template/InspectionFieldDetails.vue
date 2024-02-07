<template>
  <div :class="['inspection-details-widget', title && 'pT0']">
    <template v-if="title">
      <div class="widget-topbar">
        <div class="widget-title mL0">{{ title }}</div>
      </div>
    </template>

    <div v-if="loading" :class="['container', title && 'pL0']">
      <div v-for="index in [1, 2, 3]" :key="index" class="field">
        <el-row>
          <el-col :span="12">
            <span :class="['lines loading-shimmer', title && 'm0']"></span>
          </el-col>

          <el-col :span="12">
            <span :class="['lines loading-shimmer', title && 'm0']"></span>
          </el-col>
        </el-row>
      </div>
    </div>

    <div v-else-if="emptyState" class="mT20" style="color: #8ca1ad;">
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
      <template v-for="(field, index) in fieldsList">
        <el-row
          v-if="['TEXTAREA', 'RICH_TEXT'].includes(field.displayTypeEnum)"
          :key="field.name + index"
          class="field field-text-area"
        >
          <el-col :span="6" class="field-label">
            {{ getFormattedDisplayName(field) }}
          </el-col>
          <el-col
            v-if="field.displayTypeEnum === 'TEXTAREA'"
            :span="16"
            class="field-value line-height22"
          >
            {{ field.displayValue }}
          </el-col>
          <el-col
            v-if="field.displayTypeEnum === 'RICH_TEXT'"
            :span="16"
            class="field-value line-height22"
          >
            <div
              class="rich-text-preview-details"
              v-if="!$validation.isEmpty(field.displayValue)"
              v-html="sanitize(field.displayValue)"
            ></div>
            <span v-else>---</span>
          </el-col>
        </el-row>

        <el-row v-else :key="`${field.name}-${index}`" class="field">
          <el-col :span="12" :key="field.displayName" class="field-label">
            {{ getFormattedDisplayName(field) }}
          </el-col>
          <el-col
            :span="12"
            :key="field.displayName + 'value'"
            class="field-value"
          >
            <div v-if="field.name === 'moveApprovalNeeded'">
              {{ field.displayValue === true ? 'Yes' : 'No' }}
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

            <div
              v-else-if="
                $getProperty(field, 'field.displayType') === 'SIGNATURE'
              "
            >
              <SignatureField :field="(field || {}).field" :record="details" />
            </div>
            <template
              v-else-if="$getProperty(field, 'displayTypeEnum') === 'URL_FIELD'"
            >
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

            <span v-else>{{ field.displayValue }}</span>
          </el-col>
        </el-row>
      </template>

      <div v-if="additionalField" class="field">
        <el-row>
          <el-col :span="12" class="field-label"></el-col>
          <el-col :span="12" class="field-value"></el-col>
        </el-row>
      </div>
    </div>
    <div
      v-if="needsShowMore"
      class="text-center pT10 pB15 mR20"
      style="background-color: #fff;"
    >
      <a
        @click="toggleVisibility()"
        class="fc-link fc-link-animation text-capitalize letter-spacing0_3 f13"
        >{{ showMoreLinkText }}</a
      >
    </div>
  </div>
</template>
<script>
import FieldDetails from 'src/components/page/widget/common/field-details/HorizontalFieldDetails.vue'
import { isEmpty } from '@facilio/utils/validation'
import { sanitize } from '@facilio/utils/sanitize'

export default {
  extends: FieldDetails,
  props: ['widget', 'moduleName'],
  name: 'InspectionFieldDetails',
  created() {
    this.sanitize = sanitize
  },
  computed: {
    title() {
      let { moduleName } = this
      let isConfigurationWidget = this.$getProperty(
        this,
        'widget.widgetParams.isConfigurationWidget'
      )
      if (moduleName.includes('inspection')) {
        if (isConfigurationWidget) return 'Configuration Details'
        else return 'Field Details'
      } else {
        return ''
      }
    },
    requiredFields() {
      let fields = this.$getProperty(this, 'widget.widgetParams.fields')
      return fields
    },
  },
  methods: {
    deserialize(data) {
      let { sites } = this
      let promises = []
      let skipFields = [...this.filterfields, ...this.primaryFields]

      // Fetch field values
      let fieldsArray = this.$getProperty(data, 'fields', [])

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
      let { requiredFields } = this
      if (!isEmpty(requiredFields)) {
        let { requiredFields } = this
        this.fieldsList = fields.filter(field =>
          requiredFields.includes(field.name)
        )
      } else {
        this.fieldsList = fields.filter(field => !field.hideField)
      }

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

<style>
.inspection-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
