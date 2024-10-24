package io.github.spah1879.doclet.writer;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.tools.Diagnostic.Kind;

import io.github.spah1879.doclet.assorted.DocDescription;
import jdk.javadoc.doclet.Reporter;

public abstract class DocWriter {
  protected final Optional<Reporter> reporter;

  protected DocWriter(Reporter reporter) {
    this.reporter = Optional.ofNullable(reporter);
  }

  protected DocWriter() {
    this(null);
  }

  protected void printNote(String message) {
    reporter.ifPresentOrElse(r -> r.print(Kind.NOTE, message), () -> System.out.println(message));
  }

  public abstract void write(List<DocDescription> descriptions, File file) throws Exception;

}
