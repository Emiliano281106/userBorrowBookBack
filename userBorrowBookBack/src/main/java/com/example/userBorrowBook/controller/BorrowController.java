package com.example.userBorrowBook.controller;


import com.example.userBorrowBook.model.Borrow;
import com.example.userBorrowBook.repository.BorrowRepository;
import com.example.userBorrowBook.repository.BorrowSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/borrows")
//@CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(origins = "*")
public class BorrowController {

    @Autowired
    private BorrowRepository borrowRepository;

    @GetMapping
    public ResponseEntity<List<Borrow>> getAllBorrows() {
        List<Borrow> borrows = borrowRepository.findAll();
        return ResponseEntity.ok(borrows);
    }

    @GetMapping("/filter")
    //@Transactional // Add transaction to ensure lazy-loaded entities are initialized
    public ResponseEntity<List<Borrow>> filterBorrows(
            @RequestParam(required = false) String bookTitle,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Integer userAge,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(required = false) Boolean returned
    ) {
        List<Borrow> borrows = borrowRepository.findAll(BorrowSpecification.filterByParameters(
                bookTitle, isbn, available, userAge, archived, dob, returned
        ));

        return ResponseEntity.ok(borrows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borrow> getBorrowById(@PathVariable String id) {
        Optional<Borrow> borrow = borrowRepository.findById(id);
        return borrow.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Borrow> createBorrow(@RequestBody Borrow borrow) {
        Borrow savedBorrow = borrowRepository.save(borrow);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBorrow);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrow> updateBorrow(@PathVariable String id, @RequestBody Borrow borrowDetails) {
        Optional<Borrow> optionalBorrow = borrowRepository.findById(id);
        if (optionalBorrow.isPresent()) {
            Borrow existingBorrow = optionalBorrow.get();
            existingBorrow.setBorrowDate(borrowDetails.getBorrowDate());
            existingBorrow.setReturnDate(borrowDetails.getReturnDate());
            existingBorrow.setReturned(borrowDetails.isReturned());
            existingBorrow.setPoints(borrowDetails.getPoints());
            existingBorrow.setUser(borrowDetails.getUser());
            existingBorrow.setBook(borrowDetails.getBook());

            Borrow updatedBorrow = borrowRepository.save(existingBorrow);
            return ResponseEntity.ok(updatedBorrow);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrow(@PathVariable String id) {
        if (borrowRepository.existsById(id)) {
            borrowRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Borrow>> getBorrowsByUserId(@PathVariable String userId) {
        List<Borrow> borrows = borrowRepository.findByUserId(userId);
        return ResponseEntity.ok(borrows);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Borrow>> getBorrowsByBookId(@PathVariable String bookId) {
        List<Borrow> borrows = borrowRepository.findByBookId(bookId);
        return ResponseEntity.ok(borrows);
    }
}