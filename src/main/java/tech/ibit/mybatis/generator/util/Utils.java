package tech.ibit.mybatis.generator.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * 工具类
 *
 * @author IBIT程序猿
 */
public class Utils {

    private Utils() {
    }

    /**
     * Get entity class name for short by table name
     *
     * @param table table name
     * @return Class name for short
     */
    public static String getEntityClassName4Short(String table) {
        String[] nameParts = table.toLowerCase().split("_");
        StringBuilder className = new StringBuilder();
        for (String namePart : nameParts) {
            if (!StringUtils.isBlank(namePart)) {
                className.append(namePart.substring(0, 1).toUpperCase()).append(namePart.substring(1));
            }
        }
        return className.toString();
    }

    /**
     * Get entity class name by table name
     *
     * @param table       table name
     * @param basePackage base package
     * @return Class name
     */
    public static String getEntityName(String table, String basePackage) {
        return getClassName(table, "", basePackage, "entity");
    }

    /**
     * Get entity class package
     *
     * @param basePackage base package
     * @return package
     */
    public static String getEntityPackage(String basePackage) {
        return getPackage(basePackage, "entity");
    }

    /**
     * Get entity class file path
     *
     * @param table       table name
     * @param basePackage base package
     * @param projectDir  project directory
     * @return Class name file path
     */
    public static String getEntityFilePath(String table, String basePackage, String projectDir) {
        return getClassFilePath(table, "", basePackage, "entity", projectDir);
    }

    /**
     * Get table class name for short by table name
     *
     * @param table table name
     * @return Class name for short
     */
    public static String getPropertyClassName4Short(String table) {
        return getClassName4Short(table, "Properties");
    }

    /**
     * Get table class name by table name
     *
     * @param table       table name
     * @param basePackage base package
     * @return Class name
     */
    public static String getPropertyClassName(String table, String basePackage) {
        return getClassName(table, "Properties", basePackage, "entity.property");
    }

    /**
     * Get table class package
     *
     * @param basePackage base package
     * @return package
     */
    public static String getPropertyPackage(String basePackage) {
        return getPackage(basePackage, "entity.property");
    }

    /**
     * Get table class file path
     *
     * @param table       table name
     * @param basePackage base package
     * @param projectDir  project directory
     * @return Class file path
     */
    public static String getPropertyFilePath(String table, String basePackage, String projectDir) {
        return getClassFilePath(table, "Properties", basePackage, "entity.property", projectDir);
    }

    /**
     * Get table primary key class name for short by table name
     *
     * @param table table name
     * @return Class name for short
     */
    public static String getEntityIdClassName4Short(String table) {
        return getClassName4Short(table, "Key");
    }

    /**
     * Get table primary key class name by table name
     *
     * @param table       table name
     * @param basePackage base package
     * @return Class name
     */
    public static String getEntityIdClassName(String table, String basePackage) {
        return getClassName(table, "Key", basePackage, "entity");
    }

    /**
     * Get table primary key class package
     *
     * @param basePackage base package
     * @return package
     */
    public static String getEntityIdPackage(String basePackage) {
        return getPackage(basePackage, "entity");
    }

    /**
     * Get table primary key class file path
     *
     * @param table       table name
     * @param basePackage base package
     * @param projectDir  project directory
     * @return Class file path
     */
    public static String getEntityIdFilePath(String table, String basePackage, String projectDir) {
        return getClassFilePath(table, "Key", basePackage, "entity", projectDir);
    }

    /**
     * Get package
     *
     * @param basePackage base package
     * @param subPackage  sub package
     * @return package
     */
    public static String getPackage(String basePackage, String subPackage) {
        return StringUtils.isBlank(basePackage) ? subPackage : (basePackage + "." + subPackage);
    }

    /**
     * Get class name for short of class
     *
     * @param clazzName Name fo Class
     * @return name for short
     */
    public static String getClassName4ShortFromClassName(String clazzName) {
        int index = clazzName.lastIndexOf('.');
        return index > 0 ? clazzName.substring(index + 1) : clazzName;
    }


    /**
     * Get mapper class name for short by table name
     *
     * @param table table name
     * @return Class name for short
     */
    public static String getMapperClassName4Short(String table) {
        return getClassName4Short(table, "Mapper");
    }

    /**
     * Get mapper class name by table name
     *
     * @param table       table name
     * @param basePackage base package
     * @return Class name
     */
    public static String getMapperClassName(String table, String basePackage) {
        return getClassName(table, "Mapper", basePackage, "mapper");
    }

    /**
     * Get mapper class file path
     *
     * @param table       table name
     * @param basePackage base package
     * @param projectDir  project directory
     * @return Class file path
     */
    public static String getMapperClassFilePath(String table, String basePackage, String projectDir) {
        return getClassFilePath(table, "Mapper", basePackage, "mapper", projectDir);
    }

    /**
     * Get mapper class package
     *
     * @param basePackage base package
     * @return package
     */
    public static String getMapperPackage(String basePackage) {
        return getPackage(basePackage, "mapper");
    }


    /**
     * Get entity class name by table name
     *
     * @param table       table name
     * @param basePackage base package
     * @return Class name
     */
    public static String getEntityClassName(String table, String basePackage) {
        return getClassName(table, "", basePackage, "entity");
    }

    /**
     * Get package directory
     *
     * @param basePackage base package name
     * @param projectDir  project directory
     * @return directory
     */
    public static String getPackageDir(String basePackage, String projectDir) {
        File pomFile = new File(projectDir + "/pom.xml");
        if (pomFile.exists()) {
            return projectDir + "/src/main/java/" + getBasePackageDir(basePackage);
        }
        return projectDir + "/src/" + getBasePackageDir(basePackage);
    }

    private static String getBasePackageDir(String basePackage) {
        return StringUtils.isBlank(basePackage) ? "" : basePackage.replace('.', '/');
    }


    private static String getClassName4Short(String table, String suffix) {
        return getEntityClassName4Short(table) + suffix;
    }

    private static String getClassName(String table, String suffix, String basePackage, String subPackage) {
        return getPackage(basePackage, subPackage) + "." + getClassName4Short(table, suffix);
    }

    private static String getClassFilePath(String table, String suffix, String basePackage
            , String subPackage, String projectDir) {
        return getPackageDir(getPackage(basePackage, subPackage), projectDir) + "/"
                + getClassName4Short(table, suffix) + ".java";
    }


}
