# ibit-mybatis-generator

## 用法

### 相关引用

#### Gradle

```
compile 'tech.ibit:ibit-mybatis-generator:2.x'
```

#### Maven

```
<dependency>
  <groupId>tech.ibit</groupId>
  <artifactId>ibit-mybatis-generator</artifactId>
  <version>2.x</version>
</dependency>
```

### 代码示例

```
public void test() {

    String driverName = "com.mysql.jdbc.Driver";

    // 设置jdbc路径
    String jdbcURL = "jdbc:mysql://localhost:3306/test?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true";
    String username = "**";
    String password = "**";

    // 指定表明
    String[] tables = new String[]{
            "organization",
            "user",
            "user_login_record"
    };

    Generator generator = new Generator(driverName, jdbcURL, username, password, StringUtils.join(tables, ","));

    // 指定基本包路径
    String projectDir = "./";
    String basePackage = "tech.ibit.mybatis.generator.demo";
    generator.setDefaultProject(new ProjectInfo(projectDir, basePackage));

    // 指定作者
    generator.setAuthor("IBIT程序猿");

    // 设置生成所有类型文件，Mapper, Dao, Entity
    generator.setWithAll();
    
    // 是否覆盖（false：文件已经存在，则不覆盖）
    generator.setOverride(true);

    // generator.setWithEntity(); // 设置只生成Entity
    // generator.setWithMapper(); // 设置只生成Mapper文件

    // generator.setEntityProject(); // 可以单独设置entity项目路径
    // generator.setMapperProject(); // 可以单独设置mapper项目路径

    generator.generateFiles();

}

```

### 不足地方需要改进


 * 目前mysql支持得比较好，其他数据库待验证
 * 需要做成插件的方式，这样更加方便生成模板代码
