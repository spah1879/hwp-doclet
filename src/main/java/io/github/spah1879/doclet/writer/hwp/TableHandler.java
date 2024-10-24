package io.github.spah1879.doclet.writer.hwp;

import java.util.Collections;
import java.util.List;

import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.ControlTable;
import kr.dogfoot.hwplib.object.bodytext.control.ControlType;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.CtrlHeaderGso;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.HeightCriterion;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.HorzRelTo;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.ObjectNumberSort;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.RelativeArrange;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.TextFlowMethod;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.TextHorzArrange;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.VertRelTo;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.gso.WidthCriterion;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.sectiondefine.TextDirection;
import kr.dogfoot.hwplib.object.bodytext.control.gso.textbox.LineChange;
import kr.dogfoot.hwplib.object.bodytext.control.gso.textbox.TextVerticalAlignment;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.object.bodytext.control.table.DivideAtPageBoundary;
import kr.dogfoot.hwplib.object.bodytext.control.table.ListHeaderForCell;
import kr.dogfoot.hwplib.object.bodytext.control.table.Row;
import kr.dogfoot.hwplib.object.bodytext.control.table.Table;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.tool.TableCellMerger;

public class TableHandler {

  final ControlTable table;

  public TableHandler(Section section, boolean dividePage) {
    Paragraph paragraph = Common.addNewParagraph(section, dividePage);
    paragraph.getText().addExtendCharForTable();
    this.table = (ControlTable) paragraph.addNewControl(ControlType.Table);
  }

  public int getRowCount() {
    return table.getRowList().size();
  }

  public List<Cell> getCellsOfRow(int rowIndex) {
    return table.getRowList().get(rowIndex).getCellList();
  }

  public Cell getCell(int colIndex, int rowIndex) {
    List<Row> rows = table.getRowList();
    List<Cell> cells = rows.get(rowIndex).getCellList();
    return cells.get(colIndex);
  }

  public boolean mergeCell(int startRow, int startCol, int rowSpan, int colSpan) {
    return TableCellMerger.mergeCell(table, startRow, startCol, rowSpan, colSpan);
  }

  public void setCtrlHeaderRecord(int zOrder) {
    CtrlHeaderGso ctrlHeader = table.getHeader();
    ctrlHeader.getProperty().setLikeWord(false);
    ctrlHeader.getProperty().setApplyLineSpace(false);
    ctrlHeader.getProperty().setVertRelTo(VertRelTo.Para);
    ctrlHeader.getProperty().setVertRelativeArrange(RelativeArrange.TopOrLeft);
    ctrlHeader.getProperty().setHorzRelTo(HorzRelTo.Para);
    ctrlHeader.getProperty().setHorzRelativeArrange(RelativeArrange.Center);
    ctrlHeader.getProperty().setVertRelToParaLimit(false);
    ctrlHeader.getProperty().setAllowOverlap(false);
    ctrlHeader.getProperty().setWidthCriterion(WidthCriterion.Absolute);
    ctrlHeader.getProperty().setHeightCriterion(HeightCriterion.Absolute);
    ctrlHeader.getProperty().setProtectSize(false);
    ctrlHeader.getProperty().setTextFlowMethod(TextFlowMethod.FitWithText);
    ctrlHeader.getProperty().setTextHorzArrange(TextHorzArrange.BothSides);
    ctrlHeader.getProperty().setObjectNumberSort(ObjectNumberSort.Table);
    ctrlHeader.setxOffset(Common.mmToHwp(0.0));
    ctrlHeader.setyOffset(Common.mmToHwp(0.0));
    ctrlHeader.setWidth(Common.mmToHwp(120.0));
    ctrlHeader.setHeight(Common.mmToHwp(60.0));
    ctrlHeader.setzOrder(zOrder);
    ctrlHeader.setOutterMarginLeft((int) Common.mmToHwp(1.0));
    ctrlHeader.setOutterMarginRight((int) Common.mmToHwp(1.0));
    ctrlHeader.setOutterMarginTop((int) Common.mmToHwp(1.0));
    ctrlHeader.setOutterMarginBottom((int) Common.mmToHwp(1.0));
  }

  public void setTableRecordForCells(int borderFillId, int colums, int rows) {
    Table tableRecord = table.getTable();
    tableRecord.getProperty().setDivideAtPageBoundary(DivideAtPageBoundary.DivideByCell);
    tableRecord.getProperty().setAutoRepeatTitleRow(false);
    tableRecord.setColumnCount(colums);
    tableRecord.setRowCount(rows);
    tableRecord.setCellSpacing(0);
    tableRecord.setLeftInnerMargin((int) Common.mmToHwp(1.8));
    tableRecord.setRightInnerMargin((int) Common.mmToHwp(1.8));
    tableRecord.setTopInnerMargin((int) Common.mmToHwp(0.5));
    tableRecord.setBottomInnerMargin((int) Common.mmToHwp(0.5));
    tableRecord.setBorderFillId(borderFillId);

    List<Integer> cellCountOfRowList = tableRecord.getCellCountOfRowList();
    for (int n = 0; n < rows; n++) {
      cellCountOfRowList.add(colums);
      cellCountOfRowList.add(colums);
    }
  }

  public void setTableRecordForCells2(int borderFillId, List<Integer> columns) {
    Table tableRecord = table.getTable();
    tableRecord.getProperty().setDivideAtPageBoundary(DivideAtPageBoundary.DivideByCell);
    tableRecord.getProperty().setAutoRepeatTitleRow(false);
    tableRecord.setColumnCount(Collections.max(columns));
    tableRecord.setRowCount(columns.size());
    tableRecord.setCellSpacing(0);
    tableRecord.setLeftInnerMargin((int) Common.mmToHwp(1.8));
    tableRecord.setRightInnerMargin((int) Common.mmToHwp(1.8));
    tableRecord.setTopInnerMargin((int) Common.mmToHwp(0.5));
    tableRecord.setBottomInnerMargin((int) Common.mmToHwp(0.5));
    tableRecord.setBorderFillId(borderFillId);
    List<Integer> cellCountOfRowList = tableRecord.getCellCountOfRowList();
    for (Integer column : columns) {
      cellCountOfRowList.add(column);
    }
  }

  public void setListHeaderForCell(Cell cell, int colIndex, int rowIndex, int borderFillId) {
    ListHeaderForCell lh = cell.getListHeader();
    lh.setParaCount(1);
    lh.getProperty().setTextDirection(TextDirection.Horizontal);
    lh.getProperty().setLineChange(LineChange.Normal);
    lh.getProperty().setTextVerticalAlignment(TextVerticalAlignment.Center);
    lh.getProperty().setProtectCell(false);
    lh.getProperty().setEditableAtFormMode(false);
    lh.setColIndex(colIndex);
    lh.setRowIndex(rowIndex);
    lh.setColSpan(1);
    lh.setRowSpan(1);
    lh.setWidth(Common.mmToHwp(16.0));
    lh.setHeight(Common.mmToHwp(10.0));
    lh.setLeftMargin(0);
    lh.setRightMargin(0);
    lh.setTopMargin(0);
    lh.setBottomMargin(0);
    lh.setBorderFillId(borderFillId);
    lh.setTextWidth(Common.mmToHwp(16.0));
    lh.setFieldName("");
  }

  public void setListHeaderBorderFillForCell(int colIndex, int rowIndex, int borderFillId) {
    List<Row> rows = table.getRowList();
    List<Cell> cells = rows.get(rowIndex).getCellList();
    Cell cell = cells.get(colIndex);
    cell.getListHeader().setBorderFillId(borderFillId);
  }

  public void setListHeaderBorderFillForCell(Cell cell, int borderFillId) {
    cell.getListHeader().setBorderFillId(borderFillId);
  }

  public void addParagraphForCell(Cell cell, String text, int charShapeId) {
    Paragraph p = cell.getParagraphList().addNewParagraph();
    Common.setParaHeader(p);
    Common.setParaText(p, text);
    Common.setParaCharShape(p, charShapeId);
    Common.setParaLineSeg(p);
  }

  public void setParagraphForCell(int colIndex, int rowIndex, String text, int borderFillId, int charShapeId) {
    List<Row> rows = table.getRowList();
    List<Cell> cells = rows.get(rowIndex).getCellList();
    Cell cell = cells.get(colIndex);
    cell.getParagraphList().deleteAllParagraphs();

    if (borderFillId > 0) {
      setListHeaderBorderFillForCell(cell, borderFillId);
    }
    addParagraphForCell(cell, text, charShapeId);
  }

  public void setParagraphForCell(int colIndex, int rowIndex, String text, int borderFillId) {
    setParagraphForCell(colIndex, rowIndex, text, borderFillId, 1);
  }

  public void setParagraphForCell(int colIndex, int rowIndex, String text) {
    setParagraphForCell(colIndex, rowIndex, text, 0, 1);
  }

  public int addRow(int colNum, int cellBorderFillId) {
    int rowIndex = getRowCount();
    Row row = table.addNewRow();
    for (int colIndex = 0; colIndex < colNum; colIndex++) {
      Cell cell = row.addNewCell();
      setListHeaderForCell(cell, colIndex, rowIndex, cellBorderFillId);
      addParagraphForCell(cell, "", 1);
    }
    return rowIndex;
  }
}
