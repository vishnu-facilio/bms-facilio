<template>
  <div class="height100 pL50">
    <router-view></router-view>
  </div>
</template>
<script>
export default {
  computed: {
    product() {
      let product = {
        code: 'inventory',
        label: 'INVENTORY',
        path: '/app/inventory',
        modules: [
          {
            label: this.$t('common.header.item'),
            path: { name: 'item' },
            license: 'INVENTORY',
            permission: 'inventory:READ,READ_OWN',
          },
          {
            label: this.$t('common.header.item_types'),
            path: { name: 'itemtypes' },
            license: 'INVENTORY',
            permission: 'inventory:READ,READ_OWN',
          },
          {
            label: this.$t('common.header.tools'),
            path: { name: 'tool' },
            license: 'INVENTORY',
            permission: 'inventory:READ,READ_OWN',
          },
          {
            label: this.$t('common.header.tooltypes'),
            path: { name: 'tooltypes' },
            license: 'INVENTORY',
            permission: 'inventory:READ,READ_OWN',
          },
          {
            label: this.$t('common.header.storerooms'),
            path: { name: 'storerooms' },
            license: 'INVENTORY',
            permission: 'inventory:READ,READ_OWN',
          },
          {
            label: this.$t('common.header.inventory_request'),
            path: { name: 'inventoryrequest' },
            license: 'INVENTORY',
            permission: 'inventoryrequest:READ,READ_TEAM,READ_OWN',
          },
          {
            label: this.$t('common.products.services'),
            path: { name: 'service' },
            license: 'INVENTORY',
            permission: 'inventory:READ,READ_OWN',
          },
          {
            label: this.$t('common.header.reports'),
            path: { name: 'inventoryreports' },
            permission: 'inventory:READ,READ_OWN',
            license: 'INVENTORY',
          },
        ],
      }

      if (this.$org.id === 155) {
        product.modules.push({
          label: this.$t('common.header.gatepass'),
          path: { name: 'gatepass' },
          license: 'INVENTORY',
        })
        product.modules.push({
          label: this.$t('common.header.shipment'),
          path: { name: 'shipment' },
          license: 'INVENTORY',
        })
      }
      if (this.$helpers.isLicenseEnabled('TRANSFER_REQUEST')) {
        product.modules.push({
          label: this.$t('common.header.transfer_request'),
          path: { path: '/app/inventory/transferrequest' },
          license: 'TRANSFER_REQUEST',
          permission: 'transferrequest:READ',
        })
        product.modules.push({
          label: this.$t('common.header.shipment'),
          path: { path: '/app/inventory/trShipment' },
          license: 'TRANSFER_REQUEST',
          permission: 'transferrequest:READ',
        })
      }
      return product
    },
  },
  created() {
    this.initProduct()
  },
  watch: {
    $route() {
      this.initProduct()
    },
  },
  methods: {
    initProduct() {
      this.$store.dispatch('switchProduct', this.product)
      if (this.$route.path === '/app/inventory') {
        for (let mod of this.product.modules) {
          if (this.$hasPermission(mod.permission)) {
            this.$router.push(mod.path)
            return
          }
        }
      }
    },
  },
}
</script>
