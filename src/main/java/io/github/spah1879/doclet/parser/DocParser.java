package io.github.spah1879.doclet.parser;

import java.util.Optional;

import javax.tools.Diagnostic.Kind;

import com.sun.source.doctree.DocTree;
import com.sun.source.util.SimpleDocTreeVisitor;

import jdk.javadoc.doclet.Reporter;

public abstract class DocParser extends SimpleDocTreeVisitor<Void, Void> {

  protected final Optional<Reporter> reporter;

  protected DocParser(Reporter reporter) {
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

}
