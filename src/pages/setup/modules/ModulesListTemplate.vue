<template>
  <div v-if="isLoading">
    <Spinner :show="isLoading"></Spinner>
  </div>
  <div v-else class="container-scroll">
    <div class="d-flex flex-direction-row mL20 mR20 flex-wrap mB10">
      <div
        class="modules-card mT25 mR25"
        v-for="(module, index) in moduleList"
        :key="index"
        @click="redirectToModule(module)"
      >
        <div class="d-flex">
          <div
            class="fc-black-color f16 bold letter-spacing0_5 text-capitalize"
          >
            {{ module.displayName }}
          </div>
          <div
            v-if="allowModulesEdit"
            class="mL-auto module-edit"
            @click.stop="openModuleCreation(module)"
          >
            <inline-svg
              src="svgs/edit"
              class="vertical-middle"
              iconClass="icon icon-sm mR5 icon-edit"
            ></inline-svg>
          </div>
        </div>
        <div
          class="created-time f12 letter-spacing0_5 mT5"
          v-if="module.custom"
        >
          {{ module.createdTime | formatDate() }}
        </div>
        <div class="d-flex mT30">
          <div>
            <div class="fc-black-color f14 bold letter-spacing0_5">
              {{ $t('custommodules.list.state_flows') }}
            </div>
            <div
              class="stateflow-status f13 mT5"
              :class="
                module.stateFlowEnabled || !module.custom ? '' : 'disabled'
              "
            >
              {{
                module.stateFlowEnabled || !module.custom
                  ? $t('custommodules.list.enabled')
                  : $t('custommodules.list.disabled')
              }}
            </div>
          </div>
        </div>
        <div
          class="modules-description fc-black-color f13 letter-spacing0_5 mT20"
        >
          {{ module.description }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Spinner from '@/Spinner'

export default {
  components: {
    Spinner,
  },
  props: {
    isLoading: {
      type: Boolean,
    },
    allowModulesEdit: {
      type: Boolean,
    },
    moduleList: {
      type: Array,
    },
  },
  methods: {
    redirectToModule(module) {
      this.$emit('redirect', module)
    },
    openModuleCreation(module) {
      this.$emit('openModuleCreation', module)
    },
  },
}
</script>

<style lang="scss">
.modules-card {
  padding: 20px 20px 20px 30px;
  border-radius: 2px;
  box-shadow: 0 4px 8px 2px rgba(0, 0, 0, 0.05);
  background-color: #ffffff;
  flex: 0 0 30%;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(0.165, 0.84, 0.44, 1);
  .created-time {
    line-height: 1.83;
    color: #8ca1ad;
  }
  .module-edit {
    display: none;
  }
  &:hover {
    .module-edit {
      display: block;
    }
    transform: scale(1.1, 1.1);
  }
  .stateflow-status {
    line-height: 1.57;
    letter-spacing: 0.54px;
  }
  .modules-description {
    line-height: 1.69;
  }
  .stateflow-status {
    color: #46ca87;
    &.disabled {
      color: #eb696a;
    }
  }
}
</style>
