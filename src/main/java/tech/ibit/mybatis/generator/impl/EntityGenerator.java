package tech.ibit.mybatis.generator.impl;

import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.ProjectInfo;
import tech.ibit.mybatis.generator.table.ColumnInfo;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;

import java.io.File;
import java.util.*;

/**
 * 生成实体
 *
 * @author IBIT TECH
 */
public class EntityGenerator extends AbstractGenerator {

    /**
     * 表字段注解
     */
    private static final String CLASS_DB_TABLE = "tech.ibit.sqlbuilder.annotation.DbTable";

    /**
     * 主键注解
     */
    private static final String CLASS_DB_ID = "tech.ibit.sqlbuilder.annotation.DbId";

    /**
     * 列注解
     */
    private static final String CLASS_DB_COLUMN = "tech.ibit.sqlbuilder.annotation.DbColumn";

    /**
     * lombok.Data
     */
    private static final String CLASS_LOMBOK_DATA = "lombok.Data";

    /**
     * lombok.Setter
     */
    private static final String CLASS_LOMBOK_ALL_ARGS_CONSTRUCTOR = "lombok.AllArgsConstructor";


    /**
     * lombok.Setter
     */
    private static final String CLASS_LOMBOK_NO_ARGS_CONSTRUCTOR = "lombok.NoArgsConstructor";


    /**
     * DBId路径
     */
    private static final String CLASS_MULTI_ID = "tech.ibit.sqlbuilder.MultiId";

    /**
     * DBId
     */
    private static final String MULTI_ID_NAME = "MultiId";

    /**
     * 生成实体
     *
     * @param tableInfo 表
     * @param project   项目信息
     * @param override  是否覆盖
     * @param author    作者
     */
    public void generateFile(TableInfo tableInfo, ProjectInfo project, boolean override, String author) {

        String table = tableInfo.getTable();
        String filePath = Utils.getEntityFilePath(table, project.getBasePackage()
                , project.getProjectDir());

        File file = new File(filePath);
        if (needCreateNewFile(file, override)) {

            List<String> extraImportClasses = new ArrayList<>();
            extraImportClasses.add(CLASS_DB_TABLE);
            extraImportClasses.add(CLASS_LOMBOK_DATA);

            int idSize = tableInfo.getIdNames().size();
            int columnSize = tableInfo.getColumnNames().size();
            if (idSize > 0) {
                extraImportClasses.add(CLASS_DB_ID);
            }
            if (idSize != columnSize) {
                extraImportClasses.add(CLASS_DB_COLUMN);
            }

            // 生成实体类
            String javaCode = generateJavaCode(tableInfo, Utils.getEntityClassName4Short(table),
                    null,
                    project.getBasePackage(),
                    tableInfo.getColumns(),
                    tableInfo.getImportClasses(),
                    extraImportClasses,
                    author);
            generateFile(file, javaCode, override);

            System.out.println("Generate entity file: " + file.getPath());

            // 生成多主键类
            if (idSize > 1) {
                String primaryKeyFilePath = Utils.getEntityIdFilePath(table
                        , project.getBasePackage()
                        , project.getProjectDir());
                File primaryKeyFile = new File(primaryKeyFilePath);
                String code = generateJavaCode(tableInfo,
                        Utils.getEntityIdClassName4Short(table),
                        MULTI_ID_NAME,
                        project.getBasePackage(),
                        tableInfo.getIds(),
                        tableInfo.getIdImportClasses(),
                        Arrays.asList(CLASS_DB_TABLE, CLASS_DB_ID, CLASS_MULTI_ID, CLASS_LOMBOK_DATA,
                                CLASS_LOMBOK_NO_ARGS_CONSTRUCTOR, CLASS_LOMBOK_ALL_ARGS_CONSTRUCTOR),
                        author);
                generateFile(primaryKeyFile, code, override);
                System.out.println("Generate entity primary key file: " + primaryKeyFile.getPath());
            }
        }
    }

    /**
     * 生成java代码
     *
     * @param tableInfo          表
     * @param generateClazzName  生成类名
     * @param implementInfName   实现接口名称
     * @param basePackage        基础包
     * @param columns            生成列
     * @param importClasses      导入类
     * @param extraImportClasses 其他导入类（非列相关的）
     * @param author             作者
     * @return java代码
     */
    private String generateJavaCode(TableInfo tableInfo, String generateClazzName
            , String implementInfName, String basePackage, List<ColumnInfo> columns
            , List<String> importClasses, List<String> extraImportClasses
            , String author) {

        //get import class
        Set<String> finalImports = new HashSet<>(importClasses);
        finalImports.addAll(extraImportClasses);
        importClasses = getFinalImports(finalImports);

        StringBuilder code = new StringBuilder();
        code.append(String.format("package %s;\n\n", Utils.getEntityPackage(basePackage)));
        importClasses.forEach(importClazz -> code.append(String.format("import %s;\n", importClazz)));

        code.append("\n");

        String titleTemplateWithComment = "/**\n"
                + SPACE + "* Entity for %s(%s)\n"
                + SPACE + "*\n"
                + SPACE + "* @author %s\n"
                + SPACE + "*/\n";

        String titleTemplate = "/**\n"
                + SPACE + "* Entity for %s\n"
                + SPACE + "*\n"
                + SPACE + "* @author %s\n"
                + SPACE + "*/\n";

        if (StringUtils.isNotBlank(tableInfo.getComment())) {
            code.append(String.format(titleTemplateWithComment, tableInfo.getTable()
                    , tableInfo.getComment(), StringUtils.trimToEmpty(author)));
        } else {
            code.append(String.format(titleTemplate, tableInfo.getTable(), StringUtils.trimToEmpty(author)));
        }

        // 扩展lombok信息
        appendLombok(extraImportClasses, code);

        // 表注解
        String annotationTableTemplate = "@DbTable(name = \"%s\", alias = \"%s\")\n";
        code.append(String.format(annotationTableTemplate, tableInfo.getTable(), tableInfo.getAlias()));

        String classTemplateWithImplements = "public class %s implements %s {\n\n";
        String classTemplate = "public class %s {\n\n";
        code.append(
                null == implementInfName
                        ? String.format(classTemplate, generateClazzName)
                        : String.format(classTemplateWithImplements, generateClazzName, implementInfName)
        );

        // 扩展属性
        appendProperties(columns, code);

        code.append("}\n");
        return code.toString();
    }

    /**
     * 扩展属性
     *
     * @param columns 列信息
     * @param code    代码
     */
    private void appendProperties(List<ColumnInfo> columns, StringBuilder code) {
        // 定义注释
        String noteTemplate = BLANK + "/**\n"
                + BLANK + SPACE + "* %s\n"
                + BLANK + SPACE + "* %s\n"
                + BLANK + SPACE + "*/\n";

        // 定义列
        String columnTemplate = BLANK + "@DbColumn(name = \"%s\")\n"
                + BLANK + "private %s %s;\n\n";

        // 定义可为null的列
        String nullableColumnTemplate = BLANK + "@DbColumn(name = \"%s\", nullable = true)\n"
                + BLANK + "private %s %s;\n\n";

        // 定义主键列
        String idTemplate = BLANK + "@DbId(name = \"%s\")\n"
                + BLANK + "private %s %s;\n\n";

        // 定义自增长主键列
        String autoIncreaseIdTemplate = BLANK + "@DbId(name = \"%s\", autoIncrease = true)\n"
                + BLANK + "private %s %s;\n\n";

        //defined property
        columns.forEach(column -> {
            code.append(String.format(noteTemplate, column.getComment(), column.getColumnType()));

            String template = !column.isId()
                    ? (column.isNullable() ? nullableColumnTemplate : columnTemplate)
                    : (column.isAutoIncrement() ? autoIncreaseIdTemplate : idTemplate);
            code.append(String.format(template, column.getColumn(), column.getPropertyClass4Short(),
                    column.getPropertyName())
            );
        });
    }

    /**
     * 扩展lombok
     *
     * @param extraImportClasses 扩展lombok
     * @param code               代码
     */
    private void appendLombok(List<String> extraImportClasses, StringBuilder code) {
        // 支持lombok，只需要加注解
        code.append("@Data\n");

        // 所有参数的构造注解
        if (extraImportClasses.contains(CLASS_LOMBOK_ALL_ARGS_CONSTRUCTOR)) {
            code.append("@AllArgsConstructor\n");
        }

        // 无参构造函数
        if (extraImportClasses.contains(CLASS_LOMBOK_NO_ARGS_CONSTRUCTOR)) {
            code.append("@NoArgsConstructor\n");
        }
    }
}
