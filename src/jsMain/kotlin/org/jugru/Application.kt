package org.jugru

import androidx.compose.runtime.*
import org.jugru.model.SpeakerDTO
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.document
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.events.SyntheticSubmitEvent
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.get
import org.w3c.xhr.FormData

const val API_URL = "/jpoint-demo/api"
const val PORT = 8080


private suspend fun HttpClient.addSpeaker(speaker: SpeakerDTO, route: String = "/v3/speakers") {
    post("http://localhost:$PORT$API_URL$route") {
        contentType(ContentType.Application.Json)
        setBody(speaker)
    }
}

private val fakeData: List<SpeakerDTO>
    get() = listOf(
        SpeakerDTO(0, "Глеб", "Королькевич", 25, "Бэкендоделатель"),
        SpeakerDTO(1, "Александр", "Нозик", 38, "Фронтендоделатель")
    )

@Composable
private fun Div(
    firstClass: String,
    vararg otherClasses: String,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
): Unit = Div(
    attrs = {
        classes(firstClass, *otherClasses)
        attrs?.invoke(this)
    },
    content = content
)


@Composable
fun Application() {
    val client: HttpClient = remember {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }

    val scope = rememberCoroutineScope()
    val speakers = remember { mutableStateListOf<SpeakerDTO>().apply { addAll(fakeData) } }

    LaunchedEffect(Unit) {
        client.webSocket(path = "$API_URL/v3/speakers", host = "localhost", port = PORT) {
            while (isActive) {
                val newSpeaker: SpeakerDTO = receiveDeserialized()
                console.info("Received $newSpeaker")
                speakers.add(newSpeaker)
            }
        }
    }

    Div("container") {
        Div("row") {
            H1 {
                Text("JPoint-2024 Demo")
            }
        }
        Div("row") {
            Div("col", "overflow-auto") {
                H2 {
                    Text("Speakers")
                }
                Ul(attrs = { classes("list-group") }) {
                    speakers.forEach { speaker ->
                        Li(attrs = {
                            classes("list-group-item")
                        }) {
                            Div("card") {
                                H5(attrs = { classes("card-header") }) {
                                    Text("${speaker.firstName} ${speaker.lastName}")
                                }
                                if (speaker.description.isNotBlank()) {
                                    Div("card-body") {
                                        P(attrs = { classes("card-text") }) {
                                            Text(speaker.description)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Div("col") {
                Form(attrs = {
                    id("new-speaker-form")
                    onSubmit { event: SyntheticSubmitEvent ->
                        event.preventDefault()
                        val formData = FormData(document.forms["new-speaker-form"] as HTMLFormElement)
                        val age = formData.get("age") as? String
                        val newSpeaker = SpeakerDTO(
                            firstName = formData.get("firstName") as? String ?: "",
                            lastName = formData.get("lastName") as? String ?: "",
                            age = age?.toInt() ?: -1,
                            description = formData.get("description") as? String ?: "",
                        )
                        console.info(JSON.stringify(newSpeaker))
                        scope.launch {
                            client.addSpeaker(newSpeaker)
                        }
                    }
                }) {
                    Div("mb-3") {
                        Label("firstName", attrs = { classes("form-label") }) {
                            Text("First name")
                        }
                        Input(InputType.Text, attrs = {
                            classes("form-control")
                            name("firstName")
                        })
                    }

                    Div("mb-3") {
                        Label("lastName", attrs = { classes("form-label") }) {
                            Text("Last name")
                        }
                        Input(InputType.Text, attrs = {
                            classes("form-control")
                            name("lastName")
                        })
                    }

                    Div("mb-3") {
                        Label("age", attrs = { classes("form-label") }) {
                            Text("Age")
                        }
                        Input(InputType.Number, attrs = {
                            classes("form-control")
                            name("age")
                        })
                    }

                    Div("mb-3") {
                        Label("description", attrs = { classes("form-label") }) {
                            Text("Description")
                        }
                        TextArea(attrs = {
                            classes("form-control")
                            name("description")
                            rows(3)
                        })
                    }

                    Button(attrs = {
                        classes("btn", "btn-primary", "w-100")
                        type(ButtonType.Submit)
                    }) {
                        Text("Add speaker")
                    }
                }
            }
        }
    }
}