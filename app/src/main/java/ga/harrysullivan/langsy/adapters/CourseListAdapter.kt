package ga.harrysullivan.langsy.adapters

import android.view.DragEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ga.harrysullivan.langsy.R
import ga.harrysullivan.langsy.constants.DashboardLabel
import ga.harrysullivan.langsy.models.Course
import ga.harrysullivan.langsy.utils.CourseList

class CourseListAdapter(
    inflater: LayoutInflater,
    parent: ViewGroup,
    courses: List<Course>,
    callback: (course: Course, action: DashboardLabel) -> Unit
) {
//    private val dummyData = arrayOf("thins", "other this", "elsa", "hwwer", "foxes", "elsa")

    init {
        courses.forEach { course ->
            val courseLabel = inflater.inflate(R.layout.dashboard_course_label, parent, false)

            courseLabel.findViewById<TextView>(R.id.course_label_language).text =
                CourseList.localFromCode(course.language)
            courseLabel.findViewById<TextView>(R.id.course_label_cash).text =
                "$${course.cash.toInt()}"

            courseLabel.setOnClickListener {
                callback(course, DashboardLabel.COURSE)
            }

            courseLabel.setOnLongClickListener {
                callback(course, DashboardLabel.LEARNED_WORDS)
                true
            }


            parent.addView(courseLabel)

        }
    }
}