<template>
  <div class="relationship-summary-widget">
    <div class="relationship-header-container">
      <div class="header-title">
        <div class="title-name">{{ relationshipDisplayName }}</div>
        <div class="title-link">
          {{ relationName }} -
          {{ toModuleDisplayName }}
        </div>
      </div>
      <template
        v-if="$hasPermission(`${parentModuleName}:UPDATE`) && !showLoading"
      >
        <div
          v-if="$validation.isEmpty(details) || emptyState"
          class="associate-relation"
          @click="showLookupFieldWizard = true"
        >
          <i class="el-icon-plus"></i>
          <div class="associate-text">
            {{ $t('setup.relationship.associate') }}
          </div>
        </div>

        <div v-else class="disassociate-relation" @click="disassociateRecord">
          <i class="el-icon-remove-outline"></i>
          <div class="disassociate-text">
            {{ $t('setup.relationship.dissociate') }}
          </div>
        </div>
      </template>
    </div>

    <div class="relationship-details-card">
      <div :class="['asset-details-widget p0', widget.title && 'pT0']">
        <template v-if="widget.title">
          <div class="widget-topbar">
            <div class="widget-title mL0">{{ widget.title }}</div>
          </div>
        </template>

        <div v-if="showLoading" :class="['container', widget.title && 'pL0']">
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

        <div
          v-else-if="emptyState"
          class="mT20 height100"
          style="color: #8ca1ad;"
        >
          <div
            class="d-flex flex-direction-column height100 justify-content-center"
          >
            <inline-svg
              :src="`svgs/emptystate/readings-empty`"
              class="vertical-middle self-center"
              iconClass="icon icon-xxxlg mR5"
            ></inline-svg>
            <div class="f18 pB50 fc-black-dark bold pT10 pL10 self-center">
              {{
                $t('setup.relationship.no_module_available', {
                  moduleName: toModuleDisplayName,
                })
              }}
            </div>
          </div>
        </div>

        <div
          v-else
          :class="['container', widget.title && 'pL0']"
          ref="content-container"
        >
          <template v-for="(field, index) in filteredFieldsList">
            <el-row
              v-if="field.field.mainField"
              :key="`${field.name}-${index}-${field.displayTypeEnum}-mainfield`"
              class="field"
            >
              <el-col :span="12" class="field-label">
                {{ field.field.displayName }}
              </el-col>
              <el-col
                :span="12"
                class="field-value line-height22"
                :class="{ 'main-field-column': isLink }"
              >
                <div @click="openRecordSummary(details.id)" class="is-know">
                  {{ field.displayValue }}
                </div>
              </el-col>
            </el-row>
            <el-row
              v-else-if="field.displayTypeEnum === 'TEXTAREA'"
              :key="`${field.name}-${index}-TEXTAREA`"
              class="field full-row"
            >
              <el-col :span="6" class="field-label">
                {{ getFormattedDisplayName(field) }}
              </el-col>
              <el-col :span="16" class="field-value line-height22">
                {{ field.displayValue }}
              </el-col>
            </el-row>
            <!-- TODO Handle this based on displaytype and span-->
            <el-row
              :key="`childMeterExpression-${index}-${field.displayTypeEnum}`"
              v-else-if="
                showChildMeterExp && field.name === 'childMeterExpression'
              "
              class="field full-row"
            >
              <el-col :span="6" class="field-label">
                {{ field.displayName }}
              </el-col>
              <el-col :span="18" class="field-value">
                <child-meter-expression
                  :assetDetails="details"
                ></child-meter-expression>
              </el-col>
            </el-row>
            <el-row
              v-else
              :key="`${field.name}-${index}-${field.displayTypeEnum}`"
              class="field"
            >
              <el-col :span="12" :key="field.displayName" class="field-label">
                {{ getFormattedDisplayName(field) }}
              </el-col>
              <el-col
                :span="12"
                :key="field.displayName + 'value'"
                class="field-value"
              >
                <div v-if="isRotatingItem(field)">
                  <item-tool-avatar
                    name="true"
                    size="lg"
                    module="item"
                    :recordData="details['rotatingItem'].itemType"
                  ></item-tool-avatar>
                </div>

                <div v-else-if="isRotatingTool(field)">
                  <item-tool-avatar
                    name="true"
                    size="lg"
                    module="tool"
                    :recordData="details['rotatingTool'].toolType"
                  ></item-tool-avatar>
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
                <template
                  v-else-if="field.displayTypeEnum === 'MULTI_CURRENCY'"
                >
                  <CurrencyPopOver
                    :field="field"
                    :details="details"
                    :showInfo="true"
                  />
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
            @click="toggleVisibility(120)"
            class="fc-link fc-link-animation text-capitalize letter-spacing0_3 f13"
            >{{ showMoreLinkText }}</a
          >
        </div>
      </div>
    </div>

    <LookupWizard
      v-if="showLookupFieldWizard"
      :key="`${relationName}-${toModuleName}`"
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :listUrlConfig="listUrlConfig"
      :config="{}"
      @setListValues="setListValues"
    ></LookupWizard>
  </div>
</template>
<script>
import FieldDetails from '@/page/widget/common/field-details/HorizontalFieldDetails.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import { LookupWizard } from '@facilio/ui/forms'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: FieldDetails,
  props: ['isLoading', 'moduleName', 'parentId', 'toModuleName'],
  components: { LookupWizard },
  data() {
    return {
      showLookupFieldWizard: false,
      loading: true,
    }
  },
  computed: {
    parentModuleName() {
      return this.$attrs.moduleName
    },
    filteredFieldsList() {
      let { fieldsList } = this

      if (!isEmpty(fieldsList)) {
        let mainFieldIdx = fieldsList.findIndex(fld => fld?.field?.mainField)

        if (mainFieldIdx !== -1) {
          let mainFieldObj = fieldsList[mainFieldIdx]
          fieldsList.splice(mainFieldIdx, 1)
          fieldsList.unshift(mainFieldObj)
        }
      }

      return fieldsList
    },
    toModuleDisplayName() {
      return this.$getProperty(this.widget, 'relation.toModule.displayName')
    },
    relationName() {
      return this.$getProperty(this.widget, 'relation.relationName')
    },
    showLoading() {
      return this.isLoading || this.loading
    },
    isLink() {
      let isCustomModule = this.$getProperty(
        this.widget,
        'relation.toModule.custom'
      )
      return isCustomModule || isWebTabsEnabled()
    },
    relationshipDisplayName() {
      return this.$getProperty(this.widget, 'relation.name')
    },
    listUrlConfig() {
      let { searchText, parentId } = this
      let { relation } = this.widget || {}
      let { fromModuleName, reverseRelationLinkName } = relation || {}
      let params = {
        unAssociated: true,
      }
      if (!isEmpty(searchText)) {
        params.search = searchText
      }
      return {
        url: `v3/modules/${fromModuleName}/${parentId}/relationship/${reverseRelationLinkName}`,
        toModuleName: this.toModuleName,
        params,
      }
    },
  },
  watch: {
    details: {
      async handler(value, oldValue) {
        if (!isEqual(value, oldValue)) {
          this.getFormMeta()
        } else this.loading = false
      },
    },
  },
  methods: {
    setListValues(selectedItems) {
      let selectedId = selectedItems.map(item => {
        let { value } = item || {}
        return value
      })

      this.createRelations(selectedId)
    },
    getFormMeta() {
      let { moduleName, details } = this
      if (isEmpty(moduleName) || isEmpty(details)) {
        this.emptyState = true
        this.loading = false
        return
      }

      this.loading = true
      API.get(`/v3/summary/fields/${this.moduleName}`, {
        formId: details.formId ? details.formId : -1,
        id: details.id,
        widgetParams: this.widget?.widgetParams
          ? JSON.stringify(this.widget?.widgetParams)
          : null,
        fetchMainFields: true,
      }).then(({ error, data }) => {
        if (!error) {
          this.emptyState = false
          this.deserialize(data)
        } else {
          this.emptyState = true
          this.autoResize()
        }
        this.loading = false
      })
    },
    async createRelations(selectedId) {
      this.loading = true

      let { widget, parentId } = this
      let { relation } = widget || {}
      let { fromModuleName, toModuleName, forwardRelationLinkName } =
        relation || {}
      let url = `v3/modules/${fromModuleName}/${parentId}/relationship/${forwardRelationLinkName}`
      let params = { data: { [toModuleName]: selectedId } }
      let { error } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred ')
        this.loading = false
      } else {
        this.$message.success('Relation Added successfully')
        this.$emit('onUpdate')
      }
    },
    openRecordSummary(id) {
      let { widget } = this
      let route
      let isCustomModule = this.$getProperty(widget, 'relation.toModule.custom')

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.toModuleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id: id,
            },
          }).href
        }
      } else {
        if (isCustomModule) {
          route = this.$router.resolve({
            path: `/app/ca/modules/${this.toModuleName}/all/${id}/summary`,
          }).href
        }
      }
      if (route) window.open(route, '_blank')
    },
    async disassociateRecord() {
      let value = await this.$dialog.confirm({
        title: this.$t(`setup.relationship.dissociate`),
        message: this.$t(`setup.relationship.delete_message`),
        rbDanger: true,
        rbLabel: this.$t('setup.relationship.dissociate'),
      })

      if (!value) return

      this.loading = true

      let { widget, parentId, details } = this
      let { relation } = widget || {}
      let { fromModuleName, toModuleName, forwardRelationLinkName } =
        relation || {}
      let url = `v3/modules/${fromModuleName}/${parentId}/relationship/${forwardRelationLinkName}`
      let params = { data: { [toModuleName]: [details?.id] } }
      let { error } = await API.patch(url, params)

      if (error) {
        let { message } = error
        this.$message.error(message || 'Error Occured while deleting')
        this.loading = false
      } else {
        this.$message.success(this.$t(`common._common.delete_success`))
        this.needsShowMore = false
        this.resizeWidget({ h: this.defaultWidgetHeight })
        this.$emit('onUpdate')
      }
    },
    isRotatingItem(field) {
      let { name: fieldName } = field || {}
      let { rotatingItem } = this.details || {}
      let { id: rotatingItemId } = rotatingItem || {}

      return rotatingItemId && fieldName === 'rotatingItem'
    },
    isRotatingTool(field) {
      let { name: fieldName } = field || {}
      let { rotatingTool } = this.details || {}
      let { id: rotatingToolId } = rotatingTool || {}

      return rotatingToolId && fieldName === 'rotatingTool'
    },
  },
}
</script>
<style lang="scss" scoped>
.relationship-summary-widget {
  height: 100%;
  overflow: scroll;

  .relationship-header-container {
    border-bottom: 1px solid #f7f8f9;
    padding: 10px 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 60px;

    .header-title {
      text-transform: capitalize;
      margin-left: 4px;
      line-height: 20px;

      .title-name {
        font-size: 14px;
        letter-spacing: 1px;
        font-weight: 500;
        color: #385571;
      }
      .title-link {
        font-size: 12px;
        color: #808080;
        letter-spacing: 0.3px;
        font-weight: 400;
      }
    }

    .associate-relation,
    .disassociate-relation {
      display: flex;
      align-items: center;
      color: #ff3184;
      cursor: pointer;
      min-height: 40px;

      .associate-text,
      .disassociate-text {
        font-size: 13px;
        font-weight: bold;
        padding-left: 5px;
        line-height: normal;
        letter-spacing: 0.5px;
      }
    }
  }
}
.relationship-details-card {
  height: calc(100% - 61px);
  .asset-details-widget {
    padding-left: 20px !important;
    border-top: 1px solid #f2eeee;
    padding: 10px 30px 0px;
    text-align: left;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
}
.realtion-summary-heading {
  text-transform: capitalize;
}
.lines {
  height: 15px;
  width: 200px;
  margin: 0px 20px 0px 20px;
  border-radius: 5px;
}
.url-field-display {
  color: #46a2bf;
  &:hover {
    text-decoration: underline;
    text-underline-offset: 3px;
    color: #46a2bf;
  }
}
</style>
<style scoped>
.container {
  font-size: 13px;
  display: flex;
  flex-wrap: wrap;
  overflow: hidden;
}
.field {
  flex: 0 50%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
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
