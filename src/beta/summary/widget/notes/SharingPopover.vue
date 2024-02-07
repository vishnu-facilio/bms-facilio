<template>
  <FPopover
    :showArrow="true"
    placement="left"
    trigger="clickToOpen"
    :visible="visible"
    @visibleChange="val => (visible = val)"
  >
    <FContainer slot="content">
      <FContainer
        padding="containerXLarge"
        borderBottom="solid 1px"
        borderColor="borderNeutralBaseSubtler"
      >
        <FContainer
          display="flex"
          justifyContent="space-between"
          marginBottom="containerLarge"
        >
          <FText>{{ $t('common._common.public') }}</FText>
          <FSwitch
            v-model="allowPublic"
            @change="onTogglePublic"
            size="small"
          />
        </FContainer>
        <FText appearance="captionReg12" color="textDescription">
          {{ $t('common._common.edit_sharing_message') }}
        </FText>
      </FContainer>
      <FContainer padding="containerXLarge">
        <FCheckboxGroup
          v-model="selectedApps"
          :disabled="!allowPublic"
          :options="appOptions"
          @change="onChange"
        />
      </FContainer>
    </FContainer>
    <div>
      <slot></slot>
    </div>
  </FPopover>
</template>
<script>
import {
  FPopover,
  FContainer,
  FText,
  FSwitch,
  FCheckboxGroup,
} from '@facilio/design-system'
import SharingPopover from '@/comments/SharingPopover.vue'
export default {
  extends: SharingPopover,
  name: 'SharingPopover',
  components: { FPopover, FContainer, FText, FSwitch, FCheckboxGroup },
  data: () => ({
    visible: false,
  }),
  computed: {
    appOptions() {
      let { apps } = this || {}
      return apps.map(app => ({ ...app, label: app.name, value: app.id }))
    },
  },
  methods: {
    init() {
      if (this.value) {
        this.selectedApps = this.value.map(s => {
          return `${s.appId}`
        })
        if (this.selectedApps.length) {
          this.allowPublic = true
        }
      }
    },
  },
}
</script>
