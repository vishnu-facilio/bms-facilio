class Cell {
  constructor(rowId, colId, rowIndex, colIndex) {
    this.rowId = rowId
    this.colId = colId
    this.rowIndex = rowIndex
    this.colIndex = colIndex
    this.cellID = rowIndex + '' + colIndex
  }
  equals(otherCell) {
    if (otherCell) {
      return otherCell.cellID == this.cellID
    } else {
      return false
    }
  }
}

export default Cell
