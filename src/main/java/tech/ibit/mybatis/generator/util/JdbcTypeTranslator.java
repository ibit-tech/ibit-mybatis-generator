package tech.ibit.mybatis.generator.util;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author IBIT程序猿
 */
public class JdbcTypeTranslator {
    
    public static String getJavaClassName(int jdbcType) {
        return type2JavaClassNames.get(jdbcType);
    }
    
    public static String getJdbcTypeName(int jdbcType) {
        return type2Name.get(jdbcType);
    }

    private static Map<Integer, String> type2JavaClassNames;
    private static Map<Integer, String> type2Name;
    
    static {
        type2JavaClassNames = new HashMap<>();
        type2JavaClassNames.put(Types.ARRAY, Object.class.getName());
        type2JavaClassNames.put(Types.BIGINT, Long.class.getName());
        type2JavaClassNames.put(Types.BINARY, "byte[]");
        type2JavaClassNames.put(Types.BIT, Boolean.class.getName());
        type2JavaClassNames.put(Types.BLOB, "byte[]");
        type2JavaClassNames.put(Types.BOOLEAN, Boolean.class.getName());
        type2JavaClassNames.put(Types.CHAR, String.class.getName());
        type2JavaClassNames.put(Types.CLOB, String.class.getName());
        type2JavaClassNames.put(Types.DATALINK, Object.class.getName());
        type2JavaClassNames.put(Types.DATE, java.util.Date.class.getName());
        type2JavaClassNames.put(Types.DECIMAL, BigDecimal.class.getName());
        type2JavaClassNames.put(Types.DISTINCT, Object.class.getName());
        type2JavaClassNames.put(Types.DOUBLE, Double.class.getName());
        type2JavaClassNames.put(Types.FLOAT, Double.class.getName());
        type2JavaClassNames.put(Types.INTEGER, Integer.class.getName());
        type2JavaClassNames.put(Types.JAVA_OBJECT, Object.class.getName());
        type2JavaClassNames.put(Types.LONGNVARCHAR, String.class.getName());
        type2JavaClassNames.put(Types.LONGVARCHAR, String.class.getName());
        type2JavaClassNames.put(Types.NCHAR, String.class.getName());
        type2JavaClassNames.put(Types.NCLOB, String.class.getName());
        type2JavaClassNames.put(Types.NVARCHAR, String.class.getName());
        type2JavaClassNames.put(Types.NULL, Object.class.getName());
        type2JavaClassNames.put(Types.NUMERIC, BigDecimal.class.getName());
        type2JavaClassNames.put(Types.OTHER, Object.class.getName());
        type2JavaClassNames.put(Types.REAL, Float.class.getName());
        type2JavaClassNames.put(Types.REF, Object.class.getName());
        type2JavaClassNames.put(Types.SMALLINT, Short.class.getName());
        type2JavaClassNames.put(Types.STRUCT, Object.class.getName());
        type2JavaClassNames.put(Types.TIME, java.util.Date.class.getName());
        type2JavaClassNames.put(Types.TIMESTAMP, java.util.Date.class.getName());
        type2JavaClassNames.put(Types.TINYINT, Byte.class.getName());
        type2JavaClassNames.put(Types.VARBINARY, "byte[]");
        type2JavaClassNames.put(Types.VARCHAR, String.class.getName());

        type2Name = new HashMap<>();
        type2Name.put(Types.ARRAY, "ARRAY");
        type2Name.put(Types.BIGINT, "BIGINT");
        type2Name.put(Types.BINARY, "BINARY");
        type2Name.put(Types.BIT, "BIT");
        type2Name.put(Types.BLOB, "BLOB");
        type2Name.put(Types.BOOLEAN, "BOOLEAN");
        type2Name.put(Types.CHAR, "CHAR");
        type2Name.put(Types.CLOB, "CLOB");
        type2Name.put(Types.DATALINK, "DATALINK");
        type2Name.put(Types.DATE, "DATE");
        type2Name.put(Types.DECIMAL, "DECIMAL");
        type2Name.put(Types.DISTINCT, "DISTINCT");
        type2Name.put(Types.DOUBLE, "DOUBLE");
        type2Name.put(Types.FLOAT, "FLOAT");
        type2Name.put(Types.INTEGER, "INTEGER");
        type2Name.put(Types.JAVA_OBJECT, "JAVA_OBJECT");
        type2Name.put(Types.LONGVARBINARY, "LONGVARBINARY");
        type2Name.put(Types.LONGVARCHAR, "LONGVARCHAR");
        type2Name.put(Types.NCHAR, "NCHAR");
        type2Name.put(Types.NCLOB, "NCLOB");
        type2Name.put(Types.NVARCHAR, "NVARCHAR");
        type2Name.put(Types.LONGNVARCHAR, "LONGNVARCHAR");
        type2Name.put(Types.NULL, "NULL");
        type2Name.put(Types.NUMERIC, "NUMERIC");
        type2Name.put(Types.OTHER, "OTHER");
        type2Name.put(Types.REAL, "REAL");
        type2Name.put(Types.REF, "REF");
        type2Name.put(Types.SMALLINT, "SMALLINT");
        type2Name.put(Types.STRUCT, "STRUCT");
        type2Name.put(Types.TIME, "TIME");
        type2Name.put(Types.TIMESTAMP, "TIMESTAMP");
        type2Name.put(Types.TINYINT, "TINYINT");
        type2Name.put(Types.VARBINARY, "VARBINARY");
        type2Name.put(Types.VARCHAR, "VARCHAR");
    }
}
