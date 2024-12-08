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
    @Singular
    List<String> modifiers;
    Type type;
    Map<String, String> tags;
    Comment comment;
    @Singular
    Map<String, Map<String, Object>> annotations;
  }

  @Setter
  @Getter
  @Builder
  public static class Constructor {
    String name;
    @Singular
    List<String> modifiers;
    @Singular
    List<Parameter> parameters;
    String flatSignature;
    Map<String, String> tags;
    Comment comment;
    @Singular
    Map<String, Map<String, Object>> annotations;
  }

  @Setter
  @Getter
  @Builder
  public static class Method {
    String name;
    @Singular
    List<String> modifiers;
    Type returnType;
    @Singular
    List<Parameter> parameters;
    String flatSignature;
    Map<String, String> tags;
    Comment comment;
    @Singular
    Map<String, Map<String, Object>> annotations;
  }

  String packageName;
  String name;
  @Singular
  List<String> modifiers;
  String type;
  Map<String, String> tags;
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
