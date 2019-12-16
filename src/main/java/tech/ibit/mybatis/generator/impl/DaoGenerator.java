package tech.ibit.mybatis.generator.impl;

import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.ProjectInfo;
import tech.ibit.mybatis.generator.table.ColumnInfo;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Dao代码生成器
 *
 * @author IBIT TECH
 */
public class DaoGenerator extends AbstractGenerator {

    /**
     * NoIdDao全路径
     */
    private static final String CLASS_NO_ID_DAO = "tech.ibit.mybatis.template.dao.NoIdDao";

    /**
     * NoIdDao
     */
    private static final String NO_ID_DAO_NAME = "NoIdDao";

    /**
     * SingleIdDao全路径
     */
    private static final String CLASS_SINGLE_ID_DAO = "tech.ibit.mybatis.template.dao.SingleIdDao";

    /**
     * SingleIdDao
     */
    private static final String SINGLE_ID_DAO_NAME = "SingleIdDao";

    /**
     * MultipleIdDao全路径
     */
    private static final String CLASS_MULTIPLE_ID_DAO = "tech.ibit.mybatis.template.dao.MultipleIdDao";

    /**
     * MultipleIdDao
     */
    private static final String MULTIPLE_ID_DAO_NAME = "MultipleIdDao";

    /**
     * NoIdDaoImpl全路径
     */
    private static final String CLASS_NO_ID_DAO_IMPL = "tech.ibit.mybatis.template.dao.impl.NoIdDaoImpl";

    /**
     * NoIdDaoImpl
     */
    private static final String NO_ID_DAO_IMPL_NAME = "NoIdDaoImpl";

    /**
     * SingleIdDaoImpl全路径
     */
    private static final String CLASS_SINGLE_ID_DAO_IMPL = "tech.ibit.mybatis.template.dao.impl.SingleIdDaoImpl";

    /**
     * SingleIdDaoImpl
     */
    private static final String SINGLE_ID_DAO_IMPL_NAME = "SingleIdDaoImpl";

    /**
     * MultipleIdDaoImpl全路径
     */
    private static final String CLASS_MULTIPLE_ID_DAO_IMPL = "tech.ibit.mybatis.template.dao.impl.MultipleIdDaoImpl";

    /**
     * MultipleIdDaoImpl
     */
    private static final String MULTIPLE_ID_DAO_IMPL_NAME = "MultipleIdDaoImpl";

    /**
     * Autowired全路径
     */
    private static final String CLASS_ANNOTATION_AUTOWIRED = "org.springframework.beans.factory.annotation.Autowired";

    /**
     * Mapper全路径
     */
    private static final String CLASS_MAPPER = "tech.ibit.mybatis.template.mapper.Mapper";

    /**
     * Repository全路径
     */
    private static final String CLASS_REPOSITORY = "org.springframework.stereotype.Repository";

    /**
     * 生成文件
     *
     * @param tableInfo     表
     * @param daoProject    dao项目信息
     * @param mapperProject mapper项目信息
     * @param entityProject 实体项目信息
     * @param override      是否覆盖
     * @param author        作者
     */
    public void generateFile(TableInfo tableInfo, ProjectInfo daoProject, ProjectInfo mapperProject
            , ProjectInfo entityProject, boolean override, String author) {
        try {
            File daoFile = new File(Utils.getDaoClassFilePath(tableInfo.getTable(), daoProject.getBasePackage()
                    , daoProject.getProjectDir()));
            File daoImplFile = new File(Utils.getDaoImplClassFilePath(tableInfo.getTable(), daoProject.getBasePackage()
                    , daoProject.getProjectDir()));
            if (needCreateNewFile(daoFile, override)) {

                //dao
                String table = tableInfo.getTable();
                String entityClass4Short = Utils.getEntityClassName4Short(table);
                String daoClass4Short = Utils.getDaoClassName4Short(table);

                // 生成Dao
                generateDaoFile(daoFile, tableInfo, daoProject, entityProject
                        , override, entityClass4Short, daoClass4Short, author);

                // 生成Dao实现
                generateDaoImplFile(daoImplFile, tableInfo, daoProject, mapperProject, entityProject
                        , override, entityClass4Short, daoClass4Short, author);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成实现文件
     *
     * @param daoImplFile       dao实现文件
     * @param tableInfo         表
     * @param daoProject        dao项目信息
     * @param mapperProject     mapper项目信息
     * @param entityProject     实体项目信息
     * @param override          是否过期
     * @param entityClass4Short 实体类名缩写
     * @param daoClass4Short    dao类名缩写
     * @param author            作者
     */
    private void generateDaoImplFile(File daoImplFile, TableInfo tableInfo
            , ProjectInfo daoProject, ProjectInfo mapperProject, ProjectInfo entityProject
            , boolean override, String entityClass4Short, String daoClass4Short, String author) {

        String table = tableInfo.getTable();
        List<ColumnInfo> ids = tableInfo.getIds();
        String daoClassImpl4Short = Utils.getDaoImplClassName4Short(table);

        String daoInfImplClassName;
        String daoInfImplClassName4Short;
        String daoInfImplLine;
        List<String> importClasses = new ArrayList<>();
        if (ids.isEmpty()) {
            daoInfImplClassName = CLASS_NO_ID_DAO_IMPL;
            daoInfImplClassName4Short = NO_ID_DAO_IMPL_NAME;
            daoInfImplLine = String.format("public class %s extends %s<%s> implements %s {\n\n"
                    , daoClassImpl4Short, daoInfImplClassName4Short, entityClass4Short, daoClass4Short);
        } else if (ids.size() == 1) {
            daoInfImplClassName = CLASS_SINGLE_ID_DAO_IMPL;
            daoInfImplClassName4Short = SINGLE_ID_DAO_IMPL_NAME;
            daoInfImplLine = String.format("public class %s extends %s<%s, %s> implements %s {\n\n"
                    , daoClassImpl4Short, daoInfImplClassName4Short, entityClass4Short, ids.get(0).getPropertyClass4Short(), daoClass4Short);
            importClasses.add(ids.get(0).getPropertyClass());
        } else {
            daoInfImplClassName = CLASS_MULTIPLE_ID_DAO_IMPL;
            daoInfImplClassName4Short = MULTIPLE_ID_DAO_IMPL_NAME;
            daoInfImplLine = String.format("public class %s extends %s<%s, %s> implements %s {\n\n"
                    , daoClassImpl4Short, daoInfImplClassName4Short, entityClass4Short, Utils.getEntityIdClassName4Short(table), daoClass4Short);
            importClasses.add(Utils.getEntityIdClassName(table, entityProject.getBasePackage()));
        }

        importClasses.add(daoInfImplClassName);
        importClasses.add(Utils.getDaoClassName(table, daoProject.getBasePackage()));
        importClasses.add(Utils.getEntityClassName(table, entityProject.getBasePackage()));
        importClasses.add(Utils.getMapperClassName(table, mapperProject.getBasePackage()));
        importClasses.add(CLASS_ANNOTATION_AUTOWIRED);
        importClasses.add(CLASS_MAPPER);
        importClasses.add(CLASS_REPOSITORY);
        importClasses = getFinalImports(importClasses);

        StringBuilder daoImplCode = new StringBuilder();
        daoImplCode.append(String.format("package %s;\n\n", Utils.getDaoImplPackage(daoProject.getBasePackage())));
        importClasses.forEach(importClass -> daoImplCode.append(String.format("import %s;\n", importClass)));

        String blank = "    ";
        daoImplCode.append("\n");

        String titleTemplate = "/**\n"
                + SPACE + "* Dao for %s\n"
                + SPACE + "*\n"
                + SPACE + "* @author %s\n"
                + SPACE + "*/\n";
        daoImplCode.append(String.format(titleTemplate, table, StringUtils.trimToEmpty(author)));


        daoImplCode.append("@Repository\n");
        daoImplCode.append(daoInfImplLine);
        daoImplCode.append(blank).append("@Autowired\n")
                .append(blank).append(String.format("private %s mapper;\n", Utils.getMapperClassName4Short(table)))
                .append("\n")
                .append(blank).append("@Override\n")
                .append(blank).append(String.format("public Mapper<%s> getMapper() {\n", entityClass4Short))
                .append(blank).append(blank).append("return mapper;\n")
                .append(blank).append("}\n")
                .append("\n")
                .append(blank).append("@Override\n")
                .append(blank).append(String.format("public Class<%s> getPoClazz() {\n", entityClass4Short))
                .append(blank).append(blank).append(String.format("return %s.class;\n", entityClass4Short))
                .append(blank).append("}\n");
        daoImplCode.append("\n}\n");

        generateFile(daoImplFile, daoImplCode.toString(), override);
        System.out.println("Generate dao impl file: " + daoImplFile.getPath());
    }

    /**
     * 生成Dao文件
     *
     * @param daoFile           dao文件
     * @param tableInfo         表
     * @param daoProject        dao项目信息
     * @param entityProject     实体项目信息
     * @param override          是否覆盖
     * @param entityClass4Short 实体类名称
     * @param daoClass4Short    dao类名称
     * @param author            作者
     */
    private void generateDaoFile(File daoFile, TableInfo tableInfo, ProjectInfo daoProject, ProjectInfo entityProject
            , boolean override, String entityClass4Short, String daoClass4Short, String author) {

        String table = tableInfo.getTable();
        List<ColumnInfo> ids = tableInfo.getIds();

        String daoInfClassName;
        String daoInfClassName4Short;
        String daoInfLine;
        List<String> importClasses = new ArrayList<>();
        if (ids.isEmpty()) {
            daoInfClassName = CLASS_NO_ID_DAO;
            daoInfClassName4Short = NO_ID_DAO_NAME;
            daoInfLine = String.format("public interface %s extends %s<%s> {\n\n}\n", daoClass4Short, daoInfClassName4Short, entityClass4Short);
        } else if (ids.size() == 1) {
            daoInfClassName = CLASS_SINGLE_ID_DAO;
            daoInfClassName4Short = SINGLE_ID_DAO_NAME;
            daoInfLine = String.format("public interface %s extends %s<%s, %s> {\n\n}\n"
                    , daoClass4Short, daoInfClassName4Short, entityClass4Short, ids.get(0).getPropertyClass4Short());
            importClasses.add(ids.get(0).getPropertyClass());
        } else {
            daoInfClassName = CLASS_MULTIPLE_ID_DAO;
            daoInfClassName4Short = MULTIPLE_ID_DAO_NAME;
            daoInfLine = String.format("public interface %s extends %s<%s, %s> {\n\n}\n", daoClass4Short
                    , daoInfClassName4Short, entityClass4Short, Utils.getEntityIdClassName4Short(table));
            importClasses.add(Utils.getEntityIdClassName(table, entityProject.getBasePackage()));
        }

        importClasses.add(daoInfClassName);
        importClasses.add(Utils.getEntityClassName(table, entityProject.getBasePackage()));
        importClasses = getFinalImports(importClasses);

        StringBuilder daoCode = new StringBuilder();
        daoCode.append(String.format("package %s;\n\n", Utils.getDaoPackage(daoProject.getBasePackage())));
        importClasses.forEach(importClass -> daoCode.append(String.format("import %s;\n", importClass)));

        daoCode.append("\n");

        String titleTemplate = "/**\n"
                + SPACE + "* Dao for %s\n"
                + SPACE + "*\n"
                + SPACE + "* @author %s\n"
                + SPACE + "*/\n";
        daoCode.append(String.format(titleTemplate, table, StringUtils.trimToEmpty(author)));


        daoCode.append(daoInfLine);

        generateFile(daoFile, daoCode.toString(), override);
        System.out.println("Generate dao file: " + daoFile.getPath());
    }
}
