package tech.ibit.mybatis.generator.impl;

import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.ProjectInfo;
import tech.ibit.mybatis.generator.table.ColumnInfo;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Mapper java代码生成器
 *
 * @author IBIT TECH
 */
public class MapperJavaGenerator extends AbstractGenerator {


    /**
     * 单个主键
     */
    private static final String CLASS_SINGLE_ID_MAPPER = "tech.ibit.mybatis.SingleIdMapper";

    /**
     * 单个主键
     */
    private static final String CLASS_NAME_SINGLE_ID_MAPPER = "SingleIdMapper";

    /**
     * 多个主键
     */
    private static final String CLASS_NO_ID_MAPPER = "tech.ibit.mybatis.NoIdMapper";

    /**
     * 多个主键
     */
    private static final String CLASS_NAME_NO_ID_MAPPER = "NoIdMapper";

    /**
     * 无主键
     */
    private static final String CLASS_MULTI_ID_MAPPER = "tech.ibit.mybatis.MultipleIdMapper";

    /**
     * 无主键
     */
    private static final String CLASS_NAME_MULTI_ID_MAPPER = "MultipleIdMapper";

    /**
     * 生成文件
     *
     * @param tableInfo     表
     * @param mapperProject mapper项目信息
     * @param entityProject 实体项目信息
     * @param override      是否覆盖
     * @param author        作者
     */
    public void generateFile(TableInfo tableInfo, ProjectInfo mapperProject, ProjectInfo entityProject
            , boolean override, String author) {
        try {
            String filePath = Utils.getMapperClassFilePath(tableInfo.getTable(), mapperProject.getBasePackage()
                    , mapperProject.getProjectDir());

            List<ColumnInfo> ids = tableInfo.getIds();

            File file = new File(filePath);
            if (needCreateNewFile(file, override)) {

                String table = tableInfo.getTable();

                List<String> importClasses = new ArrayList<>(getImportClasses(ids.size(), table, entityProject));
                Collections.sort(importClasses);

                StringBuilder mapperCode = new StringBuilder();
                mapperCode.append(String.format("package %s;\n\n", Utils.getMapperPackage(mapperProject.getBasePackage())));
                importClasses.forEach(importClass -> mapperCode.append(String.format("import %s;\n", importClass)));

                mapperCode.append("\n");

                String blank = "    ";

                String titleTemplate = "/**\n"
                        + SPACE + "* Mapper for %s\n"
                        + SPACE + "*\n"
                        + SPACE + "* @author %s\n"
                        + SPACE + "*/\n";
                mapperCode.append(String.format(titleTemplate, table, StringUtils.trimToEmpty(author)));

                String entityClass4Short = Utils.getEntityClassName4Short(table);
                String mapperClass4Short = Utils.getMapperClassName4Short(table);
                mapperCode.append(String.format("public interface %s extends %s {\n\n", mapperClass4Short, getExtendMapper(ids, table)));

                mapperCode.append(
                        "    /**\n" +
                        "     * 获取实体类型\n" +
                        "     *\n" +
                        "     * @return 实体类型\n" +
                        "     */\n"
                );
                mapperCode.append(blank).append("@Override\n")
                        .append(blank).append(String.format("default Class<%s> getPoClazz() {\n", entityClass4Short))
                        .append(blank).append(blank).append(String.format("return %s.class;\n", entityClass4Short))
                        .append(blank).append("}\n\n");

                mapperCode.append("}\n");

                generateFile(file, mapperCode.toString(), override);
                System.out.println("Generate java mapper file: " + file.getPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取导入类
     *
     * @param idSize        id长度
     * @param table         表名
     * @param entityProject 实体路径
     * @return 导入类
     */
    private List<String> getImportClasses(int idSize, String table, ProjectInfo entityProject) {
        if (idSize == 0) {
            return Arrays.asList(CLASS_NO_ID_MAPPER, Utils.getEntityClassName(table, entityProject.getBasePackage()));
        }

        if (idSize == 1) {
            return Arrays.asList(CLASS_SINGLE_ID_MAPPER,
                    Utils.getEntityClassName(table, entityProject.getBasePackage()));
        }

        return Arrays.asList(CLASS_MULTI_ID_MAPPER,
                Utils.getEntityClassName(table, entityProject.getBasePackage()),
                Utils.getEntityIdClassName(table, entityProject.getBasePackage()));
    }

    /**
     * 获取扩展的mapper名称
     *
     * @param ids   主键
     * @param table 表名
     * @return 字符串
     */
    private String getExtendMapper(List<ColumnInfo> ids, String table) {

        String entityClass4Short = Utils.getEntityClassName4Short(table);

        if (ids.size() == 0) {
            return CLASS_NAME_NO_ID_MAPPER + "<" + entityClass4Short + ">";
        }

        if (ids.size() == 1) {
            return CLASS_NAME_SINGLE_ID_MAPPER + "<" + entityClass4Short + ", " + ids.get(0).getPropertyClass4Short() + ">";
        }

        return CLASS_NAME_MULTI_ID_MAPPER + "<" + entityClass4Short + ", " + Utils.getEntityIdClassName4Short(table) + ">";
    }
}
