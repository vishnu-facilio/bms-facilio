<template>
  <div
    style="height: auto"
    :class="[
      'new-summary-view wo-summary-page',
      !isNotPortal && 'portal-wo-summary-page',
    ]"
  >
    <div v-if="workorder">
      <div class="fc-pm-summary-header fc-new-pm-summary-header">
        <div class="fL pointer">
          <div class="wos-id" @click="back" :title="$t('common.header.back')">
            <img
              src="~assets/arrow-pointing-to-left.svg"
              data-position="top"
              v-tippy="{ arrow: true, animation: 'perspective' }"
              width="12"
              height="12"
              class="vertical-middle mR5 mL3 pointer"
            />
            <span class="fc-id">#{{ workorder.serialNumber }}</span>
          </div>
          <div class="heading-black22 pT5 flex-middle">
            <i
              v-if="
                $getProperty(workorder, 'moduleState.id', null) &&
                  isStatusLocked(workorder.moduleState.id, 'workorder')
              "
              class="fa fa-lock locked-wo"
              data-arrow="true"
              :title="$t('maintenance.approval.waiting_for_approval')"
              v-tippy
            ></i>
            <div
              class="max-width500px textoverflow-ellipsis show"
              :title="workorder.subject"
              v-tippy
              data-arrow="true"
            >
              {{ workorder.subject ? workorder.subject : '---' }}
            </div>
            <span class="fc-newsummary-chip">
              {{
                ($getProperty(workorder, 'moduleState.id', null) &&
                  (
                    $store.getters.getTicketStatus(
                      workorder.moduleState.id,
                      'workorder'
                    ) || {}
                  ).displayName) ||
                  '---'
              }}
            </span>
          </div>
        </div>
        <div
          class="fc-v1-sum-H-fR fR pT10 position-relative"
          style="z-index: 100;"
        >
          <asset-breakdown
            v-if="showAssetBreakdown"
            :assetBDSourceDetails="assetBDSourceDetails"
            :visibility.sync="showAssetBreakdown"
          ></asset-breakdown>
          <div class="flex-middle"></div>

          <div>
            <div class="fR display-flex">
              <div v-if="$helpers.isLicenseEnabled('GOOGLE_TRANSLATION')">
                <div
                  @click="handleTranslate"
                  class="gTranslate-icon-place fc-gtransalte"
                  v-tippy="{
                    arrow: true,
                    arrowType: 'round',
                    animation: 'fade',
                  }"
                  content="Show translation text changes in subject,description,comments and tasks"
                  v-if="$account.user.appType === 3"
                  :class="{ translateActive: isActive }"
                >
                  <inline-svg
                    src="svgs/google-translate"
                    iconClass="icon text-center icon-md vertical-middle"
                  ></inline-svg>
                </div>
              </div>
              <CustomButton
                moduleName="workorder"
                :record="workorder"
                :updateUrl="updateUrlString"
                :transformFn="transformFormData"
                :position="POSITION.SUMMARY"
                @refresh="onTransitionSuccess"
                @onSuccess="() => {}"
                @onError="() => {}"
              />
              <TransitionButtons
                ref="TransitionButtons"
                moduleName="workorder"
                :record="workorder"
                :transformFn="transformFormData"
                :transitionFilter="transitionFilter"
                :updateUrl="updateUrlString"
                :disabled="isApprovalEnabled"
                buttonClass="asset-el-btn"
                @currentState="
                  id => $setProperty(workorder, 'moduleState.id', id)
                "
                @transitionSuccess="onTransitionSuccess"
                @transitionFailure="() => {}"
              ></TransitionButtons>
              <el-dropdown
                class="mL10 fc-btn-ico-lg pointer"
                style="padding-top: 5px; padding-bottom: 5px;"
                trigger="click"
              >
                <span class="el-dropdown-link">
                  <img src="~assets/menu.svg" width="16" height="16" />
                </span>
                <el-dropdown-menu slot="dropdown" trigger="click" class="p10">
                  <div
                    class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                    @click="openPrintOptionDialog()"
                  >
                    {{ $t('common._common.print') }}
                  </div>
                  <div
                    class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                    @click="openDownloadOptionDialog()"
                  >
                    {{ $t('common._common.download') }}
                  </div>
                  <div
                    v-if="canShowReportDownTime"
                    class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                    @click="
                      openAssetBreakDown(
                        workorder.resource.id,
                        workorder.subject
                      )
                    "
                  >
                    {{ $t('maintenance._workorder.report_downtime') }}
                  </div>
                  <div
                    v-if="enablePrerequisiteApprove"
                    class="pT5 pB5 fc-label-hover label-txt-black f14 pL10 pR10 pointer"
                    @click="preRequisiteApprove()"
                  >
                    {{ $t('maintenance._workorder.prerequisite_approve') }}
                  </div>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
      </div>

      <ApprovalBar
        v-if="isApprovalEnabled"
        moduleName="workorder"
        :record="workorder"
        :transformFn="transformFormData"
        canShowEdit="true"
        @onSuccess="onTransitionSuccess"
        @onFailure="() => {}"
        @onEdit="
          () => {
            this.$refs['wo-field-details'].openWoEditForm()
          }
        "
      ></ApprovalBar>
      <div
        :class="[
          'clearboth fc-pm-summary-tab wo-summary-container',
          isApprovalEnabled && 'offset-top',
        ]"
      >
        <el-tabs v-model="activeName">
          <!-- summary -->
          <el-tab-pane
            :label="$t('maintenance._workorder.summary')"
            name="first"
          >
            <div class="pm-summary-inner-container">
              <div
                class="fL pm-summary-inner-left-con mT20 fc-res-dell fc-wo-page-scroll scrollbar-style"
              >
                <div
                  class="p30 white-bg-block pB40 fc__wo__sum__h__desc mB20"
                  v-if="workorder.subject.length > 45 || workorder.description"
                >
                  <div>
                    <div
                      class="heading-black18"
                      v-if="workorder.subject.length > 45"
                    >
                      {{ workorder.subject ? workorder.subject : '---' }}
                    </div>
                    <div
                      class="space-preline pm-summary-tab-subject"
                      v-if="workorder.description"
                      v-html="getDescription(workorder)"
                    ></div>
                  </div>
                  <div v-if="workorder.subject.length > 45">
                    <div v-if="$helpers.isLicenseEnabled('GOOGLE_TRANSLATION')">
                      <div v-if="$account.user.appType === 3">
                        <InstantTranslate
                          v-if="woDescriptiontranslate"
                          :content="workorder.subject"
                          :extraContent="workorder.description"
                        ></InstantTranslate>
                      </div>
                    </div>
                  </div>
                  <div v-else>
                    <div v-if="$helpers.isLicenseEnabled('GOOGLE_TRANSLATION')">
                      <div v-if="$account.user.appType === 3">
                        <InstantTranslate
                          v-if="woDescriptiontranslate"
                          :content="workorder.description"
                        ></InstantTranslate>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="clearboth">
                  <el-row :gutter="10">
                    <!-- tasks completed -->
                    <el-col :span="8">
                      <div
                        class="p30 white-bg-block nws-chart-container height212px width100"
                        v-if="taskChartData"
                      >
                        <f-mini-chart
                          class="nws-chart"
                          :type="'semidoughnut'"
                          :data="taskChartData"
                        ></f-mini-chart>
                      </div>
                    </el-col>
                    <!-- scheduled time  -->
                    <el-col :span="8">
                      <div
                        class="col-4 white-bg-block new-sum-scheduled height212px width100"
                      >
                        <div class="fc__wo__sum__chart_block position-relative">
                          <div class="fc-black-13 fw-bold text-left pL20">
                            {{ $t('maintenance.pm_list.scheduled') }}
                          </div>
                        </div>
                        <div
                          class="row pL20 pR20 pT20"
                          v-if="
                            (workorder.scheduledStart &&
                              workorder.scheduledStart > -1) ||
                              (workorder.estimatedEnd &&
                                workorder.estimatedEnd > -1)
                          "
                        >
                          <div class="col-6 n-su-left-cont">
                            <div class>
                              <div class="fc-text-pink11 text-uppercase">
                                {{ $t('common.header.start') }}
                              </div>
                              <div
                                class="fc-black-13 text-left pT10"
                                v-if="
                                  workorder.scheduledStart &&
                                    workorder.scheduledStart > -1
                                "
                              >
                                {{
                                  workorder.scheduledStart | formatDate(true)
                                }}
                              </div>
                              <div class="fc-black-13 text-left pT10" v-else>
                                ---
                              </div>
                            </div>
                            <div class="mT40">
                              <div class="fc-text-pink11 text-uppercase">
                                {{ $t('maintenance._workorder.finish') }}
                              </div>
                              <div
                                class="fc-black-13 text-left"
                                v-if="
                                  workorder.estimatedEnd &&
                                    workorder.estimatedEnd > -1
                                "
                              >
                                {{ workorder.estimatedEnd | formatDate(true) }}
                              </div>
                              <div
                                class="fc-black-13 text-left"
                                v-else-if="
                                  workorder.dueDate && workorder.dueDate > -1
                                "
                              >
                                {{ workorder.dueDate | formatDate(true) }}
                              </div>
                              <div class="fc-black-13 text-left" v-else>
                                ---
                              </div>
                            </div>
                          </div>
                          <div class="col-6">
                            <div
                              class="fc-black-22 pT10"
                              v-if="
                                workorder.scheduledStart &&
                                  workorder.scheduledStart > -1
                              "
                            >
                              {{ formDate(workorder.scheduledStart, 'hh:mm') }}
                              <span class="fc-grey3-text14">{{
                                formDate(workorder.scheduledStart, 'A')
                              }}</span>
                            </div>
                            <div v-else class="fc-black-22 pT10">---</div>
                            <div
                              class="fc-black-22 mT40"
                              v-if="
                                workorder.estimatedEnd &&
                                  workorder.estimatedEnd > -1
                              "
                            >
                              {{ formDate(workorder.estimatedEnd, 'hh:mm') }}
                              <span class="fc-grey3-text14">{{
                                formDate(workorder.estimatedEnd, 'A')
                              }}</span>
                            </div>
                            <div
                              class="fc-black-22 mT40"
                              v-else-if="
                                workorder.dueDate && workorder.dueDate > -1
                              "
                            >
                              {{ formDate(workorder.dueDate, 'hh:mm') }}
                              <span class="fc-grey3-text14">{{
                                formDate(workorder.dueDate, 'A')
                              }}</span>
                            </div>
                            <div v-else class="fc-black-22 mT40">---</div>
                          </div>
                        </div>
                        <div v-else>
                          <div class="text-center fc-black-14 pT60 pB60">
                            {{ $t('common._common.nodata') }}
                          </div>
                        </div>
                      </div>
                    </el-col>
                    <!-- actual time  -->
                    <el-col :span="8">
                      <div
                        class="col-4 white-bg-block new-sum-scheduled height212px width100"
                      >
                        <div class="fc__wo__sum__chart_block position-relative">
                          <div class="fc-black-13 fw-bold text-left pL20">
                            {{ $t('maintenance._workorder.actual') }}
                          </div>
                        </div>
                        <div
                          class="row pL20 pR20 pT20"
                          v-if="
                            (workorder.actualWorkStart &&
                              workorder.actualWorkStart > -1) ||
                              (workorder.actualWorkEnd &&
                                workorder.actualWorkEnd > -1)
                          "
                        >
                          <div class="col-5 n-su-left-cont">
                            <div class>
                              <div class="fc-text-pink11 text-uppercase">
                                {{ $t('common.header.start') }}
                              </div>
                              <div
                                class="fc-black-13 text-left pT10"
                                v-if="
                                  workorder.actualWorkStart &&
                                    workorder.actualWorkStart > -1
                                "
                              >
                                {{
                                  workorder.actualWorkStart | formatDate(true)
                                }}
                              </div>
                              <div class="fc-black-13 text-left pT10" v-else>
                                ---
                              </div>
                            </div>
                            <div class="mT40">
                              <div class="fc-text-pink11 text-uppercase">
                                {{ $t('maintenance._workorder.finish') }}
                              </div>
                              <div
                                class="fc-black-13 text-left"
                                v-if="
                                  workorder.actualWorkEnd &&
                                    workorder.actualWorkEnd > -1
                                "
                              >
                                {{ workorder.actualWorkEnd | formatDate(true) }}
                              </div>
                              <div class="fc-black-13 text-left" v-else>
                                ---
                              </div>
                            </div>
                          </div>
                          <div class="col-2 timer-line">
                            <div class="nu-su-timer-icon">
                              <i class="el-icon-time"></i>
                            </div>
                            <div
                              class="nu-su-time-line"
                              v-bind:class="{
                                shnotfinished:
                                  workorder.actualWorkEnd &&
                                  workorder.actualWorkEnd < 0,
                              }"
                            ></div>
                            <div
                              class="nu-su-timer-icon nu-bottom-time"
                              v-bind:class="{
                                shnotfinished:
                                  workorder.actualWorkEnd &&
                                  workorder.actualWorkEnd < 0,
                              }"
                            >
                              <i class="el-icon-time"></i>
                            </div>
                          </div>
                          <div class="col-5">
                            <div
                              class="fc-black-22 pT10"
                              v-if="
                                workorder.actualWorkStart &&
                                  workorder.actualWorkStart > -1
                              "
                            >
                              {{ formDate(workorder.actualWorkStart, 'hh:mm') }}
                              <span class="fc-grey3-text14">{{
                                formDate(workorder.actualWorkStart, 'a')
                              }}</span>
                            </div>
                            <div v-else class="fc-black-22 pT35">---</div>
                            <div
                              class="fc-black-22 pT35"
                              v-if="
                                workorder.actualWorkEnd &&
                                  workorder.actualWorkEnd > -1
                              "
                            >
                              {{ formDate(workorder.actualWorkEnd, 'hh:mm') }}
                              <span class="fc-grey3-text14">{{
                                formDate(workorder.actualWorkEnd, 'a')
                              }}</span>
                            </div>
                            <div v-else class="fc-black-22 pT35">---</div>
                          </div>
                        </div>
                        <div v-else>
                          <div class="text-center fc-black-14 pT60 pB60">
                            {{ $t('common._common.nodata') }}
                          </div>
                        </div>
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <div class="clearboth">
                  <!-- comments -->
                  <div class="white-bg-block new-summary-block mT20 pL30 pR30">
                    <div class="label-txt-black pT20 fw-bold">
                      {{ $t('maintenance.wr_list.comments') }}
                    </div>
                    <el-row
                      class="border-bottom4 pT20 pB20 visibility-visible-actions"
                      v-for="comment in comments"
                      :key="comment.id"
                    >
                      <el-col :span="22">
                        <div class="flLeft">
                          <avatar
                            size="sm"
                            :user="{
                              name: comment.createdBy
                                ? comment.createdBy.name
                                : 'Unknown User',
                            }"
                          ></avatar>
                        </div>
                        <div
                          class="flLeft"
                          style="
                              margin-top: 0px;
                              padding-left: 15px;
                              max-width: 90%;
                            "
                        >
                          <div class="flex-middle">
                            <div class="flLeft">
                              <span class="new-cmd-name">{{
                                comment.createdBy
                                  ? comment.createdBy.name
                                  : 'Unknown User'
                              }}</span>
                            </div>
                            <div
                              class="flLeft"
                              style="
                                  width: 6px;
                                  height: 6px;
                                  background: rgb(216, 216, 216);
                                  border-radius: 50%;
                                  margin-left: 23px;
                                "
                            ></div>
                            <div
                              style="
                                  font-weight: normal;
                                  font-style: normal;
                                  font-stretch: normal;
                                  line-height: normal;
                                  letter-spacing: 0.5px;
                                  color: #8ca1ad;
                                "
                              class="flLeft pL5 secondary-color f12 comment-time"
                            >
                              {{ comment.createdTime | fromNow }}
                            </div>
                            <div class="clearboth"></div>
                          </div>
                          <div style="pT5">
                            <div
                              class="markdown-style"
                              v-if="comment.bodyHTML != null"
                              v-html="comment.bodyHTML"
                            ></div>
                            <div
                              v-if="
                                $helpers.isLicenseEnabled('GOOGLE_TRANSLATION')
                              "
                            >
                              <div v-if="$account.user.appType === 3">
                                <InstantTranslate
                                  :content="comment.bodyText"
                                  v-if="woDescriptiontranslate"
                                ></InstantTranslate>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div class="clearboth"></div>
                      </el-col>
                      <el-col
                        :span="2"
                        class="mT5 pointer pointer"
                        v-if="(comment.createdBy || {}).id === $account.user.id"
                        @click="deleteNote(comment, index)"
                      >
                        <span @click="deleteNote(comment)">
                          <inline-svg
                            src="svgs/delete"
                            class="pointer edit-icon-color mL10 visibility-hide-actions"
                            iconClass="icon icon-xs fill-red"
                          ></inline-svg>
                        </span>
                      </el-col>
                    </el-row>
                    <el-row>
                      <el-col :span="24">
                        <div
                          class="row comment-area2 pL0 pR0 pT15"
                          id="commentBoxPar"
                        >
                          <div class="col-12">
                            <div class="markdown-inside">
                              <textarea
                                style="
                                  font-size: 13px;
                                  letter-spacing: 0.3px;
                                  color: #333333;
                                  border-color: #d0d9e2;
                                "
                                v-model="newComment.body"
                                :placeholder="
                                  $t('common._common.write_comment')
                                "
                                v-bind:class="{
                                  height75: commentFocus,
                                  height40: !commentFocus,
                                }"
                                class="comment-box"
                                autofocus
                                ref="commentBoxRef"
                                id="commentBoxPar"
                                @focus="focusCommentBoxtextarea"
                              />
                              <button
                                :class="[commentFocus ? 'show' : 'hide']"
                                @click="markdownhelplist"
                              >
                                <img
                                  src="~assets/markdown-mark.svg"
                                  class="pull-right pointer"
                                />
                              </button>
                            </div>
                            <div>
                              <button
                                :class="[commentFocus ? 'show' : 'hide']"
                                class="markdown-comment-btn marginL-auto"
                                @click="addComment"
                              >
                                {{ $t('common._common._comment') }}
                              </button>

                              <div v-if="workorder.requester" class="flLeft">
                                <input
                                  type="checkbox"
                                  v-model="newComment.notifyRequester"
                                  name="notifyRequester"
                                />
                                <label
                                  class="notify-req"
                                  for="notifyRequester"
                                  >{{ $t('common._common.notify') }}</label
                                >
                              </div>
                              <div class="clearboth"></div>
                            </div>
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <template v-if="this.markdownshow">
                  <markdown-help-list :markdownshow.sync="markdownshow">
                  </markdown-help-list>
                </template>
                <div class="clearboth">
                  <!-- attchments -->
                  <div class="white-bg-block new-summary-block pL30 pR30 mT20">
                    <div class="label-txt-black pT20 pB20 fw-bold">
                      {{ $t('maintenance.wr_list.attachments') }}
                    </div>
                    <attachments
                      module="ticketattachments"
                      :record="workorder"
                      @attachmentactivity="updateWoActions()"
                    ></attachments>
                  </div>
                </div>
              </div>
              <wo-field-details
                ref="wo-field-details"
                :workorder="workorder"
                :excludeFields="[]"
                :refreshWoDetails="refreshWoDetails"
                :updateWoActions="updateWoActions"
                :currentView="currentView"
              ></wo-field-details>
            </div>
          </el-tab-pane>

          <el-tab-pane
            class="clear"
            :label="$t('common.header.safety_plan')"
            name="safetyplan"
            data-test-selector="Safety_Plan"
            v-if="showHazardListWidget"
          >
            <div class="pm-summary-inner-container">
              <div
                class="fL pm-summary-inner-left-con mT20 fc-res-dell fc-wo-page-scroll scrollbar-style"
                v-bind:class="{
                  width71: !showAddPreRequest,
                  width66: showAddPreRequest,
                }"
              >
                <div
                  v-if="showHazardListWidget"
                  class="white-bg-block height300"
                >
                  <HazardListWidget
                    :widget="hazardWidget"
                    :details="workorder"
                    moduleName="workorder"
                    :staticWidgetHeight="'238'"
                  ></HazardListWidget>
                </div>
                <div
                  v-if="showHazardListWidget"
                  class="white-bg-block mT20 height300"
                >
                  <HazardPrecautions
                    :details="workorder"
                    moduleName="workorder"
                    :staticWidgetHeight="'342'"
                  ></HazardPrecautions>
                </div>
                <div
                  v-if="workorder.prerequisiteEnabled"
                  class="white-bg-block mT20"
                >
                  <div class="wo-widget-header">
                    <div class="header justify-content-space height100">
                      <div
                        class="widget-title d-flex flex-direction-column justify-center"
                      >
                        {{ $t('common.header.prerequisite_') }}
                      </div>
                    </div>
                  </div>
                  <div class="pL30 pB60 pR30">
                    <div class="label-txt-black pT40 pB20 bold">
                      {{ $t('common.wo_report.photos') }}
                    </div>
                    <el-upload
                      class="avatar-uploader"
                      list-type="picture-card"
                      multiple
                      :file-list="photos"
                      action=""
                      accept="image/*"
                      :http-request="onUpload"
                      :on-success="onUploadSuccess"
                      :on-remove="onPhotoDelete"
                    >
                      <i class="el-icon-plus"></i>
                    </el-upload>

                    <div
                      v-for="(section, index) in Object.keys(preRequestList)"
                      :key="index"
                      class="pT40"
                    >
                      <div class="label-txt-black p20 border-bottom4 fw-bold">
                        {{
                          preRequestSections[section]
                            ? preRequestSections[section].name
                            : ''
                        }}
                      </div>
                      <div
                        v-bind:class="{
                          selected: selectedPreRequest === task,
                          closed: task.status.type !== 'OPEN',
                        }"
                        v-for="(task, index1) in preRequestList[section]"
                        :key="index1"
                        class="fc__sum__task__list"
                      >
                        <div
                          class="task-item width100 flex-middle"
                          @click="showPreRequestDetails(task)"
                        >
                          <div class="dot-green mR10 mT3"></div>
                          <div
                            :class="[
                              {
                                closedTask: task.status.type !== 'OPEN',
                              },
                              'label-txt-black pointer',
                            ]"
                            style="width: 100%; word-wrap: break-word;"
                            type="text"
                            placeholder
                          >
                            {{ task.subject }}
                          </div>
                        </div>
                        <div
                          class="text-right width40 flex-middle justify-content-end"
                          v-if="task.inputType !== 1 && task.id !== -1"
                        >
                          <div
                            v-if="
                              task.inputType === 6 || task.inputType === '6'
                            "
                            class="flex-middle justify-content-end"
                          >
                            <el-radio-group
                              @change="
                                checkAndShowPreRequest(task)
                                addPreRequest(task)
                              "
                              v-model="task.inputValue"
                              :disabled="
                                task.status.type !== 'OPEN' || !canExecuteTask
                              "
                              class="fc-radio-btn"
                            >
                              <el-radio
                                color="secondary"
                                :label="task.truevalue"
                                :key="task.truevalue"
                                >{{ task.truevalue }}</el-radio
                              >
                              <el-radio
                                color="secondary"
                                :label="task.falsevalue"
                                :key="task.falsevalue"
                                >{{ task.falsevalue }}</el-radio
                              >
                            </el-radio-group>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div
                v-if="showAddPreRequest && selectedPreRequest.id !== -1"
                class="fR pm-summary-inner-right-con fc-v1-wo-sum-right fc-wo-summary-task-fixed width32-4"
              >
                <div class="fc-scrollbar-wrap pT20">
                  <div
                    class="white-bg-block fc__sum__task__details position-relative"
                  >
                    <div class>
                      <i
                        aria-hidden="true"
                        class="q-icon el-icon-close material-icons pull-right pointer wo__close__icon"
                        @click="closedisplayPreRequest"
                        :title="$t('common._common.close')"
                        v-tippy="{
                          arrow: true,
                          placement: 'top',
                        }"
                      ></i>
                      <div class="inline-flex">
                        <div class="pL30 pT20 pR30">
                          <div class="fc-black2-18">
                            {{
                              selectedPreRequest.subject
                                ? selectedPreRequest.subject
                                : ''
                            }}
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="mL30 mR30">
                      <div class="fc-black-13">
                        {{ selectedPreRequest.description }}
                      </div>
                      <div class="mT20">
                        <div
                          v-if="
                            selectedPreRequest.inputType === 6 ||
                              selectedPreRequest.inputType === '6'
                          "
                        >
                          <el-radio
                            @change="addPreRequest(selectedPreRequest)"
                            v-model="selectedPreRequest.inputValue"
                            class="fc-radio-btn"
                            color="secondary"
                            :label="selectedPreRequest.truevalue"
                            :disabled="
                              selectedPreRequest.status.type !== 'OPEN'
                            "
                            >{{ selectedPreRequest.truevalue }}</el-radio
                          >
                          <el-radio
                            @change="addPreRequest(selectedPreRequest)"
                            v-model="selectedPreRequest.inputValue"
                            class="fc-radio-btn"
                            color="secondary"
                            :label="selectedPreRequest.falsevalue"
                            :disabled="
                              selectedPreRequest.status.type !== 'OPEN'
                            "
                            >{{ selectedPreRequest.falsevalue }}</el-radio
                          >
                        </div>
                        <div class="taskoptions pB10 mT20">
                          <div class="taskoptions pB10 pT20 fc__wo__remarks">
                            <el-input
                              @change="addPreRequest(selectedPreRequest)"
                              type="text"
                              v-model="selectedPreRequest.remarks"
                              :disabled="
                                selectedPreRequest.status.type !== 'OPEN' ||
                                  !canExecuteTask
                              "
                              class="fc-input-full-border2 width100"
                              :placeholder="$t('common.products.add_remarks')"
                            ></el-input>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <wo-field-details
                v-else
                ref="wo-field-details"
                :workorder="workorder"
                :excludeFields="[]"
                :refreshWoDetails="refreshWoDetails"
                :updateWoActions="updateWoActions"
                :currentView="currentView"
              ></wo-field-details>
            </div>
          </el-tab-pane>

          <el-tab-pane
            class="clear"
            v-if="workorder.prerequisiteEnabled && !showHazardListWidget"
            :label="$t('common.header.prerequisite')"
            name="second1"
          >
            <div class="pm-summary-inner-container">
              <div
                class="pm-summary-inner-left-con fL mT20 fc-res-dell fc-wo-page-scroll scrollbar-style"
                v-bind:class="{
                  width71: !showAddPreRequest,
                  width66: showAddPreRequest,
                }"
              >
                <div>
                  <div class="fc-res-dell fc-wo-page-scroll scrollbar-style">
                    <div>
                      <div
                        class="white-bg-block pT40 pL30 pB60 pR30 height100 mB100"
                      >
                        <div class="label-txt-black pB20 bold">
                          {{ $t('common.wo_report.photos') }}
                        </div>
                        <el-upload
                          class="avatar-uploader"
                          list-type="picture-card"
                          multiple
                          :file-list="photos"
                          action=""
                          accept="image/*"
                          :http-request="onUpload"
                          :on-success="onUploadSuccess"
                          :on-remove="onPhotoDelete"
                        >
                          <i class="el-icon-plus"></i>
                        </el-upload>

                        <div
                          v-for="(section, index) in Object.keys(
                            preRequestList
                          )"
                          :key="index"
                          class="pT40"
                        >
                          <div
                            class="label-txt-black p20 border-bottom4 fw-bold"
                          >
                            {{
                              preRequestSections[section]
                                ? preRequestSections[section].name
                                : ''
                            }}
                          </div>
                          <div
                            v-bind:class="{
                              selected: selectedPreRequest === task,
                              closed: task.status.type !== 'OPEN',
                            }"
                            v-for="(task, index1) in preRequestList[section]"
                            :key="index1"
                            class="fc__sum__task__list"
                          >
                            <div
                              class="task-item width100 flex-middle"
                              @click="showPreRequestDetails(task)"
                            >
                              <div class="dot-green mR10 mT3"></div>
                              <div
                                :class="[
                                  { closedTask: task.status.type !== 'OPEN' },
                                  'label-txt-black pointer',
                                ]"
                                style="width: 100%; word-wrap: break-word;"
                                type="text"
                                placeholder
                              >
                                {{ task.subject }}
                              </div>
                            </div>
                            <div
                              class="text-right width40 flex-middle justify-content-end"
                              v-if="task.inputType !== 1 && task.id !== -1"
                            >
                              <div
                                v-if="
                                  task.inputType === 6 || task.inputType === '6'
                                "
                                class="flex-middle justify-content-end"
                              >
                                <el-radio-group
                                  @change="
                                    checkAndShowPreRequest(task)
                                    addPreRequest(task)
                                  "
                                  v-model="task.inputValue"
                                  :disabled="task.status.type !== 'OPEN'"
                                  class="fc-radio-btn"
                                >
                                  <el-radio
                                    color="secondary"
                                    :label="task.truevalue"
                                    :key="task.truevalue"
                                    >{{ task.truevalue }}</el-radio
                                  >
                                  <el-radio
                                    color="secondary"
                                    :label="task.falsevalue"
                                    :key="task.falsevalue"
                                    >{{ task.falsevalue }}</el-radio
                                  >
                                </el-radio-group>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div
                v-if="showAddPreRequest && selectedPreRequest.id !== -1"
                class="fR pm-summary-inner-right-con fc-v1-wo-sum-right fc-wo-summary-task-fixed width32-4"
              >
                <div class="fc-scrollbar-wrap pT20">
                  <div
                    class="white-bg-block fc__sum__task__details position-relative"
                  >
                    <div class>
                      <i
                        aria-hidden="true"
                        class="q-icon el-icon-close material-icons pull-right pointer wo__close__icon"
                        @click="closedisplayPreRequest"
                        :title="$t('common._common.close')"
                        v-tippy="{
                          arrow: true,
                          placement: 'top',
                        }"
                      ></i>
                      <div class="inline-flex">
                        <div class="pL30 pT20 pR30">
                          <div class="fc-black2-18">
                            {{
                              selectedPreRequest.subject
                                ? selectedPreRequest.subject
                                : ''
                            }}
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="mL30 mR30">
                      <div class="fc-black-13 text-left pT10">
                        {{ selectedPreRequest.description }}
                      </div>
                      <div class="mT20">
                        <div
                          v-if="
                            selectedPreRequest.inputType === 6 ||
                              selectedPreRequest.inputType === '6'
                          "
                        >
                          <el-radio
                            @change="addPreRequest(selectedPreRequest)"
                            v-model="selectedPreRequest.inputValue"
                            class="fc-radio-btn"
                            color="secondary"
                            :label="selectedPreRequest.truevalue"
                            :disabled="
                              selectedPreRequest.status.type !== 'OPEN'
                            "
                            >{{ selectedPreRequest.truevalue }}</el-radio
                          >
                          <el-radio
                            @change="addPreRequest(selectedPreRequest)"
                            v-model="selectedPreRequest.inputValue"
                            class="fc-radio-btn"
                            color="secondary"
                            :label="selectedPreRequest.falsevalue"
                            :disabled="
                              selectedPreRequest.status.type !== 'OPEN'
                            "
                            >{{ selectedPreRequest.falsevalue }}</el-radio
                          >
                        </div>
                        <div class="taskoptions pB10 mT20">
                          <div class="taskoptions pB10 pT20 fc__wo__remarks">
                            <el-input
                              @change="addPreRequest(selectedPreRequest)"
                              type="text"
                              v-model="selectedPreRequest.remarks"
                              :disabled="
                                !canExecuteTask ||
                                  selectedPreRequest.status.type !== 'OPEN'
                              "
                              class="fc-input-full-border2 width100"
                              :placeholder="$t('common.products.add_remarks')"
                            ></el-input>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <wo-field-details
                v-else
                ref="wo-field-details"
                :workorder="workorder"
                :excludeFields="[]"
                :refreshWoDetails="refreshWoDetails"
                :updateWoActions="updateWoActions"
                :currentView="currentView"
              ></wo-field-details>
            </div>
          </el-tab-pane>

          <el-tab-pane
            :label="$t('maintenance._workorder.tasks')"
            name="second"
            class="summaryTask-input"
          >
            <div
              v-if="
                isWorkOrderFeatureSettingsLicenseEnabled && canShowMessageBanner
              "
              class="wo_task_view_banner pT5"
            >
              <span class="wo_task_view_banner_subject">
                <fc-icon group="action" name="info"></fc-icon>
                <span class="pL5">{{ bannerMessage }}</span></span
              >
            </div>
            <div
              v-else-if="
                !isWorkOrderFeatureSettingsLicenseEnabled &&
                  !hasExecuteTaskPermission
              "
              class="wo_task_view_banner pT5"
            >
              <span class="wo_task_view_banner_subject">
                <fc-icon group="action" name="info"></fc-icon>
                <span class="pL5">{{
                  $t('common.products.workorder_no_update_task')
                }}</span></span
              >
            </div>
            <!-- Task Details Main Layout - Left Pane -->
            <div class="pm-summary-inner-container" id="summarytask">
              <div
                class="pm-summary-inner-left-con fL mT20 fc-res-dell fc-wo-page-scroll scrollbar-style"
                v-bind:class="{ width71: !showAddTask, width66: showAddTask }"
              >
                <div>
                  <!-- Task completion status chart with Close All Button -->
                  <div
                    class="p20 white-bg-block mB20"
                    v-if="
                      taskChartData &&
                        taskChartData.value &&
                        taskChartData.value > 0
                    "
                  >
                    <el-row>
                      <el-col
                        :span="
                          taskResources.length > 1 && taskResources.length <= 30
                            ? 14
                            : 15
                        "
                        class="mT10"
                      >
                        <div
                          class="fc__wo__task__bar fc__wo__task__bar-stacked"
                        >
                          <span
                            class="fc__task__gradient__bar"
                            :data="taskChartData"
                            :style="
                              'width:' +
                                (taskChartData.currentValue /
                                  taskChartData.value) *
                                  100 +
                                '%'
                            "
                          ></span>
                        </div>
                      </el-col>
                      <el-col :span="3" class="mT15">
                        <div class="flex-middle justify-content-center">
                          <div class="green-txt-12">
                            {{ taskChartData.currentValue }}
                          </div>
                          <div class="fc-grey-text12 pL3">
                            / {{ taskChartData.value }}
                          </div>
                        </div>
                      </el-col>
                      <el-col :span="5" class="text-right">
                        <el-button
                          type="button"
                          class="fc-pink-border-btn"
                          :disabled="
                            !allTaskStatus ||
                              !isPrerequisiteCompleted ||
                              !canExecuteTask
                          "
                          @click="closeAllTask"
                          v-if="
                            !(
                              taskChartData.currentValue === taskChartData.value
                            )
                          "
                        >
                          {{
                            selectedTaskResource
                              ? $t('maintenance._workorder.close_filtered_task')
                              : $t('maintenance._workorder.close_all_task')
                          }}
                        </el-button>
                        <div class="pT15 green-txt-13" v-else>
                          {{ $t('maintenance._workorder.all_task_closed') }}
                        </div>
                      </el-col>
                      <el-col
                        :span="1"
                        class="flRight mT15 pL20"
                        v-if="
                          taskResources.length > 1 && taskResources.length <= 30
                        "
                      >
                        <div
                          class="pointer user-select-none"
                          @click="
                            showTaskResourceFilter = !showTaskResourceFilter
                          "
                        >
                          <span class="rotate90">
                            <img
                              src="~statics/icons/equalization.svg"
                              style="width: 15px;"
                            />
                          </span>
                        </div>
                      </el-col>
                    </el-row>
                  </div>

                  <!-- Task Filter based on resource -->
                  <div
                    class="p10 white-bg-block mB20 flRight task-resource-filter"
                    style="width: 35%;"
                    v-if="showTaskResourceFilter"
                  >
                    <el-select
                      v-model="selectedTaskResource"
                      class="width100"
                      filterable
                      clearable
                    >
                      <el-option
                        v-for="resource in taskResources"
                        :key="resource.id"
                        :label="resource.name"
                        :value="resource.id"
                      ></el-option>
                    </el-select>
                  </div>
                  <div class="clearboth"></div>
                  <div
                    class="position-relative fc-visibility-gicon-visible height100"
                  >
                    <div>
                      <!-- Task List display -->
                      <div
                        class="white-bg-block mB20 pB40 pT20"
                        v-if="!isTaskListEmpty"
                      >
                        <!-- Add Task icon, when tasks is added directly, under no section -->
                        <div
                          class="item float_right_2"
                          v-if="Object.keys(sections).length === 0"
                        >
                          <el-tooltip
                            effect="dark"
                            :content="addTaskButtonMessage"
                            placement="top"
                          >
                            <span>
                              <el-button
                                v-if="
                                  workorderSourceTypeNotPlanned &&
                                    !isTaskListEmpty
                                "
                                :disabled="!showAddNewTask"
                                @click="addNewTask()"
                                data-test-selector="Add_Task"
                                icon="el-icon-plus"
                                class="p5"
                                type="text"
                              />
                            </span>
                          </el-tooltip>
                        </div>

                        <!-- List of Tasks based on sorted task list-->
                        <div
                          v-for="(obj, index) in getSortedTaskList()"
                          :key="index"
                          class="mT20"
                        >
                          <div
                            v-if="sections[obj.sectionId]"
                            class="label-txt-black p20 border-bottom4 fw-bold"
                          >
                            <div>
                              {{ sections[obj.sectionId].name }}
                            </div>
                            <!-- Add Task icon, when tasks is added under a specific section -->
                            <div class="float_right_1">
                              <el-tooltip
                                class="item"
                                effect="dark"
                                :content="addTaskButtonMessage"
                                placement="top"
                              >
                                <span>
                                  <el-button
                                    v-if="
                                      workorderSourceTypeNotPlanned &&
                                        !isTaskListEmpty
                                    "
                                    :disabled="!showAddNewTask"
                                    @click="addNewTaskToASection(obj.sectionId)"
                                    data-test-selector="Add_Task"
                                    icon="el-icon-plus"
                                    type="text"
                                  />
                                </span>
                              </el-tooltip>
                            </div>
                            <div
                              v-if="
                                $helpers.isLicenseEnabled('GOOGLE_TRANSLATION')
                              "
                            >
                              <div v-if="$account.user.appType === 3">
                                <InstantTranslate
                                  :content="sections[obj.sectionId].name"
                                  v-if="woDescriptiontranslate"
                                ></InstantTranslate>
                              </div>
                            </div>
                          </div>
                          <div
                            :disabled="!isPrerequisiteCompleted"
                            v-bind:class="{
                              selected: selectedTask === task,
                              closed: task.status.type !== 'OPEN',
                            }"
                            v-for="(task, index1) in obj.taskList"
                            :key="index1"
                            class="fc__sum__task__list width100"
                          >
                            <div
                              :disabled="
                                !isPrerequisiteCompleted || !canExecuteTask
                              "
                              @click="
                                isPrerequisiteCompleted && canExecuteTask
                                  ? changeTaskStatus(task)
                                  : null
                              "
                              style="cursor: pointer; padding-left: 5px;"
                              class="width40px"
                            >
                              <i
                                v-if="task.status.type === 'OPEN'"
                                :disabled="!isPrerequisiteCompleted"
                                class="fa fa-circle-thin"
                                style="color: #ff3184; font-size: 28px;"
                              ></i>
                              <i
                                v-else
                                :disabled="!isPrerequisiteCompleted"
                                class="el-icon-circle-check"
                                style="color: #ff3184; font-size: 28px;"
                              ></i>
                            </div>

                            <div
                              class="task-item width50"
                              style="padding-left: 10px; position: relative;"
                              @click="showTaskDetails(task)"
                            >
                              <el-input
                                v-if="task.id === -1"
                                style="width: 100%;"
                                v-model="task.subject"
                                type="text"
                                :placeholder="
                                  $t('maintenance._workorder.write_a_task_name')
                                "
                                :disabled="!canExecuteTask"
                              ></el-input>

                              <div v-else>
                                <div
                                  :class="[
                                    {
                                      closedTask: task.status.type !== 'OPEN',
                                    },
                                    'label-txt-black pointer',
                                  ]"
                                  style="width: 100%; word-wrap: break-word;"
                                  type="text"
                                  placeholder
                                >
                                  <span class="fc-id"
                                    >#{{ task.uniqueId }}</span
                                  >
                                  {{ task.subject }}
                                </div>
                                <div
                                  v-if="
                                    $helpers.isLicenseEnabled(
                                      'GOOGLE_TRANSLATION'
                                    )
                                  "
                                >
                                  <div v-if="$account.user.appType === 3">
                                    <InstantTranslate
                                      :content="task.subject"
                                      v-if="woDescriptiontranslate"
                                    ></InstantTranslate>
                                  </div>
                                </div>
                              </div>
                              <div
                                v-if="
                                  task.id === -1 ||
                                    (task.resource && task.resource.name)
                                "
                                class="pT5 inline-flex"
                              >
                                <div
                                  v-if="task.id === -1"
                                  @click="showSpaceAssetChooser()"
                                  class="flLeft"
                                  style="
                                      height: 10px;
                                      width: 10px;
                                      cursor: pointer;
                                    "
                                >
                                  <img src="~statics/icons/assetspace.svg" />
                                </div>
                                <div v-else class="flLeft">
                                  <img
                                    v-if="task.resource.resourceType === 1"
                                    src="~statics/space/space-resource.svg"
                                    style="height: 12px; width: 14px;"
                                  />
                                  <img
                                    v-else
                                    src="~statics/space/asset-resource.svg"
                                    style="height: 11px; width: 14px;"
                                  />
                                </div>
                                <div class="fc-grey2-text12 pL5">
                                  {{ task.resource.name || '' }}
                                </div>
                              </div>
                            </div>
                            <div
                              class="f-element text-right width50 wo-text-input-width flex-middle justify-content-end"
                              v-if="task.inputType !== 1 && task.id !== -1"
                            >
                              <div
                                v-if="
                                  task.inputType === 2 || task.inputType === '2'
                                "
                              >
                                <div
                                  v-if="
                                    task.readingField &&
                                      (task.readingField.dataTypeEnum ===
                                        'NUMBER' ||
                                        task.readingField.dataTypeEnum ===
                                          'DECIMAL') &&
                                      task.readingField.metric === 5
                                  "
                                  class="flex-middle justify-content-end"
                                >
                                  <el-input
                                    class="inline task-minutes-hours-input task-right-right-r-remove"
                                    type="number"
                                    @focus="checkAndShowTask(task)"
                                    @change="checkForHoursAndMins(task)"
                                    v-model="task.hours"
                                    :disabled="
                                      !canExecuteTask ||
                                        task.status.type !== 'OPEN' ||
                                        !isPrerequisiteCompleted
                                    "
                                  >
                                    <span
                                      slot="suffix"
                                      class="pR10 fc-grey6 f12 duration"
                                      >{{
                                        $t('maintenance.pm_list.hours')
                                      }}</span
                                    >
                                  </el-input>
                                  <el-input
                                    :ref="'fTaskMin' + task.id"
                                    class="inline task-minutes-hours-input mR10 task-right-left-r-remove"
                                    type="number"
                                    @focus="checkAndShowTask(task)"
                                    @change="checkForHoursAndMins(task)"
                                    v-model="task.minutes"
                                    :disabled="
                                      !canExecuteTask ||
                                        task.status.type !== 'OPEN' ||
                                        !isPrerequisiteCompleted
                                    "
                                  >
                                    <span
                                      slot="suffix"
                                      class="pR10 fc-grey6 f12 duration"
                                      >{{
                                        $t('maintenance.pm_list.minutes')
                                      }}</span
                                    >
                                  </el-input>
                                </div>
                                <div v-else class="position-relative">
                                  <div class="flex-middle justify-content-end">
                                    <div
                                      class="inline mR10"
                                      v-if="
                                        task.lastReading &&
                                          task.lastReading !== -1 &&
                                          task.lastReading !== '-1'
                                      "
                                    >
                                      <q-icon
                                        name="info_outline"
                                        class="info-icon mL10"
                                        size="18px"
                                        style="vertical-align: middle;"
                                        v-tippy="{
                                          html: '#prevpopup_' + task.id,
                                          position: 'bottom',
                                          arrow: true,
                                          theme: 'light',
                                          animation: 'scale',
                                        }"
                                        title
                                      ></q-icon>
                                    </div>
                                    <el-radio-group
                                      v-if="
                                        task.readingField &&
                                          isBooleanField(task.readingField)
                                      "
                                      v-model="task.inputValue"
                                      @change="onReadingChange(task)"
                                      :disabled="
                                        !canExecuteTask ||
                                          task.status.type !== 'OPEN' ||
                                          !isPrerequisiteCompleted
                                      "
                                      class="fc-radio-btn"
                                    >
                                      <el-radio
                                        class="pT15"
                                        color="secondary"
                                        label="1"
                                        >{{
                                          task.readingField.trueVal || 'True'
                                        }}</el-radio
                                      >
                                      <el-radio
                                        class="pT15"
                                        color="secondary"
                                        label="0"
                                        >{{
                                          task.readingField.falseVal || 'False'
                                        }}</el-radio
                                      >
                                    </el-radio-group>
                                    <el-select
                                      v-else-if="isReadingEnum(task)"
                                      v-model="task.inputValue"
                                      @change="onReadingChange(task)"
                                      :disabled="canDisableField(task)"
                                      class="fc-input-full-border-select2"
                                    >
                                      <el-option
                                        v-for="item in fieldOptions(task)"
                                        :key="item.id"
                                        :label="item.value"
                                        :value="item.id"
                                      ></el-option>
                                    </el-select>
                                    <el-input
                                      v-else
                                      class="input-with-select"
                                      :class="{
                                        'wo-task-group-input': isReadingFieldValidationEnabled,
                                        'wo-reading-input-width':
                                          task.readingFieldUnit < 0,
                                      }"
                                      type="number"
                                      @focus="checkAndShowTask(task)"
                                      @change="onReadingChange(task)"
                                      v-model="task.inputValue"
                                      :placeholder="
                                        $t(
                                          'maintenance._workorder.enter_reading'
                                        )
                                      "
                                      :disabled="
                                        !canExecuteTask ||
                                          task.status.type !== 'OPEN' ||
                                          !isPrerequisiteCompleted
                                      "
                                    >
                                      <el-select
                                        v-if="
                                          isReadingFieldValidationEnabled &&
                                            task.readingField &&
                                            task.readingField.metric > 0 &&
                                            task.readingFieldUnit > 0
                                        "
                                        v-model="task.readingFieldUnit"
                                        slot="append"
                                        @change="onReadingChange(task)"
                                        :disabled="!canExecuteTask"
                                      >
                                        <el-option
                                          v-for="(readingFieldUnit,
                                          index) in getReadingFieldUnits(
                                            task.readingField.metric
                                          )"
                                          :key="index"
                                          :label="readingFieldUnit.symbol"
                                          :value="readingFieldUnit.unitId"
                                        ></el-option>
                                      </el-select>
                                    </el-input>

                                    <div
                                      v-if="
                                        !isReadingFieldValidationEnabled &&
                                          task.readingField &&
                                          task.readingField.unit
                                      "
                                      class="inline pL8"
                                    >
                                      {{ task.readingField.unit }}
                                    </div>
                                  </div>

                                  <div class="time-error wo-time-error">
                                    <el-date-picker
                                      class="fc__wo__reading__time"
                                      :placeholder="
                                        $t(
                                          'maintenance._workorder.time_of_reading'
                                        )
                                      "
                                      v-model="task.inputTime"
                                      @change="updateReading(task)"
                                      :type="'datetime'"
                                      :editable="false"
                                      :disabled="
                                        !canExecuteTask ||
                                          task.status.type !== 'OPEN' ||
                                          !isPrerequisiteCompleted
                                      "
                                      :default-value="
                                        new Date(workorder.createdTime)
                                      "
                                      :picker-options="readingPickerOptions"
                                    ></el-date-picker>
                                  </div>

                                  <div
                                    v-if="isReadingFieldValidationEnabled"
                                    class="clearboth"
                                  >
                                    <div
                                      v-if="
                                        isReadingFieldValidationEnabled &&
                                          readingFieldErrorToggle
                                      "
                                    >
                                      <el-dialog
                                        v-if="task.id == currentTaskObj.id"
                                        :visible.sync="readingFieldErrorToggle"
                                        :title="$t('common.header.alert')"
                                        :fullscreen="false"
                                        :before-close="beforeCloseDialogBox"
                                        :append-to-body="true"
                                        width="46%"
                                        class="fc-dialog-center-container"
                                      >
                                        <div class="wo-error-dialog-body">
                                          <div>
                                            <AlertBanner
                                              :alert.sync="
                                                readingFieldErrorToggle
                                              "
                                              :alertMessage.sync="
                                                readingFieldError
                                              "
                                            >
                                            </AlertBanner>
                                          </div>
                                          <div
                                            v-if="readingFieldSuggestionToggle"
                                            class="mT20"
                                          >
                                            <div
                                              v-for="(readingFieldSuggestion,
                                              key) in readingFieldSuggestions"
                                              :key="key"
                                            >
                                              <WarningBanner
                                                :warning.sync="
                                                  readingFieldSuggestionToggle
                                                "
                                                :warningMessage.sync="
                                                  readingFieldSuggestion
                                                "
                                              ></WarningBanner>
                                            </div>
                                          </div>
                                          <div
                                            class="fc-pink f14 bold text-left pT20"
                                          >
                                            {{
                                              $t(
                                                'maintenance._workorder.reset_reading'
                                              )
                                            }}
                                          </div>

                                          <div class="mT20 mR5">
                                            <el-radio-group
                                              v-model="readingData"
                                            >
                                              <el-radio
                                                :label="3"
                                                class="fc-radio-btn"
                                                >{{
                                                  $t(
                                                    'maintenance._workorder.yes'
                                                  )
                                                }}</el-radio
                                              >
                                              <el-radio
                                                :label="6"
                                                class="fc-radio-btn"
                                                >{{
                                                  $t(
                                                    'maintenance._workorder.no'
                                                  )
                                                }}</el-radio
                                              >
                                            </el-radio-group>

                                            <el-row
                                              class="flex-middle pT20"
                                              v-if="readingData === 3"
                                            >
                                              <el-col :span="12">
                                                <div
                                                  class="pR20 bold text-left fc-black-13"
                                                >
                                                  {{ task.subject }}
                                                </div>
                                                <div
                                                  v-if="
                                                    $helpers.isLicenseEnabled(
                                                      'GOOGLE_TRANSLATION'
                                                    )
                                                  "
                                                >
                                                  <div
                                                    v-if="
                                                      $account.user.appType ===
                                                        3
                                                    "
                                                  >
                                                    <InstantTranslate
                                                      :content="task.subject"
                                                      v-if="
                                                        woDescriptiontranslate
                                                      "
                                                    ></InstantTranslate>
                                                  </div>
                                                </div>
                                              </el-col>
                                              <el-col :span="12">
                                                <el-input
                                                  class="fc-input-full-border"
                                                  :class="{
                                                    'wo-task-group-input': isReadingFieldValidationEnabled,
                                                    'wo-reading-input-width':
                                                      task.readingFieldUnit < 0,
                                                  }"
                                                  type="number"
                                                  @focus="
                                                    checkAndShowTask(task)
                                                  "
                                                  v-model="task.inputValue"
                                                  :placeholder="
                                                    $t(
                                                      'maintenance._workorder.enter_reading'
                                                    )
                                                  "
                                                  :disabled="!canExecuteTask"
                                                >
                                                  <el-select
                                                    v-if="
                                                      isReadingFieldValidationEnabled &&
                                                        task.readingField &&
                                                        task.readingField
                                                          .metric > 0 &&
                                                        task.readingFieldUnit >
                                                          0
                                                    "
                                                    v-model="
                                                      task.readingFieldUnit
                                                    "
                                                    slot="append"
                                                    class="fc-value-border"
                                                    :disabled="!canExecuteTask"
                                                  >
                                                    <el-option
                                                      v-for="(readingFieldUnit,
                                                      index) in getReadingFieldUnits(
                                                        task.readingField.metric
                                                      )"
                                                      :key="index"
                                                      :label="
                                                        readingFieldUnit.symbol
                                                      "
                                                      :value="
                                                        readingFieldUnit.unitId
                                                      "
                                                    ></el-option>
                                                  </el-select>
                                                </el-input>
                                              </el-col>
                                            </el-row>

                                            <div
                                              class="mT20"
                                              v-if="readingData === 3"
                                            >
                                              <div
                                                class="bold fc-black-13 text-left"
                                              >
                                                {{
                                                  $t(
                                                    'maintenance._workorder.enter_the_consumption'
                                                  )
                                                }}
                                              </div>
                                              <el-input
                                                :placeholder="
                                                  $t(
                                                    'common.placeholders.enter_the_consumption'
                                                  )
                                                "
                                                v-model="consumptionData"
                                                :disabled="!canExecuteTask"
                                                class="fc-input-full-border2 fc-width-input mT10 mR20"
                                              >
                                                <el-select
                                                  v-if="
                                                    isReadingFieldValidationEnabled &&
                                                      task.readingField &&
                                                      task.readingField.metric >
                                                        0 &&
                                                      task.readingFieldUnit > 0
                                                  "
                                                  v-model="
                                                    task.readingFieldUnit
                                                  "
                                                  slot="append"
                                                  class="width110px"
                                                  :disabled="!canExecuteTask"
                                                >
                                                  <el-option
                                                    v-for="(readingFieldUnit,
                                                    index) in getReadingFieldUnits(
                                                      task.readingField.metric
                                                    )"
                                                    :key="index"
                                                    :label="
                                                      readingFieldUnit.symbol
                                                    "
                                                    :value="
                                                      readingFieldUnit.unitId
                                                    "
                                                  ></el-option>
                                                </el-select>
                                              </el-input>
                                            </div>
                                          </div>

                                          <div class="modal-dialog-footer">
                                            <el-button
                                              v-if="readingData === 3"
                                              @click="
                                                closeReadingFieldErrorDialogBox
                                              "
                                              class="modal-btn-cancel"
                                            >
                                              {{
                                                $t(
                                                  'setup.users_management.cancel'
                                                )
                                              }}</el-button
                                            >
                                            <el-button
                                              v-if="readingData === 3"
                                              type="primary"
                                              class="modal-btn-save"
                                              :disabled="isDisabled"
                                              @click="onReadingChange(task)"
                                              >{{ $t('common._common._save') }}
                                            </el-button>
                                            <el-button
                                              v-else
                                              @click="
                                                closeReadingFieldErrorDialogBox
                                              "
                                              class="modal-btn-error uppercase"
                                            >
                                              {{
                                                $t('common._common.try_again')
                                              }}
                                            </el-button>
                                          </div>
                                        </div>
                                      </el-dialog>
                                    </div>
                                    <div
                                      v-else-if="
                                        isReadingFieldValidationEnabled &&
                                          readingFieldSuggestionToggle &&
                                          !readingFieldErrorToggle
                                      "
                                    >
                                      <el-dialog
                                        :visible.sync="
                                          readingFieldSuggestionToggle
                                        "
                                        :title="$t('common.header.note')"
                                        :fullscreen="false"
                                        :before-close="beforeCloseDialogBox"
                                        :append-to-body="true"
                                        key="1"
                                        width="41%"
                                        class="fc-dialog-center-container"
                                      >
                                        <div class="wo-error-dialog-body">
                                          <div
                                            v-for="(readingFieldSuggestion,
                                            key) in readingFieldSuggestions"
                                            :key="key"
                                          >
                                            <WarningBanner
                                              :warning.sync="
                                                readingFieldSuggestionToggle
                                              "
                                              :warningMessage.sync="
                                                readingFieldSuggestion
                                              "
                                            ></WarningBanner>
                                          </div>
                                          <div class="modal-dialog-footer">
                                            <el-button
                                              @click="
                                                closeReadingFieldSuggestionDialogBox
                                              "
                                              class="modal-btn-cancel"
                                            >
                                              {{
                                                $t(
                                                  'maintenance._workorder.back'
                                                )
                                              }}
                                            </el-button>
                                            <el-button
                                              type="primary"
                                              class="modal-btn-save"
                                              @click="
                                                callOnReadingChangeWithoutValidation(
                                                  currentTaskObj
                                                )
                                              "
                                            >
                                              {{
                                                $t(
                                                  'maintenance._workorder.continue_anyway'
                                                )
                                              }}
                                            </el-button>
                                          </div>
                                        </div>
                                      </el-dialog>
                                    </div>
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
                                      <span>
                                        ({{
                                          $t(
                                            'maintenance._workorder.previous_reading'
                                          )
                                        }})
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </div>
                              <el-input
                                v-if="
                                  task.inputType === 3 || task.inputType === '3'
                                "
                                type="text"
                                @focus="checkAndShowTask(task)"
                                :placeholder="
                                  $t('maintenance._workorder.enter_text')
                                "
                                @change="addTask(task)"
                                v-model="task.inputValue"
                                :disabled="
                                  !canExecuteTask ||
                                    task.status.type !== 'OPEN' ||
                                    !isPrerequisiteCompleted
                                "
                              ></el-input>
                              <el-input
                                v-if="
                                  task.inputType === 4 || task.inputType === '4'
                                "
                                type="number"
                                @focus="checkAndShowTask(task)"
                                :placeholder="
                                  $t(
                                    'maintenance._workorder.enter_numeric_value'
                                  )
                                "
                                @change="addTask(task)"
                                v-model="task.inputValue"
                                :disabled="
                                  !canExecuteTask ||
                                    task.status.type !== 'OPEN' ||
                                    !isPrerequisiteCompleted
                                "
                              ></el-input>
                              <span
                                v-if="
                                  task.inputType === 5 || task.inputType === '5'
                                "
                              >
                                <el-radio-group
                                  v-if="checkOptionsLength(task.options)"
                                  @change="
                                    checkAndShowTask(task)
                                    addTask(task)
                                  "
                                  v-model="task.inputValue"
                                  :disabled="
                                    !canExecuteTask ||
                                      task.status.type !== 'OPEN' ||
                                      !isPrerequisiteCompleted
                                  "
                                  class="fc-radio-btn"
                                >
                                  <el-radio
                                    v-for="(option, index) in task.options"
                                    class="pT15"
                                    color="secondary"
                                    :label="option"
                                    :key="index"
                                    >{{ option }}</el-radio
                                  >
                                </el-radio-group>
                                <el-select
                                  v-else
                                  @change="addTask(task)"
                                  @focus="checkAndShowTask(task)"
                                  v-model="task.inputValue"
                                  popper-class="f-dropdown"
                                  :disabled="canDisableField(task)"
                                >
                                  <el-option
                                    v-for="(option, index) in task.options"
                                    :key="index"
                                    :label="option"
                                    :value="option"
                                  ></el-option>
                                </el-select>
                              </span>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div
                        class="pT30 text-center white-bg-block pB30"
                        v-if="isTaskListEmpty"
                      >
                        <div class="mB10">
                          <inline-svg
                            src="svgs/emptystate/task"
                            iconClass="icon text-center icon-xxxlg"
                          ></inline-svg>
                        </div>
                        <div class="nowo-label">
                          <!-- {{ $t('maintenance._workorder.task_body_text') }} -->
                        </div>
                        <div class="pB20 pT20">
                          <el-tooltip
                            effect="dark"
                            :disabled="canShowAddTaskButton"
                            :content="addTaskButtonMessage"
                            placement="top"
                          >
                            <span>
                              <el-button
                                :disabled="!canShowAddTaskButton"
                                @click="addNewTask"
                                class="fc-btn-green-medium-fill"
                                data-test-selector="Add_Task"
                                >{{
                                  $t('maintenance._workorder.add_task')
                                }}</el-button
                              >
                            </span>
                          </el-tooltip>
                          <el-button
                            v-show="false"
                            @click="addNewTask"
                            class="fc-btn-green-medium-border mL10"
                            >{{
                              $t('maintenance._workorder.add_section')
                            }}</el-button
                          >
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- Task Details | Right Pane layout-->
            <div
              v-if="showAddTask && selectedTask.id !== -1"
              class="fR pm-summary-inner-right-con mT20 fc-wo-summary-task-fixed width32-4"
            >
              <div class="fc-scrollbar-wrap mT0">
                <div
                  class="white-bg-block fc__sum__task__details position-relative"
                >
                  <div class>
                    <i
                      aria-hidden="true"
                      class="q-icon el-icon-close material-icons pull-right pointer wo__close__icon"
                      @click="closedisplay"
                      :title="$t('common._common.close')"
                      v-tippy="{
                        arrow: true,
                        placement: 'top',
                      }"
                    ></i>
                    <div class="inline-flex">
                      <div class="pL30 pT20 pR30">
                        <div class="fc-black2-18">
                          {{ selectedTask.subject ? selectedTask.subject : '' }}
                        </div>
                        <div class="flex-middle mT10">
                          <div
                            v-if="selectedTask.resource"
                            class="pR5 vertical-middle"
                          >
                            <img
                              v-if="selectedTask.resource.resourceType === 1"
                              src="~statics/space/space-resource.svg"
                              style="height: 12px; width: 14px;"
                            />
                            <img
                              v-else
                              src="~statics/space/asset-resource.svg"
                              style="height: 11px; width: 14px;"
                            />
                          </div>
                          <div class="fc-grey2-text12">
                            {{
                              selectedTask.resource
                                ? selectedTask.resource.name
                                : ''
                            }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="mL30 mR30">
                    <div class="fc-black-13">
                      {{ selectedTask.description }}
                    </div>
                    <div class="mT20">
                      <div
                        v-if="
                          selectedTask.inputType === 2 ||
                            selectedTask.inputType === '2'
                        "
                      >
                        <div class="taskoptions pB10">
                          {{ $t('maintenance._workorder.enter') }}
                          {{ selectedTask.readingField.displayName }}
                          {{
                            selectedTask.readingField.unit
                              ? '(in ' + selectedTask.readingField.unit + ')'
                              : ''
                          }}
                        </div>
                        <el-radio-group
                          v-if="
                            selectedTask.readingField &&
                              isBooleanField(selectedTask.readingField)
                          "
                          v-model="selectedTask.inputValue"
                          @change="addTask(selectedTask)"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                          class="fc-radio-btn"
                        >
                          <el-radio class="pT15" color="secondary" label="1">{{
                            selectedTask.readingField.trueVal || 'True'
                          }}</el-radio>
                          <el-radio class="pT15" color="secondary" label="0">{{
                            selectedTask.readingField.falseVal || 'False'
                          }}</el-radio>
                        </el-radio-group>
                        <el-select
                          v-else-if="isReadingEnum(selectedTask)"
                          v-model="selectedTask.inputValue"
                          @change="addTask(selectedTask)"
                          :disabled="canDisableField(selectedTask)"
                          class="fc-input-full-border-select2 width100"
                        >
                          <el-option
                            v-for="item in fieldOptions(selectedTask)"
                            :key="item.id"
                            :label="item.value"
                            :value="item.id"
                          ></el-option>
                        </el-select>
                        <el-input
                          v-else
                          @change="addTask(selectedTask)"
                          v-model="selectedTask.inputValue"
                          type="number"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                          class="fc-input-full-border2"
                        ></el-input>
                        <el-date-picker
                          class="inputtime-detail fc-input-full-border2 width100 mT20"
                          v-model="selectedTask.inputTime"
                          @change="onReadingChange(selectedTask)"
                          :type="'datetime'"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                        ></el-date-picker>
                      </div>
                      <div
                        v-if="
                          selectedTask.inputType === 3 ||
                            selectedTask.inputType === '3'
                        "
                      >
                        <el-input
                          @change="addTask(selectedTask)"
                          v-model="selectedTask.inputValue"
                          type="text"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                          class="fc-input-full-border2"
                        ></el-input>
                      </div>
                      <div
                        v-if="
                          selectedTask.inputType === 4 ||
                            selectedTask.inputType === '4'
                        "
                      >
                        <el-input
                          @change="addTask(selectedTask)"
                          v-model="selectedTask.inputValue"
                          type="number"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                          class="fc-input-full-border2"
                        ></el-input>
                      </div>
                      <div
                        v-if="
                          selectedTask.inputType === 5 ||
                            selectedTask.inputType === '5'
                        "
                        class="f-element"
                      >
                        <div
                          v-for="(option, index) in selectedTask.options"
                          :key="index"
                        >
                          <el-radio
                            @change="addTask(selectedTask)"
                            v-model="selectedTask.inputValue"
                            class="pT15 fc-radio-btn"
                            color="secondary"
                            :label="option"
                            :disabled="
                              !canExecuteTask ||
                                selectedTask.status.type !== 'OPEN' ||
                                !isPrerequisiteCompleted
                            "
                            >{{ option }}</el-radio
                          >
                        </div>
                      </div>
                      <div
                        v-if="
                          selectedTask.inputType === 6 ||
                            selectedTask.inputType === '6'
                        "
                      >
                        <el-checkbox-group
                          v-model="selectedTask.inputValues"
                          @change="addTask(selectedTask)"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                        >
                          <el-checkbox
                            v-for="(option, index) in selectedTask.options"
                            class="newradio pT15 f-checkbox"
                            color="secondary"
                            :label="option"
                            :key="index"
                            >{{ option }}</el-checkbox
                          >
                        </el-checkbox-group>
                      </div>
                      <div class="taskoptions pB10 mT20">
                        <task-attachment-preview
                          module="taskattachments"
                          :isLockedState="isLockedState || !canExecuteTask"
                          :isTaskClosed="isTaskClosed(selectedTask)"
                          :record="selectedTask"
                          :disabled="canDisableAttachment(selectedTask)"
                          @taskattachmentactivity="updateWoActions()"
                          :key="selectedTask"
                        ></task-attachment-preview>
                      </div>
                      <div class="taskoptions pB10 pT20 fc__wo__remarks">
                        <el-input
                          @change="addTask(selectedTask)"
                          type="text"
                          v-model="selectedTask.remarks"
                          :disabled="
                            !canExecuteTask ||
                              selectedTask.status.type !== 'OPEN' ||
                              !isPrerequisiteCompleted
                          "
                          class="fc-input-full-border2"
                          :placeholder="$t('common.products.add_remarks')"
                        ></el-input>
                      </div>
                    </div>
                  </div>
                  <div class="fc__task__complete__btn">
                    <el-button
                      type="button"
                      class="fc-pink-btn-full-width"
                      :disabled="!isPrerequisiteCompleted || !canExecuteTask"
                      @click="changeTaskStatus(selectedTask)"
                      >{{ getButtonName(selectedTask) }}</el-button
                    >
                  </div>
                </div>
              </div>
            </div>

            <!--  Add task under a section | Right Pane layout-->
            <div
              v-else-if="showAddTask && selectedTask.id === -1"
              class="fR pm-summary-inner-right-con mT20 fc-wo-summary-task-fixed width32-4"
            >
              <div class="fc-scrollbar-wrap mT0">
                <div
                  class="white-bg-block fc__sum__task__details position-relative"
                >
                  <div>
                    <div
                      class
                      style="
                          padding-bottom: 25px;
                          padding-top: 17px;
                          padding-left: 30px;
                        "
                    >
                      <div class="fw-bold label-txt-black pB15">
                        {{ $t('maintenance._workorder.add_task') }}
                      </div>
                      <i
                        aria-hidden="true"
                        class="q-icon el-icon-close material-icons destroy pull-right pointer"
                        @click="closedisplay"
                      ></i>
                      <div class="inline-flex">
                        <div class="flLeft">
                          <i
                            class="el-icon-circle-check"
                            style="color: rgb(255, 49, 132); font-size: 28px;"
                          ></i>
                        </div>
                        <div class="flLeft">
                          <div class="taskheader">
                            <el-input
                              style="width: 100%;"
                              type="text"
                              v-model="selectedTask.subject"
                              :placeholder="
                                $t('maintenance._workorder.write_a_task_name')
                              "
                            ></el-input>
                          </div>
                          <div style="margin-top: -3px;">
                            <div
                              class="tasksubheader flLeft"
                              style="padding-left: 10px;"
                            >
                              {{ selectedTask.selectedResourceName }}
                            </div>
                            <div
                              class="flLeft"
                              style="
                                  height: 10px;
                                  width: 10px;
                                  margin-left: 10px;
                                  cursor: pointer;
                                "
                              @click="showSpaceAssetChooser()"
                            >
                              <img src="~statics/icons/assetspace.svg" />
                            </div>
                            <div class="clearboth"></div>
                          </div>
                        </div>
                        <div class="clearboth"></div>
                      </div>
                    </div>
                    <div
                      style="
                          margin-left: 30px;
                          margin-right: 30px;
                          margin-top: 30px;
                        "
                    >
                      <div>
                        <el-row align="middle">
                          <el-col :span="24">
                            <div class="label-txt-black fw-bold">
                              <div
                                class="taskdescinput"
                                style="
                                    font-size: 13px;
                                    letter-spacing: 0.4px;
                                    color: #6b7e91;
                                  "
                              >
                                {{ $t('maintenance._workorder.description') }}
                              </div>
                              <el-input
                                v-model="selectedTask.description"
                                class="form-item pT20 fc-input-full-border-select2"
                                type="textarea"
                              />
                            </div>
                          </el-col>
                        </el-row>
                        <div style="padding-top: 25px;" class="enableInput">
                          <el-checkbox
                            v-model="selectedTask.attachmentRequired"
                            >{{
                              $t('maintenance._workorder.photo')
                            }}</el-checkbox
                          >
                        </div>
                        <div style="padding-top: 25px;" class="enableInput">
                          <el-checkbox
                            v-model="selectedTask.enableInput"
                            @change="
                              selectedTask.inputType = !selectedTask.enableInput
                                ? 1
                                : selectedTask.inputType
                            "
                            >{{
                              $t('maintenance._workorder.enable_input')
                            }}</el-checkbox
                          >
                        </div>
                        <div v-if="selectedTask.enableInput" class="f-element">
                          <div
                            class="pT30"
                            style="
                                font-size: 13px;
                                letter-spacing: 0.4px;
                                color: #6b7e91;
                              "
                          >
                            {{ $t('maintenance._workorder.task_type') }}
                          </div>
                          <el-radio
                            @change="onSelectInput"
                            :disabled="
                              !selectedTask.resource ||
                                !selectedTask.resource.id ||
                                selectedTask.resource.id === -1 ||
                                !isPrerequisiteCompleted
                            "
                            v-model="selectedTask.inputType"
                            class="pT15 fc-radio-btn"
                            color="secondary"
                            label="2"
                          >
                            {{ $t('maintenance._workorder.reading') }}
                            {{
                              !selectedTask.resource ||
                              !selectedTask.resource.id ||
                              selectedTask.resource.id === -1
                                ? $t(
                                    'maintenance._workorder.select_asset_space'
                                  )
                                : ''
                            }}
                          </el-radio>
                          <div>
                            <el-radio
                              @change="onSelectInput"
                              v-model="selectedTask.inputType"
                              class="pT15 fc-radio-btn"
                              color="secondary"
                              label="3"
                              >{{ $t('maintenance._workorder.text') }}</el-radio
                            >
                          </div>
                          <el-radio
                            @change="onSelectInput"
                            v-model="selectedTask.inputType"
                            class="pT15 fc-radio-btn"
                            color="secondary"
                            label="4"
                            >{{
                              $t('maintenance._workorder.numeric')
                            }}</el-radio
                          >
                          <div>
                            <el-radio
                              @change="onSelectInput"
                              v-model="selectedTask.inputType"
                              class="pT15 fc-radio-btn"
                              color="secondary"
                              label="5"
                              >{{
                                $t('maintenance._workorder.option')
                              }}</el-radio
                            >
                          </div>
                          <el-radio
                            v-show="false"
                            @change="onSelectInput"
                            v-model="selectedTask.inputType"
                            class="pT15 fc-radio-btn"
                            color="secondary"
                            label="6"
                            >{{
                              $t('maintenance._workorder.checkbox')
                            }}</el-radio
                          >
                        </div>
                        <div
                          style="padding-top: 20px;"
                          v-if="selectedTask.inputType === '2'"
                        >
                          <div
                            style="
                                padding-top: 20px;
                                font-size: 13px;
                                letter-spacing: 0.4px;
                                color: #6b7e91;
                              "
                          >
                            {{ $t('maintenance._workorder.reading_field') }}
                          </div>
                          <div>
                            <el-select
                              v-model="selectedTask.readingFieldId"
                              style="width: 100%;"
                              class="form-item"
                              placeholder=" "
                            >
                              <el-option
                                v-for="readingField in taskReadingFields"
                                :key="readingField.id"
                                :label="readingField.displayName"
                                :value="readingField.id"
                              ></el-option>
                            </el-select>
                          </div>
                        </div>
                        <div
                          v-if="
                            selectedTask.inputType === '5' ||
                              selectedTask.inputType === '6'
                          "
                        >
                          <div
                            class="pT30"
                            style="
                                font-size: 13px;
                                letter-spacing: 0.4px;
                                color: #6b7e91;
                                padding-bottom: 10px;
                              "
                          >
                            {{ $t('maintenance._workorder.options') }}
                          </div>
                          <div
                            v-for="option in selectedTask.options"
                            style="padding-bottom: 10px;"
                            :key="option"
                          >
                            <div
                              v-if="selectedTask.inputType === '5'"
                              style="
                                  width: 14px;
                                  height: 14px;
                                  margin-top: 9px;
                                "
                              class="circle flLeft"
                            ></div>
                            <div
                              v-if="selectedTask.inputType === '6'"
                              style="
                                  width: 14px;
                                  height: 14px;
                                  margin-top: 9px;
                                  margin-right: 7px;
                                  border-radius: 3px;
                                  border: 1px solid #c4d1dd;
                                "
                              class="flLeft"
                            ></div>
                            <div
                              class="flLeft"
                              style="padding-left: 3px; width: 200px;"
                            >
                              <el-input
                                style="width: 100%;"
                                type="text"
                                v-model="option.name"
                                placeholder
                              ></el-input>
                            </div>
                            <div class="clearboth"></div>
                          </div>
                          <div
                            style="
                                margin-bottom: 30px;
                                font-size: 13px;
                                color: rgb(57, 178, 194);
                                cursor: pointer;
                                border-bottom: 1px solid rgb(57, 178, 194);
                                width: 70px;
                                padding-top: 10px;
                              "
                            @click="addTaskOption(selectedTask)"
                          >
                            {{ $t('maintenance._workorder.add_option') }}
                          </div>
                        </div>
                      </div>
                      <div class="p30">
                        <el-button
                          @click="addTask(selectedTask)"
                          data-test-selector="Save_Task"
                          class="btnsize txt fc-btn-green-medium-fill"
                          >{{ $t('maintenance._workorder.save') }}</el-button
                        >
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <wo-field-details
              v-else
              ref="wo-field-details"
              :workorder="workorder"
              :excludeFields="[]"
              :refreshWoDetails="refreshWoDetails"
              :updateWoActions="updateWoActions"
              :currentView="currentView"
            ></wo-field-details>

            <el-dialog
              :visible.sync="showMandatoryInputsDialog"
              :title="mandatoryInputsTitle"
              custom-class="fc-dialog-center-container"
            >
              <div v-if="showMandatoryInputsDialog" class="max-height500 pB100">
                <div v-if="addAttachmentRequiredDialog" class="pB20">
                  <mobile-attachment
                    module="taskattachments"
                    :record="selectedTask"
                    :disabled="
                      !canExecuteTask ||
                        selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    @marshalled="handlePhotoUpload($event)"
                    :doNotSave="true"
                  ></mobile-attachment>
                </div>
                <el-row v-if="addTaskRemarksDialog">
                  <el-col :span="24">
                    <p class="fc-input-label-txt">
                      {{ $t('common.wo_report.remarks') }}
                    </p>
                    <div>
                      <el-input
                        type="textarea"
                        v-model="selectedTask.remarks"
                        :autosize="{ minRows: 6, maxRows: 4 }"
                        :placeholder="$t('common.products.add_remarks')"
                        class="fc-input-full-border-textarea"
                        resize="none"
                        :disabled="!canExecuteTask"
                      ></el-input>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div class="modal-dialog-footer">
                <el-button
                  class="modal-btn-cancel"
                  @click="showMandatoryInputsDialog = false"
                  >{{ $t('common._common.cancel') }}</el-button
                >
                <el-button
                  class="modal-btn-save"
                  type="primary"
                  @click="
                    addTask(selectedTask), (showMandatoryInputsDialog = false)
                  "
                  >{{ $t('common._common._save') }}</el-button
                >
              </div>
            </el-dialog>
          </el-tab-pane>

          <el-tab-pane
            :label="$t('maintenance._workorder.items_and_labour')"
            name="third"
            v-if="
              $helpers.isLicenseEnabled('INVENTORY') &&
                !$helpers.isLicenseEnabled('PLANNED_INVENTORY') &&
                !aster
            "
          >
            <Inventory-summary
              :canDisable="canDisable"
              :workorder="workorder"
              v-if="activeName === 'third'"
              :openWorkorderId="openWorkorderId"
              :showLabour="true"
            ></Inventory-summary>
          </el-tab-pane>

          <el-tab-pane
            :label="$t('maintenance.pm_list.related_records')"
            name="fourth"
          >
            <div class="pm-summary-inner-container mT20 height100">
              <div class="width100 height100 fc-summary-content-scroll">
                <div>
                  <!-- Temp...will be removed under page builder -->
                  <WorkorderRelatedRecordsWidget
                    :id="workorder.id"
                    class="mB20"
                    v-if="$org.id === 406"
                  >
                  </WorkorderRelatedRecordsWidget>
                  <div v-if="invokeRelatedListWidget" class="white-bg-block">
                    <WoRelatedListWidget
                      :widget="woWidget"
                      :details="workorder"
                      moduleName="workorder"
                    ></WoRelatedListWidget>
                  </div>
                  <div v-if="showPermitListWidget" class="white-bg-block mT20">
                    <WorkPermitListWidget
                      :widget="permitWidget"
                      :staticWidgetHeight="'342'"
                      :details="workorder"
                      moduleName="workorder"
                    ></WorkPermitListWidget>
                  </div>

                  <WorkorderRelatedRecordsWidget
                    :id="workorder.id"
                    v-if="$org.id !== 406"
                  >
                  </WorkorderRelatedRecordsWidget>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane
            v-if="isNotPortal"
            :label="$t('maintenance.pm_list.history')"
            name="fifth"
          >
            <div class="pm-summary-inner-container mT20 height100">
              <div class="width100 height100 fc-summary-content-scroll">
                <div class="p30 white-bg-block pL80 pT30">
                  <activities
                    v-if="workorder.createdTime < 1553875578690"
                    :record="workorder"
                    :module="module_name"
                    ref="activitiesDiv"
                  ></activities>
                  <ActivitiesView
                    v-else
                    :record="workorder"
                    :module="module_name"
                    ref="activitiesDiv"
                  ></ActivitiesView>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <task-space-asset-chooser
        @associate="associate"
        :visibility.sync="visibility"
        :quickSearchQuery="quickSearchQuery"
        :filter="filter"
      ></task-space-asset-chooser>

      <work-hours
        v-if="showWorkDurationDialog"
        :visible.sync="showWorkDurationDialog"
        :id="workorder.id"
        :callback="workDurationCallBack"
      ></work-hours>

      <f-dialog
        v-if="customFieldUpdate"
        :visible="customFieldUpdate"
        :width="'30%'"
        :title="'Enter ' + selectedFieldUpdate.displayName + ' Value'"
        @save="updateCustomFields()"
        @close="closeUpdateDialog"
        :confirmTitle="'Update'"
        height="auto"
      >
        <div slot="content" class="pB30">
          <el-input
            type="textarea"
            :placeholder="
              'Type your' + ' ' + selectedFieldUpdate.displayName + ' Value'
            "
            v-if="
              selectedFieldUpdate.dataTypeEnum === 'TEXTAREA' ||
                selectedFieldUpdate.dataTypeEnum._name === 'TEXTAREA'
            "
            v-model="selectedFieldValue"
            class="fc-input-full-border-select2 line-height20"
            :autosize="{ minRows: 2, maxRows: 4 }"
            resize="none"
          ></el-input>
          <el-input
            type="textarea"
            :placeholder="
              'Type your' + ' ' + selectedFieldUpdate.displayName + ' Value'
            "
            v-else-if="
              selectedFieldUpdate.dataTypeEnum === 'STRING' ||
                selectedFieldUpdate.dataTypeEnum._name === 'STRING'
            "
            v-model="selectedFieldValue"
            class="fc-input-full-border-select2 line-height20"
            :autosize="{ minRows: 2, maxRows: 4 }"
            resize="none"
          ></el-input>
          <el-input
            type="number"
            :placeholder="
              'Type your' + ' ' + selectedFieldUpdate.displayName + ' Value'
            "
            v-else-if="
              selectedFieldUpdate.dataTypeEnum === 'NUMBER' ||
                selectedFieldUpdate.dataTypeEnum._name === 'NUMBER' ||
                selectedFieldUpdate.dataTypeEnum === 'DECIMAL' ||
                selectedFieldUpdate.dataTypeEnum._name === 'DECIMAL'
            "
            v-model="selectedFieldValue"
            class="fc-input-full-border-select2"
          ></el-input>

          <el-select
            v-else-if="
              field.dataTypeEnum === 'ENUM' ||
                field.dataTypeEnum._name === 'ENUM'
            "
            v-model="workorder.data[field.name]"
            @change="
              canEdit
                ? ((selectedFieldUpdate = field),
                  (selectedFieldValue = workorder.data[field.name]),
                  updateCustomFields())
                : null
            "
            class="fc-input-full-border-h35 width100"
          >
            <el-option
              v-for="(enumValue, index) in field.enumMap"
              :key="index"
              :label="enumValue"
              :value="parseInt(index)"
            ></el-option>
          </el-select>
        </div>
      </f-dialog>

      <workorder-options
        v-if="openTaskOptions"
        :workorders="[openWorkorderId]"
        :visibility.sync="openTaskOptions"
        :viewName="currentView"
        :isDownloadOption="isDownloadOption"
        :pdfUrl.sync="pdfUrl"
        :isPdfDownload="isPdfDownload"
      ></workorder-options>
    </div>
    <div v-else class="text-center width100 pT50 mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <pdf-download :url="pdfUrl" :isDownload.sync="isPdfDownload"></pdf-download>
  </div>
</template>
<script>
import Attachments from '@/relatedlist/SummaryAttachment2'
import WoRelatedListWidget from '@/relatedlist/ChildRelationListWidget'
import WorkPermitListWidget from '@/page/widget/common/RelatedListWidget'
import WorkorderRelatedRecordsWidget from '@/relatedlist/WorkorderRelatedRecordsWidget'
import HazardListWidget from '@/page/widget/common/CommonHazardList'
import Activities from '@/relatedlist/Activities2'
import ActivitiesView from '@/relatedlist/ActivitiesView'
import { mapState, mapGetters } from 'vuex'
import Avatar from '@/Avatar'
import FDialog from '@/FDialogNew'
import TaskSpaceAssetChooser from '@/SpaceAssetChooser'
import MobileAttachment from '@/MobileAttachment2'
import TaskAttachmentPreview from '@/TaskAttachmentPreview'
import WorkHours from 'pages/workorder/widgets/dialogs/WorkHours'
import FMiniChart from 'minicharts/components/FMiniChart'
import InventorySummary from 'pages/workorder/workorders/v1/inventory'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import AlertBanner from '@/AlertBanner'
import WarningBanner from '@/WarningBanner'
import moment from 'moment-timezone'
import AssetBreakdown from '@/AssetBreakdown'
import { QIcon } from 'quasar'
import { isEmpty } from '@facilio/utils/validation'
import workorderOptions from 'pages/workorder/workorders/v1/WorkOrderSummaryPdfOptions'
import TransitionButtons from '@/stateflow/TransitionButtons'
import HazardPrecautions from 'pages/safetyplan/component/SafetyPlanPrecautionsList'
import { eventBus } from '@/page/widget/utils/eventBus'
import WoFieldDetails from './WorkOrderFieldDetails'
import ApprovalBar from '@/approval/ApprovalBar'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { getFieldOptions } from 'util/picklist'
import { sanitize } from '@facilio/utils/sanitize'
import InstantTranslate from 'components/InstantTranslate'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isBooleanField, isEnumField } from '@facilio/utils/field'
import MarkdownHelpList from '@/relatedlist/MarkdownHelpList'
import PdfDownload from 'src/components/PDFDownload'
import { API } from '@facilio/api'
import { getApp } from '@facilio/router'

export default {
  name: 'WorkorderSummary',
  mixins: [workorderMixin],
  components: {
    Avatar,
    Activities,
    QIcon,
    Attachments,
    WoRelatedListWidget,
    WorkPermitListWidget,
    WorkorderRelatedRecordsWidget,
    FDialog,
    TaskSpaceAssetChooser,
    MobileAttachment,
    WorkHours,
    FMiniChart,
    InventorySummary,
    ActivitiesView,
    AssetBreakdown,
    AlertBanner,
    WarningBanner,
    workorderOptions,
    TransitionButtons,
    HazardListWidget,
    HazardPrecautions,
    WoFieldDetails,
    ApprovalBar,
    CustomButton,
    MarkdownHelpList,
    TaskAttachmentPreview,
    InstantTranslate,
    PdfDownload,
  },
  data() {
    return {
      taskErrorMap: {},
      invokeRelatedListWidget: false,
      woWidget: {
        relatedList: {
          module: {},
        },
      },
      permitWidget: {
        relatedList: {
          module: {},
          field: { name: 'ticket' },
        },
      },
      showPermitListWidget: false,
      nextExecutionTime: null,
      photos: [],
      onFocus: false,
      photoUploadUrl: null,
      photoUploadData: {},
      showAssetBreakdown: false,
      assetBDSourceDetails: {
        condition: null,
        assetid: null,
        sourceId: null,
        sourceType: null,
      },
      tasks: [],
      module_name: 'workorder',
      activeName: 'first',
      dialogVisible: false,
      showSiteEdit: true,
      validationRules: {},
      errorMap: {},
      errorQue: {},
      errorMessage: {},
      taskBulkList: [],
      fields: [],
      noOfUpdatedTAsk: 0,
      testVar: null,
      customFieldUpdate: false,
      visibility: false,
      quickSearchQuery: '',
      loading: true,
      summoryview: true,
      fieldvisibility: false,
      showWorkDurationDialog: false,
      workDurationCallBack: null,
      radio2: '',
      selectedFieldUpdate: null,
      selectedFieldValue: null,
      summaryWindow: 'true',
      subSection: 'summarypage',
      sequencedData: false,
      summarySection: 'details',
      taskReadingFields: [],
      editFormVisibilty: false,
      duedate: '',
      value1: '',
      subheaderMenu: [
        {
          label: this.$t('common.events.today'),
          path: { path: '/app/fa/events/today' },
        },
        {
          label: this.$t('common.events.yesterday'),
          path: { path: '/app/fa/events/yesterday' },
        },
        {
          label: this.$t('common.events.this_week'),
          path: { path: '/app/fa/events/thisweek' },
        },
        {
          label: this.$t('common.events.last_week'),
          path: { path: '/app/fa/events/lastweek' },
        },
        {
          label: this.$t('common.events.all_events'),
          path: { path: '/app/fa/events/all' },
        },
      ],
      comments: [],
      taskList: {},
      preRequestList: {},
      newTaskList: [],
      sections: {},
      preRequestSections: {},
      newComment: {
        parentModuleLinkName: 'ticketnotes',
        parentId: this.$route.params.id,
        body: null,
        notifyRequester: false,
      },
      commentFocus: true,
      newTask: {
        subject: '',
        description: '',
        resource: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        inputValidation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        id: -1,
      },
      showAddTask: false,
      selectedTask: {
        inputValidationRuleId: -1,
        inputValidation: null,
        safeLimitRuleId: -1,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        subject: '',
        selectedResourceName: '',
        inputValues: [],
      },
      showAddPreRequest: false,
      selectedPreRequest: {
        inputValidationRuleId: -1,
        inputValidation: null,
        safeLimitRuleId: -1,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        subject: '',
        selectedResourceName: '',
        inputValues: [],
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 3600 * 1000 * 24
        },
      },
      waitBoolean: true,
      downloadSummary: false,
      workOrderTotalCost: null,
      workOrderSubTotal: null,
      taskChartData: null,
      workOrderStates: [],
      activeWoStateData: null,
      selectedTaskResource: null,
      showTaskResourceFilter: false,
      metricWithUnits: {},
      readingFieldUnits: [],
      skipValidation: false,
      readingFieldErrorToggle: false,
      readingFieldError: null,
      currentValue: null,
      previousValue: null,
      readingFieldSuggestionToggle: false,
      readingFieldSuggestions: [],
      averageValue: [],
      suggestedUnits: [],
      currentTaskObj: null,
      addTaskRemarksDialog: false,
      showMandatoryInputsDialog: false,
      addAttachmentRequiredDialog: false,
      vendorsList: [],
      openTaskOptions: false,
      hazardWidget: {
        relatedList: {
          module: {},
          field: { name: 'workorder' },
        },
      },
      showHazardListWidget: false,
      currentTaskformData: {},
      POSITION: POSITION_TYPE,
      readingData: 6,
      consumptionData: '',
      markdownshow: false,
      woDescriptiontranslate: false,
      isDownloadOption: false,
      pdfUrl: '',
      workOrderSettingsRelatedData: {},
      isUserAddingTask: false,
    }
  },
  created() {
    this.isBooleanField = isBooleanField
    window.addEventListener('click', this.blurCommentBox)
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadApprovalStatus')
    this.$store.dispatch('view/loadModuleMeta', 'workorder')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
    eventBus.$on('woDescriptiontranslate', woDescriptiontranslate => {
      this.woDescriptiontranslate = woDescriptiontranslate
    })
    eventBus.$on('refreshWorkOrderPage', () => this.fetchWo(true))
  },
  destroyed() {
    eventBus.$off('woDescriptiontranslate', () => {
      this.woDescriptiontranslate = false
    })
    eventBus.$off('refreshWorkOrderPage')
  },
  mounted() {
    this.init()
    let woPromise = this.fetchWo(true)
    let taskPromise = this.loadTasks(this.workorder)
    this.loadComments()
    this.loadReadingFieldUnits()
    Promise.all([woPromise, taskPromise])
      .then(resp => {
        if (this.workorder.pm && this.workorder.pm.id) {
          this.getNextWorkOrderTime().then(resp => {
            if (!resp.data.result) {
              return
            }
            this.nextExecutionTime = resp.data.result.nextExecutionTime

            if (this.taskList) {
              let keys = Object.keys(this.taskList)
              for (let i = 0; i < keys.length; i++) {
                let tasks = this.taskList[keys[i]]
                for (let k = 0; k < tasks.length; k++) {
                  if (
                    this.workorder.pm &&
                    (!this.taskList[keys[i]][k].inputTime ||
                      this.taskList[keys[i]][k].inputTime === -1)
                  ) {
                    let nextExec = this.nextExecutionTime
                    if (nextExec && nextExec > 0) {
                      let currentTime = new Date().valueOf()
                      if (
                        !(
                          currentTime >= this.workorder.createdTime &&
                          currentTime < nextExec
                        )
                      ) {
                        this.taskList[keys[i]][
                          k
                        ].inputTime = this.convertTimeAsIfFromDatePicker(
                          this.workorder.createdTime
                        )
                      }
                    }
                  }
                }
              }
            }
          })
        }
      })
      .then(() => {
        this.loadWorkOrderFeatureSettings()
      })
    let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
    if (currentSiteId !== -1) {
      this.showSiteEdit = false
    }
    this.$http
      .get('v2/photos/prerequisitephotos?parentId=' + this.openWorkorderId)
      .then(response => {
        if (response.data && response.data.responseCode === 0) {
          this.photos = response.data.result.photos
            ? response.data.result.photos
            : []
        }
      })

    this.loadVendorsPicklist()
    eventBus.$on('refesh-parent', () => {
      this.fetchWo(true)
      this.loadWorkOrderFeatureSettings()
    })
    this.$root.$on('reloadWO', () => {
      this.fetchWo(true)
      this.loadWorkOrderFeatureSettings()
    })
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      ticketstatus: state => state.ticketStatus.workorder,
      moduleMeta: state => state.view.metaInfo,
      sites: state => state.site,
      hasWorkOrderClosePermission() {
        if (isWebTabsEnabled()) {
          return this.$hasPermission(
            `${this.moduleName}:UPDATE_CLOSE_WORKORDER`
          )
        }
        return this.$hasPermission(`${this.moduleName}:CLOSE_WORK_ORDER`)
      },
    }),
    ...mapGetters([
      'getTicketTypePickList',
      'getTicketPriority',
      'getApprovalStatus',
      'isStatusLocked',
    ]),
    isWorkOrderFeatureSettingsLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}
      return isLicenseEnabled('WORK_ORDER_FEATURE_SETTINGS')
    },
    //TODO: WORK_ORDER_FEATURE_SETTINGS license check
    canExecuteTask() {
      // -- can be removed later --
      let { isWorkOrderFeatureSettingsLicenseEnabled } = this
      if (!isWorkOrderFeatureSettingsLicenseEnabled) {
        let { hasExecuteTaskPermission } = this
        return hasExecuteTaskPermission
      }
      // -- can be removed later --

      let { workOrderSettings } = this
      let { executeTask } = workOrderSettings || {}
      let { allowed = false } = executeTask || {}
      return allowed
    },
    //TODO: WORK_ORDER_FEATURE_SETTINGS license check
    canAddTask() {
      // -- can be removed later --
      let { isWorkOrderFeatureSettingsLicenseEnabled } = this
      if (!isWorkOrderFeatureSettingsLicenseEnabled) {
        return true
      }
      // -- can be removed later --

      let { workOrderSettings } = this
      let { manageTask } = workOrderSettings || {}
      let { allowed = false } = manageTask || {}
      return allowed
    },
    workOrderSettings() {
      let { workOrderSettingsRelatedData } = this
      let { workOrderSettings = {} } = workOrderSettingsRelatedData || {}
      return workOrderSettings
    },
    canShowMessageBanner() {
      return !isEmpty(this.bannerMessage)
    },
    bannerMessage() {
      let message = null
      let {
        isTasksTab,
        isPlansTab,
        isActualsTab,
        workOrderSettingsRelatedData,
      } = this

      let { workOrderSettings } = workOrderSettingsRelatedData || {}

      // Tasks tab conditional messages
      if (isTasksTab) {
        let { executeTask } = workOrderSettings || {}
        let { reason, allowed } = executeTask || {}
        let { workorder } = this
        let { noOfTasks } = workorder || {}

        if (
          !allowed &&
          !isEmpty(reason) &&
          (isEmpty(noOfTasks) || noOfTasks <= 0)
        ) {
          /*
            -> True when executeTask is false, and there is no task in workorder 
            -> It's been done to not-to show "Task Execution not allowed" bannerMessage when there are no tasks.
           */
          message = null
        } else if (!isEmpty(reason)) {
          message = reason
        }
      } else if (isPlansTab) {
        // Plans tab conditional messages
        let { inventoryPlaning } = workOrderSettings || {}
        let { reason } = inventoryPlaning || {}
        if (!isEmpty(reason)) {
          message = reason
        }
      } else if (isActualsTab) {
        // Actuals tab conditional messages
        let { inventoryActuals } = workOrderSettings || {}
        let { reason } = inventoryActuals || {}
        if (!isEmpty(reason)) {
          message = reason
        }
      }
      return message
    },
    isTasksTab() {
      return this.activeName === 'second'
    },
    hasManageTaskPermission() {
      let { linkName } = getApp() || {}
      if (linkName === 'newapp') {
        return true
      } else {
        return this.$hasPermission('workorder:UPDATE_TASK')
      }
    },
    mandatoryInputsTitle() {
      if (this.addAttachmentRequiredDialog && this.addTaskRemarksDialog) {
        return 'Attach Photo and Add Remarks'
      }

      if (this.addAttachmentRequiredDialog) {
        return 'Attach Photo'
      }

      if (this.addTaskRemarksDialog) {
        return 'Add Remarks'
      }

      return ''
    },
    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
    aster() {
      const ASTER = 583
      const currentOrgID = this.$org.id

      return currentOrgID && currentOrgID === ASTER
    },
    readingPickerOptions() {
      if (
        !this.workorder.pm ||
        !this.nextExecutionTime ||
        this.nextExecutionTime <= 0
      ) {
        return {
          disabledDate(time) {
            return false
          },
        }
      }

      let startTime = this.$helpers
        .getOrgMoment(this.workorder.createdTime)
        .valueOf()
      let endTime = this.$helpers.getOrgMoment(this.nextExecutionTime).valueOf()

      return {
        disabledDate(time) {
          let res = !(
            (time.getTime() >= startTime && time.getTime() < endTime) ||
            (time.getTime() + 86400000 >= startTime && time.getTime() < endTime)
          )
          if (res === false) {
            console.log('time ' + time)
          }
          return res
        },
      }
    },
    tickettype() {
      return this.getTicketTypePickList()
    },
    isPrerequisiteCompleted() {
      return (
        !this.workorder.prerequisiteEnabled ||
        !this.workorder.allowNegativePreRequisite ||
        this.workorder.allowNegativePreRequisite === -1 ||
        !this.workorder.preRequestStatus ||
        this.workorder.preRequestStatus === -1 ||
        (this.workorder.preRequestStatus &&
          this.workorder.allowNegativePreRequisite === 2 &&
          (this.workorder.preRequestStatus === 2 ||
            this.workorder.preRequestStatus === 3)) ||
        (this.workorder.allowNegativePreRequisite === 3 &&
          this.workorder.preRequestStatus === 3)
      )
    },
    enablePrerequisiteApprove() {
      return (
        this.workorder.prerequisiteEnabled &&
        this.workorder.allowNegativePreRequisite &&
        this.workorder.allowNegativePreRequisite === 3 &&
        this.workorder.prerequisiteApprover &&
        !this.workorder.preRequisiteApproved &&
        this.workorder.preRequestStatus === 2
      )
    },
    isTenantEnabled() {
      return this.$helpers.isLicenseEnabled('TENANTS')
    },
    isReadingFieldValidationEnabled() {
      return this.$helpers.isLicenseEnabled('READING_FIELD_UNITS_VALIDATION')
    },
    canEdit() {
      let hasState = this.$getProperty(this.workorder, 'moduleState.id', null)
      let isLocked = this.isStatusLocked(
        this?.workorder?.moduleState?.id,
        'workorder'
      )
      let { isRequestedState } = this
      return hasState && !isLocked && !isRequestedState
    },
    openWorkorderId() {
      let id = this.$attrs.id || this.$route.params.id
      if (id) {
        return parseInt(id)
      }
      return -1
    },
    allTaskStatus() {
      let status = null
      let closedCount = 0
      let taskCount = 0
      for (let idx in this.taskList) {
        for (let idx2 in this.taskList[idx]) {
          taskCount = taskCount + 1

          let isOpen = this.taskList[idx][idx2].status.type === 'OPEN'
          let hasInputValue = this.taskList[idx][idx2].inputValue

          if (
            isOpen &&
            this.taskList[idx][idx2].inputType !== 1 &&
            !hasInputValue
          ) {
            if (status === null) {
              status = false
            }
            status = status && false
          } else {
            if (status === null) {
              status = true
            }
            status = status && true

            if (!isOpen) {
              closedCount = closedCount + 1
            }
          }
        }
      }
      if (closedCount === taskCount) {
        status = false
      }
      return status
    },
    openStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel(
        'Submitted',
        'workorder'
      ).id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    closedStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel('Closed', 'workorder')
        .id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    resolvedStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel(
        'Resolved',
        'workorder'
      ).id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    workorder() {
      return this.$store.state.workorder.currentWorkOrder
    },
    ticketStatusValue() {
      if (this.workorder && this.workorder.status && this.workorder.status.id) {
        return (
          this.$store.getters.getTicketStatus(
            this.workorder.status.id,
            'workorder'
          ) || ''
        )
      }
      return ''
    },
    inventory() {
      return this?.$store?.state?.inventory?.inventory ?? []
    },
    stockedTools() {
      return this?.$store?.state?.stockedtool?.stockedTools ?? []
    },
    taskResources() {
      let obj = {}
      if (this.taskList) {
        for (let idx in this.taskList) {
          for (let task of this.taskList[idx]) {
            if (task.resource && task.resource.id > 0) {
              obj[task.resource.id] = task.resource
            }
          }
        }
      }
      return Object.values(obj)
    },
    filter() {
      let filter = {}
      if (this.workorder.siteId && this.workorder.siteId > 0) {
        filter.site = Number(this.workorder.siteId)
      }
      return filter
    },
    allowEdit() {
      return false
    },
    isApprovalEnabled() {
      let { workorder = {} } = this
      let { approvalFlowId, approvalStatus } = workorder || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
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
    canDisable() {
      return this.isApprovalEnabled && !this.isRequestedState
    },
    currentView() {
      let viewName =
        this.$attrs.viewname || this.$route.params.viewname || 'all'

      return viewName
    },
    isDisabled() {
      return !this.consumptionData
    },
    canShowReportDownTime() {
      let { workorder, isNotPortal } = this
      let { resource } = workorder || {}
      let { resourceType, id } = resource || {}

      return isNotPortal && resourceType === 2 && id !== -1
    },
    isWoStateTransitionLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('WO_STATE_TRANSITION_V3')
    },
    updateUrlString() {
      if (!this.isWoStateTransitionLicenseEnabled) {
        return 'v2/workorders/update'
      }
      return null
    },
    isLockedState() {
      let { workorder } = this
      let { moduleState } = workorder || {}
      return this.$getProperty(moduleState, 'recordLocked', false)
    },
    hasAddTaskPermission() {
      return this.$hasPermission('workorder:UPDATE_TASK')
    },
    hasExecuteTaskPermission() {
      // hasUpdateTaskPermission changed to hasExecuteTaskPermission
      return this.$hasPermission('workorder:UPDATE_WORKORDER_TASK')
    },
    isUpdateTaskAllowed() {
      let { hasExecuteTaskPermission } = this
      return hasExecuteTaskPermission ? '' : 'pointer-events-none'
    },
    isUpdateAllowed() {
      let { hasExecuteTaskPermission } = this
      return hasExecuteTaskPermission ? '' : 'cursor-not-allowed'
    },
    isPdfDownload: {
      get() {
        let { openTaskOptions, isDownloadOption, pdfUrl } = this
        return openTaskOptions && isDownloadOption && !isEmpty(pdfUrl)
      },
      set(newValue) {
        if (!newValue) {
          this.$set(this, 'pdfUrl', '')
        }
        this.$set(this, 'openTaskOptions', newValue)
      },
    },
    isTaskListEmpty() {
      let { taskList } = this
      return isEmpty(taskList)
    },
    addTaskCheckBasedOnWorkOrderFeatureSetting() {
      let {
        hasAddTaskPermission,
        canAddTask,
        isWorkOrderFeatureSettingsLicenseEnabled,
      } = this
      let addTaskCheck = false
      if (!isWorkOrderFeatureSettingsLicenseEnabled) {
        addTaskCheck = hasAddTaskPermission
      } else {
        // TODO: after this license is removed entirely keep this part
        addTaskCheck = canAddTask
      }
      return addTaskCheck
    },
    workorderSourceTypeNotPlanned() {
      let { workorder } = this
      let { sourceType } = workorder || {}
      return !isEmpty(sourceType) && sourceType !== 5
    },
    canShowAddTaskButton() {
      let {
        workorderSourceTypeNotPlanned,
        addTaskCheckBasedOnWorkOrderFeatureSetting,
      } = this
      // can add the workorder planning setting check here (and) and move if to :disabled check
      return (
        workorderSourceTypeNotPlanned &&
        addTaskCheckBasedOnWorkOrderFeatureSetting
      )
    },
    addTaskButtonMessage() {
      let { showAddNewTask } = this
      if (showAddNewTask) {
        return 'Add Task'
      } else {
        // -- can be removed later --
        let { isWorkOrderFeatureSettingsLicenseEnabled } = this
        if (!isWorkOrderFeatureSettingsLicenseEnabled) {
          return 'You cannot add new Task!'
        }
        // -- can be removed later --
        let { workOrderSettings } = this
        let { manageTask } = workOrderSettings || {}
        let { reason } = manageTask || {}
        if (!isEmpty(reason)) {
          return reason
        }
      }
      return ''
    },
    showAddNewTask() {
      let {
        workorder,
        canEdit,
        taskList,
        resolvedStatusId,
        isUserAddingTask,
        addTaskCheckBasedOnWorkOrderFeatureSetting,
      } = this
      let { sourceType, moduleState } = workorder || {}
      let { id } = moduleState || {}
      return (
        // can add the workorder planning setting check here (and) and move if to :disabled check
        !isEmpty(sourceType) &&
        !isEmpty(taskList) &&
        sourceType !== 5 &&
        canEdit &&
        id !== resolvedStatusId &&
        !isUserAddingTask &&
        addTaskCheckBasedOnWorkOrderFeatureSetting
      )
    },
  },
  methods: {
    markdownhelplist() {
      this.markdownshow = true
    },
    convertTimeAsIfFromDatePicker(dateInMillis) {
      let currentBrowserTimeZone = Intl.DateTimeFormat().resolvedOptions()
        .timeZone
      return moment.tz(
        this.$helpers.getOrgMoment(dateInMillis).format('YYYY-MM-DD HH:mm:ss'),
        currentBrowserTimeZone
      )
    },
    getDescription(record) {
      let { description } = record
      return !isEmpty(description) ? sanitize(description) : '---'
    },
    onTransitionSuccess() {
      this.workorder.loadTimer = false
      this.refreshWoDetails()
      this.reloadRelatedRecords()
      this.onStateFlowFormSaved()
      this.loadTasks()
      this.reloadInventorySummary()
    },
    reloadRelatedRecords() {
      eventBus.$emit('refresh-related-list')
    },
    reloadInventorySummary() {
      eventBus.$emit('refresh-inventory-summary')
    },
    deleteNote(note) {
      let obj = {
        noteId: note.id,
        module: 'ticketnotes',
        parentModuleName: 'workorder',
      }
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_note'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_note_form_workorder'
          ),
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/notes/delete', obj)
              .then(({ data: { message, responseCode, result = {} } }) => {
                if (responseCode === 0) {
                  this.$message.success(
                    this.$t('common.products.note_deleted_successfully')
                  )
                  let doNote = this.comments.find(r => r.id === note.id)
                  if (doNote) {
                    this.comments.splice(this.comments.indexOf(doNote), 1)
                  }
                } else {
                  throw new Error(message)
                }
              })
          }
        })
    },
    handlePhotoUpload(attachment) {
      if (!this.selectedTask.attachment) {
        this.selectedTask.attachment = {}
      }

      this.selectedTask.attachment.module = attachment.module
      this.selectedTask.attachment.recordId = attachment.recordId
      if (attachment.attachmentType === 1) {
        this.selectedTask.attachment.beforeFile = attachment.file
        this.selectedTask.attachment.beforeFileName = attachment.fileName
      } else {
        this.selectedTask.attachment.afterFile = attachment.file
        this.selectedTask.attachment.afterFileName = attachment.fileName
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
    openPrintOptionDialog() {
      this.openTaskOptions = true
      this.isDownloadOption = false
    },
    openDownloadOptionDialog() {
      this.openTaskOptions = true
      this.isDownloadOption = true
    },
    init() {
      let { isWorkPermitLicenseEnabled } = this
      this.woWidget.relatedList.module = (this.moduleMeta || {}).module
      if (isWorkPermitLicenseEnabled) {
        this.$http.get('module/meta?moduleName=workPermit').then(response => {
          if (response.status === 200) {
            this.$setProperty(
              this,
              'permitWidget.relatedList.module',
              this.$getProperty(response, 'data.meta.module', null)
            )
            this.showPermitListWidget = true
          }
        })
      }
      this.loadSafetyPlanWidgets()
      this.invokeRelatedListWidget = true
    },
    loadSafetyPlanWidgets() {
      if (this.$helpers.isLicenseEnabled('SAFETY_PLAN')) {
        this.$http
          .get('module/meta?moduleName=workorderHazard')
          .then(response => {
            if (response.status === 200) {
              this.$setProperty(
                this,
                'hazardWidget.relatedList.module',
                this.$getProperty(response, 'data.meta.module', null)
              )
              this.$setProperty(
                this,
                'hazardWidget.relatedList.module.displayName',
                'Hazards'
              )
              this.showHazardListWidget = true
            }
          })
      }
    },
    updateReading(selectedTask) {
      if (
        selectedTask.inputTime &&
        this.nextExecutionTime &&
        this.nextExecutionTime > 0
      ) {
        let startTime = this.workorder.createdTime
        let endTime = this.nextExecutionTime

        if (
          !(
            selectedTask.inputTime.valueOf() >= startTime &&
            selectedTask.inputTime.valueOf() < endTime
          )
        ) {
          let s = this.$helpers.getOrgMoment(startTime).format('D MMM, h:mm a')
          let e = this.$helpers.getOrgMoment(endTime).format('D MMM, h:mm a')
          this.$message.error(
            this.$t('maintenance._workorder.task_input_time_invalid', [s, e])
          )
          return
        }
      }
      this.onReadingChange(selectedTask)
    },
    checkForHoursAndMins(task) {
      this.$nextTick(() => {
        if (
          this.$refs['fTaskMin' + task.id] &&
          this.$refs['fTaskMin' + task.id][0] &&
          document.activeElement ===
            this.$refs['fTaskMin' + task.id][0].$el.querySelector(
              '.el-input input.el-input__inner'
            )
        ) {
          console.log(
            document.activeElement,
            this.$refs['fTaskMin' + task.id][0].$el
          )
        } else {
          if (task && (task.hours || task.minutes)) {
            let seconds = moment
              .duration(
                `${task.hours ? task.hours : '00'}:${
                  task.minutes ? task.minutes : '00'
                }:00`
              )
              .asSeconds()
            task.inputValue = seconds
          }
          this.onReadingChange(task)
        }
      })
    },
    onUpload({ file, onSuccess, onError }) {
      let data = {
        parentId: this.openWorkorderId,
        module: 'prerequisitephotos',
      }

      let formData = new FormData()
      formData.append('file', file, file.name)
      for (let key in data) {
        this.$helpers.setFormData(key, data[key], formData)
      }

      return this.$http.post(`v2/photos/upload`, formData)
    },
    onUploadSuccess(response) {
      let {
        data: { responseCode, result },
      } = response

      if (responseCode === 0) {
        this.photos.push(result.photos[0])
        this.getPrerequisiteStatus()
        this.loadactivitie()
      }
    },
    onPhotoDelete(file) {
      this.deletePhoto(file)
    },
    focusCommentBoxtextarea() {
      this.$refs.commentBoxRef.focus()
      this.commentFocus = true
    },
    callOnReadingChangeWithoutValidation(currentTaskObj) {
      if (!isEmpty(currentTaskObj)) {
        this.onReadingChange(currentTaskObj, false)
        this.resetReadingFieldUnitDialogBox()
      }
    },
    beforeCloseDialogBox(done) {
      this.resetReadingFieldUnitDialogBox()
      done()
    },
    closeReadingFieldSuggestionDialogBox() {
      this.readingFieldSuggestionToggle = false
      this.readingFieldSuggestions = []
      this.currentTaskObj = null
    },
    closeReadingFieldErrorDialogBox() {
      this.resetReadingFieldUnitDialogBox()
      this.readingData = 6 // meterReset state is now false .
      this.consumptionData = ''
    },
    resetReadingFieldUnitDialogBox() {
      this.readingFieldErrorToggle = false
      this.readingFieldError = null
      this.readingFieldSuggestionToggle = false
      this.readingFieldSuggestions = []
      this.currentTaskObj = null
    },
    deletePhoto(value) {
      if (value) {
        this.$http
          .post('v2/photos/delete?module=prerequisitephotos', {
            id: value.id,
            parentId: value.parentId,
            photoId: value.photoId,
          })
          .then(response => {
            if (
              response.data &&
              response.data.result &&
              response.data.result.Id
            ) {
              let pht = this.photos.find(r => r.id === response.data.result.Id)
              if (pht) {
                this.photos.splice(this.photos.indexOf(pht), 1)
              }
              this.getPrerequisiteStatus()
              this.loadactivitie()
              this.$message.success(this.$t('common._common.delete_success'))
            } else {
              this.$message.error(
                this.$t('common._common.deletion_operation_failed')
              )
            }
          })
      }
    },
    getPrerequisiteStatus() {
      let self = this
      self.$http
        .get(
          'v2/workorders/getPrerequisiteStatus?workOrderId=' +
            self.openWorkorderId
        )
        .then(function(response) {
          self.workorder.preRequestStatus =
            response.data.result.preRequestStatus
        })
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
    updateWoActions() {
      this.loadactivitie()
    },
    refreshWoDetails() {
      this.fetchWo(true).then(() => {
        this.loadWorkOrderFeatureSettings()
      })
    },
    onStateFlowFormSaved() {
      this.updateWoActions()
      this.loadComments()
    },
    associate(selectedObj) {
      this.visibility = false
      this.selectedTask.resource = selectedObj
    },
    preRequisiteApprove() {
      this.updateWorkOrder([this.openWorkorderId], {
        preRequisiteApproved: true,
        preRequestStatus: 3,
      })
    },
    blurCommentBox(e) {
      //let itTargetPar = (e.path || []).filter(ele => ele.id === 'commentBoxPar')
      let { srcElement } = e || {}
      let { id } = srcElement || {}
      let isCommentBoxPar = id === 'commentBoxPar'
      if (
        (!this.newComment.body || this.newComment.body.trim() === '') &&
        !isEmpty(this.comments) &&
        !isCommentBoxPar
      ) {
        this.commentFocus = false
      }
    },
    loadReadingFieldUnits() {
      this.$http.get('v2/getReadingFieldUnits').then(response => {
        if (response.data && response.data.responseCode === 0) {
          this.metricWithUnits =
            response.data.result.readingFieldUnits.MetricsWithUnits
        }
      })
    },
    getReadingFieldUnits(readingFieldMetricId) {
      if (!isEmpty(this.metricWithUnits)) {
        let currentMetricId = Object.keys(this.metricWithUnits).find(
          metricUnitKey => metricUnitKey == readingFieldMetricId
        )
        let readingFieldUnits = this.metricWithUnits[currentMetricId]
        return readingFieldUnits
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
            routeName,
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
            params: { viewname: 'all', vendorid: data['vendors'].id },
          },
          custom: {
            name: 'custommodules-summary',
            params: {
              viewName: 'all',
              id: data[fieldName].id,
              moduleName: name,
            },
          },
        }
        let routerParams = {}

        if (name === 'vendors') routerParams = urlHash['vendors']
        else if (lookupModule.typeEnum === 'CUSTOM')
          routerParams = urlHash['custom']

        if (!isEmpty(routerParams)) {
          this.$router.push(routerParams)
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
    updateUrl(transition) {
      if (!isEmpty(transition)) {
        let stateObj = this.$store.getters.getTicketStatus(
          transition.toStateId,
          'workorder'
        )

        if (this.$getProperty(stateObj, 'type') === 'CLOSED') {
          return '/v2/workorders/close'
        } else return '/v2/workorders/update'
      }
    },
    transitionFilter(transition) {
      let stateObj = this.$store.getters.getTicketStatus(
        transition.toStateId,
        'workorder'
      )
      let isDisallowedState =
        stateObj.type === 'CLOSED' && !hasWorkOrderClosePermission
      return !isDisallowedState
    },
    handleTranslate() {
      this.woDescriptiontranslate = !this.woDescriptiontranslate
      eventBus.$emit('woDescriptiontranslate', this.woDescriptiontranslate)
    },
    isTaskClosed(task) {
      let type = this.$getProperty(task, 'status.type', '')
      return type !== 'OPEN'
    },
    canDisableAttachment(task) {
      let {
        isLockedState,
        isPrerequisiteCompleted,
        moduleMeta,
        canExecuteTask,
      } = this
      let type = this.$getProperty(task, 'status.type', '')
      let hideGallery = this.$getProperty(
        moduleMeta,
        'moduleSetting.hideGallery',
        false
      )
      return (
        type !== 'OPEN' ||
        !isPrerequisiteCompleted ||
        isLockedState ||
        hideGallery ||
        !canExecuteTask
      )
    },
    getButtonName(task) {
      return this.isTaskClosed(task)
        ? this.$t('maintenance._workorder.reopen_task')
        : this.$t('maintenance._workorder.complete_task')
    },
    fieldOptions(task) {
      let { readingField } = task || {}
      let options = []
      if (!isEmpty(readingField)) {
        let { enumMap } = readingField || {}
        options = Object.entries(enumMap || {}).map(([key, value]) => {
          return { id: key, value }
        })
      }
      return options
    },
    isReadingEnum(task) {
      let { readingField } = task || {}
      return readingField && isEnumField(readingField)
    },
    canDisableField(field) {
      let type = this.$getProperty(field, 'status.type', '')
      let { isPrerequisiteCompleted, canExecuteTask } = this
      return !canExecuteTask || !isPrerequisiteCompleted || type !== 'OPEN'
    },
    async loadWorkOrderFeatureSettings() {
      let { isWorkOrderFeatureSettingsLicenseEnabled } = this
      if (!isWorkOrderFeatureSettingsLicenseEnabled) {
        return
      }
      if (!isEmpty(this.workorder)) {
        let { workorder } = this
        let route = 'v3/workorders/features/' + workorder.id
        let { data, error } = await API.get(route)
        if (!isEmpty(data)) {
          let { workOrderSettingsRelatedData } = this

          workOrderSettingsRelatedData = {
            ...data,
          }

          this.$set(
            this,
            'workOrderSettingsRelatedData',
            workOrderSettingsRelatedData
          )
        } else if (!isEmpty(error)) {
          this.$message.error(
            error.message ||
              this.$t(
                common.workorder
                  .error_occurred_while_fetching_work_order_feature_settings_message
              )
          )
        }
      }
    },
  },
  watch: {
    workorder: function() {
      let title =
        '[#' + this.workorder.serialNumber + '] ' + this.workorder.subject
      this.setTitle(title)
      if (this.workorder.priority) {
        this.workorder.priority = this.getTicketPriority(
          this.workorder.priority.id
        )
      }
      if (!this.workorder.data) {
        this.workorder.data = {}
      }
      this.setDueDate(this.workorder.dueDate)
      this.updateTaskChartData()
    },
    openWorkorderId: function() {
      this.fetchWo(true)
      this.loadWorkOrderFeatureSettings()
    },
    filteredComments(list) {
      if (isEmpty(list)) this.commentFocus = true
    },
    moduleMeta(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.init()
      }
    },
    isActive(isActive) {
      if (!isActive && this.canShowAllComments) this.showAllComments()
    },
  },
  beforeDestroy() {
    this.$store.dispatch('workorder/removeCurrentWo')
  },
  filters: {
    getUser: function(id, users) {
      let userObj = users.find(user => user.id === id)
      if (userObj) {
        return userObj
      } else {
        return {
          name: 'System',
        }
      }
    },
    options: function(jsonobj, type) {
      let array = []
      if (type === 'category' || type === 'priority') {
        for (let jsonkey in jsonobj) {
          let val = jsonobj[jsonkey]
          array.push({ label: val, value: parseInt(jsonkey) })
        }
      } else if (type === 'user' || type === 'space') {
        for (let key in jsonobj) {
          array.push({ label: jsonobj[key].name, value: jsonobj[key].id })
        }
      }
      return array
    },
  },
}
</script>
<style lang="scss" scoped>
.float_right_1 {
  float: right;
  padding: 5px;
  margin-top: -20px;
}
.float_right_2 {
  float: right;
  margin-right: 10px;
  margin-top: -10px;
}

.wo-summary-container {
  margin: -40px 20px 0px;
}
/* Hacks for portal */
.fc-wo-page-scroll {
  height: calc(100vh - 200px);
}
.portal-wo-summary-page .fc-wo-page-scroll {
  height: calc(100vh - 150px);
}
.wo_task_view_banner {
  width: 100%;
  height: 36px;
  margin: 0px;
  background-color: #e7eff3;
  border-bottom: 1px solid #0078b9;
  text-align: center;
}

.wo_task_view_banner_subject {
  display: flex !important;
  align-items: center;
  height: 100%;
  font-size: 14px;
  font-weight: normal;
  line-height: 1.14;
  letter-spacing: 0.5px;
  overflow: hidden;
  word-break: break-all;
  -webkit-line-clamp: 1;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #324056;
  justify-content: center;
  align-items: center;
}
</style>
<style lang="scss">
.wo-summary-container .el-tabs__header {
  margin-bottom: 0;
}
.wo-summary-container.offset-top {
  margin-top: -96px;

  .el-tabs__content {
    margin-top: 58px;
  }

  .el-tabs__active-bar {
    top: 37px !important;
  }
}
.new-summary-view .fc__wo__task__bar > span {
  margin-left: 0px;
  /* For Safari 3.1 to 6.0 */
  -webkit-transition-property: width;
  -webkit-transition-duration: 1s;
  -webkit-transition-timing-function: ease-out;
  /* Standard syntax */
  transition-property: width;
  transition-duration: 1s;
  transition-timing-function: ease-out;
}
.wo__close__icon {
  padding-top: 20px;
  padding-right: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #324056;
}
.upload-space-photos {
  position: absolute;
  bottom: 5px;
  right: 0px;
  display: none;
  z-index: 10;
  width: 100%;
  text-align: center;
  background: #fff;
  padding: 5px;
}
.duration {
  bottom: 13px;
  /* left: 10px; */
  position: absolute;
  right: 1px;
}
.fc-avatar-element:hover .upload-space-photos {
  display: block;
}

.fc-avatar-carousel .el-carousel__container {
  height: 120px;
}
.avatar-uploader .el-upload--picture-card {
  width: 98px;
  height: 89px;
  line-height: 100px;
  border-radius: 4px;
  background-color: #f3f5f5;
  border: 1px solid rgba(50, 64, 86, 0.2);
  border-style: dashed;
}
.avatar-uploader .el-icon-plus {
  font-size: 22px;
  color: #324056;
  opacity: 0.5;
  font-weight: bold;
  vertical-align: baseline;
}
.avatar-uploader .el-upload-list--picture-card .el-upload-list__item {
  width: 98px;
  height: 89px;
}
.wo-summary-page .wo__details__res {
  padding-bottom: 20px;
  display: flex;
  align-items: center;
}
.task-input-width {
  width: 150px !important;
}
#summarytask .f-element .el-radio {
  width: inherit !important;
}
.wo-duration .fc-timer .t-separate {
  margin-top: 5px !important;
}
.wo-error-dialog-body {
  padding-bottom: 70px;
}
.wo-task-group-input .el-input-group__append {
  border: none;
}
.wo-task-group-input {
  width: 200px;
}
.wo-task-group-input .el-input__inner:first-child {
  width: inherit !important;
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
  border-right: 1px solid transparent !important;
}
.wo-task-group-input .el-input__inner:first-child:hover,
.wo-task-group-input .el-input__inner:first-child:active,
.wo-task-group-input .el-input__inner:first-child:focus {
  border-right: 1px #39b3c2 transparent !important;
}
.wo-task-group-input .el-input-group__append {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
}
.wo-task-group-input .el-input-group__append .el-input__inner,
.wo-task-group-input .el-input-group__append .el-select .el-input__inner {
  width: 60px !important;
  font-size: 12px !important;
  padding-left: 10px !important;
  border-left: none;
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
  border-right: 1px solid #d8dce5 !important;
}
.wo-task-group-input .el-input-group__append .el-select .el-input__inner {
  width: 100px;
}
#summarytask .wo-reading-input-width,
.wo-text-input-width .el-input__inner {
  width: 260px !important;
}
#summarytask .wo-reading-input-width .el-input__inner {
  border-right: 1px solid #d8dce5 !important;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
}
.summaryTask-input .task-minutes-hours-input .el-input__inner {
  width: 126px !important;
}
.task-right-right-r-remove .el-input__inner {
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}
.task-right-left-r-remove .el-input__inner {
  border-left: none !important;
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
}
.fc-wo-page-scroll {
  .wo-widget-header {
    border-bottom: 1px solid #f7f8f9;
    padding: 10px 15px;
    display: flex;
    flex-direction: column;
    .header {
      display: flex;
      min-height: 35px;
    }
  }
}
.new-cmd-discription {
  white-space: pre-line;
}
.fc-width-input .el-input__inner {
  width: 100%;
  border-right: none !important;
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}
.fc-width-input .el-select,
.fc-width-input .el-select .el-input__inner {
  width: 80px;
}

.fc-value-border .el-input__inner {
  border: 1px solid #d8dce5 !important;
}
.portal-wo-summary-page {
  .gTranslate-icon-place {
    padding: 10px 10px !important;
    position: absolute;
    top: 13px !important;
    left: -40px !important;
    right: inherit !important;
    cursor: pointer;
    border-radius: 4px;
    transition: all 0.3s;
    -webkit-transition: all 0.3s;
    background: #fff;
    border: 1px solid #518ef8;
  }
  .translateActive {
    border: 1px solid #518ef8;
    background: rgba(81, 142, 248, 0.1);
  }
}
</style>
