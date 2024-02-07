import { tabTypes } from '@facilio/router'

export default [
  {
    tabType: tabTypes.ATTENDANCE,
    component: () => import('src/pages/peopleV2/attendance/AttendanceList'),
  },
]
