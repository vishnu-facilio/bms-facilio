<template>
  <div :class="['asset-details-widget pT0', widget.title && 'pT0']">
    <template v-if="widget.title">
      <div class="widget-topbar">
        <div class="widget-title mL0">{{ widget.title }}</div>
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
      <div class="width100 container">
        <div
          v-for="(field, index) in fieldsList"
          :key="`top-down-field-${index}`"
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
              <template v-else-if="field.displayTypeEnum === 'MULTI_CURRENCY'">
                <CurrencyPopOver
                  :field="field"
                  :details="details"
                  :showInfo="true"
                />
              </template>
              <template v-else-if="field.displayTypeEnum === 'RICH_TEXT'">
                <div
                  class="rich-text-preview-details"
                  v-if="!$validation.isEmpty(field.displayValue)"
                  v-html="sanitize(field.displayValue)"
                ></div>
                <span v-else>{{ $t('jobplan.na') }}</span>
              </template>
              <span v-else>{{ field.displayValue }}</span>
            </div>
          </div>
        </div>
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
import FieldDetails from './HorizontalFieldDetails.vue'
import { isEmpty } from '@facilio/utils/validation'

export default {
  name: 'TopDownFieldDetails',
  extends: FieldDetails,
  methods: {
    isFullColumnSpan(field) {
      let { displayTypeEnum } = field || {}

      return ['TEXT_AREA', 'RICH_TEXT'].includes(displayTypeEnum)
    },
    isRotatingItemOrTool(field) {
      let { details } = this
      let { rotatingItem } = details || {}
      let { id } = rotatingItem || {}
      let { name } = field || {}

      if (!isEmpty(id) && name === 'rotatingItem') return true
      return false
    },
    isSignatureField(field) {
      return this.$getProperty(field, 'field.displayType') === 'SIGNATURE'
    },
    getRecordData(field) {
      let { details } = this
      let { name } = field || {}
      if (name === 'rotatingItem') {
        let { rotatingItem } = details || {}
        let { itemType } = rotatingItem || {}
        return itemType
      } else {
        let { rotatingTool } = details || {}
        let { toolType } = rotatingTool || {}
        return toolType
      }
    },
    redirectToOverview(field) {
      let { details } = this
      let { name } = field || {}
      if (name === 'rotatingItem') {
        let { rotatingItem } = details || {}
        let { id: itemId } = rotatingItem || {}
        this.$router.push({
          path: `/app/inventory/item/all/${itemId}/summary`,
        })
      } else {
        let { rotatingTool } = details || {}
        let { id: toolId } = rotatingTool || {}
        this.$router.push({
          path: `/app/inventory/tools/all/${toolId}/summary`,
        })
      }
    },
  },
}
</script>
<style scoped>
.asset-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.empty-state-color {
  color: #8ca1ad;
}
.container {
  font-size: 13px;
  display: flex;
  flex-wrap: wrap;
  overflow: hidden;
}
.field {
  flex: 0 25%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}
.field-full-width {
  flex: 0 100%;
}

.fc-link-animation {
  animation: linkanimation 0.85s linear infinite alternate;
}
@keyframes linkanimation {
  from {
    transform: translateY(-2px);
  }
  to {
    transform: translateY(2px);
  }
}

.lines {
  height: 15px;
  width: 200px;
  margin: 0px 20px 0px 20px;
  border-radius: 5px;
}

.field-label,
.field-value {
  word-break: break-word;
  padding-right: 10px;
}
.field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
.full-row {
  flex: 0 100%;
}
</style>
<style scoped lang="scss">
.url-field-display {
  color: #46a2bf;
  &:hover {
    text-decoration: underline;
    text-underline-offset: 3px;
    color: #46a2bf;
  }
}
</style>
