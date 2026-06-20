package io.github.spah1879.doclet.writer.hwp;

import java.io.UnsupportedEncodingException;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.charshape.ParaCharShape;
import kr.dogfoot.hwplib.object.bodytext.paragraph.header.ParaHeader;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.LineSegItem;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.ParaLineSeg;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.object.docinfo.BorderFill;
import kr.dogfoot.hwplib.object.docinfo.CharShape;
import kr.dogfoot.hwplib.object.docinfo.ParaShape;
import kr.dogfoot.hwplib.object.docinfo.borderfill.BackSlashDiagonalShape;
import kr.dogfoot.hwplib.object.docinfo.borderfill.BorderThickness;
import kr.dogfoot.hwplib.object.docinfo.borderfill.BorderType;
import kr.dogfoot.hwplib.object.docinfo.borderfill.SlashDiagonalShape;
import kr.dogfoot.hwplib.object.docinfo.borderfill.fillinfo.PatternFill;
import kr.dogfoot.hwplib.object.docinfo.borderfill.fillinfo.PatternType;
import kr.dogfoot.hwplib.object.etc.Color4Byte;

public final class Common {

  public static final int FIRST_CHAR_SHAPE_ID = 1;
  public static final int FIRST_PARA_SHAPE_ID = 1;

  private Common() {
  }

  public static int ptToLineHeight(double pt) {
    return (int) (pt * 100.0f);
  }

  public static long mmToHwp(double mm) {
    return (long) (mm * 72000.0f / 254.0f + 0.5f);
  }

  public static int getCharShapeIdFromFirst(HWPFile hwpFile, int ratio, boolean bold, int borderFillId) {
    CharShape cs = hwpFile.getDocInfo().getCharShapeList().get(FIRST_CHAR_SHAPE_ID).clone();
    for (int i = 0; i < 7; i++) {
      cs.getRatios().getArray()[i] = (short) ratio;
    }
    cs.getProperty().setBold(bold);
    if (borderFillId > 0)
      cs.setBorderFillId(borderFillId);
    hwpFile.getDocInfo().getCharShapeList().add(cs);
    return hwpFile.getDocInfo().getCharShapeList().size() - 1;
  }

  public static int getParaShapeIdFromFirst(HWPFile hwpFile, int borderFillId) {
    ParaShape ps = hwpFile.getDocInfo().getParaShapeList().get(FIRST_PARA_SHAPE_ID).clone();
    if (borderFillId > 0)
      ps.setBorderFillId(borderFillId);
    hwpFile.getDocInfo().getParaShapeList().add(ps);
    return hwpFile.getDocInfo().getParaShapeList().size() - 1;
  }

  public static int getBorderFillIdForChar(HWPFile hwpFile, boolean shade) {
    BorderFill bf = hwpFile.getDocInfo().addNewBorderFill();
    bf.getProperty().set3DEffect(false);
    bf.getProperty().setShadowEffect(false);
    bf.getProperty().setSlashDiagonalShape(SlashDiagonalShape.None);
    bf.getProperty().setBackSlashDiagonalShape(BackSlashDiagonalShape.None);
    bf.getLeftBorder().setType(BorderType.None);
    bf.getLeftBorder().setThickness(BorderThickness.MM0_1);
    bf.getLeftBorder().getColor().setValue(0x0);
    bf.getRightBorder().setType(BorderType.None);
    bf.getRightBorder().setThickness(BorderThickness.MM0_1);
    bf.getRightBorder().getColor().setValue(0x0);
    bf.getTopBorder().setType(BorderType.None);
    bf.getTopBorder().setThickness(BorderThickness.MM0_1);
    bf.getTopBorder().getColor().setValue(0x0);
    bf.getBottomBorder().setType(BorderType.None);
    bf.getBottomBorder().setThickness(BorderThickness.MM0_1);
    bf.getBottomBorder().getColor().setValue(0x0);
    bf.getDiagonalBorder().setType(BorderType.None);
    bf.getDiagonalBorder().setThickness(BorderThickness.MM0_1);
    bf.getDiagonalBorder().getColor().setValue(0x0);

    bf.getFillInfo().getType().setPatternFill(shade);
    if (shade) {
      bf.getFillInfo().createPatternFill();
      PatternFill pf = bf.getFillInfo().getPatternFill();
      pf.getBackColor().setValue(new Color4Byte(210, 210, 210).getValue());
      pf.setPatternType(PatternType.None);
      pf.getPatternColor().setValue(0);
    }
    return hwpFile.getDocInfo().getBorderFillList().size();
  }

  public static int getBorderFillIdForCell(HWPFile hwpFile, BorderThickness left, BorderThickness right,
      BorderThickness top, BorderThickness bottom, boolean shade) {
    BorderFill bf = hwpFile.getDocInfo().addNewBorderFill();
    bf.getProperty().set3DEffect(false);
    bf.getProperty().setShadowEffect(false);
    bf.getProperty().setSlashDiagonalShape(SlashDiagonalShape.None);
    bf.getProperty().setBackSlashDiagonalShape(BackSlashDiagonalShape.None);
    bf.getLeftBorder().setType(BorderType.Solid);
    bf.getLeftBorder().setThickness(left);
    bf.getLeftBorder().getColor().setValue(0x0);
    bf.getRightBorder().setType(BorderType.Solid);
    bf.getRightBorder().setThickness(right);
    bf.getRightBorder().getColor().setValue(0x0);
    bf.getTopBorder().setType(BorderType.Solid);
    bf.getTopBorder().setThickness(top);
    bf.getTopBorder().getColor().setValue(0x0);
    bf.getBottomBorder().setType(BorderType.Solid);
    bf.getBottomBorder().setThickness(bottom);
    bf.getBottomBorder().getColor().setValue(0x0);
    bf.getDiagonalBorder().setType(BorderType.None);
    bf.getDiagonalBorder().setThickness(BorderThickness.MM0_12);
    bf.getDiagonalBorder().getColor().setValue(0x0);

    bf.getFillInfo().getType().setPatternFill(shade);
    if (shade) {
      bf.getFillInfo().createPatternFill();
      PatternFill pf = bf.getFillInfo().getPatternFill();
      pf.getBackColor().setValue(new Color4Byte(210, 210, 210).getValue());
      pf.setPatternType(PatternType.None);
      pf.getPatternColor().setValue(0);
    }
    return hwpFile.getDocInfo().getBorderFillList().size();
  }

  public static void setParaHeader(Paragraph p) {
    setParaHeader(p, false);
  }

  public static void setParaHeader(Paragraph p, boolean dividePage) {
    ParaHeader ph = p.getHeader();
    ph.setLastInList(true);
    ph.setParaShapeId(1);
    ph.setStyleId((short) 1);
    ph.getDivideSort().setDivideSection(false);
    ph.getDivideSort().setDivideMultiColumn(false);
    ph.getDivideSort().setDividePage(dividePage);
    ph.getDivideSort().setDivideColumn(false);
    ph.setCharShapeCount(1);
    ph.setRangeTagCount(0);
    ph.setLineAlignCount(1);
    ph.setInstanceID(0);
    ph.setIsMergedByTrack(0);
    ph.setParaShapeId(FIRST_CHAR_SHAPE_ID);
  }

  public static void setParaCharShape(Paragraph p, int charShapeId) {
    p.createCharShape();

    ParaCharShape pcs = p.getCharShape();
    pcs.addParaCharShape(0, charShapeId);
  }

  public static void setParaLineSeg(Paragraph p) {
    p.createLineSeg();

    ParaLineSeg pls = p.getLineSeg();
    LineSegItem lsi = pls.addNewLineSegItem();

    lsi.setTextStartPosition(0);
    lsi.setLineVerticalPosition(0);
    lsi.setLineHeight(ptToLineHeight(10.0));
    lsi.setTextPartHeight(ptToLineHeight(10.0));
    lsi.setDistanceBaseLineToLineVerticalPosition(ptToLineHeight(10.0 * 0.85));
    lsi.setLineSpace(ptToLineHeight(3.0));
    lsi.setStartPositionFromColumn(0);
    lsi.setSegmentWidth((int) mmToHwp(50.0));
    lsi.getTag().setFirstSegmentAtLine(true);
    lsi.getTag().setLastSegmentAtLine(true);
  }

  public static void setParaText(Paragraph p, String text) {
    p.createText();
    ParaText pt = p.getText();
    try {
      pt.addString(text);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static Paragraph addNewParagraph(Section s, boolean dividePage) {
    Paragraph p = s.addNewParagraph();

    setParaHeader(p, dividePage);
    setParaCharShape(p, FIRST_CHAR_SHAPE_ID);
    setParaLineSeg(p);
    p.createText();
    return p;
  }

}