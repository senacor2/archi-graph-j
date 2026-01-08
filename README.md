# Archi-Graph-J

A program to render high level architecture diagrams with an automatic layout.

## Purpose

The purpose is to render enterprise level or domain level architecture diagrams with applications, information flows
between applications and components that group applications. Components may be nested up to 4 levels.

The program consumes 3 input files of which 2 can be exported from EAM databases if available:

1. Application list with references to the enclosing components.
2. Information flows with references to applications.

The third file describes the layout of the components. This file needs to be created manually and this is the
actual creative work.

The output is in Draw.IO-Format.

See [The How-To document](docs/howto.md) for details about the file formats and how to prepare the input files.

## Command line Syntax

```
archi-graph-j options -c component-model -a applications.csv -i informationflows.csv 
```

Options are:

- `-d` or `--debug` Turn on debug logging.
- `-t` or `--trace` Turn on tracing (more details than debug)
- `-lc` or `--lenient-comp` Applications that reference a component that is not in the component model are ignored.
- `-lf` or `--lenient-flow` Information flows that reference an application which is not in the model are ignored.
Information flows where the source or destination application is not in the model are also ignored.
- `-x` or `--validateOnly` Load the model and validate it, then exit. No rendering of output is done.
- `-X` or `--continueWithFailure` Try rendering, even if validation found issues. May fail during rendering.

## Future work

- Build a graphical editor for the component file.
- Add more output formats
