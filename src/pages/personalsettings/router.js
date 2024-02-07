export default {
  path: '/:app/personalsettings',
  component: () => import('pages/Home.vue'),
  children: [
    {
      path: '/',
      component: () => import('pages/PersonalSettings'),
      children: [
        {
          path: '/',
          redirect: 'editprofile',
        },
        {
          name: 'editprofile',
          path: 'editprofile',
          component: () => import('pages/personalsettings/EditProfile'),
        },
        {
          name: 'userchangepassword',
          path: 'userchangepassword',
          component: () => import('pages/personalsettings/ChangePassword'),
        },
        {
          name: 'notifications',
          path: 'notifications',
          component: () => import('pages/personalsettings/Notifications'),
        },
        {
          name: 'MFA',
          path: 'MFA',
          component: () => import('pages/personalsettings/mfa'),
        },
        {
          name: 'delegate',
          path: 'delegate',
          component: () => import('pages/personalsettings/IndividualDelegate'),
        },
        {
          name: 'signature',
          path: 'signature',
          component: () => import('pages/personalsettings/Signature'),
        },
        {
          name: 'apiclient',
          path: 'apiclient',
          component: () => import('pages/personalsettings/APIClients.vue'),
        },
        {
          name: 'myapps',
          path: 'myapps',
          component: () => import('pages/personalsettings/MyApps.vue'),
        },
      ],
    },
  ],
}
