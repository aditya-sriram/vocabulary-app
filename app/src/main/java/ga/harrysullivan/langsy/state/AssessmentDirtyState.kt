package ga.harrysullivan.langsy.state

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ga.harrysullivan.langsy.stateData.AssessmentDirtyStateData
import ga.harrysullivan.langsy.stateData.CurrentCourseStateData

class AssessmentDirtyState: ViewModel() {

    val data: MutableLiveData<AssessmentDirtyStateData> by lazy {
        MutableLiveData<AssessmentDirtyStateData>()
    }

}