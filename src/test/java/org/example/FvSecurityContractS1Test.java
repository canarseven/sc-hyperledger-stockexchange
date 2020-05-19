/**
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Use this file for functional testing of your smart contract.
 * Fill out the arguments and return values for a function and
 * use the CodeLens links above the transaction blocks to
 * invoke/submit transactions.
 * All transactions defined in your smart contract are used here
 * to generate tests, including those functions that would
 * normally only be used on instantiate and upgrade operations.
 * This basic test file can also be used as the basis for building
 * further functional tests to run as part of a continuous
 * integration pipeline, or for debugging locally deployed smart
 * contracts by invoking/submitting individual transactions.
 *
 * Generating this test file will also modify the build file
 * in the smart contract project directory. This will require
 * the Java classpath/configuration to be synchronized.
 */

public final class FvSecurityContractS1Test {

    Wallet fabricWallet;
    Gateway gateway;
    Gateway.Builder builder;
    Network network;
    Contract contract;
    String homedir = System.getProperty("user.home");
    Path walletPath = Paths.get(homedir, ".fabric-vscode", "environments", "Local Fabric", "wallets", "Org1");
    Path connectionProfilePath = Paths.get(homedir, ".fabric-vscode", "environments", "Local Fabric", "gateways", "Org1", "Org1.json");
    String identityName = "admin";
    boolean isLocalhostURL = JavaSmartContractUtil.hasLocalhostURLs(connectionProfilePath);

    @BeforeEach
    public void before() {
        assertThatCode(() -> {
            JavaSmartContractUtil.setDiscoverAsLocalHost(isLocalhostURL);
            fabricWallet = Wallet.createFileSystemWallet(walletPath);
            builder = Gateway.createBuilder();
            builder.identity(fabricWallet, identityName).networkConfig(connectionProfilePath).discovery(true);
            gateway = builder.connect();
            network = gateway.getNetwork("mychannel");
            contract = network.getContract("s", "SecurityContract");
        }).doesNotThrowAnyException();
    }

    @AfterEach
    public void after() {
        gateway.close();
    }

    @Nested
    class CreateOrder {
        @Test
        public void submitCreateOrderTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String method = "EXAMPLE";
            String symbol = "EXAMPLE";
            String quantity = "EXAMPLE";
            String price = "EXAMPLE";
            String timestamp = "EXAMPLE";
            String[] args = new String[]{ method, symbol, quantity, price, timestamp };

            byte[] response = contract.submitTransaction("createOrder", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetMyAccount {
        @Test
        public void submitGetMyAccountTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getMyAccount", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class OrderIdExists {
        @Test
        public void submitOrderIdExistsTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("orderIdExists", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class InitOrderId {
        @Test
        public void submitInitOrderIdTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("initOrderId", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class ReadSecurity {
        @Test
        public void submitReadSecurityTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String symbol = "EXAMPLE";
            String[] args = new String[]{ symbol };

            byte[] response = contract.submitTransaction("readSecurity", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetMyBalance {
        @Test
        public void submitGetMyBalanceTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getMyBalance", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class TraderExists {
        @Test
        public void submitTraderExistsTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String hin = "EXAMPLE";
            String[] args = new String[]{ hin };

            byte[] response = contract.submitTransaction("traderExists", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetOrderId {
        @Test
        public void submitGetOrderIdTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getOrderId", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class SecurityExists {
        @Test
        public void submitSecurityExistsTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String symbol = "EXAMPLE";
            String[] args = new String[]{ symbol };

            byte[] response = contract.submitTransaction("securityExists", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class SettleOrder {
        @Test
        public void submitSettleOrderTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String buyId = "EXAMPLE";
            String sellId = "EXAMPLE";
            String symbol = "EXAMPLE";
            String price = "EXAMPLE";
            String quantity = "EXAMPLE";
            String buyTimestamp = "EXAMPLE";
            String sellTimestamp = "EXAMPLE";
            String sellHin = "EXAMPLE";
            String[] args = new String[]{ buyId, sellId, symbol, price, quantity, buyTimestamp, sellTimestamp, sellHin };

            byte[] response = contract.submitTransaction("settleOrder", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class OrderExists {
        @Test
        public void submitOrderExistsTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String orderHash = "EXAMPLE";
            String[] args = new String[]{ orderHash };

            byte[] response = contract.submitTransaction("orderExists", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetAllOrders {
        @Test
        public void submitGetAllOrdersTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getAllOrders", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class CreateTrader {
        @Test
        public void submitCreateTraderTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("createTrader", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class CreateSecurity {
        @Test
        public void submitCreateSecurityTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: populate transaction parameters
            String symbol = "EXAMPLE";
            String name = "EXAMPLE";
            String quantity = "EXAMPLE";
            String[] args = new String[]{ symbol, name, quantity };

            byte[] response = contract.submitTransaction("createSecurity", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetMyAccString {
        @Test
        public void submitGetMyAccStringTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getMyAccString", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetMyHin {
        @Test
        public void submitGetMyHinTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getMyHin", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }

    @Nested
    class GetMyId {
        @Test
        public void submitGetMyIdTest() throws ContractException, TimeoutException, InterruptedException {
            // TODO: Update with parameters of transaction
            String[] args = new String[0];

            byte[] response = contract.submitTransaction("getMyId", args);
            // submitTransaction returns buffer of transaction return value
            // TODO: Update with return value of transaction
            assertThat(true).isEqualTo(true);
            // assertThat(new String(response)).isEqualTo("");
        }
    }
}