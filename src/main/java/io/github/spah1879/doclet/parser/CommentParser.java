package io.github.spah1879.doclet.parser;

import java.util.List;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;

import jdk.javadoc.doclet.Reporter;

public class CommentParser extends DocParser {

  private String commentFirstSentence = "";
  private String commentBody = "";
  private String commentFullBody = "";
  private List<? extends DocTree> blockTags;

  private CommentParser(Reporter reporter) {
    super(reporter);
  }

  public String getDocCommentFirstSentence() {
    return commentFirstSentence;
  }

  public String getDocCommentBody() {
    return commentBody;
  }

  public String getDocCommentFullBody() {
    return commentFullBody;
  }

  public List<? extends DocTree> getBlockTags() {
    return blockTags;
  }

  @Override
  public Void visitDocComment(DocCommentTree node, Void p) {
    commentFirstSentence = BodyParser.parse(node.getFirstSentence(), reporter.get()).getBody();
    commentBody = BodyParser.parse(node.getBody(), reporter.get()).getBody();
    commentFullBody = BodyParser.parse(node.getFullBody(), reporter.get()).getBody();

    blockTags = node.getBlockTags();
    return DEFAULT_VALUE;
  }

  public static CommentParser parse(DocCommentTree tree, Reporter reporter) {
    CommentParser parser = new CommentParser(reporter);
    parser.visit(tree, null);
    return parser;
  }

}