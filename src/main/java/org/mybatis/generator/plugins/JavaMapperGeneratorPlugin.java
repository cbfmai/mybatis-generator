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

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author Jeff Butler
 */
public class JavaMapperGeneratorPlugin extends JavaMapperGenerator {

    public JavaMapperGeneratorPlugin() {
        super(true);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        interfaze.addAnnotation("@Repository");

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        interfaze.addImportedType(parameterType);

        FullyQualifiedJavaType generateKeyJavaType;
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns != null && primaryKeyColumns.size() > 0) {
            generateKeyJavaType = primaryKeyColumns.get(0).getFullyQualifiedJavaType();
        } else {
            generateKeyJavaType = new FullyQualifiedJavaType(Long.class.getName());
        }
        FullyQualifiedJavaType exampleJavaType = new FullyQualifiedJavaType(introspectedTable.getExampleType());

        interfaze.addImportedType(new FullyQualifiedJavaType("com.zteict.common.dao.GenericMapper"));
        FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType("GenericMapper");
        daoSuperType.addTypeArgument(parameterType);
        daoSuperType.addTypeArgument(exampleJavaType);
        daoSuperType.addTypeArgument(generateKeyJavaType);

        interfaze.addImportedType(exampleJavaType);
        interfaze.addSuperInterface(daoSuperType);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }


        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null,
                introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    @Override
    protected void addCountByExampleMethod(Interface interfaze) {

    }

    @Override
    protected void addDeleteByExampleMethod(Interface interfaze) {

    }

    @Override
    protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {

    }

    @Override
    protected void addInsertMethod(Interface interfaze) {

    }

    @Override
    protected void addInsertSelectiveMethod(Interface interfaze) {

    }

    @Override
    protected void addSelectByExampleWithBLOBsMethod(Interface interfaze) {

    }

    @Override
    protected void addSelectByExampleWithoutBLOBsMethod(Interface interfaze) {

    }

    @Override
    protected void addSelectByPrimaryKeyMethod(Interface interfaze) {

    }

    @Override
    protected void addUpdateByExampleSelectiveMethod(Interface interfaze) {

    }

    @Override
    protected void addUpdateByExampleWithBLOBsMethod(Interface interfaze) {

    }

    @Override
    protected void addUpdateByExampleWithoutBLOBsMethod(Interface interfaze) {

    }

    @Override
    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {

    }

    @Override
    protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {

    }

    @Override
    protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze) {

    }
}
