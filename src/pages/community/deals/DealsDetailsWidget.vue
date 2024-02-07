<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="widget-topbar">
      <div class="widget-title mL0">Deals and Offers Details</div>
    </div>
    <div class="mT10 mB30 d-flex flex-col">
      <div
        v-if="$getProperty(details, 'neighbourhood.location')"
        class="flex-middle"
      >
        <inline-svg
          src="svgs/community-features/location"
          iconClass="icon icon icon-sm-md vertical-middle object-contain mR15"
        ></inline-svg>
        <div class="label-txt-black mR10">
          {{ $getProperty(details, 'neighbourhood.title') }}
        </div>
        <div
          @click="
            $helpers.openInMap(
              $getProperty(details, 'neighbourhood.location.lat'),
              $getProperty(details, 'neighbourhood.location.lng')
            )
          "
          class="label-txt-blue4 pointer"
        >
          View on map
        </div>
      </div>
      <div v-if="details.sysCreatedTime" class="mT20 flex-middle">
        <inline-svg
          src="svgs/community-features/clock2"
          iconClass="icon icon icon-sm-md vertical-middle object-contain mR15"
        ></inline-svg>
        <div class="label-txt-black">
          {{ $options.filters.formatDate(details.sysCreatedTime, true) }}
        </div>
      </div>
      <div v-if="details.sysCreatedBy" class="mT20 flex-middle">
        <UserAvatar
          size="sm mR5"
          v-if="details.sysCreatedBy"
          :user="$store.getters.getUser(details.sysCreatedBy.id)"
          :name="false"
        ></UserAvatar>
        <div class="label-txt-black">
          {{ $store.getters.getUser(details.sysCreatedBy.id).name }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import UserAvatar from '@/avatar/User'
export default {
  props: ['details'],
  components: {
    UserAvatar,
  },
}
</script>
