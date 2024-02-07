import { getApp } from '@facilio/router'
export default {
  methods: {
    readingSpaceAssetChoosing() {
      let appName = getApp().linkName
      appName = appName === 'newapp' ? 'app' : appName
      if (this.readinglist === 'assetreadings') {
        let url = `/${appName}/setup/assetsettings/readings`
        this.$router.push({ path: url })
      } else if (this.readinglist === 'spacereadings') {
        let url = `/${appName}/setup/assetsettings/spacereadings`
        this.$router.push({ path: url })
      } else if (this.readinglist === 'weatherreadings') {
        let url = `/${appName}/setup/assetsettings/weatherreadings`
        this.$router.push({ path: url })
      }
    },
    assetReadingChooser() {
      let appName = getApp().linkName
      appName = appName === 'newapp' ? 'app' : appName
      if (this.readingFilter === 'site') {
        let url = `/${appName}/setup/assetsettings/sitereadings`
        this.$router.push({ path: url })
      } else if (this.readingFilter === 'building') {
        let url = `/${appName}/setup/assetsettings/buildingreadings`
        this.$router.push({ path: url })
      } else if (this.readingFilter === 'floor') {
        let url = `/${appName}/setup/assetsettings/floorreadings`
        this.$router.push({ path: url })
      } else if (this.readingFilter === 'space') {
        let url = `/${appName}/setup/assetsettings/spacereadings`
        this.$router.push({ path: url })
      }
    },
  },
}
