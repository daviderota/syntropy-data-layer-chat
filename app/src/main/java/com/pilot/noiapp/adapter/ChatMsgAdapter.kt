package com.pilot.noiapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pilot.noiapp.databinding.SingleMessageChatBinding
import com.pilot.noiapp.databinding.SingleMessageChatMyBinding
import com.pilot.noiapp.model.DataLayerMessage

class ChatMsgAdapter(private val messages: ArrayList<DataLayerMessage>, val uuid: String) :
    RecyclerView.Adapter<ViewHolder>() {

    private var items: ArrayList<DataLayerMessage> = messages
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val position = getItemViewType(viewType)
        println("XXX - onCreateViewHolder() position: ${position}")

        val type = if (items.get(position).uid == uuid) Type.MY.value
        else
            Type.OTHER.value
        println("XXX - onCreateViewHolder() type: ${type}")
        return when (type) {
            Type.MY.value -> {
                MyMessageViewHolderBase(
                    SingleMessageChatMyBinding.inflate(inflater, parent, false)
                )
            }

            Type.OTHER.value -> {
                MessageViewHolderBase(
                    SingleMessageChatBinding.inflate(inflater, parent, false)
                )
            }

            else -> {
                MessageViewHolderBase(
                    SingleMessageChatBinding.inflate(inflater, parent, false)
                )
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position

    }

    enum class Type(val value: Int) {
        MY(0), OTHER(1)//, WELCOME(2)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("XXX - onBindViewHolder()-  position: ${position}")
        val item = items.get(position)

        if (item.uid == uuid) {
            val myViewHolder = holder as MyMessageViewHolderBase
            myViewHolder.bind(item)

        } else {
            val myViewHolder = holder as MessageViewHolderBase
            myViewHolder.bind(item)
        }


    }

    fun setMessages(messages: ArrayList<DataLayerMessage>) {
        println("XXX - setMessages()-  size: ${messages.size}")
        items = messages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        println("XXX - getItemCount() return ${items.size}")
        return items.size
    }

    inner class MyMessageViewHolderBase(val binding: SingleMessageChatMyBinding) :
        BaseMsgViewHolder(binding.root) {
        override fun bind(data: DataLayerMessage) {
            binding.message.text = data.msg
            binding.userUid.text = data.uid

        }
    }


    inner class MessageViewHolderBase(val binding: SingleMessageChatBinding) :
        BaseMsgViewHolder(binding.root) {
        override fun bind(data: DataLayerMessage) {
            binding.message.text = data.msg
            binding.userUid.text = data.uid

        }
    }

    /* inner class WelcomeMessageViewHolderBase(val binding: WelcomeMessageChatBinding) :
         BaseMsgViewHolder(binding.root) {
         override fun bind(data: DataLayerMessage) {
             binding.welcomeMessage.text = context?.getString(R.string.welcome_message)

         }
     }*/

    abstract inner class BaseMsgViewHolder(root: View) :
        RecyclerView.ViewHolder(root) {
        abstract fun bind(data: DataLayerMessage)

    }


}
