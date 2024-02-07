<template>
  <div>
    <div v-show="!reportLoading">
      <div
        class="header-sidebar-hide"
        :class="{
          'header-sidebar-hide': $route.path === 'pdf/summarydownloadreport',
        }"
      >
        <div v-if="workorderObject">
          <div>
            <div
              class="wo-download-report-con"
              v-for="wo in workorderObject"
              :key="wo.id"
            >
              <div>
                <div class="wo-download-header">
                  <div class="wo-download-cus-logo">
                    <img
                      v-show="$org.logoUrl || useSiteLogo"
                      :src="logoCollection[wo.siteId]"
                      class="height100px width100px object-contain"
                    />
                  </div>
                  <div class="wo-download-heading">
                    <div class="heading-black18 text-center fw-bold fc-black">
                      {{ wo.subject ? wo.subject : '---' }}
                    </div>
                    <div
                      class="flex-middle pT10"
                      style="justify-content: center;"
                    >
                      <div class="flex-middle justify-center">
                        <div class="fc-black f14 pL10">
                          {{ siteNameCollection[wo.siteId] }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="wo-download-fac-logo">
                    <img v-if="showLogo" :src="getFacilioLogoURL" />
                  </div>
                </div>
                <!-- main section -->
                <div class="wo-download-report-main-con mT10">
                  <div class="fc-black f14 line-height24 break-word">
                    {{ wo.description }}
                  </div>
                  <div>
                    <el-row class="pT20 pB20">
                      <el-col :span="12" class="pB15">
                        <el-col :span="11">
                          <div class="fc-black-text14 ">
                            {{ $t('common._common._id') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14">
                            #{{ $getProperty(wo, 'serialNumber', '') }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        v-if="isWoSourceTypeAvailable(wo)"
                        :span="12"
                        class="pB15"
                      >
                        <el-col :span="11">
                          <div class="fc-black-text14">
                            {{ $t('common._common.source_type') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14 line-height20">
                            {{ $getProperty(wo, 'sourceTypeVal', '') }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        v-if="wo.type && wo.type.id > 0"
                        :span="12"
                        class="pB15"
                      >
                        <el-col class="bold" :span="11">{{
                          $t('common._common.type')
                        }}</el-col>
                        <el-col :span="2">:</el-col>
                        <el-col :span="11">{{ getPMType(wo.type.id) }}</el-col>
                      </el-col>
                      <el-col
                        v-if="wo.assignedTo && wo.assignedTo.id > 0"
                        :span="12"
                        class="pB15"
                      >
                        <el-col class="bold" :span="11">{{
                          $t('asset.assets.asset_assigned_to')
                        }}</el-col>
                        <el-col :span="2">:</el-col>
                        <el-col :span="11">{{
                          $getProperty(wo, 'assignedTo.name', '---')
                        }}</el-col>
                      </el-col>
                      <el-col
                        v-if="wo.priority && wo.priority.id > 0"
                        :span="12"
                        class="pB15"
                      >
                        <el-col class="bold" :span="11">{{
                          $t('common.header._priority')
                        }}</el-col>
                        <el-col :span="2">:</el-col>
                        <el-col :span="11">
                          {{ getPriority(wo) }}
                        </el-col>
                      </el-col>
                      <el-col
                        v-if="canShowCategory(wo)"
                        :span="12"
                        class="pB15"
                      >
                        <el-col class="bold" :span="11">{{
                          $t('common._common.category')
                        }}</el-col>
                        <el-col :span="2">:</el-col>
                        <el-col :span="11">
                          {{ getTicketCategory(wo.category.id).displayName }}
                        </el-col>
                      </el-col>

                      <el-col
                        :span="12"
                        class="pB15"
                        v-if="wo.resource && wo.resource.resourceType === 2"
                      >
                        <el-col :span="11">
                          <div
                            class="fc-black-text14 "
                            v-if="wo.resource && wo.resource.id > 0"
                          >
                            {{ $t('common._common.asset') }}
                          </div>
                          <div class="bold" v-else>
                            {{ $t('qanda.response.space') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14 line-height20">
                            {{ wo.resource.name }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        :span="12"
                        class="pB15"
                        v-if="
                          wo.resource &&
                            (wo.resource.resourceType === 2 ||
                              wo.resource.spaceType !== 2) &&
                            wo.resource.buildingId > 0
                        "
                      >
                        <el-col :span="11">
                          <div class="fc-black-text14">
                            {{ $t('common._common.building') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14 line-height20">
                            {{ buildingsList[wo.resource.buildingId] }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        :span="12"
                        class="pB15"
                        v-if="isResourceSpaceNameAvailable(wo)"
                      >
                        <el-col :span="11">
                          <div
                            class="fc-black-text14"
                            v-if="
                              wo.resource &&
                                wo.resource.id > 0 &&
                                wo.resource.space &&
                                wo.resource.space.spaceTypeVal
                            "
                          >
                            {{ wo.resource.space.spaceTypeVal }}
                          </div>
                          <div v-else class="bold">
                            {{ $t('qanda.response.space') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14">
                            {{ wo.resource.space.name }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        :span="12"
                        class="pB15"
                        v-if="wo.resource && wo.resource.resourceType === 1"
                      >
                        <el-col :span="11">
                          <div
                            class="fc-black-text14"
                            v-if="
                              wo.resource &&
                                wo.resource.id > 0 &&
                                wo.resource.spaceTypeVal
                            "
                          >
                            {{ wo.resource.spaceTypeVal }}
                          </div>
                          <div v-else class="bold">
                            {{ $t('qanda.response.space') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14">
                            {{ wo.resource.name }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col :span="12" class="pB15" v-if="wo.createdTime > 0">
                        <el-col :span="11">
                          <div class="fc-black-text14">
                            {{ $t('common.failure_class.created_time') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14">
                            {{ wo.createdTime | formatDate }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        :span="12"
                        class="pB15"
                        v-if="wo.actualWorkStart > 0"
                      >
                        <el-col :span="11">
                          <div class="fc-black-text14">
                            {{ $t('common.wo_report.start_time') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14">
                            {{ wo.actualWorkStart | formatDate }}
                          </div>
                        </el-col>
                      </el-col>
                      <el-col
                        :span="12"
                        class="pB15"
                        v-if="wo.actualWorkEnd > 0"
                      >
                        <el-col :span="11">
                          <div class="fc-black-text14">
                            {{ $t('common.header.resolvedTime') }}
                          </div>
                        </el-col>
                        <el-col :span="2">
                          <div class="label-txt-black f14">:</div>
                        </el-col>
                        <el-col :span="11">
                          <div class="label-txt-black f14">
                            {{ wo.actualWorkEnd | formatDate }}
                          </div>
                        </el-col>
                      </el-col>
                      <!-- last -->
                      <template
                        v-if="
                          wo.sourceType === 5 && !$validation.isEmpty(wo.pm)
                        "
                      >
                        <el-col v-if="wo.triggerEnum" :span="12" class="pB15">
                          <el-col :span="11">
                            <div class="fc-black-text14">
                              {{ $t('common._common.frequency') }}
                            </div>
                          </el-col>
                          <el-col :span="2">
                            <div class="label-txt-black f14">:</div>
                          </el-col>
                          <el-col :span="11">
                            <div class="label-txt-black f14 line-height20">
                              {{ wo.triggerEnum }}
                            </div>
                          </el-col>
                        </el-col>
                        <el-col
                          v-else-if="isScheduleMsgAvail(wo)"
                          :span="12"
                          class="pB15"
                        >
                          <el-col :span="11">
                            <div class="fc-black-text14">
                              {{ $t('common._common.frequency') }}
                            </div>
                          </el-col>
                          <el-col :span="2">
                            <div class="label-txt-black f14">:</div>
                          </el-col>
                          <el-col :span="11">
                            <div class="label-txt-black f14 line-height20">
                              {{ pmMap[wo.pm.id].scheduleMsg }}
                            </div>
                          </el-col>
                        </el-col>
                        <el-col :span="12" class="pB15" v-if="isPmAvail(wo)">
                          <el-col :span="11">
                            <div class="fc-black-text14">
                              {{ $t('common.header.previous_execution') }}
                            </div>
                          </el-col>
                          <el-col :span="2">
                            <div class="label-txt-black f14">:</div>
                          </el-col>
                          <el-col :span="11">
                            <div class="label-txt-black f14">
                              {{ wo.previousExecution | formatDate }}
                            </div>
                          </el-col>
                        </el-col>
                      </template>
                    </el-row>
                  </div>
                  <!-- second section -->
                  <div
                    class="pB20 border-bottom8 tasks-print"
                    v-if="
                      wo &&
                        wo.taskLists &&
                        wo.taskLists.tasks &&
                        Object.keys(wo.taskLists.tasks).length !== 0
                    "
                  >
                    <div class="pB20 fc-black-text14 fw-bold">
                      {{ $t('common._common.tasks') }}
                    </div>
                    <el-row class="mB20 border-bottom8 border-top2">
                      <el-col :span="13">
                        <div class="label-txt-black f14 pT15 pB15 fw-bold">
                          {{ $t('common.wo_report.task_name') }}
                        </div>
                      </el-col>
                      <el-col :span="7">
                        <div
                          class="label-txt-black f14 pT10 pB10 fw-bold"
                          v-if="canShowTaskResource(wo)"
                        >
                          {{ $t('common._common._space_asset') }}
                        </div>
                      </el-col>
                      <el-col :span="4" class="fR">
                        <div class="label-txt-black f14 pT10 pB10 fw-bold">
                          {{ $t('common._common.input_value') }}
                        </div>
                      </el-col>
                    </el-row>
                    <div v-if="wo.taskLists">
                      <div
                        v-if="wo.sec"
                        v-for="(obj, index) in wo.taskL"
                        :key="index"
                        class="clear"
                      >
                        <div
                          v-if="wo.sec[obj.sectionId]"
                          class="label-txt-black f14 mB20 fw-bold mT20"
                          :style="{
                            'border-bottom': wo.sec[obj]
                              ? 'solid 2px #d9f4f7'
                              : '0',
                          }"
                        >
                          {{
                            wo.sec[obj.sectionId]
                              ? wo.sec[obj.sectionId].name
                              : ''
                          }}
                        </div>
                        <el-row
                          class="pT15"
                          v-for="(task, index1) in obj.taskList"
                          :key="index1"
                        >
                          <div>
                            <el-col :span="13">
                              <div class="label-txt-black f14">
                                {{ task.subject }}
                              </div>
                              <div
                                class="fc-black-12 pT5 text-left"
                                v-if="task.description"
                              >
                                {{ task.description }}
                              </div>
                            </el-col>
                            <el-col :span="7">
                              <div class="flex-middle">
                                <div class="fc-black f14">
                                  <!-- task section one-->
                                  <div
                                    v-if="
                                      canShowTaskResource(wo) &&
                                        task.resource &&
                                        task.resource.name
                                    "
                                    class="flex-middle"
                                  >
                                    <div class="flex-middle">
                                      <img
                                        src="~statics/space/spacepin.svg"
                                        style="height:12px; width:14px;"
                                      />
                                      <div class="pL10">
                                        {{ task.resource.name }}
                                      </div>
                                    </div>
                                  </div>
                                  <!-- task section two -->
                                </div>
                              </div>
                            </el-col>
                            <el-col :span="4" class="fR">
                              <div class="label-txt-black f14 text-center">
                                <div
                                  v-if="task.inputType !== 1 && task.id !== -1"
                                >
                                  <div
                                    v-if="
                                      task.inputType === 2 ||
                                        task.inputType === '2'
                                    "
                                  >
                                    <div
                                      v-if="
                                        task.readingField &&
                                          task.readingField.dataType === 4
                                      "
                                    >
                                      <div>{{ task.readingField.trueVal }}</div>
                                      <div>
                                        {{ task.readingField.falseVal }}
                                      </div>
                                    </div>
                                    <div v-if="!task.inputValue">
                                      {{ '--' }}
                                    </div>
                                    <div
                                      v-else-if="
                                        task.readingFieldUnit &&
                                          task.readingFieldUnit === 18 // hour
                                      "
                                    >
                                      {{
                                        convertSIUnitValueToUnit1Unit2Format(
                                          task.readingField.metric,
                                          18,
                                          19,
                                          task.inputValue
                                        )
                                      }}
                                    </div>
                                    <div v-else-if="task.readingFieldUnit">
                                      {{ task.inputValue }}
                                      {{
                                        getReadingFieldUnit(
                                          task.readingField.metric,
                                          task.readingFieldUnit
                                        )
                                      }}
                                    </div>
                                    <div
                                      v-else-if="
                                        task.readingField &&
                                          task.readingField.unit
                                      "
                                    >
                                      {{ task.inputValue }}
                                      {{ task.readingField.unit }}
                                    </div>
                                    <div v-else-if="task.inputValue">
                                      {{ task.inputValue }}
                                    </div>
                                    <div
                                      :id="'prevpopup_' + task.id"
                                      class="hide"
                                    >
                                      <div class="task-prevreading">
                                        {{ task.lastReading
                                        }}{{
                                          task.readingField &&
                                          task.readingField.unit
                                            ? task.readingField.unit
                                            : ''
                                        }}
                                        <span
                                          >({{
                                            $t(
                                              'common.header.previous_reading'
                                            )
                                          }})</span
                                        >
                                      </div>
                                    </div>
                                  </div>
                                  <div
                                    v-if="
                                      task.inputType === 4 ||
                                        task.inputType === '4'
                                    "
                                  >
                                    <div class="text-center">
                                      {{ task.inputValue }}
                                    </div>
                                  </div>
                                  <div
                                    v-if="
                                      task.inputType === 3 ||
                                        task.inputType === '3'
                                    "
                                  >
                                    <div class>{{ task.inputValue }}</div>
                                  </div>
                                  <div
                                    v-if="
                                      task.inputType === 5 ||
                                        task.inputType === '5'
                                    "
                                  >
                                    <div class>{{ task.inputValue }}</div>
                                  </div>
                                </div>
                              </div>
                            </el-col>
                          </div>
                        </el-row>
                      </div>
                    </div>
                  </div>
                  <!-- third section -->
                  <div v-if="showComments && wo.notesLists">
                    <div
                      class="border-bottom8 clear pT20"
                      v-if="wo.notesLists && wo.notesLists.length > 0"
                    >
                      <div class="fc-black-text14 fw-bold">
                        {{ $t('common._common.comments') }}
                      </div>
                      <el-row class="pB30">
                        <el-col
                          :span="24"
                          v-for="comment in wo.notesLists"
                          :key="comment.id"
                          class="mT10"
                        >
                          <div class="f14">
                            <i>
                              <b>{{ comment.createdBy.name }}</b>
                            </i>
                            <span class="pL10 pR10">|</span>
                            <span class="f12">{{
                              comment.createdTime | formatDate
                            }}</span>
                          </div>
                          <div
                            class="fc-black-14 text-left line-height18 pT10 pb10"
                          >
                            {{ comment.body }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                  <!-- fourth section -->

                  <div
                    v-if="showAttachements && woAttachments[wo.id]"
                    class="border-bottom8"
                  >
                    <div
                      class="label-txt-black fwBold mT20"
                      v-if="woAttachments[wo.id].images.length"
                    >
                      {{ $t('common.attachment_form.attachments') }}
                    </div>
                    <div>
                      <el-row :gutter="20">
                        <el-col
                          :span="12"
                          v-for="attachment in woAttachments[wo.id].images"
                          :key="attachment.id"
                        >
                          <div class="">
                            <div class="label-txt-black f14 pT10 pointer">
                              <a :href="attachment.previewUrl" target="_blank">
                                {{ attachment.fileName }}
                              </a>
                            </div>
                            <div class="fc-grey-text12 fw4 pT5">
                              {{ attachment.createdTime | formatDate(true) }}
                            </div>
                            <div class="mT20">
                              <img
                                :src="attachment.previewUrl"
                                style=";border: 1px solid #eee;max-width: 350px;max-height: 350px;"
                              />
                            </div>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- docs sec -->
                    <div>
                      <div class="fwBold pT20">
                        {{ $t('common._common.documents') }}
                      </div>
                      <div
                        class="width100"
                        v-for="attachment in woAttachments[wo.id].docs"
                        :key="attachment.id"
                      >
                        <div class="border-bottom1px pB20 pT20 pointer">
                          <a
                            :href="attachment.previewUrl"
                            target="_blank"
                            class="cursor: pointer;"
                          >
                            {{ attachment.fileName }}
                          </a>
                          <div class="fc-grey-text12 pT5 fw4">
                            {{ attachment.createdTime | formatDate(true) }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- task image print -->
                  <div
                    v-if="
                      showImage &&
                        taskAttachments[wo.id] &&
                        taskAttachments[wo.id].attachments.length
                    "
                    class="wo-task-img-con"
                  >
                    <div class="label-txt-black fwBold mT20">
                      {{ $t('common._common.task_photos') }}
                    </div>
                    <el-row
                      class="pT15"
                      v-for="(attachment, index1) in taskAttachments[wo.id]
                        .attachments"
                      :key="index1"
                    >
                      <div class="wo-summary-pdf-attachement">
                        <el-col :span="24">
                          <div class="label-txt-black f14 pT10">
                            {{ attachment.subject }}
                          </div>
                          <div class="mT10">
                            <el-row>
                              <el-col :span="12" v-if="attachment.before">
                                <div class="label-txt-black f14 fw6">
                                  <a
                                    :href="attachment.before"
                                    target="_blank"
                                    class="cursor: pointer;"
                                  >
                                    {{ $t('common.wo_report.before') }}
                                  </a>
                                </div>
                                <div class="task-image-attachment-block">
                                  <img
                                    :src="attachment.before"
                                    class="task-image"
                                  />
                                  <template
                                    v-if="
                                      attachment.before &&
                                        attachment.uploadedTime
                                    "
                                  >
                                    <div class="mT8 f11 text-fc-pink bold">
                                      {{ $t('common._common.uploaded_time') }}
                                    </div>
                                    <span class="mT4 fL fc-italic f11">
                                      {{
                                        attachment.uploadedTime | formatDate()
                                      }}
                                    </span>
                                  </template>
                                </div>
                              </el-col>
                              <el-col :span="12" v-if="attachment.after">
                                <div class="label-txt-black f14 fw6">
                                  <a
                                    :href="attachment.after"
                                    target="_blank"
                                    class="cursor: pointer;"
                                  >
                                    {{ $t('common.wo_report.after') }}
                                  </a>
                                </div>
                                <div class="task-image-attachment-block">
                                  <img
                                    :src="attachment.after"
                                    class="task-image"
                                  />
                                  <template
                                    v-if="
                                      attachment.after &&
                                        attachment.uploadedTime
                                    "
                                  >
                                    <div class="mT8 f11 text-fc-pink bold">
                                      {{ $t('common._common.uploaded_time') }}
                                    </div>
                                    <span class="mT4 fL fc-italic f11">
                                      {{
                                        attachment.uploadedTime | formatDate()
                                      }}
                                    </span>
                                  </template>
                                </div>
                              </el-col>
                            </el-row>
                          </div>
                        </el-col>
                      </div>
                    </el-row>
                  </div>
                  <WoDownloadHistory
                    :history="history[wo.id]"
                  ></WoDownloadHistory>

                  <!-- fifth section -->
                  <div v-if="showSignature">
                    <div class="pT70 pB10" v-if="$org.id === 183">
                      <el-row>
                        <el-col :span="8">
                          <div class="fc-black-14 text-left fw-bold">
                            {{ $t('common.dialog.technician_sign') }}
                          </div>
                        </el-col>
                        <el-col :span="8">
                          <div class="fc-black-14 fw-bold">
                            {{ $t('common.dialog.supervisor_sign') }}
                          </div>
                        </el-col>
                        <el-col :span="8">
                          <div class="fc-black-14 text-right fw-bold">
                            {{ $t('common.dialog.client_sign') }}
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <div class="pT70 pB10" v-else>
                      <div class="fL fc-black-14 fw-bold">
                        <div v-if="$org.id === 418">
                          <img
                            v-if="wo.signature_7Url"
                            :src="wo.signature_7Url"
                            style="width: 250px; height: 50px;object-fit: scale-down;"
                          />
                        </div>
                        <div v-else></div>
                        <div>
                          {{ $t('common._common.signature') }}
                        </div>
                      </div>
                      <div class="fR fc-black-14 fw-bold">
                        <div v-if="$org.id === 418">
                          <img
                            v-if="wo.signature_6Url"
                            :src="wo.signature_6Url"
                            style="width: 250px; height: 50px;object-fit: scale-down;"
                          />
                        </div>
                        <div v-else></div>
                        <div>
                          {{ $t('common.dialog.technician_sign') }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="reportLoading" class="header-sidebar-hide">
      <div class="wo-summary-loading">
        <spinner :show="reportLoading" size="80"></spinner>
      </div>
    </div>
  </div>
</template>
<script>
import WorkorderMixin from 'pages/pdf/mixins/WorkorderPdfMixin'
import WoDownloadHistory from './WoDownloadHistory.vue'
export default {
  mixins: [WorkorderMixin],
  components: { WoDownloadHistory },
  methods: {
    isWoSourceTypeAvailable(wo) {
      let { sourceType, sourceTypeVal } = wo || {}
      return sourceType && sourceTypeVal
    },
    isResourceSpaceNameAvailable(wo) {
      let resourceType = this.$getProperty(wo, 'resource.resourceType')
      let spaceName = this.$getProperty(wo, 'resource.space.name')
      return resourceType === 2 && spaceName
    },
    getPriority(wo) {
      let id = this.$getProperty(wo, 'priority.id', 0)
      if (id > 0) {
        let ticketPriority = this.getTicketPriority(id)
        let { displayName } = ticketPriority || {}
        return displayName
      }
      return '---'
    },
    canShowCategory(wo) {
      let id = this.$getProperty(wo, 'category.id', 0)
      return id > 0
    },
  },
}
</script>

<style lang="scss">
.header-sidebar-hide .layout-header {
  display: none;
}

.wo-summary-loading {
  position: absolute;
  top: 40%;
  left: 50%;
}

.header-sidebar-hide {
  width: 100%;
  height: 100vh;
  padding-left: 0 !important;
  background: #fff;
  overflow-y: scroll;
  overflow-x: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999999;
}

.wo-download-report-con {
  width: 100%;
  overflow: visible;
  margin-top: 20px;
  margin-bottom: 50px;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  box-sizing: border-box;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
}

.wo-download-report-con .label-txt-black {
  color: #000000 !important;
}

.wo-download-header {
  width: 100%;
  height: 110px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding-left: 20px;
  padding-right: 20px;
}

.wo-download-report-main-con {
  margin: 0px 30px 30px 30px;
}

.fc-comp-name {
  color: #000000;
  font-weight: bold;
}

.wo-download-cus-logo {
  float: left;
  width: 15%;
  margin-top: 10px;
  margin-bottom: 10px;
}

.wo-download-heading {
  width: 70%;
}

.wo-download-fac-logo {
  width: 15%;
  text-align: right;
}

.wo-summary-pdf-attachement {
  .task-image {
    width: auto;
    border-radius: 5px;
    display: inline-block;
    border: 1px solid #f2f2f2;
    cursor: pointer;
    margin-top: 5px;
    max-width: 73%;
    height: 80%;
  }

  .header-label {
    font-weight: 500;
  }

  .task-image-attachment-block {
    position: relative;
    width: 500px;
    height: 310px;
    overflow: hidden;
    margin-top: 20px;
  }
}

@page {
  orphans: 4;
  widows: 2;
  margin-bottom: 0.7cm;
  margin-top: 0.7cm;
}

@media print {
  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .normal main {
    height: 100vh !important;
    min-height: 100vh !important;
  }

  body {
    overflow: scroll;
    background: #fff;
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .normal main {
    display: block;
    width: auto;
    height: auto;
    overflow: visible;
  }

  .cardpadding {
    display: none;
  }

  .wo-download-report-con {
    display: block;
    width: auto;
    height: 100%;
    margin-top: 0;
    overflow: visible;
    display: block;
    page-break-inside: avoid;
    page-break-after: always;
    // page-break-before: always;
  }

  .scrollable {
    overflow: visible;
    margin-top: 0 !important;
    padding-top: 0px !important;
  }

  table {
    page-break-after: auto;
    page-break-inside: auto;
  }

  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  thead {
    display: table-header-group;
  }

  tfoot {
    display: table-footer-group;
  }

  .header-sidebar-hide {
    width: 100%;
    height: 100vh;
    padding-left: 0 !important;
    background: #fff;
    overflow-x: unset;
    overflow-y: unset;
    position: unset;
    top: 0;
    left: 0;
    right: 0;
    z-index: 999999;
    page-break-after: always;
    page-break-inside: avoid;
  }

  .subheader-preventive-summary {
    display: none;
    z-index: 0;
  }

  .el-dropdown-menu {
    display: none;
  }

  .print-break-page {
    display: block;
    page-break-after: always;
  }

  .label-txt-black,
  .fc-black-text14,
  .f14 {
    font-size: 12px !important;
  }

  .wo-download-report-main-con {
    height: 100%;
  }

  .other-details-clear .el-col-12:nth-of-type(3) {
    clear: both;
  }

  .clear {
    clear: both;
  }

  .wo-summary-pdf-attachement {
    .custom-file-upload {
      width: 100%;
    }
  }

  .wo-task-img-con {
    page-break-inside: avoid;
  }
}
</style>

<style lang="scss" scoped>
.fc-black-text14 {
  font-size: 14px !important;
  font-weight: 500;
}
</style>
