/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ParameterReassignmentRule
 *
 * @author Chris Mair
 */
class ParameterReassignmentRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ParameterReassignment'
    }

    void testMethodParametersWithoutReassignment_NoViolations() {
        final SOURCE = '''
            void myMethod(int a, String b, c) {
                other = 123
                println a
                doSomething(b)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testClosureParametersWithoutReassignment_NoViolations() {
        final SOURCE = '''
            def myClosure = { int a, String b, c ->
                other = 123
                println a
                doSomething(b)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testParameterReassigned_Violation() {
        final SOURCE = '''
            void myMethod(int a, String b) {
                println a
                b = 'new value'
            }
        '''
        assertSingleViolation(SOURCE, 4, "b = 'new value'", 'parameter [b] was reassigned')
    }

    void testTwoParametersReassigned_Violations() {
        final SOURCE = '''
            void myMethod(int a, b) {
                a = 123
                b = new Object()
            }
        '''
        assertTwoViolations(SOURCE,
            3, 'a = 123', 'parameter [a] was reassigned',
            4, 'b = new Object()', 'parameter [b] was reassigned')
    }

    void testMultipleMethodsWithParametersReassigned_Violations() {
        final SOURCE = '''
            void myMethod1(int a, b) {
                a = 123
            }
            void myMethod2(int a, b) {
                b = new Object()
            }
        '''
        assertTwoViolations(SOURCE,
            3, 'a = 123', 'parameter [a] was reassigned',
            6, 'b = new Object()', 'parameter [b] was reassigned')
    }

    void testMultipleClosuresWithParametersReassigned_Violations() {
        final SOURCE = '''
            def myClosure1 = { int a, b ->
                a = 123
            }
            def myClosure2 = { int a, b ->
                b = new Object()
            }
        '''
        assertTwoViolations(SOURCE,
            3, 'a = 123', 'parameter [a] was reassigned',
            6, 'b = new Object()', 'parameter [b] was reassigned')
    }

    void testParameterReassignedWithinInnerClass_Violation() {
        final SOURCE = '''
            int myMethod(Integer a) {
                def comparable = new Comparable<Integer>() {
                    int compareTo(Integer s) {
                        s = null
                        return 0
                    }
                }
                return comparable.compareTo(a)
            }
        '''
        assertSingleViolation(SOURCE, 5, 's = null', 'parameter [s] was reassigned')
    }

    void testNestedClosure_ParametersReassigned_Violations() {
        final SOURCE = '''
            def myClosure = { int a, String b ->
                println a
                def myInnerClosure = { int c ->
                    c = 39
                    a = 0
                }
                b = null
                return myInnerClosure
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:5, sourceLineText:'c = 39', messageText:'parameter [c] was reassigned'],
            [lineNumber:6, sourceLineText:'a = 0', messageText:'parameter [a] was reassigned'],
            [lineNumber:8, sourceLineText:'b = null', messageText:'parameter [b] was reassigned'])
    }

    void testAssignToFieldWithSameNameAsParameter_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private count = 0
                void myMethod(int count) {
                    println count       // parameter
                }
                void incrementCount() {
                    count = count + 1   // field
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ParameterReassignmentRule()
    }
}