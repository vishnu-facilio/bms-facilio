export default {
  path: 'bk',
  component: () => import('pages/facilitybooking/Layout'),
  children: [
    // Booking Routes
    {
      name: 'booking-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'booking-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-booking',
      path: 'facilitybooking/new',
      component: () => import(`pages/facilitybooking/booking/BookingForm`),
    },
    {
      name: 'edit-booking',
      path: 'facilitybooking/:id/edit',
      props: true,
      component: () => import(`pages/facilitybooking/booking/BookingForm`),
    },
    {
      name: 'bookingList',
      path: 'facilitybooking/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/facilitybooking/booking/BookingList`),
    },
    {
      path: 'facilitybooking/:viewname',
      component: () =>
        import('pages/facilitybooking/booking/BookingSummaryList.vue'),
      children: [
        {
          name: 'bookingSummary',
          path: ':id/overview',
          component: () =>
            import('pages/facilitybooking/booking/BookingSummary.vue'),
        },
      ],
    },
    // Facility Routes
    {
      name: 'facility-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'new-facility',
      path: 'facility/new',
      component: () => import(`pages/facilitybooking/facility/FacilityForm`),
    },
    {
      name: 'edit-facility',
      path: 'facility/:id/edit',
      props: true,
      component: () => import(`pages/facilitybooking/facility/FacilityForm`),
    },
    {
      name: 'facilityList',
      path: 'facility/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/facilitybooking/facility/FacilityList`),
    },
    {
      path: 'facility/:viewname',
      component: () =>
        import('pages/facilitybooking/facility/FacilitySummaryList.vue'),
      children: [
        {
          name: 'facilitySummary',
          path: ':id/overview',
          component: () =>
            import('pages/facilitybooking/facility/FacilitySummary.vue'),
        },
      ],
    },
    {
      name: 'amenityList',
      path: 'amenity/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
      }),
      component: () => import(`pages/facilitybooking/amenity/AmenityList`),
    },
  ],
}
