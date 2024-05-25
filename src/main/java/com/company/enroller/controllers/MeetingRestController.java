package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;
    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        if (meetingService.findById(meeting.getId()) != null) {
            return new ResponseEntity<String>(
                    "Unable to create. A meeting with id " + meeting.getId() + " already exist.",
                    HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        Meeting currentMeeting = meetingService.findById(id);
        if (currentMeeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.setId(currentMeeting.getId());
        meetingService.update(meeting);
        return new ResponseEntity<Meeting>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipants(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
         return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(@PathVariable("id") long id, @RequestBody Map<String, String> json) {
        Meeting currentMeeting = meetingService.findById(id);
        if (currentMeeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        String login = json.get("login");
        if (login == null){
            return new ResponseEntity<String>("Participant with this login doesn't exist", HttpStatus.BAD_REQUEST);
        }
        Participant addedParticipant = participantService.findByLogin(login);
        currentMeeting.addParticipant(addedParticipant);
        meetingService.update(currentMeeting);
        return new ResponseEntity<Collection<Participant>>(currentMeeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipantFromMeeting(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        meeting.removeParticipant(participant);
        meetingService.update(meeting);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);

    }
}
