# Archi-Graph-J

A program to render high level architecture diagrams with an automatic layout.

## Purpose

The purpose is to render enterprise level or domain level architecture diagrams with applications, information flows
between applications and components that group applications. Components may be nested up to 4 levels.

The program consumes 4 input files of which 2 can be exported from EAM databases if available:

1. Application list with references to the enclosing components.
2. Information flows with references to applications.

The third file describes the layout of the components. This file needs to be created manually and this is the
actual creative work.

The fourth file contains rules controlling how apps are painted in the diagram based on the application's attributes.

The output is in Draw.IO-Format.

See [The How-To document](docs/howto.md) for details about the file formats and how to prepare the input files.

## Command line Syntax

```
archi-graph-j options component-model -a applications.csv -f informationflows.csv -r rulebase.csv
```

`component-model` is the name of the JSON file that describes the component layout.

Options are:

- `-h` or `--help` shows the arguments and options.
- `-a` or `--apps` followed by the name of the application file.
- `-f` or `--flows` followed by the name of the information flows file.
- `-r` or `--rules` followed by the name of the rules file.
- `-o` or `--output` overrides the output file name. If missing, the name is the component file base name with the suffix `drawio.xml`.
- `-d` or `--debug` Turn on debug logging.
- `-t` or `--trace` Turn on tracing (more details than debug)
- `-lc` or `--lenient-comp` Applications that reference a component that is not in the component model are ignored.
- `-lf` or `--lenient-flow` Information flows that reference an application which is not in the model are ignored.
Information flows where the source or destination application is not in the model are also ignored.
- `-x` or `--validateOnly` Load the model and validate it, then exit. No rendering of output is done.
- `-X` or `--continueWithFailure` Try rendering, even if validation found issues. May fail during rendering.

## Building

An uberjar containing all dependencies can be built by running

````shell
mvn -Dskip-tests=yes package
````

This will invoke the maven shade plugin that collects all dependencies and prepares a proper manifest that
starts the Main class. You can now start the program using `java -jar archi-graph-j.jar`.

The Docker image is intended to be the base of your own image. It contains the generator and when run will show the
usage information of the current version.

Your own image would use it as a base and add the data cleansing programs, the mount point for the input and output
files, scripts that do preprocessing and start the program and override the entrypoint.


## Future work

- Build a graphical editor for the component file.
- Add more output formats
