<template>
  <div class="fc-form-container full-layout-white">
    <div class="row">
      <div class="col-lg-12 col-md-12">
        <div class="pull-left form-header">Locations</div>
        <div class="action-btn pull-right">
          <button class="btn btn--primary" @click="newLocationDialog">
            New Location
          </button>
        </div>
      </div>
    </div>
    <div class="row pT30">
      <div class="col-lg-12 col-md-12">
        <table class="fc-list-view-table fc-border-1">
          <thead>
            <tr>
              <th class="text-left" style="width:350px;">
                NAME
              </th>
              <th class="text-left" style="width:350px;">
                STREET
              </th>
              <th class="text-left" style="width:300px;">
                CITY
              </th>
              <th class="text-left" style="width:300px;">
                STATE
              </th>
              <th class="text-left" style="width:300px;">
                ZIPCODE
              </th>
              <th class="text-left" style="width:300px;">
                COUNTRY
              </th>
              <th class="text-left" style="width:300px;">
                LATITUDE
              </th>
              <th class="text-left" style="width:300px;">
                LONGITUDE
              </th>
              <th class="text-left" style="width:300px;">
                CONTACT
              </th>
              <th class="text-left" style="width:300px;">
                PHONE
              </th>
              <th class="text-left" style="width:300px;">
                FAX
              </th>
              <th class="text-left"></th>
              <th class="text-left"></th>
            </tr>
          </thead>
          <tbody v-if="loading">
            <tr>
              <td colspan="100%" class="text-center">
                <spinner :show="loading" size="80"></spinner>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow"
              v-for="location in locations"
              :key="location.id"
            >
              <td>
                {{ location.name }}
              </td>
              <td>
                {{ location.street }}
              </td>
              <td>
                {{ location.city }}
              </td>
              <td>
                {{ location.state }}
              </td>
              <td>
                {{ location.zip }}
              </td>
              <td>
                {{ location.country }}
              </td>
              <td>
                {{ location.lat }}
              </td>
              <td>
                {{ location.lng }}
              </td>
              <td v-if="location.contact">
                {{ $store.getters.getUser(location.contact.id).name }}
              </td>
              <td v-else>
                {{ location.contact }}
              </td>
              <td>
                {{ location.phone }}
              </td>
              <td>
                {{ location.faxPhone }}
              </td>
              <td>
                <button
                  class="btn btn--primary"
                  @click="editLocationDialog(location)"
                >
                  Edit
                </button>
              </td>
              <td>
                <button
                  class="btn btn--primary"
                  @click="deleteLocation(location, location.id)"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <new-location ref="newLocation"></new-location>
    <edit-location ref="editLocation"></edit-location>
  </div>
</template>
<script>
import NewLocation from 'pages/setup/NewLocation'
import EditLocation from 'pages/setup/EditLocation'
export default {
  title() {
    return 'Locations'
  },
  components: {
    NewLocation,
    EditLocation,
  },
  data() {
    return {
      loading: true,
      locations: [],
      isNewSkill: false,
    }
  },
  mounted: function() {
    this.loadlocations()
  },
  methods: {
    loadlocations: function() {
      let that = this
      that.$http
        .get('/location/locationlist')
        .then(function(response) {
          console.log('£££££££££££  locationlist ', response.data)
          that.locations = response['data'].locations
          that.loading = false
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    newLocationDialog() {
      this.$refs.newLocation.open()
    },
    editLocationDialog(data) {
      this.$refs.editLocation.open(data)
    },
    deleteLocation(data, id) {
      console.log('data : ', data)
      console.log('delete data', { location: data })
      let that = this
      that.$http
        .post('/location/delete', { locationIds: [id] })
        .then(function(response) {
          console.log('########### delete location', response.data)
          that.locations.splice(id, 1)
        })
    },
  },
}
</script>
<style scoped>
table.dataTable > tbody > tr > td {
  padding: 10px;
  vertical-align: middle;
  border-spacing: 0;
  border-collapse: collapse;
}

table.dataTable thead > tr > th {
  padding: 5px 10px 2px 10px;
  color: #6f7175;
  vertical-align: top;
  font-weight: 400;
  font-size: 13px;
  border-bottom: 0px;
}

table.dataTable tr.odd {
  background-color: '#fafafa';
}

table.dataTable tr.even {
  background-color: blue;
}

div.dataTables_info {
  padding-top: 8px;
  white-space: nowrap;
  padding-left: 10px;
}

div.dataTables_info,
div.dataTables_paginate {
  padding: 18px;
  white-space: nowrap;
}

div.row-title {
  font-weight: 400;
}

div.row-subtitle {
  font-weight: 400;
  color: #6f7175;
}

.dataTable tbody tr:hover {
  background: #fafafa;
  cursor: pointer;
}

.dataTable tr th .checkbox {
  padding-left: 17px !important;
}

.dataTable tbody tr:last-child td {
  border-bottom: 1px solid #e7e7e7 !important;
}

.dataTable > tbody > tr:first-child > td {
  border-top: 0px;
}

div.row.content-center {
  padding-top: 100px;
  padding-bottom: 144px;
}

table.dataTable.dtr-inline.collapsed > tbody > tr > td:first-child:before,
table.dataTable.dtr-inline.collapsed > tbody > tr > th:first-child:before {
  background-color: #50ca7c;
  font-size: 16px;
  line-height: 16px;
  display: none;
}
.no-screen-msg .row-title {
  font-size: 17px;
  color: #212121;
  padding: 10px 0;
}
.no-screen-msg .row-subtitle {
  font-size: 13px;
  padding: 1px 0px;
}

.dataTable tbody tr.selected {
  background: rgba(14, 153, 227, 0.1);
}
.record-list,
.record-summary {
  padding: 0;
  transition: all 0.3s;
}
.more-actions .dropdown-toggle {
  color: #d8d8d8;
  font-size: 18px;
}

.more-actions .dropdown-toggle:hover {
  color: #000000;
}

.more-actions .dropdown-menu {
  right: 0;
  left: initial;
}
.toggle-switch label {
  position: relative;
  display: block;
  height: 12px;
  width: 30px;
  background: #999;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.08s linear;
}

.toggle-switch label:after {
  position: absolute;
  left: 0;
  top: -2px;
  display: block;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fafafa;
  box-shadow: rgba(0, 0, 0, 0.4) 0px 1px 3px 0px;
  content: '';
  transition: 0.08s linear;
}

.toggle-switch label:active:after {
  transform: scale(1.15, 0.85);
}

.toggle-switch .checkbox:checked ~ label {
  background: rgba(80, 202, 124, 0.5);
}

.toggle-switch .checkbox:checked ~ label:after {
  left: 14px;
  background: #50ca7c;
}

.toggle-switch .checkbox:disabled ~ label {
  background: #d5d5d5;
  cursor: not-allowed;
  pointer-events: none;
}

.toggle-switch .checkbox:disabled ~ label:after {
  background: #bcbdbc;
}
</style>
