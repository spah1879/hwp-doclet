
# HWP Doclet

A Javadoc Doclet for HWP(Hangul Word Processor)

## Features

* Genearte **HWP/HWPX**, **DOCX** and **XLSX**  document file with javadoc tool
* Genearte **YAML**, **JSON** and Simple Text file with javadoc tool

## Compatibility & Used Libraries

* [Doclet API of Java 11 or above](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.javadoc/jdk/javadoc/doclet/Doclet.html)
* [hwplib](https://github.com/neolord0/hwplib), [hwpxlib](https://github.com/neolord0/hwpxlib) and [hwp2hwpx](https://github.com/neolord0/hwp2hwpx) is used to genearte HWP/HWPX document file
* [Apache POI](https://poi.apache.org) is used to genearte DOCX & XLSX document file
* [snakeyaml](https://bitbucket.org/snakeyaml/snakeyaml) and [jackson](https://github.com/FasterXML/jackson) is used to genearte YAML and JSON file

## Usage

### Options

* Destination directory for output file(s)<br>
  --output-directory, -d
* Output file name<br>
  --output-filename, -n
* Specify Output format(s)<br>
  --output-formats, -f<br>
  * Avaliable values : hwp, hwpx, docx, xlsx, yaml, json, text

### on Gradle

* Add javadoc block like below in build.gradle

```gradle
javadoc {
 def hwpDoclet
 rootProject.with {
  def docletConf = configurations.create('resolveDocletArtifact')
  dependencies.add(docletConf.name, 'io.github.spah1879:hwp-doclet:1.4.1)
  hwpDoclet = docletConf.resolve()[0]
 }

 failOnError = false
 destinationDir = project.docsDir
 options {
  showFromPrivate()
  doclet = "io.github.spah1879.doclet.HwpDoclet"
  docletpath hwpDoclet
  addStringOption("-output-filename", "${project.name}")
  addStringOption("-output-formats", "hwp,docx,yaml")
 }
}
```

* Then execute gradle command like below

```bash
$ gradle javadoc
```
