package org.stypox.tridenta.util

import android.os.CountDownTimer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun LifecycleAwareRepeatedAction(millisInterval: Long, onTick: () -> Unit) {
    val countDownTimer = object : CountDownTimer(
        // make sure it never gets canceled on its own
        Long.MAX_VALUE / 10,
        millisInterval
    ) {
        override fun onTick(millisUntilFinished: Long) {
            onTick()
        }
        override fun onFinish() {}
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                countDownTimer.start()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                countDownTimer.cancel()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}