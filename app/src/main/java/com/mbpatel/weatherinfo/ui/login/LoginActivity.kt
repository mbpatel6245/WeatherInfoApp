package com.mbpatel.weatherinfo.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mbpatel.weatherinfo.App
import com.mbpatel.weatherinfo.MainActivity
import com.mbpatel.weatherinfo.R
import com.mbpatel.weatherinfo.utils.Constants
import com.mbpatel.weatherinfo.utils.getPreference
import com.mbpatel.weatherinfo.utils.savePreference
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private val timeFormat = "%02d:%02d:%02d"
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }
        val mobile = getPreference(this, Constants.KEY_PREFERENCE_MOBILE)
        if (!TextUtils.isEmpty(mobile)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        btnVerify.setOnClickListener(this)
        btnVerifyCode.setOnClickListener(this)
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verificationInProgress = false
                updateUI(STATE_VERIFY_SUCCESS, credential)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                verificationInProgress = false

                if (e is FirebaseAuthInvalidCredentialsException) {
                    edtMobile.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                updateUI(STATE_VERIFY_FAILED)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token

                updateUI(STATE_CODE_SENT)

            }

        }
    }

    /**
     * Start CountDownTimer for 2min duration and update for 1 seconds
     */
    private fun setCounter() {
        countDownTimer = object : CountDownTimer(120000, 1000) {
            // adjust the milli seconds here
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = String.format(
                    timeFormat,
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
            }

            override fun onFinish() {
                tvTimer.visibility = View.GONE
            }
        }.start()
    }


    override fun onDestroy() {
        if (countDownTimer != null)
            countDownTimer?.cancel()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            2, // Timeout duration
            TimeUnit.MINUTES, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        verificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            2, // Timeout duration
            TimeUnit.MINUTES, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        App.getFirebaseAuth().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                loading.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    updateUI(STATE_SIGNIN_SUCCESS, user)
                    Log.e("USER", "->" + user?.phoneNumber)

                    savePreference(this, Constants.KEY_PREFERENCE_MOBILE, user?.phoneNumber)
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
                        edtVerificationCode.error = "Invalid OTP."
                        // [END_EXCLUDE]
                    }
                    // [START_EXCLUDE silent]
                    // Update UI
                    updateUI(STATE_SIGNIN_FAILED)
                    // [END_EXCLUDE]
                }
            }
    }

    private fun signOut() {
        App.getFirebaseAuth().signOut()
        updateUI(STATE_INITIALIZED)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(uiState: Int, cred: PhoneAuthCredential) {
        updateUI(uiState, null, cred)
    }

    private fun updateUI(
        uiState: Int,
        user: FirebaseUser? = App.getFirebaseAuth().currentUser,
        cred: PhoneAuthCredential? = null
    ) {
        when (uiState) {
            STATE_INITIALIZED -> {
                // Initialized state, show only the phone number field and start button
//                enableViews(binding.btnVerify, binding.edtMobile)
//                disableViews(binding.buttonVerifyPhone, binding.buttonResend, binding.fieldVerificationCode)
                //binding.detail.text = null
                Toast.makeText(this, "STATE INITALIZED", Toast.LENGTH_SHORT).show()
            }
            STATE_CODE_SENT -> {
                // Code sent state, show the verification field, the
//                enableViews(binding.buttonVerifyPhone, binding.buttonResend,
//                    binding.fieldPhoneNumber, binding.fieldVerificationCode)
//                disableViews(binding.buttonStartVerification)
                //binding.detail.setText(R.string.status_code_sent)
                loading.visibility = View.GONE
                groupMobileNumber.visibility = View.GONE
                groupVerification.visibility = View.VISIBLE
                setCounter()
                Toast.makeText(this, "Verification Code Successfully Sent!", Toast.LENGTH_SHORT)
                    .show()
            }
            STATE_VERIFY_FAILED -> {
                // Verification has failed, show all options
//                enableViews(binding.buttonStartVerification, binding.buttonVerifyPhone,
//                    binding.buttonResend, binding.fieldPhoneNumber,
//                    binding.fieldVerificationCode)
//                binding.detail.setText(R.string.status_verification_failed)
                loading.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Please Check Phone number or Add with country code (+91)",
                    Toast.LENGTH_SHORT
                ).show()
            }
            STATE_VERIFY_SUCCESS -> {
                // Verification has succeeded, proceed to firebase sign in
                /*disableViews(binding.buttonStartVerification, binding.buttonVerifyPhone,
                    binding.buttonResend, binding.fieldPhoneNumber,
                    binding.fieldVerificationCode)
                binding.detail.setText(R.string.status_verification_succeeded)
                */
                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.smsCode != null) {
                        // binding.fieldVerificationCode.setText(cred.smsCode)
                        Toast.makeText(
                            this,
                            "VERIFCATION SUCCESS" + cred.smsCode,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //binding.fieldVerificationCode.setText(R.string.instant_validation)
                        Toast.makeText(this, "INSTANT VALIDATION", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            STATE_SIGNIN_FAILED ->
                // No-op, handled by sign-in check
                // binding.detail.setText(R.string.status_sign_in_failed)
                Toast.makeText(this, "Invalid OTP code Please try again!", Toast.LENGTH_LONG).show()
            STATE_SIGNIN_SUCCESS -> {
            }
        } // Np-op, handled by sign-in check

        /*  if (user == null) {
              // Signed out
              binding.phoneAuthFields.visibility = View.VISIBLE
              binding.signedInButtons.visibility = View.GONE

              binding.status.setText(R.string.signed_out)
          } else {
              // Signed in
              binding.phoneAuthFields.visibility = View.GONE
              binding.signedInButtons.visibility = View.VISIBLE

              enableViews(binding.fieldPhoneNumber, binding.fieldVerificationCode)
              binding.fieldPhoneNumber.text = null
              binding.fieldVerificationCode.text = null

              binding.status.setText(R.string.signed_in)
              binding.detail.text = getString(R.string.firebase_status_fmt, user.uid)
          }*/
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = edtMobile.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            edtMobile.error = "Invalid phone number."
            return false
        }

        return true
    }

    private fun enableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = true
        }
    }

    private fun disableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = false
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnVerify -> {
                if (!validatePhoneNumber()) {
                    return
                }
                loading.visibility = View.VISIBLE
                startPhoneNumberVerification(edtMobile.text.toString())
            }
            R.id.btnVerifyCode -> {
                val code = edtVerificationCode.text.toString()
                if (TextUtils.isEmpty(code)) {
                    edtVerificationCode.error = "Cannot be empty."
                    return
                }
                loading.visibility = View.VISIBLE
                verifyPhoneNumberWithCode(storedVerificationId, code)
            }
//            R.id.tvResend -> resendVerificationCode(
//                binding.edtMobile.text.toString(),
//                resendToken
//            )
//            R.id.signOutButton -> signOut()
        }
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
        private const val STATE_INITIALIZED = 1
        private const val STATE_VERIFY_FAILED = 3
        private const val STATE_VERIFY_SUCCESS = 4
        private const val STATE_CODE_SENT = 2
        private const val STATE_SIGNIN_FAILED = 5
        private const val STATE_SIGNIN_SUCCESS = 6
    }

}