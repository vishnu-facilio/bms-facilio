<template>
  <div>
    <div v-if="!alarm"></div>
    <div v-else>
      <div>
        <div class="summaryHeader">
          <div
            class="col-8 self-center"
            style="background-color: #ffffff; padding-left: 15px"
          >
            <div style="padding-left: 32px">
              <div
                class="pull-right row"
                style="
                  color: rgb(56, 52, 52);
                  white-space: nowrap;
                  margin-right: 25px;
                  padding-top: 15px;
                  padding-top: 22px;
                "
              >
                <q-btn
                  color="secondary"
                  class="uppercase fia-alert-btn"
                  small
                  outline
                  @click="
                    acknowledgeAlarm({
                      alarm: alarm,
                      isAcknowledged: true,
                      acknowledgedBy: $account.user,
                    })
                  "
                  v-if="!alarm.isAcknowledged && isActiveAlarm(alarm)"
                  >{{ $t('common.header.acknowledge') }}</q-btn
                >
                <span
                  class="q-item-label fc-summary-ack"
                  v-else-if="!isActiveAlarm(alarm)"
                  >{{ $t('common.header.unacknowledged') }}</span
                >
                <span class="q-item-label f11" v-else
                  ><span class="pB10 fc-summary-ack ellipsis"
                    >{{ $t('common.header.acknowledged_by') }}
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
                </span>
                <q-icon
                  v-if="isActiveAlarm(alarm)"
                  slot="right"
                  name="more_vert"
                  style="
                    float: right;
                    font-size: 25px;
                    color: rgba(177, 177, 188, 0.56);
                  "
                  class="color-2"
                >
                  <q-popover ref="moreactionspopover">
                    <q-list link class="no-border">
                      <q-item>
                        <q-item-main
                          :label="$t('common.header.clear')"
                          @click="
                            updateAlarmStatus({
                              alarm: alarm,
                              severity: 'Clear',
                            }),
                              $refs.moreactionspopover.close()
                          "
                        />
                      </q-item>
                      <q-item>
                        <q-item-main
                          :label="$t('common._common.delete')"
                          @click="
                            deleteAlarm([alarm.id]),
                              $refs.moreactionspopover.close()
                          "
                        />
                      </q-item>
                      <q-item v-if="alarm.woId > 0">
                        <q-item-main
                          :label="$t('common._common.view_workorder')"
                          @click="
                            openWorkorder(alarm.woId),
                              $refs.moreactionspopover.close()
                          "
                        />
                      </q-item>
                      <q-item v-else-if="$hasPermission('alarm:CREATE_WO')">
                        <q-item-main
                          :label="$t('common.wo_report.create_workorder')"
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

              <div
                class="fc-summary-subject row"
                style="max-width: 100%"
                @click="back"
              >
                <el-button
                  style="
                    height: 8.6px;
                    width: 9px;
                    color: #39b2c2;
                    font-weight: bold;
                  "
                  type="text"
                  icon="el-icon-back"
                ></el-button>
                <div
                  class="discription-textbox"
                  style="
                    padding-left: 7px;
                    padding: 12px;
                    padding-bottom: 0px;
                    font-size: 10px;
                    letter-spacing: 0.8px;
                    color: #39b2c2;
                    font-weight: 500;
                    cursor: pointer;
                  "
                >
                  {{ $t('common.header.back') }}
                </div>
              </div>
              <div
                class="w4 fc-summary-subject-main"
                style="padding-bottom: 15px; display: inline-flex"
              >
                <div style="padding-right: 25px">
                  {{ alarm.subject ? alarm.subject : '---' }}
                </div>
                <div
                  class="q-item-label uppercase severityTag"
                  v-bind:style="{
                    'background-color': getAlarmSeverity(alarm.severity.id)
                      .color,
                  }"
                  v-bind:class="alarm.severity ? { severityTag: true } : ''"
                >
                  {{
                    alarm.severity
                      ? getAlarmSeverity(alarm.severity.id).displayName
                      : '---'
                  }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row fc-summary-row" style="margin: 20px; height: 78vh">
        <div
          class="scroll-content"
          style="background-color: #ffffff; width: 66%; padding-bottom: 50px"
        >
          <div
            class="row"
            style="
              position: relative;
              border-bottom: 1px solid rgb(238, 238, 238);
              padding-bottom: 15px;
            "
          >
            <div
              style="padding-top: 22px; padding-left: 30px"
              v-bind:class="{ tabselected: subSection === 'summarypage' }"
              class="tabel-selector subtitle"
              @click="selectSubSection('summarypage')"
            >
              {{ $t('common._common._summary') }}
              <div
                style="position: absolute; margin-top: 15px"
                v-bind:class="{ selectionbar: subSection === 'summarypage' }"
              ></div>
            </div>
            <div
              v-bind:class="{ tabselected: subSection === 'relatedalarms' }"
              style="padding-left: 40px; padding-top: 22px"
              class="tabel-selector subtitle"
              @click="selectSubSection('relatedalarms')"
            >
              {{ $t('common.header.related_alarms') }}
              <div
                style="position: absolute; margin-top: 15px"
                v-bind:class="{ selectionbar: subSection === 'relatedalarms' }"
              ></div>
            </div>
          </div>
          <div v-if="subSection === 'summarypage'">
            <div style="margin: 30px 30px 0 30px" class="pL10">
              <p
                class="id-text"
                style="
                  font-size: 13px;
                  color: #39b2c2;
                  letter-spacing: 0.4px;
                  line-height: 0;
                "
              >
                #{{ alarm.serialNumber }}
              </p>
              <div class="coltitle mT20">
                {{ alarm.subject ? alarm.subject : '---' }}
              </div>
            </div>

            <div
              v-if="
                alarm.sourceTypeEnum &&
                  alarm.sourceTypeEnum === 'THRESHOLD_ALARM'
              "
            ></div>
            <div
              v-if="
                alarm.sourceTypeEnum &&
                  alarm.sourceTypeEnum === 'THRESHOLD_ALARM'
              "
              style="
                background: rgb(246, 251, 252);
                margin-bottom: 20px;
                margin-top: 30px;
                border-top: 1px solid #f2f2f2;
                border-bottom: 1px solid #f2f2f2;
                position: relative;
              "
            >
              <div
                style="
                  float: left;
                  padding-left: 30px;
                  padding-top: 15px;
                  width: 47%;
                  padding-bottom: 15px;
                "
                v-if="alarm.readingMessage && !alarm.problem"
              >
                <div class="fc-summary-content-div p10">
                  <div class="header" style="color: #5e9aa4 !important">
                    {{ $t('common._common.message') }}
                  </div>
                  <div class="content" style="padding-top: 5px">
                    {{ alarm.readingMessage }}
                  </div>
                </div>
              </div>
              <div
                style="
                  float: left;
                  padding-left: 30px;
                  padding-top: 15px;
                  width: 47%;
                  padding-bottom: 15px;
                "
                v-if="alarm.problem"
              >
                <div class="fc-summary-content-div p10">
                  <div class="header" style="color: #5e9aa4 !important">
                    {{ $t('common._common.problem') }}
                  </div>
                  <div class="content" style="padding-top: 5px">
                    {{ alarm.problem }}
                  </div>
                </div>
              </div>
              <div
                style="
                  float: left;
                  padding-left: 30px;
                  padding-top: 15px;
                  width: 22%;
                  padding-bottom: 15px;
                "
              >
                <div class="fc-summary-content-div p10">
                  <div class="header" style="color: #5e9aa4 !important">
                    {{ $t('common._common.threshold_metric') }}
                  </div>
                  <div class="content" style="padding-top: 5px">
                    {{
                      alarm.readingFieldId
                        ? readingField
                          ? readingField.displayName
                          : loadFieldDetails()
                        : '---'
                    }}
                  </div>
                </div>
              </div>
              <div
                style="
                  float: left;
                  padding-left: 30px;
                  padding-top: 15px;
                  width: 30%;
                  padding-bottom: 40px;
                "
              >
                <div
                  class="fc-summary-content-div p10"
                  v-if="alarm.readingVal && readingField.unit"
                >
                  <div class="header" style="color: #5e9aa4 !important">
                    {{ $t('common.header.reading_recorded') }}
                  </div>
                  <div class="content" style="padding-top: 5px">
                    {{ Number(alarm.readingVal).toFixed(2) }}
                    {{ readingField.unit }}
                  </div>
                </div>
                <div v-else></div>
              </div>
              <div
                style="
                  padding-left: 30px;
                  padding-top: 0;
                  width: 30%;
                  padding-bottom: 15px;
                  position: absolute;
                  right: 0;
                  bottom: 0;
                "
              >
                <div class="fc-summary-content-div">
                  <div class="header" style="color: #5e9aa4 !important">
                    &nbsp;
                  </div>
                  <div
                    @click="jumpAlarmToAnalytics(alarm.id)"
                    class="content"
                    style="cursor: pointer; color: #39b2c2; font-size: 13px"
                  >
                    {{ $t('common.dialog.jump_to_analytics') }}
                    <img
                      style="width: 13px; height: 9px"
                      src="~statics/icons/right-arrow.svg"
                    />
                  </div>
                </div>
              </div>
              <div style="clear: both"></div>
            </div>
            <div class="row" style="padding-left: 40px; padding-top: 10px">
              <div class="fc-summary-content-div mT20">
                <div class="alarm-label">
                  {{ $t('common._common.category') }}
                </div>
                <div class="mtb5 content">
                  {{ alarm.category ? alarm.category.name : '---' }}
                </div>
              </div>
            </div>
            <div class="row mT40" style="padding-left: 40px">
              <div class="col-5">
                <div class="fc-summary-content-div">
                  <div class="header">{{ $t('common.header.source') }}</div>
                  <div class="mtb5 content">
                    {{ alarm.source ? alarm.source : '---' }}
                  </div>
                </div>
                <div style="clear: both"></div>
                <div class="fc-summary-content-div mT60">
                  <div class="header">
                    {{ $t('common._common.created_on') }}
                  </div>
                  <div class="mtb5 content" v-if="alarm.createdTime">
                    {{ alarm.createdTime | formatDate() }}
                  </div>
                  <div class="mtb5" v-else>---</div>
                </div>
              </div>
              <div class="col-5 mL30">
                <div class="fc-summary-content-div">
                  <div class="header">{{ $t('common._common.condition') }}</div>
                  <div class="mtb5 content">
                    {{ alarm.condition ? alarm.condition : '---' }}
                  </div>
                </div>
                <div class="fc-summary-content-div mT60">
                  <div class="header">
                    {{ $t('common.header.last_updated') }}
                  </div>
                  <div class="mtb5 content" v-if="alarm.modifiedTime">
                    {{ alarm.modifiedTime | formatDate() }}
                  </div>
                  <div class="mtb5" v-else>---</div>
                </div>
              </div>
            </div>
            <div
              class="row"
              style="padding-left: 40px; padding-top: 45px"
              v-if="
                alarm.additionInfo &&
                  Object.keys(alarm.additionInfo).length !== 0 &&
                  !onlyInternalProps(alarm.additionInfo)
              "
            >
              <div class="col-6">
                <div
                  class="row fc-text-pink11"
                  style="padding-left: 0px; font-size: 11px"
                >
                  {{ $t('common._common.additional_information') }}
                </div>
                <div
                  v-if="
                    key !== 'resourceId' &&
                      key !== 'eventStateEnum' &&
                      key !== 'internalStateEnum' &&
                      key !== 'PUBLISH_TYPE'
                  "
                  class="row"
                  v-for="(value, key) in alarm.additionInfo"
                  :key="key"
                >
                  <div class="col-6 key p10" style="padding-left: 0px">
                    {{ key }}
                  </div>
                  <div
                    class="col-6 value p10 justify-center"
                    style="padding-left: 0px"
                  >
                    {{ value }}
                  </div>
                </div>
              </div>
            </div>

            <div class="pL40 pT20" v-if="alarm.possibleCauses">
              <div
                class="row fc-text-pink11"
                style="padding-left: 0px; font-size: 11px"
              >
                {{ $t('common._common._possible_causes') }}
              </div>
              <div class="alarm-possible-txt" style="margin-top: -10px">
                {{ alarm.possibleCauses }}
              </div>
            </div>

            <div class="pL40 pT20" v-if="alarm.recommendation">
              <div
                class="row fc-text-pink11"
                style="padding-left: 0px; font-size: 11px"
              >
                {{ $t('common._common._recommendation') }}
              </div>
              <div class="alarm-possible-txt" style="margin-top: -10px">
                {{ alarm.recommendation }}
              </div>
            </div>

            <div style="padding-top: 50px">
              <span
                class="commenttitle"
                style="padding-bottom: 10px; padding-left: 30px"
                >{{ $t('common._common.comments') }}</span
              >
            </div>
            <div
              style="
                padding-top: 10px;
                border-bottom: 1px solid #f0f0f0;
                margin-right: 30px;
                margin-left: 30px;
              "
            ></div>
            <comments
              style="padding-left: 40px; padding-right: 30px"
              class=""
              module="ticketnotes"
              parentModule="alarm"
              :record="alarm"
              :notify="false"
            ></comments>
          </div>
          <div
            v-if="subSection === 'relatedalarms'"
            class="row"
            style="margin-right: 30px; margin-left: 35px; margin-top: 20px"
          >
            <table width="100%">
              <template
                v-if="
                  relatedalarms.length &&
                    relatedalarms.some(
                      relatedalarm => alarm.id !== relatedalarm.id
                    )
                "
              >
                <tr
                  v-if="alarm.id !== relatedalarm.id"
                  style="line-height: 2.79"
                  v-for="(relatedalarm, index) in relatedalarms"
                  :key="index"
                >
                  <td style="width: 100%">
                    <div>
                      <router-link
                        :to="'/app/fa/faults/summary/' + relatedalarm.id"
                      >
                        <div class="color-2 f12 pull-left pointer" style="">
                          <span class="f11">#</span
                          >{{ relatedalarm.serialNumber }}
                        </div>
                      </router-link>
                      <div
                        style="
                          float: left;
                          font-size: 12px;
                          letter-spacing: 0.5px;
                          text-align: left;
                          color: #919191;
                          margin-left: 15px;
                        "
                      >
                        {{ relatedalarm.subject }}
                      </div>
                      <div
                        style="
                          float: left;
                          font-size: 12px;
                          letter-spacing: 0.5px;
                          text-align: left;
                          color: #919191;
                          margin-left: 15px;
                        "
                      >
                        {{
                          getAlarmSeverity(relatedalarm.severity.id).severity
                        }}
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
                        {{
                          relatedalarm.modifiedTime
                            | formatDate('DD-MM-YYYY HH:mm')
                        }}
                      </div>
                      <div style="clear: both"></div>
                    </div>
                  </td>
                </tr>
              </template>
              <tr v-else>
                <td>
                  <p
                    style="
                      text-align: center;
                      font-size: 20px;
                      color: rgb(51, 51, 51);
                      margin-top: 40px;
                      padding-top: 40px;
                    "
                  >
                    {{ $t('common._common.nodata') }}
                  </p>
                </td>
              </tr>
            </table>
          </div>
        </div>

        <div
          class="scroll-content"
          style="
            background-color: #ffffff;
            width: 32%;
            margin-left: 13px;
            position: absolute;
            margin-left: 65%;
            height: 78vh;
          "
        >
          <div
            style="
              padding: 0px;
              border-bottom: solid 1px #eeeeee;
              line-height: 12px;
            "
          >
            <div
              class="flLeft"
              style="border-right: solid 1px #eeeeee; cursor: pointer"
              @click="selectSummarySection('details')"
            >
              <img
                style="
                  padding-top: 20px;
                  padding-left: 20px;
                  padding-right: 20px;
                  padding-bottom: 12px;
                "
                src="~statics/icons/info.svg"
              />
              <div
                v-if="summarySection === 'details'"
                class="selectionbar"
                style="margin-top: 0px; width: 100%"
              ></div>
            </div>
            <div
              class="flLeft"
              style="
                border-right: solid 1px #eeeeee;
                cursor: pointer;
                line-height: 12px;
              "
              @click="selectSummarySection('events')"
            >
              <img
                src="~statics/icons/clock2.svg"
                style="padding: 20px 20px 12px 20px"
              />
              <div
                v-if="summarySection === 'events'"
                class="selectionbar"
                style="margin-top: 0px; width: 100%"
              ></div>
            </div>
            <div class="clearboth"></div>
          </div>
          <div v-if="summarySection === 'details'" class="mT20">
            <div class="col-12 content" style="padding-left: 20px">
              <user-avatar
                v-if="alarm.assignedTo"
                size="sm"
                :user="alarm.assignedTo"
                class="alarm-summary-avatar"
              ></user-avatar>
            </div>
            <div
              class="col-10 content"
              style="padding-left: 20px; margin-left: 33px; margin-bottom: 15px"
              v-if="alarm.assignedTo"
            >
              <div></div>
              <div class="summary-alarm-id">
                <span @click="openWorkorder(alarm.woId)" class="pointer"
                  >#{{ alarm.serialNumber }}</span
                >
                <span style="color: #b9b9b9"
                  >&nbsp;|&nbsp;{{
                    alarm.status ? alarm.status.status : '---'
                  }}</span
                >
              </div>
            </div>

            <div
              class="summary-build-information"
              style="margin-top: 40px; margin-left: 24px"
            >
              <div class="building-details">
                <div class="building-label" style="width: 28%">
                  {{ $t('common.products._name') }}
                </div>
                <div
                  class="building-resulttxt q-item-label width230 pointer"
                  @click="openAsset(alarm.resource.id)"
                >
                  {{ alarm.resource ? alarm.resource.name : '---' }}
                </div>
              </div>

              <div class="building-details">
                <div class="building-label" style="width: 28%">
                  {{ $t('common.products.site') }}
                </div>
                <div
                  class="building-resulttxt q-item-label width230 pointer"
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

              <div class="building-details">
                <div class="building-label" style="width: 28%">
                  {{ $t('common._common.building') }}
                </div>
                <div
                  class="building-resulttxt q-item-label width230 pointer"
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

              <div class="building-details">
                <div class="building-label" style="width: 28%">
                  {{ $t('common._common.floor') }}
                </div>
                <div
                  class="building-resulttxt q-item-label width230 pointer"
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

              <div class="building-details">
                <div class="building-label" style="width: 28%">
                  {{ $t('common.space_asset_chooser.space') }}
                </div>
                <div
                  class="building-resulttxt q-item-label width230 pointer"
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
          </div>

          <div v-if="summarySection === 'events'">
            <div
              class="row"
              style="margin-right: 30px; margin-left: 35px; margin-top: 25px"
            >
              <div style="width: 100%">
                <div v-for="(event, index) in events" :key="index">
                  <div style="width: 100%">
                    <div
                      style="
                        border-bottom: 1px solid #f0f0f0;
                        padding-top: 8px;
                        padding-bottom: 8px;
                        display: flex;
                        align-items: center;
                        flex-direction: row;
                      "
                    >
                      <div
                        class="f12 pull-left pointer"
                        @click="openEvent(event.id)"
                        style="
                          font-size: 13px;
                          color: rgb(57, 178, 194);
                          letter-spacing: 0.4px;
                          line-height: 0;
                        "
                      >
                        #{{ event.id }}
                      </div>
                      <div
                        style="
                          float: left;
                          font-size: 12px;
                          letter-spacing: 0.5px;
                          text-align: left;
                          color: #919191;
                          margin-left: 15px;
                          width: 190px;
                        "
                      >
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
                        {{ event.createdTime | formatDate('DD-MM-YYYY hh:mm') }}
                      </div>
                      <div style="clear: both"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div
              class="row summary-content p20"
              style="border-bottom: solid 1px #f0f0f0"
              v-if="alarm.assignedTo"
            >
              <div class="col-12 header">
                {{ $t('common.header.workorder_details') }}
              </div>
              <div
                class="col-12 summary-assign"
                v-if="!alarm.assignedTo"
                style="display: inherit"
              >
                <span class="q-item-label content">{{
                  $t('common.wo_report.assign_to_')
                }}</span>
                <div class="">
                  <span
                    class="picklist-downarrow"
                    v-if="
                      $hasPermission('workorder:UPDATE,UPDATE_TEAM,UPDATE_OWN')
                    "
                  >
                    <q-icon
                      style="padding-left: 10px"
                      name="keyboard arrow down"
                      label="Assign"
                  /></span>
                  <q-popover
                    v-if="isActiveAlarm(alarm)"
                    ref="activeAlarmAssignedToPopover2"
                  >
                    <q-list link class="scroll" style="min-width: 150px">
                      <q-item
                        v-for="user in users"
                        @click="
                          assignAlarm(alarm, user.id, user.name),
                            $refs.activeAlarmAssignedToPopover2.close()
                        "
                        :key="user.id"
                      >
                        <q-item-main
                          :label="user.name"
                          style="font-size: 13px"
                        />
                      </q-item>
                    </q-list>
                  </q-popover>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
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
import Comments from '@/relatedlist/Comments'
import UserAvatar from '@/avatar/User'
import AlarmModel from '@/AlarmModel'
import moment from 'moment'
import { QItem, QItemMain, QBtn, QIcon, QPopover, QList, QModal } from 'quasar'
import { mapState, mapActions, mapGetters } from 'vuex'
import JumpToHelper from '@/mixins/JumpToHelper'
import { getFieldValue } from 'util/picklist'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { mapGettersWithLogging } from 'store/utils/log-map-getters'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  mixins: [JumpToHelper],
  data() {
    return {
      createWoIds: [],
      loading: true,
      summary: true,
      subSection: 'summarypage',
      summarySection: 'details',
      events: [],
      relatedalarms: [],
      comments: [],
      alarm: null,
      readingField: null,
      newComment: {
        parentModuleLinkName: 'ticketnotes',
        parentId: this.$route.params.id,
        body: null,
        notifyRequester: false,
      },
      commentFocus: false,
      asset: null,
    }
  },
  components: {
    Comments,
    QItem,
    QItemMain,
    QBtn,
    QIcon,
    QPopover,
    QList,
    QModal,
    AlarmModel,
    UserAvatar,
  },
  computed: {
    ...mapState({
      alarms: state => state.alarm.alarms,
      users: state => state.users,
    }),

    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),

    ...mapGetters({
      getTicketPriority: 'getTicketPriority',
      getTicketCategory: 'getTicketCategory',
      getAlarmSeverity: 'getAlarmSeverity',
    }),

    ...mapGettersWithLogging({
      storeGetSpace: 'getSpace',
    }),

    currentView() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    alarmId() {
      return this.$route.params.id
    },
  },
  watch: {
    alarm: async function() {
      let assetId = this.$getProperty(this, 'alarm.resource.id')
      if (assetId) {
        let assetName = await this.getAssetName(assetId)
        this.$set(this.alarm, 'resource.name', assetName)
      }
      let title = '[#' + this.alarm.id + '] ' + this.alarm.subject
      this.setTitle(title)
    },
    subSection: function() {
      if (this.subSection === 'relatedalarms' && this.alarm.entityId) {
        this.loadRelatedAlarms(this.alarm.entityId)
      }
    },
    alarmId() {
      this.loadAlarmDetails()
    },
  },
  mounted() {
    this.loadAlarmDetails()
  },
  async created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAlarmSeverity')
  },
  methods: {
    async getAssetName(id) {
      let { error, data } = await getFieldValue({
        lookupModuleName: 'asset',
        selectedOptionId: [id],
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
        return '---'
      } else {
        let value = this.$getProperty(data, '0.label')
        return value ? value : '---'
      }
    },
    back() {
      this.$router.go(-1)
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
    loadAlarmDetails() {
      let self = this
      this.$http
        .get(
          '/alarm/viewAlarm?id=' +
            this.alarmId +
            '&viewName=' +
            this.currentView
        )
        .then(function(response) {
          if (response.data.alarms && response.data.alarms.length) {
            self.alarm = response.data.alarms[0]
            if (self.subSection !== 'summarypage') {
              self.selectSubSection('summarypage')
            }
            self.loadAssetDetails()
          }
        })
        .catch()
    },
    goToAnalytics() {
      this.$router.replace({
        path: '/app/em/analytics/reading',
        query: { alarmId: this.alarm.id },
      })
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
    loadAssetDetails() {
      if (this.alarm.asset && this.alarm.asset.id) {
        let self = this
        let url = '/asset/summary/' + self.alarm.asset.id
        self.$http.get(url).then(function(response) {
          self.asset = response.data
        })
      }
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
    selectSummarySection(section) {
      this.summarySection = section
      if (this.summarySection === 'events') {
        this.loadEvents(this.alarm.id)
      }
    },
    focusCommentBox() {
      this.commentFocus = true
    },
    blurCommentBox() {
      if (!this.newComment.body || this.newComment.body.trim() === '') {
        this.commentFocus = false
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
    createWoDialog(id) {
      this.createWoIds = id
      this.$refs['createWOModel'].open()
      this.$refs.confirmWoModel.reset()
    },
    closeWoDialog() {
      this.$refs['createWOModel'].close()
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
      self.loading = true
      self.$http
        .post('event/alarm', queryObj)
        .then(function(response) {
          self.loading = false
          self.events = response.data.events
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
        })
    },
    loadRelatedAlarms(entityId) {
      let self = this
      let queryObj = {
        entityId: entityId,
      }
      self.loading = true
      self.$http
        .post('alarmentity/alarm', queryObj)
        .then(function(response) {
          self.loading = false
          self.relatedalarms = response.data.alarms
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
          }
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
        this.$route.path.indexOf('/summary/')
      )
      this.$router.replace({ path: newpath })
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
.fas-container {
  padding: 1rem;
}
.fas-row {
  padding: 10px;
  background: white;
}
.fas-sublabel {
  font-size: 12px;
}
div.fas-sublabel > span {
  font-weight: 400;
  padding: 7px;
  padding-left: 0px;
  font-size: 13px;
}
.fas-timer {
  font-size: 13px;
  padding-left: 7px;
}
.fas-timer div.fc-timer div.t-label {
  font-size: 17px;
}
.fas-label {
  margin: 5px;
  margin-left: 0px;
  font-size: 18px;
  font-weight: 500;
}
.fas-logo {
  text-align: center;
  background: #37ca5d;
  width: 10px !important;
  height: 45px;
  max-width: 45px !important;
  min-width: 10px !important;
  padding: 10px;
  border-radius: 24px;
  margin-right: 15px;
  margin-left: 5px;
  color: white;
}
.fas-row-2 {
  border: 1px solid #eee;
  margin: -1px;
  padding-left: 15px;
}
.fas-row-2 div.q-item-label {
  font-size: 13px;
  font-weight: 500;
}
.fas-row-2 div.q-item-sublabel {
  font-size: 12px;
}
.fia-uppercase {
  font-size: 11px !important;
  text-align: center;
  padding: 15px;
  color: #3ae6b7;
}
.fas-col-name {
  padding-top: 10px;
  margin-left: -5px;
}
.fas-col-sublabel {
  font-size: 12px;
  margin: 0px;
}
.fas-col-sublabel-2 {
  font-size: 11px;
  margin: 0px;
}
.fas-col-avatar {
  margin-top: 6px;
}
.fas-l {
  padding-bottom: 5px;
}
.wosactive {
  border-bottom: 2px solid #fd4b92;
  color: #25243e;
  padding-bottom: 10px;
}
.fia-td-logo1 {
  background-color: #f5e02d !important;
}
.fia-td-logo2 {
  background-color: #ee8e39 !important;
}
.fia-td-logo3 {
  background-color: #ff0000 !important;
}
.fia-td-logo4 {
  background-color: #f5e02d !important;
}
.fia-td-logo5 {
  background-color: #f39c12 !important;
}
.selectedTasksummary,
.selectedTasksummary .el-input .el-input__inner {
  background: #f9feff !important;
  border-top: 1px solid #9ed7de;
  border-bottom: 1px solid #9ed7de;
  margin-top: -1px;
  padding-top: 8px;
  padding-bottom: 8px;
}
.notselectedTasksummary {
  border-bottom: 1px solid #dff3f6;
  padding-top: 8px;
  padding-bottom: 8px;
}
.coldisc {
  font-size: 14px;
  line-height: 1.29;
  letter-spacing: 0.3px;
  color: #666666;
}
.commentcontent .new-comment-area {
  position: absolute;
  width: 100%;
  bottom: 0vh;
  margin-top: 20px;
  border-top: 1px solid rgb(221, 221, 221);
  padding-top: 30px;
  background-color: #fff;
  padding-left: 51px;
  padding-bottom: 90px;
  padding-right: 195px;
  padding-bottom: 74px;
  margin-left: 4px;
}
.scroll-content .fc-comments {
  width: 100%;
  padding-left: 30px;
}
.fc-comments .fc-comment-row .comment-body {
  max-width: 73% !important;
}
.severityTag {
  border-radius: 38px;
  font-size: 10px !important;
  font-weight: bold !important;
  letter-spacing: 0.8px;
  text-align: center;
  color: #ffffff !important;
  padding: 4px 10px;
  width: 72px;
  margin-top: 4px;
}
.alarm-label {
  font-size: 12px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #92a0ae;
}
.alarm-summary-avatar .fc-avatar-sm {
  position: relative;
  top: 10px;
}
</style>
