<template>
  <div class="h100">
    <div v-if="loading"></div>
    <HomePageViewer
      :homepage="homepage"
      :sections="sections"
      v-else
    ></HomePageViewer>
  </div>
</template>
<script>
import HomePageViewer from 'src/components/homepage/HomePageViewer.vue'
import { getHomepageData } from 'src/components/homepage/homepageApi.js'
import { sort, assign_uid } from '@facilio/ui/dashboard'
export default {
  components: { HomePageViewer },
  data() {
    return {
      loading: true,
      homepage: {
        sections: [
          {
            h: 32,
            homePageId: -1,
            id: '-1',
            name: '',
            orgId: -1,
            w: 12,
            x: 0,
            y: 39,
            widgets: [
              {
                id: '-2',
                layoutParams: { x: 0, y: 0, w: 6, h: 10 },
                class: ['homepage-shadow-widget'],
                orgId: -1,
                sectionId: 0,
                widgetType: 3,
                widgetTypeEnum: 'RESERVED_SPACES',
                widgetTypeObj: { name: 'recentreservedspacecard', value: 3 },
              },
              {
                id: '-3',
                layoutParams: { x: 6, y: 0, w: 6, h: 25 },
                class: ['homepage-shadow-widget'],
                orgId: -1,
                sectionId: 0,
                widgetParams: { moduleName: 'servicerequest' },
                widgetType: 5,
                widgetTypeEnum: 'TINY_LIST_VIEW',
                widgetTypeObj: { name: 'listview', value: 5 },
              },
              {
                id: '-4',
                layoutParams: { x: 0, y: 13, w: 6, h: 15 },
                class: ['homepage-shadow-widget'],
                orgId: -1,
                sectionId: 0,
                widgetType: 4,
                widgetTypeEnum: 'SPACE_FINDER',
                widgetTypeObj: { name: 'spacefinder', value: 4 },
              },
            ],
          },
          {
            infoMsg:
              'You can perform live actions directly from the home screen thanks to interactive cards. for instance, approving a visitor who is about to visit you or extending a reserved room while you are in a meeting.',
            h: 18,
            homePageId: -1,
            id: '-5',
            name: 'Live Actions',
            orgId: -1,
            w: 12,
            x: 0,
            y: 15,
            widgets: [
              {
                id: '-6',
                layoutParams: { x: 0, y: 0, w: 12, h: 16 },
                orgId: -1,
                sectionId: 0,
                widgetType: 2,
                widgetTypeEnum: 'GROUPED_ACTION_CARD',
                widgetTypeObj: { name: 'groupedactioncard', value: 2 },
              },
            ],
          },
          {
            infoMsg:
              'This section provides you with brief details on your daily bookings, visitors, announcements, etc. By clicking the card, you can view additional details.',
            h: 15,
            homePageId: -1,
            id: '-7',
            name: 'In a Nutshell',
            orgId: -1,
            w: 12,
            x: 0,
            y: 0,
            widgets: [
              {
                id: '-8',
                layoutParams: { x: 0, y: 0, w: 12, h: 15 },
                orgId: -1,
                sectionId: 0,
                widgetType: 1,
                widgetTypeEnum: 'NUT_SHELL_WIDGET',
                widgetTypeObj: { name: 'nutshellwidget', value: 1 },
              },
            ],
          },
        ],
      },
      sections: [],
    }
  },
  mounted() {
    this.init()
  },

  methods: {
    async init() {
      this.loading = true
      let { data } = await getHomepageData()
      if (data.homepage) {
        // this.homepage = data.homepage
        // 'sort' will sort the items and items inside of sections in descending order of 'y'.
        this.sections = assign_uid(
          sort(
            this.homepage.sections.map(i => {
              i['children'] = i['widgets']
              delete i.widgets
              return i
            })
          )
        )
      }
      this.loading = false
    },
  },
}
</script>
