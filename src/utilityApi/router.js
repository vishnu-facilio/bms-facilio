export default {
  name: 'surveyResponse',
  path: '/:appName/utilityCustomer',
  props: true,
  meta: {
    requiresAuth: true,
  },
  component: () => import('utilityApi/pages/Layout'),
  children: [
    {
      name: 'authenticateUtilityCustomer',
      path: 'authenticate',
      props: true,
      component: () => import('utilityApi/pages/UtilityAuthentication.vue'),
    },
  ],
}
