import { isEmpty } from '@facilio/utils/validation'

export default [
  {
    name: 'pm-create',
    path: 'pm/create',
    component: () => import('./create/PMCreation'),
    props: route => ({
      isEdit: !isEmpty(route.query.id),
      id: route.query.id,
    }),
  },
  {
    name: 'pm-list',
    path: 'pm/:viewname?',
    component: () => import('./PMList'),
    props: route => ({
      viewname: route.params.viewname,
      moduleName: 'plannedmaintenance',
    }),
  },
  {
    name: 'pm-summary',
    path: 'pm/summary/:id',
    component: () => import('./summary/PMSummary'),
    props: route => ({
      id: route.params.id,
      moduleName: 'plannedmaintenance',
    }),
  },
  {
    name: 'pm-viewmanager',
    path: ':moduleName/viewmanager',
    props: true,
    component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
  },
  {
    name: 'pm-view-creation-form',
    path: ':moduleName/viewform/:viewname?',
    props: true,
    component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
  },
]
