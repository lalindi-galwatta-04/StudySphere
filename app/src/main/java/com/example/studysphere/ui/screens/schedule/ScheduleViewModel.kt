package com.example.studysphere.ui.screens.schedule

import kotlinx.coroutines.flow.first
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysphere.data.local.entity.LectureEntity
import com.example.studysphere.data.repository.LectureRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: LectureRepository
) : ViewModel() {

    val lectures: StateFlow<List<LectureEntity>> =
        repository.allLectures.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addLecture(lecture: LectureEntity) {
        viewModelScope.launch {
            repository.insertLecture(lecture)
        }
    }

    fun updateLecture(lecture: LectureEntity) {
        viewModelScope.launch {
            repository.updateLecture(lecture)
        }
    }

    fun deleteLecture(lecture: LectureEntity) {
        viewModelScope.launch {
            repository.deleteLecture(lecture)
        }
    }

    fun initializeSampleLectures() {
        viewModelScope.launch {
            if (repository.allLectures.first().isEmpty()) {
                val samples = listOf(
                    LectureEntity(
                        day = "Mon",
                        subject = "Data Structures",
                        startTime = "09:00 AM",
                        endTime = "11:00 AM",
                        location = "Lecture Hall 01"
                    ),
                    LectureEntity(
                        day = "Mon",
                        subject = "Database Systems",
                        startTime = "11:30 AM",
                        endTime = "01:30 PM",
                        location = "Lecture Hall 03"
                    ),
                    LectureEntity(
                        day = "Tue",
                        subject = "Software Engineering",
                        startTime = "09:00 AM",
                        endTime = "11:00 AM",
                        location = "Computer Lab"
                    ),
                    LectureEntity(
                        day = "Wed",
                        subject = "Object Oriented Programming",
                        startTime = "10:00 AM",
                        endTime = "12:00 PM",
                        location = "Lecture Hall 02"
                    ),
                    LectureEntity(
                        day = "Thu",
                        subject = "Information Security",
                        startTime = "01:00 PM",
                        endTime = "03:00 PM",
                        location = "Lecture Hall 04"
                    ),
                    LectureEntity(
                        day = "Fri",
                        subject = "Mobile Application Development",
                        startTime = "09:00 AM",
                        endTime = "11:00 AM",
                        location = "Computer Lab"
                    )
                )

                samples.forEach { repository.insertLecture(it) }
            }
        }
    }
}
