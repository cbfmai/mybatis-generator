/**
 * Copyright ${license.git.copyrightYears} the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class MybatisControllerPlugin extends PluginAdapter {
    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType serviceType;
    private FullyQualifiedJavaType controllerType;
    private FullyQualifiedJavaType autowired;

    private FullyQualifiedJavaType genericController;
    private FullyQualifiedJavaType genericService;
    private FullyQualifiedJavaType requestMapping;
    private FullyQualifiedJavaType restController;

    private String servicePack;
    private String controllerPack;
    private String project;
    private String pojoUrl;

    private boolean enableAnnotation = true;

    public MybatisControllerPlugin() {
        this.slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        this.slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
    }

    public boolean validate(List<String> warnings) {
        if (StringUtility.stringHasValue(this.properties.getProperty("enableAnnotation"))) {
            this.enableAnnotation = StringUtility.isTrue(this.properties.getProperty("enableAnnotation"));
        }

        this.controllerPack = this.properties.getProperty("targetPackage");
        this.project = this.properties.getProperty("targetProject");
        this.servicePack = this.properties.getProperty("targetServicePackage");

        this.pojoUrl = this.context.getJavaModelGeneratorConfiguration().getTargetPackage();
        if (this.enableAnnotation) {
            this.autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
            this.requestMapping = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping");
            this.restController = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RestController");
        }
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        this.controllerType = new FullyQualifiedJavaType(this.controllerPack + "." + tableName + "Controller");
        this.serviceType = new FullyQualifiedJavaType(this.servicePack + "." + tableName + "Service");
        this.genericController = new FullyQualifiedJavaType("GenericController");
        this.genericService = new FullyQualifiedJavaType("GenericService");

        TopLevelClass topLevelClass = new TopLevelClass(this.controllerType);
        addLogger(topLevelClass);
        addImport(topLevelClass);

        addControllerBody(topLevelClass, introspectedTable, tableName, files);

        return files;
    }

    protected void addControllerBody(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName,
                                     List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);


        FullyQualifiedJavaType generateKeyJavaType;
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns != null && primaryKeyColumns.size() > 0) {
            generateKeyJavaType = primaryKeyColumns.get(0).getFullyQualifiedJavaType();
        } else {
            generateKeyJavaType = new FullyQualifiedJavaType(Long.class.getName());
        }
        FullyQualifiedJavaType exampleJavaType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        genericController.addTypeArgument(parameterType);
        genericController.addTypeArgument(exampleJavaType);
        genericController.addTypeArgument(generateKeyJavaType);
        topLevelClass.addImportedType(parameterType);
        topLevelClass.addImportedType(exampleJavaType);

        genericService.addTypeArgument(parameterType);
        genericService.addTypeArgument(exampleJavaType);
        genericService.addTypeArgument(generateKeyJavaType);

        topLevelClass.setSuperClass(this.genericController);

        if (this.enableAnnotation) {
            topLevelClass.addAnnotation("@RestController");
            topLevelClass.addAnnotation("@RequestMapping(\"" + StringUtils.camelToUnderline(tableName) + "\")");
        }

        addServiceField(topLevelClass, tableName);

        topLevelClass.addMethod(getService(topLevelClass, introspectedTable, tableName));
        //topLevelClass.addMethod(generatePrimaryKey(topLevelClass, introspectedTable, tableName));

        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.project,
                this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        files.add(file);
    }

    private Method generatePrimaryKey(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.addAnnotation("@Override");
        FullyQualifiedJavaType generateKeyJavaType;
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns != null && primaryKeyColumns.size() > 0) {
            generateKeyJavaType = primaryKeyColumns.get(0).getFullyQualifiedJavaType();
        } else {
            generateKeyJavaType = new FullyQualifiedJavaType(Long.class.getName());
        }
        topLevelClass.addImportedType(generateKeyJavaType);
        method.setName("generatePrimaryKey");
        method.setReturnType(generateKeyJavaType);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "pk"));
        List<IntrospectedColumn> keyColumns = introspectedTable.getPrimaryKeyColumns();
        StringBuilder sb = new StringBuilder();

        if (keyColumns != null && keyColumns.size() > 0) {
            IntrospectedColumn keyColumn = primaryKeyColumns.get(0);
            if (keyColumn.isStringColumn()) {
                sb.append("return pk;");
            } else if ("BIGINT".equals(keyColumn.getJdbcTypeName())) {
                sb.append("return Long.valueOf(pk);");
            } else if ("DOUBLE".equals(keyColumn.getJdbcTypeName())) {
                sb.append("return Double.valueOf(pk);");
            }
        }
        method.addBodyLine(sb.toString());
        return method;
    }

    private Method getService(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("getService");
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setReturnType(genericService);

        StringBuilder sb = new StringBuilder();
        sb.append("return " + toLowerCase(this.serviceType.getShortName()) + ";");
        method.addBodyLine(sb.toString());
        method.addAnnotation("@Override");
        return method;
    }

    protected void addServiceField(TopLevelClass topLevelClass, String tableName) {
        Field field = new Field();
        field.setName(toLowerCase(this.serviceType.getShortName()));
        topLevelClass.addImportedType(this.serviceType);
        field.setType(this.serviceType);
        field.setVisibility(JavaVisibility.PRIVATE);
        if (this.enableAnnotation) {
            field.addAnnotation("@Autowired");
        }
        topLevelClass.addField(field);
    }


    protected String toLowerCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    protected String toUpperCase(String tableName) {
        StringBuilder sb = new StringBuilder(tableName);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private void addImport(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.zteict.common.controller.GenericController"));
        topLevelClass.addImportedType(this.serviceType);
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.zteict.common.service.GenericService"));
        topLevelClass.addImportedType(this.requestMapping);
        topLevelClass.addImportedType(this.restController);
        topLevelClass.addImportedType(this.autowired);
        topLevelClass.addImportedType(this.slf4jLogger);
        topLevelClass.addImportedType(this.slf4jLoggerFactory);

        if (this.enableAnnotation) {
            topLevelClass.addImportedType(this.autowired);
        }
    }

    private void addLogger(TopLevelClass topLevelClass) {
        Field field = new Field();
        field.setInitializationString("LoggerFactory.getLogger(" + topLevelClass.getType().getShortName() + ".class)");
        field.setName("LOGGER");
        field.setFinal(true);
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("Logger"));
        field.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(field);
    }
}
