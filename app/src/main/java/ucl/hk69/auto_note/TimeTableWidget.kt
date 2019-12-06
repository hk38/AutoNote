package ucl.hk69.auto_note

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import io.realm.Realm
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class TimeTableWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val textTimeArray = arrayOf(R.id.wStartTime1st, R.id.wStartTime2nd, R.id.wStartTime3rd, R.id.wStartTime4th, R.id.wStartTime5th, R.id.wStartTime6th, R.id.wStartTime7th)
    val textClassArray = arrayOf(R.id.wClass1st, R.id.wClass2nd, R.id.wClass3rd, R.id.wClass4th, R.id.wClass5th, R.id.wClass6th, R.id.wClass7th)
    val textPlaceArray = arrayOf(R.id.wPlace1st, R.id.wPlace2nd, R.id.wPlace3rd, R.id.wPlace4th, R.id.wPlace5th, R.id.wPlace6th, R.id.wPlace7th)
    Realm.init(context)
    val realm = Realm.getDefaultInstance()
    val opt = realm.where(OptionData::class.java).equalTo("key", 0).findFirst()
    val cal = Calendar.getInstance()
    val weekId = when {
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY -> 0
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY -> 10
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY -> 20
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY -> 30
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY -> 40
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && opt.numOfWeek > 5 -> 50
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && opt.numOfWeek > 6 -> 60
        else -> 70
    }

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.time_table_widget)

    if(opt.numOfTime > 4) views.setViewVisibility(R.id.wll5th, View.VISIBLE)
    else views.setViewVisibility(R.id.wll5th, View.GONE)

    if(opt.numOfTime > 5) views.setViewVisibility(R.id.wll6th, View.VISIBLE)
    else views.setViewVisibility(R.id.wll6th, View.GONE)

    if(opt.numOfTime > 6) views.setViewVisibility(R.id.wll7th, View.VISIBLE)
    else views.setViewVisibility(R.id.wll7th, View.GONE)

    // アクティビティの指定
    val intent = Intent(context, MainActivity::class.java)
    // PendingIntentの取得
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    for(i in 0 until opt.numOfTime){
        val startTime = realm.where(SettingData::class.java).equalTo("id", i*10).findFirst()
        views.setTextViewText(textTimeArray[i], "${startTime?.hour}:${startTime?.minute}")

        if(weekId < 70){
            val classData = realm.where(ClassData::class.java).equalTo("id", weekId + i).findFirst()
            views.setTextViewText(textClassArray[i], classData?.className)
            views.setTextViewText(textPlaceArray[i], classData?.place)
        }

        views.setInt(textClassArray[i], "setBackgroundColor", Color.parseColor("#50" + opt.bgColor))
        views.setInt(textPlaceArray[i], "setBackgroundColor", Color.parseColor("#50" + opt.bgColor))
        views.setOnClickPendingIntent(textTimeArray[i], pendingIntent)
        views.setOnClickPendingIntent(textClassArray[i], pendingIntent)
        views.setOnClickPendingIntent(textPlaceArray[i], pendingIntent)
    }
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}