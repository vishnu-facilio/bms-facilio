<template>
  <FContainer display="flex" padding="containerXxLarge">
    <FContainer
      gap="containerXLarge"
      display="flex"
      flexDirection="column"
      alignItems="flex-start"
    >
      <FContainer display="flex" alignItems="flex-start" gap="containerXxLarge">
        <FContainer
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="flex-start"
          gap="containerMedium"
          width="139px"
        >
          <FContainer
            display="flex"
            alignItems="center"
            gap="containerLarge"
            alignSelf="stretch"
          >
            <FText appearance="captionMed12" color="textMain" fontWeight="bold"
              >Staff</FText
            >
          </FContainer>

          <FContainer
            display="flex"
            alignItems="center"
            gap="containerMedium"
            alignSelf="stretch"
          >
            <FAvatar size="XS" avatarUrl="" :userName="getStaffText"></FAvatar>
            <FText
              appearance="bodyReg14"
              marginLeft="containerLarge"
              color="textMain"
              >{{ getStaff }}</FText
            >
          </FContainer>
        </FContainer>

        <FContainer
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="flex-start"
          gap="containerMedium"
          width="139px"
        >
          <FContainer
            display="flex"
            alignItems="center"
            gap="containerLarge"
            alignSelf="stretch"
          >
            <FText appearance="captionMed12" color="textMain" fontWeight="bold"
              >Team</FText
            >
          </FContainer>

          <FContainer
            display="flex"
            alignItems="center"
            gap="containerMedium"
            alignSelf="stretch"
          >
            <FAvatar size="XS" avatarUrl="" :userName="getTeamText"></FAvatar>
            <FText
              appearance="bodyReg14"
              marginLeft="containerLarge"
              color="textMain"
              >{{ getTeam }}</FText
            >
          </FContainer>
        </FContainer>
      </FContainer>

      <FContainer
        display="flex"
        flexDirection="column"
        justifyContent="center"
        alignItems="flex-start"
        gap="containerMedium"
        width="139px"
      >
        <FContainer
          display="flex"
          alignItems="center"
          gap="containerLarge"
          alignSelf="stretch"
        >
          <FText appearance="captionMed12" color="textMain" fontWeight="bold"
            >Vendor</FText
          >
        </FContainer>

        <FContainer
          display="flex"
          alignItems="center"
          gap="containerMedium"
          alignSelf="stretch"
        >
          <FAvatar size="XS" avatarUrl="" :userName="getVendorText"></FAvatar>
          <FText
            appearance="bodyReg14"
            marginLeft="containerLarge"
            color="textMain"
            >{{ getVendor }}</FText
          >
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import {
  FContainer,
  FText,
  FAvatar,
  FCard,
  FRecordDetails,
  FDivider,
} from '@facilio/design-system'

export default {
  name: 'Responsibility',
  props: ['moduleName', 'details', 'widget'],
  components: {
    FAvatar,
    FContainer,
    FText,
    FCard,
    FRecordDetails,
    FDivider,
  },
  data() {
    return {
      excludeFields: [],
    }
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Responsibility'
    },
    workorder() {
      return this.details
    },
    getStaff() {
      return this.details?.assignedTo?.name
        ? this.details.assignedTo.name
        : '---'
    },
    getTeam() {
      return this.details?.assignmentGroup?.name
        ? this.details.assignmentGroup.name
        : '---'
    },
    getVendor() {
      return this.details?.vendor?.name ? this.details.vendor.name : '---'
    },
    getStaffText() {
      return this.details?.assignedTo?.name ? this.details.assignedTo.name : ''
    },
    getTeamText() {
      return this.details?.assignmentGroup?.name
        ? this.details.assignmentGroup.name
        : ''
    },
    getVendorText() {
      return this.details?.vendor?.name ? this.details.vendor.name : ''
    },
  },
  methods: {
    canHideField(fieldName) {
      return this.excludeFields.includes(fieldName)
    },
    vendorLicenseEnabled() {
      // const { isLicenseEnabled } = helpers
      const vendorLicense = isLicenseEnabled('VENDOR')
      return vendorLicense
    },
  },
}
</script>
