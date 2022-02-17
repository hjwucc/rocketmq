/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.client.consumer.store;

import java.util.Map;
import java.util.Set;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Offset store interface
 */
public interface OffsetStore {
    /**
     * Load
     */
    // 从消息进度存储文件加载消息进度到内存
    void load() throws MQClientException;

    /**
     * Update the offset,store it in memory
     */
    // 更新内存中的消息消费进度，increaseOnly为true时表示offset必须 > 内存中当前消费偏移量
    void updateOffset(final MessageQueue mq, final long offset, final boolean increaseOnly);

    /**
     * Get offset from local storage
     *
     * @return The fetched offset
     */
    // 读取消息消费进度，type可选值如下：
    // READ_FORM_MEMORY:从内存中读取
    // READ_FROM_STORE:从磁盘中读取
    // MEMORY_FIRST_THEN_SOTRE:先从内存中读取，再从磁盘中读取
    long readOffset(final MessageQueue mq, final ReadOffsetType type);

    /**
     * Persist all offsets,may be in local storage or remote name server
     */
    void persistAll(final Set<MessageQueue> mqs);

    /**
     * Persist the offset,may be in local storage or remote name server
     */
    // 持久化指定消息队列的消息消费进度到磁盘
    void persist(final MessageQueue mq);

    /**
     * Remove offset
     */
    // 将消息队列的消息消费进度从内存中移除
    void removeOffset(MessageQueue mq);

    /**
     * @return The cloned offset table of given topic
     */
    // 复制该主题下所有消费队列的消费进度
    Map<MessageQueue, Long> cloneOffsetTable(String topic);

    /**
     * @param mq
     * @param offset
     * @param isOneway
     */
    // 使用集群消费时，更新存储在Broker端的消息消费进度
    void updateConsumeOffsetToBroker(MessageQueue mq, long offset, boolean isOneway) throws RemotingException,
        MQBrokerException, InterruptedException, MQClientException;
}
