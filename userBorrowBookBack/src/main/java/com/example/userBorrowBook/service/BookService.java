package com.example.userBorrowBook.service;

import com.example.userBorrowBook.model.Book;
import com.example.userBorrowBook.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;


    public Page<Book> findPaginated(int pageNo, int pageSize) {
        if(pageNo < 1) throw new IllegalArgumentException();

        Pageable pageable = PageRequest.of(
                pageNo - 1,
                Math.max(1, Math.min(pageSize, 100))
        );

        return bookRepository.findAll(pageable);
    }


}