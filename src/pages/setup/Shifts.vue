<template>
  <div class="full-layout-white height100">
    <div class="setting-header">
      <div class="setting-title-block">
        <div class="setting-form-title">Shifts Hours</div>
        <div class="heading-description">List of all Shift Hours</div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button type="primary" @click="newShift" class="setup-el-btn"
          >New Shift</el-button
        >
        <new-shift
          v-if="showDialog"
          :isNew="isNew"
          :visibility.sync="showDialog"
          v-model="model"
          @reset="getShifts"
        ></new-shift>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT30">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">NAME</th>
                <th class="setting-table-th setting-th-text">SHIFT DAYS</th>
                <th class="setting-table-th setting-th-text">SHIFT TIMING</th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>

            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr class="tablerow" v-for="(s, idx) in shifts" :key="s.id">
                <td>{{ s.name }}</td>
                <td>
                  <el-row v-for="(s, i) in formatDays(s)" :key="i">{{
                    s
                  }}</el-row>
                </td>
                <td>
                  <el-row v-for="(s, i) in formatTimes(s)" :key="i">{{
                    s
                  }}</el-row>
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i class="el-icon-edit pointer" @click="editShift(s)"></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteShift(s.id, idx)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewShift from 'pages/setup/NewShift'
import { mapState, mapActions } from 'vuex'
export default {
  components: {
    NewShift,
  },
  mounted() {
    this.model = this.getInitModel()
  },
  data() {
    return {
      isNew: false,
      loading: false,
      showDialog: false,
      model: null,
    }
  },
  watch: {
    showDialog: function() {
      if (!this.showDialog) {
        this.resetModel()
      }
    },
  },
  created() {
    this.$store.dispatch('loadShifts')
  },
  computed: {
    ...mapState({
      shifts: state => state.shifts,
    }),
  },
  methods: {
    ...mapActions(['loadShifts']),
    deleteShift(id, idx) {
      this.$http
        .post(`/shifts/delete?doValidation=true`, { id })
        .then(response => {
          if (response.data.result) {
            if (response.data.result === 'failure') {
              let userList = '<ul>'
              response.data.users.forEach(e => {
                userList += `<li>${e}</li>`
              })
              userList += '</ul>'
              this.$dialog
                .confirm({
                  title: 'Delete Shift',
                  htmlMessage: `Following Users work in this shift ${userList} Do you want to proceed deleting ? <br/>`,
                  rbDanger: true,
                  rbLabel: 'Delete',
                })
                .then(confirmation => {
                  if (confirmation) {
                    this.$http
                      .post('/shifts/delete?doValidation=false', { id })
                      .then(response => {
                        if (response.data.result === 'success') {
                          this.$message.success('Shift deleted successfully.')
                          this.$store.commit('GENERIC_DELETE', {
                            type: 'shifts',
                            matches: idx,
                          })
                        }
                      })
                  }
                })
            } else if (response.data.result === 'success') {
              this.$store.commit('GENERIC_DELETE', {
                type: 'shifts',
                matches: idx,
              })
            }
          }
        })
    },
    getShifts() {
      this.loading = true
      this.loadShifts().then(response => {
        this.loading = false
      })
    },
    formatDays(val) {
      return this.format(val).stringFmts
    },
    formatTimes(val) {
      return this.format(val).tStringFmts
    },
    format(val) {
      let isFirst = true
      let days = []
      let curr = []
      val.days.forEach(e => {
        if (isFirst) {
          curr.push(e)
          isFirst = false
        } else {
          let c = curr[curr.length - 1]
          if (c.startTime === e.startTime && c.endTime === e.endTime) {
            curr.push(e)
          } else {
            days.push(curr)
            curr = [e]
          }
        }
      })
      if (curr.length !== 0) {
        days.push(curr)
      }

      let lastPass = []
      days.forEach(e => {
        let c = []
        let isFirst = true
        e.forEach(a => {
          if (isFirst) {
            c.push(a)
            isFirst = false
          } else {
            if (c[c.length - 1].dayOfWeek + 1 === a.dayOfWeek) {
              c.push(a)
            } else {
              lastPass.push(c)
              c = [a]
            }
          }
        })
        if (c.length !== 0) {
          lastPass.push(c)
        }
      })

      let stringFmts = []
      let tStringFmts = []
      lastPass.forEach(e => {
        tStringFmts.push(e[0].startTime + '-' + e[0].endTime)
        if (e.length === 1) {
          stringFmts.push(e[0].dayOfWeekVal)
        } else if (e.length === 2) {
          stringFmts.push(e[0].dayOfWeekVal + ',' + e[1].dayOfWeekVal)
        } else {
          stringFmts.push(
            e[0].dayOfWeekVal + '-' + e[e.length - 1].dayOfWeekVal
          )
        }
      })
      return { stringFmts, tStringFmts }
    },
    editShift(val) {
      this.showDialog = true
      this.model = val
      this.isNew = false
    },
    newShift(val) {
      this.showDialog = true
      this.isNew = true
    },
    getInitModel() {
      return {
        name: '',
        siteId: null,
        isSameTime: true,
        startTime: '09:00',
        endTime: '17:00',
        days: [
          {
            dayOfWeek: 1,
            dayOfWeekVal: 'MONDAY',
            startTime: '09:00',
            endTime: '17:00',
          },
          {
            dayOfWeek: 2,
            dayOfWeekVal: 'TUESDAY',
            startTime: '09:00',
            endTime: '17:00',
          },
          {
            dayOfWeek: 3,
            dayOfWeekVal: 'THURSDAY',
            startTime: '09:00',
            endTime: '17:00',
          },
          {
            dayOfWeek: 4,
            dayOfWeekVal: 'FRIDAY',
            startTime: '09:00',
            endTime: '17:00',
          },
          {
            dayOfWeek: 5,
            dayOfWeekVal: 'SATURDAY',
            startTime: '09:00',
            endTime: '17:00',
          },
        ],
      }
    },
    resetModel() {
      this.model = this.getInitModel()
    },
  },
}
</script>
<style></style>
