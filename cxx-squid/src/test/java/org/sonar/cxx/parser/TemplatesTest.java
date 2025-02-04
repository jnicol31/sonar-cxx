/*
 * C++ Community Plugin (cxx plugin)
 * Copyright (C) 2010-2021 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.cxx.parser;

import org.junit.Test;

public class TemplatesTest extends ParserBaseTestHelper {

  @Test
  public void templateDeclaration() {
    setRootRule(CxxGrammarImpl.templateDeclaration);

    mockRule(CxxGrammarImpl.templateHead);
    mockRule(CxxGrammarImpl.declaration);
    mockRule(CxxGrammarImpl.conceptDefinition);

    assertThatParser()
      .matches("templateHead declaration")
      .matches("templateHead conceptDefinition");
  }

  @Test
  public void templateDeclaration_reallife() {
    setRootRule(CxxGrammarImpl.templateDeclaration);

    assertThatParser()
      .matches("template <class T> ostream& operator<<();")
      .matches("template <class T> ostream& operator<<(ostream& strm, const int& i);")
      .matches("template <class T> ostream& operator<< (ostream& strm);")
      .matches("template <class T> ostream& operator<< (const auto_ptr<T>& p);")
      .matches("template <class T> ostream& operator<< (ostream& strm, const auto_ptr<T>& p);")
      .matches("template<bool (A::*bar)(void)> void foo();")
      .matches("template<class T> auto mul(T a, T b) -> decltype(a*b) {return a*b;}")
      .matches("template <class T, class U> concept Derived = std::is_base_of<U, T>::value;")
      .matches("template<typename T> void f(T&&) requires Eq<T>;")
      .matches("template<typename T> requires Addable<T> T add(T a, T b) { return a + b; }")
      .matches("template<bool T = false> std::string f();"); // issue #2025
  }

  @Test
  public void templateHead() {
    setRootRule(CxxGrammarImpl.templateHead);

    mockRule(CxxGrammarImpl.templateParameterList);
    mockRule(CxxGrammarImpl.requiresClause);
    mockRule(CxxGrammarImpl.templateParameter);

    assertThatParser()
      .matches("template < templateParameterList >")
      .matches("template < templateParameterList > requiresClause");
  }

  @Test
  public void templateHead_reallife() {
    setRootRule(CxxGrammarImpl.templateHead);

    assertThatParser().matches("template<typename T> requires Addable<T>");
  }

  @Test
  public void templateParameterList() {
    setRootRule(CxxGrammarImpl.templateParameterList);

    mockRule(CxxGrammarImpl.templateParameter);

    assertThatParser()
      .matches("templateParameter")
      .matches("templateParameter , templateParameter");
  }

  @Test
  public void templateParameter() {
    setRootRule(CxxGrammarImpl.templateParameter);

    mockRule(CxxGrammarImpl.typeParameter);
    mockRule(CxxGrammarImpl.parameterDeclaration);

    assertThatParser()
      .matches("typeParameter")
      .matches("parameterDeclaration");
  }

  @Test
  public void templateParameter_reallife() {
    setRootRule(CxxGrammarImpl.templateParameter);

    // type-parameter: type-parameter-key ...opt identifieropt
    assertThatParser()
      .matches("typename")
      .matches("typename T")
      .matches("class T")
      .matches("typename ... T")
      // type-parameter: type-parameter-key identifieropt = type-id
      .matches("typename T1 = int")
      .matches("typename = int")
      // type-parameter: type-constraint ...opt identifieropt
      .matches("foo")
      .matches("foo::foo")
      .matches("foo::foo<A, B>")
      // type-parameter: type-constraint identifieropt = type-id
      .matches("foo = int")
      .matches("foo::foo = int")
      .matches("foo::foo<A, B> = int")
      // type-parameter: template-head type-parameter-key ...opt identifieropt
      .matches("template<typename = float> typename T")
      .matches("template<typename = float> typename ... T")
      // type-parameter: template-head type-parameter-key identifieropt = id-expression
      .matches("template<typename = float> typename T = foo")
      .matches("template<typename = float> typename = foo::foo")
      // parameter-declaration
      .matches("auto ... vs")
      .matches("auto** pp0");
  }

  @Test
  public void requiresClause() {
    setRootRule(CxxGrammarImpl.requiresClause);

    mockRule(CxxGrammarImpl.constraintLogicalOrExpression);

    assertThatParser().matches("requires constraintLogicalOrExpression");
  }

  @Test
  public void constraintLogicalOrExpression() {
    setRootRule(CxxGrammarImpl.constraintLogicalOrExpression);

    mockRule(CxxGrammarImpl.constraintLogicalAndExpression);

    assertThatParser()
      .matches("constraintLogicalAndExpression")
      .matches("constraintLogicalAndExpression || constraintLogicalAndExpression");
  }

  @Test
  public void constraintLogicalAndExpression() {
    setRootRule(CxxGrammarImpl.constraintLogicalAndExpression);

    mockRule(CxxGrammarImpl.primaryExpression);

    assertThatParser()
      .matches("primaryExpression")
      .matches("primaryExpression && primaryExpression");
  }

  @Test
  public void typeParameter() {
    setRootRule(CxxGrammarImpl.typeParameter);

    mockRule(CxxGrammarImpl.typeParameterKey);
    mockRule(CxxGrammarImpl.typeId);
    mockRule(CxxGrammarImpl.templateHead);
    mockRule(CxxGrammarImpl.idExpression);

    assertThatParser()
      .matches("typeParameterKey")
      .matches("typeParameterKey ...")
      .matches("typeParameterKey foo")
      .matches("typeParameterKey ... foo")
      .matches("typeParameterKey = typeId")
      .matches("typeParameterKey foo = typeId")
      .matches("templateHead typeParameterKey")
      .matches("templateHead typeParameterKey ...")
      .matches("templateHead typeParameterKey ... foo")
      .matches("templateHead typeParameterKey = idExpression")
      .matches("templateHead typeParameterKey foo = idExpression")
      .matches("typeConstraint")
      .matches("typeConstraint ...")
      .matches("typeConstraint foo")
      .matches("typeConstraint ... foo")
      .matches("typeConstraint = typeId")
      .matches("typeConstraint foo = typeId");
  }

  @Test
  public void simpleTemplateId_reallife() {
    setRootRule(CxxGrammarImpl.simpleTemplateId);

    assertThatParser()
      .matches("sometype<int>")
      .matches("vector<Person*>")
      .matches("A<(X>Y)>")
      .matches("A<(X<Y)>")
      .matches("vector<std::vector<bool>>")
      .matches("Y<X<(6>1)>>")
      .matches("Y<X<(6<1)>>")
      .matches("Y<X<(6>=1)>>")
      .matches("Y<X<(6<=1)>>")
      .matches("Y<X<(6>>1)>>")
      .matches("Y<X<(6<<1)>>")
      .matches("Y<X<(6<=>1)>>");
  }

  @Test
  public void typeConstraint() {
    setRootRule(CxxGrammarImpl.typeConstraint);

    mockRule(CxxGrammarImpl.nestedNameSpecifier);
    mockRule(CxxGrammarImpl.conceptName);
    mockRule(CxxGrammarImpl.templateArgumentList);

    assertThatParser()
      .matches("conceptName")
      .matches("nestedNameSpecifier conceptName")
      .matches("conceptName < >")
      .matches("conceptName < templateArgumentList >")
      .matches("nestedNameSpecifier conceptName < >")
      .matches("nestedNameSpecifier conceptName < templateArgumentList >");
  }

  @Test
  public void templateId() {
    setRootRule(CxxGrammarImpl.templateId);

    mockRule(CxxGrammarImpl.simpleTemplateId);
    mockRule(CxxGrammarImpl.operatorFunctionId);
    mockRule(CxxGrammarImpl.templateArgumentList);
    mockRule(CxxGrammarImpl.literalOperatorId);

    assertThatParser()
      .matches("simpleTemplateId")
      .matches("operatorFunctionId < >")
      .matches("operatorFunctionId < templateArgumentList >")
      .matches("literalOperatorId < >")
      .matches("literalOperatorId < templateArgumentList >");
  }

  @Test
  public void templateId_reallife() {
    setRootRule(CxxGrammarImpl.templateId);

    assertThatParser()
      .matches("foo<int>")
      .matches("operator==<B>");
  }

  @Test
  public void templateArgumentList() {
    setRootRule(CxxGrammarImpl.templateArgumentList);

    mockRule(CxxGrammarImpl.templateArgument);

    assertThatParser()
      .matches("templateArgument")
      .matches("templateArgument ...")
      .matches("templateArgument , templateArgument")
      .matches("templateArgument , templateArgument ...");
  }

  @Test
  public void conceptDefinition() {
    setRootRule(CxxGrammarImpl.conceptDefinition);

    mockRule(CxxGrammarImpl.conceptName);
    mockRule(CxxGrammarImpl.constraintExpression);

    assertThatParser().matches("concept conceptName = constraintExpression ;");
  }

  @Test
  public void typenameSpecifier() {
    setRootRule(CxxGrammarImpl.typenameSpecifier);

    mockRule(CxxGrammarImpl.nestedNameSpecifier);
    mockRule(CxxGrammarImpl.simpleTemplateId);

    assertThatParser()
      .matches("typename nestedNameSpecifier IDENTIFIER")
      .matches("typename nestedNameSpecifier simpleTemplateId")
      .matches("typename nestedNameSpecifier template simpleTemplateId")
      .matches("typename IDENTIFIER");
  }

  @Test
  public void deductionGuide() {
    setRootRule(CxxGrammarImpl.deductionGuide);

    mockRule(CxxGrammarImpl.explicitSpecifier);
    mockRule(CxxGrammarImpl.templateName);
    mockRule(CxxGrammarImpl.parameterDeclarationClause);
    mockRule(CxxGrammarImpl.simpleTemplateId);

    assertThatParser()
      .matches("templateName ( parameterDeclarationClause ) -> simpleTemplateId ;")
      .matches("explicitSpecifier templateName ( parameterDeclarationClause ) -> simpleTemplateId ;");
  }

}
