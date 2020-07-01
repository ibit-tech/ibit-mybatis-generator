package tech.ibit.mybatis.generator.impl;


import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成表信息
 *
 * @author IBIT TECH
 */
public class PropertyGenerator extends AbstractGenerator {

    /**
     * 表引入类
     */
    private static final String CLASS_DB_TABLE = "tech.ibit.mybatis.sqlbuilder.Table";

    /**
     * 列引入类
     */
    private static final String CLASS_DB_COLUMN = "tech.ibit.mybatis.sqlbuilder.Column";

    /**
     * 生成文件
     *
     * @param tableInfo   表信息
     * @param basePackage 基础包
     * @param projectDir  项目路径
     * @param override    是否覆盖
     * @param author      作者
     */
    public void generateFile(TableInfo tableInfo, String basePackage, String projectDir, boolean override, String author) {
        String filePath = Utils.getTableFilePath(tableInfo.getTable(), basePackage, projectDir);
        File file = new File(filePath);
        if (needCreateNewFile(file, override)) {
            String javaCode = generateJavaCode(tableInfo, basePackage, author);
            generateFile(file, javaCode, override);
        }
        System.out.println("Generate table file: " + filePath);
    }


    /**
     * 生成java代码
     *
     * @param author      表
     * @param basePackage 基础包路径
     * @param tableInfo   表信息
     * @return java代码
     */
    private String generateJavaCode(TableInfo tableInfo, String basePackage, String author) {

        StringBuilder javaCode = new StringBuilder();

        javaCode.append(String.format("package %s;\n\n", Utils.getTablePackage(basePackage)));

        List<String> importClasses = new ArrayList<>();
        importClasses.add(CLASS_DB_TABLE);
        importClasses.add(CLASS_DB_COLUMN);
        importClasses.sort(String::compareTo);
        importClasses.forEach(s -> javaCode.append(String.format("import %s;\n", s)));

        javaCode.append("\n");

        // 文件头模板
        String titleTemplate = "/**\n"
                + SPACE + "* Property for %s\n"
                + SPACE + "*\n"
                + SPACE + "* @author %s\n"
                + SPACE + "*/\n";
        javaCode.append(String.format(titleTemplate, tableInfo.getTable(), StringUtils.trimToEmpty(author)));

        javaCode.append(String.format("public interface %s {\n\n", Utils.getTableClassName4Short(tableInfo.getTable())));

        // 表字段模板
        String tableTemplate = BLANK + "/**\n"
                + BLANK + SPACE + "* %s\n"
                + BLANK + SPACE + "*/\n"
                + BLANK + "Table TABLE = new Table(\"%s\", \"%s\");\n\n";

        javaCode.append(String.format(tableTemplate, StringUtils.trimToEmpty(tableInfo.getComment()), tableInfo.getTable(), tableInfo.getAlias()));

        // 列字段模板
        String columnTemplate = BLANK + "/**\n"
                + BLANK + SPACE + "* %s\n"
                + BLANK + SPACE + "*/\n"
                + BLANK + "Column %s = new Column(TABLE, \"%s\");\n\n";

        //create columns
        tableInfo.getColumns()
                .forEach(
                        column ->
                                javaCode.append(
                                        String.format(columnTemplate, StringUtils.trimToEmpty(column.getComment()), column.getPropertyName(), column.getColumn())
                                )
                );

        //end class
        javaCode.append("\n}\n");
        return javaCode.toString();
    }
}
