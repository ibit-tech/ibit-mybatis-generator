package tech.ibit.mybatis.generator;


import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 生成示例
 *
 * @author IBIT-TECH
 * mailto: ibit_tech@aliyun.com
 */
public class GeneratorTest {

    @Test
    @Ignore
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

//         generator.setWithEntity(); // 设置只生成Entity
//         generator.setWithMapper(); // 设置只生成Mapper文件
//
//         generator.setEntityProject(); // 可以单独设置entity项目路径
//         generator.setMapperProject(); // 可以单独设置mapper项目路径

        generator.generateFiles();

    }

}