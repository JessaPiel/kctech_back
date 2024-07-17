package com.kandaharcottages.kctech.Controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kandaharcottages.kctech.Model.Reservation;
import com.kandaharcottages.kctech.NotFoundException.ReservationNotFoundException;
import com.kandaharcottages.kctech.Repository.ReservationRepository;

@RestController
@RequestMapping("/api/v1/reservation")
public class ReservationController {


    ReservationRepository repo;

    public ReservationController(ReservationRepository repo){
        this.repo = repo;
    }

    @GetMapping("/all")
    public List<Reservation> getReservations() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Reservation getReservation(@PathVariable Long id){
        return repo.findById(id).orElseThrow(
            () -> new ReservationNotFoundException(id)
        );

    }

    @PostMapping("/new")
    public String addReservation(@RequestBody Reservation newReservation) {
        boolean isReserved = repo.isRoomReserved(
            newReservation.getRoomId(),
            newReservation.getCheckInDate(),
            newReservation.getCheckInTime(),
            newReservation.getCheckOutDate(),
            newReservation.getCheckOutTime()
    );
    
    if (isReserved) {
        return "The room is already reserved for the selected dates and times.";
    }

    newReservation.setReserved(true);
    newReservation.setStatus("pending");
    
    repo.save(newReservation);
    return "A new reservation is created.";
}


    @DeleteMapping("/delete/{id}")
    public String deleteReservation (@PathVariable Long id){
        repo.deleteById(id);
        return "The reservation is deleted.";
    }

   @GetMapping("/check")
    public boolean checkRoomReservation(
        @RequestParam Long roomId,
        @RequestParam LocalDate checkInDate,
        @RequestParam LocalTime checkInTime,
        @RequestParam LocalDate checkOutDate,
        @RequestParam LocalTime checkOutTime) {
    
    return repo.isRoomReserved(roomId, checkInDate, checkInTime, checkOutDate, checkOutTime);
}

    @PutMapping("/{id}")
    public Reservation updateReservationStatus(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        Reservation existingReservation = repo.findById(id).orElseThrow(
            () -> new ReservationNotFoundException(id)
        );
    
        String newStatus = updates.get("status");
        if (newStatus != null) {
            existingReservation.setStatus(newStatus);
        }
    
        return repo.save(existingReservation);
    }
    
}