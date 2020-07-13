package tech.ibit.mybatis.generator;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 项目基本信息
 *
 * @author IBIT程序猿
 */
@Data
@AllArgsConstructor
public class ProjectInfo {

    /**
     * 项目目录
     */
    private String projectDir;

    /**
     * 基础包
     */
    private String basePackage;
}
