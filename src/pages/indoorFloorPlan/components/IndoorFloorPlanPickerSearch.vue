<template>
  <div class="indoor-plan-search-con">
    <div class="custom_suffix_icon">
      <i class="el-icon-search el-input__icon"> </i>
    </div>
    <el-select
      v-model="selectedResult"
      :placeholder="$t('floorplan.search')"
      class="fc-input-full-border-select2 fp-picker-search"
      filterable
      remote
      clearable
      reserve-keyword
      :popper-append-to-body="false"
      :remote-method="remoteSearch"
      @clear="clearSearch"
    >
      <el-option-group
        v-for="(value, key) in serachResult"
        :key="key"
        :label="key"
      >
        <template v-if="key === 'Employee'">
          <el-option
            v-for="(employee, index) in value"
            :key="`employee-${index}`"
            :label="employee.name"
            :value="employee.id"
          >
            <div :key="`employee-${index}`" @click="actionEmployee(employee)">
              <el-row
                class="field-row flex flex-row list-borader-bottom align-center"
              >
                <el-col :span="2" class="self-center">
                  <div class="task-handle mR5 pointer">
                    <avatar size="md" :user="employee"></avatar>
                  </div>
                </el-col>
                <el-col :span="22" class="pL25">
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
          </el-option>
        </template>
        <template v-if="key === 'Desks'">
          <el-option
            v-for="(desk, index) in value"
            :key="`desks-${index}`"
            :label="desk.name"
            :value="desk.id"
          >
            <div :key="'desk' + index" @click="emitData('desks', desk)">
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
                        <div
                          class="fc-black-12 fw4 pT5"
                          style="color: #a3a6b1;"
                        >
                          {{ deskType[desk.deskType] }}
                        </div>
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-option>
        </template>
        <template v-if="key === 'Space'">
          <el-option
            v-for="(data, index) in value"
            :key="`space-${index}`"
            :label="data.name"
            :value="data.id"
          >
            <div :key="'space' + index" @click="emitData('space', data)">
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
          </el-option>
        </template>
        <template v-if="key === 'Lockers'">
          <el-option
            v-for="(data, index) in value"
            :key="`lockers-${index}`"
            :label="data.name"
            :value="data.id"
          >
            <div :key="'lockers' + index" @click="emitData('space', data)">
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
          </el-option>
        </template>
        <template v-if="key === 'Parkingstall'">
          <el-option
            v-for="(data, index) in value"
            :key="`parkingstall-${index}`"
            :label="data.name"
            :value="data.id"
          >
            <div :key="'parkingstall' + index" @click="emitData('space', data)">
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
          </el-option>
        </template>
      </el-option-group>
    </el-select>
  </div>
</template>
<script>
import IndoorFloorplanSearch from 'src/pages/indoorFloorPlan/components/IndoorFloorplanSearch.vue'
export default {
  extends: IndoorFloorplanSearch,
  data() {
    return {
      selectedResult: null,
      selectedModule: 'all',
      selectedFilter: {
        employee: { assignment: 'asssigned' },
        desks: { desktype: 1 },
        lockers: { status: 1 },
        parkingstall: { status: 1 },
        space: { capcity: -1 },
      },
    }
  },
  created() {
    this.searchData()
  },
  computed: {
    assignedEmployeeList() {
      let emplyeeList = []
      if (this.deskList.length) {
        this.deskList.forEach(desk => {
          if (desk.employee) {
            let emp = {
              ...desk.employee,
              desks: desk,
              floor: { id: this.floorId },
              space: desk,
            }
            this.$set(emp.space, 'floor', { id: this.floorId })

            emplyeeList.push(emp)
          }
        })
      }
      return emplyeeList
    },
    serachResult() {
      let result = {}
      let { empList, dskList, spacesList, lockersList, parkingstallList } = this
      if (empList && empList.length) {
        result['Employee'] = this.applySearchLimit(empList)
      }
      if (dskList && dskList.length) {
        result['Desks'] = this.applySearchLimit(dskList)
      }
      if (spacesList && spacesList.length) {
        result['Space'] = this.applySearchLimit(
          this.applyOthermoduleFilters(spacesList)
        )
      }
      if (lockersList && lockersList.length) {
        result['Lockers'] = this.applySearchLimit(lockersList)
      }
      if (parkingstallList && parkingstallList.length) {
        result['Parkingstall'] = this.applySearchLimit(parkingstallList)
      }
      return result
    },
  },
  methods: {
    clearSearch() {
      // need to implement clear
      this.$emit('unselectFeature')
      this.$emit('resetZoom')
    },
    remoteSearch(query) {
      this.search = query
      this.searchContent()
    },
    async searchLocalEmployee(searchText) {
      this.isLoading = true

      setTimeout(() => {
        let { assignedEmployeeList } = this
        this.empList = []
        let dataList = assignedEmployeeList.filter(emp => {
          if (searchText) {
            if (emp.name.toLowerCase().indexOf(searchText.toLowerCase()) > -1) {
              return emp
            }
            return null
          } else {
            return emp
          }
        })
        this.empList = dataList
        this.isLoading = false
      }, 1000)
    },
  },
}
</script>
<style lang="scss">
.indoor-plan-search-con {
  .el-select-group__wrap .el-select-group__title {
    font-size: 13px;
    font-weight: 600;
    color: #ff3184;
  }
  .el-select-dropdown__item {
    min-height: 60px !important;
  }
  .fp-picker-search {
    min-width: 250px;
    text-align: left;
  }
  .el-select-dropdown__item:hover .fp-search-icons {
    background-color: inherit;
  }
  .el-input__suffix .el-input__validateIcon {
    visibility: hidden;
  }
  .custom_suffix_icon {
    z-index: 10;
    position: absolute;
    right: 20px;
    color: #d7dadf;
  }
}
</style>
