package io.github.spah1879.doclet.assorted;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;

import io.github.spah1879.doclet.assorted.DocDescription.Constructor;
import io.github.spah1879.doclet.assorted.DocDescription.Field;
import io.github.spah1879.doclet.assorted.DocDescription.Method;
import io.github.spah1879.doclet.assorted.DocDescription.Parameter;
import io.github.spah1879.doclet.assorted.DocDescription.Type;
import io.github.spah1879.doclet.parser.AnnotationParser;
import io.github.spah1879.doclet.parser.BlockTagParser;
import io.github.spah1879.doclet.parser.CommentParser;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

public class Aggregator {
  private final DocletEnvironment environment;
  private final DocTrees docTrees;
  private final Reporter reporter;
  private final DocDescription.DocDescriptionBuilder builder;

  public Aggregator(Reporter reporter, DocletEnvironment environment) {
    this.environment = environment;
    this.docTrees = environment.getDocTrees();
    this.reporter = reporter;
    this.builder = DocDescription.builder();
  }

  private String getSimpleName(String qualifiedName) {
    return qualifiedName.replaceAll("[^\\. ,<]+\\.", "");
  }

  private Type buildType(TypeMirror type) {
    String full = String.valueOf(type);
    return new Type(full, getSimpleName(full));
  }

  private String getFlatSignature(List<? extends VariableElement> parameters) {
    return parameters.stream()
        .map(p -> getSimpleName(String.valueOf(p.asType())))
        .collect(Collectors.joining(", ", "(", ")"));
  }

  private Parameter buildParameter(VariableElement parameter, Map<String, String> tags) {
    String paramName = parameter.getSimpleName().toString();
    String comment = tags.entrySet().stream()
        .filter(t -> t.getKey().equals("param") && t.getValue().startsWith(paramName + " "))
        .findFirst()
        .map(t -> t.getValue().substring(paramName.length() + 1))
        .orElse("");

    return new Parameter(paramName, buildType(parameter.asType()), comment);
  }

  public void aggregateTypeElement(TypeElement element) {
    builder.packageName(String.valueOf(environment.getElementUtils().getPackageOf(element)))
        .name(String.valueOf(element.getSimpleName()))
        .type(element.getKind().name().toLowerCase());
    element.getModifiers().forEach(n -> builder.modifier(String.valueOf(n)));

    DocCommentTree commentTree = docTrees.getDocCommentTree(element);
    CommentParser parser = CommentParser.parse(commentTree, reporter);
    builder.comment(parser.getComment());
    builder.tags(BlockTagParser.parse(parser.getBlockTags(), reporter).getBlockTags());
    builder.annotations(AnnotationParser.parse(element.getAnnotationMirrors(), reporter));
  }

  public void aggregateFiledElements(List<VariableElement> elements) {
    elements.forEach(e -> {
      Field.FieldBuilder fb = Field.builder();
      fb.name(String.valueOf(e.getSimpleName())).type(buildType(e.asType()));
      e.getModifiers().forEach(n -> fb.modifier(String.valueOf(n)));

      DocCommentTree commentTree = docTrees.getDocCommentTree(e);
      CommentParser parser = CommentParser.parse(commentTree, reporter);
      fb.comment(parser.getComment());
      fb.tags(BlockTagParser.parse(parser.getBlockTags(), reporter).getBlockTags());
      fb.annotations(AnnotationParser.parse(e.getAnnotationMirrors(), reporter));
      builder.field(fb.build());
    });
  }

  public void aggregateConstructorElements(List<ExecutableElement> elements) {
    elements.forEach(e -> {
      Constructor.ConstructorBuilder cb = Constructor.builder();
      cb.name(String.valueOf(e.getEnclosingElement().getSimpleName()));
      e.getModifiers().forEach(n -> cb.modifier(String.valueOf(n)));
      cb.flatSignature(getFlatSignature(e.getParameters()));

      DocCommentTree commentTree = docTrees.getDocCommentTree(e);
      CommentParser parser = CommentParser.parse(commentTree, reporter);
      cb.comment(parser.getComment());
      Map<String, String> tags = BlockTagParser.parse(parser.getBlockTags(), reporter).getBlockTags();
      cb.tags(tags);
      e.getParameters().forEach(p -> cb.parameter(buildParameter(p, tags)));
      cb.annotations(AnnotationParser.parse(e.getAnnotationMirrors(), reporter));
      builder.constructor(cb.build());
    });
  }

  public void aggregateMethodElements(List<ExecutableElement> elements) {
    elements.forEach(e -> {
      Method.MethodBuilder mb = Method.builder();
      mb.name(String.valueOf(e.getSimpleName()));
      e.getModifiers().forEach(n -> mb.modifier(String.valueOf(n)));
      mb.returnType(buildType(e.getReturnType()));
      mb.flatSignature(getFlatSignature(e.getParameters()));

      DocCommentTree commentTree = docTrees.getDocCommentTree(e);
      CommentParser parser = CommentParser.parse(commentTree, reporter);
      mb.comment(parser.getComment());
      Map<String, String> tags = BlockTagParser.parse(parser.getBlockTags(), reporter).getBlockTags();
      mb.tags(tags);
      e.getParameters().forEach(p -> mb.parameter(buildParameter(p, tags)));
      mb.annotations(AnnotationParser.parse(e.getAnnotationMirrors(), reporter));
      builder.method(mb.build());
    });
  }

  public DocDescription getDocDescription() {
    return builder.build();
  }
}
