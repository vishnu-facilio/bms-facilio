<template>
  <div class="floorplan-search-new">
    <div
      class="fp-search-area-new"
      v-bind:class="{ 'fc-search-box-shadow': scrollPosition > 2 }"
    >
      <div
        class="fc-search-bottom-border inline width100 inline-flex self-center"
      >
        <el-input
          size="medium"
          v-model="search"
          @input="searchContent()"
          @focus="
            () => {
              this.focused = true
              this.showSearchList = true
            }
          "
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
      </div>
      <template v-if="focused || Object.keys(serachResult).length">
        <div class="">
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
    <div v-if="showSearchList" class="search-result-component">
      <IndoorFloorplanObjectList
        :searchText="search"
      ></IndoorFloorplanObjectList>
    </div>
  </div>
</template>
<script>
import IndoorFloorplanSearch from 'pages/indoorFloorPlan/components/IndoorFloorplanSearch.vue'
import IndoorFloorplanObjectList from 'src/pages/indoorFloorPlan/IndoorFloorplanObjectList.vue'
import debounce from 'lodash/debounce'

export default {
  extends: IndoorFloorplanSearch,
  components: { IndoorFloorplanObjectList },
  data() {
    return {
      showSearchList: false,
    }
  },
  methods: {
    blurSearchBox: debounce(function() {
      this.showSearchList = false
    }, 1000),
  },
}
</script>
<style>
.fp-search-area-new {
  margin: 0px 30px;
}
.fp-search-area-new .fc-input-full-border2 .el-input__inner {
  height: 36px !important;
  width: 300px;
}
.fp-search-area-new .fc-input-full-border2 .el-input__inner:hover,
.fp-search-area-new .fc-input-full-border2 .el-input__inner:focus {
  border-color: #0053cc !important;
}
</style>
