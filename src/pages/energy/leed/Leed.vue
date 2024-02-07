<template>
  <div class="layout container row le-background-color-1">
    <div class="le-layout-side col-2 col-md-2 col-xs-2">
      <div class="row">
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center p25">
          <space-avatar
            hovercard="false"
            size="huge"
            :space="{ id: -1, name: '', type: 'Building' }"
          ></space-avatar>
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-label">{{ currentLeed.name }}</span>
        </div>
      </div>
      <div class="row le-notation"></div>
      <div class="row p25">
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2">
            Leed ID : {{ currentLeed.leedId }}</span
          >
        </div>
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2"
            >Status : {{ currentLeed.buildingStatus }}</span
          >
        </div>
      </div>
      <div class="row le-notation"></div>
      <div class="row p25">
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2">
            Leed Score : {{ currentLeed.leedScore }}</span
          >
        </div>
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2">
            Energy Score : {{ currentLeed.energyScore }}</span
          >
        </div>
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2">
            Water Score : {{ currentLeed.waterScore }}</span
          >
        </div>
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2">
            Waste Score : {{ currentLeed.wasteScore }}</span
          >
        </div>
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2">
            Human Exp Score : {{ currentLeed.humanExperienceScore }}</span
          >
        </div>
        <div class="col-12 col-sm-12 col-lg-12 col-md-12 text-center ">
          <span class="le-building-sublabel-2 ellipsis"
            >Transportation Score : {{ currentLeed.transportScore }}</span
          >
        </div>
      </div>
    </div>
    <div class="le-layout-main col-10 col-md-10 col-xs-10">
      <Tab :menu="subheaderMenu" newbtn="false" parent="/app/Leed/Energy"></Tab>
      <router-view></router-view>
    </div>
  </div>
</template>
<script>
import Tab from '@/Tabs'
import SpaceAvatar from '@/avatar/Space'

export default {
  data() {
    return {
      subheaderMenu: [
        {
          label: 'Energy',
          path: { path: '/app/em/leeds/' + this.$route.params.id + '/energy' },
          logo: require('statics/leed/energy.svg'),
        },
        {
          label: 'Water',
          path: { path: '/app/em/leeds/' + this.$route.params.id + '/water' },
          logo: require('statics/leed/drop.svg'),
        },
        {
          label: 'Waste',
          path: { path: '/app/em/leeds/' + this.$route.params.id + '/waste' },
          logo: require('statics/leed/recycle.svg'),
        },
        {
          label: 'Transportation',
          path: {
            path: '/app/em/leeds/' + this.$route.params.id + '/transportation',
          },
          logo: require('statics/leed/car.svg'),
        },
        {
          label: 'Human Experience',
          path: {
            path: '/app/em/leeds/' + this.$route.params.id + '/humanexperience',
          },
          logo: require('statics/leed/male.svg'),
        },
      ],
    }
  },
  mounted() {
    console.log('mounted.....a')
  },
  computed: {
    currentLeedId() {
      return this.$route.params.id
    },
    currentLeed() {
      return this.$store.getters['leed/getLeedById'](
        parseInt(this.$route.params.id)
      )
    },
  },
  components: {
    Tab,
    SpaceAvatar,
  },
  methods: {},
}
</script>
<style>
@import './../../../assets/styles/leed.css';
</style>
