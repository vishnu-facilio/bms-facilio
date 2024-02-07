export default {
  name: 'planning',
  path: 'planning',
  component: () => import('pages/planning/Layout'),
  children: [
    {
      name: 'new-route',
      path: 'routes/new',
      component: () => import(`pages/planning/routes/RouteForm`),
      props: true,
    },
    {
      name: 'edit-route',
      path: 'routes/edit/:id',
      component: () => import(`pages/planning/routes/RouteForm`),
      props: true,
    },
    {
      name: 'routeList',
      path: 'routes/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'routes',
      }),
      component: () => import(`src/pages/planning/routes/RouteList`),
      children: [
        {
          name: 'routeSummary',
          path: ':id/summary',
          component: () => import(`pages/planning/routes/RouteSummary`),
          props: true,
        },
      ],
    },
  ],
}
