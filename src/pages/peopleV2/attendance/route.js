export default [
  {
    name: 'attendance-list',
    path: 'attendance',
    component: () => import('src/pages/peopleV2/attendance/AttendanceList'),
    props: () => ({
      moduleName: 'attendance',
    }),
  },
  {
    name: 'my-attendance-list',
    path: 'myattendance',
    component: () => import('src/pages/peopleV2/attendance/AttendanceList'),
    props: () => ({
      moduleName: 'attendance',
      myAttendance: true,
    }),
  },
]
