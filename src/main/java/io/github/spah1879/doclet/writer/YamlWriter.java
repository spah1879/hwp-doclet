package io.github.spah1879.doclet.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.writer.yaml.DocRepresenter;

public final class YamlWriter extends DocWriter {

  public static YamlWriter newInstance() {
    return new YamlWriter();
  }

  @Override
  public void write(List<DocDescription> descriptions, File file) throws FileNotFoundException {

    PrintWriter writer = new PrintWriter(file);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
    options.setCanonical(false);
    options.setExplicitStart(false);

    DocRepresenter representer = new DocRepresenter(options);
    representer.addClassTag(DocDescription.class, Tag.MAP);

    Yaml yaml = new Yaml(representer, options);

    yaml.dump(descriptions, writer);
  }
}
