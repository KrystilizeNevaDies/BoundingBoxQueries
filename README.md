# BoundingBoxQueries

Efficient bounding box queries for 3D data structures.

We currently support the following data structures:
- Bounding Volume Hierarchies (BVH) [see](./src/main/java/org/bbq/lookup/BoundingBoxLookups.java)
  - [x] List-based BVH
  - [x] Grid-based BVH
  - [x] Tree-based BVH

## Wrappers
We also have wrappers implemented for the following libraries:
- [x] [Minestom](https://minestom.net/) [see](./minestom/src/main/java/org/bbq/wrapper/MinestomBoundingBoxLookups.java)
