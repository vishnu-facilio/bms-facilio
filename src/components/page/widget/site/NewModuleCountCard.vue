<template>
  <div class="site-details-card p10">
    <div class="flex-middle justify-content-space width100 height100">
      <div
        v-for="(module, index) in modulesList"
        :key="index"
        :class="[
          'text-center',
          'related-count-container',
          'hv-center-block',
          index !== modulesList.length - 1 && 'space-card-border-right',
        ]"
      >
        <div v-if="loading">
          <spinner :show="true"></spinner>
        </div>
        <template v-else>
          <div class="mB10">
            <fc-icon group="default" :name="module.icon" size="40"></fc-icon>
          </div>
          <span
            class="related-count hv-center-inline pointer"
            @click="redirectToModuleList(module.key, recordId)"
          >
            {{ $getProperty(moduleCountMap, `${module.key}.count`, '---') }}
          </span>
          <div
            @click="redirectToModuleList(module.key, recordId)"
            class="fc-black-com f12 bold mT5 pointer"
          >
            {{
              $getProperty(moduleCountMap, `${module.key}.displayName`, '---')
            }}
          </div>
        </template>
      </div>
      <div v-if="canShowRooms" class="text-center border-left4 pL20">
        <InlineSvg
          src="svgs/spacemanagement/room"
          class="vertical-middle"
          iconClass="icon icon-xxll"
        ></InlineSvg>
        <div class="fc-black-com f24 pT10 bold pointer">
          {{ details.data.number }}
        </div>
        <div class="fc-black-com text-uppercase f12 bold mT5">
          {{ $t('space.sites.accomendation_rooms') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import ModuleCountCard from './ModuleCountCard'

export default {
  extends: ModuleCountCard,
  data() {
    return {
      modulesList: [
        {
          key: 'work_orders',
          icon: 'workorder',
        },
        {
          key: 'fire_alarms',
          icon: 'add-alarm',
        },
        {
          key: 'bms_alarms',
          icon: 'notification',
        },
        {
          key: 'assets',
          icon: 'assets',
        },
      ],
    }
  },
  computed: {
    canShowRooms() {
      return (
        this.$org?.id === 349 && this.details?.data && this.details.data.number
      )
    },
  },
}
</script>

<style lang="scss" scoped>
.site-details-card {
  overflow: scroll;
  .hv-center-block {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .hv-center-inline {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .warning-alarm {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    margin-top: -18px;
  }
  .space-card-border-right {
    border-right: 1px solid #f0f0f0;
  }
  .related-count-container {
    width: 25%;
    height: 55%;
    min-width: 110px;
    .related-count {
      font-size: 24px;
      font-weight: 500;
      margin-bottom: 5px;
    }
    .module-icon-container {
      height: 40%;
      width: 30%;
      margin-bottom: 18px;
    }
  }
}
</style>

<style lang="scss">
.site-details-card {
  .related-count-container {
    .module-icon-container {
      .icon {
        height: 100%;
        width: 100%;
      }
    }
  }
}
</style>
