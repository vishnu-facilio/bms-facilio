<template>
  <div v-if="canShowSpaceId" class="display-flex letter-spacing0_4">
    <inline-svg
      :src="locations[locations.length - 1].image"
      class="mR8 d-flex"
      iconClass="icon fc-color icon-lg"
    ></inline-svg>
    <div class="d-flex flex-wrap">
      <template v-for="location in locations">
        <span class="pointer" @click="location.fn()" :key="location.name">{{
          location.name
        }}</span>
        <span :key="location.name + '-seperator'" class="pL10 pR10">/</span>
      </template>
      <span class="text-fc-grey">{{ asset.name }}</span>
    </div>
  </div>
  <div v-else>---</div>
</template>

<script>
import SpaceMixin from '@/mixins/SpaceMixin'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [SpaceMixin],
  props: ['asset'],

  created() {
    this.locationConstruction()
  },

  data() {
    return {
      locations: [],
    }
  },
  computed: {
    canShowSpaceId() {
      if (!isEmpty(this.asset.space.id)) {
        return true
      }
      return false
    },
  },

  methods: {
    locationConstruction() {
      let { space = {} } = this.asset
      if (!isEmpty(space)) {
        let {
          site,
          building,
          floor,
          space1,
          space2,
          space3,
          space4,
          space5,
        } = space
        if (!isEmpty(site) && !isEmpty(site.name)) {
          this.locations.push({
            fn: () => {
              this.openSiteLink(space.siteId)
            },
            name: site.name,
            image: 'svgs/site_icon',
          })
        }
        if (!isEmpty(building) && !isEmpty(building.name)) {
          this.locations.push({
            fn: () => {
              this.openBuildingLink(space.siteId, space.buildingId)
            },
            name: building.name,
            image: 'svgs/building_icon_new',
          })
        }
        if (!isEmpty(floor) && !isEmpty(floor.name)) {
          this.locations.push({
            fn: () => {
              this.openFloorLink(space.siteId, space.floorId)
            },
            name: floor.name,
            image: 'svgs/floor_icon_new',
          })
        }
        if (space.spaceTypeEnum == 'SPACE') {
          if (!isEmpty(space1)) {
            this.locations.push({
              fn: () => {
                this.openSpaceLink(space.siteId, space.spaceId1)
              },
              name: space1.name,
              image: 'svgs/Space_icon_new',
            })
          }
          if (!isEmpty(space2)) {
            this.locations.push({
              fn: () => {
                this.openSpaceLink(space.siteId, space.spaceId2)
              },
              name: space2.name,
              image: 'svgs/Space_icon_new',
            })
          }
          if (!isEmpty(space3)) {
            this.locations.push({
              fn: () => {
                this.openSpaceLink(space.siteId, space.spaceId3)
              },
              name: space3.name,
              image: 'svgs/Space_icon_new',
            })
          }
          if (!isEmpty(space4)) {
            this.locations.push({
              fn: () => {
                this.openSpaceLink(space.siteId, space.spaceId4)
              },
              name: space4.name,
              image: 'svgs/Space_icon_new',
            })
          }
          if (!isEmpty(space5)) {
            this.locations.push({
              fn: () => {
                this.openSpaceLink(space.siteId, space.spaceId5)
              },
              name: space5.name,
              image: 'svgs/Space_icon_new',
            })
          }
          if (!isEmpty(space)) {
            this.locations.push({
              fn: () => {
                this.openSpaceLink(space.siteId, space.spaceId)
              },
              name: space.name,
              image: 'svgs/Space_icon_new',
            })
          }
        }
      }
    },
  },
}
</script>
