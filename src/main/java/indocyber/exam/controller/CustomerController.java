package indocyber.exam.controller;


import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.customer.UpdateCustomerDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.entity.Transaction;
import indocyber.exam.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/index")
    public String getCustomers(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "") String name,
                               Model model){
        List<CustomerGridDTO> grid = customerService.getCustomerGrid(page, name);

        long totalPages = customerService.getTotalPages(name);

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage",page);
        model.addAttribute("name", name);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs","Customer Index");

        return "customer/customer-index";

    }

    @GetMapping("/delete")
    public String delete(@RequestParam(required = true) String username, Model model) {

        Customer customer = customerService.findByUsername(username);

        if(customer.getListTransaction().size() == 0){
            customerService.deleteById(customer.getId());
            return "redirect:/customer/index";
        } else {
            model.addAttribute("breadCrumbs", "Failed to delete customer");
            return "customer/customer-delete";
        }

    }

    @GetMapping("/updateForm")
    public String updateAdminForm(@RequestParam String username, Model model, Authentication authentication){
        if (!username.equals(authentication.getName())){
            return "home/access-denied";
        }
        UpdateCustomerDTO dto = customerService.findCustomerToUpdate(username);
        model.addAttribute("customer",dto);
        model.addAttribute("breadCrumbs","Change My Profile");
        return "customer/customer-form";
    }

    @PostMapping("/update")
    public String updateAdmin(@Valid @ModelAttribute("customer") UpdateCustomerDTO dto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            model.addAttribute("breadCrumbs","Change My Profile");
            return"customer/customer-form";
        } else{
            customerService.updateCustomer(dto);
            return "redirect:/home/";
        }
    }

}
