package pl.marcinmoskala.stacktester

import android.app.Activity
import android.content.Intent.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.onItemSelectedListener
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

abstract class MainActivity : AppCompatActivity() {

    val name = this.javaClass.simpleName + " num " + counter++
    var chosenFlags: List<Pair<String, Int>> by observable(emptyList()) { p, o, n ->
        flagsMonitor.text = n.joinToString(separator = "\n", transform = { it.first })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonA.onClickStartActivityWithChosenFlag<A>()
        buttonB.onClickStartActivityWithChosenFlag<B>()
        buttonC.onClickStartActivityWithChosenFlag<C>()
        chooseFlagSpinner.setUp()
        title = name
        activityStack += this
    }

    override fun onDestroy() {
        super.onDestroy()
        activityStack -= this
    }

    fun updateStackMonitor() {
        activityStackMonitor.text = activityStack.joinToString(separator = "\n", transform = MainActivity::name)
    }

    private fun Spinner.setUp() {
        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, flagOptions.keys.toMutableList())
        onItemSelectedListener {
            onItemSelected { adapterView, view, i, l -> onItemChosen(i) }
        }
    }

    private fun onItemChosen(position: Int) {
        val flag = flagOptions.toList()[position]
        if (flag.second != 0) {
            chosenFlags += flag
        }
    }

    private inline fun <reified T : Activity> View.onClickStartActivityWithChosenFlag() {
        setOnClickListener { startActivityWithChosenFlag<T>() }
    }

    private inline fun <reified T : Activity> startActivityWithChosenFlag() {
        val intent = intentFor<T>()
        for (f in chosenFlags) intent.addFlags(f.second)
        startActivity(intent)
        chosenFlags = emptyList()
    }

    companion object {
        val flagOptions = mapOf<String, Int>(
                "SELECT TO ADD" to 0,
                "FLAG_ACTIVITY_NEW_TASK" to FLAG_ACTIVITY_NEW_TASK,
                "FLAG_ACTIVITY_CLEAR_TOP" to FLAG_ACTIVITY_CLEAR_TOP,
                "FLAG_ACTIVITY_SINGLE_TOP" to FLAG_ACTIVITY_SINGLE_TOP,
                "FLAG_ACTIVITY_NO_HISTORY" to FLAG_ACTIVITY_NO_HISTORY,
                "FLAG_ACTIVITY_MULTIPLE_TASK" to FLAG_ACTIVITY_MULTIPLE_TASK,
                "FLAG_ACTIVITY_FORWARD_RESULT" to FLAG_ACTIVITY_FORWARD_RESULT,
                "FLAG_ACTIVITY_PREVIOUS_IS_TOP" to FLAG_ACTIVITY_PREVIOUS_IS_TOP,
                "FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS" to FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
                "FLAG_ACTIVITY_BROUGHT_TO_FRONT" to FLAG_ACTIVITY_BROUGHT_TO_FRONT,
                "FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY" to FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY,
                "FLAG_ACTIVITY_NEW_DOCUMENT" to FLAG_ACTIVITY_NEW_DOCUMENT,
                "FLAG_ACTIVITY_NO_USER_ACTION" to FLAG_ACTIVITY_NO_USER_ACTION,
                "FLAG_ACTIVITY_REORDER_TO_FRONT" to FLAG_ACTIVITY_REORDER_TO_FRONT,
                "FLAG_ACTIVITY_NO_ANIMATION" to FLAG_ACTIVITY_NO_ANIMATION,
                "FLAG_ACTIVITY_CLEAR_TASK" to FLAG_ACTIVITY_CLEAR_TASK,
                "FLAG_ACTIVITY_TASK_ON_HOME" to FLAG_ACTIVITY_TASK_ON_HOME,
                "FLAG_ACTIVITY_RETAIN_IN_RECENTS" to FLAG_ACTIVITY_RETAIN_IN_RECENTS,
                "FLAG_ACTIVITY_LAUNCH_ADJACENT" to FLAG_ACTIVITY_LAUNCH_ADJACENT)
        var counter = 0
        var activityStack: List<MainActivity> by observable(listOf()) { p, o, n ->
            n.forEach { it.updateStackMonitor() }
        }
    }
}

class A : MainActivity()

class B : MainActivity()

class C : MainActivity()