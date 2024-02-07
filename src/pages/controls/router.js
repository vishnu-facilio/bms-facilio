export default {
  name: 'controls',
  path: 'co',
  meta: { root: 'co' },
  component: () => import('pages/controls/Layout'),
  children: [
    // view manager
    {
      name: 'controls-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'controls-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'graphicshome',
      path: 'graphics',
      component: () => import('pages/controls/Graphics/Graphics'),
      meta: { remember: true },
      children: [
        {
          path: 'view/:graphicsid',
          component: () => import('pages/controls/Graphics/GraphicsSummary'),
        },
      ],
    },
    {
      name: 'controlpoints',
      path: 'cp',
      component: () => import('pages/controls/ControlPoints/Layout'),
      meta: { remember: true },
      children: [
        {
          path: 'controlpoints',
          component: () =>
            import('pages/controls/ControlPoints/ControlPointsList'),
        },
        {
          path: 'controlgroups',
          component: () =>
            import('pages/controls/ControlPoints/ControlGroupsList'),
        },
      ],
    },
    {
      name: 'controllogic',
      path: 'cl',
      component: () => import('pages/controls/ControlLogic/Layout'),
      meta: { remember: true },
      children: [
        {
          path: ':viewname',
          component: () =>
            import('pages/controls/ControlLogic/ControlLogicList'),
        },
      ],
    },
    {
      name: 'commands',
      path: 'cc',
      component: () => import('pages/controls/Commands/Layout'),
      meta: { remember: true },
      children: [
        {
          path: ':viewname',
          component: () => import('pages/controls/Commands/CommandsList'),
        },
      ],
    },
    {
      name: 'schedule-create',
      path: 'schedule/create',
      component: () => import('pages/controls/controlschedule/NewSchedule'),
    },
    {
      name: 'schedule-edit',
      path: 'schedule/edit/:id',
      component: () => import('pages/controls/controlschedule/NewSchedule'),
      props: true,
    },
    {
      name: 'schedule-list',
      path: 'schedule/:viewname?',
      component: () =>
        import('pages/controls/controlschedule/ControlScheduleList'),
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
      }),
      children: [
        {
          name: 'schedule-summary',
          path: 'summary/:id',
          component: () =>
            import('pages/controls/controlschedule/ControlScheduleSummary'),
        },
      ],
    },

    {
      name: 'group-create',
      path: 'group/create',
      component: () => import('pages/controls/controlgroups/NewGroup'),
    },
    {
      name: 'group-edit',
      path: 'group/edit/:id',
      component: () => import('pages/controls/controlgroups/NewGroup'),
    },
    {
      name: 'group-list',
      path: 'group/:viewname?',
      component: () => import('pages/controls/controlgroups/ControlGroupList'),
      props: route => ({
        viewname: route.params.viewname,
        id: route.params.id,
        moduleName: 'controlGroupv2',
      }),
      children: [
        {
          name: 'group-summary',
          path: 'summary/:id',
          component: () =>
            import('pages/controls/controlgroups/ControlGroupSummary'),
        },
      ],
    },

    {
      name: 'tenant-group-summary',
      path: 'tenantgroup/summary/:id',
      component: () =>
        import('pages/controls/controlgroups/ControlGroupSummary'),
      props: route => ({
        id: route.params.id,
        moduleName: 'controlGroupv2TenantSharing',
      }),
    },
  ],
}
