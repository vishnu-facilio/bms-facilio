<template>
  <div class="height100 overflow-hidden">
    <div class="modules-details">
      <div
        class="d-flex text-capitalize cursor-pointer"
        @click="redirectToModulesList()"
      >
        <inline-svg
          src="svgs/arrow"
          class="vertical-middle"
          iconClass="icon icon-sm mR10 arrow rotate-left mT2"
        ></inline-svg>
        <div class="f16 bold letter-spacing0_5 fc-black-color text-capitalize">
          {{ moduleData.displayName }}
        </div>
      </div>
      <div v-if="moduleData.custom" class="d-flex mT40">
        <div class="calender d-flex justify-content-center">
          <inline-svg
            src="svgs/calendar-time"
            class="vertical-middle self-center"
            iconClass="icon fill-white icon-md vertical-middle"
          ></inline-svg>
        </div>
        <div>
          <div class="fc-blue-label pL10">
            {{ $t('custommodules.list.created_date') }}
          </div>
          <div class="flex-middle pT5 pL10">
            <div class="fc-black-color date f13 letter-spacing0_5">
              {{ moduleData.createdTime | formatDate() }}
            </div>
          </div>
        </div>
      </div>
      <div v-if="moduleData.custom" class="d-flex mT25">
        <div class="d-flex justify-content-center">
          <avatar
            size="lg"
            :user="moduleData.createdBy"
            color="#4273e9"
          ></avatar>
        </div>
        <div>
          <div class="fc-blue-label pL10 avatar-title">
            {{ $t('custommodules.list.created_by') }}
          </div>
          <div class="flex-middle pT5 pL10 avatar-content">
            <div class="fc-black-color date f13 letter-spacing0_5">
              {{ getUserName(moduleData.createdBy) }}
            </div>
          </div>
        </div>
      </div>
      <div class="fields-count d-flex mT30">
        <div class="text-capitalize f14 bold letter-spacing0_5 fc-black-color">
          {{ $t('custommodules.list.fields') }}
        </div>
        <div class="f14 letter-spacing0_5 fc-black-color">
          {{ fieldCount }}
        </div>
      </div>
      <div class="state-flow d-flex mT15">
        <div class="text-capitalize f14 bold letter-spacing0_5 fc-black-color">
          {{ $t('custommodules.list.state_flows') }}
        </div>
        <div
          class="f14 letter-spacing0_5 status"
          :class="
            moduleData.stateFlowEnabled || !moduleData.custom ? '' : 'disabled'
          "
        >
          {{
            moduleData.stateFlowEnabled || !moduleData.custom
              ? $t('custommodules.list.enabled')
              : $t('custommodules.list.disabled')
          }}
        </div>
      </div>
      <div class="description mT15">
        <div class="title text-uppercase f11 bold letter-spacing0_5">
          {{ $t('custommodules.list.description') }}
        </div>
        <div class="f13 letter-spacing0_5 fc-black-color mT7 line-height20">
          {{ moduleData.description }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapGetters } from 'vuex'
import Avatar from '@/Avatar'

export default {
  props: ['moduleData', 'fieldCount'],

  components: { Avatar },

  computed: {
    ...mapGetters(['getUser']),
  },

  methods: {
    redirectToModulesList() {
      let { moduleData } = this
      let { extendModule } = moduleData || {}
      let { name } = extendModule || {}

      if (name === 'asset') {
        let currentPath = this.$router.resolve({
          name: 'modules-details',
          params: {
            moduleName: name,
          },
        }).href

        this.$router.push({
          path: `${currentPath}/extendedModules`,
        })
      } else {
        this.$router.push({
          name: 'module-list',
        })
      }
    },
    getUserName(userObj) {
      if (!isEmpty(userObj)) {
        let { id } = userObj
        let currentUser = this.getUser(id)
        return currentUser.name === 'Unknown' ? '---' : currentUser.name
      }
      return '---'
    },
  },
}
</script>
