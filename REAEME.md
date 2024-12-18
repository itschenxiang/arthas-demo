# arthas

## watch & trace
### 基本使用

### 其他：如何 watch lambda表达式
在 Java 代码常常会编写如下代码：
```java
    public CommonResponse<String> watchLambda() {
        executorService.submit(() -> {
            log.info("watchLambda 1");
            executorService.submit(() -> {
                log.info("watchLambda 2");
            });
        });
        return CommonResponse.success("Hello Lambda");
    }
```
以这个代码为例，在 jdk7 之前，通常会书写一个匿名类或者一个 Runnable 的具名实现类。从 jdk8 开始，我们更加倾向于使用 lambda 表达式。
怎么去 trace 或 watch lambda 表达式呢？

#### lambda 表达式的底层实现
查看对应类的字节码，发现了 2 个意外之外的方法签名
```bash
$ javap -p target/classes/com/itschenxiang/arthasdemo/controller/IndexController.class
Compiled from "IndexController.java"
public class com.itschenxiang.arthasdemo.controller.IndexController {
  private static final org.slf4j.Logger log;
  private java.util.concurrent.ExecutorService executorService;
  public com.itschenxiang.arthasdemo.controller.IndexController();
  public void init();
  public com.itschenxiang.arthasdemo.common.CommonResponse<java.lang.String> watchLambda();
  private void lambda$watchLambda$1();
  private static void lambda$watchLambda$0();
  static {};
}
```

#### 如何 watch or trace
在 arthas 中不用这么麻烦，可以直接通过 sm 命令查看某个类的方法：

```bash
$ sm com.itschenxiang.arthasdemo.controller.IndexController
sm com.itschenxiang.arthasdemo.controller.IndexController
com.itschenxiang.arthasdemo.controller.IndexController <init>()V
com.itschenxiang.arthasdemo.controller.IndexController init()V
com.itschenxiang.arthasdemo.controller.IndexController watchLambda()Lcom/itschenxiang/arthasdemo/common/CommonResponse;
com.itschenxiang.arthasdemo.controller.IndexController lambda$watchLambda$1()V
com.itschenxiang.arthasdemo.controller.IndexController lambda$watchLambda$0()V
Affect(row-cnt:5) cost in 30 ms.
```

此时，我们直接按照 trace 的通用方式即可：
```bash
trace com.itschenxiang.arthasdemo.controller.IndexController lambda$watchLambda$1
# or
trace com.itschenxiang.arthasdemo.controller.IndexController lambda$watchLambda$0
```

这里可能还有一个疑问，多层级 lambda 表达式怎么区分这个签名？可以看到这个生成的方法名称的格式是`lambda${{methodName}}${{num}}`，对于不同方法中的 lambda 表达式好区分，对于同一个方法中的多个 lambda 表达式（顺序或层级嵌套），怎么区分。
