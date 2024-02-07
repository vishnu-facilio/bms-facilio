<template>
  <div>
    <FContainer
      padding="containerXLarge containerXxLarge"
      borderBottom="solid 1px"
      borderColor="borderNeutralBaseSubtler"
    >
      <FInput placeholder="Search" v-model="searchText">
        <template #prefix>
          <fc-icon group="action" name="search" size="16"></fc-icon>
        </template>
      </FInput>
    </FContainer>

    <div
      class="views-list-dsm-container"
      v-for="group in filteredList"
      :key="group.id"
    >
      <template v-if="!$validation.isEmpty(group.views)">
        <FContainer padding="containerXLarge containerXxLarge">
          <FText fontWeight="bold" v-if="$validation.isEmpty(searchText)">{{
            group.displayName || group.name
          }}</FText>
        </FContainer>

        <FContainer
          v-for="(view, index) in group.views"
          :key="index"
          hover:backgroundColor="backgroundNeutralHovered"
          :backgroundColor="
            isActiveView(view) ? 'backgroundNeutralHovered' : ''
          "
          padding="containerXLarge containerXxLarge"
          margin="containerSmall containerLarge"
          borderRadius="medium"
          cursor="pointer"
          @click="changeView(view, group)"
        >
          <FText
            :color="
              isActiveView(view) ? 'backgroundPrimaryDefault' : 'textMain'
            "
            >{{ view.displayName }}</FText
          >
        </FContainer>
      </template>
    </div>
    <FContainer
      borderTop="solid 1px"
      borderColor="borderNeutralBaseSubtler"
      padding="containerXLarge"
      class="view-list-manager"
    >
      <FButton appearance="tertiary" @click="loadViewManager">
        <fc-icon
          group="action"
          name="option"
          size="16"
          style="margin-right: 5px;"
        ></fc-icon>
        <FText> {{ $t('viewsmanager.list.views_manager') }}</FText>
      </FButton>
    </FContainer>
  </div>
</template>

<script>
import { FInput, FContainer, FText, FButton } from '@facilio/design-system'
import ViewsSidePanel from 'pages/views/ViewsSidePanel.vue'
export default {
  extends: ViewsSidePanel,
  name: 'ViewsList',
  components: { FInput, FContainer, FText, FButton },
}
</script>

<style scoped>
.views-list-dsm-container {
  height: calc(100vh - 215px);
}
.view-list-manager {
  display: flex;
  height: 100%;
  align-items: center;
  cursor: pointer;
}
</style>
