package indocyber.exam.service;

import indocyber.exam.configuration.MvcSecurityConfig;
import indocyber.exam.dto.admin.AdminGridDTO;
import indocyber.exam.dto.admin.InsertAdminDTO;
import indocyber.exam.dto.admin.UpdateAdminDTO;

import indocyber.exam.dto.room.UpdateRoomDTO;
import indocyber.exam.entity.Account;
import indocyber.exam.entity.Admin;
import indocyber.exam.repository.AccountRepository;
import indocyber.exam.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final Integer rowsInPage = 10;

    @Override
    public List<AdminGridDTO> getAdminGrid(Integer page) {
        Pageable pagination = PageRequest.of(page -1,rowsInPage, Sort.by("username"));

        List<AdminGridDTO> grid = adminRepository.findAllDTO(pagination);

        return grid;
    }

    @Override
    public Page<AdminGridDTO> findAllDTO(Pageable pageable) {
        return adminRepository.findAllDTOPage(pageable);
    }

    @Override
    public long getTotalPages() {
        double totalData = (double)(adminRepository.count());
        long totalPage = (long)(Math.ceil(totalData/rowsInPage));
        return totalPage;
    }

    @Override
    public void deleteByUsername(String username) {
        accountRepository.deleteById(username);
    }

    @Override
    public AdminGridDTO findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public void insertAdmin(InsertAdminDTO dto) {
        PasswordEncoder passwordEncoder = MvcSecurityConfig.passwordEncoder();
        String hashPassword = passwordEncoder.encode(dto.getPassword());
        Account account = new Account(
                dto.getUsername(),
                hashPassword,
                "Admin"
        );
        accountRepository.save(account);

        Admin entity = new Admin(
                dto.getUsername(),
                dto.getJobTitle()
        );

        adminRepository.save(entity);


    }

    @Override
    public void updateAdmin(UpdateAdminDTO dto) {
        PasswordEncoder passwordEncoder = MvcSecurityConfig.passwordEncoder();
        String hashPassword = passwordEncoder.encode(dto.getPassword());


        Account account = new Account(
                dto.getUsername(),
                hashPassword,
                "Admin"
        );

        Admin admin = new Admin(
                dto.getUsername(),
                dto.getJobTitle()
        );

        accountRepository.save(account);
        adminRepository.save(admin);

    }

    @Override
    public void updateAdminFromDTO(UpdateAdminDTO dto) {
        PasswordEncoder passwordEncoder = MvcSecurityConfig.passwordEncoder();
        String hashPassword = passwordEncoder.encode(dto.getPassword());

        AdminGridDTO oldAdmin = adminRepository.findByUsername(dto.getUsername());

        Account account = new Account(
                dto.getUsername(),
                hashPassword,
                "Admin"
        );

        Admin admin = new Admin(
                dto.getUsername(),
                dto.getJobTitle()
        );

        accountRepository.save(account);
        adminRepository.save(admin);
    }

    @Override
    public UpdateAdminDTO findAdminToUpdate(String username) {
        AdminGridDTO admin = adminRepository.findByUsername(username);
        UpdateAdminDTO dto = new UpdateAdminDTO(
                username,
                admin.getJobTitle()
        );
        return dto;
    }
}
