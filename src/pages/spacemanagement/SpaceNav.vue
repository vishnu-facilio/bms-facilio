<template>
  <div>
    <div class="fc-space-header row">
      <div class="col-3 col-sm-3 col-lg-3 pull-left self-center f18 fw5">
        {{ $t('common.header.spacemanagement') }}
      </div>
      <div class="col-6 col-sm-6 col-lg-6">
        <div class="row text-center item-center">
          <router-link
            tag="div"
            to="sites"
            class="col-1 col-sm-1 col-md-1 col-lg-1 fc-margin-auto fc-margin-right-o p10 pL15 pR15 bg-color-1 fc-space-header-card fc-border-1 uppercase pointer sp-color-7"
            style="border-right-width: 0px;padding-bottom:12px;"
            ><div
              class=" f18 fw6 fc-identify-color-1"
              style="padding-bottom:4px;"
            >
              {{ sitesCount }}
            </div>
            <div class="f11">{{ $t('space.sites._sites') }}</div></router-link
          >
          <router-link
            tag="div"
            to="zones"
            class="col-1 col-sm-1 col-md-1 col-lg-1 fc-margin-auto fc-margin-left-o p10 pL15 pR15 bg-color-1 fc-space-header-card fc-border-1 uppercase pointer sp-color-7"
            style="padding-bottom:12px;"
            ><div
              class=" f18 fw6 fc-identify-color-1"
              style="padding-bottom:4px;"
            >
              {{ zonesCount }}
            </div>
            <div class="f11">{{ $t('space.sites.zones') }}</div></router-link
          >
        </div>
      </div>
      <div class="col-3 col-sm-3 col-lg-3 pull-right text-right self-center">
        <button
          class="sh-button sh-button-add button-add  fc-shadow sp-sh-btn"
          v-if="$hasPermission('space:CREATE')"
          @click="createNew"
        >
          <i
            aria-hidden="true"
            data-arrow="true"
            v-tippy
            :title="$t('space.sites.add_site')"
            class="q-icon material-icons"
            style="font-weight: 700;"
            >add</i
          >
        </button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    sitesCount() {
      return this.$store.state.space.sites.length
    },
    zonesCount() {
      return this.$store.state.space.zones.length
    },
  },
  data() {
    return {
      showCreateDialog: false,
      isNew: true,
    }
  },
  mounted() {
    this.$store.dispatch('space/fetchSites')
    this.$store.dispatch('space/fetchZones')
  },
  methods: {
    createNew() {
      this.$emit('new')
    },
  },
}
</script>
<style>
.sp-sh-btn {
  padding: 9px 11px;
  font-size: 17px;
}
</style>
