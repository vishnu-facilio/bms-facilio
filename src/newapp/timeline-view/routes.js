import { pageTypes, DEFAULT } from '@facilio/router'

const {
  TIMELINE_LIST,
  TIMELINE_VIEW_MANAGER,
  TIMELINE_CREATE,
  TIMELINE_EDIT,
} = pageTypes

export default [
  {
    pageType: TIMELINE_LIST,
    moduleName: DEFAULT,
    component: () => import('./TimeLineView.vue'),
  },
  {
    pageType: TIMELINE_VIEW_MANAGER,
    moduleName: DEFAULT,
    component: () => import('./view-layout/ViewManagerLayout.vue'),
  },
  {
    pageType: TIMELINE_CREATE,
    moduleName: DEFAULT,
    component: () => import('./view-layout/ViewCreationForm.vue'),
  },
  {
    pageType: TIMELINE_EDIT,
    moduleName: DEFAULT,
    component: () => import('./view-layout/ViewCreationForm.vue'),
  },
]
