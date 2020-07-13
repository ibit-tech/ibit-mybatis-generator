package tech.ibit.mybatis.generator.table;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import tech.ibit.mybatis.generator.util.JdbcTypeTranslater;
import tech.ibit.mybatis.generator.util.Utils;

import java.sql.Types;

/**
 * 模板列对象
 *
 * @author IBIT程序猿
 */
@Getter
public class ColumnInfo {

    public static final String SPLIT_CHAR = "_";
    /**
     * 属性类型（完整路径）
     */
    private String propertyClass;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     * 列名
     */
    private String column;

    /**
     * JDBC类型
     */
    private int jdbcType;

    /**
     * JDBC类型名称
     */
    private String jdbcTypeName;

    /**
     * 是否为主键
     */
    private boolean id;

    /**
     * 是否为自增长
     */
    private boolean autoIncrement;

    /**
     * 是否可为null
     */
    private boolean nullable;

    /**
     * 列备注
     */
    private String comment;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * 构造函数
     *
     * @param jdbcType   列JDBC类型
     * @param column     列名
     * @param columnType 列类型
     */
    public ColumnInfo(String column, String columnType, int jdbcType) {
        this(column, columnType, jdbcType, false, false);
    }

    /**
     * 构造函数
     *
     * @param column        列
     * @param columnType    列类型
     * @param jdbcType      列JDBC类型
     * @param id            是否为主键
     * @param autoIncrement 是否自增长
     */
    public ColumnInfo(String column, String columnType, int jdbcType, boolean id, boolean autoIncrement) {
        this(column, columnType, jdbcType, id, autoIncrement, false, "");
    }

    /**
     * 构造函数
     *
     * @param column        列
     * @param columnType    列类型
     * @param jdbcType      列JDBC类型
     * @param id            是否为主键
     * @param autoIncrement 是否自增长
     * @param nullable      是否可为null
     * @param comment       列备注
     */
    public ColumnInfo(String column, String columnType, int jdbcType, boolean id
            , boolean autoIncrement, boolean nullable, String comment) {
        this.column = column;
        this.jdbcType = jdbcType;
        this.columnType = columnType;
        this.id = id;
        this.autoIncrement = autoIncrement;
        this.nullable = nullable;
        this.comment = comment;

        jdbcTypeName = JdbcTypeTranslater.getJdbcTypeName(jdbcType);
        propertyClass = JdbcTypeTranslater.getJavaClassName(jdbcType);
        propertyName = getPropertyByColumn(column);
    }


    /**
     * 获取属性类名
     *
     * @return 属性类名
     */
    public String getPropertyClass4Short() {
        return Utils.getClassName4ShortFromClassName(propertyClass);
    }

    /**
     * 获取属性类Setter方法名
     *
     * @return 属性类Setter方法名
     */
    public String getPropertySetterMethod() {
        String property = getPropertyName();
        return "set" + property.substring(0, 1).toUpperCase() + property.substring(1);

    }

    /**
     * 获取属性类Getter方法名
     *
     * @return 属性类Getter方法
     */
    public String getPropertyGetterMethod() {
        String property = getPropertyName();
        String prefix = Types.BOOLEAN == jdbcType ? "is" : "get";
        return prefix + property.substring(0, 1).toUpperCase() + property.substring(1);
    }

    /**
     * 获取属性名
     *
     * @param column 列名
     * @return 属性名
     */
    private String getPropertyByColumn(String column) {
        String[] nameParts = column.toLowerCase().split(SPLIT_CHAR);
        StringBuilder propertyName = new StringBuilder();
        for (int i = 0; i < nameParts.length; i++) {
            String namePart = nameParts[i];
            if (!StringUtils.isBlank(namePart)) {
                if (i == 0) {
                    propertyName.append(namePart);
                } else {
                    propertyName.append(namePart.substring(0, 1).toUpperCase()).append(namePart.substring(1));
                }
            }
        }
        return propertyName.toString();
    }
}
