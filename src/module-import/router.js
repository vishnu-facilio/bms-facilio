export default {
  path: '/:app/import',
  component: () => import('pages/Home.vue'),
  children: [
    {
      name: 'newreadingimport',
      path: 'asset/reading',
      component: () => import('src/module-import/NewReadingsImport.vue'),
    },
    {
      name: 'moduleimport-old',
      path: ':moduleName',
      props: true,
      component: () => import('src/module-import/ImportModule.vue'),
    },
    {
      name: 'moduleimport',
      path: 'newimport/:moduleName',
      props: true,
      component: () => import('src/module-import/NewImportModule.vue'),
    },
  ],
}
