package indocyber.exam.controller;

import indocyber.exam.dto.transaction.InsertTransactionDTO;
import indocyber.exam.dto.customer.CustomerGridDTO;
import indocyber.exam.dto.room.InsertRoomDTO;
import indocyber.exam.dto.room.RoomGridDTO;
import indocyber.exam.dto.room.UpdateRoomDTO;
import indocyber.exam.entity.Customer;
import indocyber.exam.entity.Room;
import indocyber.exam.entity.Transaction;
import indocyber.exam.service.RoomService;
import indocyber.exam.utility.Dropdown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/index")
    public String getCustomers(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "") String roomNumber,
                               Model model){

        List<RoomGridDTO> grid = roomService.getRoomGrid(page, roomNumber);

        long totalPages = roomService.getTotalPages(roomNumber);

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage",page);
        model.addAttribute("roomNumber", roomNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs","Room Index");

        return "room/room-index";
    }

    @GetMapping("/upsertForm")
    public String upsertForm(@RequestParam(required = false) String roomNumber,
                             Model model){
        if(roomNumber != null){
            UpdateRoomDTO dto = roomService.getRoomToUpdate(roomNumber);
            model.addAttribute("room", dto);
            model.addAttribute("type","update");
            model.addAttribute("typeDropdown", Dropdown.getRoomType());
            model.addAttribute("breadCrumbs","Room Index / Update Room");
        } else {
            InsertRoomDTO dto = new InsertRoomDTO();
            model.addAttribute("room", dto);
            model.addAttribute("type","insert");
            model.addAttribute("typeDropdown", Dropdown.getRoomType());
            model.addAttribute("breadCrumbs","Room Index / Insert Room");
        }

        return "room/room-form";
    }

    @PostMapping("/insert")
    public String insert(@Valid @ModelAttribute("room")InsertRoomDTO dto,
                         BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("type","insert");
            model.addAttribute("typeDropdown", Dropdown.getRoomType());
            model.addAttribute("breadCrumbs","Room Index / Insert Room");
            return "room/room-form";
        } else{
            roomService.insertRoom(dto);
            return "redirect:/room/index";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam(required = true) String roomNumber, Model model) {

        Room room = roomService.findById(roomNumber);

        if(room.getIsReserved() || room.getIsOccupied()){
            model.addAttribute("breadCrumbs", "Failed to delete room");
            return "room/room-delete";
        }

        if(room.getTransactionList().size() == 0){
            roomService.deleteById(roomNumber);
            return "redirect:/room/index";
        } else {
            model.addAttribute("breadCrumbs", "Failed to delete room");
            return "room/room-delete";
        }

    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("room")UpdateRoomDTO dto,
                         BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("type","update");
            model.addAttribute("typeDropdown", Dropdown.getRoomType());
            model.addAttribute("breadCrumbs","Room Index / Update Room");
            return "room/room-form";
        } else{
            roomService.updateRoom(dto);
            return "redirect:/room/index";
        }
    }

    @GetMapping("/vacantRoom")
    public String getVacantRoom(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "") String roomType,
                                Model model){
        if(roomType == null){
            roomType = "";
        }
        List<RoomGridDTO> grid = roomService.getVacantRoomGrid(page, roomType);

        long totalPages = roomService.getVacantTotalPages(roomType);

        List<Dropdown> typeDropdown = Dropdown.getRoomType();

        model.addAttribute("grid", grid);
        model.addAttribute("currentPage",page);
        model.addAttribute("roomType", roomType);
        model.addAttribute("typeDropdown", typeDropdown);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("breadCrumbs","Room Index");

        return "room/vacant-room-index";
    }

    @GetMapping("/reserveForm")
    public String reserveForm(@RequestParam String roomNumber,
                              @RequestParam String username,
                              Model model){
        Customer cust = roomService.getCustomerByUsername(username);
        List<Transaction> listTransaction = cust.getListTransaction();
        for(Transaction tr : listTransaction){
            if(tr.getIsPending() || (tr.getIsConfirmed() && !tr.getIsExpired())){
                return "transaction/cannot-new-transaction";
            }
        }
        InsertTransactionDTO dto = new InsertTransactionDTO();
        UpdateRoomDTO roomDTO = roomService.getRoomToUpdate(roomNumber);
        dto.setRoomNumber(roomNumber);
        dto.setType(roomDTO.getType());
        dto.setPrice(roomDTO.getPrice());


        dto.setCustomerId(cust.getId());

        model.addAttribute("transaction",dto);
        model.addAttribute("breadCrumbs","Reserve Room");
        return "room/reserve-room";
    }




}
