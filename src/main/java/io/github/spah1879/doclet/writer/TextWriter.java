package io.github.spah1879.doclet.writer;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.assorted.DocDescription.Constructor;
import io.github.spah1879.doclet.assorted.DocDescription.Field;
import io.github.spah1879.doclet.assorted.DocDescription.Method;
import io.github.spah1879.doclet.assorted.DocDescription.TagValue;
import jdk.javadoc.doclet.Reporter;

public class TextWriter extends DocWriter {
  private final StringBuilder sb;

  public TextWriter(Reporter reporter) {
    super(reporter);
    this.sb = new StringBuilder();
  }

  public TextWriter() {
    this(null);
  }

  public static TextWriter newInstance() {
    return new TextWriter();
  }

  public TextWriter add(String text) {
    sb.append(text);
    return this;
  }

  public TextWriter add(Object text) {
    sb.append(String.valueOf(text));
    return this;
  }

  public void describeTypeElement(DocDescription typeDesc) {
    add("\n0. Element Information\n");
    add("\tPackage: ").add(typeDesc.getPackageName()).add("\n");
    add("\tElement Name: ").add(typeDesc.getName()).add("\n");
    add("\tModifier(").add(typeDesc.getModifier().getElements().size()).add("): ");
    typeDesc.getModifier().getElements().forEach(modifier -> add(modifier).add(" "));
    add("\n");
    add("\tType: ").add(typeDesc.getType()).add("\n");

    add("\tTags:\n");
    typeDesc.getTags().entrySet().forEach(t -> {
      add("\t\t[@").add(t.getKey()).add("]\n");
      TagValue value = t.getValue();
      add("\t\t\t- Items:\n");
      value.getItems().forEach(tagTuple -> {
        add("\t\t\t\t- Name: ").add(tagTuple.getName()).add("\n");
        add("\t\t\t\t  Description: ").add(tagTuple.getDescription()).add("\n");
      });
      add("\t\t\t- Combined: ").add(value.getCombined()).add("\n");
    });

    add("\tComment:\n")
        .add("\t\tFirst Sentence: ").add(typeDesc.getComment().getFirstSentence()).add("\n")
        .add("\t\tBody: ").add(typeDesc.getComment().getBody()).add("\n")
        .add("\t\tFull Body: ").add(typeDesc.getComment().getFullBody()).add("\n");
  }

  public void describeFiledElements(List<Field> fields) {
    int index = 1;
    add("\n1. Field Details\n");
    for (Field field : fields) {
      add("\t").add(index).add(". Field Name: ").add(field.getName()).add("\n");
      add("\t   Modifier(").add(field.getModifier().getElements().size()).add("): ");
      add(field.getModifier().getCombined());
      add("\n");
      add("\t   Type: ").add(field.getType().getFull()).add("\n");

      add("\tTags:\n");
      field.getTags().entrySet().forEach(t -> {
        add("\t\t[@").add(t.getKey()).add("]\n");
        TagValue value = t.getValue();
        add("\t\t\t- Items:\n");
        value.getItems().forEach(tagTuple -> {
          add("\t\t\t\t- Name: ").add(tagTuple.getName()).add("\n");
          add("\t\t\t\t  Description: ").add(tagTuple.getDescription()).add("\n");
        });
        add("\t\t\t- Combined: ").add(value.getCombined()).add("\n");
      });

      add("\tComment:\n")
          .add("\t\tFirst Sentence: ").add(field.getComment().getFirstSentence()).add("\n")
          .add("\t\tBody: ").add(field.getComment().getBody()).add("\n")
          .add("\t\tFull Body: ").add(field.getComment().getFullBody()).add("\n");
      add("\n");
      index++;
    }
  }

  public void describeConstructorElements(List<Constructor> constructors) {
    int index = 1;
    add("\n2. Constructor Details\n");
    for (Constructor constructor : constructors) {
      add("\t").add(index).add(". Constructor Name: ")
          .add(constructor.getName()).add("\n");
      add("\t Modifier(").add(constructor.getModifier().getElements().size()).add("): ");
      constructor.getModifier().getElements().forEach(modifier -> add(modifier).add(" "));
      add("\n");
      add("\t   Parameters: \n");
      constructor.getParameters().forEach(parameter -> add("\t\t   - ").add(parameter.getName())
          .add(" ").add(parameter.getType().getFull()).add(" ")
          .add(parameter.getComment()).add("\n"));
      add("\t   Flat Signature: ").add(constructor.getFlatSignature()).add("\n");

      add("\tTags:\n");
      constructor.getTags().entrySet().forEach(t -> {
        add("\t\t[@").add(t.getKey()).add("]\n");
        TagValue value = t.getValue();
        add("\t\t\t- Items:\n");
        value.getItems().forEach(tagTuple -> {
          add("\t\t\t\t- Name: ").add(tagTuple.getName()).add("\n");
          add("\t\t\t\t  Description: ").add(tagTuple.getDescription()).add("\n");
        });
        add("\t\t\t- Combined: ").add(value.getCombined()).add("\n");
      });

      add("\tComment:\n")
          .add("\t\tFirst Sentence: ").add(constructor.getComment().getFirstSentence()).add("\n")
          .add("\t\tBody: ").add(constructor.getComment().getBody()).add("\n")
          .add("\t\tFull Body: ").add(constructor.getComment().getFullBody()).add("\n");
      add("\n");
      index++;
    }
  }

  public void describeMethodElements(List<Method> methods) {
    int index = 1;
    add("\n3. Method Details\n");
    for (Method method : methods) {
      add("\t").add(index).add(". Method Name: ").add(method.getName()).add("\n");
      add("\t   Modifier(").add(method.getModifier().getElements().size()).add("): ");
      add(method.getModifier().getCombined());
      add("\n");
      add("\t   Return Type: ").add(method.getReturnType().getFull()).add("\n");
      add("\t   Parameters: \n");
      method.getParameters().forEach(parameter -> add("\t\t   - ").add(parameter.getName())
          .add(" ").add(parameter.getType().getFull()).add(" ")
          .add(parameter.getComment()).add("\n"));
      add("\t   Flat Signature: ").add(method.getFlatSignature()).add("\n");

      add("\tTags:\n");
      method.getTags().entrySet().forEach(t -> {
        add("\t\t[@").add(t.getKey()).add("]\n");
        TagValue value = t.getValue();
        add("\t\t\t- Items:\n");
        value.getItems().forEach(tagTuple -> {
          add("\t\t\t\t- Name: ").add(tagTuple.getName()).add("\n");
          add("\t\t\t\t  Description: ").add(tagTuple.getDescription()).add("\n");
        });
        add("\t\t\t- Combined: ").add(value.getCombined()).add("\n");
      });

      add("\tComment:\n")
          .add("\t\tFirst Sentence: ").add(method.getComment().getFirstSentence()).add("\n")
          .add("\t\tBody: ").add(method.getComment().getBody()).add("\n")
          .add("\t\tFull Body: ").add(method.getComment().getFullBody()).add("\n");
      add("\n");
      add("\n");
      index++;
    }
  }

  @Override
  public void write(List<DocDescription> descriptions, File file, List<String> outputForamts)
      throws Exception {
    add("==============================================\n");
    add("Text Doclet Begin\n");

    descriptions.forEach(description -> {
      describeTypeElement(description);
      describeFiledElements(description.getFields());
      describeConstructorElements(description.getConstructors());
      describeMethodElements(description.getMethods());
      add("-----------------------------------------\n");
    });

    try (PrintWriter writer = new PrintWriter(file)) {
      writer.print(sb.toString());
      writer.flush();
    }
  }
}
