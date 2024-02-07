<script>
import { API } from '@facilio/api'
import AttendanceSummary from 'src/pages/peopleV2/attendance/AttendanceSummary'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
import TransitionButtons from 'src/pages/peopleV2/attendance/TransitionButtons.vue'
export default {
  name: 'AttendanceView',
  mixins: [PeopleMixin],
  components: {
    AttendanceSummary,
    TransitionButtons,
  },
  props: {
    selectedEmployee: {
      required: true,
      datatype: Object,
    },
    timelineRange: {
      required: true,
      datatype: Object,
    },
    myAttendance: {
      datatype: Boolean,
      default: false,
    },
  },
  created() {
    this.fetchCurrentEmployeeAttendance()
    this.$root.$on('refresh-attendance', () => {
      this.fetchCurrentEmployeeAttendance()
    })
  },
  watch: {
    timelineRange: {
      handler() {
        this.fetchCurrentEmployeeAttendance()
      },
      deep: true,
    },
    selectedEmployee: {
      handler() {
        this.fetchCurrentEmployeeAttendance()
      },
      deep: true,
    },
  },
  data() {
    return {
      attendance: {
        list: [],
        isLoading: true,
      },
      shifts: null,
    }
  },
  computed: {
    allowDateTimeSelector() {
      return !this.myAttendance
    },
    showTransitionButtons() {
      return Boolean(this.selectedEmployee && !this.myAttendance)
    },
    employeeID() {
      return this.selectedEmployee.id
    },
    currentEmployeeName() {
      return this.selectedEmployee?.name ? this.selectedEmployee.name : '---'
    },
  },
  methods: {
    async fetchAttendance(rangeFrom, rangeTo, peopleID) {
      if (!peopleID) {
        return
      }
      this.attendance.isLoading = true
      let route = `/v3/attendance/view?peopleID=${peopleID}&rangeFrom=${rangeFrom}&rangeTo=${rangeTo}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.shifts = data.shifts
        this.attendance.list = data.attendance
        for (let attendance of this.attendance.list) {
          const shiftID = attendance?.shift?.id
          if (shiftID) {
            attendance.shift = data.shifts[attendance?.shift?.id]
          }
        }
      }
      this.attendance.isLoading = false
    },
    fetchCurrentEmployeeAttendance() {
      const employeeID = this.selectedEmployee?.id
      this.fetchAttendance(
        this.timelineRange.from,
        this.timelineRange.to,
        employeeID
      )
    },
  },
}
</script>
