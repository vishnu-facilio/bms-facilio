import ConnectedApps from '../connectedApps/routes.js'
import Calendar from '../calendar/routes.js'
import Analytics from '../analytics/routes.js'
import Reports from '../reports/routes.js'
import Dashboard from '../dashboard/routes.js'
import Kpi from '../kpi/routes.js'
import NewKpi from '../newKpi/routes.js'
import ReadingRules from '../readingrules/routes.js'
import Approvals from '../approvals/routes'
import Floorplan from '../floorplan/routes'
import Portfolio from '../portfolio/routes.js'
import WorkplaceAnalytics from '../workplace-analytics/routes'
import Timeline from '../timeline-view/routes'
import ServiceCatalog from '../serviceCatalog/routes'
import Pivot from '../pivot/routes'
import EnergyStar from '../energyStar/routes'
import Surveys from '../survey/routes'
import ShiftPlanner from '../shiftPlanner/routes'
import MyAttendance from '../myAttendance/routes'
import Attendance from '../attendance/routes'
import ControlGroups from '../controlgroups/routes'
import mandv from '../mandv/routes'

const router = [
  ...ConnectedApps,
  ...Calendar,
  ...Analytics,
  ...Reports,
  ...Dashboard,
  ...Kpi,
  ...Approvals,
  ...Floorplan,
  ...Portfolio,
  ...WorkplaceAnalytics,
  ...Timeline,
  ...ServiceCatalog,
  ...Pivot,
  ...EnergyStar,
  ...Surveys,
  ...ShiftPlanner,
  ...MyAttendance,
  ...Attendance,
  ...NewKpi,
  ...ReadingRules,
  ...ControlGroups,
  ...mandv,
]

export default router
