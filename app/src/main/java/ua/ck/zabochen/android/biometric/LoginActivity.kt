package ua.ck.zabochen.android.biometric

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_login.*

// Guides:
// https://developer.android.com/reference/androidx/biometric/BiometricPrompt
// https://developer.android.com/training/sign-in/biometric-auth
// https://android-developers.googleblog.com/2019/10/one-biometric-api-over-all-android.html
// https://medium.com/androiddevelopers/migrating-from-fingerprintmanager-to-biometricprompt-4bc5f570dccd

// Security
// https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b
// https://www.youtube.com/watch?v=VeUhQvCObJY

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initUi()

        // Next
        isSupportBiometric()
    }

    private fun initUi() {
        btnGo.setOnClickListener {
            navigateToMainScreen()
        }
    }

    // Check whether the device supports biometric authentication
    private fun isSupportBiometric(): Boolean {
        return when (BiometricManager.from(this).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.i("LoginActivity", "isSupportBiometric: App can authenticate using biometrics")

                // Ask the user to authenticate
                getBiometricPrompt().authenticate(getPromptInfo())
                true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.i("LoginActivity", "isSupportBiometric: No biometric features available on this device")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.i("LoginActivity", "isSupportBiometric: Biometric features are currently unavailable")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.i(
                    "LoginActivity",
                    "isSupportBiometric: The user hasn't associated any biometric credentials with their account"
                )
                return false
            }
            else -> {
                Log.i("LoginActivity", "isSupportBiometric: ELSE -> FALSE")
                false
            }
        }
    }

    private fun getBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // is called when the user has been authenticated using
                // a credential that the device recognizes
                Log.i("LoginActivity", "onAuthenticationSucceeded")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // is called when an unrecoverable error occurs
                Log.i("LoginActivity", "onAuthenticationError")
                // Error Codes
                when (errorCode) {
                    BiometricPrompt.ERROR_CANCELED -> {
                        Log.i("LoginActivity", "onAuthenticationError: ERROR_CANCELED")
                    }
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        Log.i(
                            "LoginActivity",
                            "onAuthenticationError: ERROR_NEGATIVE_BUTTON"
                        )
                    }
                    BiometricPrompt.ERROR_USER_CANCELED -> {
                        Log.i("LoginActivity", "onAuthenticationError: ERROR_USER_CANCELED")
                    }
                    else -> Log.i("LoginActivity", "onAuthenticationError: ELSE => ERROR")

                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // is called when the user is rejected,
                // for example when a non-enrolled fingerprint is placed on the sensor,
                // but unlike with onAuthenticationError(),
                // the user can continue trying to authenticate
                Log.i("LoginActivity", "onAuthenticationFailed")
            }
        }
        return BiometricPrompt(this, executor, callback)
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Title")
            .setSubtitle("Subtitle")
            .setDescription("Description")
            //.setNegativeButtonText("Negative Button") // Cancel fingerprint auth - onAuthenticationError
            .setDeviceCredentialAllowed(true) // Use device PIN, pattern or password
            .setConfirmationRequired(true)
            .build()
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}