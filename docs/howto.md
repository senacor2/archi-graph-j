# How to create a component model

This is probably the trickiest part but once set up, the model will be rather stable.
The model is defined as a JSON file as shown in the data directory.

Think of the drawing canvas as something like a spreadsheet of rectangular cells.
The top left cell has the coordinate 0,0.
Each application occupies one cell.
A component must be sized to accommodate all apps and leave enough space for a proper routing of the information flows.
The size of the component header is included in the component height, so the minimum height of a component is 2 rows:
one row for the header and one for the body. The minimum width is 1.
When components contain sub-components (up to for levels of nesting are allowed), the outer component must reserve
the space for each component including the header. An `app-area` can be defined for each component containing
apps and sub-components.

Leave one empty cell on each side of a top level components because the proxy apps that represent cross component
information flows will be placed here.