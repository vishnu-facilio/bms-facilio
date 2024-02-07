export default {
  path: 'cy',
  component: () => import('pages/community/Layout'),
  children: [
    // Announcement Routes
    {
      name: 'announcements-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'announcements-view-creation-form',
      path: ':moduleName/viewform/:viewname?',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewCreationForm.vue'),
    },
    {
      name: 'new-announcement',
      path: 'announcements/new',
      props: true,
      component: () => import(`pages/community/announcements/AnnouncementForm`),
    },
    {
      name: 'edit-announcement',
      path: 'announcements/:id/edit',
      props: true,
      component: () => import(`pages/community/announcements/AnnouncementForm`),
    },
    {
      name: 'announcementsList',
      path: 'announcements/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'announcement',
        attachmentsModuleName: 'announcementattachments',
      }),
      component: () => import(`pages/community/announcements/AnnouncementList`),
      children: [
        {
          name: 'announcementSummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'announcement',
            notesModuleName: 'announcementnotes',
            attachmentsModuleName: 'announcementattachments',
          }),
          component: () =>
            import(`pages/community/announcements/AnnouncementSummary`),
        },
      ],
    },

    //audience
    {
      name: 'new-audience',
      path: 'audience/new',
      props: true,
      component: () => import(`pages/community/audience/AudienceForm`),
    },
    {
      name: 'edit-audience',
      path: 'audience/:id/edit',
      props: true,
      component: () => import(`pages/community/audience/AudienceForm`),
    },
    {
      name: 'audienceList',
      path: 'audience/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'audience',
      }),
      component: () => import(`pages/community/audience/AudienceList`),
      children: [
        {
          name: 'audienceSummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'audience',
          }),
          component: () => import(`pages/community/audience/AudienceSummary`),
        },
      ],
    },
    // Neighbourhood Routes
    {
      name: 'new-neighbourhood',
      path: 'neighbourhood/new',
      props: true,
      component: () =>
        import(`pages/community/neighbourhood/NeighbourhoodForm`),
    },
    {
      name: 'edit-neighbourhood',
      path: 'neighbourhood/:id/edit',
      props: true,
      component: () =>
        import(`pages/community/neighbourhood/NeighbourhoodForm`),
    },
    {
      name: 'neighbourhoodList',
      path: 'neighbourhood/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'neighbourhood',
        attachmentsModuleName: 'neighbourhoodattachments',
      }),
      component: () =>
        import(`pages/community/neighbourhood/NeighbourhoodList`),
      children: [
        {
          name: 'neighbourhoodSummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'neighbourhood',
            notesModuleName: 'neighbourhoodnotes',
            attachmentsModuleName: 'neighbourhoodattachments',
          }),
          component: () =>
            import(`pages/community/neighbourhood/NeighbourhoodSummary`),
        },
      ],
    },
    // Deals and offers Routes
    {
      name: 'new-dealsandoffers',
      path: 'deals/new',
      props: true,
      component: () => import(`pages/community/deals/DealsAndOffersForm`),
    },
    {
      name: 'edit-dealsandoffers',
      path: 'deals/:id/edit',
      props: true,
      component: () => import(`pages/community/deals/DealsAndOffersForm`),
    },
    {
      name: 'dealsandoffersList',
      path: 'deals/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'dealsandoffers',
        attachmentsModuleName: 'dealsandoffersattachments',
      }),
      component: () => import(`pages/community/deals/DealsAndOffersList`),
      children: [
        {
          name: 'dealsandoffersSummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'dealsandoffers',
            notesModuleName: 'dealsandoffersnotes',
            attachmentsModuleName: 'dealsandoffersattachments',
          }),
          component: () => import(`pages/community/deals/DealsSummary`),
        },
      ],
    },
    // News and Information
    {
      name: 'news-viewmanager',
      path: ':moduleName/viewmanager',
      props: true,
      component: () => import('src/newapp/viewmanager/ViewManagerLayout.vue'),
    },
    {
      name: 'new-news',
      path: 'news/new',
      props: true,
      component: () => import(`pages/community/news/NewsForm`),
    },
    {
      name: 'edit-news',
      path: 'news/:id/edit',
      props: true,
      component: () => import(`pages/community/news/NewsForm`),
    },
    {
      name: 'newsList',
      path: 'news/:viewname?',
      props: route => ({
        viewname: route.params.viewname,
        moduleName: 'newsandinformation',
        attachmentsModuleName: 'newsandinformationattachments',
      }),
      component: () => import(`pages/community/news/NewsList`),
      children: [
        {
          name: 'newsSummary',
          path: ':id/overview',
          props: route => ({
            viewname: route.params.viewname,
            id: route.params.id,
            moduleName: 'newsandinformation',
            notesModuleName: 'newsandinformationnotes',
            attachmentsModuleName: 'newsandinformationattachments',
          }),
          component: () => import(`pages/community/news/NewsSummary`),
        },
      ],
    },
  ],
}
