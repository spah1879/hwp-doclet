package io.github.spah1879.doclet.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.source.doctree.AuthorTree;
import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.HiddenTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ProvidesTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.SerialDataTree;
import com.sun.source.doctree.SerialFieldTree;
import com.sun.source.doctree.SerialTree;
import com.sun.source.doctree.SinceTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.UsesTree;
import com.sun.source.doctree.VersionTree;

import jdk.javadoc.doclet.Reporter;

public class BlockTagParser extends DocParser {

  private final Map<String, String> blockTags;

  private BlockTagParser(Reporter reporter) {
    super(reporter);
    this.blockTags = new HashMap<>();
  }

  private void addToBlockTags(String tagName, String name, List<? extends DocTree> contents) {
    blockTags.put(tagName, BodyParser.parse(name, contents, reporter.get()).getBody());
  }

  private void addToBlockTags(String tagName, List<? extends DocTree> contents) {
    addToBlockTags(tagName, "", contents);
  }

  @Override
  public Void visitAuthor(AuthorTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getName());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitDeprecated(DeprecatedTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getBody());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitHidden(HiddenTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getBody());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitParam(ParamTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getName().getName().toString(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitProvides(ProvidesTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getServiceType().getSignature(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitReturn(ReturnTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitSee(SeeTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getReference());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitSerial(SerialTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitSerialData(SerialDataTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitSerialField(SerialFieldTree node, Void p) {
    String name = node.getName().getName().toString() + " " + node.getType().getSignature();
    addToBlockTags(node.getTagName(), name, node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitSince(SinceTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getBody());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitThrows(ThrowsTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getExceptionName().getSignature(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitUnknownBlockTag(UnknownBlockTagTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getContent());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitUses(UsesTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getServiceType().getSignature(), node.getDescription());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitVersion(VersionTree node, Void p) {
    addToBlockTags(node.getTagName(), node.getBody());
    return DEFAULT_VALUE;
  }

  @Override
  public Void visitOther(DocTree node, Void p) {
    List<DocTree> contents = new ArrayList<>();
    contents.add(node);
    addToBlockTags("other", contents);
    return DEFAULT_VALUE;
  }

  public Map<String, String> getBlockTags() {
    return blockTags;
  }

  public static BlockTagParser parse(DocTree tag, Reporter reporter) {
    BlockTagParser parser = new BlockTagParser(reporter);
    parser.visit(tag, null);
    return parser;
  }

  public static BlockTagParser parse(List<? extends DocTree> tags, Reporter reporter) {
    BlockTagParser parser = new BlockTagParser(reporter);
    parser.visit(tags, null);
    return parser;
  }

}