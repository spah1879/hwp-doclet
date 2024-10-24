package io.github.spah1879.doclet.assorted;

import java.util.Collections;
import java.util.List;

import jdk.javadoc.doclet.Doclet.Option;

public abstract class DocletOption implements Option {

  private final String description;
  private final List<String> names;
  private final String parameters;

  protected DocletOption(List<String> names, String description, String parameters) {
    this.names = names;
    this.description = description;
    this.parameters = parameters;
  }

  @Override
  public int getArgumentCount() {
    return 1;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Kind getKind() {
    return Kind.STANDARD;
  }

  @Override
  public List<String> getNames() {
    return names;
  }

  @Override
  public String getParameters() {
    return parameters;
  }

  public static class FakeOption extends DocletOption {

    public FakeOption(String name, String description) {
      super(Collections.singletonList(name), description, "none");
    }

    @Override
    public boolean process(String option, List<String> arguments) {
      return true;
    }
  }
}
