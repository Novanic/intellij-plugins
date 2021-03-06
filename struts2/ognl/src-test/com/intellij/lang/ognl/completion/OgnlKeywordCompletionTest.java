/*
 * Copyright 2011 The authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.lang.ognl.completion;

import static com.intellij.lang.ognl.psi.OgnlKeyword.*;

/**
 * {@link OgnlKeywordCompletionContributor}.
 *
 * @author Yann C&eacute;bron
 */
public class OgnlKeywordCompletionTest extends OgnlCompletionTestCase {

  public void testAfterPlainReference() throws Throwable {
    doTest("reference <caret> ",
           SHL, SHR, USHR,
           AND, BAND, OR, BOR, XOR,
           EQ, NEQ, LT, LTE, GT, GTE,
           IN, NOT_IN,
           INSTANCEOF);
  }

  public void testAfterSimpleExpression() throws Throwable {
    doTest("1 + 2 <caret> ",
           SHL, SHR, USHR,
           AND, BAND, OR, BOR, XOR,
           EQ, NEQ, LT, LTE, GT, GTE,
           IN, NOT_IN);
  }

  public void testAfterQuestion() throws Throwable {
    doTest("reference ? <caret> ",
           FALSE, TRUE, NULL);
  }

  public void testAfterEquals() throws Throwable {
    doTest("reference == <caret> ",
           FALSE, TRUE, NULL);
  }

  public void testAfterAnd() throws Throwable {
    doTest("reference && <caret> ",
           FALSE, TRUE, NULL);
  }

}