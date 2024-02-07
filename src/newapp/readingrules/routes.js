import { pageTypes } from '@facilio/router'

export default [
  {
    pageType: pageTypes.RULES_LIST,
    component: () => import('pages/firealarm/rules/Layout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/firealarm/rules/Rules.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.RULES_SUMMARY,
    component: () => import('pages/firealarm/rules/RuleOverview.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/firealarm/rules/RuleOverview.vue'),
      },
    ],
  },
  {
    pageType: pageTypes.RULES_CREATE,
    component: () => import('pages/alarm/rule/RuleConfiguration.vue'),
  },
  {
    pageType: pageTypes.RULES_EDIT,
    component: () => import('pages/alarm/rule/RuleConfiguration.vue'),
  },
]
