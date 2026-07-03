package com.codershubinc.nullvoidlauncher.ui.focus

import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.time.LocalTime
import java.util.Locale
import java.util.concurrent.TimeUnit
import org.json.JSONObject 
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

// Subtask inside Timeline Event checklist
data class TimelineTask(
    val id: String,
    val title: String,
    val completed: Boolean,
)

// Representation of the Timeline Phase with checklist tasks
data class TimelineEvent(
    val id: String,
    val startTime: String,  // Format: "HH:MM"
    val endTime: String,    // Format: "HH:MM"
    val title: String,
    val category: String,
    val coreAction: String,
    val tasks: List<TimelineTask>
)

enum class ServiceStatus {
    CONNECTING, CONNECTED, DISCONNECTED, ERROR, FALLBACK
}

class ActivePhaseService(
    private val serverUrl: String,
    private val userId: String,
    private val onStatusChanged: ((ServiceStatus) -> Unit)? = null,
    private val onPhaseChanged: (TimelineEvent?, Int, Int) -> Unit
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // Required for infinite SSE connections
        .build()

    private var eventSource: EventSource? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var scheduler: ScheduledExecutorService? = null
    private var isUsingFallback = false
    private var lastKnownEvents: List<TimelineEvent> = emptyList()

    // Start the real-time server-sent events stream
    fun startListening() {
        // Cancel any active stream or scheduler before starting a new one
        stopListening()
        
        isUsingFallback = false
        onStatusChanged?.invoke(ServiceStatus.CONNECTING)

        // Start a local ticker to ensure minutes/progress update even if server is silent
        startLocalTicker()

        val request = Request.Builder()
            .url("$serverUrl/api/progress/realtime?userId=$userId")
            .header("Accept", "text/event-stream")
            .build()

        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                println("SSE Stream Opened.")
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (type == "progress") {
                    handleProgressUpdate(data)
                }
            }

            override fun onClosed(eventSource: EventSource) {
                println("SSE Stream Closed.")
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                System.err.println("SSE Stream Error: ${t?.message}. Activating local fallback simulator.")
                startFallbackSimulator()
            }
        }

        try {
            eventSource = EventSources.createFactory(client).newEventSource(request, listener)
        } catch (e: Exception) {
            e.printStackTrace()
            startFallbackSimulator()
        }
    }

    fun stopListening() {
        eventSource?.cancel()
        eventSource = null
        scheduler?.shutdownNow()
        scheduler = null
        isUsingFallback = false
    }

    // Parse JSON payloads including the tasks array
    private fun handleProgressUpdate(jsonData: String) {
        try {
            val root = JSONObject(jsonData)
            val timelineArray = root.getJSONArray("timeline")
            val eventsList = mutableListOf<TimelineEvent>()

            for (i in 0 until timelineArray.length()) {
                val obj = timelineArray.getJSONObject(i)
                
                val tasksList = mutableListOf<TimelineTask>()
                if (obj.has("tasks")) {
                    val tasksArray = obj.getJSONArray("tasks")
                    for (j in 0 until tasksArray.length()) {
                        val taskObj = tasksArray.getJSONObject(j)
                        tasksList.add(
                            TimelineTask(
                                id = taskObj.getString("id"),
                                title = taskObj.getString("title"),
                                completed = taskObj.optBoolean("completed", false)
                            )
                        )
                    }
                }

                eventsList.add(
                    TimelineEvent(
                        id = obj.getString("id"),
                        startTime = obj.getString("startTime"),
                        endTime = obj.getString("endTime"),
                        title = obj.getString("title"),
                        category = obj.optString("category", "focus"),
                        coreAction = obj.optString("coreAction", ""),
                        tasks = tasksList
                    )
                )
            }

            lastKnownEvents = eventsList
            updateActivePhase(eventsList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startLocalTicker() {
        scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler?.scheduleWithFixedDelay({
            if (lastKnownEvents.isNotEmpty()) {
                updateActivePhase(lastKnownEvents)
            }
        }, 5, 10, TimeUnit.SECONDS) // Update every 10 seconds for better responsiveness
    }

    // Compute active phase details and dispatch to main thread
    private fun updateActivePhase(eventsList: List<TimelineEvent>) {
        val now = LocalTime.now()
        val currentMinutes = (now.hour * 60) + now.minute

        val activePhase = eventsList.find { isEventActive(it, currentMinutes) }
        
        var progress = 0
        var minutesLeft = 0

        if (activePhase != null) {
            val start = timeToMinutes(activePhase.startTime)
            val end = timeToMinutes(activePhase.endTime)

            val totalDuration = if (end > start) {
                end - start
            } else {
                (1440 - start) + end
            }

            val elapsed = if (end > start) {
                currentMinutes - start
            } else {
                if (currentMinutes >= start) {
                    currentMinutes - start
                } else {
                    (1440 - start) + currentMinutes
                }
            }

            progress = if (totalDuration > 0) {
                (elapsed.toDouble() / totalDuration.toDouble() * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }

            minutesLeft = (totalDuration - elapsed).coerceAtLeast(0)
        }

        mainHandler.post {
            onPhaseChanged(activePhase, progress, minutesLeft)
        }
    }

    // Local Fallback Simulation
    private fun startFallbackSimulator() {
        if (isUsingFallback) return
        isUsingFallback = true
        
        // Overwrite the ticker if it exists, or just use it to push mock data
        scheduler?.shutdownNow()
        scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler?.scheduleWithFixedDelay({
            val mockEvents = getMockTimelineEvents()
            lastKnownEvents = mockEvents
            updateActivePhase(mockEvents)
        }, 0, 5, TimeUnit.SECONDS)
    }

    private fun getMockTimelineEvents(): List<TimelineEvent> {
        val now = LocalTime.now()
        val currentHour = now.hour

        // Generate dynamically spanning timeline events
        val hour1 = String.format(Locale.getDefault(), "%02d:00", (currentHour - 1 + 24) % 24)
        val hour2 = String.format(Locale.getDefault(), "%02d:30", currentHour)
        val hour3 = String.format(Locale.getDefault(), "%02d:00", (currentHour + 1) % 24)
        val hour4 = String.format(Locale.getDefault(), "%02d:30", (currentHour + 2) % 24)

        return listOf(
            TimelineEvent(
                id = "1", 
                startTime = hour1, 
                endTime = hour2, 
                title = "Kotlin SSE Integration", 
                category = "deep_work",
                coreAction = "Configure OkHttp SSE client and parser protocols",
                tasks = listOf(
                    TimelineTask("1-0", "Configure OkHttp SSE client", completed = true),
                    TimelineTask("1-1", "Implement parsing logic for active phase", completed = true),
                    TimelineTask("1-2", "Design UI elements for checklist & progress", completed = false),
                    TimelineTask("1-3", "Verify build and execution correctness", completed = false),
                )
            ),
            TimelineEvent(
                id = "2", 
                startTime = hour2, 
                endTime = hour3, 
                title = "Deep Focus Session", 
                category = "work",
                coreAction = "Implement active phase tile checklist UI inside FocusModeScreen",
                tasks = listOf(
                    TimelineTask("2-0", "Display checklist subtasks with checkboxes", true),
                    TimelineTask("2-1", "Apply strike-through line decoration to completed tasks", false),
                    TimelineTask("2-2", "Include minutes remaining text label indicator", false)
                )
            ),
            TimelineEvent(
                id = "3", 
                startTime = hour3, 
                endTime = hour4, 
                title = "Wind Down & Plan", 
                category = "reflect",
                coreAction = "Review daily achievements and compile progress summary",
                tasks = listOf(
                    TimelineTask("3-0", "Update local TODO lists", true),
                    TimelineTask("3-1", "Review Gradle dependencies and files config", false),
                    TimelineTask("3-2", "Shut down local developer environment", false)
                )
            )
        )
    }

    private fun timeToMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.split(":")
            if (parts.size >= 2) {
                val h = parts[0].trim().toInt()
                val m = parts[1].trim().toInt()
                h * 60 + m
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun isEventActive(event: TimelineEvent, currentMinutes: Int): Boolean {
        val start = timeToMinutes(event.startTime)
        val end = timeToMinutes(event.endTime)

        return if (end > start) {
            currentMinutes in start until end
        } else {
            currentMinutes !in end until start
        }
    }
}
