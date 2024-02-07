<template>
  <div>
    <template v-if="widgetParams === 'transferrequestcard1'">
      <store-room-avatar
        v-if="detail"
        :name="false"
        size="lg"
        :storeRoom="detail"
      ></store-room-avatar>
      <img
        class="fc-avatar fc-avatar-lg"
        src="~statics/inventory/store-img.jpg"
        v-else
      />
    </template>
    <template v-else-if="widgetParams === 'transferrequestcard2'">
      <div class="fc-in-custom-icon" style="background-color: #efa25e">
        <InlineSvg
          src="svgs/requested-date"
          iconClass="icon icon-md vertical-middle fc-white-color"
        ></InlineSvg>
      </div>
    </template>
    <template v-else-if="widgetParams === 'transferrequestcard3'">
      <user-avatar size="lg" :name="false" :user="userId"></user-avatar>
    </template>
    <template v-else-if="showBalanceIcon">
      <div class="fc-in-custom-icon">
        <InlineSvg
          src="svgs/storeroom/balance"
          iconClass="icon icon-xlg vertical-middle fc-white-color"
        ></InlineSvg>
      </div>
    </template>
    <template v-else-if="showCalendarIcon">
      <div class="fc-in-custom-icon">
        <InlineSvg
          src="svgs/storeroom/calender"
          iconClass="icon icon-xlg vertical-middle fc-white-color"
        ></InlineSvg>
      </div>
    </template>
    <template v-else-if="price">
      <div class="fc-in-custom-icon">
        <InlineSvg
          src="svgs/storeroom/price"
          iconClass="icon icon-xlg vertical-middle fc-white-color"
        ></InlineSvg>
      </div>
    </template>
    <template v-else-if="storeroom">
      <div class="fc-in-custom-icon">
        <InlineSvg
          src="svgs/storeroom/storeroom"
          iconClass="icon icon-xlg vertical-middle fc-white-color"
        ></InlineSvg>
      </div>
    </template>
    <template v-else-if="individuallyTracked">
      <div class="fc-in-custom-icon">
        <InlineSvg
          src="svgs/storeroom/individuallyTracked"
          iconClass="icon icon-xlg vertical-middle fc-white-color"
        ></InlineSvg>
      </div>
    </template>
  </div>
</template>
<script>
import StoreRoomAvatar from '@/avatar/Storeroom'
import UserAvatar from '@/avatar/User'
export default {
  name: 'InventorySummaryIcons',
  props: [
    'detail',
    'widgetParams',
    'userId',
    'storeroom',
    'individuallyTracked',
    'price',
  ],
  components: {
    StoreRoomAvatar,
    UserAvatar,
  },
  computed: {
    showCalendarIcon() {
      return (
        ['itemtypescard2', 'tooltypescard2'].includes(this.widgetParams) ||
        (['itemcard1', 'toolcard1'].includes(this.widgetParams) &&
          !this.storeroom)
      )
    },
    showBalanceIcon() {
      return [
        'itemtypescard1',
        'tooltypescard1',
        'itemcard2',
        'toolcard2',
      ].includes(this.widgetParams)
    },
  },
}
</script>
