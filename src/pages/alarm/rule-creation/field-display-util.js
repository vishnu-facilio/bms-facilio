export const isLookupTypeField = field => {
  let { displayType } = field || {}
  return ['LOOKUP_SIMPLE', 'MULTI_LOOKUP_SIMPLE'].includes(displayType)
}
export const isLookupPopupField = field => {
  let { displayType } = field || {}
  return ['LOOKUP_POPUP'].includes(displayType)
}
export const isPicklistTypeField = field => {
  let { displayType } = field || {}
  return ['SELECTBOX', 'MULTI_SELECTBOX'].includes(displayType)
}
export const isBooleanTypeField = field => {
  let { displayType } = field || {}
  return ['DECISION_BOX'].includes(displayType)
}

export const isLookupField = field => {
  let { displayType } = field || {}
  return ['LOOKUP_SIMPLE'].includes(displayType)
}

export const isMultiLookupField = field => {
  let { displayType } = field || {}
  return ['MULTI_LOOKUP_SIMPLE'].includes(displayType)
}

export const isDateTypeField = field => {
  let { displayType } = field || {}
  return ['DATETIME', 'DATE'].includes(displayType)
}
