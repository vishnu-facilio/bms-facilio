import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.MY_ATTENDANCE,
    component: () => import('src/pages/peopleV2/attendance/AttendanceList'),
    props: () => ({
      myAttendance: true,
    }),
  },
]
