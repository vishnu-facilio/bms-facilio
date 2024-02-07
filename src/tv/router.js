// function page (page) {
//   return () => import(`tv/${page}.vue`)
// }

export default [
  {
    name: 'session',
    path: ':sessionid',
    component: () => import('tv/Home.vue'),
  },
]
