<template>
  <div
    class="fp-search-section fp-search-container"
    v-bind:class="{ 'hide-section': hidesidebar }"
  >
    <!-- search -->
    <div
      class="fc-floor-search-block"
      style="height: inherit;"
      @mouseleave="handleMouseLeave()"
      @mouseup="handleMouseUP()"
    >
      <div
        class="fp-search-area"
        v-bind:class="{ 'fc-search-box-shadow': scrollPosition > 2 }"
      >
        <div
          class="fc-search-bottom-border inline width100 inline-flex self-center"
        >
          <el-input
            size="medium"
            v-model="search"
            @input="searchContent()"
            @focus="focused = true"
            @blur="blurSearchBox()"
            class="width100 fc-input-full-border2 fc-input-full-border2-prefix"
            placeholder="Search"
            clearable
          >
            <i
              slot="prefix"
              class="el-input__icon pointer fc-grey8 f14 fwBold el-icon-search mR5"
            ></i>
          </el-input>
          <div class="f20 pL10 self-centter pT3">
            <el-popover
              v-model="searchpopover"
              placement="bottom"
              width="230"
              trigger="click"
            >
              <div>
                <div class="fc-pink fw6 p10 pB5 clearboth f11 text-uppercase">
                  Search By
                </div>
                <div
                  class="p10 fc-black-13 text-left flex-middle justify-content-space border-bottom-line pointer"
                  @click="switchModule({ name: 'All', value: 'all' })"
                >
                  <div class="flex-middle">
                    <InlineSvg
                      src="svgs/all"
                      iconClass="icon-sm icon all-fill-color"
                      class="width20px"
                    ></InlineSvg>
                    <div class="fc-black-13 fw6 pointer">
                      All
                    </div>
                  </div>
                  <div>
                    <i
                      class="el-icon-check check-active"
                      v-if="selectedModule === 'all'"
                    ></i>
                  </div>
                </div>
                <div
                  v-for="(module, index) in searchMosules"
                  :key="index"
                  class="fc-black-13 text-left fc-floor-popup-menu"
                  @click="switchModule(module)"
                >
                  <div class="flex-middle">
                    <InlineSvg
                      :src="module.icon"
                      :iconClass="module.iconClass"
                      class="width20px"
                    ></InlineSvg>
                    <div class="fc-black-13 text-left bold">
                      {{ module.name }}
                    </div>
                  </div>
                  <div class="">
                    <i
                      class="el-icon-check check-active"
                      v-if="selectedModule === module.value"
                    ></i>
                  </div>
                </div>
              </div>

              <inline-svg
                slot="reference"
                src="svgs/filter3"
                class="vertical-middle fc-filter-icon"
                iconClass="icon icon-md"
              ></inline-svg>
            </el-popover>
          </div>
        </div>
        <template v-if="focused || Object.keys(serachResult).length">
          <div class="">
            <div class="f12 p10 search-subtext">
              {{ getModuleDisplayName }} results
            </div>
            <div class="fp-filter-section ">
              <div v-if="selectedModule === 'employee'" class="flex">
                <div class="p10 pL0 pT0">
                  <el-select
                    prop="agents"
                    v-model="selectedFilter[selectedModule]['assignment']"
                    filterable
                    default-first-option
                    placeholder="Employee"
                    size="mini"
                    @change="searchContent"
                    class="fc-input-full-border-select2 round-select"
                  >
                    <el-option
                      v-for="(filter, index) in filtersOptions[selectedModule][
                        'assignment'
                      ]"
                      :key="index"
                      :label="filter.name"
                      :value="filter.value"
                      no-data-text="No filter"
                      clearable
                    ></el-option>
                  </el-select>
                </div>
                <div class="p10 pL0 pT0">
                  <el-select
                    prop="agents"
                    v-model="selectedFilter[selectedModule]['department']"
                    filterable
                    clearable
                    default-first-option
                    placeholder="Department"
                    size="mini"
                    @change="searchContent"
                    class="fc-input-full-border-select2 round-select"
                  >
                    <el-option
                      v-for="(department, index) in departments"
                      :key="index"
                      :label="department.name"
                      :value="department.id"
                      no-data-text="No Data"
                    ></el-option>
                  </el-select>
                </div>
              </div>
              <div v-else-if="selectedModule === 'desks'" class="flex">
                <div class="p10 pL0 pT0">
                  <el-select
                    v-model="selectedFilter[selectedModule]['desktype']"
                    filterable
                    default-first-option
                    placeholder="Desk type"
                    size="mini"
                    @change="searchContent"
                    class="fc-input-full-border-select2 round-select"
                  >
                    <el-option
                      v-for="(filter, index) in filtersOptions[selectedModule][
                        'desktypes'
                      ]"
                      :key="index"
                      :label="filter.name"
                      :value="filter.value"
                      no-data-text="No filter"
                      clearable
                    ></el-option>
                  </el-select>
                </div>
              </div>
              <div v-else-if="selectedModule === 'space'" class="flex">
                <div class="p10 pL0 pT0">
                  <el-select
                    v-model="selectedFilter[selectedModule]['capcity']"
                    filterable
                    default-first-option
                    placeholder="Capacity"
                    size="mini"
                    @change="searchContent"
                    class="fc-input-full-border-select2 round-select fc-floorplan-room"
                  >
                    <i
                      slot="prefix"
                      class="el-icon-user-solid f12"
                      style="color: #a9aacb;padding-top: 9px;"
                    ></i>
                    <el-option
                      v-for="(filter, index) in filtersOptions[selectedModule][
                        'capcity'
                      ]"
                      :key="index"
                      :label="filter.name"
                      :value="filter.value"
                      no-data-text="No filter"
                      clearable
                    ></el-option>
                  </el-select>
                </div>
              </div>
              <div v-else-if="selectedModule === 'lockers'" class="flex">
                <div class="p10 pL0 pT0">
                  <el-select
                    v-model="selectedFilter[selectedModule]['status']"
                    filterable
                    default-first-option
                    placeholder="Status"
                    size="mini"
                    @change="searchContent"
                    class="fc-input-full-border-select2 round-select"
                  >
                    <el-option
                      v-for="(filter, index) in filtersOptions[selectedModule][
                        'status'
                      ]"
                      :key="index"
                      :label="filter.name"
                      :value="filter.value"
                      no-data-text="No filter"
                      clearable
                    ></el-option>
                  </el-select>
                </div>
              </div>
              <div v-else-if="selectedModule === 'parkingstall'" class="flex">
                <div class="p10 pL0 pT0">
                  <el-select
                    v-model="selectedFilter[selectedModule]['status']"
                    filterable
                    default-first-option
                    placeholder="Status"
                    size="mini"
                    @change="searchContent"
                    class="fc-input-full-border-select2 round-select"
                  >
                    <el-option
                      v-for="(filter, index) in filtersOptions[selectedModule][
                        'status'
                      ]"
                      :key="index"
                      :label="filter.name"
                      :value="filter.value"
                      no-data-text="No filter"
                      clearable
                    ></el-option>
                  </el-select>
                </div>
              </div>
            </div>
          </div>
        </template>
        <div class="sidebarhide" @click="handleSideBar()">
          <div class="sidebarhide-bar">
            <i class="el-icon-arrow-right" v-if="hidesidebar"></i>
            <i class="el-icon-arrow-left" v-else></i>
          </div>
        </div>
      </div>
      <template v-if="isLoading && firstTimeLoad">
        <div
          id="fp-result-view"
          class="fc-search-result-con list-container mT15 fp-result-list"
        >
          <spinner :show="isLoading" size="80"></spinner>
        </div>
      </template>
      <template v-else-if="Object.keys(serachResult).length && firstTimeLoad">
        <div
          id="fp-result-view"
          v-on:scroll.passive="onScroll"
          v-bind:class="{ 'all-view': selectedModule === 'all' }"
          class="fc-search-result-con list-container mT15 fp-result-list"
        >
          <div
            class="fc-pink f12 text-uppercase pT15 bold"
            v-if="serachResult['employee']"
          >
            Employee
          </div>
          <template v-if="viewerMode === 'ASSIGNMENT'">
            <div
              v-for="(employee, index) in serachResult['employee']"
              :key="index"
            >
              <el-row
                class="field-row flex flex-row list-borader-bottom align-center"
              >
                <el-col :span="2" class="self-center">
                  <div
                    class="task-handle mR5 pointer"
                    @click="actionEmployee(employee)"
                  >
                    <avatar size="md" :user="employee"></avatar>
                  </div>
                </el-col>
                <el-col :span="20" class="pL15">
                  <div
                    class="fc-black-14 text-left fc-rightside-ico pinter"
                    @click="actionEmployee(employee)"
                  >
                    <div>
                      {{ employee.name || '' }}
                    </div>

                    <div class="flex flex-row pT5">
                      <div
                        class="flex flex-row pR10"
                        v-if="employee && employee.space"
                      >
                        <div>
                          <inline-svg
                            src="svgs/office_desk"
                            iconClass="deskFill icon-path-fill vertical-bottom"
                          ></inline-svg>
                        </div>
                        <div
                          class="fc-black-12 fw4 text-left break-word line-height18 truncate-text pL3"
                          v-tippy="{
                            arrow: true,
                            arrowType: 'round',
                            animation: 'fade',
                          }"
                          :content="employee.space.name"
                          style="max-width: 100px;"
                        >
                          {{ employee.space.name }}
                        </div>
                      </div>
                      <div
                        class="flex-middle"
                        v-if="employee.data && employee.data.department"
                      >
                        <div>
                          <inline-svg
                            src="svgs/department2"
                            iconClass="icon icon-sm icon-path-fill mR5"
                          >
                          </inline-svg>
                        </div>
                        <div
                          class="fc-black-12 fw4 text-left break-word line-height18 truncate-text"
                          style="max-width: 150px;"
                          v-tippy="{
                            arrow: true,
                            arrowType: 'round',
                            animation: 'fade',
                          }"
                          :content="employee.data.department.name"
                        >
                          {{
                            employee.data && employee.data.department
                              ? employee.data.department.name
                              : ''
                          }}
                        </div>
                      </div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="2" class="cursor-drag" v-if="showAssignOption">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content="Drag and drop employee"
                    placement="top-start"
                  >
                    <div
                      class="more-icon-contianer"
                      @mousedown="handleMouseDown($event, employee)"
                      @mouseup="handleEmpMouseUp(employee)"
                      @mousemove="handleMouseMove($event, employee)"
                    >
                      <inline-svg src="svgs/header-more"></inline-svg>
                      <inline-svg
                        src="svgs/header-more"
                        style="bottom: 12px;"
                      ></inline-svg>
                    </div>
                  </el-tooltip>
                </el-col>
              </el-row>
            </div>
          </template>
          <template v-else>
            <div
              v-for="(employee, index) in serachResult['employee']"
              :key="index"
              @click="actionEmployee(employee)"
            >
              <el-row
                class="field-row flex flex-row list-borader-bottom align-center"
              >
                <el-col :span="2" class="self-center">
                  <div class="task-handle mR5 pointer">
                    <avatar size="md" :user="employee"></avatar>
                  </div>
                </el-col>
                <el-col :span="22" class="pL15">
                  <div class="fc-black-14 text-left fc-rightside-ico pointer">
                    <div>
                      {{ employee.name || '' }}
                    </div>

                    <div class="flex flex-row pT5">
                      <div
                        class="flex flex-row pR10"
                        v-if="employee && employee.space"
                      >
                        <div>
                          <inline-svg
                            src="svgs/office_desk"
                            iconClass="deskFill icon-path-fill vertical-bottom"
                          ></inline-svg>
                        </div>
                        <div
                          class="fc-black-12 fw4 text-left break-word line-height18 truncate-text pL3"
                          v-tippy="{
                            arrow: true,
                            arrowType: 'round',
                            animation: 'fade',
                          }"
                          :content="employee.space.name"
                          style="max-width: 100px;"
                        >
                          {{ employee.space.name }}
                        </div>
                      </div>
                      <div
                        class="flex-middle"
                        v-if="employee.data && employee.data.department"
                      >
                        <div>
                          <inline-svg
                            src="svgs/department2"
                            iconClass="icon icon-sm icon-path-fill mR5"
                          >
                          </inline-svg>
                        </div>
                        <div
                          class="fc-black-12 fw4 text-left break-word line-height18 truncate-text"
                          style="max-width: 150px;"
                          v-tippy="{
                            arrow: true,
                            arrowType: 'round',
                            animation: 'fade',
                          }"
                          :content="employee.data.department.name"
                        >
                          {{
                            employee.data && employee.data.department
                              ? employee.data.department.name
                              : ''
                          }}
                        </div>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>

          <div
            class="fc-pink f12 text-uppercase pT15 bold"
            v-if="serachResult['desks']"
          >
            Desks
          </div>
          <div
            v-for="(desk, index) in serachResult['desks']"
            :key="'desk' + index"
            @click="emitData('desks', desk)"
          >
            <el-row
              class="field-row flex flex-row list-borader-bottom align-center pT15 pB15"
            >
              <el-col :span="2" class="self-center">
                <div class="task-handle mR5 pointer">
                  <div class="fp-search-icons">
                    <inline-svg
                      src="svgs/office_desk"
                      iconClass="icon text-center fp-icon-inside icon-xxl"
                    ></inline-svg>
                  </div>
                </div>
              </el-col>
              <el-col :span="22" class="pL10">
                <div class="bold fc-black-14 text-left fc-rightside-ico">
                  <div>
                    {{ desk.name || '' }}
                  </div>

                  <div class="flex-middle justify-content-space">
                    <div class="flex-middle">
                      <div class="fc-black-12 fw4 pT5" style="color: #a3a6b1;">
                        {{ deskType[desk.deskType] }}
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div
            class="fc-pink f12 text-uppercase pT15 bold"
            v-if="serachResult['space']"
          >
            Rooms
          </div>
          <div
            v-for="(data, index) in serachResult['space']"
            :key="'space' + index"
            @click="emitData('space', data)"
          >
            <el-row
              class="field-row flex flex-row list-borader-bottom align-center pT15 pB15"
            >
              <el-col :span="2" class="self-center">
                <div class="task-handle mR5 pointer">
                  <div class="fp-search-icons">
                    <inline-svg
                      src="svgs/room1"
                      iconClass="icon text-center icon-md  fp-icon-inside"
                    ></inline-svg>
                  </div>
                </div>
              </el-col>
              <el-col :span="22" class="pL10">
                <div class="bold fc-black-14 text-left fc-rightside-ico">
                  <div class="flex-middle">
                    <div>
                      {{ data.name || '' }}
                    </div>
                    <div class="fc-room-tag" v-if="data.isReservable">
                      <el-tag>Reservable</el-tag>
                    </div>
                  </div>

                  <div class="pT5 flex-middle">
                    <i
                      class="el-icon-user-solid f14"
                      style="color: #a9aacb;"
                    ></i>
                    <div class="pL5 fc-black-12 fw4">
                      {{ data.maxOccupancy > 0 ? data.maxOccupancy : 0 }}
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div
            class="fc-pink f12 text-uppercase pT15 bold"
            v-if="serachResult['lockers']"
          >
            Lockers
          </div>
          <div
            v-for="(data, index) in serachResult['lockers']"
            :key="'lockers' + index"
            @click="emitData('space', data)"
          >
            <el-row
              class="field-row flex flex-row list-borader-bottom align-center pT15 pB15"
            >
              <el-col :span="2" class="self-center">
                <div class="task-handle mR5 pointer">
                  <div class="fp-search-icons">
                    <inline-svg
                      src="svgs/locker"
                      iconClass="icon text-center icon-md  fp-icon-inside"
                    ></inline-svg>
                  </div>
                </div>
              </el-col>
              <el-col :span="22" class="pL10">
                <div class="bold fc-black-14 text-left fc-rightside-ico">
                  <div>
                    {{ data.name || '' }}
                  </div>

                  <div class="flex-middle justify-content-space">
                    <div class="flex-middle">
                      <div
                        class="fc-black-12 fw4"
                        v-if="data.data && data.data.singleline"
                      >
                        {{ data.data.singleline }}
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div
            class="fc-pink f12 text-uppercase pT15 bold"
            v-if="serachResult['parkingstall']"
          >
            Parkings
          </div>
          <div
            v-for="(data, index) in serachResult['parkingstall']"
            :key="'parkingstall' + index"
            @click="emitData('space', data)"
          >
            <el-row
              class="field-row flex flex-row list-borader-bottom align-center pT15 pB15"
            >
              <el-col :span="2" class="self-center">
                <div class="task-handle mR5 pointer">
                  <div class="fp-search-icons">
                    <inline-svg
                      src="svgs/parking"
                      iconClass="icon text-center icon-md  fp-icon-inside"
                    ></inline-svg>
                  </div>
                </div>
              </el-col>
              <el-col :span="22" class="pL10">
                <div class="bold fc-black-14 text-left fc-rightside-ico">
                  <div>
                    {{ data.name || '' }}
                  </div>

                  <div class="flex-middle justify-content-space">
                    <div class="flex-middle">
                      <div class="fc-black-12 fw4" v-if="data.parkingType">
                        {{ parkingtypeMap[data.parkingType] }}
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </template>
      <template v-else-if="firstTimeLoad">
        <div
          id="fp-result-view"
          class="fc-search-result-con list-container mT15 fp-result-list nodata text-center"
        >
          <inline-svg
            src="svgs/floor-plan-empty"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="fwBold">
            No search results found
          </div>
        </div>
      </template>
    </div>
    <el-dialog
      title="SWITCH FLOOR"
      :visible.sync="openFloorPlanDialog"
      width="30%"
      :before-close="closeDialog"
      class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <div class="height150">
        <div class="label-txt-black" v-if="openfloorplanData">
          <span v-if="openfloorplanData.loading">
            <spinner :show="true" size="80"></spinner>
          </span>
          <span v-else>
            {{ openfloorplanData.message }}
          </span>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="openFloorPlanDialog = false" class="modal-btn-cancel"
          >{{ 'CANCEL' }}
        </el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="openFloorPlan()"
          >{{ 'SWITCH' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import Avatar from '@/Avatar'
import debounce from 'lodash/debounce'
import { API } from '@facilio/api'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  props: [
    'visible',
    'deskList',
    'loading',
    'departments',
    'viewerMode',
    'spaceList',
    'floorId',
    'parkings',
    'lockers',
    'floorplanId',
  ],
  components: {
    Avatar,
  },
  data() {
    return {
      selectedDragEmployee: null,
      searchLimit: 10,
      firstTimeLoad: true,
      openFloorPlanDialog: false,
      openfloorplanData: null,
      hidesidebar: true,
      deskType: { 1: 'Assignable', 2: 'Hoteling', 3: 'Hot' },
      spaceCategories: [],
      scrollPosition: 0,
      selectedModule: 'all',
      focused: true,
      isLoading: false,
      isDragging: false,
      mouseDown: false,
      searchpopover: false,
      search: null,
      showSearchbox: false,
      employelistview: true,
      showHidesearchIcon: true,
      unassigned: false,
      searchBy: {
        employee: true,
        desk: false,
        room: false,
        parking: false,
      },
      fieldSectionDragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
      activeSearchby: ['1'],
      searchMosules: [
        {
          name: 'Employee',
          value: 'employee',
          icon: 'svgs/person',
          iconClass: 'deskFill icon-path-fill align-top',
        },
        {
          name: 'Desk',
          value: 'desks',
          icon: 'svgs/office_desk',
          iconClass: 'deskFill icon-path-fill vertical-bottom',
        },
        {
          name: 'Room',
          value: 'space',
          icon: 'svgs/room2',
          iconClass: 'roomFill icon-path-fill',
        },
        {
          name: 'Locker',
          value: 'lockers',
          icon: 'svgs/locker',
          iconClass: 'lockerFill icon-path-fill',
        },
        {
          name: 'Parking Lot',
          value: 'parkingstall',
          icon: 'svgs/parking',
          iconClass: 'parkingFill icon-path-fill',
        },
      ],
      employeLoading: false,
      empList: [],
      dskList: [],
      filtersOptions: {
        employee: {
          assignment: [
            { name: 'All', value: 'all' },
            { name: 'Seated', value: 'asssigned' },
            { name: 'Unseated', value: 'unasssigned' },
          ],
          department: [],
        },
        desks: {
          desktypes: [
            { name: 'All', value: -1 },
            { name: 'Assignable', value: 1 },
            { name: 'Hoteling', value: 2 },
            { name: 'Hot', value: 3 },
          ],
        },
        space: {
          capcity: [
            { name: 'Capacity', value: -1 },
            { name: '1', value: 1 },
            { name: '2', value: 2 },
            { name: '3', value: 3 },
            { name: '4', value: 4 },
            { name: '5', value: 5 },
            { name: '6', value: 6 },
            { name: '7', value: 7 },
            { name: '8', value: 8 },
            { name: '9', value: 9 },
            { name: '10+', value: 10 },
          ],
        },
        lockers: {
          status: [
            { name: 'All', value: -1 },
            { name: 'Vacant', value: 1 },
            { name: 'Occupied', value: 2 },
          ],
        },
        parkingstall: {
          status: [
            { name: 'All', value: -1 },
            { name: 'Vacant', value: 'vacant' },
            { name: 'Occupied', value: 'occupied' },
          ],
        },
      },
      selectedFilter: {
        employee: { assignment: 'unasssigned' },
        desks: { desktype: 1 },
        lockers: { status: 1 },
        parkingstall: { status: 1 },
        space: { capcity: -1 },
      },
      spacesList: [],
      lockersList: [],
      parkingstallList: [],
      parkingStatus: [],
    }
  },
  created() {
    this.loadParkingStatus()
  },
  mounted() {
    this.getSideBarDetails()
    this.getSpaceCatgory()
    if (this.viewerMode === 'ASSIGNMENT') {
      this.selectedModule = 'employee'
    } else if (this.viewerMode === 'BOOKING') {
      this.selectedFilter['desks']['desktype'] = -1
      this.selectedModule = 'all'
      this.firstTimeLoad = false
      this.focused = false
    }
    this.searchData()
    this.initEvents()
  },
  computed: {
    ...mapState('webtabs', {
      currentTab: state => state.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),

    hasAssignPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('ASSIGN', currentTab)
    },
    parkingIdvsName() {
      let map = {}
      if (this.parkings && this.parkings.length) {
        this.parkings.forEach(parking => {
          this.$set(map, parking.id, parking.name)
        })
      }
      return map
    },
    locakerIdvsName() {
      let map = {}
      if (this.lockers && this.lockers.length) {
        this.lockers.forEach(locker => {
          this.$set(map, locker.id, locker.name)
        })
      }
      return map
    },
    parkingtypeMap() {
      return {
        1: 'Parking Stall',
        2: 'Handicap',
        3: 'Carpool',
      }
    },
    deskCategoryId() {
      let deskCategory = this.spaceCategories.find(rt => rt.name === 'Desk')
      if (deskCategory) {
        return deskCategory.id
      }
      return null
    },
    count() {
      return this.empList.length
    },
    serachResult() {
      let result = {}
      let { empList, dskList, spacesList, lockersList, parkingstallList } = this
      if (empList && empList.length) {
        result['employee'] = this.applySearchLimit(empList)
      }
      if (dskList && dskList.length) {
        result['desks'] = this.applySearchLimit(dskList)
      }
      if (spacesList && spacesList.length) {
        result['space'] = this.applySearchLimit(
          this.applyOthermoduleFilters(spacesList)
        )
      }
      if (lockersList && lockersList.length) {
        result['lockers'] = this.applySearchLimit(lockersList)
      }
      if (parkingstallList && parkingstallList.length) {
        result['parkingstall'] = this.applySearchLimit(parkingstallList)
      }
      return result
    },
    getModuleDisplayName() {
      let { searchMosules, selectedModule } = this
      if (selectedModule === 'all') {
        return 'All'
      }
      if (searchMosules && selectedModule) {
        return this.searchMosules.find(rt => rt.value === selectedModule).name
      }
      return 'All'
    },
    employeeList() {
      let { deskList, empList } = this
      if (deskList && deskList.length) {
        let employeeMap = {}
        let deskName = {}
        deskList.forEach(rt => {
          if (rt.employee && rt.employee.id) {
            this.$set(employeeMap, rt.employee.id, rt.id)
            this.$set(deskName, rt.employee.id, rt)
          }
        })
        empList.forEach(rt => {
          if (employeeMap[rt.id]) {
            this.$set(rt, 'desk', deskName[rt.id])
          }
        })
        return empList
      }
      return []
    },
    hasViewPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('VIEW', currentTab)
    },
    showAssignOption() {
      if (this.hasViewPermission) {
        if (this.hasAssignPermission) {
          return true
        }
      }
      return false
    },
  },
  beforeDestroy() {
    eventBus.$off('REFRESH_EMPLOYEE_LIST', () => {})
  },
  methods: {
    initEvents() {
      eventBus.$on('REFRESH_EMPLOYEE_LIST', this.handleEmployeeRefresh)
    },
    handleEmployeeRefresh() {
      console.log('======>')
      this.searchEmployee(this.search)
    },
    async loadParkingStatus() {
      API.get(`v2/state/list?parentModuleName=parkingstall`).then(
        ({ data }) => {
          this.parkingStatus = data.status || []
        }
      )
    },
    applyOthermoduleFilters(spaceList) {
      if (spaceList && spaceList.length) {
        return spaceList.filter(space => {
          if (this.parkingIdvsName[space.id]) {
            return null
          } else if (this.locakerIdvsName[space.id]) {
            return null
          }
          return space
        })
      }
    },
    applySearchLimit(data) {
      if (this.searchLimit && data.length) {
        // data.length = this.searchLimit
        data.splice(this.searchLimit)
      }
      return data
    },
    openFloorPlan() {
      if (this.openfloorplanData && this.openfloorplanData.indoorFloorPlanId) {
        this.$router.push({
          path: `/employee/floorplan/floor-map/indoorfloorplan/${this.openfloorplanData.floorId}/${this.openfloorplanData.indoorFloorPlanId}`,
          query: { deskId: this.openfloorplanData.deskId },
        })
        // window.open(routeData.href, '_blank')
        this.closeDialog()
      }
    },
    closeDialog() {
      this.openFloorPlanDialog = false
    },
    actionEmployee(employee) {
      this.openfloorplanData = {}
      if (employee && employee.space && employee.space.id) {
        let { floor } = employee.space
        if (floor && floor.id) {
          if (floor.id === this.floorId) {
            this.emitData('desks', employee.space)
          } else {
            let data = {
              deskId: employee.space.id,
              deskName: employee.space.name,
              employeeName: employee.name,
              floorId: floor.id,
              message: '',
              loading: true,
              indoorFloorPlanId: -1,
            }
            this.openfloorplanData = data

            this.openFloorPlanDialog = true
            this.getFloor()
          }
        }
      }
    },
    async getFloor() {
      let { floorId } = this.openfloorplanData

      let params = { floorId: floorId }

      let { data } = await API.post(`v2/floor/details`, params)
      if (data) {
        let { floor } = data
        let { building } = floor
        this.openfloorplanData.indoorFloorPlanId = floor.indoorFloorPlanId
        let { employeeName } = this.openfloorplanData
        this.openfloorplanData.message = `${employeeName} is located at ${
          floor.name
        } ${
          building && building.name ? ', ' + building.name : ''
        }. Do you want to switch the floor plan?`
      }
      this.openfloorplanData.loading = false
    },
    openFloorPlanAndDesk() {},
    getSideBarDetails() {
      let sidebar = window.localStorage.getItem('searchSideBar')
      if (sidebar && sidebar === 'hide') {
        this.hidesidebar = true
      } else {
        this.hidesidebar = false
      }
    },
    handleSideBar() {
      this.hidesidebar = !this.hidesidebar
      if (this.hidesidebar) {
        window.localStorage.setItem('searchSideBar', 'hide')
      } else {
        window.localStorage.setItem('searchSideBar', 'show')
      }
    },
    emitData(modulesName, data) {
      this.$emit('focus', modulesName, data)
    },
    switchModule(module) {
      this.search = null
      this.empList = []
      this.dskList = []
      this.spacesList = []
      this.lockersList = []
      this.parkingstallList = []
      this.selectedModule = module.value
      this.searchpopover = false
      this.searchData()
    },
    onScroll() {
      this.scrollPosition = document.querySelector('#fp-result-view').scrollTop
    },
    blurSearchBox() {
      // if (!this.search) {
      //   this.focused = false
      // }
    },
    getData() {
      this.employeeSearch(this.search)
    },
    async getAll(searchText) {
      this.isLoading = true
      this.dskList = []

      let params = {
        search: searchText,
      }

      let { data } = await API.get(
        `v3/floorplan/search/${this.floorplanId}`,
        params
      )
      if (data?.records) {
        data.records.forEach(records => {
          let { name } = records
          if (name === 'desks' && records?.data) {
            this.dskList = records.data
          }
          if (name === 'space' && records?.data) {
            this.spacesList = records.data
          }
          if (name === 'lockers' && records?.data) {
            this.lockersList = records.data
          }
          if (name === 'parkingstall' && records?.data) {
            this.parkingstallList = records.data
          }
          if (name === 'employee' && records?.data) {
            this.empList = records.data
          }
        })
      }
      this.isLoading = false
    },
    searchData() {
      this.firstTimeLoad = true
      this.focused = true
      if (this.selectedModule === 'employee') {
        this.employeeSearch(this.search)
      } else if (this.selectedModule === 'desks') {
        this.searchDesks(this.search)
      } else if (this.selectedModule === 'space') {
        this.searchSpaces(this.search)
      } else if (this.selectedModule === 'lockers') {
        this.searchLocalLockers(this.search)
      } else if (this.selectedModule === 'parkingstall') {
        this.searchParkings(this.search)
      } else if (this.selectedModule === 'all') {
        this.getAll(this.search)
      }
    },
    searchContent: debounce(function() {
      this.searchData()
    }, 1000),
    handleMouseMove($event, employee) {
      if (this.mouseDown && !this.isDragging) {
        this.onselectPrevent()
        this.$emit('dragElement', $event, employee)
        this.isDragging = true
        this.mouseDown = false
      }
    },
    mouseDragClickAction() {},
    handleEmpMouseUp(employee) {
      if (this.isDragging) {
        this.mouseDragClickAction()
        this.isDragging = false
      } else {
        this.actionEmployee(employee)
      }
      this.mouseDown = false
      this.onselectEnable()
    },
    handleMouseDown() {
      this.onselectPrevent()
      this.mouseDown = true
    },
    handleMouseUP() {
      if (this.isDragging) {
        this.isDragging = false
        this.onselectEnable()
      }
    },
    onselectPrevent() {
      document.onselectstart = () => {
        return false // cancel selection
      }
    },
    onselectEnable() {
      document.onselectstart = () => {
        return true // cancel selection
      }
    },
    handleMouseLeave() {
      if (this.isDragging) {
        this.handleClose()
      }
    },
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
      this.isDragging = false
    },
    hideSearchBox() {
      this.showSearchbox = false
      this.showHidesearchIcon = true
    },
    clickToShowSearchBox() {
      ;(this.showSearchbox = true), (this.showHidesearchIcon = false)
    },
    async getSpaceCatgory() {
      let params = {
        moduleName: 'spacecategory',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
      }
      let { data } = await API.get(`v3/modules/data/list`, params)
      if (data) {
        this.spaceCategories = data.spacecategory
      }
    },
    async searchLocaker(searchText) {
      let filters = {
        name: { operatorId: 5, value: [searchText] },
      }
      let params = {
        moduleName: 'lockers',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
      }
      if (searchText) {
        params = {
          moduleName: 'lockers',
          page: 1,
          perPage: this.searchLimit,
          includeParentFilter: true,
          filters: JSON.stringify(filters),
        }
      }
      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.lockersList = data.moduleDatas
        }
      })
    },
    async searchparkingstall(searchText) {
      let filters = {
        name: { operatorId: 5, value: [searchText] },
      }
      let params = {
        moduleName: 'parkingstall',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
      }
      if (searchText) {
        params = {
          moduleName: 'parkingstall',
          page: 1,
          perPage: this.searchLimit,
          includeParentFilter: true,
          filters: JSON.stringify(filters),
        }
      }
      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.parkingstallList = data.moduleDatas
        }
      })
    },
    // async searchDeskss(searchText) {
    //   let { selectedFilter, selectedModule } = this
    //   this.isLoading = true
    //   this.dskList = []
    //   let filters = {
    //     deskType: {
    //       operatorId: 54,
    //       value: [String(selectedFilter[selectedModule]['desktype'])],
    //     },
    //   }
    //   let params = {
    //     moduleName: 'desks',
    //     page: 1,
    //     perPage: this.searchLimit,
    //     includeParentFilter: true,
    //     filters: JSON.stringify(filters),
    //   }
    //   if (searchText) {
    //     filters['name'] = { operatorId: 5, value: [searchText] }
    //     params = {
    //       moduleName: 'desks',
    //       page: 1,
    //       perPage: this.searchLimit,
    //       includeParentFilter: true,
    //       filters: JSON.stringify(filters),
    //     }
    //   }

    //   await API.get(`v2/module/data/list`, params).then(({ data }) => {
    //     if (data) {
    //       this.dskList = data.moduleDatas
    //     }
    //   })
    //   this.isLoading = false
    // },
    async searchDesks(searchText) {
      this.isLoading = true
      let { selectedFilter, selectedModule } = this
      this.dskList = []

      let params = {
        search: searchText,
        moduleName: 'desks',
      }
      if (selectedFilter[selectedModule]['desktype'] !== -1) {
        params[
          'filters'
        ] = `{\"deskType\":{\"operatorId\":54,\"value\":[\"${selectedFilter[selectedModule]['desktype']}\"]}}`
      }
      let { data } = await API.get(
        `v3/floorplan/search/${this.floorplanId}`,
        params
      )

      if (data) {
        this.dskList = this.getRecords('desks', data)
      }
      this.isLoading = false
    },
    async searchLocalLockers(searchText) {
      this.isLoading = true
      let { selectedFilter, selectedModule } = this
      this.lockersList = []

      let params = {
        search: searchText,
        moduleName: selectedModule,
        includeParentFilter: true,
      }
      if (selectedFilter[selectedModule]['status'] !== -1) {
        let filters = {
          employee: { operatorId: selectedFilter[selectedModule]['status'] },
        }
        params['filters'] = JSON.stringify(filters)
        params.includeParentFilter = true
      }
      let { data } = await API.get(
        `v3/floorplan/search/${this.floorplanId}`,
        params
      )

      if (data) {
        this.lockersList = this.getRecords(selectedModule, data)
      }
      this.isLoading = false
    },
    async searchParkings(searchText) {
      this.isLoading = true
      let { selectedFilter, selectedModule } = this
      this.parkingstallList = []

      let params = {
        search: searchText,
        moduleName: selectedModule,
      }
      if (selectedFilter[selectedModule]['status'] !== -1) {
        let statusId = this.parkingStatus.find(
          rt => rt.name === selectedFilter[selectedModule]['status']
        )
        if (statusId) {
          params[
            'filters'
          ] = `{\"moduleState\":{\"operatorId\":36,\"value\":[\"${statusId}\"]}}`
        }
      }

      let { data } = await API.get(
        `v3/floorplan/search/${this.floorplanId}`,
        params
      )

      if (data) {
        this.parkingstallList = this.getRecords(selectedModule, data)
      }
      this.isLoading = false
    },
    async searchSpaces(searchText) {
      this.isLoading = true
      let { selectedFilter, selectedModule } = this
      this.spacesList = []

      let params = {
        search: searchText,
        moduleName: selectedModule,
      }
      if (selectedFilter[selectedModule]['capcity'] !== -1) {
        params[
          'filters'
        ] = `{\"maxOccupancy\":{\"operatorId\":9,\"value\":[\"${selectedFilter[selectedModule]['capcity']}\"]}}`
      }
      let { data } = await API.get(
        `v3/floorplan/search/${this.floorplanId}`,
        params
      )

      if (data) {
        this.spacesList = this.getRecords(selectedModule, data)
      }
      this.isLoading = false
    },
    async searchSpace(searchText) {
      let { selectedFilter, selectedModule } = this
      let filters = {
        spaceCategory: { operatorId: 37, value: [`${this.deskCategoryId}`] },
      }

      if (selectedFilter[selectedModule]['capcity']) {
        filters['maxOccupancy'] = {
          operatorId: 12,
          value: ['' + selectedFilter[selectedModule]['capcity']],
        }
      }
      let params = {
        moduleName: 'space',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
        filters: JSON.stringify(filters),
      }
      if (searchText) {
        filters['name'] = { operatorId: 5, value: [searchText] }
        params = {
          moduleName: 'space',
          page: 1,
          perPage: this.searchLimit,
          includeParentFilter: true,
          filters: JSON.stringify(filters),
        }
      }
      return await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.spacesList = data.moduleDatas
        }
      })
    },
    async searchEmployee(searchText) {
      let filters = {}

      let params = {
        moduleName: 'employee',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
        filters: JSON.stringify(filters),
      }
      if (searchText) {
        filters['name'] = { operatorId: 5, value: [searchText] }
        params = {
          moduleName: 'employee',
          page: 1,
          perPage: this.searchLimit,
          includeParentFilter: true,
          filters: JSON.stringify(filters),
        }
      }

      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.empList = data.moduleDatas
        }
      })
    },
    async employeeSearch(searchText, loadAll) {
      let { selectedFilter, selectedModule } = this
      this.isLoading = true
      this.empList = []

      let filters = {}

      // department: {
      //     operatorId: 35,
      //     lookupOperatorId: 5,
      //     field: 'name',
      //     value: [searchText],
      //     orFilters: [{ field: 'name', operatorId: 5 }],
      //   },

      if (!loadAll) {
        if (selectedFilter[selectedModule]['assignment'] === 'asssigned') {
          filters = {
            desks: {
              operatorId: 88,
              relatedFieldName: 'employee',
              relatedOperatorId: 2,
              filterFieldName: 'employee',
              value: ['test'],
            },
          }
        } else if (selectedFilter[selectedModule]['assignment'] === 'all') {
          filters = {}
        } else {
          filters = {
            desks: {
              operatorId: 92,
              relatedFieldName: 'employee',
              relatedOperatorId: 2,
              filterFieldName: 'employee',
              value: ['test'],
            },
          }
        }

        if (selectedFilter[selectedModule]['department']) {
          filters['department'] = {
            operatorId: 36,
            value: [String(selectedFilter[selectedModule]['department'])],
          }
        }
      }

      let params = {
        moduleName: 'employee',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
        filters: JSON.stringify(filters),
      }
      if (searchText) {
        filters['name'] = { operatorId: 5, value: [searchText] }
        params = {
          moduleName: 'employee',
          page: 1,
          perPage: this.searchLimit,
          includeParentFilter: true,
          filters: JSON.stringify(filters),
        }
      }
      this.employeLoading = true

      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.empList = data.moduleDatas
        }
        this.employeLoading = false
      })
      this.isLoading = false
    },
    async getEmployee() {
      this.empList = []
      let params = {
        moduleName: 'employee',
        page: 1,
        perPage: this.searchLimit,
        includeParentFilter: true,
      }

      let filters = {
        desks: {
          operatorId: 92,
          relatedFieldName: 'employee',
          filterFieldName: 'employee',
          relatedOperatorId: 2,
          value: ['test'],
        },
      }

      if (this.unassigned) {
        params = {
          page: 1,
          perPage: this.searchLimit,
          includeParentFilter: true,
          moduleName: 'employee',
          filters: JSON.stringify(filters),
        }
      }
      this.employeLoading = true

      let { data } = await API.get(`v2/module/data/list`, params)
      if (data) {
        this.empList = data.moduleDatas
      }
      this.employeLoading = false
    },
    getRecords(moduleName, data) {
      if (data?.records) {
        let list = data.records.find(rt => rt.name === moduleName)
        if (list?.data && list?.data.length) {
          return list.data
        }
      }
      return []
    },
  },
}
</script>
<style scoped>
.label-txt-black {
  line-height: 1.5;
  word-break: initial;
}
</style>
<style lang="scss">
.more-icon-contianer {
  transform: rotate(90deg);
  width: 0px;
  position: relative;
  bottom: 10px;
  opacity: 0.5;
}
.fp-search-container {
  position: absolute;
  left: 0;
  top: 0;
  z-index: 1;
  cursor: pointer;
}
.indoor-fp-sidebar {
  background: #fff;
  border-right: 1px solid #eee;
  overflow: hidden;
}

.left-sidebar-mapbox {
  //position: absolute;
  z-index: 1;
  // transform: translateX(-295px);
  overflow-x: hidden;
  transition: transform 3s;
}

.list-container {
  height: calc(100vh - 260px);
  overflow-y: scroll;
  overflow-x: hidden;
}
.list-container .all-view {
  height: calc(100vh - 215px);
}

.indoor-fp-sidebar .filter-config-right {
  width: 100%;
  padding: 18px 0;
}

.space-search-block {
  height: 35px;
}

.fc-empty-icon-sidebar {
  height: 60vh;
  display: flex;
  align-content: center;
  justify-content: center;
  flex-direction: column;
}

.db-user-filter-manager-dialog .field-row {
  width: 100%;
  max-height: 100%;
  padding: 0 18px;
  box-shadow: none;
  border: none;
  margin-top: 0;
  margin-bottom: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.fc-employee-sidebar {
  width: 334px;
  position: absolute;
  z-index: 1;
}

.fc-floor-search-align {
  padding: 0 18px;
}

.fc-rightside-ico {
  font-size: 13px;
  width: 100%;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 0.98px;
  color: #324056;
}

.fc-floor-search-filter {
  box-shadow: 0 0 2px 0 rgb(0 0 0 / 15%);
  padding: 7px 10px;
  border-radius: 3px;

  .inline {
    position: relative;
    top: 4px;
  }
}

.fc-floor-search-popup {
  padding: 20px;
  border-radius: 6px;
}

.switch-border-bottom {
  padding: 15px 0 15px;
  border-bottom: solid 1px rgba(0, 0, 0, 0.1);

  .el-switch__core {
    width: 40px;
    height: 20px;

    &:after {
      width: 15px;
      height: 15px;
      top: 2px;
      left: 3px;
    }
  }
}

.fc-floor-empty-subtext {
  font-size: 10px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.61px;
  text-align: center;
  color: #b3b3b3;
}

.fc-floor-search-block {
  width: 335px;
  height: 190px;
  margin: 10px;
  border-radius: 8px;
  box-shadow: -2px 5px 7px 0 rgb(0 0 0 / 10%);
  background-color: #ffffff;
  position: relative;
  border-top-right-radius: 0;
}
.fc-search-bottom-border {
  padding-bottom: 5px;
}
.fc-search-txt {
  font-size: 13px;
  font-weight: 400;
  line-height: normal;
  letter-spacing: normal;
  color: #6b7e91;
}
.fc-search-filter-block {
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  .fc-search-ico-bg {
    width: 35px;
    height: 35px;
    cursor: pointer;
    display: flex;
    align-content: flex-start;
    justify-content: space-around;
    align-items: center;
    border-radius: 100%;
    box-shadow: 0 0 2px 0.5px rgba(77, 77, 77, 0.22);
    background-color: #ff3184;
  }
}
.fc-search-minimize {
  width: 25px;
  height: 25px;
  border-radius: 0 3px 3px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 7px 0 rgb(0 0 0 / 10%);
  background-color: #ffffff;
  position: absolute;
  z-index: 10;
  border-left: 1px solid #eee;
  right: -25px;
  cursor: pointer;
  color: rgb(0 0 0 / 60%);
  font-weight: bold;
  &:hover {
    background-color: #ff3184;
    i {
      color: #fff;
    }
  }
}
.fc-search-ico-align {
  text-align: center;
  display: flex;
  align-items: center;
  flex-direction: column;
}

.fc-search-group-btn {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  flex-direction: row;
}

.fc-search-round-btn {
  border-radius: 14px;
  border: solid 1px #d0d9e2;
  background: #fff;
  font-size: 11px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
  padding: 7px 11px !important;
  margin-bottom: 10px;
  margin-right: 9px;
  margin-left: 0 !important;
  &:hover {
    color: #fff;
    background-color: #ff3184;
    border: 1px solid #ff3184;
  }
}

.fc-search-round-btn-active {
  color: #fff;
  font-size: 11px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: normal;
  color: #ffffff;
  background-color: #ff3184 !important;
  border: 1px solid #ff3184 !important;
  &:hover {
    color: #807f7f !important;
    background: #fff !important;
    border: 1px solid #d8d8d8 !important;
  }
}
.fc-btn-grey {
  font-size: 12px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #324056;
  border-radius: 4px;
  padding: 8px 10px;
  background: #fff;
  border: solid 1px #e2e8ef;
}
.fc-search-filter-show {
  width: 100%;
  padding: 3px 0;
  margin-top: 10px;
  background-color: #fafafa;
  text-align: center;
  cursor: pointer;
  .el-icon-d-arrow-left {
    transform: rotate(270deg);
    color: rgb(0 0 0 / 40%);
  }
}
.fc-result-border-bot {
  border-bottom: 1px solid rgb(235 235 235 / 70%);
}
.fc-search-collapse {
  border-top: none;
  border-bottom: none;
  .el-collapse-item__header {
    font-size: 14px;
    color: #2d394d;
    font-weight: bold;
    border-bottom: none;
    height: inherit;
    line-height: inherit;
    margin-top: 15px;
  }
  .el-icon-arrow-right {
    font-size: 16px;
    transform: rotate(90deg);
    font-weight: bold;
  }
  .el-collapse-item__arrow.is-active {
    transform: rotate(-90deg);
  }
  .el-collapse-item__content {
    padding-bottom: 20px;
  }
}
.fc-group-name {
  font-size: 14px;
  color: #ee518f;
  font-weight: 500;
  padding: 5px 0;
  letter-spacing: 0.6px;
}
.module-choose:hover .el-icon-check {
  color: #39b3c2;
  font-weight: 700;
}
.check-active {
  color: #39b3c2;
  font-weight: 700;
}
.fc-input-full-border2-prefix .el-input__inner {
  padding-left: 30px !important;
}
.search-subtext {
  font-size: 12px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #b3b3b3;
  text-align: left;
  padding-left: 0px;
}
.round-select .el-input__inner {
  border-radius: 11px !important;
  height: 28px !important;
  line-height: 28px !important;
  font-size: 12px !important;
  max-width: 110px;
}

.round-select .el-input__inner::placeholder {
  font-size: 12px !important;
}
.round-select .el-select-dropdown__item {
  font-size: 12px !important;
}
.fp-filter-section {
}
.fp-search-area {
  padding: 15px 15px 0;
  position: relative;
}
.fp-result-list {
  padding: 15px;
  margin-top: 0;
  padding-top: 0;
}
.fc-search-box-shadow {
  box-shadow: 0 1px 7px 0 rgb(0 0 0 / 10%);
}
.nodata {
  text-align: center;
  margin-top: 20%;
}
.list-borader-bottom {
  padding-top: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgb(228 234 237 / 50%);
}
.fp-icon-inside {
  padding-left: 4px;
  padding-top: 4px;
  #shape,
  #Rectangle-path {
    fill: #39b3c2;
  }
}

.fp-search-icons {
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-floor-popup-menu {
  cursor: pointer;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid rgb(222 231 239 / 40%);
  &:hover {
    background: #f0f9fe;
  }
}
.deskFill {
  width: 14px;
  height: 17px;
  fill: rgba(0, 0, 0, 0.1);
}
.icon-path-fill #shape,
.icon-path-fill #Rectangle-path {
  fill: #a9aacb;
}
.sidebarhide {
  width: 30px;
  height: 40px;
  position: absolute;
  right: -29px;
  top: 0px;
}
.sidebarhide-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  height: 100%;
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
  box-shadow: 5px 0px 7px 0 rgb(0 0 0 / 10%);
  // border-left: 1px solid rgb(222 231 239 / 60%);
}
.sidebarhide-bar i {
  font-size: 15px;
  font-weight: bold;
  color: rgb(50 64 86 / 40%);
}
.fp-search-section.hide-section {
  visibility: visible;
  -webkit-transform: translate3d(-100%, 0, 0);
  transform: translate3d(-97%, 0, 0);
  transition: 0.3s;
}
.fp-search-section {
  visibility: visible;
  -webkit-transform: translate3d(0, 0, 0);
  transform: translate3d(0, 0, 0);
  transition: 0.3s;
}
.all-fill-color {
  fill: #a9aacb !important;
}
.border-bottom-line {
  cursor: pointer;
  border-bottom: 1px solid rgba(222, 231, 239, 0.4);
  &:hover {
    background: #f0f9fe;
  }
}
.fc-floorplan-room {
  .el-input__inner {
    padding-left: 15px !important;
  }
}
.fc-room-tag {
  .el-tag {
    border-radius: 2px;
    border: solid 1px rgb(84 163 109 / 20%);
    font-size: 10px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.39px;
    color: #54a36d;
    background: #ecfff2;
    height: inherit;
    padding: 1px 5px;
    margin-left: 10px;
  }
  .not-avilable {
    border-radius: 2px;
    border: solid 1px rgb(220 134 32 / 60%);
    font-size: 10px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: 0.39px;
    color: #dc8620;
    background: #fffaf3;
    height: inherit;
    padding: 1px 5px;
    margin-left: 10px;
  }
}
.width20px {
  width: 25px;
}
</style>
