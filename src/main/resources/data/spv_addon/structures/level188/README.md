# Level 188 Structure Files

This directory should contain the following NBT structure files for Level 188:

## Required Structure Files:

1. **entrance.nbt** - 16x100x16 entrance structure (placed at chunk 0,0)
2. **exit.nbt** - 16x100x16 exit structure (placed at chunk 3,3)
3. **room1.nbt** - 16x100x16 room variant 1
4. **room2.nbt** - 16x100x16 room variant 2
5. **room3.nbt** - 16x100x16 room variant 3
6. **room4.nbt** - 16x100x16 room variant 4

## Structure Specifications:

- **Dimensions**: Each structure should be 16x100x16 blocks (one chunk wide, 100 blocks tall)
- **Height**: Structures span from Y=0 to Y=99 (100 blocks total)
- **Material**: Use concrete blocks or appropriate Level 188 themed blocks
- **Layout**: The 4x4 chunk arrangement creates a 64x64 base with 100 block height

## Chunk Layout:
```
[entrance] [room1] [room2] [exit]
[room3]    [room4] [room1] [room2]
[room2]    [room3] [room4] [room1]
[exit]     [room2] [room3] [entrance]
```

## How to Create Structures:

1. Build the structures in-game using creative mode
2. Use structure blocks to save them as NBT files
3. Place the NBT files in this directory
4. Ensure each structure is exactly 16x100x16 blocks

## Fallback Behavior:

If structure files are missing, the generator will create a simple hollow 64x100x64 concrete structure as a fallback.
