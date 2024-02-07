<template>
  <div>
    <!-- alarm header -->
    <div v-if="!alarm"></div>
    <div v-else>
      <div class="alarm-summary-header">
        <div class="fL">
          <div class="fc-id">#{{ alarm.serialNumber }}</div>
          <div class="fc-black3-24 max-width650px textoverflow-height-ellipsis">
            {{ alarm.subject ? alarm.subject : '---' }}
          </div>
          <div>
            <div v-if="alarm.severity" class="flex-middle mT10">
              <div
                class="uppercase secondary-color summaryseverityTag f11"
                v-bind:style="{
                  'background-color': getAlarmSeverity(alarm.severity.id).color,
                }"
              >
                {{
                  alarm.severity.id
                    ? getAlarmSeverity(alarm.severity.id).displayName
                    : '---'
                }}
              </div>
              <div
                class="fc-blue-label mL10 f11 letter-spacing: 0.7px;"
                style="margin-top: -4px"
              >
                {{ alarm.modifiedTime | fromNow }}
              </div>
            </div>
          </div>
        </div>
        <div class="fR">
          <div class="triangle-close">
            <i
              class="close-summary-icon material-icons pull-right pointer"
              @click="closeSummary"
              data-theme="light"
              data-arrow="true"
              v-tippy
              >close</i
            >
          </div>
          <div class="display-flex">
            <el-button
              class="fc-btn-green-lg-fill"
              @click="acknowledgeAlarms()"
              v-if="
                !alarm.isAcknowledged &&
                  isActiveAlarm(alarm) &&
                  $hasPermission('alarm:ACKNOWLEDGE_ALARM')
              "
              >Acknowledge</el-button
            >
            <div
              class="q-item-label fc-summary-ack status-btn1 pR10 pointer"
              v-else-if="!isActiveAlarm(alarm)"
            >
              Unacknowledged
            </div>
            <div class="q-item-label mR10 status-btn2" v-else>
              <span class="pB10 fc-summary-ack ellipsis"
                >Acknowledged by
                {{
                  alarm.acknowledgedBy
                    ? $store.getters.getUser(alarm.acknowledgedBy.id).name
                    : 'Unknown'
                }}</span
              >
              <div class="text-right">
                &nbsp;<span class="fc-summary-timeago text-right">{{
                  alarm.acknowledgedTime > 0
                    ? alarm.acknowledgedTime
                    : new Date() | fromNow
                }}</span>
              </div>
            </div>
            <el-button
              class="fc-btn-green-lg-border"
              v-if="isActiveAlarm(alarm) && $hasPermission('alarm:CLEAR_ALARM')"
              @click="updateAlarmsStatus()"
              >CLEAR</el-button
            >
            <q-icon
              v-if="isActiveAlarm(alarm)"
              slot="right"
              name="more_vert"
              class="fc-btn-ico-lg mL10"
            >
              <q-popover ref="moreactionspopover">
                <q-list link class="no-border">
                  <q-item>
                    <q-item-main label="Delete" @click="deleteAlarmById()" />
                  </q-item>
                  <q-item>
                    <q-item-main
                      label="Export CSV"
                      @click="
                        exportSummary(1), $refs.moreactionspopover.close()
                      "
                    />
                  </q-item>
                  <q-item>
                    <q-item-main
                      label="Export Excel"
                      @click="
                        exportSummary(2), $refs.moreactionspopover.close()
                      "
                    />
                  </q-item>
                  <q-item v-if="alarm.woId > 0">
                    <q-item-main
                      label="View Workorder"
                      @click="
                        openWorkorder(alarm.woId),
                          $refs.moreactionspopover.close()
                      "
                    />
                  </q-item>
                  <q-item v-else-if="$hasPermission('alarm:CREATE_WO')">
                    <q-item-main
                      label="Create Workorder"
                      @click="
                        createWoDialog([alarm.id]),
                          $refs.moreactionspopover.close()
                      "
                    />
                  </q-item>
                </q-list>
              </q-popover>
            </q-icon>
          </div>
        </div>
      </div>
    </div>
    <!-- alarm summary body -->
    <div class="alarm-summary-body clearboth">
      <div class="alarm-summary-body-left">
        <div class="alaram-summary-body-scroll">
          <div class="alarm-summary-inner">
            <!-- first -->
            <div
              class="fc-white-bg-container mB10"
              v-if="!alarm.ruleId && !alarm.readingVal"
            >
              <div class="fc-blue-label f11 text-uppercase letter-spacing07">
                Source
              </div>
              <div class="label-txt-black mT10 line-height20">
                {{ alarm.source ? alarm.source : '---' }}
              </div>
            </div>
            <div
              class="alarm-summary-box-block"
              v-if="
                alarm.sourceTypeEnum &&
                  alarm.sourceTypeEnum === 'THRESHOLD_ALARM'
              "
            >
              <div
                class="alarm-summary-box-left alarm-summary-box mR10"
                v-if="alarm.readingMessage && !alarm.problem"
              >
                <div v-if="alarm && alarm.problem">
                  <div
                    class="fc-blue-label f11 text-uppercase letter-spacing07"
                  >
                    PROBLEM
                  </div>
                  <div class="label-txt-black mT10 line-height20">
                    {{ alarm.problem }}
                  </div>
                </div>
                <div
                  v-else-if="alarm && alarm.readingMessage && !alarm.problem"
                >
                  <div
                    class="fc-blue-label f11 text-uppercase letter-spacing07"
                  >
                    Message
                  </div>
                  <div class="label-txt-black mT10 line-height20">
                    {{ loadmsg() }}
                  </div>
                </div>
              </div>
              <div
                class="alarm-summary-box-left alarm-summary-box"
                :style="alarm.readingMessage ? 'width:100%' : ''"
                v-if="alarm.readingVal && readingField.displayName"
              >
                <el-row>
                  <el-col
                    :span="12"
                    v-if="alarm.readingVal && readingField.displayName"
                  >
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      Threshold Metric
                    </div>
                    <div class="label-txt-black mT10 line-height20">
                      {{
                        alarm.readingFieldId
                          ? readingField
                            ? readingField.displayName
                            : loadFieldDetails()
                          : '---'
                      }}
                    </div>
                  </el-col>
                  <el-col :span="12" v-else> </el-col>
                  <el-col
                    :span="12"
                    class="pL20"
                    v-if="alarm.readingVal && readingField.unit"
                  >
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      RECORDED VALUE
                    </div>
                    <div class="label-txt-black mT10 line-height20">
                      {{ Number(alarm.readingVal).toFixed(2) }}
                      {{ loadReadingField() }}
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
            <!-- impact start-->
            <div class="alarm-summary-box-block mT10" v-if="alarm.data">
              <div class="alarm-summary-box-left alarm-summary-box width100">
                <el-row>
                  <el-col
                    :span="6"
                    v-for="(key, value) in alarm.data"
                    :key="value"
                  >
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      {{ getFieldByName(value).displayName }}
                    </div>
                    <div class="label-txt-black line-height20 mT10">
                      {{
                        $helpers.unitValueString(
                          getFieldByName(value),
                          formattedKey(key)
                        )
                      }}
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
            <!-- impact end -->
            <div class="alarm-summary-box-block mT10">
              <div class="alarm-summary-box-left alarm-summary-box">
                <div class="fc-blue-label f11 text-uppercase letter-spacing07">
                  Condition
                </div>
                <div
                  class="label-txt-black mT10 line-height20"
                  v-if="alarm.condition"
                >
                  {{ alarm.condition ? alarm.condition : '---' }}
                </div>
                <div class="label-txt-black mT10 line-height20" v-else>---</div>
              </div>
              <div class="alarm-summary-box-left alarm-summary-box mL10">
                <el-row>
                  <el-col :span="12">
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      Created On
                    </div>
                    <div
                      class="fc-black-13 mT10 line-height20 text-left"
                      v-if="alarm.createdTime"
                    >
                      {{ alarm.createdTime | formatDate() }}
                    </div>
                    <div class="label-txt-black mT10" v-else>---</div>
                  </el-col>
                  <el-col :span="12" class="pL10 pR0">
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      Last Recorded
                    </div>
                    <div
                      class="fc-black-13 mT10 line-height20 text-left"
                      v-if="alarm.modifiedTime"
                    >
                      {{ alarm.modifiedTime | formatDate() }}
                    </div>
                    <div class="label-txt-black mT10" v-else>---</div>
                  </el-col>
                </el-row>
              </div>
            </div>
            <!-- graph -->
            <div
              class="alarm-summary-graph-container"
              v-if="
                (alarm && alarm.readingVal) ||
                  alarm.sourceType === 12 ||
                  alarm.sourceType === 9
              "
            >
              <div class="alarm-summary-graph-inner">
                <div class="alarm-summary-graph-header">
                  <div class="fc-black-16 bold fL">Alarms Report</div>
                </div>
                <div class="">
                  <f-new-analytic-report
                    v-if="analyticsConfig"
                    class="alarm-summary-chart"
                    ref="newAlarmAnalyticReport"
                    :config="analyticsConfig"
                  ></f-new-analytic-report>
                </div>
                <div class="analytics-txt pB0">
                  <div class="fc-summary-content-div">
                    <div
                      @click="
                        Analytics(alarm.id, null, alarm.sourceType !== 12)
                      "
                      class="content analytics-txt"
                      style="
                        cursor: pointer;
                        color: rgb(57, 178, 194);
                        font-size: 13px;
                        text-align: right;
                        font-weight: 500;
                        margin-right: 20px;
                      "
                    >
                      Go to Analytics
                      <img
                        style="width: 13px; height: 9px"
                        src="~statics/icons/right-arrow.svg"
                      />
                    </div>
                  </div>
                </div>
                <div>
                  <div v-if="alarm.possibleCauses || alarm.recommendation > 0">
                    <div class="pL20 pR20 mT10 pT20 pB20 border-top3">
                      <div v-if="alarm.possibleCauses">
                        <div
                          class="fc-blue-label f11 text-uppercase letter-spacing07"
                        >
                          POSSIBLE CAUSES
                        </div>
                        <div
                          class="label-txt-black mT10 line-height20 space-preline"
                        >
                          {{ alarm.possibleCauses }}
                        </div>
                      </div>
                      <div v-else-if="!alarm.possibleCauses"></div>
                    </div>
                    <div class="mT10 pL20 pR20 pB20">
                      <div v-if="alarm.recommendation">
                        <div
                          class="fc-blue-label f11 text-uppercase letter-spacing07"
                        >
                          POSSIBLE SOLUTIONS
                        </div>
                        <div
                          class="label-txt-black mT10 line-height20 space-preline"
                        >
                          {{ alarm.recommendation }}
                        </div>
                      </div>
                      <div v-else-if="alarm.recommendation"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- cause -->
            <!-- possible causes -->

            <!-- possible causes -->

            <!-- alarm graph container new -->
            <div
              class="mB10 position-relative"
              v-if="
                additionInfo &&
                  additionInfo.rcaJSONArray &&
                  additionInfo.rcaJSONArray.length > 0
              "
            >
              <div class="fc-grey2 mT20 fw-bold text-uppercase f11">
                Root Cause Analysis
              </div>
              <el-collapse
                ref="alarmlistcollapse"
                v-model="activeRule"
                accordion
                class="dashboard-collapse alarm-summary-dashboard"
              >
                <el-collapse-item
                  v-for="(rca, index) in additionInfo.rcaJSONArray"
                  :key="index"
                  :name="rca.rcaRule.id"
                  class="pT10 position-relative"
                >
                  <template slot="title">
                    <div class="fL width90">
                      <div class="fc-black3-16 text-capitalize">
                        {{
                          rca.rcaRule.actions[0].template
                            ? rca.rcaRule.actions[0].template.originalTemplate
                              ? rca.rcaRule.actions[0].template.originalTemplate
                                  .problem
                              : null
                            : null
                        }}
                        <span
                          style="text-transform: lowercase"
                          class="fc-grey2-text12 bold"
                        >
                          ({{
                            rca.rcaRule
                              ? 'v' + rca.rcaRule.versionNumber
                              : null
                          }})</span
                        >
                        <span class="fc-grey2 f10 f10 pL20" v-if="rca.isActive"
                          ><i
                            class="fa fa-circle pR10 fc-green"
                            aria-hidden="true"
                          >
                          </i
                          >Active
                        </span>
                        <span v-else class="fc-grey2 f10 pL20">
                          <i
                            class="fa fa-circle pR10 fc-red f10"
                            aria-hidden="true"
                          ></i>
                          Inactive
                        </span>
                      </div>
                    </div>
                    <div class="fR">
                      <i class="el-icon-arrow-up f16"></i>
                      <i class="el-icon-arrow-down f16"></i>
                    </div>
                  </template>

                  <div class="clear">
                    <div>
                      <f-new-analytic-report
                        v-if="rca.analyticsConfig"
                        class="alarm-summary-chart alarm-summary-chart-root-cause"
                        ref="newAnalyticReport"
                        :config="rca.analyticsConfig"
                      ></f-new-analytic-report>
                    </div>
                    <div class="analytics-txt">
                      <div class="fc-summary-content-div">
                        <div
                          @click="
                            Analytics(alarm.id, null, false, rca.rcaRule.id)
                          "
                          class="content analytics-txt"
                          style="
                            cursor: pointer;
                            color: rgb(57, 178, 194);
                            font-size: 13px;
                            text-align: right;
                            font-weight: 500;
                            margin-right: 20px;
                          "
                        >
                          Go to Analytics
                          <img
                            style="width: 13px; height: 9px"
                            src="~statics/icons/right-arrow.svg"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                  <div
                    class="pT20 pL20 pB20 pR20 border-top3"
                    v-if="rca.rcaRule.actions && rca.rcaRule.actions[0]"
                  >
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      POSSIBLE CAUSES
                    </div>
                    <div
                      class="label-txt-black mT10 line-height20 space-preline"
                    >
                      {{
                        rca.rcaRule.actions[0].template
                          ? rca.rcaRule.actions[0].template.originalTemplate
                            ? rca.rcaRule.actions[0].template.originalTemplate
                                .possibleCauses
                            : null
                          : null
                      }}
                    </div>
                  </div>
                  <div class="pL20 pB20 pR20">
                    <div
                      class="fc-blue-label f11 text-uppercase letter-spacing07"
                    >
                      POSSIBLE SOLUTIONS
                    </div>
                    <div
                      class="label-txt-black mT10 line-height20 space-preline"
                    >
                      {{
                        rca.rcaRule.actions[0].template
                          ? rca.rcaRule.actions[0].template.originalTemplate
                            ? rca.rcaRule.actions[0].template.originalTemplate
                                .recommendation
                            : null
                          : null
                      }}
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>
            <!-- alarm graph container new  -->
            <!-- tool -->
            <div class="fc-white-bg-container mT10 p0 alarm-comment-con">
              <div class="row pT20" style="border-bottom: 1px solid #f4f4f4">
                <div class="col-12">
                  <div class="row pL20">
                    <div
                      class="col-1 fc-black-13 text-left pointer bold"
                      @click="selectSubSection('comment')"
                    >
                      Comments
                      <div
                        v-bind:class="{ wosactive: subSection === 'comment' }"
                        style="width: 50%"
                      ></div>
                    </div>
                    <div
                      class="col-1 fc-black-13 text-left pointer bold mL60"
                      @click="selectSubSection('event')"
                    >
                      Events
                      <div
                        v-bind:class="{ wosactive: subSection === 'event' }"
                        style="width: 50%"
                      ></div>
                    </div>
                    <div
                      class="col-3 fc-black-13 text-left pointer bold mL40"
                      @click="selectSubSection('relatedalarms')"
                    >
                      Related Alarms
                      <div
                        v-bind:class="{
                          wosactive: subSection === 'relatedalarms',
                        }"
                        style="width: 30%"
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="subSection === 'relatedalarms'">
                <spinner
                  v-if="relatedAlarmsLoading"
                  :show="relatedAlarmsLoading"
                  size="50"
                ></spinner>
                <div
                  v-else-if="
                    !relatedalarms ||
                      !relatedalarms.length ||
                      (relatedalarms.length === 1 &&
                        relatedalarms[0].id === alarm.id)
                  "
                  class="empty-msg"
                >
                  No related alarms found.
                </div>
                <template v-else>
                  <el-row
                    v-if="alarm.id !== relatedalarm.id"
                    style="
                      line-height: 2.79;
                      border-bottom: solid 1px rgba(151, 151, 151, 0.2);
                    "
                    v-for="(relatedalarm, index) in relatedalarms"
                    :key="index"
                    class="pR10 pT10 pB10 mL20 mR20"
                  >
                    <el-col :span="2">
                      <router-link
                        :to="'/app/fa/faults/summary/' + relatedalarm.id"
                      >
                        <div class="id-color f13 pull-left pointer">
                          #{{ relatedalarm.serialNumber }}
                        </div>
                      </router-link>
                    </el-col>
                    <el-col :span="15">
                      <div class="event-content2">
                        {{ relatedalarm.subject }}
                      </div>
                    </el-col>
                    <el-col :span="2">
                      <div
                        class="fc-id"
                        v-bind:style="{
                          color: getAlarmSeverity(alarm.severity.id).color,
                        }"
                      >
                        {{
                          getAlarmSeverity(relatedalarm.severity.id)
                            .severity === 'Clear' &&
                          relatedalarm.previousSeverity
                            ? getAlarmSeverity(relatedalarm.previousSeverity.id)
                                .severity
                            : getAlarmSeverity(relatedalarm.severity.id)
                                .severity
                        }}
                      </div>
                    </el-col>
                    <el-col :span="5">
                      <div
                        style="
                          font-size: 12px;
                          letter-spacing: 0.4px;
                          text-align: right;
                          color: #919191;
                        "
                      >
                        {{ relatedalarm.modifiedTime | formatDate() }}
                      </div>
                    </el-col>
                  </el-row>
                </template>
              </div>
              <div
                v-if="subSection === 'event'"
                class="row event-inner-container"
              >
                <spinner
                  v-if="eventsLoading"
                  :show="eventsLoading"
                  size="50"
                ></spinner>
                <table v-else width="100%;" style="margin-bottom: 100px">
                  <tr
                    style="
                      border-bottom: solid 1px rgba(151, 151, 151, 0.2);
                      line-height: 2.79;
                    "
                    v-for="(event, index) in events"
                    :key="index"
                  >
                    <td style="width: 100%">
                      <div class="event-msg">
                        <router-link
                          :to="'/app/fa/events/all/summary/' + event.id"
                        >
                          <div class="id-color f12 pull-left pointer" style="">
                            <span class="f11">#</span>{{ event.id }}
                          </div>
                        </router-link>
                        <div class="event-content">
                          {{ event.eventMessage }}
                        </div>
                        <div
                          style="
                            float: right;
                            font-size: 12px;
                            letter-spacing: 0.4px;
                            text-align: right;
                            color: #919191;
                          "
                        >
                          {{ event.createdTime | formatDate() }}
                        </div>
                        <div style="clear: both"></div>
                      </div>
                    </td>
                  </tr>
                </table>
              </div>
              <comments
                style="margin-left: 40px; margin-right: 30px"
                class=""
                v-if="subSection === 'comment'"
                module="ticketnotes"
                parentModule="alarm"
                :record="alarm"
                :notify="false"
              ></comments>
            </div>
          </div>
        </div>
      </div>
      <div class="alarm-summary-body-right clearboth">
        <div class="alaram-summary-body-scroll mL30 mR20">
          <div
            v-if="alarm.createdTime && alarm.clearedTime === -1"
            class="mT20"
          >
            <div class="fc-blue-label f11 text-uppercase">ALARM DURATION</div>
            <timer
              class="alarm-timer p10"
              :time="alarm.createdTime"
              :twoDigits="showingTwoDigit"
              :title="alarm.createdTime | formatDate()"
              v-tippy="{
                html: '#timer_popover_' + alarm.id,
                distance: 0,
                interactive: true,
                theme: 'light',
                animation: 'scale',
                arrow: true,
              }"
            ></timer>
          </div>
          <div
            v-else-if="alarm.createdTime && alarm.clearedTime !== -1"
            class="mT20"
          >
            <div class="fc-blue-label f11 text-uppercase">ALARM DURATION</div>
            <timer
              class="alarm-timer p10"
              :time="alarm.clearedTime - alarm.createdTime"
              :staticTime="true"
              :twoDigits="showingTwoDigit"
              :title="(alarm.clearedTime - alarm.createdTime) | formatDate()"
              v-tippy="{
                html: '#timer_popover_' + alarm.id,
                distance: 0,
                interactive: true,
                theme: 'light',
                animation: 'scale',
                arrow: true,
              }"
            ></timer>
          </div>
          <div v-if="resource" class="asset-con">
            <div class="mT20">
              <div class="fc-blue-label f11 text-uppercase">ASSET CATEGORY</div>
              <div
                class="mT10 label-txt-black"
                v-if="resource && resource.category && resource.category.id"
              >
                {{
                  resource
                    ? getAssetCategoryById(resource.category.id).displayName
                      ? getAssetCategoryById(resource.category.id).displayName
                      : '---'
                    : '---'
                }}
              </div>
            </div>
            <div class="mT25">
              <div class="fc-blue-label f11 text-uppercase">ASSET NAME</div>
              <div
                class="mT10 label-txt-black pointer"
                v-if="resource && resource.name"
                @click="openAsset(alarm.resource.id)"
              >
                {{ resource.name ? resource.name : '---' }}
              </div>
            </div>
            <div
              v-if="
                resource &&
                  resource.space &&
                  resource.space.name &&
                  getSpace(1, alarm.resource.space.id) !== '---'
              "
              class="mT25"
            >
              <div class="fc-blue-label f11 text-uppercase">SITE</div>
              <div
                class="mT10 label-txt-black pointer"
                @click="openSite(alarm.resource.space.id)"
              >
                {{
                  alarm.resource &&
                  alarm.resource.space &&
                  alarm.resource.space.id
                    ? getSpace(1, alarm.resource.space.id)
                    : '---'
                }}
              </div>
            </div>
            <div
              v-if="
                resource &&
                  resource.space &&
                  resource.space.name &&
                  getSpace(2, alarm.resource.space.id) !== '---'
              "
              class="mT25"
            >
              <div class="fc-blue-label f11 text-uppercase">BUILDING</div>
              <div
                class="mT10 label-txt-black pointer"
                @click="openBuilding(alarm.resource.space.id)"
              >
                {{
                  alarm.resource &&
                  alarm.resource.space &&
                  alarm.resource.space.id
                    ? getSpace(2, alarm.resource.space.id)
                    : '---'
                }}
              </div>
            </div>
            <div
              v-if="
                resource &&
                  resource.space &&
                  resource.space.name &&
                  getSpace(3, alarm.resource.space.id) !== '---'
              "
              class="mT25"
            >
              <div class="fc-blue-label f11 text-uppercase">FLOOR</div>
              <div
                class="mT10 label-txt-black pointer"
                @click="openFloor(alarm.resource.space.id)"
              >
                {{
                  alarm.resource &&
                  alarm.resource.space &&
                  alarm.resource.space.id
                    ? getSpace(3, alarm.resource.space.id)
                    : '---'
                }}
              </div>
            </div>
            <div
              v-if="
                resource &&
                  resource.space &&
                  resource.space.name &&
                  getSpace(4, alarm.resource.space.id) !== '---'
              "
              class="mT25"
            >
              <div class="fc-blue-label f11 text-uppercase">SPACE</div>
              <div
                class="mT10 label-txt-black pointer"
                @click="openSpace(alarm.resource.space.id)"
              >
                {{
                  alarm.resource &&
                  alarm.resource.space &&
                  alarm.resource.space.id
                    ? getSpace(4, alarm.resource.space.id)
                    : '---'
                }}
              </div>
            </div>
          </div>
          <div v-if="alarm && alarm.woId && workorder" class="mT25">
            <div class="">
              <div class="fc-text-pink11">WORKORDER</div>
              <div
                class="label-txt-black mT5 pointer fc-id"
                v-if="workorder && workorder.id"
                @click="openWorkorder(alarm.woId)"
              >
                #{{ workorder.id }}
              </div>
              <div
                class="label-txt-black mT5 pointer"
                v-if="workorder && workorder.subject"
                @click="openWorkorder(alarm.woId)"
              >
                {{ workorder.subject ? workorder.subject : '---' }}
              </div>
            </div>
            <div>
              <div
                class="mT20"
                v-if="
                  workorder && workorder.dueDate && workorder.dueDate !== -1
                "
              >
                <div class="fc-blue-label f11">Due Time</div>
                <div
                  class="mT10 label-txt-black"
                  v-if="workorder && workorder.dueDate"
                >
                  {{ workorder.dueDate !== -1 ? workorder.dueDateString : '' }}
                </div>
              </div>
              <div class="mT20">
                <div class="fc-blue-label f11">Status</div>
                <div
                  class="mT10 label-txt-black"
                  v-if="
                    workorder &&
                      workorder.status &&
                      workorder.status.displayName
                  "
                >
                  {{
                    workorder.status.displayName
                      ? workorder.status.displayName
                      : '---'
                  }}
                </div>
              </div>
              <div class="mT20" v-if="workorder && workorder.assignedTo > 0">
                <div class="fc-blue-label f11">ASSIGNED TO</div>
                <div
                  class="mT10 label-txt-black"
                  v-if="
                    workorder &&
                      workorder.assignedTo &&
                      workorder.assignedTo.name
                  "
                >
                  {{
                    workorder.assignedTo.name
                      ? workorder.assignedTo.name
                      : '---'
                  }}
                </div>
              </div>
              <div class="mT20">
                <div class="fc-blue-label f11">PRIORITY</div>
                <div
                  class="mT10 label-txt-black"
                  v-if="
                    workorder &&
                      workorder.priority &&
                      workorder.priority.priority
                  "
                >
                  <i
                    class="fa fa-circle f9"
                    v-bind:style="{ color: workorder.priority.colour }"
                    aria-hidden="true"
                  >
                  </i>
                  {{
                    workorder.priority.priority
                      ? workorder.priority.priority
                      : '---'
                  }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      v-if="showAssetExport"
      class="dialog-header-remove export-dialog"
      :visible.sync="showAssetExport"
      width="30%"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">Export Alarm summary</div>
        </div>
      </div>
      <div class="export-body-dailog">
        <div>
          <el-checkbox
            checked
            style="padding-bottom: 10px; padding-left: 40px; padding-top: 10px"
            disabled
            >All Events</el-checkbox
          >
        </div>
        <el-checkbox-group
          v-model="selectedFields"
          class="check-padding-remove"
        >
          <div class="row">
            <el-checkbox
              v-for="field in assetFields"
              :key="field.id"
              :label="field.id"
              class="check-width"
              >{{ field.displayName }}</el-checkbox
            >
          </div>
        </el-checkbox-group>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeExportDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button type="primary" @click="exportEvent()" class="modal-btn-save"
          >EXPORT DATA</el-button
        >
      </div>
    </el-dialog>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none"
    ></iframe>
    <q-modal
      ref="createWOModel"
      noBackdropDismiss
      content-classes="fc-model"
      :content-css="{
        padding: '0px',
        background: '#f7f8fa',
        Width: '10vw',
        Height: '30vh',
      }"
    >
      <alarm-model
        ref="confirmWoModel"
        @submit="createWO"
        @closed="closeWoDialog"
      ></alarm-model>
    </q-modal>
  </div>
</template>
<script>
import Timer from '@/Timer'
import Comments from '@/relatedlist/Comments'
import AlarmModel from '@/AlarmModel'
import moment from 'moment'
import { QItem, QIcon, QPopover, QList, QModal } from 'quasar'
import { mapState, mapActions, mapGetters } from 'vuex'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import JumpToHelper from '@/mixins/JumpToHelper'
import AnalyticsMixin from 'pages/energy/analytics/mixins/AnalyticsMixin'
import newDateHelper from '@/mixins/NewDateHelper'
import { QItemMain } from 'quasar'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import NewReportHelper from 'pages/report/mixins/NewReportHelper'
import * as d3 from 'd3'
import { mapGettersWithLogging } from 'store/utils/log-map-getters'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  name: 'NewAlarmSummary',
  mixins: [JumpToHelper, NewReportHelper, AnalyticsMixin, newDateHelper],
  data() {
    return {
      alarmFields: [],
      assetFields: [],
      activeRule: null,
      workorder: null,
      eventsLoading: false,
      relatedAlarmsLoading: false,
      summary: true,
      showAssetExport: false,
      severity: {},
      subSection: 'comment',
      events: [],
      showingTwoDigit: true,
      relatedalarms: [],
      selectedFields: [],
      chartOperatorId: null,
      exportDownloadUrl: null,
      asset: null,
      exportType: null,
      readingField: null,
      analyticsConfig: null,
      loading: true,
      isAcknowledged: false,
      additionInfo: null,
    }
  },
  components: {
    FNewAnalyticReport,
    Timer,
    Comments,
    QItem,
    QItemMain,
    QModal,
    QIcon,
    QPopover,
    QList,
    AlarmModel,
  },
  computed: {
    ...mapState({
      alarms: state => state.alarm.alarms,
      users: state => state.users,
      severityStatus: state => state.alarmSeverity,
    }),

    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),

    ...mapGetters({
      getTicketPriority: 'getTicketPriority',
      getTicketCategory: 'getTicketCategory',
      getAssetCategoryById: 'getAssetCategory',
      getAlarmSeverity: 'getAlarmSeverity',
    }),

    ...mapGettersWithLogging({
      storeGetSpace: 'getSpace',
    }),

    alarm: {
      // getter
      get: function() {
        return this.$store.state.alarm.currentAlarm
      },
      // setter
      set: function(newValue) {},
    },
    alarmId() {
      return parseInt(this.$route.params.id)
    },
    resource() {
      if (this.alarm && this.alarm.resource) {
        return this.alarm.resource
      } else {
        return null
      }
    },
  },
  created() {
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadAlarmSeverity')
    this.$store.dispatch('alarm/fetchAlarm', {
      id: parseInt(this.$route.params.id),
    })
  },
  watch: {
    alarm: function() {
      let title = '[#' + this.alarm.serialNumber + '] ' + this.alarm.subject
      this.setTitle(title)
      this.loadFieldDetails()
      this.loadWoDetails()
      this.activeRule = null
      if (this.subSection === 'event') {
        this.loadEvents(this.alarm.id)
      } else if (this.subSection === 'relatedalarms' && this.alarm.entityId) {
        this.loadRelatedAlarms(this.alarm.entityId)
      }
      let config = {
        alarmId: this.alarm.id,
        isWithPrerequsite:
          this.alarm.sourceType === 12 ||
          this.alarm.sourceType === 9 ||
          this.alarm.ruleId < 0
            ? false
            : true,
        dateFilter: this.getDatePickerObject(),
        hidechartoptions: true,
        hidetabular: true,
        hidecharttypechanger: true,
        fixedChartHeight: 300,
        isFromAlarmSummary: true,
        applyReportDate:
          this.alarm.sourceType === 12 || this.alarm.sourceType === 9,
      }
      if (
        this.alarm.readingVal ||
        this.alarm.sourceType === 12 ||
        this.alarm.sourceType === 9
      ) {
        if (this.analyticsConfig) {
          this.analyticsConfig = null
          this.$nextTick(() => {
            this.analyticsConfig = config
          })
        } else {
          this.analyticsConfig = config
        }
      }
    },

    activeRule: {
      handler: function(newValue, oldValue) {
        if (newValue !== null) {
          this.handleChange(newValue)
        }
      },
    },
    subSection: function() {
      if (this.subSection === 'event') {
        this.loadEvents(this.alarm.id)
      } else if (this.subSection === 'relatedalarms' && this.alarm.entityId) {
        this.loadRelatedAlarms(this.alarm.entityId)
      }
    },
    alarmId: function(newVal) {
      this.$store.dispatch('alarm/fetchAlarm', { id: this.alarmId })
      this.loadAssetDetails()
      this.loadWoDetails()
    },
  },
  mounted() {
    this.loadAssetDetails()
    this.loadWoDetails()
  },
  methods: {
    formattedKey(key) {
      return d3.format(',')(key)
    },
    ...mapActions({
      assignAlarmApi: 'alarm/assignAlarm',
      addAlarmApi: 'alarm/addAlarm',
      updateAlarmStatus: 'alarm/updateAlarmStatus',
      notifyAlarm: 'alarm/notifyAlarm',
      acknowledgeAlarm: 'alarm/acknowledgeAlarm',
      getRelatedWorkorderId: 'alarm/getRelatedWorkorderId',
      deleteAlarm: 'alarm/deleteAlarm',
    }),
    acknowledgeAlarms() {
      let self = this
      this.acknowledgeAlarm({
        alarm: this.alarm,
        isAcknowledged: true,
        acknowledgedBy: this.$account.user,
      })
      this.isAcknowledged = true
      this.loadAssetDetails()
    },
    updateAlarmsStatus() {
      this.updateAlarmStatus({
        alarm: this.alarm,
        clearedTime: Date.now(),
        severity: this.severityStatus.find(
          status => status.severity === 'Clear'
        ),
      })
      this.alarm.clearedBy = this.$account.user
    },
    loadRCAReport() {
      if (
        this.additionInfo &&
        this.additionInfo.rcaJSONArray &&
        this.additionInfo.rcaJSONArray.length > 0
      ) {
        this.additionInfo.rcaJSONArray.forEach(d => {
          this.$set(d, 'analyticsConfig', null)
          this.$nextTick(() => {
            this.$set(d, 'analyticsConfig', {
              alarmId: this.alarm.id,
              readingRuleId: d.rcaRule.id,
              dateFilter: this.getDatePickerObject(),
              hidechartoptions: true,
              hidetabular: true,
              hidecharttypechanger: true,
              fixedChartHeight: 300,
              isFromAlarmSummary: true,
              applyReportDate: this.alarm.sourceType === 12,
            })
          })
        })
      }
    },
    loadAssetDetails() {
      this.loadAlarmFields()
      this.$http
        .post('/alarm/fetchAlarmSummary', { id: [this.$route.params.id] })
        .then(response => {
          if (!this.alarm) {
            this.alarm = response.data.alarms[0]
            this.additionInfo = this.alarm.additionInfo
          } else {
            this.additionInfo = response.data.alarms[0].additionInfo
          }
          this.loadRCAReport()
          if (this.isAcknowledged) {
            this.alarm.isAcknowledged = true
            this.alarm.acknowledgedBy = this.$account.user
            this.isAcknowledged = false
          }
        })
        .catch(function(error) {
          if (error) {
            console.log(error)
          }
        })
    },
    loadAlarmFields() {
      this.$util.loadFields('alarm', false).then(fields => {
        this.alarmFields = fields
      })
    },
    getFieldByName(field) {
      let fields = this.alarmFields.filter(d => d.name === field)
      return fields[0]
    },
    handleChange(changedId) {
      if (
        this.$refs.newAnalyticReport &&
        this.$refs.newAnalyticReport.length > 0
      ) {
        for (let ref of this.$refs.newAnalyticReport) {
          if (ref.config.readingRuleId === changedId) {
            let index = this.$refs.newAnalyticReport.indexOf(ref)
            this.$refs.newAnalyticReport[index].resize()
          }
        }
      }
    },
    loadmsg() {
      return this.alarm.readingMessage.replace('&deg;C', '°C')
    },
    loadReadingField() {
      return this.readingField.unit.replace('&deg;C', '°C')
    },
    getDatePickerObject() {
      let modifiedTime
      modifiedTime = this.alarm.createdTime
      if (this.alarm.modifiedTime > 0) {
        modifiedTime = this.alarm.modifiedTime
      }
      return newDateHelper.getDatePickerObject(
        this.alarm.sourceType === 12 ? 20 : 62,
        '' + modifiedTime
      )
    },
    chartOperatorHandling(operatorId) {
      this.chartOperatorId = operatorId
    },
    loadWoDetails() {
      let self = this
      if (this.alarm && this.alarm.woId && this.alarm.woId > 0) {
        let url = '/workorder/summary/' + this.alarm.woId
        return self.$http.get(url).then(function(response) {
          self.workorder = response.data.workorder
        })
      } else {
        self.workorder = null
      }
      this.$forceUpdate()
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    exportEvent(type) {
      type = 1
      let array = {
        alarmId: this.alarmId,
        fieldId: this.selectedFields,
        type: this.exportType,
        parentId: this.alarm.resource.id,
      }
      let self = this
      let url = 'event/eventExport'
      self.$message({
        showClose: true,
        message: 'Downloading...',
        type: 'success',
      })
      this.$http.post(url, array).then(function(response) {
        self.exportDownloadUrl = response.data.fileUrl
        self.showAssetExport = false
      })
    },
    closeExportDialog() {
      this.showAssetExport = false
      this.selectedFields = []
    },
    exportSummary(type) {
      this.exportType = type
      this.showAssetExport = true
      this.$util.loadAssetReadingFields(this.alarm.resource.id).then(fields => {
        this.assetFields = fields
      })
    },
    createWoDialog(id) {
      this.createWoIds = id
      this.$refs['createWOModel'].open()
      this.$refs.confirmWoModel.reset()
    },
    getSpace(spaceType, spaceId) {
      let obj = this.storeGetSpace(spaceId)
      if (obj) {
        if (spaceType === 1) {
          if (obj.spaceType === 1) {
            return obj.name
          } else {
            return this.getSpace(1, obj.siteId)
          }
        } else if (spaceType === 2) {
          if (obj.spaceType === 2) {
            return obj.name
          } else if (obj.spaceType === 3 || obj.spaceType === 4) {
            return this.getSpace(2, obj.buildingId)
          }
        } else if (spaceType === 3) {
          if (obj.spaceType === 3) {
            return obj.name
          } else if (obj.spaceType === 4) {
            return this.getSpace(3, obj.floorId)
          }
        } else if (spaceType === 4) {
          if (obj.spaceType === 4) {
            return obj.name
          }
        }
      }
      return '---'
    },
    loadFieldDetails: function() {
      let self = this
      if (
        self.alarm.sourceTypeEnum &&
        self.alarm.sourceTypeEnum === 'THRESHOLD_ALARM'
      ) {
        let url = '/field/' + this.alarm.readingFieldId
        self.$http.get(url).then(function(response) {
          self.readingField = response.data.field
        })
      }
    },
    goToAnalytics() {
      this.$router.replace({
        path: '/app/em/analytics/reading',
        query: { alarmId: this.alarm.id },
      })
    },
    Analytics(alarmId, datePicker, isWithPrerequsite, ruleId) {
      let buildingId = this.alarm.resource.space.buildingId
      this.jumpAlarmToAnalytics(
        alarmId,
        datePicker,
        isWithPrerequsite,
        ruleId,
        buildingId
      )
    },
    openEvent(id) {
      let url = '/app/fa/events/all/summary/' + id
      this.$router.replace({ path: url })
    },
    openAsset(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.push({ name, params: { viewname: 'all', id } })
          }
        } else {
          let url = '/app/at/assets/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
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
    openSite(id) {
      if (id) {
        let obj = this.storeGetSpace(id)
        let sid = obj.siteId

        if (sid) {
          let parentPath = this.findRoute()

          if (parentPath) {
            let url = `${parentPath}/site/${sid}/overview`
            this.$router.replace({ path: url })
          }
        }
      }
    },
    openBuilding(id) {
      if (id) {
        let obj = this.storeGetSpace(id)
        let sid = obj.siteId
        let bid = this.storeGetSpace(id).id

        if (bid && sid) {
          let parentPath = this.findRoute()

          if (parentPath) {
            let url = `${parentPath}/site/${sid}/building/${bid}`
            this.$router.replace({ path: url })
          }
        }
      }
    },
    openFloor(id) {
      if (id) {
        let obj = this.storeGetSpace(id)
        let sid = obj.siteId
        let bid = this.storeGetSpace(id).id

        if (bid && sid) {
          let parentPath = this.findRoute()

          if (parentPath) {
            let url = `${parentPath}/site/${sid}/floor/${bid}`
            this.$router.replace({ path: url })
          }
        }
      }
    },
    openSpace(id) {
      if (id) {
        let obj = this.storeGetSpace(id)
        let sid = obj.siteId
        let bid = this.storeGetSpace(id).id

        if (bid && sid) {
          let parentPath = this.findRoute()

          if (parentPath) {
            let url = `${parentPath}/site/${sid}/space/${bid}`
            this.$router.replace({ path: url })
          }
        }
      }
    },
    isActiveAlarm(alarm) {
      if (this.getAlarmSeverity(alarm.severity.id).severity !== 'Clear') {
        return true
      }
      return false
    },
    loadEvents(alarmId) {
      let self = this
      let queryObj = {
        alarmId: alarmId,
      }
      self.eventsLoading = true
      self.$http
        .post('event/alarm', queryObj)
        .then(function(response) {
          self.eventsLoading = false
          self.events = response.data.events
        })
        .catch(function(error) {
          self.eventsLoading = false
        })
    },
    loadRelatedAlarms(entityId) {
      let self = this
      let queryObj = {
        entityId: entityId,
      }
      self.relatedAlarmsLoading = true
      self.$http
        .post('alarmentity/alarm', queryObj)
        .then(function(response) {
          self.relatedAlarmsLoading = false
          self.relatedalarms = response.data.alarms
        })
        .catch(function(error) {
          self.relatedAlarmsLoading = false
        })
    },
    assignAlarm: function(alarm, userId, userName) {
      let data = {
        id: [alarm.id],
        alarm: {
          assignedTo: {
            id: userId,
            name: userName,
          },
        },
      }
      this.assignAlarmApi({ data: data, alarm: alarm })
    },
    closeSummary() {
      this.summary = false
      let newpath = this.$route.path.substring(
        0,
        this.$route.path.indexOf('/newsummary/')
      )
      this.$router.push({
        path: newpath,
        query: this.$route.query,
      })
    },
    deleteAlarmById() {
      let self = this
      self.$dialog
        .confirm({
          title: 'Delete Alarm',
          message: 'Are you sure you want to delete this Alarm?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(function(value) {
          if (value) {
            self.deleteAlarm([self.alarm.id])
            self.$message({
              message: 'Alarm deleted successfully!',
              type: 'success',
            })
            let newpath =
              '/app/fa/faults/unacknowledged/summary/' + self.alarms[0].id
            self.$router.replace({ path: newpath })
          }
        })
    },
    createWO(data) {
      let self = this
      let fields = {}
      if (data.category) {
        fields.category = {
          id: data.category,
          name: this.getTicketCategory(data.category).name,
        }
      }
      if (data.priority) {
        fields.priority = {
          id: data.priority,
          name: this.getTicketPriority(data.priority),
        }
      }
      if (data.assignedTo) {
        fields.assignedTo = data.assignedTo
      }
      if (data.assignmentGroup) {
        fields.assignmentGroup = data.assignmentGroup
      }
      self.$store
        .dispatch('alarm/createWoFromAlarm', {
          id: this.createWoIds,
          fields: fields,
        })
        .then(function() {
          self.$dialog.notify('Workorder created successfully!')
          self.$refs['createWOModel'].close()
        })
    },
    closeWoDialog() {
      this.$refs['createWOModel'].close()
    },
    selectSubSection(section) {
      this.subSection = section
    },
    alarmstatus(status) {
      if (status === 1) {
        return ''
      } else if (status === 2) {
        return 'Suppressed'
      } else if (status === 3) {
        return 'Cleared'
      }
    },
    openWorkorder(id) {
      if (id > 0) {
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.push({ name, params: { viewname: 'all', id } })
          }
        } else {
          this.$router.push({ path: '/app/wo/orders/summary/' + id })
        }
      } else {
        return false
      }
    },
    onlyInternalProps(additionInfo) {
      for (let key in additionInfo) {
        if (
          key !== '' &&
          key !== 'resourceId' &&
          key !== 'eventStateEnum' &&
          key !== 'internalStateEnum' &&
          key !== 'PUBLISH_TYPE'
        ) {
          return false
        }
      }
      return true
    },
  },
  filters: {
    moment: function(date, format) {
      return moment(date).format(format)
    },
    getFollowerUser(value, users) {
      if (value.indexOf('@') !== -1) {
        let userObj = users.find(user => user.email === value)
        if (userObj) {
          return userObj
        }
      }
      return {
        name: value,
      }
    },
    sensorName: function(deviceId, devices) {
      let sensorName
      for (let key in devices) {
        if (devices[key].id === deviceId) {
          sensorName = devices[key].name
        }
      }
      return sensorName
    },
    sensorType: function(deviceId, devices) {
      let sensorType
      for (let key in devices) {
        if (devices[key].id === deviceId) {
          sensorType = devices[key].type
        }
      }
      return sensorType
    },
  },
}
</script>
<style>
.alarm-summary-header {
  width: 100%;
  background: #ffffff;
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 20px 20px;
  border-bottom: 1px solid #eeeeee;
}
.alarm-comment-con {
  width: 100%;
  overflow-y: scroll;
  overflow-x: hidden;
  padding-bottom: 30px !important;
  clear: both;
}
.alarm-summary-chart .f-singlechart .fc-newchart-container svg {
  height: 250px;
}
.alarm-comment-con .fc-comments {
  margin-left: 0 !important;
  margin-right: 0 !important;
  padding-left: 20px;
  padding-right: 20px;
}
.export-body-dailog {
  height: 50vh;
  overflow-x: hidden;
  overflow-y: scroll;
  padding-bottom: 40px;
}
.check-padding-remove .el-checkbox + .el-checkbox {
  margin-left: 0;
}
.check-width {
  width: 100%;
  padding-bottom: 7px;
  padding-left: 40px;
  padding-top: 10px;
  padding-bottom: 10px;
}
.check-width:nth-child(odd) {
  background-color: #fbfbfb;
}
.check-width:nth-child(even) {
  background-color: #ffffff;
}
.check-width:hover {
  background-color: #ffffff;
}
.event-inner-container {
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 10px;
}
.event-inner-container .event-msg {
  padding-top: 8px;
  padding-bottom: 8px;
  padding-left: 10px;
  padding-right: 10px;
}
.event-content2 {
  width: 350px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  float: left;
  font-size: 12px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #919191;
  margin-left: 15px;
}
.event-content {
  width: 350px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  float: left;
  font-size: 12px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #919191;
  margin-left: 15px;
}
.alaram-summary-body-scroll .fc-timer .t-label {
  color: #e94c4c;
  text-align: left !important;
  font-size: 22px !important;
}
.alaram-summary-body-scroll .alarm-timer {
  padding: 0 !important;
}
.alaram-summary-body-scroll .fc-timer.alarm-timer .t-sublabel {
  text-align: left !important;
  font-size: 10px !important;
  letter-spacing: 0.5px !important;
  text-align: center !important;
  color: #333333 !important;
  padding-top: 5px !important;
}
.alaram-summary-body-scroll .alarm-timer {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px !important;
}
.alaram-summary-body-scroll .fc-timer div.t-mins,
.fc-timer div.t-secs,
.alaram-summary-body-scroll .fc-timer div.t-hours,
.alaram-summary-body-scroll .fc-timer div.t-days {
  flex: none !important;
  padding-left: 0 !important;
}
.alaram-summary-body-scroll .fc-timer .t-hours {
  padding-left: 0 !important;
}
.alaram-summary-body-scroll .fc-timer span.t-separate {
  margin-left: 0 !important;
  margin-right: 0 !important;
  padding-right: 10px !important;
  padding-left: 10px !important;
}
.asset-con {
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}
.alarm-summary-chart .fLegendContainer-right {
  margin-top: 15px !important;
}
.alarm-summary-chart .new-analytics-filter-section {
  text-align: right !important;
  margin-top: -44px !important;
  padding-right: 10px;
}
.alarm-summary-chart .alarm-summary-graph-inner {
  background: none !important;
  box-shadow: none !important;
}
.analytics-txt {
  cursor: pointer;
  color: rgb(57, 178, 194);
  font-size: 13px;
  text-align: right;
  font-weight: 500;
  margin-right: 20px;
  padding-bottom: 10px;
  margin-top: 20px;
}
.alarm-summary-graph-container {
  background: #fff;
  height: 100%;
  min-height: 400px;
  margin-top: 10px;
  position: relative;
  box-shadow: 0 3px 7px 0 rgba(233, 233, 226, 0.5);
}
.alarm-summary-chart .analytics-spinner {
  margin-top: 80px;
  margin-bottom: 180px;
}
.alarm-summary-chart-root-cause .new-analytics-filter-section {
  position: absolute;
  right: 31px;
  top: 16px;
  margin-top: 0 !important;
}
</style>
