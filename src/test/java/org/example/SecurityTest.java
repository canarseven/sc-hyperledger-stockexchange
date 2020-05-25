/*
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

package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class SecurityTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Security security = new Security("AAPL", "Apple Inc.", "100");

            assertThat(security).isEqualTo(security);
        }

        @Test
        public void isSymmetric() {
            Security secA = new Security("AAPL", "Apple Inc.", "100");
            Security secB = new Security("AAPL", "Apple Inc.", "100");

            assertThat(secA).isEqualTo(secB);
            assertThat(secB).isEqualTo(secA);
        }

        @Test
        public void isTransitive() {
            Security secA = new Security("AAPL", "Apple Inc.", "100");
            Security secB = new Security("AAPL", "Apple Inc.", "100");
            Security secC = new Security("AAPL", "Apple Inc.", "100");

            assertThat(secA).isEqualTo(secB);
            assertThat(secB).isEqualTo(secC);
            assertThat(secA).isEqualTo(secC);
        }

        @Test
        public void handlesInequality() {
            Security secA = new Security("AAPL", "Apple Inc.", "100");
            Security secB = new Security("SNAP", "Snap Inc.", "50");

            assertThat(secA).isNotEqualTo(secB);
        }

        @Test
        public void handlesOtherObjects() {
            Security secA = new Security("AAPL", "Apple Inc.", "100");
            String secB = "not a security";

            assertThat(secA).isNotEqualTo(secB);
        }

        @Test
        public void handlesNull() {
            Security sec = new Security("AAPL", "Apple Inc.", "100");

            assertThat(sec).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesSecurity() {
        Security sec = new Security("AAPL", "Apple Inc.", "100");

        assertThat(sec.toString()).isEqualTo("Security@" + sec.hashCode() + " [symbol=AAPL, name=Apple Inc., quantity=100]");
    }
}
