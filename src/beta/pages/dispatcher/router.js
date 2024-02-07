export default {
  path: 'dispatcher',
  component: () => import('src/beta/pages/dispatcher/Layout.vue'),
  children: [
    {
      name: 'dispatcher-console',
      path: 'console',
      props: true,
      component: () =>
        import(
          'src/beta/pages/dispatcher/dispatcher-console/DispatcherConsoleBoard.vue'
        ),
      children: [
        {
          path: '*',
          component: () =>
            import(
              'src/beta/pages/dispatcher/dispatcher-console/DispatcherConsoleBoard.vue'
            ),
        },
      ],
    },
  ],
}
