package tech.ibit.mybatis.generator;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.impl.*;
import tech.ibit.mybatis.generator.table.TableInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 代码生成器
 *
 * @author IBIT TECH
 */
public class Generator {

    /**
     * JDBC url
     */
    private String jdbcUrl;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * jdbc driver类名
     */
    private String driverName;

    /**
     * 生成表名
     */
    @Setter
    @Getter
    private String[] tables;

    /**
     * 默认项目信息
     */
    @Setter
    @Getter
    private ProjectInfo defaultProject;

    /**
     * 实体项目信息
     */
    @Setter
    @Getter
    private ProjectInfo entityProject;

    /**
     * Mapper项目信息
     */
    @Setter
    @Getter
    private ProjectInfo mapperProject;

    /**
     * Dao项目信息
     */
    @Setter
    @Getter
    private ProjectInfo daoProject;


    /**
     * 是否覆盖
     */
    @Setter
    @Getter
    private boolean override = true;

    /**
     * 是否生成实体
     */
    @Setter
    @Getter
    private boolean withEntity;

    /**
     * 是否生成Mapper
     */
    @Setter
    @Getter
    private boolean withMapper;

    /**
     * 是否生成Dao
     */
    @Setter
    @Getter
    private boolean withDao;

    /**
     * 表生成器
     */
    private PropertyGenerator propertyGenerator;

    /**
     * 实体生成器
     */
    private EntityGenerator entityGenerator;

    /**
     * Dao生成器
     */
    private DaoGenerator daoGenerator;

    /**
     * Mapper java代码生成器
     */
    private MapperJavaGenerator mapperJavaGenerator;

    /**
     * Mapper xml代码生成器
     */
    private MapperXmlGenerator mapperXmlGenerator;

    /**
     * 作者
     */
    @Setter
    @Getter
    private String author;


    /**
     * 构造函数
     *
     * @param driverName jdbc driver类名
     * @param jdbcUrl    jdbc url
     * @param username   用户名
     * @param password   密码
     * @param tableStr   表字符串，多个用","分割
     */
    public Generator(String driverName, String jdbcUrl, String username
            , String password, String tableStr) {
        this.driverName = driverName;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        tables = tableStr.split("\\s*,\\s*");

        propertyGenerator = new PropertyGenerator();
        entityGenerator = new EntityGenerator();
        daoGenerator = new DaoGenerator();
        mapperJavaGenerator = new MapperJavaGenerator();
        mapperXmlGenerator = new MapperXmlGenerator();
    }

    /**
     * 构造函数
     *
     * @param driverName  jdbc driver类名
     * @param jdbcUrl     jdbc url
     * @param username    用户名
     * @param password    密码
     * @param tableStr    表字符串，多个用","分割
     * @param projectDir  项目目录
     * @param basePackage 基础包路径
     */
    public Generator(String driverName, String jdbcUrl, String username
            , String password, String tableStr
            , String projectDir, String basePackage) {
        this(driverName, jdbcUrl, username, password, tableStr);
        defaultProject = new ProjectInfo(projectDir, basePackage);
    }

    /**
     * 获取非空默认项目信息
     *
     * @return 非空项目信息
     */
    private ProjectInfo getNotNullDefaultProject() {
        assert null != defaultProject;
        return defaultProject;
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * 是否需要生成文件
     *
     * @return 判断结果
     */
    private boolean needGenerateFiles() {
        return withEntity || withDao || withMapper;
    }

    /**
     * 生成文件
     */
    public void generateFiles() {

        if (!needGenerateFiles()) {
            return;
        }

        if (StringUtils.isBlank(jdbcUrl)) {
            System.err.println("Jdbc url not found!");
            return;
        }

        if (!withEntity && !withDao) {
            return;
        }

        try (Connection con = getConnection()) {

            for (String table : tables) {
                if (!StringUtils.isBlank(table)) {
                    try {

                        TableInfo tableInfo = getTTable(con, table);

                        ProjectInfo entityProject = getProjectInfoWithDefault(getEntityProject(), getNotNullDefaultProject());

                        // 生成表和实体
                        if (withEntity) {

                            propertyGenerator.generateFile(tableInfo, entityProject.getBasePackage()
                                    , entityProject.getProjectDir(), override, author);
                            entityGenerator.generateFile(tableInfo, entityProject,
                                    override, author);
                        }

                        // 生成mapper
                        if (withMapper) {
                            ProjectInfo mapperProject = getProjectInfoWithDefault(getMapperProject(), getNotNullDefaultProject());
                            mapperJavaGenerator.generateFile(tableInfo, mapperProject, entityProject, override, author);
                            mapperXmlGenerator.generateFile(tableInfo, mapperProject, entityProject, override);
                        }

                        // 生成Dao
                        if (withDao) {
                            ProjectInfo daoProject = getProjectInfoWithDefault(getDaoProject(), getNotNullDefaultProject());
                            ProjectInfo mapperProject = getProjectInfoWithDefault(getMapperProject(), getNotNullDefaultProject());
                            daoGenerator.generateFile(tableInfo, daoProject, mapperProject, entityProject, override, author);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private ProjectInfo getProjectInfoWithDefault(ProjectInfo project, ProjectInfo defaultProject) {
        return null == project ? defaultProject : project;
    }


    /**
     * 获取表信息
     *
     * @param conn  数据库连接
     * @param table 表名
     * @return 表信息
     * @throws SQLException
     */
    private TableInfo getTTable(Connection conn, String table) throws SQLException {
        TableInfo tableInfo = new TableInfo(table, getComment(conn, table));

        Set<String> ids = getIds(conn, table);
        List<String> columns = new ArrayList<>();
        try (ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), conn.getSchema(), table, "%")) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                tableInfo.addColumn(columnName, getColumnType(rs), rs.getInt("DATA_TYPE")
                        , ids.contains(columnName), getBoolean(rs.getString("IS_AUTOINCREMENT"))
                        , getBoolean(rs.getString("IS_NULLABLE")), formatComment(rs.getString("REMARKS")));
                columns.add(columnName);
            }
            return tableInfo;
        }
    }

    /**
     * 判断boolean
     *
     * @param value 值
     * @return 值
     */
    private boolean getBoolean(String value) {
        return "YES".equals(value);
    }

    /**
     * 获取列类型
     *
     * @param rs 结果集
     * @return 列类型
     * @throws SQLException SQL异常
     */
    private String getColumnType(ResultSet rs) throws SQLException {
        String typeName = rs.getString("TYPE_NAME");
        int size = rs.getInt("COLUMN_SIZE");
        if (null != rs.getObject("DECIMAL_DIGITS")) {
            int digits = rs.getInt("DECIMAL_DIGITS");
            return typeName + "(" + size + ", " + digits + ")";
        }
        return typeName + "(" + size + ")";
    }

    /**
     * 获取备注
     *
     * @param conn  连接
     * @param table 表
     * @return 备注
     * @throws SQLException SQL异常
     */
    private String getComment(Connection conn, String table) throws SQLException {

        try (ResultSet rs = conn.getMetaData().getTables(null, null, table, null)) {
            if (rs.next()) {
                return formatComment(rs.getString("REMARKS"));
            }
        }
        return "";
    }

    /**
     * 格式化备注
     *
     * @param comment 备注
     * @return 格式化后的备注
     */
    private String formatComment(String comment) {
        comment = StringUtils.trimToEmpty(comment);
        return comment.replace('\n', ' ');
    }

    /**
     * 获取主键
     *
     * @param conn  连接
     * @param table 表
     * @return 主键
     * @throws SQLException SQL异常
     */
    private Set<String> getIds(Connection conn, String table) throws SQLException {
        Set<String> primaryKeys = new LinkedHashSet<>();
        try (ResultSet pk = conn.getMetaData().getPrimaryKeys(null, null, table)) {
            while (pk.next()) {
                primaryKeys.add(pk.getString("COLUMN_NAME"));
            }
        }
        return primaryKeys;
    }

    /**
     * 设置生成全部
     */
    public void setWithAll() {
        withEntity = true;
        withDao = true;
        withMapper = true;
    }


}
