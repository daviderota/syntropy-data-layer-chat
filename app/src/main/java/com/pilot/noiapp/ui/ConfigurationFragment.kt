package com.pilot.noiapp.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pilot.noiapp.R
import com.pilot.noiapp.databinding.FragmentConfigurationBinding
import com.pilot.noiapp.viewmodels.ConfigurationViewModel

class ConfigurationFragment : BaseFragment() {

    private var _binding: FragmentConfigurationBinding? = null
    private var dialog: AlertDialog? = null
    private val developerUrl: String = "https://docs.syntropynet.com/docs/data-layer-overview"

    private val viewModel by activityViewModels<ConfigurationViewModel>()

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
            viewModel.setConfiguration(
                binding.accessToken.editText?.text.toString(),
                binding.natsUrl.editText?.text.toString(),
                binding.stream.editText?.text.toString()
            )
            (activity as MainActivity).startService()
            findNavController().navigate(R.id.action_configurationFragment_to_chatFragment)
        }

        binding.accessToken.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                changeBtnGoStatus()
            }

        })

        binding.stream.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                changeBtnGoStatus()
            }

        })

       /* binding.info.setOnClickListener {
            dialog = AlertDialog.Builder(requireContext()).create()
            dialog?.setMessage(requireContext().getString(R.string.configuration_fragment_info_message))
            dialog?.setButton(
                DialogInterface.BUTTON_POSITIVE,
                getString(R.string.configuration_fragment_btn_go),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val urlIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(developerUrl)
                        )
                        startActivity(urlIntent)
                    }

                })
            dialog?.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.configuration_fragment_btn_cancel),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }
                })

            dialog?.show()
        }*/
    }

    private fun changeBtnGoStatus() {
        binding.btnGo.isEnabled = (binding.stream.editText?.text?.trim()
            ?.isNotBlank() == true && binding.stream.editText?.text?.trim()?.isNotBlank() == true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}