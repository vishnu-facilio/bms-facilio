<template>
  <div class="layout-padding scrollable" id="calendarpage">
    <full-calendar
      id="calenderview"
      class="calenderview fc-theme-color"
      style="padding-bottom: 100px;"
      @event-drop="eventDrop"
      @event-selected="eventSelected"
      :config="config"
      :events="events"
      ref="calendar"
    ></full-calendar>
    <div
      id="calendercolors"
      style="width: 192px;padding-top: 22px;position: absolute;top: 20px;z-index: 1;right: 39px;"
    >
      <div
        style="float:left;border-left: 1px solid #979797;height: 20px;opacity: 0.2;margin-top: 4px;"
      ></div>
      <div class="flLeft" style="padding-left: 12px">
        <template
          v-for="(type, key, index) in typeConfig[calendarType].options"
        >
          <template v-if="index === 4 && optionsCount > 5">
            <div
              :class="[
                'circle-inactive',
                'colordropdown',
                'flLeft',
                { active: showAllCircles },
              ]"
              @click.stop="showAllCircles = !showAllCircles"
              :key="key"
              v-click-outside="closeDropdown"
            >
              <i class="el-icon-arrow-down"></i>
            </div>
            <div style="clear:both;" :key="'clear' + key"></div>
          </template>
          <div
            :class="[
              'colorContainer',
              'colorTitlePosition' + currentColorTypes[key].colorcode,
              index < 4 || optionsCount === 5 ? 'flLeft' : 'circlemore',
              { active: showAllCircles },
            ]"
            :key="'color' + key"
          >
            <div
              v-if="!currentColorTypes[key].active"
              :class="[
                'circle-inactive',
                'color' + currentColorTypes[key].colorcode,
              ]"
              @click="filterCalendar(key)"
            ></div>
            <i
              v-else
              :class="[
                'el-icon-circle-check',
                'color' + currentColorTypes[key].colorcode,
              ]"
              :style="{ 'font-size': '27px', position: 'relative' }"
              @click="filterCalendar(key)"
            ></i>
            <div style="clear:both;"></div>
            <div
              :class="[
                'colorTitle',
                'colorcard',
                index < 4 || optionsCount === 5
                  ? 'colorcard' + currentColorTypes[key].colorcode
                  : 'colormore',
              ]"
            >
              <div class="typestyle">
                {{ typeConfig[calendarType].label }} :
              </div>
              <div class="colorcardtext">{{ type }}</div>
              <div style="clear:both;"></div>
            </div>
          </div>
        </template>
        <div style="clear:both;"></div>
      </div>
      <el-popover
        ref="typepopover"
        placement="bottom"
        v-model="showTypePopup"
        width="200"
        trigger="click"
      >
        <div style="left: 10%;position: relative;padding-bottom: 10px;">
          <div style="margin: 4px 0 8px;">
            {{ $t('maintenance.calender.select_color_type') }}
          </div>
          <el-select
            class="mB5"
            @change="
              updateCalendarColor()
              showTypePopup = false
            "
            v-model="calendarType"
          >
            <el-option
              v-for="(type, name) in typeConfig"
              v-if="
                !type.views ||
                  !type.views.length ||
                  type.views.includes(currentView)
              "
              :key="type.id"
              :label="type.label"
              :value="name"
            ></el-option>
          </el-select>
        </div>
        <div
          class="flRight"
          style="margin-top: 6px;padding-left: 5px;cursor:pointer"
          slot="reference"
        >
          <span class="more-overflow"></span>
          <span class="more-overflow"></span>
          <span class="more-overflow"></span>
        </div>
      </el-popover>
      <div style="clear:both;"></div>
    </div>
    <div class="popup1">
      <div class="dropdownarrow">
        <div @click="loadResources('staff')" class="dropdown">
          {{ $t('maintenance.calender.staff') }}
        </div>
        <div @click="loadResources('team')" class="dropdown">
          {{ $t('maintenance.calender.team') }}
        </div>
      </div>
    </div>
    <div class="popup2" style="padding:10px">
      <template v-if="['asset', 'space'].includes(selectedResource)">
        <div
          v-for="field in resourceConfig"
          v-if="
            (field.spaceType !== 4 || isAsset) &&
              field.options &&
              Object.keys(field.options).length
          "
          :key="'resource' + field.spaceType"
          class="pB15"
        >
          <div class="filtertitle">{{ field.placeHolder }}</div>
          <el-select
            v-model="field.value"
            filterable
            clearable
            @change=";(isEventLoading = true), onLocationSelected(field)"
          >
            <el-option
              v-for="option in field.options"
              :key="field.spaceType + '_' + option.id"
              :label="option.name"
              :value="option.id"
            ></el-option>
          </el-select>
        </div>
        <template v-if="isAsset">
          <div class="filtertitle">{{ assetCategory.placeHolder }}</div>
          <el-select
            v-model="assetCategory.value"
            filterable
            clearable
            @change=";(isEventLoading = true), onCategorySelected()"
          >
            <el-option
              v-for="(label, value) in assetCategory.options"
              :key="value"
              :label="label"
              :value="value"
            ></el-option>
          </el-select>
        </template>
      </template>
      <template v-else-if="selectedResource === 'staff'">
        <div class="filtertitle">{{ $t('maintenance.calender.team') }}</div>
        <el-select
          v-model="selectedTeam"
          filterable
          clearable
          @change="onTeamSelected()"
        >
          <el-option
            v-for="group in groups"
            :key="'group' + group.id"
            :label="group.name"
            :value="group.id"
          ></el-option>
        </el-select>
      </template>
    </div>
    <div
      v-if="isPlanned"
      v-show="showPopover"
      :style="popoverStyle"
      v-click-outside="hide"
      class="position"
      id="eventpopover"
      style="width:404px;min-height:272px;background-color:#fff;z-index:1000;position:absolute;"
    >
      <div
        :class="eventDetails.colortype"
        style="border-bottom: 1px solid #eaedf1;padding-top:20px;padding-left:30px;padding-right:30px;padding-bottom:20px;"
      >
        <div>
          <span class="pull-right" style="cursor:pointer;">
            <template v-if="!eventDetails.projected">
              <el-dropdown
                v-if="
                  eventDetails.jobStatus === 1 || eventDetails.jobStatus === 2
                "
                trigger="click"
                @command="updatePmJob($event, eventDetails, 4)"
                size="small"
              >
                <el-button
                  v-if="!eventDetails.isMulti"
                  round
                  class="roundbutton"
                  >{{ $t('maintenance.calender.turn_off') }}</el-button
                >
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="current">{{
                    $t('maintenance.calender.selected')
                  }}</el-dropdown-item>
                  <el-dropdown-item command="all">All</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <el-button
                v-else-if="eventDetails.jobStatus === 4"
                round
                class="roundbutton"
                @click="updatePmJob('current', eventDetails, 1)"
                >{{ $t('maintenance.calender.turn_on') }}</el-button
              >
            </template>
            <i
              style=" font-size: 18px;font-weight: normal;color:#fff;vertical-align: middle;"
              class="el-icon-close"
              @click="closeDialog"
            ></i>
          </span>
          <div class="datestyle">{{ eventDetails.executionTime }}</div>
          <div class="eventstyle">{{ eventDetails.eventTitle }}</div>
        </div>
      </div>
      <div
        style="padding-top:20px;padding-left:30px;padding-right:30px;padding-bottom:20px;"
      >
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.type') }}
          </div>
          <div class="intext" style="float:left;">{{ eventDetails.type }}</div>
          <div style="clear:both;"></div>
        </div>
        <div v-if="eventDetails.category" class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.space_asset') }}
          </div>
          <div class="intext" style="float:left;">
            {{ eventDetails.category }}
          </div>
          <div style="clear:both;"></div>
        </div>
        <div v-else-if="eventDetails.resource" class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.space_asset') }}
          </div>
          <div class="intext" style="float:left;">
            {{ eventDetails.resource }}
          </div>
          <div style="clear:both;"></div>
        </div>
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.assignedto') }}
          </div>
          <div class="intext" style="float:left;">
            <span
              >{{
                eventDetails.assignmentGroup
                  ? eventDetails.assignmentGroup
                  : '---'
              }}
              /</span
            >
            <span>{{
              eventDetails.assignedTo ? eventDetails.assignedTo : '---'
            }}</span>
          </div>
          <div style="clear:both;"></div>
        </div>
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance._workorder.frequency') }}
          </div>
          <div class="intext" style="float:left;">
            {{ eventDetails.frequency }}
          </div>
          <div style="clear:both;"></div>
        </div>
        <div v-if="eventDetails.colorTypeValue" class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ typeConfig[calendarType].label }}
          </div>
          <div class="intext" style="float:left;">
            {{ eventDetails.colorTypeValue }}
          </div>
          <div style="clear:both;"></div>
        </div>
      </div>
      <div
        @click="opensummary(eventDetails)"
        style="cursor:pointer;padding-top:20px;padding-left:30px;padding-right:30px;padding-bottom:20px;"
      >
        <span class="moredetails">{{
          $t('maintenance.calender.view_details')
        }}</span>
      </div>
    </div>
    <div
      v-else-if="!isPlanned"
      v-show="showPopover"
      :style="popoverStyle"
      v-click-outside="hide"
      class="position"
      id="eventpopover"
      style="width:404px;min-height:272px;background-color:#fff;z-index:1;position:absolute;"
    >
      <div
        :class="eventDetails.colortype"
        style="border-bottom: 1px solid #eaedf1;padding-top:20px;padding-left:30px;padding-right:30px;padding-bottom:20px;"
      >
        <div>
          <span class="pull-right" style="cursor:pointer;" @click="closeDialog"
            ><i
              style=" font-size: 18px;font-weight: normal;"
              class="el-icon-close"
            ></i
          ></span>
          <div class="datestyle">{{ eventDetails.executionTime }}</div>
          <div class="eventstyle">{{ eventDetails.eventTitle }}</div>
        </div>
      </div>
      <div
        style="padding-top:20px;padding-left:30px;padding-right:30px;padding-bottom:20px;"
      >
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.type') }}
          </div>
          <div class="intext" style="float:left;">{{ eventDetails.type }}</div>
          <div style="clear:both;"></div>
        </div>
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.space_asset') }}
          </div>
          <div class="intext" style="float:left;">
            {{ eventDetails.resource }}
          </div>
          <div style="clear:both;"></div>
        </div>
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.assignedto') }}
          </div>
          <div class="intext" style="float:left;">
            <span
              >{{
                eventDetails.assignmentGroup
                  ? eventDetails.assignmentGroup
                  : '---'
              }}
              /</span
            >
            <span>{{
              eventDetails.assignedTo ? eventDetails.assignedTo : '---'
            }}</span>
          </div>
          <div style="clear:both;"></div>
        </div>
        <div class="pT20">
          <div class="subheader" style="width:100px;float:left;">
            {{ $t('maintenance.wr_list.category') }}
          </div>
          <div class="intext" style="float:left;">
            {{ eventDetails.category }}
          </div>
          <div style="clear:both;"></div>
        </div>
      </div>
      <div
        @click="opensummary(eventDetails)"
        style="cursor:pointer;padding-top:20px;padding-left:30px;padding-right:30px;padding-bottom:20px;"
      >
        <span class="moredetails">{{
          $t('maintenance.calender.more_details')
        }}</span>
      </div>
    </div>
    <div style="position: absolute; top: 50%;left:47.5%;z-index:5">
      <spinner :show="isEventLoading" size="70"></spinner>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'

import { FullCalendar } from 'vue-full-calendar'
import $ from 'jquery'
import { mapState, mapGetters } from 'vuex'
import ResourceMixin from '@/mixins/ResourceMixin'
import moment from 'moment-timezone'
import { getFieldValue, getFieldOptions } from 'util/picklist'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  title() {
    return this.$options.filters.viewName(this.currentView)
  },
  mixins: [ResourceMixin],
  data() {
    return {
      spaces: [],
      assets: [],
      showPopover: false,
      dontHide: false,
      popoverStyle: {
        top: '0px',
        left: '0px',
      },
      isEventLoading: false,
      eventDetails: {
        eventTitle: null,
        executionTime: null,
        type: null,
        asset: null,
        space: null,
        assignedTo: null,
        frequency: null,
        jobStatus: null,
        pmId: -1,
      },
      events: [],
      eventList: {},
      triggers: {},
      selectedResource: 'staff',
      selectedTeam: null,
      resourceList: [],
      config: {
        viewRender: function(view) {
          let name = $('.calenderview').data('resourceName')
          let field = $('.calenderview').data('resourceField')
          if (
            $('.calenderview .fc-resource-area .fc-widget-header #filtericons')
              .length === 0
          ) {
            $('.calenderview .fc-resource-area .fc-widget-header').append(
              '<div id="filtericons">' +
                '<i id="dropdownid" class="el-icon-arrow-down" style="position:absolute;bottom: 3px;z-index:1000;right: 72px;cursor: pointer;"></i>' +
                '<img src ="' +
                require('statics/icons/equalization.svg') +
                ' " id="filterid" style="cursor: pointer;width: 16px;height:15px;transform: rotate(90deg);position: absolute;z-index:1000;right: 15px;top: -18px;">' +
                '</div>'
            )
          }
          if (field === 'team') {
            $('.calenderview #filterid').hide()
          }
          $('.fc-resource-area .fc-widget-header .fc-cell-text').text(
            name || 'Staff'
          )
        },
        eventRender: function(event, element) {
          let isYearView =
            $('#calenderview').fullCalendar('getView').name ===
              'timelineYear' && event.frequencyText
          let html =
            '<div id="' +
            event.key +
            '" bgcolortype="' +
            event.colortype +
            '" class="fc-content eventcontainer bdcolor' +
            event.colortype +
            (isYearView ? ' pm-year' : '') +
            '"><span class="fc-time">'
          if (isYearView) {
            html += event.frequencyText + '</span>'
          } else {
            html +=
              event.starttime +
              '</span> <span class="fc-title">' +
              event.title +
              '</span></div>'
          }
          element.html(html)
        },
        header: {
          left: 'today,prev,next title',
          center: '',
          right:
            'timelineDay,timelineWeek,timelineMonth,timelineYear,agendaDay,agendaWeek,month',
        },
        buttonText: {
          today: this.$t('maintenance.calender.go_to_today'),
          month: this.$t('maintenance.planner.views.MONTH'),
          week: this.$t('maintenance.planner.views.WEEK'),
          day: this.$t('maintenance.planner.views.DAY'),
        },
        views: {
          timelineYear: {
            type: 'timeline',
            duration: { year: 1 },
            resourceAreaWidth: '15%',
            slotDuration: { weeks: 1 },
            slotLabelFormat: ['ww'],
            slotWidth: 30,
          },
          timelineMonth: {
            type: 'timeline',
            duration: { month: 1 },
            resourceAreaWidth: '15%',
            slotLabelFormat: ['DD'],
            slotWidth: 30,
          },
          timelineWeek: {
            type: 'timeline',
            duration: { weeks: 1 },
            slotDuration: { days: 1 },
            resourceAreaWidth: '15%',
            slotLabelFormat: ['ddd M/D'],
          },
          timelineDay: {
            type: 'timeline',
            duration: { days: 1 },
            resourceAreaWidth: '15%',
            slotMinutes: 60,
            slotDuration: '01:00:00',
            slotLabelInterval: '04:00:00',
          },
          agendaDay: {
            resources: false,
          },
          basicWeek: {
            buttonText: 'BASIC WEEK',
          },
          basicDay: {
            buttonText: 'BASIC DAY',
            resources: false,
          },
        },
        eventLimit: true,
        navLinks: true,
        selectable: true,
        selectHelper: true,
        editable: true,
        schedulerLicenseKey: 'GPL-My-Project-Is-Open-Source',
        defaultView:
          this.$route.params.viewname === 'pmplanner' ||
          this.$route.params.viewname === 'woplanner'
            ? 'timelineMonth'
            : 'month',
        fixedWeekCount: false,
        resources: [],
        eventAllow: function(dropLocation, draggedEvent) {
          if (draggedEvent.resourceId.includes('_')) {
            return false
          }
          return true
        },
        locale: this.$i18n.locale,
      },
      calendarType: 'frequency',
      typeConfig: {
        typeId: {
          label: this.$t('maintenance.wr_list.type'),
          options: {},
          wokey: 'type',
          noneLabel: 'Others',
        },
        categoryId: {
          label: this.$t('maintenance.wr_list.category'),
          options: {},
          wokey: 'category',
          noneLabel: 'Others',
        },
        statusId: {
          label: this.$t('maintenance._workorder.status'),
          options: {},
          wokey: 'status',
          views: ['woplanner', 'workorder'],
        },
        priorityId: {
          label: this.$t('maintenance.wr_list.priority'),
          options: {},
          wokey: 'priority',
          noneLabel: 'Others',
        },
        frequency: {
          label: this.$t('maintenance._workorder.frequency'),
          options: {},
          views: ['pmplanner', 'planned'],
        },
      },
      currentColorTypes: { '-1': { colorcode: 1, active: true } },
      eventColors: [
        '#ffccc2',
        '#ffedb3',
        '#cdefdb',
        '#d9d3ee',
        '#f9c9db',
        '#b9ede9',
        '#ffddba',
        '#faf5cb',
        '#e7f3cf',
        '#bfd7e1',
      ],
      showTypePopup: false,
      showAllCircles: false,
      frequencyText: {
        0: 'O',
        1: 'D',
        2: 'W',
        3: 'M',
        4: 'Q',
        5: 'H',
        6: 'A',
      },
    }
  },
  filters: {
    viewName: function(name) {
      if (name === 'planned') {
        return 'Planned Maintenance'
      } else if (name === 'pmplanner') {
        return 'PM Planner'
      } else if (name === 'workorder') {
        return 'Work Order'
      } else if (name === 'woplanner') {
        return 'Work Order Planner'
      }
    },
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadUsers')
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      ticketstatus: state => state.ticketStatus.workorder,
      assetcategory: state => state.assetCategory,
      users: state => state.users,
    }),

    ...mapGetters([
      'getTicketTypePickList',
      'getSpaceCategoryPickList',
      'getGroup',
      'getTicketCategory',
    ]),
    tickettype() {
      return this.getTicketTypePickList()
    },
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    filters() {
      return this.$route.query.search
        ? JSON.parse(this.$route.query.search)
        : null
    },
    currentView() {
      return this.$route.params.viewname
    },
    typeFieldName() {
      return this.typeConfig[this.calendarType].hasOwnProperty('key')
        ? this.typeConfig[this.calendarType].key
        : this.calendarType
    },
    optionsCount() {
      return Object.keys(this.typeConfig[this.calendarType].options).length
    },
    resourceview() {
      return (
        this.currentView === 'pmplanner' || this.currentView === 'woplanner'
      )
    },
    isPlanned() {
      return this.currentView === 'planned' || this.currentView === 'pmplanner'
    },
    isPmV2() {
      return this.$helpers.isLicenseEnabled('PM_PLANNER')
    },
    moduleName() {
      return this.isPlanned ? 'planned' : 'workorder'
    },
    calType() {
      return this.isPlanned ? '' : this.$route.query.type || 'dueDate'
    },
    isNewCode() {
      return this.$route.query.code !== 'old'
    },
  },
  components: {
    'full-calendar': FullCalendar,
  },
  watch: {
    filters: function(val) {
      this.resetCircle()
      this.loadEvents()
    },
    currentView: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadEvents()
        this.setTitle(this.$options.filters.viewName(newVal))
      }
      this.configHeaders()
      this.registerEvents()
      if (this.resourceview) {
        this.reInitResourceData()
      }
    },
    calendarType() {
      this.setCurrentColorType()
      if (this.filters) {
        this.$router.replace({ query: {} })
      } else {
        this.loadEvents()
      }
    },
    assetList(assets) {
      if (this.selectedResource === 'asset') {
        if (!assets || !assets.length) {
          return
        }
        let list = assets.map(asset => ({ id: asset.id, title: asset.name }))
        let resourceMap = {}
        if (this.isPlanned) {
          if (this.events) {
            this.events.forEach(i => {
              resourceMap[i.space] = true
              resourceMap[i.asset] = true
            })
          }
          if (list) {
            this.resourceList = list.filter(i => resourceMap[i.id])
          }
        } else {
          this.resourceList = list
        }
        this.sortAndSetResources()
      }
    },
    spaceList(list) {
      if (this.selectedResource === 'space') {
        if (!list || !list.length) {
          return
        }
        let l = list.map(space => ({ id: space.id, title: space.name }))
        let resourceMap = {}
        if (this.events) {
          this.events.forEach(i => {
            resourceMap[i.space] = true
            resourceMap[i.asset] = true
          })
        }
        if (list) {
          this.resourceList = l.filter(i => resourceMap[i.id])
        }
        this.sortAndSetResources()
      }
    },
    calType(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.loadEvents()
      }
    },
  },
  mounted() {
    Vue.use(FullCalendar)
    this.setFilterTypeOptions()
    this.configHeaders()
    this.loadEvents()
    this.registerEvents()
  },
  methods: {
    loadSpaceList() {
      let l = this.spaces.map(space => ({ id: space.id, title: space.name }))
      let resourceMap = {}
      if (this.events) {
        this.events.forEach(i => {
          resourceMap[i.space] = true
          resourceMap[i.asset] = true
        })
      }
      if (l) {
        this.resourceList = l.filter(i => resourceMap[i.id])
      }
      this.sortAndSetResources()
    },
    loadAssetList() {
      let list = this.assets.map(asset => ({ id: asset.id, title: asset.name }))
      let resourceMap = {}
      if (this.events) {
        this.events.forEach(i => {
          resourceMap[i.space] = true
          resourceMap[i.asset] = true
        })
      }
      if (list) {
        this.resourceList = list.filter(i => resourceMap[i.id])
      }
      this.sortAndSetResources()
    },
    handleScroll() {
      $('#calendercolors').css({ top: 20 + $('#calenderview').position().top })
    },
    configHeaders() {
      if (this.resourceview) {
        if ($('#calenderview').fullCalendar('getView').name === 'agendaWeek') {
          $('#calenderview').fullCalendar('changeView', 'timelineWeek')
        } else if (
          $('#calenderview').fullCalendar('getView').name === 'agendaDay'
        ) {
          $('#calenderview').fullCalendar('changeView', 'timelineDay')
        } else {
          $('#calenderview').fullCalendar('changeView', 'timelineMonth')
        }
        this.loadResources('staff')
        $('#calenderview .fc-month-button').hide()
        $('#calenderview .fc-agendaWeek-button').hide()
        $('#calenderview .fc-agendaDay-button').hide()
        $('#calenderview .fc-timelineYear-button').show()
        $('#calenderview .fc-timelineMonth-button').show()
        $('#calenderview .fc-timelineWeek-button').show()
        $('#calenderview .fc-timelineDay-button').show()
      } else {
        if (
          $('#calenderview').fullCalendar('getView').name === 'timelineWeek'
        ) {
          $('#calenderview').fullCalendar('changeView', 'agendaWeek')
        } else if (
          $('#calenderview').fullCalendar('getView').name === 'timelineDay'
        ) {
          $('#calenderview').fullCalendar('changeView', 'agendaDay')
        } else {
          $('#calenderview').fullCalendar('changeView', 'month')
        }
        $('#calenderview .fc-timelineYear-button').hide()
        $('#calenderview .fc-timelineMonth-button').hide()
        $('#calenderview .fc-timelineWeek-button').hide()
        $('#calenderview .fc-timelineDay-button').hide()
        $('#calenderview .fc-month-button').show()
        $('#calenderview .fc-agendaWeek-button').show()
        $('#calenderview .fc-agendaDay-button').show()
      }
    },
    getType(template) {
      let { isPmV2 } = this
      let { typeId } = template || {}

      if (isPmV2) {
        return this.$getProperty(template, 'type', '---')
      } else if (typeId > 0) {
        return this.getPMType(typeId) || '---'
      }
    },
    async eventSelected(calEvent, jsEvent, view) {
      if (this.isPlanned) {
        let { eventList } = this
        let { job, pmId } = calEvent || {}
        let pmObj = eventList[pmId] || {}
        let { woTemplate } = pmObj || {}
        let { typeId } = woTemplate || {}
        let template = { ...job, typeId }
        let asset = '---'

        if (template.assetId > 0) {
          let { error, data } = await getFieldValue({
            lookupModuleName: 'asset',
            selectedOptionId: [template.assetId],
          })

          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            asset = this.$getProperty(data, '0.label', '---')
          }
        }
        this.eventDetails = {
          isMulti: calEvent.isMulti,
          eventTitle: calEvent.title,
          executionTime: calEvent.startdate,
          type: this.getType(template),
          asset,
          space:
            template.spaceId > 0
              ? this.$store.getters.getSpace(template.spaceId).displayName
              : '---',
          resource:
            template.resourceId > 0 && template.resource
              ? template.resource.name
              : '---',
          assignedTo:
            template.assignedToId > 0
              ? this.$store.getters.getUser(template.assignedToId).name
              : '',
          assignmentGroup:
            template.assignmentGroupId > 0
              ? this.getGroup(template.assignmentGroupId).name
              : '',
          frequency: this.triggers[calEvent.job.pmTriggerId].scheduleMsg,
          id: calEvent.id,
          colortype: 'bgcolor' + calEvent.colortype,
          jobId: calEvent.job.id,
          pmId: calEvent.pmId,
          jobStatus: calEvent.job.status,
          projected: calEvent.job.projected,
          category: calEvent.category,
        }
        if (
          this.calendarType !== 'typeId' &&
          this.calendarType !== 'frequency'
        ) {
          let value =
            this.eventList[calEvent.pmId][this.calendarType] ||
            template[this.calendarType] ||
            -1
          this.eventDetails.colorTypeValue =
            value !== -1
              ? this.typeConfig[this.calendarType].options[value]
              : '---'
        }
      } else {
        let {
          type,
          asset,
          space,
          resource,
          assignedTo,
          assignmentGroup,
          assignmentGroupId,
          category,
          id,
        } = this.eventList[calEvent.key] || {}
        let assetValue = '---'

        if (asset && asset.id > 0) {
          let { error, data } = await getFieldValue({
            lookupModuleName: 'asset',
            selectedOptionId: [asset.id],
          })

          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            asset = this.$getProperty(data, '0.label', '---')
          }
        }
        this.eventDetails = {
          eventTitle: calEvent.title,
          executionTime: calEvent.startdate,
          type: type && type.id > 0 ? this.getPMType(type.id) : '---',
          asset: assetValue,
          space:
            space && space.id > 0
              ? this.$store.getters.getSpace(space.id).displayName
              : '---',
          resource: resource && resource.id > 0 ? resource.name : '---',
          assignedTo:
            assignedTo && assignedTo.id > 0
              ? this.$store.getters.getUser(assignedTo.id).name
              : '',
          assignmentGroup:
            assignmentGroup && assignmentGroupId > 0
              ? this.getGroup(assignmentGroupId).name
              : '',
          category:
            category && category.id > 0
              ? this.getTicketCategory(category.id).displayName
              : '---',
          id: id,
          colortype: 'bgcolor' + calEvent.colortype,
        }
      }
      $("a[class*='eventselected']").removeClass(function(index, css) {
        return (css.match(/(^|\s)eventselected\S+/g) || []).join(' ')
      })
      if (calEvent.resourceview) {
        $('#' + calEvent.key)
          .parent()
          .addClass('eventselected00')
          .addClass('eventselected' + $('#' + calEvent.key).attr('bgcolortype'))
      } else {
        $('#' + calEvent.key)
          .parent()
          .addClass('eventselected0')
          .addClass('eventselected' + $('#' + calEvent.key).attr('bgcolortype'))
      }
      this.showPopover = true
      this.dontHide = true
      this.$nextTick(() => {
        let appHeight = $('#q-app').height()
        let menuWidth = $('#eventpopover').width()
        let menuHeight = $('#eventpopover').height() + 40
        let reSize = jsEvent.pageY - menuHeight / 2
        if (appHeight <= reSize + menuHeight) {
          reSize = jsEvent.pageY - menuHeight - 50
        }
        let borderHeight = reSize - 50

        if (borderHeight <= 120) {
          borderHeight = 120
        }

        let menuVisX =
          jsEvent.pageX > $(window).width() / 2
            ? jsEvent.pageX - menuWidth - 100
            : jsEvent.pageX
        let menuVisY = borderHeight
        this.popoverStyle.left = menuVisX + 'px'
        this.popoverStyle.top = menuVisY + 'px'
      })
    },
    async eventDrop(calEvent) {
      if (this.isPlanned) {
        if (calEvent.job.id !== -1) {
          let self = this
          let params = {
            id: [calEvent.job.id],
            pmJob: {
              nextExecutionTime: self.$helpers
                .getDateInOrg(calEvent.start.format())
                .unix(),
            },
            pmId: calEvent.pmId,
          }
          if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
            params = {
              id: [calEvent.job.id],
              pmJob: {
                nextExecutionTime:
                  self.$helpers.getDateInOrg(calEvent.start.format()).unix() *
                  1000,
              },
              pmId: calEvent.pmId,
            }
          }
          if (
            this.selectedResource === 'staff' ||
            this.selectedResource === 'team'
          ) {
            params.resourceId = calEvent.resourceId
            params.resourceType = this.selectedResource
          }
          let { error } = await API.post(
            '/workorder/updatePreventiveMaintenanceJob',
            params
          )
          if (!error) {
            this.$message.success(this.$t('maintenance.pm.job_updated'))
            this.loadEvents()
          }
        }
      } else {
        let params = {
          id: [calEvent.key],
          fields: {
            dueDate: this.$helpers.getTimeInOrg(calEvent.start.format()),
          },
        }
        if (this.selectedResource === 'staff') {
          params.fields.assignedTo = { id: calEvent.resourceId }
        } else if (this.selectedResource === 'team') {
          params.fields.assignmentGroup = { id: calEvent.resourceId }
        } else {
          params.fields.resource = { id: calEvent.resourceId }
        }

        this.$store
          .dispatch('workorder/updateWorkOrder', params)
          .then(response => {
            this.$message.success('Workorder updated successfully!')
            this.loadEvents()
          })
          .catch(() => {
            this.$message.error('Workorder updation failed')
          })
      }
    },
    getPMType(type) {
      return this.tickettype[type]
    },
    removeResources() {
      let prevResources = $('#calenderview').fullCalendar('getResources')
      for (let i = 0; i < prevResources.length; i++) {
        $('#calenderview').fullCalendar('removeResource', prevResources[i].id)
      }
    },
    loadResources(resource) {
      this.isEventLoading = true
      this.removeResources()
      let name
      this.selectedResource = resource
      if (resource === 'staff') {
        name = 'Staff'
        this.resourceList = [
          { id: -1, title: 'Unassigned' },
          ...this.users.map(user => ({ id: user.id, title: user.name })),
        ]
        if (this.selectedResource !== 'staff') {
          this.selectedTeam = null
        }
      } else if (resource === 'team') {
        name = 'Team'
        let groups = this.groups
        this.resourceList = [{ id: -1, title: 'Unassigned' }]
        for (let i = 0; i < groups.length; i++) {
          this.resourceList.push({
            id: groups[i].id,
            title: groups[i].name,
            children: [],
          })
          for (let j = 0; j < groups[i].members.length; j++) {
            this.resourceList[i + 1].children.push({
              id: groups[i].id + '_' + groups[i].members[j].memberId,
              title: groups[i].members[j].name,
            })
          }
        }
      } else if (resource === 'asset') {
        name = 'Asset'
        this.isAsset = true
        this.loadAssetList()
      } else if (resource === 'space') {
        name = 'Space'
        this.isAsset = false
        this.loadSpaceList()
      }

      if (resource !== 'team') {
        $('.calenderview #filterid').show()
      } else {
        $('.calenderview #filterid').hide()
      }
      $('.fc-resource-area .fc-widget-header .fc-cell-text').text(name)
      $('.calenderview').data('resourceName', name)
      $('.calenderview').data('resourceField', this.selectedResource)

      if (resource !== 'asset' && resource !== 'space') {
        this.sortAndSetResources()
      }
    },
    onTeamSelected() {
      $('#calendarpage .popup2').removeClass('showpopup2')
      if (this.selectedTeam) {
        this.removeResources()
        let users = this.groups.find(group => group.id === this.selectedTeam)
          .members
        this.resourceList = []
        for (let i = 0; i < users.length; i++) {
          this.resourceList[i] = { id: users[i].id, title: users[i].name }
        }
        this.sortAndSetResources()
      } else {
        this.loadResources('staff')
      }
    },
    loadGroupedEvents(response) {
      if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
        return this.loadNewGroupedEvents(response)
      } else {
        return this.loadOldGroupedEvents(response)
      }
    },
    loadNewGroupedEvents(response) {
      if (!response.data.pmTriggerTimeBasedGroupedMap) {
        return []
      }

      let events = []
      let timeGroups = response.data.pmTriggerTimeBasedGroupedMap
      let pmMap = response.data.pmMap || {}
      let triggerMap = response.data.pmTriggerMap || {}
      let dateFormat = 'YYYY-MM-DD HH:mm'

      Object.keys(timeGroups).forEach(nextExecutionTime => {
        let trigJobs = timeGroups[nextExecutionTime]
        Object.keys(trigJobs).forEach((triggerId, i) => {
          let jobs = trigJobs[triggerId]
          let trigger = triggerMap[triggerId]
          let pm = pmMap[trigger.pmId]
          let template = pm.woTemplate
          let category = null
          let resources = jobs.map(x => x.resource)
          let length = resources ? resources.length : 0
          if (
            pm.pmCreationType &&
            pm.pmCreationType === 2 &&
            pm.assignmentType
          ) {
            if (pm.assignmentType === 1) {
              if (length) {
                category = `${length} Floors`
              }
            } else if (
              pm.assignmentType === 4 &&
              pm.assetCategoryId &&
              pm.assetCategoryId > 0 &&
              this.assetcategory
            ) {
              let cat = this.assetcategory.find(
                category => category.id === pm.assetCategoryId
              )
              if (cat) {
                if (length) {
                  category = `${length} ${cat.name}`
                }
              }
            } else if (
              pm.assignmentType === 3 &&
              pm.spaceCategoryId &&
              pm.spaceCategoryId > 0 &&
              this.spacecategory
            ) {
              let cat = this.spacecategory[pm.spaceCategoryId]
              if (cat) {
                if (length) {
                  category = `${length} ${cat.name}`
                }
              }
            }
          }

          let next = Number(nextExecutionTime)
          if (!(jobs[0].status === 4 && next < this.$options.filters.now())) {
            let event = {
              isMulti: true,
              key: 'event-multi' + i,
              jobId: null,
              pmId: pm.id,
              title: this.$getProperty(jobs[0], 'woSubject', '---'),
              start: this.$options.filters.toDateFormat(next, dateFormat),
              end: this.$options.filters.toDateFormat(
                Number(nextExecutionTime) + 30 * 60,
                dateFormat
              ),
              asset: template.resourceId,
              space: template.resourceId,
              team: template.assignmentGroupId,
              staff: template.assignedToId,
              starttime: this.$options.filters.toDateFormat(next, 'HH:mm'),
              startdate: this.$options.filters.toDateFormat(next, dateFormat),
              pmfrequency: pm.frequency,
              frequencyText: this.frequencyText[pm.frequency],
              resourceview: this.resourceview,
              job: jobs[0],
              assignmentType: pm.assignmentType,
              category,
              constraint: {
                start: this.$options.filters.toDateFormat(
                  new Date(),
                  'YYYY-MM-DD'
                ),
                end: this.$options.filters.addDays(new Date(), 61, true),
              },
            }
            if (this.$getProperty(event, 'job.pmTriggerId')) {
              event.frequency = this.$getProperty(
                this.triggers,
                `${event.job.pmTriggerId}.frequency`,
                -1
              )
            }
            if (!event.frequency) {
              event.frequency = -1
            }
            this.setPMEventOptions(event)
            events.push(event)
          }
        })
      })
      return events
    },
    loadOldGroupedEvents(response) {
      if (!response.data.pmTriggerTimeBasedGroupedMap) {
        return []
      }

      let events = []
      let timeGroups = response.data.pmTriggerTimeBasedGroupedMap
      let pmMap = response.data.pmMap || {}
      let triggerMap = response.data.pmTriggerMap || {}
      let dateFormat = 'YYYY-MM-DD HH:mm'

      Object.keys(timeGroups).forEach(nextExecutionTime => {
        let trigJobs = timeGroups[nextExecutionTime]
        Object.keys(trigJobs).forEach((triggerId, i) => {
          let jobs = trigJobs[triggerId]
          let trigger = triggerMap[triggerId]
          let pm = pmMap[trigger.pmId]
          let template = pm.woTemplate
          let category = null
          let resources = jobs.map(x => x.resource)
          let length = resources ? resources.length : 0
          if (
            pm.pmCreationType &&
            pm.pmCreationType === 2 &&
            pm.assignmentType
          ) {
            if (pm.assignmentType === 1) {
              if (length) {
                category = `${length} Floors`
              }
            } else if (
              pm.assignmentType === 4 &&
              pm.assetCategoryId &&
              pm.assetCategoryId > 0 &&
              this.assetcategory
            ) {
              let cat = this.assetcategory.find(
                category => category.id === pm.assetCategoryId
              )
              if (cat) {
                if (length) {
                  category = `${length} ${cat.name}`
                }
              }
            } else if (
              pm.assignmentType === 3 &&
              pm.spaceCategoryId &&
              pm.spaceCategoryId > 0 &&
              this.spacecategory
            ) {
              let cat = this.spacecategory[pm.spaceCategoryId]
              if (cat) {
                if (length) {
                  category = `${length} ${cat.name}`
                }
              }
            }
          }

          let next = Number(nextExecutionTime) * 1000
          if (!(jobs[0].status === 4 && next < this.$options.filters.now())) {
            let event = {
              isMulti: true,
              key: 'event-multi' + i,
              jobId: null,
              pmId: pm.id,
              title: this.$getProperty(jobs[0], 'woSubject', '---'),
              start: this.$options.filters.toDateFormat(next, dateFormat),
              end: this.$options.filters.toDateFormat(
                (Number(nextExecutionTime) + 30 * 60) * 1000,
                dateFormat
              ),
              asset: template.resourceId,
              space: template.resourceId,
              team: template.assignmentGroupId,
              staff: template.assignedToId,
              starttime: this.$options.filters.toDateFormat(next, 'HH:mm'),
              startdate: this.$options.filters.toDateFormat(next, dateFormat),
              pmfrequency: pm.frequency,
              frequencyText: this.frequencyText[pm.frequency],
              resourceview: this.resourceview,
              job: jobs[0],
              assignmentType: pm.assignmentType,
              category,
              constraint: {
                start: this.$options.filters.toDateFormat(
                  new Date(),
                  'YYYY-MM-DD'
                ),
                end: this.$options.filters.addDays(new Date(), 61, true),
              },
            }
            this.setPMEventOptions(event)
            events.push(event)
          }
        })
      })
      return events
    },
    async loadEvents() {
      let { isPmV2 } = this
      if (isPmV2) {
        await this.loadPmV2Events()
      } else if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
        this.loadNewEvents()
      } else {
        this.loadOldEvents()
      }
    },
    async loadPmV2Events() {
      this.isEventLoading = true
      this.events = []
      let {
        $timezone,
        $options,
        currentView,
        filters,
        resourceview,
        frequencyText,
        isPlanned,
      } = this
      let startTime =
        moment($('#calenderview').fullCalendar('getView').start)
          .tz($timezone)
          .startOf('day')
          .valueOf() / 1000
      let endTime =
        moment($('#calenderview').fullCalendar('getView').end)
          .tz($timezone)
          .startOf('day')
          .valueOf() / 1000
      let url = `v3/workorder/calender/getPpmJobs`
      let params = { startTime, endTime, currentView }
      let { frequency } = filters || {}
      if (!isEmpty(filters)) {
        if (isEmpty(frequency)) {
          params = {
            ...params,
            filters: encodeURIComponent(JSON.stringify(filters)),
          }
        } else {
          params = {
            ...params,
            frequencyFilter: encodeURIComponent(JSON.stringify(filters)),
          }
        }
      }
      let { error, data } = await API.get(url, params)

      if (isEmpty(error)) {
        let { result } = data || {}
        let {
          plannedmaintenanceJobList: pmJobList = [],
          plannedmaintenanceMap: pmMap,
          planedmaintenaceTriggerList: pmTriggerMap,
        } = result || {}
        let dateFormat = 'YYYY-MM-DD HH:mm'

        if (!isEmpty(pmJobList)) {
          this.eventList = pmMap || {}
          this.triggers = pmTriggerMap || {}

          pmJobList.forEach(job => {
            let {
              nextExecutionTime,
              pmTriggerId,
              resourceId,
              status,
              id,
              woSubject,
              assignmentGroupId,
              assignedToId,
            } = job || {}
            let pmTrigger = this.$getProperty(
              pmTriggerMap,
              `${pmTriggerId}`,
              {}
            )
            let { frequency } = pmTrigger || {}
            let skipEvent =
              status === 4 && nextExecutionTime < $options.filters.now()

            if (!skipEvent) {
              let event = {
                ...job,
                key: `event${id}`,
                jobId: id,
                title: woSubject,
                start: $options.filters.toDateFormat(
                  nextExecutionTime,
                  dateFormat
                ),
                end: $options.filters.toDateFormat(
                  nextExecutionTime + 30 * 60,
                  dateFormat
                ),
                asset: resourceId,
                space: resourceId,
                team: assignmentGroupId,
                staff: assignedToId,
                starttime: $options.filters.toDateFormat(
                  nextExecutionTime,
                  'HH:mm'
                ),
                startdate: $options.filters.toDateFormat(
                  nextExecutionTime,
                  dateFormat
                ),
                frequencyText: this.$getProperty(
                  frequencyText,
                  `${frequency}`,
                  '---'
                ),
                constraint: {
                  start: $options.filters.toDateFormat(
                    new Date(),
                    'YYYY-MM-DD'
                  ),
                  end: $options.filters.addDays(new Date(), 61, true),
                },
                resourceview,
                job,
                frequency: frequency,
              }
              event = this.setPMV2EventOptions(event) || {}
              this.events.push(event)
            }
          })
        }
        if (isPlanned && currentView === 'planned') {
          let groupedEvents = this.loadGroupedEvents({ data: result }) || []
          if (!isEmpty(groupedEvents)) this.events.push(...groupedEvents)
        }
        this.sortAndSetResources()
      }
      this.isEventLoading = false
    },
    loadNewEvents() {
      let self = this
      self.events = []
      let startDate =
        moment($('#calenderview').fullCalendar('getView').start)
          .tz(self.$timezone)
          .startOf('day')
          .valueOf() / 1000
      let endDate =
        moment($('#calenderview').fullCalendar('getView').end)
          .tz(self.$timezone)
          .startOf('day')
          .valueOf() / 1000
      let url = ''
      if (!this.isPlanned) {
        url = '/workorder/open?startTime=' + startDate + '&endTime=' + endDate
      } else {
        url =
          '/workorder/getUpcomingPreventiveMaintenance?startTime=' +
          startDate +
          '&endTime=' +
          endDate
        if (self.isNewCode) {
          url =
            '/workorder/getPMJobs?startTime=' +
            startDate +
            '&endTime=' +
            endDate
        }
        url += `&currentView=${self.currentView}`
      }
      if (this.filters) {
        let filters = this.filters
        //frequency field doesn not exist in workorder module , sending it breaks Filter->Criteria construction
        // is frequency filter is selected , send it as a seperate param instead
        if (!this.isPlanned) {
          url += '&includeParentFilter=true'
        }

        if (!filters.frequency) {
          url += '&filters=' + encodeURIComponent(JSON.stringify(filters))
        } else {
          url +=
            '&frequencyFilter=' + encodeURIComponent(JSON.stringify(filters))
        }
      }
      this.isEventLoading = true
      self.$http.get(url).then(function(response) {
        self.events = []
        let dateFormat = 'YYYY-MM-DD HH:mm'
        if (
          self.isPlanned &&
          (response.data.pmJobs || response.data.pmJobList)
        ) {
          self.eventList = response.data.pmMap
          self.triggers = response.data.pmTriggerMap
          self.assets = []
          self.spaces = []
          if (response.data.pmResourcesMap) {
            for (let key in response.data.pmResourcesMap) {
              if (response.data.pmResourcesMap[key].resourceType === 2) {
                self.assets.push(response.data.pmResourcesMap[key])
              } else {
                self.spaces.push(response.data.pmResourcesMap[key])
              }
            }
          }

          let pmJobs = []
          if (self.isNewCode) {
            pmJobs = response.data.pmJobList
          } else {
            pmJobs = response.data.pmJobs
          }
          for (let i = 0; i < pmJobs.length; i++) {
            let job = pmJobs[i]
            let nextExecutionTime = job.nextExecutionTime
            if (
              job.status === 4 &&
              nextExecutionTime < self.$options.filters.now()
            ) {
              continue
            }

            let pmTrigger = response.data.pmTriggerMap[job.pmTriggerId]
            let pm = response.data.pmMap[pmTrigger.pmId]
            let template = job.template ? job.template : pm.woTemplate

            let resourceId = template.resourceId
            if (
              (!resourceId || resourceId <= 0) &&
              job.resourceId &&
              job.resourceId > 0
            ) {
              resourceId = job.resourceId
            }

            let assignedToId = job.assignedToId
            if (!assignedToId || assignedToId <= 0) {
              assignedToId = template.assignedToId
            }

            let event = {
              id: job.id,
              key: 'event' + i,
              jobId: job.id,
              pmId: pm.id,
              title: self.$getProperty(job, 'woSubject', '---'),
              start: self.$options.filters.toDateFormat(
                nextExecutionTime,
                dateFormat
              ),
              end: self.$options.filters.toDateFormat(
                job.nextExecutionTime + 30 * 60,
                dateFormat
              ),
              asset: resourceId,
              space: resourceId,
              team: template.assignmentGroupId,
              staff: assignedToId,
              starttime: self.$options.filters.toDateFormat(
                nextExecutionTime,
                'HH:mm'
              ),
              startdate: self.$options.filters.toDateFormat(
                nextExecutionTime,
                dateFormat
              ),
              pmfrequency: pm.frequency,
              frequencyText: self.frequencyText[pm.frequency],
              resourceview: self.resourceview,
              job: job,
              constraint: {
                start: self.$options.filters.toDateFormat(
                  new Date(),
                  'YYYY-MM-DD'
                ),
                end: self.$options.filters.addDays(new Date(), 61, true),
              },
            }
            //temp fix for frequency legend color not set

            if (self.$getProperty(event, 'job.pmTriggerId')) {
              event.frequency = self.$getProperty(
                self.triggers,
                `${event.job.pmTriggerId}.frequency`,
                -1
              )
            }
            if (!event.frequency) {
              event.frequency = -1
            }

            self.setPMEventOptions(event)
            self.events.push(event)
          }
        } else if (response.data.workOrders) {
          self.assets = []
          self.spaces = []
          for (let x in response.data.workOrders) {
            let wo = response.data.workOrders[x]

            if (wo.resource) {
              if (wo.resource.resourceType === 2) {
                self.assets.push(wo.resource)
              } else {
                self.spaces.push(wo.resource)
              }
            }

            let colorCode
            let color
            let type = self.typeConfig[self.calendarType].wokey
              ? self.typeConfig[self.calendarType].wokey
              : self.calendarType
            let colorTypeValue =
              (wo[type] && typeof wo[type] === 'object'
                ? wo[type].id
                : wo[type]) || -1
            colorCode = self.currentColorTypes[colorTypeValue].colorcode
            color = self.eventColors[colorCode - 1]

            let start, end
            if (self.calType === 'est') {
              start = self.$options.filters.toDateFormat(
                wo.estimatedStart,
                dateFormat
              )
              end = self.$options.filters.toDateFormat(
                wo.estimatedEnd,
                dateFormat
              )
            } else {
              start = self.$options.filters.toDateFormat(wo.dueDate, dateFormat)
              end = self.$options.filters.toDateFormat(
                wo.dueDate + 30 * 60 * 1000,
                dateFormat
              )
            }

            self.events.push({
              key: wo.id,
              title: wo.subject,
              start: start,
              end: end,
              starttime: self.$options.filters.toDateFormat(
                wo.dueDate,
                'HH:mm'
              ),
              startdate: self.$options.filters.toDateFormat(
                wo.dueDate,
                dateFormat
              ),
              asset: wo.resource ? wo.resource.id : -1,
              space: wo.resource ? wo.resource.id : -1,
              team: wo.assignmentGroup ? wo.assignmentGroup.id : -1,
              staff: wo.assignedTo ? wo.assignedTo.id : -1,
              resourceview: self.resourceview,
              editable: true,
              colortype: colorCode,
              color: color,
              constraint: {
                start: self.$options.filters.toDateFormat(
                  new Date(),
                  'YYYY-MM-DD'
                ),
                end: self.$options.filters.addDays(new Date(), 61, true),
              },
            })
            self.eventList[wo.id] = wo
          }
        }
        if (self.isPlanned) {
          if (self.currentView === 'planned') {
            let multi = self.loadGroupedEvents(response)
            if (multi && multi.length) {
              self.events.push(...multi)
            }
          }
        }
        self.sortAndSetResources()
        self.isEventLoading = false
      })
    },
    loadOldEvents() {
      let self = this
      self.events = []
      let startDate =
        moment($('#calenderview').fullCalendar('getView').start)
          .tz(self.$timezone)
          .startOf('day')
          .valueOf() / 1000
      let endDate =
        moment($('#calenderview').fullCalendar('getView').end)
          .tz(self.$timezone)
          .startOf('day')
          .valueOf() / 1000
      let url = ''
      if (!this.isPlanned) {
        url = '/workorder/open?startTime=' + startDate + '&endTime=' + endDate
      } else {
        url =
          '/workorder/getUpcomingPreventiveMaintenance?startTime=' +
          startDate +
          '&endTime=' +
          endDate
        if (self.isNewCode) {
          url =
            '/workorder/getPMJobs?startTime=' +
            startDate +
            '&endTime=' +
            endDate
        }
        url += `&currentView=${self.currentView}`
      }
      if (this.filters) {
        let filters = this.filters
        if (!this.isPlanned) {
          url += '&includeParentFilter=true'
        }
        url += '&filters=' + encodeURIComponent(JSON.stringify(filters))
      }
      this.isEventLoading = true
      self.$http.get(url).then(function(response) {
        self.events = []
        let dateFormat = 'YYYY-MM-DD HH:mm'
        if (
          self.isPlanned &&
          (response.data.pmJobs || response.data.pmJobList)
        ) {
          self.eventList = response.data.pmMap
          self.triggers = response.data.pmTriggerMap
          self.assets = []
          self.spaces = []
          if (response.data.pmResourcesMap) {
            for (let key in response.data.pmResourcesMap) {
              if (response.data.pmResourcesMap[key].resourceType === 2) {
                self.assets.push(response.data.pmResourcesMap[key])
              } else {
                self.spaces.push(response.data.pmResourcesMap[key])
              }
            }
          }

          let pmJobs = []
          if (self.isNewCode) {
            pmJobs = response.data.pmJobList
          } else {
            pmJobs = response.data.pmJobs
          }
          for (let i = 0; i < pmJobs.length; i++) {
            let job = pmJobs[i]
            let nextExecutionTime = job.nextExecutionTime * 1000
            if (
              job.status === 4 &&
              nextExecutionTime < self.$options.filters.now()
            ) {
              continue
            }

            let pmTrigger = response.data.pmTriggerMap[job.pmTriggerId]
            let pm = response.data.pmMap[pmTrigger.pmId]
            let template = job.template ? job.template : pm.woTemplate

            let resourceId = template.resourceId
            if (
              (!resourceId || resourceId <= 0) &&
              job.resourceId &&
              job.resourceId > 0
            ) {
              resourceId = job.resourceId
            }

            let assignedToId = job.assignedToId
            if (!assignedToId || assignedToId <= 0) {
              assignedToId = template.assignedToId
            }

            let event = {
              id: job.id,
              key: 'event' + i,
              jobId: job.id,
              pmId: pm.id,
              title: self.$getProperty(job, 'woSubject', '---'),
              start: self.$options.filters.toDateFormat(
                nextExecutionTime,
                dateFormat
              ),
              end: self.$options.filters.toDateFormat(
                (job.nextExecutionTime + 30 * 60) * 1000,
                dateFormat
              ),
              asset: resourceId,
              space: resourceId,
              team: template.assignmentGroupId,
              staff: assignedToId,
              starttime: self.$options.filters.toDateFormat(
                nextExecutionTime,
                'HH:mm'
              ),
              startdate: self.$options.filters.toDateFormat(
                nextExecutionTime,
                dateFormat
              ),
              pmfrequency: pm.frequency,
              frequencyText: self.frequencyText[pm.frequency],
              resourceview: self.resourceview,
              job: job,
              constraint: {
                start: self.$options.filters.toDateFormat(
                  new Date(),
                  'YYYY-MM-DD'
                ),
                end: self.$options.filters.addDays(new Date(), 61, true),
              },
            }
            self.setPMEventOptions(event)
            self.events.push(event)
          }
        } else if (response.data.workOrders) {
          self.assets = []
          self.spaces = []
          for (let x in response.data.workOrders) {
            let wo = response.data.workOrders[x]

            if (wo.resource) {
              if (wo.resource.resourceType === 2) {
                self.assets.push(wo.resource)
              } else {
                self.spaces.push(wo.resource)
              }
            }

            let colorCode
            let color
            let type = self.typeConfig[self.calendarType].wokey
              ? self.typeConfig[self.calendarType].wokey
              : self.calendarType
            let colorTypeValue =
              (wo[type] && typeof wo[type] === 'object'
                ? wo[type].id
                : wo[type]) || -1
            colorCode = self.currentColorTypes[colorTypeValue].colorcode
            color = self.eventColors[colorCode - 1]

            let start, end
            if (self.calType === 'est') {
              start = self.$options.filters.toDateFormat(
                wo.estimatedStart,
                dateFormat
              )
              end = self.$options.filters.toDateFormat(
                wo.estimatedEnd,
                dateFormat
              )
            } else {
              start = self.$options.filters.toDateFormat(wo.dueDate, dateFormat)
              end = self.$options.filters.toDateFormat(
                wo.dueDate + 30 * 60 * 1000,
                dateFormat
              )
            }

            self.events.push({
              key: wo.id,
              title: wo.subject,
              start: start,
              end: end,
              starttime: self.$options.filters.toDateFormat(
                wo.dueDate,
                'HH:mm'
              ),
              startdate: self.$options.filters.toDateFormat(
                wo.dueDate,
                dateFormat
              ),
              asset: wo.resource ? wo.resource.id : -1,
              space: wo.resource ? wo.resource.id : -1,
              team: wo.assignmentGroup ? wo.assignmentGroup.id : -1,
              staff: wo.assignedTo ? wo.assignedTo.id : -1,
              resourceview: self.resourceview,
              editable: true,
              colortype: colorCode,
              color: color,
              constraint: {
                start: self.$options.filters.toDateFormat(
                  new Date(),
                  'YYYY-MM-DD'
                ),
                end: self.$options.filters.addDays(new Date(), 61, true),
              },
            })
            self.eventList[wo.id] = wo
          }
        }
        if (self.isPlanned) {
          if (self.currentView === 'planned') {
            let multi = self.loadGroupedEvents(response)
            if (multi && multi.length) {
              self.events.push(...multi)
            }
          }
        }
        self.sortAndSetResources()
        self.isEventLoading = false
      })
    },
    sortAndSetResources() {
      this.removeResources()
      let resources = []
      this.events.forEach(event => {
        event.resourceId = event[this.selectedResource]
        resources.push(event.resourceId)
        if (this.selectedResource === 'team') {
          if (event.resourceId === -1 && event.staff > 0) {
            event.resourceId = -2
          }
        } else if (this.selectedResource === 'staff') {
          if (event.resourceId === -1 && event.team > 0) {
            event.resourceId = -2
          }
        }
        event.resourceEditable =
          event.editable &&
          (this.selectedResource === 'staff' ||
            this.selectedResource === 'team')
      })
      if (!this.resourceview) {
        return
      }
      this.resourceList.sort((a, b) => {
        if (a.id === -1 || b.id === -1) {
          return -1
        }
        let event1 = this.events.find(event => event.resourceId === a.id)
        let event2 = this.events.find(event => event.resourceId === b.id)
        if (this.isPlanned) {
          return event1 && event2
            ? parseInt(event1.pmfrequency) - parseInt(event2.pmfrequency)
            : event1 || !event2
            ? -1
            : 1
        } else {
          return event1 && event2 ? 0 : event1 || !event2 ? -1 : 1
        }
      })

      let len = this.resourceList.length <= 100 ? this.resourceList.length : 100 // temp
      for (let i = 0; i < len; i++) {
        let option = this.resourceList[i]
        $('#calenderview').fullCalendar('addResource', option)
      }

      setTimeout(() => {
        this.$refs.calendar.$emit('reload-events')
        this.isEventLoading = false
      }, 100)
    },
    setPMEventOptions(event) {
      if (event.job.status === 3) {
        event.colortype = this.currentColorTypes.completed.colorcode
        event.color = this.currentColorTypes.completed.color
        event.editable = false
      } else if (event.job.status === 4) {
        event.colortype = this.currentColorTypes.inactive.colorcode
        event.color = this.currentColorTypes.inactive.color
        event.editable = false
      } else {
        let pm = this.eventList[event.pmId]
        let template = event.job.template || pm.woTemplate

        let colorTypeValue =
          pm[this.calendarType] || template[this.calendarType] || -1

        if (this.calendarType == 'frequency' && event.frequency) {
          //temp fix
          colorTypeValue = event.frequency
        }

        event.colortype = this.currentColorTypes[colorTypeValue].colorcode
        event.color = this.eventColors[event.colortype - 1]
        event.editable = !event.job.projected
      }
    },
    setPMV2EventOptions(event) {
      let { eventColors, currentColorTypes } = this
      let { frequency } = event || {}
      let colortype = this.$getProperty(
        currentColorTypes,
        `${frequency}.colorcode`,
        -1
      )
      let color = this.$getProperty(eventColors, `${colortype - 1}`, null)

      event = { ...event, colortype, color, editable: true }
      return event
    },
    updatePmJob(command, eventDetails, status) {
      let url, params
      if (command === 'current') {
        url = '/workorder/updatePreventiveMaintenanceJob'
        params = { id: [eventDetails.jobId], pmJob: { status: status } }
      } else {
        url = '/workorder/changePreventiveMaintenanceStatus'
        params = {
          id: [eventDetails.pmId],
          preventivemaintenance: { status: false },
        }
      }
      this.$http.post(url, params).then(response => {
        if (typeof response.data === 'object') {
          this.$message.success('Status Updated Successfully')
          if (command === 'current') {
            let event = this.events.find(
              event => event.jobId === eventDetails.jobId
            )
            event.job.status = response.data.pmJob.status
            this.setPMEventOptions(event)
          } else {
            this.events = this.events.filter(
              event => event.pmId !== eventDetails.pmId
            )
          }
          this.closeDialog()
        } else {
          this.$message.error('Updation Failed')
        }
      })
    },
    opensummary(event) {
      let { id } = event || {}
      if (isWebTabsEnabled()) {
        let moduleName = 'workorder'
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

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
        this.$router.push({ path: '/app/wo/orders/summary/' + id })
      }
    },
    prettyScheduleObject(scheduleObj) {
      if (!scheduleObj) {
        return '---'
      }

      let prettyStr = ''
      if (scheduleObj.frequencyTypeEnum === 'DAILY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'day'
            : scheduleObj.frequency + ' days')
      } else if (scheduleObj.frequencyTypeEnum === 'WEEKLY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'week'
            : scheduleObj.frequency + ' weeks')
        let days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        prettyStr += ' on '
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          prettyStr += days[i + 1]
        }
      } else if (scheduleObj.frequencyTypeEnum === 'MONTHLY_DAY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'month'
            : scheduleObj.frequency + ' months')
        prettyStr += ' on '
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          prettyStr += scheduleObj.values[i] + this.nth(scheduleObj.values[i])
        }
      } else if (scheduleObj.frequencyTypeEnum === 'MONTHLY_WEEK') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'month'
            : scheduleObj.frequency + ' months')
        prettyStr += ' on ('
        let weeks = ['First', 'Second', 'Third', 'Fourth', 'Last']
        let days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        prettyStr += weeks[scheduleObj.weekFrequency - 1] + ' week'
        prettyStr += ' - '
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          prettyStr += days[i + 1]
        }
        prettyStr += ')'
      } else if (scheduleObj.frequencyTypeEnum === 'YEARLY') {
        prettyStr =
          'Every ' +
          (scheduleObj.frequency === 1
            ? 'year'
            : scheduleObj.frequency + ' years')
        prettyStr += ' on '
        let months = [
          'Jan',
          'Feb',
          'Mar',
          'Apr',
          'May',
          'Jun',
          'Jul',
          'Aug',
          'Sep',
          'Oct',
          'Nov',
          'Dec',
        ]
        for (let i = 0; i < scheduleObj.values.length; i++) {
          if (i !== 0) {
            prettyStr += ','
          }
          let j = scheduleObj.values[i]
          prettyStr += months[j - 1]
        }
      }
      return prettyStr
    },
    nth(d) {
      if (d > 3 && d < 21) return 'th'
      switch (d % 10) {
        case 1:
          return 'st'
        case 2:
          return 'nd'
        case 3:
          return 'rd'
        default:
          return 'th'
      }
    },
    setFilterTypeOptions() {
      this.typeConfig.typeId.options = this.$helpers.cloneObject(
        this.tickettype
      )
      this.typeConfig.statusId.options = this.$helpers.cloneObject(
        this.ticketstatus
      )
      this.typeConfig.frequency.options = Object.assign(
        {},
        this.$constants.FACILIO_FREQUENCY,
        { 0: 'Once' }
      )
      if (
        this.calendarType !== 'categoryId' &&
        this.calendarType !== 'priorityId'
      ) {
        this.setCurrentColorType()
      }
      this.loadPickList('ticketcategory', 'categoryId')
      this.loadPickList('ticketpriority', 'priorityId')
    },
    setCurrentColorType() {
      this.currentColorTypes = {
        '-1': { colorcode: 10, active: true },
        completed: { colorcode: 'complete', color: '#d0cece' },
        inactive: { colorcode: 'inactive', color: '#ededed' },
      }
      Object.keys(this.typeConfig[this.calendarType].options)
        .filter(key => parseInt(key) !== -1)
        .forEach((key, index) => {
          this.currentColorTypes[key] = {
            colorcode: (index % this.eventColors.length) + 1,
            active: true,
          }
        })
      if (
        this.typeConfig[this.calendarType].noneLabel &&
        !this.typeConfig[this.calendarType].options[-1]
      ) {
        this.typeConfig[this.calendarType].options[-1] = this.typeConfig[
          this.calendarType
        ].noneLabel
      }
      if (this.filters != null && Object.keys(this.filters).length) {
        this.resetCircle()
      }
    },
    resetCircle() {
      if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
        return this.resetNewCircle()
      } else {
        return this.resetOldCircle()
      }
    },
    resetOldCircle() {
      let type =
        !this.isPlanned && this.typeConfig[this.calendarType].wokey
          ? this.typeConfig[this.calendarType].wokey
          : this.calendarType
      let isColorFiltered =
        this.filters && this.filters[type] && this.filters[type].value
      for (let key in this.currentColorTypes) {
        if (this.currentColorTypes.hasOwnProperty(key)) {
          let data = this.currentColorTypes[key]
          if (isColorFiltered) {
            data.active =
              parseInt(key) !== -1
                ? this.filters[type].value.find(
                    val => parseInt(val) === parseInt(key)
                  )
                : !this.$route.query.hasOwnProperty('isnone') ||
                  this.$route.query.isnone
          } else {
            data.active = true
          }
        }
      }
    },
    resetNewCircle() {
      let type = this.typeConfig[this.calendarType].wokey
        ? this.typeConfig[this.calendarType].wokey
        : this.calendarType
      let isColorFiltered =
        this.filters && this.filters[type] && this.filters[type].value
      for (let key in this.currentColorTypes) {
        if (this.currentColorTypes.hasOwnProperty(key)) {
          let data = this.currentColorTypes[key]
          if (isColorFiltered) {
            data.active =
              parseInt(key) !== -1
                ? this.filters[type].value.find(
                    val => parseInt(val) === parseInt(key)
                  )
                : !this.$route.query.hasOwnProperty('isnone') ||
                  this.$route.query.isnone
          } else {
            data.active = true
          }
        }
      }
    },
    updateCalendarColor() {
      this.$store.dispatch('updateCalendarColor', this.calendarType)
    },
    filterCalendar(value) {
      //disble click action
      // if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
      //   return this.filterNewCalendar(value)
      // } else {
      //   return this.filterOldCalendar(value)
      // }
    },
    filterNewCalendar(value) {
      this.currentColorTypes[value].active = !this.currentColorTypes[value]
        .active

      let config = this.typeConfig[this.calendarType]
      if (!config) {
        return
      }

      let values = []
      for (let key in this.typeConfig[this.calendarType].options) {
        if (
          this.typeConfig[this.calendarType].options.hasOwnProperty(key) &&
          parseInt(key) !== -1
        ) {
          if (this.currentColorTypes[key].active) {
            values.push(key + '')
          }
        }
      }
      let count =
        this.typeConfig[this.calendarType].options[-1] &&
        this.currentColorTypes[-1].active
          ? this.optionsCount - 1
          : this.optionsCount
      if (values.length && values.length !== count) {
        let key = this.typeConfig[this.calendarType].wokey
          ? this.typeConfig[this.calendarType].wokey
          : this.calendarType
        let filter = {
          [key]: { operatorId: config.operatorId || 36, value: values },
        }
        let query = { search: JSON.stringify(filter), open: false }
        if (this.typeConfig[this.calendarType].options[-1]) {
          query.isnone = this.currentColorTypes[-1].active
        }
        this.$router.replace({ query: query })
      } else {
        this.$router.replace({ query: {} })
      }
    },
    filterOldCalendar(value) {
      this.currentColorTypes[value].active = !this.currentColorTypes[value]
        .active

      let config = this.typeConfig[this.calendarType]
      if (!config) {
        return
      }

      let values = []
      for (let key in this.typeConfig[this.calendarType].options) {
        if (
          this.typeConfig[this.calendarType].options.hasOwnProperty(key) &&
          parseInt(key) !== -1
        ) {
          if (this.currentColorTypes[key].active) {
            values.push(key + '')
          }
        }
      }
      let count =
        this.typeConfig[this.calendarType].options[-1] &&
        this.currentColorTypes[-1].active
          ? this.optionsCount - 1
          : this.optionsCount
      if (values.length && values.length !== count) {
        let key =
          !this.isPlanned && this.typeConfig[this.calendarType].wokey
            ? this.typeConfig[this.calendarType].wokey
            : this.calendarType
        let filter = {
          [key]: { operatorId: config.operatorId || 36, value: values },
        }
        let query = { search: JSON.stringify(filter), open: false }
        if (this.typeConfig[this.calendarType].options[-1]) {
          query.isnone = this.currentColorTypes[-1].active
        }
        this.$router.replace({ query: query })
      } else {
        this.$router.replace({ query: {} })
      }
    },
    async loadPickList(moduleName, fieldName) {
      this.picklistOptions = {}
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.typeConfig[fieldName].options = options
        if (this.calendarType === fieldName) {
          this.setCurrentColorType()
        }
      }
    },
    registerEvents() {
      let self = this
      $('#calenderview .fc-button-group .fc-prev-button').click(function() {
        self.events = []
        self.loadEvents()
        return false
      })
      $('#calenderview .fc-button-group .fc-next-button').click(function() {
        self.events = []
        self.loadEvents()
        return false
      })
      $('#calenderview .fc-today-button').click(function() {
        self.events = []
        self.loadEvents()
        return false
      })
      $('#calenderview .fc-button-group .fc-month-button').click(function() {
        self.events = []
        self.loadEvents()
        return false
      })
      $('#calenderview .fc-button-group .fc-agendaWeek-button').click(
        function() {
          self.events = []
          self.loadEvents()
          return false
        }
      )
      $('#calenderview .fc-button-group .fc-agendaDay-button').click(
        function() {
          self.events = []
          self.loadEvents()
          return false
        }
      )
      $('#calenderview .fc-button-group .fc-basicWeek-button').click(
        function() {
          self.events = []
          self.loadEvents()
          return false
        }
      )
      $('#calenderview .fc-button-group .fc-basicDay-button').click(function() {
        self.events = []
        self.loadEvents()
        return false
      })
      $('#calenderview .fc-button-group .fc-timelineYear-button').click(
        function() {
          self.events = []
          self.loadEvents()
          self.registerPopupEvents()
          return false
        }
      )
      $('#calenderview .fc-button-group .fc-timelineMonth-button').click(
        function() {
          self.events = []
          self.loadEvents()
          self.registerPopupEvents()
          return false
        }
      )
      $('#calenderview .fc-button-group .fc-timelineWeek-button').click(
        function() {
          self.events = []
          self.loadEvents()
          self.registerPopupEvents()
          return false
        }
      )
      $('#calenderview .fc-button-group .fc-timelineDay-button').click(
        function() {
          self.events = []
          self.loadEvents()
          self.registerPopupEvents()
          return false
        }
      )
      self.registerPopupEvents()
    },
    registerPopupEvents() {
      $('#dropdownid').click(function() {
        $('#calendarpage .popup1').addClass('showpopup1')
        $('#calendarpage .popup2').removeClass('showpopup2')
        return false
      })
      $('#filterid').click(function() {
        $('#calendarpage .popup2').addClass('showpopup2')
        $('#calendarpage .popup1').removeClass('showpopup1')
        return false
      })
      $('html').click(function(event) {
        if (
          $(event.target).closest('#calendarpage .popup2.showpopup2').length ===
          0
        ) {
          $('#calendarpage .popup1').removeClass('showpopup1')
          $('#calendarpage .popup2').removeClass('showpopup2')
        }
      })
      $('#calendarpage').on('scroll', this.handleScroll)
    },
    hide() {
      if (this.showPopover && !this.dontHide) {
        this.showPopover = false
        $("a[class*='eventselected']").removeClass(function(index, css) {
          return (css.match(/(^|\s)eventselected\S+/g) || []).join(' ')
        })
      }
      this.dontHide = false
    },
    closeDialog() {
      this.showPopover = false
      $("a[class*='eventselected']").removeClass(function(index, css) {
        return (css.match(/(^|\s)eventselected\S+/g) || []).join(' ')
      })
    },
    closeDropdown() {
      this.showAllCircles = false
    },
    getPmCreationTypeEnum(pmId) {
      let pm = this.$getProperty(this, `eventList.${pmId}`)
      if (!isEmpty(pm)) {
        return pm.pmCreationTypeEnum
      }
      return null
    },
  },
}
</script>
<style>
@import '~fullcalendar/dist/fullcalendar.css';
@import '~fullcalendar-scheduler/dist/scheduler.min.css';
@media (min-width: 1200px) {
  .layout-padding {
    padding: 0px;
    margin: auto;
  }
}
.calenderview .fc-right {
  padding-right: 200px;
}
.typestyle {
  float: left;
  font-size: 13px;
  letter-spacing: 0.1px;
  color: #666666;
  padding-right: 10px;
}
.colorcardtext {
  float: left;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.1px;
}
.colorcard {
  position: absolute;
  z-index: 1000;
  background-color: #fff;
  padding: 12px 6px;
  border-radius: 5px;
  box-shadow: 0 4px 45px 0 rgba(194, 193, 193, 0.5);
  top: 55px;
  width: 140px;
}
.colorcard1 {
  right: 90px;
}
.colorcard2 {
  right: 55px;
}
.colorcard3 {
  right: 20px;
}
.colorcard4 {
  right: -13px;
}
.colorcard5 {
  right: 2px;
}
.colorcard10 {
  right: -13px;
}

.colormore,
.circlemore .colorcard5 {
  right: 105% !important;
  top: -10%;
  width: fit-content;
}

.popup1.showpopup1 {
  display: block !important;
  position: absolute;
  z-index: 1000;
  top: 122px;
  left: 86px;
}
.showpopup2 {
  display: block !important;
  position: absolute;
  z-index: 1000;
  left: 106px;
  top: 128px;
}
.dropdown {
  color: #666;
  font-size: 13px;
  padding: 12px;
  cursor: pointer;
}
.dropdown:hover {
  background: rgba(245, 245, 245, 0.5);
}
.popup1 {
  display: none;
  background-color: #fff;
  border-radius: 5px;
  width: 100px;
  box-shadow: 0 4px 45px 0 rgba(194, 193, 193, 0.5);
  padding: 10px 0;
}
.popup2 {
  display: none;
  background-color: #fff;
  border-radius: 5px;
  width: 175px;
  box-shadow: 0 4px 45px 0 rgba(194, 193, 193, 0.5);
}
.popup2 .filtertitle::after {
  content: '';
  position: absolute;
  bottom: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 7px;
  border-style: solid;
  border-color: #fff transparent transparent transparent;
  transform: rotate(180deg);
}
.filtertitle {
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.4px;
  text-align: left;
  padding-top: 10px;
  color: #6b7e91;
  text-overflow: ellipsis;
  white-space: nowrap;
}

#calendarpage .color1,
.colorpopover .color1 {
  color: #ff5633;
}
#calendarpage .color2,
.colorpopover .color2 {
  color: #ffc301;
}
#calendarpage .color3,
.colorpopover .color3 {
  color: #57c787;
}
#calendarpage .color4,
.colorpopover .color4 {
  color: #816bc5;
}
#calendarpage .color5,
.colorpopover .color5 {
  color: #ea4985;
}
#calendarpage .color6,
.colorpopover .color6 {
  color: #17c1b5;
}
#calendarpage .color7,
.colorpopover .color7 {
  color: #fe8e19;
}
#calendarpage .color8,
.colorpopover .color8 {
  color: #eddd52;
}
#calendarpage .color9,
.colorpopover .color9 {
  color: #add45f;
}
#calendarpage .color10,
.colorpopover .color10 {
  color: #2b7a9b;
}
#calendarpage .colorcomplete,
.colorpopover .colorcomplete {
  color: #a7a7a7;
}
#calendarpage .colorinactive,
.colorpopover .colorinactive {
  color: #a7a7a7;
}

#calendarpage .bgcolor1,
#calendarpage .eventselected1 {
  background-color: #ff5633 !important;
}
#calendarpage .bgcolor2,
#calendarpage .eventselected2 {
  background-color: #ffc301 !important;
}
#calendarpage .bgcolor3,
#calendarpage .eventselected3 {
  background-color: #57c787 !important;
}
#calendarpage .bgcolor4,
#calendarpage .eventselected4 {
  background-color: #816bc5 !important;
}
#calendarpage .bgcolor5,
#calendarpage .eventselected5 {
  background-color: #ea4985 !important;
}
#calendarpage .bgcolor6,
#calendarpage .eventselected6 {
  background-color: #17c1b5 !important;
}
#calendarpage .bgcolor7,
#calendarpage .eventselected7 {
  background-color: #fe8e19 !important;
}
#calendarpage .bgcolor8,
#calendarpage .eventselected8 {
  background-color: #eddd52 !important;
}
#calendarpage .bgcolor9,
#calendarpage .eventselected9 {
  background-color: #add45f !important;
}
#calendarpage .bgcolor10,
#calendarpage .eventselected10 {
  background-color: #2b7a9b !important;
}
#calendarpage .bgcolorcomplete,
#calendarpage .eventselectedcomplete {
  background-color: #a7a7a7 !important;
}
#calendarpage .bgcolorinactive,
#calendarpage .eventselectedinactive {
  background-color: #a7a7a7 !important;
}

#calendarpage .eventselected0.fc-event,
#calendarpage .eventselected00.fc-event {
  color: white !important;
}
#calendarpage .eventselected0 .fc-content {
  border-left: 4px solid transparent !important;
}

.fc-content.pm-year {
  text-align: center;
}

#calendarpage .circle-inactive {
  width: 25px;
  height: 25px;
  border-radius: 50%;
  margin: 2px 1px 0;
}

#calendarpage .circle-inactive.color1 {
  background-color: #ffccc2;
}
#calendarpage .circle-inactive.color2 {
  background-color: #ffedb3;
}
#calendarpage .circle-inactive.color3 {
  background-color: #cdefdb;
}
#calendarpage .circle-inactive.color4 {
  background-color: #d9d3ee;
}
#calendarpage .circle-inactive.color5 {
  background-color: #f9c9db;
}
#calendarpage .circle-inactive.color6 {
  background-color: #b9ede9;
}
#calendarpage .circle-inactive.color7 {
  background-color: #ffddba;
}
#calendarpage .circle-inactive.color8 {
  background-color: #faf5cb;
}
#calendarpage .circle-inactive.color9 {
  background-color: #e7f3cf;
}
#calendarpage .circle-inactive.color10 {
  background-color: #bfd7e1;
}

.colordropdown {
  background-color: #e37f85;
  text-align: center;
  line-height: 26px;
  color: #ffffff;
  transition: 0.2s all ease;
  cursor: pointer;
  margin: 1px 9px 2px 0 !important;
  z-index: 5;
}

.colordropdown.active {
  transform: rotate(180deg);
}
.calenderview .fc-timeline .fc-divider {
  width: 0px !important;
}
.calenderview .fc-timeline .fc-widget-header {
  height: 40px !important;
  background-color: #f8f9fa;
}
.calenderview .fc-unthemed th,
.fc-unthemed td,
.fc-unthemed thead,
.fc-unthemed tbody,
.fc-unthemed .fc-divider,
.fc-unthemed .fc-row,
.fc-unthemed .fc-content,
.fc-unthemed .fc-popover,
.fc-unthemed .fc-list-view,
.fc-unthemed .fc-list-heading td {
  border-color: #eaecf0 !important;
}
.calenderview .fc-timeline a.fc-cell-text {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}
.calenderview .fc-timeline .fc-widget-header span.fc-cell-text {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  padding-left: 15px;
}
.calenderview .fc-timeline .fc-widget-content span.fc-cell-text {
  font-size: 14px;
  color: #333;
  padding-left: 15px;
}
.calenderview .fc-widget-content {
  max-height: 230px;
  overflow-y: scroll !important;
}
.calenderview
  .fc-timeline
  a.fc-timeline-event.fc-h-event.fc-event.fc-start.fc-end.fc-draggable.fc-resizable {
  margin: 2px;
}
.calenderview
  .fc-timelineWeek-view.fc-timeline
  a.fc-timeline-event.fc-h-event.fc-event.fc-start.fc-end.fc-draggable.fc-resizable {
  padding: 0px;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer,
.calenderview .fc-view.fc-agenda-view .eventcontainer {
  padding: 5px;
  margin-bottom: 2px;
  margin-right: 5px;
}
.calenderview .fc-view.fc-timelineWeek-view.fc-timeline .eventcontainer {
  padding: 5px;
  margin-right: 5px;
}
.calenderview .fc-view.fc-timelineMonth-view.fc-timeline .eventcontainer,
.calenderview .fc-view.fc-timelineYear-view.fc-timeline .eventcontainer {
  font-size: 11px;
  padding-left: 2px;
}
.calenderview
  .fc-view.fc-timelineMonth-view.fc-timeline
  .eventcontainer
  .fc-time,
.calenderview
  .fc-view.fc-timelineYear-view.fc-timeline
  .eventcontainer
  .fc-time {
  font-weight: 500;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor1,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor1,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor1 {
  border-left: 4px solid #ff5633 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor2,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor2,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor2 {
  border-left: 4px solid #ffc301 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor3,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor3,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor3 {
  border-left: 4px solid #57c787 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor4,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor4,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor4 {
  border-left: 4px solid #816bc5 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor5,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor5,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor5 {
  border-left: 4px solid #ea4985 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor6,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor6,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor6 {
  border-left: 4px solid #17c1b5 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor7,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor7,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor7 {
  border-left: 4px solid #fe8e19 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor8,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor8,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor8 {
  border-left: 4px solid #eddd52 !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor9,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor9,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor9 {
  border-left: 4px solid #add45f !important;
}
.calenderview .fc-view.fc-month-view.fc-basic-view .eventcontainer.bdcolor10,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolor10,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolor10 {
  border-left: 4px solid #2b7a9b !important;
}
.calenderview
  .fc-view.fc-month-view.fc-basic-view
  .eventcontainer.bdcolorcomplete,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolorcomplete,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolorcomplete {
  border-left: 4px solid #a7a7a7 !important;
}
.calenderview
  .fc-view.fc-month-view.fc-basic-view
  .eventcontainer.bdcolorinactive,
.calenderview
  .fc-view.fc-timelineWeek-view.fc-timeline
  .eventcontainer.bdcolorinactive,
.calenderview .fc-view.fc-agenda-view .eventcontainer.bdcolorinactive {
  border-left: 4px solid #a7a7a7 !important;
}
.fc-unthemed td.fc-today {
  background-color: #fff8fb;
}

.colorContainer {
  padding-right: 7px;
  /* cursor: pointer; */
}
.colorContainer.circlemore {
  position: relative;
  left: 80%;
  padding-top: 2px;
  visibility: hidden;
  transition: 0.2s all ease;
  top: -15px;
}
.colorContainer.circlemore.active {
  top: 0;
  visibility: visible;
}

.colorContainer:hover .colorTitle {
  display: block;
}
.colorContainer .colorTitle {
  display: none;
}
.colorContainer .colorTitle ::after {
  content: '';
  position: absolute;
  bottom: 100%;
  margin-right: -5px;
  border-width: 7px;
  border-style: solid;
  border-color: #fff transparent transparent transparent;
  transform: rotate(180deg);
  left: 40%;
}
.colorContainer.colorTitlePosition5 .colorTitle ::after {
  left: 76%;
}
.colorContainer .colormore.colorTitle ::after {
  bottom: 33%;
  margin-right: 0px;
  transform: rotate(270deg);
  left: 100%;
}

.headertext {
  font-size: 14px;
  font-weight: normal;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: #25243e;
}
.colortext {
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.3px;
  color: #50516c;
}
.calenderview {
  margin: 18px;
  background-color: #fff;
  position: relative;
}
.calenderview .fc-prev-button {
  display: inline;
}
.calenderview span.fc-icon.fc-icon-right-single-arrow {
  color: var(--fc-theme-color);
  font-size: 16px;
}
.calenderview button.fc-next-button.fc-button.fc-state-default {
  padding-left: 0px;
}
.calenderview button.fc-prev-button.fc-button.fc-state-default {
  padding-right: 0px;
}
.calenderview button.fc-prev-button.fc-button.fc-state-default.fc-corner-left {
  font-size: 100%;
  border: none;
  background: white;
  border-radius: 0;
  outline: none;
  box-shadow: none;
  padding-left: 0px;
  padding-right: 0px;
}
.calenderview span.fc-icon.fc-icon-left-single-arrow {
  color: var(--fc-theme-color);
  font-size: 16px;
}
.calenderview .fc-icon-right-single-arrow:after,
.calenderview .fc-icon-left-single-arrow:after {
  font-weight: normal;
}
.pT20 {
  padding-top: 20px;
}
.calenderview button {
  margin-top: 21px;
  border: none;
  background: white;
  border-radius: 0;
  outline: none;
  box-shadow: none;
}
.calenderview .fc-button-group {
  padding-top: 25px;
}
.calenderview .h2 {
  font-size: 18px;
  letter-spacing: 0.3px;
  text-align: center;
  color: #25243e;
}
.calenderview .fc-view-container {
  margin: 20px;
  overflow: scroll;
}
.calenderview .fc-more-popover .fc-event-container {
  padding: 10px;
  border-top: solid 1px #f2f2f3;
}
.calenderview .fc-time-grid-event.fc-short .fc-title {
  font-size: 12px;
  padding: 0;
}
.calenderview .fc-day-header {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.2px;
  text-align: center;
  color: #555555;
  padding-top: 15px;
  padding-bottom: 15px;
  border: none;
}
.calenderview .fc-center {
  display: inline-block;
  font-size: 18px;
  letter-spacing: 0.3px;
  color: #25243e;
}
.calenderview .fc-state-default.fc-state-active {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.3px;
  color: var(--fc-theme-color);
  padding-left: 12px;
  padding-right: 12px;
}
.calenderview .fc-state-default {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.6px;
  color: #aeaeae;
  text-transform: uppercase;
  padding-left: 12px;
  padding-right: 12px;
}
.calenderview .fc-toolbar h2 {
  font-size: 18px;
  letter-spacing: 0.2px;
  text-align: center;
  color: #000;
  font-weight: 500;
  padding-top: 26px;
}
.calenderview .fc-toolbar.fc-header-toolbar {
  margin: 15px;
}
.calenderview .fc-resourceview-button {
  text-transform: none;
  font-size: 13px;
  color: var(--fc-theme-color);
  font-weight: normal;
  border: 1px solid var(--fc-theme-color);
  border-radius: 17.5px;
}
.calenderview .fc-event {
  font-size: 12px;
  letter-spacing: 0.3px;
  color: #333333;
}
.calenderview .fc-unthemed .fc-popover {
  background-color: #fff;
  box-shadow: 0 4px 18px 0 rgba(194, 193, 193, 0.5);
}
.calenderview .fc-popover .fc-header {
  padding: 15px 4px !important;
}
.calenderview .fc-icon-x:after {
  content: '';
}
.calenderview .fc-day-number {
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.2px;
  color: #25243e;
}
.calenderview .fc-unthemed th,
.fc-unthemed td,
.fc-unthemed thead,
.fc-unthemed tbody,
.fc-unthemed .fc-divider,
.fc-unthemed .fc-row,
.fc-unthemed .fc-content,
.fc-unthemed .fc-popover,
.fc-unthemed .fc-list-heading td {
  border-color: #eaedf1;
}
.calenderview div.fc-widget-header {
  background: #fff !important;
  padding: 0px !important;
}
.calenderview .fc-time-grid-event .fc-time {
  font-size: 0.85em;
  white-space: nowrap;
  font-weight: bold;
}
.position {
  box-shadow: 0 4px 18px 0 rgba(194, 193, 193, 0.5);
}
.eventstyle {
  font-size: 17px;
  letter-spacing: 0.4px;
  text-align: left;
  color: #fff;
  font-weight: 500;
  max-width: 330px;
  margin-top: 5px;
}
.subheader {
  font-size: 13px;
  letter-spacing: 0.1px;
  color: #666666;
  padding-right: 15px;
}
.moredetails {
  font-size: 13px;
  letter-spacing: 0.3px;
  color: var(--fc-theme-color);
}
.intext {
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.1px;
  color: #333333;
}
.datestyle {
  font-size: 12px;
  letter-spacing: 0.4px;
  color: #fff;
  padding-top: 5px;
  padding-bottom: 7px;
}
#calenderview .fc-day-grid-event {
  margin: 0px 2px 0;
  padding: 0px;
}
#calenderview .fc-event {
  border: none;
  border-radius: 0;
}
#calenderview .fc-popover .fc-widget-header .fc-title {
  /* float: right; */
  padding-right: 6px;
  white-space: normal;
}

.more-overflow {
  height: 4px;
  width: 4px;
  background-color: #000000;
  opacity: 0.3;
  border-radius: 50%;
  display: block;
  margin-bottom: 2px;
}

.roundbutton {
  border-color: #fff;
  color: #fff;
  font-size: 11px;
  font-weight: bold;
  padding: 6px 15px !important;
  padding-bottom: 15px;
  margin-right: 10px;
  background: transparent;
}
.roundbutton.el-button:focus,
.roundbutton.el-button:hover {
  background-color: transparent !important;
  color: #fff;
}

.popup2 .el-input__inner {
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
