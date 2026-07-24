package com.example.kmmexlvistech.presentation.ui.login

import androidx.compose.foundation.layout.aspectRatio


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kmmexlvistech.presentation.theme.otp_background
import com.example.kmmexlvistech.presentation.theme.otp_border


@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 4,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        ),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText,
                        modifier = Modifier
                            .weight(1f)                 // ✅ distribute equally
                            .aspectRatio(1f)            // ✅ keep square shape
                            .border(
                                1.dp,
                                otp_border,
                                RoundedCornerShape(10.dp)
                            )
                    )
                    if (index != otpCount - 1) {
                        Spacer(modifier = Modifier.width(6.dp)) // spacing
                    }
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> "" // no "-" placeholder, keep clean
        index > text.length -> ""
        else -> text[index].toString()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.titleLarge,
            color = if (isFocused) otp_background else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
