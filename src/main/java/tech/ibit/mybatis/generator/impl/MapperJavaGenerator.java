package tech.ibit.mybatis.generator.impl;

import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.ProjectInfo;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Mapper java代码生成器
 *
 * @author IBIT TECH
 */
public class MapperJavaGenerator extends AbstractGenerator {


    /**
     * Mapper类路径
     */
    private static final String CLASS_MAPPER = "tech.ibit.mybatis.template.mapper.Mapper";

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

            File file = new File(filePath);
            if (needCreateNewFile(file, override)) {

                String table = tableInfo.getTable();

                List<String> importClasses = new ArrayList<>();
                importClasses.add(CLASS_MAPPER);
                importClasses.add(Utils.getEntityClassName(table, entityProject.getBasePackage()));
                Collections.sort(importClasses);

                StringBuilder mapperCode = new StringBuilder();
                mapperCode.append(String.format("package %s;\n\n", Utils.getMapperPackage(mapperProject.getBasePackage())));
                importClasses.forEach(importClass -> mapperCode.append(String.format("import %s;\n", importClass)));

                mapperCode.append("\n");

                String titleTemplate = "/**\n"
                        + SPACE + "* Mapper for %s\n"
                        + SPACE + "*\n"
                        + SPACE + "* @author %s\n"
                        + SPACE + "*/\n";
                mapperCode.append(String.format(titleTemplate, table, StringUtils.trimToEmpty(author)));

                String entityClass4Short = Utils.getEntityClassName4Short(table);
                String mapperClass4Short = Utils.getMapperClassName4Short(table);
                mapperCode.append(String.format("public interface %s extends Mapper<%s> {\n\n}\n", mapperClass4Short, entityClass4Short));

                generateFile(file, mapperCode.toString(), override);
                System.out.println("Generate java mapper file: " + file.getPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
