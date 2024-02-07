export default {
  name: 'safetyPlanMain',
  path: 'sf',
  component: () => import('pages/safetyplan/Layout'),
  children: [
    {
      name: 'safetyplan-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'safetyplan',
      path: 'safetyplan/:viewname?',
      component: () => import('pages/safetyplan/sp/SafetyPlanCommonList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'safetyPlan',
      }),
    },
    {
      path: 'safetyplan/:viewname',
      component: () => import('pages/safetyplan/sp/SafetyPlanCommonList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'safetyPlan',
      }),
      children: [
        {
          name: 'safetyPlanSummary',
          path: ':id/overview',
          component: () => import('pages/safetyplan/sp/SafetyPlanOverview'),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'safetyPlan',
            id: route.params.id,
          }),
        },
      ],
    },
    {
      name: 'safetyplan-new',
      path: 'safetyplan/create',
      props: () => ({
        moduleName: 'safetyPlan',
      }),
      component: () => import('src/pages/safetyplan/sp/NewSafetyPlan.vue'),
    },
    {
      name: 'safetyplan-edit',
      path: 'safetyplan/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'safetyPlan',
      }),
      component: () => import('src/pages/safetyplan/sp/NewSafetyPlan.vue'),
    },
    {
      name: 'hazard',
      path: 'hazard/:viewname?',
      component: () => import('pages/safetyplan/hazard/HazardsList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'hazard',
      }),
    },
    {
      path: 'hazard/:viewname',
      component: () => import('pages/safetyplan/hazard/HazardsList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'hazard',
      }),
      children: [
        {
          name: 'hazardSummary',
          path: ':id/overview',
          component: () => import('pages/safetyplan/hazard/HazardPlanOverview'),
          props: route => ({
            viewname: route.params.viewname,
            moduleName: 'hazard',
          }),
        },
      ],
    },
    {
      name: 'hazard-new',
      path: 'hazard/create',
      props: () => ({
        moduleName: 'hazard',
      }),
      component: () => import('src/pages/safetyplan/hazard/HazardForm.vue'),
    },
    {
      name: 'hazard-edit',
      path: 'hazard/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'hazard',
      }),
      component: () => import('src/pages/safetyplan/hazard/HazardForm.vue'),
    },
    {
      name: 'precaution',
      path: 'precaution/:viewname?',
      component: () => import('pages/safetyplan/precaution/PrecautionsList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'precaution',
      }),
    },
    {
      path: 'precaution/:viewname',
      component: () => import('pages/safetyplan/precaution/PrecautionsList'),
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'precaution',
      }),
      children: [
        {
          name: 'precautionSummary',
          path: ':id/overview',
          component: () =>
            import('pages/safetyplan/precaution/PrecautionOverview'),
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'precaution',
          }),
        },
      ],
    },
    {
      name: 'precaution-new',
      path: 'precaution/create',
      props: () => ({
        moduleName: 'precaution',
      }),
      component: () =>
        import('src/pages/safetyplan/precaution/PrecautionForm.vue'),
    },
    {
      name: 'precaution-edit',
      path: 'precaution/edit/:id',
      props: route => ({
        id: route.params.id,
        moduleName: 'precaution',
      }),
      component: () =>
        import('src/pages/safetyplan/precaution/PrecautionForm.vue'),
    },
  ],
}
