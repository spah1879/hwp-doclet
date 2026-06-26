package io.github.spah1879.doclet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import io.github.spah1879.doclet.assorted.Aggregator;
import io.github.spah1879.doclet.assorted.DocDescription;
import io.github.spah1879.doclet.assorted.DocletOption;
import io.github.spah1879.doclet.assorted.DocletOption.FakeOption;
import io.github.spah1879.doclet.parser.ElementParser;
import io.github.spah1879.doclet.writer.DocxWriter;
import io.github.spah1879.doclet.writer.HwpWriter;
import io.github.spah1879.doclet.writer.TextWriter;
import io.github.spah1879.doclet.writer.YamlWriter;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

public class HwpDoclet implements Doclet {

  private static String UNSUPPORTED_OPTION = "unsupported option";
  private String destinationDir;
  private String outputFilename;
  private List<String> outputForamts;
  private Reporter reporter;

  @Override
  public void init(Locale locale, Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    Option[] options = {
        new DocletOption(Arrays.asList("--output-directory", "-d"),
            "Destination directory for output files", "<path>") {
          @Override
          public boolean process(String option, List<String> arguments) {
            destinationDir = arguments.get(0);
            return true;
          }
        },
        new DocletOption(Arrays.asList("--output-filename", "-n"), "Output file name", "<file>") {
          @Override
          public boolean process(String option, List<String> arguments) {
            outputFilename = arguments.get(0);
            return true;
          }
        },
        new DocletOption(Arrays.asList("--output-formats", "-f"), "Specify Output format(s) [hwp,hwpx,docx,yaml,text]",
            "<format>") {
          @Override
          public boolean process(String option, List<String> arguments) {
            outputForamts = List.of(arguments.get(0).toLowerCase().split(","));
            return true;
          }
        },
        new FakeOption("-doctitle", UNSUPPORTED_OPTION), // to prevent gradle error
        new FakeOption("-windowtitle", UNSUPPORTED_OPTION), // to prevent gradle error
        new FakeOption("-notimestamp", UNSUPPORTED_OPTION), // to prevent gradle error
        new FakeOption("-Xdoclint:none", UNSUPPORTED_OPTION), // to prevent gradle error
        new FakeOption("-html5", UNSUPPORTED_OPTION) // to prevent gradle error
    };

    return new HashSet<>(Arrays.asList(options));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean run(DocletEnvironment environment) {

    List<DocDescription> descriptions = new ArrayList<>();

    for (Element element : environment.getSpecifiedElements()) {

      if (element instanceof TypeElement) {
        ElementParser parser = ElementParser.parse(element.getEnclosedElements(), reporter);
        Aggregator aggregator = new Aggregator(reporter, environment);
        aggregator.aggregateTypeElement((TypeElement) element);
        aggregator.aggregateFiledElements(parser.getvariables());
        aggregator.aggregateConstructorElements(parser.getConstructors());
        aggregator.aggregateMethodElements(parser.getmethods());
        descriptions.add(aggregator.getDocDescription());
      } else {
        reporter.print(Kind.NOTE,
            "Unhandle SpecifiedElement " + element.getKind() + " / " + element.toString());
      }
    }

    try {
      if (outputForamts.contains("hwp") || outputForamts.contains("hwpx")) {
        File file = new File(destinationDir, outputFilename + ".hwp");
        HwpWriter.newInstance().write(descriptions, file, outputForamts);
      }
      if (outputForamts.contains("docx")) {
        File file = new File(destinationDir, outputFilename + ".docx");
        DocxWriter.newInstance().write(descriptions, file, outputForamts);
      }
      if (outputForamts.contains("yaml")) {
        File file = new File(destinationDir, outputFilename + ".yaml");
        YamlWriter.newInstance().write(descriptions, file, outputForamts);
      }
      if (outputForamts.contains("text")) {
        File file = new File(destinationDir, outputFilename + ".txt");
        TextWriter.newInstance().write(descriptions, file, outputForamts);
      }
    } catch (Exception e) {
      reporter.print(Kind.ERROR, e.getLocalizedMessage());
      return false;
    }

    return true;
  }
}
