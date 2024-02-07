export default {
  path: '/:app/workflow',
  component: () => import('pages/Home.vue'),
  children: [
    {
      path: '/',
      component: () => import('src/pages/workflow/Workflow.vue'),
    },
  ],
}
