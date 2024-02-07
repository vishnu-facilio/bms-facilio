<script>
import { isEmpty } from '@facilio/utils/validation'
import CatalogList from 'pages/catalog/CatalogList'

export default {
  extends: CatalogList,
  methods: {
    redirectToCatalogRequest(catalog) {
      let { id: catalogId, groupId, name, externalURL } = catalog
      if (!isEmpty(externalURL)) {
        window.open(externalURL, '_blank', 'noopener,noreferrer')
      } else {
        this.$router.push({
          path: `requests/${catalogId}`,
          meta: {
            groupId,
            name,
          },
        })
      }
    },
    redirectToSetup() {},
  },
}
</script>
<style lang="scss" scoped>
.catalog-container {
  height: calc(100vh - 30px);

  .catalog-items-container {
    height: initial;
  }

  .loading-container.mT70 {
    margin-top: 0;
  }
}
</style>
