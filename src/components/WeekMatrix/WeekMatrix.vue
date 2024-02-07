<template>
  <el-table
    :data="tableData"
    class="mT10 width100 weekmatrix"
    :cell-style="{
      padding: '5px 5px 5px 15px',
      fontWeight: 'bold',
      color: '#324056',
    }"
    :header-cell-style="{
      background: 'white',
      padding: '5px 5px 5px 15px',
      color: '#324056',
    }"
  >
    <!-- day col -->
    <el-table-column width="100" label="DAYS" prop="day"> </el-table-column>

    <el-table-column label="WEEK">
      <template v-slot:header>
        <div style="text-align: center">
          {{ $t('common.wo_report.weeks_') }}
        </div>
      </template>

      <!-- all col -->
      <el-table-column
        :label="$t('common._common._all')"
        width="70"
        align="center"
      >
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.all"
            @change="allCheckBoxChangeActions(scope.row)"
          ></el-checkbox>
        </template>
      </el-table-column>

      <!-- 1st week -->
      <el-table-column label="1st" width="70" align="center">
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.one"
            @change="checkBoxChangeActions(scope.row)"
          ></el-checkbox>
        </template>
      </el-table-column>

      <!-- 2st week -->
      <el-table-column label="2nd" width="70" align="center">
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.two"
            @change="checkBoxChangeActions(scope.row)"
          ></el-checkbox>
        </template>
      </el-table-column>

      <!-- 3st week -->
      <el-table-column label="3rd" width="70" align="center">
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.three"
            @change="checkBoxChangeActions(scope.row)"
          ></el-checkbox>
        </template>
      </el-table-column>

      <!-- 4st week -->
      <el-table-column label="4th" width="70" align="center">
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.four"
            @change="checkBoxChangeActions(scope.row)"
          ></el-checkbox>
        </template>
      </el-table-column>

      <!-- 5st week -->
      <el-table-column label="5th" width="70" align="center">
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.five"
            @change="checkBoxChangeActions(scope.row)"
          ></el-checkbox>
        </template>
      </el-table-column>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  props: ['weekendData'],
  data() {
    return {
      tableData: [
        {
          day: 'Sunday',
          key: 1,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Monday',
          key: 2,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Tuesday',
          key: 3,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Wednesday',
          key: 4,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Thursday',
          key: 5,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Friday',
          key: 6,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Saturday',
          key: 7,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
      ],
    }
  },
  computed: {
    tableDataJson() {
      return this.generateWeekendJson()
    },
  },
  watch: {
    tableDataJson: {
      handler(value) {
        this.$emit('update:weekendData', value)
      },
    },
  },
  methods: {
    fillTableData() {
      let self = this
      if (this.weekendData) {
        let weekendJSON = JSON.parse(this.weekendData)
        for (let week in weekendJSON) {
          for (let day of weekendJSON[week]) {
            self.tableData[day - 1][this.getNumberWord(week)] = true
          }
        }
        for (let key in this.tableData) {
          this.checkBoxChangeActions(this.tableData[key])
        }
      }
    },
    getNumberWord(val) {
      switch (parseInt(val)) {
        case 1:
          return 'one'
        case 2:
          return 'two'
        case 3:
          return 'three'
        case 4:
          return 'four'
        case 5:
          return 'five'
      }
    },
    generateWeekendJson() {
      let weekend = { 1: [], 2: [], 3: [], 4: [], 5: [] }
      for (let tab of this.tableData) {
        if (tab.one) {
          weekend['1'].push(tab.key)
        }
        if (tab.two) {
          weekend['2'].push(tab.key)
        }
        if (tab.three) {
          weekend['3'].push(tab.key)
        }
        if (tab.four) {
          weekend['4'].push(tab.key)
        }
        if (tab.five) {
          weekend['5'].push(tab.key)
        }
      }
      for (let key in weekend) {
        if (weekend[key].length === 0) {
          delete weekend[key]
        }
      }
      return JSON.stringify(weekend)
    },
    allCheckBoxChangeActions(val) {
      val.one = val.all
      val.two = val.all
      val.three = val.all
      val.four = val.all
      val.five = val.all
    },
    checkBoxChangeActions(val) {
      if (val.one && val.two && val.three && val.four && val.five) {
        val.all = true
      } else {
        val.all = false
      }
    },
  },
  mounted() {
    if (this.weekendData) {
      this.fillTableData()
    }
  },
}
</script>
