package ru.d3st.travelblogapp.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import ru.d3st.travelblogapp.R
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import java.text.SimpleDateFormat
import java.util.*

/**
 * databinding адаптеры
 */

/**
 * Метод для отображения Pictures с помощью бибилотеки Glide
 * c обработкой ошибок
 *
 * @param imgView view где будет отображаться picture
 * @param imgUrl url picture
 */

//для установки изображений
//так же в манифесте стоит включить интернет пермишин
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {

    imgUrl?.let{
        imgView.load(imgUrl){
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }

}

/**
 * Метод для скрытия - отображения View
 *
 * @param view сама View
 * @param show сотояние видимости View
 */
@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}



/**
 * Метод для отображения имени блогера
 *
 * @param item данный блогер
 */
@BindingAdapter("name")
fun TextView.setName(item: BloggerDomain?) {
    if (item != null) {
        text = item.name
    }
}

/**
 * Метод для отображения продолжительности видео
 *
 * @param item данное видео
 */
@BindingAdapter("runtime")
fun TextView.setRuntime(item: VideoDomain?) {
    if (item != null) {
        val runtime = (item.end.time - item.start.time)
        val inFormat = millisToDate(runtime)

        text = inFormat
    }
}

/**
 * Метод для отображения количества видео у пользователя
 *
 * @param item данный блогер
 */
@BindingAdapter("videosCount")
fun TextView.setVideosCount(item: BloggerDomain?) {
    if (item != null) {
        text = context.getString(R.string.videos_count, item.videos)
    }
}

private const val DATE_FORMAT = "mm:ss"

fun millisToDate(millis: Long) : String {
    return SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date(millis))
}


