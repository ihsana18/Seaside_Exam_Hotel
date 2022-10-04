package indocyber.exam.service;

import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.admin.InsertAdminDTO;
import indocyber.exam.dto.admin.UpdateAdminDTO;
import indocyber.exam.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface AdminService {

    List<AdminGridDTO> getAdminGrid(Integer page);

    Page<AdminGridDTO> findAllDTO(Pageable pageable);

    long getTotalPages();

    void deleteByUsername(String username);

    AdminGridDTO findByUsername(String username);

    void insertAdmin(InsertAdminDTO upsertAdminDTO);

    void updateAdmin(UpdateAdminDTO upsertAdminDTO);

    void updateAdminFromDTO(UpdateAdminDTO updateAdminDTO);

    UpdateAdminDTO findAdminToUpdate(String username);

}
