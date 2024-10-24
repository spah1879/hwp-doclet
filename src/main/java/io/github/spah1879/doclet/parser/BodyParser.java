package io.github.spah1879.doclet.parser;

import java.util.List;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;

import jdk.javadoc.doclet.Reporter;

public class BodyParser extends DocParser {

  private final StringBuilder sb;

  private BodyParser(String prefix, Reporter reporter) {
    super(reporter);
    sb = new StringBuilder();
    sb.append(prefix == null ? "" : prefix);
  }

  @Override
  public Void visitText(TextTree node, Void p) {
    if (sb.length() > 0)
      sb.append(" ");
    sb.append(node.getBody());

    return DEFAULT_VALUE;
  }

  @Override
  public Void visitStartElement(StartElementTree node, Void p) {
    sb.append("<").append(node.getName());
    node.getAttributes().forEach(attr -> {
      sb.append(" " + attr.toString());
    });
    if (node.isSelfClosing())
      sb.append("/");
    sb.append(">");

    return DEFAULT_VALUE;
  }

  @Override
  public Void visitEndElement(EndElementTree node, Void p) {
    sb.append("</").append(node.getName()).append(">");

    return DEFAULT_VALUE;
  }

  @Override
  public Void visitEntity(EntityTree node, Void p) {
    reportUnhandledNode(node);
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitOther(DocTree node, Void p) {
    reportUnhandledNode(node);
    return DEFAULT_VALUE;
  }

  public String getBody() {
    return sb.toString();
  }

  public static BodyParser parse(String prefix, List<? extends DocTree> docTree, Reporter reporter) {
    BodyParser parser = new BodyParser(prefix, reporter);
    parser.visit(docTree, null);
    return parser;
  }

  public static BodyParser parse(List<? extends DocTree> docTree, Reporter reporter) {
    return parse("", docTree, reporter);
  }

}
