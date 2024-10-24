package io.github.spah1879.doclet.parser;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import jdk.javadoc.doclet.Reporter;

public class ElementParser implements ElementVisitor<Void, Void> {

  private final Reporter reporter;

  private final List<ExecutableElement> constructors = new ArrayList<>();
  private final List<ExecutableElement> methods = new ArrayList<>();
  private final List<VariableElement> variables = new ArrayList<>();

  private ElementParser(Reporter reporter) {
    this.reporter = reporter;
  }

  private void printNote(String message) {
    reporter.print(Diagnostic.Kind.NOTE, message);
  }

  private void reportKindAndString(String prefix, Element e) {
    printNote(prefix + "Kind: " + e.getKind() + " / " + e.toString());
  }

  @Override
  public Void visit(Element e, Void p) {
    return (e == null) ? null : e.accept(this, p);
  }

  @Override
  public Void visitVariable(VariableElement e, Void p) {
    variables.add(e);
    return null;
  }

  @Override
  public Void visitExecutable(ExecutableElement e, Void p) {

    switch (e.getKind()) {
      case CONSTRUCTOR:
        constructors.add(e);
        break;
      case METHOD:
        methods.add(e);
        break;
      default:
        reportKindAndString("Unhandle ExecutableElement ", e);
        break;
    }

    return null;
  }

  @Override
  public Void visitPackage(PackageElement e, Void p) {
    reportKindAndString("Unimplemented method 'visitPackage' ", e);
    return null;
  }

  @Override
  public Void visitType(TypeElement e, Void p) {
    reportKindAndString("Unimplemented method 'visitType' ", e);
    return null;
  }

  @Override
  public Void visitTypeParameter(TypeParameterElement e, Void p) {
    reportKindAndString("Unimplemented method 'visitTypeParameter' ", e);
    return null;
  }

  @Override
  public Void visitUnknown(Element e, Void p) {
    reportKindAndString("Unimplemented method 'visitUnknown' ", e);
    return null;
  }

  public List<ExecutableElement> getConstructors() {
    return constructors;
  }

  public List<ExecutableElement> getmethods() {
    return methods;
  }

  public List<VariableElement> getvariables() {
    return variables;
  }

  public static ElementParser parse(List<? extends Element> elements, Reporter reporter) {
    ElementParser parser = new ElementParser(reporter);
    elements.forEach(e -> parser.visit(e, null));
    return parser;
  }

}
