<template>
  <div>
    <div
      class="facilio-resource-planner"
      :class="[{ 'full-screen': isFullScreen }]"
      ref="resourcePlanner"
    >
      <div class="fc-rp-top-bar white-bg inline-flex">
        <div class="inline-flex" style="width: 41%;">
          <slot name="plannerLeftTop"></slot>
          <el-button
            @click="todayClicked"
            class="mL10 fc-btn-grey-border40 pL20 pR20"
            >Today</el-button
          >
          <div class="green-txt-13 pT15 pL10" v-if="saving">Saving...</div>
        </div>
        <div
          class="fc-text-pink bold flex-middle facilio-resource-date-picker-container"
          style="width: 20%;"
        >
          <new-date-picker
            v-if="showPicker"
            :dateObj="pickerObj"
            @date="navigateFromPicker"
            :zone="timeZone"
            :tabs="datePickerTabs()"
            class="facilio-resource-date-picker"
          ></new-date-picker>
        </div>

        <!-- Don't use visiblity.sync , created hook doesn't fire in that case -->
        <el-dialog
          @close="showSettings = false"
          title="Planner Settings"
          v-if="showSettings"
          visible
          width="30%"
          class="fc-dialog-center-container fc-dialog-center-body-p0"
          append-to-body
        >
          <div class="height450 pL20 pR20">
            <!-- close from cancel button -->
            <settings
              @close="showSettings = false"
              :settings="plannerSettings.viewSettings"
              :selectedView="this.currentView"
              :views="views"
              @saveSettings="handleSettingsChange"
            ></settings>
          </div>
        </el-dialog>

        <!-- TO do move to seperate component -->
        <!-- all settings are saved only on hitting save, hence using a temp variable as v model ,on save persist -->
        <el-dialog
          @close="showMoveSettings = false"
          title="Default Move action"
          v-if="showMoveSettings"
          visible
          width="30%"
          class="fc-dialog-center-container fc-dialog-center-body-p0"
          append-to-body
        >
          <div class="height150 pL20 pR20">
            <div class="fc-input-label-txt pB10 mT20">
              On workorder drag and drop ,move
            </div>
            <el-select
              class="fc-input-full-border2 width100"
              v-model="moveTypeSelect"
            >
              <el-option
                v-for="(option, index) in moveTypeOptions"
                :key="index"
                :value="option"
                :label="$t(`maintenance.planner.move_type.${option}`)"
              >
              </el-option>
            </el-select>
            <div class="modal-dialog-footer">
              <el-button
                @click="showMoveSettings = false"
                class="modal-btn-cancel "
                >Cancel
              </el-button>
              <el-button @click="saveMoveType" class="modal-btn-save "
                >Save
              </el-button>
            </div>
          </div>
        </el-dialog>

        <el-dialog
          @close="showLegendSettings = false"
          title="Legend settings"
          v-if="showLegendSettings"
          visible
          width="30%"
          class="fc-dialog-center-container fc-dialog-center-body-p0"
          append-to-body
        >
          <div class="height150 pL20 pR20">
            <div class="fc-input-label-txt pB10 mT20">Color based on</div>
            <el-select
              class="fc-input-full-border2 width100"
              v-model="legendColorAttribute"
            >
              <el-option
                v-for="(option, index) in legendOptions"
                :key="index"
                :value="option.name"
                :label="option.displayName"
              ></el-option>
            </el-select>
            <div class="modal-dialog-footer">
              <el-button
                @click="showLegendSettings = false"
                class="modal-btn-cancel"
                >Cancel</el-button
              >
              <el-button @click="saveLegendSettings" class="modal-btn-save"
                >Save</el-button
              >
            </div>
          </div>
        </el-dialog>
        <column-settings
          v-if="showColumnSettings"
          @close="showColumnSettings = false"
          :settings="plannerSettings.columnSettings"
          :plannerType="plannerType"
          @save="saveColumnSettings"
        >
        </column-settings>
        <time-metric-settings
          @close="showTimeMetricSettings = false"
          v-if="showTimeMetricSettings"
          @save="saveTimeMetricSettings"
          :settings="plannerSettings.timeMetricSettings"
        >
        </time-metric-settings>

        <el-dialog
          :visible.sync="showDropOptions"
          @closed="handleClose"
          width="30%"
          class="fc-dialog-header-hide"
          append-to-body
        >
          <div class="move-options height150 mL40 mR40">
            <div class="label-txt-black line-height20">
              Do you want to move all subsequent <br />
              WorkOrders?
            </div>

            <el-checkbox class="pT10" v-model="dontAskAgain"
              >Don't ask again</el-checkbox
            >

            <div
              v-if="this.dontAskAgain"
              class="mT10 line-height20 fc-grey2 f12"
            >
              You can change this later in settings
            </div>
            <div class="modal-dialog-footer">
              <el-button
                @click="handleUserDropOption('subsequent')"
                class="modal-btn-save"
                >Yes, Move</el-button
              >
              <el-button
                @click="handleUserDropOption('single')"
                class="modal-btn-cancel"
                >No, Don't move</el-button
              >
            </div>
          </div>
        </el-dialog>

        <f-new-popover
          v-if="showCard"
          @close="handleCardClose"
          id="customPopover"
        >
          <template slot="popoverContent">
            <slot
              name="card"
              :event="openTask"
              :col="gridColumns[openCell.colIndex]"
            >
            </slot>
          </template>
        </f-new-popover>
        <!-- <div data="x-arrow" id="x-arrow"></div> -->

        <!-- Do not change the label , radio group does not provide a value and label , only label -->
        <div>
          <!-- <div class="" style="width: 31%;"> -->
          <el-radio-group
            v-model="currentView"
            @change="handleViewTabClick($event)"
            class="mR20 fc-pm-summary-planner-radio"
            v-show="views.length > 1"
          >
            <el-radio-button
              v-for="(view, index) in views"
              :label="view"
              :key="index"
            >
              <div>{{ viewState[view].displayName }}</div>
            </el-radio-button>
          </el-radio-group>
          <slot name="plannerRightTop"> </slot>
          <el-button-group class="fc-btn-group-white settings-fullscreen">
            <el-dropdown
              @command="handleSettingsDropDown"
              trigger="click"
              v-if="allowUserSettings"
            >
              <el-button class="fc-icon-hover-parent pm-settings-btn">
                <InlineSvg
                  src="svgs/setting"
                  iconClass="icon icon-md"
                ></InlineSvg>
              </el-button>

              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="planner"
                  >Planner Settings</el-dropdown-item
                >
                <el-dropdown-item
                  v-if="this.isPMAssetPlanner"
                  command="moveType"
                  >Default move action</el-dropdown-item
                >
                <el-dropdown-item
                  v-if="this.customizationSettings.columnSettings"
                  command="column"
                  >Column Settings</el-dropdown-item
                >
                <el-dropdown-item
                  v-if="this.customizationSettings.calendarSettings"
                  command="timeMetric"
                  >Calendar Settings</el-dropdown-item
                >
                <el-dropdown-item
                  v-if="this.customizationSettings.legendSettings"
                  command="legend"
                  >Legend Settings
                </el-dropdown-item>
                <slot name="extraSettings"></slot>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button
              @click="isFullScreen = !isFullScreen"
              class="pm-fullscreen-btn fc-icon-hover-parent"
            >
              <!-- <i class="el-icon-top-right"></i> -->
              <InlineSvg
                src="svgs/full-screen"
                iconClass="icon icon-xl vertical-middle mL10"
              ></InlineSvg>
            </el-button>
            <el-button
              class="fc-icon-hover-parent pm-printer-btn"
              v-if="allowDownload"
              @click="printPlanner"
            >
              <i class="el-icon-printer f14 fc-black-color align-top"></i>
            </el-button>
          </el-button-group>
          <!-- <button @click="prevPage"> P</button>
      <button @click="nextPage"> N</button> -->
        </div>
      </div>

      <div class="grid-container white-bg" v-if="!loading">
        <div
          v-if="tableRowData.length > 0"
          class="table-container"
          ref="tableContainer"
        >
          <table
            cellpadding="0"
            :style="[
              currentView == 'WEEK'
                ? { 'table-layout': 'fixed' }
                : { 'table-layout': 'auto' },
            ]"
            :class="[[currentView]]"
          >
            <thead
              :class="[{ 'is-grouped': currentViewState.grouping }]"
              ref="tableHeader"
            >
              <!-- When grouping is off , only only row with resource type and grid columns
            When on two rows ,1st is resource type ,grouping , 2nd is gridline-->
              <tr>
                <!-- Top Left cell -->
                <!-- Two cases ,for oberois planner , multiple cells here -->
                <template v-if="isServerRows">
                  <!-- for each column sticky left=sum of previous col widths -->
                  <th
                    v-for="(resourceHeader, index) in resourceHeaders"
                    :key="resourceHeader.name"
                    :rowspan="resourceTypeRowSpan"
                    class="rp-type-header server-rows"
                    v-bind:style="{ left: index * 151 + 'px' }"
                  >
                    <div class="rp-type-header-cell text-left">
                      {{
                        $t(
                          `maintenance.planner.colHeaders.${plannerType}.${resourceHeader.name}`
                        )
                      }}
                    </div>
                  </th>
                </template>
                <th
                  v-else
                  class="rp-type-header"
                  :rowspan="resourceTypeRowSpan"
                >
                  <div class="rp-type-header-cell text-left">
                    <slot name="resourceSelection"></slot>
                    <!-- {{resourceKey}} -->
                  </div>
                </th>

                <!-- Grouping header row -->
                <template v-if="currentViewState.grouping">
                  <th
                    v-for="(group, groupIndex) in groupColumns"
                    :colspan="group.gridColumns.length"
                    :key="groupIndex"
                    class="rp-group-header rp-group-boundary"
                  >
                    <div
                      class="rp-group-header-cell"
                      v-on:click="groupHeaderClick(group)"
                    >
                      {{ group.label }}
                    </div>
                  </th>
                </template>

                <template v-else>
                  <th
                    v-for="column in gridColumns"
                    :key="column.start"
                    class="rp-grid-header"
                  >
                    <div
                      class="rp-grid-header-cell"
                      v-on:click="colHeaderClick($event, column)"
                      v-on:mousedown="preventTextSelection"
                    >
                      {{ column.label }}
                    </div>
                  </th>
                </template>
              </tr>
              <tr
                v-if="
                  currentViewState.grouping &&
                    currentViewState.showGridLineHeaders
                "
              >
                <!--Grid line header row  -->

                <th
                  v-for="column in gridColumns"
                  :key="column.start"
                  class="rp-grid-header"
                  :class="[{ 'rp-group-boundary': column.isGroupBoundary }]"
                >
                  <div
                    class="rp-grid-header-cell"
                    v-on:click="colHeaderClick($event, column)"
                    v-on:mousedown="preventTextSelection"
                  >
                    {{ column.label }}
                  </div>
                </th>
              </tr>
            </thead>
            <tbody>
              <!-- Seperate rows to show catrgory grouping , they are sticky at top , prev category pushed by next one -->
              <!--  -->
              <template v-for="row in visibleRows">
                <tr
                  v-if="
                    resourceGrouping && groupingBreaks[row.rowIndex] != null
                  "
                  :key="'seperator-' + row.rowIndex"
                  class="rp-seperator-row"
                >
                  <th
                    class="rp-seperator-item bg-seperator-color"
                    :colspan="resourceHeaders.length"
                    :style="{
                      position: 'sticky',
                      left: 0 + 'px',
                      top: headerHeight + 'px',
                      'z-index': 12 + row.rowIndex,
                    }"
                  >
                    <div class="rp-seperator-item-cell p10">
                      <i
                        class="rp-seperator-icon el-icon-arrow-up"
                        v-if="
                          resourceGroupMap[groupingBreaks[row.rowIndex]].visible
                        "
                        @click="
                          toggleResourceGroupCollapse(
                            groupingBreaks[row.rowIndex]
                          )
                        "
                      ></i>
                      <i
                        class="rp-seperator-icon el-icon-arrow-down"
                        v-else
                        @click="
                          toggleResourceGroupCollapse(
                            groupingBreaks[row.rowIndex]
                          )
                        "
                      ></i>
                      {{ groupingBreaks[row.rowIndex] }}
                    </div>
                  </th>
                  <td
                    class="rp-seperator-item-body bg-seperator-color"
                    :colspan="gridColumns.length"
                    :style="{
                      position: 'sticky',
                      top: headerHeight + 'px',
                      'z-index': 10 + row.rowIndex,
                    }"
                  >
                    <div class="rp-seperator-item-body-cell"></div>
                  </td>
                </tr>

                <tr
                  :key="row.rowIndex"
                  v-if="!resourceGrouping || row.visibility"
                >
                  <!-- Column grid -->
                  <template v-if="isServerRows">
                    <!-- resource items have row span and ragged array like structure
                sticky left= resource title length- rowlength *width +item index *width
                -->
                    <th
                      v-for="(resourceTitle,
                      resourceTitleColIndex) in resourceTitles[row.rowIndex]
                        .data"
                      class="rp-resource-item server-rows"
                      :key="resourceTitleColIndex"
                      :rowspan="resourceTitle.rowSpan"
                      v-bind:style="{
                        left:
                          (resourceHeaders.length -
                            resourceTitles[row.rowIndex].data.length) *
                            151 +
                          resourceTitleColIndex * 151 +
                          'px',
                      }"
                    >
                      <div
                        class="rp-resource-item-cell"
                        :style="{
                          position: 'sticky',
                          top: headerHeight + 'px',
                        }"
                      >
                        {{ resourceTitle.name }}
                      </div>
                    </th>
                  </template>

                  <th v-else class="rp-resource-item">
                    <div class="rp-resource-item-cell ">
                      {{ row.resource.title }}
                    </div>
                  </th>
                  <!-- Column grid -->
                  <td
                    v-for="(col, colIndex) in gridColumns"
                    :key="row.data[colIndex].cellID"
                    :class="[{ 'rp-group-boundary': col.isGroupBoundary }]"
                  >
                    <div
                      class="rp-cell"
                      :class="[
                        {
                          'unallowed-date': currentDateCol
                            ? colIndex <= currentDateCol.index
                            : false,
                        },
                        { 'past-date': col.past },
                      ]"
                      @dragover="handleDragOver($event, row.data[colIndex])"
                      @drop="handleDrop($event, row.data[colIndex])"
                      @dragenter="handleDragEnter($event, row.data[colIndex])"
                      @dragleave="handleDragLeave($event, row.data[colIndex])"
                      @click="
                        $emit('cellClicked', {
                          cell: row.data[colIndex],
                          col: gridColumns[colIndex],
                        })
                      "
                    >
                      <div
                        @drag="handleDrag"
                        class="rp-task "
                        :class="[
                          {
                            'task-selected': task.selection,
                            ['wo-legend-color-' + task.colorIndex]:
                              task.colorIndex != null
                                ? task.colorIndex != -1
                                : false,
                          },
                        ]"
                        v-for="(task, index) in row.data[colIndex].tasks"
                        :key="index"
                        @click.stop="
                          handleTaskClick($event, task, row.data[colIndex])
                        "
                        @dragstart="
                          handleDragStart($event, task, row.data[colIndex])
                        "
                        @dragend="handleDragEnd"
                        :style="
                          allowStetch
                            ? { width: task.taskSpan * taskWidth + 'px' }
                            : {}
                        "
                        :draggable="isDragDropAllowed"
                        :data-taskid="task.id"
                      >
                        <!--TO DO : if editable is mentioned for each row use the setting else default based on overal drag allow param -->

                        <slot
                          name="taskContent"
                          :event="task"
                          :displayType="currentViewState['displayType']"
                        >
                        </slot>
                      </div>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>

        <div v-else class="resource-empty">
          <inline-svg
            src="svgs/emptystate/workorder"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            {{ $t('maintenance._workorder.no_wo_title') }}
          </div>
        </div>
        <portal to="pmPagination" v-if="paginate">
          <pagination
            :key="'pm-planner-pagination'"
            :currentPage.sync="pageNumber"
            :total="itemCount"
            :perPage="pageSize"
            class="mR15"
          ></pagination>
        </portal>
      </div>
      <div class="resource-loading" v-else>
        <spinner :show="loading" width="50" height="50"></spinner>
      </div>
    </div>
  </div>
</template>

<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
import CalendarMixin from 'src/pages/workorder/CalendarMixin'
import { PlannerTypes } from 'src/pages/workorder/CalendarConstants'
import Popper from 'popper.js'
import FNewPopover from 'src/components/FNewPopover'
import NewDatePicker from '@/NewDatePicker'
import Settings from 'src/pages/workorder/FResourceSettings'
import timeMetricSettings from './TimeMetricSettings'
import columnSettings from './ColumnSettings'
import isNil from 'lodash/isNil'
import throttle from 'lodash/throttle'
import Cell from './Cell'
import InlineSvg from '@/InlineSvg'
import { Message } from 'element-ui'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'

export default {
  mixins: [CalendarMixin],
  props: {
    plannerType: {
      type: String,
      //PLANNER TYPE ENUM , ASSETPLANNER/STAFF Planner
    },
    allowDownload: {
      type: Boolean,
      default: false,
    },
    isRowDragRestrict: {
      type: Boolean,
      default: true,
    },
    resourceGrouping: {
      type: Boolean,
      default: false,
    },
    paginate: {
      type: Boolean,
      default: false,
    },
    pageSize: {
      type: Number,
      default: 15,
    },
    isServerRows: {
      type: Boolean,
      default: false,
    },
    isPMAssetPlanner: {
      type: Boolean,
      default: false,
    },
    customizationSettings: {
      type: Object,
      default: () => {
        return {}
      },
    },
    saving: {
      type: Boolean,
      default: false,
    },
    minWidth: {
      type: String,
      default: '',
    },
    allowStetch: {
      type: Boolean,
      default: false,
    },
    taskWidth: {
      type: Number,
      default: 56,
    },
    noDataText: {
      type: String,
    },

    //special drag drop actions for PM asset planner alone
  },
  components: {
    Pagination,
    settings: Settings,
    timeMetricSettings,
    columnSettings,
    InlineSvg,
    NewDatePicker,
    FNewPopover,
  },

  data() {
    return {
      resourceList: [],
      resourceKey: null,
      moveTypeOptions: ['askEachTime', 'single', 'subsequent'],
      moveTypeSelect: null,
      showMoveSettings: false,

      showColumnSettings: false,

      showTimeMetricSettings: false,

      showLegendSettings: false,
      legendOptions: [
        { name: 'none', displayName: 'None' },
        { name: 'timeMetric', displayName: 'Calendar' },
        { name: 'category', displayName: 'Category' },
        { name: 'priority', displayName: 'Priority' },
        { name: 'type', displayName: 'Type' },
        { name: 'frequency', displayName: 'Frequency' },
      ],
      legendColorAttribute: null,

      tableRowData: [],
      resourceTitles: [],
      resourceHeaders: [],
      resourceGroupMap: {},
      groupingBreaks: {},
      headerHeight: 0,
      headerWidth: 0,

      resources: [],
      itemCount: null,
      pageNumber: 1,
      pageStartIndex: null,
      pageEndIndex: null,
      visibleRows: [],

      dragDrop: {
        //task,source are needed but no need to be  reactive
        // task: null,
        // sourceCell: null,
        //hoveredCell: null,

        multiSelectItems: [],
        // ghostTaskIDs: [],
        // dragInProgress: false,
        multiSelectCol: null,
      },
      // allowCardClose:false,
      showDropOptions: false,
      showCard: false,
      openTask: null,
      openCell: null,
      dontAskAgain: false,
      moveTaskBuffer: [],
    }
  },

  watch: {
    pageNumber(to, from) {
      console.log('new page', to)

      if (to > from) {
        this.nextPage()
        this.$nextTick(() => {
          this.$refs['tableContainer'].scrollTop = 0
        })
      } else {
        this.prevPage()
        this.$nextTick(() => {
          this.$refs['tableContainer'].scrollTop = 0
        })
      }
    },
  },
  created() {
    //listen for ESC Key press to exit  full screen
    this.escapeListener = e => {
      if (e.key === 'Escape') {
        this.isFullScreen = false
      }
    }
    document.addEventListener('keydown', this.escapeListener)
  },
  destroyed() {
    document.removeEventListener('keydown', this.escapeListener)
  },
  beforeUpdate() {
    this.timeBeforeUpdate = performance.now()
  },
  updated() {
    // console.log(oldData,newData)
    //console.log('after',JSON.parse(JSON.stringify(this.$data)))
    console.warn(
      'FP Updated time took=',
      performance.now() - this.timeBeforeUpdate
    )
  },
  methods: {
    generatePages() {
      this.resources = []
      //get number of unique resources from resource ID and generate page boundaries with it
      //need to find ranges in each page
      if (this.resourceTitles.length == 0) {
        return
      }

      let resourceID = this.resourceTitles[0].id
      let resourceStartIndex = 0
      for (let i = 0; i < this.resourceTitles.length; i++) {
        if (resourceID != this.resourceTitles[i].id) {
          this.resources.push({
            resourceID,
            resourceStartIndex,
            resourceEndIndex: i - 1,
          })
          //next resource below
          ;(resourceID = this.resourceTitles[i].id), (resourceStartIndex = i)
        }
      }
      this.resources.push({
        resourceID,
        resourceStartIndex,
        resourceEndIndex: this.resourceTitles.length - 1,
      })
      this.itemCount = this.resources.length
      this.pageStartIndex = 0
      this.pageEndIndex = this.pageSize - 1
      if (this.pageEndIndex >= this.itemCount) {
        this.pageEndIndex = this.itemCount - 1
      }
      this.setVisibleRows()
    },
    prevPage() {
      // if(this.pageStartIndex==0)
      // {
      //   return
      // }
      // else{
      console.log('prev page')
      this.pageStartIndex = (this.pageNumber - 1) * this.pageSize
      this.pageEndIndex = this.pageNumber * this.pageSize - 1

      if (this.pageEndIndex >= this.itemCount) {
        this.pageEndIndex = this.itemCount - 1
      }

      this.setVisibleRows()
    },
    nextPage() {
      console.log('next page')
      this.pageStartIndex = (this.pageNumber - 1) * this.pageSize
      this.pageEndIndex = this.pageNumber * this.pageSize - 1

      if (this.pageEndIndex >= this.itemCount) {
        this.pageEndIndex = this.itemCount - 1
      }
      this.setVisibleRows()
    },
    setVisibleRows(pageStartIndex, pageEndIndex) {
      this.visibleRows = this.tableRowData.slice(
        this.resources[this.pageStartIndex].resourceStartIndex,
        this.resources[this.pageEndIndex].resourceEndIndex + 1
      )
      this.generateResourceGroups()
    },

    scrollColToView() {
      if (this.currentDateCol) {
        let scrollAmount = this.currentDateCol.index * 56
        let scrollContainer = this.$refs['tableContainer']
        if (!isEmpty(scrollContainer)) {
          scrollContainer.scrollLeft = scrollAmount
        }
      }
    },
    printPlanner() {
      let { linkName = 'app' } = getApp()
      linkName = linkName == 'newapp' ? 'app' : linkName

      //to do ,later get url/route from user as prop
      let printUrl = new URL(window.location)
      printUrl.searchParams.append('paginate', false)
      printUrl.searchParams.append('plannerType', this.plannerType)
      printUrl.pathname = `${linkName}/pdf/pmplanner`
      window.open(printUrl.toString())
    },
    handleSettingsDropDown(command) {
      this.$emit('settings:open', command)
      switch (command) {
        case 'planner':
          this.showSettings = true
          break

        case 'moveType':
          this.moveTypeSelect = this.plannerSettings.moveType
          this.showMoveSettings = true
          break
        case 'column':
          this.showColumnSettings = true
          break
        case 'timeMetric':
          this.showTimeMetricSettings = true
          break
        case 'legend':
          this.legendColorAttribute = this.plannerSettings.legendSettings[0].name
          this.showLegendSettings = true
          break
      }
    },

    async loadSettings() {
      //replace with API call
      try {
        let resp = await this.$http.post('v2/pmplanner/settings', {
          settings: { plannerType: PlannerTypes[this.plannerType] },
        })
        return resp.data.result.settings
      } catch (e) {
        console.log('error in loading settings', e)
      }
    },
    handleSettingsChange(viewSettings) {
      console.log('settings changed ')
      this.plannerSettings.viewSettings = viewSettings

      this.showSettings = false
      this.saveSettingsRequest()
        .then(resp => {
          console.log('settings saved')
          this.initPlanner()
        })
        .catch(e => {
          console.log('error loading Planner setting ', e)
        })
    },
    saveMoveType() {
      this.plannerSettings.moveType = this.moveTypeSelect
      this.saveSettingsRequest()
      this.showMoveSettings = false
    },
    saveColumnSettings(columnSettings) {
      this.showColumnSettings = false
      this.plannerSettings.columnSettings = columnSettings
      this.saveSettingsRequest().then(() => {
        console.log('column settings saved ', columnSettings)
        this.initPlanner()
      })
    },
    saveTimeMetricSettings(timeMetricSettings) {
      this.showTimeMetricSettings = false
      this.plannerSettings.timeMetricSettings = timeMetricSettings
      this.saveSettingsRequest().then(() => {
        console.log('time metric settings saved ', timeMetricSettings)
        this.initPlanner()
      })
    },
    saveLegendSettings() {
      this.plannerSettings.legendSettings = [
        {
          name: this.legendColorAttribute,
          displayName: this.legendOptions.find(
            e => e.name == this.legendColorAttribute
          ).displayName,
        },
      ]
      this.showLegendSettings = false
      this.saveSettingsRequest().then(() => {
        console.log('legend metric settings saved ')
        this.initPlanner()
      })
    },

    async saveSettingsRequest() {
      try {
        if (!isNil(this.plannerSettings.timeMetricSettings)) {
          //not null or undefined , but [] is valid
          let metricsEnabledCount = this.plannerSettings.timeMetricSettings.filter(
            e => e.enabled
          ).length

          console.log('metrics selected', metricsEnabledCount)
          //if more than one metric is selected , show the calendar column else hide it
          if (metricsEnabledCount > 1) {
            //time metric column is last
            this.plannerSettings.columnSettings[3].enabled = true
          } else {
            this.plannerSettings.columnSettings[3].enabled = false
          }
        }
        let url = 'v2/pmplanner/update/settings'
        let resp = await this.$http.post(url, {
          settings: this.plannerSettings,
        })
        return resp
      } catch (e) {
        console.log('error in saving settings')
        throw e
      }
    },

    // gridColumnClass(column) {
    //   console.log('grid col class method')
    //   if (column.start <= this.currentDate && this.currentDate < column.end) {
    //     return 'current-date'
    //   }
    // },
    handleClose() {
      this.userDropOptionPromise('single')
    },
    handleCardClose() {
      // if(this.allowCardClose){
      this.showCard = false
      this.openTask = null
      this.openCell = null

      // else{
      //     this.$refs['popover'].positionPopOver()
      // }
    },

    groupHeaderClick(group) {
      this.goToRange(group.start, this.viewState[this.currentView].grouping)
      this.setPicker()
    },
    colHeaderClick(event, column) {
      if (event.shiftKey) {
        this.selectAll(column)
      } else {
        this.goToRange(column.start, this.viewState[this.currentView].gridLines)
        this.setPicker()
      }
    },
    preventTextSelection() {
      if (event.shiftKey) {
        event.preventDefault()
      }
    },
    isDropAllowed(sourceCell, destinationCell) {
      if (sourceCell.equals(destinationCell)) {
        return
      }
      //allow drop only if greater than current date and drop across rows is permitted
      let isValidTime =
        sourceCell.colId > this.currentDate &&
        destinationCell.colId > this.currentDate
      if (isValidTime) {
        let isDragAlowed = this.isRowDragRestrict
          ? sourceCell.rowIndex == destinationCell.rowIndex
          : true
        if (!isDragAlowed) {
          Message.info("Can't reschedule across rows")
        }
        return isDragAlowed
      } else {
        Message.info("Can't reschedule  events to/from past dates")
      }
      return false
    },
    // dropTargetClass(cell) {
    //   console.log('drop target class called')
    //   if (cell.equals(this.dragDrop.hoveredCell)) {
    //     //this indicates the currently hovered cell ,

    //     if (this.dragDrop.hoveredCell.colId > this.currentDate) {
    //       console.log('valid drop target')
    //       return 'valid-drop-target'
    //     } else {
    //       return 'invalid-drop-target'
    //     }
    //   } else {
    //     return 'task-list-plain'
    //   }
    // },

    handleDrag(e) {
      //console.log("drag fired");
      this.handleScroll(e)
      // if(this.isMultiSelect)
      // {
      //     this.moveDragGhost(
      //     e.clientX - this.originalX,
      //     e.clientY - this.originalY
      //   );
      // }

      // if (this.plannerSettings.moveType == 'subsequent' || this.isMultiSelect) {
      //   this.moveDragGhost(
      //     e.clientX - this.originalX,
      //     e.clientY - this.originalY
      //   )
      // }
    },
    //reset to zero once done on dragend, this must always succed so dont throttle
    resetDragGhost() {
      //console.log('drag ghost reset')
      this.drawCustomGhost(0, 0)
    },
    //this needs to be throttled to be smooth
    moveDragGhost: throttle(function(offSetX, offSetY) {
      this.drawCustomGhost(offSetX, offSetY)
    }, 50), //adjust this throttle to render a smooth ghost
    drawCustomGhost(offSetX, offSetY) {
      if (!this.dragDrop.dragInProgress) return
      //console.log('drawing ghost',offSetX,offSetY)
      if (this.dragDrop.ghostTaskIDs.length == 0) return
      let selectQuery = ''
      this.dragDrop.ghostTaskIDs.forEach(taskId => {
        selectQuery += `div[data-taskid='${taskId}'],`
      })

      selectQuery = selectQuery.substring(0, selectQuery.length - 1)
      // console.log(selectQuery)
      let taskNodes = document.querySelectorAll(selectQuery)

      taskNodes.forEach(taskNode => {
        //needed for smooth animation
        // requestAnimationFrame(() => {
        // // taskNode.style.position='absolute'
        // //taskNode.style.transform = `translate(${offSetX}px,${offSetY}px)`;
        // taskNode.style.left = offSetX + "px";
        // taskNode.style.top = offSetY + "px";
        // });
      })
    },
    handleScroll: throttle(function(mouseEvent) {
      console.log('handle scroll')

      let scrollContainer = this.$refs['tableContainer']
      //start scrolling up when mouse reaches header

      let containerRect = scrollContainer.getBoundingClientRect()

      //top scroll triggered when drag reaches  header
      let topPoint = containerRect.top + this.$refs['tableHeader'].offsetHeight
      let bottomPoint = containerRect.top + scrollContainer.offsetHeight
      let rightPoint = containerRect.left + scrollContainer.offsetWidth
      // console.log("client y", mouseEvent.clientY);

      let leftPoint = containerRect.left + 150 // 150px is header col sticky
      //left scroll triggered when drag reaches  fixed col to the left
      if (this.isServerRows) {
        leftPoint = containerRect.left + this.resourceHeaders.length * 150
      }

      let scrollMeta = {}

      //vertical scroll  up
      if (mouseEvent.clientY < topPoint) {
        scrollMeta.scrollTop = -25 //scroll 25 pixels top
      } else if (mouseEvent.clientY > bottomPoint) {
        scrollMeta.scrollTop = 25
      }

      //horizontal scroll left , right
      if (mouseEvent.clientX < leftPoint) {
        scrollMeta.scrollLeft = -25
      } else if (mouseEvent.clientX > rightPoint) {
        scrollMeta.scrollLeft = 25
      }
      if (!this.$helpers.isEmpty(scrollMeta)) this.scrollContainer(scrollMeta)
    }, 100),

    scrollContainer(scrollMeta) {
      console.log('container scrolled')
      let container = this.$refs['tableContainer']
      if (scrollMeta.scrollTop) {
        container.scrollTop += scrollMeta.scrollTop
        // console.log("vertical");
      }
      if (scrollMeta.scrollLeft) {
        container.scrollLeft += scrollMeta.scrollLeft
        // console.log("horizontal");
      }

      // }
    },
    handleTaskClick(event, task, cell) {
      if (event.shiftKey) {
        this.selectTask(task, cell)
      } else {
        // this.openTask.pageX=event.pageX
        // this.openTask.pageY=event.pageY
        setTimeout(() => {
          //check and remove settimeout usage
          this.openCell = cell
          this.openTask = task

          //close old card and then show new one
          this.showCard = false
          this.$nextTick(() => {
            this.showCard = true
            this.$nextTick(() => {
              this.$emit('eventClick', {
                event: this.openTask,
                displayType: this.currentViewState['displayType'],
                col: this.gridColumns[this.openCell.colIndex],
              })
              this.positionPopOver()
            })
          })
        }, 0)
      }
    },
    positionPopOver() {
      const popper = this.$refs['resourcePlanner'].querySelector(
        '#customPopover'
      )
      let scrollContainer = this.$refs['tableContainer']
      const reference = scrollContainer.querySelector(
        `div[data-taskid= '${this.openTask.id}']`
      )

      //console.log(reference,popper)
      let pop = new Popper(reference, popper, {
        placement: 'right',
        modifiers: {
          preventOverflow: {
            enabled: true,
            boundariesElement: scrollContainer,
          },
        },
      })
    },

    selectTask(task, cell, selectAll = false) {
      if (!this.isPMAssetPlanner) return //multi select not allowed in staff view
      if (task.selection) {
        if (selectAll) {
          return //don't unselect already selected during all selection
        }
        task.selection = false
        let taskIndex = this.dragDrop.multiSelectItems.findIndex(
          e => task.id == e.task.id
        )
        this.dragDrop.multiSelectItems.splice(taskIndex, 1)
      } else {
        //
        if (this.isMultiSelect) {
          //some other items are already selected , restrict selection to single column
          if (cell.colIndex != this.dragDrop.multiSelectCol) {
            Message.info('Select multiple items within same column')
            return
          }
        }
        this.dragDrop.multiSelectCol = cell.colIndex
        task.selection = true
        this.dragDrop.multiSelectItems.push({ task, sourceCell: cell })
      }
    },
    selectAll(column) {
      //get total number of tasks in row
      let colIndex = column.index
      console.log('select all')
      let totalTaskCount = 0

      this.visibleRows.forEach(row => {
        row.data[colIndex].tasks.forEach(task => {
          totalTaskCount++
        })
      })
      console.log('task count', totalTaskCount)
      console.log('selection count', this.dragDrop.multiSelectItems.length)
      //true is select all , false is deselect All , delect shoud be done only when all tasks are already selected
      let isSelectAll = totalTaskCount != this.dragDrop.multiSelectItems.length

      this.visibleRows.forEach(row => {
        row.data[colIndex].tasks.forEach(task => {
          this.selectTask(task, row.data[colIndex], isSelectAll)
        })
      })
      //get all tasks in column
    },
    clearAllSelections() {
      console.log('clear selections called')
      this.dragDrop.multiSelectItems.forEach(e => {
        e.task.selection = false
      })
      this.dragDrop.multiSelectItems = []
    },
    handleDragStart(e, task, cell) {
      //e.dataTransfer.setDragImage(document.createElement('span'),0,0)
      if (this.isMultiSelect) {
        //if dragged cell not selected cell cancel the drag

        if (
          !this.dragDrop.multiSelectItems.find(e => {
            return task.id == e.task.id
          })
        ) {
          console.log('unallowed drag')
          e.preventDefault()
          return false
        }
      }

      //  this.dragDrop.dragInProgress = true

      console.log('drag start')
      e.dataTransfer.setData('text/plain', 'dummy')
      //without set timeout ghost won't be drawn as item is hidden before being dragged

      //setTimeout(() => e.target.classList.add('hide'), 0)
      requestAnimationFrame(() => {
        e.target.classList.add('hide')
      })
      this.dragDrop.task = task
      this.dragDrop.sourceCell = cell
      // e.preventDefault()

      this.originalX = e.clientX
      this.originalY = e.clientY
      //for multiple move case we need to draw ghosts for all  cells
      //3 cases ,
      // single select subsequent , multi subsequent and non subsequent

      // if (this.isMultiSelect) {
      //   if (this.plannerSettings.moveType == 'subsequent') {
      //     console.log(' get multi and subsequent ghost')
      //     this.dragDrop.multiSelectItems.forEach(e => {
      //       this.dragDrop.ghostTaskIDs.push(
      //         ...this.getAllNextInrowTaskIds(e.sourceCell)
      //       )
      //     })
      //   } else {
      //     this.dragDrop.ghostTaskIDs = this.dragDrop.multiSelectItems.map(e => {
      //       return e.task.id
      //     })
      //     console.log('get multi  ghost')
      //   }
      // } else if (this.plannerSettings.moveType == 'subsequent') {
      //   console.log('get subsequent  ghost')
      //   this.dragDrop.ghostTaskIDs = this.getAllNextInrowTaskIds(cell)
      //   //multiselect and subsequent move
      // }
    },
    getAllNextInrowTaskIds(cell) {
      let allMatchedTasksIds = []
      let colIndex = cell.colIndex
      let rowIndex = cell.rowIndex

      for (let i = colIndex; i < this.tableRowData[rowIndex].data.length; i++) {
        this.tableRowData[rowIndex].data[i].tasks.forEach(task => {
          // if(task.id==this.dragDrop.task.id)return
          allMatchedTasksIds.push(task.id)
        })
      }
      return allMatchedTasksIds
    },

    handleDrop(e, destinationCell) {
      e.preventDefault()
      console.log('drop')
      // let colDisplacement = destinationCell.colIndex - this.dragDrop.sourceCell.colIndex;

      if (this.isDropAllowed(this.dragDrop.sourceCell, destinationCell)) {
        //this.dragDrop.dragInProgress = false
        //single or subsequent move option available for PM PLanner asset view
        if (this.isPMAssetPlanner) {
          this.extraDropOptions(
            this.dragDrop.task,
            this.dragDrop.sourceCell,
            destinationCell
          )

          return
        } else {
          this.handleNormalDragDrop(
            this.dragDrop.task,
            this.dragDrop.sourceCell,
            destinationCell
          )
        }
      }
    },

    handleNormalDragDrop(draggedTask, sourceCell, destinationCell) {
      console.log('Normal  drop')
      this.moveWorkOrder(draggedTask, sourceCell, destinationCell)
    },
    //only for PM Planner asset view , single ,subsequent,with and without modal case
    extraDropOptions(draggedTask, sourceCell, destinationCell) {
      console.log('PM Planner Asset view drop')
      if (this.plannerSettings.moveType == 'askEachTime') {
        //show dialog case
        this.isMultiSelect
          ? this.moveWorkOrderMultiple(sourceCell, destinationCell)
          : this.moveWorkOrder(draggedTask, sourceCell, destinationCell)

        //show modal after item has moved
        this.$nextTick(() => {
          // setTimeout(() => {
          this.promptUserDropAction().then(option => {
            //this.plannerSettings.moveType = option;
            if (option == 'subsequent') {
              this.isMultiSelect
                ? this.moveSubsequentWorkOrdersMultiple(
                    sourceCell,
                    destinationCell
                  )
                : this.clearAllSelections()
              this.moveSubsequentWorkOrders(
                draggedTask,
                sourceCell,
                destinationCell
              )
            }
            // this.userDropOptionPromise = null;
            this.showDropOptions = false
            this.clearAllSelections()
          })
          // }, 100);
        })
      } else if (this.plannerSettings.moveType == 'single') {
        this.isMultiSelect
          ? this.moveWorkOrderMultiple(sourceCell, destinationCell)
          : this.moveWorkOrder(draggedTask, sourceCell, destinationCell)
        this.clearAllSelections()
      } else if (this.plannerSettings.moveType == 'subsequent') {
        this.isMultiSelect
          ? this.moveWorkOrderMultiple(sourceCell, destinationCell)
          : this.moveWorkOrder(draggedTask, sourceCell, destinationCell)
        this.isMultiSelect
          ? this.moveSubsequentWorkOrdersMultiple(sourceCell, destinationCell)
          : this.moveSubsequentWorkOrders(
              draggedTask,
              sourceCell,
              destinationCell
            )
        this.clearAllSelections()
      }
    },
    //move one workorder per row but multiple selected in the same column
    moveWorkOrderMultiple(sourceCell, destinationCell) {
      let colDisplacement = destinationCell.colIndex - sourceCell.colIndex
      this.dragDrop.multiSelectItems.forEach(e => {
        this.moveWorkOrder(
          e.task,
          e.sourceCell,
          this.tableRowData[e.sourceCell.rowIndex].data[
            e.sourceCell.colIndex + colDisplacement
          ]
        )
      })
      //setTimeout(()=>{this.clearAllSelections()},0)
    },
    moveWorkOrder(draggedTask, sourceCell, destinationCell, emitEvent = true) {
      let colDisplacement = destinationCell.colIndex - sourceCell.colIndex
      draggedTask.start = this.getNextTimeStamp(
        draggedTask.start,
        this.currentViewState.gridLines,
        colDisplacement
      )
      //recalculate formatted label string when task date changes
      this.formatLabel(draggedTask)
      this.spliceAndInsertTask(draggedTask, sourceCell, destinationCell)

      if (emitEvent) {
        this.$emit('dropped', {
          event: draggedTask,
          sourceCell: sourceCell,
          destinationCell: destinationCell,
        })
      }
    },
    //for subsequent move task that was dragged has already moved,so no need to move it again
    moveSubsequentWorkOrdersMultiple(sourceCell, destinationCell) {
      let colDisplacement = destinationCell.colIndex - sourceCell.colIndex
      this.dragDrop.multiSelectItems.forEach(e => {
        this.moveSubsequentWorkOrders(
          e.task,
          e.sourceCell,
          this.tableRowData[e.sourceCell.rowIndex].data[
            e.sourceCell.colIndex + colDisplacement
          ]
        )
      })
    },
    moveSubsequentWorkOrders(draggedTask, draggedSourceCell, destinationCell) {
      let colDisplacement =
        destinationCell.colIndex - draggedSourceCell.colIndex
      // get all items in the row after the current item and move them each by a factor equal to col displacement
      let dragColIndex = draggedSourceCell.colIndex
      let rowIndex = draggedSourceCell.rowIndex
      //when moving subsequent tasks don't loop thorough and mutate , that results in a task being moved many times
      let newPositions = []
      for (let i = dragColIndex; i < this.gridColumns.length; i++) {
        newPositions.push(
          ...this.calculateNewPostion(
            draggedTask,
            this.tableRowData[rowIndex].data[i],
            colDisplacement
          )
        )
      }

      newPositions.forEach(e => {
        this.spliceAndInsertTask(e.task, e.sourceCell, e.destinationCell)
        this.$emit('dropped', {
          event: e.task,
          sourceCell: e.sourceCell,
          destinationCell: e.destinationCell,
        })
      })
    },

    calculateNewPostion(draggedTask, sourceCell, colDisplacement) {
      let newPosOfTasksInCell = []
      //for each cell move all tasks by amount user moved draggedTask
      sourceCell.tasks.forEach(task => {
        let newTime = this.getNextTimeStamp(
          task.start,
          this.currentViewState.gridLines,
          colDisplacement
        )

        if (
          newTime >= this.gridColumns[0].start &&
          newTime < this.gridColumns[this.gridColumns.length - 1].end &&
          (this.currentDateCol ? newTime >= this.currentDateCol.end : true) &&
          draggedTask.id != task.id // task that is dragged  is always moved first , avoid moving the same task again
        ) {
          let destinationCell = this.tableRowData[sourceCell.rowIndex].data[
            sourceCell.colIndex + colDisplacement
          ]
          //mutate the start time to new cell time
          task.start = newTime
          this.formatLabel(task)
          //refresh the label when date changes

          newPosOfTasksInCell.push({ task, sourceCell, destinationCell })
        }
      })

      return newPosOfTasksInCell
    },
    spliceAndInsertTask(task, sourceCell, destinationCell) {
      //change resource key to reflect new row
      if (!this.isRowDragRestrict) {
        //to do , dont get resourcekey dynamically
        task[this.resourceKey] = this.tableRowData[
          destinationCell.rowIndex
        ].resource.id
      }

      let deleteIndex = sourceCell.tasks.findIndex(e => {
        return e.id == task.id
      })
      sourceCell.tasks.splice(deleteIndex, 1)

      let insertIndex = destinationCell.tasks.findIndex(e => {
        return this.start < e.start
      })
      if (insertIndex == -1) {
        insertIndex = destinationCell.tasks.length
      }
      destinationCell.tasks.splice(insertIndex, 0, task)
    },
    handleDragEnd(e) {
      console.log('drag end')
      if (e.target.classList.contains('hide')) {
        e.target.classList.remove('hide')
      }

      //this.resetDragGhost()
      // this.dragDrop.ghostTaskIDs = []
      // this.dragDrop.dragInProgress = false
      //this.clearAllSelections()

      // this.dragDrop.hoveredCell = null
    },
    handleDragEnter(e, cell) {
      e.preventDefault()
      console.log('drag enter fired ')
      //this.dragDrop.hoveredCell = cell
    },
    handleDragOver(e, cell) {
      //console.log(e,hoveredCell);

      // if(this.currentDate.valueOf()<this.dra)
      e.preventDefault()
      // console.log("drag over");
    },
    handleDragLeave() {
      //this.dragDrop.hoveredCell=null
      console.log('drag leave')
    },
    handleUserDropOption(option) {
      if (this.dontAskAgain) {
        this.plannerSettings.moveType = option
        this.saveSettingsRequest()
      }
      this.userDropOptionPromise(option)
      this.dontAskAgain = false
    },
    promptUserDropAction() {
      this.showDropOptions = true
      return new Promise(resolve => {
        this.userDropOptionPromise = resolve
      })
    },
    /**
     * maintain planner date state , refresh the grid alone
     */
    refreshGrid() {
      this.loading = true
      this.emitViewChanged()
    },
    /**
     * call this to render the planner with given tasks and resources
     *
     * @param {taskList} - List of tasks to render
     * @param {resourceList} - List of resources
     * @param {resourceKey} - which field in task listed is mapped to resourcelist
     */

    renderPlanner(taskList, resourceList, resourceKey) {
      // this.resourceMeta=resourceMeta
      // this.allResources=allResources
      //console.time();
      this.resourceList = resourceList

      this.taskList = taskList
      this.resourceKey = resourceKey

      this.generateColumnData()
      this.spreadTasksToColumns()
      this.generateRowData()
      //console.timeEnd();

      if (this.paginate) {
        console.log('paginating')
        this.generatePages()
      } else {
        console.log('no pagination')
        this.visibleRows = this.tableRowData
      }
      this.loading = false
      this.$nextTick(() => {
        this.$emit('ready')

        this.scrollColToView()
      })
    },
    setHeaderHeight() {
      this.headerHeight = this.$refs['tableHeader'].offsetHeight
    },
    renderPlannerNew(resourceHeaders, resourceTitles, taskData) {
      let timeBefore = performance.now()
      this.resourceHeaders = resourceHeaders
      this.resourceTitles = resourceTitles

      this.generateColumnData()
      this.processServerRows(taskData)

      this.resourceGroupMap = {}
      this.groupingBreak = {}

      if (this.paginate) {
        console.log('paginating')
        //for paginated case ,generate resource groups per page
        this.generatePages()
      } else {
        console.log('no pagination')
        this.visibleRows = this.tableRowData
        if (this.resourceGrouping) {
          this.generateResourceGroups()
        }
      }

      this.loading = false
      this.$nextTick(() => {
        this.$emit('ready')
        this.setHeaderHeight()
        this.scrollColToView()
      })
      console.warn('time for newRender=', performance.now() - timeBefore)
    },
    //new method , server rows,copied code , TODO , refractor to common format
    processServerRows(data) {
      let perfStart = performance.now()
      this.tableRowData = []
      data.forEach((rowTasks, rowIndex) => {
        //initialize an  array of columns with cell object for each row
        let rowColumns = []

        for (let i = 0; i < this.gridColumns.length; i++) {
          let cell = new Cell(null, this.gridColumns[i].start, rowIndex, i)
          cell.tasks = []
          rowColumns.push(cell)
        }

        let colIndex = 0
        rowTasks.some(task => {
          this.formatLabel(task)
          //iterate through rowTasks list,
          //for each task go to the correct grid(column) and insert

          while (task.start >= this.gridColumns[colIndex].end) {
            colIndex++
            if (
              task.start >= this.gridColumns[this.gridColumns.length - 1].end
            ) {
              return true
              //break out of loop when one of the tasks exceeds end column in grid
            }
          }
          this.$set(task, 'selection', false)
          rowColumns[colIndex].tasks.push(task)
          return false //some func  false calls next interation , true breaks loop
        })

        this.tableRowData.push({
          data: rowColumns,
          rowIndex: rowIndex,
          visibility: true,
        })
      })
      console.warn('generate rows took', performance.now() - perfStart)
    },

    generateResourceGroups() {
      this.groupingBreaks = {}
      this.resourceGroupMap = {}
      //this.visibleRows.forEach(visibleRow=>{visibleRow.visibility=true})
      //to do , see if it's possible to maintain state across pages

      this.visibleRows.forEach(visibleRow => {
        visibleRow.visibility = true //this is row visiblity
        let rg = this.resourceTitles[visibleRow.rowIndex].resourceGroup
        if (this.resourceGroupMap[rg]) {
          this.resourceGroupMap[rg].resources.push(visibleRow.rowIndex)
        } else {
          this.resourceGroupMap[rg] = {
            resources: [visibleRow.rowIndex],
            visible: true, //this is to toggle expand /collapse in icon
          }
          this.groupingBreaks[visibleRow.rowIndex] = rg
        }
      })
    },

    toggleResourceGroupCollapse(resourceGroup) {
      console.log('toggle visibility for', resourceGroup)
      this.resourceGroupMap[resourceGroup].visible = !this.resourceGroupMap[
        resourceGroup
      ].visible
      this.resourceGroupMap[resourceGroup].resources.forEach(e => {
        this.tableRowData[e].visibility = this.resourceGroupMap[
          resourceGroup
        ].visible
      })
    },

    generateRowData() {
      ///generate column Data for el table
      this.tableRowData = []

      // this.tableColData.unshift({ prop: "resource", label: "Staff" });
      //generate row data for el table
      //create a map resource count , use that later to sort the resource list in order of maximum assigned tasks

      let resourceTaskCountMap = {}

      this.resourceList.forEach((resource, rowIndex) => {
        resourceTaskCountMap[resource.id] = 0
        let row = {}
        //row data structure : resource={} , data=[{cell},{cell}....]
        row.resource = resource
        row.data = []
        row.rowIndex = rowIndex
        this.gridColumns.forEach((column, colIndex) => {
          let cell = new Cell(resource.id, column.start, rowIndex, colIndex)
          cell.tasks = []
          cell.tasks = column.tasks.filter(task => {
            return task[this.resourceKey] == resource.id
          })
          cell.tasks.forEach(task => {
            //if tasks have an end time , need to set

            if (task.end && this.allowStetch) {
              let taskLength = task.end - task.start
              let taskSpan = Math.ceil(taskLength / this.getGridLength())

              this.$set(task, 'taskSpan', taskSpan)
            }
          })
          // row[column.prop] = cell;
          row.data[colIndex] = cell
          resourceTaskCountMap[resource.id] += row.data[colIndex].tasks.length
        })
        this.tableRowData.push(row)
      })

      //row index is wrong on sort , need to fix
      // this.tableRowData.sort((e1, e2) => {
      //   return (
      //     -1 *
      //     (resourceTaskCountMap[e1["resource"].id] -
      //       resourceTaskCountMap[e2["resource"].id])
      //   );
      // });
    },

    formatLabel(task) {
      task.dateTime = this.$helpers
        .getOrgMoment(task.start)
        .format(this.getLabelFormat(this.viewState[this.currentView].gridLines))
    },
    getGridLength() {
      return this.gridColumns[1].start - this.gridColumns[0].start
    },
    deleteEvent(taskID) {
      //need to find the event and delete it from the table structure

      let eventDetails = this.getEventDetails(taskID)
      eventDetails.cell.tasks.splice(eventDetails.taskIndex, 1)
      console.log('deleting event', eventDetails.task)
      this.handleCardClose()
    },
    //currently used only in staff view
    reAssignResource(taskID, newResourceID) {
      let eventDetails = this.getEventDetails(taskID)
      let task = eventDetails.task
      let sourceCell = eventDetails.cell

      let newColIndex = sourceCell.colIndex
      let newRowIndex = this.tableRowData.findIndex(row => {
        return row.resource.id == newResourceID
      })
      let destinationCell = this.tableRowData[newRowIndex].data[newColIndex]
      this.moveWorkOrder(task, sourceCell, destinationCell)
      this.handleCardClose()
    },
    getEventDetails(taskID) {
      let eventDetails = null
      this.tableRowData.forEach(row => {
        row.data.forEach(cell => {
          cell.tasks.forEach((task, taskIndex) => {
            if (task.id == taskID) {
              console.log('found event')
              eventDetails = { task: task, taskIndex, cell }
              return
            }
          })
        })
      })
      //can't use return to break out of foreach inside foreach
      return eventDetails
    },
  },
  computed: {
    isMultiSelect() {
      return this.dragDrop.multiSelectItems.length != 0
    },

    gridColHeaderClass() {
      return this.viewState[this.currentView].showGridLineHeaders
        ? 'grid-col-header'
        : 'grid-col-header-hide'
    },

    resourceTypeRowSpan() {
      return this.currentViewState.showGridLineHeaders ||
        this.currentViewState.grouping
        ? 2
        : 1
    },
  },
}
</script>

<style lang="scss">
.facilio-resource-planner {
  width: 100%;
  height: 100%;
}
.pm-printer-btn {
  width: 50px;
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
  color: #ffffff;
  .el-icon-printer {
    vertical-align: text-top;
    font-size: 16px;
  }
}
.fc-btn-group-white .pm-printer-btn:hover .el-icon-printer {
  color: #fff;
}
.pm-settings-btn {
  border-top-left-radius: 0px !important;
  border-bottom-left-radius: 0px !important;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
}

.facilio-resource-planner.full-screen {
  background-color: #f7f8f9;
  padding: 30px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1200;
  animation: builder-active 0.2s ease-in-out;
  .table-container {
    height: calc(100vh - 140px) !important;
  }
}
.facilio-resource-planner .grid-container {
  width: 100%;
  height: 100%;
  /* overflow: auto; */
}
.facilio-resource-planner .fc-rp-top-bar {
  justify-content: space-between;
  padding: 20px 20px 10px;
  .settings-fullscreen {
    // width: 100px;
    .settings {
      // width: 50%;
    }
    .fullscreen {
      // width: 50%;
    }
  }
}
.settings-fullscreen.el-button-group > .el-button:last-child {
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
.pm-fullscreen-btn {
  width: 50px;
  border-top-left-radius: 3px !important;
  border-bottom-left-radius: 3px !important;
}
.settings-fullscreen .el-button:first-child {
  border-top-left-radius: 3px;
  border-bottom-left-radius: 3px;
  border-top-right-radius: 0px;
  border-bottom-right-radius: 0px;
  border-left: none;
  padding-left: 12px !important;
  padding-right: 12px !important;
}

// Native table styles
.facilio-resource-planner {
  .table-container {
    scroll-behavior: smooth;
    overflow: scroll;
    width: 100%;
    // padding-bottom: 20px !important;
    td {
      height: 0 !important; //Required , only when td height is set , div inside cell respects 100% height
    }

    .rp-group-header {
      background-color: #f8f8f9; //adding for now since cell isn't expanding to fill th
      z-index: 3;
      position: sticky;
      top: 0;
    }
    .rp-group-header-cell {
      line-height: 30px;
      background-color: #f8f8f9;
      font-size: 12px;
      font-weight: 500;
      letter-spacing: 0.4px;
      text-align: center;
      color: #324056;
      // height: 29px;
      // line-height: 29px;
      padding: 0;
      &:hover {
        cursor: pointer;
        background: #f1f1f1;
        -webkit-transition: background-color 0.5s ease-out;
        -moz-transition: background-color 0.5s ease-out;
        -o-transition: background-color 0.5s ease-out;
        transition: background-color 0.5s ease-out;
      }
    }
    .is-grouped .rp-grid-header {
      z-index: 3;
      position: sticky;
      top: 31px;
    }
    .rp-grid-header {
      z-index: 3;
      position: sticky;
      top: 0;
    }

    .rp-grid-header-cell {
      min-width: 56px;
      //used for case when all cells are collapsed via resource grouping
      // height: 75px;
      line-height: 38px;
      font-size: 12px;
      font-weight: 500;
      letter-spacing: 0.4px;
      text-align: center;
      color: #324056;
      background: #f8f8f9;
      &:hover {
        cursor: pointer;
        background: #f1f1f1 !important;
        -webkit-transition: background-color 0.5s ease-out;
        -moz-transition: background-color 0.5s ease-out;
        -o-transition: background-color 0.5s ease-out;
        transition: background-color 0.5s ease-out;
      }
    }
    //grouping on case , lighten gridline headers
    .is-grouped {
      .rp-grid-header-cell {
        background: #ffffff;
        &:hover {
          cursor: pointer;
          background: #f8f8f9 !important;
          -webkit-transition: background-color 0.5s ease-out;
          -moz-transition: background-color 0.5s ease-out;
          -o-transition: background-color 0.5s ease-out;
          transition: background-color 0.5s ease-out;
        }
      }
    }

    .rp-cell {
      // border: 1px solid black;

      height: 100%;
      overflow-wrap: break-word;
    }

    .rp-task.drag-in-progress {
      // opacity: .75;
    }

    .rp-resource-item {
      //max-width: 150px;
      width: 150px;
      z-index: 3; //task items are position relative , so while scrolling up/left must go behind sticky headers
      position: sticky;
      left: 0;
      background: #ffffff;
      &.server-rows {
        left: unset;
      }
      // width: 200px;
    }

    .rp-resource-item-cell {
      width: 150px;
      overflow-wrap: break-word; //keep for now
      //width: 200px;
      // white-space: nowrap;

      // padding: 18px 20px;
      padding-left: 10px;
      padding-right: 10px;
      font-weight: 400;
      font-weight: 400;
      line-height: 20px;
      letter-spacing: 0.4px;
      color: #324056;

      // height: 75px;
    }
    .rp-type-header {
      width: 150px;
      text-align: center;
      background: #ffffff;
      position: sticky;
      z-index: 5; //type header is 0,0 item , must always float over sticky col and header
      top: 0;
      left: 0;

      &.server-rows {
        left: unset;
      }
    }
    .rp-task.hide {
      opacity: 0;
    }

    .move-options {
      display: flex;
      flex-direction: column;
    }

    //using individual borders instead of all + collapse to fix border disappearing under position sticky elements
    border-left: 1px solid #e8ebef;
    border-top: 1px solid #e8ebef;
    border-right: 1px solid #e8ebef;

    table {
      width: 100%;
      // border: 1px solid black;
      border-collapse: separate;
      border-spacing: 0;
      //otherwise 1 px gap exists between borders , needed when border-collapse not collapse
      th,
      td {
        padding: 0;
        // width:100px;
        border-bottom: 1px solid #eff2f5;
        border-right: 1px solid rgba(232, 235, 239, 0.6);
      }
      td.rp-group-boundary {
        border-right: 1px solid #e8ebef;
      }
      th.rp-group-boundary {
        border-right: 1px solid #e8ebef;
      }
    }
  }

  // .drag-in-progress{
  //   .unallowed-date
  //   {
  //     opacity: .75;
  //     background: #f8f8f9;
  //   }
  // }

  .ellipsis {
    text-overflow: ellipsis;

    /* Required for text-overflow to do anything */
    white-space: nowrap;
    overflow: hidden;
  }
}
.resource-empty,
.resource-loading {
  height: 65vh;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.rp-type-header-cell {
  width: 150px; //fix for now as table is not respecting the width attribute
  //white-space: nowrap;
  padding-top: 10px;
  padding-bottom: 10px;
  padding-left: 10px;
  padding-right: 10px;
}
.facilio-resource-date-picker .cal-left-btn,
.facilio-resource-date-picker .cal-right-btn {
  padding: 0 !important;
}
.facilio-resource-date-picker .button-row .el-button {
  height: 36px !important;
}
.pm-planner-col-icon {
  position: absolute;
  right: 10px;
  top: 12px;
}

.rp-seperator-row {
  // background: #f2f5f5;
}
.rp-seperator-item-body-cell {
  // background: #f2f5f5;
  min-height: 42px;
}
.rp-seperator-item {
  border-right: unset !important;
}

.bg-seperator-color {
  background: #f8f8f9;
}
// since seperator row is empty  except for first col(col header) , fix width but allow text to overflow and rmove border
.rp-seperator-item-cell {
  width: 150px;
  overflow: visible;
  white-space: nowrap;
  // position: sticky;
  // // background: #ffffff;
  // left: 0;
  // z-index: 3;
  min-height: 42px;

  .el-icon-arrow-down,
  .el-icon-arrow-up {
    font-weight: 600;
    margin-left: 5px;
    margin-right: 5px;
    border-radius: 50%;
    color: #576161;
    background: #e4ecec;
    padding: 5px;
    text-align: center;
    cursor: pointer;
  }
}
</style>
