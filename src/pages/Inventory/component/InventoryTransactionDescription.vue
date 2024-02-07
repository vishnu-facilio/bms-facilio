<template>
  <div>
    <div
      v-if="
        transaction.transactionType === 2 && transaction.transactionState === 2
      "
    >
      {{ $t('common._common.issued_to_wo') }}
      <router-link
        class="fc-id"
        :to="{ path: '/app/wo/orders/summary/' + transaction.parentId }"
        >#{{ transaction.parentId }}</router-link
      >
    </div>
    <div
      v-if="
        transaction.transactionType === 2 && transaction.transactionState === 4
      "
    >
      {{ $t('common._common.used_in_wo') }}
      <router-link
        class="fc-id"
        :to="{ path: '/app/wo/orders/summary/' + transaction.parentId }"
        >#{{ transaction.parentId }}</router-link
      >
    </div>
    <div
      v-if="
        transaction.transactionState === 9 ||
          transaction.transactionState === 10
      "
    >
      {{ $t('common._common.used_in_tr') }}
      <router-link
        class="fc-id"
        :to="{
          path:
            '/app/inventory/transferrequest/all/' +
            transaction.parentId +
            '/overview',
        }"
        >#{{ transaction.parentId }}</router-link
      >
    </div>
    <div
      v-if="
        transaction.transactionType === 3 && transaction.transactionState === 2
      "
    >
      <span class="mR10">{{ $t('common._common.issued_to') }}</span>
      <user-avatar size="sm" :user="transaction.issuedTo"></user-avatar>
      <!--   <user-avatar
        v-if="transaction.resource && transaction.resource.resourceType === 4"
        size="md"
        :user="transaction.issuedTo"
      ></user-avatar>
     <span
        v-else-if="
          transaction.resource && transaction.resource.resourceType === 2
        "
      >
        {{ $t('common._common.asset') }}
        <router-link
          class="fc-id"
          :to="{
            path: '/app/at/assets/all/' + transaction.parentId + '/overview/',
          }"
          >#{{ transaction.parentId }}</router-link
        >
        {{ ' - ' + transaction.resource.name }}
      </span>
      <span
        v-else-if="
          transaction.resource && transaction.resource.resourceType === 1
        "
      >
        {{ $t('common._common.space') }}
        <router-link
          class="fc-id"
          :to="{ path: getSpaceRouteLink(transaction.resource) }"
          >#{{ transaction.parentId }}</router-link
        >
        {{ ' - ' + transaction.resource.name }}
      </span> -->
    </div>
    <div
      v-if="
        transaction.transactionType === 2 && transaction.transactionState === 3
      "
    >
      {{ $t('common._common.returned_from_wo') }}
      <router-link
        class="fc-id"
        :to="{ path: '/app/wo/orders/summary/' + transaction.parentId }"
        >#{{ transaction.parentId }}</router-link
      >
    </div>
    <div
      v-if="
        transaction.transactionType === 4 &&
          transaction.transactionState === 1 &&
          transaction.shipment
      "
    >
      {{ $t('common._common.transferred_from_shipment') }}
      <router-link
        class="fc-id"
        :to="{
          path: `/app/inventory/shipment/all/${transaction.shipment}/summary`,
        }"
        >#{{ transaction.shipment }}</router-link
      >
    </div>
    <div
      v-else-if="
        transaction.transactionType === 4 &&
          transaction.transactionState === 2 &&
          transaction.shipment
      "
    >
      {{ $t('common._common.issued_to_shipment') }}
      <router-link
        class="fc-id"
        :to="{
          path: `/app/inventory/shipment/all/${transaction.shipment}/summary/`,
        }"
        >#{{ transaction.shipment }}</router-link
      >
    </div>
    <div v-else-if="transaction.transactionType === 4">
      {{ $t('common._common.transferred') }}
    </div>
    <div
      v-if="
        transaction.transactionType === 3 && transaction.transactionState === 3
      "
    >
      <span class="mR10">{{ $t('common._common.returned_from') }}</span>
      <user-avatar
        size="sm"
        :user="$store.getters.getUser(transaction.parentId)"
      ></user-avatar>
      <!-- <user-avatar
        v-if="transaction.resource && transaction.resource.resourceType === 4"
        size="md"
        :user="$store.getters.getUser(transaction.parentId)"
      ></user-avatar>
      <span
        v-else-if="
          transaction.resource && transaction.resource.resourceType === 2
        "
      >
        {{ $t('common._common.asset') }}
        <router-link
          class="fc-id"
          :to="{
            path: '/app/at/assets/all/' + transaction.parentId + '/overview/',
          }"
          >#{{ transaction.parentId }}</router-link
        >
        {{ ' - ' + transaction.resource.name }}
      </span>
      <span
        v-else-if="
          transaction.resource && transaction.resource.resourceType === 1
        "
      >
        {{ $t('common._common.space') }}
        <router-link
          class="fc-id"
          :to="{ path: getSpaceRouteLink(transaction.resource) }"
          >#{{ transaction.parentId }}</router-link
        >
        {{ ' - ' + transaction.resource.name }}
      </span> -->
    </div>
  </div>
</template>
<script>
import inventoryMixin from 'pages/Inventory/mixin/inventoryHelper'
import UserAvatar from '@/avatar/User'
export default {
  props: ['transaction'],
  mixins: [inventoryMixin],
  components: {
    UserAvatar,
  },
}
</script>
