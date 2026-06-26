package io.github.spah1879.doclet.writer;

import static org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth.*;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.assorted.DocDescription.Field;
import io.github.spah1879.doclet.assorted.DocDescription.Method;
import io.github.spah1879.doclet.assorted.DocDescription.Parameter;

public class DocxWriter extends DocWriter {

  private static final int BASE_COLUMN_COUNT = 24;
  private static final int CELL_UNIT_WIDTH = 368;
  private static final int CLASS_ROW_COUNT = 2;
  private static final int METHOD_ROW_COUNT_OF_TITLE = 1;
  private static final int METHOD_ROW_COUNT_PER_METHOD = 2;
  private static final int PARAMETER_ROW_COUNT_OF_TITLE = 1;

  private static final String TITLE_NAME = "이름";
  private static final String TITLE_PACKAGE = "패키지";
  private static final String TITLE_TYPE = "타입";
  private static final String TITLE_SINCE = "작성일";
  private static final String TITLE_AUTHOR = "작성자";
  private static final String TITLE_PROPERTY = "속성";
  private static final String TITLE_NO_PROPERTY = "속성 없음";
  private static final String TITLE_MODIFIERS = "제어자";
  private static final String TITLE_DESCRIPTION = "설명";
  private static final String TITLE_METHOD = "메소드";
  private static final String TITLE_NO_METHOD = "메소드 없음";
  private static final String TITLE_RETURN_TYPE = "리턴타입";
  private static final String TITLE_PARAMETERS = "파라미터";
  private static final String TITLE_DETAIL = "상세";

  private XWPFDocument doc;
  private XWPFTable table;

  public static DocxWriter newInstance() {
    return new DocxWriter();
  }

  private void setupDocument(XWPFDocument doc) {
    this.doc = doc;
  }

  private void setupTable(int rowCount) {
    this.table = this.doc.createTable(rowCount, BASE_COLUMN_COUNT);

    // Force a FIXED layout layout rule so Word respects custom widths
    CTTblPr tblPr = table.getCTTbl().getTblPr();
    if (tblPr == null)
      tblPr = table.getCTTbl().addNewTblPr();
    CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
    layoutType.setType(STTblLayoutType.FIXED);

    // Get or create cell margin properties
    CTTblCellMar cellMar = tblPr.isSetTblCellMar() ? tblPr.getTblCellMar() : tblPr.addNewTblCellMar();

    // Set margins in Twips (1/1440 inch). Example: 120 twips ≈ 6pt
    cellMar.addNewTop().setW(BigInteger.valueOf(0));
    cellMar.addNewBottom().setW(BigInteger.valueOf(0));
    cellMar.addNewLeft().setW(BigInteger.valueOf(109));
    cellMar.addNewRight().setW(BigInteger.valueOf(109));

    // Explicitly set the type of unit to "dxa" (twips)
    cellMar.getTop().setType(DXA);
    cellMar.getBottom().setType(DXA);
    cellMar.getLeft().setType(DXA);
    cellMar.getRight().setType(DXA);

    // Populate and size the table row-by-row
    for (XWPFTableRow row : table.getRows()) {
      for (XWPFTableCell cell : row.getTableCells()) {
        // Assign explicit width converted to a String format
        cell.setWidth(String.valueOf(CELL_UNIT_WIDTH));
        // Optional text alignment
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
      }
    }

    // Set the table alignment to center
    table.setTableAlignment(TableRowAlign.CENTER);

    // Set thick/bold outer and inner lines for the table
    final int thickness = 12; // 12 * 1/8 pt = 2.5pt thick bold line
    final String color = "000000"; // Black

    table.setTopBorder(XWPFBorderType.SINGLE, thickness, 0, color);
    table.setBottomBorder(XWPFBorderType.SINGLE, thickness, 0, color);
    table.setLeftBorder(XWPFBorderType.SINGLE, thickness, 0, color);
    table.setRightBorder(XWPFBorderType.SINGLE, thickness, 0, color);
  }

  private String getModifierString(List<String> modifiers) {
    return modifiers.stream().collect(Collectors.joining(" "));
  }

  private void mergeHorizontalCells(XWPFTableRow row, int startCol, int colSpan) {
    XWPFTableCell cell = row.getCell(startCol);
    // Set the grid span for the cell to merge it across multiple columns
    CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
    CTDecimalNumber gridSpan = tcPr.isSetGridSpan() ? tcPr.getGridSpan() : tcPr.addNewGridSpan();
    gridSpan.setVal(BigInteger.valueOf(colSpan));
    // Adjust the width of the cell to accommodate the span
    cell.setWidth(String.valueOf(CELL_UNIT_WIDTH * colSpan));
    // Remove the extra cells that are now spanned by the merged cell
    for (int i = startCol + colSpan - 1; i > startCol; i--) {
      row.removeCell(i);
    }
  }

  private void mergeVerticalCells(int col, int fromRow, int toRow) {
    for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
      XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
      CTTcPr tcPr = cell.getCTTc().getTcPr();
      if (tcPr == null)
        tcPr = cell.getCTTc().addNewTcPr();

      CTVMerge vMerge = tcPr.addNewVMerge();
      if (rowIndex == fromRow) {
        // First cell starts the merge
        vMerge.setVal(STMerge.RESTART);
      } else {
        // Other cells continue the merge
        vMerge.setVal(STMerge.CONTINUE);
      }
    }
  }

  private void setCellTextAndProperties(XWPFTableCell cell, String text, boolean isTitle) {
    if (isTitle) {
      cell.setColor("D2D2D2");
      XWPFParagraph paragraph = cell.getParagraphs().get(0);
      XWPFRun run = paragraph.createRun();
      run.setText(text);
      run.setBold(true);
    } else {
      cell.setText(text);
    }
  }

  private void setBorderGridlineBold(XWPFTableRow row) {
    final int thickness = 12; // 12 * 1/8 pt = 2.5pt thick bold line
    final String color = "000000"; // Black

    for (XWPFTableCell cell : row.getTableCells()) {
      // 1. Get or create cell properties
      CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
      // 2. Get or create cell borders object
      CTTcBorders tcBorders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();
      // 3. Define the bottom border line
      CTBorder bottomBorder = tcBorders.isSetBottom() ? tcBorders.getBottom() : tcBorders.addNewBottom();

      bottomBorder.setVal(STBorder.SINGLE);
      bottomBorder.setSz(BigInteger.valueOf(thickness));
      bottomBorder.setColor(color);
    }
  }

  private void describeClass(DocDescription desc) {

    XWPFTableRow row1 = table.getRow(0);
    mergeHorizontalCells(row1, 16, 8);
    mergeHorizontalCells(row1, 12, 4);
    mergeHorizontalCells(row1, 4, 8);
    mergeHorizontalCells(row1, 0, 4);
    setCellTextAndProperties(row1.getCell(0), TITLE_NAME, true);
    setCellTextAndProperties(row1.getCell(1), desc.getName(), false);
    setCellTextAndProperties(row1.getCell(2), TITLE_PACKAGE, true);
    setCellTextAndProperties(row1.getCell(3), desc.getPackageName(), false);

    XWPFTableRow row2 = table.getRow(1);
    mergeHorizontalCells(row2, 19, 5);
    mergeHorizontalCells(row2, 16, 3);
    mergeHorizontalCells(row2, 11, 5);
    mergeHorizontalCells(row2, 8, 3);
    mergeHorizontalCells(row2, 3, 5);
    mergeHorizontalCells(row2, 0, 3);
    Map<String, String> tags = desc.getTags();
    setCellTextAndProperties(row2.getCell(0), TITLE_TYPE, true);
    setCellTextAndProperties(row2.getCell(1), desc.getType(), false);
    setCellTextAndProperties(row2.getCell(2), TITLE_AUTHOR, true);
    setCellTextAndProperties(row2.getCell(3), tags.getOrDefault("author", ""), false);
    setCellTextAndProperties(row2.getCell(4), TITLE_SINCE, true);
    setCellTextAndProperties(row2.getCell(5), tags.getOrDefault("since", ""), false);

    setBorderGridlineBold(row2);
  }

  private void describeFields(List<Field> fields, int rowIndex) {
    XWPFTableRow row1 = table.getRow(rowIndex);
    mergeHorizontalCells(row1, 0, BASE_COLUMN_COUNT);
    if (fields.isEmpty()) {
      setCellTextAndProperties(row1.getCell(0), TITLE_NO_PROPERTY, true);
      return;
    }
    setCellTextAndProperties(row1.getCell(0), TITLE_PROPERTY, true);
    rowIndex++;

    XWPFTableRow row2 = table.getRow(rowIndex);
    mergeHorizontalCells(row2, 16, 8);
    mergeHorizontalCells(row2, 12, 4);
    mergeHorizontalCells(row2, 6, 6);
    mergeHorizontalCells(row2, 0, 6);
    setCellTextAndProperties(row2.getCell(0), TITLE_NAME, true);
    setCellTextAndProperties(row2.getCell(1), TITLE_MODIFIERS, true);
    setCellTextAndProperties(row2.getCell(2), TITLE_TYPE, true);
    setCellTextAndProperties(row2.getCell(3), TITLE_DESCRIPTION, true);
    rowIndex++;

    for (Field field : fields) {
      XWPFTableRow fieldRow = table.getRow(rowIndex);
      mergeHorizontalCells(fieldRow, 16, 8);
      mergeHorizontalCells(fieldRow, 12, 4);
      mergeHorizontalCells(fieldRow, 6, 6);
      mergeHorizontalCells(fieldRow, 0, 6);
      setCellTextAndProperties(fieldRow.getCell(0), field.getName(), false);
      setCellTextAndProperties(fieldRow.getCell(1), getModifierString(field.getModifiers()), false);
      setCellTextAndProperties(fieldRow.getCell(2), field.getType().getSimple(), false);
      setCellTextAndProperties(fieldRow.getCell(3), field.getComment().getFirstSentence(), false);
      rowIndex++;
    }

    setBorderGridlineBold(table.getRow(rowIndex - 1));
  }

  private void describeMethods(List<Method> methods, int rowIndex) {
    XWPFTableRow row = table.getRow(rowIndex);
    mergeHorizontalCells(row, 0, BASE_COLUMN_COUNT);
    if (methods.isEmpty()) {
      setCellTextAndProperties(row.getCell(0), TITLE_NO_METHOD, true);
      return;
    }
    setCellTextAndProperties(row.getCell(0), TITLE_METHOD, true);
    rowIndex++;

    for (Method method : methods) {
      row = table.getRow(rowIndex);
      mergeHorizontalCells(row, 15, 9);
      mergeHorizontalCells(row, 12, 3);
      mergeHorizontalCells(row, 3, 9);
      mergeHorizontalCells(row, 0, 3);
      setCellTextAndProperties(row.getCell(0), TITLE_NAME, true);
      setCellTextAndProperties(row.getCell(1), method.getName(), false);
      setCellTextAndProperties(row.getCell(2), TITLE_DESCRIPTION, true);
      setCellTextAndProperties(row.getCell(3), method.getComment().getFirstSentence(), false);
      rowIndex++;

      row = table.getRow(rowIndex);
      mergeHorizontalCells(row, 16, 8);
      mergeHorizontalCells(row, 13, 3);
      mergeHorizontalCells(row, 9, 4);
      mergeHorizontalCells(row, 6, 3);
      mergeHorizontalCells(row, 3, 3);
      mergeHorizontalCells(row, 0, 3);
      setCellTextAndProperties(row.getCell(0), TITLE_MODIFIERS, true);
      setCellTextAndProperties(row.getCell(1), getModifierString(method.getModifiers()), false);
      setCellTextAndProperties(row.getCell(2), TITLE_RETURN_TYPE, true);
      setCellTextAndProperties(row.getCell(3), method.getReturnType().getSimple(), false);
      setCellTextAndProperties(row.getCell(4), TITLE_PARAMETERS, true);
      setCellTextAndProperties(row.getCell(5), method.getFlatSignature(), false);
      rowIndex++;

      List<Parameter> parameters = method.getParameters();
      if (!parameters.isEmpty()) {
        row = table.getRow(rowIndex);
        int fromRow = table.getRows().indexOf(row);
        mergeHorizontalCells(row, 11, 13);
        mergeHorizontalCells(row, 7, 4);
        mergeHorizontalCells(row, 3, 4);
        mergeHorizontalCells(row, 0, 3);
        setCellTextAndProperties(row.getCell(0), TITLE_DETAIL, true);
        setCellTextAndProperties(row.getCell(1), TITLE_NAME, true);
        setCellTextAndProperties(row.getCell(2), TITLE_TYPE, true);
        setCellTextAndProperties(row.getCell(3), TITLE_DESCRIPTION, true);
        rowIndex++;

        for (Parameter parameter : parameters) {
          row = table.getRow(rowIndex);
          mergeHorizontalCells(row, 11, 13);
          mergeHorizontalCells(row, 7, 4);
          mergeHorizontalCells(row, 3, 4);
          mergeHorizontalCells(row, 0, 3);
          setCellTextAndProperties(row.getCell(1), parameter.getName(), false);
          setCellTextAndProperties(row.getCell(2), parameter.getType().getSimple(), false);
          setCellTextAndProperties(row.getCell(3), parameter.getComment(), false);
          rowIndex++;
        }
        mergeVerticalCells(0, fromRow, fromRow + parameters.size());
      }
    }
  }

  private void finish() {
    // Insert Page Break
    XWPFParagraph breakParagraph = doc.createParagraph();
    XWPFRun breakRun = breakParagraph.createRun();
    breakRun.addBreak(BreakType.PAGE);
  }

  private void describe(DocDescription desc) {
    final int propertiesRowCount = desc.getFields().isEmpty() ? 1 : desc.getFields().size() + 2;
    int parametersRowTotalCount = 0;
    final List<Method> methods = desc.getMethods();
    for (Method method : methods) {
      if (!method.getParameters().isEmpty()) {
        parametersRowTotalCount += method.getParameters().size() + PARAMETER_ROW_COUNT_OF_TITLE;
      }
    }
    final int methodsRowCount = METHOD_ROW_COUNT_OF_TITLE + methods.size() * METHOD_ROW_COUNT_PER_METHOD;
    final int totalRowCount = CLASS_ROW_COUNT + propertiesRowCount + methodsRowCount + parametersRowTotalCount;

    setupTable(totalRowCount);
    describeClass(desc);
    describeFields(desc.getFields(), CLASS_ROW_COUNT);
    describeMethods(desc.getMethods(), CLASS_ROW_COUNT + propertiesRowCount);
    finish();
  }

  @Override
  public void write(List<DocDescription> descriptions, File file, List<String> outputForamts) throws Exception {

    try (XWPFDocument doc = new XWPFDocument(); FileOutputStream out = new FileOutputStream(file)) {
      setupDocument(doc);

      for (DocDescription desc : descriptions) {
        describe(desc);
      }

      doc.write(out);
    }
  }
}
