<template>
  <div class="h100 white-background p20 overflow-hidden">
    <div>
      <div class="pB10 fc-black-14 text-left bold">
        {{ $t('floorplan.departments') }}
      </div>
      <div>
        <el-input
          size="medium"
          v-model="search"
          class="width100 fc-input-full-border2 fc-input-full-border2-prefix wpa-search"
          placeholder="Search"
          clearable
          @input="searchContent"
        >
          <i
            slot="prefix"
            class="el-input__icon pointer fc-grey8 f14 fwBold el-icon-search mR5"
          ></i>
        </el-input>
      </div>
    </div>
    <div class="pT20 checkbox-containter overflo-auto h100" v-if="!loading">
      <el-checkbox
        class="width80 ellipsis overflow-hidden pL15 pB10"
        @change="handleSelectAll()"
        v-model="selectAll"
        >{{ $t('floorplan.select_all') }}</el-checkbox
      >
      <template v-for="(department, index) in filteredList">
        <div :key="index" class="checkbox-gray-area flex">
          <div
            class="dp-color"
            :style="{ background: department.color, 'border-radius': '4px' }"
          ></div>
          <el-checkbox
            class="width80 ellipsis overflow-hidden"
            :disabled="department.disabled"
            v-bind:class="{ disable: department.selected }"
            @change="changeDepartment()"
            v-model="department.selected"
            >{{ department.name }}</el-checkbox
          >
          <el-tooltip placement="top">
            <div slot="content">
              <div class="pB5 f13">
                {{
                  `${getInfo(department).occupied} out of ${getInfo(department)
                    .vacant + getInfo(department).occupied} seats placed in ${
                    department.name
                  }`
                }}
              </div>
              <!-- <div class="pB5">
                {{
                  `Vacant desks ${getInfo(department).vacant}/${getInfo(
                    department
                  ).vacant + getInfo(department).occupied}`
                }}
              </div>
              <div class="">
                {{
                  `Occupied desks ${getInfo(department).occupied}/${getInfo(
                    department
                  ).vacant + getInfo(department).occupied}`
                }}
              </div> -->
            </div>
            <div class="dp-value bold p10 pointer" v-if="department.selected">
              {{
                `${getInfo(department).occupied}/${getInfo(department).vacant +
                  getInfo(department).occupied}`
              }}
            </div>
          </el-tooltip>
          <!-- <div class="dp-value bold p10" v-if="department.selected">
            {{ department.value }}
          </div> -->
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'
import isEqual from 'lodash/isEqual'

export default {
  props: ['departmentData', 'departmentInfo'],
  created() {
    this.init()
  },
  data() {
    return {
      departmentList: [],
      search: null,
      departments: [],
      selectedDepartmentmap: [],
      firstDepartmentMap: {},
      loading: false,
      counter: 0,
      selectAll: true,
    }
  },
  watch: {
    departmentData: {
      handler: function(newVal, oldVal) {
        if (
          newVal !== null &&
          Object.keys(newVal).length > 0 &&
          !isEqual(newVal, oldVal)
        ) {
          this.formatedDepartmentinfo()
          if (this.counter > 0) {
            this.prepareDepartmentafterData()
          } else {
            this.prepareDepartments()
          }
          this.counter++
        }
      },
      deep: true,
    },
  },
  computed: {
    formatDepartmentInfo() {
      let result = {}
      Object.values(this.departmentInfo).forEach(d => {
        Object.keys(d).forEach(key => {
          let data = result[key]
          let incomingData = d[key]
          if (!data) {
            this.$set(result, key, {
              vacant: 0,
              occupied: 0,
            })
          }
          let vacant = result[key].vacant
          let occupied = result[key].occupied
          let incomingDataVacant = incomingData.vacant || 0
          let incomingDataOccupied = incomingData.occupied || 0
          this.$set(result, key, {
            vacant: vacant + incomingDataVacant,
            occupied: occupied + incomingDataOccupied,
          })
        })
      })
      return result
    },
    filteredList() {
      if (this.search !== null) {
        return this.departmentList.filter(rt => {
          if (rt.name.toLowerCase().indexOf(this.search.toLowerCase()) >= 0) {
            return rt
          }
          return null
        })
      }
      return this.departmentList
    },
  },
  methods: {
    handleSelectAll() {
      if (this.selectAll) {
        this.departmentList.forEach(data => {
          if (!data.disabled) {
            this.$set(data, 'selected', true)
          }
        })
      } else {
        this.departmentList.forEach(data => {
          if (!data.disabled) {
            this.$set(data, 'selected', false)
          }
        })
      }
      this.changeDepartment()
    },
    formatedDepartmentinfo() {},
    getInfo(department) {
      return (
        this.formatDepartmentInfo[department.id] || { vacant: 0, occupied: 0 }
      )
    },

    searchContent: debounce(function() {
      this.prepareDepartmentafterData()
    }, 1000),
    prepareDepartments() {
      let activeList = []
      let disabledList = []
      this.firstDepartmentMap = this.$helpers.cloneObject(this.departmentData)
      this.departments.forEach(data => {
        this.$set(data, 'selected', false)
        if (this.departmentData[data.id]) {
          this.$set(data, 'selected', true)

          this.$set(data, 'disabled', false)
          this.$set(data, 'value', this.departmentData[data.id].value)
          activeList.push(data)
        } else {
          this.$set(data, 'disabled', true)
          disabledList.push(data)
        }
      })
      this.departmentList = this.$helpers.cloneObject([
        ...activeList,
        ...disabledList,
      ])
    },
    prepareDepartmentafterData() {
      let activeList = []
      let disabledList = []

      this.departmentList.forEach(data => {
        if (this.firstDepartmentMap[data.id]) {
          this.$set(
            data,
            'value',
            this.departmentData[data.id]?.value
              ? this.departmentData[data.id].value
              : 0
          )
          activeList.push(data)
        } else {
          // this.$set(data, 'disabled', true)
          disabledList.push(data)
        }
      })
      this.departmentList = this.$helpers.cloneObject([
        ...activeList,
        ...disabledList,
      ])
    },
    async init() {
      await this.getDepartments()
      this.formatedDepartmentinfo()
      //this.prepareDepartments()
    },
    async getDepartments() {
      this.loading = true
      let params = {
        moduleName: 'department',
      }
      let { data } = await API.get(`v2/module/data/list`, params)
      this.departments = data.moduleDatas
      this.loading = false
    },
    changeDepartment() {
      let ids = []
      this.departmentList.forEach(rt => {
        if (!rt.selected && !rt.disabled) {
          ids.push(rt.id)
        }
      })
      this.$emit('departmentChanged', ids)
    },
  },
}
</script>
<style lang="scss">
.wpa-search .el-input__inner {
  padding-left: 35px !important;
}
.checkbox-gray-area {
  height: 35px;
  border-radius: 4px;
  background-color: #fafafa;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  .el-checkbox {
    display: flex;
    align-items: center;
    padding-left: 10px;
    padding-right: 10px;
  }
}
.checkbox-containter {
  overflow: auto;
}
.dp-color {
  width: 5px;
  height: 26px;
  display: flex;
  align-items: center;
}
.checkbox-containter .el-checkbox__label,
.checkbox-containter .el-checkbox__input.is-checked + .el-checkbox__label {
  white-space: nowrap;
  width: 90%;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #232323;
}
</style>
