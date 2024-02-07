import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.APPROVAL_ACTIVITY,
    component: () => import(`pages/approvals/ApprovalActivities.vue`),
  },
  {
    pageType: pageTypes.APPROVAL_LIST,
    component: () => import(`pages/approvals/ApprovalList.vue`),
  },
]
