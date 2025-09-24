package com.example.cockroach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.core.graphics.drawable.toBitmap
import com.example.cockroach.data.model.Beetle

@Composable
fun BeetleSprite(
    beetle: Beetle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Canvas(
        modifier = modifier
            .size(64.dp, 80.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        val resourceId = context.resources.getIdentifier(
            beetle.type.spriteResource,
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            val drawable = context.resources.getDrawable(resourceId, null)
            val bitmap = drawable.toBitmap(768, 80).asImageBitmap()

            drawBeetleFrame(bitmap, beetle.animationFrame)
        }
    }
}

private fun DrawScope.drawBeetleFrame(bitmap: ImageBitmap, frame: Int) {
    val frameWidth = 64f
    val frameHeight = 80f
    val scale = 2f

    val srcX = (frame % 12) * frameWidth

    val srcRect = Rect(
        offset = Offset(srcX, 0f),
        size = androidx.compose.ui.geometry.Size(frameWidth, frameHeight)
    )

    drawImage(
        image = bitmap,
        srcOffset = IntOffset(srcX.toInt(), 0),
        srcSize = androidx.compose.ui.unit.IntSize(frameWidth.toInt(), frameHeight.toInt()),
        dstOffset = IntOffset.Zero,
        dstSize = androidx.compose.ui.unit.IntSize(
            (frameWidth * scale).toInt(),
            (frameHeight * scale).toInt()
        )
    )
}