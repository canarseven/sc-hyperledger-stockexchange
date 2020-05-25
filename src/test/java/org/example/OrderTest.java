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

public final class OrderTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Order order = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");

            assertThat(order).isEqualTo(order);
        }

        @Test
        public void isSymmetric() {
            Order orderA = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");
            Order orderB = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");

            assertThat(orderA).isEqualTo(orderB);
            assertThat(orderB).isEqualTo(orderA);
        }

        @Test
        public void isTransitive() {
            Order orderA = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");
            Order orderB = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");
            Order orderC = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");

            assertThat(orderA).isEqualTo(orderB);
            assertThat(orderB).isEqualTo(orderC);
            assertThat(orderC).isEqualTo(orderA);
        }

        @Test
        public void handlesInequality() {
            Order orderA = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");
            Order orderB = new Order("SNAP", "10", "40", "0", "1590400763", "true", "false", "B4020");

            assertThat(orderA).isNotEqualTo(orderB);
        }

        @Test
        public void handlesOtherObjects() {
            Order orderA = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");
            String orderB = "not an order";

            assertThat(orderA).isNotEqualTo(orderB);
        }

        @Test
        public void handlesNull() {
            Order order = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");

            assertThat(order).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesOrder() {
        Order order = new Order("AAPL", "5", "1000", "0", "1590400763", "true", "false", "A2010");

        assertThat(order.toString()).isEqualTo("Order@" + order.getHash() + " [orderHash=" + order.getHash() + "]");
    }
}
