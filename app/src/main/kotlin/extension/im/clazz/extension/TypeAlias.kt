package im.clazz.extension

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import chat.Relationship
import chat.model.ChatMessage
import com.halo.model.Data
import com.halo.net.ResponseXListener
import im.clazz.campus.group.CommentableHeaderView
import im.clazz.frament.list.BaseListItemViewHolder
import im.clazz.model.Record
import im.clazz.model.SingleWheelItem
import org.json.JSONObject
import java.io.File


typealias  Callback = () -> Unit
typealias  IntCallback = (i: Int?) -> Unit
typealias  BooleanCallback = (boolean: Boolean?) -> Unit
typealias  OffsetCallback = (offset: Int?) -> Unit
typealias  ViewCallback = (view: View?) -> Unit
typealias  FileCallback = (file: File?) -> Unit
typealias  StringCallback = (string: String?) -> Unit
typealias  RecorderCallback = (record: Record?) -> Unit
typealias  TitleTabCallback = (isLeftSelected: Boolean) -> Unit
typealias  JSONCallback = (jsonObj: JSONObject?) -> Unit
typealias  LessonClickCallback = (jsonObj: JSONObject?, lessonView:View?) -> Unit
typealias  OnRelationChangeListener = (uid: String, relationship: Relationship) -> Unit
typealias  AnyCallback = (any: Any?) -> Unit
typealias  PickerCallback<T> = (item: T?) -> Unit

typealias  ClassTimePickerCallback<W, S, E> = (weekday: W?, startClass: S?, endClass: E?) -> Unit
typealias  StartEndPickerCallback = (start: SingleWheelItem?, end: SingleWheelItem?) -> Unit

typealias  ViewBuilder<T> = (context:Context) -> CommentableHeaderView<T>


typealias  RequestMethod<TList> = (offset: Int?, keyword: String?, responseListener: ResponseXListener<TList>?) -> Unit
typealias  ItemViewHolderBuilder<T> = () -> BaseListItemViewHolder<T>
typealias  DataBuilder<T> = () -> T
typealias  ShouldDecorateFilter = (view: View?) -> Boolean
typealias  OnActivityResultCallback = (requestCode: Int, resultCode: Int, data: Intent?) -> Unit
typealias  OnTouchCallback = (event: MotionEvent) -> Unit

typealias  SendFailCallback = (chatMessage: ChatMessage, data: Data) -> Unit
typealias  SendSuccessCallback = (chatMessage: ChatMessage) -> Unit

typealias EditCallback = (str: String?, successCallback: Callback?, failCallback: Callback?) -> Unit