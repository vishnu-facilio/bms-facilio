<template>
  <FContainer>
    <!-- <FTable :columns="columns" :data="data" hideBorder :showSelect="false" /> -->
    <FContainer class="under-maintanence">
      <FContainer>
        <img
          width="250"
          height="250"
          src="https://www.pngall.com/wp-content/uploads/5/Construction-Maintenance-Transparent.png"
        />
      </FContainer>
      <FContainer display="block">
        <FText>
          {{ 'Widget Under Maintanence' }}
        </FText>
      </FContainer>
    </FContainer>
  </FContainer>
</template>

<style lang="scss" scoped>
.under-maintanence {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
</style>
