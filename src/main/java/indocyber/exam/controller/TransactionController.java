package indocyber.exam.controller;

import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.room.UpdateRoomDTO;
import indocyber.exam.dto.transaction.TransactionGridDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Transaction;
import indocyber.exam.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    @EventListener(ApplicationReadyEvent.class)
    public void finishingOrExpiringTransaction() {
        List<Customer> allCust = transactionService.findAllCustomer();
        for(Customer cust : allCust){
            List<Transaction> transactionList = cust.getListTransaction();
            for(Transaction transaction : transactionList){
                if(transaction.getIsPending() && LocalDate.now().isAfter(transaction.getCheckIn())){
                    transactionService.expireTransaction(transaction);
                }
                if(transaction.getIsConfirmed() && LocalDate.now().isAfter(transaction.getCheckOut())){
                    transactionService.finishTransaction(transaction);
                }
            }
        }
    }

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/index")
    public String getCustomers(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "") String customerName,
                               @RequestParam(defaultValue = "") String roomNumber,
                               Model model){

        List<TransactionGridDTO> grid = transactionService.getTransactionGrid(page, customerName, roomNumber);

        long totalPages = transactionService.getTotalPages(customerName, roomNumber);

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage",page);
        model.addAttribute("customerName", customerName);
        model.addAttribute("roomNumber", roomNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs","Reservation Index");

        return "transaction/transaction-index";
    }

    @GetMapping("/history")
    public String getHistory(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam String username,
                             Model model){

        List<TransactionGridDTO> grid = transactionService.getCustomerTransaction(page, username);
        Customer cust = transactionService.getCustomerByUsername(username);

        long totalPages = transactionService.getDetailPages(username);

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs","Transaction History of "+cust.getName() );

        return "transaction/transaction-history";
    }

    @GetMapping("/myHistory")
    public String getMyHistory(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam String username,
                             Model model, Authentication authentication){

        if(!username.equals(authentication.getName())){
            return "home/access-denied";
        }

        List<TransactionGridDTO> grid = transactionService.getCustomerTransaction(page, username);
        Customer cust = transactionService.getCustomerByUsername(username);

        long totalPages = transactionService.getDetailPages(username);

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs","Transaction History of "+cust.getName() );

        return "transaction/transaction-history";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam(required = true) Integer id, Model model) {

        Transaction transaction = transactionService.findById(id);

        if(transaction.getIsConfirmed()  == true){
            model.addAttribute("breadCrumbs", "Failed to delete reservation");
            return "transaction/transaction-delete";
        }
        else{
            transactionService.deleteById(id);
            return "redirect:/transaction/index";

        }

    }



    @GetMapping("/detail")
    private String getTransactionDetail(@RequestParam String roomNumber, Model model){
        InsertTransactionDTO dto = transactionService.findActiveTransaction(roomNumber);
        String status = "";
        if(dto.getIsPending()){
            status = "Pending";
        }
        if(dto.getIsConfirmed() && !dto.getIsExpired()){
            status = "Confirmed";
        }

        model.addAttribute("transaction", dto);
        model.addAttribute("breadCrumbs", "See Reservation Detail");
        model.addAttribute("status", status);

        return "transaction/transaction-detail";

    }

    @PostMapping("/reserve")
    public String reserveRoom(@Valid @ModelAttribute("transaction") InsertTransactionDTO dto,
                              BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            model.addAttribute("breadCrumbs","Reserve Room");
            return "room/reserve-room";
        }

        transactionService.saveTransaction(dto);
        return "redirect:/home/";
    }

    @GetMapping("/deleteReservation")
    public String deleteReservation(@RequestParam(required = true) Integer id, Model model) {
        transactionService.deleteById(id);
        return "redirect:/room/vacantRoom";
    }

    @PostMapping("/confirm")
    public String confirmRoom(@Valid @ModelAttribute("transaction") InsertTransactionDTO dto){
        transactionService.confirmTransaction(dto);
        return "redirect:/home/";
    }

    @GetMapping("/reservation")
    public String myReservation(@RequestParam String username, Model model, Authentication authentication){

        if (!username.equals(authentication.getName())){
            return "home/access-denied";
        }

        Customer cust = transactionService.getCustomerByUsername(username);
        List<Transaction> transactions = cust.getListTransaction();
        Transaction transaction = null;
        for(Transaction tr : transactions){
            if(tr.getIsPending() || (tr.getIsConfirmed() && !tr.getIsExpired())){
                transaction = tr;
            }
        }

        if(transaction == null){
            return "transaction/no-transaction";
        }

        boolean trigger = false;

        if(transaction.getIsConfirmed() == true){
            trigger = true;
        }

        InsertTransactionDTO dto = transactionService.getTransactionToSee(transaction.getId());

        model.addAttribute("transaction", dto);
        model.addAttribute("breadCrumbs", "Confirm Reservation");
        model.addAttribute("trigger", trigger);

        return "transaction/transaction-confirmation";

    }
}
