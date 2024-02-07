<template>
  <RelationList
    v-if="showRelation"
    v-bind="$attrs"
    :widget="widget"
    :details="details"
    :parentId="parentId"
    :toModuleName="toModuleName"
  ></RelationList>
  <RelationSummary
    v-else
    v-bind="$attrs"
    :widget="widget"
    :moduleName="toModuleName"
    :details="relationRecord"
    :parentId="parentId"
    :isV3Api="true"
    :primaryFields="[]"
    :isLoading="isLoading"
    :toModuleName="toModuleName"
    @onUpdate="loadData"
  ></RelationSummary>
</template>
<script>
import RelationList from './RelationListWidget'
import RelationSummary from './RelationSummaryWidget'
import { API } from '@facilio/api'

const relationTypeMap = {
  ONE_TO_ONE: 1,
  ONE_TO_MANY: 2,
  MANY_TO_ONE: 3,
  MANY_TO_MANY: 4,
}

export default {
  name: 'RelationWidget',
  props: ['details', 'widget', 'id'],
  components: {
    RelationList,
    RelationSummary,
  },
  data() {
    return {
      relationRecord: null,
      isLoading: false,
    }
  },
  created() {
    if (!this.showRelation) this.loadData()
  },
  computed: {
    parentId() {
      return this.id
    },
    toModuleName() {
      let { relation } = this.widget || {}
      let { toModuleName } = relation || {}

      return toModuleName
    },
    showRelation() {
      let { relation } = this.widget || {}
      let { relationType } = relation || {}
      let { ONE_TO_MANY, MANY_TO_MANY } = relationTypeMap || {}

      return [ONE_TO_MANY, MANY_TO_MANY].includes(relationType)
    },
    moduleName() {
      return this.$attrs.moduleName
    },
  },
  methods: {
    async loadData() {
      this.isLoading = true

      let { relation } = this.widget || {}
      let { reverseRelationLinkName } = relation || {}

      let params = {
        viewName: 'hidden-all',
        includeParentFilter: true,
        withCount: true,
      }
      let url = `v3/modules/${this.moduleName}/${this.details?.id}/relationship/${reverseRelationLinkName}`
      let { data, error } = await API.get(url, params, {
        force: true,
      })

      if (error) {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      } else {
        this.relationRecord = this.$getProperty(
          data,
          `${this.toModuleName}.0`,
          {}
        )
      }
      this.isLoading = false
    },
  },
}
</script>
