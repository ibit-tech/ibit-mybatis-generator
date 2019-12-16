package tech.ibit.mybatis.generator.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import tech.ibit.mybatis.generator.ProjectInfo;
import tech.ibit.mybatis.generator.table.TableInfo;
import tech.ibit.mybatis.generator.util.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;


/**
 * Mapper xml代码生成器
 *
 * @author IBIT TECH
 */
public class MapperXmlGenerator extends AbstractGenerator {

    /**
     * 生成文件
     *
     * @param table         表
     * @param mapperProject mapper项目信息
     * @param entityProject 实体项目信息
     * @param override      是否覆盖
     */
    public void generateFile(TableInfo table, ProjectInfo mapperProject, ProjectInfo entityProject, boolean override) {
        try {

            String xmlFilePath = Utils.getMapperXmlFilePath(table.getTable(), mapperProject.getBasePackage()
                    , mapperProject.getProjectDir());
            File xmlFile = new File(xmlFilePath);
            if (needCreateNewFile(xmlFile, override)) {
                createParentDirs(xmlFile);

                String mapperClass = Utils.getMapperClassName(table.getTable(), mapperProject.getBasePackage());
                String entityClass = Utils.getEntityClassName(table.getTable(), entityProject.getBasePackage());

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.newDocument();
                doc.setXmlStandalone(true);

                //root node mapper
                Element rootElement = doc.createElement("mapper");
                doc.appendChild(rootElement);
                rootElement.setAttribute("namespace", mapperClass);

                //resultMap
                Element resultMap = doc.createElement("resultMap");
                resultMap.setAttribute("id", "resultMap");
                resultMap.setAttribute("type", entityClass);
                rootElement.appendChild(resultMap);

                table.getColumns().forEach(e -> {
                    String eleName = e.isId() ? "id" : "result";
                    Element ele = doc.createElement(eleName);
                    ele.setAttribute("column", e.getColumn());
                    ele.setAttribute("property", e.getPropertyName());
                    ele.setAttribute("jdbcType", e.getJdbcTypeName());
                    resultMap.appendChild(ele);
                });

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//mybatis.org//DTD Mapper 3.0//EN");
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(xmlFile);
                transformer.transform(source, result);

                System.out.println("Generate java mapper file: " + xmlFile.getPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
