package dev.arie.gravity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CarouselScreen(sensorResult: SensorResult) {
    val pages = listOf(
        PageItem("Brazil", R.drawable.brazil_bg, R.drawable.brazil),
        PageItem("France", R.drawable.france_bg, R.drawable.france),
        PageItem("Iceland", R.drawable.iceland_bg, R.drawable.iceland),
        PageItem("We're here to help", 0, 0),
    )
    val pagerState = rememberPagerState()

    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        val topGuideline = createGuidelineFromTop(0.13f)
        val (title, pager, indicator) = createRefs()

        Text(
            text = when (pagerState.currentPage) {
                3 -> "We're here to assist you"
                else -> "Where do you want to go to?"
            },
            modifier = Modifier.constrainAs(title) {
                top.linkTo(topGuideline)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(pager.top)
            },
            style = TextStyle(
                fontSize = when (pagerState.currentPage) {
                    3 -> 28.sp
                    else -> 24.sp
                },
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight(600)
            )
        )
        HorizontalPager(
            modifier = Modifier.constrainAs(pager) {
                top.linkTo(title.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            state = pagerState,
            count = 4,
            verticalAlignment = Alignment.CenterVertically
        ) { index ->
            if (index <= 2) {
                ScrollItem(pages[index], sensorResult)
            } else {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lighthouse))
                Column {
                    LottieAnimation(
                        modifier = Modifier
                            .size(400.dp)
                            .padding(
                                start = 24.dp,
                                end = 24.dp
                            ),
                        composition = composition,
                        progress = {
                            sensorResult.roll
                        }
                    )

                    Button(
                        onClick = {
                            // no-op
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF05A846),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp)
                    ) {
                        Text(text = "Ok, got it")
                    }
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .background(color = Color.Transparent)
                .padding(16.dp, bottom = 64.dp)
                .constrainAs(indicator) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            activeColor = colorResource(R.color.purple_500)
        )
    }
}

@Composable
fun ScrollItem(
    page: PageItem,
    sensorResult: SensorResult
) {
    Column {
        Text(
            text = "${page.name}?",
            style = TextStyle(
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight(300)
            ),
            modifier = Modifier.padding(start = 48.dp, end = 48.dp, top = 8.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, end = 48.dp, top = 24.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(id = page.background),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = sensorResult.roll.dp.roundToPx(),
                            y = -sensorResult.pitch.dp.roundToPx()
                        )
                    }
                    .width(300.dp)
                    .height(400.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp)),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                alignment = BiasAlignment(
                    horizontalBias = (sensorResult.roll * 0.005).toFloat(),
                    verticalBias = 0f,
                )
            )
            Image(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp)),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                painter = painterResource(id = page.image),
                contentDescription = "Image of ${page.name}"
            )
        }
    }
}
