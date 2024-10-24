package io.github.spah1879.doclet.writer;

import static kr.dogfoot.hwplib.object.docinfo.borderfill.BorderThickness.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.assorted.DocDescription.Field;
import io.github.spah1879.doclet.assorted.DocDescription.Method;
import io.github.spah1879.doclet.assorted.DocDescription.Parameter;
import io.github.spah1879.doclet.assorted.DocDescription.Tag;
import io.github.spah1879.doclet.writer.hwp.Common;
import io.github.spah1879.doclet.writer.hwp.TableHandler;
import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.tool.blankfilemaker.BlankFileMaker;
import kr.dogfoot.hwplib.writer.HWPWriter;

public class HwpWriter extends DocWriter {

  private static final int BASE_COLUMN_COUNT = 12;
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

  private int boldCharShapeId;
  private int boldSlimCharShapeId;
  private int basicBorderFillId;
  private int basicShadeBorderFillId;
  private int topLeftShadeBorderFillId;
  private int topMiddleBorderFillId;
  private int topMiddleShadeBorderFillId;
  private int topRightBorderFillId;
  private int middleLeftBorderFillId;
  private int middleLeftShadeBorderFillId;
  private int middleRightBorderFillId;
  private int middleRightShadeBorderFillId;
  private int middleUpBoldShadeBorderFillId;
  private int bottomLeftBorderFillId;
  private int bottomLeftShadeBorderFillId;
  private int bottomMiddleBorderFillId;
  private int bottomMiddleShadeBorderFillId;
  private int bottomRightBorderFillId;
  private int bottomRightShadeBorderFillId;
  private int bottomUpBoldShadeBorderFillId;
  private List<Integer> middleShadeFillIds;

  private TableHandler tableHandler;

  public static HwpWriter newInstance() {
    return new HwpWriter();
  }

  private void prepareStockObjects(HWPFile hwpFile) {
    boldCharShapeId = Common.getCharShapeIdFromDefault(hwpFile, 100, true);
    boldSlimCharShapeId = Common.getCharShapeIdFromDefault(hwpFile, 90, true);
    basicBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_12, MM0_12, MM0_12);
    basicShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_12, MM0_12, MM0_12, true);
    topLeftShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_12, MM0_6, MM0_12, true);
    topMiddleBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_12, MM0_6, MM0_12);
    topMiddleShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_12, MM0_6, MM0_12, true);
    topRightBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_6, MM0_6, MM0_12);
    middleLeftBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_12, MM0_12, MM0_12);
    middleLeftShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_12, MM0_12, MM0_12, true);
    middleRightBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_6, MM0_12, MM0_12);
    middleRightShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_6, MM0_12, MM0_12, true);
    middleUpBoldShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_6, MM0_4, MM0_12, true);
    bottomLeftBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_12, MM0_12, MM0_6);
    bottomLeftShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_12, MM0_12, MM0_6, true);
    bottomMiddleBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_12, MM0_12, MM0_6);
    bottomMiddleShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_12, MM0_12, MM0_6, true);
    bottomRightBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_6, MM0_12, MM0_6);
    bottomRightShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_12, MM0_6, MM0_12, MM0_6, true);
    bottomUpBoldShadeBorderFillId = Common.getBorderFillIdForCell(hwpFile, MM0_6, MM0_6, MM0_4, MM0_6, true);
    middleShadeFillIds = Arrays.asList(middleLeftShadeBorderFillId, basicShadeBorderFillId,
        middleRightShadeBorderFillId);
  }

  private String getTagValue(List<Tag> tags, String tagName) {
    return tags.stream()
        .filter(t -> t.getName().equals(tagName))
        .findFirst()
        .map(Tag::getValue)
        .orElse("");
  }

  private String getModifierString(List<String> modifiers) {
    return modifiers.stream().collect(Collectors.joining(" "));
  }

  private boolean isCellShade(int colIndex, int rowIndex) {
    int borderFillId = tableHandler.getCell(colIndex, rowIndex).getListHeader().getBorderFillId();
    return middleShadeFillIds.contains(borderFillId);
  }

  private void describeClass(DocDescription desc, int rowIndex) {
    List<Tag> tags = desc.getTags();

    tableHandler.setParagraphForCell(0, rowIndex, TITLE_NAME, topLeftShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(1, rowIndex, desc.getName(), topMiddleBorderFillId);
    tableHandler.setParagraphForCell(2, rowIndex, TITLE_PACKAGE, topMiddleShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(3, rowIndex, desc.getPackageName(), topRightBorderFillId);
    rowIndex++;

    tableHandler.setParagraphForCell(0, rowIndex, TITLE_TYPE, middleLeftShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(1, rowIndex, desc.getType());
    tableHandler.setParagraphForCell(2, rowIndex, TITLE_AUTHOR, basicShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(3, rowIndex, getTagValue(tags, "author"));
    tableHandler.setParagraphForCell(4, rowIndex, TITLE_SINCE, basicShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(5, rowIndex, getTagValue(tags, "since"), middleRightBorderFillId);
  }

  private void describeFields(List<Field> fields, int rowIndex) {
    if (fields.isEmpty()) {
      tableHandler.setParagraphForCell(0, rowIndex, TITLE_NO_PROPERTY, middleUpBoldShadeBorderFillId,
          boldCharShapeId);
      return;
    }

    tableHandler.setParagraphForCell(0, rowIndex, TITLE_PROPERTY, middleUpBoldShadeBorderFillId, boldCharShapeId);
    rowIndex++;

    tableHandler.setParagraphForCell(0, rowIndex, TITLE_NAME, middleLeftShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(1, rowIndex, TITLE_MODIFIERS, basicShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(2, rowIndex, TITLE_TYPE, basicShadeBorderFillId, boldCharShapeId);
    tableHandler.setParagraphForCell(3, rowIndex, TITLE_DESCRIPTION, middleRightShadeBorderFillId, boldCharShapeId);
    rowIndex++;

    for (Field field : fields) {
      tableHandler.setParagraphForCell(0, rowIndex, field.getName(), middleLeftBorderFillId);
      tableHandler.setParagraphForCell(1, rowIndex, getModifierString(field.getModifiers()));
      tableHandler.setParagraphForCell(2, rowIndex, field.getType().getSimple());
      tableHandler.setParagraphForCell(3, rowIndex, field.getComment().getFirstSentence(),
          middleRightBorderFillId);
      rowIndex++;
    }
  }

  private void describeMethods(List<Method> methods, int rowIndex) {
    if (methods.isEmpty()) {
      tableHandler.setParagraphForCell(0, rowIndex, TITLE_NO_METHOD, middleUpBoldShadeBorderFillId, boldCharShapeId);
      return;
    }

    tableHandler.setParagraphForCell(0, rowIndex, TITLE_METHOD, middleUpBoldShadeBorderFillId, boldCharShapeId);
    rowIndex++;

    int index = 0;
    for (Method method : methods) {
      tableHandler.setParagraphForCell(0, rowIndex, TITLE_NAME, middleLeftShadeBorderFillId, boldCharShapeId);
      tableHandler.setParagraphForCell(1, rowIndex, method.getName());
      tableHandler.setParagraphForCell(2, rowIndex, TITLE_DESCRIPTION, basicShadeBorderFillId, boldCharShapeId);
      tableHandler.setParagraphForCell(3, rowIndex, method.getComment().getFirstSentence(), middleRightBorderFillId);
      rowIndex++;

      tableHandler.setParagraphForCell(0, rowIndex, TITLE_MODIFIERS, middleLeftShadeBorderFillId, boldCharShapeId);
      tableHandler.setParagraphForCell(1, rowIndex, getModifierString(method.getModifiers()));
      tableHandler.setParagraphForCell(2, rowIndex, TITLE_RETURN_TYPE, basicShadeBorderFillId, boldSlimCharShapeId);
      tableHandler.setParagraphForCell(3, rowIndex, method.getReturnType().getSimple());
      tableHandler.setParagraphForCell(4, rowIndex, TITLE_PARAMETERS, basicShadeBorderFillId, boldSlimCharShapeId);
      tableHandler.setParagraphForCell(5, rowIndex, method.getFlatSignature(), middleRightBorderFillId);

      rowIndex++;

      List<Parameter> parameters = methods.get(index).getParameters();
      if (!parameters.isEmpty()) {
        tableHandler.setParagraphForCell(0, rowIndex, TITLE_DETAIL, middleLeftShadeBorderFillId, boldCharShapeId);
        tableHandler.setParagraphForCell(1, rowIndex, TITLE_NAME, basicShadeBorderFillId, boldCharShapeId);
        tableHandler.setParagraphForCell(2, rowIndex, TITLE_TYPE, basicShadeBorderFillId, boldCharShapeId);
        tableHandler.setParagraphForCell(3, rowIndex, TITLE_DESCRIPTION, middleRightShadeBorderFillId,
            boldCharShapeId);
        rowIndex++;

        for (Parameter parameter : parameters) {
          tableHandler.setParagraphForCell(0, rowIndex, parameter.getName());
          tableHandler.setParagraphForCell(1, rowIndex, parameter.getType().getSimple());
          tableHandler.setParagraphForCell(2, rowIndex, parameter.getComment(), middleRightBorderFillId);
          rowIndex++;
        }
      }

      index++;
    }
  }

  private void finish() {
    int rowIndex = tableHandler.getRowCount() - 1;
    List<Cell> cells = tableHandler.getCellsOfRow(rowIndex);
    int cellCount = cells.size();

    if (cellCount == 1) {
      tableHandler.setListHeaderBorderFillForCell(0, rowIndex, bottomUpBoldShadeBorderFillId);
      return;
    }

    int colIndex;
    int borderFillId;
    int firstRowIndex = rowIndex;
    int firstColIndex = cells.get(0).getListHeader().getColIndex();
    if (firstColIndex != 0) {
      firstRowIndex--;
      for (; firstRowIndex > 0; firstRowIndex--) {
        Cell firstCell = tableHandler.getCellsOfRow(firstRowIndex).get(0);
        if (firstCell.getListHeader().getColIndex() == 0) {
          break;
        }
      }
      colIndex = 0;
    } else
      colIndex = 1;
    borderFillId = isCellShade(0, firstRowIndex) ? bottomLeftShadeBorderFillId : bottomLeftBorderFillId;
    tableHandler.setListHeaderBorderFillForCell(0, firstRowIndex, borderFillId);
    for (; colIndex < cellCount - 1; colIndex++) {
      borderFillId = isCellShade(colIndex, rowIndex) ? bottomMiddleShadeBorderFillId : bottomMiddleBorderFillId;
      tableHandler.setListHeaderBorderFillForCell(colIndex, rowIndex, borderFillId);
    }
    borderFillId = isCellShade(colIndex, rowIndex) ? bottomRightShadeBorderFillId : bottomRightBorderFillId;
    tableHandler.setListHeaderBorderFillForCell(colIndex, rowIndex, borderFillId);
  }

  private void describe(DocDescription desc, Section section) {
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

    tableHandler = new TableHandler(section, true);
    tableHandler.setCtrlHeaderRecord(0);
    tableHandler.setTableRecordForCells(basicBorderFillId, BASE_COLUMN_COUNT, totalRowCount);

    int rowIndex;

    rowIndex = tableHandler.addRow(BASE_COLUMN_COUNT, basicBorderFillId);
    tableHandler.mergeCell(rowIndex, 8, 1, 4);
    tableHandler.mergeCell(rowIndex, 6, 1, 2);
    tableHandler.mergeCell(rowIndex, 2, 1, 4);
    tableHandler.mergeCell(rowIndex, 0, 1, 2);
    rowIndex = tableHandler.addRow(BASE_COLUMN_COUNT, basicBorderFillId);
    tableHandler.mergeCell(rowIndex, 9, 1, 3);
    tableHandler.mergeCell(rowIndex, 5, 1, 3);
    tableHandler.mergeCell(rowIndex, 1, 1, 3);
    describeClass(desc, tableHandler.getRowCount() - CLASS_ROW_COUNT);

    for (int i = 0; i < propertiesRowCount; i++) {
      rowIndex = tableHandler.addRow(BASE_COLUMN_COUNT, basicBorderFillId);
      if (i == 0) {
        tableHandler.mergeCell(rowIndex, 0, 1, 12);
      } else {
        tableHandler.mergeCell(rowIndex, 8, 1, 4);
        tableHandler.mergeCell(rowIndex, 6, 1, 2);
        tableHandler.mergeCell(rowIndex, 3, 1, 3);
        tableHandler.mergeCell(rowIndex, 0, 1, 3);
      }
    }
    describeFields(desc.getFields(), tableHandler.getRowCount() - propertiesRowCount);

    for (int i = 0; i < methodsRowCount; i++) {
      rowIndex = tableHandler.addRow(BASE_COLUMN_COUNT, basicBorderFillId);
      if (i == 0) {
        tableHandler.mergeCell(rowIndex, 0, 1, 12);
      } else if (i % 2 == 1) {
        tableHandler.mergeCell(rowIndex, 7, 1, 5);
        tableHandler.mergeCell(rowIndex, 1, 1, 5);
      } else if (i % 2 == 0) {
        tableHandler.mergeCell(rowIndex, 8, 1, 4);
        tableHandler.mergeCell(rowIndex, 4, 1, 3);
        tableHandler.mergeCell(rowIndex, 1, 1, 2);
        int methodIndex = (i / METHOD_ROW_COUNT_PER_METHOD) - METHOD_ROW_COUNT_OF_TITLE;
        List<Parameter> parameters = methods.get(methodIndex).getParameters();
        if (!parameters.isEmpty()) {
          int parametersRowCount = PARAMETER_ROW_COUNT_OF_TITLE + parameters.size();
          for (int j = 0; j < parametersRowCount; j++) {
            rowIndex = tableHandler.addRow(BASE_COLUMN_COUNT, basicBorderFillId);
            tableHandler.mergeCell(rowIndex, 5, 1, 7);
            tableHandler.mergeCell(rowIndex, 3, 1, 2);
            tableHandler.mergeCell(rowIndex, 1, 1, 2);
          }
          tableHandler.mergeCell(rowIndex - parametersRowCount + 1, 0, parametersRowCount, 1);
        }
      }
    }
    describeMethods(methods, tableHandler.getRowCount() - methodsRowCount - parametersRowTotalCount);
    finish();
  }

  @Override
  public void write(List<DocDescription> descriptions, File file) throws Exception {

    HWPFile hwpFile = BlankFileMaker.make();
    if (hwpFile == null) {
      throw new FileNotFoundException();
    }

    prepareStockObjects(hwpFile);
    Section section = hwpFile.getBodyText().getSectionList().get(0);
    section.deleteParagraph(0);

    descriptions.forEach(description -> describe(description, section));

    HWPWriter.toFile(hwpFile, file.getAbsolutePath());
  }

}
