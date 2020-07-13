package tech.ibit.mybatis.generator.impl;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象生成器
 *
 * @author IBIT程序猿
 */
abstract class AbstractGenerator {

    /**
     * 定义缩紧，4个空格
     */
    static final String BLANK = "    ";

    /**
     * 空格
     */
    static final String SPACE = " ";

    /**
     * java.lang包前缀
     */
    private static final String PACKAGE_JAVA_LANG_PREFIX = "java.lang";

    /**
     * 生成文件
     *
     * @param file     文件
     * @param javaCode java编码
     * @param override 是否覆盖
     */
    void generateFile(File file, String javaCode, boolean override) {
        if (needCreateNewFile(file, override)) {
            createParentDirs(file);
            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.write(javaCode, out, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否需要生成新的文件
     *
     * @param file     文件
     * @param override 是否覆盖
     * @return 判断结果
     */
    boolean needCreateNewFile(File file, boolean override) {
        return !file.exists() || !file.isFile() || override;
    }


    /**
     * 生成父目录
     *
     * @param file 文件
     */
    void createParentDirs(File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            parentFile.mkdirs();
        }
    }

    /**
     * 获取最后导入的包（排除java.lang开头的，并排序）
     *
     * @param imports 导入包路径列表
     * @return 导入的包
     */
    List<String> getFinalImports(Collection<String> imports) {
        return imports.stream()
                .filter(importClazz -> !importClazz.startsWith(PACKAGE_JAVA_LANG_PREFIX))
                .sorted().collect(Collectors.toList());
    }

}
