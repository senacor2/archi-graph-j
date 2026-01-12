# How to create a component model

This is probably the trickiest part but once set up, the model will be rather stable.
The model is defined as a JSON file as shown in the data directory.

Think of the drawing canvas as something like a spreadsheet of rectangular cells. As long as there is no design
tool for the model I use an Excel spreadsheet to create an initial design.
The top left cell has the coordinate 0,0.
Each application occupies one cell.

A component must be sized to accommodate all apps and leave enough space for a proper routing of the information flows.
The size of the component header is included in the component height, so the minimum height of a component is 2 rows:
one row for the header and one for the body. The minimum width is 1.
When components contain sub-components (up to for levels of nesting are allowed), the outer component must reserve
the space for each component including the header. An `app-area` can be defined for each component containing
apps and sub-components.

The coordinates inside a component also start at 0,0 in the body of the component.

Leave one empty cell on each side of a top level components because the proxy apps that represent cross component
information flows will be placed here. It may be necessary to enlarge the top level components in order to provide
enough room for the proxy applications.

# How to create the application list

The application is read from a CSV file:

| Field name | Example | Description                                                                                 |
| ---- | ---- |---------------------------------------------------------------------------------------------|
| App-Id | APP-1234 | Unique Id of the application, usually taken from an EAM database                            |
| App-Name | AIDA | Descriptive name of the application. Does not have to be unique but it helps                |
| Component | Retail Master Data | Name of the component. Must match the name used in the component model                      |
| Attribut 1 | 4 | 1st custom information about the App. Used to control formatting of the apps in the diagram |
| Attribute 2 | No | 2nd custom information |
| Attribute 3 | 2028 | 3rd custom information |
| Attribute 4 | No | 4th custom information |

The data should be extracted from an EAM system and the process of extracting, cleaning and formatting should be
automated to be repeatable.

The data file **must** contain a header line which is used to determine the field names of the attributes.

Suggested cleanup activities are:

1. Remove all apps which are not relevant for the diagram.
2. Make sure that all apps are assigned a component.
3. Determine how many apps are in each component in order to size the components correctly.
4. Harmonize spelling if necessary. Remove leading and trailing whitespace from names.
5. It may be necessary to consolidate multiple apps into one for the purpose of the diagram. This may be necessary 
   for centrally maintained application templates which are rolled out to various markets.

# How to create the information flow list

The information flow list is another CSV file:

| Field name | Example | Description |
| --- | --- | --- |
| Source app id | APP-1234 | ID of the source application |
| Dest app id | APP-2345 | ID of the destination application |
| Information flow ID | IF 3456 | Unique ID of the information flow. |
| Business object | Customer | Descriptive name of the business object transported over the flow. Can be empty |
| Flow direction | oneway or twoway | oneway = flow from source to destination, twoway = bidirectional flow |

# The Components file

A system is defined as a tree, documented in JSON. The root of the tree is a system with top-level components. The
name of the system is shown in a horizontal heading bar at the top of the diagram.

Each component has a name (which **must** match the component name used in the applications file) and (row/colum) 
coordinates and height and width also expressed in rows and columns.

A component with no sub-components uses the entire component body, i.e. the overall size without the top row 
occupied by the component heading for apps which are also placed in cells. When the component has sub-components it 
is the responsibility of the designer to fit them into the space.

For components that contain sub-components **and** applications you need to tell the layout algorithm where to place 
the applications. Use the `app-area` element of the component to define where the algorithm shall place the apps. 
The `app-area` must not overlap any sub-components.

In the current design the algorithm is restricted to four levels of nested components.

# How to create the Rules file

The appearance of applications in the diagram is driven by a rules file. If omitted, all applications will have black
and white text.

Rules are defined in a tabular format: Each line is a rule with conditions and results. The rule engine evaluates each
rule in the order they are defined. If all conditions match, the result in this line is returned. Otherwise, the next
line is evaluated until the end of the file. If no rule matches, the default format will be used (white text, black
background).

Because rules are evaluated sequentially you must define the most specific rules first and less specific towards the
end of the file. A rule with all wildcard matches must be at the end of the file, or it will obscure all lines that
follow.

The rules file is a CSV file, and it must include a heading line. The heading line names the attributes checked by the
rule engine and the results returned by the rules. The attribute names must be equal to the additional attribute fields
defined in the heading of the applications file. In addition, the `id` attribute is supported which references the
_AppID_.

## Rule name

Each rule has a name. Rule names are used when the legend is painted, so make them descriptive. Otherwise, the legend
will be of limited usefulness.

Multiple rules can have the same name. This is useful when you want to define _OR_ conditions. While all condition fields
in a rule are connected by _AND_, rules are connected by _OR_. When two rules have the same name, they should have the
same result. While this appears to be redundant, tabular rules are easiest to understand and to learn and a little bit
of redundancy seems to be acceptable.

## Conditions

The rule engine supports three condition types:

1. When the column contains a value, this value must be equal to the corresponding value of the additional attribute 
   of the application. When the value contains one or more asterisks `*`, it is interpreted as a regular expression 
   and matching is performed.
2. When the value is preceded by an `!`, this value must **not** be equal to the corresponding value of the 
   additional attribute of the application. When the value contains one or more asterisks `*`, it is interpreted as 
   a regular expression and matching is performed.
3. When the column contains a single asterisk `*` this is the wildcard expression which matches any value in the 
   additional application attributes.

## Results

Results define the value returned by the rules. A rule can return one or more values as key/value pairs. Currently,
the following keys are supported:

- `backgroundColor` - the background color of the application rectangle.
- `borderColor` - the color of the rectangle's border.
- `fontColor` - the color used to print the text (i.e. the application name).
- `fillStyle` - How the background color is used. DrawIO supports these styles: `solid` to entirely fill the rectangle
  with the background color, `hatch` paints the background with diagonal lines and `dots` paints a spotted background.

The result names must be preceded by `result.` identifying them as results.

## Example Rules File

```
"Name","id","market","target","replacedByAbc","eamStatus","result.backgroundColor","result.fontColor","result.
borderColor","result.fillStyle"
"Replaced by ABC","*","*","*","Yes","*","#EA9999","#000000","#000000","solid"
"Local application","*","!central","*","*","*","#378C96","#FFFFFF","#000000","solid"
"New central application","*","*","2026","*","*","#38761D","#FFFFFF","#000000","solid"
"Existing central application","*","*","*","*","*","#000000","#FFFFFF","#000000","solid"
```

The first line contains the headers. _Name_ is a predefined column where the rule name must be defined. _id_ is also
predefined and refers to the AppID of an application. Use this column for app-specific appearence.

_market_, _target_, _replacedByAbc_ and _eamStatus_ are attributes that would also appear in the applications file.
Make sure that they are spelled equally.

The remainder of the header are the result columns, each identified by the `result.`-Prefix.

The example above contains four rules.

The first rule, named _Replaced by ABC_ returns a result, when the _replacedByAbc_ column contains _Yes_. It returns
a solid pink background, a black border and text.

The second rule, named _Local Application_ returns a result, when the _market_ column is not _central_. It returns a
blueish solid background, white text and a black border.

The third rule, named _New central application_ returns a result, when the _target_ column is equal to _2026_. It 
returns a solid dark green background, white text and a black border.

The final rule, named _Existing central application" always returns a result because all conditions are wildcards.