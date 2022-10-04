package indocyber.exam.controller;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.admin.InsertAdminDTO;
import indocyber.exam.dto.admin.UpdateAdminDTO;
import indocyber.exam.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/index")
    public String getAdmins(@RequestParam(defaultValue = "1") Integer page,
                            Model model){
        List<AdminGridDTO> grid = adminService.getAdminGrid(page);

        long totalPages = adminService.getTotalPages();

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs", "Admin Index");

        return "admin/admin-index";
    }

    @GetMapping("/insertAdmin")
    public String insertAdminForm(Model model){
        InsertAdminDTO dto = new InsertAdminDTO();
        model.addAttribute("admin",dto);
        model.addAttribute("breadCrumbs","Admin Index / Insert Admin");
        return "admin/admin-insert-form";
    }

    @PostMapping("/insert")
    public String insertAdmin(@Valid @ModelAttribute("admin") InsertAdminDTO dto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("breadCrumbs","Admin Index / Insert Admin");
            return"admin/admin-insert-form";
        } else{
            adminService.insertAdmin(dto);
            return "redirect:/admin/index";
        }
    }

    @GetMapping("/updateAdmin")
    public String updateAdminForm(@RequestParam String username, Model model, Authentication authentication){
        if (!username.equals(authentication.getName())){
            return "home/access-denied";
        }
        UpdateAdminDTO dto = adminService.findAdminToUpdate(username);
        model.addAttribute("admin",dto);
        model.addAttribute("breadCrumbs","Admin Index / Update Admin");
        return "admin/admin-update-form";
    }

    @PostMapping("/update")
    public String updateAdmin(@Valid @ModelAttribute("admin") UpdateAdminDTO dto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("breadCrumbs","Admin Index / Update Admin");
            return"admin/admin-update-form";
        } else{
            adminService.updateAdmin(dto);
            return "redirect:/";
        }
    }

    @GetMapping("/delete")
    public String deleteAdmin(@RequestParam String username){
        adminService.deleteByUsername(username);
        return "redirect:/admin/index";
    }

}
