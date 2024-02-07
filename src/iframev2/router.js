export default [
  {
    path: '/:app/iframe',
    component: () => import('./Layout.vue'),
    children: [
      {
        path: 'newrules/new',
        props: true,
        component: () =>
          import('src/pages/alarm/rule-creation/AlarmRuleWrapper.vue'),
      },
      {
        path: 'newrule/:id/edit',
        props: true,
        component: () =>
          import('src/pages/alarm/rule-creation/AlarmRuleWrapper.vue'),
      },
      {
        path: 'virtualMeterTemplate/create',
        props: true,
        component: () =>
          import('src/beta/pages/meter/vmTemplate/NewVMTemplate.vue'),
      },
      {
        path: 'virtualMeterTemplate/:id/edit',
        props: true,
        component: () =>
          import('src/beta/pages/meter/vmTemplate/NewVMTemplate.vue'),
      },
      {
        path: 'inspectionTemplate/:id/builder', //path
        props: true,
        component: () =>
          import(
            'src/pages/inspection/inspection-builder/InspectionBuilderWrapper.vue'
          ),
      },
      {
        path: 'inductionTemplate/:id/builder', //path
        props: true,
        component: () =>
          import(
            'src/pages/induction/induction-builder/InductionBuilderWrapper.vue'
          ),
      },
      {
        path: 'inspectionResponse/:id/liveForm', //path
        props: true,
        component: () =>
          import(
            'src/pages/inspection/individual-inspection/InspectionLiveFormWrapper.vue'
          ),
      },
      {
        path: 'inductionResponse/:id/liveForm', //path
        props: true,
        component: () =>
          import(
            'src/pages/induction/individual-induction/InductionLiveFormWrapper.vue'
          ),
      },
    ],
  },
]
