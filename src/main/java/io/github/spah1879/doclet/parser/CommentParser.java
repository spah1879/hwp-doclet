package io.github.spah1879.doclet.parser;

import java.util.List;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;

import io.github.spah1879.doclet.assorted.DocDescription.Comment;
import jdk.javadoc.doclet.Reporter;

public class CommentParser extends DocParser {

  private Comment.CommentBuilder cb;
  private List<? extends DocTree> blockTags;

  private CommentParser(Reporter reporter) {
    super(reporter);
    cb = Comment.builder();
  }

  public Comment getComment() {
    Comment comment = cb.build();
    if (comment.getFirstSentence() == null) {
      comment.setFirstSentence("");
    }
    if (comment.getBody() == null) {
      comment.setBody("");
    }
    if (comment.getFullBody() == null) {
      comment.setFullBody("");
    }
    return comment;
  }

  public List<? extends DocTree> getBlockTags() {
    return blockTags;
  }

  @Override
  public Void visitDocComment(DocCommentTree node, Void p) {
    cb.firstSentence(BodyParser.parse(node.getFirstSentence(), reporter.get()).getBody());
    cb.body(BodyParser.parse(node.getBody(), reporter.get()).getBody());
    cb.fullBody(BodyParser.parse(node.getFullBody(), reporter.get()).getBody());
    blockTags = node.getBlockTags();
    return DEFAULT_VALUE;
  }

  public static CommentParser parse(DocCommentTree tree, Reporter reporter) {
    CommentParser parser = new CommentParser(reporter);
    parser.visit(tree, null);
    return parser;
  }

}