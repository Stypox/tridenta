package org.stypox.tridenta.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.enums.CardinalPoint
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.ui.destinations.LineTripsScreenDestination
import org.stypox.tridenta.ui.destinations.StopTripsScreenDestination
import org.stypox.tridenta.ui.nav.DEEP_LINK_PREFIX
import java.lang.Float.min

private fun generateBitmapWithText(
    shortName: String,
    drawBackground: (Canvas) -> Int,
): Bitmap {
    // create a bitmap (240px seems good)
    val bitmap = Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // draw background
    val textColor = drawBackground(canvas)

    // create a painter for the text
    val paint = Paint()
    paint.color = textColor
    paint.textAlign = Paint.Align.CENTER

    // set the text size based on how wide and high the text to draw is
    val bounds = Rect()
    paint.textSize = 100.0f
    paint.getTextBounds(shortName, 0, shortName.length, bounds)
    // this formula to set text size seems to work well based on manual testing
    paint.textSize = min(180.0f / bounds.width(), 120.0f / bounds.height()) * 100.0f

    // draw text on top
    canvas.drawText(
        shortName,
        // ensure the text is centered
        canvas.width / 2.0f,
        canvas.height / 2.0f - (paint.descent() + paint.ascent()) / 2.0f,
        paint
    )

    return bitmap
}

private fun generateLineBitmap(context: Context, @ColorInt color: Int?, shortName: String): Bitmap {
    return generateBitmapWithText(shortName) { canvas ->
        val backgroundColor = color.toLineColor()

        // change the background color to the
        context.getDrawable(R.drawable.shortcut_background)?.apply {
            setTint(backgroundColor.toArgb())
            setBounds(0, 0, 240, 240)
            draw(canvas)
        }

        textColorOnBackground(backgroundColor).toArgb()
    }
}

private fun generateStopBitmap(
    context: Context,
    stopLineType: StopLineType,
    cardinalPoint: CardinalPoint
): Bitmap {
    return generateBitmapWithText(context.getString(cardinalPoint.shortName)) { canvas ->
        // draw the shortcut background in the original color
        context.getDrawable(R.drawable.shortcut_background)?.apply {
            setBounds(0, 0, 240, 240)
            draw(canvas)
        }

        // draw additional semitransparent background with urban/suburban icons
        context.getDrawable(stopLineType.shortcutDrawable)?.apply {
            alpha = 50
            setBounds(0, 0, 240, 240)
            draw(canvas)
        }

        // use the same color as urban/suburban icons
        ContextCompat.getColor(context, R.color.shortcut_foreground_color)
    }

}

fun buildStopShortcutInfo(context: Context, stop: DbStop): ShortcutInfoCompat.Builder {
    val route = StopTripsScreenDestination(stop.stopId, stop.type).route
    return ShortcutInfoCompat.Builder(context, route)
        .setShortLabel(stop.name)
        .setLongLabel(stop.name)
        .setIcon(
            when (stop.cardinalPoint) {
                is CardinalPoint -> IconCompat.createWithBitmap(
                    generateStopBitmap(context, stop.type, stop.cardinalPoint)
                )

                else -> IconCompat.createWithResource(context, stop.type.shortcutDrawable)
            }
        )
        .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse(DEEP_LINK_PREFIX + route)))
}

fun buildLineShortcutInfo(context: Context, line: DbLine): ShortcutInfoCompat.Builder {
    val route = LineTripsScreenDestination(line.lineId, line.type).route
    return ShortcutInfoCompat.Builder(context, route)
        .setShortLabel(line.longName)
        .setLongLabel(line.longName)
        .setIcon(
            IconCompat.createWithBitmap(generateLineBitmap(context, line.color, line.shortName))
        )
        .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse(DEEP_LINK_PREFIX + route)))
}