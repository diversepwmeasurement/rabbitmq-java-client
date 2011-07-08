//  The contents of this file are subject to the Mozilla Public License
//  Version 1.1 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License
//  at http://www.mozilla.org/MPL/
//
//  Software distributed under the License is distributed on an "AS IS"
//  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
//  the License for the specific language governing rights and
//  limitations under the License.
//
//  The Original Code is RabbitMQ.
//
//  The Initial Developer of the Original Code is VMware, Inc.
//  Copyright (c) 2007-2011 VMware, Inc.  All rights reserved.

package com.rabbitmq.client.test.server;

import com.rabbitmq.client.test.ConfirmBase;

import java.io.IOException;

public class MessageRecovery extends ConfirmBase
{

    private final static String Q = "recovery-test";

    public void test() throws IOException, InterruptedException {
        channel.queueDeclare(Q, true, false, false, null);
        publish("", Q, true, false, false);
        waitAcks();
        restart();
        assertDelivered(Q, 1);
        channel.queueDelete(Q);
    }

}