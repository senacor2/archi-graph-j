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

## Future work

- Build a graphical editor for the component file.
- Add more output formats
