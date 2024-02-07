<template>
  <div :class="['asset-details-widget', widget.title && 'pT0']">
    <template v-if="widget.title">
      <div class="widget-topbar">
        <div class="widget-title mL0">{{ widget.title }}</div>
      </div>
    </template>

    <div v-if="loading" :class="['container', widget.title && 'pL0']">
      <div v-for="index in [1, 2, 3]" :key="index" class="field">
        <el-row>
          <el-col :span="12">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
          </el-col>

          <el-col :span="12">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
          </el-col>
        </el-row>
      </div>

      <div class="field">
        <el-row>
          <el-col :span="12"></el-col>
          <el-col :span="12"></el-col>
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
          class="field full-row"
        >
          <el-col :span="6" class="field-label">
            {{ getFormattedDisplayName(field) }}
          </el-col>
          <el-col
            v-if="field.displayTypeEnum === 'TEXTAREA'"
            :span="16"
            class="field-value line-height22 text-area"
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
        <!-- TODO Handle this based on displaytype and span-->
        <el-row
          :key="`childMeter-${field.name}-${index}`"
          v-else-if="showChildMeterExp && field.name === 'childMeterExpression'"
          class="field full-row"
        >
          <el-col :span="6" class="field-label">{{ field.displayName }}</el-col>
          <el-col :span="18" class="field-value">
            <child-meter-expression
              :assetDetails="alarmDetails"
            ></child-meter-expression>
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
            <div
              v-if="
                alarmDetails.rotatingItem &&
                  alarmDetails.rotatingItem.id &&
                  field.name === 'rotatingItem'
              "
              @click="
                $router.push({
                  path:
                    '/app/inventory/item/' +
                    'all/' +
                    alarmDetails.rotatingItem.id +
                    '/summary',
                })
              "
            >
              <item-tool-avatar
                name="true"
                size="lg"
                module="item"
                :recordData="alarmDetails['rotatingItem'].itemType"
              ></item-tool-avatar>
            </div>

            <div
              v-else-if="
                alarmDetails.rotatingTool &&
                  alarmDetails.rotatingTool.id &&
                  field.name === 'rotatingTool'
              "
              @click="
                $router.push({
                  path:
                    '/app/inventory/tools/' +
                    'all/' +
                    alarmDetails.rotatingTool.id +
                    '/summary',
                })
              "
            >
              <item-tool-avatar
                name="true"
                size="lg"
                module="tool"
                :recordData="alarmDetails['rotatingTool'].toolType"
              ></item-tool-avatar>
            </div>
            <div v-else-if="isLookupSimple(field)">
              <GlimpseLookupWrapper
                :field="field"
                :record="alarmDetails"
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
              <SignatureField
                :field="(field || {}).field"
                :record="alarmDetails"
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
            <template v-else-if="field.displayTypeEnum === 'CURRENCY'">
              <div class="d-flex" v-if="showCurrencyField(field)">
                <span>{{ getCurrencyFieldValue(field) }}</span>
                <el-popover
                  v-if="showInfoIcon(field)"
                  v-model="showPopOver"
                  placement="bottom"
                  :title="$t('setup.currency.rate')"
                  width="230"
                  trigger="click"
                >
                  <div
                    class="currency-conversion-rate"
                    @click="showPopOver = false"
                  >
                    <fc-icon
                      group="default"
                      name="close"
                      size="15"
                      class="pointer rate-svg-close"
                    ></fc-icon>
                    <span class="currency-desc">
                      {{ `${getCurrencyFieldValue(field, true)}` }}
                    </span>
                  </div>
                  <fc-icon
                    slot="reference"
                    group="dsm"
                    class="pointer info-position mL2"
                    name="info"
                    size="13"
                  ></fc-icon>
                </el-popover>
              </div>
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
  props: ['widget'],
  name: 'AlarmSecondaryDetails',
  created() {
    this.sanitize = sanitize
  },
  computed: {
    alarmDetails() {
      let { details } = this
      return this.$getProperty(details, 'alarm', {})
    },
    requiredFields() {
      let { widget } = this
      let { widgetParams } = widget || {}
      return this.$getProperty(widgetParams, 'fields', [])
    },
  },
  methods: {
    deserialize(data) {
      let promises = []
      let { filterfields } = this
      let skipFields = [...filterfields]

      // Fetch field values
      let fieldsArray = this.$getProperty(data, 'fields', [])

      let fields = []
      fieldsArray.forEach(currentField => {
        let { alarmDetails } = this
        let { field } = currentField
        let fieldName = this.$getProperty(field, 'name', '---')

        if (!skipFields.includes(fieldName)) {
          let promise = this.getFormattedValue(currentField, alarmDetails).then(
            value => {
              this.$set(currentField, 'displayValue', value)
            }
          )
          promises.push(promise)
          fields.push(currentField)
        }
      })
      let { requiredFields } = this
      if (!isEmpty(requiredFields)) {
        let { requiredFields } = this
        this.fieldsList = fields.filter(field => {
          let { name } = field
          return requiredFields.includes(name)
        })
      } else {
        this.fieldsList = fields.filter(field => {
          let { hideField } = field
          return !hideField
        })
      }
      let { fieldsList } = this
      let { length } = fieldsList || {}
      let textAreaField = fieldsList.filter(field => {
        let { displayTypeEnum } = field
        return displayTypeEnum === 'TEXTAREA'
      })
      let fieldCount = length - (textAreaField || []).length

      if (fieldCount % 2 !== 0) this.additionalField = true

      Promise.all(promises).then(() => {
        this.loading = false
        this.autoResize()
      })
    },
  },
}
</script>
