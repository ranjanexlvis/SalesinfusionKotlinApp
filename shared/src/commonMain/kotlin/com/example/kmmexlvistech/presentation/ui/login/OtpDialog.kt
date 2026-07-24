package com.example.kmmexlvistech.presentation.ui.login


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun OtpDialog(
    otpLength: Int = 4,
    autoFillOtp: String? = null,   // 👈 ADD THIS
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onResendOtp: (() -> Unit)? = null
) {
    var otpText by remember { mutableStateOf("") }

    // ✅ Auto-fill when SMS OTP arrives
    LaunchedEffect(autoFillOtp) {
        autoFillOtp?.let {
            if (it.length == otpLength) {
                otpText = it
            }
        }
    }

    val isOtpComplete = otpText.length == otpLength

    var timer by remember { mutableStateOf(30) }
    var isResendEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(timer) {
        if (timer > 0) {
            delay(1000L)
            timer--
        } else {
            isResendEnabled = true
        }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter OTP",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                OtpTextField(
                    otpText = otpText,
                    otpCount = otpLength,
                    onOtpTextChange = { value, _ -> otpText = value }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isResendEnabled) {
                    Text(
                        text = "Resend OTP in ${timer}s",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    TextButton(onClick = {
                        timer = 30
                        isResendEnabled = false
                        onResendOtp?.invoke()
                    }) {
                        Text(
                            text = "Resend OTP",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel", color = Color.Gray)
                    }
                    Button(
                        onClick = { onConfirm(otpText) },
                        enabled = isOtpComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Verify")
                    }
                }
            }
        }
    }
}
