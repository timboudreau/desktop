Geometry
========

Extensions to the JDK's built-in geometry classes, and utilities for performing
various geometric calculations.

Specifically of interest:

 * `Circle` - a circle `Shape` primitive with many useful methods for determining
containment, distance, tangent lines, distance from center and edges and other 
things that have many applications
 * `EqLine` - A double-based `Line2D` implementation with features such as
   * Rotation
   * Set angle
   * Move by distance + angle
   * Fuzzy equality comparison
   * Easily compute perpendiculars
   * Easily find intersection points
 * `EqPoint` and `EqPointDouble` - float and double implementations of `Point2D` 
with fuzzy equality testing
 * `Axis`, `Hemisphere`, `Quadrant`, `Sector` - for working with partitions of circles
 * `Angle` - for performing computations that combine angles correctly
 * `Rhombus` - a rhombus `Shape` primitive defined by a center point, two corner diagonals
and a rotation angle
 * `Triangle2D` - a triangle `Shape` primitive with methods for tesselation, identifying 
nearest points and more
 * `EnhRectangle2D` - a Rectangle2D whose `add(Rectangle2D)` detects if it or the passed
rectangle is empty and avoids including the coordinates of an empty rectangle in the
result, for using in building _damage regions_
 * `Polygon2D` - a polygon geometric primitive with optimized hit-testing, finding the
nearest points to a given point (useful when adding points in a UI), homomorphism detection
 * `MinimalAggregateShapeFloat` and `MinimalAggregateShapeDouble` - decomposes multiple
shapes into a minimal-footprint array of coordinates and `PathIterator` instructions,
for destructive additive CSG operations
 * `AnglesAnalyzer` - analysis tools for decomposing an ad-hoc shape as defined by its
`PathIterator`, identifying generally the type of shape and other details
 * Path element classes to make working with `PathIterator`s more straightforward
 * `MutableRectangle2D` - a rectangle primitive that implements the necessaries to
easily create an edge-and-corner-resizable UI component (e.g. for rectangular selection)
 * `GeometryUtils` - Utilities for things such as computing the exterior length of an
arbitrary shape, various containment and intersection utilities, equality-within-tolerance,
converting angles and coordinates to strings with finite precision
 * `PooledTransform` - allows AffineTransform instances, which can be a bottleneck
if many components use them for painting (there can be hundreds of thousands in-memory
in graphics editor applications), allowing them to be borrowed used and returned (or
abandoned and `PhantomReferences` and a `ReferenceQueue` will take care of returning them
to the pool)


The angle and circle classes assume a coordinate system where 12 o'clock is 0 degrees.

