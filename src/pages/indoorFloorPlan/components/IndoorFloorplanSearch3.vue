<template>
  <div
    class="fp-search-fixed-container"
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
          <!-- <div class="f20 pL10 self-centter pT3">
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
          </div> -->
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
import IndoorFloorplanSearch from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch.vue'

export default {
  extends: IndoorFloorplanSearch,
  data() {
    return {
      showSearchList: false,
    }
  },
}
</script>
<style>
.fp-search-fixed-container .fc-floor-search-block {
  width: 100%;
  height: 100% !important;
  margin: 0px;
  border-radius: 0px;
  box-shadow: none;
}
.fp-search-fixed-container .fp-result-list {
  height: calc(100vh - 200px);
}
.fp-search-fixed-container .round-select .el-input__inner {
  border-radius: 4px !important;
}
</style>
