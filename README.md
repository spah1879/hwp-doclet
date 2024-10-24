
# HWP Doclet
A Javadoc Doclet for HWP(Hangul Word Processor)

## Features
* Genearte HWP document file with javadoc tool
* Genearte YAML file with javadoc tool
* Genearte Simple Text file with javadoc tool

## Compatibility & Used Libraries
* [Doclet API of Java 11 or above ](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.javadoc/jdk/javadoc/doclet/Doclet.html)
* [hwplib](https://github.com/neolord0/hwplib) is used to genearte HWP document file
* [snakeyaml](https://bitbucket.org/snakeyaml/snakeyaml) is used to genearte YAML file

## Usage

### Options
* Destination directory for output file(s)<br>
  --output-directory, -d
* Output file name<br>
  --output-filename, -n
* Specify Output format(s)<br>
  --output-formats, -f<br>
  - Avaliable values : hwp, yaml, text

### on Gradle
* Add javadoc block like below in build.gradle

```
javadoc {
	def hwpDoclet
	rootProject.with {
		def docletConf = configurations.create('resolveDocletArtifact')
		dependencies.add(docletConf.name, 'io.github.spah1879:hwp-doclet:1.0.0')
		hwpDoclet = docletConf.resolve()[0]
	}

	failOnError = false
	destinationDir = project.docsDir
	options {
		showFromPrivate()
		doclet = "io.github.spah1879.doclet.HwpDoclet"
		docletpath hwpDoclet
		addStringOption("-output-filename", "${project.name}")
		addStringOption("-output-formats", "hwp,yaml")
	}
}
```

* Then execute gradle command like below
```
$ gradle javadoc
```