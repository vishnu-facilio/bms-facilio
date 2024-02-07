<template>
  <FContainer height="100%" display="flex">
    <FShimmer
      v-if="$validation.isEmpty(currentViewDisplayName)"
      :width="180"
      :height="28"
    />

    <div v-else-if="!canShowViewsSidePanel && currentGroupViews.length > 1">
      <FPopover
        trigger="clickToOpen"
        v-model="showPopover"
        @show="() => (showPopover = true)"
        @hide="() => (showPopover = false)"
      >
        <div slot="content">
          <FContainer
            display="flex"
            padding="containerLarge containerXLarge"
            alignItems="center"
            gap="containerLarge"
            borderBottom="solid 1px"
            borderColor="borderNeutralGrey02Light"
          >
            <fc-icon group="files" name="folder"></fc-icon>
            <FText appearance="headingMed14">{{
              currentGroup.displayName
            }}</FText>
          </FContainer>
          <FDropdownMenu
            :menuList="currentGroupViews"
            width="200px"
            :selectedValue="selectedViewObj"
            @change="switchView"
          >
            <FMenuItem
              v-for="view in currentGroupViews"
              :value="view.value"
              :key="view.value"
            >
              {{ view.label }}
            </FMenuItem>
            <!-- <FContainer
              display="flex"
              justifyContent="center"
              alignItems="center"
              padding="containerLarge"
              borderTop="solid 1px"
              borderColor="borderNeutralGrey02Light"
            >
              <FButton appearance="tertiary">
                {{ $t('viewsmanager.list.show_all') }}
              </FButton>
            </FContainer> -->
          </FDropdownMenu>
        </div>
        <FButton
          appearance="tertiary"
          iconGroup="text-edit"
          iconName="chevron-down"
          iconPosition="suffix"
          >{{ currentViewDisplayName }}</FButton
        >
      </FPopover>
    </div>
    <FText v-else>{{ currentViewDisplayName }}</FText>
  </FContainer>
</template>

<script>
import {
  FShimmer,
  FButton,
  FContainer,
  FPopover,
  FDropdownMenu,
  FMenuItem,
  FText,
} from '@facilio/design-system'
import ViewHeader from 'src/newapp/components/ViewHeaderWithoutGroups.vue'
export default {
  extends: ViewHeader,
  name: 'ViewHeader',
  components: {
    FShimmer,
    FButton,
    FContainer,
    FPopover,
    FDropdownMenu,
    FMenuItem,
    FText,
  },
  data: () => ({
    showPopover: false,
  }),
  computed: {
    currentGroupViews() {
      let { filteredViews } = this
      let { list, more } = filteredViews || {}
      let views = [...(list || []), ...(more || [])]
      views = views.map(view => {
        return { ...view, value: view.name }
      })
      return views || {}
    },
    selectedViewObj() {
      let { currentView, currentGroupViews } = this || {}
      return currentGroupViews.filter(view => view.name === currentView)
    },
  },
  methods: {
    switchView(views) {
      let [view] = views || []
      this.showPopover = false
      this.goToView(view)
    },
  },
}
</script>
