# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**archi-graph-j** is a Java CLI tool that generates Draw.IO architecture diagrams from CSV/JSON input files. It takes a component model (JSON) plus optional applications (CSV), information flows (CSV), and formatting rules (CSV), and produces a Draw.IO XML file.

## Build and Test Commands

```bash
# Build (fat JAR with all dependencies)
mvn clean package

# Build without tests
mvn clean package -DskipTests

# Run all tests
mvn clean test

# Run a single test class
mvn test -Dtest=RenderModelTest

# Run a single test method
mvn test -Dtest=RenderModelTest#methodName
```

Output artifact: `target/archi-graph-j-1.0-SNAPSHOT-shaded.jar`

**Run the tool:**
```bash
java -jar target/archi-graph-j-1.0-SNAPSHOT-shaded.jar -o output.xml component-model.json
```

Key CLI flags: `-a apps.csv`, `-f flows.csv`, `-r rules.csv`, `-o output.xml`, `-x` (validate only), `-lc`/`-lf` (lenient modes).

## Version Control

The project is tracked in a git repository which is synced to  senacor2/archi-graph-j 
on github. The remote requires pull requests. We use feature branches for all changes.

## Architecture

The pipeline is: **Read → Validate → Render → Draw**

### Packages

| Package | Responsibility |
|---|---|
| `model` | Domain entities: `Model`, `L1Component`, `Component`, `Application`, `InformationFlow`, layout helpers |
| `read` | Parses JSON component model and CSV files into the domain model |
| `validate` | `LayoutValidator` (geometry, nesting, overlap) and `SemanticValidator` (references, capacity) |
| `render` | `RenderModel` converts domain model to visual `Rectangle`/`Line` elements; formatters apply styling |
| `rules` | Rule engine evaluating conditions against app attributes to determine visual style |
| `draw.drawio` | `DrawModelImpl` converts render elements to jgraphx `mxCell` objects and writes Draw.IO XML |

### Key Design Points

**Grid-based layout:** Everything is positioned on a grid where `ROW_HEIGHT = 200px` and `COL_WIDTH = 320px`. Components occupy grid cells; applications are placed within component app-areas.

**Component hierarchy:** `L1Component` → `Component` (up to 4 nesting levels). Each component has a head rectangle and a body rectangle. Applications appear inside body cells.

**Proxy applications:** When an app appears in a flow but lives in another L1 component, a proxy copy is placed in the `ProxyBoxLayout` margin area around the L1 component.

**Line routing:** `RenderModel.getAnchors()` routes information flow lines — straight if in same row/col with no apps between, L-shaped otherwise.

**Rule engine:** `RuleBase` loads rules from CSV; first-match-wins against application attributes (exact, regex `value*pattern`, negated `!value`, wildcard `*`). Rules control `backgroundColor`, `borderColor`, `fontColor`, `fillStyle`.

**Rendering elements:** `Rectangle` and `Line` implement a `draw(DrawModel)` visitor method, keeping render logic separate from output format.

### Input File Formats

- **Component model (JSON):** Hierarchy of components with grid coordinates, sizes, and app-area regions
- **Applications (CSV):** ID, Name, Component, plus 4 custom attributes for rule matching
- **Information flows (CSV):** Source ID, Dest ID, Flow ID, Business Object, Direction (one-way/two-way)
- **Rules (CSV):** Conditions per attribute column → result key-value pairs

See `docs/howto.md` for detailed format specifications and `data/` for sample files.

### Key Dependencies

- **jgraphx 4.2.3** — Draw.IO/JGraph model and XML output (from GitHub package registry)
- **Lombok** — `@Getter`, `@Setter`, `@Builder`, `@Slf4j` used throughout
- **jackson-databind** — JSON parsing for component model
- **commons-csv / commons-cli** — CSV and CLI argument parsing
- **JUnit 5 + AssertJ** — Testing
