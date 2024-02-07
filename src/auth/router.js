export default [
  {
    name: 'proxyuser',
    path: '/auth/proxyuser',
    component: () => import(`./FacilioProxy`),
  },
  {
    name: 'portalproxyuser',
    path: '/auth/portalproxyuser',
    component: () => import(`./FacilioProxy`),
  },
  {
    path: '/app/signup',
    redirect: { name: 'signup' },
  },
  {
    path: '/service/signup',
    redirect: { name: 'signup' },
  },
  {
    name: 'signup',
    path: '/auth/signup',
    component: () => import(`./Signup`),
  },

  {
    path: '/app/login',
    redirect: { name: 'login' },
  },
  {
    path: '/service/login',
    redirect: { name: 'login' },
  },
  {
    path: '/vendor/login',
    redirect: { name: 'login' },
  },
  {
    path: '/tenant/login',
    redirect: { name: 'login' },
  },
  {
    name: 'login',
    path: '/auth/login',
    component: () => import(`./Login`),
  },
  {
    name: 'mfaSetup',
    path: '/auth/mfasetup',
    component: () => import(`./MFASetup`),
  },
  {
    path: '/app/logout',
    redirect: { name: 'logout' },
  },
  {
    name: 'logout',
    path: '/auth/logout',
    component: () => import(`./Logout`),
  },

  {
    path: '/app/freset_password',
    redirect: { name: 'passwordreset' },
  },
  {
    path: '/service/reset_password',
    redirect: { name: 'passwordreset' },
  },
  {
    name: 'passwordreset',
    path: '/auth/passwordreset',
    component: () => import(`./PasswordReset`),
  },

  {
    path: '/app/fconfirm_reset_password/:invitetoken',
    redirect: ({ params }) => ({
      name: 'confirmpassword',
      params,
    }),
  },
  {
    path: '/service/fconfirm_reset_password/:invitetoken',
    redirect: ({ params }) => ({
      name: 'confirmpassword',
      params,
    }),
  },
  {
    name: 'confirmpassword',
    path: '/auth/confirmpassword/:invitetoken',
    component: () => import(`./PasswordResetConfirm`),
  },

  {
    path: '/service/invitation/:invitetoken',
    redirect: ({ params }) => ({
      name: 'acceptInvite',
      params,
    }),
  },
  {
    path: '/app/invitation/:invitetoken',
    redirect: ({ params }) => ({
      name: 'acceptInvite',
      params,
    }),
  },
  {
    name: 'acceptInvite',
    path: '/auth/invitation/:invitetoken',
    component: () => import(`./AcceptInvite`),
  },

  {
    path: '/service/emailregistration/:invitetoken',
    redirect: ({ params }) => ({
      name: 'emailRegistration',
      params,
    }),
  },
  {
    path: '/app/emailregistration/:invitetoken',
    redirect: ({ params }) => ({
      name: 'emailRegistration',
      params,
    }),
  },
  {
    name: 'emailRegistration',
    path: '/auth/emailregistration/:invitetoken',
    component: () => import(`./EmailRegistration`),
  },

  {
    path: '/app/entry',
    redirect: '/auth/entry',
  },
  {
    path: '/auth/entry',
    component: () => import(`./SAMLLogout`),
    meta: {
      requiresAuth: false,
    },
  },

  {
    path: '/app/mobile/entry',
    redirect: 'auth/mobile/entry',
  },
  {
    path: '/auth/mobile/entry',
    component: () => import(`./SAMLLogout`),
    meta: {
      requiresAuth: false,
    },
  },

  {
    path: '/app/mobile/login',
    redirect: '/auth/mobile/login',
  },
  {
    path: '/auth/mobile/login',
    component: () => import(`./Login`),
  },

  {
    path: '/service/mobile/login',
    redirect: '/auth/mobile/service/login',
  },
  {
    path: '/app/mobile/service/login',
    redirect: '/auth/mobile/service/login',
  },
  {
    path: '/app/mobile/vendor/login',
    redirect: '/auth/mobile/vendor/login',
  },
  {
    path: '/auth/mobile/workq/login',
    component: () => import(`./Login`),
  },
  {
    path: '/auth/mobile/tenant/login',
    component: () => import(`./Login`),
  },
  {
    path: '/auth/mobile/service/login',
    component: () => import(`./Login`),
  },
  {
    path: '/auth/mobile/vendor/login',
    component: () => import(`./Login`),
  },
  {
    path: '/app/loginsuccess',
    redirect: { name: 'loginSuccess' },
  },
  {
    name: 'loginSuccess',
    path: '/auth/loginsuccess',
    component: () => import(`./LoginSuccess`),
  },
]
