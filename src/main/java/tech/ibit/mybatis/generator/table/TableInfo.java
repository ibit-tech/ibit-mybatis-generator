package tech.ibit.mybatis.generator.table;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板表对象
 *
 * @author IBIT程序猿
 */
@Getter
public class TableInfo {

    /**
     * 分割符
     */
    public static final String SPLIT_CHAR = "_";

    /**
     * 包分割符
     */
    public static final String PACKAGE_SPLIT_CHAR = ".";

    /**
     * 表名
     */
    private String table;

    /**
     * 备注
     */
    private String comment;

    /**
     * 列列表
     */
    private List<ColumnInfo> columns;

    /**
     * 表别名
     */
    private String alias;

    /**
     * 构造函数
     *
     * @param table   表名
     * @param comment 备注
     */
    public TableInfo(String table, String comment) {
        this.table = table;
        this.comment = comment;
        alias = generateAlias(table);
        columns = new ArrayList<>();
    }

    /**
     * 增加列
     *
     * @param column     列名
     * @param jdbcType   JDBC类型
     * @param columnType 列类型
     */
    public void addColumn(String column, String columnType, int jdbcType) {
        columns.add(new ColumnInfo(column, columnType, jdbcType));
    }

    /**
     * 增加列
     *
     * @param column        列名
     * @param jdbcType      JDBC类型
     * @param columnType    列类型
     * @param isId          是否为id
     * @param autoIncrement 是否自增长
     * @param nullable      是否可为null
     * @param comment       备注
     */
    public void addColumn(String column, String columnType, int jdbcType, boolean isId
            , boolean autoIncrement, boolean nullable, String comment) {
        columns.add(new ColumnInfo(column, columnType, jdbcType, isId, autoIncrement, nullable, comment));
        columns.sort((c1, c2) -> {
            if (c1.isId() ^ c2.isId()) {
                return c1.isId() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
            return 0;
        });
    }

    /**
     * 获取引入类
     *
     * @return 引入类列表
     */
    public List<String> getImportClasses() {
        return getImportClasses(columns);
    }

    /**
     * 获取主键引入类
     *
     * @return 主键引入类列表
     */
    public List<String> getIdImportClasses() {
        return getImportClasses(columns.stream().filter(ColumnInfo::isId).collect(Collectors.toList()));
    }

    /**
     * 获取主键列表
     *
     * @return 主键列表
     */
    public List<ColumnInfo> getIds() {
        return columns.stream().filter(ColumnInfo::isId).collect(Collectors.toList());
    }

    /**
     * 获取列名称列表
     *
     * @return 列名称列表
     */
    public List<String> getColumnNames() {
        return columns.stream().map(ColumnInfo::getColumn).collect(Collectors.toList());
    }

    /**
     * 获取主键名称列表
     *
     * @return 主键名称列表
     */
    public List<String> getIdNames() {
        return columns.stream()
                .filter(ColumnInfo::isId)
                .map(ColumnInfo::getColumn)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 生成表表别名
     *
     * @param table 表名
     * @return 表别名
     */
    private String generateAlias(String table) {
        String[] nameParts = table.split(SPLIT_CHAR);
        StringBuilder alias = new StringBuilder();
        for (String namePart : nameParts) {
            if (namePart.length() > 1) {
                alias.append(namePart.charAt(0));
            }
        }
        return alias.toString();
    }

    /**
     * 通过列获取引入类列表
     *
     * @param tColumns 列列表
     * @return 引入类列表
     */
    private List<String> getImportClasses(List<? extends ColumnInfo> tColumns) {
        if (tColumns.isEmpty()) {
            return Collections.emptyList();
        }
        return tColumns
                .stream()
                .filter(e -> e.getPropertyClass().indexOf(PACKAGE_SPLIT_CHAR) > 0)
                .map(ColumnInfo::getPropertyClass).distinct().sorted()
                .collect(Collectors.toList());
    }
}
