package com.example.drawapp.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap

//сохранять пути и цвета

data class PathData(
    val path: Path = Path(),
    //цвет по умолчанию
    val color: Color = Color.Blue,
    //ширина по умолчанию
    val lineWidth: Float = 5f,
    val cap: StrokeCap = StrokeCap.Round
)
