package io.github.spah1879.doclet.writer;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.spah1879.doclet.assorted.DocDescription;

public final class JsonWriter extends DocWriter {

  public static JsonWriter newInstance() {
    return new JsonWriter();
  }

  @Override
  public void write(List<DocDescription> descriptions, File file, List<String> outputForamts) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(file, descriptions);
  }

}
