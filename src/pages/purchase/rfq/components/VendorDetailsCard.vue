<template>
  <div class="vendor-summary">
    <div class="vendor-detail">
      <div class="vendor-summary-heading">
        {{ $t('common.inventory.primary_contact_name') }}
      </div>
      <div class="vendor-summary-value">
        {{ vendorQuote.primaryContactName }}
      </div>
    </div>
    <div class="vendor-detail">
      <div class="vendor-summary-heading">
        {{ $t('common.inventory.email_id') }}
      </div>
      <div class="vendor-summary-value">
        {{ vendorQuote.emailId }}
      </div>
    </div>
    <div class="vendor-detail">
      <div class="vendor-summary-heading">
        {{ $t('common.inventory.contact_num') }}
      </div>
      <div class="vendor-summary-value">
        {{ vendorQuote.contactNumber }}
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'VendorDetailsCard',
  props: ['vendorQuote'],
}
</script>
<style scoped>
.vendor-summary {
  display: flex;
  padding-left: 18px;
  padding-top: 30px;
  padding-bottom: 30px;
}
.vendor-detail {
  width: 350px;
  padding-right: 30px;
}
.vendor-summary-heading {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #797b80;
  display: flex;
}
.vendor-summary-value {
  font-size: 14px;
  letter-spacing: 0.5px;
  color: #324056;
  word-break: break-all;
  padding-top: 10px;
}
</style>
