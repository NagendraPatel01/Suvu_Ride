package com.suviridedriver.ui.account

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.suviridedriver.R
import com.suviridedriver.databinding.FragmentAccountBinding
import com.suviridedriver.ui.about.AboutActivity
import com.suviridedriver.ui.add_recharge.AddRechargeActivity
import com.suviridedriver.ui.help_support.SupportActivity
import com.suviridedriver.ui.language.LanguageActivity
import com.suviridedriver.ui.make_payment.MakePaymentActivity
import com.suviridedriver.ui.number_verification.MobileNumberActivity
import com.suviridedriver.ui.personal_details.ProfileActivity
import com.suviridedriver.ui.rewards.RewardActivity
import com.suviridedriver.ui.suvi_wallet.SuviWalletActivity
import com.suviridedriver.ui.welcome.WelcomeActivity
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val accountViewModel by viewModels<AccountViewModel>()
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClicks()

        getPprofiielObserver()

    }

    override fun onResume() {
        super.onResume()
        accountViewModel.getDriverDetail()

      //  setProfile()
    }

    private fun setClicks() {
        binding.user.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.llWallet.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), SuviWalletActivity::class.java)
            startActivity(intent)
        })

        binding.referAndEarn.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), RewardActivity::class.java)
            startActivity(intent)
        })
        binding.helpSupport.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), SupportActivity::class.java)
            startActivity(intent)
        })
        binding.about.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intent)
        })
        binding.llRecharge.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), AddRechargeActivity::class.java)

            startActivity(intent)
        })
        binding.logOut.setOnClickListener(View.OnClickListener {
            logout()
        })
    }

    private fun setProfile() {
        try {
            val name = AppSession.getValue(requireContext(), Constants.USER_NAME)!!
            val number = AppSession.getValue(requireContext(), Constants.USER_MOBILE_NUMBER)!!
            val address = AppSession.getValue(requireContext(), Constants.ADDRESS)!!
            val profile_pic = AppSession.getValue(requireContext(), Constants.PROFILE_PIC)!!
            binding.tvUserName.text = name
            Glide.with(this)
                .load(profile_pic)
                .placeholder(R.drawable.profile_icon)
                .into(binding.userImage)

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    private fun logout() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout_bottom_sheet_layout)
        val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
        val tvLogout = dialog.findViewById<TextView>(R.id.tvLogout)
        val tvLogoutTitle = dialog.findViewById<TextView>(R.id.tvLogoutTitle)
        val tvLogoutMessage = dialog.findViewById<TextView>(R.id.tvLogoutMessage)

        tvLogoutTitle.text = "Logout"
        tvLogoutMessage.text = "Are you Sure you want to logout?"

        tvGoBack.setOnClickListener {
            dialog.dismiss()
        }

        tvLogout.setOnClickListener {
            customerLogout(dialog)
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun customerLogout(dialog: Dialog) {
        accountViewModel.driverLogout()
        accountViewModel.driverLogoutResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Log.i(TAG, "Success" + it.data)
                    clearData(it.data!!.message)
                    dialog.dismiss()
                }
                is NetworkResult.Error -> {
                    Log.i(TAG, "Error" + it.message)

                }
                is NetworkResult.Loading -> {
                    Log.i(TAG, "Loading")
                }
            }
        }
    }

    fun clearData(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        TokenManager.saveToken("", requireContext())
        AppSession.clearSharedPreference(requireContext())
        startActivity(Intent(requireActivity(), LanguageActivity::class.java))
        requireActivity().finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getPprofiielObserver()
    {

        accountViewModel.getDriverDetailResponseLiveData.observe(viewLifecycleOwner) {
          //  hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                      //  showLog("driver_details", "loginResponse " + it.data)
                        val data = it.data
                        val drivingStatus = data.drivingLicence.documentSubmission
                        val vehiclesStatus = data.vehiclesDetails.documentSubmission
                        val addressStatus = data.drivingLicence.address
                        val takeSelfieStatus = data.takeSelfie.documentSubmission
                        val paymentStatus = data.paymentVerification

                      //  Glide.with(this).load(data.takeSelfie.selfie).placeholder(R.drawable.profile_icon).into(binding.ivUserPic)
                        // showLog("profile_pic", profile_pic)
                        binding.tvUserName.text = data.drivingLicence.fullName
                        Glide.with(this)
                            .load(data.takeSelfie.selfie)
                            .placeholder(R.drawable.profile_icon)
                            .into(binding.userImage)
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("driver_details", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                 //   showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

}