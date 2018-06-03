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

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

public class MybatisServicePlugin extends PluginAdapter {
    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType serviceType;
    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType interfaceType;
    private FullyQualifiedJavaType pojoType;
    private FullyQualifiedJavaType pojoCriteriaType;
    private FullyQualifiedJavaType pojoSubCriteriaType;
    private FullyQualifiedJavaType listType;
    private FullyQualifiedJavaType mapType;
    private FullyQualifiedJavaType stringUtilsType;
    private FullyQualifiedJavaType autowired;
    private FullyQualifiedJavaType service;
    private FullyQualifiedJavaType returnType;
    private FullyQualifiedJavaType pagerType;
    private String servicePack;
    private String serviceImplPack;
    private String project;
    private String pojoUrl;
    private List<Method> methods;
    private boolean enableAnnotation = true;
    private boolean enableInsert = false;
    private boolean enableInsertSelective = false;
    private boolean enableDeleteByPrimaryKey = false;
    private boolean enableDeleteByExample = false;
    private boolean enableUpdateByExample = false;
    private boolean enableUpdateByExampleSelective = false;
    private boolean enableUpdateByPrimaryKey = false;
    private boolean enableUpdateByPrimaryKeySelective = false;

    public MybatisServicePlugin() {
        this.slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        this.slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        this.methods = new ArrayList<Method>();
    }

    public boolean validate(List<String> warnings) {
        if (StringUtility.stringHasValue(this.properties.getProperty("enableAnnotation"))) {
            this.enableAnnotation = StringUtility.isTrue(this.properties.getProperty("enableAnnotation"));
        }
        String enableInsert = this.properties.getProperty("enableInsert");

        String enableUpdateByExampleSelective = this.properties.getProperty("enableUpdateByExampleSelective");

        String enableInsertSelective = this.properties.getProperty("enableInsertSelective");

        String enableUpdateByPrimaryKey = this.properties.getProperty("enableUpdateByPrimaryKey");

        String enableDeleteByPrimaryKey = this.properties.getProperty("enableDeleteByPrimaryKey");

        String enableDeleteByExample = this.properties.getProperty("enableDeleteByExample");

        String enableUpdateByPrimaryKeySelective = this.properties.getProperty("enableUpdateByPrimaryKeySelective");

        String enableUpdateByExample = this.properties.getProperty("enableUpdateByExample");
        if (StringUtility.stringHasValue(enableInsert)) {
            this.enableInsert = StringUtility.isTrue(enableInsert);
        }
        if (StringUtility.stringHasValue(enableUpdateByExampleSelective)) {
            this.enableUpdateByExampleSelective = StringUtility.isTrue(enableUpdateByExampleSelective);
        }
        if (StringUtility.stringHasValue(enableInsertSelective)) {
            this.enableInsertSelective = StringUtility.isTrue(enableInsertSelective);
        }
        if (StringUtility.stringHasValue(enableUpdateByPrimaryKey)) {
            this.enableUpdateByPrimaryKey = StringUtility.isTrue(enableUpdateByPrimaryKey);
        }
        if (StringUtility.stringHasValue(enableDeleteByPrimaryKey)) {
            this.enableDeleteByPrimaryKey = StringUtility.isTrue(enableDeleteByPrimaryKey);
        }
        if (StringUtility.stringHasValue(enableDeleteByExample)) {
            this.enableDeleteByExample = StringUtility.isTrue(enableDeleteByExample);
        }
        if (StringUtility.stringHasValue(enableUpdateByPrimaryKeySelective)) {
            this.enableUpdateByPrimaryKeySelective = StringUtility.isTrue(enableUpdateByPrimaryKeySelective);
        }
        if (StringUtility.stringHasValue(enableUpdateByExample)) {
            this.enableUpdateByExample = StringUtility.isTrue(enableUpdateByExample);
        }
        this.servicePack = this.properties.getProperty("targetPackage");
        this.serviceImplPack = this.properties.getProperty("implementationPackage");
        this.project = this.properties.getProperty("targetProject");

        this.pojoUrl = this.context.getJavaModelGeneratorConfiguration().getTargetPackage();
        if (this.enableAnnotation) {
            this.autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
            this.service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        }
        return true;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        this.interfaceType = new FullyQualifiedJavaType(this.servicePack + "." + tableName + "Service");

        this.daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        this.serviceType = new FullyQualifiedJavaType(this.serviceImplPack + "." + tableName + "ServiceImpl");

        this.pojoType = new FullyQualifiedJavaType(this.pojoUrl + "." + tableName);

        this.pojoCriteriaType = new FullyQualifiedJavaType(this.pojoUrl + "." + tableName + "Criteria");
        this.pojoSubCriteriaType = new FullyQualifiedJavaType(this.pojoUrl + "." + tableName + "Criteria.Criteria");
        this.listType = new FullyQualifiedJavaType("java.util.List");
        this.mapType = new FullyQualifiedJavaType("java.util.Map");
        this.stringUtilsType = new FullyQualifiedJavaType("org.springframework.util.StringUtils");
        this.pagerType = new FullyQualifiedJavaType("com.mmtech.common.entity.Pager");
        Interface interface1 = new Interface(this.interfaceType);
        TopLevelClass topLevelClass = new TopLevelClass(this.serviceType);
        addLogger(topLevelClass);

        addImport(interface1, topLevelClass);

        addService(topLevelClass, interface1, introspectedTable, tableName, files);

        addServiceImpl(topLevelClass, introspectedTable, tableName, files);

        return files;
    }

    protected void addService(TopLevelClass topLevelClass, Interface interface1, IntrospectedTable introspectedTable,
                              String tableName, List<GeneratedJavaFile> files) {
        interface1.setVisibility(JavaVisibility.PUBLIC);

        Method method = addEntity(topLevelClass, introspectedTable, tableName);
        method.getBodyLines().clear();
        interface1.addMethod(method);

        method = deleteEntity(introspectedTable, tableName);
        method.getBodyLines().clear();
        interface1.addMethod(method);

        method = modifyEntity(topLevelClass, introspectedTable, tableName);
        method.getBodyLines().clear();
        interface1.addMethod(method);

        method = getEntity(introspectedTable, tableName);
        method.getBodyLines().clear();
        interface1.addMethod(method);

        method = getEntitys(introspectedTable, tableName);
        method.getBodyLines().clear();
        interface1.addMethod(method);

        method = getPagerEntitys(introspectedTable, tableName);
        method.getBodyLines().clear();
        interface1.addMethod(method);
        if (this.enableDeleteByPrimaryKey) {
            method = getOtherInteger("removeByPrimaryKey", "deleteByPrimaryKey", introspectedTable, tableName, 2);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableUpdateByPrimaryKeySelective) {
            method = getOtherInteger("saveByPrimaryKeySelective", "updateByPrimaryKeySelective", introspectedTable,
                    tableName, 1);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableUpdateByPrimaryKey) {
            method = getOtherInteger("saveByPrimaryKey", "updateByPrimaryKey", introspectedTable, tableName, 1);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableDeleteByExample) {
            method = getOtherInteger("removeByCriteria", "deleteByCriteria", introspectedTable, tableName, 3);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableUpdateByExampleSelective) {
            method = getOtherInteger("saveByCriteriaSelective", "updateByCriteriaSelective", introspectedTable,
                    tableName, 4);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableUpdateByExample) {
            method = getOtherInteger("saveByCriteria", "updateByCriteria", introspectedTable, tableName, 4);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableInsert) {
            method = getOtherInsertboolean("create", "insert", introspectedTable, tableName);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        if (this.enableInsertSelective) {
            method = getOtherInsertboolean("createSelective", "insertSelective", introspectedTable, tableName);
            method.getBodyLines().clear();
            interface1.addMethod(method);
        }
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, this.project,
                this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        files.add(file);
    }

    private Method getPagerEntitys(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("getPager" + tableName);
        method.setReturnType(new FullyQualifiedJavaType("Pager<" + tableName + ">"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Map<String, Object>"), "params"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addBodyLine(this.pojoCriteriaType.getShortName() + " criteria = new "
                + this.pojoCriteriaType.getShortName() + "();");
        method.addBodyLine("Pager<" + tableName + "> pager = new Pager<" + tableName + ">();");
        method.addBodyLine(this.pojoSubCriteriaType.getShortName() + " cri = criteria.createCriteria();");

        /*method.addBodyLine("if( params.containsKey(\"page\") ){");
        method.addBodyLine("pager.setCurrentPage(Integer.parseInt(params.get(\"page\").toString()));");
        method.addBodyLine("}");
        method.addBodyLine("if(params.containsKey(\"size\") ){");
        method.addBodyLine("pager.setSize(Integer.parseInt(params.get(\"size\").toString()));");
        method.addBodyLine("}");
        method.addBodyLine("int start = (pager.getCurrentPage()-1)*pager.getSize();");
        method.addBodyLine("criteria.setLimit(start);");
        method.addBodyLine("criteria.setOffset(pager.getSize());");*/

        method.addBodyLine("if (params.containsKey(\"offset\")) {\n" +
                "    int offset = Integer.parseInt(params.get(\"offset\").toString());\n" +
                "    int limit = Integer.parseInt(params.get(\"limit\").toString());\n" +
                "    pager.setCurrentPage(offset / limit + 1);\n" +
                "}\n" +
                "if (params.containsKey(\"limit\")) {\n" +
                "    pager.setSize(Integer.parseInt(params.get(\"limit\").toString()));\n" +
                "}");
        method.addBodyLine("setCriteria(cri, params);");
        method.addBodyLine("pager.setData(" + getDaoShort() + "selectByCondition(criteria));");
        method.addBodyLine("pager.setTotal(" + getDaoShort() + "countByCondition(criteria));");
        method.addBodyLine("pager.setTotalPage((int)Math.ceil((double)pager.getTotal()/pager.getSize()));");
        method.addBodyLine("return pager;");
        return method;
    }

    private Method getEntitys(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("get" + tableName + "s");
        method.setReturnType(new FullyQualifiedJavaType("List<" + tableName + ">"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Map<String,Object>"), "params"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addBodyLine(
                this.pojoCriteriaType.getShortName() + " criteria=new " + this.pojoCriteriaType.getShortName() + "();");
        method.addBodyLine("Criteria cri = criteria.createCriteria();");
        method.addBodyLine("setCriteria(cri, params);");

        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (((introspectedColumn.isJDBCDateColumn()) || (introspectedColumn.isJDBCTimeColumn()))
                    && ("createdTime".equals(introspectedColumn.getJavaProperty()))) {
                method.addBodyLine("if(params.containsKey(\"orderBy\")){");
                method.addBodyLine(
                        "criteria.setOrderByClause(params.get(\"orderBy\").toString().replaceAll(\"_\", \" \"));");
                method.addBodyLine("}else{");
                method.addBodyLine("criteria.setOrderByClause(\"createdTime desc\"); ");
                method.addBodyLine("}");
            }
        }
        method.addBodyLine(String.format("return this.%sselectByCondition(criteria);", new Object[]{getDaoShort()}));

        return method;
    }

    private Method getEntity(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("get" + tableName);
        method.setReturnType(this.pojoType);
        FullyQualifiedJavaType type;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return this.");
        sb.append(getDaoShort());
        sb.append("selectByPrimaryKey");
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    private Method modifyEntity(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("modify" + tableName);
        method.setReturnType(this.pojoType);
        method.addParameter(new Parameter(this.pojoType, "record"));
        method.setVisibility(JavaVisibility.PUBLIC);

        List<IntrospectedColumn> introspectedColumnsList = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumnsList) {
            if ("id".equalsIgnoreCase(introspectedColumn.getJavaProperty())) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.util.StringUtils"));
                method.addBodyLine("if(StringUtils.isEmpty(record.getId())){");
                method.addBodyLine("return this.add" + tableName + "(record);");
                method.addBodyLine("}");
            }
            if ("updatedTime".equalsIgnoreCase(introspectedColumn.getJavaProperty())) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Date"));
                method.addBodyLine("record.setUpdatedTime(new Date());");
            }
        }
        method.addBodyLine(String.format("if (this.%supdateByPrimaryKeySelective(record) == 1 ) {", new Object[]{getDaoShort()}));
        method.addBodyLine(" return record;");
        method.addBodyLine(" } else { ");
        method.addBodyLine(" return null;");
        method.addBodyLine(" }");
        return method;
    }

    private Method deleteEntity(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("delete" + tableName);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        String params = addParams(introspectedTable, method, 2);
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return this.");
        sb.append(getDaoShort());
        if (introspectedTable.hasBLOBColumns()) {
            sb.append("deleteByPrimaryKeyWithoutBLOBs");
        } else {
            sb.append("deleteByPrimaryKey");
        }
        sb.append("(");
        sb.append(params);
        sb.append(")==1;");
        method.addBodyLine(sb.toString());
        return method;
    }

    private Method addEntity(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("add" + tableName);
        method.setReturnType(this.pojoType);
        method.addParameter(new Parameter(this.pojoType, "record"));
        method.setVisibility(JavaVisibility.PUBLIC);
        List<IntrospectedColumn> introspectedColumnsList = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumnsList) {
            if ("id".equalsIgnoreCase(introspectedColumn.getJavaProperty())) {
                topLevelClass.addImportedType(
                        new FullyQualifiedJavaType("com.mmtech.common.util.SnowFlake"));
                method.addBodyLine("if(StringUtils.isEmpty(record.getId())){");
                method.addBodyLine("record.setId(SnowFlake.getId());");
                method.addBodyLine("}");
            }
            if ("createdTime".equalsIgnoreCase(introspectedColumn.getJavaProperty())) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Date"));
                method.addBodyLine("record.setCreatedTime(new Date(System.currentTimeMillis()));");
            }
            if ("updatedTime".equalsIgnoreCase(introspectedColumn.getJavaProperty())) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Date"));
                method.addBodyLine("record.setUpdatedTime(new Date( System.currentTimeMillis()));");
            }
        }
        method.addBodyLine(String.format("if ( this.%sinsert(record) == 1 ) {", new Object[]{getDaoShort()}));
        method.addBodyLine("return record;");
        method.addBodyLine("} else { ");
        method.addBodyLine(" return null;");
        method.addBodyLine("}");
        return method;
    }

    private Method setCriteria(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("setCriteria");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addParameter(new Parameter(this.pojoSubCriteriaType, "criteria"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Map<String,Object>"), "params"));
        method.setVisibility(JavaVisibility.PRIVATE);
        List<IntrospectedColumn> introspectedColumnsList = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumnsList) {
            String javaProperty = introspectedColumn.getJavaProperty();
            if (introspectedColumn.isStringColumn()) {
                method.addBodyLine(
                        "if(!StringUtils.isEmpty(params.getOrDefault(\"" + javaProperty + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty) + "EqualTo(params.get(\"" + javaProperty
                        + "\").toString().trim());");
                method.addBodyLine(
                        "if(!StringUtils.isEmpty(params.getOrDefault(\"like" + javaProperty + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty) + "Like(\"%\" + params.get(\"like"
                        + javaProperty + "\").toString().trim() + \"%\");");
            }
            if ("TIMESTAMP".equals(introspectedColumn.getJdbcTypeName())) {
                method.addBodyLine("if(!StringUtils.isEmpty(params.getOrDefault(\"greaterThan" + javaProperty
                        + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty)
                        + "GreaterThan(new Date(Long.parseLong(params.get(\"greaterThan" + javaProperty
                        + "\").toString()) ));");
                method.addBodyLine("if(!StringUtils.isEmpty(params.getOrDefault(\"lessThan" + javaProperty
                        + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty)
                        + "LessThan(new Date(Long.parseLong(params.get(\"lessThan" + javaProperty
                        + "\").toString()) ));");
            }
            if ("INTEGER".equals(introspectedColumn.getJdbcTypeName())) {
                method.addBodyLine(
                        "if(!StringUtils.isEmpty(params.getOrDefault(\"" + javaProperty + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty) + "EqualTo(Integer.parseInt(params.get(\""
                        + javaProperty + "\").toString()));");
                method.addBodyLine("if(!StringUtils.isEmpty(params.getOrDefault(\"greaterThan" + javaProperty
                        + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty)
                        + "GreaterThan(Integer.parseInt(params.get(\"greaterThan" + javaProperty + "\").toString()));");
                method.addBodyLine("if(!StringUtils.isEmpty(params.getOrDefault(\"lessThan" + javaProperty
                        + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty)
                        + "LessThan(Integer.parseInt(params.get(\"lessThan" + javaProperty + "\").toString()));");
            }
            if ("BIGINT".equals(introspectedColumn.getJdbcTypeName())) {
                method.addBodyLine(
                        "if(!StringUtils.isEmpty(params.getOrDefault(\"" + javaProperty + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty) + "EqualTo(Long.parseLong(params.get(\""
                        + javaProperty + "\").toString()));");
                method.addBodyLine("if(!StringUtils.isEmpty(params.getOrDefault(\"greaterThan" + javaProperty
                        + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty)
                        + "GreaterThan(Long.parseLong(params.get(\"greaterThan" + javaProperty + "\").toString()));");
                method.addBodyLine("if(!StringUtils.isEmpty(params.getOrDefault(\"lessThan" + javaProperty
                        + "\",\"\").toString()))");
                method.addBodyLine("criteria.and" + toUpperCase(javaProperty)
                        + "LessThan(Long.parseLong(params.get(\"lessThan" + javaProperty + "\").toString()));");
            }
        }
        return method;
    }

    protected void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName,
                                  List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        topLevelClass.addSuperInterface(this.interfaceType);
        if (this.enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(this.service);
        }
        addField(topLevelClass, tableName);

        topLevelClass.addMethod(setCriteria(topLevelClass, introspectedTable, tableName));
        topLevelClass.addMethod(addEntity(topLevelClass, introspectedTable, tableName));
        topLevelClass.addMethod(deleteEntity(introspectedTable, tableName));
        topLevelClass.addMethod(modifyEntity(topLevelClass, introspectedTable, tableName));
        topLevelClass.addMethod(getEntity(introspectedTable, tableName));
        topLevelClass.addMethod(getEntitys(introspectedTable, tableName));
        topLevelClass.addMethod(getPagerEntitys(introspectedTable, tableName));
        if (this.enableDeleteByPrimaryKey) {
            topLevelClass.addMethod(
                    getOtherInteger("removeByPrimaryKey", "deleteByPrimaryKey", introspectedTable, tableName, 2));
        }
        if (this.enableUpdateByPrimaryKeySelective) {
            topLevelClass.addMethod(getOtherInteger("saveByPrimaryKeySelective", "updateByPrimaryKeySelective",
                    introspectedTable, tableName, 1));
        }
        if (this.enableUpdateByPrimaryKey) {
            topLevelClass.addMethod(
                    getOtherInteger("saveByPrimaryKey", "updateByPrimaryKey", introspectedTable, tableName, 1));
        }
        if (this.enableDeleteByExample) {
            topLevelClass.addMethod(
                    getOtherInteger("removeByCriteria", "deleteByCriteria", introspectedTable, tableName, 3));
        }
        if (this.enableUpdateByExampleSelective) {
            topLevelClass.addMethod(getOtherInteger("saveByCriteriaSelective", "updateByCriteriaSelective",
                    introspectedTable, tableName, 4));
        }
        if (this.enableUpdateByExample) {
            topLevelClass
                    .addMethod(getOtherInteger("saveByCriteria", "updateByCriteria", introspectedTable, tableName, 4));
        }
        if (this.enableInsert) {
            topLevelClass.addMethod(getOtherInsertboolean("create", "insert", introspectedTable, tableName));
        }
        if (this.enableInsertSelective) {
            topLevelClass.addMethod(
                    getOtherInsertboolean("createSelective", "insertSelective", introspectedTable, tableName));
        }
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, this.project,
                this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        files.add(file);
    }

    protected void addField(TopLevelClass topLevelClass, String tableName) {
        Field field = new Field();
        field.setName(toLowerCase(this.daoType.getShortName()));
        topLevelClass.addImportedType(this.daoType);
        field.setType(this.daoType);
        field.setVisibility(JavaVisibility.PRIVATE);
        if (this.enableAnnotation) {
            field.addAnnotation("@Autowired");
        }
        topLevelClass.addField(field);
    }

    protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("selectByPrimaryKey");
        method.setReturnType(this.pojoType);
        FullyQualifiedJavaType type;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return this.");
        sb.append(getDaoShort());
        sb.append("selectByPrimaryKey");
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    protected Method countByExample(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("countByCriteria");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(this.pojoCriteriaType, "condition"));
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("int count = this.");
        sb.append(getDaoShort());
        sb.append("countByCriteria");
        sb.append("(");
        sb.append("condition");
        sb.append(");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("logger.debug(\"count: {}\", count);");
        method.addBodyLine("return count;");
        return method;
    }

    protected Method selectByExample(IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName("findByCriteria");
        method.setReturnType(new FullyQualifiedJavaType("List<" + tableName + ">"));
        method.addParameter(new Parameter(this.pojoCriteriaType, "condition"));
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return this.");
        sb.append(getDaoShort());
        if (introspectedTable.hasBLOBColumns()) {
            sb.append("selectByConditionWithoutBLOBs");
        } else {
            sb.append("selectByCondition");
        }
        sb.append("(");
        sb.append("condition");
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    protected Method getOtherInteger(String methodName, String daoName, IntrospectedTable introspectedTable,
                                     String tableName, int type) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        String params = addParams(introspectedTable, method, type);
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append("return this.");
        sb.append(getDaoShort());
        if ((introspectedTable.hasBLOBColumns()) && (!"saveByPrimaryKeySelective".equals(methodName))
                && (!"removeByPrimaryKey".equals(methodName)) && (!"removeByCriteria".equals(methodName))
                && (!"saveByCriteriaSelective".equals(methodName))) {
            sb.append(daoName + "WithoutBLOBs");
        } else {
            sb.append(daoName);
        }
        sb.append("(");
        sb.append(params);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    protected Method getOtherInsertboolean(String methodName, String daoName, IntrospectedTable introspectedTable,
                                           String tableName) {
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(this.returnType);
        method.addParameter(new Parameter(this.pojoType, "record"));
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        if (this.returnType == null) {
            sb.append("this.");
        } else {
            sb.append("return this.");
        }
        sb.append(getDaoShort());
        sb.append(daoName);
        sb.append("(");
        sb.append("record");
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
        switch (type1) {
            case 1:
                method.addParameter(new Parameter(this.pojoType, "record"));
                return "record";
            case 2:
                FullyQualifiedJavaType type;
                if (introspectedTable.getRules().generatePrimaryKeyClass()) {
                    type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
                    method.addParameter(new Parameter(type, "key"));
                } else {
                    for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                        type = introspectedColumn.getFullyQualifiedJavaType();
                        method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
                    }
                }
                StringBuffer sb = new StringBuffer();
                for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
                return sb.toString();
            case 3:
                method.addParameter(new Parameter(this.pojoCriteriaType, "condition"));
                return "condition";
            case 4:
                method.addParameter(0, new Parameter(this.pojoType, "record"));
                method.addParameter(1, new Parameter(this.pojoCriteriaType, "condition"));

                return "record, condition";
        }
        return null;
    }

    protected void addComment(JavaElement field, String comment) {
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        comment = comment.replaceAll("\n", "<br>\n\t * ");
        sb.append(comment);
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    protected void addField(TopLevelClass topLevelClass) {
        Field field = new Field();
        field.setName("success");
        field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        field.setVisibility(JavaVisibility.PRIVATE);
        addComment(field, "执行结果");
        topLevelClass.addField(field);

        field = new Field();
        field.setName("message");
        field.setType(FullyQualifiedJavaType.getStringInstance());
        field.setVisibility(JavaVisibility.PRIVATE);
        addComment(field, "消息结果");
        topLevelClass.addField(field);
    }

    protected void addMethod(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setSuccess");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "success"));
        method.addBodyLine("this.success = success;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        method.setName("isSuccess");
        method.addBodyLine("return success;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setMessage");
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "message"));
        method.addBodyLine("this.message = message;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("getMessage");
        method.addBodyLine("return message;");
        topLevelClass.addMethod(method);
    }

    protected void addMethod(TopLevelClass topLevelClass, String tableName) {
        Method method2 = new Method();
        for (int i = 0; i < this.methods.size(); i++) {
            Method method = new Method();
            method2 = (Method) this.methods.get(i);
            method = method2;
            method.getBodyLines().clear();
            method.getAnnotations().clear();
            StringBuilder sb = new StringBuilder();
            sb.append("return this.");
            sb.append(getDaoShort());
            sb.append(method.getName());
            sb.append("(");
            List<Parameter> list = method.getParameters();
            for (int j = 0; j < list.size(); j++) {
                sb.append(((Parameter) list.get(j)).getName());
                sb.append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append(");");
            method.addBodyLine(sb.toString());
            topLevelClass.addMethod(method);
        }
        this.methods.clear();
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

    private void addImport(Interface interfaces, TopLevelClass topLevelClass) {
        interfaces.addImportedType(this.pojoType);
        interfaces.addImportedType(this.listType);
        interfaces.addImportedType(this.mapType);
        interfaces.addImportedType(this.pagerType);
        topLevelClass.addImportedType(this.daoType);
        topLevelClass.addImportedType(this.interfaceType);
        topLevelClass.addImportedType(this.pojoType);
        topLevelClass.addImportedType(this.pojoCriteriaType);
        topLevelClass.addImportedType(this.pojoSubCriteriaType);
        topLevelClass.addImportedType(this.listType);
        topLevelClass.addImportedType(this.mapType);
        topLevelClass.addImportedType(this.slf4jLogger);
        topLevelClass.addImportedType(this.slf4jLoggerFactory);
        topLevelClass.addImportedType(this.stringUtilsType);
        topLevelClass.addImportedType(this.pagerType);
        if (this.enableAnnotation) {
            topLevelClass.addImportedType(this.service);
            topLevelClass.addImportedType(this.autowired);
        }
    }

    private void addLogger(TopLevelClass topLevelClass) {
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("LoggerFactory.getLogger(" + topLevelClass.getType().getShortName() + ".class)");
        field.setName("logger");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("Logger"));
        field.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(field);
    }

    private String getDaoShort() {
        return toLowerCase(this.daoType.getShortName()) + ".";
    }

    public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
                                               IntrospectedTable introspectedTable) {
        this.returnType = method.getReturnType();
        return true;
    }
}
