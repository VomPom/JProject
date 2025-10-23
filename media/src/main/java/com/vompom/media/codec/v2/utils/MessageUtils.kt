package com.vompom.media.codec.v2.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.MessageQueue
import com.vompom.media.codec.v2.docode.model.PlayerMessage

/**
 *
 * Created by @juliswang on 2025/10/15 21:38
 *
 * @Description
 */

object MessageUtils {

    /**
     * 在当前的消息队列中获取最后一个等于 what 的消息，并将其他消息全部移除
     * @param what    被查询的信息类型
     * @param handler 被查询的 handler
     */
    fun getLastMessageObjAndRemoveOther(what: Int, handler: Handler?): Message? {
        val targetMessageList = getMessageByWhat(what, handler)
        val message = targetMessageList.lastOrNull()
        targetMessageList.forEach {
            if (it != message) {
                handler?.removeMessages(it.what, it.obj)
            }
        }
        return message
    }

    @SuppressLint("DiscouragedPrivateApi")
    fun getMessageByWhat(what: Int, handler: Handler?): List<Message> {
        if (handler == null) return emptyList()
        var message = getMessage(handler)

        val targetMessageList = ArrayList<Message>()

        while (message != null) {
            if (message.what == what && (PlayerMessage::class.java.isInstance(message.obj))) {
                targetMessageList.add(message)
            } else if (message.target === handler) {
                // 如果有其他事件导致seek事件非连续，中断本次查询
                break
            }
            val f = Message::class.java.getDeclaredField("next")
            f.isAccessible = true
            message = (f.get(message) as Message?)
        }
        return targetMessageList.toList()
    }

    /**
     * 获取所有的 Message 主要测试使用
     */
    fun getAllMessage(handler: Handler?): List<Message> {
        if (handler == null) return emptyList()
        var message = getMessage(handler)
        val targetMessageList = ArrayList<Message>()

        while (message != null) {
            if ((PlayerMessage::class.java.isInstance(message.obj))) {
                targetMessageList.add(message)
            }
            val f = Message::class.java.getDeclaredField("next")
            f.isAccessible = true
            message = (f.get(message) as Message?)
        }
        return targetMessageList.toList()
    }

    /**
     * 获取消队列中的第一个 Message
     * @param handler 被查询的 handler
     */
    private fun getMessage(handler: Handler): Message? {
        val queue = handler.looper.queue
        val messageField = MessageQueue::class.java.getDeclaredField("mMessages")
        messageField.isAccessible = true
        return messageField.get(queue) as Message?
    }
}