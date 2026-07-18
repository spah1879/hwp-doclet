package io.github.spah1879.doclet.writer;

import static io.github.spah1879.doclet.writer.common.TitleConstants.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.assorted.DocDescription.Field;
import io.github.spah1879.doclet.assorted.DocDescription.Method;
import io.github.spah1879.doclet.assorted.DocDescription.Parameter;

public class XlsxWriter extends DocWriter {

  private static final int BASE_COLUMN_COUNT = 12;
  private static final int CLASS_ROW_COUNT = 2;
  private static final int METHOD_ROW_COUNT_OF_TITLE = 1;
  private static final int METHOD_ROW_COUNT_PER_METHOD = 2;
  private static final int PARAMETER_ROW_COUNT_OF_TITLE = 1;

  Sheet sheet;
  Font headerFont;

  public static XlsxWriter newInstance() {
    return new XlsxWriter();
  }

  private void setupSanitizedUniqueSheet(Workbook workbook, String name) {
    String sanitized = name == null ? "Sheet" : name.replaceAll("[^A-Za-z0-9 _.-]", "");
    if (sanitized.trim().isEmpty()) {
      sanitized = "Sheet";
    }
    String uniqueName = sanitized.length() > 31 ? sanitized.substring(0, 31) : sanitized;

    int suffix = 1;
    while (workbook.getSheet(uniqueName) != null) {
      uniqueName = uniqueName.length() + suffix <= 31 ? uniqueName + suffix
          : uniqueName.substring(0, 31 - String.valueOf(suffix).length()) + suffix;
      suffix++;
    }

    sheet = workbook.createSheet(uniqueName);
  }

  private void setHeaderFont(Workbook workbook) {
    headerFont = workbook.createFont();
    headerFont.setBold(true);
  }

  private void mergeHorizontalCells(int rowNum, int startCol, int colSpan) {
    int endCol = startCol + colSpan - 1;
    CellRangeAddress mergeRange = new CellRangeAddress(rowNum, rowNum, startCol, endCol);
    sheet.addMergedRegion(mergeRange);
  }

  private void mergeVerticalCells(int colNum, int startRow, int rowSpan) {
    int endRow = startRow + rowSpan - 1;
    CellRangeAddress mergeRange = new CellRangeAddress(startRow, endRow, colNum, colNum);
    sheet.addMergedRegion(mergeRange);
  }

  void setOutline(int firstRow, int lastRow) {
    Workbook workbook = sheet.getWorkbook();
    final int lasCol = BASE_COLUMN_COUNT - 1;
    for (int rowNum = firstRow; rowNum <= lastRow; rowNum++) {
      Row row = sheet.getRow(rowNum);
      for (int colNum = 0; colNum <= lasCol; colNum++) {
        final int col = colNum;
        Cell cell = Objects.requireNonNullElseGet(row.getCell(colNum), () -> row.createCell(col));
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setBorderTop(rowNum == firstRow ? BorderStyle.MEDIUM : BorderStyle.THIN);
        style.setBorderBottom(rowNum == lastRow ? BorderStyle.MEDIUM : BorderStyle.THIN);
        style.setBorderLeft(colNum == 0 ? BorderStyle.MEDIUM : BorderStyle.THIN);
        style.setBorderRight(colNum == lasCol ? BorderStyle.MEDIUM : BorderStyle.THIN);
        cell.setCellStyle(style);
      }
    }
  }

  private void setCell(Row row, int col, String value, boolean isTitle) {
    Cell cell = Objects.requireNonNullElseGet(row.getCell(col), () -> row.createCell(col));
    CellStyle cellStyle = sheet.getWorkbook().createCellStyle();

    cellStyle.cloneStyleFrom(cell.getCellStyle());
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    if (isTitle) {
      cellStyle.setFont(headerFont);
      cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }
    cell.setCellStyle(cellStyle);
    cell.setCellValue(value == null ? "" : value);
  }

  private void describeClass(DocDescription desc) {
    Row row1 = sheet.getRow(0);
    mergeHorizontalCells(0, 0, 2);
    mergeHorizontalCells(0, 2, 4);
    mergeHorizontalCells(0, 6, 2);
    mergeHorizontalCells(0, 8, 4);
    setCell(row1, 0, TITLE_NAME, true);
    setCell(row1, 2, desc.getName(), false);
    setCell(row1, 6, TITLE_PACKAGE, true);
    setCell(row1, 8, desc.getPackageName(), false);

    Row row2 = sheet.getRow(1);
    mergeHorizontalCells(1, 1, 3);
    mergeHorizontalCells(1, 5, 3);
    mergeHorizontalCells(1, 9, 3);
    Map<String, String> tags = desc.getTags();
    setCell(row2, 0, TITLE_TYPE, true);
    setCell(row2, 1, desc.getType(), false);
    setCell(row2, 4, TITLE_AUTHOR, true);
    setCell(row2, 5, tags.getOrDefault("author", ""), false);
    setCell(row2, 8, TITLE_SINCE, true);
    setCell(row2, 9, tags.getOrDefault("since", ""), false);

    setOutline(0, 1);
  }

  private void describeFields(List<Field> fields, int rowIndex) {
    final int firstRow = rowIndex;

    Row row = sheet.getRow(rowIndex);
    mergeHorizontalCells(rowIndex, 0, BASE_COLUMN_COUNT);
    if (fields.isEmpty()) {
      setCell(row, 0, TITLE_NO_PROPERTY, true);
      setOutline(firstRow, firstRow);
      return;
    }
    setCell(row, 0, TITLE_PROPERTY, true);
    rowIndex++;

    Row row2 = sheet.getRow(rowIndex);
    mergeHorizontalCells(rowIndex, 0, 3);
    mergeHorizontalCells(rowIndex, 3, 3);
    mergeHorizontalCells(rowIndex, 6, 2);
    mergeHorizontalCells(rowIndex, 8, 4);
    setCell(row2, 0, TITLE_NAME, true);
    setCell(row2, 3, TITLE_MODIFIERS, true);
    setCell(row2, 6, TITLE_TYPE, true);
    setCell(row2, 8, TITLE_DESCRIPTION, true);
    rowIndex++;

    for (Field field : fields) {
      Row fieldRow = sheet.getRow(rowIndex);
      mergeHorizontalCells(rowIndex, 0, 3);
      mergeHorizontalCells(rowIndex, 3, 3);
      mergeHorizontalCells(rowIndex, 6, 2);
      mergeHorizontalCells(rowIndex, 8, 4);
      setCell(fieldRow, 0, field.getName(), false);
      setCell(fieldRow, 3, getModifierString(field.getModifiers()), false);
      setCell(fieldRow, 6, field.getType().getSimple(), false);
      setCell(fieldRow, 8, field.getComment().getFirstSentence(), false);
      rowIndex++;
    }

    setOutline(firstRow, rowIndex - 1);
  }

  private void describeMethods(List<Method> methods, int rowIndex) {
    final int firstRow = rowIndex;

    Row row = sheet.getRow(rowIndex);
    mergeHorizontalCells(rowIndex, 0, BASE_COLUMN_COUNT);
    if (methods.isEmpty()) {
      setCell(row, 0, TITLE_NO_METHOD, true);
      setOutline(firstRow, firstRow);
      return;
    }
    setCell(row, 0, TITLE_METHOD, true);
    rowIndex++;

    for (Method method : methods) {
      row = sheet.getRow(rowIndex);
      mergeHorizontalCells(rowIndex, 1, 5);
      mergeHorizontalCells(rowIndex, 7, 5);
      setCell(row, 0, TITLE_NAME, true);
      setCell(row, 1, method.getName(), false);
      setCell(row, 6, TITLE_DESCRIPTION, true);
      setCell(row, 7, method.getComment().getFirstSentence(), false);
      rowIndex++;

      row = sheet.getRow(rowIndex);
      mergeHorizontalCells(rowIndex, 1, 2);
      mergeHorizontalCells(rowIndex, 4, 3);
      mergeHorizontalCells(rowIndex, 8, 4);
      setCell(row, 0, TITLE_MODIFIERS, true);
      setCell(row, 1, getModifierString(method.getModifiers()), false);
      setCell(row, 3, TITLE_RETURN_TYPE, true);
      setCell(row, 4, method.getReturnType().getSimple(), false);
      setCell(row, 7, TITLE_PARAMETERS, true);
      setCell(row, 8, method.getFlatSignature(), false);
      rowIndex++;

      List<Parameter> parameters = method.getParameters();
      if (!parameters.isEmpty()) {
        row = sheet.getRow(rowIndex);
        final int fromRow = rowIndex;
        mergeHorizontalCells(rowIndex, 1, 2);
        mergeHorizontalCells(rowIndex, 3, 2);
        mergeHorizontalCells(rowIndex, 5, 7);
        setCell(row, 0, TITLE_DETAIL, true);
        setCell(row, 1, TITLE_NAME, true);
        setCell(row, 3, TITLE_TYPE, true);
        setCell(row, 5, TITLE_DESCRIPTION, true);
        rowIndex++;

        for (Parameter parameter : parameters) {
          row = sheet.getRow(rowIndex);
          mergeHorizontalCells(rowIndex, 1, 2);
          mergeHorizontalCells(rowIndex, 3, 2);
          mergeHorizontalCells(rowIndex, 5, 7);
          setCell(row, 1, parameter.getName(), false);
          setCell(row, 3, parameter.getType().getSimple(), false);
          setCell(row, 5, parameter.getComment(), false);
          rowIndex++;
        }
        mergeVerticalCells(0, fromRow, parameters.size() + 1);
      }
    }

    setOutline(firstRow, rowIndex - 1);
  }

  private void describe(DocDescription desc, Workbook workbook) {
    final int propRowCount = desc.getFields().isEmpty() ? 1 : desc.getFields().size() + 2;
    int paramRowTotalCount = 0;
    final List<Method> methods = desc.getMethods();
    for (Method method : methods) {
      if (!method.getParameters().isEmpty()) {
        paramRowTotalCount += method.getParameters().size() + PARAMETER_ROW_COUNT_OF_TITLE;
      }
    }
    final int methodsRowCount = METHOD_ROW_COUNT_OF_TITLE + methods.size() * METHOD_ROW_COUNT_PER_METHOD;
    final int totalRowCount = CLASS_ROW_COUNT + propRowCount + methodsRowCount + paramRowTotalCount;

    setupSanitizedUniqueSheet(workbook, desc.getName());
    setHeaderFont(workbook);

    for (int rowNum = 0; rowNum < totalRowCount; rowNum++) {
      Row row = sheet.createRow(rowNum);
      for (int colNum = 0; colNum < BASE_COLUMN_COUNT; colNum++) {
        sheet.setColumnWidth(colNum, 10 * 256);
        row.createCell(colNum);
      }
    }

    describeClass(desc);
    describeFields(desc.getFields(), CLASS_ROW_COUNT);
    describeMethods(desc.getMethods(), CLASS_ROW_COUNT + propRowCount);
  }

  @Override
  public void write(List<DocDescription> descriptions, File file, List<String> outputForamts) throws Exception {

    try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {

      for (DocDescription desc : descriptions) {
        describe(desc, workbook);
      }

      workbook.write(out);
    }
  }
}
