<template>
  <div
    :class="[
      !isDisableForApproval
        ? 'fR pm-summary-inner-right-con width28 height100 scrollbar-style'
        : 'wo-sidebar-margin',
      !isNotPortal && 'portal-wo-scroll',
    ]"
  >
    <div
      :class="[
        'fc-scrollbar-wrap',
        {
          pT20: !isDisableForApproval && !forV3,
          mT10: isDisableForApproval && !forV3,
        },
      ]"
    >
      <div
        v-if="!isDisableForApproval && $org.id !== 297"
        class="white-bg-block mb fc__wo__sum__timer pB20 pT20"
      >
        <div class="fc-id11 text-uppercase pL15">
          {{ $t('maintenance._workorder.work_duration') }}
        </div>
        <wo-timer
          class="alarm-timer wo-duration"
          :loading="loading"
          :actualWorkDurationField="actualWorkDurationField"
        ></wo-timer>
      </div>
      <div
        class="white-bg-block mT20 p20"
        v-if="
          !$helpers.isLicenseEnabled('QUOTATION') &&
            $helpers.isLicenseEnabled('INVENTORY') &&
            !isDisableForApproval
        "
      >
        <div class="fc-id11 text-uppercase">
          {{ $t('maintenance._workorder.total_cost') }}
        </div>
        <div class="fc-black-17 pT5">
          <span
            v-if="$currency === '$' || $currency === '₹'"
            v-html="recordCurrency || $currency"
            class="f15"
          ></span>
          {{
            workorder.totalCost > -1 ? $d3.format(',')(workorder.totalCost) : 0
          }}
          <span
            class="f15"
            v-if="$currency !== '$' && $currency !== '₹'"
            v-html="recordCurrency || $currency"
          ></span>
        </div>
      </div>

      <WOSummaryResourceCard
        :record="workorder"
        class="mT20"
        moduleName="workorder"
      ></WOSummaryResourceCard>

      <WOQuotationCard
        v-if="$helpers.isLicenseEnabled('QUOTATION')"
        :workorder="workorder"
      ></WOQuotationCard>

      <div v-if="isTenantEnabled && !canHideField('tenant')">
        <div
          v-if="workorder.tenant && workorder.tenant.id > 0"
          class="white-bg-block mT20 p20 wo-tenant-card text-left"
        >
          <div class="fc-id11 text-uppercase text-left">
            {{ $t('maintenance.wr_list.tenant') }}
          </div>
          <div
            class="pointer pT5 text-left"
            @click="openTenant(workorder.tenant.id)"
          >
            <el-row class="flex-middle">
              <el-col :span="2">
                <tenant-avatar
                  :name="false"
                  size="md"
                  :tenant="workorder.tenant"
                ></tenant-avatar>
              </el-col>
              <el-col :span="22" class="pT3 pL10">
                <div class="label-txt-black bold line-height20">
                  {{ workorder.tenant.name }}
                </div>
                <div class="fc-black-13 text-left line-height20">
                  {{ workorder.tenant.primaryContactName }}
                </div>
                <div class="fc-black-13 text-left line-height20">
                  {{ workorder.tenant.primaryContactEmail }}
                </div>
                <div class="pT5 fc-black-13 text-left">
                  {{ workorder.tenant.primaryContactPhone }}
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <!-- workorder details section start-->
      <!-- team and staff start-->

      <div class="white-bg-block mT20 p20 mB20">
        <div class="fc-id11 text-uppercase text-left">
          {{ $t('maintenance.wr_list.responsibility') }}
        </div>
        <div
          class="fc-text-pink11 text-uppercase text-uppercase text-left pT10 pB10 f10"
        >
          {{ $t('maintenance.wr_list.staff/team') }}
        </div>
        <el-row class="flex-middle pB20">
          <el-col :span="24">
            <div class="label-txt-black visibility-visible-actions">
              <div
                class="flLeft width80"
                v-if="
                  (workorder.assignedTo && workorder.assignedTo.id > 0) ||
                    (workorder.assignmentGroup &&
                      workorder.assignmentGroup.id > 0)
                "
              >
                <user-avatar
                  size="md"
                  :user="workorder.assignedTo"
                  :group="workorder.assignmentGroup"
                  :showPopover="true"
                  :showLabel="true"
                  moduleName="workorder"
                ></user-avatar>
              </div>
              <div class="flLeft color-d" v-else>--- / ---</div>
            </div>
          </el-col>
        </el-row>

        <!-- vendor -->

        <div v-if="!canHideField('vendor')">
          <div class="fc-text-pink11 text-uppercase text-left f10">
            {{ $t('maintenance.wr_list.vendor') }}
          </div>
          <el-row class="flex-middle pB20">
            <el-col :span="24">
              <div class="label-txt-black mT10">
                <div class="flLeft" :class="workorder.vendor ? '' : 'color-d'">
                  {{
                    workorder.vendor && workorder.vendor.name
                      ? workorder.vendor.name
                      : '---'
                  }}
                </div>
                <div class="clearboth"></div>
              </div>
            </el-col>
          </el-row>

          <div>
            <div class="fc-text-pink11 text-uppercase text-left f10">
              {{ $t('common.products.requester') }}
            </div>
            <el-row class="flex-middle pB20">
              <el-col :span="24">
                <div class="label-txt-black visibility-visible-actions">
                  <div
                    v-if="workorder.requester && workorder.requester.name"
                    class="pT10"
                  >
                    {{ workorder.requester.name }}
                  </div>
                  <div v-else class="pT10">---</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <!-- team and staff end -->
      <!-- WO Field Details -->
      <div :class="[!isDisableForApproval && $org.id !== 297 ? 'mT20' : '']">
        <div
          :class="[
            {
              'white-bg-block position-relative p20 pT20': !isDisableForApproval,
            },
          ]"
          style="overflow: scroll;"
          class="approval-wo-fields-con"
        >
          <div
            v-if="!isDisableForApproval"
            class="flex-middle justify-content-space mB20"
          >
            <div class="label-txt-black fwBold">
              {{ $t('common.header.Workorder_details') }}
            </div>
            <div
              @click="openWoEditForm"
              v-if="canEdit"
              class="wo-details-edit-btn"
            >
              <i class="el-icon-edit pR5"></i>
              {{ $t('common._common.edit') }}
            </div>
          </div>
          <div v-if="workorder.space && !canHideField('space')">
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
                    <div style="padding-bottom: 35px;" class="place">
                      {{
                        workorder.space.id
                          ? getSpace(workorder.space.id).name
                          : '---'
                      }}
                    </div>
                  </div>
                  <div v-if="workorder.space.spaceTypeVal === 'Building'">
                    <div style="padding-bottom: 35px;" class="inplace">
                      {{ workorder.space.name }}
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <el-row v-if="!canHideField('type')" class="flex-middle pB20">
            <el-col :span="10">
              <div class="fc-blue-label">
                {{ getFieldDisplayName('type') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div class="label-txt-black visibility-visible-actions">
                <div
                  style="font-size: 14px; letter-spacing: 0.6px; color: #333;"
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

          <el-row class="flex-middle pB20">
            <el-col :span="10">
              <div class="fc-blue-label">
                {{ getFieldDisplayName('priority') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div class="label-txt-black visibility-visible-actions">
                <div
                  style="font-size: 14px; letter-spacing: 0.6px; color: #333;"
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
                          color: getTicketPriority(workorder.priority.id)
                            .colour,
                        }"
                        aria-hidden="true"
                      ></i>
                      {{
                        workorder.priority && workorder.priority.id > 0
                          ? getTicketPriority(workorder.priority.id).displayName
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

          <el-row class="flex-middle pB20">
            <el-col :span="10">
              <div class="fc-blue-label">
                {{ getFieldDisplayName('category') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div class="label-txt-black visibility-visible-actions">
                <div
                  style="font-size: 14px; letter-spacing: 0.6px; color: #333;"
                  class="flLeft picklist"
                >
                  <div
                    class="flLeft"
                    v-if="workorder.category && workorder.category.id > 0"
                  >
                    {{
                      (getTicketCategory(workorder.category.id) || {})
                        .displayName || '---'
                    }}
                  </div>
                  <div class="flLeft color-d" v-else>---</div>
                  <div class="clearboth"></div>
                </div>
              </div>
            </el-col>
          </el-row>

          <div v-if="isClientEnabled">
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

          <div class="clearboth"></div>

          <el-row
            v-if="!canHideField('sourceTypeVal')"
            class="flex-middle pB20"
          >
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

          <el-row v-if="!canHideField('createdTime')" class="flex-middle pB20">
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
                  v-if="workorder.dueDate !== -1"
                >
                  {{ workorder.dueDate | formatDate() }}
                </div>
                <div class="flLeft color-d" v-else>
                  ---
                </div>
                <div class="clearboth"></div>
              </div>
            </el-col>
          </el-row>

          <el-row
            v-if="!canHideField('responseDueDate')"
            class="wo__details__res"
          >
            <el-col :span="10">
              <div class="fc-blue-label">
                {{ getFieldDisplayName('responseDueDate') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div class="label-txt-black visibility-visible-actions">
                <div class="flLeft" v-if="workorder.responseDueDate !== -1">
                  {{ workorder.responseDueDate | formatDate }}
                </div>
                <div class="flLeft color-d" v-else>---</div>
                <div class="clearboth"></div>
              </div>
            </el-col>
          </el-row>

          <div
            v-if="
              [5, 14].includes(workorder.sourceType) &&
                !canHideField('sourceType')
            "
          >
            <el-row class="flex-middle pB20">
              <el-col :span="10">
                <div class="fc-blue-label">
                  {{ getFieldDisplayName('pm') }}
                </div>
              </el-col>
              <el-col :span="14">
                <div class="label-txt-black">
                  <div
                    class="flLeft bluetxt"
                    style="cursor: pointer;"
                    @click="openPM"
                  >
                    {{ workorder.pm ? '#' + workorder.pm.id : '---' }}
                  </div>
                  <div class="clearboth"></div>
                </div>
              </el-col>
            </el-row>
          </div>

          <div v-if="workorder.parentWO && !canHideField('parentWO')">
            <el-row class="flex-middle pB20">
              <el-col :span="10">
                <div class="fc-blue-label">
                  {{ getFieldDisplayName('parentWO') }}
                </div>
              </el-col>
              <el-col :span="14">
                <div class="label-txt-black">
                  <div
                    class="flLeft bluetxt"
                    style="cursor: pointer;"
                    @click="openParentWo(workorder.parentWO)"
                  >
                    {{
                      workorder.parentWO && workorder.parentWO.serialNumber
                        ? '#' + workorder.parentWO.serialNumber
                        : '---'
                    }}
                  </div>
                  <div class="clearboth"></div>
                </div>
              </el-col>
            </el-row>
          </div>

          <div
            v-if="
              workorder.safetyPlan &&
                this.$helpers.isLicenseEnabled('SAFETY_PLAN') &&
                !canHideField('safetyPlan')
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

          <template v-for="(field, index) in filteredFields">
            <div
              v-if="
                !field.default &&
                  workorder.data &&
                  !isFieldEmpty(field, workorder.data)
              "
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
                    <div
                      v-if="
                        field.dataTypeEnum === 'DATE' ||
                          field.dataTypeEnum._name === 'DATE'
                      "
                    >
                      {{ workorder.data[field.name] | formatDate() }}
                    </div>
                    <div
                      v-else-if="
                        field.dataTypeEnum === 'DATE_TIME' ||
                          field.dataTypeEnum._name === 'DATE_TIME'
                      "
                    >
                      {{ workorder.data[field.name] | formatDate() }}
                    </div>
                    <div
                      style="
                        font-size: 14px;
                        letter-spacing: 0.6px;
                        color: #333;
                      "
                      class="picklist"
                      v-else-if="
                        field.dataTypeEnum === 'ENUM' ||
                          field.dataTypeEnum._name === 'ENUM'
                      "
                    >
                      <div
                        class="flLeft"
                        :class="
                          (workorder.data || {})[field.name] ? '' : 'color-d'
                        "
                      >
                        {{
                          (workorder.data || {})[field.name]
                            ? field.enumMap[
                                parseInt((workorder.data || {})[field.name])
                              ]
                            : '---'
                        }}
                      </div>
                    </div>
                    <div
                      style="
                        font-size: 14px;
                        letter-spacing: 0.6px;
                        color: #333;
                      "
                      class="picklist overflow-hidden"
                      v-else-if="
                        field.dataTypeEnum === 'MULTI_ENUM' ||
                          field.dataTypeEnum._name === 'MULTI_ENUM'
                      "
                    >
                      <div
                        :class="
                          (workorder.data || {})[field.name] ? '' : 'color-d'
                        "
                      >
                        {{
                          getMultiEnumFieldValues({
                            field,
                            data: workorder.data,
                          })
                        }}
                      </div>
                    </div>
                    <div
                      style="
                        font-size: 14px;
                        letter-spacing: 0.6px;
                        color: #333;
                      "
                      class="picklist"
                      v-else-if="
                        field.dataTypeEnum === 'BOOLEAN' ||
                          field.dataTypeEnum._name === 'BOOLEAN'
                      "
                    >
                      <div
                        class="flLeft"
                        :class="
                          $validation.isBoolean(
                            (workorder.data || {})[field.name]
                          )
                            ? ''
                            : 'color-d'
                        "
                      >
                        {{
                          $validation.isBoolean(
                            (workorder.data || {})[field.name]
                          )
                            ? (workorder.data || {})[field.name]
                              ? field.trueVal || 'True'
                              : field.falseVal || 'False'
                            : '---'
                        }}
                      </div>
                    </div>
                    <div
                      v-else-if="
                        !canHideField('Number') &&
                          (field.dataTypeEnum === 'NUMBER' ||
                            field.dataTypeEnum._name === 'NUMBER') &&
                          field.displayTypeInt === 23
                      "
                    >
                      {{
                        $helpers.getFormattedDuration(
                          workorder.data[field.name],
                          !$validation.isEmpty(field.unit) ? field.unit : 's'
                        )
                      }}
                    </div>
                    <div
                      v-else-if="
                        $getProperty(field, 'displayType') === 'SIGNATURE'
                      "
                    >
                      <SignatureField :field="field" :record="workorder" />
                    </div>
                    <template v-else-if="isFileTypeField(field)">
                      <div
                        v-if="
                          !$validation.isEmpty(
                            workorder.data[`${field.name}FileName`]
                          )
                        "
                        @click="openAttachment(field, workorder.data || {})"
                        class="d-flex file-column width100"
                      >
                        <el-tooltip
                          effect="dark"
                          :content="workorder.data[`${field.name}FileName`]"
                          placement="top-start"
                          :open-delay="1000"
                        >
                          <a class="truncate-text">
                            {{ workorder.data[`${field.name}FileName`] }}
                          </a>
                        </el-tooltip>
                      </div>
                      <div v-else>---</div>
                    </template>
                    <div v-else-if="canShowLookupField(field)">
                      <div
                        v-if="
                          $getProperty(field, 'lookupModule.name', '') ===
                            'users' &&
                            $getProperty(workorder, `data.${field.name}.id`)
                        "
                      >
                        {{
                          $store.getters.getUser(workorder.data[field.name].id)
                            .name
                        }}
                      </div>
                      <div v-else-if="isLookupSimpleField(field)">
                        <GlimpseLookupWrapper
                          :field="field"
                          :record="workorder.data"
                          recordModuleName="workorder"
                        ></GlimpseLookupWrapper>
                      </div>
                    </div>
                    <div
                      v-else-if="
                        !$validation.isEmpty(workorder.data[field.name]) &&
                          field.dataTypeEnum === 'MULTI_LOOKUP'
                      "
                    >
                      {{ getMultiLookupValue(workorder.data[field.name]) }}
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
                    <div
                      v-else-if="
                        workorder.data &&
                          !$validation.isEmpty(workorder.data[field.name])
                      "
                    >
                      {{ workorder.data[field.name] }}
                    </div>

                    <div v-else>
                      ---
                    </div>
                    <div class="clearboth"></div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </div>
      </div>
      <!-- workorder details section end -->
    </div>
    <WoV3EditForm
      v-if="editFormVisibilty"
      moduleName="workorder"
      :visibility="editFormVisibilty"
      :wo="workorder"
      @visibilityUpdate="handleUpdateVisibility"
    >
    </WoV3EditForm>
    <workorder-floorplanview
      v-if="floorplanVisible"
      :visible="floorplanVisible"
      :floorplandata="floorPlanViewMode"
      @close="
        () => {
          floorplanVisible = false
        }
      "
    >
    </workorder-floorplanview>
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
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  findRouteForTab,
  tabTypes,
} from '@facilio/router'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import { isLookupField, isFileTypeField } from '@facilio/utils/field'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import WoTimer from '@/WorkorderTimer'
import UserAvatar from '@/avatar/User'
import WoV3EditForm from 'src/pages/workorder/workorders/v3/WoV3EditForm.vue'
import TenantAvatar from '@/avatar/Tenant'
import WorkorderFloorplanview from '@/WorkorderFloorplanview'
import WOQuotationCard from './WOQuotationCard'
import SignatureField from '@/list/SignatureColumn'
import WOSummaryResourceCard from './WOSummaryResourceCard'
import RichTextPreview from './RichTextPreviewDialog'
import GlimpseLookupWrapper from 'src/newapp/components/GlimpseLookupWrapper.vue'
import { mapGettersWithLogging } from 'store/utils/log-map-getters'

export default {
  name: 'WorkOrderFieldDetails',
  mixins: [workorderMixin],
  components: {
    WoTimer,
    UserAvatar,
    WoV3EditForm,
    TenantAvatar,
    WorkorderFloorplanview,
    WOQuotationCard,
    SignatureField,
    WOSummaryResourceCard,
    RichTextPreview,
    GlimpseLookupWrapper,
  },
  props: [
    'workorder',
    'updateWoActions',
    'refreshWoDetails',
    'excludeFields',
    'isDisableForApproval',
    'currentView',
    'forV3',
  ],
  data() {
    return {
      floorplanVisible: false,
      duedate: '',
      vendorsList: [],
      editFormVisibilty: false,
      fields: [],
      floorPlanViewMode: {},
      loading: true,
      selectedFile: null,
      showPreview: false,
      richTextContent: null,
      richTextField: null,
      showRichTextPreview: false,
    }
  },
  mounted() {
    this.loadVendorsPicklist()
    this.loadFields()
    this.isFileTypeField = isFileTypeField
  },

  computed: {
    ...mapGetters([
      'getTicketPriority',
      'isStatusLocked',
      'getApprovalStatus',
      'getTicketCategory',
    ]),
    ...mapGettersWithLogging(['getSpace']),

    isTenantEnabled() {
      return this.$helpers.isLicenseEnabled('TENANTS')
    },

    isClientEnabled() {
      return this.$helpers.isLicenseEnabled('CLIENT')
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
    filteredFields() {
      // this is only for investa org vendor portal
      if (this.$org.id === 17) {
        return (this.fields || []).filter(field => field.name !== 'wototalcost')
      }
      return this.fields || []
    },

    canEdit() {
      if (
        !this.$hasPermission('workorder:UPDATE') ||
        this.$account.org.orgId === 406 ||
        (this.$account.org.orgId === 274 && this.$account.user.roleId === 1240)
      ) {
        // Restricting for 406 || BrightFM ,Techinician Role
        return false
      } else {
        let hasState = this.$getProperty(this.workorder, 'moduleState.id', null)
        let isLocked = hasState
          ? this.isStatusLocked(this.workorder.moduleState.id, 'workorder')
          : false
        let { isRequestedState } = this

        return hasState && !isLocked && !isRequestedState
      }
    },
    actualWorkDurationField() {
      return this.fields.find(field => field.name === 'actualWorkDuration')
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
      let { data } = workorder || {}
      let { name } = field || {}

      return this.$getProperty(data, name, '')
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
    getFieldDisplayName(fieldName) {
      let { fields } = this
      let { displayName } = fields.find(f => f.name === fieldName) || {}
      return displayName || ''
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
    canShowLookupField(field) {
      return !isEmpty(field)
        ? !this.canHideField('isLookupField') && isLookupField(field)
        : false
    },
    openTenant(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('tenant', pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: 'all',
                id,
              },
            })
          }
        } else {
          let url = '/app/tm/tenants/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
    },

    async loadVendorsPicklist() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'vendors' },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.vendorsList = options
      }
    },

    openParentWo(parentWo) {
      let { id } = parentWo || {}

      if (id) {
        if (isWebTabsEnabled()) {
          let { currentView } = this
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: currentView,
                id,
              },
            })
          }
        } else {
          this.$router.push({ path: '/app/wo/orders/summary/' + id })
        }
      } else {
        return
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
          let { id } = name === 'vendors' ? data[name] : data[fieldName]

          this.$router.push({
            name: routeName,
            params: {
              viewname: 'all',
              id,
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

    updateWo() {
      this.updateWoActions()
      this.refreshWoDetails()
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    canHideField(fieldName) {
      return this.excludeFields.includes(fieldName)
    },
    isFieldEmpty(field, workorderData) {
      let { name, displayType } = field
      let value = workorderData[name]
      if (displayType === 'SIGNATURE' || isFileTypeField(field)) {
        value = workorderData[`${name}Id`]
      }

      return isEmpty(value)
    },
    openAttachment(field, workorder) {
      this.selectedFile = {
        contentType: workorder[`${field.name}ContentType`],
        fileName: workorder[`${field.name}FileName`],
        downloadUrl: workorder[`${field.name}DownloadUrl`],
        previewUrl: workorder[`${field.name}Url`],
      }
      this.showPreview = true
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
  },
}
</script>
<style lang="scss" scoped>
.fc-scrollbar-wrap {
  padding-bottom: 0;
  margin-top: 0;
}
.wo-sidebar-margin {
  margin-bottom: 100px;
}
.wo-fields-scroll {
  height: calc(100vh - 180px);
}
.portal-wo-fields.wo-fields-scroll {
  height: calc(100vh - 130px);
}

.view-rich-text {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #2b8bff;
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
}
.portal-wo-scroll {
  height: calc(100vh - 130px);
}
</style>
