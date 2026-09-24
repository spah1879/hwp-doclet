package io.github.spah1879.doclet.assorted;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Setter
@Getter
@Builder
public final class DocDescription {

  @Setter
  @Getter
  @Builder
  @AllArgsConstructor
  public static class Type {
    String full;
    String simple;
  }

  @Setter
  @Getter
  @Builder
  public static class Modifier {
    @Singular
    List<String> elements;
    String combined;
  }

  @Setter
  @Getter
  @Builder
  public static class TagTuple {
    String name;
    String description;
  }

  @Setter
  @Getter
  @Builder
  public static class TagValue {
    List<TagTuple> items;
    String combined;
  }

  @Setter
  @Getter
  @Builder
  public static class Comment {
    String firstSentence;
    String body;
    String fullBody;
  }

  @Setter
  @Getter
  @Builder
  @AllArgsConstructor
  public static class Parameter {
    String name;
    Type type;
    String comment;
  }

  @Setter
  @Getter
  @Builder
  public static class Field {
    String name;
    Modifier modifier;
    Type type;
    Map<String, TagValue> tags;
    Comment comment;
    @Singular
    Map<String, Map<String, Object>> annotations;
  }

  @Setter
  @Getter
  @Builder
  public static class Constructor {
    String name;
    Modifier modifier;
    @Singular
    List<Parameter> parameters;
    String parameterSpec;
    String flatSignature;
    Map<String, TagValue> tags;
    Comment comment;
    @Singular
    Map<String, Map<String, Object>> annotations;
  }

  @Setter
  @Getter
  @Builder
  public static class Method {
    String name;
    Modifier modifier;
    Type returnType;
    @Singular
    List<Parameter> parameters;
    String parameterSpec;
    String flatSignature;
    Map<String, TagValue> tags;
    Comment comment;
    @Singular
    Map<String, Map<String, Object>> annotations;
  }

  String packageName;
  String name;
  Modifier modifier;
  String type;
  Map<String, TagValue> tags;
  Comment comment;
  @Singular
  Map<String, Map<String, Object>> annotations;
  @Singular
  List<Field> fields;
  @Singular
  List<Constructor> constructors;
  @Singular
  List<Method> methods;
}
