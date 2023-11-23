// Copyright (c) 2023 Broadcom. All Rights Reserved. The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 2.0 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.

package com.rabbitmq.client.test;

import static com.rabbitmq.client.test.TestUtils.LatchConditions.completed;
import static com.rabbitmq.client.test.TestUtils.waitAtMost;
import static org.assertj.core.api.Assertions.assertThat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BlockedConnectionTest extends BrokerTestCase {

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void errorInBlockListenerShouldCloseConnection(boolean nio) throws Exception {
    ConnectionFactory cf = TestUtils.connectionFactory();
    if (nio) {
      cf.useNio();
    } else {
      cf.useBlockingIo();
    }
    Connection c = cf.newConnection();
    CountDownLatch shutdownLatch = new CountDownLatch(1);
    c.addShutdownListener(cause -> shutdownLatch.countDown());
    CountDownLatch blockedLatch = new CountDownLatch(1);
    c.addBlockedListener(
        reason -> {
          blockedLatch.countDown();
          throw new RuntimeException("error in blocked listener!");
        },
        () -> {});
    try {
      block();
      Channel ch = c.createChannel();
      ch.basicPublish("", "", null, "dummy".getBytes());
      assertThat(blockedLatch).is(completed());
    } finally {
      unblock();
    }
    assertThat(shutdownLatch).is(completed());
    waitAtMost(() -> !c.isOpen());
  }

}
