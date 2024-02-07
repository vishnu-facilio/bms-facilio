<template>
  <div>
    <div v-if="field.name === 'movable'">
      {{
        moduleData.type && getAssetType(moduleData.type.id).movable
          ? 'Yes'
          : 'No'
      }}
    </div>
    <div v-if="field.name === 'geoLocation'">
      {{
        moduleData.geoLocation
          ? '@' + $helpers.parseLocation(moduleData.geoLocation)
          : '---'
      }}
    </div>
    <div v-if="field.name === 'currentLocation'">
      {{
        moduleData.currentLocation
          ? '@' + $helpers.parseLocation(moduleData.currentLocation)
          : '---'
      }}
    </div>
    <div v-if="field.name === 'designatedLocation'">
      {{ moduleData.designatedLocation ? 'Yes' : 'No' }}
    </div>
    <div v-if="field.name === 'distanceMoved'">
      {{
        moduleData.currentLocation
          ? '@' + $helpers.parseLocation(moduleData.currentLocation)
          : '---'
      }}
    </div>
    <div v-if="field.name === 'boundaryRadius'">
      {{
        moduleData.boundaryRadius > 0 ? moduleData.boundaryRadius + 'm' : '---'
      }}
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'

export default {
  props: ['field', 'moduleData'],
  computed: {
    ...mapGetters(['getAssetType']),
  },
}
</script>
