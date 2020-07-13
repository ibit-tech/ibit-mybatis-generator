package tech.ibit.mybatis.generator.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.ProjectInfo;
import tech.ibit.mybatis.generator.table.ColumnInfo;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;
import tech.ibit.mybatis.utils.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Mapper java代码生成器
 *
 * @author IBIT程序猿
 */
public class MapperJavaGenerator extends AbstractGenerator {

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
            String filePath = Utils.getMapperClassFilePath(
                    tableInfo.getTable(), mapperProject.getBasePackage(), mapperProject.getProjectDir());

            File file = new File(filePath);
            if (needCreateNewFile(file, override)) {

                int idSize = tableInfo.getIdNames().size();

                String mapperCode;

                if (0 == idSize) {
                    mapperCode = getNoIdMapperCode(tableInfo, mapperProject, entityProject, author);
                } else if (1 == idSize) {
                    mapperCode = getSingleIdMapperCode(tableInfo, mapperProject, entityProject, author);
                } else {
                    mapperCode = getMultipleIdMapperCode(tableInfo, mapperProject, entityProject, author);
                }

                generateFile(file, mapperCode, override);
                System.out.println("Generate java mapper file: " + file.getPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 单主键
    private String getMultipleIdMapperCode(TableInfo tableInfo
            , ProjectInfo mapperProject, ProjectInfo entityProject, String author) throws IOException {

        Map<String, String> varMap = new HashMap<>(10);

        String table = tableInfo.getTable();

        // 公共参数
        addCommonVars(table, mapperProject, author, varMap);

        // 差异参数，imports
        List<String> imports = getCommonImportClasses(table, entityProject);
        imports.add("tech.ibit.mybatis.MultipleIdMapper");

        // 主键类
        imports.add(Utils.getEntityIdClassName(table, entityProject.getBasePackage()));

        Collections.sort(imports);

        varMap.put("imports", getImportString(imports));
        varMap.put("idName", Utils.getEntityIdClassName4Short(table));

        return getMapperCode("template/MultipleIdMapper.template", varMap);

    }


    // 单主键
    private String getSingleIdMapperCode(TableInfo tableInfo
            , ProjectInfo mapperProject, ProjectInfo entityProject, String author) throws IOException {

        Map<String, String> varMap = new HashMap<>(10);

        String table = tableInfo.getTable();

        // 公共参数
        addCommonVars(table, mapperProject, author, varMap);

        // 差异参数，imports
        List<String> imports = getCommonImportClasses(table, entityProject);
        imports.add("tech.ibit.mybatis.SingleIdMapper");
        imports.add("tech.ibit.mybatis.sqlbuilder.Column");
        Collections.sort(imports);

        ColumnInfo id = tableInfo.getIds().get(0);
        varMap.put("imports", getImportString(imports));
        varMap.put("idName", id.getPropertyClass4Short());
        varMap.put("idColumn", id.getPropertyName());

        return getMapperCode("template/SingleIdMapper.template", varMap);

    }


    // 无主键
    private String getNoIdMapperCode(TableInfo tableInfo
            , ProjectInfo mapperProject, ProjectInfo entityProject, String author) throws IOException {

        Map<String, String> varMap = new HashMap<>(10);

        String table = tableInfo.getTable();

        // 公共参数
        addCommonVars(table, mapperProject, author, varMap);


        // 差异参数，imports
        List<String> imports = getCommonImportClasses(table, entityProject);
        imports.add("tech.ibit.mybatis.NoIdMapper");
        Collections.sort(imports);

        varMap.put("imports", getImportString(imports));

        return getMapperCode("template/NoIdMapper.template", varMap);

    }

    // 公共参数

    /**
     * 公共参数
     *
     * @param varMap        参数map
     * @param mapperProject mapper 项目路径
     * @param author        作者
     * @param table         表名
     */
    private void addCommonVars(String table, ProjectInfo mapperProject, String author, Map<String, String> varMap) {
        varMap.put("package", String.format("package %s;", Utils.getMapperPackage(mapperProject.getBasePackage())));
        varMap.put("tableName", table);
        varMap.put("author", StringUtils.trimToEmpty(author));
        varMap.put("entityName", Utils.getEntityClassName4Short(table));
        varMap.put("propertyName", Utils.getPropertyClassName4Short(table));
        varMap.put("mapperName", Utils.getMapperClassName4Short(table));
    }

    /**
     * 获取 mapperCode
     *
     * @param templatePath 模板路径
     * @param varMap       变量
     * @return mapperCode
     * @throws IOException IO异常
     */
    private String getMapperCode(String templatePath, Map<String, String> varMap) throws IOException {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(templatePath)) {
            String template = IOUtils.toString(in);
            for (Map.Entry<String, String> entry : varMap.entrySet()) {
                template = template.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            return template;
        }
    }

    /**
     * 获取import子串
     *
     * @param imports 引入类
     * @return import子串
     */
    private String getImportString(List<String> imports) {
        if (CollectionUtils.isEmpty(imports)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        imports.forEach(i -> sb.append(String.format("import %s;\n", i)));
        return sb.toString();
    }

    /**
     * 获取公共的引入类
     *
     * @param table         表名
     * @param entityProject 实体类路径
     * @return 引入类列表
     */
    private List<String> getCommonImportClasses(String table, ProjectInfo entityProject) {
        List<String> commonImports = new ArrayList<>();
        commonImports.add(Utils.getEntityClassName(table, entityProject.getBasePackage()));
        commonImports.add(Utils.getPropertyClassName(table, entityProject.getBasePackage()));
        commonImports.add("tech.ibit.mybatis.sqlbuilder.Table");
        return commonImports;
    }

}
