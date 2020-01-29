package ga.harrysullivan.langsy.databases

import ga.harrysullivan.langsy.daos.CurrentCourseDao
import ga.harrysullivan.langsy.daos.TrainerDao

class CurrentCourseDatabase private constructor() {

    var currentCourseDao = CurrentCourseDao()
        private set

    companion object {

        @Volatile private var instance: CurrentCourseDatabase? = null
        fun getInstance()=
            instance ?: synchronized(this) {
                instance ?: CurrentCourseDatabase().also { instance = it }
            }
    }
}