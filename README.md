# MiniLib

## Import

### Maven

```
<dependency>
    <groupId>org.lkop</groupId>
    <artifactId>minilib</artifactId>
    <version>0.1.0</version>
    <scope>system</scope>
    <systemPath>YOUR_PATH/minilib.jar</systemPath>
</dependency>
```
Specify `out\artifacts\MiniLib\minilib.jar` path in `<systemPath>`.

## How To Use

[//]: # (### Main)

### Tests

Add `@MiniLib` annotation in each test you want to be parsed.

```diff
+ @ExtendWith(MiniLibAnnotation.class)
+ @MiniLibDependenciesFolder("---- DEPENDENCIES PATH (eg. C:/Users/MyUser/.m2) ----")
+ @MiniLibOutputFolder("---- OUTPUT PATH (default "./" ) ----") <-- if removed, default "./" will be applied
class MyTestClass {

    @Test
+   @MiniLib
    void MyTestMethod() {
        //My code...
    }
}
```

If you do not add `@MiniLibOutputFolder("---- OUTPUT PATH ----")`, default output path `./` will be used.


## License

GNU General Public License v3.0