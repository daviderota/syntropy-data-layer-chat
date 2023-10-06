package com.pilot.noiapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.pilot.noiapp.databinding.FragmentConfigurationBinding
import com.pilot.noiapp.viewmodels.ConfigurationViewModel

class ConfigurationFragment : BaseFragment() {

    private var _binding: FragmentConfigurationBinding? = null

    private val configurationViewModel by activityViewModels<ConfigurationViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGo.setOnClickListener {
            configurationViewModel.setUsername(binding.username.editText?.text.toString())


        }

        binding.username.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                changeBtnGoStatus()
            }

        })


    }

    private fun changeBtnGoStatus() {
        binding.btnGo.isEnabled = (binding.username.editText?.text?.trim()?.isNotBlank() == true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}