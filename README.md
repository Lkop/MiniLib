# MiniLib

## Import

### Maven

Add this in your `pom.xml`
```
<dependency>
    <groupId>org.lkop</groupId>
    <artifactId>minilib</artifactId>
    <version>0.1.0</version>
    <scope>system</scope>
    <systemPath>YOUR_PATH/minilib.jar</systemPath>
</dependency>
```
Specify `out\artifacts\MiniLib\minilib.jar` path in `<systemPath>`

## How To Use

```diff
+ @ExtendWith(MiniLibAnnotationBeforeEach.class)
class MyTestClass {

    @Test
+   @MiniLib
    void MyTestMethod() {
        //My code...
    }
}
```
## License

GNU General Public License v3.0