/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ObjectOverrideMisspelledMethodNameRule
 *
 * @author @Hackergarten
 */
class ObjectOverrideMisspelledMethodNameRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ObjectOverrideMisspelledMethodName"
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
        	boolean equals(Object o){}
        	class MyClass() {boolean equals(o){}}
            int hashCode(){}
            String toString(){}
        '''
        assertNoViolations(SOURCE)
    }

    void testEqual() {
        final SOURCE = '''
            boolean equal(Object o) {}
            boolean equal(int other) {}                 // ok; wrong param type
            boolean equal(Object o, int other) {}       // ok; too many params
        '''
        assertSingleViolation(SOURCE, 2, 'boolean equal(Object o) {}')
    }

    void testEquals_WrongCase() {
        final SOURCE = '''
            boolean eQuals(Object o) {}
            boolean equaLS(Object o) {}
            boolean equals(int other) {}                 // ok; wrong param type
            boolean equals(Object o, int other) {}       // ok; too many params
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'boolean eQuals(Object o) {}'],
                [lineNumber:3, sourceLineText:'boolean equaLS(Object o) {}'])
    }

    void testHashCode_WrongCase() {
        final SOURCE = '''
            int hashcode() {}
            Object haSHcode(int value) {}      // Note that it does not enforce return type or empty params
            int hashCOde() {}
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'int hashcode() {}'],
                [lineNumber:3, sourceLineText:'Object haSHcode(int value) {}'],
                [lineNumber:4, sourceLineText:'int hashCOde() {}'],
        )
    }

    void testToString_WrongCase() {
        final SOURCE = '''
            String tostring() {}
            String tostring(int value) {}   // ok; not empty param
            String toSTring() {}
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'String tostring() {}'],
                [lineNumber:4, sourceLineText:'String toSTring() {}'],
        )
    }

    protected Rule createRule() {
        return new ObjectOverrideMisspelledMethodNameRule()
    }

}
