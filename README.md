# Game-of-Life
A concurrent Life-like cellular automaton simulator.

### Requirements
* Scala 2.11.8. The binaries should be in your PATH.
* [CSO](https://www.cs.ox.ac.uk/people/bernard.sufrin/personal/CSO/). You'll need to edit the makefil and set the `CSOPATH` variable to the path of your `cso.jar` file. 

### Usage
Compile the program using make.
```
make
```

To run the program you'll need a RLE pattern file. To simulate the pattern in `pattern` for `n` generations execute the script `Life`.

```
./Life pattern n
```

