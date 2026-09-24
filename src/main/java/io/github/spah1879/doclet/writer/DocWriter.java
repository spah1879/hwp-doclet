package io.github.spah1879.doclet.writer;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.tools.Diagnostic.Kind;

import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.assorted.DocDescription.TagValue;
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

  protected String getCombinedTag(Map<String, TagValue> tags, String tagName) {
    return tags.containsKey(tagName) ? tags.get(tagName).getCombined() : "";
  }

  public abstract void write(List<DocDescription> descriptions, File file, List<String> outputForamts) throws Exception;

}
