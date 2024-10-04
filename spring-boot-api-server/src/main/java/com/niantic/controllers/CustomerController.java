package com.niantic.controllers;

import com.niantic.data.CustomerDao;
import com.niantic.models.Customer;
import com.niantic.services.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.niantic.exceptions.HttpError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin
public class CustomerController
{
    private final CustomerDao customerDao;
    private final LoggingService logger;

    @Autowired
    public CustomerController(CustomerDao customerDao, LoggingService logger) {
        this.customerDao = customerDao;
        this.logger = logger;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<?> getAllCustomers()
    {
        try
        {
            var customers = customerDao.getCustomers();

            return ResponseEntity.ok(customers);
        }
        catch (Exception e)
        {
            logger.logMessage(e.getMessage());

            var error = new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(),HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Oops, something went wrong!");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id)
    {
        try
        {
            var customer = customerDao.getCustomer(id);
            if (customer == null)
            {
                logger.logMessage("Customer with id " + id + " not found.");
                var error = new HttpError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.toString(), "Customer with id " + id + "can't be found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(error);
            }

            return ResponseEntity.ok(customer);
        }
        catch(Exception e)
        {
            logger.logMessage(e.getMessage());
            var error = new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Oops, something went wrong!");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(error);
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer addCustomer(@RequestBody Customer customer) { return customerDao.addCustomer(customer); }

    @PutMapping("{id")
    public ResponseEntity<?> updateCustomer(@PathVariable int id, @RequestBody Customer customer)
    {
        try
        {
            var currentCustomer = customerDao.getCustomer(id);
            if (currentCustomer == null)
            {
                var error = new HttpError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.toString(), "Customer with id " + id + " is invalid");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(error);
            }

            customerDao.updateCustomer(id, customer);
            return ResponseEntity.noContent()
                                 .build();
        }
        catch (Exception e)
        {
            var error = new HttpError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Oops, something went wrong!");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable int id) { customerDao.deleteCustomer(id); }
}
