package com.example.drawapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.drawapp.ui.theme.DrawAppTheme
import androidx.compose.ui.graphics.Path
import com.example.drawapp.ui.theme.BottomPanel
import com.example.drawapp.ui.theme.PathData

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //путь по умолчанию пустой
            val pathData = remember {
                mutableStateOf(PathData())
            }
            //PathList потому что нам надо передавать несколько путей (цвета)
            val pathList = remember {
                mutableStateListOf(PathData())
            }
            DrawAppTheme {
                Column {
                    DrawCanvas(pathData, pathList)
                    BottomPanel(
                        onClick = { color ->
                        pathData.value = pathData.value.copy(color = color)},
                        onLineWidthChange = { lineWidth ->        pathData.value = pathData.value.copy(lineWidth = lineWidth)
                        },
                        onBackClick = {
                            if (pathList.isNotEmpty()) {
                            pathList.removeAt(pathList.size - 1)
                        } },
                        onCapClick = { cap -> pathData.value = pathData.value.copy(cap = cap)
                        })

                    }
                }
            }
        }
    }


//когда выбираем цвет, приходят сюда данные
@Composable
fun DrawCanvas(pathData: MutableState<PathData>, pathList: SnapshotStateList<PathData>) {
    //для изменения состояния
    var tempPath = Path()


    Canvas(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.7F)
        .pointerInput(true) {
            detectDragGestures (
                onDragStart = {
                    //как только мы отпустили палец и заново начали проводить им, создается новый tempPath
                    tempPath = Path()
                },
                //для одной линии 1 датакласс
                onDragEnd = {
                    pathList.add(
                        pathData.value.copy(
                            //передаем путь
                            path = tempPath
                        )
                    )


                }
            ){ change, dragAmount ->
                //указать точку откуда будем рисовать линию
                //change - коорд Х и У, dragAmount насколько удалились от точки
                tempPath.moveTo(
                    change.position.x - dragAmount.x,
                    change.position.y - dragAmount.y
                )
                tempPath.lineTo(
                    change.position.x,
                    change.position.y
                )
                //оптимизация процесса. Без него у нас будут нарастающие классы точек, много памяти будет занимать
                if(pathList.size>0)//без if будет ошибка, так как при запуске size=0
                {
                    pathList.removeAt(pathList.size-1)
                }
                pathList.add(
                    pathData.value.copy(
                        //передаем путь
                        path = tempPath
                    )
                )
            }
        }
    ) {
        //цикл будет по очереди выдавать все пути, которые мы сохранили
        pathList.forEach { pathData ->
            drawPath(
                pathData.path,
                color= pathData.color,
                style= Stroke(
                    pathData.lineWidth,
                    cap = pathData.cap //cap для стиля линий. Без него это полосочки Butt(заметно при большой толщине).
                    // Можно сделать квадрат, круг, линия
                )
            )
        }

    }

}
