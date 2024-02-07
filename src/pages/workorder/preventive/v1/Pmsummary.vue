<template>
  <div class="pm-summary">
    <div class="fc-pm-summary-header">
      <div class="fL">
        <div class="flex-middle">
          <i
            @click="back"
            class="el-icon-back f18 pointer fc-black-color bold"
          ></i>
          <span class="fc-id bold pL10">#{{ pm && pm.id ? pm.id : '' }}</span>
          <div class="fc-separator-lg mL20 mR20"></div>
          <div class="heading-black22">
            {{ workorder ? workorder.subject : '' }}
          </div>
        </div>
      </div>
      <div class="fR show z-index9 position-relative">
        <div class="flex-middle">
          <div class="fc-summary-btn">
            <div class="pull-left row" style="padding-left:30px;">
              <router-link
                :to="goToEdit()"
                replace
                v-if="
                  $hasPermission('planned:UPDATE') ||
                    ($hasPermission('planned:UPDATE_OWN') && checkown()) ||
                    ($hasPermission('planned:UPDATE_TEAM') && checkteam)
                "
              >
                <div class="flLeft primarybtn" style="margin-right:10px;">
                  <q-btn small class="fc-wo-border-btn">
                    {{ $t('common._common.edit') }}
                  </q-btn>
                </div>
              </router-link>
              <el-button
                v-if="showExecuteNow()"
                @click="preWoValidation()"
                :disabled="isExecuting"
                type="primary"
                class="setup-el-btn edit-btn"
              >
                {{
                  isExecuting
                    ? $t('maintenance.pm_list.executing')
                    : $t('maintenance.pm_list.execute_now')
                }}
              </el-button>
            </div>
          </div>
        </div>
        <div
          class="pm-reading-vs-task-switch"
          v-if="activeName === 'third' && $route.query.pmreading"
        >
          <span @click="toggleReadingTable = true" v-if="!toggleReadingTable">{{
            $t('maintenance.pm_list.switch_to_reading')
          }}</span>
          <span
            @click="toggleReadingTable = false"
            v-else-if="!readingData.length && toggleReadingTable"
            >{{ $t('maintenance.pm_list.switch_to_task') }}</span
          >
          <span
            @click="updateReadingData()"
            v-else-if="readingData.length && toggleReadingTable"
            >{{ $t('maintenance.pm_list.save_data') }}</span
          >
        </div>
      </div>

      <div
        class="clearboth mT50 fc-pm-summary-tab"
        :class="[
          {
            'hide-overflow': activeName === 'second' || activeName === 'fourth',
          },
        ]"
      >
        <div
          v-if="showExtendScheduleBanner"
          class="fc-notify-msg-blue flex-middle pm-trigger-notify justify-content-center"
        >
          <InlineSvg
            src="svgs/info-2"
            iconClass="icon icon-sm mR10"
          ></InlineSvg>
          Maintenance jobs are planned only untill
          {{ (this.pm.woGeneratedUpto * 1000) | formatDate(true) }}. Would you
          like to schedule for later days ?
          <span class="fc-dark-blue4 f13 pointer pL5" @click="showExtension"
            >Extend Now</span
          >
        </div>
        <div
          v-if="showRefreshBanner"
          class="fc-notify-msg-blue flex-middle pm-trigger-notify justify-content-center"
        >
          <InlineSvg src="svgs/info-2" iconClass="icon icon-sm mR10"></InlineSvg
          >Schedule generation is underway. Would you like to refresh now ?
          <span class="fc-dark-blue4 f13 pointer pL5" @click="handleRefresh"
            >Refresh</span
          >
          <i v-if="refreshButtonLoading" class="el-icon-loading mL5"></i>
        </div>
        <el-tabs v-model="activeName" @tab-click="handleClick" class="pm-tabs">
          <!-- summary -->
          <el-tab-pane :label="$t('maintenance.pm_list.summary')" name="first">
            <div class="pm-summary-inner-container">
              <div
                class="fL pm-summary-inner-left-con mT20 pm-summary-tab-inner-left-con"
              >
                <div class="p30 white-bg-block">
                  <div class="heading-black18">{{ workorder.subject }}</div>
                  <div class="space-preline pm-summary-tab-subject">
                    {{ workorder.description ? workorder.description : '---' }}
                  </div>
                </div>

                <div class="pm-summary-card-container mT20">
                  <!-- first card -->
                  <div class="pm-summarycard-split-con mR20">
                    <div class="summary-card">
                      <p class="fc-black-11 fwBold text-uppercase">
                        {{ $t('maintenance.pm_list.next_scheduled') }}
                      </p>
                      <div class="summary-data-block pT10">
                        <div class="date-block">
                          <div v-if="pm.nextExecutionTime > 0">
                            <div class="fc-black2-18">
                              {{ pm.nextExecutionTime | formatDate(true) }}
                            </div>
                            <div class="fc-grey2 f13 pT5 text-center">
                              {{
                                pm.nextExecutionTime | formatDate(false, true)
                              }}
                            </div>
                          </div>
                          <div v-else>
                            <div class="fc-black3-16">---</div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!-- second card -->
                    <div class="summary-card">
                      <p class="fc-black-11 fwBold text-uppercase">
                        {{ $t('maintenance.pm_list.last_triggered') }}
                      </p>
                      <div class="summary-data-block pT10">
                        <div class="date-block">
                          <div v-if="workordersArr && workordersArr[0]">
                            <div class="fc-black2-18">
                              {{
                                workordersArr[0].createdTime | formatDate(true)
                              }}
                            </div>
                            <div class="fc-grey2 f13 text-center pT5">
                              {{
                                workordersArr[0].createdTime
                                  | formatDate(false, true)
                              }}
                            </div>
                          </div>
                          <div v-else>
                            <div class="fc-black3-16">---</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- third card -->
                  <div class="pm-summarycard-split-con">
                    <div class="summary-card">
                      <div v-if="pm.pmCreationType === 1">
                        <p
                          class="fc-black-11 bold text-uppercase"
                          v-if="workorder && workorder.resource"
                        >
                          NO OF {{ workorder.resource.resourceTypeEnum }}
                        </p>
                        <div class="summary-data-block pT0">
                          <div class="date-block">
                            <div v-if="pm.pmCreationType === 1">
                              <div v-if="workorder && workorder.resource">
                                <div
                                  class="label-txt-black fw3"
                                  style="font-size: 40px;"
                                >
                                  1
                                </div>
                                <div class="fc-grey2 f13">
                                  {{ workorder.resource.name }}
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div
                        v-else-if="
                          pm.pmCreationType === 2 && pm.assignmentType === 4
                        "
                      >
                        <p class="fc-black-11 fwBold text-uppercase">
                          {{ $t('maintenance.pm_list.associated_assets') }}
                        </p>
                        <div class="summary-data-block pT0">
                          <div class="date-block">
                            <div
                              v-if="
                                pm.pmCreationType === 2 &&
                                  pm.assignmentType === 4
                              "
                            >
                              <div
                                v-if="
                                  pm.pmIncludeExcludeResourceContexts &&
                                    pm.pmIncludeExcludeResourceContexts.length
                                "
                                class="label-txt-black"
                                style="font-size: 40px;"
                              >
                                <div
                                  class="label-txt-black fw3"
                                  style="font-size: 40px;"
                                >
                                  {{
                                    `${pm.pmIncludeExcludeResourceContexts.length}`
                                  }}
                                </div>
                                <div class="fc-grey2 f13" v-if="assetcategory">
                                  {{ assetCategoryName }}
                                </div>
                              </div>
                              <div v-else-if="pm.assetCategoryId > -1">
                                <div
                                  class="label-txt-black fw3"
                                  style="font-size: 40px;"
                                >
                                  {{ ` ${resources.length}` }}
                                </div>
                                <div class="fc-grey2 f13" v-if="assetcategory">
                                  {{
                                    `${
                                      assetcategory.find(
                                        i => i.id === Number(pm.assetCategoryId)
                                      ).name
                                    }`
                                  }}
                                </div>
                              </div>
                            </div>
                            <div v-else>
                              <div class="label-txt-black mT0">
                                {{
                                  $t('maintenance.pm_list.no_associated_assets')
                                }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div
                        v-else-if="
                          pm.pmCreationType === 2 && pm.assignmentType === 3
                        "
                      >
                        <p class="fc-black-11 fwBold text-uppercase">
                          {{ $t('maintenance.pm_list.associated_spaces') }}
                        </p>
                        <div class="summary-data-block pT0">
                          <div class="date-block">
                            <div
                              v-if="
                                pm.pmCreationType === 2 &&
                                  pm.assignmentType === 3
                              "
                            >
                              <div
                                v-if="
                                  pm.pmIncludeExcludeResourceContexts &&
                                    pm.pmIncludeExcludeResourceContexts.length
                                "
                                class="label-txt-black"
                                style="font-size: 40px;"
                              >
                                <div
                                  class="label-txt-black"
                                  style="font-size: 40px;"
                                >
                                  {{
                                    `${pm.pmIncludeExcludeResourceContexts.length}`
                                  }}
                                </div>
                                <div v-if="spacecategory" class="fc-grey2 f13">
                                  {{
                                    spacecategory[Number(pm.spaceCategoryId)]
                                  }}
                                </div>
                              </div>
                              <div v-else-if="pm.spaceCategoryId > -1">
                                <div
                                  class="label-txt-black"
                                  style="font-size: 40px;"
                                >
                                  {{ ` ${resources.length}` }}
                                </div>
                                <div class="fc-grey2 f13">
                                  {{ `${spacecategory[pm.spaceCategoryId]}` }}
                                </div>
                              </div>
                            </div>
                            <div v-else>
                              <div class="label-txt-black mT0">
                                {{
                                  $t('maintenance.pm_list.no_associated_spaces')
                                }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div
                        v-else-if="
                          pm.pmCreationType === 2 && pm.assignmentType === 1
                        "
                      >
                        <p class="fc-black-11 fwBold text-uppercase">
                          {{ $t('maintenance.pm_list.associated_spaces') }}
                        </p>
                        <div class="summary-data-block pT0">
                          <div class="date-block">
                            <div
                              v-if="
                                pm.pmCreationType === 2 &&
                                  pm.assignmentType === 1
                              "
                            >
                              <div
                                v-if="
                                  pm.pmIncludeExcludeResourceContexts &&
                                    pm.pmIncludeExcludeResourceContexts.length
                                "
                                class="label-txt-black"
                                style="font-size: 40px;"
                              >
                                <div
                                  class="label-txt-black"
                                  style="font-size: 40px;"
                                >
                                  {{
                                    `${pm.pmIncludeExcludeResourceContexts.length}`
                                  }}
                                </div>
                                <div class="fc-grey2 f13">
                                  {{ $t('space.sites.floors') }}
                                </div>
                              </div>
                              <div v-else-if="pm.spaceCategoryId > -1">
                                <div
                                  class="label-txt-black"
                                  style="font-size: 40px;"
                                >
                                  {{ ` ${resources.length}` }}
                                </div>
                                <div class="fc-grey2 f13">
                                  {{ $t('space.sites.floors') }}
                                </div>
                              </div>
                            </div>
                            <div v-else></div>
                          </div>
                        </div>
                      </div>
                      <div v-else>
                        <p class="fc-black-11 fwBold text-uppercase"></p>
                      </div>
                    </div>
                    <div class="summary-card">
                      <p class="fc-black-11 fwBold text-uppercase">
                        {{ $t('maintenance.pm_list.freq_assignedto') }}
                      </p>
                      <div class="summary-data-block pT10">
                        <div class v-if="freqAssignUser">
                          <user-avatar
                            size="md"
                            :user="freqAssignUser"
                            :name="false"
                          ></user-avatar>
                          <div
                            class="fc-black-14 pT5 bold"
                            style="padding-right: 0;"
                          >
                            {{ freqAssignUser.name }}
                          </div>
                        </div>
                        <div v-else-if="workflowResult.woFreqAssign.loading">
                          <spinner
                            :show="workflowResult.woFreqAssign.loading"
                            size="60"
                          ></spinner>
                        </div>
                        <div v-else>
                          <avatar size="xlg" :user="{ name: '---' }"></avatar>
                          <div class="fc-black-15 mT10"></div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="pm-summary-single-card-con mT20">
                  <div class="summary-card pm-summary-single-card">
                    <p class="fc-black-11 fwBold text-uppercase">
                      {{ $t('maintenance.pm_list.on_time_completion') }}
                    </p>
                    <div class="summary-data-block">
                      <div>
                        <div
                          v-if="workflowResult.woOnTime.loading"
                          class="flex-middle justify-content-center"
                        >
                          <spinner
                            :show="workflowResult.woOnTime.loading"
                            size="60"
                          ></spinner>
                        </div>
                        <div v-else class="pm-sum-pie-chart">
                          <pie
                            :percent="
                              workflowResult.woOnTime.result
                                ? Math.round(workflowResult.woOnTime.result)
                                : 0
                            "
                            width="73"
                          ></pie>
                          <div
                            v-if="previousMonthworkflowResult.woOnTime.result"
                            class="fc-black-12"
                            style="padding-top: 13px;"
                          >
                            <span class="fc-red2-16">
                              {{
                                previousMonthworkflowResult.woOnTime.result
                                  ? Math.round(
                                      previousMonthworkflowResult.woOnTime
                                        .result
                                    )
                                  : 0
                              }}%
                            </span>
                            till {{ endDate }}
                          </div>
                          <div
                            v-else-if="endDate > 0"
                            class="fc-black-12"
                            style="padding-top: 13px;"
                          >
                            <span class="fc-red2-16">0%</span>
                            till
                            {{ endDate }}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="summary-card pm-summary-single-card">
                    <p class="fc-black-11 fwBold text-uppercase">
                      {{ $t('maintenance.pm_list.average_work_duration') }}
                    </p>
                    <template
                      v-if="
                        averageDuration !== null ||
                          lastMonthAverageDuration !== null
                      "
                    >
                      <div class="flex-middle justify-center flex-col mT40">
                        <div v-if="averageDuration && averageDuration.days > 0">
                          <div class="flex-middle">
                            <div class>
                              <template
                                v-if="
                                  averageDuration && lastMonthAverageDuration
                                "
                              >
                                <i
                                  v-if="
                                    averageDuration.days >
                                      lastMonthAverageDuration.days
                                  "
                                  class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #2cb988;"
                                ></i>
                                <i
                                  v-else-if="
                                    averageDuration.days <
                                      lastMonthAverageDuration.days
                                  "
                                  class="el-icon-bottom fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #e87171;"
                                ></i>

                                <i
                                  class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #2cb988;"
                                ></i>
                              </template>
                            </div>
                            <div class="fc-black-com f36 fw3 pR20">
                              {{ averageDuration.days }}
                            </div>
                          </div>
                          <div class="fc-grey2 f13 pR10">
                            {{
                              averageDuration.days > 1
                                ? $t('maintenance.pm_list.days')
                                : 'day'
                            }}
                          </div>
                        </div>
                        <div
                          v-else-if="averageDuration && averageDuration.hours"
                        >
                          <div class="flex-middle align-center">
                            <div>
                              <i
                                v-if="
                                  lastMonthAverageDuration !== null &&
                                    averageDuration.hours >
                                      lastMonthAverageDuration.hours
                                "
                                class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                style="color: #2cb988;"
                              ></i>
                              <i
                                v-else-if="
                                  lastMonthAverageDuration !== null &&
                                    averageDuration.hours <
                                      lastMonthAverageDuration.hours
                                "
                                class="el-icon-bottom fc-black-15 vertical-middle fwBold width0 pR20"
                                style="color: #e87171;"
                              ></i>
                              <i
                                v-else
                                class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                style="color: #2cb988;"
                              ></i>
                            </div>
                            <div class="fc-black-com f36 fw3 pR20">
                              {{ averageDuration.hours }}
                            </div>
                          </div>
                          <div class="fc-grey2 f13 pR10">
                            {{ $t('maintenance.pm_list.hours') }}
                          </div>
                        </div>
                        <div v-else class="fc-black-com f36 fw3 mT40">
                          <i
                            class="el-icon-top fc-black-15 vertical-middle fwBold"
                          ></i>
                          00.00
                        </div>
                        <div
                          v-if="lastMonthAverageDuration !== null"
                          class="fc-black-12 mT40"
                        >
                          <span class>
                            {{
                              lastMonthAverageDuration.days !== 0
                                ? lastMonthAverageDuration.days +
                                  (lastMonthAverageDuration.days > 1
                                    ? ' days '
                                    : ' day ')
                                : ''
                            }}
                            {{
                              lastMonthAverageDuration.hours !== 0
                                ? lastMonthAverageDuration.hours +
                                  (lastMonthAverageDuration.hours > 1
                                    ? ' hours '
                                    : ' hour ')
                                : ''
                            }}
                          </span>
                          till {{ endDate }}
                        </div>
                        <div
                          v-else
                          class="fc-black-12"
                          style="padding-top: 13px;"
                        >
                          <span class="fc-red2-16">0</span>
                          till {{ endDate }}
                        </div>
                      </div>
                    </template>
                    <div
                      v-else-if="workflowResult.woAvgRes.loading"
                      class="flex-middle justify-content-center"
                    >
                      <spinner
                        :show="workflowResult.woAvgRes.loading"
                        size="60"
                      ></spinner>
                    </div>
                    <template class="fc-black3-16 mT40" v-else>
                      <div class="flex-middle justify-center flex-col mT30">
                        <div class="fc-black-com f36 fw3">
                          <InlineSvg
                            src="svgs/work-duration-empty"
                            iconClass="icon icon-xxxlg"
                            class="vertical-middle"
                          ></InlineSvg>
                        </div>
                        <div class="fc-black-12 mT40"></div>
                      </div>
                    </template>
                  </div>

                  <div
                    class="summary-card pm-summary-single-card"
                    style="border-right: transparent !important;"
                  >
                    <p class="fc-black-11 fwBold text-uppercase">
                      {{ $t('maintenance.pm_list.average_response_time') }}
                    </p>
                    <div class="flex-middle justify-center flex-col pT0">
                      <template
                        v-if="averageRes > 0 || previousMonthAverageRes > 0"
                      >
                        <div>
                          <div
                            v-if="
                              averageResponseTime.timedays &&
                                averageResponseTime.timedays > 0
                            "
                            class="flex-middle justify-center flex-col mT40"
                          >
                            <div class="flex-middle justify-center">
                              <div>
                                <i
                                  v-if="
                                    previousMonthAverageResponseTime.timedays &&
                                      averageResponseTime.timedays >
                                        previousMonthAverageResponseTime.timedays
                                  "
                                  class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #2cb988;"
                                ></i>
                                <i
                                  v-else-if="
                                    previousMonthAverageResponseTime.timedays &&
                                      averageResponseTime.timedays <
                                        previousMonthAverageResponseTime.timedays
                                  "
                                  class="el-icon-bottom fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #e87171;"
                                ></i>
                                <i
                                  v-else
                                  class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #2cb988;"
                                ></i>
                              </div>
                              <div class="fc-black-com f36 fw3 pR20">
                                {{ averageResponseTime.timedays }}
                              </div>
                            </div>
                            <div class="fc-grey2 f13">
                              {{ $t('maintenance.pm_list.days') }}
                            </div>
                          </div>
                          <div
                            v-else-if="
                              averageResponseTime.timehours &&
                                averageResponseTime.timehours > 0
                            "
                            class="flex-middle justify-center flex-col mT40"
                          >
                            <div class="flex-middle justify-center">
                              <i
                                v-if="
                                  previousMonthAverageResponseTime.timehours &&
                                    averageResponseTime.timehours >
                                      previousMonthAverageResponseTime.timehours
                                "
                                class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                style="color: #2cb988;"
                              ></i>
                              <i
                                v-else-if="
                                  previousMonthAverageResponseTime.timehours &&
                                    averageResponseTime.timehours <
                                      previousMonthAverageResponseTime.timehours
                                "
                                class="el-icon-bottom fc-black-15 vertical-middle fwBold width0 pR20"
                                style="color: #e87171;"
                              ></i>
                              <i
                                v-else
                                class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                style="color: #2cb988;"
                              ></i>
                              <div class="fc-black-com f36 fw3 pR20">
                                {{ averageResponseTime.timehours }}
                              </div>
                            </div>
                            <div class="fc-grey2 f13">
                              {{ $t('maintenance.pm_list.hours') }}
                            </div>
                          </div>
                          <div
                            v-else-if="averageResponseTime.timemins > 0"
                            class="flex-middle justify-center flex-col mT40"
                          >
                            <div class="flex-middle justify-center">
                              <div>
                                <i
                                  v-if="
                                    previousMonthAverageResponseTime.timemins &&
                                      averageResponseTime.timemins >
                                        previousMonthAverageResponseTime.timemins
                                  "
                                  class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #2cb988;"
                                ></i>
                                <i
                                  v-else-if="
                                    previousMonthAverageResponseTime.timemins &&
                                      averageResponseTime.timemins <
                                        previousMonthAverageResponseTime.timemins
                                  "
                                  class="el-icon-bottom fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #e87171;"
                                ></i>
                                <i
                                  v-else
                                  class="el-icon-top fc-black-15 vertical-middle fwBold width0 pR20"
                                  style="color: #2cb988;"
                                ></i>
                                <div class="fc-black-com f36 fw3 pR20">
                                  {{ averageResponseTime.timemins }}
                                </div>
                              </div>
                            </div>
                            <div class="fc-grey2 f13">
                              {{ $t('maintenance.pm_list.minutes') }}
                            </div>
                          </div>
                          <div v-else class="fc-black-com f36 fw3 mT40">
                            <i
                              class="el-icon-top fc-black-15 vertical-middle fwBold"
                            ></i>
                            00.00
                          </div>
                          <div
                            v-if="
                              previousMonthAverageResponseTime.timedays > 0 ||
                                previousMonthAverageResponseTime.timehours >
                                  0 ||
                                previousMonthAverageResponseTime.timemins > 0
                            "
                            class="fc-black-12 mT40"
                          >
                            <span class>
                              {{
                                previousMonthAverageResponseTime.timedays
                                  ? previousMonthAverageResponseTime.timedays +
                                    ' days till ' +
                                    endDate
                                  : previousMonthAverageResponseTime.timehours
                                  ? previousMonthAverageResponseTime.timehours +
                                    ' hours till ' +
                                    endDate
                                  : previousMonthAverageResponseTime.timemins
                                  ? previousMonthAverageResponseTime.timemins +
                                    ' minutes till ' +
                                    endDate
                                  : ''
                              }}
                            </span>
                          </div>
                          <div
                            v-else
                            class="fc-black-12"
                            style="padding-top: 13px;"
                          >
                            <span class="fc-red2-16">0</span>
                            till {{ endDate }}
                          </div>
                        </div>
                      </template>
                      <div
                        v-else-if="avgResponseLoading"
                        class="flex-middle justify-content-center"
                      >
                        <spinner :show="avgResponseLoading" size="60"></spinner>
                      </div>
                      <template class="fc-black3-16" v-else>
                        <div
                          class="flex-middle justify-center flex-col"
                          style="margin-top: 30px;"
                        >
                          <div class="fc-black-com f36 fw3">
                            <InlineSvg
                              src="svgs/work-duration-empty"
                              iconClass="icon icon-xxxlg"
                              class="vertical-middle"
                            ></InlineSvg>
                          </div>
                        </div>
                      </template>
                    </div>
                  </div>
                </div>

                <!-- attachement start-->
                <div class="white-bg-block mT20 width100">
                  <div
                    class="fc-black3-16 border-bottom9 pT20 pB20 pL30 pR30 height60px"
                  >
                    <div class="fL">
                      {{ $t('maintenance.wr_list.attachments') }}
                    </div>
                  </div>
                  <div
                    v-if="workorder.attachments && workorder.attachments.length"
                    class="pm-summary-attachement mT10 pB20"
                  >
                    <div
                      class="fc-attachment-row pL10 pR10 mR30 mL30 border-bottom7"
                      v-for="attachment in workorder.attachments"
                      :key="attachment.fileName"
                    >
                      <el-row class="pT10 pB10 flex-middle">
                        <el-col :span="1">
                          <img
                            src="~assets/picture.svg"
                            width="20px"
                            class="mT5"
                          />
                        </el-col>
                        <el-col :span="20">
                          <a
                            class="attachment-label text-left fc-black-13 show max-width300px textoverflow-ellipsis"
                            :href="attachment.previewUrl"
                            target="_blank"
                            >{{ attachment.fileName }}</a
                          >
                        </el-col>
                        <el-col :span="2">
                          <div class="attachment-sublabel">
                            <span class="comment-file">{{
                              attachment.fileSize | prettyBytes
                            }}</span>
                            <span
                              class="comment-file"
                              v-if="attachment.status === 1"
                            >
                              ,
                              {{ $t('maintenance.pm_list.updateding') }}...
                            </span>
                            <span
                              class="comment-file"
                              v-else-if="attachment.status === 2"
                              >, {{ $t('maintenance.pm_list.success') }}</span
                            >
                            <span class="comment-file" v-else>
                              {{ attachment.error }}
                            </span>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </div>
                  <div
                    v-else
                    class="text-center height200 nowo-label flex-middle justify-content-center flex-direction-column"
                  >
                    <inline-svg
                      src="svgs/emptystate/attachements"
                      iconClass="icon text-center icon-80"
                    ></inline-svg>
                    <div class="f14 nowo-label">
                      {{ $t('maintenance.pm_list.no_attachments') }}
                    </div>
                  </div>
                </div>
                <!-- attachement end  -->
                <div
                  v-if="showChart"
                  class="white-bg-block mT20 position-relative"
                >
                  <div class="pL30 pR20 pB15 pT20 fc-black3-16 border-bottom11">
                    {{ $t('maintenance.pm_list.average_work_duration') }}
                  </div>
                  <f-new-analytic-modular-report
                    ref="analyticReport"
                    :module="computedModule"
                    :reportid="null"
                    :hideTabs="true"
                    :showDatePicker="true"
                    :serverConfig.sync="serverConfig"
                    class="fc-pm-summary-chart-block"
                  ></f-new-analytic-modular-report>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <!-- Trigger -->
          <el-tab-pane
            v-if="pm.triggers && pm.triggers.length"
            :label="$t('maintenance.pm_list.trigger')"
            name="second"
            lazy
            class="position-relative"
          >
            <div
              v-if="showExtendScheduleBanner"
              class="width100"
              style="height: 38px;background: transparent;"
            ></div>
            <div class="pm-summary-inner-container mT20">
              <div class="white-bg-block">
                <el-tabs
                  v-model="triggertab"
                  @tab-click="handleClick"
                  class="trigger-tab"
                >
                  <el-tab-pane label="overview" name="overview">
                    <pm-triggers
                      :pm="pm"
                      :resources="resources"
                      :pmObject="pmObject"
                      v-if="activeName === 'second'"
                    ></pm-triggers>
                    <!-- resource planning -->
                  </el-tab-pane>
                  <el-tab-pane label="Asset Planner" name="asset" lazy>
                    <pm-summary-resource-planner
                      :pm="pm"
                      @viewChanged="handleViewChange"
                      ref="assetplanner"
                    ></pm-summary-resource-planner>
                  </el-tab-pane>

                  <el-tab-pane label="Staff Planner" name="staff" lazy>
                    <pm-summary-resource-planner
                      :pm="pm"
                      @viewChanged="handleViewChange"
                      isStaffPlanner
                      ref="staffplanner"
                    ></pm-summary-resource-planner>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane
            v-if="
              (newViewPreRequests && newViewPreRequests.length > 0) ||
                preRequests.length > 0
            "
            label="PREREQUISITES"
            name="fifth"
          >
            <div class="pm-summary-inner-container">
              <div
                class="fL pm-summary-inner-left-con mT20 white-bg-block"
                style="width: 70.5% !important;padding-bottom: 100px; height: 100vh; overflow-y: scroll;"
              >
                <div class="pL30 pR30 pT20">
                  <div class="label-txt-black">
                    {{ $t('maintenance.pm_list.validation') }}
                  </div>
                  <el-checkbox disabled v-model="photoMandatory" class="pT20">{{
                    $t('maintenance._workorder.photo')
                  }}</el-checkbox>
                  <el-checkbox
                    disabled
                    v-if="false"
                    v-model="allowNegative"
                    class="pT20"
                    >{{
                      $t('maintenance.pm_list.allow_user_approve')
                    }}</el-checkbox
                  >

                  <div v-if="allowNegativePreRequisite === 3">
                    <el-form
                      disabled
                      :model="workorder"
                      :label-position="'top'"
                    >
                      <div class="fc-input-label-txt pT20">
                        {{ $t('maintenance.pm_list.approver_title') }}
                      </div>
                      <el-form-item
                        prop="approvers"
                        class="mB10"
                        style="margin-left: -10px;"
                      >
                        <el-row
                          v-for="(approver, key) in approvers"
                          class="criteria-condition-block max-width650px"
                          :key="key"
                        >
                          <el-col :span="6">
                            <el-select
                              v-model="approver.sharingType"
                              placeholder="Select"
                              class="fc-input-full-border-select2 width100"
                            >
                              <el-option
                                v-for="(field, index) in approvalTypes"
                                :key="index"
                                :label="field.name"
                                :value="field.id"
                              ></el-option>
                            </el-select>
                          </el-col>
                          <el-col :span="6" class="mL20">
                            <el-select
                              v-model="approver.approversId"
                              placeholder="Select"
                              class="fc-input-full-border-select2 width100"
                            >
                              <el-option
                                v-for="(objList,
                                index) in approver.approvesDataType"
                                :key="index"
                                :label="objList.name"
                                :value="objList.id"
                              ></el-option>
                            </el-select>
                          </el-col>
                        </el-row>
                      </el-form-item>
                    </el-form>
                  </div>
                </div>

                <div class="pL30 pR30 pB40" v-if="isNewViewTempPreRequest">
                  <!-- task list show Start-->
                  <div class="task-tab-block mT0">
                    <div
                      v-for="(data, index) in newViewPreRequests"
                      :key="index"
                    >
                      <div
                        v-if="data.sectionName !== 'default'"
                        style="margin-top: 20px;padding-top: 10px;font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
                        :style="{
                          'border-bottom':
                            data.sectionName !== 'default'
                              ? '1px solid #eceef1'
                              : '0',
                        }"
                      >
                        {{ data.sectionName }}
                      </div>
                      <div
                        class="task-container"
                        v-for="(task, idx) in data.preRequests"
                        :key="idx"
                        @click="showDetailsPreRequest(task)"
                      >
                        <div
                          :class="[
                            'task-list',
                            { 'selected-task': selectedPreRequest === task },
                          ]"
                          class="pL10"
                        >
                          <div class="task-content">
                            <i
                              class="fa fa-circle"
                              style="color: #39b2c2; font-size: 10px; padding-right: 5px;position:relative;width: 20px;float: left;"
                              aria-hidden="true"
                            ></i>
                            <div style="width: 75%;">
                              {{ task.subject ? task.subject : '' }}
                            </div>
                            <div class="flex-middle mR10 justify-content-end">
                              <div class="flex-middle">
                                <div class="dot-green-postivite"></div>
                                <div class="pL5 label-txt-black">
                                  {{ task.truevalue }}
                                </div>
                              </div>
                              <div class="flex-middle pL30">
                                <div class="dot-red-negative"></div>
                                <div class="pL5 label-txt-black">
                                  {{ task.falsevalue }}
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div
                      v-if="!isNewViewTempPreRequest"
                      v-for="(section, sectionname, index) in tasks"
                      :key="index"
                    >
                      <div
                        v-if="sectionname !== 'default'"
                        style="padding-top:35px; font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
                        :style="{
                          'border-bottom':
                            sectionname !== 'default'
                              ? 'solid 1px #d9f4f7'
                              : '0',
                        }"
                      >
                        {{ sectionname }}
                      </div>
                      <div
                        class="task-container"
                        v-for="(task, idx) in section"
                        :key="idx"
                        @click="showDetailsPreRequest(task)"
                      >
                        <div
                          :class="[
                            'task-list',
                            { 'selected-task': selectedPreRequest === task },
                          ]"
                          class="fc-list-hover-task"
                        >
                          <div class="task-content">
                            <i
                              class="fa fa-circle"
                              style="color: #b8e5eb; font-size: 10px; padding-right: 5px;position:relative;width: 20px;float: left;"
                              aria-hidden="true"
                            ></i>
                            <div class="task-content-item">
                              {{ task.subject ? task.subject : '' }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div
                class="fR pm-summary-inner-right-con fc-v1-wo-sum-right width28"
                v-if="!toggleReadingTable"
              >
                <div class="fc-scrollbar-wrap pT10">
                  <div
                    class="p20 white-bg-block pm-summary-right-bg height100vh"
                    v-if="selectedPreRequest"
                  >
                    <div class="pB70">
                      <div class="task-details-header position-relative">
                        <div
                          class="tdetail-title-block fc-tdetail-title-block fR"
                        >
                          <i
                            class="el-icon-close tclose-icon text-left wo__close__icon"
                            @click="closedisplay"
                            title="Close"
                            v-tippy="{
                              arrow: true,
                              animation: 'shift-away',
                              placement: 'top',
                            }"
                          ></i>
                        </div>
                        <div class="task-details-list clearboth">
                          <div class="task-detail-content">
                            <div class="task-list-item label-txt-black bold">
                              {{ selectedPreRequest.subject }}
                            </div>
                          </div>
                        </div>
                      </div>
                      <div>
                        <div>
                          <div v-if="selectedPreRequest.description">
                            <div class="fc-blue-label">
                              {{ $t('maintenance._workorder.description') }}
                            </div>
                            <el-input
                              disabled
                              v-model="selectedPreRequest.description"
                              class="form-item mT10"
                              type="textarea"
                            />
                          </div>
                          <div
                            v-else-if="!selectedPreRequest.description"
                          ></div>
                        </div>
                        <div v-if="selectedPreRequest.inputType === '6'">
                          <div class="pT10 label-txt-black clearboth">
                            {{ $t('maintenance._workorder.options') }}
                          </div>
                          <div class="taskoption pT20">
                            <div>
                              <el-input
                                disabled
                                class="woidth100 fc-input-full-border2"
                                type="text"
                                v-model="selectedPreRequest.truevalue"
                                placeholder
                              ></el-input>
                            </div>
                            <div class="pT20">
                              <el-input
                                disabled
                                type="text"
                                class="width100 fc-input-full-border2"
                                v-model="selectedPreRequest.falsevalue"
                                placeholder
                              ></el-input>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <!-- Tasks -->
          <el-tab-pane :label="$t('maintenance._workorder.tasks')" name="third">
            <div class="pm-summary-inner-container">
              <div
                class="fL pm-summary-inner-left-con mT20"
                style="padding-bottom: 100px; height: 100vh; overflow-y: scroll;"
                v-bind:class="{
                  'pm-layout-full-wdith': toggleReadingTable,
                  'pm-layout-half-wdith': !toggleReadingTable,
                }"
              >
                <div
                  class="pL30 pR30 pT10 pB40 white-bg-block height100 over overflow-y-scroll"
                  v-if="isNewViewTemp && !toggleReadingTable"
                >
                  <!-- task list show Start-->
                  <div class="task-tab-block mT0">
                    <div v-for="(data, index) in newViewTasks" :key="index">
                      <div
                        v-if="data.sectionName !== 'default'"
                        style="margin-top: 20px;padding-top: 10px;font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
                        :style="{
                          'border-bottom':
                            data.sectionName !== 'default'
                              ? '1px solid #eceef1'
                              : '0',
                        }"
                      >
                        {{ data.sectionName }}
                        <div class="fR mR15">{{ data.resourceLabel }}</div>
                      </div>
                      <div
                        class="task-container"
                        v-for="(task, idx) in data.tasks"
                        :key="idx"
                        @click="showDetails(task)"
                      >
                        <div
                          :class="[
                            'task-list',
                            { 'selected-task': selectedTask === task },
                          ]"
                          class="pL10"
                        >
                          <div class="task-content">
                            <div class="task-content-item">
                              <span class="fc-id">#{{ task.uniqueId }}</span>
                              {{ task.subject ? task.subject : '' }}
                            </div>
                            <div
                              class="task-list-icon"
                              v-if="task.resource && task.resource.id !== -1"
                            >
                              <span class="task-resource-name">{{
                                task.resource.name
                              }}</span>
                              <img
                                v-if="task.resource.resourceType === 1"
                                src="~statics/space/space-resource.svg"
                                style="height:12px; width:14px;"
                              />
                              <img
                                v-else
                                src="~statics/space/asset-resource.svg"
                                style="height:11px; width:14px;"
                              />
                            </div>
                            <div
                              v-if="task.resourceLabel"
                              class="task-list-icon"
                            >
                              <span class="task-resource-name">{{
                                task.resourceLabel
                              }}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <template v-for="(section, sectionname, index) in tasks">
                      <div v-if="!isNewViewTemp" :key="index">
                        <div
                          v-if="sectionname !== 'default'"
                          style="padding-top:35px; font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
                          :style="{
                            'border-bottom':
                              sectionname !== 'default'
                                ? 'solid 1px #d9f4f7'
                                : '0',
                          }"
                        >
                          {{ sectionname }}
                        </div>
                        <div
                          class="task-container"
                          v-for="(task, idx) in section"
                          :key="idx"
                          @click="showDetails(task)"
                        >
                          <div
                            :class="[
                              'task-list',
                              { 'selected-task': selectedTask === task },
                            ]"
                            class="fc-list-hover-task"
                          >
                            <div class="task-content">
                              <i
                                class="fa fa-circle"
                                style="color: #b8e5eb; font-size: 10px; padding-right: 5px;position:relative;width: 20px;float: left;"
                                aria-hidden="true"
                              ></i>
                              <div class="task-content-item">
                                {{ task.subject ? task.subject : '' }}
                              </div>
                              <div
                                class="task-list-icon"
                                v-if="task.resource && task.resource.id !== -1"
                              >
                                <span class="task-resource-name">{{
                                  task.resource.name
                                }}</span>
                                <img
                                  v-if="task.resource.resourceType === 1"
                                  src="~statics/space/space-resource.svg"
                                  style="height:12px; width:14px;"
                                />
                                <img
                                  v-else
                                  src="~statics/space/asset-resource.svg"
                                  style="height:11px; width:14px;"
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </template>
                  </div>
                  <!-- task list show End -->
                </div>
                <div
                  class="over overflow-y-scroll"
                  v-if="isNewViewTemp && toggleReadingTable"
                >
                  <div class="task-tab-block mT0">
                    <f-pm-readingtable
                      :pmObject="pmObject"
                      @readingData="getReadingData"
                      :pm="pm"
                      :workorder="workorder"
                    ></f-pm-readingtable>
                  </div>
                </div>
              </div>
              <div
                class="fR pm-summary-inner-right-con fc-v1-wo-sum-right width28"
                v-if="!toggleReadingTable"
              >
                <div class="fc-scrollbar-wrap pT10">
                  <div
                    class="p20 white-bg-block pm-summary-right-bg height100vh"
                    v-if="selectedTask"
                  >
                    <div class="pB70">
                      <div class="task-details-header position-relative">
                        <div
                          class="tdetail-title-block fc-tdetail-title-block fR"
                        >
                          <i
                            class="el-icon-close tclose-icon text-left wo__close__icon"
                            @click="closedisplay"
                            title="Close"
                            v-tippy="{
                              arrow: true,
                              placement: 'top',
                            }"
                          ></i>
                        </div>
                        <div class="task-details-list clearboth">
                          <div class="task-detail-content">
                            <div
                              class="task-list-item mB20"
                              style="font-weight: 600; font-size: 14px; margin-bottom: 25px;color: #324056;"
                            >
                              {{ selectedTask.subject }}
                              <div
                                class="task-content-item"
                                v-if="
                                  selectedTask.resource &&
                                    selectedTask.resource.id !== -1
                                "
                              >
                                <span class="task-resource-name">{{
                                  selectedTask.resource.name
                                }}</span>
                                <img
                                  v-if="
                                    selectedTask.resource.resourceType === 1
                                  "
                                  src="~statics/space/space-resource.svg"
                                  style="height:12px; width:14px;"
                                />
                                <img
                                  v-else
                                  src="~statics/space/asset-resource.svg"
                                  style="height:11px; width:14px;"
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div>
                        <div>
                          <div v-if="selectedTask.description">
                            <div class="fc-blue-label">
                              {{ $t('maintenance._workorder.description') }}
                            </div>
                            <el-input
                              disabled
                              v-model="selectedTask.description"
                              class="form-item mT10"
                              type="textarea"
                            />
                          </div>
                          <div v-else-if="!selectedTask.description"></div>
                        </div>
                        <div
                          style="padding-top:20px;"
                          class="enableInput pmremoved-checkbox"
                        >
                          <el-checkbox
                            disabled
                            v-model="selectedTask.enableInput"
                            >{{
                              $t('maintenance._workorder.enable_input')
                            }}</el-checkbox
                          >
                        </div>

                        <div v-if="selectedTask.enableInput">
                          <div class="pT30 fc-blue-label">
                            {{ $t('maintenance._workorder.task_type') }}
                          </div>
                          <el-radio
                            disabled
                            v-model="selectedTask.inputType"
                            class="fc-radio-btn pT15"
                            color="secondary"
                            label="2"
                            >{{
                              $t('maintenance._workorder.reading')
                            }}</el-radio
                          >
                          <div>
                            <el-radio
                              disabled
                              v-model="selectedTask.inputType"
                              class="fc-radio-btn pT15"
                              color="secondary"
                              label="3"
                              >{{ $t('maintenance._workorder.text') }}</el-radio
                            >
                          </div>
                          <el-radio
                            disabled
                            v-model="selectedTask.inputType"
                            class="fc-radio-btn pT15"
                            color="secondary"
                            label="4"
                            >{{ $t('maintenance._workorder.number') }}</el-radio
                          >
                          <div>
                            <el-radio
                              disabled
                              v-model="selectedTask.inputType"
                              class="fc-radio-btn pT15"
                              color="secondary"
                              label="5"
                              >{{
                                $t('maintenance._workorder.option')
                              }}</el-radio
                            >
                          </div>
                          <div>
                            <el-radio
                              disabled
                              v-model="selectedTask.inputType"
                              class="fc-radio-btn pT15"
                              color="secondary"
                              label="6"
                              >{{
                                $t('maintenance._workorder.boolean')
                              }}</el-radio
                            >
                          </div>
                        </div>
                        <div v-if="selectedTask.inputType === '2'">
                          <div class="textcolor">
                            {{ $t('maintenance._workorder.reading_field') }}
                          </div>
                          <div>
                            <el-select
                              disabled
                              v-model="selectedTask.readingFieldId"
                              style="width:100%"
                              class="form-item"
                              placeholder=" "
                            >
                              <el-option
                                v-if="selectedTask.readingField"
                                :key="selectedTask.readingField.id"
                                :label="selectedTask.readingField.displayName"
                                :value="selectedTask.readingField.id"
                              ></el-option>
                            </el-select>
                          </div>
                        </div>
                        <div
                          style="display: none;"
                          v-if="selectedTask.inputType === '4'"
                        >
                          <div class="textcolor">
                            {{ $t('maintenance.pm_list.validation') }}
                          </div>
                          <el-select
                            disabled
                            v-model="selectedTask.validation"
                            style="width:100%"
                            class="form-item"
                            placeholder=" "
                          >
                            <el-option
                              label="None"
                              key="none"
                              value="none"
                            ></el-option>
                            <el-option
                              label="Incremental"
                              key="incremental"
                              value="incremental"
                            ></el-option>
                            <el-option
                              label="Decremental"
                              key="decremental"
                              value="decremental"
                            ></el-option>
                            <el-option
                              label="Safe Limit"
                              key="safeLimit"
                              value="safeLimit"
                            ></el-option>
                          </el-select>
                          <div v-if="selectedTask.validation === 'safeLimit'">
                            <div class="textcolor">
                              {{ $t('maintenance.pm_list.minimum_value') }}
                            </div>
                            <el-input
                              disabled
                              type="number"
                              v-model="selectedTask.minSafeLimit"
                            ></el-input>
                            <div class="textcolor">
                              {{ $t('maintenance.pm_list.max_value') }}
                            </div>
                            <el-input
                              disabled
                              type="number"
                              v-model="selectedTask.maxSafeLimit"
                            ></el-input>
                          </div>
                        </div>
                        <div
                          v-if="
                            selectedTask.inputType === '5' ||
                              selectedTask.inputType === '6'
                          "
                        >
                          <div class="pT30 fc-blue-label pB10">
                            {{ $t('maintenance._workorder.options') }}
                          </div>
                          <div
                            :key="index"
                            v-for="(option, index) in selectedTask.options"
                            style="padding-bottom:10px;"
                            class="taskoption"
                          >
                            <div
                              class="flLeft"
                              style="padding-left:3px; width:200px;"
                            >
                              <el-input
                                disabled
                                style="width:100%;"
                                type="text"
                                v-model="option.name"
                                placeholder
                              ></el-input>
                            </div>
                            <div class="clearboth"></div>
                          </div>
                        </div>
                        <div class="pT30 fc-blue-label">
                          {{ $t('maintenance._workorder.validations') }}
                        </div>
                        <div
                          style="padding-top:25px;"
                          class="enableInput pmremoved-checkbox"
                        >
                          <el-checkbox
                            disabled
                            v-model="selectedTask.attachmentRequired"
                            >{{
                              $t('maintenance._workorder.photo_required')
                            }}</el-checkbox
                          >
                        </div>
                        <div
                          style="padding-top:25px;"
                          class="enableInput pmremoved-checkbox"
                        >
                          <el-checkbox
                            disabled
                            v-model="selectedTask.remarksRequired"
                            >{{
                              $t('maintenance._workorder.remarks_required')
                            }}</el-checkbox
                          >
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <!-- History -->
          <el-tab-pane :label="$t('maintenance.pm_list.history')" name="fourth">
            <pm-history
              :pm="pmObject"
              :workorder="workorder"
              :resourceType="resourceTypeEnum"
              :resources="resources"
              :pmWorkorders="workordersArr"
              v-if="activeName === 'fourth'"
            ></pm-history>
          </el-tab-pane>

          <!-- right side view start-->
          <div
            class="fR pm-summary-inner-right-con fc-v1-wo-sum-right width28"
            v-if="
              activeName === 'first' ||
                (pmDetailsview &&
                  !toggleReadingTable &&
                  (activeName === 'third' || activeName === 'fifth'))
            "
          >
            <div class="fc-scrollbar-wrap pT10">
              <div class="white-bg-block p20 mB20">
                <div class="fc-black-13 fwBold text-uppercase text-left">
                  {{ $t('maintenance.pm_list.trigger') }}
                </div>
                <el-row class="wo__details__res pT10">
                  <el-col :span="24" class="flex-middle">
                    <template v-if="pm.triggers && pm.triggers.length === 1">
                      <div v-if="pm.triggers[0].triggerExecutionSource === 1">
                        <div class="label-txt-black">
                          {{
                            pm.triggers[0].scheduleMsg
                              ? pm.triggers[0].scheduleMsg
                              : ''
                          }}
                        </div>
                      </div>
                      <div
                        v-else-if="pm.triggers[0].triggerExecutionSource === 2"
                        :key="index"
                        class="reading mT10"
                      >
                        <div class="trigger-content">
                          <div class="trigger-subheading pb5">Reading</div>
                        </div>
                      </div>
                      <div
                        v-else-if="pm.triggers[0].triggerExecutionSource === 3"
                        :key="index"
                        class="reading mT10"
                      >
                        <div class="label-txt-black pointer">
                          Alarm Condition
                          <i
                            class="el-icon-right f14 workorder-bg pL10 pointer fwBold vertical-text-bottom"
                          ></i>
                        </div>
                      </div>
                      <i
                        @click="activeName = 'second'"
                        class="el-icon-right f14 workorder-bg pL10 pointer fwBold vertical-text-bottom"
                      ></i>
                    </template>
                    <template
                      class="label-txt-black"
                      v-else-if="!pm.triggers || pm.triggers.length === 0"
                    >
                      ---
                    </template>
                    <template v-else-if="pm.triggers && pm.triggers.length > 1">
                      <div
                        class="label-txt-black pointer"
                        @click="activeName = 'second'"
                      >
                        {{ getTriggersFrequencyLists(pm.triggers) }}
                        <i
                          class="el-icon-right f14 workorder-bg pL10 pointer fwBold vertical-text-bottom"
                        ></i>
                      </div>
                    </template>
                  </el-col>
                </el-row>
              </div>
              <div class="p30 white-bg-block pm-summary-right-bg">
                <el-row class="wo__details__res pB20">
                  <el-col :span="11">
                    <div class="fc-blue-label">SITE</div>
                  </el-col>
                  <el-col :span="13">
                    <div class="label-txt-black">
                      {{ getSiteName(pm.siteId) }}
                    </div>
                  </el-col>
                </el-row>

                <div>
                  <el-row
                    class="wo__details__res pB20"
                    v-if="pm.pmCreationType === 2 && pm.baseSpaceId !== null"
                  >
                    <el-col :span="11">
                      <div class="fc-blue-label line-height18">
                        {{ $t('maintenance.pm_list.building') }}
                      </div>
                    </el-col>
                    <el-col :span="13">
                      <div
                        v-if="buildingList[Number(pm.baseSpaceId)]"
                        class="label-txt-black"
                      >
                        {{ buildingList[Number(pm.baseSpaceId)] }}
                      </div>
                      <div v-else class="building-resulttxt">---</div>
                    </el-col>
                  </el-row>
                </div>

                <div v-if="pm.pmCreationType === 2 && pm.assignmentType === 1">
                  <el-row class="wo__details__res pB20">
                    <el-col :span="11">
                      <div class="fc-blue-label line-height18">Floors</div>
                    </el-col>
                    <el-col :span="13">
                      <div
                        v-if="
                          pm.pmIncludeExcludeResourceContexts &&
                            pm.pmIncludeExcludeResourceContexts.length
                        "
                        class="label-txt-black"
                      >
                        {{
                          `${pm.pmIncludeExcludeResourceContexts.length} Floors`
                        }}
                      </div>
                      <div v-else class="label-txt-black">
                        {{ `All Floors` }}
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <div v-if="pm.pmCreationType === 2 && pm.assignmentType === 3">
                  <el-row class="wo__details__res pB20">
                    <el-col :span="11">
                      <div class="fc-blue-label line-height18">
                        {{ $t('maintenance._workorder.space') }}
                      </div>
                    </el-col>
                    <el-col :span="13">
                      <div
                        v-if="
                          pm.pmIncludeExcludeResourceContexts &&
                            pm.pmIncludeExcludeResourceContexts.length
                        "
                        class="label-txt-black"
                      >
                        {{
                          `${pm.pmIncludeExcludeResourceContexts.length} ${
                            spacecategory[pm.spaceCategoryId]
                          }`
                        }}
                      </div>
                      <div v-else class="label-txt-black">
                        {{ `All ${spacecategory[pm.spaceCategoryId]}` }}
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <div v-if="pm.pmCreationType === 2 && pm.assignmentType === 4">
                  <el-row class="wo__details__res pB20">
                    <el-col :span="11">
                      <div class="fc-blue-label line-height18">
                        {{ $t('maintenance._workorder.asset') }}
                      </div>
                    </el-col>
                    <el-col :span="13">
                      <div
                        v-if="
                          assetcategory &&
                            pm.pmIncludeExcludeResourceContexts &&
                            pm.pmIncludeExcludeResourceContexts.length
                        "
                        class="label-txt-black"
                      >
                        {{
                          `${pm.pmIncludeExcludeResourceContexts.length} ${assetCategoryName}`
                        }}
                      </div>
                      <div
                        v-else-if="assetcategory && pm.assetCategoryId > -1"
                        class="label-txt-black"
                      >
                        {{ `All ${assetCategoryName}` }}
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <el-row class="wo__details__res pB20">
                  <el-col :span="11">
                    <div class="fc-blue-label line-height18">
                      {{ $t('maintenance.wr_list.priority') }}
                    </div>
                  </el-col>
                  <el-col :span="13">
                    <div
                      v-if="workorder.priority"
                      class="label-txt-black"
                      style="cursor: pointer;"
                    >
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
                    </div>
                    <div v-else class="building-resulttxt">---</div>
                  </el-col>
                </el-row>
                <el-row class="wo__details__res pB20">
                  <el-col :span="11">
                    <div class="fc-blue-label line-height18">
                      {{ $t('maintenance.wr_list.category') }}
                    </div>
                  </el-col>
                  <el-col :span="13">
                    <div
                      v-if="workorder.category && workorder.category.id"
                      class="label-txt-black"
                      style="cursor: pointer;"
                    >
                      {{
                        workorder.category && workorder.category.id
                          ? getTicketCategory(workorder.category.id).displayName
                          : '---'
                      }}
                    </div>
                  </el-col>
                </el-row>
                <div v-if="pm.pmCreationType === 1">
                  <template
                    v-if="
                      workorder.resource &&
                        workorder.resource.resourceType === 2
                    "
                    @click="openAsset(workorder.resource.id)"
                  >
                    <el-row class="wo__details__res pB20">
                      <el-col :span="11">
                        <div class="fc-blue-label pT25">
                          {{ $t('maintenance._workorder.asset') }}
                        </div>
                      </el-col>
                      <el-col
                        :span="13"
                        class="flex-middle pm-summary-space-ico"
                      >
                        <div
                          class="building-asset-img"
                          style="width: 10%;"
                          @click="openAsset(workorder.resource.id)"
                        >
                          <asset-avatar
                            size="mobile-xlg"
                            name="false"
                            :asset="workorder.resource"
                          ></asset-avatar>
                        </div>
                        <div
                          class="building-resulttxt"
                          @click="openAsset(workorder.resource.id)"
                        >
                          <div
                            class="label-txt-black"
                            style="cursor: pointer;word-break: break-all;"
                          >
                            {{ workorder.resource.name }}
                          </div>
                          <span class="label-txt-black">
                            {{
                              workorder.resource.space &&
                              workorder.resource.space.id > 0
                                ? workorder.resource.space.name
                                : '---'
                            }}
                          </span>
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                  <template
                    v-else-if="
                      workorder.resource &&
                        workorder.resource.resourceType === 1
                    "
                  >
                    <el-row class="wo__details__res pB20">
                      <el-col :span="11">
                        <div
                          class="fc-blue-label pT10"
                          style="text-transform: uppercase;"
                        >
                          {{ $t('maintenance._workorder.space') }}
                        </div>
                      </el-col>
                      <el-col :span="13" class="pm-summary-space-ico">
                        <div class="building-resulttxt flex-middle">
                          <space-avatar
                            class="asset-space-img fL mR10"
                            :name="false"
                            size="md"
                            :space="workorder.resource"
                          ></space-avatar>
                          <div
                            class="label-txt-black"
                            style="padding-left: 9px;cursor: pointer;position: relative;"
                            @click="redirect"
                          >
                            {{ workorder.resource.name }}
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </template>
                  <template v-else>
                    <el-row class="wo__details__res pB20">
                      <el-col :span="11">
                        <div class="fc-blue-label line-height18">
                          {{ $t('maintenance.wr_list.space_asset') }}
                        </div>
                      </el-col>
                      <el-col :span="13">
                        <div class="label-txt-black">---</div>
                      </el-col>
                    </el-row>
                  </template>
                </div>
                <div
                  v-if="
                    workorder.safetyPlan &&
                      this.$helpers.isLicenseEnabled('SAFETY_PLAN')
                  "
                >
                  <el-row class="wo__details__res pB20">
                    <el-col :span="11">
                      <div class="fc-blue-label line-height18">
                        {{ $t('maintenance.wr_list.safetyPlan') }}
                      </div>
                    </el-col>
                    <el-col :span="13">
                      <div class="label-txt-black">
                        <span>
                          {{
                            workorder.safetyPlan && workorder.safetyPlan.name
                              ? workorder.safetyPlan.name
                              : '---'
                          }}
                        </span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <el-row class="wo__details__res pB20">
                  <el-col :span="11">
                    <div class="fc-blue-label line-height18">
                      {{ $t('maintenance.pm_list.maintenance_type') }}
                    </div>
                  </el-col>
                  <el-col :span="13">
                    <div class style="cursor: pointer;">
                      <span>
                        {{
                          workorder.type && workorder.type.id > 0
                            ? pmTypes[workorder.type.id]
                            : '---'
                        }}
                      </span>
                    </div>
                  </el-col>
                </el-row>

                <el-row class="wo__details__res pB20 flex-middle">
                  <el-col :span="11">
                    <div
                      class="fc-blue-label"
                      style="text-transform: uppercase;"
                    >
                      {{ $t('maintenance.wr_list.assignedto') }}
                    </div>
                  </el-col>
                  <el-col :span="13">
                    <div
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
                    <div v-else>--- / ---</div>
                  </el-col>
                </el-row>

                <el-row class="wo__details__res pB20">
                  <el-col :span="11">
                    <div class="fc-blue-label line-height18">
                      {{ $t('maintenance.wr_list.vendor') }}
                    </div>
                  </el-col>
                  <el-col :span="13">
                    <div class style="cursor: pointer;">
                      <span>
                        {{
                          workorder.vendor && workorder.vendor.id > 0
                            ? workorder.vendor.name
                            : '---'
                        }}
                      </span>
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div class="p20 white-bg-block mT20">
                <div class="fc-blue-label border-bottom4 pB20">
                  {{ $t('maintenance.pm_list.notification') }}
                </div>
                <div class="notifications-container">
                  <template
                    v-if="
                      pm.pmCreationType === 1 && reminders && reminders.length
                    "
                  >
                    <div
                      class="notfications-block"
                      v-for="reminder in reminders"
                      :key="reminder.id"
                      style="border-bottom: 1px solid #d9f4f7;"
                    >
                      <div
                        v-if="
                          workorder.assignedTo && workorder.assignedTo.id > 0
                        "
                        class="notification-profile-icon"
                      >
                        <user-avatar
                          size="sm"
                          :user="workorder.assignedTo"
                          :name="false"
                          v-tippy
                          :title="workorder.assignedTo.name"
                        ></user-avatar>
                      </div>
                      <div class="notification-resulttxt">
                        {{ $t('maintenance.pm_list.will_be_notified') }}
                        <b>
                          {{ reminderDurations[reminder.duration] }}
                          {{ reminderType[reminder.type] }}
                        </b>
                      </div>
                    </div>
                  </template>
                  <template
                    v-else-if="
                      pm.pmCreationType === 2 && reminders && reminders.length
                    "
                  >
                    <el-row
                      v-for="(reminder, key) in reminders"
                      :key="key"
                      class="pm-form-notifications-row visibility-visible-actions notfications-block"
                      style="border-bottom: 1px solid #d9f4f7;"
                    >
                      <el-col :span="9" class="pT5 pB5">
                        {{ reminder.name }}
                      </el-col>
                      <el-col :span="6" class="pT5 pB5">
                        {{ reminderDurations[reminder.duration] }}
                        {{ reminderType[reminder.type] }}
                      </el-col>
                      <el-col :span="6" class="fc-tag">
                        <el-tag v-if="reminder.isEmail">Email</el-tag>
                        <el-tag v-if="reminder.isMobile">Mobile</el-tag>
                        <el-tag v-if="reminder.isSms">SMS</el-tag>
                      </el-col>
                    </el-row>
                  </template>
                  <div v-else>
                    <div class="notification-resulttxt text-center mT30 mB30">
                      {{ $t('maintenance.pm_list.no_notification') }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- right side view end -->
        </el-tabs>
      </div>
      <div v-if="showNotification">
        <PmNotification
          :showConfigureNotification.sync="showNotification"
          :reminders="reminders"
        ></PmNotification>
      </div>
    </div>
    <!-- header end -->
    <el-dialog
      :visible.sync="showExecutionConfig"
      :append-to-body="true"
      width="30%"
      title="SELECT SPACE/ASSETS"
      class="fc-dialog-center-container resource-dialog"
      :before-close="beforeConfigClose"
    >
      <div class="mT20">
        <el-input
          placeholder="search"
          v-model="searchQuery"
          @change="forceUpdate"
          type="search"
          class="fc-input-full-border2 text-input-icon-align input-padding width100"
          prefix-icon="el-icon-search"
        ></el-input>
      </div>
      <table class="fc-pm-form-table">
        <thead>
          <tr class="pm-trigger-head-tr">
            <th class="pT10 pB10">
              <el-checkbox
                v-model="selectAllResources"
                @change="selected"
                class="pR20"
              ></el-checkbox
              >SELECT ALL
            </th>
          </tr>
        </thead>
        <tbody class="mT10">
          <tr
            v-for="(resource, key) in resources"
            :key="key"
            v-show="
              !searchQuery.trim() ||
                resource.name
                  .trim()
                  .toLowerCase()
                  .startsWith(searchQuery.trim().toLowerCase())
            "
            class="mB10"
          >
            <td class="pT15 pB15">
              <el-checkbox
                @change="forceUpdate"
                v-model="resources[key].selected"
                class="pR20"
              ></el-checkbox>
              {{ resource.name }}
            </td>
          </tr>
        </tbody>
      </table>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancelExecutionConfig"
          >CANCEL</el-button
        >
        <el-button type="primary" class="modal-btn-save" @click="executeMultiPM"
          >EXECUTE</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      v-if="showExtendScheduleDialog"
      :visible.sync="showExtendScheduleDialog"
      :width="'25%'"
      :append-to-body="true"
      title="Extend Schedule"
      class="fc-dialog-center-container"
    >
      <div>
        <div class="height150">
          <div class="fc-input-label-txt">Extend Upto</div>
          <el-date-picker
            v-model="extendUpto"
            type="date"
            value-format="timestamp"
            placeholder="Pick a day"
            class="fc-input-full-border2 width100"
          ></el-date-picker>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-save" @click="handleExtend"
            >Extend</el-button
          >
          <el-button
            class="modal-btn-cancel"
            @click="showExtendScheduleDialog = false"
            >Cancel</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Avatar from '@/Avatar'
import { isEmpty } from '@facilio/utils/validation'
import { QBtn } from 'quasar'
import modularUserFilter from 'src/pages/report/mixins/modularUserFilter'
import PmHistory from 'pages/workorder/preventive/v1/PmHistory'
import PmTriggers from 'pages/workorder/preventive/v1/PmTriggers'
import UserAvatar from '@/avatar/User'
import AssetAvatar from '@/avatar/Asset'
import SpaceAvatar from '@/avatar/Space'
import Spinner from '@/Spinner'
import pie from '@/snapChart/donutProgress'
import FNewAnalyticModularReport from 'src/pages/energy/analytics/components/FNewAnalyticModularReport'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import PmNotification from 'pages/workorder/preventive/pmNotification'
import PmSummaryResourcePlanner from './PmSummaryPlannerView'
import moment from 'moment-timezone'
import InlineSvg from '@/InlineSvg'
import FPmReadingtable from '@/PmReadingsable'
import { mapState, mapGetters } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
  tabTypes,
  findRouteForTab,
} from '@facilio/router'
import { API } from '@facilio/api'

export default {
  components: {
    PmSummaryResourcePlanner,
    Avatar,
    QBtn,
    UserAvatar,
    AssetAvatar,
    SpaceAvatar,
    Spinner,
    pie,
    PmHistory,
    PmTriggers,
    FNewAnalyticModularReport,
    PmNotification,
    InlineSvg,
    FPmReadingtable,
  },

  mixins: [modularUserFilter],
  data() {
    return {
      workordersArr: [],
      toggleReadingTable: false,
      openPopOver: false,
      workorderObj: {},
      extendUpto: null,
      plannerView: null,
      readingData: [],
      showExtendScheduleDialog: false,
      refreshButtonLoading: false,
      selectAllResources: false,
      endDate: '',
      resType: '',
      resName: '',
      fields: [],
      resourceTypeEnum: '',
      showExecutionConfig: false,
      showNotification: false,
      frequencyMap: {
        DAILY: 12,
        WEEKLY: 11,
        MONTHLY: 10,
        QUARTERTLY: 25,
        HALF_YEARLY: 5,
        ANNUALLY: 8,
      },
      pmObject: {},
      model: {},
      moduleMeta: {},
      pmModuleMeta: {},
      days: '',
      hours: '',
      mins: '',
      secs: '',
      activeName: 'first',
      triggertab: 'overview',
      searchQuery: '',
      serverConfig: {},
      tabPosition: 'top',
      showChart: false,
      workorder: null,
      loading: false,
      pm: null,
      isExecuting: false,
      averageRes: 0,
      previousMonthAverageRes: '',
      previousMonthAverageResponseTime: {},
      averageResponseTime: {},
      avgResponseLoading: false,
      tasks: {},
      preRequests: {},
      photoMandatory: false,
      allowNegative: false,
      allowNegativePreRequisite: 2,
      approvers: [{ sharingType: null, approversId: null }],
      approvalTypes: [
        {
          id: 1,
          name: 'User',
        },
        {
          id: 2,
          name: 'Role',
        },
        {
          id: 3,
          name: 'Group',
        },
      ],
      newViewPreRequests: [],
      newViewTasks: [],
      isNewViewTemp: false,
      isNewViewTempPreRequest: false,
      reminders: [],
      readingFields: [],
      taskReadingFields: {},
      reminderType: {
        1: 'before execution',
        2: 'after execution',
        3: 'before due',
        4: 'after due',
      },
      workflowResult: {
        woOnTime: {
          result: null,
          loading: false,
        },
        woAvgRes: {
          result: null,
          loading: false,
        },
        woFreqAssign: {
          result: null,
          loading: false,
        },
      },
      previousMonthworkflowResult: {
        woOnTime: {
          result: null,
        },
        woAvgRes: {
          result: null,
        },
      },
      showTaskDetails: false,
      selectedTask: null,
      showPreRequestDetails: false,
      selectedPreRequest: null,
      resources: [],
      resourceSearchQuery: '',
      pmDetailsview: true,
      assetPlannerView: null,
      staffPlannerView: null,
      assetExtendUpto: null,
      staffExtendUpto: null,
      execResources: [],
      singleSite: {},
    }
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings').then(() => {
      this.loadResourcePlanner(this.$route.params.id).then(response => {
        let resourcePlanners = response.data.resourcePlanners
        this.loadPreventiveSummary(resourcePlanners)
      })
    })
  },
  computed: {
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
      assetcategory: state => state.assetCategory,
      site: state => state.site,
      sites: state => state.sites,
    }),
    ...mapGetters([
      'getTicketTypePickList',
      'getSpaceCategoryPickList',
      'getBuildingsPickList',
      'getUser',
      'getTicketPriority',
      'getTicketCategory',
      'getCurrentUser',
    ]),

    buildingList() {
      return this.getBuildingsPickList()
    },
    tickettype() {
      return this.getTicketTypePickList()
    },
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    checkteam() {
      if (
        !this.workorder.assignmentGroup ||
        this.workorder.assignmentGroup == 'null'
      ) {
        return 0
      } else {
        for (let i in this.$store.state.groups) {
          if (
            this.$store.state.groups[i].groupId ===
            this.workorder.assignmentGroup.id
          ) {
            if (
              this.$store.state.groups[i].members.find(
                mem => mem.id === this.$account.user.id
              )
            ) {
              return 1
            }
          }
        }
        return 0
      }
    },
    showRefreshBanner() {
      return (
        this.pm &&
        this.pm.woGenerationStatus &&
        ((this.triggertab === 'asset' && this.assetPlannerView) ||
          (this.triggertab === 'staff' && this.staffPlannerView))
      )
    },
    showExtendScheduleBanner() {
      if (this.assetPlannerView || this.staffPlannerView) {
        if (!this.pm.woGeneratedUpto || this.pm.woGeneratedUpto < 0) {
          return false
        }
        let show = true
        let generatedUpto = this.pm.woGeneratedUpto * 1000
        let now = Date.now()
        show = show && !this.pm.woGenerationStatus
        show =
          show &&
          this.activeName == 'second' &&
          (this.triggertab === 'asset' || this.triggertab === 'staff')

        if (this.triggertab === 'asset') {
          show =
            show &&
            this.assetPlannerView.start <= now + 24 * 60 * 60 * 365 * 1000 //show within a year from now
          show = show && now <= this.assetPlannerView.end // show after now
          show =
            show &&
            (generatedUpto < 0 || generatedUpto <= this.assetPlannerView.end) // show when generated upto within the end time or it is not filled
        } else if (this.triggertab === 'staff') {
          show =
            show &&
            this.staffPlannerView.start <= now + 24 * 60 * 60 * 365 * 1000 //show within a year from now
          show = show && now <= this.staffPlannerView.end // show after now
          show =
            show &&
            (generatedUpto < 0 || generatedUpto <= this.staffPlannerView.end) // show when generated upto within the end time or it is not filled
        }
        return show
      }
      return false
    },

    computedModule() {
      if (this.moduleMeta) {
        return {
          moduleName: this.moduleMeta.name,
          moduleId: this.moduleMeta.module.moduleId,
          meta: {
            fieldMeta: {
              xField: this.moduleMeta.module.moduleId,
            },
          },
        }
      }
      return {}
    },
    reminderDurations() {
      let options = {}
      options[1800] = '30 Min'
      for (let i = 1; i < 24; i++) {
        options[i * 3600] = i + (i === 1 ? 'hr' : 'hrs')
        options[i * 24 * 3600] = i + (i === 1 ? 'day' : 'days')
      }

      for (let i = 24; i < 32; i++) {
        options[i * 24 * 3600] = i + 'days'
      }
      return options
    },
    pmTypes() {
      return this.tickettype
    },
    lastMonthAverageDuration() {
      if (this.previousMonthworkflowResult.woAvgRes.result) {
        let duration = this.previousMonthworkflowResult.woAvgRes.result
        let days = Math.floor(duration / 86400)
        duration -= days * 86400
        let hours = (duration / 3600).toFixed(2)
        return {
          days: days,
          hours: hours,
        }
      }
      return null
    },
    averageDuration() {
      if (this.workflowResult.woAvgRes.result) {
        let duration = this.workflowResult.woAvgRes.result
        let days = Math.floor(duration / 86400)
        duration -= days * 86400
        let hours = (duration / 3600).toFixed(2)
        return {
          days: days,
          hours: hours,
        }
      }
      return null
    },
    freqAssignUser() {
      if (
        this.workflowResult.woFreqAssign.result &&
        this.workflowResult.woFreqAssign.result[0] &&
        this.workflowResult.woFreqAssign.result[0].id
      ) {
        return this.getUser(this.workflowResult.woFreqAssign.result[0].id)
      }
      return null
    },
    assetCategoryName() {
      let category = this.assetcategory.find(
        i => i.id === Number(this.pm.assetCategoryId)
      )
      return category ? category.name : ''
    },
  },
  methods: {
    loadWorkorders() {
      let { $route } = this
      let { params } = $route || {}
      let { id } = params
      id = parseInt(id)
      let data = {
        pmId: id,
      }
      let url = '/v2/workorders/fetchRelatedWorkorders'
      API.get(url, data).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { workorders } = data || []
          this.workordersArr = workorders
        }
      })
    },
    async loadSite() {
      let siteIdList = []
      const EQUALS = 36
      let { pm } = this
      let { siteIds = [] } = pm || {}
      if (isEmpty(siteIds)) {
        let { siteId } = pm
        if (siteId > 0) {
          siteIds.push(siteId)
        } else {
          return false
        }
      }
      siteIds.forEach(siteId => siteIdList.push(siteId + ''))
      let params = {
        filters: JSON.stringify({
          id: {
            operatorId: EQUALS,
            value: siteIdList,
          },
        }),
      }
      let { list, error } = await API.fetchAll('site', params)
      if (error) {
        this.$message.error(error || 'Error ocurred')
        return false
      } else {
        if (list.length > 0) {
          this.$setProperty(this, 'singleSite', list[0])
        }
        return list.length > 0
      }
    },

    goToEdit() {
      let { id } = this.$route.params
      let pmCreationType = this.$getProperty(this.pm, 'pmCreationTypeEnum')
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('preventivemaintenance', pageTypes.EDIT) || {}

        if (name) {
          return {
            name,
            params: { id: id, pmCreationType: pmCreationType },
          }
        }
      } else {
        return `/app/wo/planned/new?edit=${id}`
      }
    },
    getMultiResources() {
      this.isExecuting = true
    },
    showExecuteNow() {
      return this.pm && this.pm.status && this.pm.isAllowedToExecute
    },
    isAllowedToExecute() {
      let sharingContext = []
      for (let i = 0; i < this.pm.triggers.length; i++) {
        if (this.pm.triggers[i].type === 4) {
          sharingContext.push(...this.pm.triggers[i].sharingContext)
        }
      }

      let currentUser = this.getCurrentUser()
      for (let k = 0; k < sharingContext.length; k++) {
        if (sharingContext[k].type === 1) {
          if (currentUser.ouid === sharingContext[k].userId) {
            return true
          }
        } else if (sharingContext[k].type === 2) {
          if (currentUser.roleId === sharingContext[k].roleId) {
            return true
          }
        } else if (sharingContext[k].type === 3) {
          if (
            currentUser.groups &&
            currentUser.groups.includes(sharingContext[k].groupId)
          ) {
            return true
          }
        }
      }
    },
    getReadingData(data) {
      this.readingData = data
    },
    updateReadingData() {
      let self = this
      let params = {
        taskContextList: [],
      }
      this.readingData.forEach(rt => {
        params.taskContextList.push({
          id: rt.id,
          inputValue: rt.inputValue,
          inputValues: null,
          remarks: null,
          readingFieldId: rt.readingFieldId,
          inputTime: rt.createdTime,
        })
      })
      let url = 'task/correctPMReadings'
      self.$http.post(url, params).then(function(response) {
        self.toggleReadingTable = false
        self.readingData = []
      })
    },
    handleRefresh() {
      this.refreshButtonLoading = true
      this.$http
        .get('v2/workorders/pmDetail?id=' + parseInt(this.$route.params.id))
        .then(resp => {
          this.loadWorkorders()
          if (!resp.data.preventivemaintenance.woGenerationStatus) {
            this.pm.woGenerationStatus =
              resp.data.preventivemaintenance.woGenerationStatus
            this.pm.woGeneratedUpto =
              resp.data.preventivemaintenance.woGeneratedUpto
            if (this.triggertab === 'asset') {
              this.$refs['assetplanner'].refresh()
            } else if (this.triggertab === 'staff') {
              this.$refs['staffplanner'].refresh()
            }
          }
          this.refreshButtonLoading = false
        })
        .catch(() => {
          this.refreshButtonLoading = false
        })
    },
    showExtension() {
      this.showExtendScheduleDialog = true
      this.extendUpto = this.assetExtendUpto
    },
    handleExtend() {
      this.showExtendScheduleDialog = false
      this.pm.woGenerationStatus = true
      this.$http
        .post('/workorder/generateSchedule', {
          pmId: this.pm.id,
          endTime: Math.floor(this.extendUpto / 1000),
        })
        .catch(_ => {
          this.$message({
            message: 'Failed to generated schedule',
            type: 'failure',
          })
          this.$set(this.pm, 'woGenerationStatus', false)
        })
    },
    handleViewChange(e) {
      if (this.triggertab === 'asset') {
        this.assetPlannerView = e
        this.assetExtendUpto = e.end
      } else if (this.triggertab === 'staff') {
        this.staffPlannerView = e
        this.staffExtendUpto = e.end
      }
    },
    getTriggersFrequencyLists(triggers) {
      let a = []
      triggers.forEach(d => {
        if (!a.includes(d.scheduleMsg)) {
          a.push(d.scheduleMsg)
        }
      })
      return a.join(', ')
    },
    handleClick(tab, event) {
      let tabContainer = document.querySelector(
        '.fc-pm-summary-tab .el-tabs__content'
      )
      tabContainer.scrollTop = 0
      //reset tab scroll on switch
    },
    showNotificationForm() {
      this.showNotification = true
    },
    getSiteName(siteId) {
      if (this.singleSite && siteId) {
        return this.singleSite.name
      }
      return '---'
    },
    loadModuleInfo(pm) {
      let self = this
      self.$http
        .get('/module/meta?moduleName=' + 'workorder')
        .then(function(response) {
          if (response) {
            self.moduleMeta = response.data.meta
            self.fields = self.moduleMeta.fields
            self.buildCriteria(pm)
          }
        })
    },
    loadPmmetaInfo() {
      let self = this
      self.$http
        .get('/module/meta?moduleName=' + 'preventivemaintenance')
        .then(function(response) {
          if (response) {
            self.pmModuleMeta = response.data.meta
          }
        })
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    buildCriteria(pm) {
      let obj = []
      let obj1 = []
      obj = this.moduleMeta.fields.filter(lf => lf.name === 'createdTime')
      obj1 = this.moduleMeta.fields.filter(
        lf => lf.name === 'actualWorkDuration'
      )
      let element = {}
      let xField = {}
      let yField = {}
      let dateField = {}
      element['sortFields'] = null
      element['sortOrder'] = null
      element['limit'] = null
      element['moduleType'] = null
      element['moduleName'] = this.moduleMeta.name
      element['groupBy'] = null
      if (this.pm.triggers && this.pm.triggers.length > 1) {
        let a = []
        this.pm.triggers.forEach(d => {
          a.push(d.frequencyEnum)
        })
        if (a.includes('DAILY')) {
          xField['aggr'] = 12
        } else if (a.includes('WEEKLY')) {
          xField['aggr'] = 11
        } else if (a.includes('MONTHLY')) {
          xField['aggr'] = 10
        } else if (a.includes('QUARTERTLY')) {
          xField['aggr'] = 25
        } else if (a.includes('HALF_YEARLY')) {
          xField['aggr'] = 5
        } else if (a.includes('ANNUALLY')) {
          xField['aggr'] = 8
        }
      } else if (this.pm.triggers && this.pm.triggers.length === 1) {
        if (this.frequencyMap[this.pm.triggers[0].frequencyEnum]) {
          xField['aggr'] = this.frequencyMap[this.pm.triggers[0].frequencyEnum]
        } else {
          xField['aggr'] = 12
        }
      } else {
        xField['aggr'] = 12
      }
      dateField['field_id'] = obj[0].id
      dateField['module_id'] = this.computedModule.moduleId
      if (xField['aggr'] === 12) {
        dateField['operator'] = 31
      } else if (xField['aggr'] === 11) {
        dateField['operator'] = 28
      } else {
        dateField['operator'] = 44
      }
      let dateObject = NewDateHelper.getDatePickerObject(dateField['operator'])
      dateField['date_value'] = dateObject.value[0] + ''

      xField['field_id'] = obj[0].id
      xField['module_id'] = this.computedModule.moduleId
      element['xField'] = xField
      let yFieldArray = []
      yField['field_id'] = obj1[0].id
      yField['module_id'] = this.computedModule.moduleId
      yField['aggr'] = 2
      yFieldArray[0] = yField
      yFieldArray[1] = null
      element['yField'] = yFieldArray
      element['dateField'] = dateField

      let criteria = {
        pattern: '(1)',
        conditions: {
          '1': {
            fieldName: 'pm',
            value: this.pm.id,
            columnName: 'WorkOrders.PM_ID',
            operatorId: 9,
            isResourceOperator: false,
            parseLabel: null,
            operatorLabel: 'equals',
            active: true,
            isSpacePicker: false,
          },
        },
        resourceOperator: false,
      }
      element['dateFilter'] = NewDateHelper.getDatePickerObject(
        dateField.operator,
        dateField.date_value ? dateField.date_value : null
      )
      element['criteria'] = criteria
      element['isTime'] = true
      element['isCustomDateField'] = false
      if (this.resources && this.resources.length > 1) {
        let a = [this.defaultSettings['LOOKUP']]
        let b = this.moduleMeta.fields.filter(i => i.name === 'resource')
        a[0].fieldId = b[0].id
        a[0].name = 'Space / Asset'
        let c = []
        if (this.resources && this.resources.length) {
          this.resources.forEach(d => {
            let e = {}
            e[d.id] = d.name
            c.push(e)
            a[0].chooseValue.values.push(d.id.toString())
          })
        }
        a[0].allValues = c
        a[0].chooseValue.otherEnabled = false
        a[0].chooseValue.type = 1

        element['userFilters'] = a
        element['hideDataPoints'] = []
      }
      this.serverConfig = element
      this.showChart = true
    },
    executeMultiPM() {
      let data = {}
      data.pmId = this.pm.id
      data.pmIncludeExcludeResourceContexts = this.resources
        .filter(i => i.selected)
        .map(k => {
          return {
            resourceId: k.id,
            pmId: this.pm.id,
            isInclude: true,
          }
        })
      this.$http
        .post('/workorder/executePreventiveMaintenance', data)
        .then(response => {
          let wo = response.data.workorder
          if (wo) {
            this.workordersArr.unshift(wo)
            this.$message({
              message: 'Preventive Maintenance has been successfully executed',
              type: 'success',
            })
          }
          this.isExecuting = false
          this.showExecutionConfig = false
          this.beforeConfigClose()
        })
    },
    cancelExecutionConfig() {
      this.showExecutionConfig = false
      this.beforeConfigClose()
    },
    selected(val) {
      for (let i = 0; i < this.resources.length; i++) {
        if (
          !this.searchQuery.trim() ||
          this.resources.name
            .trim()
            .toLowerCase()
            .startsWith(this.searchQuery.trim().toLowerCase())
        ) {
          this.resources[i].selected = val
        }
      }
      this.$forceUpdate()
    },
    resourceLabel({
      pmCreationType,
      assignmentType,
      categoryId,
      resourceCount,
      parentAssingmentType,
      parentCategoryId,
      parentResourceCount,
      pmResourceLabel,
    }) {
      if (assignmentType === 5) {
        if (parentAssingmentType === 1) {
          if (!resourceCount) {
            if (!parentResourceCount) {
              return 'All Floors'
            } else {
              return `${parentResourceCount} Floors`
            }
          } else {
            return `${resourceCount} Floors`
          }
        } else if (parentAssingmentType === 3) {
          if (parentCategoryId && parentCategoryId > 0) {
            if (!resourceCount) {
              if (!parentResourceCount) {
                return `All ${this.spacecategory[Number(parentCategoryId)]}`
              } else {
                return `${parentResourceCount} ${
                  this.spacecategory[Number(parentCategoryId)]
                }`
              }
            } else {
              return `${resourceCount} ${
                this.spacecategory[Number(parentCategoryId)]
              }`
            }
          }
        } else if (parentAssingmentType === 4) {
          if (parentCategoryId && parentCategoryId > 0 && this.assetcategory) {
            if (!resourceCount) {
              if (!parentResourceCount) {
                return `All ${
                  this.assetcategory.find(
                    i => i.id === Number(parentCategoryId)
                  ).name
                }`
              } else {
                return `${parentResourceCount} ${
                  this.assetcategory.find(
                    i => i.id === Number(parentCategoryId)
                  ).name
                }`
              }
            } else {
              return `${resourceCount} ${
                this.assetcategory.find(i => i.id === Number(parentCategoryId))
                  .name
              }`
            }
          }
        } else if (pmCreationType === 1) {
          return pmResourceLabel
        }
      } else if (assignmentType === 4) {
        if (categoryId && categoryId > 0 && this.assetcategory) {
          if (!resourceCount) {
            return `All ${
              this.assetcategory.find(i => i.id === Number(categoryId)).name
            }`
          } else {
            return `${resourceCount} ${
              this.assetcategory.find(i => i.id === Number(categoryId)).name
            }`
          }
        }
      } else if (assignmentType === 3) {
        if (categoryId && categoryId > 0) {
          if (!resourceCount) {
            return `All ${this.spacecategory[Number(categoryId)]}`
          } else {
            return `${resourceCount} ${this.spacecategory[Number(categoryId)]}`
          }
        }
      }
      return ''
    },

    currentView() {
      if (this.viewName) {
        return this.viewName
      }
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'active'
    },
    back() {
      this.$router.go(-1)
    },
    sort(arr) {
      let len = arr.length
      for (let i = len - 1; i >= 0; i--) {
        for (let j = 1; j <= i; j++) {
          if (arr[j - 1].sequence > arr[j].sequence) {
            let temp = arr[j - 1]
            arr[j - 1] = arr[j]
            arr[j] = temp
          }
        }
      }
      return arr
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
    redirect() {
      if (this.workorder.resource.siteId) {
        let parentPath = this.findRoute()

        if (parentPath) {
          let url = `${parentPath}/site/${this.workorder.resource.siteId}`

          if (this.workorder.resource.spaceType === 1) {
            url += '/overview'
          } else if (this.workorder.resource.spaceType === 2) {
            url += '/building/' + this.workorder.resource.spaceId
          } else if (this.workorder.resource.spaceType === 3) {
            url += '/floor/' + this.workorder.resource.spaceId
          } else if (this.workorder.resource.spaceType === 4) {
            url += '/space/' + this.workorder.resource.spaceId
          }
          this.$router.push({ path: url })
        }
      }
    },
    userObj(userId) {
      let userObj = null
      if (userId && userId > 0 && this.users) {
        userObj = this.users.find(i => i.ouid === userId)
      }
      return userObj
    },
    getAverageWoResponseWorkflow() {
      let self = this
      let d = new Date()
      let n = d.getTime()
      let m = this.$options.filters.formatDate(new Date(), true, false)
      this.endDate = moment(new Date())
        .subtract(1, 'months')
        .endOf('month')
        .format('MMM YYYY')
      let dateFrom = moment(m)
        .subtract(1, 'months')
        .endOf('month')
        .format('YYYY-MM-DD')
      let a = []
      a.push(parseInt(self.$route.params.id))
      let url = '/v2/workflow/getDefaultWorkflowResult'
      let params = 'defaultWorkflowId=' + 1 + '&paramList=' + a
      url = url + '?' + params
      let queryObj = {
        defaultWorkflowId: 1,
        paramList: a,
      }
      self.avgResponseLoading = true
      return self.$http.get(url).then(function(response) {
        if (
          response.data.result &&
          response.data.result.workflow &&
          response.data.result.workflow.returnValue
        ) {
          self.averageRes =
            response.data.result.workflow.returnValue.responseTime
          self.previousMonthAverageRes =
            response.data.result.workflow.returnValue.previousMonthresponseTime
        }
        if (self.averageRes > 0) {
          let dateObj = self.averageRes
          let date
          date = dateObj / 1000
          if (date < 0 && date > -2) {
            date = 0
          }
          let d = Math.floor(date / (24 * 60 * 60))
          let h = Math.floor((date % (24 * 60 * 60)) / 3600)
          let m = Math.floor((date % 3600) / 60)
          let s = Math.floor((date % 3600) % 60)

          self.averageResponseTime.days = d >= 0 ? d : 0
          self.averageResponseTime.hours = h >= 0 ? h : 0
          self.averageResponseTime.mins = m >= 0 ? m : 0
          self.averageResponseTime.secs = s >= 0 ? s : 0
          if (self.averageResponseTime.days > 0) {
            self.averageResponseTime.timedays = self.averageResponseTime.days
          } else if (self.averageResponseTime.hours > 0) {
            self.averageResponseTime.timehours =
              self.averageResponseTime.hours +
              '.' +
              self.averageResponseTime.mins
          } else if (self.averageResponseTime.mins > 0) {
            self.averageResponseTime.timemins = self.averageResponseTime.mins
          }
        }
        if (self.previousMonthAverageRes > 0) {
          let dateObj = self.previousMonthAverageRes
          let date
          date = dateObj / 1000
          if (date < 0 && date > -2) {
            date = 0
          }
          let d = Math.floor(date / (24 * 60 * 60))
          let h = Math.floor((date % (24 * 60 * 60)) / 3600)
          let m = Math.floor((date % 3600) / 60)
          let s = Math.floor((date % 3600) % 60)

          self.previousMonthAverageResponseTime.days = d >= 0 ? d : 0
          self.previousMonthAverageResponseTime.hours = h >= 0 ? h : 0
          self.previousMonthAverageResponseTime.mins = m >= 0 ? m : 0
          if (self.previousMonthAverageResponseTime.days > 0) {
            self.previousMonthAverageResponseTime.timedays =
              self.previousMonthAverageResponseTime.days
          } else if (self.previousMonthAverageResponseTime.hours > 0) {
            self.previousMonthAverageResponseTime.timehours =
              self.previousMonthAverageResponseTime.hours +
              '.' +
              self.previousMonthAverageResponseTime.mins
          } else if (self.previousMonthAverageResponseTime.mins > 0) {
            self.previousMonthAverageResponseTime.timemins =
              self.previousMonthAverageResponseTime.mins
          }
        }
        self.avgResponseLoading = false
      })
    },
    getFieldDrop(approver) {
      approver.approversId = null
      approver.approvesDataType = []
      if (approver.sharingType === 1) {
        approver.approvesDataType = this.users
      } else if (approver.sharingType === 2) {
        approver.approvesDataType = this.roles
      } else if (approver.sharingType === 3) {
        approver.approvesDataType = this.teams
      }
    },
    loadResourcePlanner(id) {
      return this.$http.get(`v2/workorders/pmResourcePlanner?id=${id}`)
    },
    loadPreventiveSummary(resourcePlanners) {
      this.loading = true
      if (this.$route.params.id) {
        return this.$http
          .get(
            'v2/workorders/pmDetail?hidePMResourcePlanner=true&id=' +
              parseInt(this.$route.params.id)
          )
          .then(response => {
            this.loadWorkorders()
            this.workorder = response.data.workorder
            this.photoMandatory = this.workorder.photoMandatory
            this.allowNegativePreRequisite = this.workorder.allowNegativePreRequisite
            this.allowNegative = this.workorder.allowNegativePreRequisite === 3
            this.approvers = response.data.prerequisiteApproverTemplates
            if (
              response.data.prerequisiteApproverTemplates &&
              response.data.prerequisiteApproverTemplates.length > 0
            ) {
              response.data.prerequisiteApproverTemplates.forEach(approver => {
                this.getFieldDrop(approver)
                if (approver.sharingType === 1) {
                  approver.approversId = approver.userId
                } else if (approver.sharingType === 2) {
                  approver.approversId = parseInt(approver.roleId)
                } else if (approver.tysharingTypepe === 3) {
                  approver.approversId = parseInt(approver.groupId)
                }
              })
              this.approvers = response.data.prerequisiteApproverTemplates
            }

            this.pmObject = response.data
            this.pm = response.data.preventivemaintenance
            this.loadModuleInfo(response.data.preventivemaintenance)
            this.loadPmmetaInfo()

            if (this.pm.pmCreationType) {
              if (resourcePlanners) {
                resourcePlanners.forEach(r => {
                  this.resources.push({
                    id: r.resourceId,
                    name: r.resourceName,
                    assignedToId: r.assignedToId,
                  })
                })
              }
            }
            if (this.pm) {
              if (this.pm.pmCreationType === 2) {
                this.resType = 'Building'
                this.resName = this.buildingList[Number(this.pm.baseSpaceId)]
              } else if (
                this.pm.pmCreationType === 2 &&
                this.pm.assignmentType === 1
              ) {
                this.resType = 'Floors'
                this.resName = this.buildingList[Number(this.pm.baseSpaceId)]
              }
            }
            if (this.pm.triggers && this.pm.triggers.length) {
              this.resources.sort(function(a, b) {
                let nameA = a.name.toUpperCase()
                let nameB = b.name.toUpperCase()
                if (nameA < nameB) {
                  return -1
                }
                if (nameA > nameB) {
                  return 1
                }
                return 0
              })
            }

            if (
              response.data.listOfTasks &&
              response.data.listOfTasks[0].sequence !== -1
            ) {
              this.sectionList = response.data.sectionTemplates
                ? response.data.sectionTemplates
                : []
              let sorted = this.sort(response.data.listOfTasks)
              this.taskList = sorted.map(task => {
                if (task.options) {
                  task.options = task.options.map(option => ({
                    name: option,
                  }))
                }
                task.enableInput = task.inputType > 1
                task.inputType += ''
                task.validation = 'none'
                task.inputValidationRuleId = -1
                if (task.readingRules) {
                  task.readingRules.forEach(rule => {
                    task.inputValidationRuleId = rule.id
                    if (rule.workflow.resultEvaluator === '(b!=-1)&&(a<b)') {
                      task.validation = 'incremental'
                    } else if (
                      rule.workflow.resultEvaluator === '(b!=-1)&&(a>b)'
                    ) {
                      task.validation = 'decremental'
                    } else if (
                      rule.workflow.resultEvaluator ===
                        '(b!=-1&&a<b)||(c!=-1&&a>c)' &&
                      rule.workflow.expressions
                    ) {
                      task.validation = 'safeLimit'
                      let min = rule.workflow.expressions.find(val => {
                        return val.name === 'b'
                      }).constant
                      task.minSafeLimit = min === '-1' ? null : min
                      let max = rule.workflow.expressions.find(val => {
                        return val.name === 'c'
                      }).constant
                      task.maxSafeLimit = max === '-1' ? null : max
                    }
                  })
                }
                return task
              })
              let currentSectionId = -1
              let currentSectionTasks = []
              let newTasjList = []
              let pmResourceLabel = ''
              if (
                this.pm.pmCreationType === 1 &&
                this.workorder &&
                this.workorder.resource
              ) {
                pmResourceLabel = this.workorder.resource.name
              }
              this.taskList.forEach((ele, index) => {
                if (ele.sectionId === currentSectionId) {
                  let sec = this.sectionList
                    ? this.sectionList.find(
                        section => section.id === currentSectionId
                      )
                    : null
                  let assignmentType = sec ? sec.assignmentType : null
                  let categoryId =
                    assignmentType === 3
                      ? sec.spaceCategoryId
                      : assignmentType === 4
                      ? sec.assetCategoryId
                      : null

                  let resourceCount = sec ? sec.pmIncludeExcludeCount : null

                  let parentAssingmentType = this.pm.assignmentType
                  let parentCategoryId =
                    parentAssingmentType === 3
                      ? this.pm.spaceCategoryId
                      : parentAssingmentType === 4
                      ? this.pm.assetCategoryId
                      : null
                  let parentResourceCount = this.pm.pmIncludeExcludeCount

                  let taskTemplate =
                    sec && sec.taskTemplates
                      ? sec.taskTemplates.find(t => t.sequence === ele.sequence)
                      : null
                  if (taskTemplate) {
                    let sectionAssignmentType = sec ? sec.assignmentType : null
                    let sectionResourceCount = sec
                      ? sec.pmIncludeExcludeCount
                      : null
                    let sectionCategoryId =
                      assignmentType === 3
                        ? sec.spaceCategoryId
                        : assignmentType === 4
                        ? sec.assetCategoryId
                        : null
                    if (sectionAssignmentType === 5) {
                      sectionAssignmentType = parentAssingmentType
                      sectionResourceCount = parentResourceCount
                      sectionCategoryId = parentCategoryId
                    }
                    let taskAssignmentType = taskTemplate.assignmentType
                    let taskCategoryId =
                      taskAssignmentType === 3
                        ? taskTemplate.spaceCategoryId
                        : taskAssignmentType === 4
                        ? taskTemplate.assetCategoryId
                        : null
                    let taskResourceCount = taskTemplate.pmIncludeExcludeCount
                    ele.resourceLabel = this.resourceLabel({
                      pmCreationType: this.pm.pmCreationType,
                      assignmentType: taskAssignmentType,
                      categoryId: taskCategoryId,
                      resourceCount: taskResourceCount,
                      parentAssingmentType: sectionAssignmentType,
                      parentCategoryId: sectionCategoryId,
                      parentResourceCount: sectionResourceCount,
                      pmResourceLabel: pmResourceLabel,
                    })
                    if (
                      taskTemplate.additionInfo &&
                      taskTemplate.additionInfo.options
                    ) {
                      ele.options = taskTemplate.additionInfo.options.map(
                        option => ({
                          name: option,
                        })
                      )
                    }
                  }
                  currentSectionTasks.push(ele)

                  if (index === this.taskList.length - 1) {
                    if (ele.sectionId === -1) {
                      newTasjList.push({
                        sectionId: -1,
                        sectionName: 'default',
                        tasks: currentSectionTasks,
                        resourceLabel: this.resourceLabel({
                          pmCreationType: this.pm.pmCreationType,
                          assignmentType,
                          categoryId,
                          resourceCount,
                          parentAssingmentType,
                          parentCategoryId,
                          parentResourceCount,
                          pmResourceLabel: pmResourceLabel,
                        }),
                      })
                    } else {
                      newTasjList.push({
                        sectionId: sec.id,
                        sectionName: sec.name,
                        tasks: currentSectionTasks,
                        resourceLabel: this.resourceLabel({
                          pmCreationType: this.pm.pmCreationType,
                          assignmentType,
                          categoryId,
                          resourceCount,
                          parentAssingmentType,
                          parentCategoryId,
                          parentResourceCount,
                          pmResourceLabel: pmResourceLabel,
                        }),
                      })
                    }
                  }
                } else {
                  let sec = this.sectionList
                    ? this.sectionList.find(
                        section => section.id === currentSectionId
                      )
                    : null
                  let assignmentType = sec ? sec.assignmentType : null
                  let categoryId =
                    assignmentType === 3
                      ? sec.spaceCategoryId
                      : assignmentType === 4
                      ? sec.assetCategoryId
                      : null
                  let resourceCount = sec ? sec.pmIncludeExcludeCount : null

                  let parentAssingmentType = this.pm.assignmentType
                  let parentCategoryId =
                    parentAssingmentType === 3
                      ? this.pm.spaceCategoryId
                      : parentAssingmentType === 4
                      ? this.pm.assetCategoryId
                      : null
                  let parentResourceCount = this.pm.pmIncludeExcludeCount

                  if (sec) {
                    newTasjList.push({
                      sectionId: sec.id,
                      sectionName: sec.name,
                      tasks: currentSectionTasks,
                      resourceLabel: this.resourceLabel({
                        pmCreationType: this.pm.pmCreationType,
                        assignmentType,
                        categoryId,
                        resourceCount,
                        parentAssingmentType,
                        parentCategoryId,
                        parentResourceCount,
                        pmResourceLabel: pmResourceLabel,
                      }),
                    })
                  } else if (currentSectionId === -1) {
                    newTasjList.push({
                      sectionId: -1,
                      sectionName: 'default',
                      tasks: currentSectionTasks,
                    })
                  }
                  currentSectionTasks = [ele]
                  currentSectionId = ele.sectionId
                  sec = this.sectionList
                    ? this.sectionList.find(
                        section => section.id === currentSectionId
                      )
                    : null

                  assignmentType = sec ? sec.assignmentType : null
                  resourceCount = sec ? sec.pmIncludeExcludeCount : null
                  categoryId =
                    assignmentType === 3
                      ? sec.spaceCategoryId
                      : assignmentType === 4
                      ? sec.assetCategoryId
                      : null

                  if (sec) {
                    let taskTemplate = sec.taskTemplates
                      ? sec.taskTemplates.find(t => t.sequence === ele.sequence)
                      : null
                    if (taskTemplate) {
                      let sectionAssignmentType = assignmentType
                      let sectionResourceCount = resourceCount
                      let sectionCategoryId
                      if (sectionAssignmentType === 5) {
                        sectionCategoryId = parentCategoryId
                      } else {
                        sectionCategoryId =
                          assignmentType === 3
                            ? sec.spaceCategoryId
                            : assignmentType === 4
                            ? sec.assetCategoryId
                            : null
                      }
                      if (assignmentType === 5) {
                        sectionAssignmentType = parentAssingmentType
                        sectionResourceCount = this.pm.pmIncludeExcludeCount
                      }
                      let taskAssignmentType = taskTemplate.assignmentType
                      let taskCategoryId =
                        taskAssignmentType === 3
                          ? taskTemplate.spaceCategoryId
                          : taskAssignmentType === 4
                          ? taskTemplate.assetCategoryId
                          : null

                      let taskResourceCount = taskTemplate.pmIncludeExcludeCount
                      ele.resourceLabel = this.resourceLabel({
                        pmCreationType: this.pm.pmCreationType,
                        assignmentType: taskAssignmentType,
                        categoryId: taskCategoryId,
                        resourceCount: taskResourceCount,
                        parentAssingmentType: sectionAssignmentType,
                        parentCategoryId: sectionCategoryId,
                        parentResourceCount: sectionResourceCount,
                        pmResourceLabel: pmResourceLabel,
                      })
                      if (
                        taskTemplate.additionInfo &&
                        taskTemplate.additionInfo.options
                      ) {
                        ele.options = taskTemplate.additionInfo.options.map(
                          option => ({
                            name: option,
                          })
                        )
                      }
                    }
                  }

                  if (index === this.taskList.length - 1) {
                    newTasjList.push({
                      sectionId: sec.id,
                      sectionName: sec.name,
                      tasks: currentSectionTasks,
                      resourceLabel: this.resourceLabel({
                        pmCreationType: this.pm.pmCreationType,
                        assignmentType,
                        categoryId,
                        resourceCount,
                        parentAssingmentType,
                        parentCategoryId,
                        parentResourceCount,
                        pmResourceLabel: pmResourceLabel,
                      }),
                    })
                  }
                }
              })
              this.newViewTasks = newTasjList
              this.isNewViewTemp = true
            } else {
              this.tasks = response.data.taskList
              this.isNewViewTemp = false
              this.setTasksList()
            }
            if (
              !isEmpty(response.data.listOfPreRequests) &&
              response.data.listOfPreRequests[0].sequence !== -1
            ) {
              this.sectionList = response.data.preRequestSectionTemplates
                ? response.data.preRequestSectionTemplates
                : []
              let sorted = this.sort(response.data.listOfPreRequests)
              this.taskList = sorted.map(task => {
                if (task.options) {
                  task.options = task.options.map(option => ({ name: option }))
                }
                task.enableInput = task.inputType > 1
                task.inputType += ''
                task.validation = 'none'
                task.inputValidationRuleId = -1
                return task
              })
              let currentSectionId = -1
              let currentSectionTasks = []
              let newTasjList = []
              let pmResourceLabel = ''
              if (
                this.pm.pmCreationType === 1 &&
                this.workorder &&
                this.workorder.resource
              ) {
                pmResourceLabel = this.workorder.resource.name
              }

              this.taskList.forEach((ele, index) => {
                if (ele.sectionId === currentSectionId) {
                  let sec = this.sectionList.find(
                    section => section.id === currentSectionId
                  )
                  let assignmentType = sec ? sec.assignmentType : null
                  let categoryId =
                    assignmentType === 3
                      ? sec.spaceCategoryId
                      : assignmentType === 4
                      ? sec.assetCategoryId
                      : null
                  let resourceCount = sec ? sec.pmIncludeExcludeCount : null
                  let parentAssingmentType = this.pm.assignmentType
                  let parentCategoryId =
                    parentAssingmentType === 3
                      ? this.pm.spaceCategoryId
                      : parentAssingmentType === 4
                      ? this.pm.assetCategoryId
                      : null
                  let parentResourceCount = this.pm.pmIncludeExcludeCount

                  let taskTemplate =
                    sec && sec.taskTemplates
                      ? sec.taskTemplates.find(t => t.sequence === ele.sequence)
                      : null
                  if (taskTemplate) {
                    let sectionAssignmentType = sec ? sec.assignmentType : null
                    let sectionResourceCount = sec
                      ? sec.pmIncludeExcludeCount
                      : null
                    let sectionCategoryId =
                      assignmentType === 3
                        ? sec.spaceCategoryId
                        : assignmentType === 4
                        ? sec.assetCategoryId
                        : null
                    if (sectionAssignmentType === 5) {
                      sectionAssignmentType = parentAssingmentType
                      sectionCategoryId = parentCategoryId
                    }
                    let taskAssignmentType = taskTemplate.assignmentType
                    let taskCategoryId =
                      taskAssignmentType === 3
                        ? taskTemplate.spaceCategoryId
                        : taskAssignmentType === 4
                        ? taskTemplate.assetCategoryId
                        : null

                    let taskResourceCount = taskTemplate.pmIncludeExcludeCount
                    ele.resourceLabel = this.resourceLabel({
                      pmCreationType: this.pm.pmCreationType,
                      assignmentType: taskAssignmentType,
                      categoryId: taskCategoryId,
                      resourceCount: taskResourceCount,
                      parentAssingmentType: sectionAssignmentType,
                      parentCategoryId: sectionCategoryId,
                      parentResourceCount: sectionResourceCount,
                      pmResourceLabel: pmResourceLabel,
                    })
                    if (
                      taskTemplate.additionInfo &&
                      taskTemplate.additionInfo.options
                    ) {
                      ele.options = taskTemplate.additionInfo.options.map(
                        option => ({ name: option })
                      )
                    }
                    if (
                      taskTemplate.additionInfo &&
                      taskTemplate.additionInfo.truevalue
                    ) {
                      ele.truevalue = taskTemplate.additionInfo.truevalue
                    }
                    if (
                      taskTemplate.additionInfo &&
                      taskTemplate.additionInfo.falsevalue
                    ) {
                      ele.falsevalue = taskTemplate.additionInfo.falsevalue
                    }
                  }
                  currentSectionTasks.push(ele)
                  if (index === this.taskList.length - 1) {
                    if (ele.sectionId === -1) {
                      newTasjList.push({
                        sectionId: -1,
                        sectionName: 'default',
                        preRequests: currentSectionTasks,
                        resourceLabel: this.resourceLabel({
                          pmCreationType: this.pm.pmCreationType,
                          assignmentType,
                          categoryId,
                          resourceCount,
                          parentAssingmentType,
                          parentCategoryId,
                          parentResourceCount,
                          pmResourceLabel: pmResourceLabel,
                        }),
                      })
                    } else {
                      newTasjList.push({
                        sectionId: sec.id,
                        sectionName: sec.name,
                        preRequests: currentSectionTasks,
                        resourceLabel: this.resourceLabel({
                          pmCreationType: this.pm.pmCreationType,
                          assignmentType,
                          categoryId,
                          resourceCount,
                          parentAssingmentType,
                          parentCategoryId,
                          parentResourceCount,
                          pmResourceLabel: pmResourceLabel,
                        }),
                      })
                    }
                  }
                } else {
                  let sec = this.sectionList.find(
                    section => section.id === currentSectionId
                  )
                  let assignmentType = sec ? sec.assignmentType : null
                  let categoryId =
                    assignmentType === 3
                      ? sec.spaceCategoryId
                      : assignmentType === 4
                      ? sec.assetCategoryId
                      : null
                  let resourceCount = sec ? sec.pmIncludeExcludeCount : null
                  let parentAssingmentType = this.pm.assignmentType
                  let parentCategoryId =
                    parentAssingmentType === 3
                      ? this.pm.spaceCategoryId
                      : parentAssingmentType === 4
                      ? this.pm.assetCategoryId
                      : null
                  let parentResourceCount = this.pm.pmIncludeExcludeCount
                  if (sec) {
                    newTasjList.push({
                      sectionId: sec.id,
                      sectionName: sec.name,
                      preRequests: currentSectionTasks,
                      resourceLabel: this.resourceLabel({
                        pmCreationType: this.pm.pmCreationType,
                        assignmentType,
                        categoryId,
                        resourceCount,
                        parentAssingmentType,
                        parentCategoryId,
                        parentResourceCount,
                        pmResourceLabel: pmResourceLabel,
                      }),
                    })
                  } else if (currentSectionId === -1) {
                    newTasjList.push({
                      sectionId: -1,
                      sectionName: 'default',
                      preRequests: currentSectionTasks,
                    })
                  }
                  currentSectionTasks = [ele]
                  currentSectionId = ele.sectionId
                  sec = this.sectionList.find(
                    section => section.id === currentSectionId
                  )
                  assignmentType = sec ? sec.assignmentType : null
                  resourceCount = sec ? sec.pmIncludeExcludeCount : null
                  categoryId =
                    assignmentType === 3
                      ? sec.spaceCategoryId
                      : assignmentType === 4
                      ? sec.assetCategoryId
                      : null
                  if (sec) {
                    let taskTemplate = sec.taskTemplates.find(
                      t => t.sequence === ele.sequence
                    )
                    if (taskTemplate) {
                      let sectionAssignmentType = assignmentType
                      let sectionResourceCount = resourceCount
                      let sectionCategoryId
                      if (sectionAssignmentType === 5) {
                        sectionCategoryId = parentCategoryId
                      } else {
                        sectionCategoryId =
                          assignmentType === 3
                            ? sec.spaceCategoryId
                            : assignmentType === 4
                            ? sec.assetCategoryId
                            : null
                      }
                      if (assignmentType === 5) {
                        sectionAssignmentType = parentAssingmentType
                        sectionResourceCount = this.pm.pmIncludeExcludeCount
                      }
                      let taskAssignmentType = taskTemplate.assignmentType
                      let taskCategoryId =
                        taskAssignmentType === 3
                          ? taskTemplate.spaceCategoryId
                          : taskAssignmentType === 4
                          ? taskTemplate.assetCategoryId
                          : null

                      let taskResourceCount = taskTemplate.pmIncludeExcludeCount
                      ele.resourceLabel = this.resourceLabel({
                        pmCreationType: this.pm.pmCreationType,
                        assignmentType: taskAssignmentType,
                        categoryId: taskCategoryId,
                        resourceCount: taskResourceCount,
                        parentAssingmentType: sectionAssignmentType,
                        parentCategoryId: sectionCategoryId,
                        parentResourceCount: sectionResourceCount,
                        pmResourceLabel: pmResourceLabel,
                      })

                      if (
                        taskTemplate.additionInfo &&
                        taskTemplate.additionInfo.options
                      ) {
                        ele.options = taskTemplate.additionInfo.options.map(
                          option => ({ name: option })
                        )
                      }
                      if (
                        taskTemplate.additionInfo &&
                        taskTemplate.additionInfo.truevalue
                      ) {
                        ele.truevalue = taskTemplate.additionInfo.truevalue
                      }
                      if (
                        taskTemplate.additionInfo &&
                        taskTemplate.additionInfo.falsevalue
                      ) {
                        ele.falsevalue = taskTemplate.additionInfo.falsevalue
                      }
                    }
                  }

                  if (index === this.taskList.length - 1) {
                    newTasjList.push({
                      sectionId: sec.id,
                      sectionName: sec.name,
                      preRequests: currentSectionTasks,
                      resourceLabel: this.resourceLabel({
                        pmCreationType: this.pm.pmCreationType,
                        assignmentType,
                        categoryId,
                        resourceCount,
                        parentAssingmentType,
                        parentCategoryId,
                        parentResourceCount,
                        pmResourceLabel: pmResourceLabel,
                      }),
                    })
                  }
                }
              })
              this.newViewPreRequests = newTasjList
              this.isNewViewTempPreRequest = true
            } else {
              this.preRequests = response.data.preRequestList
              this.isNewViewTempPreRequest = false
              this.setPreRequestsList()
            }
            this.reminders = response.data.reminders
            this.model['emitForm'] = false
            this.model['isEdit'] = true
            this.model['isLoading'] = false
            this.model['isEdit'] = true
            this.model['taskData'] = {
              taskSections: [this.newViewTasks],
            }
            this.model['triggerData'] = {
              reminders: [this.reminders],
            }
            this.loading = false
            if (
              resourcePlanners &&
              resourcePlanners.length &&
              resourcePlanners[0].resource
            ) {
              this.resourceTypeEnum = resourcePlanners[0].resource
                .resourceTypeEnum
                ? resourcePlanners[0].resource.resourceTypeEnum
                : null
            }
            this.loadSite()
          })
          .then(() => {
            this.$util
              .loadReadingFields(this.workorder.resource)
              .then(response => {
                this.readingFields = response
                this.setReadingFieldsForTriggers()
              })
            this.getAverageWoResponseWorkflow()
            this.fetchCard('woOnTime')
            this.fetchPreviousMonthCard('woOnTime')
            this.fetchPreviousMonthCard('woAvgRes')
            this.fetchCard('woFreqAssign')
            this.fetchCard('woAvgRes')
          })
      }
    },
    getPMType(type) {
      return this.tickettype[type]
    },
    openWorkOrder(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

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
        this.$router.push({
          path: '/app/wo/orders/summary/' + id,
        })
      }
    },
    preWoValidation() {
      if (!this.pm.markIgnoredWo) {
        this.executePM()
        return
      }
      this.$dialog
        .confirm({
          title: 'Skip Ignored Workorder',
          message:
            'Do you want to skip workorders if not started till next due ?',
          rbClass: 'pm-sum-btn-green',
          className: 'pmsummary-wo-create-dialog',
          rbLabel: 'Execute PM',
        })
        .then(confirmExecute => {
          if (confirmExecute) {
            this.executePM()
          }
        })
    },
    executePM() {
      if (!this.pm) {
        return
      }
      this.isExecuting = true
      if (this.pm.pmCreationType === 2) {
        this.showExecutionConfig = true
        return
      }

      let data = {}
      data.pmId = this.pm.id
      this.$http
        .post('/workorder/executePreventiveMaintenance', data)
        .then(response => {
          let wo = response.data.workorder
          if (wo) {
            this.workorderObj = wo
            this.workordersArr.unshift(wo)

            this.$dialog
              .confirm({
                title: 'Pm Executed',
                message:
                  this.workorder.subject + ' has been successfully created.',
                rbClass: 'pm-sum-btn-green',
                className: 'pmsummary-wo-create-dialog',
                rbLabel: 'Go to workorder',
              })
              .then(confirmDelete => {
                if (confirmDelete) {
                  this.openWorkOrder(wo.id)
                }
              })
            this.isExecuting = false
            this.openPopOver = true
          }
        })
    },
    beforeConfigClose(done) {
      this.resources.forEach(i => {
        i.selected = false
      })
      this.selectAllResources = false
      this.isExecuting = false
      if (done) {
        done()
      }
    },
    openAsset(id) {
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}

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
        this.$router.push({
          path: '/app/at/assets/all/' + id + '/overview',
        })
      }
    },
    showDetails(task) {
      this.showPreRequestDetails = false
      this.selectedPreRequest = null
      this.showTaskDetails = true
      this.selectedTask = task
      this.pmDetailsview = false
    },
    showDetailsPreRequest(preRequest) {
      this.showTaskDetails = false
      this.selectedTask = null
      this.showPreRequestDetails = true
      this.selectedPreRequest = preRequest
      this.pmDetailsview = false
    },
    setTasksList() {
      for (let section in this.tasks) {
        if (this.tasks.hasOwnProperty(section)) {
          this.tasks[section] = this.tasks[section].map(task => {
            if (task.options) {
              task.options = task.options.map(option => ({
                name: option,
              }))
            }
            task.enableInput = task.inputType > 1
            task.inputType += ''
            return task
          })
        }
      }
    },
    setPreRequestsList() {
      for (let section in this.preRequests) {
        if (this.preRequests.hasOwnProperty(section)) {
          this.preRequests[section] = this.preRequests[section].map(task => {
            if (task.options) {
              task.options = task.options.map(option => ({ name: option }))
            }
            task.enableInput = task.inputType > 1
            task.inputType += ''
            return task
          })
        }
      }
    },
    setReadingFieldsForTriggers() {
      if (this.pm.triggers) {
        this.pm.triggers = this.pm.triggers.map(trigger => {
          if (!trigger.schedule && this.readingFields) {
            trigger.readingField = this.readingFields.find(
              field => field.id === trigger.readingFieldId
            )
          }
          return trigger
        })
      }
    },
    fetchPreviousMonthCard(type) {
      let workflow
      if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
        if (type === 'woOnTime') {
          workflow = this.getNewPreviousMonthWoOnTimeWorkflow()
        } else if (type === 'woAvgRes') {
          workflow = this.getNewPreviousMonthWoAvgResWorkflow()
        }
      } else {
        if (type === 'woOnTime') {
          workflow = this.getPreviousMonthWoOnTimeWorkflow()
        } else if (type === 'woAvgRes') {
          workflow = this.getPreviousMonthWoAvgResWorkflow()
        }
      }
      if (workflow) {
        this.workflowResult[type].loading = true
        return new Promise((resolve, reject) => {
          this.$http
            .post('/v2/executeworkflow', {
              workflow: workflow,
            })
            .then(response => {
              if (response.data.responseCode === 0) {
                this.previousMonthworkflowResult[type].result =
                  response.data.result.workflowResult
                resolve(response.data.result)
              }
            })
        })
      }
    },
    fetchCard(type) {
      let workflow
      if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
        if (type === 'woOnTime') {
          workflow = this.getNewWoOnTimeWorkflow()
        } else if (type === 'woAvgRes') {
          workflow = this.getNewWoAvgResWorkflow()
        } else if (type === 'woFreqAssign') {
          workflow = this.getNewWoFreqAssignWorkflow()
        }
      } else {
        if (type === 'woOnTime') {
          workflow = this.getWoOnTimeWorkflow()
        } else if (type === 'woAvgRes') {
          workflow = this.getWoAvgResWorkflow()
        } else if (type === 'woFreqAssign') {
          workflow = this.getWoFreqAssignWorkflow()
        }
      }
      if (workflow) {
        this.workflowResult[type].loading = true
        return new Promise((resolve, reject) => {
          this.$http
            .post('/v2/executeworkflow', {
              workflow: workflow,
            })
            .then(response => {
              this.workflowResult[type].loading = false
              if (response.data.responseCode === 0) {
                this.workflowResult[type].result =
                  response.data.result.workflowResult
                resolve(response.data.result)
              }
            })
        })
      }
    },
    getNewWoAvgResWorkflow() {
      if (!this.pm || !this.workordersArr) {
        return 0
      }
      let expressions = [
        {
          name: 'a',
          fieldName: 'actualWorkDuration',
          aggregateString: 'avg',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1 and 2 and 3)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
              2: {
                fieldName: 'jobStatus',
                operatorId: 9,
                sequence: '2',
                value: 3,
              },
              3: {
                fieldName: 'createdTime',
                operatorId: 44,
                sequence: '3',
              },
            },
          },
        },
      ]
      return {
        expressions: expressions,
      }
    },
    getNewWoFreqAssignWorkflow() {
      if (!this.pm || !this.workordersArr) {
        return 0
      }
      let expressions = [
        {
          name: 'a',
          fieldName: 'assignedTo',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
              2: {
                fieldName: 'jobStatus',
                operatorId: 9,
                sequence: '2',
                value: 3,
              },
            },
          },
          groupBy: 'assignedTo',
          orderByFieldName: 'count(*)',
          sortBy: 'desc',
          limit: 1,
        },
      ]
      return {
        expressions: expressions,
      }
    },
    getNewWoOnTimeWorkflow() {
      if (!this.pm || !this.workordersArr || !this.workordersArr[0]) {
        return 0
      }
      let expressions = [
        {
          name: 'a',
          fieldName: 'dueDate',
          aggregateString: 'count',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1 and 2 and 3 and 4)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
              2: {
                fieldName: 'actualWorkEnd',
                operatorId: 18,
                sequence: '2',
                value: 'DUE_DATE',
              },
              3: {
                fieldName: 'jobStatus',
                operatorId: 9,
                sequence: '3',
                value: 3,
              },
              4: {
                fieldName: 'createdTime',
                operatorId: 44,
                sequence: '4',
              },
            },
          },
        },
        {
          name: 'b',
          fieldName: 'id',
          aggregateString: 'count',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
              2: {
                fieldName: 'jobStatus',
                operatorId: 9,
                sequence: '2',
                value: 3,
              },
            },
          },
        },
      ]
      return {
        expressions: expressions,
        resultEvaluator: '(a/b)*100',
      }
    },
    getWoOnTimeWorkflow() {
      if (!this.pm || !this.workordersArr || !this.workordersArr[0]) {
        return 0
      }
      let expressions = [
        {
          name: 'a',
          fieldName: 'dueDate',
          aggregateString: 'count',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1 and 2 and 3)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
              2: {
                fieldName: 'actualWorkEnd',
                operatorId: 18,
                sequence: '2',
                value: 'DUE_DATE',
              },
              3: {
                fieldName: 'createdTime',
                operatorId: 44,
                sequence: '3',
              },
            },
          },
        },
        {
          name: 'b',
          fieldName: 'id',
          aggregateString: 'count',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
            },
          },
        },
      ]
      return {
        expressions: expressions,
        resultEvaluator: '(a/b)*100',
      }
    },
    getNewPreviousMonthWoOnTimeWorkflow() {
      if (!this.pm || !this.workordersArr || !this.workordersArr[0]) {
        return 0
      }
      let m = moment(new Date()).format('DD MMM YYYY')
      let dateFrom = moment(m)
        .subtract(1, 'months')
        .endOf('month')
        .format('YYYY-MM-DD')
      let lastMonth = moment(dateFrom, 'YYYY/MM/DD').valueOf()
      if (this.pm.createdTime < lastMonth) {
        let expressions = [
          {
            name: 'a',
            fieldName: 'dueDate',
            aggregateString: 'count',
            moduleName: 'workorder',
            criteria: {
              pattern: '(1 and 2 and 3 and 4)',
              conditions: {
                1: {
                  fieldName: 'pm',
                  operatorId: 36,
                  sequence: '1',
                  value: this.pm.id,
                },
                2: {
                  fieldName: 'actualWorkEnd',
                  operatorId: 18,
                  sequence: '2',
                  value: 'DUE_DATE',
                },
                3: {
                  fieldName: 'createdTime',
                  operatorId: 20,
                  sequence: '3',
                  value: this.pm.createdTime + ',' + lastMonth,
                },
                4: {
                  fieldName: 'jobStatus',
                  operatorId: 9,
                  sequence: '4',
                  value: 3,
                },
              },
            },
          },
          {
            name: 'b',
            fieldName: 'id',
            aggregateString: 'count',
            moduleName: 'workorder',
            criteria: {
              pattern: '(1)',
              conditions: {
                1: {
                  fieldName: 'pm',
                  operatorId: 36,
                  sequence: '1',
                  value: this.pm.id,
                },
              },
            },
          },
        ]
        return {
          expressions: expressions,
          resultEvaluator: '(a/b)*100',
        }
      } else {
        return 0
      }
    },
    getPreviousMonthWoOnTimeWorkflow() {
      if (!this.pm || !this.workordersArr || !this.workordersArr[0]) {
        return 0
      }
      let m = moment(new Date()).format('DD MMM YYYY')
      let dateFrom = moment(m)
        .subtract(1, 'months')
        .endOf('month')
        .format('YYYY-MM-DD')
      let lastMonth = moment(dateFrom, 'YYYY/MM/DD').valueOf()
      if (this.pm.createdTime < lastMonth) {
        let expressions = [
          {
            name: 'a',
            fieldName: 'dueDate',
            aggregateString: 'count',
            moduleName: 'workorder',
            criteria: {
              pattern: '(1 and 2 and 3)',
              conditions: {
                1: {
                  fieldName: 'pm',
                  operatorId: 36,
                  sequence: '1',
                  value: this.pm.id,
                },
                2: {
                  fieldName: 'actualWorkEnd',
                  operatorId: 18,
                  sequence: '2',
                  value: 'DUE_DATE',
                },
                3: {
                  fieldName: 'createdTime',
                  operatorId: 20,
                  sequence: '3',
                  value: this.pm.createdTime + ',' + lastMonth,
                },
              },
            },
          },
          {
            name: 'b',
            fieldName: 'id',
            aggregateString: 'count',
            moduleName: 'workorder',
            criteria: {
              pattern: '(1)',
              conditions: {
                1: {
                  fieldName: 'pm',
                  operatorId: 36,
                  sequence: '1',
                  value: this.pm.id,
                },
              },
            },
          },
        ]
        return {
          expressions: expressions,
          resultEvaluator: '(a/b)*100',
        }
      } else {
        return 0
      }
    },
    getNewPreviousMonthWoAvgResWorkflow() {
      if (!this.pm || !this.workordersArr) {
        return 0
      }
      let m = moment(new Date()).format('DD MMM YYYY')
      let dateFrom = moment(m)
        .subtract(1, 'months')
        .endOf('month')
        .format('YYYY-MM-DD')
      let lastMonth = moment(dateFrom, 'YYYY/MM/DD').valueOf()
      if (this.pm.createdTime < lastMonth) {
        let expressions = [
          {
            name: 'a',
            fieldName: 'actualWorkDuration',
            aggregateString: 'avg',
            moduleName: 'workorder',
            criteria: {
              pattern: '(1 and 2 and 3)',
              conditions: {
                1: {
                  fieldName: 'pm',
                  operatorId: 36,
                  sequence: '1',
                  value: this.pm.id,
                },
                2: {
                  fieldName: 'createdTime',
                  operatorId: 20,
                  sequence: '2',
                  value: this.pm.createdTime + ',' + lastMonth,
                },
                3: {
                  fieldName: 'jobStatus',
                  operatorId: 9,
                  sequence: '3',
                  value: 3,
                },
              },
            },
          },
        ]
        return {
          expressions: expressions,
        }
      } else {
        return 0
      }
    },
    getPreviousMonthWoAvgResWorkflow() {
      if (!this.pm || !this.workordersArr) {
        return 0
      }
      let m = moment(new Date()).format('DD MMM YYYY')
      let dateFrom = moment(m)
        .subtract(1, 'months')
        .endOf('month')
        .format('YYYY-MM-DD')
      let lastMonth = moment(dateFrom, 'YYYY/MM/DD').valueOf()
      if (this.pm.createdTime < lastMonth) {
        let expressions = [
          {
            name: 'a',
            fieldName: 'actualWorkDuration',
            aggregateString: 'avg',
            moduleName: 'workorder',
            criteria: {
              pattern: '(1 and 2)',
              conditions: {
                1: {
                  fieldName: 'pm',
                  operatorId: 36,
                  sequence: '1',
                  value: this.pm.id,
                },
                2: {
                  fieldName: 'createdTime',
                  operatorId: 20,
                  sequence: '2',
                  value: this.pm.createdTime + ',' + lastMonth,
                },
              },
            },
          },
        ]
        return {
          expressions: expressions,
        }
      } else {
        return 0
      }
    },
    getWoAvgResWorkflow() {
      if (!this.pm || !this.workordersArr) {
        return 0
      }
      let expressions = [
        {
          name: 'a',
          fieldName: 'actualWorkDuration',
          aggregateString: 'avg',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
              2: {
                fieldName: 'createdTime',
                operatorId: 44,
                sequence: '2',
              },
            },
          },
        },
      ]
      return {
        expressions: expressions,
      }
    },
    checkown() {
      return this.$account.user.id === this.workorder.assignedTo.id
    },
    getWoFreqAssignWorkflow() {
      if (!this.pm || !this.workordersArr) {
        return 0
      }
      let expressions = [
        {
          name: 'a',
          fieldName: 'assignedTo',
          moduleName: 'workorder',
          criteria: {
            pattern: '(1)',
            conditions: {
              1: {
                fieldName: 'pm',
                operatorId: 36,
                sequence: '1',
                value: this.pm.id,
              },
            },
          },
          groupBy: 'assignedTo',
          orderByFieldName: 'count(*)',
          sortBy: 'desc',
          limit: 1,
        },
      ]
      return {
        expressions: expressions,
      }
    },
    handlePreview() {},
    handleRemove() {},
    closedisplay() {
      this.selectedTask = null
      this.selectedPreRequest = null
      this.pmDetailsview = true
    },
  },
}
</script>
<style>
.trigger-overview-scroll {
  max-width: 840px;
  height: 100%;
  overflow-y: scroll;
  padding-bottom: 100px;
}
.fc-pm-summary-chart-block .report-userFilter {
  position: absolute;
}
.fc-pm-summary-chart-block .report-userFilter .mL20 {
  margin-left: 30px !important;
}
.fc-pm-summary-chart-block .fc-input-full-border-h35 {
  position: absolute;
  top: 0px;
  z-index: 2;
}
.fc-pm-summary-chart-block .f-singlechart .f-chart-type-single {
  margin-right: 30px;
  position: absolute;
  top: -56px !important;
  right: -3px !important;
}
.fc-pm-summary-chart-block .report-graph-header {
  height: inherit;
}
.fc-pm-summary-chart-block .f-singlechart .fc-new-chart-type-single {
  top: -55px !important;
  right: 0 !important;
}
.asset-one {
  position: absolute;
  top: 70px;
  z-index: 2;
  left: 20px;
  font-size: 14px;
  color: #324056;
  font-weight: 500;
}
.pm-trigger-notify {
  position: absolute;
  top: 110px;
  z-index: 100;
  right: 0;
  left: 0;
}
.pm-reading-vs-task-switch {
  font-size: 12px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: right;
  color: #396dc2;
  position: absolute;
  top: 55px;
  right: 0;
  cursor: pointer;
}
.pm-layout-full-wdith {
  width: 100% !important;
}
.pm-layout-half-wdith {
  width: 70.5% !important;
}
.pm-tabs .el-tabs__nav-wrap::after {
  display: none;
}
.trigger-tab .el-tabs__nav-wrap::after {
  display: block !important;
}
.pm-sum-btn-green {
  width: 50%;
  padding-top: 18px;
  padding-bottom: 18px;
  cursor: pointer;
  background-color: #39b2c2;
  border: transparent;
  letter-spacing: 1.1px !important;
  text-align: center;
  color: #ffffff;
  text-transform: uppercase !important;
  font-weight: 500 !important;
  border-radius: 0;
  float: right;
  line-height: 16px;
  cursor: pointer;
  margin-left: 0 !important;
}
</style>
