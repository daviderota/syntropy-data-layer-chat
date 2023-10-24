package com.pilot.noiapp.ui

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilot.noiapp.adapter.ChatMsgAdapter
import com.pilot.noiapp.databinding.FragmentChatBinding
import com.pilot.noiapp.databinding.FragmentChatListBinding
import com.pilot.noiapp.model.DataLayerMessage
import com.pilot.noiapp.viewmodels.ConfigurationViewModel
import com.pilot.noiapp.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ConfigurationViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()

    private var androidUID: String? = null
    private var chatMsgAdapter: ChatMsgAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidUID = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.messages.observe(viewLifecycleOwner) {
            it?.let {
                if (chatMsgAdapter == null) {
                    initAdapter(it)
                } else
                    chatMsgAdapter?.setMessages(it)
            }
        }
        binding.sendMsg.setOnClickListener {
            (activity as? MainActivity)?.sendMessageToDataLayer(binding.editTextChat.text.toString())
            binding.editTextChat.text?.clear()
        }


    }

    private fun initAdapter(messages:ArrayList<DataLayerMessage>){
        chatMsgAdapter = ChatMsgAdapter(messages, androidUID ?: "")
        chatMsgAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerviewChat.smoothScrollToPosition(chatMsgAdapter?.itemCount ?: 0)
            }
        });
        binding.recyclerviewChat.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewChat.adapter = chatMsgAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}