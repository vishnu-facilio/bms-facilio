<template>
  <div
    v-if="loading"
    class="flex-middle fc-empty-white m10 fc-agent-empty-state"
  >
    <spinner :show="loading" size="80"></spinner>
  </div>
  <div v-else>
    <div class="position-relative p20 pT20 overflow-y-scroll break-word">
      <div class="flex-middle justify-content-space mB20">
        <div class="label-txt-black fwBold">
          {{ $t('common.header.Workorder_details') }}
        </div>
        <div
          @click="openWoEditForm"
          v-if="isWorkOrderEditable()"
          class="wo-details-edit-btn"
        >
          <i class="el-icon-edit pR5"></i>
          {{ $t('common._common.edit') }}
        </div>
      </div>

      <!-- Space Field -->
      <div v-if="workorder.space">
        <el-row class="flex-middle pB20">
          <el-col :span="10">
            <div class="fc-blue-label">
              <div>
                {{ $t('maintenance._workorder.raised_for') }}
              </div>
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div v-if="workorder.space.spaceTypeVal === 'Building'">
                <div class="place">
                  {{
                    workorder.space.id
                      ? getSpace(workorder.space.siteId).name
                      : '---'
                  }}
                </div>
              </div>
              <div v-else-if="workorder.space.spaceTypeVal === 'Site'">
                <div style="padding-bottom: 35px" class="place">
                  {{
                    workorder.space.id
                      ? getSpace(workorder.space.id).name
                      : '---'
                  }}
                </div>
              </div>
              <div v-if="workorder.space.spaceTypeVal === 'Building'">
                <div style="padding-bottom: 35px" class="inplace">
                  {{ workorder.space.name }}
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Type Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('type') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              style="font-size: 14px; letter-spacing: 0.6px; color: #333"
              class="flLeft picklist"
            >
              <div
                class="flLeft"
                v-if="workorder.type && workorder.type.id > 0"
              >
                {{ getPMType(workorder.type.id) }}
              </div>
              <div class="flLeft color-d" v-else>---</div>
              <div class="clearboth"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Priority Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('priority') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              style="font-size: 14px; letter-spacing: 0.6px; color: #333"
              class="flLeft picklist"
            >
              <div
                class="flLeft"
                v-if="workorder.priority && workorder.priority.id > 0"
              >
                <span class="q-item-label">
                  <i
                    class="fa fa-circle prioritytag"
                    v-bind:style="{
                      color: workorder.priority.colour,
                    }"
                    aria-hidden="true"
                  ></i>
                  {{
                    workorder.priority && workorder.priority.id > 0
                      ? workorder.priority.displayName
                      : '---'
                  }}
                </span>
              </div>
              <div class="flLeft color-d" v-else>---</div>
              <div class="clearboth"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Category Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('category') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              style="font-size: 14px; letter-spacing: 0.6px; color: #333"
              class="flLeft picklist"
            >
              <div
                class="flLeft"
                v-if="workorder.category && workorder.category.id > 0"
              >
                {{
                  workorder.category ? workorder.category.displayName : '---'
                }}
              </div>
              <div class="flLeft color-d" v-else>---</div>
              <div class="clearboth"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- Client Field -->
      <div v-if="clientLicenseEnabled">
        <el-row class="flex-middle pB20">
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('client') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black visibility-visible-actions">
              <div
                v-if="workorder.client && workorder.client.id > 0"
                class="flLeft"
              >
                {{ workorder.client.name }}
              </div>
              <div v-else class="flLeft">---</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Serivce Request Field -->
      <div v-if="serviceRequestLicenseEnabled">
        <el-row class="flex-middle pB20">
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('serviceRequest') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black visibility-visible-actions">
              <div
                v-if="
                  workorder.serviceRequest && workorder.serviceRequest.id > 0
                "
                class="flLeft bluetxt"
                style="cursor: pointer"
                @click="openServiceRequest(workorder.serviceRequest)"
              >
                #{{ workorder.serviceRequest.id }}
              </div>
              <div v-else class="flLeft">---</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="clearboth"></div>

      <!-- Source Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('sourceType') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black">
            <div
              v-if="workorder.sourceType && workorder.sourceTypeVal"
              class="flLeft"
            >
              {{ workorder.sourceTypeVal }}
            </div>
            <div v-else class="flLeft">---</div>
            <div class="clearboth"></div>
          </div>
        </el-col>
      </el-row>

      <!-- Created Time Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('createdTime') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black">
            <div class="flLeft">
              {{ workorder.createdTime | formatDate() }}
            </div>
            <div class="clearboth"></div>
          </div>
        </el-col>
      </el-row>

      <!-- Created By Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('createdBy') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black">
            <div class="flLeft">
              {{ workorder.createdBy ? workorder.createdBy.name : '---' }}
            </div>
            <div class="clearboth"></div>
          </div>
        </el-col>
      </el-row>

      <!-- Due Date Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('dueDate') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              class="flLeft"
              @click="$refs.showdatepicker.focus()"
              v-if="!$validation.isEmpty(workorder.dueDate)"
            >
              {{ workorder.dueDate | formatDate }}
            </div>
            <div class="flLeft color-d" v-else>---</div>
            <div class="clearboth"></div>
          </div>
        </el-col>
      </el-row>

      <!-- Response Due Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('responseDueDate') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              class="flLeft"
              v-if="!$validation.isEmpty(workorder.responseDueDate)"
            >
              {{ workorder.responseDueDate | formatDate }}
            </div>
            <div class="flLeft color-d" v-else>---</div>
            <div class="clearboth"></div>
          </div>
        </el-col>
      </el-row>

      <!-- Source Type Due Field -->
      <!-- PM, v2 PM -->
      <div v-if="sourceIsPMOrImport(workorder.sourceType)">
        <el-row
          class="flex-middle pB20"
          v-if="workorder && !$validation.isEmpty(workorder.pmV2)"
        >
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('pmV2') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div
                class="flLeft"
                :class="
                  isRouteSet('plannedmaintenance')
                    ? 'bluetxt pmv2-tag cursor-point'
                    : ''
                "
                @click="openPM"
              >
                {{ workorder.pmV2 ? '#' + workorder.pmV2 : '---' }}
              </div>
              <div class="clearboth"></div>
            </div>
          </el-col>
        </el-row>
        <el-row class="flex-middle pB20" v-else>
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('pm') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div
                class="flLeft"
                :class="
                  isRouteSet('preventivemaintenance')
                    ? 'bluetxt pmv2-tag cursor-point '
                    : ''
                "
                @click="openPM"
              >
                {{ workorder.pm ? '#' + workorder.pm.id : '---' }}
              </div>
              <div class="clearboth"></div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- JobPlan Field -->
      <div v-if="workorder && !$validation.isEmpty(workorder.jobPlan)">
        <el-row class="flex-middle pB20">
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('jobPlan') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div
                class="flLeft"
                :class="
                  isRouteSet('jobplan') ? 'bluetxt pmv2-tag cursor-point ' : ''
                "
                @click="openJobPlan"
              >
                {{ getJobPlanTag(workorder.jobPlan) }}
              </div>
              <div class="clearboth"></div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Parent WO Field -->
      <div v-if="workorder.parentWO">
        <el-row class="flex-middle pB20">
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('parentWO') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div
                class="flLeft"
                :class="
                  isRouteSet('workorder')
                    ? 'bluetxt pmv2-tag cursor-point '
                    : ''
                "
                @click="openParentWo(workorder.parentWO)"
              >
                {{
                  workorder.parentWO && workorder.parentWO.id
                    ? '#' + workorder.parentWO.id
                    : '---'
                }}
              </div>
              <div class="clearboth"></div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Safety Plan Due Field -->
      <div
        v-if="
          workorder.safetyPlan && this.$helpers.isLicenseEnabled('SAFETY_PLAN')
        "
      >
        <el-row class="wo__details__res">
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('safetyPlan') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div class="flLeft bluetxt">
                {{
                  workorder.safetyPlan && workorder.safetyPlan.name
                    ? workorder.safetyPlan.name
                    : '---'
                }}
              </div>
              <div class="clearboth"></div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- StateFlow Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('stateFlowId') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              v-if="
                workorder.stateFlowId &&
                  !$validation.isEmpty(workorder.stateFlowRule) &&
                  !$validation.isEmpty(workorder.stateFlowRule.name)
              "
              class="flLeft"
            >
              {{ workorder.stateFlowRule.name }}
            </div>
            <div v-else class="flLeft">---</div>
          </div>
        </el-col>
      </el-row>

      <!-- SLA  Field -->
      <el-row class="flex-middle pB20">
        <el-col :span="10">
          <div class="fc-blue-label">
            {{ getFieldDisplayName('slaPolicyId') }}
          </div>
        </el-col>
        <el-col :span="14">
          <div class="label-txt-black visibility-visible-actions">
            <div
              v-if="
                workorder.slaPolicyId > 0 &&
                  !$validation.isEmpty(workorder.slaRule) &&
                  !$validation.isEmpty(workorder.slaRule.name)
              "
              class="flLeft"
            >
              {{ workorder.slaRule.name }}
            </div>
            <div v-else class="flLeft">---</div>
          </div>
        </el-col>
      </el-row>

      <!-- Route Field -->
      <div v-if="workorder && !$validation.isEmpty(workorder.route)">
        <el-row class="flex-middle pB20">
          <el-col :span="10">
            <div class="fc-blue-label">
              {{ getFieldDisplayName('route') }}
            </div>
          </el-col>
          <el-col :span="14">
            <div class="label-txt-black">
              <div
                class="flLeft bluetxt cursor-point clearboth"
                @click="openRoute"
              >
                {{ '#' + $getProperty(workorder, 'route.id') }}
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- rendering other fields -->
      <template v-for="(field, index) in fields">
        <div
          v-if="!field.default && workorder && !isFieldEmpty(field, workorder)"
          :key="index"
        >
          <el-row class="pB20 flex-middle">
            <el-col :span="10">
              <div class="fc-blue-label line-height20">
                {{ field.displayName }}
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="label-txt-black visibility-visible-actions flex-middle"
              >
                <!-- date type -->
                <div
                  v-if="
                    field.dataTypeEnum === 'DATE' ||
                      field.dataTypeEnum._name === 'DATE'
                  "
                >
                  {{ workorder[field.name] | formatDate() }}
                </div>
                <!-- date time type -->
                <div
                  v-else-if="
                    field.dataTypeEnum === 'DATE_TIME' ||
                      field.dataTypeEnum._name === 'DATE_TIME'
                  "
                >
                  {{ workorder[field.name] | formatDate() }}
                </div>
                <!-- enum type -->
                <div
                  style="font-size: 14px; letter-spacing: 0.6px; color: #333"
                  class="picklist"
                  v-else-if="
                    field.dataTypeEnum === 'ENUM' ||
                      field.dataTypeEnum._name === 'ENUM'
                  "
                >
                  <div
                    class="flLeft"
                    :class="(workorder || {})[field.name] ? '' : 'color-d'"
                  >
                    {{
                      (workorder || {})[field.name]
                        ? field.enumMap[parseInt((workorder || {})[field.name])]
                        : '---'
                    }}
                  </div>
                </div>
                <!-- multi-enum type -->
                <div
                  style="font-size: 14px; letter-spacing: 0.6px; color: #333"
                  class="picklist overflow-hidden"
                  v-else-if="
                    field.dataTypeEnum === 'MULTI_ENUM' ||
                      field.dataTypeEnum._name === 'MULTI_ENUM'
                  "
                >
                  <div :class="(workorder || {})[field.name] ? '' : 'color-d'">
                    {{
                      getMultiEnumFieldValues({
                        field,
                        data: workorder,
                      })
                    }}
                  </div>
                </div>
                <!-- boolean type -->
                <div
                  style="font-size: 14px; letter-spacing: 0.6px; color: #333"
                  class="picklist"
                  v-else-if="
                    field.dataTypeEnum === 'BOOLEAN' ||
                      field.dataTypeEnum._name === 'BOOLEAN'
                  "
                >
                  <div
                    class="flLeft"
                    :class="
                      $validation.isBoolean((workorder || {})[field.name])
                        ? ''
                        : 'color-d'
                    "
                  >
                    {{
                      $validation.isBoolean((workorder || {})[field.name])
                        ? (workorder || {})[field.name]
                          ? field.trueVal || 'True'
                          : field.falseVal || 'False'
                        : '---'
                    }}
                  </div>
                </div>
                <!-- number type -->
                <div
                  v-else-if="
                    (field.dataTypeEnum === 'NUMBER' ||
                      field.dataTypeEnum._name === 'NUMBER') &&
                      field.displayTypeInt === 23
                  "
                >
                  {{
                    $helpers.getFormattedDuration(
                      workorder[field.name],
                      !$validation.isEmpty(field.unit) ? field.unit : 's'
                    )
                  }}
                </div>
                <!-- time type -->
                <div v-else-if="$getProperty(field, 'displayType') === 'TIME'">
                  <span>{{ getFormatedTime(workorder[field.name]) }}</span>
                </div>
                <!-- signature type -->
                <div
                  v-else-if="$getProperty(field, 'displayType') === 'SIGNATURE'"
                >
                  <SignatureField :field="field" :record="workorder" />
                </div>
                <!-- file type -->
                <template v-else-if="isFileTypeField(field)">
                  <div
                    v-if="
                      !$validation.isEmpty(workorder[`${field.name}FileName`])
                    "
                    @click="openAttachment(field, workorder || {})"
                    class="d-flex file-column width100"
                  >
                    <el-tooltip
                      effect="dark"
                      :content="workorder[`${field.name}FileName`]"
                      placement="top-start"
                      :open-delay="1000"
                    >
                      <a class="truncate-text">
                        {{ workorder[`${field.name}FileName`] }}
                      </a>
                    </el-tooltip>
                  </div>
                  <div v-else>---</div>
                </template>
                <!-- url type -->
                <div v-else-if="field.displayType === 'URL_FIELD'">
                  <div
                    v-if="!$validation.isEmpty(workorder[field.name])"
                    class="d-flex"
                  >
                    <a
                      rel="nofollow"
                      class="url-field-display"
                      :href="$getProperty(workorder[field.name], 'href', '')"
                      :target="
                        $getProperty(workorder[field.name], 'target', '')
                      "
                      referrerpolicy="no-referrer"
                      >{{ getUrlDisplayName(workorder[field.name]) }}</a
                    >
                  </div>
                  <span v-else>---</span>
                </div>
                <div v-else-if="canShowRichTextContent(field)">
                  <div class="d-flex ">
                    <fc-icon
                      group="default"
                      name="eye-open"
                      color="#2b8bff"
                    ></fc-icon>
                    <span
                      class="mL7 mT2 view-rich-text"
                      @click="openRichTextPreview(field)"
                    >
                      {{ $t('common._common.view') }}</span
                    >
                  </div>
                </div>
                <!-- lookup type -->
                <div v-else-if="canShowLookupField(field)">
                  <div
                    v-if="
                      $getProperty(field, 'lookupModule.name', '') ===
                        'users' && $getProperty(workorder, `${field.name}.id`)
                    "
                  >
                    {{ $store.getters.getUser(workorder[field.name].id).name }}
                  </div>
                  <div v-else-if="isLookupSimpleField(field)">
                    <GlimpseLookupWrapper
                      :field="getFieldObj(field)"
                      :record="workorder"
                      :siteList="sites"
                      :recordModuleName="moduleName"
                    ></GlimpseLookupWrapper>
                  </div>

                  <div v-else-if="isLookupLocationField(field)">
                    <div class="bluetxt pointer" @click="redirectToMap(field)">
                      {{ handleLocationFieldValue(field) }}
                    </div>
                    <!-- <div
                      v-if="
                        !$validation.isEmpty(
                          getDisplayValue(field, workorder[field.name])
                        )
                      "
                      class="bluetxt pointer"
                      @click="
                        $helpers.openInMapForLatLngStr(
                          getDisplayValue(field, workorder[field.name])
                        )
                      "
                    >
                      {{ `@${getDisplayValue(field, workorder[field.name])}` }}
                    </div> -->
                    <!-- <span v-else>---</span> -->
                  </div>
                </div>
                <!-- mutli-lookup type -->
                <div
                  v-else-if="
                    !$validation.isEmpty(workorder[field.name]) &&
                      field.dataTypeEnum === 'MULTI_LOOKUP'
                  "
                >
                  {{ getMultiLookupValue(workorder[field.name]) }}
                </div>
                <div
                  v-else-if="
                    !$validation.isEmpty(workorder[field.name]) &&
                      field.dataTypeEnum === 'CURRENCY_FIELD'
                  "
                  class="flex-middle"
                >
                  <span>{{
                    getCurrencyFieldValue(workorder[field.name])
                  }}</span>
                  <el-popover
                    v-if="showInfoIcon(workorder[field.name])"
                    v-model="field['showPopOver']"
                    placement="left"
                    :title="$t('setup.currency.rate')"
                    width="200"
                    trigger="click"
                  >
                    <div
                      class="currency-conversion-rate"
                      @click="field['showPopOver'] = false"
                    >
                      <fc-icon
                        group="default"
                        name="close"
                        size="15"
                        class="pointer rate-svg-close"
                      ></fc-icon>
                      <span class="currency-desc">
                        {{
                          `${getCurrencyFieldValue(
                            workorder[field.name],
                            true
                          )}`
                        }}
                      </span>
                    </div>
                    <fc-icon
                      slot="reference"
                      group="dsm"
                      class="pointer info-position mL2 flex-middle"
                      name="info"
                      size="14"
                    ></fc-icon>
                  </el-popover>
                </div>
                <div
                  v-else-if="
                    !$validation.isEmpty(workorder[field.name]) &&
                      field.dataTypeEnum === 'MULTI_CURRENCY_FIELD'
                  "
                  class="flex-middle"
                >
                  <CurrencyPopOver
                    :field="field"
                    :details="workorder"
                    :showInfo="true"
                  />
                </div>
                <div
                  v-else-if="
                    workorder && !$validation.isEmpty(workorder[field.name])
                  "
                >
                  {{ workorder[field.name] }}
                </div>
                <div v-else>---</div>
                <div class="clearboth"></div>
              </div>
            </el-col>
          </el-row>
        </div>
      </template>
    </div>
    <!-- workorder details section end -->
    <WoV3EditForm
      v-if="editFormVisibilty"
      moduleName="workorder"
      :visibility="editFormVisibilty"
      :wo="workorder"
      @visibilityUpdate="handleUpdateVisibility"
    >
    </WoV3EditForm>
    <preview-file
      :visibility.sync="showPreview"
      v-if="showPreview && selectedFile"
      :previewFile="selectedFile"
      :files="[selectedFile]"
    ></preview-file>
    <RichTextPreview
      v-if="showRichTextPreview"
      :showRichTextPreview="showRichTextPreview"
      :richTextField="richTextField"
      :richTextContent="richTextContent"
      @closeRichTextPreview="closeRichTextPreview"
    />
  </div>
</template>
<script>
import WoV3EditForm from 'src/pages/workorder/workorders/v3/WoV3EditForm.vue'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import utils from 'pages/workorder/workorders/v3/mixins/utils'
import { isLookupField, isFileTypeField } from '@facilio/utils/field'
import PreviewFile from '@/PreviewFile'
import SignatureField from '@/list/SignatureColumn'
import { mapGetters } from 'vuex'
import { getFormatedTime } from '@/mixins/TimeFormatMixin.js'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import RichTextPreview from 'pages/workorder/workorders/v1/RichTextPreviewDialog.vue'
import GlimpseLookupWrapper from 'src/newapp/components/GlimpseLookupWrapper.vue'
import { mapGettersWithLogging } from 'store/utils/log-map-getters'
import { getDisplayValue, isGeoLocationField } from 'util/field-utils'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

export default {
  name: 'WorkorderDetails',
  components: {
    SignatureField,
    WoV3EditForm,
    PreviewFile,
    RichTextPreview,
    GlimpseLookupWrapper,
    CurrencyPopOver,
  },
  props: ['moduleName', 'details'],
  mixins: [workorderMixin, utils],
  data() {
    return {
      editFormVisibilty: false,
      fields: [],
      loading: false,
      selectedFile: null,
      showPreview: false,
      richTextContent: null,
      richTextField: null,
      showRichTextPreview: false,
    }
  },
  created() {
    this.isGeoLocationField = isGeoLocationField
    this.getDisplayValue = getDisplayValue
  },
  mounted() {
    this.loadFields()
    this.$root.$on('editWorkorder', () => {
      this.openWoEditForm()
    })
    this.isFileTypeField = isFileTypeField
  },
  computed: {
    ...mapGetters([
      'getTicketPriority',
      'getTicketCategory',
      'getApprovalStatus',
      'isStatusLocked',
      'getUser',
    ]),
    ...mapGettersWithLogging(['getSpace']),
    workorder() {
      return this.details.workorder
    },
    isRequestedState() {
      let { workorder } = this
      let { approvalStatus } = workorder || {}
      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    clientLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('CLIENT')
    },
    serviceRequestLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('SERVICE_REQUEST')
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.$getProperty(this, '$account.data.currencyInfo') || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
  },
  methods: {
    canShowRichTextContent(field) {
      let { displayType } = field || {}
      let richTextContent = this.getRichTextContent(field)

      return !isEmpty(richTextContent) && displayType === 'RICH_TEXT'
    },
    getRichTextContent(field) {
      let { workorder } = this
      let { name } = field || {}

      return this.$getProperty(workorder, name, '')
    },
    openRichTextPreview(field) {
      this.richTextContent = this.getRichTextContent(field)
      this.richTextField = field
      this.showRichTextPreview = true
    },
    closeRichTextPreview() {
      this.richTextContent = null
      this.richTextField = null
      this.showRichTextPreview = false
    },
    handleUpdateVisibility(data) {
      this.editFormVisibilty = data
    },
    openParentWo(parentWo) {
      let { id } = parentWo || {}
      if (!id) {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({
            name,
            params: {
              id,
            },
          })
        }
      } else {
        this.$router.push({ path: '/app/wo/orders/summary/' + id })
      }
    },
    openServiceRequest(serviceRequest) {
      let { id } = serviceRequest || {}
      if (!id) {
        return
      }
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('serviceRequest', pageTypes.OVERVIEW) || {}
        if (name) {
          this.$router.push({
            name,
            params: {
              id,
            },
          })
        }
      } else {
        this.$router.replace({
          path: '/app/sr/serviceRequest/all/' + id + '/overview',
        })
      }
    },
    sourceIsPMOrImport(sourceType) {
      if (!sourceType) {
        return false
      }
      const PM = 5
      const IMPORT = 14
      return [PM, IMPORT].includes(sourceType)
    },
    getMultiEnumFieldValues(props) {
      let { field, data = {} } = props
      let { name, enumMap } = field
      let values = data[name] || []
      let valueStr = values.reduce((accStr, value) => {
        let str = enumMap[value] || ''
        return isEmpty(accStr) ? `${str}` : `${accStr}, ${str}`
      }, '')
      return isEmpty(valueStr) ? '---' : valueStr
    },
    getMultiLookupValue(value) {
      let lookupRecordNames = (value || []).map(
        currRecord =>
          currRecord.displayName || currRecord.name || currRecord.subject
      )
      if (lookupRecordNames.length > 5) {
        return `${lookupRecordNames.slice(0, 5).join(', ')} +${Math.abs(
          lookupRecordNames.length - 5
        )}`
      } else {
        return !isEmpty(lookupRecordNames)
          ? `${lookupRecordNames.join(', ')}`
          : '---'
      }
    },
    redirectToSpecialModule(field, data) {
      if (isEmpty(field) || isEmpty(data)) return

      let { name: fieldName, lookupModule = {} } = field

      if (isEmpty(lookupModule)) return

      let { name } = lookupModule

      if (isWebTabsEnabled()) {
        let { name: routeName } =
          findRouteForModule(name, pageTypes.OVERVIEW) || {}

        if (routeName) {
          const lookupObject = data[fieldName]
          const lookupID = lookupObject.id
          this.$router.push({
            name: routeName,
            params: {
              id: lookupID,
            },
          })
        }
      } else {
        let urlHash = {
          vendors: {
            name: 'vendorsummary',
            params: {
              viewname: 'all',
              vendorid: this.$getProperty(data, 'vendors.id', null),
            },
          },
          custom: {
            name: 'custommodules-summary',
            params: {
              viewName: 'all',
              id: data[fieldName].id,
              moduleName: name,
            },
          },
          inspectionResponse: {
            name: 'individualInspectionSummary',
            params: {
              viewname: 'all',
              id: this.$getProperty(data, `${fieldName}.id`, null),
            },
          },
          inductionResponse: {
            name: 'individualInductionList',
            params: {
              viewname: 'all',
              id: this.$getProperty(data, `${fieldName}.id`, null),
            },
          },
        }
        let routerParams = {}

        if (name === 'vendors') routerParams = urlHash['vendors']
        else if (lookupModule.typeEnum === 'CUSTOM')
          routerParams = urlHash['custom']
        else if (Object.keys(urlHash).includes(name))
          routerParams = urlHash[name]

        if (!isEmpty(routerParams)) {
          this.$router.push(routerParams)
        }
      }
    },
    canShowLookupField(field) {
      return !isEmpty(field) ? isLookupField(field) : false
    },
    isFieldEmpty(field, workorderData) {
      let { name, displayType } = field
      let value = workorderData[name]

      if (displayType === 'SIGNATURE' || isFileTypeField(field)) {
        value = workorderData[`${name}Id`]
      }
      return isEmpty(value)
    },
    refreshWorkorder() {
      API.fetchRecord('workorder', {
        id: this.details.workorder.id,
      }).then(({ workorder, error }) => {
        if (!error) {
          this.workorder = workorder
          this.$root.$emit('reloadWO', { wo: workorder })
        }
      })
    },
    getPMType(type) {
      return this.$store.getters.getTicketTypePickList()[type]
    },
    openWoEditForm() {
      this.editFormVisibilty = true
    },
    openPM() {
      let pmV2 = this.$getProperty(this, 'workorder.pmV2')
      let pmV1 = this.$getProperty(this, 'workorder.pm')

      if (isWebTabsEnabled()) {
        let moduleName = !isEmpty(pmV2)
          ? 'plannedmaintenance'
          : 'preventivemaintenance'
        let recordId = !isEmpty(pmV2) ? pmV2 : pmV1?.id
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id: recordId,
            },
          })
        }
      } else {
        let { id, pmCreationTypeEnum } = pmV1 || {}
        if (this.$helpers.isLicenseEnabled('PM_PLANNER') && !isEmpty(pmV2)) {
          this.$router.replace({
            path: '/app/wo/pm/summary/' + pmV2,
          })
        } else if (
          this.$helpers.isLicenseEnabled('MULTISITEPM') &&
          !isEmpty(pmV1) &&
          ['MULTI_SITE'].includes(pmCreationTypeEnum)
        ) {
          this.$router.replace({
            path: '/app/wo/planned/multisummary/' + id,
          })
        } else if (
          !isEmpty(pmV1) &&
          ['SINGLE', 'MULTIPLE'].includes(pmCreationTypeEnum)
        ) {
          this.$router.replace({
            path: '/app/wo/planned/summary/' + id,
          })
        } else {
          this.$message({
            message: this.$t(
              'maintenance._workorder.cannot_navigate_to_preventive_maintenance'
            ),
            type: 'error',
          })
        }
      }
    },
    openJobPlan() {
      let { workorder } = this
      let { jobPlan } = workorder || {}
      let { group, jobPlanVersion: version } = jobPlan || {}
      let groupId = this.$getProperty(group, 'id', null)
      let route = {}

      version = `v${version}`
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('jobplan', pageTypes.OVERVIEW) || {}
        if (name) {
          route = {
            name,
            params: {
              viewname: 'all',
              id: groupId,
            },
            query: { version },
          }
        }
      } else {
        route = {
          name: 'jobPlanSummary',
          params: { moduleName: 'jobplan', viewname: 'all', id: groupId },
          query: { version },
        }
      }
      if (!isEmpty(route)) {
        this.$router.push(route)
      }
    },
    openRoute() {
      let { workorder, $router } = this
      let { route } = workorder || {}
      let { id } = route || {}

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('routes', pageTypes.OVERVIEW) || {}
        if (name) {
          $router.push({
            name,
            params: {
              viewname: 'all',
              id,
            },
          })
        }
      } else {
        $router.replace({
          path: '/app/planning/routes/all/' + id + '/summary',
        })
      }
    },
    getFieldDisplayName(fieldName) {
      let { fields } = this
      let { displayName } = fields.find(f => f.name === fieldName) || {}
      return displayName || ''
    },
    loadFields() {
      this.loading = true
      this.$util.loadModuleMeta('workorder').then(meta => {
        this.fields = meta.fields || []
        this.loading = false
      })
    },
    refreshWoDetails() {
      this.refreshWorkorder()
    },
    getUrlDisplayName(field) {
      let { name, href } = field || {}

      if (name) {
        return name
      } else {
        return href
      }
    },
    openAttachment(field, workorder) {
      let { name } = field || {}
      this.selectedFile = {
        contentType: workorder[`${name}ContentType`],
        fileName: workorder[`${name}FileName`],
        downloadUrl: workorder[`${name}DownloadUrl`],
        previewUrl: workorder[`${name}Url`],
      }
      this.showPreview = true
    },
    getFormatedTime(value) {
      return getFormatedTime(value)
    },
    getCurrencyFieldValue(value, rate = false) {
      let {
        displaySymbol: baseSymbol,
        currencyCode: baseCode,
        multiCurrencyEnabled,
      } = this.multiCurrency || {}

      let {
        displaySymbol,
        currencyValue,
        baseCurrencyValue,
        exchangeRate,
        currencyCode,
      } = value || {}
      let baseValue = ''
      if (multiCurrencyEnabled && baseCode !== currencyCode)
        baseValue = `( ${baseSymbol} ${baseCurrencyValue} )`
      return rate
        ? `${baseSymbol} 1 = ${displaySymbol} ${exchangeRate}`
        : `${displaySymbol} ${currencyValue} ${baseValue}`
    },
    showInfoIcon(value) {
      let { currencyCode: baseCode, multiCurrencyEnabled } =
        this.multiCurrency || {}
      let { currencyCode } = value || {}
      return multiCurrencyEnabled && baseCode !== currencyCode
    },
    getFieldObj(field) {
      let { name } = field || {}
      return { name, field }
    },
    getJobPlanTag(jobPlan) {
      let { group, jobPlanVersion } = jobPlan || {}
      let { id } = group || {}

      return `#${id} / v${jobPlanVersion}`
    },
    getLocationFieldValue(field) {
      let { name } = field || {}
      let fieldValue = this.$getProperty(this.workorder, name)

      return getDisplayValue(field, fieldValue)
    },
    handleLocationFieldValue(field) {
      let locationDisplayValue = this.getLocationFieldValue(field)
      return !isEmpty(locationDisplayValue) ? `@${locationDisplayValue}` : '---'
    },
    redirectToMap(field) {
      let locationDisplayValue = this.getLocationFieldValue(field)
      this.$helpers.openInMapForLatLngStr(locationDisplayValue)
    },
    isRouteSet(moduleName) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
        if (!isEmpty(name)) {
          return true
        } else {
          return false
        }
      } else {
        return true
      }
    },
  },
}
</script>

<style scoped lang="scss">
.view-rich-text {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #2b8bff;
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
}
.cursor-point {
  cursor: pointer;
}
.rate-svg-close {
  position: absolute;
  right: 11px;
  top: 13px;
}
.pmv2-tag {
  &:hover {
    text-decoration: underline;
  }
}
</style>
