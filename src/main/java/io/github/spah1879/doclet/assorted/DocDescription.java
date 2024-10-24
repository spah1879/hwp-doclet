package io.github.spah1879.doclet.assorted;

import java.util.List;

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
  @AllArgsConstructor
  public static class Tag {
    String name;
    String value;
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
    @Singular
    List<Tag> tags;
    Comment comment;
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
    @Singular
    List<Tag> tags;
    Comment comment;
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
    @Singular
    List<Tag> tags;
    Comment comment;
  }

  String packageName;
  String name;
  @Singular
  List<String> modifiers;
  String type;
  @Singular
  List<Tag> tags;
  Comment comment;
  @Singular
  List<Field> fields;
  @Singular
  List<Constructor> constructors;
  @Singular
  List<Method> methods;
}
