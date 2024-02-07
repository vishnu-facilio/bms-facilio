export default {
  path: 'en',
  meta: {
    root: 'en',
  },
  component: () => import('pages/energyAnalytics/Layout'),
  children: [
    {
      name: 'energymeter-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'energymeter-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'energyhome',
      path: 'energy',
      component: () => import('pages/energyAnalytics/energy/Energy'),
    },
    {
      path: 'energy/summary/:id',
      component: () => import('pages/energyAnalytics/energy/EnergySummary'),
    },
    {
      name: 'mv-energy-open',
      path: 'mv',
      redirect: 'mv/open',
    },
    {
      name: 'mv-energy-project-list',
      path: 'mv/:viewname',
      component: () => import('pages/energyAnalytics/mv/MVList.vue'),
    },
    {
      name: 'mv-energy-project-edit',
      path: 'mv/edit/:id',
      component: () => import('pages/energyAnalytics/mv/MVProjectCreation.vue'),
    },
    {
      name: 'mv-energy-project-new',
      path: 'mv/project/new',
      component: () => import('pages/energyAnalytics/mv/MVProjectCreation.vue'),
    },
    {
      name: 'mv-energy-project-summary',
      path: 'mv/summary/:id',
      component: () => import('pages/energyAnalytics/mv/MVSummary.vue'),
    },
    {
      path: 'energymeter',
      meta: { remember: true },
      redirect: 'energymeter/energy',
    },
    {
      name: 'energyHome',
      path: 'energymeter/:viewname',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'energymeter',
      }),
      component: () =>
        import('pages/energyAnalytics/energyMeter/EnergyMeterList.vue'),
      meta: { remember: true },
    },
    {
      name: 'energyOverview',
      path: 'energymeter/:viewname/:assetid',
      component: () =>
        import('pages/energyAnalytics/energyMeter/EnergyOverviewLayout.vue'),
      children: [
        {
          path: 'overview',
          component: () =>
            import('pages/energyAnalytics/energyMeter/EnergyMeterSummary.vue'),
        },
      ],
    },
  ],
}
