package indocyber.exam.controller;

import indocyber.exam.dto.customer.InsertCustomerDTO;
import indocyber.exam.service.AccountService;
import indocyber.exam.utility.Dropdown;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/loginForm")
    public String loginForm(Model model) {

        List<Dropdown> role = Dropdown.getRoleDropdown();
        model.addAttribute("role", role);
        return "account/login-form";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("customer") InsertCustomerDTO dto,
                           BindingResult bindingResult,
                           Model model){

        if(bindingResult.hasErrors()){
            return "account/register-form";
        }

        accountService.registerCustomer(dto);
        return "redirect:/account/loginForm";
    }

    @GetMapping("/registerForm")
    public String registerForm(Model model){

        InsertCustomerDTO dto = new InsertCustomerDTO();

        model.addAttribute("customer",dto);

        return "account/register-form";
    }
}
