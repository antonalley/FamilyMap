package com.example.familymap.model

import model.Authtoken
import model.Event
import model.Person
import request_result.allEventResult
import request_result.allPersonResult


object DataCache {
    var authtoken: Authtoken? = null
    var username: String? = null
    var userPersonID: String? = null
    var persons: Map<String, Person>? = null // PersonID to person for faster access
    var events: Map<String, Event>? = null // eventID to event for faster access

    fun DataCache(ip : String, port : String, authtoken : String, username: String, personID: String) {
        val client = Client(ip, port)
        this.authtoken = Authtoken(username,authtoken)
        this.username = username
        this.userPersonID = personID
        var personsr : allPersonResult? = client.getPeople(authtoken)
        var eventsr : allEventResult? = client.getEvents(authtoken)
        if (personsr != null) {
            var personsMap : MutableMap<String, Person> = mutableMapOf()
            for (person in personsr.data.toList()) {
                personsMap += Pair(person.personID, person)
            }
            this.persons = personsMap
        }
        if (eventsr != null) {
            var eventsMap : MutableMap<String, Event> = mutableMapOf()
            for (event in eventsr.data.toList()) {
                eventsMap += Pair(event.eventID, event)
            }
            this.events = eventsMap
        }
    }
}



