package io.github.spah1879.doclet.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.sun.source.doctree.DocTree;

import jdk.javadoc.doclet.Reporter;

public class AnnotationParser implements AnnotationValueVisitor<Object, Void> {

  private final Map<String, Map<String, Object>> annotations;

  protected final Optional<Reporter> reporter;

  protected AnnotationParser(Reporter reporter) {
    annotations = new HashMap<>();
    this.reporter = Optional.ofNullable(reporter);
  }

  protected void printNote(String message) {
    reporter.ifPresentOrElse(r -> r.print(Kind.NOTE, message), () -> System.out.println(message));
  }

  protected void reportKindAndString(String prefix, DocTree node) {
    printNote(prefix + "Kind: " + node.getKind() + " / " + node.toString());
  }

  protected void reportKindAndString(DocTree node) {
    reportKindAndString("", node);
  }

  protected void reportUnhandledNode(DocTree node) {
    reportKindAndString("Unhandled ", node);
  }

  @Override
  public Object visit(AnnotationValue av, Void p) {
    return av.accept(this, null);
  }

  @Override
  public Boolean visitBoolean(boolean b, Void p) {
    return b;
  }

  @Override
  public Byte visitByte(byte b, Void p) {
    return b;
  }

  @Override
  public Character visitChar(char c, Void p) {
    return c;
  }

  @Override
  public Double visitDouble(double d, Void p) {
    return d;
  }

  @Override
  public Float visitFloat(float f, Void p) {
    return f;
  }

  @Override
  public Integer visitInt(int i, Void p) {
    return i;
  }

  @Override
  public Long visitLong(long i, Void p) {
    return i;
  }

  @Override
  public Short visitShort(short s, Void p) {
    return s;
  }

  @Override
  public String visitString(String s, Void p) {
    return s;
  }

  @Override
  public String visitType(TypeMirror t, Void p) {
    return t.toString();
  }

  @Override
  public String visitEnumConstant(VariableElement c, Void p) {
    return c.asType().toString() + "." + c.getSimpleName().toString();
  }

  @Override
  public Object visitAnnotation(AnnotationMirror a, Void p) {
    String annotationName = a.getAnnotationType().toString();
    Map<String, Object> values = new HashMap<>();
    a.getElementValues().entrySet().forEach(e -> {
      Object value = visit(e.getValue(), null);
      values.put(e.getKey().getSimpleName().toString(), value);
    });
    annotations.put(annotationName, values);

    return null;
  }

  @Override
  public Object visitArray(List<? extends AnnotationValue> vals, Void p) {
    if (vals.isEmpty())
      return null;

    List<Object> objects = new ArrayList<>();
    vals.forEach(val -> {
      objects.add(val.accept(this, null));
    });

    return objects;
  }

  @Override
  public String visitUnknown(AnnotationValue av, Void p) {
    return av.toString();
  }

  public static Map<String, Map<String, Object>> parse(List<? extends AnnotationMirror> annotationMirrors,
      Reporter reporter) {
    AnnotationParser parser = new AnnotationParser(reporter);
    annotationMirrors.forEach(am -> parser.visitAnnotation(am, null));
    return parser.annotations;
  }

}
